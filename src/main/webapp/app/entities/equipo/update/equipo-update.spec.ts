import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IEquipo } from '../equipo.model';
import { EquipoService } from '../service/equipo.service';

import { EquipoFormService } from './equipo-form.service';
import { EquipoUpdate } from './equipo-update';

describe('Equipo Management Update Component', () => {
  let comp: EquipoUpdate;
  let fixture: ComponentFixture<EquipoUpdate>;
  let activatedRoute: ActivatedRoute;
  let equipoFormService: EquipoFormService;
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

    fixture = TestBed.createComponent(EquipoUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    equipoFormService = TestBed.inject(EquipoFormService);
    equipoService = TestBed.inject(EquipoService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const equipo: IEquipo = { id: 21995 };

      activatedRoute.data = of({ equipo });
      comp.ngOnInit();

      expect(comp.equipo).toEqual(equipo);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IEquipo>();
      const equipo = { id: 20906 };
      vitest.spyOn(equipoFormService, 'getEquipo').mockReturnValue(equipo);
      vitest.spyOn(equipoService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ equipo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(equipo);
      saveSubject.complete();

      // THEN
      expect(equipoFormService.getEquipo).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(equipoService.update).toHaveBeenCalledWith(expect.objectContaining(equipo));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IEquipo>();
      const equipo = { id: 20906 };
      vitest.spyOn(equipoFormService, 'getEquipo').mockReturnValue({ id: null });
      vitest.spyOn(equipoService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ equipo: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(equipo);
      saveSubject.complete();

      // THEN
      expect(equipoFormService.getEquipo).toHaveBeenCalled();
      expect(equipoService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IEquipo>();
      const equipo = { id: 20906 };
      vitest.spyOn(equipoService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ equipo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(equipoService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
