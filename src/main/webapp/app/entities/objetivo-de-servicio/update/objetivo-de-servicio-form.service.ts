import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IObjetivoDeServicio, NewObjetivoDeServicio } from '../objetivo-de-servicio.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IObjetivoDeServicio for edit and NewObjetivoDeServicioFormGroupInput for create.
 */
type ObjetivoDeServicioFormGroupInput = IObjetivoDeServicio | PartialWithRequiredKeyOf<NewObjetivoDeServicio>;

type ObjetivoDeServicioFormDefaults = Pick<NewObjetivoDeServicio, 'id'>;

type ObjetivoDeServicioFormGroupContent = {
  id: FormControl<IObjetivoDeServicio['id'] | NewObjetivoDeServicio['id']>;
  tipo: FormControl<IObjetivoDeServicio['tipo']>;
  severidadAplicable: FormControl<IObjetivoDeServicio['severidadAplicable']>;
  minutosObjetivo: FormControl<IObjetivoDeServicio['minutosObjetivo']>;
  descripcion: FormControl<IObjetivoDeServicio['descripcion']>;
  servicio: FormControl<IObjetivoDeServicio['servicio']>;
};

export type ObjetivoDeServicioFormGroup = FormGroup<ObjetivoDeServicioFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ObjetivoDeServicioFormService {
  createObjetivoDeServicioFormGroup(objetivoDeServicio?: ObjetivoDeServicioFormGroupInput): ObjetivoDeServicioFormGroup {
    const objetivoDeServicioRawValue = {
      ...this.getFormDefaults(),
      ...(objetivoDeServicio ?? { id: null }),
    };

    return new FormGroup<ObjetivoDeServicioFormGroupContent>({
      id: new FormControl(
        { value: objetivoDeServicioRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      tipo: new FormControl(objetivoDeServicioRawValue.tipo, {
        validators: [Validators.required],
      }),
      severidadAplicable: new FormControl(objetivoDeServicioRawValue.severidadAplicable, {
        validators: [Validators.required],
      }),
      minutosObjetivo: new FormControl(objetivoDeServicioRawValue.minutosObjetivo, {
        validators: [Validators.required, Validators.min(1), Validators.max(10080)],
      }),
      descripcion: new FormControl(objetivoDeServicioRawValue.descripcion, {
        validators: [Validators.maxLength(255)],
      }),
      servicio: new FormControl(objetivoDeServicioRawValue.servicio, {
        validators: [Validators.required],
      }),
    });
  }

  getObjetivoDeServicio(form: ObjetivoDeServicioFormGroup): IObjetivoDeServicio | NewObjetivoDeServicio {
    return form.getRawValue();
  }

  resetForm(form: ObjetivoDeServicioFormGroup, objetivoDeServicio: ObjetivoDeServicioFormGroupInput): void {
    const objetivoDeServicioRawValue = { ...this.getFormDefaults(), ...objetivoDeServicio };
    form.reset({
      ...objetivoDeServicioRawValue,
      id: { value: objetivoDeServicioRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): ObjetivoDeServicioFormDefaults {
    return {
      id: null,
    };
  }
}
