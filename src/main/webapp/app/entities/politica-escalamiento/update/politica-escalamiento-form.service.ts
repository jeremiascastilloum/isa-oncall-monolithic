import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IPoliticaEscalamiento, NewPoliticaEscalamiento } from '../politica-escalamiento.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPoliticaEscalamiento for edit and NewPoliticaEscalamientoFormGroupInput for create.
 */
type PoliticaEscalamientoFormGroupInput = IPoliticaEscalamiento | PartialWithRequiredKeyOf<NewPoliticaEscalamiento>;

type PoliticaEscalamientoFormDefaults = Pick<NewPoliticaEscalamiento, 'id'>;

type PoliticaEscalamientoFormGroupContent = {
  id: FormControl<IPoliticaEscalamiento['id'] | NewPoliticaEscalamiento['id']>;
  nombre: FormControl<IPoliticaEscalamiento['nombre']>;
  descripcion: FormControl<IPoliticaEscalamiento['descripcion']>;
  repetirVeces: FormControl<IPoliticaEscalamiento['repetirVeces']>;
  servicio: FormControl<IPoliticaEscalamiento['servicio']>;
};

export type PoliticaEscalamientoFormGroup = FormGroup<PoliticaEscalamientoFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PoliticaEscalamientoFormService {
  createPoliticaEscalamientoFormGroup(politicaEscalamiento?: PoliticaEscalamientoFormGroupInput): PoliticaEscalamientoFormGroup {
    const politicaEscalamientoRawValue = {
      ...this.getFormDefaults(),
      ...(politicaEscalamiento ?? { id: null }),
    };

    return new FormGroup<PoliticaEscalamientoFormGroupContent>({
      id: new FormControl(
        { value: politicaEscalamientoRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      nombre: new FormControl(politicaEscalamientoRawValue.nombre, {
        validators: [Validators.required, Validators.maxLength(60)],
      }),
      descripcion: new FormControl(politicaEscalamientoRawValue.descripcion, {
        validators: [Validators.maxLength(255)],
      }),
      repetirVeces: new FormControl(politicaEscalamientoRawValue.repetirVeces, {
        validators: [Validators.required, Validators.min(0), Validators.max(5)],
      }),
      servicio: new FormControl(politicaEscalamientoRawValue.servicio, {
        validators: [Validators.required],
      }),
    });
  }

  getPoliticaEscalamiento(form: PoliticaEscalamientoFormGroup): IPoliticaEscalamiento | NewPoliticaEscalamiento {
    return form.getRawValue();
  }

  resetForm(form: PoliticaEscalamientoFormGroup, politicaEscalamiento: PoliticaEscalamientoFormGroupInput): void {
    const politicaEscalamientoRawValue = { ...this.getFormDefaults(), ...politicaEscalamiento };
    form.reset({
      ...politicaEscalamientoRawValue,
      id: { value: politicaEscalamientoRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): PoliticaEscalamientoFormDefaults {
    return {
      id: null,
    };
  }
}
