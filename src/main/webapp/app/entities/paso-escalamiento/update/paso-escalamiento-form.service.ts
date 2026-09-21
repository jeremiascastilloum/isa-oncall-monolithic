import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IPasoEscalamiento, NewPasoEscalamiento } from '../paso-escalamiento.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPasoEscalamiento for edit and NewPasoEscalamientoFormGroupInput for create.
 */
type PasoEscalamientoFormGroupInput = IPasoEscalamiento | PartialWithRequiredKeyOf<NewPasoEscalamiento>;

type PasoEscalamientoFormDefaults = Pick<NewPasoEscalamiento, 'id'>;

type PasoEscalamientoFormGroupContent = {
  id: FormControl<IPasoEscalamiento['id'] | NewPasoEscalamiento['id']>;
  orden: FormControl<IPasoEscalamiento['orden']>;
  esperaMinutos: FormControl<IPasoEscalamiento['esperaMinutos']>;
  canal: FormControl<IPasoEscalamiento['canal']>;
  politica: FormControl<IPasoEscalamiento['politica']>;
  rotacion: FormControl<IPasoEscalamiento['rotacion']>;
  destinatarioDirecto: FormControl<IPasoEscalamiento['destinatarioDirecto']>;
};

export type PasoEscalamientoFormGroup = FormGroup<PasoEscalamientoFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PasoEscalamientoFormService {
  createPasoEscalamientoFormGroup(pasoEscalamiento?: PasoEscalamientoFormGroupInput): PasoEscalamientoFormGroup {
    const pasoEscalamientoRawValue = {
      ...this.getFormDefaults(),
      ...(pasoEscalamiento ?? { id: null }),
    };

    return new FormGroup<PasoEscalamientoFormGroupContent>({
      id: new FormControl(
        { value: pasoEscalamientoRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      orden: new FormControl(pasoEscalamientoRawValue.orden, {
        validators: [Validators.required, Validators.min(1), Validators.max(10)],
      }),
      esperaMinutos: new FormControl(pasoEscalamientoRawValue.esperaMinutos, {
        validators: [Validators.required, Validators.min(0), Validators.max(120)],
      }),
      canal: new FormControl(pasoEscalamientoRawValue.canal, {
        validators: [Validators.required],
      }),
      politica: new FormControl(pasoEscalamientoRawValue.politica, {
        validators: [Validators.required],
      }),
      rotacion: new FormControl(pasoEscalamientoRawValue.rotacion),
      destinatarioDirecto: new FormControl(pasoEscalamientoRawValue.destinatarioDirecto),
    });
  }

  getPasoEscalamiento(form: PasoEscalamientoFormGroup): IPasoEscalamiento | NewPasoEscalamiento {
    return form.getRawValue();
  }

  resetForm(form: PasoEscalamientoFormGroup, pasoEscalamiento: PasoEscalamientoFormGroupInput): void {
    const pasoEscalamientoRawValue = { ...this.getFormDefaults(), ...pasoEscalamiento };
    form.reset({
      ...pasoEscalamientoRawValue,
      id: { value: pasoEscalamientoRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): PasoEscalamientoFormDefaults {
    return {
      id: null,
    };
  }
}
