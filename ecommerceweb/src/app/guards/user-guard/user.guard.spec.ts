import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';
import { UserGuard } from './user.guard';

describe('UserGuard', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [UserGuard]
    });
  });

  it('should be created', () => {
    const guard = TestBed.inject(UserGuard);
    expect(guard).toBeTruthy();
  });
});