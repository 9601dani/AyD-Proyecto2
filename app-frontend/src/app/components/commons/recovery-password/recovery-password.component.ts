import { Component, inject, OnInit } from '@angular/core';
import { NavbarComponent } from "../navbar/navbar.component";
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';
import { CommonModule } from '@angular/common';
import Swal from 'sweetalert2';
import { Router } from '@angular/router';

@Component({
  selector: 'app-recovery-password',
  standalone: true,
  imports: [
    NavbarComponent,
    CommonModule,
    ReactiveFormsModule
  ],
  templateUrl: './recovery-password.component.html',
  styleUrl: './recovery-password.component.scss'
})
export class RecoveryPasswordComponent {

  _authService = inject(AuthService);
  emailControl: FormControl = new FormControl('', [Validators.required, Validators.email]);
  _router = inject(Router);

  recoveryPassword() {
    if(this.emailControl.invalid) {
      Swal.fire({
        title: 'Error!',
        text: 'Por favor ingresa un correo válido.',
        icon: 'error',
        confirmButtonText: 'Ok!'
      })
      return;
    }

    const email = this.emailControl.value;

    this._authService.sendRecoveryPassword(email).subscribe({
      next: (response) => {
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
      },
      complete: () => {
        this._router.navigate(['/login'])
      }
    });
  }

}
