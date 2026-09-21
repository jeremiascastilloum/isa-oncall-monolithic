import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { provideTranslateService } from '@ngx-translate/core';
import { of } from 'rxjs';

import { AccionCorrectivaDetail } from './accion-correctiva-detail';

describe('AccionCorrectiva Management Detail Component', () => {
  let comp: AccionCorrectivaDetail;
  let fixture: ComponentFixture<AccionCorrectivaDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideTranslateService(),
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./accion-correctiva-detail').then(m => m.AccionCorrectivaDetail),
              resolve: { accionCorrectiva: () => of({ id: 26689 }) },
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
    fixture = TestBed.createComponent(AccionCorrectivaDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load accionCorrectiva on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', AccionCorrectivaDetail);

      // THEN
      expect(instance.accionCorrectiva()).toEqual(expect.objectContaining({ id: 26689 }));
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
