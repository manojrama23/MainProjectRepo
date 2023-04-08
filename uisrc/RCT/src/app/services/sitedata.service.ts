import { Injectable } from '@angular/core';
import { Http, Headers, Response, ResponseContentType } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map'

@Injectable()
export class SitedataService {

    headers: any;
    constructor(private http: Http) {
        this.headers = new Headers({ "content-type": "application/json" });
    }

    /*
     * get File Rule during page load
     * @param : searchStatus, searchCriteria, paginationDetails, serviceToken
     * @retun : json Object
     */

    getSiteCiqList(searchStatus, searchCriteria, serviceToken,fromDt,toDt) {
        return this.http.post('/getCiqList', JSON.stringify({
            "searchCriteria": searchCriteria,
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "searchStatus": searchStatus,
            "fromDate":fromDt,
			"toDate":toDt,
            "customerName": JSON.parse(sessionStorage.getItem("selectedCustomerList")).customerName,
            "customerId":JSON.parse(sessionStorage.getItem("selectedCustomerList")).id,
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName     
        }), { headers: this.headers });
    }

    getAllSiteData(searchStatus,searchCriteria, serviceToken,paginationDetails){
        return this.http.post('/getSiteDataDetails', JSON.stringify( {
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

	/*
   * To Add File Rule Details
   * @param : fileRuleFormData, serviceToken
   * @retun : json Object
   */
  packSiteData(ciqName, remarks, eNBName, eNBId, serviceToken, enbDetails = null) {
        return this.http.post('/packEnbData', JSON.stringify({
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "customerName": JSON.parse(sessionStorage.getItem("selectedCustomerList")).customerName,
            "customerId":JSON.parse(sessionStorage.getItem("selectedCustomerList")).id,
            "ciqFileName": ciqName,
            "enbName":eNBName,
            "enbId":eNBId,
            "enbDetails": enbDetails,
            "remarks":remarks,
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
             "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName     
        }), { headers: this.headers });
    }

    getENBData(ciqName, serviceToken){
        return this.http.post('/getEnbDetails', JSON.stringify( {
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "ciqName": ciqName.ciqFileName,
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
            "ciqName": ciqName.ciqFileName,
            "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
            "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
          } ), { headers: this.headers } );
      }

    downloadFile(key, serviceToken) {
        return this.http.post('/downloadFile',  {
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,            
            "serviceToken":serviceToken,
            "filePath":key.filePath,            
            "fileName":key.fileName,
            "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
            "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName 
          } ,{ responseType: ResponseContentType.Blob } );
    }

    deleteSiteDeta(key, paginationDetails, serviceToken) {
        return this.http.post('/deleteSiteDataDetails', JSON.stringify({
            "serviceToken": serviceToken,
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "id": key.id,
            "fileName":key.fileName,
            "filePath":key.filePath,
            "pagination":paginationDetails,
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName     
        }), { headers: this.headers });
    }

    updateFileRuleDetails(siteDataDetails, paginationDetails, serviceToken) {
        return this.http.post('/updateSiteDataDetails', JSON.stringify({
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "pagination":paginationDetails,
            "customerName": JSON.parse(sessionStorage.getItem("selectedCustomerList")).customerName,
            "customerId":JSON.parse(sessionStorage.getItem("selectedCustomerList")).id,
            "siteDataDetails": siteDataDetails,
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
             "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName     
        }), { headers: this.headers });
    }

    mailCiqFile(key,emailId, serviceToken) {
        return this.http.post('/sendMailWithAttachment', JSON.stringify({
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "fileName":key.fileName,
            "filePath":key.filePath,
            "emailId":emailId,
            "id":key.id,
            "source":"sitedata",
            "customerName": JSON.parse(sessionStorage.getItem("selectedCustomerList")).customerName,
            "customerId":JSON.parse(sessionStorage.getItem("selectedCustomerList")).id,
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
             "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName     
        }), { headers: this.headers });
    }

    getSiteReportInputDetails(serviceToken, ciqName, enbDetails){
        return this.http.post('/getSiteReportInputDetails', JSON.stringify( {
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "neDetails": enbDetails,
            "ciqName": ciqName,
            "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
            "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
          } ), { headers: this.headers } );
    }

    exportSiteReportDetails(reportData, serviceToken, ciqName, siteId) {
        return this.http.post('/exportSiteReportDetails',  {
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "reportDetails":reportData,
            "ciqName": ciqName,
            "siteId":siteId ? siteId : null,
            "customerName": JSON.parse(sessionStorage.getItem("selectedCustomerList")).customerName,
            "customerId":JSON.parse(sessionStorage.getItem("selectedCustomerList")).id,
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName     
          } ,{ responseType: ResponseContentType.Blob } );
    }
    viewSiteReportDetailsById(serviceToken, siteDataId, ciqName, enbDetails){
        return this.http.post('/getSiteDetailsById', JSON.stringify( {
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "neDetails": enbDetails,
            "siteDataId": siteDataId,
            "ciqName": ciqName,
            // "neDetails":[{"enbName":"061452_CONCORD_2_NH_HUB","enbId":"61452"}],
            "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
            "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
          } ), { headers: this.headers } );
    }

    viewOVFailureMessage(serviceToken, testId,ciqName,neName) {
        return this.http.post('/siteReportInfo', JSON.stringify({
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "testId":testId,
            "NEName":neName,
            "ciqName":ciqName,
            "migrationType":"",
            "migrationSubType":"",
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName           
        }), { headers: this.headers });
      }
      retryMilestoneUpdate(serviceToken, ovRetryData, milestones, ovUpdate = false) {
        return this.http.post('/ovToUploadSiteReport', JSON.stringify({
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "runTestId": ovRetryData ? ovRetryData.runTestId : "",
            "ciqName": ovRetryData ? ovRetryData.ciqFileName : "",
            "neName": ovRetryData ? ovRetryData.neName : "",
            "migrationType":"",
            "migrationSubType":"",
            "ovUpdate": ovUpdate,
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
            "milestones": milestones
        }), { headers: this.headers });
      }

      uploadSiteReportToOV(serviceToken, data) {
        return this.http.post('/reportUploadtoOV', JSON.stringify({
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "serviceToken": serviceToken,
          "runTestId": data.id,
          "neName":data.neName,
          "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
          "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName
             
        }), { headers: this.headers });
      }
}

