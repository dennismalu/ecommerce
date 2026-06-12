import { TestBed } from '@angular/core/testing';

import { ListaDesideriService } from './lista-desideri.service';

describe('ListaDesideriService', () => {
  let service: ListaDesideriService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ListaDesideriService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
