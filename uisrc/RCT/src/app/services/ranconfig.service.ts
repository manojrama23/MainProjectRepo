import { Injectable } from '@angular/core';
import { Http, Headers, Response, ResponseContentType } from '@angular/http';
//import { Observable } from 'rxjs/Observable';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { filter, map, mergeMap} from 'rxjs/operators';
//import 'rxjs/add/operator/map'

@Injectable()

export class RanconfigService {

  headers: any;
  constructor(private http: Http) { 
    this.headers = new Headers( { "content-type": "application/json"});
  }


  /*
   * get user upload the xl file
   * @param : 
   * @retun : 
   */

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
		
		getCiqSheetDetails(serviceToken,fileName){
	    return this.http.post('/getCiqSheetName', JSON.stringify( {
	        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
	        "serviceToken": serviceToken,
	        "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
					"customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
					"programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
					"programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
					"fileName":fileName
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
    getDeatilsByCiq(serviceToken, fileName, sheetName, subSheetName, paginationDetails, searchStatus, searchCriteria) {
        return this.http.post('/getCiqSheetDisply', JSON.stringify({
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "fileName": fileName,
            "sheetName": sheetName,
            "subSheetName": subSheetName ? subSheetName : "",
            "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
            "customerId": JSON.parse(sessionStorage.selectedCustomerList).id,
            "pagination": paginationDetails,
            "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
            "searchStatus" : searchStatus,
            "searchCriteria": searchCriteria
        }), { headers: this.headers });
    }

    getDeatilsByEnb(fileName, fileId, enbName, enbId, serviceToken){
	    return this.http.post('/getEnbInfo', JSON.stringify( {
	          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
		      "serviceToken": serviceToken,		      
		      "fileName":fileName,
		      "fileId":fileId,
		      "enbName":enbName,
		      "enbId":enbId,
		      "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
						"customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
						"programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
	      } ), { headers: this.headers } );
    }

    getDeatilsOfEnodeList( menuName, fileName, fileId, enbName, enbId, paginationDetails, serviceToken){
	    return this.http.post('/getEnbTableInfo', JSON.stringify( {
	          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
		      "serviceToken": serviceToken,		     
		      "menuName":menuName,
		      "fileName":fileName,
		      "fileId":fileId,
		      "enbName":enbName,
		      "enbId":enbId,
		      "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
        	  "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
					"pagination":paginationDetails,
					"programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
	      } ), { headers: this.headers } );
    }

    deleteENodeBDetails(ciqFileDetails, id, serviceToken){
	    return this.http.post('/deleteCiqRowDetails', JSON.stringify( {
	        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
	        "serviceToken": serviceToken,
					"id": id,					
	      	"ciqAuditDetail":ciqFileDetails,
	        "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
					"customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
					"programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
	      } ), { headers: this.headers } );
	  }
	  updateENodeBDetails(searchByCiqBlock, ciqDetails, serviceToken,ciqFileDetails,fileName){
	    if(searchByCiqBlock == true){
	    	return this.http.post('/updateCiqFileDetaiils', JSON.stringify( {
	        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
	        "serviceToken": serviceToken,
					"ciqDetails":ciqDetails,
					"ciqAuditDetail":ciqFileDetails,
					"fileName":fileName,
	        "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
					"customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
					"programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
	      } ), { headers: this.headers } );
	    }else{
	    	return this.http.post('/updateCiqFileDetaiilsEnbs', JSON.stringify( {
	        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
	        "serviceToken": serviceToken,
	        "eNodeBDetails":ciqDetails,
					"menuName":ciqDetails.menuName,
					"ciqAuditDetail":ciqFileDetails,
					"fileName":fileName,
	        "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
					"customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
					"programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
	      } ), { headers: this.headers } );
	    }
	  }
	  createENodeBDetails(searchByCiqBlock, ciqDetails, serviceToken,ciqFileDetails){
	    if(searchByCiqBlock == true){
	    	return this.http.post('/updateCiqFileDetaiils', JSON.stringify( {
	        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
	        "serviceToken": serviceToken,
					"ciqDetails":ciqDetails,
					"ciqAuditDetail":ciqFileDetails,
	        "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
					"customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
					"programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
	      } ), { headers: this.headers } );	
	    }else{
	    	return this.http.post('/createEnodeFileDetaiils', JSON.stringify( {
	        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
	        "serviceToken": serviceToken,
	        "eNodeBDetails":ciqDetails,
	        "menuName":ciqDetails.menuName,
	        "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
					"customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
					"programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
	      } ), { headers: this.headers } );
	    }
	    
	  }

	  generateCsvFile( searchByCiqBlock,generateValue, serviceToken,integrationType, supportCA){
			if(searchByCiqBlock == true){
	    return this.http.post('/generateCiqBasedFiles', JSON.stringify( {
	          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
		      "serviceToken": serviceToken,
		      "ciqName":generateValue.fileName,
		      "fileId":generateValue.fileId,
		      "enbName":generateValue.enbName,
			  "enbId":generateValue.enbId,
		      "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
						"customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
						"programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
	      } ), { headers: this.headers } );
    }else{
			return this.http.post('/generateCiqNeBasedFiles', JSON.stringify( {
				"sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
			"serviceToken": serviceToken,
			"ciqName":generateValue.fileName,
			"fileId":generateValue.fileId,
			"enbName":generateValue.enbName,
			"enbId":generateValue.enbId,
			"supportCA": supportCA,
			"customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
			"customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
			"programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
		"programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
		"integrationType": integrationType
		} ), { headers: this.headers } );
		}
}


validateFile( searchByCiqBlock,validateValue, serviceToken){
	if(searchByCiqBlock == true){
	return this.http.post('/validationCiqDetails', JSON.stringify( {
				"sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
			"serviceToken": serviceToken,
			"fileName":validateValue.fileName,			
			"customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
				"customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
				"programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
		"programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
		} ), { headers: this.headers } );
}else{
	return this.http.post('/validationEnbDetails', JSON.stringify( {
		"sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
	"serviceToken": serviceToken,
	"fileName":validateValue.fileName,	
	"enbName":validateValue.enbName,
	"enbId":validateValue.enbId,
	"customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
	"customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
	"programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
"programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
} ), { headers: this.headers } );
}
}



}