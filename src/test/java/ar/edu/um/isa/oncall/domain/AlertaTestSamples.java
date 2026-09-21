package ar.edu.um.isa.oncall.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AlertaTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static Alerta getAlertaSample1() {
        return new Alerta().id(1L).fingerprint("fingerprint1").resumen("resumen1").payload("payload1");
    }

    public static Alerta getAlertaSample2() {
        return new Alerta().id(2L).fingerprint("fingerprint2").resumen("resumen2").payload("payload2");
    }

    public static Alerta getAlertaRandomSampleGenerator() {
        return new Alerta()
            .id(longCount.incrementAndGet())
            .fingerprint(UUID.randomUUID().toString())
            .resumen(UUID.randomUUID().toString())
            .payload(UUID.randomUUID().toString());
    }
}
