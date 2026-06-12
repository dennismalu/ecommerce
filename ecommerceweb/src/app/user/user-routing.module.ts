import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserGuard } from '../guards/user-guard/user.guard';
import { UserDashboardComponent } from '../components/user/user-dashboard/user-dashboard.component';
import { PageNotFoundComponent } from '../components/page-not-found/page-not-found.component';
import { SearchProductComponent } from '../components/search-product/search-product.component';
import { CarrelloComponent } from '../components/user/carrello/carrello.component';
import { ListaDesideriComponent } from '../components/user/lista-desideri/lista-desideri.component';

const routes: Routes = [
  { path: "dashboard", component: UserDashboardComponent, canActivate: [UserGuard] },
  { path: "carrello", component: CarrelloComponent, canActivate: [UserGuard] },
  { path: "lista-desideri", component: ListaDesideriComponent, canActivate: [UserGuard] }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UserRoutingModule { }
