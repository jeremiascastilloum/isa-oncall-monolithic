package ar.edu.um.isa.oncall.service.mapper;

import ar.edu.um.isa.oncall.domain.ObjetivoDeServicio;
import ar.edu.um.isa.oncall.domain.Servicio;
import ar.edu.um.isa.oncall.service.dto.ObjetivoDeServicioDTO;
import ar.edu.um.isa.oncall.service.dto.ServicioDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ObjetivoDeServicio} and its DTO {@link ObjetivoDeServicioDTO}.
 */
@Mapper(componentModel = "spring")
public interface ObjetivoDeServicioMapper extends EntityMapper<ObjetivoDeServicioDTO, ObjetivoDeServicio> {
    @Mapping(target = "servicio", source = "servicio", qualifiedByName = "servicioNombre")
    ObjetivoDeServicioDTO toDto(ObjetivoDeServicio s);

    @Named("servicioNombre")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombre", source = "nombre")
    ServicioDTO toDtoServicioNombre(Servicio servicio);
}
