import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { Canal } from 'app/entities/enumerations/canal.model';
import { IPoliticaEscalamiento } from 'app/entities/politica-escalamiento/politica-escalamiento.model';
import { PoliticaEscalamientoService } from 'app/entities/politica-escalamiento/service/politica-escalamiento.service';
import { IRotacion } from 'app/entities/rotacion/rotacion.model';
import { RotacionService } from 'app/entities/rotacion/service/rotacion.service';
import { IUser } from 'app/entities/user/user.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';

import { IPasoEscalamiento } from '../paso-escalamiento.model';
import { PasoEscalamientoService } from '../service/paso-escalamiento.service';

import { PasoEscalamientoFormGroup, PasoEscalamientoFormService } from './paso-escalamiento-form.service';
import { UserService } from 'app/entities/user/service/user.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-paso-escalamiento-update',
  templateUrl: './paso-escalamiento-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class PasoEscalamientoUpdate implements OnInit {
  readonly isSaving = signal(false);
  pasoEscalamiento: IPasoEscalamiento | null = null;
  canalValues = Object.keys(Canal);

  politicaEscalamientosSharedCollection = signal<IPoliticaEscalamiento[]>([]);
  rotacionsSharedCollection = signal<IRotacion[]>([]);
  usersSharedCollection = signal<IUser[]>([]);

  protected pasoEscalamientoService = inject(PasoEscalamientoService);
  protected pasoEscalamientoFormService = inject(PasoEscalamientoFormService);
  protected politicaEscalamientoService = inject(PoliticaEscalamientoService);
  protected rotacionService = inject(RotacionService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PasoEscalamientoFormGroup = this.pasoEscalamientoFormService.createPasoEscalamientoFormGroup();

  comparePoliticaEscalamiento = (o1: IPoliticaEscalamiento | null, o2: IPoliticaEscalamiento | null): boolean =>
    this.politicaEscalamientoService.comparePoliticaEscalamiento(o1, o2);

  compareRotacion = (o1: IRotacion | null, o2: IRotacion | null): boolean => this.rotacionService.compareRotacion(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ pasoEscalamiento }) => {
      this.pasoEscalamiento = pasoEscalamiento;
      if (pasoEscalamiento) {
        this.updateForm(pasoEscalamiento);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const pasoEscalamiento = this.pasoEscalamientoFormService.getPasoEscalamiento(this.editForm);
    if (pasoEscalamiento.id === null) {
      this.subscribeToSaveResponse(this.pasoEscalamientoService.create(pasoEscalamiento));
    } else {
      this.subscribeToSaveResponse(this.pasoEscalamientoService.update(pasoEscalamiento));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IPasoEscalamiento | null>): void {
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

  protected updateForm(pasoEscalamiento: IPasoEscalamiento): void {
    this.pasoEscalamiento = pasoEscalamiento;
    this.pasoEscalamientoFormService.resetForm(this.editForm, pasoEscalamiento);

    this.politicaEscalamientosSharedCollection.update(politicaEscalamientos =>
      this.politicaEscalamientoService.addPoliticaEscalamientoToCollectionIfMissing<IPoliticaEscalamiento>(
        politicaEscalamientos,
        pasoEscalamiento.politica,
      ),
    );
    this.rotacionsSharedCollection.update(rotacions =>
      this.rotacionService.addRotacionToCollectionIfMissing<IRotacion>(rotacions, pasoEscalamiento.rotacion),
    );
    this.usersSharedCollection.update(users =>
      this.userService.addUserToCollectionIfMissing<IUser>(users, pasoEscalamiento.destinatarioDirecto),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.politicaEscalamientoService
      .query()
      .pipe(map((res: HttpResponse<IPoliticaEscalamiento[]>) => res.body ?? []))
      .pipe(
        map((politicaEscalamientos: IPoliticaEscalamiento[]) =>
          this.politicaEscalamientoService.addPoliticaEscalamientoToCollectionIfMissing<IPoliticaEscalamiento>(
            politicaEscalamientos,
            this.pasoEscalamiento?.politica,
          ),
        ),
      )
      .subscribe((politicaEscalamientos: IPoliticaEscalamiento[]) => this.politicaEscalamientosSharedCollection.set(politicaEscalamientos));

    this.rotacionService
      .query()
      .pipe(map((res: HttpResponse<IRotacion[]>) => res.body ?? []))
      .pipe(
        map((rotacions: IRotacion[]) =>
          this.rotacionService.addRotacionToCollectionIfMissing<IRotacion>(rotacions, this.pasoEscalamiento?.rotacion),
        ),
      )
      .subscribe((rotacions: IRotacion[]) => this.rotacionsSharedCollection.set(rotacions));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(
        map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.pasoEscalamiento?.destinatarioDirecto)),
      )
      .subscribe((users: IUser[]) => this.usersSharedCollection.set(users));
  }
}
