import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { NavbarComponent } from '../../commons/navbar/navbar.component';
import { UserService } from '../../../services/user.service';
import { Router } from '@angular/router';
import { CompanyCurrencyPipe } from '../../../pipes/company-currency.pipe';

@Component({
  selector: 'app-view-service',
  standalone: true,
  imports: [
    NavbarComponent,
    CommonModule,
    MatButtonModule,
    MatIconModule,
    CompanyCurrencyPipe
  ],
  templateUrl: './view-service.component.html',
  styleUrl: './view-service.component.scss'
})
export class ViewServiceComponent implements OnInit {

  services: any[] = [];

  constructor(
    private _user_service: UserService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this._user_service.getAllServices().subscribe(
      (res) => {
        this.services = res;
      },
      (err) => {
        console.error('Error al cargar los servicios', err);
      }
    );
  }

  editService(serviceId: number): void {
    this.router.navigate(['/services/edit-service', serviceId]);
  }
}
