import { Component, OnInit } from '@angular/core';
import { NavbarComponent } from '../../commons/navbar/navbar.component';
import { UserService } from '../../../services/user.service';
import Swal from 'sweetalert2';
import { CompanyCurrencyPipe } from '../../../pipes/company-currency.pipe';
import { LocalStorageService } from '../../../services/local-storage.service';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';
import FileSaver from 'file-saver';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-report-bill',
  standalone: true,
  imports: [
    NavbarComponent,
    CommonModule,
    FormsModule,
    CompanyCurrencyPipe
  ],
  templateUrl: './report-bill.component.html',
  styleUrl: './report-bill.component.scss',
  providers: [ CompanyCurrencyPipe ]
})
export class ReportBillComponent implements OnInit {
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
    this.loadReport();
  }

  loadReport() {
    this._userService.getBillReport().subscribe((data: any) => {
      if(data.length > 0){
        this.appointments = data;
        this.filteredAppointments = data;
      }
    },(error: any) => {
      Swal.fire({
        title: 'Error',
        text: error.error.message,
        icon: 'error',
      });
    })
  }

  filterByDate() {
    if (!this.startDate || !this.endDate) {
      this.filteredAppointments = this.appointments;
      return;
    }

    this.filteredAppointments = this.appointments.filter(appointment => {
      const appointmentDate = new Date(appointment.billDate).getTime();
      const start = new Date(this.startDate).getTime();
      const end = new Date(this.endDate).getTime();

      return appointmentDate >= start && appointmentDate <= end;
    });
  }

  exportToPDF() {
    const doc = new jsPDF(); 
    
    const reportTitle = 'Reporte de Facturas';
    const userName = this._localStorageService.getUserName() || 'Usuario desconocido';
    const userPhoto = this._localStorageService.getUserPhoto();
  
    let titlePositionY = 30;
    const imagePositionX = 10;
  
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
  
    const columns = ['Nombre', 'NIT', 'Dirección', 'Monto Total', 'Adelanto', 'Fecha'];
    
    const rows = this.filteredAppointments.map(appointment => [
      appointment.name,
      appointment.nit,
      appointment.address,
      this._companyCurrencyPipe.transform(appointment.totalAmount),
      this._companyCurrencyPipe.transform(appointment.advancement),
      this.formatDate(appointment.billDate)
    ]);
  
    const tableStartY = titlePositionY + 40;
  
    autoTable(doc, {
      head: [columns],  
      body: rows,
      startY: tableStartY
    });
  
    doc.save('reporte_facturas.pdf');
  }

  exportToCSV() {
    const titles = ['Nombre', 'NIT', 'Dirección', 'Monto Total', 'Adelanto', 'Fecha'];
    
    const csvContent = [
      titles.join(','),
      ...this.filteredAppointments.map(appointment => [
        `"${appointment.name}"`,
        `"${appointment.nit}"`,
        `"${appointment.address}"`,
        `"${appointment.totalAmount}"`,
        `"${appointment.advancement}"`,
        `"${this.formatDate(appointment.billDate)}"`
      ].join(','))
    ].join('\n'); 
    

    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    FileSaver.saveAs(blob, 'reporte_facturas.csv');
  }

  formatDate(date: string | Date): string {
    const options: Intl.DateTimeFormatOptions = { day: '2-digit', month: 'short', year: 'numeric' };
    return new Date(date).toLocaleDateString('es-ES', options);
  }
  
}
