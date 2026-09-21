package ar.edu.um.isa.oncall.service.dto;

import ar.edu.um.isa.oncall.domain.enumeration.TipoEvento;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.edu.um.isa.oncall.domain.EventoDeIncidente} entity.
 */
@Schema(description = "Linea de tiempo del incidente. Cada cosa que pasa deja una entrada.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EventoDeIncidenteDTO implements Serializable {

    private Long id;

    @NotNull
    private TipoEvento tipo;

    @NotNull
    @Size(max = 500)
    private String detalle;

    @NotNull
    private Instant ocurridoEn;

    @NotNull
    private Boolean automatico;

    @NotNull
    private IncidenteDTO incidente;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoEvento getTipo() {
        return tipo;
    }

    public void setTipo(TipoEvento tipo) {
        this.tipo = tipo;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public Instant getOcurridoEn() {
        return ocurridoEn;
    }

    public void setOcurridoEn(Instant ocurridoEn) {
        this.ocurridoEn = ocurridoEn;
    }

    public Boolean getAutomatico() {
        return automatico;
    }

    public void setAutomatico(Boolean automatico) {
        this.automatico = automatico;
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
        if (!(o instanceof EventoDeIncidenteDTO)) {
            return false;
        }

        EventoDeIncidenteDTO eventoDeIncidenteDTO = (EventoDeIncidenteDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, eventoDeIncidenteDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EventoDeIncidenteDTO{" +
            "id=" + getId() +
            ", tipo='" + getTipo() + "'" +
            ", detalle='" + getDetalle() + "'" +
            ", ocurridoEn='" + getOcurridoEn() + "'" +
            ", automatico='" + getAutomatico() + "'" +
            ", incidente=" + getIncidente() +
            "}";
    }
}
