import { Component, inject, OnInit } from '@angular/core';
import { NavbarComponent } from "../../commons/navbar/navbar.component";
import { EmployeeWithImage, Resources, Service } from '../../../interfaces/interfaces';
import { UserService } from '../../../services/user.service';
import Swal from 'sweetalert2';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { CompanyCurrencyPipe } from '../../../pipes/company-currency.pipe';
import { MatDividerModule } from '@angular/material/divider';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-reserve',
  standalone: true,
  imports: [
    NavbarComponent,
    CommonModule,
    CompanyCurrencyPipe,
    MatDividerModule,
    ReactiveFormsModule
  ],
  templateUrl: './reserve.component.html',
  styleUrl: './reserve.component.scss'
})
export class ReserveComponent implements OnInit {
  _userService = inject(UserService);
  _router = inject(Router);
  _fb = inject(FormBuilder);

  services: Service[] = [];
  resources: Resources[] = [];
  employees: EmployeeWithImage[] = [];
  selectedServices: Service[] = [];
  selectedResource: Resources | null = null;
  selectedEmployee: EmployeeWithImage | null = null;
  currentStep = "Servicios";
  total: number = 0;
  servicesForm!: FormGroup;
  random: any = {
    name: "Cualquiera",
    role: "Máxima disponibilidad",
    imageProfile: "random.svg"
  }

  steps: any[] = [
    { id: 1, name: "Servicios" },
    { id: 2, name: "Recursos" },
    { id: 3, name: "Empleados" },
    { id: 4, name: "Agenda" },
    { id: 5, name: "Confirmación" }
  ];

  ngOnInit(): void {
    this.servicesForm = this._fb.group({});
    this.getServices();
  }

  private getServices() {
    this._userService.getAllServices().subscribe({
      next: (response: Service[]) => {
        this.services = response;
        this.services.forEach(service => {
          const control = new FormControl();
          control.setValue(false);
          this.servicesForm.addControl(service.name, control);
        })
      },
      error: err => {
        Swal.fire({
          title: 'Error!',
          text: 'Error al conseguir los servicios.',
          icon: 'error',
          confirmButtonText: 'Ok!'
        });
        this._router.navigate(['/home']);
      }
    });
  }

  onServiceToggle(service: Service, isChecked: boolean): void {
    if (isChecked) {
      this.selectedServices.push(service);
      this.total += service.price;
      return;
    }
    this.selectedServices = this.selectedServices.filter(s => s.id !== service.id);
    this.total -= service.price;
  }

  verifyServices() {
    if (this.selectedServices.length === 0) {
      Swal.fire({
        title: 'Error!',
        text: 'Por favor seleccione un servicio.',
        icon: 'error',
        confirmButtonText: 'Ok!'
      })
      return;
    }

    const ids: number[] = this.selectedServices.map((service) => service.id);
    this._userService.getResourcesByServicesId(ids).subscribe({
      next: response => {
        this.resources = response;
        this.currentStep = "Recursos";
        if(this.resources.length === 0) {
          this.verifyResource();
        }
      },
      error: error => {
        console.error(error)
      }
    })
  }


  verifyResource() {
    const ids: number[] = this.selectedServices.map((service) => service.id);
    this._userService.getEmployeesByServicesId(ids).subscribe({
      next: response => {
        this.employees = response;
        this.currentStep = "Empleados";
        if(this.employees.length === 0) {
          this.currentStep = "Horario";
        }
      }
    })
  }

}
