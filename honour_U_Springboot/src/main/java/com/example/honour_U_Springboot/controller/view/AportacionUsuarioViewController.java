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

    // ---------- Helpers de cookie ----------
    private String cookieNameFor(String token) {
        // Nombre de cookie seguro: sin caracteres prohibidos
        return "ok_" + token.replaceAll("[^A-Za-z0-9_-]", "_");
    }
    private String getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (name.equals(c.getName())) return c.getValue();
        }
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
            cookie.setMaxAge(60 * 60 * 24 * 180); // 180 días
            response.addCookie(cookie);
        }
        session.setAttribute(sessionKey, ownerKey);

        model.addAttribute("proyecto", proyecto);
        model.addAttribute("nuevaAportacion", new Aportacion());
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

        aportacion.setOwnerKey(ownerKey);
        aportacion.setProyecto(proyecto);

        // Guardar primero para obtener ID
        aportacionService.saveAportacion(aportacion);

        // Guardar archivos
        String baseDir = System.getProperty("user.dir") + "/uploads/"
                + proyecto.getTokenUrl() + "/" + aportacion.getAportacionId() + "/";
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
                String urlRel = "/uploads/" + proyecto.getTokenUrl() + "/" + aportacion.getAportacionId() + "/" + safe;
                urls.add(urlRel);
            }
        }
        aportacion.setMediaUrls(urls);

        // Fallback URL si no hay archivos
        if (urls.isEmpty()) {
            String urlAportacion = proyecto.getUrlProyecto() + "/" + aportacion.getAportacionId();
            aportacion.setUrl(urlAportacion);
        }

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
        aport.setMensaje(form.getMensaje());
        aport.setRemitente(form.getRemitente());
        aport.setEsVisible(form.isEsVisible());

        // (Opcional) añadir nuevas imágenes
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

    // ---------- Ver una aportación (opcional) ----------
    @GetMapping("/{token}/aportaciones/{aportacionId}")
    public String verAportacion(@PathVariable String token,
                                @PathVariable Long aportacionId,
                                Model model) throws Exception {
        Proyecto proyecto = proyectoService.findByTokenUrl(token);
        if (proyecto == null) throw new Exception("Proyecto no encontrado");

        Aportacion aportacion = aportacionService.findAportacionById(aportacionId);
        if (aportacion == null) throw new Exception("Aportación no encontrada");

        model.addAttribute("proyecto", proyecto);
        model.addAttribute("aportacion", aportacion);
        return "usuario/editarAportacion"; // o "usuario/verAportacion" si tienes plantilla específica
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

        String sessionKey = "ownerKey:" + token;
        String cookieName = cookieNameFor(token);

        String ownerKey = (String) session.getAttribute(sessionKey);
        if (ownerKey == null) ownerKey = getCookie(request, cookieName);

        if (ownerKey == null || !ownerKey.equals(aport.getOwnerKey())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes eliminar esta aportación");
        }

        // (Opcional) borrar también los archivos del disco aquí si quieres

        aportacionService.deleteAportacionById(aportacionId);
        return "redirect:/proyectos/token/" + token + "/aportaciones";
    }

    @GetMapping("/{token}/aportaciones/{aportacionId}/ver")
    public String verAportacionPublica(@PathVariable String token,
                                       @PathVariable Long aportacionId,
                                       Model model) throws Exception {
        Proyecto proyecto = proyectoService.findByTokenUrl(token);
        if (proyecto == null) throw new Exception("Proyecto no encontrado");

        Aportacion aport = aportacionService.findAportacionById(aportacionId);
        if (aport == null) throw new Exception("Aportación no encontrada");

        if (!aport.isEsVisible()) {
            // Si no es visible, no la exponemos
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN, "Aportación no visible");
        }

        model.addAttribute("proyecto", proyecto);
        model.addAttribute("aportacion", aport);
        return "usuario/verAportacion"; // plantilla de solo lectura
    }

}
