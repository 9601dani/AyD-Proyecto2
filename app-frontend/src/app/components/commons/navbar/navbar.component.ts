import { Component, OnInit } from '@angular/core';
import { LocalStorageService } from '../../../services/local-storage.service';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { NotLogoDirective } from '../../../directives/not-logo.directive';
import { NotProfileDirective } from '../../../directives/not-profile.directive';
import { UserService } from '../../../services/user.service';
import { Observable } from 'rxjs';
import { Module } from '../../../models/Module.model';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    NotLogoDirective,
    NotProfileDirective
  ],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent implements OnInit {

  isLogged: boolean = false;
  isActive: boolean = false;
  showGuestButton: boolean = true;
  activeModule: any;
  modules: any[] = [];
  logoUrl = "";
  userPhoto = "";
  username = "";
  notOptionsUrls = ['/login'];
  constructor(
    private _localStorageService: LocalStorageService,
    private router: Router,
    private _userService: UserService
  ) { }

  ngOnInit(): void {

    this.disableReuseRoute();

    this.isLogged = this._localStorageService.getUserId() !== null;
    if (this.isLogged) {
      this.username = this._localStorageService.getUserName();
      this.showGuestButton = false;
      this.getPages();
      return;
    }

    this.showOptions();
  }

  disableReuseRoute() {
    this.router.routeReuseStrategy.shouldReuseRoute = () => false;

		this.router.events.subscribe((event) => {
			if (event.constructor.name === "NavigationEnd") {
				this.router.navigated = false;
			}
		});
  }

  getPages() {
    const id = this._localStorageService.getUserId();
    this._userService.getPages(id).subscribe({
      next: response => {
        this.modules = response;
        console.log(this.modules);
      }
    });
  }

  showOptions() {
    const url = this.router.url;
    this.showGuestButton = !this.notOptionsUrls.includes(url);
    console.log(url, this.showGuestButton, this.isLogged)
  }

  toggleNavbar() {
    this.isActive = !this.isActive;
  }

  toggleSubmenu(module: string) {
    this.activeModule = this.activeModule === module ? null : module;
  }

  myAccount() {

  }

  logout() {
    this._localStorageService.logout();
    this.router.navigate(['/home']);
  }

}
