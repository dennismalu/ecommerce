import { TestBed } from '@angular/core/testing';
import { CanActivateFn, Router } from '@angular/router';
import { LocalStorageService } from '../../services/storage-service/local-storage.service';
import { LoggedInGuard } from './logged-in.guard';

describe('LoggedInGuard', () => {
beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [LoggedInGuard]
    });
  });

  it('should be created', () => {
    const guard = TestBed.inject(LoggedInGuard);
    expect(guard).toBeTruthy();
  });
});


