import { AfterViewInit, Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-error-dialog',
  templateUrl: './error-dialog.component.html',
  styleUrl: './error-dialog.component.css'
})
export class ErrorDialogComponent implements AfterViewInit {
  showAnimation = false; // Variabile per controllare l'animazione

  constructor(
    public dialogRef: MatDialogRef<ErrorDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { title: string, message: string }
  ) {}

  close() {
    this.dialogRef.close();
  }

  ngAfterViewInit() {
    //attiva l'animazione dopo che il componente è stato inizializzato
    setTimeout(() => {
      this.showAnimation = true;
    }, 75); //piccolo ritardo per garantire che il DOM sia pronto
  }
}