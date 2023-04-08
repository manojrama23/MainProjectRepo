import { Injectable } from '@angular/core';
import { Http, Headers, Response, ResponseContentType } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map'

@Injectable()
export class CmdrulebuilderService {

    headers: any;
    constructor(private http: Http) {
        this.headers = new Headers({ "content-type": "application/json" });
    }

    /*
     * get File Rule during page load
     * @param : searchStatus, searchCriteria, paginationDetails, serviceToken
     * @retun : json Object
     */

    getAllCmdRules(migrationType, subType,searchStatus, searchCriteria, paginationDetails, serviceToken) {
        return this.http.post('/getCmdRuleBuilder', JSON.stringify({
            "pagination": paginationDetails,
            "searchCriteria": searchCriteria,
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "searchStatus": searchStatus,
            "migrationType":migrationType == "preaudit" ? "premigration" : migrationType == "nestatus" ? "premigration": migrationType,
            "subType": (subType && migrationType != 'premigration') ? subType :"",
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
    createCmdRule(migrationType, subType, fileRuleFormData, serviceToken) {
        return this.http.post('/createCmdRuleBuilder', JSON.stringify({
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "migrationType":migrationType == "preaudit" ? "premigration" : migrationType == "nestatus" ? "premigration": migrationType,
            "subType": subType ? subType :"",
            "customerName": JSON.parse(sessionStorage.getItem("selectedCustomerList")).customerName,
            "customerId":JSON.parse(sessionStorage.getItem("selectedCustomerList")).id,
            "cmdRuleDetail": fileRuleFormData,
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
        }), { headers: this.headers });
    }

	/*
   * To delete File Rule
   * @param : rowId, serviceToken
   * @retun : json Object
   */

    deleteCmdRuleDeta(rowId, serviceToken) {
        return this.http.post('/deleteCmdRuleBuilder', JSON.stringify({
            "serviceToken": serviceToken,
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "id": rowId,
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
        }), { headers: this.headers });
    }

   

    updateCmdRuleDetails(migrationType, subType,fileRuleFormData, serviceToken) {
        return this.http.post('/updateCmdRuleBuilder', JSON.stringify({
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "customerName": JSON.parse(sessionStorage.getItem("selectedCustomerList")).customerName,
            "customerId":JSON.parse(sessionStorage.getItem("selectedCustomerList")).id,
            "cmdRuleDetail": fileRuleFormData,
            "migrationType":migrationType == "preaudit" ? "premigration" : migrationType == "nestatus" ? "premigration": migrationType,
            "subType": subType ? subType :"",
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
        }), { headers: this.headers });
    }
    updateXmlCmdRuleDetails(migrationType, subType,xmlTableFormData, serviceToken) {
        return this.http.post('/updateXmlFileRule', JSON.stringify({
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "customerName": JSON.parse(sessionStorage.getItem("selectedCustomerList")).customerName,
            "customerId":JSON.parse(sessionStorage.getItem("selectedCustomerList")).id,
            "xmlRuleDetail": xmlTableFormData,
            "migrationType":migrationType == "preaudit" ? "premigration" : migrationType == "nestatus" ? "premigration": migrationType,
            "subType": subType ? subType : "",
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
        }), { headers: this.headers });
    }

    getAllXmlData(migrationType, subType,searchStatus, searchCriteria, paginationDetails, serviceToken) {
        return this.http.post('/loadXmlFileRule', JSON.stringify({
            "pagination": paginationDetails,
            "searchCriteria": searchCriteria,
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "searchStatus": searchStatus,
            "migrationType":migrationType == "preaudit" ? "premigration" : migrationType == "nestatus" ? "premigration": migrationType,
            "subType": subType ? subType : "",
            "customerName": JSON.parse(sessionStorage.getItem("selectedCustomerList")).customerName,
            "customerId":JSON.parse(sessionStorage.getItem("selectedCustomerList")).id,
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
        }), { headers: this.headers });
    }

    saveRootAndElementData(cmdRuleXmlCreateData,serviceToken,migrationType, subType) {
        return this.http.post('/createXmlFileRule',JSON.stringify({
            "serviceToken": serviceToken,
            "migrationType":migrationType == "preaudit" ? "premigration" :  migrationType,
            "subType": subType ? subType : "",
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "xmlRuleDetail":cmdRuleXmlCreateData,
            "customerName": JSON.parse(sessionStorage.getItem("selectedCustomerList")).customerName,
            "customerId":JSON.parse(sessionStorage.getItem("selectedCustomerList")).id,
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
        }),{ headers: this.headers });
    }

    deleteXmlCmdRow(rowId, serviceToken) {
        return this.http.post('/deleteXmlFileRule', JSON.stringify({
            "serviceToken": serviceToken,
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "id": rowId,
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
        }), { headers: this.headers });
    }

    getAllShellData(migrationType, subType,searchStatus, searchCriteria, paginationDetails, serviceToken) {
        return this.http.post('/getShellCmdRuleBuilder', JSON.stringify({
            "pagination": paginationDetails,
            "searchCriteria": searchCriteria,
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "searchStatus": searchStatus,
            "migrationType":migrationType == "preaudit" ? "premigration" :  migrationType,
            "subType": subType ? subType : "",
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
    createShellRule(migrationType, subType, fileRuleFormData, serviceToken) {
        return this.http.post('/createShellCmdRuleBuilder', JSON.stringify({
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "migrationType": migrationType == "preaudit" ? "premigration" :  migrationType,
            "subType": subType ? subType : "",
            "customerName": JSON.parse(sessionStorage.getItem("selectedCustomerList")).customerName,
            "customerId": JSON.parse(sessionStorage.getItem("selectedCustomerList")).id,
            "cmdRuleDetail": fileRuleFormData,
            "programId": JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName": JSON.parse(sessionStorage.getItem("selectedProgram")).programName
        }), { headers: this.headers });
    }

    updateShellCmdRuleDetails(migrationType, subType,fileRuleFormData, serviceToken) {
        return this.http.post('/updateShellCmdRuleBuilder', JSON.stringify({
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "customerName": JSON.parse(sessionStorage.getItem("selectedCustomerList")).customerName,
            "customerId":JSON.parse(sessionStorage.getItem("selectedCustomerList")).id,
            "cmdRuleDetail": fileRuleFormData,
            "migrationType":migrationType == "preaudit" ? "premigration" :  migrationType,
            "subType": subType ? subType :"",
            "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
            "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
        }), { headers: this.headers });
    }

    /*
   * To delete File Rule
   * @param : rowId, serviceToken
   * @retun : json Object
   */

  deleteShellRow(rowId, serviceToken) {
    return this.http.post('/deleteShellCmdRuleBuilder', JSON.stringify({
        "serviceToken": serviceToken,
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "id": rowId,
        "programId" : JSON.parse(sessionStorage.getItem("selectedProgram")).id,
        "programName" : JSON.parse(sessionStorage.getItem("selectedProgram")).programName
    }), { headers: this.headers });
}


}

