package ar.edu.um.isa.oncall.service;

import ar.edu.um.isa.oncall.domain.*; // for static metamodels
import ar.edu.um.isa.oncall.domain.Servicio;
import ar.edu.um.isa.oncall.repository.ServicioRepository;
import ar.edu.um.isa.oncall.service.criteria.ServicioCriteria;
import ar.edu.um.isa.oncall.service.dto.ServicioDTO;
import ar.edu.um.isa.oncall.service.mapper.ServicioMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Servicio} entities in the database.
 * The main input is a {@link ServicioCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ServicioDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ServicioQueryService extends QueryService<Servicio> {

    private static final Logger LOG = LoggerFactory.getLogger(ServicioQueryService.class);

    private final ServicioRepository servicioRepository;

    private final ServicioMapper servicioMapper;

    public ServicioQueryService(ServicioRepository servicioRepository, ServicioMapper servicioMapper) {
        this.servicioRepository = servicioRepository;
        this.servicioMapper = servicioMapper;
    }

    /**
     * Return a {@link List} of {@link ServicioDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ServicioDTO> findByCriteria(ServicioCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<Servicio> specification = createSpecification(criteria);
        return servicioMapper.toDto(servicioRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ServicioCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Servicio> specification = createSpecification(criteria);
        return servicioRepository.count(specification);
    }

    /**
     * Function to convert {@link ServicioCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Servicio> createSpecification(ServicioCriteria criteria) {
        Specification<Servicio> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(Servicio_.equipo, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), Servicio_.id),
                    buildStringSpecification(criteria.getNombre(), Servicio_.nombre),
                    buildStringSpecification(criteria.getDescripcion(), Servicio_.descripcion),
                    buildSpecification(criteria.getCriticidad(), Servicio_.criticidad),
                    buildSpecification(criteria.getEntorno(), Servicio_.entorno),
                    buildStringSpecification(criteria.getRepositorioUrl(), Servicio_.repositorioUrl),
                    buildSpecification(criteria.getActivo(), Servicio_.activo),
                    buildSpecification(criteria.getEquipoId(), root -> root.join(Servicio_.equipo, JoinType.LEFT).get(Equipo_.id)),
                    buildSpecification(criteria.getObjetivoId(), root ->
                        root.join(Servicio_.objetivos, JoinType.LEFT).get(ObjetivoDeServicio_.id)
                    ),
                    buildSpecification(criteria.getAlertaId(), root -> root.join(Servicio_.alertas, JoinType.LEFT).get(Alerta_.id)),
                    buildSpecification(criteria.getPoliticaId(), root ->
                        root.join(Servicio_.politicas, JoinType.LEFT).get(PoliticaEscalamiento_.id)
                    ),
                    buildSpecification(criteria.getIncidenteId(), root -> root.join(Servicio_.incidentes, JoinType.LEFT).get(Incidente_.id))
                )
            );
        }
        return specification;
    }
}
