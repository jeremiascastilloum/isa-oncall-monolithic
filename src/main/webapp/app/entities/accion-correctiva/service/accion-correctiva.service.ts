import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IAccionCorrectiva, NewAccionCorrectiva } from '../accion-correctiva.model';

export type PartialUpdateAccionCorrectiva = Partial<IAccionCorrectiva> & Pick<IAccionCorrectiva, 'id'>;

type RestOf<T extends IAccionCorrectiva | NewAccionCorrectiva> = Omit<T, 'fechaLimite'> & {
  fechaLimite?: string | null;
};

export type RestAccionCorrectiva = RestOf<IAccionCorrectiva>;

export type NewRestAccionCorrectiva = RestOf<NewAccionCorrectiva>;

export type PartialUpdateRestAccionCorrectiva = RestOf<PartialUpdateAccionCorrectiva>;

@Injectable()
export class AccionCorrectivasService {
  readonly accionCorrectivasParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly accionCorrectivasResource = httpResource<RestAccionCorrectiva[]>(() => {
    const params = this.accionCorrectivasParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of accionCorrectiva that have been fetched. It is updated when the accionCorrectivasResource emits a new value.
   * In case of error while fetching the accionCorrectivas, the signal is set to an empty array.
   */
  readonly accionCorrectivas = computed(() =>
    (this.accionCorrectivasResource.hasValue() ? this.accionCorrectivasResource.value() : []).map(item =>
      this.convertValueFromServer(item),
    ),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/accion-correctivas');

  protected convertValueFromServer(restAccionCorrectiva: RestAccionCorrectiva): IAccionCorrectiva {
    return {
      ...restAccionCorrectiva,
      fechaLimite: restAccionCorrectiva.fechaLimite ? dayjs(restAccionCorrectiva.fechaLimite) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class AccionCorrectivaService extends AccionCorrectivasService {
  protected readonly http = inject(HttpClient);

  create(accionCorrectiva: NewAccionCorrectiva): Observable<IAccionCorrectiva> {
    const copy = this.convertValueFromClient(accionCorrectiva);
    return this.http.post<RestAccionCorrectiva>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(accionCorrectiva: IAccionCorrectiva): Observable<IAccionCorrectiva> {
    const copy = this.convertValueFromClient(accionCorrectiva);
    return this.http
      .put<RestAccionCorrectiva>(`${this.resourceUrl}/${encodeURIComponent(this.getAccionCorrectivaIdentifier(accionCorrectiva))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(accionCorrectiva: PartialUpdateAccionCorrectiva): Observable<IAccionCorrectiva> {
    const copy = this.convertValueFromClient(accionCorrectiva);
    return this.http
      .patch<RestAccionCorrectiva>(`${this.resourceUrl}/${encodeURIComponent(this.getAccionCorrectivaIdentifier(accionCorrectiva))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IAccionCorrectiva> {
    return this.http
      .get<RestAccionCorrectiva>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IAccionCorrectiva[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestAccionCorrectiva[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getAccionCorrectivaIdentifier(accionCorrectiva: Pick<IAccionCorrectiva, 'id'>): number {
    return accionCorrectiva.id;
  }

  compareAccionCorrectiva(o1: Pick<IAccionCorrectiva, 'id'> | null, o2: Pick<IAccionCorrectiva, 'id'> | null): boolean {
    return o1 && o2 ? this.getAccionCorrectivaIdentifier(o1) === this.getAccionCorrectivaIdentifier(o2) : o1 === o2;
  }

  addAccionCorrectivaToCollectionIfMissing<Type extends Pick<IAccionCorrectiva, 'id'>>(
    accionCorrectivaCollection: Type[],
    ...accionCorrectivasToCheck: (Type | null | undefined)[]
  ): Type[] {
    const accionCorrectivas: Type[] = accionCorrectivasToCheck.filter(isPresent);
    if (accionCorrectivas.length > 0) {
      const accionCorrectivaCollectionIdentifiers = accionCorrectivaCollection.map(accionCorrectivaItem =>
        this.getAccionCorrectivaIdentifier(accionCorrectivaItem),
      );
      const accionCorrectivasToAdd = accionCorrectivas.filter(accionCorrectivaItem => {
        const accionCorrectivaIdentifier = this.getAccionCorrectivaIdentifier(accionCorrectivaItem);
        if (accionCorrectivaCollectionIdentifiers.includes(accionCorrectivaIdentifier)) {
          return false;
        }
        accionCorrectivaCollectionIdentifiers.push(accionCorrectivaIdentifier);
        return true;
      });
      return [...accionCorrectivasToAdd, ...accionCorrectivaCollection];
    }
    return accionCorrectivaCollection;
  }

  protected convertValueFromClient<T extends IAccionCorrectiva | NewAccionCorrectiva | PartialUpdateAccionCorrectiva>(
    accionCorrectiva: T,
  ): RestOf<T> {
    return {
      ...accionCorrectiva,
      fechaLimite: accionCorrectiva.fechaLimite?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertResponseFromServer(res: RestAccionCorrectiva): IAccionCorrectiva {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestAccionCorrectiva[]): IAccionCorrectiva[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
