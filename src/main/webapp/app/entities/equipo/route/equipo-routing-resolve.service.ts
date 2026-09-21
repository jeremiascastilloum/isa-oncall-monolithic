import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { IEquipo } from '../equipo.model';
import { EquipoService } from '../service/equipo.service';

const equipoResolve = (route: ActivatedRouteSnapshot): Observable<null | IEquipo> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(EquipoService);
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

export default equipoResolve;
