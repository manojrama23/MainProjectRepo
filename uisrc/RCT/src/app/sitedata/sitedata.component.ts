import { ViewContainerRef, Input, ElementRef, Renderer, Component, OnInit, ViewChild, HostListener } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { NgbModal, ModalDismissReasons } from '@ng-bootstrap/ng-bootstrap';
import { PregrowService } from '../services/pregrow.service';
import { SharedService } from '../services/shared.service';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { filter, map, mergeMap } from 'rxjs/operators';
import { validator } from '../validation';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { SitedataService } from '../services/sitedata.service';
import * as FileSaver from 'file-saver';
import * as _ from 'underscore';
import * as $ from 'jquery';
import { DatePipe } from '@angular/common';
import { error } from 'protractor';

@Component({
    selector: 'app-sitedata',
    templateUrl: './sitedata.component.html',
    styleUrls: ['./sitedata.component.scss'],
    providers: [SitedataService]
})

export class SitedataComponent implements OnInit {
    ciqConfig: object;
    emailId:any;
    siteDataModelsTableData: any=[];
    dropdownSettings = {};
    dropdownSettingsNEs = {};
    selectedEnb: any;
    getEndList: any;
    enbListData: any;
    selectedCiq: any;
    tableData: any;
    showNoDataFound: boolean;
    tableShowHide: boolean= false;
    showLoader: boolean = true;
    searchBlock: boolean = false;
    createNewForm: boolean = false;
    tableDataHeight: any;
    expandSerchRow: boolean = false;
    pageCount: any; // for pagination
    currentPage: any; // for pagination
    pageSize: any; // for pagination
    totalPages: any; // for pagination
    TableRowLength: any; // for pagination
    paginationDetails: any; // for pagination
    pageRenge: any; // for pagination
    paginationDisabbled: boolean = false;
    editableFormArray = [];
    fileRuleRowId: any;
    // Following variables are used to dispaly success, confirm and failure model(s)
    showModelMessage: boolean = false;
    modelData: any;
    closeResult: string;
    currentEditRow: any;
    // To track activity
    searchStatus: string;
    status:any = "";;
    operator:any = "";;
    searchCriteria: any;
    sessionExpiredModalBlock: any; // Helps to close/open the model window
    pager: any = {}; // pager Object
    message: any;
    successModalBlock: any;
    navigationSubscription: any;
    fromDate:any;
    toDate:any;
    errMessage:boolean= false;
    ciqListData;any;
    getSiteDataList:any=[];
    searchCiqList:any=[];
    noDataVisibility :boolean = false;
    max = new Date();
    sStartDate:any;
    sEndDate:any;
    programChangeSubscription:any;
    networkType: any = "";
    programName: any = "";
    getNESiteList:any=[];
    dropdownListNE = [];
    selectedItemsNE: any;
    nenamebutton:boolean =true;
    siteNEsModalBlock: any;
    siteCompReportBlock: any;
    reportData: any;
    carriersFlag: any;
    criticalCheckInfoList: any;
    reportFormOptions: any;
    issuesCategory: any;
    issueAttributeToList: any;
    issueTechnologyList: any;
    issueResolvedList: any;
    showInnerLoader: boolean = false;
    timelineIssuesList: any = [];
    timelineList: any = [];
    troubleshootTimelineList: any = [];
    currIssueEditScript: any = null;
    currTimelineEditScript: any = null;
    userGroupDetails : any;
    siteReportNE: any;
    selectedTableRow: any;
    selectedReportCIQ: any;
    postAuditIssues;//[{"testName":"ENB State Check","yangCommand":"managed-element enb-function eutran-generic-cell eutran-cell-fdd-tdd usage-state","test":"To check if eNB is in the correct operational mode and its admin state","expectedResult":"operational-state: enabled, administrative-state: unlocked, operational-mode: normal-mode","actionItem":"Change administrative-state: unlocked.\n Change operational-mode: normal-mode. Check Cell state\n Check alarms if eNB continues to be disabled","auditIssue":"1. CellNum : 18 administrative-state : locked \n2. CellNum : 32 administrative-state : locked \n3. CellNum : 34 administrative-state : locked","remarks":""},{"testName":"ENB State Check","yangCommand":"managed-element enb-function eutran-generic-cell eutran-cell-fdd-tdd usage-state","test":"To check if eNB is in the correct operational mode and its admin state","expectedResult":"operational-state: enabled, administrative-state: unlocked, operational-mode: normal-mode","actionItem":"Change administrative-state: unlocked.\n Change operational-mode: normal-mode. Check Cell state\n Check alarms if eNB continues to be disabled","auditIssue":"1. CellNum : 18 operational-mode : growth-mode \n2. CellNum : 32 operational-mode : growth-mode \n3. CellNum : 34 operational-mode : growth-mode","remarks":""}];//[{"command":"managed-element enb-function eutran-generic-cell eutran-cell-fdd-tdd usage-state","test":"To check if eNB is in the correct operational mode and its admin state","expectedResult":"operational-state: enabled,\nadministrative-state: unlocked, \noperational-mode: normal-mode","actionItem":"Change administrative-state: unlocked.\n Change operational-mode: normal-mode. Check Cell state\n Check alarms if eNB continues to be disabled","failure":{"cell-num":[18,22,32],"active-ue-count":[0,0,0]}},{"command":"managed-element enb-function eutran-generic-cell eutran-cell-fdd-tdd operational-state","test":"All cells unlocked and enabled.","expectedResult":"[ON ALL CELLS]:\noperational-state: enabled,\nadministrative-state: unlocked,\ns1-ap state: enabled","actionItem":"If administrative-state is locked, unlock the cell \nCheck SCTP and S1AP state \nPing MME \nCheck routing information","failure":{"cell-num":[18,22,32],"operational-state":["disabled","disabled","disabled"]}},{"command":"active-alarm-entries","test":"Active alarms","expectedResult ":"Should be 0 alarms","actionItem":"Refer to 'Ask Me' document for alarm. Refer to C&I database of alarm debugging and actions to debug it","failure":{"cell-num":[18,22,32],"administrative-state":["locked","locked","locked"]}}];
    siteReportConfiguredEmails = ["abc@xyz.com", "abc1@xyz.com", "abcdef_ghij@xyzabc.com"];
    // isCancellationReport:boolean = false;
    ovRetryData: any;
    failureMsgBlock: any;
    milestoneResult: any;
    historyResult: any;
    validationData: any = {
        "rules": {
            "ciqName":{
                "required":true
            },
            "nes":{
                "required":true
            },
            "remarks":{
                "required":false
            }
        },
        "messages": {
            "ciqName":{
                "required":"Please select CIQ List"
            },
            "nes":{
                "required":"Please select NE Name"
            },
            "remarks":{
                "required":"Remarks is required"
            }

        }
    };

    @ViewChild('searchTab') searchTabRef;
    @ViewChild('packDataTab') packDataTabRef;
    @ViewChild('packForm') packForm;
    @ViewChild('sessionExpiredModal') sessionExpiredModalRef: ElementRef;
    @ViewChild('successModal') successModalRef: ElementRef;
    @ViewChild('searchForm') searchForm;

    constructor(
        private element: ElementRef,
        private renderer: Renderer,
        private router: Router,
        private modalService: NgbModal,
        private siteDataService: SitedataService,
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
        this.tableDataHeight = (($("#contentWrapper").height()) - ($("#container").height() + $(".mainHead").height() + $(".nav").height() + 50));
        this.searchBlock = false;
        this.createNewForm = true;
        this.searchStatus = 'load';
        this.showLoader = true;
        this.fromDate = null;
        this.toDate = null;
        this.setMenuHighlight("packData"); 
        this.emailId  = JSON.parse(sessionStorage.loginDetails).emailId;
        //For Pagination
        this.currentPage = 1;
        this.totalPages = 1;
        this.TableRowLength = 10;
        this.pageSize = 10;
        this.showInnerLoader = false;
        let paginationDetails = {
            "count": this.TableRowLength,
            "page": this.currentPage
        };
        this.paginationDetails = paginationDetails;
        this.selectedEnb=null;
        this.getSiteDataList = [];
        this.selectedCiq = [];
        this.ovRetryData = {}
        this.ciqConfig = {
            displayKey: "ciqFileName",
            search: true,
            height: '200px',
            placeholder: '--Select--',
            customComparator: () => { },
           // limitTo: this.getSiteDataList.length,
            moreText: 'more',

            noResultsFound: 'No results found!',
            searchPlaceholder: 'Search',
            searchOnKey: 'ciqFileName',
        }
        this.siteReportNE = null;
        this.selectedTableRow = null;
        this.selectedReportCIQ = null;
        this.userGroupDetails = JSON.parse(sessionStorage.loginDetails).userGroup;
        this.reportData = {"neName":"","neId":"","reportDate":"","siteReportStatus":"","eNodeBName":"","eNodeBSW":"","fsuSW":"","vDUSW":"","project":"","softWareRelease":"","market":"","fuzeProjId":"","resAuditIssueCheck":false,"integrationType":"","vendorType":"","mmCommComp":"","mmOpsATP":"","dssCommComp":"","dssOpsATP":"","fsuIntegBypass":"","fsuIntegMultiplex":"","finalIntegStatus":"","currCBANDIntegStatus":"","finalCBANDIntegStatus":"","typeOfEffort":"","remarks":"","timeLineDetails":"","categoryDetails":"","lteCommComp":"","lteCBRSCommComp":"","lteLAACommComp":"","lteOpsATP":"","lteCBRSOpsAtp":"","lteLAAOpsATP":"","tcReleased":"","ovTicketNum":""};//, "emailConfigured":[]
        this.carriersFlag = {"700":{"likeforlikeCheckBox":false,"incrementalCheckBox":false},"850":{"likeforlikeCheckBox":false,"incrementalCheckBox":false},"AWS":{"likeforlikeCheckBox":false,"incrementalCheckBox":false},"PCS":{"likeforlikeCheckBox":false,"incrementalCheckBox":false},"AWS3":{"likeforlikeCheckBox":false,"incrementalCheckBox":false},"CBRS":{"likeforlikeCheckBox":false,"incrementalCheckBox":false},"LAA":{"likeforlikeCheckBox":false,"incrementalCheckBox":false}};//{"likeForLike":{"700":false,"850":false,"AW":false,"PCS":false,"AWS":false,"CBRS":false,"IAA":false},"incremental":{"700":false,"850":false,"AW":false,"PCS":false,"AWS":false,"CBRS":false,"IAA":false}};
        this.resetReportTimelineIssueForm();
        this.getAllSiteData();
        this.getSiteCiqList(this.fromDate,this.toDate);
        this.resetPackForm();

        setTimeout(() => {
            //this.showLoader = false;
            validator.performValidation(event, this.validationData, "search");
        }, 1000);

    }

    ngOnDestroy() {
        this.programChangeSubscription.unsubscribe();
        // avoid memory leaks here by cleaning up after ourselves. If we  
        // don't then we will continue to run our ngOnInit()   
        // method on every navigationEnd event.
        if (this.navigationSubscription) {
            this.navigationSubscription.unsubscribe();
        }
    }

    resetPackForm() {
        setTimeout(() => {           
            this.packForm.nativeElement.reset();
        }, 0);
    }
    
    searchTabBind() {
        this.currentPage = 1;
        let searchCrtra = { "fileName": "", "ciqFileName": "", "neName": "", "searchStartDate": "", "searchEndDate": "" };
        this.searchCriteria = searchCrtra;

        this.setMenuHighlight("search");
        this.searchBlock = true;
        this.createNewForm = false;
        this.searchStatus = 'load';
        this.tableShowHide = true;
        this.getAllSiteData();
        // Close if edit form is in open state
        if (this.currentEditRow != undefined) {
            this.currentEditRow.className = "editRow";
        }
        this.editableFormArray = [];
        /* setTimeout(() => {
            validator.performValidation(event, this.validationData, "search");
        }, 10); */

        this.searchForm.nativeElement.reset();

    }
    clearSearchFrom() {
        this.searchForm.nativeElement.reset();
    }
  

    packDataTabBind() {
        this.showNoDataFound = false;
        this.tableShowHide = false;
        this.searchBlock = false;
        this.createNewForm = true;
        this.setMenuHighlight("packData");
        this.selectedCiq =[];
        this.selectedEnb=null;
        this.editableFormArray = [];
        this.getAllSiteData();
        this.getSiteCiqList(this.fromDate,this.toDate);
        setTimeout(() => {
            validator.performValidation(event, this.validationData, "create");
        }, 10);
    }

    /*
     * Used to dispaly search result based on selected criteria
     * @param : event
     * @retun : null
     */

    searchSiteData(event) {
        this.tableShowHide = false;
        setTimeout(() => {
            $("#dataWrapper").find(".scrollBody").scrollLeft(0);
        }, 0);

        validator.performValidation(event, this.validationData, "search");
        setTimeout(() => {
            if (this.isValidForm(event)) {
                if (!event.target.classList.contains('buttonDisabled')) {
                    this.showLoader = true;

                    // To hide the No Data Found and REMOVAL DETAILS Form
                    this.createNewForm = false;
                    this.showNoDataFound = false;

                    let currentForm = event.target.parentNode.parentNode,
                        searchCrtra = { 
                            "fileName": currentForm.querySelector("#searchFileName").value,
                            "ciqFileName": currentForm.querySelector("#searchCiqName").value,   
                            "neName": currentForm.querySelector("#searchNeName").value,
                            "searchStartDate": currentForm.querySelector("#searchStartDate").value,
                            "searchEndDate":currentForm.querySelector("#searchEndDate").value                            
                        };
                    if (searchCrtra.searchStartDate || searchCrtra.searchEndDate || searchCrtra.neName || searchCrtra.ciqFileName || searchCrtra.fileName) {
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
                    this.getAllSiteData();
                }
            }
        }, 0);
    }

    getAllSiteData() { 
        this.tableShowHide = false;    
        this.showLoader = true;
        $("#dataWrapper").find(".scrollBody").scrollLeft(0);
        this.siteDataService.getAllSiteData(this.searchStatus, this.searchCriteria, this.sharedService.createServiceToken(),this.paginationDetails)
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
                               this.totalPages = this.tableData.pageCount;
                               this.networkType = JSON.parse(sessionStorage.getItem("selectedProgram")).networkTypeDetailsEntity.networkType;
                               this.programName = JSON.parse(sessionStorage.getItem("selectedProgram")).programName;   
                               let pageCount = [];
                                  for (var i = 1; i <= this.tableData.pageCount; i++) {
                                      pageCount.push(i);
                                  }
                                  this.pageRenge = pageCount;
                                  this.pager = this.getPager(this.totalPages*this.pageSize, this.currentPage, this.pageRenge);
  
                                  if(this.tableData.siteDataModels.length == 0){
                                    this.tableShowHide = false;
                                    this.noDataVisibility = true;
                                  }else{
                                    this.searchCiqList = this.tableData.ciqList;                                    
                                    this.tableShowHide = true;
                                    this.noDataVisibility = false;
                                    setTimeout(() => {
                                      let tableWidth = document.getElementById('siteDataTable').scrollWidth;
                                       
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
                                      
                  }, 100);
              },
              error => {
                //Please Comment while checkIn
                /* setTimeout(() => {
                this.showLoader = false;                  
                this.tableData = {"searchStartDate":"02/11/2021","pageCount":1,"searchEndDate":"08/30/2021","siteDataModels":[{"id":513,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"fileName":"SiteCompletionReport_20210830_10_11_40.xlsx","filePath":"/home/bala/Samsung/SMART/Network_config_Details//","ciqFileName":"ciq1","neName":"061452_CONCORD_2_NH_HUB","remarks":"","packedBy":"","packedDate":"2021-08-30 10:11:48","searchStartDate":null,"searchEndDate":null,"siteReportStatus":"Exception","neId":"61452","siteName":null,"reportType":"SITE","ovSiteReportStatus":"SUCCESS"}],"neNameList":["070177_AMERICAN_STORAGE","070215_HOUGHTON","071299_FORT_EDWARD","070282_MTSO","070356_BUFFALO_WATERFRONT","061452_CONCORD_2_NH_HUB"],"ciqList":["UNY-NE-VZ_CIQ_Ver3.7.300_08052020.xlsx","UNY-NE-VZ_CIQ_Ver3.7.48_08242020.xlsx","UNY-NE-VZ_CIQ_Ver3.6.6_03252020.xlsx","UNY-NE-VZ_CIQ_Ver3.7.28_07242020.xlsx","ciq1","UNY-NE-VZ_CIQ_Ver3.7.367_08052020.xlsx","AthithyaUNY-NE-VZ_CIQ_Ver3.7.96_11022020.xlsx","UNY-NE-VZ_CIQ_Ver3.6.44_04142020.xlsx"],"sessionId":"4ea833f3","serviceToken":"84679","fileList":["SITE_DATA_070215_HOUGHTON_10202020_15_41_53.zip","SITE_DATA_070215_HOUGHTON_12092020_16_27_37.zip","SiteCompletionReport_20210830_10_11_40.xlsx","SITE_DATA_070177_AMERICAN_STORAGE_08122020_10_22_19.zip","SITE_DATA_070282_MTSO_08112020_10_58_15.zip","SITE_DATA_070356_BUFFALO_WATERFRONT_07272020_16_38_08.zip","SITE_DATA_070215_HOUGHTON_10202020_16_47_50.zip","SITE_DATA_070282_MTSO_04092020_11_01_13.zip","SITE_DATA_071299_FORT_EDWARD_08112020_16_21_32.zip","SITE_DATA_070215_HOUGHTON_10222020_14_08_39.zip","SITE_DATA_070282_MTSO_04172020_12_39_40.zip"],"status":"SUCCESS"};
                this.networkType = JSON.parse(sessionStorage.getItem("selectedProgram")).networkTypeDetailsEntity.networkType;
                this.programName = JSON.parse(sessionStorage.getItem("selectedProgram")).programName; 
                this.totalPages = this.tableData.pageCount;
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
                    
                      if(this.tableData.siteDataModels.length == 0){
                        this.tableShowHide = false;
                        this.noDataVisibility = true;
                      }else{
                        this.searchCiqList = this.tableData.ciqList;
                        
                        this.tableShowHide = true;
                        this.noDataVisibility = false;
                        setTimeout(() => {
                          let tableWidth = document.getElementById('siteDataTable').scrollWidth;
                          
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
                }, 100); */
               
                //Please Comment while checkIn
          });
    }


    /*
     * on click of edit row enable corresponding
     * @param : current row index
     * @retun : boolean
     */

    checkFormEnable(index) {
        let indexValue = this.editableFormArray.indexOf(index);
        return indexValue >= 0 ? true : false;
    }


    /*
     * Used to display the table data on load/by default
     * @param : repairStation,userName,reflect (edit/delete)
     * @retun : null
     */

    getSiteCiqList(fromDate,toDate) {
        this.showLoader=true;
        let fromDt,toDt;
        if(fromDate & toDate){
          fromDt = this.datePipe.transform(fromDate,"MM/dd/yyyy"); // On change Date,Update Row
          toDt = this.datePipe.transform(toDate,"MM/dd/yyyy");
          this.searchStatus = "search"; 
        }else{
          fromDt = null;//Loading Page fromDate and toDate is null
          toDt = null;
          this.searchStatus = "load";      
        }
        
        this.siteDataService.getSiteCiqList(this.searchStatus, this.searchCriteria, this.sharedService.createServiceToken(),fromDt,toDt)
        .subscribe(
            data => {
                setTimeout(() => { 
                  let jsonStatue = data.json();
                //   this.showLoader = false;
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
                                this.fromDate = new Date( jsonStatue.fromDate);
                                this.toDate = new Date(jsonStatue.toDate);
                                this.getSiteDataList = jsonStatue.getCiqList;  
                                
                                let getSelectedCIQ = JSON.parse(sessionStorage.getItem("selectedCIQ"));
                                if (this.getSiteDataList.length > 0 && getSelectedCIQ) {
                                    this.selectedCiq = getSelectedCIQ;
                                }
                                else {
                                    this.selectedCiq = this.getSiteDataList.length > 0 ? this.getSiteDataList[0] : null;
                                    // Update Session storage for selectedCIQ
                                    this.sharedService.updateSelectedCIQInSessionStorage(this.selectedCiq);
                                }

                                this.getENBData();

                                /* if(this.getSiteDataList.length > 0){
                                    this.selectedCiq = this.getSiteDataList[0];
                                    this.getENBData();
                                } */
                                this.selectedEnb=null;
                                //  this.showLoader = false;
                                 setTimeout(() => {
                                  let tableWidth = document.getElementById('siteDataTable').scrollWidth;
                                          $(".scrollBody table").css("min-width",(tableWidth) + "px");
                                          $(".scrollHead table").css("width", tableWidth + "px"); 
                                          $(".scrollBody").on("scroll", function (event) {
                                              $(".formEditRow #formWrapper").css("margin-left", (event.target.scrollLeft - 5) + "px");
                                              $(".form-control-fixed").css("right",(event.target.scrollLeft * -1) + "px");
                                              $(".scrollHead table").css("margin-left",(event.target.scrollLeft * -1) + "px");
                                          });
                                          $(".scrollBody").css("max-height",(this.tableDataHeight - 10) + "px");  
                                  },0);                           
                          }else{
                            this.showLoader = false;
                            this.displayModel(jsonStatue.reason,"failureIcon");
                          }
                        }   
                      }
                                    
                }, 100);
            },
            error => {
              //Please Comment while checkIn
              /* setTimeout(() => { 
                // this.showLoader = false;
                // no data 
               // this.ciqListData = {"fromDate":"03/20/2019","toDate":"03/27/2019","sessionId":"e5f6854","serviceToken":"83600","getCiqList":[],"status":"SUCCESS"};
                this.ciqListData ={"fromDate":"03/20/2019","toDate":"03/27/2019","sessionId":"e5f6854","serviceToken":"83600","getCiqList":[{"id":5,"programDetailsEntity":{"id":1,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"VZN-4G-LSM","programDescription":"LSM","status":"Inactive","creationDate":"2019-03-25T12:53:38.000+0000","createdBy":"superadmin"},"ciqFileName":"UNY-NE-VZ_CIQ_Ver2.82_01282019.xlsx","ciqFilePath":"Customer/1/PreMigration/Input/UNY-NE-VZ_CIQ_Ver2.82_01282019/CIQ/","scriptFileName":"1_1111_LEXINGTON_12_MA.zip","scriptFilePath":"Customer/1/PreMigration/Input/UNY-NE-VZ_CIQ_Ver2.82_01282019/SCRIPT/","checklistFileName":"Verizon_Migration Checklist 100418_version4.4.xlsx","checklistFilePath":"Customer/1/PreMigration/Input/UNY-NE-VZ_CIQ_Ver2.82_01282019/CHECKLIST/","ciqVersion":"ORIGINAL","fileSourceType":"UPLOAD","uploadBy":"superadmin","remarks":"","creationDate":"2019-03-22T13:34:29.000+0000"}],"status":"SUCCESS"};
                  if(this.ciqListData.sessionId == "408" || this.ciqListData.status == "Invalid User"){
                     this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal modalWidth scriptView'});
                    }
                    if(this.ciqListData.status == "SUCCESS"){
                            this.fromDate = new Date( this.ciqListData.fromDate);
                            this.toDate = new Date(this.ciqListData.toDate);
                            this.getSiteDataList = this.ciqListData.getCiqList;  
                            
                            let getSelectedCIQ = JSON.parse(sessionStorage.getItem("selectedCIQ"));
                            if (this.getSiteDataList.length > 0 && getSelectedCIQ) {
                                this.selectedCiq = getSelectedCIQ;
                            }
                            else {
                                this.selectedCiq = this.getSiteDataList.length > 0 ? this.getSiteDataList[0] : null;
                                // Update Session storage for selectedCIQ
                                this.sharedService.updateSelectedCIQInSessionStorage(this.selectedCiq);
                            }

                            this.getENBData();
                            
                            // if(this.getSiteDataList.length > 0){
                            //     this.selectedCiq = this.getSiteDataList[0];
                            //     this.getENBData();
                            // }
                            this.selectedEnb=null;
                            //  this.showLoader = false;
                             setTimeout(() => {
                              let tableWidth = document.getElementById('siteDataTable').scrollWidth;
                                      $(".scrollBody table").css("min-width",(tableWidth) + "px");
                                      $(".scrollHead table").css("width", tableWidth + "px"); 
                                      $(".scrollBody").on("scroll", function (event) {
                                          $(".formEditRow #formWrapper").css("margin-left", (event.target.scrollLeft - 5) + "px");
                                          $(".form-control-fixed").css("right",(event.target.scrollLeft * -1) + "px");
                                          $(".scrollHead table").css("margin-left",(event.target.scrollLeft * -1) + "px");
                                      });
                                      $(".scrollBody").css("max-height",(this.tableDataHeight - 10) + "px");  
                              },0);                          
                      }else{
                        this.showLoader = false;
                        this.displayModel(this.ciqListData.reason,"failureIcon");
                      }
              }, 100); */
              //Please Comment while checkIn
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

    /* validates current submitted form is valid and free from errors
     * @param : pass the event
     * @retun : boolean
     */

    isValidForm(event) {
        return ($(event.target).parents("form").find(".error-border").length == 0) ? true : false;
    }




    /* Used to add new Airline config details 
     * @param : event
     * @retun : null
     */
    packSiteData(event) {
        if (this.selectedCiq != "" && this.selectedCiq != undefined && this.selectedCiq.ciqFileName) {
            this.validationData.rules.ciqName.required = false;

        } else {
            this.validationData.rules.ciqName.required = true;

        }
        if(this.programName != "VZN-5G-MM") {
            if (this.selectedEnb) {
                this.validationData.rules.nes.required = false;
        
            } else {
                this.validationData.rules.nes.required = true;
            }
        }
        else {
            if(this.selectedItemsNE && this.selectedItemsNE.length > 0) {
                this.validationData.rules.nes.required = false;
            } else {
                this.validationData.rules.nes.required = true;
            }
        }
        
        // TODO : For 5G-MM, get data from this.selectedItemsNE
        validator.performValidation(event, this.validationData, "save_update");
        setTimeout(() => {

            if (this.isValidForm(event)) {

                this.showLoader = true;
                let currentForm = event.target.parentNode.parentNode,
                    sitePackData = {
                        "remarks": currentForm.querySelector("#remarks").value
                    };

                let enbDetails = [];
                if(this.programName != "VZN-5G-MM") {
                    let tempObj = {
                        "enbName": this.selectedEnb.eNBName,
                        "enbId": this.selectedEnb.eNBId
                    }
                    enbDetails.push(tempObj);
                }
                else {
                    for(let i = 0; i < this.selectedItemsNE.length; i++) {
                        let tempObj = {
                            "enbName": this.selectedItemsNE[i].item_text,
                            "enbId": this.selectedItemsNE[i].item_id
                        }
                        enbDetails.push(tempObj);
                    }
                }
                this.siteDataService.packSiteData(this.selectedCiq.ciqFileName, sitePackData.remarks, this.selectedEnb.eNBName, this.selectedEnb.eNBId, this.sharedService.createServiceToken(), enbDetails)
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
                            } else {

                                if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {

                                    if (jsonStatue.status == "SUCCESS") {
                                       /*  this.searchTabBind();
                                        setTimeout(() => { */
                                            this.showLoader = false;
                                            this.message = "Packed successfully!";
                                            this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                                            this.searchStatus = "load";
                                      //  }, 1000);
                                    } else {
                                        this.showLoader = false;
                                        this.displayModel(jsonStatue.reason, "failureIcon");
                                    }
                                }
                            }
                        },
                        error => {
                            //Please Comment while checkIn

                          /*   setTimeout(() => {
              
                              this.showLoader = false;
              
                              let jsonStatue = { "sessionId": "e9004f23", "reason": null, "status": "SUCCESS", "serviceToken": "64438" };
              
                              if (jsonStatue.status == "SUCCESS") {
                                //this.searchTabBind();
                              //  setTimeout(() => {
                                    this.showLoader = false;
                                    this.message = "Packed successfully!";
                                    this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                                    this.searchStatus = "load";
                               // }, 1000);
                            } else {

                                this.showLoader = false;
                                this.displayModel(jsonStatue.reason, "failureIcon");
                            }
              
                            }, 1000); */

                            //Please Comment while checkIn
                        });
            }
        }, 0);
    }

    getENBData(updateSessionStorage = false){    
        if (this.selectedCiq) {
            this.showLoader = true;
            this.selectedEnb = null;
            // Update the sessionStorage selected CIQ if CIQ list is getting changed from UI dropdown
            updateSessionStorage ? this.sharedService.updateSelectedCIQInSessionStorage(this.selectedCiq) : "";
            if(this.programName != 'VZN-5G-MM') {
                this.siteDataService.getENBData(this.selectedCiq, this.sharedService.createServiceToken() )
                    .subscribe(
                        data => {
                            setTimeout(() => { 
                            let jsonStatue = data.json();
                            this.enbListData = data.json();
                            this.showLoader = false;
                                if(jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User"){
                                this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                                } else {
                                    if( this.sharedService.isvalidServiceToken( parseInt(data.json().serviceToken,10) ) || data.json().sessionId == "408"){
                                    if(this.enbListData.status == "SUCCESS"){
                                        this.getEndList = this.enbListData.eNBList;
                                        /* let getNeslistStore = this.enbListData.eNBList;
                                        for (let itm of getNeslistStore) {
                                            let dropdownList = { item_id: itm.eNBId, item_text: itm.eNBName };
                                            this.getEndList.push(dropdownList);
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
                                        this.getEndList = [];
                                    }
                                    }   
                                }
                                                
                            }, 100);
                        },
                        error => {
                        //Please Comment while checkIn
                        /* setTimeout(() => { 
                            this.showLoader = false;
                            this.enbListData = JSON.parse('{"eNBList":[{"eNBName":"061192_NORTHWOOD_LAKE_NH","eNBId":"61192"},{"eNBName":"061452_CONCORD_2_NH_HUB","eNBId":"61452"},{"eNBName":"073461_PRATTSBURGH","eNBId":"73461"},{"eNBName":"073462_East_Corning","eNBId":"73462"},{"eNBName":"073466_Howard","eNBId":"73466"},{"eNBName":"073474_Hornellsville","eNBId":"73474"},{"eNBName":"073484_ADDISON","eNBId":"73484"},{"eNBName":"072409_Press_Building","eNBId":"72409"},{"eNBName":"072412_Binghamton_DT","eNBId":"72412"},{"eNBName":"072413_SUNY_Binghamton","eNBId":"72413"},{"eNBName":"072415_Vestal","eNBId":"72415"},{"eNBName":"072416_Chenango","eNBId":"72416"},{"eNBName":"072417_Kirkwood","eNBId":"72417"},{"eNBName":"072419_Windsor","eNBId":"72419"},{"eNBName":"072424_CASTLE_CREEK","eNBId":"72424"},{"eNBName":"072425_Killawog","eNBId":"72425"},{"eNBName":"072426_East_Richford","eNBId":"72426"},{"eNBName":"072427_Caroline","eNBId":"72427"},{"eNBName":"072430_Owego_North","eNBId":"72430"},{"eNBName":"072431_Owego","eNBId":"72431"},{"eNBName":"072432_Apalachin","eNBId":"72432"},{"eNBName":"072433_Nichols","eNBId":"72433"},{"eNBName":"072442_CROCKER_CREEK","eNBId":"72442"},{"eNBName":"072443_MAINE_DT","eNBId":"72443"},{"eNBName":"072451_BELDEN","eNBId":"72451"},{"eNBName":"072452_TIOGA_CENTER","eNBId":"72452"},{"eNBName":"072454_CATATONK","eNBId":"72454"},{"eNBName":"072458_CHENANGO_DT","eNBId":"72458"},{"eNBName":"072478_Big_Flats","eNBId":"72478"},{"eNBName":"070033_POWERS_RD","eNBId":"70033"},{"eNBName":"070005_RTE_263_GETZVILLE","eNBId":"70005"},{"eNBName":"070562_BOWEN_RD","eNBId":"70562"},{"eNBName":"073313_FREY_RD","eNBId":"73313"},{"eNBName":"073326_BAKER_RD","eNBId":"73326"}],"sessionId":"2a7a3636","serviceToken":"79044","status":"SUCCESS"}');
                            if(this.enbListData.sessionId == "408" || this.enbListData.status == "Invalid User"){
                                this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                                }
                            if(this.enbListData.status == "SUCCESS"){
                                this.getEndList = this.enbListData.eNBList;
                            //     let getNeslistStore = this.enbListData.eNBList;
                            //     for (let itm of getNeslistStore) {
                            //       let dropdownList = { item_id: itm.eNBId, item_text: itm.eNBName };
                            //       this.getEndList.push(dropdownList);
                            //   }
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
                                this.getEndList = [];
                            }
                        }, 100); */
                        //Please Comment while checkIn
                    });
            }
            else {
                this.siteDataService.getNeSiteListData(this.selectedCiq, this.sharedService.createServiceToken() )
                .subscribe(
                    data => {
                        // setTimeout(() => { 
                        let jsonStatue = data.json();
                        this.showLoader = false;
                            if(jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User"){
                            this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                            } else {
                                if( this.sharedService.isvalidServiceToken( parseInt(data.json().serviceToken,10) ) || data.json().sessionId == "408"){
                                    if(jsonStatue.status == "SUCCESS") {
                                        this.getNESiteList = jsonStatue.siteList;
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
                                    } else{
                                        this.showLoader = false;
                                        this.getEndList = [];
                                    }
                                }
                            }
                                            
                        // }, 1000);
                    },
                    error => {
                    //Please Comment while checkIn
                    /* setTimeout(() => { 
                        this.showLoader = false;
                        let jsonStatue = JSON.parse('{"siteList":{"CA_SAC_SACRAMENTODT_252":[{"eNBName":"03600010055_5GDU_CA_SAC_SACRAMENTODT_252","eNBId":"3600010055","siteName":"CA_SAC_SACRAMENTODT_252"},{"eNBName":"03600010056_5GDU_CA_SAC_SACRAMENTODT_252","eNBId":"3600010056","siteName":"CA_SAC_SACRAMENTODT_252"},{"eNBName":"03600010057_5GDU_CA_SAC_SACRAMENTODT_252","eNBId":"3600010057","siteName":"CA_SAC_SACRAMENTODT_252"}],"CA_SAC_OAKPARK_068":[{"eNBName":"03600210028_5GDU_CA_SAC_OAKPARK_068","eNBId":"3600210028","siteName":"CA_SAC_OAKPARK_068"},{"eNBName":"03600210029_5GDU_CA_SAC_OAKPARK_068","eNBId":"3600210029","siteName":"CA_SAC_OAKPARK_068"},{"eNBName":"03600210030_5GDU_CA_SAC_OAKPARK_068","eNBId":"3600210030","siteName":"CA_SAC_OAKPARK_068"}],"CA_SAC_SACRAMENTODT_214":[{"eNBName":"03600080076_5GDU_CA_SAC_SACRAMENTODT_214","eNBId":"3600080076","siteName":"CA_SAC_SACRAMENTODT_214"},{"eNBName":"03600080077_5GDU_CA_SAC_SACRAMENTODT_214","eNBId":"3600080077","siteName":"CA_SAC_SACRAMENTODT_214"}],"CA_SAC_PARKWAY_016":[{"eNBName":"03600210040_5GDU_CA_SAC_PARKWAY_016","eNBId":"3600210040","siteName":"CA_SAC_PARKWAY_016"},{"eNBName":"03600210041_5GDU_CA_SAC_PARKWAY_016","eNBId":"3600210041","siteName":"CA_SAC_PARKWAY_016"}],"CA_SAC_PARKWAY_018":[{"eNBName":"03600210094_5GDU_CA_SAC_PARKWAY_018","eNBId":"3600210094","siteName":"CA_SAC_PARKWAY_018"},{"eNBName":"03600210095_5GDU_CA_SAC_PARKWAY_018","eNBId":"3600210095","siteName":"CA_SAC_PARKWAY_018"},{"eNBName":"03600210096_5GDU_CA_SAC_PARKWAY_018","eNBId":"3600210096","siteName":"CA_SAC_PARKWAY_018"}],"CA_SAC_SACRAMENTODT_250":[{"eNBName":"03600010040_5GDU_CA_SAC_SACRAMENTODT_250","eNBId":"3600010040","siteName":"CA_SAC_SACRAMENTODT_250"},{"eNBName":"03600010041_5GDU_CA_SAC_SACRAMENTODT_250","eNBId":"3600010041","siteName":"CA_SAC_SACRAMENTODT_250"}],"CA_SAC_SACRAMENTODT_096":[{"eNBName":"03600080094_5GDU_CA_SAC_SACRAMENTODT_096","eNBId":"3600080094","siteName":"CA_SAC_SACRAMENTODT_096"},{"eNBName":"03600080095_5GDU_CA_SAC_SACRAMENTODT_096","eNBId":"3600080095","siteName":"CA_SAC_SACRAMENTODT_096"},{"eNBName":"03600080096_5GDU_CA_SAC_SACRAMENTODT_096","eNBId":"3600080096","siteName":"CA_SAC_SACRAMENTODT_096"}],"CA_SAC_CENTRALSAC_098":[{"eNBName":"03600080121_5GDU_CA_SAC_CENTRALSAC_098","eNBId":"3600080121","siteName":"CA_SAC_CENTRALSAC_098"},{"eNBName":"03600080122_5GDU_CA_SAC_CENTRALSAC_098","eNBId":"3600080122","siteName":"CA_SAC_CENTRALSAC_098"},{"eNBName":"03600080123_5GDU_CA_SAC_CENTRALSAC_098","eNBId":"3600080123","siteName":"CA_SAC_CENTRALSAC_098"}],"CA_SAC_CENTRALSAC_129":[{"eNBName":"03600360001_5GAU_CA_SAC_CENTRALSAC_129","eNBId":"3600360001","siteName":"CA_SAC_CENTRALSAC_129"},{"eNBName":"03600360002_5GAU_CA_SAC_CENTRALSAC_129","eNBId":"3600360002","siteName":"CA_SAC_CENTRALSAC_129"},{"eNBName":"03600360003_5GAU_CA_SAC_CENTRALSAC_129","eNBId":"3600360003","siteName":"CA_SAC_CENTRALSAC_129"}],"CA_SAC_WOODCREEK_058":[{"eNBName":"03600010133_5GDU_CA_SAC_WOODCREEK_058","eNBId":"3600010133","siteName":"CA_SAC_WOODCREEK_058"},{"eNBName":"03600010134_5GDU_CA_SAC_WOODCREEK_058","eNBId":"3600010134","siteName":"CA_SAC_WOODCREEK_058"},{"eNBName":"03600010135_5GDU_CA_SAC_WOODCREEK_058","eNBId":"3600010135","siteName":"CA_SAC_WOODCREEK_058"}],"SACRAMENTODT_5G091":[{"eNBName":"03600010010_5GAU_SACRAMENTODT_5G091","eNBId":"3600010010","siteName":"SACRAMENTODT_5G091"},{"eNBName":"03600010011_5GAU_SACRAMENTODT_5G091","eNBId":"3600010011","siteName":"SACRAMENTODT_5G091"}],"CA_SAC_LANDPARK_089":[{"eNBName":"03600050046_5GDU_CA_SAC_LANDPARK_089","eNBId":"3600050046","siteName":"CA_SAC_LANDPARK_089"},{"eNBName":"03600050047_5GDU_CA_SAC_LANDPARK_089","eNBId":"3600050047","siteName":"CA_SAC_LANDPARK_089"},{"eNBName":"03600050048_5GDU_CA_SAC_LANDPARK_089","eNBId":"3600050048","siteName":"CA_SAC_LANDPARK_089"}],"CA_SAC_WOODCREEK_017":[{"eNBName":"03600010148_5GDU_CA_SAC_WOODCREEK_017","eNBId":"3600010148","siteName":"CA_SAC_WOODCREEK_017"},{"eNBName":"03600010149_5GDU_CA_SAC_WOODCREEK_017","eNBId":"3600010149","siteName":"CA_SAC_WOODCREEK_017"},{"eNBName":"03600010150_5GDU_CA_SAC_WOODCREEK_017","eNBId":"3600010150","siteName":"CA_SAC_WOODCREEK_017"}],"CA_SAC_PARKWAY_012":[{"eNBName":"03600210043_5GDU_CA_SAC_PARKWAY_012","eNBId":"3600210043","siteName":"CA_SAC_PARKWAY_012"},{"eNBName":"03600210044_5GDU_CA_SAC_PARKWAY_012","eNBId":"3600210044","siteName":"CA_SAC_PARKWAY_012"},{"eNBName":"03600210045_5GDU_CA_SAC_PARKWAY_012","eNBId":"3600210045","siteName":"CA_SAC_PARKWAY_012"}],"CA_SAC_WESTPARK_058":[{"eNBName":"03600010121_5GDU_CA_SAC_WESTPARK_058","eNBId":"3600010121","siteName":"CA_SAC_WESTPARK_058"},{"eNBName":"03600010122_5GDU_CA_SAC_WESTPARK_058","eNBId":"3600010122","siteName":"CA_SAC_WESTPARK_058"},{"eNBName":"03600010123_5GDU_CA_SAC_WESTPARK_058","eNBId":"3600010123","siteName":"CA_SAC_WESTPARK_058"}],"CA_SAC_CENTRALSAC_127":[{"eNBName":"03600080091_5GDU_CA_SAC_CENTRALSAC_127","eNBId":"3600080091","siteName":"CA_SAC_CENTRALSAC_127"},{"eNBName":"03600080092_5GDU_CA_SAC_CENTRALSAC_127","eNBId":"3600080092","siteName":"CA_SAC_CENTRALSAC_127"},{"eNBName":"03600080093_5GDU_CA_SAC_CENTRALSAC_127","eNBId":"3600080093","siteName":"CA_SAC_CENTRALSAC_127"}],"CA_SAC_WOODCREEK_050":[{"eNBName":"03600010154_5GDU_CA_SAC_WOODCREEK_050","eNBId":"3600010154","siteName":"CA_SAC_WOODCREEK_050"},{"eNBName":"03600010155_5GDU_CA_SAC_WOODCREEK_050","eNBId":"3600010155","siteName":"CA_SAC_WOODCREEK_050"},{"eNBName":"03600010156_5GDU_CA_SAC_WOODCREEK_050","eNBId":"3600010156","siteName":"CA_SAC_WOODCREEK_050"}],"CA_SAC_SACRAMENTODT_526":[{"eNBName":"03600080088_5GDU_CA_SAC_SACRAMENTODT_526","eNBId":"3600080088","siteName":"CA_SAC_SACRAMENTODT_526"},{"eNBName":"03600080089_5GDU_CA_SAC_SACRAMENTODT_526","eNBId":"3600080089","siteName":"CA_SAC_SACRAMENTODT_526"},{"eNBName":"03600080090_5GDU_CA_SAC_SACRAMENTODT_526","eNBId":"3600080090","siteName":"CA_SAC_SACRAMENTODT_526"}],"CA_SAC_SACRAMENTODT_208":[{"eNBName":"03600010034_5GDU_CA_SAC_SACRAMENTODT_208","eNBId":"3600010034","siteName":"CA_SAC_SACRAMENTODT_208"},{"eNBName":"03600010035_5GDU_CA_SAC_SACRAMENTODT_208","eNBId":"3600010035","siteName":"CA_SAC_SACRAMENTODT_208"}],"CA_SAC_LANDPARK_009":[{"eNBName":"03600210001_5GAU_CA_SAC_LANDPARK_009","eNBId":"3600210001","siteName":"CA_SAC_LANDPARK_009"},{"eNBName":"03600210002_5GAU_CA_SAC_LANDPARK_009","eNBId":"3600210002","siteName":"CA_SAC_LANDPARK_009"},{"eNBName":"03600210003_5GAU_CA_SAC_LANDPARK_009","eNBId":"3600210003","siteName":"CA_SAC_LANDPARK_009"}],"CA_SAC_WOODCREEK_010":[{"eNBName":"03600010145_5GDU_CA_SAC_WOODCREEK_010","eNBId":"3600010145","siteName":"CA_SAC_WOODCREEK_010"},{"eNBName":"03600010146_5GDU_CA_SAC_WOODCREEK_010","eNBId":"3600010146","siteName":"CA_SAC_WOODCREEK_010"},{"eNBName":"03600010147_5GDU_CA_SAC_WOODCREEK_010","eNBId":"3600010147","siteName":"CA_SAC_WOODCREEK_010"}],"CA_SAC_LANDPARK_090":[{"eNBName":"03600210082_5GDU_CA_SAC_LANDPARK_090","eNBId":"3600210082","siteName":"CA_SAC_LANDPARK_090"},{"eNBName":"03600210083_5GDU_CA_SAC_LANDPARK_090","eNBId":"3600210083","siteName":"CA_SAC_LANDPARK_090"}],"CA_SAC_WOODCREEK_003":[{"eNBName":"03600010142_5GDU_CA_SAC_WOODCREEK_003","eNBId":"3600010142","siteName":"CA_SAC_WOODCREEK_003"},{"eNBName":"03600010143_5GDU_CA_SAC_WOODCREEK_003","eNBId":"3600010143","siteName":"CA_SAC_WOODCREEK_003"},{"eNBName":"03600010144_5GDU_CA_SAC_WOODCREEK_003","eNBId":"3600010144","siteName":"CA_SAC_WOODCREEK_003"}],"CA_SAC_WOODCREEK_049":[{"eNBName":"03600010130_5GDU_CA_SAC_WOODCREEK_049","eNBId":"3600010130","siteName":"CA_SAC_WOODCREEK_049"},{"eNBName":"03600010131_5GDU_CA_SAC_WOODCREEK_049","eNBId":"3600010131","siteName":"CA_SAC_WOODCREEK_049"},{"eNBName":"03600010132_5GDU_CA_SAC_WOODCREEK_049","eNBId":"3600010132","siteName":"CA_SAC_WOODCREEK_049"}],"CA_SAC_SOUTHSAC_026":[{"eNBName":"03600210097_5GDU_CA_SAC_SOUTHSAC_026","eNBId":"3600210097","siteName":"CA_SAC_SOUTHSAC_026"},{"eNBName":"03600210098_5GDU_CA_SAC_SOUTHSAC_026","eNBId":"3600210098","siteName":"CA_SAC_SOUTHSAC_026"},{"eNBName":"03600210099_5GDU_CA_SAC_SOUTHSAC_026","eNBId":"3600210099","siteName":"CA_SAC_SOUTHSAC_026"}],"CA_SAC_PARKWAY_023":[{"eNBName":"03600210037_5GDU_CA_SAC_PARKWAY_023","eNBId":"3600210037","siteName":"CA_SAC_PARKWAY_023"},{"eNBName":"03600210038_5GDU_CA_SAC_PARKWAY_023","eNBId":"3600210038","siteName":"CA_SAC_PARKWAY_023"}],"CA_SAC_TRUXEL_206":[{"eNBName":"03600480034_5GDU_CA_SAC_TRUXEL_206","eNBId":"3600480034","siteName":"CA_SAC_TRUXEL_206"},{"eNBName":"03600480035_5GDU_CA_SAC_TRUXEL_206","eNBId":"3600480035","siteName":"CA_SAC_TRUXEL_206"}],"CA_SAC_WOODCREEK_083":[{"eNBName":"03600010163_5GDU_CA_SAC_WOODCREEK_083","eNBId":"3600010163","siteName":"CA_SAC_WOODCREEK_083"},{"eNBName":"03600010164_5GDU_CA_SAC_WOODCREEK_083","eNBId":"3600010164","siteName":"CA_SAC_WOODCREEK_083"},{"eNBName":"03600010165_5GDU_CA_SAC_WOODCREEK_083","eNBId":"3600010165","siteName":"CA_SAC_WOODCREEK_083"}],"CA_SAC_HAGGINWOOD_136":[{"eNBName":"03600480043_5GDU_CA_SAC_HAGGINWOOD_136","eNBId":"3600480043","siteName":"CA_SAC_HAGGINWOOD_136"},{"eNBName":"03600480044_5GDU_CA_SAC_HAGGINWOOD_136","eNBId":"3600480044","siteName":"CA_SAC_HAGGINWOOD_136"},{"eNBName":"03600480045_5GDU_CA_SAC_HAGGINWOOD_136","eNBId":"3600480045","siteName":"CA_SAC_HAGGINWOOD_136"}],"CA_SAC_SACRAMENTODT_511":[{"eNBName":"03600080043_5GAU_CA_SAC_SACRAMENTODT_511","eNBId":"3600080043","siteName":"CA_SAC_SACRAMENTODT_511"},{"eNBName":"03600080044_5GAU_CA_SAC_SACRAMENTODT_511","eNBId":"3600080044","siteName":"CA_SAC_SACRAMENTODT_511"},{"eNBName":"03600080045_5GAU_CA_SAC_SACRAMENTODT_511","eNBId":"3600080045","siteName":"CA_SAC_SACRAMENTODT_511"}],"CA_SAC_SACRAMENTODT_236":[{"eNBName":"03600080082_5GDU_CA_SAC_SACRAMENTODT_236","eNBId":"3600080082","siteName":"CA_SAC_SACRAMENTODT_236"},{"eNBName":"03600080083_5GDU_CA_SAC_SACRAMENTODT_236","eNBId":"3600080083","siteName":"CA_SAC_SACRAMENTODT_236"},{"eNBName":"03600080084_5GDU_CA_SAC_SACRAMENTODT_236","eNBId":"3600080084","siteName":"CA_SAC_SACRAMENTODT_236"}],"CA_SAC_SACRAMENTODT_237":[{"eNBName":"03600080085_5GDU_CA_SAC_SACRAMENTODT_237","eNBId":"3600080085","siteName":"CA_SAC_SACRAMENTODT_237"},{"eNBName":"03600080086_5GDU_CA_SAC_SACRAMENTODT_237","eNBId":"3600080086","siteName":"CA_SAC_SACRAMENTODT_237"}],"CA_SAC_PARKWAY_038":[{"eNBName":"03600050049_5GDU_CA_SAC_PARKWAY_038","eNBId":"3600050049","siteName":"CA_SAC_PARKWAY_038"},{"eNBName":"03600050050_5GDU_CA_SAC_PARKWAY_038","eNBId":"3600050050","siteName":"CA_SAC_PARKWAY_038"}],"CA_SAC_OAKPARK_163":[{"eNBName":"03600210025_5GDU_CA_SAC_OAKPARK_163","eNBId":"3600210025","siteName":"CA_SAC_OAKPARK_163"},{"eNBName":"03600210026_5GDU_CA_SAC_OAKPARK_163","eNBId":"3600210026","siteName":"CA_SAC_OAKPARK_163"},{"eNBName":"03600210027_5GDU_CA_SAC_OAKPARK_163","eNBId":"3600210027","siteName":"CA_SAC_OAKPARK_163"}],"CA_SAC_HAGGINWOOD_172":[{"eNBName":"03600480040_5GDU_CA_SAC_HAGGINWOOD_172","eNBId":"3600480040","siteName":"CA_SAC_HAGGINWOOD_172"},{"eNBName":"03600480041_5GDU_CA_SAC_HAGGINWOOD_172","eNBId":"3600480041","siteName":"CA_SAC_HAGGINWOOD_172"}],"CA_SAC_LANDPARK_065":[{"eNBName":"03600050037_5GDU_CA_SAC_LANDPARK_065","eNBId":"3600050037","siteName":"CA_SAC_LANDPARK_065"},{"eNBName":"03600050038_5GDU_CA_SAC_LANDPARK_065","eNBId":"3600050038","siteName":"CA_SAC_LANDPARK_065"}],"CA_SAC_WOODCREEK_037":[{"eNBName":"03600010151_5GDU_CA_SAC_WOODCREEK_037","eNBId":"3600010151","siteName":"CA_SAC_WOODCREEK_037"},{"eNBName":"03600010152_5GDU_CA_SAC_WOODCREEK_037","eNBId":"3600010152","siteName":"CA_SAC_WOODCREEK_037"},{"eNBName":"03600010153_5GDU_CA_SAC_WOODCREEK_037","eNBId":"3600010153","siteName":"CA_SAC_WOODCREEK_037"}],"CA_SAC_GOLDEN1_342":[{"eNBName":"03600050004_5GAU_CA_SAC_GOLDEN1_342","eNBId":"3600050004","siteName":"CA_SAC_GOLDEN1_342"},{"eNBName":"03600050005_5GAU_CA_SAC_GOLDEN1_342","eNBId":"3600050005","siteName":"CA_SAC_GOLDEN1_342"}],"CA_SAC_SACRAMENTODT_228":[{"eNBName":"03600080079_5GDU_CA_SAC_SACRAMENTODT_228","eNBId":"3600080079","siteName":"CA_SAC_SACRAMENTODT_228"},{"eNBName":"03600080080_5GDU_CA_SAC_SACRAMENTODT_228","eNBId":"3600080080","siteName":"CA_SAC_SACRAMENTODT_228"},{"eNBName":"03600080081_5GDU_CA_SAC_SACRAMENTODT_228","eNBId":"3600080081","siteName":"CA_SAC_SACRAMENTODT_228"}],"CA_SAC_WESTPARK_110":[{"eNBName":"03600010136_5GDU_CA_SAC_WESTPARK_110","eNBId":"3600010136","siteName":"CA_SAC_WESTPARK_110"},{"eNBName":"03600010137_5GDU_CA_SAC_WESTPARK_110","eNBId":"3600010137","siteName":"CA_SAC_WESTPARK_110"},{"eNBName":"03600010138_5GDU_CA_SAC_WESTPARK_110","eNBId":"3600010138","siteName":"CA_SAC_WESTPARK_110"}],"CA_SAC_WESTPARK_111":[{"eNBName":"03600010139_5GDU_CA_SAC_WESTPARK_111","eNBId":"3600010139","siteName":"CA_SAC_WESTPARK_111"},{"eNBName":"03600010140_5GDU_CA_SAC_WESTPARK_111","eNBId":"3600010140","siteName":"CA_SAC_WESTPARK_111"},{"eNBName":"03600010141_5GDU_CA_SAC_WESTPARK_111","eNBId":"3600010141","siteName":"CA_SAC_WESTPARK_111"}],"CA_SAC_WOODCREEK_077":[{"eNBName":"03600010160_5GDU_CA_SAC_WOODCREEK_077","eNBId":"3600010160","siteName":"CA_SAC_WOODCREEK_077"},{"eNBName":"03600010161_5GDU_CA_SAC_WOODCREEK_077","eNBId":"3600010161","siteName":"CA_SAC_WOODCREEK_077"},{"eNBName":"03600010162_5GDU_CA_SAC_WOODCREEK_077","eNBId":"3600010162","siteName":"CA_SAC_WOODCREEK_077"}],"CA_SAC_POCKET_022":[{"eNBName":"03600050079_5GDU_CA_SAC_POCKET_022","eNBId":"3600050079","siteName":"CA_SAC_POCKET_022"},{"eNBName":"03600050080_5GDU_CA_SAC_POCKET_022","eNBId":"3600050080","siteName":"CA_SAC_POCKET_022"},{"eNBName":"03600050081_5GDU_CA_SAC_POCKET_022","eNBId":"3600050081","siteName":"CA_SAC_POCKET_022"}],"CA_SAC_PARKWAY_049":[{"eNBName":"03600210034_5GDU_CA_SAC_PARKWAY_049","eNBId":"3600210034","siteName":"CA_SAC_PARKWAY_049"},{"eNBName":"03600210035_5GDU_CA_SAC_PARKWAY_049","eNBId":"3600210035","siteName":"CA_SAC_PARKWAY_049"},{"eNBName":"03600210036_5GDU_CA_SAC_PARKWAY_049","eNBId":"3600210036","siteName":"CA_SAC_PARKWAY_049"}],"CA_SAC_LANDPARK_073":[{"eNBName":"03600210064_5GDU_CA_SAC_LANDPARK_073","eNBId":"3600210064","siteName":"CA_SAC_LANDPARK_073"},{"eNBName":"03600210065_5GDU_CA_SAC_LANDPARK_073","eNBId":"3600210065","siteName":"CA_SAC_LANDPARK_073"},{"eNBName":"03600210066_5GDU_CA_SAC_LANDPARK_073","eNBId":"3600210066","siteName":"CA_SAC_LANDPARK_073"}],"CA_SAC_WESTPARK_109":[{"eNBName":"03600010127_5GDU_CA_SAC_WESTPARK_109","eNBId":"3600010127","siteName":"CA_SAC_WESTPARK_109"},{"eNBName":"03600010128_5GDU_CA_SAC_WESTPARK_109","eNBId":"3600010128","siteName":"CA_SAC_WESTPARK_109"},{"eNBName":"03600010129_5GDU_CA_SAC_WESTPARK_109","eNBId":"3600010129","siteName":"CA_SAC_WESTPARK_109"}],"CA_SAC_LANDPARK_036":[{"eNBName":"03600210091_5GDU_CA_SAC_LANDPARK_036","eNBId":"3600210091","siteName":"CA_SAC_LANDPARK_036"},{"eNBName":"03600210092_5GDU_CA_SAC_LANDPARK_036","eNBId":"3600210092","siteName":"CA_SAC_LANDPARK_036"},{"eNBName":"03600210093_5GDU_CA_SAC_LANDPARK_036","eNBId":"3600210093","siteName":"CA_SAC_LANDPARK_036"}],"CA_SAC_WOODCREEK_062":[{"eNBName":"03600010157_5GDU_CA_SAC_WOODCREEK_062","eNBId":"3600010157","siteName":"CA_SAC_WOODCREEK_062"},{"eNBName":"03600010158_5GDU_CA_SAC_WOODCREEK_062","eNBId":"3600010158","siteName":"CA_SAC_WOODCREEK_062"},{"eNBName":"03600010159_5GDU_CA_SAC_WOODCREEK_062","eNBId":"3600010159","siteName":"CA_SAC_WOODCREEK_062"}],"CA_SAC_GOLDEN1_191":[{"eNBName":"03600050001_5GAU_CA_SAC_GOLDEN1_191","eNBId":"3600050001","siteName":"CA_SAC_GOLDEN1_191"},{"eNBName":"03600050002_5GAU_CA_SAC_GOLDEN1_191","eNBId":"3600050002","siteName":"CA_SAC_GOLDEN1_191"},{"eNBName":"03600050003_5GAU_CA_SAC_GOLDEN1_191","eNBId":"3600050003","siteName":"CA_SAC_GOLDEN1_191"}]},"sessionId":"964a8be1","serviceToken":"55861","status":"SUCCESS"}');
                        if(jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User"){
                            this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                        }
                        if(jsonStatue.status == "SUCCESS"){
                            this.getNESiteList = jsonStatue.siteList;
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
                        } else {
                            this.showLoader = false;
                            this.getEndList = [];
                        }
                    }, 100); */
                    //Please Comment while checkIn
                });
            }
        }
        else {
            this.getEndList = [];
            this.selectedEnb = null;
        }
      }

      downLoadFile(key) {
        this.siteDataService.downloadFile(key,this.sharedService.createServiceToken())
        .subscribe(
            data => {
                // let fileName = key.fileName + ".zip";

                let blob = new Blob([data["_body"]], {
                    type: "application/octet-stream"
                });
    
                FileSaver.saveAs(blob, key.fileName);              
    
            },
            error => {
                //Please Comment while checkIn
                /* let jsonStatue: any = {"sessionId":"506db794","reason":"Download Failed","status":"SUCCESS","serviceToken":"63524"};
                    let blob = new Blob([jsonStatue["_body"]], {
                        type: "application/octet-stream"
                    });
    
                    FileSaver.saveAs(blob,key.fileName); 
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

    
    
    
    
      /*
     * on click of edit row create a blueprint and append next to the current row
     * @param : current row event , current row json object and row index
     * @retun : null
     */

    editRow(event, key, index) {
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

        let currentEditedForm = document.querySelector("#editedRow" + index),
            currFormEle = 0,
            currentElement;

        this.formWidth = this.element.nativeElement.querySelector('#tableWrapper').clientWidth - 30 + "px";
        this.scrollLeft = this.element.nativeElement.querySelector('.scrollBody').scrollLeft + "px";

        for (currFormEle = 0; currFormEle < Object.keys(key).length; currFormEle++) {

            currentElement = currentEditedForm.querySelector('#' + Object.keys(key)[currFormEle]);

            // To set darkAircraft
            if (currentElement) {
                let currFormEleValue = "";
                if (key[Object.keys(key)[currFormEle]] == null) {
                    currFormEleValue = "";
                } else {
                    currFormEleValue = key[Object.keys(key)[currFormEle]];
                }
                console.log(currFormEleValue);
                console.log(currentElement.value);

                currentElement.value = currFormEleValue;
            }
        }
    }

    /*
     * on click of cancel edit then close the current edited form
     * @param : index, identifier
     * @retun : null
     */

    cancelEditRow(index, identifier) {
        $(".editRowDisabled").attr("class", "editRow");
        $(".deleteRowDisabled").attr("class","deleteRow");
        let currentEditedForm = document.querySelector(".row_id_" + identifier);

        this.editableFormArray.splice(this.editableFormArray.indexOf(index), 1);

        this.checkFormEnable(index); //TODO : need to recheck this function
        // Enable search button 
        document.querySelector("#searchButton").classList.remove("buttonDisabled");
        this.paginationDisabbled = false;

    }

    /*
     * On click delete button open a modal for confirmation for delete entire row
     * @param : content, userName
     * @retun : null
     */
    deleteRow(event, confirmModal, key) {

        let deleteState = event.target;

        if (deleteState.className != "deleteRowDisabled") {

            this.modalService.open(confirmModal, {keyboard: false,backdrop: 'static',size: 'lg',windowClass: 'confirm-modal'})
            .result.then((result) => {
                this.closeResult = `Closed with: ${result}`;
            }, (reason) => {

                this.showLoader = true;

                this.siteDataService.deleteSiteDeta(key, this.paginationDetails, this.sharedService.createServiceToken())
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
                                        this.message = "Site Data deleted successfully!";
                                        this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
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
              
                              let jsonStatue = { "reason": null, "sessionId": "5f3732a4", "serviceToken": "80356", "status": "SUCCESS" };
              
                              if (jsonStatue.status == "SUCCESS") {
                                  this.message = "Site Data deleted successfully!";
                                  this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                              }
                            }, 1000); */
                            //Please Comment while checkIn
                        });
            });
        }
    }

    /*
     * Used to update airline config details
     * @param : event
     * @retun : null
     */
    updateFileRuleDetails(event,key) {
        setTimeout(() => {
            $("#dataWrapper").find(".scrollBody").scrollLeft(0);
        }, 0);

        validator.performValidation(event, this.validationData, "save_update");
        setTimeout(() => {
            if (this.isValidForm(event)) {
                this.showLoader = true;

                let currentForm = event.target.parentNode.parentNode,
                siteDataDetails = {
                    "id": key.id,
                    "programDetailsEntity": key.programDetailsEntity,
                    "fileName": key.fileName,
                    "filePath": key.filePath,
                    "fileType": key.fileType,
                    "ciqFileName": key.ciqFileName,
                    "neName": key.neName,
                    "remarks": currentForm.querySelector("#remarks").value
                    };

                this.siteDataService.updateFileRuleDetails(siteDataDetails, this.paginationDetails, this.sharedService.createServiceToken())
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
                                        this.message = "Site Data updated successfully!";
                                        this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                                        // Hide all the form/Table/Nodatafound
                                        this.tableShowHide = false;
                                        this.showNoDataFound = false;
                                        this.createNewForm = false;

                                        // Cancel current edit form
                                        this.cancelEditRow(siteDataDetails.id, '');

                                        // Enable add new button 

                                        //document.querySelector(".searchButton").className = document.querySelector(".createbtn").className.replace("buttonDisabled","");

                                    } else {

                                        this.displayModel(jsonStatue.reason, "failureIcon");
                                        // Enable add new button 
                                        //document.querySelector(".createbtn").className = document.querySelector(".createbtn").className.replace("buttonDisabled","");
                                    }
                                }
                            }
                        },
                        error => {
                            //Please Comment while checkIn

                            /* let jsonStatue = { "sessionId": "72e938b8", "reason": null, "status": "SUCCESS", "serviceToken": "81307" };
                            if (jsonStatue.status == "SUCCESS") {
                              this.showLoader = true;
                              setTimeout(() => {
                                this.showLoader = false;
                                this.message = "Site Data updated successfully!";
                                  this.successModalBlock = this.modalService.open(this.successModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'success-modal'});
                                // Cancel current edit form
                                this.cancelEditRow(siteDataDetails.id, '');
                              }, 1000);
                            } else {
                              this.displayModel(jsonStatue.reason, "failureIcon");
                            } */
                            //Please Comment while checkIn
                        });
            }
        }, 0);
    }


    mailFile(event, mailPopUp, key, index) { 
                this.modalService.open(mailPopUp, {keyboard: false,backdrop: 'static',size: 'lg',windowClass: 'confirm-modal mail-modal'})
                .result.then((result) => {
                    this.closeResult = `Closed with: ${result}`;
                }, (reason) => {    
                    this.showLoader = true;    
                    this.siteDataService.mailCiqFile(key, this.emailId, this.sharedService.createServiceToken())
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

    /* On click of search highlight open record types
     * @param : current Tab Item (open/close)
     * @retun : null
     */

    setMenuHighlight(selectedElement) {
        this.searchTabRef.nativeElement.id = (selectedElement == "search") ? "activeTab" : "inactiveTab";
        this.packDataTabRef.nativeElement.id = (selectedElement == "packData") ? "activeTab" : "inactiveTab";
    }


    /*
     * Convert String date and time to Date Object
     * @param : dateString
     * @retun : Date Object
     */

    getDateObject(dateString) {

        if (dateString != null && dateString != "") {

            let dateValue = dateString.split('-');

            return new Date(dateValue[0], (dateValue[1] - 1), dateValue[2].split(' ')[0]);
        }

        return "";
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
                "count": this.pageSize,
                "page": page
            };

            this.paginationDetails = paginationDetails;
            this.pager = this.getPager(this.totalPages * this.pageSize, this.currentPage, this.pageRenge);
            this.getAllSiteData();
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


    /*
     * On session expired close the webpage and logout
     * @param : null
     * @retun : null
     */

    closeAndLogout() {
        //TODO : Need to set this.isLoggedIn global
        this.sessionExpiredModalBlock.close();
        this.sharedService.setUserLoggedIn(false);

        setTimeout(() => {
            sessionStorage.clear();
            this.router.navigate(['/']);
        }, 10);
    }

    closeModel() {
        this.successModalBlock.close();
        this.ngOnInit();
    }


    onChangeTableRowLength(event) {
        this.showLoader = true;
        this.pageSize = event.target.value;

        this.currentPage = 1;

        let paginationDetails = {
            "count": this.pageSize,
            "page": this.currentPage
        };

        this.paginationDetails = paginationDetails;
        setTimeout(() => {
            this.showLoader = false;
            $("#dataWrapper").find(".scrollBody").scrollLeft(0);
            this.getAllSiteData();
        }, 0);

    }

    /*
     * On click sort header in table then sort the data ascending and decending
     * @param : columnName, event and current Index
     * @retun : null
     */

    changeSorting(predicate, event, index) {
        this.sharedService.dynamicSort(predicate, event, index, this.tableData.fileRuleBuilderData);
    }

    onChangeDate(){
        this.errMessage = false;
        if(this.fromDate && this.toDate){
          this.getSiteCiqList(this.fromDate,this.toDate);
          //this.ngOnInit();
        }else{        
          this.errMessage = true;
        }
    }

    onChangesites(event: any) {

        this.dropdownListNE = [];
        /* for (let itm of this.selectedEnb) {

            for (let item of itm.value) {

                let dropdownListNE = item.eNBName;
                this.dropdownListNE.push(dropdownListNE);
            }
            this.selectedItemsNE = this.dropdownListNE;
        } */
        if(this.selectedEnb && this.selectedEnb.value) {
            for (let item of this.selectedEnb.value) {
                let dropdownList = { item_id: item.eNBId, item_text: item.eNBName };
                // let dropdownListNE = item;
                this.dropdownListNE.push(dropdownList);
            }
            this.selectedItemsNE = this.dropdownListNE;
            this.nenamebutton = this.selectedEnb.value.length > 0 ? false : true;
        }

        this.dropdownSettingsNEs = {
            singleSelection: false,
            idField: 'item_id',
            textField: 'item_text',
            itemsShowLimit: 1,
            allowSearchFilter: true,
            searchPlaceholderText: "Search",
            clearSearchFilter: true,
            maxHeight: 300
        };
    }

    getNeList(content) {
        this.siteNEsModalBlock = this.modalService.open(content, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView' });
    }

    closeModelSiteNEs() {
        this.siteNEsModalBlock.close();
    }

    siteCompletionReport(event, siteCompReportModal) {
        if (this.selectedCiq != "" && this.selectedCiq != undefined && this.selectedCiq.ciqFileName) {
            this.validationData.rules.ciqName.required = false;

        } else {
            this.validationData.rules.ciqName.required = true;

        }
        if (this.selectedEnb) {
            this.validationData.rules.nes.required = false;
    
        } else {
            this.validationData.rules.nes.required = true;
        }
        validator.performValidation(event, this.validationData, "save_update");

        setTimeout(() => {
            if (this.isValidForm(event)) {

                // Call the API and get the configuration dropdown data
                this.showLoader = true;
                this.selectedReportCIQ = this.selectedCiq.ciqFileName;
                let enbDetails = [];
                if(this.programName != "VZN-5G-MM") {
                    let tempObj = {
                        "enbName": this.selectedEnb.eNBName,
                        "enbId": this.selectedEnb.eNBId
                    }
                    enbDetails.push(tempObj);
                }
                else {
                    for(let i = 0; i < this.selectedItemsNE.length; i++) {
                        let tempObj = {
                            "enbName": this.selectedItemsNE[i].item_text,
                            "enbId": this.selectedItemsNE[i].item_id
                        }
                        enbDetails.push(tempObj);
                    }
                }

                this.siteDataService.getSiteReportInputDetails(this.sharedService.createServiceToken(), this.selectedCiq.ciqFileName, enbDetails)
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
                                        this.reportFormOptions = JSON.parse(jsonStatue.siteInputs);
                                        this.issuesCategory = Object.keys(this.reportFormOptions.issueCategory);
                                        this.issueAttributeToList = this.reportFormOptions.issueAttributeToList;
                                        this.issueTechnologyList = this.reportFormOptions.issueTechnologyList;
                                        this.issueResolvedList = this.reportFormOptions.issueResolvedList;

                                        this.postAuditIssues = jsonStatue.postAuditIssues ? jsonStatue.postAuditIssues : [];
                                        let isInfoAvailable = true;
                                        for (let index = 0; index < this.postAuditIssues.length; index++) {
                                            if (this.postAuditIssues[index].errorCode.indexOf("_INFO") < 0) {
                                                isInfoAvailable = false; //Exceptional case if _INFO is not in any one of them
                                                break;
                                            }
                                        }
                                        
                                        setTimeout(() => {
                                            this.reportData["siteReportStatus"] = isInfoAvailable ? "Completion" : "Exception";//(this.postAuditIssues && this.postAuditIssues.length > 0) ? "Exception" : "Completion";
                                            this.reportData["project"] = this.programName == 'VZN-5G-MM' ? "SNAP 5G" : this.programName == 'VZN-5G-DSS' ? "LS3" : this.programName == 'VZN-5G-CBAND' ? "LS6" : this.programName == 'VZN-4G-FSU' ? "FSU Bypass" : "";
                                            this.reportData["vendorType"] = "Samsung";

                                            // Some fixed value to be shown
                                            let siteDetailsValues = jsonStatue.siteDetails;
                                            if(siteDetailsValues) {
                                                let keysHavingValues = Object.keys(siteDetailsValues);
                                                for(let key of keysHavingValues) {
                                                    try {
                                                        this.reportData[key] = siteDetailsValues[key];
                                                    } catch (error) {
                                                        
                                                    }
                                                }
                                            }
                                        }, 100);
                                        this.setTimeLineInitialData();
                                        this.setCriticalCheckInitialData();
                                        this.setSiteReportNE();
                                        this.reportData["reportDate"] = new Date();
                                        this.reportData.userName = JSON.parse(sessionStorage.loginDetails).userName;
                                        this.siteCompReportBlock = this.modalService.open(siteCompReportModal, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView reportView' });
                                } else {
                                    this.showLoader = false;
                                    this.displayModel(jsonStatue.reason, "failureIcon");
                                }
                                }
                                else {
                                    this.showLoader = false;
                                    this.displayModel("No Data", "failureIcon");
                                }
                            }
                        },
                        error => {
                            this.showLoader = false;

                            //Please Comment while checkIn

                            /* setTimeout(() => {
            
                                this.showLoader = false;
            
                                // let jsonStatue = {"siteInputs":{"projects":["SNAP - 5G","BAU - 5G"],"swRelease":["20-C-0","21-A-0","21-B-0"],"market":["OPW","WBV","TRI","CGC","CTX","NYM","NE","UNY","SAC","NO","PEN FL","HOU TX"],"integType":["Hot Cut","NSB","FOA Support","Test/Lab support"],"status":["Cancelled 48 Hr.","Cancelled 24 Hr.","Failed ","Rolled Back","Pre Migration Successful","Migration Successful","Partial Complete"],"issueCategory":{"Antenna":["ANT-00 Uncommon Failure","ANT-01 VSWR alarms","ANT-02 Fails RSSI test(s)","ANT-03 Fails VSWR test(s)"],"Cancelled":["CAN-00 Uncommon Failure","CAN-01 No Conquest & XCM","CAN-02 Site Access","CAN-03 Resources (TC,FC,DT)","CAN-05 Permits","CAN-06 Missing Material","CAN-07 VZ 48 Hr. Notice","CAN-08 Missing GPS","CAN-09 Time Constraints","CAN-10 Pre Ex. Alarms","CAN-11 Transport"],"Configuration":["CNF-00 Uncommon Failure","CNF-01 RU FW not loaded or issue","CNF-02 Incorrect Parameter","CNF-03 Incorrect Fiber length","CNF-04 Incorrect MME Pool","CNF-05 FW/SW fail to load /activated","CNF-06 Missing Inter-VM routes on vCU","CNF-07 DHCP connection issue","CNF-08 vCU hosting the AU not instantiated by customer","CNF-09 Static routes not configured on USM"],"Call Testing":["CTE-00 Uncommon Failure","CTE-01 No UE For Call Test","CTE-02 Low DL throughput","CTE-03 Failed to Attach to 5g","CTE-04 Failed to Attach to 4g","CTE-05 Inter-RAT NBR not defined between FSM4 and 5G AU/vCU","CTE-06 Nearby Overpowering Site"],"Design":["DES-00 Uncommon Failure","DES-01 Mismatch between Design and Physical Install","DES-02 X2 Links OOS after X2/ENDC script","DES-03 Incorrect CIQ Fail to create GROW Profiles","DES-04 Incorrect IP Information in CIQ and respective RF scripts","DES-05 Alarms - Pre-existing on Equipment/Network","DES-06 RF Scripts missing or not created","DES-07 Incorrect Parameters  - does not follow the engineering rules","DES-08 Missing Parameters","DES-09 CIQ missing in RF FTP Server","DES-10 Designed Vs. Install (example: designed for GEN2 Vs. physical GEN1 installed)"],"Hardware":["HW-00 Uncommon Failure","HW-01 Site/Sector Down -Pre","HW-03 PowerShift - DC power per requirement at RRH end","HW-04 RETs Misconfigured","HW-05 RETs disabled or lost the connectivity","HW-06 GPS unit Faulty","HW-07 Faulty Radio","HW-08 Bad Router card","HW-09 Bad Fiber/SFP/ CPRI fiber wrong length, dirty, broken","HW-10 Bad RFM (RRH, MMU)","HW-11 Bad CDU-30, LCC, LMD","HW-12 Cancelled the site / has not enough time in MW","HW-13 CPRI Power Readings out of range","HW-14 Door/Cabinet/Fan alarm Issue","HW-15 SFP incorrect/Faulty on CDU side","HW-16 SFP incorrect/Faulty on Tower Top side","HW-17 Non Samsung Approved GPS Cable"],"Installation":["INS-00 Uncommon Failure","INS-01 Installation not complete or Incorrect Installation","INS-02 Unable to commission the node","INS-03 Backhaul not ready","INS-04 Chassis backplane issue. Not able to reseat the card","INS-05 No Show","INS-06 GPS Sync Issue","INS-07 No Power","INS-08 Power issue/ Alarm","INS-09 Missing Backhaul Fiber","INS-10 Missing GPS Cable","INS-11 Missing CPRI Fiber","INS-12 Fibers not Labeled","INS-13 Missing Backhaul SFP","INS-14 Missing CPRI SFP","INS-15 Missing Equipment at Site","INS-16 Access - FT couldn’t load ENV","INS-17 RU is not connected","INS-18 Missing Attenuators","INS-19 Fibers not plugged into Raycap/OVP/Junction box/CPRI Panel","INS-20 Fibers connected to incorrect ports on the Radio head","INS-21 Missing Raycap/OVP/Junction box/CPRI Panel"],"Site Readiness":["SRE-00 Uncommon Failure","SRE-01 Site location unknown","SRE-02 Bad AU","SRE-03 GC has no proper Tools for Troubleshooting","SRE-04 SANE Issue","SRE-05 Rescheduled/Cancelled by PM","SRE-06 Site is removed/relocated by customer","SRE-07 Chrome (web-browser) issue","SRE-08 DUO issue/timed out"],"C&I Delivery tool":["SRT-00 Uncommon Failure","SRT-01 Unable to load CIQ to SRCT","SRT-02 Sectors are grown on different software load","SRT-03 Grow profiles not matching with engineering rules","SRT-04 SRCT not reachable","SRT-05 DBs down","SRT-06 Unable to run pnp grow script","SRT-07 SRCT failed to grow sector(s) on USM","SRT-08 Script too big for SCRT to run","SRT-09 SRCT Connection Manager issue","SRT-10 Leading zero is being removed by SRT tool (4G)"],"Transport":["TPT-00 Uncommon Failure","TPT-01 CSR Router Issue / Customer did not complete Router configuration","TPT-02 Incorrect Gateway IP in CIQ","TPT-03 Bad Backhaul Fiber","TPT-04 No connection to vRAN","TPT-05 No connection to RRH","TPT-06 GPS issue on IXR","TPT-07 No light from the IXR ports","TPT-08 Telecom IP alarm","TPT-09 Router port in CIQ is already in use","TPT-10 4G backhaul loosing remote connectivity when 5G backhaul is connected","TPT-11 Incorrect Router port Information on CIQ","TPT-12 Missing Router card","TPT-13 Cells fail to restore"],"S// EMS":["USM-00 Uncommon Failure","USM-01 slow response","USM-02 eNB alarms are not synced","USM-03 GA software not staged","USM-04 vDU not instantiated","USM-05 Unable to access USM","USM-06 Duplicate gNB in multiple ACPFs","USM-07 Could not lock/unlock Cell","USM-08 vRAN not ready","USM-09 GA software issue"]},"timelineList":["Nokia AU Powered Down / ALU locked down and site unmanaged","Samsung AUs Reachable","Samsung AUs C&I Start","Samsung AUs C&I Finish","Samsung AUs Powered down for pole installation","Samsung AUs pingable post Installation","Handoff to RF for Testing","Basic Call Test Complete (ATP & DT Passed )","Total C&I Effort"]},"sessionId":"40b5f7a","serviceToken":"67720","status":"SUCCESS","reason":""};
                                let jsonStatue = this.networkType == '5G' ? (this.programName == 'VZN-5G-MM' ?
                                    {"siteInputs":"{\"status\":[\"Cancelled 48 Hr.\",\"Cancelled 24 Hr.\",\"Failed \",\"Rolled Back\",\"Pre Migration Successful\",\"Migration Successful\",\"Partial Complete\",\"N/A\"],\"projects\":[\"SNAP 5G\",\"SNAP 4G & 5G\",\"SNAP 4G Hot Cut\",\"SNAP 4G Coverage/Capacity\",\"SNAP DSS/FSU\",\"NSB 4G\",\"NSB 5G\",\"LS3\",\"LS6\",\"BAU\",\"FSU Bypass\",\"N/A\"],\"swRelease\":[\"20-A-0\",\"20-C-0\",\"21-A-0\",\"N/A\"],\"region\":[\"OPW\",\"WBV\",\"TRI\",\"CGC\",\"CTX\",\"NYM\",\"NE\",\"UNY\",\"SAC\",\"NO\",\"PEN FL\",\"HOU TX\",\"N/A\"],\"integType\":[\"Hot Cut\",\"NSB\",\"FOA Support\",\"Test/Lab support\",\"N/A\"],\"vendorType\":[\"TWS\",\"Samsung\"],\"issueCategory\":{\"Antenna\":[\"ANT-00 Uncommon Failure\",\"ANT-01 VSWR/RSSI alarms\",\"ANT-02 Fails RSSI test(s)\",\"ANT-03 Fails VSWR test(s)\",\"ANT-04 Incorrect RFDS\",\"ANT-05 Defective Antenna\",\"ANT-06 BAD Hybrid cable\",\"ANT-07 BAD Diplexer\",\"N/A\"],\"Cancelled\":[\"CAN-00 Uncommon Failure\",\"CAN-01 No Conquest & XCM\",\"CAN-02 Site Access\",\"CAN-03 Resources (TC,FC,DT)\",\"CAN-04 Weather\",\"CAN-05 Permits\",\"CAN-06 Missing Material\",\"CAN-07 VZ 48 Hr. Notice\",\"CAN-08 Missing GPS\",\"CAN-09 Time Constraints\",\"CAN-10 Pre Ex. Alarms\",\"CAN-11 Transport\",\"CAN-12 No C&I Resources (SCH. 48 Hr. but CAN in 24Hr.)\",\"CAN-13 Construction Not Complete\",\"CAN-14 No Health Check from Fast Team\",\"CAN-15 NSB ENV Not Loaded\",\"CAN-16 Cancelled the site / has not enough time in MW\",\"CAN-17 Rescheduled/Cancelled by PM\",\"CAN-18 Root Metrics\",\"CAN-19 No N/A Support Vz\",\"CAN-20 Resources No Show\",\"N/A\"],\"Configuration\":[\"CNF-00 Uncommon Failure\",\"CNF-01 FW/SW fail to load /activated\",\"CNF-02 Missing Inter-VM routes on vCU\",\"CNF-03 DHCP connection issue\",\"CNF-04 vCU hosting the AU not instantiated by customer\",\"CNF-05 Static routes not configured on USM\",\"CNF-06 Swapped Sectors\",\"CNF-07 DES-02 X2 Links OOS after X2/ENDC script\",\"N/A\"],\"RET\":[\"RET -01 RET Motor Jam\",\"RET -02 RET Not Scanned using SBT\",\"RET -03 RET Not Scanned using AISG\",\"RET -04 RET Misconfigured (RFDS Correct) *\",\"RET -05 RETs Disabled or lost the connectivity\",\"N/A\"],\"Design\":[\"DES-00 Uncommon Failure\",\"DES-01 Mismatch between Design and Physical Install\",\"DES-02 Mismatch between Design and NE configuration\",\"DES-03 RF Scripts missing or not created\",\"DES-04 Incorrect Parameters  - does not follow the engineering rules\",\"DES-05 Missing Parameters\",\"DES-06 CIQ missing in RF FTP Server\",\"DES-07 Missing site information in OV\",\"DES-08 Missing RFDS\",\"DES-09 Missing RET form\",\"DES-10 Missing Conquest/XCM/CIQ for the site\",\"DES-11 Incorrect site information in OV\",\"DES-12 Incorrect RFDS\",\"DES-13 Incorrect RET form\",\"DES-14 Incorrect CIQ - Port Mapping\",\"DES-15 Incorrect CIQ - Power Settings\",\"DES-16 Incorrect CIQ - BW settings\",\"DES-17 Incorrect CIQ - MME Pool\",\"DES-18 Incorrect CIQ - Other (Not port mapping, power, BW settings or MME pool)\",\"DES-19 Incorrect IP Information in CIQ\",\"DES-20 RF Script Execution Failure\",\"N/A\"],\"Hardware\":[\"HW-00 Uncommon Failure \",\"HW-01 Critical condition of unknown cause (only issues that have been escalated and are under investigation)\",\"HW-02 PowerShift - Faulty or Misconfigured\",\"HW-03 GPS unit Faulty\",\"HW-04 Faulty Radio/ Antenna (RRH/MMU)\",\"HW-05 RRH ATP Power Test Failed\",\"HW-06 Bad AU\",\"HW-07 Bad Router card\",\"HW-08 Bad / Broken Fiber\",\"HW-09 Bad Chassis\",\"HW-10 CPRI Power Readings out of range\",\"HW-11 Door/Cabinet/Fan alarm Issue\",\"HW-12 SFP incorrect/Faulty on CDU30 or FSU\",\"HW-13 SFP incorrect/Faulty on Tower Top side\",\"HW-14 Non Samsung Approved GPS Cable\",\"HW-15 Bad Y Cable (between the FSU and the DU)\",\"HW-16 - UDA Alarms/issues\",\"HW-17 Bad SFP\",\"HW-18 CPRI fiber wrong length, dirty, broken\",\"HW-19 Dirty fiber\",\"HW-20 Bad LCC\",\"HW-21 Bad LMD\",\"HW-22 Bad FSU\",\"N/A\"],\"Installation\":[\"INS-00 Uncommon Failure\",\"INS-01 Faulty rectifier or insufficient rectifiers installed\",\"INS-02 Missing Backhaul Fiber\",\"INS-03 Missing GPS Cable\",\"INS-04 Missing CPRI Fiber\",\"INS-05 Fibers not Labeled\",\"INS-06 Missing Backhaul SFP\",\"INS-07 Missing CPRI SFP\",\"INS-08 Other Missing Materials\",\"INS-09 Missing Tools or Equipment necessary for installation\",\"INS-10 RU installation not complete\",\"INS-11 Missing Attenuators\",\"INS-12 Fibers not plugged into Raycap/OVP/Junction box/CPRI Panel\",\"INS-13 Fibers connected to incorrect ports on the Radio head\",\"INS-14 Missing Raycap/OVP/Junction box/CPRI Panel\",\"N/A\"],\"Site Readiness\":[\"SRE-00 Uncommon Failure\",\"SRE-01 Site Access Delay\",\"SRE-02 Resource Delay (TC,FC,DT)\",\"SRE-03 Resource Delay- C&I\",\"SRE-04 DUO/SANE Issue affecting SRCT or Engineer login\",\"SRE-05 Change in Planning, project logistics or priorities\",\"SRE-06 Non-C&I Related -Human Error\",\"SRE-07 Weather Delay\",\"SRE-08 Installation Delay\",\"SRE-09 Time Constraints (MW, sunset, etc.)\",\"SRE-10 Pre-existing alarms or conditions\",\"SRE-11 Missing Pre-existing health checks\",\"SRE-12 NSB ENV Not Loaded\",\"SRE-13 vRAN not ready \",\"SRE-14 vDU not instantiated\",\"SRE-15 C&I Related -Human Error\",\"N/A\"],\"C&I Delivery tool\":[\"SRT-00 Uncommon Failure \",\"SRT-01 Unable to load CIQ to SRCT\",\"SRT-02 Sectors are grown on different software load\",\"SRT-03 Bug in automation code\",\"SRT-04 SRCT not reachable\",\"SRT-05 DBs down\",\"SRT-06 Unable to run pnp grow script\",\"SRT-07 SRCT update needed\",\"SRT-08 Script too big for SCRT to run\",\"SRT-09 SRCT generated script execution failure\",\"SRT-10 Delayed/hang in progress during execution\",\"N/A\"],\"Transport\":[\"TPT-00 Uncommon Failure\",\"TPT-01 CSR Router Issue / Customer did not complete Router configuration\",\"TPT-02 No connection to vRAN\",\"TPT-03 Incorrect GTP/PTP configuration \",\"TPT-04 OAM Routing issue\",\"TPT-05 Telecom Routing issue\",\"N/A\"],\"S// EMS\":[\"USM-00 Uncommon Failure\",\"USM-01 slow response\",\"USM-02 eNB alarms are not synced\",\"USM-03 GA software not staged \",\"USM-04 Unable to access USM\",\"USM-05 Duplicate gNB in multiple ACPFs\",\"USM-06 Could not lock/unlock Cell\",\"USM-07 GA software issue\",\"USM-08 USM MAX Capacity\",\"USM-09 SAS Registration\",\"N/A\"],\"N/A\":[\"N/A\"]},\"timelineList\":[\"Nokia AU Powered Down / ALU locked down and site unmanaged\",\"Samsung AUs Reachable\",\"Samsung AUs C&I Start\",\"Samsung AUs C&I Finish\",\"Samsung AUs Powered down for pole installation\",\"Samsung AUs pingable post Installation\",\"Handoff to RF for Testing\",\"Basic Call Test Complete (ATP & DT Passed )\"],\"troubleshootTimelineList\":[\"Troubleshooting Time\",\"Total C&I Effort\"],\"reportStatus\":[\"Completion\",\"Exception\",\"N/A\"],\"typeOfEffort\":[\"1st Touch -1st Attempt \",\"1st Touch - 2nd Attempt \",\"2nd Touch 1st Attempt\",\"2nd Touch 2nd Attempt\",\"Hot Cut - 1st Attempt\",\"Hot Cut - 2nd Attempt\",\"Revisit - TS\",\"Prechecks\",\"Cutover\",\"N/A\"],\"criticalCheckList\":[\"Alarm Free\",\"AU/Cells in state requested per market\",\"TWAMP Ping test report attached\",\"Basic Sanity Test All Pass\",\"RF ATP Started\",\"Follow-Up Required\"],\"issueResolvedList\":[\"Yes\",\"No\",\"N/A\"],\"issueAttributeToList\":[\"Verizon\",\"Samsung\",\"Tool\",\"Process\",\"RF Design\",\"C&I\",\"Deployment\",\"N/A\"]}","sessionId":"704c25e1","neIDs":"7901610076|7901610077","serviceToken":"54619","siteDetails":{"eNodeBName":"","eNodeBSW":"","fsuSW":"","vDUSW":"","softWareRelease":"21.B.0-0201(AU.21B.P1.01)","fuzeProjId":"16280008"},"status":"SUCCESS","reason":""} : 
                                    {"siteInputs":"{\n \"integType\": [\n \"Hot Cut\",\n \"Coverage\",\n \"Capacity\",\n \"NSB\",\n \"Carrier Add\",\n \"FSU Install\",\n \"DSS Cutover\",\n \"Troubleshooting\"\n ],\"vendorType\":[\"TWS\",\"Samsung\"],\n \"issueAttributeToList\": [\n \"Verizon\",\n \"Samsung\",\n \"Tool\",\n \"Process\",\n \"RF Design\",\n \"C&I\",\n \"Deployment\"\n ],\n \"issueCategory\": {\n \"Antenna\": [\n \"ANT-00 Uncommon Failure\",\n \"ANT-01 VSWR/RSSI alarms\",\n \"ANT-02 Fails RSSI test(s)\",\n \"ANT-03 Fails VSWR test(s)\",\n \"ANT-04 RET Motor Jam\",\n \"ANT-05 RET Not Scanned\",\n \"ANT-06 RETs Misconfigured\",\n \"ANT-07 RETs disabled or lost the connectivity\"\n ],\n \"C&I Delivery tool\": [\n \"SRT-00 Uncommon Failure \",\n \"SRT-01 Unable to load CIQ to SRCT\",\n \"SRT-02 Sectors are grown on different software load\",\n \"SRT-03 Bug in automation code\",\n \"SRT-04 SRCT not reachable\",\n \"SRT-05 DBs down\",\n \"SRT-06 Unable to run pnp grow script\",\n \"SRT-07 SRCT update needed\",\n \"SRT-08 Script too big for SCRT to run\",\n \"SRT-09 SRCT generated script execution failure\",\n \"SRT-10 Delayed/hang in progress during execution\"\n ],\n \"Call Testing\": [\n \"CTE-00 Uncommon Failure \",\n \"CTE-01 No UE For Call Test\",\n \"CTE-02 Low DL throughput\",\n \"CTE-03 Failed to Attach to 5G\",\n \"CTE-04 Failed to Attach to 4G\",\n \"CTE-05 Inter-RAT NBR not defined between FSM4 and 5G AU/vCU\",\n \"CTE-06 Nearby Overpowering Site\"\n ],\n \"Cancelled\": [\n \"CAN-00 Uncommon Failure\",\n \"CAN-01 No Conquest & XCM\",\n \"CAN-02 Site Access\",\n \"CAN-03 Resources (TC,FC,DT)\",\n \"CAN-04 Weather\",\n \"CAN-05 Permits\",\n \"CAN-06 Missing Material\",\n \"CAN-07 VZ 48 Hr. Notice\",\n \"CAN-08 Missing GPS\",\n \"CAN-09 Time Constraints\",\n \"CAN-10 Pre Ex. Alarms\",\n \"CAN-11 Transport\",\n \"CAN-12 No C&I Resources (SCH. 48 Hr. but CAN in 24Hr.)\",\n \"CAN-13 Construction Not Complete\",\n \"CAN-14 No Health Check from Fast Team\",\n \"CAN-15 NSB ENV Not Loaded\",\n \"CAN-16 Cancelled the site / has not enough time in MW\",\n \"CAN-17 Rescheduled/Cancelled by PM\",\n \"CAN-18 Root Metrics\",\n \"CAN-19 No N/A Support Vz\"\n ],\n \"Configuration\": [\n \"CNF-00 Uncommon Failure\",\n \"CNF-01 FW/SW fail to load /activated\",\n \"CNF-02 Missing Inter-VM routes on vCU\",\n \"CNF-03 DHCP connection issue\",\n \"CNF-04 vCU hosting the AU not instantiated by customer\",\n \"CNF-05 Static routes not configured on USM\",\n \"CNF-06 Swapped Sectors\",\n \"CNF-07 DES-02 X2 Links OOS after X2/ENDC script\"\n ],\n \"Design\": [\n \"DES-00 Uncommon Failure\",\n \"DES-01 Mismatch between Design and Physical Install\",\n \"DES-02 Mismatch between Design and NE configuration\",\n \"DES-03 RF Scripts missing or not created\",\n \"DES-04 Incorrect Parameters - does not follow the engineering rules\",\n \"DES-05 Missing Parameters\",\n \"DES-06 CIQ missing in RF FTP Server\",\n \"DES-07 Missing site information in OV\",\n \"DES-08 Missing RFDS\",\n \"DES-09 Missing RET form\",\n \"DES-10 Missing Conquest/XCM/CIQ for the site\",\n \"DES-11 Incorrect site information in OV\",\n \"DES-12 Incorrect RFDS\",\n \"DES-13 Incorrect RET form\",\n \"DES-14 Incorrect CIQ - Port Mapping\",\n \"DES-15 Incorrect CIQ - Power Settings\",\n \"DES-16 Incorrect CIQ - BW settings\",\n \"DES-17 Incorrect CIQ - MME Pool\",\n \"DES-18 Incorrect CIQ - Other (Not port mapping, power, BW settings or MME pool)\",\n \"DES-19 Incorrect IP Information in CIQ\",\n \"DES-20 RF Script Execution Failure\"\n ],\n \"Hardware\": [\n \"HW-00 Uncommon Failure \",\n \"HW-01 Critical condition of unknown cause (only issues that have been escalated and are under investigation)\",\n \"HW-02 PowerShift - Faulty or Misconfigured\",\n \"HW-03 GPS unit Faulty\",\n \"HW-04 Faulty Radio/ Antenna (RRH/MMU)\",\n \"HW-05 RRH ATP Power Test Failed\",\n \"HW-06 Bad AU\",\n \"HW-07 Bad Router card\",\n \"HW-08 Bad Fiber/SFP/ CPRI fiber wrong length, dirty, broken\",\n \"HW-09 Bad Chassis, LCC, LMD, FSU\",\n \"HW-10 CPRI Power Readings out of range\",\n \"HW-11 Door/Cabinet/Fan alarm Issue\",\n \"HW-12 SFP incorrect/Faulty on CDU30 or FSU\",\n \"HW-13 SFP incorrect/Faulty on Tower Top side\",\n \"HW-14 Non Samsung Approved GPS Cable\",\n \"HW-15 Bad Y Cable (between the FSU and the DU)\",\n \"HW-16 - UDA Alarms/issues\"\n ],\n \"Installation\": [\n \"INS-00 Uncommon Failure\",\n \"INS-01 Faulty rectifier or insufficient rectifiers installed\",\n \"INS-02 Missing Backhaul Fiber\",\n \"INS-03 Missing GPS Cable\",\n \"INS-04 Missing CPRI Fiber\",\n \"INS-05 Fibers not Labeled\",\n \"INS-06 Missing Backhaul SFP\",\n \"INS-07 Missing CPRI SFP\",\n \"INS-08 Other Missing Materials\",\n \"INS-09 Missing Tools or Equipment necessary for installation\",\n \"INS-10 RU installation not complete\",\n \"INS-11 Missing Attenuators\",\n \"INS-12 Fibers not plugged into Raycap/OVP/Junction box/CPRI Panel\",\n \"INS-13 Fibers connected to incorrect ports on the Radio head\",\n \"INS-14 Missing Raycap/OVP/Junction box/CPRI Panel\"\n ],\n \"S// EMS\": [\n \"USM-00 Uncommon Failure\",\n \"USM-01 slow response\",\n \"USM-02 eNB alarms are not synced\",\n \"USM-03 GA software not staged \",\n \"USM-04 Unable to access USM\",\n \"USM-05 Duplicate gNB in multiple ACPFs\",\n \"USM-06 Could not lock/unlock Cell\",\n \"USM-07 GA software issue\",\n \"USM-08 USM MAX Capacity\",\n \"USM-09 SAS Registration\"\n ],\n \"Site Readiness\": [\n \"SRE-00 Uncommon Failure\",\n \"SRE-01 Site Access Delay\",\n \"SRE-02 Resource Delay (TC,FC,DT)\",\n \"SRE-03 Resource Delay- C&I\",\n \"SRE-04 DUO/SANE Issue affecting SRCT or Engineer login\",\n \"SRE-05 Change in Planning, project logistics or priorities\",\n \"SRE-06 Non-C&I Related -Human Error\",\n \"SRE-07 Weather Delay\",\n \"SRE-08 Installation Delay\",\n \"SRE-09 Time Constraints (MW, sunset, etc.)\",\n \"SRE-10 Pre-existing alarms or conditions\",\n \"SRE-11 Missing Pre-existing health checks\",\n \"SRE-12 NSB ENV Not Loaded\",\n \"SRE-13 vRAN not ready \",\n \"SRE-14 vDU not instantiated\"\n ],\n \"Transport\": [\n \"TPT-00 Uncommon Failure\",\n \"TPT-01 CSR Router Issue / Customer did not complete Router configuration\",\n \"TPT-02 No connection to vRAN\",\n \"TPT-03 Incorrect GTP/PTP configuration \",\n \"TPT-04 OAM Routing issue\",\n \"TPT-05 Telecom Routing issue\"\n ]\n },\n \"issueResolvedList\": [\n \"Yes\",\n \"No\"\n ],\n \"projects\": [\n \"SNAP 5G\",\n \"SNAP 4G & 5G\",\n \"SNAP 4G Hot Cut\",\n \"SNAP 4G Coverage/Capacity\",\n \"SNAP DSS/FSU\",\n \"NSB 4G\",\n \"NSB 5G\",\n \"LS3\",\n \"LS6\",\n \"BAU\",\n \"FSU Bypass\"\n ],\n \"region\": [\n \"OPW\",\n \"WBV\",\n \"TRI\",\n \"CGC\",\n \"CTX\",\n \"NYM\",\n \"NE\",\n \"UNY\",\n \"SAC\",\n \"NO\",\n \"PEN FL\",\n \"HOU TX\"\n ],\n \"reportStatus\": [\n \"Completion\",\n \"Exception\"\n ],\n \"status\": [\n \"Cancelled 48 Hr.\",\n \"Cancelled 24 Hr.\",\n \"Failed \",\n \"Rolled Back\",\n \"Pre Migration Successful\",\n \"Partial Complete\",\n \"Migration Successful\"\n ],\n \"swRelease\": [\n \"20.C.0\",\n \"21.A.0\",\n \"21.B.0\"\n ],\n \"timelineList\": [\n \"C&I Prechecks Start\",\n \"C&I Prechecks Start\",\n \"C&I Integration Start\",\n \"Audits Pass\",\n \"C&I Integration Complete\",\n \"Troubleshooting Time\",\n \"Total C&I Effort\"\n ],\"troubleshootTimelineList\":[\"Troubleshooting Time\",\"Total C&I Effort\"],\n \"typeOfEffort\": [\n \"1st Touch -1st Attempt \",\n \"1st Touch - 2nd Attempt \",\n \"2nd Touch 1st Attempt\",\n \"2nd Touch 2nd Attempt\",\n \"Hot Cut - 1st Attempt\",\n \"Hot Cut - 2nd Attempt\",\n \"Revisit - TS\",\n \"Prechecks\",\n \"Cutover\"\n ],\n \"vDUSW\": [\n \"21A\",\n \"21BP3\",\n \"21BP3.01\",\n \"21BP4\"\n ],\n \"eNodeBSW\": [\n \"21AP3\",\n \"21BP3\"\n ],\n \"fsuSW\": [\n \"21AP2\",\n \"21BP1\"\n ]\n}","sessionId":"a9b31cca","serviceToken":"95483","siteDetails":{"eNodeBName":"NORTHRIDGE","eNodeBSW":"21.B.0-0104(ENB.21B.P3.01)","fsuSW":"21.B.0-0201(FSU.21B.P1.01) ","vDUSW":"21.B.0-0400(VDU.21B.P3.00)"},"postAuditIssues":[],"status":"SUCCESS"}) :
                                    {"siteInputs":"{\"status\":[\"Cancelled 48 Hr.\",\"Cancelled 24 Hr.\",\"Failed \",\"Rolled Back\",\"Pre Migration Successful\",\"Partial Complete\",\"Migration Successful\",\"N/A\"],\"projects\":[\"SNAP 5G\",\"SNAP 4G & 5G\",\"SNAP 4G Hot Cut\",\"SNAP 4G Coverage/Capacity\",\"SNAP DSS/FSU\",\"NSB 4G\",\"NSB 5G\",\"LS3\",\"LS6\",\"BAU\",\"FSU Bypass\",\"N/A\"],\"swRelease\":[\"20.C.0\",\"21.A.0\",\"21.B.0\",\"N/A\"],\"region\":[\"OPW\",\"WBV\",\"TRI\",\"CGC\",\"CTX\",\"NYM\",\"NE\",\"UNY\",\"SAC\",\"NO\",\"PEN FL\",\"HOU TX\",\"N/A\"],\"integType\":[\"Hot Cut\",\"Coverage\",\"Capacity\",\"NSB\",\"Carrier Add\",\"FSU Install\",\"DSS Cutover\",\"Troubleshooting\",\"N/A\"],\"vendorType\":[\"TWS\",\"Samsung\"],\"issueCategory\":{\"N/A\":[\"N/A\"],\"Antenna\":[\"ANT-00 Uncommon Failure\",\"ANT-01 VSWR/RSSI alarms\",\"ANT-02 Fails RSSI test(s)\",\"ANT-03 Fails VSWR test(s)\",\"ANT-04 Incorrect RFDS\",\"ANT-05 Defective Antenna\",\"ANT-06 BAD Hybrid cable\",\"ANT-07 BAD Diplexer\",\"N/A\"],\"Cancelled\":[\"CAN-00 Uncommon Failure\",\"CAN-01 No Conquest & XCM\",\"CAN-02 Site Access\",\"CAN-03 Resources (TC,FC,DT)\",\"CAN-04 Weather\",\"CAN-05 Permits\",\"CAN-06 Missing Material\",\"CAN-07 VZ 48 Hr. Notice\",\"CAN-08 Missing GPS\",\"CAN-09 Time Constraints\",\"CAN-10 Pre Ex. Alarms\",\"CAN-11 Transport\",\"CAN-12 No C&I Resources (SCH. 48 Hr. but CAN in 24Hr.)\",\"CAN-13 Construction Not Complete\",\"CAN-14 No Health Check from Fast Team\",\"CAN-15 NSB ENV Not Loaded\",\"CAN-16 Cancelled the site / has not enough time in MW\",\"CAN-17 Rescheduled/Cancelled by PM\",\"CAN-18 Root Metrics\",\"CAN-19 No N/A Support Vz\",\"CAN-20 Resources No Show\",\"N/A\"],\"Configuration\":[\"CNF-00 Uncommon Failure\",\"CNF-01 FW/SW fail to load /activated\",\"CNF-02 Missing Inter-VM routes on vCU\",\"CNF-03 DHCP connection issue\",\"CNF-04 vCU hosting the AU not instantiated by customer\",\"CNF-05 Static routes not configured on USM\",\"CNF-06 Swapped Sectors\",\"CNF-07 DES-02 X2 Links OOS after X2/ENDC script\",\"N/A\"],\"RET\":[\"RET -01 RET Motor Jam\",\"RET -02 RET Not Scanned using SBT\",\"RET -03 RET Not Scanned using AISG\",\"RET -04 RET Misconfigured (RFDS Correct) *\",\"RET -05 RETs Disabled or lost the connectivity\",\"N/A\"],\"Design\":[\"DES-00 Uncommon Failure\",\"DES-01 Mismatch between Design and Physical Install\",\"DES-02 Mismatch between Design and NE configuration\",\"DES-03 RF Scripts missing or not created\",\"DES-04 Incorrect Parameters  - does not follow the engineering rules\",\"DES-05 Missing Parameters\",\"DES-06 CIQ missing in RF FTP Server\",\"DES-07 Missing site information in OV\",\"DES-08 Missing RFDS\",\"DES-09 Missing RET form\",\"DES-10 Missing Conquest/XCM/CIQ for the site\",\"DES-11 Incorrect site information in OV\",\"DES-12 Incorrect RFDS\",\"DES-13 Incorrect RET form\",\"DES-14 Incorrect CIQ - Port Mapping\",\"DES-15 Incorrect CIQ - Power Settings\",\"DES-16 Incorrect CIQ - BW settings\",\"DES-17 Incorrect CIQ - MME Pool\",\"DES-18 Incorrect CIQ - Other (Not port mapping, power, BW settings or MME pool)\",\"DES-19 Incorrect IP Information in CIQ\",\"DES-20 RF Script Execution Failure\",\"N/A\"],\"Hardware\":[\"HW-00 Uncommon Failure \",\"HW-01 Critical condition of unknown cause (only issues that have been escalated and are under investigation)\",\"HW-02 PowerShift - Faulty or Misconfigured\",\"HW-03 GPS unit Faulty\",\"HW-04 Faulty Radio/ Antenna (RRH/MMU)\",\"HW-05 RRH ATP Power Test Failed\",\"HW-06 Bad AU\",\"HW-07 Bad Router card\",\"HW-08 Bad / Broken Fiber\",\"HW-09 Bad Chassis\",\"HW-10 CPRI Power Readings out of range\",\"HW-11 Door/Cabinet/Fan alarm Issue\",\"HW-12 SFP incorrect/Faulty on CDU30 or FSU\",\"HW-13 SFP incorrect/Faulty on Tower Top side\",\"HW-14 Non Samsung Approved GPS Cable\",\"HW-15 Bad Y Cable (between the FSU and the DU)\",\"HW-16 - UDA Alarms/issues\",\"HW-17 Bad SFP\",\"HW-18 CPRI fiber wrong length, dirty, broken\",\"HW-19 Dirty fiber\",\"HW-20 Bad LCC\",\"HW-21 Bad LMD\",\"HW-22 Bad FSU\",\"N/A\"],\"Installation\":[\"INS-00 Uncommon Failure\",\"INS-01 Faulty rectifier or insufficient rectifiers installed\",\"INS-02 Missing Backhaul Fiber\",\"INS-03 Missing GPS Cable\",\"INS-04 Missing CPRI Fiber\",\"INS-05 Fibers not Labeled\",\"INS-06 Missing Backhaul SFP\",\"INS-07 Missing CPRI SFP\",\"INS-08 Other Missing Materials\",\"INS-09 Missing Tools or Equipment necessary for installation\",\"INS-10 RU installation not complete\",\"INS-11 Missing Attenuators\",\"INS-12 Fibers not plugged into Raycap/OVP/Junction box/CPRI Panel\",\"INS-13 Fibers connected to incorrect ports on the Radio head\",\"INS-14 Missing Raycap/OVP/Junction box/CPRI Panel\",\"N/A\"],\"Site Readiness\":[\"SRE-00 Uncommon Failure\",\"SRE-01 Site Access Delay\",\"SRE-02 Resource Delay (TC,FC,DT)\",\"SRE-03 Resource Delay- C&I\",\"SRE-04 DUO/SANE Issue affecting SRCT or Engineer login\",\"SRE-05 Change in Planning, project logistics or priorities\",\"SRE-06 Non-C&I Related -Human Error\",\"SRE-07 Weather Delay\",\"SRE-08 Installation Delay\",\"SRE-09 Time Constraints (MW, sunset, etc.)\",\"SRE-10 Pre-existing alarms or conditions\",\"SRE-11 Missing Pre-existing health checks\",\"SRE-12 NSB ENV Not Loaded\",\"SRE-13 vRAN not ready \",\"SRE-14 vDU not instantiated\",\"SRE-15 C&I Related -Human Error\",\"N/A\"],\"C&I Delivery tool\":[\"SRT-00 Uncommon Failure \",\"SRT-01 Unable to load CIQ to SRCT\",\"SRT-02 Sectors are grown on different software load\",\"SRT-03 Bug in automation code\",\"SRT-04 SRCT not reachable\",\"SRT-05 DBs down\",\"SRT-06 Unable to run pnp grow script\",\"SRT-07 SRCT update needed\",\"SRT-08 Script too big for SCRT to run\",\"SRT-09 SRCT generated script execution failure\",\"SRT-10 Delayed/hang in progress during execution\",\"N/A\"],\"Transport\":[\"TPT-00 Uncommon Failure\",\"TPT-01 CSR Router Issue / Customer did not complete Router configuration\",\"TPT-02 No connection to vRAN\",\"TPT-03 Incorrect GTP/PTP configuration \",\"TPT-04 OAM Routing issue\",\"TPT-05 Telecom Routing issue\",\"N/A\"],\"S// EMS\":[\"USM-00 Uncommon Failure\",\"USM-01 slow response\",\"USM-02 eNB alarms are not synced\",\"USM-03 GA software not staged \",\"USM-04 Unable to access USM\",\"USM-05 Duplicate gNB in multiple ACPFs\",\"USM-06 Could not lock/unlock Cell\",\"USM-07 GA software issue\",\"USM-08 USM MAX Capacity\",\"USM-09 SAS Registration\",\"N/A\"]},\"issueAttributeToList\":[\"Verizon\",\"Samsung\",\"Tool\",\"Process\",\"RF Design\",\"C&I\",\"Deployment\",\"N/A\"],\"issueResolvedList\":[\"Yes\",\"No\",\"N/A\"],\"timelineList\":[\"Nokia ENB unmanaged\",\"Samsung CDU-30 pingable\",\"Samsung CDU-30 C&I Start\",\"Samsung CDU-30C&I End\",\"Waiting on tower crew to complete work on radios\",\"Detected all the radios,fw upgrade, OPS ATP\",\"Unlocked sectors\",\"Basic Call Test Complete\"],\"troubleshootTimelineList\":[\"Troubleshooting Time\",\"Total C&I Effort\"],\"criticalCheckList\":[{\"title\":\"Alarm Free\",\"options\":[\"Yes\",\"No\"],\"mandatory\":[\"No\"],\"desc\":\"State what the alarm is\"},{\"title\":\"Cell Admin State is per CIQ\",\"options\":[\"Yes\",\"No\"],\"mandatory\":[\"No\"],\"desc\":\"State why\"},{\"title\":\"RSSI Alarm Checked\",\"options\":[\"Yes - no alarms\",\"No - RSSI alarms are present\"],\"mandatory\":[\"No - RSSI alarms are present\"],\"desc\":\"State what radio and alarm\"},{\"title\":\"RSSI Imbalance Checked\",\"options\":[\"Yes - no RSSI imbalance\",\"No - RSSI imbalance detected\"],\"mandatory\":[\"No - RSSI imbalance detected\"],\"desc\":\"State what radio and what path(s) / details\"},{\"title\":\"VSWR Checked\",\"options\":[\"Yes - all VSWR passing\",\"No - VSWR not passing\"],\"mandatory\":[\"No - VSWR not passing\"],\"desc\":\"State what radio and path\"},{\"title\":\"Fiber Checked\",\"options\":[\"Yes - all readings passing\",\"No - non passing reading detected\"],\"mandatory\":[\"No - non passing reading detected\"],\"desc\":\"State what radio and path\"},{\"title\":\"SFP Checked\",\"options\":[\"Yes -all  SFPs are recommended types\",\"No - non recommended SFP detected\"],\"mandatory\":[\"No - non recommended SFP detected\"],\"desc\":\"State what radio or ECP location\"},{\"title\":\"ATP All Pass\",\"options\":[\"Yes\",\"No\"],\"mandatory\":[\"No\"],\"desc\":\"State details why ATP did not pass\"},{\"title\":\"All RETs Scanned\",\"options\":[\"Yes\",\"No\"],\"mandatory\":[\"Yes\",\"No\"],\"desc\":\"State how many RETs are detected\"},{\"title\":\"FSU Bypass\",\"options\":[\"Yes - bypass complete\",\"No - bypass not completed\"],\"mandatory\":[\"No - bypass not completed\"],\"desc\":\"State reason why bypass was not completed\"},{\"title\":\"All RETs Labeled(Accounting for Every Carrier)\",\"options\":[\"Yes - all RETS labeled\",\"No - not all RETs labaled\"],\"mandatory\":[\"No - not all RETs labaled\"],\"desc\":\"State reason why labeling was not completed\"},{\"title\":\"Pre Migration Health Check File Reviewed\",\"options\":[\"Yes - reviews\",\"No - not reviews\"],\"mandatory\":[\"No - not reviews\"],\"desc\":\"State reason why pre check was not reviewed\"},{\"title\":\"Any Pre Existing Alarms\",\"options\":[\"Yes - pre-existing alarms were seen\",\"No - no pre-existing alarms\"],\"mandatory\":[\"Yes - pre-existing alarms were seen\"],\"desc\":\"State what is the pre-existing alarm seen\"},{\"title\":\"RET form Received\",\"options\":[\"Yes\",\"No\"],\"mandatory\":[\"No\"],\"desc\":\"State reason why RET form was not received\"},{\"title\":\"Confirm if AISG OnSite\",\"options\":[\"Yes - AISG onsite\",\"No - SBT onsite\"],\"mandatory\":[],\"desc\":\"More details on AISG / SBT configuration\"},{\"title\":\"Follow-Up Required\",\"options\":[\"Yes\",\"No\"],\"mandatory\":[\"Yes\"],\"desc\":\"State why follow up is required\"}],\"reportStatus\":[\"Completion\",\"Exception\",\"N/A\"],\"typeOfEffort\":[\"1st Touch -1st Attempt \",\"1st Touch - 2nd Attempt \",\"2nd Touch 1st Attempt\",\"2nd Touch 2nd Attempt\",\"Hot Cut - 1st Attempt\",\"Hot Cut - 2nd Attempt\",\"Revisit - TS\",\"Prechecks\",\"Cutover\",\"N/A\"]}","sessionId":"6ab48c82","serviceToken":"93994","siteDetails":{"eNodeBName":"","eNodeBSW":"","fsuSW":"","vDUSW":"","softWareRelease":"21.D.0-0300(ENB.21D.P2.00)","fuzeProjId":"259785"},"postAuditIssues":[],"status":"SUCCESS"};

                                if (jsonStatue.status == "SUCCESS") {
                                    this.showLoader = false;
                                    this.reportFormOptions = JSON.parse(jsonStatue.siteInputs);
                                    this.issuesCategory = Object.keys(this.reportFormOptions.issueCategory);
                                    this.issueAttributeToList = this.reportFormOptions.issueAttributeToList;
                                    this.issueTechnologyList = this.reportFormOptions.issueTechnologyList;
                                    this.issueResolvedList = this.reportFormOptions.issueResolvedList;

                                    this.postAuditIssues = jsonStatue.postAuditIssues ? jsonStatue.postAuditIssues : [];
                                    let isInfoAvailable = true;
                                    for (let index = 0; index < this.postAuditIssues.length; index++) {
                                        if (this.postAuditIssues[index].errorCode.indexOf("_INFO") < 0) {
                                            isInfoAvailable = false; //Exceptional case if _INFO is not in any one of them
                                            break;
                                        }
                                    }
                                    this.reportData["reportDate"] = new Date();
                                    this.reportData.userName = JSON.parse(sessionStorage.loginDetails).userName;
                                    setTimeout(() => {
                                        this.reportData["siteReportStatus"] = isInfoAvailable ? "Completion" : "Exception";//(this.postAuditIssues && this.postAuditIssues.length > 0) ? "Exception" : "Completion";
                                        this.reportData["project"] = this.programName == 'VZN-5G-MM' ? "SNAP 5G" : this.programName == 'VZN-5G-DSS' ? "LS3" : this.programName == 'VZN-5G-CBAND' ? "LS6" : this.programName == 'VZN-4G-FSU' ? "FSU Bypass" : "";
                                        this.reportData["vendorType"] = "Samsung";

                                        // Some fixed value to be shown
                                        let siteDetailsValues = jsonStatue.siteDetails;
                                        if(siteDetailsValues) {
                                            let keysHavingValues = Object.keys(siteDetailsValues);
                                            for(let key of keysHavingValues) {
                                                try {
                                                    this.reportData[key] = siteDetailsValues[key];
                                                } catch (error) {
                                                    
                                                }
                                            }
                                        }
                                    }, 100);
                                    this.setTimeLineInitialData();
                                    this.setCriticalCheckInitialData();
                                    this.setSiteReportNE();
                                    this.siteCompReportBlock = this.modalService.open(siteCompReportModal, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView reportView' });
                                } else {
                                    this.showLoader = false;
                                    this.displayModel(jsonStatue.reason, "failureIcon");
                                }
            
                            }, 100); */

                            //Please Comment while checkIn   
                        });
            }
        }, 100);
        
    }

    closeModelSiteCompReport() {
        this.issuesCategory = null;
        this.siteReportNE = null;
        this.selectedTableRow = null;
        this.selectedReportCIQ = null;
        this.reportFormOptions = null;
        // this.isCancellationReport = false;
        this.reportData = {"neName":"","neId":"","reportDate":"","siteReportStatus":"","eNodeBName":"","eNodeBSW":"","fsuSW":"","vDUSW":"","project":"","softWareRelease":"","market":"","fuzeProjId":"","resAuditIssueCheck":false,"integrationType":"","vendorType":"","mmCommComp":"","mmOpsATP":"","dssCommComp":"","dssOpsATP":"","fsuIntegBypass":"","fsuIntegMultiplex":"","finalIntegStatus":"","currCBANDIntegStatus":"","finalCBANDIntegStatus":"","typeOfEffort":"","remarks":"","timeLineDetails":"","troubleshootTimelineDetails":"","categoryDetails":"","lteCommComp":"","lteCBRSCommComp":"","lteLAACommComp":"","lteOpsATP":"","lteCBRSOpsAtp":"","lteLAAOpsATP":"","tcReleased":"","ovTicketNum":""};
        this.carriersFlag = {"700":{"likeforlikeCheckBox":false,"incrementalCheckBox":false},"850":{"likeforlikeCheckBox":false,"incrementalCheckBox":false},"AWS":{"likeforlikeCheckBox":false,"incrementalCheckBox":false},"PCS":{"likeforlikeCheckBox":false,"incrementalCheckBox":false},"AWS3":{"likeforlikeCheckBox":false,"incrementalCheckBox":false},"CBRS":{"likeforlikeCheckBox":false,"incrementalCheckBox":false},"LAA":{"likeforlikeCheckBox":false,"incrementalCheckBox":false}};
        this.resetReportTimelineIssueForm();
        this.siteCompReportBlock.close();
    }

    resetReportTimelineIssueForm() {
        this.currIssueEditScript = null;
        this.currTimelineEditScript = null;
        this.timelineIssuesList = {
            "timelines" : [
                {
                    "timelineId": null,                       
                    "timeLine": "",
                    "siteDate": "",
                    "siteTime": "",
                    "remarks":"",
                    "inTimelineEditMode": true

                }
            ],
            "issues" : [
                {
                    "issueId": null,                       
                    "category": "",
                    "issue": "",
                    "technology": "",
                    "attribute":"",
                    "resolved":"",
                    "remarks":"",
                    "inIssueEditMode": true

                }
            ]
        }
    }

    setTimeLineInitialData() {
        this.timelineList = [];
        this.troubleshootTimelineList = [];
        if(this.reportFormOptions) {
            for(let timelineIndex = 0; timelineIndex < this.reportFormOptions['timelineList'].length; timelineIndex++) {
                let tempObj = {
                    "timeLine": this.reportFormOptions['timelineList'][timelineIndex],
                    "siteDate": new Date(),//this.datePipe.transform(new Date(),"MM-dd-yyyy"),
                    "siteTime": "",
                    "siteTimeNA": false,
                    "remarks":"",
                };
                this.timelineList.push(tempObj);
            }
            for(let index = 0; index < this.reportFormOptions['troubleshootTimelineList'].length; index++) {
                let tempObj = {
                    "timeLine": this.reportFormOptions['troubleshootTimelineList'][index],
                    "siteDate": new Date(),//this.datePipe.transform(new Date(),"MM-dd-yyyy"),
                    "siteTime": "",
                    "siteTimeHr": "0",
                    "siteTimeMin": "0",
                    "remarks":"",
                };
                this.troubleshootTimelineList.push(tempObj);
            }
        }
    }

    setCriticalCheckInitialData() {
        this.criticalCheckInfoList = [];
        if(this.reportFormOptions) {
            for(let criticalCheckIndex = 0; criticalCheckIndex < this.reportFormOptions['criticalCheckList'].length; criticalCheckIndex++) {
                let tempObj = {
                    "title": this.reportFormOptions['criticalCheckList'][criticalCheckIndex]["title"],
                    "options": this.reportFormOptions['criticalCheckList'][criticalCheckIndex]["options"],
                    "mandatory": this.reportFormOptions['criticalCheckList'][criticalCheckIndex]["mandatory"],
                    "checkPerformed": this.reportFormOptions['criticalCheckList'][criticalCheckIndex]["options"][1],
                    "desc": this.reportFormOptions['criticalCheckList'][criticalCheckIndex]["desc"],
                    "remarks":"",
                };
                this.criticalCheckInfoList.push(tempObj);
            }
        }
    }

    addTimelineRow() {
    /*No need to declare as object while creating. While saving object is saved.*/
        let tempObj = {
            "timelineId": null,
            "timeLine": "",
            "siteDate": "",
            "siteTime": "",
            "remarks":"",
            "inTimelineEditMode": true
        };
        this.timelineIssuesList.timelines.unshift(tempObj);
    }

    addIssueRow() {
        let tempObj = {
            "issueId": null,
            "category": "",
            "issue": "",
            "technology":"",
            "attribute":"",
            "resolved":"",
            "remarks":"",
            "inIssueEditMode": true
        };
        this.timelineIssuesList.issues.unshift(tempObj);
    }

    /* siteDateChange(event, index) {
        this.timelineList[index] = this.datePipe.transform(this.timelineList[index],"dd/MM/yyyy");
    } */

    downloadReport(event) {
        this.reportData.resAuditIssueCheck = this.reportData.resAuditIssueCheck ? "Yes" : "No";
        this.reportData.isCancellationReport = this.reportData.isCancellationReport ? "Yes" : "No";
        // this.reportData["neName"] = this.siteReportNE.title;//this.programName == 'VZN-5G-MM' ? (this.selectedEnb ? this.selectedEnb.key : '') : this.selectedEnb.eNBName;
        // this.reportData["neId"] = this.siteReportNE.ids;//this.programName == 'VZN-5G-MM' ? this.joinObjArray(this.selectedItemsNE, 'item_id') : this.selectedEnb.eNBId;
        this.reportData["postAuditIssues"] = this.postAuditIssues;

        let timelineData = [];
        for(let index = 0; index < this.timelineList.length; index ++) {
            let tempObj = {
                "timeLine": this.timelineList[index].timeLine,
                "siteDate": this.datePipe.transform(this.timelineList[index].siteDate,"MM-dd-yyyy"),
                "siteTime": this.reportData.isCancellationReport == "Yes" ? "" : (this.timelineList[index].siteTimeNA ? "NA" : this.datePipe.transform(this.timelineList[index].siteTime, "HH:mm")),
                "remarks": this.timelineList[index].remarks,
            }
            timelineData.push(tempObj);
        }

        let troubleshootTimelineList = [];
        for(let index = 0; index < this.troubleshootTimelineList.length; index ++) {
            let tempObj = {
                "timeLine": this.troubleshootTimelineList[index].timeLine,
                "siteDate": this.datePipe.transform(this.troubleshootTimelineList[index].siteDate,"MM-dd-yyyy"),
                "siteTime": this.reportData.isCancellationReport == "Yes"  ? "" : this.sharedService.addLeadeingZero(this.troubleshootTimelineList[index].siteTimeHr) + ":" + this.sharedService.addLeadeingZero(this.troubleshootTimelineList[index].siteTimeMin),
                "siteTimeHr": this.sharedService.addLeadeingZero(this.troubleshootTimelineList[index].siteTimeHr)+"",
                "siteTimeMin": this.sharedService.addLeadeingZero(this.troubleshootTimelineList[index].siteTimeMin)+"",
                "remarks": this.troubleshootTimelineList[index].remarks,
            }
            troubleshootTimelineList.push(tempObj);
        }

        if(this.reportData.isCancellationReport == "Yes") {
            for(let index = 0; index < this.criticalCheckInfoList.length; index++ ) {
                this.criticalCheckInfoList[index].checkPerformed = "";
            }
        }

        this.reportData["timeLineDetails"] = timelineData;//this.timelineIssuesList.timelines;
        this.reportData["troubleshootTimelineDetails"] = troubleshootTimelineList;
        this.reportData["checkDetails"] = this.criticalCheckInfoList;
        this.reportData["categoryDetails"] = this.getAddedIssues(); //this.timelineIssuesList.issues;
        
        let carrierFlags = Object.keys(this.carriersFlag);
        let carriers = [];
        if(this.networkType == '4G') {
            for(let index = 0; index < carrierFlags.length; index ++) {
                let tempObj = {
                    "likeforlike": carrierFlags[index],
                    "incremental": carrierFlags[index],
                    "likeforlikeCheckBox":this.carriersFlag[carrierFlags[index]]['likeforlikeCheckBox'] ? "Yes" : "No",
                    "incrementalCheckBox": this.carriersFlag[carrierFlags[index]]['incrementalCheckBox'] ? "Yes" : "No"
                };
                carriers.push(tempObj);
            }
        }
        this.reportData['carriers'] = carriers;
        // console.log("ReportFormData : ", JSON.stringify(this.reportData));

        this.siteDataService.exportSiteReportDetails(this.reportData, this.sharedService.createServiceToken(), this.selectedReportCIQ, this.selectedTableRow )
            .subscribe(
                data => {
                    /* let blob = new Blob([data["_body"]], {
                        type: "application/octet-stream"
                    });
        
                    let newFileName = "SiteCompletionReport.xlsx";
        
                    FileSaver.saveAs(blob,newFileName); */

                    let blob = new Blob([data["_body"]], {
                        type: "application/octet-stream"
                    });

                    let timestamp = this.sharedService.getCurrentTimestamp();
                    let filename = this.reportData ? this.reportData.project + "-" + this.reportData.market + "-" + this.reportData.neName + "-" + this.reportData.finalIntegStatus : "";
                    // let tempReportStatus = this.reportData ? this.reportData.siteReportStatus : "";
                    // let reportStatus = this.sharedService.camelCase(tempReportStatus);
                    // let techName = this.getTechnologyName();
                    // let newFileName = "Site" + reportStatus + "Report_" + this.siteReportNE.title + "_" + timestamp + "-" + techName + ".xlsx";
                    let newFileName =  filename + "_" + timestamp + ".xlsx";

                    FileSaver.saveAs(blob,newFileName);
                    
                    this.closeModelSiteCompReport();
                    this.getAllSiteData();
        
                },
                error => {
                    //Please Comment while checkIn
                    /* let jsonStatue: any = {"sessionId":"506db794","reason":"Download Failed","status":"SUCCESS","serviceToken":"63524"};
                    let blob = new Blob([jsonStatue["_body"]], {
                        type: "application/octet-stream"
                    });
                    // (Project) + (Region) + (Ne Name) + (Final Integration Status) + Timestamp.xls
                    let timestamp = this.sharedService.getCurrentTimestamp();
                    let filename = this.reportData ? this.reportData.project + "-" + this.reportData.market + "-" + this.reportData.neName + "-" + this.reportData.finalIntegStatus : "";
                    // let tempReportStatus = this.reportData ? this.reportData.siteReportStatus : "";
                    // let reportStatus = this.sharedService.camelCase(tempReportStatus);
                    // let techName = this.getTechnologyName();
                    // let newFileName = "Site" + reportStatus + "Report_" + this.siteReportNE.title + "_" + timestamp + "-" + techName + ".xlsx";
                    let newFileName =  filename + "_" + timestamp + ".xlsx";
        
                    FileSaver.saveAs(blob,newFileName); 
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


    editSectionRow(event, key, index, sectionName) {
        let errorMsg = "edit is in progress. Please save or cancel the current editing row.";
        if (sectionName == 'issues') {
            if (!this.checkRowInEditMode(sectionName)) {
                //Save old data to use when canceled
                this.currIssueEditScript = JSON.parse(JSON.stringify(this.timelineIssuesList.issues[index]));  //Making deep copy              
                key.inIssueEditMode = true;
            }
            else {
                this.displayModel("Issue " + errorMsg, "failureIcon");
            }
        } else if (sectionName == 'timeline') {
            if (!this.checkRowInEditMode(sectionName)) {
                //Save old data to use when canceled
                this.currTimelineEditScript = JSON.parse(JSON.stringify(this.timelineIssuesList.timelines[index]));  //Making deep copy              
                key.inTimelineEditMode = true;
            }
            else {
                this.displayModel("Timeline " + errorMsg, "failureIcon");
            }
        }
    }

    checkRowInEditMode(sectionName) {
        let isInEditMode = false;
        if (sectionName == 'issues') {           
            for (let i = 0; i < this.timelineIssuesList.issues.length; i++) {
                if (this.timelineIssuesList.issues[i].inIssueEditMode == true) {
                    isInEditMode = true;
                    break;
                }
            }
        } else if (sectionName == 'timeline') {           
            for (let i = 0; i < this.timelineIssuesList.timelines.length; i++) {
                if (this.timelineIssuesList.timelines[i].inTimelineEditMode == true) {
                    isInEditMode = true;
                    break;
                }
            }
        }
        return isInEditMode;
    }
    saveIssueRow(event, key, index1) {        
        let validations = {
            "rules": {},
            "messages": {}
        };

        validations.rules["issueCategory_" + [index1]] = { "required": true };
        validations.rules["issue_" + [index1]] = { "required": true };
        validations.rules["issueTech_" + [index1]] = { "required": true };
        validations.rules["issueAttribute_" + [index1]] = { "required": true };
        validations.rules["issueResolved_" + [index1]] = { "required": true };
        
        validations.messages["issueCategory_" + [index1]] = { "required": "Category is required" };
        validations.messages["issue_" + [index1]] = { "required": "Issue is required" };
        validations.messages["issueTech_" + [index1]] = { "required": "Technology is required" };
        validations.messages["issueAttribute_" + [index1]] = { "required": "Attribute is required" };
        validations.messages["issueResolved_" + [index1]] = { "required": "Resolved is required"};

        validator.performValidation(event, validations, "save_update");
        setTimeout(() => {
            if (this.isValidScriptRowForm(event)) {              
                key.inIssueEditMode = false;
                this.currIssueEditScript = null;
            }
        }, 1000);
    }
    isValidScriptRowForm(event) {
        return ($(event.target).parents("form#scriptRowForm").find(".error-border").length == 0) ? true : false;
    }
    saveTimelineRow(event, key, index1) {        
        // validator.performValidation(event, validations, "save_update");
        setTimeout(() => {
            // if (this.isValidScriptRowForm(event)) {              
                key.inTimelineEditMode = false;
                this.currTimelineEditScript = null;
            // }
        }, 1000);
    }
    deleteSectionRow(confirmModal, key, index, sectionName) {
         this.modalService.open(confirmModal, {keyboard: false,backdrop: 'static',size: 'lg',windowClass: 'confirm-modal'}).result.then((result) => {
             this.closeResult = `Closed with: ${result}`;
         }, (reason) => {
            if (sectionName == 'issues') {
                 this.timelineIssuesList.issues.splice(index, 1);
                 // If there is no row added to issues list, add an empty row
                 if (this.timelineIssuesList.issues.length == 0) {
                     this.addIssueRow();
                 }
            } else if (sectionName == 'timeline') {
                this.timelineIssuesList.timelines.splice(index, 1);
                // If there is no row added to timeline list, add an empty row
                if (this.timelineIssuesList.timelines.length == 0) {
                    this.addTimelineRow();
                }
           }
         });
        
     }
    cancelIssueRow(key, index) { 
        key.inIssueEditMode = false;
        if (this.currIssueEditScript) {            
            this.timelineIssuesList.issues[index] = this.currIssueEditScript;;
            this.currIssueEditScript = null;           
        }
        else {             
            this.timelineIssuesList.issues.splice(index,1);            
            // If there is no row added to issues list, add an empty row
            if (this.timelineIssuesList.issues.length == 0) {
               this.addIssueRow();
            } 
        }
    }
    cancelTimelineRow(key, index) { 
        key.inTimelineEditMode = false;
        if (this.currTimelineEditScript) {            
            this.timelineIssuesList.timelines[index] = this.currTimelineEditScript;;
            this.currTimelineEditScript = null;           
        }
        else {             
            this.timelineIssuesList.timelines.splice(index,1);            
            // If there is no row added to issues list, add an empty row
            if (this.timelineIssuesList.timelines.length == 0) {
               this.addTimelineRow();
            } 
        }
    }

    isIssueListEditing() {
        let retClass = "";
        for (let i = 0; i < this.timelineIssuesList.issues.length; i++) {                
            if (this.timelineIssuesList.issues[i].inIssueEditMode) {                      
                retClass = "buttonDisabled";
                break;
            }           
        }            
        return retClass;
    }

    getAddedIssues() {
        let issueList = [];
        for (let i = 0; i < this.timelineIssuesList.issues.length; i++) {                
            if (this.timelineIssuesList.issues[i].inIssueEditMode) {                      
                continue;
            }
            issueList.push(this.timelineIssuesList.issues[i]);
        }
        return issueList;
    }

    joinObjArray(objArray, attr) {
        var joinedArray = [];
        for (var i = 0; i < objArray.length; i++) {
            joinedArray.push(objArray[i][attr]);
        }
        return joinedArray.join("|");
    }

    viewSiteReport(siteCompReportModal, key) {

        setTimeout(() => {
            this.showLoader = true;
            let enbDetails = [];
            this.siteReportNE = {
                "title": this.programName != "VZN-5G-MM" ? key.neName : key.siteName,
                "ids": key.neId
            };

            this.selectedTableRow = key.id;
            this.selectedReportCIQ = key.ciqFileName;

            if(this.programName != "VZN-5G-MM") {
                let tempObj = {
                    "enbName": key.neName,
                    "enbId": key.neId
                }
                enbDetails.push(tempObj);
            }
            else {
                let neIds = key.neId ? key.neId.split("|") : [];
                for(let i = 0; i < neIds.length; i++) {
                    let tempObj = {
                        "enbName": key.neName,
                        "enbId": neIds[i]
                    }
                    enbDetails.push(tempObj);
                }
            }

            this.siteDataService.viewSiteReportDetailsById(this.sharedService.createServiceToken(), key.id, key.ciqFileName, enbDetails)
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
                                    this.reportData = jsonStatue.reportDetails;
                                    
                                    for(let index = 0; index < this.reportData.carriers.length; index++) {
                                        this.carriersFlag[this.reportData.carriers[index].likeforlike]["likeforlikeCheckBox"] = this.reportData.carriers[index].likeforlikeCheckBox == "Yes" ? true : false;
                                        this.carriersFlag[this.reportData.carriers[index].incremental]["incrementalCheckBox"] = this.reportData.carriers[index].incrementalCheckBox == "Yes" ? true : false;
                                    }

                                    // this.isCancellationReport = this.reportData.isCancellationReport == "Yes" ? true : false;
                                    this.reportData.resAuditIssueCheck = this.reportData.resAuditIssueCheck == "Yes" ? true : false;
                                    this.reportData.isCancellationReport = this.reportData.isCancellationReport == "Yes" ? true : false;
                                    setTimeout(() => {
                                        if (this.reportData.isCancellationReport) {
                                            this.reportFormOptions = JSON.parse(jsonStatue.siteInputs);
                                            this.setCriticalCheckInitialData();
                                        }
                                        else {
                                            this.criticalCheckInfoList = this.reportData.checkDetails;
                                        }
                                    }, 100);
                                    

                                    this.timelineList = this.reportData.timeLineDetails;
                                    for(let index = 0; index < this.timelineList.length; index++) {
                                        this.timelineList[index].siteDate = this.dateFormatHelper(this.timelineList[index].siteDate);
                                        if(this.timelineList[index].siteTime == "NA") {
                                            this.timelineList[index].siteTime = "";
                                            this.timelineList[index].siteTimeNA = true;
                                        }
                                        else {
                                            this.timelineList[index].siteTime = this.getDateTimeFromTime(this.timelineList[index].siteTime);
                                            this.timelineList[index].siteTimeNA = false;
                                        }
                                    }

                                    this.troubleshootTimelineList = this.reportData.troubleshootTimelineDetails;
                                    for(let index = 0; index < this.troubleshootTimelineList.length; index++) {
                                        this.troubleshootTimelineList[index].siteDate = this.dateFormatHelper(this.troubleshootTimelineList[index].siteDate);
                                        // let siteTime = this.troubleshootTimelineList[index].siteTime;
                                        // this.troubleshootTimelineList[index].siteTimeHr = siteTime.split(":")[0];
                                        // this.troubleshootTimelineList[index].siteTimeMin = siteTime.split(":")[1];
                                    }
                                    
                                    this.reportFormOptions = JSON.parse(jsonStatue.siteInputs);
                                    // Set all form data
                                    this.reportData.reportDate = this.dateFormatHelper(this.reportData.reportDate);
                                    this.reportData.userName = key.packedBy;

                                    this.issuesCategory = Object.keys(this.reportFormOptions.issueCategory);
                                    this.issueAttributeToList = this.reportFormOptions.issueAttributeToList;
                                    this.issueTechnologyList = this.reportFormOptions.issueTechnologyList;
                                    this.issueResolvedList = this.reportFormOptions.issueResolvedList;

                                    this.timelineIssuesList.issues = this.reportData["categoryDetails"];
                                    this.postAuditIssues = jsonStatue.postAuditIssues ? jsonStatue.postAuditIssues : [];
                                    setTimeout(() => {
                                        // Set default project based on program
                                        if(!this.reportData["project"]) {
                                            this.reportData["project"] = this.programName == 'VZN-5G-MM' ? "NSB 5G" : this.programName == 'VZN-5G-DSS' ? "LS3" : this.programName == 'VZN-5G-CBAND' ? "LS6" : "";
                                        }
                                    }, 100);
                                    this.siteCompReportBlock = this.modalService.open(siteCompReportModal, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView reportView' });
                                } else {
                                    this.showLoader = false;
                                    this.displayModel(jsonStatue.reason, "failureIcon");
                                }
                            }
                            else {
                                this.showLoader = false;
                                this.displayModel("No Data", "failureIcon");
                            }
                        }
                    },
                    error => {
                        this.showLoader = false;

                        //Please Comment while checkIn

                        /* setTimeout(() => {
        
                            this.showLoader = false;
        
                            // let jsonStatue = {"siteInputs":{"projects":["SNAP - 5G","BAU - 5G"],"swRelease":["20-C-0","21-A-0","21-B-0"],"market":["OPW","WBV","TRI","CGC","CTX","NYM","NE","UNY","SAC","NO","PEN FL","HOU TX"],"integType":["Hot Cut","NSB","FOA Support","Test/Lab support"],"status":["Cancelled 48 Hr.","Cancelled 24 Hr.","Failed ","Rolled Back","Pre Migration Successful","Migration Successful","Partial Complete"],"issueCategory":{"Antenna":["ANT-00 Uncommon Failure","ANT-01 VSWR alarms","ANT-02 Fails RSSI test(s)","ANT-03 Fails VSWR test(s)"],"Cancelled":["CAN-00 Uncommon Failure","CAN-01 No Conquest & XCM","CAN-02 Site Access","CAN-03 Resources (TC,FC,DT)","CAN-05 Permits","CAN-06 Missing Material","CAN-07 VZ 48 Hr. Notice","CAN-08 Missing GPS","CAN-09 Time Constraints","CAN-10 Pre Ex. Alarms","CAN-11 Transport"],"Configuration":["CNF-00 Uncommon Failure","CNF-01 RU FW not loaded or issue","CNF-02 Incorrect Parameter","CNF-03 Incorrect Fiber length","CNF-04 Incorrect MME Pool","CNF-05 FW/SW fail to load /activated","CNF-06 Missing Inter-VM routes on vCU","CNF-07 DHCP connection issue","CNF-08 vCU hosting the AU not instantiated by customer","CNF-09 Static routes not configured on USM"],"Call Testing":["CTE-00 Uncommon Failure","CTE-01 No UE For Call Test","CTE-02 Low DL throughput","CTE-03 Failed to Attach to 5g","CTE-04 Failed to Attach to 4g","CTE-05 Inter-RAT NBR not defined between FSM4 and 5G AU/vCU","CTE-06 Nearby Overpowering Site"],"Design":["DES-00 Uncommon Failure","DES-01 Mismatch between Design and Physical Install","DES-02 X2 Links OOS after X2/ENDC script","DES-03 Incorrect CIQ Fail to create GROW Profiles","DES-04 Incorrect IP Information in CIQ and respective RF scripts","DES-05 Alarms - Pre-existing on Equipment/Network","DES-06 RF Scripts missing or not created","DES-07 Incorrect Parameters  - does not follow the engineering rules","DES-08 Missing Parameters","DES-09 CIQ missing in RF FTP Server","DES-10 Designed Vs. Install (example: designed for GEN2 Vs. physical GEN1 installed)"],"Hardware":["HW-00 Uncommon Failure","HW-01 Site/Sector Down -Pre","HW-03 PowerShift - DC power per requirement at RRH end","HW-04 RETs Misconfigured","HW-05 RETs disabled or lost the connectivity","HW-06 GPS unit Faulty","HW-07 Faulty Radio","HW-08 Bad Router card","HW-09 Bad Fiber/SFP/ CPRI fiber wrong length, dirty, broken","HW-10 Bad RFM (RRH, MMU)","HW-11 Bad CDU-30, LCC, LMD","HW-12 Cancelled the site / has not enough time in MW","HW-13 CPRI Power Readings out of range","HW-14 Door/Cabinet/Fan alarm Issue","HW-15 SFP incorrect/Faulty on CDU side","HW-16 SFP incorrect/Faulty on Tower Top side","HW-17 Non Samsung Approved GPS Cable"],"Installation":["INS-00 Uncommon Failure","INS-01 Installation not complete or Incorrect Installation","INS-02 Unable to commission the node","INS-03 Backhaul not ready","INS-04 Chassis backplane issue. Not able to reseat the card","INS-05 No Show","INS-06 GPS Sync Issue","INS-07 No Power","INS-08 Power issue/ Alarm","INS-09 Missing Backhaul Fiber","INS-10 Missing GPS Cable","INS-11 Missing CPRI Fiber","INS-12 Fibers not Labeled","INS-13 Missing Backhaul SFP","INS-14 Missing CPRI SFP","INS-15 Missing Equipment at Site","INS-16 Access - FT couldn’t load ENV","INS-17 RU is not connected","INS-18 Missing Attenuators","INS-19 Fibers not plugged into Raycap/OVP/Junction box/CPRI Panel","INS-20 Fibers connected to incorrect ports on the Radio head","INS-21 Missing Raycap/OVP/Junction box/CPRI Panel"],"Site Readiness":["SRE-00 Uncommon Failure","SRE-01 Site location unknown","SRE-02 Bad AU","SRE-03 GC has no proper Tools for Troubleshooting","SRE-04 SANE Issue","SRE-05 Rescheduled/Cancelled by PM","SRE-06 Site is removed/relocated by customer","SRE-07 Chrome (web-browser) issue","SRE-08 DUO issue/timed out"],"C&I Delivery tool":["SRT-00 Uncommon Failure","SRT-01 Unable to load CIQ to SRCT","SRT-02 Sectors are grown on different software load","SRT-03 Grow profiles not matching with engineering rules","SRT-04 SRCT not reachable","SRT-05 DBs down","SRT-06 Unable to run pnp grow script","SRT-07 SRCT failed to grow sector(s) on USM","SRT-08 Script too big for SCRT to run","SRT-09 SRCT Connection Manager issue","SRT-10 Leading zero is being removed by SRT tool (4G)"],"Transport":["TPT-00 Uncommon Failure","TPT-01 CSR Router Issue / Customer did not complete Router configuration","TPT-02 Incorrect Gateway IP in CIQ","TPT-03 Bad Backhaul Fiber","TPT-04 No connection to vRAN","TPT-05 No connection to RRH","TPT-06 GPS issue on IXR","TPT-07 No light from the IXR ports","TPT-08 Telecom IP alarm","TPT-09 Router port in CIQ is already in use","TPT-10 4G backhaul loosing remote connectivity when 5G backhaul is connected","TPT-11 Incorrect Router port Information on CIQ","TPT-12 Missing Router card","TPT-13 Cells fail to restore"],"S// EMS":["USM-00 Uncommon Failure","USM-01 slow response","USM-02 eNB alarms are not synced","USM-03 GA software not staged","USM-04 vDU not instantiated","USM-05 Unable to access USM","USM-06 Duplicate gNB in multiple ACPFs","USM-07 Could not lock/unlock Cell","USM-08 vRAN not ready","USM-09 GA software issue"]},"timelineList":["Nokia AU Powered Down / ALU locked down and site unmanaged","Samsung AUs Reachable","Samsung AUs C&I Start","Samsung AUs C&I Finish","Samsung AUs Powered down for pole installation","Samsung AUs pingable post Installation","Handoff to RF for Testing","Basic Call Test Complete (ATP & DT Passed )","Total C&I Effort"]},"sessionId":"40b5f7a","serviceToken":"67720","status":"SUCCESS","reason":""};
                            let jsonStatue = this.networkType == '5G' ? (this.programName == 'VZN-5G-MM' ? 
                                {"siteInputs":"{\"status\":[\"Cancelled 48 Hr.\",\"Cancelled 24 Hr.\",\"Failed \",\"Rolled Back\",\"Pre Migration Successful\",\"Migration Successful\",\"Partial Complete\",\"N/A\"],\"projects\":[\"SNAP 5G\",\"SNAP 4G & 5G\",\"SNAP 4G Hot Cut\",\"SNAP 4G Coverage/Capacity\",\"SNAP DSS/FSU\",\"NSB 4G\",\"NSB 5G\",\"LS3\",\"LS6\",\"BAU\",\"FSU Bypass\",\"N/A\"],\"swRelease\":[\"20-A-0\",\"20-C-0\",\"21-A-0\",\"N/A\"],\"region\":[\"OPW\",\"WBV\",\"TRI\",\"CGC\",\"CTX\",\"NYM\",\"NE\",\"UNY\",\"SAC\",\"NO\",\"PEN FL\",\"HOU TX\",\"N/A\"],\"integType\":[\"Hot Cut\",\"NSB\",\"FOA Support\",\"Test/Lab support\",\"N/A\"],\"vendorType\":[\"TWS\",\"Samsung\"],\"issueCategory\":{\"Antenna\":[\"ANT-00 Uncommon Failure\",\"ANT-01 VSWR/RSSI alarms\",\"ANT-02 Fails RSSI test(s)\",\"ANT-03 Fails VSWR test(s)\",\"ANT-04 Incorrect RFDS\",\"ANT-05 Defective Antenna\",\"ANT-06 BAD Hybrid cable\",\"ANT-07 BAD Diplexer\",\"N/A\"],\"Cancelled\":[\"CAN-00 Uncommon Failure\",\"CAN-01 No Conquest & XCM\",\"CAN-02 Site Access\",\"CAN-03 Resources (TC,FC,DT)\",\"CAN-04 Weather\",\"CAN-05 Permits\",\"CAN-06 Missing Material\",\"CAN-07 VZ 48 Hr. Notice\",\"CAN-08 Missing GPS\",\"CAN-09 Time Constraints\",\"CAN-10 Pre Ex. Alarms\",\"CAN-11 Transport\",\"CAN-12 No C&I Resources (SCH. 48 Hr. but CAN in 24Hr.)\",\"CAN-13 Construction Not Complete\",\"CAN-14 No Health Check from Fast Team\",\"CAN-15 NSB ENV Not Loaded\",\"CAN-16 Cancelled the site / has not enough time in MW\",\"CAN-17 Rescheduled/Cancelled by PM\",\"CAN-18 Root Metrics\",\"CAN-19 No N/A Support Vz\",\"CAN-20 Resources No Show\",\"N/A\"],\"Configuration\":[\"CNF-00 Uncommon Failure\",\"CNF-01 FW/SW fail to load /activated\",\"CNF-02 Missing Inter-VM routes on vCU\",\"CNF-03 DHCP connection issue\",\"CNF-04 vCU hosting the AU not instantiated by customer\",\"CNF-05 Static routes not configured on USM\",\"CNF-06 Swapped Sectors\",\"CNF-07 DES-02 X2 Links OOS after X2/ENDC script\",\"N/A\"],\"RET\":[\"RET -01 RET Motor Jam\",\"RET -02 RET Not Scanned using SBT\",\"RET -03 RET Not Scanned using AISG\",\"RET -04 RET Misconfigured (RFDS Correct) *\",\"RET -05 RETs Disabled or lost the connectivity\",\"N/A\"],\"Design\":[\"DES-00 Uncommon Failure\",\"DES-01 Mismatch between Design and Physical Install\",\"DES-02 Mismatch between Design and NE configuration\",\"DES-03 RF Scripts missing or not created\",\"DES-04 Incorrect Parameters  - does not follow the engineering rules\",\"DES-05 Missing Parameters\",\"DES-06 CIQ missing in RF FTP Server\",\"DES-07 Missing site information in OV\",\"DES-08 Missing RFDS\",\"DES-09 Missing RET form\",\"DES-10 Missing Conquest/XCM/CIQ for the site\",\"DES-11 Incorrect site information in OV\",\"DES-12 Incorrect RFDS\",\"DES-13 Incorrect RET form\",\"DES-14 Incorrect CIQ - Port Mapping\",\"DES-15 Incorrect CIQ - Power Settings\",\"DES-16 Incorrect CIQ - BW settings\",\"DES-17 Incorrect CIQ - MME Pool\",\"DES-18 Incorrect CIQ - Other (Not port mapping, power, BW settings or MME pool)\",\"DES-19 Incorrect IP Information in CIQ\",\"DES-20 RF Script Execution Failure\",\"N/A\"],\"Hardware\":[\"HW-00 Uncommon Failure \",\"HW-01 Critical condition of unknown cause (only issues that have been escalated and are under investigation)\",\"HW-02 PowerShift - Faulty or Misconfigured\",\"HW-03 GPS unit Faulty\",\"HW-04 Faulty Radio/ Antenna (RRH/MMU)\",\"HW-05 RRH ATP Power Test Failed\",\"HW-06 Bad AU\",\"HW-07 Bad Router card\",\"HW-08 Bad / Broken Fiber\",\"HW-09 Bad Chassis\",\"HW-10 CPRI Power Readings out of range\",\"HW-11 Door/Cabinet/Fan alarm Issue\",\"HW-12 SFP incorrect/Faulty on CDU30 or FSU\",\"HW-13 SFP incorrect/Faulty on Tower Top side\",\"HW-14 Non Samsung Approved GPS Cable\",\"HW-15 Bad Y Cable (between the FSU and the DU)\",\"HW-16 - UDA Alarms/issues\",\"HW-17 Bad SFP\",\"HW-18 CPRI fiber wrong length, dirty, broken\",\"HW-19 Dirty fiber\",\"HW-20 Bad LCC\",\"HW-21 Bad LMD\",\"HW-22 Bad FSU\",\"N/A\"],\"Installation\":[\"INS-00 Uncommon Failure\",\"INS-01 Faulty rectifier or insufficient rectifiers installed\",\"INS-02 Missing Backhaul Fiber\",\"INS-03 Missing GPS Cable\",\"INS-04 Missing CPRI Fiber\",\"INS-05 Fibers not Labeled\",\"INS-06 Missing Backhaul SFP\",\"INS-07 Missing CPRI SFP\",\"INS-08 Other Missing Materials\",\"INS-09 Missing Tools or Equipment necessary for installation\",\"INS-10 RU installation not complete\",\"INS-11 Missing Attenuators\",\"INS-12 Fibers not plugged into Raycap/OVP/Junction box/CPRI Panel\",\"INS-13 Fibers connected to incorrect ports on the Radio head\",\"INS-14 Missing Raycap/OVP/Junction box/CPRI Panel\",\"N/A\"],\"Site Readiness\":[\"SRE-00 Uncommon Failure\",\"SRE-01 Site Access Delay\",\"SRE-02 Resource Delay (TC,FC,DT)\",\"SRE-03 Resource Delay- C&I\",\"SRE-04 DUO/SANE Issue affecting SRCT or Engineer login\",\"SRE-05 Change in Planning, project logistics or priorities\",\"SRE-06 Non-C&I Related -Human Error\",\"SRE-07 Weather Delay\",\"SRE-08 Installation Delay\",\"SRE-09 Time Constraints (MW, sunset, etc.)\",\"SRE-10 Pre-existing alarms or conditions\",\"SRE-11 Missing Pre-existing health checks\",\"SRE-12 NSB ENV Not Loaded\",\"SRE-13 vRAN not ready \",\"SRE-14 vDU not instantiated\",\"SRE-15 C&I Related -Human Error\",\"N/A\"],\"C&I Delivery tool\":[\"SRT-00 Uncommon Failure \",\"SRT-01 Unable to load CIQ to SRCT\",\"SRT-02 Sectors are grown on different software load\",\"SRT-03 Bug in automation code\",\"SRT-04 SRCT not reachable\",\"SRT-05 DBs down\",\"SRT-06 Unable to run pnp grow script\",\"SRT-07 SRCT update needed\",\"SRT-08 Script too big for SCRT to run\",\"SRT-09 SRCT generated script execution failure\",\"SRT-10 Delayed/hang in progress during execution\",\"N/A\"],\"Transport\":[\"TPT-00 Uncommon Failure\",\"TPT-01 CSR Router Issue / Customer did not complete Router configuration\",\"TPT-02 No connection to vRAN\",\"TPT-03 Incorrect GTP/PTP configuration \",\"TPT-04 OAM Routing issue\",\"TPT-05 Telecom Routing issue\",\"N/A\"],\"S// EMS\":[\"USM-00 Uncommon Failure\",\"USM-01 slow response\",\"USM-02 eNB alarms are not synced\",\"USM-03 GA software not staged \",\"USM-04 Unable to access USM\",\"USM-05 Duplicate gNB in multiple ACPFs\",\"USM-06 Could not lock/unlock Cell\",\"USM-07 GA software issue\",\"USM-08 USM MAX Capacity\",\"USM-09 SAS Registration\",\"N/A\"],\"N/A\":[\"N/A\"]},\"timelineList\":[\"Nokia AU Powered Down / ALU locked down and site unmanaged\",\"Samsung AUs Reachable\",\"Samsung AUs C&I Start\",\"Samsung AUs C&I Finish\",\"Samsung AUs Powered down for pole installation\",\"Samsung AUs pingable post Installation\",\"Handoff to RF for Testing\",\"Basic Call Test Complete (ATP & DT Passed )\"],\"troubleshootTimelineList\":[\"Troubleshooting Time\",\"Total C&I Effort\"],\"reportStatus\":[\"Completion\",\"Exception\",\"N/A\"],\"typeOfEffort\":[\"1st Touch -1st Attempt \",\"1st Touch - 2nd Attempt \",\"2nd Touch 1st Attempt\",\"2nd Touch 2nd Attempt\",\"Hot Cut - 1st Attempt\",\"Hot Cut - 2nd Attempt\",\"Revisit - TS\",\"Prechecks\",\"Cutover\",\"N/A\"],\"issueResolvedList\":[\"Yes\",\"No\",\"N/A\"],\"issueAttributeToList\":[\"Verizon\",\"Samsung\",\"Tool\",\"Process\",\"RF Design\",\"C&I\",\"Deployment\",\"N/A\"]}","ciqName":"VZW_CTX_mmWave_SNAP_CIQ_020922_v19.5.xlsx","reportDetails":{"preChkPass":"No","neId":"13302100034|13302100035|13302100036_test","typeOfEffort":"Cutover","eNodeBName":"","mmCommComp":"Cancelled 24 Hr.","finalCbrsStatus":"","atpAllPass":"No","softWareRelease":"21.B.0-0201(AU.21B.P1.01)","fuzeProjId":"12345","followUpRequired":"No","project":"NSB 5G","categoryDetails":[{"issueId":null,"issue":"N/A","inIssueEditMode":false,"attribute":"N/A","category":"N/A","remarks":"","resolved":"N/A"}],"cellAdminStateIsPerCiq":"No","vDUInstCorrect":"No","neName":"0064_TX_DAL_IRVING_W4_064_-_A","cbrsStatus":"","alarmFree":"Yes","troubleshootTimelineDetails":[{"siteDate":"04-22-2022","siteTime":"01:00","timeLine":"Troubleshooting Time","remarks":""},{"siteDate":"04-22-2022","siteTime":"02:00","timeLine":"Total C&I Effort","remarks":""}],"vDUDay2Complete":"No","currCBANDIntegStatus":"","finalLTEStatus":"","basicSanityTestAllPass":"No","integrationType":"NSB","vendorType":"TWS","reportDate":"04-22-2022","finalCBANDIntegStatus":"","crossAnchor":"No","finalIntegStatus":"Rolled Back","siteReportStatus":"Completion","resAuditIssueCheck":"No","twampPingTestRepAttached":"Yes","eNodeBSW":"","carriers":[],"finalLaaStatus":"","market":"WBV","fsuBypass":"No","rfAtpStarted":"No","vDUSW":"","concFSUInteg":"No","allRetsScanned":"No","postChkPass":"No","timeLineDetails":[{"siteDate":"04-22-2022","siteTime":"NA","timeLine":"Nokia AU Powered Down / ALU locked down and site unmanaged","remarks":""},{"siteDate":"04-22-2022","siteTime":"NA","timeLine":"Samsung AUs Reachable","remarks":""},{"siteDate":"04-22-2022","siteTime":"NA","timeLine":"Samsung AUs C&I Start","remarks":""},{"siteDate":"04-22-2022","siteTime":"NA","timeLine":"Samsung AUs C&I Finish","remarks":""},{"siteDate":"04-22-2022","siteTime":"NA","timeLine":"Samsung AUs Powered down for pole installation","remarks":""},{"siteDate":"04-22-2022","siteTime":"NA","timeLine":"Samsung AUs pingable post Installation","remarks":""},{"siteDate":"04-22-2022","siteTime":"NA","timeLine":"Handoff to RF for Testing","remarks":""},{"siteDate":"04-22-2022","siteTime":"16:26","timeLine":"Basic Call Test Complete (ATP & DT Passed )","remarks":""}],"samsungUpStatus":"","allRetsLabeled":"No","remarks":"","laaStatus":"","auCellsReqperMarket":"Yes","fsuSW":""},"programName":"VZN-5G-MM","customerId":1,"siteId":null,"sessionId":"6ed5daf5","serviceToken":"89161","customerName":"","programId":40,"status":"SUCCESS","reason":""} : 
                                {"siteInputs":"{\n  \"integType\": [\n    \"Hot Cut\",\n    \"Coverage\",\n    \"Capacity\",\n    \"NSB\",\n    \"Carrier Add\",\n    \"FSU Install\",\n    \"DSS Cutover\",\n    \"Troubleshooting\"\n  ],\"vendorType\":[\"TWS\",\"Samsung\"],\n  \"issueAttributeToList\": [\n    \"Verizon\",\n    \"Samsung\",\n    \"Tool\",\n    \"Process\",\n    \"RF Design\",\n    \"C&I\",\n    \"Deployment\"\n  ],\n  \"issueCategory\": {\n    \"Antenna\": [\n      \"ANT-00 Uncommon Failure\",\n      \"ANT-01 VSWR/RSSI alarms\",\n      \"ANT-02 Fails RSSI test(s)\",\n      \"ANT-03 Fails VSWR test(s)\",\n      \"ANT-04 RET Motor Jam\",\n      \"ANT-05 RET Not Scanned\",\n      \"ANT-06 RETs Misconfigured\",\n      \"ANT-07 RETs disabled or lost the connectivity\"\n    ],\n    \"C&I Delivery tool\": [\n      \"SRT-00 Uncommon Failure \",\n      \"SRT-01 Unable to load CIQ to SRCT\",\n      \"SRT-02 Sectors are grown on different software load\",\n      \"SRT-03 Bug in automation code\",\n      \"SRT-04 SRCT not reachable\",\n      \"SRT-05 DBs down\",\n      \"SRT-06 Unable to run pnp grow script\",\n      \"SRT-07 SRCT update needed\",\n      \"SRT-08 Script too big for SCRT to run\",\n      \"SRT-09 SRCT generated script execution failure\",\n      \"SRT-10 Delayed/hang in progress during execution\"\n    ],\n    \"Call Testing\": [\n      \"CTE-00 Uncommon Failure \",\n      \"CTE-01 No UE For Call Test\",\n      \"CTE-02 Low DL throughput\",\n      \"CTE-03 Failed to Attach to 5G\",\n      \"CTE-04 Failed to Attach to 4G\",\n      \"CTE-05 Inter-RAT NBR not defined between FSM4 and 5G AU/vCU\",\n      \"CTE-06 Nearby Overpowering Site\"\n    ],\n    \"Cancelled\": [\n      \"CAN-00 Uncommon Failure\",\n      \"CAN-01 No Conquest & XCM\",\n      \"CAN-02 Site Access\",\n      \"CAN-03 Resources (TC,FC,DT)\",\n      \"CAN-04 Weather\",\n      \"CAN-05 Permits\",\n      \"CAN-06 Missing Material\",\n      \"CAN-07 VZ 48 Hr. Notice\",\n      \"CAN-08 Missing GPS\",\n      \"CAN-09 Time Constraints\",\n      \"CAN-10 Pre Ex. Alarms\",\n      \"CAN-11 Transport\",\n      \"CAN-12 No C&I Resources (SCH. 48 Hr. but CAN in 24Hr.)\",\n      \"CAN-13 Construction Not Complete\",\n      \"CAN-14 No Health Check from Fast Team\",\n      \"CAN-15 NSB ENV Not Loaded\",\n      \"CAN-16 Cancelled the site / has not enough time in MW\",\n      \"CAN-17 Rescheduled/Cancelled by PM\",\n      \"CAN-18 Root Metrics\",\n      \"CAN-19 No N/A Support Vz\"\n    ],\n    \"Configuration\": [\n      \"CNF-00 Uncommon Failure\",\n      \"CNF-01 FW/SW fail to load /activated\",\n      \"CNF-02 Missing Inter-VM routes on vCU\",\n      \"CNF-03 DHCP connection issue\",\n      \"CNF-04 vCU hosting the AU not instantiated by customer\",\n      \"CNF-05 Static routes not configured on USM\",\n      \"CNF-06 Swapped Sectors\",\n      \"CNF-07 DES-02 X2 Links OOS after X2/ENDC script\"\n    ],\n    \"Design\": [\n      \"DES-00 Uncommon Failure\",\n      \"DES-01 Mismatch between Design and Physical Install\",\n      \"DES-02 Mismatch between Design and NE configuration\",\n      \"DES-03 RF Scripts missing or not created\",\n      \"DES-04 Incorrect Parameters  - does not follow the engineering rules\",\n      \"DES-05 Missing Parameters\",\n      \"DES-06 CIQ missing in RF FTP Server\",\n      \"DES-07 Missing site information in OV\",\n      \"DES-08 Missing RFDS\",\n      \"DES-09 Missing RET form\",\n      \"DES-10 Missing Conquest/XCM/CIQ for the site\",\n      \"DES-11 Incorrect site information in OV\",\n      \"DES-12 Incorrect RFDS\",\n      \"DES-13 Incorrect RET form\",\n      \"DES-14 Incorrect CIQ - Port Mapping\",\n      \"DES-15 Incorrect CIQ - Power Settings\",\n      \"DES-16 Incorrect CIQ - BW settings\",\n      \"DES-17 Incorrect CIQ - MME Pool\",\n      \"DES-18 Incorrect CIQ - Other (Not port mapping, power, BW settings or MME pool)\",\n      \"DES-19 Incorrect IP Information in CIQ\",\n      \"DES-20 RF Script Execution Failure\"\n    ],\n    \"Hardware\": [\n      \"HW-00 Uncommon Failure \",\n      \"HW-01 Critical condition of unknown cause (only issues that have been escalated and are under investigation)\",\n      \"HW-02 PowerShift - Faulty or Misconfigured\",\n      \"HW-03 GPS unit Faulty\",\n      \"HW-04 Faulty Radio/ Antenna (RRH/MMU)\",\n      \"HW-05 RRH ATP Power Test Failed\",\n      \"HW-06 Bad AU\",\n      \"HW-07 Bad Router card\",\n      \"HW-08 Bad Fiber/SFP/ CPRI fiber wrong length, dirty, broken\",\n      \"HW-09 Bad Chassis, LCC, LMD, FSU\",\n      \"HW-10 CPRI Power Readings out of range\",\n      \"HW-11 Door/Cabinet/Fan alarm Issue\",\n      \"HW-12 SFP incorrect/Faulty on CDU30 or FSU\",\n      \"HW-13 SFP incorrect/Faulty on Tower Top side\",\n      \"HW-14 Non Samsung Approved GPS Cable\",\n      \"HW-15 Bad Y Cable (between the FSU and the DU)\",\n      \"HW-16 - UDA Alarms/issues\"\n    ],\n    \"Installation\": [\n      \"INS-00 Uncommon Failure\",\n      \"INS-01 Faulty rectifier or insufficient rectifiers installed\",\n      \"INS-02 Missing Backhaul Fiber\",\n      \"INS-03 Missing GPS Cable\",\n      \"INS-04 Missing CPRI Fiber\",\n      \"INS-05 Fibers not Labeled\",\n      \"INS-06 Missing Backhaul SFP\",\n      \"INS-07 Missing CPRI SFP\",\n      \"INS-08 Other Missing Materials\",\n      \"INS-09 Missing Tools or Equipment necessary for installation\",\n      \"INS-10 RU installation not complete\",\n      \"INS-11 Missing Attenuators\",\n      \"INS-12 Fibers not plugged into Raycap/OVP/Junction box/CPRI Panel\",\n      \"INS-13 Fibers connected to incorrect ports on the Radio head\",\n      \"INS-14 Missing Raycap/OVP/Junction box/CPRI Panel\"\n    ],\n    \"S// EMS\": [\n      \"USM-00 Uncommon Failure\",\n      \"USM-01 slow response\",\n      \"USM-02 eNB alarms are not synced\",\n      \"USM-03 GA software not staged \",\n      \"USM-04 Unable to access USM\",\n      \"USM-05 Duplicate gNB in multiple ACPFs\",\n      \"USM-06 Could not lock/unlock Cell\",\n      \"USM-07 GA software issue\",\n      \"USM-08 USM MAX Capacity\",\n      \"USM-09 SAS Registration\"\n    ],\n    \"Site Readiness\": [\n      \"SRE-00 Uncommon Failure\",\n      \"SRE-01 Site Access Delay\",\n      \"SRE-02 Resource Delay (TC,FC,DT)\",\n      \"SRE-03 Resource Delay- C&I\",\n      \"SRE-04 DUO/SANE Issue affecting SRCT or Engineer login\",\n      \"SRE-05 Change in Planning, project logistics or priorities\",\n      \"SRE-06 Non-C&I Related -Human Error\",\n      \"SRE-07 Weather Delay\",\n      \"SRE-08 Installation Delay\",\n      \"SRE-09 Time Constraints (MW, sunset, etc.)\",\n      \"SRE-10 Pre-existing alarms or conditions\",\n      \"SRE-11 Missing Pre-existing health checks\",\n      \"SRE-12 NSB ENV Not Loaded\",\n      \"SRE-13 vRAN not ready \",\n      \"SRE-14 vDU not instantiated\"\n    ],\n    \"Transport\": [\n      \"TPT-00 Uncommon Failure\",\n      \"TPT-01 CSR Router Issue / Customer did not complete Router configuration\",\n      \"TPT-02 No connection to vRAN\",\n      \"TPT-03 Incorrect GTP/PTP configuration \",\n      \"TPT-04 OAM Routing issue\",\n      \"TPT-05 Telecom Routing issue\"\n    ]\n  },\n  \"issueResolvedList\": [\n    \"Yes\",\n    \"No\"\n  ],\n  \"projects\": [\n    \"SNAP 5G\",\n    \"SNAP 4G & 5G\",\n    \"SNAP 4G Hot Cut\",\n    \"SNAP 4G Coverage/Capacity\",\n    \"SNAP DSS/FSU\",\n    \"NSB 4G\",\n    \"NSB 5G\",\n    \"LS3\",\n    \"LS6\",\n    \"BAU\",\n    \"FSU Bypass\"\n  ],\n  \"region\": [\n    \"OPW\",\n    \"WBV\",\n    \"TRI\",\n    \"CGC\",\n    \"CTX\",\n    \"NYM\",\n    \"NE\",\n    \"UNY\",\n    \"SAC\",\n    \"NO\",\n    \"PEN FL\",\n    \"HOU TX\"\n  ],\n  \"reportStatus\": [\n    \"Completion\",\n    \"Exception\"\n  ],\n  \"status\": [\n    \"Cancelled 48 Hr.\",\n    \"Cancelled 24 Hr.\",\n    \"Failed \",\n    \"Rolled Back\",\n    \"Pre Migration Successful\",\n    \"Partial Complete\",\n    \"Migration Successful\"\n  ],\n  \"swRelease\": [\n    \"20.C.0\",\n    \"21.A.0\",\n    \"21.B.0\"\n  ],\n  \"timelineList\": [\n    \"C&I Prechecks Start\",\n    \"C&I Prechecks Start\",\n    \"C&I Integration Start\",\n    \"Audits Pass\",\n    \"C&I Integration Complete\",\n    \"Troubleshooting Time\",\n    \"Total C&I Effort\"\n  ],\n  \"typeOfEffort\": [\n    \"1st Touch -1st Attempt \",\n    \"1st Touch - 2nd Attempt \",\n    \"2nd Touch 1st Attempt\",\n    \"2nd Touch 2nd Attempt\",\n    \"Hot Cut - 1st Attempt\",\n    \"Hot Cut - 2nd Attempt\",\n    \"Revisit - TS\",\n    \"Prechecks\",\n    \"Cutover\"\n  ],\n  \"vDUSW\": [\n    \"21A\",\n    \"21BP3\",\n    \"21BP3.01\",\n    \"21BP4\"\n  ],\n  \"eNodeBSW\": [\n    \"21AP3\",\n    \"21BP3\"\n  ],\n  \"fsuSW\": [\n    \"21AP2\",\n    \"21BP1\"\n  ]\n}","ciqName":"VZW_WBV_LS6_CIQ_v2.00_01242022.xlsx","reportDetails":{"preChkPass":"No","neId":"11394503076","typeOfEffort":"1st Touch -1st Attempt","eNodeBName":"NANSEMOND","mmCommComp":"","atpAllPass":"No","softWareRelease":null,"fuzeProjId":"12345","followUpRequired":"No","project":"LS6","categoryDetails":[{"issueId":null,"issue":"SRT-01 Unable to load CIQ to SRCT","inIssueEditMode":false,"attribute":"Samsung","category":"C&I Delivery tool","remarks":"","resolved":"Yes"}],"cellAdminStateIsPerCiq":"No","vDUInstCorrect":"No","neName":"11394503076_5GDU_NANSEMOND-NWNW","lteCBRSCommComp":"","alarmFree":"No","currCBANDIntegStatus":"Cancelled 24 Hr.","basicSanityTestAllPass":"No","integrationType":"","vendorType":"TWS","reportDate":"03-11-2022","finalCBANDIntegStatus":"Cancelled 48 Hr.","crossAnchor":"Yes","finalIntegStatus":"","dssCommComp":"Cancelled 24 Hr.","dssOpsATP":"Cancelled 48 Hr.","fsuIntegBypass":"Cancelled 24 Hr.","fsuIntegMultiplex":"Rolled Back","siteReportStatus":"Completion","postAuditIssues":[],"resAuditIssueCheck":"No","twampPingTestRepAttached":"No","eNodeBSW":"21.B.0-0104(ENB.21B.P3.01)","carriers":[],"market":"WBV","fsuBypass":"No","rfAtpStarted":"No","vDUSW":"21.B.0-0600(VDU.21B.P5.00)","concFSUInteg":"No","allRetsScanned":"No","postChkPass":"Yes","timeLineDetails":[{"siteDate":"03-11-2022","siteTime":"1","timeLine":"C&I Prechecks Start","remarks":""},{"siteDate":"03-11-2022","siteTime":"2","timeLine":"C&I Prechecks Start","remarks":""},{"siteDate":"03-11-2022","siteTime":"3","timeLine":"C&I Integration Start","remarks":""},{"siteDate":"03-11-2022","siteTime":"4","timeLine":"Audits Pass","remarks":""},{"siteDate":"03-11-2022","siteTime":"5","timeLine":"C&I Integration Complete","remarks":""}],"troubleshootTimelineDetails":[{"siteDate":"12-10-2022","siteTime":"02:10","timeLine":"Troubleshooting Time","remarks":""},{"siteDate":"03-09-2022","siteTime":"10:09","timeLine":"Total C&I Effort","remarks":""}],"lteCommComp":"","allRetsLabeled":"No","remarks":"","lteLAACommComp":"","lteOpsATP":"","lteCBRSOpsAtp":"","lteLAAOpsATP":"","tcReleased":"","ovTicketNum":"","auCellsReqperMarket":"No","fsuSW":""},"programName":"VZN-5G-CBAND","customerId":1,"siteId":null,"sessionId":"e75023be","serviceToken":"71696","customerName":"","programId":44,"postAuditIssues":[],"status":"SUCCESS"}) : 
                                {"ciqName":"Tri_State_Test_Sites_CIQ.xlsx","reportDetails":{"neId":"88123","mmCommComp":"","atpAllPass":"No","softWareRelease":"a12","fuzeProjId":"12345","followUpRequired":"No","project":"FSU","categoryDetails":[{"issueId":null,"issue":"","inIssueEditMode":true,"attribute":"","category":"","remarks":"","resolved":""}],"cellAdminStateIsPerCiq":"No","checkRSSI":"Yes","checkRSSIImbl":"No","checkVSWR":"No","checkFIBER":"No","checkSFP":"No","preMigrationHealth":false,"preExistingAlarm":false,"retRecived":false,"onsiteConfirm":false,"neName":"088123_Branchburg4","alarmFree":"No","basicSanityTestAllPass":"No","integrationType":"NSB","vendorType":"TWS","reportDate":"09-23-2021","finalIntegStatus":"Failed","siteReportStatus":"Completion","postAuditIssues":[],"resAuditIssueCheck":"No","twampPingTestRepAttached":"No","carriers":[{"likeforlike":"700","likeforlikeCheckBox":"No","incremental":"700","incrementalCheckBox":"No"},{"likeforlike":"850","likeforlikeCheckBox":"Yes","incremental":"850","incrementalCheckBox":"No"},{"likeforlike":"AWS","likeforlikeCheckBox":"No","incremental":"AWS","incrementalCheckBox":"Yes"},{"likeforlike":"PCS","likeforlikeCheckBox":"No","incremental":"PCS","incrementalCheckBox":"No"},{"likeforlike":"AWS3","likeforlikeCheckBox":"No","incremental":"AWS3","incrementalCheckBox":"No"},{"likeforlike":"CBRS","likeforlikeCheckBox":"Yes","incremental":"CBRS","incrementalCheckBox":"No"},{"likeforlike":"LAA","likeforlikeCheckBox":"No","incremental":"LAA","incrementalCheckBox":"No"}],"market":"NYM","fsuBypass":"Yes","rfAtpStarted":"No","allRetsScanned":"No","timeLineDetails":[{"siteDate":"09-23-2021","siteTime":"19:07","timeLine":"Nokia ENB unmanaged","remarks":"","siteTimeNA":false},{"siteDate":"09-23-2021","siteTime":"12:45","timeLine":"Samsung CDU-30 pingable","remarks":"","siteTimeNA":false},{"siteDate":"09-23-2021","siteTime":"01:02","timeLine":"Samsung CDU-30 C&I Start","remarks":"","siteTimeNA":false},{"siteDate":"09-23-2021","siteTime":"NA","timeLine":"Samsung CDU-30 C&I End","remarks":"","siteTimeNA":true},{"siteDate":"09-23-2021","siteTime":"NA","timeLine":"Waiting on tower crew to complete work on radios","remarks":"Hello All","siteTimeNA":true},{"siteDate":"09-23-2021","siteTime":"17:59","timeLine":"Detected all the radios, fw upgrade, OPS ATP","remarks":"","siteTimeNA":false},{"siteDate":"09-23-2021","siteTime":"NA","timeLine":"Unlocked sectors","remarks":"","siteTimeNA":true},{"siteDate":"09-23-2021","siteTime":"04:59","timeLine":"Basic Call Test Complete ","remarks":"","siteTimeNA":false}],"troubleshootTimelineDetails":[{"timeLine":"Troubleshooting Time","siteDate":"03-10-2022","siteTime":"11:34","siteTimeHr":"11","siteTimeMin":"34","remarks":"SRCT"},{"timeLine":"Total C&I Effort","siteDate":"03-10-2022","siteTime":"23:45","remarks":"Samsung"}],"checkDetails":[{"checkPerformed":"No","title":"Alarm Free","remarks":"This is my remarks"},{"checkPerformed":"Yes","title":"RSSI Alarm Checked","remarks":"No way"}],"allRetsLabeled":"No","remarks":"Hello All","auCellsReqperMarket":"No","lteCommComp":"Migration Successful","lteCBRSCommComp":"Rolled Back","lteLAACommComp":"Failed","lteOpsATP":"Rolled Back","lteCBRSOpsAtp":"Failed","lteLAAOpsATP":"Rolled Back","tcReleased":"No","ovTicketNum":"132","typeOfEffort":"Revisit - TS"},"sessionId":"6e1b7aa3","customerName":"","siteInputs":"{\"status\":[\"Cancelled 48 Hr.\",\"Cancelled 24 Hr.\",\"Failed \",\"Rolled Back\",\"Pre Migration Successful\",\"Migration Successful\",\"Partial Complete\"],\"projects\":[\"SNAP 5G\",\"SNAP 4G & 5G\",\"SNAP 4G Hot Cut\",\"SNAP 4G Coverage/Capacity\",\"SNAP DSS/FSU\",\"NSB 4G\",\"NSB 5G\",\"LS3 DSS/FSU - UNY /NE\",\"BAU\",\"FSU\"],\"swRelease\":[\"20.C.0\",\"21.A.0\",\"21.B.0\"],\"region\":[\"OPW\",\"WBV\",\"TRI\",\"CGC\",\"CTX\",\"NYM\",\"NE\",\"UNY\",\"SAC\",\"NO\",\"PEN FL\",\"HOU TX\"],\"integType\":[\"Hot Cut\",\"Coverage\",\"Capacity\",\"NSB\",\"Carrier Add\",\"FSU Install\",\"DSS Cutover\",\"LS6 ENDC\",\"LS3 ENDC\"],\"vendorType\":[\"TWS\",\"Samsung\"],\"issueCategory\":{\"Antenna\":[\"ANT-00 Uncommon Failure\",\"ANT-01 VSWR alarms\",\"ANT-02 Fails RSSI test(s)\",\"ANT-03 Fails VSWR test(s)\"],\"Cancelled\":[\"CAN-00 Uncommon Failure\",\"CAN-01 No Conquest & XCM\",\"CAN-02 Site Access\",\"CAN-03 Resources (TC,FC,DT)\",\"CAN-05 Permits\",\"CAN-06 Missing Material\",\"CAN-07 VZ 48 Hr. Notice\",\"CAN-08 Missing GPS\",\"CAN-09 Time Constraints\",\"CAN-10 Pre Ex. Alarms\",\"CAN-11 Transport\"],\"Configuration\":[\"CNF-00 Uncommon Failure\",\"CNF-01 RU FW not loaded or issue\",\"CNF-02 Incorrect Parameter\",\"CNF-03 Incorrect Fiber length\",\"CNF-04 Incorrect MME Pool\",\"CNF-05 FW/SW fail to load /activated\",\"CNF-06 Missing Inter-VM routes on vCU\",\"CNF-07 DHCP connection issue\",\"CNF-08 vCU hosting the AU not instantiated by customer\",\"CNF-09 Static routes not configured on USM\"],\"Call Testing\":[\"CTE-00 Uncommon Failure\",\"CTE-01 No UE For Call Test\",\"CTE-02 Low DL throughput\",\"CTE-03 Failed to Attach to 5g\",\"CTE-04 Failed to Attach to 4g\",\"CTE-05 Inter-RAT NBR not defined between FSM4 and 5G AU/vCU\",\"CTE-06 Nearby Overpowering Site\"],\"Design\":[\"DES-00 Uncommon Failure\",\"DES-01 Mismatch between Design and Physical Install\",\"DES-02 X2 Links OOS after X2/ENDC script\",\"DES-03 Incorrect CIQ Fail to create GROW Profiles\",\"DES-04 Incorrect IP Information in CIQ and respective RF scripts\",\"DES-05 Alarms - Pre-existing on Equipment/Network\",\"DES-06 RF Scripts missing or not created\",\"DES-07 Incorrect Parameters  - does not follow the engineering rules\",\"DES-08 Missing Parameters\",\"DES-09 CIQ missing in RF FTP Server\",\"DES-10 Designed Vs. Install (example: designed for GEN2 Vs. physical GEN1 installed)\"],\"Hardware\":[\"HW-00 Uncommon Failure\",\"HW-01 Site/Sector Down -Pre\",\"HW-03 PowerShift - DC power per requirement at RRH end\",\"HW-04 RETs Misconfigured\",\"HW-05 RETs disabled or lost the connectivity\",\"HW-06 GPS unit Faulty\",\"HW-07 Faulty Radio\",\"HW-08 Bad Router card\",\"HW-09 Bad Fiber/SFP/ CPRI fiber wrong length, dirty, broken\",\"HW-10 Bad RFM (RRH, MMU)\",\"HW-11 Bad CDU-30, LCC, LMD\",\"HW-12 Cancelled the site / has not enough time in MW\",\"HW-13 CPRI Power Readings out of range\",\"HW-14 Door/Cabinet/Fan alarm Issue\",\"HW-15 SFP incorrect/Faulty on CDU side\",\"HW-16 SFP incorrect/Faulty on Tower Top side\",\"HW-17 Non Samsung Approved GPS Cable\"],\"Installation\":[\"INS-00 Uncommon Failure\",\"INS-01 Installation not complete or Incorrect Installation\",\"INS-02 Unable to commission the node\",\"INS-03 Backhaul not ready\",\"INS-04 Chassis backplane issue. Not able to reseat the card\",\"INS-05 No Show\",\"INS-06 GPS Sync Issue\",\"INS-07 No Power\",\"INS-08 Power issue/ Alarm\",\"INS-09 Missing Backhaul Fiber\",\"INS-10 Missing GPS Cable\",\"INS-11 Missing CPRI Fiber\",\"INS-12 Fibers not Labeled\",\"INS-13 Missing Backhaul SFP\",\"INS-14 Missing CPRI SFP\",\"INS-15 Missing Equipment at Site\",\"INS-16 Access - FT couldn’t load ENV\",\"INS-17 RU is not connected\",\"INS-18 Missing Attenuators\",\"INS-19 Fibers not plugged into Raycap/OVP/Junction box/CPRI Panel\",\"INS-20 Fibers connected to incorrect ports on the Radio head\",\"INS-21 Missing Raycap/OVP/Junction box/CPRI Panel\"],\"Site Readiness\":[\"SRE-00 Uncommon Failure\",\"SRE-01 Site location unknown\",\"SRE-02 Bad AU\",\"SRE-03 GC has no proper Tools for Troubleshooting\",\"SRE-04 SANE Issue\",\"SRE-05 Rescheduled/Cancelled by PM\",\"SRE-06 Site is removed/relocated by customer\",\"SRE-07 Chrome (web-browser) issue\",\"SRE-08 DUO issue/timed out\"],\"C&I Delivery tool\":[\"SRT-00 Uncommon Failure\",\"SRT-01 Unable to load CIQ to SRCT\",\"SRT-02 Sectors are grown on different software load\",\"SRT-03 Grow profiles not matching with engineering rules\",\"SRT-04 SRCT not reachable\",\"SRT-05 DBs down\",\"SRT-06 Unable to run pnp grow script\",\"SRT-07 SRCT failed to grow sector(s) on USM\",\"SRT-08 Script too big for SCRT to run\",\"SRT-09 SRCT Connection Manager issue\",\"SRT-10 Leading zero is being removed by SRT tool (4G)\"],\"Transport\":[\"TPT-00 Uncommon Failure\",\"TPT-01 CSR Router Issue / Customer did not complete Router configuration\",\"TPT-02 Incorrect Gateway IP in CIQ\",\"TPT-03 Bad Backhaul Fiber\",\"TPT-04 No connection to vRAN\",\"TPT-05 No connection to RRH\",\"TPT-06 GPS issue on IXR\",\"TPT-07 No light from the IXR ports\",\"TPT-08 Telecom IP alarm\",\"TPT-09 Router port in CIQ is already in use\",\"TPT-10 4G backhaul loosing remote connectivity when 5G backhaul is connected\",\"TPT-11 Incorrect Router port Information on CIQ\",\"TPT-12 Missing Router card\",\"TPT-13 Cells fail to restore\"],\"S// EMS\":[\"USM-00 Uncommon Failure\",\"USM-01 slow response\",\"USM-02 eNB alarms are not synced\",\"USM-03 GA software not staged\",\"USM-04 vDU not instantiated\",\"USM-05 Unable to access USM\",\"USM-06 Duplicate gNB in multiple ACPFs\",\"USM-07 Could not lock/unlock Cell\",\"USM-08 vRAN not ready\",\"USM-09 GA software issue\"]},\"issueAttributeToList\":[\"Verizon\",\"Samsung\",\"Tool\",\"Process\",\"RF Design\",\"C&I\",\"Deployment\"],\"issueResolvedList\":[\"Yes\",\"No\"],\"timelineList\":[\"Nokia ENB unmanaged\",\"Samsung CDU-30 pingable\",\"Samsung CDU-30 C&I Start\",\"Samsung CDU-30 C&I End\",\"Waiting on tower crew to complete work on radios\",\"Detected all the radios, fw upgrade, OPS ATP\",\"Unlocked sectors\",\"Basic Call Test Complete \",\"Troubleshooting Time1\",\"Total C&I Effort1\"],\"criticalCheckList\":[\"Alarm Free\",\"Cells Admin state is per CIQ\",\"RSSI Alarm Checked\",\"RSSI Imbalance Checked\",\"VSWR Checked\",\"Fiber Checked\",\"SFP Checked\",\"ATP all pass\",\"All RETs scanned\",\"FSU Bypass\",\"All RETs labeled (Accounting for every carrier)\",\"Follow-up Required\"],\"reportStatus\":[\"Completion\",\"Exception\"],\"typeOfEffort\":[\"1st Touch -1st Attempt\",\"1st Touch - 2nd Attempt\",\"2nd Touch 1st Attempt\",\"2nd Touch 2nd Attempt\",\"Hot Cut - 1st Attempt\",\"Hot Cut - 2nd Attempt\",\"Revisit - TS\",\"Prechecks\",\"Cutover\"]}","programName":"VZN-4G-USM-LIVE","customerId":1,"siteId":null,"serviceToken":"69262","programId":34,"postAuditIssues":[{"test":"","testName":"PTP Sync","yangCommand":"","auditIssue":"pod-type : dpp sync-state : unlocked\n","expectedResult":"sync-state should be “locked”   to the CSR(LS6)","actionItem":"","remarks":"","errorCode":"LS3_Vir_Port_CompletionReport"},{"test":"","testName":"vDU Virtual Port check","yangCommand":"","auditIssue":"pod-type : rmp interface-name : - mtu : 1500\npod-type : rmp interface-name : - mtu : 1500\npod-type : dpp interface-name : - mtu : 1500\npod-type : dpp interface-name : - mtu : 1500\npod-type : dpp interface-name : - mtu : 1500\npod-type : dpp interface-name : - mtu : 1500\npod-type : dpp interface-name : - mtu : 1500\npod-type : dpp interface-name : - mtu : 1500\npod-type : dpp interface-name : - mtu : 1500\npod-type : dpp interface-name : - mtu : 1500\npod-type : dip interface-name : - mtu : 1500\n","expectedResult":"LS6 vDU should have 12 virtual-ports.\nThe virtual port MTU should be configured per CIQ","actionItem":"","remarks":"","errorCode":""},{"test":"","testName":"vDU external interface check","yangCommand":"","auditIssue":"pod-type : rmp interface-name : fh1 ip : fd00:4888:2a:11:0:406:0:401\npod-type : rmp interface-name : fh2 ip : fd00:4888:2a:12:0:406:0:401\npod-type : dpp interface-name : mh0 ip : 2001:4888:2010:e23d:122:406::1100\npod-type : dpp interface-name : mh1 ip : fd00:4888:20:520:0:406:0:400\npod-type : dip interface-name : mh0 ip : 2001:4888:2010:a0bb:104:406:0:2100\n","expectedResult":"LS6 vDU should have 6 external interfaces.\nThe IP address should be configured per CIQ.","actionItem":"","remarks":"","errorCode":""},{"test":"","testName":"F1-C status check on vDU","yangCommand":"","auditIssue":"gnb-cu-cp-name : 05790525GNBWSBOACPF9051 remote-ip-address : 2001:4888:2010:30:106:406:0:b033\n","expectedResult":"Remote ip address should match with that of CIC IP from CIQ","actionItem":"","remarks":"","errorCode":""},{"test":"","testName":"vDU Cell Antenna Diversity, CRS & ARFCN, Bandwidth, Cell path type Check","yangCommand":"","auditIssue":"cell-identity : 122 nr-physical-cell-id : 120 nr-arfcn-dl: 648672 nr-arfcn-ul : 648672\ncell-identity : 138 nr-physical-cell-id : 41 nr-arfcn-dl: 648672 nr-arfcn-ul : 648672\ncell-identity : 2522 nr-physical-cell-id : 122 nr-arfcn-dl: 648672 nr-arfcn-ul : 648672\n","expectedResult":"verify ARFCN and PCI","actionItem":"","remarks":"","errorCode":""},{"test":"","testName":"vDU Cell Antenna Diversity, CRS & ARFCN, Bandwidth, Cell path type Check","yangCommand":"","auditIssue":"cell-identity : 122 dl-antenna-count : dl-antenna-count-64tx\ncell-identity : 138 dl-antenna-count : dl-antenna-count-64tx\ncell-identity : 2522 dl-antenna-count : dl-antenna-count-64tx\n","expectedResult":"verify dl-antenna-count","actionItem":"","remarks":"","errorCode":""},{"test":"","testName":"vDU Cell Antenna Diversity, CRS & ARFCN, Bandwidth, Cell path type Check","yangCommand":"","auditIssue":"cell-identity : 122 ul-antenna-count : ul-antenna-count-64rx\ncell-identity : 138 ul-antenna-count : ul-antenna-count-64rx\ncell-identity : 2522 ul-antenna-count : ul-antenna-count-64rx\n","expectedResult":"verify ul-antenna-count","actionItem":"","remarks":"","errorCode":""},{"test":"","testName":"vDU Cell Antenna Diversity, CRS & ARFCN, Bandwidth, Cell path type Check","yangCommand":"","auditIssue":"cell-identity : 122 number-of-rx-paths-per-ru : 16\ncell-identity : 138 number-of-rx-paths-per-ru : 16\ncell-identity : 2522 number-of-rx-paths-per-ru : 16\n","expectedResult":"number-of-rx-paths-per-ru","actionItem":"","remarks":"","errorCode":""},{"test":"","testName":"vDU Cell Antenna Diversity, CRS & ARFCN, Bandwidth, Cell path type Check","yangCommand":"","auditIssue":"cell-identity : 122 cell-path-type : select-path-all\ncell-identity : 138 cell-path-type : select-path-all\ncell-identity : 2522 cell-path-type : select-path-all\n","expectedResult":"verify cell-path-type","actionItem":"","remarks":"","errorCode":""},{"test":"","testName":"Capture Post -Alarms on vDU","yangCommand":"","auditIssue":"alarm-type : no-phase-clock-ref-signal probable-cause : link-failure specific-problem : dpp no-phase-clock-ref-signal\n","expectedResult":"List all active alarms, only SERVICE-OFF alarm is expected","actionItem":"","remarks":"","errorCode":""},{"test":"","testName":"MMU ping test from vDU : oru ID 2","yangCommand":"","auditIssue":"o-ran-ru-id : 2 mplane-ipv6 : -\n","expectedResult":"mplane-ipv6","actionItem":"","remarks":"","errorCode":""},{"test":"","testName":"vDU Cell Antenna Diversity, CRS & ARFCN, Bandwidth, Cell path type Check","yangCommand":"","auditIssue":"cell-identity : 122 cell-num : 0\ncell-identity : 138 cell-num : 1\ncell-identity : 2522 cell-num : 2\n","expectedResult":"verify cell-num","actionItem":"","remarks":"","errorCode":""},{"test":"","testName":"vDU Cell Antenna Diversity, CRS & ARFCN, Bandwidth, Cell path type Check","yangCommand":"","auditIssue":"cell-identity : 122 administrative-state : unlocked\ncell-identity : 138 administrative-state : unlocked\ncell-identity : 2522 administrative-state : unlocked\n","expectedResult":"administrative-state : unlocked","actionItem":"","remarks":"","errorCode":""}],"status":"SUCCESS"};

                            // this.reportData = {"alarmFree":"Yes","allRetsLabeled":"Yes","allRetsScanned":"No","atpAllPass":"No","auCellsReqperMarket":"No","basicSanityTestAllPass":"No","carriers":[{"likeforlike":"700","incremental":"700","likeforlikeCheckBox":"No","incrementalCheckBox":"No"},{"likeforlike":"850","incremental":"850","likeforlikeCheckBox":"No","incrementalCheckBox":"Yes"},{"likeforlike":"AW","incremental":"AW","likeforlikeCheckBox":"Yes","incrementalCheckBox":"No"},{"likeforlike":"PCS","incremental":"PCS","likeforlikeCheckBox":"Yes","incrementalCheckBox":"No"},{"likeforlike":"AWS","incremental":"AWS","likeforlikeCheckBox":"No","incrementalCheckBox":"Yes"},{"likeforlike":"CBRS","incremental":"CBRS","likeforlikeCheckBox":"No","incrementalCheckBox":"No"},{"likeforlike":"LAA","incremental":"LAA","likeforlikeCheckBox":"No","incrementalCheckBox":"No"}],"categoryDetails":[{"issueId":null,"category":"Transport","issue":"TPT-05 No connection to RRH","remarks":"","inIssueEditMode":false},{"issueId":null,"category":"Installation","issue":"INS-07 No Power","remarks":"Hello All","inIssueEditMode":false}],"cellAdminStateIsPerCiq":"Yes","mmCommComp":"","finalIntegStatus":"Migration Successful","followUpRequired":"No","lteCBRSCommComp":"Rolled Back","lteLAACommComp":"Rolled Back","integrationType":"","lteCommComp":"Pre Migration Successful","market":"TRI","neId":"61452","neName":"061452_CONCORD_2_NH_HUB","postAuditIssues":[{"testName":"ENB State Check","yangCommand":"managed-element enb-function eutran-generic-cell eutran-cell-fdd-tdd usage-state","test":"To check if eNB is in the correct operational mode and its admin state","expectedResult":"operational-state: enabled, administrative-state: unlocked, operational-mode: normal-mode","actionItem":"Change administrative-state: unlocked.\n Change operational-mode: normal-mode. Check Cell state\n Check alarms if eNB continues to be disabled","auditIssue":"1. CellNum : 18 administrative-state : locked \n2. CellNum : 32 administrative-state : locked \n3. CellNum : 34 administrative-state : locked","remarks":""},{"testName":"ENB State Check","yangCommand":"managed-element enb-function eutran-generic-cell eutran-cell-fdd-tdd usage-state","test":"To check if eNB is in the correct operational mode and its admin state","expectedResult":"operational-state: enabled, administrative-state: unlocked, operational-mode: normal-mode","actionItem":"Change administrative-state: unlocked.\n Change operational-mode: normal-mode. Check Cell state\n Check alarms if eNB continues to be disabled","auditIssue":"1. CellNum : 18 operational-mode : growth-mode \n2. CellNum : 32 operational-mode : growth-mode \n3. CellNum : 34 operational-mode : growth-mode","remarks":""}],"project":"SNAP 4G Hot Cut","remarks":"","reportDate":"08-27-2021","resAuditIssueCheck":true,"rfAtpStarted":"No","siteReportStatus":"Exception","softWareRelease":"21-A-0","timeLineDetails":[{"timeLine":"Nokia ENB unmanaged","siteDate":"08-27-2021","siteTime":"2:33 AM","remarks":"Hello"},{"timeLine":"Samsung CDU-30 pingable","siteDate":"08-27-2021","siteTime":"","remarks":""},{"timeLine":"Samsung CDU-30 C&I Start","siteDate":"08-27-2021","siteTime":"","remarks":""},{"timeLine":"Waiting on tower crew to complete work on radios","siteDate":"08-27-2021","siteTime":"","remarks":""},{"timeLine":"Detected all the radios, fw upgrade, OPS ATP","siteDate":"08-27-2021","siteTime":"","remarks":""},{"timeLine":"Unlocked sectors","siteDate":"08-27-2021","siteTime":"","remarks":""},{"timeLine":"Basic Call Test Complete ","siteDate":"08-27-2021","siteTime":"","remarks":""}],"twampPingTestRepAttached":"Yes"};

                            if (jsonStatue.status == "SUCCESS") {
                                this.showLoader = false;
                                this.reportData = jsonStatue.reportDetails;

                                for(let index = 0; index < this.reportData.carriers.length; index++) {
                                    this.carriersFlag[this.reportData.carriers[index].likeforlike]["likeforlikeCheckBox"] = this.reportData.carriers[index].likeforlikeCheckBox == "Yes" ? true : false;
                                    this.carriersFlag[this.reportData.carriers[index].incremental]["incrementalCheckBox"] = this.reportData.carriers[index].incrementalCheckBox == "Yes" ? true : false;
                                }

                                // this.isCancellationReport = this.reportData.isCancellationReport == "Yes" ? true : false;
                                this.reportData.resAuditIssueCheck = this.reportData.resAuditIssueCheck == "Yes" ? true : false;
                                this.reportData.isCancellationReport = this.reportData.isCancellationReport == "Yes" ? true : false;
                                setTimeout(() => {
                                    if(this.reportData.isCancellationReport) {
                                        this.reportFormOptions = JSON.parse(jsonStatue.siteInputs);
                                        this.setCriticalCheckInitialData();
                                    }
                                    else {
                                        this.criticalCheckInfoList = this.reportData.checkDetails;
                                    }
                                }, 100);

                                this.timelineList = this.reportData.timeLineDetails;
                                for(let index = 0; index < this.timelineList.length; index++) {
                                    this.timelineList[index].siteDate = new Date(this.timelineList[index].siteDate);
                                    if(this.timelineList[index].siteTime == "NA") {
                                        this.timelineList[index].siteTime = "";
                                        this.timelineList[index].siteTimeNA = true;
                                    }
                                    else {
                                        this.timelineList[index].siteTime = this.getDateTimeFromTime(this.timelineList[index].siteTime);
                                        this.timelineList[index].siteTimeNA = false;
                                    }
                                }

                                this.troubleshootTimelineList = this.reportData.troubleshootTimelineDetails;
                                for(let index = 0; index < this.troubleshootTimelineList.length; index++) {
                                    this.troubleshootTimelineList[index].siteDate = new Date(this.troubleshootTimelineList[index].siteDate);
                                    // let siteTime = this.troubleshootTimelineList[index].siteTime;
                                    // this.troubleshootTimelineList[index].siteTimeHr = siteTime.split(":")[0];
                                    // this.troubleshootTimelineList[index].siteTimeMin = siteTime.split(":")[1];
                                }
                                
                                this.reportFormOptions = JSON.parse(jsonStatue.siteInputs);
                                // Set all form data
                                this.reportData.reportDate = new Date(this.reportData.reportDate);
                                this.reportData.userName = key.packedBy;
                                this.issuesCategory = Object.keys(this.reportFormOptions.issueCategory);
                                this.issueAttributeToList = this.reportFormOptions.issueAttributeToList;
                                this.issueTechnologyList = this.reportFormOptions.issueTechnologyList;
                                this.issueResolvedList = this.reportFormOptions.issueResolvedList;

                                this.timelineIssuesList.issues = this.reportData["categoryDetails"];
                                this.postAuditIssues = jsonStatue.postAuditIssues ? jsonStatue.postAuditIssues : [];
                                setTimeout(() => {
                                    // Set default project based on program
                                    if(!this.reportData["project"]) {
                                        this.reportData["project"] = this.programName == 'VZN-5G-MM' ? "NSB 5G" : this.programName == 'VZN-5G-DSS' ? "LS3" : this.programName == 'VZN-5G-CBAND' ? "LS6" : "";
                                    }
                                }, 100);
                                this.siteCompReportBlock = this.modalService.open(siteCompReportModal, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView reportView' });
                            } else {
                                this.showLoader = false;
                                this.displayModel(jsonStatue.reason, "failureIcon");
                            }
        
                        }, 100); */

                        //Please Comment while checkIn   
                    });
        }, 100);
        
    }

    setSiteReportNE() {
        let tempObj = null;
        this.siteReportNE = null;
        if(this.programName != "VZN-5G-MM") {
            tempObj = {
                "title": this.selectedEnb ? this.selectedEnb.eNBName : "",
                "ids": this.selectedEnb ? this.selectedEnb.eNBId : "",
            }
        }
        else {
            tempObj = {
                "title": this.selectedEnb ? this.selectedEnb.key : "",
                "ids": this.joinObjArray(this.selectedItemsNE, "item_id"),
            }
        }
        
        this.siteReportNE = tempObj;
        this.reportData["neName"] = this.siteReportNE["title"];
        this.reportData["neId"] = this.siteReportNE["ids"];
    }

    compareIssueCategoryFn(o1: any, o2: any) {
        return o1 && o2 ? o1 === o2 : o1 === o2;
    }

    showField(section, programName, field) {
        let fieldShowHideObj = {"GeneralInfo":{"VZN-4G-USM-LIVE":{"reportDate":true,"siteName":false,"neName":true,"neId":true,"siteReportStatus":true,"project":true,"softWareRelease":true,"market":true,"integrationType":true,"vendorType":true,"eNodeBSW":false,"vDUSW":false,"fsuSW":false,"eNodeBName":false,"fuzeProjId":true},"VZN-5G-MM":{"reportDate":true,"siteName":true,"neName":false,"neId":true,"siteReportStatus":true,"project":true,"softWareRelease":true,"market":true,"integrationType":true,"vendorType":true,"eNodeBSW":false,"vDUSW":false,"fsuSW":false,"eNodeBName":false,"fuzeProjId":true},"VZN-5G-DSS":{"reportDate":true,"siteName":false,"neName":true,"neId":true,"siteReportStatus":true,"project":true,"softWareRelease":false,"market":true,"integrationType":false,"vendorType":true,"eNodeBSW":true,"eNodeBName":true,"vDUSW":true,"fsuSW":true,"fuzeProjId":true},"VZN-5G-CBAND":{"reportDate":true,"siteName":false,"neName":true,"neId":true,"siteReportStatus":true,"project":true,"softWareRelease":false,"market":true,"integrationType":false,"vendorType":true,"eNodeBSW":true,"vDUSW":true,"fsuSW":false,"eNodeBName":true,"fuzeProjId":false},"VZN-4G-FSU":{"reportDate":true,"siteName":false,"neName":true,"neId":true,"siteReportStatus":true,"project":true,"softWareRelease":false,"market":true,"integrationType":false,"vendorType":true,"eNodeBSW":true,"eNodeBName":true,"vDUSW":false,"fsuSW":true,"fuzeProjId":false}},"SiteMigInfo":{"VZN-4G-USM-LIVE":{"lteCommComp":true,"lteCBRSCommComp":true,"lteCBRSOpsAtp":true,"lteLAACommComp":true,"lteLAAOpsATP":true,"tcReleased":true,"finalIntegStatus":true,"ovTicketNum":true,"lteOpsATP":true,"mmCommComp":false,"mmOpsATP":false,"typeOfEffortReport":true,"currCBANDIntegStatus":false,"finalCBANDIntegStatus":false,"dssCommComp":false,"dssOpsATP":false,"fsuIntegBypass":true,"fsuIntegMultiplex":false},"VZN-5G-MM":{"lteCommComp":false,"lteCBRSCommComp":false,"lteLAACommComp":false,"finalIntegStatus":true,"ovTicketNum":true,"mmCommComp":true,"mmOpsATP":true,"typeOfEffortReport":true,"currCBANDIntegStatus":false,"finalCBANDIntegStatus":false,"lteCBRSOpsAtp":false,"lteLAAOpsATP":false,"tcReleased":true,"lteOpsATP":false,"dssCommComp":false,"dssOpsATP":false,"fsuIntegBypass":false,"fsuIntegMultiplex":false},"VZN-5G-DSS":{"lteCommComp":false,"lteCBRSCommComp":false,"lteLAACommComp":false,"finalIntegStatus":true,"ovTicketNum":true,"mmCommComp":false,"mmOpsATP":false,"typeOfEffortReport":true,"currCBANDIntegStatus":false,"finalCBANDIntegStatus":false,"lteCBRSOpsAtp":false,"lteLAAOpsATP":false,"tcReleased":true,"lteOpsATP":false,"dssCommComp":true,"dssOpsATP":true,"fsuIntegBypass":false,"fsuIntegMultiplex":false},"VZN-5G-CBAND":{"lteCommComp":false,"lteCBRSCommComp":false,"lteLAACommComp":false,"finalIntegStatus":true,"ovTicketNum":true,"mmCommComp":false,"mmOpsATP":false,"typeOfEffortReport":true,"currCBANDIntegStatus":true,"finalCBANDIntegStatus":true,"lteCBRSOpsAtp":false,"lteLAAOpsATP":false,"tcReleased":true,"lteOpsATP":false,"dssCommComp":false,"dssOpsATP":false,"fsuIntegBypass":false,"fsuIntegMultiplex":false},"VZN-4G-FSU":{"lteCommComp":false,"lteCBRSCommComp":false,"lteLAACommComp":false,"finalIntegStatus":true,"ovTicketNum":true,"mmCommComp":false,"mmOpsATP":false,"typeOfEffortReport":true,"currCBANDIntegStatus":false,"finalCBANDIntegStatus":false,"lteCBRSOpsAtp":false,"lteLAAOpsATP":false,"tcReleased":true,"lteOpsATP":false,"dssCommComp":false,"dssOpsATP":false,"fsuIntegBypass":true,"fsuIntegMultiplex":true}}};

        let showField: Boolean = false;

        try {
            showField = fieldShowHideObj[section][programName][field];
        }
        catch {

        }
        return showField;
    }

    resetToValidValue(input, index, type) {
        // Change the model value to max if invalid input
        if(type == 'Hr' && (input > 23 || input < 0)) {
            this.troubleshootTimelineList[index].siteTimeHr = 23;
        }
        else if(type == 'Min' && (input > 59 || input < 0)) {
            this.troubleshootTimelineList[index].siteTimeMin = 59;
        }
        this.troubleshootTimelineList[index].siteTimeHr = this.sharedService.addLeadeingZero(this.troubleshootTimelineList[index].siteTimeHr);
        this.troubleshootTimelineList[index].siteTimeMin = this.sharedService.addLeadeingZero(this.troubleshootTimelineList[index].siteTimeMin);
    }

    getDateTimeFromTime(time) {
        // Time in format 01:45 am
        let d = new Date();
        let timeData = time.split(":");
        /* let timeArr = timeData[0].split(":");
        let isPM = (timeData[1] == 'PM' && timeArr[0] != '12') ? true : false;
        let hr = isPM ? parseInt(timeArr[0]) + 12 : timeArr[0]; */

        d.setHours(timeData[0]);
        d.setMinutes(timeData[1]);
        return d;
    }

    getTechnologyName() {
        let techName = "";
        switch(this.programName) {
            case "VZN-5G-MM":
                techName = "5G";
                break;
            case "VZN-5G-DSS":
                techName = "LS3";
                break;
            case "VZN-5G-CBAND":
                techName = "LS6";
                break;
            case "VZN-4G-FSU":
                techName = "FSU";
                break;
            default:
                techName = "4G";
        }
        return techName;
    }

    issuecategoryChange(index) {
        if(this.timelineIssuesList.issues[index].category == "N/A") {
            this.timelineIssuesList.issues[index].issue = "N/A";
            this.timelineIssuesList.issues[index].technology = "N/A";
            this.timelineIssuesList.issues[index].attribute = "N/A";
            this.timelineIssuesList.issues[index].resolved = "N/A";
        }
    }

    viewOVFailureMessagae(failureErrorModal, key) {
        this.ovRetryData = {
            runTestId: key.id,
            ciqName: key.ciqFileName,
            neName: key.neName
        };
        this.showLoader = true;

        this.siteDataService.viewOVFailureMessage(this.sharedService.createServiceToken(), key.id, key.ciqFileName, key.neName)
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
                        
                        let jsonStatue = {"history":"[2022-09-22 14:11:14]-Successfully MileStone: P_PREEXISTING_RSSI Updated for NeId: 243653\n[2022-09-22 11:07:02]-Successfully MileStone: P_PREEXISTING_RSSI Updated for NeId: 243653\n","sessionId":"66f02d41","milestones":[{"customerDetailsEntity":null,"migrationType":null,"failedScript":null,"migrationSubType":null,"checklistFileName":null,"neName":null,"progressStatus":null,"ciqName":null,"outputFilepath":null,"testName":null,"testDescription":null,"lsmName":null,"lsmVersion":null,"useCase":null,"status":"fail","userName":null,"creationDate":null,"useCaseDetails":null,"customerId":0,"id":0,"wfmid":0,"useCaseSequence":0,"result":null,"resultFilePath":null,"fromDate":null,"toDate":null,"generateScriptPath":null,"migStatusDesc":null,"ovUpdateStatus":null,"name":"P_PREEXISTING_RSSI"}],"serviceToken":"84611","status":"SUCCESS","reason":""};

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

                    }, 100); */

                    //Please Comment while checkIn   
                });
    }

    closeModelFailureMsg() {
        this.milestoneResult = [];
        this.historyResult = "";
        this.ovRetryData = {};
        this.failureMsgBlock.close();
    }

    ovRetry() {
        this.showInnerLoader = true;

        this.siteDataService.retryMilestoneUpdate(this.sharedService.createServiceToken(), this.ovRetryData, this.milestoneResult)
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

    uploadToOV (key) {
        this.showLoader = true;
        this.siteDataService.uploadSiteReportToOV(this.sharedService.createServiceToken(), key)
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
                                this.message = jsonStatue.reason;
                                this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                                this.closeModelFailureMsg();
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
                        
                        let jsonStatue = {"reason":"","sessionId":"8909e5cf","serviceToken":"61626","status":"SUCCESS","messageInfo":"Upload Site Report to OV Successfully."};

                        if (jsonStatue.status == "SUCCESS") {
                            this.showLoader = false;
                            this.message = "Upload Site Report to OV Successfully.";
                            this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                            this.closeModelFailureMsg();
                        } else {
                            this.showLoader = false;
                            this.displayModel(jsonStatue.reason, "failureIcon");
                        }

                    }, 100); */

                    //Please Comment while checkIn   
                });
    }
    dateFormatHelper(str:string){
        if(str.indexOf(":") != -1){
          return str;
        }else{
          const [month, day, year] = str.split("-");
          const date = new Date(+year, +month - 1, +day);
          return date;
        }    
      }
}





