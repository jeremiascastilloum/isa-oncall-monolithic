package ar.edu.um.isa.oncall.repository;

import ar.edu.um.isa.oncall.domain.PasoEscalamiento;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PasoEscalamiento entity.
 */
@Repository
public interface PasoEscalamientoRepository extends JpaRepository<PasoEscalamiento, Long> {
    @Query(
        "select pasoEscalamiento from PasoEscalamiento pasoEscalamiento where pasoEscalamiento.destinatarioDirecto.login = ?#{authentication.name}"
    )
    List<PasoEscalamiento> findByDestinatarioDirectoIsCurrentUser();

    default Optional<PasoEscalamiento> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<PasoEscalamiento> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<PasoEscalamiento> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select pasoEscalamiento from PasoEscalamiento pasoEscalamiento left join fetch pasoEscalamiento.politica left join fetch pasoEscalamiento.rotacion left join fetch pasoEscalamiento.destinatarioDirecto",
        countQuery = "select count(pasoEscalamiento) from PasoEscalamiento pasoEscalamiento"
    )
    Page<PasoEscalamiento> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select pasoEscalamiento from PasoEscalamiento pasoEscalamiento left join fetch pasoEscalamiento.politica left join fetch pasoEscalamiento.rotacion left join fetch pasoEscalamiento.destinatarioDirecto"
    )
    List<PasoEscalamiento> findAllWithToOneRelationships();

    @Query(
        "select pasoEscalamiento from PasoEscalamiento pasoEscalamiento left join fetch pasoEscalamiento.politica left join fetch pasoEscalamiento.rotacion left join fetch pasoEscalamiento.destinatarioDirecto where pasoEscalamiento.id =:id"
    )
    Optional<PasoEscalamiento> findOneWithToOneRelationships(@Param("id") Long id);
}
