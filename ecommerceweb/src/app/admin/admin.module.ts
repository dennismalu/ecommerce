import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgZorroImportsModule } from '../ng-zorro-imports'; 

import { AdminRoutingModule } from './admin-routing.module';


@NgModule({
  declarations: [
  ],
  imports: [
    CommonModule,
    AdminRoutingModule,
    NgZorroImportsModule
  ]
})
export class AdminModule { }
