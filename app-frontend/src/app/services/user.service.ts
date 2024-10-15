import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Module } from '../models/Module.model';
import { CompanySetting } from '../models/CompanySetting.model';
import {UserAllResponse} from "../interfaces/interfaces";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  readonly apiUser = "http://localhost:8000/user";
  readonly apiCompanySettings = "http://localhost:8000/company-settings";

  constructor(private http: HttpClient) { }

  getPages(id: number): Observable<Module[]> {
    return this.http.get<Module[]>(`${this.apiUser}/pages/${id}`);
  }

  getSettingsType(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiCompanySettings}/setting-types`);
  }

  getCompanySettingsByType(settingType: string): Observable<CompanySetting[]> {
    return this.http.get<CompanySetting[]>(`${this.apiCompanySettings}/setting-type/${settingType}`);
  }

  updateCompanySettings(data: any[]): Observable<any> {
    return this.http.put(`${this.apiCompanySettings}/update`, data);
  }
  
  getMyProfile(id: number): Observable<UserAllResponse> {
    return this.http.get<UserAllResponse>(`${this.apiUser}/profile/${id}`);
  }

  updateProfile(id: number, user: UserAllResponse): Observable<UserAllResponse> {
    return this.http.put<UserAllResponse>(`${this.apiUser}/profile/${id}`, user);
  }
}
