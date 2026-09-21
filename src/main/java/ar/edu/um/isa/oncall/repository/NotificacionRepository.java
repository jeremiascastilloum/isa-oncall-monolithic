package ar.edu.um.isa.oncall.repository;

import ar.edu.um.isa.oncall.domain.Notificacion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Notificacion entity.
 */
@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long>, JpaSpecificationExecutor<Notificacion> {
    @Query("select notificacion from Notificacion notificacion where notificacion.destinatario.login = ?#{authentication.name}")
    List<Notificacion> findByDestinatarioIsCurrentUser();

    default Optional<Notificacion> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Notificacion> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Notificacion> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select notificacion from Notificacion notificacion left join fetch notificacion.incidente left join fetch notificacion.destinatario",
        countQuery = "select count(notificacion) from Notificacion notificacion"
    )
    Page<Notificacion> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select notificacion from Notificacion notificacion left join fetch notificacion.incidente left join fetch notificacion.destinatario"
    )
    List<Notificacion> findAllWithToOneRelationships();

    @Query(
        "select notificacion from Notificacion notificacion left join fetch notificacion.incidente left join fetch notificacion.destinatario where notificacion.id =:id"
    )
    Optional<Notificacion> findOneWithToOneRelationships(@Param("id") Long id);
}
