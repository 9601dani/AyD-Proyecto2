import { Component, OnInit } from '@angular/core';
import { NavbarComponent } from '../../commons/navbar/navbar.component';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, AbstractControl } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import Swal from 'sweetalert2';
import { Employee, Roles } from '../../../interfaces/interfaces';
import { UserService } from '../../../services/user.service';

@Component({
  selector: 'app-add-employee',
  standalone: true,
  imports: [
    NavbarComponent,
    CommonModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
  ],
  templateUrl: './add-employee.component.html',
  styleUrl: './add-employee.component.scss'
})
export class AddEmployeeComponent implements OnInit {
  employeeForm!: FormGroup;

  roles: Roles[] = [];
  showPassword = false;
  showConfirmPassword = false;

  constructor(
    private fb: FormBuilder,
    private _userService: UserService
  ) {}

  ngOnInit() {
    this.employeeForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      dateOfBirth: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      username: ['', Validators.required],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required],
      role: [1, Validators.required] 
    }, { validator: this.passwordMatchValidator });

    this.loadRoles();
  }

  // Validador personalizado para verificar que las contraseñas coincidan
  passwordMatchValidator(control: AbstractControl): { [key: string]: boolean } | null {
    const password = control.get('password');
    const confirmPassword = control.get('confirmPassword');
    if (password && confirmPassword && password.value !== confirmPassword.value) {
      return { passwordMismatch: true };
    }
    return null;
  }

  loadRoles() {
    this._userService.getAllRoles().subscribe(
      (data: Roles[]) => {
        if (data) {
          this.roles = data;
        }
      }
    );
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  toggleConfirmPasswordVisibility(): void {
    this.showConfirmPassword = !this.showConfirmPassword;
  }

  onSubmit() {
    if (this.employeeForm.valid) {
      this._userService.createEmployee(this.employeeForm.value).subscribe(
        (data: Employee) => {
          if (data) {
            console
            Swal.fire({
              title: 'Empleado creado',
              text: 'El empleado ha sido creado exitosamente.',
              icon: 'success'
            });
            this.employeeForm.reset();
          }
        },
        (error: any) => {
          Swal.fire({
            title: 'Error',
            text: 'Ha ocurrido un error al intentar crear el empleado. Favor revisa el usuario y correo electronico.',
            icon: 'error'
          });
        }
      );
      
    } else {
      Swal.fire({
        title: 'Error',
        text: 'Por favor, corrija los errores en el formulario.',
        icon: 'error'
      });
    }
  }

  onCancel() {
    this.employeeForm.reset();
  }

  onDateChange(event: any) {
    const inputDate = event.target.value;
    const [day, month, year] = inputDate.split('/');
  
    if (day && month && year && this.isValidDate(day, month, year)) {
      this.employeeForm.patchValue({
        dateOfBirth: `${year}-${month}-${day}`
      });
    } else {
      this.employeeForm.get('dateOfBirth')?.setErrors({ invalidDate: true });
    }
  }
  
  isValidDate(day: string, month: string, year: string): boolean {
    const date = new Date(`${year}-${month}-${day}`);
    return !isNaN(date.getTime()) && day.length === 2 && month.length === 2 && year.length === 4;
  }

  formatDate(date: string): string {
    const d = new Date(date);
    const day = ('0' + d.getDate()).slice(-2);
    const month = ('0' + (d.getMonth() + 1)).slice(-2);
    const year = d.getFullYear();
    return `${day}/${month}/${year}`;
  }
}
