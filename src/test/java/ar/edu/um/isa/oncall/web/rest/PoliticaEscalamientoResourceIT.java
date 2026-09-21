package ar.edu.um.isa.oncall.web.rest;

import static ar.edu.um.isa.oncall.domain.PoliticaEscalamientoAsserts.*;
import static ar.edu.um.isa.oncall.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ar.edu.um.isa.oncall.IntegrationTest;
import ar.edu.um.isa.oncall.domain.PoliticaEscalamiento;
import ar.edu.um.isa.oncall.domain.Servicio;
import ar.edu.um.isa.oncall.repository.PoliticaEscalamientoRepository;
import ar.edu.um.isa.oncall.service.PoliticaEscalamientoService;
import ar.edu.um.isa.oncall.service.dto.PoliticaEscalamientoDTO;
import ar.edu.um.isa.oncall.service.mapper.PoliticaEscalamientoMapper;
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
 * Integration tests for the {@link PoliticaEscalamientoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PoliticaEscalamientoResourceIT {

    private static final String DEFAULT_NOMBRE = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPCION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPCION = "BBBBBBBBBB";

    private static final Integer DEFAULT_REPETIR_VECES = 0;
    private static final Integer UPDATED_REPETIR_VECES = 1;

    private static final String ENTITY_API_URL = "/api/politica-escalamientos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PoliticaEscalamientoRepository politicaEscalamientoRepository;

    @Mock
    private PoliticaEscalamientoRepository politicaEscalamientoRepositoryMock;

    @Autowired
    private PoliticaEscalamientoMapper politicaEscalamientoMapper;

    @Mock
    private PoliticaEscalamientoService politicaEscalamientoServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPoliticaEscalamientoMockMvc;

    private PoliticaEscalamiento politicaEscalamiento;

    private PoliticaEscalamiento insertedPoliticaEscalamiento;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PoliticaEscalamiento createEntity(EntityManager em) {
        PoliticaEscalamiento politicaEscalamiento = new PoliticaEscalamiento()
            .nombre(DEFAULT_NOMBRE)
            .descripcion(DEFAULT_DESCRIPCION)
            .repetirVeces(DEFAULT_REPETIR_VECES);
        // Add required entity
        Servicio servicio;
        if (TestUtil.findAll(em, Servicio.class).isEmpty()) {
            servicio = ServicioResourceIT.createEntity(em);
            em.persist(servicio);
            em.flush();
        } else {
            servicio = TestUtil.findAll(em, Servicio.class).get(0);
        }
        politicaEscalamiento.setServicio(servicio);
        return politicaEscalamiento;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PoliticaEscalamiento createUpdatedEntity(EntityManager em) {
        PoliticaEscalamiento updatedPoliticaEscalamiento = new PoliticaEscalamiento()
            .nombre(UPDATED_NOMBRE)
            .descripcion(UPDATED_DESCRIPCION)
            .repetirVeces(UPDATED_REPETIR_VECES);
        // Add required entity
        Servicio servicio;
        if (TestUtil.findAll(em, Servicio.class).isEmpty()) {
            servicio = ServicioResourceIT.createUpdatedEntity(em);
            em.persist(servicio);
            em.flush();
        } else {
            servicio = TestUtil.findAll(em, Servicio.class).get(0);
        }
        updatedPoliticaEscalamiento.setServicio(servicio);
        return updatedPoliticaEscalamiento;
    }

    @BeforeEach
    void initTest() {
        politicaEscalamiento = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedPoliticaEscalamiento != null) {
            politicaEscalamientoRepository.delete(insertedPoliticaEscalamiento);
            insertedPoliticaEscalamiento = null;
        }
    }

    @Test
    @Transactional
    void createPoliticaEscalamiento() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PoliticaEscalamiento
        PoliticaEscalamientoDTO politicaEscalamientoDTO = politicaEscalamientoMapper.toDto(politicaEscalamiento);
        var returnedPoliticaEscalamientoDTO = om.readValue(
            restPoliticaEscalamientoMockMvc
                .perform(
                    post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(politicaEscalamientoDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PoliticaEscalamientoDTO.class
        );

        // Validate the PoliticaEscalamiento in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPoliticaEscalamiento = politicaEscalamientoMapper.toEntity(returnedPoliticaEscalamientoDTO);
        assertPoliticaEscalamientoUpdatableFieldsEquals(
            returnedPoliticaEscalamiento,
            getPersistedPoliticaEscalamiento(returnedPoliticaEscalamiento)
        );

        insertedPoliticaEscalamiento = returnedPoliticaEscalamiento;
    }

    @Test
    @Transactional
    void createPoliticaEscalamientoWithExistingId() throws Exception {
        // Create the PoliticaEscalamiento with an existing ID
        politicaEscalamiento.setId(1L);
        PoliticaEscalamientoDTO politicaEscalamientoDTO = politicaEscalamientoMapper.toDto(politicaEscalamiento);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPoliticaEscalamientoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(politicaEscalamientoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PoliticaEscalamiento in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNombreIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        politicaEscalamiento.setNombre(null);

        // Create the PoliticaEscalamiento, which fails.
        PoliticaEscalamientoDTO politicaEscalamientoDTO = politicaEscalamientoMapper.toDto(politicaEscalamiento);

        restPoliticaEscalamientoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(politicaEscalamientoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRepetirVecesIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        politicaEscalamiento.setRepetirVeces(null);

        // Create the PoliticaEscalamiento, which fails.
        PoliticaEscalamientoDTO politicaEscalamientoDTO = politicaEscalamientoMapper.toDto(politicaEscalamiento);

        restPoliticaEscalamientoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(politicaEscalamientoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPoliticaEscalamientos() throws Exception {
        // Initialize the database
        insertedPoliticaEscalamiento = politicaEscalamientoRepository.saveAndFlush(politicaEscalamiento);

        // Get all the politicaEscalamientoList
        restPoliticaEscalamientoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(politicaEscalamiento.getId().intValue())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE)))
            .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION)))
            .andExpect(jsonPath("$.[*].repetirVeces").value(hasItem(DEFAULT_REPETIR_VECES)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPoliticaEscalamientosWithEagerRelationshipsIsEnabled() throws Exception {
        when(politicaEscalamientoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPoliticaEscalamientoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(politicaEscalamientoServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPoliticaEscalamientosWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(politicaEscalamientoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPoliticaEscalamientoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(politicaEscalamientoRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getPoliticaEscalamiento() throws Exception {
        // Initialize the database
        insertedPoliticaEscalamiento = politicaEscalamientoRepository.saveAndFlush(politicaEscalamiento);

        // Get the politicaEscalamiento
        restPoliticaEscalamientoMockMvc
            .perform(get(ENTITY_API_URL_ID, politicaEscalamiento.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(politicaEscalamiento.getId().intValue()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE))
            .andExpect(jsonPath("$.descripcion").value(DEFAULT_DESCRIPCION))
            .andExpect(jsonPath("$.repetirVeces").value(DEFAULT_REPETIR_VECES));
    }

    @Test
    @Transactional
    void getNonExistingPoliticaEscalamiento() throws Exception {
        // Get the politicaEscalamiento
        restPoliticaEscalamientoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPoliticaEscalamiento() throws Exception {
        // Initialize the database
        insertedPoliticaEscalamiento = politicaEscalamientoRepository.saveAndFlush(politicaEscalamiento);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the politicaEscalamiento
        PoliticaEscalamiento updatedPoliticaEscalamiento = politicaEscalamientoRepository
            .findById(politicaEscalamiento.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedPoliticaEscalamiento are not directly saved in db
        em.detach(updatedPoliticaEscalamiento);
        updatedPoliticaEscalamiento.nombre(UPDATED_NOMBRE).descripcion(UPDATED_DESCRIPCION).repetirVeces(UPDATED_REPETIR_VECES);
        PoliticaEscalamientoDTO politicaEscalamientoDTO = politicaEscalamientoMapper.toDto(updatedPoliticaEscalamiento);

        restPoliticaEscalamientoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, politicaEscalamientoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(politicaEscalamientoDTO))
            )
            .andExpect(status().isOk());

        // Validate the PoliticaEscalamiento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPoliticaEscalamientoToMatchAllProperties(updatedPoliticaEscalamiento);
    }

    @Test
    @Transactional
    void putNonExistingPoliticaEscalamiento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        politicaEscalamiento.setId(longCount.incrementAndGet());

        // Create the PoliticaEscalamiento
        PoliticaEscalamientoDTO politicaEscalamientoDTO = politicaEscalamientoMapper.toDto(politicaEscalamiento);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPoliticaEscalamientoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, politicaEscalamientoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(politicaEscalamientoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PoliticaEscalamiento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPoliticaEscalamiento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        politicaEscalamiento.setId(longCount.incrementAndGet());

        // Create the PoliticaEscalamiento
        PoliticaEscalamientoDTO politicaEscalamientoDTO = politicaEscalamientoMapper.toDto(politicaEscalamiento);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPoliticaEscalamientoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(politicaEscalamientoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PoliticaEscalamiento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPoliticaEscalamiento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        politicaEscalamiento.setId(longCount.incrementAndGet());

        // Create the PoliticaEscalamiento
        PoliticaEscalamientoDTO politicaEscalamientoDTO = politicaEscalamientoMapper.toDto(politicaEscalamiento);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPoliticaEscalamientoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(politicaEscalamientoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PoliticaEscalamiento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePoliticaEscalamientoWithPatch() throws Exception {
        // Initialize the database
        insertedPoliticaEscalamiento = politicaEscalamientoRepository.saveAndFlush(politicaEscalamiento);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the politicaEscalamiento using partial update
        PoliticaEscalamiento partialUpdatedPoliticaEscalamiento = new PoliticaEscalamiento();
        partialUpdatedPoliticaEscalamiento.setId(politicaEscalamiento.getId());

        partialUpdatedPoliticaEscalamiento.descripcion(UPDATED_DESCRIPCION);

        restPoliticaEscalamientoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPoliticaEscalamiento.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPoliticaEscalamiento))
            )
            .andExpect(status().isOk());

        // Validate the PoliticaEscalamiento in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPoliticaEscalamientoUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPoliticaEscalamiento, politicaEscalamiento),
            getPersistedPoliticaEscalamiento(politicaEscalamiento)
        );
    }

    @Test
    @Transactional
    void fullUpdatePoliticaEscalamientoWithPatch() throws Exception {
        // Initialize the database
        insertedPoliticaEscalamiento = politicaEscalamientoRepository.saveAndFlush(politicaEscalamiento);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the politicaEscalamiento using partial update
        PoliticaEscalamiento partialUpdatedPoliticaEscalamiento = new PoliticaEscalamiento();
        partialUpdatedPoliticaEscalamiento.setId(politicaEscalamiento.getId());

        partialUpdatedPoliticaEscalamiento.nombre(UPDATED_NOMBRE).descripcion(UPDATED_DESCRIPCION).repetirVeces(UPDATED_REPETIR_VECES);

        restPoliticaEscalamientoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPoliticaEscalamiento.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPoliticaEscalamiento))
            )
            .andExpect(status().isOk());

        // Validate the PoliticaEscalamiento in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPoliticaEscalamientoUpdatableFieldsEquals(
            partialUpdatedPoliticaEscalamiento,
            getPersistedPoliticaEscalamiento(partialUpdatedPoliticaEscalamiento)
        );
    }

    @Test
    @Transactional
    void patchNonExistingPoliticaEscalamiento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        politicaEscalamiento.setId(longCount.incrementAndGet());

        // Create the PoliticaEscalamiento
        PoliticaEscalamientoDTO politicaEscalamientoDTO = politicaEscalamientoMapper.toDto(politicaEscalamiento);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPoliticaEscalamientoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, politicaEscalamientoDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(politicaEscalamientoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PoliticaEscalamiento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPoliticaEscalamiento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        politicaEscalamiento.setId(longCount.incrementAndGet());

        // Create the PoliticaEscalamiento
        PoliticaEscalamientoDTO politicaEscalamientoDTO = politicaEscalamientoMapper.toDto(politicaEscalamiento);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPoliticaEscalamientoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(politicaEscalamientoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PoliticaEscalamiento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPoliticaEscalamiento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        politicaEscalamiento.setId(longCount.incrementAndGet());

        // Create the PoliticaEscalamiento
        PoliticaEscalamientoDTO politicaEscalamientoDTO = politicaEscalamientoMapper.toDto(politicaEscalamiento);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPoliticaEscalamientoMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(politicaEscalamientoDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PoliticaEscalamiento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePoliticaEscalamiento() throws Exception {
        // Initialize the database
        insertedPoliticaEscalamiento = politicaEscalamientoRepository.saveAndFlush(politicaEscalamiento);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the politicaEscalamiento
        restPoliticaEscalamientoMockMvc
            .perform(delete(ENTITY_API_URL_ID, politicaEscalamiento.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return politicaEscalamientoRepository.count();
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

    protected PoliticaEscalamiento getPersistedPoliticaEscalamiento(PoliticaEscalamiento politicaEscalamiento) {
        return politicaEscalamientoRepository.findById(politicaEscalamiento.getId()).orElseThrow();
    }

    protected void assertPersistedPoliticaEscalamientoToMatchAllProperties(PoliticaEscalamiento expectedPoliticaEscalamiento) {
        assertPoliticaEscalamientoAllPropertiesEquals(
            expectedPoliticaEscalamiento,
            getPersistedPoliticaEscalamiento(expectedPoliticaEscalamiento)
        );
    }

    protected void assertPersistedPoliticaEscalamientoToMatchUpdatableProperties(PoliticaEscalamiento expectedPoliticaEscalamiento) {
        assertPoliticaEscalamientoAllUpdatablePropertiesEquals(
            expectedPoliticaEscalamiento,
            getPersistedPoliticaEscalamiento(expectedPoliticaEscalamiento)
        );
    }
}
