import { Component } from '@angular/core';
import { NavbarComponent } from '../../commons/navbar/navbar.component';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { UserService } from '../../../services/user.service';

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
      },
      (error) => {
        console.error(error);
      }
    );
    
  }

  editResource(id: number): void {
    this.router.navigate(['resources/add-resource', id]);
  }
}
