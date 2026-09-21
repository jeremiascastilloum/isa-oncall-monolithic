package ar.edu.um.isa.oncall.domain;

import static ar.edu.um.isa.oncall.domain.IncidenteTestSamples.*;
import static ar.edu.um.isa.oncall.domain.NotificacionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.um.isa.oncall.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NotificacionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Notificacion.class);
        Notificacion notificacion1 = getNotificacionSample1();
        Notificacion notificacion2 = new Notificacion();
        assertThat(notificacion1).isNotEqualTo(notificacion2);

        notificacion2.setId(notificacion1.getId());
        assertThat(notificacion1).isEqualTo(notificacion2);

        notificacion2 = getNotificacionSample2();
        assertThat(notificacion1).isNotEqualTo(notificacion2);
    }

    @Test
    void incidenteTest() {
        Notificacion notificacion = getNotificacionRandomSampleGenerator();
        Incidente incidenteBack = getIncidenteRandomSampleGenerator();

        notificacion.setIncidente(incidenteBack);
        assertThat(notificacion.getIncidente()).isEqualTo(incidenteBack);

        notificacion.incidente(null);
        assertThat(notificacion.getIncidente()).isNull();
    }
}
