import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IPoliticaEscalamiento } from '../politica-escalamiento.model';
import { PoliticaEscalamientoService } from '../service/politica-escalamiento.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './politica-escalamiento-delete-dialog.html',
  imports: [TranslateDirective, FormsModule, FontAwesomeModule, AlertError],
})
export class PoliticaEscalamientoDeleteDialog {
  politicaEscalamiento?: IPoliticaEscalamiento;

  protected readonly politicaEscalamientoService = inject(PoliticaEscalamientoService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.politicaEscalamientoService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
