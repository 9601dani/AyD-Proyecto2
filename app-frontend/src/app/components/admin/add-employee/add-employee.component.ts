import { Component, OnInit } from '@angular/core';
import { NavbarComponent } from '../../commons/navbar/navbar.component';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import Swal from 'sweetalert2';

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

  roles: Array<{id: number, name: string}> = [
    { id: 1, name: 'Administrador' },
    { id: 2, name: 'Empleado' }
  ];

  showPassword = false;
  showConfirmPassword = false;

  constructor(
    private fb: FormBuilder
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
    });
    this.roles = this.loadRoles();
  }

  loadRoles() {
    return this.roles;
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  toggleConfirmPasswordVisibility(): void {
    this.showConfirmPassword = !this.showConfirmPassword;
  }

  onSubmit() {
    if (this.employeeForm.valid) {
      console.log(this.employeeForm.value)
      Swal.fire({
        title: 'Registro exitoso',
        text: 'El empleado ha sido registrado correctamente.',
        icon: 'success'
      });
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
