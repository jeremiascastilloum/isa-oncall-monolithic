import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { IIncidente } from '../incidente.model';
import { IncidenteService } from '../service/incidente.service';

const incidenteResolve = (route: ActivatedRouteSnapshot): Observable<null | IIncidente> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(IncidenteService);
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

export default incidenteResolve;
