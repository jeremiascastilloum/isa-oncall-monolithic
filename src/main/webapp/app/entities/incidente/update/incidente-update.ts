import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { EstadoIncidente } from 'app/entities/enumerations/estado-incidente.model';
import { Severidad } from 'app/entities/enumerations/severidad.model';
import { ServicioService } from 'app/entities/servicio/service/servicio.service';
import { IServicio } from 'app/entities/servicio/servicio.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';

import { IIncidente } from '../incidente.model';
import { IncidenteService } from '../service/incidente.service';

import { IncidenteFormGroup, IncidenteFormService } from './incidente-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-incidente-update',
  templateUrl: './incidente-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class IncidenteUpdate implements OnInit {
  readonly isSaving = signal(false);
  incidente: IIncidente | null = null;
  severidadValues = Object.keys(Severidad);
  estadoIncidenteValues = Object.keys(EstadoIncidente);

  usersSharedCollection = signal<IUser[]>([]);
  serviciosSharedCollection = signal<IServicio[]>([]);

  protected incidenteService = inject(IncidenteService);
  protected incidenteFormService = inject(IncidenteFormService);
  protected userService = inject(UserService);
  protected servicioService = inject(ServicioService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: IncidenteFormGroup = this.incidenteFormService.createIncidenteFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareServicio = (o1: IServicio | null, o2: IServicio | null): boolean => this.servicioService.compareServicio(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ incidente }) => {
      this.incidente = incidente;
      if (incidente) {
        this.updateForm(incidente);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const incidente = this.incidenteFormService.getIncidente(this.editForm);
    if (incidente.id === null) {
      this.subscribeToSaveResponse(this.incidenteService.create(incidente));
    } else {
      this.subscribeToSaveResponse(this.incidenteService.update(incidente));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IIncidente | null>): void {
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

  protected updateForm(incidente: IIncidente): void {
    this.incidente = incidente;
    this.incidenteFormService.resetForm(this.editForm, incidente);

    this.usersSharedCollection.update(users => this.userService.addUserToCollectionIfMissing<IUser>(users, incidente.comandante));
    this.serviciosSharedCollection.update(servicios =>
      this.servicioService.addServicioToCollectionIfMissing<IServicio>(servicios, ...(incidente.servicios ?? [])),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.incidente?.comandante)))
      .subscribe((users: IUser[]) => this.usersSharedCollection.set(users));

    this.servicioService
      .query()
      .pipe(map((res: HttpResponse<IServicio[]>) => res.body ?? []))
      .pipe(
        map((servicios: IServicio[]) =>
          this.servicioService.addServicioToCollectionIfMissing<IServicio>(servicios, ...(this.incidente?.servicios ?? [])),
        ),
      )
      .subscribe((servicios: IServicio[]) => this.serviciosSharedCollection.set(servicios));
  }
}
