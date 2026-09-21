package ar.edu.um.isa.oncall.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Politica de escalamiento de un servicio: a quien se avisa,
 * en que orden y cuanto se espera antes de subir un escalon.
 */
@Entity
@Table(name = "politica_escalamiento")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PoliticaEscalamiento implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 60)
    @Column(name = "nombre", length = 60, nullable = false)
    private String nombre;

    @Size(max = 255)
    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @NotNull
    @Min(value = 0)
    @Max(value = 5)
    @Column(name = "repetir_veces", nullable = false)
    private Integer repetirVeces;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "equipo", "objetivos", "alertas", "politicas", "incidentes" }, allowSetters = true)
    private Servicio servicio;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "politica")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "politica", "rotacion", "destinatarioDirecto" }, allowSetters = true)
    private Set<PasoEscalamiento> pasos = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PoliticaEscalamiento id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public PoliticaEscalamiento nombre(String nombre) {
        this.setNombre(nombre);
        return this;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public PoliticaEscalamiento descripcion(String descripcion) {
        this.setDescripcion(descripcion);
        return this;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getRepetirVeces() {
        return this.repetirVeces;
    }

    public PoliticaEscalamiento repetirVeces(Integer repetirVeces) {
        this.setRepetirVeces(repetirVeces);
        return this;
    }

    public void setRepetirVeces(Integer repetirVeces) {
        this.repetirVeces = repetirVeces;
    }

    public Servicio getServicio() {
        return this.servicio;
    }

    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }

    public PoliticaEscalamiento servicio(Servicio servicio) {
        this.setServicio(servicio);
        return this;
    }

    public Set<PasoEscalamiento> getPasos() {
        return this.pasos;
    }

    public void setPasos(Set<PasoEscalamiento> pasoEscalamientos) {
        if (this.pasos != null) {
            this.pasos.forEach(i -> i.setPolitica(null));
        }
        if (pasoEscalamientos != null) {
            pasoEscalamientos.forEach(i -> i.setPolitica(this));
        }
        this.pasos = pasoEscalamientos;
    }

    public PoliticaEscalamiento pasos(Set<PasoEscalamiento> pasoEscalamientos) {
        this.setPasos(pasoEscalamientos);
        return this;
    }

    public PoliticaEscalamiento addPaso(PasoEscalamiento pasoEscalamiento) {
        this.pasos.add(pasoEscalamiento);
        pasoEscalamiento.setPolitica(this);
        return this;
    }

    public PoliticaEscalamiento removePaso(PasoEscalamiento pasoEscalamiento) {
        this.pasos.remove(pasoEscalamiento);
        pasoEscalamiento.setPolitica(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PoliticaEscalamiento)) {
            return false;
        }
        return getId() != null && getId().equals(((PoliticaEscalamiento) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PoliticaEscalamiento{" +
            "id=" + getId() +
            ", nombre='" + getNombre() + "'" +
            ", descripcion='" + getDescripcion() + "'" +
            ", repetirVeces=" + getRepetirVeces() +
            "}";
    }
}
