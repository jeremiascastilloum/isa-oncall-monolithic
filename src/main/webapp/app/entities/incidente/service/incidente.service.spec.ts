import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IIncidente } from '../incidente.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../incidente.test-samples';

import { IncidenteService, RestIncidente } from './incidente.service';

const requireRestSample: RestIncidente = {
  ...sampleWithRequiredData,
  detectadoEn: sampleWithRequiredData.detectadoEn?.toJSON(),
  reconocidoEn: sampleWithRequiredData.reconocidoEn?.toJSON(),
  mitigadoEn: sampleWithRequiredData.mitigadoEn?.toJSON(),
  resueltoEn: sampleWithRequiredData.resueltoEn?.toJSON(),
};

describe('Incidente Service', () => {
  let service: IncidenteService;
  let httpMock: HttpTestingController;
  let expectedResult: IIncidente | IIncidente[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(IncidenteService);
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

    it('should create a Incidente', () => {
      const incidente = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(incidente).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Incidente', () => {
      const incidente = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(incidente).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Incidente', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Incidente', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Incidente', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addIncidenteToCollectionIfMissing', () => {
      it('should add a Incidente to an empty array', () => {
        const incidente: IIncidente = sampleWithRequiredData;
        expectedResult = service.addIncidenteToCollectionIfMissing([], incidente);
        expect(expectedResult).toEqual([incidente]);
      });

      it('should not add a Incidente to an array that contains it', () => {
        const incidente: IIncidente = sampleWithRequiredData;
        const incidenteCollection: IIncidente[] = [
          {
            ...incidente,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addIncidenteToCollectionIfMissing(incidenteCollection, incidente);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Incidente to an array that doesn't contain it", () => {
        const incidente: IIncidente = sampleWithRequiredData;
        const incidenteCollection: IIncidente[] = [sampleWithPartialData];
        expectedResult = service.addIncidenteToCollectionIfMissing(incidenteCollection, incidente);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(incidente);
      });

      it('should add only unique Incidente to an array', () => {
        const incidenteArray: IIncidente[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const incidenteCollection: IIncidente[] = [sampleWithRequiredData];
        expectedResult = service.addIncidenteToCollectionIfMissing(incidenteCollection, ...incidenteArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const incidente: IIncidente = sampleWithRequiredData;
        const incidente2: IIncidente = sampleWithPartialData;
        expectedResult = service.addIncidenteToCollectionIfMissing([], incidente, incidente2);
        expect(expectedResult).toEqual([incidente, incidente2]);
      });

      it('should accept null and undefined values', () => {
        const incidente: IIncidente = sampleWithRequiredData;
        expectedResult = service.addIncidenteToCollectionIfMissing([], null, incidente, undefined);
        expect(expectedResult).toEqual([incidente]);
      });

      it('should return initial array if no Incidente is added', () => {
        const incidenteCollection: IIncidente[] = [sampleWithRequiredData];
        expectedResult = service.addIncidenteToCollectionIfMissing(incidenteCollection, undefined, null);
        expect(expectedResult).toEqual(incidenteCollection);
      });
    });

    describe('compareIncidente', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareIncidente(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 31968 };
        const entity2 = null;

        const compareResult1 = service.compareIncidente(entity1, entity2);
        const compareResult2 = service.compareIncidente(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 31968 };
        const entity2 = { id: 10195 };

        const compareResult1 = service.compareIncidente(entity1, entity2);
        const compareResult2 = service.compareIncidente(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 31968 };
        const entity2 = { id: 31968 };

        const compareResult1 = service.compareIncidente(entity1, entity2);
        const compareResult2 = service.compareIncidente(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
