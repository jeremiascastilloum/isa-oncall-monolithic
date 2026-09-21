import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import NotificacionResolve from './route/notificacion-routing-resolve.service';

const notificacionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/notificacion').then(m => m.Notificacion),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/notificacion-detail').then(m => m.NotificacionDetail),
    resolve: {
      notificacion: NotificacionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/notificacion-update').then(m => m.NotificacionUpdate),
    resolve: {
      notificacion: NotificacionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/notificacion-update').then(m => m.NotificacionUpdate),
    resolve: {
      notificacion: NotificacionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default notificacionRoute;
