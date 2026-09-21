import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IServicio, NewServicio } from '../servicio.model';

export type PartialUpdateServicio = Partial<IServicio> & Pick<IServicio, 'id'>;

@Injectable()
export class ServiciosService {
  readonly serviciosParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly serviciosResource = httpResource<IServicio[]>(() => {
    const params = this.serviciosParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of servicio that have been fetched. It is updated when the serviciosResource emits a new value.
   * In case of error while fetching the servicios, the signal is set to an empty array.
   */
  readonly servicios = computed(() => (this.serviciosResource.hasValue() ? this.serviciosResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/servicios');
}

@Injectable({ providedIn: 'root' })
export class ServicioService extends ServiciosService {
  protected readonly http = inject(HttpClient);

  create(servicio: NewServicio): Observable<IServicio> {
    return this.http.post<IServicio>(this.resourceUrl, servicio);
  }

  update(servicio: IServicio): Observable<IServicio> {
    return this.http.put<IServicio>(`${this.resourceUrl}/${encodeURIComponent(this.getServicioIdentifier(servicio))}`, servicio);
  }

  partialUpdate(servicio: PartialUpdateServicio): Observable<IServicio> {
    return this.http.patch<IServicio>(`${this.resourceUrl}/${encodeURIComponent(this.getServicioIdentifier(servicio))}`, servicio);
  }

  find(id: number): Observable<IServicio> {
    return this.http.get<IServicio>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IServicio[]>> {
    const options = createRequestOption(req);
    return this.http.get<IServicio[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getServicioIdentifier(servicio: Pick<IServicio, 'id'>): number {
    return servicio.id;
  }

  compareServicio(o1: Pick<IServicio, 'id'> | null, o2: Pick<IServicio, 'id'> | null): boolean {
    return o1 && o2 ? this.getServicioIdentifier(o1) === this.getServicioIdentifier(o2) : o1 === o2;
  }

  addServicioToCollectionIfMissing<Type extends Pick<IServicio, 'id'>>(
    servicioCollection: Type[],
    ...serviciosToCheck: (Type | null | undefined)[]
  ): Type[] {
    const servicios: Type[] = serviciosToCheck.filter(isPresent);
    if (servicios.length > 0) {
      const servicioCollectionIdentifiers = servicioCollection.map(servicioItem => this.getServicioIdentifier(servicioItem));
      const serviciosToAdd = servicios.filter(servicioItem => {
        const servicioIdentifier = this.getServicioIdentifier(servicioItem);
        if (servicioCollectionIdentifiers.includes(servicioIdentifier)) {
          return false;
        }
        servicioCollectionIdentifiers.push(servicioIdentifier);
        return true;
      });
      return [...serviciosToAdd, ...servicioCollection];
    }
    return servicioCollection;
  }
}
