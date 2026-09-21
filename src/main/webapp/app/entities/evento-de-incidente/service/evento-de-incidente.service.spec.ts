import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IEventoDeIncidente } from '../evento-de-incidente.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../evento-de-incidente.test-samples';

import { EventoDeIncidenteService, RestEventoDeIncidente } from './evento-de-incidente.service';

const requireRestSample: RestEventoDeIncidente = {
  ...sampleWithRequiredData,
  ocurridoEn: sampleWithRequiredData.ocurridoEn?.toJSON(),
};

describe('EventoDeIncidente Service', () => {
  let service: EventoDeIncidenteService;
  let httpMock: HttpTestingController;
  let expectedResult: IEventoDeIncidente | IEventoDeIncidente[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(EventoDeIncidenteService);
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

    it('should create a EventoDeIncidente', () => {
      const eventoDeIncidente = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(eventoDeIncidente).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a EventoDeIncidente', () => {
      const eventoDeIncidente = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(eventoDeIncidente).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a EventoDeIncidente', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of EventoDeIncidente', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a EventoDeIncidente', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addEventoDeIncidenteToCollectionIfMissing', () => {
      it('should add a EventoDeIncidente to an empty array', () => {
        const eventoDeIncidente: IEventoDeIncidente = sampleWithRequiredData;
        expectedResult = service.addEventoDeIncidenteToCollectionIfMissing([], eventoDeIncidente);
        expect(expectedResult).toEqual([eventoDeIncidente]);
      });

      it('should not add a EventoDeIncidente to an array that contains it', () => {
        const eventoDeIncidente: IEventoDeIncidente = sampleWithRequiredData;
        const eventoDeIncidenteCollection: IEventoDeIncidente[] = [
          {
            ...eventoDeIncidente,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addEventoDeIncidenteToCollectionIfMissing(eventoDeIncidenteCollection, eventoDeIncidente);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a EventoDeIncidente to an array that doesn't contain it", () => {
        const eventoDeIncidente: IEventoDeIncidente = sampleWithRequiredData;
        const eventoDeIncidenteCollection: IEventoDeIncidente[] = [sampleWithPartialData];
        expectedResult = service.addEventoDeIncidenteToCollectionIfMissing(eventoDeIncidenteCollection, eventoDeIncidente);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(eventoDeIncidente);
      });

      it('should add only unique EventoDeIncidente to an array', () => {
        const eventoDeIncidenteArray: IEventoDeIncidente[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const eventoDeIncidenteCollection: IEventoDeIncidente[] = [sampleWithRequiredData];
        expectedResult = service.addEventoDeIncidenteToCollectionIfMissing(eventoDeIncidenteCollection, ...eventoDeIncidenteArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const eventoDeIncidente: IEventoDeIncidente = sampleWithRequiredData;
        const eventoDeIncidente2: IEventoDeIncidente = sampleWithPartialData;
        expectedResult = service.addEventoDeIncidenteToCollectionIfMissing([], eventoDeIncidente, eventoDeIncidente2);
        expect(expectedResult).toEqual([eventoDeIncidente, eventoDeIncidente2]);
      });

      it('should accept null and undefined values', () => {
        const eventoDeIncidente: IEventoDeIncidente = sampleWithRequiredData;
        expectedResult = service.addEventoDeIncidenteToCollectionIfMissing([], null, eventoDeIncidente, undefined);
        expect(expectedResult).toEqual([eventoDeIncidente]);
      });

      it('should return initial array if no EventoDeIncidente is added', () => {
        const eventoDeIncidenteCollection: IEventoDeIncidente[] = [sampleWithRequiredData];
        expectedResult = service.addEventoDeIncidenteToCollectionIfMissing(eventoDeIncidenteCollection, undefined, null);
        expect(expectedResult).toEqual(eventoDeIncidenteCollection);
      });
    });

    describe('compareEventoDeIncidente', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareEventoDeIncidente(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 32466 };
        const entity2 = null;

        const compareResult1 = service.compareEventoDeIncidente(entity1, entity2);
        const compareResult2 = service.compareEventoDeIncidente(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 32466 };
        const entity2 = { id: 167 };

        const compareResult1 = service.compareEventoDeIncidente(entity1, entity2);
        const compareResult2 = service.compareEventoDeIncidente(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 32466 };
        const entity2 = { id: 32466 };

        const compareResult1 = service.compareEventoDeIncidente(entity1, entity2);
        const compareResult2 = service.compareEventoDeIncidente(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
