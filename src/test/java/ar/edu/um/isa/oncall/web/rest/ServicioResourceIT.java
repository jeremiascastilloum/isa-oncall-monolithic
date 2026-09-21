package ar.edu.um.isa.oncall.web.rest;

import static ar.edu.um.isa.oncall.domain.ServicioAsserts.*;
import static ar.edu.um.isa.oncall.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ar.edu.um.isa.oncall.IntegrationTest;
import ar.edu.um.isa.oncall.domain.Equipo;
import ar.edu.um.isa.oncall.domain.Incidente;
import ar.edu.um.isa.oncall.domain.Servicio;
import ar.edu.um.isa.oncall.domain.enumeration.Criticidad;
import ar.edu.um.isa.oncall.domain.enumeration.Entorno;
import ar.edu.um.isa.oncall.repository.ServicioRepository;
import ar.edu.um.isa.oncall.service.ServicioService;
import ar.edu.um.isa.oncall.service.dto.ServicioDTO;
import ar.edu.um.isa.oncall.service.mapper.ServicioMapper;
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
 * Integration tests for the {@link ServicioResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ServicioResourceIT {

    private static final String DEFAULT_NOMBRE = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPCION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPCION = "BBBBBBBBBB";

    private static final Criticidad DEFAULT_CRITICIDAD = Criticidad.TIER1;
    private static final Criticidad UPDATED_CRITICIDAD = Criticidad.TIER2;

    private static final Entorno DEFAULT_ENTORNO = Entorno.PRODUCCION;
    private static final Entorno UPDATED_ENTORNO = Entorno.STAGING;

    private static final String DEFAULT_REPOSITORIO_URL = "AAAAAAAAAA";
    private static final String UPDATED_REPOSITORIO_URL = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVO = false;
    private static final Boolean UPDATED_ACTIVO = true;

    private static final String ENTITY_API_URL = "/api/servicios";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ServicioRepository servicioRepository;

    @Mock
    private ServicioRepository servicioRepositoryMock;

    @Autowired
    private ServicioMapper servicioMapper;

    @Mock
    private ServicioService servicioServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restServicioMockMvc;

    private Servicio servicio;

    private Servicio insertedServicio;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Servicio createEntity(EntityManager em) {
        Servicio servicio = new Servicio()
            .nombre(DEFAULT_NOMBRE)
            .descripcion(DEFAULT_DESCRIPCION)
            .criticidad(DEFAULT_CRITICIDAD)
            .entorno(DEFAULT_ENTORNO)
            .repositorioUrl(DEFAULT_REPOSITORIO_URL)
            .activo(DEFAULT_ACTIVO);
        // Add required entity
        Equipo equipo;
        if (TestUtil.findAll(em, Equipo.class).isEmpty()) {
            equipo = EquipoResourceIT.createEntity();
            em.persist(equipo);
            em.flush();
        } else {
            equipo = TestUtil.findAll(em, Equipo.class).get(0);
        }
        servicio.setEquipo(equipo);
        return servicio;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Servicio createUpdatedEntity(EntityManager em) {
        Servicio updatedServicio = new Servicio()
            .nombre(UPDATED_NOMBRE)
            .descripcion(UPDATED_DESCRIPCION)
            .criticidad(UPDATED_CRITICIDAD)
            .entorno(UPDATED_ENTORNO)
            .repositorioUrl(UPDATED_REPOSITORIO_URL)
            .activo(UPDATED_ACTIVO);
        // Add required entity
        Equipo equipo;
        if (TestUtil.findAll(em, Equipo.class).isEmpty()) {
            equipo = EquipoResourceIT.createUpdatedEntity();
            em.persist(equipo);
            em.flush();
        } else {
            equipo = TestUtil.findAll(em, Equipo.class).get(0);
        }
        updatedServicio.setEquipo(equipo);
        return updatedServicio;
    }

    @BeforeEach
    void initTest() {
        servicio = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedServicio != null) {
            servicioRepository.delete(insertedServicio);
            insertedServicio = null;
        }
    }

    @Test
    @Transactional
    void createServicio() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Servicio
        ServicioDTO servicioDTO = servicioMapper.toDto(servicio);
        var returnedServicioDTO = om.readValue(
            restServicioMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(servicioDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ServicioDTO.class
        );

        // Validate the Servicio in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedServicio = servicioMapper.toEntity(returnedServicioDTO);
        assertServicioUpdatableFieldsEquals(returnedServicio, getPersistedServicio(returnedServicio));

        insertedServicio = returnedServicio;
    }

    @Test
    @Transactional
    void createServicioWithExistingId() throws Exception {
        // Create the Servicio with an existing ID
        servicio.setId(1L);
        ServicioDTO servicioDTO = servicioMapper.toDto(servicio);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restServicioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(servicioDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Servicio in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNombreIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        servicio.setNombre(null);

        // Create the Servicio, which fails.
        ServicioDTO servicioDTO = servicioMapper.toDto(servicio);

        restServicioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(servicioDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCriticidadIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        servicio.setCriticidad(null);

        // Create the Servicio, which fails.
        ServicioDTO servicioDTO = servicioMapper.toDto(servicio);

        restServicioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(servicioDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEntornoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        servicio.setEntorno(null);

        // Create the Servicio, which fails.
        ServicioDTO servicioDTO = servicioMapper.toDto(servicio);

        restServicioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(servicioDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActivoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        servicio.setActivo(null);

        // Create the Servicio, which fails.
        ServicioDTO servicioDTO = servicioMapper.toDto(servicio);

        restServicioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(servicioDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllServicios() throws Exception {
        // Initialize the database
        insertedServicio = servicioRepository.saveAndFlush(servicio);

        // Get all the servicioList
        restServicioMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(servicio.getId().intValue())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE)))
            .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION)))
            .andExpect(jsonPath("$.[*].criticidad").value(hasItem(DEFAULT_CRITICIDAD.toString())))
            .andExpect(jsonPath("$.[*].entorno").value(hasItem(DEFAULT_ENTORNO.toString())))
            .andExpect(jsonPath("$.[*].repositorioUrl").value(hasItem(DEFAULT_REPOSITORIO_URL)))
            .andExpect(jsonPath("$.[*].activo").value(hasItem(DEFAULT_ACTIVO)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllServiciosWithEagerRelationshipsIsEnabled() throws Exception {
        when(servicioServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restServicioMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(servicioServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllServiciosWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(servicioServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restServicioMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(servicioRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getServicio() throws Exception {
        // Initialize the database
        insertedServicio = servicioRepository.saveAndFlush(servicio);

        // Get the servicio
        restServicioMockMvc
            .perform(get(ENTITY_API_URL_ID, servicio.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(servicio.getId().intValue()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE))
            .andExpect(jsonPath("$.descripcion").value(DEFAULT_DESCRIPCION))
            .andExpect(jsonPath("$.criticidad").value(DEFAULT_CRITICIDAD.toString()))
            .andExpect(jsonPath("$.entorno").value(DEFAULT_ENTORNO.toString()))
            .andExpect(jsonPath("$.repositorioUrl").value(DEFAULT_REPOSITORIO_URL))
            .andExpect(jsonPath("$.activo").value(DEFAULT_ACTIVO));
    }

    @Test
    @Transactional
    void getServiciosByIdFiltering() throws Exception {
        // Initialize the database
        insertedServicio = servicioRepository.saveAndFlush(servicio);

        Long id = servicio.getId();

        defaultServicioFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultServicioFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultServicioFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllServiciosByNombreIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedServicio = servicioRepository.saveAndFlush(servicio);

        // Get all the servicioList where nombre equals to
        defaultServicioFiltering("nombre.equals=" + DEFAULT_NOMBRE, "nombre.equals=" + UPDATED_NOMBRE);
    }

    @Test
    @Transactional
    void getAllServiciosByNombreIsInShouldWork() throws Exception {
        // Initialize the database
        insertedServicio = servicioRepository.saveAndFlush(servicio);

        // Get all the servicioList where nombre in
        defaultServicioFiltering("nombre.in=" + DEFAULT_NOMBRE + "," + UPDATED_NOMBRE, "nombre.in=" + UPDATED_NOMBRE);
    }

    @Test
    @Transactional
    void getAllServiciosByNombreIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedServicio = servicioRepository.saveAndFlush(servicio);

        // Get all the servicioList where nombre is not null
        defaultServicioFiltering("nombre.specified=true", "nombre.specified=false");
    }

    @Test
    @Transactional
    void getAllServiciosByNombreContainsSomething() throws Exception {
        // Initialize the database
        insertedServicio = servicioRepository.saveAndFlush(servicio);

        // Get all the servicioList where nombre contains
        defaultServicioFiltering("nombre.contains=" + DEFAULT_NOMBRE, "nombre.contains=" + UPDATED_NOMBRE);
    }

    @Test
    @Transactional
    void getAllServiciosByNombreNotContainsSomething() throws Exception {
        // Initialize the database
        insertedServicio = servicioRepository.saveAndFlush(servicio);

        // Get all the servicioList where nombre does not contain
        defaultServicioFiltering("nombre.doesNotContain=" + UPDATED_NOMBRE, "nombre.doesNotContain=" + DEFAULT_NOMBRE);
    }

    @Test
    @Transactional
    void getAllServiciosByDescripcionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedServicio = servicioRepository.saveAndFlush(servicio);

        // Get all the servicioList where descripcion equals to
        defaultServicioFiltering("descripcion.equals=" + DEFAULT_DESCRIPCION, "descripcion.equals=" + UPDATED_DESCRIPCION);
    }

    @Test
    @Transactional
    void getAllServiciosByDescripcionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedServicio = servicioRepository.saveAndFlush(servicio);

        // Get all the servicioList where descripcion in
        defaultServicioFiltering(
            "descripcion.in=" + DEFAULT_DESCRIPCION + "," + UPDATED_DESCRIPCION,
            "descripcion.in=" + UPDATED_DESCRIPCION
        );
    }

    @Test
    @Transactional
    void getAllServiciosByDescripcionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedServicio = servicioRepository.saveAndFlush(servicio);

        // Get all the servicioList where descripcion is not null
        defaultServicioFiltering("descripcion.specified=true", "descripcion.specified=false");
    }

    @Test
    @Transactional
    void getAllServiciosByDescripcionContainsSomething() throws Exception {
        // Initialize the database
        insertedServicio = servicioRepository.saveAndFlush(servicio);

        // Get all the servicioList where descripcion contains
        defaultServicioFiltering("descripcion.contains=" + DEFAULT_DESCRIPCION, "descripcion.contains=" + UPDATED_DESCRIPCION);
    }

    @Test
    @Transactional
    void getAllServiciosByDescripcionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedServicio = servicioRepository.saveAndFlush(servicio);

        // Get all the servicioList where descripcion does not contain
        defaultServicioFiltering("descripcion.doesNotContain=" + UPDATED_DESCRIPCION, "descripcion.doesNotContain=" + DEFAULT_DESCRIPCION);
    }

    @Test
    @Transactional
    void getAllServiciosByCriticidadIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedServicio = servicioRepository.saveAndFlush(servicio);

        // Get all the servicioList where criticidad equals to
        defaultServicioFiltering("criticidad.equals=" + DEFAULT_CRITICIDAD, "criticidad.equals=" + UPDATED_CRITICIDAD);
    }

    @Test
    @Transactional
    void getAllServiciosByCriticidadIsInShouldWork() throws Exception {
        // Initialize the database
        insertedServicio = servicioRepository.saveAndFlush(servicio);

        // Get all the servicioList where criticidad in
        defaultServicioFiltering("criticidad.in=" + DEFAULT_CRITICIDAD + "," + UPDATED_CRITICIDAD, "criticidad.in=" + UPDATED_CRITICIDAD);
    }

    @Test
    @Transactional
    void getAllServiciosByCriticidadIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedServicio = servicioRepository.saveAndFlush(servicio);

        // Get all the servicioList where criticidad is not null
        defaultServicioFiltering("criticidad.specified=true", "criticidad.specified=false");
    }

    @Test
    @Transactional
    void getAllServiciosByEntornoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedServicio = servicioRepository.saveAndFlush(servicio);

        // Get all the servicioList where entorno equals to
        defaultServicioFiltering("entorno.equals=" + DEFAULT_ENTORNO, "entorno.equals=" + UPDATED_ENTORNO);
    }

    @Test
    @Transactional
    void getAllServiciosByEntornoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedServicio = servicioRepository.saveAndFlush(servicio);

        // Get all the servicioList where entorno in
        defaultServicioFiltering("entorno.in=" + DEFAULT_ENTORNO + "," + UPDATED_ENTORNO, "entorno.in=" + UPDATED_ENTORNO);
    }

    @Test
    @Transactional
    void getAllServiciosByEntornoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedServicio = servicioRepository.saveAndFlush(servicio);

        // Get all the servicioList where entorno is not null
        defaultServicioFiltering("entorno.specified=true", "entorno.specified=false");
    }

    @Test
    @Transactional
    void getAllServiciosByRepositorioUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedServicio = servicioRepository.saveAndFlush(servicio);

        // Get all the servicioList where repositorioUrl equals to
        defaultServicioFiltering("repositorioUrl.equals=" + DEFAULT_REPOSITORIO_URL, "repositorioUrl.equals=" + UPDATED_REPOSITORIO_URL);
    }

    @Test
    @Transactional
    void getAllServiciosByRepositorioUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedServicio = servicioRepository.saveAndFlush(servicio);

        // Get all the servicioList where repositorioUrl in
        defaultServicioFiltering(
            "repositorioUrl.in=" + DEFAULT_REPOSITORIO_URL + "," + UPDATED_REPOSITORIO_URL,
            "repositorioUrl.in=" + UPDATED_REPOSITORIO_URL
        );
    }

    @Test
    @Transactional
    void getAllServiciosByRepositorioUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedServicio = servicioRepository.saveAndFlush(servicio);

        // Get all the servicioList where repositorioUrl is not null
        defaultServicioFiltering("repositorioUrl.specified=true", "repositorioUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllServiciosByRepositorioUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedServicio = servicioRepository.saveAndFlush(servicio);

        // Get all the servicioList where repositorioUrl contains
        defaultServicioFiltering(
            "repositorioUrl.contains=" + DEFAULT_REPOSITORIO_URL,
            "repositorioUrl.contains=" + UPDATED_REPOSITORIO_URL
        );
    }

    @Test
    @Transactional
    void getAllServiciosByRepositorioUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedServicio = servicioRepository.saveAndFlush(servicio);

        // Get all the servicioList where repositorioUrl does not contain
        defaultServicioFiltering(
            "repositorioUrl.doesNotContain=" + UPDATED_REPOSITORIO_URL,
            "repositorioUrl.doesNotContain=" + DEFAULT_REPOSITORIO_URL
        );
    }

    @Test
    @Transactional
    void getAllServiciosByActivoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedServicio = servicioRepository.saveAndFlush(servicio);

        // Get all the servicioList where activo equals to
        defaultServicioFiltering("activo.equals=" + DEFAULT_ACTIVO, "activo.equals=" + UPDATED_ACTIVO);
    }

    @Test
    @Transactional
    void getAllServiciosByActivoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedServicio = servicioRepository.saveAndFlush(servicio);

        // Get all the servicioList where activo in
        defaultServicioFiltering("activo.in=" + DEFAULT_ACTIVO + "," + UPDATED_ACTIVO, "activo.in=" + UPDATED_ACTIVO);
    }

    @Test
    @Transactional
    void getAllServiciosByActivoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedServicio = servicioRepository.saveAndFlush(servicio);

        // Get all the servicioList where activo is not null
        defaultServicioFiltering("activo.specified=true", "activo.specified=false");
    }

    @Test
    @Transactional
    void getAllServiciosByEquipoIsEqualToSomething() throws Exception {
        Equipo equipo;
        if (TestUtil.findAll(em, Equipo.class).isEmpty()) {
            servicioRepository.saveAndFlush(servicio);
            equipo = EquipoResourceIT.createEntity();
        } else {
            equipo = TestUtil.findAll(em, Equipo.class).get(0);
        }
        em.persist(equipo);
        em.flush();
        servicio.setEquipo(equipo);
        servicioRepository.saveAndFlush(servicio);
        Long equipoId = equipo.getId();
        // Get all the servicioList where equipo equals to equipoId
        defaultServicioShouldBeFound("equipoId.equals=" + equipoId);

        // Get all the servicioList where equipo equals to (equipoId + 1)
        defaultServicioShouldNotBeFound("equipoId.equals=" + (equipoId + 1));
    }

    @Test
    @Transactional
    void getAllServiciosByIncidenteIsEqualToSomething() throws Exception {
        Incidente incidente;
        if (TestUtil.findAll(em, Incidente.class).isEmpty()) {
            servicioRepository.saveAndFlush(servicio);
            incidente = IncidenteResourceIT.createEntity();
        } else {
            incidente = TestUtil.findAll(em, Incidente.class).get(0);
        }
        em.persist(incidente);
        em.flush();
        servicio.addIncidente(incidente);
        servicioRepository.saveAndFlush(servicio);
        Long incidenteId = incidente.getId();
        // Get all the servicioList where incidente equals to incidenteId
        defaultServicioShouldBeFound("incidenteId.equals=" + incidenteId);

        // Get all the servicioList where incidente equals to (incidenteId + 1)
        defaultServicioShouldNotBeFound("incidenteId.equals=" + (incidenteId + 1));
    }

    private void defaultServicioFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultServicioShouldBeFound(shouldBeFound);
        defaultServicioShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultServicioShouldBeFound(String filter) throws Exception {
        restServicioMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(servicio.getId().intValue())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE)))
            .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION)))
            .andExpect(jsonPath("$.[*].criticidad").value(hasItem(DEFAULT_CRITICIDAD.toString())))
            .andExpect(jsonPath("$.[*].entorno").value(hasItem(DEFAULT_ENTORNO.toString())))
            .andExpect(jsonPath("$.[*].repositorioUrl").value(hasItem(DEFAULT_REPOSITORIO_URL)))
            .andExpect(jsonPath("$.[*].activo").value(hasItem(DEFAULT_ACTIVO)));

        // Check, that the count call also returns 1
        restServicioMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultServicioShouldNotBeFound(String filter) throws Exception {
        restServicioMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restServicioMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingServicio() throws Exception {
        // Get the servicio
        restServicioMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingServicio() throws Exception {
        // Initialize the database
        insertedServicio = servicioRepository.saveAndFlush(servicio);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the servicio
        Servicio updatedServicio = servicioRepository.findById(servicio.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedServicio are not directly saved in db
        em.detach(updatedServicio);
        updatedServicio
            .nombre(UPDATED_NOMBRE)
            .descripcion(UPDATED_DESCRIPCION)
            .criticidad(UPDATED_CRITICIDAD)
            .entorno(UPDATED_ENTORNO)
            .repositorioUrl(UPDATED_REPOSITORIO_URL)
            .activo(UPDATED_ACTIVO);
        ServicioDTO servicioDTO = servicioMapper.toDto(updatedServicio);

        restServicioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, servicioDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(servicioDTO))
            )
            .andExpect(status().isOk());

        // Validate the Servicio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedServicioToMatchAllProperties(updatedServicio);
    }

    @Test
    @Transactional
    void putNonExistingServicio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        servicio.setId(longCount.incrementAndGet());

        // Create the Servicio
        ServicioDTO servicioDTO = servicioMapper.toDto(servicio);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restServicioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, servicioDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(servicioDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Servicio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchServicio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        servicio.setId(longCount.incrementAndGet());

        // Create the Servicio
        ServicioDTO servicioDTO = servicioMapper.toDto(servicio);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServicioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(servicioDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Servicio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamServicio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        servicio.setId(longCount.incrementAndGet());

        // Create the Servicio
        ServicioDTO servicioDTO = servicioMapper.toDto(servicio);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServicioMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(servicioDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Servicio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateServicioWithPatch() throws Exception {
        // Initialize the database
        insertedServicio = servicioRepository.saveAndFlush(servicio);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the servicio using partial update
        Servicio partialUpdatedServicio = new Servicio();
        partialUpdatedServicio.setId(servicio.getId());

        partialUpdatedServicio.descripcion(UPDATED_DESCRIPCION).criticidad(UPDATED_CRITICIDAD).entorno(UPDATED_ENTORNO);

        restServicioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedServicio.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedServicio))
            )
            .andExpect(status().isOk());

        // Validate the Servicio in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertServicioUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedServicio, servicio), getPersistedServicio(servicio));
    }

    @Test
    @Transactional
    void fullUpdateServicioWithPatch() throws Exception {
        // Initialize the database
        insertedServicio = servicioRepository.saveAndFlush(servicio);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the servicio using partial update
        Servicio partialUpdatedServicio = new Servicio();
        partialUpdatedServicio.setId(servicio.getId());

        partialUpdatedServicio
            .nombre(UPDATED_NOMBRE)
            .descripcion(UPDATED_DESCRIPCION)
            .criticidad(UPDATED_CRITICIDAD)
            .entorno(UPDATED_ENTORNO)
            .repositorioUrl(UPDATED_REPOSITORIO_URL)
            .activo(UPDATED_ACTIVO);

        restServicioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedServicio.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedServicio))
            )
            .andExpect(status().isOk());

        // Validate the Servicio in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertServicioUpdatableFieldsEquals(partialUpdatedServicio, getPersistedServicio(partialUpdatedServicio));
    }

    @Test
    @Transactional
    void patchNonExistingServicio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        servicio.setId(longCount.incrementAndGet());

        // Create the Servicio
        ServicioDTO servicioDTO = servicioMapper.toDto(servicio);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restServicioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, servicioDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(servicioDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Servicio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchServicio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        servicio.setId(longCount.incrementAndGet());

        // Create the Servicio
        ServicioDTO servicioDTO = servicioMapper.toDto(servicio);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServicioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(servicioDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Servicio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamServicio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        servicio.setId(longCount.incrementAndGet());

        // Create the Servicio
        ServicioDTO servicioDTO = servicioMapper.toDto(servicio);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServicioMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(servicioDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Servicio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteServicio() throws Exception {
        // Initialize the database
        insertedServicio = servicioRepository.saveAndFlush(servicio);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the servicio
        restServicioMockMvc
            .perform(delete(ENTITY_API_URL_ID, servicio.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return servicioRepository.count();
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

    protected Servicio getPersistedServicio(Servicio servicio) {
        return servicioRepository.findById(servicio.getId()).orElseThrow();
    }

    protected void assertPersistedServicioToMatchAllProperties(Servicio expectedServicio) {
        assertServicioAllPropertiesEquals(expectedServicio, getPersistedServicio(expectedServicio));
    }

    protected void assertPersistedServicioToMatchUpdatableProperties(Servicio expectedServicio) {
        assertServicioAllUpdatablePropertiesEquals(expectedServicio, getPersistedServicio(expectedServicio));
    }
}
