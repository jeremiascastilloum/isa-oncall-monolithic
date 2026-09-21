import dayjs from 'dayjs/esm';

import { INotificacion, NewNotificacion } from './notificacion.model';

export const sampleWithRequiredData: INotificacion = {
  id: 4607,
  canal: 'EMAIL',
  destino: 'yuck',
  estado: 'ENVIADA',
  intentos: 1,
};

export const sampleWithPartialData: INotificacion = {
  id: 202,
  canal: 'EMAIL',
  destino: 'as',
  estado: 'ENTREGADA',
  enviadaEn: dayjs('2023-12-04T04:15'),
  intentos: 0,
};

export const sampleWithFullData: INotificacion = {
  id: 17311,
  canal: 'PUSH',
  destino: 'who anguished',
  estado: 'ENTREGADA',
  enviadaEn: dayjs('2023-12-04T14:43'),
  intentos: 7,
  errorMensaje: 'upbeat mmm outside',
};

export const sampleWithNewData: NewNotificacion = {
  canal: 'EMAIL',
  destino: 'muffled scarcely festival',
  estado: 'ENVIADA',
  intentos: 3,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
