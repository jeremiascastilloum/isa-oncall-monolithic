import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IRotacion } from '../rotacion.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../rotacion.test-samples';

import { RotacionService } from './rotacion.service';

const requireRestSample: IRotacion = {
  ...sampleWithRequiredData,
};

describe('Rotacion Service', () => {
  let service: RotacionService;
  let httpMock: HttpTestingController;
  let expectedResult: IRotacion | IRotacion[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(RotacionService);
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

    it('should create a Rotacion', () => {
      const rotacion = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(rotacion).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Rotacion', () => {
      const rotacion = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(rotacion).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Rotacion', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Rotacion', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Rotacion', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addRotacionToCollectionIfMissing', () => {
      it('should add a Rotacion to an empty array', () => {
        const rotacion: IRotacion = sampleWithRequiredData;
        expectedResult = service.addRotacionToCollectionIfMissing([], rotacion);
        expect(expectedResult).toEqual([rotacion]);
      });

      it('should not add a Rotacion to an array that contains it', () => {
        const rotacion: IRotacion = sampleWithRequiredData;
        const rotacionCollection: IRotacion[] = [
          {
            ...rotacion,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addRotacionToCollectionIfMissing(rotacionCollection, rotacion);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Rotacion to an array that doesn't contain it", () => {
        const rotacion: IRotacion = sampleWithRequiredData;
        const rotacionCollection: IRotacion[] = [sampleWithPartialData];
        expectedResult = service.addRotacionToCollectionIfMissing(rotacionCollection, rotacion);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(rotacion);
      });

      it('should add only unique Rotacion to an array', () => {
        const rotacionArray: IRotacion[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const rotacionCollection: IRotacion[] = [sampleWithRequiredData];
        expectedResult = service.addRotacionToCollectionIfMissing(rotacionCollection, ...rotacionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const rotacion: IRotacion = sampleWithRequiredData;
        const rotacion2: IRotacion = sampleWithPartialData;
        expectedResult = service.addRotacionToCollectionIfMissing([], rotacion, rotacion2);
        expect(expectedResult).toEqual([rotacion, rotacion2]);
      });

      it('should accept null and undefined values', () => {
        const rotacion: IRotacion = sampleWithRequiredData;
        expectedResult = service.addRotacionToCollectionIfMissing([], null, rotacion, undefined);
        expect(expectedResult).toEqual([rotacion]);
      });

      it('should return initial array if no Rotacion is added', () => {
        const rotacionCollection: IRotacion[] = [sampleWithRequiredData];
        expectedResult = service.addRotacionToCollectionIfMissing(rotacionCollection, undefined, null);
        expect(expectedResult).toEqual(rotacionCollection);
      });
    });

    describe('compareRotacion', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareRotacion(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 23556 };
        const entity2 = null;

        const compareResult1 = service.compareRotacion(entity1, entity2);
        const compareResult2 = service.compareRotacion(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 23556 };
        const entity2 = { id: 23369 };

        const compareResult1 = service.compareRotacion(entity1, entity2);
        const compareResult2 = service.compareRotacion(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 23556 };
        const entity2 = { id: 23556 };

        const compareResult1 = service.compareRotacion(entity1, entity2);
        const compareResult2 = service.compareRotacion(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
