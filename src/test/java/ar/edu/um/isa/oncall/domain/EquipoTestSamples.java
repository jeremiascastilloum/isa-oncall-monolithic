package ar.edu.um.isa.oncall.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class EquipoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static Equipo getEquipoSample1() {
        return new Equipo().id(1L).nombre("nombre1").emailContacto("emailContacto1").canalChat("canalChat1");
    }

    public static Equipo getEquipoSample2() {
        return new Equipo().id(2L).nombre("nombre2").emailContacto("emailContacto2").canalChat("canalChat2");
    }

    public static Equipo getEquipoRandomSampleGenerator() {
        return new Equipo()
            .id(longCount.incrementAndGet())
            .nombre(UUID.randomUUID().toString())
            .emailContacto(UUID.randomUUID().toString())
            .canalChat(UUID.randomUUID().toString());
    }
}
