package ar.edu.um.isa.oncall.service.mapper;

import static ar.edu.um.isa.oncall.domain.RotacionAsserts.*;
import static ar.edu.um.isa.oncall.domain.RotacionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RotacionMapperTest {

    private RotacionMapper rotacionMapper;

    @BeforeEach
    void setUp() {
        rotacionMapper = new RotacionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getRotacionSample1();
        var actual = rotacionMapper.toEntity(rotacionMapper.toDto(expected));
        assertRotacionAllPropertiesEquals(expected, actual);
    }
}
