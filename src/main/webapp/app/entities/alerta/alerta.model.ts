import dayjs from 'dayjs/esm';

import { OrigenAlerta } from 'app/entities/enumerations/origen-alerta.model';
import { IIncidente } from 'app/entities/incidente/incidente.model';
import { IServicio } from 'app/entities/servicio/servicio.model';

export interface IAlerta {
  id: number;
  fingerprint?: string | null;
  origen?: keyof typeof OrigenAlerta | null;
  resumen?: string | null;
  payload?: string | null;
  recibidaEn?: dayjs.Dayjs | null;
  procesada?: boolean | null;
  servicio?: Pick<IServicio, 'id' | 'nombre'> | null;
  incidente?: Pick<IIncidente, 'id' | 'titulo'> | null;
}

export type NewAlerta = Omit<IAlerta, 'id'> & { id: null };
