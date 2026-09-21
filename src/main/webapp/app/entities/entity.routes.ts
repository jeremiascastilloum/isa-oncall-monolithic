import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'oncallApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'equipo',
    data: { pageTitle: 'oncallApp.equipo.home.title' },
    loadChildren: () => import('./equipo/equipo.routes'),
  },
  {
    path: 'servicio',
    data: { pageTitle: 'oncallApp.servicio.home.title' },
    loadChildren: () => import('./servicio/servicio.routes'),
  },
  {
    path: 'objetivo-de-servicio',
    data: { pageTitle: 'oncallApp.objetivoDeServicio.home.title' },
    loadChildren: () => import('./objetivo-de-servicio/objetivo-de-servicio.routes'),
  },
  {
    path: 'alerta',
    data: { pageTitle: 'oncallApp.alerta.home.title' },
    loadChildren: () => import('./alerta/alerta.routes'),
  },
  {
    path: 'incidente',
    data: { pageTitle: 'oncallApp.incidente.home.title' },
    loadChildren: () => import('./incidente/incidente.routes'),
  },
  {
    path: 'evento-de-incidente',
    data: { pageTitle: 'oncallApp.eventoDeIncidente.home.title' },
    loadChildren: () => import('./evento-de-incidente/evento-de-incidente.routes'),
  },
  {
    path: 'rotacion',
    data: { pageTitle: 'oncallApp.rotacion.home.title' },
    loadChildren: () => import('./rotacion/rotacion.routes'),
  },
  {
    path: 'turno-de-guardia',
    data: { pageTitle: 'oncallApp.turnoDeGuardia.home.title' },
    loadChildren: () => import('./turno-de-guardia/turno-de-guardia.routes'),
  },
  {
    path: 'politica-escalamiento',
    data: { pageTitle: 'oncallApp.politicaEscalamiento.home.title' },
    loadChildren: () => import('./politica-escalamiento/politica-escalamiento.routes'),
  },
  {
    path: 'paso-escalamiento',
    data: { pageTitle: 'oncallApp.pasoEscalamiento.home.title' },
    loadChildren: () => import('./paso-escalamiento/paso-escalamiento.routes'),
  },
  {
    path: 'notificacion',
    data: { pageTitle: 'oncallApp.notificacion.home.title' },
    loadChildren: () => import('./notificacion/notificacion.routes'),
  },
  {
    path: 'postmortem',
    data: { pageTitle: 'oncallApp.postmortem.home.title' },
    loadChildren: () => import('./postmortem/postmortem.routes'),
  },
  {
    path: 'accion-correctiva',
    data: { pageTitle: 'oncallApp.accionCorrectiva.home.title' },
    loadChildren: () => import('./accion-correctiva/accion-correctiva.routes'),
  },
  {
    path: 'user-management',
    data: { pageTitle: 'userManagement.home.title' },
    loadChildren: () => import('./admin/user-management/user-management.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
