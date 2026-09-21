import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslatePipe } from '@ngx-translate/core';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IObjetivoDeServicio } from '../objetivo-de-servicio.model';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-objetivo-de-servicio-detail',
  templateUrl: './objetivo-de-servicio-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, TranslatePipe, RouterLink],
})
export class ObjetivoDeServicioDetail {
  readonly objetivoDeServicio = input<IObjetivoDeServicio | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
