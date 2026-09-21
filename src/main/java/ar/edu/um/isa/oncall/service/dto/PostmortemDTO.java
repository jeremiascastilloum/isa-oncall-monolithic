package ar.edu.um.isa.oncall.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.edu.um.isa.oncall.domain.Postmortem} entity.
 */
@Schema(description = "Analisis posterior al incidente. Sin culpables, con causas.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PostmortemDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 140)
    private String titulo;

    @NotNull
    @Size(max = 500)
    private String resumen;

    @NotNull
    @Size(max = 2000)
    private String causaRaiz;

    @Size(max = 4000)
    private String lineaDeTiempo;

    @Size(max = 2000)
    private String leccionesAprendidas;

    @NotNull
    private Boolean publicado;

    private Instant publicadoEn;

    @NotNull
    private IncidenteDTO incidente;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getResumen() {
        return resumen;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public String getCausaRaiz() {
        return causaRaiz;
    }

    public void setCausaRaiz(String causaRaiz) {
        this.causaRaiz = causaRaiz;
    }

    public String getLineaDeTiempo() {
        return lineaDeTiempo;
    }

    public void setLineaDeTiempo(String lineaDeTiempo) {
        this.lineaDeTiempo = lineaDeTiempo;
    }

    public String getLeccionesAprendidas() {
        return leccionesAprendidas;
    }

    public void setLeccionesAprendidas(String leccionesAprendidas) {
        this.leccionesAprendidas = leccionesAprendidas;
    }

    public Boolean getPublicado() {
        return publicado;
    }

    public void setPublicado(Boolean publicado) {
        this.publicado = publicado;
    }

    public Instant getPublicadoEn() {
        return publicadoEn;
    }

    public void setPublicadoEn(Instant publicadoEn) {
        this.publicadoEn = publicadoEn;
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
        if (!(o instanceof PostmortemDTO)) {
            return false;
        }

        PostmortemDTO postmortemDTO = (PostmortemDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, postmortemDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PostmortemDTO{" +
            "id=" + getId() +
            ", titulo='" + getTitulo() + "'" +
            ", resumen='" + getResumen() + "'" +
            ", causaRaiz='" + getCausaRaiz() + "'" +
            ", lineaDeTiempo='" + getLineaDeTiempo() + "'" +
            ", leccionesAprendidas='" + getLeccionesAprendidas() + "'" +
            ", publicado='" + getPublicado() + "'" +
            ", publicadoEn='" + getPublicadoEn() + "'" +
            ", incidente=" + getIncidente() +
            "}";
    }
}
