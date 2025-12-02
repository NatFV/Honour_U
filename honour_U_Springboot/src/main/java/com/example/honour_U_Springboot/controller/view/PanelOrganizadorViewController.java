// com.example.honour_U_Springboot.controller.view.OrganizadorViewController.java
package com.example.honour_U_Springboot.controller.view;

import com.example.honour_U_Springboot.model.Aportacion;
import com.example.honour_U_Springboot.model.Proyecto;
import com.example.honour_U_Springboot.service.AportacionService;
import com.example.honour_U_Springboot.service.ProyectoService;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Proyecto que gestiona las vistas del panel de organizador
 * @author Natalia Fernández
 * @version 1
 */
@Controller
@RequestMapping("/proyectos/admin")
public class PanelOrganizadorViewController {
    @Autowired private ProyectoService proyectoService;
    @Autowired private AportacionService aportacionService;
    //SpringTemplateEngine -> motor de plantillas Thymleaf para renderizar HTML que
    //luego pasa a un generador de PDF
    @Autowired private SpringTemplateEngine templateEngine;


    /**
     * Método para mostrar panel de control que gestionará el organizador
     * @param adminToken token para la url del panel
     * @param model para mostrar los datos en el template
     * @return la vista del panel de proyecto
     * @throws Exception
     */
    @GetMapping("/{adminToken}/panel")
    public String mostrarPanel(@PathVariable String adminToken, Model model) throws Exception {
        Proyecto p = proyectoService.findByAdminToken(adminToken);
        List<Aportacion> aportes = aportacionService.findByProyectoOrderado(p);
        model.addAttribute("proyecto", p);
        model.addAttribute("aportaciones", aportes);
        return "organizador/panelProyecto";
    }

    /**
     * Método para ver las aportaciones en el panel de de control
     * Endpoint sólo para admin para que valide que el id pertenece a adminToken
     * pero no valide esVisible (el organizador debería poder ver todo)
     * @param adminToken de la url del panel
     * @param id del proyecto
     * @param model para que los datos se puedan ver en Thymeleaf
     * @return la vista de la aportación del usuario
     * @throws Exception
     */
    @GetMapping("/{adminToken}/aportaciones/{id}/ver")
    public String verAportacionAdmin(@PathVariable String adminToken,
                                     @PathVariable Long id,
                                     Model model) throws Exception {
        Proyecto p = proyectoService.findByAdminToken(adminToken);
        Aportacion a = aportacionService.findAportacionById(id);

        if (!a.getProyecto().getProyectoId().equals(p.getProyectoId())) {
            // No pertenece a este proyecto → fuera
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN, "No pertenece a este proyecto");
        }

        model.addAttribute("proyecto", p);
        model.addAttribute("aportacion", a);
        return "usuario/verAportacion";
    }

    /**
     * Método para editar aportación desde el panel de amin
     * @param adminToken para cargar la url del panel
     * @param id de la aportación
     * @param model para mostrar los datos en la vista
     * @return el formulario de edición de la aportación
     * @throws Exception si no encuentra aportaciones de ese proyecto
     */
    @GetMapping("/{adminToken}/aportaciones/{id}/editar")
    public String editarAportacionAdmin(@PathVariable String adminToken,
                                        @PathVariable Long id,
                                        Model model) throws Exception {
        //Busca el proyecto y la aportación
        Proyecto p = proyectoService.findByAdminToken(adminToken);
        Aportacion a = aportacionService.findAportacionById(id);

        if (!a.getProyecto().getProyectoId().equals(p.getProyectoId())) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN, "No pertenece a este proyecto");
        }

        //muestra el poryecto y la aportación existente
        model.addAttribute("proyecto", p);
        model.addAttribute("aportacion", a);

        model.addAttribute("adminMode", true);
        // Reutilizamos la vista de usuario
        return "usuario/editarAportacion";
    }

    /**
     * Método para actualizar la aportación y guardar cambios
     * @param adminToken para la url del panel de control
     * @param id de la aportación
     * @param form con el formulario de la aportación para actualizar
     * @return la vista del panel actualizado
     * @throws Exception si no encuentra la aportación
     */
    @PostMapping("/{adminToken}/aportaciones/{id}/editar")
    public String actualizarAportacionAdmin(@PathVariable String adminToken,
                                            @PathVariable Long id,
                                            @ModelAttribute("aportacion") Aportacion form,
                                            @RequestParam(value = "imagenes", required = false) MultipartFile[] imagenes)
            throws Exception {

        Proyecto p = proyectoService.findByAdminToken(adminToken);
        Aportacion a = aportacionService.findAportacionById(id);
        if (!a.getProyecto().getProyectoId().equals(p.getProyectoId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No pertenece a este proyecto");
        }

        // Texto / flags
        a.setMensaje(form.getMensaje() == null ? "" : form.getMensaje());
        a.setRemitente((form.getRemitente() == null || form.getRemitente().isBlank()) ? "Anónimo" : form.getRemitente());
        a.setEsVisible(form.isEsVisible());

        // ¿Hay archivos nuevos?
        boolean hayNuevos = imagenes != null && Arrays.stream(imagenes).anyMatch(f -> f != null && !f.isEmpty());
        if (hayNuevos) {
            List<String> nuevas = new ArrayList<>();

            if (a.getPageType() == Aportacion.PageType.PORTADA) {
                // Reemplaza todo lo que hubiera en /uploads/{token}/portada
                Path base = Paths.get(System.getProperty("user.dir"), "uploads", p.getTokenUrl(), "portada");
                if (Files.exists(base)) {
                    Files.walk(base).sorted(Comparator.reverseOrder())
                            .forEach(q -> { try { Files.deleteIfExists(q); } catch (Exception ignore) {} });
                }
                Files.createDirectories(base);

                for (MultipartFile mf : imagenes) {
                    if (mf == null || mf.isEmpty()) continue;
                    String original = mf.getOriginalFilename();
                    String safe = System.currentTimeMillis() + "_" + (original != null ? original.replaceAll("[^a-zA-Z0-9._-]", "_") : "img");
                    Path destino = base.resolve(safe);
                    mf.transferTo(destino.toFile());
                    nuevas.add("/uploads/" + p.getTokenUrl() + "/portada/" + safe);
                }

                a.setMediaUrls(nuevas);
                if (!nuevas.isEmpty()) a.setUrl(nuevas.get(0));

            } else {
                // Reemplaza TODO lo que hubiera en /uploads/{token}/{aportacionId}
                Path base = Paths.get(System.getProperty("user.dir"), "uploads", p.getTokenUrl(), String.valueOf(a.getAportacionId()));
                if (Files.exists(base)) {
                    Files.walk(base).sorted(Comparator.reverseOrder())
                            .forEach(q -> { try { Files.deleteIfExists(q); } catch (Exception ignore) {} });
                }
                Files.createDirectories(base);

                for (MultipartFile mf : imagenes) {
                    if (mf == null || mf.isEmpty()) continue;
                    String original = mf.getOriginalFilename();
                    String safe = System.currentTimeMillis() + "_" + (original != null ? original.replaceAll("[^a-zA-Z0-9._-]", "_") : "archivo");
                    Path destino = base.resolve(safe);
                    mf.transferTo(destino.toFile());
                    nuevas.add("/uploads/" + p.getTokenUrl() + "/" + a.getAportacionId() + "/" + safe);
                }

                a.setMediaUrls(nuevas);
                if (!nuevas.isEmpty()) a.setUrl(nuevas.get(0));
            }
        }

        aportacionService.saveAportacion(a);
        return "redirect:/proyectos/admin/" + adminToken + "/panel";
    }


    /**
     * Método para eliminar la aportación desde el panel de control
     * @param adminToken para la url del panel
     * @param id de la aportación
     * @return el panel de control actualizado
     * @throws Exception si no encuentra el proyecto
     */
    @PostMapping("/{adminToken}/aportaciones/{id}/eliminar")
    public String eliminarAportacionAdmin(@PathVariable String adminToken,
                                          @PathVariable Long id) throws Exception {
        Proyecto p = proyectoService.findByAdminToken(adminToken);
        Aportacion a = aportacionService.findAportacionById(id);

        if (!a.getProyecto().getProyectoId().equals(p.getProyectoId())) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN, "No pertenece a este proyecto");
        }

        // Borrado archivos de portada en la carpeta de uploads
        //getProperty busca la carpeta en la que se ejecuta la aplicación
        //Files.walk recorre recursivamente el directorio dir
        try {
            Path dir = Paths.get(System.getProperty("user.dir"), "uploads",
                    p.getTokenUrl(), String.valueOf(a.getAportacionId()));
            if (Files.exists(dir)) {
                Files.walk(dir)
                        //invierte el orden para que se borren antes archivos y directorios que carpetas padre
                        .sorted(Comparator.reverseOrder())
                        .forEach(path -> { try { Files.deleteIfExists(path); } catch (Exception ignore) {} });
            }
        } catch (Exception ignore) {}

        aportacionService.deleteAportacionById(id);
        return "redirect:/proyectos/admin/" + adminToken + "/panel";
    }


    /**
     * Método para guardar el orden de las aportaciones
     * @param adminToken para el panel de organizador
     * @param ids de las aportaciones
     * @return la lista ordenada
     * @throws Exception
     */
    @PostMapping(value="/{adminToken}/orden", consumes = "application/json")
    @ResponseBody
    public ResponseEntity<?> guardarOrden(@PathVariable String adminToken,
                                          @RequestBody List<Long> ids) throws Exception {
        Proyecto p = proyectoService.findByAdminToken(adminToken);
        // Mapear id -> orden (1..n)
        int orden = 1;
        for (Long id : ids) {
            //Si la aportación no pertenece al proyecto, se salta
            Aportacion a = aportacionService.findAportacionById(id);
            if (!a.getProyecto().getProyectoId().equals(p.getProyectoId())) continue;
            //Si pertenece al proyecto se establece la orden y se guarda
            a.setOrden(orden++);
            aportacionService.saveAportacion(a);
        }
        //Creamos la respuesta para enviar al cliente que contiene el contenido y el código de estado
        return ResponseEntity.ok(Map.of("ok", true));
    }


    /**
     * Método para crear la portada
     * @param adminToken de la url del panel de control
     * @return la página actualizada con la portada al final
     * @throws Exception
     */
    @PostMapping("/{adminToken}/crear-portada")
    public String crearPortada(@PathVariable String adminToken) throws Exception {
        Proyecto p = proyectoService.findByAdminToken(adminToken);
        Aportacion portada = new Aportacion();
        portada.setProyecto(p);
        portada.setRemitente("");// opcional
        portada.setMensaje("Título del homenaje"); // editable luego
        portada.setEsVisible(true);
        portada.setPageType(Aportacion.PageType.PORTADA);
        aportacionService.saveAportacion(portada); // se le asigna orden al final
        aportacionService.saveAportacion(portada);
        return "redirect:/proyectos/admin/"+adminToken+"/panel";
    }

    /**
     * Método para mostrar el forumlario de portada
     * @param adminToken para la url del panel de control
     * @param model para que los datos se puedan mostrar en thymeleaf
     * @return el formulario de la creación de portada
     * @throws Exception
     */
    @GetMapping("/{adminToken}/portada")
    public String mostrarForumularioPortada(@PathVariable String adminToken, Model model) throws Exception {
        Proyecto p = proyectoService.findByAdminToken(adminToken);

        // Busca si ya hay una portada existente
        Aportacion portada = aportacionService.findByProyectoOrderado(p).stream()
                .filter(a -> a.getPageType() == Aportacion.PageType.PORTADA)
                .findFirst()
                .orElse(null);

        model.addAttribute("proyecto", p);
        model.addAttribute("portada", portada); // puede ser null
        return "organizador/portadaForm";
    }


    /**
     * Método para guardar portada
     * @param adminToken del url del panel de control
     * @param titulo
     * @param imagenes
     * @return el template actualizado con la portada
     * @throws Exception
     */
    @PostMapping("/{adminToken}/portada")
    public String guardarPortada(@PathVariable String adminToken,
                                 @RequestParam("titulo") String titulo,
                                 @RequestParam(value = "imagenes", required = false) List<MultipartFile> imagenes)
            throws Exception {

        Proyecto p = proyectoService.findByAdminToken(adminToken);

        // Buscar si ya existe una portada
        Aportacion portada = aportacionService.findByProyectoOrderado(p).stream()
                .filter(a -> a.getPageType() == Aportacion.PageType.PORTADA)
                .findFirst()
                .orElseGet(() -> {
                    Aportacion a = new Aportacion();
                    a.setProyecto(p);
                    a.setPageType(Aportacion.PageType.PORTADA);
                    a.setEsVisible(true);
                    return a;
                });

        portada.setMensaje(titulo);

        // Guardar imágenes (una o varias)
        List<String> nuevasUrls = new ArrayList<>();
        if (imagenes != null) {
            String baseDir = System.getProperty("user.dir") + "/uploads/" + p.getTokenUrl() + "/portada/";
            Files.createDirectories(Paths.get(baseDir));

            for (MultipartFile img : imagenes) {
                if (img == null || img.isEmpty()) continue;
                String original = img.getOriginalFilename();
                String safe = System.currentTimeMillis() + "_" +
                        (original != null ? original.replaceAll("[^a-zA-Z0-9._-]", "_") : "img");
                Path destino = Paths.get(baseDir, safe);
                img.transferTo(destino.toFile());
                nuevasUrls.add("/uploads/" + p.getTokenUrl() + "/portada/" + safe);
            }
        }

        // Solo reemplazar si realmente hay nuevas imágenes
                if (!nuevasUrls.isEmpty()) {
                    portada.setMediaUrls(nuevasUrls);
                }

        // Guardar la portada como aportación
                aportacionService.saveAportacion(portada);

                return "redirect:/proyectos/admin/" + adminToken + "/panel";
            }


    /**
     * Método para eliminar portada
      * @param adminToken de la url de del panel de control
     * @return el panel de control actualizado
     * @throws Exception
     */
    @PostMapping("/{adminToken}/portada/eliminar")
    public String eliminarPortada(@PathVariable String adminToken) throws Exception {
        Proyecto p = proyectoService.findByAdminToken(adminToken);

        // Busca la portada del proyecto
        List<Aportacion> portadas = aportacionService.findByProyectoOrderado(p).stream()
                .filter(a -> a.getPageType() == Aportacion.PageType.PORTADA)
                .toList();

        // Borra los archivos de /uploads/{token}/portada/*
        try {
            Path dir = Paths.get(System.getProperty("user.dir"), "uploads", p.getTokenUrl(), "portada");
            if (Files.exists(dir)) {
                Files.walk(dir)
                        .sorted(Comparator.reverseOrder())
                        .forEach(path -> { try { Files.deleteIfExists(path); } catch (Exception ignore) {} });
            }
        } catch (Exception ignore) {}

        // Elimina el/los registros de BD
        for (Aportacion a : portadas) {
            aportacionService.deleteAportacionById(a.getAportacionId());
        }

        return "redirect:/proyectos/admin/" + adminToken + "/panel";
    }

    /**
     * Método para crear el índice
     * @param adminToken para el url del panel de control
     * @return el panel actualizado con el índice
     * @throws Exception
     */
    @PostMapping("/{adminToken}/indice")
    public String crearIndice(@PathVariable String adminToken) throws Exception {
        Proyecto p = proyectoService.findByAdminToken(adminToken);

        // 1) Páginas ordenadas del proyecto
        List<Aportacion> ordenadas = aportacionService.findByProyectoOrderado(p);

        // 2) Construimos líneas del índice SOLO con páginas normales, en el ORDEN actual
        StringBuilder sb = new StringBuilder();
        int page = 1;
        for (Aportacion a : ordenadas) {
            if (a.getPageType() == Aportacion.PageType.NORMAL) {
                String remit = (a.getRemitente() == null || a.getRemitente().isBlank())
                        ? ""
                        : a.getRemitente();
                sb.append(page).append(". ").append(remit).append("\n");
                page++;
            }
        }

        // 3) Buscar índice existente o crearlo
        Aportacion indice = ordenadas.stream()
                .filter(a -> a.getPageType() == Aportacion.PageType.INDICE)
                .findFirst()
                .orElseGet(() -> {
                    Aportacion a = new Aportacion();
                    a.setProyecto(p);
                    a.setPageType(Aportacion.PageType.INDICE);
                    a.setEsVisible(true);           // visible para admin; el usuario NO lo ve porque su vista filtra NORMAL
                    a.setRemitente("");             // opcional
                    a.setMensaje("");               // se rellena abajo
                    return a;
                });

        // 4) Guardar el contenido del índice en el mensaje
        indice.setMensaje(sb.toString());
        // opcional: limpiar media de índice
        indice.setMediaUrls(new ArrayList<>());

        aportacionService.saveAportacion(indice);

        // 5) Volver al panel
        return "redirect:/proyectos/admin/" + adminToken + "/panel";
    }

    /**
     * Método para añadir página en blanco
     * @param adminToken para el panel de control
     * @return el panel actualizado con la página en blanco
     * @throws Exception
     */
    @PostMapping("/{adminToken}/addBlank")
    public String addBlank(@PathVariable String adminToken) throws Exception {
        Proyecto p = proyectoService.findByAdminToken(adminToken);
        aportacionService.aniadirPaginaBlanco(p);
        return "redirect:/proyectos/admin/" + adminToken + "/panel";
    }


    /**
     * Método para generar y devolver el pdf del proyecto
     * @param adminToken para la url del panel
     * @param request para hcer peticiones al servidor
     * @param response para obtener respuestas del servidor
     * @throws Exception
     */
    @GetMapping("/{adminToken}/export.pdf")
    public void exportPdf(@PathVariable String adminToken,
                          HttpServletRequest request,
                          HttpServletResponse response) throws Exception {
        //Se busca el proyecto y la aportación
        Proyecto p = proyectoService.findByAdminToken(adminToken);
        List<Aportacion> pags = aportacionService.findByProyectoOrderado(p);

        // Se prepara la plantilla:
        //1.-Se construye la baseURi (protocolo+host+puerto) para que las rutas de las
        //imágenes se resuelvan bien
        String baseUri = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();

        // 2.-Crea un contexto Thymeleaf y mete el proyecto, páginas y base
        Context ctx = new Context(request.getLocale());
        ctx.setVariable("proyecto", p);
        ctx.setVariable("paginas", pags);
        ctx.setVariable("baseUri", baseUri);
        //Obtiene un html final al procesar la plantilla pdf con Thymeleaf
        String html = templateEngine.process("pdf/libro", ctx);
        //Configura la respuesta http y la cabecera para mostrar el pdf en el navegador y no forzar descarga
        response.setContentType("application/pdf");

        String filename = (p.getNombreProyecto() == null ? "libro" : p.getNombreProyecto().replaceAll("[^a-zA-Z0-9_-]","_")) + ".pdf";
        response.setHeader("Content-Disposition", "inline; filename=\"" + filename + "\"");
        //Convierte el html en el pdf con el builder
        com.openhtmltopdf.pdfboxout.PdfRendererBuilder builder = new com.openhtmltopdf.pdfboxout.PdfRendererBuilder();
        builder.useFastMode();
        //Le pase el html y baseUri
        builder.withHtmlContent(html, baseUri);
        //Escribe el pdf directamente en el OutPutStream de la respuesta
        builder.toStream(response.getOutputStream());
        builder.run();
    }



}
