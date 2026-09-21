package ar.edu.um.isa.oncall.service;

import ar.edu.um.isa.oncall.domain.Alerta;
import ar.edu.um.isa.oncall.repository.AlertaRepository;
import ar.edu.um.isa.oncall.service.dto.AlertaDTO;
import ar.edu.um.isa.oncall.service.mapper.AlertaMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ar.edu.um.isa.oncall.domain.Alerta}.
 */
@Service
@Transactional
public class AlertaService {

    private static final Logger LOG = LoggerFactory.getLogger(AlertaService.class);

    private final AlertaRepository alertaRepository;

    private final AlertaMapper alertaMapper;

    public AlertaService(AlertaRepository alertaRepository, AlertaMapper alertaMapper) {
        this.alertaRepository = alertaRepository;
        this.alertaMapper = alertaMapper;
    }

    /**
     * Save a alerta.
     *
     * @param alertaDTO the entity to save.
     * @return the persisted entity.
     */
    public AlertaDTO save(AlertaDTO alertaDTO) {
        LOG.debug("Request to save Alerta : {}", alertaDTO);
        Alerta alerta = alertaMapper.toEntity(alertaDTO);
        alerta = alertaRepository.save(alerta);
        return alertaMapper.toDto(alerta);
    }

    /**
     * Update a alerta.
     *
     * @param alertaDTO the entity to save.
     * @return the persisted entity.
     */
    public AlertaDTO update(AlertaDTO alertaDTO) {
        LOG.debug("Request to update Alerta : {}", alertaDTO);
        Alerta alerta = alertaMapper.toEntity(alertaDTO);
        alerta = alertaRepository.save(alerta);
        return alertaMapper.toDto(alerta);
    }

    /**
     * Partially update a alerta.
     *
     * @param alertaDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<AlertaDTO> partialUpdate(AlertaDTO alertaDTO) {
        LOG.debug("Request to partially update Alerta : {}", alertaDTO);

        return alertaRepository
            .findById(alertaDTO.getId())
            .map(existingAlerta -> {
                alertaMapper.partialUpdate(existingAlerta, alertaDTO);

                return existingAlerta;
            })
            .map(alertaRepository::save)
            .map(alertaMapper::toDto);
    }

    /**
     * Get all the alertas with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<AlertaDTO> findAllWithEagerRelationships(Pageable pageable) {
        return alertaRepository.findAllWithEagerRelationships(pageable).map(alertaMapper::toDto);
    }

    /**
     * Get one alerta by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AlertaDTO> findOne(Long id) {
        LOG.debug("Request to get Alerta : {}", id);
        return alertaRepository.findOneWithEagerRelationships(id).map(alertaMapper::toDto);
    }

    /**
     * Delete the alerta by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Alerta : {}", id);
        alertaRepository.deleteById(id);
    }
}
