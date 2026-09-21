import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { provideTranslateService } from '@ngx-translate/core';
import { of } from 'rxjs';

import { PasoEscalamientoDetail } from './paso-escalamiento-detail';

describe('PasoEscalamiento Management Detail Component', () => {
  let comp: PasoEscalamientoDetail;
  let fixture: ComponentFixture<PasoEscalamientoDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideTranslateService(),
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./paso-escalamiento-detail').then(m => m.PasoEscalamientoDetail),
              resolve: { pasoEscalamiento: () => of({ id: 11127 }) },
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
    fixture = TestBed.createComponent(PasoEscalamientoDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load pasoEscalamiento on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', PasoEscalamientoDetail);

      // THEN
      expect(instance.pasoEscalamiento()).toEqual(expect.objectContaining({ id: 11127 }));
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
