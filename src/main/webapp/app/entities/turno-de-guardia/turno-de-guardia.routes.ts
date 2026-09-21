import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import TurnoDeGuardiaResolve from './route/turno-de-guardia-routing-resolve.service';

const turnoDeGuardiaRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/turno-de-guardia').then(m => m.TurnoDeGuardia),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/turno-de-guardia-detail').then(m => m.TurnoDeGuardiaDetail),
    resolve: {
      turnoDeGuardia: TurnoDeGuardiaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/turno-de-guardia-update').then(m => m.TurnoDeGuardiaUpdate),
    resolve: {
      turnoDeGuardia: TurnoDeGuardiaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/turno-de-guardia-update').then(m => m.TurnoDeGuardiaUpdate),
    resolve: {
      turnoDeGuardia: TurnoDeGuardiaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default turnoDeGuardiaRoute;
