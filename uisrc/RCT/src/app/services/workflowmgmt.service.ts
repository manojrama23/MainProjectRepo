import { Injectable } from '@angular/core';
import { Http, Headers, Response, ResponseContentType } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map'
import { type } from 'jquery';

@Injectable()
export class WorkflowmgmtService {
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
    return this.http.post('/loadWfmrunTest', JSON.stringify({
      "pagination": paginationDetails,  
      "searchCriteria": searchCriteria ? searchCriteria : {},
      "searchStatus": searchStatus,
      "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
      "serviceToken": serviceToken,
      "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
      "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
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

    uploadRunTestDetails(serviceToken,premigData,ranAtpFlag, runTestFormDetails,commissionType,requestType,runTestFormDetailsPost, runTestFormDetailsNEGrow,generateAllSites,selectedNEIDs, fsuType,paginationDetails, integrationType,bulkNEData = null, supportCA = null, isRETUseCaseSelected = false,  formdata = null, runTestFormDetailsPreAudit = null, runTestFormDetailsNeStatus = null) {
      let runTestData = {
        "premigration":premigData?{
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "serviceToken": serviceToken,
          "ciqFileName": runTestFormDetails.ciqName,
          "ciqName": bulkNEData ? "" : runTestFormDetails.ciqName,
          "fileId": 265,
          "neDetails": runTestFormDetails.neDetails,
          //"enbId": runTestFormDetails.neDetails[0].neId,
          "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
          "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
          "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
          "pagination":paginationDetails,
          "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
          "fsuType":fsuType,
          "neIDs":selectedNEIDs,
          "fileType":"ALL",
          "generateAllSites":generateAllSites,
          "supportCA": supportCA,
          "remarks":"",
          "integrationType": integrationType
        }:null,
        "negrow": (runTestFormDetailsNEGrow.useCase && runTestFormDetailsNEGrow.useCase.length > 0) ?{
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "serviceToken": serviceToken,
          "userName": JSON.parse(sessionStorage.getItem("loginDetails")).userName,
          "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
          "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
          "runTestFormDetails": runTestFormDetailsNEGrow,
          "requestType": requestType,
          "migrationType": "premigration",
          "migrationSubType": "NEGrow",
          "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
          "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
        }:null,
        "migration":(runTestFormDetails.useCase && runTestFormDetails.useCase.length > 0) ?{
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "userName": JSON.parse(sessionStorage.getItem("loginDetails")).userName,
          "serviceToken": serviceToken,
          "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
          "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
          "runTestFormDetails": runTestFormDetails,
          "requestType":requestType,
          "migrationType":"migration",
          "migrationSubType":"precheck",
          "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
          "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
        }:null,
        "Nestatus":(runTestFormDetailsNeStatus.useCase && runTestFormDetailsNeStatus.useCase.length > 0) ?{
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "userName": JSON.parse(sessionStorage.getItem("loginDetails")).userName,
          "serviceToken": serviceToken,
          "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
          "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
          "runTestFormDetails": runTestFormDetailsNeStatus,
          "requestType":requestType,
          "migrationType":"premigration",
          "migrationSubType":"Nestatus",
          "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
          "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
        }:null,
        "preaudit":(runTestFormDetailsPreAudit.useCase && runTestFormDetailsPreAudit.useCase.length > 0) ?{
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "userName": JSON.parse(sessionStorage.getItem("loginDetails")).userName,
          "serviceToken": serviceToken,
          "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
          "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
          "runTestFormDetails": runTestFormDetailsPreAudit,
          "requestType":requestType,
          "migrationType":"premigration",
          "migrationSubType":"preaudit",
          "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
          "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
        }:null,
        "postmigration":(runTestFormDetailsPost.useCase && runTestFormDetailsPost.useCase.length > 0) ? {
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "userName": JSON.parse(sessionStorage.getItem("loginDetails")).userName,
          "serviceToken": serviceToken,
          "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
          "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
          "runTestFormDetails": runTestFormDetailsPost,
          "requestType":requestType,
          "migrationType":"postmigration",
          "migrationSubType":"AUDIT",
          "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
          "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
          "useCaseName": ranAtpFlag ? "Ran_Atp" :""
        } : null,
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "state":"normal",
        "generateAllSites":generateAllSites,
        "userName": JSON.parse(sessionStorage.getItem("loginDetails")).userName,
        "ciqName": bulkNEData ? "" : runTestFormDetails.ciqName,
        // "enbName": runTestFormDetails.neDetails[0].neName,
        // "enbId":    runTestFormDetails.neDetails[0].neId,
        "neDetails": runTestFormDetails.neDetails,
        "neIdData": bulkNEData ? bulkNEData.join(',') : null,
       // "lsmVersion": runTestFormDetails.lsmVersion,
       // "lsmName": runTestFormDetails.lsmName,
        //"lsmId":runTestFormDetails.lsmId,
        "serviceToken": serviceToken,
        "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
        "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
      }
      if(requestType == "RUN_TEST"){ 
        if(isRETUseCaseSelected) {
          // let retRunTestData = {
          //   "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          //   "userName": JSON.parse(sessionStorage.getItem("loginDetails")).userName,
          //   "serviceToken": serviceToken,
          //   "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
          //   "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
          //   "runTestFormDetails": runTestFormDetailsPost,
          //   "requestType": requestType,
          //   "migrationType": "postmigration",
          //   "migrationSubType": "AUDIT",
          //   "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
          //   "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName
          // }
          formdata.append("retRunTest", JSON.stringify(runTestData));
          return this.http.post('/runTestRETWFM', formdata);
        }
        else {
        
        let apiUrl = bulkNEData ? "/bulkCiqWithWFMTest" : "/workFlowManageRunTest"
        return this.http.post(apiUrl, JSON.stringify(runTestData), { headers: this.headers });
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


    independentRun(serviceToken,state, runTestFormDetails,runTestFormDetailsNeStatus,runTestFormDetailsPost, runTestFormDetailsPreAudit,runTestFormDetailsNEGrow,selectedNEIDs,paginationDetails, type, ranAtpFlagUseCase = false) {
      let apiName = "/runIndependent";
      if(!type) {
        apiName = "/continueWFM";
      }
      return this.http.post(apiName, JSON.stringify({
        "premigration":{
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "serviceToken": serviceToken,
          "ciqFileName": runTestFormDetails.ciqName,
          "ciqName": runTestFormDetails.ciqName,
          "fileId": 265,
          "neDetails": runTestFormDetails.neDetails,
          //"enbId": runTestFormDetails.neDetails[0].neId,
          "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
          "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
          "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
          "pagination":paginationDetails,
          "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
          "neVersion":"",
          "neIDs":selectedNEIDs,
          "fileType":"ALL",
          "remarks":""
        },
        "negrow": (runTestFormDetailsNEGrow.useCase && runTestFormDetailsNEGrow.useCase.length > 0) ?{
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "serviceToken": serviceToken,
          "userName": JSON.parse(sessionStorage.getItem("loginDetails")).userName,
          "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
          "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
          "runTestFormDetails": runTestFormDetailsNEGrow,
          "requestType": "RUN_TEST",
          "migrationType": "premigration",
          "migrationSubType": "NEGrow",
          "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
          "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
        }:null,
        "migration":(runTestFormDetails.useCase && runTestFormDetails.useCase.length > 0) ?{
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "userName": JSON.parse(sessionStorage.getItem("loginDetails")).userName,
          "serviceToken": serviceToken,
          "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
          "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
          "runTestFormDetails": runTestFormDetails,
          "requestType":"RUN_TEST",
          "migrationType":"migration",
          "migrationSubType":"precheck",
          "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
          "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
        }:null,
        "postmigration":(runTestFormDetailsPost.useCase && runTestFormDetailsPost.useCase.length > 0) ? {
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "userName": JSON.parse(sessionStorage.getItem("loginDetails")).userName,
          "serviceToken": serviceToken,
          "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
          "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
          "runTestFormDetails": runTestFormDetailsPost,
          "requestType":"RUN_TEST",
          "migrationType":"postmigration",
          "migrationSubType":"AUDIT",
          "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
          "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
          "useCaseName": ranAtpFlagUseCase ? "Ran_Atp" :""
        } : null,
        "Nestatus":(runTestFormDetailsNeStatus.useCase && runTestFormDetailsNeStatus.useCase.length > 0) ?{
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "userName": JSON.parse(sessionStorage.getItem("loginDetails")).userName,
          "serviceToken": serviceToken,
          "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
          "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
          "runTestFormDetails": runTestFormDetailsNeStatus,
          "requestType":"RUN_TEST",
          "migrationType":"premigration",
          "migrationSubType":"Nestatus",
          "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
          "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
        }:null,
        "preaudit":(runTestFormDetailsPreAudit.useCase && runTestFormDetailsPreAudit.useCase.length > 0) ? {
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "userName": JSON.parse(sessionStorage.getItem("loginDetails")).userName,
          "serviceToken": serviceToken,
          "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
          "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
          "runTestFormDetails": runTestFormDetailsPreAudit,
          "requestType":"RUN_TEST",
          "migrationType":"premigration",
          "migrationSubType":"preaudit",
          "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
          "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
        } : null,
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "state":state,
        "userName": JSON.parse(sessionStorage.getItem("loginDetails")).userName,
        "ciqName": runTestFormDetails.ciqName,
        // "enbName": runTestFormDetails.neDetails[0].neName,
        // "enbId":    runTestFormDetails.neDetails[0].neId,
        "neDetails": runTestFormDetails.neDetails,
        "testname":runTestFormDetails.testname,
        "id":runTestFormDetails.id,
       // "lsmVersion": runTestFormDetails.lsmVersion,
       // "lsmName": runTestFormDetails.lsmName,
        //"lsmId":runTestFormDetails.lsmId,
        "serviceToken": serviceToken,
        "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
        "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
      }), { headers: this.headers });
  }
    getFailureLogs(serviceToken, key,type) {
      return this.http.post('/geterrorLogs', JSON.stringify({
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        //"runTestId": id,
        "preErrorFile":key.preErrorFile,
        "neGrowErrorFile":key.neGrowErrorFile,
        "migErrorFile":key.migErrorFile,
        "postErrorFile":key.postErrorFile,
        "preAuditErrorFile":key.preAuditErrorFile,
        "neStatusErrorFile":key.neStatusErrorFile,
        "type":type,
        "wfmid":key.id,
        "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
        "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName
      }), { headers: this.headers });
    }
    /*
   * To delete File Rule
   * @param : rowId, serviceToken
   * @retun : json Object
   */

    deleteRunTestData(rowId, serviceToken, commissionType) {
      return this.http.post('/deleteWfmRunTestData', JSON.stringify({
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

  reRunContinueTest(serviceToken, type, testId, skipScriptIds, reRunScriptID, commissionType, id, resColumn) {
    return this.http.post('/reRunContinueWfmTest', JSON.stringify({
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
        "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
        "requestType": "RUN_TEST",
        "migrationSubType": resColumn == "NEGrow" ? resColumn : commissionType,
        "migrationType": resColumn == "NEGrow" ? "Premigration" : "migration",
        "userName": JSON.parse(sessionStorage.getItem("loginDetails")).userName,
        "runTestId": testId,
        "id": id,
        "runType": type,
        "reRunScriptID": reRunScriptID,
        "skipScriptIds": skipScriptIds
       
    }), { headers: this.headers });
  }
  generatedetails(serviceToken){
    return this.http.post('/generateScriptWFM', JSON.stringify({
    "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
    "serviceToken": serviceToken,
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

    getMigUseCases(ciqName, selectedNE, serviceToken, commissionType, isPostMig) {
      return this.http.post('/getMigUseCases', JSON.stringify({
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "ciqFileName": ciqName ? ciqName.ciqFileName : "",
        "migrationType": isPostMig == 'post' ? "postmigration" :isPostMig == 'pre' ? "Premigration" :isPostMig == 'nes' ? "Premigration" : "migration",
        "migrationSubType": isPostMig == 'post' ? "AUDIT": isPostMig == 'pre' ? "preaudit" : isPostMig == 'nes' ? "Nestatus" : commissionType,
        "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
        "userName": JSON.parse(sessionStorage.getItem("loginDetails")).userName,
        "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
        "enbID": selectedNE // ? selectedNE.eNBId : null
      }), { headers: this.headers });
    }
    getRowMigUseCases(ciqName, selectedNE, serviceToken, commissionType,type) {
      return this.http.post('/getMigUseCases', JSON.stringify({
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "ciqFileName": ciqName ? ciqName : "",
        "migrationType": type=="Migration"?"migration":"Premigration",
        "migrationSubType": commissionType,
        "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
        "userName": JSON.parse(sessionStorage.getItem("loginDetails")).userName,
        "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
        "enbID": selectedNE ? [selectedNE]: null
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

    getDeatilsByChecklist(serviceToken, paginationDetails, rowData) {
      return this.http.post('/getAllDetailsByChecklist', JSON.stringify({
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "checklistFileName": rowData.checklistFileName,
        "ciqFileName": rowData.ciqName,
        "enodeName": rowData.enodeName,
        "sheetName": rowData.sheetName,
        "runTestId": rowData.id,
        "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
        "pagination": paginationDetails,
        "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName
      }), { headers: this.headers });
    }

    updateCheckList(serviceToken, rowData, checklistTableData) {
      return this.http.post('/updateCheckListFileDetails', JSON.stringify({
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "checklistFileName": rowData.checklistFileName,
        "ciqFileName": rowData.ciqName,
        "enodeName": rowData.enodeName,
        "sheetName": rowData.sheetName,
        "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
        "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
        "checklistTableData": checklistTableData
      }), { headers: this.headers });
    }

    getRunningLogs(serviceToken, id) {
      return this.http.post('/getRunningLogs', JSON.stringify({
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "runTestId": id,
        "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
        "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName
      }), { headers: this.headers });
    }

    getResultLogs(serviceToken, id) {
      return this.http.post('/getneoutputlog', JSON.stringify({
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "runTestId": id,
        "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
        "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName
      }), { headers: this.headers });
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

    downloadFileRow(serviceToken, key) {
      return this.http.post('/downloadLogsWFM',  {
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,            
          "serviceToken":serviceToken,
          "wfid":key.id,
          "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
          "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
          "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
          "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName 
        } ,{ responseType: ResponseContentType.Blob } );

  }
    downloadResultFile(key, serviceToken) {
      return this.http.post('/downloadFile',  {
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,            
        "serviceToken":serviceToken,
        "ciqName": key.ciqName,
        "enbName": key.neName,
        "enbId": key.enbId,
        "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
        "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
        "premigration":{
        "fileNamePre": key.fileNamePre,
        "filePathPre": key.filePathPre,
        "commPath":key.commPath,
        "commZipName":key.commZipName,
        "envPath":key.envPath,
        "csvPath":key.csvPath,
        "csvZipName":key.csvZipName,
        "envZipName":key.envZipName
        },
        "migration": null,
        "postmigration": null,
        "negrow":null
        } ,{ responseType: ResponseContentType.Blob } );

  }

  downloadReports(serviceToken, wfmId) {
    return this.http.post('/downloadReportsWFM', {
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,            
        "serviceToken":serviceToken,
        "wfid":wfmId,
        "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
        "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName 
      } ,{ responseType: ResponseContentType.Blob } );

}
    getConnectionLog(serviceToken, commissionType) {
      return this.http.post('/getConnectionLog', JSON.stringify({
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "migrationType": "migration",
        "migrationSubType": commissionType,
        "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
        "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName
      }), { headers: this.headers });
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
            "generateScriptPath": filepath
            //"ranatpFilePath":ranatpFilePath
          } ,{ responseType: ResponseContentType.Blob } );

    }
    downloadGenScriptsRow(key,type, serviceToken,ranatpFilePath) {
      
      return this.http.post('/downloadLoadFiles',  {
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,            
          "serviceToken":serviceToken,
          "ciqName": key.ciqName,
          "enbName": key.neName,
          "enbId": key.enbId,
          "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
          "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
          "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
          "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
          "fileNamePre": key.fileNamePre,
          "filePathPre": key.filePathPre,
          "commPath":key.commPath,
          "commZipName":key.commZipName,
          "envPath":key.envPath,
          "csvPath":key.csvPath,
          "csvZipName":key.csvZipName,
          "envZipName":key.envZipName,
          
          "migration": (type != 'PreMigration')?{
            "filePath": key.migrationRunTestModel ? key.migrationRunTestModel.generateScriptPath : null
          }:null,

          "postmigration": (type != 'PreMigration')?{
            "filePath": key.postMigrationRunTestModel ? key.postMigrationRunTestModel.generateScriptPath : null
          }:null,
          "ranatp": (type != 'PreMigration')?{
            "filePath": (ranatpFilePath != '')? ranatpFilePath :null
          }:null,
          "negrow": (type != 'PreMigration')?{
            "filePath": key.negrowRunTestModel ? key.negrowRunTestModel.generateScriptPath : null
          }:null,
          "preaudit": (type != 'PreMigration')?{
            "filePath": key.preAuditMigrationRunTestModel ? key.preAuditMigrationRunTestModel.generateScriptPath : null
          }:null
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

    stopWFM(serviceToken, testRowDetails) {
      return this.http.post('/stopWFM', JSON.stringify({
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
        "userName": JSON.parse(sessionStorage.getItem("loginDetails")).userName,
        "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
        "wfmid" : testRowDetails.id,
        "neGrowStatus" : testRowDetails.neGrowStatus,
        "migStatus" : testRowDetails.migStatus,
        "postMigStatus" : testRowDetails.postMigStatus,
        "preAuditStatus" : testRowDetails.preAuditStatus,
        "neStatus":testRowDetails.neStatus

      }), { headers: this.headers });
    }

    continueRowRun(serviceToken, state, runTestFormDetails, runTestFormDetailsPost, runTestFormDetailsPreAudit,runTestFormDetailsNeStatus,runTestFormDetailsNEGrow, selectedNEIDs, paginationDetails) {
      console.log("hii")
      return this.http.post('/continueWFM', JSON.stringify({
        "premigration": (runTestFormDetailsNEGrow.useCase.length == 0 && runTestFormDetails.useCase.length == 0 && runTestFormDetailsPost.useCase.length == 0) ? {
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "serviceToken": serviceToken,
          "ciqFileName": runTestFormDetails.ciqName,
          "ciqName": runTestFormDetails.ciqName,
          "fileId": 265,
          "neDetails": runTestFormDetails.neDetails,
          //"enbId": runTestFormDetails.neDetails[0].neId,
          "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
          "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
          "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
          "pagination": paginationDetails,
          "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
          "neVersion": "",
          "neIDs": selectedNEIDs,
          "fileType": "ALL",
          "remarks": ""
        } : null,
        "negrow": (runTestFormDetailsNEGrow.useCase && runTestFormDetailsNEGrow.useCase.length > 0) ? {
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "serviceToken": serviceToken,
          "userName": JSON.parse(sessionStorage.getItem("loginDetails")).userName,
          "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
          "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
          "runTestFormDetails": runTestFormDetailsNEGrow,
          "requestType": "RUN_TEST",
          "migrationType": "premigration",
          "migrationSubType": "NEGrow",
          "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
          "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName
        } : null,
        "migration": (runTestFormDetails.useCase && runTestFormDetails.useCase.length > 0) ? {
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "userName": JSON.parse(sessionStorage.getItem("loginDetails")).userName,
          "serviceToken": serviceToken,
          "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
          "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
          "runTestFormDetails": runTestFormDetails,
          "requestType": "RUN_TEST",
          "migrationType": "migration",
          "migrationSubType": "precheck",
          "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
          "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName
        } : null,
        "postmigration": (runTestFormDetailsPost.useCase && runTestFormDetailsPost.useCase.length > 0) ? {
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "userName": JSON.parse(sessionStorage.getItem("loginDetails")).userName,
          "serviceToken": serviceToken,
          "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
          "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
          "runTestFormDetails": runTestFormDetailsPost,
          "requestType": "RUN_TEST",
          "migrationType": "postmigration",
          "migrationSubType": "AUDIT",
          "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
          "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName
        } : null,
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "state": state,
        "userName": JSON.parse(sessionStorage.getItem("loginDetails")).userName,
        "ciqName": runTestFormDetails.ciqName,
        // "enbName": runTestFormDetails.neDetails[0].neName,
        // "enbId":    runTestFormDetails.neDetails[0].neId,
        "neDetails": runTestFormDetails.neDetails,
        "testname": runTestFormDetails.testname,
        "id": runTestFormDetails.id,
        // "lsmVersion": runTestFormDetails.lsmVersion,
        // "lsmName": runTestFormDetails.lsmName,
        //"lsmId":runTestFormDetails.lsmId,
        "serviceToken": serviceToken,
        "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
        "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName
      }), { headers: this.headers });
    }
    getAuditPassFailReport(rowDetails, serviceToken) {
      return this.http.post('/getAudit4GPassFailSummaryReport', JSON.stringify({
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "runTestId": rowDetails ? rowDetails.id : null,
        "neName": rowDetails ? rowDetails.neName : "",
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
        "runTestId": rowDetails ? rowDetails.id : null,
        "neName": rowDetails ? rowDetails.neName : "",
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
        "wfmKey": true
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
    viewSiteReportDetailsById(serviceToken, siteDataId, ciqName, enbDetails) {
      let apiUrl = siteDataId ? "/getSiteDetailsById" : "/getSiteReportInputDetails"

      return this.http.post(apiUrl, JSON.stringify({
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "neDetails": enbDetails,
        "siteDataId": siteDataId,
        "ciqName": ciqName,
        // "neDetails":[{"enbName":"061452_CONCORD_2_NH_HUB","enbId":"61452"}],
        "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
        "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName
      }), { headers: this.headers });
    }
    exportSiteReportDetails(reportData, serviceToken, ciqName, selectedTableRow) {
      return this.http.post('/exportSiteReportDetails', {
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "reportDetails": reportData,
        "ciqName": ciqName,
        "siteId": selectedTableRow ? selectedTableRow.siteId : null,
        "workFlowId": selectedTableRow ? selectedTableRow.workFlowId : null,
        "customerName": JSON.parse(sessionStorage.getItem("selectedCustomerList")).customerName,
        "customerId": JSON.parse(sessionStorage.getItem("selectedCustomerList")).id,
        "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName
      }, { responseType: ResponseContentType.Blob });
    }
    getHistorySiteDetails(serviceToken, enbId) {
      return this.http.post('/getHistorySiteDetails', JSON.stringify({
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "neId": enbId,
        "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
        "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName
      }), { headers: this.headers });
    }

    downloadSiteReportFile(key, serviceToken) {
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

    saveSiteReportDetails(reportData, serviceToken, ciqName, selectedTableRow) {
      return this.http.post('/saveSiteReportDetails', JSON.stringify({
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "reportDetails": reportData,
        "ciqName": ciqName,
        "siteId": selectedTableRow ? selectedTableRow.siteId : null,
        "workFlowId": selectedTableRow ? selectedTableRow.workFlowId : null,
        "customerName": JSON.parse(sessionStorage.getItem("selectedCustomerList")).customerName,
        "customerId": JSON.parse(sessionStorage.getItem("selectedCustomerList")).id,
        "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName
      }), { headers: this.headers });
    }

    getUserNameList(serviceToken) {
      return this.http.post('/getUserNameList', JSON.stringify({
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
        "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName
      }), { headers: this.headers });
    }

    getInProgressSiteDetails(serviceToken, userNameList, fromDate, toDate, sityTypeRadio) {
      let apiUrl = sityTypeRadio == 'duoExec' ? 'getDuoExecErrorNeList' : '/getInProgressNeData';
      return this.http.post(apiUrl, JSON.stringify({
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "userNameList": userNameList,
        "toDate":toDate, 
        "fromDate":fromDate,
        "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
        "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName
      }), { headers: this.headers });
    }

    stopBulkNeData(serviceToken, siteList) {
      return this.http.post('/stopBulkNeData', JSON.stringify({
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "neListMap": siteList,
        "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
        "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName
      }), { headers: this.headers });
    }
    getRadioUnitListData(ciqName, selectedNEItems, serviceToken) {
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
    viewOVFailureMessage(serviceToken, testId, smName, neName, ovRetryData) {
      return this.http.post('/messageInfoOV', JSON.stringify({
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "serviceToken": serviceToken,
          "testId":testId,
          "SMName":smName,
          "NEName":neName,
          "migrationType": ovRetryData.migType == "migration" ? "migration" : ovRetryData.migType == "postMigration" ? "postmigration" : "",
          "migrationSubType": ovRetryData.migType == "migration" ? "precheck" : ovRetryData.migType == "postMigration" ? "AUDIT" : "",
          "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
          "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
          "useCaseName": ovRetryData.useCaseName
         
      }), { headers: this.headers });
    }
    viewOVFailureMessageSiteReport(serviceToken, testId, smName, neName, ovRetryData) {
      return this.http.post('/siteReportInfo', JSON.stringify({
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "serviceToken": serviceToken,
          "testId":testId,
          "NEName":neName,
          "ciqName": ovRetryData.ciqName,
          "migrationType": "",
          "migrationSubType": "",
          "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
          "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName         
      }), { headers: this.headers });
    }
    retryMilestoneUpdateSiteReport(serviceToken, ovRetryData, milestones = [], ovUpdate = false) {
      return this.http.post('/ovToUploadSiteReport', JSON.stringify({
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "serviceToken": serviceToken,
          "workFlowId":ovRetryData ? ovRetryData.workFlowId : "",
          "runTestId": ovRetryData ? ovRetryData.runTestId : "",
          "ciqName": ovRetryData ? ovRetryData.ciqName : "",
          "neName": ovRetryData ? ovRetryData.neName : "",
          "migrationType": "",
          "migrationSubType": "",
          "ovUpdate": ovUpdate,
          "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
          "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
          "useCaseName": ovRetryData.useCaseName,
          "milestones": milestones
      }), { headers: this.headers });
    }
    retryMilestoneUpdate(serviceToken, ovRetryData, milestones = [], ovUpdate = false) {
      return this.http.post('/retryMilestoneUpdate', JSON.stringify({
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "serviceToken": serviceToken,
          "runTestId": ovRetryData ? ovRetryData.runTestId : "",
          "ciqName": ovRetryData ? ovRetryData.ciqName : "",
          "neName": ovRetryData ? ovRetryData.neName : "",
          "migrationType": ovRetryData.migType == "migration" ? "migration" : ovRetryData.migType == "postMigration" ? "postmigration" : "",
          "migrationSubType": ovRetryData.migType == "migration" ? "precheck" : ovRetryData.migType == "postMigration" ? "AUDIT" : "",
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
          "migrationType": ovRetryData.migType == "migration" ? "migration" : ovRetryData.migType == "postMigration" ? "postmigration" : "",
          "migrationSubType": ovRetryData.migType == "migration" ? "precheck" : ovRetryData.migType == "postMigration" ? "AUDIT" : "",
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
  postChangeStatus(serviceToken,criteria) {
    return this.http.post('/forceComplete', JSON.stringify({
      "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
      "serviceToken": serviceToken,
      "runTestId": criteria.id,
      "postMigStatus":criteria.status,
      "remarks":criteria.comments,
      "wfmId":criteria.wfmid,
      "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
      "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
      "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
      "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName
         
    }), { headers: this.headers });
  }
  uploadSiteReportToOV(serviceToken, data) {
    return this.http.post('/reportUploadtoOV', JSON.stringify({
      "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
      "serviceToken": serviceToken,
      "runTestId": data.siteReportId,
      "neName":data.neName,
      "workFlowId":data.id,
      "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
      "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName
         
    }), { headers: this.headers });
  }
}


