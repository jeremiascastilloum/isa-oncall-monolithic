package ar.edu.um.isa.oncall.web.rest;

import ar.edu.um.isa.oncall.repository.PostmortemRepository;
import ar.edu.um.isa.oncall.service.PostmortemService;
import ar.edu.um.isa.oncall.service.dto.PostmortemDTO;
import ar.edu.um.isa.oncall.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link ar.edu.um.isa.oncall.domain.Postmortem}.
 */
@RestController
@RequestMapping("/api/postmortems")
public class PostmortemResource {

    private static final Logger LOG = LoggerFactory.getLogger(PostmortemResource.class);

    private static final String ENTITY_NAME = "postmortem";

    @Value("${jhipster.clientApp.name:oncall}")
    private String applicationName;

    private final PostmortemService postmortemService;

    private final PostmortemRepository postmortemRepository;

    public PostmortemResource(PostmortemService postmortemService, PostmortemRepository postmortemRepository) {
        this.postmortemService = postmortemService;
        this.postmortemRepository = postmortemRepository;
    }

    /**
     * {@code POST  /postmortems} : Create a new postmortem.
     *
     * @param postmortemDTO the postmortemDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new postmortemDTO, or with status {@code 400 (Bad Request)} if the postmortem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PostmortemDTO> createPostmortem(@Valid @RequestBody PostmortemDTO postmortemDTO) throws URISyntaxException {
        LOG.debug("REST request to save Postmortem : {}", postmortemDTO);
        if (postmortemDTO.getId() != null) {
            throw new BadRequestAlertException("A new postmortem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        postmortemDTO = postmortemService.save(postmortemDTO);
        return ResponseEntity.created(new URI("/api/postmortems/" + postmortemDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, postmortemDTO.getId().toString()))
            .body(postmortemDTO);
    }

    /**
     * {@code PUT  /postmortems/:id} : Updates an existing postmortem.
     *
     * @param id the id of the postmortemDTO to save.
     * @param postmortemDTO the postmortemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated postmortemDTO,
     * or with status {@code 400 (Bad Request)} if the postmortemDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the postmortemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PostmortemDTO> updatePostmortem(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PostmortemDTO postmortemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Postmortem : {}, {}", id, postmortemDTO);
        if (postmortemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, postmortemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!postmortemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        postmortemDTO = postmortemService.update(postmortemDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, postmortemDTO.getId().toString()))
            .body(postmortemDTO);
    }

    /**
     * {@code PATCH  /postmortems/:id} : Partial updates given fields of an existing postmortem, field will ignore if it is null
     *
     * @param id the id of the postmortemDTO to save.
     * @param postmortemDTO the postmortemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated postmortemDTO,
     * or with status {@code 400 (Bad Request)} if the postmortemDTO is not valid,
     * or with status {@code 404 (Not Found)} if the postmortemDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the postmortemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PostmortemDTO> partialUpdatePostmortem(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PostmortemDTO postmortemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Postmortem partially : {}, {}", id, postmortemDTO);
        if (postmortemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, postmortemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!postmortemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PostmortemDTO> result = postmortemService.partialUpdate(postmortemDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, postmortemDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /postmortems} : get all the Postmortems.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Postmortems in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PostmortemDTO>> getAllPostmortems(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of Postmortems");
        Page<PostmortemDTO> page;
        if (eagerload) {
            page = postmortemService.findAllWithEagerRelationships(pageable);
        } else {
            page = postmortemService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /postmortems/:id} : get the "id" postmortem.
     *
     * @param id the id of the postmortemDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the postmortemDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PostmortemDTO> getPostmortem(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Postmortem : {}", id);
        Optional<PostmortemDTO> postmortemDTO = postmortemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(postmortemDTO);
    }

    /**
     * {@code DELETE  /postmortems/:id} : delete the "id" postmortem.
     *
     * @param id the id of the postmortemDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePostmortem(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Postmortem : {}", id);
        postmortemService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
