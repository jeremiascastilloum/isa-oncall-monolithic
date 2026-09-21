import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { IIncidente } from 'app/entities/incidente/incidente.model';
import { IncidenteService } from 'app/entities/incidente/service/incidente.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IPostmortem } from '../postmortem.model';
import { PostmortemService } from '../service/postmortem.service';

import { PostmortemFormGroup, PostmortemFormService } from './postmortem-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-postmortem-update',
  templateUrl: './postmortem-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class PostmortemUpdate implements OnInit {
  readonly isSaving = signal(false);
  postmortem: IPostmortem | null = null;

  incidentesCollection = signal<IIncidente[]>([]);

  protected postmortemService = inject(PostmortemService);
  protected postmortemFormService = inject(PostmortemFormService);
  protected incidenteService = inject(IncidenteService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PostmortemFormGroup = this.postmortemFormService.createPostmortemFormGroup();

  compareIncidente = (o1: IIncidente | null, o2: IIncidente | null): boolean => this.incidenteService.compareIncidente(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ postmortem }) => {
      this.postmortem = postmortem;
      if (postmortem) {
        this.updateForm(postmortem);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const postmortem = this.postmortemFormService.getPostmortem(this.editForm);
    if (postmortem.id === null) {
      this.subscribeToSaveResponse(this.postmortemService.create(postmortem));
    } else {
      this.subscribeToSaveResponse(this.postmortemService.update(postmortem));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IPostmortem | null>): void {
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

  protected updateForm(postmortem: IPostmortem): void {
    this.postmortem = postmortem;
    this.postmortemFormService.resetForm(this.editForm, postmortem);

    this.incidentesCollection.set(
      this.incidenteService.addIncidenteToCollectionIfMissing<IIncidente>(this.incidentesCollection(), postmortem.incidente),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.incidenteService
      .query({ 'postmortemId.specified': 'false' })
      .pipe(map((res: HttpResponse<IIncidente[]>) => res.body ?? []))
      .pipe(
        map((incidentes: IIncidente[]) =>
          this.incidenteService.addIncidenteToCollectionIfMissing<IIncidente>(incidentes, this.postmortem?.incidente),
        ),
      )
      .subscribe((incidentes: IIncidente[]) => this.incidentesCollection.set(incidentes));
  }
}
