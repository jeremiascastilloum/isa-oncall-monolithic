package ar.edu.um.isa.oncall.service;

import ar.edu.um.isa.oncall.domain.Alerta;
import ar.edu.um.isa.oncall.domain.EventoDeIncidente;
import ar.edu.um.isa.oncall.domain.Incidente;
import ar.edu.um.isa.oncall.domain.Servicio;
import ar.edu.um.isa.oncall.domain.enumeration.Criticidad;
import ar.edu.um.isa.oncall.domain.enumeration.EstadoIncidente;
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
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Regla de negocio 1 · Deduplicacion de alertas por fingerprint.
 *
 * <p>Una alerta no es un incidente. Si el mismo disco se llena tres veces en diez minutos
 * llegan tres alertas con el mismo fingerprint, y sin embargo la interrupcion es una sola.
 * Esta clase es la que decide, para cada alerta que entra, si abre un incidente nuevo o si
 * la pega a uno que ya esta abierto.</p>
 *
 * <p>El criterio es una conjuncion de tres condiciones. La alerta se deduplica si existe un
 * incidente que:</p>
 * <ol>
 *   <li>ya recibio una alerta con <strong>el mismo fingerprint</strong>,</li>
 *   <li>sobre <strong>el mismo servicio</strong> — el fingerprint solo no alcanza, porque dos
 *       servicios pueden emitir la misma senal y son dos interrupciones distintas,</li>
 *   <li>y sigue <strong>abierto</strong>, es decir su estado no es {@code RESUELTO} ni
 *       {@code CERRADO}, y fue detectado dentro de la ventana de deduplicacion.</li>
 * </ol>
 *
 * <p>Si no hay ninguno, la alerta abre un incidente nuevo. En los dos casos la alerta queda
 * asociada a un incidente, se marca {@code procesada} y deja una entrada en la linea de tiempo:
 * lo que no se escribe en {@code EventoDeIncidente} no se puede reconstruir en el postmortem.</p>
 *
 * <p>La ventana existe para que una alerta que reaparece una semana despues no se pegue a un
 * incidente viejo que quedo abierto por olvido. Se configura con
 * {@code oncall.deduplicacion.ventana-minutos}.</p>
 *
 * <p>Limite conocido: dos alertas con el mismo fingerprint procesadas en paralelo pueden abrir
 * dos incidentes, porque la deteccion y el alta no son atomicas entre transacciones. Resolverlo
 * pide un indice unico o un lock por fingerprint, y queda fuera del alcance de esta clase.</p>
 */
@Service
@Transactional
public class DeduplicacionDeAlertasService {

    private static final Logger LOG = LoggerFactory.getLogger(DeduplicacionDeAlertasService.class);

    /** Ventana usada cuando la propiedad no esta configurada: dos horas. */
    public static final int VENTANA_POR_DEFECTO_MINUTOS = 120;

    /** Estados que dan el incidente por terminado: una alerta nueva ya no se le pega. */
    private static final Set<EstadoIncidente> ESTADOS_CERRADOS = EnumSet.of(EstadoIncidente.RESUELTO, EstadoIncidente.CERRADO);

    private static final int MAX_TITULO = 140;
    private static final int MAX_DESCRIPCION = 2000;
    private static final int MAX_DETALLE = 500;

    private final AlertaRepository alertaRepository;
    private final IncidenteRepository incidenteRepository;
    private final IncidenteDeduplicacionRepository incidenteDeduplicacionRepository;
    private final EventoDeIncidenteRepository eventoDeIncidenteRepository;
    private final ServicioRepository servicioRepository;

    private final Duration ventana;

    public DeduplicacionDeAlertasService(
        AlertaRepository alertaRepository,
        IncidenteRepository incidenteRepository,
        IncidenteDeduplicacionRepository incidenteDeduplicacionRepository,
        EventoDeIncidenteRepository eventoDeIncidenteRepository,
        ServicioRepository servicioRepository,
        @Value("${oncall.deduplicacion.ventana-minutos:" + VENTANA_POR_DEFECTO_MINUTOS + "}") int ventanaMinutos
    ) {
        if (ventanaMinutos < 1) {
            throw new IllegalArgumentException("oncall.deduplicacion.ventana-minutos debe ser al menos 1, y es " + ventanaMinutos);
        }
        this.alertaRepository = alertaRepository;
        this.incidenteRepository = incidenteRepository;
        this.incidenteDeduplicacionRepository = incidenteDeduplicacionRepository;
        this.eventoDeIncidenteRepository = eventoDeIncidenteRepository;
        this.servicioRepository = servicioRepository;
        this.ventana = Duration.ofMinutes(ventanaMinutos);
    }

    /**
     * Procesa una alerta cruda: la persiste y decide si abre un incidente o la deduplica.
     *
     * @param entrante la senal tal como la manda la herramienta de monitoreo.
     * @return que se hizo con la alerta y a que incidente quedo asociada.
     * @throws ServicioInexistenteException si el servicio referenciado no esta en el catalogo.
     */
    public ResultadoDeduplicacionDTO ingestar(AlertaEntranteDTO entrante) {
        LOG.debug("Ingestando alerta con fingerprint {} para el servicio {}", entrante.getFingerprint(), entrante.getServicioId());

        Servicio servicio = servicioRepository
            .findById(entrante.getServicioId())
            .orElseThrow(() -> new ServicioInexistenteException(entrante.getServicioId()));

        Instant recibidaEn = entrante.getRecibidaEn() != null ? entrante.getRecibidaEn() : Instant.now();

        Alerta alerta = new Alerta()
            .fingerprint(entrante.getFingerprint())
            .origen(entrante.getOrigen())
            .resumen(entrante.getResumen())
            .payload(entrante.getPayload())
            .recibidaEn(recibidaEn)
            .procesada(false)
            .servicio(servicio);

        return buscarIncidenteAbierto(entrante.getFingerprint(), servicio.getId(), recibidaEn)
            .map(incidente -> deduplicar(alerta, incidente))
            .orElseGet(() -> abrirIncidente(alerta, servicio));
    }

    /**
     * El incidente abierto mas reciente que ya vio este fingerprint sobre este servicio.
     *
     * Es el paso de decision de la regla, aislado para poder leerlo y probarlo solo.
     */
    public Optional<Incidente> buscarIncidenteAbierto(String fingerprint, Long servicioId, Instant recibidaEn) {
        Instant detectadoDesde = recibidaEn.minus(ventana);
        List<Incidente> candidatos = incidenteDeduplicacionRepository.buscarIncidentesAbiertosPorFingerprint(
            fingerprint,
            servicioId,
            ESTADOS_CERRADOS,
            detectadoDesde,
            PageRequest.of(0, 1)
        );
        return candidatos.isEmpty() ? Optional.empty() : Optional.of(candidatos.get(0));
    }

    /**
     * Camino A: ya hay un incidente abierto. La alerta se absorbe y no se abre nada nuevo.
     */
    private ResultadoDeduplicacionDTO deduplicar(Alerta alerta, Incidente incidente) {
        alerta.setIncidente(incidente);
        alerta.setProcesada(true);
        // se fuerza el flush: el conteo de ocurrencias de abajo tiene que ver esta alerta
        Alerta guardada = alertaRepository.saveAndFlush(alerta);

        long ocurrencias = incidenteDeduplicacionRepository.contarAlertasConFingerprint(incidente.getId(), guardada.getFingerprint());

        String motivo =
            "Alerta duplicada: el fingerprint '" +
            guardada.getFingerprint() +
            "' ya tenia un incidente abierto sobre el servicio '" +
            guardada.getServicio().getNombre() +
            "'. Es la ocurrencia numero " +
            ocurrencias +
            ". No se abrio un incidente nuevo.";

        registrarEnLineaDeTiempo(incidente, TipoEvento.NOTA, motivo, guardada.getRecibidaEn());

        LOG.debug("Alerta {} deduplicada contra el incidente {}", guardada.getId(), incidente.getId());

        return new ResultadoDeduplicacionDTO(
            guardada.getId(),
            incidente.getId(),
            incidente.getTitulo(),
            ResultadoDeduplicacionDTO.Accion.ALERTA_DEDUPLICADA,
            ocurrencias,
            motivo
        );
    }

    /**
     * Camino B: no hay nada abierto para ese fingerprint. Esta alerta es una interrupcion nueva.
     */
    private ResultadoDeduplicacionDTO abrirIncidente(Alerta alerta, Servicio servicio) {
        Incidente incidente = new Incidente()
            .titulo(recortar(alerta.getResumen(), MAX_TITULO))
            .descripcion(
                recortar(
                    "Abierto automaticamente por la alerta '" +
                        alerta.getResumen() +
                        "' (fingerprint '" +
                        alerta.getFingerprint() +
                        "') recibida desde " +
                        alerta.getOrigen() +
                        ".",
                    MAX_DESCRIPCION
                )
            )
            .severidad(severidadSegun(servicio.getCriticidad()))
            .estado(EstadoIncidente.ABIERTO)
            .detectadoEn(alerta.getRecibidaEn());
        incidente.addServicio(servicio);
        incidente = incidenteRepository.saveAndFlush(incidente);

        alerta.setIncidente(incidente);
        alerta.setProcesada(true);
        Alerta guardada = alertaRepository.saveAndFlush(alerta);

        String motivo =
            "Incidente abierto: no habia ninguno abierto para el fingerprint '" +
            guardada.getFingerprint() +
            "' sobre el servicio '" +
            servicio.getNombre() +
            "'. Severidad " +
            incidente.getSeveridad() +
            " derivada de la criticidad " +
            servicio.getCriticidad() +
            " del servicio.";

        registrarEnLineaDeTiempo(incidente, TipoEvento.CREACION, motivo, guardada.getRecibidaEn());

        LOG.debug("Alerta {} abrio el incidente {}", guardada.getId(), incidente.getId());

        return new ResultadoDeduplicacionDTO(
            guardada.getId(),
            incidente.getId(),
            incidente.getTitulo(),
            ResultadoDeduplicacionDTO.Accion.INCIDENTE_ABIERTO,
            1L,
            motivo
        );
    }

    /**
     * La severidad del incidente sale de la criticidad del servicio afectado.
     *
     * Es una traduccion deliberadamente simple: lo importante es que la decida la aplicacion
     * y no la persona que carga el incidente a mano.
     */
    private Severidad severidadSegun(Criticidad criticidad) {
        return switch (criticidad) {
            case TIER1 -> Severidad.SEV1;
            case TIER2 -> Severidad.SEV2;
            case TIER3 -> Severidad.SEV3;
        };
    }

    /**
     * Cada decision automatica deja rastro. Es lo que despues permite escribir el postmortem.
     */
    private void registrarEnLineaDeTiempo(Incidente incidente, TipoEvento tipo, String detalle, Instant ocurridoEn) {
        EventoDeIncidente evento = new EventoDeIncidente()
            .tipo(tipo)
            .detalle(recortar(detalle, MAX_DETALLE))
            .ocurridoEn(ocurridoEn)
            .automatico(true)
            .incidente(incidente);
        eventoDeIncidenteRepository.save(evento);
    }

    private String recortar(String texto, int largoMaximo) {
        if (texto == null || texto.length() <= largoMaximo) {
            return texto;
        }
        return texto.substring(0, largoMaximo - 3) + "...";
    }
}
