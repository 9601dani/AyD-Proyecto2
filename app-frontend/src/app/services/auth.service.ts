import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  readonly apiAuth = "http://localhost:8080/auth";

  constructor(private http: HttpClient) { }

  registerUser(user: any): Observable<any> {
    return this.http.post(`${this.apiAuth}/register`, user);
  }

  login(user: any): Observable<any> {
    return this.http.post(`${this.apiAuth}/login`, user);
  }
}
