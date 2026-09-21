package ar.edu.um.isa.oncall.repository;

import ar.edu.um.isa.oncall.domain.PoliticaEscalamiento;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PoliticaEscalamiento entity.
 */
@Repository
public interface PoliticaEscalamientoRepository extends JpaRepository<PoliticaEscalamiento, Long> {
    default Optional<PoliticaEscalamiento> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<PoliticaEscalamiento> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<PoliticaEscalamiento> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select politicaEscalamiento from PoliticaEscalamiento politicaEscalamiento left join fetch politicaEscalamiento.servicio",
        countQuery = "select count(politicaEscalamiento) from PoliticaEscalamiento politicaEscalamiento"
    )
    Page<PoliticaEscalamiento> findAllWithToOneRelationships(Pageable pageable);

    @Query("select politicaEscalamiento from PoliticaEscalamiento politicaEscalamiento left join fetch politicaEscalamiento.servicio")
    List<PoliticaEscalamiento> findAllWithToOneRelationships();

    @Query(
        "select politicaEscalamiento from PoliticaEscalamiento politicaEscalamiento left join fetch politicaEscalamiento.servicio where politicaEscalamiento.id =:id"
    )
    Optional<PoliticaEscalamiento> findOneWithToOneRelationships(@Param("id") Long id);
}
