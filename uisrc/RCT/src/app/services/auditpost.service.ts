import { Injectable } from '@angular/core';
import { Http, Headers, Response, ResponseContentType } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map'

@Injectable()
export class AuditpostService {
    httpOptions: any;

    headers: any;

    constructor(private http: Http) {
    this.headers = new Headers({ "content-type": "application/json" });
   }

  /*
   * get File Rule during page load
   * @param : searchStatus, searchCriteria, paginationDetails, serviceToken
   * @retun : json Object
   */

  getRunTest(searchStatus, searchCriteria, paginationDetails, serviceToken, commissionType, showMySites) {
    return this.http.post('/loadrunTest', JSON.stringify({
      "pagination": paginationDetails,  
      "searchCriteria": searchCriteria ? searchCriteria : {},
      "searchStatus": searchStatus,
      "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
      "serviceToken": serviceToken,
      "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
      "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
      "migrationSubType":"AUDIT",
      "migrationType":"postmigration",
      "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
      "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
      "userName": JSON.parse(sessionStorage.getItem("loginDetails")).userName,
      "showUserData" : showMySites
    }), { headers: this.headers });
  }

	 /*
     * get user upload the . file
     * @param : 
     * @retun : 
     */

    uploadRunTestDetails(serviceToken, runTestFormDetails,commissionType,requestType, isRETUseCaseSelected = false,  formdata = null) {
      if(requestType == "RUN_TEST"){
        if(isRETUseCaseSelected) {
          let retRunTestData = {
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "userName": JSON.parse(sessionStorage.getItem("loginDetails")).userName,
            "serviceToken": serviceToken,
            "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
            "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
            "runTestFormDetails": runTestFormDetails,
            "requestType": requestType,
            "migrationType": "postmigration",
            "migrationSubType": "AUDIT",
            "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName
          }
          formdata.append("retRunTest", JSON.stringify(retRunTestData));
          return this.http.post('/runTestRET', formdata);
        }
        else {
          return this.http.post('/runTest', JSON.stringify({
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "userName": JSON.parse(sessionStorage.getItem("loginDetails")).userName,
            "serviceToken": serviceToken,
            "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
            "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
            "runTestFormDetails": runTestFormDetails,
            "requestType": requestType,
            "migrationType": "postmigration",
            "migrationSubType": "AUDIT",
            "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName
          }), { headers: this.headers });
        }
      }else if(requestType =="CHECK_CONNECTION"){
        return this.http.post('/checkConnection', JSON.stringify({
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "userName": JSON.parse(sessionStorage.getItem("loginDetails")).userName,
          "serviceToken": serviceToken,
          "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
          "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
          "runTestFormDetails": runTestFormDetails,
          "requestType":requestType,
          "migrationType":"postmigration",
          "migrationSubType":"AUDIT",
          "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
          "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
        }), { headers: this.headers });
      }
      else if(requestType =="GENERATE"){
        return this.http.post('/generateScript', JSON.stringify({
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "userName": JSON.parse(sessionStorage.getItem("loginDetails")).userName,
          "serviceToken": serviceToken,
          "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
          "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
          "runTestFormDetails": runTestFormDetails,
          "requestType":requestType,
          "migrationType":"postmigration",
          "migrationSubType":"AUDIT",
          "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
          "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
        }), { headers: this.headers });
      }
        
    }

    saveUseCaseContent(useCaseData, serviceToken) {
        return this.http.post('/saveUseCaseContent', JSON.stringify({
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "useCaseName": useCaseData.useCaseName,
            "actualExecution": useCaseData.actualExecution,
            "updatedExecution": useCaseData.updatedExecution,
            "inEditMode" : useCaseData.inEditMode,
            "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
            "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
        }), { headers: this.headers });
    }
    
    /*
   * To delete File Rule
   * @param : rowId, serviceToken
   * @retun : json Object
   */

    deleteRunTestData(rowId, serviceToken, commissionType) {
      return this.http.post('/deleteRunTestData', JSON.stringify({
          "serviceToken": serviceToken,
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "id": rowId,
          "migrationType":"postmigration",
          "migrationSubType":"AUDIT",
          "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
          "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
          "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
          "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName
      }), { headers: this.headers });
    }
  viewTestResult(serviceToken, testId,smName,neName) {
    return this.http.post('/ruleResult', JSON.stringify({
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "testId":testId,
        "SMName":smName,
        "NEName":neName,
        "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
       
    }), { headers: this.headers });
  }

  reRunTest(serviceToken, testId, useCaseDetails) {
    return this.http.post('/reRunTest', JSON.stringify({
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "runTestId":testId,
        "useCaseDetails": useCaseDetails,
        "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
       
    }), { headers: this.headers });
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
        "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
       
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

    getChecklistSheetDetails(serviceToken, rowData) {
        return this.http.post('/getCheckListAllSheetNames', JSON.stringify({
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
            "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
            "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
            "checklistFileName": rowData.checklistFileName,
            "ciqFileName": rowData.ciqName
        }), { headers: this.headers });
    }

    getDeatilsByChecklist(serviceToken, paginationDetails, rowData){
	    return this.http.post('/getAllDetailsByChecklist', JSON.stringify( {
	        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
		    "serviceToken": serviceToken,
			"checklistFileName":rowData.checklistFileName,
            "ciqFileName":rowData.ciqName,
            "enodeName":rowData.enodeName,
            "sheetName":rowData.sheetName,
            "runTestId" : rowData.id,
		    "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
        	"customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
			"pagination":paginationDetails,
			"programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        	"programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
	      } ), { headers: this.headers } );
    }

    updateCheckList(serviceToken, rowData, checklistTableData) {
        return this.http.post('/updateCheckListFileDetails', JSON.stringify( {
	        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
		    "serviceToken": serviceToken,
			"checklistFileName":rowData.checklistFileName,
            "ciqFileName":rowData.ciqName,
            "enodeName":rowData.enodeName,
			"sheetName":rowData.sheetName,
		    "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
        	"customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
			"programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
            "checklistTableData": checklistTableData
	      } ), { headers: this.headers } );
    }

    getRunningLogs(serviceToken, id) {
        return this.http.post('/getRunningLogs', JSON.stringify( {
	        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
		    "serviceToken": serviceToken,
			"runTestId":id,
		    "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
        	"customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
			"programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
	      } ), { headers: this.headers } );
    }

    downloadFile(serviceToken, key) {
        return this.http.post('/downloadLogs',  {
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,            
            "serviceToken":serviceToken,
            "runTestId":key.id,
            "filePath": key.outputFilepath,
            "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
            "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName 
          } ,{ responseType: ResponseContentType.Blob } );

    }

/*     downLoadResultFile(serviceToken, key) {
        return this.http.post('/downloadHtml',  {
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,            
            "serviceToken":serviceToken,
            "runTestId":key.id,
            "fileName": key.result,
            "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
            "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName 
          } ,{ responseType: ResponseContentType.Blob } );

    } */

    getConnectionLog(serviceToken) {
        return this.http.post('/getConnectionLog', JSON.stringify( {
	        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "migrationType":"postmigration",
            "migrationSubType":"AUDIT",
		    "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
        	"customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
			"programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
	      } ), { headers: this.headers } );
    }

  /*   getUseCaseListData(ciqName,neDetails,serviceToken){
        return this.http.post('/loadUsecase', JSON.stringify( {
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "ciqName": ciqName,
            "neDetails":neDetails,           
            "migrationSubType":"AUDIT",
            "migrationType":"postmigration",
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
          } ), { headers: this.headers } );
      } */

      downloadResultFile(fileName,filePath, serviceToken) {
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

    getChecklistScriptDetails(serviceToken, rowData, stepIndex) {
        return this.http.post('/getCheckListBasedScriptExecutionDetails', JSON.stringify({
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "stepIndex": stepIndex,
            "checkListFileName": rowData.checklistFileName,
            "ciqFileName": rowData.ciqName,
            "sheetName": rowData.sheetName,
            "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
            "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
            "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName
        }), { headers: this.headers });
    }

    downloadGenScripts(filepath, serviceToken) {
        return this.http.post('/downloadScripts',  {
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,            
            "serviceToken":serviceToken,
            "generateScriptPath": filepath,
          } ,{ responseType: ResponseContentType.Blob } );

    }

    getNeConfigDetails(serviceToken, ciqName, selectedNE) {
      return this.http.post('/getNeConfigDetails', JSON.stringify({
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
        "userName": JSON.parse(sessionStorage.getItem("loginDetails")).userName,
        "enbParamsDetails": {
          "ciqName": ciqName.ciqFileName,
          "enbId": selectedNE ? selectedNE[0].eNBId : null,
          "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
          "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName
        }

      }), { headers: this.headers });
    }

    getMigUseCases(ciqName, selectedNE, serviceToken, commissionType) {
      return this.http.post('/getMigUseCases', JSON.stringify({
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "ciqFileName": ciqName ? ciqName.ciqFileName : "",
        "migrationType":"postmigration",
        "migrationSubType":"AUDIT",
        "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
        "userName": JSON.parse(sessionStorage.getItem("loginDetails")).userName,
        "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
        "enbID": selectedNE // ? selectedNE.eNBId : null
      }), { headers: this.headers });
    }
    getAuditPassFailReport(rowDetails, serviceToken) {
      return this.http.post('/getAudit4GPassFailSummaryReport', JSON.stringify({
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "runTestId": rowDetails.id,
        "neName": rowDetails.neName,
        "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
        "userName": JSON.parse(sessionStorage.getItem("loginDetails")).userName,
        "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
      }), { headers: this.headers });
    }
    getAuditSummaryReport(rowDetails, serviceToken) {
      return this.http.post('/getAudit4GSummaryReport', JSON.stringify({
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "runTestId": rowDetails.id,
        "neName": rowDetails.neName,
        "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
        "userName": JSON.parse(sessionStorage.getItem("loginDetails")).userName,
        "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
      }), { headers: this.headers });
    }
    downloadBulkAuditReport(serviceToken, paginationDetails, searchCriteria, searchStatus, bulkSummaryReport) {
      let data = {
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "searchCriteria": searchCriteria ? searchCriteria : {},
        "searchStatus": searchStatus,
        "migrationSubType":"AUDIT",
        "showUserData": false,
        "migrationType":"postmigration",
        "pagination": paginationDetails,
        "customerName": JSON.parse(sessionStorage.getItem("selectedCustomerList")).customerName,
        "customerId":JSON.parse(sessionStorage.getItem("selectedCustomerList")).id,
        "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName ,
        "userName": JSON.parse(sessionStorage.getItem("loginDetails")).userName,
        "wfmKey": false   
      }
      if (bulkSummaryReport) {
        return this.http.post('/bulkAuditSummaryReport', data  ,{ responseType: ResponseContentType.Blob } );
      } else {
        return this.http.post('/bulkAuditReport', data  ,{ responseType: ResponseContentType.Blob } );
      }
    }
    downloadAuditPassFailReport(serviceToken, rowDetails, auditIssueNEInfo) {
      return this.http.post('/downloadAudit4GPassFailSummaryReport',  {
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "runTestId": rowDetails.id,
        "neId": auditIssueNEInfo.neId,
        "neName": auditIssueNEInfo.neName,
        "customerName": JSON.parse(sessionStorage.getItem("selectedCustomerList")).customerName,
        "customerId":JSON.parse(sessionStorage.getItem("selectedCustomerList")).id,
        "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName ,
        "userName": JSON.parse(sessionStorage.getItem("loginDetails")).userName,    
      } ,{ responseType: ResponseContentType.Blob } );
    }
    downloadAuditSummaryReport(serviceToken, postAuditIssues, auditIssueNEInfo) {
      return this.http.post('/downloadAudit4GSummaryReport',  {
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "postAuditIssues":postAuditIssues,
        "neId": auditIssueNEInfo.neId,
        "neName": auditIssueNEInfo.neName,
        "customerName": JSON.parse(sessionStorage.getItem("selectedCustomerList")).customerName,
        "customerId":JSON.parse(sessionStorage.getItem("selectedCustomerList")).id,
        "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName     
      } ,{ responseType: ResponseContentType.Blob } );
    }
    getRadioUnitListData(ciqName, selectedNEItems, serviceToken){
      return this.http.post('/fetchRadioUnit', JSON.stringify({
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "serviceToken": serviceToken,
          "ciqName": ciqName,
          // "eNBName": selectedNEItems.eNBName,
          "eNBDetails": selectedNEItems,
          "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
          "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
          "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
          "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName
      }), { headers: this.headers });
    }
    viewOVFailureMessage(serviceToken, testId,smName,neName, useCaseName) {
      return this.http.post('/messageInfoOV', JSON.stringify({
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "serviceToken": serviceToken,
          "testId":testId,
          "SMName":smName,
          "NEName":neName,
          "migrationType":"postmigration",
          "migrationSubType":"AUDIT",
          "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
          "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
          "useCaseName": useCaseName
         
      }), { headers: this.headers });
    }
    retryMilestoneUpdate(serviceToken, ovRetryData, milestones, ovUpdate = false) {
      return this.http.post('/retryMilestoneUpdate', JSON.stringify({
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "serviceToken": serviceToken,
          "runTestId": ovRetryData ? ovRetryData.runTestId : "",
          "ciqName": ovRetryData ? ovRetryData.ciqName : "",
          "neName": ovRetryData ? ovRetryData.neName : "",
          "migrationType":"postmigration",
          "migrationSubType":"AUDIT",
          "ovUpdate": ovUpdate,
          "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
          "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
          "useCaseName": ovRetryData.useCaseName,
          "milestones": milestones
         
      }), { headers: this.headers });
    }
    retryFailedMilestoneUpdate(serviceToken, ovRetryData, failedMilestone, ovUpdate = false) {
      return this.http.post('/retryFailedMilestoneUpdate', JSON.stringify({
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "serviceToken": serviceToken,
          "runTestId": ovRetryData ? ovRetryData.runTestId : "",
          "ciqName": ovRetryData ? ovRetryData.ciqName : "",
          "neName": ovRetryData ? ovRetryData.neName : "",
          "migrationType":"postmigration",
          "migrationSubType":"AUDIT",
          "ovUpdate": ovUpdate,
          "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
          "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
          "failedMilestone": failedMilestone,
          "useCaseName": ovRetryData.useCaseName
         
      }), { headers: this.headers });
    }
}

