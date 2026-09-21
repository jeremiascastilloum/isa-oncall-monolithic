import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { provideTranslateService } from '@ngx-translate/core';
import { of } from 'rxjs';

import { ObjetivoDeServicioDetail } from './objetivo-de-servicio-detail';

describe('ObjetivoDeServicio Management Detail Component', () => {
  let comp: ObjetivoDeServicioDetail;
  let fixture: ComponentFixture<ObjetivoDeServicioDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideTranslateService(),
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./objetivo-de-servicio-detail').then(m => m.ObjetivoDeServicioDetail),
              resolve: { objetivoDeServicio: () => of({ id: 18261 }) },
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
    fixture = TestBed.createComponent(ObjetivoDeServicioDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load objetivoDeServicio on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ObjetivoDeServicioDetail);

      // THEN
      expect(instance.objetivoDeServicio()).toEqual(expect.objectContaining({ id: 18261 }));
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
