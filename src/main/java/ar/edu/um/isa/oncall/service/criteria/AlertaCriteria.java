package ar.edu.um.isa.oncall.service.criteria;

import ar.edu.um.isa.oncall.domain.enumeration.OrigenAlerta;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link ar.edu.um.isa.oncall.domain.Alerta} entity. This class is used
 * in {@link ar.edu.um.isa.oncall.web.rest.AlertaResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /alertas?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AlertaCriteria implements Serializable, Criteria {

    /**
     * Class for filtering OrigenAlerta
     */
    public static class OrigenAlertaFilter extends Filter<OrigenAlerta> {

        public OrigenAlertaFilter() {}

        public OrigenAlertaFilter(OrigenAlertaFilter filter) {
            super(filter);
        }

        @Override
        public OrigenAlertaFilter copy() {
            return new OrigenAlertaFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter fingerprint;

    private OrigenAlertaFilter origen;

    private StringFilter resumen;

    private StringFilter payload;

    private InstantFilter recibidaEn;

    private BooleanFilter procesada;

    private LongFilter servicioId;

    private LongFilter incidenteId;

    private Boolean distinct;

    public AlertaCriteria() {}

    public AlertaCriteria(AlertaCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.fingerprint = other.optionalFingerprint().map(StringFilter::copy).orElse(null);
        this.origen = other.optionalOrigen().map(OrigenAlertaFilter::copy).orElse(null);
        this.resumen = other.optionalResumen().map(StringFilter::copy).orElse(null);
        this.payload = other.optionalPayload().map(StringFilter::copy).orElse(null);
        this.recibidaEn = other.optionalRecibidaEn().map(InstantFilter::copy).orElse(null);
        this.procesada = other.optionalProcesada().map(BooleanFilter::copy).orElse(null);
        this.servicioId = other.optionalServicioId().map(LongFilter::copy).orElse(null);
        this.incidenteId = other.optionalIncidenteId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public AlertaCriteria copy() {
        return new AlertaCriteria(this);
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

    public StringFilter getFingerprint() {
        return fingerprint;
    }

    public Optional<StringFilter> optionalFingerprint() {
        return Optional.ofNullable(fingerprint);
    }

    public StringFilter fingerprint() {
        if (fingerprint == null) {
            setFingerprint(new StringFilter());
        }
        return fingerprint;
    }

    public void setFingerprint(StringFilter fingerprint) {
        this.fingerprint = fingerprint;
    }

    public OrigenAlertaFilter getOrigen() {
        return origen;
    }

    public Optional<OrigenAlertaFilter> optionalOrigen() {
        return Optional.ofNullable(origen);
    }

    public OrigenAlertaFilter origen() {
        if (origen == null) {
            setOrigen(new OrigenAlertaFilter());
        }
        return origen;
    }

    public void setOrigen(OrigenAlertaFilter origen) {
        this.origen = origen;
    }

    public StringFilter getResumen() {
        return resumen;
    }

    public Optional<StringFilter> optionalResumen() {
        return Optional.ofNullable(resumen);
    }

    public StringFilter resumen() {
        if (resumen == null) {
            setResumen(new StringFilter());
        }
        return resumen;
    }

    public void setResumen(StringFilter resumen) {
        this.resumen = resumen;
    }

    public StringFilter getPayload() {
        return payload;
    }

    public Optional<StringFilter> optionalPayload() {
        return Optional.ofNullable(payload);
    }

    public StringFilter payload() {
        if (payload == null) {
            setPayload(new StringFilter());
        }
        return payload;
    }

    public void setPayload(StringFilter payload) {
        this.payload = payload;
    }

    public InstantFilter getRecibidaEn() {
        return recibidaEn;
    }

    public Optional<InstantFilter> optionalRecibidaEn() {
        return Optional.ofNullable(recibidaEn);
    }

    public InstantFilter recibidaEn() {
        if (recibidaEn == null) {
            setRecibidaEn(new InstantFilter());
        }
        return recibidaEn;
    }

    public void setRecibidaEn(InstantFilter recibidaEn) {
        this.recibidaEn = recibidaEn;
    }

    public BooleanFilter getProcesada() {
        return procesada;
    }

    public Optional<BooleanFilter> optionalProcesada() {
        return Optional.ofNullable(procesada);
    }

    public BooleanFilter procesada() {
        if (procesada == null) {
            setProcesada(new BooleanFilter());
        }
        return procesada;
    }

    public void setProcesada(BooleanFilter procesada) {
        this.procesada = procesada;
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
        final AlertaCriteria that = (AlertaCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(fingerprint, that.fingerprint) &&
            Objects.equals(origen, that.origen) &&
            Objects.equals(resumen, that.resumen) &&
            Objects.equals(payload, that.payload) &&
            Objects.equals(recibidaEn, that.recibidaEn) &&
            Objects.equals(procesada, that.procesada) &&
            Objects.equals(servicioId, that.servicioId) &&
            Objects.equals(incidenteId, that.incidenteId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fingerprint, origen, resumen, payload, recibidaEn, procesada, servicioId, incidenteId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AlertaCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalFingerprint().map(f -> "fingerprint=" + f + ", ").orElse("") +
            optionalOrigen().map(f -> "origen=" + f + ", ").orElse("") +
            optionalResumen().map(f -> "resumen=" + f + ", ").orElse("") +
            optionalPayload().map(f -> "payload=" + f + ", ").orElse("") +
            optionalRecibidaEn().map(f -> "recibidaEn=" + f + ", ").orElse("") +
            optionalProcesada().map(f -> "procesada=" + f + ", ").orElse("") +
            optionalServicioId().map(f -> "servicioId=" + f + ", ").orElse("") +
            optionalIncidenteId().map(f -> "incidenteId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
