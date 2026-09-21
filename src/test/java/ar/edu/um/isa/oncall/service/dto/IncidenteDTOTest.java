package ar.edu.um.isa.oncall.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.um.isa.oncall.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class IncidenteDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(IncidenteDTO.class);
        IncidenteDTO incidenteDTO1 = new IncidenteDTO();
        incidenteDTO1.setId(1L);
        IncidenteDTO incidenteDTO2 = new IncidenteDTO();
        assertThat(incidenteDTO1).isNotEqualTo(incidenteDTO2);
        incidenteDTO2.setId(incidenteDTO1.getId());
        assertThat(incidenteDTO1).isEqualTo(incidenteDTO2);
        incidenteDTO2.setId(2L);
        assertThat(incidenteDTO1).isNotEqualTo(incidenteDTO2);
        incidenteDTO1.setId(null);
        assertThat(incidenteDTO1).isNotEqualTo(incidenteDTO2);
    }
}
