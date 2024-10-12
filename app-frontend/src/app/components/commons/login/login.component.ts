import { CommonModule } from '@angular/common';
import { Component, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { MatIconModule } from "@angular/material/icon";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from "@angular/forms";
import Swal from 'sweetalert2';
import { RegisterModalComponent } from '../register-modal/register-modal.component';
import { AuthService } from '../../../services/auth.service';
import { CookieService } from 'ngx-cookie-service';
import { LocalStorageService } from '../../../services/local-storage.service';
import { Router } from '@angular/router';
import { NotLogoDirective } from '../../../directives/not-logo.directive';
import { NavbarComponent } from '../navbar/navbar.component';

@Component({
  selector: 'app-login',
  standalone: true,
  
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RegisterModalComponent,
    MatIconModule,
    MatProgressSpinnerModule,
    NotLogoDirective,
    NavbarComponent
  ],
  providers: [CookieService],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class LoginComponent{
  loginForm: FormGroup;
  registerForm: FormGroup;
  hide = true;
  isModalVisible = false;
  registerModalTitle = "¡Regístrate!";
  logoUrl = "";
  hidePassword = true;
  isLoading = false;
  isLoginMode = false;
  
  constructor(
    private fb: FormBuilder,
    private _authService: AuthService,
    private _cookieService: CookieService,
    private _localStorageService: LocalStorageService,
    private _router: Router
   
  ) {
    this.loginForm = this.fb.group({
      usernameOrEmail: ["", Validators.required],
      password: ["", Validators.required],
    });

    this.registerForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      username: ['', Validators.required],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required]
    }, { validators: this.passwordMatchValidator });
  }
  passwordMatchValidator(form: FormGroup) {
    const password = form.get('password')?.value || '';
    const confirmPassword = form.get('confirmPassword')?.value || '';
    return password === confirmPassword ? null : { passwordMismatch: true };
  }

  openRegisterModal() {
    this.isModalVisible = true;
    document.body.classList.add("is-modal-active");
  }

  closeRegisterModal() {
    this.isModalVisible = false;
    document.body.classList.remove("is-modal-active");
    this.isLoading = false;
  }

  onRegister() {
    if (this.registerForm.valid) {
      const data = {
        email: this.registerForm.get("email")?.value,
        username: this.registerForm.get("username")?.value,
        password: this.registerForm.get("password")?.value,
      };
      const registerData = this.registerForm.value;
      this.isLoading = true;

      setTimeout(() => {
        this._authService.registerUser(registerData).subscribe(
          (response) => {
            this.isLoading = false;
            Swal.fire("¡Registro exitoso!", "Por favor, inicia sesión", "success");
            this.registerForm.reset();
            this.isModalVisible = false;
            document.body.classList.remove("is-modal-active");
          }, error => {
            Swal.fire({
              title: 'Error!',
              text: 'No se pudo registrar al usuario.',
              icon: 'error'
            })
          }
        );
      }
      , 1000);
      this.registerForm.reset();
    } else {
      Swal.fire("Error", "Por favor, complete los campos requeridos", "error");
      this.isLoading = false;
      this.registerForm.reset();
      return;
    }
  }

  get usernameOrEmailHasErrorRequired() {
    return this.loginForm.get("usernameOrEmail")?.hasError("required");
  }

  get passwordHasErrorRequired() {
    return this.loginForm.get("password")?.hasError("required");
  }
  togglePasswordVisibility() {
    this.hidePassword = !this.hidePassword;
  }

  forgotPassword() {
    Swal.fire("¡Lo sentimos!", "Esta funcionalidad aún no está disponible", "info");
    // this._router.navigate(["/forgot-password"]);
  }

  onLogin() {
    this.isLoading = true;

    if (this.loginForm.invalid) {
      Swal.fire('Error', 'Por favor, complete los campos requeridos', 'error');
      return;
    }

    const data = {
      usernameOrEmail: this.loginForm.get('usernameOrEmail')?.value,
      password: this.loginForm.get('password')?.value
    };

    this._authService.login(data)
    .subscribe({
      next: response => {
        this._cookieService.set('token', response.token);
        this._localStorageService.setUserId(response.id);
        this._localStorageService.setUsername(response.username);
        this._router.navigate(['/home']);
      },
      error: error => {
        Swal.fire({
          title: "Error!",
          text: error.error.message,
          icon: "error"
        })
      },
      complete: () => {
        this.isLoading = false;
      }
    })
  
  }


  
}
