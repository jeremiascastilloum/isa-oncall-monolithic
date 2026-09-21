import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import PostmortemResolve from './route/postmortem-routing-resolve.service';

const postmortemRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/postmortem').then(m => m.Postmortem),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/postmortem-detail').then(m => m.PostmortemDetail),
    resolve: {
      postmortem: PostmortemResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/postmortem-update').then(m => m.PostmortemUpdate),
    resolve: {
      postmortem: PostmortemResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/postmortem-update').then(m => m.PostmortemUpdate),
    resolve: {
      postmortem: PostmortemResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default postmortemRoute;
