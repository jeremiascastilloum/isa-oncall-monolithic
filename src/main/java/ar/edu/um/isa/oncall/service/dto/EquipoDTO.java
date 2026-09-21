package ar.edu.um.isa.oncall.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link ar.edu.um.isa.oncall.domain.Equipo} entity.
 */
@Schema(description = "Equipo responsable de uno o mas servicios en produccion.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EquipoDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 50)
    private String nombre;

    @NotNull
    @Size(max = 120)
    private String emailContacto;

    @Size(max = 60)
    private String canalChat;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmailContacto() {
        return emailContacto;
    }

    public void setEmailContacto(String emailContacto) {
        this.emailContacto = emailContacto;
    }

    public String getCanalChat() {
        return canalChat;
    }

    public void setCanalChat(String canalChat) {
        this.canalChat = canalChat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EquipoDTO)) {
            return false;
        }

        EquipoDTO equipoDTO = (EquipoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, equipoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EquipoDTO{" +
            "id=" + getId() +
            ", nombre='" + getNombre() + "'" +
            ", emailContacto='" + getEmailContacto() + "'" +
            ", canalChat='" + getCanalChat() + "'" +
            "}";
    }
}
