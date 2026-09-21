import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IObjetivoDeServicio } from '../objetivo-de-servicio.model';
import { ObjetivoDeServicioService } from '../service/objetivo-de-servicio.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './objetivo-de-servicio-delete-dialog.html',
  imports: [TranslateDirective, FormsModule, FontAwesomeModule, AlertError],
})
export class ObjetivoDeServicioDeleteDialog {
  objetivoDeServicio?: IObjetivoDeServicio;

  protected readonly objetivoDeServicioService = inject(ObjetivoDeServicioService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.objetivoDeServicioService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
