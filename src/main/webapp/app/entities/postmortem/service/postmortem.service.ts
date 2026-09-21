import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IPostmortem, NewPostmortem } from '../postmortem.model';

export type PartialUpdatePostmortem = Partial<IPostmortem> & Pick<IPostmortem, 'id'>;

type RestOf<T extends IPostmortem | NewPostmortem> = Omit<T, 'publicadoEn'> & {
  publicadoEn?: string | null;
};

export type RestPostmortem = RestOf<IPostmortem>;

export type NewRestPostmortem = RestOf<NewPostmortem>;

export type PartialUpdateRestPostmortem = RestOf<PartialUpdatePostmortem>;

@Injectable()
export class PostmortemsService {
  readonly postmortemsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly postmortemsResource = httpResource<RestPostmortem[]>(() => {
    const params = this.postmortemsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of postmortem that have been fetched. It is updated when the postmortemsResource emits a new value.
   * In case of error while fetching the postmortems, the signal is set to an empty array.
   */
  readonly postmortems = computed(() =>
    (this.postmortemsResource.hasValue() ? this.postmortemsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/postmortems');

  protected convertValueFromServer(restPostmortem: RestPostmortem): IPostmortem {
    return {
      ...restPostmortem,
      publicadoEn: restPostmortem.publicadoEn ? dayjs(restPostmortem.publicadoEn) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class PostmortemService extends PostmortemsService {
  protected readonly http = inject(HttpClient);

  create(postmortem: NewPostmortem): Observable<IPostmortem> {
    const copy = this.convertValueFromClient(postmortem);
    return this.http.post<RestPostmortem>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(postmortem: IPostmortem): Observable<IPostmortem> {
    const copy = this.convertValueFromClient(postmortem);
    return this.http
      .put<RestPostmortem>(`${this.resourceUrl}/${encodeURIComponent(this.getPostmortemIdentifier(postmortem))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(postmortem: PartialUpdatePostmortem): Observable<IPostmortem> {
    const copy = this.convertValueFromClient(postmortem);
    return this.http
      .patch<RestPostmortem>(`${this.resourceUrl}/${encodeURIComponent(this.getPostmortemIdentifier(postmortem))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IPostmortem> {
    return this.http
      .get<RestPostmortem>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IPostmortem[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestPostmortem[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getPostmortemIdentifier(postmortem: Pick<IPostmortem, 'id'>): number {
    return postmortem.id;
  }

  comparePostmortem(o1: Pick<IPostmortem, 'id'> | null, o2: Pick<IPostmortem, 'id'> | null): boolean {
    return o1 && o2 ? this.getPostmortemIdentifier(o1) === this.getPostmortemIdentifier(o2) : o1 === o2;
  }

  addPostmortemToCollectionIfMissing<Type extends Pick<IPostmortem, 'id'>>(
    postmortemCollection: Type[],
    ...postmortemsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const postmortems: Type[] = postmortemsToCheck.filter(isPresent);
    if (postmortems.length > 0) {
      const postmortemCollectionIdentifiers = postmortemCollection.map(postmortemItem => this.getPostmortemIdentifier(postmortemItem));
      const postmortemsToAdd = postmortems.filter(postmortemItem => {
        const postmortemIdentifier = this.getPostmortemIdentifier(postmortemItem);
        if (postmortemCollectionIdentifiers.includes(postmortemIdentifier)) {
          return false;
        }
        postmortemCollectionIdentifiers.push(postmortemIdentifier);
        return true;
      });
      return [...postmortemsToAdd, ...postmortemCollection];
    }
    return postmortemCollection;
  }

  protected convertValueFromClient<T extends IPostmortem | NewPostmortem | PartialUpdatePostmortem>(postmortem: T): RestOf<T> {
    return {
      ...postmortem,
      publicadoEn: postmortem.publicadoEn?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestPostmortem): IPostmortem {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestPostmortem[]): IPostmortem[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
