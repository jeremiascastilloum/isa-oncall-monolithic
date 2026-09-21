package ar.edu.um.isa.oncall.service;

import ar.edu.um.isa.oncall.domain.Postmortem;
import ar.edu.um.isa.oncall.repository.PostmortemRepository;
import ar.edu.um.isa.oncall.service.dto.PostmortemDTO;
import ar.edu.um.isa.oncall.service.mapper.PostmortemMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ar.edu.um.isa.oncall.domain.Postmortem}.
 */
@Service
@Transactional
public class PostmortemService {

    private static final Logger LOG = LoggerFactory.getLogger(PostmortemService.class);

    private final PostmortemRepository postmortemRepository;

    private final PostmortemMapper postmortemMapper;

    public PostmortemService(PostmortemRepository postmortemRepository, PostmortemMapper postmortemMapper) {
        this.postmortemRepository = postmortemRepository;
        this.postmortemMapper = postmortemMapper;
    }

    /**
     * Save a postmortem.
     *
     * @param postmortemDTO the entity to save.
     * @return the persisted entity.
     */
    public PostmortemDTO save(PostmortemDTO postmortemDTO) {
        LOG.debug("Request to save Postmortem : {}", postmortemDTO);
        Postmortem postmortem = postmortemMapper.toEntity(postmortemDTO);
        postmortem = postmortemRepository.save(postmortem);
        return postmortemMapper.toDto(postmortem);
    }

    /**
     * Update a postmortem.
     *
     * @param postmortemDTO the entity to save.
     * @return the persisted entity.
     */
    public PostmortemDTO update(PostmortemDTO postmortemDTO) {
        LOG.debug("Request to update Postmortem : {}", postmortemDTO);
        Postmortem postmortem = postmortemMapper.toEntity(postmortemDTO);
        postmortem = postmortemRepository.save(postmortem);
        return postmortemMapper.toDto(postmortem);
    }

    /**
     * Partially update a postmortem.
     *
     * @param postmortemDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PostmortemDTO> partialUpdate(PostmortemDTO postmortemDTO) {
        LOG.debug("Request to partially update Postmortem : {}", postmortemDTO);

        return postmortemRepository
            .findById(postmortemDTO.getId())
            .map(existingPostmortem -> {
                postmortemMapper.partialUpdate(existingPostmortem, postmortemDTO);

                return existingPostmortem;
            })
            .map(postmortemRepository::save)
            .map(postmortemMapper::toDto);
    }

    /**
     * Get all the postmortems.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PostmortemDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Postmortems");
        return postmortemRepository.findAll(pageable).map(postmortemMapper::toDto);
    }

    /**
     * Get all the postmortems with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<PostmortemDTO> findAllWithEagerRelationships(Pageable pageable) {
        return postmortemRepository.findAllWithEagerRelationships(pageable).map(postmortemMapper::toDto);
    }

    /**
     * Get one postmortem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PostmortemDTO> findOne(Long id) {
        LOG.debug("Request to get Postmortem : {}", id);
        return postmortemRepository.findOneWithEagerRelationships(id).map(postmortemMapper::toDto);
    }

    /**
     * Delete the postmortem by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Postmortem : {}", id);
        postmortemRepository.deleteById(id);
    }
}
