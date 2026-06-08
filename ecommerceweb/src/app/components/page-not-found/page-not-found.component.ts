import { Component } from '@angular/core';
import { LocalStorageService } from '../../services/storage-service/local-storage.service';
import { AuthService } from '../../services/auth-service/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-page-not-found',
  templateUrl: './page-not-found.component.html',
  styleUrl: './page-not-found.component.css'
})
export class PageNotFoundComponent {

  buttonText: string = '';

  constructor(
    private router: Router,
    private localStorageService: LocalStorageService,
    private authService: AuthService
  ){
    if(this.localStorageService.hasToken() && this.localStorageService.isUserLoggedIn()){
      this.buttonText = "Torna alla dashboard";
    }
    else if(this.localStorageService.hasToken() && this.localStorageService.isAdminLoggedIn()){
      this.buttonText = "Torna alla dashboard";
    }
    else{
      //manca qualche dato (problema, soprattutto se manca lo userRole)
      this.buttonText = "Torna alla home";
      this.authService.logout()
    }
  }

  redirect(){
    if(this.localStorageService.hasToken() && this.localStorageService.isUserLoggedIn()){
      this.router.navigateByUrl("/user/dashboard");
    }
    else if(this.localStorageService.hasToken() && this.localStorageService.isAdminLoggedIn()){
      this.router.navigateByUrl("/admin/dashboard");
    }
    else{
      this.buttonText = "Torna alla home";
      this.router.navigateByUrl("/home");
    }
  }
}
