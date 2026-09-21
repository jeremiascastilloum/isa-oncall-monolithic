package ar.edu.um.isa.oncall.domain;

import ar.edu.um.isa.oncall.domain.enumeration.Severidad;
import ar.edu.um.isa.oncall.domain.enumeration.TipoObjetivo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Objetivo de nivel de servicio (SLO) que despues usamos para saber
 * si respondimos a tiempo.
 */
@Entity
@Table(name = "objetivo_de_servicio")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ObjetivoDeServicio implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoObjetivo tipo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "severidad_aplicable", nullable = false)
    private Severidad severidadAplicable;

    @NotNull
    @Min(value = 1)
    @Max(value = 10080)
    @Column(name = "minutos_objetivo", nullable = false)
    private Integer minutosObjetivo;

    @Size(max = 255)
    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "equipo", "objetivos", "alertas", "politicas", "incidentes" }, allowSetters = true)
    private Servicio servicio;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ObjetivoDeServicio id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoObjetivo getTipo() {
        return this.tipo;
    }

    public ObjetivoDeServicio tipo(TipoObjetivo tipo) {
        this.setTipo(tipo);
        return this;
    }

    public void setTipo(TipoObjetivo tipo) {
        this.tipo = tipo;
    }

    public Severidad getSeveridadAplicable() {
        return this.severidadAplicable;
    }

    public ObjetivoDeServicio severidadAplicable(Severidad severidadAplicable) {
        this.setSeveridadAplicable(severidadAplicable);
        return this;
    }

    public void setSeveridadAplicable(Severidad severidadAplicable) {
        this.severidadAplicable = severidadAplicable;
    }

    public Integer getMinutosObjetivo() {
        return this.minutosObjetivo;
    }

    public ObjetivoDeServicio minutosObjetivo(Integer minutosObjetivo) {
        this.setMinutosObjetivo(minutosObjetivo);
        return this;
    }

    public void setMinutosObjetivo(Integer minutosObjetivo) {
        this.minutosObjetivo = minutosObjetivo;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public ObjetivoDeServicio descripcion(String descripcion) {
        this.setDescripcion(descripcion);
        return this;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Servicio getServicio() {
        return this.servicio;
    }

    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }

    public ObjetivoDeServicio servicio(Servicio servicio) {
        this.setServicio(servicio);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ObjetivoDeServicio)) {
            return false;
        }
        return getId() != null && getId().equals(((ObjetivoDeServicio) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ObjetivoDeServicio{" +
            "id=" + getId() +
            ", tipo='" + getTipo() + "'" +
            ", severidadAplicable='" + getSeveridadAplicable() + "'" +
            ", minutosObjetivo=" + getMinutosObjetivo() +
            ", descripcion='" + getDescripcion() + "'" +
            "}";
    }
}
