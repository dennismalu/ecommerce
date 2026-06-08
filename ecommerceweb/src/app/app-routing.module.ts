import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RegisterComponent } from './components/register/register.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { LoginComponent } from './components/login/login.component';
import { HomeComponent } from './components/home/home.component';
import { UserModule } from './user/user.module';
import { NoAuthGuard } from './guards/no-auth-guard/no-auth.guard';
import { LoggedInGuard } from './guards/logged-in-guard/logged-in.guard';
import { PageNotFoundComponent } from './components/page-not-found/page-not-found.component';
import { SearchProductComponent } from './components/search-product/search-product.component';

const routes: Routes = [
  { path: "", redirectTo: "/home", pathMatch: 'full' },
  //pathMatch: 'full' ---> l'URL deve corrispondere esattamente alla route vuota ("") per attivare il redirect
  { path: "home", component: HomeComponent }, //, canActivate: [LoggedInGuard]
  { path: "login", component: LoginComponent, canActivate: [NoAuthGuard] },
  { path: "register", component: RegisterComponent, canActivate: [NoAuthGuard] },
  { path: "admin", loadChildren: () => import("./admin/admin.module").then(m => m.AdminModule) },
  { path: "user", loadChildren: () => import("./user/user.module").then(m => m.UserModule) },


  // Route di fallback per URL non validi
  { path: "**", component: PageNotFoundComponent } 
  // la route (**) deve essere definita dopo tutte le altre altrimenti potrebbe sovrascriverle
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

