import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { environment } from '../../../environments/environments';
import { LocalStorageService } from '../storage-service/local-storage.service';

const BASIC_URL = environment["BASIC_URL"]

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(
    private http: HttpClient,
    private localStorageService: LocalStorageService
  ) {}

  

  //metodo che fa una POST sul backend
  register(signupDTO: any): Observable<any>{
    return this.http.post(BASIC_URL + "sign-up", signupDTO).pipe(
      tap((response: any) => {
        // Estrai i dati dalla risposta
        const jwtToken = response.jwtToken;
        const name = response.name;
        //const userId = response.userId;
        const userRole = response.role;

        // Salva i dati nel localStorage (se non sono vuoti)
        if (jwtToken && userRole) {
          this.localStorageService.saveToken(jwtToken);
          this.localStorageService.saveName(name);
          this.localStorageService.saveUserRole(userRole);
          
          //di default non memorizzo email e password, l'utente sceglierà se memorizzarle al prossimo accesso
          this.localStorageService.removeEmail();
          this.localStorageService.removePassword();
        } 
        else {
          console.error('Errore nei dati inviati dal server: ', response);
        }

        return response;
      })
    );
  }



  //metodo che fa una POST sul backend
  login(authenticationRequest: any): Observable<any>{
    return this.http.post(BASIC_URL + "authenticate", authenticationRequest).pipe(
      tap((response: any) => {
        // Estrai i dati dalla risposta
        const jwtToken = response.jwtToken;
        const name = response.name;
        //const userId = response.userId;
        const userRole = response.role;

        // Salva i dati nel localStorage (se non sono vuoti)
        if (jwtToken && userRole) {
          this.localStorageService.saveToken(jwtToken);
          this.localStorageService.saveName(name);
          this.localStorageService.saveUserRole(userRole);
          if (authenticationRequest.ricordami) {
            this.localStorageService.saveEmail(authenticationRequest.email);
            this.localStorageService.savePassword(authenticationRequest.password);
          }
          else{
            this.localStorageService.removeEmail();
            this.localStorageService.removePassword();
          }
        } 
        else {
          console.error('Errore nei dati inviati dal server: ', response);
        }

        return response;
      })
    );
  }



  logout(): void {
    this.localStorageService.removeToken();
    this.localStorageService.removeName();
    this.localStorageService.removeUserRole();
  }


}
