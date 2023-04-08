import { Injectable } from '@angular/core';
import { Http, Headers, Response,ResponseContentType } from '@angular/http';
//import { Observable } from 'rxjs/Observable';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { filter, map, mergeMap} from 'rxjs/operators';
//import 'rxjs/add/operator/map'

@Injectable({
  providedIn: 'root'
})
export class SystemmanagerconfigService {

  headers: any;
  constructor(private http: Http) { 
    this.headers = new Headers( { "content-type": "application/json"});
  }


  /*
   * create new user
   * @param : userDetails object, serviceToken
   * @retun : json Object
   */

  createLsm(networkConfigList, serviceToken,updateIp){
    return this.http.post('/saveNetworkConfigDetails', JSON.stringify( {
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "networkConfigDetails": networkConfigList,
        "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
        "updateIp":updateIp
      } ), { headers: this.headers } );
  }
  /*
   * get user list during page load
   * @param : serviceToken
   * @retun : json Object
   */

	getLSMDetails(searchStatus,searchCriteria,paginationDetails, serviceToken ){
		return this.http.post('/getNetworkConfigDetails', JSON.stringify( {
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "pagination":paginationDetails,
        "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
        "searchDetails": searchCriteria,
        "searchStatus": searchStatus
       
      } ), { headers: this.headers } );
	}

  /*
     * update row user
     * @param : deleteUser object, serviceToken
     * @retun : json Object
     */

   updateLsmDetail(networkDetails, serviceToken,updateIp){
    return this.http.post('/saveNetworkConfigDetails', JSON.stringify( {
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        "serviceToken": serviceToken,
        "networkConfigDetails": networkDetails,
        "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
        "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
         "updateIp":updateIp
      } ), { headers: this.headers } );
  }

    /*
     * delete row user
     * @param : deleteUser object, serviceToken
     * @retun : json Object
     */

    deleteUserDetails(id, serviceToken){
      return this.http.post('/deleteNetworkConfigDetails', JSON.stringify( {
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
            "serviceToken": serviceToken,
            "id": id,
            "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
            "customerId":JSON.parse(sessionStorage.selectedCustomerList).id
          } ), { headers: this.headers } );
    }

    uploadFile(formdata,serviceToken){
      formdata.append('sessionId', JSON.parse(sessionStorage.getItem("loginDetails")).sessionId);
      formdata.append('serviceToken', serviceToken);
      formdata.append('fileName', "networkConfigFile" ),      
      formdata.append("customerName",JSON.parse(sessionStorage.selectedCustomerList).customerName);
      formdata.append("customerId",JSON.parse(sessionStorage.selectedCustomerList).id);
      return this.http.post( '/importNetworkConfigDetails', formdata);
  }
  exportNetworkDetails(serviceToken){
    return this.http.post('/isZipAvilableForNwConfig', JSON.stringify( {
      "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
      "serviceToken": serviceToken,    
      "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
      "customerId":JSON.parse(sessionStorage.selectedCustomerList).id
    } ), { headers: this.headers } );
  }
  downloadFile(serviceToken, searchCriteria, searchStatus){
    return this.http.post('/exportNetworkConfigDetails',  {
      "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
      "serviceToken": serviceToken,    
      "customerName":JSON.parse(sessionStorage.selectedCustomerList).customerName,
      "customerId":JSON.parse(sessionStorage.selectedCustomerList).id,
      "searchDetails": searchCriteria,
      "searchStatus": searchStatus
    } , { responseType: ResponseContentType.Blob });
  }

  
}
