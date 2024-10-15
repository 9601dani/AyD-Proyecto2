import {Component, Inject, OnInit, PLATFORM_ID} from '@angular/core';
import {NavbarComponent} from "../../commons/navbar/navbar.component";
import {User, UserAllResponse, UserProfile} from "../../../interfaces/interfaces";
import {HttpClient} from "@angular/common/http";
import {CommonModule, isPlatformBrowser} from "@angular/common";
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {NotProfileDirective} from "../../../directives/not-profile.directive";
import {UserService} from "../../../services/user.service";
import Swal from "sweetalert2";

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    NavbarComponent,
    CommonModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    NotProfileDirective

  ],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent implements OnInit{

  userProfileAll: UserAllResponse = {
    email: '',
    username: '',
    nit: '',
    description: '',
    image_profile: ''
  };
  initialValues: any;
  profileForm!: FormGroup;
  changesMade=false;

  constructor(
    private fb: FormBuilder,
    private _userService: UserService,
    @Inject(PLATFORM_ID) private platformId: Object
  ) { }


  ngOnInit() {
    this.getUserProfile();
    this.profileForm = this.fb.group({
      nit: [''],
      description: ['']
    });

    this.profileForm.valueChanges.subscribe(() => {
      this.changesMade = !this.areValuesEqual(this.initialValues, this.profileForm.value);
    });
  }

  getUserProfile(): void {
    if (isPlatformBrowser(this.platformId)) {
      const user_id = localStorage.getItem('user_id');
      if(user_id == null) {
        Swal.fire({
          title: 'Error',
          text: 'No se ha podido obtener el usuario',
          icon: 'error'
        });
        return;
      }else{
        this._userService.getMyProfile(parseInt(user_id)).subscribe(
          (res: UserAllResponse) => {
            this.userProfileAll = res;
            this.initialValues = this.profileForm.value;
            this.profileForm.patchValue({
              nit: this.userProfileAll.nit,
              description: this.userProfileAll.description,
              image_profile: this.userProfileAll.image_profile
            });
          },
          (err) => {
            Swal.fire({
              title: 'Error',
              text: 'No se ha podido obtener el usuario: '+ user_id,
              icon: 'error'
            });
          }
        );
      }
    }
  }
  areValuesEqual(initialValues: any, currentValues: any): boolean {
    return JSON.stringify(initialValues) === JSON.stringify(currentValues);
  }

  onUpdatePhoto(): void {
    alert('Actualizar foto de perfil no implementado');
  }

  saveChanges(): void {
    if (this.profileForm.valid) {
      const profileData = this.profileForm.value;
      if(profileData.nit == null || profileData.nit == '') {
        Swal.fire({
          title: 'Error',
          text: 'El campo NIT no puede estar vacío',
          icon: 'error'
        });
        return;
      }
      if(profileData.description == null || profileData.description == '') {
        Swal.fire({
          title: 'Error',
          text: 'El campo descripción no puede estar vacío',
          icon: 'error'
        });
        return;
      }
      this._userService.updateProfile(parseInt(localStorage.getItem('user_id')!), profileData).subscribe(
        (res: UserAllResponse) => {
          this.userProfileAll = res;
          this.initialValues = this.profileForm.value;
          Swal.fire({
            title: 'Cambios guardados',
            text: 'Los cambios se han guardado correctamente',
            icon: 'success'
          });
        },
        (err) => {
          Swal.fire({
            title: 'Error',
            text: 'No se ha podido guardar los cambios',
            icon: 'error'
          });
        }
      );
    }
  }
}
