package ar.edu.um.isa.oncall.service.mapper;

import ar.edu.um.isa.oncall.domain.Incidente;
import ar.edu.um.isa.oncall.domain.Postmortem;
import ar.edu.um.isa.oncall.service.dto.IncidenteDTO;
import ar.edu.um.isa.oncall.service.dto.PostmortemDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Postmortem} and its DTO {@link PostmortemDTO}.
 */
@Mapper(componentModel = "spring")
public interface PostmortemMapper extends EntityMapper<PostmortemDTO, Postmortem> {
    @Mapping(target = "incidente", source = "incidente", qualifiedByName = "incidenteTitulo")
    PostmortemDTO toDto(Postmortem s);

    @Named("incidenteTitulo")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "titulo", source = "titulo")
    IncidenteDTO toDtoIncidenteTitulo(Incidente incidente);
}
