import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import EquipoResolve from './route/equipo-routing-resolve.service';

const equipoRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/equipo').then(m => m.Equipo),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/equipo-detail').then(m => m.EquipoDetail),
    resolve: {
      equipo: EquipoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/equipo-update').then(m => m.EquipoUpdate),
    resolve: {
      equipo: EquipoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/equipo-update').then(m => m.EquipoUpdate),
    resolve: {
      equipo: EquipoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default equipoRoute;
