import { TestBed } from '@angular/core/testing';
import { CanActivateFn, Router } from '@angular/router';
import { NoAuthGuard } from './no-auth.guard';
import { LocalStorageService } from '../../services/storage-service/local-storage.service';

describe('NoAuthGuard', () => {
beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [NoAuthGuard]
    });
  });

  it('should be created', () => {
    const guard = TestBed.inject(NoAuthGuard);
    expect(guard).toBeTruthy();
  });
});
