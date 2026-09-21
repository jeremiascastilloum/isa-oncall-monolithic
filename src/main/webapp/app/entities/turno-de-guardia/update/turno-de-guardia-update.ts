import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { IRotacion } from 'app/entities/rotacion/rotacion.model';
import { RotacionService } from 'app/entities/rotacion/service/rotacion.service';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { TurnoDeGuardiaService } from '../service/turno-de-guardia.service';
import { ITurnoDeGuardia } from '../turno-de-guardia.model';

import { TurnoDeGuardiaFormGroup, TurnoDeGuardiaFormService } from './turno-de-guardia-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-turno-de-guardia-update',
  templateUrl: './turno-de-guardia-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class TurnoDeGuardiaUpdate implements OnInit {
  readonly isSaving = signal(false);
  turnoDeGuardia: ITurnoDeGuardia | null = null;

  rotacionsSharedCollection = signal<IRotacion[]>([]);
  usersSharedCollection = signal<IUser[]>([]);

  protected turnoDeGuardiaService = inject(TurnoDeGuardiaService);
  protected turnoDeGuardiaFormService = inject(TurnoDeGuardiaFormService);
  protected rotacionService = inject(RotacionService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TurnoDeGuardiaFormGroup = this.turnoDeGuardiaFormService.createTurnoDeGuardiaFormGroup();

  compareRotacion = (o1: IRotacion | null, o2: IRotacion | null): boolean => this.rotacionService.compareRotacion(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ turnoDeGuardia }) => {
      this.turnoDeGuardia = turnoDeGuardia;
      if (turnoDeGuardia) {
        this.updateForm(turnoDeGuardia);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const turnoDeGuardia = this.turnoDeGuardiaFormService.getTurnoDeGuardia(this.editForm);
    if (turnoDeGuardia.id === null) {
      this.subscribeToSaveResponse(this.turnoDeGuardiaService.create(turnoDeGuardia));
    } else {
      this.subscribeToSaveResponse(this.turnoDeGuardiaService.update(turnoDeGuardia));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ITurnoDeGuardia | null>): void {
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

  protected updateForm(turnoDeGuardia: ITurnoDeGuardia): void {
    this.turnoDeGuardia = turnoDeGuardia;
    this.turnoDeGuardiaFormService.resetForm(this.editForm, turnoDeGuardia);

    this.rotacionsSharedCollection.update(rotacions =>
      this.rotacionService.addRotacionToCollectionIfMissing<IRotacion>(rotacions, turnoDeGuardia.rotacion),
    );
    this.usersSharedCollection.update(users => this.userService.addUserToCollectionIfMissing<IUser>(users, turnoDeGuardia.responsable));
  }

  protected loadRelationshipsOptions(): void {
    this.rotacionService
      .query()
      .pipe(map((res: HttpResponse<IRotacion[]>) => res.body ?? []))
      .pipe(
        map((rotacions: IRotacion[]) =>
          this.rotacionService.addRotacionToCollectionIfMissing<IRotacion>(rotacions, this.turnoDeGuardia?.rotacion),
        ),
      )
      .subscribe((rotacions: IRotacion[]) => this.rotacionsSharedCollection.set(rotacions));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.turnoDeGuardia?.responsable)))
      .subscribe((users: IUser[]) => this.usersSharedCollection.set(users));
  }
}
