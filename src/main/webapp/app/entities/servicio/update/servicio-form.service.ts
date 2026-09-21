import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IServicio, NewServicio } from '../servicio.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IServicio for edit and NewServicioFormGroupInput for create.
 */
type ServicioFormGroupInput = IServicio | PartialWithRequiredKeyOf<NewServicio>;

type ServicioFormDefaults = Pick<NewServicio, 'id' | 'activo' | 'incidentes'>;

type ServicioFormGroupContent = {
  id: FormControl<IServicio['id'] | NewServicio['id']>;
  nombre: FormControl<IServicio['nombre']>;
  descripcion: FormControl<IServicio['descripcion']>;
  criticidad: FormControl<IServicio['criticidad']>;
  entorno: FormControl<IServicio['entorno']>;
  repositorioUrl: FormControl<IServicio['repositorioUrl']>;
  activo: FormControl<IServicio['activo']>;
  equipo: FormControl<IServicio['equipo']>;
  incidentes: FormControl<IServicio['incidentes']>;
};

export type ServicioFormGroup = FormGroup<ServicioFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ServicioFormService {
  createServicioFormGroup(servicio?: ServicioFormGroupInput): ServicioFormGroup {
    const servicioRawValue = {
      ...this.getFormDefaults(),
      ...(servicio ?? { id: null }),
    };

    return new FormGroup<ServicioFormGroupContent>({
      id: new FormControl(
        { value: servicioRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      nombre: new FormControl(servicioRawValue.nombre, {
        validators: [Validators.required, Validators.maxLength(60)],
      }),
      descripcion: new FormControl(servicioRawValue.descripcion, {
        validators: [Validators.maxLength(500)],
      }),
      criticidad: new FormControl(servicioRawValue.criticidad, {
        validators: [Validators.required],
      }),
      entorno: new FormControl(servicioRawValue.entorno, {
        validators: [Validators.required],
      }),
      repositorioUrl: new FormControl(servicioRawValue.repositorioUrl, {
        validators: [Validators.maxLength(255)],
      }),
      activo: new FormControl(servicioRawValue.activo, {
        validators: [Validators.required],
      }),
      equipo: new FormControl(servicioRawValue.equipo, {
        validators: [Validators.required],
      }),
      incidentes: new FormControl(servicioRawValue.incidentes ?? []),
    });
  }

  getServicio(form: ServicioFormGroup): IServicio | NewServicio {
    return form.getRawValue();
  }

  resetForm(form: ServicioFormGroup, servicio: ServicioFormGroupInput): void {
    const servicioRawValue = { ...this.getFormDefaults(), ...servicio };
    form.reset({
      ...servicioRawValue,
      id: { value: servicioRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): ServicioFormDefaults {
    return {
      id: null,
      activo: false,
      incidentes: [],
    };
  }
}
