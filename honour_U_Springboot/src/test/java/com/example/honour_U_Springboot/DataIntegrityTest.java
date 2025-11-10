package com.example.honour_U_Springboot;

import com.example.honour_U_Springboot.model.*;
import com.example.honour_U_Springboot.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class DataIntegrityTest {

    @Autowired
    private ProyectoRepository proyectoRepository;
    @Autowired
    private AportacionRepository aportacionRepository;
    @Autowired
    private DestinatarioRepository destinatarioRepository;
    @Autowired
    private DireccionRepository direccionRepository;
    @Autowired
    private LibroRepository libroRepository;

    /**
     * TI-09 — Integridad en cascada entre Destinatario y Direcciones
     */
    @Test
    void testCascadaDestinatarioDirecciones() {
        Destinatario destinatario = new Destinatario();
        destinatario.setNombre("María");
        destinatario.setApellido("Gómez");

        Direccion dir1 = new Direccion();
        dir1.setCalle("Mayor");
        dir1.setNumero("10");
        dir1.setCodigoPostal("28001");
        dir1.setPais("España");
        dir1.setLetra("B");
        dir1.setPiso("2");
        dir1.setDestinatario(destinatario);

        Direccion dir2 = new Direccion();
        dir2.setCalle("Sol");
        dir2.setNumero("22");
        dir2.setCodigoPostal("28002");
        dir2.setPais("España");
        dir2.setLetra("A");
        dir2.setPiso("1");
        dir2.setDestinatario(destinatario);

        destinatario.setDirecciones(Set.of(dir1, dir2));
        destinatario = destinatarioRepository.save(destinatario);

        assertEquals(2, direccionRepository.findByDestinatario(destinatario).size());

        // Eliminar destinatario y comprobar eliminación en cascada
        destinatarioRepository.delete(destinatario);
        destinatarioRepository.flush();

        assertEquals(0, direccionRepository.findByDestinatario(destinatario).size(),
                "Las direcciones deberían eliminarse junto con el destinatario");
    }

    /**
     * TI-10 — Restricciones de nulidad en campos obligatorios
     */
    @Test
    void testGuardarLibroInvalido() {
        Libro libro = new Libro();
        libro.setFormato("Digital");
        libro.setCopias(3);
        libro.setPaginas(50);

        assertThrows(DataIntegrityViolationException.class, () -> {
            libroRepository.saveAndFlush(libro);
        });
    }

    @Test
    void testGuardarLibroValido() {
        Libro libro = new Libro();
        libro.setTituloLibro("Libro válido");
        libro.setFormato("Digital");
        libro.setCopias(3);
        libro.setPaginas(50);

        assertDoesNotThrow(() -> {
            libroRepository.saveAndFlush(libro);
        });
    }
}
