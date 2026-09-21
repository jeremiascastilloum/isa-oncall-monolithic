package ar.edu.um.isa.oncall.service.mapper;

import ar.edu.um.isa.oncall.domain.Equipo;
import ar.edu.um.isa.oncall.service.dto.EquipoDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Equipo} and its DTO {@link EquipoDTO}.
 */
@Mapper(componentModel = "spring")
public interface EquipoMapper extends EntityMapper<EquipoDTO, Equipo> {}
