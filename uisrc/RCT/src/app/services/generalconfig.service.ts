import { Injectable } from '@angular/core';
import { Http, Headers, Response } from '@angular/http';
//import { Observable } from 'rxjs/Observable';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { filter, map, mergeMap} from 'rxjs/operators';
//import 'rxjs/add/operator/map'

@Injectable({
  providedIn: 'root'
})
export class GeneralconfigService {

  headers: any;
  constructor(private http: Http) { 
    this.headers = new Headers( { "content-type": "application/json"});
  }


  getConfigDetails(serviceToken, migrationType) {
    return this.http.post('/getConfigDetails', JSON.stringify( {
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "serviceToken": serviceToken,
          "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
          "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
          "generalConfigType" : migrationType
       
        } ), { headers: this.headers } );
  }
  getNetworkTypeDetails(serviceToken){
    return this.http.post('/getNetworkTypeDetails', JSON.stringify( {
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,        
        "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId":JSON.parse(sessionStorage.selectedCustomerList).id       
      } ), { headers: this.headers } );
  }

  getNeVersionDetails(serviceToken){
    return this.http.post('/getNeVersionDetails', JSON.stringify( {
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,        
        "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId":JSON.parse(sessionStorage.selectedCustomerList).id       
      } ), { headers: this.headers } );
  }
  getCustomerDetails(serviceToken) {
    return this.http.post('/getCustomerList', JSON.stringify( {
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "serviceToken": serviceToken        
       
        } ), { headers: this.headers } );
	}
		/*
   * To update Config details
   * @param : inputEl,serviceToken
   * @retun : json Object
   */

   updateConfigWithID(lruformData,serviceToken, type, key){
       key.value = lruformData.dynamicParam;
       if(type == 'PROGRAM TEMPLATE'){
        return this.http.post('/programTemplateUpdate', JSON.stringify({
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          /* "programTemplateDetails": {
              "id": key.id,
              "value": lruformData.dynamicParam,
              "label": type
          }, */
          "programTemplateDetails": key,
          "serviceToken": serviceToken,
          "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
          "customerId": JSON.parse(sessionStorage.selectedCustomerList).id
      }), { headers: this.headers });
      } else if(key.inputType) {
         return this.http.post('/programTemplateUpdate', JSON.stringify({
           "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
           "programTemplateDetails": key,
           "serviceToken": serviceToken,
           "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
           "customerId": JSON.parse(sessionStorage.selectedCustomerList).id
         }), { headers: this.headers });
      } else if(type == 'S & R'){
        return this.http.post('/updateSchedulingTemplate', JSON.stringify({
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,        
          "schedulingTemplateDetails": key,
          "serviceToken": serviceToken,
          "customerName": JSON.parse(sessionStorage.selectedCustomerList).customerName,
          "customerId": JSON.parse(sessionStorage.selectedCustomerList).id
      }), { headers: this.headers });
       }
      
  }

  updateConfigWithoutID(lruformData,serviceToken, type){
    return this.http.post('/saveConfigDetails', JSON.stringify( {
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "type": type,
          "serviceToken": serviceToken,
          "configDetails":lruformData,
          "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
          "customerId":JSON.parse(sessionStorage.selectedCustomerList).id
        } ), { headers: this.headers } );
  }


  updateNetworkType(networkdetails,serviceToken){
    return this.http.post('/saveNetworkType', JSON.stringify( {
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,          
          "serviceToken": serviceToken,
          "nwTypeDetails":networkdetails
        } ), { headers: this.headers } );
  }
  updateNeVersion(neVersionDetails,serviceToken){
    return this.http.post('/saveNeVersion', JSON.stringify( {
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,          
          "serviceToken": serviceToken,
          "neVersionDetails":neVersionDetails
        } ), { headers: this.headers } );
  }
  deleteNetworkType(id,serviceToken){
    return this.http.post('/deleteNetworkType', JSON.stringify( {
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "serviceToken": serviceToken,
          "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
          "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
          "nwTypeId": id   
        } ), { headers: this.headers } );
  }
  deleteNeVersion(id, serviceToken){
    return this.http.post('/deleteNeVersionDetails', JSON.stringify( {
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "serviceToken": serviceToken,
          "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
          "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
          "id": id   
        } ), { headers: this.headers } );
  }
  addCustomer(customerDetails,serviceToken){
    return this.http.post('/saveCustomer', JSON.stringify( {
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "serviceToken": serviceToken,
          "customerDetails": customerDetails          
        } ), { headers: this.headers } );
  }
  saveCustomer(customerDetails,formdata,serviceToken){
    formdata.append('sessionId', JSON.parse(sessionStorage.getItem("loginDetails")).sessionId);
    formdata.append('serviceToken', serviceToken);
    formdata.append('fileName', "icon" ),   
    formdata.append("customerDetails",JSON.stringify(customerDetails));
    formdata.append("customerName",JSON.parse(sessionStorage.selectedCustomerList).customerName);
    formdata.append("customerId",JSON.parse(sessionStorage.selectedCustomerList).id);
    return this.http.post( '/addCustomer', formdata);
    
  }
  saveImage(formdata,serviceToken,custId,custName){
    formdata.append('sessionId', JSON.parse(sessionStorage.getItem("loginDetails")).sessionId);
    formdata.append('serviceToken', serviceToken);
    formdata.append('fileName', "icon" ),
    formdata.append("customerName",custName);
    formdata.append("customerId",custId);   
    return this.http.post( '/updateCustomerIcon', formdata);
  }

  deleteCustomerRow(id,serviceToken){
    return this.http.post('/deleteCustomerDetails', JSON.stringify( {
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "serviceToken": serviceToken,
          "id": id          
        } ), { headers: this.headers } );
  }
  deleteCustomer(id,serviceToken){
    return this.http.post('/deleteCustomer', JSON.stringify( {
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
          "serviceToken": serviceToken,
          "id": id          
        } ), { headers: this.headers } );
  }
}
