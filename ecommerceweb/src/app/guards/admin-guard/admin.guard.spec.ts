import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { AdminGuard } from './admin.guard';

describe('AdminGuard', () => {
beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [AdminGuard]
    });
  });

  it('should be created', () => {
    const guard = TestBed.inject(AdminGuard);
    expect(guard).toBeTruthy();
  });
});