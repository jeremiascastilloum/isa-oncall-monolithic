import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import IncidenteResolve from './route/incidente-routing-resolve.service';

const incidenteRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/incidente').then(m => m.Incidente),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/incidente-detail').then(m => m.IncidenteDetail),
    resolve: {
      incidente: IncidenteResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/incidente-update').then(m => m.IncidenteUpdate),
    resolve: {
      incidente: IncidenteResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/incidente-update').then(m => m.IncidenteUpdate),
    resolve: {
      incidente: IncidenteResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default incidenteRoute;
