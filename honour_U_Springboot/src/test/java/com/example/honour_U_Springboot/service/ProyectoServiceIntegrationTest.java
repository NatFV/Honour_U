package com.example.honour_U_Springboot.service;

import com.example.honour_U_Springboot.model.Proyecto;
import com.example.honour_U_Springboot.repository.ProyectoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Prueba de integración completa: Servicio + Repositorio + BD H2
 * @author Natalia Fernandez
 * @version 1
 */
@SpringBootTest  // Carga todo el contexto de Spring Boot
@ActiveProfiles("test")  // Usa configuración de src/test/resources/application-test.properties
class ProyectoServiceIntegrationTest {

    @Autowired
    private ProyectoService proyectoService;

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Test
    void testGuardarYRecuperarProyectoDesdeElServicio() {
        // Crear nuevo proyecto
        Proyecto proyecto = new Proyecto();
        proyecto.setNombreProyecto("Homenaje a Pablo");
        proyecto.setOrganizador("Marta Gómez");
        proyecto.setDescripcion("Proyecto de prueba de integración completa");
        proyecto.setPlazoFinalizacion(LocalDate.of(2025, 12, 31));

        // Guardar usando el servicio (esto debería invocar el repositorio real)
        Proyecto guardado = proyectoService.saveProyecto(proyecto);

        //  Verificar que se generó ID y token
        assertThat(guardado.getProyectoId()).isNotNull();
        assertThat(guardado.getTokenUrl()).isNotNull();

        //  Recuperar el mismo proyecto por token desde el servicio
        Proyecto encontrado = proyectoService.findByTokenUrl(guardado.getTokenUrl());
        assertThat(encontrado).isNotNull();
        assertThat(encontrado.getNombreProyecto()).isEqualTo("Homenaje a Pablo");

        //  Verificar que realmente está en la base de datos
        Proyecto desdeBD = proyectoRepository.findById(guardado.getProyectoId()).orElse(null);
        assertThat(desdeBD).isNotNull();
        assertThat(desdeBD.getOrganizador()).isEqualTo("Marta Gómez");
    }

    @Test
    void testActualizarProyecto() {
        //  Crear y guardar
        Proyecto proyecto = new Proyecto();
        proyecto.setNombreProyecto("Homenaje original");
        proyecto.setOrganizador("Laura");
        proyecto.setDescripcion("Versión original");
        proyecto.setPlazoFinalizacion(LocalDate.of(2025, 6, 30));

        Proyecto guardado = proyectoService.saveProyecto(proyecto);

        //  Modificar algunos campos
        guardado.setDescripcion("Versión actualizada");
        guardado.setOrganizador("Laura Actualizada");

        // Actualizar con el servicio
        Proyecto actualizado = proyectoService.updateProyecto(guardado.getProyectoId(), guardado);

        //  Verificar cambios
        assertThat(actualizado.getDescripcion()).isEqualTo("Versión actualizada");
        assertThat(actualizado.getOrganizador()).isEqualTo("Laura Actualizada");
    }

    @Test
    void testEliminarProyecto() {
        // Crear y guardar
        Proyecto proyecto = new Proyecto();
        proyecto.setNombreProyecto("Proyecto a eliminar");
        proyecto.setOrganizador("Carlos");
        proyecto.setDescripcion("Será eliminado");
        proyecto.setPlazoFinalizacion(LocalDate.of(2025, 11, 1));

        Proyecto guardado = proyectoService.saveProyecto(proyecto);

        Long id = guardado.getProyectoId();

        // Eliminarlo
        proyectoService.deleteProyectoById(id);

        //  Confirmar que ya no existe
        assertThat(proyectoRepository.findById(id)).isEmpty();
    }
}
