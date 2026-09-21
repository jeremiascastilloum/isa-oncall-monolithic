import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ITurnoDeGuardia } from '../turno-de-guardia.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../turno-de-guardia.test-samples';

import { RestTurnoDeGuardia, TurnoDeGuardiaService } from './turno-de-guardia.service';

const requireRestSample: RestTurnoDeGuardia = {
  ...sampleWithRequiredData,
  desde: sampleWithRequiredData.desde?.toJSON(),
  hasta: sampleWithRequiredData.hasta?.toJSON(),
};

describe('TurnoDeGuardia Service', () => {
  let service: TurnoDeGuardiaService;
  let httpMock: HttpTestingController;
  let expectedResult: ITurnoDeGuardia | ITurnoDeGuardia[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(TurnoDeGuardiaService);
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

    it('should create a TurnoDeGuardia', () => {
      const turnoDeGuardia = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(turnoDeGuardia).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TurnoDeGuardia', () => {
      const turnoDeGuardia = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(turnoDeGuardia).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TurnoDeGuardia', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TurnoDeGuardia', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TurnoDeGuardia', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addTurnoDeGuardiaToCollectionIfMissing', () => {
      it('should add a TurnoDeGuardia to an empty array', () => {
        const turnoDeGuardia: ITurnoDeGuardia = sampleWithRequiredData;
        expectedResult = service.addTurnoDeGuardiaToCollectionIfMissing([], turnoDeGuardia);
        expect(expectedResult).toEqual([turnoDeGuardia]);
      });

      it('should not add a TurnoDeGuardia to an array that contains it', () => {
        const turnoDeGuardia: ITurnoDeGuardia = sampleWithRequiredData;
        const turnoDeGuardiaCollection: ITurnoDeGuardia[] = [
          {
            ...turnoDeGuardia,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTurnoDeGuardiaToCollectionIfMissing(turnoDeGuardiaCollection, turnoDeGuardia);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TurnoDeGuardia to an array that doesn't contain it", () => {
        const turnoDeGuardia: ITurnoDeGuardia = sampleWithRequiredData;
        const turnoDeGuardiaCollection: ITurnoDeGuardia[] = [sampleWithPartialData];
        expectedResult = service.addTurnoDeGuardiaToCollectionIfMissing(turnoDeGuardiaCollection, turnoDeGuardia);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(turnoDeGuardia);
      });

      it('should add only unique TurnoDeGuardia to an array', () => {
        const turnoDeGuardiaArray: ITurnoDeGuardia[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const turnoDeGuardiaCollection: ITurnoDeGuardia[] = [sampleWithRequiredData];
        expectedResult = service.addTurnoDeGuardiaToCollectionIfMissing(turnoDeGuardiaCollection, ...turnoDeGuardiaArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const turnoDeGuardia: ITurnoDeGuardia = sampleWithRequiredData;
        const turnoDeGuardia2: ITurnoDeGuardia = sampleWithPartialData;
        expectedResult = service.addTurnoDeGuardiaToCollectionIfMissing([], turnoDeGuardia, turnoDeGuardia2);
        expect(expectedResult).toEqual([turnoDeGuardia, turnoDeGuardia2]);
      });

      it('should accept null and undefined values', () => {
        const turnoDeGuardia: ITurnoDeGuardia = sampleWithRequiredData;
        expectedResult = service.addTurnoDeGuardiaToCollectionIfMissing([], null, turnoDeGuardia, undefined);
        expect(expectedResult).toEqual([turnoDeGuardia]);
      });

      it('should return initial array if no TurnoDeGuardia is added', () => {
        const turnoDeGuardiaCollection: ITurnoDeGuardia[] = [sampleWithRequiredData];
        expectedResult = service.addTurnoDeGuardiaToCollectionIfMissing(turnoDeGuardiaCollection, undefined, null);
        expect(expectedResult).toEqual(turnoDeGuardiaCollection);
      });
    });

    describe('compareTurnoDeGuardia', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTurnoDeGuardia(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 28122 };
        const entity2 = null;

        const compareResult1 = service.compareTurnoDeGuardia(entity1, entity2);
        const compareResult2 = service.compareTurnoDeGuardia(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 28122 };
        const entity2 = { id: 2676 };

        const compareResult1 = service.compareTurnoDeGuardia(entity1, entity2);
        const compareResult2 = service.compareTurnoDeGuardia(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 28122 };
        const entity2 = { id: 28122 };

        const compareResult1 = service.compareTurnoDeGuardia(entity1, entity2);
        const compareResult2 = service.compareTurnoDeGuardia(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
