import { Injectable } from '@angular/core';
import { Http, Headers, Response, ResponseContentType } from '@angular/http';
//import { Observable } from 'rxjs/Observable';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { filter, map, mergeMap} from 'rxjs/operators';
//import 'rxjs/add/operator/map'


@Injectable({
  providedIn: 'root'
})
export class PregenerateService {

  headers: any;
  constructor(private http: Http) { 
    this.headers = new Headers( { "content-type": "application/json"});
  }


  /*
   * get user upload the xl file
   * @param : 
   * @retun : 
   */

  getGenerateDetails(searchStatus,searchCriteria, serviceToken,paginationDetails,fileType, showMySites){
        return this.http.post('/getCsvAuditDetails', JSON.stringify( {
             "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
              "serviceToken": serviceToken,
              "pagination":paginationDetails,
              "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
              "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
              "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
              "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
              "searchStatus": searchStatus,
              "searchCriteria": searchCriteria,
              "fileType":fileType,
              "showUserData" : showMySites
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

    validatedetails(ciqFileName,serviceToken,paginationDetails,neDetails,fsuType, generateAllSites = null) {
      return this.http.post('/validationEnbDetailsPreMigration', JSON.stringify({
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "serviceToken": serviceToken, 
          "pagination":paginationDetails,
          "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
          "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
          "fileName":ciqFileName,
          "neDetails":neDetails,
          "fsuType": fsuType,
          "generateAllSites": generateAllSites
      }), { headers: this.headers });
  }
   
  generatedetails( fileType,ciqFileName,neDetails,serviceToken,paginationDetails,remarksVal,selectedNEIDs,fsuType, integrationType, generateAllSites = null, supportCA = null, ovUpdate = false) {
    return this.http.post('/generateFile', JSON.stringify({
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken, 
        "pagination":paginationDetails,
        "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
        "fileType":fileType,        
        "ciqFileName":ciqFileName,
        "neDetails":neDetails,
        "neIDs": selectedNEIDs,
        "fsuType": fsuType,
        "generateAllSites": generateAllSites,
        "supportCA": supportCA,
	"ovUpdate": ovUpdate,
        "remarks": remarksVal,
        "integrationType": integrationType
    }), { headers: this.headers });
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


getNeSiteListData(ciqName, serviceToken){
  return this.http.post('/getEnbDetails5GMM', JSON.stringify( {
      "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
      "serviceToken": serviceToken,
      "ciqName": ciqName,
      "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
      "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
      "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
      "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
    } ), { headers: this.headers } );
}

    updateGenerateDetails(generateInfoAuditDetails, serviceToken){
        return this.http.post('/updateGeneratedFileDetails', JSON.stringify( {
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "generateInfoAuditDetails": generateInfoAuditDetails
        } ), { headers: this.headers } );
    }

   

    /*
     * delete row user
     * @param : deleteUser object, serviceToken
     * @retun : json Object
     */

    deleteUserDetails(id, fileName, filePath, serviceToken){
      return this.http.post('/deleteGeneratedFileDetails', JSON.stringify( {
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "id": id,
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "fileName":fileName,
            "filePath":filePath
          } ), { headers: this.headers } );
    }

    downloadFile(fileName,filePath, serviceToken) {
        return this.http.post('/downloadFile',  {
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,            
            "serviceToken":serviceToken,
            "filePath":filePath,            
            "fileName":fileName,
            "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
            "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName 
          } ,{ responseType: ResponseContentType.Blob } );

    }

    mailCiqFile(key,emailId, serviceToken) {
        return this.http.post('/sendMailWithAttachment', JSON.stringify({
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "fileName":key.fileName,
            "filePath":key.filePath,
            "emailId":emailId,
            "id":key.id,
            "source":"generate",
            "customerName": JSON.parse(sessionStorage.getItem("selectedCustomerList")).customerName,
            "customerId":JSON.parse(sessionStorage.getItem("selectedCustomerList")).id,
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
             "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName     
        }), { headers: this.headers });
    }
    getNeIdList(ciqName, selectedNe, serviceToken) {
      return this.http.post('/getNeIdDetails', JSON.stringify( {
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "ciqName": ciqName,
        "neId": selectedNe,
        "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
        "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
      } ), { headers: this.headers } );
    }
    viewOVFailureMessage(serviceToken, testId,smName,neName, useCaseName) {
      return this.http.post('/messageInfoOVpre', JSON.stringify({
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "serviceToken": serviceToken,
          "testId":testId,
          "SMName":smName,
          "NEName":neName,
          "migrationType": "premigration",
          "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
          "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
          "useCaseName": useCaseName
         
      }), { headers: this.headers });
    }
    retryMilestoneUpdate(serviceToken, ovRetryData, milestones = [], ovUpdate = false) {
      return this.http.post('/retryMilestoneUpdatepre', JSON.stringify({
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "serviceToken": serviceToken,
          "runTestId": ovRetryData ? ovRetryData.runTestId : "",
          "ciqName": ovRetryData ? ovRetryData.ciqName : "",
          "neName": ovRetryData ? ovRetryData.neName : "",
          "migrationType": "premigration",
          "ovUpdate": ovUpdate,
          "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
          "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
          "useCaseName": ovRetryData.useCaseName,
          "milestones": milestones

      }), { headers: this.headers });
    }
}
