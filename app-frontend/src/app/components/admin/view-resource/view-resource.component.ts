import { Component } from '@angular/core';
import { NavbarComponent } from '../../commons/navbar/navbar.component';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { UserService } from '../../../services/user.service';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-view-resource',
  standalone: true,
  imports: [
    NavbarComponent,
    CommonModule,
    MatButtonModule,
    MatIconModule,
    FormsModule
  ],
  templateUrl: './view-resource.component.html',
  styleUrls: ['./view-resource.component.scss']
})
export class ViewResourceComponent {
  resources: Array<{ id: number, name: string, attributes: Array<{ name: string, description: string }> }> = [];
  filteredResources: Array<{ id: number, name: string, attributes: Array<{ name: string, description: string }> }> = [];
  searchTerm: string = '';

  constructor(
    private router: Router,
    private _userService: UserService
  ) { }

  ngOnInit(): void {
    this.loadResources();
  }

  loadResources(): void {
    this._userService.getAllResources().subscribe(
      (response) => {
        this.resources = response;
        this.filteredResources = response; 
      },
      (error) => {
        console.error(error);
      }
    );
  }

  filterResources(): void {
    const searchLower = this.searchTerm.toLowerCase();
    
    this.filteredResources = this.resources.filter(resource => {
      const matchesName = resource.name.toLowerCase().includes(searchLower);
      const matchesAttribute = resource.attributes.some(attr => 
        attr.name.toLowerCase().includes(searchLower) || 
        attr.description.toLowerCase().includes(searchLower)
      );
      
      return matchesName || matchesAttribute;
    });
  }

  editResource(id: number): void {
    this.router.navigate(['resources/add-resource', id]);
  }
}
