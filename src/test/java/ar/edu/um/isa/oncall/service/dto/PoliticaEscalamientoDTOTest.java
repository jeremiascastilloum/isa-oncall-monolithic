package ar.edu.um.isa.oncall.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.um.isa.oncall.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PoliticaEscalamientoDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PoliticaEscalamientoDTO.class);
        PoliticaEscalamientoDTO politicaEscalamientoDTO1 = new PoliticaEscalamientoDTO();
        politicaEscalamientoDTO1.setId(1L);
        PoliticaEscalamientoDTO politicaEscalamientoDTO2 = new PoliticaEscalamientoDTO();
        assertThat(politicaEscalamientoDTO1).isNotEqualTo(politicaEscalamientoDTO2);
        politicaEscalamientoDTO2.setId(politicaEscalamientoDTO1.getId());
        assertThat(politicaEscalamientoDTO1).isEqualTo(politicaEscalamientoDTO2);
        politicaEscalamientoDTO2.setId(2L);
        assertThat(politicaEscalamientoDTO1).isNotEqualTo(politicaEscalamientoDTO2);
        politicaEscalamientoDTO1.setId(null);
        assertThat(politicaEscalamientoDTO1).isNotEqualTo(politicaEscalamientoDTO2);
    }
}
