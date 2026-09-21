import { IObjetivoDeServicio, NewObjetivoDeServicio } from './objetivo-de-servicio.model';

export const sampleWithRequiredData: IObjetivoDeServicio = {
  id: 17001,
  tipo: 'DISPONIBILIDAD',
  severidadAplicable: 'SEV4',
  minutosObjetivo: 4231,
};

export const sampleWithPartialData: IObjetivoDeServicio = {
  id: 8347,
  tipo: 'TIEMPO_DE_RECONOCIMIENTO',
  severidadAplicable: 'SEV2',
  minutosObjetivo: 3131,
};

export const sampleWithFullData: IObjetivoDeServicio = {
  id: 18224,
  tipo: 'TIEMPO_DE_RECONOCIMIENTO',
  severidadAplicable: 'SEV1',
  minutosObjetivo: 2758,
  descripcion: 'frugal wetly',
};

export const sampleWithNewData: NewObjetivoDeServicio = {
  tipo: 'TIEMPO_DE_RECONOCIMIENTO',
  severidadAplicable: 'SEV2',
  minutosObjetivo: 9844,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
