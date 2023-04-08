import { Injectable } from '@angular/core';
import { Http, Headers, Response } from '@angular/http';
//import { Observable } from 'rxjs/Observable';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { filter, map, mergeMap} from 'rxjs/operators';
//import 'rxjs/add/operator/map'

@Injectable()

export class DashboardService {

  headers: any;
  constructor(private http: Http) { 
    this.headers = new Headers( { "content-type": "application/json"});
  }


  /*
   * get user list during page load
   * @param : serviceToken
   * @retun : json Object
   */

	getDashBoardDetails( serviceToken ){
	 return this.http.post('/dashBoardDetails', JSON.stringify( {
             "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
              "serviceToken": serviceToken,
              "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
              "customerId":JSON.parse(sessionStorage.selectedCustomerList).id
        } ), { headers: this.headers } );
    }
    
    cpuUsageData(serviceToken) {
        return this.http.post('/getCpuUsage', JSON.stringify( {
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
             "serviceToken": serviceToken,
             "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
             "customerId":JSON.parse(sessionStorage.selectedCustomerList).id
       } ), { headers: this.headers } );
    }

    getMapData(serviceToken, searchStatus, searchCriteria) {
        return this.http.post('/getMapDetails', JSON.stringify({
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "searchStatus": searchStatus,
            "searchDetails": searchCriteria,
            "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
            "customerId": JSON.parse(sessionStorage.selectedCustomerList).id
        }), { headers: this.headers });
    }
    
    getActiveUsers(serviceToken) {
        return this.http.post('/getUserMapDetails', JSON.stringify({
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
            "customerId": JSON.parse(sessionStorage.selectedCustomerList).id
        }), { headers: this.headers });
    }
}
