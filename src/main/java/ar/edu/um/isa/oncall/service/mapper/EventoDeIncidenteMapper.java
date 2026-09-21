package ar.edu.um.isa.oncall.service.mapper;

import ar.edu.um.isa.oncall.domain.EventoDeIncidente;
import ar.edu.um.isa.oncall.domain.Incidente;
import ar.edu.um.isa.oncall.service.dto.EventoDeIncidenteDTO;
import ar.edu.um.isa.oncall.service.dto.IncidenteDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link EventoDeIncidente} and its DTO {@link EventoDeIncidenteDTO}.
 */
@Mapper(componentModel = "spring")
public interface EventoDeIncidenteMapper extends EntityMapper<EventoDeIncidenteDTO, EventoDeIncidente> {
    @Mapping(target = "incidente", source = "incidente", qualifiedByName = "incidenteTitulo")
    EventoDeIncidenteDTO toDto(EventoDeIncidente s);

    @Named("incidenteTitulo")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "titulo", source = "titulo")
    IncidenteDTO toDtoIncidenteTitulo(Incidente incidente);
}
