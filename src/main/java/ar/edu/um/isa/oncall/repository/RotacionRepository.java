package ar.edu.um.isa.oncall.repository;

import ar.edu.um.isa.oncall.domain.Rotacion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Rotacion entity.
 */
@Repository
public interface RotacionRepository extends JpaRepository<Rotacion, Long> {
    default Optional<Rotacion> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Rotacion> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Rotacion> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select rotacion from Rotacion rotacion left join fetch rotacion.equipo",
        countQuery = "select count(rotacion) from Rotacion rotacion"
    )
    Page<Rotacion> findAllWithToOneRelationships(Pageable pageable);

    @Query("select rotacion from Rotacion rotacion left join fetch rotacion.equipo")
    List<Rotacion> findAllWithToOneRelationships();

    @Query("select rotacion from Rotacion rotacion left join fetch rotacion.equipo where rotacion.id =:id")
    Optional<Rotacion> findOneWithToOneRelationships(@Param("id") Long id);
}
