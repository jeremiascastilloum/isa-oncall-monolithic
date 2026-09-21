import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ITurnoDeGuardia, NewTurnoDeGuardia } from '../turno-de-guardia.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITurnoDeGuardia for edit and NewTurnoDeGuardiaFormGroupInput for create.
 */
type TurnoDeGuardiaFormGroupInput = ITurnoDeGuardia | PartialWithRequiredKeyOf<NewTurnoDeGuardia>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ITurnoDeGuardia | NewTurnoDeGuardia> = Omit<T, 'desde' | 'hasta'> & {
  desde?: string | null;
  hasta?: string | null;
};

type TurnoDeGuardiaFormRawValue = FormValueOf<ITurnoDeGuardia>;

type NewTurnoDeGuardiaFormRawValue = FormValueOf<NewTurnoDeGuardia>;

type TurnoDeGuardiaFormDefaults = Pick<NewTurnoDeGuardia, 'id' | 'desde' | 'hasta' | 'esReemplazo'>;

type TurnoDeGuardiaFormGroupContent = {
  id: FormControl<TurnoDeGuardiaFormRawValue['id'] | NewTurnoDeGuardia['id']>;
  desde: FormControl<TurnoDeGuardiaFormRawValue['desde']>;
  hasta: FormControl<TurnoDeGuardiaFormRawValue['hasta']>;
  esReemplazo: FormControl<TurnoDeGuardiaFormRawValue['esReemplazo']>;
  nota: FormControl<TurnoDeGuardiaFormRawValue['nota']>;
  rotacion: FormControl<TurnoDeGuardiaFormRawValue['rotacion']>;
  responsable: FormControl<TurnoDeGuardiaFormRawValue['responsable']>;
};

export type TurnoDeGuardiaFormGroup = FormGroup<TurnoDeGuardiaFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TurnoDeGuardiaFormService {
  createTurnoDeGuardiaFormGroup(turnoDeGuardia?: TurnoDeGuardiaFormGroupInput): TurnoDeGuardiaFormGroup {
    const turnoDeGuardiaRawValue = this.convertTurnoDeGuardiaToTurnoDeGuardiaRawValue({
      ...this.getFormDefaults(),
      ...(turnoDeGuardia ?? { id: null }),
    });

    return new FormGroup<TurnoDeGuardiaFormGroupContent>({
      id: new FormControl(
        { value: turnoDeGuardiaRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      desde: new FormControl(turnoDeGuardiaRawValue.desde, {
        validators: [Validators.required],
      }),
      hasta: new FormControl(turnoDeGuardiaRawValue.hasta, {
        validators: [Validators.required],
      }),
      esReemplazo: new FormControl(turnoDeGuardiaRawValue.esReemplazo),
      nota: new FormControl(turnoDeGuardiaRawValue.nota, {
        validators: [Validators.maxLength(255)],
      }),
      rotacion: new FormControl(turnoDeGuardiaRawValue.rotacion, {
        validators: [Validators.required],
      }),
      responsable: new FormControl(turnoDeGuardiaRawValue.responsable, {
        validators: [Validators.required],
      }),
    });
  }

  getTurnoDeGuardia(form: TurnoDeGuardiaFormGroup): ITurnoDeGuardia | NewTurnoDeGuardia {
    return this.convertTurnoDeGuardiaRawValueToTurnoDeGuardia(form.getRawValue());
  }

  resetForm(form: TurnoDeGuardiaFormGroup, turnoDeGuardia: TurnoDeGuardiaFormGroupInput): void {
    const turnoDeGuardiaRawValue = this.convertTurnoDeGuardiaToTurnoDeGuardiaRawValue({ ...this.getFormDefaults(), ...turnoDeGuardia });
    form.reset({
      ...turnoDeGuardiaRawValue,
      id: { value: turnoDeGuardiaRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): TurnoDeGuardiaFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      desde: currentTime,
      hasta: currentTime,
      esReemplazo: false,
    };
  }

  private convertTurnoDeGuardiaRawValueToTurnoDeGuardia(
    rawTurnoDeGuardia: TurnoDeGuardiaFormRawValue | NewTurnoDeGuardiaFormRawValue,
  ): ITurnoDeGuardia | NewTurnoDeGuardia {
    return {
      ...rawTurnoDeGuardia,
      desde: dayjs(rawTurnoDeGuardia.desde, DATE_TIME_FORMAT),
      hasta: dayjs(rawTurnoDeGuardia.hasta, DATE_TIME_FORMAT),
    };
  }

  private convertTurnoDeGuardiaToTurnoDeGuardiaRawValue(
    turnoDeGuardia: ITurnoDeGuardia | (Partial<NewTurnoDeGuardia> & TurnoDeGuardiaFormDefaults),
  ): TurnoDeGuardiaFormRawValue | PartialWithRequiredKeyOf<NewTurnoDeGuardiaFormRawValue> {
    return {
      ...turnoDeGuardia,
      desde: turnoDeGuardia.desde ? turnoDeGuardia.desde.format(DATE_TIME_FORMAT) : undefined,
      hasta: turnoDeGuardia.hasta ? turnoDeGuardia.hasta.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
