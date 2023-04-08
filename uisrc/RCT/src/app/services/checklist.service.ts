import { Injectable } from '@angular/core';
import { Http, Headers, Response, ResponseContentType } from '@angular/http';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { filter, map, mergeMap } from 'rxjs/operators';

@Injectable()
export class ChecklistService {
    headers: any;
    constructor(private http: Http) {
        this.headers = new Headers({ "content-type": "application/json" });
	}
	




    getCheckListData(serviceToken,fromDate,toDate, searchStatus) {
        return this.http.post('/getCheckListData', JSON.stringify({
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
			"customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
			"programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
			"programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
			"searchStatus":searchStatus,
			"fromDate":fromDate,
			"toDate":toDate
        }), { headers: this.headers });
    }
    getChecklistListData(serviceToken){
	    return this.http.post('/getChecklistListData', JSON.stringify( {
	        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
	        "serviceToken": serviceToken,
	        "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
			"customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
			"programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
			"programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
			"searchStatus":"load",
			"fromDate":null,
			"toDate":null
	      } ), { headers: this.headers } );
		}
		
		getChecklistSheetDetails(serviceToken,fileName,checkListFileName,ciqFileName){
	    return this.http.post('/getChecklistSheetDetails', JSON.stringify( {
	        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
	        "serviceToken": serviceToken,
	        "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
			"customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
			"programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
			"programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
			"fileName":fileName,			
			"checkListFileName":checkListFileName,
			"ciqFileName":ciqFileName
	      } ), { headers: this.headers } );
	}
	

	getDeatilsByChecklist(serviceToken,fileName,sheetName,paginationDetails,checkListFileName,ciqFileName){
	    return this.http.post('/getDeatilsByChecklist', JSON.stringify( {
	        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
		    "serviceToken": serviceToken,		     
			"fileName":fileName,	    
			"checkListFileName":checkListFileName,
			"ciqFileName":ciqFileName,
			"sheetName":sheetName,
		    "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
        	"customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
			"pagination":paginationDetails,
			"programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        	"programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
	      } ), { headers: this.headers } );
    }

    

   

    deleteChecklistDetails(fileName, id, serviceToken,checkListFileName,ciqFileName){
	    return this.http.post('/deleteChecklistRowDetails', JSON.stringify( {
	        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
	        "serviceToken": serviceToken,
	        "id": id,
			"fileName":fileName,
			"checkListFileName":checkListFileName,
			"ciqFileName":ciqFileName,
	        "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
			"customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
			"programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        	"programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
	      } ), { headers: this.headers } );
	  }
	 /*  updateChecklistDetails(searchByChecklistBlock, checklistDetails, serviceToken){
	  
	    	return this.http.post('/updateChecklistFileDetails', JSON.stringify( {
	        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
	        "serviceToken": serviceToken,
	        "checklistDetails":checklistDetails,
	        "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
					"customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
					"programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
	      } ), { headers: this.headers } );
	    
	  }
	  createChecklistDetails(searchByChecklistBlock, checklistDetails, serviceToken){
	
	    	return this.http.post('/createChecklistFileDetails', JSON.stringify( {
	        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
	        "serviceToken": serviceToken,
	        "checklistDetails":checklistDetails,
	        "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
					"customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
					"programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
		  } ), { headers: this.headers } );	 */
		  
    saveCheckListFileDetaiils(searchByChecklistBlock, checklistDetails, serviceToken, checkListFileName, ciqFileName) {
        return this.http.post('/saveCheckListFileDetaiils', JSON.stringify({
            "searchByChecklistBlock": searchByChecklistBlock,
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "checklistDetails": checklistDetails,
            "checkListFileName": checkListFileName,
            "ciqFileName": ciqFileName,
            "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
            "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
            "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName
        }), { headers: this.headers });
    }

    getChecklistScriptDetails(serviceToken, id, checkListFileName, ciqFileName, sheetName) {
        return this.http.post('/getCheckListBasedScriptExecutionDetails', JSON.stringify({
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "stepIndex": id,
            "checkListFileName": checkListFileName,
            "ciqFileName": ciqFileName,
            "sheetName" : sheetName,
            "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
            "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
            "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName
        }), { headers: this.headers });
    }

    saveChecklistScriptDetails(serviceToken, id, checkListFileName, ciqFileName, scriptDetails, sheetName, configType) {
        return this.http.post('/saveCheckListBasedScriptExecutionDetails', JSON.stringify({
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "stepIndex": id,
            "checkListFileName": checkListFileName,
            "ciqFileName": ciqFileName,
            "sheetName" : sheetName,
            "configType" : configType,
            "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
            "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
            "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
            "scriptList": scriptDetails
        }), { headers: this.headers });
    }

}
