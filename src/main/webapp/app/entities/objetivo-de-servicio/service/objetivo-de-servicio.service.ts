import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IObjetivoDeServicio, NewObjetivoDeServicio } from '../objetivo-de-servicio.model';

export type PartialUpdateObjetivoDeServicio = Partial<IObjetivoDeServicio> & Pick<IObjetivoDeServicio, 'id'>;

@Injectable()
export class ObjetivoDeServiciosService {
  readonly objetivoDeServiciosParams = signal<
    Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined
  >(undefined);
  readonly objetivoDeServiciosResource = httpResource<IObjetivoDeServicio[]>(() => {
    const params = this.objetivoDeServiciosParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of objetivoDeServicio that have been fetched. It is updated when the objetivoDeServiciosResource emits a new value.
   * In case of error while fetching the objetivoDeServicios, the signal is set to an empty array.
   */
  readonly objetivoDeServicios = computed(() =>
    this.objetivoDeServiciosResource.hasValue() ? this.objetivoDeServiciosResource.value() : [],
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/objetivo-de-servicios');
}

@Injectable({ providedIn: 'root' })
export class ObjetivoDeServicioService extends ObjetivoDeServiciosService {
  protected readonly http = inject(HttpClient);

  create(objetivoDeServicio: NewObjetivoDeServicio): Observable<IObjetivoDeServicio> {
    return this.http.post<IObjetivoDeServicio>(this.resourceUrl, objetivoDeServicio);
  }

  update(objetivoDeServicio: IObjetivoDeServicio): Observable<IObjetivoDeServicio> {
    return this.http.put<IObjetivoDeServicio>(
      `${this.resourceUrl}/${encodeURIComponent(this.getObjetivoDeServicioIdentifier(objetivoDeServicio))}`,
      objetivoDeServicio,
    );
  }

  partialUpdate(objetivoDeServicio: PartialUpdateObjetivoDeServicio): Observable<IObjetivoDeServicio> {
    return this.http.patch<IObjetivoDeServicio>(
      `${this.resourceUrl}/${encodeURIComponent(this.getObjetivoDeServicioIdentifier(objetivoDeServicio))}`,
      objetivoDeServicio,
    );
  }

  find(id: number): Observable<IObjetivoDeServicio> {
    return this.http.get<IObjetivoDeServicio>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IObjetivoDeServicio[]>> {
    const options = createRequestOption(req);
    return this.http.get<IObjetivoDeServicio[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getObjetivoDeServicioIdentifier(objetivoDeServicio: Pick<IObjetivoDeServicio, 'id'>): number {
    return objetivoDeServicio.id;
  }

  compareObjetivoDeServicio(o1: Pick<IObjetivoDeServicio, 'id'> | null, o2: Pick<IObjetivoDeServicio, 'id'> | null): boolean {
    return o1 && o2 ? this.getObjetivoDeServicioIdentifier(o1) === this.getObjetivoDeServicioIdentifier(o2) : o1 === o2;
  }

  addObjetivoDeServicioToCollectionIfMissing<Type extends Pick<IObjetivoDeServicio, 'id'>>(
    objetivoDeServicioCollection: Type[],
    ...objetivoDeServiciosToCheck: (Type | null | undefined)[]
  ): Type[] {
    const objetivoDeServicios: Type[] = objetivoDeServiciosToCheck.filter(isPresent);
    if (objetivoDeServicios.length > 0) {
      const objetivoDeServicioCollectionIdentifiers = objetivoDeServicioCollection.map(objetivoDeServicioItem =>
        this.getObjetivoDeServicioIdentifier(objetivoDeServicioItem),
      );
      const objetivoDeServiciosToAdd = objetivoDeServicios.filter(objetivoDeServicioItem => {
        const objetivoDeServicioIdentifier = this.getObjetivoDeServicioIdentifier(objetivoDeServicioItem);
        if (objetivoDeServicioCollectionIdentifiers.includes(objetivoDeServicioIdentifier)) {
          return false;
        }
        objetivoDeServicioCollectionIdentifiers.push(objetivoDeServicioIdentifier);
        return true;
      });
      return [...objetivoDeServiciosToAdd, ...objetivoDeServicioCollection];
    }
    return objetivoDeServicioCollection;
  }
}
