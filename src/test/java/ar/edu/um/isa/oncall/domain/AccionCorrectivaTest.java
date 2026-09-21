package ar.edu.um.isa.oncall.domain;

import static ar.edu.um.isa.oncall.domain.AccionCorrectivaTestSamples.*;
import static ar.edu.um.isa.oncall.domain.PostmortemTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.um.isa.oncall.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AccionCorrectivaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AccionCorrectiva.class);
        AccionCorrectiva accionCorrectiva1 = getAccionCorrectivaSample1();
        AccionCorrectiva accionCorrectiva2 = new AccionCorrectiva();
        assertThat(accionCorrectiva1).isNotEqualTo(accionCorrectiva2);

        accionCorrectiva2.setId(accionCorrectiva1.getId());
        assertThat(accionCorrectiva1).isEqualTo(accionCorrectiva2);

        accionCorrectiva2 = getAccionCorrectivaSample2();
        assertThat(accionCorrectiva1).isNotEqualTo(accionCorrectiva2);
    }

    @Test
    void postmortemTest() {
        AccionCorrectiva accionCorrectiva = getAccionCorrectivaRandomSampleGenerator();
        Postmortem postmortemBack = getPostmortemRandomSampleGenerator();

        accionCorrectiva.setPostmortem(postmortemBack);
        assertThat(accionCorrectiva.getPostmortem()).isEqualTo(postmortemBack);

        accionCorrectiva.postmortem(null);
        assertThat(accionCorrectiva.getPostmortem()).isNull();
    }
}
