import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AppConstants } from '../_shared/app.constants';

const httpOptions = {};

@Injectable({
  providedIn: 'root',
})
export class UserService {
  constructor(private http: HttpClient) { }

  getUser(): Observable<any> {
    return this.http.get(AppConstants.API_URL + '/user/me', httpOptions);
  }

  getUserBoard(): Observable<any> {
    return this.http.get(AppConstants.API_URL + '/user', httpOptions);
  }

  getModeratorBoard(): Observable<any> {
    return this.http.get(AppConstants.API_URL + '/mod', httpOptions);
  }

  getAdminBoard(): Observable<any> {
    return this.http.get(AppConstants.API_URL + '/admin', httpOptions);
  }

  saveUPreferences(settings : any): Observable<any> {
    return this.http.post(
      AppConstants.API_BASE_URL + '/saveUserPreferences',
      settings,
      httpOptions
    );
  }

  saveULikes(likes : any): Observable<any> {
    return this.http.post(
      AppConstants.API_BASE_URL + '/saveMeGusta',
      likes,
      httpOptions
    );
  }

  getDogsForUser(id: String): Observable<any> {
    const url = AppConstants.API_BASE_URL + '/dogsForUser' + '?userid=' + id;
    return this.http.get(url, httpOptions);
  }

  getDogsForUserWithKnn(id: String): Observable<any> {
    const url = AppConstants.API_BASE_URL + '/dogsForUserWithKnn' + '?userid=' + id;
    return this.http.get(url, httpOptions);
  }

  getDogForUserFromModel(id: String): Observable<any> {
    const url = AppConstants.API_BASE_URL + '/getDogForUserFromModel' + '?userid=' + id;
    return this.http.get(url, httpOptions);
  }

  getUStage(id: String): Observable<any> {
    const url = AppConstants.API_BASE_URL + '/getUserStage' + '?userid=' + id;
    return this.http.get(url, httpOptions);
  }

  getUPreferences(id: String): Observable<any> {
    const url = AppConstants.API_BASE_URL + '/getUserPreferences' + '?userid=' + id;
    return this.http.get(url, httpOptions);
  }

}
