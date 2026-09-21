import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IPostmortem } from 'app/entities/postmortem/postmortem.model';
import { PostmortemService } from 'app/entities/postmortem/service/postmortem.service';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { IAccionCorrectiva } from '../accion-correctiva.model';
import { AccionCorrectivaService } from '../service/accion-correctiva.service';

import { AccionCorrectivaFormService } from './accion-correctiva-form.service';
import { AccionCorrectivaUpdate } from './accion-correctiva-update';

describe('AccionCorrectiva Management Update Component', () => {
  let comp: AccionCorrectivaUpdate;
  let fixture: ComponentFixture<AccionCorrectivaUpdate>;
  let activatedRoute: ActivatedRoute;
  let accionCorrectivaFormService: AccionCorrectivaFormService;
  let accionCorrectivaService: AccionCorrectivaService;
  let postmortemService: PostmortemService;
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

    fixture = TestBed.createComponent(AccionCorrectivaUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    accionCorrectivaFormService = TestBed.inject(AccionCorrectivaFormService);
    accionCorrectivaService = TestBed.inject(AccionCorrectivaService);
    postmortemService = TestBed.inject(PostmortemService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Postmortem query and add missing value', () => {
      const accionCorrectiva: IAccionCorrectiva = { id: 12026 };
      const postmortem: IPostmortem = { id: 24266 };
      accionCorrectiva.postmortem = postmortem;

      const postmortemCollection: IPostmortem[] = [{ id: 24266 }];
      vitest.spyOn(postmortemService, 'query').mockReturnValue(of(new HttpResponse({ body: postmortemCollection })));
      const additionalPostmortems = [postmortem];
      const expectedCollection: IPostmortem[] = [...additionalPostmortems, ...postmortemCollection];
      vitest.spyOn(postmortemService, 'addPostmortemToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ accionCorrectiva });
      comp.ngOnInit();

      expect(postmortemService.query).toHaveBeenCalled();
      expect(postmortemService.addPostmortemToCollectionIfMissing).toHaveBeenCalledWith(
        postmortemCollection,
        ...additionalPostmortems.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.postmortemsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call User query and add missing value', () => {
      const accionCorrectiva: IAccionCorrectiva = { id: 12026 };
      const responsable: IUser = { id: 3944 };
      accionCorrectiva.responsable = responsable;

      const userCollection: IUser[] = [{ id: 3944 }];
      vitest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [responsable];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      vitest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ accionCorrectiva });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.usersSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const accionCorrectiva: IAccionCorrectiva = { id: 12026 };
      const postmortem: IPostmortem = { id: 24266 };
      accionCorrectiva.postmortem = postmortem;
      const responsable: IUser = { id: 3944 };
      accionCorrectiva.responsable = responsable;

      activatedRoute.data = of({ accionCorrectiva });
      comp.ngOnInit();

      expect(comp.postmortemsSharedCollection()).toContainEqual(postmortem);
      expect(comp.usersSharedCollection()).toContainEqual(responsable);
      expect(comp.accionCorrectiva).toEqual(accionCorrectiva);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IAccionCorrectiva>();
      const accionCorrectiva = { id: 26689 };
      vitest.spyOn(accionCorrectivaFormService, 'getAccionCorrectiva').mockReturnValue(accionCorrectiva);
      vitest.spyOn(accionCorrectivaService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ accionCorrectiva });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(accionCorrectiva);
      saveSubject.complete();

      // THEN
      expect(accionCorrectivaFormService.getAccionCorrectiva).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(accionCorrectivaService.update).toHaveBeenCalledWith(expect.objectContaining(accionCorrectiva));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IAccionCorrectiva>();
      const accionCorrectiva = { id: 26689 };
      vitest.spyOn(accionCorrectivaFormService, 'getAccionCorrectiva').mockReturnValue({ id: null });
      vitest.spyOn(accionCorrectivaService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ accionCorrectiva: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(accionCorrectiva);
      saveSubject.complete();

      // THEN
      expect(accionCorrectivaFormService.getAccionCorrectiva).toHaveBeenCalled();
      expect(accionCorrectivaService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IAccionCorrectiva>();
      const accionCorrectiva = { id: 26689 };
      vitest.spyOn(accionCorrectivaService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ accionCorrectiva });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(accionCorrectivaService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('comparePostmortem', () => {
      it('should forward to postmortemService', () => {
        const entity = { id: 24266 };
        const entity2 = { id: 9716 };
        vitest.spyOn(postmortemService, 'comparePostmortem');
        comp.comparePostmortem(entity, entity2);
        expect(postmortemService.comparePostmortem).toHaveBeenCalledWith(entity, entity2);
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
