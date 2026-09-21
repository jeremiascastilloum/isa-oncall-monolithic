package ar.edu.um.isa.oncall.service.mapper;

import ar.edu.um.isa.oncall.domain.Alerta;
import ar.edu.um.isa.oncall.domain.Incidente;
import ar.edu.um.isa.oncall.domain.Servicio;
import ar.edu.um.isa.oncall.service.dto.AlertaDTO;
import ar.edu.um.isa.oncall.service.dto.IncidenteDTO;
import ar.edu.um.isa.oncall.service.dto.ServicioDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Alerta} and its DTO {@link AlertaDTO}.
 */
@Mapper(componentModel = "spring")
public interface AlertaMapper extends EntityMapper<AlertaDTO, Alerta> {
    @Mapping(target = "servicio", source = "servicio", qualifiedByName = "servicioNombre")
    @Mapping(target = "incidente", source = "incidente", qualifiedByName = "incidenteTitulo")
    AlertaDTO toDto(Alerta s);

    @Named("servicioNombre")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombre", source = "nombre")
    ServicioDTO toDtoServicioNombre(Servicio servicio);

    @Named("incidenteTitulo")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "titulo", source = "titulo")
    IncidenteDTO toDtoIncidenteTitulo(Incidente incidente);
}
