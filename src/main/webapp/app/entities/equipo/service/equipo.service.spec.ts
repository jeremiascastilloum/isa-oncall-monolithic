import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IEquipo } from '../equipo.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../equipo.test-samples';

import { EquipoService } from './equipo.service';

const requireRestSample: IEquipo = {
  ...sampleWithRequiredData,
};

describe('Equipo Service', () => {
  let service: EquipoService;
  let httpMock: HttpTestingController;
  let expectedResult: IEquipo | IEquipo[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(EquipoService);
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

    it('should create a Equipo', () => {
      const equipo = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(equipo).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Equipo', () => {
      const equipo = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(equipo).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Equipo', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Equipo', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Equipo', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addEquipoToCollectionIfMissing', () => {
      it('should add a Equipo to an empty array', () => {
        const equipo: IEquipo = sampleWithRequiredData;
        expectedResult = service.addEquipoToCollectionIfMissing([], equipo);
        expect(expectedResult).toEqual([equipo]);
      });

      it('should not add a Equipo to an array that contains it', () => {
        const equipo: IEquipo = sampleWithRequiredData;
        const equipoCollection: IEquipo[] = [
          {
            ...equipo,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addEquipoToCollectionIfMissing(equipoCollection, equipo);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Equipo to an array that doesn't contain it", () => {
        const equipo: IEquipo = sampleWithRequiredData;
        const equipoCollection: IEquipo[] = [sampleWithPartialData];
        expectedResult = service.addEquipoToCollectionIfMissing(equipoCollection, equipo);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(equipo);
      });

      it('should add only unique Equipo to an array', () => {
        const equipoArray: IEquipo[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const equipoCollection: IEquipo[] = [sampleWithRequiredData];
        expectedResult = service.addEquipoToCollectionIfMissing(equipoCollection, ...equipoArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const equipo: IEquipo = sampleWithRequiredData;
        const equipo2: IEquipo = sampleWithPartialData;
        expectedResult = service.addEquipoToCollectionIfMissing([], equipo, equipo2);
        expect(expectedResult).toEqual([equipo, equipo2]);
      });

      it('should accept null and undefined values', () => {
        const equipo: IEquipo = sampleWithRequiredData;
        expectedResult = service.addEquipoToCollectionIfMissing([], null, equipo, undefined);
        expect(expectedResult).toEqual([equipo]);
      });

      it('should return initial array if no Equipo is added', () => {
        const equipoCollection: IEquipo[] = [sampleWithRequiredData];
        expectedResult = service.addEquipoToCollectionIfMissing(equipoCollection, undefined, null);
        expect(expectedResult).toEqual(equipoCollection);
      });
    });

    describe('compareEquipo', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareEquipo(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 20906 };
        const entity2 = null;

        const compareResult1 = service.compareEquipo(entity1, entity2);
        const compareResult2 = service.compareEquipo(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 20906 };
        const entity2 = { id: 21995 };

        const compareResult1 = service.compareEquipo(entity1, entity2);
        const compareResult2 = service.compareEquipo(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 20906 };
        const entity2 = { id: 20906 };

        const compareResult1 = service.compareEquipo(entity1, entity2);
        const compareResult2 = service.compareEquipo(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
