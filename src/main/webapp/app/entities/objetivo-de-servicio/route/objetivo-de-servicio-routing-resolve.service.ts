import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { IObjetivoDeServicio } from '../objetivo-de-servicio.model';
import { ObjetivoDeServicioService } from '../service/objetivo-de-servicio.service';

const objetivoDeServicioResolve = (route: ActivatedRouteSnapshot): Observable<null | IObjetivoDeServicio> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(ObjetivoDeServicioService);
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

export default objetivoDeServicioResolve;
