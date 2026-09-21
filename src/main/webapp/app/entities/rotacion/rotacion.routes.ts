import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import RotacionResolve from './route/rotacion-routing-resolve.service';

const rotacionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/rotacion').then(m => m.Rotacion),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/rotacion-detail').then(m => m.RotacionDetail),
    resolve: {
      rotacion: RotacionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/rotacion-update').then(m => m.RotacionUpdate),
    resolve: {
      rotacion: RotacionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/rotacion-update').then(m => m.RotacionUpdate),
    resolve: {
      rotacion: RotacionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default rotacionRoute;
