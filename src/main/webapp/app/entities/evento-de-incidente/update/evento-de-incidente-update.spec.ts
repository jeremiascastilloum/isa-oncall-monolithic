import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IIncidente } from 'app/entities/incidente/incidente.model';
import { IncidenteService } from 'app/entities/incidente/service/incidente.service';
import { IEventoDeIncidente } from '../evento-de-incidente.model';
import { EventoDeIncidenteService } from '../service/evento-de-incidente.service';

import { EventoDeIncidenteFormService } from './evento-de-incidente-form.service';
import { EventoDeIncidenteUpdate } from './evento-de-incidente-update';

describe('EventoDeIncidente Management Update Component', () => {
  let comp: EventoDeIncidenteUpdate;
  let fixture: ComponentFixture<EventoDeIncidenteUpdate>;
  let activatedRoute: ActivatedRoute;
  let eventoDeIncidenteFormService: EventoDeIncidenteFormService;
  let eventoDeIncidenteService: EventoDeIncidenteService;
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

    fixture = TestBed.createComponent(EventoDeIncidenteUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    eventoDeIncidenteFormService = TestBed.inject(EventoDeIncidenteFormService);
    eventoDeIncidenteService = TestBed.inject(EventoDeIncidenteService);
    incidenteService = TestBed.inject(IncidenteService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Incidente query and add missing value', () => {
      const eventoDeIncidente: IEventoDeIncidente = { id: 167 };
      const incidente: IIncidente = { id: 31968 };
      eventoDeIncidente.incidente = incidente;

      const incidenteCollection: IIncidente[] = [{ id: 31968 }];
      vitest.spyOn(incidenteService, 'query').mockReturnValue(of(new HttpResponse({ body: incidenteCollection })));
      const additionalIncidentes = [incidente];
      const expectedCollection: IIncidente[] = [...additionalIncidentes, ...incidenteCollection];
      vitest.spyOn(incidenteService, 'addIncidenteToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ eventoDeIncidente });
      comp.ngOnInit();

      expect(incidenteService.query).toHaveBeenCalled();
      expect(incidenteService.addIncidenteToCollectionIfMissing).toHaveBeenCalledWith(
        incidenteCollection,
        ...additionalIncidentes.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.incidentesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const eventoDeIncidente: IEventoDeIncidente = { id: 167 };
      const incidente: IIncidente = { id: 31968 };
      eventoDeIncidente.incidente = incidente;

      activatedRoute.data = of({ eventoDeIncidente });
      comp.ngOnInit();

      expect(comp.incidentesSharedCollection()).toContainEqual(incidente);
      expect(comp.eventoDeIncidente).toEqual(eventoDeIncidente);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IEventoDeIncidente>();
      const eventoDeIncidente = { id: 32466 };
      vitest.spyOn(eventoDeIncidenteFormService, 'getEventoDeIncidente').mockReturnValue(eventoDeIncidente);
      vitest.spyOn(eventoDeIncidenteService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ eventoDeIncidente });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(eventoDeIncidente);
      saveSubject.complete();

      // THEN
      expect(eventoDeIncidenteFormService.getEventoDeIncidente).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(eventoDeIncidenteService.update).toHaveBeenCalledWith(expect.objectContaining(eventoDeIncidente));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IEventoDeIncidente>();
      const eventoDeIncidente = { id: 32466 };
      vitest.spyOn(eventoDeIncidenteFormService, 'getEventoDeIncidente').mockReturnValue({ id: null });
      vitest.spyOn(eventoDeIncidenteService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ eventoDeIncidente: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(eventoDeIncidente);
      saveSubject.complete();

      // THEN
      expect(eventoDeIncidenteFormService.getEventoDeIncidente).toHaveBeenCalled();
      expect(eventoDeIncidenteService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IEventoDeIncidente>();
      const eventoDeIncidente = { id: 32466 };
      vitest.spyOn(eventoDeIncidenteService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ eventoDeIncidente });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(eventoDeIncidenteService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
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
