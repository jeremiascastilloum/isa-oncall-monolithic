package ar.edu.um.isa.oncall.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ObjetivoDeServicioTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static ObjetivoDeServicio getObjetivoDeServicioSample1() {
        return new ObjetivoDeServicio().id(1L).minutosObjetivo(1).descripcion("descripcion1");
    }

    public static ObjetivoDeServicio getObjetivoDeServicioSample2() {
        return new ObjetivoDeServicio().id(2L).minutosObjetivo(2).descripcion("descripcion2");
    }

    public static ObjetivoDeServicio getObjetivoDeServicioRandomSampleGenerator() {
        return new ObjetivoDeServicio()
            .id(longCount.incrementAndGet())
            .minutosObjetivo(intCount.incrementAndGet())
            .descripcion(UUID.randomUUID().toString());
    }
}
