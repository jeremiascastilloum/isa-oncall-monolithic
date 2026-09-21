package ar.edu.um.isa.oncall.repository;

import ar.edu.um.isa.oncall.domain.TurnoDeGuardia;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TurnoDeGuardia entity.
 */
@Repository
public interface TurnoDeGuardiaRepository extends JpaRepository<TurnoDeGuardia, Long> {
    @Query("select turnoDeGuardia from TurnoDeGuardia turnoDeGuardia where turnoDeGuardia.responsable.login = ?#{authentication.name}")
    List<TurnoDeGuardia> findByResponsableIsCurrentUser();

    default Optional<TurnoDeGuardia> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<TurnoDeGuardia> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<TurnoDeGuardia> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select turnoDeGuardia from TurnoDeGuardia turnoDeGuardia left join fetch turnoDeGuardia.rotacion left join fetch turnoDeGuardia.responsable",
        countQuery = "select count(turnoDeGuardia) from TurnoDeGuardia turnoDeGuardia"
    )
    Page<TurnoDeGuardia> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select turnoDeGuardia from TurnoDeGuardia turnoDeGuardia left join fetch turnoDeGuardia.rotacion left join fetch turnoDeGuardia.responsable"
    )
    List<TurnoDeGuardia> findAllWithToOneRelationships();

    @Query(
        "select turnoDeGuardia from TurnoDeGuardia turnoDeGuardia left join fetch turnoDeGuardia.rotacion left join fetch turnoDeGuardia.responsable where turnoDeGuardia.id =:id"
    )
    Optional<TurnoDeGuardia> findOneWithToOneRelationships(@Param("id") Long id);
}
