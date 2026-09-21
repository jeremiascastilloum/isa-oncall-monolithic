package ar.edu.um.isa.oncall.web.rest;

import ar.edu.um.isa.oncall.repository.IncidenteRepository;
import ar.edu.um.isa.oncall.service.IncidenteQueryService;
import ar.edu.um.isa.oncall.service.IncidenteService;
import ar.edu.um.isa.oncall.service.criteria.IncidenteCriteria;
import ar.edu.um.isa.oncall.service.dto.IncidenteDTO;
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
 * REST controller for managing {@link ar.edu.um.isa.oncall.domain.Incidente}.
 */
@RestController
@RequestMapping("/api/incidentes")
public class IncidenteResource {

    private static final Logger LOG = LoggerFactory.getLogger(IncidenteResource.class);

    private static final String ENTITY_NAME = "incidente";

    @Value("${jhipster.clientApp.name:oncall}")
    private String applicationName;

    private final IncidenteService incidenteService;

    private final IncidenteRepository incidenteRepository;

    private final IncidenteQueryService incidenteQueryService;

    public IncidenteResource(
        IncidenteService incidenteService,
        IncidenteRepository incidenteRepository,
        IncidenteQueryService incidenteQueryService
    ) {
        this.incidenteService = incidenteService;
        this.incidenteRepository = incidenteRepository;
        this.incidenteQueryService = incidenteQueryService;
    }

    /**
     * {@code POST  /incidentes} : Create a new incidente.
     *
     * @param incidenteDTO the incidenteDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new incidenteDTO, or with status {@code 400 (Bad Request)} if the incidente has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<IncidenteDTO> createIncidente(@Valid @RequestBody IncidenteDTO incidenteDTO) throws URISyntaxException {
        LOG.debug("REST request to save Incidente : {}", incidenteDTO);
        if (incidenteDTO.getId() != null) {
            throw new BadRequestAlertException("A new incidente cannot already have an ID", ENTITY_NAME, "idexists");
        }
        incidenteDTO = incidenteService.save(incidenteDTO);
        return ResponseEntity.created(new URI("/api/incidentes/" + incidenteDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, incidenteDTO.getId().toString()))
            .body(incidenteDTO);
    }

    /**
     * {@code PUT  /incidentes/:id} : Updates an existing incidente.
     *
     * @param id the id of the incidenteDTO to save.
     * @param incidenteDTO the incidenteDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated incidenteDTO,
     * or with status {@code 400 (Bad Request)} if the incidenteDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the incidenteDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<IncidenteDTO> updateIncidente(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody IncidenteDTO incidenteDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Incidente : {}, {}", id, incidenteDTO);
        if (incidenteDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, incidenteDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!incidenteRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        incidenteDTO = incidenteService.update(incidenteDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, incidenteDTO.getId().toString()))
            .body(incidenteDTO);
    }

    /**
     * {@code PATCH  /incidentes/:id} : Partial updates given fields of an existing incidente, field will ignore if it is null
     *
     * @param id the id of the incidenteDTO to save.
     * @param incidenteDTO the incidenteDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated incidenteDTO,
     * or with status {@code 400 (Bad Request)} if the incidenteDTO is not valid,
     * or with status {@code 404 (Not Found)} if the incidenteDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the incidenteDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<IncidenteDTO> partialUpdateIncidente(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody IncidenteDTO incidenteDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Incidente partially : {}, {}", id, incidenteDTO);
        if (incidenteDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, incidenteDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!incidenteRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<IncidenteDTO> result = incidenteService.partialUpdate(incidenteDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, incidenteDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /incidentes} : get all the Incidentes.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Incidentes in body.
     */
    @GetMapping("")
    public ResponseEntity<List<IncidenteDTO>> getAllIncidentes(
        IncidenteCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Incidentes by criteria: {}", criteria);

        Page<IncidenteDTO> page = incidenteQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /incidentes/count} : count all the incidentes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countIncidentes(IncidenteCriteria criteria) {
        LOG.debug("REST request to count Incidentes by criteria: {}", criteria);
        return ResponseEntity.ok().body(incidenteQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /incidentes/:id} : get the "id" incidente.
     *
     * @param id the id of the incidenteDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the incidenteDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<IncidenteDTO> getIncidente(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Incidente : {}", id);
        Optional<IncidenteDTO> incidenteDTO = incidenteService.findOne(id);
        return ResponseUtil.wrapOrNotFound(incidenteDTO);
    }

    /**
     * {@code DELETE  /incidentes/:id} : delete the "id" incidente.
     *
     * @param id the id of the incidenteDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncidente(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Incidente : {}", id);
        incidenteService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
