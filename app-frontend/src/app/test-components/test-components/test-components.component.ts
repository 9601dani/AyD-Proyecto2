import { AfterViewInit, Component } from '@angular/core';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatButtonModule} from '@angular/material/button';
import {MatDividerModule} from '@angular/material/divider';
import {MatCardModule} from '@angular/material/card';
import { MatNativeDateModule } from '@angular/material/core';


import Splide from '@splidejs/splide';

@Component({
  selector: 'app-test-components',
  standalone: true,
  imports: [MatDatepickerModule, MatInputModule, MatFormFieldModule, MatButtonModule, MatDividerModule, MatCardModule,MatNativeDateModule],
  templateUrl: './test-components.component.html',
  styleUrl: './test-components.component.scss'
})
export class TestComponentsComponent implements AfterViewInit {

  ngAfterViewInit(): void {
    if (typeof document !== 'undefined') {
      new Splide('.splide', {
        type: 'loop',
        perPage: 1,
        autoplay: true,
        
      }).mount();
    }
  }

}
