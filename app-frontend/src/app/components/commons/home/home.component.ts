import {Component, OnInit, AfterViewInit, CUSTOM_ELEMENTS_SCHEMA, inject, PLATFORM_ID, Inject} from '@angular/core';
import { NavbarComponent } from '../navbar/navbar.component';
import {MatIcon, MatIconModule} from "@angular/material/icon";
import {CommonModule, DatePipe, isPlatformBrowser} from "@angular/common";
import Splide from "@splidejs/splide";
import { UserService } from '../../../services/user.service';
import { CompanySetting } from '../../../models/CompanySetting.model';
import { LocalStorageService } from '../../../services/local-storage.service';
import { Comment, CommentResponse, CompanySettingConfig } from '../../../interfaces/interfaces';
import { ImagePipe } from '../../../pipes/image.pipe';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    NavbarComponent,
    CommonModule,
    MatIcon,
    ImagePipe,
    FormsModule,
    MatIconModule,
    DatePipe
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class HomeComponent implements OnInit{

  newCommentText: string = '';
  newCommentValue: number = 5;

  constructor(
    @Inject(PLATFORM_ID) private platformId: Object
  ) { }

  private _userService = inject(UserService)
  private _localStorageService = inject(LocalStorageService)


  serviceImages: { url: string, alt: string }[] = [
  ];

  isLogged: boolean = false;
  direction: string = "";
  start_hour: string = "";
  end_hour: string = "";
  service_img1: string = "";
  service_img2: string = "";
  service_img3: string = "";
  service_img4: string = "";
  comments :CommentResponse[] = [];
  
  
  ngOnInit() {
    const settings: CompanySettingConfig[] = [
      { keyName: "currency", property: "setCurrency" },
      { keyName: "direction", property: "direction" },
      { keyName: "start_hour", property: "start_hour" },
      { keyName: "end_hour", property: "end_hour" },
      { keyName: "service_img1", property: "service_img1" },
      { keyName: "service_img2", property: "service_img2" },
      { keyName: "service_img3", property: "service_img3" },
      { keyName: "service_img4", property: "service_img4" }
    ];

    let imagesLoaded = 0;

    this.isLogged = this._localStorageService.getUserName() !== null;

    this.getComments();
  
    settings.forEach(setting => {
      this._userService.getCompanySettingByKeyName(setting.keyName).subscribe({
        next: (response: CompanySetting) => {
          if (setting.property === 'setCurrency') {
            this._localStorageService.setCurrency(response.keyValue);
          }else {
            (this as any)[setting.property] = response.keyValue;
            if (['service_img1', 'service_img2', 'service_img3', 'service_img4'].includes(setting.property)) {
              imagesLoaded++;
            }
            if (imagesLoaded === 4) {
              this.addImagesToServiceImages();
              if (isPlatformBrowser(this.platformId)) {
                this.initializeSplide();
              }
            }
          }
        },
        error: err => {
          console.error(err.error);
        }
      });
    });
  }

  addImagesToServiceImages() {
    this.serviceImages = [
      { url: this.service_img1, alt: 'Imagen 1' },
      { url: this.service_img2, alt: 'Imagen 2' },
      { url: this.service_img3, alt: 'Imagen 3' },
      { url: this.service_img4, alt: 'Imagen 4' }
    ];
  }

  ngAfterViewInit() {
    if (isPlatformBrowser(this.platformId) && this.serviceImages.length > 0) {
      this.initializeSplide();
    }
  }

  initializeSplide() {
    const splide = new Splide('#service-slider', {
      type: 'loop',
      perPage: 1,
      autoplay: true,
      gap: '1rem'
    });

    splide.mount();
  }

  addComment() {
    const date = new Date();
    const formattedDate = date.getFullYear() + '-' +
      ('0' + (date.getMonth() + 1)).slice(-2) + '-' +
      ('0' + date.getDate()).slice(-2) + 'T' +
      ('0' + date.getHours()).slice(-2) + ':' +
      ('0' + date.getMinutes()).slice(-2) + ':' +
      ('0' + date.getSeconds()).slice(-2);

    const newComment: Comment = {
      id: this.comments.length + 1,
      FK_User: this._localStorageService.getUserId(),
      comment: this.newCommentText,
      value: this.newCommentValue,
      created_at: formattedDate
    };

    if(this.newCommentValue < 1 || this.newCommentValue > 5) {
      Swal.fire({
        icon: 'error',
        title: 'Error',
        text: 'El valor de la calificación debe ser entre 1 y 5.'
      });
      return;
    }

    console.log("New comment: ", newComment);
    this._userService.saveComment(newComment).subscribe({
      next: (response: any) => {
        Swal.fire({
          icon: 'success',
          title: 'Comentario agregado',
          text: 'Tu comentario ha sido agregado exitosamente.'
        });
        this.comments.push(response); 
        this.newCommentText = '';
        this.newCommentValue = 5; 
      },
      error: err => {
        console.error(err.error);
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'Hubo un error al agregar tu comentario.'
        });
      }
    });
        this.newCommentText = '';
        this.newCommentValue = 5; 
  }

  getComments() {
    this._userService.getAllComments().subscribe({
      next: (response: CommentResponse[]) => {
        this.comments = response;
      },
      error: err => {
        console.error(err.error);
      }
    });
  }
}
