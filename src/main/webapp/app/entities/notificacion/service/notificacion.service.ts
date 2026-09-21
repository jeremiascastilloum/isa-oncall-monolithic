import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { INotificacion, NewNotificacion } from '../notificacion.model';

export type PartialUpdateNotificacion = Partial<INotificacion> & Pick<INotificacion, 'id'>;

type RestOf<T extends INotificacion | NewNotificacion> = Omit<T, 'enviadaEn'> & {
  enviadaEn?: string | null;
};

export type RestNotificacion = RestOf<INotificacion>;

export type NewRestNotificacion = RestOf<NewNotificacion>;

export type PartialUpdateRestNotificacion = RestOf<PartialUpdateNotificacion>;

@Injectable()
export class NotificacionsService {
  readonly notificacionsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly notificacionsResource = httpResource<RestNotificacion[]>(() => {
    const params = this.notificacionsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of notificacion that have been fetched. It is updated when the notificacionsResource emits a new value.
   * In case of error while fetching the notificacions, the signal is set to an empty array.
   */
  readonly notificacions = computed(() =>
    (this.notificacionsResource.hasValue() ? this.notificacionsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/notificacions');

  protected convertValueFromServer(restNotificacion: RestNotificacion): INotificacion {
    return {
      ...restNotificacion,
      enviadaEn: restNotificacion.enviadaEn ? dayjs(restNotificacion.enviadaEn) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class NotificacionService extends NotificacionsService {
  protected readonly http = inject(HttpClient);

  create(notificacion: NewNotificacion): Observable<INotificacion> {
    const copy = this.convertValueFromClient(notificacion);
    return this.http.post<RestNotificacion>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(notificacion: INotificacion): Observable<INotificacion> {
    const copy = this.convertValueFromClient(notificacion);
    return this.http
      .put<RestNotificacion>(`${this.resourceUrl}/${encodeURIComponent(this.getNotificacionIdentifier(notificacion))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(notificacion: PartialUpdateNotificacion): Observable<INotificacion> {
    const copy = this.convertValueFromClient(notificacion);
    return this.http
      .patch<RestNotificacion>(`${this.resourceUrl}/${encodeURIComponent(this.getNotificacionIdentifier(notificacion))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<INotificacion> {
    return this.http
      .get<RestNotificacion>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<INotificacion[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestNotificacion[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getNotificacionIdentifier(notificacion: Pick<INotificacion, 'id'>): number {
    return notificacion.id;
  }

  compareNotificacion(o1: Pick<INotificacion, 'id'> | null, o2: Pick<INotificacion, 'id'> | null): boolean {
    return o1 && o2 ? this.getNotificacionIdentifier(o1) === this.getNotificacionIdentifier(o2) : o1 === o2;
  }

  addNotificacionToCollectionIfMissing<Type extends Pick<INotificacion, 'id'>>(
    notificacionCollection: Type[],
    ...notificacionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const notificacions: Type[] = notificacionsToCheck.filter(isPresent);
    if (notificacions.length > 0) {
      const notificacionCollectionIdentifiers = notificacionCollection.map(notificacionItem =>
        this.getNotificacionIdentifier(notificacionItem),
      );
      const notificacionsToAdd = notificacions.filter(notificacionItem => {
        const notificacionIdentifier = this.getNotificacionIdentifier(notificacionItem);
        if (notificacionCollectionIdentifiers.includes(notificacionIdentifier)) {
          return false;
        }
        notificacionCollectionIdentifiers.push(notificacionIdentifier);
        return true;
      });
      return [...notificacionsToAdd, ...notificacionCollection];
    }
    return notificacionCollection;
  }

  protected convertValueFromClient<T extends INotificacion | NewNotificacion | PartialUpdateNotificacion>(notificacion: T): RestOf<T> {
    return {
      ...notificacion,
      enviadaEn: notificacion.enviadaEn?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestNotificacion): INotificacion {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestNotificacion[]): INotificacion[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
