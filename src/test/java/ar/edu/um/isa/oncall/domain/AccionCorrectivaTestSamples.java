package ar.edu.um.isa.oncall.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AccionCorrectivaTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static AccionCorrectiva getAccionCorrectivaSample1() {
        return new AccionCorrectiva().id(1L).descripcion("descripcion1").ticketUrl("ticketUrl1");
    }

    public static AccionCorrectiva getAccionCorrectivaSample2() {
        return new AccionCorrectiva().id(2L).descripcion("descripcion2").ticketUrl("ticketUrl2");
    }

    public static AccionCorrectiva getAccionCorrectivaRandomSampleGenerator() {
        return new AccionCorrectiva()
            .id(longCount.incrementAndGet())
            .descripcion(UUID.randomUUID().toString())
            .ticketUrl(UUID.randomUUID().toString());
    }
}
