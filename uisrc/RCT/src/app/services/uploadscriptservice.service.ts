import { Injectable } from '@angular/core';
import { Http, Headers, Response, ResponseContentType } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map'

@Injectable()
export class UploadscriptService {
    httpOptions: any;

    headers: any;

    constructor(private http: Http) {
        this.headers = new Headers({ "content-type": "application/json" });

    }

    /*
     * get uploadscript during page load
     * @param : paginationDetails, serviceToken
     * @retun : json Object
     */

    getUploadScript(searchStatus,searchCriteria,paginationDetails, serviceToken, migrationType, subType) {
        return this.http.post('/getUploadScript', JSON.stringify({
            "pagination": paginationDetails,
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "customerName": JSON.parse(sessionStorage.getItem("selectedCustomerList")).customerName,
            "customerId": JSON.parse(sessionStorage.getItem("selectedCustomerList")).id,
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
            "migrationType":migrationType == "preaudit" ? "premigration" : migrationType == "nestatus" ? "premigration": migrationType,
            "subType": subType ? subType : "",
            "searchStatus": searchStatus,
            "searchCriteria": searchCriteria
        }), { headers: this.headers });
    }
    /*
     * get user upload the . file
     * @param : 
     * @retun : 
     */


    uploadDetails(formdata, fileName, allowDuplicate, serviceToken, name, version, remarks, migrationType, subType, selectedScript, selUtility, selectedTerminal, utilUserName, utilPswd,prompt, argumentVal, sudoPassword="") {
        formdata.append('sessionId', JSON.parse(sessionStorage.getItem("loginDetails")).sessionId);
        formdata.append('serviceToken', serviceToken);
        formdata.append('customerName', JSON.parse(sessionStorage.getItem("selectedCustomerList")).customerName);
        formdata.append('customerId', JSON.parse(sessionStorage.getItem("selectedCustomerList")).id);
        formdata.append('fileName', fileName);
        formdata.append("fileType", "MIGRATION");
        formdata.append("uploadedBy", JSON.parse(sessionStorage.getItem("loginDetails")).userName);

        formdata.append("allowDuplicate", allowDuplicate);
        // formdata.append("nwType", nwType);
        formdata.append("lsmName", name);
        formdata.append("lsmVersion", version);
        formdata.append("remarks", remarks);
        formdata.append( "programId" , JSON.parse(sessionStorage.getItem("selectedProgram")).id);
        formdata.append("programName" , JSON.parse(sessionStorage.getItem("selectedProgram")).programName);
        formdata.append("migrationType", migrationType == "preaudit" ? "premigration" : migrationType == "nestatus" ? "premigration": migrationType);
        formdata.append("subType",subType ? subType : "",);
        formdata.append("state", "Uploaded");
        formdata.append("scriptType",selectedScript ? selectedScript : "");
        formdata.append("connectionLocation",selUtility ? selUtility : "");
        formdata.append("connectionTerminalUserName",selectedTerminal.termUsername ? selectedTerminal.termUsername : "");
        formdata.append("connectionTerminalPwd",selectedTerminal.termPassword ? selectedTerminal.termPassword : "");
        formdata.append("connectionTerminal",selectedTerminal.terminalName ? selectedTerminal.terminalName : "");
        formdata.append("connectionLocationUserName",utilUserName ? utilUserName : "");
        formdata.append("connectionLocationPwd",utilPswd ? utilPswd : "");
        formdata.append("sudoPassword", sudoPassword);
        formdata.append("prompt",prompt ? prompt : "");
        formdata.append("arguments", argumentVal ? argumentVal : "");
        return this.http.post('/saveUploadScript', formdata);
    }


	/*
   * To delete File Rule
   * @param : rowId, serviceToken
   * @retun : json Object
   */

    deleteUploadData(rowId, serviceToken, migrationType, subType) {
        return this.http.post('/deleteUploadScript', JSON.stringify({
            "serviceToken": serviceToken,
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "id": rowId,
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
            "migrationType":migrationType == "preaudit" ? "premigration" : migrationType == "nestatus" ? "premigration": migrationType,
            "subType": subType ? subType : "",
        }), { headers: this.headers });
    }

    updateUploadDetails(uploadScriptFormData, serviceToken, migrationType, subType) {
        return this.http.post('/updateUploadScript', JSON.stringify({
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "customerName": JSON.parse(sessionStorage.getItem("selectedCustomerList")).customerName,
            "customerId":JSON.parse(sessionStorage.getItem("selectedCustomerList")).id,
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
            "migrationType":migrationType == "preaudit" ? "premigration" : migrationType == "nestatus" ? "premigration": migrationType,
            "subType": subType ? subType : "",
            "state": "upload",
            "uploadScriptFormData": uploadScriptFormData
        }), { headers: this.headers });
    }

    viewScriptContent(scriptData, serviceToken, migrationType, subType) {
        return this.http.post('/viewScript', JSON.stringify({
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "filePath": scriptData.filePath,
            "fileName": scriptData.fileName,
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
            "migrationType":migrationType == "preaudit" ? "premigration" : migrationType == "nestatus" ? "premigration": migrationType,
            "subType": subType ? subType : "",
            "id": scriptData.id
        }), { headers: this.headers });
    }

    saveScriptContent(scriptData, serviceToken, migrationType, subType) {
        return this.http.post('/saveViewScript', JSON.stringify({
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            //"action": "edit_use_case_script",
            "fileName": scriptData.fileName,
            "id": scriptData.id,
            "filePath": scriptData.filePath,
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
            "migrationType":migrationType == "preaudit" ? "premigration" : migrationType == "nestatus" ? "premigration": migrationType,
            "subType": subType ? subType : "",
            "scriptFileContent": scriptData.scriptFileContent //scriptData.scriptFileContent.replace(/(\r\n\t|\n|\r\t)/gm,"")   
        }), { headers: this.headers });
    }

    searchUploadScript(searchStatus,searchCriteria,paginationDetails, serviceToken, migrationType, subType) {
        return this.http.post('/searchUploadScript', JSON.stringify({
            "pagination": paginationDetails,
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "customerName": JSON.parse(sessionStorage.getItem("selectedCustomerList")).customerName,
            "customerId": JSON.parse(sessionStorage.getItem("selectedCustomerList")).id,
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName,
            "migrationType":migrationType == "preaudit" ? "premigration" : migrationType == "nestatus" ? "premigration": migrationType,
            "subType": subType ? subType : "",
            "searchStatus": searchStatus,
            "searchCriteria": searchCriteria
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

