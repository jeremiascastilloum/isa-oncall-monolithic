import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import ServicioResolve from './route/servicio-routing-resolve.service';

const servicioRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/servicio').then(m => m.Servicio),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/servicio-detail').then(m => m.ServicioDetail),
    resolve: {
      servicio: ServicioResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/servicio-update').then(m => m.ServicioUpdate),
    resolve: {
      servicio: ServicioResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/servicio-update').then(m => m.ServicioUpdate),
    resolve: {
      servicio: ServicioResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default servicioRoute;
