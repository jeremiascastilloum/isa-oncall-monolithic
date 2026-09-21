package ar.edu.um.isa.oncall.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.um.isa.oncall.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AccionCorrectivaDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AccionCorrectivaDTO.class);
        AccionCorrectivaDTO accionCorrectivaDTO1 = new AccionCorrectivaDTO();
        accionCorrectivaDTO1.setId(1L);
        AccionCorrectivaDTO accionCorrectivaDTO2 = new AccionCorrectivaDTO();
        assertThat(accionCorrectivaDTO1).isNotEqualTo(accionCorrectivaDTO2);
        accionCorrectivaDTO2.setId(accionCorrectivaDTO1.getId());
        assertThat(accionCorrectivaDTO1).isEqualTo(accionCorrectivaDTO2);
        accionCorrectivaDTO2.setId(2L);
        assertThat(accionCorrectivaDTO1).isNotEqualTo(accionCorrectivaDTO2);
        accionCorrectivaDTO1.setId(null);
        assertThat(accionCorrectivaDTO1).isNotEqualTo(accionCorrectivaDTO2);
    }
}
