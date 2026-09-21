package ar.edu.um.isa.oncall.service;

import ar.edu.um.isa.oncall.domain.PoliticaEscalamiento;
import ar.edu.um.isa.oncall.repository.PoliticaEscalamientoRepository;
import ar.edu.um.isa.oncall.service.dto.PoliticaEscalamientoDTO;
import ar.edu.um.isa.oncall.service.mapper.PoliticaEscalamientoMapper;
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
 * Service Implementation for managing {@link ar.edu.um.isa.oncall.domain.PoliticaEscalamiento}.
 */
@Service
@Transactional
public class PoliticaEscalamientoService {

    private static final Logger LOG = LoggerFactory.getLogger(PoliticaEscalamientoService.class);

    private final PoliticaEscalamientoRepository politicaEscalamientoRepository;

    private final PoliticaEscalamientoMapper politicaEscalamientoMapper;

    public PoliticaEscalamientoService(
        PoliticaEscalamientoRepository politicaEscalamientoRepository,
        PoliticaEscalamientoMapper politicaEscalamientoMapper
    ) {
        this.politicaEscalamientoRepository = politicaEscalamientoRepository;
        this.politicaEscalamientoMapper = politicaEscalamientoMapper;
    }

    /**
     * Save a politicaEscalamiento.
     *
     * @param politicaEscalamientoDTO the entity to save.
     * @return the persisted entity.
     */
    public PoliticaEscalamientoDTO save(PoliticaEscalamientoDTO politicaEscalamientoDTO) {
        LOG.debug("Request to save PoliticaEscalamiento : {}", politicaEscalamientoDTO);
        PoliticaEscalamiento politicaEscalamiento = politicaEscalamientoMapper.toEntity(politicaEscalamientoDTO);
        politicaEscalamiento = politicaEscalamientoRepository.save(politicaEscalamiento);
        return politicaEscalamientoMapper.toDto(politicaEscalamiento);
    }

    /**
     * Update a politicaEscalamiento.
     *
     * @param politicaEscalamientoDTO the entity to save.
     * @return the persisted entity.
     */
    public PoliticaEscalamientoDTO update(PoliticaEscalamientoDTO politicaEscalamientoDTO) {
        LOG.debug("Request to update PoliticaEscalamiento : {}", politicaEscalamientoDTO);
        PoliticaEscalamiento politicaEscalamiento = politicaEscalamientoMapper.toEntity(politicaEscalamientoDTO);
        politicaEscalamiento = politicaEscalamientoRepository.save(politicaEscalamiento);
        return politicaEscalamientoMapper.toDto(politicaEscalamiento);
    }

    /**
     * Partially update a politicaEscalamiento.
     *
     * @param politicaEscalamientoDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PoliticaEscalamientoDTO> partialUpdate(PoliticaEscalamientoDTO politicaEscalamientoDTO) {
        LOG.debug("Request to partially update PoliticaEscalamiento : {}", politicaEscalamientoDTO);

        return politicaEscalamientoRepository
            .findById(politicaEscalamientoDTO.getId())
            .map(existingPoliticaEscalamiento -> {
                politicaEscalamientoMapper.partialUpdate(existingPoliticaEscalamiento, politicaEscalamientoDTO);

                return existingPoliticaEscalamiento;
            })
            .map(politicaEscalamientoRepository::save)
            .map(politicaEscalamientoMapper::toDto);
    }

    /**
     * Get all the politicaEscalamientos.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<PoliticaEscalamientoDTO> findAll() {
        LOG.debug("Request to get all PoliticaEscalamientos");
        return politicaEscalamientoRepository
            .findAll()
            .stream()
            .map(politicaEscalamientoMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the politicaEscalamientos with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<PoliticaEscalamientoDTO> findAllWithEagerRelationships(Pageable pageable) {
        return politicaEscalamientoRepository.findAllWithEagerRelationships(pageable).map(politicaEscalamientoMapper::toDto);
    }

    /**
     * Get one politicaEscalamiento by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PoliticaEscalamientoDTO> findOne(Long id) {
        LOG.debug("Request to get PoliticaEscalamiento : {}", id);
        return politicaEscalamientoRepository.findOneWithEagerRelationships(id).map(politicaEscalamientoMapper::toDto);
    }

    /**
     * Delete the politicaEscalamiento by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete PoliticaEscalamiento : {}", id);
        politicaEscalamientoRepository.deleteById(id);
    }
}
