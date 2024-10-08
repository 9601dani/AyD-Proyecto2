import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiAuth = "http://localhost:8080/auth";

  constructor( private http: HttpClient) { }

  registerUser(user: any){
    console.log("hare un post a ", `${this.apiAuth}/register`);
    return this.http.post(`${this.apiAuth}/register`, user);

  }
}
