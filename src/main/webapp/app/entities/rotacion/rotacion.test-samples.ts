import { IRotacion, NewRotacion } from './rotacion.model';

export const sampleWithRequiredData: IRotacion = {
  id: 26073,
  nombre: 'through print',
  tipo: 'SEMANAL',
  zonaHoraria: 'for cap whoa',
  activa: true,
};

export const sampleWithPartialData: IRotacion = {
  id: 7860,
  nombre: 'pitiful',
  tipo: 'PERSONALIZADA',
  zonaHoraria: 'atop absentmindedly',
  activa: false,
};

export const sampleWithFullData: IRotacion = {
  id: 27562,
  nombre: 'powerfully beyond license',
  tipo: 'DIARIA',
  zonaHoraria: 'dicker sternly barring',
  activa: false,
};

export const sampleWithNewData: NewRotacion = {
  nombre: 'true indeed strict',
  tipo: 'PERSONALIZADA',
  zonaHoraria: 'where famously',
  activa: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
