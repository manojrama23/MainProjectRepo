import { Component, Input, OnChanges, SimpleChange, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal, NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { AuthenticationService } from '../services/authentication.service';
import { ChangePassword } from './changePassword';
import { SharedService } from '../services/shared.service';
import { validator } from '../validation';
import * as $ from 'jquery';

@Component({
    selector: 'rct-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.scss'],
    providers: [AuthenticationService, NgbActiveModal]
})
export class LoginComponent implements OnInit {

    closeResult: string;
    auth: any = {};


    validationData: any = {
        "rules": {
            "mailId": {
                "required": true,
                // "pattern": /^[a-zA-Z0-9._-]+@[a-zA-Z0-9-]+\.[a-zA-Z.]{2,5}$/
                "pattern" : /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
            }
        },
        "messages": {
            "mailId": {
                "required": "Email Id is required",
                "pattern": "Please enter a valid Email Id"
            }
        }
    };


    // Following variables are used in change password model
    chngPwd: any = {};
    chngPwdValidationMessage: string = "";
    showValidationMessage: boolean = false;
    chngePwdModalRef: any; // Helps to close/open the model window
    reason: string = "";


    // Following variables are used to dispaly success, confirm and failure model(s)
    showModelMessage: boolean = false;
    modelData: any;

    constructor(

        private router: Router,
        private modalService: NgbModal,
        private activeModal: NgbActiveModal,
        private authenticationService: AuthenticationService,
        private sharedService: SharedService

    ) { }

    ngOnInit() {
        this.showValidationMessage = false;
    }

    /*
     * On click Login authenticate user
     * @param : null
     * @retun : null
     */

    authenticate() {
        let userGrp: any = {
            "Super Administrator": "/dashboard",
            "Administrator": "/dashboard",
            "Commission Engineer": "/dashboard"
        };

        this.authenticationService.login(this.auth.username, this.auth.password, this.sharedService.createServiceToken())
            .subscribe(
                data => {
                    let successData = data.json();
                    this.reason = successData.reason;

                    if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {

                        if (successData.validUser) {
                            this.sharedService.setUserLoggedIn(true);

                            sessionStorage.setItem('loginDetails', JSON.stringify({
                                "userName": successData.userName,
                                "userGroup": successData.userGroup,
                                "emailId":successData.emailId,
                                "sessionId": successData.sessionId,
                                "rctSnapShot": successData.rctSnapShot,
                                "validUser": successData.validUser,
                                "customerList": successData.customerList,
                                "programSubscription": successData.programNamesList,
                                "timeOut": isNaN(parseInt(successData.sessionTimeOut)) ? 3600 : parseInt(successData.sessionTimeOut) * 60  //Convert into seconds
                            }));
                            sessionStorage.setItem('selectedCustomerList', JSON.stringify({
                                "customerName": successData.customerName,
                                "id": successData.customerId
                            }));
                            //Select first program name as selected
                            sessionStorage.setItem('selectedProgram', successData.programNamesList.length > 0 ? JSON.stringify(successData.programNamesList[0]) : null);
                            

                            /* this.sharedService.isLoginState = true;
                            // this.sharedService.programSubscription = successData.programNamesList;

                            //Select first program name as selected
                            this.sharedService.selectedProgram = successData.programNamesList[0]; */
                            if (successData.userGroup == 'Default User') {
                                this.router.navigate(["/usermanagement"]);
                            }
                            // else if (successData.userGroup == 'Super Administrator') {
                            //     this.router.navigate(["/home"]);
                            // }
                            else {
                                if (successData.customerList.length > 0) {
                                    this.router.navigate(["/dashboard"]);
                                }
                                else {
                                    this.router.navigate(["/generalconfig"]);
                                }
                            }
                            /* if ((successData.userGroup == "Administrator") || (successData.userGroup == "Super Administrator")) {
                                this.router.navigate(["/home"]);
                            } else {
                                this.router.navigate([userGrp[successData.userGroup]]);
                            } */
                            // this.router.navigate( [userGrp[successData.userGroup]] );
                        } else {
                            $("#invalid_User").show();
                        }
                    }
                },
                error => {
                    //Please Comment while checkIn
                    //let successData = {"customerId":"1","customerName":"Verizon","sessionId":"7e088256","userGroup":"Administrator","userName":"admin","rctSnapShot":"v1.2.9","validUser":true,"customerList":[{"id":1,"customerName":"Verizon"},{"id":2,"customerName":"AT&T"},{"id":3,"customerName":"Sprint"}]};
                    /* let successData = {"rctSnapShot":"v1.2.0","userFullName":"Super Administrator","emailId":"marubathula.swetha@ltts.com","networkTypeId":null,"sessionId":"f552d660","userName":"superadmin","customerName":"","validUser":true,"programNamesList":[{"id":1,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"VZN-4G-LSM","programDescription":"LSM","status":"Active","creationDate":"2019-03-22T10:19:48.000+0000","createdBy":"superadmin"},{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"VZN-4G-VLSM","programDescription":"VLSM","status":"Active","creationDate":"2019-03-22T10:18:37.000+0000","createdBy":"superadmin"},{"id":3,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-MIMO","programDescription":"MIMO","status":"Active","creationDate":"2019-03-22T10:19:15.000+0000","createdBy":"superadmin"},{"id":4,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-CLWR_CONV","programDescription":"CLWR_CONV","status":"Active","creationDate":"2019-03-20T14:12:06.000+0000","createdBy":"superadmin"}],"customerId":1,"customerList":[{"id":2,"customerName":"Verizon","iconPath":"/customer/verizon_ 03282019_12_04_19_icon.png","status":"Active","customerShortName":"VZN","customerDetails":[{"id":1,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"VZN-4G-LSM","programDescription":"LSM","status":"Active","creationDate":"2019-03-22T10:19:48.000+0000","createdBy":"superadmin"},{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"VZN-4G-VLSM","programDescription":"VLSM","status":"Active","creationDate":"2019-03-22T10:18:37.000+0000","createdBy":"superadmin"}]},{"id":4,"customerName":"Sprint","iconPath":"/customer/sprint_ 03222019_15_49_01_icon.png","status":"Active","customerShortName":"SPT","customerDetails":[{"id":3,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-MIMO","programDescription":"MIMO","status":"Active","creationDate":"2019-03-22T10:19:15.000+0000","createdBy":"superadmin"},{"id":4,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-CLWR_CONV","programDescription":"CLWR_CONV","status":"Active","creationDate":"2019-03-20T14:12:06.000+0000","createdBy":"superadmin"}]}],"networkType":"","serviceToken":"87749","userGroup":"Super Administrator","sessionTimeOut":"30"};
                    // let successData = {"sessionId":"f552d660","serviceToken":"87749","rctSnapShot":"v1.2.0","userFullName":"Super Administrator","emailId":"marubathula.swetha@ltts.com","networkTypeId":null,"userName":"superadmin","customerName":"","validUser":true,"programNamesList":[],"customerId":1,"customerList":[{"id":2,"customerName":"Verizon","iconPath":"/customer/verizon_ 03282019_12_04_19_icon.png","status":"Active","customerShortName":"VZN","customerDetails":[{"id":1,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"VZN-4G-LSM","programDescription":"LSM","status":"Active","creationDate":"2019-03-22T10:19:48.000+0000","createdBy":"superadmin"},{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"VZN-4G-VLSM","programDescription":"VLSM","status":"Active","creationDate":"2019-03-22T10:18:37.000+0000","createdBy":"superadmin"}]},{"id":4,"customerName":"Sprint","iconPath":"/customer/sprint_ 03222019_15_49_01_icon.png","status":"Active","customerShortName":"SPT","customerDetails":[{"id":3,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-MIMO","programDescription":"MIMO","status":"Active","creationDate":"2019-03-22T10:19:15.000+0000","createdBy":"superadmin"},{"id":4,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-CLWR_CONV","programDescription":"CLWR_CONV","status":"Active","creationDate":"2019-03-20T14:12:06.000+0000","createdBy":"superadmin"}]}],"networkType":"","userGroup":"Super Administrator"};
                    this.sharedService.setUserLoggedIn(true);
                    sessionStorage.setItem('loginDetails', JSON.stringify({
                        "sessionId": "7e088256",
                        //"userGroup": "Administrator",
                        //"userGroup":"Super Administrator",
                        // "userGroup":"Commission Manager",
                        // "userGroup":"Commission Engineer",
                        //"userGroup":"Default User",
                        "userGroup": successData.userGroup,
                        "emailId":successData.emailId,
                        "userName": "admin",
                        "rctSnapShot": "v1.0.0",
                        "customerList": successData.customerList,
                        "validUser": true,
                        "programSubscription": successData.programNamesList,
                        "timeOut": isNaN(parseInt(successData.sessionTimeOut)) ? 3600 : parseInt(successData.sessionTimeOut) * 60  //Convert into seconds
                    }));
                    sessionStorage.setItem('selectedCustomerList', JSON.stringify({
                        "customerName": successData.customerName,
                        "id": successData.customerId
                    }));
                    //Select first program name as selected
                    sessionStorage.setItem('selectedProgram', successData.programNamesList.length > 0 ? JSON.stringify(successData.programNamesList[0]) : null);

                    // this.sharedService.isLoginState = true;

                    setTimeout(() => {
                        if (successData.userGroup == 'Default User') {
                            this.router.navigate(["/usermanagement"]);
                        }
                        // else if (successData.userGroup == 'Super Administrator') {
                        //     this.router.navigate(["/home"]);
                        // }
                        else {
                            if (successData.customerList.length > 0) {
                                this.router.navigate(["/dashboard"]);
                            }
                            else {
                                this.router.navigate(["/generalconfig"]);
                            }
                        }
                        //this.router.navigate( [userGrp["Project Manager"]] );
                    }, 0); */
                    //Please Comment while checkIn

                    //this.alertService.error(error);TODO : This need to implement
                });
    }

    /*
     * On click forgot password link open a modal popup
     * @param : content
     * @retun : null
     */


    openForgotPasswordModal(content) {

        this.chngePwdModalRef = this.modalService.open(content, { windowClass: 'password-modal', keyboard: false, backdrop: 'static', size: 'sm' });

        // To reset validation message(s) while opaning change password modal 
        this.chngPwdValidationMessage = "";

        validator.performValidation(event, this.validationData, "create");
    }

    /* validates current submitted form is valid and free from errors
     * @param : pass the event
     * @retun : boolean
     */

    isValidForm(event) {
        return ($(event.target).parents("form").find(".error-border").length == 0) ? true : false;
    }


    /*
     * On click save user password will be modified/updated
     * @param : null
     * @retun : json object
     */

    changePassword(event) {
        setTimeout(() => {
            if (this.isValidForm(event)) {
                let currentForm = event.target.parentNode.parentNode,
                    forgotPassword = {
                        "emailId": currentForm.querySelector("#mailId").value,
                    };

                this.authenticationService.forgotPassword(forgotPassword)
                    .subscribe(data => {
                        let jsonStatue = data.json().status;
                        if (jsonStatue == "SUCCESS") {
                            this.displayModel("Temporary password sent to your email address.", "successIcon");
                            this.chngePwdModalRef.close();
                            this.ngOnInit();
                        } else {
                            this.showValidationMessage = true;
                            this.chngPwdValidationMessage = data.json().reason;
                        }
                    }, error => {
                        //Please Comment while checkIn
                        /* let jsonStatue = "SUCCESS";
                          if(jsonStatue == "SUCCESS"){
                              this.displayModel("Your temporary password is sent to you email address.","successIcon");
                              this.chngePwdModalRef.close();
                              this.ngOnInit();
                          } else {
                              this.showValidationMessage = true;
                              this.chngPwdValidationMessage = "Invalid Email ID";
                         } */
                        //Please Comment while checkIn
                    });
            }
        });
    }

    /*
     * Used to dispaly the status messages like SUCCESS/FAILURE on view
     * @param : message, messageType (successIcon/failureIcon)
     * @retun : null
     */

    displayModel(message: string, messageType: string) {

        this.showModelMessage = true;
        this.modelData = {
            "message": message,
            "modelType": messageType
        };

        setTimeout(() => {
            this.showModelMessage = false;
        }, 10);
    }
}
