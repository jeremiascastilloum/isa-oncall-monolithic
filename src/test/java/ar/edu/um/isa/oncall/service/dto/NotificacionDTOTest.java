package ar.edu.um.isa.oncall.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.um.isa.oncall.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NotificacionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(NotificacionDTO.class);
        NotificacionDTO notificacionDTO1 = new NotificacionDTO();
        notificacionDTO1.setId(1L);
        NotificacionDTO notificacionDTO2 = new NotificacionDTO();
        assertThat(notificacionDTO1).isNotEqualTo(notificacionDTO2);
        notificacionDTO2.setId(notificacionDTO1.getId());
        assertThat(notificacionDTO1).isEqualTo(notificacionDTO2);
        notificacionDTO2.setId(2L);
        assertThat(notificacionDTO1).isNotEqualTo(notificacionDTO2);
        notificacionDTO1.setId(null);
        assertThat(notificacionDTO1).isNotEqualTo(notificacionDTO2);
    }
}
