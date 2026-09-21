package ar.edu.um.isa.oncall.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class NotificacionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static Notificacion getNotificacionSample1() {
        return new Notificacion().id(1L).destino("destino1").intentos(1).errorMensaje("errorMensaje1");
    }

    public static Notificacion getNotificacionSample2() {
        return new Notificacion().id(2L).destino("destino2").intentos(2).errorMensaje("errorMensaje2");
    }

    public static Notificacion getNotificacionRandomSampleGenerator() {
        return new Notificacion()
            .id(longCount.incrementAndGet())
            .destino(UUID.randomUUID().toString())
            .intentos(intCount.incrementAndGet())
            .errorMensaje(UUID.randomUUID().toString());
    }
}
