package ar.edu.um.isa.oncall.service.mapper;

import static ar.edu.um.isa.oncall.domain.ServicioAsserts.*;
import static ar.edu.um.isa.oncall.domain.ServicioTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServicioMapperTest {

    private ServicioMapper servicioMapper;

    @BeforeEach
    void setUp() {
        servicioMapper = new ServicioMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getServicioSample1();
        var actual = servicioMapper.toEntity(servicioMapper.toDto(expected));
        assertServicioAllPropertiesEquals(expected, actual);
    }
}
