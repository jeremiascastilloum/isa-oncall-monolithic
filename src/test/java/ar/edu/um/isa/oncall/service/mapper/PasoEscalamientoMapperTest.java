package ar.edu.um.isa.oncall.service.mapper;

import static ar.edu.um.isa.oncall.domain.PasoEscalamientoAsserts.*;
import static ar.edu.um.isa.oncall.domain.PasoEscalamientoTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PasoEscalamientoMapperTest {

    private PasoEscalamientoMapper pasoEscalamientoMapper;

    @BeforeEach
    void setUp() {
        pasoEscalamientoMapper = new PasoEscalamientoMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPasoEscalamientoSample1();
        var actual = pasoEscalamientoMapper.toEntity(pasoEscalamientoMapper.toDto(expected));
        assertPasoEscalamientoAllPropertiesEquals(expected, actual);
    }
}
