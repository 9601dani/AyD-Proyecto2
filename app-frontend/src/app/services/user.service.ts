import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Module } from '../models/Module.model';
import { CompanySetting } from '../models/CompanySetting.model';
import {ResponseString, Service, UserAllResponse} from "../interfaces/interfaces";
import { LocalStorageService } from './local-storage.service';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  readonly apiUser = "http://localhost:8000/user";
  readonly apiServices = "http://localhost:8000/services";
  readonly apiCompanySettings = "http://localhost:8000/company-settings";

  constructor(private http: HttpClient, private _localStorage: LocalStorageService) { }

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
    user.imageProfile = this._localStorage.getUserPhoto();
    return this.http.put<UserAllResponse>(`${this.apiUser}/profile/${id}`, user);
  }

  getUserInfo(userId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUser}/info/${userId}`);
  }

  updateImgUserInformation(id: number, img: ResponseString): Observable<any> {
    return this.http.put<any>(`${this.apiUser}/profile/img/${id}`, img);
  }
  

  /**Routes for services CRU */

  getServiceById(id: number): Observable<Service> {
    return this.http.get<Service>(`${this.apiServices}/${id}`);
  }

  getAllServices(): Observable<Service[]> {
    return this.http.get<Service[]>(`${this.apiServices}`);
  }

  createService(service: Service): Observable<Service> {
    return this.http.post<Service>(`${this.apiServices}`, service);
  }

  updateService(id: number, service: Service): Observable<Service> {
    return this.http.put<Service>(`${this.apiServices}/${id}`, service);
  }

  set2fa(id: number): Observable<any> {
    return this.http.put(`${this.apiUser}/set-2fa/${id}`, null);
  }
  
}
