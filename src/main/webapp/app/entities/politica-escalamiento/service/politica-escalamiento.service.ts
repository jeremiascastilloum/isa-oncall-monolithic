import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IPoliticaEscalamiento, NewPoliticaEscalamiento } from '../politica-escalamiento.model';

export type PartialUpdatePoliticaEscalamiento = Partial<IPoliticaEscalamiento> & Pick<IPoliticaEscalamiento, 'id'>;

@Injectable()
export class PoliticaEscalamientosService {
  readonly politicaEscalamientosParams = signal<
    Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined
  >(undefined);
  readonly politicaEscalamientosResource = httpResource<IPoliticaEscalamiento[]>(() => {
    const params = this.politicaEscalamientosParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of politicaEscalamiento that have been fetched. It is updated when the politicaEscalamientosResource emits a new value.
   * In case of error while fetching the politicaEscalamientos, the signal is set to an empty array.
   */
  readonly politicaEscalamientos = computed(() =>
    this.politicaEscalamientosResource.hasValue() ? this.politicaEscalamientosResource.value() : [],
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/politica-escalamientos');
}

@Injectable({ providedIn: 'root' })
export class PoliticaEscalamientoService extends PoliticaEscalamientosService {
  protected readonly http = inject(HttpClient);

  create(politicaEscalamiento: NewPoliticaEscalamiento): Observable<IPoliticaEscalamiento> {
    return this.http.post<IPoliticaEscalamiento>(this.resourceUrl, politicaEscalamiento);
  }

  update(politicaEscalamiento: IPoliticaEscalamiento): Observable<IPoliticaEscalamiento> {
    return this.http.put<IPoliticaEscalamiento>(
      `${this.resourceUrl}/${encodeURIComponent(this.getPoliticaEscalamientoIdentifier(politicaEscalamiento))}`,
      politicaEscalamiento,
    );
  }

  partialUpdate(politicaEscalamiento: PartialUpdatePoliticaEscalamiento): Observable<IPoliticaEscalamiento> {
    return this.http.patch<IPoliticaEscalamiento>(
      `${this.resourceUrl}/${encodeURIComponent(this.getPoliticaEscalamientoIdentifier(politicaEscalamiento))}`,
      politicaEscalamiento,
    );
  }

  find(id: number): Observable<IPoliticaEscalamiento> {
    return this.http.get<IPoliticaEscalamiento>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IPoliticaEscalamiento[]>> {
    const options = createRequestOption(req);
    return this.http.get<IPoliticaEscalamiento[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getPoliticaEscalamientoIdentifier(politicaEscalamiento: Pick<IPoliticaEscalamiento, 'id'>): number {
    return politicaEscalamiento.id;
  }

  comparePoliticaEscalamiento(o1: Pick<IPoliticaEscalamiento, 'id'> | null, o2: Pick<IPoliticaEscalamiento, 'id'> | null): boolean {
    return o1 && o2 ? this.getPoliticaEscalamientoIdentifier(o1) === this.getPoliticaEscalamientoIdentifier(o2) : o1 === o2;
  }

  addPoliticaEscalamientoToCollectionIfMissing<Type extends Pick<IPoliticaEscalamiento, 'id'>>(
    politicaEscalamientoCollection: Type[],
    ...politicaEscalamientosToCheck: (Type | null | undefined)[]
  ): Type[] {
    const politicaEscalamientos: Type[] = politicaEscalamientosToCheck.filter(isPresent);
    if (politicaEscalamientos.length > 0) {
      const politicaEscalamientoCollectionIdentifiers = politicaEscalamientoCollection.map(politicaEscalamientoItem =>
        this.getPoliticaEscalamientoIdentifier(politicaEscalamientoItem),
      );
      const politicaEscalamientosToAdd = politicaEscalamientos.filter(politicaEscalamientoItem => {
        const politicaEscalamientoIdentifier = this.getPoliticaEscalamientoIdentifier(politicaEscalamientoItem);
        if (politicaEscalamientoCollectionIdentifiers.includes(politicaEscalamientoIdentifier)) {
          return false;
        }
        politicaEscalamientoCollectionIdentifiers.push(politicaEscalamientoIdentifier);
        return true;
      });
      return [...politicaEscalamientosToAdd, ...politicaEscalamientoCollection];
    }
    return politicaEscalamientoCollection;
  }
}
