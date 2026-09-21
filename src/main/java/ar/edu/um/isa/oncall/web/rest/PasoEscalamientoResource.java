package ar.edu.um.isa.oncall.web.rest;

import ar.edu.um.isa.oncall.repository.PasoEscalamientoRepository;
import ar.edu.um.isa.oncall.service.PasoEscalamientoService;
import ar.edu.um.isa.oncall.service.dto.PasoEscalamientoDTO;
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
 * REST controller for managing {@link ar.edu.um.isa.oncall.domain.PasoEscalamiento}.
 */
@RestController
@RequestMapping("/api/paso-escalamientos")
public class PasoEscalamientoResource {

    private static final Logger LOG = LoggerFactory.getLogger(PasoEscalamientoResource.class);

    private static final String ENTITY_NAME = "pasoEscalamiento";

    @Value("${jhipster.clientApp.name:oncall}")
    private String applicationName;

    private final PasoEscalamientoService pasoEscalamientoService;

    private final PasoEscalamientoRepository pasoEscalamientoRepository;

    public PasoEscalamientoResource(
        PasoEscalamientoService pasoEscalamientoService,
        PasoEscalamientoRepository pasoEscalamientoRepository
    ) {
        this.pasoEscalamientoService = pasoEscalamientoService;
        this.pasoEscalamientoRepository = pasoEscalamientoRepository;
    }

    /**
     * {@code POST  /paso-escalamientos} : Create a new pasoEscalamiento.
     *
     * @param pasoEscalamientoDTO the pasoEscalamientoDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pasoEscalamientoDTO, or with status {@code 400 (Bad Request)} if the pasoEscalamiento has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PasoEscalamientoDTO> createPasoEscalamiento(@Valid @RequestBody PasoEscalamientoDTO pasoEscalamientoDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save PasoEscalamiento : {}", pasoEscalamientoDTO);
        if (pasoEscalamientoDTO.getId() != null) {
            throw new BadRequestAlertException("A new pasoEscalamiento cannot already have an ID", ENTITY_NAME, "idexists");
        }
        pasoEscalamientoDTO = pasoEscalamientoService.save(pasoEscalamientoDTO);
        return ResponseEntity.created(new URI("/api/paso-escalamientos/" + pasoEscalamientoDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, pasoEscalamientoDTO.getId().toString()))
            .body(pasoEscalamientoDTO);
    }

    /**
     * {@code PUT  /paso-escalamientos/:id} : Updates an existing pasoEscalamiento.
     *
     * @param id the id of the pasoEscalamientoDTO to save.
     * @param pasoEscalamientoDTO the pasoEscalamientoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pasoEscalamientoDTO,
     * or with status {@code 400 (Bad Request)} if the pasoEscalamientoDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the pasoEscalamientoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PasoEscalamientoDTO> updatePasoEscalamiento(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PasoEscalamientoDTO pasoEscalamientoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PasoEscalamiento : {}, {}", id, pasoEscalamientoDTO);
        if (pasoEscalamientoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pasoEscalamientoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pasoEscalamientoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        pasoEscalamientoDTO = pasoEscalamientoService.update(pasoEscalamientoDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, pasoEscalamientoDTO.getId().toString()))
            .body(pasoEscalamientoDTO);
    }

    /**
     * {@code PATCH  /paso-escalamientos/:id} : Partial updates given fields of an existing pasoEscalamiento, field will ignore if it is null
     *
     * @param id the id of the pasoEscalamientoDTO to save.
     * @param pasoEscalamientoDTO the pasoEscalamientoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pasoEscalamientoDTO,
     * or with status {@code 400 (Bad Request)} if the pasoEscalamientoDTO is not valid,
     * or with status {@code 404 (Not Found)} if the pasoEscalamientoDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the pasoEscalamientoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PasoEscalamientoDTO> partialUpdatePasoEscalamiento(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PasoEscalamientoDTO pasoEscalamientoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PasoEscalamiento partially : {}, {}", id, pasoEscalamientoDTO);
        if (pasoEscalamientoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pasoEscalamientoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pasoEscalamientoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PasoEscalamientoDTO> result = pasoEscalamientoService.partialUpdate(pasoEscalamientoDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, pasoEscalamientoDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /paso-escalamientos} : get all the Paso Escalamientos.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Paso Escalamientos in body.
     */
    @GetMapping("")
    public List<PasoEscalamientoDTO> getAllPasoEscalamientos(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all PasoEscalamientos");
        return pasoEscalamientoService.findAll();
    }

    /**
     * {@code GET  /paso-escalamientos/:id} : get the "id" pasoEscalamiento.
     *
     * @param id the id of the pasoEscalamientoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pasoEscalamientoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PasoEscalamientoDTO> getPasoEscalamiento(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PasoEscalamiento : {}", id);
        Optional<PasoEscalamientoDTO> pasoEscalamientoDTO = pasoEscalamientoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(pasoEscalamientoDTO);
    }

    /**
     * {@code DELETE  /paso-escalamientos/:id} : delete the "id" pasoEscalamiento.
     *
     * @param id the id of the pasoEscalamientoDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePasoEscalamiento(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PasoEscalamiento : {}", id);
        pasoEscalamientoService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
