import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, finalize } from 'rxjs';

import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IEquipo } from '../equipo.model';
import { EquipoService } from '../service/equipo.service';

import { EquipoFormGroup, EquipoFormService } from './equipo-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-equipo-update',
  templateUrl: './equipo-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class EquipoUpdate implements OnInit {
  readonly isSaving = signal(false);
  equipo: IEquipo | null = null;

  protected equipoService = inject(EquipoService);
  protected equipoFormService = inject(EquipoFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: EquipoFormGroup = this.equipoFormService.createEquipoFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ equipo }) => {
      this.equipo = equipo;
      if (equipo) {
        this.updateForm(equipo);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const equipo = this.equipoFormService.getEquipo(this.editForm);
    if (equipo.id === null) {
      this.subscribeToSaveResponse(this.equipoService.create(equipo));
    } else {
      this.subscribeToSaveResponse(this.equipoService.update(equipo));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IEquipo | null>): void {
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

  protected updateForm(equipo: IEquipo): void {
    this.equipo = equipo;
    this.equipoFormService.resetForm(this.editForm, equipo);
  }
}
