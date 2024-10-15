import {Component, OnInit, AfterViewInit, CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import { NavbarComponent } from '../navbar/navbar.component';
import {MatIcon} from "@angular/material/icon";
import {CommonModule} from "@angular/common";
import Splide from "@splidejs/splide";

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

  serviceImages: { url: string, alt: string }[] = [
    { url: 'https://media-cdn.tripadvisor.com/media/photo-s/12/7d/47/aa/cancha-techada-de-futbol.jpg', alt: 'Imagen 1' },
    { url: 'https://media-cdn.tripadvisor.com/media/photo-s/12/7d/47/aa/cancha-techada-de-futbol.jpg', alt: 'Imagen 2' },
    { url: 'https://media-cdn.tripadvisor.com/media/photo-s/12/7d/47/aa/cancha-techada-de-futbol.jpg', alt: 'Imagen 3' }
  ];

  ngOnInit() {
  }


}
