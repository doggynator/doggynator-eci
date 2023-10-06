import { environment } from './../../environments/environment';
export class AppConstants {
  public static API_BASE_URL = environment.apiURL || '';
  private static OAUTH2_URL =
    AppConstants.API_BASE_URL + '/oauth2/authorization';
  private static REDIRECT_URL = '?redirect_uri=' + (environment.redirectUrl || '/login');
  public static API_URL = AppConstants.API_BASE_URL + '/api';
  public static AUTH_API = AppConstants.API_URL + '/auth';
  public static FACEBOOK_AUTH_URL =
    AppConstants.OAUTH2_URL + '/facebook' + AppConstants.REDIRECT_URL;
}
