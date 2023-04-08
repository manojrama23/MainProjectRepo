import { Injectable } from '@angular/core';
import { Http, Headers, Response } from '@angular/http';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { filter, map, mergeMap} from 'rxjs/operators';
@Injectable({
  providedIn: 'root'
})
export class AudittrailService {

  headers: any;
  constructor(private http: Http) { 
    this.headers = new Headers( { "content-type": "application/json"});
  }


  /*
   * get AuditTrailDetails list during page load
   * @param : serviceToken
   * @retun : json Object
   */

	getAuditTrailDetails(searchCriteria, searchStatus, paginationDetails, serviceToken ){
    return this.http.post('/getAuditTrail', JSON.stringify( {
        "searchCriteria":searchCriteria,
        "searchStatus":searchStatus,
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "pagination":paginationDetails,
        "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId":JSON.parse(sessionStorage.selectedCustomerList).id
       
      } ), { headers: this.headers } );
  }
  /*
   * get EventSubName list
   * @param : serviceToken
   * @retun : json Object
   */
  getEventSubNameDetails(eventName, serviceToken ){
    return this.http.post('/getAuditFilter', JSON.stringify( {
        "eventName":eventName,
        "searchStatus":"EVENT_SUB_NAME",
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId":JSON.parse(sessionStorage.selectedCustomerList).id
       
      } ), { headers: this.headers } );
  }

    /*
   * get Action list  
   * @param : serviceToken
   * @retun : json Object
   */
  getEventActionDetails(eventName, eventSubName, serviceToken ){
    return this.http.post('/getAuditFilter', JSON.stringify( {
        "eventName":eventName,
        "eventSubName":eventSubName,
        "searchStatus":'EVENT_ACTION',
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId":JSON.parse(sessionStorage.selectedCustomerList).id
       
      } ), { headers: this.headers } );
  }
  /*
   * get UserName list
   * @param : serviceToken
   * @retun : json Object
   */
  getEventUserNameDetails(eventName, eventSubName, action, serviceToken ){
    return this.http.post('/getAuditFilter', JSON.stringify( {
        "eventName":eventName,
        "eventSubName":eventSubName,
        "actionPerformed":action,
        "searchStatus":'EVENT_USER_NAME',
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId":JSON.parse(sessionStorage.selectedCustomerList).id
       
      } ), { headers: this.headers } );
  }
}
