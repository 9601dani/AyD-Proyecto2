import { Component, inject, OnInit } from '@angular/core';
import { NavbarComponent } from "../navbar/navbar.component";
import { AuthService } from '../../../services/auth.service';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import Swal from 'sweetalert2';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [
    NavbarComponent,
    CommonModule,
    ReactiveFormsModule
  ],
  templateUrl: './reset-password.component.html',
  styleUrl: './reset-password.component.scss'
})
export class ResetPasswordComponent implements OnInit {

  _authService = inject(AuthService)
  newPasswordControl = new FormControl('', [Validators.required, Validators.minLength(6)])
  confirmPasswordControl = new FormControl('', [Validators.required, Validators.minLength(6)])
  _activatedRoute = inject(ActivatedRoute);
  _router = inject(Router);
  token: string = '';


  ngOnInit(): void {
    this._activatedRoute.paramMap.subscribe(params => {
      this.token = params.get('token') || '';
    });
  }

  resetPassword() {
    if(this.newPasswordControl.invalid || 
      this.confirmPasswordControl.invalid ||
      this.newPasswordControl.value !== this.confirmPasswordControl.value) {
        Swal.fire({
          title: 'Error!',
          text: 'Por favor ingrese una contraseña válida.',
          icon: 'error',
          confirmButtonText: 'Ok!'
        })
        return;
    }

    const password = this.newPasswordControl.value;

    this._authService.resetPassword(this.token, { password }).subscribe({
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
      },
      complete: () => {
        this._router.navigate(['/login'])
      }
    })
  }
}
