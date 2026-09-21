package ar.edu.um.isa.oncall.service;

import ar.edu.um.isa.oncall.domain.Equipo;
import ar.edu.um.isa.oncall.repository.EquipoRepository;
import ar.edu.um.isa.oncall.service.dto.EquipoDTO;
import ar.edu.um.isa.oncall.service.mapper.EquipoMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ar.edu.um.isa.oncall.domain.Equipo}.
 */
@Service
@Transactional
public class EquipoService {

    private static final Logger LOG = LoggerFactory.getLogger(EquipoService.class);

    private final EquipoRepository equipoRepository;

    private final EquipoMapper equipoMapper;

    public EquipoService(EquipoRepository equipoRepository, EquipoMapper equipoMapper) {
        this.equipoRepository = equipoRepository;
        this.equipoMapper = equipoMapper;
    }

    /**
     * Save a equipo.
     *
     * @param equipoDTO the entity to save.
     * @return the persisted entity.
     */
    public EquipoDTO save(EquipoDTO equipoDTO) {
        LOG.debug("Request to save Equipo : {}", equipoDTO);
        Equipo equipo = equipoMapper.toEntity(equipoDTO);
        equipo = equipoRepository.save(equipo);
        return equipoMapper.toDto(equipo);
    }

    /**
     * Update a equipo.
     *
     * @param equipoDTO the entity to save.
     * @return the persisted entity.
     */
    public EquipoDTO update(EquipoDTO equipoDTO) {
        LOG.debug("Request to update Equipo : {}", equipoDTO);
        Equipo equipo = equipoMapper.toEntity(equipoDTO);
        equipo = equipoRepository.save(equipo);
        return equipoMapper.toDto(equipo);
    }

    /**
     * Partially update a equipo.
     *
     * @param equipoDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<EquipoDTO> partialUpdate(EquipoDTO equipoDTO) {
        LOG.debug("Request to partially update Equipo : {}", equipoDTO);

        return equipoRepository
            .findById(equipoDTO.getId())
            .map(existingEquipo -> {
                equipoMapper.partialUpdate(existingEquipo, equipoDTO);

                return existingEquipo;
            })
            .map(equipoRepository::save)
            .map(equipoMapper::toDto);
    }

    /**
     * Get all the equipos.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<EquipoDTO> findAll() {
        LOG.debug("Request to get all Equipos");
        return equipoRepository.findAll().stream().map(equipoMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one equipo by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<EquipoDTO> findOne(Long id) {
        LOG.debug("Request to get Equipo : {}", id);
        return equipoRepository.findById(id).map(equipoMapper::toDto);
    }

    /**
     * Delete the equipo by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Equipo : {}", id);
        equipoRepository.deleteById(id);
    }
}
