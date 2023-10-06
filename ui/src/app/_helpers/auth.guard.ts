import { inject } from '@angular/core';
import {
  CanActivateFn, CanMatchFn,
  Router
} from '@angular/router';

import { StorageService } from '../_services/storage.service';

export const authGuard: CanMatchFn|CanActivateFn = () => {
  const storageService = inject(StorageService);
  const router = inject(Router);

  if (storageService.isLoggedIn()) {
    return true;
  }

  // Redirect to the login page
  return router.parseUrl('/login');
};
