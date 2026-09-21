import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IIncidente, NewIncidente } from '../incidente.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IIncidente for edit and NewIncidenteFormGroupInput for create.
 */
type IncidenteFormGroupInput = IIncidente | PartialWithRequiredKeyOf<NewIncidente>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IIncidente | NewIncidente> = Omit<T, 'detectadoEn' | 'reconocidoEn' | 'mitigadoEn' | 'resueltoEn'> & {
  detectadoEn?: string | null;
  reconocidoEn?: string | null;
  mitigadoEn?: string | null;
  resueltoEn?: string | null;
};

type IncidenteFormRawValue = FormValueOf<IIncidente>;

type NewIncidenteFormRawValue = FormValueOf<NewIncidente>;

type IncidenteFormDefaults = Pick<
  NewIncidente,
  'id' | 'detectadoEn' | 'reconocidoEn' | 'mitigadoEn' | 'resueltoEn' | 'cumplioObjetivo' | 'servicios'
>;

type IncidenteFormGroupContent = {
  id: FormControl<IncidenteFormRawValue['id'] | NewIncidente['id']>;
  titulo: FormControl<IncidenteFormRawValue['titulo']>;
  descripcion: FormControl<IncidenteFormRawValue['descripcion']>;
  severidad: FormControl<IncidenteFormRawValue['severidad']>;
  estado: FormControl<IncidenteFormRawValue['estado']>;
  detectadoEn: FormControl<IncidenteFormRawValue['detectadoEn']>;
  reconocidoEn: FormControl<IncidenteFormRawValue['reconocidoEn']>;
  mitigadoEn: FormControl<IncidenteFormRawValue['mitigadoEn']>;
  resueltoEn: FormControl<IncidenteFormRawValue['resueltoEn']>;
  usuariosAfectados: FormControl<IncidenteFormRawValue['usuariosAfectados']>;
  cumplioObjetivo: FormControl<IncidenteFormRawValue['cumplioObjetivo']>;
  comandante: FormControl<IncidenteFormRawValue['comandante']>;
  servicios: FormControl<IncidenteFormRawValue['servicios']>;
};

export type IncidenteFormGroup = FormGroup<IncidenteFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class IncidenteFormService {
  createIncidenteFormGroup(incidente?: IncidenteFormGroupInput): IncidenteFormGroup {
    const incidenteRawValue = this.convertIncidenteToIncidenteRawValue({
      ...this.getFormDefaults(),
      ...(incidente ?? { id: null }),
    });

    return new FormGroup<IncidenteFormGroupContent>({
      id: new FormControl(
        { value: incidenteRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      titulo: new FormControl(incidenteRawValue.titulo, {
        validators: [Validators.required, Validators.maxLength(140)],
      }),
      descripcion: new FormControl(incidenteRawValue.descripcion, {
        validators: [Validators.maxLength(2000)],
      }),
      severidad: new FormControl(incidenteRawValue.severidad, {
        validators: [Validators.required],
      }),
      estado: new FormControl(incidenteRawValue.estado, {
        validators: [Validators.required],
      }),
      detectadoEn: new FormControl(incidenteRawValue.detectadoEn, {
        validators: [Validators.required],
      }),
      reconocidoEn: new FormControl(incidenteRawValue.reconocidoEn),
      mitigadoEn: new FormControl(incidenteRawValue.mitigadoEn),
      resueltoEn: new FormControl(incidenteRawValue.resueltoEn),
      usuariosAfectados: new FormControl(incidenteRawValue.usuariosAfectados, {
        validators: [Validators.min(0)],
      }),
      cumplioObjetivo: new FormControl(incidenteRawValue.cumplioObjetivo),
      comandante: new FormControl(incidenteRawValue.comandante),
      servicios: new FormControl(incidenteRawValue.servicios ?? []),
    });
  }

  getIncidente(form: IncidenteFormGroup): IIncidente | NewIncidente {
    return this.convertIncidenteRawValueToIncidente(form.getRawValue());
  }

  resetForm(form: IncidenteFormGroup, incidente: IncidenteFormGroupInput): void {
    const incidenteRawValue = this.convertIncidenteToIncidenteRawValue({ ...this.getFormDefaults(), ...incidente });
    form.reset({
      ...incidenteRawValue,
      id: { value: incidenteRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): IncidenteFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      detectadoEn: currentTime,
      reconocidoEn: currentTime,
      mitigadoEn: currentTime,
      resueltoEn: currentTime,
      cumplioObjetivo: false,
      servicios: [],
    };
  }

  private convertIncidenteRawValueToIncidente(rawIncidente: IncidenteFormRawValue | NewIncidenteFormRawValue): IIncidente | NewIncidente {
    return {
      ...rawIncidente,
      detectadoEn: dayjs(rawIncidente.detectadoEn, DATE_TIME_FORMAT),
      reconocidoEn: dayjs(rawIncidente.reconocidoEn, DATE_TIME_FORMAT),
      mitigadoEn: dayjs(rawIncidente.mitigadoEn, DATE_TIME_FORMAT),
      resueltoEn: dayjs(rawIncidente.resueltoEn, DATE_TIME_FORMAT),
    };
  }

  private convertIncidenteToIncidenteRawValue(
    incidente: IIncidente | (Partial<NewIncidente> & IncidenteFormDefaults),
  ): IncidenteFormRawValue | PartialWithRequiredKeyOf<NewIncidenteFormRawValue> {
    return {
      ...incidente,
      detectadoEn: incidente.detectadoEn ? incidente.detectadoEn.format(DATE_TIME_FORMAT) : undefined,
      reconocidoEn: incidente.reconocidoEn ? incidente.reconocidoEn.format(DATE_TIME_FORMAT) : undefined,
      mitigadoEn: incidente.mitigadoEn ? incidente.mitigadoEn.format(DATE_TIME_FORMAT) : undefined,
      resueltoEn: incidente.resueltoEn ? incidente.resueltoEn.format(DATE_TIME_FORMAT) : undefined,
      servicios: incidente.servicios ?? [],
    };
  }
}
