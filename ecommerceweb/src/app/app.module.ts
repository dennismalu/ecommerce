import { NgModule } from '@angular/core';
import { BrowserModule, provideClientHydration } from '@angular/platform-browser';
import { NgZorroImportsModule } from './ng-zorro-imports'; 

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { NZ_I18N } from 'ng-zorro-antd/i18n';
import { it_IT } from 'ng-zorro-antd/i18n';
import { registerLocaleData } from '@angular/common';
import it from '@angular/common/locales/it';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { HTTP_INTERCEPTORS, provideHttpClient } from '@angular/common/http';
import { RegisterComponent } from './components/register/register.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { HttpClientModule } from '@angular/common/http';
import { MatDialogModule } from '@angular/material/dialog';
import { ErrorDialogComponent } from './components/dialogs/error-dialog/error-dialog.component';
import { SuccessDialogComponent } from './components/dialogs/success-dialog/success-dialog.component';
import { LoginComponent } from './components/login/login.component';
import { HomeComponent } from './components/home/home.component';
import { AdminModule } from './admin/admin.module';
import { UserModule } from './user/user.module';
import { AdminDashboardComponent } from './components/admin/admin-dashboard/admin-dashboard.component';
import { UserDashboardComponent } from './components/user/user-dashboard/user-dashboard.component';
import { PageNotFoundComponent } from './components/page-not-found/page-not-found.component';
import { FooterComponent } from './components/footer/footer.component';
import { NewCategoryComponent } from './components/admin/new-category/new-category.component';
import { ResponseErrorTokenInterceptor } from './interceptors/response-error-token-interceptor/response-error-token.interceptor';
import { RequestAddTokenInterceptor } from './interceptors/request-add-token-interceptor/request-add-token.interceptor';
import { NewProductComponent } from './components/admin/new-product/new-product.component';
import { AllProductsComponent } from './components/all-products/all-products.component';
import { UpdateProductComponent } from './components/admin/update-product/update-product.component';
import { AllProductsFilterBackendComponent } from './components/all-products-filter-backend/all-products-filter-backend.component';
import { AllProductsDisabledFilterBackendComponent } from './components/admin/all-products-disabled-filter-backend/all-products-disabled-filter-backend.component';
import { SearchProductComponent } from './components/search-product/search-product.component';
registerLocaleData(it);

@NgModule({
  declarations: [
    AppComponent,
    RegisterComponent,
    NavbarComponent,
    ErrorDialogComponent,
    SuccessDialogComponent,
    LoginComponent,
    HomeComponent,
    AdminDashboardComponent,
    UserDashboardComponent,
    PageNotFoundComponent,
    FooterComponent,
    NewCategoryComponent,
    NewProductComponent,
    AllProductsComponent,
    UpdateProductComponent,
    AllProductsFilterBackendComponent,
    AllProductsDisabledFilterBackendComponent,
    SearchProductComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    NgZorroImportsModule,
    ReactiveFormsModule,
    //HttpClientModule,
    MatDialogModule,
    AdminModule,
    UserModule
  ],
  providers: [
    provideClientHydration(),
    { 
      provide: NZ_I18N, 
      useValue: it_IT 
    },
    provideAnimationsAsync(),
    provideHttpClient(),

    //interceptor che gestisce le risposte con codice 401 (token scaduto o non valido)
    {
      provide: HTTP_INTERCEPTORS,
      useClass: ResponseErrorTokenInterceptor,
      multi: true //IMPORTANTE: permette di usare più interceptor
    },
    
    //interceptor che aggiunge il token (se presente) alle richieste in uscita 
    { 
      provide: HTTP_INTERCEPTORS,
      useClass: RequestAddTokenInterceptor, 
      multi: true }

  ],
  
  bootstrap: [AppComponent]
})
export class AppModule { }

