import dayjs from 'dayjs/esm';

import { TipoEvento } from 'app/entities/enumerations/tipo-evento.model';
import { IIncidente } from 'app/entities/incidente/incidente.model';

export interface IEventoDeIncidente {
  id: number;
  tipo?: keyof typeof TipoEvento | null;
  detalle?: string | null;
  ocurridoEn?: dayjs.Dayjs | null;
  automatico?: boolean | null;
  incidente?: Pick<IIncidente, 'id' | 'titulo'> | null;
}

export type NewEventoDeIncidente = Omit<IEventoDeIncidente, 'id'> & { id: null };
