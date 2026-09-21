import dayjs from 'dayjs/esm';

import { Canal } from 'app/entities/enumerations/canal.model';
import { EstadoNotificacion } from 'app/entities/enumerations/estado-notificacion.model';
import { IIncidente } from 'app/entities/incidente/incidente.model';
import { IUser } from 'app/entities/user/user.model';

export interface INotificacion {
  id: number;
  canal?: keyof typeof Canal | null;
  destino?: string | null;
  estado?: keyof typeof EstadoNotificacion | null;
  enviadaEn?: dayjs.Dayjs | null;
  intentos?: number | null;
  errorMensaje?: string | null;
  incidente?: Pick<IIncidente, 'id' | 'titulo'> | null;
  destinatario?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewNotificacion = Omit<INotificacion, 'id'> & { id: null };
