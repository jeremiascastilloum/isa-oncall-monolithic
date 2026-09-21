import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import PasoEscalamientoResolve from './route/paso-escalamiento-routing-resolve.service';

const pasoEscalamientoRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/paso-escalamiento').then(m => m.PasoEscalamiento),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/paso-escalamiento-detail').then(m => m.PasoEscalamientoDetail),
    resolve: {
      pasoEscalamiento: PasoEscalamientoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/paso-escalamiento-update').then(m => m.PasoEscalamientoUpdate),
    resolve: {
      pasoEscalamiento: PasoEscalamientoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/paso-escalamiento-update').then(m => m.PasoEscalamientoUpdate),
    resolve: {
      pasoEscalamiento: PasoEscalamientoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default pasoEscalamientoRoute;
