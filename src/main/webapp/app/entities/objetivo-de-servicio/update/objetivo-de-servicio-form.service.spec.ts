import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../objetivo-de-servicio.test-samples';

import { ObjetivoDeServicioFormService } from './objetivo-de-servicio-form.service';

describe('ObjetivoDeServicio Form Service', () => {
  let service: ObjetivoDeServicioFormService;

  beforeEach(() => {
    service = TestBed.inject(ObjetivoDeServicioFormService);
  });

  describe('Service methods', () => {
    describe('createObjetivoDeServicioFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createObjetivoDeServicioFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            tipo: expect.any(Object),
            severidadAplicable: expect.any(Object),
            minutosObjetivo: expect.any(Object),
            descripcion: expect.any(Object),
            servicio: expect.any(Object),
          }),
        );
      });

      it('passing IObjetivoDeServicio should create a new form with FormGroup', () => {
        const formGroup = service.createObjetivoDeServicioFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            tipo: expect.any(Object),
            severidadAplicable: expect.any(Object),
            minutosObjetivo: expect.any(Object),
            descripcion: expect.any(Object),
            servicio: expect.any(Object),
          }),
        );
      });
    });

    describe('getObjetivoDeServicio', () => {
      it('should return NewObjetivoDeServicio for default ObjetivoDeServicio initial value', () => {
        const formGroup = service.createObjetivoDeServicioFormGroup(sampleWithNewData);

        const objetivoDeServicio = service.getObjetivoDeServicio(formGroup);

        expect(objetivoDeServicio).toMatchObject(sampleWithNewData);
      });

      it('should return NewObjetivoDeServicio for empty ObjetivoDeServicio initial value', () => {
        const formGroup = service.createObjetivoDeServicioFormGroup();

        const objetivoDeServicio = service.getObjetivoDeServicio(formGroup);

        expect(objetivoDeServicio).toMatchObject({});
      });

      it('should return IObjetivoDeServicio', () => {
        const formGroup = service.createObjetivoDeServicioFormGroup(sampleWithRequiredData);

        const objetivoDeServicio = service.getObjetivoDeServicio(formGroup);

        expect(objetivoDeServicio).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IObjetivoDeServicio should not enable id FormControl', () => {
        const formGroup = service.createObjetivoDeServicioFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewObjetivoDeServicio should disable id FormControl', () => {
        const formGroup = service.createObjetivoDeServicioFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
