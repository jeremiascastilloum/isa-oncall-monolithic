import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ServicioService } from 'app/entities/servicio/service/servicio.service';
import { IServicio } from 'app/entities/servicio/servicio.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { IIncidente } from '../incidente.model';
import { IncidenteService } from '../service/incidente.service';

import { IncidenteFormService } from './incidente-form.service';
import { IncidenteUpdate } from './incidente-update';

describe('Incidente Management Update Component', () => {
  let comp: IncidenteUpdate;
  let fixture: ComponentFixture<IncidenteUpdate>;
  let activatedRoute: ActivatedRoute;
  let incidenteFormService: IncidenteFormService;
  let incidenteService: IncidenteService;
  let userService: UserService;
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

    fixture = TestBed.createComponent(IncidenteUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    incidenteFormService = TestBed.inject(IncidenteFormService);
    incidenteService = TestBed.inject(IncidenteService);
    userService = TestBed.inject(UserService);
    servicioService = TestBed.inject(ServicioService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call User query and add missing value', () => {
      const incidente: IIncidente = { id: 10195 };
      const comandante: IUser = { id: 3944 };
      incidente.comandante = comandante;

      const userCollection: IUser[] = [{ id: 3944 }];
      vitest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [comandante];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      vitest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ incidente });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.usersSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Servicio query and add missing value', () => {
      const incidente: IIncidente = { id: 10195 };
      const servicios: IServicio[] = [{ id: 24037 }];
      incidente.servicios = servicios;

      const servicioCollection: IServicio[] = [{ id: 24037 }];
      vitest.spyOn(servicioService, 'query').mockReturnValue(of(new HttpResponse({ body: servicioCollection })));
      const additionalServicios = [...servicios];
      const expectedCollection: IServicio[] = [...additionalServicios, ...servicioCollection];
      vitest.spyOn(servicioService, 'addServicioToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ incidente });
      comp.ngOnInit();

      expect(servicioService.query).toHaveBeenCalled();
      expect(servicioService.addServicioToCollectionIfMissing).toHaveBeenCalledWith(
        servicioCollection,
        ...additionalServicios.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.serviciosSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const incidente: IIncidente = { id: 10195 };
      const comandante: IUser = { id: 3944 };
      incidente.comandante = comandante;
      const servicio: IServicio = { id: 24037 };
      incidente.servicios = [servicio];

      activatedRoute.data = of({ incidente });
      comp.ngOnInit();

      expect(comp.usersSharedCollection()).toContainEqual(comandante);
      expect(comp.serviciosSharedCollection()).toContainEqual(servicio);
      expect(comp.incidente).toEqual(incidente);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IIncidente>();
      const incidente = { id: 31968 };
      vitest.spyOn(incidenteFormService, 'getIncidente').mockReturnValue(incidente);
      vitest.spyOn(incidenteService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ incidente });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(incidente);
      saveSubject.complete();

      // THEN
      expect(incidenteFormService.getIncidente).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(incidenteService.update).toHaveBeenCalledWith(expect.objectContaining(incidente));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IIncidente>();
      const incidente = { id: 31968 };
      vitest.spyOn(incidenteFormService, 'getIncidente').mockReturnValue({ id: null });
      vitest.spyOn(incidenteService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ incidente: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(incidente);
      saveSubject.complete();

      // THEN
      expect(incidenteFormService.getIncidente).toHaveBeenCalled();
      expect(incidenteService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IIncidente>();
      const incidente = { id: 31968 };
      vitest.spyOn(incidenteService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ incidente });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(incidenteService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('should forward to userService', () => {
        const entity = { id: 3944 };
        const entity2 = { id: 6275 };
        vitest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });

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
