package ar.edu.um.isa.oncall.service.mapper;

import ar.edu.um.isa.oncall.domain.PoliticaEscalamiento;
import ar.edu.um.isa.oncall.domain.Servicio;
import ar.edu.um.isa.oncall.service.dto.PoliticaEscalamientoDTO;
import ar.edu.um.isa.oncall.service.dto.ServicioDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PoliticaEscalamiento} and its DTO {@link PoliticaEscalamientoDTO}.
 */
@Mapper(componentModel = "spring")
public interface PoliticaEscalamientoMapper extends EntityMapper<PoliticaEscalamientoDTO, PoliticaEscalamiento> {
    @Mapping(target = "servicio", source = "servicio", qualifiedByName = "servicioNombre")
    PoliticaEscalamientoDTO toDto(PoliticaEscalamiento s);

    @Named("servicioNombre")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombre", source = "nombre")
    ServicioDTO toDtoServicioNombre(Servicio servicio);
}
