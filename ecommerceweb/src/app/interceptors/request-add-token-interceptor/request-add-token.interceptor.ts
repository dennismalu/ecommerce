import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../../services/auth-service/auth.service';
import { LocalStorageService } from '../../services/storage-service/local-storage.service';

@Injectable()
export class RequestAddTokenInterceptor implements HttpInterceptor {

  constructor(private localStorageService: LocalStorageService) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // aggiunge il token JWT come header di autenticazione
    if(this.localStorageService.hasToken()){
      const jwt = this.localStorageService.getToken();
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${jwt}`
        }
      });
    }
    return next.handle(request);
  }
}

