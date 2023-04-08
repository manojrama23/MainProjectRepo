import { Injectable } from '@angular/core';
import { Http, Headers, Response, ResponseContentType } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';

@Injectable()
export class RanatpService {
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
      "migrationSubType":"RANATP",
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

    uploadRunTestDetails(serviceToken, runTestFormDetails, formdata, commissionType,requestType) {
      if(requestType == "RUN_TEST"){
        /* return this.http.post('/runTest', JSON.stringify({
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "serviceToken": serviceToken,
          "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
          "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
          "runTestFormDetails": runTestFormDetails,
          "requestType":requestType,
          "migrationType":"postmigration",
          "migrationSubType":"RANATP",
          "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
          "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
        }), { headers: this.headers }); */

        let ranAtpRunTest = {
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "userName": JSON.parse(sessionStorage.getItem("loginDetails")).userName,
            "serviceToken": serviceToken,
            "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
            "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
            "runTestFormDetails": runTestFormDetails,
            "requestType": requestType,
            "migrationType": "postmigration",
            "migrationSubType": "RANATP",
            "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName
        }
        formdata.append("ranAtpRunTest", JSON.stringify(ranAtpRunTest));
        return this.http.post('/runTestRanAtp', formdata);

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
          "migrationSubType":"RANATP",
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
          "migrationType":"postmigration",
          "migrationSubType":"RANATP",
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
          "migrationSubType":"RANATP",
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

    getBandListData(ciqName, selectedNEItems, serviceToken){
        return this.http.post('/fetchBandName', JSON.stringify({
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "ciqName": ciqName,
            "eNBName": selectedNEItems.eNBName,
            "eNBId": selectedNEItems.eNBId,
            "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
            "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
            "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName
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


 /*    downloadResultFile(fileName,filePath, serviceToken) {
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

    } */


    getConnectionLog(serviceToken) {
        return this.http.post('/getConnectionLog', JSON.stringify( {
	        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "migrationType":"postmigration",
            "migrationSubType":"RANATP",
		    "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
        	"customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
			"programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
	      } ), { headers: this.headers } );
    }
/*     getUseCaseListData(ciqName,neDetails,serviceToken){
        return this.http.post('/loadUsecase', JSON.stringify( {
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "ciqName": ciqName,
            "neDetails":neDetails,           
            "migrationSubType":"RANATP",
            "migrationType":"postmigration",
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
          "enbId": selectedNE ? selectedNE.eNBId : null,
          "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
          "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName
        }

      }), { headers: this.headers });
    }

}

