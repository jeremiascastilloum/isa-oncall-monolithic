package ar.edu.um.isa.oncall.service;

import java.io.Serial;

/**
 * La alerta entrante apunta a un servicio que no esta en el catalogo.
 *
 * Sin catalogo no hay a quien rutear: la alerta se rechaza en vez de abrir un incidente huerfano.
 */
public class ServicioInexistenteException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Long servicioId;

    public ServicioInexistenteException(Long servicioId) {
        super("No existe un servicio con id " + servicioId);
        this.servicioId = servicioId;
    }

    public Long getServicioId() {
        return servicioId;
    }
}
