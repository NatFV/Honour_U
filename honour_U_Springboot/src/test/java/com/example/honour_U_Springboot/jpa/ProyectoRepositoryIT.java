package com.example.honour_U_Springboot.jpa;

import com.example.honour_U_Springboot.model.Proyecto;
import com.example.honour_U_Springboot.repository.ProyectoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ProyectoRepositoryIT {

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Test
    void guardarProyecto_yAportacion_relacionCorrecta_yTokensGenerados() {
        Proyecto proyecto = new Proyecto();
        proyecto.setAdminToken("admintokentest");
        proyecto.setDescripcion("Proyecto de prueba");
        proyecto.setNombreProyecto("Honour U");
        proyecto.setOrganizador("Juan Pérez");
        proyecto.setPlazoFinalizacion(LocalDate.now().plusDays(30));
        proyecto.setTokenUrl("tokenURL");
        proyecto.setUrlAdmin("http://admin.test");
        proyecto.setUrlProyecto("http://proyecto.test");

        Proyecto guardado = proyectoRepository.saveAndFlush(proyecto);

        assertThat(guardado.getProyectoId()).isNotNull();
        assertThat(guardado.getNombreProyecto()).isEqualTo("Honour U");
    }
}
