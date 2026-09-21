package ar.edu.um.isa.oncall.service.mapper;

import static ar.edu.um.isa.oncall.domain.NotificacionAsserts.*;
import static ar.edu.um.isa.oncall.domain.NotificacionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NotificacionMapperTest {

    private NotificacionMapper notificacionMapper;

    @BeforeEach
    void setUp() {
        notificacionMapper = new NotificacionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getNotificacionSample1();
        var actual = notificacionMapper.toEntity(notificacionMapper.toDto(expected));
        assertNotificacionAllPropertiesEquals(expected, actual);
    }
}
