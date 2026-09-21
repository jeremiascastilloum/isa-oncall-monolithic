package ar.edu.um.isa.oncall.repository;

import ar.edu.um.isa.oncall.domain.Incidente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class IncidenteRepositoryWithBagRelationshipsImpl implements IncidenteRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String INCIDENTES_PARAMETER = "incidentes";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Incidente> fetchBagRelationships(Optional<Incidente> incidente) {
        return incidente.map(this::fetchServicios);
    }

    @Override
    public Page<Incidente> fetchBagRelationships(Page<Incidente> incidentes) {
        return new PageImpl<>(fetchBagRelationships(incidentes.getContent()), incidentes.getPageable(), incidentes.getTotalElements());
    }

    @Override
    public List<Incidente> fetchBagRelationships(List<Incidente> incidentes) {
        return Optional.of(incidentes).map(this::fetchServicios).orElse(List.of());
    }

    Incidente fetchServicios(Incidente result) {
        return entityManager
            .createQuery(
                "select incidente from Incidente incidente left join fetch incidente.servicios where incidente.id = :id",
                Incidente.class
            )
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<Incidente> fetchServicios(List<Incidente> incidentes) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, incidentes.size()).forEach(index -> order.put(incidentes.get(index).getId(), index));
        List<Incidente> result = entityManager
            .createQuery(
                "select incidente from Incidente incidente left join fetch incidente.servicios where incidente in :incidentes",
                Incidente.class
            )
            .setParameter(INCIDENTES_PARAMETER, incidentes)
            .getResultList();
        result.sort((o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
