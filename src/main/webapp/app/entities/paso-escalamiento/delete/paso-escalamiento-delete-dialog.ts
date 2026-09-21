import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IPasoEscalamiento } from '../paso-escalamiento.model';
import { PasoEscalamientoService } from '../service/paso-escalamiento.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './paso-escalamiento-delete-dialog.html',
  imports: [TranslateDirective, FormsModule, FontAwesomeModule, AlertError],
})
export class PasoEscalamientoDeleteDialog {
  pasoEscalamiento?: IPasoEscalamiento;

  protected readonly pasoEscalamientoService = inject(PasoEscalamientoService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.pasoEscalamientoService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
