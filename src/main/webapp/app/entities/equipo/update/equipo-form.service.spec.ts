import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../equipo.test-samples';

import { EquipoFormService } from './equipo-form.service';

describe('Equipo Form Service', () => {
  let service: EquipoFormService;

  beforeEach(() => {
    service = TestBed.inject(EquipoFormService);
  });

  describe('Service methods', () => {
    describe('createEquipoFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createEquipoFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nombre: expect.any(Object),
            emailContacto: expect.any(Object),
            canalChat: expect.any(Object),
          }),
        );
      });

      it('passing IEquipo should create a new form with FormGroup', () => {
        const formGroup = service.createEquipoFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nombre: expect.any(Object),
            emailContacto: expect.any(Object),
            canalChat: expect.any(Object),
          }),
        );
      });
    });

    describe('getEquipo', () => {
      it('should return NewEquipo for default Equipo initial value', () => {
        const formGroup = service.createEquipoFormGroup(sampleWithNewData);

        const equipo = service.getEquipo(formGroup);

        expect(equipo).toMatchObject(sampleWithNewData);
      });

      it('should return NewEquipo for empty Equipo initial value', () => {
        const formGroup = service.createEquipoFormGroup();

        const equipo = service.getEquipo(formGroup);

        expect(equipo).toMatchObject({});
      });

      it('should return IEquipo', () => {
        const formGroup = service.createEquipoFormGroup(sampleWithRequiredData);

        const equipo = service.getEquipo(formGroup);

        expect(equipo).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IEquipo should not enable id FormControl', () => {
        const formGroup = service.createEquipoFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewEquipo should disable id FormControl', () => {
        const formGroup = service.createEquipoFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
