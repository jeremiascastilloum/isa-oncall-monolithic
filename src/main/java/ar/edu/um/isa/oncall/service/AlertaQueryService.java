package ar.edu.um.isa.oncall.service;

import ar.edu.um.isa.oncall.domain.*; // for static metamodels
import ar.edu.um.isa.oncall.domain.Alerta;
import ar.edu.um.isa.oncall.repository.AlertaRepository;
import ar.edu.um.isa.oncall.service.criteria.AlertaCriteria;
import ar.edu.um.isa.oncall.service.dto.AlertaDTO;
import ar.edu.um.isa.oncall.service.mapper.AlertaMapper;
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
 * Service for executing complex queries for {@link Alerta} entities in the database.
 * The main input is a {@link AlertaCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link AlertaDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AlertaQueryService extends QueryService<Alerta> {

    private static final Logger LOG = LoggerFactory.getLogger(AlertaQueryService.class);

    private final AlertaRepository alertaRepository;

    private final AlertaMapper alertaMapper;

    public AlertaQueryService(AlertaRepository alertaRepository, AlertaMapper alertaMapper) {
        this.alertaRepository = alertaRepository;
        this.alertaMapper = alertaMapper;
    }

    /**
     * Return a {@link Page} of {@link AlertaDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<AlertaDTO> findByCriteria(AlertaCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Alerta> specification = createSpecification(criteria);
        return alertaRepository.findAll(specification, page).map(alertaMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AlertaCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Alerta> specification = createSpecification(criteria);
        return alertaRepository.count(specification);
    }

    /**
     * Function to convert {@link AlertaCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Alerta> createSpecification(AlertaCriteria criteria) {
        Specification<Alerta> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(Alerta_.servicio, JoinType.LEFT);
                root.fetch(Alerta_.incidente, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), Alerta_.id),
                    buildStringSpecification(criteria.getFingerprint(), Alerta_.fingerprint),
                    buildSpecification(criteria.getOrigen(), Alerta_.origen),
                    buildStringSpecification(criteria.getResumen(), Alerta_.resumen),
                    buildStringSpecification(criteria.getPayload(), Alerta_.payload),
                    buildRangeSpecification(criteria.getRecibidaEn(), Alerta_.recibidaEn),
                    buildSpecification(criteria.getProcesada(), Alerta_.procesada),
                    buildSpecification(criteria.getServicioId(), root -> root.join(Alerta_.servicio, JoinType.LEFT).get(Servicio_.id)),
                    buildSpecification(criteria.getIncidenteId(), root -> root.join(Alerta_.incidente, JoinType.LEFT).get(Incidente_.id))
                )
            );
        }
        return specification;
    }
}
