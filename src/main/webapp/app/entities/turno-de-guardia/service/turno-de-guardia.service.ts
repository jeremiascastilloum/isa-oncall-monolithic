import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ITurnoDeGuardia, NewTurnoDeGuardia } from '../turno-de-guardia.model';

export type PartialUpdateTurnoDeGuardia = Partial<ITurnoDeGuardia> & Pick<ITurnoDeGuardia, 'id'>;

type RestOf<T extends ITurnoDeGuardia | NewTurnoDeGuardia> = Omit<T, 'desde' | 'hasta'> & {
  desde?: string | null;
  hasta?: string | null;
};

export type RestTurnoDeGuardia = RestOf<ITurnoDeGuardia>;

export type NewRestTurnoDeGuardia = RestOf<NewTurnoDeGuardia>;

export type PartialUpdateRestTurnoDeGuardia = RestOf<PartialUpdateTurnoDeGuardia>;

@Injectable()
export class TurnoDeGuardiasService {
  readonly turnoDeGuardiasParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly turnoDeGuardiasResource = httpResource<RestTurnoDeGuardia[]>(() => {
    const params = this.turnoDeGuardiasParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of turnoDeGuardia that have been fetched. It is updated when the turnoDeGuardiasResource emits a new value.
   * In case of error while fetching the turnoDeGuardias, the signal is set to an empty array.
   */
  readonly turnoDeGuardias = computed(() =>
    (this.turnoDeGuardiasResource.hasValue() ? this.turnoDeGuardiasResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/turno-de-guardias');

  protected convertValueFromServer(restTurnoDeGuardia: RestTurnoDeGuardia): ITurnoDeGuardia {
    return {
      ...restTurnoDeGuardia,
      desde: restTurnoDeGuardia.desde ? dayjs(restTurnoDeGuardia.desde) : undefined,
      hasta: restTurnoDeGuardia.hasta ? dayjs(restTurnoDeGuardia.hasta) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class TurnoDeGuardiaService extends TurnoDeGuardiasService {
  protected readonly http = inject(HttpClient);

  create(turnoDeGuardia: NewTurnoDeGuardia): Observable<ITurnoDeGuardia> {
    const copy = this.convertValueFromClient(turnoDeGuardia);
    return this.http.post<RestTurnoDeGuardia>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(turnoDeGuardia: ITurnoDeGuardia): Observable<ITurnoDeGuardia> {
    const copy = this.convertValueFromClient(turnoDeGuardia);
    return this.http
      .put<RestTurnoDeGuardia>(`${this.resourceUrl}/${encodeURIComponent(this.getTurnoDeGuardiaIdentifier(turnoDeGuardia))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(turnoDeGuardia: PartialUpdateTurnoDeGuardia): Observable<ITurnoDeGuardia> {
    const copy = this.convertValueFromClient(turnoDeGuardia);
    return this.http
      .patch<RestTurnoDeGuardia>(`${this.resourceUrl}/${encodeURIComponent(this.getTurnoDeGuardiaIdentifier(turnoDeGuardia))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<ITurnoDeGuardia> {
    return this.http
      .get<RestTurnoDeGuardia>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<ITurnoDeGuardia[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestTurnoDeGuardia[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getTurnoDeGuardiaIdentifier(turnoDeGuardia: Pick<ITurnoDeGuardia, 'id'>): number {
    return turnoDeGuardia.id;
  }

  compareTurnoDeGuardia(o1: Pick<ITurnoDeGuardia, 'id'> | null, o2: Pick<ITurnoDeGuardia, 'id'> | null): boolean {
    return o1 && o2 ? this.getTurnoDeGuardiaIdentifier(o1) === this.getTurnoDeGuardiaIdentifier(o2) : o1 === o2;
  }

  addTurnoDeGuardiaToCollectionIfMissing<Type extends Pick<ITurnoDeGuardia, 'id'>>(
    turnoDeGuardiaCollection: Type[],
    ...turnoDeGuardiasToCheck: (Type | null | undefined)[]
  ): Type[] {
    const turnoDeGuardias: Type[] = turnoDeGuardiasToCheck.filter(isPresent);
    if (turnoDeGuardias.length > 0) {
      const turnoDeGuardiaCollectionIdentifiers = turnoDeGuardiaCollection.map(turnoDeGuardiaItem =>
        this.getTurnoDeGuardiaIdentifier(turnoDeGuardiaItem),
      );
      const turnoDeGuardiasToAdd = turnoDeGuardias.filter(turnoDeGuardiaItem => {
        const turnoDeGuardiaIdentifier = this.getTurnoDeGuardiaIdentifier(turnoDeGuardiaItem);
        if (turnoDeGuardiaCollectionIdentifiers.includes(turnoDeGuardiaIdentifier)) {
          return false;
        }
        turnoDeGuardiaCollectionIdentifiers.push(turnoDeGuardiaIdentifier);
        return true;
      });
      return [...turnoDeGuardiasToAdd, ...turnoDeGuardiaCollection];
    }
    return turnoDeGuardiaCollection;
  }

  protected convertValueFromClient<T extends ITurnoDeGuardia | NewTurnoDeGuardia | PartialUpdateTurnoDeGuardia>(
    turnoDeGuardia: T,
  ): RestOf<T> {
    return {
      ...turnoDeGuardia,
      desde: turnoDeGuardia.desde?.toJSON() ?? null,
      hasta: turnoDeGuardia.hasta?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestTurnoDeGuardia): ITurnoDeGuardia {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestTurnoDeGuardia[]): ITurnoDeGuardia[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
