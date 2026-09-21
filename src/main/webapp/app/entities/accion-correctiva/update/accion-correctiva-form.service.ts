import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IAccionCorrectiva, NewAccionCorrectiva } from '../accion-correctiva.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAccionCorrectiva for edit and NewAccionCorrectivaFormGroupInput for create.
 */
type AccionCorrectivaFormGroupInput = IAccionCorrectiva | PartialWithRequiredKeyOf<NewAccionCorrectiva>;

type AccionCorrectivaFormDefaults = Pick<NewAccionCorrectiva, 'id'>;

type AccionCorrectivaFormGroupContent = {
  id: FormControl<IAccionCorrectiva['id'] | NewAccionCorrectiva['id']>;
  descripcion: FormControl<IAccionCorrectiva['descripcion']>;
  prioridad: FormControl<IAccionCorrectiva['prioridad']>;
  estado: FormControl<IAccionCorrectiva['estado']>;
  fechaLimite: FormControl<IAccionCorrectiva['fechaLimite']>;
  ticketUrl: FormControl<IAccionCorrectiva['ticketUrl']>;
  postmortem: FormControl<IAccionCorrectiva['postmortem']>;
  responsable: FormControl<IAccionCorrectiva['responsable']>;
};

export type AccionCorrectivaFormGroup = FormGroup<AccionCorrectivaFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AccionCorrectivaFormService {
  createAccionCorrectivaFormGroup(accionCorrectiva?: AccionCorrectivaFormGroupInput): AccionCorrectivaFormGroup {
    const accionCorrectivaRawValue = {
      ...this.getFormDefaults(),
      ...(accionCorrectiva ?? { id: null }),
    };

    return new FormGroup<AccionCorrectivaFormGroupContent>({
      id: new FormControl(
        { value: accionCorrectivaRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      descripcion: new FormControl(accionCorrectivaRawValue.descripcion, {
        validators: [Validators.required, Validators.maxLength(300)],
      }),
      prioridad: new FormControl(accionCorrectivaRawValue.prioridad, {
        validators: [Validators.required],
      }),
      estado: new FormControl(accionCorrectivaRawValue.estado, {
        validators: [Validators.required],
      }),
      fechaLimite: new FormControl(accionCorrectivaRawValue.fechaLimite),
      ticketUrl: new FormControl(accionCorrectivaRawValue.ticketUrl, {
        validators: [Validators.maxLength(255)],
      }),
      postmortem: new FormControl(accionCorrectivaRawValue.postmortem, {
        validators: [Validators.required],
      }),
      responsable: new FormControl(accionCorrectivaRawValue.responsable),
    });
  }

  getAccionCorrectiva(form: AccionCorrectivaFormGroup): IAccionCorrectiva | NewAccionCorrectiva {
    return form.getRawValue();
  }

  resetForm(form: AccionCorrectivaFormGroup, accionCorrectiva: AccionCorrectivaFormGroupInput): void {
    const accionCorrectivaRawValue = { ...this.getFormDefaults(), ...accionCorrectiva };
    form.reset({
      ...accionCorrectivaRawValue,
      id: { value: accionCorrectivaRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): AccionCorrectivaFormDefaults {
    return {
      id: null,
    };
  }
}
