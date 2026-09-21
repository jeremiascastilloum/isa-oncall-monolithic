package ar.edu.um.isa.oncall.service.dto;

import ar.edu.um.isa.oncall.domain.enumeration.Criticidad;
import ar.edu.um.isa.oncall.domain.enumeration.Entorno;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link ar.edu.um.isa.oncall.domain.Servicio} entity.
 */
@Schema(description = "Un servicio desplegado. Es la unidad sobre la que se abren incidentes.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ServicioDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 60)
    private String nombre;

    @Size(max = 500)
    private String descripcion;

    @NotNull
    private Criticidad criticidad;

    @NotNull
    private Entorno entorno;

    @Size(max = 255)
    private String repositorioUrl;

    @NotNull
    private Boolean activo;

    @NotNull
    private EquipoDTO equipo;

    private Set<IncidenteDTO> incidentes = new HashSet<>();

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

    public Criticidad getCriticidad() {
        return criticidad;
    }

    public void setCriticidad(Criticidad criticidad) {
        this.criticidad = criticidad;
    }

    public Entorno getEntorno() {
        return entorno;
    }

    public void setEntorno(Entorno entorno) {
        this.entorno = entorno;
    }

    public String getRepositorioUrl() {
        return repositorioUrl;
    }

    public void setRepositorioUrl(String repositorioUrl) {
        this.repositorioUrl = repositorioUrl;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public EquipoDTO getEquipo() {
        return equipo;
    }

    public void setEquipo(EquipoDTO equipo) {
        this.equipo = equipo;
    }

    public Set<IncidenteDTO> getIncidentes() {
        return incidentes;
    }

    public void setIncidentes(Set<IncidenteDTO> incidentes) {
        this.incidentes = incidentes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServicioDTO)) {
            return false;
        }

        ServicioDTO servicioDTO = (ServicioDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, servicioDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ServicioDTO{" +
            "id=" + getId() +
            ", nombre='" + getNombre() + "'" +
            ", descripcion='" + getDescripcion() + "'" +
            ", criticidad='" + getCriticidad() + "'" +
            ", entorno='" + getEntorno() + "'" +
            ", repositorioUrl='" + getRepositorioUrl() + "'" +
            ", activo='" + getActivo() + "'" +
            ", equipo=" + getEquipo() +
            ", incidentes=" + getIncidentes() +
            "}";
    }
}
