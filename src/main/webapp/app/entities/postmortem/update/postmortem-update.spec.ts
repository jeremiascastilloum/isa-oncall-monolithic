import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IIncidente } from 'app/entities/incidente/incidente.model';
import { IncidenteService } from 'app/entities/incidente/service/incidente.service';
import { IPostmortem } from '../postmortem.model';
import { PostmortemService } from '../service/postmortem.service';

import { PostmortemFormService } from './postmortem-form.service';
import { PostmortemUpdate } from './postmortem-update';

describe('Postmortem Management Update Component', () => {
  let comp: PostmortemUpdate;
  let fixture: ComponentFixture<PostmortemUpdate>;
  let activatedRoute: ActivatedRoute;
  let postmortemFormService: PostmortemFormService;
  let postmortemService: PostmortemService;
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

    fixture = TestBed.createComponent(PostmortemUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    postmortemFormService = TestBed.inject(PostmortemFormService);
    postmortemService = TestBed.inject(PostmortemService);
    incidenteService = TestBed.inject(IncidenteService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call incidente query and add missing value', () => {
      const postmortem: IPostmortem = { id: 9716 };
      const incidente: IIncidente = { id: 31968 };
      postmortem.incidente = incidente;

      const incidenteCollection: IIncidente[] = [{ id: 31968 }];
      vitest.spyOn(incidenteService, 'query').mockReturnValue(of(new HttpResponse({ body: incidenteCollection })));
      const expectedCollection: IIncidente[] = [incidente, ...incidenteCollection];
      vitest.spyOn(incidenteService, 'addIncidenteToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ postmortem });
      comp.ngOnInit();

      expect(incidenteService.query).toHaveBeenCalled();
      expect(incidenteService.addIncidenteToCollectionIfMissing).toHaveBeenCalledWith(incidenteCollection, incidente);
      expect(comp.incidentesCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const postmortem: IPostmortem = { id: 9716 };
      const incidente: IIncidente = { id: 31968 };
      postmortem.incidente = incidente;

      activatedRoute.data = of({ postmortem });
      comp.ngOnInit();

      expect(comp.incidentesCollection()).toContainEqual(incidente);
      expect(comp.postmortem).toEqual(postmortem);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IPostmortem>();
      const postmortem = { id: 24266 };
      vitest.spyOn(postmortemFormService, 'getPostmortem').mockReturnValue(postmortem);
      vitest.spyOn(postmortemService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ postmortem });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(postmortem);
      saveSubject.complete();

      // THEN
      expect(postmortemFormService.getPostmortem).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(postmortemService.update).toHaveBeenCalledWith(expect.objectContaining(postmortem));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IPostmortem>();
      const postmortem = { id: 24266 };
      vitest.spyOn(postmortemFormService, 'getPostmortem').mockReturnValue({ id: null });
      vitest.spyOn(postmortemService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ postmortem: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(postmortem);
      saveSubject.complete();

      // THEN
      expect(postmortemFormService.getPostmortem).toHaveBeenCalled();
      expect(postmortemService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IPostmortem>();
      const postmortem = { id: 24266 };
      vitest.spyOn(postmortemService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ postmortem });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(postmortemService.update).toHaveBeenCalled();
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
