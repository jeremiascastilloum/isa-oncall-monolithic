package ar.edu.um.isa.oncall.service.dto;

import ar.edu.um.isa.oncall.domain.enumeration.Severidad;
import ar.edu.um.isa.oncall.domain.enumeration.TipoObjetivo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link ar.edu.um.isa.oncall.domain.ObjetivoDeServicio} entity.
 */
@Schema(description = "Objetivo de nivel de servicio (SLO) que despues usamos para saber\nsi respondimos a tiempo.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ObjetivoDeServicioDTO implements Serializable {

    private Long id;

    @NotNull
    private TipoObjetivo tipo;

    @NotNull
    private Severidad severidadAplicable;

    @NotNull
    @Min(value = 1)
    @Max(value = 10080)
    private Integer minutosObjetivo;

    @Size(max = 255)
    private String descripcion;

    @NotNull
    private ServicioDTO servicio;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoObjetivo getTipo() {
        return tipo;
    }

    public void setTipo(TipoObjetivo tipo) {
        this.tipo = tipo;
    }

    public Severidad getSeveridadAplicable() {
        return severidadAplicable;
    }

    public void setSeveridadAplicable(Severidad severidadAplicable) {
        this.severidadAplicable = severidadAplicable;
    }

    public Integer getMinutosObjetivo() {
        return minutosObjetivo;
    }

    public void setMinutosObjetivo(Integer minutosObjetivo) {
        this.minutosObjetivo = minutosObjetivo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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
        if (!(o instanceof ObjetivoDeServicioDTO)) {
            return false;
        }

        ObjetivoDeServicioDTO objetivoDeServicioDTO = (ObjetivoDeServicioDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, objetivoDeServicioDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ObjetivoDeServicioDTO{" +
            "id=" + getId() +
            ", tipo='" + getTipo() + "'" +
            ", severidadAplicable='" + getSeveridadAplicable() + "'" +
            ", minutosObjetivo=" + getMinutosObjetivo() +
            ", descripcion='" + getDescripcion() + "'" +
            ", servicio=" + getServicio() +
            "}";
    }
}
