import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { Criticidad } from 'app/entities/enumerations/criticidad.model';
import { Entorno } from 'app/entities/enumerations/entorno.model';
import { IEquipo } from 'app/entities/equipo/equipo.model';
import { EquipoService } from 'app/entities/equipo/service/equipo.service';
import { IIncidente } from 'app/entities/incidente/incidente.model';
import { IncidenteService } from 'app/entities/incidente/service/incidente.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';

import { ServicioService } from '../service/servicio.service';
import { IServicio } from '../servicio.model';

import { ServicioFormGroup, ServicioFormService } from './servicio-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-servicio-update',
  templateUrl: './servicio-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class ServicioUpdate implements OnInit {
  readonly isSaving = signal(false);
  servicio: IServicio | null = null;
  criticidadValues = Object.keys(Criticidad);
  entornoValues = Object.keys(Entorno);

  equiposSharedCollection = signal<IEquipo[]>([]);
  incidentesSharedCollection = signal<IIncidente[]>([]);

  protected servicioService = inject(ServicioService);
  protected servicioFormService = inject(ServicioFormService);
  protected equipoService = inject(EquipoService);
  protected incidenteService = inject(IncidenteService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ServicioFormGroup = this.servicioFormService.createServicioFormGroup();

  compareEquipo = (o1: IEquipo | null, o2: IEquipo | null): boolean => this.equipoService.compareEquipo(o1, o2);

  compareIncidente = (o1: IIncidente | null, o2: IIncidente | null): boolean => this.incidenteService.compareIncidente(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ servicio }) => {
      this.servicio = servicio;
      if (servicio) {
        this.updateForm(servicio);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const servicio = this.servicioFormService.getServicio(this.editForm);
    if (servicio.id === null) {
      this.subscribeToSaveResponse(this.servicioService.create(servicio));
    } else {
      this.subscribeToSaveResponse(this.servicioService.update(servicio));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IServicio | null>): void {
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

  protected updateForm(servicio: IServicio): void {
    this.servicio = servicio;
    this.servicioFormService.resetForm(this.editForm, servicio);

    this.equiposSharedCollection.update(equipos => this.equipoService.addEquipoToCollectionIfMissing<IEquipo>(equipos, servicio.equipo));
    this.incidentesSharedCollection.update(incidentes =>
      this.incidenteService.addIncidenteToCollectionIfMissing<IIncidente>(incidentes, ...(servicio.incidentes ?? [])),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.equipoService
      .query()
      .pipe(map((res: HttpResponse<IEquipo[]>) => res.body ?? []))
      .pipe(map((equipos: IEquipo[]) => this.equipoService.addEquipoToCollectionIfMissing<IEquipo>(equipos, this.servicio?.equipo)))
      .subscribe((equipos: IEquipo[]) => this.equiposSharedCollection.set(equipos));

    this.incidenteService
      .query()
      .pipe(map((res: HttpResponse<IIncidente[]>) => res.body ?? []))
      .pipe(
        map((incidentes: IIncidente[]) =>
          this.incidenteService.addIncidenteToCollectionIfMissing<IIncidente>(incidentes, ...(this.servicio?.incidentes ?? [])),
        ),
      )
      .subscribe((incidentes: IIncidente[]) => this.incidentesSharedCollection.set(incidentes));
  }
}
