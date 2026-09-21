package ar.edu.um.isa.oncall.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.um.isa.oncall.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EventoDeIncidenteDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EventoDeIncidenteDTO.class);
        EventoDeIncidenteDTO eventoDeIncidenteDTO1 = new EventoDeIncidenteDTO();
        eventoDeIncidenteDTO1.setId(1L);
        EventoDeIncidenteDTO eventoDeIncidenteDTO2 = new EventoDeIncidenteDTO();
        assertThat(eventoDeIncidenteDTO1).isNotEqualTo(eventoDeIncidenteDTO2);
        eventoDeIncidenteDTO2.setId(eventoDeIncidenteDTO1.getId());
        assertThat(eventoDeIncidenteDTO1).isEqualTo(eventoDeIncidenteDTO2);
        eventoDeIncidenteDTO2.setId(2L);
        assertThat(eventoDeIncidenteDTO1).isNotEqualTo(eventoDeIncidenteDTO2);
        eventoDeIncidenteDTO1.setId(null);
        assertThat(eventoDeIncidenteDTO1).isNotEqualTo(eventoDeIncidenteDTO2);
    }
}
