import { TipoRotacion } from 'app/entities/enumerations/tipo-rotacion.model';
import { IEquipo } from 'app/entities/equipo/equipo.model';

export interface IRotacion {
  id: number;
  nombre?: string | null;
  tipo?: keyof typeof TipoRotacion | null;
  zonaHoraria?: string | null;
  activa?: boolean | null;
  equipo?: Pick<IEquipo, 'id' | 'nombre'> | null;
}

export type NewRotacion = Omit<IRotacion, 'id'> & { id: null };
