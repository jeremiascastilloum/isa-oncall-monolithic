import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IAlerta } from '../alerta.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../alerta.test-samples';

import { AlertaService, RestAlerta } from './alerta.service';

const requireRestSample: RestAlerta = {
  ...sampleWithRequiredData,
  recibidaEn: sampleWithRequiredData.recibidaEn?.toJSON(),
};

describe('Alerta Service', () => {
  let service: AlertaService;
  let httpMock: HttpTestingController;
  let expectedResult: IAlerta | IAlerta[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(AlertaService);
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

    it('should create a Alerta', () => {
      const alerta = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(alerta).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Alerta', () => {
      const alerta = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(alerta).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Alerta', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Alerta', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Alerta', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addAlertaToCollectionIfMissing', () => {
      it('should add a Alerta to an empty array', () => {
        const alerta: IAlerta = sampleWithRequiredData;
        expectedResult = service.addAlertaToCollectionIfMissing([], alerta);
        expect(expectedResult).toEqual([alerta]);
      });

      it('should not add a Alerta to an array that contains it', () => {
        const alerta: IAlerta = sampleWithRequiredData;
        const alertaCollection: IAlerta[] = [
          {
            ...alerta,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addAlertaToCollectionIfMissing(alertaCollection, alerta);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Alerta to an array that doesn't contain it", () => {
        const alerta: IAlerta = sampleWithRequiredData;
        const alertaCollection: IAlerta[] = [sampleWithPartialData];
        expectedResult = service.addAlertaToCollectionIfMissing(alertaCollection, alerta);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(alerta);
      });

      it('should add only unique Alerta to an array', () => {
        const alertaArray: IAlerta[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const alertaCollection: IAlerta[] = [sampleWithRequiredData];
        expectedResult = service.addAlertaToCollectionIfMissing(alertaCollection, ...alertaArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const alerta: IAlerta = sampleWithRequiredData;
        const alerta2: IAlerta = sampleWithPartialData;
        expectedResult = service.addAlertaToCollectionIfMissing([], alerta, alerta2);
        expect(expectedResult).toEqual([alerta, alerta2]);
      });

      it('should accept null and undefined values', () => {
        const alerta: IAlerta = sampleWithRequiredData;
        expectedResult = service.addAlertaToCollectionIfMissing([], null, alerta, undefined);
        expect(expectedResult).toEqual([alerta]);
      });

      it('should return initial array if no Alerta is added', () => {
        const alertaCollection: IAlerta[] = [sampleWithRequiredData];
        expectedResult = service.addAlertaToCollectionIfMissing(alertaCollection, undefined, null);
        expect(expectedResult).toEqual(alertaCollection);
      });
    });

    describe('compareAlerta', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareAlerta(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 13581 };
        const entity2 = null;

        const compareResult1 = service.compareAlerta(entity1, entity2);
        const compareResult2 = service.compareAlerta(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 13581 };
        const entity2 = { id: 26829 };

        const compareResult1 = service.compareAlerta(entity1, entity2);
        const compareResult2 = service.compareAlerta(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 13581 };
        const entity2 = { id: 13581 };

        const compareResult1 = service.compareAlerta(entity1, entity2);
        const compareResult2 = service.compareAlerta(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
