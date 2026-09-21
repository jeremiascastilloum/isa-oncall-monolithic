package ar.edu.um.isa.oncall.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PoliticaEscalamientoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static PoliticaEscalamiento getPoliticaEscalamientoSample1() {
        return new PoliticaEscalamiento().id(1L).nombre("nombre1").descripcion("descripcion1").repetirVeces(1);
    }

    public static PoliticaEscalamiento getPoliticaEscalamientoSample2() {
        return new PoliticaEscalamiento().id(2L).nombre("nombre2").descripcion("descripcion2").repetirVeces(2);
    }

    public static PoliticaEscalamiento getPoliticaEscalamientoRandomSampleGenerator() {
        return new PoliticaEscalamiento()
            .id(longCount.incrementAndGet())
            .nombre(UUID.randomUUID().toString())
            .descripcion(UUID.randomUUID().toString())
            .repetirVeces(intCount.incrementAndGet());
    }
}
