package ar.edu.um.isa.oncall.service.mapper;

import static ar.edu.um.isa.oncall.domain.IncidenteAsserts.*;
import static ar.edu.um.isa.oncall.domain.IncidenteTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IncidenteMapperTest {

    private IncidenteMapper incidenteMapper;

    @BeforeEach
    void setUp() {
        incidenteMapper = new IncidenteMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getIncidenteSample1();
        var actual = incidenteMapper.toEntity(incidenteMapper.toDto(expected));
        assertIncidenteAllPropertiesEquals(expected, actual);
    }
}
