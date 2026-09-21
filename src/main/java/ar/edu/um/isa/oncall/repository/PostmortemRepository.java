package ar.edu.um.isa.oncall.repository;

import ar.edu.um.isa.oncall.domain.Postmortem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Postmortem entity.
 */
@Repository
public interface PostmortemRepository extends JpaRepository<Postmortem, Long> {
    default Optional<Postmortem> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Postmortem> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Postmortem> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select postmortem from Postmortem postmortem left join fetch postmortem.incidente",
        countQuery = "select count(postmortem) from Postmortem postmortem"
    )
    Page<Postmortem> findAllWithToOneRelationships(Pageable pageable);

    @Query("select postmortem from Postmortem postmortem left join fetch postmortem.incidente")
    List<Postmortem> findAllWithToOneRelationships();

    @Query("select postmortem from Postmortem postmortem left join fetch postmortem.incidente where postmortem.id =:id")
    Optional<Postmortem> findOneWithToOneRelationships(@Param("id") Long id);
}
