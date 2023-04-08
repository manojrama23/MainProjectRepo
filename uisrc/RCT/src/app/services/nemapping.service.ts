import { Injectable } from '@angular/core';
import { Http, Headers, Response, ResponseContentType } from '@angular/http';
//import { Observable } from 'rxjs/Observable';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { filter, map, mergeMap} from 'rxjs/operators';
//import 'rxjs/add/operator/map'

@Injectable({
  providedIn: 'root'
})
export class NemappingService {

  headers: any;
  constructor(private http: Http) { 
    this.headers = new Headers( { "content-type": "application/json"});
  }



  getNeMappingDetails(searchStatus,searchCriteria, serviceToken,paginationDetails){
    if(searchStatus == "load")
    {
      return this.http.post('/getNeMappingDetails', JSON.stringify( {
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
         "serviceToken": serviceToken,
         "pagination":paginationDetails,
         "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
         "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
         "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
         "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
         "searchStatus": searchStatus
         
   } ), { headers: this.headers } );

    }
    else if(searchStatus == "search")
    {
      return this.http.post('/getNeMappingDetails', JSON.stringify( {
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
         "serviceToken": serviceToken,
         "pagination":paginationDetails,
         "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
         "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
         "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
         "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
         "searchStatus": searchStatus,
         "searchCriteria": searchCriteria,
         
   } ), { headers: this.headers } );
    }


    }




    updateNemappingDetails( updatedDetails, serviceToken){
      return this.http.post('/saveNeMappingDetails', JSON.stringify( {
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "serviceToken": serviceToken,
          "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
          
          "updatedNeMapDetails":updatedDetails
      } ), { headers: this.headers } );
  }
}
