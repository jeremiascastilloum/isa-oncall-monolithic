package ar.edu.um.isa.oncall.web.rest;

import ar.edu.um.isa.oncall.repository.NotificacionRepository;
import ar.edu.um.isa.oncall.service.NotificacionQueryService;
import ar.edu.um.isa.oncall.service.NotificacionService;
import ar.edu.um.isa.oncall.service.criteria.NotificacionCriteria;
import ar.edu.um.isa.oncall.service.dto.NotificacionDTO;
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
 * REST controller for managing {@link ar.edu.um.isa.oncall.domain.Notificacion}.
 */
@RestController
@RequestMapping("/api/notificacions")
public class NotificacionResource {

    private static final Logger LOG = LoggerFactory.getLogger(NotificacionResource.class);

    private static final String ENTITY_NAME = "notificacion";

    @Value("${jhipster.clientApp.name:oncall}")
    private String applicationName;

    private final NotificacionService notificacionService;

    private final NotificacionRepository notificacionRepository;

    private final NotificacionQueryService notificacionQueryService;

    public NotificacionResource(
        NotificacionService notificacionService,
        NotificacionRepository notificacionRepository,
        NotificacionQueryService notificacionQueryService
    ) {
        this.notificacionService = notificacionService;
        this.notificacionRepository = notificacionRepository;
        this.notificacionQueryService = notificacionQueryService;
    }

    /**
     * {@code POST  /notificacions} : Create a new notificacion.
     *
     * @param notificacionDTO the notificacionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new notificacionDTO, or with status {@code 400 (Bad Request)} if the notificacion has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<NotificacionDTO> createNotificacion(@Valid @RequestBody NotificacionDTO notificacionDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save Notificacion : {}", notificacionDTO);
        if (notificacionDTO.getId() != null) {
            throw new BadRequestAlertException("A new notificacion cannot already have an ID", ENTITY_NAME, "idexists");
        }
        notificacionDTO = notificacionService.save(notificacionDTO);
        return ResponseEntity.created(new URI("/api/notificacions/" + notificacionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, notificacionDTO.getId().toString()))
            .body(notificacionDTO);
    }

    /**
     * {@code PUT  /notificacions/:id} : Updates an existing notificacion.
     *
     * @param id the id of the notificacionDTO to save.
     * @param notificacionDTO the notificacionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notificacionDTO,
     * or with status {@code 400 (Bad Request)} if the notificacionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the notificacionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<NotificacionDTO> updateNotificacion(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody NotificacionDTO notificacionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Notificacion : {}, {}", id, notificacionDTO);
        if (notificacionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, notificacionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!notificacionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        notificacionDTO = notificacionService.update(notificacionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, notificacionDTO.getId().toString()))
            .body(notificacionDTO);
    }

    /**
     * {@code PATCH  /notificacions/:id} : Partial updates given fields of an existing notificacion, field will ignore if it is null
     *
     * @param id the id of the notificacionDTO to save.
     * @param notificacionDTO the notificacionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notificacionDTO,
     * or with status {@code 400 (Bad Request)} if the notificacionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the notificacionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the notificacionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<NotificacionDTO> partialUpdateNotificacion(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody NotificacionDTO notificacionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Notificacion partially : {}, {}", id, notificacionDTO);
        if (notificacionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, notificacionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!notificacionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<NotificacionDTO> result = notificacionService.partialUpdate(notificacionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, notificacionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /notificacions} : get all the Notificacions.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Notificacions in body.
     */
    @GetMapping("")
    public ResponseEntity<List<NotificacionDTO>> getAllNotificacions(
        NotificacionCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Notificacions by criteria: {}", criteria);

        Page<NotificacionDTO> page = notificacionQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /notificacions/count} : count all the notificacions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countNotificacions(NotificacionCriteria criteria) {
        LOG.debug("REST request to count Notificacions by criteria: {}", criteria);
        return ResponseEntity.ok().body(notificacionQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /notificacions/:id} : get the "id" notificacion.
     *
     * @param id the id of the notificacionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the notificacionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<NotificacionDTO> getNotificacion(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Notificacion : {}", id);
        Optional<NotificacionDTO> notificacionDTO = notificacionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(notificacionDTO);
    }

    /**
     * {@code DELETE  /notificacions/:id} : delete the "id" notificacion.
     *
     * @param id the id of the notificacionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotificacion(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Notificacion : {}", id);
        notificacionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
