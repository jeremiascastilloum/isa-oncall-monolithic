import { Severidad } from 'app/entities/enumerations/severidad.model';
import { TipoObjetivo } from 'app/entities/enumerations/tipo-objetivo.model';
import { IServicio } from 'app/entities/servicio/servicio.model';

export interface IObjetivoDeServicio {
  id: number;
  tipo?: keyof typeof TipoObjetivo | null;
  severidadAplicable?: keyof typeof Severidad | null;
  minutosObjetivo?: number | null;
  descripcion?: string | null;
  servicio?: Pick<IServicio, 'id' | 'nombre'> | null;
}

export type NewObjetivoDeServicio = Omit<IObjetivoDeServicio, 'id'> & { id: null };
