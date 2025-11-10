package com.example.honour_U_Springboot.repository;

import com.example.honour_U_Springboot.model.Proyecto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest  // 🔹 Carga solo la capa JPA (repositorios, entidades, BD en memoria)
@ActiveProfiles("test")  // 🔹 Usa configuración de src/test/resources/application-test.properties
class ProyectoRepositoryTest {

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Test
    void testGuardarYLeerProyecto() {
        // 1️⃣ Crear proyecto
        Proyecto proyecto = new Proyecto();
        proyecto.setNombreProyecto("Homenaje a Juan");
        proyecto.setOrganizador("Ana López");
        proyecto.setDescripcion("Proyecto de prueba");
        proyecto.setPlazoFinalizacion(LocalDate.of(2025, 12, 31));

        // 2️⃣ Guardarlo en la base de datos (H2)
        Proyecto guardado = proyectoRepository.save(proyecto);

        // 3️⃣ Comprobar que se generó un ID
        assertThat(guardado.getProyectoId()).isNotNull();

        // 4️⃣ Recuperarlo desde la base de datos
        Proyecto encontrado = proyectoRepository.findById(guardado.getProyectoId()).orElse(null);

        // 5️⃣ Verificar los valores
        assertThat(encontrado).isNotNull();
        assertThat(encontrado.getNombreProyecto()).isEqualTo("Homenaje a Juan");
        assertThat(encontrado.getOrganizador()).isEqualTo("Ana López");
    }
}
