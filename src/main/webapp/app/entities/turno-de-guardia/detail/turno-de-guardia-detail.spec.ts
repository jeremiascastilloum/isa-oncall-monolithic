import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { provideTranslateService } from '@ngx-translate/core';
import { of } from 'rxjs';

import { TurnoDeGuardiaDetail } from './turno-de-guardia-detail';

describe('TurnoDeGuardia Management Detail Component', () => {
  let comp: TurnoDeGuardiaDetail;
  let fixture: ComponentFixture<TurnoDeGuardiaDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideTranslateService(),
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./turno-de-guardia-detail').then(m => m.TurnoDeGuardiaDetail),
              resolve: { turnoDeGuardia: () => of({ id: 28122 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    });
    const library = TestBed.inject(FaIconLibrary);
    library.addIcons(faArrowLeft);
    library.addIcons(faPencilAlt);
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TurnoDeGuardiaDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load turnoDeGuardia on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', TurnoDeGuardiaDetail);

      // THEN
      expect(instance.turnoDeGuardia()).toEqual(expect.objectContaining({ id: 28122 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      vitest.spyOn(globalThis.history, 'back');
      comp.previousState();
      expect(globalThis.history.back).toHaveBeenCalled();
    });
  });
});
