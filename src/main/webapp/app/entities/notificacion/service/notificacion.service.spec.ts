import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { INotificacion } from '../notificacion.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../notificacion.test-samples';

import { NotificacionService, RestNotificacion } from './notificacion.service';

const requireRestSample: RestNotificacion = {
  ...sampleWithRequiredData,
  enviadaEn: sampleWithRequiredData.enviadaEn?.toJSON(),
};

describe('Notificacion Service', () => {
  let service: NotificacionService;
  let httpMock: HttpTestingController;
  let expectedResult: INotificacion | INotificacion[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(NotificacionService);
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

    it('should create a Notificacion', () => {
      const notificacion = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(notificacion).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Notificacion', () => {
      const notificacion = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(notificacion).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Notificacion', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Notificacion', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Notificacion', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addNotificacionToCollectionIfMissing', () => {
      it('should add a Notificacion to an empty array', () => {
        const notificacion: INotificacion = sampleWithRequiredData;
        expectedResult = service.addNotificacionToCollectionIfMissing([], notificacion);
        expect(expectedResult).toEqual([notificacion]);
      });

      it('should not add a Notificacion to an array that contains it', () => {
        const notificacion: INotificacion = sampleWithRequiredData;
        const notificacionCollection: INotificacion[] = [
          {
            ...notificacion,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addNotificacionToCollectionIfMissing(notificacionCollection, notificacion);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Notificacion to an array that doesn't contain it", () => {
        const notificacion: INotificacion = sampleWithRequiredData;
        const notificacionCollection: INotificacion[] = [sampleWithPartialData];
        expectedResult = service.addNotificacionToCollectionIfMissing(notificacionCollection, notificacion);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(notificacion);
      });

      it('should add only unique Notificacion to an array', () => {
        const notificacionArray: INotificacion[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const notificacionCollection: INotificacion[] = [sampleWithRequiredData];
        expectedResult = service.addNotificacionToCollectionIfMissing(notificacionCollection, ...notificacionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const notificacion: INotificacion = sampleWithRequiredData;
        const notificacion2: INotificacion = sampleWithPartialData;
        expectedResult = service.addNotificacionToCollectionIfMissing([], notificacion, notificacion2);
        expect(expectedResult).toEqual([notificacion, notificacion2]);
      });

      it('should accept null and undefined values', () => {
        const notificacion: INotificacion = sampleWithRequiredData;
        expectedResult = service.addNotificacionToCollectionIfMissing([], null, notificacion, undefined);
        expect(expectedResult).toEqual([notificacion]);
      });

      it('should return initial array if no Notificacion is added', () => {
        const notificacionCollection: INotificacion[] = [sampleWithRequiredData];
        expectedResult = service.addNotificacionToCollectionIfMissing(notificacionCollection, undefined, null);
        expect(expectedResult).toEqual(notificacionCollection);
      });
    });

    describe('compareNotificacion', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareNotificacion(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 30327 };
        const entity2 = null;

        const compareResult1 = service.compareNotificacion(entity1, entity2);
        const compareResult2 = service.compareNotificacion(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 30327 };
        const entity2 = { id: 24975 };

        const compareResult1 = service.compareNotificacion(entity1, entity2);
        const compareResult2 = service.compareNotificacion(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 30327 };
        const entity2 = { id: 30327 };

        const compareResult1 = service.compareNotificacion(entity1, entity2);
        const compareResult2 = service.compareNotificacion(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
