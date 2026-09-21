import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IEquipo } from 'app/entities/equipo/equipo.model';
import { EquipoService } from 'app/entities/equipo/service/equipo.service';
import { IRotacion } from '../rotacion.model';
import { RotacionService } from '../service/rotacion.service';

import { RotacionFormService } from './rotacion-form.service';
import { RotacionUpdate } from './rotacion-update';

describe('Rotacion Management Update Component', () => {
  let comp: RotacionUpdate;
  let fixture: ComponentFixture<RotacionUpdate>;
  let activatedRoute: ActivatedRoute;
  let rotacionFormService: RotacionFormService;
  let rotacionService: RotacionService;
  let equipoService: EquipoService;

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

    fixture = TestBed.createComponent(RotacionUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    rotacionFormService = TestBed.inject(RotacionFormService);
    rotacionService = TestBed.inject(RotacionService);
    equipoService = TestBed.inject(EquipoService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Equipo query and add missing value', () => {
      const rotacion: IRotacion = { id: 23369 };
      const equipo: IEquipo = { id: 20906 };
      rotacion.equipo = equipo;

      const equipoCollection: IEquipo[] = [{ id: 20906 }];
      vitest.spyOn(equipoService, 'query').mockReturnValue(of(new HttpResponse({ body: equipoCollection })));
      const additionalEquipos = [equipo];
      const expectedCollection: IEquipo[] = [...additionalEquipos, ...equipoCollection];
      vitest.spyOn(equipoService, 'addEquipoToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ rotacion });
      comp.ngOnInit();

      expect(equipoService.query).toHaveBeenCalled();
      expect(equipoService.addEquipoToCollectionIfMissing).toHaveBeenCalledWith(
        equipoCollection,
        ...additionalEquipos.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.equiposSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const rotacion: IRotacion = { id: 23369 };
      const equipo: IEquipo = { id: 20906 };
      rotacion.equipo = equipo;

      activatedRoute.data = of({ rotacion });
      comp.ngOnInit();

      expect(comp.equiposSharedCollection()).toContainEqual(equipo);
      expect(comp.rotacion).toEqual(rotacion);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IRotacion>();
      const rotacion = { id: 23556 };
      vitest.spyOn(rotacionFormService, 'getRotacion').mockReturnValue(rotacion);
      vitest.spyOn(rotacionService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ rotacion });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(rotacion);
      saveSubject.complete();

      // THEN
      expect(rotacionFormService.getRotacion).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(rotacionService.update).toHaveBeenCalledWith(expect.objectContaining(rotacion));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IRotacion>();
      const rotacion = { id: 23556 };
      vitest.spyOn(rotacionFormService, 'getRotacion').mockReturnValue({ id: null });
      vitest.spyOn(rotacionService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ rotacion: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(rotacion);
      saveSubject.complete();

      // THEN
      expect(rotacionFormService.getRotacion).toHaveBeenCalled();
      expect(rotacionService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IRotacion>();
      const rotacion = { id: 23556 };
      vitest.spyOn(rotacionService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ rotacion });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(rotacionService.update).toHaveBeenCalled();
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
  });
});
