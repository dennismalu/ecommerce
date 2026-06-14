import { Component } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth-service/auth.service';
import { DialogService } from '../../services/dialog-service/dialog-service.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register', //tag HTML per usare questo componente
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  //! in TypeScript è l'operatore di asserzione non-null; quando dichiaro una variabile in TS il compilatore 
  //verifica che la variabile sia inizializzata prima di essere utilizzata, se non è inizializzata o se 
  //potrebbe essere null/undefined genera un errore
  //nel mio caso non la sto inizializzando immediatamente nella dichiarazione ma successivamente, all'interno 
  //del metodo ngOnInit(); senza il ! il compilatore avrebbe generato un errore, ma aggiungendo il ! evito 
  //l'errore, sapendo che validateForm verrà inizializzato successivamente in ngOnInit()
  validateForm: FormGroup; //variabile di tipo FormGroup che rappresenta l'intero form

  isSpinning = false //booleano per mostrare/nascondere lo spinner di caricamento



  //il costruttore inietta il servizio FormBuilder, che semplifica la creazione di form reattivi
  //l'authService è necessario per effettuare la POST al backend (registrare un utente)
  constructor(
    private router: Router,
    private fb: FormBuilder, 
    private authService: AuthService,
    private dialogService: DialogService  
  ) {
    this.validateForm = this.fb.group({
      //quando uso FormBuilder per creare un form reattivo ogni campo del form è definito come un array con
      //due elementi ---> nomeCampo: [valoreIniziale, regoleDiValidazione]
      //--->  valoreIniziale: è il valore predefinito del campo quando il form viene creato
      //      --> "" corrisponde alla stringa vuota; se volessi potrei mettere "MarioRossi123" come valore iniziale
      //      --> potrei usare null ma lo evito perché porta ad errori nella gestione di logiche più complesse
      //--->  regoleDiValidazione: è un array di funzioni che definiscono le regole di validazione per quel campo
      name: ["", [Validators.required]],
      //Validators.required: verifica che il campo non sia vuoto
      email: ["", [
        Validators.required, 
        Validators.pattern(/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/)   ]],
        //Validators.email: validatore predefinito di Angular che controlla il formato dell'email
        //ha un problema: accetta email del tipo utente@gmail (anche senza .com) --> ho preferito la regex
      password: ["", [
        Validators.required,
        Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!/@#$%^&*(),.?+\-":{}|<>]).{8,}$/)   ]],
      confirmPassword: ["", [Validators.required]]
    },
    { validators: this.confirmationvalidator }  );

    //La funzione confirmationvalidator verifica se il valore del campo confirmPassword 
    //corrisponde al valore del campo password; tuttavia questa funzione viene eseguita 
    //solo quando il campo confirmPassword viene modificato, non quando il campo password
    //cambia: il campo confirmPassword dipende dal valore del campo password, ma Angular 
    //non sa automaticamente che deve ricalcolare la validazione di confirmPassword quando 
    //password cambia, di conseguenza se modifico il campo password dopo aver inserito un 
    //valore diverso in confirmPassword la validazione di confirmPassword non viene aggiornata.
    //Per risolvere questo problema devo garantire che il campo confirmPassword venga ricalcolato 
    //ogni volta che il campo password cambia: sfrutto il metodo updateValueAndValidity() di 
    //Angular, che forza il ricalcolo della validazione di un campo.
  }



  //metodo del ciclo di vita di Angular che viene eseguito quando un componente viene inizializzato
  ngOnInit(){
    //listener per il campo 'password' 
    this.validateForm.get('password')?.valueChanges.subscribe(() => { //.subscribe((newValue) => {
      //console.log('Nuovo valore del campo password:', newValue);
      this.validateForm.get('confirmPassword')?.updateValueAndValidity();
    });
    //this.validateForm.get('password') restituisce il controllo del campo password come un 
    //oggetto di tipo FormControl, tuttavia se il campo password non esiste nel form (ad 
    //esempio se il form non è stato ancora inizializzato correttamente) get('password') 
    //restituisce null; se provassimo ad accedere direttamente a .valueChanges su un valore 
    //null otterremmo un errore di runtime; utilizzando ? se this.validateForm.get('password') 
    //è null o undefined l'espressione si interrompe senza tentare di accedere a .valueChanges, 
    //evitando errori; solo se this.validateForm.get('password') è un oggetto valido viene 
    //eseguito il codice successivo (cioè .valueChanges.subscribe(...))

    //valueChanges è un Observable fornito da Angular per ogni campo del form (FormControl): 
    //un Observable è un flusso di dati che emette nuovi valori ogni volta che il valore del 
    //campo cambia; il metodo subscribe() permette di osservare i cambiamenti emessi 
    //dall'Observable: ogni volta che il valore del campo cambia, il codice all'interno della 
    //funzione passata a subscribe() viene eseguito.
  }



  //nomeFunzione = (parametro): {tipo di ritorno}
  confirmationvalidator (group: FormGroup){   //: { [s: string]: boolean } {
    //FormControl (vecchio metodo): classe di Angular che rappresenta un singolo campo di un form reattivo;
    //   contiene il valore del campo, lo stato (valido/non valido), e altre informazioni utili.
    //La funzione restituisce un oggetto con chiavi di tipo stringa (s) e valori di tipo booleano

    const password = group.get('password');
    const confirmPassword = group.get('confirmPassword');

    
    //se il campo "password" non è valido non va eseguito il controllo su "confirmPassword"
    if (password?.invalid) {
      confirmPassword?.setErrors(null); //nessun errore da restituire, bisogna segnalare solo l'errore di "Password"
      return; 
    }


    //la password inserita è valida
    if (!confirmPassword?.value) { //controllo se il valore del campo è vuoto (null, undefined o stringa vuota "")
      confirmPassword?.setErrors({ required: true }); //il campo è obbligatorio e non è stato compilato
      return;
    }
    else if (confirmPassword?.value !== password?.value) {
      confirmPassword?.setErrors({ confirm: true });
      return;
    }
    confirmPassword?.setErrors(null); //tutto valido: rimuove tutti gli errori da confirmPassword
  }



  
  register(){
    //console.log(this.validateForm.value)
    // Verifica se il form è valido
    if (this.validateForm.valid) {
      //se il form è valido procede con la registrazione
      this.isSpinning = true;
      this.authService.register(this.validateForm.value).subscribe({
        next: (response) => {
          //console.log("Registrazione avvenuta con successo:", response);
          this.dialogService.openSuccessDialog("Operazione completata", "Registrazione avvenuta con successo!");
          this.router.navigateByUrl("/user/dashboard");
        },
        error: (error) => {
          //console.error("Errore durante la registrazione:", error);
          this.dialogService.openErrorDialog("Errore durante la registrazione", error.error);
        }
      });
      this.isSpinning = false;
    }
    else{
      //se il form non è valido segnala gli errori e interrompe l'esecuzione
      this.dialogService.openErrorDialog("Errore", "Compila tutti i campi correttamente!");
    }
  }
//dopo aver modificato il file html l'utente non può premere il pulsante finché
//tutti i campi non sono corretti, quindi la verifica è praticamente inutile

}
