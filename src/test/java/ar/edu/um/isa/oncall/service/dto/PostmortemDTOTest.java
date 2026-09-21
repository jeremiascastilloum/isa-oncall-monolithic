package ar.edu.um.isa.oncall.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.um.isa.oncall.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PostmortemDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PostmortemDTO.class);
        PostmortemDTO postmortemDTO1 = new PostmortemDTO();
        postmortemDTO1.setId(1L);
        PostmortemDTO postmortemDTO2 = new PostmortemDTO();
        assertThat(postmortemDTO1).isNotEqualTo(postmortemDTO2);
        postmortemDTO2.setId(postmortemDTO1.getId());
        assertThat(postmortemDTO1).isEqualTo(postmortemDTO2);
        postmortemDTO2.setId(2L);
        assertThat(postmortemDTO1).isNotEqualTo(postmortemDTO2);
        postmortemDTO1.setId(null);
        assertThat(postmortemDTO1).isNotEqualTo(postmortemDTO2);
    }
}
