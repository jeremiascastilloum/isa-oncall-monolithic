import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../rotacion.test-samples';

import { RotacionFormService } from './rotacion-form.service';

describe('Rotacion Form Service', () => {
  let service: RotacionFormService;

  beforeEach(() => {
    service = TestBed.inject(RotacionFormService);
  });

  describe('Service methods', () => {
    describe('createRotacionFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createRotacionFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nombre: expect.any(Object),
            tipo: expect.any(Object),
            zonaHoraria: expect.any(Object),
            activa: expect.any(Object),
            equipo: expect.any(Object),
          }),
        );
      });

      it('passing IRotacion should create a new form with FormGroup', () => {
        const formGroup = service.createRotacionFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nombre: expect.any(Object),
            tipo: expect.any(Object),
            zonaHoraria: expect.any(Object),
            activa: expect.any(Object),
            equipo: expect.any(Object),
          }),
        );
      });
    });

    describe('getRotacion', () => {
      it('should return NewRotacion for default Rotacion initial value', () => {
        const formGroup = service.createRotacionFormGroup(sampleWithNewData);

        const rotacion = service.getRotacion(formGroup);

        expect(rotacion).toMatchObject(sampleWithNewData);
      });

      it('should return NewRotacion for empty Rotacion initial value', () => {
        const formGroup = service.createRotacionFormGroup();

        const rotacion = service.getRotacion(formGroup);

        expect(rotacion).toMatchObject({});
      });

      it('should return IRotacion', () => {
        const formGroup = service.createRotacionFormGroup(sampleWithRequiredData);

        const rotacion = service.getRotacion(formGroup);

        expect(rotacion).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IRotacion should not enable id FormControl', () => {
        const formGroup = service.createRotacionFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewRotacion should disable id FormControl', () => {
        const formGroup = service.createRotacionFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
