import { IPasoEscalamiento, NewPasoEscalamiento } from './paso-escalamiento.model';

export const sampleWithRequiredData: IPasoEscalamiento = {
  id: 24995,
  orden: 6,
  esperaMinutos: 120,
  canal: 'EMAIL',
};

export const sampleWithPartialData: IPasoEscalamiento = {
  id: 21347,
  orden: 4,
  esperaMinutos: 3,
  canal: 'WEBHOOK',
};

export const sampleWithFullData: IPasoEscalamiento = {
  id: 18284,
  orden: 1,
  esperaMinutos: 65,
  canal: 'PUSH',
};

export const sampleWithNewData: NewPasoEscalamiento = {
  orden: 4,
  esperaMinutos: 119,
  canal: 'LLAMADA',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
