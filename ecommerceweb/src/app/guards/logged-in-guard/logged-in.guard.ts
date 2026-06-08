import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { LocalStorageService } from '../../services/storage-service/local-storage.service';
import { NzNotificationService } from 'ng-zorro-antd/notification';
import { AuthService } from '../../services/auth-service/auth.service';

@Injectable({
  providedIn: 'root'
})
export class LoggedInGuard implements CanActivate {

  constructor(
    private router: Router,
    private NZnotification: NzNotificationService,
    private localStorageService: LocalStorageService,
    private authService: AuthService
  ){}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean { 
    if(this.localStorageService.hasToken() && this.localStorageService.isUserLoggedIn()){
      var name = this.localStorageService.getName();
      this.router.navigateByUrl("/user/dashboard");
      this.NZnotification.success("Accesso effettuato!", "Bentornato " + name + "!", {nzDuration: 5000});
      return false;
    }
    else if(this.localStorageService.hasToken() && this.localStorageService.isAdminLoggedIn()){
      this.router.navigateByUrl("/admin/dashboard");
      this.NZnotification.success("Accesso effettuato!", "Felice di rivederti capo!", {nzDuration: 5000});
      return false;
    }
    else{
      //manca qualche dato (problema, soprattutto se manca lo userRole)
      this.authService.logout()
    }
    return true;
  }
}
