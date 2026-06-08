import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllProductsFilterBackendComponent } from './all-products-filter-backend.component';

describe('AllProductsFilterBackendComponent', () => {
  let component: AllProductsFilterBackendComponent;
  let fixture: ComponentFixture<AllProductsFilterBackendComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AllProductsFilterBackendComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AllProductsFilterBackendComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
