import { Criticidad } from 'app/entities/enumerations/criticidad.model';
import { Entorno } from 'app/entities/enumerations/entorno.model';
import { IEquipo } from 'app/entities/equipo/equipo.model';
import { IIncidente } from 'app/entities/incidente/incidente.model';

export interface IServicio {
  id: number;
  nombre?: string | null;
  descripcion?: string | null;
  criticidad?: keyof typeof Criticidad | null;
  entorno?: keyof typeof Entorno | null;
  repositorioUrl?: string | null;
  activo?: boolean | null;
  equipo?: Pick<IEquipo, 'id' | 'nombre'> | null;
  incidentes?: Pick<IIncidente, 'id'>[] | null;
}

export type NewServicio = Omit<IServicio, 'id'> & { id: null };
