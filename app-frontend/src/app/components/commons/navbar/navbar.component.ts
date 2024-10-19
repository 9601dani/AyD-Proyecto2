import { Component, OnInit } from '@angular/core';
import { LocalStorageService } from '../../../services/local-storage.service';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { NotLogoDirective } from '../../../directives/not-logo.directive';
import { NotProfileDirective } from '../../../directives/not-profile.directive';
import { UserService } from '../../../services/user.service';
import { Observable } from 'rxjs';
import { Module } from '../../../models/Module.model';
import Swal from 'sweetalert2';
import { ImagePipe } from '../../../pipes/image.pipe';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    NotLogoDirective,
    NotProfileDirective,
    ImagePipe
  ],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent implements OnInit {

  isLogged: boolean = false;
  isActive: boolean = false;
  showButtons: boolean = true;
  activeModule: any;
  modules: any[] = [];
  logoUrl = "";
  userPhoto = "";
  username = "";
  notOptionsUrls = ['login', 'verify-2fa', 'verify-email'];
  constructor(
    private _localStorageService: LocalStorageService,
    private router: Router,
    private _userService: UserService
  ) { }

  ngOnInit(): void {
    this.isLogged = this._localStorageService.getUserId() !== null;
    if (this.isLogged) {
      this.username = this._localStorageService.getUserName();
      this.showButtons = true;
      this.getPages();
      this.setUserPhoto();
      return;
    }

    this.showOptions();
  }

  getPages() {
    const id = this._localStorageService.getUserId();
    this._userService.getPages(id).subscribe({
      next: response => {
        this.modules = response;
      }
    });
  }

  setUserPhoto() {
    const id = this._localStorageService.getUserId();
    this.userPhoto = this._localStorageService.getUserPhoto();

    if(!this.userPhoto) {
      this._userService.getUserInfo(id).subscribe({
        next: (response: any) => {
          this.userPhoto = response.path;
          this._localStorageService.setUserPhoto(this.userPhoto);
        },
        error: error => {
          Swal.fire({
            title: "Error!",
            text: error.error.message,
            icon: 'error'
          })
        }
      })
    }
  }

  showOptions() {
    const url = this.router.url.split("/")[1];
    this.showButtons = !this.notOptionsUrls.includes(url);
  }

  toggleNavbar() {
    this.isActive = !this.isActive;
  }

  toggleSubmenu(module: string) {
    this.activeModule = this.activeModule === module ? null : module;
  }

  myAccount() {
    this.router.navigate(['/edit/profile']);
  }

  logout() {
    this._localStorageService.logout();
    this.router.navigate(['/login']);
  }

}
