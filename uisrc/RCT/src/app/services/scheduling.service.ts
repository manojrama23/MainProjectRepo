import { Injectable } from '@angular/core';
import { Http, Headers, Response, ResponseContentType } from '@angular/http';
//import { Observable } from 'rxjs/Observable';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { filter, map, mergeMap} from 'rxjs/operators';
//import 'rxjs/add/operator/map'

@Injectable({
  providedIn: 'root'
})
export class SchedulingService {

 
  headers: any;
  constructor(private http: Http) { 
    this.headers = new Headers( { "content-type": "application/json"});
  }



  getSchedulingDetails(searchStatus,searchDetails, serviceToken,paginationDetails,custId){
    if(searchStatus == "load")
    {
      return this.http.post('/getSchedulingDetails', JSON.stringify( {
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
         "serviceToken": serviceToken,
         "pagination":paginationDetails,
         "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
         "customerId":(JSON.parse(sessionStorage.loginDetails).userGroup)=="Super Administrator"? custId : JSON.parse(sessionStorage.selectedCustomerList).id,
//         "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
         "searchStatus": searchStatus
         
   } ), { headers: this.headers } );

    }
    else if(searchStatus == "search")
    {
      return this.http.post('/getSchedulingDetails', JSON.stringify( {
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
         "serviceToken": serviceToken,
         "pagination":paginationDetails,
         "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
         "customerId":(JSON.parse(sessionStorage.loginDetails).userGroup)=="Super Administrator"? custId : JSON.parse(sessionStorage.selectedCustomerList).id,
         "searchStatus": searchStatus,
         "searchDetails": searchDetails,
         
   } ), { headers: this.headers } );
    }


    }



  saveSchedule(schedulingDetails, serviceToken,custId){
    return this.http.post('/saveSchedulingDetails', JSON.stringify( {
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "schedulingDetails": schedulingDetails,
        "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId":(JSON.parse(sessionStorage.loginDetails).userGroup)=="Super Administrator"? custId : JSON.parse(sessionStorage.selectedCustomerList).id
       
      } ), { headers: this.headers } );
  }

/* 
  cloneSchedule(schedulingDetails, serviceToken)
  {
      return this.http.post('/cloneSchedule', JSON.stringify( {
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "schedulingDetails":schedulingDetails,
        "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
      } ), { headers: this.headers } );	
    
    
  } */


/*   updateSchedule(schedulingDetails, serviceToken){
      return this.http.post('/updateSchedule', JSON.stringify( {
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "schedulingDetails":schedulingDetails,
        "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
       
      } ), { headers: this.headers } );
    
  } */


  
  deleteSchedulingDetails(id, serviceToken,custId){
    return this.http.post('/deleteSchedulingDetails', JSON.stringify( {
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "serviceToken": serviceToken,
          "id": id,
          "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
          "customerId":(JSON.parse(sessionStorage.loginDetails).userGroup)=="Super Administrator"? custId : JSON.parse(sessionStorage.selectedCustomerList).id
        } ), { headers: this.headers } );
  }

  downloadFile(serviceToken, custId, searchDetails, searchStatus){
    return this.http.post('/exportSchedulingDetails',  {
      "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
      "serviceToken": serviceToken,    
      "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
      "customerId":(JSON.parse(sessionStorage.loginDetails).userGroup)=="Super Administrator"? custId : JSON.parse(sessionStorage.selectedCustomerList).id,
      "searchDetails": searchDetails,
      "searchStatus": searchStatus
    } , { responseType: ResponseContentType.Blob });
  }

  uploadFile(formdata,serviceToken,custId){
    formdata.append('sessionId', JSON.parse(sessionStorage.getItem("loginDetails")).sessionId);
    formdata.append('serviceToken', serviceToken);
    formdata.append('fileName', "schedulingFile" ),      
    formdata.append("customerName",JSON.parse(sessionStorage.selectedCustomerList).customerName);
    formdata.append("customerId",(JSON.parse(sessionStorage.loginDetails).userGroup)=="Super Administrator"? custId : JSON.parse(sessionStorage.selectedCustomerList).id);
    return this.http.post( '/importSchedulingDetails', formdata);
}
getCustomerIdList(serviceToken){
  return this.http.post('/getCustomerIdList', JSON.stringify( {
    "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
    "serviceToken": serviceToken,
} ), { headers: this.headers } );
}

}
