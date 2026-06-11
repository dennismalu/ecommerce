import { Component } from '@angular/core';
import { AuthService } from '../../services/auth-service/auth.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { DialogService } from '../../services/dialog-service/dialog-service.service';
import { LocalStorageService } from '../../services/storage-service/local-storage.service';
import { Router } from '@angular/router';
import { NzNotificationService } from 'ng-zorro-antd/notification';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  //! in TypeScript è l'operatore di asserzione non-null; quando dichiaro una variabile in TS il compilatore 
  //verifica che la variabile sia inizializzata prima di essere utilizzata, se non è inizializzata o se 
  //potrebbe essere null/undefined genera un errore
  //nel mio caso non la sto inizializzando immediatamente nella dichiarazione ma successivamente, all'interno 
  //del metodo ngOnInit(); senza il ! il compilatore avrebbe generato un errore, ma aggiungendo il ! evito 
  //l'errore, sapendo che validateForm verrà inizializzato successivamente in ngOnInit()
  validateForm: FormGroup; //variabile di tipo FormGroup che rappresenta l'intero form

  isSpinning = false //booleano per mostrare/nascondere lo spinner di caricamento



  //il costruttore inietta il servizio FormBuilder, che semplifica la creazione di form reattivi
  //l'authService è necessario per effettuare la POST al backend (login dell'utente)
  constructor(
    private authService: AuthService,
    private fb: FormBuilder,
    private dialogService: DialogService,
    private localStorageService: LocalStorageService,
    private router: Router,
    private NZnotification: NzNotificationService
  ) {
    this.validateForm = this.fb.group({
      //quando uso FormBuilder per creare un form reattivo ogni campo del form è definito come un array con
      //due elementi ---> nomeCampo: [valoreIniziale, regoleDiValidazione]
      //--->  valoreIniziale: è il valore predefinito del campo quando il form viene creato
      //      --> "" corrisponde alla stringa vuota; se volessi potrei mettere "MarioRossi123" come valore iniziale
      //      --> potrei usare null ma lo evito perché porta ad errori nella gestione di logiche più complesse
      //--->  regoleDiValidazione: è un array di funzioni che definiscono le regole di validazione per quel campo
      email: ["", [Validators.required]],
      //Validators.required: verifica che il campo non sia vuoto
      password: ["", [Validators.required]],
      ricordami: [false] //imposta il valore iniziale della checkbox su false (non selezionato)
    }); 
  }
  //il costruttore inietta il servizio FormBuilder, che semplifica la creazione di form reattivi
  //l'authService è necessario per effettuare la POST al backend (login dell'utente)



  //metodo del ciclo di vita di Angular che viene eseguito quando un componente viene inizializzato
  ngOnInit(){
    var emailLocalStorage = this.localStorageService.getEmail();
    var passwordLocalStorage = this.localStorageService.getPassword();

    //setto email e password se sono memorizzate nel localstorage
    if(emailLocalStorage && passwordLocalStorage){
      this.validateForm.patchValue({
        email: emailLocalStorage,
        password: passwordLocalStorage,
        ricordami: true
      });
    }    
  }



  login(){
    //console.log(this.validateForm.value)
    // Verifica se il form è valido
    if (this.validateForm.valid) {
      //se il form è valido procede con la registrazione
      this.isSpinning = true;
      this.authService.login(this.validateForm.value).subscribe({
        next: (response) => {
          //this.NZnotification.success("Operazione completata", "Login effettuato con successo!", {nzDuration: 5000});
          //this.dialogService.openSuccessDialog("Operazione completata", "Login avvenuto con successo!");
          //console.log(response);
          if(this.localStorageService.isAdminLoggedIn()){
            this.router.navigateByUrl("/admin/dashboard");
            this.NZnotification.success("Accesso effettuato!", "Felice di rivederti capo!", {nzDuration: 5000});
          }
          else if(this.localStorageService.isUserLoggedIn()){
            var name = this.localStorageService.getName();
            this.router.navigateByUrl("/user/dashboard");
            this.NZnotification.success("Accesso effettuato!", "Bentornato " + name + "!", {nzDuration: 5000});
          }
        }
        //, //errore "credenziali non corrette" gestito dall'interceptor (stato 401)
        //error: (error) => {
        //  this.dialogService.openErrorDialog("Errore durante il login", error.error);
        //}
      });
      this.isSpinning = false;
    }
    else{
      //se il form non è valido segnala gli errori e interrompe l'esecuzione
      this.dialogService.openErrorDialog("Errore", "Compila tutti i campi!");
    }
  }
//dopo aver modificato il file html l'utente non può premere il pulsante finché
//tutti i campi non sono corretti, quindi la verifica è praticamente inutile



}
