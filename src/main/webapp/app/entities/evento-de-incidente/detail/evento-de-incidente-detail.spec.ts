import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { provideTranslateService } from '@ngx-translate/core';
import { of } from 'rxjs';

import { EventoDeIncidenteDetail } from './evento-de-incidente-detail';

describe('EventoDeIncidente Management Detail Component', () => {
  let comp: EventoDeIncidenteDetail;
  let fixture: ComponentFixture<EventoDeIncidenteDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideTranslateService(),
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./evento-de-incidente-detail').then(m => m.EventoDeIncidenteDetail),
              resolve: { eventoDeIncidente: () => of({ id: 32466 }) },
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
    fixture = TestBed.createComponent(EventoDeIncidenteDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load eventoDeIncidente on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', EventoDeIncidenteDetail);

      // THEN
      expect(instance.eventoDeIncidente()).toEqual(expect.objectContaining({ id: 32466 }));
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
