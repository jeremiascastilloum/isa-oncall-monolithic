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
 * Equipo responsable de uno o mas servicios en produccion.
 */
@Entity
@Table(name = "equipo")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Equipo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(name = "nombre", length = 50, nullable = false, unique = true)
    private String nombre;

    @NotNull
    @Size(max = 120)
    @Column(name = "email_contacto", length = 120, nullable = false)
    private String emailContacto;

    @Size(max = 60)
    @Column(name = "canal_chat", length = 60)
    private String canalChat;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "equipo")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "equipo", "objetivos", "alertas", "politicas", "incidentes" }, allowSetters = true)
    private Set<Servicio> servicios = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "equipo")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "equipo", "turnos", "pasos" }, allowSetters = true)
    private Set<Rotacion> rotacions = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Equipo id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public Equipo nombre(String nombre) {
        this.setNombre(nombre);
        return this;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmailContacto() {
        return this.emailContacto;
    }

    public Equipo emailContacto(String emailContacto) {
        this.setEmailContacto(emailContacto);
        return this;
    }

    public void setEmailContacto(String emailContacto) {
        this.emailContacto = emailContacto;
    }

    public String getCanalChat() {
        return this.canalChat;
    }

    public Equipo canalChat(String canalChat) {
        this.setCanalChat(canalChat);
        return this;
    }

    public void setCanalChat(String canalChat) {
        this.canalChat = canalChat;
    }

    public Set<Servicio> getServicios() {
        return this.servicios;
    }

    public void setServicios(Set<Servicio> servicios) {
        if (this.servicios != null) {
            this.servicios.forEach(i -> i.setEquipo(null));
        }
        if (servicios != null) {
            servicios.forEach(i -> i.setEquipo(this));
        }
        this.servicios = servicios;
    }

    public Equipo servicios(Set<Servicio> servicios) {
        this.setServicios(servicios);
        return this;
    }

    public Equipo addServicio(Servicio servicio) {
        this.servicios.add(servicio);
        servicio.setEquipo(this);
        return this;
    }

    public Equipo removeServicio(Servicio servicio) {
        this.servicios.remove(servicio);
        servicio.setEquipo(null);
        return this;
    }

    public Set<Rotacion> getRotacions() {
        return this.rotacions;
    }

    public void setRotacions(Set<Rotacion> rotacions) {
        if (this.rotacions != null) {
            this.rotacions.forEach(i -> i.setEquipo(null));
        }
        if (rotacions != null) {
            rotacions.forEach(i -> i.setEquipo(this));
        }
        this.rotacions = rotacions;
    }

    public Equipo rotacions(Set<Rotacion> rotacions) {
        this.setRotacions(rotacions);
        return this;
    }

    public Equipo addRotacion(Rotacion rotacion) {
        this.rotacions.add(rotacion);
        rotacion.setEquipo(this);
        return this;
    }

    public Equipo removeRotacion(Rotacion rotacion) {
        this.rotacions.remove(rotacion);
        rotacion.setEquipo(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Equipo)) {
            return false;
        }
        return getId() != null && getId().equals(((Equipo) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Equipo{" +
            "id=" + getId() +
            ", nombre='" + getNombre() + "'" +
            ", emailContacto='" + getEmailContacto() + "'" +
            ", canalChat='" + getCanalChat() + "'" +
            "}";
    }
}
