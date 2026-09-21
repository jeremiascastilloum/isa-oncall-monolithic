package ar.edu.um.isa.oncall.repository;

import ar.edu.um.isa.oncall.domain.Alerta;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Alerta entity.
 */
@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long>, JpaSpecificationExecutor<Alerta> {
    default Optional<Alerta> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Alerta> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Alerta> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select alerta from Alerta alerta left join fetch alerta.servicio left join fetch alerta.incidente",
        countQuery = "select count(alerta) from Alerta alerta"
    )
    Page<Alerta> findAllWithToOneRelationships(Pageable pageable);

    @Query("select alerta from Alerta alerta left join fetch alerta.servicio left join fetch alerta.incidente")
    List<Alerta> findAllWithToOneRelationships();

    @Query("select alerta from Alerta alerta left join fetch alerta.servicio left join fetch alerta.incidente where alerta.id =:id")
    Optional<Alerta> findOneWithToOneRelationships(@Param("id") Long id);
}
