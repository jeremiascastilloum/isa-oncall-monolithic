package ar.edu.um.isa.oncall.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PasoEscalamientoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static PasoEscalamiento getPasoEscalamientoSample1() {
        return new PasoEscalamiento().id(1L).orden(1).esperaMinutos(1);
    }

    public static PasoEscalamiento getPasoEscalamientoSample2() {
        return new PasoEscalamiento().id(2L).orden(2).esperaMinutos(2);
    }

    public static PasoEscalamiento getPasoEscalamientoRandomSampleGenerator() {
        return new PasoEscalamiento()
            .id(longCount.incrementAndGet())
            .orden(intCount.incrementAndGet())
            .esperaMinutos(intCount.incrementAndGet());
    }
}
