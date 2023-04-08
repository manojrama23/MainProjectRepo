import { Injectable } from '@angular/core';
import { Http, Headers, Response } from '@angular/http';
//import { Observable } from 'rxjs/Observable';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { filter, map, mergeMap} from 'rxjs/operators';
//import 'rxjs/add/operator/map'

@Injectable()

export class UserManagementService {

  headers: any;
  constructor(private http: Http) { 
    this.headers = new Headers( { "content-type": "application/json"});
  }


  /*
   * get user list during page load
   * @param : serviceToken
   * @retun : json Object
   */

  getUserDetails(selectedCustomerList, serviceToken, paginationDetails, searchStatus, searchCriteria) {
      return this.http.post('/userList', JSON.stringify( {
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "pagination": paginationDetails,
        "customerName":selectedCustomerList.customerName,
        "customerId":selectedCustomerList.id,
        "searchStatus": searchStatus,
        "searchDetails": searchCriteria
      } ), { headers: this.headers } );
  } 

  /*
   * create new user
   * @param : userDetails object, serviceToken
   * @retun : json Object
   */

	createUser(userDetails, serviceToken){
		return this.http.post('/createUser', JSON.stringify( {
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "dbDetail": userDetails,
        "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId":JSON.parse(sessionStorage.selectedCustomerList).id
       
      } ), { headers: this.headers } );
	}
  

  updateUser(userDetails, serviceToken){
    return this.http.post('/updateUser', JSON.stringify( {
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "dbDetail": userDetails,
        "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId":JSON.parse(sessionStorage.selectedCustomerList).id
       
      } ), { headers: this.headers } );
  }

  /*
   * delete row user
   * @param : deleteUser object, serviceToken
   * @retun : json Object
   */

  deleteUserDetails(id, serviceToken){
    return this.http.post('/deleteUser', JSON.stringify( {
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "userId": id,
        "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId":JSON.parse(sessionStorage.selectedCustomerList).id
       
      } ), { headers: this.headers } );
  }
}
