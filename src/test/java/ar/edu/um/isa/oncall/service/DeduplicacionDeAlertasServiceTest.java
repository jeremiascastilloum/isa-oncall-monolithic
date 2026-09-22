package ar.edu.um.isa.oncall.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ar.edu.um.isa.oncall.domain.Alerta;
import ar.edu.um.isa.oncall.domain.EventoDeIncidente;
import ar.edu.um.isa.oncall.domain.Incidente;
import ar.edu.um.isa.oncall.domain.Servicio;
import ar.edu.um.isa.oncall.domain.enumeration.Criticidad;
import ar.edu.um.isa.oncall.domain.enumeration.EstadoIncidente;
import ar.edu.um.isa.oncall.domain.enumeration.OrigenAlerta;
import ar.edu.um.isa.oncall.domain.enumeration.Severidad;
import ar.edu.um.isa.oncall.domain.enumeration.TipoEvento;
import ar.edu.um.isa.oncall.repository.AlertaRepository;
import ar.edu.um.isa.oncall.repository.EventoDeIncidenteRepository;
import ar.edu.um.isa.oncall.repository.IncidenteDeduplicacionRepository;
import ar.edu.um.isa.oncall.repository.IncidenteRepository;
import ar.edu.um.isa.oncall.repository.ServicioRepository;
import ar.edu.um.isa.oncall.service.dto.AlertaEntranteDTO;
import ar.edu.um.isa.oncall.service.dto.ResultadoDeduplicacionDTO;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

/**
 * La regla de deduplicacion, probada sin base de datos: son decisiones, no consultas.
 */
@ExtendWith(MockitoExtension.class)
class DeduplicacionDeAlertasServiceTest {

    private static final long SERVICIO_ID = 7L;
    private static final long OTRO_SERVICIO_ID = 8L;
    private static final String FINGERPRINT = "disco-lleno-db-01";
    private static final Instant RECIBIDA_EN = Instant.parse("2026-03-10T03:00:00Z");
    private static final int VENTANA_MINUTOS = 120;

    @Mock
    private AlertaRepository alertaRepository;

    @Mock
    private IncidenteRepository incidenteRepository;

    @Mock
    private IncidenteDeduplicacionRepository incidenteDeduplicacionRepository;

    @Mock
    private EventoDeIncidenteRepository eventoDeIncidenteRepository;

    @Mock
    private ServicioRepository servicioRepository;

    @Captor
    private ArgumentCaptor<EventoDeIncidente> eventoCaptor;

    private DeduplicacionDeAlertasService servicioDeDeduplicacion;

    private Servicio servicio;

    @BeforeEach
    void init() {
        servicio = servicioConCriticidad(Criticidad.TIER1);
        servicioDeDeduplicacion = nuevoServicio(VENTANA_MINUTOS);
    }

    private DeduplicacionDeAlertasService nuevoServicio(int ventanaMinutos) {
        return new DeduplicacionDeAlertasService(
            alertaRepository,
            incidenteRepository,
            incidenteDeduplicacionRepository,
            eventoDeIncidenteRepository,
            servicioRepository,
            ventanaMinutos
        );
    }

    // ---------------------------------------------------------------------
    // Camino A · no hay nada abierto: la alerta abre un incidente
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("Una alerta con un fingerprint que nadie vio abre un incidente nuevo")
    void abreUnIncidenteCuandoElFingerprintEsNuevo() {
        darDeAltaElServicio();
        noHayIncidentesAbiertos();
        alertaGuardadaConId();
        incidenteGuardadoConId();

        ResultadoDeduplicacionDTO resultado = servicioDeDeduplicacion.ingestar(alertaEntrante());

        assertThat(resultado.getAccion()).isEqualTo(ResultadoDeduplicacionDTO.Accion.INCIDENTE_ABIERTO);
        assertThat(resultado.isDuplicada()).isFalse();
        assertThat(resultado.getOcurrencias()).isEqualTo(1);
        assertThat(resultado.getIncidenteId()).isNotNull();

        ArgumentCaptor<Incidente> incidenteCaptor = ArgumentCaptor.forClass(Incidente.class);
        verify(incidenteRepository).saveAndFlush(incidenteCaptor.capture());
        Incidente abierto = incidenteCaptor.getValue();
        assertThat(abierto.getEstado()).isEqualTo(EstadoIncidente.ABIERTO);
        assertThat(abierto.getTitulo()).isEqualTo("Disco al 95% en db-01");
        assertThat(abierto.getDetectadoEn()).isEqualTo(RECIBIDA_EN);
        assertThat(abierto.getServicios()).containsExactly(servicio);
    }

    @Test
    @DisplayName("La alerta que abre el incidente queda asociada a el y marcada como procesada")
    void laAlertaQueAbreElIncidenteQuedaAsociadaYProcesada() {
        darDeAltaElServicio();
        noHayIncidentesAbiertos();
        alertaGuardadaConId();
        incidenteGuardadoConId();

        servicioDeDeduplicacion.ingestar(alertaEntrante());

        ArgumentCaptor<Alerta> alertaCaptor = ArgumentCaptor.forClass(Alerta.class);
        verify(alertaRepository).saveAndFlush(alertaCaptor.capture());
        Alerta guardada = alertaCaptor.getValue();
        assertThat(guardada.getProcesada()).isTrue();
        assertThat(guardada.getIncidente()).isNotNull();
        assertThat(guardada.getServicio()).isEqualTo(servicio);
        assertThat(guardada.getFingerprint()).isEqualTo(FINGERPRINT);
    }

    @Test
    @DisplayName("Abrir un incidente deja una entrada de tipo CREACION en la linea de tiempo")
    void abrirUnIncidenteDejaUnEventoDeCreacion() {
        darDeAltaElServicio();
        noHayIncidentesAbiertos();
        alertaGuardadaConId();
        incidenteGuardadoConId();

        servicioDeDeduplicacion.ingestar(alertaEntrante());

        verify(eventoDeIncidenteRepository).save(eventoCaptor.capture());
        EventoDeIncidente evento = eventoCaptor.getValue();
        assertThat(evento.getTipo()).isEqualTo(TipoEvento.CREACION);
        assertThat(evento.getAutomatico()).isTrue();
        assertThat(evento.getOcurridoEn()).isEqualTo(RECIBIDA_EN);
        assertThat(evento.getDetalle()).contains(FINGERPRINT);
    }

    @ParameterizedTest(name = "un servicio {0} abre incidentes {1}")
    @CsvSource({ "TIER1,SEV1", "TIER2,SEV2", "TIER3,SEV3" })
    @DisplayName("La severidad del incidente se deriva de la criticidad del servicio")
    void derivaLaSeveridadDeLaCriticidadDelServicio(Criticidad criticidad, Severidad esperada) {
        servicio = servicioConCriticidad(criticidad);
        darDeAltaElServicio();
        noHayIncidentesAbiertos();
        alertaGuardadaConId();
        incidenteGuardadoConId();

        servicioDeDeduplicacion.ingestar(alertaEntrante());

        ArgumentCaptor<Incidente> incidenteCaptor = ArgumentCaptor.forClass(Incidente.class);
        verify(incidenteRepository).saveAndFlush(incidenteCaptor.capture());
        assertThat(incidenteCaptor.getValue().getSeveridad()).isEqualTo(esperada);
    }

    @Test
    @DisplayName("Un resumen mas largo que el titulo del incidente se recorta en vez de romper el alta")
    void recortaElResumenLargoParaQueEntreEnElTitulo() {
        darDeAltaElServicio();
        noHayIncidentesAbiertos();
        alertaGuardadaConId();
        incidenteGuardadoConId();

        AlertaEntranteDTO entrante = alertaEntrante();
        entrante.setResumen("x".repeat(200));

        servicioDeDeduplicacion.ingestar(entrante);

        ArgumentCaptor<Incidente> incidenteCaptor = ArgumentCaptor.forClass(Incidente.class);
        verify(incidenteRepository).saveAndFlush(incidenteCaptor.capture());
        assertThat(incidenteCaptor.getValue().getTitulo()).hasSize(140).endsWith("...");
    }

    // ---------------------------------------------------------------------
    // Camino B · ya hay un incidente abierto: la alerta se absorbe
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("Una alerta repetida se pega al incidente abierto y no abre otro")
    void deduplicaContraElIncidenteAbierto() {
        darDeAltaElServicio();
        Incidente abierto = incidenteAbierto(42L);
        hayUnIncidenteAbierto(abierto);
        when(incidenteDeduplicacionRepository.contarAlertasConFingerprint(42L, FINGERPRINT)).thenReturn(3L);
        alertaGuardadaConId();

        ResultadoDeduplicacionDTO resultado = servicioDeDeduplicacion.ingestar(alertaEntrante());

        assertThat(resultado.getAccion()).isEqualTo(ResultadoDeduplicacionDTO.Accion.ALERTA_DEDUPLICADA);
        assertThat(resultado.isDuplicada()).isTrue();
        assertThat(resultado.getIncidenteId()).isEqualTo(42L);
        assertThat(resultado.getOcurrencias()).isEqualTo(3);
        verify(incidenteRepository, never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("La alerta deduplicada tambien queda asociada al incidente y procesada")
    void laAlertaDeduplicadaQuedaAsociadaYProcesada() {
        darDeAltaElServicio();
        Incidente abierto = incidenteAbierto(42L);
        hayUnIncidenteAbierto(abierto);
        alertaGuardadaConId();

        servicioDeDeduplicacion.ingestar(alertaEntrante());

        ArgumentCaptor<Alerta> alertaCaptor = ArgumentCaptor.forClass(Alerta.class);
        verify(alertaRepository).saveAndFlush(alertaCaptor.capture());
        assertThat(alertaCaptor.getValue().getIncidente()).isEqualTo(abierto);
        assertThat(alertaCaptor.getValue().getProcesada()).isTrue();
    }

    @Test
    @DisplayName("Deduplicar deja una NOTA en la linea de tiempo con el numero de ocurrencia")
    void deduplicarDejaUnaNotaEnLaLineaDeTiempo() {
        darDeAltaElServicio();
        hayUnIncidenteAbierto(incidenteAbierto(42L));
        when(incidenteDeduplicacionRepository.contarAlertasConFingerprint(42L, FINGERPRINT)).thenReturn(3L);
        alertaGuardadaConId();

        servicioDeDeduplicacion.ingestar(alertaEntrante());

        verify(eventoDeIncidenteRepository).save(eventoCaptor.capture());
        EventoDeIncidente evento = eventoCaptor.getValue();
        assertThat(evento.getTipo()).isEqualTo(TipoEvento.NOTA);
        assertThat(evento.getAutomatico()).isTrue();
        assertThat(evento.getDetalle()).contains(FINGERPRINT).contains("ocurrencia numero 3");
    }

    // ---------------------------------------------------------------------
    // El criterio de busqueda: fingerprint + servicio + abierto + ventana
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("Solo se consideran incidentes del mismo fingerprint, del mismo servicio y no cerrados")
    void buscaPorFingerprintServicioYEstadoNoCerrado() {
        darDeAltaElServicio();
        noHayIncidentesAbiertos();
        alertaGuardadaConId();
        incidenteGuardadoConId();

        servicioDeDeduplicacion.ingestar(alertaEntrante());

        ArgumentCaptor<String> fingerprintCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Long> servicioCaptor = ArgumentCaptor.forClass(Long.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Collection<EstadoIncidente>> estadosCaptor = ArgumentCaptor.forClass(Collection.class);
        ArgumentCaptor<Instant> desdeCaptor = ArgumentCaptor.forClass(Instant.class);

        verify(incidenteDeduplicacionRepository).buscarIncidentesAbiertosPorFingerprint(
            fingerprintCaptor.capture(),
            servicioCaptor.capture(),
            estadosCaptor.capture(),
            desdeCaptor.capture(),
            any(Pageable.class)
        );

        assertThat(fingerprintCaptor.getValue()).isEqualTo(FINGERPRINT);
        assertThat(servicioCaptor.getValue()).isEqualTo(SERVICIO_ID);
        assertThat(estadosCaptor.getValue()).containsExactlyInAnyOrder(EstadoIncidente.RESUELTO, EstadoIncidente.CERRADO);
        assertThat(desdeCaptor.getValue()).isEqualTo(RECIBIDA_EN.minus(Duration.ofMinutes(VENTANA_MINUTOS)));
    }

    @ParameterizedTest(name = "un incidente {0} sigue absorbiendo alertas")
    @EnumSource(value = EstadoIncidente.class, names = { "ABIERTO", "RECONOCIDO", "MITIGADO" })
    @DisplayName("Los estados que todavia no terminaron el incidente siguen deduplicando")
    void losEstadosNoTerminadosSiguenDeduplicando(EstadoIncidente estado) {
        darDeAltaElServicio();
        hayUnIncidenteAbierto(incidenteAbierto(42L).estado(estado));
        alertaGuardadaConId();

        ResultadoDeduplicacionDTO resultado = servicioDeDeduplicacion.ingestar(alertaEntrante());

        assertThat(resultado.getAccion()).isEqualTo(ResultadoDeduplicacionDTO.Accion.ALERTA_DEDUPLICADA);
        verify(incidenteRepository, never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("Una ventana mas corta cambia el piso de la busqueda")
    void laVentanaConfiguradaDefineElPisoDeLaBusqueda() {
        servicioDeDeduplicacion = nuevoServicio(10);
        darDeAltaElServicio();
        noHayIncidentesAbiertos();
        alertaGuardadaConId();
        incidenteGuardadoConId();

        servicioDeDeduplicacion.ingestar(alertaEntrante());

        ArgumentCaptor<Instant> desdeCaptor = ArgumentCaptor.forClass(Instant.class);
        verify(incidenteDeduplicacionRepository).buscarIncidentesAbiertosPorFingerprint(
            anyString(),
            anyLong(),
            any(),
            desdeCaptor.capture(),
            any(Pageable.class)
        );
        assertThat(desdeCaptor.getValue()).isEqualTo(RECIBIDA_EN.minus(Duration.ofMinutes(10)));
    }

    @Test
    @DisplayName("Una ventana no positiva es un error de configuracion y frena el arranque")
    void rechazaUnaVentanaNoPositiva() {
        assertThatThrownBy(() -> nuevoServicio(0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("ventana-minutos");
    }

    // ---------------------------------------------------------------------
    // Bordes
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("Si la alerta no trae recibidaEn, se toma el instante en que entro")
    void usaElInstanteActualCuandoLaAlertaNoTraeFecha() {
        darDeAltaElServicio();
        noHayIncidentesAbiertos();
        alertaGuardadaConId();
        incidenteGuardadoConId();

        Instant antes = Instant.now();
        AlertaEntranteDTO entrante = alertaEntrante();
        entrante.setRecibidaEn(null);

        servicioDeDeduplicacion.ingestar(entrante);

        ArgumentCaptor<Alerta> alertaCaptor = ArgumentCaptor.forClass(Alerta.class);
        verify(alertaRepository).saveAndFlush(alertaCaptor.capture());
        assertThat(alertaCaptor.getValue().getRecibidaEn()).isBetween(antes, Instant.now());
    }

    @Test
    @DisplayName("Una alerta de un servicio que no esta en el catalogo se rechaza")
    void rechazaAlertasDeUnServicioInexistente() {
        when(servicioRepository.findById(OTRO_SERVICIO_ID)).thenReturn(Optional.empty());

        AlertaEntranteDTO entrante = alertaEntrante();
        entrante.setServicioId(OTRO_SERVICIO_ID);

        assertThatThrownBy(() -> servicioDeDeduplicacion.ingestar(entrante))
            .isInstanceOf(ServicioInexistenteException.class)
            .hasMessageContaining(String.valueOf(OTRO_SERVICIO_ID));

        verify(alertaRepository, never()).saveAndFlush(any());
        verify(incidenteRepository, never()).saveAndFlush(any());
    }

    // ---------------------------------------------------------------------
    // Utilidades
    // ---------------------------------------------------------------------

    private AlertaEntranteDTO alertaEntrante() {
        AlertaEntranteDTO entrante = new AlertaEntranteDTO();
        entrante.setServicioId(SERVICIO_ID);
        entrante.setFingerprint(FINGERPRINT);
        entrante.setOrigen(OrigenAlerta.PROMETHEUS);
        entrante.setResumen("Disco al 95% en db-01");
        entrante.setPayload("{\"instance\":\"db-01\"}");
        entrante.setRecibidaEn(RECIBIDA_EN);
        return entrante;
    }

    private Servicio servicioConCriticidad(Criticidad criticidad) {
        return new Servicio().id(SERVICIO_ID).nombre("pagos-api").criticidad(criticidad);
    }

    private Incidente incidenteAbierto(long id) {
        return new Incidente().id(id).titulo("Disco al 95% en db-01").estado(EstadoIncidente.ABIERTO).detectadoEn(RECIBIDA_EN);
    }

    private void darDeAltaElServicio() {
        when(servicioRepository.findById(SERVICIO_ID)).thenReturn(Optional.of(servicio));
    }

    private void noHayIncidentesAbiertos() {
        when(
            incidenteDeduplicacionRepository.buscarIncidentesAbiertosPorFingerprint(
                anyString(),
                anyLong(),
                any(),
                any(Instant.class),
                any(Pageable.class)
            )
        ).thenReturn(List.of());
    }

    private void hayUnIncidenteAbierto(Incidente incidente) {
        when(
            incidenteDeduplicacionRepository.buscarIncidentesAbiertosPorFingerprint(
                anyString(),
                anyLong(),
                any(),
                any(Instant.class),
                any(Pageable.class)
            )
        ).thenReturn(List.of(incidente));
    }

    /** El alta de la alerta devuelve la entidad con id, como haria la base. */
    private void alertaGuardadaConId() {
        when(alertaRepository.saveAndFlush(any(Alerta.class))).thenAnswer(invocacion -> {
            Alerta alerta = invocacion.getArgument(0);
            if (alerta.getId() == null) {
                alerta.setId(100L);
            }
            return alerta;
        });
    }

    /** Idem para el incidente, solo en los casos en que la regla decide abrir uno. */
    private void incidenteGuardadoConId() {
        when(incidenteRepository.saveAndFlush(any(Incidente.class))).thenAnswer(invocacion -> {
            Incidente incidente = invocacion.getArgument(0);
            if (incidente.getId() == null) {
                incidente.setId(42L);
            }
            return incidente;
        });
    }
}
