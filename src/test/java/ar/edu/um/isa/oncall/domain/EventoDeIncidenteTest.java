package ar.edu.um.isa.oncall.domain;

import static ar.edu.um.isa.oncall.domain.EventoDeIncidenteTestSamples.*;
import static ar.edu.um.isa.oncall.domain.IncidenteTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.um.isa.oncall.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EventoDeIncidenteTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EventoDeIncidente.class);
        EventoDeIncidente eventoDeIncidente1 = getEventoDeIncidenteSample1();
        EventoDeIncidente eventoDeIncidente2 = new EventoDeIncidente();
        assertThat(eventoDeIncidente1).isNotEqualTo(eventoDeIncidente2);

        eventoDeIncidente2.setId(eventoDeIncidente1.getId());
        assertThat(eventoDeIncidente1).isEqualTo(eventoDeIncidente2);

        eventoDeIncidente2 = getEventoDeIncidenteSample2();
        assertThat(eventoDeIncidente1).isNotEqualTo(eventoDeIncidente2);
    }

    @Test
    void incidenteTest() {
        EventoDeIncidente eventoDeIncidente = getEventoDeIncidenteRandomSampleGenerator();
        Incidente incidenteBack = getIncidenteRandomSampleGenerator();

        eventoDeIncidente.setIncidente(incidenteBack);
        assertThat(eventoDeIncidente.getIncidente()).isEqualTo(incidenteBack);

        eventoDeIncidente.incidente(null);
        assertThat(eventoDeIncidente.getIncidente()).isNull();
    }
}
