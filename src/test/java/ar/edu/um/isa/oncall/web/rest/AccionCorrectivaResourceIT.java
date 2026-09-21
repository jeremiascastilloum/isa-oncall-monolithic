package ar.edu.um.isa.oncall.web.rest;

import static ar.edu.um.isa.oncall.domain.AccionCorrectivaAsserts.*;
import static ar.edu.um.isa.oncall.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ar.edu.um.isa.oncall.IntegrationTest;
import ar.edu.um.isa.oncall.domain.AccionCorrectiva;
import ar.edu.um.isa.oncall.domain.Postmortem;
import ar.edu.um.isa.oncall.domain.enumeration.EstadoAccion;
import ar.edu.um.isa.oncall.domain.enumeration.Prioridad;
import ar.edu.um.isa.oncall.repository.AccionCorrectivaRepository;
import ar.edu.um.isa.oncall.repository.UserRepository;
import ar.edu.um.isa.oncall.service.AccionCorrectivaService;
import ar.edu.um.isa.oncall.service.dto.AccionCorrectivaDTO;
import ar.edu.um.isa.oncall.service.mapper.AccionCorrectivaMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
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
 * Integration tests for the {@link AccionCorrectivaResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AccionCorrectivaResourceIT {

    private static final String DEFAULT_DESCRIPCION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPCION = "BBBBBBBBBB";

    private static final Prioridad DEFAULT_PRIORIDAD = Prioridad.ALTA;
    private static final Prioridad UPDATED_PRIORIDAD = Prioridad.MEDIA;

    private static final EstadoAccion DEFAULT_ESTADO = EstadoAccion.PENDIENTE;
    private static final EstadoAccion UPDATED_ESTADO = EstadoAccion.EN_CURSO;

    private static final LocalDate DEFAULT_FECHA_LIMITE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_FECHA_LIMITE = LocalDate.parse("2023-12-04");

    private static final String DEFAULT_TICKET_URL = "AAAAAAAAAA";
    private static final String UPDATED_TICKET_URL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/accion-correctivas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AccionCorrectivaRepository accionCorrectivaRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private AccionCorrectivaRepository accionCorrectivaRepositoryMock;

    @Autowired
    private AccionCorrectivaMapper accionCorrectivaMapper;

    @Mock
    private AccionCorrectivaService accionCorrectivaServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAccionCorrectivaMockMvc;

    private AccionCorrectiva accionCorrectiva;

    private AccionCorrectiva insertedAccionCorrectiva;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AccionCorrectiva createEntity(EntityManager em) {
        AccionCorrectiva accionCorrectiva = new AccionCorrectiva()
            .descripcion(DEFAULT_DESCRIPCION)
            .prioridad(DEFAULT_PRIORIDAD)
            .estado(DEFAULT_ESTADO)
            .fechaLimite(DEFAULT_FECHA_LIMITE)
            .ticketUrl(DEFAULT_TICKET_URL);
        // Add required entity
        Postmortem postmortem;
        if (TestUtil.findAll(em, Postmortem.class).isEmpty()) {
            postmortem = PostmortemResourceIT.createEntity(em);
            em.persist(postmortem);
            em.flush();
        } else {
            postmortem = TestUtil.findAll(em, Postmortem.class).get(0);
        }
        accionCorrectiva.setPostmortem(postmortem);
        return accionCorrectiva;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AccionCorrectiva createUpdatedEntity(EntityManager em) {
        AccionCorrectiva updatedAccionCorrectiva = new AccionCorrectiva()
            .descripcion(UPDATED_DESCRIPCION)
            .prioridad(UPDATED_PRIORIDAD)
            .estado(UPDATED_ESTADO)
            .fechaLimite(UPDATED_FECHA_LIMITE)
            .ticketUrl(UPDATED_TICKET_URL);
        // Add required entity
        Postmortem postmortem;
        if (TestUtil.findAll(em, Postmortem.class).isEmpty()) {
            postmortem = PostmortemResourceIT.createUpdatedEntity(em);
            em.persist(postmortem);
            em.flush();
        } else {
            postmortem = TestUtil.findAll(em, Postmortem.class).get(0);
        }
        updatedAccionCorrectiva.setPostmortem(postmortem);
        return updatedAccionCorrectiva;
    }

    @BeforeEach
    void initTest() {
        accionCorrectiva = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedAccionCorrectiva != null) {
            accionCorrectivaRepository.delete(insertedAccionCorrectiva);
            insertedAccionCorrectiva = null;
        }
    }

    @Test
    @Transactional
    void createAccionCorrectiva() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AccionCorrectiva
        AccionCorrectivaDTO accionCorrectivaDTO = accionCorrectivaMapper.toDto(accionCorrectiva);
        var returnedAccionCorrectivaDTO = om.readValue(
            restAccionCorrectivaMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(accionCorrectivaDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AccionCorrectivaDTO.class
        );

        // Validate the AccionCorrectiva in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAccionCorrectiva = accionCorrectivaMapper.toEntity(returnedAccionCorrectivaDTO);
        assertAccionCorrectivaUpdatableFieldsEquals(returnedAccionCorrectiva, getPersistedAccionCorrectiva(returnedAccionCorrectiva));

        insertedAccionCorrectiva = returnedAccionCorrectiva;
    }

    @Test
    @Transactional
    void createAccionCorrectivaWithExistingId() throws Exception {
        // Create the AccionCorrectiva with an existing ID
        accionCorrectiva.setId(1L);
        AccionCorrectivaDTO accionCorrectivaDTO = accionCorrectivaMapper.toDto(accionCorrectiva);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAccionCorrectivaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(accionCorrectivaDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AccionCorrectiva in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDescripcionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        accionCorrectiva.setDescripcion(null);

        // Create the AccionCorrectiva, which fails.
        AccionCorrectivaDTO accionCorrectivaDTO = accionCorrectivaMapper.toDto(accionCorrectiva);

        restAccionCorrectivaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(accionCorrectivaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPrioridadIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        accionCorrectiva.setPrioridad(null);

        // Create the AccionCorrectiva, which fails.
        AccionCorrectivaDTO accionCorrectivaDTO = accionCorrectivaMapper.toDto(accionCorrectiva);

        restAccionCorrectivaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(accionCorrectivaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEstadoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        accionCorrectiva.setEstado(null);

        // Create the AccionCorrectiva, which fails.
        AccionCorrectivaDTO accionCorrectivaDTO = accionCorrectivaMapper.toDto(accionCorrectiva);

        restAccionCorrectivaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(accionCorrectivaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAccionCorrectivas() throws Exception {
        // Initialize the database
        insertedAccionCorrectiva = accionCorrectivaRepository.saveAndFlush(accionCorrectiva);

        // Get all the accionCorrectivaList
        restAccionCorrectivaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(accionCorrectiva.getId().intValue())))
            .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION)))
            .andExpect(jsonPath("$.[*].prioridad").value(hasItem(DEFAULT_PRIORIDAD.toString())))
            .andExpect(jsonPath("$.[*].estado").value(hasItem(DEFAULT_ESTADO.toString())))
            .andExpect(jsonPath("$.[*].fechaLimite").value(hasItem(DEFAULT_FECHA_LIMITE.toString())))
            .andExpect(jsonPath("$.[*].ticketUrl").value(hasItem(DEFAULT_TICKET_URL)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAccionCorrectivasWithEagerRelationshipsIsEnabled() throws Exception {
        when(accionCorrectivaServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAccionCorrectivaMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(accionCorrectivaServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAccionCorrectivasWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(accionCorrectivaServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAccionCorrectivaMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(accionCorrectivaRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getAccionCorrectiva() throws Exception {
        // Initialize the database
        insertedAccionCorrectiva = accionCorrectivaRepository.saveAndFlush(accionCorrectiva);

        // Get the accionCorrectiva
        restAccionCorrectivaMockMvc
            .perform(get(ENTITY_API_URL_ID, accionCorrectiva.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(accionCorrectiva.getId().intValue()))
            .andExpect(jsonPath("$.descripcion").value(DEFAULT_DESCRIPCION))
            .andExpect(jsonPath("$.prioridad").value(DEFAULT_PRIORIDAD.toString()))
            .andExpect(jsonPath("$.estado").value(DEFAULT_ESTADO.toString()))
            .andExpect(jsonPath("$.fechaLimite").value(DEFAULT_FECHA_LIMITE.toString()))
            .andExpect(jsonPath("$.ticketUrl").value(DEFAULT_TICKET_URL));
    }

    @Test
    @Transactional
    void getNonExistingAccionCorrectiva() throws Exception {
        // Get the accionCorrectiva
        restAccionCorrectivaMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAccionCorrectiva() throws Exception {
        // Initialize the database
        insertedAccionCorrectiva = accionCorrectivaRepository.saveAndFlush(accionCorrectiva);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the accionCorrectiva
        AccionCorrectiva updatedAccionCorrectiva = accionCorrectivaRepository.findById(accionCorrectiva.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAccionCorrectiva are not directly saved in db
        em.detach(updatedAccionCorrectiva);
        updatedAccionCorrectiva
            .descripcion(UPDATED_DESCRIPCION)
            .prioridad(UPDATED_PRIORIDAD)
            .estado(UPDATED_ESTADO)
            .fechaLimite(UPDATED_FECHA_LIMITE)
            .ticketUrl(UPDATED_TICKET_URL);
        AccionCorrectivaDTO accionCorrectivaDTO = accionCorrectivaMapper.toDto(updatedAccionCorrectiva);

        restAccionCorrectivaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, accionCorrectivaDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(accionCorrectivaDTO))
            )
            .andExpect(status().isOk());

        // Validate the AccionCorrectiva in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAccionCorrectivaToMatchAllProperties(updatedAccionCorrectiva);
    }

    @Test
    @Transactional
    void putNonExistingAccionCorrectiva() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        accionCorrectiva.setId(longCount.incrementAndGet());

        // Create the AccionCorrectiva
        AccionCorrectivaDTO accionCorrectivaDTO = accionCorrectivaMapper.toDto(accionCorrectiva);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAccionCorrectivaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, accionCorrectivaDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(accionCorrectivaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AccionCorrectiva in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAccionCorrectiva() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        accionCorrectiva.setId(longCount.incrementAndGet());

        // Create the AccionCorrectiva
        AccionCorrectivaDTO accionCorrectivaDTO = accionCorrectivaMapper.toDto(accionCorrectiva);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAccionCorrectivaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(accionCorrectivaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AccionCorrectiva in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAccionCorrectiva() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        accionCorrectiva.setId(longCount.incrementAndGet());

        // Create the AccionCorrectiva
        AccionCorrectivaDTO accionCorrectivaDTO = accionCorrectivaMapper.toDto(accionCorrectiva);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAccionCorrectivaMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(accionCorrectivaDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AccionCorrectiva in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAccionCorrectivaWithPatch() throws Exception {
        // Initialize the database
        insertedAccionCorrectiva = accionCorrectivaRepository.saveAndFlush(accionCorrectiva);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the accionCorrectiva using partial update
        AccionCorrectiva partialUpdatedAccionCorrectiva = new AccionCorrectiva();
        partialUpdatedAccionCorrectiva.setId(accionCorrectiva.getId());

        partialUpdatedAccionCorrectiva.descripcion(UPDATED_DESCRIPCION).ticketUrl(UPDATED_TICKET_URL);

        restAccionCorrectivaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAccionCorrectiva.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAccionCorrectiva))
            )
            .andExpect(status().isOk());

        // Validate the AccionCorrectiva in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAccionCorrectivaUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAccionCorrectiva, accionCorrectiva),
            getPersistedAccionCorrectiva(accionCorrectiva)
        );
    }

    @Test
    @Transactional
    void fullUpdateAccionCorrectivaWithPatch() throws Exception {
        // Initialize the database
        insertedAccionCorrectiva = accionCorrectivaRepository.saveAndFlush(accionCorrectiva);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the accionCorrectiva using partial update
        AccionCorrectiva partialUpdatedAccionCorrectiva = new AccionCorrectiva();
        partialUpdatedAccionCorrectiva.setId(accionCorrectiva.getId());

        partialUpdatedAccionCorrectiva
            .descripcion(UPDATED_DESCRIPCION)
            .prioridad(UPDATED_PRIORIDAD)
            .estado(UPDATED_ESTADO)
            .fechaLimite(UPDATED_FECHA_LIMITE)
            .ticketUrl(UPDATED_TICKET_URL);

        restAccionCorrectivaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAccionCorrectiva.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAccionCorrectiva))
            )
            .andExpect(status().isOk());

        // Validate the AccionCorrectiva in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAccionCorrectivaUpdatableFieldsEquals(
            partialUpdatedAccionCorrectiva,
            getPersistedAccionCorrectiva(partialUpdatedAccionCorrectiva)
        );
    }

    @Test
    @Transactional
    void patchNonExistingAccionCorrectiva() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        accionCorrectiva.setId(longCount.incrementAndGet());

        // Create the AccionCorrectiva
        AccionCorrectivaDTO accionCorrectivaDTO = accionCorrectivaMapper.toDto(accionCorrectiva);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAccionCorrectivaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, accionCorrectivaDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(accionCorrectivaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AccionCorrectiva in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAccionCorrectiva() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        accionCorrectiva.setId(longCount.incrementAndGet());

        // Create the AccionCorrectiva
        AccionCorrectivaDTO accionCorrectivaDTO = accionCorrectivaMapper.toDto(accionCorrectiva);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAccionCorrectivaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(accionCorrectivaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AccionCorrectiva in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAccionCorrectiva() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        accionCorrectiva.setId(longCount.incrementAndGet());

        // Create the AccionCorrectiva
        AccionCorrectivaDTO accionCorrectivaDTO = accionCorrectivaMapper.toDto(accionCorrectiva);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAccionCorrectivaMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(accionCorrectivaDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AccionCorrectiva in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAccionCorrectiva() throws Exception {
        // Initialize the database
        insertedAccionCorrectiva = accionCorrectivaRepository.saveAndFlush(accionCorrectiva);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the accionCorrectiva
        restAccionCorrectivaMockMvc
            .perform(delete(ENTITY_API_URL_ID, accionCorrectiva.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return accionCorrectivaRepository.count();
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

    protected AccionCorrectiva getPersistedAccionCorrectiva(AccionCorrectiva accionCorrectiva) {
        return accionCorrectivaRepository.findById(accionCorrectiva.getId()).orElseThrow();
    }

    protected void assertPersistedAccionCorrectivaToMatchAllProperties(AccionCorrectiva expectedAccionCorrectiva) {
        assertAccionCorrectivaAllPropertiesEquals(expectedAccionCorrectiva, getPersistedAccionCorrectiva(expectedAccionCorrectiva));
    }

    protected void assertPersistedAccionCorrectivaToMatchUpdatableProperties(AccionCorrectiva expectedAccionCorrectiva) {
        assertAccionCorrectivaAllUpdatablePropertiesEquals(
            expectedAccionCorrectiva,
            getPersistedAccionCorrectiva(expectedAccionCorrectiva)
        );
    }
}
