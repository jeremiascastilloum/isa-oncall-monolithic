package ar.edu.um.isa.oncall.domain;

import static ar.edu.um.isa.oncall.domain.AlertaTestSamples.*;
import static ar.edu.um.isa.oncall.domain.EventoDeIncidenteTestSamples.*;
import static ar.edu.um.isa.oncall.domain.IncidenteTestSamples.*;
import static ar.edu.um.isa.oncall.domain.NotificacionTestSamples.*;
import static ar.edu.um.isa.oncall.domain.PostmortemTestSamples.*;
import static ar.edu.um.isa.oncall.domain.ServicioTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.um.isa.oncall.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class IncidenteTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Incidente.class);
        Incidente incidente1 = getIncidenteSample1();
        Incidente incidente2 = new Incidente();
        assertThat(incidente1).isNotEqualTo(incidente2);

        incidente2.setId(incidente1.getId());
        assertThat(incidente1).isEqualTo(incidente2);

        incidente2 = getIncidenteSample2();
        assertThat(incidente1).isNotEqualTo(incidente2);
    }

    @Test
    void servicioTest() {
        Incidente incidente = getIncidenteRandomSampleGenerator();
        Servicio servicioBack = getServicioRandomSampleGenerator();

        incidente.addServicio(servicioBack);
        assertThat(incidente.getServicios()).containsOnly(servicioBack);

        incidente.removeServicio(servicioBack);
        assertThat(incidente.getServicios()).doesNotContain(servicioBack);

        incidente.servicios(new HashSet<>(Set.of(servicioBack)));
        assertThat(incidente.getServicios()).containsOnly(servicioBack);

        incidente.setServicios(new HashSet<>());
        assertThat(incidente.getServicios()).doesNotContain(servicioBack);
    }

    @Test
    void postmortemTest() {
        Incidente incidente = getIncidenteRandomSampleGenerator();
        Postmortem postmortemBack = getPostmortemRandomSampleGenerator();

        incidente.setPostmortem(postmortemBack);
        assertThat(incidente.getPostmortem()).isEqualTo(postmortemBack);
        assertThat(postmortemBack.getIncidente()).isEqualTo(incidente);

        incidente.postmortem(null);
        assertThat(incidente.getPostmortem()).isNull();
        assertThat(postmortemBack.getIncidente()).isNull();
    }

    @Test
    void alertaTest() {
        Incidente incidente = getIncidenteRandomSampleGenerator();
        Alerta alertaBack = getAlertaRandomSampleGenerator();

        incidente.addAlerta(alertaBack);
        assertThat(incidente.getAlertas()).containsOnly(alertaBack);
        assertThat(alertaBack.getIncidente()).isEqualTo(incidente);

        incidente.removeAlerta(alertaBack);
        assertThat(incidente.getAlertas()).doesNotContain(alertaBack);
        assertThat(alertaBack.getIncidente()).isNull();

        incidente.alertas(new HashSet<>(Set.of(alertaBack)));
        assertThat(incidente.getAlertas()).containsOnly(alertaBack);
        assertThat(alertaBack.getIncidente()).isEqualTo(incidente);

        incidente.setAlertas(new HashSet<>());
        assertThat(incidente.getAlertas()).doesNotContain(alertaBack);
        assertThat(alertaBack.getIncidente()).isNull();
    }

    @Test
    void eventoTest() {
        Incidente incidente = getIncidenteRandomSampleGenerator();
        EventoDeIncidente eventoDeIncidenteBack = getEventoDeIncidenteRandomSampleGenerator();

        incidente.addEvento(eventoDeIncidenteBack);
        assertThat(incidente.getEventos()).containsOnly(eventoDeIncidenteBack);
        assertThat(eventoDeIncidenteBack.getIncidente()).isEqualTo(incidente);

        incidente.removeEvento(eventoDeIncidenteBack);
        assertThat(incidente.getEventos()).doesNotContain(eventoDeIncidenteBack);
        assertThat(eventoDeIncidenteBack.getIncidente()).isNull();

        incidente.eventos(new HashSet<>(Set.of(eventoDeIncidenteBack)));
        assertThat(incidente.getEventos()).containsOnly(eventoDeIncidenteBack);
        assertThat(eventoDeIncidenteBack.getIncidente()).isEqualTo(incidente);

        incidente.setEventos(new HashSet<>());
        assertThat(incidente.getEventos()).doesNotContain(eventoDeIncidenteBack);
        assertThat(eventoDeIncidenteBack.getIncidente()).isNull();
    }

    @Test
    void notificacionTest() {
        Incidente incidente = getIncidenteRandomSampleGenerator();
        Notificacion notificacionBack = getNotificacionRandomSampleGenerator();

        incidente.addNotificacion(notificacionBack);
        assertThat(incidente.getNotificacions()).containsOnly(notificacionBack);
        assertThat(notificacionBack.getIncidente()).isEqualTo(incidente);

        incidente.removeNotificacion(notificacionBack);
        assertThat(incidente.getNotificacions()).doesNotContain(notificacionBack);
        assertThat(notificacionBack.getIncidente()).isNull();

        incidente.notificacions(new HashSet<>(Set.of(notificacionBack)));
        assertThat(incidente.getNotificacions()).containsOnly(notificacionBack);
        assertThat(notificacionBack.getIncidente()).isEqualTo(incidente);

        incidente.setNotificacions(new HashSet<>());
        assertThat(incidente.getNotificacions()).doesNotContain(notificacionBack);
        assertThat(notificacionBack.getIncidente()).isNull();
    }
}
