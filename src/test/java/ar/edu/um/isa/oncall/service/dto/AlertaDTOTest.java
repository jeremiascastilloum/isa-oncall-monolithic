package ar.edu.um.isa.oncall.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.um.isa.oncall.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AlertaDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AlertaDTO.class);
        AlertaDTO alertaDTO1 = new AlertaDTO();
        alertaDTO1.setId(1L);
        AlertaDTO alertaDTO2 = new AlertaDTO();
        assertThat(alertaDTO1).isNotEqualTo(alertaDTO2);
        alertaDTO2.setId(alertaDTO1.getId());
        assertThat(alertaDTO1).isEqualTo(alertaDTO2);
        alertaDTO2.setId(2L);
        assertThat(alertaDTO1).isNotEqualTo(alertaDTO2);
        alertaDTO1.setId(null);
        assertThat(alertaDTO1).isNotEqualTo(alertaDTO2);
    }
}
