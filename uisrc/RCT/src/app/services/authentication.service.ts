import { Injectable } from '@angular/core';
import { Http, Headers, Response, ResponseContentType } from '@angular/http';

@Injectable()
export class AuthenticationService {
  headers: any;
  constructor(private http: Http) { 
    this.headers = new Headers( { "content-type": "application/json"});
  }

  /*
   * authenticate user login
   * @param : user name, password, serviceToken
   * @retun : json Object
   */

	login(username: string, password: string, serviceToken) {
    let encryptLoginPassword = btoa(password);
    return this.http.post('/loginAction', JSON.stringify( {
             "username": username,
             "password": encryptLoginPassword,
             "serviceToken": serviceToken,
             "platform": "RMT"
        } ), { headers: this.headers } );
  }

  /*
   * destroy the session and logout the user
   * @param : null
   * @retun : json Object
   */

	logout(serviceToken) {
    return this.http.post('/logoutAction', JSON.stringify( {
             "serviceToken":serviceToken,
              "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId
        } ), { headers: this.headers } );
  }

  /*
   * change password 
   * @param : json Object ( username, password, new password ), serviceToken
   * @retun : json Object
   */

	updatePassword(chngPwd, serviceToken){
    let encryptCurrentPassword = btoa(chngPwd.currentPwd),
        encryptNewPassword = btoa(chngPwd.newPswd);
    return this.http.post('/changePassword', JSON.stringify( {
            "userName": JSON.parse(sessionStorage.getItem("loginDetails")).userName,
            "currentPassword": encryptCurrentPassword,
            "newPassword": encryptNewPassword,
            "serviceToken":serviceToken,
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId
        } ), { headers: this.headers } );
  }

  /*
   * forgot password 
   * @param : json Object ( username, emailid ), serviceToken
   * @retun : json Object
   */

  forgotPassword(forgotPwd){
    return this.http.post('/forgotPassword', JSON.stringify({
      "userName": forgotPwd.userName,
      "emailId": forgotPwd.emailId
    } ), { headers: this.headers } );
  }

  downloadFile(fileName,filePath, serviceToken) {
    return this.http.post('/downloadFile',  {
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,            
        "serviceToken":serviceToken,
        "filePath":filePath,            
        "fileName":fileName
      } ,{ responseType: ResponseContentType.Blob } );

}
}	
