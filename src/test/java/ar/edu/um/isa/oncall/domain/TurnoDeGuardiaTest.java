package ar.edu.um.isa.oncall.domain;

import static ar.edu.um.isa.oncall.domain.RotacionTestSamples.*;
import static ar.edu.um.isa.oncall.domain.TurnoDeGuardiaTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.um.isa.oncall.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TurnoDeGuardiaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TurnoDeGuardia.class);
        TurnoDeGuardia turnoDeGuardia1 = getTurnoDeGuardiaSample1();
        TurnoDeGuardia turnoDeGuardia2 = new TurnoDeGuardia();
        assertThat(turnoDeGuardia1).isNotEqualTo(turnoDeGuardia2);

        turnoDeGuardia2.setId(turnoDeGuardia1.getId());
        assertThat(turnoDeGuardia1).isEqualTo(turnoDeGuardia2);

        turnoDeGuardia2 = getTurnoDeGuardiaSample2();
        assertThat(turnoDeGuardia1).isNotEqualTo(turnoDeGuardia2);
    }

    @Test
    void rotacionTest() {
        TurnoDeGuardia turnoDeGuardia = getTurnoDeGuardiaRandomSampleGenerator();
        Rotacion rotacionBack = getRotacionRandomSampleGenerator();

        turnoDeGuardia.setRotacion(rotacionBack);
        assertThat(turnoDeGuardia.getRotacion()).isEqualTo(rotacionBack);

        turnoDeGuardia.rotacion(null);
        assertThat(turnoDeGuardia.getRotacion()).isNull();
    }
}
