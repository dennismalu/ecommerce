import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { LocalStorageService } from '../../services/storage-service/local-storage.service';
import { NzNotificationService } from 'ng-zorro-antd/notification';
import { AuthService } from '../../services/auth-service/auth.service';

@Injectable({
  providedIn: 'root'
})
export class UserGuard implements CanActivate {

  constructor(
    private router: Router,
    private NZnotification: NzNotificationService,
    private localStorageService: LocalStorageService,
    private authService: AuthService
  ){}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean { 
    // Logica per determinare se l'utente può accedere alla rotta
    if(this.localStorageService.isAdminLoggedIn()){
      this.router.navigateByUrl("/admin/dashboard");
      this.NZnotification.error("ERRORE", "Stai cercando di accedere all'area utente!", {nzDuration: 5000});
      return false;
    }
    else if(!this.localStorageService.hasToken() || !this.localStorageService.hasUserRole()){
      this.authService.logout();
      this.router.navigateByUrl("/login");
      this.NZnotification.error("ERRORE", "Per favore, riaccedi!", {nzDuration: 5000});
      return false;
    }
    return true;
  }
}


