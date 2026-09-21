import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IEquipo, NewEquipo } from '../equipo.model';

export type PartialUpdateEquipo = Partial<IEquipo> & Pick<IEquipo, 'id'>;

@Injectable()
export class EquiposService {
  readonly equiposParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly equiposResource = httpResource<IEquipo[]>(() => {
    const params = this.equiposParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of equipo that have been fetched. It is updated when the equiposResource emits a new value.
   * In case of error while fetching the equipos, the signal is set to an empty array.
   */
  readonly equipos = computed(() => (this.equiposResource.hasValue() ? this.equiposResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/equipos');
}

@Injectable({ providedIn: 'root' })
export class EquipoService extends EquiposService {
  protected readonly http = inject(HttpClient);

  create(equipo: NewEquipo): Observable<IEquipo> {
    return this.http.post<IEquipo>(this.resourceUrl, equipo);
  }

  update(equipo: IEquipo): Observable<IEquipo> {
    return this.http.put<IEquipo>(`${this.resourceUrl}/${encodeURIComponent(this.getEquipoIdentifier(equipo))}`, equipo);
  }

  partialUpdate(equipo: PartialUpdateEquipo): Observable<IEquipo> {
    return this.http.patch<IEquipo>(`${this.resourceUrl}/${encodeURIComponent(this.getEquipoIdentifier(equipo))}`, equipo);
  }

  find(id: number): Observable<IEquipo> {
    return this.http.get<IEquipo>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IEquipo[]>> {
    const options = createRequestOption(req);
    return this.http.get<IEquipo[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getEquipoIdentifier(equipo: Pick<IEquipo, 'id'>): number {
    return equipo.id;
  }

  compareEquipo(o1: Pick<IEquipo, 'id'> | null, o2: Pick<IEquipo, 'id'> | null): boolean {
    return o1 && o2 ? this.getEquipoIdentifier(o1) === this.getEquipoIdentifier(o2) : o1 === o2;
  }

  addEquipoToCollectionIfMissing<Type extends Pick<IEquipo, 'id'>>(
    equipoCollection: Type[],
    ...equiposToCheck: (Type | null | undefined)[]
  ): Type[] {
    const equipos: Type[] = equiposToCheck.filter(isPresent);
    if (equipos.length > 0) {
      const equipoCollectionIdentifiers = equipoCollection.map(equipoItem => this.getEquipoIdentifier(equipoItem));
      const equiposToAdd = equipos.filter(equipoItem => {
        const equipoIdentifier = this.getEquipoIdentifier(equipoItem);
        if (equipoCollectionIdentifiers.includes(equipoIdentifier)) {
          return false;
        }
        equipoCollectionIdentifiers.push(equipoIdentifier);
        return true;
      });
      return [...equiposToAdd, ...equipoCollection];
    }
    return equipoCollection;
  }
}
