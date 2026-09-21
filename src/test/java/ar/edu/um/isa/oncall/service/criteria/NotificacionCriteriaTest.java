package ar.edu.um.isa.oncall.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class NotificacionCriteriaTest {

    @Test
    void newNotificacionCriteriaHasAllFiltersNullTest() {
        var notificacionCriteria = new NotificacionCriteria();
        assertThat(notificacionCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void notificacionCriteriaFluentMethodsCreatesFiltersTest() {
        var notificacionCriteria = new NotificacionCriteria();

        setAllFilters(notificacionCriteria);

        assertThat(notificacionCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void notificacionCriteriaCopyCreatesNullFilterTest() {
        var notificacionCriteria = new NotificacionCriteria();
        var copy = notificacionCriteria.copy();

        assertThat(notificacionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(notificacionCriteria)
        );
    }

    @Test
    void notificacionCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var notificacionCriteria = new NotificacionCriteria();
        setAllFilters(notificacionCriteria);

        var copy = notificacionCriteria.copy();

        assertThat(notificacionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(notificacionCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var notificacionCriteria = new NotificacionCriteria();

        assertThat(notificacionCriteria).hasToString("NotificacionCriteria{}");
    }

    private static void setAllFilters(NotificacionCriteria notificacionCriteria) {
        notificacionCriteria.id();
        notificacionCriteria.canal();
        notificacionCriteria.destino();
        notificacionCriteria.estado();
        notificacionCriteria.enviadaEn();
        notificacionCriteria.intentos();
        notificacionCriteria.errorMensaje();
        notificacionCriteria.incidenteId();
        notificacionCriteria.destinatarioId();
        notificacionCriteria.distinct();
    }

    private static Condition<NotificacionCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCanal()) &&
                condition.apply(criteria.getDestino()) &&
                condition.apply(criteria.getEstado()) &&
                condition.apply(criteria.getEnviadaEn()) &&
                condition.apply(criteria.getIntentos()) &&
                condition.apply(criteria.getErrorMensaje()) &&
                condition.apply(criteria.getIncidenteId()) &&
                condition.apply(criteria.getDestinatarioId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<NotificacionCriteria> copyFiltersAre(
        NotificacionCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCanal(), copy.getCanal()) &&
                condition.apply(criteria.getDestino(), copy.getDestino()) &&
                condition.apply(criteria.getEstado(), copy.getEstado()) &&
                condition.apply(criteria.getEnviadaEn(), copy.getEnviadaEn()) &&
                condition.apply(criteria.getIntentos(), copy.getIntentos()) &&
                condition.apply(criteria.getErrorMensaje(), copy.getErrorMensaje()) &&
                condition.apply(criteria.getIncidenteId(), copy.getIncidenteId()) &&
                condition.apply(criteria.getDestinatarioId(), copy.getDestinatarioId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
