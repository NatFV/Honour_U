// src/main/java/com/example/honour_U_Springboot/web/controller/LibroWizardController.java
package com.example.honour_U_Springboot.controller.view;


import com.example.honour_U_Springboot.model.*;
import com.example.honour_U_Springboot.repository.LibroRepository;
import com.example.honour_U_Springboot.repository.ProyectoRepository;
import com.example.honour_U_Springboot.service.DestinatarioService;
import com.example.honour_U_Springboot.service.LibroService;
import com.example.honour_U_Springboot.web.formularioParaLibro.LibroWizardForm;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para poder crear un libro físico o en pdf
 * Utiliza la clase LibroWizard Form para que el usuario cree el libro en
 * función de sus preferencias
 */
@Controller
@RequestMapping("/proyectos/admin/{adminToken}")
public class LibroWizardController {

    private final ProyectoRepository proyectoRepo;
    private final LibroRepository libroRepo;
    private final LibroService libroService;
    private final DestinatarioService destinatarioService;

    //Constructor
    public LibroWizardController(ProyectoRepository proyectoRepo,
                                 LibroRepository libroRepo,
                                 LibroService libroService,
                                 DestinatarioService destinatarioService) {
        this.proyectoRepo = proyectoRepo;
        this.libroRepo = libroRepo;
        this.libroService = libroService;
        this.destinatarioService = destinatarioService;
    }

    /**
     * Método para cargar el proyecto
     * @param adminToken para encontrar el proyecto
     * @return el proyecto cargado
     */
    @ModelAttribute("proyecto")
    public Proyecto cargarProyecto(@PathVariable String adminToken) {
        return proyectoRepo.findByAdminToken(adminToken)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado: " + adminToken));
    }

    @GetMapping("/libro/crear")
    public String show(@ModelAttribute("proyecto") Proyecto proyecto, Model model) {
        LibroWizardForm form = new LibroWizardForm();
        form.setFormato("PDF");
        form.setCopias(1);
        form.setPaginas(1);
        // por UX podrías prechequear email o físico según contexto
        model.addAttribute("form", form);
        return "pdf/libro-wizard";
    }

    /**
     * Método para crear libro físico o virtual
     * @param proyecto del libro que se quiere realizar
     * @param form el formulario a rellenar
     * @param br interfaz que detecta errores cuando no se pone el valor adecuado
     * @param model para mostrar los datos en Thymeleaf
     * @return el formulario completado
     */
    @PostMapping("/libro")
    @Transactional
    public String crearLibroFisicoOvirtual(@ModelAttribute("proyecto") Proyecto proyecto,
                                 @Valid @ModelAttribute("form") LibroWizardForm form,
                                 BindingResult br,
                                 Model model) {

        // Validación: al menos una opción
        if (!form.isEnvioEmail() && !form.isEnvioFisico()) {
            model.addAttribute("globalError", "Selecciona al menos una opción: email o envío físico.");
            return "pdf/libro-wizard";
        }
        // Si email marcado, email requerido
        if (form.isEnvioEmail()) {
            if (form.getEmail() == null || form.getEmail().isBlank()) {
                br.rejectValue("email", "email.requerido", "El email es obligatorio al seleccionar 'Enviar por email'.");
            }
        }
        // Si se marca el físico, campos mínimos
        if (form.isEnvioFisico()) {
            if (form.getDestinatarioNombre() == null || form.getDestinatarioNombre().isBlank()
                    || form.getCalle() == null || form.getCalle().isBlank()
                    || form.getNumero() == null || form.getNumero().isBlank()
                    || form.getPais() == null || form.getPais().isBlank()) {
                model.addAttribute("globalError", "Para envío físico, indica al menos Nombre, Calle y País.");
                return "pdf/libro-wizard";
            }
        }
        if (br.hasErrors()) return "pdf/libro-wizard";

        // Un solo libro por proyecto: crear o actualizar
        Libro libro = libroRepo.findByProyecto(proyecto).orElseGet(Libro::new);
        libro.setTituloLibro(form.getTituloLibro());
        libro.setFormato(form.getFormato());
        libro.setCopias(form.getCopias());
        libro.setPaginas(form.getPaginas());
        libro.setProyecto(proyecto);
        libro = libroService.saveLibro(libro);

        // Envío físico: crear/actualizar destinatario 1:1
        if (form.isEnvioFisico()) {
            Destinatario dest = libro.getDestinatario(); // inverso (si lo mantienes)
            if (dest == null) dest = new Destinatario();
            dest.setNombre(form.getDestinatarioNombre());
            dest.setApellido(form.getDestinatarioApellido());
            dest.setTelefono(form.getTelefono());
            dest.setEmail(form.getEmail()); // opcional
            dest.setLibro(libro); // propietario 1:1

            Direccion dir = dest.getDirecciones().stream().findFirst().orElse(null);
            if (dir == null) {
                dir = new Direccion();
                dir.setDestinatario(dest);
                dest.getDirecciones().add(dir);
            }
            dir.setCalle(form.getCalle());
            dir.setNumero(form.getNumero());
            dir.setPiso(form.getPiso());
            dir.setLetra(form.getLetra());
            dir.setCodigoPostal(form.getCodigoPostal());
            dir.setPais(form.getPais());

            dest = destinatarioService.saveDestinatario(dest);
            libro.setDestinatario(dest);
            libroService.saveLibro(libro);
        }

//        // Envío por email (placeholder)
//        if (form.isEnvioEmail()) {
//            // TODO: aquí pondrías tu servicio real de emailing (adjuntar PDF, link, etc.)
//            // emailService.enviarLibro(form.getEmail(), libro);
//        }

        return "redirect:/proyectos/admin/" + proyecto.getAdminToken()
                + "/libro/" + libro.getLibroId() + "/confirmacion";
    }

    /**
     * Método para confirmar que el libro se ha creado
     * @param libroId
     * @param proyecto
     * @param model
     * @return el formulario de confirmación del libro
     */
    @GetMapping("/libro/{libroId}/confirmacion")
    public String confirmarLibro(@PathVariable Long libroId,
                          @ModelAttribute("proyecto") Proyecto proyecto,
                          Model model) {
        // Carga el libro para decidir si mostrar "Descargar PDF"
        Libro libro;
        try {
            libro = libroService.findLibroById(libroId);
        } catch (Exception e) {
            // maneja el error como prefieras (redirigir, 404, etc.)
            throw new IllegalArgumentException("Libro no encontrado: " + libroId, e);
        }
        model.addAttribute("libro", libro);
        return "pdf/libro-confirmacion";
    }





}
