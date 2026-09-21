package ar.edu.um.isa.oncall.service;

import ar.edu.um.isa.oncall.domain.EventoDeIncidente;
import ar.edu.um.isa.oncall.repository.EventoDeIncidenteRepository;
import ar.edu.um.isa.oncall.service.dto.EventoDeIncidenteDTO;
import ar.edu.um.isa.oncall.service.mapper.EventoDeIncidenteMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ar.edu.um.isa.oncall.domain.EventoDeIncidente}.
 */
@Service
@Transactional
public class EventoDeIncidenteService {

    private static final Logger LOG = LoggerFactory.getLogger(EventoDeIncidenteService.class);

    private final EventoDeIncidenteRepository eventoDeIncidenteRepository;

    private final EventoDeIncidenteMapper eventoDeIncidenteMapper;

    public EventoDeIncidenteService(
        EventoDeIncidenteRepository eventoDeIncidenteRepository,
        EventoDeIncidenteMapper eventoDeIncidenteMapper
    ) {
        this.eventoDeIncidenteRepository = eventoDeIncidenteRepository;
        this.eventoDeIncidenteMapper = eventoDeIncidenteMapper;
    }

    /**
     * Save a eventoDeIncidente.
     *
     * @param eventoDeIncidenteDTO the entity to save.
     * @return the persisted entity.
     */
    public EventoDeIncidenteDTO save(EventoDeIncidenteDTO eventoDeIncidenteDTO) {
        LOG.debug("Request to save EventoDeIncidente : {}", eventoDeIncidenteDTO);
        EventoDeIncidente eventoDeIncidente = eventoDeIncidenteMapper.toEntity(eventoDeIncidenteDTO);
        eventoDeIncidente = eventoDeIncidenteRepository.save(eventoDeIncidente);
        return eventoDeIncidenteMapper.toDto(eventoDeIncidente);
    }

    /**
     * Update a eventoDeIncidente.
     *
     * @param eventoDeIncidenteDTO the entity to save.
     * @return the persisted entity.
     */
    public EventoDeIncidenteDTO update(EventoDeIncidenteDTO eventoDeIncidenteDTO) {
        LOG.debug("Request to update EventoDeIncidente : {}", eventoDeIncidenteDTO);
        EventoDeIncidente eventoDeIncidente = eventoDeIncidenteMapper.toEntity(eventoDeIncidenteDTO);
        eventoDeIncidente = eventoDeIncidenteRepository.save(eventoDeIncidente);
        return eventoDeIncidenteMapper.toDto(eventoDeIncidente);
    }

    /**
     * Partially update a eventoDeIncidente.
     *
     * @param eventoDeIncidenteDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<EventoDeIncidenteDTO> partialUpdate(EventoDeIncidenteDTO eventoDeIncidenteDTO) {
        LOG.debug("Request to partially update EventoDeIncidente : {}", eventoDeIncidenteDTO);

        return eventoDeIncidenteRepository
            .findById(eventoDeIncidenteDTO.getId())
            .map(existingEventoDeIncidente -> {
                eventoDeIncidenteMapper.partialUpdate(existingEventoDeIncidente, eventoDeIncidenteDTO);

                return existingEventoDeIncidente;
            })
            .map(eventoDeIncidenteRepository::save)
            .map(eventoDeIncidenteMapper::toDto);
    }

    /**
     * Get all the eventoDeIncidentes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<EventoDeIncidenteDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all EventoDeIncidentes");
        return eventoDeIncidenteRepository.findAll(pageable).map(eventoDeIncidenteMapper::toDto);
    }

    /**
     * Get all the eventoDeIncidentes with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<EventoDeIncidenteDTO> findAllWithEagerRelationships(Pageable pageable) {
        return eventoDeIncidenteRepository.findAllWithEagerRelationships(pageable).map(eventoDeIncidenteMapper::toDto);
    }

    /**
     * Get one eventoDeIncidente by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<EventoDeIncidenteDTO> findOne(Long id) {
        LOG.debug("Request to get EventoDeIncidente : {}", id);
        return eventoDeIncidenteRepository.findOneWithEagerRelationships(id).map(eventoDeIncidenteMapper::toDto);
    }

    /**
     * Delete the eventoDeIncidente by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete EventoDeIncidente : {}", id);
        eventoDeIncidenteRepository.deleteById(id);
    }
}
