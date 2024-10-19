import { Component } from '@angular/core';
import { NavbarComponent } from '../../commons/navbar/navbar.component';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-view-resource',
  standalone: true,
  imports: [
    NavbarComponent,
    CommonModule
  ],
  templateUrl: './view-resource.component.html',
  styleUrl: './view-resource.component.scss'
})
export class ViewResourceComponent {
  resources: Array<{ id: number, name: string, attributes: Array<{ name: string, description: string }> }> = [];

  constructor(
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadResources();
  }

  loadResources(): void {
    this.resources = [
      {
        id: 1,
        name: 'Cancha de Futbol 5',
        attributes: [
          { name: 'Tamaño', description: '50 x 30 metros' },
          { name: 'Material', description: 'Césped Artificial' }
        ]
      },
      {
        id: 2,
        name: 'Cancha de Basket',
        attributes: [
          { name: 'Altura de Aro', description: '3.05 metros' },
          { name: 'Material', description: 'Parquet de Madera' }
        ]
      }
    ];
  }

  editResource(id: number): void {
    this.router.navigate(['resources/add-resource', id]);
  }
}
