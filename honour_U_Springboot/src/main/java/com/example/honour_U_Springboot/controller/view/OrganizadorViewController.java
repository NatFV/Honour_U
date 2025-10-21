// com.example.honour_U_Springboot.controller.view.OrganizadorViewController.java
package com.example.honour_U_Springboot.controller.view;

import com.example.honour_U_Springboot.model.Aportacion;
import com.example.honour_U_Springboot.model.Proyecto;
import com.example.honour_U_Springboot.service.AportacionService;
import com.example.honour_U_Springboot.service.ProyectoService;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Controller
@RequestMapping("/proyectos/admin")
public class OrganizadorViewController {

    @Autowired private ProyectoService proyectoService;
    @Autowired private AportacionService aportacionService;
    @Autowired private SpringTemplateEngine templateEngine;
    @Autowired private ServletContext servletContext;

    // Panel principal del organizador
    @GetMapping("/{adminToken}/panel")
    public String panel(@PathVariable String adminToken, Model model) throws Exception {
        Proyecto p = proyectoService.findByAdminToken(adminToken);
        List<Aportacion> aportes = aportacionService.findByProyectoOrderado(p);
        model.addAttribute("proyecto", p);
        model.addAttribute("aportaciones", aportes);
        return "admin/panelProyecto";
    }


    // Guardar nuevo orden (JSON: [id1, id2, id3, ...])
    @PostMapping(value="/{adminToken}/orden", consumes = "application/json")
    @ResponseBody
    public ResponseEntity<?> guardarOrden(@PathVariable String adminToken,
                                          @RequestBody List<Long> ids) throws Exception {
        Proyecto p = proyectoService.findByAdminToken(adminToken);
        // Mapear id -> orden (1..n)
        int orden = 1;
        for (Long id : ids) {
            Aportacion a = aportacionService.findAportacionById(id);
            if (!a.getProyecto().getProyectoId().equals(p.getProyectoId())) continue;
            a.setOrden(orden++);
            aportacionService.saveAportacion(a);
        }

        return ResponseEntity.ok(Map.of("ok", true));
    }

    // Toggle visible/invisible rápido
    @PostMapping("/{adminToken}/aportaciones/{id}/toggleVisible")
    @ResponseBody
    public ResponseEntity<?> toggleVisible(@PathVariable String adminToken,
                                           @PathVariable Long id) throws Exception {
        Proyecto p = proyectoService.findByAdminToken(adminToken);
        Aportacion a = aportacionService.findAportacionById(id);
        if (!a.getProyecto().getProyectoId().equals(p.getProyectoId()))
            return ResponseEntity.badRequest().body(Map.of("ok", false));

        a.setEsVisible(!a.isEsVisible());
        aportacionService.saveAportacion(a);
        return ResponseEntity.ok(Map.of("ok", true, "visible", a.isEsVisible()));
    }

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
        // Opcional: súbela al principio:
        portada.setOrden(1);
        aportacionService.saveAportacion(portada);
        return "redirect:/proyectos/admin/"+adminToken+"/panel";
    }


    @GetMapping("/{adminToken}/export.pdf")
    public void exportPdf(@PathVariable String adminToken,
                          HttpServletRequest request,
                          HttpServletResponse response) throws Exception {

        Proyecto p = proyectoService.findByAdminToken(adminToken);
        List<Aportacion> pags = aportacionService.findByProyectoOrderado(p);

        // URL base absoluta (para que OpenHTMLToPDF pueda resolver /uploads/**)
        String baseUri = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();

        // Contexto normal (NO WebContext)
        Context ctx = new Context(request.getLocale());
        ctx.setVariable("proyecto", p);
        ctx.setVariable("paginas", pags);
        ctx.setVariable("baseUri", baseUri);

        String html = templateEngine.process("pdf/libro", ctx);

        response.setContentType("application/pdf");
        String filename = (p.getNombreProyecto() == null ? "libro" : p.getNombreProyecto().replaceAll("[^a-zA-Z0-9_-]","_")) + ".pdf";
        response.setHeader("Content-Disposition", "inline; filename=\"" + filename + "\"");

        com.openhtmltopdf.pdfboxout.PdfRendererBuilder builder = new com.openhtmltopdf.pdfboxout.PdfRendererBuilder();
        builder.useFastMode();
        builder.withHtmlContent(html, baseUri);  // importante
        builder.toStream(response.getOutputStream());
        builder.run();
    }

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

        // Guardar (tu servicio ya hace saveAndFlush)
                aportacionService.saveAportacion(portada);

                return "redirect:/proyectos/admin/" + adminToken + "/panel";
            }


    @PostMapping("/{adminToken}/indice")
    public String crearIndice(@PathVariable String adminToken) throws Exception {
        Proyecto p = proyectoService.findByAdminToken(adminToken);

        // 1) Páginas ordenadas del proyecto
        List<Aportacion> ordenadas = aportacionService.findByProyectoOrderado(p);

        // 2) Construimos líneas del índice SOLO con páginas normales, en el ORDEN actual
        //    (si quieres numerar contando portada/índice, indícalo y te lo ajusto)
        StringBuilder sb = new StringBuilder();
        int page = 1;
        for (Aportacion a : ordenadas) {
            if (a.getPageType() == Aportacion.PageType.NORMAL) {
                String remit = (a.getRemitente() == null || a.getRemitente().isBlank())
                        ? "Anónimo"
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
                    // lo dejamos al final; si quieres que quede tras portada, lo arrastras en el panel
                    return a;
                });

        // 4) Guardar el contenido del índice en el mensaje (texto plano; tu plantilla PDF ya lo renderiza)
        indice.setMensaje(sb.toString());
        // opcional: limpiar media de índice
        indice.setMediaUrls(new ArrayList<>());

        aportacionService.saveAportacion(indice);

        // 5) Volver al panel
        return "redirect:/proyectos/admin/" + adminToken + "/panel";
    }

    @PostMapping("/{adminToken}/addBlank")
    public String addBlank(@PathVariable String adminToken) throws Exception {
        Proyecto p = proyectoService.findByAdminToken(adminToken);
        aportacionService.addBlankPageAtEnd(p);
        return "redirect:/proyectos/admin/" + adminToken + "/panel";
    }


    /**
     * Endpoint sólo para admin para que valide que el id pertenece a adminToken
     * pero no valide esVisible (el organizador debería poder ver todo)
     * @param adminToken
     * @param id
     * @param model
     * @return
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
        // Puedes reutilizar la misma vista que usas en público si no depende del check de visibilidad
        // o crear una específica para admin:
        return "usuario/verAportacion"; // o "usuario/verAportacion" si es neutral
    }

    /**
     * Método para crear una portada
     * @param adminToken
     * @param model
     * @return
     * @throws Exception
     */
    @GetMapping("/{adminToken}/portada")
    public String portadaForm(@PathVariable String adminToken, Model model) throws Exception {
        Proyecto p = proyectoService.findByAdminToken(adminToken);

        // Busca si ya hay una portada existente
        Aportacion portada = aportacionService.findByProyectoOrderado(p).stream()
                .filter(a -> a.getPageType() == Aportacion.PageType.PORTADA)
                .findFirst()
                .orElse(null);

        model.addAttribute("proyecto", p);
        model.addAttribute("portada", portada); // puede ser null
        return "admin/portadaForm";
    }

    @PostMapping("/{adminToken}/portada/eliminar")
    public String eliminarPortada(@PathVariable String adminToken) throws Exception {
        Proyecto p = proyectoService.findByAdminToken(adminToken);

        // Busca la(s) portada(s) del proyecto
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

    // EDITAR (GET) — sin comprobar ownerKey ni visibilidad
    @GetMapping("/{adminToken}/aportaciones/{id}/editar")
    public String editarAportacionAdmin(@PathVariable String adminToken,
                                        @PathVariable Long id,
                                        Model model) throws Exception {
        Proyecto p = proyectoService.findByAdminToken(adminToken);
        Aportacion a = aportacionService.findAportacionById(id);

        if (!a.getProyecto().getProyectoId().equals(p.getProyectoId())) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN, "No pertenece a este proyecto");
        }

        model.addAttribute("proyecto", p);
        model.addAttribute("aportacion", a);
        // Reutilizamos la vista de usuario si ya la tienes hecha
        return "usuario/editarAportacion"; // <- usa tu template existente
    }

    // EDITAR (POST) — guarda cambios
    @PostMapping("/{adminToken}/aportaciones/{id}/editar")
    public String actualizarAportacionAdmin(@PathVariable String adminToken,
                                            @PathVariable Long id,
                                            @ModelAttribute("aportacion") Aportacion form) throws Exception {
        Proyecto p = proyectoService.findByAdminToken(adminToken);
        Aportacion a = aportacionService.findAportacionById(id);

        if (!a.getProyecto().getProyectoId().equals(p.getProyectoId())) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN, "No pertenece a este proyecto");
        }

        a.setMensaje(form.getMensaje() == null ? "" : form.getMensaje());
        a.setRemitente((form.getRemitente() == null || form.getRemitente().isBlank()) ? "Anónimo" : form.getRemitente());
        a.setEsVisible(form.isEsVisible());
        // Mantén el tipo de página tal cual (NORMAL/PORTADA/INDICE)
        aportacionService.saveAportacion(a);

        return "redirect:/proyectos/admin/" + adminToken + "/panel";
    }

    // ELIMINAR — sin ownerKey ni visibilidad
    @PostMapping("/{adminToken}/aportaciones/{id}/eliminar")
    public String eliminarAportacionAdmin(@PathVariable String adminToken,
                                          @PathVariable Long id) throws Exception {
        Proyecto p = proyectoService.findByAdminToken(adminToken);
        Aportacion a = aportacionService.findAportacionById(id);

        if (!a.getProyecto().getProyectoId().equals(p.getProyectoId())) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN, "No pertenece a este proyecto");
        }

        // (Opcional) borra sus archivos del disco como haces en otros sitios
        try {
            Path dir = Paths.get(System.getProperty("user.dir"), "uploads",
                    p.getTokenUrl(), String.valueOf(a.getAportacionId()));
            if (Files.exists(dir)) {
                Files.walk(dir)
                        .sorted(Comparator.reverseOrder())
                        .forEach(path -> { try { Files.deleteIfExists(path); } catch (Exception ignore) {} });
            }
        } catch (Exception ignore) {}

        aportacionService.deleteAportacionById(id);
        return "redirect:/proyectos/admin/" + adminToken + "/panel";
    }














}
