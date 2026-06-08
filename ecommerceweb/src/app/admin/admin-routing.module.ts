import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdminGuard } from '../guards/admin-guard/admin.guard';
import { AdminDashboardComponent } from '../components/admin/admin-dashboard/admin-dashboard.component';
import { PageNotFoundComponent } from '../components/page-not-found/page-not-found.component';
import { NewCategoryComponent } from '../components/admin/new-category/new-category.component';
import { NewProductComponent } from '../components/admin/new-product/new-product.component';
import { UpdateProductComponent } from '../components/admin/update-product/update-product.component';
import { AllProductsDisabledFilterBackendComponent } from '../components/admin/all-products-disabled-filter-backend/all-products-disabled-filter-backend.component';
import { SearchProductComponent } from '../components/search-product/search-product.component';

const routes: Routes = [
  { path: "dashboard", component: AdminDashboardComponent, canActivate: [AdminGuard] },
  { path: "disable-products", component: AllProductsDisabledFilterBackendComponent, canActivate: [AdminGuard] },
  { path: "new-category", component: NewCategoryComponent, canActivate: [AdminGuard] },
  { path: "new-product", component: NewProductComponent, canActivate: [AdminGuard] },
  { path: "product/:id", component: UpdateProductComponent, canActivate: [AdminGuard] },
  { path: "search-product", component: SearchProductComponent, canActivate: [AdminGuard] }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AdminRoutingModule { }
