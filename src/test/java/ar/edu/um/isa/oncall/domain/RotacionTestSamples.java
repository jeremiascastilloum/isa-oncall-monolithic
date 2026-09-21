package ar.edu.um.isa.oncall.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class RotacionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static Rotacion getRotacionSample1() {
        return new Rotacion().id(1L).nombre("nombre1").zonaHoraria("zonaHoraria1");
    }

    public static Rotacion getRotacionSample2() {
        return new Rotacion().id(2L).nombre("nombre2").zonaHoraria("zonaHoraria2");
    }

    public static Rotacion getRotacionRandomSampleGenerator() {
        return new Rotacion()
            .id(longCount.incrementAndGet())
            .nombre(UUID.randomUUID().toString())
            .zonaHoraria(UUID.randomUUID().toString());
    }
}
