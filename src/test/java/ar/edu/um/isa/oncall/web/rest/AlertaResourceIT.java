package ar.edu.um.isa.oncall.web.rest;

import static ar.edu.um.isa.oncall.domain.AlertaAsserts.*;
import static ar.edu.um.isa.oncall.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ar.edu.um.isa.oncall.IntegrationTest;
import ar.edu.um.isa.oncall.domain.Alerta;
import ar.edu.um.isa.oncall.domain.Incidente;
import ar.edu.um.isa.oncall.domain.Servicio;
import ar.edu.um.isa.oncall.domain.enumeration.OrigenAlerta;
import ar.edu.um.isa.oncall.repository.AlertaRepository;
import ar.edu.um.isa.oncall.service.AlertaService;
import ar.edu.um.isa.oncall.service.dto.AlertaDTO;
import ar.edu.um.isa.oncall.service.mapper.AlertaMapper;
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
 * Integration tests for the {@link AlertaResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AlertaResourceIT {

    private static final String DEFAULT_FINGERPRINT = "AAAAAAAAAA";
    private static final String UPDATED_FINGERPRINT = "BBBBBBBBBB";

    private static final OrigenAlerta DEFAULT_ORIGEN = OrigenAlerta.PROMETHEUS;
    private static final OrigenAlerta UPDATED_ORIGEN = OrigenAlerta.DATADOG;

    private static final String DEFAULT_RESUMEN = "AAAAAAAAAA";
    private static final String UPDATED_RESUMEN = "BBBBBBBBBB";

    private static final String DEFAULT_PAYLOAD = "AAAAAAAAAA";
    private static final String UPDATED_PAYLOAD = "BBBBBBBBBB";

    private static final Instant DEFAULT_RECIBIDA_EN = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RECIBIDA_EN = Instant.ofEpochMilli(1701729143509L);

    private static final Boolean DEFAULT_PROCESADA = false;
    private static final Boolean UPDATED_PROCESADA = true;

    private static final String ENTITY_API_URL = "/api/alertas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AlertaRepository alertaRepository;

    @Mock
    private AlertaRepository alertaRepositoryMock;

    @Autowired
    private AlertaMapper alertaMapper;

    @Mock
    private AlertaService alertaServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAlertaMockMvc;

    private Alerta alerta;

    private Alerta insertedAlerta;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Alerta createEntity(EntityManager em) {
        Alerta alerta = new Alerta()
            .fingerprint(DEFAULT_FINGERPRINT)
            .origen(DEFAULT_ORIGEN)
            .resumen(DEFAULT_RESUMEN)
            .payload(DEFAULT_PAYLOAD)
            .recibidaEn(DEFAULT_RECIBIDA_EN)
            .procesada(DEFAULT_PROCESADA);
        // Add required entity
        Servicio servicio;
        if (TestUtil.findAll(em, Servicio.class).isEmpty()) {
            servicio = ServicioResourceIT.createEntity(em);
            em.persist(servicio);
            em.flush();
        } else {
            servicio = TestUtil.findAll(em, Servicio.class).get(0);
        }
        alerta.setServicio(servicio);
        return alerta;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Alerta createUpdatedEntity(EntityManager em) {
        Alerta updatedAlerta = new Alerta()
            .fingerprint(UPDATED_FINGERPRINT)
            .origen(UPDATED_ORIGEN)
            .resumen(UPDATED_RESUMEN)
            .payload(UPDATED_PAYLOAD)
            .recibidaEn(UPDATED_RECIBIDA_EN)
            .procesada(UPDATED_PROCESADA);
        // Add required entity
        Servicio servicio;
        if (TestUtil.findAll(em, Servicio.class).isEmpty()) {
            servicio = ServicioResourceIT.createUpdatedEntity(em);
            em.persist(servicio);
            em.flush();
        } else {
            servicio = TestUtil.findAll(em, Servicio.class).get(0);
        }
        updatedAlerta.setServicio(servicio);
        return updatedAlerta;
    }

    @BeforeEach
    void initTest() {
        alerta = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedAlerta != null) {
            alertaRepository.delete(insertedAlerta);
            insertedAlerta = null;
        }
    }

    @Test
    @Transactional
    void createAlerta() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Alerta
        AlertaDTO alertaDTO = alertaMapper.toDto(alerta);
        var returnedAlertaDTO = om.readValue(
            restAlertaMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(alertaDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AlertaDTO.class
        );

        // Validate the Alerta in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAlerta = alertaMapper.toEntity(returnedAlertaDTO);
        assertAlertaUpdatableFieldsEquals(returnedAlerta, getPersistedAlerta(returnedAlerta));

        insertedAlerta = returnedAlerta;
    }

    @Test
    @Transactional
    void createAlertaWithExistingId() throws Exception {
        // Create the Alerta with an existing ID
        alerta.setId(1L);
        AlertaDTO alertaDTO = alertaMapper.toDto(alerta);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAlertaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(alertaDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Alerta in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFingerprintIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        alerta.setFingerprint(null);

        // Create the Alerta, which fails.
        AlertaDTO alertaDTO = alertaMapper.toDto(alerta);

        restAlertaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(alertaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkOrigenIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        alerta.setOrigen(null);

        // Create the Alerta, which fails.
        AlertaDTO alertaDTO = alertaMapper.toDto(alerta);

        restAlertaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(alertaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkResumenIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        alerta.setResumen(null);

        // Create the Alerta, which fails.
        AlertaDTO alertaDTO = alertaMapper.toDto(alerta);

        restAlertaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(alertaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRecibidaEnIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        alerta.setRecibidaEn(null);

        // Create the Alerta, which fails.
        AlertaDTO alertaDTO = alertaMapper.toDto(alerta);

        restAlertaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(alertaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkProcesadaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        alerta.setProcesada(null);

        // Create the Alerta, which fails.
        AlertaDTO alertaDTO = alertaMapper.toDto(alerta);

        restAlertaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(alertaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAlertas() throws Exception {
        // Initialize the database
        insertedAlerta = alertaRepository.saveAndFlush(alerta);

        // Get all the alertaList
        restAlertaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(alerta.getId().intValue())))
            .andExpect(jsonPath("$.[*].fingerprint").value(hasItem(DEFAULT_FINGERPRINT)))
            .andExpect(jsonPath("$.[*].origen").value(hasItem(DEFAULT_ORIGEN.toString())))
            .andExpect(jsonPath("$.[*].resumen").value(hasItem(DEFAULT_RESUMEN)))
            .andExpect(jsonPath("$.[*].payload").value(hasItem(DEFAULT_PAYLOAD)))
            .andExpect(jsonPath("$.[*].recibidaEn").value(hasItem(DEFAULT_RECIBIDA_EN.toString())))
            .andExpect(jsonPath("$.[*].procesada").value(hasItem(DEFAULT_PROCESADA)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAlertasWithEagerRelationshipsIsEnabled() throws Exception {
        when(alertaServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAlertaMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(alertaServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAlertasWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(alertaServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAlertaMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(alertaRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getAlerta() throws Exception {
        // Initialize the database
        insertedAlerta = alertaRepository.saveAndFlush(alerta);

        // Get the alerta
        restAlertaMockMvc
            .perform(get(ENTITY_API_URL_ID, alerta.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(alerta.getId().intValue()))
            .andExpect(jsonPath("$.fingerprint").value(DEFAULT_FINGERPRINT))
            .andExpect(jsonPath("$.origen").value(DEFAULT_ORIGEN.toString()))
            .andExpect(jsonPath("$.resumen").value(DEFAULT_RESUMEN))
            .andExpect(jsonPath("$.payload").value(DEFAULT_PAYLOAD))
            .andExpect(jsonPath("$.recibidaEn").value(DEFAULT_RECIBIDA_EN.toString()))
            .andExpect(jsonPath("$.procesada").value(DEFAULT_PROCESADA));
    }

    @Test
    @Transactional
    void getAlertasByIdFiltering() throws Exception {
        // Initialize the database
        insertedAlerta = alertaRepository.saveAndFlush(alerta);

        Long id = alerta.getId();

        defaultAlertaFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultAlertaFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultAlertaFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAlertasByFingerprintIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAlerta = alertaRepository.saveAndFlush(alerta);

        // Get all the alertaList where fingerprint equals to
        defaultAlertaFiltering("fingerprint.equals=" + DEFAULT_FINGERPRINT, "fingerprint.equals=" + UPDATED_FINGERPRINT);
    }

    @Test
    @Transactional
    void getAllAlertasByFingerprintIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAlerta = alertaRepository.saveAndFlush(alerta);

        // Get all the alertaList where fingerprint in
        defaultAlertaFiltering(
            "fingerprint.in=" + DEFAULT_FINGERPRINT + "," + UPDATED_FINGERPRINT,
            "fingerprint.in=" + UPDATED_FINGERPRINT
        );
    }

    @Test
    @Transactional
    void getAllAlertasByFingerprintIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAlerta = alertaRepository.saveAndFlush(alerta);

        // Get all the alertaList where fingerprint is not null
        defaultAlertaFiltering("fingerprint.specified=true", "fingerprint.specified=false");
    }

    @Test
    @Transactional
    void getAllAlertasByFingerprintContainsSomething() throws Exception {
        // Initialize the database
        insertedAlerta = alertaRepository.saveAndFlush(alerta);

        // Get all the alertaList where fingerprint contains
        defaultAlertaFiltering("fingerprint.contains=" + DEFAULT_FINGERPRINT, "fingerprint.contains=" + UPDATED_FINGERPRINT);
    }

    @Test
    @Transactional
    void getAllAlertasByFingerprintNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAlerta = alertaRepository.saveAndFlush(alerta);

        // Get all the alertaList where fingerprint does not contain
        defaultAlertaFiltering("fingerprint.doesNotContain=" + UPDATED_FINGERPRINT, "fingerprint.doesNotContain=" + DEFAULT_FINGERPRINT);
    }

    @Test
    @Transactional
    void getAllAlertasByOrigenIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAlerta = alertaRepository.saveAndFlush(alerta);

        // Get all the alertaList where origen equals to
        defaultAlertaFiltering("origen.equals=" + DEFAULT_ORIGEN, "origen.equals=" + UPDATED_ORIGEN);
    }

    @Test
    @Transactional
    void getAllAlertasByOrigenIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAlerta = alertaRepository.saveAndFlush(alerta);

        // Get all the alertaList where origen in
        defaultAlertaFiltering("origen.in=" + DEFAULT_ORIGEN + "," + UPDATED_ORIGEN, "origen.in=" + UPDATED_ORIGEN);
    }

    @Test
    @Transactional
    void getAllAlertasByOrigenIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAlerta = alertaRepository.saveAndFlush(alerta);

        // Get all the alertaList where origen is not null
        defaultAlertaFiltering("origen.specified=true", "origen.specified=false");
    }

    @Test
    @Transactional
    void getAllAlertasByResumenIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAlerta = alertaRepository.saveAndFlush(alerta);

        // Get all the alertaList where resumen equals to
        defaultAlertaFiltering("resumen.equals=" + DEFAULT_RESUMEN, "resumen.equals=" + UPDATED_RESUMEN);
    }

    @Test
    @Transactional
    void getAllAlertasByResumenIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAlerta = alertaRepository.saveAndFlush(alerta);

        // Get all the alertaList where resumen in
        defaultAlertaFiltering("resumen.in=" + DEFAULT_RESUMEN + "," + UPDATED_RESUMEN, "resumen.in=" + UPDATED_RESUMEN);
    }

    @Test
    @Transactional
    void getAllAlertasByResumenIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAlerta = alertaRepository.saveAndFlush(alerta);

        // Get all the alertaList where resumen is not null
        defaultAlertaFiltering("resumen.specified=true", "resumen.specified=false");
    }

    @Test
    @Transactional
    void getAllAlertasByResumenContainsSomething() throws Exception {
        // Initialize the database
        insertedAlerta = alertaRepository.saveAndFlush(alerta);

        // Get all the alertaList where resumen contains
        defaultAlertaFiltering("resumen.contains=" + DEFAULT_RESUMEN, "resumen.contains=" + UPDATED_RESUMEN);
    }

    @Test
    @Transactional
    void getAllAlertasByResumenNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAlerta = alertaRepository.saveAndFlush(alerta);

        // Get all the alertaList where resumen does not contain
        defaultAlertaFiltering("resumen.doesNotContain=" + UPDATED_RESUMEN, "resumen.doesNotContain=" + DEFAULT_RESUMEN);
    }

    @Test
    @Transactional
    void getAllAlertasByPayloadIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAlerta = alertaRepository.saveAndFlush(alerta);

        // Get all the alertaList where payload equals to
        defaultAlertaFiltering("payload.equals=" + DEFAULT_PAYLOAD, "payload.equals=" + UPDATED_PAYLOAD);
    }

    @Test
    @Transactional
    void getAllAlertasByPayloadIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAlerta = alertaRepository.saveAndFlush(alerta);

        // Get all the alertaList where payload in
        defaultAlertaFiltering("payload.in=" + DEFAULT_PAYLOAD + "," + UPDATED_PAYLOAD, "payload.in=" + UPDATED_PAYLOAD);
    }

    @Test
    @Transactional
    void getAllAlertasByPayloadIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAlerta = alertaRepository.saveAndFlush(alerta);

        // Get all the alertaList where payload is not null
        defaultAlertaFiltering("payload.specified=true", "payload.specified=false");
    }

    @Test
    @Transactional
    void getAllAlertasByPayloadContainsSomething() throws Exception {
        // Initialize the database
        insertedAlerta = alertaRepository.saveAndFlush(alerta);

        // Get all the alertaList where payload contains
        defaultAlertaFiltering("payload.contains=" + DEFAULT_PAYLOAD, "payload.contains=" + UPDATED_PAYLOAD);
    }

    @Test
    @Transactional
    void getAllAlertasByPayloadNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAlerta = alertaRepository.saveAndFlush(alerta);

        // Get all the alertaList where payload does not contain
        defaultAlertaFiltering("payload.doesNotContain=" + UPDATED_PAYLOAD, "payload.doesNotContain=" + DEFAULT_PAYLOAD);
    }

    @Test
    @Transactional
    void getAllAlertasByRecibidaEnIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAlerta = alertaRepository.saveAndFlush(alerta);

        // Get all the alertaList where recibidaEn equals to
        defaultAlertaFiltering("recibidaEn.equals=" + DEFAULT_RECIBIDA_EN, "recibidaEn.equals=" + UPDATED_RECIBIDA_EN);
    }

    @Test
    @Transactional
    void getAllAlertasByRecibidaEnIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAlerta = alertaRepository.saveAndFlush(alerta);

        // Get all the alertaList where recibidaEn in
        defaultAlertaFiltering("recibidaEn.in=" + DEFAULT_RECIBIDA_EN + "," + UPDATED_RECIBIDA_EN, "recibidaEn.in=" + UPDATED_RECIBIDA_EN);
    }

    @Test
    @Transactional
    void getAllAlertasByRecibidaEnIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAlerta = alertaRepository.saveAndFlush(alerta);

        // Get all the alertaList where recibidaEn is not null
        defaultAlertaFiltering("recibidaEn.specified=true", "recibidaEn.specified=false");
    }

    @Test
    @Transactional
    void getAllAlertasByProcesadaIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAlerta = alertaRepository.saveAndFlush(alerta);

        // Get all the alertaList where procesada equals to
        defaultAlertaFiltering("procesada.equals=" + DEFAULT_PROCESADA, "procesada.equals=" + UPDATED_PROCESADA);
    }

    @Test
    @Transactional
    void getAllAlertasByProcesadaIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAlerta = alertaRepository.saveAndFlush(alerta);

        // Get all the alertaList where procesada in
        defaultAlertaFiltering("procesada.in=" + DEFAULT_PROCESADA + "," + UPDATED_PROCESADA, "procesada.in=" + UPDATED_PROCESADA);
    }

    @Test
    @Transactional
    void getAllAlertasByProcesadaIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAlerta = alertaRepository.saveAndFlush(alerta);

        // Get all the alertaList where procesada is not null
        defaultAlertaFiltering("procesada.specified=true", "procesada.specified=false");
    }

    @Test
    @Transactional
    void getAllAlertasByServicioIsEqualToSomething() throws Exception {
        Servicio servicio;
        if (TestUtil.findAll(em, Servicio.class).isEmpty()) {
            alertaRepository.saveAndFlush(alerta);
            servicio = ServicioResourceIT.createEntity(em);
        } else {
            servicio = TestUtil.findAll(em, Servicio.class).get(0);
        }
        em.persist(servicio);
        em.flush();
        alerta.setServicio(servicio);
        alertaRepository.saveAndFlush(alerta);
        Long servicioId = servicio.getId();
        // Get all the alertaList where servicio equals to servicioId
        defaultAlertaShouldBeFound("servicioId.equals=" + servicioId);

        // Get all the alertaList where servicio equals to (servicioId + 1)
        defaultAlertaShouldNotBeFound("servicioId.equals=" + (servicioId + 1));
    }

    @Test
    @Transactional
    void getAllAlertasByIncidenteIsEqualToSomething() throws Exception {
        Incidente incidente;
        if (TestUtil.findAll(em, Incidente.class).isEmpty()) {
            alertaRepository.saveAndFlush(alerta);
            incidente = IncidenteResourceIT.createEntity();
        } else {
            incidente = TestUtil.findAll(em, Incidente.class).get(0);
        }
        em.persist(incidente);
        em.flush();
        alerta.setIncidente(incidente);
        alertaRepository.saveAndFlush(alerta);
        Long incidenteId = incidente.getId();
        // Get all the alertaList where incidente equals to incidenteId
        defaultAlertaShouldBeFound("incidenteId.equals=" + incidenteId);

        // Get all the alertaList where incidente equals to (incidenteId + 1)
        defaultAlertaShouldNotBeFound("incidenteId.equals=" + (incidenteId + 1));
    }

    private void defaultAlertaFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultAlertaShouldBeFound(shouldBeFound);
        defaultAlertaShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAlertaShouldBeFound(String filter) throws Exception {
        restAlertaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(alerta.getId().intValue())))
            .andExpect(jsonPath("$.[*].fingerprint").value(hasItem(DEFAULT_FINGERPRINT)))
            .andExpect(jsonPath("$.[*].origen").value(hasItem(DEFAULT_ORIGEN.toString())))
            .andExpect(jsonPath("$.[*].resumen").value(hasItem(DEFAULT_RESUMEN)))
            .andExpect(jsonPath("$.[*].payload").value(hasItem(DEFAULT_PAYLOAD)))
            .andExpect(jsonPath("$.[*].recibidaEn").value(hasItem(DEFAULT_RECIBIDA_EN.toString())))
            .andExpect(jsonPath("$.[*].procesada").value(hasItem(DEFAULT_PROCESADA)));

        // Check, that the count call also returns 1
        restAlertaMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAlertaShouldNotBeFound(String filter) throws Exception {
        restAlertaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAlertaMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAlerta() throws Exception {
        // Get the alerta
        restAlertaMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAlerta() throws Exception {
        // Initialize the database
        insertedAlerta = alertaRepository.saveAndFlush(alerta);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the alerta
        Alerta updatedAlerta = alertaRepository.findById(alerta.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAlerta are not directly saved in db
        em.detach(updatedAlerta);
        updatedAlerta
            .fingerprint(UPDATED_FINGERPRINT)
            .origen(UPDATED_ORIGEN)
            .resumen(UPDATED_RESUMEN)
            .payload(UPDATED_PAYLOAD)
            .recibidaEn(UPDATED_RECIBIDA_EN)
            .procesada(UPDATED_PROCESADA);
        AlertaDTO alertaDTO = alertaMapper.toDto(updatedAlerta);

        restAlertaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, alertaDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(alertaDTO))
            )
            .andExpect(status().isOk());

        // Validate the Alerta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAlertaToMatchAllProperties(updatedAlerta);
    }

    @Test
    @Transactional
    void putNonExistingAlerta() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alerta.setId(longCount.incrementAndGet());

        // Create the Alerta
        AlertaDTO alertaDTO = alertaMapper.toDto(alerta);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAlertaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, alertaDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(alertaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Alerta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAlerta() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alerta.setId(longCount.incrementAndGet());

        // Create the Alerta
        AlertaDTO alertaDTO = alertaMapper.toDto(alerta);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAlertaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(alertaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Alerta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAlerta() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alerta.setId(longCount.incrementAndGet());

        // Create the Alerta
        AlertaDTO alertaDTO = alertaMapper.toDto(alerta);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAlertaMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(alertaDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Alerta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAlertaWithPatch() throws Exception {
        // Initialize the database
        insertedAlerta = alertaRepository.saveAndFlush(alerta);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the alerta using partial update
        Alerta partialUpdatedAlerta = new Alerta();
        partialUpdatedAlerta.setId(alerta.getId());

        partialUpdatedAlerta
            .fingerprint(UPDATED_FINGERPRINT)
            .origen(UPDATED_ORIGEN)
            .resumen(UPDATED_RESUMEN)
            .recibidaEn(UPDATED_RECIBIDA_EN)
            .procesada(UPDATED_PROCESADA);

        restAlertaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAlerta.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAlerta))
            )
            .andExpect(status().isOk());

        // Validate the Alerta in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAlertaUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedAlerta, alerta), getPersistedAlerta(alerta));
    }

    @Test
    @Transactional
    void fullUpdateAlertaWithPatch() throws Exception {
        // Initialize the database
        insertedAlerta = alertaRepository.saveAndFlush(alerta);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the alerta using partial update
        Alerta partialUpdatedAlerta = new Alerta();
        partialUpdatedAlerta.setId(alerta.getId());

        partialUpdatedAlerta
            .fingerprint(UPDATED_FINGERPRINT)
            .origen(UPDATED_ORIGEN)
            .resumen(UPDATED_RESUMEN)
            .payload(UPDATED_PAYLOAD)
            .recibidaEn(UPDATED_RECIBIDA_EN)
            .procesada(UPDATED_PROCESADA);

        restAlertaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAlerta.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAlerta))
            )
            .andExpect(status().isOk());

        // Validate the Alerta in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAlertaUpdatableFieldsEquals(partialUpdatedAlerta, getPersistedAlerta(partialUpdatedAlerta));
    }

    @Test
    @Transactional
    void patchNonExistingAlerta() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alerta.setId(longCount.incrementAndGet());

        // Create the Alerta
        AlertaDTO alertaDTO = alertaMapper.toDto(alerta);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAlertaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, alertaDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(alertaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Alerta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAlerta() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alerta.setId(longCount.incrementAndGet());

        // Create the Alerta
        AlertaDTO alertaDTO = alertaMapper.toDto(alerta);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAlertaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(alertaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Alerta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAlerta() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alerta.setId(longCount.incrementAndGet());

        // Create the Alerta
        AlertaDTO alertaDTO = alertaMapper.toDto(alerta);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAlertaMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(alertaDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Alerta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAlerta() throws Exception {
        // Initialize the database
        insertedAlerta = alertaRepository.saveAndFlush(alerta);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the alerta
        restAlertaMockMvc
            .perform(delete(ENTITY_API_URL_ID, alerta.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return alertaRepository.count();
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

    protected Alerta getPersistedAlerta(Alerta alerta) {
        return alertaRepository.findById(alerta.getId()).orElseThrow();
    }

    protected void assertPersistedAlertaToMatchAllProperties(Alerta expectedAlerta) {
        assertAlertaAllPropertiesEquals(expectedAlerta, getPersistedAlerta(expectedAlerta));
    }

    protected void assertPersistedAlertaToMatchUpdatableProperties(Alerta expectedAlerta) {
        assertAlertaAllUpdatablePropertiesEquals(expectedAlerta, getPersistedAlerta(expectedAlerta));
    }
}
