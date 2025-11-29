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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Clase AportacionUsuarioViewController
 * Maneja las vistas del controlador para el manejo de aportaciones por parte del usuario
 * @author Natalia Fernández
 * @version 1
 */

@Controller
@RequestMapping("/proyectos/token")
public class AportacionUsuarioViewController {

    @Autowired private AportacionService aportacionService;
    @Autowired private ProyectoService proyectoService;

    /** Cookies **/

    /**
     * Genera un nombre de cookie seguro a partir del token del proyecto.
     * Reemplaza cualquier carácter que no sea letra, número, guion o guion bajo por '_'.
     * Esto evita errores con nombres de cookie que contengan caracteres especiales.
     */
    private String cookieNameFor(String token) {
        return "ok_" + token.replaceAll("[^A-Za-z0-9_-]", "_");
    }

    /**
     * Busca una cookie concreta en la petición HTTP.
     * Recorre todas las cookies del request y devuelve el valor si la encuentra.
     *  Si no existe la cookie o no hay cookies, devuelve null.
     * @param request petición HTTP que contiene las cookies
     * @param name Nombre de la cookie a buscar
     * @return valor de la cookie o null si no se encuentra
     */

    private String getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) if (name.equals(c.getName())) return c.getValue();
        return null;
    }

    /** LISTAR Y FORMULARIO DE APORTACIONES **/

    /**
     * Método para mostrar el formulario de las aportaciones
     * Gestiona la ownerKey del usuario mediante sesión y cookies,
     * identificando aportaciones propias sin usar login
     * Obtiene y filtra las aportaciones del proyecto en propias, de otros y todas
     * Prepara atributos necesarios para la vista del formulario de aportaciones
     * @param token único del proyecto
     * @param model para pasar datos a la vista
     * @param session http de usuario para guardar ownerKEY
     * @param request http, para leer cocokies
     * @param response respuesta http, para crear cookies si necesario
     * @return nombre de la vista a mostrar
     * @throws Exception si no encuentra el token
     */

    @GetMapping("/{token}/aportaciones")
    public String mostrarFormularioAportacion(@PathVariable String token,
                                              Model model,
                                              HttpSession session,
                                              HttpServletRequest request,
                                              HttpServletResponse response) throws Exception {

        Proyecto proyecto = proyectoService.findByTokenUrl(token);
        if (proyecto == null) throw new Exception("Proyecto no encontrado");

        // OwnerKey: identifica al visitante sin login
        //Se intenta recuperar la ownerKey desde la sesión o cookie
        String sessionKey = "ownerKey:" + token; //clave de la sesión
        String cookieName = cookieNameFor(token); //nombre de la cookie
        String ownerKey = (String) session.getAttribute(sessionKey);
        if (ownerKey == null) ownerKey = getCookie(request, cookieName);

        //Si no existe, se genera una nueva ownerKey y se guarda en cookie y sesión
        if (ownerKey == null) {
            ownerKey = UUID.randomUUID().toString();
            Cookie cookie = new Cookie(cookieName, ownerKey);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24 * 180);
            response.addCookie(cookie);
        }
        session.setAttribute(sessionKey, ownerKey);

        // Obtener aportaciones
        List<Aportacion> todas = aportacionService.findByProyecto(proyecto);

        // Filtrar aportaciones propias (NORMAL y ownerKey coincide)
        final String finalOwnerKey = ownerKey;
        List<Aportacion> mias = todas.stream()
                .filter(a -> a.getPageType() == Aportacion.PageType.NORMAL)
                .filter(a -> finalOwnerKey.equals(a.getOwnerKey()))
                .toList();


        // Filtrar aportaciones de otros participantes (NORMAL y ownerKey distinto)
        final String finalOwnerKey1 = ownerKey;
        List<Aportacion> otras = todas.stream()
                .filter(a -> a.getPageType() == Aportacion.PageType.NORMAL)
                .filter(a -> a.getOwnerKey() != null && !a.getOwnerKey().isBlank())
                .filter(a -> !finalOwnerKey1.equals(a.getOwnerKey()))
                .toList();

        // Aportaciones normales ordenadas para mostrar en la vista
        List<Aportacion> aportaciones = aportacionService.findByProyectoOrderado(proyecto)
                .stream()
                .filter(a -> a.getPageType() == Aportacion.PageType.NORMAL)
                .filter(a -> a.getOwnerKey() != null && !a.getOwnerKey().isBlank())
                .toList();

        // --- Pasar datos a la vista ---
        model.addAttribute("proyecto", proyecto);
        model.addAttribute("nuevaAportacion", new Aportacion());
        model.addAttribute("mias", mias);
        model.addAttribute("otras", otras);
        model.addAttribute("aportaciones", aportaciones);

        return "usuario/nuevaAportacion";
    }

    /** CREAR APORTACIÓN **/
    /**
     * Método para guardar una aportación de un proyecto
     * Busca el proyecto por token
     * Valida campos de aportaciones tipo NORMAL
     * Guarda inicialmente aportación para obtener ID y orden
     * Si hay archivos los guarda en carpetas y genera sus URLSs
     * Actualiza las aportaciones con las url de las imágenes y la url principal
     * Guarda de nuevo la aportación con datos completos
     * @param token del proyecto
     * @param aportacion objeto aportación recibida desde el formulario
     * @param imagenes (opcionales)
     * @param session http del usuario para guardar la ownerKey
     * @param request solicitud http para leer las cookies
     * @param model para enviar mensajes de error a la vista
     * @return redirección a la página de aportaciones del proyecto
     * @throws Exception si no encuentra el tocken
     */

    @PostMapping("/{token}/aportaciones")
    public String guardarAportacion(@PathVariable String token,
                                    @ModelAttribute("nuevaAportacion") Aportacion aportacion,
                                    @RequestParam(value = "imagenes", required = false) MultipartFile[] imagenes,
                                    HttpSession session,
                                    HttpServletRequest request,
                                    Model model) throws Exception {

        Proyecto proyecto = proyectoService.findByTokenUrl(token);
        if (proyecto == null) throw new Exception("Proyecto no encontrado");

        // Validaciones
        if (aportacion.getPageType() == Aportacion.PageType.NORMAL) {

            if (aportacion.getRemitente() == null || aportacion.getRemitente().isBlank()) {
                model.addAttribute("error", "El remitente no puede estar vacío");
                return "usuario/nuevaAportacion";
            }

            if (aportacion.getMensaje() == null || aportacion.getMensaje().isBlank()) {
                model.addAttribute("error", "El mensaje no puede estar vacío");
                return "usuario/nuevaAportacion";
            }

        } else {
            aportacion.setRemitente(null);
            aportacion.setMensaje(null);
        }

        // OwnerKey
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
        aportacion.setPageType(Aportacion.PageType.NORMAL);

        // 1) PRIMER GUARDADO → Genera ID y orden
        aportacionService.saveAportacion(aportacion);
        Long idGenerado = aportacion.getAportacionId();

        // 2) Manjeo de archivos
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

        if (urls.isEmpty()) {
            aportacion.setUrl("/proyectos/token/" + token + "/aportaciones/" + idGenerado + "/ver");
        } else {
            aportacion.setUrl(urls.get(0));
        }

        // 3) SEGUNDO GUARDADO → Sin recalcular orden/URL
        aportacionService.saveAportacion(aportacion);

        return "redirect:/proyectos/token/" + token + "/aportaciones";
    }

    /** EDITAR APORTACIÓN **/

    /**
     * Método para editar aportación
     * Encuentra proyecto por token
     * Abre la owner key en la sesión y la cookie para editarla
     * (si no se encuentra no se puede editar)
     * obtiene la aportación a editar
     * @param token del proyecto
     * @param aportacionId id de la aportación
     * @param model para ver los datos en la vista
     * @param session http del usuario para obtener la ownerKey
     * @param request solicitud http para leer las cookies
     * @return la vista con la aportación a editar
     * @throws Exception si no se encuentra el proyecto o la aportación
     */

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

        String ownerKey = (String) session.getAttribute("ownerKey:" + token);
        if (ownerKey == null) ownerKey = getCookie(request, cookieNameFor(token));

        if (ownerKey == null || !ownerKey.equals(aport.getOwnerKey())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes editar esta aportación");
        }

        model.addAttribute("proyecto", proyecto);
        model.addAttribute("aportacion", aport);

        return "usuario/editarAportacion";
    }

    /** EDICIÓN DE APORTACIONES **/

    /**
     * Método para actualizar la aportación
     * Busca en la sesión el ownerkey y la cookie
     * Actualiza texto e imágenes
     * @param token del proyecto
     * @param aportacionId id de la aportación
     * @param form formulario con la información
     * @param nuevasImagenes (opcional) nuevas imágenes para actualizar aportaciones
     * @param session http del usuario para obtener la ownerKey
     * @param request solicitud http para leer las cookies
     * @return la página de aportaciones actualizada
     * @throws Exception si la aportación no se puede editar al no encontrar cookie guardada
     */

    @PostMapping("/{token}/aportaciones/{aportacionId}/editar")
    public String actualizarAportacion(@PathVariable String token,
                                       @PathVariable Long aportacionId,
                                       @ModelAttribute("aportacion") Aportacion form,
                                       @RequestParam(value="imagenes", required=false) MultipartFile[] nuevasImagenes,
                                       HttpSession session,
                                       HttpServletRequest request) throws Exception {


        Proyecto proyecto = proyectoService.findByTokenUrl(token);
        Aportacion aport = aportacionService.findAportacionById(aportacionId);

        String ownerKey = (String) session.getAttribute("ownerKey:" + token);
        if (ownerKey == null) ownerKey = getCookie(request, cookieNameFor(token));

        if (!ownerKey.equals(aport.getOwnerKey())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes editar esta aportación");
        }

        // 1) Actualizar texto mediante service (lógica de negocio)
        aportacionService.updateAportacion(aportacionId, form);

        // 2) Manejar archivos (solo si hay nuevos)
        List<String> urls = new ArrayList<>();

        if (nuevasImagenes != null && nuevasImagenes.length > 0) {

            Path dir = Paths.get(System.getProperty("user.dir"), "uploads",
                    proyecto.getTokenUrl(),
                    String.valueOf(aport.getAportacionId()));

            try {
                if (Files.exists(dir)) {
                    Files.walk(dir)
                            .sorted(Comparator.reverseOrder())
                            .forEach(p -> { try { Files.deleteIfExists(p); } catch (Exception ignore) {} });
                }
            } catch (Exception ignore) {}

            // Subir nuevos
            String baseDir = System.getProperty("user.dir") + "/uploads/"
                    + proyecto.getTokenUrl() + "/" + aport.getAportacionId() + "/";
            Path carpeta = Paths.get(baseDir);
            Files.createDirectories(carpeta);

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
            aport.setUrl(urls.isEmpty()
                    ? "/proyectos/token/" + token + "/aportaciones/" + aport.getAportacionId() + "/ver"
                    : urls.get(0));

            aportacionService.saveAportacion(aport);
        }

        return "redirect:/proyectos/token/" + token + "/aportaciones";
    }

    /** VER APORTACIÓN **/
    /**
     * Método para mostrar la aportación si es pública
     * @param token del proyecto
     * @param aportacionId id de la aportación
     * @param model Modelo para pasar los datos a la vista
     * @return la aportación
     * @throws Exception si no es visible
     */

    @GetMapping("/{token}/aportaciones/{aportacionId}/ver")
    public String verAportacionPublica(@PathVariable String token,
                                       @PathVariable Long aportacionId,
                                       Model model) throws Exception {

        Proyecto proyecto = proyectoService.findByTokenUrl(token);
        Aportacion aport = aportacionService.findAportacionById(aportacionId);

        if (!aport.isEsVisible()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Aportación no visible");
        }

        model.addAttribute("proyecto", proyecto);
        model.addAttribute("aportacion", aport);

        return "usuario/verAportacion";
    }

    /**
     * Método para eliminar aportación
     * Encuentra la aportación por proyecto y id
     * Busca la cookie en la sesión en la que está guardada la ownerKewy
     * @param token del proyecto
     * @param aportacionId id de la aportación
     * @param session http del usuario que guarda la ownerKey
     * @param request solicitud http para leer cookies
     * @return la vista de las aportaciones actualizadas
     * @throws Exception si no encuentra la cookie o si la aportación no es de tipo NORMAL
     */

    @PostMapping("/{token}/aportaciones/{aportacionId}/eliminar")
    public String eliminarAportacion(@PathVariable String token,
                                     @PathVariable Long aportacionId,
                                     HttpSession session,
                                     HttpServletRequest request) throws Exception {

        Proyecto proyecto = proyectoService.findByTokenUrl(token);
        Aportacion aport = aportacionService.findAportacionById(aportacionId);

        if (aport.getPageType() != Aportacion.PageType.NORMAL) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes eliminar portada/índice");
        }

        String ownerKey = (String) session.getAttribute("ownerKey:" + token);
        if (ownerKey == null) ownerKey = getCookie(request, cookieNameFor(token));

        if (!ownerKey.equals(aport.getOwnerKey())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes eliminar esta aportación");
        }

        // Borrar archivos físicos
        try {
            Path dir = Paths.get(System.getProperty("user.dir"), "uploads",
                    proyecto.getTokenUrl(),
                    String.valueOf(aport.getAportacionId()));

            if (Files.exists(dir)) {
                Files.walk(dir)
                        .sorted(Comparator.reverseOrder())
                        .forEach(p -> { try { Files.deleteIfExists(p); } catch (Exception ignore) {} });
            }
        } catch (Exception ignore) {}

        proyecto.getAportaciones().removeIf(a -> a.getAportacionId().equals(aportacionId));
        aportacionService.deleteAportacionById(aportacionId);

        return "redirect:/proyectos/token/" + token + "/aportaciones";
    }

}
