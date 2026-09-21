package ar.edu.um.isa.oncall.domain;

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
 * Analisis posterior al incidente. Sin culpables, con causas.
 */
@Entity
@Table(name = "postmortem")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Postmortem implements Serializable {

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

    @NotNull
    @Size(max = 500)
    @Column(name = "resumen", length = 500, nullable = false)
    private String resumen;

    @NotNull
    @Size(max = 2000)
    @Column(name = "causa_raiz", length = 2000, nullable = false)
    private String causaRaiz;

    @Size(max = 4000)
    @Column(name = "linea_de_tiempo", length = 4000)
    private String lineaDeTiempo;

    @Size(max = 2000)
    @Column(name = "lecciones_aprendidas", length = 2000)
    private String leccionesAprendidas;

    @NotNull
    @Column(name = "publicado", nullable = false)
    private Boolean publicado;

    @Column(name = "publicado_en")
    private Instant publicadoEn;

    @JsonIgnoreProperties(value = { "comandante", "servicios", "postmortem", "alertas", "eventos", "notificacions" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private Incidente incidente;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "postmortem")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "postmortem", "responsable" }, allowSetters = true)
    private Set<AccionCorrectiva> accions = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Postmortem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return this.titulo;
    }

    public Postmortem titulo(String titulo) {
        this.setTitulo(titulo);
        return this;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getResumen() {
        return this.resumen;
    }

    public Postmortem resumen(String resumen) {
        this.setResumen(resumen);
        return this;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public String getCausaRaiz() {
        return this.causaRaiz;
    }

    public Postmortem causaRaiz(String causaRaiz) {
        this.setCausaRaiz(causaRaiz);
        return this;
    }

    public void setCausaRaiz(String causaRaiz) {
        this.causaRaiz = causaRaiz;
    }

    public String getLineaDeTiempo() {
        return this.lineaDeTiempo;
    }

    public Postmortem lineaDeTiempo(String lineaDeTiempo) {
        this.setLineaDeTiempo(lineaDeTiempo);
        return this;
    }

    public void setLineaDeTiempo(String lineaDeTiempo) {
        this.lineaDeTiempo = lineaDeTiempo;
    }

    public String getLeccionesAprendidas() {
        return this.leccionesAprendidas;
    }

    public Postmortem leccionesAprendidas(String leccionesAprendidas) {
        this.setLeccionesAprendidas(leccionesAprendidas);
        return this;
    }

    public void setLeccionesAprendidas(String leccionesAprendidas) {
        this.leccionesAprendidas = leccionesAprendidas;
    }

    public Boolean getPublicado() {
        return this.publicado;
    }

    public Postmortem publicado(Boolean publicado) {
        this.setPublicado(publicado);
        return this;
    }

    public void setPublicado(Boolean publicado) {
        this.publicado = publicado;
    }

    public Instant getPublicadoEn() {
        return this.publicadoEn;
    }

    public Postmortem publicadoEn(Instant publicadoEn) {
        this.setPublicadoEn(publicadoEn);
        return this;
    }

    public void setPublicadoEn(Instant publicadoEn) {
        this.publicadoEn = publicadoEn;
    }

    public Incidente getIncidente() {
        return this.incidente;
    }

    public void setIncidente(Incidente incidente) {
        this.incidente = incidente;
    }

    public Postmortem incidente(Incidente incidente) {
        this.setIncidente(incidente);
        return this;
    }

    public Set<AccionCorrectiva> getAccions() {
        return this.accions;
    }

    public void setAccions(Set<AccionCorrectiva> accionCorrectivas) {
        if (this.accions != null) {
            this.accions.forEach(i -> i.setPostmortem(null));
        }
        if (accionCorrectivas != null) {
            accionCorrectivas.forEach(i -> i.setPostmortem(this));
        }
        this.accions = accionCorrectivas;
    }

    public Postmortem accions(Set<AccionCorrectiva> accionCorrectivas) {
        this.setAccions(accionCorrectivas);
        return this;
    }

    public Postmortem addAccion(AccionCorrectiva accionCorrectiva) {
        this.accions.add(accionCorrectiva);
        accionCorrectiva.setPostmortem(this);
        return this;
    }

    public Postmortem removeAccion(AccionCorrectiva accionCorrectiva) {
        this.accions.remove(accionCorrectiva);
        accionCorrectiva.setPostmortem(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Postmortem)) {
            return false;
        }
        return getId() != null && getId().equals(((Postmortem) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Postmortem{" +
            "id=" + getId() +
            ", titulo='" + getTitulo() + "'" +
            ", resumen='" + getResumen() + "'" +
            ", causaRaiz='" + getCausaRaiz() + "'" +
            ", lineaDeTiempo='" + getLineaDeTiempo() + "'" +
            ", leccionesAprendidas='" + getLeccionesAprendidas() + "'" +
            ", publicado='" + getPublicado() + "'" +
            ", publicadoEn='" + getPublicadoEn() + "'" +
            "}";
    }
}
