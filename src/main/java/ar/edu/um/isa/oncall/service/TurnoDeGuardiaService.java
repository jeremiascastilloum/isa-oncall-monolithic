package ar.edu.um.isa.oncall.service;

import ar.edu.um.isa.oncall.domain.TurnoDeGuardia;
import ar.edu.um.isa.oncall.repository.TurnoDeGuardiaRepository;
import ar.edu.um.isa.oncall.service.dto.TurnoDeGuardiaDTO;
import ar.edu.um.isa.oncall.service.mapper.TurnoDeGuardiaMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ar.edu.um.isa.oncall.domain.TurnoDeGuardia}.
 */
@Service
@Transactional
public class TurnoDeGuardiaService {

    private static final Logger LOG = LoggerFactory.getLogger(TurnoDeGuardiaService.class);

    private final TurnoDeGuardiaRepository turnoDeGuardiaRepository;

    private final TurnoDeGuardiaMapper turnoDeGuardiaMapper;

    public TurnoDeGuardiaService(TurnoDeGuardiaRepository turnoDeGuardiaRepository, TurnoDeGuardiaMapper turnoDeGuardiaMapper) {
        this.turnoDeGuardiaRepository = turnoDeGuardiaRepository;
        this.turnoDeGuardiaMapper = turnoDeGuardiaMapper;
    }

    /**
     * Save a turnoDeGuardia.
     *
     * @param turnoDeGuardiaDTO the entity to save.
     * @return the persisted entity.
     */
    public TurnoDeGuardiaDTO save(TurnoDeGuardiaDTO turnoDeGuardiaDTO) {
        LOG.debug("Request to save TurnoDeGuardia : {}", turnoDeGuardiaDTO);
        TurnoDeGuardia turnoDeGuardia = turnoDeGuardiaMapper.toEntity(turnoDeGuardiaDTO);
        turnoDeGuardia = turnoDeGuardiaRepository.save(turnoDeGuardia);
        return turnoDeGuardiaMapper.toDto(turnoDeGuardia);
    }

    /**
     * Update a turnoDeGuardia.
     *
     * @param turnoDeGuardiaDTO the entity to save.
     * @return the persisted entity.
     */
    public TurnoDeGuardiaDTO update(TurnoDeGuardiaDTO turnoDeGuardiaDTO) {
        LOG.debug("Request to update TurnoDeGuardia : {}", turnoDeGuardiaDTO);
        TurnoDeGuardia turnoDeGuardia = turnoDeGuardiaMapper.toEntity(turnoDeGuardiaDTO);
        turnoDeGuardia = turnoDeGuardiaRepository.save(turnoDeGuardia);
        return turnoDeGuardiaMapper.toDto(turnoDeGuardia);
    }

    /**
     * Partially update a turnoDeGuardia.
     *
     * @param turnoDeGuardiaDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TurnoDeGuardiaDTO> partialUpdate(TurnoDeGuardiaDTO turnoDeGuardiaDTO) {
        LOG.debug("Request to partially update TurnoDeGuardia : {}", turnoDeGuardiaDTO);

        return turnoDeGuardiaRepository
            .findById(turnoDeGuardiaDTO.getId())
            .map(existingTurnoDeGuardia -> {
                turnoDeGuardiaMapper.partialUpdate(existingTurnoDeGuardia, turnoDeGuardiaDTO);

                return existingTurnoDeGuardia;
            })
            .map(turnoDeGuardiaRepository::save)
            .map(turnoDeGuardiaMapper::toDto);
    }

    /**
     * Get all the turnoDeGuardias.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<TurnoDeGuardiaDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all TurnoDeGuardias");
        return turnoDeGuardiaRepository.findAll(pageable).map(turnoDeGuardiaMapper::toDto);
    }

    /**
     * Get all the turnoDeGuardias with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<TurnoDeGuardiaDTO> findAllWithEagerRelationships(Pageable pageable) {
        return turnoDeGuardiaRepository.findAllWithEagerRelationships(pageable).map(turnoDeGuardiaMapper::toDto);
    }

    /**
     * Get one turnoDeGuardia by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TurnoDeGuardiaDTO> findOne(Long id) {
        LOG.debug("Request to get TurnoDeGuardia : {}", id);
        return turnoDeGuardiaRepository.findOneWithEagerRelationships(id).map(turnoDeGuardiaMapper::toDto);
    }

    /**
     * Delete the turnoDeGuardia by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete TurnoDeGuardia : {}", id);
        turnoDeGuardiaRepository.deleteById(id);
    }
}
