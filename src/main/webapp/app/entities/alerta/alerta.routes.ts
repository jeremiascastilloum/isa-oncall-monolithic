import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import AlertaResolve from './route/alerta-routing-resolve.service';

const alertaRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/alerta').then(m => m.Alerta),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/alerta-detail').then(m => m.AlertaDetail),
    resolve: {
      alerta: AlertaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/alerta-update').then(m => m.AlertaUpdate),
    resolve: {
      alerta: AlertaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/alerta-update').then(m => m.AlertaUpdate),
    resolve: {
      alerta: AlertaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default alertaRoute;
