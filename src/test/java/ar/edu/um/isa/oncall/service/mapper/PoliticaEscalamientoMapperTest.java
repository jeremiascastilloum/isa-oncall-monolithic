package ar.edu.um.isa.oncall.service.mapper;

import static ar.edu.um.isa.oncall.domain.PoliticaEscalamientoAsserts.*;
import static ar.edu.um.isa.oncall.domain.PoliticaEscalamientoTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PoliticaEscalamientoMapperTest {

    private PoliticaEscalamientoMapper politicaEscalamientoMapper;

    @BeforeEach
    void setUp() {
        politicaEscalamientoMapper = new PoliticaEscalamientoMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPoliticaEscalamientoSample1();
        var actual = politicaEscalamientoMapper.toEntity(politicaEscalamientoMapper.toDto(expected));
        assertPoliticaEscalamientoAllPropertiesEquals(expected, actual);
    }
}
