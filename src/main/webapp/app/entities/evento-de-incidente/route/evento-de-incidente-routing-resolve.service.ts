import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { IEventoDeIncidente } from '../evento-de-incidente.model';
import { EventoDeIncidenteService } from '../service/evento-de-incidente.service';

const eventoDeIncidenteResolve = (route: ActivatedRouteSnapshot): Observable<null | IEventoDeIncidente> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(EventoDeIncidenteService);
    return service.find(id).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 404) {
          router.navigate(['404']);
        } else {
          router.navigate(['error']);
        }
        return EMPTY;
      }),
    );
  }

  return of(null);
};

export default eventoDeIncidenteResolve;
