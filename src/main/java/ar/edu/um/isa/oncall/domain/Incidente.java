package ar.edu.um.isa.oncall.domain;

import ar.edu.um.isa.oncall.domain.enumeration.EstadoIncidente;
import ar.edu.um.isa.oncall.domain.enumeration.Severidad;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Incidente: la interrupcion real que estamos gestionando.
 * Las marcas de tiempo son la materia prima del MTTA y el MTTR.
 */
@Entity
@Table(name = "incidente")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Incidente implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 140)
    @Column(name = "titulo", length = 140, nullable = false)
    private String titulo;

    @Size(max = 2000)
    @Column(name = "descripcion", length = 2000)
    private String descripcion;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "severidad", nullable = false)
    private Severidad severidad;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoIncidente estado;

    @NotNull
    @Column(name = "detectado_en", nullable = false)
    private Instant detectadoEn;

    @Column(name = "reconocido_en")
    private Instant reconocidoEn;

    @Column(name = "mitigado_en")
    private Instant mitigadoEn;

    @Column(name = "resuelto_en")
    private Instant resueltoEn;

    @Min(value = 0)
    @Column(name = "usuarios_afectados")
    private Integer usuariosAfectados;

    @Column(name = "cumplio_objetivo")
    private Boolean cumplioObjetivo;

    @ManyToOne(fetch = FetchType.LAZY)
    private User comandante;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_incidente__servicio",
        joinColumns = @JoinColumn(name = "incidente_id"),
        inverseJoinColumns = @JoinColumn(name = "servicio_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "equipo", "objetivos", "alertas", "politicas", "incidentes" }, allowSetters = true)
    private Set<Servicio> servicios = new HashSet<>();

    @JsonIgnoreProperties(value = { "incidente", "accions" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "incidente")
    private Postmortem postmortem;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "incidente")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "servicio", "incidente" }, allowSetters = true)
    private Set<Alerta> alertas = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "incidente")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "incidente" }, allowSetters = true)
    private Set<EventoDeIncidente> eventos = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "incidente")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "incidente", "destinatario" }, allowSetters = true)
    private Set<Notificacion> notificacions = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Incidente id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return this.titulo;
    }

    public Incidente titulo(String titulo) {
        this.setTitulo(titulo);
        return this;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public Incidente descripcion(String descripcion) {
        this.setDescripcion(descripcion);
        return this;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Severidad getSeveridad() {
        return this.severidad;
    }

    public Incidente severidad(Severidad severidad) {
        this.setSeveridad(severidad);
        return this;
    }

    public void setSeveridad(Severidad severidad) {
        this.severidad = severidad;
    }

    public EstadoIncidente getEstado() {
        return this.estado;
    }

    public Incidente estado(EstadoIncidente estado) {
        this.setEstado(estado);
        return this;
    }

    public void setEstado(EstadoIncidente estado) {
        this.estado = estado;
    }

    public Instant getDetectadoEn() {
        return this.detectadoEn;
    }

    public Incidente detectadoEn(Instant detectadoEn) {
        this.setDetectadoEn(detectadoEn);
        return this;
    }

    public void setDetectadoEn(Instant detectadoEn) {
        this.detectadoEn = detectadoEn;
    }

    public Instant getReconocidoEn() {
        return this.reconocidoEn;
    }

    public Incidente reconocidoEn(Instant reconocidoEn) {
        this.setReconocidoEn(reconocidoEn);
        return this;
    }

    public void setReconocidoEn(Instant reconocidoEn) {
        this.reconocidoEn = reconocidoEn;
    }

    public Instant getMitigadoEn() {
        return this.mitigadoEn;
    }

    public Incidente mitigadoEn(Instant mitigadoEn) {
        this.setMitigadoEn(mitigadoEn);
        return this;
    }

    public void setMitigadoEn(Instant mitigadoEn) {
        this.mitigadoEn = mitigadoEn;
    }

    public Instant getResueltoEn() {
        return this.resueltoEn;
    }

    public Incidente resueltoEn(Instant resueltoEn) {
        this.setResueltoEn(resueltoEn);
        return this;
    }

    public void setResueltoEn(Instant resueltoEn) {
        this.resueltoEn = resueltoEn;
    }

    public Integer getUsuariosAfectados() {
        return this.usuariosAfectados;
    }

    public Incidente usuariosAfectados(Integer usuariosAfectados) {
        this.setUsuariosAfectados(usuariosAfectados);
        return this;
    }

    public void setUsuariosAfectados(Integer usuariosAfectados) {
        this.usuariosAfectados = usuariosAfectados;
    }

    public Boolean getCumplioObjetivo() {
        return this.cumplioObjetivo;
    }

    public Incidente cumplioObjetivo(Boolean cumplioObjetivo) {
        this.setCumplioObjetivo(cumplioObjetivo);
        return this;
    }

    public void setCumplioObjetivo(Boolean cumplioObjetivo) {
        this.cumplioObjetivo = cumplioObjetivo;
    }

    public User getComandante() {
        return this.comandante;
    }

    public void setComandante(User user) {
        this.comandante = user;
    }

    public Incidente comandante(User user) {
        this.setComandante(user);
        return this;
    }

    public Set<Servicio> getServicios() {
        return this.servicios;
    }

    public void setServicios(Set<Servicio> servicios) {
        this.servicios = servicios;
    }

    public Incidente servicios(Set<Servicio> servicios) {
        this.setServicios(servicios);
        return this;
    }

    public Incidente addServicio(Servicio servicio) {
        this.servicios.add(servicio);
        return this;
    }

    public Incidente removeServicio(Servicio servicio) {
        this.servicios.remove(servicio);
        return this;
    }

    public Postmortem getPostmortem() {
        return this.postmortem;
    }

    public void setPostmortem(Postmortem postmortem) {
        if (this.postmortem != null) {
            this.postmortem.setIncidente(null);
        }
        if (postmortem != null) {
            postmortem.setIncidente(this);
        }
        this.postmortem = postmortem;
    }

    public Incidente postmortem(Postmortem postmortem) {
        this.setPostmortem(postmortem);
        return this;
    }

    public Set<Alerta> getAlertas() {
        return this.alertas;
    }

    public void setAlertas(Set<Alerta> alertas) {
        if (this.alertas != null) {
            this.alertas.forEach(i -> i.setIncidente(null));
        }
        if (alertas != null) {
            alertas.forEach(i -> i.setIncidente(this));
        }
        this.alertas = alertas;
    }

    public Incidente alertas(Set<Alerta> alertas) {
        this.setAlertas(alertas);
        return this;
    }

    public Incidente addAlerta(Alerta alerta) {
        this.alertas.add(alerta);
        alerta.setIncidente(this);
        return this;
    }

    public Incidente removeAlerta(Alerta alerta) {
        this.alertas.remove(alerta);
        alerta.setIncidente(null);
        return this;
    }

    public Set<EventoDeIncidente> getEventos() {
        return this.eventos;
    }

    public void setEventos(Set<EventoDeIncidente> eventoDeIncidentes) {
        if (this.eventos != null) {
            this.eventos.forEach(i -> i.setIncidente(null));
        }
        if (eventoDeIncidentes != null) {
            eventoDeIncidentes.forEach(i -> i.setIncidente(this));
        }
        this.eventos = eventoDeIncidentes;
    }

    public Incidente eventos(Set<EventoDeIncidente> eventoDeIncidentes) {
        this.setEventos(eventoDeIncidentes);
        return this;
    }

    public Incidente addEvento(EventoDeIncidente eventoDeIncidente) {
        this.eventos.add(eventoDeIncidente);
        eventoDeIncidente.setIncidente(this);
        return this;
    }

    public Incidente removeEvento(EventoDeIncidente eventoDeIncidente) {
        this.eventos.remove(eventoDeIncidente);
        eventoDeIncidente.setIncidente(null);
        return this;
    }

    public Set<Notificacion> getNotificacions() {
        return this.notificacions;
    }

    public void setNotificacions(Set<Notificacion> notificacions) {
        if (this.notificacions != null) {
            this.notificacions.forEach(i -> i.setIncidente(null));
        }
        if (notificacions != null) {
            notificacions.forEach(i -> i.setIncidente(this));
        }
        this.notificacions = notificacions;
    }

    public Incidente notificacions(Set<Notificacion> notificacions) {
        this.setNotificacions(notificacions);
        return this;
    }

    public Incidente addNotificacion(Notificacion notificacion) {
        this.notificacions.add(notificacion);
        notificacion.setIncidente(this);
        return this;
    }

    public Incidente removeNotificacion(Notificacion notificacion) {
        this.notificacions.remove(notificacion);
        notificacion.setIncidente(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Incidente)) {
            return false;
        }
        return getId() != null && getId().equals(((Incidente) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Incidente{" +
            "id=" + getId() +
            ", titulo='" + getTitulo() + "'" +
            ", descripcion='" + getDescripcion() + "'" +
            ", severidad='" + getSeveridad() + "'" +
            ", estado='" + getEstado() + "'" +
            ", detectadoEn='" + getDetectadoEn() + "'" +
            ", reconocidoEn='" + getReconocidoEn() + "'" +
            ", mitigadoEn='" + getMitigadoEn() + "'" +
            ", resueltoEn='" + getResueltoEn() + "'" +
            ", usuariosAfectados=" + getUsuariosAfectados() +
            ", cumplioObjetivo='" + getCumplioObjetivo() + "'" +
            "}";
    }
}
