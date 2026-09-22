package ar.edu.um.isa.oncall.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ar.edu.um.isa.oncall.IntegrationTest;
import ar.edu.um.isa.oncall.domain.Alerta;
import ar.edu.um.isa.oncall.domain.Equipo;
import ar.edu.um.isa.oncall.domain.EventoDeIncidente;
import ar.edu.um.isa.oncall.domain.Incidente;
import ar.edu.um.isa.oncall.domain.Servicio;
import ar.edu.um.isa.oncall.domain.enumeration.Criticidad;
import ar.edu.um.isa.oncall.domain.enumeration.Entorno;
import ar.edu.um.isa.oncall.domain.enumeration.EstadoIncidente;
import ar.edu.um.isa.oncall.domain.enumeration.OrigenAlerta;
import ar.edu.um.isa.oncall.domain.enumeration.Severidad;
import ar.edu.um.isa.oncall.domain.enumeration.TipoEvento;
import ar.edu.um.isa.oncall.repository.AlertaRepository;
import ar.edu.um.isa.oncall.repository.IncidenteRepository;
import ar.edu.um.isa.oncall.service.dto.AlertaEntranteDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests para {@link IngestaDeAlertasResource}: la regla de deduplicacion
 * mirada de punta a punta, con la base real.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class IngestaDeAlertasResourceIT {

    private static final String API_INGESTA = "/api/alertas/ingesta";

    private static final String FINGERPRINT = "disco-lleno-db-01";

    private static final Instant RECIBIDA_EN = Instant.parse("2026-03-10T03:00:00Z");

    private static final AtomicInteger SECUENCIA = new AtomicInteger();

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restIngestaMockMvc;

    @Autowired
    private AlertaRepository alertaRepository;

    @Autowired
    private IncidenteRepository incidenteRepository;

    @Test
    @Transactional
    @DisplayName("La primera alerta de un fingerprint abre un incidente")
    void laPrimeraAlertaAbreUnIncidente() throws Exception {
        Servicio servicio = servicioPersistido(Criticidad.TIER1);
        long incidentesAntes = incidenteRepository.count();

        restIngestaMockMvc
            .perform(
                post(API_INGESTA)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(alertaEntrante(servicio, FINGERPRINT, RECIBIDA_EN)))
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.accion").value("INCIDENTE_ABIERTO"))
            .andExpect(jsonPath("$.duplicada").value(false))
            .andExpect(jsonPath("$.ocurrencias").value(1))
            .andExpect(jsonPath("$.incidenteId").isNotEmpty());

        assertThat(incidenteRepository.count()).isEqualTo(incidentesAntes + 1);

        Incidente incidente = incidenteDe(servicio);
        assertThat(incidente.getEstado()).isEqualTo(EstadoIncidente.ABIERTO);
        assertThat(incidente.getSeveridad()).isEqualTo(Severidad.SEV1);
        assertThat(incidente.getDetectadoEn()).isEqualTo(RECIBIDA_EN);

        List<Alerta> alertas = alertasDe(servicio);
        assertThat(alertas).hasSize(1);
        assertThat(alertas.get(0).getProcesada()).isTrue();
        assertThat(alertas.get(0).getIncidente().getId()).isEqualTo(incidente.getId());

        assertThat(eventosDe(incidente))
            .singleElement()
            .satisfies(evento -> {
                assertThat(evento.getTipo()).isEqualTo(TipoEvento.CREACION);
                assertThat(evento.getAutomatico()).isTrue();
            });
    }

    @Test
    @Transactional
    @DisplayName("Tres alertas con el mismo fingerprint colapsan en un unico incidente")
    void tresAlertasIgualesColapsanEnUnIncidente() throws Exception {
        Servicio servicio = servicioPersistido(Criticidad.TIER2);
        long incidentesAntes = incidenteRepository.count();

        ingestar(servicio, FINGERPRINT, RECIBIDA_EN).andExpect(jsonPath("$.accion").value("INCIDENTE_ABIERTO"));

        ingestar(servicio, FINGERPRINT, RECIBIDA_EN.plusSeconds(180))
            .andExpect(jsonPath("$.accion").value("ALERTA_DEDUPLICADA"))
            .andExpect(jsonPath("$.duplicada").value(true))
            .andExpect(jsonPath("$.ocurrencias").value(2));

        ingestar(servicio, FINGERPRINT, RECIBIDA_EN.plusSeconds(360))
            .andExpect(jsonPath("$.accion").value("ALERTA_DEDUPLICADA"))
            .andExpect(jsonPath("$.ocurrencias").value(3));

        assertThat(incidenteRepository.count()).isEqualTo(incidentesAntes + 1);
        assertThat(alertasDe(servicio))
            .hasSize(3)
            .allSatisfy(alerta -> assertThat(alerta.getProcesada()).isTrue());

        Incidente incidente = incidenteDe(servicio);
        assertThat(alertasDe(servicio)).allSatisfy(alerta -> assertThat(alerta.getIncidente().getId()).isEqualTo(incidente.getId()));
        assertThat(eventosDe(incidente))
            .hasSize(3)
            .filteredOn(evento -> evento.getTipo() == TipoEvento.NOTA)
            .hasSize(2);
    }

    @Test
    @Transactional
    @DisplayName("Dos fingerprints distintos sobre el mismo servicio son dos incidentes")
    void dosFingerprintsDistintosAbrenDosIncidentes() throws Exception {
        Servicio servicio = servicioPersistido(Criticidad.TIER1);
        long incidentesAntes = incidenteRepository.count();

        ingestar(servicio, FINGERPRINT, RECIBIDA_EN).andExpect(jsonPath("$.accion").value("INCIDENTE_ABIERTO"));
        ingestar(servicio, "memoria-al-limite-db-01", RECIBIDA_EN).andExpect(jsonPath("$.accion").value("INCIDENTE_ABIERTO"));

        assertThat(incidenteRepository.count()).isEqualTo(incidentesAntes + 2);
    }

    @Test
    @Transactional
    @DisplayName("El mismo fingerprint sobre dos servicios distintos son dos incidentes")
    void elMismoFingerprintEnDosServiciosAbreDosIncidentes() throws Exception {
        Servicio pagos = servicioPersistido(Criticidad.TIER1);
        Servicio reportes = servicioPersistido(Criticidad.TIER3);
        long incidentesAntes = incidenteRepository.count();

        ingestar(pagos, FINGERPRINT, RECIBIDA_EN).andExpect(jsonPath("$.accion").value("INCIDENTE_ABIERTO"));
        ingestar(reportes, FINGERPRINT, RECIBIDA_EN).andExpect(jsonPath("$.accion").value("INCIDENTE_ABIERTO"));

        assertThat(incidenteRepository.count()).isEqualTo(incidentesAntes + 2);
        assertThat(incidenteDe(pagos).getSeveridad()).isEqualTo(Severidad.SEV1);
        assertThat(incidenteDe(reportes).getSeveridad()).isEqualTo(Severidad.SEV3);
    }

    @Test
    @Transactional
    @DisplayName("Si el incidente ya se cerro, la alerta que vuelve abre uno nuevo")
    void unaAlertaQueVuelveDespuesDeCerrarAbreUnIncidenteNuevo() throws Exception {
        Servicio servicio = servicioPersistido(Criticidad.TIER1);
        long incidentesAntes = incidenteRepository.count();

        ingestar(servicio, FINGERPRINT, RECIBIDA_EN).andExpect(jsonPath("$.accion").value("INCIDENTE_ABIERTO"));

        Incidente incidente = incidenteDe(servicio);
        incidente.setEstado(EstadoIncidente.CERRADO);
        incidenteRepository.saveAndFlush(incidente);

        ingestar(servicio, FINGERPRINT, RECIBIDA_EN.plusSeconds(600)).andExpect(jsonPath("$.accion").value("INCIDENTE_ABIERTO"));

        assertThat(incidenteRepository.count()).isEqualTo(incidentesAntes + 2);
    }

    @Test
    @Transactional
    @DisplayName("Fuera de la ventana de deduplicacion se abre un incidente nuevo")
    void fueraDeLaVentanaSeAbreUnIncidenteNuevo() throws Exception {
        Servicio servicio = servicioPersistido(Criticidad.TIER1);
        long incidentesAntes = incidenteRepository.count();

        ingestar(servicio, FINGERPRINT, RECIBIDA_EN).andExpect(jsonPath("$.accion").value("INCIDENTE_ABIERTO"));

        // la ventana configurada para los tests es de 120 minutos
        ingestar(servicio, FINGERPRINT, RECIBIDA_EN.plusSeconds(121 * 60)).andExpect(jsonPath("$.accion").value("INCIDENTE_ABIERTO"));

        assertThat(incidenteRepository.count()).isEqualTo(incidentesAntes + 2);
    }

    @Test
    @Transactional
    @DisplayName("Una alerta de un servicio que no existe se rechaza con 400")
    void rechazaUnaAlertaDeUnServicioInexistente() throws Exception {
        long alertasAntes = alertaRepository.count();

        AlertaEntranteDTO entrante = new AlertaEntranteDTO();
        entrante.setServicioId(Long.MAX_VALUE);
        entrante.setFingerprint(FINGERPRINT);
        entrante.setOrigen(OrigenAlerta.PROMETHEUS);
        entrante.setResumen("Disco al 95% en db-01");
        entrante.setRecibidaEn(RECIBIDA_EN);

        restIngestaMockMvc
            .perform(post(API_INGESTA).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(entrante)))
            .andExpect(status().isBadRequest());

        assertThat(alertaRepository.count()).isEqualTo(alertasAntes);
    }

    @Test
    @Transactional
    @DisplayName("Una alerta sin fingerprint no llega a la regla")
    void rechazaUnaAlertaSinFingerprint() throws Exception {
        Servicio servicio = servicioPersistido(Criticidad.TIER1);

        AlertaEntranteDTO entrante = alertaEntrante(servicio, FINGERPRINT, RECIBIDA_EN);
        entrante.setFingerprint(null);

        restIngestaMockMvc
            .perform(post(API_INGESTA).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(entrante)))
            .andExpect(status().isBadRequest());
    }

    // ---------------------------------------------------------------------
    // Utilidades
    // ---------------------------------------------------------------------

    private ResultActions ingestar(Servicio servicio, String fingerprint, Instant recibidaEn) throws Exception {
        return restIngestaMockMvc
            .perform(
                post(API_INGESTA)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(alertaEntrante(servicio, fingerprint, recibidaEn)))
            )
            .andExpect(status().isCreated());
    }

    private AlertaEntranteDTO alertaEntrante(Servicio servicio, String fingerprint, Instant recibidaEn) {
        AlertaEntranteDTO entrante = new AlertaEntranteDTO();
        entrante.setServicioId(servicio.getId());
        entrante.setFingerprint(fingerprint);
        entrante.setOrigen(OrigenAlerta.PROMETHEUS);
        entrante.setResumen("Disco al 95% en db-01");
        entrante.setPayload("{\"instance\":\"db-01\"}");
        entrante.setRecibidaEn(recibidaEn);
        return entrante;
    }

    private Servicio servicioPersistido(Criticidad criticidad) {
        int n = SECUENCIA.incrementAndGet();
        Equipo equipo = new Equipo().nombre("equipo-dedup-" + n).emailContacto("equipo-dedup-" + n + "@um.edu.ar");
        em.persist(equipo);

        Servicio servicio = new Servicio()
            .nombre("servicio-dedup-" + n)
            .criticidad(criticidad)
            .entorno(Entorno.PRODUCCION)
            .activo(true)
            .equipo(equipo);
        em.persist(servicio);
        em.flush();
        return servicio;
    }

    private Incidente incidenteDe(Servicio servicio) {
        List<Alerta> alertas = alertasDe(servicio);
        assertThat(alertas).isNotEmpty();
        return alertas.get(0).getIncidente();
    }

    private List<Alerta> alertasDe(Servicio servicio) {
        return em
            .createQuery("select a from Alerta a where a.servicio.id = :servicioId order by a.recibidaEn asc", Alerta.class)
            .setParameter("servicioId", servicio.getId())
            .getResultList();
    }

    private List<EventoDeIncidente> eventosDe(Incidente incidente) {
        return em
            .createQuery("select e from EventoDeIncidente e where e.incidente.id = :incidenteId", EventoDeIncidente.class)
            .setParameter("incidenteId", incidente.getId())
            .getResultList();
    }
}
