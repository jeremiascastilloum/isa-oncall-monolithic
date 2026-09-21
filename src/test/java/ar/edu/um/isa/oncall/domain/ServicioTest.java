package ar.edu.um.isa.oncall.domain;

import static ar.edu.um.isa.oncall.domain.AlertaTestSamples.*;
import static ar.edu.um.isa.oncall.domain.EquipoTestSamples.*;
import static ar.edu.um.isa.oncall.domain.IncidenteTestSamples.*;
import static ar.edu.um.isa.oncall.domain.ObjetivoDeServicioTestSamples.*;
import static ar.edu.um.isa.oncall.domain.PoliticaEscalamientoTestSamples.*;
import static ar.edu.um.isa.oncall.domain.ServicioTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.um.isa.oncall.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ServicioTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Servicio.class);
        Servicio servicio1 = getServicioSample1();
        Servicio servicio2 = new Servicio();
        assertThat(servicio1).isNotEqualTo(servicio2);

        servicio2.setId(servicio1.getId());
        assertThat(servicio1).isEqualTo(servicio2);

        servicio2 = getServicioSample2();
        assertThat(servicio1).isNotEqualTo(servicio2);
    }

    @Test
    void equipoTest() {
        Servicio servicio = getServicioRandomSampleGenerator();
        Equipo equipoBack = getEquipoRandomSampleGenerator();

        servicio.setEquipo(equipoBack);
        assertThat(servicio.getEquipo()).isEqualTo(equipoBack);

        servicio.equipo(null);
        assertThat(servicio.getEquipo()).isNull();
    }

    @Test
    void objetivoTest() {
        Servicio servicio = getServicioRandomSampleGenerator();
        ObjetivoDeServicio objetivoDeServicioBack = getObjetivoDeServicioRandomSampleGenerator();

        servicio.addObjetivo(objetivoDeServicioBack);
        assertThat(servicio.getObjetivos()).containsOnly(objetivoDeServicioBack);
        assertThat(objetivoDeServicioBack.getServicio()).isEqualTo(servicio);

        servicio.removeObjetivo(objetivoDeServicioBack);
        assertThat(servicio.getObjetivos()).doesNotContain(objetivoDeServicioBack);
        assertThat(objetivoDeServicioBack.getServicio()).isNull();

        servicio.objetivos(new HashSet<>(Set.of(objetivoDeServicioBack)));
        assertThat(servicio.getObjetivos()).containsOnly(objetivoDeServicioBack);
        assertThat(objetivoDeServicioBack.getServicio()).isEqualTo(servicio);

        servicio.setObjetivos(new HashSet<>());
        assertThat(servicio.getObjetivos()).doesNotContain(objetivoDeServicioBack);
        assertThat(objetivoDeServicioBack.getServicio()).isNull();
    }

    @Test
    void alertaTest() {
        Servicio servicio = getServicioRandomSampleGenerator();
        Alerta alertaBack = getAlertaRandomSampleGenerator();

        servicio.addAlerta(alertaBack);
        assertThat(servicio.getAlertas()).containsOnly(alertaBack);
        assertThat(alertaBack.getServicio()).isEqualTo(servicio);

        servicio.removeAlerta(alertaBack);
        assertThat(servicio.getAlertas()).doesNotContain(alertaBack);
        assertThat(alertaBack.getServicio()).isNull();

        servicio.alertas(new HashSet<>(Set.of(alertaBack)));
        assertThat(servicio.getAlertas()).containsOnly(alertaBack);
        assertThat(alertaBack.getServicio()).isEqualTo(servicio);

        servicio.setAlertas(new HashSet<>());
        assertThat(servicio.getAlertas()).doesNotContain(alertaBack);
        assertThat(alertaBack.getServicio()).isNull();
    }

    @Test
    void politicaTest() {
        Servicio servicio = getServicioRandomSampleGenerator();
        PoliticaEscalamiento politicaEscalamientoBack = getPoliticaEscalamientoRandomSampleGenerator();

        servicio.addPolitica(politicaEscalamientoBack);
        assertThat(servicio.getPoliticas()).containsOnly(politicaEscalamientoBack);
        assertThat(politicaEscalamientoBack.getServicio()).isEqualTo(servicio);

        servicio.removePolitica(politicaEscalamientoBack);
        assertThat(servicio.getPoliticas()).doesNotContain(politicaEscalamientoBack);
        assertThat(politicaEscalamientoBack.getServicio()).isNull();

        servicio.politicas(new HashSet<>(Set.of(politicaEscalamientoBack)));
        assertThat(servicio.getPoliticas()).containsOnly(politicaEscalamientoBack);
        assertThat(politicaEscalamientoBack.getServicio()).isEqualTo(servicio);

        servicio.setPoliticas(new HashSet<>());
        assertThat(servicio.getPoliticas()).doesNotContain(politicaEscalamientoBack);
        assertThat(politicaEscalamientoBack.getServicio()).isNull();
    }

    @Test
    void incidenteTest() {
        Servicio servicio = getServicioRandomSampleGenerator();
        Incidente incidenteBack = getIncidenteRandomSampleGenerator();

        servicio.addIncidente(incidenteBack);
        assertThat(servicio.getIncidentes()).containsOnly(incidenteBack);
        assertThat(incidenteBack.getServicios()).containsOnly(servicio);

        servicio.removeIncidente(incidenteBack);
        assertThat(servicio.getIncidentes()).doesNotContain(incidenteBack);
        assertThat(incidenteBack.getServicios()).doesNotContain(servicio);

        servicio.incidentes(new HashSet<>(Set.of(incidenteBack)));
        assertThat(servicio.getIncidentes()).containsOnly(incidenteBack);
        assertThat(incidenteBack.getServicios()).containsOnly(servicio);

        servicio.setIncidentes(new HashSet<>());
        assertThat(servicio.getIncidentes()).doesNotContain(incidenteBack);
        assertThat(incidenteBack.getServicios()).doesNotContain(servicio);
    }
}
