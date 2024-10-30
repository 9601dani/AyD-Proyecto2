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

export interface Appointment {
  userId: number,
  resourceId?: number,
  startTime: string,
  endTime: string,
  employeeId?: number,
  servicesId: number[]
}

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
    eventOverlap: false,
    slotEventOverlap: false,
    validRange: {
      start: new Date().toISOString().split('T')[0]
    },
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
  currentAppointment: Appointment | null = null;
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

    const id = this._localStorageService.getUserId();

    const calendarApi = info.view.calendar;
    const events = calendarApi.getEvents();

    const minutes: number = this.selectedServices.map(service => service.timeAprox)
    .reduce((a, b) => a + b);
    const init: Date = new Date(info.date);
    const end: Date = new Date(init.getTime() + minutes * 60000);

    const slotMaxTime = new Date(info.date);
    const [hours, minutesMax] = (this.calendarOptions?.slotMaxTime as string).split(':');
    slotMaxTime.setHours(parseInt(hours, 10), parseInt(minutesMax, 10), 0, 0);

    if (end > slotMaxTime) {
      Swal.fire({
        title: 'Error!',
        text: `La cita no puede finalizar después de las ${this.calendarOptions.slotMaxTime}.`,
        icon: 'error',
        confirmButtonText: 'Ok'
      });
      return;
    }

    const hasOverlap = events.some((event: any) => {
      return init < event.end && end > event.start && event.extendedProps?.userId !== id;
    });

    if(hasOverlap) {
      Swal.fire({
        title: 'Error!',
        text: 'Horario no disponible. Por favor cambie de recurso o empleado y vuelva a intentarlo.',
        icon: 'error',
        confirmButtonText: 'Ok'
      })
      return;
    }

    const today = new Date();
    if(init.getTime() < today.getTime()) {
      Swal.fire({
        title: 'Error!',
        text: 'No puede agendar una cita en horario no disponible.',
        icon: 'error',
        confirmButtonText: 'Ok'
      })
      return;
    }

    const username = this._localStorageService.getUserName();

    const timezoneOffset = init.getTimezoneOffset() * 60000;
    const adjustedInit = new Date(init.getTime() - timezoneOffset);
    const adjustedEnd = new Date(end.getTime() - timezoneOffset);

    this.currentAppointment = {
      userId: id,
      startTime: adjustedInit.toISOString().slice(0, -1),
      endTime: adjustedEnd.toISOString().slice(0, -1),
      servicesId: this.selectedServices.map(service => service.id)
    }

    const userEvent = (this.calendarOptions.events as any[]).find(e => e.extendedProps?.userId === id);

    if (userEvent) {
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
          this.verifyEmployee()
        }
      }
    })
  }

  verifyEmployee() {
    this.currentStep = "Horario";
    // TODO: GET ALL appointments with resource or employee
    const resource = this.selectedResource === null ? 0 : this.selectedResource.id;
    const employee = this.selectedEmployee === null ? 0 : this.selectedEmployee.id;

    this._userService.findByResourceOrEmployee(resource, employee).subscribe({
      next: (response: any[]) => {
        response.forEach(element => {
          this.calendarOptions.events = [
            ...(this.calendarOptions.events as any[]),
            {
              title: element.username,
              start: element.startTime,
              end: element.endTime,
              color: 'red'
            }
          ]
        })
      }
    })
  }

  saveAppointment() {
    if (this.currentAppointment === null) {
      Swal.fire({
        title: 'Error!',
        text: 'No se ha elegido una fecha para la cita.',
        icon: 'error',
        confirmButtonText: 'Ok'
      })
      this.currentStep = "Horario";
      return;
    }

    if (this.selectedEmployee !== null) {
      this.currentAppointment.employeeId = this.selectedEmployee.id;
    }

    if (this.selectedResource !== null) {
      this.currentAppointment.resourceId = this.selectedResource.id;
    }

    console.log(this.currentAppointment);
    this._userService.saveAppointment(this.currentAppointment).subscribe({
      next: response => {
        Swal.fire({
          title: 'Éxito!',
          text: `Se ha creado la reservación No. ${response.id}`,
          icon: 'success',
          confirmButtonText: 'Ok'
        })
        this._router.navigate(['/home']);
      },
      error: err => {
        console.log(err)
      }
    })
  }

}
