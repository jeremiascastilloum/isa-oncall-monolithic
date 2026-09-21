package ar.edu.um.isa.oncall.service;

import ar.edu.um.isa.oncall.domain.Incidente;
import ar.edu.um.isa.oncall.repository.IncidenteRepository;
import ar.edu.um.isa.oncall.service.dto.IncidenteDTO;
import ar.edu.um.isa.oncall.service.mapper.IncidenteMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ar.edu.um.isa.oncall.domain.Incidente}.
 */
@Service
@Transactional
public class IncidenteService {

    private static final Logger LOG = LoggerFactory.getLogger(IncidenteService.class);

    private final IncidenteRepository incidenteRepository;

    private final IncidenteMapper incidenteMapper;

    public IncidenteService(IncidenteRepository incidenteRepository, IncidenteMapper incidenteMapper) {
        this.incidenteRepository = incidenteRepository;
        this.incidenteMapper = incidenteMapper;
    }

    /**
     * Save a incidente.
     *
     * @param incidenteDTO the entity to save.
     * @return the persisted entity.
     */
    public IncidenteDTO save(IncidenteDTO incidenteDTO) {
        LOG.debug("Request to save Incidente : {}", incidenteDTO);
        Incidente incidente = incidenteMapper.toEntity(incidenteDTO);
        incidente = incidenteRepository.save(incidente);
        return incidenteMapper.toDto(incidente);
    }

    /**
     * Update a incidente.
     *
     * @param incidenteDTO the entity to save.
     * @return the persisted entity.
     */
    public IncidenteDTO update(IncidenteDTO incidenteDTO) {
        LOG.debug("Request to update Incidente : {}", incidenteDTO);
        Incidente incidente = incidenteMapper.toEntity(incidenteDTO);
        incidente = incidenteRepository.save(incidente);
        return incidenteMapper.toDto(incidente);
    }

    /**
     * Partially update a incidente.
     *
     * @param incidenteDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<IncidenteDTO> partialUpdate(IncidenteDTO incidenteDTO) {
        LOG.debug("Request to partially update Incidente : {}", incidenteDTO);

        return incidenteRepository
            .findById(incidenteDTO.getId())
            .map(existingIncidente -> {
                incidenteMapper.partialUpdate(existingIncidente, incidenteDTO);

                return existingIncidente;
            })
            .map(incidenteRepository::save)
            .map(incidenteMapper::toDto);
    }

    /**
     * Get all the incidentes with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<IncidenteDTO> findAllWithEagerRelationships(Pageable pageable) {
        return incidenteRepository.findAllWithEagerRelationships(pageable).map(incidenteMapper::toDto);
    }

    /**
     *  Get all the incidentes where Postmortem is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<IncidenteDTO> findAllWherePostmortemIsNull() {
        LOG.debug("Request to get all incidentes where Postmortem is null");
        return StreamSupport.stream(incidenteRepository.findAll().spliterator(), false)
            .filter(incidente -> incidente.getPostmortem() == null)
            .map(incidenteMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one incidente by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<IncidenteDTO> findOne(Long id) {
        LOG.debug("Request to get Incidente : {}", id);
        return incidenteRepository.findOneWithEagerRelationships(id).map(incidenteMapper::toDto);
    }

    /**
     * Delete the incidente by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Incidente : {}", id);
        incidenteRepository.deleteById(id);
    }
}
