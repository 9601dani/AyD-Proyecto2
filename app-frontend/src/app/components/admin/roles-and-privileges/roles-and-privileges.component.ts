import { Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import Swal from 'sweetalert2';
import { LocalStorageService } from '../../../services/local-storage.service';
import { UserService } from '../../../services/user.service';
import { NavbarComponent } from '../../commons/navbar/navbar.component';

interface Role {
  id: number;
  name: string;
  description: string;
}

@Component({
  selector: 'app-roles-and-privileges',
  standalone: true,
  imports: [ReactiveFormsModule, NavbarComponent],
  templateUrl: './roles-and-privileges.component.html',
  styleUrl: './roles-and-privileges.component.scss'
})
export class RolesAndPrivilegesComponent implements OnInit {

  roleForm!: FormGroup;
  pagesForm!: FormGroup;
  roleControl = new FormControl(0, Validators.required);
  roles: Role[] = [];
  isUpdating = false;
  currentRoleId = 0;
  currentPermissionsRoleId = 0;
  pages: any[] = [];
  currentPages: any[] = [];
  constructor(
    private _localStorageService: LocalStorageService,
    private fb: FormBuilder,
    private _userService: UserService
  ) { }

  ngOnInit(): void {
    this.roleForm = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required]
    })

    this.pagesForm = this.fb.group({
      pages: this.fb.array([])
    });

    this._userService.getRoles().subscribe((data: any) => {
      this.roles = data;
    }, error => {
      this.roles = [];
    });

    this._userService.getAllPages().subscribe((data: any) => {
      this.pages = data;
      this.pages.forEach(page => {
        (this.pagesForm.get('pages') as FormArray).push(new FormControl(false));
      });
    }, error => {
      this.pages = [];
    });
  }

  submit() {
    if (this.roleForm.invalid) {
      Swal.fire('Error!', 'Por favor llene los campos del formulario.', 'error');
      return;
    }

    const { name, description } = this.roleForm.value;
    if (this.isUpdating) {
      this.updateRole(name, description);
    } else {
      this.saveRole(name, description);
    }
  }

  saveRole(name: string, description: string) {
    this._userService.addRole(name, description).subscribe((data: any) => {
      Swal.fire('Creado', 'El rol ha sido creado.', 'success');
      window.location.reload();
    }, error => {
      Swal.fire('Error!', 'Un error ha ocurrido al crear el nuevo rol.', 'error');
    });
  }

  updateRole(name: string, description: string) {
    if (this.currentRoleId === 0) {
      Swal.fire('Error!', 'No se ha seleccionado un rol para actualizar.', 'error');
      return;
    }

    this._userService.updateRole(name, description, this.currentRoleId).subscribe((data: any) => {
      Swal.fire('Actualizado', 'El rol ha sido actualizado.', 'success');
      window.location.reload();
    }, error => {
      Swal.fire('Error!', 'Un error ha ocurrido al actualizar el rol.', 'error');
    });
  }

  editRole() {
    if(this.roleControl.invalid) {
      return;
    }
    const value = this.roleControl.value;

    if(value === 0) return;

    const role = this.roles.find(role => role.id === Number(value));

    if(!role) return;

    this.roleForm.setValue({
      name: role.name,
      description: role.description
    });

    this.isUpdating = true;
    this.currentRoleId = role.id;
  }

  cancelUpdate() {
    this.roleForm.reset();
    this.isUpdating = false;
    this.currentRoleId = 0;
  }

  chooseRolePages(roleId: any) {
    console.log("changing")
    // this.currentPermissionsRoleId = roleId;
    // const rolePages = this.roles[this.currentPermissionsRoleId - 1].pages;
    // // update new form array with currentRoleId permissions
    // (this.pagesForm.get('pages') as FormArray).controls.forEach((control, index) => {
    //   control.setValue(rolePages.some((page: any) => page.id === this.pages[index].id));
    // });
  }

  updatePages() {
    if (this.currentPermissionsRoleId === 0) {
      Swal.fire('Error!', 'No se ha seleccionado un rol para actualizar.', 'error');
      return;
    }

    const pages = this.pagesForm.value.pages;
    const rolePages = this.pages.filter((page, index) => pages[index]);

    console.log(rolePages);
    this._userService.updateRolePages(this.currentPermissionsRoleId, rolePages).subscribe((data: any) => {
      Swal.fire('Actualizado', 'Los permisos del rol han sido actualizados.', 'success');
      window.location.reload();
    }, error => {
      Swal.fire('Error!', 'Un error ha ocurrido al actualizar los permisos del rol.', 'error');
      console.log(error);
    });
  }

  getPrivileges() {
    if(this.roleControl.invalid) return;
    
    const value = Number(this.roleControl.value);
    
    if(value === 0) return;
    this.currentPermissionsRoleId = value;
    console.log(this.currentPermissionsRoleId)
    
    this._userService.getAllRolePages(value).subscribe({
      next: response => {
        (this.pagesForm.get('pages') as FormArray).controls.forEach((control, index) => {
          control.setValue(response.some((page: any) => page.id === this.pages[index].id));
        });
      }
    })
  }
}
