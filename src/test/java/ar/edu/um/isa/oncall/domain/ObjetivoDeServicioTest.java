package ar.edu.um.isa.oncall.domain;

import static ar.edu.um.isa.oncall.domain.ObjetivoDeServicioTestSamples.*;
import static ar.edu.um.isa.oncall.domain.ServicioTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.um.isa.oncall.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ObjetivoDeServicioTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ObjetivoDeServicio.class);
        ObjetivoDeServicio objetivoDeServicio1 = getObjetivoDeServicioSample1();
        ObjetivoDeServicio objetivoDeServicio2 = new ObjetivoDeServicio();
        assertThat(objetivoDeServicio1).isNotEqualTo(objetivoDeServicio2);

        objetivoDeServicio2.setId(objetivoDeServicio1.getId());
        assertThat(objetivoDeServicio1).isEqualTo(objetivoDeServicio2);

        objetivoDeServicio2 = getObjetivoDeServicioSample2();
        assertThat(objetivoDeServicio1).isNotEqualTo(objetivoDeServicio2);
    }

    @Test
    void servicioTest() {
        ObjetivoDeServicio objetivoDeServicio = getObjetivoDeServicioRandomSampleGenerator();
        Servicio servicioBack = getServicioRandomSampleGenerator();

        objetivoDeServicio.setServicio(servicioBack);
        assertThat(objetivoDeServicio.getServicio()).isEqualTo(servicioBack);

        objetivoDeServicio.servicio(null);
        assertThat(objetivoDeServicio.getServicio()).isNull();
    }
}
