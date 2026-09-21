import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { IPoliticaEscalamiento } from '../politica-escalamiento.model';
import { PoliticaEscalamientoService } from '../service/politica-escalamiento.service';

const politicaEscalamientoResolve = (route: ActivatedRouteSnapshot): Observable<null | IPoliticaEscalamiento> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(PoliticaEscalamientoService);
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

export default politicaEscalamientoResolve;
