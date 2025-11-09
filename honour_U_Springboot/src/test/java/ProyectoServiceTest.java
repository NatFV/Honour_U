import com.example.honour_U_Springboot.model.Proyecto;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ProyectoTest {

    @Test
    void ensureTokens_generaTokensSiNoExisten() {
        Proyecto p = new Proyecto();
        p.setNombreProyecto("Homenaje Luis");
        p.setOrganizador("Ana");
        p.setDescripcion("desc");
        p.setPlazoFinalizacion(LocalDate.now().plusDays(30));

        // Antes: null
        assertThat(p.getAdminToken()).isNull();
        assertThat(p.getTokenUrl()).isNull();

        // Act
        p.ensureTokens();

        // Después: generados
        assertThat(p.getAdminToken()).isNotBlank();
        assertThat(p.getTokenUrl()).isNotBlank();
    }

    @Test
    void ensureTokens_noSobrescribeTokensSiYaExisten() {
        Proyecto p = new Proyecto();
        p.setAdminToken("admin-fixed");
        p.setTokenUrl("aport-fixed");

        p.ensureTokens();

        assertThat(p.getAdminToken()).isEqualTo("admin-fixed");
        assertThat(p.getTokenUrl()).isEqualTo("aport-fixed");
    }
}