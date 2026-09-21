import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IEquipo } from 'app/entities/equipo/equipo.model';
import { EquipoService } from 'app/entities/equipo/service/equipo.service';
import { IIncidente } from 'app/entities/incidente/incidente.model';
import { IncidenteService } from 'app/entities/incidente/service/incidente.service';
import { ServicioService } from '../service/servicio.service';
import { IServicio } from '../servicio.model';

import { ServicioFormService } from './servicio-form.service';
import { ServicioUpdate } from './servicio-update';

describe('Servicio Management Update Component', () => {
  let comp: ServicioUpdate;
  let fixture: ComponentFixture<ServicioUpdate>;
  let activatedRoute: ActivatedRoute;
  let servicioFormService: ServicioFormService;
  let servicioService: ServicioService;
  let equipoService: EquipoService;
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

    fixture = TestBed.createComponent(ServicioUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    servicioFormService = TestBed.inject(ServicioFormService);
    servicioService = TestBed.inject(ServicioService);
    equipoService = TestBed.inject(EquipoService);
    incidenteService = TestBed.inject(IncidenteService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Equipo query and add missing value', () => {
      const servicio: IServicio = { id: 644 };
      const equipo: IEquipo = { id: 20906 };
      servicio.equipo = equipo;

      const equipoCollection: IEquipo[] = [{ id: 20906 }];
      vitest.spyOn(equipoService, 'query').mockReturnValue(of(new HttpResponse({ body: equipoCollection })));
      const additionalEquipos = [equipo];
      const expectedCollection: IEquipo[] = [...additionalEquipos, ...equipoCollection];
      vitest.spyOn(equipoService, 'addEquipoToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ servicio });
      comp.ngOnInit();

      expect(equipoService.query).toHaveBeenCalled();
      expect(equipoService.addEquipoToCollectionIfMissing).toHaveBeenCalledWith(
        equipoCollection,
        ...additionalEquipos.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.equiposSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Incidente query and add missing value', () => {
      const servicio: IServicio = { id: 644 };
      const incidentes: IIncidente[] = [{ id: 31968 }];
      servicio.incidentes = incidentes;

      const incidenteCollection: IIncidente[] = [{ id: 31968 }];
      vitest.spyOn(incidenteService, 'query').mockReturnValue(of(new HttpResponse({ body: incidenteCollection })));
      const additionalIncidentes = [...incidentes];
      const expectedCollection: IIncidente[] = [...additionalIncidentes, ...incidenteCollection];
      vitest.spyOn(incidenteService, 'addIncidenteToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ servicio });
      comp.ngOnInit();

      expect(incidenteService.query).toHaveBeenCalled();
      expect(incidenteService.addIncidenteToCollectionIfMissing).toHaveBeenCalledWith(
        incidenteCollection,
        ...additionalIncidentes.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.incidentesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const servicio: IServicio = { id: 644 };
      const equipo: IEquipo = { id: 20906 };
      servicio.equipo = equipo;
      const incidente: IIncidente = { id: 31968 };
      servicio.incidentes = [incidente];

      activatedRoute.data = of({ servicio });
      comp.ngOnInit();

      expect(comp.equiposSharedCollection()).toContainEqual(equipo);
      expect(comp.incidentesSharedCollection()).toContainEqual(incidente);
      expect(comp.servicio).toEqual(servicio);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IServicio>();
      const servicio = { id: 24037 };
      vitest.spyOn(servicioFormService, 'getServicio').mockReturnValue(servicio);
      vitest.spyOn(servicioService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ servicio });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(servicio);
      saveSubject.complete();

      // THEN
      expect(servicioFormService.getServicio).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(servicioService.update).toHaveBeenCalledWith(expect.objectContaining(servicio));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IServicio>();
      const servicio = { id: 24037 };
      vitest.spyOn(servicioFormService, 'getServicio').mockReturnValue({ id: null });
      vitest.spyOn(servicioService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ servicio: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(servicio);
      saveSubject.complete();

      // THEN
      expect(servicioFormService.getServicio).toHaveBeenCalled();
      expect(servicioService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IServicio>();
      const servicio = { id: 24037 };
      vitest.spyOn(servicioService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ servicio });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(servicioService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareEquipo', () => {
      it('should forward to equipoService', () => {
        const entity = { id: 20906 };
        const entity2 = { id: 21995 };
        vitest.spyOn(equipoService, 'compareEquipo');
        comp.compareEquipo(entity, entity2);
        expect(equipoService.compareEquipo).toHaveBeenCalledWith(entity, entity2);
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
