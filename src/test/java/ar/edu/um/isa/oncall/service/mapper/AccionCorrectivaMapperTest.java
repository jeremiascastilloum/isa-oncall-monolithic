package ar.edu.um.isa.oncall.service.mapper;

import static ar.edu.um.isa.oncall.domain.AccionCorrectivaAsserts.*;
import static ar.edu.um.isa.oncall.domain.AccionCorrectivaTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AccionCorrectivaMapperTest {

    private AccionCorrectivaMapper accionCorrectivaMapper;

    @BeforeEach
    void setUp() {
        accionCorrectivaMapper = new AccionCorrectivaMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAccionCorrectivaSample1();
        var actual = accionCorrectivaMapper.toEntity(accionCorrectivaMapper.toDto(expected));
        assertAccionCorrectivaAllPropertiesEquals(expected, actual);
    }
}
