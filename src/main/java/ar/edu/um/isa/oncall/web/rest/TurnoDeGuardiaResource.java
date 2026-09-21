package ar.edu.um.isa.oncall.web.rest;

import ar.edu.um.isa.oncall.repository.TurnoDeGuardiaRepository;
import ar.edu.um.isa.oncall.service.TurnoDeGuardiaService;
import ar.edu.um.isa.oncall.service.dto.TurnoDeGuardiaDTO;
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
 * REST controller for managing {@link ar.edu.um.isa.oncall.domain.TurnoDeGuardia}.
 */
@RestController
@RequestMapping("/api/turno-de-guardias")
public class TurnoDeGuardiaResource {

    private static final Logger LOG = LoggerFactory.getLogger(TurnoDeGuardiaResource.class);

    private static final String ENTITY_NAME = "turnoDeGuardia";

    @Value("${jhipster.clientApp.name:oncall}")
    private String applicationName;

    private final TurnoDeGuardiaService turnoDeGuardiaService;

    private final TurnoDeGuardiaRepository turnoDeGuardiaRepository;

    public TurnoDeGuardiaResource(TurnoDeGuardiaService turnoDeGuardiaService, TurnoDeGuardiaRepository turnoDeGuardiaRepository) {
        this.turnoDeGuardiaService = turnoDeGuardiaService;
        this.turnoDeGuardiaRepository = turnoDeGuardiaRepository;
    }

    /**
     * {@code POST  /turno-de-guardias} : Create a new turnoDeGuardia.
     *
     * @param turnoDeGuardiaDTO the turnoDeGuardiaDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new turnoDeGuardiaDTO, or with status {@code 400 (Bad Request)} if the turnoDeGuardia has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TurnoDeGuardiaDTO> createTurnoDeGuardia(@Valid @RequestBody TurnoDeGuardiaDTO turnoDeGuardiaDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save TurnoDeGuardia : {}", turnoDeGuardiaDTO);
        if (turnoDeGuardiaDTO.getId() != null) {
            throw new BadRequestAlertException("A new turnoDeGuardia cannot already have an ID", ENTITY_NAME, "idexists");
        }
        turnoDeGuardiaDTO = turnoDeGuardiaService.save(turnoDeGuardiaDTO);
        return ResponseEntity.created(new URI("/api/turno-de-guardias/" + turnoDeGuardiaDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, turnoDeGuardiaDTO.getId().toString()))
            .body(turnoDeGuardiaDTO);
    }

    /**
     * {@code PUT  /turno-de-guardias/:id} : Updates an existing turnoDeGuardia.
     *
     * @param id the id of the turnoDeGuardiaDTO to save.
     * @param turnoDeGuardiaDTO the turnoDeGuardiaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated turnoDeGuardiaDTO,
     * or with status {@code 400 (Bad Request)} if the turnoDeGuardiaDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the turnoDeGuardiaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TurnoDeGuardiaDTO> updateTurnoDeGuardia(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TurnoDeGuardiaDTO turnoDeGuardiaDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TurnoDeGuardia : {}, {}", id, turnoDeGuardiaDTO);
        if (turnoDeGuardiaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, turnoDeGuardiaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!turnoDeGuardiaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        turnoDeGuardiaDTO = turnoDeGuardiaService.update(turnoDeGuardiaDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, turnoDeGuardiaDTO.getId().toString()))
            .body(turnoDeGuardiaDTO);
    }

    /**
     * {@code PATCH  /turno-de-guardias/:id} : Partial updates given fields of an existing turnoDeGuardia, field will ignore if it is null
     *
     * @param id the id of the turnoDeGuardiaDTO to save.
     * @param turnoDeGuardiaDTO the turnoDeGuardiaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated turnoDeGuardiaDTO,
     * or with status {@code 400 (Bad Request)} if the turnoDeGuardiaDTO is not valid,
     * or with status {@code 404 (Not Found)} if the turnoDeGuardiaDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the turnoDeGuardiaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TurnoDeGuardiaDTO> partialUpdateTurnoDeGuardia(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TurnoDeGuardiaDTO turnoDeGuardiaDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TurnoDeGuardia partially : {}, {}", id, turnoDeGuardiaDTO);
        if (turnoDeGuardiaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, turnoDeGuardiaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!turnoDeGuardiaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TurnoDeGuardiaDTO> result = turnoDeGuardiaService.partialUpdate(turnoDeGuardiaDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, turnoDeGuardiaDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /turno-de-guardias} : get all the Turno De Guardias.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Turno De Guardias in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TurnoDeGuardiaDTO>> getAllTurnoDeGuardias(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of TurnoDeGuardias");
        Page<TurnoDeGuardiaDTO> page;
        if (eagerload) {
            page = turnoDeGuardiaService.findAllWithEagerRelationships(pageable);
        } else {
            page = turnoDeGuardiaService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /turno-de-guardias/:id} : get the "id" turnoDeGuardia.
     *
     * @param id the id of the turnoDeGuardiaDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the turnoDeGuardiaDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TurnoDeGuardiaDTO> getTurnoDeGuardia(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TurnoDeGuardia : {}", id);
        Optional<TurnoDeGuardiaDTO> turnoDeGuardiaDTO = turnoDeGuardiaService.findOne(id);
        return ResponseUtil.wrapOrNotFound(turnoDeGuardiaDTO);
    }

    /**
     * {@code DELETE  /turno-de-guardias/:id} : delete the "id" turnoDeGuardia.
     *
     * @param id the id of the turnoDeGuardiaDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTurnoDeGuardia(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TurnoDeGuardia : {}", id);
        turnoDeGuardiaService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
