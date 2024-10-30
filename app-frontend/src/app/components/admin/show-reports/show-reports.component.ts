import { Component, OnInit } from '@angular/core';
import { NavbarComponent } from '../../commons/navbar/navbar.component';
import { UserService } from '../../../services/user.service';
import Swal from 'sweetalert2';
import { CommonModule, CurrencyPipe} from '@angular/common';
import { FormsModule } from '@angular/forms';
import FileSaver from 'file-saver';
import jsPDF from 'jspdf';
import 'jspdf-autotable';
import { CompanyCurrencyPipe } from '../../../pipes/company-currency.pipe';
import autoTable from 'jspdf-autotable';
import { LocalStorageService } from '../../../services/local-storage.service';


@Component({
  selector: 'app-show-reports',
  standalone: true,
  imports: [
    NavbarComponent,
    CommonModule,
    FormsModule,
    CompanyCurrencyPipe
  ],
  templateUrl: './show-reports.component.html',
  styleUrl: './show-reports.component.scss',
  providers: [CompanyCurrencyPipe]
})
export class ShowReportsComponent implements OnInit {

  appointments: any[] = [];
  filteredAppointments: any[] = [];
  startDate: string = '';
  endDate: string = '';


  constructor(
    private _userService: UserService,
    private _companyCurrencyPipe: CompanyCurrencyPipe,
    private _localStorageService: LocalStorageService
  ) { }

  ngOnInit(): void {
    this.loadAppointments();
  }

  loadAppointments() {
    this._userService.getAllAppointments().subscribe((data: any) => {
      if(data.length > 0){
        this.appointments = data;
        this.filteredAppointments = data;
      }
    },(error: any) => {
      Swal.fire({
        title: 'Error',
        text: 'No se pudo cargar la información',
        icon: error.error.message,
      });
    })
  }

  filterAppointments() {
    if(!this.startDate || !this.endDate) {
      this.filteredAppointments = this.appointments;
      return;
    }
    this.filteredAppointments = this.appointments.filter(appointment => {
      const startDate = new Date(this.startDate).getTime();
      const endDate = new Date(this.endDate).getTime();
      const appointmentDate = new Date(appointment.startTime).getTime();
      return startDate <= appointmentDate && appointmentDate <= endDate;
    });
  }
  
  exportToCSV() {
    const titles = ['Empleado', 'Recurso', 'Fecha', 'Estado', 'Servicios', 'Total'];
    
    const csvContent = [
      titles.join(','),  
      ...this.filteredAppointments.map(appointment => [
        `"${appointment.employeeFirstName || 'Sin empleado'}"`,
        `"${appointment.resourceName || 'Sin recurso'}"`,
        `"${this.formatDate(appointment.startTime)}"`, 
        `"${this.translateState(appointment.state)}"`,  
        `"${appointment.serviceNames.join(', ')}"`,
        `"${appointment.total}"`
      ].join(',')) 
    ].join('\n'); 
  
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    FileSaver.saveAs(blob, 'reporte_citas.csv');
  }
  
  
  

  exportToPDF() {
    const doc = new jsPDF(); 
  
    const reportTitle = 'Reporte de Citas';
    const userName = this._localStorageService.getUserName() || 'Usuario desconocido';
    const userPhoto = this._localStorageService.getUserPhoto();
    
    const imagePositionX = 10;
    let titlePositionY = 30;
    

    const imageUrl = userPhoto;
    const imageBase64 = ''; 
    if (imageBase64) {
      doc.addImage(imageBase64, 'JPEG', imagePositionX, 10, 30, 30);
      titlePositionY = 50;
    }
  
    doc.setFontSize(18);
    doc.text(reportTitle, 105, titlePositionY, { align: 'center' }); 
  
    doc.setFontSize(12);
    doc.text(`Generado por: ${userName}`, 105, titlePositionY + 10, { align: 'center' });
  
    const startDateText = this.startDate ? this.startDate : '-------';
    const endDateText = this.endDate ? this.endDate : '-------';
    
    doc.text(`Fecha inicio: ${startDateText}`, 10, titlePositionY + 20);
    doc.text(`Fecha fin: ${endDateText}`, 10, titlePositionY + 30); 
  
    const columns = ['Empleado', 'Recurso', 'Fecha', 'Estado', 'Servicios', 'Total'];
    
    const rows = this.filteredAppointments.map(appointment => [
      appointment.employeeFirstName || 'Sin empleado',  
      appointment.resourceName || 'Sin recurso',      
      this.formatDate(appointment.startTime),
      this.translateState(appointment.state),
      appointment.serviceNames.join(', '), 
      this._companyCurrencyPipe.transform(appointment.total)
    ]);
  
    const tableStartY = titlePositionY + 40;
  
    autoTable(doc, {    
      head: [columns],  
      body: rows,
      startY: tableStartY
    });
  
    doc.save('reporte_citas.pdf');
  }
    

  formatDate(date: string | Date): string {
    const options: Intl.DateTimeFormatOptions = { day: '2-digit', month: 'short', year: 'numeric' };
    return new Date(date).toLocaleDateString('es-ES', options);
  }

  translateState(state: string): string {
    switch(state) {
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
}
