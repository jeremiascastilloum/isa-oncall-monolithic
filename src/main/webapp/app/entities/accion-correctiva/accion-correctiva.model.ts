import dayjs from 'dayjs/esm';

import { EstadoAccion } from 'app/entities/enumerations/estado-accion.model';
import { Prioridad } from 'app/entities/enumerations/prioridad.model';
import { IPostmortem } from 'app/entities/postmortem/postmortem.model';
import { IUser } from 'app/entities/user/user.model';

export interface IAccionCorrectiva {
  id: number;
  descripcion?: string | null;
  prioridad?: keyof typeof Prioridad | null;
  estado?: keyof typeof EstadoAccion | null;
  fechaLimite?: dayjs.Dayjs | null;
  ticketUrl?: string | null;
  postmortem?: Pick<IPostmortem, 'id' | 'titulo'> | null;
  responsable?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewAccionCorrectiva = Omit<IAccionCorrectiva, 'id'> & { id: null };
