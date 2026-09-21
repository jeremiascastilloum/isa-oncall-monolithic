package ar.edu.um.isa.oncall.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PostmortemTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static Postmortem getPostmortemSample1() {
        return new Postmortem()
            .id(1L)
            .titulo("titulo1")
            .resumen("resumen1")
            .causaRaiz("causaRaiz1")
            .lineaDeTiempo("lineaDeTiempo1")
            .leccionesAprendidas("leccionesAprendidas1");
    }

    public static Postmortem getPostmortemSample2() {
        return new Postmortem()
            .id(2L)
            .titulo("titulo2")
            .resumen("resumen2")
            .causaRaiz("causaRaiz2")
            .lineaDeTiempo("lineaDeTiempo2")
            .leccionesAprendidas("leccionesAprendidas2");
    }

    public static Postmortem getPostmortemRandomSampleGenerator() {
        return new Postmortem()
            .id(longCount.incrementAndGet())
            .titulo(UUID.randomUUID().toString())
            .resumen(UUID.randomUUID().toString())
            .causaRaiz(UUID.randomUUID().toString())
            .lineaDeTiempo(UUID.randomUUID().toString())
            .leccionesAprendidas(UUID.randomUUID().toString());
    }
}
