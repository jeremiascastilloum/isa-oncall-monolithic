package ar.edu.um.isa.oncall.web.rest;

import static ar.edu.um.isa.oncall.domain.PostmortemAsserts.*;
import static ar.edu.um.isa.oncall.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ar.edu.um.isa.oncall.IntegrationTest;
import ar.edu.um.isa.oncall.domain.Incidente;
import ar.edu.um.isa.oncall.domain.Postmortem;
import ar.edu.um.isa.oncall.repository.PostmortemRepository;
import ar.edu.um.isa.oncall.service.PostmortemService;
import ar.edu.um.isa.oncall.service.dto.PostmortemDTO;
import ar.edu.um.isa.oncall.service.mapper.PostmortemMapper;
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
 * Integration tests for the {@link PostmortemResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PostmortemResourceIT {

    private static final String DEFAULT_TITULO = "AAAAAAAAAA";
    private static final String UPDATED_TITULO = "BBBBBBBBBB";

    private static final String DEFAULT_RESUMEN = "AAAAAAAAAA";
    private static final String UPDATED_RESUMEN = "BBBBBBBBBB";

    private static final String DEFAULT_CAUSA_RAIZ = "AAAAAAAAAA";
    private static final String UPDATED_CAUSA_RAIZ = "BBBBBBBBBB";

    private static final String DEFAULT_LINEA_DE_TIEMPO = "AAAAAAAAAA";
    private static final String UPDATED_LINEA_DE_TIEMPO = "BBBBBBBBBB";

    private static final String DEFAULT_LECCIONES_APRENDIDAS = "AAAAAAAAAA";
    private static final String UPDATED_LECCIONES_APRENDIDAS = "BBBBBBBBBB";

    private static final Boolean DEFAULT_PUBLICADO = false;
    private static final Boolean UPDATED_PUBLICADO = true;

    private static final Instant DEFAULT_PUBLICADO_EN = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PUBLICADO_EN = Instant.ofEpochMilli(1701729143509L);

    private static final String ENTITY_API_URL = "/api/postmortems";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PostmortemRepository postmortemRepository;

    @Mock
    private PostmortemRepository postmortemRepositoryMock;

    @Autowired
    private PostmortemMapper postmortemMapper;

    @Mock
    private PostmortemService postmortemServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPostmortemMockMvc;

    private Postmortem postmortem;

    private Postmortem insertedPostmortem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Postmortem createEntity(EntityManager em) {
        Postmortem postmortem = new Postmortem()
            .titulo(DEFAULT_TITULO)
            .resumen(DEFAULT_RESUMEN)
            .causaRaiz(DEFAULT_CAUSA_RAIZ)
            .lineaDeTiempo(DEFAULT_LINEA_DE_TIEMPO)
            .leccionesAprendidas(DEFAULT_LECCIONES_APRENDIDAS)
            .publicado(DEFAULT_PUBLICADO)
            .publicadoEn(DEFAULT_PUBLICADO_EN);
        // Add required entity
        Incidente incidente;
        if (TestUtil.findAll(em, Incidente.class).isEmpty()) {
            incidente = IncidenteResourceIT.createEntity();
            em.persist(incidente);
            em.flush();
        } else {
            incidente = TestUtil.findAll(em, Incidente.class).get(0);
        }
        postmortem.setIncidente(incidente);
        return postmortem;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Postmortem createUpdatedEntity(EntityManager em) {
        Postmortem updatedPostmortem = new Postmortem()
            .titulo(UPDATED_TITULO)
            .resumen(UPDATED_RESUMEN)
            .causaRaiz(UPDATED_CAUSA_RAIZ)
            .lineaDeTiempo(UPDATED_LINEA_DE_TIEMPO)
            .leccionesAprendidas(UPDATED_LECCIONES_APRENDIDAS)
            .publicado(UPDATED_PUBLICADO)
            .publicadoEn(UPDATED_PUBLICADO_EN);
        // Add required entity
        Incidente incidente;
        if (TestUtil.findAll(em, Incidente.class).isEmpty()) {
            incidente = IncidenteResourceIT.createUpdatedEntity();
            em.persist(incidente);
            em.flush();
        } else {
            incidente = TestUtil.findAll(em, Incidente.class).get(0);
        }
        updatedPostmortem.setIncidente(incidente);
        return updatedPostmortem;
    }

    @BeforeEach
    void initTest() {
        postmortem = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedPostmortem != null) {
            postmortemRepository.delete(insertedPostmortem);
            insertedPostmortem = null;
        }
    }

    @Test
    @Transactional
    void createPostmortem() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Postmortem
        PostmortemDTO postmortemDTO = postmortemMapper.toDto(postmortem);
        var returnedPostmortemDTO = om.readValue(
            restPostmortemMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(postmortemDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PostmortemDTO.class
        );

        // Validate the Postmortem in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPostmortem = postmortemMapper.toEntity(returnedPostmortemDTO);
        assertPostmortemUpdatableFieldsEquals(returnedPostmortem, getPersistedPostmortem(returnedPostmortem));

        insertedPostmortem = returnedPostmortem;
    }

    @Test
    @Transactional
    void createPostmortemWithExistingId() throws Exception {
        // Create the Postmortem with an existing ID
        postmortem.setId(1L);
        PostmortemDTO postmortemDTO = postmortemMapper.toDto(postmortem);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPostmortemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(postmortemDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Postmortem in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTituloIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        postmortem.setTitulo(null);

        // Create the Postmortem, which fails.
        PostmortemDTO postmortemDTO = postmortemMapper.toDto(postmortem);

        restPostmortemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(postmortemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkResumenIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        postmortem.setResumen(null);

        // Create the Postmortem, which fails.
        PostmortemDTO postmortemDTO = postmortemMapper.toDto(postmortem);

        restPostmortemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(postmortemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCausaRaizIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        postmortem.setCausaRaiz(null);

        // Create the Postmortem, which fails.
        PostmortemDTO postmortemDTO = postmortemMapper.toDto(postmortem);

        restPostmortemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(postmortemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPublicadoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        postmortem.setPublicado(null);

        // Create the Postmortem, which fails.
        PostmortemDTO postmortemDTO = postmortemMapper.toDto(postmortem);

        restPostmortemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(postmortemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPostmortems() throws Exception {
        // Initialize the database
        insertedPostmortem = postmortemRepository.saveAndFlush(postmortem);

        // Get all the postmortemList
        restPostmortemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(postmortem.getId().intValue())))
            .andExpect(jsonPath("$.[*].titulo").value(hasItem(DEFAULT_TITULO)))
            .andExpect(jsonPath("$.[*].resumen").value(hasItem(DEFAULT_RESUMEN)))
            .andExpect(jsonPath("$.[*].causaRaiz").value(hasItem(DEFAULT_CAUSA_RAIZ)))
            .andExpect(jsonPath("$.[*].lineaDeTiempo").value(hasItem(DEFAULT_LINEA_DE_TIEMPO)))
            .andExpect(jsonPath("$.[*].leccionesAprendidas").value(hasItem(DEFAULT_LECCIONES_APRENDIDAS)))
            .andExpect(jsonPath("$.[*].publicado").value(hasItem(DEFAULT_PUBLICADO)))
            .andExpect(jsonPath("$.[*].publicadoEn").value(hasItem(DEFAULT_PUBLICADO_EN.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPostmortemsWithEagerRelationshipsIsEnabled() throws Exception {
        when(postmortemServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPostmortemMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(postmortemServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPostmortemsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(postmortemServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPostmortemMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(postmortemRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getPostmortem() throws Exception {
        // Initialize the database
        insertedPostmortem = postmortemRepository.saveAndFlush(postmortem);

        // Get the postmortem
        restPostmortemMockMvc
            .perform(get(ENTITY_API_URL_ID, postmortem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(postmortem.getId().intValue()))
            .andExpect(jsonPath("$.titulo").value(DEFAULT_TITULO))
            .andExpect(jsonPath("$.resumen").value(DEFAULT_RESUMEN))
            .andExpect(jsonPath("$.causaRaiz").value(DEFAULT_CAUSA_RAIZ))
            .andExpect(jsonPath("$.lineaDeTiempo").value(DEFAULT_LINEA_DE_TIEMPO))
            .andExpect(jsonPath("$.leccionesAprendidas").value(DEFAULT_LECCIONES_APRENDIDAS))
            .andExpect(jsonPath("$.publicado").value(DEFAULT_PUBLICADO))
            .andExpect(jsonPath("$.publicadoEn").value(DEFAULT_PUBLICADO_EN.toString()));
    }

    @Test
    @Transactional
    void getNonExistingPostmortem() throws Exception {
        // Get the postmortem
        restPostmortemMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPostmortem() throws Exception {
        // Initialize the database
        insertedPostmortem = postmortemRepository.saveAndFlush(postmortem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the postmortem
        Postmortem updatedPostmortem = postmortemRepository.findById(postmortem.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPostmortem are not directly saved in db
        em.detach(updatedPostmortem);
        updatedPostmortem
            .titulo(UPDATED_TITULO)
            .resumen(UPDATED_RESUMEN)
            .causaRaiz(UPDATED_CAUSA_RAIZ)
            .lineaDeTiempo(UPDATED_LINEA_DE_TIEMPO)
            .leccionesAprendidas(UPDATED_LECCIONES_APRENDIDAS)
            .publicado(UPDATED_PUBLICADO)
            .publicadoEn(UPDATED_PUBLICADO_EN);
        PostmortemDTO postmortemDTO = postmortemMapper.toDto(updatedPostmortem);

        restPostmortemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, postmortemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(postmortemDTO))
            )
            .andExpect(status().isOk());

        // Validate the Postmortem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPostmortemToMatchAllProperties(updatedPostmortem);
    }

    @Test
    @Transactional
    void putNonExistingPostmortem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        postmortem.setId(longCount.incrementAndGet());

        // Create the Postmortem
        PostmortemDTO postmortemDTO = postmortemMapper.toDto(postmortem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPostmortemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, postmortemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(postmortemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Postmortem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPostmortem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        postmortem.setId(longCount.incrementAndGet());

        // Create the Postmortem
        PostmortemDTO postmortemDTO = postmortemMapper.toDto(postmortem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostmortemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(postmortemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Postmortem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPostmortem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        postmortem.setId(longCount.incrementAndGet());

        // Create the Postmortem
        PostmortemDTO postmortemDTO = postmortemMapper.toDto(postmortem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostmortemMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(postmortemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Postmortem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePostmortemWithPatch() throws Exception {
        // Initialize the database
        insertedPostmortem = postmortemRepository.saveAndFlush(postmortem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the postmortem using partial update
        Postmortem partialUpdatedPostmortem = new Postmortem();
        partialUpdatedPostmortem.setId(postmortem.getId());

        partialUpdatedPostmortem
            .titulo(UPDATED_TITULO)
            .causaRaiz(UPDATED_CAUSA_RAIZ)
            .leccionesAprendidas(UPDATED_LECCIONES_APRENDIDAS)
            .publicado(UPDATED_PUBLICADO)
            .publicadoEn(UPDATED_PUBLICADO_EN);

        restPostmortemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPostmortem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPostmortem))
            )
            .andExpect(status().isOk());

        // Validate the Postmortem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPostmortemUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPostmortem, postmortem),
            getPersistedPostmortem(postmortem)
        );
    }

    @Test
    @Transactional
    void fullUpdatePostmortemWithPatch() throws Exception {
        // Initialize the database
        insertedPostmortem = postmortemRepository.saveAndFlush(postmortem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the postmortem using partial update
        Postmortem partialUpdatedPostmortem = new Postmortem();
        partialUpdatedPostmortem.setId(postmortem.getId());

        partialUpdatedPostmortem
            .titulo(UPDATED_TITULO)
            .resumen(UPDATED_RESUMEN)
            .causaRaiz(UPDATED_CAUSA_RAIZ)
            .lineaDeTiempo(UPDATED_LINEA_DE_TIEMPO)
            .leccionesAprendidas(UPDATED_LECCIONES_APRENDIDAS)
            .publicado(UPDATED_PUBLICADO)
            .publicadoEn(UPDATED_PUBLICADO_EN);

        restPostmortemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPostmortem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPostmortem))
            )
            .andExpect(status().isOk());

        // Validate the Postmortem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPostmortemUpdatableFieldsEquals(partialUpdatedPostmortem, getPersistedPostmortem(partialUpdatedPostmortem));
    }

    @Test
    @Transactional
    void patchNonExistingPostmortem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        postmortem.setId(longCount.incrementAndGet());

        // Create the Postmortem
        PostmortemDTO postmortemDTO = postmortemMapper.toDto(postmortem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPostmortemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, postmortemDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(postmortemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Postmortem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPostmortem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        postmortem.setId(longCount.incrementAndGet());

        // Create the Postmortem
        PostmortemDTO postmortemDTO = postmortemMapper.toDto(postmortem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostmortemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(postmortemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Postmortem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPostmortem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        postmortem.setId(longCount.incrementAndGet());

        // Create the Postmortem
        PostmortemDTO postmortemDTO = postmortemMapper.toDto(postmortem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostmortemMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(postmortemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Postmortem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePostmortem() throws Exception {
        // Initialize the database
        insertedPostmortem = postmortemRepository.saveAndFlush(postmortem);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the postmortem
        restPostmortemMockMvc
            .perform(delete(ENTITY_API_URL_ID, postmortem.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return postmortemRepository.count();
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

    protected Postmortem getPersistedPostmortem(Postmortem postmortem) {
        return postmortemRepository.findById(postmortem.getId()).orElseThrow();
    }

    protected void assertPersistedPostmortemToMatchAllProperties(Postmortem expectedPostmortem) {
        assertPostmortemAllPropertiesEquals(expectedPostmortem, getPersistedPostmortem(expectedPostmortem));
    }

    protected void assertPersistedPostmortemToMatchUpdatableProperties(Postmortem expectedPostmortem) {
        assertPostmortemAllUpdatablePropertiesEquals(expectedPostmortem, getPersistedPostmortem(expectedPostmortem));
    }
}
