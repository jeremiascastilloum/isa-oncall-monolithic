package ar.edu.um.isa.oncall.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class IncidenteTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static Incidente getIncidenteSample1() {
        return new Incidente().id(1L).titulo("titulo1").descripcion("descripcion1").usuariosAfectados(1);
    }

    public static Incidente getIncidenteSample2() {
        return new Incidente().id(2L).titulo("titulo2").descripcion("descripcion2").usuariosAfectados(2);
    }

    public static Incidente getIncidenteRandomSampleGenerator() {
        return new Incidente()
            .id(longCount.incrementAndGet())
            .titulo(UUID.randomUUID().toString())
            .descripcion(UUID.randomUUID().toString())
            .usuariosAfectados(intCount.incrementAndGet());
    }
}
