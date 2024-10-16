import {Component, Inject, OnInit, PLATFORM_ID, ViewChild} from '@angular/core';
import {NavbarComponent} from "../../commons/navbar/navbar.component";
import {ResponseString, User, UserAllResponse, UserProfile} from "../../../interfaces/interfaces";
import {HttpClient} from "@angular/common/http";
import {CommonModule, isPlatformBrowser, JsonPipe} from "@angular/common";
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {NotProfileDirective} from "../../../directives/not-profile.directive";
import {UserService} from "../../../services/user.service";
import Swal from "sweetalert2";
import { LocalStorageService } from '../../../services/local-storage.service';
import { ImgService } from '../../../services/img.service';
import { ImagePipe } from '../../../pipes/image.pipe';

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
    NotProfileDirective,
    ImagePipe

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
    imageProfile: ''
  };
  initialValues: any;
  profileForm!: FormGroup;
  changesMade=false;

  selectedFile: File | null = null;
  selectedPreviewImage: string | null = null;
  errorMessage: string = '';

  
  @ViewChild('fileInput') fileInput: any;

  constructor(
    private fb: FormBuilder,
    private _userService: UserService,
    private _localStorageService: LocalStorageService,
    private _imgService: ImgService,
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
            console.log(res);
            this.userProfileAll = res;
            this.initialValues = this.profileForm.value;
            this.profileForm.patchValue({
              nit: this.userProfileAll.nit,
              description: this.userProfileAll.description,
              image_profile: this.userProfileAll.imageProfile
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

  triggerFileInput() {
    this.fileInput.nativeElement.click();
  }

  onFileSelected(event: any): void {
    const file: File = event.target.files[0];
    const allowedTypes = ['image/jpeg', 'image/png', 'image/jpg'];

    if (file && allowedTypes.includes(file.type)) {
      this.selectedFile = file;
      this.errorMessage = '';

      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.selectedPreviewImage = e.target.result;
      };
      reader.readAsDataURL(file);
    } else {
      this.selectedFile = null;
      this.selectedPreviewImage = null;
      this.errorMessage = 'Solo se permiten imágenes con formato .jpg, .jpeg o .png';
    }
  }

  onUpload(): void {
    if (this.selectedFile) {
      const formData = new FormData();
      formData.append('file', this.selectedFile, this.selectedFile.name);
  
      const oldImage = this._localStorageService.getItem(this._localStorageService.USER_PHOTO);
      
      this._imgService.updateImgProfile(oldImage, this.selectedFile).subscribe(
        (res: ResponseString) => {
          const imagePath = res.message; 
          
          this.updateProfileImgUser(imagePath);
          this.selectedPreviewImage = null;
        },
        (err) => {
          Swal.fire({
            title: 'Error',
            text: 'No se ha podido actualizar la imagen de perfil',
            icon: 'error'
          });
          console.log(err);
        }
      );
    }
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

updateProfileImgUser(img: string): void {

  const path = { message: img };

  this._userService.updateImgUserInformation(parseInt(localStorage.getItem('user_id')!), path).subscribe({
    next: (res: any) => {

      this._localStorageService.setUserPhoto(res);

      Swal.fire({
        title: 'Imagen de perfil actualizada',
        text: 'La imagen de perfil se ha actualizado correctamente',
        icon: 'success'
      });
    },
    error: (err) => {
      console.error('Error actualizando la imagen de perfil:', err);

      Swal.fire({
        title: 'Error',
        text: 'No se ha podido actualizar la imagen de perfil',
        icon: 'error'
      });
    }
  });
}

  
}
