package ar.edu.um.isa.oncall.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ServicioCriteriaTest {

    @Test
    void newServicioCriteriaHasAllFiltersNullTest() {
        var servicioCriteria = new ServicioCriteria();
        assertThat(servicioCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void servicioCriteriaFluentMethodsCreatesFiltersTest() {
        var servicioCriteria = new ServicioCriteria();

        setAllFilters(servicioCriteria);

        assertThat(servicioCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void servicioCriteriaCopyCreatesNullFilterTest() {
        var servicioCriteria = new ServicioCriteria();
        var copy = servicioCriteria.copy();

        assertThat(servicioCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(servicioCriteria)
        );
    }

    @Test
    void servicioCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var servicioCriteria = new ServicioCriteria();
        setAllFilters(servicioCriteria);

        var copy = servicioCriteria.copy();

        assertThat(servicioCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(servicioCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var servicioCriteria = new ServicioCriteria();

        assertThat(servicioCriteria).hasToString("ServicioCriteria{}");
    }

    private static void setAllFilters(ServicioCriteria servicioCriteria) {
        servicioCriteria.id();
        servicioCriteria.nombre();
        servicioCriteria.descripcion();
        servicioCriteria.criticidad();
        servicioCriteria.entorno();
        servicioCriteria.repositorioUrl();
        servicioCriteria.activo();
        servicioCriteria.equipoId();
        servicioCriteria.objetivoId();
        servicioCriteria.alertaId();
        servicioCriteria.politicaId();
        servicioCriteria.incidenteId();
        servicioCriteria.distinct();
    }

    private static Condition<ServicioCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getNombre()) &&
                condition.apply(criteria.getDescripcion()) &&
                condition.apply(criteria.getCriticidad()) &&
                condition.apply(criteria.getEntorno()) &&
                condition.apply(criteria.getRepositorioUrl()) &&
                condition.apply(criteria.getActivo()) &&
                condition.apply(criteria.getEquipoId()) &&
                condition.apply(criteria.getObjetivoId()) &&
                condition.apply(criteria.getAlertaId()) &&
                condition.apply(criteria.getPoliticaId()) &&
                condition.apply(criteria.getIncidenteId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ServicioCriteria> copyFiltersAre(ServicioCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getNombre(), copy.getNombre()) &&
                condition.apply(criteria.getDescripcion(), copy.getDescripcion()) &&
                condition.apply(criteria.getCriticidad(), copy.getCriticidad()) &&
                condition.apply(criteria.getEntorno(), copy.getEntorno()) &&
                condition.apply(criteria.getRepositorioUrl(), copy.getRepositorioUrl()) &&
                condition.apply(criteria.getActivo(), copy.getActivo()) &&
                condition.apply(criteria.getEquipoId(), copy.getEquipoId()) &&
                condition.apply(criteria.getObjetivoId(), copy.getObjetivoId()) &&
                condition.apply(criteria.getAlertaId(), copy.getAlertaId()) &&
                condition.apply(criteria.getPoliticaId(), copy.getPoliticaId()) &&
                condition.apply(criteria.getIncidenteId(), copy.getIncidenteId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
