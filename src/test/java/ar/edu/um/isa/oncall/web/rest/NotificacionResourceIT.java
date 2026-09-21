package ar.edu.um.isa.oncall.web.rest;

import static ar.edu.um.isa.oncall.domain.NotificacionAsserts.*;
import static ar.edu.um.isa.oncall.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ar.edu.um.isa.oncall.IntegrationTest;
import ar.edu.um.isa.oncall.domain.Incidente;
import ar.edu.um.isa.oncall.domain.Notificacion;
import ar.edu.um.isa.oncall.domain.User;
import ar.edu.um.isa.oncall.domain.enumeration.Canal;
import ar.edu.um.isa.oncall.domain.enumeration.EstadoNotificacion;
import ar.edu.um.isa.oncall.repository.NotificacionRepository;
import ar.edu.um.isa.oncall.repository.UserRepository;
import ar.edu.um.isa.oncall.service.NotificacionService;
import ar.edu.um.isa.oncall.service.dto.NotificacionDTO;
import ar.edu.um.isa.oncall.service.mapper.NotificacionMapper;
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
 * Integration tests for the {@link NotificacionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class NotificacionResourceIT {

    private static final Canal DEFAULT_CANAL = Canal.EMAIL;
    private static final Canal UPDATED_CANAL = Canal.SMS;

    private static final String DEFAULT_DESTINO = "AAAAAAAAAA";
    private static final String UPDATED_DESTINO = "BBBBBBBBBB";

    private static final EstadoNotificacion DEFAULT_ESTADO = EstadoNotificacion.PENDIENTE;
    private static final EstadoNotificacion UPDATED_ESTADO = EstadoNotificacion.ENVIADA;

    private static final Instant DEFAULT_ENVIADA_EN = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ENVIADA_EN = Instant.ofEpochMilli(1701729143509L);

    private static final Integer DEFAULT_INTENTOS = 0;
    private static final Integer UPDATED_INTENTOS = 1;
    private static final Integer SMALLER_INTENTOS = 0 - 1;

    private static final String DEFAULT_ERROR_MENSAJE = "AAAAAAAAAA";
    private static final String UPDATED_ERROR_MENSAJE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/notificacions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private NotificacionRepository notificacionRepositoryMock;

    @Autowired
    private NotificacionMapper notificacionMapper;

    @Mock
    private NotificacionService notificacionServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restNotificacionMockMvc;

    private Notificacion notificacion;

    private Notificacion insertedNotificacion;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Notificacion createEntity(EntityManager em) {
        Notificacion notificacion = new Notificacion()
            .canal(DEFAULT_CANAL)
            .destino(DEFAULT_DESTINO)
            .estado(DEFAULT_ESTADO)
            .enviadaEn(DEFAULT_ENVIADA_EN)
            .intentos(DEFAULT_INTENTOS)
            .errorMensaje(DEFAULT_ERROR_MENSAJE);
        // Add required entity
        Incidente incidente;
        if (TestUtil.findAll(em, Incidente.class).isEmpty()) {
            incidente = IncidenteResourceIT.createEntity();
            em.persist(incidente);
            em.flush();
        } else {
            incidente = TestUtil.findAll(em, Incidente.class).get(0);
        }
        notificacion.setIncidente(incidente);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        notificacion.setDestinatario(user);
        return notificacion;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Notificacion createUpdatedEntity(EntityManager em) {
        Notificacion updatedNotificacion = new Notificacion()
            .canal(UPDATED_CANAL)
            .destino(UPDATED_DESTINO)
            .estado(UPDATED_ESTADO)
            .enviadaEn(UPDATED_ENVIADA_EN)
            .intentos(UPDATED_INTENTOS)
            .errorMensaje(UPDATED_ERROR_MENSAJE);
        // Add required entity
        Incidente incidente;
        if (TestUtil.findAll(em, Incidente.class).isEmpty()) {
            incidente = IncidenteResourceIT.createUpdatedEntity();
            em.persist(incidente);
            em.flush();
        } else {
            incidente = TestUtil.findAll(em, Incidente.class).get(0);
        }
        updatedNotificacion.setIncidente(incidente);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedNotificacion.setDestinatario(user);
        return updatedNotificacion;
    }

    @BeforeEach
    void initTest() {
        notificacion = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedNotificacion != null) {
            notificacionRepository.delete(insertedNotificacion);
            insertedNotificacion = null;
        }
    }

    @Test
    @Transactional
    void createNotificacion() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Notificacion
        NotificacionDTO notificacionDTO = notificacionMapper.toDto(notificacion);
        var returnedNotificacionDTO = om.readValue(
            restNotificacionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificacionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            NotificacionDTO.class
        );

        // Validate the Notificacion in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedNotificacion = notificacionMapper.toEntity(returnedNotificacionDTO);
        assertNotificacionUpdatableFieldsEquals(returnedNotificacion, getPersistedNotificacion(returnedNotificacion));

        insertedNotificacion = returnedNotificacion;
    }

    @Test
    @Transactional
    void createNotificacionWithExistingId() throws Exception {
        // Create the Notificacion with an existing ID
        notificacion.setId(1L);
        NotificacionDTO notificacionDTO = notificacionMapper.toDto(notificacion);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restNotificacionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificacionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Notificacion in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCanalIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notificacion.setCanal(null);

        // Create the Notificacion, which fails.
        NotificacionDTO notificacionDTO = notificacionMapper.toDto(notificacion);

        restNotificacionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificacionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDestinoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notificacion.setDestino(null);

        // Create the Notificacion, which fails.
        NotificacionDTO notificacionDTO = notificacionMapper.toDto(notificacion);

        restNotificacionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificacionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEstadoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notificacion.setEstado(null);

        // Create the Notificacion, which fails.
        NotificacionDTO notificacionDTO = notificacionMapper.toDto(notificacion);

        restNotificacionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificacionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIntentosIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notificacion.setIntentos(null);

        // Create the Notificacion, which fails.
        NotificacionDTO notificacionDTO = notificacionMapper.toDto(notificacion);

        restNotificacionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificacionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllNotificacions() throws Exception {
        // Initialize the database
        insertedNotificacion = notificacionRepository.saveAndFlush(notificacion);

        // Get all the notificacionList
        restNotificacionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notificacion.getId().intValue())))
            .andExpect(jsonPath("$.[*].canal").value(hasItem(DEFAULT_CANAL.toString())))
            .andExpect(jsonPath("$.[*].destino").value(hasItem(DEFAULT_DESTINO)))
            .andExpect(jsonPath("$.[*].estado").value(hasItem(DEFAULT_ESTADO.toString())))
            .andExpect(jsonPath("$.[*].enviadaEn").value(hasItem(DEFAULT_ENVIADA_EN.toString())))
            .andExpect(jsonPath("$.[*].intentos").value(hasItem(DEFAULT_INTENTOS)))
            .andExpect(jsonPath("$.[*].errorMensaje").value(hasItem(DEFAULT_ERROR_MENSAJE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllNotificacionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(notificacionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restNotificacionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(notificacionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllNotificacionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(notificacionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restNotificacionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(notificacionRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getNotificacion() throws Exception {
        // Initialize the database
        insertedNotificacion = notificacionRepository.saveAndFlush(notificacion);

        // Get the notificacion
        restNotificacionMockMvc
            .perform(get(ENTITY_API_URL_ID, notificacion.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(notificacion.getId().intValue()))
            .andExpect(jsonPath("$.canal").value(DEFAULT_CANAL.toString()))
            .andExpect(jsonPath("$.destino").value(DEFAULT_DESTINO))
            .andExpect(jsonPath("$.estado").value(DEFAULT_ESTADO.toString()))
            .andExpect(jsonPath("$.enviadaEn").value(DEFAULT_ENVIADA_EN.toString()))
            .andExpect(jsonPath("$.intentos").value(DEFAULT_INTENTOS))
            .andExpect(jsonPath("$.errorMensaje").value(DEFAULT_ERROR_MENSAJE));
    }

    @Test
    @Transactional
    void getNotificacionsByIdFiltering() throws Exception {
        // Initialize the database
        insertedNotificacion = notificacionRepository.saveAndFlush(notificacion);

        Long id = notificacion.getId();

        defaultNotificacionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultNotificacionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultNotificacionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllNotificacionsByCanalIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotificacion = notificacionRepository.saveAndFlush(notificacion);

        // Get all the notificacionList where canal equals to
        defaultNotificacionFiltering("canal.equals=" + DEFAULT_CANAL, "canal.equals=" + UPDATED_CANAL);
    }

    @Test
    @Transactional
    void getAllNotificacionsByCanalIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotificacion = notificacionRepository.saveAndFlush(notificacion);

        // Get all the notificacionList where canal in
        defaultNotificacionFiltering("canal.in=" + DEFAULT_CANAL + "," + UPDATED_CANAL, "canal.in=" + UPDATED_CANAL);
    }

    @Test
    @Transactional
    void getAllNotificacionsByCanalIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotificacion = notificacionRepository.saveAndFlush(notificacion);

        // Get all the notificacionList where canal is not null
        defaultNotificacionFiltering("canal.specified=true", "canal.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificacionsByDestinoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotificacion = notificacionRepository.saveAndFlush(notificacion);

        // Get all the notificacionList where destino equals to
        defaultNotificacionFiltering("destino.equals=" + DEFAULT_DESTINO, "destino.equals=" + UPDATED_DESTINO);
    }

    @Test
    @Transactional
    void getAllNotificacionsByDestinoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotificacion = notificacionRepository.saveAndFlush(notificacion);

        // Get all the notificacionList where destino in
        defaultNotificacionFiltering("destino.in=" + DEFAULT_DESTINO + "," + UPDATED_DESTINO, "destino.in=" + UPDATED_DESTINO);
    }

    @Test
    @Transactional
    void getAllNotificacionsByDestinoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotificacion = notificacionRepository.saveAndFlush(notificacion);

        // Get all the notificacionList where destino is not null
        defaultNotificacionFiltering("destino.specified=true", "destino.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificacionsByDestinoContainsSomething() throws Exception {
        // Initialize the database
        insertedNotificacion = notificacionRepository.saveAndFlush(notificacion);

        // Get all the notificacionList where destino contains
        defaultNotificacionFiltering("destino.contains=" + DEFAULT_DESTINO, "destino.contains=" + UPDATED_DESTINO);
    }

    @Test
    @Transactional
    void getAllNotificacionsByDestinoNotContainsSomething() throws Exception {
        // Initialize the database
        insertedNotificacion = notificacionRepository.saveAndFlush(notificacion);

        // Get all the notificacionList where destino does not contain
        defaultNotificacionFiltering("destino.doesNotContain=" + UPDATED_DESTINO, "destino.doesNotContain=" + DEFAULT_DESTINO);
    }

    @Test
    @Transactional
    void getAllNotificacionsByEstadoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotificacion = notificacionRepository.saveAndFlush(notificacion);

        // Get all the notificacionList where estado equals to
        defaultNotificacionFiltering("estado.equals=" + DEFAULT_ESTADO, "estado.equals=" + UPDATED_ESTADO);
    }

    @Test
    @Transactional
    void getAllNotificacionsByEstadoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotificacion = notificacionRepository.saveAndFlush(notificacion);

        // Get all the notificacionList where estado in
        defaultNotificacionFiltering("estado.in=" + DEFAULT_ESTADO + "," + UPDATED_ESTADO, "estado.in=" + UPDATED_ESTADO);
    }

    @Test
    @Transactional
    void getAllNotificacionsByEstadoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotificacion = notificacionRepository.saveAndFlush(notificacion);

        // Get all the notificacionList where estado is not null
        defaultNotificacionFiltering("estado.specified=true", "estado.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificacionsByEnviadaEnIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotificacion = notificacionRepository.saveAndFlush(notificacion);

        // Get all the notificacionList where enviadaEn equals to
        defaultNotificacionFiltering("enviadaEn.equals=" + DEFAULT_ENVIADA_EN, "enviadaEn.equals=" + UPDATED_ENVIADA_EN);
    }

    @Test
    @Transactional
    void getAllNotificacionsByEnviadaEnIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotificacion = notificacionRepository.saveAndFlush(notificacion);

        // Get all the notificacionList where enviadaEn in
        defaultNotificacionFiltering("enviadaEn.in=" + DEFAULT_ENVIADA_EN + "," + UPDATED_ENVIADA_EN, "enviadaEn.in=" + UPDATED_ENVIADA_EN);
    }

    @Test
    @Transactional
    void getAllNotificacionsByEnviadaEnIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotificacion = notificacionRepository.saveAndFlush(notificacion);

        // Get all the notificacionList where enviadaEn is not null
        defaultNotificacionFiltering("enviadaEn.specified=true", "enviadaEn.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificacionsByIntentosIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotificacion = notificacionRepository.saveAndFlush(notificacion);

        // Get all the notificacionList where intentos equals to
        defaultNotificacionFiltering("intentos.equals=" + DEFAULT_INTENTOS, "intentos.equals=" + UPDATED_INTENTOS);
    }

    @Test
    @Transactional
    void getAllNotificacionsByIntentosIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotificacion = notificacionRepository.saveAndFlush(notificacion);

        // Get all the notificacionList where intentos in
        defaultNotificacionFiltering("intentos.in=" + DEFAULT_INTENTOS + "," + UPDATED_INTENTOS, "intentos.in=" + UPDATED_INTENTOS);
    }

    @Test
    @Transactional
    void getAllNotificacionsByIntentosIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotificacion = notificacionRepository.saveAndFlush(notificacion);

        // Get all the notificacionList where intentos is not null
        defaultNotificacionFiltering("intentos.specified=true", "intentos.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificacionsByIntentosIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotificacion = notificacionRepository.saveAndFlush(notificacion);

        // Get all the notificacionList where intentos is greater than or equal to
        defaultNotificacionFiltering(
            "intentos.greaterThanOrEqual=" + DEFAULT_INTENTOS,
            "intentos.greaterThanOrEqual=" + (DEFAULT_INTENTOS + 1)
        );
    }

    @Test
    @Transactional
    void getAllNotificacionsByIntentosIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotificacion = notificacionRepository.saveAndFlush(notificacion);

        // Get all the notificacionList where intentos is less than or equal to
        defaultNotificacionFiltering("intentos.lessThanOrEqual=" + DEFAULT_INTENTOS, "intentos.lessThanOrEqual=" + SMALLER_INTENTOS);
    }

    @Test
    @Transactional
    void getAllNotificacionsByIntentosIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedNotificacion = notificacionRepository.saveAndFlush(notificacion);

        // Get all the notificacionList where intentos is less than
        defaultNotificacionFiltering("intentos.lessThan=" + (DEFAULT_INTENTOS + 1), "intentos.lessThan=" + DEFAULT_INTENTOS);
    }

    @Test
    @Transactional
    void getAllNotificacionsByIntentosIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedNotificacion = notificacionRepository.saveAndFlush(notificacion);

        // Get all the notificacionList where intentos is greater than
        defaultNotificacionFiltering("intentos.greaterThan=" + SMALLER_INTENTOS, "intentos.greaterThan=" + DEFAULT_INTENTOS);
    }

    @Test
    @Transactional
    void getAllNotificacionsByErrorMensajeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotificacion = notificacionRepository.saveAndFlush(notificacion);

        // Get all the notificacionList where errorMensaje equals to
        defaultNotificacionFiltering("errorMensaje.equals=" + DEFAULT_ERROR_MENSAJE, "errorMensaje.equals=" + UPDATED_ERROR_MENSAJE);
    }

    @Test
    @Transactional
    void getAllNotificacionsByErrorMensajeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotificacion = notificacionRepository.saveAndFlush(notificacion);

        // Get all the notificacionList where errorMensaje in
        defaultNotificacionFiltering(
            "errorMensaje.in=" + DEFAULT_ERROR_MENSAJE + "," + UPDATED_ERROR_MENSAJE,
            "errorMensaje.in=" + UPDATED_ERROR_MENSAJE
        );
    }

    @Test
    @Transactional
    void getAllNotificacionsByErrorMensajeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotificacion = notificacionRepository.saveAndFlush(notificacion);

        // Get all the notificacionList where errorMensaje is not null
        defaultNotificacionFiltering("errorMensaje.specified=true", "errorMensaje.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificacionsByErrorMensajeContainsSomething() throws Exception {
        // Initialize the database
        insertedNotificacion = notificacionRepository.saveAndFlush(notificacion);

        // Get all the notificacionList where errorMensaje contains
        defaultNotificacionFiltering("errorMensaje.contains=" + DEFAULT_ERROR_MENSAJE, "errorMensaje.contains=" + UPDATED_ERROR_MENSAJE);
    }

    @Test
    @Transactional
    void getAllNotificacionsByErrorMensajeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedNotificacion = notificacionRepository.saveAndFlush(notificacion);

        // Get all the notificacionList where errorMensaje does not contain
        defaultNotificacionFiltering(
            "errorMensaje.doesNotContain=" + UPDATED_ERROR_MENSAJE,
            "errorMensaje.doesNotContain=" + DEFAULT_ERROR_MENSAJE
        );
    }

    @Test
    @Transactional
    void getAllNotificacionsByIncidenteIsEqualToSomething() throws Exception {
        Incidente incidente;
        if (TestUtil.findAll(em, Incidente.class).isEmpty()) {
            notificacionRepository.saveAndFlush(notificacion);
            incidente = IncidenteResourceIT.createEntity();
        } else {
            incidente = TestUtil.findAll(em, Incidente.class).get(0);
        }
        em.persist(incidente);
        em.flush();
        notificacion.setIncidente(incidente);
        notificacionRepository.saveAndFlush(notificacion);
        Long incidenteId = incidente.getId();
        // Get all the notificacionList where incidente equals to incidenteId
        defaultNotificacionShouldBeFound("incidenteId.equals=" + incidenteId);

        // Get all the notificacionList where incidente equals to (incidenteId + 1)
        defaultNotificacionShouldNotBeFound("incidenteId.equals=" + (incidenteId + 1));
    }

    @Test
    @Transactional
    void getAllNotificacionsByDestinatarioIsEqualToSomething() throws Exception {
        User destinatario;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            notificacionRepository.saveAndFlush(notificacion);
            destinatario = UserResourceIT.createEntity();
        } else {
            destinatario = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(destinatario);
        em.flush();
        notificacion.setDestinatario(destinatario);
        notificacionRepository.saveAndFlush(notificacion);
        Long destinatarioId = destinatario.getId();
        // Get all the notificacionList where destinatario equals to destinatarioId
        defaultNotificacionShouldBeFound("destinatarioId.equals=" + destinatarioId);

        // Get all the notificacionList where destinatario equals to (destinatarioId + 1)
        defaultNotificacionShouldNotBeFound("destinatarioId.equals=" + (destinatarioId + 1));
    }

    private void defaultNotificacionFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultNotificacionShouldBeFound(shouldBeFound);
        defaultNotificacionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultNotificacionShouldBeFound(String filter) throws Exception {
        restNotificacionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notificacion.getId().intValue())))
            .andExpect(jsonPath("$.[*].canal").value(hasItem(DEFAULT_CANAL.toString())))
            .andExpect(jsonPath("$.[*].destino").value(hasItem(DEFAULT_DESTINO)))
            .andExpect(jsonPath("$.[*].estado").value(hasItem(DEFAULT_ESTADO.toString())))
            .andExpect(jsonPath("$.[*].enviadaEn").value(hasItem(DEFAULT_ENVIADA_EN.toString())))
            .andExpect(jsonPath("$.[*].intentos").value(hasItem(DEFAULT_INTENTOS)))
            .andExpect(jsonPath("$.[*].errorMensaje").value(hasItem(DEFAULT_ERROR_MENSAJE)));

        // Check, that the count call also returns 1
        restNotificacionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultNotificacionShouldNotBeFound(String filter) throws Exception {
        restNotificacionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restNotificacionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingNotificacion() throws Exception {
        // Get the notificacion
        restNotificacionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingNotificacion() throws Exception {
        // Initialize the database
        insertedNotificacion = notificacionRepository.saveAndFlush(notificacion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notificacion
        Notificacion updatedNotificacion = notificacionRepository.findById(notificacion.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedNotificacion are not directly saved in db
        em.detach(updatedNotificacion);
        updatedNotificacion
            .canal(UPDATED_CANAL)
            .destino(UPDATED_DESTINO)
            .estado(UPDATED_ESTADO)
            .enviadaEn(UPDATED_ENVIADA_EN)
            .intentos(UPDATED_INTENTOS)
            .errorMensaje(UPDATED_ERROR_MENSAJE);
        NotificacionDTO notificacionDTO = notificacionMapper.toDto(updatedNotificacion);

        restNotificacionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, notificacionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificacionDTO))
            )
            .andExpect(status().isOk());

        // Validate the Notificacion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedNotificacionToMatchAllProperties(updatedNotificacion);
    }

    @Test
    @Transactional
    void putNonExistingNotificacion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificacion.setId(longCount.incrementAndGet());

        // Create the Notificacion
        NotificacionDTO notificacionDTO = notificacionMapper.toDto(notificacion);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNotificacionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, notificacionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificacionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Notificacion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchNotificacion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificacion.setId(longCount.incrementAndGet());

        // Create the Notificacion
        NotificacionDTO notificacionDTO = notificacionMapper.toDto(notificacion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificacionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificacionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Notificacion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamNotificacion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificacion.setId(longCount.incrementAndGet());

        // Create the Notificacion
        NotificacionDTO notificacionDTO = notificacionMapper.toDto(notificacion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificacionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificacionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Notificacion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateNotificacionWithPatch() throws Exception {
        // Initialize the database
        insertedNotificacion = notificacionRepository.saveAndFlush(notificacion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notificacion using partial update
        Notificacion partialUpdatedNotificacion = new Notificacion();
        partialUpdatedNotificacion.setId(notificacion.getId());

        partialUpdatedNotificacion
            .canal(UPDATED_CANAL)
            .destino(UPDATED_DESTINO)
            .estado(UPDATED_ESTADO)
            .enviadaEn(UPDATED_ENVIADA_EN)
            .errorMensaje(UPDATED_ERROR_MENSAJE);

        restNotificacionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNotificacion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedNotificacion))
            )
            .andExpect(status().isOk());

        // Validate the Notificacion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertNotificacionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedNotificacion, notificacion),
            getPersistedNotificacion(notificacion)
        );
    }

    @Test
    @Transactional
    void fullUpdateNotificacionWithPatch() throws Exception {
        // Initialize the database
        insertedNotificacion = notificacionRepository.saveAndFlush(notificacion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notificacion using partial update
        Notificacion partialUpdatedNotificacion = new Notificacion();
        partialUpdatedNotificacion.setId(notificacion.getId());

        partialUpdatedNotificacion
            .canal(UPDATED_CANAL)
            .destino(UPDATED_DESTINO)
            .estado(UPDATED_ESTADO)
            .enviadaEn(UPDATED_ENVIADA_EN)
            .intentos(UPDATED_INTENTOS)
            .errorMensaje(UPDATED_ERROR_MENSAJE);

        restNotificacionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNotificacion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedNotificacion))
            )
            .andExpect(status().isOk());

        // Validate the Notificacion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertNotificacionUpdatableFieldsEquals(partialUpdatedNotificacion, getPersistedNotificacion(partialUpdatedNotificacion));
    }

    @Test
    @Transactional
    void patchNonExistingNotificacion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificacion.setId(longCount.incrementAndGet());

        // Create the Notificacion
        NotificacionDTO notificacionDTO = notificacionMapper.toDto(notificacion);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNotificacionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, notificacionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(notificacionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Notificacion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchNotificacion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificacion.setId(longCount.incrementAndGet());

        // Create the Notificacion
        NotificacionDTO notificacionDTO = notificacionMapper.toDto(notificacion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificacionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(notificacionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Notificacion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamNotificacion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificacion.setId(longCount.incrementAndGet());

        // Create the Notificacion
        NotificacionDTO notificacionDTO = notificacionMapper.toDto(notificacion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificacionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(notificacionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Notificacion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteNotificacion() throws Exception {
        // Initialize the database
        insertedNotificacion = notificacionRepository.saveAndFlush(notificacion);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the notificacion
        restNotificacionMockMvc
            .perform(delete(ENTITY_API_URL_ID, notificacion.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return notificacionRepository.count();
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

    protected Notificacion getPersistedNotificacion(Notificacion notificacion) {
        return notificacionRepository.findById(notificacion.getId()).orElseThrow();
    }

    protected void assertPersistedNotificacionToMatchAllProperties(Notificacion expectedNotificacion) {
        assertNotificacionAllPropertiesEquals(expectedNotificacion, getPersistedNotificacion(expectedNotificacion));
    }

    protected void assertPersistedNotificacionToMatchUpdatableProperties(Notificacion expectedNotificacion) {
        assertNotificacionAllUpdatablePropertiesEquals(expectedNotificacion, getPersistedNotificacion(expectedNotificacion));
    }
}
