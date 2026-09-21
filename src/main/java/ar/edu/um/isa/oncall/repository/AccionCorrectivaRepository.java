package ar.edu.um.isa.oncall.repository;

import ar.edu.um.isa.oncall.domain.AccionCorrectiva;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AccionCorrectiva entity.
 */
@Repository
public interface AccionCorrectivaRepository extends JpaRepository<AccionCorrectiva, Long> {
    @Query(
        "select accionCorrectiva from AccionCorrectiva accionCorrectiva where accionCorrectiva.responsable.login = ?#{authentication.name}"
    )
    List<AccionCorrectiva> findByResponsableIsCurrentUser();

    default Optional<AccionCorrectiva> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<AccionCorrectiva> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<AccionCorrectiva> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select accionCorrectiva from AccionCorrectiva accionCorrectiva left join fetch accionCorrectiva.postmortem left join fetch accionCorrectiva.responsable",
        countQuery = "select count(accionCorrectiva) from AccionCorrectiva accionCorrectiva"
    )
    Page<AccionCorrectiva> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select accionCorrectiva from AccionCorrectiva accionCorrectiva left join fetch accionCorrectiva.postmortem left join fetch accionCorrectiva.responsable"
    )
    List<AccionCorrectiva> findAllWithToOneRelationships();

    @Query(
        "select accionCorrectiva from AccionCorrectiva accionCorrectiva left join fetch accionCorrectiva.postmortem left join fetch accionCorrectiva.responsable where accionCorrectiva.id =:id"
    )
    Optional<AccionCorrectiva> findOneWithToOneRelationships(@Param("id") Long id);
}
