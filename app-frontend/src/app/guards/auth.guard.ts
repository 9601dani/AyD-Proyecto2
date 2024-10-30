import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';
import { LocalStorageService } from '../services/local-storage.service';
import Swal from 'sweetalert2';

export const authGuard: CanActivateFn = (route, state) => {
  const _cookieService = inject(CookieService);
  const _localStorageService = inject(LocalStorageService);
  const _router = inject(Router);

  const token = _cookieService.get('token');
  if(!token) {
    Swal.fire({
      title: 'Error!',
      text: 'No se encuentra autenticado.',
      icon: 'error',
      confirmButtonText: 'Ok'
    })
    _router.navigate(['/home'])
    return false;
  }

  return true;
};
