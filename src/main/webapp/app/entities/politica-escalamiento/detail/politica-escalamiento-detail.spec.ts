import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { provideTranslateService } from '@ngx-translate/core';
import { of } from 'rxjs';

import { PoliticaEscalamientoDetail } from './politica-escalamiento-detail';

describe('PoliticaEscalamiento Management Detail Component', () => {
  let comp: PoliticaEscalamientoDetail;
  let fixture: ComponentFixture<PoliticaEscalamientoDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideTranslateService(),
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./politica-escalamiento-detail').then(m => m.PoliticaEscalamientoDetail),
              resolve: { politicaEscalamiento: () => of({ id: 17575 }) },
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
    fixture = TestBed.createComponent(PoliticaEscalamientoDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load politicaEscalamiento on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', PoliticaEscalamientoDetail);

      // THEN
      expect(instance.politicaEscalamiento()).toEqual(expect.objectContaining({ id: 17575 }));
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
