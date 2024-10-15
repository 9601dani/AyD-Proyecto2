import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ImgService {

  readonly apiImg = "http://localhost:8000/img";

  constructor(private http: HttpClient) { }

  upload(data: any): Observable<string[]> {
    return this.http.post<string[]>(`${this.apiImg}/upload`, data);
  }
}
