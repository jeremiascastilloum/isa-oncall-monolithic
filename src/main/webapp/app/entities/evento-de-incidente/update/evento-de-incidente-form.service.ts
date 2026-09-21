import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IEventoDeIncidente, NewEventoDeIncidente } from '../evento-de-incidente.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IEventoDeIncidente for edit and NewEventoDeIncidenteFormGroupInput for create.
 */
type EventoDeIncidenteFormGroupInput = IEventoDeIncidente | PartialWithRequiredKeyOf<NewEventoDeIncidente>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IEventoDeIncidente | NewEventoDeIncidente> = Omit<T, 'ocurridoEn'> & {
  ocurridoEn?: string | null;
};

type EventoDeIncidenteFormRawValue = FormValueOf<IEventoDeIncidente>;

type NewEventoDeIncidenteFormRawValue = FormValueOf<NewEventoDeIncidente>;

type EventoDeIncidenteFormDefaults = Pick<NewEventoDeIncidente, 'id' | 'ocurridoEn' | 'automatico'>;

type EventoDeIncidenteFormGroupContent = {
  id: FormControl<EventoDeIncidenteFormRawValue['id'] | NewEventoDeIncidente['id']>;
  tipo: FormControl<EventoDeIncidenteFormRawValue['tipo']>;
  detalle: FormControl<EventoDeIncidenteFormRawValue['detalle']>;
  ocurridoEn: FormControl<EventoDeIncidenteFormRawValue['ocurridoEn']>;
  automatico: FormControl<EventoDeIncidenteFormRawValue['automatico']>;
  incidente: FormControl<EventoDeIncidenteFormRawValue['incidente']>;
};

export type EventoDeIncidenteFormGroup = FormGroup<EventoDeIncidenteFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class EventoDeIncidenteFormService {
  createEventoDeIncidenteFormGroup(eventoDeIncidente?: EventoDeIncidenteFormGroupInput): EventoDeIncidenteFormGroup {
    const eventoDeIncidenteRawValue = this.convertEventoDeIncidenteToEventoDeIncidenteRawValue({
      ...this.getFormDefaults(),
      ...(eventoDeIncidente ?? { id: null }),
    });

    return new FormGroup<EventoDeIncidenteFormGroupContent>({
      id: new FormControl(
        { value: eventoDeIncidenteRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      tipo: new FormControl(eventoDeIncidenteRawValue.tipo, {
        validators: [Validators.required],
      }),
      detalle: new FormControl(eventoDeIncidenteRawValue.detalle, {
        validators: [Validators.required, Validators.maxLength(500)],
      }),
      ocurridoEn: new FormControl(eventoDeIncidenteRawValue.ocurridoEn, {
        validators: [Validators.required],
      }),
      automatico: new FormControl(eventoDeIncidenteRawValue.automatico, {
        validators: [Validators.required],
      }),
      incidente: new FormControl(eventoDeIncidenteRawValue.incidente, {
        validators: [Validators.required],
      }),
    });
  }

  getEventoDeIncidente(form: EventoDeIncidenteFormGroup): IEventoDeIncidente | NewEventoDeIncidente {
    return this.convertEventoDeIncidenteRawValueToEventoDeIncidente(form.getRawValue());
  }

  resetForm(form: EventoDeIncidenteFormGroup, eventoDeIncidente: EventoDeIncidenteFormGroupInput): void {
    const eventoDeIncidenteRawValue = this.convertEventoDeIncidenteToEventoDeIncidenteRawValue({
      ...this.getFormDefaults(),
      ...eventoDeIncidente,
    });
    form.reset({
      ...eventoDeIncidenteRawValue,
      id: { value: eventoDeIncidenteRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): EventoDeIncidenteFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      ocurridoEn: currentTime,
      automatico: false,
    };
  }

  private convertEventoDeIncidenteRawValueToEventoDeIncidente(
    rawEventoDeIncidente: EventoDeIncidenteFormRawValue | NewEventoDeIncidenteFormRawValue,
  ): IEventoDeIncidente | NewEventoDeIncidente {
    return {
      ...rawEventoDeIncidente,
      ocurridoEn: dayjs(rawEventoDeIncidente.ocurridoEn, DATE_TIME_FORMAT),
    };
  }

  private convertEventoDeIncidenteToEventoDeIncidenteRawValue(
    eventoDeIncidente: IEventoDeIncidente | (Partial<NewEventoDeIncidente> & EventoDeIncidenteFormDefaults),
  ): EventoDeIncidenteFormRawValue | PartialWithRequiredKeyOf<NewEventoDeIncidenteFormRawValue> {
    return {
      ...eventoDeIncidente,
      ocurridoEn: eventoDeIncidente.ocurridoEn ? eventoDeIncidente.ocurridoEn.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
