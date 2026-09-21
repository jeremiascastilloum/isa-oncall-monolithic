package ar.edu.um.isa.oncall.domain;

import static ar.edu.um.isa.oncall.domain.PasoEscalamientoTestSamples.*;
import static ar.edu.um.isa.oncall.domain.PoliticaEscalamientoTestSamples.*;
import static ar.edu.um.isa.oncall.domain.ServicioTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.um.isa.oncall.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PoliticaEscalamientoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PoliticaEscalamiento.class);
        PoliticaEscalamiento politicaEscalamiento1 = getPoliticaEscalamientoSample1();
        PoliticaEscalamiento politicaEscalamiento2 = new PoliticaEscalamiento();
        assertThat(politicaEscalamiento1).isNotEqualTo(politicaEscalamiento2);

        politicaEscalamiento2.setId(politicaEscalamiento1.getId());
        assertThat(politicaEscalamiento1).isEqualTo(politicaEscalamiento2);

        politicaEscalamiento2 = getPoliticaEscalamientoSample2();
        assertThat(politicaEscalamiento1).isNotEqualTo(politicaEscalamiento2);
    }

    @Test
    void servicioTest() {
        PoliticaEscalamiento politicaEscalamiento = getPoliticaEscalamientoRandomSampleGenerator();
        Servicio servicioBack = getServicioRandomSampleGenerator();

        politicaEscalamiento.setServicio(servicioBack);
        assertThat(politicaEscalamiento.getServicio()).isEqualTo(servicioBack);

        politicaEscalamiento.servicio(null);
        assertThat(politicaEscalamiento.getServicio()).isNull();
    }

    @Test
    void pasoTest() {
        PoliticaEscalamiento politicaEscalamiento = getPoliticaEscalamientoRandomSampleGenerator();
        PasoEscalamiento pasoEscalamientoBack = getPasoEscalamientoRandomSampleGenerator();

        politicaEscalamiento.addPaso(pasoEscalamientoBack);
        assertThat(politicaEscalamiento.getPasos()).containsOnly(pasoEscalamientoBack);
        assertThat(pasoEscalamientoBack.getPolitica()).isEqualTo(politicaEscalamiento);

        politicaEscalamiento.removePaso(pasoEscalamientoBack);
        assertThat(politicaEscalamiento.getPasos()).doesNotContain(pasoEscalamientoBack);
        assertThat(pasoEscalamientoBack.getPolitica()).isNull();

        politicaEscalamiento.pasos(new HashSet<>(Set.of(pasoEscalamientoBack)));
        assertThat(politicaEscalamiento.getPasos()).containsOnly(pasoEscalamientoBack);
        assertThat(pasoEscalamientoBack.getPolitica()).isEqualTo(politicaEscalamiento);

        politicaEscalamiento.setPasos(new HashSet<>());
        assertThat(politicaEscalamiento.getPasos()).doesNotContain(pasoEscalamientoBack);
        assertThat(pasoEscalamientoBack.getPolitica()).isNull();
    }
}
