import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { INotificacion, NewNotificacion } from '../notificacion.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts INotificacion for edit and NewNotificacionFormGroupInput for create.
 */
type NotificacionFormGroupInput = INotificacion | PartialWithRequiredKeyOf<NewNotificacion>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends INotificacion | NewNotificacion> = Omit<T, 'enviadaEn'> & {
  enviadaEn?: string | null;
};

type NotificacionFormRawValue = FormValueOf<INotificacion>;

type NewNotificacionFormRawValue = FormValueOf<NewNotificacion>;

type NotificacionFormDefaults = Pick<NewNotificacion, 'id' | 'enviadaEn'>;

type NotificacionFormGroupContent = {
  id: FormControl<NotificacionFormRawValue['id'] | NewNotificacion['id']>;
  canal: FormControl<NotificacionFormRawValue['canal']>;
  destino: FormControl<NotificacionFormRawValue['destino']>;
  estado: FormControl<NotificacionFormRawValue['estado']>;
  enviadaEn: FormControl<NotificacionFormRawValue['enviadaEn']>;
  intentos: FormControl<NotificacionFormRawValue['intentos']>;
  errorMensaje: FormControl<NotificacionFormRawValue['errorMensaje']>;
  incidente: FormControl<NotificacionFormRawValue['incidente']>;
  destinatario: FormControl<NotificacionFormRawValue['destinatario']>;
};

export type NotificacionFormGroup = FormGroup<NotificacionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class NotificacionFormService {
  createNotificacionFormGroup(notificacion?: NotificacionFormGroupInput): NotificacionFormGroup {
    const notificacionRawValue = this.convertNotificacionToNotificacionRawValue({
      ...this.getFormDefaults(),
      ...(notificacion ?? { id: null }),
    });

    return new FormGroup<NotificacionFormGroupContent>({
      id: new FormControl(
        { value: notificacionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      canal: new FormControl(notificacionRawValue.canal, {
        validators: [Validators.required],
      }),
      destino: new FormControl(notificacionRawValue.destino, {
        validators: [Validators.required, Validators.maxLength(120)],
      }),
      estado: new FormControl(notificacionRawValue.estado, {
        validators: [Validators.required],
      }),
      enviadaEn: new FormControl(notificacionRawValue.enviadaEn),
      intentos: new FormControl(notificacionRawValue.intentos, {
        validators: [Validators.required, Validators.min(0), Validators.max(10)],
      }),
      errorMensaje: new FormControl(notificacionRawValue.errorMensaje, {
        validators: [Validators.maxLength(255)],
      }),
      incidente: new FormControl(notificacionRawValue.incidente, {
        validators: [Validators.required],
      }),
      destinatario: new FormControl(notificacionRawValue.destinatario, {
        validators: [Validators.required],
      }),
    });
  }

  getNotificacion(form: NotificacionFormGroup): INotificacion | NewNotificacion {
    return this.convertNotificacionRawValueToNotificacion(form.getRawValue());
  }

  resetForm(form: NotificacionFormGroup, notificacion: NotificacionFormGroupInput): void {
    const notificacionRawValue = this.convertNotificacionToNotificacionRawValue({ ...this.getFormDefaults(), ...notificacion });
    form.reset({
      ...notificacionRawValue,
      id: { value: notificacionRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): NotificacionFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      enviadaEn: currentTime,
    };
  }

  private convertNotificacionRawValueToNotificacion(
    rawNotificacion: NotificacionFormRawValue | NewNotificacionFormRawValue,
  ): INotificacion | NewNotificacion {
    return {
      ...rawNotificacion,
      enviadaEn: dayjs(rawNotificacion.enviadaEn, DATE_TIME_FORMAT),
    };
  }

  private convertNotificacionToNotificacionRawValue(
    notificacion: INotificacion | (Partial<NewNotificacion> & NotificacionFormDefaults),
  ): NotificacionFormRawValue | PartialWithRequiredKeyOf<NewNotificacionFormRawValue> {
    return {
      ...notificacion,
      enviadaEn: notificacion.enviadaEn ? notificacion.enviadaEn.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
