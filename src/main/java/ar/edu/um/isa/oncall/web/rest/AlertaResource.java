package ar.edu.um.isa.oncall.web.rest;

import ar.edu.um.isa.oncall.repository.AlertaRepository;
import ar.edu.um.isa.oncall.service.AlertaQueryService;
import ar.edu.um.isa.oncall.service.AlertaService;
import ar.edu.um.isa.oncall.service.criteria.AlertaCriteria;
import ar.edu.um.isa.oncall.service.dto.AlertaDTO;
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
 * REST controller for managing {@link ar.edu.um.isa.oncall.domain.Alerta}.
 */
@RestController
@RequestMapping("/api/alertas")
public class AlertaResource {

    private static final Logger LOG = LoggerFactory.getLogger(AlertaResource.class);

    private static final String ENTITY_NAME = "alerta";

    @Value("${jhipster.clientApp.name:oncall}")
    private String applicationName;

    private final AlertaService alertaService;

    private final AlertaRepository alertaRepository;

    private final AlertaQueryService alertaQueryService;

    public AlertaResource(AlertaService alertaService, AlertaRepository alertaRepository, AlertaQueryService alertaQueryService) {
        this.alertaService = alertaService;
        this.alertaRepository = alertaRepository;
        this.alertaQueryService = alertaQueryService;
    }

    /**
     * {@code POST  /alertas} : Create a new alerta.
     *
     * @param alertaDTO the alertaDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new alertaDTO, or with status {@code 400 (Bad Request)} if the alerta has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AlertaDTO> createAlerta(@Valid @RequestBody AlertaDTO alertaDTO) throws URISyntaxException {
        LOG.debug("REST request to save Alerta : {}", alertaDTO);
        if (alertaDTO.getId() != null) {
            throw new BadRequestAlertException("A new alerta cannot already have an ID", ENTITY_NAME, "idexists");
        }
        alertaDTO = alertaService.save(alertaDTO);
        return ResponseEntity.created(new URI("/api/alertas/" + alertaDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, alertaDTO.getId().toString()))
            .body(alertaDTO);
    }

    /**
     * {@code PUT  /alertas/:id} : Updates an existing alerta.
     *
     * @param id the id of the alertaDTO to save.
     * @param alertaDTO the alertaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated alertaDTO,
     * or with status {@code 400 (Bad Request)} if the alertaDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the alertaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AlertaDTO> updateAlerta(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AlertaDTO alertaDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Alerta : {}, {}", id, alertaDTO);
        if (alertaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, alertaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!alertaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        alertaDTO = alertaService.update(alertaDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, alertaDTO.getId().toString()))
            .body(alertaDTO);
    }

    /**
     * {@code PATCH  /alertas/:id} : Partial updates given fields of an existing alerta, field will ignore if it is null
     *
     * @param id the id of the alertaDTO to save.
     * @param alertaDTO the alertaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated alertaDTO,
     * or with status {@code 400 (Bad Request)} if the alertaDTO is not valid,
     * or with status {@code 404 (Not Found)} if the alertaDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the alertaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AlertaDTO> partialUpdateAlerta(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AlertaDTO alertaDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Alerta partially : {}, {}", id, alertaDTO);
        if (alertaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, alertaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!alertaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AlertaDTO> result = alertaService.partialUpdate(alertaDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, alertaDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /alertas} : get all the Alertas.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Alertas in body.
     */
    @GetMapping("")
    public ResponseEntity<List<AlertaDTO>> getAllAlertas(
        AlertaCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Alertas by criteria: {}", criteria);

        Page<AlertaDTO> page = alertaQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /alertas/count} : count all the alertas.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countAlertas(AlertaCriteria criteria) {
        LOG.debug("REST request to count Alertas by criteria: {}", criteria);
        return ResponseEntity.ok().body(alertaQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /alertas/:id} : get the "id" alerta.
     *
     * @param id the id of the alertaDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the alertaDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AlertaDTO> getAlerta(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Alerta : {}", id);
        Optional<AlertaDTO> alertaDTO = alertaService.findOne(id);
        return ResponseUtil.wrapOrNotFound(alertaDTO);
    }

    /**
     * {@code DELETE  /alertas/:id} : delete the "id" alerta.
     *
     * @param id the id of the alertaDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlerta(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Alerta : {}", id);
        alertaService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
