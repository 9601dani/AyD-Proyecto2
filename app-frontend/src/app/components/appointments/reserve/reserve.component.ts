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
import { ImagePipe } from '../../../pipes/image.pipe';
import { NotProfileDirective } from '../../../directives/not-profile.directive';
import { FullCalendarModule } from '@fullcalendar/angular';
import { CalendarOptions } from '@fullcalendar/core/index.js';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin from '@fullcalendar/interaction';
import esLocale from '@fullcalendar/core/locales/es';
import { CompanySetting } from '../../../models/CompanySetting.model';
import { LocalStorageService } from '../../../services/local-storage.service';


@Component({
  selector: 'app-reserve',
  standalone: true,
  imports: [
    NavbarComponent,
    CommonModule,
    CompanyCurrencyPipe,
    MatDividerModule,
    ReactiveFormsModule,
    ImagePipe,
    NotProfileDirective,
    FullCalendarModule
  ],
  templateUrl: './reserve.component.html',
  styleUrl: './reserve.component.scss'
})
export class ReserveComponent implements OnInit {

  _userService = inject(UserService);
  _router = inject(Router);
  _fb = inject(FormBuilder);
  _localStorageService = inject(LocalStorageService);
  calendarOptions: CalendarOptions = {
    initialView: 'timeGridWeek',
    plugins: [dayGridPlugin, timeGridPlugin, interactionPlugin],
    editable: false,
    selectable: false,
    allDaySlot: false,
    expandRows: true,
    locale: esLocale,
    slotMinTime: '07:00:00',
    slotMaxTime: `19:00:00`,
    dateClick: (info) => this.createReserve(info),
    events: []
  };

  services: Service[] = [];
  resources: Resources[] = [];
  employees: EmployeeWithImage[] = [];
  selectedServices: Service[] = [];
  selectedResource: Resources | null = null;
  selectedEmployee: EmployeeWithImage | null = null;
  currentStep = "Servicios";
  appointment: any;
  total: number = 0;
  servicesForm!: FormGroup;
  random: any = {
    name: "Cualquiera",
    role: "Máxima disponibilidad",
    imageProfile: "random.svg"
  }

  ngOnInit(): void {
    this.servicesForm = this._fb.group({});
    this.getServices();
    this.getCompanySchedule();
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

  private getCompanySchedule() {
    this._userService.getCompanySettingByKeyName("start_hour").subscribe({
      next: (response: CompanySetting) => {
        this.calendarOptions.slotMinTime = `${response.keyValue}`
      },
      error: error => {
        console.error(error)
      }
    });
    this._userService.getCompanySettingByKeyName("end_hour").subscribe({
      next: (response: CompanySetting) => {
        this.calendarOptions.slotMaxTime = `${response.keyValue}`
      },
      error: error => {
        console.error(error)
      }
    })
  }

  createReserve(info: any) {
    const username = this._localStorageService.getUserName();
    const id = this._localStorageService.getUserId();
    const minutes: number = this.selectedServices.map(service => service.timeAprox)
      .reduce((a, b) => a + b);
    const init: Date = new Date(info.date);
    const end: Date = new Date(init.getTime() + minutes * 60000);

    const userEvent = (this.calendarOptions.events as any[]).find(e => e.extendedProps?.userId === id);

    if(userEvent) {
      this.calendarOptions.events = (this.calendarOptions.events as any[]).filter(e => e.extendedProps?.userId !== id);
    }

    this.calendarOptions.events = [
      ...(this.calendarOptions.events as any[]),
      {
        title: username,
        start: init,
        end: end,
        color: 'green',
        extendedProps: {
          userId: id
        }
      }
    ]
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
        if (this.resources.length === 0) {
          this.verifyResource();
        }
      },
      error: error => {
        console.error(error)
      }
    })
  }

  selectRandom(array: any, type: 'resource' | 'employee') {
    const random = Math.floor(Math.random() * array.length);

    switch (type) {
      case 'resource':
        this.selectResource(array[random]);
        return;
      case 'employee':
        this.selectEmployee(array[random]);
        return
    }
  }

  selectResource(resource: Resources) {
    this.selectedResource = resource;
  }

  selectEmployee(employee: EmployeeWithImage) {
    this.selectedEmployee = employee;
  }

  verifyResource() {
    const ids: number[] = this.selectedServices.map((service) => service.id);
    this._userService.getEmployeesByServicesId(ids).subscribe({
      next: response => {
        this.employees = response;
        this.currentStep = "Empleados";
        if (this.employees.length === 0) {
          this.currentStep = "Horario";
        }
      }
    })
  }

  verifyEmployee() {
    this.currentStep = "Horario";
  }
}
