package ar.edu.um.isa.oncall.service.mapper;

import ar.edu.um.isa.oncall.domain.Equipo;
import ar.edu.um.isa.oncall.domain.Rotacion;
import ar.edu.um.isa.oncall.service.dto.EquipoDTO;
import ar.edu.um.isa.oncall.service.dto.RotacionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Rotacion} and its DTO {@link RotacionDTO}.
 */
@Mapper(componentModel = "spring")
public interface RotacionMapper extends EntityMapper<RotacionDTO, Rotacion> {
    @Mapping(target = "equipo", source = "equipo", qualifiedByName = "equipoNombre")
    RotacionDTO toDto(Rotacion s);

    @Named("equipoNombre")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombre", source = "nombre")
    EquipoDTO toDtoEquipoNombre(Equipo equipo);
}
