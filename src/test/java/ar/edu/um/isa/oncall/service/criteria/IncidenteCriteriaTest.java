package ar.edu.um.isa.oncall.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class IncidenteCriteriaTest {

    @Test
    void newIncidenteCriteriaHasAllFiltersNullTest() {
        var incidenteCriteria = new IncidenteCriteria();
        assertThat(incidenteCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void incidenteCriteriaFluentMethodsCreatesFiltersTest() {
        var incidenteCriteria = new IncidenteCriteria();

        setAllFilters(incidenteCriteria);

        assertThat(incidenteCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void incidenteCriteriaCopyCreatesNullFilterTest() {
        var incidenteCriteria = new IncidenteCriteria();
        var copy = incidenteCriteria.copy();

        assertThat(incidenteCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(incidenteCriteria)
        );
    }

    @Test
    void incidenteCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var incidenteCriteria = new IncidenteCriteria();
        setAllFilters(incidenteCriteria);

        var copy = incidenteCriteria.copy();

        assertThat(incidenteCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(incidenteCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var incidenteCriteria = new IncidenteCriteria();

        assertThat(incidenteCriteria).hasToString("IncidenteCriteria{}");
    }

    private static void setAllFilters(IncidenteCriteria incidenteCriteria) {
        incidenteCriteria.id();
        incidenteCriteria.titulo();
        incidenteCriteria.descripcion();
        incidenteCriteria.severidad();
        incidenteCriteria.estado();
        incidenteCriteria.detectadoEn();
        incidenteCriteria.reconocidoEn();
        incidenteCriteria.mitigadoEn();
        incidenteCriteria.resueltoEn();
        incidenteCriteria.usuariosAfectados();
        incidenteCriteria.cumplioObjetivo();
        incidenteCriteria.comandanteId();
        incidenteCriteria.servicioId();
        incidenteCriteria.postmortemId();
        incidenteCriteria.alertaId();
        incidenteCriteria.eventoId();
        incidenteCriteria.notificacionId();
        incidenteCriteria.distinct();
    }

    private static Condition<IncidenteCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getTitulo()) &&
                condition.apply(criteria.getDescripcion()) &&
                condition.apply(criteria.getSeveridad()) &&
                condition.apply(criteria.getEstado()) &&
                condition.apply(criteria.getDetectadoEn()) &&
                condition.apply(criteria.getReconocidoEn()) &&
                condition.apply(criteria.getMitigadoEn()) &&
                condition.apply(criteria.getResueltoEn()) &&
                condition.apply(criteria.getUsuariosAfectados()) &&
                condition.apply(criteria.getCumplioObjetivo()) &&
                condition.apply(criteria.getComandanteId()) &&
                condition.apply(criteria.getServicioId()) &&
                condition.apply(criteria.getPostmortemId()) &&
                condition.apply(criteria.getAlertaId()) &&
                condition.apply(criteria.getEventoId()) &&
                condition.apply(criteria.getNotificacionId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<IncidenteCriteria> copyFiltersAre(IncidenteCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getTitulo(), copy.getTitulo()) &&
                condition.apply(criteria.getDescripcion(), copy.getDescripcion()) &&
                condition.apply(criteria.getSeveridad(), copy.getSeveridad()) &&
                condition.apply(criteria.getEstado(), copy.getEstado()) &&
                condition.apply(criteria.getDetectadoEn(), copy.getDetectadoEn()) &&
                condition.apply(criteria.getReconocidoEn(), copy.getReconocidoEn()) &&
                condition.apply(criteria.getMitigadoEn(), copy.getMitigadoEn()) &&
                condition.apply(criteria.getResueltoEn(), copy.getResueltoEn()) &&
                condition.apply(criteria.getUsuariosAfectados(), copy.getUsuariosAfectados()) &&
                condition.apply(criteria.getCumplioObjetivo(), copy.getCumplioObjetivo()) &&
                condition.apply(criteria.getComandanteId(), copy.getComandanteId()) &&
                condition.apply(criteria.getServicioId(), copy.getServicioId()) &&
                condition.apply(criteria.getPostmortemId(), copy.getPostmortemId()) &&
                condition.apply(criteria.getAlertaId(), copy.getAlertaId()) &&
                condition.apply(criteria.getEventoId(), copy.getEventoId()) &&
                condition.apply(criteria.getNotificacionId(), copy.getNotificacionId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
