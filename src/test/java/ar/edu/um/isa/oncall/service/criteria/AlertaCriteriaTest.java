package ar.edu.um.isa.oncall.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class AlertaCriteriaTest {

    @Test
    void newAlertaCriteriaHasAllFiltersNullTest() {
        var alertaCriteria = new AlertaCriteria();
        assertThat(alertaCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void alertaCriteriaFluentMethodsCreatesFiltersTest() {
        var alertaCriteria = new AlertaCriteria();

        setAllFilters(alertaCriteria);

        assertThat(alertaCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void alertaCriteriaCopyCreatesNullFilterTest() {
        var alertaCriteria = new AlertaCriteria();
        var copy = alertaCriteria.copy();

        assertThat(alertaCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(alertaCriteria)
        );
    }

    @Test
    void alertaCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var alertaCriteria = new AlertaCriteria();
        setAllFilters(alertaCriteria);

        var copy = alertaCriteria.copy();

        assertThat(alertaCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(alertaCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var alertaCriteria = new AlertaCriteria();

        assertThat(alertaCriteria).hasToString("AlertaCriteria{}");
    }

    private static void setAllFilters(AlertaCriteria alertaCriteria) {
        alertaCriteria.id();
        alertaCriteria.fingerprint();
        alertaCriteria.origen();
        alertaCriteria.resumen();
        alertaCriteria.payload();
        alertaCriteria.recibidaEn();
        alertaCriteria.procesada();
        alertaCriteria.servicioId();
        alertaCriteria.incidenteId();
        alertaCriteria.distinct();
    }

    private static Condition<AlertaCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getFingerprint()) &&
                condition.apply(criteria.getOrigen()) &&
                condition.apply(criteria.getResumen()) &&
                condition.apply(criteria.getPayload()) &&
                condition.apply(criteria.getRecibidaEn()) &&
                condition.apply(criteria.getProcesada()) &&
                condition.apply(criteria.getServicioId()) &&
                condition.apply(criteria.getIncidenteId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<AlertaCriteria> copyFiltersAre(AlertaCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getFingerprint(), copy.getFingerprint()) &&
                condition.apply(criteria.getOrigen(), copy.getOrigen()) &&
                condition.apply(criteria.getResumen(), copy.getResumen()) &&
                condition.apply(criteria.getPayload(), copy.getPayload()) &&
                condition.apply(criteria.getRecibidaEn(), copy.getRecibidaEn()) &&
                condition.apply(criteria.getProcesada(), copy.getProcesada()) &&
                condition.apply(criteria.getServicioId(), copy.getServicioId()) &&
                condition.apply(criteria.getIncidenteId(), copy.getIncidenteId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
