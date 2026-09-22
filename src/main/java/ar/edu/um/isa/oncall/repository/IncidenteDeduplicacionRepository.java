package ar.edu.um.isa.oncall.repository;

import ar.edu.um.isa.oncall.domain.Incidente;
import ar.edu.um.isa.oncall.domain.enumeration.EstadoIncidente;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Consultas que necesita la regla de deduplicacion de alertas.
 *
 * Vive en una interfaz aparte de {@link IncidenteRepository} a proposito: ese archivo lo
 * escribe el generador y {@code jhipster jdl oncall.jh --force} lo pisa. Lo que escribimos
 * nosotros va en archivos que el generador no conoce.
 */
@Repository
public interface IncidenteDeduplicacionRepository extends org.springframework.data.repository.Repository<Incidente, Long> {
    /**
     * Incidentes todavia abiertos que ya recibieron una alerta con este fingerprint sobre
     * este mismo servicio, del mas reciente al mas viejo.
     *
     * El fingerprint solo no alcanza: dos servicios distintos pueden emitir la misma senal
     * ("disco al 90%") y son dos interrupciones distintas.
     *
     * @param fingerprint el fingerprint de la alerta entrante.
     * @param servicioId el servicio sobre el que llego la alerta.
     * @param estadosCerrados estados que dan el incidente por terminado.
     * @param detectadoDesde piso de la ventana de deduplicacion.
     * @param pageable usado para pedir un unico resultado.
     * @return los incidentes candidatos, el mas reciente primero.
     */
    @Query(
        """
        select distinct incidente from Incidente incidente
            join incidente.alertas alerta
        where alerta.fingerprint = :fingerprint
            and alerta.servicio.id = :servicioId
            and incidente.estado not in :estadosCerrados
            and incidente.detectadoEn >= :detectadoDesde
        order by incidente.detectadoEn desc
        """
    )
    List<Incidente> buscarIncidentesAbiertosPorFingerprint(
        @Param("fingerprint") String fingerprint,
        @Param("servicioId") Long servicioId,
        @Param("estadosCerrados") Collection<EstadoIncidente> estadosCerrados,
        @Param("detectadoDesde") Instant detectadoDesde,
        Pageable pageable
    );

    /**
     * Cuantas veces ya se repitio este fingerprint dentro del incidente. Sirve para dejar
     * escrito en la linea de tiempo "esta es la tercera vez".
     *
     * @param incidenteId el incidente al que quedaron pegadas las alertas.
     * @param fingerprint el fingerprint repetido.
     * @return la cantidad de alertas con ese fingerprint ya asociadas al incidente.
     */
    @Query("select count(alerta) from Alerta alerta where alerta.incidente.id = :incidenteId and alerta.fingerprint = :fingerprint")
    long contarAlertasConFingerprint(@Param("incidenteId") Long incidenteId, @Param("fingerprint") String fingerprint);
}
