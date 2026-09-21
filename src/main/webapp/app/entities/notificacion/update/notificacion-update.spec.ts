import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IIncidente } from 'app/entities/incidente/incidente.model';
import { IncidenteService } from 'app/entities/incidente/service/incidente.service';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { INotificacion } from '../notificacion.model';
import { NotificacionService } from '../service/notificacion.service';

import { NotificacionFormService } from './notificacion-form.service';
import { NotificacionUpdate } from './notificacion-update';

describe('Notificacion Management Update Component', () => {
  let comp: NotificacionUpdate;
  let fixture: ComponentFixture<NotificacionUpdate>;
  let activatedRoute: ActivatedRoute;
  let notificacionFormService: NotificacionFormService;
  let notificacionService: NotificacionService;
  let incidenteService: IncidenteService;
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

    fixture = TestBed.createComponent(NotificacionUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    notificacionFormService = TestBed.inject(NotificacionFormService);
    notificacionService = TestBed.inject(NotificacionService);
    incidenteService = TestBed.inject(IncidenteService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Incidente query and add missing value', () => {
      const notificacion: INotificacion = { id: 24975 };
      const incidente: IIncidente = { id: 31968 };
      notificacion.incidente = incidente;

      const incidenteCollection: IIncidente[] = [{ id: 31968 }];
      vitest.spyOn(incidenteService, 'query').mockReturnValue(of(new HttpResponse({ body: incidenteCollection })));
      const additionalIncidentes = [incidente];
      const expectedCollection: IIncidente[] = [...additionalIncidentes, ...incidenteCollection];
      vitest.spyOn(incidenteService, 'addIncidenteToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ notificacion });
      comp.ngOnInit();

      expect(incidenteService.query).toHaveBeenCalled();
      expect(incidenteService.addIncidenteToCollectionIfMissing).toHaveBeenCalledWith(
        incidenteCollection,
        ...additionalIncidentes.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.incidentesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call User query and add missing value', () => {
      const notificacion: INotificacion = { id: 24975 };
      const destinatario: IUser = { id: 3944 };
      notificacion.destinatario = destinatario;

      const userCollection: IUser[] = [{ id: 3944 }];
      vitest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [destinatario];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      vitest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ notificacion });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.usersSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const notificacion: INotificacion = { id: 24975 };
      const incidente: IIncidente = { id: 31968 };
      notificacion.incidente = incidente;
      const destinatario: IUser = { id: 3944 };
      notificacion.destinatario = destinatario;

      activatedRoute.data = of({ notificacion });
      comp.ngOnInit();

      expect(comp.incidentesSharedCollection()).toContainEqual(incidente);
      expect(comp.usersSharedCollection()).toContainEqual(destinatario);
      expect(comp.notificacion).toEqual(notificacion);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<INotificacion>();
      const notificacion = { id: 30327 };
      vitest.spyOn(notificacionFormService, 'getNotificacion').mockReturnValue(notificacion);
      vitest.spyOn(notificacionService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ notificacion });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(notificacion);
      saveSubject.complete();

      // THEN
      expect(notificacionFormService.getNotificacion).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(notificacionService.update).toHaveBeenCalledWith(expect.objectContaining(notificacion));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<INotificacion>();
      const notificacion = { id: 30327 };
      vitest.spyOn(notificacionFormService, 'getNotificacion').mockReturnValue({ id: null });
      vitest.spyOn(notificacionService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ notificacion: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(notificacion);
      saveSubject.complete();

      // THEN
      expect(notificacionFormService.getNotificacion).toHaveBeenCalled();
      expect(notificacionService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<INotificacion>();
      const notificacion = { id: 30327 };
      vitest.spyOn(notificacionService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ notificacion });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(notificacionService.update).toHaveBeenCalled();
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
