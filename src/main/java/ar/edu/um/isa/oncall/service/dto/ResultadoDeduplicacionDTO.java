package ar.edu.um.isa.oncall.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;

/**
 * Que decidio la regla de deduplicacion con la alerta que acaba de entrar.
 */
@Schema(description = "Resultado de aplicar la regla de deduplicacion sobre una alerta entrante.")
public class ResultadoDeduplicacionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Las dos unicas salidas posibles de la regla.
     */
    public enum Accion {
        /** No habia incidente abierto para ese fingerprint: se abrio uno. */
        INCIDENTE_ABIERTO,
        /** Ya habia un incidente abierto: la alerta se pego a ese y no se abrio otro. */
        ALERTA_DEDUPLICADA,
    }

    private Long alertaId;

    private Long incidenteId;

    private String incidenteTitulo;

    private Accion accion;

    @Schema(description = "Cuantas alertas con este fingerprint acumula ya el incidente, contando la que acaba de entrar.")
    private long ocurrencias;

    @Schema(description = "Por que la regla decidio lo que decidio. Es lo que queda en la linea de tiempo.")
    private String motivo;

    public ResultadoDeduplicacionDTO() {
        // requerido por Jackson
    }

    public ResultadoDeduplicacionDTO(
        Long alertaId,
        Long incidenteId,
        String incidenteTitulo,
        Accion accion,
        long ocurrencias,
        String motivo
    ) {
        this.alertaId = alertaId;
        this.incidenteId = incidenteId;
        this.incidenteTitulo = incidenteTitulo;
        this.accion = accion;
        this.ocurrencias = ocurrencias;
        this.motivo = motivo;
    }

    /**
     * @return {@code true} cuando la alerta se absorbio en un incidente que ya estaba abierto.
     */
    @Schema(description = "Atajo de lectura: true cuando la alerta no abrio un incidente nuevo.")
    public boolean isDuplicada() {
        return accion == Accion.ALERTA_DEDUPLICADA;
    }

    public Long getAlertaId() {
        return alertaId;
    }

    public void setAlertaId(Long alertaId) {
        this.alertaId = alertaId;
    }

    public Long getIncidenteId() {
        return incidenteId;
    }

    public void setIncidenteId(Long incidenteId) {
        this.incidenteId = incidenteId;
    }

    public String getIncidenteTitulo() {
        return incidenteTitulo;
    }

    public void setIncidenteTitulo(String incidenteTitulo) {
        this.incidenteTitulo = incidenteTitulo;
    }

    public Accion getAccion() {
        return accion;
    }

    public void setAccion(Accion accion) {
        this.accion = accion;
    }

    public long getOcurrencias() {
        return ocurrencias;
    }

    public void setOcurrencias(long ocurrencias) {
        this.ocurrencias = ocurrencias;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ResultadoDeduplicacionDTO otro)) {
            return false;
        }
        return (
            Objects.equals(alertaId, otro.alertaId) &&
            Objects.equals(incidenteId, otro.incidenteId) &&
            Objects.equals(incidenteTitulo, otro.incidenteTitulo) &&
            accion == otro.accion &&
            ocurrencias == otro.ocurrencias &&
            Objects.equals(motivo, otro.motivo)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(alertaId, incidenteId, incidenteTitulo, accion, ocurrencias, motivo);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ResultadoDeduplicacionDTO{" +
            "alertaId=" + getAlertaId() +
            ", incidenteId=" + getIncidenteId() +
            ", incidenteTitulo='" + getIncidenteTitulo() + "'" +
            ", accion='" + getAccion() + "'" +
            ", ocurrencias=" + getOcurrencias() +
            ", motivo='" + getMotivo() + "'" +
            "}";
    }
}
