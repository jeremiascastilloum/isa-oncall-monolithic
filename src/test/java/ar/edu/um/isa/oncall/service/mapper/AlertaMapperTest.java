package ar.edu.um.isa.oncall.service.mapper;

import static ar.edu.um.isa.oncall.domain.AlertaAsserts.*;
import static ar.edu.um.isa.oncall.domain.AlertaTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AlertaMapperTest {

    private AlertaMapper alertaMapper;

    @BeforeEach
    void setUp() {
        alertaMapper = new AlertaMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAlertaSample1();
        var actual = alertaMapper.toEntity(alertaMapper.toDto(expected));
        assertAlertaAllPropertiesEquals(expected, actual);
    }
}
