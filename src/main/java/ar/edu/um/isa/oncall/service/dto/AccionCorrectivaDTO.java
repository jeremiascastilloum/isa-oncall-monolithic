package ar.edu.um.isa.oncall.service.dto;

import ar.edu.um.isa.oncall.domain.enumeration.EstadoAccion;
import ar.edu.um.isa.oncall.domain.enumeration.Prioridad;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link ar.edu.um.isa.oncall.domain.AccionCorrectiva} entity.
 */
@Schema(description = "Compromiso concreto que sale del postmortem.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AccionCorrectivaDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 300)
    private String descripcion;

    @NotNull
    private Prioridad prioridad;

    @NotNull
    private EstadoAccion estado;

    private LocalDate fechaLimite;

    @Size(max = 255)
    private String ticketUrl;

    @NotNull
    private PostmortemDTO postmortem;

    private UserDTO responsable;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Prioridad prioridad) {
        this.prioridad = prioridad;
    }

    public EstadoAccion getEstado() {
        return estado;
    }

    public void setEstado(EstadoAccion estado) {
        this.estado = estado;
    }

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(LocalDate fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public String getTicketUrl() {
        return ticketUrl;
    }

    public void setTicketUrl(String ticketUrl) {
        this.ticketUrl = ticketUrl;
    }

    public PostmortemDTO getPostmortem() {
        return postmortem;
    }

    public void setPostmortem(PostmortemDTO postmortem) {
        this.postmortem = postmortem;
    }

    public UserDTO getResponsable() {
        return responsable;
    }

    public void setResponsable(UserDTO responsable) {
        this.responsable = responsable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AccionCorrectivaDTO)) {
            return false;
        }

        AccionCorrectivaDTO accionCorrectivaDTO = (AccionCorrectivaDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, accionCorrectivaDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AccionCorrectivaDTO{" +
            "id=" + getId() +
            ", descripcion='" + getDescripcion() + "'" +
            ", prioridad='" + getPrioridad() + "'" +
            ", estado='" + getEstado() + "'" +
            ", fechaLimite='" + getFechaLimite() + "'" +
            ", ticketUrl='" + getTicketUrl() + "'" +
            ", postmortem=" + getPostmortem() +
            ", responsable=" + getResponsable() +
            "}";
    }
}
