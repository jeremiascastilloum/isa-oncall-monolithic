package ar.edu.um.isa.oncall.domain;

import ar.edu.um.isa.oncall.domain.enumeration.Canal;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Un escalon de la politica.
 */
@Entity
@Table(name = "paso_escalamiento")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PasoEscalamiento implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Min(value = 1)
    @Max(value = 10)
    @Column(name = "orden", nullable = false)
    private Integer orden;

    @NotNull
    @Min(value = 0)
    @Max(value = 120)
    @Column(name = "espera_minutos", nullable = false)
    private Integer esperaMinutos;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "canal", nullable = false)
    private Canal canal;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "servicio", "pasos" }, allowSetters = true)
    private PoliticaEscalamiento politica;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "equipo", "turnos", "pasos" }, allowSetters = true)
    private Rotacion rotacion;

    @ManyToOne(fetch = FetchType.LAZY)
    private User destinatarioDirecto;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PasoEscalamiento id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getOrden() {
        return this.orden;
    }

    public PasoEscalamiento orden(Integer orden) {
        this.setOrden(orden);
        return this;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public Integer getEsperaMinutos() {
        return this.esperaMinutos;
    }

    public PasoEscalamiento esperaMinutos(Integer esperaMinutos) {
        this.setEsperaMinutos(esperaMinutos);
        return this;
    }

    public void setEsperaMinutos(Integer esperaMinutos) {
        this.esperaMinutos = esperaMinutos;
    }

    public Canal getCanal() {
        return this.canal;
    }

    public PasoEscalamiento canal(Canal canal) {
        this.setCanal(canal);
        return this;
    }

    public void setCanal(Canal canal) {
        this.canal = canal;
    }

    public PoliticaEscalamiento getPolitica() {
        return this.politica;
    }

    public void setPolitica(PoliticaEscalamiento politicaEscalamiento) {
        this.politica = politicaEscalamiento;
    }

    public PasoEscalamiento politica(PoliticaEscalamiento politicaEscalamiento) {
        this.setPolitica(politicaEscalamiento);
        return this;
    }

    public Rotacion getRotacion() {
        return this.rotacion;
    }

    public void setRotacion(Rotacion rotacion) {
        this.rotacion = rotacion;
    }

    public PasoEscalamiento rotacion(Rotacion rotacion) {
        this.setRotacion(rotacion);
        return this;
    }

    public User getDestinatarioDirecto() {
        return this.destinatarioDirecto;
    }

    public void setDestinatarioDirecto(User user) {
        this.destinatarioDirecto = user;
    }

    public PasoEscalamiento destinatarioDirecto(User user) {
        this.setDestinatarioDirecto(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PasoEscalamiento)) {
            return false;
        }
        return getId() != null && getId().equals(((PasoEscalamiento) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PasoEscalamiento{" +
            "id=" + getId() +
            ", orden=" + getOrden() +
            ", esperaMinutos=" + getEsperaMinutos() +
            ", canal='" + getCanal() + "'" +
            "}";
    }
}
