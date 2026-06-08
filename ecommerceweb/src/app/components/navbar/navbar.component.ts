import { ChangeDetectorRef, Component, Inject, OnInit, PLATFORM_ID } from '@angular/core';
import { LocalStorageService } from '../../services/storage-service/local-storage.service';
import { AuthService } from '../../services/auth-service/auth.service';
import { NzNotificationService } from 'ng-zorro-antd/notification';
import { Router } from '@angular/router';
import { isPlatformBrowser } from '@angular/common';
import { HostListener } from '@angular/core';

@Component({
  selector: 'app-navbar', //tag HTML per usare questo componente
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {
  isBrowser = false;
  isUserLoggedIn = false;
  isAdminLoggedIn = false;
  menuOpen = false;

  constructor(
    @Inject(PLATFORM_ID) private platformId: any,
    public localStorageService: LocalStorageService,
    private authService: AuthService,
    private router: Router,
    private NZnotification: NzNotificationService
  ){
    //OBIETTIVO: la navbar non deve venire renderizzata sul server (dove non 
    //esiste localStorage), ma solo sul client, dove i dati sono disponibili
    this.isBrowser = isPlatformBrowser(this.platformId);
    if (!this.isBrowser) return; // se sono sul server esco immediatamente dal metodo

    this.isUserLoggedIn = this.localStorageService.isUserLoggedIn();
    this.isAdminLoggedIn = this.localStorageService.isAdminLoggedIn();

     //LOG
    console.log('Immediately after initializeLoginState() →', {
      role: this.localStorageService.getUserRole(),
      isUserLoggedIn: this.isUserLoggedIn,
      isAdminLoggedIn: this.isAdminLoggedIn,
      currentUserStatus: this.currentUserStatus
    });

    // Poi sottoscrivi ai cambiamenti futuri
    this.localStorageService.userLoggedInObservable.subscribe(status => {
      //console.log('User logged in status changed to:', status); //log
      this.isUserLoggedIn = status;
      //console.log('Current user status:', this.currentUserStatus); //log
    });

    this.localStorageService.adminLoggedInObservable.subscribe(status => {
      //console.log('Admin logged in status changed to:', status); //log
      this.isAdminLoggedIn = status;
      //console.log('Current user status:', this.currentUserStatus); //log
    });
  }



  toggleMenu() {
    this.menuOpen = !this.menuOpen;
  }

  closeMenu() {
    this.menuOpen = false;
  }

  logoutAndClose() {
    this.logout();
    this.closeMenu();
  }

  
  // per chiudere il menu quando si preme su un qualsiasi punto del browser
  // fuori dal menu stesso (in qualsiasi punto della pagina al di fuori del menu)
  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    // Se il menu non è aperto, non fare nulla
    if (!this.menuOpen) return;

    // Cerca l'elemento del menu (puoi usare un template reference o una classe)
    const menuElement = document.querySelector('.dropdown-menu');
    const menuToggle = document.querySelector('.menu-toggle');
    //.dropdown-menu e .menu-toggle devono corrispondere agli elementi del tuo template.

    // Se il click NON è avvenuto dentro il menu né sul pulsante "Menu", chiudi il menu;
    // la gestione del tocco sul pulsante quando il menu è già aperto è gestita da toggleMenu()
    if ( menuElement && !menuElement.contains(event.target as Node) 
          && menuToggle && !menuToggle.contains(event.target as Node)
    ) {
      this.closeMenu();
    }
  }

  
  logout(){
    this.authService.logout()
    this.NZnotification.success("Operazione completata", "Logout effettuato con successo!", {nzDuration: 5000});
    this.router.navigateByUrl("/");
  }



















  //SOLO PER DEBUG
  get currentUserStatus(): 'guest' | 'user' | 'admin' {
    if (this.isAdminLoggedIn) {
      return 'admin';
    } 
    else if (this.isUserLoggedIn) {
      return 'user';
    } 
    else {
      return 'guest';
    }
  }
}


