package ar.edu.um.isa.oncall.service.mapper;

import static ar.edu.um.isa.oncall.domain.EventoDeIncidenteAsserts.*;
import static ar.edu.um.isa.oncall.domain.EventoDeIncidenteTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EventoDeIncidenteMapperTest {

    private EventoDeIncidenteMapper eventoDeIncidenteMapper;

    @BeforeEach
    void setUp() {
        eventoDeIncidenteMapper = new EventoDeIncidenteMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getEventoDeIncidenteSample1();
        var actual = eventoDeIncidenteMapper.toEntity(eventoDeIncidenteMapper.toDto(expected));
        assertEventoDeIncidenteAllPropertiesEquals(expected, actual);
    }
}
