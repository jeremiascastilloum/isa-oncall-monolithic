package ar.edu.um.isa.oncall.web.rest;

import ar.edu.um.isa.oncall.repository.AccionCorrectivaRepository;
import ar.edu.um.isa.oncall.service.AccionCorrectivaService;
import ar.edu.um.isa.oncall.service.dto.AccionCorrectivaDTO;
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
 * REST controller for managing {@link ar.edu.um.isa.oncall.domain.AccionCorrectiva}.
 */
@RestController
@RequestMapping("/api/accion-correctivas")
public class AccionCorrectivaResource {

    private static final Logger LOG = LoggerFactory.getLogger(AccionCorrectivaResource.class);

    private static final String ENTITY_NAME = "accionCorrectiva";

    @Value("${jhipster.clientApp.name:oncall}")
    private String applicationName;

    private final AccionCorrectivaService accionCorrectivaService;

    private final AccionCorrectivaRepository accionCorrectivaRepository;

    public AccionCorrectivaResource(
        AccionCorrectivaService accionCorrectivaService,
        AccionCorrectivaRepository accionCorrectivaRepository
    ) {
        this.accionCorrectivaService = accionCorrectivaService;
        this.accionCorrectivaRepository = accionCorrectivaRepository;
    }

    /**
     * {@code POST  /accion-correctivas} : Create a new accionCorrectiva.
     *
     * @param accionCorrectivaDTO the accionCorrectivaDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new accionCorrectivaDTO, or with status {@code 400 (Bad Request)} if the accionCorrectiva has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AccionCorrectivaDTO> createAccionCorrectiva(@Valid @RequestBody AccionCorrectivaDTO accionCorrectivaDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save AccionCorrectiva : {}", accionCorrectivaDTO);
        if (accionCorrectivaDTO.getId() != null) {
            throw new BadRequestAlertException("A new accionCorrectiva cannot already have an ID", ENTITY_NAME, "idexists");
        }
        accionCorrectivaDTO = accionCorrectivaService.save(accionCorrectivaDTO);
        return ResponseEntity.created(new URI("/api/accion-correctivas/" + accionCorrectivaDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, accionCorrectivaDTO.getId().toString()))
            .body(accionCorrectivaDTO);
    }

    /**
     * {@code PUT  /accion-correctivas/:id} : Updates an existing accionCorrectiva.
     *
     * @param id the id of the accionCorrectivaDTO to save.
     * @param accionCorrectivaDTO the accionCorrectivaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated accionCorrectivaDTO,
     * or with status {@code 400 (Bad Request)} if the accionCorrectivaDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the accionCorrectivaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AccionCorrectivaDTO> updateAccionCorrectiva(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AccionCorrectivaDTO accionCorrectivaDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AccionCorrectiva : {}, {}", id, accionCorrectivaDTO);
        if (accionCorrectivaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, accionCorrectivaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!accionCorrectivaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        accionCorrectivaDTO = accionCorrectivaService.update(accionCorrectivaDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, accionCorrectivaDTO.getId().toString()))
            .body(accionCorrectivaDTO);
    }

    /**
     * {@code PATCH  /accion-correctivas/:id} : Partial updates given fields of an existing accionCorrectiva, field will ignore if it is null
     *
     * @param id the id of the accionCorrectivaDTO to save.
     * @param accionCorrectivaDTO the accionCorrectivaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated accionCorrectivaDTO,
     * or with status {@code 400 (Bad Request)} if the accionCorrectivaDTO is not valid,
     * or with status {@code 404 (Not Found)} if the accionCorrectivaDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the accionCorrectivaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AccionCorrectivaDTO> partialUpdateAccionCorrectiva(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AccionCorrectivaDTO accionCorrectivaDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update AccionCorrectiva partially : {}, {}", id, accionCorrectivaDTO);
        if (accionCorrectivaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, accionCorrectivaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!accionCorrectivaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AccionCorrectivaDTO> result = accionCorrectivaService.partialUpdate(accionCorrectivaDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, accionCorrectivaDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /accion-correctivas} : get all the Accion Correctivas.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Accion Correctivas in body.
     */
    @GetMapping("")
    public List<AccionCorrectivaDTO> getAllAccionCorrectivas(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all AccionCorrectivas");
        return accionCorrectivaService.findAll();
    }

    /**
     * {@code GET  /accion-correctivas/:id} : get the "id" accionCorrectiva.
     *
     * @param id the id of the accionCorrectivaDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the accionCorrectivaDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AccionCorrectivaDTO> getAccionCorrectiva(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AccionCorrectiva : {}", id);
        Optional<AccionCorrectivaDTO> accionCorrectivaDTO = accionCorrectivaService.findOne(id);
        return ResponseUtil.wrapOrNotFound(accionCorrectivaDTO);
    }

    /**
     * {@code DELETE  /accion-correctivas/:id} : delete the "id" accionCorrectiva.
     *
     * @param id the id of the accionCorrectivaDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccionCorrectiva(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AccionCorrectiva : {}", id);
        accionCorrectivaService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
