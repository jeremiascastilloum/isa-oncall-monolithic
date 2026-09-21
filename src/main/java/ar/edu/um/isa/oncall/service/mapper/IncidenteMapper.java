package ar.edu.um.isa.oncall.service.mapper;

import ar.edu.um.isa.oncall.domain.Incidente;
import ar.edu.um.isa.oncall.domain.Servicio;
import ar.edu.um.isa.oncall.domain.User;
import ar.edu.um.isa.oncall.service.dto.IncidenteDTO;
import ar.edu.um.isa.oncall.service.dto.ServicioDTO;
import ar.edu.um.isa.oncall.service.dto.UserDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Incidente} and its DTO {@link IncidenteDTO}.
 */
@Mapper(componentModel = "spring")
public interface IncidenteMapper extends EntityMapper<IncidenteDTO, Incidente> {
    @Mapping(target = "comandante", source = "comandante", qualifiedByName = "userLogin")
    @Mapping(target = "servicios", source = "servicios", qualifiedByName = "servicioNombreSet")
    IncidenteDTO toDto(Incidente s);

    @Mapping(target = "removeServicio", ignore = true)
    Incidente toEntity(IncidenteDTO incidenteDTO);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("servicioNombre")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombre", source = "nombre")
    ServicioDTO toDtoServicioNombre(Servicio servicio);

    @Named("servicioNombreSet")
    default Set<ServicioDTO> toDtoServicioNombreSet(Set<Servicio> servicio) {
        return servicio.stream().map(this::toDtoServicioNombre).collect(Collectors.toSet());
    }
}
