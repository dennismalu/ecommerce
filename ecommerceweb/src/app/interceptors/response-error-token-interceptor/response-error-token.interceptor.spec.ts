import { TestBed } from '@angular/core/testing';
import { HttpInterceptorFn } from '@angular/common/http';

import { ResponseErrorTokenInterceptor } from './response-error-token.interceptor';

describe('ResponseErrorTokenInterceptor', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ResponseErrorTokenInterceptor]
    });
  });

  it('should be created', () => {
    const interceptor = TestBed.inject(ResponseErrorTokenInterceptor);
    expect(interceptor).toBeTruthy();
  });
});