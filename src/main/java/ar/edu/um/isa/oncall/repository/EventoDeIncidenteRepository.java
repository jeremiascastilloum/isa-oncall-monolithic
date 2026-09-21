package ar.edu.um.isa.oncall.repository;

import ar.edu.um.isa.oncall.domain.EventoDeIncidente;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the EventoDeIncidente entity.
 */
@Repository
public interface EventoDeIncidenteRepository extends JpaRepository<EventoDeIncidente, Long> {
    default Optional<EventoDeIncidente> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<EventoDeIncidente> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<EventoDeIncidente> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select eventoDeIncidente from EventoDeIncidente eventoDeIncidente left join fetch eventoDeIncidente.incidente",
        countQuery = "select count(eventoDeIncidente) from EventoDeIncidente eventoDeIncidente"
    )
    Page<EventoDeIncidente> findAllWithToOneRelationships(Pageable pageable);

    @Query("select eventoDeIncidente from EventoDeIncidente eventoDeIncidente left join fetch eventoDeIncidente.incidente")
    List<EventoDeIncidente> findAllWithToOneRelationships();

    @Query(
        "select eventoDeIncidente from EventoDeIncidente eventoDeIncidente left join fetch eventoDeIncidente.incidente where eventoDeIncidente.id =:id"
    )
    Optional<EventoDeIncidente> findOneWithToOneRelationships(@Param("id") Long id);
}
