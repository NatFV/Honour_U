package com.example.honour_U_Springboot.controller.view;

import com.example.honour_U_Springboot.model.Aportacion;
import com.example.honour_U_Springboot.model.Proyecto;
import com.example.honour_U_Springboot.service.AportacionService;
import com.example.honour_U_Springboot.service.ProyectoService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Controller
@RequestMapping("/proyectos/token")
public class AportacionUsuarioViewController {

    @Autowired private AportacionService aportacionService;
    @Autowired private ProyectoService proyectoService;

    // ---------- Helpers cookie ----------
    private String cookieNameFor(String token) {
        return "ok_" + token.replaceAll("[^A-Za-z0-9_-]", "_");
    }
    private String getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) if (name.equals(c.getName())) return c.getValue();
        return null;
    }

    // ---------- Página principal: crear + listar ----------
    @GetMapping("/{token}/aportaciones")
    public String mostrarFormularioAportacion(@PathVariable String token,
                                              Model model,
                                              HttpSession session,
                                              HttpServletRequest request,
                                              HttpServletResponse response) throws Exception {
        Proyecto proyecto = proyectoService.findByTokenUrl(token);
        if (proyecto == null) throw new Exception("Proyecto no encontrado");

        String sessionKey = "ownerKey:" + token;     // en sesión puede llevar ':'
        String cookieName = cookieNameFor(token);    // la cookie NO
        String ownerKey = (String) session.getAttribute(sessionKey);
        if (ownerKey == null) ownerKey = getCookie(request, cookieName);
        if (ownerKey == null) {
            ownerKey = UUID.randomUUID().toString();
            Cookie cookie = new Cookie(cookieName, ownerKey);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24 * 180);
            response.addCookie(cookie);
        }
        session.setAttribute(sessionKey, ownerKey);

        // Filtrado en servidor: SOLO PageType.NORMAL
        List<Aportacion> todas = aportacionService.findByProyecto(proyecto);
        String finalOwnerKey = ownerKey;
        List<Aportacion> mias = todas.stream()
                .filter(a -> a.getPageType() == Aportacion.PageType.NORMAL)
                .filter(a -> finalOwnerKey.equals(a.getOwnerKey()))
                .toList();
        String finalOwnerKey1 = ownerKey;
        List<Aportacion> otras = todas.stream()
                .filter(a -> a.getPageType() == Aportacion.PageType.NORMAL)
                .filter(a -> !finalOwnerKey1.equals(a.getOwnerKey()))
                .toList();

        //lista única ORDENADA y filtrada en servidor
        List<Aportacion> ordenadas = aportacionService.findByProyectoOrderado(proyecto)
                .stream()
                .filter(a -> a.getPageType() == Aportacion.PageType.NORMAL)
                .toList();

        model.addAttribute("proyecto", proyecto);
        model.addAttribute("nuevaAportacion", new Aportacion());
        model.addAttribute("mias", mias);
        model.addAttribute("otras", otras);
        model.addAttribute("sessionOwnerKey", ownerKey); // por si lo necesitas en la vista

        // Cargar aportaciones frescas desde la BD
        List<Aportacion> aportaciones = aportacionService.findByProyectoOrderado(proyecto)
                .stream()
                .filter(a -> a.getPageType() == Aportacion.PageType.NORMAL)
                .toList();
        model.addAttribute("aportaciones", aportaciones);
        return "usuario/nuevaAportacion";
    }

    // ---------- Crear aportación ----------
    @PostMapping("/{token}/aportaciones")
    public String guardarAportacion(@PathVariable String token,
                                    @ModelAttribute("nuevaAportacion") Aportacion aportacion,
                                    @RequestParam(value = "imagenes", required = false) MultipartFile[] imagenes,
                                    HttpSession session,
                                    HttpServletRequest request) throws Exception {

        Proyecto proyecto = proyectoService.findByTokenUrl(token);
        if (proyecto == null) throw new Exception("Proyecto no encontrado");

        String sessionKey = "ownerKey:" + token;
        String cookieName = cookieNameFor(token);
        String ownerKey = (String) session.getAttribute(sessionKey);
        if (ownerKey == null) ownerKey = getCookie(request, cookieName);
        if (ownerKey == null) {
            ownerKey = UUID.randomUUID().toString();
            session.setAttribute(sessionKey, ownerKey);
        }

        if (aportacion.getRemitente() == null || aportacion.getRemitente().isBlank())
            aportacion.setRemitente("Anónimo");
        if (aportacion.getMensaje() == null) aportacion.setMensaje("");

        aportacion.setOwnerKey(ownerKey);
        aportacion.setProyecto(proyecto);
        aportacion.setPageType(Aportacion.PageType.NORMAL);

        // 🔹 Guarda temporalmente para generar el ID
        aportacionService.saveAportacion(aportacion);
        Long idGenerado = aportacion.getAportacionId();

        // 🔹 Subida de archivos
        String baseDir = System.getProperty("user.dir") + "/uploads/"
                + proyecto.getTokenUrl() + "/" + idGenerado + "/";
        Path carpeta = Paths.get(baseDir);
        Files.createDirectories(carpeta);

        List<String> urls = new ArrayList<>();
        if (imagenes != null) {
            for (MultipartFile img : imagenes) {
                if (img == null || img.isEmpty()) continue;
                String original = img.getOriginalFilename();
                String safe = System.currentTimeMillis() + "_" +
                        (original != null ? original.replaceAll("[^a-zA-Z0-9._-]", "_") : "archivo");
                Path destino = carpeta.resolve(safe);
                img.transferTo(destino.toFile());
                String urlRel = "/uploads/" + proyecto.getTokenUrl() + "/" + idGenerado + "/" + safe;
                urls.add(urlRel);
            }
        }
        aportacion.setMediaUrls(urls);

        // Siempre asigna una URL (sea archivo o enlace de visualización)
        if (urls.isEmpty()) {
            aportacion.setUrl("/proyectos/token/" + token + "/aportaciones/" + idGenerado + "/ver");
        } else {
            // O si quieres usar la primera imagen/video como URL principal
            aportacion.setUrl(urls.get(0));
        }

        // Guardar definitivamente con URL ya completa
        aportacionService.saveAportacion(aportacion);

        return "redirect:/proyectos/token/" + token + "/aportaciones";
    }

    // ---------- Editar (GET) ----------
    @GetMapping("/{token}/aportaciones/{aportacionId}/editar")
    public String editarAportacion(@PathVariable String token,
                                   @PathVariable Long aportacionId,
                                   Model model,
                                   HttpSession session,
                                   HttpServletRequest request) throws Exception {
        Proyecto proyecto = proyectoService.findByTokenUrl(token);
        if (proyecto == null) throw new Exception("Proyecto no encontrado");

        Aportacion aport = aportacionService.findAportacionById(aportacionId);
        if (aport == null) throw new Exception("Aportación no encontrada");

        String sessionKey = "ownerKey:" + token;
        String cookieName = cookieNameFor(token);
        String ownerKey = (String) session.getAttribute(sessionKey);
        if (ownerKey == null) ownerKey = getCookie(request, cookieName);

        if (ownerKey == null || !ownerKey.equals(aport.getOwnerKey())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes editar esta aportación");
        }

        model.addAttribute("proyecto", proyecto);
        model.addAttribute("aportacion", aport);
        return "usuario/editarAportacion";
    }

    // ---------- Editar (POST) ----------
    @PostMapping("/{token}/aportaciones/{aportacionId}/editar")
    public String actualizarAportacion(@PathVariable String token,
                                       @PathVariable Long aportacionId,
                                       @ModelAttribute("aportacion") Aportacion form,
                                       @RequestParam(value="imagenes", required=false) MultipartFile[] nuevasImagenes,
                                       HttpSession session,
                                       HttpServletRequest request) throws Exception {
        Proyecto proyecto = proyectoService.findByTokenUrl(token);
        if (proyecto == null) throw new Exception("Proyecto no encontrado");

        Aportacion aport = aportacionService.findAportacionById(aportacionId);
        if (aport == null) throw new Exception("Aportación no encontrada");

        String sessionKey = "ownerKey:" + token;
        String cookieName = cookieNameFor(token);
        String ownerKey = (String) session.getAttribute(sessionKey);
        if (ownerKey == null) ownerKey = getCookie(request, cookieName);
        if (ownerKey == null || !ownerKey.equals(aport.getOwnerKey())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes editar esta aportación");
        }

        // Actualizar campos permitidos
        aport.setMensaje(form.getMensaje() == null ? "" : form.getMensaje());
        aport.setRemitente((form.getRemitente() == null || form.getRemitente().isBlank()) ? "Anónimo" : form.getRemitente());
        aport.setEsVisible(form.isEsVisible());
        aport.setPageType(Aportacion.PageType.NORMAL); // siempre NORMAL

        // Añadir nuevas imágenes (opcional)
        if (nuevasImagenes != null && nuevasImagenes.length > 0) {
            String baseDir = System.getProperty("user.dir") + "/uploads/"
                    + proyecto.getTokenUrl() + "/" + aport.getAportacionId() + "/";
            Path carpeta = Paths.get(baseDir);
            Files.createDirectories(carpeta);

            List<String> urls = (aport.getMediaUrls() != null)
                    ? new ArrayList<>(aport.getMediaUrls())
                    : new ArrayList<>();

            for (MultipartFile img : nuevasImagenes) {
                if (img == null || img.isEmpty()) continue;
                String original = img.getOriginalFilename();
                String safe = System.currentTimeMillis() + "_" +
                        (original != null ? original.replaceAll("[^a-zA-Z0-9._-]", "_") : "archivo");
                Path destino = carpeta.resolve(safe);
                img.transferTo(destino.toFile());
                String urlRel = "/uploads/" + proyecto.getTokenUrl() + "/" + aport.getAportacionId() + "/" + safe;
                urls.add(urlRel);
            }
            aport.setMediaUrls(urls);
        }

        aportacionService.saveAportacion(aport);
        return "redirect:/proyectos/token/" + token + "/aportaciones";
    }

    // ---------- Ver (solo lectura pública si visible) ----------
    @GetMapping("/{token}/aportaciones/{aportacionId}/ver")
    public String verAportacionPublica(@PathVariable String token,
                                       @PathVariable Long aportacionId,
                                       Model model) throws Exception {
        Proyecto proyecto = proyectoService.findByTokenUrl(token);
        if (proyecto == null) throw new Exception("Proyecto no encontrado");

        Aportacion aport = aportacionService.findAportacionById(aportacionId);
        if (aport == null) throw new Exception("Aportación no encontrada");

        if (!aport.isEsVisible()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Aportación no visible");
        }

        model.addAttribute("proyecto", proyecto);
        model.addAttribute("aportacion", aport);
        return "usuario/verAportacion";
    }

    // ---------- Eliminar ----------
    @PostMapping("/{token}/aportaciones/{aportacionId}/eliminar")
    public String eliminarAportacion(@PathVariable String token,
                                     @PathVariable Long aportacionId,
                                     HttpSession session,
                                     HttpServletRequest request) throws Exception {
        Proyecto proyecto = proyectoService.findByTokenUrl(token);
        if (proyecto == null) throw new Exception("Proyecto no encontrado");

        Aportacion aport = aportacionService.findAportacionById(aportacionId);
        if (aport == null) throw new Exception("Aportación no encontrada");
        if (aport.getPageType() != Aportacion.PageType.NORMAL) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes eliminar portada/índice");
        }

        String sessionKey = "ownerKey:" + token;
        String cookieName = cookieNameFor(token);
        String ownerKey = (String) session.getAttribute(sessionKey);
        if (ownerKey == null) ownerKey = getCookie(request, cookieName);
        if (ownerKey == null || !ownerKey.equals(aport.getOwnerKey())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes eliminar esta aportación");
        }

        // (Opcional) borrar archivos del disco
        try {
            Path dir = Paths.get(System.getProperty("user.dir"), "uploads",
                    proyecto.getTokenUrl(), String.valueOf(aport.getAportacionId()));
            if (Files.exists(dir)) {
                Files.walk(dir)
                        .sorted(Comparator.reverseOrder())
                        .forEach(p -> { try { Files.deleteIfExists(p); } catch (Exception ignore) {} });
            }
        } catch (Exception ignore) {}

        //Sincronizamos del lado padre (Esto limpia la lista en memoria en el mismo momento del borrado)
        proyecto.getAportaciones().removeIf(a -> a.getAportacionId().equals(aportacionId));

        aportacionService.deleteAportacionById(aportacionId);
        return "redirect:/proyectos/token/" + token + "/aportaciones";
    }

}
