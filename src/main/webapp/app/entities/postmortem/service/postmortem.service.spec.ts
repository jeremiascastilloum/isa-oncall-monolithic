import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IPostmortem } from '../postmortem.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../postmortem.test-samples';

import { PostmortemService, RestPostmortem } from './postmortem.service';

const requireRestSample: RestPostmortem = {
  ...sampleWithRequiredData,
  publicadoEn: sampleWithRequiredData.publicadoEn?.toJSON(),
};

describe('Postmortem Service', () => {
  let service: PostmortemService;
  let httpMock: HttpTestingController;
  let expectedResult: IPostmortem | IPostmortem[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(PostmortemService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Postmortem', () => {
      const postmortem = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(postmortem).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Postmortem', () => {
      const postmortem = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(postmortem).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Postmortem', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Postmortem', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Postmortem', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addPostmortemToCollectionIfMissing', () => {
      it('should add a Postmortem to an empty array', () => {
        const postmortem: IPostmortem = sampleWithRequiredData;
        expectedResult = service.addPostmortemToCollectionIfMissing([], postmortem);
        expect(expectedResult).toEqual([postmortem]);
      });

      it('should not add a Postmortem to an array that contains it', () => {
        const postmortem: IPostmortem = sampleWithRequiredData;
        const postmortemCollection: IPostmortem[] = [
          {
            ...postmortem,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPostmortemToCollectionIfMissing(postmortemCollection, postmortem);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Postmortem to an array that doesn't contain it", () => {
        const postmortem: IPostmortem = sampleWithRequiredData;
        const postmortemCollection: IPostmortem[] = [sampleWithPartialData];
        expectedResult = service.addPostmortemToCollectionIfMissing(postmortemCollection, postmortem);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(postmortem);
      });

      it('should add only unique Postmortem to an array', () => {
        const postmortemArray: IPostmortem[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const postmortemCollection: IPostmortem[] = [sampleWithRequiredData];
        expectedResult = service.addPostmortemToCollectionIfMissing(postmortemCollection, ...postmortemArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const postmortem: IPostmortem = sampleWithRequiredData;
        const postmortem2: IPostmortem = sampleWithPartialData;
        expectedResult = service.addPostmortemToCollectionIfMissing([], postmortem, postmortem2);
        expect(expectedResult).toEqual([postmortem, postmortem2]);
      });

      it('should accept null and undefined values', () => {
        const postmortem: IPostmortem = sampleWithRequiredData;
        expectedResult = service.addPostmortemToCollectionIfMissing([], null, postmortem, undefined);
        expect(expectedResult).toEqual([postmortem]);
      });

      it('should return initial array if no Postmortem is added', () => {
        const postmortemCollection: IPostmortem[] = [sampleWithRequiredData];
        expectedResult = service.addPostmortemToCollectionIfMissing(postmortemCollection, undefined, null);
        expect(expectedResult).toEqual(postmortemCollection);
      });
    });

    describe('comparePostmortem', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePostmortem(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 24266 };
        const entity2 = null;

        const compareResult1 = service.comparePostmortem(entity1, entity2);
        const compareResult2 = service.comparePostmortem(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 24266 };
        const entity2 = { id: 9716 };

        const compareResult1 = service.comparePostmortem(entity1, entity2);
        const compareResult2 = service.comparePostmortem(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 24266 };
        const entity2 = { id: 24266 };

        const compareResult1 = service.comparePostmortem(entity1, entity2);
        const compareResult2 = service.comparePostmortem(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
