package ar.edu.um.isa.oncall.service.mapper;

import ar.edu.um.isa.oncall.domain.PasoEscalamiento;
import ar.edu.um.isa.oncall.domain.PoliticaEscalamiento;
import ar.edu.um.isa.oncall.domain.Rotacion;
import ar.edu.um.isa.oncall.domain.User;
import ar.edu.um.isa.oncall.service.dto.PasoEscalamientoDTO;
import ar.edu.um.isa.oncall.service.dto.PoliticaEscalamientoDTO;
import ar.edu.um.isa.oncall.service.dto.RotacionDTO;
import ar.edu.um.isa.oncall.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PasoEscalamiento} and its DTO {@link PasoEscalamientoDTO}.
 */
@Mapper(componentModel = "spring")
public interface PasoEscalamientoMapper extends EntityMapper<PasoEscalamientoDTO, PasoEscalamiento> {
    @Mapping(target = "politica", source = "politica", qualifiedByName = "politicaEscalamientoNombre")
    @Mapping(target = "rotacion", source = "rotacion", qualifiedByName = "rotacionNombre")
    @Mapping(target = "destinatarioDirecto", source = "destinatarioDirecto", qualifiedByName = "userLogin")
    PasoEscalamientoDTO toDto(PasoEscalamiento s);

    @Named("politicaEscalamientoNombre")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombre", source = "nombre")
    PoliticaEscalamientoDTO toDtoPoliticaEscalamientoNombre(PoliticaEscalamiento politicaEscalamiento);

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
