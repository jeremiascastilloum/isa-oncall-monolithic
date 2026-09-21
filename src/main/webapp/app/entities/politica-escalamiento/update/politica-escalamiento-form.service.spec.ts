import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../politica-escalamiento.test-samples';

import { PoliticaEscalamientoFormService } from './politica-escalamiento-form.service';

describe('PoliticaEscalamiento Form Service', () => {
  let service: PoliticaEscalamientoFormService;

  beforeEach(() => {
    service = TestBed.inject(PoliticaEscalamientoFormService);
  });

  describe('Service methods', () => {
    describe('createPoliticaEscalamientoFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPoliticaEscalamientoFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nombre: expect.any(Object),
            descripcion: expect.any(Object),
            repetirVeces: expect.any(Object),
            servicio: expect.any(Object),
          }),
        );
      });

      it('passing IPoliticaEscalamiento should create a new form with FormGroup', () => {
        const formGroup = service.createPoliticaEscalamientoFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nombre: expect.any(Object),
            descripcion: expect.any(Object),
            repetirVeces: expect.any(Object),
            servicio: expect.any(Object),
          }),
        );
      });
    });

    describe('getPoliticaEscalamiento', () => {
      it('should return NewPoliticaEscalamiento for default PoliticaEscalamiento initial value', () => {
        const formGroup = service.createPoliticaEscalamientoFormGroup(sampleWithNewData);

        const politicaEscalamiento = service.getPoliticaEscalamiento(formGroup);

        expect(politicaEscalamiento).toMatchObject(sampleWithNewData);
      });

      it('should return NewPoliticaEscalamiento for empty PoliticaEscalamiento initial value', () => {
        const formGroup = service.createPoliticaEscalamientoFormGroup();

        const politicaEscalamiento = service.getPoliticaEscalamiento(formGroup);

        expect(politicaEscalamiento).toMatchObject({});
      });

      it('should return IPoliticaEscalamiento', () => {
        const formGroup = service.createPoliticaEscalamientoFormGroup(sampleWithRequiredData);

        const politicaEscalamiento = service.getPoliticaEscalamiento(formGroup);

        expect(politicaEscalamiento).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPoliticaEscalamiento should not enable id FormControl', () => {
        const formGroup = service.createPoliticaEscalamientoFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPoliticaEscalamiento should disable id FormControl', () => {
        const formGroup = service.createPoliticaEscalamientoFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
