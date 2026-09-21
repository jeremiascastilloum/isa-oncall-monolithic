package ar.edu.um.isa.oncall.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.um.isa.oncall.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RotacionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RotacionDTO.class);
        RotacionDTO rotacionDTO1 = new RotacionDTO();
        rotacionDTO1.setId(1L);
        RotacionDTO rotacionDTO2 = new RotacionDTO();
        assertThat(rotacionDTO1).isNotEqualTo(rotacionDTO2);
        rotacionDTO2.setId(rotacionDTO1.getId());
        assertThat(rotacionDTO1).isEqualTo(rotacionDTO2);
        rotacionDTO2.setId(2L);
        assertThat(rotacionDTO1).isNotEqualTo(rotacionDTO2);
        rotacionDTO1.setId(null);
        assertThat(rotacionDTO1).isNotEqualTo(rotacionDTO2);
    }
}
