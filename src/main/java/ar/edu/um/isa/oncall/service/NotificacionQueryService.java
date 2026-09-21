package ar.edu.um.isa.oncall.service;

import ar.edu.um.isa.oncall.domain.*; // for static metamodels
import ar.edu.um.isa.oncall.domain.Notificacion;
import ar.edu.um.isa.oncall.repository.NotificacionRepository;
import ar.edu.um.isa.oncall.service.criteria.NotificacionCriteria;
import ar.edu.um.isa.oncall.service.dto.NotificacionDTO;
import ar.edu.um.isa.oncall.service.mapper.NotificacionMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Notificacion} entities in the database.
 * The main input is a {@link NotificacionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link NotificacionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class NotificacionQueryService extends QueryService<Notificacion> {

    private static final Logger LOG = LoggerFactory.getLogger(NotificacionQueryService.class);

    private final NotificacionRepository notificacionRepository;

    private final NotificacionMapper notificacionMapper;

    public NotificacionQueryService(NotificacionRepository notificacionRepository, NotificacionMapper notificacionMapper) {
        this.notificacionRepository = notificacionRepository;
        this.notificacionMapper = notificacionMapper;
    }

    /**
     * Return a {@link Page} of {@link NotificacionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<NotificacionDTO> findByCriteria(NotificacionCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Notificacion> specification = createSpecification(criteria);
        return notificacionRepository.findAll(specification, page).map(notificacionMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(NotificacionCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Notificacion> specification = createSpecification(criteria);
        return notificacionRepository.count(specification);
    }

    /**
     * Function to convert {@link NotificacionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Notificacion> createSpecification(NotificacionCriteria criteria) {
        Specification<Notificacion> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(Notificacion_.incidente, JoinType.LEFT);
                root.fetch(Notificacion_.destinatario, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), Notificacion_.id),
                    buildSpecification(criteria.getCanal(), Notificacion_.canal),
                    buildStringSpecification(criteria.getDestino(), Notificacion_.destino),
                    buildSpecification(criteria.getEstado(), Notificacion_.estado),
                    buildRangeSpecification(criteria.getEnviadaEn(), Notificacion_.enviadaEn),
                    buildRangeSpecification(criteria.getIntentos(), Notificacion_.intentos),
                    buildStringSpecification(criteria.getErrorMensaje(), Notificacion_.errorMensaje),
                    buildSpecification(criteria.getIncidenteId(), root ->
                        root.join(Notificacion_.incidente, JoinType.LEFT).get(Incidente_.id)
                    ),
                    buildSpecification(criteria.getDestinatarioId(), root ->
                        root.join(Notificacion_.destinatario, JoinType.LEFT).get(User_.id)
                    )
                )
            );
        }
        return specification;
    }
}
