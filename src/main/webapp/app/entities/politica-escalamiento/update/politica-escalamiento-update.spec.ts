import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ServicioService } from 'app/entities/servicio/service/servicio.service';
import { IServicio } from 'app/entities/servicio/servicio.model';
import { IPoliticaEscalamiento } from '../politica-escalamiento.model';
import { PoliticaEscalamientoService } from '../service/politica-escalamiento.service';

import { PoliticaEscalamientoFormService } from './politica-escalamiento-form.service';
import { PoliticaEscalamientoUpdate } from './politica-escalamiento-update';

describe('PoliticaEscalamiento Management Update Component', () => {
  let comp: PoliticaEscalamientoUpdate;
  let fixture: ComponentFixture<PoliticaEscalamientoUpdate>;
  let activatedRoute: ActivatedRoute;
  let politicaEscalamientoFormService: PoliticaEscalamientoFormService;
  let politicaEscalamientoService: PoliticaEscalamientoService;
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

    fixture = TestBed.createComponent(PoliticaEscalamientoUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    politicaEscalamientoFormService = TestBed.inject(PoliticaEscalamientoFormService);
    politicaEscalamientoService = TestBed.inject(PoliticaEscalamientoService);
    servicioService = TestBed.inject(ServicioService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Servicio query and add missing value', () => {
      const politicaEscalamiento: IPoliticaEscalamiento = { id: 306 };
      const servicio: IServicio = { id: 24037 };
      politicaEscalamiento.servicio = servicio;

      const servicioCollection: IServicio[] = [{ id: 24037 }];
      vitest.spyOn(servicioService, 'query').mockReturnValue(of(new HttpResponse({ body: servicioCollection })));
      const additionalServicios = [servicio];
      const expectedCollection: IServicio[] = [...additionalServicios, ...servicioCollection];
      vitest.spyOn(servicioService, 'addServicioToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ politicaEscalamiento });
      comp.ngOnInit();

      expect(servicioService.query).toHaveBeenCalled();
      expect(servicioService.addServicioToCollectionIfMissing).toHaveBeenCalledWith(
        servicioCollection,
        ...additionalServicios.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.serviciosSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const politicaEscalamiento: IPoliticaEscalamiento = { id: 306 };
      const servicio: IServicio = { id: 24037 };
      politicaEscalamiento.servicio = servicio;

      activatedRoute.data = of({ politicaEscalamiento });
      comp.ngOnInit();

      expect(comp.serviciosSharedCollection()).toContainEqual(servicio);
      expect(comp.politicaEscalamiento).toEqual(politicaEscalamiento);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IPoliticaEscalamiento>();
      const politicaEscalamiento = { id: 17575 };
      vitest.spyOn(politicaEscalamientoFormService, 'getPoliticaEscalamiento').mockReturnValue(politicaEscalamiento);
      vitest.spyOn(politicaEscalamientoService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ politicaEscalamiento });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(politicaEscalamiento);
      saveSubject.complete();

      // THEN
      expect(politicaEscalamientoFormService.getPoliticaEscalamiento).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(politicaEscalamientoService.update).toHaveBeenCalledWith(expect.objectContaining(politicaEscalamiento));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IPoliticaEscalamiento>();
      const politicaEscalamiento = { id: 17575 };
      vitest.spyOn(politicaEscalamientoFormService, 'getPoliticaEscalamiento').mockReturnValue({ id: null });
      vitest.spyOn(politicaEscalamientoService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ politicaEscalamiento: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(politicaEscalamiento);
      saveSubject.complete();

      // THEN
      expect(politicaEscalamientoFormService.getPoliticaEscalamiento).toHaveBeenCalled();
      expect(politicaEscalamientoService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IPoliticaEscalamiento>();
      const politicaEscalamiento = { id: 17575 };
      vitest.spyOn(politicaEscalamientoService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ politicaEscalamiento });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(politicaEscalamientoService.update).toHaveBeenCalled();
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
