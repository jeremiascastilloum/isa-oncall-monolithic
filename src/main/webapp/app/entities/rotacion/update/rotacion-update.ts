import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { TipoRotacion } from 'app/entities/enumerations/tipo-rotacion.model';
import { IEquipo } from 'app/entities/equipo/equipo.model';
import { EquipoService } from 'app/entities/equipo/service/equipo.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IRotacion } from '../rotacion.model';
import { RotacionService } from '../service/rotacion.service';

import { RotacionFormGroup, RotacionFormService } from './rotacion-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-rotacion-update',
  templateUrl: './rotacion-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class RotacionUpdate implements OnInit {
  readonly isSaving = signal(false);
  rotacion: IRotacion | null = null;
  tipoRotacionValues = Object.keys(TipoRotacion);

  equiposSharedCollection = signal<IEquipo[]>([]);

  protected rotacionService = inject(RotacionService);
  protected rotacionFormService = inject(RotacionFormService);
  protected equipoService = inject(EquipoService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: RotacionFormGroup = this.rotacionFormService.createRotacionFormGroup();

  compareEquipo = (o1: IEquipo | null, o2: IEquipo | null): boolean => this.equipoService.compareEquipo(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ rotacion }) => {
      this.rotacion = rotacion;
      if (rotacion) {
        this.updateForm(rotacion);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const rotacion = this.rotacionFormService.getRotacion(this.editForm);
    if (rotacion.id === null) {
      this.subscribeToSaveResponse(this.rotacionService.create(rotacion));
    } else {
      this.subscribeToSaveResponse(this.rotacionService.update(rotacion));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IRotacion | null>): void {
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

  protected updateForm(rotacion: IRotacion): void {
    this.rotacion = rotacion;
    this.rotacionFormService.resetForm(this.editForm, rotacion);

    this.equiposSharedCollection.update(equipos => this.equipoService.addEquipoToCollectionIfMissing<IEquipo>(equipos, rotacion.equipo));
  }

  protected loadRelationshipsOptions(): void {
    this.equipoService
      .query()
      .pipe(map((res: HttpResponse<IEquipo[]>) => res.body ?? []))
      .pipe(map((equipos: IEquipo[]) => this.equipoService.addEquipoToCollectionIfMissing<IEquipo>(equipos, this.rotacion?.equipo)))
      .subscribe((equipos: IEquipo[]) => this.equiposSharedCollection.set(equipos));
  }
}
