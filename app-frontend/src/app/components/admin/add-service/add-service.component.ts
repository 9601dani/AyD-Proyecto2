import { Component, OnInit } from '@angular/core';
import { NavbarComponent } from '../../commons/navbar/navbar.component';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { UserService } from '../../../services/user.service';
import { ActivatedRoute, Router } from '@angular/router'; 
import Swal from 'sweetalert2';

@Component({
  selector: 'app-add-service',
  standalone: true,
  imports: [
    NavbarComponent,
    CommonModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
  ],
  templateUrl: './add-service.component.html',
  styleUrl: './add-service.component.scss'
})

export class AddServiceComponent implements OnInit {
  service_form!: FormGroup;
  isEditMode = false; 
  serviceId!: number; 

  constructor(
    private fb: FormBuilder,
    private _user_service: UserService,
    private route: ActivatedRoute, 
    private router: Router  
  ) { }

  ngOnInit(): void {
    this.service_form = this.fb.group({
      name: ['', [Validators.required, Validators.maxLength(100)]],
      description: [''],
      price: [null, [Validators.required, Validators.min(0.01)]],
      pageInformation: [''],
      timeAprox: [null, [Validators.required, Validators.min(1)]],
      isAvailable: [1]
    });

    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.serviceId = params['id'];
        this.loadServiceData(this.serviceId); 
      }
    });
  }

  loadServiceData(serviceId: number): void {
    this._user_service.getServiceById(serviceId).subscribe(
      (res) => {
        this.service_form.patchValue(res); 
      },
      (err) => {
        Swal.fire({
          title: 'Error',
          text: 'Ha ocurrido un error al cargar el servicio',
          icon: 'error',
          confirmButtonText: 'Aceptar'
        });
      }
    );
  }

  onSubmit(): void {
    if (this.service_form.valid) {
      if (this.isEditMode) {
        this._user_service.updateService(this.serviceId, this.service_form.value).subscribe(
          (res) => {
            Swal.fire({
              title: 'Servicio actualizado: ' + res.name,
              text: 'El servicio ha sido actualizado con éxito',
              icon: 'success',
              confirmButtonText: 'Aceptar'
            }).then(() => {
              this.router.navigate(['/services/show-services']); 
            });
          },
          (err) => {
            Swal.fire({
              title: 'Error',
              text: 'Ha ocurrido un error al actualizar el servicio',
              icon: 'error',
              confirmButtonText: 'Aceptar'
            });
          }
        );
      } else {
        this._user_service.createService(this.service_form.value).subscribe(
          (res) => {
            this.service_form.reset();
            Swal.fire({
              title: 'Servicio creado: ' + res.name,
              text: 'El servicio ha sido creado con éxito',
              icon: 'success',
              confirmButtonText: 'Aceptar'
            });
          },
          (err) => {
            Swal.fire({
              title: 'Error',
              text: 'Ha ocurrido un error al crear el servicio, Parece que el servicio ya existe',
              icon: 'error',
              confirmButtonText: 'Aceptar'
            });
          }
        );
      }
    } else {
      Swal.fire({
        title: 'Error',
        text: 'Por favor, rellene todos los campos',
        icon: 'error',
        confirmButtonText: 'Aceptar'
      });
    }
  }

  onCancel(): void {
    if (this.isEditMode) {
      this.router.navigate(['/services/show-services']);
    } else {
      this.service_form.reset();
    }
  }
}
