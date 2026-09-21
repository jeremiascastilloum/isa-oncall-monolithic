import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IRotacion, NewRotacion } from '../rotacion.model';

export type PartialUpdateRotacion = Partial<IRotacion> & Pick<IRotacion, 'id'>;

@Injectable()
export class RotacionsService {
  readonly rotacionsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly rotacionsResource = httpResource<IRotacion[]>(() => {
    const params = this.rotacionsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of rotacion that have been fetched. It is updated when the rotacionsResource emits a new value.
   * In case of error while fetching the rotacions, the signal is set to an empty array.
   */
  readonly rotacions = computed(() => (this.rotacionsResource.hasValue() ? this.rotacionsResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/rotacions');
}

@Injectable({ providedIn: 'root' })
export class RotacionService extends RotacionsService {
  protected readonly http = inject(HttpClient);

  create(rotacion: NewRotacion): Observable<IRotacion> {
    return this.http.post<IRotacion>(this.resourceUrl, rotacion);
  }

  update(rotacion: IRotacion): Observable<IRotacion> {
    return this.http.put<IRotacion>(`${this.resourceUrl}/${encodeURIComponent(this.getRotacionIdentifier(rotacion))}`, rotacion);
  }

  partialUpdate(rotacion: PartialUpdateRotacion): Observable<IRotacion> {
    return this.http.patch<IRotacion>(`${this.resourceUrl}/${encodeURIComponent(this.getRotacionIdentifier(rotacion))}`, rotacion);
  }

  find(id: number): Observable<IRotacion> {
    return this.http.get<IRotacion>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IRotacion[]>> {
    const options = createRequestOption(req);
    return this.http.get<IRotacion[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getRotacionIdentifier(rotacion: Pick<IRotacion, 'id'>): number {
    return rotacion.id;
  }

  compareRotacion(o1: Pick<IRotacion, 'id'> | null, o2: Pick<IRotacion, 'id'> | null): boolean {
    return o1 && o2 ? this.getRotacionIdentifier(o1) === this.getRotacionIdentifier(o2) : o1 === o2;
  }

  addRotacionToCollectionIfMissing<Type extends Pick<IRotacion, 'id'>>(
    rotacionCollection: Type[],
    ...rotacionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const rotacions: Type[] = rotacionsToCheck.filter(isPresent);
    if (rotacions.length > 0) {
      const rotacionCollectionIdentifiers = rotacionCollection.map(rotacionItem => this.getRotacionIdentifier(rotacionItem));
      const rotacionsToAdd = rotacions.filter(rotacionItem => {
        const rotacionIdentifier = this.getRotacionIdentifier(rotacionItem);
        if (rotacionCollectionIdentifiers.includes(rotacionIdentifier)) {
          return false;
        }
        rotacionCollectionIdentifiers.push(rotacionIdentifier);
        return true;
      });
      return [...rotacionsToAdd, ...rotacionCollection];
    }
    return rotacionCollection;
  }
}
