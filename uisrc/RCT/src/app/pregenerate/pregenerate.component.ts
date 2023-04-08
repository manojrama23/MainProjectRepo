import { ViewContainerRef, Input, ElementRef, Renderer, Component, OnInit, ViewChild, HostListener } from '@angular/core';
// import { Router} from '@angular/router';
import { Router, NavigationEnd } from '@angular/router';
import { NgbModal, ModalDismissReasons} from '@ng-bootstrap/ng-bootstrap';
import { PregenerateService } from '../services/pregenerate.service';
import { SharedService } from '../services/shared.service';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { filter, map, mergeMap} from 'rxjs/operators';
import { validator } from '../validation';
import { DatePipe } from '@angular/common';
import {platformBrowserDynamic} from '@angular/platform-browser-dynamic';
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';
import * as FileSaver from 'file-saver';
import * as $ from 'jquery';
import * as _ from 'underscore';

@Component({
  selector: 'rct-pregenerate',
  templateUrl: './pregenerate.component.html',
  styleUrls: ['./pregenerate.component.scss'],
  providers: [PregenerateService]
})
  export class PregenerateComponent implements OnInit { 
    emailId:any;
    dropdownSettings = {};
    dropdownSettingsNEs = {};
    showLoader:boolean = true;
    showInnerLoader: boolean;
    tableData:any;    
    closeResult:string;    
    noDataVisibility :boolean = false;
    showModelMessage: boolean = false;
    generateBlock: boolean = false;
    searchBlock: boolean = false;
    messageType: any;
    modelData :any;
    fileType: string ="COMMISSION_SCRIPT";
    fileName: any;
    enbName:any;
    remarksVal: any;
    sessionExpiredModalBlock : any; // Helps to close/open the model window
    successModalBlock : any;
    confirmModalBlock: any;
    message : any; 
    tableShowHide :boolean = false;
    tableDataHeight:any;
    max = new Date();
    fromDate:any;
    toDate:any;
    selectedNeVersion :any;
    selectedFSUType: any;
    searchCriteria: any;
    searchStatus: string;
    navigationSubscription: any;
    ciqNamesList: any =[];
    neNamesList: any =[];
    editableFormArray = [];
    pageCount: any; // for pagination
    currentPage: any; // for pagination
    pageSize: any; // for pagination
    totalPages: any; // for pagination
    TableRowLength: any; // for pagination
    paginationDetails: any; // for pagination
    pageRenge: any; // for pagination
    paginationDisabbled: boolean = false;    
    pager: any = {}; // pager Object
    radioBtnActive: any = {};
    nesListData: any;
    searchStartDate:any;
    searchEndDate:any;
    ciqNEFileDetails:any;
    ciqFileName:any;
    dropdownListNE = [];
    getNeslist:any=[];
    getNesitelist:any=[];
    errMessage:boolean= false;
    ciqListData;any;
    getCiqList:any=[];
    neDetails:any;
    searchCiqList:any;
    enbId:any
    remarks:any;
    viewModalBlock:any;
    validateDetails:any;
    ciqNameConfig:object;
    programChangeSubscription: any;
    networkType: any = "";
    programType: any = "";
    programName: any = "";
    nenamebutton:boolean =true;
    getNeIDslist: any = [];
    selectedNEIDs = [];
    neIdDropdownSettings = {};
    // generateAllSites: boolean;
    generateAllSites: boolean = false;
    showMySites: boolean = false;
    supportCA: boolean;
    ovUpdate: boolean;
    milestoneResult: any;
    historyResult: any;  
    userGroupDetails : any;
    errorMsgResult: any;
    successMsgResult: any;
    ovRetryData: any;
    failureMsgBlock: any;
    migrationStrategy: string = 'Legacy IP';
    

   validationData: any = {
          "rules": {
            "ciqName": {
              "required": true
            },
            "nes": {
              "required": true
            },
            /* "neIDs": {
              "required": false
            } */

          },
          "messages": {           
             "ciqName":{
                "required":"Please select CIQ Name"
            },
            "nes":{
                "required":"Please select NE"
            },
            /* "neIDs": {
              "required": "Please select NE ID"
            } */
          }        
    }; 
  @ViewChild('generateTab') generateTabRef: ElementRef;
  @ViewChild('searchTab') searchTabRef: ElementRef;  
  @ViewChild('confirmModal') confirmModalRef: ElementRef;
  @ViewChild('bluePrintForm') bluePrintForm;  
  @ViewChild('searchForm') searchForm;
  @ViewChild('sessionExpiredModal') sessionExpiredModalRef: ElementRef;
  @ViewChild('successModal') successModalRef: ElementRef;
  @ViewChild('viewModal') viewModalRef: ElementRef;
  dropdownSettingsNE: { singleSelection: boolean; idField: string; textField: string; selectAllText: string; unSelectAllText: string; itemsShowLimit: number; allowSearchFilter: boolean; };
  selectedItemsNE: any;


  
  constructor(
    private element: ElementRef,
    private renderer: Renderer,
    private router:Router,
    private modalService: NgbModal,
    private pregenerateService: PregenerateService,
    private sharedService: SharedService,
    private datePipe: DatePipe
    ) {
        /* this.navigationSubscription = this.router.events.subscribe((e: any) => {
            // If it is a NavigationEnd event re-initalise the component
            if (e instanceof NavigationEnd) {
                console.log("Constructor : " + e);
                this.ngOnInit();
            }
        }); */
    }

  ngOnInit() {
    this.loadInitialData();
      this.programChangeSubscription = this.sharedService.currentPgm.subscribe(pgm => {
          if (pgm) {
            // Clear Session storage for selectedCIQ on new program selection
            this.sharedService.updateSelectedCIQInSessionStorage(null);
            this.loadInitialData();
          }
      });
  }
  loadInitialData(){
    this.generateBlock =true;
    this.searchBlock = false;
    this.searchStatus = 'load';
    this.fromDate = null;
    this.toDate = null;
    this.setMenuHighlight("generate");
    this.currentPage = 1;
    this.totalPages = 1;
    this.TableRowLength = 10;
    this.pageSize = 10;
    this.programName = JSON.parse(sessionStorage.getItem("selectedProgram")).programName;
    this.selectedNeVersion = "";
    this.selectedFSUType = "";

    if(this.programName == "VZN-5G-MM" || this.programName == "VZN-5G-CBAND" || this.programName == "VZN-5G-DSS") {
      this.fileType = "ALL";
    }
    else if(this.programName == "VZN-4G-FSU"){
      this.fileType = "ENV";
    }
    else {
      this.fileType = "COMMISSION_SCRIPT";
    }

    // this.fileType="COMMISSION_SCRIPT";
    this.emailId  = JSON.parse(sessionStorage.loginDetails).emailId;
    //this.getCiqList="";
     let paginationDetails = {
          "count": parseInt(this.TableRowLength,10),
          "page": this.currentPage
      };
    this.paginationDetails = paginationDetails;  
  
        
    this.showLoader = true;
    this.getNeslist=[];
    this.neDetails =null;
    this.editableFormArray = [];
    this.ciqNameConfig = {
        displayKey: "ciqFileName", 
        search: true, 
        height: '200px',
        placeholder: '--Select--',
        customComparator: () => { },
        //limitTo: this.getCiqList.length,
        moreText: 'more',                  
        noResultsFound: 'No results found!',
        searchPlaceholder: 'Search', 
        searchOnKey: 'ciqFileName',
    }
    setTimeout(() => this.generateAllSites = true, 100);
    this.getCiqList = [];
    this.userGroupDetails = JSON.parse(sessionStorage.loginDetails).userGroup;
    this.ovRetryData = {};
    this.getGenerateDetails();
    this.getCiqListData(this.fromDate,this.toDate);
    setTimeout(() => {
      this.supportCA = false;
      this.ovUpdate = false;
      this.migrationStrategy = 'Legacy IP';
    }, 100);
    this.resetGenerateForm();
  }

  ngOnDestroy() {
    // avoid memory leaks here by cleaning up after ourselves. If we  
    // don't then we will continue to run our ngOnInit()   
    // method on every navigationEnd event.
    this.programChangeSubscription.unsubscribe();
    if (this.navigationSubscription) {
      this.navigationSubscription.unsubscribe();
    }
  }
  resetGenerateForm() {
      setTimeout(() => {
          this.bluePrintForm.nativeElement.reset();
      }, 0);
      validator.removeFormValidation("flightSearchFormWrapper", this.validationData);
  }
  generateTabBind() {
    this.currentPage = 1;
    this.setMenuHighlight("generate");
    this.noDataVisibility = false;
    this.tableShowHide = false;
    this.searchBlock = false;
    this.generateBlock = true;
    //this.showLoader = true;
    this.searchStatus = 'load';

    // CR_1 - Change for the requirement NE name should be selected on change of radio button 
    // this.ciqNEFileDetails =[];
    // this.getNeslist=[];
    // this.neDetails = [];

    this.remarks ="";

    let paginationDetails = {
        "count": parseInt( this.TableRowLength, 10 ),
        "page": this.currentPage
    };
    this.paginationDetails = paginationDetails;

    this.getGenerateDetails();
    // this.getCiqListData(this.fromDate,this.toDate);  //CR_1
    // this.selectedVersion = "";
    // this.selectedLsmName = "";
    this.editableFormArray = [];
   
}

searchTabBind() {
    this.currentPage = 1;
  let searchCrtra = { "fileName": "", "ciqFileName": "", "neName": "", "searchStartDate": "", "searchEndDate": "" };
  this.searchCriteria = searchCrtra;

  this.setMenuHighlight("search");
  this.searchBlock = true;
  this.generateBlock = false;
  this.searchStatus = 'load';
  this.tableShowHide = false;
  this.showMySites = false;
  this.supportCA = false;
  this.ovUpdate = false;
  this. getGenerateDetails();
  
  setTimeout(() => {
    this.bluePrintForm.nativeElement.reset();
  }, 0);
 /*  // Close if edit form is in open state
  if (this.currentEditRow != undefined) {
      this.currentEditRow.className = "editRow";
  } */
  this.editableFormArray = [];
 
}
    
searchGenerate(event) {

  setTimeout(() => {
      $("#dataWrapper").find(".scrollBody").scrollLeft(0);
  }, 0);

  
  setTimeout(() => {
      
          if (!event.target.classList.contains('buttonDisabled')) {
              this.showLoader = true;
              this.tableShowHide = false;

              // To hide the No Data Found and REMOVAL DETAILS Form
              this.generateBlock = false;
              this.noDataVisibility = false;

              let currentForm = event.target.parentNode.parentNode.parentNode,
                  searchCrtra = {
                      "fileName": currentForm.querySelector("#fileName").value,
                      "ciqFileName":currentForm.querySelector("#searchCiqName").value,
                      "siteName":currentForm.querySelector("#searchSiteName") ? currentForm.querySelector("#searchSiteName").value : "",
                      "neName": currentForm.querySelector("#searchNeName").value,
                      "searchStartDate": currentForm.querySelector("#searchStartDate").value,
                      "searchEndDate": currentForm.querySelector("#searchEndDate").value,
                      "userName": currentForm.querySelector("#searchUserName").value
                  };

              if (searchCrtra.fileName || searchCrtra.ciqFileName || searchCrtra.siteName || searchCrtra.neName || searchCrtra.searchStartDate || searchCrtra.searchEndDate || searchCrtra.userName) {
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
              this.getGenerateDetails();
          }
    
  }, 0);
}

onChangeLoad(value){
  this.fileType = value;
  if (this.generateBlock) {
    // this.bluePrintForm.nativeElement.reset();  //CR_1
    /*  this.ciqNEFileDetails =[];
     this.getNeslist=[]; */
    this.generateTabBind();
    /* this.selectedNEIDs = [];
    this.neDetails = null;
    this.getNeIDslist = []; */

    if(value == "ENDC" && this.networkType == "5G") {
      if(this.neDetails) {
        //Call to load 5g NE IDs
        this.onChangeNEs(null);
      }
    }
    else {
      this.selectedNEIDs = [];
    }
  } else if (this.searchBlock) {
    this.searchForm.nativeElement.reset();
    this.searchTabBind();
  }
    /* this.getCiqList = "";
    this.getNeslist = [];
    this.getGenerateDetails();
    this.getCiqListData(this.fromDate, this.toDate); */
}

clearSearchFrom() {
    this.bluePrintForm.nativeElement.reset();  
}

getCiqListData(fromDate,toDate){
    this.showLoader=true;
    let fromDt,toDt;
    this.ciqNEFileDetails = [];
    if(fromDate & toDate){
      fromDt = this.datePipe.transform(fromDate,"MM/dd/yyyy"); // On change Date,Update Row
      toDt = this.datePipe.transform(toDate,"MM/dd/yyyy");
      this.searchStatus = "search"; 
    }else{
      fromDt = null;//Loading Page fromDate and toDate is null
      toDt = null;
      this.searchStatus = "load";      
    }
    
    this.pregenerateService.getCiqListData(this.sharedService.createServiceToken(),fromDt,toDt,this.searchStatus)
    .subscribe(
        data => {
            // setTimeout(() => { 
              let jsonStatue = data.json();
              this.ciqListData = data.json();
              // this.showLoader = false;
                  if(jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User"){
                    if(!this.sessionExpiredModalBlock){
                        this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, {
                            keyboard: false,
                            backdrop: 'static',
                            size: 'lg',
                            windowClass: 'session-modal'
                        });
                    }
                  } else {
                    if( this.sharedService.isvalidServiceToken( parseInt(data.json().serviceToken,10) ) || data.json().sessionId == "408"){
                      if(jsonStatue.status == "SUCCESS"){
                        this.fromDate = new Date( this.ciqListData.fromDate);
                        this.toDate = new Date(this.ciqListData.toDate);
                        
                        this.getCiqList = this.ciqListData.getCiqList;
                        let getSelectedCIQ = JSON.parse(sessionStorage.getItem("selectedCIQ"));
                        if (this.getCiqList.length > 0 && getSelectedCIQ) {
                          this.ciqNEFileDetails = getSelectedCIQ;
                        }
                        else {
                          this.ciqNEFileDetails = this.getCiqList.length > 0 ? this.getCiqList[0] : null;
                          // Update Session storage for selectedCIQ
                          this.sharedService.updateSelectedCIQInSessionStorage(this.ciqNEFileDetails);
                        }
                        // this.ciqNEFileDetails = this.getCiqList.length > 0 ? this.getCiqList[0] : [];
                        this.getNEdata();

                      }else{
                        this.showLoader = false;
                        this.getCiqList = [];
                      }
                     
                    this.neDetails =null;
                     }   
                  }
                                
            // }, 1000);
        },
        error => {
          //Please Comment while checkIn
         /*  setTimeout(() => { 
            this.showLoader = false;
            //this.ciqListData = {"sessionId":"b98fb19a","serviceToken":"52298","getCiqList":[{"id":27,"fileName":"FDMIMO_FIT_CIQ_08242018_SprintTemplate2.xlsx","uploadBy":"admin","customerId":2,"remarks":"Kamlesh is testing","creationDate":"2018-12-24T10:17:16.000+0000","lsmVersion":"3","networkType":"4G"},{"id":28,"fileName":"FDMIMO_FIT_CIQ_08242018_SprintTemplate_extra1.xlsx","uploadBy":"superadmin","customerId":2,"remarks":"Superadmin","creationDate":"2018-12-24T11:39:07.000+0000","lsmVersion":"LSM25","networkType":"4G"}],"status":"SUCCESS"};
            this.ciqListData ={"fromDate":"05/19/2020","toDate":"05/26/2020","sessionId":"f998f98d","serviceToken":"70369","getCiqList":[{"id":116,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2019-11-22T06:36:02.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"ciqFileName":"UNY-NE-VZ_CIQ_Ver3.6.75_05222020.xlsx","ciqFilePath":"Customer/34/PreMigration/Input/UNY-NE-VZ_CIQ_Ver3.6.75_05222020/CIQ/","scriptFileName":"70215.zip","scriptFilePath":"Customer/34/PreMigration/Input/UNY-NE-VZ_CIQ_Ver3.6.75_05222020/SCRIPT/","checklistFileName":"Verizon_USM_Migration Checklist_1.1.5(2).xlsx","checklistFilePath":"Customer/34/PreMigration/Input/UNY-NE-VZ_CIQ_Ver3.6.75_05222020/CHECKLIST/","ciqVersion":"ORIGINAL","fileSourceType":"UPLOAD","uploadBy":"superadmin","remarks":"","creationDate":"2020-05-25T17:22:28.000+0000"},{"id":115,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2019-11-22T06:36:02.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"ciqFileName":"UNY-NE-VZ_CIQ_Ver3.6.74_05212020.xlsx","ciqFilePath":"Customer/34/PreMigration/Input/UNY-NE-VZ_CIQ_Ver3.6.74_05212020/CIQ/","scriptFileName":"70215.zip","scriptFilePath":"Customer/34/PreMigration/Input/UNY-NE-VZ_CIQ_Ver3.6.74_05212020/SCRIPT/","checklistFileName":"Verizon_USM_Migration Checklist_1.1.5(2).xlsx","checklistFilePath":"Customer/34/PreMigration/Input/UNY-NE-VZ_CIQ_Ver3.6.74_05212020/CHECKLIST/","ciqVersion":"ORIGINAL","fileSourceType":"UPLOAD","uploadBy":"superadmin","remarks":"","creationDate":"2020-05-21T17:40:32.000+0000"}],"status":"SUCCESS"};
              if(this.ciqListData.sessionId == "408" || this.ciqListData.status == "Invalid User"){
                 this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                }
              if(this.ciqListData.status == "SUCCESS"){
                this.fromDate = new Date( this.ciqListData.fromDate);
                this.toDate = new Date(this.ciqListData.toDate);                
                this.getCiqList = this.ciqListData.getCiqList;
                let getSelectedCIQ = JSON.parse(sessionStorage.getItem("selectedCIQ"));
                if(this.getCiqList.length > 0 && getSelectedCIQ) {
                  this.ciqNEFileDetails = getSelectedCIQ;
                }
                else {
                  this.ciqNEFileDetails = this.getCiqList.length > 0 ? this.getCiqList[0] : [];
                  // Update Session storage for selectedCIQ
                  this.sharedService.updateSelectedCIQInSessionStorage(this.ciqNEFileDetails);
                }
                // this.ciqNEFileDetails = this.getCiqList.length > 0 ? this.getCiqList[0] : [];
                this.getNEdata();
              }else{
                this.showLoader = false;
                this.getCiqList = [];
              }             
                this.neDetails =null;
          }, 1000); */
          //Please Comment while checkIn
        });
  }

  getGenerateDetails(){
 
    this.tableShowHide = false;
    
    this.showLoader = true;
    $("#dataWrapper").find(".scrollBody").scrollLeft(0);
    this.pregenerateService.getGenerateDetails(this.searchStatus, this.searchCriteria, this.sharedService.createServiceToken(),this.paginationDetails, this.fileType, this.showMySites)
          .subscribe(
              data => {
                  setTimeout(() => {
                    let jsonStatue = data.json();
                    this.showLoader = false;
                    
                        if(jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User"){                       
                            if(!this.sessionExpiredModalBlock){
                                this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, {
                                    keyboard: false,
                                    backdrop: 'static',
                                    size: 'lg',
                                    windowClass: 'session-modal'
                                });
                            }
                       
                        } else {
  
                          if( this.sharedService.isvalidServiceToken( parseInt(data.json().serviceToken,10) ) || data.json().sessionId == "408"){
                            if(jsonStatue.status == "SUCCESS"){
                              this.tableData = jsonStatue;
                              
                              this.programType = this.tableData.programType;
                                if (this.tableData.programGenerateFileDetails && this.tableData.programGenerateFileDetails.length > 0) {
                                  this.networkType = JSON.parse(sessionStorage.getItem("selectedProgram")).networkTypeDetailsEntity.networkType; //this.tableData.programGenerateFileDetails[0].programDetailsEntity.networkTypeDetailsEntity.networkType;
                                  /* if(this.networkType == '5G' && this.fileType == 'ENDC') {
                                    this.validationData.rules.neIDs["required"] = true;
                                  }
                                  else {
                                    this.validationData.rules.neIDs["required"] = false;
                                  } */
                                  this.radioBtnActive = {
                                    "env" : this.tableData.programGenerateFileDetails[0].env == 'Active' ? true : false,
                                    "csv": this.tableData.programGenerateFileDetails[0].csv == 'Active' ? true : false,
                                    "commissionScript": this.tableData.programGenerateFileDetails[0].commissionScript == 'Active' ? true : false,
                                    "all": this.tableData.programGenerateFileDetails[0].all == 'Active' ? true : false,
                                    "endc": this.tableData.programGenerateFileDetails[0].endc == 'Active' ? true : false
                                  }
                                }
                                //this.ciqNamesList = this.tableData.ciqFileName;
                                //this.neNamesList = this.tableData.neName;
                                
                               this.totalPages = this.tableData.pageCount;
                                  let pageCount = [];
                                  for (var i = 1; i <= this.tableData.pageCount; i++) {
                                      pageCount.push(i);
                                  }
                                  this.pageRenge = pageCount;
                                  this.pager = this.getPager(this.totalPages*this.pageSize, this.currentPage, this.pageRenge);
  
                              if(this.tableData.csvAuditTrailDetModels.length == 0){
                                this.tableShowHide = false;
                                this.noDataVisibility = true;
                              }else{
                                this.searchCiqList = this.tableData.ciqList;
                                this.tableShowHide = true;
                                 this.noDataVisibility = false;
                                 setTimeout(() => {
                                  let tableWidth = document.getElementById('csvAuditTrailDetModels').scrollWidth;
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
  
                           }else{
                            this.showLoader = false;
                            this.displayModel(jsonStatue.reason,"failureIcon");  
                           }   
                        }
                                      
                  }, 1000);
              },
              error => {
                //Please Comment while checkIn
                /* setTimeout(() => {
                  this.showLoader = false;
                  //this.tableData = {"generateDetails":[{"id":1,"fileName":"FDMIMO_FIT_CIQ_08242018_SprintTemplate.xlsx","status":"COMPLETED","remarks":"uploaded successfull","creationDate":"2018-10-31 14:41:50"},{"id":1,"fileName":"FDMIMO_FIT_CIQ_08242018_SprintTemplate.xlsx","status":"COMPLETED","remarks":"uploaded successfull","creationDate":"2018-10-31 14:41:50"},{"id":1,"fileName":"FDMIMO_FIT_CIQ_08242018_SprintTemplate.xlsx","status":"FAILED","remarks":"uploaded successfull","creationDate":"2018-10-31 14:41:50"},{"id":1,"fileName":"FDMIMO_FIT_CIQ_08242018_SprintTemplate.xlsx","status":"IN PROGRESS","remarks":"uploaded successfull","creationDate":"2018-10-31 14:41:50"},{"id":1,"fileName":"FDMIMO_FIT_CIQ_08242018_SprintTemplate.xlsx","uploadBy":"admin","remarks":"uploaded successfull","creationDate":"2018-10-31 14:41:50"},{"id":1,"fileName":"FDMIMO_FIT_CIQ_08242018_SprintTemplate.xlsx","uploadBy":"admin","remarks":"uploaded successfull","creationDate":"2018-10-31 14:41:50"},{"id":1,"fileName":"FDMIMO_FIT_CIQ_08242018_SprintTemplate.xlsx","uploadBy":"admin","remarks":"uploaded successfull","creationDate":"2018-10-31 14:41:50"},{"id":1,"fileName":"FDMIMO_FIT_CIQ_08242018_SprintTemplate.xlsx","uploadBy":"admin","remarks":"uploaded successfull","creationDate":"2018-10-31 14:41:50"},{"id":1,"fileName":"FDMIMO_FIT_CIQ_08242018_SprintTemplate.xlsx","uploadBy":"admin","remarks":"uploaded successfull","creationDate":"2018-10-31 14:41:50"},{"id":1,"fileName":"abce.xlsx","uploadBy":"admin","remarks":"uploaded successfull","creationDate":"2018-10-31 14:41:50"},{"id":1,"fileName":"xyze.xlsx","uploadBy":"admin","remarks":"uploaded successfull","creationDate":"2018-10-31 14:41:50"}],"sessionId":"15f815ee","serviceToken":"51615","status":"SUCCESS","pageCount":2};
                //    this.tableData = {"sessionId":"458e9e98","serviceToken":"68416","status":"SUCCESS","ciqName":["CIQ_1"],"pageCount":1,"csvfileName":["CSV-1"],"csvAuditTrailDetModels":[{"id":1,"csvFileName":"CSV-1","csvFilePath":"/home/user","customerId":null,"generatedBy":"Supriya","status":"Active","lsmVersion":null,"networkTypeId":null,"networkType":null,"generationDate":"2019-02-14 12:19:50","programName":null,"neName":"LSM","ciqFileName":"CIQ_1","remarks":"TEST","programDetailsEntity":null,"searchStartDate":null,"searchEndDate":null}],"neName":["LSM"]};         
                  // this.tableData=  {"pageCount":1,"csvfileName":["CSV-1"],"csvAuditTrailDetModels":[{"id":1,"csvFileName":"CSV-1","csvFilePath":"/home/user","customerId":null,"generatedBy":"Supriya","status":"Active","lsmVersion":null,"networkTypeId":null,"networkType":null,"generationDate":"2019-02-14 12:19:50","programName":null,"neName":"LSM","ciqFileName":"CIQ_1","remarks":"TEST","programDetailsEntity":null,"searchStartDate":null,"searchEndDate":null}],"ciqName":["CIQ_1"],"neName":["LSM"],"sessionId":"10b604ea","serviceToken":"88033","status":"SUCCESS"}; 
                  ////this.tableData = {"searchStartDate":"02/21/2019","programGenerateFileDetails":[{"id":1,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"env":"Inactive","csv":"Inactive","commissionScript":"Active"}],"pageCount":0,"csvfileName":[],"csvAuditTrailDetModels":[],"ciqName":[],"searchEndDate":"02/28/2019","neName":[],"sessionId":"897f06b3","serviceToken":"53559","status":"SUCCESS"};
                //this.tableData = {"sessionId":"897f06b3","serviceToken":"53559","searchStartDate":"02/21/2019","programGenerateFileDetails":[{"id":1,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"env":"Inactive","csv":"Inactive","commissionScript":"Active"}],"pageCount":0,"csvfileName":[],"csvAuditTrailDetModels":[],"ciqName":["sfesdfe","sfsedf","sdfsdfe"],"searchEndDate":"02/28/2019","neName":["sfsdfed","sedfsfsd","sefffes"],"status":"SUCCESS"};
                // this.tableData = {"searchStartDate":"04/28/2021","programGenerateFileDetails":[{"id":47,"programDetailsEntity":{"id":38,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-FSU","programDescription":"FSU","status":"Active","creationDate":"2020-06-05T01:33:48.000+0000","createdBy":"superadmin","sourceProgramId":34,"sourceprogramName":"VZN-4G-USM-LIVE"},"env":"Active","csv":"Active","commissionScript":"InActive","endc":"","all":""}],"programType":"FSU","pageCount":0,"csvAuditTrailDetModels":[],"searchEndDate":"05/05/2021","ciqList":["20200610_VZW_NE_850NR_CIQ.xlsx","VZW_UPNY_850NR_CIQ_0612.xlsx","VZW_NE_850NR_CIQ_070820.xlsx","20200420_VZW_NE_850NR_CIQ.xlsx"],"sessionId":"1e573a3b","serviceToken":"66578","status":"SUCCESS"};
                this.tableData ={"searchStartDate":"03/24/2021","programGenerateFileDetails":[{"id":49,"programDetailsEntity":{"id":40,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-MM","programDescription":"MM","status":"Active","creationDate":"2020-08-27T06:50:11.000+0000","createdBy":"superadmin","sourceProgramId":34,"sourceprogramName":"VZN-4G-USM-LIVE"},"env":"Active","csv":"Active","commissionScript":"Active","endc":"Active","all":"Active"}],"pageCount":2,"csvAuditTrailDetModels":[{"id":15747,"programDetailsEntity":{"id":40,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-MM","programDescription":"MM","status":"Active","creationDate":"2020-08-27T06:50:11.000+0000","createdBy":"superadmin","sourceProgramId":34,"sourceprogramName":"VZN-4G-USM-LIVE"},"fileName":"ALL_05887195555_5GDU_BOS_ARLINGTON_E_055_MA-WSBO_03312021_07_06_32.zip","filePath":"Customer/40/PreMigration/Output/VZW_5GNR_mmWave_NewEngland/AU/ALL/20A/5887195555/","fileType":"ALL","ciqFileName":"VZW_5GNR_mmWave_NewEngland.xlsx","neName":"05887195555_5GDU_BOS_ARLINGTON_E_055_MA-WSBO","generatedBy":"superadmin","ovUpdateStatus":"Failure","generationDate":"2021-03-31 07:06:32","searchStartDate":null,"searchEndDate":null,"remarks":"Generated in Ran Config","siteName":"Flo"},{"id":15743,"programDetailsEntity":{"id":40,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-MM","programDescription":"MM","status":"Active","creationDate":"2020-08-27T06:50:11.000+0000","createdBy":"superadmin","sourceProgramId":34,"sourceprogramName":"VZN-4G-USM-LIVE"},"fileName":"ALL_05887195554_5GDU_BOS_ARLINGTON_E_055_MA-WSBO_03312021_07_06_28.zip","filePath":"Customer/40/PreMigration/Output/VZW_5GNR_mmWave_NewEngland/AU/ALL/20A/5887195554/","fileType":"ALL","ciqFileName":"VZW_5GNR_mmWave_NewEngland.xlsx","neName":"05887195554_5GDU_BOS_ARLINGTON_E_055_MA-WSBO","generatedBy":"superadmin","ovUpdateStatus":"Failure","generationDate":"2021-03-31 07:06:28","searchStartDate":null,"searchEndDate":null,"remarks":"Generated in Ran Config","siteName":"Flo"},{"id":15739,"programDetailsEntity":{"id":40,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-MM","programDescription":"MM","status":"Active","creationDate":"2020-08-27T06:50:11.000+0000","createdBy":"superadmin","sourceProgramId":34,"sourceprogramName":"VZN-4G-USM-LIVE"},"fileName":"ALL_5887195555_5GDU_BOS_ARLINGTON_E_055_MA-WSBO_03312021_07_05_04.zip","filePath":"Customer/40/PreMigration/Output/VZW_5GNR_mmWave_NewEngland/AU/ALL/20A/5887195555/","fileType":"ALL","ciqFileName":"VZW_5GNR_mmWave_NewEngland.xlsx","neName":"5887195555_5GDU_BOS_ARLINGTON_E_055_MA-WSBO","generatedBy":"superadmin","ovUpdateStatus":"Success","generationDate":"2021-03-31 07:05:04","searchStartDate":null,"searchEndDate":null,"remarks":"","siteName":"Flo"},{"id":15738,"programDetailsEntity":{"id":40,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-MM","programDescription":"MM","status":"Active","creationDate":"2020-08-27T06:50:11.000+0000","createdBy":"superadmin","sourceProgramId":34,"sourceprogramName":"VZN-4G-USM-LIVE"},"fileName":"ALL_05887195554_5GDU_BOS_ARLINGTON_E_055_MA-WSBO_03312021_07_05_00.zip","filePath":"Customer/40/PreMigration/Output/VZW_5GNR_mmWave_NewEngland/AU/ALL/20A/5887195554/","fileType":"ALL","ciqFileName":"VZW_5GNR_mmWave_NewEngland.xlsx","neName":"05887195554_5GDU_BOS_ARLINGTON_E_055_MA-WSBO","generatedBy":"superadmin","generationDate":"2021-03-31 07:05:00","searchStartDate":null,"searchEndDate":null,"remarks":"","siteName":"Flo"},{"id":15722,"programDetailsEntity":{"id":40,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-MM","programDescription":"MM","status":"Active","creationDate":"2020-08-27T06:50:11.000+0000","createdBy":"superadmin","sourceProgramId":34,"sourceprogramName":"VZN-4G-USM-LIVE"},"fileName":"ALL_6186485382_5GDU_WSBO_OFFICE_03252021_04_48_36.zip","filePath":"Customer/40/PreMigration/Output/VZW_5GNR_mmWave_NewEngland/AU/ALL/20A/6186485382/","fileType":"ALL","ciqFileName":"VZW_5GNR_mmWave_NewEngland.xlsx","neName":"6186485382_5GDU_WSBO_OFFICE","generatedBy":"admin","generationDate":"2021-03-25 04:48:36","searchStartDate":null,"searchEndDate":null,"remarks":"","siteName":null},{"id":15721,"programDetailsEntity":{"id":40,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-MM","programDescription":"MM","status":"Active","creationDate":"2020-08-27T06:50:11.000+0000","createdBy":"superadmin","sourceProgramId":34,"sourceprogramName":"VZN-4G-USM-LIVE"},"fileName":"ALL_5887195557_5GDU_BOS_ARLINGTON_E_058_MA-WSBO_03252021_04_15_01.zip","filePath":"Customer/40/PreMigration/Output/VZW_5GNR_mmWave_NewEngland/AU/ALL/20C/5887195557/","fileType":"ALL","ciqFileName":"VZW_5GNR_mmWave_NewEngland.xlsx","neName":"5887195557_5GDU_BOS_ARLINGTON_E_058_MA-WSBO","generatedBy":"admin","generationDate":"2021-03-25 04:15:01","searchStartDate":null,"searchEndDate":null,"remarks":"Generated in Ran Config","siteName":null},{"id":15717,"programDetailsEntity":{"id":40,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-MM","programDescription":"MM","status":"Active","creationDate":"2020-08-27T06:50:11.000+0000","createdBy":"superadmin","sourceProgramId":34,"sourceprogramName":"VZN-4G-USM-LIVE"},"fileName":"ALL_5887195556_5GDU_BOS_ARLINGTON_E_058_MA-WSBO_03252021_04_14_58.zip","filePath":"Customer/40/PreMigration/Output/VZW_5GNR_mmWave_NewEngland/AU/ALL/20C/5887195556/","fileType":"ALL","ciqFileName":"VZW_5GNR_mmWave_NewEngland.xlsx","neName":"5887195556_5GDU_BOS_ARLINGTON_E_058_MA-WSBO","generatedBy":"admin","generationDate":"2021-03-25 04:14:58","searchStartDate":null,"searchEndDate":null,"remarks":"Generated in Ran Config","siteName":null},{"id":15713,"programDetailsEntity":{"id":40,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-MM","programDescription":"MM","status":"Active","creationDate":"2020-08-27T06:50:11.000+0000","createdBy":"superadmin","sourceProgramId":34,"sourceprogramName":"VZN-4G-USM-LIVE"},"fileName":"ALL_5887195556_5GDU_BOS_ARLINGTON_E_058_MA-WSBO_03242021_10_12_27.zip","filePath":"Customer/40/PreMigration/Output/VZW_5GNR_mmWave_NewEngland/AU/ALL/20C/5887195556/","fileType":"ALL","ciqFileName":"VZW_5GNR_mmWave_NewEngland.xlsx","neName":"5887195556_5GDU_BOS_ARLINGTON_E_058_MA-WSBO","generatedBy":"superadmin","generationDate":"2021-03-24 10:12:27","searchStartDate":null,"searchEndDate":null,"remarks":"","siteName":null},{"id":15712,"programDetailsEntity":{"id":40,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-MM","programDescription":"MM","status":"Active","creationDate":"2020-08-27T06:50:11.000+0000","createdBy":"superadmin","sourceProgramId":34,"sourceprogramName":"VZN-4G-USM-LIVE"},"fileName":"ALL_5887195555_5GDU_BOS_ARLINGTON_E_055_MA-WSBO_03242021_10_11_08.zip","filePath":"Customer/40/PreMigration/Output/VZW_5GNR_mmWave_NewEngland/AU/ALL/20A/5887195555/","fileType":"ALL","ciqFileName":"VZW_5GNR_mmWave_NewEngland.xlsx","neName":"5887195555_5GDU_BOS_ARLINGTON_E_055_MA-WSBO","generatedBy":"superadmin","generationDate":"2021-03-24 10:11:08","searchStartDate":null,"searchEndDate":null,"remarks":"","siteName":null},{"id":15711,"programDetailsEntity":{"id":40,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-MM","programDescription":"MM","status":"Active","creationDate":"2020-08-27T06:50:11.000+0000","createdBy":"superadmin","sourceProgramId":34,"sourceprogramName":"VZN-4G-USM-LIVE"},"fileName":"ALL_5887195555_5GDU_BOS_ARLINGTON_E_055_MA-WSBO_03242021_09_53_52.zip","filePath":"Customer/40/PreMigration/Output/VZW_5GNR_mmWave_NewEngland/AU/ALL/20A/5887195555/","fileType":"ALL","ciqFileName":"VZW_5GNR_mmWave_NewEngland.xlsx","neName":"5887195555_5GDU_BOS_ARLINGTON_E_055_MA-WSBO","generatedBy":"superadmin","generationDate":"2021-03-24 09:53:52","searchStartDate":null,"searchEndDate":null,"remarks":"","siteName":null}],"searchEndDate":"03/31/2021","ciqList":["VZW_5GNR_CIQ_Houston_06252020_v35.6.xlsx","VZW_5GNR_mmWave_NewEngland_CIQ_020121_v53.8 (2).xlsx","VZW_5GNR_CIQ_Boston_Providence_WB_Hartford_07232020_v36.4.xlsx","VZW_5GNR_CIQ_UPNY_AllMarkets_092520_v3.4.xlsx","VZW_5GNR_mmWave_NewEngland_MODIFIED.xlsx","VZW_5GNR_CIQ_Sacramento_11162020_v30.3.xlsx","VZW_5GNR_CIQ_Houston_09162020_v38.1.xlsx","VZW_5GNR_CIQ_UPNY_AllMarkets_092520_v3.4_MODIFIED.xlsx","VZW_5GNR_CIQ_Sacramento_01132020_v7.xlsx","VZW_TriState_mmWave_SNAP_CIQ_022521_v1.1.xlsx","VZW_NR_mmWave_NewEngland_CIQ_020221_v54.xlsx","VZW_5GNR_CIQ_Pensacola_080520_v4.xlsx","VZW_5GNR_CIQ_UPNY_AllMarkets_012821_v7 1_ref.xlsx","VZW_5GNR_mmWave_NewEngland_CIQ_020221_v54(1).xlsx","VZW_5GNR_CIQ_Pensacola_100620_v10_v2 .xlsx","VZW_5GNR_CIQ_UPNY_AllMarkets_072920_v0.5_MODIFIED.xlsx","VZW_5GNR_CIQ_Sacramento_09302020_v17.xlsx","VZW_5GNR_CIQ_UPNY_AllMarkets_072920_v0.5_Modified.xlsx","VZW_5GNR_CIQ_UPNY_AllMarkets_072920_v0.5.xlsx","VZW_5GNR_CIQ_UPNY_AllMarkets_072920_v0.5_Modified (1).xlsx","VZW_5GNR_CIQ_Pensacola_090920_v8.xlsx","VZW_5GNR_CIQ_Pensacola_100720_v10.1.xlsx","VZW_5GNR_CIQ_Boston_Providence_WB_Hartford_09102020_v38.3.xlsx","VZW_5GNR_CIQ_NOLA_09102020_v8.xlsx","VZW_5GNR_CIQ_UPNY_AllMarkets_070920_v0.1.xlsx","VZW_5GNR_CIQ_Houston_09092020_v37.9.xlsx","VZW_5GNR_CIQ_UPNY_AllMarkets_091120_v2.9.xlsx","VZW_5GNR_CIQ_NOLA_09182020_v10.xlsx","VZW_TriState_mmWave_SNAP_CIQ_022521_v1.1_MODIFIED.xlsx","VZW_5GNR_mmWave_NewEngland.xlsx"],"sessionId":"c59c4ce3","serviceToken":"74021","status":"SUCCESS"};
                // this.programName = JSON.parse(sessionStorage.getItem("selectedProgram")).programName;

                this.programType = this.tableData.programType;
                this.totalPages = this.tableData.pageCount;
                     if(this.tableData.programGenerateFileDetails && this.tableData.programGenerateFileDetails.length > 0) {
                        this.networkType = JSON.parse(sessionStorage.getItem("selectedProgram")).networkTypeDetailsEntity.networkType; //this.tableData.programGenerateFileDetails[0].programDetailsEntity.networkTypeDetailsEntity.networkType;
                        // if(this.networkType == '5G' && this.fileType == 'ENDC') {
                        //   this.validationData.rules.neIDs["required"] = true;
                        // }
                        // else {
                        //   this.validationData.rules.neIDs["required"] = false;
                        // }
                        this.radioBtnActive = {
                            "env" : this.tableData.programGenerateFileDetails[0].env == 'Active' ? true : false,
                            "csv": this.tableData.programGenerateFileDetails[0].csv == 'Active' ? true : false,
                            "commissionScript": this.tableData.programGenerateFileDetails[0].commissionScript == 'Active' ? true : false,
                            "all": this.tableData.programGenerateFileDetails[0].all == 'Active' ? true : false,
                            "endc": this.tableData.programGenerateFileDetails[0].endc == 'Active' ? true : false
                        }
                     }
                      let pageCount = [];
                      for (var i = 1; i <= this.tableData.pageCount; i++) {
                          pageCount.push(i);
                      }
                      this.pageRenge = pageCount;
                      this.pager = this.getPager(this.totalPages*this.pageSize, this.currentPage, this.pageRenge);
                      //this.ciqNamesList = this.tableData.ciqFileName;
                      //this.neNamesList = this.tableData.neName;
                    if(this.tableData.sessionId == "408" || this.tableData.status == "Invalid User"){
                         this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                        }
                   
                    
                      if(this.tableData.csvAuditTrailDetModels.length == 0){
                        this.tableShowHide = false;
                        this.noDataVisibility = true;
                      }else{
                        this.searchCiqList = this.tableData.ciqList;
                        
                        this.tableShowHide = true;
                        this.noDataVisibility = false;
                        setTimeout(() => {
                          let tableWidth = document.getElementById('csvAuditTrailDetModels').scrollWidth;
                          
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
 
  downLoadFile(fileName,filePath) {

    this.pregenerateService.downloadFile(fileName,filePath,this.sharedService.createServiceToken())
    .subscribe(
        data => {
            let blob = new Blob([data["_body"]], {
                type: "application/octet-stream"
            });

            FileSaver.saveAs(blob,fileName);              

        },
        error => {
            //Please Comment while checkIn
          /*   let jsonStatue: any = {"sessionId":"506db794","reason":"Download Failed","status":"SUCCESS","serviceToken":"63524"};
                let blob = new Blob([jsonStatue["_body"]], {
                    type: "application/octet-stream"
                });

                FileSaver.saveAs(blob,fileName); 
                setTimeout(() => { 
                this.showLoader = false;
                if(jsonStatue.status == "SUCCESS"){
                
                } else {
                this.displayModel(jsonStatue.reason,"failureIcon");  
                }
        
            }, 1000); */
            //Please Comment while checkIn
        });

  }

  mailFile(event, mailPopUp, key, index) { 
    this.modalService.open(mailPopUp, {keyboard: false,backdrop: 'static',size: 'lg',windowClass: 'confirm-modal mail-modal'})
    .result.then((result) => {
        this.closeResult = `Closed with: ${result}`;
    }, (reason) => {    
        this.showLoader = true;    
        this.pregenerateService.mailCiqFile(key, this.emailId, this.sharedService.createServiceToken())
            .subscribe(
                data => {
                    let jsonStatue = data.json();
                    this.showLoader = false;    
                    if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {    
                        this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, {
                            keyboard: false,
                            backdrop: 'static',
                            size: 'lg',
                            windowClass: 'session-modal'
                        });    
                    } else {    
                        if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {    
                            if (jsonStatue.status == "SUCCESS") {
                                this.displayModel(jsonStatue.reason, "successIcon");
                            } else {
                              this.displayModel(jsonStatue.reason, "failureIcon");
                            }
                        }
                    }    
                },
                error => {
                    //Please Comment while checkIn
                    /* setTimeout(() => {                  
                      this.showLoader = false;                  
                      let jsonStatue = {"reason":"Mail Sent Successfully","sessionId":"5ad21e7a","serviceToken":"50471","status":"SUCCESS"};                  
                      if (jsonStatue.status == "SUCCESS") {
                          this.displayModel(jsonStatue.reason, "successIcon");
                      } else {
                        this.displayModel(jsonStatue.reason, "failureIcon");
                      }
                    }, 1000); */
                    //Please Comment while checkIn
                });
    });



}
  getNEdata(updateSessionStorage = false){ 
    if(this.ciqNEFileDetails)
    {   
    this.showLoader = true;
    this.neDetails = null;
    // Update the sessionStorage selected CIQ if CIQ list is getting changed from UI dropdown
    updateSessionStorage ? this.sharedService.updateSelectedCIQInSessionStorage(this.ciqNEFileDetails) : "";
    if(this.programName != 'VZN-5G-MM'){
    this.pregenerateService.getNeListData(this.ciqNEFileDetails.ciqFileName, this.sharedService.createServiceToken() )
        .subscribe(
            data => {
                // setTimeout(() => { 
                  let jsonStatue = data.json();
                  this.nesListData = data.json();
                  this.showLoader = false;
                      if(jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User"){
                       this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                      } else {
                        if( this.sharedService.isvalidServiceToken( parseInt(data.json().serviceToken,10) ) || data.json().sessionId == "408"){
                          if(this.nesListData.status == "SUCCESS"){
                            this.getNeslist = this.nesListData.eNBList;
                            /* this.getNeslist = [];                             
                            let getNeslistStore = this.nesListData.eNBList;
                            for (let itm of getNeslistStore) {
                              let dropdownList = { item_id: itm.eNBId, item_text: itm.eNBName };
                              this.getNeslist.push(dropdownList);
                          } */
                          this.dropdownSettings = {
                              singleSelection: true,
                              idField: 'item_id',
                              textField: 'item_text',
                              itemsShowLimit: 1,
                              allowSearchFilter: true,
                              searchPlaceholderText:"Search",
                              clearSearchFilter:true,
                              maxHeight: 300
                          }; 
                          }else{
                            this.showLoader = false;
                            this.getNeslist = [];
                          }
                         }   
                      }
                                    
                // }, 1000);
            },
            error => {
              //Please Comment while checkIn
             /*  setTimeout(() => { 
                this.showLoader = false;
                this.nesListData = JSON.parse('{"eNBList":[{"eNBName":"061192_NORTHWOOD_LAKE_NH","eNBId":"61192"},{"eNBName":"061452_CONCORD_2_NH_HUB","eNBId":"61452"},{"eNBName":"073461_PRATTSBURGH","eNBId":"73461"},{"eNBName":"073462_East_Corning","eNBId":"73462"},{"eNBName":"073466_Howard","eNBId":"73466"},{"eNBName":"073474_Hornellsville","eNBId":"73474"},{"eNBName":"073484_ADDISON","eNBId":"73484"},{"eNBName":"072409_Press_Building","eNBId":"72409"},{"eNBName":"072412_Binghamton_DT","eNBId":"72412"},{"eNBName":"072413_SUNY_Binghamton","eNBId":"72413"},{"eNBName":"072415_Vestal","eNBId":"72415"},{"eNBName":"072416_Chenango","eNBId":"72416"},{"eNBName":"072417_Kirkwood","eNBId":"72417"},{"eNBName":"072419_Windsor","eNBId":"72419"},{"eNBName":"072424_CASTLE_CREEK","eNBId":"72424"},{"eNBName":"072425_Killawog","eNBId":"72425"},{"eNBName":"072426_East_Richford","eNBId":"72426"},{"eNBName":"072427_Caroline","eNBId":"72427"},{"eNBName":"072430_Owego_North","eNBId":"72430"},{"eNBName":"072431_Owego","eNBId":"72431"},{"eNBName":"072432_Apalachin","eNBId":"72432"},{"eNBName":"072433_Nichols","eNBId":"72433"},{"eNBName":"072442_CROCKER_CREEK","eNBId":"72442"},{"eNBName":"072443_MAINE_DT","eNBId":"72443"},{"eNBName":"072451_BELDEN","eNBId":"72451"},{"eNBName":"072452_TIOGA_CENTER","eNBId":"72452"},{"eNBName":"072454_CATATONK","eNBId":"72454"},{"eNBName":"072458_CHENANGO_DT","eNBId":"72458"},{"eNBName":"072478_Big_Flats","eNBId":"72478"},{"eNBName":"070033_POWERS_RD","eNBId":"70033"},{"eNBName":"070005_RTE_263_GETZVILLE","eNBId":"70005"},{"eNBName":"070562_BOWEN_RD","eNBId":"70562"},{"eNBName":"073313_FREY_RD","eNBId":"73313"},{"eNBName":"073326_BAKER_RD","eNBId":"73326"}],"sessionId":"2a7a3636","serviceToken":"79044","status":"SUCCESS"}');
                  if(this.nesListData.sessionId == "408" || this.nesListData.status == "Invalid User"){
                     this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                    }
                  if(this.nesListData.status == "SUCCESS"){
                    this.getNeslist = this.nesListData.eNBList;
                  //   this.getNeslist = [];                             
                  //   let getNeslistStore = this.nesListData.eNBList;
                  //   for (let itm of getNeslistStore) {
                  //     let dropdownList = { item_id: itm.eNBId, item_text: itm.eNBName };
                  //     this.getNeslist.push(dropdownList);
                  // }
                  this.dropdownSettings = {
                      singleSelection: true,
                      idField: 'item_id',
                      textField: 'item_text',
                      itemsShowLimit: 1,
                      allowSearchFilter: true,
                      searchPlaceholderText:"Search",
                      clearSearchFilter:true,
                      maxHeight: 300
                  };                    
                  }else{
                    this.showLoader = false;
                    this.getNeslist = [];
                  }
              }, 100); */
              //Please Comment while checkIn
        });
      }
      else{

        this.pregenerateService.getNeSiteListData(this.ciqNEFileDetails.ciqFileName, this.sharedService.createServiceToken() )
        .subscribe(
            data => {
                // setTimeout(() => { 
                  let jsonStatue = data.json();
                  this.nesListData = data.json();
                  this.showLoader = false;
                      if(jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User"){
                       this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                      } else {
                        if( this.sharedService.isvalidServiceToken( parseInt(data.json().serviceToken,10) ) || data.json().sessionId == "408"){
                          if(this.nesListData.status == "SUCCESS"){
                            this.getNesitelist = this.nesListData.siteList;
                            /* this.getNeslist = [];                             
                            let getNeslistStore = this.nesListData.eNBList;
                            for (let itm of getNeslistStore) {
                              let dropdownList = { item_id: itm.eNBId, item_text: itm.eNBName };
                              this.getNeslist.push(dropdownList);
                          } */
                          this.dropdownSettings = {
                              singleSelection: true,
                              idField: 'item_id',
                              textField: 'item_text',
                              itemsShowLimit: 1,
                              allowSearchFilter: true,
                              searchPlaceholderText:"Search",
                              clearSearchFilter:true,
                              maxHeight: 300
                          }; 
                          }else{
                            this.showLoader = false;
                            this.getNeslist = [];
                          }
                         }   
                      }
                                    
                // }, 1000);
            },
            error => {
              //Please Comment while checkIn
               /* setTimeout(() => { 
                this.showLoader = false;
                this.nesListData = JSON.parse('{"siteList":{"CA_SAC_SACRAMENTODT_252":[{"eNBName":"03600010055_5GDU_CA_SAC_SACRAMENTODT_252","eNBId":"3600010055","siteName":"CA_SAC_SACRAMENTODT_252"},{"eNBName":"03600010056_5GDU_CA_SAC_SACRAMENTODT_252","eNBId":"3600010056","siteName":"CA_SAC_SACRAMENTODT_252"},{"eNBName":"03600010057_5GDU_CA_SAC_SACRAMENTODT_252","eNBId":"3600010057","siteName":"CA_SAC_SACRAMENTODT_252"}],"CA_SAC_OAKPARK_068":[{"eNBName":"03600210028_5GDU_CA_SAC_OAKPARK_068","eNBId":"3600210028","siteName":"CA_SAC_OAKPARK_068"},{"eNBName":"03600210029_5GDU_CA_SAC_OAKPARK_068","eNBId":"3600210029","siteName":"CA_SAC_OAKPARK_068"},{"eNBName":"03600210030_5GDU_CA_SAC_OAKPARK_068","eNBId":"3600210030","siteName":"CA_SAC_OAKPARK_068"}],"CA_SAC_SACRAMENTODT_214":[{"eNBName":"03600080076_5GDU_CA_SAC_SACRAMENTODT_214","eNBId":"3600080076","siteName":"CA_SAC_SACRAMENTODT_214"},{"eNBName":"03600080077_5GDU_CA_SAC_SACRAMENTODT_214","eNBId":"3600080077","siteName":"CA_SAC_SACRAMENTODT_214"}],"CA_SAC_PARKWAY_016":[{"eNBName":"03600210040_5GDU_CA_SAC_PARKWAY_016","eNBId":"3600210040","siteName":"CA_SAC_PARKWAY_016"},{"eNBName":"03600210041_5GDU_CA_SAC_PARKWAY_016","eNBId":"3600210041","siteName":"CA_SAC_PARKWAY_016"}],"CA_SAC_PARKWAY_018":[{"eNBName":"03600210094_5GDU_CA_SAC_PARKWAY_018","eNBId":"3600210094","siteName":"CA_SAC_PARKWAY_018"},{"eNBName":"03600210095_5GDU_CA_SAC_PARKWAY_018","eNBId":"3600210095","siteName":"CA_SAC_PARKWAY_018"},{"eNBName":"03600210096_5GDU_CA_SAC_PARKWAY_018","eNBId":"3600210096","siteName":"CA_SAC_PARKWAY_018"}],"CA_SAC_SACRAMENTODT_250":[{"eNBName":"03600010040_5GDU_CA_SAC_SACRAMENTODT_250","eNBId":"3600010040","siteName":"CA_SAC_SACRAMENTODT_250"},{"eNBName":"03600010041_5GDU_CA_SAC_SACRAMENTODT_250","eNBId":"3600010041","siteName":"CA_SAC_SACRAMENTODT_250"}],"CA_SAC_SACRAMENTODT_096":[{"eNBName":"03600080094_5GDU_CA_SAC_SACRAMENTODT_096","eNBId":"3600080094","siteName":"CA_SAC_SACRAMENTODT_096"},{"eNBName":"03600080095_5GDU_CA_SAC_SACRAMENTODT_096","eNBId":"3600080095","siteName":"CA_SAC_SACRAMENTODT_096"},{"eNBName":"03600080096_5GDU_CA_SAC_SACRAMENTODT_096","eNBId":"3600080096","siteName":"CA_SAC_SACRAMENTODT_096"}],"CA_SAC_CENTRALSAC_098":[{"eNBName":"03600080121_5GDU_CA_SAC_CENTRALSAC_098","eNBId":"3600080121","siteName":"CA_SAC_CENTRALSAC_098"},{"eNBName":"03600080122_5GDU_CA_SAC_CENTRALSAC_098","eNBId":"3600080122","siteName":"CA_SAC_CENTRALSAC_098"},{"eNBName":"03600080123_5GDU_CA_SAC_CENTRALSAC_098","eNBId":"3600080123","siteName":"CA_SAC_CENTRALSAC_098"}],"CA_SAC_CENTRALSAC_129":[{"eNBName":"03600360001_5GAU_CA_SAC_CENTRALSAC_129","eNBId":"3600360001","siteName":"CA_SAC_CENTRALSAC_129"},{"eNBName":"03600360002_5GAU_CA_SAC_CENTRALSAC_129","eNBId":"3600360002","siteName":"CA_SAC_CENTRALSAC_129"},{"eNBName":"03600360003_5GAU_CA_SAC_CENTRALSAC_129","eNBId":"3600360003","siteName":"CA_SAC_CENTRALSAC_129"}],"CA_SAC_WOODCREEK_058":[{"eNBName":"03600010133_5GDU_CA_SAC_WOODCREEK_058","eNBId":"3600010133","siteName":"CA_SAC_WOODCREEK_058"},{"eNBName":"03600010134_5GDU_CA_SAC_WOODCREEK_058","eNBId":"3600010134","siteName":"CA_SAC_WOODCREEK_058"},{"eNBName":"03600010135_5GDU_CA_SAC_WOODCREEK_058","eNBId":"3600010135","siteName":"CA_SAC_WOODCREEK_058"}],"SACRAMENTODT_5G091":[{"eNBName":"03600010010_5GAU_SACRAMENTODT_5G091","eNBId":"3600010010","siteName":"SACRAMENTODT_5G091"},{"eNBName":"03600010011_5GAU_SACRAMENTODT_5G091","eNBId":"3600010011","siteName":"SACRAMENTODT_5G091"}],"CA_SAC_LANDPARK_089":[{"eNBName":"03600050046_5GDU_CA_SAC_LANDPARK_089","eNBId":"3600050046","siteName":"CA_SAC_LANDPARK_089"},{"eNBName":"03600050047_5GDU_CA_SAC_LANDPARK_089","eNBId":"3600050047","siteName":"CA_SAC_LANDPARK_089"},{"eNBName":"03600050048_5GDU_CA_SAC_LANDPARK_089","eNBId":"3600050048","siteName":"CA_SAC_LANDPARK_089"}],"CA_SAC_WOODCREEK_017":[{"eNBName":"03600010148_5GDU_CA_SAC_WOODCREEK_017","eNBId":"3600010148","siteName":"CA_SAC_WOODCREEK_017"},{"eNBName":"03600010149_5GDU_CA_SAC_WOODCREEK_017","eNBId":"3600010149","siteName":"CA_SAC_WOODCREEK_017"},{"eNBName":"03600010150_5GDU_CA_SAC_WOODCREEK_017","eNBId":"3600010150","siteName":"CA_SAC_WOODCREEK_017"}],"CA_SAC_PARKWAY_012":[{"eNBName":"03600210043_5GDU_CA_SAC_PARKWAY_012","eNBId":"3600210043","siteName":"CA_SAC_PARKWAY_012"},{"eNBName":"03600210044_5GDU_CA_SAC_PARKWAY_012","eNBId":"3600210044","siteName":"CA_SAC_PARKWAY_012"},{"eNBName":"03600210045_5GDU_CA_SAC_PARKWAY_012","eNBId":"3600210045","siteName":"CA_SAC_PARKWAY_012"}],"CA_SAC_WESTPARK_058":[{"eNBName":"03600010121_5GDU_CA_SAC_WESTPARK_058","eNBId":"3600010121","siteName":"CA_SAC_WESTPARK_058"},{"eNBName":"03600010122_5GDU_CA_SAC_WESTPARK_058","eNBId":"3600010122","siteName":"CA_SAC_WESTPARK_058"},{"eNBName":"03600010123_5GDU_CA_SAC_WESTPARK_058","eNBId":"3600010123","siteName":"CA_SAC_WESTPARK_058"}],"CA_SAC_CENTRALSAC_127":[{"eNBName":"03600080091_5GDU_CA_SAC_CENTRALSAC_127","eNBId":"3600080091","siteName":"CA_SAC_CENTRALSAC_127"},{"eNBName":"03600080092_5GDU_CA_SAC_CENTRALSAC_127","eNBId":"3600080092","siteName":"CA_SAC_CENTRALSAC_127"},{"eNBName":"03600080093_5GDU_CA_SAC_CENTRALSAC_127","eNBId":"3600080093","siteName":"CA_SAC_CENTRALSAC_127"}],"CA_SAC_WOODCREEK_050":[{"eNBName":"03600010154_5GDU_CA_SAC_WOODCREEK_050","eNBId":"3600010154","siteName":"CA_SAC_WOODCREEK_050"},{"eNBName":"03600010155_5GDU_CA_SAC_WOODCREEK_050","eNBId":"3600010155","siteName":"CA_SAC_WOODCREEK_050"},{"eNBName":"03600010156_5GDU_CA_SAC_WOODCREEK_050","eNBId":"3600010156","siteName":"CA_SAC_WOODCREEK_050"}],"CA_SAC_SACRAMENTODT_526":[{"eNBName":"03600080088_5GDU_CA_SAC_SACRAMENTODT_526","eNBId":"3600080088","siteName":"CA_SAC_SACRAMENTODT_526"},{"eNBName":"03600080089_5GDU_CA_SAC_SACRAMENTODT_526","eNBId":"3600080089","siteName":"CA_SAC_SACRAMENTODT_526"},{"eNBName":"03600080090_5GDU_CA_SAC_SACRAMENTODT_526","eNBId":"3600080090","siteName":"CA_SAC_SACRAMENTODT_526"}],"CA_SAC_SACRAMENTODT_208":[{"eNBName":"03600010034_5GDU_CA_SAC_SACRAMENTODT_208","eNBId":"3600010034","siteName":"CA_SAC_SACRAMENTODT_208"},{"eNBName":"03600010035_5GDU_CA_SAC_SACRAMENTODT_208","eNBId":"3600010035","siteName":"CA_SAC_SACRAMENTODT_208"}],"CA_SAC_LANDPARK_009":[{"eNBName":"03600210001_5GAU_CA_SAC_LANDPARK_009","eNBId":"3600210001","siteName":"CA_SAC_LANDPARK_009"},{"eNBName":"03600210002_5GAU_CA_SAC_LANDPARK_009","eNBId":"3600210002","siteName":"CA_SAC_LANDPARK_009"},{"eNBName":"03600210003_5GAU_CA_SAC_LANDPARK_009","eNBId":"3600210003","siteName":"CA_SAC_LANDPARK_009"}],"CA_SAC_WOODCREEK_010":[{"eNBName":"03600010145_5GDU_CA_SAC_WOODCREEK_010","eNBId":"3600010145","siteName":"CA_SAC_WOODCREEK_010"},{"eNBName":"03600010146_5GDU_CA_SAC_WOODCREEK_010","eNBId":"3600010146","siteName":"CA_SAC_WOODCREEK_010"},{"eNBName":"03600010147_5GDU_CA_SAC_WOODCREEK_010","eNBId":"3600010147","siteName":"CA_SAC_WOODCREEK_010"}],"CA_SAC_LANDPARK_090":[{"eNBName":"03600210082_5GDU_CA_SAC_LANDPARK_090","eNBId":"3600210082","siteName":"CA_SAC_LANDPARK_090"},{"eNBName":"03600210083_5GDU_CA_SAC_LANDPARK_090","eNBId":"3600210083","siteName":"CA_SAC_LANDPARK_090"}],"CA_SAC_WOODCREEK_003":[{"eNBName":"03600010142_5GDU_CA_SAC_WOODCREEK_003","eNBId":"3600010142","siteName":"CA_SAC_WOODCREEK_003"},{"eNBName":"03600010143_5GDU_CA_SAC_WOODCREEK_003","eNBId":"3600010143","siteName":"CA_SAC_WOODCREEK_003"},{"eNBName":"03600010144_5GDU_CA_SAC_WOODCREEK_003","eNBId":"3600010144","siteName":"CA_SAC_WOODCREEK_003"}],"CA_SAC_WOODCREEK_049":[{"eNBName":"03600010130_5GDU_CA_SAC_WOODCREEK_049","eNBId":"3600010130","siteName":"CA_SAC_WOODCREEK_049"},{"eNBName":"03600010131_5GDU_CA_SAC_WOODCREEK_049","eNBId":"3600010131","siteName":"CA_SAC_WOODCREEK_049"},{"eNBName":"03600010132_5GDU_CA_SAC_WOODCREEK_049","eNBId":"3600010132","siteName":"CA_SAC_WOODCREEK_049"}],"CA_SAC_SOUTHSAC_026":[{"eNBName":"03600210097_5GDU_CA_SAC_SOUTHSAC_026","eNBId":"3600210097","siteName":"CA_SAC_SOUTHSAC_026"},{"eNBName":"03600210098_5GDU_CA_SAC_SOUTHSAC_026","eNBId":"3600210098","siteName":"CA_SAC_SOUTHSAC_026"},{"eNBName":"03600210099_5GDU_CA_SAC_SOUTHSAC_026","eNBId":"3600210099","siteName":"CA_SAC_SOUTHSAC_026"}],"CA_SAC_PARKWAY_023":[{"eNBName":"03600210037_5GDU_CA_SAC_PARKWAY_023","eNBId":"3600210037","siteName":"CA_SAC_PARKWAY_023"},{"eNBName":"03600210038_5GDU_CA_SAC_PARKWAY_023","eNBId":"3600210038","siteName":"CA_SAC_PARKWAY_023"}],"CA_SAC_TRUXEL_206":[{"eNBName":"03600480034_5GDU_CA_SAC_TRUXEL_206","eNBId":"3600480034","siteName":"CA_SAC_TRUXEL_206"},{"eNBName":"03600480035_5GDU_CA_SAC_TRUXEL_206","eNBId":"3600480035","siteName":"CA_SAC_TRUXEL_206"}],"CA_SAC_WOODCREEK_083":[{"eNBName":"03600010163_5GDU_CA_SAC_WOODCREEK_083","eNBId":"3600010163","siteName":"CA_SAC_WOODCREEK_083"},{"eNBName":"03600010164_5GDU_CA_SAC_WOODCREEK_083","eNBId":"3600010164","siteName":"CA_SAC_WOODCREEK_083"},{"eNBName":"03600010165_5GDU_CA_SAC_WOODCREEK_083","eNBId":"3600010165","siteName":"CA_SAC_WOODCREEK_083"}],"CA_SAC_HAGGINWOOD_136":[{"eNBName":"03600480043_5GDU_CA_SAC_HAGGINWOOD_136","eNBId":"3600480043","siteName":"CA_SAC_HAGGINWOOD_136"},{"eNBName":"03600480044_5GDU_CA_SAC_HAGGINWOOD_136","eNBId":"3600480044","siteName":"CA_SAC_HAGGINWOOD_136"},{"eNBName":"03600480045_5GDU_CA_SAC_HAGGINWOOD_136","eNBId":"3600480045","siteName":"CA_SAC_HAGGINWOOD_136"}],"CA_SAC_SACRAMENTODT_511":[{"eNBName":"03600080043_5GAU_CA_SAC_SACRAMENTODT_511","eNBId":"3600080043","siteName":"CA_SAC_SACRAMENTODT_511"},{"eNBName":"03600080044_5GAU_CA_SAC_SACRAMENTODT_511","eNBId":"3600080044","siteName":"CA_SAC_SACRAMENTODT_511"},{"eNBName":"03600080045_5GAU_CA_SAC_SACRAMENTODT_511","eNBId":"3600080045","siteName":"CA_SAC_SACRAMENTODT_511"}],"CA_SAC_SACRAMENTODT_236":[{"eNBName":"03600080082_5GDU_CA_SAC_SACRAMENTODT_236","eNBId":"3600080082","siteName":"CA_SAC_SACRAMENTODT_236"},{"eNBName":"03600080083_5GDU_CA_SAC_SACRAMENTODT_236","eNBId":"3600080083","siteName":"CA_SAC_SACRAMENTODT_236"},{"eNBName":"03600080084_5GDU_CA_SAC_SACRAMENTODT_236","eNBId":"3600080084","siteName":"CA_SAC_SACRAMENTODT_236"}],"CA_SAC_SACRAMENTODT_237":[{"eNBName":"03600080085_5GDU_CA_SAC_SACRAMENTODT_237","eNBId":"3600080085","siteName":"CA_SAC_SACRAMENTODT_237"},{"eNBName":"03600080086_5GDU_CA_SAC_SACRAMENTODT_237","eNBId":"3600080086","siteName":"CA_SAC_SACRAMENTODT_237"}],"CA_SAC_PARKWAY_038":[{"eNBName":"03600050049_5GDU_CA_SAC_PARKWAY_038","eNBId":"3600050049","siteName":"CA_SAC_PARKWAY_038"},{"eNBName":"03600050050_5GDU_CA_SAC_PARKWAY_038","eNBId":"3600050050","siteName":"CA_SAC_PARKWAY_038"}],"CA_SAC_OAKPARK_163":[{"eNBName":"03600210025_5GDU_CA_SAC_OAKPARK_163","eNBId":"3600210025","siteName":"CA_SAC_OAKPARK_163"},{"eNBName":"03600210026_5GDU_CA_SAC_OAKPARK_163","eNBId":"3600210026","siteName":"CA_SAC_OAKPARK_163"},{"eNBName":"03600210027_5GDU_CA_SAC_OAKPARK_163","eNBId":"3600210027","siteName":"CA_SAC_OAKPARK_163"}],"CA_SAC_HAGGINWOOD_172":[{"eNBName":"03600480040_5GDU_CA_SAC_HAGGINWOOD_172","eNBId":"3600480040","siteName":"CA_SAC_HAGGINWOOD_172"},{"eNBName":"03600480041_5GDU_CA_SAC_HAGGINWOOD_172","eNBId":"3600480041","siteName":"CA_SAC_HAGGINWOOD_172"}],"CA_SAC_LANDPARK_065":[{"eNBName":"03600050037_5GDU_CA_SAC_LANDPARK_065","eNBId":"3600050037","siteName":"CA_SAC_LANDPARK_065"},{"eNBName":"03600050038_5GDU_CA_SAC_LANDPARK_065","eNBId":"3600050038","siteName":"CA_SAC_LANDPARK_065"}],"CA_SAC_WOODCREEK_037":[{"eNBName":"03600010151_5GDU_CA_SAC_WOODCREEK_037","eNBId":"3600010151","siteName":"CA_SAC_WOODCREEK_037"},{"eNBName":"03600010152_5GDU_CA_SAC_WOODCREEK_037","eNBId":"3600010152","siteName":"CA_SAC_WOODCREEK_037"},{"eNBName":"03600010153_5GDU_CA_SAC_WOODCREEK_037","eNBId":"3600010153","siteName":"CA_SAC_WOODCREEK_037"}],"CA_SAC_GOLDEN1_342":[{"eNBName":"03600050004_5GAU_CA_SAC_GOLDEN1_342","eNBId":"3600050004","siteName":"CA_SAC_GOLDEN1_342"},{"eNBName":"03600050005_5GAU_CA_SAC_GOLDEN1_342","eNBId":"3600050005","siteName":"CA_SAC_GOLDEN1_342"}],"CA_SAC_SACRAMENTODT_228":[{"eNBName":"03600080079_5GDU_CA_SAC_SACRAMENTODT_228","eNBId":"3600080079","siteName":"CA_SAC_SACRAMENTODT_228"},{"eNBName":"03600080080_5GDU_CA_SAC_SACRAMENTODT_228","eNBId":"3600080080","siteName":"CA_SAC_SACRAMENTODT_228"},{"eNBName":"03600080081_5GDU_CA_SAC_SACRAMENTODT_228","eNBId":"3600080081","siteName":"CA_SAC_SACRAMENTODT_228"}],"CA_SAC_WESTPARK_110":[{"eNBName":"03600010136_5GDU_CA_SAC_WESTPARK_110","eNBId":"3600010136","siteName":"CA_SAC_WESTPARK_110"},{"eNBName":"03600010137_5GDU_CA_SAC_WESTPARK_110","eNBId":"3600010137","siteName":"CA_SAC_WESTPARK_110"},{"eNBName":"03600010138_5GDU_CA_SAC_WESTPARK_110","eNBId":"3600010138","siteName":"CA_SAC_WESTPARK_110"}],"CA_SAC_WESTPARK_111":[{"eNBName":"03600010139_5GDU_CA_SAC_WESTPARK_111","eNBId":"3600010139","siteName":"CA_SAC_WESTPARK_111"},{"eNBName":"03600010140_5GDU_CA_SAC_WESTPARK_111","eNBId":"3600010140","siteName":"CA_SAC_WESTPARK_111"},{"eNBName":"03600010141_5GDU_CA_SAC_WESTPARK_111","eNBId":"3600010141","siteName":"CA_SAC_WESTPARK_111"}],"CA_SAC_WOODCREEK_077":[{"eNBName":"03600010160_5GDU_CA_SAC_WOODCREEK_077","eNBId":"3600010160","siteName":"CA_SAC_WOODCREEK_077"},{"eNBName":"03600010161_5GDU_CA_SAC_WOODCREEK_077","eNBId":"3600010161","siteName":"CA_SAC_WOODCREEK_077"},{"eNBName":"03600010162_5GDU_CA_SAC_WOODCREEK_077","eNBId":"3600010162","siteName":"CA_SAC_WOODCREEK_077"}],"CA_SAC_POCKET_022":[{"eNBName":"03600050079_5GDU_CA_SAC_POCKET_022","eNBId":"3600050079","siteName":"CA_SAC_POCKET_022"},{"eNBName":"03600050080_5GDU_CA_SAC_POCKET_022","eNBId":"3600050080","siteName":"CA_SAC_POCKET_022"},{"eNBName":"03600050081_5GDU_CA_SAC_POCKET_022","eNBId":"3600050081","siteName":"CA_SAC_POCKET_022"}],"CA_SAC_PARKWAY_049":[{"eNBName":"03600210034_5GDU_CA_SAC_PARKWAY_049","eNBId":"3600210034","siteName":"CA_SAC_PARKWAY_049"},{"eNBName":"03600210035_5GDU_CA_SAC_PARKWAY_049","eNBId":"3600210035","siteName":"CA_SAC_PARKWAY_049"},{"eNBName":"03600210036_5GDU_CA_SAC_PARKWAY_049","eNBId":"3600210036","siteName":"CA_SAC_PARKWAY_049"}],"CA_SAC_LANDPARK_073":[{"eNBName":"03600210064_5GDU_CA_SAC_LANDPARK_073","eNBId":"3600210064","siteName":"CA_SAC_LANDPARK_073"},{"eNBName":"03600210065_5GDU_CA_SAC_LANDPARK_073","eNBId":"3600210065","siteName":"CA_SAC_LANDPARK_073"},{"eNBName":"03600210066_5GDU_CA_SAC_LANDPARK_073","eNBId":"3600210066","siteName":"CA_SAC_LANDPARK_073"}],"CA_SAC_WESTPARK_109":[{"eNBName":"03600010127_5GDU_CA_SAC_WESTPARK_109","eNBId":"3600010127","siteName":"CA_SAC_WESTPARK_109"},{"eNBName":"03600010128_5GDU_CA_SAC_WESTPARK_109","eNBId":"3600010128","siteName":"CA_SAC_WESTPARK_109"},{"eNBName":"03600010129_5GDU_CA_SAC_WESTPARK_109","eNBId":"3600010129","siteName":"CA_SAC_WESTPARK_109"}],"CA_SAC_LANDPARK_036":[{"eNBName":"03600210091_5GDU_CA_SAC_LANDPARK_036","eNBId":"3600210091","siteName":"CA_SAC_LANDPARK_036"},{"eNBName":"03600210092_5GDU_CA_SAC_LANDPARK_036","eNBId":"3600210092","siteName":"CA_SAC_LANDPARK_036"},{"eNBName":"03600210093_5GDU_CA_SAC_LANDPARK_036","eNBId":"3600210093","siteName":"CA_SAC_LANDPARK_036"}],"CA_SAC_WOODCREEK_062":[{"eNBName":"03600010157_5GDU_CA_SAC_WOODCREEK_062","eNBId":"3600010157","siteName":"CA_SAC_WOODCREEK_062"},{"eNBName":"03600010158_5GDU_CA_SAC_WOODCREEK_062","eNBId":"3600010158","siteName":"CA_SAC_WOODCREEK_062"},{"eNBName":"03600010159_5GDU_CA_SAC_WOODCREEK_062","eNBId":"3600010159","siteName":"CA_SAC_WOODCREEK_062"}],"CA_SAC_GOLDEN1_191":[{"eNBName":"03600050001_5GAU_CA_SAC_GOLDEN1_191","eNBId":"3600050001","siteName":"CA_SAC_GOLDEN1_191"},{"eNBName":"03600050002_5GAU_CA_SAC_GOLDEN1_191","eNBId":"3600050002","siteName":"CA_SAC_GOLDEN1_191"},{"eNBName":"03600050003_5GAU_CA_SAC_GOLDEN1_191","eNBId":"3600050003","siteName":"CA_SAC_GOLDEN1_191"}]},"sessionId":"964a8be1","serviceToken":"55861","status":"SUCCESS"}');
                  if(this.nesListData.sessionId == "408" || this.nesListData.status == "Invalid User"){
                     this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                    }
                  if(this.nesListData.status == "SUCCESS"){
                    this.getNesitelist = this.nesListData.siteList;
                  //   this.getNeslist = [];                             
                  //   let getNeslistStore = this.nesListData.eNBList;
                  //   for (let itm of getNeslistStore) {
                  //     let dropdownList = { item_id: itm.eNBId, item_text: itm.eNBName };
                  //     this.getNeslist.push(dropdownList);
                  // }
                  this.dropdownSettings = {
                      singleSelection: true,
                      idField: 'item_id',
                      textField: 'item_text',
                      itemsShowLimit: 1,
                      allowSearchFilter: true,
                      searchPlaceholderText:"Search",
                      clearSearchFilter:true,
                      maxHeight: 300
                  };                    
                  }else{
                    this.showLoader = false;
                    this.getNeslist = [];
                  }
              }, 100); */
              //Please Comment while checkIn
        });
      }
    }
    else
    {
      this.getNeslist = [];    
    }
  }


  validate(event) {
    console.log(this.neDetails)
    if (this.ciqNEFileDetails !="" && this.ciqNEFileDetails != undefined &&  this.ciqNEFileDetails.ciqFileName) {
        this.validationData.rules.ciqName.required = false;
  
    } else {
        this.validationData.rules.ciqName.required = true;
  
    }    
    if (this.neDetails) {
        this.validationData.rules.nes.required = false;

    } else {
        this.validationData.rules.nes.required = true;
    }
    let tempNEDetails = this.neDetails;
    /* if (this.selectedNEIDs && this.selectedNEIDs.length > 0) {
      this.validationData.rules.neIDs.required = false;

    } else {
      this.validationData.rules.neIDs.required = true;
    } */
    
    validator.performValidation(event, this.validationData, "save_update");
    setTimeout(() => {
        if (this.isValidForm(event)) {
            this.showLoader = true;
            let neDetails =[];
            if(this.programName!='VZN-5G-MM'){
              if(tempNEDetails) {
                for(let item of tempNEDetails ){
                  let selNE = {
                      "neId": item.eNBId,
                      "neName": item.eNBName
                  }
                  neDetails.push(selNE);
              }
            }
          }
          else
          {
              if(this.selectedItemsNE) {
                
                  for(let itm of tempNEDetails){
                    console.log(itm)
                      for(let item of itm.value){
                      
                        for(let c of this.selectedItemsNE ){
                          if(c == item.eNBName)
                          {
                              let selNE = {
                                  "neId": item.eNBId,
                                  "neName": item.eNBName
                              }
                              neDetails.push(selNE);
                          }
                        }

                      }
                  }
       
              }
          }
       let currentForm = event.target.parentNode.parentNode;


      this.ciqFileName = currentForm.querySelector("#ciqName").value;
      this.enbName = currentForm.querySelector("#nes").value;
      // let neVersion = currentForm.querySelector("#neVersion") ? currentForm.querySelector("#neVersion").value : null;
      let fsuType = currentForm.querySelector("#fsuType") ? currentForm.querySelector("#fsuType").value : null;
      this.remarksVal = currentForm.querySelector("#remarks").value;

    
    this.pregenerateService.validatedetails(this.ciqNEFileDetails.ciqFileName, this.sharedService.createServiceToken(),this.paginationDetails, neDetails, fsuType, this.programName == "VZN-5G-MM" ? this.generateAllSites : null)
        .subscribe(
            data => {
                setTimeout(() => { 
                let jsonStatue = data.json();  
                if (jsonStatue.status == "403") {
                    this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: "static", size: "lg", windowClass: "session-modal" });
                } else {
                    if (
                        this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {
                        if (jsonStatue.status == "SUCCESS") {
                            this.showLoader = false;
                             this.message = "Files Validated Successfully!";
                            this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: "static", size: "lg", windowClass: "success-modal" }); 
                           // this.displayModel(jsonStatue.reason, "successIcon");
                        } else {                            
                            this.validateDetails = jsonStatue.errorDetails;
                            setTimeout(()=>{
                             this.showLoader = false;
                             this.viewModalBlock = this.modalService.open( this.viewModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth validateView' });
                            },100);
                        }
                    }
                
                }
            }, 1000);
            },
            error => {

                //Please Comment while checkIn
              /*   let jsonStatue: any = {"reason":"Validations Fail","sessionId":"55e5b0ad","serviceToken":"75370","status":"SUCCESS","errorDetails":[{"rowId":1,"propertyName":"EARFCN_DL","errorMessage":"EARFCN_DL value should be 5230 as BandName value is 700MHz"},{"rowId":1,"propertyName":"antennaPathDelayUL(m)","errorMessage":"antennaPathDelayUL(m) lessThan the value of antennaPathDelayDL(m)"},{"rowId":2,"propertyName":"antennaPathDelayUL(m)","errorMessage":"antennaPathDelayUL(m) lessThan the value of antennaPathDelayDL(m)"},{"rowId":3,"propertyName":"EARFCN_DL","errorMessage":"EARFCN_DL value should be 5230 as BandName value is 700MHz"},{"rowId":3,"propertyName":"Cell_ID","errorMessage":"Cell_ID value would be any values of these 1,2,3,4,5,6,7 as BandName value is 700MHz"},{"rowId":3,"propertyName":"antennaPathDelayUL(m)","errorMessage":"antennaPathDelayUL(m) lessThan the value of antennaPathDelayDL(m)"},{"rowId":4,"propertyName":"Cell_ID","errorMessage":"Cell_ID value would be any values of these 12,22,32,42,52,62,72,82,92 as BandName value is AWS-1"},{"rowId":4,"propertyName":"antennaPathDelayUL(m)","errorMessage":"antennaPathDelayUL(m) lessThan the value of antennaPathDelayDL(m)"},{"rowId":5,"propertyName":"antennaPathDelayUL(m)","errorMessage":"antennaPathDelayUL(m) lessThan the value of antennaPathDelayDL(m)"},{"rowId":6,"propertyName":"antennaPathDelayUL(m)","errorMessage":"antennaPathDelayUL(m) lessThan the value of antennaPathDelayDL(m)"},{"rowId":7,"propertyName":"antennaPathDelayUL(m)","errorMessage":"antennaPathDelayUL(m) lessThan the value of antennaPathDelayDL(m)"},{"rowId":8,"propertyName":"antennaPathDelayUL(m)","errorMessage":"antennaPathDelayUL(m) lessThan the value of antennaPathDelayDL(m)"},{"rowId":10,"propertyName":"antennaPathDelayUL(m)","errorMessage":"antennaPathDelayUL(m) lessThan the value of antennaPathDelayDL(m)"},{"rowId":11,"propertyName":"antennaPathDelayUL(m)","errorMessage":"antennaPathDelayUL(m) lessThan the value of antennaPathDelayDL(m)"},{"rowId":12,"propertyName":"antennaPathDelayUL(m)","errorMessage":"antennaPathDelayUL(m) lessThan the value of antennaPathDelayDL(m)"}]};
              
                setTimeout(() => {
                    
                    if (jsonStatue.status == "403") {
                        this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: "static", size: "lg", windowClass: "session-modal" });
                    }
                    else {
                        if (jsonStatue.status == "SUCCESS") {
                            this.showLoader = false;
                            this.message = "Files Validated Successfully!";
                            this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: "static", size: "lg", windowClass: "success-modal" });
                        } else {
                            this.validateDetails = jsonStatue.errorDetails;
                            setTimeout(()=>{
                            this.showLoader = false;
                             this.viewModalBlock = this.modalService.open( this.viewModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth validateView' });
                            },100);
                        }
                    }

                }, 1000);  */
                //Please Comment while checkIn
            }
        );
      }
      }, 0);

  }

  closeModelcheckConn() {
    this.successModalBlock.close();
}
  onChangesites(event: any){

    this.dropdownListNE=[];
    for (let itm of this.neDetails) {
                 
      for(let item of itm.value){
         
          let dropdownListNE = item.eNBName;
          this.dropdownListNE.push(dropdownListNE);
      }
          this.selectedItemsNE=this.dropdownListNE;
      }
      if(this.neDetails.length >0)
        this.nenamebutton=false;
      else
        this.nenamebutton=true;

      this.dropdownSettingsNEs = {
        singleSelection: false,
        idField: 'item_id',
        textField: 'item_text',
        itemsShowLimit: 1,
        allowSearchFilter: true,
        searchPlaceholderText:"Search",
        clearSearchFilter:true,
        maxHeight: 300
        };
  }
  getNeList(content) {
    this.successModalBlock = this.modalService.open(content, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView' });
  }
generate(event) {
    if (this.ciqNEFileDetails !="" && this.ciqNEFileDetails != undefined &&  this.ciqNEFileDetails.ciqFileName) {
        this.validationData.rules.ciqName.required = false;
    
    } else {
        this.validationData.rules.ciqName.required = true;
    
    } 
    if (this.neDetails != null && this.neDetails.length > 0) { // this.neDetails.length > 0
        console.log(this.neDetails)
        this.validationData.rules.nes.required = false;

    } else {
        this.validationData.rules.nes.required = true;
    }
    /* if (this.selectedNEIDs && this.selectedNEIDs.length > 0) {
      this.validationData.rules.neIDs.required = false;

    } else {
      this.validationData.rules.neIDs.required = true;
    } */
    validator.performValidation(event, this.validationData, "save_update");
    setTimeout(()=>{
        if (this.isValidForm(event)) {
            this.showLoader = true;
            let neDetails =[];
            if(this.programName!='VZN-5G-MM'){
              if(this.neDetails) {
                for(let item of this.neDetails ){
                  let selNE = {
                      "neId": item.eNBId,
                      "neName": item.eNBName
                  }
                  neDetails.push(selNE);
              }
            }
            console.log(neDetails);
          }
          else
          {
              if(this.selectedItemsNE) {
                
                  for(let itm of this.neDetails){
                    console.log(itm)
                      for(let item of itm.value){
                      
                        for(let c of this.selectedItemsNE ){
                          if(c == item.eNBName)
                          {
                              let selNE = {
                                  "neId": item.eNBId,
                                  "neName": item.eNBName
                              }
                              neDetails.push(selNE);
                          }
                        }

                      }
                  }
       
              }
          }
            let currentForm = event.target.parentNode.parentNode;
            // let neVersion = currentForm.querySelector("#neVersion") ? currentForm.querySelector("#neVersion").value : null;
            let fsuType = currentForm.querySelector("#fsuType") ? currentForm.querySelector("#fsuType").value : null;
            this.remarksVal = currentForm.querySelector("#remarks").value;
            let integrationType = currentForm.querySelector("#migrationStrategy");
            let migrationStrategy = integrationType && this.programName == 'VZN-4G-USM-LIVE' && this.fileType == 'CSV' ? integrationType.value : null;
            this.pregenerateService.generatedetails(this.fileType, this.ciqNEFileDetails.ciqFileName, neDetails, this.sharedService.createServiceToken(), this.paginationDetails, this.remarksVal, this.selectedNEIDs, fsuType, migrationStrategy, this.programName == "VZN-5G-MM" ? this.generateAllSites : null, this.programName == "VZN-4G-USM-LIVE" ? this.supportCA : null, this.ovUpdate)
                .subscribe(
                    data => {
                        setTimeout(() => {
                            let jsonStatue = data.json();
                            if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
                                this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });

                            } else {
                                if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {
                                    let validNeIdRep = ""
                                    if (jsonStatue.validateNeIdReport) {
                                      validNeIdRep = '\n' + jsonStatue.validateNeIdReport
                                    }
                                    if (jsonStatue.status == "SUCCESS") {
                                        this.showLoader = false;
                                        this.message = jsonStatue.reason + validNeIdRep;
                                        if(this.message.indexOf('$') > -1) {
                                          this.message = this.message.replace('$', '\n');
                                        }
                                        this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: "static", size: "lg", windowClass: "success-modal" });
                                    } else {
                                        this.showLoader = false;
                                        let msg = jsonStatue.reason + validNeIdRep;
                                        this.displayModel(msg.replace('$', '\n'), "failureIcon");
                                    }
                                }
                            }
                        }, 1000);
                    },
                    error => {

                        //Please Comment while checkIn
                        /* let jsonStatue: any = { "reason": "COMMISSION SCRIPT$File Generated Successfully", "sessionId": "61e3c597", "serviceToken": "68126", "status": "SUCCESS" };
    
                        setTimeout(() => {
                            this.showLoader = false;
                            if (jsonStatue.status == "403") {
                                this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: "static", size: "lg", windowClass: "session-modal" });
                            }
                            else {
                                  let validNeIdRep = ""
                                  if (jsonStatue.validateNeIdReport) {
                                    validNeIdRep = '\n' + jsonStatue.validateNeIdReport
                                  }
                                if (jsonStatue.status == "SUCCESS") {
                                    this.message = jsonStatue.reason + validNeIdRep;
                                    if(this.message.indexOf('$') > -1) {
                                      this.message = this.message.replace('$', '\n');
                                    }
                                    this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: "static", size: "lg", windowClass: "success-modal" });
                                } else {
                                  let msg = jsonStatue.reason + validNeIdRep;
                                  this.displayModel(msg.replace('$', '\n'), "failureIcon");
                                }
                            }    
                        }, 1000); */
                        //Please Comment while checkIn
                    }
                );
        }

    }, 0);

}

  /*
   * on click of edit row create a blueprint and append next to the current row
   * @param : current row event , current row json object and row index
   * @retun : null
   */

  editRow(event, key, index) {

    //    this.cancelCreateNew(event);
    
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
          setTimeout(() => {
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
    
      updateEditRow(index, key, event){
        validator.performValidation(event, this.validationData, "save_update");
    
        setTimeout(() => {
          if(this.isValidForm(event)){
              console.log(key);
            this.showLoader = true;
            let currentEditedForm = event.target.parentNode.parentNode,
            generateInfoAuditDetails = {
                      "id": key.id,
                      "programDetailsEntity": key.programDetailsEntity,
                      "fileName": key.fileName,
                      "filePath": key.filePath,
                      "fileType": key.fileType,
                      "ciqFileName": key.ciqFileName,
                      "neName": key.neName,
                      "generatedBy": key.generatedBy,
                      "generationDate": key.generationDate,
                      "searchStartDate": key.searchStartDate,
                      "searchEndDate": key.searchEndDate,
                      "remarks": currentEditedForm.querySelector("#remarks").value
                      //"status": currentEditedForm.querySelector("#status").value
                 }; 
            this.pregenerateService.updateGenerateDetails(generateInfoAuditDetails, this.sharedService.createServiceToken())
            .subscribe(
                data => {
                    let jsonStatue = data.json();
                    this.showLoader = false;
    
                    if(jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User"){
                
                      this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                
                    } else {
                
                      if( this.sharedService.isvalidServiceToken( parseInt(data.json().serviceToken,10) ) || data.json().sessionId == "408"){               
                          if(jsonStatue.status == "SUCCESS"){
                             this.message = jsonStatue.reason;
                             this.successModalBlock = this.modalService.open(this.successModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'success-modal'});
    
                         
                             this.cancelEditRow(key.id, '');
                           
                          } else {
                            this.displayModel(jsonStatue.reason,"failureIcon");  
                          }
                      }   
                    }
                    
                },  
                error => {
                  //Please Comment while checkIn
              /*   let jsonStatue: any = {"reason":"Generated Audit Details Updated Successfully!","sessionId":"c49f47af","serviceToken":"61799","status":"SUCCESS"};
                 setTimeout(() => {
                    this.showLoader = false;
                    if(jsonStatue.status == "SUCCESS"){
                     this.message = jsonStatue.reason;
                    this.successModalBlock = this.modalService.open(this.successModalRef,{keyboard: false, backdrop: "static",size:'lg',windowClass:"success-modal"});
                     // this.displayModel("User details updated successfully !","successIcon");
                      //this.createForm = false;
                      this.cancelEditRow(key.id, '');
                    } else {
                      this.displayModel(jsonStatue.reason,"failureIcon");  
                    }
              
                  }, 1000); */
                  //Please Comment while checkIn
                });
            }
          }, 0);
      }
      /*
       * on click of cancel edit then close the current edited form
       * @param : index, identifier
       * @retun : null
       */
    
      cancelEditRow(index, identifier){
        $(".editRowDisabled").attr("class","editRow");
        $(".deleteRowDisabled").attr("class","deleteRow");
        let currentEditedForm = document.querySelector("#row_id_"+identifier);
        //this.editableFormArray.splice(this.editableFormArray.indexOf(index), 1);
        this.editableFormArray = [];
        this.checkFormEnable(index); //TODO : need to recheck this function
        this.sharedService.userNavigation = true; //un block user navigation
        currentEditedForm.lastElementChild.lastElementChild.children[0].className = "editRow";
      }
    
      /*
       * On click delete row open a modal for confirmation
       * @param : content, userName
       * @retun : null
       */
    
        deleteRow(confirmModal, userName, id, fileName,filePath,event) {
            if (event.target.className != "deleteRowDisabled") {
          this.modalService.open(confirmModal,{keyboard: false, backdrop: 'static', size:'lg', windowClass:'confirm-modal'}).result.then((result) => {
            this.closeResult = `Closed with: ${result}`;
          }, (reason) => {
            this.showLoader = true;
    
            this.pregenerateService.deleteUserDetails(id, fileName, filePath, this.sharedService.createServiceToken())
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
                                   this.message = "Generate details deleted successfully!";
                                   this.successModalBlock = this.modalService.open(this.successModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'success-modal'});
                              } else {                           
                                  this.displayModel(jsonStatue.reason,"failureIcon");  
                              } 
                            }
                        }
                          console.log("im here success :) ");
                       
                      },
                      error => {
                        //Please Comment while checkIn
                    /*   setTimeout(() => {
                          this.showLoader = false;
                         let jsonStatue = {"reason":"Details Deleted Successfully","sessionId":"8fc6b002","serviceToken":"73365","status":"SUCCESS"};
                        if(jsonStatue.status == "SUCCESS"){
                          this.message = "Generate details deleted successfully!";
                          this.successModalBlock = this.modalService.open(this.successModalRef,{keyboard: false, backdrop: "static",size:'lg',windowClass:"success-modal"});
                        } else {
                            this.displayModel(jsonStatue.reason,"failureIcon");  
                        }
                      }, 1000); */
                      //Please Comment while checkIn
                       
                          //this.alertService.error(error);TODO : This need to implement
                  });
          });
        }
      }

      /**
       * 
       * This function will call two APIs, one to get NE Config details which will auto select NE Version and NE NAME
       * and other one is to get the Band List
       */
      
      onChangeNEs(event) {
        if (this.neDetails && this.fileType == 'ENDC') {
          this.showLoader = true;
          this.pregenerateService.getNeIdList(this.ciqNEFileDetails.ciqFileName, this.neDetails, this.sharedService.createServiceToken())
            .subscribe(
              data => {
                setTimeout(() => {
                  let jsonStatue = data.json();
                  this.showLoader = false;
                  if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
                    this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
                  } else {
                    if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {
                      if (jsonStatue.status == "SUCCESS") {
                        this.getNeIDslist = [];
                        let getNeslistStore = jsonStatue.neList;
                        for (let itm of getNeslistStore) {
                          let dropdownList = { item_id: itm, item_text: itm };
                          this.getNeIDslist.push(dropdownList);
                        }
                        this.neIdDropdownSettings = {
                          singleSelection: false,
                          idField: 'item_id',
                          textField: 'item_text',
                          selectAllText: 'Select All',
                          unSelectAllText: 'UnSelect All',
                          itemsShowLimit: 1,
                          allowSearchFilter: true
                        };
                      } else {
                        this.showLoader = false;
                      }
                    }
                  }

                }, 1000);
              },
              error => {
                //Please Comment while checkIn
                /* setTimeout(() => {
                    this.showLoader = false;
                    let jsonStatue = JSON.parse('{"sessionId":"aa5393be","serviceToken":"77360","status":"SUCCESS","neList":["34212","323242","334442","443342","343242","443421","121342"]}');
                    if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
                        this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
                    }
                    if (jsonStatue.status == "SUCCESS") {
                      this.getNeIDslist = [];
                      let getNeslistStore = jsonStatue.neList;
                      for (let itm of getNeslistStore) {
                        let dropdownList = { item_id: itm, item_text: itm };
                        this.getNeIDslist.push(dropdownList);
                      }
                      this.neIdDropdownSettings = {
                        singleSelection: false,
                        idField: 'item_id',
                        textField: 'item_text',
                        selectAllText: 'Select All',
                        unSelectAllText: 'UnSelect All',
                        itemsShowLimit: 1,
                        allowSearchFilter: true
                      };
                    } else {
                        this.showLoader = false;
                    }
                }, 100); */
                //Please Comment while checkIn
              });
            }
            else {
              this.getNeIDslist = [];
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
                    "count": parseInt(this.pageSize,10),
                    "page": parseInt(page)
                };
    
                this.paginationDetails = paginationDetails;
                this.pager = this.getPager(this.totalPages*this.pageSize, this.currentPage, this.pageRenge);
                this.paginationDisabbled = false;
                // Hide all the form/Table/Nodatafound5
                this.tableShowHide = false;
                
                this.getGenerateDetails();
    
    
            }, 0);
    
    
    
        };

        setMenuHighlight(selectedElement) {
          this.generateTabRef.nativeElement.id = (selectedElement == "generate") ? "activeTab" : "inactiveTab";
          this.searchTabRef.nativeElement.id = (selectedElement == "search") ? "activeTab" : "inactiveTab";
      }
    
     onChangeTableRowLength(event) {
            this.showLoader = true;
            this.pageSize = event.target.value;
    
            this.currentPage = 1;
    
            let paginationDetails = {
                "count": parseInt(this.pageSize,10),
                "page": parseInt(this.currentPage)
            };
    
            this.paginationDetails = paginationDetails;
            this.paginationDisabbled = false;
            // Hide all the form/Table/Nodatafound5
            this.tableShowHide = false;
            
            setTimeout(() => {
                this.showLoader = false;
                $("#dataWrapper").find(".scrollBody").scrollLeft(0);
                this.getGenerateDetails();
            }, 0);
    
        }
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

    /* validates current submitted form is valid and free from errors
     * @param : pass the event
     * @retun : boolean
     */

    isValidForm(event) {
        return ($(event.target).parents("form").find(".error-border").length == 0) ? true : false;
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
  checkFormEnable(index){
    let indexValue = this.editableFormArray.indexOf(index);
    return indexValue >= 0?true:false;
 }
 closeModel(){
  this.successModalBlock.close();
  //this.generateTabBind();
  //this.ngOnInit();
    //this.getGenerateDetails();    
    this.generateTabBind();
    
}
  closeAndLogout(){
    //TODO : Need to set this.isLoggedIn global
    this.sessionExpiredModalBlock.close();
    this.sharedService.setUserLoggedIn(false);

    setTimeout(() => { 
      sessionStorage.clear();
      this.router.navigate(['/']); 
    }, 10);
  }
  

  compareFn(o1: any, o2: any) {
    return o1 && o2 ? o1.id === o2.id : o1 === o2;
  }

  onChangeDate(){
    this.errMessage = false;
    //this.getNeslist=[];    
    //this.neDetails =[];
    this.ciqNEFileDetails=[];
    if(this.fromDate && this.toDate){
      this.getCiqListData(this.fromDate,this.toDate);
      //this.ngOnInit();
    }else{        
      this.errMessage = true;
    }
  }

  changeSorting(predicate, event, index){
    this.sharedService.dynamicSort(predicate, event, index, this.tableData.csvAuditTrailDetModels);
  }
  
  changeSortingVal(predicate, event, index) {
    this.sharedService.dynamicSort(predicate, event, index, this.validateDetails);
}
  
  closeView()
  {
    this.viewModalBlock.close();
  }
  downloadErrorData()
  {
    setTimeout(()=>{
      var ele = document.getElementById('demo-table');        
      var test = ele.innerText.toString().replace(/,/g , "  ");
      var blob = new Blob([test], {
        type: "application/octet-stream"
          });
          FileSaver.saveAs(blob, "errorDetails.xlsx");
      
    },0);
  
  }

  toggleSitesTable() {
    if (!this.searchBlock) {
      // Called to reload the table data
      setTimeout(() => this.getGenerateDetails(), 100);
    }
  }
  viewOVFailureMessagae(failureErrorModal, key) {
    this.ovRetryData = {
        runTestId: key.id,
        ciqName: key.ciqName,
        neName: key.neName,
        useCaseName: key.useCase
    };
    this.showLoader = true;

    this.pregenerateService.viewOVFailureMessage(this.sharedService.createServiceToken(), key.id, key.lsmName, key.neName, key.useCase)
        .subscribe(
            data => {
                let jsonStatue = data.json();
                
                if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
                    this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, {
                        keyboard: false,
                        backdrop: 'static',
                        size: 'lg',
                        windowClass: 'session-modal'
                    });
                }
                else {
                    if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {

                        if (jsonStatue.status == "SUCCESS") {
                            this.showLoader = false;

                            this.milestoneResult = jsonStatue.milestones;
                            this.historyResult = jsonStatue.history;
                            this.failureMsgBlock = this.modalService.open(failureErrorModal, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView' });

                            setTimeout(() => {
                                var objDiv = document.getElementById("errorModalView");
                                objDiv.scrollTop = objDiv.scrollHeight;
                            }, 100);
                        } else {
                            this.showLoader = false;
                            this.displayModel(jsonStatue.reason, "failureIcon");
                        }
                    }
                }
            },
            error => {
                //Please Comment while checkIn

                /* setTimeout(() => {

                    this.showLoader = false;
                    
                    let jsonStatue = {"history":"[2022-09-12 18:39:49]-failed to fetch the Tracker ID\n[2022-09-12 18:43:26]-failed to fetch the Tracker ID for ID: 085079\n[2022-09-12 18:39:49]-failed to fetch the Tracker ID\n[2022-09-12 18:43:26]-failed to fetch the Tracker ID for ID: 085079\n[2022-09-12 18:39:49]-failed to fetch the Tracker ID\n[2022-09-12 18:43:26]-failed to fetch the Tracker ID for ID: 085079\n[2022-09-12 18:39:49]-failed to fetch the Tracker ID\n[2022-09-12 18:43:26]-failed to fetch the Tracker ID for ID: 085079\n[2022-09-12 18:39:49]-failed to fetch the Tracker ID\n[2022-09-12 18:43:26]-failed to fetch the Tracker ID for ID: 085079\n[2022-09-12 18:39:49]-failed to fetch the Tracker ID\n[2022-09-12 18:43:26]-failed to fetch the Tracker ID for ID: 085079\n[2022-09-12 18:39:49]-failed to fetch the Tracker ID\n[2022-09-12 18:43:26]-failed to fetch the Tracker ID for ID: 085079\n[2022-09-13 09:46:06]-failed to fetch the Tracker ID for ID: 085079\n[2022-09-13 09:49:47]-Successfully MileStone: 16300 Updated for NeId: 085079\n","sessionId":"ed7d9416","milestones":[{"customerDetailsEntity":null,"migrationType":null,"failedScript":null,"migrationSubType":null,"checklistFileName":null,"neName":null,"progressStatus":null,"ciqName":null,"outputFilepath":null,"testName":null,"testDescription":null,"lsmName":null,"lsmVersion":null,"useCase":null,"status":"pass","userName":null,"creationDate":null,"useCaseDetails":null,"customerId":0,"id":0,"wfmid":0,"useCaseSequence":0,"result":null,"resultFilePath":null,"fromDate":null,"toDate":null,"generateScriptPath":null,"migStatusDesc":null,"ovUpdateStatus":null,"name":"16300"},{"customerDetailsEntity":null,"migrationType":null,"failedScript":null,"migrationSubType":null,"checklistFileName":null,"neName":null,"progressStatus":null,"ciqName":null,"outputFilepath":null,"testName":null,"testDescription":null,"lsmName":null,"lsmVersion":null,"useCase":null,"status":"fail","userName":null,"creationDate":null,"useCaseDetails":null,"customerId":0,"id":0,"wfmid":0,"useCaseSequence":0,"result":null,"resultFilePath":null,"fromDate":null,"toDate":null,"generateScriptPath":null,"migStatusDesc":null,"ovUpdateStatus":null,"name":"16300"},{"customerDetailsEntity":null,"migrationType":null,"failedScript":null,"migrationSubType":null,"checklistFileName":null,"neName":null,"progressStatus":null,"ciqName":null,"outputFilepath":null,"testName":null,"testDescription":null,"lsmName":null,"lsmVersion":null,"useCase":null,"status":"pass","userName":null,"creationDate":null,"useCaseDetails":null,"customerId":0,"id":0,"wfmid":0,"useCaseSequence":0,"result":null,"resultFilePath":null,"fromDate":null,"toDate":null,"generateScriptPath":null,"migStatusDesc":null,"ovUpdateStatus":null,"name":"16300"},{"customerDetailsEntity":null,"migrationType":null,"failedScript":null,"migrationSubType":null,"checklistFileName":null,"neName":null,"progressStatus":null,"ciqName":null,"outputFilepath":null,"testName":null,"testDescription":null,"lsmName":null,"lsmVersion":null,"useCase":null,"status":"fail","userName":null,"creationDate":null,"useCaseDetails":null,"customerId":0,"id":0,"wfmid":0,"useCaseSequence":0,"result":null,"resultFilePath":null,"fromDate":null,"toDate":null,"generateScriptPath":null,"migStatusDesc":null,"ovUpdateStatus":null,"name":"16300"},{"customerDetailsEntity":null,"migrationType":null,"failedScript":null,"migrationSubType":null,"checklistFileName":null,"neName":null,"progressStatus":null,"ciqName":null,"outputFilepath":null,"testName":null,"testDescription":null,"lsmName":null,"lsmVersion":null,"useCase":null,"status":"pass","userName":null,"creationDate":null,"useCaseDetails":null,"customerId":0,"id":0,"wfmid":0,"useCaseSequence":0,"result":null,"resultFilePath":null,"fromDate":null,"toDate":null,"generateScriptPath":null,"migStatusDesc":null,"ovUpdateStatus":null,"name":"16300"},{"customerDetailsEntity":null,"migrationType":null,"failedScript":null,"migrationSubType":null,"checklistFileName":null,"neName":null,"progressStatus":null,"ciqName":null,"outputFilepath":null,"testName":null,"testDescription":null,"lsmName":null,"lsmVersion":null,"useCase":null,"status":"fail","userName":null,"creationDate":null,"useCaseDetails":null,"customerId":0,"id":0,"wfmid":0,"useCaseSequence":0,"result":null,"resultFilePath":null,"fromDate":null,"toDate":null,"generateScriptPath":null,"migStatusDesc":null,"ovUpdateStatus":null,"name":"16300"},{"customerDetailsEntity":null,"migrationType":null,"failedScript":null,"migrationSubType":null,"checklistFileName":null,"neName":null,"progressStatus":null,"ciqName":null,"outputFilepath":null,"testName":null,"testDescription":null,"lsmName":null,"lsmVersion":null,"useCase":null,"status":"pass","userName":null,"creationDate":null,"useCaseDetails":null,"customerId":0,"id":0,"wfmid":0,"useCaseSequence":0,"result":null,"resultFilePath":null,"fromDate":null,"toDate":null,"generateScriptPath":null,"migStatusDesc":null,"ovUpdateStatus":null,"name":"16300"},{"customerDetailsEntity":null,"migrationType":null,"failedScript":null,"migrationSubType":null,"checklistFileName":null,"neName":null,"progressStatus":null,"ciqName":null,"outputFilepath":null,"testName":null,"testDescription":null,"lsmName":null,"lsmVersion":null,"useCase":null,"status":"fail","userName":null,"creationDate":null,"useCaseDetails":null,"customerId":0,"id":0,"wfmid":0,"useCaseSequence":0,"result":null,"resultFilePath":null,"fromDate":null,"toDate":null,"generateScriptPath":null,"migStatusDesc":null,"ovUpdateStatus":null,"name":"16300"},{"customerDetailsEntity":null,"migrationType":null,"failedScript":null,"migrationSubType":null,"checklistFileName":null,"neName":null,"progressStatus":null,"ciqName":null,"outputFilepath":null,"testName":null,"testDescription":null,"lsmName":null,"lsmVersion":null,"useCase":null,"status":"pass","userName":null,"creationDate":null,"useCaseDetails":null,"customerId":0,"id":0,"wfmid":0,"useCaseSequence":0,"result":null,"resultFilePath":null,"fromDate":null,"toDate":null,"generateScriptPath":null,"migStatusDesc":null,"ovUpdateStatus":null,"name":"16300"},{"customerDetailsEntity":null,"migrationType":null,"failedScript":null,"migrationSubType":null,"checklistFileName":null,"neName":null,"progressStatus":null,"ciqName":null,"outputFilepath":null,"testName":null,"testDescription":null,"lsmName":null,"lsmVersion":null,"useCase":null,"status":"fail","userName":null,"creationDate":null,"useCaseDetails":null,"customerId":0,"id":0,"wfmid":0,"useCaseSequence":0,"result":null,"resultFilePath":null,"fromDate":null,"toDate":null,"generateScriptPath":null,"migStatusDesc":null,"ovUpdateStatus":null,"name":"16300"}],"serviceToken":"57118","status":"SUCCESS"};
                    if (jsonStatue.status == "SUCCESS") {
                        this.showLoader = false;
                        
                        this.milestoneResult = jsonStatue.milestones;
                        this.historyResult = jsonStatue.history;
                        this.failureMsgBlock = this.modalService.open(failureErrorModal, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView' });
                        
                        setTimeout(() => {
                            var objDiv = document.getElementById("errorModalView");
                            objDiv.scrollTop = objDiv.scrollHeight;
                        }, 100);
                    } else {
                        this.showLoader = false;
                        this.displayModel("jsonStatue.reason", "failureIcon");

                    }

                }, 100); */

                //Please Comment while checkIn   
            });
}

  ovRetry() {
      this.showInnerLoader = true;

      this.pregenerateService.retryMilestoneUpdate(this.sharedService.createServiceToken(), this.ovRetryData, this.milestoneResult)
          .subscribe(
              data => {
                  let jsonStatue = data.json();
                  
                  if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
                      this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, {
                          keyboard: false,
                          backdrop: 'static',
                          size: 'lg',
                          windowClass: 'session-modal'
                      });
                  }
                  else {
                      if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {

                          if (jsonStatue.status == "SUCCESS") {
                              this.showInnerLoader = false;
                              this.message = "Rerun successfully!";
                              this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                              this.closeModelFailureMsg();
                          } else {
                              this.showInnerLoader = false;
                              this.displayModel(jsonStatue.reason, "failureIcon");

                          }
                      }
                  }
              },
              error => {
                  //Please Comment while checkIn

                  /* setTimeout(() => {

                      this.showInnerLoader = false;
                      
                      let jsonStatue = {"reason":"","sessionId":"8909e5cf","serviceToken":"61626","status":"SUCCESS","messageInfo":"Connection refused by 1st API"};

                      if (jsonStatue.status == "SUCCESS") {
                          this.showInnerLoader = false;
                          this.message = "Rerun successfully!";
                          this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                          this.closeModelFailureMsg();
                      } else {
                          this.showInnerLoader = false;
                          this.displayModel(jsonStatue.reason, "failureIcon");
                      }

                  }, 100); */

                  //Please Comment while checkIn   
              });
  }
  closeModelFailureMsg() {
    this.errorMsgResult = "";
    this.successMsgResult = "";
    this.milestoneResult = [];
    this.historyResult = "";
    this.ovRetryData = {};
    this.failureMsgBlock.close();
}
ovRetryFailedMilestone(failedMilestone: any) {
  this.showInnerLoader = true;

  this.pregenerateService.retryMilestoneUpdate(this.sharedService.createServiceToken(), this.ovRetryData, failedMilestone)
      .subscribe(
          data => {
              let jsonStatue = data.json();
              
              if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
                  this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, {
                      keyboard: false,
                      backdrop: 'static',
                      size: 'lg',
                      windowClass: 'session-modal'
                  });
              }
              else {
                  if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {

                      if (jsonStatue.status == "SUCCESS") {
                          this.showInnerLoader = false;
                          this.message = "Rerun successfully!";
                          this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                          this.closeModelFailureMsg();
                      } else {
                          this.showInnerLoader = false;
                          this.displayModel(jsonStatue.reason, "failureIcon");

                      }
                  }
              }
          },
          error => {
              //Please Comment while checkIn

              /* setTimeout(() => {

                  this.showInnerLoader = true;
                  
                  let jsonStatue = {"reason":"","sessionId":"8909e5cf","serviceToken":"61626","status":"SUCCESS","messageInfo":"Connection refused by 1st API"};

                  if (jsonStatue.status == "SUCCESS") {
                      this.showInnerLoader = false;
                      this.message = "Rerun successfully!";
                      this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                      this.closeModelFailureMsg();
                  } else {
                     this.showInnerLoader = false;
                      this.displayModel(jsonStatue.reason, "failureIcon");
                  }

              }, 100); */

              //Please Comment while checkIn   
          });
}
}
