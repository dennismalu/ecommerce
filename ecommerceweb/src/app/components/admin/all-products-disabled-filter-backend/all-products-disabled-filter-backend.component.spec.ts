import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllProductsDisabledFilterBackendComponent } from './all-products-disabled-filter-backend.component';

describe('AllProductsDisabledFilterBackendComponent', () => {
  let component: AllProductsDisabledFilterBackendComponent;
  let fixture: ComponentFixture<AllProductsDisabledFilterBackendComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AllProductsDisabledFilterBackendComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AllProductsDisabledFilterBackendComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
