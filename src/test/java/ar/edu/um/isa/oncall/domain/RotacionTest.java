package ar.edu.um.isa.oncall.domain;

import static ar.edu.um.isa.oncall.domain.EquipoTestSamples.*;
import static ar.edu.um.isa.oncall.domain.PasoEscalamientoTestSamples.*;
import static ar.edu.um.isa.oncall.domain.RotacionTestSamples.*;
import static ar.edu.um.isa.oncall.domain.TurnoDeGuardiaTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.um.isa.oncall.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class RotacionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Rotacion.class);
        Rotacion rotacion1 = getRotacionSample1();
        Rotacion rotacion2 = new Rotacion();
        assertThat(rotacion1).isNotEqualTo(rotacion2);

        rotacion2.setId(rotacion1.getId());
        assertThat(rotacion1).isEqualTo(rotacion2);

        rotacion2 = getRotacionSample2();
        assertThat(rotacion1).isNotEqualTo(rotacion2);
    }

    @Test
    void equipoTest() {
        Rotacion rotacion = getRotacionRandomSampleGenerator();
        Equipo equipoBack = getEquipoRandomSampleGenerator();

        rotacion.setEquipo(equipoBack);
        assertThat(rotacion.getEquipo()).isEqualTo(equipoBack);

        rotacion.equipo(null);
        assertThat(rotacion.getEquipo()).isNull();
    }

    @Test
    void turnoTest() {
        Rotacion rotacion = getRotacionRandomSampleGenerator();
        TurnoDeGuardia turnoDeGuardiaBack = getTurnoDeGuardiaRandomSampleGenerator();

        rotacion.addTurno(turnoDeGuardiaBack);
        assertThat(rotacion.getTurnos()).containsOnly(turnoDeGuardiaBack);
        assertThat(turnoDeGuardiaBack.getRotacion()).isEqualTo(rotacion);

        rotacion.removeTurno(turnoDeGuardiaBack);
        assertThat(rotacion.getTurnos()).doesNotContain(turnoDeGuardiaBack);
        assertThat(turnoDeGuardiaBack.getRotacion()).isNull();

        rotacion.turnos(new HashSet<>(Set.of(turnoDeGuardiaBack)));
        assertThat(rotacion.getTurnos()).containsOnly(turnoDeGuardiaBack);
        assertThat(turnoDeGuardiaBack.getRotacion()).isEqualTo(rotacion);

        rotacion.setTurnos(new HashSet<>());
        assertThat(rotacion.getTurnos()).doesNotContain(turnoDeGuardiaBack);
        assertThat(turnoDeGuardiaBack.getRotacion()).isNull();
    }

    @Test
    void pasoTest() {
        Rotacion rotacion = getRotacionRandomSampleGenerator();
        PasoEscalamiento pasoEscalamientoBack = getPasoEscalamientoRandomSampleGenerator();

        rotacion.addPaso(pasoEscalamientoBack);
        assertThat(rotacion.getPasos()).containsOnly(pasoEscalamientoBack);
        assertThat(pasoEscalamientoBack.getRotacion()).isEqualTo(rotacion);

        rotacion.removePaso(pasoEscalamientoBack);
        assertThat(rotacion.getPasos()).doesNotContain(pasoEscalamientoBack);
        assertThat(pasoEscalamientoBack.getRotacion()).isNull();

        rotacion.pasos(new HashSet<>(Set.of(pasoEscalamientoBack)));
        assertThat(rotacion.getPasos()).containsOnly(pasoEscalamientoBack);
        assertThat(pasoEscalamientoBack.getRotacion()).isEqualTo(rotacion);

        rotacion.setPasos(new HashSet<>());
        assertThat(rotacion.getPasos()).doesNotContain(pasoEscalamientoBack);
        assertThat(pasoEscalamientoBack.getRotacion()).isNull();
    }
}
