package ar.edu.um.isa.oncall.domain;

import ar.edu.um.isa.oncall.domain.enumeration.Criticidad;
import ar.edu.um.isa.oncall.domain.enumeration.Entorno;
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
 * Un servicio desplegado. Es la unidad sobre la que se abren incidentes.
 */
@Entity
@Table(name = "servicio")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Servicio implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 60)
    @Column(name = "nombre", length = 60, nullable = false, unique = true)
    private String nombre;

    @Size(max = 500)
    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "criticidad", nullable = false)
    private Criticidad criticidad;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "entorno", nullable = false)
    private Entorno entorno;

    @Size(max = 255)
    @Column(name = "repositorio_url", length = 255)
    private String repositorioUrl;

    @NotNull
    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "servicios", "rotacions" }, allowSetters = true)
    private Equipo equipo;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "servicio")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "servicio" }, allowSetters = true)
    private Set<ObjetivoDeServicio> objetivos = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "servicio")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "servicio", "incidente" }, allowSetters = true)
    private Set<Alerta> alertas = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "servicio")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "servicio", "pasos" }, allowSetters = true)
    private Set<PoliticaEscalamiento> politicas = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "servicios")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "comandante", "servicios", "postmortem", "alertas", "eventos", "notificacions" }, allowSetters = true)
    private Set<Incidente> incidentes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Servicio id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public Servicio nombre(String nombre) {
        this.setNombre(nombre);
        return this;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public Servicio descripcion(String descripcion) {
        this.setDescripcion(descripcion);
        return this;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Criticidad getCriticidad() {
        return this.criticidad;
    }

    public Servicio criticidad(Criticidad criticidad) {
        this.setCriticidad(criticidad);
        return this;
    }

    public void setCriticidad(Criticidad criticidad) {
        this.criticidad = criticidad;
    }

    public Entorno getEntorno() {
        return this.entorno;
    }

    public Servicio entorno(Entorno entorno) {
        this.setEntorno(entorno);
        return this;
    }

    public void setEntorno(Entorno entorno) {
        this.entorno = entorno;
    }

    public String getRepositorioUrl() {
        return this.repositorioUrl;
    }

    public Servicio repositorioUrl(String repositorioUrl) {
        this.setRepositorioUrl(repositorioUrl);
        return this;
    }

    public void setRepositorioUrl(String repositorioUrl) {
        this.repositorioUrl = repositorioUrl;
    }

    public Boolean getActivo() {
        return this.activo;
    }

    public Servicio activo(Boolean activo) {
        this.setActivo(activo);
        return this;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Equipo getEquipo() {
        return this.equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

    public Servicio equipo(Equipo equipo) {
        this.setEquipo(equipo);
        return this;
    }

    public Set<ObjetivoDeServicio> getObjetivos() {
        return this.objetivos;
    }

    public void setObjetivos(Set<ObjetivoDeServicio> objetivoDeServicios) {
        if (this.objetivos != null) {
            this.objetivos.forEach(i -> i.setServicio(null));
        }
        if (objetivoDeServicios != null) {
            objetivoDeServicios.forEach(i -> i.setServicio(this));
        }
        this.objetivos = objetivoDeServicios;
    }

    public Servicio objetivos(Set<ObjetivoDeServicio> objetivoDeServicios) {
        this.setObjetivos(objetivoDeServicios);
        return this;
    }

    public Servicio addObjetivo(ObjetivoDeServicio objetivoDeServicio) {
        this.objetivos.add(objetivoDeServicio);
        objetivoDeServicio.setServicio(this);
        return this;
    }

    public Servicio removeObjetivo(ObjetivoDeServicio objetivoDeServicio) {
        this.objetivos.remove(objetivoDeServicio);
        objetivoDeServicio.setServicio(null);
        return this;
    }

    public Set<Alerta> getAlertas() {
        return this.alertas;
    }

    public void setAlertas(Set<Alerta> alertas) {
        if (this.alertas != null) {
            this.alertas.forEach(i -> i.setServicio(null));
        }
        if (alertas != null) {
            alertas.forEach(i -> i.setServicio(this));
        }
        this.alertas = alertas;
    }

    public Servicio alertas(Set<Alerta> alertas) {
        this.setAlertas(alertas);
        return this;
    }

    public Servicio addAlerta(Alerta alerta) {
        this.alertas.add(alerta);
        alerta.setServicio(this);
        return this;
    }

    public Servicio removeAlerta(Alerta alerta) {
        this.alertas.remove(alerta);
        alerta.setServicio(null);
        return this;
    }

    public Set<PoliticaEscalamiento> getPoliticas() {
        return this.politicas;
    }

    public void setPoliticas(Set<PoliticaEscalamiento> politicaEscalamientos) {
        if (this.politicas != null) {
            this.politicas.forEach(i -> i.setServicio(null));
        }
        if (politicaEscalamientos != null) {
            politicaEscalamientos.forEach(i -> i.setServicio(this));
        }
        this.politicas = politicaEscalamientos;
    }

    public Servicio politicas(Set<PoliticaEscalamiento> politicaEscalamientos) {
        this.setPoliticas(politicaEscalamientos);
        return this;
    }

    public Servicio addPolitica(PoliticaEscalamiento politicaEscalamiento) {
        this.politicas.add(politicaEscalamiento);
        politicaEscalamiento.setServicio(this);
        return this;
    }

    public Servicio removePolitica(PoliticaEscalamiento politicaEscalamiento) {
        this.politicas.remove(politicaEscalamiento);
        politicaEscalamiento.setServicio(null);
        return this;
    }

    public Set<Incidente> getIncidentes() {
        return this.incidentes;
    }

    public void setIncidentes(Set<Incidente> incidentes) {
        if (this.incidentes != null) {
            this.incidentes.forEach(i -> i.removeServicio(this));
        }
        if (incidentes != null) {
            incidentes.forEach(i -> i.addServicio(this));
        }
        this.incidentes = incidentes;
    }

    public Servicio incidentes(Set<Incidente> incidentes) {
        this.setIncidentes(incidentes);
        return this;
    }

    public Servicio addIncidente(Incidente incidente) {
        this.incidentes.add(incidente);
        incidente.getServicios().add(this);
        return this;
    }

    public Servicio removeIncidente(Incidente incidente) {
        this.incidentes.remove(incidente);
        incidente.getServicios().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Servicio)) {
            return false;
        }
        return getId() != null && getId().equals(((Servicio) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Servicio{" +
            "id=" + getId() +
            ", nombre='" + getNombre() + "'" +
            ", descripcion='" + getDescripcion() + "'" +
            ", criticidad='" + getCriticidad() + "'" +
            ", entorno='" + getEntorno() + "'" +
            ", repositorioUrl='" + getRepositorioUrl() + "'" +
            ", activo='" + getActivo() + "'" +
            "}";
    }
}
