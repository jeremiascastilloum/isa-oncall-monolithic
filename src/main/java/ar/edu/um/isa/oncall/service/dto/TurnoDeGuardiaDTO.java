package ar.edu.um.isa.oncall.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.edu.um.isa.oncall.domain.TurnoDeGuardia} entity.
 */
@Schema(description = "Ventana concreta de tiempo en la que una persona esta de guardia.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TurnoDeGuardiaDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant desde;

    @NotNull
    private Instant hasta;

    private Boolean esReemplazo;

    @Size(max = 255)
    private String nota;

    @NotNull
    private RotacionDTO rotacion;

    @NotNull
    private UserDTO responsable;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDesde() {
        return desde;
    }

    public void setDesde(Instant desde) {
        this.desde = desde;
    }

    public Instant getHasta() {
        return hasta;
    }

    public void setHasta(Instant hasta) {
        this.hasta = hasta;
    }

    public Boolean getEsReemplazo() {
        return esReemplazo;
    }

    public void setEsReemplazo(Boolean esReemplazo) {
        this.esReemplazo = esReemplazo;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public RotacionDTO getRotacion() {
        return rotacion;
    }

    public void setRotacion(RotacionDTO rotacion) {
        this.rotacion = rotacion;
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
        if (!(o instanceof TurnoDeGuardiaDTO)) {
            return false;
        }

        TurnoDeGuardiaDTO turnoDeGuardiaDTO = (TurnoDeGuardiaDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, turnoDeGuardiaDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TurnoDeGuardiaDTO{" +
            "id=" + getId() +
            ", desde='" + getDesde() + "'" +
            ", hasta='" + getHasta() + "'" +
            ", esReemplazo='" + getEsReemplazo() + "'" +
            ", nota='" + getNota() + "'" +
            ", rotacion=" + getRotacion() +
            ", responsable=" + getResponsable() +
            "}";
    }
}
