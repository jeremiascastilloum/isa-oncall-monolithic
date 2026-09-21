package ar.edu.um.isa.oncall.web.rest;

import ar.edu.um.isa.oncall.repository.RotacionRepository;
import ar.edu.um.isa.oncall.service.RotacionService;
import ar.edu.um.isa.oncall.service.dto.RotacionDTO;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link ar.edu.um.isa.oncall.domain.Rotacion}.
 */
@RestController
@RequestMapping("/api/rotacions")
public class RotacionResource {

    private static final Logger LOG = LoggerFactory.getLogger(RotacionResource.class);

    private static final String ENTITY_NAME = "rotacion";

    @Value("${jhipster.clientApp.name:oncall}")
    private String applicationName;

    private final RotacionService rotacionService;

    private final RotacionRepository rotacionRepository;

    public RotacionResource(RotacionService rotacionService, RotacionRepository rotacionRepository) {
        this.rotacionService = rotacionService;
        this.rotacionRepository = rotacionRepository;
    }

    /**
     * {@code POST  /rotacions} : Create a new rotacion.
     *
     * @param rotacionDTO the rotacionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new rotacionDTO, or with status {@code 400 (Bad Request)} if the rotacion has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<RotacionDTO> createRotacion(@Valid @RequestBody RotacionDTO rotacionDTO) throws URISyntaxException {
        LOG.debug("REST request to save Rotacion : {}", rotacionDTO);
        if (rotacionDTO.getId() != null) {
            throw new BadRequestAlertException("A new rotacion cannot already have an ID", ENTITY_NAME, "idexists");
        }
        rotacionDTO = rotacionService.save(rotacionDTO);
        return ResponseEntity.created(new URI("/api/rotacions/" + rotacionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, rotacionDTO.getId().toString()))
            .body(rotacionDTO);
    }

    /**
     * {@code PUT  /rotacions/:id} : Updates an existing rotacion.
     *
     * @param id the id of the rotacionDTO to save.
     * @param rotacionDTO the rotacionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rotacionDTO,
     * or with status {@code 400 (Bad Request)} if the rotacionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the rotacionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RotacionDTO> updateRotacion(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RotacionDTO rotacionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Rotacion : {}, {}", id, rotacionDTO);
        if (rotacionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rotacionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!rotacionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        rotacionDTO = rotacionService.update(rotacionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, rotacionDTO.getId().toString()))
            .body(rotacionDTO);
    }

    /**
     * {@code PATCH  /rotacions/:id} : Partial updates given fields of an existing rotacion, field will ignore if it is null
     *
     * @param id the id of the rotacionDTO to save.
     * @param rotacionDTO the rotacionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rotacionDTO,
     * or with status {@code 400 (Bad Request)} if the rotacionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the rotacionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the rotacionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<RotacionDTO> partialUpdateRotacion(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RotacionDTO rotacionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Rotacion partially : {}, {}", id, rotacionDTO);
        if (rotacionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rotacionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!rotacionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RotacionDTO> result = rotacionService.partialUpdate(rotacionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, rotacionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /rotacions} : get all the Rotacions.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Rotacions in body.
     */
    @GetMapping("")
    public List<RotacionDTO> getAllRotacions(@RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload) {
        LOG.debug("REST request to get all Rotacions");
        return rotacionService.findAll();
    }

    /**
     * {@code GET  /rotacions/:id} : get the "id" rotacion.
     *
     * @param id the id of the rotacionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the rotacionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RotacionDTO> getRotacion(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Rotacion : {}", id);
        Optional<RotacionDTO> rotacionDTO = rotacionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(rotacionDTO);
    }

    /**
     * {@code DELETE  /rotacions/:id} : delete the "id" rotacion.
     *
     * @param id the id of the rotacionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRotacion(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Rotacion : {}", id);
        rotacionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
