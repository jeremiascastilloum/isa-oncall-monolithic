import dayjs from 'dayjs/esm';

import { IIncidente } from 'app/entities/incidente/incidente.model';

export interface IPostmortem {
  id: number;
  titulo?: string | null;
  resumen?: string | null;
  causaRaiz?: string | null;
  lineaDeTiempo?: string | null;
  leccionesAprendidas?: string | null;
  publicado?: boolean | null;
  publicadoEn?: dayjs.Dayjs | null;
  incidente?: Pick<IIncidente, 'id' | 'titulo'> | null;
}

export type NewPostmortem = Omit<IPostmortem, 'id'> & { id: null };
