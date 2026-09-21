package ar.edu.um.isa.oncall.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Ventana concreta de tiempo en la que una persona esta de guardia.
 */
@Entity
@Table(name = "turno_de_guardia")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TurnoDeGuardia implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "desde", nullable = false)
    private Instant desde;

    @NotNull
    @Column(name = "hasta", nullable = false)
    private Instant hasta;

    @Column(name = "es_reemplazo")
    private Boolean esReemplazo;

    @Size(max = 255)
    @Column(name = "nota", length = 255)
    private String nota;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "equipo", "turnos", "pasos" }, allowSetters = true)
    private Rotacion rotacion;

    @ManyToOne(optional = false)
    @NotNull
    private User responsable;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TurnoDeGuardia id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDesde() {
        return this.desde;
    }

    public TurnoDeGuardia desde(Instant desde) {
        this.setDesde(desde);
        return this;
    }

    public void setDesde(Instant desde) {
        this.desde = desde;
    }

    public Instant getHasta() {
        return this.hasta;
    }

    public TurnoDeGuardia hasta(Instant hasta) {
        this.setHasta(hasta);
        return this;
    }

    public void setHasta(Instant hasta) {
        this.hasta = hasta;
    }

    public Boolean getEsReemplazo() {
        return this.esReemplazo;
    }

    public TurnoDeGuardia esReemplazo(Boolean esReemplazo) {
        this.setEsReemplazo(esReemplazo);
        return this;
    }

    public void setEsReemplazo(Boolean esReemplazo) {
        this.esReemplazo = esReemplazo;
    }

    public String getNota() {
        return this.nota;
    }

    public TurnoDeGuardia nota(String nota) {
        this.setNota(nota);
        return this;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public Rotacion getRotacion() {
        return this.rotacion;
    }

    public void setRotacion(Rotacion rotacion) {
        this.rotacion = rotacion;
    }

    public TurnoDeGuardia rotacion(Rotacion rotacion) {
        this.setRotacion(rotacion);
        return this;
    }

    public User getResponsable() {
        return this.responsable;
    }

    public void setResponsable(User user) {
        this.responsable = user;
    }

    public TurnoDeGuardia responsable(User user) {
        this.setResponsable(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TurnoDeGuardia)) {
            return false;
        }
        return getId() != null && getId().equals(((TurnoDeGuardia) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TurnoDeGuardia{" +
            "id=" + getId() +
            ", desde='" + getDesde() + "'" +
            ", hasta='" + getHasta() + "'" +
            ", esReemplazo='" + getEsReemplazo() + "'" +
            ", nota='" + getNota() + "'" +
            "}";
    }
}
