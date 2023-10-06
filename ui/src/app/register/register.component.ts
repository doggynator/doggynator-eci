import { Component, OnInit } from '@angular/core';
import { AuthService } from '../_services/auth.service';
import { AppConstants } from '../_shared/app.constants';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
  form: any = {
    username: null,
    email: null,
    password: null,
    matchingPassword: null
  };
  isSuccessful = false;
  isSignUpFailed = false;
  errorMessage = '';
  isLoading = false;
  facebookURL = AppConstants.FACEBOOK_AUTH_URL

  constructor(private authService: AuthService) { }

  ngOnInit(): void {
    console.debug("");

  }

  onSubmit(): void {
    const { username, email, password, matchingPassword } = this.form;

    this.authService.register(username, email, password, matchingPassword).subscribe({
      next: data => {
        this.isSuccessful = true;
        this.isSignUpFailed = false;
      },
      error: err => {
        this.errorMessage = err.error.message;
        this.isSignUpFailed = true;
      }
    });
  }

  fb_login(): void {
    this.isLoading = true;
  }
}
