import { ViewContainerRef, Input, ElementRef, Renderer, Component, OnInit, ViewChild, HostListener } from '@angular/core';
import { Router} from '@angular/router';
import { NgbModal, ModalDismissReasons} from '@ng-bootstrap/ng-bootstrap';
import { UserManagementService } from '../services/user-management.service';
import { SharedService } from '../services/shared.service';
//import { Observable } from 'rxjs/Observable';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { filter, map, mergeMap} from 'rxjs/operators';
import { validator } from '../validation';
import {platformBrowserDynamic} from '@angular/platform-browser-dynamic';
//import { IMultiSelectOption, IMultiSelectSettings, IMultiSelectTexts} from 'angular-2-dropdown-multiselect';
import * as _ from 'underscore';
import * as $ from 'jquery';
@Component({
  selector: 'rct-usermanagement',
  templateUrl: './usermanagement.component.html',
  styleUrls: ['./usermanagement.component.scss'],
  providers: [UserManagementService]
})
  export class UsermanagementComponent implements OnInit {
  dirty :boolean = false; 
  showLoader:boolean = true;
  tableData:any;
  userRoleList:any;
  closeResult:string;
  privilegeSetting : object;
  tableShowHide :boolean = false;
  noDataVisibility :boolean = false;
  formWidth:any;
  scrollLeft:any;
  showModelMessage: boolean = false;
  createForm: boolean = true;
  messageType: any;
  checkFormEdit: boolean = true;
  modelData :any;
  editableFormArray = [];
  sessionExpiredModalBlock : any; // Helps to close/open the model window
  successModalBlock : any;
  message : any;
  options :any;
  /*myOptions: IMultiSelectOption[];
  myOptionsAirline: IMultiSelectOption[];
  mySettings: IMultiSelectSettings;
  myTexts: IMultiSelectTexts;*/
  optionsModel:any;
  optionsModelLocation:any;
  optionsModelLocationCreate:any;
  optionsModelCreate:any;
  locationModel:any;
  airlineModel:any
  roleRepairStation:any;
  roleAirline:any;
  windowInnerHieght:any;
  windowInnerWidth:any;
  windowScreenHieght:any;
  searchBlockHieght :any;
  pageWrapperHeight:any;
  expandCreateRow: boolean = false;
  tableDataHeight:any;
  userGroupDetails : any;
  mOptions = [];
  mOptionsAirline = [];
  selectedItemsLocation = [];
  selectedItemsAirline = [];
  selectedItemsLocationEdit = [];
  selectedItemsAirlineEdit = [];

  dropdownSettings = {};
  selectedCustomerList:any;
  selectedCustomerName = "";
  selectedItems:any = "";
  dropdownList = [];

  pageCount: any; // for pagination
  currentPage: any; // for pagination
  pageSize: any; // for pagination
  totalPages: any; // for pagination
  TableRowLength: any; // for pagination
  paginationDetails: any; // for pagination
  pageRenge: any; // for pagination
  paginationDisabbled: boolean = false;
  pager: any = {}; // pager Object

  showProgramList: boolean = true;

  searchBlock: boolean = false;
  searchStatus: string;
  searchCriteria: any;
  selectedSearchRole: any = "";
  selectedSearchCustomer: any = "";
  selectedSearchProgram: any = "";
  searchProgList: any = "";
  

  validationData : any = {
            "rules": {
                "userName": {
                    "required": true,
                    "minlength" : 3,
                    "pattern":/^\S*$/
                },
                "userFullName": {
                    "required": true
                },
                "customerName": {
                    "required": true
                },
                "programName": {
                    "customfunction": true
                },
                "emailId": {
                    "required": true,
                    "pattern":/^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
                    //"pattern":/^[a-zA-Z0-9._-]+@[a-zA-Z0-9-]+\.[a-zA-Z.]{2,5}$/
                },
                "password": {
                    "required": true
                },
                "cnfrmPswd": {
                    "required": true,
                    "compareField" : "#password"
                },
                "role": {
                    "required": true
                },
                "vpnUserName": {
                    "required": true
                },
                "vpnPassword": {
                    "required": true,
                    //"pattern": /^[^$]*$/
                }
            },
            "messages": {
                "userName": {
                    "required": "User Name is required",
                    "minlength" : "min length should not be lesser than 3 characters",
                    "pattern" : "Username should not contains space"
                },
                "userFullName": {
                    "required": "User Full Name is required"
                },
                "customerName": {
                    "required": "Customer Name is required"
                },
                "programName": {
                    "customfunction": "Program Name is required"
                },
                "emailId": {
                    "required": "Email is required",
                    "pattern" : "Please enter a valid Email"
                },
                "password": {
                    "required": "Password is required"
                },
                "cnfrmPswd": {
                    "required": "Confirm Password is required",
                    "compareField" : "Password and Confirm Password not matching"
                },
                "role": {
                    "required": "Role is required"
                },
                "vpnUserName": {
                    "required": "VPN User Name is required"
                },
                "vpnPassword": {
                    "required": "VPN Password is required",
                    "pattern": "'$' not allowed since '$' is a special variable in the shell script"
                }
            }
          };

  // What to clone
  @ViewChild('clone') template;

  // Where to insert the cloned content
  @ViewChild('container', {read:ViewContainerRef}) container;

  @ViewChild('createTab') createTabRef: ElementRef;
  @ViewChild('searchTab') searchTabRef: ElementRef;

  @ViewChild('confirmModal') confirmModalRef: ElementRef;
  @ViewChild('sessionExpiredModal') sessionExpiredModalRef: ElementRef;
  @ViewChild('successModal') successModalRef: ElementRef;
  @ViewChild('searchForm') searchForm;


  // @HostListener allows us to also guard against browser refresh, close, etc.
  //@HostListener('window:beforeunload', ['$event'])
  /*canDeactivate(): Observable<boolean> | boolean {
    // insert logic to check if there are pending changes here;
    // returning true will navigate without confirmation
    // returning false will show a confirm dialog before navigating away
    return false;*/
 // }
  
  constructor(
    private element: ElementRef,
    private renderer: Renderer,
    private router:Router,
    private modalService: NgbModal,
    private userManagementService: UserManagementService,
    private sharedService: SharedService
    ) {}

  ngOnInit() {
      //For Pagination
      this.currentPage = 1;
      this.totalPages = 1;
      this.TableRowLength = 10;
      this.pageSize = 10;
      let paginationDetails = {
        "count": this.TableRowLength,
        "page": this.currentPage
    };

    this.paginationDetails = paginationDetails;
      
    this.windowInnerHieght = window.innerHeight;
    this.windowInnerWidth = window.innerWidth;
    this.windowScreenHieght = window.screen.height;
    this.pageWrapperHeight = $("#contentWrapper").height();
    this.searchBlockHieght = $(".bluePrintFormWrapper").height();  
    this.tableDataHeight = (($("#contentWrapper").height()) - ($("#container").height()+$(".mainHead").height()+$(".topButtonWrapper").height()));          
    this.userGroupDetails = JSON.parse(sessionStorage.loginDetails).userGroup;
    //this.userGroupDetails = JSON.parse(sessionStorage.loginDetails);
    this.userRoleList = [];
    this.selectedCustomerList = JSON.parse(sessionStorage.selectedCustomerList);
    this.selectedItems = [];
    this.selectedCustomerName = "";
    this.selectedItemsLocation = [];
    this.selectedItemsAirline = [];
    this.selectedItemsLocationEdit = [];
    this.selectedItemsAirlineEdit = [];

    this.searchBlock = true;
    this.searchStatus = 'load';
    this.setMenuHighlight("search");
    this.createForm = false;
    this.selectedSearchRole = "";
    this.selectedSearchCustomer = "";
    this.selectedSearchProgram = "";

  
    this.showLoader = true;
    this.tableShowHide = false;
    this.sharedService.userNavigation = true;//unblock user navigation
    this.editableFormArray = [];
    //this.myOptions = [];
    //this.myOptionsAirline = [];
    
    this.optionsModelCreate = [];
    this.optionsModelLocationCreate = [];
    this.optionsModelLocation = [];
    this.optionsModel = [];   
    this.searchProgList = JSON.parse(sessionStorage.loginDetails).programSubscription;
    this.userManagementOnLoad();
  
  }

  userManagementOnLoad(){
    $("#dataWrapper").find(".scrollBody").scrollLeft(0);
    this.showLoader = true;
    this.userManagementService.getUserDetails( this.selectedCustomerList, this.sharedService.createServiceToken(),this.paginationDetails, this.searchStatus, this.searchCriteria )
    .subscribe(
        data => {
            setTimeout(() => { 
              let jsonStatue = data.json();
              this.tableData = data.json();
                  if(jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User"){
                 
                   this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                 
                  } else {

                    if( this.sharedService.isvalidServiceToken( parseInt(data.json().serviceToken,10) ) || data.json().sessionId == "408"){
                      if(jsonStatue.status == "SUCCESS"){
                        this.showLoader = false;
                        this.tableData = jsonStatue;

                        this.totalPages = this.tableData.pageCount;
                        let pageCount = [];
                        for (var i = 1; i <= this.tableData.pageCount; i++) {
                            pageCount.push(i);
                        }
                        this.pageRenge = pageCount;
                        this.pager = this.getPager(this.totalPages * this.pageSize, this.currentPage, this.pageRenge);


                        /* if( this.userGroupDetails == "Default User" && this.tableData.userList.length > 0 ) {
                            $("#createnewBtn").addClass("buttonDisabled");
                        }
                        else {
                            $("#createnewBtn").removeClass("buttonDisabled");
                        } */
                    
                        //Sort the roles with id
                        let userRoleList = this.tableData.roleList;
                        userRoleList.sort(function (a, b) {
                            return a.id - b.id;
                        });

                        //Finding role id of current user 
                        let roleId = -1;
                        for(let roleIndex = 0; roleIndex < userRoleList.length; roleIndex++) {
                            if(userRoleList[roleIndex].role == this.userGroupDetails) {
                                roleId = userRoleList[roleIndex].id;
                            }
                        }

                        for (let i = 0; i < userRoleList.length; i++) {
                            //If roleId is Default User, Enable only Super Admininstrator (id = 2)
                            if (roleId == 1) {
                                if (userRoleList[i].id == 2) {
                                    userRoleList[i].isAllowed = true;
                                }
                                else {
                                    userRoleList[i].isAllowed = false;
                                    // userRoleList.splice(i, 1);
                                }
                            }
                            else {
                                if (userRoleList[i].id <= roleId) {
                                    userRoleList[i].isAllowed = false;
                                }
                                else {
                                    userRoleList[i].isAllowed = true;
                                }
                            }
                        }

                        //Remove Default user from list
                        userRoleList.splice(0, 1);
                        this.userRoleList = userRoleList;

                        this.privilegeSetting = this.tableData.privilegeSetting;
                        
                        if(this.tableData.userList.length == 0){
                          this.tableShowHide = false;
                          this.noDataVisibility = true;
                        }else{
                          this.tableShowHide = true;
                           this.noDataVisibility = false;
                           setTimeout(() => { 
                            let tableWidth = document.getElementById('ManageUserDetails').scrollWidth;
                                    $(".scrollBody table").css("min-width",(tableWidth) + "px");
                                    $(".scrollHead table").css("width", tableWidth + "px");

                                
                                    $(".scrollBody").on("scroll", function (event) {
                                        $(".formEditRow #formWrapper").css("margin-left", (event.target.scrollLeft - 5) + "px");
                                        $(".form-control-fixed").css("right",(event.target.scrollLeft * -1) + "px");
                                        $(".scrollHead table").css("margin-left",(event.target.scrollLeft * -1) + "px");
                                    });
                                    $(".scrollBody").css("max-height",(this.tableDataHeight - 10) + "px");

                            },0); 
                        }
                     
                      }else{
                        this.showLoader = false;
                        this.displayModel(jsonStatue.reason,"failureIcon");   
                      }

                     }   
                  }
                                
            }, 1000);
        },
        error => {
          //Please Comment while checkIn
         /*  setTimeout(() => { 
            this.showLoader = false;
            //this.tableData = {"sessionId":"2342sda","serviceToken":"122423","status":"SUCCESS","userList":[{"id":3,"userName":"balapafgdt","password":"testbbssasd","cnfrmPswd":"7bbc6a6462c96552ca269742ad37bd59","roleId":3,"role":"Admin","emailId":"testrefgdr@gmail.com","remarks":"goodsdfgsg","creationDate":"2017-10-17 18:33:23","lastLoginDate":"2017-10-17 18:35:41","status":"Active"},{"id":4,"userName":"patil123rt","password":"testbbssasd","cnfrmPswd":"7bbc6a6462c96552ca269742ad37bd59","roleId":3,"role":"Admin","emailId":"testrefgdrfsdf@gmail.com","remarks":"goodsdfgsg","creationDate":"2017-10-17 18:33:23","lastLoginDate":"2017-10-17 18:35:41","status":"Active"},{"id":5,"userName":"kamalesh","password":"kamalesh","cnfrmPswd":"7bbc6a6462c96552ca269742ad37bd59","roleId":3,"role":"Admin","emailId":"kamalesh@gmail.com","remarks":"goodsdfgsg","creationDate":"2017-10-17 18:33:23","lastLoginDate":"2017-10-17 18:35:41","status":"Inactive"}],"roleList":[{"id":3,"role":"Admin"},{"id":4,"role":"PSE"}]};
            // this.tableData = {"customerList":[{"id":12,"customerName":"All"},{"id":2,"customerName":"AT&T"},{"id":3,"customerName":"Sprint"},{"id":1,"customerName":"Verizon"}],"sessionId":"f2352bc2","serviceToken":"60206","status":"SUCCESS","reason":null,"userList":[{"id":1,"userName":"Kamlesh","password":"kamlesh","cnfrmPswd":"kamlesh","roleId":1,"role":"Administrator","emailId":"kamlesh.gupta@lnttechservices.com","location":null,"remarks":"Admin User.","creationDate":"2018-10-05 18:01:55","lastLoginDate":"2018-11-05 18:49:39","status":"Active","userFullName":"Kamlesh Kumar","customerName":"Verizon","networkType":"2G","customerId":null},{"id":1,"userName":"Jhon","password":"jhon","cnfrmPswd":"jhon","roleId":1,"role":"Administrator","emailId":"jhon@lnttechservices.com","location":null,"remarks":"Admin User.","creationDate":"2018-10-05 18:01:55","lastLoginDate":"2018-11-05 18:49:39","status":"Active","userFullName":"Jhon D","customerName":"Verizon","networkType":"3G","customerId":null},{"id":1,"userName":"Sreeraj","password":"sreeraj","cnfrmPswd":"sreeraj","roleId":1,"role":"Administrator","emailId":"sreeraj.mano@lnttechservices.com","location":null,"remarks":"Admin User.","creationDate":"2018-10-05 18:01:55","lastLoginDate":"2018-11-05 18:49:39","status":"Active","userFullName":"Sreeraj Manoharan","customerName":"Verizon","networkType":"4G","customerId":null},{"id":1,"userName":"Rakesh","password":"rakesh","cnfrmPswd":"rakesh","roleId":1,"role":"Commission Engineer","emailId":"rakesh.kumar@lnttechservices.com","location":null,"remarks":"Commission Engineer User.","creationDate":"2018-10-05 18:01:55","lastLoginDate":"2018-11-05 18:49:39","status":"Active","userFullName":"Rakesh Kumar","customerName":"Verizon","networkType":"5G","customerId":null},{"id":1,"userName":"Ramesh","password":"ramesh","cnfrmPswd":"ramesh","roleId":1,"role":"Super Administrator","emailId":"ramesh.kumar@lnttechservices.com","location":null,"remarks":"Super Administrator User.","creationDate":"2018-10-05 18:01:55","lastLoginDate":"2018-11-05 18:49:39","status":"Active","userFullName":"Ramesh Kumar","customerName":"Verizon","networkType":"6G","customerId":null}],"roleList":[{"id":1,"role":"Super Administrator"},{"id":1,"role":"Administrator"},{"id":2,"role":"Commission Engineer"}],"networkTypeDetailsEntities":[{"id":12,"networkType":"All","createdBy":"admin","caretedDate":null,"status":"Active","remarks":"active"},{"id":1,"networkType":"4G","createdBy":"admin","caretedDate":null,"status":"Active","remarks":"active"},{"id":2,"networkType":"5G","createdBy":"admin","caretedDate":null,"status":"Active","remarks":"active"}]};
            //this.tableData = {"sessionId":"f2352bc2","serviceToken":"60206","status":"SUCCESS","reason":null,"userList":null,"customerList":[{"id":2,"customerName":"AT&T"},{"id":3,"customerName":"Sprint"},{"id":1,"customerName":"Verizon"}],"roleList":[{"id":1,"role":"Super Administrator"},{"id":1,"role":"Administrator"},{"id":2,"role":"Commission Engineer"}]};
            this.tableData = {"reason":null,"pageCount":2,"userList":[{"id":14,"userName":"sup","password":"123","cnfrmPswd":"123","roleId":3,"role":"Administrator","emailId":"sup@gmail.com","location":null,"remarks":"","creationDate":"2019-04-09 03:25:34","lastLoginDate":null,"status":"Active","userFullName":"sup","customerName":"Verizon","customerId":2,"networkTypeId":null,"networkType":null,"programName":["All"],"vpnUserName":"1234","vpnPassword":"1234","createdBy":"superadmin"},{"id":13,"userName":"hfgh","password":"1234","cnfrmPswd":"1234","roleId":3,"role":"Administrator","emailId":"s@gmail.com","location":null,"remarks":"","creationDate":"2019-04-09 02:53:49","lastLoginDate":"2019-04-09 02:54:26","status":"Active","userFullName":"ghk","customerName":"Verizon","customerId":2,"networkTypeId":null,"networkType":null,"programName":["All"],"vpnUserName":"123","vpnPassword":"1234","createdBy":"superadmin"},{"id":8,"userName":"son","password":"son","cnfrmPswd":"son","roleId":5,"role":"Commission Engineer","emailId":"son.hoang@partner.sea.samsung.com","location":null,"remarks":"","creationDate":"2019-04-05 05:11:54","lastLoginDate":null,"status":"Active","userFullName":"Son Hong","customerName":"Verizon","customerId":2,"networkTypeId":null,"networkType":null,"programName":["VZN-4G-LSM","VZN-4G-VLSM"],"vpnUserName":"admin","vpnPassword":"admin","createdBy":"superadmin"},{"id":4,"userName":"superadmin","password":"admin","cnfrmPswd":"admin","roleId":2,"role":"Super Administrator","emailId":"anithalakshmi.tr@ltts.com","location":null,"remarks":"","creationDate":"2019-03-30 16:08:28","lastLoginDate":"2019-04-09 03:46:16","status":"Active","userFullName":"Super Administrator","customerName":"All","customerId":1,"networkTypeId":null,"networkType":null,"programName":["All"],"vpnUserName":"arun","vpnPassword":"arun","createdBy":"superadmin"},{"id":9,"userName":"Dykes","password":"admin","cnfrmPswd":"admin","roleId":3,"role":"Administrator","emailId":"dykes.g2@partner.sea.samsung.com","location":null,"remarks":"","creationDate":"2019-03-30 12:38:01","lastLoginDate":"2019-03-30 20:35:56","status":"Active","userFullName":"Dykes Griffin","customerName":"Sprint","customerId":4,"networkTypeId":null,"networkType":null,"programName":["All"],"vpnUserName":"dykes","vpnPassword":"dykes","createdBy":"superadmin"},{"id":3,"userName":"suruchi","password":"admin","cnfrmPswd":"admin","roleId":2,"role":"Super Administrator","emailId":"suruchi.g@sea.samsung.com","location":null,"remarks":"","creationDate":"2019-03-21 13:22:34","lastLoginDate":null,"status":"Active","userFullName":"Suruchi Gupta","customerName":"All","customerId":1,"networkTypeId":null,"networkType":null,"programName":["All"],"vpnUserName":"suruchi","vpnPassword":"suruchi","createdBy":"superadmin"},{"id":11,"userName":"matthew","password":"admin","cnfrmPswd":"admin","roleId":5,"role":"Commission Engineer","emailId":"m.hermez@partner.sea.samsung.com","location":null,"remarks":"","creationDate":"2019-03-21 00:05:15","lastLoginDate":null,"status":"Active","userFullName":"Matthew Hermez","customerName":"Sprint","customerId":4,"networkTypeId":null,"networkType":null,"programName":["SPT-4G-MIMO","SPT-4G-CLWR_CONV"],"vpnUserName":"matthew","vpnPassword":"matthew","createdBy":"superadmin"},{"id":10,"userName":"thang","password":"admin","cnfrmPswd":"admin","roleId":4,"role":"Commission Manager","emailId":"thang.pham2@partner.sea.samsung.com","location":null,"remarks":"","creationDate":"2019-03-21 00:03:44","lastLoginDate":null,"status":"Active","userFullName":"Thang Pham","customerName":"Sprint","customerId":4,"networkTypeId":null,"networkType":null,"programName":["SPT-4G-MIMO","SPT-4G-CLWR_CONV"],"vpnUserName":"thang","vpnPassword":"thang","createdBy":"superadmin"},{"id":7,"userName":"john","password":"admin","cnfrmPswd":"admin","roleId":4,"role":"Commission Manager","emailId":"m.baliwag@partner.sea.samsung.com","location":null,"remarks":"","creationDate":"2019-03-20 23:59:18","lastLoginDate":null,"status":"Active","userFullName":"Mar John Baliwag","customerName":"Verizon","customerId":2,"networkTypeId":null,"networkType":null,"programName":["VZN-4G-LSM","VZN-4G-VLSM"],"vpnUserName":"john","vpnPassword":"john","createdBy":"superadmin"},{"id":6,"userName":"lakisha","password":"admin","cnfrmPswd":"admin","roleId":3,"role":"Administrator","emailId":"l.spain@partner.sea.samsung.com","location":null,"remarks":"","creationDate":"2019-03-20 23:57:43","lastLoginDate":null,"status":"Active","userFullName":"Lakisha Spain","customerName":"Verizon","customerId":2,"networkTypeId":null,"networkType":null,"programName":["All"],"vpnUserName":"lakisha","vpnPassword":"lakisha","createdBy":"superadmin"}],"customerList":[{"id":1,"customerName":"All","iconPath":" ","status":"Active","customerShortName":null,"customerDetails":[]},{"id":2,"customerName":"Verizon","iconPath":"/customer/verizon_ 03222019_18_53_36_icon.png","status":"Active","customerShortName":"VZN","customerDetails":[{"id":1,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T13:24:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"VZN-4G-CDU30","programDescription":"CDU30","status":"Active","creationDate":"2019-03-30T17:34:43.000+0000","createdBy":"superadmin"},{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T13:24:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"VZN-4G-VLSM","programDescription":"VLSM","status":"Active","creationDate":"2019-03-22T15:33:50.000+0000","createdBy":"superadmin"},{"id":11,"networkTypeDetailsEntity":{"id":3,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-01-23T11:03:34.000+0000","status":"Active","remarks":"","networkColor":"#0e183b"},"programName":"VZN-5G-NR","programDescription":"NR","status":"Active","creationDate":"2019-03-22T18:53:58.000+0000","createdBy":"superadmin"}]},{"id":3,"customerName":"AT&T","iconPath":"/customer/at&t_ 03222019_16_12_35_icon.png","status":"Active","customerShortName":"ATT","customerDetails":[{"id":12,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T13:24:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"ATT-4G-CBRS","programDescription":"CBRS","status":"Active","creationDate":"2019-03-22T16:13:18.000+0000","createdBy":"superadmin"},{"id":13,"networkTypeDetailsEntity":{"id":3,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-01-23T11:03:34.000+0000","status":"Active","remarks":"","networkColor":"#0e183b"},"programName":"ATT-5G-PICO","programDescription":"PICO","status":"Active","creationDate":"2019-03-22T16:13:41.000+0000","createdBy":"superadmin"}]},{"id":4,"customerName":"Sprint","iconPath":"/customer/sprint_ 03222019_18_55_22_icon.png","status":"Active","customerShortName":"SPT","customerDetails":[{"id":3,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T13:24:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-MIMO","programDescription":"MIMO","status":"Active","creationDate":"2019-03-21T20:23:18.000+0000","createdBy":"superadmin"},{"id":4,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T13:24:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-CDU30","programDescription":"CDU30","status":"Active","creationDate":"2019-03-22T18:55:42.000+0000","createdBy":"superadmin"},{"id":6,"networkTypeDetailsEntity":{"id":3,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-01-23T11:03:34.000+0000","status":"Active","remarks":"","networkColor":"#0e183b"},"programName":"SPT-5G-FPGA","programDescription":"FPGA","status":"Active","creationDate":"2019-03-22T15:21:03.000+0000","createdBy":"superadmin"}]}],"sessionId":"8041bae1","serviceToken":"66501","roleList":[{"id":3,"role":"Administrator"},{"id":5,"role":"Commission Engineer"},{"id":4,"role":"Commission Manager"},{"id":1,"role":"Default User"},{"id":2,"role":"Super Administrator"}],"status":"SUCCESS"};

                if(this.tableData.sessionId == "408" || this.tableData.status == "Invalid User"){
                   this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                }

                this.totalPages = this.tableData.pageCount;
                let pageCount = [];
                for (var i = 1; i <= this.tableData.pageCount; i++) {
                    pageCount.push(i);
                }
                this.pageRenge = pageCount;
                this.pager = this.getPager(this.totalPages * this.pageSize, this.currentPage, this.pageRenge);

                // this.userGroupDetails = "Default User";

                //Sort the roles with id
                let userRoleList = this.tableData.roleList;
                userRoleList.sort(function (a, b) {
                    return a.id - b.id;
                });

                //Finding role id of current user 
                let roleId = -1;
                for(let roleIndex = 0; roleIndex < userRoleList.length; roleIndex++) {
                    if(userRoleList[roleIndex].role == this.userGroupDetails) {
                        roleId = userRoleList[roleIndex].id;
                    }
                }

                for (let i = 0; i < userRoleList.length; i++) {
                    //If roleId is Default User, Enable only Super Admininstrator (id = 2)
                    if (roleId == 1) {
                        if (userRoleList[i].id == 2) {
                            userRoleList[i].isAllowed = true;
                        }
                        else {
                            userRoleList[i].isAllowed = false;
                            // userRoleList.splice(i, 1);
                        }
                    }
                    else {
                        if (userRoleList[i].id <= roleId) {
                            userRoleList[i].isAllowed = false;
                        }
                        else {
                            userRoleList[i].isAllowed = true;
                        }
                    }
                }

                //Remove Default user from list
                userRoleList.splice(0, 1);
                this.userRoleList = userRoleList;

                this.privilegeSetting = {"USER_NAME":true};
                // this.myOptions = this.tableData.locationList;
              
                if(this.tableData.userList.length == 0){
                  this.tableShowHide = false;
                  this.noDataVisibility = true;
                }else{

                  this.tableShowHide = true;
                  this.noDataVisibility = false;
                  setTimeout(() => { 
                    let tableWidth = document.getElementById('ManageUserDetails').scrollWidth;
                    $(".scrollBody table").css("min-width",(tableWidth) + "px");
                    $(".scrollHead table").css("width", tableWidth + "px");

                
                    $(".scrollBody").on("scroll", function (event) {
                        $(".formEditRow #formWrapper").css("margin-left", (event.target.scrollLeft - 5) + "px");
                        $(".form-control-fixed").css("right",(event.target.scrollLeft * -1) + "px");
                        $(".scrollHead table").css("margin-left",(event.target.scrollLeft * -1) + "px");
                    });
                    //$(".scrollBody").css("max-height",(this.windowScreenHieght/2) + "px");
                    $(".scrollBody").css("max-height",(this.tableDataHeight - 10) + "px");                   
                    },0);                      
                }             
          }, 1000); */
          //Please Comment while checkIn
    });
  }

  searchTabBind() {
    let searchCrtra = { "userName": "", "userFullName": "", "roleId": "", "customerId": "", "programNamehidden": "", "emailId" : "", "status" : "", "createdBy" : "" };
    this.searchCriteria = searchCrtra;

    this.setMenuHighlight("search");

    this.createForm = false;
    this.searchBlock = true;
    this.searchStatus = 'load';
    this.selectedSearchRole = "";
    this.selectedSearchCustomer = "";
    this.selectedSearchProgram = "";

    this.tableShowHide = false;
    this.userManagementOnLoad();

    // Close if edit form is in open state
    /* if (this.currentEditRow != undefined) {
      this.currentEditRow.className = "editRow";
    } */
    this.searchForm.nativeElement.reset();

    this.editableFormArray = [];

    /* this.searchBlock = true;
    this.uploadBlock = false;
   // this.createBlock=false;
    this.searchStatus = 'load';
    this.tableShowHide = false;
    this.getUploadScript();
    // Close if edit form is in open state
    if (this.currentEditRow != undefined) {
        this.currentEditRow.className = "editRow";
    }
    this.searchForm.nativeElement.reset();
 
    this.editableFormArray = [];
    setTimeout(() => {
        validator.performValidation(event, this.validationData, "search");
    }, 10); */
  }

  clearSearchFrom() {
    this.selectedSearchRole = "";
    this.selectedSearchCustomer = "";
    this.selectedSearchProgram = "";
    this.searchForm.nativeElement.reset();
  }

  searchUserMgmt(event) {

    setTimeout(() => {
      $("#dataWrapper").find(".scrollBody").scrollLeft(0);
    }, 0);

    validator.performValidation(event, this.validationData, "search");
    setTimeout(() => {
      if (this.isValidForm(event)) {
        if (!event.target.classList.contains('buttonDisabled')) {
          this.showLoader = true;
          this.tableShowHide = false;

          // To hide the No Data Found and REMOVAL DETAILS Form
          /* this.uploadBlock = false;
          this.showNoDataFound = false; */

          let currentForm = event.target.parentNode.parentNode.parentNode,
            searchCrtra = {
              "userName": currentForm.querySelector("#searchUserName").value,
              "userFullName": currentForm.querySelector("#searchName").value,
              "roleId": $("#searchRole" + " option:selected").attr("id") ? parseInt($("#searchRole" + " option:selected").attr("id")) : null,
              "customerId": $("#searchCustomer" + " option:selected").attr("id"),//currentForm.querySelector("#searchCustomer").value,
              "programNamehidden": currentForm.querySelector("#searchProgName").value,//$("#searchProgName" + " option:selected").attr("id"),//
              "emailId": currentForm.querySelector("#searchEmail").value,
              "status": currentForm.querySelector("#searchStatus").value,
              "createdBy": currentForm.querySelector("#searchCreatedBy").value
            };

          if (searchCrtra.userName || searchCrtra.userFullName || searchCrtra.roleId || searchCrtra.customerId || searchCrtra.programNamehidden || searchCrtra.emailId || searchCrtra.status || searchCrtra.createdBy) {
            this.searchStatus = "search";
          }
          else {
            this.searchStatus = "load";
          }

          this.searchCriteria = searchCrtra;

          this.currentPage = 1;
          let paginationDetails = {
            "count": this.pageSize,
            "page": this.currentPage
          };

          this.paginationDetails = paginationDetails;
          // TO get the searched data
          this.userManagementOnLoad();
        }
      }
    }, 0);
  }

  setMenuHighlight(selectedElement) {
      this.createTabRef.nativeElement.id = (selectedElement == "create") ? "activeTab" : "inactiveTab";
      //  this.createTabRef.nativeElement.id = (selectedElement == "create") ? "activeTab" : "inactiveTab";
      this.searchTabRef.nativeElement.id = (selectedElement == "search") ? "activeTab" : "inactiveTab";
  }

  onItemSelect (item:any) {
    console.log(item);
  }
  onSelectAll (items: any) {
    console.log(items);
  }

  getProgramName(selectedCustomerName) {
    //   this.useCaseValue = [];
    //   this.selectedCustomerName = "";
      this.selectedItems = [];
      this.dropdownList = [];
      let customerList = this.tableData.customerList;
      let programList = [];
      if (this.userGroupDetails == 'Super Administrator') {
          for (let customerIndex = 0; customerIndex < customerList.length; customerIndex++) {
              if (customerList[customerIndex].customerName == selectedCustomerName) {
                  programList = customerList[customerIndex].customerDetails;
              }
          }
      }
      else {
        programList = JSON.parse(sessionStorage.loginDetails).programSubscription;
      }
      for (let itm of programList) {
          let dropdownList = { item_id: itm.programName, item_text: itm.programName };
          this.dropdownList.push(dropdownList);
      }

      this.dropdownSettings = {
          singleSelection: false,
          idField: 'item_id',
          textField: 'item_text',
          selectAllText: 'Select All',
          unSelectAllText: 'UnSelect All',
          itemsShowLimit: 1,
          allowSearchFilter: false
      };
  }
  
  closeModel(){
      this.successModalBlock.close();
      this.ngOnInit();
  }



  /* validates current submitted form is valid and free from errors
   * @param : pass the event
   * @retun : boolean
   */

  isValidForm(event){
    return ( $(event.target).parents("form").find(".error-border").length == 0) ? true : false;
  }


  
  /* On click create new save take the data from the form and form json
   * @param : pass the event
   * @retun : null
   */

  addNewRow(event){
    if (this.selectedItems.length > 0) {
        this.validationData.rules.programName.customfunction = false;

    } else {
        this.validationData.rules.programName.customfunction = true;

    }
    validator.performValidation(event, this.validationData, "save_update");
    setTimeout(() => { 
      if(this.isValidForm(event)){
         this.showLoader = true;
        // this.element.nativeElement.querySelector('.createbtn').classList.remove('buttonDisabled');
        
        let currentForm = event.target.parentNode.parentNode,
            userDetails = {
                        "id": null,                       
                        "userName": currentForm.querySelector("#userName").value,
                        "userFullName": currentForm.querySelector("#userFullName").value,
                        /*"customerName": (currentForm.querySelector("#role").value != 'Administrator') ? currentForm.querySelector("#customerName").value : "",
                        "customerId": (currentForm.querySelector("#role").value != 'Administrator') ? $("#customerName option:selected")[0].id : "",*/
                        "customerName": currentForm.querySelector("#customerName").value,
                        "customerId": $("#customerName option:selected")[0].id,
                        /* "networkType": currentForm.querySelector("#networkType").value,
                        "networkTypeId": $("#networkType option:selected")[0].id, */
                        "password":btoa(currentForm.querySelector("#password").value),              
                        "role": currentForm.querySelector("#role").value,
                        "roleId": $("#role option:selected")[0].id,
                        "emailId": currentForm.querySelector("#emailId").value,
                        "status": currentForm.querySelector("#status").value,
                        "remarks": currentForm.querySelector("#remarks").value,
                        "programName": (this.selectedItems && this.selectedItems.length > 0) ? this.selectedItems : [currentForm.querySelector("#programName1").value],
                        "vpnUserName": currentForm.querySelector("#vpnUserName").value,
                        "vpnPassword": btoa(currentForm.querySelector("#vpnPassword").value)
                       }
        this.sharedService.userNavigation = true; //un block user navigation

        this.userManagementService.createUser(userDetails, this.sharedService.createServiceToken())
        .subscribe(
          data => {

              let currentData = data.json();
              this.showLoader = false;
              
                  if(currentData.sessionId == "408" || currentData.status == "Invalid User"){
              
                     this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                  } else{                       
                    
                    if( this.sharedService.isvalidServiceToken( parseInt(data.json().serviceToken,10) ) || data.json().sessionId == "408"){

                      if(currentData.status=="SUCCESS"){
                         this.message = "User created successfully!";
                         this.successModalBlock = this.modalService.open(this.successModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'success-modal'});
                        //this.displayModel("User created successfully!","successIcon");
                        // this.createForm = false;
                        this.cancelCreateNew(event);
                      } else if(currentData.status=="FAILED"){
                          this.displayModel(currentData.reason,"failureIcon");
                      }
                    }     
                }
                    
          },
          error => {
            //Please Comment while checkIn
            /* let currentData = {"sessionId":"408","reason":"failed to update","status":"SUCCESS","serviceToken":"81749"};
            setTimeout(() => {
              this.showLoader = false;
              if(currentData.status=="SUCCESS"){
                this.message = "User created successfully!";
                this.successModalBlock = this.modalService.open(this.successModalRef,{windowClass: 'success-modal',keyboard: false, backdrop: 'static' , size: 'lg'});
                this.cancelCreateNew(event);
              } else if(currentData.status=="FAILED"){
                this.displayModel(currentData.reason,"failureIcon");
              }   
            }, 1000); */
            //Please Comment while checkIn
          });
      }
    },0);    
  }

  
 /*
   * Used to dispaly the status messages like SUCCESS/FAILURE on view
   * @param : message, messageType (successIcon/failureIcon)
   * @retun : null
   */
  
  displayModel(message:string,messageType:string){
    this.messageType = messageType;
    this.showModelMessage = true;
    this.modelData = {
      "message" : message,
      "modelType" : messageType
    };

    setTimeout(() => { 
      this.showModelMessage = false;
      
    }, 10);
  }
  
  /*
   * On session expired close the webpage and logout
   * @param : null
   * @retun : null
   */

  cancelCreateNew(event){
    this.container.clear();
    this.createForm = false;
    this.searchBlock = true;
    this.tableShowHide = true;
    this.setMenuHighlight("search");
    this.selectedItems = [];
    this.showProgramList = true;
    this.selectedCustomerName = "";

    // document.querySelector(".createbtn").className = document.querySelector(".createbtn").className.replace("buttonDisabled","");
    $(".scrollBody").css("max-height",(this.tableDataHeight - 10) + "px");
  }

  /*
   * On session expired close the webpage and logout
   * @param : null
   * @retun : null
   */

  closeAndLogout(){
    //TODO : Need to set this.isLoggedIn global
    this.sessionExpiredModalBlock.close();
    this.sharedService.setUserLoggedIn(false);

    setTimeout(() => { 
      sessionStorage.clear();
      this.router.navigate(['/']); 
    }, 10);
  }

  /*
   * On click sort header in table then sort the data ascending and decending
   * @param : columnName, event and current Index
   * @retun : null
   */

  changeSorting(predicate, event, index){
    this.sharedService.dynamicSort(predicate, event, index, this.tableData.userList);
  }
  
  /*
   * On click create new copy the blue print and show the form
   * @param : pass the event
   * @retun : null
   */

  createNew(event){
    if(this.element.nativeElement.querySelector('#tableWrapper')){
      this.formWidth = this.element.nativeElement.querySelector('#tableWrapper').clientWidth  + "px";  
    }
    if(this.element.nativeElement.querySelector('.scrollBody')){
      this.scrollLeft = this.element.nativeElement.querySelector('.scrollBody').scrollLeft + "px";   
    }

    // this.tableShowHide = false;
    this.searchBlock = false;
    this.searchStatus = 'load';
    this.setMenuHighlight("create");
    this.tableShowHide = false;
    
    // Close if any edit form is opend
    this.editableFormArray = [];

    // Enable all edit buttons
    $(".editRowDisabled").attr("class","editRow");  
    $(".deleteRowDisabled").attr("class","deleteRow"); 
      let currentTarget = event.target;
     
        this.sharedService.userNavigation = false;//block user navigation
          
        /* if (currentTarget.className.indexOf('buttonDisabled') < 0 ) {
          this.container.createEmbeddedView(this.template);
          //this.sharedService.userNavigation = false;//block user navigation
          currentTarget.className += " buttonDisabled";
          setTimeout(() => { 
            validator.performValidation(event, this.validationData, "create");
           }, 10);
      } */
      this.createForm = true;
      this.selectedItems = [];

      if ((JSON.parse(sessionStorage.getItem("loginDetails")).userGroup == "Default User") || (JSON.parse(sessionStorage.getItem("loginDetails")).userGroup == "Super Administrator")) {
          this.selectedCustomerName = "";
      }
      else {
          this.selectedCustomerName = JSON.parse(sessionStorage.getItem("selectedCustomerList")).customerName;
          this.getProgramName(this.selectedCustomerName);
      }

/*       this.selectedCustomerName = JSON.parse(sessionStorage.getItem("selectedCustomerList")).customerName;
 */      setTimeout(() => { 
        this.searchBlockHieght = $(".bluePrintFormWrapper").height();  
        $(".scrollBody").css("max-height",(this.tableDataHeight - this.searchBlockHieght) -30 + "px");

      },0);
          
         

  }



   /*
   * On click delete row open a modal for confirmation
   * @param : content, userName
   * @retun : null
   */

    deleteRow(confirmModal, userName, id,event) {
        if (event.target.className != "deleteRowDisabled") {
      this.modalService.open(confirmModal,{keyboard: false, backdrop: 'static', size:'lg', windowClass:'confirm-modal'}).result.then((result) => {
        this.closeResult = `Closed with: ${result}`;
      }, (reason) => {
        this.showLoader = true;

        this.userManagementService.deleteUserDetails(id, this.sharedService.createServiceToken())
              .subscribe(
                  data => {
                    let jsonStatue = data.json();
                    setTimeout(() => { 
                        this.showLoader = false;
                    }, 2000);                      

                      if(jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User"){
                        
                           this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                      
                      } else {
                        
                        if( this.sharedService.isvalidServiceToken( parseInt(data.json().serviceToken,10) ) || data.json().sessionId == "408"){

                          if(jsonStatue.status == "SUCCESS"){
                               this.message = "User deleted successfully!";
                               this.successModalBlock = this.modalService.open(this.successModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'success-modal'});
                                  //this.displayModel("User details deleted successfully!","successIcon");
                          } else {
                           
                              this.displayModel(jsonStatue.reason,"failureIcon");  
                          } 
                        }
                    }
                      console.log("im here success :) ");
                   
                  },
                  error => {
                    //Please Comment while checkIn
                  /*setTimeout(() => { 
                      this.showLoader = false;
                     let jsonStatue = {"reason":null,"sessionId":"5f3732a4","serviceToken":"80356","status":"SUCCESS"};
                    if(jsonStatue.status == "SUCCESS"){
                      this.message = "User deleted successfully!";
                      this.successModalBlock = this.modalService.open(this.successModalRef,{keyboard: false, backdrop: "static",size:'lg',windowClass:"success-modal"});
                    } else {
                     
                        this.displayModel(jsonStatue.reason,"failureIcon");  
                    } 
                  }, 1000);*/
                  //Please Comment while checkIn
                   
                      //this.alertService.error(error);TODO : This need to implement
              });
      });
    }
  }

  /*
   * delete confirmation popup handle dismiss click
   * @param : reason object
   * @retun : string
   */

  private getDismissReason(reason: any): string {
    if (reason === ModalDismissReasons.ESC) {
      return 'by pressing ESC';
    } else if (reason === ModalDismissReasons.BACKDROP_CLICK) {
      return 'by clicking on a backdrop';
    } else {
      return  `with: ${reason}`;
    }
  }

  /*
   * on click of edit row enable corresponding 
   * @param : current row index
   * @retun : boolean
   */

  checkFormEnable(index){
     let indexValue = this.editableFormArray.indexOf(index);
     return indexValue >= 0?true:false;
  }


  onChangeLocation(event){
    if(event.length == 0){
          $("#location").val("");
      }else{
        $("#location").val(event.toString());
      }

  }

  onChangeAirline(event){
      if(event.length == 0){
          $("#airlineName").val("");
      }else{
        $("#airlineName").val(event.toString());
      }
  }
   

  /*
   * on click of cancel edit then close the current edited form
   * @param : index, identifier
   * @retun : null
   */

  cancelEditRow(index, identifier){
    $(".editRowDisabled").attr("class","editRow");
    $(".deleteRowDisabled").attr("class","deleteRow");
    let currentEditedForm = document.querySelector(".row_id_"+identifier);
    //this.editableFormArray.splice(this.editableFormArray.indexOf(index), 1);
    this.editableFormArray = [];
    this.checkFormEnable(index); //TODO : need to recheck this function
    this.sharedService.userNavigation = true; //un block user navigation
    currentEditedForm.lastElementChild.lastElementChild.children[0].className = "editRow";
  }
  
  /*
   * on click of edit row create a blueprint and append next to the current row
   * @param : current row event , current row json object and row index 
   * @retun : null
   */

  editRow(event, key, index) {

    this.cancelCreateNew(event);
    this.showProgramList = true;
    let editState : any = event.target,
        parentForm : any;
       $(".editRowDisabled").attr("class","editRow");
       $(".deleteRowDisabled").attr("class","deleteRow");
    if( editState.className != "editRowDisabled"){ //enable click only if it is enabled
      editState.className = "editRowDisabled";
      $(editState).next().attr("class", "deleteRowDisabled") // Disable delete on edit row    
      // To enable one edit form at a time in table
      if(this.editableFormArray.length >= 1){
          this.editableFormArray = [];
          this.editableFormArray.push(index);
      } else {
         this.editableFormArray.push(index);
      }
      this.sharedService.userNavigation = false; //block user navigation

      if (key.customerName) {
        this.getProgramName(key.customerName);
        this.selectedItems = key.programName;
        /* let selectedItemsList = key.programName;
        for (let itm of selectedItemsList) {
            let dropdownList = { item_id: itm, item_text: itm };
            this.selectedItems.push(dropdownList);
        } */
      }

      setTimeout(() => { 
        /* if(key.role == this.userGroupDetails){
          $("#customerName").attr("disabled","disabled");
          $("#networkType").attr("disabled","disabled");
          this.validationData.rules.customerName.required = false;
        }else{
          $("#customerName option[value='All']").prop("disabled", true);
          $("#networkType option[value='All']").prop("disabled", true);
          $("#customerName").removeAttr("disabled");
          $("#networkType").removeAttr("disabled");

          this.validationData.rules.customerName.required = true;
        } */
        this.onChnageRole(key.role);
        this.editRowInTable(event, key, index);
        parentForm = document.querySelector("#editedRow"+index);
        //map validation for fields
        validator.performValidation(event, this.validationData, "edit");

      },0);
    }
  }

/*
   * on click of edit bind the current row data in the input components
   * @param : current row event and current row json object 
   * @retun : null
   */

  editRowInTable = function (event, key, index) {

      let currentEditedForm = document.querySelector("#editedRow"+index),
          currFormEle = 0,
          currentElement;
          this.formWidth = this.element.nativeElement.querySelector('#tableWrapper').clientWidth - 30 + "px";
          this.scrollLeft = this.element.nativeElement.querySelector('.scrollBody').scrollLeft + "px"; 
          
      for (currFormEle = 0; currFormEle < Object.keys(key).length; currFormEle++) {

          currentElement = currentEditedForm.querySelector('#'+Object.keys(key)[currFormEle]);

          if(currentElement){
            currentElement.value = key[ Object.keys(key)[currFormEle] ];
          }

          //TO DO for checkbox
      }
    }

/*
   * On click of update button in edit then send data to server and close the block
   * @param : null
   * @retun : null
   */

  updateEditRow(index, identifier, role, event){
    if (this.selectedItems.length > 0) {
        this.validationData.rules.programName.customfunction = false;

    } else {
        this.validationData.rules.programName.customfunction = true;

    }
    if(this.userGroupDetails == role) {
        this.validationData.rules.role.required = false;
    }
    validator.performValidation(event, this.validationData, "save_update");
    setTimeout(() => { 
      if(this.isValidForm(event)){
        this.showLoader = true;
        let currentEditedForm = event.target.parentNode.parentNode,
             userDetails = {
                  "id": identifier,
                  "userName": currentEditedForm.querySelector("#userName").value,
                  "userFullName": currentEditedForm.querySelector("#userFullName").value,
                  /*"customerName": (currentEditedForm.querySelector("#role").value != 'Administrator') ? currentEditedForm.querySelector("#customerName").value : undefined,
                  "customerId": (currentEditedForm.querySelector("#role").value != 'Administrator') ? $("#customerName option:selected")[0].id : undefined,*/
                  "customerName": currentEditedForm.querySelector("#customerName").value,
                  "customerId": $("#customerName option:selected")[0].id,
                  /* "networkType": currentEditedForm.querySelector("#networkType").value,
                  "networkTypeId": $("#networkType option:selected")[0].id, */
                  "password":btoa(currentEditedForm.querySelector("#password").value),              
                  "role": currentEditedForm.querySelector("#role").value,
                  "roleId": $("#role option:selected")[0].id,
                  "emailId":currentEditedForm.querySelector("#emailId").value,
                  "status": currentEditedForm.querySelector("#status").value,
                  /* "creationDate": currentEditedForm.querySelector("#creationDate").value,
                  "lastLoginDate": currentEditedForm.querySelector("#lastLoginDate").value, */
                  "remarks": currentEditedForm.querySelector("#remarks").value,
                  "programName": (this.selectedItems && this.selectedItems.length > 0) ? this.selectedItems : [currentEditedForm.querySelector("#programName1").value],
                  "vpnUserName": currentEditedForm.querySelector("#vpnUserName").value,
                  "vpnPassword": btoa(currentEditedForm.querySelector("#vpnPassword").value)
             };
             
        this.userManagementService.updateUser(userDetails, this.sharedService.createServiceToken())
        .subscribe(
            data => {
                let jsonStatue = data.json();
                this.showLoader = false;

                if(jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User"){
            
                  this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
            
                } else {
            
                  if( this.sharedService.isvalidServiceToken( parseInt(data.json().serviceToken,10) ) || data.json().sessionId == "408"){               
                      if(jsonStatue.status == "SUCCESS"){
                         this.message = "User details updated successfully !";
                         this.successModalBlock = this.modalService.open(this.successModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'success-modal'});

                      //  this.displayModel("User details updated successfully !","successIcon");
                         this.cancelEditRow(userDetails.id, '');
                       // this.createForm = false;
                      } else {
                        this.displayModel(jsonStatue.reason,"failureIcon");  
                      } 
                  }   
                }
                
            },  
            error => {
              //Please Comment while checkIn
              /* let jsonStatue: any = {"sessionId":"506db794","reason":"Updation Failed","status":"SUCCESS","serviceToken":"63524"};
              setTimeout(() => { 
                this.showLoader = false;
                if(jsonStatue.status == "SUCCESS"){
                 this.message = "User details updated successfully !";
                this.successModalBlock = this.modalService.open(this.successModalRef,{keyboard: false, backdrop: "static",size:'lg',windowClass:"success-modal"});
                 // this.displayModel("User details updated successfully !","successIcon");
                  //this.createForm = false;
                  this.cancelEditRow(userDetails.id, '');
                } else {
                  this.displayModel(jsonStatue.reason,"failureIcon");  
                }
          
              }, 1000); */
              //Please Comment while checkIn
            });
        }
      }, 0);
  }

  onChnageRole(role){

      if (this.userGroupDetails != "Administrator" && this.userGroupDetails != "Commission Manager") {
          $("#customerName").val("");
          $("#networkType").val("");
          if (role == 'Super Administrator') {
              this.showProgramList = false;
              $("#customerName").attr("disabled", "disabled");
              $("#customerName").val("All");
              setTimeout(() => {
                  $("#programName1").attr("disabled", "disabled");
                  $("#programName1").val("All");
                  // this.validationData.rules.programName.required = false;
              });
              this.validationData.rules.customerName.required = false;
          } else {
              $("#customerName option[value='All']").prop("disabled", true);
              $("#customerName").removeAttr("disabled");
              if (role == 'Administrator') {
                  this.showProgramList = false;
                  setTimeout(() => {
                      $("#programName1").attr("disabled", "disabled");
                      $("#programName1").val("All");
                      // this.validationData.rules.programName.required = false;
                  });
              }
              else {
                  this.showProgramList = true;
                  // this.validationData.rules.programName.required = true;
              }
              /* $("#networkType option[value='All']").prop("disabled", true);
              $("#networkType").removeAttr("disabled"); */
              this.validationData.rules.customerName.required = true;
              //   this.validationData.rules.networkType.required = true;
          }
      }
  }

   /*
     * set pages on user clicks.
     * @param {number} - indicates page number.
     */
    setPage(page) {
        this.showLoader = true;
        setTimeout(() => {
            this.showLoader = false;
            if (page < 1 || page > this.totalPages) {
                return;
            }
            this.currentPage = page;
            //this.pageSwap();
            let paginationDetails = {
                "count": parseInt(this.pageSize),
                "page": page
            };

            this.paginationDetails = paginationDetails;
            this.pager = this.getPager(this.totalPages * this.pageSize, this.currentPage, this.pageRenge);
            this.tableShowHide = false;
            this.userManagementOnLoad();


        }, 0);
    };
    getPager(totalItems: number, currentPage: number = 1, pageSize: number = 10) {
        // calculate total pages
        let totalPages = Math.ceil(totalItems / this.pageSize);

        let startPage: number, endPage: number;
        if (totalPages <= 10) {
            // less than 10 total pages so show all
            startPage = 1;
            endPage = totalPages;
        } else {
            // more than 10 total pages so calculate start and end pages
            if (currentPage <= 6) {
                startPage = 1;
                endPage = 10;
            } else if (currentPage + 4 >= totalPages) {
                startPage = totalPages - 9;
                endPage = totalPages;
            } else {
                startPage = currentPage - 5;
                endPage = currentPage + 4;
            }
        }

        // calculate start and end item indexes
        let startIndex = (currentPage - 1) * pageSize;
        let endIndex = Math.min(startIndex + pageSize - 1, totalItems - 1);

        // create an array of pages to ng-repeat in the pager control
        let pages = _.range(startPage, endPage + 1);

        // return object with all pager properties required by the view
        return {
            totalItems: totalItems,
            currentPage: currentPage,
            pageSize: pageSize,
            totalPages: totalPages,
            startPage: startPage,
            endPage: endPage,
            startIndex: startIndex,
            endIndex: endIndex,
            pages: pages
        };
    }

    onChangeTableRowLength(event) {
        this.showLoader = true;
        this.pageSize = parseInt(event.target.value);

        this.currentPage = 1;

        let paginationDetails = {
            "count": this.pageSize,
            "page": this.currentPage
        };

        this.paginationDetails = paginationDetails;
        this.tableShowHide = false;        
        setTimeout(() => {
            this.showLoader = false;
            $("#dataWrapper").find(".scrollBody").scrollLeft(0);
            this.userManagementOnLoad();
        }, 0);

    }

    compareFn(o1: any, o2: any) {
      return o1 && o2 ? o1.id === o2.id : o1 === o2;
    }

  
}
