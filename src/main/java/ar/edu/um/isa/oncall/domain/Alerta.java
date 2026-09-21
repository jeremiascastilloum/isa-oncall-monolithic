package ar.edu.um.isa.oncall.domain;

import ar.edu.um.isa.oncall.domain.enumeration.OrigenAlerta;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Senal cruda que llega desde una herramienta de monitoreo.
 * El fingerprint es la clave de deduplicacion: varias alertas con el
 * mismo fingerprint deberian colapsar en un unico incidente.
 */
@Entity
@Table(name = "alerta")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Alerta implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 120)
    @Column(name = "fingerprint", length = 120, nullable = false)
    private String fingerprint;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "origen", nullable = false)
    private OrigenAlerta origen;

    @NotNull
    @Size(max = 200)
    @Column(name = "resumen", length = 200, nullable = false)
    private String resumen;

    @Size(max = 4000)
    @Column(name = "payload", length = 4000)
    private String payload;

    @NotNull
    @Column(name = "recibida_en", nullable = false)
    private Instant recibidaEn;

    @NotNull
    @Column(name = "procesada", nullable = false)
    private Boolean procesada;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "equipo", "objetivos", "alertas", "politicas", "incidentes" }, allowSetters = true)
    private Servicio servicio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "comandante", "servicios", "postmortem", "alertas", "eventos", "notificacions" }, allowSetters = true)
    private Incidente incidente;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Alerta id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFingerprint() {
        return this.fingerprint;
    }

    public Alerta fingerprint(String fingerprint) {
        this.setFingerprint(fingerprint);
        return this;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public OrigenAlerta getOrigen() {
        return this.origen;
    }

    public Alerta origen(OrigenAlerta origen) {
        this.setOrigen(origen);
        return this;
    }

    public void setOrigen(OrigenAlerta origen) {
        this.origen = origen;
    }

    public String getResumen() {
        return this.resumen;
    }

    public Alerta resumen(String resumen) {
        this.setResumen(resumen);
        return this;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public String getPayload() {
        return this.payload;
    }

    public Alerta payload(String payload) {
        this.setPayload(payload);
        return this;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Instant getRecibidaEn() {
        return this.recibidaEn;
    }

    public Alerta recibidaEn(Instant recibidaEn) {
        this.setRecibidaEn(recibidaEn);
        return this;
    }

    public void setRecibidaEn(Instant recibidaEn) {
        this.recibidaEn = recibidaEn;
    }

    public Boolean getProcesada() {
        return this.procesada;
    }

    public Alerta procesada(Boolean procesada) {
        this.setProcesada(procesada);
        return this;
    }

    public void setProcesada(Boolean procesada) {
        this.procesada = procesada;
    }

    public Servicio getServicio() {
        return this.servicio;
    }

    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }

    public Alerta servicio(Servicio servicio) {
        this.setServicio(servicio);
        return this;
    }

    public Incidente getIncidente() {
        return this.incidente;
    }

    public void setIncidente(Incidente incidente) {
        this.incidente = incidente;
    }

    public Alerta incidente(Incidente incidente) {
        this.setIncidente(incidente);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Alerta)) {
            return false;
        }
        return getId() != null && getId().equals(((Alerta) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Alerta{" +
            "id=" + getId() +
            ", fingerprint='" + getFingerprint() + "'" +
            ", origen='" + getOrigen() + "'" +
            ", resumen='" + getResumen() + "'" +
            ", payload='" + getPayload() + "'" +
            ", recibidaEn='" + getRecibidaEn() + "'" +
            ", procesada='" + getProcesada() + "'" +
            "}";
    }
}
