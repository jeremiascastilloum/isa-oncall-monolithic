import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { Canal } from 'app/entities/enumerations/canal.model';
import { EstadoNotificacion } from 'app/entities/enumerations/estado-notificacion.model';
import { IIncidente } from 'app/entities/incidente/incidente.model';
import { IncidenteService } from 'app/entities/incidente/service/incidente.service';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';

import { INotificacion } from '../notificacion.model';
import { NotificacionService } from '../service/notificacion.service';

import { NotificacionFormGroup, NotificacionFormService } from './notificacion-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-notificacion-update',
  templateUrl: './notificacion-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class NotificacionUpdate implements OnInit {
  readonly isSaving = signal(false);
  notificacion: INotificacion | null = null;
  canalValues = Object.keys(Canal);
  estadoNotificacionValues = Object.keys(EstadoNotificacion);

  incidentesSharedCollection = signal<IIncidente[]>([]);
  usersSharedCollection = signal<IUser[]>([]);

  protected notificacionService = inject(NotificacionService);
  protected notificacionFormService = inject(NotificacionFormService);
  protected incidenteService = inject(IncidenteService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: NotificacionFormGroup = this.notificacionFormService.createNotificacionFormGroup();

  compareIncidente = (o1: IIncidente | null, o2: IIncidente | null): boolean => this.incidenteService.compareIncidente(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ notificacion }) => {
      this.notificacion = notificacion;
      if (notificacion) {
        this.updateForm(notificacion);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const notificacion = this.notificacionFormService.getNotificacion(this.editForm);
    if (notificacion.id === null) {
      this.subscribeToSaveResponse(this.notificacionService.create(notificacion));
    } else {
      this.subscribeToSaveResponse(this.notificacionService.update(notificacion));
    }
  }

  protected subscribeToSaveResponse(result: Observable<INotificacion | null>): void {
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

  protected updateForm(notificacion: INotificacion): void {
    this.notificacion = notificacion;
    this.notificacionFormService.resetForm(this.editForm, notificacion);

    this.incidentesSharedCollection.update(incidentes =>
      this.incidenteService.addIncidenteToCollectionIfMissing<IIncidente>(incidentes, notificacion.incidente),
    );
    this.usersSharedCollection.update(users => this.userService.addUserToCollectionIfMissing<IUser>(users, notificacion.destinatario));
  }

  protected loadRelationshipsOptions(): void {
    this.incidenteService
      .query()
      .pipe(map((res: HttpResponse<IIncidente[]>) => res.body ?? []))
      .pipe(
        map((incidentes: IIncidente[]) =>
          this.incidenteService.addIncidenteToCollectionIfMissing<IIncidente>(incidentes, this.notificacion?.incidente),
        ),
      )
      .subscribe((incidentes: IIncidente[]) => this.incidentesSharedCollection.set(incidentes));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.notificacion?.destinatario)))
      .subscribe((users: IUser[]) => this.usersSharedCollection.set(users));
  }
}
