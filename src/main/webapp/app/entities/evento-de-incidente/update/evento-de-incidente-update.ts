import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { TipoEvento } from 'app/entities/enumerations/tipo-evento.model';
import { IIncidente } from 'app/entities/incidente/incidente.model';
import { IncidenteService } from 'app/entities/incidente/service/incidente.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IEventoDeIncidente } from '../evento-de-incidente.model';
import { EventoDeIncidenteService } from '../service/evento-de-incidente.service';

import { EventoDeIncidenteFormGroup, EventoDeIncidenteFormService } from './evento-de-incidente-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-evento-de-incidente-update',
  templateUrl: './evento-de-incidente-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class EventoDeIncidenteUpdate implements OnInit {
  readonly isSaving = signal(false);
  eventoDeIncidente: IEventoDeIncidente | null = null;
  tipoEventoValues = Object.keys(TipoEvento);

  incidentesSharedCollection = signal<IIncidente[]>([]);

  protected eventoDeIncidenteService = inject(EventoDeIncidenteService);
  protected eventoDeIncidenteFormService = inject(EventoDeIncidenteFormService);
  protected incidenteService = inject(IncidenteService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: EventoDeIncidenteFormGroup = this.eventoDeIncidenteFormService.createEventoDeIncidenteFormGroup();

  compareIncidente = (o1: IIncidente | null, o2: IIncidente | null): boolean => this.incidenteService.compareIncidente(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ eventoDeIncidente }) => {
      this.eventoDeIncidente = eventoDeIncidente;
      if (eventoDeIncidente) {
        this.updateForm(eventoDeIncidente);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const eventoDeIncidente = this.eventoDeIncidenteFormService.getEventoDeIncidente(this.editForm);
    if (eventoDeIncidente.id === null) {
      this.subscribeToSaveResponse(this.eventoDeIncidenteService.create(eventoDeIncidente));
    } else {
      this.subscribeToSaveResponse(this.eventoDeIncidenteService.update(eventoDeIncidente));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IEventoDeIncidente | null>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving.set(false);
  }

  protected updateForm(eventoDeIncidente: IEventoDeIncidente): void {
    this.eventoDeIncidente = eventoDeIncidente;
    this.eventoDeIncidenteFormService.resetForm(this.editForm, eventoDeIncidente);

    this.incidentesSharedCollection.update(incidentes =>
      this.incidenteService.addIncidenteToCollectionIfMissing<IIncidente>(incidentes, eventoDeIncidente.incidente),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.incidenteService
      .query()
      .pipe(map((res: HttpResponse<IIncidente[]>) => res.body ?? []))
      .pipe(
        map((incidentes: IIncidente[]) =>
          this.incidenteService.addIncidenteToCollectionIfMissing<IIncidente>(incidentes, this.eventoDeIncidente?.incidente),
        ),
      )
      .subscribe((incidentes: IIncidente[]) => this.incidentesSharedCollection.set(incidentes));
  }
}
