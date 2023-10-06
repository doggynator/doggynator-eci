import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../_services/auth.service';
import { StorageService } from '../_services/storage.service';
import { UserService } from '../_services/user.service';
import { AppConstants } from '../_shared/app.constants';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent implements OnInit {
  form: any = {
    email: null,
    password: null,
  };

  facebookURL = AppConstants.FACEBOOK_AUTH_URL
  isLoading = false;
  isLoggedIn = false;
  isLoginFailed = false;
  errorMessage = '';
  roles: string[] = [];

  observable = {
    next: (data: any) => {
      this.storageService.saveUser(data.body);
      this.isLoginFailed = false;
      this.isLoggedIn = true;
      this.roles = this.storageService.getUser().roles;
      this.isLoading = false
      this.router.navigate(['/loading']).then(() => {
        window.location.reload();
      });
    },
    error: (err: any) => {
      if (err.error.status == 401) {
        this.errorMessage = "Email o contraseña incorrectos";
      } else {
        this.errorMessage = err.error.message;
      }
      this.isLoginFailed = true;
      this.isLoading = false
    },
  };

  constructor(
    private authService: AuthService,
    private storageService: StorageService,
    private userService : UserService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    let welcomeItem = document.getElementById('welcomeId');

    if (welcomeItem != null) {
      let hidden = welcomeItem.getAttribute('hidden');
      welcomeItem.setAttribute('hidden', hidden || '');
      console.error('hubo un error');
    }
    this.route.queryParams.subscribe((params) => {
      if (params['token']) {
        this.isLoading = true;
        this.authService
          .loginFacebookToken(params['token'])
          .subscribe(this.observable);
      }
    });
    if (this.storageService.isLoggedIn()) {
      this.isLoggedIn = true;
      this.roles = this.storageService.getUser().roles;
    }
  }

  onSubmit(): void {
    this.isLoading = true;
    const { email, password } = this.form;

    this.authService.login(email, password).subscribe(this.observable);
  }

  fb_login(): void {
    this.isLoading = true;
  }

  reloadPage(): void {
    window.location.reload();
  }
}
