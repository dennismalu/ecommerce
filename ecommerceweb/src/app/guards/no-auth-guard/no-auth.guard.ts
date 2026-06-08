import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { LocalStorageService } from '../../services/storage-service/local-storage.service';
import { NzNotificationService } from 'ng-zorro-antd/notification';
import { AuthService } from '../../services/auth-service/auth.service';

@Injectable({
  providedIn: 'root'
})
export class NoAuthGuard implements CanActivate {

  constructor(
    private router: Router,
    private NZnotification: NzNotificationService,
    private localStorageService: LocalStorageService,
    private authService: AuthService
  ){}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean { 
    if(this.localStorageService.hasToken()){
      if(this.localStorageService.isUserLoggedIn()){
        this.router.navigateByUrl("/user/dashboard");
        this.NZnotification.error("Errore", "Effettua il logout per poter "+ 
          "accedere o registrarti con un altro account!", {nzDuration: 5000});
        return false;
      }
      else if(this.localStorageService.isAdminLoggedIn()){
        this.router.navigateByUrl("/admin/dashboard");
        this.NZnotification.error("Errore", "Effettua il logout per poter "+ 
          "accedere o registrarti con un altro account!", {nzDuration: 5000});
        return false;
      }
      else{
        //manca qualche dato (problema, soprattutto se manca lo userRole)
        this.authService.logout()
      }
    }
    return true;
  }
}


