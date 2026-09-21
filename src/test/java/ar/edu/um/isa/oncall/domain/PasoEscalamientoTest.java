package ar.edu.um.isa.oncall.domain;

import static ar.edu.um.isa.oncall.domain.PasoEscalamientoTestSamples.*;
import static ar.edu.um.isa.oncall.domain.PoliticaEscalamientoTestSamples.*;
import static ar.edu.um.isa.oncall.domain.RotacionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.um.isa.oncall.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PasoEscalamientoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PasoEscalamiento.class);
        PasoEscalamiento pasoEscalamiento1 = getPasoEscalamientoSample1();
        PasoEscalamiento pasoEscalamiento2 = new PasoEscalamiento();
        assertThat(pasoEscalamiento1).isNotEqualTo(pasoEscalamiento2);

        pasoEscalamiento2.setId(pasoEscalamiento1.getId());
        assertThat(pasoEscalamiento1).isEqualTo(pasoEscalamiento2);

        pasoEscalamiento2 = getPasoEscalamientoSample2();
        assertThat(pasoEscalamiento1).isNotEqualTo(pasoEscalamiento2);
    }

    @Test
    void politicaTest() {
        PasoEscalamiento pasoEscalamiento = getPasoEscalamientoRandomSampleGenerator();
        PoliticaEscalamiento politicaEscalamientoBack = getPoliticaEscalamientoRandomSampleGenerator();

        pasoEscalamiento.setPolitica(politicaEscalamientoBack);
        assertThat(pasoEscalamiento.getPolitica()).isEqualTo(politicaEscalamientoBack);

        pasoEscalamiento.politica(null);
        assertThat(pasoEscalamiento.getPolitica()).isNull();
    }

    @Test
    void rotacionTest() {
        PasoEscalamiento pasoEscalamiento = getPasoEscalamientoRandomSampleGenerator();
        Rotacion rotacionBack = getRotacionRandomSampleGenerator();

        pasoEscalamiento.setRotacion(rotacionBack);
        assertThat(pasoEscalamiento.getRotacion()).isEqualTo(rotacionBack);

        pasoEscalamiento.rotacion(null);
        assertThat(pasoEscalamiento.getRotacion()).isNull();
    }
}
