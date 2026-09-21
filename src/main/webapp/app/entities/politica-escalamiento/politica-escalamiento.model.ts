import { IServicio } from 'app/entities/servicio/servicio.model';

export interface IPoliticaEscalamiento {
  id: number;
  nombre?: string | null;
  descripcion?: string | null;
  repetirVeces?: number | null;
  servicio?: Pick<IServicio, 'id' | 'nombre'> | null;
}

export type NewPoliticaEscalamiento = Omit<IPoliticaEscalamiento, 'id'> & { id: null };
