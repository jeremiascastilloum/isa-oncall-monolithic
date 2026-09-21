import dayjs from 'dayjs/esm';

import { IPostmortem, NewPostmortem } from './postmortem.model';

export const sampleWithRequiredData: IPostmortem = {
  id: 3653,
  titulo: 'promptly oxidise',
  resumen: 'up',
  causaRaiz: 'till tomorrow',
  publicado: false,
};

export const sampleWithPartialData: IPostmortem = {
  id: 12180,
  titulo: 'eek forenenst',
  resumen: 'mentor cutover',
  causaRaiz: 'huzzah cycle um',
  publicado: false,
  publicadoEn: dayjs('2023-12-04T16:37'),
};

export const sampleWithFullData: IPostmortem = {
  id: 18104,
  titulo: 'outrank polished mispronounce',
  resumen: 'bid',
  causaRaiz: 'whose key healthily',
  lineaDeTiempo: 'instantly woot',
  leccionesAprendidas: 'colorfully',
  publicado: false,
  publicadoEn: dayjs('2023-12-04T16:47'),
};

export const sampleWithNewData: NewPostmortem = {
  titulo: 'variable',
  resumen: 'ew throughout lightly',
  causaRaiz: 'when mysteriously',
  publicado: false,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
