import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { TurnoDeGuardiaService } from '../service/turno-de-guardia.service';
import { ITurnoDeGuardia } from '../turno-de-guardia.model';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './turno-de-guardia-delete-dialog.html',
  imports: [TranslateDirective, FormsModule, FontAwesomeModule, AlertError],
})
export class TurnoDeGuardiaDeleteDialog {
  turnoDeGuardia?: ITurnoDeGuardia;

  protected readonly turnoDeGuardiaService = inject(TurnoDeGuardiaService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.turnoDeGuardiaService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
