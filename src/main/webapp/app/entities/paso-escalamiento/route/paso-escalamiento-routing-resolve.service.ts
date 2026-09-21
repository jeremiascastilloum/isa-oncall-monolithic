import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { IPasoEscalamiento } from '../paso-escalamiento.model';
import { PasoEscalamientoService } from '../service/paso-escalamiento.service';

const pasoEscalamientoResolve = (route: ActivatedRouteSnapshot): Observable<null | IPasoEscalamiento> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(PasoEscalamientoService);
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

export default pasoEscalamientoResolve;
