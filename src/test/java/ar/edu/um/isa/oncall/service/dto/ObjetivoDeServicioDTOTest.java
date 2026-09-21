package ar.edu.um.isa.oncall.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.um.isa.oncall.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ObjetivoDeServicioDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ObjetivoDeServicioDTO.class);
        ObjetivoDeServicioDTO objetivoDeServicioDTO1 = new ObjetivoDeServicioDTO();
        objetivoDeServicioDTO1.setId(1L);
        ObjetivoDeServicioDTO objetivoDeServicioDTO2 = new ObjetivoDeServicioDTO();
        assertThat(objetivoDeServicioDTO1).isNotEqualTo(objetivoDeServicioDTO2);
        objetivoDeServicioDTO2.setId(objetivoDeServicioDTO1.getId());
        assertThat(objetivoDeServicioDTO1).isEqualTo(objetivoDeServicioDTO2);
        objetivoDeServicioDTO2.setId(2L);
        assertThat(objetivoDeServicioDTO1).isNotEqualTo(objetivoDeServicioDTO2);
        objetivoDeServicioDTO1.setId(null);
        assertThat(objetivoDeServicioDTO1).isNotEqualTo(objetivoDeServicioDTO2);
    }
}
