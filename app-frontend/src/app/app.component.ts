import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { TestComponentsComponent } from './test-components/test-components/test-components.component';
import {CommonModule} from "@angular/common";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, TestComponentsComponent, CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'app-frontend';
}
