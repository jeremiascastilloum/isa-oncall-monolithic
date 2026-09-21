package ar.edu.um.isa.oncall.web.rest;

import static ar.edu.um.isa.oncall.domain.IncidenteAsserts.*;
import static ar.edu.um.isa.oncall.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ar.edu.um.isa.oncall.IntegrationTest;
import ar.edu.um.isa.oncall.domain.Incidente;
import ar.edu.um.isa.oncall.domain.Servicio;
import ar.edu.um.isa.oncall.domain.User;
import ar.edu.um.isa.oncall.domain.enumeration.EstadoIncidente;
import ar.edu.um.isa.oncall.domain.enumeration.Severidad;
import ar.edu.um.isa.oncall.repository.IncidenteRepository;
import ar.edu.um.isa.oncall.repository.UserRepository;
import ar.edu.um.isa.oncall.service.IncidenteService;
import ar.edu.um.isa.oncall.service.dto.IncidenteDTO;
import ar.edu.um.isa.oncall.service.mapper.IncidenteMapper;
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
 * Integration tests for the {@link IncidenteResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class IncidenteResourceIT {

    private static final String DEFAULT_TITULO = "AAAAAAAAAA";
    private static final String UPDATED_TITULO = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPCION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPCION = "BBBBBBBBBB";

    private static final Severidad DEFAULT_SEVERIDAD = Severidad.SEV1;
    private static final Severidad UPDATED_SEVERIDAD = Severidad.SEV2;

    private static final EstadoIncidente DEFAULT_ESTADO = EstadoIncidente.ABIERTO;
    private static final EstadoIncidente UPDATED_ESTADO = EstadoIncidente.RECONOCIDO;

    private static final Instant DEFAULT_DETECTADO_EN = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DETECTADO_EN = Instant.ofEpochMilli(1701729143509L);

    private static final Instant DEFAULT_RECONOCIDO_EN = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RECONOCIDO_EN = Instant.ofEpochMilli(1701729143509L);

    private static final Instant DEFAULT_MITIGADO_EN = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_MITIGADO_EN = Instant.ofEpochMilli(1701729143509L);

    private static final Instant DEFAULT_RESUELTO_EN = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RESUELTO_EN = Instant.ofEpochMilli(1701729143509L);

    private static final Integer DEFAULT_USUARIOS_AFECTADOS = 0;
    private static final Integer UPDATED_USUARIOS_AFECTADOS = 1;
    private static final Integer SMALLER_USUARIOS_AFECTADOS = 0 - 1;

    private static final Boolean DEFAULT_CUMPLIO_OBJETIVO = false;
    private static final Boolean UPDATED_CUMPLIO_OBJETIVO = true;

    private static final String ENTITY_API_URL = "/api/incidentes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private IncidenteRepository incidenteRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private IncidenteRepository incidenteRepositoryMock;

    @Autowired
    private IncidenteMapper incidenteMapper;

    @Mock
    private IncidenteService incidenteServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restIncidenteMockMvc;

    private Incidente incidente;

    private Incidente insertedIncidente;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Incidente createEntity() {
        return new Incidente()
            .titulo(DEFAULT_TITULO)
            .descripcion(DEFAULT_DESCRIPCION)
            .severidad(DEFAULT_SEVERIDAD)
            .estado(DEFAULT_ESTADO)
            .detectadoEn(DEFAULT_DETECTADO_EN)
            .reconocidoEn(DEFAULT_RECONOCIDO_EN)
            .mitigadoEn(DEFAULT_MITIGADO_EN)
            .resueltoEn(DEFAULT_RESUELTO_EN)
            .usuariosAfectados(DEFAULT_USUARIOS_AFECTADOS)
            .cumplioObjetivo(DEFAULT_CUMPLIO_OBJETIVO);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Incidente createUpdatedEntity() {
        return new Incidente()
            .titulo(UPDATED_TITULO)
            .descripcion(UPDATED_DESCRIPCION)
            .severidad(UPDATED_SEVERIDAD)
            .estado(UPDATED_ESTADO)
            .detectadoEn(UPDATED_DETECTADO_EN)
            .reconocidoEn(UPDATED_RECONOCIDO_EN)
            .mitigadoEn(UPDATED_MITIGADO_EN)
            .resueltoEn(UPDATED_RESUELTO_EN)
            .usuariosAfectados(UPDATED_USUARIOS_AFECTADOS)
            .cumplioObjetivo(UPDATED_CUMPLIO_OBJETIVO);
    }

    @BeforeEach
    void initTest() {
        incidente = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedIncidente != null) {
            incidenteRepository.delete(insertedIncidente);
            insertedIncidente = null;
        }
    }

    @Test
    @Transactional
    void createIncidente() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Incidente
        IncidenteDTO incidenteDTO = incidenteMapper.toDto(incidente);
        var returnedIncidenteDTO = om.readValue(
            restIncidenteMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(incidenteDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            IncidenteDTO.class
        );

        // Validate the Incidente in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedIncidente = incidenteMapper.toEntity(returnedIncidenteDTO);
        assertIncidenteUpdatableFieldsEquals(returnedIncidente, getPersistedIncidente(returnedIncidente));

        insertedIncidente = returnedIncidente;
    }

    @Test
    @Transactional
    void createIncidenteWithExistingId() throws Exception {
        // Create the Incidente with an existing ID
        incidente.setId(1L);
        IncidenteDTO incidenteDTO = incidenteMapper.toDto(incidente);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restIncidenteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(incidenteDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Incidente in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTituloIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        incidente.setTitulo(null);

        // Create the Incidente, which fails.
        IncidenteDTO incidenteDTO = incidenteMapper.toDto(incidente);

        restIncidenteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(incidenteDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSeveridadIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        incidente.setSeveridad(null);

        // Create the Incidente, which fails.
        IncidenteDTO incidenteDTO = incidenteMapper.toDto(incidente);

        restIncidenteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(incidenteDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEstadoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        incidente.setEstado(null);

        // Create the Incidente, which fails.
        IncidenteDTO incidenteDTO = incidenteMapper.toDto(incidente);

        restIncidenteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(incidenteDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDetectadoEnIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        incidente.setDetectadoEn(null);

        // Create the Incidente, which fails.
        IncidenteDTO incidenteDTO = incidenteMapper.toDto(incidente);

        restIncidenteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(incidenteDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllIncidentes() throws Exception {
        // Initialize the database
        insertedIncidente = incidenteRepository.saveAndFlush(incidente);

        // Get all the incidenteList
        restIncidenteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(incidente.getId().intValue())))
            .andExpect(jsonPath("$.[*].titulo").value(hasItem(DEFAULT_TITULO)))
            .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION)))
            .andExpect(jsonPath("$.[*].severidad").value(hasItem(DEFAULT_SEVERIDAD.toString())))
            .andExpect(jsonPath("$.[*].estado").value(hasItem(DEFAULT_ESTADO.toString())))
            .andExpect(jsonPath("$.[*].detectadoEn").value(hasItem(DEFAULT_DETECTADO_EN.toString())))
            .andExpect(jsonPath("$.[*].reconocidoEn").value(hasItem(DEFAULT_RECONOCIDO_EN.toString())))
            .andExpect(jsonPath("$.[*].mitigadoEn").value(hasItem(DEFAULT_MITIGADO_EN.toString())))
            .andExpect(jsonPath("$.[*].resueltoEn").value(hasItem(DEFAULT_RESUELTO_EN.toString())))
            .andExpect(jsonPath("$.[*].usuariosAfectados").value(hasItem(DEFAULT_USUARIOS_AFECTADOS)))
            .andExpect(jsonPath("$.[*].cumplioObjetivo").value(hasItem(DEFAULT_CUMPLIO_OBJETIVO)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllIncidentesWithEagerRelationshipsIsEnabled() throws Exception {
        when(incidenteServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restIncidenteMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(incidenteServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllIncidentesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(incidenteServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restIncidenteMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(incidenteRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getIncidente() throws Exception {
        // Initialize the database
        insertedIncidente = incidenteRepository.saveAndFlush(incidente);

        // Get the incidente
        restIncidenteMockMvc
            .perform(get(ENTITY_API_URL_ID, incidente.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(incidente.getId().intValue()))
            .andExpect(jsonPath("$.titulo").value(DEFAULT_TITULO))
            .andExpect(jsonPath("$.descripcion").value(DEFAULT_DESCRIPCION))
            .andExpect(jsonPath("$.severidad").value(DEFAULT_SEVERIDAD.toString()))
            .andExpect(jsonPath("$.estado").value(DEFAULT_ESTADO.toString()))
            .andExpect(jsonPath("$.detectadoEn").value(DEFAULT_DETECTADO_EN.toString()))
            .andExpect(jsonPath("$.reconocidoEn").value(DEFAULT_RECONOCIDO_EN.toString()))
            .andExpect(jsonPath("$.mitigadoEn").value(DEFAULT_MITIGADO_EN.toString()))
            .andExpect(jsonPath("$.resueltoEn").value(DEFAULT_RESUELTO_EN.toString()))
            .andExpect(jsonPath("$.usuariosAfectados").value(DEFAULT_USUARIOS_AFECTADOS))
            .andExpect(jsonPath("$.cumplioObjetivo").value(DEFAULT_CUMPLIO_OBJETIVO));
    }

    @Test
    @Transactional
    void getIncidentesByIdFiltering() throws Exception {
        // Initialize the database
        insertedIncidente = incidenteRepository.saveAndFlush(incidente);

        Long id = incidente.getId();

        defaultIncidenteFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultIncidenteFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultIncidenteFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllIncidentesByTituloIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedIncidente = incidenteRepository.saveAndFlush(incidente);

        // Get all the incidenteList where titulo equals to
        defaultIncidenteFiltering("titulo.equals=" + DEFAULT_TITULO, "titulo.equals=" + UPDATED_TITULO);
    }

    @Test
    @Transactional
    void getAllIncidentesByTituloIsInShouldWork() throws Exception {
        // Initialize the database
        insertedIncidente = incidenteRepository.saveAndFlush(incidente);

        // Get all the incidenteList where titulo in
        defaultIncidenteFiltering("titulo.in=" + DEFAULT_TITULO + "," + UPDATED_TITULO, "titulo.in=" + UPDATED_TITULO);
    }

    @Test
    @Transactional
    void getAllIncidentesByTituloIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedIncidente = incidenteRepository.saveAndFlush(incidente);

        // Get all the incidenteList where titulo is not null
        defaultIncidenteFiltering("titulo.specified=true", "titulo.specified=false");
    }

    @Test
    @Transactional
    void getAllIncidentesByTituloContainsSomething() throws Exception {
        // Initialize the database
        insertedIncidente = incidenteRepository.saveAndFlush(incidente);

        // Get all the incidenteList where titulo contains
        defaultIncidenteFiltering("titulo.contains=" + DEFAULT_TITULO, "titulo.contains=" + UPDATED_TITULO);
    }

    @Test
    @Transactional
    void getAllIncidentesByTituloNotContainsSomething() throws Exception {
        // Initialize the database
        insertedIncidente = incidenteRepository.saveAndFlush(incidente);

        // Get all the incidenteList where titulo does not contain
        defaultIncidenteFiltering("titulo.doesNotContain=" + UPDATED_TITULO, "titulo.doesNotContain=" + DEFAULT_TITULO);
    }

    @Test
    @Transactional
    void getAllIncidentesByDescripcionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedIncidente = incidenteRepository.saveAndFlush(incidente);

        // Get all the incidenteList where descripcion equals to
        defaultIncidenteFiltering("descripcion.equals=" + DEFAULT_DESCRIPCION, "descripcion.equals=" + UPDATED_DESCRIPCION);
    }

    @Test
    @Transactional
    void getAllIncidentesByDescripcionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedIncidente = incidenteRepository.saveAndFlush(incidente);

        // Get all the incidenteList where descripcion in
        defaultIncidenteFiltering(
            "descripcion.in=" + DEFAULT_DESCRIPCION + "," + UPDATED_DESCRIPCION,
            "descripcion.in=" + UPDATED_DESCRIPCION
        );
    }

    @Test
    @Transactional
    void getAllIncidentesByDescripcionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedIncidente = incidenteRepository.saveAndFlush(incidente);

        // Get all the incidenteList where descripcion is not null
        defaultIncidenteFiltering("descripcion.specified=true", "descripcion.specified=false");
    }

    @Test
    @Transactional
    void getAllIncidentesByDescripcionContainsSomething() throws Exception {
        // Initialize the database
        insertedIncidente = incidenteRepository.saveAndFlush(incidente);

        // Get all the incidenteList where descripcion contains
        defaultIncidenteFiltering("descripcion.contains=" + DEFAULT_DESCRIPCION, "descripcion.contains=" + UPDATED_DESCRIPCION);
    }

    @Test
    @Transactional
    void getAllIncidentesByDescripcionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedIncidente = incidenteRepository.saveAndFlush(incidente);

        // Get all the incidenteList where descripcion does not contain
        defaultIncidenteFiltering("descripcion.doesNotContain=" + UPDATED_DESCRIPCION, "descripcion.doesNotContain=" + DEFAULT_DESCRIPCION);
    }

    @Test
    @Transactional
    void getAllIncidentesBySeveridadIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedIncidente = incidenteRepository.saveAndFlush(incidente);

        // Get all the incidenteList where severidad equals to
        defaultIncidenteFiltering("severidad.equals=" + DEFAULT_SEVERIDAD, "severidad.equals=" + UPDATED_SEVERIDAD);
    }

    @Test
    @Transactional
    void getAllIncidentesBySeveridadIsInShouldWork() throws Exception {
        // Initialize the database
        insertedIncidente = incidenteRepository.saveAndFlush(incidente);

        // Get all the incidenteList where severidad in
        defaultIncidenteFiltering("severidad.in=" + DEFAULT_SEVERIDAD + "," + UPDATED_SEVERIDAD, "severidad.in=" + UPDATED_SEVERIDAD);
    }

    @Test
    @Transactional
    void getAllIncidentesBySeveridadIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedIncidente = incidenteRepository.saveAndFlush(incidente);

        // Get all the incidenteList where severidad is not null
        defaultIncidenteFiltering("severidad.specified=true", "severidad.specified=false");
    }

    @Test
    @Transactional
    void getAllIncidentesByEstadoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedIncidente = incidenteRepository.saveAndFlush(incidente);

        // Get all the incidenteList where estado equals to
        defaultIncidenteFiltering("estado.equals=" + DEFAULT_ESTADO, "estado.equals=" + UPDATED_ESTADO);
    }

    @Test
    @Transactional
    void getAllIncidentesByEstadoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedIncidente = incidenteRepository.saveAndFlush(incidente);

        // Get all the incidenteList where estado in
        defaultIncidenteFiltering("estado.in=" + DEFAULT_ESTADO + "," + UPDATED_ESTADO, "estado.in=" + UPDATED_ESTADO);
    }

    @Test
    @Transactional
    void getAllIncidentesByEstadoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedIncidente = incidenteRepository.saveAndFlush(incidente);

        // Get all the incidenteList where estado is not null
        defaultIncidenteFiltering("estado.specified=true", "estado.specified=false");
    }

    @Test
    @Transactional
    void getAllIncidentesByDetectadoEnIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedIncidente = incidenteRepository.saveAndFlush(incidente);

        // Get all the incidenteList where detectadoEn equals to
        defaultIncidenteFiltering("detectadoEn.equals=" + DEFAULT_DETECTADO_EN, "detectadoEn.equals=" + UPDATED_DETECTADO_EN);
    }

    @Test
    @Transactional
    void getAllIncidentesByDetectadoEnIsInShouldWork() throws Exception {
        // Initialize the database
        insertedIncidente = incidenteRepository.saveAndFlush(incidente);

        // Get all the incidenteList where detectadoEn in
        defaultIncidenteFiltering(
            "detectadoEn.in=" + DEFAULT_DETECTADO_EN + "," + UPDATED_DETECTADO_EN,
            "detectadoEn.in=" + UPDATED_DETECTADO_EN
        );
    }

    @Test
    @Transactional
    void getAllIncidentesByDetectadoEnIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedIncidente = incidenteRepository.saveAndFlush(incidente);

        // Get all the incidenteList where detectadoEn is not null
        defaultIncidenteFiltering("detectadoEn.specified=true", "detectadoEn.specified=false");
    }

    @Test
    @Transactional
    void getAllIncidentesByReconocidoEnIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedIncidente = incidenteRepository.saveAndFlush(incidente);

        // Get all the incidenteList where reconocidoEn equals to
        defaultIncidenteFiltering("reconocidoEn.equals=" + DEFAULT_RECONOCIDO_EN, "reconocidoEn.equals=" + UPDATED_RECONOCIDO_EN);
    }

    @Test
    @Transactional
    void getAllIncidentesByReconocidoEnIsInShouldWork() throws Exception {
        // Initialize the database
        insertedIncidente = incidenteRepository.saveAndFlush(incidente);

        // Get all the incidenteList where reconocidoEn in
        defaultIncidenteFiltering(
            "reconocidoEn.in=" + DEFAULT_RECONOCIDO_EN + "," + UPDATED_RECONOCIDO_EN,
            "reconocidoEn.in=" + UPDATED_RECONOCIDO_EN
        );
    }

    @Test
    @Transactional
    void getAllIncidentesByReconocidoEnIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedIncidente = incidenteRepository.saveAndFlush(incidente);

        // Get all the incidenteList where reconocidoEn is not null
        defaultIncidenteFiltering("reconocidoEn.specified=true", "reconocidoEn.specified=false");
    }

    @Test
    @Transactional
    void getAllIncidentesByMitigadoEnIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedIncidente = incidenteRepository.saveAndFlush(incidente);

        // Get all the incidenteList where mitigadoEn equals to
        defaultIncidenteFiltering("mitigadoEn.equals=" + DEFAULT_MITIGADO_EN, "mitigadoEn.equals=" + UPDATED_MITIGADO_EN);
    }

    @Test
    @Transactional
    void getAllIncidentesByMitigadoEnIsInShouldWork() throws Exception {
        // Initialize the database
        insertedIncidente = incidenteRepository.saveAndFlush(incidente);

        // Get all the incidenteList where mitigadoEn in
        defaultIncidenteFiltering(
            "mitigadoEn.in=" + DEFAULT_MITIGADO_EN + "," + UPDATED_MITIGADO_EN,
            "mitigadoEn.in=" + UPDATED_MITIGADO_EN
        );
    }

    @Test
    @Transactional
    void getAllIncidentesByMitigadoEnIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedIncidente = incidenteRepository.saveAndFlush(incidente);

        // Get all the incidenteList where mitigadoEn is not null
        defaultIncidenteFiltering("mitigadoEn.specified=true", "mitigadoEn.specified=false");
    }

    @Test
    @Transactional
    void getAllIncidentesByResueltoEnIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedIncidente = incidenteRepository.saveAndFlush(incidente);

        // Get all the incidenteList where resueltoEn equals to
        defaultIncidenteFiltering("resueltoEn.equals=" + DEFAULT_RESUELTO_EN, "resueltoEn.equals=" + UPDATED_RESUELTO_EN);
    }

    @Test
    @Transactional
    void getAllIncidentesByResueltoEnIsInShouldWork() throws Exception {
        // Initialize the database
        insertedIncidente = incidenteRepository.saveAndFlush(incidente);

        // Get all the incidenteList where resueltoEn in
        defaultIncidenteFiltering(
            "resueltoEn.in=" + DEFAULT_RESUELTO_EN + "," + UPDATED_RESUELTO_EN,
            "resueltoEn.in=" + UPDATED_RESUELTO_EN
        );
    }

    @Test
    @Transactional
    void getAllIncidentesByResueltoEnIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedIncidente = incidenteRepository.saveAndFlush(incidente);

        // Get all the incidenteList where resueltoEn is not null
        defaultIncidenteFiltering("resueltoEn.specified=true", "resueltoEn.specified=false");
    }

    @Test
    @Transactional
    void getAllIncidentesByUsuariosAfectadosIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedIncidente = incidenteRepository.saveAndFlush(incidente);

        // Get all the incidenteList where usuariosAfectados equals to
        defaultIncidenteFiltering(
            "usuariosAfectados.equals=" + DEFAULT_USUARIOS_AFECTADOS,
            "usuariosAfectados.equals=" + UPDATED_USUARIOS_AFECTADOS
        );
    }

    @Test
    @Transactional
    void getAllIncidentesByUsuariosAfectadosIsInShouldWork() throws Exception {
        // Initialize the database
        insertedIncidente = incidenteRepository.saveAndFlush(incidente);

        // Get all the incidenteList where usuariosAfectados in
        defaultIncidenteFiltering(
            "usuariosAfectados.in=" + DEFAULT_USUARIOS_AFECTADOS + "," + UPDATED_USUARIOS_AFECTADOS,
            "usuariosAfectados.in=" + UPDATED_USUARIOS_AFECTADOS
        );
    }

    @Test
    @Transactional
    void getAllIncidentesByUsuariosAfectadosIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedIncidente = incidenteRepository.saveAndFlush(incidente);

        // Get all the incidenteList where usuariosAfectados is not null
        defaultIncidenteFiltering("usuariosAfectados.specified=true", "usuariosAfectados.specified=false");
    }

    @Test
    @Transactional
    void getAllIncidentesByUsuariosAfectadosIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedIncidente = incidenteRepository.saveAndFlush(incidente);

        // Get all the incidenteList where usuariosAfectados is greater than or equal to
        defaultIncidenteFiltering(
            "usuariosAfectados.greaterThanOrEqual=" + DEFAULT_USUARIOS_AFECTADOS,
            "usuariosAfectados.greaterThanOrEqual=" + UPDATED_USUARIOS_AFECTADOS
        );
    }

    @Test
    @Transactional
    void getAllIncidentesByUsuariosAfectadosIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedIncidente = incidenteRepository.saveAndFlush(incidente);

        // Get all the incidenteList where usuariosAfectados is less than or equal to
        defaultIncidenteFiltering(
            "usuariosAfectados.lessThanOrEqual=" + DEFAULT_USUARIOS_AFECTADOS,
            "usuariosAfectados.lessThanOrEqual=" + SMALLER_USUARIOS_AFECTADOS
        );
    }

    @Test
    @Transactional
    void getAllIncidentesByUsuariosAfectadosIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedIncidente = incidenteRepository.saveAndFlush(incidente);

        // Get all the incidenteList where usuariosAfectados is less than
        defaultIncidenteFiltering(
            "usuariosAfectados.lessThan=" + UPDATED_USUARIOS_AFECTADOS,
            "usuariosAfectados.lessThan=" + DEFAULT_USUARIOS_AFECTADOS
        );
    }

    @Test
    @Transactional
    void getAllIncidentesByUsuariosAfectadosIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedIncidente = incidenteRepository.saveAndFlush(incidente);

        // Get all the incidenteList where usuariosAfectados is greater than
        defaultIncidenteFiltering(
            "usuariosAfectados.greaterThan=" + SMALLER_USUARIOS_AFECTADOS,
            "usuariosAfectados.greaterThan=" + DEFAULT_USUARIOS_AFECTADOS
        );
    }

    @Test
    @Transactional
    void getAllIncidentesByCumplioObjetivoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedIncidente = incidenteRepository.saveAndFlush(incidente);

        // Get all the incidenteList where cumplioObjetivo equals to
        defaultIncidenteFiltering(
            "cumplioObjetivo.equals=" + DEFAULT_CUMPLIO_OBJETIVO,
            "cumplioObjetivo.equals=" + UPDATED_CUMPLIO_OBJETIVO
        );
    }

    @Test
    @Transactional
    void getAllIncidentesByCumplioObjetivoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedIncidente = incidenteRepository.saveAndFlush(incidente);

        // Get all the incidenteList where cumplioObjetivo in
        defaultIncidenteFiltering(
            "cumplioObjetivo.in=" + DEFAULT_CUMPLIO_OBJETIVO + "," + UPDATED_CUMPLIO_OBJETIVO,
            "cumplioObjetivo.in=" + UPDATED_CUMPLIO_OBJETIVO
        );
    }

    @Test
    @Transactional
    void getAllIncidentesByCumplioObjetivoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedIncidente = incidenteRepository.saveAndFlush(incidente);

        // Get all the incidenteList where cumplioObjetivo is not null
        defaultIncidenteFiltering("cumplioObjetivo.specified=true", "cumplioObjetivo.specified=false");
    }

    @Test
    @Transactional
    void getAllIncidentesByComandanteIsEqualToSomething() throws Exception {
        User comandante;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            incidenteRepository.saveAndFlush(incidente);
            comandante = UserResourceIT.createEntity();
        } else {
            comandante = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(comandante);
        em.flush();
        incidente.setComandante(comandante);
        incidenteRepository.saveAndFlush(incidente);
        Long comandanteId = comandante.getId();
        // Get all the incidenteList where comandante equals to comandanteId
        defaultIncidenteShouldBeFound("comandanteId.equals=" + comandanteId);

        // Get all the incidenteList where comandante equals to (comandanteId + 1)
        defaultIncidenteShouldNotBeFound("comandanteId.equals=" + (comandanteId + 1));
    }

    @Test
    @Transactional
    void getAllIncidentesByServicioIsEqualToSomething() throws Exception {
        Servicio servicio;
        if (TestUtil.findAll(em, Servicio.class).isEmpty()) {
            incidenteRepository.saveAndFlush(incidente);
            servicio = ServicioResourceIT.createEntity(em);
        } else {
            servicio = TestUtil.findAll(em, Servicio.class).get(0);
        }
        em.persist(servicio);
        em.flush();
        incidente.addServicio(servicio);
        incidenteRepository.saveAndFlush(incidente);
        Long servicioId = servicio.getId();
        // Get all the incidenteList where servicio equals to servicioId
        defaultIncidenteShouldBeFound("servicioId.equals=" + servicioId);

        // Get all the incidenteList where servicio equals to (servicioId + 1)
        defaultIncidenteShouldNotBeFound("servicioId.equals=" + (servicioId + 1));
    }

    private void defaultIncidenteFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultIncidenteShouldBeFound(shouldBeFound);
        defaultIncidenteShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultIncidenteShouldBeFound(String filter) throws Exception {
        restIncidenteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(incidente.getId().intValue())))
            .andExpect(jsonPath("$.[*].titulo").value(hasItem(DEFAULT_TITULO)))
            .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION)))
            .andExpect(jsonPath("$.[*].severidad").value(hasItem(DEFAULT_SEVERIDAD.toString())))
            .andExpect(jsonPath("$.[*].estado").value(hasItem(DEFAULT_ESTADO.toString())))
            .andExpect(jsonPath("$.[*].detectadoEn").value(hasItem(DEFAULT_DETECTADO_EN.toString())))
            .andExpect(jsonPath("$.[*].reconocidoEn").value(hasItem(DEFAULT_RECONOCIDO_EN.toString())))
            .andExpect(jsonPath("$.[*].mitigadoEn").value(hasItem(DEFAULT_MITIGADO_EN.toString())))
            .andExpect(jsonPath("$.[*].resueltoEn").value(hasItem(DEFAULT_RESUELTO_EN.toString())))
            .andExpect(jsonPath("$.[*].usuariosAfectados").value(hasItem(DEFAULT_USUARIOS_AFECTADOS)))
            .andExpect(jsonPath("$.[*].cumplioObjetivo").value(hasItem(DEFAULT_CUMPLIO_OBJETIVO)));

        // Check, that the count call also returns 1
        restIncidenteMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultIncidenteShouldNotBeFound(String filter) throws Exception {
        restIncidenteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restIncidenteMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingIncidente() throws Exception {
        // Get the incidente
        restIncidenteMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingIncidente() throws Exception {
        // Initialize the database
        insertedIncidente = incidenteRepository.saveAndFlush(incidente);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the incidente
        Incidente updatedIncidente = incidenteRepository.findById(incidente.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedIncidente are not directly saved in db
        em.detach(updatedIncidente);
        updatedIncidente
            .titulo(UPDATED_TITULO)
            .descripcion(UPDATED_DESCRIPCION)
            .severidad(UPDATED_SEVERIDAD)
            .estado(UPDATED_ESTADO)
            .detectadoEn(UPDATED_DETECTADO_EN)
            .reconocidoEn(UPDATED_RECONOCIDO_EN)
            .mitigadoEn(UPDATED_MITIGADO_EN)
            .resueltoEn(UPDATED_RESUELTO_EN)
            .usuariosAfectados(UPDATED_USUARIOS_AFECTADOS)
            .cumplioObjetivo(UPDATED_CUMPLIO_OBJETIVO);
        IncidenteDTO incidenteDTO = incidenteMapper.toDto(updatedIncidente);

        restIncidenteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, incidenteDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(incidenteDTO))
            )
            .andExpect(status().isOk());

        // Validate the Incidente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedIncidenteToMatchAllProperties(updatedIncidente);
    }

    @Test
    @Transactional
    void putNonExistingIncidente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        incidente.setId(longCount.incrementAndGet());

        // Create the Incidente
        IncidenteDTO incidenteDTO = incidenteMapper.toDto(incidente);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIncidenteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, incidenteDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(incidenteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Incidente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchIncidente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        incidente.setId(longCount.incrementAndGet());

        // Create the Incidente
        IncidenteDTO incidenteDTO = incidenteMapper.toDto(incidente);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIncidenteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(incidenteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Incidente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamIncidente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        incidente.setId(longCount.incrementAndGet());

        // Create the Incidente
        IncidenteDTO incidenteDTO = incidenteMapper.toDto(incidente);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIncidenteMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(incidenteDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Incidente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateIncidenteWithPatch() throws Exception {
        // Initialize the database
        insertedIncidente = incidenteRepository.saveAndFlush(incidente);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the incidente using partial update
        Incidente partialUpdatedIncidente = new Incidente();
        partialUpdatedIncidente.setId(incidente.getId());

        partialUpdatedIncidente
            .titulo(UPDATED_TITULO)
            .severidad(UPDATED_SEVERIDAD)
            .detectadoEn(UPDATED_DETECTADO_EN)
            .mitigadoEn(UPDATED_MITIGADO_EN)
            .resueltoEn(UPDATED_RESUELTO_EN)
            .usuariosAfectados(UPDATED_USUARIOS_AFECTADOS)
            .cumplioObjetivo(UPDATED_CUMPLIO_OBJETIVO);

        restIncidenteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedIncidente.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedIncidente))
            )
            .andExpect(status().isOk());

        // Validate the Incidente in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertIncidenteUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedIncidente, incidente),
            getPersistedIncidente(incidente)
        );
    }

    @Test
    @Transactional
    void fullUpdateIncidenteWithPatch() throws Exception {
        // Initialize the database
        insertedIncidente = incidenteRepository.saveAndFlush(incidente);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the incidente using partial update
        Incidente partialUpdatedIncidente = new Incidente();
        partialUpdatedIncidente.setId(incidente.getId());

        partialUpdatedIncidente
            .titulo(UPDATED_TITULO)
            .descripcion(UPDATED_DESCRIPCION)
            .severidad(UPDATED_SEVERIDAD)
            .estado(UPDATED_ESTADO)
            .detectadoEn(UPDATED_DETECTADO_EN)
            .reconocidoEn(UPDATED_RECONOCIDO_EN)
            .mitigadoEn(UPDATED_MITIGADO_EN)
            .resueltoEn(UPDATED_RESUELTO_EN)
            .usuariosAfectados(UPDATED_USUARIOS_AFECTADOS)
            .cumplioObjetivo(UPDATED_CUMPLIO_OBJETIVO);

        restIncidenteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedIncidente.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedIncidente))
            )
            .andExpect(status().isOk());

        // Validate the Incidente in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertIncidenteUpdatableFieldsEquals(partialUpdatedIncidente, getPersistedIncidente(partialUpdatedIncidente));
    }

    @Test
    @Transactional
    void patchNonExistingIncidente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        incidente.setId(longCount.incrementAndGet());

        // Create the Incidente
        IncidenteDTO incidenteDTO = incidenteMapper.toDto(incidente);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIncidenteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, incidenteDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(incidenteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Incidente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchIncidente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        incidente.setId(longCount.incrementAndGet());

        // Create the Incidente
        IncidenteDTO incidenteDTO = incidenteMapper.toDto(incidente);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIncidenteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(incidenteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Incidente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamIncidente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        incidente.setId(longCount.incrementAndGet());

        // Create the Incidente
        IncidenteDTO incidenteDTO = incidenteMapper.toDto(incidente);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIncidenteMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(incidenteDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Incidente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteIncidente() throws Exception {
        // Initialize the database
        insertedIncidente = incidenteRepository.saveAndFlush(incidente);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the incidente
        restIncidenteMockMvc
            .perform(delete(ENTITY_API_URL_ID, incidente.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return incidenteRepository.count();
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

    protected Incidente getPersistedIncidente(Incidente incidente) {
        return incidenteRepository.findById(incidente.getId()).orElseThrow();
    }

    protected void assertPersistedIncidenteToMatchAllProperties(Incidente expectedIncidente) {
        assertIncidenteAllPropertiesEquals(expectedIncidente, getPersistedIncidente(expectedIncidente));
    }

    protected void assertPersistedIncidenteToMatchUpdatableProperties(Incidente expectedIncidente) {
        assertIncidenteAllUpdatablePropertiesEquals(expectedIncidente, getPersistedIncidente(expectedIncidente));
    }
}
