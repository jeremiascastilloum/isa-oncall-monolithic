package ar.edu.um.isa.oncall.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.um.isa.oncall.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PasoEscalamientoDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PasoEscalamientoDTO.class);
        PasoEscalamientoDTO pasoEscalamientoDTO1 = new PasoEscalamientoDTO();
        pasoEscalamientoDTO1.setId(1L);
        PasoEscalamientoDTO pasoEscalamientoDTO2 = new PasoEscalamientoDTO();
        assertThat(pasoEscalamientoDTO1).isNotEqualTo(pasoEscalamientoDTO2);
        pasoEscalamientoDTO2.setId(pasoEscalamientoDTO1.getId());
        assertThat(pasoEscalamientoDTO1).isEqualTo(pasoEscalamientoDTO2);
        pasoEscalamientoDTO2.setId(2L);
        assertThat(pasoEscalamientoDTO1).isNotEqualTo(pasoEscalamientoDTO2);
        pasoEscalamientoDTO1.setId(null);
        assertThat(pasoEscalamientoDTO1).isNotEqualTo(pasoEscalamientoDTO2);
    }
}
