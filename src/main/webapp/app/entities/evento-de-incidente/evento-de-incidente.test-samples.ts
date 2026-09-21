import dayjs from 'dayjs/esm';

import { IEventoDeIncidente, NewEventoDeIncidente } from './evento-de-incidente.model';

export const sampleWithRequiredData: IEventoDeIncidente = {
  id: 10884,
  tipo: 'ASIGNACION',
  detalle: 'especially while',
  ocurridoEn: dayjs('2023-12-04T16:14'),
  automatico: false,
};

export const sampleWithPartialData: IEventoDeIncidente = {
  id: 18705,
  tipo: 'ASIGNACION',
  detalle: 'midst pro so',
  ocurridoEn: dayjs('2023-12-04T12:21'),
  automatico: false,
};

export const sampleWithFullData: IEventoDeIncidente = {
  id: 22194,
  tipo: 'ASIGNACION',
  detalle: 'although barring',
  ocurridoEn: dayjs('2023-12-04T20:14'),
  automatico: true,
};

export const sampleWithNewData: NewEventoDeIncidente = {
  tipo: 'NOTA',
  detalle: 'truly why',
  ocurridoEn: dayjs('2023-12-04T02:23'),
  automatico: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
