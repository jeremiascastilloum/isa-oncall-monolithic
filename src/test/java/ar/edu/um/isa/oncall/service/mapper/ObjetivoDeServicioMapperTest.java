package ar.edu.um.isa.oncall.service.mapper;

import static ar.edu.um.isa.oncall.domain.ObjetivoDeServicioAsserts.*;
import static ar.edu.um.isa.oncall.domain.ObjetivoDeServicioTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ObjetivoDeServicioMapperTest {

    private ObjetivoDeServicioMapper objetivoDeServicioMapper;

    @BeforeEach
    void setUp() {
        objetivoDeServicioMapper = new ObjetivoDeServicioMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getObjetivoDeServicioSample1();
        var actual = objetivoDeServicioMapper.toEntity(objetivoDeServicioMapper.toDto(expected));
        assertObjetivoDeServicioAllPropertiesEquals(expected, actual);
    }
}
