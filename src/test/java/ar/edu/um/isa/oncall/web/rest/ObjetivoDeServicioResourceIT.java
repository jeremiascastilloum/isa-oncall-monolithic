package ar.edu.um.isa.oncall.web.rest;

import static ar.edu.um.isa.oncall.domain.ObjetivoDeServicioAsserts.*;
import static ar.edu.um.isa.oncall.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ar.edu.um.isa.oncall.IntegrationTest;
import ar.edu.um.isa.oncall.domain.ObjetivoDeServicio;
import ar.edu.um.isa.oncall.domain.Servicio;
import ar.edu.um.isa.oncall.domain.enumeration.Severidad;
import ar.edu.um.isa.oncall.domain.enumeration.TipoObjetivo;
import ar.edu.um.isa.oncall.repository.ObjetivoDeServicioRepository;
import ar.edu.um.isa.oncall.service.ObjetivoDeServicioService;
import ar.edu.um.isa.oncall.service.dto.ObjetivoDeServicioDTO;
import ar.edu.um.isa.oncall.service.mapper.ObjetivoDeServicioMapper;
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
 * Integration tests for the {@link ObjetivoDeServicioResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ObjetivoDeServicioResourceIT {

    private static final TipoObjetivo DEFAULT_TIPO = TipoObjetivo.TIEMPO_DE_RECONOCIMIENTO;
    private static final TipoObjetivo UPDATED_TIPO = TipoObjetivo.TIEMPO_DE_RESOLUCION;

    private static final Severidad DEFAULT_SEVERIDAD_APLICABLE = Severidad.SEV1;
    private static final Severidad UPDATED_SEVERIDAD_APLICABLE = Severidad.SEV2;

    private static final Integer DEFAULT_MINUTOS_OBJETIVO = 1;
    private static final Integer UPDATED_MINUTOS_OBJETIVO = 2;

    private static final String DEFAULT_DESCRIPCION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPCION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/objetivo-de-servicios";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ObjetivoDeServicioRepository objetivoDeServicioRepository;

    @Mock
    private ObjetivoDeServicioRepository objetivoDeServicioRepositoryMock;

    @Autowired
    private ObjetivoDeServicioMapper objetivoDeServicioMapper;

    @Mock
    private ObjetivoDeServicioService objetivoDeServicioServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restObjetivoDeServicioMockMvc;

    private ObjetivoDeServicio objetivoDeServicio;

    private ObjetivoDeServicio insertedObjetivoDeServicio;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ObjetivoDeServicio createEntity(EntityManager em) {
        ObjetivoDeServicio objetivoDeServicio = new ObjetivoDeServicio()
            .tipo(DEFAULT_TIPO)
            .severidadAplicable(DEFAULT_SEVERIDAD_APLICABLE)
            .minutosObjetivo(DEFAULT_MINUTOS_OBJETIVO)
            .descripcion(DEFAULT_DESCRIPCION);
        // Add required entity
        Servicio servicio;
        if (TestUtil.findAll(em, Servicio.class).isEmpty()) {
            servicio = ServicioResourceIT.createEntity(em);
            em.persist(servicio);
            em.flush();
        } else {
            servicio = TestUtil.findAll(em, Servicio.class).get(0);
        }
        objetivoDeServicio.setServicio(servicio);
        return objetivoDeServicio;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ObjetivoDeServicio createUpdatedEntity(EntityManager em) {
        ObjetivoDeServicio updatedObjetivoDeServicio = new ObjetivoDeServicio()
            .tipo(UPDATED_TIPO)
            .severidadAplicable(UPDATED_SEVERIDAD_APLICABLE)
            .minutosObjetivo(UPDATED_MINUTOS_OBJETIVO)
            .descripcion(UPDATED_DESCRIPCION);
        // Add required entity
        Servicio servicio;
        if (TestUtil.findAll(em, Servicio.class).isEmpty()) {
            servicio = ServicioResourceIT.createUpdatedEntity(em);
            em.persist(servicio);
            em.flush();
        } else {
            servicio = TestUtil.findAll(em, Servicio.class).get(0);
        }
        updatedObjetivoDeServicio.setServicio(servicio);
        return updatedObjetivoDeServicio;
    }

    @BeforeEach
    void initTest() {
        objetivoDeServicio = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedObjetivoDeServicio != null) {
            objetivoDeServicioRepository.delete(insertedObjetivoDeServicio);
            insertedObjetivoDeServicio = null;
        }
    }

    @Test
    @Transactional
    void createObjetivoDeServicio() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ObjetivoDeServicio
        ObjetivoDeServicioDTO objetivoDeServicioDTO = objetivoDeServicioMapper.toDto(objetivoDeServicio);
        var returnedObjetivoDeServicioDTO = om.readValue(
            restObjetivoDeServicioMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(objetivoDeServicioDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ObjetivoDeServicioDTO.class
        );

        // Validate the ObjetivoDeServicio in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedObjetivoDeServicio = objetivoDeServicioMapper.toEntity(returnedObjetivoDeServicioDTO);
        assertObjetivoDeServicioUpdatableFieldsEquals(
            returnedObjetivoDeServicio,
            getPersistedObjetivoDeServicio(returnedObjetivoDeServicio)
        );

        insertedObjetivoDeServicio = returnedObjetivoDeServicio;
    }

    @Test
    @Transactional
    void createObjetivoDeServicioWithExistingId() throws Exception {
        // Create the ObjetivoDeServicio with an existing ID
        objetivoDeServicio.setId(1L);
        ObjetivoDeServicioDTO objetivoDeServicioDTO = objetivoDeServicioMapper.toDto(objetivoDeServicio);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restObjetivoDeServicioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(objetivoDeServicioDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ObjetivoDeServicio in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTipoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        objetivoDeServicio.setTipo(null);

        // Create the ObjetivoDeServicio, which fails.
        ObjetivoDeServicioDTO objetivoDeServicioDTO = objetivoDeServicioMapper.toDto(objetivoDeServicio);

        restObjetivoDeServicioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(objetivoDeServicioDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSeveridadAplicableIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        objetivoDeServicio.setSeveridadAplicable(null);

        // Create the ObjetivoDeServicio, which fails.
        ObjetivoDeServicioDTO objetivoDeServicioDTO = objetivoDeServicioMapper.toDto(objetivoDeServicio);

        restObjetivoDeServicioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(objetivoDeServicioDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMinutosObjetivoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        objetivoDeServicio.setMinutosObjetivo(null);

        // Create the ObjetivoDeServicio, which fails.
        ObjetivoDeServicioDTO objetivoDeServicioDTO = objetivoDeServicioMapper.toDto(objetivoDeServicio);

        restObjetivoDeServicioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(objetivoDeServicioDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllObjetivoDeServicios() throws Exception {
        // Initialize the database
        insertedObjetivoDeServicio = objetivoDeServicioRepository.saveAndFlush(objetivoDeServicio);

        // Get all the objetivoDeServicioList
        restObjetivoDeServicioMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(objetivoDeServicio.getId().intValue())))
            .andExpect(jsonPath("$.[*].tipo").value(hasItem(DEFAULT_TIPO.toString())))
            .andExpect(jsonPath("$.[*].severidadAplicable").value(hasItem(DEFAULT_SEVERIDAD_APLICABLE.toString())))
            .andExpect(jsonPath("$.[*].minutosObjetivo").value(hasItem(DEFAULT_MINUTOS_OBJETIVO)))
            .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllObjetivoDeServiciosWithEagerRelationshipsIsEnabled() throws Exception {
        when(objetivoDeServicioServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restObjetivoDeServicioMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(objetivoDeServicioServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllObjetivoDeServiciosWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(objetivoDeServicioServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restObjetivoDeServicioMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(objetivoDeServicioRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getObjetivoDeServicio() throws Exception {
        // Initialize the database
        insertedObjetivoDeServicio = objetivoDeServicioRepository.saveAndFlush(objetivoDeServicio);

        // Get the objetivoDeServicio
        restObjetivoDeServicioMockMvc
            .perform(get(ENTITY_API_URL_ID, objetivoDeServicio.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(objetivoDeServicio.getId().intValue()))
            .andExpect(jsonPath("$.tipo").value(DEFAULT_TIPO.toString()))
            .andExpect(jsonPath("$.severidadAplicable").value(DEFAULT_SEVERIDAD_APLICABLE.toString()))
            .andExpect(jsonPath("$.minutosObjetivo").value(DEFAULT_MINUTOS_OBJETIVO))
            .andExpect(jsonPath("$.descripcion").value(DEFAULT_DESCRIPCION));
    }

    @Test
    @Transactional
    void getNonExistingObjetivoDeServicio() throws Exception {
        // Get the objetivoDeServicio
        restObjetivoDeServicioMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingObjetivoDeServicio() throws Exception {
        // Initialize the database
        insertedObjetivoDeServicio = objetivoDeServicioRepository.saveAndFlush(objetivoDeServicio);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the objetivoDeServicio
        ObjetivoDeServicio updatedObjetivoDeServicio = objetivoDeServicioRepository.findById(objetivoDeServicio.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedObjetivoDeServicio are not directly saved in db
        em.detach(updatedObjetivoDeServicio);
        updatedObjetivoDeServicio
            .tipo(UPDATED_TIPO)
            .severidadAplicable(UPDATED_SEVERIDAD_APLICABLE)
            .minutosObjetivo(UPDATED_MINUTOS_OBJETIVO)
            .descripcion(UPDATED_DESCRIPCION);
        ObjetivoDeServicioDTO objetivoDeServicioDTO = objetivoDeServicioMapper.toDto(updatedObjetivoDeServicio);

        restObjetivoDeServicioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, objetivoDeServicioDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(objetivoDeServicioDTO))
            )
            .andExpect(status().isOk());

        // Validate the ObjetivoDeServicio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedObjetivoDeServicioToMatchAllProperties(updatedObjetivoDeServicio);
    }

    @Test
    @Transactional
    void putNonExistingObjetivoDeServicio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        objetivoDeServicio.setId(longCount.incrementAndGet());

        // Create the ObjetivoDeServicio
        ObjetivoDeServicioDTO objetivoDeServicioDTO = objetivoDeServicioMapper.toDto(objetivoDeServicio);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restObjetivoDeServicioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, objetivoDeServicioDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(objetivoDeServicioDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ObjetivoDeServicio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchObjetivoDeServicio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        objetivoDeServicio.setId(longCount.incrementAndGet());

        // Create the ObjetivoDeServicio
        ObjetivoDeServicioDTO objetivoDeServicioDTO = objetivoDeServicioMapper.toDto(objetivoDeServicio);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restObjetivoDeServicioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(objetivoDeServicioDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ObjetivoDeServicio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamObjetivoDeServicio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        objetivoDeServicio.setId(longCount.incrementAndGet());

        // Create the ObjetivoDeServicio
        ObjetivoDeServicioDTO objetivoDeServicioDTO = objetivoDeServicioMapper.toDto(objetivoDeServicio);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restObjetivoDeServicioMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(objetivoDeServicioDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ObjetivoDeServicio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateObjetivoDeServicioWithPatch() throws Exception {
        // Initialize the database
        insertedObjetivoDeServicio = objetivoDeServicioRepository.saveAndFlush(objetivoDeServicio);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the objetivoDeServicio using partial update
        ObjetivoDeServicio partialUpdatedObjetivoDeServicio = new ObjetivoDeServicio();
        partialUpdatedObjetivoDeServicio.setId(objetivoDeServicio.getId());

        partialUpdatedObjetivoDeServicio.descripcion(UPDATED_DESCRIPCION);

        restObjetivoDeServicioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedObjetivoDeServicio.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedObjetivoDeServicio))
            )
            .andExpect(status().isOk());

        // Validate the ObjetivoDeServicio in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertObjetivoDeServicioUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedObjetivoDeServicio, objetivoDeServicio),
            getPersistedObjetivoDeServicio(objetivoDeServicio)
        );
    }

    @Test
    @Transactional
    void fullUpdateObjetivoDeServicioWithPatch() throws Exception {
        // Initialize the database
        insertedObjetivoDeServicio = objetivoDeServicioRepository.saveAndFlush(objetivoDeServicio);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the objetivoDeServicio using partial update
        ObjetivoDeServicio partialUpdatedObjetivoDeServicio = new ObjetivoDeServicio();
        partialUpdatedObjetivoDeServicio.setId(objetivoDeServicio.getId());

        partialUpdatedObjetivoDeServicio
            .tipo(UPDATED_TIPO)
            .severidadAplicable(UPDATED_SEVERIDAD_APLICABLE)
            .minutosObjetivo(UPDATED_MINUTOS_OBJETIVO)
            .descripcion(UPDATED_DESCRIPCION);

        restObjetivoDeServicioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedObjetivoDeServicio.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedObjetivoDeServicio))
            )
            .andExpect(status().isOk());

        // Validate the ObjetivoDeServicio in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertObjetivoDeServicioUpdatableFieldsEquals(
            partialUpdatedObjetivoDeServicio,
            getPersistedObjetivoDeServicio(partialUpdatedObjetivoDeServicio)
        );
    }

    @Test
    @Transactional
    void patchNonExistingObjetivoDeServicio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        objetivoDeServicio.setId(longCount.incrementAndGet());

        // Create the ObjetivoDeServicio
        ObjetivoDeServicioDTO objetivoDeServicioDTO = objetivoDeServicioMapper.toDto(objetivoDeServicio);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restObjetivoDeServicioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, objetivoDeServicioDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(objetivoDeServicioDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ObjetivoDeServicio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchObjetivoDeServicio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        objetivoDeServicio.setId(longCount.incrementAndGet());

        // Create the ObjetivoDeServicio
        ObjetivoDeServicioDTO objetivoDeServicioDTO = objetivoDeServicioMapper.toDto(objetivoDeServicio);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restObjetivoDeServicioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(objetivoDeServicioDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ObjetivoDeServicio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamObjetivoDeServicio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        objetivoDeServicio.setId(longCount.incrementAndGet());

        // Create the ObjetivoDeServicio
        ObjetivoDeServicioDTO objetivoDeServicioDTO = objetivoDeServicioMapper.toDto(objetivoDeServicio);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restObjetivoDeServicioMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(objetivoDeServicioDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ObjetivoDeServicio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteObjetivoDeServicio() throws Exception {
        // Initialize the database
        insertedObjetivoDeServicio = objetivoDeServicioRepository.saveAndFlush(objetivoDeServicio);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the objetivoDeServicio
        restObjetivoDeServicioMockMvc
            .perform(delete(ENTITY_API_URL_ID, objetivoDeServicio.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return objetivoDeServicioRepository.count();
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

    protected ObjetivoDeServicio getPersistedObjetivoDeServicio(ObjetivoDeServicio objetivoDeServicio) {
        return objetivoDeServicioRepository.findById(objetivoDeServicio.getId()).orElseThrow();
    }

    protected void assertPersistedObjetivoDeServicioToMatchAllProperties(ObjetivoDeServicio expectedObjetivoDeServicio) {
        assertObjetivoDeServicioAllPropertiesEquals(expectedObjetivoDeServicio, getPersistedObjetivoDeServicio(expectedObjetivoDeServicio));
    }

    protected void assertPersistedObjetivoDeServicioToMatchUpdatableProperties(ObjetivoDeServicio expectedObjetivoDeServicio) {
        assertObjetivoDeServicioAllUpdatablePropertiesEquals(
            expectedObjetivoDeServicio,
            getPersistedObjetivoDeServicio(expectedObjetivoDeServicio)
        );
    }
}
