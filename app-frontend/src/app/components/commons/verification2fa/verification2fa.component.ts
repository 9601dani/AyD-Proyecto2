import { Component, OnInit } from '@angular/core';
import { NavbarComponent } from "../navbar/navbar.component";

@Component({
  selector: 'app-verification2fa',
  standalone: true,
  imports: [NavbarComponent],
  templateUrl: './verification2fa.component.html',
  styleUrl: './verification2fa.component.scss'
})
export class Verification2faComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

  verifyCode() {

  }

}
