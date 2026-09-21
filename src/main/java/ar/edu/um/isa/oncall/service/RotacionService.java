package ar.edu.um.isa.oncall.service;

import ar.edu.um.isa.oncall.domain.Rotacion;
import ar.edu.um.isa.oncall.repository.RotacionRepository;
import ar.edu.um.isa.oncall.service.dto.RotacionDTO;
import ar.edu.um.isa.oncall.service.mapper.RotacionMapper;
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
 * Service Implementation for managing {@link ar.edu.um.isa.oncall.domain.Rotacion}.
 */
@Service
@Transactional
public class RotacionService {

    private static final Logger LOG = LoggerFactory.getLogger(RotacionService.class);

    private final RotacionRepository rotacionRepository;

    private final RotacionMapper rotacionMapper;

    public RotacionService(RotacionRepository rotacionRepository, RotacionMapper rotacionMapper) {
        this.rotacionRepository = rotacionRepository;
        this.rotacionMapper = rotacionMapper;
    }

    /**
     * Save a rotacion.
     *
     * @param rotacionDTO the entity to save.
     * @return the persisted entity.
     */
    public RotacionDTO save(RotacionDTO rotacionDTO) {
        LOG.debug("Request to save Rotacion : {}", rotacionDTO);
        Rotacion rotacion = rotacionMapper.toEntity(rotacionDTO);
        rotacion = rotacionRepository.save(rotacion);
        return rotacionMapper.toDto(rotacion);
    }

    /**
     * Update a rotacion.
     *
     * @param rotacionDTO the entity to save.
     * @return the persisted entity.
     */
    public RotacionDTO update(RotacionDTO rotacionDTO) {
        LOG.debug("Request to update Rotacion : {}", rotacionDTO);
        Rotacion rotacion = rotacionMapper.toEntity(rotacionDTO);
        rotacion = rotacionRepository.save(rotacion);
        return rotacionMapper.toDto(rotacion);
    }

    /**
     * Partially update a rotacion.
     *
     * @param rotacionDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<RotacionDTO> partialUpdate(RotacionDTO rotacionDTO) {
        LOG.debug("Request to partially update Rotacion : {}", rotacionDTO);

        return rotacionRepository
            .findById(rotacionDTO.getId())
            .map(existingRotacion -> {
                rotacionMapper.partialUpdate(existingRotacion, rotacionDTO);

                return existingRotacion;
            })
            .map(rotacionRepository::save)
            .map(rotacionMapper::toDto);
    }

    /**
     * Get all the rotacions.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<RotacionDTO> findAll() {
        LOG.debug("Request to get all Rotacions");
        return rotacionRepository.findAll().stream().map(rotacionMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the rotacions with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<RotacionDTO> findAllWithEagerRelationships(Pageable pageable) {
        return rotacionRepository.findAllWithEagerRelationships(pageable).map(rotacionMapper::toDto);
    }

    /**
     * Get one rotacion by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<RotacionDTO> findOne(Long id) {
        LOG.debug("Request to get Rotacion : {}", id);
        return rotacionRepository.findOneWithEagerRelationships(id).map(rotacionMapper::toDto);
    }

    /**
     * Delete the rotacion by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Rotacion : {}", id);
        rotacionRepository.deleteById(id);
    }
}
