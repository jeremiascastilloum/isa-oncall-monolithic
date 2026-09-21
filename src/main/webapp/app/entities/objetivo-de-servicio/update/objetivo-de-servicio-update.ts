import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { Severidad } from 'app/entities/enumerations/severidad.model';
import { TipoObjetivo } from 'app/entities/enumerations/tipo-objetivo.model';
import { ServicioService } from 'app/entities/servicio/service/servicio.service';
import { IServicio } from 'app/entities/servicio/servicio.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IObjetivoDeServicio } from '../objetivo-de-servicio.model';
import { ObjetivoDeServicioService } from '../service/objetivo-de-servicio.service';

import { ObjetivoDeServicioFormGroup, ObjetivoDeServicioFormService } from './objetivo-de-servicio-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-objetivo-de-servicio-update',
  templateUrl: './objetivo-de-servicio-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class ObjetivoDeServicioUpdate implements OnInit {
  readonly isSaving = signal(false);
  objetivoDeServicio: IObjetivoDeServicio | null = null;
  tipoObjetivoValues = Object.keys(TipoObjetivo);
  severidadValues = Object.keys(Severidad);

  serviciosSharedCollection = signal<IServicio[]>([]);

  protected objetivoDeServicioService = inject(ObjetivoDeServicioService);
  protected objetivoDeServicioFormService = inject(ObjetivoDeServicioFormService);
  protected servicioService = inject(ServicioService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ObjetivoDeServicioFormGroup = this.objetivoDeServicioFormService.createObjetivoDeServicioFormGroup();

  compareServicio = (o1: IServicio | null, o2: IServicio | null): boolean => this.servicioService.compareServicio(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ objetivoDeServicio }) => {
      this.objetivoDeServicio = objetivoDeServicio;
      if (objetivoDeServicio) {
        this.updateForm(objetivoDeServicio);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const objetivoDeServicio = this.objetivoDeServicioFormService.getObjetivoDeServicio(this.editForm);
    if (objetivoDeServicio.id === null) {
      this.subscribeToSaveResponse(this.objetivoDeServicioService.create(objetivoDeServicio));
    } else {
      this.subscribeToSaveResponse(this.objetivoDeServicioService.update(objetivoDeServicio));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IObjetivoDeServicio | null>): void {
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

  protected updateForm(objetivoDeServicio: IObjetivoDeServicio): void {
    this.objetivoDeServicio = objetivoDeServicio;
    this.objetivoDeServicioFormService.resetForm(this.editForm, objetivoDeServicio);

    this.serviciosSharedCollection.update(servicios =>
      this.servicioService.addServicioToCollectionIfMissing<IServicio>(servicios, objetivoDeServicio.servicio),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.servicioService
      .query()
      .pipe(map((res: HttpResponse<IServicio[]>) => res.body ?? []))
      .pipe(
        map((servicios: IServicio[]) =>
          this.servicioService.addServicioToCollectionIfMissing<IServicio>(servicios, this.objetivoDeServicio?.servicio),
        ),
      )
      .subscribe((servicios: IServicio[]) => this.serviciosSharedCollection.set(servicios));
  }
}
