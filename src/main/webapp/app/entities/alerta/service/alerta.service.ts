import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IAlerta, NewAlerta } from '../alerta.model';

export type PartialUpdateAlerta = Partial<IAlerta> & Pick<IAlerta, 'id'>;

type RestOf<T extends IAlerta | NewAlerta> = Omit<T, 'recibidaEn'> & {
  recibidaEn?: string | null;
};

export type RestAlerta = RestOf<IAlerta>;

export type NewRestAlerta = RestOf<NewAlerta>;

export type PartialUpdateRestAlerta = RestOf<PartialUpdateAlerta>;

@Injectable()
export class AlertasService {
  readonly alertasParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly alertasResource = httpResource<RestAlerta[]>(() => {
    const params = this.alertasParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of alerta that have been fetched. It is updated when the alertasResource emits a new value.
   * In case of error while fetching the alertas, the signal is set to an empty array.
   */
  readonly alertas = computed(() =>
    (this.alertasResource.hasValue() ? this.alertasResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/alertas');

  protected convertValueFromServer(restAlerta: RestAlerta): IAlerta {
    return {
      ...restAlerta,
      recibidaEn: restAlerta.recibidaEn ? dayjs(restAlerta.recibidaEn) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class AlertaService extends AlertasService {
  protected readonly http = inject(HttpClient);

  create(alerta: NewAlerta): Observable<IAlerta> {
    const copy = this.convertValueFromClient(alerta);
    return this.http.post<RestAlerta>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(alerta: IAlerta): Observable<IAlerta> {
    const copy = this.convertValueFromClient(alerta);
    return this.http
      .put<RestAlerta>(`${this.resourceUrl}/${encodeURIComponent(this.getAlertaIdentifier(alerta))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(alerta: PartialUpdateAlerta): Observable<IAlerta> {
    const copy = this.convertValueFromClient(alerta);
    return this.http
      .patch<RestAlerta>(`${this.resourceUrl}/${encodeURIComponent(this.getAlertaIdentifier(alerta))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IAlerta> {
    return this.http.get<RestAlerta>(`${this.resourceUrl}/${encodeURIComponent(id)}`).pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IAlerta[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestAlerta[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getAlertaIdentifier(alerta: Pick<IAlerta, 'id'>): number {
    return alerta.id;
  }

  compareAlerta(o1: Pick<IAlerta, 'id'> | null, o2: Pick<IAlerta, 'id'> | null): boolean {
    return o1 && o2 ? this.getAlertaIdentifier(o1) === this.getAlertaIdentifier(o2) : o1 === o2;
  }

  addAlertaToCollectionIfMissing<Type extends Pick<IAlerta, 'id'>>(
    alertaCollection: Type[],
    ...alertasToCheck: (Type | null | undefined)[]
  ): Type[] {
    const alertas: Type[] = alertasToCheck.filter(isPresent);
    if (alertas.length > 0) {
      const alertaCollectionIdentifiers = alertaCollection.map(alertaItem => this.getAlertaIdentifier(alertaItem));
      const alertasToAdd = alertas.filter(alertaItem => {
        const alertaIdentifier = this.getAlertaIdentifier(alertaItem);
        if (alertaCollectionIdentifiers.includes(alertaIdentifier)) {
          return false;
        }
        alertaCollectionIdentifiers.push(alertaIdentifier);
        return true;
      });
      return [...alertasToAdd, ...alertaCollection];
    }
    return alertaCollection;
  }

  protected convertValueFromClient<T extends IAlerta | NewAlerta | PartialUpdateAlerta>(alerta: T): RestOf<T> {
    return {
      ...alerta,
      recibidaEn: alerta.recibidaEn?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestAlerta): IAlerta {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestAlerta[]): IAlerta[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
