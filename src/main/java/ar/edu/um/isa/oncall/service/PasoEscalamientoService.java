package ar.edu.um.isa.oncall.service;

import ar.edu.um.isa.oncall.domain.PasoEscalamiento;
import ar.edu.um.isa.oncall.repository.PasoEscalamientoRepository;
import ar.edu.um.isa.oncall.service.dto.PasoEscalamientoDTO;
import ar.edu.um.isa.oncall.service.mapper.PasoEscalamientoMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ar.edu.um.isa.oncall.domain.PasoEscalamiento}.
 */
@Service
@Transactional
public class PasoEscalamientoService {

    private static final Logger LOG = LoggerFactory.getLogger(PasoEscalamientoService.class);

    private final PasoEscalamientoRepository pasoEscalamientoRepository;

    private final PasoEscalamientoMapper pasoEscalamientoMapper;

    public PasoEscalamientoService(PasoEscalamientoRepository pasoEscalamientoRepository, PasoEscalamientoMapper pasoEscalamientoMapper) {
        this.pasoEscalamientoRepository = pasoEscalamientoRepository;
        this.pasoEscalamientoMapper = pasoEscalamientoMapper;
    }

    /**
     * Save a pasoEscalamiento.
     *
     * @param pasoEscalamientoDTO the entity to save.
     * @return the persisted entity.
     */
    public PasoEscalamientoDTO save(PasoEscalamientoDTO pasoEscalamientoDTO) {
        LOG.debug("Request to save PasoEscalamiento : {}", pasoEscalamientoDTO);
        PasoEscalamiento pasoEscalamiento = pasoEscalamientoMapper.toEntity(pasoEscalamientoDTO);
        pasoEscalamiento = pasoEscalamientoRepository.save(pasoEscalamiento);
        return pasoEscalamientoMapper.toDto(pasoEscalamiento);
    }

    /**
     * Update a pasoEscalamiento.
     *
     * @param pasoEscalamientoDTO the entity to save.
     * @return the persisted entity.
     */
    public PasoEscalamientoDTO update(PasoEscalamientoDTO pasoEscalamientoDTO) {
        LOG.debug("Request to update PasoEscalamiento : {}", pasoEscalamientoDTO);
        PasoEscalamiento pasoEscalamiento = pasoEscalamientoMapper.toEntity(pasoEscalamientoDTO);
        pasoEscalamiento = pasoEscalamientoRepository.save(pasoEscalamiento);
        return pasoEscalamientoMapper.toDto(pasoEscalamiento);
    }

    /**
     * Partially update a pasoEscalamiento.
     *
     * @param pasoEscalamientoDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PasoEscalamientoDTO> partialUpdate(PasoEscalamientoDTO pasoEscalamientoDTO) {
        LOG.debug("Request to partially update PasoEscalamiento : {}", pasoEscalamientoDTO);

        return pasoEscalamientoRepository
            .findById(pasoEscalamientoDTO.getId())
            .map(existingPasoEscalamiento -> {
                pasoEscalamientoMapper.partialUpdate(existingPasoEscalamiento, pasoEscalamientoDTO);

                return existingPasoEscalamiento;
            })
            .map(pasoEscalamientoRepository::save)
            .map(pasoEscalamientoMapper::toDto);
    }

    /**
     * Get all the pasoEscalamientos.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<PasoEscalamientoDTO> findAll() {
        LOG.debug("Request to get all PasoEscalamientos");
        return pasoEscalamientoRepository
            .findAll()
            .stream()
            .map(pasoEscalamientoMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the pasoEscalamientos with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<PasoEscalamientoDTO> findAllWithEagerRelationships(Pageable pageable) {
        return pasoEscalamientoRepository.findAllWithEagerRelationships(pageable).map(pasoEscalamientoMapper::toDto);
    }

    /**
     * Get one pasoEscalamiento by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PasoEscalamientoDTO> findOne(Long id) {
        LOG.debug("Request to get PasoEscalamiento : {}", id);
        return pasoEscalamientoRepository.findOneWithEagerRelationships(id).map(pasoEscalamientoMapper::toDto);
    }

    /**
     * Delete the pasoEscalamiento by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete PasoEscalamiento : {}", id);
        pasoEscalamientoRepository.deleteById(id);
    }
}
