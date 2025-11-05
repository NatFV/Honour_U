package com.example.honour_U_Springboot.controller.view;

import com.example.honour_U_Springboot.model.Destinatario;
import com.example.honour_U_Springboot.model.Libro;
import com.example.honour_U_Springboot.repository.LibroRepository;
import com.example.honour_U_Springboot.service.DestinatarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Clase DestinatarioController
 * Maneja las vistas del controlador
 */
@Controller
@RequestMapping("/backoffice")
public class DestinatarioViewController {
    @Autowired
    private DestinatarioService destinatarioService;
    @Autowired
    private LibroRepository libroRepository;

    /**
     * Clase mostrar Destinatarios
     * @param libroId el libro al que se dirige el destinatario
     * @param model conecta la información de las vistas con el controlador
     * @return la vista de destinatarios
     */
    @GetMapping("/destinatarios")
    public String mostrarDestinatarios(@RequestParam(value = "libroId", required = true) Long libroId, Model model) {
        Libro libro = libroRepository.findById(libroId).orElse(null);
        if (libro == null) {
            // Si no existe libro con ese id, redirige o muestra error (por ejemplo redirigir a lista de libros)
            return "redirect:/backoffice/libros";
        }

        List<Destinatario> destinatarios = destinatarioService.findAllDestinatarios();
        model.addAttribute("destinatarios", destinatarios);

        Destinatario destinatario = new Destinatario();
        destinatario.setLibro(libro);  // asociamos el libro
        model.addAttribute("destinatario", destinatario);

        return "backoffice/crearDestinatario";
    }


    @PostMapping("/destinatarios")
    public String guardarDestinatario(@ModelAttribute Destinatario destinatario) {
        destinatarioService.saveDestinatario(destinatario);
        Long libroId = (destinatario.getLibro() != null) ? destinatario.getLibro().getLibroId() : null;
        return (libroId != null)
                ? "redirect:/backoffice/destinatarios?libroId=" + libroId
                : "redirect:/backoffice/libros";
    }

    /**
     * Método para editar destinatarios
     * @param id del destiantario
     * @param model conecta las vistas con controlador
     * @return la vista de editar destinatario
     * @throws Exception si no la puede mostrar
     */
    @GetMapping("/destinatarios/{id}/edit")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) throws Exception {
        Destinatario destinatario = destinatarioService.findDestinatarioById(id);
        model.addAttribute("destinatario", destinatario);
        return "backoffice/editarDestinatario"; // Vista a crear
    }

    /**
     * Método actualizar destinatrio
     * @param id
     * @param destinatario
     * @return vista de destinatarois
     */
    @PostMapping("/destinatarios/{id}/update")
    public String actualizarDestinatario(@PathVariable Long id, @ModelAttribute Destinatario destinatario) {
        destinatarioService.updateDestinatario(id, destinatario);
        Long libroId = (destinatario.getLibro() != null) ? destinatario.getLibro().getLibroId() : null;
        return (libroId != null)
                ? "redirect:/backoffice/destinatarios?libroId=" + libroId
                : "redirect:/backoffice/libros";
    }

    /**
     * Método eliminar destinatario
     * @param id del destinatario que se quiere actualizar
     * @return lista actualizada de destinatarios
     */
    @GetMapping("/destinatarios/{id}/delete")
    public String eliminarDestinatario(@PathVariable Long id) throws Exception {
        // obtenemos el destinatario para saber su libro antes de borrar
        Destinatario dest = destinatarioService.findDestinatarioById(id);
        Long libroId = (dest != null && dest.getLibro() != null) ? dest.getLibro().getLibroId() : null;

        destinatarioService.deleteDestinatarioById(id);

        return (libroId != null)
                ? "redirect:/backoffice/destinatarios?libroId=" + libroId
                : "redirect:/backoffice/libros";
    }

    /**
     * Método nuevo destinatario
     * @param libroId para agregar destinatario
     * @param model
     * @return template nuevo de crear destinatario
     */
    @GetMapping("/destinatarios/nuevo")
    public String nuevoDestinatario(@RequestParam(value = "libroId", required = false) Long libroId, Model model) {
        Destinatario destinatario = new Destinatario();
        if (libroId != null) {
            Libro libro = libroRepository.findById(libroId).orElse(null);
            destinatario.setLibro(libro);
        }
        model.addAttribute("destinatario", destinatario);
        return "backoffice/crearDestinatario"; // tu template nuevo destinatario
    }

}

