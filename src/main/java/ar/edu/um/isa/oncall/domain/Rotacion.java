package ar.edu.um.isa.oncall.domain;

import ar.edu.um.isa.oncall.domain.enumeration.TipoRotacion;
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
 * Rotacion de guardia de un equipo. Define el patron; los turnos
 * concretos viven en TurnoDeGuardia.
 */
@Entity
@Table(name = "rotacion")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Rotacion implements Serializable {

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

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoRotacion tipo;

    @NotNull
    @Size(max = 50)
    @Column(name = "zona_horaria", length = 50, nullable = false)
    private String zonaHoraria;

    @NotNull
    @Column(name = "activa", nullable = false)
    private Boolean activa;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "servicios", "rotacions" }, allowSetters = true)
    private Equipo equipo;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "rotacion")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "rotacion", "responsable" }, allowSetters = true)
    private Set<TurnoDeGuardia> turnos = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "rotacion")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "politica", "rotacion", "destinatarioDirecto" }, allowSetters = true)
    private Set<PasoEscalamiento> pasos = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Rotacion id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public Rotacion nombre(String nombre) {
        this.setNombre(nombre);
        return this;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoRotacion getTipo() {
        return this.tipo;
    }

    public Rotacion tipo(TipoRotacion tipo) {
        this.setTipo(tipo);
        return this;
    }

    public void setTipo(TipoRotacion tipo) {
        this.tipo = tipo;
    }

    public String getZonaHoraria() {
        return this.zonaHoraria;
    }

    public Rotacion zonaHoraria(String zonaHoraria) {
        this.setZonaHoraria(zonaHoraria);
        return this;
    }

    public void setZonaHoraria(String zonaHoraria) {
        this.zonaHoraria = zonaHoraria;
    }

    public Boolean getActiva() {
        return this.activa;
    }

    public Rotacion activa(Boolean activa) {
        this.setActiva(activa);
        return this;
    }

    public void setActiva(Boolean activa) {
        this.activa = activa;
    }

    public Equipo getEquipo() {
        return this.equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

    public Rotacion equipo(Equipo equipo) {
        this.setEquipo(equipo);
        return this;
    }

    public Set<TurnoDeGuardia> getTurnos() {
        return this.turnos;
    }

    public void setTurnos(Set<TurnoDeGuardia> turnoDeGuardias) {
        if (this.turnos != null) {
            this.turnos.forEach(i -> i.setRotacion(null));
        }
        if (turnoDeGuardias != null) {
            turnoDeGuardias.forEach(i -> i.setRotacion(this));
        }
        this.turnos = turnoDeGuardias;
    }

    public Rotacion turnos(Set<TurnoDeGuardia> turnoDeGuardias) {
        this.setTurnos(turnoDeGuardias);
        return this;
    }

    public Rotacion addTurno(TurnoDeGuardia turnoDeGuardia) {
        this.turnos.add(turnoDeGuardia);
        turnoDeGuardia.setRotacion(this);
        return this;
    }

    public Rotacion removeTurno(TurnoDeGuardia turnoDeGuardia) {
        this.turnos.remove(turnoDeGuardia);
        turnoDeGuardia.setRotacion(null);
        return this;
    }

    public Set<PasoEscalamiento> getPasos() {
        return this.pasos;
    }

    public void setPasos(Set<PasoEscalamiento> pasoEscalamientos) {
        if (this.pasos != null) {
            this.pasos.forEach(i -> i.setRotacion(null));
        }
        if (pasoEscalamientos != null) {
            pasoEscalamientos.forEach(i -> i.setRotacion(this));
        }
        this.pasos = pasoEscalamientos;
    }

    public Rotacion pasos(Set<PasoEscalamiento> pasoEscalamientos) {
        this.setPasos(pasoEscalamientos);
        return this;
    }

    public Rotacion addPaso(PasoEscalamiento pasoEscalamiento) {
        this.pasos.add(pasoEscalamiento);
        pasoEscalamiento.setRotacion(this);
        return this;
    }

    public Rotacion removePaso(PasoEscalamiento pasoEscalamiento) {
        this.pasos.remove(pasoEscalamiento);
        pasoEscalamiento.setRotacion(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Rotacion)) {
            return false;
        }
        return getId() != null && getId().equals(((Rotacion) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Rotacion{" +
            "id=" + getId() +
            ", nombre='" + getNombre() + "'" +
            ", tipo='" + getTipo() + "'" +
            ", zonaHoraria='" + getZonaHoraria() + "'" +
            ", activa='" + getActiva() + "'" +
            "}";
    }
}
