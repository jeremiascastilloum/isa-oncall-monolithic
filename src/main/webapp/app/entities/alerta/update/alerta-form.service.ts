import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IAlerta, NewAlerta } from '../alerta.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAlerta for edit and NewAlertaFormGroupInput for create.
 */
type AlertaFormGroupInput = IAlerta | PartialWithRequiredKeyOf<NewAlerta>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IAlerta | NewAlerta> = Omit<T, 'recibidaEn'> & {
  recibidaEn?: string | null;
};

type AlertaFormRawValue = FormValueOf<IAlerta>;

type NewAlertaFormRawValue = FormValueOf<NewAlerta>;

type AlertaFormDefaults = Pick<NewAlerta, 'id' | 'recibidaEn' | 'procesada'>;

type AlertaFormGroupContent = {
  id: FormControl<AlertaFormRawValue['id'] | NewAlerta['id']>;
  fingerprint: FormControl<AlertaFormRawValue['fingerprint']>;
  origen: FormControl<AlertaFormRawValue['origen']>;
  resumen: FormControl<AlertaFormRawValue['resumen']>;
  payload: FormControl<AlertaFormRawValue['payload']>;
  recibidaEn: FormControl<AlertaFormRawValue['recibidaEn']>;
  procesada: FormControl<AlertaFormRawValue['procesada']>;
  servicio: FormControl<AlertaFormRawValue['servicio']>;
  incidente: FormControl<AlertaFormRawValue['incidente']>;
};

export type AlertaFormGroup = FormGroup<AlertaFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AlertaFormService {
  createAlertaFormGroup(alerta?: AlertaFormGroupInput): AlertaFormGroup {
    const alertaRawValue = this.convertAlertaToAlertaRawValue({
      ...this.getFormDefaults(),
      ...(alerta ?? { id: null }),
    });

    return new FormGroup<AlertaFormGroupContent>({
      id: new FormControl(
        { value: alertaRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      fingerprint: new FormControl(alertaRawValue.fingerprint, {
        validators: [Validators.required, Validators.maxLength(120)],
      }),
      origen: new FormControl(alertaRawValue.origen, {
        validators: [Validators.required],
      }),
      resumen: new FormControl(alertaRawValue.resumen, {
        validators: [Validators.required, Validators.maxLength(200)],
      }),
      payload: new FormControl(alertaRawValue.payload, {
        validators: [Validators.maxLength(4000)],
      }),
      recibidaEn: new FormControl(alertaRawValue.recibidaEn, {
        validators: [Validators.required],
      }),
      procesada: new FormControl(alertaRawValue.procesada, {
        validators: [Validators.required],
      }),
      servicio: new FormControl(alertaRawValue.servicio, {
        validators: [Validators.required],
      }),
      incidente: new FormControl(alertaRawValue.incidente),
    });
  }

  getAlerta(form: AlertaFormGroup): IAlerta | NewAlerta {
    return this.convertAlertaRawValueToAlerta(form.getRawValue());
  }

  resetForm(form: AlertaFormGroup, alerta: AlertaFormGroupInput): void {
    const alertaRawValue = this.convertAlertaToAlertaRawValue({ ...this.getFormDefaults(), ...alerta });
    form.reset({
      ...alertaRawValue,
      id: { value: alertaRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): AlertaFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      recibidaEn: currentTime,
      procesada: false,
    };
  }

  private convertAlertaRawValueToAlerta(rawAlerta: AlertaFormRawValue | NewAlertaFormRawValue): IAlerta | NewAlerta {
    return {
      ...rawAlerta,
      recibidaEn: dayjs(rawAlerta.recibidaEn, DATE_TIME_FORMAT),
    };
  }

  private convertAlertaToAlertaRawValue(
    alerta: IAlerta | (Partial<NewAlerta> & AlertaFormDefaults),
  ): AlertaFormRawValue | PartialWithRequiredKeyOf<NewAlertaFormRawValue> {
    return {
      ...alerta,
      recibidaEn: alerta.recibidaEn ? alerta.recibidaEn.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
