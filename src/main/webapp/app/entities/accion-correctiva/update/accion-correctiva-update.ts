import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbInputDatepicker } from '@ng-bootstrap/ng-bootstrap/datepicker';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { EstadoAccion } from 'app/entities/enumerations/estado-accion.model';
import { Prioridad } from 'app/entities/enumerations/prioridad.model';
import { IPostmortem } from 'app/entities/postmortem/postmortem.model';
import { PostmortemService } from 'app/entities/postmortem/service/postmortem.service';
import { IUser } from 'app/entities/user/user.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';

import { IAccionCorrectiva } from '../accion-correctiva.model';
import { AccionCorrectivaService } from '../service/accion-correctiva.service';

import { AccionCorrectivaFormGroup, AccionCorrectivaFormService } from './accion-correctiva-form.service';
import { UserService } from 'app/entities/user/service/user.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-accion-correctiva-update',
  templateUrl: './accion-correctiva-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule, NgbInputDatepicker],
})
export class AccionCorrectivaUpdate implements OnInit {
  readonly isSaving = signal(false);
  accionCorrectiva: IAccionCorrectiva | null = null;
  prioridadValues = Object.keys(Prioridad);
  estadoAccionValues = Object.keys(EstadoAccion);

  postmortemsSharedCollection = signal<IPostmortem[]>([]);
  usersSharedCollection = signal<IUser[]>([]);

  protected accionCorrectivaService = inject(AccionCorrectivaService);
  protected accionCorrectivaFormService = inject(AccionCorrectivaFormService);
  protected postmortemService = inject(PostmortemService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: AccionCorrectivaFormGroup = this.accionCorrectivaFormService.createAccionCorrectivaFormGroup();

  comparePostmortem = (o1: IPostmortem | null, o2: IPostmortem | null): boolean => this.postmortemService.comparePostmortem(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ accionCorrectiva }) => {
      this.accionCorrectiva = accionCorrectiva;
      if (accionCorrectiva) {
        this.updateForm(accionCorrectiva);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const accionCorrectiva = this.accionCorrectivaFormService.getAccionCorrectiva(this.editForm);
    if (accionCorrectiva.id === null) {
      this.subscribeToSaveResponse(this.accionCorrectivaService.create(accionCorrectiva));
    } else {
      this.subscribeToSaveResponse(this.accionCorrectivaService.update(accionCorrectiva));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IAccionCorrectiva | null>): void {
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

  protected updateForm(accionCorrectiva: IAccionCorrectiva): void {
    this.accionCorrectiva = accionCorrectiva;
    this.accionCorrectivaFormService.resetForm(this.editForm, accionCorrectiva);

    this.postmortemsSharedCollection.update(postmortems =>
      this.postmortemService.addPostmortemToCollectionIfMissing<IPostmortem>(postmortems, accionCorrectiva.postmortem),
    );
    this.usersSharedCollection.update(users => this.userService.addUserToCollectionIfMissing<IUser>(users, accionCorrectiva.responsable));
  }

  protected loadRelationshipsOptions(): void {
    this.postmortemService
      .query()
      .pipe(map((res: HttpResponse<IPostmortem[]>) => res.body ?? []))
      .pipe(
        map((postmortems: IPostmortem[]) =>
          this.postmortemService.addPostmortemToCollectionIfMissing<IPostmortem>(postmortems, this.accionCorrectiva?.postmortem),
        ),
      )
      .subscribe((postmortems: IPostmortem[]) => this.postmortemsSharedCollection.set(postmortems));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.accionCorrectiva?.responsable)))
      .subscribe((users: IUser[]) => this.usersSharedCollection.set(users));
  }
}
