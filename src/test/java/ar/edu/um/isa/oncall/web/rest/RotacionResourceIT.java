package ar.edu.um.isa.oncall.web.rest;

import static ar.edu.um.isa.oncall.domain.RotacionAsserts.*;
import static ar.edu.um.isa.oncall.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ar.edu.um.isa.oncall.IntegrationTest;
import ar.edu.um.isa.oncall.domain.Equipo;
import ar.edu.um.isa.oncall.domain.Rotacion;
import ar.edu.um.isa.oncall.domain.enumeration.TipoRotacion;
import ar.edu.um.isa.oncall.repository.RotacionRepository;
import ar.edu.um.isa.oncall.service.RotacionService;
import ar.edu.um.isa.oncall.service.dto.RotacionDTO;
import ar.edu.um.isa.oncall.service.mapper.RotacionMapper;
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
 * Integration tests for the {@link RotacionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class RotacionResourceIT {

    private static final String DEFAULT_NOMBRE = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE = "BBBBBBBBBB";

    private static final TipoRotacion DEFAULT_TIPO = TipoRotacion.DIARIA;
    private static final TipoRotacion UPDATED_TIPO = TipoRotacion.SEMANAL;

    private static final String DEFAULT_ZONA_HORARIA = "AAAAAAAAAA";
    private static final String UPDATED_ZONA_HORARIA = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVA = false;
    private static final Boolean UPDATED_ACTIVA = true;

    private static final String ENTITY_API_URL = "/api/rotacions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RotacionRepository rotacionRepository;

    @Mock
    private RotacionRepository rotacionRepositoryMock;

    @Autowired
    private RotacionMapper rotacionMapper;

    @Mock
    private RotacionService rotacionServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRotacionMockMvc;

    private Rotacion rotacion;

    private Rotacion insertedRotacion;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Rotacion createEntity(EntityManager em) {
        Rotacion rotacion = new Rotacion()
            .nombre(DEFAULT_NOMBRE)
            .tipo(DEFAULT_TIPO)
            .zonaHoraria(DEFAULT_ZONA_HORARIA)
            .activa(DEFAULT_ACTIVA);
        // Add required entity
        Equipo equipo;
        if (TestUtil.findAll(em, Equipo.class).isEmpty()) {
            equipo = EquipoResourceIT.createEntity();
            em.persist(equipo);
            em.flush();
        } else {
            equipo = TestUtil.findAll(em, Equipo.class).get(0);
        }
        rotacion.setEquipo(equipo);
        return rotacion;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Rotacion createUpdatedEntity(EntityManager em) {
        Rotacion updatedRotacion = new Rotacion()
            .nombre(UPDATED_NOMBRE)
            .tipo(UPDATED_TIPO)
            .zonaHoraria(UPDATED_ZONA_HORARIA)
            .activa(UPDATED_ACTIVA);
        // Add required entity
        Equipo equipo;
        if (TestUtil.findAll(em, Equipo.class).isEmpty()) {
            equipo = EquipoResourceIT.createUpdatedEntity();
            em.persist(equipo);
            em.flush();
        } else {
            equipo = TestUtil.findAll(em, Equipo.class).get(0);
        }
        updatedRotacion.setEquipo(equipo);
        return updatedRotacion;
    }

    @BeforeEach
    void initTest() {
        rotacion = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedRotacion != null) {
            rotacionRepository.delete(insertedRotacion);
            insertedRotacion = null;
        }
    }

    @Test
    @Transactional
    void createRotacion() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Rotacion
        RotacionDTO rotacionDTO = rotacionMapper.toDto(rotacion);
        var returnedRotacionDTO = om.readValue(
            restRotacionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(rotacionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            RotacionDTO.class
        );

        // Validate the Rotacion in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedRotacion = rotacionMapper.toEntity(returnedRotacionDTO);
        assertRotacionUpdatableFieldsEquals(returnedRotacion, getPersistedRotacion(returnedRotacion));

        insertedRotacion = returnedRotacion;
    }

    @Test
    @Transactional
    void createRotacionWithExistingId() throws Exception {
        // Create the Rotacion with an existing ID
        rotacion.setId(1L);
        RotacionDTO rotacionDTO = rotacionMapper.toDto(rotacion);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRotacionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(rotacionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Rotacion in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNombreIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        rotacion.setNombre(null);

        // Create the Rotacion, which fails.
        RotacionDTO rotacionDTO = rotacionMapper.toDto(rotacion);

        restRotacionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(rotacionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTipoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        rotacion.setTipo(null);

        // Create the Rotacion, which fails.
        RotacionDTO rotacionDTO = rotacionMapper.toDto(rotacion);

        restRotacionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(rotacionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkZonaHorariaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        rotacion.setZonaHoraria(null);

        // Create the Rotacion, which fails.
        RotacionDTO rotacionDTO = rotacionMapper.toDto(rotacion);

        restRotacionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(rotacionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActivaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        rotacion.setActiva(null);

        // Create the Rotacion, which fails.
        RotacionDTO rotacionDTO = rotacionMapper.toDto(rotacion);

        restRotacionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(rotacionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllRotacions() throws Exception {
        // Initialize the database
        insertedRotacion = rotacionRepository.saveAndFlush(rotacion);

        // Get all the rotacionList
        restRotacionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(rotacion.getId().intValue())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE)))
            .andExpect(jsonPath("$.[*].tipo").value(hasItem(DEFAULT_TIPO.toString())))
            .andExpect(jsonPath("$.[*].zonaHoraria").value(hasItem(DEFAULT_ZONA_HORARIA)))
            .andExpect(jsonPath("$.[*].activa").value(hasItem(DEFAULT_ACTIVA)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRotacionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(rotacionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRotacionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(rotacionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRotacionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(rotacionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRotacionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(rotacionRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getRotacion() throws Exception {
        // Initialize the database
        insertedRotacion = rotacionRepository.saveAndFlush(rotacion);

        // Get the rotacion
        restRotacionMockMvc
            .perform(get(ENTITY_API_URL_ID, rotacion.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(rotacion.getId().intValue()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE))
            .andExpect(jsonPath("$.tipo").value(DEFAULT_TIPO.toString()))
            .andExpect(jsonPath("$.zonaHoraria").value(DEFAULT_ZONA_HORARIA))
            .andExpect(jsonPath("$.activa").value(DEFAULT_ACTIVA));
    }

    @Test
    @Transactional
    void getNonExistingRotacion() throws Exception {
        // Get the rotacion
        restRotacionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRotacion() throws Exception {
        // Initialize the database
        insertedRotacion = rotacionRepository.saveAndFlush(rotacion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the rotacion
        Rotacion updatedRotacion = rotacionRepository.findById(rotacion.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedRotacion are not directly saved in db
        em.detach(updatedRotacion);
        updatedRotacion.nombre(UPDATED_NOMBRE).tipo(UPDATED_TIPO).zonaHoraria(UPDATED_ZONA_HORARIA).activa(UPDATED_ACTIVA);
        RotacionDTO rotacionDTO = rotacionMapper.toDto(updatedRotacion);

        restRotacionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, rotacionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(rotacionDTO))
            )
            .andExpect(status().isOk());

        // Validate the Rotacion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRotacionToMatchAllProperties(updatedRotacion);
    }

    @Test
    @Transactional
    void putNonExistingRotacion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rotacion.setId(longCount.incrementAndGet());

        // Create the Rotacion
        RotacionDTO rotacionDTO = rotacionMapper.toDto(rotacion);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRotacionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, rotacionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(rotacionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Rotacion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRotacion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rotacion.setId(longCount.incrementAndGet());

        // Create the Rotacion
        RotacionDTO rotacionDTO = rotacionMapper.toDto(rotacion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRotacionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(rotacionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Rotacion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRotacion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rotacion.setId(longCount.incrementAndGet());

        // Create the Rotacion
        RotacionDTO rotacionDTO = rotacionMapper.toDto(rotacion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRotacionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(rotacionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Rotacion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRotacionWithPatch() throws Exception {
        // Initialize the database
        insertedRotacion = rotacionRepository.saveAndFlush(rotacion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the rotacion using partial update
        Rotacion partialUpdatedRotacion = new Rotacion();
        partialUpdatedRotacion.setId(rotacion.getId());

        partialUpdatedRotacion.nombre(UPDATED_NOMBRE).tipo(UPDATED_TIPO);

        restRotacionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRotacion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRotacion))
            )
            .andExpect(status().isOk());

        // Validate the Rotacion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRotacionUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedRotacion, rotacion), getPersistedRotacion(rotacion));
    }

    @Test
    @Transactional
    void fullUpdateRotacionWithPatch() throws Exception {
        // Initialize the database
        insertedRotacion = rotacionRepository.saveAndFlush(rotacion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the rotacion using partial update
        Rotacion partialUpdatedRotacion = new Rotacion();
        partialUpdatedRotacion.setId(rotacion.getId());

        partialUpdatedRotacion.nombre(UPDATED_NOMBRE).tipo(UPDATED_TIPO).zonaHoraria(UPDATED_ZONA_HORARIA).activa(UPDATED_ACTIVA);

        restRotacionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRotacion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRotacion))
            )
            .andExpect(status().isOk());

        // Validate the Rotacion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRotacionUpdatableFieldsEquals(partialUpdatedRotacion, getPersistedRotacion(partialUpdatedRotacion));
    }

    @Test
    @Transactional
    void patchNonExistingRotacion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rotacion.setId(longCount.incrementAndGet());

        // Create the Rotacion
        RotacionDTO rotacionDTO = rotacionMapper.toDto(rotacion);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRotacionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, rotacionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(rotacionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Rotacion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRotacion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rotacion.setId(longCount.incrementAndGet());

        // Create the Rotacion
        RotacionDTO rotacionDTO = rotacionMapper.toDto(rotacion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRotacionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(rotacionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Rotacion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRotacion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rotacion.setId(longCount.incrementAndGet());

        // Create the Rotacion
        RotacionDTO rotacionDTO = rotacionMapper.toDto(rotacion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRotacionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(rotacionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Rotacion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRotacion() throws Exception {
        // Initialize the database
        insertedRotacion = rotacionRepository.saveAndFlush(rotacion);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the rotacion
        restRotacionMockMvc
            .perform(delete(ENTITY_API_URL_ID, rotacion.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return rotacionRepository.count();
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

    protected Rotacion getPersistedRotacion(Rotacion rotacion) {
        return rotacionRepository.findById(rotacion.getId()).orElseThrow();
    }

    protected void assertPersistedRotacionToMatchAllProperties(Rotacion expectedRotacion) {
        assertRotacionAllPropertiesEquals(expectedRotacion, getPersistedRotacion(expectedRotacion));
    }

    protected void assertPersistedRotacionToMatchUpdatableProperties(Rotacion expectedRotacion) {
        assertRotacionAllUpdatablePropertiesEquals(expectedRotacion, getPersistedRotacion(expectedRotacion));
    }
}
