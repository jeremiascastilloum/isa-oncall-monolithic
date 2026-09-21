import { IUser } from './user.model';

export const sampleWithRequiredData: IUser = {
  id: 24814,
  login: 'Martin36',
};

export const sampleWithPartialData: IUser = {
  id: 966,
  login: 'Adan_QuinonezSantiago',
};

export const sampleWithFullData: IUser = {
  id: 5440,
  login: 'Antonio.EstradaCarrera20',
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
