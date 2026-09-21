import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../incidente.test-samples';

import { IncidenteFormService } from './incidente-form.service';

describe('Incidente Form Service', () => {
  let service: IncidenteFormService;

  beforeEach(() => {
    service = TestBed.inject(IncidenteFormService);
  });

  describe('Service methods', () => {
    describe('createIncidenteFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createIncidenteFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            titulo: expect.any(Object),
            descripcion: expect.any(Object),
            severidad: expect.any(Object),
            estado: expect.any(Object),
            detectadoEn: expect.any(Object),
            reconocidoEn: expect.any(Object),
            mitigadoEn: expect.any(Object),
            resueltoEn: expect.any(Object),
            usuariosAfectados: expect.any(Object),
            cumplioObjetivo: expect.any(Object),
            comandante: expect.any(Object),
            servicios: expect.any(Object),
          }),
        );
      });

      it('passing IIncidente should create a new form with FormGroup', () => {
        const formGroup = service.createIncidenteFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            titulo: expect.any(Object),
            descripcion: expect.any(Object),
            severidad: expect.any(Object),
            estado: expect.any(Object),
            detectadoEn: expect.any(Object),
            reconocidoEn: expect.any(Object),
            mitigadoEn: expect.any(Object),
            resueltoEn: expect.any(Object),
            usuariosAfectados: expect.any(Object),
            cumplioObjetivo: expect.any(Object),
            comandante: expect.any(Object),
            servicios: expect.any(Object),
          }),
        );
      });
    });

    describe('getIncidente', () => {
      it('should return NewIncidente for default Incidente initial value', () => {
        const formGroup = service.createIncidenteFormGroup(sampleWithNewData);

        const incidente = service.getIncidente(formGroup);

        expect(incidente).toMatchObject(sampleWithNewData);
      });

      it('should return NewIncidente for empty Incidente initial value', () => {
        const formGroup = service.createIncidenteFormGroup();

        const incidente = service.getIncidente(formGroup);

        expect(incidente).toMatchObject({});
      });

      it('should return IIncidente', () => {
        const formGroup = service.createIncidenteFormGroup(sampleWithRequiredData);

        const incidente = service.getIncidente(formGroup);

        expect(incidente).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IIncidente should not enable id FormControl', () => {
        const formGroup = service.createIncidenteFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewIncidente should disable id FormControl', () => {
        const formGroup = service.createIncidenteFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
