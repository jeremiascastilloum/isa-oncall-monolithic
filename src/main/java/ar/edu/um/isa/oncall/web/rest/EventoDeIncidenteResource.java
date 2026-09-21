package ar.edu.um.isa.oncall.web.rest;

import ar.edu.um.isa.oncall.repository.EventoDeIncidenteRepository;
import ar.edu.um.isa.oncall.service.EventoDeIncidenteService;
import ar.edu.um.isa.oncall.service.dto.EventoDeIncidenteDTO;
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
 * REST controller for managing {@link ar.edu.um.isa.oncall.domain.EventoDeIncidente}.
 */
@RestController
@RequestMapping("/api/evento-de-incidentes")
public class EventoDeIncidenteResource {

    private static final Logger LOG = LoggerFactory.getLogger(EventoDeIncidenteResource.class);

    private static final String ENTITY_NAME = "eventoDeIncidente";

    @Value("${jhipster.clientApp.name:oncall}")
    private String applicationName;

    private final EventoDeIncidenteService eventoDeIncidenteService;

    private final EventoDeIncidenteRepository eventoDeIncidenteRepository;

    public EventoDeIncidenteResource(
        EventoDeIncidenteService eventoDeIncidenteService,
        EventoDeIncidenteRepository eventoDeIncidenteRepository
    ) {
        this.eventoDeIncidenteService = eventoDeIncidenteService;
        this.eventoDeIncidenteRepository = eventoDeIncidenteRepository;
    }

    /**
     * {@code POST  /evento-de-incidentes} : Create a new eventoDeIncidente.
     *
     * @param eventoDeIncidenteDTO the eventoDeIncidenteDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new eventoDeIncidenteDTO, or with status {@code 400 (Bad Request)} if the eventoDeIncidente has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<EventoDeIncidenteDTO> createEventoDeIncidente(@Valid @RequestBody EventoDeIncidenteDTO eventoDeIncidenteDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save EventoDeIncidente : {}", eventoDeIncidenteDTO);
        if (eventoDeIncidenteDTO.getId() != null) {
            throw new BadRequestAlertException("A new eventoDeIncidente cannot already have an ID", ENTITY_NAME, "idexists");
        }
        eventoDeIncidenteDTO = eventoDeIncidenteService.save(eventoDeIncidenteDTO);
        return ResponseEntity.created(new URI("/api/evento-de-incidentes/" + eventoDeIncidenteDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, eventoDeIncidenteDTO.getId().toString()))
            .body(eventoDeIncidenteDTO);
    }

    /**
     * {@code PUT  /evento-de-incidentes/:id} : Updates an existing eventoDeIncidente.
     *
     * @param id the id of the eventoDeIncidenteDTO to save.
     * @param eventoDeIncidenteDTO the eventoDeIncidenteDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated eventoDeIncidenteDTO,
     * or with status {@code 400 (Bad Request)} if the eventoDeIncidenteDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the eventoDeIncidenteDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<EventoDeIncidenteDTO> updateEventoDeIncidente(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody EventoDeIncidenteDTO eventoDeIncidenteDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update EventoDeIncidente : {}, {}", id, eventoDeIncidenteDTO);
        if (eventoDeIncidenteDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, eventoDeIncidenteDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!eventoDeIncidenteRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        eventoDeIncidenteDTO = eventoDeIncidenteService.update(eventoDeIncidenteDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, eventoDeIncidenteDTO.getId().toString()))
            .body(eventoDeIncidenteDTO);
    }

    /**
     * {@code PATCH  /evento-de-incidentes/:id} : Partial updates given fields of an existing eventoDeIncidente, field will ignore if it is null
     *
     * @param id the id of the eventoDeIncidenteDTO to save.
     * @param eventoDeIncidenteDTO the eventoDeIncidenteDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated eventoDeIncidenteDTO,
     * or with status {@code 400 (Bad Request)} if the eventoDeIncidenteDTO is not valid,
     * or with status {@code 404 (Not Found)} if the eventoDeIncidenteDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the eventoDeIncidenteDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<EventoDeIncidenteDTO> partialUpdateEventoDeIncidente(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody EventoDeIncidenteDTO eventoDeIncidenteDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update EventoDeIncidente partially : {}, {}", id, eventoDeIncidenteDTO);
        if (eventoDeIncidenteDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, eventoDeIncidenteDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!eventoDeIncidenteRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<EventoDeIncidenteDTO> result = eventoDeIncidenteService.partialUpdate(eventoDeIncidenteDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, eventoDeIncidenteDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /evento-de-incidentes} : get all the Evento De Incidentes.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Evento De Incidentes in body.
     */
    @GetMapping("")
    public ResponseEntity<List<EventoDeIncidenteDTO>> getAllEventoDeIncidentes(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of EventoDeIncidentes");
        Page<EventoDeIncidenteDTO> page;
        if (eagerload) {
            page = eventoDeIncidenteService.findAllWithEagerRelationships(pageable);
        } else {
            page = eventoDeIncidenteService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /evento-de-incidentes/:id} : get the "id" eventoDeIncidente.
     *
     * @param id the id of the eventoDeIncidenteDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the eventoDeIncidenteDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EventoDeIncidenteDTO> getEventoDeIncidente(@PathVariable("id") Long id) {
        LOG.debug("REST request to get EventoDeIncidente : {}", id);
        Optional<EventoDeIncidenteDTO> eventoDeIncidenteDTO = eventoDeIncidenteService.findOne(id);
        return ResponseUtil.wrapOrNotFound(eventoDeIncidenteDTO);
    }

    /**
     * {@code DELETE  /evento-de-incidentes/:id} : delete the "id" eventoDeIncidente.
     *
     * @param id the id of the eventoDeIncidenteDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEventoDeIncidente(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete EventoDeIncidente : {}", id);
        eventoDeIncidenteService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
