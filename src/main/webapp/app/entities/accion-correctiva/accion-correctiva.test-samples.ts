import dayjs from 'dayjs/esm';

import { IAccionCorrectiva, NewAccionCorrectiva } from './accion-correctiva.model';

export const sampleWithRequiredData: IAccionCorrectiva = {
  id: 18857,
  descripcion: 'carpool towards tool',
  prioridad: 'MEDIA',
  estado: 'DESCARTADA',
};

export const sampleWithPartialData: IAccionCorrectiva = {
  id: 29532,
  descripcion: 'utilization fort',
  prioridad: 'BAJA',
  estado: 'COMPLETADA',
  fechaLimite: dayjs('2023-12-04'),
};

export const sampleWithFullData: IAccionCorrectiva = {
  id: 22398,
  descripcion: 'maroon',
  prioridad: 'ALTA',
  estado: 'COMPLETADA',
  fechaLimite: dayjs('2023-12-04'),
  ticketUrl: 'hutch',
};

export const sampleWithNewData: NewAccionCorrectiva = {
  descripcion: 'embed geez',
  prioridad: 'ALTA',
  estado: 'DESCARTADA',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
