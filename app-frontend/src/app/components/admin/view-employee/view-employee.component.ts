import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { NavbarComponent } from '../../commons/navbar/navbar.component';
import { Router } from '@angular/router';
import { UserService } from '../../../services/user.service';
import { Employee } from '../../../interfaces/interfaces';
import Swal from 'sweetalert2';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-view-employee',
  standalone: true,
  imports: [
    NavbarComponent,
    CommonModule,
    MatButtonModule,
    MatIconModule,
    FormsModule
  ],
  templateUrl: './view-employee.component.html',
  styleUrls: ['./view-employee.component.scss']
})
export class ViewEmployeeComponent implements OnInit {

  employees: Employee[] = [];
  filteredEmployees: Employee[] = [];
  searchTerm: string = '';
  defaultImage = 'assets/default-employee.png';

  constructor(
    private employeeService: UserService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadEmployees();
  }

  loadEmployees(): void {
    this.employeeService.getAllEmployees().subscribe(
      (res: Employee[]) => {
        this.employees = res;
        this.filteredEmployees = res; 
      },
      (err) => {
        Swal.fire(
          'Error',
          'Error al cargar los empleados ' + err.error.message,
          'error'
        );
      }
    );
  }

  filterEmployees(): void {
    const searchLower = this.searchTerm.toLowerCase();
  
    this.filteredEmployees = this.employees.filter(employee => {
      return (
        (employee.firstName && employee.firstName.toLowerCase().includes(searchLower)) ||
        (employee.lastName && employee.lastName.toLowerCase().includes(searchLower)) ||
        (employee.email && employee.email.toLowerCase().includes(searchLower)) ||
        (employee.username && employee.username.toLowerCase().includes(searchLower))
      );
    });
  }

  editEmployee(employeeId: number): void {
    this.router.navigate(['/employees/edit', employeeId]);
  }
}
