package ar.edu.um.isa.oncall.service.dto;

import ar.edu.um.isa.oncall.domain.enumeration.TipoRotacion;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link ar.edu.um.isa.oncall.domain.Rotacion} entity.
 */
@Schema(description = "Rotacion de guardia de un equipo. Define el patron; los turnos\nconcretos viven en TurnoDeGuardia.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RotacionDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 60)
    private String nombre;

    @NotNull
    private TipoRotacion tipo;

    @NotNull
    @Size(max = 50)
    private String zonaHoraria;

    @NotNull
    private Boolean activa;

    @NotNull
    private EquipoDTO equipo;

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

    public TipoRotacion getTipo() {
        return tipo;
    }

    public void setTipo(TipoRotacion tipo) {
        this.tipo = tipo;
    }

    public String getZonaHoraria() {
        return zonaHoraria;
    }

    public void setZonaHoraria(String zonaHoraria) {
        this.zonaHoraria = zonaHoraria;
    }

    public Boolean getActiva() {
        return activa;
    }

    public void setActiva(Boolean activa) {
        this.activa = activa;
    }

    public EquipoDTO getEquipo() {
        return equipo;
    }

    public void setEquipo(EquipoDTO equipo) {
        this.equipo = equipo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RotacionDTO)) {
            return false;
        }

        RotacionDTO rotacionDTO = (RotacionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, rotacionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RotacionDTO{" +
            "id=" + getId() +
            ", nombre='" + getNombre() + "'" +
            ", tipo='" + getTipo() + "'" +
            ", zonaHoraria='" + getZonaHoraria() + "'" +
            ", activa='" + getActiva() + "'" +
            ", equipo=" + getEquipo() +
            "}";
    }
}
