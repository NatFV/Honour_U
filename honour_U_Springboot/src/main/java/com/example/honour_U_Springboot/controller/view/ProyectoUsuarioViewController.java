package com.example.honour_U_Springboot.controller.view;

import com.example.honour_U_Springboot.model.Aportacion;
import com.example.honour_U_Springboot.model.Proyecto;
import com.example.honour_U_Springboot.service.ProyectoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;

/**
 * @author Natalia Fernández
 * Clase ProyectoUsuarioViewController
 * Contiene los métodos para mostrar el formulario de proyecto,
 * crear un proyecto y guardarlo.
 */

@Controller
@RequestMapping("/usuario/proyectos")
public class ProyectoUsuarioViewController {

    @Autowired
    private ProyectoService proyectoService;

    /**
     * Método para mostrar el formulario que crea el proyecto
     * @param model que añade un atributo llamado proyecto con una instancia vacía de Proyecto para enlazar el formulario.
     * @return la vista de nuevo proyecto que mostrará ese formulario
     */
    @GetMapping ("/nuevo")
    public String mostrarFormularioProyecto(Model model) {
        model.addAttribute("proyecto", new Proyecto());
        return "usuario/nuevoProyecto"; // apunta al template dentro de /usuario
    }

    /**
     * Método para guardar proyecto y generar las direcciones públicas y de administrador
     * @param proyecto que se pasa con los datos del proyecto
     * Se construyen los urls a partir del ServerUriComponentsBuilder, y de los tokens
     * @param model para pasar los datos a la vista
     * @return una vista que muestra ambos enlaces
     */
    @PostMapping("/nuevo")
    public String guardarProyecto(Proyecto proyecto, Model model) {

        // Generar tokens si faltan (público + admin)
        if (proyecto.getTokenUrl() == null || proyecto.getTokenUrl().isBlank()) {
            proyecto.setTokenUrl(UUID.randomUUID().toString());
        }
        if (proyecto.getAdminToken() == null || proyecto.getAdminToken().isBlank()) {
            proyecto.setAdminToken(UUID.randomUUID().toString());
        }

        // Utilizamos ServerletUriComponentsBuilder (clase de Spring Web que construye
        //el identificador de un recurso (URI)) En este caso construiría: http://localhost:8081/...
        String base = ServletUriComponentsBuilder
                .fromCurrentContextPath() //toma datos desde el contexto actual (protocolo, host,ruta,query)
                .build() //lo ensambla
                .toUriString(); //serializa los componentes para construir la URL final

        // Creación de los enlaces con la base y los tokens
        String linkAportaciones = base + "/proyectos/token/" + proyecto.getTokenUrl() + "/aportaciones";
        String linkAdmin        = base + "/proyectos/admin/" + proyecto.getAdminToken() + "/panel";

        // Asignación de las URLS antes de guardar
        proyecto.setUrlProyecto(linkAportaciones);
        proyecto.setUrlAdmin(linkAdmin);

        // Persistencia para salvar el proyecto
        Proyecto guardado = proyectoService.saveProyecto(proyecto);


        // Se pasan los datos a la vista de enlaces
        model.addAttribute("proyecto", guardado);
        model.addAttribute("linkAportaciones", linkAportaciones);
        model.addAttribute("linkAdmin", linkAdmin);


        return "usuario/proyectoLinks"; // vista que muestra ambos enlaces
    }

}

