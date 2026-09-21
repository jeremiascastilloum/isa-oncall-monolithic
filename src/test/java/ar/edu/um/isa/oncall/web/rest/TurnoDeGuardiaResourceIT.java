package ar.edu.um.isa.oncall.web.rest;

import static ar.edu.um.isa.oncall.domain.TurnoDeGuardiaAsserts.*;
import static ar.edu.um.isa.oncall.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ar.edu.um.isa.oncall.IntegrationTest;
import ar.edu.um.isa.oncall.domain.Rotacion;
import ar.edu.um.isa.oncall.domain.TurnoDeGuardia;
import ar.edu.um.isa.oncall.domain.User;
import ar.edu.um.isa.oncall.repository.TurnoDeGuardiaRepository;
import ar.edu.um.isa.oncall.repository.UserRepository;
import ar.edu.um.isa.oncall.service.TurnoDeGuardiaService;
import ar.edu.um.isa.oncall.service.dto.TurnoDeGuardiaDTO;
import ar.edu.um.isa.oncall.service.mapper.TurnoDeGuardiaMapper;
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
 * Integration tests for the {@link TurnoDeGuardiaResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TurnoDeGuardiaResourceIT {

    private static final Instant DEFAULT_DESDE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DESDE = Instant.ofEpochMilli(1701729143509L);

    private static final Instant DEFAULT_HASTA = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_HASTA = Instant.ofEpochMilli(1701729143509L);

    private static final Boolean DEFAULT_ES_REEMPLAZO = false;
    private static final Boolean UPDATED_ES_REEMPLAZO = true;

    private static final String DEFAULT_NOTA = "AAAAAAAAAA";
    private static final String UPDATED_NOTA = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/turno-de-guardias";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TurnoDeGuardiaRepository turnoDeGuardiaRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private TurnoDeGuardiaRepository turnoDeGuardiaRepositoryMock;

    @Autowired
    private TurnoDeGuardiaMapper turnoDeGuardiaMapper;

    @Mock
    private TurnoDeGuardiaService turnoDeGuardiaServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTurnoDeGuardiaMockMvc;

    private TurnoDeGuardia turnoDeGuardia;

    private TurnoDeGuardia insertedTurnoDeGuardia;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TurnoDeGuardia createEntity(EntityManager em) {
        TurnoDeGuardia turnoDeGuardia = new TurnoDeGuardia()
            .desde(DEFAULT_DESDE)
            .hasta(DEFAULT_HASTA)
            .esReemplazo(DEFAULT_ES_REEMPLAZO)
            .nota(DEFAULT_NOTA);
        // Add required entity
        Rotacion rotacion;
        if (TestUtil.findAll(em, Rotacion.class).isEmpty()) {
            rotacion = RotacionResourceIT.createEntity(em);
            em.persist(rotacion);
            em.flush();
        } else {
            rotacion = TestUtil.findAll(em, Rotacion.class).get(0);
        }
        turnoDeGuardia.setRotacion(rotacion);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        turnoDeGuardia.setResponsable(user);
        return turnoDeGuardia;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TurnoDeGuardia createUpdatedEntity(EntityManager em) {
        TurnoDeGuardia updatedTurnoDeGuardia = new TurnoDeGuardia()
            .desde(UPDATED_DESDE)
            .hasta(UPDATED_HASTA)
            .esReemplazo(UPDATED_ES_REEMPLAZO)
            .nota(UPDATED_NOTA);
        // Add required entity
        Rotacion rotacion;
        if (TestUtil.findAll(em, Rotacion.class).isEmpty()) {
            rotacion = RotacionResourceIT.createUpdatedEntity(em);
            em.persist(rotacion);
            em.flush();
        } else {
            rotacion = TestUtil.findAll(em, Rotacion.class).get(0);
        }
        updatedTurnoDeGuardia.setRotacion(rotacion);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedTurnoDeGuardia.setResponsable(user);
        return updatedTurnoDeGuardia;
    }

    @BeforeEach
    void initTest() {
        turnoDeGuardia = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedTurnoDeGuardia != null) {
            turnoDeGuardiaRepository.delete(insertedTurnoDeGuardia);
            insertedTurnoDeGuardia = null;
        }
    }

    @Test
    @Transactional
    void createTurnoDeGuardia() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TurnoDeGuardia
        TurnoDeGuardiaDTO turnoDeGuardiaDTO = turnoDeGuardiaMapper.toDto(turnoDeGuardia);
        var returnedTurnoDeGuardiaDTO = om.readValue(
            restTurnoDeGuardiaMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(turnoDeGuardiaDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TurnoDeGuardiaDTO.class
        );

        // Validate the TurnoDeGuardia in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTurnoDeGuardia = turnoDeGuardiaMapper.toEntity(returnedTurnoDeGuardiaDTO);
        assertTurnoDeGuardiaUpdatableFieldsEquals(returnedTurnoDeGuardia, getPersistedTurnoDeGuardia(returnedTurnoDeGuardia));

        insertedTurnoDeGuardia = returnedTurnoDeGuardia;
    }

    @Test
    @Transactional
    void createTurnoDeGuardiaWithExistingId() throws Exception {
        // Create the TurnoDeGuardia with an existing ID
        turnoDeGuardia.setId(1L);
        TurnoDeGuardiaDTO turnoDeGuardiaDTO = turnoDeGuardiaMapper.toDto(turnoDeGuardia);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTurnoDeGuardiaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(turnoDeGuardiaDTO)))
            .andExpect(status().isBadRequest());

        // Validate the TurnoDeGuardia in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDesdeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        turnoDeGuardia.setDesde(null);

        // Create the TurnoDeGuardia, which fails.
        TurnoDeGuardiaDTO turnoDeGuardiaDTO = turnoDeGuardiaMapper.toDto(turnoDeGuardia);

        restTurnoDeGuardiaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(turnoDeGuardiaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkHastaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        turnoDeGuardia.setHasta(null);

        // Create the TurnoDeGuardia, which fails.
        TurnoDeGuardiaDTO turnoDeGuardiaDTO = turnoDeGuardiaMapper.toDto(turnoDeGuardia);

        restTurnoDeGuardiaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(turnoDeGuardiaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTurnoDeGuardias() throws Exception {
        // Initialize the database
        insertedTurnoDeGuardia = turnoDeGuardiaRepository.saveAndFlush(turnoDeGuardia);

        // Get all the turnoDeGuardiaList
        restTurnoDeGuardiaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(turnoDeGuardia.getId().intValue())))
            .andExpect(jsonPath("$.[*].desde").value(hasItem(DEFAULT_DESDE.toString())))
            .andExpect(jsonPath("$.[*].hasta").value(hasItem(DEFAULT_HASTA.toString())))
            .andExpect(jsonPath("$.[*].esReemplazo").value(hasItem(DEFAULT_ES_REEMPLAZO)))
            .andExpect(jsonPath("$.[*].nota").value(hasItem(DEFAULT_NOTA)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTurnoDeGuardiasWithEagerRelationshipsIsEnabled() throws Exception {
        when(turnoDeGuardiaServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTurnoDeGuardiaMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(turnoDeGuardiaServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTurnoDeGuardiasWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(turnoDeGuardiaServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTurnoDeGuardiaMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(turnoDeGuardiaRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getTurnoDeGuardia() throws Exception {
        // Initialize the database
        insertedTurnoDeGuardia = turnoDeGuardiaRepository.saveAndFlush(turnoDeGuardia);

        // Get the turnoDeGuardia
        restTurnoDeGuardiaMockMvc
            .perform(get(ENTITY_API_URL_ID, turnoDeGuardia.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(turnoDeGuardia.getId().intValue()))
            .andExpect(jsonPath("$.desde").value(DEFAULT_DESDE.toString()))
            .andExpect(jsonPath("$.hasta").value(DEFAULT_HASTA.toString()))
            .andExpect(jsonPath("$.esReemplazo").value(DEFAULT_ES_REEMPLAZO))
            .andExpect(jsonPath("$.nota").value(DEFAULT_NOTA));
    }

    @Test
    @Transactional
    void getNonExistingTurnoDeGuardia() throws Exception {
        // Get the turnoDeGuardia
        restTurnoDeGuardiaMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTurnoDeGuardia() throws Exception {
        // Initialize the database
        insertedTurnoDeGuardia = turnoDeGuardiaRepository.saveAndFlush(turnoDeGuardia);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the turnoDeGuardia
        TurnoDeGuardia updatedTurnoDeGuardia = turnoDeGuardiaRepository.findById(turnoDeGuardia.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTurnoDeGuardia are not directly saved in db
        em.detach(updatedTurnoDeGuardia);
        updatedTurnoDeGuardia.desde(UPDATED_DESDE).hasta(UPDATED_HASTA).esReemplazo(UPDATED_ES_REEMPLAZO).nota(UPDATED_NOTA);
        TurnoDeGuardiaDTO turnoDeGuardiaDTO = turnoDeGuardiaMapper.toDto(updatedTurnoDeGuardia);

        restTurnoDeGuardiaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, turnoDeGuardiaDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(turnoDeGuardiaDTO))
            )
            .andExpect(status().isOk());

        // Validate the TurnoDeGuardia in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTurnoDeGuardiaToMatchAllProperties(updatedTurnoDeGuardia);
    }

    @Test
    @Transactional
    void putNonExistingTurnoDeGuardia() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        turnoDeGuardia.setId(longCount.incrementAndGet());

        // Create the TurnoDeGuardia
        TurnoDeGuardiaDTO turnoDeGuardiaDTO = turnoDeGuardiaMapper.toDto(turnoDeGuardia);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTurnoDeGuardiaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, turnoDeGuardiaDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(turnoDeGuardiaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TurnoDeGuardia in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTurnoDeGuardia() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        turnoDeGuardia.setId(longCount.incrementAndGet());

        // Create the TurnoDeGuardia
        TurnoDeGuardiaDTO turnoDeGuardiaDTO = turnoDeGuardiaMapper.toDto(turnoDeGuardia);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTurnoDeGuardiaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(turnoDeGuardiaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TurnoDeGuardia in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTurnoDeGuardia() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        turnoDeGuardia.setId(longCount.incrementAndGet());

        // Create the TurnoDeGuardia
        TurnoDeGuardiaDTO turnoDeGuardiaDTO = turnoDeGuardiaMapper.toDto(turnoDeGuardia);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTurnoDeGuardiaMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(turnoDeGuardiaDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TurnoDeGuardia in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTurnoDeGuardiaWithPatch() throws Exception {
        // Initialize the database
        insertedTurnoDeGuardia = turnoDeGuardiaRepository.saveAndFlush(turnoDeGuardia);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the turnoDeGuardia using partial update
        TurnoDeGuardia partialUpdatedTurnoDeGuardia = new TurnoDeGuardia();
        partialUpdatedTurnoDeGuardia.setId(turnoDeGuardia.getId());

        partialUpdatedTurnoDeGuardia.desde(UPDATED_DESDE).esReemplazo(UPDATED_ES_REEMPLAZO);

        restTurnoDeGuardiaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTurnoDeGuardia.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTurnoDeGuardia))
            )
            .andExpect(status().isOk());

        // Validate the TurnoDeGuardia in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTurnoDeGuardiaUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTurnoDeGuardia, turnoDeGuardia),
            getPersistedTurnoDeGuardia(turnoDeGuardia)
        );
    }

    @Test
    @Transactional
    void fullUpdateTurnoDeGuardiaWithPatch() throws Exception {
        // Initialize the database
        insertedTurnoDeGuardia = turnoDeGuardiaRepository.saveAndFlush(turnoDeGuardia);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the turnoDeGuardia using partial update
        TurnoDeGuardia partialUpdatedTurnoDeGuardia = new TurnoDeGuardia();
        partialUpdatedTurnoDeGuardia.setId(turnoDeGuardia.getId());

        partialUpdatedTurnoDeGuardia.desde(UPDATED_DESDE).hasta(UPDATED_HASTA).esReemplazo(UPDATED_ES_REEMPLAZO).nota(UPDATED_NOTA);

        restTurnoDeGuardiaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTurnoDeGuardia.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTurnoDeGuardia))
            )
            .andExpect(status().isOk());

        // Validate the TurnoDeGuardia in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTurnoDeGuardiaUpdatableFieldsEquals(partialUpdatedTurnoDeGuardia, getPersistedTurnoDeGuardia(partialUpdatedTurnoDeGuardia));
    }

    @Test
    @Transactional
    void patchNonExistingTurnoDeGuardia() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        turnoDeGuardia.setId(longCount.incrementAndGet());

        // Create the TurnoDeGuardia
        TurnoDeGuardiaDTO turnoDeGuardiaDTO = turnoDeGuardiaMapper.toDto(turnoDeGuardia);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTurnoDeGuardiaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, turnoDeGuardiaDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(turnoDeGuardiaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TurnoDeGuardia in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTurnoDeGuardia() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        turnoDeGuardia.setId(longCount.incrementAndGet());

        // Create the TurnoDeGuardia
        TurnoDeGuardiaDTO turnoDeGuardiaDTO = turnoDeGuardiaMapper.toDto(turnoDeGuardia);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTurnoDeGuardiaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(turnoDeGuardiaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TurnoDeGuardia in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTurnoDeGuardia() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        turnoDeGuardia.setId(longCount.incrementAndGet());

        // Create the TurnoDeGuardia
        TurnoDeGuardiaDTO turnoDeGuardiaDTO = turnoDeGuardiaMapper.toDto(turnoDeGuardia);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTurnoDeGuardiaMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(turnoDeGuardiaDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TurnoDeGuardia in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTurnoDeGuardia() throws Exception {
        // Initialize the database
        insertedTurnoDeGuardia = turnoDeGuardiaRepository.saveAndFlush(turnoDeGuardia);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the turnoDeGuardia
        restTurnoDeGuardiaMockMvc
            .perform(delete(ENTITY_API_URL_ID, turnoDeGuardia.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return turnoDeGuardiaRepository.count();
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

    protected TurnoDeGuardia getPersistedTurnoDeGuardia(TurnoDeGuardia turnoDeGuardia) {
        return turnoDeGuardiaRepository.findById(turnoDeGuardia.getId()).orElseThrow();
    }

    protected void assertPersistedTurnoDeGuardiaToMatchAllProperties(TurnoDeGuardia expectedTurnoDeGuardia) {
        assertTurnoDeGuardiaAllPropertiesEquals(expectedTurnoDeGuardia, getPersistedTurnoDeGuardia(expectedTurnoDeGuardia));
    }

    protected void assertPersistedTurnoDeGuardiaToMatchUpdatableProperties(TurnoDeGuardia expectedTurnoDeGuardia) {
        assertTurnoDeGuardiaAllUpdatablePropertiesEquals(expectedTurnoDeGuardia, getPersistedTurnoDeGuardia(expectedTurnoDeGuardia));
    }
}
