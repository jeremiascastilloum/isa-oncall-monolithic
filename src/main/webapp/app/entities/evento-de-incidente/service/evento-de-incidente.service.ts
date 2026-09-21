import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IEventoDeIncidente, NewEventoDeIncidente } from '../evento-de-incidente.model';

export type PartialUpdateEventoDeIncidente = Partial<IEventoDeIncidente> & Pick<IEventoDeIncidente, 'id'>;

type RestOf<T extends IEventoDeIncidente | NewEventoDeIncidente> = Omit<T, 'ocurridoEn'> & {
  ocurridoEn?: string | null;
};

export type RestEventoDeIncidente = RestOf<IEventoDeIncidente>;

export type NewRestEventoDeIncidente = RestOf<NewEventoDeIncidente>;

export type PartialUpdateRestEventoDeIncidente = RestOf<PartialUpdateEventoDeIncidente>;

@Injectable()
export class EventoDeIncidentesService {
  readonly eventoDeIncidentesParams = signal<
    Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined
  >(undefined);
  readonly eventoDeIncidentesResource = httpResource<RestEventoDeIncidente[]>(() => {
    const params = this.eventoDeIncidentesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of eventoDeIncidente that have been fetched. It is updated when the eventoDeIncidentesResource emits a new value.
   * In case of error while fetching the eventoDeIncidentes, the signal is set to an empty array.
   */
  readonly eventoDeIncidentes = computed(() =>
    (this.eventoDeIncidentesResource.hasValue() ? this.eventoDeIncidentesResource.value() : []).map(item =>
      this.convertValueFromServer(item),
    ),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/evento-de-incidentes');

  protected convertValueFromServer(restEventoDeIncidente: RestEventoDeIncidente): IEventoDeIncidente {
    return {
      ...restEventoDeIncidente,
      ocurridoEn: restEventoDeIncidente.ocurridoEn ? dayjs(restEventoDeIncidente.ocurridoEn) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class EventoDeIncidenteService extends EventoDeIncidentesService {
  protected readonly http = inject(HttpClient);

  create(eventoDeIncidente: NewEventoDeIncidente): Observable<IEventoDeIncidente> {
    const copy = this.convertValueFromClient(eventoDeIncidente);
    return this.http.post<RestEventoDeIncidente>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(eventoDeIncidente: IEventoDeIncidente): Observable<IEventoDeIncidente> {
    const copy = this.convertValueFromClient(eventoDeIncidente);
    return this.http
      .put<RestEventoDeIncidente>(`${this.resourceUrl}/${encodeURIComponent(this.getEventoDeIncidenteIdentifier(eventoDeIncidente))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(eventoDeIncidente: PartialUpdateEventoDeIncidente): Observable<IEventoDeIncidente> {
    const copy = this.convertValueFromClient(eventoDeIncidente);
    return this.http
      .patch<RestEventoDeIncidente>(
        `${this.resourceUrl}/${encodeURIComponent(this.getEventoDeIncidenteIdentifier(eventoDeIncidente))}`,
        copy,
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IEventoDeIncidente> {
    return this.http
      .get<RestEventoDeIncidente>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IEventoDeIncidente[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestEventoDeIncidente[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getEventoDeIncidenteIdentifier(eventoDeIncidente: Pick<IEventoDeIncidente, 'id'>): number {
    return eventoDeIncidente.id;
  }

  compareEventoDeIncidente(o1: Pick<IEventoDeIncidente, 'id'> | null, o2: Pick<IEventoDeIncidente, 'id'> | null): boolean {
    return o1 && o2 ? this.getEventoDeIncidenteIdentifier(o1) === this.getEventoDeIncidenteIdentifier(o2) : o1 === o2;
  }

  addEventoDeIncidenteToCollectionIfMissing<Type extends Pick<IEventoDeIncidente, 'id'>>(
    eventoDeIncidenteCollection: Type[],
    ...eventoDeIncidentesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const eventoDeIncidentes: Type[] = eventoDeIncidentesToCheck.filter(isPresent);
    if (eventoDeIncidentes.length > 0) {
      const eventoDeIncidenteCollectionIdentifiers = eventoDeIncidenteCollection.map(eventoDeIncidenteItem =>
        this.getEventoDeIncidenteIdentifier(eventoDeIncidenteItem),
      );
      const eventoDeIncidentesToAdd = eventoDeIncidentes.filter(eventoDeIncidenteItem => {
        const eventoDeIncidenteIdentifier = this.getEventoDeIncidenteIdentifier(eventoDeIncidenteItem);
        if (eventoDeIncidenteCollectionIdentifiers.includes(eventoDeIncidenteIdentifier)) {
          return false;
        }
        eventoDeIncidenteCollectionIdentifiers.push(eventoDeIncidenteIdentifier);
        return true;
      });
      return [...eventoDeIncidentesToAdd, ...eventoDeIncidenteCollection];
    }
    return eventoDeIncidenteCollection;
  }

  protected convertValueFromClient<T extends IEventoDeIncidente | NewEventoDeIncidente | PartialUpdateEventoDeIncidente>(
    eventoDeIncidente: T,
  ): RestOf<T> {
    return {
      ...eventoDeIncidente,
      ocurridoEn: eventoDeIncidente.ocurridoEn?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestEventoDeIncidente): IEventoDeIncidente {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestEventoDeIncidente[]): IEventoDeIncidente[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
