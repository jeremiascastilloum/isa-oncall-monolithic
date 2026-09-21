package ar.edu.um.isa.oncall.domain;

import ar.edu.um.isa.oncall.domain.enumeration.EstadoAccion;
import ar.edu.um.isa.oncall.domain.enumeration.Prioridad;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Compromiso concreto que sale del postmortem.
 */
@Entity
@Table(name = "accion_correctiva")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AccionCorrectiva implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 300)
    @Column(name = "descripcion", length = 300, nullable = false)
    private String descripcion;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "prioridad", nullable = false)
    private Prioridad prioridad;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoAccion estado;

    @Column(name = "fecha_limite")
    private LocalDate fechaLimite;

    @Size(max = 255)
    @Column(name = "ticket_url", length = 255)
    private String ticketUrl;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "incidente", "accions" }, allowSetters = true)
    private Postmortem postmortem;

    @ManyToOne(fetch = FetchType.LAZY)
    private User responsable;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AccionCorrectiva id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public AccionCorrectiva descripcion(String descripcion) {
        this.setDescripcion(descripcion);
        return this;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Prioridad getPrioridad() {
        return this.prioridad;
    }

    public AccionCorrectiva prioridad(Prioridad prioridad) {
        this.setPrioridad(prioridad);
        return this;
    }

    public void setPrioridad(Prioridad prioridad) {
        this.prioridad = prioridad;
    }

    public EstadoAccion getEstado() {
        return this.estado;
    }

    public AccionCorrectiva estado(EstadoAccion estado) {
        this.setEstado(estado);
        return this;
    }

    public void setEstado(EstadoAccion estado) {
        this.estado = estado;
    }

    public LocalDate getFechaLimite() {
        return this.fechaLimite;
    }

    public AccionCorrectiva fechaLimite(LocalDate fechaLimite) {
        this.setFechaLimite(fechaLimite);
        return this;
    }

    public void setFechaLimite(LocalDate fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public String getTicketUrl() {
        return this.ticketUrl;
    }

    public AccionCorrectiva ticketUrl(String ticketUrl) {
        this.setTicketUrl(ticketUrl);
        return this;
    }

    public void setTicketUrl(String ticketUrl) {
        this.ticketUrl = ticketUrl;
    }

    public Postmortem getPostmortem() {
        return this.postmortem;
    }

    public void setPostmortem(Postmortem postmortem) {
        this.postmortem = postmortem;
    }

    public AccionCorrectiva postmortem(Postmortem postmortem) {
        this.setPostmortem(postmortem);
        return this;
    }

    public User getResponsable() {
        return this.responsable;
    }

    public void setResponsable(User user) {
        this.responsable = user;
    }

    public AccionCorrectiva responsable(User user) {
        this.setResponsable(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AccionCorrectiva)) {
            return false;
        }
        return getId() != null && getId().equals(((AccionCorrectiva) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AccionCorrectiva{" +
            "id=" + getId() +
            ", descripcion='" + getDescripcion() + "'" +
            ", prioridad='" + getPrioridad() + "'" +
            ", estado='" + getEstado() + "'" +
            ", fechaLimite='" + getFechaLimite() + "'" +
            ", ticketUrl='" + getTicketUrl() + "'" +
            "}";
    }
}
