import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IObjetivoDeServicio } from '../objetivo-de-servicio.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../objetivo-de-servicio.test-samples';

import { ObjetivoDeServicioService } from './objetivo-de-servicio.service';

const requireRestSample: IObjetivoDeServicio = {
  ...sampleWithRequiredData,
};

describe('ObjetivoDeServicio Service', () => {
  let service: ObjetivoDeServicioService;
  let httpMock: HttpTestingController;
  let expectedResult: IObjetivoDeServicio | IObjetivoDeServicio[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ObjetivoDeServicioService);
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

    it('should create a ObjetivoDeServicio', () => {
      const objetivoDeServicio = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(objetivoDeServicio).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ObjetivoDeServicio', () => {
      const objetivoDeServicio = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(objetivoDeServicio).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ObjetivoDeServicio', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ObjetivoDeServicio', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ObjetivoDeServicio', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addObjetivoDeServicioToCollectionIfMissing', () => {
      it('should add a ObjetivoDeServicio to an empty array', () => {
        const objetivoDeServicio: IObjetivoDeServicio = sampleWithRequiredData;
        expectedResult = service.addObjetivoDeServicioToCollectionIfMissing([], objetivoDeServicio);
        expect(expectedResult).toEqual([objetivoDeServicio]);
      });

      it('should not add a ObjetivoDeServicio to an array that contains it', () => {
        const objetivoDeServicio: IObjetivoDeServicio = sampleWithRequiredData;
        const objetivoDeServicioCollection: IObjetivoDeServicio[] = [
          {
            ...objetivoDeServicio,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addObjetivoDeServicioToCollectionIfMissing(objetivoDeServicioCollection, objetivoDeServicio);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ObjetivoDeServicio to an array that doesn't contain it", () => {
        const objetivoDeServicio: IObjetivoDeServicio = sampleWithRequiredData;
        const objetivoDeServicioCollection: IObjetivoDeServicio[] = [sampleWithPartialData];
        expectedResult = service.addObjetivoDeServicioToCollectionIfMissing(objetivoDeServicioCollection, objetivoDeServicio);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(objetivoDeServicio);
      });

      it('should add only unique ObjetivoDeServicio to an array', () => {
        const objetivoDeServicioArray: IObjetivoDeServicio[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const objetivoDeServicioCollection: IObjetivoDeServicio[] = [sampleWithRequiredData];
        expectedResult = service.addObjetivoDeServicioToCollectionIfMissing(objetivoDeServicioCollection, ...objetivoDeServicioArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const objetivoDeServicio: IObjetivoDeServicio = sampleWithRequiredData;
        const objetivoDeServicio2: IObjetivoDeServicio = sampleWithPartialData;
        expectedResult = service.addObjetivoDeServicioToCollectionIfMissing([], objetivoDeServicio, objetivoDeServicio2);
        expect(expectedResult).toEqual([objetivoDeServicio, objetivoDeServicio2]);
      });

      it('should accept null and undefined values', () => {
        const objetivoDeServicio: IObjetivoDeServicio = sampleWithRequiredData;
        expectedResult = service.addObjetivoDeServicioToCollectionIfMissing([], null, objetivoDeServicio, undefined);
        expect(expectedResult).toEqual([objetivoDeServicio]);
      });

      it('should return initial array if no ObjetivoDeServicio is added', () => {
        const objetivoDeServicioCollection: IObjetivoDeServicio[] = [sampleWithRequiredData];
        expectedResult = service.addObjetivoDeServicioToCollectionIfMissing(objetivoDeServicioCollection, undefined, null);
        expect(expectedResult).toEqual(objetivoDeServicioCollection);
      });
    });

    describe('compareObjetivoDeServicio', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareObjetivoDeServicio(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 18261 };
        const entity2 = null;

        const compareResult1 = service.compareObjetivoDeServicio(entity1, entity2);
        const compareResult2 = service.compareObjetivoDeServicio(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 18261 };
        const entity2 = { id: 26426 };

        const compareResult1 = service.compareObjetivoDeServicio(entity1, entity2);
        const compareResult2 = service.compareObjetivoDeServicio(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 18261 };
        const entity2 = { id: 18261 };

        const compareResult1 = service.compareObjetivoDeServicio(entity1, entity2);
        const compareResult2 = service.compareObjetivoDeServicio(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
