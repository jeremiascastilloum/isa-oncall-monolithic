package ar.edu.um.isa.oncall.service;

import ar.edu.um.isa.oncall.domain.Notificacion;
import ar.edu.um.isa.oncall.repository.NotificacionRepository;
import ar.edu.um.isa.oncall.service.dto.NotificacionDTO;
import ar.edu.um.isa.oncall.service.mapper.NotificacionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ar.edu.um.isa.oncall.domain.Notificacion}.
 */
@Service
@Transactional
public class NotificacionService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificacionService.class);

    private final NotificacionRepository notificacionRepository;

    private final NotificacionMapper notificacionMapper;

    public NotificacionService(NotificacionRepository notificacionRepository, NotificacionMapper notificacionMapper) {
        this.notificacionRepository = notificacionRepository;
        this.notificacionMapper = notificacionMapper;
    }

    /**
     * Save a notificacion.
     *
     * @param notificacionDTO the entity to save.
     * @return the persisted entity.
     */
    public NotificacionDTO save(NotificacionDTO notificacionDTO) {
        LOG.debug("Request to save Notificacion : {}", notificacionDTO);
        Notificacion notificacion = notificacionMapper.toEntity(notificacionDTO);
        notificacion = notificacionRepository.save(notificacion);
        return notificacionMapper.toDto(notificacion);
    }

    /**
     * Update a notificacion.
     *
     * @param notificacionDTO the entity to save.
     * @return the persisted entity.
     */
    public NotificacionDTO update(NotificacionDTO notificacionDTO) {
        LOG.debug("Request to update Notificacion : {}", notificacionDTO);
        Notificacion notificacion = notificacionMapper.toEntity(notificacionDTO);
        notificacion = notificacionRepository.save(notificacion);
        return notificacionMapper.toDto(notificacion);
    }

    /**
     * Partially update a notificacion.
     *
     * @param notificacionDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<NotificacionDTO> partialUpdate(NotificacionDTO notificacionDTO) {
        LOG.debug("Request to partially update Notificacion : {}", notificacionDTO);

        return notificacionRepository
            .findById(notificacionDTO.getId())
            .map(existingNotificacion -> {
                notificacionMapper.partialUpdate(existingNotificacion, notificacionDTO);

                return existingNotificacion;
            })
            .map(notificacionRepository::save)
            .map(notificacionMapper::toDto);
    }

    /**
     * Get all the notificacions with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<NotificacionDTO> findAllWithEagerRelationships(Pageable pageable) {
        return notificacionRepository.findAllWithEagerRelationships(pageable).map(notificacionMapper::toDto);
    }

    /**
     * Get one notificacion by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<NotificacionDTO> findOne(Long id) {
        LOG.debug("Request to get Notificacion : {}", id);
        return notificacionRepository.findOneWithEagerRelationships(id).map(notificacionMapper::toDto);
    }

    /**
     * Delete the notificacion by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Notificacion : {}", id);
        notificacionRepository.deleteById(id);
    }
}
