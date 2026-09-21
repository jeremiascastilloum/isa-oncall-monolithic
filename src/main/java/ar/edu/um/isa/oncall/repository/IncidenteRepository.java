package ar.edu.um.isa.oncall.repository;

import ar.edu.um.isa.oncall.domain.Incidente;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Incidente entity.
 *
 * When extending this class, extend IncidenteRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface IncidenteRepository
    extends IncidenteRepositoryWithBagRelationships, JpaRepository<Incidente, Long>, JpaSpecificationExecutor<Incidente>
{
    @Query("select incidente from Incidente incidente where incidente.comandante.login = ?#{authentication.name}")
    List<Incidente> findByComandanteIsCurrentUser();

    default Optional<Incidente> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findOneWithToOneRelationships(id));
    }

    default List<Incidente> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships());
    }

    default Page<Incidente> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships(pageable));
    }

    @Query(
        value = "select incidente from Incidente incidente left join fetch incidente.comandante",
        countQuery = "select count(incidente) from Incidente incidente"
    )
    Page<Incidente> findAllWithToOneRelationships(Pageable pageable);

    @Query("select incidente from Incidente incidente left join fetch incidente.comandante")
    List<Incidente> findAllWithToOneRelationships();

    @Query("select incidente from Incidente incidente left join fetch incidente.comandante where incidente.id =:id")
    Optional<Incidente> findOneWithToOneRelationships(@Param("id") Long id);
}
