package ar.edu.um.isa.oncall.service;

import ar.edu.um.isa.oncall.domain.ObjetivoDeServicio;
import ar.edu.um.isa.oncall.repository.ObjetivoDeServicioRepository;
import ar.edu.um.isa.oncall.service.dto.ObjetivoDeServicioDTO;
import ar.edu.um.isa.oncall.service.mapper.ObjetivoDeServicioMapper;
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
 * Service Implementation for managing {@link ar.edu.um.isa.oncall.domain.ObjetivoDeServicio}.
 */
@Service
@Transactional
public class ObjetivoDeServicioService {

    private static final Logger LOG = LoggerFactory.getLogger(ObjetivoDeServicioService.class);

    private final ObjetivoDeServicioRepository objetivoDeServicioRepository;

    private final ObjetivoDeServicioMapper objetivoDeServicioMapper;

    public ObjetivoDeServicioService(
        ObjetivoDeServicioRepository objetivoDeServicioRepository,
        ObjetivoDeServicioMapper objetivoDeServicioMapper
    ) {
        this.objetivoDeServicioRepository = objetivoDeServicioRepository;
        this.objetivoDeServicioMapper = objetivoDeServicioMapper;
    }

    /**
     * Save a objetivoDeServicio.
     *
     * @param objetivoDeServicioDTO the entity to save.
     * @return the persisted entity.
     */
    public ObjetivoDeServicioDTO save(ObjetivoDeServicioDTO objetivoDeServicioDTO) {
        LOG.debug("Request to save ObjetivoDeServicio : {}", objetivoDeServicioDTO);
        ObjetivoDeServicio objetivoDeServicio = objetivoDeServicioMapper.toEntity(objetivoDeServicioDTO);
        objetivoDeServicio = objetivoDeServicioRepository.save(objetivoDeServicio);
        return objetivoDeServicioMapper.toDto(objetivoDeServicio);
    }

    /**
     * Update a objetivoDeServicio.
     *
     * @param objetivoDeServicioDTO the entity to save.
     * @return the persisted entity.
     */
    public ObjetivoDeServicioDTO update(ObjetivoDeServicioDTO objetivoDeServicioDTO) {
        LOG.debug("Request to update ObjetivoDeServicio : {}", objetivoDeServicioDTO);
        ObjetivoDeServicio objetivoDeServicio = objetivoDeServicioMapper.toEntity(objetivoDeServicioDTO);
        objetivoDeServicio = objetivoDeServicioRepository.save(objetivoDeServicio);
        return objetivoDeServicioMapper.toDto(objetivoDeServicio);
    }

    /**
     * Partially update a objetivoDeServicio.
     *
     * @param objetivoDeServicioDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ObjetivoDeServicioDTO> partialUpdate(ObjetivoDeServicioDTO objetivoDeServicioDTO) {
        LOG.debug("Request to partially update ObjetivoDeServicio : {}", objetivoDeServicioDTO);

        return objetivoDeServicioRepository
            .findById(objetivoDeServicioDTO.getId())
            .map(existingObjetivoDeServicio -> {
                objetivoDeServicioMapper.partialUpdate(existingObjetivoDeServicio, objetivoDeServicioDTO);

                return existingObjetivoDeServicio;
            })
            .map(objetivoDeServicioRepository::save)
            .map(objetivoDeServicioMapper::toDto);
    }

    /**
     * Get all the objetivoDeServicios.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ObjetivoDeServicioDTO> findAll() {
        LOG.debug("Request to get all ObjetivoDeServicios");
        return objetivoDeServicioRepository
            .findAll()
            .stream()
            .map(objetivoDeServicioMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the objetivoDeServicios with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ObjetivoDeServicioDTO> findAllWithEagerRelationships(Pageable pageable) {
        return objetivoDeServicioRepository.findAllWithEagerRelationships(pageable).map(objetivoDeServicioMapper::toDto);
    }

    /**
     * Get one objetivoDeServicio by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ObjetivoDeServicioDTO> findOne(Long id) {
        LOG.debug("Request to get ObjetivoDeServicio : {}", id);
        return objetivoDeServicioRepository.findOneWithEagerRelationships(id).map(objetivoDeServicioMapper::toDto);
    }

    /**
     * Delete the objetivoDeServicio by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ObjetivoDeServicio : {}", id);
        objetivoDeServicioRepository.deleteById(id);
    }
}
