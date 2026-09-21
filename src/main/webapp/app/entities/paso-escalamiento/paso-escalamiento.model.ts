import { Canal } from 'app/entities/enumerations/canal.model';
import { IPoliticaEscalamiento } from 'app/entities/politica-escalamiento/politica-escalamiento.model';
import { IRotacion } from 'app/entities/rotacion/rotacion.model';
import { IUser } from 'app/entities/user/user.model';

export interface IPasoEscalamiento {
  id: number;
  orden?: number | null;
  esperaMinutos?: number | null;
  canal?: keyof typeof Canal | null;
  politica?: Pick<IPoliticaEscalamiento, 'id' | 'nombre'> | null;
  rotacion?: Pick<IRotacion, 'id' | 'nombre'> | null;
  destinatarioDirecto?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewPasoEscalamiento = Omit<IPasoEscalamiento, 'id'> & { id: null };
