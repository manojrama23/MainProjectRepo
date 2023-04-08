import { Injectable } from '@angular/core';
import { Http, Headers, Response, ResponseContentType } from '@angular/http';
//import { Observable } from 'rxjs/Observable';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { filter, map, mergeMap} from 'rxjs/operators';
//import 'rxjs/add/operator/map'

@Injectable({
  providedIn: 'root'
})
export class SnrreportsService {

  headers: any;
  constructor(private http: Http) { 
    this.headers = new Headers( { "content-type": "application/json"});
  }

  getSnRReports(serviceToken,custId, selectDuration, selectedDate) {
      return this.http.post('/getCIReport', JSON.stringify( {
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "selectedDuration":selectDuration,
        "dailySelectedDate":selectedDate,
        "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId":custId ? custId : JSON.parse(sessionStorage.selectedCustomerList).id,
      } ), { headers: this.headers } );	   
    }

    snrGoClick(serviceToken,customerId,selectDuration,fromDt,toDt,selectedDate) {
        return this.http.post('/getCIReport', JSON.stringify( {
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "serviceToken": serviceToken,
          "customerId":customerId ? customerId : JSON.parse(sessionStorage.selectedCustomerList).id,
          "selectedDuration":selectDuration,
          "periodicFromDate":fromDt,
          "periodicToDate":toDt,
          "dailySelectedDate":selectedDate,
          "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
        } ), { headers: this.headers } );	   
      }

      getCustomerIdList(serviceToken) {
        return this.http.post('/getCustomerIdList', JSON.stringify( {
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "serviceToken": serviceToken,
          "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
          "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
        } ), { headers: this.headers } );	   
      }




  
}
