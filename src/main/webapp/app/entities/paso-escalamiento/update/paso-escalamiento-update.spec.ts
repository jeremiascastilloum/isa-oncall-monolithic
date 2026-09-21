import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IPoliticaEscalamiento } from 'app/entities/politica-escalamiento/politica-escalamiento.model';
import { PoliticaEscalamientoService } from 'app/entities/politica-escalamiento/service/politica-escalamiento.service';
import { IRotacion } from 'app/entities/rotacion/rotacion.model';
import { RotacionService } from 'app/entities/rotacion/service/rotacion.service';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { IPasoEscalamiento } from '../paso-escalamiento.model';
import { PasoEscalamientoService } from '../service/paso-escalamiento.service';

import { PasoEscalamientoFormService } from './paso-escalamiento-form.service';
import { PasoEscalamientoUpdate } from './paso-escalamiento-update';

describe('PasoEscalamiento Management Update Component', () => {
  let comp: PasoEscalamientoUpdate;
  let fixture: ComponentFixture<PasoEscalamientoUpdate>;
  let activatedRoute: ActivatedRoute;
  let pasoEscalamientoFormService: PasoEscalamientoFormService;
  let pasoEscalamientoService: PasoEscalamientoService;
  let politicaEscalamientoService: PoliticaEscalamientoService;
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

    fixture = TestBed.createComponent(PasoEscalamientoUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    pasoEscalamientoFormService = TestBed.inject(PasoEscalamientoFormService);
    pasoEscalamientoService = TestBed.inject(PasoEscalamientoService);
    politicaEscalamientoService = TestBed.inject(PoliticaEscalamientoService);
    rotacionService = TestBed.inject(RotacionService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call PoliticaEscalamiento query and add missing value', () => {
      const pasoEscalamiento: IPasoEscalamiento = { id: 32102 };
      const politica: IPoliticaEscalamiento = { id: 17575 };
      pasoEscalamiento.politica = politica;

      const politicaEscalamientoCollection: IPoliticaEscalamiento[] = [{ id: 17575 }];
      vitest.spyOn(politicaEscalamientoService, 'query').mockReturnValue(of(new HttpResponse({ body: politicaEscalamientoCollection })));
      const additionalPoliticaEscalamientos = [politica];
      const expectedCollection: IPoliticaEscalamiento[] = [...additionalPoliticaEscalamientos, ...politicaEscalamientoCollection];
      vitest.spyOn(politicaEscalamientoService, 'addPoliticaEscalamientoToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ pasoEscalamiento });
      comp.ngOnInit();

      expect(politicaEscalamientoService.query).toHaveBeenCalled();
      expect(politicaEscalamientoService.addPoliticaEscalamientoToCollectionIfMissing).toHaveBeenCalledWith(
        politicaEscalamientoCollection,
        ...additionalPoliticaEscalamientos.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.politicaEscalamientosSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Rotacion query and add missing value', () => {
      const pasoEscalamiento: IPasoEscalamiento = { id: 32102 };
      const rotacion: IRotacion = { id: 23556 };
      pasoEscalamiento.rotacion = rotacion;

      const rotacionCollection: IRotacion[] = [{ id: 23556 }];
      vitest.spyOn(rotacionService, 'query').mockReturnValue(of(new HttpResponse({ body: rotacionCollection })));
      const additionalRotacions = [rotacion];
      const expectedCollection: IRotacion[] = [...additionalRotacions, ...rotacionCollection];
      vitest.spyOn(rotacionService, 'addRotacionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ pasoEscalamiento });
      comp.ngOnInit();

      expect(rotacionService.query).toHaveBeenCalled();
      expect(rotacionService.addRotacionToCollectionIfMissing).toHaveBeenCalledWith(
        rotacionCollection,
        ...additionalRotacions.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.rotacionsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call User query and add missing value', () => {
      const pasoEscalamiento: IPasoEscalamiento = { id: 32102 };
      const destinatarioDirecto: IUser = { id: 3944 };
      pasoEscalamiento.destinatarioDirecto = destinatarioDirecto;

      const userCollection: IUser[] = [{ id: 3944 }];
      vitest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [destinatarioDirecto];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      vitest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ pasoEscalamiento });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.usersSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const pasoEscalamiento: IPasoEscalamiento = { id: 32102 };
      const politica: IPoliticaEscalamiento = { id: 17575 };
      pasoEscalamiento.politica = politica;
      const rotacion: IRotacion = { id: 23556 };
      pasoEscalamiento.rotacion = rotacion;
      const destinatarioDirecto: IUser = { id: 3944 };
      pasoEscalamiento.destinatarioDirecto = destinatarioDirecto;

      activatedRoute.data = of({ pasoEscalamiento });
      comp.ngOnInit();

      expect(comp.politicaEscalamientosSharedCollection()).toContainEqual(politica);
      expect(comp.rotacionsSharedCollection()).toContainEqual(rotacion);
      expect(comp.usersSharedCollection()).toContainEqual(destinatarioDirecto);
      expect(comp.pasoEscalamiento).toEqual(pasoEscalamiento);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IPasoEscalamiento>();
      const pasoEscalamiento = { id: 11127 };
      vitest.spyOn(pasoEscalamientoFormService, 'getPasoEscalamiento').mockReturnValue(pasoEscalamiento);
      vitest.spyOn(pasoEscalamientoService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pasoEscalamiento });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(pasoEscalamiento);
      saveSubject.complete();

      // THEN
      expect(pasoEscalamientoFormService.getPasoEscalamiento).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(pasoEscalamientoService.update).toHaveBeenCalledWith(expect.objectContaining(pasoEscalamiento));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IPasoEscalamiento>();
      const pasoEscalamiento = { id: 11127 };
      vitest.spyOn(pasoEscalamientoFormService, 'getPasoEscalamiento').mockReturnValue({ id: null });
      vitest.spyOn(pasoEscalamientoService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pasoEscalamiento: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(pasoEscalamiento);
      saveSubject.complete();

      // THEN
      expect(pasoEscalamientoFormService.getPasoEscalamiento).toHaveBeenCalled();
      expect(pasoEscalamientoService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IPasoEscalamiento>();
      const pasoEscalamiento = { id: 11127 };
      vitest.spyOn(pasoEscalamientoService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pasoEscalamiento });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(pasoEscalamientoService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('comparePoliticaEscalamiento', () => {
      it('should forward to politicaEscalamientoService', () => {
        const entity = { id: 17575 };
        const entity2 = { id: 306 };
        vitest.spyOn(politicaEscalamientoService, 'comparePoliticaEscalamiento');
        comp.comparePoliticaEscalamiento(entity, entity2);
        expect(politicaEscalamientoService.comparePoliticaEscalamiento).toHaveBeenCalledWith(entity, entity2);
      });
    });

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
