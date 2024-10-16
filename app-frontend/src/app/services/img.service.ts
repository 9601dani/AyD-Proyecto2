import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { ResponseString } from '../interfaces/interfaces';

@Injectable({
  providedIn: 'root'
})
export class ImgService {

  readonly apiImg = "http://localhost:8000/img";

  constructor(private http: HttpClient) { }

  upload(data: any): Observable<string[]> {
    return this.http.post<string[]>(`${this.apiImg}/upload`, data);
  }

  updateImgProfile(oldNamePath: string, file: File): Observable<ResponseString> {
    const formData = new FormData();
    
    formData.append('file', file);
    
    formData.append('nameOldImage', oldNamePath || '');
    return this.http.put<ResponseString>(`${this.apiImg}/upload/profile`, formData);
  }
  
}
