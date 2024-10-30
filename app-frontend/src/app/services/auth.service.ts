import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  readonly apiAuth = "http://localhost:8000/auth";

  constructor(private http: HttpClient) { }

  registerUser(user: any): Observable<any> {
    return this.http.post(`${this.apiAuth}/register`, user);
  }

  login(user: any): Observable<any> {
    return this.http.post(`${this.apiAuth}/login`, user);
  }

  verifyEmail(token: string): Observable<any> {
    return this.http.put(`${this.apiAuth}/verify-email/${token}`, null);
  }

  sendEmailVerification(data: any): Observable<any> {
    return this.http.put(`${this.apiAuth}/send-email/${data}`, null);
  }

  verify2fa(id: number, code: string): Observable<any> {
    return this.http.put(`${this.apiAuth}/verify-2FA/${id}/${code}`, null);
  }

  send2fa(id: number): Observable<any> {
    return this.http.post(`${this.apiAuth}/send-2FA/${id}`, null);
  }

  sendRecoveryPassword(data: any): Observable<any> {
    return this.http.post(`${this.apiAuth}/recovery-password/${data}`, null)
  }

  resetPassword(token: string, data: any): Observable<any> {
    return this.http.put(`${this.apiAuth}/reset-password/${token}`, data);
  }
}
