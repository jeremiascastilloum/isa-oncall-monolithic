package ar.edu.um.isa.oncall.service.criteria;

import ar.edu.um.isa.oncall.domain.enumeration.Canal;
import ar.edu.um.isa.oncall.domain.enumeration.EstadoNotificacion;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link ar.edu.um.isa.oncall.domain.Notificacion} entity. This class is used
 * in {@link ar.edu.um.isa.oncall.web.rest.NotificacionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /notificacions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NotificacionCriteria implements Serializable, Criteria {

    /**
     * Class for filtering Canal
     */
    public static class CanalFilter extends Filter<Canal> {

        public CanalFilter() {}

        public CanalFilter(CanalFilter filter) {
            super(filter);
        }

        @Override
        public CanalFilter copy() {
            return new CanalFilter(this);
        }
    }

    /**
     * Class for filtering EstadoNotificacion
     */
    public static class EstadoNotificacionFilter extends Filter<EstadoNotificacion> {

        public EstadoNotificacionFilter() {}

        public EstadoNotificacionFilter(EstadoNotificacionFilter filter) {
            super(filter);
        }

        @Override
        public EstadoNotificacionFilter copy() {
            return new EstadoNotificacionFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private CanalFilter canal;

    private StringFilter destino;

    private EstadoNotificacionFilter estado;

    private InstantFilter enviadaEn;

    private IntegerFilter intentos;

    private StringFilter errorMensaje;

    private LongFilter incidenteId;

    private LongFilter destinatarioId;

    private Boolean distinct;

    public NotificacionCriteria() {}

    public NotificacionCriteria(NotificacionCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.canal = other.optionalCanal().map(CanalFilter::copy).orElse(null);
        this.destino = other.optionalDestino().map(StringFilter::copy).orElse(null);
        this.estado = other.optionalEstado().map(EstadoNotificacionFilter::copy).orElse(null);
        this.enviadaEn = other.optionalEnviadaEn().map(InstantFilter::copy).orElse(null);
        this.intentos = other.optionalIntentos().map(IntegerFilter::copy).orElse(null);
        this.errorMensaje = other.optionalErrorMensaje().map(StringFilter::copy).orElse(null);
        this.incidenteId = other.optionalIncidenteId().map(LongFilter::copy).orElse(null);
        this.destinatarioId = other.optionalDestinatarioId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public NotificacionCriteria copy() {
        return new NotificacionCriteria(this);
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

    public CanalFilter getCanal() {
        return canal;
    }

    public Optional<CanalFilter> optionalCanal() {
        return Optional.ofNullable(canal);
    }

    public CanalFilter canal() {
        if (canal == null) {
            setCanal(new CanalFilter());
        }
        return canal;
    }

    public void setCanal(CanalFilter canal) {
        this.canal = canal;
    }

    public StringFilter getDestino() {
        return destino;
    }

    public Optional<StringFilter> optionalDestino() {
        return Optional.ofNullable(destino);
    }

    public StringFilter destino() {
        if (destino == null) {
            setDestino(new StringFilter());
        }
        return destino;
    }

    public void setDestino(StringFilter destino) {
        this.destino = destino;
    }

    public EstadoNotificacionFilter getEstado() {
        return estado;
    }

    public Optional<EstadoNotificacionFilter> optionalEstado() {
        return Optional.ofNullable(estado);
    }

    public EstadoNotificacionFilter estado() {
        if (estado == null) {
            setEstado(new EstadoNotificacionFilter());
        }
        return estado;
    }

    public void setEstado(EstadoNotificacionFilter estado) {
        this.estado = estado;
    }

    public InstantFilter getEnviadaEn() {
        return enviadaEn;
    }

    public Optional<InstantFilter> optionalEnviadaEn() {
        return Optional.ofNullable(enviadaEn);
    }

    public InstantFilter enviadaEn() {
        if (enviadaEn == null) {
            setEnviadaEn(new InstantFilter());
        }
        return enviadaEn;
    }

    public void setEnviadaEn(InstantFilter enviadaEn) {
        this.enviadaEn = enviadaEn;
    }

    public IntegerFilter getIntentos() {
        return intentos;
    }

    public Optional<IntegerFilter> optionalIntentos() {
        return Optional.ofNullable(intentos);
    }

    public IntegerFilter intentos() {
        if (intentos == null) {
            setIntentos(new IntegerFilter());
        }
        return intentos;
    }

    public void setIntentos(IntegerFilter intentos) {
        this.intentos = intentos;
    }

    public StringFilter getErrorMensaje() {
        return errorMensaje;
    }

    public Optional<StringFilter> optionalErrorMensaje() {
        return Optional.ofNullable(errorMensaje);
    }

    public StringFilter errorMensaje() {
        if (errorMensaje == null) {
            setErrorMensaje(new StringFilter());
        }
        return errorMensaje;
    }

    public void setErrorMensaje(StringFilter errorMensaje) {
        this.errorMensaje = errorMensaje;
    }

    public LongFilter getIncidenteId() {
        return incidenteId;
    }

    public Optional<LongFilter> optionalIncidenteId() {
        return Optional.ofNullable(incidenteId);
    }

    public LongFilter incidenteId() {
        if (incidenteId == null) {
            setIncidenteId(new LongFilter());
        }
        return incidenteId;
    }

    public void setIncidenteId(LongFilter incidenteId) {
        this.incidenteId = incidenteId;
    }

    public LongFilter getDestinatarioId() {
        return destinatarioId;
    }

    public Optional<LongFilter> optionalDestinatarioId() {
        return Optional.ofNullable(destinatarioId);
    }

    public LongFilter destinatarioId() {
        if (destinatarioId == null) {
            setDestinatarioId(new LongFilter());
        }
        return destinatarioId;
    }

    public void setDestinatarioId(LongFilter destinatarioId) {
        this.destinatarioId = destinatarioId;
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
        final NotificacionCriteria that = (NotificacionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(canal, that.canal) &&
            Objects.equals(destino, that.destino) &&
            Objects.equals(estado, that.estado) &&
            Objects.equals(enviadaEn, that.enviadaEn) &&
            Objects.equals(intentos, that.intentos) &&
            Objects.equals(errorMensaje, that.errorMensaje) &&
            Objects.equals(incidenteId, that.incidenteId) &&
            Objects.equals(destinatarioId, that.destinatarioId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, canal, destino, estado, enviadaEn, intentos, errorMensaje, incidenteId, destinatarioId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NotificacionCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCanal().map(f -> "canal=" + f + ", ").orElse("") +
            optionalDestino().map(f -> "destino=" + f + ", ").orElse("") +
            optionalEstado().map(f -> "estado=" + f + ", ").orElse("") +
            optionalEnviadaEn().map(f -> "enviadaEn=" + f + ", ").orElse("") +
            optionalIntentos().map(f -> "intentos=" + f + ", ").orElse("") +
            optionalErrorMensaje().map(f -> "errorMensaje=" + f + ", ").orElse("") +
            optionalIncidenteId().map(f -> "incidenteId=" + f + ", ").orElse("") +
            optionalDestinatarioId().map(f -> "destinatarioId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
