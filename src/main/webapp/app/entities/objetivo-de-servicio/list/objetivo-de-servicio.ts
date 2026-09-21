import { ChangeDetectionStrategy, Component, OnInit, effect, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Data, ParamMap, Router, RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { TranslatePipe } from '@ngx-translate/core';
import { Subscription, combineLatest, filter, tap } from 'rxjs';

import { DEFAULT_SORT_DATA, ITEM_DELETED_EVENT, SORT } from 'app/config/navigation.constants';
import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { ObjetivoDeServicioDeleteDialog } from '../delete/objetivo-de-servicio-delete-dialog';
import { IObjetivoDeServicio } from '../objetivo-de-servicio.model';
import { ObjetivoDeServicioService } from '../service/objetivo-de-servicio.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-objetivo-de-servicio',
  templateUrl: './objetivo-de-servicio.html',
  imports: [
    RouterLink,
    FormsModule,
    FontAwesomeModule,
    AlertError,
    Alert,
    SortDirective,
    SortByDirective,
    TranslateDirective,
    TranslatePipe,
  ],
})
export class ObjetivoDeServicio implements OnInit {
  subscription: Subscription | null = null;
  readonly objetivoDeServicios = signal<IObjetivoDeServicio[]>([]);

  sortState = sortStateSignal({});

  readonly router = inject(Router);
  protected readonly objetivoDeServicioService = inject(ObjetivoDeServicioService);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly isLoading = this.objetivoDeServicioService.objetivoDeServiciosResource.isLoading;
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);

  constructor() {
    effect(() => {
      this.objetivoDeServicios.set(this.fillComponentAttributesFromResponseBody([...this.objetivoDeServicioService.objetivoDeServicios()]));
    });
  }

  trackId = (item: IObjetivoDeServicio): number => this.objetivoDeServicioService.getObjetivoDeServicioIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => {
          if (this.objetivoDeServicios().length === 0) {
            this.load();
          }
        }),
      )
      .subscribe();
  }

  delete(objetivoDeServicio: IObjetivoDeServicio): void {
    const modalRef = this.modalService.open(ObjetivoDeServicioDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.objetivoDeServicio = objetivoDeServicio;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        tap(() => this.load()),
      )
      .subscribe();
  }

  load(): void {
    this.queryBackend();
  }

  navigateToWithComponentValues(event: SortState): void {
    this.handleNavigation(event);
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    this.sortState.set(this.sortService.parseSortParam(params.get(SORT) ?? data[DEFAULT_SORT_DATA]));
  }

  protected refineData(data: IObjetivoDeServicio[]): IObjetivoDeServicio[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: IObjetivoDeServicio[]): IObjetivoDeServicio[] {
    return this.refineData(data);
  }

  protected queryBackend(): void {
    const queryObject: any = {
      eagerload: true,
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    this.objetivoDeServicioService.objetivoDeServiciosParams.set(queryObject);
  }

  protected handleNavigation(sortState: SortState): void {
    const queryParamsObj = {
      sort: this.sortService.buildSortParam(sortState),
    };

    this.router.navigate(['./'], {
      relativeTo: this.activatedRoute,
      queryParams: queryParamsObj,
    });
  }
}
