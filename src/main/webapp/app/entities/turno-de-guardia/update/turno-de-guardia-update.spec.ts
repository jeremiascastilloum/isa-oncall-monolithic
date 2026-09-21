import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IRotacion } from 'app/entities/rotacion/rotacion.model';
import { RotacionService } from 'app/entities/rotacion/service/rotacion.service';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { TurnoDeGuardiaService } from '../service/turno-de-guardia.service';
import { ITurnoDeGuardia } from '../turno-de-guardia.model';

import { TurnoDeGuardiaFormService } from './turno-de-guardia-form.service';
import { TurnoDeGuardiaUpdate } from './turno-de-guardia-update';

describe('TurnoDeGuardia Management Update Component', () => {
  let comp: TurnoDeGuardiaUpdate;
  let fixture: ComponentFixture<TurnoDeGuardiaUpdate>;
  let activatedRoute: ActivatedRoute;
  let turnoDeGuardiaFormService: TurnoDeGuardiaFormService;
  let turnoDeGuardiaService: TurnoDeGuardiaService;
  let rotacionService: RotacionService;
  let userService: UserService;

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

    fixture = TestBed.createComponent(TurnoDeGuardiaUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    turnoDeGuardiaFormService = TestBed.inject(TurnoDeGuardiaFormService);
    turnoDeGuardiaService = TestBed.inject(TurnoDeGuardiaService);
    rotacionService = TestBed.inject(RotacionService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Rotacion query and add missing value', () => {
      const turnoDeGuardia: ITurnoDeGuardia = { id: 2676 };
      const rotacion: IRotacion = { id: 23556 };
      turnoDeGuardia.rotacion = rotacion;

      const rotacionCollection: IRotacion[] = [{ id: 23556 }];
      vitest.spyOn(rotacionService, 'query').mockReturnValue(of(new HttpResponse({ body: rotacionCollection })));
      const additionalRotacions = [rotacion];
      const expectedCollection: IRotacion[] = [...additionalRotacions, ...rotacionCollection];
      vitest.spyOn(rotacionService, 'addRotacionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ turnoDeGuardia });
      comp.ngOnInit();

      expect(rotacionService.query).toHaveBeenCalled();
      expect(rotacionService.addRotacionToCollectionIfMissing).toHaveBeenCalledWith(
        rotacionCollection,
        ...additionalRotacions.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.rotacionsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call User query and add missing value', () => {
      const turnoDeGuardia: ITurnoDeGuardia = { id: 2676 };
      const responsable: IUser = { id: 3944 };
      turnoDeGuardia.responsable = responsable;

      const userCollection: IUser[] = [{ id: 3944 }];
      vitest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [responsable];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      vitest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ turnoDeGuardia });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.usersSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const turnoDeGuardia: ITurnoDeGuardia = { id: 2676 };
      const rotacion: IRotacion = { id: 23556 };
      turnoDeGuardia.rotacion = rotacion;
      const responsable: IUser = { id: 3944 };
      turnoDeGuardia.responsable = responsable;

      activatedRoute.data = of({ turnoDeGuardia });
      comp.ngOnInit();

      expect(comp.rotacionsSharedCollection()).toContainEqual(rotacion);
      expect(comp.usersSharedCollection()).toContainEqual(responsable);
      expect(comp.turnoDeGuardia).toEqual(turnoDeGuardia);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ITurnoDeGuardia>();
      const turnoDeGuardia = { id: 28122 };
      vitest.spyOn(turnoDeGuardiaFormService, 'getTurnoDeGuardia').mockReturnValue(turnoDeGuardia);
      vitest.spyOn(turnoDeGuardiaService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ turnoDeGuardia });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(turnoDeGuardia);
      saveSubject.complete();

      // THEN
      expect(turnoDeGuardiaFormService.getTurnoDeGuardia).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(turnoDeGuardiaService.update).toHaveBeenCalledWith(expect.objectContaining(turnoDeGuardia));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ITurnoDeGuardia>();
      const turnoDeGuardia = { id: 28122 };
      vitest.spyOn(turnoDeGuardiaFormService, 'getTurnoDeGuardia').mockReturnValue({ id: null });
      vitest.spyOn(turnoDeGuardiaService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ turnoDeGuardia: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(turnoDeGuardia);
      saveSubject.complete();

      // THEN
      expect(turnoDeGuardiaFormService.getTurnoDeGuardia).toHaveBeenCalled();
      expect(turnoDeGuardiaService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ITurnoDeGuardia>();
      const turnoDeGuardia = { id: 28122 };
      vitest.spyOn(turnoDeGuardiaService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ turnoDeGuardia });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(turnoDeGuardiaService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareRotacion', () => {
      it('should forward to rotacionService', () => {
        const entity = { id: 23556 };
        const entity2 = { id: 23369 };
        vitest.spyOn(rotacionService, 'compareRotacion');
        comp.compareRotacion(entity, entity2);
        expect(rotacionService.compareRotacion).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareUser', () => {
      it('should forward to userService', () => {
        const entity = { id: 3944 };
        const entity2 = { id: 6275 };
        vitest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
