import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IEquipo, NewEquipo } from '../equipo.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IEquipo for edit and NewEquipoFormGroupInput for create.
 */
type EquipoFormGroupInput = IEquipo | PartialWithRequiredKeyOf<NewEquipo>;

type EquipoFormDefaults = Pick<NewEquipo, 'id'>;

type EquipoFormGroupContent = {
  id: FormControl<IEquipo['id'] | NewEquipo['id']>;
  nombre: FormControl<IEquipo['nombre']>;
  emailContacto: FormControl<IEquipo['emailContacto']>;
  canalChat: FormControl<IEquipo['canalChat']>;
};

export type EquipoFormGroup = FormGroup<EquipoFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class EquipoFormService {
  createEquipoFormGroup(equipo?: EquipoFormGroupInput): EquipoFormGroup {
    const equipoRawValue = {
      ...this.getFormDefaults(),
      ...(equipo ?? { id: null }),
    };

    return new FormGroup<EquipoFormGroupContent>({
      id: new FormControl(
        { value: equipoRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      nombre: new FormControl(equipoRawValue.nombre, {
        validators: [Validators.required, Validators.maxLength(50)],
      }),
      emailContacto: new FormControl(equipoRawValue.emailContacto, {
        validators: [Validators.required, Validators.maxLength(120)],
      }),
      canalChat: new FormControl(equipoRawValue.canalChat, {
        validators: [Validators.maxLength(60)],
      }),
    });
  }

  getEquipo(form: EquipoFormGroup): IEquipo | NewEquipo {
    return form.getRawValue();
  }

  resetForm(form: EquipoFormGroup, equipo: EquipoFormGroupInput): void {
    const equipoRawValue = { ...this.getFormDefaults(), ...equipo };
    form.reset({
      ...equipoRawValue,
      id: { value: equipoRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): EquipoFormDefaults {
    return {
      id: null,
    };
  }
}
