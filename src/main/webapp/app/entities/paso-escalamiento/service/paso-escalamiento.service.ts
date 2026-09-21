import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IPasoEscalamiento, NewPasoEscalamiento } from '../paso-escalamiento.model';

export type PartialUpdatePasoEscalamiento = Partial<IPasoEscalamiento> & Pick<IPasoEscalamiento, 'id'>;

@Injectable()
export class PasoEscalamientosService {
  readonly pasoEscalamientosParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly pasoEscalamientosResource = httpResource<IPasoEscalamiento[]>(() => {
    const params = this.pasoEscalamientosParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of pasoEscalamiento that have been fetched. It is updated when the pasoEscalamientosResource emits a new value.
   * In case of error while fetching the pasoEscalamientos, the signal is set to an empty array.
   */
  readonly pasoEscalamientos = computed(() => (this.pasoEscalamientosResource.hasValue() ? this.pasoEscalamientosResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/paso-escalamientos');
}

@Injectable({ providedIn: 'root' })
export class PasoEscalamientoService extends PasoEscalamientosService {
  protected readonly http = inject(HttpClient);

  create(pasoEscalamiento: NewPasoEscalamiento): Observable<IPasoEscalamiento> {
    return this.http.post<IPasoEscalamiento>(this.resourceUrl, pasoEscalamiento);
  }

  update(pasoEscalamiento: IPasoEscalamiento): Observable<IPasoEscalamiento> {
    return this.http.put<IPasoEscalamiento>(
      `${this.resourceUrl}/${encodeURIComponent(this.getPasoEscalamientoIdentifier(pasoEscalamiento))}`,
      pasoEscalamiento,
    );
  }

  partialUpdate(pasoEscalamiento: PartialUpdatePasoEscalamiento): Observable<IPasoEscalamiento> {
    return this.http.patch<IPasoEscalamiento>(
      `${this.resourceUrl}/${encodeURIComponent(this.getPasoEscalamientoIdentifier(pasoEscalamiento))}`,
      pasoEscalamiento,
    );
  }

  find(id: number): Observable<IPasoEscalamiento> {
    return this.http.get<IPasoEscalamiento>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IPasoEscalamiento[]>> {
    const options = createRequestOption(req);
    return this.http.get<IPasoEscalamiento[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getPasoEscalamientoIdentifier(pasoEscalamiento: Pick<IPasoEscalamiento, 'id'>): number {
    return pasoEscalamiento.id;
  }

  comparePasoEscalamiento(o1: Pick<IPasoEscalamiento, 'id'> | null, o2: Pick<IPasoEscalamiento, 'id'> | null): boolean {
    return o1 && o2 ? this.getPasoEscalamientoIdentifier(o1) === this.getPasoEscalamientoIdentifier(o2) : o1 === o2;
  }

  addPasoEscalamientoToCollectionIfMissing<Type extends Pick<IPasoEscalamiento, 'id'>>(
    pasoEscalamientoCollection: Type[],
    ...pasoEscalamientosToCheck: (Type | null | undefined)[]
  ): Type[] {
    const pasoEscalamientos: Type[] = pasoEscalamientosToCheck.filter(isPresent);
    if (pasoEscalamientos.length > 0) {
      const pasoEscalamientoCollectionIdentifiers = pasoEscalamientoCollection.map(pasoEscalamientoItem =>
        this.getPasoEscalamientoIdentifier(pasoEscalamientoItem),
      );
      const pasoEscalamientosToAdd = pasoEscalamientos.filter(pasoEscalamientoItem => {
        const pasoEscalamientoIdentifier = this.getPasoEscalamientoIdentifier(pasoEscalamientoItem);
        if (pasoEscalamientoCollectionIdentifiers.includes(pasoEscalamientoIdentifier)) {
          return false;
        }
        pasoEscalamientoCollectionIdentifiers.push(pasoEscalamientoIdentifier);
        return true;
      });
      return [...pasoEscalamientosToAdd, ...pasoEscalamientoCollection];
    }
    return pasoEscalamientoCollection;
  }
}
