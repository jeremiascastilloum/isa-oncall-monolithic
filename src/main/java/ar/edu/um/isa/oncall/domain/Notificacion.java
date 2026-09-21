package ar.edu.um.isa.oncall.domain;

import ar.edu.um.isa.oncall.domain.enumeration.Canal;
import ar.edu.um.isa.oncall.domain.enumeration.EstadoNotificacion;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Intento de avisarle a una persona por un canal.
 */
@Entity
@Table(name = "notificacion")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Notificacion implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "canal", nullable = false)
    private Canal canal;

    @NotNull
    @Size(max = 120)
    @Column(name = "destino", length = 120, nullable = false)
    private String destino;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoNotificacion estado;

    @Column(name = "enviada_en")
    private Instant enviadaEn;

    @NotNull
    @Min(value = 0)
    @Max(value = 10)
    @Column(name = "intentos", nullable = false)
    private Integer intentos;

    @Size(max = 255)
    @Column(name = "error_mensaje", length = 255)
    private String errorMensaje;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "comandante", "servicios", "postmortem", "alertas", "eventos", "notificacions" }, allowSetters = true)
    private Incidente incidente;

    @ManyToOne(optional = false)
    @NotNull
    private User destinatario;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Notificacion id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Canal getCanal() {
        return this.canal;
    }

    public Notificacion canal(Canal canal) {
        this.setCanal(canal);
        return this;
    }

    public void setCanal(Canal canal) {
        this.canal = canal;
    }

    public String getDestino() {
        return this.destino;
    }

    public Notificacion destino(String destino) {
        this.setDestino(destino);
        return this;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public EstadoNotificacion getEstado() {
        return this.estado;
    }

    public Notificacion estado(EstadoNotificacion estado) {
        this.setEstado(estado);
        return this;
    }

    public void setEstado(EstadoNotificacion estado) {
        this.estado = estado;
    }

    public Instant getEnviadaEn() {
        return this.enviadaEn;
    }

    public Notificacion enviadaEn(Instant enviadaEn) {
        this.setEnviadaEn(enviadaEn);
        return this;
    }

    public void setEnviadaEn(Instant enviadaEn) {
        this.enviadaEn = enviadaEn;
    }

    public Integer getIntentos() {
        return this.intentos;
    }

    public Notificacion intentos(Integer intentos) {
        this.setIntentos(intentos);
        return this;
    }

    public void setIntentos(Integer intentos) {
        this.intentos = intentos;
    }

    public String getErrorMensaje() {
        return this.errorMensaje;
    }

    public Notificacion errorMensaje(String errorMensaje) {
        this.setErrorMensaje(errorMensaje);
        return this;
    }

    public void setErrorMensaje(String errorMensaje) {
        this.errorMensaje = errorMensaje;
    }

    public Incidente getIncidente() {
        return this.incidente;
    }

    public void setIncidente(Incidente incidente) {
        this.incidente = incidente;
    }

    public Notificacion incidente(Incidente incidente) {
        this.setIncidente(incidente);
        return this;
    }

    public User getDestinatario() {
        return this.destinatario;
    }

    public void setDestinatario(User user) {
        this.destinatario = user;
    }

    public Notificacion destinatario(User user) {
        this.setDestinatario(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Notificacion)) {
            return false;
        }
        return getId() != null && getId().equals(((Notificacion) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Notificacion{" +
            "id=" + getId() +
            ", canal='" + getCanal() + "'" +
            ", destino='" + getDestino() + "'" +
            ", estado='" + getEstado() + "'" +
            ", enviadaEn='" + getEnviadaEn() + "'" +
            ", intentos=" + getIntentos() +
            ", errorMensaje='" + getErrorMensaje() + "'" +
            "}";
    }
}
