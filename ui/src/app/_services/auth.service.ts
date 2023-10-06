import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable, Subject } from 'rxjs';
import { AppConstants } from '../_shared/app.constants';


const httpOptions = {
  observe: 'response' as 'response'
};

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor(private http: HttpClient) {}

  login(email: string, password: string): Observable<any> {
    return this.http.post(
      AppConstants.AUTH_API + '/signin',
      {
        email,
        password,
      },
      httpOptions
    );
  }

  loginFacebookToken(token: string): Observable<any> {
    return this.http.post(
      AppConstants.AUTH_API + '/facebook/signin',
      {
        accessToken: token
      },
      httpOptions
    );
  }

  loginFacebook(): Observable<any> {
    return this.http.get(
      AppConstants.FACEBOOK_AUTH_URL,
      httpOptions
    );
  }

  register(username: string, email: string, password: string, matchingPassword: string): Observable<any> {
    return this.http.post(
      AppConstants.AUTH_API + '/signup',
      {
        username,
        email,
        password,
        matchingPassword,
        socialProvider: 'LOCAL'
      },
      httpOptions
    );
  }

  logout(): Observable<any> {
    let res: any;
    return this.http.post(
      AppConstants.AUTH_API + '/signout',
      null,
      httpOptions
    );
  }

  refreshToken() {
    return this.http.post(AppConstants.AUTH_API + '/refreshtoken', { }, httpOptions);
  }
}
