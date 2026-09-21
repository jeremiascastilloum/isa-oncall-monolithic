package ar.edu.um.isa.oncall.domain;

import static ar.edu.um.isa.oncall.domain.AlertaTestSamples.*;
import static ar.edu.um.isa.oncall.domain.IncidenteTestSamples.*;
import static ar.edu.um.isa.oncall.domain.ServicioTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.um.isa.oncall.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AlertaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Alerta.class);
        Alerta alerta1 = getAlertaSample1();
        Alerta alerta2 = new Alerta();
        assertThat(alerta1).isNotEqualTo(alerta2);

        alerta2.setId(alerta1.getId());
        assertThat(alerta1).isEqualTo(alerta2);

        alerta2 = getAlertaSample2();
        assertThat(alerta1).isNotEqualTo(alerta2);
    }

    @Test
    void servicioTest() {
        Alerta alerta = getAlertaRandomSampleGenerator();
        Servicio servicioBack = getServicioRandomSampleGenerator();

        alerta.setServicio(servicioBack);
        assertThat(alerta.getServicio()).isEqualTo(servicioBack);

        alerta.servicio(null);
        assertThat(alerta.getServicio()).isNull();
    }

    @Test
    void incidenteTest() {
        Alerta alerta = getAlertaRandomSampleGenerator();
        Incidente incidenteBack = getIncidenteRandomSampleGenerator();

        alerta.setIncidente(incidenteBack);
        assertThat(alerta.getIncidente()).isEqualTo(incidenteBack);

        alerta.incidente(null);
        assertThat(alerta.getIncidente()).isNull();
    }
}
