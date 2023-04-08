import { Injectable } from '@angular/core';
import { Http, Headers, Response, ResponseContentType } from '@angular/http';
//import { Observable } from 'rxjs/Observable';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { filter, map, mergeMap} from 'rxjs/operators';
//import 'rxjs/add/operator/map'

@Injectable({
  providedIn: 'root'
})
export class PregrowService {

  headers: any;
  constructor(private http: Http) { 
    this.headers = new Headers( { "content-type": "application/json"});
  }

    getPreGrowDetails( serviceToken ,paginationDetails,searchStatus,searchCriteria,){
        return this.http.post('/getNeGrowDetails', JSON.stringify( {
             "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
              "serviceToken": serviceToken,
              "pagination":paginationDetails,
              "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
              "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
              "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
              "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
              "searchStatus": searchStatus,
              "searchCriteria": searchCriteria
        } ), { headers: this.headers } );
    }

    growNe(growData, serviceToken) {
        return this.http.post('/grow', JSON.stringify( {
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "growData": growData
        } ), { headers: this.headers } );
    }


getNeListData(ciqName, serviceToken){
    return this.http.post('/getEnbDetails', JSON.stringify( {
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "ciqName": ciqName,
        "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
        "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
      } ), { headers: this.headers } );
}
getCiqListData(serviceToken,fromDate,toDate,searchStatus){			 
    return this.http.post('/getCiqList', JSON.stringify( {
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
                "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
                "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
                "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
                "searchStatus":searchStatus,
                "fromDate":fromDate,
                "toDate":toDate
      } ), { headers: this.headers } );
    }

    updatePreGrowDetails(ciqDetails, serviceToken){
        return this.http.post('/updateCiqAuditDetails', JSON.stringify( {
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "ciqAuditDetail": ciqDetails
        } ), { headers: this.headers } );
    }

    viewTestResult(serviceToken, testId) {
        return this.http.post('/neGrowResult', JSON.stringify({
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "testId":testId
           
        }), { headers: this.headers });
      }

   

    /*
     * delete row user
     * @param : deleteUser object, serviceToken
     * @retun : json Object
     */

    deleteFileDetails(id, fileName, serviceToken){
      return this.http.post('/deleteCiq', JSON.stringify( {
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "id": id,
            "fileName":fileName
          } ), { headers: this.headers } );
    }

    submitPreGrowDetails(ciqDetails, lsmName, serviceToken){
      return this.http.post('/transferCiqAuditFile', JSON.stringify( {
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "serviceToken": serviceToken,
          "csvAuditDetails": ciqDetails,
          "lsmName":lsmName.split("-Id-")[0],
          "lsmId":lsmName.split("-Id-")[1],
          "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
          "customerId":JSON.parse(sessionStorage.selectedCustomerList).id
      } ), { headers: this.headers } );
  }

  scriptOutput(serviceToken,testId,useCaseName,useCaseId,scriptName,scriptId) {
    return this.http.post('/scriptResult', JSON.stringify({
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "testId":testId,
        "UseCaseId":useCaseId,
        "UseCaseName": useCaseName,
        "ScriptId":scriptId,
        "ScriptName": scriptName,
       
    }), { headers: this.headers });
  }
}
