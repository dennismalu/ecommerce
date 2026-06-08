import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserGuard } from '../guards/user-guard/user.guard';
import { UserDashboardComponent } from '../components/user/user-dashboard/user-dashboard.component';
import { PageNotFoundComponent } from '../components/page-not-found/page-not-found.component';
import { SearchProductComponent } from '../components/search-product/search-product.component';

const routes: Routes = [
  { path: "dashboard", component: UserDashboardComponent, canActivate: [UserGuard] },
  { path: "search-product", component: SearchProductComponent, canActivate: [UserGuard] }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UserRoutingModule { }
