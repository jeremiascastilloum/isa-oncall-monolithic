package ar.edu.um.isa.oncall.service.dto;

import ar.edu.um.isa.oncall.domain.enumeration.OrigenAlerta;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * La senal cruda tal como la manda la herramienta de monitoreo.
 *
 * No es {@link AlertaDTO}: el que dispara el webhook no sabe si la alerta ya fue procesada
 * ni a que incidente pertenece. Esas dos cosas las decide la regla de deduplicacion.
 */
@Schema(description = "Alerta cruda que entra por el webhook de monitoreo, antes de deduplicar.")
public class AlertaEntranteDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Schema(description = "Servicio sobre el que se disparo la alerta.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long servicioId;

    @NotNull
    @Size(max = 120)
    @Schema(description = "Clave de deduplicacion que trae la herramienta de monitoreo.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String fingerprint;

    @NotNull
    private OrigenAlerta origen;

    @NotNull
    @Size(max = 200)
    private String resumen;

    @Size(max = 4000)
    private String payload;

    @Schema(description = "Momento en que la herramienta emitio la senal. Si no viene, se toma el instante actual.")
    private Instant recibidaEn;

    public Long getServicioId() {
        return servicioId;
    }

    public void setServicioId(Long servicioId) {
        this.servicioId = servicioId;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public OrigenAlerta getOrigen() {
        return origen;
    }

    public void setOrigen(OrigenAlerta origen) {
        this.origen = origen;
    }

    public String getResumen() {
        return resumen;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Instant getRecibidaEn() {
        return recibidaEn;
    }

    public void setRecibidaEn(Instant recibidaEn) {
        this.recibidaEn = recibidaEn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AlertaEntranteDTO otra)) {
            return false;
        }
        return (
            Objects.equals(servicioId, otra.servicioId) &&
            Objects.equals(fingerprint, otra.fingerprint) &&
            origen == otra.origen &&
            Objects.equals(resumen, otra.resumen) &&
            Objects.equals(payload, otra.payload) &&
            Objects.equals(recibidaEn, otra.recibidaEn)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(servicioId, fingerprint, origen, resumen, payload, recibidaEn);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AlertaEntranteDTO{" +
            "servicioId=" + getServicioId() +
            ", fingerprint='" + getFingerprint() + "'" +
            ", origen='" + getOrigen() + "'" +
            ", resumen='" + getResumen() + "'" +
            ", payload='" + getPayload() + "'" +
            ", recibidaEn='" + getRecibidaEn() + "'" +
            "}";
    }
}
