import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Module } from '../models/Module.model';
import { CompanySetting } from '../models/CompanySetting.model';
import { Attribute, Comment, CommentResponse, Employee, Resources, ResponseString, Roles, Service, ServiceWithEmplAndRes, UserAllResponse } from "../interfaces/interfaces";

import { LocalStorageService } from './local-storage.service';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  readonly apiUser = "http://localhost:8000/user";
  readonly apiServices = "http://localhost:8000/services";
  readonly apiCompanySettings = "http://localhost:8000/company-settings";
  readonly apiRoles = "http://localhost:8000/role";
  readonly apiEmployees = "http://localhost:8000/employee";
  readonly apiResources = "http://localhost:8000/resource";
  readonly apiComments = "http://localhost:8000/comment";
  readonly apiAppointment = "http://localhost:8000/appointment";

  constructor(private http: HttpClient, private _localStorage: LocalStorageService) { }

  getPages(id: number): Observable<Module[]> {
    return this.http.get<Module[]>(`${this.apiUser}/pages/${id}`);
  }

  getCompanySettingByKeyName(keyName: string): Observable<CompanySetting> {
    return this.http.get<CompanySetting>(`${this.apiCompanySettings}/${keyName}`);
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

  getServiceById(id: number): Observable<ServiceWithEmplAndRes> {
    return this.http.get<ServiceWithEmplAndRes>(`${this.apiServices}/${id}`);
  }

  getAllServices(): Observable<Service[]> {
    return this.http.get<Service[]>(`${this.apiServices}`);
  }

  createService(service: ServiceWithEmplAndRes): Observable<Service> {
    return this.http.post<Service>(`${this.apiServices}`, service);
  }

  updateService(id: number, service: Service): Observable<Service> {
    return this.http.put<Service>(`${this.apiServices}/${id}`, service);
  }

  set2fa(id: number): Observable<any> {
    return this.http.put(`${this.apiUser}/set-2fa/${id}`, null);
  }

  /**
   * Routes for roles
   */

  getAllRoles(): Observable<Roles[]> {
    return this.http.get<Roles[]>(`${this.apiRoles}`);
  }


  /**
   * Routes for employees
   */

  getAllEmployees(): Observable<Employee[]> {
    return this.http.get<Employee[]>(`${this.apiEmployees}`);
  }

  createEmployee(employee: any): Observable<Employee> {
    return this.http.post<Employee>(`${this.apiEmployees}`, employee);
  }


  /**
   * Routes for Attributes
   */

  getAllAttributes(): Observable<Attribute> {
    return this.http.get<Attribute>(`${this.apiResources}/attributes`);
  }

  createAttribute(attribute: Attribute): Observable<Attribute> {
    return this.http.post<Attribute>(`${this.apiResources}/attributes`, attribute);
  }

  /**
   * Routes for RoutesHasAttributes
   */

  deleteAttributeFromResource(attributeHasId: number): Observable<ResponseString> {
    return this.http.delete<ResponseString>(`${this.apiResources}/attributes/${attributeHasId}`);
  }

  /**
   * Routes for Resources
   */

  getAllResources(): Observable<Resources[]> {
    return this.http.get<Resources[]>(`${this.apiResources}`);
  }

  getResourceById(id: number): Observable<Resources> {
    return this.http.get<Resources>(`${this.apiResources}/${id}`);
  }

  createResource(resource: Resources): Observable<Resources> {
    return this.http.post<Resources>(`${this.apiResources}`, resource);
  }

  updateResource(id: number, resource: Resources): Observable<Resources> {
    return this.http.put<Resources>(`${this.apiResources}/${id}`, resource);
  }


  /**
   * Routes for Comments
   */

  getAllComments(): Observable<CommentResponse[]> {
    return this.http.get<CommentResponse[]>(`${this.apiComments}`);
  }

  saveComment(comment: Comment): Observable<CommentResponse> {
    return this.http.post<CommentResponse>(`${this.apiComments}`, comment);
  }


  getResourcesByServicesId(ids: number[]): Observable<any> {
    return this.http.get(`${this.apiServices}/reserve/resources`, {
      params: {
        ids: ids.join(",")
      }
    });
  }

  getEmployeesByServicesId(ids: number[]): Observable<any> {
    return this.http.get(`${this.apiServices}/reserve/employees`, {
      params: {
        ids: ids.join(",")
      }
    });
  }

  saveAppointment(data: any): Observable<any> {
    return this.http.post(`${this.apiAppointment}/save`, data)
  }

  findByResourceOrEmployee(resource: number, employee: number): Observable<any> {
    return this.http.get(`${this.apiAppointment}/find`, {
      params: {
        resource, employee
      }
    })
  }

  getMyAppointments(id: number): Observable<any> {
    return this.http.get(`${this.apiUser}/appointments/${id}`);
  }
}
