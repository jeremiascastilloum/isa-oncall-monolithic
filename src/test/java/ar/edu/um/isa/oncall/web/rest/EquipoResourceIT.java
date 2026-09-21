package ar.edu.um.isa.oncall.web.rest;

import static ar.edu.um.isa.oncall.domain.EquipoAsserts.*;
import static ar.edu.um.isa.oncall.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ar.edu.um.isa.oncall.IntegrationTest;
import ar.edu.um.isa.oncall.domain.Equipo;
import ar.edu.um.isa.oncall.repository.EquipoRepository;
import ar.edu.um.isa.oncall.service.dto.EquipoDTO;
import ar.edu.um.isa.oncall.service.mapper.EquipoMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link EquipoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class EquipoResourceIT {

    private static final String DEFAULT_NOMBRE = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL_CONTACTO = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL_CONTACTO = "BBBBBBBBBB";

    private static final String DEFAULT_CANAL_CHAT = "AAAAAAAAAA";
    private static final String UPDATED_CANAL_CHAT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/equipos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private EquipoMapper equipoMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEquipoMockMvc;

    private Equipo equipo;

    private Equipo insertedEquipo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Equipo createEntity() {
        return new Equipo().nombre(DEFAULT_NOMBRE).emailContacto(DEFAULT_EMAIL_CONTACTO).canalChat(DEFAULT_CANAL_CHAT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Equipo createUpdatedEntity() {
        return new Equipo().nombre(UPDATED_NOMBRE).emailContacto(UPDATED_EMAIL_CONTACTO).canalChat(UPDATED_CANAL_CHAT);
    }

    @BeforeEach
    void initTest() {
        equipo = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedEquipo != null) {
            equipoRepository.delete(insertedEquipo);
            insertedEquipo = null;
        }
    }

    @Test
    @Transactional
    void createEquipo() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Equipo
        EquipoDTO equipoDTO = equipoMapper.toDto(equipo);
        var returnedEquipoDTO = om.readValue(
            restEquipoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(equipoDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            EquipoDTO.class
        );

        // Validate the Equipo in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedEquipo = equipoMapper.toEntity(returnedEquipoDTO);
        assertEquipoUpdatableFieldsEquals(returnedEquipo, getPersistedEquipo(returnedEquipo));

        insertedEquipo = returnedEquipo;
    }

    @Test
    @Transactional
    void createEquipoWithExistingId() throws Exception {
        // Create the Equipo with an existing ID
        equipo.setId(1L);
        EquipoDTO equipoDTO = equipoMapper.toDto(equipo);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEquipoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(equipoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Equipo in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNombreIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        equipo.setNombre(null);

        // Create the Equipo, which fails.
        EquipoDTO equipoDTO = equipoMapper.toDto(equipo);

        restEquipoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(equipoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEmailContactoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        equipo.setEmailContacto(null);

        // Create the Equipo, which fails.
        EquipoDTO equipoDTO = equipoMapper.toDto(equipo);

        restEquipoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(equipoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllEquipos() throws Exception {
        // Initialize the database
        insertedEquipo = equipoRepository.saveAndFlush(equipo);

        // Get all the equipoList
        restEquipoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(equipo.getId().intValue())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE)))
            .andExpect(jsonPath("$.[*].emailContacto").value(hasItem(DEFAULT_EMAIL_CONTACTO)))
            .andExpect(jsonPath("$.[*].canalChat").value(hasItem(DEFAULT_CANAL_CHAT)));
    }

    @Test
    @Transactional
    void getEquipo() throws Exception {
        // Initialize the database
        insertedEquipo = equipoRepository.saveAndFlush(equipo);

        // Get the equipo
        restEquipoMockMvc
            .perform(get(ENTITY_API_URL_ID, equipo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(equipo.getId().intValue()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE))
            .andExpect(jsonPath("$.emailContacto").value(DEFAULT_EMAIL_CONTACTO))
            .andExpect(jsonPath("$.canalChat").value(DEFAULT_CANAL_CHAT));
    }

    @Test
    @Transactional
    void getNonExistingEquipo() throws Exception {
        // Get the equipo
        restEquipoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEquipo() throws Exception {
        // Initialize the database
        insertedEquipo = equipoRepository.saveAndFlush(equipo);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the equipo
        Equipo updatedEquipo = equipoRepository.findById(equipo.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedEquipo are not directly saved in db
        em.detach(updatedEquipo);
        updatedEquipo.nombre(UPDATED_NOMBRE).emailContacto(UPDATED_EMAIL_CONTACTO).canalChat(UPDATED_CANAL_CHAT);
        EquipoDTO equipoDTO = equipoMapper.toDto(updatedEquipo);

        restEquipoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, equipoDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(equipoDTO))
            )
            .andExpect(status().isOk());

        // Validate the Equipo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEquipoToMatchAllProperties(updatedEquipo);
    }

    @Test
    @Transactional
    void putNonExistingEquipo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        equipo.setId(longCount.incrementAndGet());

        // Create the Equipo
        EquipoDTO equipoDTO = equipoMapper.toDto(equipo);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEquipoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, equipoDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(equipoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Equipo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEquipo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        equipo.setId(longCount.incrementAndGet());

        // Create the Equipo
        EquipoDTO equipoDTO = equipoMapper.toDto(equipo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEquipoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(equipoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Equipo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEquipo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        equipo.setId(longCount.incrementAndGet());

        // Create the Equipo
        EquipoDTO equipoDTO = equipoMapper.toDto(equipo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEquipoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(equipoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Equipo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEquipoWithPatch() throws Exception {
        // Initialize the database
        insertedEquipo = equipoRepository.saveAndFlush(equipo);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the equipo using partial update
        Equipo partialUpdatedEquipo = new Equipo();
        partialUpdatedEquipo.setId(equipo.getId());

        restEquipoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEquipo.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEquipo))
            )
            .andExpect(status().isOk());

        // Validate the Equipo in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEquipoUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedEquipo, equipo), getPersistedEquipo(equipo));
    }

    @Test
    @Transactional
    void fullUpdateEquipoWithPatch() throws Exception {
        // Initialize the database
        insertedEquipo = equipoRepository.saveAndFlush(equipo);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the equipo using partial update
        Equipo partialUpdatedEquipo = new Equipo();
        partialUpdatedEquipo.setId(equipo.getId());

        partialUpdatedEquipo.nombre(UPDATED_NOMBRE).emailContacto(UPDATED_EMAIL_CONTACTO).canalChat(UPDATED_CANAL_CHAT);

        restEquipoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEquipo.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEquipo))
            )
            .andExpect(status().isOk());

        // Validate the Equipo in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEquipoUpdatableFieldsEquals(partialUpdatedEquipo, getPersistedEquipo(partialUpdatedEquipo));
    }

    @Test
    @Transactional
    void patchNonExistingEquipo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        equipo.setId(longCount.incrementAndGet());

        // Create the Equipo
        EquipoDTO equipoDTO = equipoMapper.toDto(equipo);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEquipoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, equipoDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(equipoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Equipo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEquipo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        equipo.setId(longCount.incrementAndGet());

        // Create the Equipo
        EquipoDTO equipoDTO = equipoMapper.toDto(equipo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEquipoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(equipoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Equipo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEquipo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        equipo.setId(longCount.incrementAndGet());

        // Create the Equipo
        EquipoDTO equipoDTO = equipoMapper.toDto(equipo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEquipoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(equipoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Equipo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEquipo() throws Exception {
        // Initialize the database
        insertedEquipo = equipoRepository.saveAndFlush(equipo);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the equipo
        restEquipoMockMvc
            .perform(delete(ENTITY_API_URL_ID, equipo.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return equipoRepository.count();
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

    protected Equipo getPersistedEquipo(Equipo equipo) {
        return equipoRepository.findById(equipo.getId()).orElseThrow();
    }

    protected void assertPersistedEquipoToMatchAllProperties(Equipo expectedEquipo) {
        assertEquipoAllPropertiesEquals(expectedEquipo, getPersistedEquipo(expectedEquipo));
    }

    protected void assertPersistedEquipoToMatchUpdatableProperties(Equipo expectedEquipo) {
        assertEquipoAllUpdatablePropertiesEquals(expectedEquipo, getPersistedEquipo(expectedEquipo));
    }
}
