package ar.edu.um.isa.oncall.web.rest;

import static ar.edu.um.isa.oncall.domain.EventoDeIncidenteAsserts.*;
import static ar.edu.um.isa.oncall.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ar.edu.um.isa.oncall.IntegrationTest;
import ar.edu.um.isa.oncall.domain.EventoDeIncidente;
import ar.edu.um.isa.oncall.domain.Incidente;
import ar.edu.um.isa.oncall.domain.enumeration.TipoEvento;
import ar.edu.um.isa.oncall.repository.EventoDeIncidenteRepository;
import ar.edu.um.isa.oncall.service.EventoDeIncidenteService;
import ar.edu.um.isa.oncall.service.dto.EventoDeIncidenteDTO;
import ar.edu.um.isa.oncall.service.mapper.EventoDeIncidenteMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link EventoDeIncidenteResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class EventoDeIncidenteResourceIT {

    private static final TipoEvento DEFAULT_TIPO = TipoEvento.CREACION;
    private static final TipoEvento UPDATED_TIPO = TipoEvento.ASIGNACION;

    private static final String DEFAULT_DETALLE = "AAAAAAAAAA";
    private static final String UPDATED_DETALLE = "BBBBBBBBBB";

    private static final Instant DEFAULT_OCURRIDO_EN = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_OCURRIDO_EN = Instant.ofEpochMilli(1701729143509L);

    private static final Boolean DEFAULT_AUTOMATICO = false;
    private static final Boolean UPDATED_AUTOMATICO = true;

    private static final String ENTITY_API_URL = "/api/evento-de-incidentes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EventoDeIncidenteRepository eventoDeIncidenteRepository;

    @Mock
    private EventoDeIncidenteRepository eventoDeIncidenteRepositoryMock;

    @Autowired
    private EventoDeIncidenteMapper eventoDeIncidenteMapper;

    @Mock
    private EventoDeIncidenteService eventoDeIncidenteServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEventoDeIncidenteMockMvc;

    private EventoDeIncidente eventoDeIncidente;

    private EventoDeIncidente insertedEventoDeIncidente;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EventoDeIncidente createEntity(EntityManager em) {
        EventoDeIncidente eventoDeIncidente = new EventoDeIncidente()
            .tipo(DEFAULT_TIPO)
            .detalle(DEFAULT_DETALLE)
            .ocurridoEn(DEFAULT_OCURRIDO_EN)
            .automatico(DEFAULT_AUTOMATICO);
        // Add required entity
        Incidente incidente;
        if (TestUtil.findAll(em, Incidente.class).isEmpty()) {
            incidente = IncidenteResourceIT.createEntity();
            em.persist(incidente);
            em.flush();
        } else {
            incidente = TestUtil.findAll(em, Incidente.class).get(0);
        }
        eventoDeIncidente.setIncidente(incidente);
        return eventoDeIncidente;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EventoDeIncidente createUpdatedEntity(EntityManager em) {
        EventoDeIncidente updatedEventoDeIncidente = new EventoDeIncidente()
            .tipo(UPDATED_TIPO)
            .detalle(UPDATED_DETALLE)
            .ocurridoEn(UPDATED_OCURRIDO_EN)
            .automatico(UPDATED_AUTOMATICO);
        // Add required entity
        Incidente incidente;
        if (TestUtil.findAll(em, Incidente.class).isEmpty()) {
            incidente = IncidenteResourceIT.createUpdatedEntity();
            em.persist(incidente);
            em.flush();
        } else {
            incidente = TestUtil.findAll(em, Incidente.class).get(0);
        }
        updatedEventoDeIncidente.setIncidente(incidente);
        return updatedEventoDeIncidente;
    }

    @BeforeEach
    void initTest() {
        eventoDeIncidente = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedEventoDeIncidente != null) {
            eventoDeIncidenteRepository.delete(insertedEventoDeIncidente);
            insertedEventoDeIncidente = null;
        }
    }

    @Test
    @Transactional
    void createEventoDeIncidente() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the EventoDeIncidente
        EventoDeIncidenteDTO eventoDeIncidenteDTO = eventoDeIncidenteMapper.toDto(eventoDeIncidente);
        var returnedEventoDeIncidenteDTO = om.readValue(
            restEventoDeIncidenteMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventoDeIncidenteDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            EventoDeIncidenteDTO.class
        );

        // Validate the EventoDeIncidente in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedEventoDeIncidente = eventoDeIncidenteMapper.toEntity(returnedEventoDeIncidenteDTO);
        assertEventoDeIncidenteUpdatableFieldsEquals(returnedEventoDeIncidente, getPersistedEventoDeIncidente(returnedEventoDeIncidente));

        insertedEventoDeIncidente = returnedEventoDeIncidente;
    }

    @Test
    @Transactional
    void createEventoDeIncidenteWithExistingId() throws Exception {
        // Create the EventoDeIncidente with an existing ID
        eventoDeIncidente.setId(1L);
        EventoDeIncidenteDTO eventoDeIncidenteDTO = eventoDeIncidenteMapper.toDto(eventoDeIncidente);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEventoDeIncidenteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventoDeIncidenteDTO)))
            .andExpect(status().isBadRequest());

        // Validate the EventoDeIncidente in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTipoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        eventoDeIncidente.setTipo(null);

        // Create the EventoDeIncidente, which fails.
        EventoDeIncidenteDTO eventoDeIncidenteDTO = eventoDeIncidenteMapper.toDto(eventoDeIncidente);

        restEventoDeIncidenteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventoDeIncidenteDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDetalleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        eventoDeIncidente.setDetalle(null);

        // Create the EventoDeIncidente, which fails.
        EventoDeIncidenteDTO eventoDeIncidenteDTO = eventoDeIncidenteMapper.toDto(eventoDeIncidente);

        restEventoDeIncidenteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventoDeIncidenteDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkOcurridoEnIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        eventoDeIncidente.setOcurridoEn(null);

        // Create the EventoDeIncidente, which fails.
        EventoDeIncidenteDTO eventoDeIncidenteDTO = eventoDeIncidenteMapper.toDto(eventoDeIncidente);

        restEventoDeIncidenteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventoDeIncidenteDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAutomaticoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        eventoDeIncidente.setAutomatico(null);

        // Create the EventoDeIncidente, which fails.
        EventoDeIncidenteDTO eventoDeIncidenteDTO = eventoDeIncidenteMapper.toDto(eventoDeIncidente);

        restEventoDeIncidenteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventoDeIncidenteDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllEventoDeIncidentes() throws Exception {
        // Initialize the database
        insertedEventoDeIncidente = eventoDeIncidenteRepository.saveAndFlush(eventoDeIncidente);

        // Get all the eventoDeIncidenteList
        restEventoDeIncidenteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(eventoDeIncidente.getId().intValue())))
            .andExpect(jsonPath("$.[*].tipo").value(hasItem(DEFAULT_TIPO.toString())))
            .andExpect(jsonPath("$.[*].detalle").value(hasItem(DEFAULT_DETALLE)))
            .andExpect(jsonPath("$.[*].ocurridoEn").value(hasItem(DEFAULT_OCURRIDO_EN.toString())))
            .andExpect(jsonPath("$.[*].automatico").value(hasItem(DEFAULT_AUTOMATICO)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEventoDeIncidentesWithEagerRelationshipsIsEnabled() throws Exception {
        when(eventoDeIncidenteServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEventoDeIncidenteMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(eventoDeIncidenteServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEventoDeIncidentesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(eventoDeIncidenteServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEventoDeIncidenteMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(eventoDeIncidenteRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getEventoDeIncidente() throws Exception {
        // Initialize the database
        insertedEventoDeIncidente = eventoDeIncidenteRepository.saveAndFlush(eventoDeIncidente);

        // Get the eventoDeIncidente
        restEventoDeIncidenteMockMvc
            .perform(get(ENTITY_API_URL_ID, eventoDeIncidente.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(eventoDeIncidente.getId().intValue()))
            .andExpect(jsonPath("$.tipo").value(DEFAULT_TIPO.toString()))
            .andExpect(jsonPath("$.detalle").value(DEFAULT_DETALLE))
            .andExpect(jsonPath("$.ocurridoEn").value(DEFAULT_OCURRIDO_EN.toString()))
            .andExpect(jsonPath("$.automatico").value(DEFAULT_AUTOMATICO));
    }

    @Test
    @Transactional
    void getNonExistingEventoDeIncidente() throws Exception {
        // Get the eventoDeIncidente
        restEventoDeIncidenteMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEventoDeIncidente() throws Exception {
        // Initialize the database
        insertedEventoDeIncidente = eventoDeIncidenteRepository.saveAndFlush(eventoDeIncidente);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the eventoDeIncidente
        EventoDeIncidente updatedEventoDeIncidente = eventoDeIncidenteRepository.findById(eventoDeIncidente.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedEventoDeIncidente are not directly saved in db
        em.detach(updatedEventoDeIncidente);
        updatedEventoDeIncidente.tipo(UPDATED_TIPO).detalle(UPDATED_DETALLE).ocurridoEn(UPDATED_OCURRIDO_EN).automatico(UPDATED_AUTOMATICO);
        EventoDeIncidenteDTO eventoDeIncidenteDTO = eventoDeIncidenteMapper.toDto(updatedEventoDeIncidente);

        restEventoDeIncidenteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, eventoDeIncidenteDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(eventoDeIncidenteDTO))
            )
            .andExpect(status().isOk());

        // Validate the EventoDeIncidente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEventoDeIncidenteToMatchAllProperties(updatedEventoDeIncidente);
    }

    @Test
    @Transactional
    void putNonExistingEventoDeIncidente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        eventoDeIncidente.setId(longCount.incrementAndGet());

        // Create the EventoDeIncidente
        EventoDeIncidenteDTO eventoDeIncidenteDTO = eventoDeIncidenteMapper.toDto(eventoDeIncidente);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventoDeIncidenteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, eventoDeIncidenteDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(eventoDeIncidenteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EventoDeIncidente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEventoDeIncidente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        eventoDeIncidente.setId(longCount.incrementAndGet());

        // Create the EventoDeIncidente
        EventoDeIncidenteDTO eventoDeIncidenteDTO = eventoDeIncidenteMapper.toDto(eventoDeIncidente);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventoDeIncidenteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(eventoDeIncidenteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EventoDeIncidente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEventoDeIncidente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        eventoDeIncidente.setId(longCount.incrementAndGet());

        // Create the EventoDeIncidente
        EventoDeIncidenteDTO eventoDeIncidenteDTO = eventoDeIncidenteMapper.toDto(eventoDeIncidente);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventoDeIncidenteMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventoDeIncidenteDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the EventoDeIncidente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEventoDeIncidenteWithPatch() throws Exception {
        // Initialize the database
        insertedEventoDeIncidente = eventoDeIncidenteRepository.saveAndFlush(eventoDeIncidente);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the eventoDeIncidente using partial update
        EventoDeIncidente partialUpdatedEventoDeIncidente = new EventoDeIncidente();
        partialUpdatedEventoDeIncidente.setId(eventoDeIncidente.getId());

        partialUpdatedEventoDeIncidente.tipo(UPDATED_TIPO).detalle(UPDATED_DETALLE);

        restEventoDeIncidenteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEventoDeIncidente.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEventoDeIncidente))
            )
            .andExpect(status().isOk());

        // Validate the EventoDeIncidente in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEventoDeIncidenteUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedEventoDeIncidente, eventoDeIncidente),
            getPersistedEventoDeIncidente(eventoDeIncidente)
        );
    }

    @Test
    @Transactional
    void fullUpdateEventoDeIncidenteWithPatch() throws Exception {
        // Initialize the database
        insertedEventoDeIncidente = eventoDeIncidenteRepository.saveAndFlush(eventoDeIncidente);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the eventoDeIncidente using partial update
        EventoDeIncidente partialUpdatedEventoDeIncidente = new EventoDeIncidente();
        partialUpdatedEventoDeIncidente.setId(eventoDeIncidente.getId());

        partialUpdatedEventoDeIncidente
            .tipo(UPDATED_TIPO)
            .detalle(UPDATED_DETALLE)
            .ocurridoEn(UPDATED_OCURRIDO_EN)
            .automatico(UPDATED_AUTOMATICO);

        restEventoDeIncidenteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEventoDeIncidente.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEventoDeIncidente))
            )
            .andExpect(status().isOk());

        // Validate the EventoDeIncidente in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEventoDeIncidenteUpdatableFieldsEquals(
            partialUpdatedEventoDeIncidente,
            getPersistedEventoDeIncidente(partialUpdatedEventoDeIncidente)
        );
    }

    @Test
    @Transactional
    void patchNonExistingEventoDeIncidente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        eventoDeIncidente.setId(longCount.incrementAndGet());

        // Create the EventoDeIncidente
        EventoDeIncidenteDTO eventoDeIncidenteDTO = eventoDeIncidenteMapper.toDto(eventoDeIncidente);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventoDeIncidenteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, eventoDeIncidenteDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(eventoDeIncidenteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EventoDeIncidente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEventoDeIncidente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        eventoDeIncidente.setId(longCount.incrementAndGet());

        // Create the EventoDeIncidente
        EventoDeIncidenteDTO eventoDeIncidenteDTO = eventoDeIncidenteMapper.toDto(eventoDeIncidente);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventoDeIncidenteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(eventoDeIncidenteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EventoDeIncidente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEventoDeIncidente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        eventoDeIncidente.setId(longCount.incrementAndGet());

        // Create the EventoDeIncidente
        EventoDeIncidenteDTO eventoDeIncidenteDTO = eventoDeIncidenteMapper.toDto(eventoDeIncidente);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventoDeIncidenteMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(eventoDeIncidenteDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the EventoDeIncidente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEventoDeIncidente() throws Exception {
        // Initialize the database
        insertedEventoDeIncidente = eventoDeIncidenteRepository.saveAndFlush(eventoDeIncidente);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the eventoDeIncidente
        restEventoDeIncidenteMockMvc
            .perform(delete(ENTITY_API_URL_ID, eventoDeIncidente.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return eventoDeIncidenteRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected EventoDeIncidente getPersistedEventoDeIncidente(EventoDeIncidente eventoDeIncidente) {
        return eventoDeIncidenteRepository.findById(eventoDeIncidente.getId()).orElseThrow();
    }

    protected void assertPersistedEventoDeIncidenteToMatchAllProperties(EventoDeIncidente expectedEventoDeIncidente) {
        assertEventoDeIncidenteAllPropertiesEquals(expectedEventoDeIncidente, getPersistedEventoDeIncidente(expectedEventoDeIncidente));
    }

    protected void assertPersistedEventoDeIncidenteToMatchUpdatableProperties(EventoDeIncidente expectedEventoDeIncidente) {
        assertEventoDeIncidenteAllUpdatablePropertiesEquals(
            expectedEventoDeIncidente,
            getPersistedEventoDeIncidente(expectedEventoDeIncidente)
        );
    }
}
