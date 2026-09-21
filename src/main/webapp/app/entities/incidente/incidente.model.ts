import dayjs from 'dayjs/esm';

import { EstadoIncidente } from 'app/entities/enumerations/estado-incidente.model';
import { Severidad } from 'app/entities/enumerations/severidad.model';
import { IServicio } from 'app/entities/servicio/servicio.model';
import { IUser } from 'app/entities/user/user.model';

export interface IIncidente {
  id: number;
  titulo?: string | null;
  descripcion?: string | null;
  severidad?: keyof typeof Severidad | null;
  estado?: keyof typeof EstadoIncidente | null;
  detectadoEn?: dayjs.Dayjs | null;
  reconocidoEn?: dayjs.Dayjs | null;
  mitigadoEn?: dayjs.Dayjs | null;
  resueltoEn?: dayjs.Dayjs | null;
  usuariosAfectados?: number | null;
  cumplioObjetivo?: boolean | null;
  comandante?: Pick<IUser, 'id' | 'login'> | null;
  servicios?: Pick<IServicio, 'id' | 'nombre'>[] | null;
}

export type NewIncidente = Omit<IIncidente, 'id'> & { id: null };
