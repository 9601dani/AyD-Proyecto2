import { Component, OnInit } from '@angular/core';
import { NavbarComponent } from "../navbar/navbar.component";
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';
import { Router } from '@angular/router';
import { LocalStorageService } from '../../../services/local-storage.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-verification2fa',
  standalone: true,
  imports: [
    NavbarComponent,
    ReactiveFormsModule
  ],
  templateUrl: './verification2fa.component.html',
  styleUrl: './verification2fa.component.scss'
})
export class Verification2faComponent implements OnInit {

  code = new FormControl('', [Validators.required, Validators.minLength(6)]);
  constructor(
    private _authService: AuthService,
    private _localStorageService: LocalStorageService,
    private _router: Router
  ) { }

  ngOnInit(): void {
  }

  verifyCode() {

    if(this.code.invalid) {
      Swal.fire({
        title: 'Error!',
        text: 'Por favor ingrese el código de autenticación.',
        icon: 'error',
        confirmButtonText: 'Ok!'
      })
      return;
    }

    const id = this._localStorageService.getUserId();
    const code = this.code.value;

    if(!id) {
      Swal.fire({
        title: 'Error!',
        text: 'No se encontró el id del usuario.',
        icon: 'error'
      })
      this._localStorageService.logout();
      this._router.navigate(['/login']);
      return;
    }

    this._authService.verify2fa(id, code!).subscribe({
      next: response => {
        this._router.navigate(['/home'])
      },
      error: err => {
        Swal.fire({
          title: 'Error!',
          text: err.error.message,
          icon: 'error',
          confirmButtonText: 'Ok!'
        })
      }
    })
  }

  send2fa() {
    const id = this._localStorageService.getUserId();

    if(!id) {
      Swal.fire({
        title: 'Error!',
        text: 'No se encontró la información del usuario.',
        icon: 'error',
        confirmButtonText: 'Ok!'
      })
      this._localStorageService.logout();
      this._router.navigate(['/login']);
      return;
    }

    this._authService.send2fa(id).subscribe({
      next: response => {
        Swal.fire({
          title: 'Éxito!',
          text: response.message,
          icon: 'success',
          confirmButtonText: 'Ok!'
        })
      },
      error: err => {
        const { message } = err.error;
        Swal.fire({
          title: 'Error!',
          text: message,
          icon: 'error',
          confirmButtonText: 'Ok!'
        })
      }
    })

  }

}
