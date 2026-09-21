package ar.edu.um.isa.oncall.web.rest;

import ar.edu.um.isa.oncall.repository.PoliticaEscalamientoRepository;
import ar.edu.um.isa.oncall.service.PoliticaEscalamientoService;
import ar.edu.um.isa.oncall.service.dto.PoliticaEscalamientoDTO;
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
 * REST controller for managing {@link ar.edu.um.isa.oncall.domain.PoliticaEscalamiento}.
 */
@RestController
@RequestMapping("/api/politica-escalamientos")
public class PoliticaEscalamientoResource {

    private static final Logger LOG = LoggerFactory.getLogger(PoliticaEscalamientoResource.class);

    private static final String ENTITY_NAME = "politicaEscalamiento";

    @Value("${jhipster.clientApp.name:oncall}")
    private String applicationName;

    private final PoliticaEscalamientoService politicaEscalamientoService;

    private final PoliticaEscalamientoRepository politicaEscalamientoRepository;

    public PoliticaEscalamientoResource(
        PoliticaEscalamientoService politicaEscalamientoService,
        PoliticaEscalamientoRepository politicaEscalamientoRepository
    ) {
        this.politicaEscalamientoService = politicaEscalamientoService;
        this.politicaEscalamientoRepository = politicaEscalamientoRepository;
    }

    /**
     * {@code POST  /politica-escalamientos} : Create a new politicaEscalamiento.
     *
     * @param politicaEscalamientoDTO the politicaEscalamientoDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new politicaEscalamientoDTO, or with status {@code 400 (Bad Request)} if the politicaEscalamiento has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PoliticaEscalamientoDTO> createPoliticaEscalamiento(
        @Valid @RequestBody PoliticaEscalamientoDTO politicaEscalamientoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save PoliticaEscalamiento : {}", politicaEscalamientoDTO);
        if (politicaEscalamientoDTO.getId() != null) {
            throw new BadRequestAlertException("A new politicaEscalamiento cannot already have an ID", ENTITY_NAME, "idexists");
        }
        politicaEscalamientoDTO = politicaEscalamientoService.save(politicaEscalamientoDTO);
        return ResponseEntity.created(new URI("/api/politica-escalamientos/" + politicaEscalamientoDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, politicaEscalamientoDTO.getId().toString()))
            .body(politicaEscalamientoDTO);
    }

    /**
     * {@code PUT  /politica-escalamientos/:id} : Updates an existing politicaEscalamiento.
     *
     * @param id the id of the politicaEscalamientoDTO to save.
     * @param politicaEscalamientoDTO the politicaEscalamientoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated politicaEscalamientoDTO,
     * or with status {@code 400 (Bad Request)} if the politicaEscalamientoDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the politicaEscalamientoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PoliticaEscalamientoDTO> updatePoliticaEscalamiento(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PoliticaEscalamientoDTO politicaEscalamientoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PoliticaEscalamiento : {}, {}", id, politicaEscalamientoDTO);
        if (politicaEscalamientoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, politicaEscalamientoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!politicaEscalamientoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        politicaEscalamientoDTO = politicaEscalamientoService.update(politicaEscalamientoDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, politicaEscalamientoDTO.getId().toString()))
            .body(politicaEscalamientoDTO);
    }

    /**
     * {@code PATCH  /politica-escalamientos/:id} : Partial updates given fields of an existing politicaEscalamiento, field will ignore if it is null
     *
     * @param id the id of the politicaEscalamientoDTO to save.
     * @param politicaEscalamientoDTO the politicaEscalamientoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated politicaEscalamientoDTO,
     * or with status {@code 400 (Bad Request)} if the politicaEscalamientoDTO is not valid,
     * or with status {@code 404 (Not Found)} if the politicaEscalamientoDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the politicaEscalamientoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PoliticaEscalamientoDTO> partialUpdatePoliticaEscalamiento(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PoliticaEscalamientoDTO politicaEscalamientoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PoliticaEscalamiento partially : {}, {}", id, politicaEscalamientoDTO);
        if (politicaEscalamientoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, politicaEscalamientoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!politicaEscalamientoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PoliticaEscalamientoDTO> result = politicaEscalamientoService.partialUpdate(politicaEscalamientoDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, politicaEscalamientoDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /politica-escalamientos} : get all the Politica Escalamientos.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Politica Escalamientos in body.
     */
    @GetMapping("")
    public List<PoliticaEscalamientoDTO> getAllPoliticaEscalamientos(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all PoliticaEscalamientos");
        return politicaEscalamientoService.findAll();
    }

    /**
     * {@code GET  /politica-escalamientos/:id} : get the "id" politicaEscalamiento.
     *
     * @param id the id of the politicaEscalamientoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the politicaEscalamientoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PoliticaEscalamientoDTO> getPoliticaEscalamiento(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PoliticaEscalamiento : {}", id);
        Optional<PoliticaEscalamientoDTO> politicaEscalamientoDTO = politicaEscalamientoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(politicaEscalamientoDTO);
    }

    /**
     * {@code DELETE  /politica-escalamientos/:id} : delete the "id" politicaEscalamiento.
     *
     * @param id the id of the politicaEscalamientoDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePoliticaEscalamiento(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PoliticaEscalamiento : {}", id);
        politicaEscalamientoService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
