package ar.edu.um.isa.oncall.domain;

import static ar.edu.um.isa.oncall.domain.AccionCorrectivaTestSamples.*;
import static ar.edu.um.isa.oncall.domain.IncidenteTestSamples.*;
import static ar.edu.um.isa.oncall.domain.PostmortemTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.um.isa.oncall.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PostmortemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Postmortem.class);
        Postmortem postmortem1 = getPostmortemSample1();
        Postmortem postmortem2 = new Postmortem();
        assertThat(postmortem1).isNotEqualTo(postmortem2);

        postmortem2.setId(postmortem1.getId());
        assertThat(postmortem1).isEqualTo(postmortem2);

        postmortem2 = getPostmortemSample2();
        assertThat(postmortem1).isNotEqualTo(postmortem2);
    }

    @Test
    void incidenteTest() {
        Postmortem postmortem = getPostmortemRandomSampleGenerator();
        Incidente incidenteBack = getIncidenteRandomSampleGenerator();

        postmortem.setIncidente(incidenteBack);
        assertThat(postmortem.getIncidente()).isEqualTo(incidenteBack);

        postmortem.incidente(null);
        assertThat(postmortem.getIncidente()).isNull();
    }

    @Test
    void accionTest() {
        Postmortem postmortem = getPostmortemRandomSampleGenerator();
        AccionCorrectiva accionCorrectivaBack = getAccionCorrectivaRandomSampleGenerator();

        postmortem.addAccion(accionCorrectivaBack);
        assertThat(postmortem.getAccions()).containsOnly(accionCorrectivaBack);
        assertThat(accionCorrectivaBack.getPostmortem()).isEqualTo(postmortem);

        postmortem.removeAccion(accionCorrectivaBack);
        assertThat(postmortem.getAccions()).doesNotContain(accionCorrectivaBack);
        assertThat(accionCorrectivaBack.getPostmortem()).isNull();

        postmortem.accions(new HashSet<>(Set.of(accionCorrectivaBack)));
        assertThat(postmortem.getAccions()).containsOnly(accionCorrectivaBack);
        assertThat(accionCorrectivaBack.getPostmortem()).isEqualTo(postmortem);

        postmortem.setAccions(new HashSet<>());
        assertThat(postmortem.getAccions()).doesNotContain(accionCorrectivaBack);
        assertThat(accionCorrectivaBack.getPostmortem()).isNull();
    }
}
