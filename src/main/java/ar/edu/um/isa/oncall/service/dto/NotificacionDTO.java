package ar.edu.um.isa.oncall.service.dto;

import ar.edu.um.isa.oncall.domain.enumeration.Canal;
import ar.edu.um.isa.oncall.domain.enumeration.EstadoNotificacion;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.edu.um.isa.oncall.domain.Notificacion} entity.
 */
@Schema(description = "Intento de avisarle a una persona por un canal.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NotificacionDTO implements Serializable {

    private Long id;

    @NotNull
    private Canal canal;

    @NotNull
    @Size(max = 120)
    private String destino;

    @NotNull
    private EstadoNotificacion estado;

    private Instant enviadaEn;

    @NotNull
    @Min(value = 0)
    @Max(value = 10)
    private Integer intentos;

    @Size(max = 255)
    private String errorMensaje;

    @NotNull
    private IncidenteDTO incidente;

    @NotNull
    private UserDTO destinatario;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Canal getCanal() {
        return canal;
    }

    public void setCanal(Canal canal) {
        this.canal = canal;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public EstadoNotificacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoNotificacion estado) {
        this.estado = estado;
    }

    public Instant getEnviadaEn() {
        return enviadaEn;
    }

    public void setEnviadaEn(Instant enviadaEn) {
        this.enviadaEn = enviadaEn;
    }

    public Integer getIntentos() {
        return intentos;
    }

    public void setIntentos(Integer intentos) {
        this.intentos = intentos;
    }

    public String getErrorMensaje() {
        return errorMensaje;
    }

    public void setErrorMensaje(String errorMensaje) {
        this.errorMensaje = errorMensaje;
    }

    public IncidenteDTO getIncidente() {
        return incidente;
    }

    public void setIncidente(IncidenteDTO incidente) {
        this.incidente = incidente;
    }

    public UserDTO getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(UserDTO destinatario) {
        this.destinatario = destinatario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NotificacionDTO)) {
            return false;
        }

        NotificacionDTO notificacionDTO = (NotificacionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, notificacionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NotificacionDTO{" +
            "id=" + getId() +
            ", canal='" + getCanal() + "'" +
            ", destino='" + getDestino() + "'" +
            ", estado='" + getEstado() + "'" +
            ", enviadaEn='" + getEnviadaEn() + "'" +
            ", intentos=" + getIntentos() +
            ", errorMensaje='" + getErrorMensaje() + "'" +
            ", incidente=" + getIncidente() +
            ", destinatario=" + getDestinatario() +
            "}";
    }
}
