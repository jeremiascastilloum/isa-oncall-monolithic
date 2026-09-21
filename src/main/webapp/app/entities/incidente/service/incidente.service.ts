import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IIncidente, NewIncidente } from '../incidente.model';

export type PartialUpdateIncidente = Partial<IIncidente> & Pick<IIncidente, 'id'>;

type RestOf<T extends IIncidente | NewIncidente> = Omit<T, 'detectadoEn' | 'reconocidoEn' | 'mitigadoEn' | 'resueltoEn'> & {
  detectadoEn?: string | null;
  reconocidoEn?: string | null;
  mitigadoEn?: string | null;
  resueltoEn?: string | null;
};

export type RestIncidente = RestOf<IIncidente>;

export type NewRestIncidente = RestOf<NewIncidente>;

export type PartialUpdateRestIncidente = RestOf<PartialUpdateIncidente>;

@Injectable()
export class IncidentesService {
  readonly incidentesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly incidentesResource = httpResource<RestIncidente[]>(() => {
    const params = this.incidentesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of incidente that have been fetched. It is updated when the incidentesResource emits a new value.
   * In case of error while fetching the incidentes, the signal is set to an empty array.
   */
  readonly incidentes = computed(() =>
    (this.incidentesResource.hasValue() ? this.incidentesResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/incidentes');

  protected convertValueFromServer(restIncidente: RestIncidente): IIncidente {
    return {
      ...restIncidente,
      detectadoEn: restIncidente.detectadoEn ? dayjs(restIncidente.detectadoEn) : undefined,
      reconocidoEn: restIncidente.reconocidoEn ? dayjs(restIncidente.reconocidoEn) : undefined,
      mitigadoEn: restIncidente.mitigadoEn ? dayjs(restIncidente.mitigadoEn) : undefined,
      resueltoEn: restIncidente.resueltoEn ? dayjs(restIncidente.resueltoEn) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class IncidenteService extends IncidentesService {
  protected readonly http = inject(HttpClient);

  create(incidente: NewIncidente): Observable<IIncidente> {
    const copy = this.convertValueFromClient(incidente);
    return this.http.post<RestIncidente>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(incidente: IIncidente): Observable<IIncidente> {
    const copy = this.convertValueFromClient(incidente);
    return this.http
      .put<RestIncidente>(`${this.resourceUrl}/${encodeURIComponent(this.getIncidenteIdentifier(incidente))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(incidente: PartialUpdateIncidente): Observable<IIncidente> {
    const copy = this.convertValueFromClient(incidente);
    return this.http
      .patch<RestIncidente>(`${this.resourceUrl}/${encodeURIComponent(this.getIncidenteIdentifier(incidente))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IIncidente> {
    return this.http
      .get<RestIncidente>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IIncidente[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestIncidente[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getIncidenteIdentifier(incidente: Pick<IIncidente, 'id'>): number {
    return incidente.id;
  }

  compareIncidente(o1: Pick<IIncidente, 'id'> | null, o2: Pick<IIncidente, 'id'> | null): boolean {
    return o1 && o2 ? this.getIncidenteIdentifier(o1) === this.getIncidenteIdentifier(o2) : o1 === o2;
  }

  addIncidenteToCollectionIfMissing<Type extends Pick<IIncidente, 'id'>>(
    incidenteCollection: Type[],
    ...incidentesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const incidentes: Type[] = incidentesToCheck.filter(isPresent);
    if (incidentes.length > 0) {
      const incidenteCollectionIdentifiers = incidenteCollection.map(incidenteItem => this.getIncidenteIdentifier(incidenteItem));
      const incidentesToAdd = incidentes.filter(incidenteItem => {
        const incidenteIdentifier = this.getIncidenteIdentifier(incidenteItem);
        if (incidenteCollectionIdentifiers.includes(incidenteIdentifier)) {
          return false;
        }
        incidenteCollectionIdentifiers.push(incidenteIdentifier);
        return true;
      });
      return [...incidentesToAdd, ...incidenteCollection];
    }
    return incidenteCollection;
  }

  protected convertValueFromClient<T extends IIncidente | NewIncidente | PartialUpdateIncidente>(incidente: T): RestOf<T> {
    return {
      ...incidente,
      detectadoEn: incidente.detectadoEn?.toJSON() ?? null,
      reconocidoEn: incidente.reconocidoEn?.toJSON() ?? null,
      mitigadoEn: incidente.mitigadoEn?.toJSON() ?? null,
      resueltoEn: incidente.resueltoEn?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestIncidente): IIncidente {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestIncidente[]): IIncidente[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
