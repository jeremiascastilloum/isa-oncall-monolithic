package ar.edu.um.isa.oncall.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link ar.edu.um.isa.oncall.domain.PoliticaEscalamiento} entity.
 */
@Schema(
    description = "Politica de escalamiento de un servicio: a quien se avisa,\nen que orden y cuanto se espera antes de subir un escalon."
)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PoliticaEscalamientoDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 60)
    private String nombre;

    @Size(max = 255)
    private String descripcion;

    @NotNull
    @Min(value = 0)
    @Max(value = 5)
    private Integer repetirVeces;

    @NotNull
    private ServicioDTO servicio;

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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getRepetirVeces() {
        return repetirVeces;
    }

    public void setRepetirVeces(Integer repetirVeces) {
        this.repetirVeces = repetirVeces;
    }

    public ServicioDTO getServicio() {
        return servicio;
    }

    public void setServicio(ServicioDTO servicio) {
        this.servicio = servicio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PoliticaEscalamientoDTO)) {
            return false;
        }

        PoliticaEscalamientoDTO politicaEscalamientoDTO = (PoliticaEscalamientoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, politicaEscalamientoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PoliticaEscalamientoDTO{" +
            "id=" + getId() +
            ", nombre='" + getNombre() + "'" +
            ", descripcion='" + getDescripcion() + "'" +
            ", repetirVeces=" + getRepetirVeces() +
            ", servicio=" + getServicio() +
            "}";
    }
}
