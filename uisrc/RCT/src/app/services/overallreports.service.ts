import { Injectable } from '@angular/core';
import { Http, Headers, Response, ResponseContentType } from '@angular/http';
//import { Observable } from 'rxjs/Observable';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { filter, map, mergeMap} from 'rxjs/operators';
//import 'rxjs/add/operator/map'

@Injectable({
  providedIn: 'root'
})
export class OverallreportsService {

  headers: any;
  constructor(private http: Http) { 
    this.headers = new Headers( { "content-type": "application/json"});
  }



  getOverallReportsDetails(searchStatus,searchDetails, serviceToken,paginationDetails,custId){
    if(searchStatus == "load")
    {
      return this.http.post('/getOverallReportsDetails', JSON.stringify( {
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
      return this.http.post('/getOverallReportsDetails', JSON.stringify( {
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



    saveOverallReportsDetails(overallDetails, serviceToken,custId){
    return this.http.post('/saveOverallReportsDetails', JSON.stringify( {
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "overallDetails": overallDetails,
        "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId":(JSON.parse(sessionStorage.loginDetails).userGroup)=="Super Administrator"? custId : JSON.parse(sessionStorage.selectedCustomerList).id
       
      } ), { headers: this.headers } );
  }



  
  deleteOverallReportsDetails(id, serviceToken,custId){
    return this.http.post('/deleteOverallReportsDetails', JSON.stringify( {
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "serviceToken": serviceToken,
          "id": id,
          "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
          "customerId":(JSON.parse(sessionStorage.loginDetails).userGroup)=="Super Administrator"? custId : JSON.parse(sessionStorage.selectedCustomerList).id
        } ), { headers: this.headers } );
  }

  downloadFile(serviceToken, custId, searchDetails, searchStatus){
    return this.http.post('/exportOverallDetails',  {
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
    formdata.append('fileName', "overallReportsFile" ),      
    formdata.append("customerName",JSON.parse(sessionStorage.selectedCustomerList).customerName);
    formdata.append("customerId",(JSON.parse(sessionStorage.loginDetails).userGroup)=="Super Administrator"? custId : JSON.parse(sessionStorage.selectedCustomerList).id);
    return this.http.post( '/importOverallDetails', formdata);
}



saveEodReportsDetails(overallDetails,serviceToken,custId){
  if(custId ==2){
    return this.http.post('/saveOverallReportsDetails', JSON.stringify( {
      "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,          
      "serviceToken": serviceToken,
      "overallDetails":overallDetails,
      "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
      "customerId": (JSON.parse(sessionStorage.loginDetails).userGroup) == "Super Administrator" ? custId : JSON.parse(sessionStorage.selectedCustomerList).id

    } ), { headers: this.headers } );
  }else if(custId ==4){
    return this.http.post('/saveOverallReportsDetails', JSON.stringify( {
      "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,          
      "serviceToken": serviceToken,
      "overallDetails":overallDetails,
      "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
      "customerId": (JSON.parse(sessionStorage.loginDetails).userGroup) == "Super Administrator" ? custId : JSON.parse(sessionStorage.selectedCustomerList).id

    } ), { headers: this.headers } );
  }
 
}
deleteNeVersion(id, serviceToken,custId){
  return this.http.post('/deleteOverallReportsDetails', JSON.stringify( {
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId": (JSON.parse(sessionStorage.loginDetails).userGroup) == "Super Administrator" ? custId : JSON.parse(sessionStorage.selectedCustomerList).id,
        "id": id   
      } ), { headers: this.headers } );
}
getCustomerIdList(serviceToken){
  return this.http.post('/getCustomerIdList', JSON.stringify( {
    "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
    "serviceToken": serviceToken,
} ), { headers: this.headers } );
}
}
