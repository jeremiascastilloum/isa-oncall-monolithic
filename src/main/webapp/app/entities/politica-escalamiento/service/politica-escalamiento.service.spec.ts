import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IPoliticaEscalamiento } from '../politica-escalamiento.model';
import {
  sampleWithFullData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithRequiredData,
} from '../politica-escalamiento.test-samples';

import { PoliticaEscalamientoService } from './politica-escalamiento.service';

const requireRestSample: IPoliticaEscalamiento = {
  ...sampleWithRequiredData,
};

describe('PoliticaEscalamiento Service', () => {
  let service: PoliticaEscalamientoService;
  let httpMock: HttpTestingController;
  let expectedResult: IPoliticaEscalamiento | IPoliticaEscalamiento[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(PoliticaEscalamientoService);
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

    it('should create a PoliticaEscalamiento', () => {
      const politicaEscalamiento = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(politicaEscalamiento).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a PoliticaEscalamiento', () => {
      const politicaEscalamiento = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(politicaEscalamiento).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a PoliticaEscalamiento', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of PoliticaEscalamiento', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a PoliticaEscalamiento', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addPoliticaEscalamientoToCollectionIfMissing', () => {
      it('should add a PoliticaEscalamiento to an empty array', () => {
        const politicaEscalamiento: IPoliticaEscalamiento = sampleWithRequiredData;
        expectedResult = service.addPoliticaEscalamientoToCollectionIfMissing([], politicaEscalamiento);
        expect(expectedResult).toEqual([politicaEscalamiento]);
      });

      it('should not add a PoliticaEscalamiento to an array that contains it', () => {
        const politicaEscalamiento: IPoliticaEscalamiento = sampleWithRequiredData;
        const politicaEscalamientoCollection: IPoliticaEscalamiento[] = [
          {
            ...politicaEscalamiento,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPoliticaEscalamientoToCollectionIfMissing(politicaEscalamientoCollection, politicaEscalamiento);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a PoliticaEscalamiento to an array that doesn't contain it", () => {
        const politicaEscalamiento: IPoliticaEscalamiento = sampleWithRequiredData;
        const politicaEscalamientoCollection: IPoliticaEscalamiento[] = [sampleWithPartialData];
        expectedResult = service.addPoliticaEscalamientoToCollectionIfMissing(politicaEscalamientoCollection, politicaEscalamiento);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(politicaEscalamiento);
      });

      it('should add only unique PoliticaEscalamiento to an array', () => {
        const politicaEscalamientoArray: IPoliticaEscalamiento[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const politicaEscalamientoCollection: IPoliticaEscalamiento[] = [sampleWithRequiredData];
        expectedResult = service.addPoliticaEscalamientoToCollectionIfMissing(politicaEscalamientoCollection, ...politicaEscalamientoArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const politicaEscalamiento: IPoliticaEscalamiento = sampleWithRequiredData;
        const politicaEscalamiento2: IPoliticaEscalamiento = sampleWithPartialData;
        expectedResult = service.addPoliticaEscalamientoToCollectionIfMissing([], politicaEscalamiento, politicaEscalamiento2);
        expect(expectedResult).toEqual([politicaEscalamiento, politicaEscalamiento2]);
      });

      it('should accept null and undefined values', () => {
        const politicaEscalamiento: IPoliticaEscalamiento = sampleWithRequiredData;
        expectedResult = service.addPoliticaEscalamientoToCollectionIfMissing([], null, politicaEscalamiento, undefined);
        expect(expectedResult).toEqual([politicaEscalamiento]);
      });

      it('should return initial array if no PoliticaEscalamiento is added', () => {
        const politicaEscalamientoCollection: IPoliticaEscalamiento[] = [sampleWithRequiredData];
        expectedResult = service.addPoliticaEscalamientoToCollectionIfMissing(politicaEscalamientoCollection, undefined, null);
        expect(expectedResult).toEqual(politicaEscalamientoCollection);
      });
    });

    describe('comparePoliticaEscalamiento', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePoliticaEscalamiento(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 17575 };
        const entity2 = null;

        const compareResult1 = service.comparePoliticaEscalamiento(entity1, entity2);
        const compareResult2 = service.comparePoliticaEscalamiento(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 17575 };
        const entity2 = { id: 306 };

        const compareResult1 = service.comparePoliticaEscalamiento(entity1, entity2);
        const compareResult2 = service.comparePoliticaEscalamiento(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 17575 };
        const entity2 = { id: 17575 };

        const compareResult1 = service.comparePoliticaEscalamiento(entity1, entity2);
        const compareResult2 = service.comparePoliticaEscalamiento(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
