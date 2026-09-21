import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { IPostmortem } from '../postmortem.model';
import { PostmortemService } from '../service/postmortem.service';

const postmortemResolve = (route: ActivatedRouteSnapshot): Observable<null | IPostmortem> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(PostmortemService);
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

export default postmortemResolve;
