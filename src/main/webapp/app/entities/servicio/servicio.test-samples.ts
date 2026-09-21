import { IServicio, NewServicio } from './servicio.model';

export const sampleWithRequiredData: IServicio = {
  id: 27960,
  nombre: 'besides that reckon',
  criticidad: 'TIER3',
  entorno: 'STAGING',
  activo: false,
};

export const sampleWithPartialData: IServicio = {
  id: 12320,
  nombre: 'meh',
  criticidad: 'TIER1',
  entorno: 'PRODUCCION',
  activo: true,
};

export const sampleWithFullData: IServicio = {
  id: 15992,
  nombre: 'gaseous total',
  descripcion: 'pigpen beneath swerve',
  criticidad: 'TIER2',
  entorno: 'PRODUCCION',
  repositorioUrl: 'downchange',
  activo: true,
};

export const sampleWithNewData: NewServicio = {
  nombre: 'absent',
  criticidad: 'TIER3',
  entorno: 'DESARROLLO',
  activo: false,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
