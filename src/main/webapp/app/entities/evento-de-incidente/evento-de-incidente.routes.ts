import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import EventoDeIncidenteResolve from './route/evento-de-incidente-routing-resolve.service';

const eventoDeIncidenteRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/evento-de-incidente').then(m => m.EventoDeIncidente),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/evento-de-incidente-detail').then(m => m.EventoDeIncidenteDetail),
    resolve: {
      eventoDeIncidente: EventoDeIncidenteResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/evento-de-incidente-update').then(m => m.EventoDeIncidenteUpdate),
    resolve: {
      eventoDeIncidente: EventoDeIncidenteResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/evento-de-incidente-update').then(m => m.EventoDeIncidenteUpdate),
    resolve: {
      eventoDeIncidente: EventoDeIncidenteResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default eventoDeIncidenteRoute;
