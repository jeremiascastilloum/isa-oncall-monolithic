import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../alerta.test-samples';

import { AlertaFormService } from './alerta-form.service';

describe('Alerta Form Service', () => {
  let service: AlertaFormService;

  beforeEach(() => {
    service = TestBed.inject(AlertaFormService);
  });

  describe('Service methods', () => {
    describe('createAlertaFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createAlertaFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            fingerprint: expect.any(Object),
            origen: expect.any(Object),
            resumen: expect.any(Object),
            payload: expect.any(Object),
            recibidaEn: expect.any(Object),
            procesada: expect.any(Object),
            servicio: expect.any(Object),
            incidente: expect.any(Object),
          }),
        );
      });

      it('passing IAlerta should create a new form with FormGroup', () => {
        const formGroup = service.createAlertaFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            fingerprint: expect.any(Object),
            origen: expect.any(Object),
            resumen: expect.any(Object),
            payload: expect.any(Object),
            recibidaEn: expect.any(Object),
            procesada: expect.any(Object),
            servicio: expect.any(Object),
            incidente: expect.any(Object),
          }),
        );
      });
    });

    describe('getAlerta', () => {
      it('should return NewAlerta for default Alerta initial value', () => {
        const formGroup = service.createAlertaFormGroup(sampleWithNewData);

        const alerta = service.getAlerta(formGroup);

        expect(alerta).toMatchObject(sampleWithNewData);
      });

      it('should return NewAlerta for empty Alerta initial value', () => {
        const formGroup = service.createAlertaFormGroup();

        const alerta = service.getAlerta(formGroup);

        expect(alerta).toMatchObject({});
      });

      it('should return IAlerta', () => {
        const formGroup = service.createAlertaFormGroup(sampleWithRequiredData);

        const alerta = service.getAlerta(formGroup);

        expect(alerta).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IAlerta should not enable id FormControl', () => {
        const formGroup = service.createAlertaFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewAlerta should disable id FormControl', () => {
        const formGroup = service.createAlertaFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
