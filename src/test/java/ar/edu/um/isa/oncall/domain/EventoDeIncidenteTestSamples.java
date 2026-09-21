package ar.edu.um.isa.oncall.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class EventoDeIncidenteTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static EventoDeIncidente getEventoDeIncidenteSample1() {
        return new EventoDeIncidente().id(1L).detalle("detalle1");
    }

    public static EventoDeIncidente getEventoDeIncidenteSample2() {
        return new EventoDeIncidente().id(2L).detalle("detalle2");
    }

    public static EventoDeIncidente getEventoDeIncidenteRandomSampleGenerator() {
        return new EventoDeIncidente().id(longCount.incrementAndGet()).detalle(UUID.randomUUID().toString());
    }
}
