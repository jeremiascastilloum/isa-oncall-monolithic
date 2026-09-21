import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { ServicioService } from 'app/entities/servicio/service/servicio.service';
import { IServicio } from 'app/entities/servicio/servicio.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IPoliticaEscalamiento } from '../politica-escalamiento.model';
import { PoliticaEscalamientoService } from '../service/politica-escalamiento.service';

import { PoliticaEscalamientoFormGroup, PoliticaEscalamientoFormService } from './politica-escalamiento-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-politica-escalamiento-update',
  templateUrl: './politica-escalamiento-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class PoliticaEscalamientoUpdate implements OnInit {
  readonly isSaving = signal(false);
  politicaEscalamiento: IPoliticaEscalamiento | null = null;

  serviciosSharedCollection = signal<IServicio[]>([]);

  protected politicaEscalamientoService = inject(PoliticaEscalamientoService);
  protected politicaEscalamientoFormService = inject(PoliticaEscalamientoFormService);
  protected servicioService = inject(ServicioService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PoliticaEscalamientoFormGroup = this.politicaEscalamientoFormService.createPoliticaEscalamientoFormGroup();

  compareServicio = (o1: IServicio | null, o2: IServicio | null): boolean => this.servicioService.compareServicio(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ politicaEscalamiento }) => {
      this.politicaEscalamiento = politicaEscalamiento;
      if (politicaEscalamiento) {
        this.updateForm(politicaEscalamiento);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const politicaEscalamiento = this.politicaEscalamientoFormService.getPoliticaEscalamiento(this.editForm);
    if (politicaEscalamiento.id === null) {
      this.subscribeToSaveResponse(this.politicaEscalamientoService.create(politicaEscalamiento));
    } else {
      this.subscribeToSaveResponse(this.politicaEscalamientoService.update(politicaEscalamiento));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IPoliticaEscalamiento | null>): void {
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

  protected updateForm(politicaEscalamiento: IPoliticaEscalamiento): void {
    this.politicaEscalamiento = politicaEscalamiento;
    this.politicaEscalamientoFormService.resetForm(this.editForm, politicaEscalamiento);

    this.serviciosSharedCollection.update(servicios =>
      this.servicioService.addServicioToCollectionIfMissing<IServicio>(servicios, politicaEscalamiento.servicio),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.servicioService
      .query()
      .pipe(map((res: HttpResponse<IServicio[]>) => res.body ?? []))
      .pipe(
        map((servicios: IServicio[]) =>
          this.servicioService.addServicioToCollectionIfMissing<IServicio>(servicios, this.politicaEscalamiento?.servicio),
        ),
      )
      .subscribe((servicios: IServicio[]) => this.serviciosSharedCollection.set(servicios));
  }
}
