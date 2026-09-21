package ar.edu.um.isa.oncall.web.rest;

import static ar.edu.um.isa.oncall.domain.PasoEscalamientoAsserts.*;
import static ar.edu.um.isa.oncall.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ar.edu.um.isa.oncall.IntegrationTest;
import ar.edu.um.isa.oncall.domain.PasoEscalamiento;
import ar.edu.um.isa.oncall.domain.PoliticaEscalamiento;
import ar.edu.um.isa.oncall.domain.enumeration.Canal;
import ar.edu.um.isa.oncall.repository.PasoEscalamientoRepository;
import ar.edu.um.isa.oncall.repository.UserRepository;
import ar.edu.um.isa.oncall.service.PasoEscalamientoService;
import ar.edu.um.isa.oncall.service.dto.PasoEscalamientoDTO;
import ar.edu.um.isa.oncall.service.mapper.PasoEscalamientoMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link PasoEscalamientoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PasoEscalamientoResourceIT {

    private static final Integer DEFAULT_ORDEN = 1;
    private static final Integer UPDATED_ORDEN = 2;

    private static final Integer DEFAULT_ESPERA_MINUTOS = 0;
    private static final Integer UPDATED_ESPERA_MINUTOS = 1;

    private static final Canal DEFAULT_CANAL = Canal.EMAIL;
    private static final Canal UPDATED_CANAL = Canal.SMS;

    private static final String ENTITY_API_URL = "/api/paso-escalamientos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PasoEscalamientoRepository pasoEscalamientoRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private PasoEscalamientoRepository pasoEscalamientoRepositoryMock;

    @Autowired
    private PasoEscalamientoMapper pasoEscalamientoMapper;

    @Mock
    private PasoEscalamientoService pasoEscalamientoServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPasoEscalamientoMockMvc;

    private PasoEscalamiento pasoEscalamiento;

    private PasoEscalamiento insertedPasoEscalamiento;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PasoEscalamiento createEntity(EntityManager em) {
        PasoEscalamiento pasoEscalamiento = new PasoEscalamiento()
            .orden(DEFAULT_ORDEN)
            .esperaMinutos(DEFAULT_ESPERA_MINUTOS)
            .canal(DEFAULT_CANAL);
        // Add required entity
        PoliticaEscalamiento politicaEscalamiento;
        if (TestUtil.findAll(em, PoliticaEscalamiento.class).isEmpty()) {
            politicaEscalamiento = PoliticaEscalamientoResourceIT.createEntity(em);
            em.persist(politicaEscalamiento);
            em.flush();
        } else {
            politicaEscalamiento = TestUtil.findAll(em, PoliticaEscalamiento.class).get(0);
        }
        pasoEscalamiento.setPolitica(politicaEscalamiento);
        return pasoEscalamiento;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PasoEscalamiento createUpdatedEntity(EntityManager em) {
        PasoEscalamiento updatedPasoEscalamiento = new PasoEscalamiento()
            .orden(UPDATED_ORDEN)
            .esperaMinutos(UPDATED_ESPERA_MINUTOS)
            .canal(UPDATED_CANAL);
        // Add required entity
        PoliticaEscalamiento politicaEscalamiento;
        if (TestUtil.findAll(em, PoliticaEscalamiento.class).isEmpty()) {
            politicaEscalamiento = PoliticaEscalamientoResourceIT.createUpdatedEntity(em);
            em.persist(politicaEscalamiento);
            em.flush();
        } else {
            politicaEscalamiento = TestUtil.findAll(em, PoliticaEscalamiento.class).get(0);
        }
        updatedPasoEscalamiento.setPolitica(politicaEscalamiento);
        return updatedPasoEscalamiento;
    }

    @BeforeEach
    void initTest() {
        pasoEscalamiento = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedPasoEscalamiento != null) {
            pasoEscalamientoRepository.delete(insertedPasoEscalamiento);
            insertedPasoEscalamiento = null;
        }
    }

    @Test
    @Transactional
    void createPasoEscalamiento() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PasoEscalamiento
        PasoEscalamientoDTO pasoEscalamientoDTO = pasoEscalamientoMapper.toDto(pasoEscalamiento);
        var returnedPasoEscalamientoDTO = om.readValue(
            restPasoEscalamientoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pasoEscalamientoDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PasoEscalamientoDTO.class
        );

        // Validate the PasoEscalamiento in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPasoEscalamiento = pasoEscalamientoMapper.toEntity(returnedPasoEscalamientoDTO);
        assertPasoEscalamientoUpdatableFieldsEquals(returnedPasoEscalamiento, getPersistedPasoEscalamiento(returnedPasoEscalamiento));

        insertedPasoEscalamiento = returnedPasoEscalamiento;
    }

    @Test
    @Transactional
    void createPasoEscalamientoWithExistingId() throws Exception {
        // Create the PasoEscalamiento with an existing ID
        pasoEscalamiento.setId(1L);
        PasoEscalamientoDTO pasoEscalamientoDTO = pasoEscalamientoMapper.toDto(pasoEscalamiento);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPasoEscalamientoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pasoEscalamientoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PasoEscalamiento in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkOrdenIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pasoEscalamiento.setOrden(null);

        // Create the PasoEscalamiento, which fails.
        PasoEscalamientoDTO pasoEscalamientoDTO = pasoEscalamientoMapper.toDto(pasoEscalamiento);

        restPasoEscalamientoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pasoEscalamientoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEsperaMinutosIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pasoEscalamiento.setEsperaMinutos(null);

        // Create the PasoEscalamiento, which fails.
        PasoEscalamientoDTO pasoEscalamientoDTO = pasoEscalamientoMapper.toDto(pasoEscalamiento);

        restPasoEscalamientoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pasoEscalamientoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCanalIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pasoEscalamiento.setCanal(null);

        // Create the PasoEscalamiento, which fails.
        PasoEscalamientoDTO pasoEscalamientoDTO = pasoEscalamientoMapper.toDto(pasoEscalamiento);

        restPasoEscalamientoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pasoEscalamientoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPasoEscalamientos() throws Exception {
        // Initialize the database
        insertedPasoEscalamiento = pasoEscalamientoRepository.saveAndFlush(pasoEscalamiento);

        // Get all the pasoEscalamientoList
        restPasoEscalamientoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pasoEscalamiento.getId().intValue())))
            .andExpect(jsonPath("$.[*].orden").value(hasItem(DEFAULT_ORDEN)))
            .andExpect(jsonPath("$.[*].esperaMinutos").value(hasItem(DEFAULT_ESPERA_MINUTOS)))
            .andExpect(jsonPath("$.[*].canal").value(hasItem(DEFAULT_CANAL.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPasoEscalamientosWithEagerRelationshipsIsEnabled() throws Exception {
        when(pasoEscalamientoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPasoEscalamientoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(pasoEscalamientoServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPasoEscalamientosWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(pasoEscalamientoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPasoEscalamientoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(pasoEscalamientoRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getPasoEscalamiento() throws Exception {
        // Initialize the database
        insertedPasoEscalamiento = pasoEscalamientoRepository.saveAndFlush(pasoEscalamiento);

        // Get the pasoEscalamiento
        restPasoEscalamientoMockMvc
            .perform(get(ENTITY_API_URL_ID, pasoEscalamiento.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(pasoEscalamiento.getId().intValue()))
            .andExpect(jsonPath("$.orden").value(DEFAULT_ORDEN))
            .andExpect(jsonPath("$.esperaMinutos").value(DEFAULT_ESPERA_MINUTOS))
            .andExpect(jsonPath("$.canal").value(DEFAULT_CANAL.toString()));
    }

    @Test
    @Transactional
    void getNonExistingPasoEscalamiento() throws Exception {
        // Get the pasoEscalamiento
        restPasoEscalamientoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPasoEscalamiento() throws Exception {
        // Initialize the database
        insertedPasoEscalamiento = pasoEscalamientoRepository.saveAndFlush(pasoEscalamiento);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pasoEscalamiento
        PasoEscalamiento updatedPasoEscalamiento = pasoEscalamientoRepository.findById(pasoEscalamiento.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPasoEscalamiento are not directly saved in db
        em.detach(updatedPasoEscalamiento);
        updatedPasoEscalamiento.orden(UPDATED_ORDEN).esperaMinutos(UPDATED_ESPERA_MINUTOS).canal(UPDATED_CANAL);
        PasoEscalamientoDTO pasoEscalamientoDTO = pasoEscalamientoMapper.toDto(updatedPasoEscalamiento);

        restPasoEscalamientoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pasoEscalamientoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(pasoEscalamientoDTO))
            )
            .andExpect(status().isOk());

        // Validate the PasoEscalamiento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPasoEscalamientoToMatchAllProperties(updatedPasoEscalamiento);
    }

    @Test
    @Transactional
    void putNonExistingPasoEscalamiento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pasoEscalamiento.setId(longCount.incrementAndGet());

        // Create the PasoEscalamiento
        PasoEscalamientoDTO pasoEscalamientoDTO = pasoEscalamientoMapper.toDto(pasoEscalamiento);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPasoEscalamientoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pasoEscalamientoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(pasoEscalamientoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PasoEscalamiento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPasoEscalamiento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pasoEscalamiento.setId(longCount.incrementAndGet());

        // Create the PasoEscalamiento
        PasoEscalamientoDTO pasoEscalamientoDTO = pasoEscalamientoMapper.toDto(pasoEscalamiento);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPasoEscalamientoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(pasoEscalamientoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PasoEscalamiento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPasoEscalamiento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pasoEscalamiento.setId(longCount.incrementAndGet());

        // Create the PasoEscalamiento
        PasoEscalamientoDTO pasoEscalamientoDTO = pasoEscalamientoMapper.toDto(pasoEscalamiento);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPasoEscalamientoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pasoEscalamientoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PasoEscalamiento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePasoEscalamientoWithPatch() throws Exception {
        // Initialize the database
        insertedPasoEscalamiento = pasoEscalamientoRepository.saveAndFlush(pasoEscalamiento);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pasoEscalamiento using partial update
        PasoEscalamiento partialUpdatedPasoEscalamiento = new PasoEscalamiento();
        partialUpdatedPasoEscalamiento.setId(pasoEscalamiento.getId());

        partialUpdatedPasoEscalamiento.orden(UPDATED_ORDEN).esperaMinutos(UPDATED_ESPERA_MINUTOS).canal(UPDATED_CANAL);

        restPasoEscalamientoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPasoEscalamiento.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPasoEscalamiento))
            )
            .andExpect(status().isOk());

        // Validate the PasoEscalamiento in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPasoEscalamientoUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPasoEscalamiento, pasoEscalamiento),
            getPersistedPasoEscalamiento(pasoEscalamiento)
        );
    }

    @Test
    @Transactional
    void fullUpdatePasoEscalamientoWithPatch() throws Exception {
        // Initialize the database
        insertedPasoEscalamiento = pasoEscalamientoRepository.saveAndFlush(pasoEscalamiento);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pasoEscalamiento using partial update
        PasoEscalamiento partialUpdatedPasoEscalamiento = new PasoEscalamiento();
        partialUpdatedPasoEscalamiento.setId(pasoEscalamiento.getId());

        partialUpdatedPasoEscalamiento.orden(UPDATED_ORDEN).esperaMinutos(UPDATED_ESPERA_MINUTOS).canal(UPDATED_CANAL);

        restPasoEscalamientoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPasoEscalamiento.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPasoEscalamiento))
            )
            .andExpect(status().isOk());

        // Validate the PasoEscalamiento in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPasoEscalamientoUpdatableFieldsEquals(
            partialUpdatedPasoEscalamiento,
            getPersistedPasoEscalamiento(partialUpdatedPasoEscalamiento)
        );
    }

    @Test
    @Transactional
    void patchNonExistingPasoEscalamiento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pasoEscalamiento.setId(longCount.incrementAndGet());

        // Create the PasoEscalamiento
        PasoEscalamientoDTO pasoEscalamientoDTO = pasoEscalamientoMapper.toDto(pasoEscalamiento);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPasoEscalamientoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, pasoEscalamientoDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(pasoEscalamientoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PasoEscalamiento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPasoEscalamiento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pasoEscalamiento.setId(longCount.incrementAndGet());

        // Create the PasoEscalamiento
        PasoEscalamientoDTO pasoEscalamientoDTO = pasoEscalamientoMapper.toDto(pasoEscalamiento);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPasoEscalamientoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(pasoEscalamientoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PasoEscalamiento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPasoEscalamiento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pasoEscalamiento.setId(longCount.incrementAndGet());

        // Create the PasoEscalamiento
        PasoEscalamientoDTO pasoEscalamientoDTO = pasoEscalamientoMapper.toDto(pasoEscalamiento);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPasoEscalamientoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(pasoEscalamientoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PasoEscalamiento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePasoEscalamiento() throws Exception {
        // Initialize the database
        insertedPasoEscalamiento = pasoEscalamientoRepository.saveAndFlush(pasoEscalamiento);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the pasoEscalamiento
        restPasoEscalamientoMockMvc
            .perform(delete(ENTITY_API_URL_ID, pasoEscalamiento.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return pasoEscalamientoRepository.count();
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

    protected PasoEscalamiento getPersistedPasoEscalamiento(PasoEscalamiento pasoEscalamiento) {
        return pasoEscalamientoRepository.findById(pasoEscalamiento.getId()).orElseThrow();
    }

    protected void assertPersistedPasoEscalamientoToMatchAllProperties(PasoEscalamiento expectedPasoEscalamiento) {
        assertPasoEscalamientoAllPropertiesEquals(expectedPasoEscalamiento, getPersistedPasoEscalamiento(expectedPasoEscalamiento));
    }

    protected void assertPersistedPasoEscalamientoToMatchUpdatableProperties(PasoEscalamiento expectedPasoEscalamiento) {
        assertPasoEscalamientoAllUpdatablePropertiesEquals(
            expectedPasoEscalamiento,
            getPersistedPasoEscalamiento(expectedPasoEscalamiento)
        );
    }
}
