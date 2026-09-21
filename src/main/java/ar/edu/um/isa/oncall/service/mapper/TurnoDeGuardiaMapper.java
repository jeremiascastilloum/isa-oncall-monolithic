package ar.edu.um.isa.oncall.service.mapper;

import ar.edu.um.isa.oncall.domain.Rotacion;
import ar.edu.um.isa.oncall.domain.TurnoDeGuardia;
import ar.edu.um.isa.oncall.domain.User;
import ar.edu.um.isa.oncall.service.dto.RotacionDTO;
import ar.edu.um.isa.oncall.service.dto.TurnoDeGuardiaDTO;
import ar.edu.um.isa.oncall.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TurnoDeGuardia} and its DTO {@link TurnoDeGuardiaDTO}.
 */
@Mapper(componentModel = "spring")
public interface TurnoDeGuardiaMapper extends EntityMapper<TurnoDeGuardiaDTO, TurnoDeGuardia> {
    @Mapping(target = "rotacion", source = "rotacion", qualifiedByName = "rotacionNombre")
    @Mapping(target = "responsable", source = "responsable", qualifiedByName = "userLogin")
    TurnoDeGuardiaDTO toDto(TurnoDeGuardia s);

    @Named("rotacionNombre")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombre", source = "nombre")
    RotacionDTO toDtoRotacionNombre(Rotacion rotacion);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
