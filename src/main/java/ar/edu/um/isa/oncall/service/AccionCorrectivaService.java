package ar.edu.um.isa.oncall.service;

import ar.edu.um.isa.oncall.domain.AccionCorrectiva;
import ar.edu.um.isa.oncall.repository.AccionCorrectivaRepository;
import ar.edu.um.isa.oncall.service.dto.AccionCorrectivaDTO;
import ar.edu.um.isa.oncall.service.mapper.AccionCorrectivaMapper;
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
 * Service Implementation for managing {@link ar.edu.um.isa.oncall.domain.AccionCorrectiva}.
 */
@Service
@Transactional
public class AccionCorrectivaService {

    private static final Logger LOG = LoggerFactory.getLogger(AccionCorrectivaService.class);

    private final AccionCorrectivaRepository accionCorrectivaRepository;

    private final AccionCorrectivaMapper accionCorrectivaMapper;

    public AccionCorrectivaService(AccionCorrectivaRepository accionCorrectivaRepository, AccionCorrectivaMapper accionCorrectivaMapper) {
        this.accionCorrectivaRepository = accionCorrectivaRepository;
        this.accionCorrectivaMapper = accionCorrectivaMapper;
    }

    /**
     * Save a accionCorrectiva.
     *
     * @param accionCorrectivaDTO the entity to save.
     * @return the persisted entity.
     */
    public AccionCorrectivaDTO save(AccionCorrectivaDTO accionCorrectivaDTO) {
        LOG.debug("Request to save AccionCorrectiva : {}", accionCorrectivaDTO);
        AccionCorrectiva accionCorrectiva = accionCorrectivaMapper.toEntity(accionCorrectivaDTO);
        accionCorrectiva = accionCorrectivaRepository.save(accionCorrectiva);
        return accionCorrectivaMapper.toDto(accionCorrectiva);
    }

    /**
     * Update a accionCorrectiva.
     *
     * @param accionCorrectivaDTO the entity to save.
     * @return the persisted entity.
     */
    public AccionCorrectivaDTO update(AccionCorrectivaDTO accionCorrectivaDTO) {
        LOG.debug("Request to update AccionCorrectiva : {}", accionCorrectivaDTO);
        AccionCorrectiva accionCorrectiva = accionCorrectivaMapper.toEntity(accionCorrectivaDTO);
        accionCorrectiva = accionCorrectivaRepository.save(accionCorrectiva);
        return accionCorrectivaMapper.toDto(accionCorrectiva);
    }

    /**
     * Partially update a accionCorrectiva.
     *
     * @param accionCorrectivaDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<AccionCorrectivaDTO> partialUpdate(AccionCorrectivaDTO accionCorrectivaDTO) {
        LOG.debug("Request to partially update AccionCorrectiva : {}", accionCorrectivaDTO);

        return accionCorrectivaRepository
            .findById(accionCorrectivaDTO.getId())
            .map(existingAccionCorrectiva -> {
                accionCorrectivaMapper.partialUpdate(existingAccionCorrectiva, accionCorrectivaDTO);

                return existingAccionCorrectiva;
            })
            .map(accionCorrectivaRepository::save)
            .map(accionCorrectivaMapper::toDto);
    }

    /**
     * Get all the accionCorrectivas.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<AccionCorrectivaDTO> findAll() {
        LOG.debug("Request to get all AccionCorrectivas");
        return accionCorrectivaRepository
            .findAll()
            .stream()
            .map(accionCorrectivaMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the accionCorrectivas with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<AccionCorrectivaDTO> findAllWithEagerRelationships(Pageable pageable) {
        return accionCorrectivaRepository.findAllWithEagerRelationships(pageable).map(accionCorrectivaMapper::toDto);
    }

    /**
     * Get one accionCorrectiva by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AccionCorrectivaDTO> findOne(Long id) {
        LOG.debug("Request to get AccionCorrectiva : {}", id);
        return accionCorrectivaRepository.findOneWithEagerRelationships(id).map(accionCorrectivaMapper::toDto);
    }

    /**
     * Delete the accionCorrectiva by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete AccionCorrectiva : {}", id);
        accionCorrectivaRepository.deleteById(id);
    }
}
