package ar.edu.um.isa.oncall.web.rest;

import ar.edu.um.isa.oncall.repository.ObjetivoDeServicioRepository;
import ar.edu.um.isa.oncall.service.ObjetivoDeServicioService;
import ar.edu.um.isa.oncall.service.dto.ObjetivoDeServicioDTO;
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
 * REST controller for managing {@link ar.edu.um.isa.oncall.domain.ObjetivoDeServicio}.
 */
@RestController
@RequestMapping("/api/objetivo-de-servicios")
public class ObjetivoDeServicioResource {

    private static final Logger LOG = LoggerFactory.getLogger(ObjetivoDeServicioResource.class);

    private static final String ENTITY_NAME = "objetivoDeServicio";

    @Value("${jhipster.clientApp.name:oncall}")
    private String applicationName;

    private final ObjetivoDeServicioService objetivoDeServicioService;

    private final ObjetivoDeServicioRepository objetivoDeServicioRepository;

    public ObjetivoDeServicioResource(
        ObjetivoDeServicioService objetivoDeServicioService,
        ObjetivoDeServicioRepository objetivoDeServicioRepository
    ) {
        this.objetivoDeServicioService = objetivoDeServicioService;
        this.objetivoDeServicioRepository = objetivoDeServicioRepository;
    }

    /**
     * {@code POST  /objetivo-de-servicios} : Create a new objetivoDeServicio.
     *
     * @param objetivoDeServicioDTO the objetivoDeServicioDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new objetivoDeServicioDTO, or with status {@code 400 (Bad Request)} if the objetivoDeServicio has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ObjetivoDeServicioDTO> createObjetivoDeServicio(@Valid @RequestBody ObjetivoDeServicioDTO objetivoDeServicioDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ObjetivoDeServicio : {}", objetivoDeServicioDTO);
        if (objetivoDeServicioDTO.getId() != null) {
            throw new BadRequestAlertException("A new objetivoDeServicio cannot already have an ID", ENTITY_NAME, "idexists");
        }
        objetivoDeServicioDTO = objetivoDeServicioService.save(objetivoDeServicioDTO);
        return ResponseEntity.created(new URI("/api/objetivo-de-servicios/" + objetivoDeServicioDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, objetivoDeServicioDTO.getId().toString()))
            .body(objetivoDeServicioDTO);
    }

    /**
     * {@code PUT  /objetivo-de-servicios/:id} : Updates an existing objetivoDeServicio.
     *
     * @param id the id of the objetivoDeServicioDTO to save.
     * @param objetivoDeServicioDTO the objetivoDeServicioDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated objetivoDeServicioDTO,
     * or with status {@code 400 (Bad Request)} if the objetivoDeServicioDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the objetivoDeServicioDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ObjetivoDeServicioDTO> updateObjetivoDeServicio(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ObjetivoDeServicioDTO objetivoDeServicioDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ObjetivoDeServicio : {}, {}", id, objetivoDeServicioDTO);
        if (objetivoDeServicioDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, objetivoDeServicioDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!objetivoDeServicioRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        objetivoDeServicioDTO = objetivoDeServicioService.update(objetivoDeServicioDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, objetivoDeServicioDTO.getId().toString()))
            .body(objetivoDeServicioDTO);
    }

    /**
     * {@code PATCH  /objetivo-de-servicios/:id} : Partial updates given fields of an existing objetivoDeServicio, field will ignore if it is null
     *
     * @param id the id of the objetivoDeServicioDTO to save.
     * @param objetivoDeServicioDTO the objetivoDeServicioDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated objetivoDeServicioDTO,
     * or with status {@code 400 (Bad Request)} if the objetivoDeServicioDTO is not valid,
     * or with status {@code 404 (Not Found)} if the objetivoDeServicioDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the objetivoDeServicioDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ObjetivoDeServicioDTO> partialUpdateObjetivoDeServicio(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ObjetivoDeServicioDTO objetivoDeServicioDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ObjetivoDeServicio partially : {}, {}", id, objetivoDeServicioDTO);
        if (objetivoDeServicioDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, objetivoDeServicioDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!objetivoDeServicioRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ObjetivoDeServicioDTO> result = objetivoDeServicioService.partialUpdate(objetivoDeServicioDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, objetivoDeServicioDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /objetivo-de-servicios} : get all the Objetivo De Servicios.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Objetivo De Servicios in body.
     */
    @GetMapping("")
    public List<ObjetivoDeServicioDTO> getAllObjetivoDeServicios(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all ObjetivoDeServicios");
        return objetivoDeServicioService.findAll();
    }

    /**
     * {@code GET  /objetivo-de-servicios/:id} : get the "id" objetivoDeServicio.
     *
     * @param id the id of the objetivoDeServicioDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the objetivoDeServicioDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ObjetivoDeServicioDTO> getObjetivoDeServicio(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ObjetivoDeServicio : {}", id);
        Optional<ObjetivoDeServicioDTO> objetivoDeServicioDTO = objetivoDeServicioService.findOne(id);
        return ResponseUtil.wrapOrNotFound(objetivoDeServicioDTO);
    }

    /**
     * {@code DELETE  /objetivo-de-servicios/:id} : delete the "id" objetivoDeServicio.
     *
     * @param id the id of the objetivoDeServicioDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteObjetivoDeServicio(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ObjetivoDeServicio : {}", id);
        objetivoDeServicioService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
