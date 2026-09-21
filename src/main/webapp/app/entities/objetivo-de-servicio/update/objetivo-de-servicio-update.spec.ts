import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ServicioService } from 'app/entities/servicio/service/servicio.service';
import { IServicio } from 'app/entities/servicio/servicio.model';
import { IObjetivoDeServicio } from '../objetivo-de-servicio.model';
import { ObjetivoDeServicioService } from '../service/objetivo-de-servicio.service';

import { ObjetivoDeServicioFormService } from './objetivo-de-servicio-form.service';
import { ObjetivoDeServicioUpdate } from './objetivo-de-servicio-update';

describe('ObjetivoDeServicio Management Update Component', () => {
  let comp: ObjetivoDeServicioUpdate;
  let fixture: ComponentFixture<ObjetivoDeServicioUpdate>;
  let activatedRoute: ActivatedRoute;
  let objetivoDeServicioFormService: ObjetivoDeServicioFormService;
  let objetivoDeServicioService: ObjetivoDeServicioService;
  let servicioService: ServicioService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideTranslateService(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    });

    fixture = TestBed.createComponent(ObjetivoDeServicioUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    objetivoDeServicioFormService = TestBed.inject(ObjetivoDeServicioFormService);
    objetivoDeServicioService = TestBed.inject(ObjetivoDeServicioService);
    servicioService = TestBed.inject(ServicioService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Servicio query and add missing value', () => {
      const objetivoDeServicio: IObjetivoDeServicio = { id: 26426 };
      const servicio: IServicio = { id: 24037 };
      objetivoDeServicio.servicio = servicio;

      const servicioCollection: IServicio[] = [{ id: 24037 }];
      vitest.spyOn(servicioService, 'query').mockReturnValue(of(new HttpResponse({ body: servicioCollection })));
      const additionalServicios = [servicio];
      const expectedCollection: IServicio[] = [...additionalServicios, ...servicioCollection];
      vitest.spyOn(servicioService, 'addServicioToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ objetivoDeServicio });
      comp.ngOnInit();

      expect(servicioService.query).toHaveBeenCalled();
      expect(servicioService.addServicioToCollectionIfMissing).toHaveBeenCalledWith(
        servicioCollection,
        ...additionalServicios.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.serviciosSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const objetivoDeServicio: IObjetivoDeServicio = { id: 26426 };
      const servicio: IServicio = { id: 24037 };
      objetivoDeServicio.servicio = servicio;

      activatedRoute.data = of({ objetivoDeServicio });
      comp.ngOnInit();

      expect(comp.serviciosSharedCollection()).toContainEqual(servicio);
      expect(comp.objetivoDeServicio).toEqual(objetivoDeServicio);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IObjetivoDeServicio>();
      const objetivoDeServicio = { id: 18261 };
      vitest.spyOn(objetivoDeServicioFormService, 'getObjetivoDeServicio').mockReturnValue(objetivoDeServicio);
      vitest.spyOn(objetivoDeServicioService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ objetivoDeServicio });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(objetivoDeServicio);
      saveSubject.complete();

      // THEN
      expect(objetivoDeServicioFormService.getObjetivoDeServicio).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(objetivoDeServicioService.update).toHaveBeenCalledWith(expect.objectContaining(objetivoDeServicio));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IObjetivoDeServicio>();
      const objetivoDeServicio = { id: 18261 };
      vitest.spyOn(objetivoDeServicioFormService, 'getObjetivoDeServicio').mockReturnValue({ id: null });
      vitest.spyOn(objetivoDeServicioService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ objetivoDeServicio: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(objetivoDeServicio);
      saveSubject.complete();

      // THEN
      expect(objetivoDeServicioFormService.getObjetivoDeServicio).toHaveBeenCalled();
      expect(objetivoDeServicioService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IObjetivoDeServicio>();
      const objetivoDeServicio = { id: 18261 };
      vitest.spyOn(objetivoDeServicioService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ objetivoDeServicio });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(objetivoDeServicioService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareServicio', () => {
      it('should forward to servicioService', () => {
        const entity = { id: 24037 };
        const entity2 = { id: 644 };
        vitest.spyOn(servicioService, 'compareServicio');
        comp.compareServicio(entity, entity2);
        expect(servicioService.compareServicio).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
