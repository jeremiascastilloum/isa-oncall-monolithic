import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import PoliticaEscalamientoResolve from './route/politica-escalamiento-routing-resolve.service';

const politicaEscalamientoRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/politica-escalamiento').then(m => m.PoliticaEscalamiento),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/politica-escalamiento-detail').then(m => m.PoliticaEscalamientoDetail),
    resolve: {
      politicaEscalamiento: PoliticaEscalamientoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/politica-escalamiento-update').then(m => m.PoliticaEscalamientoUpdate),
    resolve: {
      politicaEscalamiento: PoliticaEscalamientoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/politica-escalamiento-update').then(m => m.PoliticaEscalamientoUpdate),
    resolve: {
      politicaEscalamiento: PoliticaEscalamientoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default politicaEscalamientoRoute;
