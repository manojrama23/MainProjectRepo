import { Injectable } from '@angular/core';
import { Http, Headers, Response, ResponseContentType } from '@angular/http';

@Injectable({
  providedIn: 'root'
})
export class ReportsService {

  headers: any;
  constructor(private http: Http) { 
    this.headers = new Headers( { "content-type": "application/json"});
  }

  getOvDetails(searchStatus,serviceToken,paginationDetails, programName, searchCriteria ){
      return this.http.post('/getOvStatusScheduledDetails', JSON.stringify( {
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
         "serviceToken": serviceToken,
         "pagination":paginationDetails,
         "programName":programName,
         "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
         //"customerId":(JSON.parse(sessionStorage.loginDetails).userGroup)=="Super Administrator"? custId : JSON.parse(sessionStorage.selectedCustomerList).id,
        "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
         "searchStatus": searchStatus,
         "searchCriteria": searchCriteria
         
   } ), { headers: this.headers } );
  }
  getOverallReportsDetails(searchStatus,searchDetails, serviceToken,paginationDetails,custId,programName){
    if(searchStatus == "load")
    {
      return this.http.post('/getReports', JSON.stringify( {
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
         "serviceToken": serviceToken,
         "pagination":paginationDetails,
         "programName":programName,
         "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
         "customerId":(JSON.parse(sessionStorage.loginDetails).userGroup)=="Super Administrator"? custId : JSON.parse(sessionStorage.selectedCustomerList).id,
//         "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
         "searchStatus": searchStatus
         
   } ), { headers: this.headers } );

    }
    else if(searchStatus == "search")
    {
      return this.http.post('/getReports', JSON.stringify( {
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
         "serviceToken": serviceToken,
         "pagination":paginationDetails,
         "programName":programName,
         "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
         "customerId":(JSON.parse(sessionStorage.loginDetails).userGroup)=="Super Administrator"? custId : JSON.parse(sessionStorage.selectedCustomerList).id,
         "searchStatus": searchStatus,
         "searchDetails": searchDetails,
         
   } ), { headers: this.headers } );
    }


    }

    downloadFile(serviceToken, custId,paginationDetails,programName, searchDetails, searchStatus,selectedItems){
      return this.http.post('/download',  {
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,  
        "pagination":paginationDetails,
           "programName":programName,  
        "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId":(JSON.parse(sessionStorage.loginDetails).userGroup)=="Super Administrator"? custId : JSON.parse(sessionStorage.selectedCustomerList).id,
        "searchDetails": searchDetails,
        "filter":selectedItems,
        "searchStatus": searchStatus
      } , { responseType: ResponseContentType.Blob });
    }
    getCustomerIdList(serviceToken){
      return this.http.post('/getCustomerIdList', JSON.stringify( {
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
    } ), { headers: this.headers } );
    }
    runSchedule(serviceToken,selectedProgramName,selectedfetchdays){
      return this.http.post('/scheduleCheck', JSON.stringify( {
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "programName": selectedProgramName,
        "fetchDate": selectedfetchdays
    } ), { headers: this.headers } );
    }
    downloadCiqFile(fileName,filePath, serviceToken) {
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
  getFailureLogs(serviceToken, key,type) {
    return this.http.post('/geterrorLogs', JSON.stringify({
      "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
      "serviceToken": serviceToken,
      //"runTestId": id,
      "preErrorFile":key.preErrorFile,
      "neGrowErrorFile":key.negrowErrorFile,
      "migErrorFile":key.migErrorFile,
      "postErrorFile":key.postErrorFile,
      "type":type,
      "wfmid":parseInt(key.wfmid),
      "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
      "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
      "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
      "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName
    }), { headers: this.headers });
  }
  ExportFile(serviceToken, searchCriteria, searchStatus,paginationDetails){
    return this.http.post('/exportOvDetails',  {
      "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
      "serviceToken": serviceToken,   
      "pagination":paginationDetails, 
      "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
      "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
      "searchDetails": searchCriteria,
      "searchStatus": searchStatus
    } , { responseType: ResponseContentType.Blob });
  }
  deleteRunTestData(rowId, serviceToken) {
    return this.http.post('/deleteOvDetails', JSON.stringify({
        "serviceToken": serviceToken,
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "id": rowId,
        "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
        "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName
    }), { headers: this.headers });
  }

  updateGenerateDetails(key,generateInfoAuditDetails, serviceToken){
    return this.http.post('/updateOvDetails', JSON.stringify( {
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "id": generateInfoAuditDetails.id,
        "programName":key.customerDetailsEntity.programName,
        "preMigGrowGenerationDate":generateInfoAuditDetails.preMigGrowGenerationDate,
        "envGenerationDate":generateInfoAuditDetails.envGenerationDate,
        "ciqGenerationDate":generateInfoAuditDetails.ciqGenerationDate,
        "trackerId": generateInfoAuditDetails.trackerId,
        "neId": generateInfoAuditDetails.neId,
        //"generateInfoAuditDetails": generateInfoAuditDetails
    } ), { headers: this.headers } );
}
  bulkReportDownload(serviceToken){
    return this.http.post('/getAuditCriticalParamsBulkReport',  {
      "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
      "serviceToken": serviceToken
    } , { responseType: ResponseContentType.Blob });
  }
  getAuditReportDetails(serviceToken, paginationDetails, searchCriteria) {
    return this.http.post(
      "/getAuditCriticalParamsSummaryReport",
      JSON.stringify({
        sessionId: JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        serviceToken: serviceToken,
        pagination: paginationDetails,
        searchCriteria: searchCriteria,
      }),
      { headers: this.headers }
    );
  }
  getFailureReport(serviceToken,searchCriteria){
    return this.http.post('/getMigScriptFailureReport',  {
      "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
      "serviceToken": serviceToken,
      "searchCriteria" : searchCriteria
    } , { responseType: ResponseContentType.Blob });
  }
  getVdartDailyStatusDetails(serviceToken, searchCriteria) {
    return this.http.post(
      "/getVdartDailyStatusDetails",
      JSON.stringify({
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "searchCriteria": searchCriteria,
        "searchStatus": "search",
        "showUserData": false,
        "migrationType": "postmigration",
        "migrationSubType": "AUDIT",
        "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
        "userName": JSON.parse(sessionStorage.getItem("loginDetails")).userName,
      }),
      { headers: this.headers , responseType: ResponseContentType.Blob }
    );
  }
}
