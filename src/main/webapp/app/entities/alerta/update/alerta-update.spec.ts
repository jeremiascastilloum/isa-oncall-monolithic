import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IIncidente } from 'app/entities/incidente/incidente.model';
import { IncidenteService } from 'app/entities/incidente/service/incidente.service';
import { ServicioService } from 'app/entities/servicio/service/servicio.service';
import { IServicio } from 'app/entities/servicio/servicio.model';
import { IAlerta } from '../alerta.model';
import { AlertaService } from '../service/alerta.service';

import { AlertaFormService } from './alerta-form.service';
import { AlertaUpdate } from './alerta-update';

describe('Alerta Management Update Component', () => {
  let comp: AlertaUpdate;
  let fixture: ComponentFixture<AlertaUpdate>;
  let activatedRoute: ActivatedRoute;
  let alertaFormService: AlertaFormService;
  let alertaService: AlertaService;
  let servicioService: ServicioService;
  let incidenteService: IncidenteService;

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

    fixture = TestBed.createComponent(AlertaUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    alertaFormService = TestBed.inject(AlertaFormService);
    alertaService = TestBed.inject(AlertaService);
    servicioService = TestBed.inject(ServicioService);
    incidenteService = TestBed.inject(IncidenteService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Servicio query and add missing value', () => {
      const alerta: IAlerta = { id: 26829 };
      const servicio: IServicio = { id: 24037 };
      alerta.servicio = servicio;

      const servicioCollection: IServicio[] = [{ id: 24037 }];
      vitest.spyOn(servicioService, 'query').mockReturnValue(of(new HttpResponse({ body: servicioCollection })));
      const additionalServicios = [servicio];
      const expectedCollection: IServicio[] = [...additionalServicios, ...servicioCollection];
      vitest.spyOn(servicioService, 'addServicioToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ alerta });
      comp.ngOnInit();

      expect(servicioService.query).toHaveBeenCalled();
      expect(servicioService.addServicioToCollectionIfMissing).toHaveBeenCalledWith(
        servicioCollection,
        ...additionalServicios.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.serviciosSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Incidente query and add missing value', () => {
      const alerta: IAlerta = { id: 26829 };
      const incidente: IIncidente = { id: 31968 };
      alerta.incidente = incidente;

      const incidenteCollection: IIncidente[] = [{ id: 31968 }];
      vitest.spyOn(incidenteService, 'query').mockReturnValue(of(new HttpResponse({ body: incidenteCollection })));
      const additionalIncidentes = [incidente];
      const expectedCollection: IIncidente[] = [...additionalIncidentes, ...incidenteCollection];
      vitest.spyOn(incidenteService, 'addIncidenteToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ alerta });
      comp.ngOnInit();

      expect(incidenteService.query).toHaveBeenCalled();
      expect(incidenteService.addIncidenteToCollectionIfMissing).toHaveBeenCalledWith(
        incidenteCollection,
        ...additionalIncidentes.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.incidentesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const alerta: IAlerta = { id: 26829 };
      const servicio: IServicio = { id: 24037 };
      alerta.servicio = servicio;
      const incidente: IIncidente = { id: 31968 };
      alerta.incidente = incidente;

      activatedRoute.data = of({ alerta });
      comp.ngOnInit();

      expect(comp.serviciosSharedCollection()).toContainEqual(servicio);
      expect(comp.incidentesSharedCollection()).toContainEqual(incidente);
      expect(comp.alerta).toEqual(alerta);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IAlerta>();
      const alerta = { id: 13581 };
      vitest.spyOn(alertaFormService, 'getAlerta').mockReturnValue(alerta);
      vitest.spyOn(alertaService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ alerta });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(alerta);
      saveSubject.complete();

      // THEN
      expect(alertaFormService.getAlerta).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(alertaService.update).toHaveBeenCalledWith(expect.objectContaining(alerta));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IAlerta>();
      const alerta = { id: 13581 };
      vitest.spyOn(alertaFormService, 'getAlerta').mockReturnValue({ id: null });
      vitest.spyOn(alertaService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ alerta: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(alerta);
      saveSubject.complete();

      // THEN
      expect(alertaFormService.getAlerta).toHaveBeenCalled();
      expect(alertaService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IAlerta>();
      const alerta = { id: 13581 };
      vitest.spyOn(alertaService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ alerta });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(alertaService.update).toHaveBeenCalled();
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

    describe('compareIncidente', () => {
      it('should forward to incidenteService', () => {
        const entity = { id: 31968 };
        const entity2 = { id: 10195 };
        vitest.spyOn(incidenteService, 'compareIncidente');
        comp.compareIncidente(entity, entity2);
        expect(incidenteService.compareIncidente).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
