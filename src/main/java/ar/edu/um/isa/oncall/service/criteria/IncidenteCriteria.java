package ar.edu.um.isa.oncall.service.criteria;

import ar.edu.um.isa.oncall.domain.enumeration.EstadoIncidente;
import ar.edu.um.isa.oncall.domain.enumeration.Severidad;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link ar.edu.um.isa.oncall.domain.Incidente} entity. This class is used
 * in {@link ar.edu.um.isa.oncall.web.rest.IncidenteResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /incidentes?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class IncidenteCriteria implements Serializable, Criteria {

    /**
     * Class for filtering Severidad
     */
    public static class SeveridadFilter extends Filter<Severidad> {

        public SeveridadFilter() {}

        public SeveridadFilter(SeveridadFilter filter) {
            super(filter);
        }

        @Override
        public SeveridadFilter copy() {
            return new SeveridadFilter(this);
        }
    }

    /**
     * Class for filtering EstadoIncidente
     */
    public static class EstadoIncidenteFilter extends Filter<EstadoIncidente> {

        public EstadoIncidenteFilter() {}

        public EstadoIncidenteFilter(EstadoIncidenteFilter filter) {
            super(filter);
        }

        @Override
        public EstadoIncidenteFilter copy() {
            return new EstadoIncidenteFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter titulo;

    private StringFilter descripcion;

    private SeveridadFilter severidad;

    private EstadoIncidenteFilter estado;

    private InstantFilter detectadoEn;

    private InstantFilter reconocidoEn;

    private InstantFilter mitigadoEn;

    private InstantFilter resueltoEn;

    private IntegerFilter usuariosAfectados;

    private BooleanFilter cumplioObjetivo;

    private LongFilter comandanteId;

    private LongFilter servicioId;

    private LongFilter postmortemId;

    private LongFilter alertaId;

    private LongFilter eventoId;

    private LongFilter notificacionId;

    private Boolean distinct;

    public IncidenteCriteria() {}

    public IncidenteCriteria(IncidenteCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.titulo = other.optionalTitulo().map(StringFilter::copy).orElse(null);
        this.descripcion = other.optionalDescripcion().map(StringFilter::copy).orElse(null);
        this.severidad = other.optionalSeveridad().map(SeveridadFilter::copy).orElse(null);
        this.estado = other.optionalEstado().map(EstadoIncidenteFilter::copy).orElse(null);
        this.detectadoEn = other.optionalDetectadoEn().map(InstantFilter::copy).orElse(null);
        this.reconocidoEn = other.optionalReconocidoEn().map(InstantFilter::copy).orElse(null);
        this.mitigadoEn = other.optionalMitigadoEn().map(InstantFilter::copy).orElse(null);
        this.resueltoEn = other.optionalResueltoEn().map(InstantFilter::copy).orElse(null);
        this.usuariosAfectados = other.optionalUsuariosAfectados().map(IntegerFilter::copy).orElse(null);
        this.cumplioObjetivo = other.optionalCumplioObjetivo().map(BooleanFilter::copy).orElse(null);
        this.comandanteId = other.optionalComandanteId().map(LongFilter::copy).orElse(null);
        this.servicioId = other.optionalServicioId().map(LongFilter::copy).orElse(null);
        this.postmortemId = other.optionalPostmortemId().map(LongFilter::copy).orElse(null);
        this.alertaId = other.optionalAlertaId().map(LongFilter::copy).orElse(null);
        this.eventoId = other.optionalEventoId().map(LongFilter::copy).orElse(null);
        this.notificacionId = other.optionalNotificacionId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public IncidenteCriteria copy() {
        return new IncidenteCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getTitulo() {
        return titulo;
    }

    public Optional<StringFilter> optionalTitulo() {
        return Optional.ofNullable(titulo);
    }

    public StringFilter titulo() {
        if (titulo == null) {
            setTitulo(new StringFilter());
        }
        return titulo;
    }

    public void setTitulo(StringFilter titulo) {
        this.titulo = titulo;
    }

    public StringFilter getDescripcion() {
        return descripcion;
    }

    public Optional<StringFilter> optionalDescripcion() {
        return Optional.ofNullable(descripcion);
    }

    public StringFilter descripcion() {
        if (descripcion == null) {
            setDescripcion(new StringFilter());
        }
        return descripcion;
    }

    public void setDescripcion(StringFilter descripcion) {
        this.descripcion = descripcion;
    }

    public SeveridadFilter getSeveridad() {
        return severidad;
    }

    public Optional<SeveridadFilter> optionalSeveridad() {
        return Optional.ofNullable(severidad);
    }

    public SeveridadFilter severidad() {
        if (severidad == null) {
            setSeveridad(new SeveridadFilter());
        }
        return severidad;
    }

    public void setSeveridad(SeveridadFilter severidad) {
        this.severidad = severidad;
    }

    public EstadoIncidenteFilter getEstado() {
        return estado;
    }

    public Optional<EstadoIncidenteFilter> optionalEstado() {
        return Optional.ofNullable(estado);
    }

    public EstadoIncidenteFilter estado() {
        if (estado == null) {
            setEstado(new EstadoIncidenteFilter());
        }
        return estado;
    }

    public void setEstado(EstadoIncidenteFilter estado) {
        this.estado = estado;
    }

    public InstantFilter getDetectadoEn() {
        return detectadoEn;
    }

    public Optional<InstantFilter> optionalDetectadoEn() {
        return Optional.ofNullable(detectadoEn);
    }

    public InstantFilter detectadoEn() {
        if (detectadoEn == null) {
            setDetectadoEn(new InstantFilter());
        }
        return detectadoEn;
    }

    public void setDetectadoEn(InstantFilter detectadoEn) {
        this.detectadoEn = detectadoEn;
    }

    public InstantFilter getReconocidoEn() {
        return reconocidoEn;
    }

    public Optional<InstantFilter> optionalReconocidoEn() {
        return Optional.ofNullable(reconocidoEn);
    }

    public InstantFilter reconocidoEn() {
        if (reconocidoEn == null) {
            setReconocidoEn(new InstantFilter());
        }
        return reconocidoEn;
    }

    public void setReconocidoEn(InstantFilter reconocidoEn) {
        this.reconocidoEn = reconocidoEn;
    }

    public InstantFilter getMitigadoEn() {
        return mitigadoEn;
    }

    public Optional<InstantFilter> optionalMitigadoEn() {
        return Optional.ofNullable(mitigadoEn);
    }

    public InstantFilter mitigadoEn() {
        if (mitigadoEn == null) {
            setMitigadoEn(new InstantFilter());
        }
        return mitigadoEn;
    }

    public void setMitigadoEn(InstantFilter mitigadoEn) {
        this.mitigadoEn = mitigadoEn;
    }

    public InstantFilter getResueltoEn() {
        return resueltoEn;
    }

    public Optional<InstantFilter> optionalResueltoEn() {
        return Optional.ofNullable(resueltoEn);
    }

    public InstantFilter resueltoEn() {
        if (resueltoEn == null) {
            setResueltoEn(new InstantFilter());
        }
        return resueltoEn;
    }

    public void setResueltoEn(InstantFilter resueltoEn) {
        this.resueltoEn = resueltoEn;
    }

    public IntegerFilter getUsuariosAfectados() {
        return usuariosAfectados;
    }

    public Optional<IntegerFilter> optionalUsuariosAfectados() {
        return Optional.ofNullable(usuariosAfectados);
    }

    public IntegerFilter usuariosAfectados() {
        if (usuariosAfectados == null) {
            setUsuariosAfectados(new IntegerFilter());
        }
        return usuariosAfectados;
    }

    public void setUsuariosAfectados(IntegerFilter usuariosAfectados) {
        this.usuariosAfectados = usuariosAfectados;
    }

    public BooleanFilter getCumplioObjetivo() {
        return cumplioObjetivo;
    }

    public Optional<BooleanFilter> optionalCumplioObjetivo() {
        return Optional.ofNullable(cumplioObjetivo);
    }

    public BooleanFilter cumplioObjetivo() {
        if (cumplioObjetivo == null) {
            setCumplioObjetivo(new BooleanFilter());
        }
        return cumplioObjetivo;
    }

    public void setCumplioObjetivo(BooleanFilter cumplioObjetivo) {
        this.cumplioObjetivo = cumplioObjetivo;
    }

    public LongFilter getComandanteId() {
        return comandanteId;
    }

    public Optional<LongFilter> optionalComandanteId() {
        return Optional.ofNullable(comandanteId);
    }

    public LongFilter comandanteId() {
        if (comandanteId == null) {
            setComandanteId(new LongFilter());
        }
        return comandanteId;
    }

    public void setComandanteId(LongFilter comandanteId) {
        this.comandanteId = comandanteId;
    }

    public LongFilter getServicioId() {
        return servicioId;
    }

    public Optional<LongFilter> optionalServicioId() {
        return Optional.ofNullable(servicioId);
    }

    public LongFilter servicioId() {
        if (servicioId == null) {
            setServicioId(new LongFilter());
        }
        return servicioId;
    }

    public void setServicioId(LongFilter servicioId) {
        this.servicioId = servicioId;
    }

    public LongFilter getPostmortemId() {
        return postmortemId;
    }

    public Optional<LongFilter> optionalPostmortemId() {
        return Optional.ofNullable(postmortemId);
    }

    public LongFilter postmortemId() {
        if (postmortemId == null) {
            setPostmortemId(new LongFilter());
        }
        return postmortemId;
    }

    public void setPostmortemId(LongFilter postmortemId) {
        this.postmortemId = postmortemId;
    }

    public LongFilter getAlertaId() {
        return alertaId;
    }

    public Optional<LongFilter> optionalAlertaId() {
        return Optional.ofNullable(alertaId);
    }

    public LongFilter alertaId() {
        if (alertaId == null) {
            setAlertaId(new LongFilter());
        }
        return alertaId;
    }

    public void setAlertaId(LongFilter alertaId) {
        this.alertaId = alertaId;
    }

    public LongFilter getEventoId() {
        return eventoId;
    }

    public Optional<LongFilter> optionalEventoId() {
        return Optional.ofNullable(eventoId);
    }

    public LongFilter eventoId() {
        if (eventoId == null) {
            setEventoId(new LongFilter());
        }
        return eventoId;
    }

    public void setEventoId(LongFilter eventoId) {
        this.eventoId = eventoId;
    }

    public LongFilter getNotificacionId() {
        return notificacionId;
    }

    public Optional<LongFilter> optionalNotificacionId() {
        return Optional.ofNullable(notificacionId);
    }

    public LongFilter notificacionId() {
        if (notificacionId == null) {
            setNotificacionId(new LongFilter());
        }
        return notificacionId;
    }

    public void setNotificacionId(LongFilter notificacionId) {
        this.notificacionId = notificacionId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final IncidenteCriteria that = (IncidenteCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(titulo, that.titulo) &&
            Objects.equals(descripcion, that.descripcion) &&
            Objects.equals(severidad, that.severidad) &&
            Objects.equals(estado, that.estado) &&
            Objects.equals(detectadoEn, that.detectadoEn) &&
            Objects.equals(reconocidoEn, that.reconocidoEn) &&
            Objects.equals(mitigadoEn, that.mitigadoEn) &&
            Objects.equals(resueltoEn, that.resueltoEn) &&
            Objects.equals(usuariosAfectados, that.usuariosAfectados) &&
            Objects.equals(cumplioObjetivo, that.cumplioObjetivo) &&
            Objects.equals(comandanteId, that.comandanteId) &&
            Objects.equals(servicioId, that.servicioId) &&
            Objects.equals(postmortemId, that.postmortemId) &&
            Objects.equals(alertaId, that.alertaId) &&
            Objects.equals(eventoId, that.eventoId) &&
            Objects.equals(notificacionId, that.notificacionId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            titulo,
            descripcion,
            severidad,
            estado,
            detectadoEn,
            reconocidoEn,
            mitigadoEn,
            resueltoEn,
            usuariosAfectados,
            cumplioObjetivo,
            comandanteId,
            servicioId,
            postmortemId,
            alertaId,
            eventoId,
            notificacionId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "IncidenteCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalTitulo().map(f -> "titulo=" + f + ", ").orElse("") +
            optionalDescripcion().map(f -> "descripcion=" + f + ", ").orElse("") +
            optionalSeveridad().map(f -> "severidad=" + f + ", ").orElse("") +
            optionalEstado().map(f -> "estado=" + f + ", ").orElse("") +
            optionalDetectadoEn().map(f -> "detectadoEn=" + f + ", ").orElse("") +
            optionalReconocidoEn().map(f -> "reconocidoEn=" + f + ", ").orElse("") +
            optionalMitigadoEn().map(f -> "mitigadoEn=" + f + ", ").orElse("") +
            optionalResueltoEn().map(f -> "resueltoEn=" + f + ", ").orElse("") +
            optionalUsuariosAfectados().map(f -> "usuariosAfectados=" + f + ", ").orElse("") +
            optionalCumplioObjetivo().map(f -> "cumplioObjetivo=" + f + ", ").orElse("") +
            optionalComandanteId().map(f -> "comandanteId=" + f + ", ").orElse("") +
            optionalServicioId().map(f -> "servicioId=" + f + ", ").orElse("") +
            optionalPostmortemId().map(f -> "postmortemId=" + f + ", ").orElse("") +
            optionalAlertaId().map(f -> "alertaId=" + f + ", ").orElse("") +
            optionalEventoId().map(f -> "eventoId=" + f + ", ").orElse("") +
            optionalNotificacionId().map(f -> "notificacionId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
