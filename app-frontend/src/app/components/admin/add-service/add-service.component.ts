import { Component, OnInit } from '@angular/core';
import { NavbarComponent } from '../../commons/navbar/navbar.component';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { UserService } from '../../../services/user.service';
import { ActivatedRoute, Router } from '@angular/router'; 
import Swal from 'sweetalert2';
import { Employee, Resources } from '../../../interfaces/interfaces';

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

  /* employees: Employee[] = [
    {
      id: 1,
      firstName: 'Juan',
      lastName: 'Pérez',
      dateOfBirth: '1990-05-15',
      email: 'juan.perez@example.com',
      username: 'juanperez',
      password: 'password123',
      role: 1
    },
    {
      id: 2,
      firstName: 'Ana',
      lastName: 'Gómez',
      dateOfBirth: '1985-11-20',
      email: 'ana.gomez@example.com',
      username: 'anagomez',
      password: 'password123',
      role: 2
    },
    {
      id: 3,
      firstName: 'Luis',
      lastName: 'Fernández',
      dateOfBirth: '1992-07-12',
      email: 'luis.fernandez@example.com',
      username: 'luisfernandez',
      password: 'password123',
      role: 3
    },
    {
      id: 4,
      firstName: 'Carla',
      lastName: 'Mendoza',
      dateOfBirth: '1988-03-08',
      email: 'carla.mendoza@example.com',
      username: 'carlamendoza',
      password: 'password123',
      role: 1
    },
    {
      id: 5,
      firstName: 'David',
      lastName: 'García',
      dateOfBirth: '1993-09-25',
      email: 'david.garcia@example.com',
      username: 'davidgarcia',
      password: 'password123',
      role: 2
    }
  ];
   */

  employees : Employee[] = [];
  resources: Resources[] = [];

  filteredEmployees: Employee[] = [...this.employees];
  addedEmployees: Employee[] = [];
  

  filteredResources: Array<{ name: string }> = [...this.resources];
  addedResources: Array<{ name: string }> = [];

  employeeSearchControl = new FormControl('');
  selectedEmployeesControl = new FormControl([]);

  resourceSearchControl = new FormControl('');
  selectedResourcesControl = new FormControl([]);

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
      isAvailable: [true],
      employees: [this.addedEmployees],
      resources: [this.addedResources]
    });

    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.serviceId = params['id'];
        this.loadServiceData(this.serviceId); 
      }
    });

    this.employeeSearchControl.valueChanges.subscribe(value => {
      if (!value) {
        this.filteredEmployees = [...this.employees];
        return;
      }
      this.filteredEmployees = this.employees.filter(emp =>
        emp.firstName.toLowerCase().includes(value.toLowerCase())
      );
    });

    this.resourceSearchControl.valueChanges.subscribe(value => {
      if (!value) {
        this.filteredResources = [...this.resources];
        return;
      }
      this.filteredResources = this.resources.filter(res =>
        res.name.toLowerCase().includes(value.toLowerCase())
      );
    });

    this.loadEmployees();
    this.loadResources();
  }

  loadServiceData(serviceId: number): void {
    this._user_service.getServiceById(serviceId).subscribe(
      (res) => {
        this.service_form.patchValue(res); 
        this.addedEmployees = res.employees;
        this.addedResources = res.resources;
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
            this.addedEmployees = [];
            this.addedResources = [];
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

  addSelectedEmployees(): void {
    const selectedEmployeeIds = this.selectedEmployeesControl.value;
    if (!selectedEmployeeIds) return;
    selectedEmployeeIds.forEach((id: number) => {
      const employee = this.employees.find(emp => emp.id === id);
      if (employee && !this.addedEmployees.find(emp => emp.id === id)) {
        this.addedEmployees.push(employee);
      }
    });
    this.selectedEmployeesControl.setValue([]);
    this.service_form.get('employees')?.setValue(this.addedEmployees);
  }

  removeEmployee(employeeToRemove: Employee): void {
    this.addedEmployees = this.addedEmployees.filter(emp => emp.id !== employeeToRemove.id);
    this.service_form.get('employees')?.setValue(this.addedEmployees);
  }
  

  addSelectedResources(): void {
    const selectedResourceNames = this.selectedResourcesControl.value;
    if (!selectedResourceNames) return;
    selectedResourceNames.forEach((name: string) => {
      const resource = this.resources.find(res => res.name === name);
      if (resource && !this.addedResources.find(res => res.name === name)) {
        this.addedResources.push(resource);
      }
    });
    this.selectedResourcesControl.setValue([]);
    this.service_form.get('resources')?.setValue(this.addedResources);
  }

  removeResource(resourceToRemove: { name: string }): void {
    this.addedResources = this.addedResources.filter(res => res.name !== resourceToRemove.name);
    this.service_form.get('resources')?.setValue(this.addedResources);
  }

  loadEmployees(): void {
    this._user_service.getAllEmployees().subscribe(
      (res: Employee[]) => {
        this.employees = res;
        this.filteredEmployees = res; 
      },
      (err) => {
        Swal.fire(
          'Error',
          'Error al cargar los empleados ' + err.error.message,
          'error'
        );
      }
    );
  }

  loadResources(): void {
    this._user_service.getAllResources().subscribe((res: Resources[]) => {
      this.resources = res;
      this.filteredResources = res.map(r => ({ name: r.name }));
    }, (err) => {
      Swal.fire({
        title: 'Error',
        text: 'Ha ocurrido un error al cargar los recursos',
        icon: 'error',
        confirmButtonText: 'Aceptar'
      });
    });
  }
}
