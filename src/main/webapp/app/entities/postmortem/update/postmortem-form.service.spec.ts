import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../postmortem.test-samples';

import { PostmortemFormService } from './postmortem-form.service';

describe('Postmortem Form Service', () => {
  let service: PostmortemFormService;

  beforeEach(() => {
    service = TestBed.inject(PostmortemFormService);
  });

  describe('Service methods', () => {
    describe('createPostmortemFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPostmortemFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            titulo: expect.any(Object),
            resumen: expect.any(Object),
            causaRaiz: expect.any(Object),
            lineaDeTiempo: expect.any(Object),
            leccionesAprendidas: expect.any(Object),
            publicado: expect.any(Object),
            publicadoEn: expect.any(Object),
            incidente: expect.any(Object),
          }),
        );
      });

      it('passing IPostmortem should create a new form with FormGroup', () => {
        const formGroup = service.createPostmortemFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            titulo: expect.any(Object),
            resumen: expect.any(Object),
            causaRaiz: expect.any(Object),
            lineaDeTiempo: expect.any(Object),
            leccionesAprendidas: expect.any(Object),
            publicado: expect.any(Object),
            publicadoEn: expect.any(Object),
            incidente: expect.any(Object),
          }),
        );
      });
    });

    describe('getPostmortem', () => {
      it('should return NewPostmortem for default Postmortem initial value', () => {
        const formGroup = service.createPostmortemFormGroup(sampleWithNewData);

        const postmortem = service.getPostmortem(formGroup);

        expect(postmortem).toMatchObject(sampleWithNewData);
      });

      it('should return NewPostmortem for empty Postmortem initial value', () => {
        const formGroup = service.createPostmortemFormGroup();

        const postmortem = service.getPostmortem(formGroup);

        expect(postmortem).toMatchObject({});
      });

      it('should return IPostmortem', () => {
        const formGroup = service.createPostmortemFormGroup(sampleWithRequiredData);

        const postmortem = service.getPostmortem(formGroup);

        expect(postmortem).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPostmortem should not enable id FormControl', () => {
        const formGroup = service.createPostmortemFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPostmortem should disable id FormControl', () => {
        const formGroup = service.createPostmortemFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
