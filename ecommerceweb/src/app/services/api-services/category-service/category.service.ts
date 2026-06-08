import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../../environments/environments';
import { Observable } from 'rxjs';
import { LocalStorageService } from '../../storage-service/local-storage.service';
import { AuthService } from '../../auth-service/auth.service';


const BASIC_URL = environment['BASIC_URL']


@Injectable({
  providedIn: 'root'
})
export class CategoryService {

  constructor(
    private http: HttpClient,
    private localStorageService: LocalStorageService,
    private authService: AuthService
  ) { }


  getAllCategories(): Observable<any>{
    return this.http.get(BASIC_URL + "api/public/categories")
  }

  newCategory(categoryDTO: any): Observable<any>{
    return this.http.post(BASIC_URL + "api/admin/category", categoryDTO)
  }

}