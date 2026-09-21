package ar.edu.um.isa.oncall.service.dto;

import ar.edu.um.isa.oncall.domain.enumeration.EstadoIncidente;
import ar.edu.um.isa.oncall.domain.enumeration.Severidad;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link ar.edu.um.isa.oncall.domain.Incidente} entity.
 */
@Schema(
    description = "Incidente: la interrupcion real que estamos gestionando.\nLas marcas de tiempo son la materia prima del MTTA y el MTTR."
)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class IncidenteDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 140)
    private String titulo;

    @Size(max = 2000)
    private String descripcion;

    @NotNull
    private Severidad severidad;

    @NotNull
    private EstadoIncidente estado;

    @NotNull
    private Instant detectadoEn;

    private Instant reconocidoEn;

    private Instant mitigadoEn;

    private Instant resueltoEn;

    @Min(value = 0)
    private Integer usuariosAfectados;

    private Boolean cumplioObjetivo;

    private UserDTO comandante;

    private Set<ServicioDTO> servicios = new HashSet<>();

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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Severidad getSeveridad() {
        return severidad;
    }

    public void setSeveridad(Severidad severidad) {
        this.severidad = severidad;
    }

    public EstadoIncidente getEstado() {
        return estado;
    }

    public void setEstado(EstadoIncidente estado) {
        this.estado = estado;
    }

    public Instant getDetectadoEn() {
        return detectadoEn;
    }

    public void setDetectadoEn(Instant detectadoEn) {
        this.detectadoEn = detectadoEn;
    }

    public Instant getReconocidoEn() {
        return reconocidoEn;
    }

    public void setReconocidoEn(Instant reconocidoEn) {
        this.reconocidoEn = reconocidoEn;
    }

    public Instant getMitigadoEn() {
        return mitigadoEn;
    }

    public void setMitigadoEn(Instant mitigadoEn) {
        this.mitigadoEn = mitigadoEn;
    }

    public Instant getResueltoEn() {
        return resueltoEn;
    }

    public void setResueltoEn(Instant resueltoEn) {
        this.resueltoEn = resueltoEn;
    }

    public Integer getUsuariosAfectados() {
        return usuariosAfectados;
    }

    public void setUsuariosAfectados(Integer usuariosAfectados) {
        this.usuariosAfectados = usuariosAfectados;
    }

    public Boolean getCumplioObjetivo() {
        return cumplioObjetivo;
    }

    public void setCumplioObjetivo(Boolean cumplioObjetivo) {
        this.cumplioObjetivo = cumplioObjetivo;
    }

    public UserDTO getComandante() {
        return comandante;
    }

    public void setComandante(UserDTO comandante) {
        this.comandante = comandante;
    }

    public Set<ServicioDTO> getServicios() {
        return servicios;
    }

    public void setServicios(Set<ServicioDTO> servicios) {
        this.servicios = servicios;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IncidenteDTO)) {
            return false;
        }

        IncidenteDTO incidenteDTO = (IncidenteDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, incidenteDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "IncidenteDTO{" +
            "id=" + getId() +
            ", titulo='" + getTitulo() + "'" +
            ", descripcion='" + getDescripcion() + "'" +
            ", severidad='" + getSeveridad() + "'" +
            ", estado='" + getEstado() + "'" +
            ", detectadoEn='" + getDetectadoEn() + "'" +
            ", reconocidoEn='" + getReconocidoEn() + "'" +
            ", mitigadoEn='" + getMitigadoEn() + "'" +
            ", resueltoEn='" + getResueltoEn() + "'" +
            ", usuariosAfectados=" + getUsuariosAfectados() +
            ", cumplioObjetivo='" + getCumplioObjetivo() + "'" +
            ", comandante=" + getComandante() +
            ", servicios=" + getServicios() +
            "}";
    }
}
