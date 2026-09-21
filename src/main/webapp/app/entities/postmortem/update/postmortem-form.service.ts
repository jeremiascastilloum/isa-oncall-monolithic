import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IPostmortem, NewPostmortem } from '../postmortem.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPostmortem for edit and NewPostmortemFormGroupInput for create.
 */
type PostmortemFormGroupInput = IPostmortem | PartialWithRequiredKeyOf<NewPostmortem>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IPostmortem | NewPostmortem> = Omit<T, 'publicadoEn'> & {
  publicadoEn?: string | null;
};

type PostmortemFormRawValue = FormValueOf<IPostmortem>;

type NewPostmortemFormRawValue = FormValueOf<NewPostmortem>;

type PostmortemFormDefaults = Pick<NewPostmortem, 'id' | 'publicado' | 'publicadoEn'>;

type PostmortemFormGroupContent = {
  id: FormControl<PostmortemFormRawValue['id'] | NewPostmortem['id']>;
  titulo: FormControl<PostmortemFormRawValue['titulo']>;
  resumen: FormControl<PostmortemFormRawValue['resumen']>;
  causaRaiz: FormControl<PostmortemFormRawValue['causaRaiz']>;
  lineaDeTiempo: FormControl<PostmortemFormRawValue['lineaDeTiempo']>;
  leccionesAprendidas: FormControl<PostmortemFormRawValue['leccionesAprendidas']>;
  publicado: FormControl<PostmortemFormRawValue['publicado']>;
  publicadoEn: FormControl<PostmortemFormRawValue['publicadoEn']>;
  incidente: FormControl<PostmortemFormRawValue['incidente']>;
};

export type PostmortemFormGroup = FormGroup<PostmortemFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PostmortemFormService {
  createPostmortemFormGroup(postmortem?: PostmortemFormGroupInput): PostmortemFormGroup {
    const postmortemRawValue = this.convertPostmortemToPostmortemRawValue({
      ...this.getFormDefaults(),
      ...(postmortem ?? { id: null }),
    });

    return new FormGroup<PostmortemFormGroupContent>({
      id: new FormControl(
        { value: postmortemRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      titulo: new FormControl(postmortemRawValue.titulo, {
        validators: [Validators.required, Validators.maxLength(140)],
      }),
      resumen: new FormControl(postmortemRawValue.resumen, {
        validators: [Validators.required, Validators.maxLength(500)],
      }),
      causaRaiz: new FormControl(postmortemRawValue.causaRaiz, {
        validators: [Validators.required, Validators.maxLength(2000)],
      }),
      lineaDeTiempo: new FormControl(postmortemRawValue.lineaDeTiempo, {
        validators: [Validators.maxLength(4000)],
      }),
      leccionesAprendidas: new FormControl(postmortemRawValue.leccionesAprendidas, {
        validators: [Validators.maxLength(2000)],
      }),
      publicado: new FormControl(postmortemRawValue.publicado, {
        validators: [Validators.required],
      }),
      publicadoEn: new FormControl(postmortemRawValue.publicadoEn),
      incidente: new FormControl(postmortemRawValue.incidente, {
        validators: [Validators.required],
      }),
    });
  }

  getPostmortem(form: PostmortemFormGroup): IPostmortem | NewPostmortem {
    return this.convertPostmortemRawValueToPostmortem(form.getRawValue());
  }

  resetForm(form: PostmortemFormGroup, postmortem: PostmortemFormGroupInput): void {
    const postmortemRawValue = this.convertPostmortemToPostmortemRawValue({ ...this.getFormDefaults(), ...postmortem });
    form.reset({
      ...postmortemRawValue,
      id: { value: postmortemRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): PostmortemFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      publicado: false,
      publicadoEn: currentTime,
    };
  }

  private convertPostmortemRawValueToPostmortem(
    rawPostmortem: PostmortemFormRawValue | NewPostmortemFormRawValue,
  ): IPostmortem | NewPostmortem {
    return {
      ...rawPostmortem,
      publicadoEn: dayjs(rawPostmortem.publicadoEn, DATE_TIME_FORMAT),
    };
  }

  private convertPostmortemToPostmortemRawValue(
    postmortem: IPostmortem | (Partial<NewPostmortem> & PostmortemFormDefaults),
  ): PostmortemFormRawValue | PartialWithRequiredKeyOf<NewPostmortemFormRawValue> {
    return {
      ...postmortem,
      publicadoEn: postmortem.publicadoEn ? postmortem.publicadoEn.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
