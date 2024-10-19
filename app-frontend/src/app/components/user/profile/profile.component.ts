import {Component, Inject, OnInit, PLATFORM_ID, ViewChild} from '@angular/core';
import {NavbarComponent} from "../../commons/navbar/navbar.component";
import {ResponseString, UserAllResponse} from "../../../interfaces/interfaces";
import {CommonModule, isPlatformBrowser} from "@angular/common";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
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
    imageProfile: '',
    dpi: '',        
    phoneNumber: '' 
  };
  initialValues: any;
  profileForm!: FormGroup;
  authForm!: FormGroup;
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
      nit: ['', Validators.required],           
      description: ['', Validators.required],    
      phoneNumber: ['', [Validators.required, Validators.pattern(/^[0-9]{8}$/)]],
      dpi: ['', [Validators.required, Validators.pattern(/^[0-9]{13}$/)]] 
    });

    this.authForm = this.fb.group({
      twoFactorAuth: [false]  
    });

    this.profileForm.valueChanges.subscribe(() => {
      this.changesMade = !this.areValuesEqual(this.initialValues, this.profileForm.value);
    });
    
  }

  getUserProfile(): void {
    if (isPlatformBrowser(this.platformId)) {
      const user_id = this._localStorageService.getUserId();
      if(user_id === null) {
        Swal.fire({
          title: 'Error',
          text: 'No se ha podido obtener el usuario',
          icon: 'error'
        });
        return;
      } else {
        this._userService.getMyProfile(user_id).subscribe(
          (res: UserAllResponse) => {
            this.userProfileAll = res;
            this.initialValues = this.profileForm.value;
            this.profileForm.patchValue({
              nit: this.userProfileAll.nit,
              description: this.userProfileAll.description,
              phoneNumber: this.userProfileAll.phoneNumber, 
              dpi: this.userProfileAll.dpi,                 
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
  
      const oldImage = this._localStorageService.getUserPhoto();
      
      this._imgService.updateImgProfile(oldImage, this.selectedFile).subscribe(
        (res: ResponseString) => {
          const imagePath = res.message; 
          
          this.updateProfileImgUser(imagePath);
          this.selectedFile = null;
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
      if(profileData.phoneNumber == null || profileData.phoneNumber == '') {
        Swal.fire({
          title: 'Error',
          text: 'El campo Teléfono no puede estar vacío',
          icon: 'error'
        });
        return;
      }
      if(profileData.dpi == null || profileData.dpi == '') {
        Swal.fire({
          title: 'Error',
          text: 'El campo DPI no puede estar vacío',
          icon: 'error'
        });
        return;
      }
      this._userService.updateProfile(this._localStorageService.getUserId(), profileData).subscribe(
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
    } else {
      Swal.fire({
        title: 'Error',
        text: 'Por favor, complete los campos vacios',
        icon: 'error'
      });
    }
  }

  updateProfileImgUser(img: string): void {

    const path = { message: img };

    this._userService.updateImgUserInformation(this._localStorageService.getUserId(), path).subscribe({
      next: (res: any) => {

        this._localStorageService.setUserPhoto(res.path);

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

  setTwoFactorAuth(value: boolean): void {
    if (this.authForm.get('twoFactorAuth')?.value !== value) {
      this.authForm.patchValue({
        twoFactorAuth: value
      });

      const userId = this._localStorageService.getUserId();
      if (userId !== null) {
       Swal.fire({
          title: 'Manda el valor: ' + value,
          text: 'El valor se ha actualizado correctamente',
          icon: 'success'
        });
      }
    }
  }
  
}
