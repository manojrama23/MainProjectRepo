
import { Injectable } from '@angular/core';
import { Http, Headers, Response, ResponseContentType } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map'

@Injectable()
export class FilerulebuilderService {

    headers: any;
    constructor(private http: Http) {
        this.headers = new Headers({ "content-type": "application/json" });
    }

    /*
     * get File Rule during page load
     * @param : searchStatus, searchCriteria, paginationDetails, serviceToken
     * @retun : json Object
     */

    getAllFileRules(migrationType, subType,searchStatus, searchCriteria, paginationDetails, serviceToken) {
        return this.http.post('/getFileRuleBuilder', JSON.stringify({
            "pagination": paginationDetails,
            "searchCriteria": searchCriteria,
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "searchStatus": searchStatus,
            "migrationType":migrationType,
            "subType": subType ? subType :"",
            "customerName": JSON.parse(sessionStorage.getItem("selectedCustomerList")).customerName,
            "customerId":JSON.parse(sessionStorage.getItem("selectedCustomerList")).id,
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
             "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName     
        }), { headers: this.headers });
    }

	/*
   * To Add File Rule Details
   * @param : fileRuleFormData, serviceToken
   * @retun : json Object
   */
  createFileRule(migrationType, subType, fileRuleFormData, serviceToken) {
        return this.http.post('/createFileRuleBuilder', JSON.stringify({
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "migrationType":migrationType,
            "subType": subType ? subType :"",
            "customerName": JSON.parse(sessionStorage.getItem("selectedCustomerList")).customerName,
            "customerId":JSON.parse(sessionStorage.getItem("selectedCustomerList")).id,
            "fileRuleDetail": fileRuleFormData,
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
             "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName     
        }), { headers: this.headers });
    }

	/*
   * To delete File Rule
   * @param : rowId, serviceToken
   * @retun : json Object
   */

    deleteFileRuleDeta(rowId, serviceToken) {
        return this.http.post('/deleteFileRuleBuilder', JSON.stringify({
            "serviceToken": serviceToken,
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "id": rowId,
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
             "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName     
        }), { headers: this.headers });
    }

    updateFileRuleDetails(migrationType, subType,fileRuleFormData, serviceToken) {
        return this.http.post('/updateFileRuleBuilder', JSON.stringify({
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "customerName": JSON.parse(sessionStorage.getItem("selectedCustomerList")).customerName,
            "customerId":JSON.parse(sessionStorage.getItem("selectedCustomerList")).id,
            "fileRuleDetail": fileRuleFormData,
            "migrationType":migrationType,
            "subType": subType ? subType :"",
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
             "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName     
        }), { headers: this.headers });
    }


}

