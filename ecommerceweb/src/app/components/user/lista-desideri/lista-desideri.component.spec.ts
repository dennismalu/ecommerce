import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListaDesideriComponent } from './lista-desideri.component';

describe('ListaDesideriComponent', () => {
  let component: ListaDesideriComponent;
  let fixture: ComponentFixture<ListaDesideriComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ListaDesideriComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ListaDesideriComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
