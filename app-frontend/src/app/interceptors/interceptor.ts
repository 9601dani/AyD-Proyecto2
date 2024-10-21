import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpResponse } from "@angular/common/http";
import { catchError, Observable, tap, throwError } from "rxjs";
import { CookieService } from "ngx-cookie-service";
import { LocalStorageService } from "../services/local-storage.service";
import { Router } from "@angular/router";
import { Injectable } from "@angular/core";
import Swal from "sweetalert2";

@Injectable()
export class Interceptor implements HttpInterceptor {

    constructor(
        private _cookieService: CookieService,
        private _localStorageService: LocalStorageService,
        private _router: Router
    ) { }

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        req = req.clone({
           setHeaders: {
            Authorization: `Bearer ${this._cookieService.get("token")}`
           } 
        });

        return next.handle(req).pipe(
            catchError((error: any) => {
                if(error instanceof HttpErrorResponse) {
                    if(error.status === 403) {
                        const { message } = error.error;
                        this._localStorageService.clear();
                        this._cookieService.deleteAll();

                        Swal.fire({
                            title: 'Error!',
                            text: message,
                            icon: 'error',
                            confirmButtonText: 'Ok!'
                        })

                        this._router.navigateByUrl('/temp', { skipLocationChange: true }).then(() => {
                            this._router.navigate(["/home"]);
                        });
                        
                    }
                }
                return throwError(() => error);
            })
        );
    }

}