import {Component, OnInit, AfterViewInit, CUSTOM_ELEMENTS_SCHEMA, inject} from '@angular/core';
import { NavbarComponent } from '../navbar/navbar.component';
import {MatIcon} from "@angular/material/icon";
import {CommonModule} from "@angular/common";
import Splide from "@splidejs/splide";
import { UserService } from '../../../services/user.service';
import { CompanySetting } from '../../../models/CompanySetting.model';
import { LocalStorageService } from '../../../services/local-storage.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    NavbarComponent,
    CommonModule,
    MatIcon
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class HomeComponent implements OnInit{

  _userService = inject(UserService)
  _localStorageService = inject(LocalStorageService)

  serviceImages: { url: string, alt: string }[] = [
    { url: 'https://media-cdn.tripadvisor.com/media/photo-s/12/7d/47/aa/cancha-techada-de-futbol.jpg', alt: 'Imagen 1' },
    { url: 'https://media-cdn.tripadvisor.com/media/photo-s/12/7d/47/aa/cancha-techada-de-futbol.jpg', alt: 'Imagen 2' },
    { url: 'https://media-cdn.tripadvisor.com/media/photo-s/12/7d/47/aa/cancha-techada-de-futbol.jpg', alt: 'Imagen 3' }
  ];

  ngOnInit() {
    this._userService.getCompanySettingByKeyName("currency").subscribe({
      next: (response: CompanySetting) => {
        this._localStorageService.setCurrency(response.keyValue);
      },
      error: err => {
        console.error(err.error);
      }
    })
  }


}
