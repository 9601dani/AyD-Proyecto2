import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { TestComponentsComponent } from './test-components/test-components/test-components.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, TestComponentsComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'app-frontend';
}
