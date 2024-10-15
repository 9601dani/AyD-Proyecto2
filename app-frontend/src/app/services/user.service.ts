import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Module } from '../models/Module.model';
import {UserAllResponse} from "../interfaces/interfaces";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  readonly apiUser = "http://localhost:8000/user";

  constructor(private http: HttpClient) { }

  getPages(id: number): Observable<Module[]> {
    return this.http.get<Module[]>(`${this.apiUser}/pages/${id}`);
  }

  getMyProfile(id: number): Observable<UserAllResponse> {
    return this.http.get<UserAllResponse>(`${this.apiUser}/profile/${id}`);
  }

  updateProfile(id: number, user: UserAllResponse): Observable<UserAllResponse> {
    return this.http.put<UserAllResponse>(`${this.apiUser}/profile/${id}`, user);
  }
}
