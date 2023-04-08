import { Injectable } from '@angular/core';
import { Http, Headers, Response, ResponseContentType } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map'

@Injectable()
export class UsecaseService {

    headers: any;
    constructor(private http: Http) {
        this.headers = new Headers({ "content-type": "application/json" });
    }

    /*
     * get Use Case during page load
     * @param : searchStatus, searchCriteria, paginationDetails, serviceToken
     * @retun : json Object
     */

    getAllUseCases(searchStatus, searchCriteria, paginationDetails, serviceToken, migrationType,subType) {
        return this.http.post('/loadUseCaseBuilder', JSON.stringify({
            "pagination": paginationDetails,
            "searchCriteria": searchCriteria,
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "searchStatus": searchStatus,
            "customerName": JSON.parse(sessionStorage.getItem("selectedCustomerList")).customerName,
            "customerId":JSON.parse(sessionStorage.getItem("selectedCustomerList")).id,
            "migrationType":migrationType == "preaudit" ? "premigration" : migrationType == "nestatus" ? "premigration": migrationType,
            "subType": subType ? subType : "",
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
        }), { headers: this.headers });
    }

    /*
   * To Add Use Case Details
   * @param : useCaseFormData, serviceToken
   * @retun : json Object
   */
    createUseCase(useCaseFormData, serviceToken,migrationType,subType) {
        return this.http.post('/createUseCaseBuilder', JSON.stringify({
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "customerName": JSON.parse(sessionStorage.getItem("selectedCustomerList")).customerName,
            "customerId":JSON.parse(sessionStorage.getItem("selectedCustomerList")).id,
            "migrationType":migrationType == "preaudit" ? "premigration" : migrationType == "nestatus" ? "premigration": migrationType,
            "subType": subType ? subType : "",
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
            "useCaseDetails": useCaseFormData

        }), { headers: this.headers });
    }

    /*
    * To delete Use Case
    * @param : rowId, serviceToken
    * @retun : json Object
    */

    deleteUseCaseDeta(rowId, serviceToken) {
        return this.http.post('/deleteUseCaseBuilder', JSON.stringify({
            "serviceToken": serviceToken,
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "id": rowId
        }), { headers: this.headers });
    }

    /*
   * To update Use Case Details
   * @param : useCaseFormData, serviceToken
   * @retun : json Object
   */
    updateUseCaseDetails(useCaseFormData, serviceToken,migrationType,subType) {
        return this.http.post('/updateUseCaseBuilder', JSON.stringify({
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "customerName": JSON.parse(sessionStorage.getItem("selectedCustomerList")).customerName,
            "customerId":JSON.parse(sessionStorage.getItem("selectedCustomerList")).id,
            "migrationType":migrationType == "preaudit" ? "premigration" : migrationType == "nestatus" ? "premigration": migrationType,
            "subType": subType ? subType : "",
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
            "useCaseDetails": useCaseFormData
        }), { headers: this.headers });
    }

    saveScriptContent(scriptData, serviceToken, migrationType, subType) {
        return this.http.post('/saveUseCaseViewScript', JSON.stringify({
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            //"action": "edit_use_case_script",
            "id": scriptData.id,
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
            "migrationType": migrationType == "preaudit" ? "premigration" : migrationType == "nestatus" ? "premigration": migrationType,
            "subType": subType ? subType : "",
            "useCaseId":scriptData.useCaseId,
            "scriptId":scriptData.scriptId,
            "scriptFileContent": scriptData.scriptFileContent //scriptData.scriptFileContent.replace(/(\r\n\t|\n|\r\t)/gm,"")   
        }), { headers: this.headers });
    }

    viewScriptContent(scriptData, serviceToken, migrationType, subType) {
        return this.http.post('/viewUseCaseScript', JSON.stringify({
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "useCaseId":scriptData.useCaseId,
            "scriptId":scriptData.scriptId,
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
            "migrationType": migrationType == "preaudit" ? "premigration" : migrationType == "nestatus" ? "premigration": migrationType,
            "subType": subType ? subType : "",
            "id": scriptData.id
        }), { headers: this.headers });
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
