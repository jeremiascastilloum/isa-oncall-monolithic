package ar.edu.um.isa.oncall.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TurnoDeGuardiaTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static TurnoDeGuardia getTurnoDeGuardiaSample1() {
        return new TurnoDeGuardia().id(1L).nota("nota1");
    }

    public static TurnoDeGuardia getTurnoDeGuardiaSample2() {
        return new TurnoDeGuardia().id(2L).nota("nota2");
    }

    public static TurnoDeGuardia getTurnoDeGuardiaRandomSampleGenerator() {
        return new TurnoDeGuardia().id(longCount.incrementAndGet()).nota(UUID.randomUUID().toString());
    }
}
