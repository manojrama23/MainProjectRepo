import { Injectable } from '@angular/core';
import { Http, Headers, Response, ResponseContentType } from '@angular/http';
//import { Observable } from 'rxjs/Observable';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { filter, map, mergeMap} from 'rxjs/operators';
//import 'rxjs/add/operator/map'

@Injectable()

export class CiquploadService {

  headers: any;
  constructor(private http: Http) { 
    this.headers = new Headers( { "content-type": "application/json"});
  }


  /*
   * get user upload the xl file
   * @param : 
   * @retun : 
   */

	uplaodFile(remarks, formdata, fileName, allowDuplicate, serviceToken, timeZone, activation ,ovCkeckedOrNot,scriptfiles){
        
        let uploadCiqFileDetails = {
            'sessionId': JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            'serviceToken': serviceToken,
            'activate':activation,
            'remarks': remarks,
            'searchStatus': "load",
            'allowDuplicate': allowDuplicate,
            "fileSourceType": "UPLOAD",
            "scriptfiles": scriptfiles,
            "overallInteraction":ovCkeckedOrNot,
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
            'customerName':JSON.parse(sessionStorage.selectedCustomerList).customerName,
            'customerId':JSON.parse(sessionStorage.selectedCustomerList).id
        };
        formdata.append("uploadCiqFileDetails",JSON.stringify(uploadCiqFileDetails))
    
        return this.http.post( '/uploadCiq', formdata);
    }
   

    getuploadetails(radioOperation,searchStatus, searchCriteria, serviceToken ,paginationDetails){
        if(radioOperation == "UPLOAD" || radioOperation == "FETCH") {
            return this.http.post('/getCiqAuditDetailsList', JSON.stringify( {
                "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
                 "serviceToken": serviceToken,
                 "searchStatus":searchStatus,
                 "fileSourceType":radioOperation,
                 "searchCriteria":searchCriteria,
                 "pagination":paginationDetails,
                 "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
                 "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
                 "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
                 "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
          
           } ), { headers: this.headers } );
        } else{
            return this.http.post('/getCiqAuditDetailsList', JSON.stringify( {
                "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
                 "serviceToken": serviceToken,
                 "searchStatus":searchStatus,
                 "searchCriteria":searchCriteria,
                 "fileSourceType":"",
                 "pagination":paginationDetails,
                 "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
                 "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
                 "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
                 "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
          
           } ), { headers: this.headers } );
        }
    }

    fetchCiqDetails(fetchFormDetails,allowDuplicate,serviceToken, activation,type, importSelectedSites) {
        let apiUrl = type == 'IMPORT' ? '/CiqsFromRFDB' :'fetchPreMigrationFiles' ;
        let fileSourceType = type == 'IMPORT' ? 'FETCHRFDB' :'FETCH';
        let dataObj = {
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
             "serviceToken": serviceToken,
             "ciqNetworkConfigId":fetchFormDetails.ciqNetworkConfigId,
             "scriptNetworkConfigId":fetchFormDetails.scriptNetworkConfigId,
              "allowDuplicate":allowDuplicate,
              "remarks":fetchFormDetails.remarks,
              "market":fetchFormDetails.market,
              "rfScriptList":fetchFormDetails.rfScriptList,
              "activate":activation,
              "fileSourceType":fileSourceType, 
              "checkList":fetchFormDetails.checkList,             
             "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
             "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
             "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
             "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
       }
       if(type == 'IMPORT') {
         dataObj['importSelectedSites'] = importSelectedSites
         dataObj['customerName'] = JSON.parse(sessionStorage.loginDetails).userName
       }
       return this.http.post(apiUrl, JSON.stringify(dataObj), { headers: this.headers } );
    }
    forceFetch(fetchFormDetails,allowDuplicate,serviceToken, activation){
        return this.http.post('/forceFetch', JSON.stringify( {
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "ciqNetworkConfigId":fetchFormDetails.ciqNetworkConfigId,
            "scriptNetworkConfigId":fetchFormDetails.scriptNetworkConfigId,
             "allowDuplicate":allowDuplicate,
             "remarks":fetchFormDetails.remarks,
             "market":fetchFormDetails.market,
             "rfScriptList":fetchFormDetails.rfScriptList,
             "activate":activation,
             "fileSourceType":"FETCH", 
             "checkList":fetchFormDetails.checkList,             
            "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
            "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName 
      } ), { headers: this.headers } );
      }
    updateCiqDetails(ciqDetails,key, serviceToken,formdata, deleteScriptFileList){      
        let updateCiqDetails = {
            'sessionId': JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            'serviceToken': serviceToken,
            'ciqAuditDetail':ciqDetails,                                  
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
            'customerName':JSON.parse(sessionStorage.selectedCustomerList).customerName,
            'customerId':JSON.parse(sessionStorage.selectedCustomerList).id,
            "deletedScripts": deleteScriptFileList
        };
        formdata.append("updateCiqDetails",JSON.stringify(updateCiqDetails))    
        return this.http.post( '/updateCiqAuditDetails', formdata);
    }

    getUploadedCiqFile(serviceToken, timeZone){
      return this.http.post('/getUploadedBomFileStatus', JSON.stringify( {
              "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
              "serviceToken": serviceToken,
              "timeZone":timeZone,
              "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
              "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
              "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
              "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
       
          } ), { headers: this.headers } );
    }

    /*
     * delete row user
     * @param : deleteUser object, serviceToken
     * @retun : json Object
     */

    deleteUserDetails(key, serviceToken){
      return this.http.post('/deleteCiq', JSON.stringify( {
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "networkType": key.networkType,
            "lsmVersion": key.lsmVersion,
            "id": key.id,
            "ciqFileName":key.ciqFileName,
            "scriptFileName":key.scriptFileName,
            "checklistFileName":key.checklistFileName,
            "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
            "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
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

  
}
