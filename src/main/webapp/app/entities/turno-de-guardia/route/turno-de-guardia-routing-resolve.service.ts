import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { TurnoDeGuardiaService } from '../service/turno-de-guardia.service';
import { ITurnoDeGuardia } from '../turno-de-guardia.model';

const turnoDeGuardiaResolve = (route: ActivatedRouteSnapshot): Observable<null | ITurnoDeGuardia> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(TurnoDeGuardiaService);
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

export default turnoDeGuardiaResolve;
