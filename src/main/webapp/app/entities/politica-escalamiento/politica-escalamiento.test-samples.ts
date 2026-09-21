import { IPoliticaEscalamiento, NewPoliticaEscalamiento } from './politica-escalamiento.model';

export const sampleWithRequiredData: IPoliticaEscalamiento = {
  id: 26176,
  nombre: 'including oof',
  repetirVeces: 4,
};

export const sampleWithPartialData: IPoliticaEscalamiento = {
  id: 10571,
  nombre: 'when minority',
  descripcion: 'pastel wonderfully',
  repetirVeces: 5,
};

export const sampleWithFullData: IPoliticaEscalamiento = {
  id: 5017,
  nombre: 'phooey drat',
  descripcion: 'fruitful because yippee',
  repetirVeces: 0,
};

export const sampleWithNewData: NewPoliticaEscalamiento = {
  nombre: 'sometimes hourly',
  repetirVeces: 2,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
