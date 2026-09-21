package ar.edu.um.isa.oncall.service.dto;

import ar.edu.um.isa.oncall.domain.enumeration.Canal;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link ar.edu.um.isa.oncall.domain.PasoEscalamiento} entity.
 */
@Schema(description = "Un escalon de la politica.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PasoEscalamientoDTO implements Serializable {

    private Long id;

    @NotNull
    @Min(value = 1)
    @Max(value = 10)
    private Integer orden;

    @NotNull
    @Min(value = 0)
    @Max(value = 120)
    private Integer esperaMinutos;

    @NotNull
    private Canal canal;

    @NotNull
    private PoliticaEscalamientoDTO politica;

    private RotacionDTO rotacion;

    private UserDTO destinatarioDirecto;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public Integer getEsperaMinutos() {
        return esperaMinutos;
    }

    public void setEsperaMinutos(Integer esperaMinutos) {
        this.esperaMinutos = esperaMinutos;
    }

    public Canal getCanal() {
        return canal;
    }

    public void setCanal(Canal canal) {
        this.canal = canal;
    }

    public PoliticaEscalamientoDTO getPolitica() {
        return politica;
    }

    public void setPolitica(PoliticaEscalamientoDTO politica) {
        this.politica = politica;
    }

    public RotacionDTO getRotacion() {
        return rotacion;
    }

    public void setRotacion(RotacionDTO rotacion) {
        this.rotacion = rotacion;
    }

    public UserDTO getDestinatarioDirecto() {
        return destinatarioDirecto;
    }

    public void setDestinatarioDirecto(UserDTO destinatarioDirecto) {
        this.destinatarioDirecto = destinatarioDirecto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PasoEscalamientoDTO)) {
            return false;
        }

        PasoEscalamientoDTO pasoEscalamientoDTO = (PasoEscalamientoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, pasoEscalamientoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PasoEscalamientoDTO{" +
            "id=" + getId() +
            ", orden=" + getOrden() +
            ", esperaMinutos=" + getEsperaMinutos() +
            ", canal='" + getCanal() + "'" +
            ", politica=" + getPolitica() +
            ", rotacion=" + getRotacion() +
            ", destinatarioDirecto=" + getDestinatarioDirecto() +
            "}";
    }
}
