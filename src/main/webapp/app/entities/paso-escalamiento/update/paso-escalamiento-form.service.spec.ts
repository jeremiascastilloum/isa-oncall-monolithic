import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../paso-escalamiento.test-samples';

import { PasoEscalamientoFormService } from './paso-escalamiento-form.service';

describe('PasoEscalamiento Form Service', () => {
  let service: PasoEscalamientoFormService;

  beforeEach(() => {
    service = TestBed.inject(PasoEscalamientoFormService);
  });

  describe('Service methods', () => {
    describe('createPasoEscalamientoFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPasoEscalamientoFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            orden: expect.any(Object),
            esperaMinutos: expect.any(Object),
            canal: expect.any(Object),
            politica: expect.any(Object),
            rotacion: expect.any(Object),
            destinatarioDirecto: expect.any(Object),
          }),
        );
      });

      it('passing IPasoEscalamiento should create a new form with FormGroup', () => {
        const formGroup = service.createPasoEscalamientoFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            orden: expect.any(Object),
            esperaMinutos: expect.any(Object),
            canal: expect.any(Object),
            politica: expect.any(Object),
            rotacion: expect.any(Object),
            destinatarioDirecto: expect.any(Object),
          }),
        );
      });
    });

    describe('getPasoEscalamiento', () => {
      it('should return NewPasoEscalamiento for default PasoEscalamiento initial value', () => {
        const formGroup = service.createPasoEscalamientoFormGroup(sampleWithNewData);

        const pasoEscalamiento = service.getPasoEscalamiento(formGroup);

        expect(pasoEscalamiento).toMatchObject(sampleWithNewData);
      });

      it('should return NewPasoEscalamiento for empty PasoEscalamiento initial value', () => {
        const formGroup = service.createPasoEscalamientoFormGroup();

        const pasoEscalamiento = service.getPasoEscalamiento(formGroup);

        expect(pasoEscalamiento).toMatchObject({});
      });

      it('should return IPasoEscalamiento', () => {
        const formGroup = service.createPasoEscalamientoFormGroup(sampleWithRequiredData);

        const pasoEscalamiento = service.getPasoEscalamiento(formGroup);

        expect(pasoEscalamiento).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPasoEscalamiento should not enable id FormControl', () => {
        const formGroup = service.createPasoEscalamientoFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPasoEscalamiento should disable id FormControl', () => {
        const formGroup = service.createPasoEscalamientoFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
