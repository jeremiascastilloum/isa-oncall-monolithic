import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../accion-correctiva.test-samples';

import { AccionCorrectivaFormService } from './accion-correctiva-form.service';

describe('AccionCorrectiva Form Service', () => {
  let service: AccionCorrectivaFormService;

  beforeEach(() => {
    service = TestBed.inject(AccionCorrectivaFormService);
  });

  describe('Service methods', () => {
    describe('createAccionCorrectivaFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createAccionCorrectivaFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            descripcion: expect.any(Object),
            prioridad: expect.any(Object),
            estado: expect.any(Object),
            fechaLimite: expect.any(Object),
            ticketUrl: expect.any(Object),
            postmortem: expect.any(Object),
            responsable: expect.any(Object),
          }),
        );
      });

      it('passing IAccionCorrectiva should create a new form with FormGroup', () => {
        const formGroup = service.createAccionCorrectivaFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            descripcion: expect.any(Object),
            prioridad: expect.any(Object),
            estado: expect.any(Object),
            fechaLimite: expect.any(Object),
            ticketUrl: expect.any(Object),
            postmortem: expect.any(Object),
            responsable: expect.any(Object),
          }),
        );
      });
    });

    describe('getAccionCorrectiva', () => {
      it('should return NewAccionCorrectiva for default AccionCorrectiva initial value', () => {
        const formGroup = service.createAccionCorrectivaFormGroup(sampleWithNewData);

        const accionCorrectiva = service.getAccionCorrectiva(formGroup);

        expect(accionCorrectiva).toMatchObject(sampleWithNewData);
      });

      it('should return NewAccionCorrectiva for empty AccionCorrectiva initial value', () => {
        const formGroup = service.createAccionCorrectivaFormGroup();

        const accionCorrectiva = service.getAccionCorrectiva(formGroup);

        expect(accionCorrectiva).toMatchObject({});
      });

      it('should return IAccionCorrectiva', () => {
        const formGroup = service.createAccionCorrectivaFormGroup(sampleWithRequiredData);

        const accionCorrectiva = service.getAccionCorrectiva(formGroup);

        expect(accionCorrectiva).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IAccionCorrectiva should not enable id FormControl', () => {
        const formGroup = service.createAccionCorrectivaFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewAccionCorrectiva should disable id FormControl', () => {
        const formGroup = service.createAccionCorrectivaFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
