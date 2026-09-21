import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../evento-de-incidente.test-samples';

import { EventoDeIncidenteFormService } from './evento-de-incidente-form.service';

describe('EventoDeIncidente Form Service', () => {
  let service: EventoDeIncidenteFormService;

  beforeEach(() => {
    service = TestBed.inject(EventoDeIncidenteFormService);
  });

  describe('Service methods', () => {
    describe('createEventoDeIncidenteFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createEventoDeIncidenteFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            tipo: expect.any(Object),
            detalle: expect.any(Object),
            ocurridoEn: expect.any(Object),
            automatico: expect.any(Object),
            incidente: expect.any(Object),
          }),
        );
      });

      it('passing IEventoDeIncidente should create a new form with FormGroup', () => {
        const formGroup = service.createEventoDeIncidenteFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            tipo: expect.any(Object),
            detalle: expect.any(Object),
            ocurridoEn: expect.any(Object),
            automatico: expect.any(Object),
            incidente: expect.any(Object),
          }),
        );
      });
    });

    describe('getEventoDeIncidente', () => {
      it('should return NewEventoDeIncidente for default EventoDeIncidente initial value', () => {
        const formGroup = service.createEventoDeIncidenteFormGroup(sampleWithNewData);

        const eventoDeIncidente = service.getEventoDeIncidente(formGroup);

        expect(eventoDeIncidente).toMatchObject(sampleWithNewData);
      });

      it('should return NewEventoDeIncidente for empty EventoDeIncidente initial value', () => {
        const formGroup = service.createEventoDeIncidenteFormGroup();

        const eventoDeIncidente = service.getEventoDeIncidente(formGroup);

        expect(eventoDeIncidente).toMatchObject({});
      });

      it('should return IEventoDeIncidente', () => {
        const formGroup = service.createEventoDeIncidenteFormGroup(sampleWithRequiredData);

        const eventoDeIncidente = service.getEventoDeIncidente(formGroup);

        expect(eventoDeIncidente).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IEventoDeIncidente should not enable id FormControl', () => {
        const formGroup = service.createEventoDeIncidenteFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewEventoDeIncidente should disable id FormControl', () => {
        const formGroup = service.createEventoDeIncidenteFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
