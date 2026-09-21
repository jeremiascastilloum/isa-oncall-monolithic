package ar.edu.um.isa.oncall.service.dto;

import ar.edu.um.isa.oncall.domain.enumeration.OrigenAlerta;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.edu.um.isa.oncall.domain.Alerta} entity.
 */
@Schema(
    description = "Senal cruda que llega desde una herramienta de monitoreo.\nEl fingerprint es la clave de deduplicacion: varias alertas con el\nmismo fingerprint deberian colapsar en un unico incidente."
)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AlertaDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 120)
    private String fingerprint;

    @NotNull
    private OrigenAlerta origen;

    @NotNull
    @Size(max = 200)
    private String resumen;

    @Size(max = 4000)
    private String payload;

    @NotNull
    private Instant recibidaEn;

    @NotNull
    private Boolean procesada;

    @NotNull
    private ServicioDTO servicio;

    private IncidenteDTO incidente;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Boolean getProcesada() {
        return procesada;
    }

    public void setProcesada(Boolean procesada) {
        this.procesada = procesada;
    }

    public ServicioDTO getServicio() {
        return servicio;
    }

    public void setServicio(ServicioDTO servicio) {
        this.servicio = servicio;
    }

    public IncidenteDTO getIncidente() {
        return incidente;
    }

    public void setIncidente(IncidenteDTO incidente) {
        this.incidente = incidente;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AlertaDTO)) {
            return false;
        }

        AlertaDTO alertaDTO = (AlertaDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, alertaDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AlertaDTO{" +
            "id=" + getId() +
            ", fingerprint='" + getFingerprint() + "'" +
            ", origen='" + getOrigen() + "'" +
            ", resumen='" + getResumen() + "'" +
            ", payload='" + getPayload() + "'" +
            ", recibidaEn='" + getRecibidaEn() + "'" +
            ", procesada='" + getProcesada() + "'" +
            ", servicio=" + getServicio() +
            ", incidente=" + getIncidente() +
            "}";
    }
}
