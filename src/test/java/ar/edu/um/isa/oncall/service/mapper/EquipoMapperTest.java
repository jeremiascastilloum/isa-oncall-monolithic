package ar.edu.um.isa.oncall.service.mapper;

import static ar.edu.um.isa.oncall.domain.EquipoAsserts.*;
import static ar.edu.um.isa.oncall.domain.EquipoTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EquipoMapperTest {

    private EquipoMapper equipoMapper;

    @BeforeEach
    void setUp() {
        equipoMapper = new EquipoMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getEquipoSample1();
        var actual = equipoMapper.toEntity(equipoMapper.toDto(expected));
        assertEquipoAllPropertiesEquals(expected, actual);
    }
}
