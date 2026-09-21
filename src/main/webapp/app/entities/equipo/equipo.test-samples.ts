import { IEquipo, NewEquipo } from './equipo.model';

export const sampleWithRequiredData: IEquipo = {
  id: 11706,
  nombre: 'pish scare gratefully',
  emailContacto: 'phew',
};

export const sampleWithPartialData: IEquipo = {
  id: 6618,
  nombre: 'wherever',
  emailContacto: 'wearily',
  canalChat: 'although mmm',
};

export const sampleWithFullData: IEquipo = {
  id: 16760,
  nombre: 'polyester',
  emailContacto: 'caption unbalance',
  canalChat: 'fondly',
};

export const sampleWithNewData: NewEquipo = {
  nombre: 'safely hmph',
  emailContacto: 'furthermore',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
