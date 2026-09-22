package ar.edu.um.isa.oncall.web.rest;

import ar.edu.um.isa.oncall.service.DeduplicacionDeAlertasService;
import ar.edu.um.isa.oncall.service.ServicioInexistenteException;
import ar.edu.um.isa.oncall.service.dto.AlertaEntranteDTO;
import ar.edu.um.isa.oncall.service.dto.ResultadoDeduplicacionDTO;
import ar.edu.um.isa.oncall.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Punto de entrada de las alertas crudas del monitoreo.
 *
 * <p>Es deliberadamente un controlador aparte de {@link AlertaResource}: ese lo escribe el
 * generador y el ABM que expone tiene que seguir siendo un ABM. Crear una alerta por
 * {@code POST /api/alertas} sigue creando una fila y nada mas; la regla de negocio vive en
 * {@code POST /api/alertas/ingesta}, que es lo que apunta el webhook de Prometheus o Datadog.</p>
 */
@RestController
@RequestMapping("/api/alertas")
public class IngestaDeAlertasResource {

    private static final Logger LOG = LoggerFactory.getLogger(IngestaDeAlertasResource.class);

    private static final String ENTITY_NAME = "alerta";

    private final DeduplicacionDeAlertasService deduplicacionDeAlertasService;

    public IngestaDeAlertasResource(DeduplicacionDeAlertasService deduplicacionDeAlertasService) {
        this.deduplicacionDeAlertasService = deduplicacionDeAlertasService;
    }

    /**
     * {@code POST /api/alertas/ingesta} : recibe una alerta cruda y le aplica la regla de
     * deduplicacion por fingerprint.
     *
     * @param alertaEntranteDTO la senal que manda la herramienta de monitoreo.
     * @return {@link ResponseEntity} con estado {@code 201 (Created)} y el resultado de la regla:
     *         si la alerta abrio un incidente nuevo o si se absorbio en uno ya abierto.
     * @throws URISyntaxException si la URI de Location queda mal formada.
     */
    @PostMapping("/ingesta")
    public ResponseEntity<ResultadoDeduplicacionDTO> ingestarAlerta(@Valid @RequestBody AlertaEntranteDTO alertaEntranteDTO)
        throws URISyntaxException {
        LOG.debug("REST request para ingestar Alerta : {}", alertaEntranteDTO);

        ResultadoDeduplicacionDTO resultado;
        try {
            resultado = deduplicacionDeAlertasService.ingestar(alertaEntranteDTO);
        } catch (ServicioInexistenteException e) {
            throw new BadRequestAlertException(e.getMessage(), ENTITY_NAME, "servicionotfound");
        }

        return ResponseEntity.created(new URI("/api/alertas/" + resultado.getAlertaId())).body(resultado);
    }
}
