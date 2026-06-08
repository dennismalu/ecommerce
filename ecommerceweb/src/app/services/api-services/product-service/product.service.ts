import { Injectable } from '@angular/core';
import { environment } from '../../../../environments/environments';
import { HttpClient } from '@angular/common/http';
import { LocalStorageService } from '../../storage-service/local-storage.service';
import { AuthService } from '../../auth-service/auth.service';
import { Observable } from 'rxjs';


const BASIC_URL = environment['BASIC_URL']

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  constructor(
    private http: HttpClient,
    private localStorageService: LocalStorageService,
    private authService: AuthService
  ) { }


  getAllProducts(): Observable<any>{
    return this.http.get(BASIC_URL + "api/public/products")
  }

  //solo prodotti abilitati
  getAllProductsFilteredSortered(page: number, size: number, sort: number, category: number): Observable<any>{
    return this.http.get
      (BASIC_URL + "api/public/products-filtered-sortered?page=" + page 
        + "&size=" + size + "&sort=" + sort + "&category=" + category)
  }

  // solo prodotti disabilitati
  getAllProductsDisabledFilteredSortered(page: number, size: number, sort: number, category: number): Observable<any>{
    return this.http.get
      (BASIC_URL + "api/admin/products-disabled-filtered-sortered?page=" + page 
        + "&size=" + size + "&sort=" + sort + "&category=" + category)
  }

  // solo prodotti abilitati
  searchProductByTitleFilteredSortered(name: string, page: number, size: number, sort: number, category: number): Observable<any>{
    return this.http.get
      (BASIC_URL + "api/public/search-products-filtered-sortered/" + name + "?page=" + page 
        + "&size=" + size + "&sort=" + sort + "&category=" + category)
  }

  // tutti i prodotti prodotti (abilitati e disabilitati)
  searchProductByTitleWithDisabledFilteredSortered(name: string, withDisabled: boolean, page: number, size: number, sort: number, category: number): Observable<any>{
    let disabled: number;
    if(withDisabled){ disabled=1; } //true
    else { disabled=0; } //false
    return this.http.get
      (BASIC_URL + "api/admin/search-products-with-disabled-filtered-sortered/" + name 
        + "?withDisabled=" + disabled + "&page=" + page + "&size=" + size + "&sort=" + sort + "&category=" + category)
  }

  getProduct(id: number): Observable<any>{
    return this.http.get(BASIC_URL + "api/public/product/" + id)
  }

  newProduct(productDTO: any): Observable<any>{
    return this.http.post(BASIC_URL + "api/admin/product", productDTO)
  }
  
  updateProduct(productDTO: any): Observable<any>{
    return this.http.put(BASIC_URL + "api/admin/product", productDTO)
  }

  enableProduct(id: number): Observable<any>{
    return this.http.put(BASIC_URL + "api/admin/product/" + id + "/enable", "")
  }

  disableProduct(id: number): Observable<any>{
    return this.http.put(BASIC_URL + "api/admin/product/" + id + "/disable", "")
  }

  deleteProduct(id: number): Observable<any>{
    return this.http.delete(BASIC_URL + "api/admin/product/" + id)
  }

}

