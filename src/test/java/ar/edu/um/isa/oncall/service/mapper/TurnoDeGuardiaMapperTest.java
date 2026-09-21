package ar.edu.um.isa.oncall.service.mapper;

import static ar.edu.um.isa.oncall.domain.TurnoDeGuardiaAsserts.*;
import static ar.edu.um.isa.oncall.domain.TurnoDeGuardiaTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TurnoDeGuardiaMapperTest {

    private TurnoDeGuardiaMapper turnoDeGuardiaMapper;

    @BeforeEach
    void setUp() {
        turnoDeGuardiaMapper = new TurnoDeGuardiaMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTurnoDeGuardiaSample1();
        var actual = turnoDeGuardiaMapper.toEntity(turnoDeGuardiaMapper.toDto(expected));
        assertTurnoDeGuardiaAllPropertiesEquals(expected, actual);
    }
}
