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
    selectable: false,
    allDaySlot: false,
    expandRows: true,
    eventOverlap: false,
    slotEventOverlap: false,
    slotMinTime: '05:00:00',
    slotMaxTime: `22:00:00`,
    eventClick: this.handleEventClick.bind(this) 
  };

  colors: { [key: string]: string } = {
    CONFIRMADA: '#28a745',
    PENDIENTE: '#7ea3cc',
    FINALIZADA: '#183059',
    CANCELADA: '#D72638',
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
        element: appointment
      },
      color: this.colors[appointment.state]
    }));

    this.calendarOptions.events = events;
  }

  handleEventClick(info: any) {
    const currency = this._localStorage.getCurrency() || '$';

    const event = info.event.extendedProps.element;
    const totalFormated = `${currency} ${event.total}`;
        let confirmButtonText = 'Marcar como cancelada';
        let isConfirmButtonDisabled = false;

        if (event.state === 'PENDIENTE') {
          confirmButtonText = 'Marcar como cancelada';
        } else if (event.state !== 'PENDIENTE') {
          confirmButtonText = 'Acción no Disponible';
          isConfirmButtonDisabled = true;
        }
    Swal.fire({
      title: info.event.title,
      html: `
        <p><strong>Empleado Asignado:</strong> ${event.employeeFirstName}</p>
        <p><strong>Recurso Asignado:</strong> ${event.resourceName}</p>
        <p><strong>Estado:</strong> ${event.state}</p>
        <p><strong>Total: Q</strong> ${event.total}</p>
        <p><strong>Usuario:</strong> ${event.username}</p>
        <button id="btn-confirmar" class="button is-primary" ${isConfirmButtonDisabled ? 'disabled' : ''}>${confirmButtonText}</button>
        <button id="btn-cancelar" class="button is-danger"}>Cerrar</button>
      `,
      icon: 'info',
      showConfirmButton: false,
      showCancelButton: false
    });

    document.getElementById('btn-confirmar')?.addEventListener('click', () => {
      if (!isConfirmButtonDisabled) {

        if (event.state === 'PENDIENTE') {
          Swal.fire({
            title: 'Marcar como Cancelada',
            text: '¿Está seguro que desea cancelar su cita? No se realizará un reembolso una vez cancelada la cita.',
            icon: 'question',
            confirmButtonText: 'Confirmar'
          }).then(result => {
            if(result.isConfirmed) {
              this._userService.updateAppointmentState(event.id, "CANCELADA").subscribe({
                next: response => {
                  Swal.fire({
                    title: 'Éxito!',
                    text: response.message,
                    icon: 'success'
                  })
                },
                complete: () => {
                  window.location.reload();
                }
              })
            }
          })
        } else {
          Swal.fire({
            title: 'Error!',
            text: 'Acción no disponible',
            icon: 'error'
          })
        }
      }

      // Swal.close();
    });

    document.getElementById('btn-cancelar')?.addEventListener('click', () => {
      Swal.close();
    });
  }

  goToReserve() {
    this._router.navigate(['appointments/reserve']);
  }
}
