package ar.edu.um.isa.oncall.service.criteria;

import ar.edu.um.isa.oncall.domain.enumeration.Criticidad;
import ar.edu.um.isa.oncall.domain.enumeration.Entorno;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link ar.edu.um.isa.oncall.domain.Servicio} entity. This class is used
 * in {@link ar.edu.um.isa.oncall.web.rest.ServicioResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /servicios?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ServicioCriteria implements Serializable, Criteria {

    /**
     * Class for filtering Criticidad
     */
    public static class CriticidadFilter extends Filter<Criticidad> {

        public CriticidadFilter() {}

        public CriticidadFilter(CriticidadFilter filter) {
            super(filter);
        }

        @Override
        public CriticidadFilter copy() {
            return new CriticidadFilter(this);
        }
    }

    /**
     * Class for filtering Entorno
     */
    public static class EntornoFilter extends Filter<Entorno> {

        public EntornoFilter() {}

        public EntornoFilter(EntornoFilter filter) {
            super(filter);
        }

        @Override
        public EntornoFilter copy() {
            return new EntornoFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter nombre;

    private StringFilter descripcion;

    private CriticidadFilter criticidad;

    private EntornoFilter entorno;

    private StringFilter repositorioUrl;

    private BooleanFilter activo;

    private LongFilter equipoId;

    private LongFilter objetivoId;

    private LongFilter alertaId;

    private LongFilter politicaId;

    private LongFilter incidenteId;

    private Boolean distinct;

    public ServicioCriteria() {}

    public ServicioCriteria(ServicioCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.nombre = other.optionalNombre().map(StringFilter::copy).orElse(null);
        this.descripcion = other.optionalDescripcion().map(StringFilter::copy).orElse(null);
        this.criticidad = other.optionalCriticidad().map(CriticidadFilter::copy).orElse(null);
        this.entorno = other.optionalEntorno().map(EntornoFilter::copy).orElse(null);
        this.repositorioUrl = other.optionalRepositorioUrl().map(StringFilter::copy).orElse(null);
        this.activo = other.optionalActivo().map(BooleanFilter::copy).orElse(null);
        this.equipoId = other.optionalEquipoId().map(LongFilter::copy).orElse(null);
        this.objetivoId = other.optionalObjetivoId().map(LongFilter::copy).orElse(null);
        this.alertaId = other.optionalAlertaId().map(LongFilter::copy).orElse(null);
        this.politicaId = other.optionalPoliticaId().map(LongFilter::copy).orElse(null);
        this.incidenteId = other.optionalIncidenteId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ServicioCriteria copy() {
        return new ServicioCriteria(this);
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

    public StringFilter getNombre() {
        return nombre;
    }

    public Optional<StringFilter> optionalNombre() {
        return Optional.ofNullable(nombre);
    }

    public StringFilter nombre() {
        if (nombre == null) {
            setNombre(new StringFilter());
        }
        return nombre;
    }

    public void setNombre(StringFilter nombre) {
        this.nombre = nombre;
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

    public CriticidadFilter getCriticidad() {
        return criticidad;
    }

    public Optional<CriticidadFilter> optionalCriticidad() {
        return Optional.ofNullable(criticidad);
    }

    public CriticidadFilter criticidad() {
        if (criticidad == null) {
            setCriticidad(new CriticidadFilter());
        }
        return criticidad;
    }

    public void setCriticidad(CriticidadFilter criticidad) {
        this.criticidad = criticidad;
    }

    public EntornoFilter getEntorno() {
        return entorno;
    }

    public Optional<EntornoFilter> optionalEntorno() {
        return Optional.ofNullable(entorno);
    }

    public EntornoFilter entorno() {
        if (entorno == null) {
            setEntorno(new EntornoFilter());
        }
        return entorno;
    }

    public void setEntorno(EntornoFilter entorno) {
        this.entorno = entorno;
    }

    public StringFilter getRepositorioUrl() {
        return repositorioUrl;
    }

    public Optional<StringFilter> optionalRepositorioUrl() {
        return Optional.ofNullable(repositorioUrl);
    }

    public StringFilter repositorioUrl() {
        if (repositorioUrl == null) {
            setRepositorioUrl(new StringFilter());
        }
        return repositorioUrl;
    }

    public void setRepositorioUrl(StringFilter repositorioUrl) {
        this.repositorioUrl = repositorioUrl;
    }

    public BooleanFilter getActivo() {
        return activo;
    }

    public Optional<BooleanFilter> optionalActivo() {
        return Optional.ofNullable(activo);
    }

    public BooleanFilter activo() {
        if (activo == null) {
            setActivo(new BooleanFilter());
        }
        return activo;
    }

    public void setActivo(BooleanFilter activo) {
        this.activo = activo;
    }

    public LongFilter getEquipoId() {
        return equipoId;
    }

    public Optional<LongFilter> optionalEquipoId() {
        return Optional.ofNullable(equipoId);
    }

    public LongFilter equipoId() {
        if (equipoId == null) {
            setEquipoId(new LongFilter());
        }
        return equipoId;
    }

    public void setEquipoId(LongFilter equipoId) {
        this.equipoId = equipoId;
    }

    public LongFilter getObjetivoId() {
        return objetivoId;
    }

    public Optional<LongFilter> optionalObjetivoId() {
        return Optional.ofNullable(objetivoId);
    }

    public LongFilter objetivoId() {
        if (objetivoId == null) {
            setObjetivoId(new LongFilter());
        }
        return objetivoId;
    }

    public void setObjetivoId(LongFilter objetivoId) {
        this.objetivoId = objetivoId;
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

    public LongFilter getPoliticaId() {
        return politicaId;
    }

    public Optional<LongFilter> optionalPoliticaId() {
        return Optional.ofNullable(politicaId);
    }

    public LongFilter politicaId() {
        if (politicaId == null) {
            setPoliticaId(new LongFilter());
        }
        return politicaId;
    }

    public void setPoliticaId(LongFilter politicaId) {
        this.politicaId = politicaId;
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
        final ServicioCriteria that = (ServicioCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(nombre, that.nombre) &&
            Objects.equals(descripcion, that.descripcion) &&
            Objects.equals(criticidad, that.criticidad) &&
            Objects.equals(entorno, that.entorno) &&
            Objects.equals(repositorioUrl, that.repositorioUrl) &&
            Objects.equals(activo, that.activo) &&
            Objects.equals(equipoId, that.equipoId) &&
            Objects.equals(objetivoId, that.objetivoId) &&
            Objects.equals(alertaId, that.alertaId) &&
            Objects.equals(politicaId, that.politicaId) &&
            Objects.equals(incidenteId, that.incidenteId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            nombre,
            descripcion,
            criticidad,
            entorno,
            repositorioUrl,
            activo,
            equipoId,
            objetivoId,
            alertaId,
            politicaId,
            incidenteId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ServicioCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalNombre().map(f -> "nombre=" + f + ", ").orElse("") +
            optionalDescripcion().map(f -> "descripcion=" + f + ", ").orElse("") +
            optionalCriticidad().map(f -> "criticidad=" + f + ", ").orElse("") +
            optionalEntorno().map(f -> "entorno=" + f + ", ").orElse("") +
            optionalRepositorioUrl().map(f -> "repositorioUrl=" + f + ", ").orElse("") +
            optionalActivo().map(f -> "activo=" + f + ", ").orElse("") +
            optionalEquipoId().map(f -> "equipoId=" + f + ", ").orElse("") +
            optionalObjetivoId().map(f -> "objetivoId=" + f + ", ").orElse("") +
            optionalAlertaId().map(f -> "alertaId=" + f + ", ").orElse("") +
            optionalPoliticaId().map(f -> "politicaId=" + f + ", ").orElse("") +
            optionalIncidenteId().map(f -> "incidenteId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
