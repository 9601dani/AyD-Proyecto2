import { Component, OnInit } from '@angular/core';
import { NavbarComponent } from '../../commons/navbar/navbar.component';
import { UserService } from '../../../services/user.service';
import { LocalStorageService } from '../../../services/local-storage.service';
import Swal from 'sweetalert2';

import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid'; 
import interactionPlugin from '@fullcalendar/interaction';
import esLocale from '@fullcalendar/core/locales/es';
import { FullCalendarComponent, FullCalendarModule } from '@fullcalendar/angular';
import { CalendarOptions } from '@fullcalendar/core/index.js';
import { Router } from '@angular/router';

@Component({
  selector: 'app-my-appointments',
  standalone: true,
  imports: [
    NavbarComponent,
    FullCalendarModule
    
  ],
  templateUrl: './my-appointments.component.html',
  styleUrl: './my-appointments.component.scss'
})
export class MyAppointmentsComponent implements OnInit {
  calendarPlugins = [dayGridPlugin, timeGridPlugin, interactionPlugin];
  esLocale = esLocale;

  calendarEvents: any[] = [];
  hasAppointments: boolean = false;

  calendarOptions: CalendarOptions = {
    initialView: 'timeGridWeek',
    plugins: [dayGridPlugin, timeGridPlugin, interactionPlugin],
    locale: esLocale,
    events: [], 
    editable: false,
    eventClick: this.handleEventClick.bind(this) 
  };

  constructor(
    private _userService: UserService,
    private _localStorage: LocalStorageService,
    private _router: Router
  ) { }

  ngOnInit(): void {
    this.loadMyAppointments();
  }

  loadMyAppointments() {
    this._userService.getMyAppointments(this._localStorage.getUserId()).subscribe((data) => {
      if(data.length > 0){
        this.loadCalendarEvents(data);
        this.hasAppointments = true;
      }else{
        this.hasAppointments = false;
        Swal.fire({
          icon: 'error',
          title: '0 citas',
          text: 'Actualmente no tienes citas'
        });
      }
    }, (error) => {
      Swal.fire({
        icon: 'error',
        title: 'Error',
        text: error.error.message
      });
    });
  }

  loadCalendarEvents(appointments: any[]) {
    const events = appointments.map(appointment => ({
      title: appointment.serviceNames[0],  
      start: appointment.startTime,        
      end: appointment.endTime,           
      extendedProps: {                     
        employeeFirstName: appointment.employeeFirstName,
        resourceName: appointment.resourceName,
        state: this.translateState(appointment.state),
        total: appointment.total,
        username: appointment.username
      },
      color: 'blue'
    }));

    this.calendarOptions.events = events;
  }

  translateState(state: string): string {
    switch (state) {
      case 'PENDING':
        return 'Pendiente';
      case 'CONFIRMED':
        return 'Confirmada';
      case 'CANCELED':
        return 'Cancelada';
      default:
        return state;
    }
  }

  handleEventClick(info: any) {
    const event = info.event.extendedProps;
    Swal.fire({
      title: info.event.title,
      html: `
        <p><strong>Empleado Asignado:</strong> ${event.employeeFirstName}</p>
        <p><strong>Recurso Asignado:</strong> ${event.resourceName}</p>
        <p><strong>Estado:</strong> ${event.state}</p>
        <p><strong>Total: Q</strong> ${event.total}</p>
        <p><strong>Usuario:</strong> ${event.username}</p>
      `,
      icon: 'info'
    });
  }

  goToReserve() {
    this._router.navigate(['appointments/reserve']);
  }
}
