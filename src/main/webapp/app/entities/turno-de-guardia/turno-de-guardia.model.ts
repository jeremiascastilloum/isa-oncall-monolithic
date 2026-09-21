import dayjs from 'dayjs/esm';

import { IRotacion } from 'app/entities/rotacion/rotacion.model';
import { IUser } from 'app/entities/user/user.model';

export interface ITurnoDeGuardia {
  id: number;
  desde?: dayjs.Dayjs | null;
  hasta?: dayjs.Dayjs | null;
  esReemplazo?: boolean | null;
  nota?: string | null;
  rotacion?: Pick<IRotacion, 'id' | 'nombre'> | null;
  responsable?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewTurnoDeGuardia = Omit<ITurnoDeGuardia, 'id'> & { id: null };
