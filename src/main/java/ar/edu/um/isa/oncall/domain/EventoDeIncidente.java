package ar.edu.um.isa.oncall.domain;

import ar.edu.um.isa.oncall.domain.enumeration.TipoEvento;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Linea de tiempo del incidente. Cada cosa que pasa deja una entrada.
 */
@Entity
@Table(name = "evento_de_incidente")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EventoDeIncidente implements Serializable {

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
    private TipoEvento tipo;

    @NotNull
    @Size(max = 500)
    @Column(name = "detalle", length = 500, nullable = false)
    private String detalle;

    @NotNull
    @Column(name = "ocurrido_en", nullable = false)
    private Instant ocurridoEn;

    @NotNull
    @Column(name = "automatico", nullable = false)
    private Boolean automatico;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "comandante", "servicios", "postmortem", "alertas", "eventos", "notificacions" }, allowSetters = true)
    private Incidente incidente;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public EventoDeIncidente id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoEvento getTipo() {
        return this.tipo;
    }

    public EventoDeIncidente tipo(TipoEvento tipo) {
        this.setTipo(tipo);
        return this;
    }

    public void setTipo(TipoEvento tipo) {
        this.tipo = tipo;
    }

    public String getDetalle() {
        return this.detalle;
    }

    public EventoDeIncidente detalle(String detalle) {
        this.setDetalle(detalle);
        return this;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public Instant getOcurridoEn() {
        return this.ocurridoEn;
    }

    public EventoDeIncidente ocurridoEn(Instant ocurridoEn) {
        this.setOcurridoEn(ocurridoEn);
        return this;
    }

    public void setOcurridoEn(Instant ocurridoEn) {
        this.ocurridoEn = ocurridoEn;
    }

    public Boolean getAutomatico() {
        return this.automatico;
    }

    public EventoDeIncidente automatico(Boolean automatico) {
        this.setAutomatico(automatico);
        return this;
    }

    public void setAutomatico(Boolean automatico) {
        this.automatico = automatico;
    }

    public Incidente getIncidente() {
        return this.incidente;
    }

    public void setIncidente(Incidente incidente) {
        this.incidente = incidente;
    }

    public EventoDeIncidente incidente(Incidente incidente) {
        this.setIncidente(incidente);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EventoDeIncidente)) {
            return false;
        }
        return getId() != null && getId().equals(((EventoDeIncidente) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EventoDeIncidente{" +
            "id=" + getId() +
            ", tipo='" + getTipo() + "'" +
            ", detalle='" + getDetalle() + "'" +
            ", ocurridoEn='" + getOcurridoEn() + "'" +
            ", automatico='" + getAutomatico() + "'" +
            "}";
    }
}
