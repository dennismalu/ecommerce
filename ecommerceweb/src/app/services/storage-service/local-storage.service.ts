import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LocalStorageService {

  private userLoggedInSubject = new BehaviorSubject<boolean>(this.isUserLoggedIn());
  private adminLoggedInSubject = new BehaviorSubject<boolean>(this.isAdminLoggedIn());

  // Observable esposti ai componenti
  userLoggedInObservable = this.userLoggedInSubject.asObservable();
  adminLoggedInObservable = this.adminLoggedInSubject.asObservable();


  constructor(@Inject(PLATFORM_ID) private platformId: any) { }


  // Metodo helper per verificare se siamo nel browser
  private isBrowser(): boolean {
    return isPlatformBrowser(this.platformId);
  }





  saveTheme(theme: any){
    if (this.isBrowser()) {
      localStorage.removeItem('theme')
      localStorage.setItem('theme', theme);
    }
  }

  getTheme(): string | null {
    if (this.isBrowser()) {
      const theme = localStorage.getItem('theme');
      if(!theme) return null;
      return theme;
    } 
    return null;
  }

  removeTheme(): void{
    if (this.isBrowser()) {
      localStorage.removeItem('theme');
    }
  }



  saveName(name: any){
    if (this.isBrowser()) {
      localStorage.removeItem('name')
      localStorage.setItem('name', name);
    }
  }

  getName(): string | null {
    if (this.isBrowser()) {
      const name = localStorage.getItem('name');
      if(!name) return null;
      return name;
    } 
    return null;
  }

  removeName(): void{
    if (this.isBrowser()) {
      localStorage.removeItem('name');
    }
  }



  saveUserRole(userRole: any){
    if (this.isBrowser()) {
      localStorage.removeItem('userRole')
      localStorage.setItem('userRole', userRole);

      // Aggiorna i BehaviorSubjects
      this.userLoggedInSubject.next(this.isUserLoggedIn()); //login utente
      this.adminLoggedInSubject.next(this.isAdminLoggedIn()); //login admin
    }
  }

  getUserRole(): string | null {
    if (this.isBrowser()) {
      const role = localStorage.getItem('userRole');
      if(!role) return null;
      return role;
    } 
    return null;
  }

  hasUserRole(): boolean{
    return this.getUserRole() != null
  }

  removeUserRole(): void{
    if (this.isBrowser()) {
      localStorage.removeItem('userRole');

      // Aggiorna i BehaviorSubjects
      this.userLoggedInSubject.next(false); //logout utente
      this.adminLoggedInSubject.next(false); //logout admin
    }
  }

  isUserLoggedIn(): boolean{
    return this.getUserRole() == "USER"
  }

  isAdminLoggedIn(): boolean{
    return this.getUserRole() == "ADMIN"
  }





  saveToken(jwtToken: any){
    if (this.isBrowser()) {
      localStorage.removeItem('jwtToken')
      localStorage.setItem('jwtToken', jwtToken);
    }
  }

  hasToken(): boolean{
    return this.getToken() != null
  }
  

  getToken(): string | null {
    if (this.isBrowser()) {
      const jwtToken = localStorage.getItem('jwtToken');
      if(!jwtToken) return null;
      return jwtToken;
    }
    return null;
  }
  
  removeToken(): void{
    if (this.isBrowser()) {
      localStorage.removeItem('jwtToken');
    }
  }




  saveEmail(email: any){
    if (this.isBrowser()) {
      localStorage.removeItem('email')
      localStorage.setItem('email', email);
    }
  }

  getEmail(): string | null {
    if (this.isBrowser()) {
      const email = localStorage.getItem('email');
      if(!email) return null;
      return email;
    }
    return null;
  }
  
  removeEmail(): void{
    if (this.isBrowser()) {
      localStorage.removeItem('email');
    }
  }




  savePassword(password: any){
    if (this.isBrowser()) {
      localStorage.removeItem('password')
      localStorage.setItem('password', password);
    }
  }

  getPassword(): string | null {
    if (this.isBrowser()) {
      const password = localStorage.getItem('password');
      if(!password) return null;
      return password;
    }
    return null
  }
  
  removePassword(): void{
    if (this.isBrowser()) {
      localStorage.removeItem('password');
    }
  }


}


