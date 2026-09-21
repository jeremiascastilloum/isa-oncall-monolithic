package ar.edu.um.isa.oncall.web.rest;

import ar.edu.um.isa.oncall.repository.ServicioRepository;
import ar.edu.um.isa.oncall.service.ServicioQueryService;
import ar.edu.um.isa.oncall.service.ServicioService;
import ar.edu.um.isa.oncall.service.criteria.ServicioCriteria;
import ar.edu.um.isa.oncall.service.dto.ServicioDTO;
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
 * REST controller for managing {@link ar.edu.um.isa.oncall.domain.Servicio}.
 */
@RestController
@RequestMapping("/api/servicios")
public class ServicioResource {

    private static final Logger LOG = LoggerFactory.getLogger(ServicioResource.class);

    private static final String ENTITY_NAME = "servicio";

    @Value("${jhipster.clientApp.name:oncall}")
    private String applicationName;

    private final ServicioService servicioService;

    private final ServicioRepository servicioRepository;

    private final ServicioQueryService servicioQueryService;

    public ServicioResource(
        ServicioService servicioService,
        ServicioRepository servicioRepository,
        ServicioQueryService servicioQueryService
    ) {
        this.servicioService = servicioService;
        this.servicioRepository = servicioRepository;
        this.servicioQueryService = servicioQueryService;
    }

    /**
     * {@code POST  /servicios} : Create a new servicio.
     *
     * @param servicioDTO the servicioDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new servicioDTO, or with status {@code 400 (Bad Request)} if the servicio has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ServicioDTO> createServicio(@Valid @RequestBody ServicioDTO servicioDTO) throws URISyntaxException {
        LOG.debug("REST request to save Servicio : {}", servicioDTO);
        if (servicioDTO.getId() != null) {
            throw new BadRequestAlertException("A new servicio cannot already have an ID", ENTITY_NAME, "idexists");
        }
        servicioDTO = servicioService.save(servicioDTO);
        return ResponseEntity.created(new URI("/api/servicios/" + servicioDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, servicioDTO.getId().toString()))
            .body(servicioDTO);
    }

    /**
     * {@code PUT  /servicios/:id} : Updates an existing servicio.
     *
     * @param id the id of the servicioDTO to save.
     * @param servicioDTO the servicioDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated servicioDTO,
     * or with status {@code 400 (Bad Request)} if the servicioDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the servicioDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ServicioDTO> updateServicio(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ServicioDTO servicioDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Servicio : {}, {}", id, servicioDTO);
        if (servicioDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, servicioDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!servicioRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        servicioDTO = servicioService.update(servicioDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, servicioDTO.getId().toString()))
            .body(servicioDTO);
    }

    /**
     * {@code PATCH  /servicios/:id} : Partial updates given fields of an existing servicio, field will ignore if it is null
     *
     * @param id the id of the servicioDTO to save.
     * @param servicioDTO the servicioDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated servicioDTO,
     * or with status {@code 400 (Bad Request)} if the servicioDTO is not valid,
     * or with status {@code 404 (Not Found)} if the servicioDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the servicioDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ServicioDTO> partialUpdateServicio(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ServicioDTO servicioDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Servicio partially : {}, {}", id, servicioDTO);
        if (servicioDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, servicioDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!servicioRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ServicioDTO> result = servicioService.partialUpdate(servicioDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, servicioDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /servicios} : get all the Servicios.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Servicios in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ServicioDTO>> getAllServicios(ServicioCriteria criteria) {
        LOG.debug("REST request to get Servicios by criteria: {}", criteria);

        List<ServicioDTO> entityList = servicioQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /servicios/count} : count all the servicios.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countServicios(ServicioCriteria criteria) {
        LOG.debug("REST request to count Servicios by criteria: {}", criteria);
        return ResponseEntity.ok().body(servicioQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /servicios/:id} : get the "id" servicio.
     *
     * @param id the id of the servicioDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the servicioDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ServicioDTO> getServicio(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Servicio : {}", id);
        Optional<ServicioDTO> servicioDTO = servicioService.findOne(id);
        return ResponseUtil.wrapOrNotFound(servicioDTO);
    }

    /**
     * {@code DELETE  /servicios/:id} : delete the "id" servicio.
     *
     * @param id the id of the servicioDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServicio(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Servicio : {}", id);
        servicioService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
