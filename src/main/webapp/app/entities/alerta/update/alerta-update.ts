import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { OrigenAlerta } from 'app/entities/enumerations/origen-alerta.model';
import { IIncidente } from 'app/entities/incidente/incidente.model';
import { IncidenteService } from 'app/entities/incidente/service/incidente.service';
import { ServicioService } from 'app/entities/servicio/service/servicio.service';
import { IServicio } from 'app/entities/servicio/servicio.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IAlerta } from '../alerta.model';
import { AlertaService } from '../service/alerta.service';

import { AlertaFormGroup, AlertaFormService } from './alerta-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-alerta-update',
  templateUrl: './alerta-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class AlertaUpdate implements OnInit {
  readonly isSaving = signal(false);
  alerta: IAlerta | null = null;
  origenAlertaValues = Object.keys(OrigenAlerta);

  serviciosSharedCollection = signal<IServicio[]>([]);
  incidentesSharedCollection = signal<IIncidente[]>([]);

  protected alertaService = inject(AlertaService);
  protected alertaFormService = inject(AlertaFormService);
  protected servicioService = inject(ServicioService);
  protected incidenteService = inject(IncidenteService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: AlertaFormGroup = this.alertaFormService.createAlertaFormGroup();

  compareServicio = (o1: IServicio | null, o2: IServicio | null): boolean => this.servicioService.compareServicio(o1, o2);

  compareIncidente = (o1: IIncidente | null, o2: IIncidente | null): boolean => this.incidenteService.compareIncidente(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ alerta }) => {
      this.alerta = alerta;
      if (alerta) {
        this.updateForm(alerta);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const alerta = this.alertaFormService.getAlerta(this.editForm);
    if (alerta.id === null) {
      this.subscribeToSaveResponse(this.alertaService.create(alerta));
    } else {
      this.subscribeToSaveResponse(this.alertaService.update(alerta));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IAlerta | null>): void {
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

  protected updateForm(alerta: IAlerta): void {
    this.alerta = alerta;
    this.alertaFormService.resetForm(this.editForm, alerta);

    this.serviciosSharedCollection.update(servicios =>
      this.servicioService.addServicioToCollectionIfMissing<IServicio>(servicios, alerta.servicio),
    );
    this.incidentesSharedCollection.update(incidentes =>
      this.incidenteService.addIncidenteToCollectionIfMissing<IIncidente>(incidentes, alerta.incidente),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.servicioService
      .query()
      .pipe(map((res: HttpResponse<IServicio[]>) => res.body ?? []))
      .pipe(
        map((servicios: IServicio[]) => this.servicioService.addServicioToCollectionIfMissing<IServicio>(servicios, this.alerta?.servicio)),
      )
      .subscribe((servicios: IServicio[]) => this.serviciosSharedCollection.set(servicios));

    this.incidenteService
      .query()
      .pipe(map((res: HttpResponse<IIncidente[]>) => res.body ?? []))
      .pipe(
        map((incidentes: IIncidente[]) =>
          this.incidenteService.addIncidenteToCollectionIfMissing<IIncidente>(incidentes, this.alerta?.incidente),
        ),
      )
      .subscribe((incidentes: IIncidente[]) => this.incidentesSharedCollection.set(incidentes));
  }
}
