import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { SuccessDialogComponent } from '../../components/dialogs/success-dialog/success-dialog.component';
import { ErrorDialogComponent } from '../../components/dialogs/error-dialog/error-dialog.component';

@Injectable({
  providedIn: 'root'
})
export class DialogService {
  constructor(private dialog: MatDialog) {}

  //dialog di successo
  openSuccessDialog(title: string, message: string) {
    this.dialog.open(SuccessDialogComponent, {
      width: '300px',
      data: { title, message }
    });
  }

  //dialog di errore
  openErrorDialog(title:string, message: string) {
    this.dialog.open(ErrorDialogComponent, {
      width: '300px',
      data: { title, message }
    });
  }
}

