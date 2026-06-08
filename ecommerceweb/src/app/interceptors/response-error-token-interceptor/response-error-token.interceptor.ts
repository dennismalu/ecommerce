import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { catchError, EMPTY, Observable, throwError } from "rxjs";
import { DialogService } from "../../services/dialog-service/dialog-service.service";
import { of } from 'rxjs';
import { NzNotificationService } from "ng-zorro-antd/notification";
import { Router } from "@angular/router";
import { AuthService } from "../../services/auth-service/auth.service";

@Injectable()
export class ResponseErrorTokenInterceptor implements HttpInterceptor {
  constructor(
    private router: Router,
    private dialogService: DialogService,
    private NZnotification: NzNotificationService,
    private authService: AuthService
  ) {}

  intercept(req: HttpRequest<any>, next: HttpHandler) : Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          // Token scaduto o non valido: log in console invece del redirect
          console.warn('Token non valido o scaduto');
          console.log('Dettagli errore:', error);
          this.authService.logout();


          // Ottieni il percorso corrente
          const currentPath = this.router.url;
          //console.log(currentPath)
          
          // Messaggio personalizzato in base alla pagina
          const messageConfig = this.getMessageByPath(error, currentPath);
          
          this.dialogService.openErrorDialog(messageConfig.title, messageConfig.message);
          //this.NZnotification.error(messageConfig.title, messageConfig.message, {nzDuration: 5000});

          return EMPTY;
        }

        // Ri-lancia l'errore in modo che i componenti possano gestirlo ulteriormente se necessario
        return throwError(() => error);
        
      })
    );
  }


  private getMessageByPath(error: any, currentPath: string): { title: string; message: string } {
    //dati di accesso sbagliati
    if (currentPath.startsWith('/login')) {
      return {
        title: "Errore durante il login",
        message: error.error
      };
    }

    //messaggio di default per tutte le altre pagine --> token scaduto o non valido
    this.router.navigateByUrl("/login");
    return {
      title: "Sessione scaduta",
      message: "Per favore riaccedi alla piattaforma!"
    };
  }


}

