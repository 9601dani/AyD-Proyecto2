import { Component, inject, OnInit } from '@angular/core';
import Swal from 'sweetalert2';
import { UserService } from '../../../services/user.service';
import { LocalStorageService } from '../../../services/local-storage.service';
import { CalendarOptions } from '@fullcalendar/core/index.js';

import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin from '@fullcalendar/interaction';
import esLocale from '@fullcalendar/core/locales/es';
import { NavbarComponent } from '../../commons/navbar/navbar.component';
import { FullCalendarModule } from '@fullcalendar/angular';

@Component({
  selector: 'app-my-schedule',
  standalone: true,
  imports: [NavbarComponent, FullCalendarModule],
  templateUrl: './my-schedule.component.html',
  styleUrl: './my-schedule.component.scss'
})
export class MyScheduleComponent implements OnInit {

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

  _userService = inject(UserService);
  _localStorageService = inject(LocalStorageService);

  ngOnInit(): void {
    const id = this._localStorageService.getUserId();
    this._userService.getAppointmentsByEmployee(id).subscribe({
      next: response => {
        response.forEach((element: any) => {
          this.calendarOptions.events = [
            ...(this.calendarOptions.events as any[]),
            {
              title: element.username,
              start: element.startTime,
              end: element.endTime,
              color: this.colors[element.state],
              extendedProps: {
                element: element
              }
            }
          ]
        })
      }
    })
  }


  handleEventClick(info: any) {
    console.log(info);
    const currency = this._localStorageService.getCurrency() || '$';
    const event = info.event.extendedProps.element;
    this._userService.getBillByAppointmentId(event.id).subscribe({
      next: response => {
        const totalFormated = `${currency} ${event.total}`;
        const advancementFormated = `${currency} ${response.advancement}`;
        let confirmButtonText = 'Marcar como completada';
        let isConfirmButtonDisabled = false;

        if (event.state === 'CONFIRMADA') {
          confirmButtonText = 'Marcar como completada';
        } else if (event.state === 'FINALIZADA') {
          confirmButtonText = 'Acción no Disponible';
          isConfirmButtonDisabled = true;
        }

        Swal.fire({
          title: info.event.title,
          html: `
      <p><strong>Empleado Asignado:</strong> ${event.employeeFirstName}</p>
      <p><strong>Recurso Asignado:</strong> ${event.resourceName}</p>
      <p><strong>Estado:</strong> ${event.state}</p>
      <p><strong>Usuario:</strong> ${event.username}</p>
      <p><strong>Adelanto:</strong> ${advancementFormated}</p>
      <p><strong>Total:</strong> ${totalFormated}</p>
      
      <button id="btn-confirmar" class="button is-primary" ${isConfirmButtonDisabled ? 'disabled' : ''}>${confirmButtonText}</button>
      <button id="btn-cancelar" class="button is-danger"}>Cancelar</button>
    `,
          icon: 'info',
          showConfirmButton: false,
          showCancelButton: false
        });

        document.getElementById('btn-confirmar')?.addEventListener('click', () => {
          if (!isConfirmButtonDisabled) {

            if (event.state === 'CONFIRMADA') {
              Swal.fire({
                title: 'Marcar como Finalizada',
                text: '¿Seguro que desea marcar esta cita como FINALIZADA?',
                icon: 'question',
                confirmButtonText: 'Confirmar'
              }).then(result => {
                if(result.isConfirmed) {
                  this._userService.updateAppointmentState(event.id, "FINALIZADA").subscribe({
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
    })

  }
}
