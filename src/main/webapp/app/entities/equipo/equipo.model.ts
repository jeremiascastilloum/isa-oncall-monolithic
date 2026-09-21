export interface IEquipo {
  id: number;
  nombre?: string | null;
  emailContacto?: string | null;
  canalChat?: string | null;
}

export type NewEquipo = Omit<IEquipo, 'id'> & { id: null };
