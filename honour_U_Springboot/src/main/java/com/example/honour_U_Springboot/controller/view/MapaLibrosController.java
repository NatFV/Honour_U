package com.example.honour_U_Springboot.controller.view;

import com.example.honour_U_Springboot.service.DireccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

/**
 * Clase MapaLibrosController
 * Maneja las vistas del mapa
 */
@Controller
public class MapaLibrosController {

    @Autowired
    private DireccionService direccionService;

    /**
     * Método mostrar mapa libros
     * @param model que conecta el controlador con la vista
     * @return  una vista con un mapa de libros por país
     */
    @GetMapping("/mapa-libros-v2")
    public String mostrarMapaLibros(Model model) {
        Map<String, Long> librosPorPais = direccionService.contarLibrosPorPais();
        model.addAttribute("librosPorPais", librosPorPais);
        return "mapaLibros";
    }
}

