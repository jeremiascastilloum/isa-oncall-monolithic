package ar.edu.um.isa.oncall.service.mapper;

import ar.edu.um.isa.oncall.domain.AccionCorrectiva;
import ar.edu.um.isa.oncall.domain.Postmortem;
import ar.edu.um.isa.oncall.domain.User;
import ar.edu.um.isa.oncall.service.dto.AccionCorrectivaDTO;
import ar.edu.um.isa.oncall.service.dto.PostmortemDTO;
import ar.edu.um.isa.oncall.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AccionCorrectiva} and its DTO {@link AccionCorrectivaDTO}.
 */
@Mapper(componentModel = "spring")
public interface AccionCorrectivaMapper extends EntityMapper<AccionCorrectivaDTO, AccionCorrectiva> {
    @Mapping(target = "postmortem", source = "postmortem", qualifiedByName = "postmortemTitulo")
    @Mapping(target = "responsable", source = "responsable", qualifiedByName = "userLogin")
    AccionCorrectivaDTO toDto(AccionCorrectiva s);

    @Named("postmortemTitulo")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "titulo", source = "titulo")
    PostmortemDTO toDtoPostmortemTitulo(Postmortem postmortem);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
