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

    // ---------- Cookies ----------
    //Se utilizan para recordar el autor de las aportaciones en un sistema
    //sin login para fomentar la colaboración directa de los participantes

    /**
     * Método para crear un nombre de cookie por navegador y proyecto
     * @param token del proyecto
     * @return un nombre de cookie por proyecto que no rompe las reglas de http
     */
    private String cookieNameFor(String token) {
        return "ok_" + token.replaceAll("[^A-Za-z0-9_-]", "_");
    }

    /**
     * Método para buscaruna cookie guardada previamente
     * @param request que es la petición que el navegador envía al servidor,
     *                para que muestre las cookies guardadas
     * @param name el nombre de la cookie
     * @return el valor si encuentra la cookie y si no la encuentra devuelve null
     * De esa manera se podrá editar/eliminar aportación
     */
    private String getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) if (name.equals(c.getName())) return c.getValue();
        return null;
    }

    /**
     * Método para mostrar aportaciones
     * @param token del proyecto
     * @param model que expone los datos a la plantilla de Thymeleaf
     * @param session almacena datos por usuario para conservarlos entre peticiones
     * @param request para hacer peticiones al servidor
     * @param response para recibir respuestas del servidor
     * @return la vista de nueva Aportación y aportaciones existentes
     * @throws Exception si no encuentra el proyecto
     */
    @GetMapping("/{token}/aportaciones")
    public String mostrarFormularioAportacion(@PathVariable String token,
                                              Model model,
                                              HttpSession session,
                                              HttpServletRequest request,
                                              HttpServletResponse response) throws Exception {
        Proyecto proyecto = proyectoService.findByTokenUrl(token);
        if (proyecto == null) throw new Exception("Proyecto no encontrado");

        //Captura la ownerkey de la sessión y genera la cookie
        String sessionKey = "ownerKey:" + token;     // en sesión puede llevar ':'
        String cookieName = cookieNameFor(token);    // la cookie NO
        //El servidor busca las ownerkeys disponibles en la sesión y en la cookie
        String ownerKey = (String) session.getAttribute(sessionKey);
        if (ownerKey == null) ownerKey = getCookie(request, cookieName);
        //Si no la hay crea una ownerKey y una cookie que dura un máximo de 180 días
        if (ownerKey == null) {
            ownerKey = UUID.randomUUID().toString();
            Cookie cookie = new Cookie(cookieName, ownerKey);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24 * 180);
            response.addCookie(cookie);
        }
        session.setAttribute(sessionKey, ownerKey);

        // Obtención de todas las aportaciones del proyecto
        List<Aportacion> todas = aportacionService.findByProyecto(proyecto);
        //Filtro paraa las aportaciones propias clasificadas como normal
        //Si la ownerkey de las aportaciones coinciden con las de la sesión, se obtienen
        //las aportaciones propias
        String finalOwnerKey = ownerKey;
        List<Aportacion> mias = todas.stream()
                .filter(a -> a.getPageType() == Aportacion.PageType.NORMAL)
                .filter(a -> finalOwnerKey.equals(a.getOwnerKey()))
                .toList();

        // Filtro para  obtener las aportaciones de otros participantes
        // Si la ownerkey es distinta de null y no es una página en blanco se obtiene
        //las aportaciones de otros participantes
        String finalOwnerKey1 = ownerKey;
        List<Aportacion> otras = todas.stream()
                .filter(a -> a.getPageType() == Aportacion.PageType.NORMAL)
                .filter(a -> a.getOwnerKey() != null && !a.getOwnerKey().isBlank())
                .filter(a -> !finalOwnerKey1.equals(a.getOwnerKey()))
                .toList();

        //lista única ORDENADA y filtrada en servidor
        List<Aportacion> ordenadas = aportacionService.findByProyectoOrderado(proyecto)
                .stream()
                .filter(a -> a.getPageType() == Aportacion.PageType.NORMAL)
                .filter(a -> a.getOwnerKey() != null && !a.getOwnerKey().isBlank())
                .toList();

        //Se trasladan los datos del controlador a la vista (Thymeleaf)
        model.addAttribute("proyecto", proyecto);
        model.addAttribute("nuevaAportacion", new Aportacion());
        model.addAttribute("mias", mias);
        model.addAttribute("otras", otras);
       // model.addAttribute("sessionOwnerKey", ownerKey); // por si lo necesitas en la vista

        // Cargar aportaciones ordenadas
        List<Aportacion> aportaciones = aportacionService.findByProyectoOrderado(proyecto)
                .stream()
                .filter(a -> a.getPageType() == Aportacion.PageType.NORMAL)
                .filter(a -> a.getOwnerKey() != null && !a.getOwnerKey().isBlank())
                .toList();
        model.addAttribute("aportaciones", aportaciones);

        return "usuario/nuevaAportacion";
    }

    /**
     * Método para guardar aportaciones
     * @param token del proyecto
     * @param aportacion a guardar
     * @param imagenes a guardar
     * @param session almacena datos por usuario para conservarlos entre peticiones
     * @param request para hacer peticiones
     * @return el link con las aportaciones actualizadas
     * @throws Exception si no encuentra proyecto
     */
    @PostMapping("/{token}/aportaciones")
    public String guardarAportacion(@PathVariable String token,
                                    @ModelAttribute("nuevaAportacion") Aportacion aportacion,
                                    @RequestParam(value = "imagenes", required = false) MultipartFile[] imagenes,
                                    HttpSession session,
                                    HttpServletRequest request, Model model) throws Exception {

        // Validación mínima
        if (aportacion.getRemitente() == null || aportacion.getRemitente().isBlank()) {
            model.addAttribute("error", "El remitente no puede estar vacío");
            return "usuario/nuevaAportacion"; // plantilla del formulario
        }
        if (aportacion.getMensaje() == null || aportacion.getMensaje().isBlank()) {
            model.addAttribute("error", "El mensaje no puede estar vacío");
            return "usuario/nuevaAportacion"; // plantilla del formulario
        }
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

        // Guarda temporalmente para generar el ID
        aportacionService.saveAportacion(aportacion);
        Long idGenerado = aportacion.getAportacionId();

        // Subida de archivos
        //Creación de carpetas en disco
        //Ruta final:
        String baseDir = System.getProperty("user.dir") + "/uploads/"
                + proyecto.getTokenUrl() + "/" + idGenerado + "/";
        Path carpeta = Paths.get(baseDir);
        Files.createDirectories(carpeta);

        //Creación de una lista para guardar URLS de los archivos
        List<String> urls = new ArrayList<>();
        if (imagenes != null) {
            for (MultipartFile img : imagenes) {
                if (img == null || img.isEmpty()) continue;
                String original = img.getOriginalFilename();
                //quita caracteres raros y le antepone un timestamp para evitar colisiones
                String safe = System.currentTimeMillis() + "_" +
                        (original != null ? original.replaceAll("[^a-zA-Z0-9._-]", "_") : "archivo");
                Path destino = carpeta.resolve(safe);
                img.transferTo(destino.toFile());
                String urlRel = "/uploads/" + proyecto.getTokenUrl() + "/" + idGenerado + "/" + safe;
                urls.add(urlRel);
            }
        }
        //guarda los urls en la entidad de aportación
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

    /**
     * Método para editar aportación
     * @param token del proyecto
     * @param aportacionId
     * @param model para exponer los datos a la plantilla de Thymeleaf
     * @param session almacena datos por usuario para conservarlos entre peticiones
     * @param request para hacer peticiones
     * @return el formulario para editar aportación
     * @throws Exception si no encuentra el proyecto
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

    /**
     * Método para editar aportación (POST)
     * @param token del proyecto
     * @param aportacionId
     * @param form con la aportación a editar
     * @param nuevasImagenes en caso de que se añadan
     * @param session almacena datos por usuario para conservarlos entre peticiones
     * @param request para hacer peticiones
     * @return el link con las aportaciones actualizadas
     * @throws Exception si no encuentra el proyecto
     */
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

        // Actualizar texto
        aport.setMensaje(form.getMensaje() == null ? "" : form.getMensaje());
        aport.setRemitente((form.getRemitente() == null || form.getRemitente().isBlank())
                ? "Anónimo"
                : form.getRemitente());
        aport.setEsVisible(form.isEsVisible());
        aport.setPageType(Aportacion.PageType.NORMAL);

        // Obtener los medios actuales
        List<String> urls = (aport.getMediaUrls() != null)
                ? new ArrayList<>(aport.getMediaUrls())
                : new ArrayList<>();

        // Reemplazar medios anteriores si se suben nuevos
        if (nuevasImagenes != null && nuevasImagenes.length > 0) {

            // Borrar archivos del disco
            Path dir = Paths.get(System.getProperty("user.dir"), "uploads",
                    proyecto.getTokenUrl(), String.valueOf(aport.getAportacionId()));
            try {
                if (Files.exists(dir)) {
                    Files.walk(dir)
                            .sorted(Comparator.reverseOrder())
                            .forEach(p -> { try { Files.deleteIfExists(p); } catch (Exception ignore) {} });
                }
            } catch (Exception ignore) {}

            urls.clear(); // <-- borrar lista de URLs anteriores
        }

        // Subir nuevos archivos
        if (nuevasImagenes != null && nuevasImagenes.length > 0) {
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
        }

        // Guardar URLs actualizadas
        aport.setMediaUrls(urls);

        // Actualizar la URL general (enlace para ver la aportación)
        aport.setUrl(urls.isEmpty()
                ? "/proyectos/token/" + token + "/aportaciones/" + aport.getAportacionId() + "/ver"
                : urls.get(0));

        aportacionService.saveAportacion(aport);

        return "redirect:/proyectos/token/" + token + "/aportaciones";
    }


    /**
     * Método para mostrar las aportaciones de otros participantes
     * en caso de ser visibles
     * @param token del proyecto
     * @param aportacionId
     * @param model que expone los datos a la plantilla de Thymeleaf
     * @return el formulario ver Aportación
     * @throws Exception si no encuentra el proyecto
     */
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

    /**
     * Método para eliminar aportaciones
     * @param token del proyecto
     * @param aportacionId
     * @param session almacena datos por usuario para conservarlos entre peticiones
     * @param request para hacer peticiones
     * @return el link con la lista actualizada de aportaciones
     * @throws Exception si no encuentra el proyecto
     */
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
