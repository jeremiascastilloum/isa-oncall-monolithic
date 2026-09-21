package ar.edu.um.isa.oncall.repository;

import ar.edu.um.isa.oncall.domain.ObjetivoDeServicio;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ObjetivoDeServicio entity.
 */
@Repository
public interface ObjetivoDeServicioRepository extends JpaRepository<ObjetivoDeServicio, Long> {
    default Optional<ObjetivoDeServicio> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ObjetivoDeServicio> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ObjetivoDeServicio> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select objetivoDeServicio from ObjetivoDeServicio objetivoDeServicio left join fetch objetivoDeServicio.servicio",
        countQuery = "select count(objetivoDeServicio) from ObjetivoDeServicio objetivoDeServicio"
    )
    Page<ObjetivoDeServicio> findAllWithToOneRelationships(Pageable pageable);

    @Query("select objetivoDeServicio from ObjetivoDeServicio objetivoDeServicio left join fetch objetivoDeServicio.servicio")
    List<ObjetivoDeServicio> findAllWithToOneRelationships();

    @Query(
        "select objetivoDeServicio from ObjetivoDeServicio objetivoDeServicio left join fetch objetivoDeServicio.servicio where objetivoDeServicio.id =:id"
    )
    Optional<ObjetivoDeServicio> findOneWithToOneRelationships(@Param("id") Long id);
}
