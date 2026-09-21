import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IRotacion, NewRotacion } from '../rotacion.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IRotacion for edit and NewRotacionFormGroupInput for create.
 */
type RotacionFormGroupInput = IRotacion | PartialWithRequiredKeyOf<NewRotacion>;

type RotacionFormDefaults = Pick<NewRotacion, 'id' | 'activa'>;

type RotacionFormGroupContent = {
  id: FormControl<IRotacion['id'] | NewRotacion['id']>;
  nombre: FormControl<IRotacion['nombre']>;
  tipo: FormControl<IRotacion['tipo']>;
  zonaHoraria: FormControl<IRotacion['zonaHoraria']>;
  activa: FormControl<IRotacion['activa']>;
  equipo: FormControl<IRotacion['equipo']>;
};

export type RotacionFormGroup = FormGroup<RotacionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class RotacionFormService {
  createRotacionFormGroup(rotacion?: RotacionFormGroupInput): RotacionFormGroup {
    const rotacionRawValue = {
      ...this.getFormDefaults(),
      ...(rotacion ?? { id: null }),
    };

    return new FormGroup<RotacionFormGroupContent>({
      id: new FormControl(
        { value: rotacionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      nombre: new FormControl(rotacionRawValue.nombre, {
        validators: [Validators.required, Validators.maxLength(60)],
      }),
      tipo: new FormControl(rotacionRawValue.tipo, {
        validators: [Validators.required],
      }),
      zonaHoraria: new FormControl(rotacionRawValue.zonaHoraria, {
        validators: [Validators.required, Validators.maxLength(50)],
      }),
      activa: new FormControl(rotacionRawValue.activa, {
        validators: [Validators.required],
      }),
      equipo: new FormControl(rotacionRawValue.equipo, {
        validators: [Validators.required],
      }),
    });
  }

  getRotacion(form: RotacionFormGroup): IRotacion | NewRotacion {
    return form.getRawValue();
  }

  resetForm(form: RotacionFormGroup, rotacion: RotacionFormGroupInput): void {
    const rotacionRawValue = { ...this.getFormDefaults(), ...rotacion };
    form.reset({
      ...rotacionRawValue,
      id: { value: rotacionRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): RotacionFormDefaults {
    return {
      id: null,
      activa: false,
    };
  }
}
