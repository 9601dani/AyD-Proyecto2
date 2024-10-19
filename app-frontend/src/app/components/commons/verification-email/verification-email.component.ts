import { Component, OnInit } from '@angular/core';
import { NavbarComponent } from "../navbar/navbar.component";
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-verification-email',
  standalone: true,
  imports: [NavbarComponent],
  templateUrl: './verification-email.component.html',
  styleUrl: './verification-email.component.scss'
})
export class VerificationEmailComponent implements OnInit {

  token: string = '';

  constructor(
    private _router: Router,
    private _activatedRoute: ActivatedRoute,
    private _authService: AuthService
  ) { }

  ngOnInit(): void {
    this._activatedRoute.paramMap.subscribe(params => {
      this.token = params.get('token') || '';
    });
  }

  verifyEmail() {
    this._authService.verifyEmail(this.token).subscribe({
      next: response => {
        Swal.fire({
          title: "Éxito!",
          text: "Usuario verificado!",
          icon: 'success',
          confirmButtonText: 'Ok!'
        })
      },
      error: err => {
        Swal.fire({
          title: 'Error!',
          text: err.error.message,
          icon: 'error',
          confirmButtonText: 'Ok!'
        })
      },
      complete: () => {
        this._router.navigate(['/login'])
      }
    })
  }

}
