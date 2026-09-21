package ar.edu.um.isa.oncall.repository;

import ar.edu.um.isa.oncall.domain.Incidente;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface IncidenteRepositoryWithBagRelationships {
    Optional<Incidente> fetchBagRelationships(Optional<Incidente> incidente);

    List<Incidente> fetchBagRelationships(List<Incidente> incidentes);

    Page<Incidente> fetchBagRelationships(Page<Incidente> incidentes);
}
