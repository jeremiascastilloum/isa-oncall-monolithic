package ar.edu.um.isa.oncall.service;

import ar.edu.um.isa.oncall.domain.*; // for static metamodels
import ar.edu.um.isa.oncall.domain.Incidente;
import ar.edu.um.isa.oncall.repository.IncidenteRepository;
import ar.edu.um.isa.oncall.service.criteria.IncidenteCriteria;
import ar.edu.um.isa.oncall.service.dto.IncidenteDTO;
import ar.edu.um.isa.oncall.service.mapper.IncidenteMapper;
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
 * Service for executing complex queries for {@link Incidente} entities in the database.
 * The main input is a {@link IncidenteCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link IncidenteDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class IncidenteQueryService extends QueryService<Incidente> {

    private static final Logger LOG = LoggerFactory.getLogger(IncidenteQueryService.class);

    private final IncidenteRepository incidenteRepository;

    private final IncidenteMapper incidenteMapper;

    public IncidenteQueryService(IncidenteRepository incidenteRepository, IncidenteMapper incidenteMapper) {
        this.incidenteRepository = incidenteRepository;
        this.incidenteMapper = incidenteMapper;
    }

    /**
     * Return a {@link Page} of {@link IncidenteDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<IncidenteDTO> findByCriteria(IncidenteCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Incidente> specification = createSpecification(criteria);
        return incidenteRepository.fetchBagRelationships(incidenteRepository.findAll(specification, page)).map(incidenteMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(IncidenteCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Incidente> specification = createSpecification(criteria);
        return incidenteRepository.count(specification);
    }

    /**
     * Function to convert {@link IncidenteCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Incidente> createSpecification(IncidenteCriteria criteria) {
        Specification<Incidente> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(Incidente_.comandante, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), Incidente_.id),
                    buildStringSpecification(criteria.getTitulo(), Incidente_.titulo),
                    buildStringSpecification(criteria.getDescripcion(), Incidente_.descripcion),
                    buildSpecification(criteria.getSeveridad(), Incidente_.severidad),
                    buildSpecification(criteria.getEstado(), Incidente_.estado),
                    buildRangeSpecification(criteria.getDetectadoEn(), Incidente_.detectadoEn),
                    buildRangeSpecification(criteria.getReconocidoEn(), Incidente_.reconocidoEn),
                    buildRangeSpecification(criteria.getMitigadoEn(), Incidente_.mitigadoEn),
                    buildRangeSpecification(criteria.getResueltoEn(), Incidente_.resueltoEn),
                    buildRangeSpecification(criteria.getUsuariosAfectados(), Incidente_.usuariosAfectados),
                    buildSpecification(criteria.getCumplioObjetivo(), Incidente_.cumplioObjetivo),
                    buildSpecification(criteria.getComandanteId(), root -> root.join(Incidente_.comandante, JoinType.LEFT).get(User_.id)),
                    buildSpecification(criteria.getServicioId(), root -> root.join(Incidente_.servicios, JoinType.LEFT).get(Servicio_.id)),
                    buildSpecification(criteria.getPostmortemId(), root ->
                        root.join(Incidente_.postmortem, JoinType.LEFT).get(Postmortem_.id)
                    ),
                    buildSpecification(criteria.getAlertaId(), root -> root.join(Incidente_.alertas, JoinType.LEFT).get(Alerta_.id)),
                    buildSpecification(criteria.getEventoId(), root ->
                        root.join(Incidente_.eventos, JoinType.LEFT).get(EventoDeIncidente_.id)
                    ),
                    buildSpecification(criteria.getNotificacionId(), root ->
                        root.join(Incidente_.notificacions, JoinType.LEFT).get(Notificacion_.id)
                    )
                )
            );
        }
        return specification;
    }
}
