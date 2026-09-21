import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../turno-de-guardia.test-samples';

import { TurnoDeGuardiaFormService } from './turno-de-guardia-form.service';

describe('TurnoDeGuardia Form Service', () => {
  let service: TurnoDeGuardiaFormService;

  beforeEach(() => {
    service = TestBed.inject(TurnoDeGuardiaFormService);
  });

  describe('Service methods', () => {
    describe('createTurnoDeGuardiaFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTurnoDeGuardiaFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            desde: expect.any(Object),
            hasta: expect.any(Object),
            esReemplazo: expect.any(Object),
            nota: expect.any(Object),
            rotacion: expect.any(Object),
            responsable: expect.any(Object),
          }),
        );
      });

      it('passing ITurnoDeGuardia should create a new form with FormGroup', () => {
        const formGroup = service.createTurnoDeGuardiaFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            desde: expect.any(Object),
            hasta: expect.any(Object),
            esReemplazo: expect.any(Object),
            nota: expect.any(Object),
            rotacion: expect.any(Object),
            responsable: expect.any(Object),
          }),
        );
      });
    });

    describe('getTurnoDeGuardia', () => {
      it('should return NewTurnoDeGuardia for default TurnoDeGuardia initial value', () => {
        const formGroup = service.createTurnoDeGuardiaFormGroup(sampleWithNewData);

        const turnoDeGuardia = service.getTurnoDeGuardia(formGroup);

        expect(turnoDeGuardia).toMatchObject(sampleWithNewData);
      });

      it('should return NewTurnoDeGuardia for empty TurnoDeGuardia initial value', () => {
        const formGroup = service.createTurnoDeGuardiaFormGroup();

        const turnoDeGuardia = service.getTurnoDeGuardia(formGroup);

        expect(turnoDeGuardia).toMatchObject({});
      });

      it('should return ITurnoDeGuardia', () => {
        const formGroup = service.createTurnoDeGuardiaFormGroup(sampleWithRequiredData);

        const turnoDeGuardia = service.getTurnoDeGuardia(formGroup);

        expect(turnoDeGuardia).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITurnoDeGuardia should not enable id FormControl', () => {
        const formGroup = service.createTurnoDeGuardiaFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTurnoDeGuardia should disable id FormControl', () => {
        const formGroup = service.createTurnoDeGuardiaFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
