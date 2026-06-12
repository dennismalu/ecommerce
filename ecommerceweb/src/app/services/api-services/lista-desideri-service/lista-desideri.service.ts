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
export class ListaDesideriService {

  constructor(
    private http: HttpClient,
    private localStorageService: LocalStorageService,
    private authService: AuthService
  ) {}
  
  
  getListaDesideri(): Observable<any>{
    return this.http.get(BASIC_URL + "api/customer/lista-desideri")
  }

  addToListaDesideri(idProdotto: number): Observable<any>{
    return this.http.post(BASIC_URL + "api/customer/lista-desideri/"+
      "add?idProdotto=" + idProdotto, "")
  }

  removeFromListaDesideri(idProdottoListaDesideri: number): Observable<any>{
    return this.http.post(BASIC_URL + "api/customer/lista-desideri/"+
      "remove?idProdottoListaDesideri=" + idProdottoListaDesideri, "")
  }

}
  
    