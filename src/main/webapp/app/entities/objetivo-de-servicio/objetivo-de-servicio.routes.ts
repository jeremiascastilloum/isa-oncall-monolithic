import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import ObjetivoDeServicioResolve from './route/objetivo-de-servicio-routing-resolve.service';

const objetivoDeServicioRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/objetivo-de-servicio').then(m => m.ObjetivoDeServicio),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/objetivo-de-servicio-detail').then(m => m.ObjetivoDeServicioDetail),
    resolve: {
      objetivoDeServicio: ObjetivoDeServicioResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/objetivo-de-servicio-update').then(m => m.ObjetivoDeServicioUpdate),
    resolve: {
      objetivoDeServicio: ObjetivoDeServicioResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/objetivo-de-servicio-update').then(m => m.ObjetivoDeServicioUpdate),
    resolve: {
      objetivoDeServicio: ObjetivoDeServicioResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default objetivoDeServicioRoute;
