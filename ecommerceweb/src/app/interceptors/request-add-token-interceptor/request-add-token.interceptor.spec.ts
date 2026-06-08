import { TestBed } from '@angular/core/testing';
import { HttpInterceptorFn } from '@angular/common/http';

import { RequestAddTokenInterceptor } from './request-add-token.interceptor';

describe('RequestAddTokenInterceptor', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [RequestAddTokenInterceptor]
    });
  });

  it('should be created', () => {
    const interceptor = TestBed.inject(RequestAddTokenInterceptor);
    expect(interceptor).toBeTruthy();
  });
});
