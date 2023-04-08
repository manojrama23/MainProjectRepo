import { Injectable } from '@angular/core';
import { Http, Headers, Response, ResponseContentType } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map'

@Injectable()
export class RuntestService {
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
      "migrationSubType":commissionType,
      "migrationType":"migration",
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

    uploadRunTestDetails(serviceToken, runTestFormDetails,commissionType,requestType) {
      if(requestType == "RUN_TEST"){
        return this.http.post('/runTest', JSON.stringify({
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "userName": JSON.parse(sessionStorage.getItem("loginDetails")).userName,
          "serviceToken": serviceToken,
          "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
          "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
          "runTestFormDetails": runTestFormDetails,
          "requestType":requestType,
          "migrationType":"migration",
          "migrationSubType":commissionType,
          "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
          "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
        }), { headers: this.headers });
      }else if(requestType =="CHECK_CONNECTION"){
        return this.http.post('/checkConnection', JSON.stringify({
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "userName": JSON.parse(sessionStorage.getItem("loginDetails")).userName,
          "serviceToken": serviceToken,
          "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
          "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
          "runTestFormDetails": runTestFormDetails,
          "requestType":requestType,
          "migrationType":"migration",
          "migrationSubType":commissionType,
          "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
          "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
        }), { headers: this.headers });
      }else if(requestType =="GENERATE"){
        return this.http.post('/generateScript', JSON.stringify({
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "userName": JSON.parse(sessionStorage.getItem("loginDetails")).userName,
          "serviceToken": serviceToken,
          "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
          "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
          "runTestFormDetails": runTestFormDetails,
          "requestType":requestType,
          "migrationType":"migration",
          "migrationSubType":commissionType,
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
          "migrationType":"migration",
          "migrationSubType":commissionType,
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

  viewFailureMessage(serviceToken, testId,smName,neName) {
    return this.http.post('/messageInfo', JSON.stringify({
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "testId":testId,
        "SMName":smName,
        "NEName":neName,
        "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
       
    }), { headers: this.headers });
  }

  reRunContinueTest(serviceToken, type, testId, skipScriptIds, reRunScriptID, commissionType,wfmid, prePostAuditFlag = false, ovUpdate = false) {
    if(wfmid == 0){
    return this.http.post('/reRunContinueTest', JSON.stringify({
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
        "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
        "requestType": "RUN_TEST",
        "migrationSubType": commissionType,
        "migrationType": "migration",
        "userName": JSON.parse(sessionStorage.getItem("loginDetails")).userName,
        "runTestId": testId,
        "runType": type,
        "reRunScriptID": reRunScriptID,
        "prePostAuditFlag": prePostAuditFlag,
        "ovUpdate": ovUpdate,
        "skipScriptIds": skipScriptIds.join()
       
    }), { headers: this.headers });
  }
  else{
    return this.http.post('/reRunContinueWfmTest', JSON.stringify({
      "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
      "serviceToken": serviceToken,
      "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
      "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
      "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
      "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
      "requestType": "RUN_TEST",
      "migrationSubType": commissionType,
      "migrationType": "migration",
      "userName": JSON.parse(sessionStorage.getItem("loginDetails")).userName,
      "runTestId": testId,
      "id": wfmid,
      "runType": type,
      "reRunScriptID": reRunScriptID,
      "prePostAuditFlag": prePostAuditFlag,
      "ovUpdate": ovUpdate,
      "skipScriptIds": skipScriptIds
     
  }), { headers: this.headers });
  }
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

    getMigUseCases(ciqName, selectedNE, serviceToken, commissionType) {
      return this.http.post('/getMigUseCases', JSON.stringify({
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "ciqFileName": ciqName ? ciqName.ciqFileName : "",
        "migrationType": "migration",
        "migrationSubType": commissionType,
        "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
        "userName": JSON.parse(sessionStorage.getItem("loginDetails")).userName,
        "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
        "enbID": selectedNE // ? selectedNE.eNBId : null
      }), { headers: this.headers });
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

    getConnectionLog(serviceToken, commissionType) {
        return this.http.post('/getConnectionLog', JSON.stringify( {
	        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "migrationType":"migration",
            "migrationSubType":commissionType,
		    "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
        	"customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
			"programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
	      } ), { headers: this.headers } );
    }
   
/*     getUseCaseListData(ciqName,neDetails,commissionType,serviceToken){
        return this.http.post('/loadUsecase', JSON.stringify( {
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "ciqName": ciqName,
            "neDetails":neDetails,           
            "migrationType":"migration",
            "migrationSubType":commissionType,
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
          } ), { headers: this.headers } );
      } */

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
          "enbId": selectedNE, // ? selectedNE[0].eNBId : null, // Temporary until NE Mapping fix from UI
          // "enbId": selectedNE ? selectedNE.eNBId : null,
          "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
          "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName
        }

      }), { headers: this.headers });
    }

    downloadDiffFile(fileName,filePath, serviceToken) {
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
    viewOVFailureMessage(serviceToken, testId,smName,neName, commissionType, useCaseName) {
      return this.http.post('/messageInfoOV', JSON.stringify({
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "serviceToken": serviceToken,
          "testId":testId,
          "SMName":smName,
          "NEName":neName,
          "migrationType": "migration",
          "migrationSubType": commissionType,
          "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
          "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
          "useCaseName": useCaseName
         
      }), { headers: this.headers });
    }
    retryMilestoneUpdate(serviceToken, ovRetryData, commissionType, milestones = [], ovUpdate = false) {
      return this.http.post('/retryMilestoneUpdate', JSON.stringify({
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "serviceToken": serviceToken,
          "runTestId": ovRetryData ? ovRetryData.runTestId : "",
          "ciqName": ovRetryData ? ovRetryData.ciqName : "",
          "neName": ovRetryData ? ovRetryData.neName : "",
          "migrationType": "migration",
          "migrationSubType": commissionType,
          "ovUpdate": ovUpdate,
          "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
          "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
          "useCaseName": ovRetryData.useCaseName,
          "milestones": milestones
         
      }), { headers: this.headers });
    }
    retryFailedMilestoneUpdate(serviceToken, ovRetryData, commissionType, failedMilestone, ovUpdate = false) {
      return this.http.post('/retryFailedMilestoneUpdate', JSON.stringify({
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "serviceToken": serviceToken,
          "runTestId": ovRetryData ? ovRetryData.runTestId : "",
          "ciqName": ovRetryData ? ovRetryData.ciqName : "",
          "neName": ovRetryData ? ovRetryData.neName : "",
          "migrationType": "migration",
          "migrationSubType": commissionType,
          "ovUpdate": ovUpdate,
          "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
          "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
          "failedMilestone": failedMilestone,
          "useCaseName": ovRetryData.useCaseName
         
      }), { headers: this.headers });
    }
    uploadScriptFile(formData,serviceToken,useCase, script, smName, neName, runTestId, file, ciqName){
      let uploadScriptDetails = {
        'useCaseId': useCase.useCaseId,
        'useCaseName': useCase.useCaseName,
        'scriptName': script.scriptName,
        'scriptExeSeq': script.scriptExeSeq,
        'scriptId': script.scriptId,
        'smName': smName,
        'neName': neName,
        'runTestId': runTestId,
        'fileName': file.name,
        'sessionId': JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        'serviceToken': serviceToken,
        'migrationType': 'migration',
        'programId': JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        'programName': JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
        'ciqName': ciqName
      }
      formData.append('uploadScriptDetails', JSON.stringify(uploadScriptDetails));
      return this.http.post( '/uploadScript', formData);
  }
}

