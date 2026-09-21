package ar.edu.um.isa.oncall.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.um.isa.oncall.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TurnoDeGuardiaDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TurnoDeGuardiaDTO.class);
        TurnoDeGuardiaDTO turnoDeGuardiaDTO1 = new TurnoDeGuardiaDTO();
        turnoDeGuardiaDTO1.setId(1L);
        TurnoDeGuardiaDTO turnoDeGuardiaDTO2 = new TurnoDeGuardiaDTO();
        assertThat(turnoDeGuardiaDTO1).isNotEqualTo(turnoDeGuardiaDTO2);
        turnoDeGuardiaDTO2.setId(turnoDeGuardiaDTO1.getId());
        assertThat(turnoDeGuardiaDTO1).isEqualTo(turnoDeGuardiaDTO2);
        turnoDeGuardiaDTO2.setId(2L);
        assertThat(turnoDeGuardiaDTO1).isNotEqualTo(turnoDeGuardiaDTO2);
        turnoDeGuardiaDTO1.setId(null);
        assertThat(turnoDeGuardiaDTO1).isNotEqualTo(turnoDeGuardiaDTO2);
    }
}
