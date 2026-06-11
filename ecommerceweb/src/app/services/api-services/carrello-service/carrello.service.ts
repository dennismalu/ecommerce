import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { LocalStorageService } from '../../storage-service/local-storage.service';
import { AuthService } from '../../auth-service/auth.service';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environments';


const BASIC_URL = environment['BASIC_URL']

@Injectable({
  providedIn: 'root'
})
export class CarrelloService {

  constructor(
    private http: HttpClient,
    private localStorageService: LocalStorageService,
    private authService: AuthService
  ) {}


  getCarrello(): Observable<any>{
    return this.http.get(BASIC_URL + "api/customer/carrello")
  }

  addToCart(idProdotto: number): Observable<any>{
    return this.http.post(BASIC_URL + "api/customer/carrello/"+
      "add?idProdotto=" + idProdotto, "")
  }

  updateQuantity(idProdottoCarrello: number, quantity: number): Observable<any>{
    return this.http.post(BASIC_URL + "api/customer/carrello/update?idProdottoCarrello="
      + idProdottoCarrello + "&quantity=" + quantity, "")
  }

  removeFromCart(idProdottoCarrello: number): Observable<any>{
    return this.http.post(BASIC_URL + "api/customer/carrello/"+
      "remove?idProdottoCarrello=" + idProdottoCarrello, "")
  }



}
