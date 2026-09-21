package ar.edu.um.isa.oncall.service.mapper;

import ar.edu.um.isa.oncall.domain.Incidente;
import ar.edu.um.isa.oncall.domain.Notificacion;
import ar.edu.um.isa.oncall.domain.User;
import ar.edu.um.isa.oncall.service.dto.IncidenteDTO;
import ar.edu.um.isa.oncall.service.dto.NotificacionDTO;
import ar.edu.um.isa.oncall.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Notificacion} and its DTO {@link NotificacionDTO}.
 */
@Mapper(componentModel = "spring")
public interface NotificacionMapper extends EntityMapper<NotificacionDTO, Notificacion> {
    @Mapping(target = "incidente", source = "incidente", qualifiedByName = "incidenteTitulo")
    @Mapping(target = "destinatario", source = "destinatario", qualifiedByName = "userLogin")
    NotificacionDTO toDto(Notificacion s);

    @Named("incidenteTitulo")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "titulo", source = "titulo")
    IncidenteDTO toDtoIncidenteTitulo(Incidente incidente);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
