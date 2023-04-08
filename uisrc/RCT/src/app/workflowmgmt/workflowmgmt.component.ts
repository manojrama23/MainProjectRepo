import { ViewContainerRef, Input, ElementRef, Renderer, Component, OnInit, ViewChild, HostListener } from '@angular/core';
import { Router,NavigationEnd } from '@angular/router';
import { NgbModal, ModalDismissReasons, NgbTabsetConfig } from '@ng-bootstrap/ng-bootstrap';
import { PregrowService } from '../services/pregrow.service';
import { SharedService } from '../services/shared.service';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { filter, map, mergeMap } from 'rxjs/operators';
import { validator } from '../validation';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { WorkflowmgmtService } from '../services/workflowmgmt.service';
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';
import { NgbPopover } from '@ng-bootstrap/ng-bootstrap';
import * as FileSaver from 'file-saver';
import * as _ from 'underscore';
import * as $ from 'jquery';
import * as jsPDF from 'jspdf';
import { DatePipe } from '@angular/common';


@Component({
  selector: 'app-workflowmgmt',
  templateUrl: './workflowmgmt.component.html',
  styleUrls: ['./workflowmgmt.component.scss'],
  providers: [WorkflowmgmtService]
})

export class WorkflowmgmtComponent implements OnInit {
    tabularResultsData:any;
    max = new Date();
    isProcessCompleted:any;
    isResultProcessCompleted:boolean;
    fromDt:any;
    statusCheck:any;
    neVersion :any;
    fsuType: any;
    tableData: any;
    editMode: number = -1;
    editScriptMode : number = -1;
    editCLMode:number =-1;
    showNoDataFound: boolean;
    tableShowHide: boolean;
    checkListTable:boolean = false;
    showLoader: boolean = true;
    showInnerLoader: boolean = false;
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
    viewData: any
    // useCaseData: any
    nwTypeDetails: any;
    lsmVersionDetails: any;
    selectedVersion: any = "";
    selectedName: any = "";
    lsmNameDetails: any;
    testResultsData: any;
    showWideRunningLog:boolean;
    neNameList:any =[];
    neNameSiteList:any =[];
    ciqDetails:any;
    neDetails:any;
    testName:any;
    wfmid:any;
    useCaseSO = "";
    scriptList = [];
    showOutput: boolean = false;
    scriptOutput: any;
    selectedScriptName: string = '';
    ciqName: string = '';
    selectedUsecase: any;
    selectedScript: any
    isItInProgress:boolean;
    useCaseValue = [];
    PMiguseCaseValue = [];
    PAMiguseCaseValue = [];
    NGuseCaseValue = [];
    MiguseCaseValue = [];
    scriptValue = [];
    scriptValueNG = [];
    scriptValueMig = [];
    scriptValuePMig = [];
    pascriptValuePMig = [];
    useCaseModelData = {};
    premigData:boolean;
    totalScriptSelected: number = 0;
    selectedLsm: any = ""
    // Following variables are used to dispaly success, confirm and failure model(s)
    showModelMessage: boolean = false;
    modelData: any;
    closeResult: string;
    currentEditRow: any;
    successModalBlock: any;
    successModalBlock1: any;
    pswdModalBlock:any;
    useCaseModalBlock:any;
    viewModalBlock: any;
    failureMsgBlock: any;
    message: any;
    testId: any;
    commissionType ="";
    searchBlock: boolean = false;
    createNewForm: boolean = false;
    disableDropdown: boolean = false;
     // To track activity
     searchStatus: string;
     searchCriteria: any;
     smVersion:any;
     ciqList:any;
     nameDetails:any ="";
     versionDetails:any;
    // To track activity
    navigationSubscription: any;
    ckeckedOrNot:boolean = false;
    rfScriptFlag: boolean = false;
    ranAtpFlag: boolean = false;
    isPRECHECKShow: boolean = false;
    ranAtpFlagUseCase: boolean = false;
    sessionExpiredModalBlock: any; // Helps to close/open the model window
    pager: any = {}; // pager Object
    //   testName : any;
    //   nwType : any;
    //   lsmVersion : any;
    //   useCase : any;
    //   lsmName:any;
    //   testDescription:any;
    dropdownList = [];
    useCaseListPM = [];
    useCaseListPA = [];
    useCaseListNE = []
    dropdownNEList =[];
    dropdownRadioUnitList = [];
    dropdownListNG = [];
    selectedItems = [];
    selectedPMigItems=[];
    selectedPAMigItems=[];
    selectedNGItems=[];
    selectedNGStatusItems=[];
    selectedMigItems=[];
    selectedItemsPMig=[];
    selectedItemsPAMig=[];
    selectedItemsNEMig=[];
    selectedItemsNG=[];
    selectedRadioUnittems =[];
    viewUsecaseType:any;
    selectedNEItems =null;
    dropdownSettings = {};
    dropdownPMigSettings = {};
    dropdownPAMigSettings = {};
    dropdownNGSettings={};
    dropdownMigSettings = {};
    dropdownSettingsNE = {};
    dropdownSettingsRadioUnit = {};
    pgmName:any;
    smName:any;
    neName:any;
    serverInfo:any;
    runTestDetails:any;
    runTestDetailsPost:any;
    runTestDetailsNEGrow:any;
    requestType:any;

    selSearchSMName:any;
    selSearchVer:any;
    selSearchCiqName;
    isRETDisabled: boolean = true;
    isRSSIDisabled: boolean = true;
    // Check List
    checkListModalBlock: any;
    noCheckListDataVisibility: boolean = false;
    checklistSheetList = [];
    checklistTableData:any;
    sheetName:any;
    sheetHighlight:any;
    rowData: any;

    // Running Log
    runningLogs: any;
    runningLogModalBlock: any;
    showRunningLogLauncher: boolean;
    showRunningLogContent: boolean;

    // Check Connection Log
    checkConnLogs: any;
    checkConnLogModalBlock: any;

    selectedProgram: any;
    ciqConfig: object;
    interval: any;
    runningLogInterval: any;
    resultPopupInterval: any;

    scriptFilesData: any;
    generateAllSites: boolean = false;
  
    allScriptSelect: boolean = true;
    showMySites: boolean = false;
    failedScript: boolean = false;
    programChangeSubscription:any;
    networkType: any = "";
    programName: any = "";
    ICONS = [32, 16, 8, 4, 2, 1]; // ["Play", "Runniing Log", "CheckList", "Result", "DownloadLogs", "DownloadGenScript"]
    STATE_USECASE = [4, 2, 1, 8, 16]; //[NEGrow, Migration, PostMigration]
    showStateUseCase = "";
    useCaseModelKey = null;
    postMigModalKey = null;
    postWfmId = null;
    useCaseDropDownVisibility = 0;
    userGroup = JSON.parse(sessionStorage.loginDetails).userGroup;
    iscommisionEngineer:boolean = false;

    /* @ViewChild('p1') public p1: NgbPopover;
    @ViewChild('p2') public p2: NgbPopover; */
    p1: NgbPopover;
    p2: NgbPopover;
    selectedNEIDs = [];
    programType: any;
    disableDropdownNG: boolean=false;
    dropdownListNE = [];
    selectedItemsNE: any;
    selectedNEItemss=[];
    disabled: boolean=false;
    selectedNeVersion:any;
    selectedFSUType: any;
    dropdownNGList: any[];
    dropdownMigList: any[];
    dropdownPMigList: any[];

    auditIssuesBlock: any;
    auditIssueNEInfo: any;
    postAuditIssues: any;
    siteDetailsValues: any;
    postAuditPassFailIssues: any;
    auditIssueKey: any;
    selectedAuditTab: string = 'auditFailureTab';

    bulkNeCheck:boolean = false;
    rfScriptList: any;
    rfScriptListModel: any;
    scriptListModalBlock: any;

    siteCompReportBlock: any;
    
    reportData: any;
    carriersFlag: any;
    criticalCheckInfoList: any;
    reportFormOptions: any;
    issuesCategory: any;
    // TODO: Timebeing solution. Below variables will be part of issueCategory
    issueAttributeToList: any;
    issueTechnologyList: any;
    issueResolvedList: any;

    timelineIssuesList: any = [];
    timelineList: any = [];
    troubleshootTimelineList: any = [];
    currIssueEditScript: any = null;
    currTimelineEditScript: any = null;

    siteReportNE: any;
    selectedTableRow: any;
    selectedReportCIQ: any;

    siteReportHistoryBlock: any;
    siteReportHistory: any;

    stopSiteBlock: any;

    userGroupDetails: any;

    siteInProgressFromDt: any = new Date();
    siteInProgressToDt: any = new Date();
    sityTypeRadio: any = "inProgress";
    userList:any = [];
    selectedUser = [];
    auditInprogressSites: any;
    allSiteRowSelected: any = false;
    supportCA: boolean;
    resultColumn: string = "";
    isRSSIUseCaseSelected: boolean = false;
    isRETUseCaseSelected: boolean = false;
    prePostAuditFlag: boolean = false;
    runTestPreAudit: any;
    runTestNeStatus: any;
    PAuseCaseValue: any[];
    PAscriptValue: any[];
    NEuseCaseValue: any[];
    NEscriptValue: any[];
    dropdownPreAuditList: any[];
    Scriptcount: any = 0;
    multipleDuo: boolean = false;
    ovUpdate: boolean = false;
    postMigStatus:boolean = false;
    comments: string = "";
    migrationStrategy: string = 'Legacy IP';
    ovRetryData: any;
    siteReportOV: boolean = false;
    errorMsgResult: any;
    milestoneResult: any;
    historyResult: any; 
    dropdownNeStatusList: any[];
    NSuseCaseValue: any[];
    NSscriptValue: any[];
    dropdownNEMigSettings: {};
    scriptcompleted = 0;
    growUsecaseTooltip: string = "pnp$ To Grow eNB+Cell on the USM\nGrowEnb$ To Grow only eNB on the USM\nGrowCell$ To Grow only cells on selected eNB\nCA_Usecase$ Select only when doing carrier add along with GrowCell\nDeleteNE$ Delete the eNB on USM";
    migUsecaseTooltip: string = "RFUsecase$ To apply RF configurations for selected eNB on USM\nEndcUsecase$To apply ENDC configurations for selected eNB on USM";
    auditUsecaseTooltip: string = "OranHealthCheck$ To check eNB health after CI Commissioning is completed\nrssiImbalance_Rangecheck$ To check RSSI related parmaters after CI Commissioning is completed\nOranAudit_Cbrs$ To check CBRS related parmameters after CI Commissioning is completed\nRET_Audit$ To check RET related configuration. Need to have a standardized RET form to perform this.\nOcnsTest$ To check Power-VSWR related parmameters after CI Commissioning is completed\ngrowPrefixRem$ To be selected if GROW_ should be removed from the eNB Name.";
    

    @HostListener('window:scroll', ['$event'])
    scrollHandler(event) {
        if (this.p1 && this.p1.isOpen()) {
            this.p1.close();
        }

        if (this.p2 && this.p2.isOpen()) {
            this.p2.close();
        }
    }

    validationData: any = {
        "rules": {
            "testName": {
                "required": true
            },           
            "lsmVersion": {
                "required": true
            },
            "lsmName": {
                "required": true
            },
            "ciqName":{
                "required": true
            },
            "location": {
                "required": true //this.validateUseCase()
            },
             "neName": {
                "required": true 
            },
            "updatedExeSeq": {
                "required": true,
                "customfunction": true
            },
            "password":{
                "required":false
            }

        },
        "messages": {
            "testName": {
                "required": "Name is required"
            },            
            "lsmVersion": {
                "required": "SM Version is required"
            },
            "lsmName": {
                "required": "SM Name is required"
            },
            "ciqName":{
                "required": "CIQ is required"
            },
            "location": {
                "required": "Use Case is required"
            },
            "neName": {
                "required": "NE Name is required"
            },
            "updatedExeSeq": {
                "required": "Exe seq is required",
                "customfunction": "Duplicate exe seq"
            },
            "password":{
                "required":"Password required"
            }
        }
    };


    @ViewChild('sessionExpiredModal') sessionExpiredModalRef: ElementRef;
    @ViewChild('confirmModal') confirmModalRef: ElementRef;
    @ViewChild('deleteModal') deleteModalRef: ElementRef;
    @ViewChild('successModal') successModalRef: ElementRef;
    @ViewChild('passwordModal') passwordModalRef: ElementRef;
    @ViewChild('searchTab') searchTabRef: ElementRef;
    @ViewChild('createNewTab') createNewTabRef: ElementRef;
    @ViewChild('runTestForm') runTestForm;
    @ViewChild('searchForm') searchForm;
    @ViewChild('filePost') filePostRef: ElementRef;
    @ViewChild('migPromptModal') migPromptModalRef: ElementRef;
    @ViewChild('changeStatusModal') changeStatusModalRef : ElementRef;


    constructor(
        private element: ElementRef,
        private renderer: Renderer,
        private router: Router,
        private modalService: NgbModal,
        private runtestService: WorkflowmgmtService,
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
        this.sharedService.startDuoConnectionCheck();
    }
    loadInitialData(){
        this.tableDataHeight = (($("#contentWrapper").height()) - ($("#container").height() + $(".mainHead").height() + $(".nav").height() + 50));        
        this.showLoader = true;
        //For Pagination
        this.currentPage = 1;
        this.totalPages = 1;
        this.TableRowLength = 10;
        this.pageSize = 10;
        this.commissionType = "precheck";
        this.createNewForm = true;  
        this.searchBlock = false;
        this.runningLogs = "";
        this.checkConnLogs = "";
        this.isRSSIDisabled = true;
        this.isRETDisabled = true;
        this.disabled=false;
        this.setMenuHighlight("createNew");
        this.searchStatus = 'load';
        this.selectedProgram = JSON.parse(sessionStorage.getItem("selectedProgram"));
        let paginationDetails = {
            "count": this.TableRowLength,
            "page": this.currentPage
        };
        this.selectedItems = [];
        this.ciqDetails = [];
        this.clearSelectedNEs();
        this.selectedNEItems = null;
        this.ckeckedOrNot = true;
        this.rfScriptFlag = true;
        this.bulkNeCheck = false;
        this.ranAtpFlag=false;
        this.isPRECHECKShow = false;
        this.prePostAuditFlag = false;
        this.ovUpdate = false;
        this.multipleDuo = this.programName == 'VZN-4G-USM-LIVE' || this.programName == 'VZN-5G-MM' ? true : false;
        this.paginationDetails = paginationDetails;
        this.ciqConfig = {
            displayKey: "ciqFileName",
            search: true,
            height: '200px',
            placeholder: '--Select--',
            customComparator: () => { },
          //  limitTo: this.ciqList.length,
            moreText: 'more',
            noResultsFound: 'No results found!',
            searchPlaceholder: 'Search',
            searchOnKey: 'ciqFileName',
        }
        this.userGroupDetails = JSON.parse(sessionStorage.loginDetails).userGroup;
        this.selectedFSUType = "";
        this.rfScriptList = "";
        this.reportData = {"neName":"","neId":"","reportDate":"","siteReportStatus":"","eNodeBName":"","eNodeBSW":"","fsuSW":"","vDUSW":"","project":"","softWareRelease":"","market":"","fuzeProjId":"","resAuditIssueCheck":false,"integrationType":"","vendorType":"","mmCommComp":"","mmOpsATP":"","dssCommComp":"","dssOpsATP":"","fsuIntegBypass":"","fsuIntegMultiplex":"","finalIntegStatus":"","currCBANDIntegStatus":"","finalCBANDIntegStatus":"","typeOfEffort":"","remarks":"","timeLineDetails":"","categoryDetails":"","lteCommComp":"","lteCBRSCommComp":"","lteLAACommComp":"","lteOpsATP":"","lteCBRSOpsAtp":"","lteLAAOpsATP":"","tcReleased":"","ovTicketNum":""};//, "emailConfigured":[]
        this.carriersFlag = {"700":{"likeforlikeCheckBox":false,"incrementalCheckBox":false},"850":{"likeforlikeCheckBox":false,"incrementalCheckBox":false},"AWS":{"likeforlikeCheckBox":false,"incrementalCheckBox":false},"PCS":{"likeforlikeCheckBox":false,"incrementalCheckBox":false},"AWS3":{"likeforlikeCheckBox":false,"incrementalCheckBox":false},"CBRS":{"likeforlikeCheckBox":false,"incrementalCheckBox":false},"LAA":{"likeforlikeCheckBox":false,"incrementalCheckBox":false}};//{"likeForLike":{"700":false,"850":false,"AW":false,"PCS":false,"AWS":false,"CBRS":false,"IAA":false},"incremental":{"700":false,"850":false,"AW":false,"PCS":false,"AWS":false,"CBRS":false,"IAA":false}};
        this.resetReportTimelineIssueForm();
        this.resultColumn = "";
        this.ciqList = [];
        this.ovRetryData = {};
        this.siteReportOV = false;
        this.getRunTest();
        this.resetRunTestForm();
        setTimeout(() => this.generateAllSites = true, 100);
        setTimeout(() => this.supportCA = false, 100);
        /* // On every 10s refresh the table
        if(!this.interval) {
            this.interval = setInterval( () => {
                this.updateRunTestTable();
            }, 10000 );
        } */
        this.migrationStrategy = 'Legacy IP'
        
    }

    clearSearchFrom() {
        this.searchForm.nativeElement.reset();  
    }
      

    ngOnDestroy() {
        this.programChangeSubscription.unsubscribe();
        // avoid memory leaks here by cleaning up after ourselves. If we  
        // don't then we will continue to run our ngOnInit()   
        // method on every navigationEnd event.
        if (this.navigationSubscription) {
            this.navigationSubscription.unsubscribe();
        }
        clearInterval( this.interval );
        clearInterval( this.runningLogInterval );
        clearInterval( this.resultPopupInterval );
        this.sharedService.stopDuoConnectionCheck();
    }
    resetRunTestForm() {
        setTimeout(() => {
            this.runTestForm.nativeElement.reset();
        }, 0);
        // remove validation messages
        validator.removeFormValidation("bluePrintFormWrapper", this.validationData);
    }
    /*
   * Used to display the table data on load/by default
   * @param : repairStation,userName,reflect (edit/delete)
   * @retun : null
   */

    getRunTest() {
        this.showLoader = true;
        $("#dataWrapper").find(".scrollBody").scrollLeft(0);
        this.runtestService.getRunTest(this.searchStatus, this.searchCriteria, this.paginationDetails, this.sharedService.createServiceToken(), this.commissionType, this.showMySites).subscribe(
            data => {
                let jsonStatue = data.json();
                if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
                    if(!this.sessionExpiredModalBlock) {
                        this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, {
                            keyboard: false,
                            backdrop: 'static',
                            size: 'lg',
                            windowClass: 'session-modal'
                        });
                    }

                } else {

                    if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {

                        if (jsonStatue.status == "SUCCESS") {

                            setTimeout(() => {
                                this.showLoader = false;
                                this.tableData = jsonStatue;
                                this.totalPages = this.tableData.pageCount;
                                this.smVersion = this.tableData.smVersion;
                                this.ciqList = this.tableData.getCiqList;
                                this.networkType = JSON.parse(sessionStorage.getItem("selectedProgram")).networkTypeDetailsEntity.networkType;
                                this.programName = JSON.parse(sessionStorage.getItem("selectedProgram")).programName;
                                if(this.ciqList.length >0){
                                    // this.ciqDetails = this.ciqList[0];
                                    let getSelectedCIQ = JSON.parse(sessionStorage.getItem("selectedCIQ"));
                                    if (getSelectedCIQ) {
                                        this.ciqDetails = getSelectedCIQ;
                                    }
                                    else {
                                        this.ciqDetails = this.ciqList[0];
                                        // Update Session storage for selectedCIQ
                                        this.sharedService.updateSelectedCIQInSessionStorage(this.ciqDetails);
                                    }
                                    this.getNEList(this.ciqDetails);
                                }
                                this.getUseCase();
                                this.getNeGrowUseCase();
                                // this.getPostMigUseCase(this.tableData.postmigusecaselist);
                                // this.getPostMigUseCases(this.tableData.postmigusecaselist);

                                if(this.programName =="VZN-4G-FSU")
                                     this.selectedNeVersion="19.A.0"
                                 if(this.programName =="VZN-5G-DSS")
                                      this.disableDropdownNG = false;
                                  else
                                      this.disableDropdownNG = false;

                                if(this.programName =="VZN-4G-FSU")
                                    this.disableDropdown = false;
                                  else
                                    this.disableDropdown = false;
                                this.migrationStrategy = this.programName == 'VZN-4G-USM-LIVE' ? 'Legacy IP' : null;
                                this.ovUpdate = this.programName == 'VZN-4G-USM-LIVE' ? true : false;
                                this.selectedNEItems = null;
                                this.programType = this.tableData.programType;
                                //this.selectedItems =[];
                                this.ckeckedOrNot = true;
                                this.rfScriptFlag = true;
                                this.isItInProgress=this.tableData.isInProgress;
                                let pageCount = [];
                                for (var i = 1; i <= this.tableData.pageCount; i++) {
                                    pageCount.push(i);
                                }
                                this.pageRenge = pageCount;
                                this.pager = this.getPager(this.totalPages * this.pageSize, this.currentPage, this.pageRenge);

                                // To display table data
                                if (this.tableData.runTestTableDetails.length != 0) {

                                    this.showNoDataFound = false;
                                    this.tableShowHide = true;

                                    setTimeout(() => {
                                        let tableWidth = document.getElementById('runTestScrollHead').scrollWidth;
                                        $(".runTestWrapper .scrollBody table#runTestScrollBody").css("min-width", (tableWidth) + "px");
                                        $(".runTestWrapper .scrollHead table#runTestScrollHead").css("width", tableWidth + "px");
            
            
                                        $(".runTestWrapper .scrollBody").on("scroll", function (event) {
                                           // $(".formEditRow #formWrapper").css("margin-left", (event.target.scrollLeft - 5) + "px");
                                            $(".runTestRow .form-control-fixed").css("right", (event.target.scrollLeft * -1) + "px");
                                            $(".runTestWrapper .scrollHead table#runTestScrollHead").css("margin-left", (event.target.scrollLeft * -1) + "px");
                                        });
                                        

                                        if (this.expandSerchRow == false) {
                                            setTimeout(() => {
                                                $(".runTestWrapper .scrollBody").css("max-height", (this.tableDataHeight - $("#tabContentWrapper").height()) + "px");
                                            }, 0);
                                        } else {
                                            setTimeout(() => {
                                                $(".runTestWrapper .scrollBody").css("max-height", (this.tableDataHeight - $("#tabContentWrapper").height()) + "px");
                                            }, 0);
                                        }
                                        

                                    }, 0);
                                } else {
                                    this.tableShowHide = false;
                                    this.showNoDataFound = true;
                                }

                                if(this.isItInProgress)
                                {
                                    // console.log("here");
                                    //this.count=0;
         
                                    // On every 10s refresh the table
                                    if(!this.interval) {
                                    //    console.log("here inside");

                                        this.interval = setInterval( () => {
                                            this.updateRunTestTable();
                                        }, 10000 );
                                    }
                
                                }
                                else
                                {
                                    clearInterval( this.interval );
                                    this.interval=null;

                                    //console.log("there");
                
                                }
                                this.multipleDuo = (this.programName == 'VZN-4G-USM-LIVE' || this.programName == 'VZN-5G-MM') ? true : false;
                            }, 1000);

                        } else {
                            this.showLoader = false;
                            this.displayModel(jsonStatue.reason, "failureIcon");
                        }
                    }
                }
            },
            error => {
                //Please Comment while checkIn  

                /* this.showLoader = true;
                setTimeout(() => {

                    this.showLoader = false;
                    this.networkType = JSON.parse(sessionStorage.getItem("selectedProgram")).networkTypeDetailsEntity.networkType;
                    this.programName = JSON.parse(sessionStorage.getItem("selectedProgram")).programName;

                    //NoData
                    // this.tableData = JSON.parse('{"sessionId":"19c4c64","serviceToken":"53531","status":"SUCCESS","fromDate":"03/08/2019","pageCount":1,"isInProgress":true,"smVersion":[{"name":"20.A.0","id":41,"smNameList":[{"name":"Boston-5G","id":98},{"name":"5G_NE","id":105},{"name":"Nola NE","id":106},{"name":"New Engd","id":107},{"name":"Pensacola_NE","id":109}]},{"name":"20.B.0","id":42,"smNameList":[{"name":"Sacremento NE","id":99},{"name":"Test_5G","id":100},{"name":"New_England_SM","id":108},{"name":"Pensa NE","id":110}]}],"toDate":"03/15/2019","useCaseList":[{"useCaseName":"AU_Checks_Usecase_10012020","useCaseId":42,"ucSleepInterval":"1","executionSequence":1,"scripts":[{"scriptId":31,"scriptSleepInterval":"1","scriptName":"pre_check.sh","scriptExeSequence":1,"useGeneratedScript":"NO"}]},{"useCaseName":"RF_Use_Case","useCaseId":43,"executionSequence":1,"scripts":[{"scriptId":31,"scriptName":"pre_check.sh","scriptExeSequence":1,"scriptSleepInterval":"1","useGeneratedScript":"YES"}],"ucSleepInterval":"1"}],"getCiqList":[{"id":49,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T16:24:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T16:41:30.000+0000","createdBy":"superadmin"},"ciqFileName":"UNY-NE-VZ_CIQ_Ver2.82_03122019.xlsx","ciqFilePath":"/home/user/RCT/rctsoftware/Samsung/SMART/Customer/23/PreMigration/Input/UNY-NE-VZ_CIQ_Ver2.82_03122019/CIQ/","scriptFileName":"1_58154_LEXINGTON_12_MA.zip,1_6003_networdata123.zip,1_6013_networkcohyutrr.zip,1_6203_networkconfig.zip","scriptFilePath":"/home/user/RCT/rctsoftware/Samsung/SMART/Customer/23/PreMigration/Input/UNY-NE-VZ_CIQ_Ver2.82_03122019/SCRIPT/","checklistFileName":"","checklistFilePath":"","ciqVersion":"ORIGINAL","fileSourceType":"FETCH","uploadBy":"superadmin","remarks":"","creationDate":"2019-03-12T20:38:02.000+0000"}],"runTestTableDetails":[]}');
                    //Data
                    this.tableData = this.programName == 'VZN-5G-MM' ? 
                        JSON.parse('{"pageCount":1,"toDate":"08/02/2022","sessionId":"19b14231","getCiqList":[{"id":541,"programDetailsEntity":{"id":40,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-MM","programDescription":"MM","status":"Active","creationDate":"2021-07-27T14:14:37.000+0000","createdBy":"superadmin","sourceProgramId":34,"sourceprogramName":"VZN-4G-USM-LIVE"},"ciqFileName":"VZW_WBV_mmWave_SNAP_CIQ_062422_v17.7.xlsx","ciqFilePath":"Customer/40/PreMigration/Input/VZW_WBV_mmWave_SNAP_CIQ_062422_v17.7/CIQ/","scriptFileName":"10701550083.zip,10701550082.zip,10701590030.zip","scriptFilePath":"Customer/40/PreMigration/Input/VZW_WBV_mmWave_SNAP_CIQ_062422_v17.7/SCRIPT/","checklistFileName":"Verizon_USM_Migration Checklist_1.1.5.xlsx","checklistFilePath":"Customer/40/PreMigration/Input/VZW_WBV_mmWave_SNAP_CIQ_062422_v17.7/CHECKLIST/","ciqVersion":"ORIGINAL","fileSourceType":"FETCH_20220725_16:34:53","uploadBy":"superadmin","remarks":"","creationDate":"2022-08-02T07:34:24.000+0000","fetchInfo":null},{"id":506,"programDetailsEntity":{"id":40,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-MM","programDescription":"MM","status":"Active","creationDate":"2021-07-27T14:14:37.000+0000","createdBy":"superadmin","sourceProgramId":34,"sourceprogramName":"VZN-4G-USM-LIVE"},"ciqFileName":"VZW_NYM_mmWave_SNAP_CIQ_041322_v11.3.xlsx","ciqFilePath":"Customer/40/PreMigration/Input/VZW_NYM_mmWave_SNAP_CIQ_041322_v11.3/CIQ/","scriptFileName":"8203010059.zip,8002000004.zip,8002000005.zip","scriptFilePath":"Customer/40/PreMigration/Input/VZW_NYM_mmWave_SNAP_CIQ_041322_v11.3/SCRIPT/","checklistFileName":"Verizon_USM_Migration Checklist_1.1.5.xlsx","checklistFilePath":"Customer/40/PreMigration/Input/VZW_NYM_mmWave_SNAP_CIQ_041322_v11.3/CHECKLIST/","ciqVersion":"ORIGINAL","fileSourceType":"UPLOAD","uploadBy":"superadmin","remarks":"","creationDate":"2022-08-02T08:07:09.000+0000","fetchInfo":null}],"postmigusecaselist":[{"useCaseName":"ACPF","useCaseId":10312,"ucSleepInterval":"1000","executionSequence":12,"scripts":[{"scriptId":35792,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"ACPF_E1_F1_X2_check.xml","scriptExeSequence":13332}]},{"useCaseName":"ACPF_test","useCaseId":10313,"ucSleepInterval":"1000","executionSequence":14,"scripts":[{"scriptId":35794,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"ACPF_Get_CSL_Serv_Settings.xml","scriptExeSequence":14323}]},{"useCaseName":"5gendc","useCaseId":10450,"ucSleepInterval":"1000","executionSequence":17,"scripts":[{"scriptId":36457,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"eNB_ENDCAudit20A0SWVer.xml","scriptExeSequence":12346}]},{"useCaseName":"5gENDC20b","useCaseId":10674,"ucSleepInterval":"1000","executionSequence":18,"scripts":[{"scriptId":39177,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"eNB_ENDCAudit20B0SWVer.xml","scriptExeSequence":13425}]},{"useCaseName":"ACPFA1A2","useCaseId":11496,"ucSleepInterval":"1000","executionSequence":321,"scripts":[{"scriptId":43590,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"ACPF_A1_A2_A3_Incl_NR_UL_checks.xml","scriptExeSequence":2324}]},{"useCaseName":"Acpfap3","useCaseId":12794,"ucSleepInterval":"1000","executionSequence":542,"scripts":[{"scriptId":46026,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"ACPF_20AP3Checks.xml","scriptExeSequence":32121},{"scriptId":43590,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"ACPF_A1_A2_A3_Incl_NR_UL_checks.xml","scriptExeSequence":38439}]},{"useCaseName":"Acpf20C","useCaseId":16714,"ucSleepInterval":"1000","executionSequence":299,"scripts":[{"scriptId":51899,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"ACPF_20C_Audit.xml","scriptExeSequence":136}]},{"useCaseName":"AU_20C","useCaseId":16715,"ucSleepInterval":"1000","executionSequence":256,"scripts":[{"scriptId":51900,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"AU_20C_Param_Check.xml","scriptExeSequence":342}]},{"useCaseName":"ACPF_E1F1X2_20C","useCaseId":16720,"ucSleepInterval":"1000","executionSequence":1321,"scripts":[{"scriptId":51910,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"ACPF_E1_F1_X2_20C_check.xml","scriptExeSequence":123}]},{"useCaseName":"ACPF_a1a2a3_20C","useCaseId":16721,"ucSleepInterval":"1000","executionSequence":3213,"scripts":[{"scriptId":51909,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"ACPF_A1_A2_A3_20C_checks.xml","scriptExeSequence":1397}]},{"useCaseName":"Endc_20C","useCaseId":17139,"ucSleepInterval":"1000","executionSequence":1454,"scripts":[{"scriptId":52805,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"eNB_ENDCAudit20C0SWVer.xml","scriptExeSequence":13221}]},{"useCaseName":"Test123","useCaseId":17140,"ucSleepInterval":"1000","executionSequence":1231,"scripts":[{"scriptId":51900,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"AU_20C_Param_Check.xml","scriptExeSequence":1389}]},{"useCaseName":"20C_ACPF","useCaseId":17201,"ucSleepInterval":"1000","executionSequence":101,"scripts":[{"scriptId":51899,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"ACPF_20C_Audit.xml","scriptExeSequence":201}]},{"useCaseName":"AU_20C_Processor","useCaseId":25048,"ucSleepInterval":"1000","executionSequence":2101,"scripts":[{"scriptId":69848,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"AU_20C_Processor_Entries.xml","scriptExeSequence":650}]},{"useCaseName":"IAU","useCaseId":28161,"ucSleepInterval":"1000","executionSequence":345,"scripts":[{"scriptId":76837,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"IAU_Param_Check.xml","scriptExeSequence":205}]},{"useCaseName":"ACPF_E1_F1","useCaseId":28192,"ucSleepInterval":"1000","executionSequence":259,"scripts":[{"scriptId":51910,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"ACPF_E1_F1_X2_20C_check.xml","scriptExeSequence":206}]},{"useCaseName":"TWAMP","useCaseId":33734,"ucSleepInterval":"1000","executionSequence":196,"scripts":[{"scriptId":108032,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"AU_DIAGNOSTIC.xml","scriptExeSequence":208},{"scriptId":108031,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"AU_TWAMP_F1C_LINK.xml","scriptExeSequence":207}]},{"useCaseName":"IAU_New_dec28","useCaseId":37391,"ucSleepInterval":"1000","executionSequence":309,"scripts":[{"scriptId":76837,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"IAU_Param_Check.xml","scriptExeSequence":205}]}],"fromDate":"07/26/2022","migusecaselist":[],"smVersion":[{"name":"20.A.0","id":41,"smNameList":[{"name":"Aathi5G","useCaseList":[{"useCaseName":"5gendc","useCaseId":10450,"executionSequence":17},{"useCaseName":"5gENDC20b","useCaseId":10674,"executionSequence":18},{"useCaseName":"ACPFA1A2","useCaseId":11496,"executionSequence":321},{"useCaseName":"Acpfap3","useCaseId":12794,"executionSequence":542},{"useCaseName":"Acpf20C","useCaseId":16714,"executionSequence":299},{"useCaseName":"AU_20C","useCaseId":16715,"executionSequence":256},{"useCaseName":"ACPF_E1F1X2_20C","useCaseId":16720,"executionSequence":1321},{"useCaseName":"ACPF_a1a2a3_20C","useCaseId":16721,"executionSequence":3213},{"useCaseName":"Endc_20C","useCaseId":17139,"executionSequence":1454},{"useCaseName":"20C_ACPF","useCaseId":17201,"executionSequence":101},{"useCaseName":"AU_20C_Processor","useCaseId":25048,"executionSequence":2101},{"useCaseName":"IAU","useCaseId":28161,"executionSequence":345},{"useCaseName":"ACPF_E1_F1","useCaseId":28192,"executionSequence":259},{"useCaseName":"IAU_New_dec28","useCaseId":37391,"executionSequence":309},{"useCaseName":"TWAMP","useCaseId":33734,"executionSequence":196}],"id":99},{"name":"5G_MM","id":109}]},{"name":"20.C.0","id":46,"smNameList":[{"name":"NewYork","useCaseList":[{"useCaseName":"5gendc","useCaseId":10450,"executionSequence":17},{"useCaseName":"5gENDC20b","useCaseId":10674,"executionSequence":18},{"useCaseName":"ACPFA1A2","useCaseId":11496,"executionSequence":321},{"useCaseName":"Acpfap3","useCaseId":12794,"executionSequence":542},{"useCaseName":"Acpf20C","useCaseId":16714,"executionSequence":299},{"useCaseName":"AU_20C","useCaseId":16715,"executionSequence":256},{"useCaseName":"ACPF_E1F1X2_20C","useCaseId":16720,"executionSequence":1321},{"useCaseName":"ACPF_a1a2a3_20C","useCaseId":16721,"executionSequence":3213},{"useCaseName":"Endc_20C","useCaseId":17139,"executionSequence":1454},{"useCaseName":"20C_ACPF","useCaseId":17201,"executionSequence":101},{"useCaseName":"AU_20C_Processor","useCaseId":25048,"executionSequence":2101},{"useCaseName":"IAU","useCaseId":28161,"executionSequence":345},{"useCaseName":"ACPF_E1_F1","useCaseId":28192,"executionSequence":259},{"useCaseName":"IAU_New_dec28","useCaseId":37391,"executionSequence":309},{"useCaseName":"TWAMP","useCaseId":33734,"executionSequence":196}],"id":106},{"name":"ENG","id":107},{"name":"5G_MM","useCaseList":[{"useCaseName":"5gendc","useCaseId":10450,"executionSequence":17},{"useCaseName":"5gENDC20b","useCaseId":10674,"executionSequence":18},{"useCaseName":"ACPFA1A2","useCaseId":11496,"executionSequence":321},{"useCaseName":"Acpfap3","useCaseId":12794,"executionSequence":542},{"useCaseName":"Acpf20C","useCaseId":16714,"executionSequence":299},{"useCaseName":"AU_20C","useCaseId":16715,"executionSequence":256},{"useCaseName":"ACPF_E1F1X2_20C","useCaseId":16720,"executionSequence":1321},{"useCaseName":"ACPF_a1a2a3_20C","useCaseId":16721,"executionSequence":3213},{"useCaseName":"Endc_20C","useCaseId":17139,"executionSequence":1454},{"useCaseName":"20C_ACPF","useCaseId":17201,"executionSequence":101},{"useCaseName":"AU_20C_Processor","useCaseId":25048,"executionSequence":2101},{"useCaseName":"IAU","useCaseId":28161,"executionSequence":345},{"useCaseName":"ACPF_E1_F1","useCaseId":28192,"executionSequence":259},{"useCaseName":"IAU_New_dec28","useCaseId":37391,"executionSequence":309},{"useCaseName":"TWAMP","useCaseId":33734,"executionSequence":196}],"id":108}]},{"name":"21.A.0","id":48,"smNameList":[{"name":"5G_MM_2","useCaseList":[{"useCaseName":"5gendc","useCaseId":10450,"executionSequence":17},{"useCaseName":"5gENDC20b","useCaseId":10674,"executionSequence":18},{"useCaseName":"ACPFA1A2","useCaseId":11496,"executionSequence":321},{"useCaseName":"Acpfap3","useCaseId":12794,"executionSequence":542},{"useCaseName":"Acpf20C","useCaseId":16714,"executionSequence":299},{"useCaseName":"AU_20C","useCaseId":16715,"executionSequence":256},{"useCaseName":"ACPF_E1F1X2_20C","useCaseId":16720,"executionSequence":1321},{"useCaseName":"ACPF_a1a2a3_20C","useCaseId":16721,"executionSequence":3213},{"useCaseName":"Endc_20C","useCaseId":17139,"executionSequence":1454},{"useCaseName":"20C_ACPF","useCaseId":17201,"executionSequence":101},{"useCaseName":"AU_20C_Processor","useCaseId":25048,"executionSequence":2101},{"useCaseName":"IAU","useCaseId":28161,"executionSequence":345},{"useCaseName":"ACPF_E1_F1","useCaseId":28192,"executionSequence":259},{"useCaseName":"IAU_New_dec28","useCaseId":37391,"executionSequence":309},{"useCaseName":"TWAMP","useCaseId":33734,"executionSequence":196}],"id":115},{"name":"5G_MM","id":122},{"name":"5G_MM_3","id":125},{"name":"5G_MM_test","id":133},{"name":"5G_MM_Sacra","id":135},{"name":"5G_MM_A","id":138}]},{"name":"21.B.0","id":54,"smNameList":[{"name":"5G_MM_Sacro","id":134},{"name":"5G_MM_OV","id":137},{"name":"5G_MM_CTX","id":150}]},{"name":"21.C.0","id":59,"smNameList":[{"name":"5G_MM_21C","id":147}]},{"name":"21.D.0","id":63,"smNameList":[{"name":"5G_MM_5","id":128},{"name":"5G_IAU_21D","id":139}]}],"ranAtpUseCaseList":[],"negrowuseCaseLst":[],"serviceToken":"57414","runTestTableDetails":[{"customerDetailsEntity":null,"enbId":"11706500011","neName":"11706500011_5GDU_Battlefield_Shopping_Center_SC-CHNT","ciqName":"VZW_WBV_mmWave_SNAP_CIQ_062422_v17.7.xlsx","testName":"WFM_11706500011_5GDU_Battlefield_Shopping_Center_SC-CHNT_08022022_13_14_14","testDescription":"","lsmName":null,"lsmVersion":null,"useCase":null,"status":"Completed","userName":"superadmin","creationDate":"2022-08-02 13:14:14","customerId":0,"id":2931,"result":null,"fromDate":null,"toDate":null,"migrationRunTestModel":null,"negrowRunTestModel":null,"ranAtpRunTestModel":null,"preMigStatus":"Failure","neGrowStatus":"CannotStart","inputRequired":"NEGROW","preMigrationScriptPath":null,"preMigrationFileName":null,"filePathPre":null,"fileNamePre":null,"commPath":null,"envPath":null,"csvPath":null,"commZipName":null,"envZipName":null,"csvZipName":null,"preErrorFile":"/home/user/Samsung/SMART//ErrorLog/pre011706500011_08022022_13_14_14.log","neGrowErrorFile":"/home/user/Samsung/SMART//ErrorLog/negrow011706500011_08022022_13_14_14.log","migErrorFile":"/home/user/Samsung/SMART//ErrorLog/mig011706500011_08022022_13_14_14.log","siteName":"Battlefield_Shopping_Center_SC","siteReportStatus":"Partial Save","siteReportId":null,"migStatus":"CannotStart","postErrorFile":"/home/user/Samsung/SMART//ErrorLog/post011706500011_08022022_13_14_14.log","postMigStatus":"CannotStart","postMigrationRunTestModel":null},{"customerDetailsEntity":null,"enbId":"10701550082","neName":"10701550082_5GDU_WDC_Douglas_57_ADLP-ANJT","ciqName":"VZW_WBV_mmWave_SNAP_CIQ_062422_v17.7.xlsx","testName":"WFM_10701550082_5GDU_WDC_Douglas_57_ADLP-ANJT_08022022_13_04_47","testDescription":"","lsmName":"5G_IAU_21D","lsmVersion":"21.D.0","useCase":null,"status":"Completed","userName":"superadmin","creationDate":"2022-08-02 13:04:47","customerId":0,"id":2928,"result":null,"fromDate":null,"toDate":null,"migrationRunTestModel":{"customerDetailsEntity":null,"migrationType":"Migration","failedScript":"false","migrationSubType":"PreCheck","checklistFileName":"Verizon_USM_Migration Checklist_1.1.5.xlsx","neName":"10701550082_5GDU_WDC_Douglas_57_ADLP-ANJT","progressStatus":"Completed","ciqName":"VZW_WBV_mmWave_SNAP_CIQ_062422_v17.7.xlsx","outputFilepath":"Customer/40/Migration/10701550082/PreCheck/Output/97521_output.txt","testName":"WFM_10701550082_5GDU_WDC_Douglas_57_ADLP-ANJT_08022022_13_05_07","testDescription":"Ran from WFM","lsmName":"5G_IAU_21D","lsmVersion":"21.D.0","useCase":"RF_Scripts_Usecase10701550082_08022022,","status":"Success","userName":"superadmin","creationDate":"2022-08-02 13:05:34","useCaseDetails":null,"customerId":0,"id":6777,"wfmid":0,"useCaseSequence":0,"result":"","resultFilePath":"","fromDate":null,"toDate":null,"generateScriptPath":"Customer/40/Migration/10701550082/PreCheck/GenerateScript/6777/3000_5GIAU21D_RFScriptsUsecase1070155008208022022_44800_3000107015500ACPF0acpf-cell-report-config-20220610-1357RFUsecase1070155008208022022_163384.sh,Customer/40/Migration/10701550082/PreCheck/GenerateScript/6777/3100_5GIAU21D_RFScriptsUsecase1070155008208022022_44800_3100107015500ACPF0IntraNRANR-20220610-1357RFUsecase1070155008208022022_163385.sh","migStatusDesc":null},"negrowRunTestModel":{"customerDetailsEntity":null,"migrationType":"PreMigration","failedScript":"false","migrationSubType":"NEGrow","checklistFileName":"Verizon_USM_Migration Checklist_1.1.5.xlsx","neName":"10701550082_5GDU_WDC_Douglas_57_ADLP-ANJT","progressStatus":"Completed","ciqName":"VZW_WBV_mmWave_SNAP_CIQ_062422_v17.7.xlsx","outputFilepath":"Customer/40/PreMigration/10701550082/NEGrow/Output/97521_output.txt","testName":"WFM_10701550082_5GDU_WDC_Douglas_57_ADLP-ANJT_08022022_13_04_50","testDescription":"Ran from WFM","lsmName":"5G_IAU_21D","lsmVersion":"21.D.0","useCase":"pnp21DUsecase_10701550082_08022022,","status":"Success","userName":"superadmin","creationDate":"2022-08-02 13:05:00","useCaseDetails":null,"customerId":0,"id":6775,"wfmid":0,"useCaseSequence":0,"result":"","resultFilePath":"","fromDate":null,"toDate":null,"generateScriptPath":"Customer/40/PreMigration/10701550082/NEGrow/GenerateScript/6775/15000_5GIAU21D_pnp21DUsecase1070155008208022022_44803_CSVpnp21DmacroindoordistGEN2107015500825GDUWDCDouglas57ADLP-ANJT08022022_163388.sh","migStatusDesc":null},"ranAtpRunTestModel":null,"preMigStatus":"Completed","neGrowStatus":"Completed","inputRequired":"PMIG","preMigrationScriptPath":null,"preMigrationFileName":null,"filePathPre":"/home/user/Samsung/SMART/Customer/40/PreMigration/Output/VZW_WBV_mmWave_SNAP_CIQ_062422_v17.7/AU/ALL/21D/10701550082/","fileNamePre":"ALL_10701550082_5GDU_WDC_Douglas_57_ADLP-ANJT_08022022_13_04_50.zip","commPath":null,"envPath":null,"csvPath":null,"commZipName":null,"envZipName":null,"csvZipName":null,"preErrorFile":"/home/user/Samsung/SMART//ErrorLog/pre010701550082_08022022_13_04_47.log","neGrowErrorFile":"/home/user/Samsung/SMART//ErrorLog/negrow010701550082_08022022_13_04_47.log","migErrorFile":"/home/user/Samsung/SMART//ErrorLog/mig010701550082_08022022_13_04_47.log","siteName":"WDC_Douglas_57","siteReportStatus":"Partial Save","siteReportId":null,"migStatus":"Completed","postErrorFile":"/home/user/Samsung/SMART//ErrorLog/post010701550082_08022022_13_04_47.log","postMigStatus":"InputsRequired","postMigrationRunTestModel":null},{"customerDetailsEntity":null,"enbId":"10701550083","neName":"10701550083_5GDU_WDC_Douglas_57_ADLP-ANJT","ciqName":"VZW_WBV_mmWave_SNAP_CIQ_062422_v17.7.xlsx","testName":"WFM_10701550083_5GDU_WDC_Douglas_57_ADLP-ANJT_08022022_13_04_47","testDescription":"","lsmName":"5G_IAU_21D","lsmVersion":"21.D.0","useCase":null,"status":"Completed","userName":"superadmin","creationDate":"2022-08-02 13:04:47","customerId":0,"id":2929,"result":null,"fromDate":null,"toDate":null,"migrationRunTestModel":{"customerDetailsEntity":null,"migrationType":"Migration","failedScript":"false","migrationSubType":"PreCheck","checklistFileName":"Verizon_USM_Migration Checklist_1.1.5.xlsx","neName":"10701550083_5GDU_WDC_Douglas_57_ADLP-ANJT","progressStatus":"Completed","ciqName":"VZW_WBV_mmWave_SNAP_CIQ_062422_v17.7.xlsx","outputFilepath":"Customer/40/Migration/10701550083/PreCheck/Output/97521_output.txt","testName":"WFM_10701550083_5GDU_WDC_Douglas_57_ADLP-ANJT_08022022_13_05_10","testDescription":"Ran from WFM","lsmName":"5G_IAU_21D","lsmVersion":"21.D.0","useCase":"RF_Scripts_Usecase10701550083_08022022,","status":"Success","userName":"superadmin","creationDate":"2022-08-02 13:06:16","useCaseDetails":null,"customerId":0,"id":6778,"wfmid":0,"useCaseSequence":0,"result":"","resultFilePath":"","fromDate":null,"toDate":null,"generateScriptPath":"Customer/40/Migration/10701550083/PreCheck/GenerateScript/6778/3000_5GIAU21D_RFScriptsUsecase1070155008308022022_44804_3000107015500ACPF0acpf-cell-report-config-20220610-1357RFUsecase1070155008308022022_163392.sh,Customer/40/Migration/10701550083/PreCheck/GenerateScript/6778/3100_5GIAU21D_RFScriptsUsecase1070155008308022022_44804_3100107015500ACPF0IntraNRANR-20220610-1357RFUsecase1070155008308022022_163390.sh,Customer/40/Migration/10701550083/PreCheck/GenerateScript/6778/4000_5GIAU21D_RFScriptsUsecase1070155008308022022_44804_40001550083AU0csl-tce-server-20220610-1357RFUsecase1070155008308022022_163396.sh,Customer/40/Migration/10701550083/PreCheck/GenerateScript/6778/4100_5GIAU21D_RFScriptsUsecase1070155008308022022_44804_41001550083AU0electrical-tilt-20220610-1357RFUsecase1070155008308022022_163391.sh,Customer/40/Migration/10701550083/PreCheck/GenerateScript/6778/4200_5GIAU21D_RFScriptsUsecase1070155008308022022_44804_42001550083AU0ipv6-route-20220610-1357RFUsecase1070155008308022022_163399.sh,Customer/40/Migration/10701550083/PreCheck/GenerateScript/6778/4300_5GIAU21D_RFScriptsUsecase1070155008308022022_44804_43001550083AU0dl-mimo-20220610-1357RFUsecase1070155008308022022_163395.sh,Customer/40/Migration/10701550083/PreCheck/GenerateScript/6778/4400_5GIAU21D_RFScriptsUsecase1070155008308022022_44804_44001550083AU0au-qci-20220610-1357RFUsecase1070155008308022022_163393.sh,Customer/40/Migration/10701550083/PreCheck/GenerateScript/6778/4600_5GIAU21D_RFScriptsUsecase1070155008308022022_44804_46001550083AU0au-cell-20220610-1357RFUsecase1070155008308022022_163394.sh,Customer/40/Migration/10701550083/PreCheck/GenerateScript/6778/4700_5GIAU21D_RFScriptsUsecase1070155008308022022_44804_47001550083AU0op-mode-20220610-1357RFUsecase1070155008308022022_163398.sh,Customer/40/Migration/10701550083/PreCheck/GenerateScript/6778/4800_5GIAU21D_RFScriptsUsecase1070155008308022022_44804_48001550083AU0auto-fusing-flag-20220610-1357RFUsecase1070155008308022022_163389.sh,Customer/40/Migration/10701550083/PreCheck/GenerateScript/6778/5200_5GIAU21D_RFScriptsUsecase1070155008308022022_44804_5200107015500ACPF0nr-intra-nbr-20220610-1357RFUsecase1070155008308022022_163397.sh","migStatusDesc":null},"negrowRunTestModel":{"customerDetailsEntity":null,"migrationType":"PreMigration","failedScript":"false","migrationSubType":"NEGrow","checklistFileName":"Verizon_USM_Migration Checklist_1.1.5.xlsx","neName":"10701550083_5GDU_WDC_Douglas_57_ADLP-ANJT","progressStatus":"Completed","ciqName":"VZW_WBV_mmWave_SNAP_CIQ_062422_v17.7.xlsx","outputFilepath":"Customer/40/PreMigration/10701550083/NEGrow/Output/97521_output.txt","testName":"WFM_10701550083_5GDU_WDC_Douglas_57_ADLP-ANJT_08022022_13_04_55","testDescription":"Ran from WFM","lsmName":"5G_IAU_21D","lsmVersion":"21.D.0","useCase":"pnp21DUsecase_10701550083_08022022,","status":"Success","userName":"superadmin","creationDate":"2022-08-02 13:05:03","useCaseDetails":null,"customerId":0,"id":6776,"wfmid":0,"useCaseSequence":0,"result":"","resultFilePath":"","fromDate":null,"toDate":null,"generateScriptPath":"Customer/40/PreMigration/10701550083/NEGrow/GenerateScript/6776/15000_5GIAU21D_pnp21DUsecase1070155008308022022_44807_CSVpnp21DmacroindoordistGEN2107015500835GDUWDCDouglas57ADLP-ANJT08022022_163402.sh","migStatusDesc":null},"ranAtpRunTestModel":null,"preMigStatus":"Completed","neGrowStatus":"Completed","inputRequired":"PMIG","preMigrationScriptPath":null,"preMigrationFileName":null,"filePathPre":"/home/user/Samsung/SMART/Customer/40/PreMigration/Output/VZW_WBV_mmWave_SNAP_CIQ_062422_v17.7/AU/ALL/21D/10701550083/","fileNamePre":"ALL_10701550083_5GDU_WDC_Douglas_57_ADLP-ANJT_08022022_13_04_55.zip","commPath":null,"envPath":null,"csvPath":null,"commZipName":null,"envZipName":null,"csvZipName":null,"preErrorFile":"/home/user/Samsung/SMART//ErrorLog/pre010701550083_08022022_13_04_47.log","neGrowErrorFile":"/home/user/Samsung/SMART//ErrorLog/negrow010701550083_08022022_13_04_47.log","migErrorFile":"/home/user/Samsung/SMART//ErrorLog/mig010701550083_08022022_13_04_47.log","siteName":"WDC_Douglas_57","siteReportStatus":"NotExecuted","siteReportId":null,"migStatus":"Completed","postErrorFile":"/home/user/Samsung/SMART//ErrorLog/post010701550083_08022022_13_04_47.log","postMigStatus":"InputsRequired","postMigrationRunTestModel":null},{"customerDetailsEntity":null,"enbId":"10701550082","neName":"10701550082_5GDU_WDC_Douglas_57_ADLP-ANJT","ciqName":"VZW_WBV_mmWave_SNAP_CIQ_062422_v17.7.xlsx","testName":"WFM_10701550082_5GDU_WDC_Douglas_57_ADLP-ANJT_08012022_18_22_00","testDescription":"","lsmName":"5G_IAU_21D","lsmVersion":"21.D.0","useCase":null,"status":"Completed","userName":"superadmin","creationDate":"2022-08-01 18:22:00","customerId":0,"id":2923,"result":null,"fromDate":null,"toDate":null,"migrationRunTestModel":{"customerDetailsEntity":null,"migrationType":"Migration","failedScript":"false","migrationSubType":"PreCheck","checklistFileName":"Verizon_USM_Migration Checklist_1.1.5.xlsx","neName":"10701550082_5GDU_WDC_Douglas_57_ADLP-ANJT","progressStatus":"Completed","ciqName":"VZW_WBV_mmWave_SNAP_CIQ_062422_v17.7.xlsx","outputFilepath":"Customer/40/Migration/10701550082/PreCheck/Output/92290_output.txt","testName":"WFM_10701550082_5GDU_WDC_Douglas_57_ADLP-ANJT_08012022_18_23_19","testDescription":"Ran from WFM","lsmName":"5G_IAU_21D","lsmVersion":"21.D.0","useCase":"RF_Scripts_Usecase10701550082_08012022,","status":"Success","userName":"superadmin","creationDate":"2022-08-01 18:26:17","useCaseDetails":null,"customerId":0,"id":6768,"wfmid":0,"useCaseSequence":0,"result":"","resultFilePath":"","fromDate":null,"toDate":null,"generateScriptPath":"Customer/40/Migration/10701550082/PreCheck/GenerateScript/6768/3000_5GIAU21D_RFScriptsUsecase1070155008208012022_44782_3000107015500ACPF0acpf-cell-report-config-20220610-1357RFUsecase1070155008208012022_163341.sh,Customer/40/Migration/10701550082/PreCheck/GenerateScript/6768/3100_5GIAU21D_RFScriptsUsecase1070155008208012022_44782_3100107015500ACPF0IntraNRANR-20220610-1357RFUsecase1070155008208012022_163340.sh","migStatusDesc":null},"negrowRunTestModel":{"customerDetailsEntity":null,"migrationType":"PreMigration","failedScript":"false","migrationSubType":"NEGrow","checklistFileName":"Verizon_USM_Migration Checklist_1.1.5.xlsx","neName":"10701550082_5GDU_WDC_Douglas_57_ADLP-ANJT","progressStatus":"Completed","ciqName":"VZW_WBV_mmWave_SNAP_CIQ_062422_v17.7.xlsx","outputFilepath":"Customer/40/PreMigration/10701550082/NEGrow/Output/92290_output.txt","testName":"WFM_10701550082_5GDU_WDC_Douglas_57_ADLP-ANJT_08012022_18_22_03","testDescription":"Ran from WFM","lsmName":"5G_IAU_21D","lsmVersion":"21.D.0","useCase":"pnp21DUsecase_10701550082_08012022,","status":"Success","userName":"superadmin","creationDate":"2022-08-01 18:23:11","useCaseDetails":null,"customerId":0,"id":6766,"wfmid":0,"useCaseSequence":0,"result":"","resultFilePath":"","fromDate":null,"toDate":null,"generateScriptPath":"Customer/40/PreMigration/10701550082/NEGrow/GenerateScript/6766/15000_5GIAU21D_pnp21DUsecase1070155008208012022_44785_CSVpnp21DmacroindoordistGEN2107015500825GDUWDCDouglas57ADLP-ANJT08012022_163344.sh","migStatusDesc":null},"ranAtpRunTestModel":null,"preMigStatus":"Completed","neGrowStatus":"Completed","inputRequired":"PMIG","preMigrationScriptPath":null,"preMigrationFileName":null,"filePathPre":"/home/user/Samsung/SMART/Customer/40/PreMigration/Output/VZW_WBV_mmWave_SNAP_CIQ_062422_v17.7/AU/ALL/21D/10701550082/","fileNamePre":"ALL_10701550082_5GDU_WDC_Douglas_57_ADLP-ANJT_08012022_18_22_03.zip","commPath":null,"envPath":null,"csvPath":null,"commZipName":null,"envZipName":null,"csvZipName":null,"preErrorFile":"/home/user/Samsung/SMART//ErrorLog/pre010701550082_08012022_18_22_00.log","neGrowErrorFile":"/home/user/Samsung/SMART//ErrorLog/negrow010701550082_08012022_18_22_00.log","migErrorFile":"/home/user/Samsung/SMART//ErrorLog/mig010701550082_08012022_18_22_00.log","siteName":"WDC_Douglas_57","siteReportStatus":"Partial Save","siteReportId":null,"migStatus":"Completed","postErrorFile":"/home/user/Samsung/SMART//ErrorLog/post010701550082_08012022_18_22_00.log","postMigStatus":"InputsRequired","postMigrationRunTestModel":null},{"customerDetailsEntity":null,"enbId":"10701550083","neName":"10701550083_5GDU_WDC_Douglas_57_ADLP-ANJT","ciqName":"VZW_WBV_mmWave_SNAP_CIQ_062422_v17.7.xlsx","testName":"WFM_10701550083_5GDU_WDC_Douglas_57_ADLP-ANJT_08012022_18_22_00","testDescription":"","lsmName":"5G_IAU_21D","lsmVersion":"21.D.0","useCase":null,"status":"Completed","userName":"superadmin","creationDate":"2022-08-01 18:22:00","customerId":0,"id":2924,"result":null,"fromDate":null,"toDate":null,"migrationRunTestModel":{"customerDetailsEntity":null,"migrationType":"Migration","failedScript":"false","migrationSubType":"PreCheck","checklistFileName":"Verizon_USM_Migration Checklist_1.1.5.xlsx","neName":"10701550083_5GDU_WDC_Douglas_57_ADLP-ANJT","progressStatus":"Completed","ciqName":"VZW_WBV_mmWave_SNAP_CIQ_062422_v17.7.xlsx","outputFilepath":"Customer/40/Migration/10701550083/PreCheck/Output/92290_output.txt","testName":"WFM_10701550083_5GDU_WDC_Douglas_57_ADLP-ANJT_08012022_18_24_22","testDescription":"Ran from WFM","lsmName":"5G_IAU_21D","lsmVersion":"21.D.0","useCase":"RF_Scripts_Usecase10701550083_08012022,","status":"Success","userName":"superadmin","creationDate":"2022-08-01 18:37:32","useCaseDetails":null,"customerId":0,"id":6769,"wfmid":0,"useCaseSequence":0,"result":"","resultFilePath":"","fromDate":null,"toDate":null,"generateScriptPath":"Customer/40/Migration/10701550083/PreCheck/GenerateScript/6769/3000_5GIAU21D_RFScriptsUsecase1070155008308012022_44786_3000107015500ACPF0acpf-cell-report-config-20220610-1357RFUsecase1070155008308012022_163351.sh,Customer/40/Migration/10701550083/PreCheck/GenerateScript/6769/3100_5GIAU21D_RFScriptsUsecase1070155008308012022_44786_3100107015500ACPF0IntraNRANR-20220610-1357RFUsecase1070155008308012022_163349.sh,Customer/40/Migration/10701550083/PreCheck/GenerateScript/6769/4000_5GIAU21D_RFScriptsUsecase1070155008308012022_44786_40001550083AU0csl-tce-server-20220610-1357RFUsecase1070155008308012022_163346.sh,Customer/40/Migration/10701550083/PreCheck/GenerateScript/6769/4100_5GIAU21D_RFScriptsUsecase1070155008308012022_44786_41001550083AU0electrical-tilt-20220610-1357RFUsecase1070155008308012022_163355.sh,Customer/40/Migration/10701550083/PreCheck/GenerateScript/6769/4200_5GIAU21D_RFScriptsUsecase1070155008308012022_44786_42001550083AU0ipv6-route-20220610-1357RFUsecase1070155008308012022_163345.sh,Customer/40/Migration/10701550083/PreCheck/GenerateScript/6769/4300_5GIAU21D_RFScriptsUsecase1070155008308012022_44786_43001550083AU0dl-mimo-20220610-1357RFUsecase1070155008308012022_163350.sh,Customer/40/Migration/10701550083/PreCheck/GenerateScript/6769/4400_5GIAU21D_RFScriptsUsecase1070155008308012022_44786_44001550083AU0au-qci-20220610-1357RFUsecase1070155008308012022_163353.sh,Customer/40/Migration/10701550083/PreCheck/GenerateScript/6769/4600_5GIAU21D_RFScriptsUsecase1070155008308012022_44786_46001550083AU0au-cell-20220610-1357RFUsecase1070155008308012022_163352.sh,Customer/40/Migration/10701550083/PreCheck/GenerateScript/6769/4700_5GIAU21D_RFScriptsUsecase1070155008308012022_44786_47001550083AU0op-mode-20220610-1357RFUsecase1070155008308012022_163354.sh,Customer/40/Migration/10701550083/PreCheck/GenerateScript/6769/4800_5GIAU21D_RFScriptsUsecase1070155008308012022_44786_48001550083AU0auto-fusing-flag-20220610-1357RFUsecase1070155008308012022_163347.sh,Customer/40/Migration/10701550083/PreCheck/GenerateScript/6769/5200_5GIAU21D_RFScriptsUsecase1070155008308012022_44786_5200107015500ACPF0nr-intra-nbr-20220610-1357RFUsecase1070155008308012022_163348.sh","migStatusDesc":null},"negrowRunTestModel":{"customerDetailsEntity":null,"migrationType":"PreMigration","failedScript":"false","migrationSubType":"NEGrow","checklistFileName":"Verizon_USM_Migration Checklist_1.1.5.xlsx","neName":"10701550083_5GDU_WDC_Douglas_57_ADLP-ANJT","progressStatus":"Completed","ciqName":"VZW_WBV_mmWave_SNAP_CIQ_062422_v17.7.xlsx","outputFilepath":"Customer/40/PreMigration/10701550083/NEGrow/Output/92290_output.txt","testName":"WFM_10701550083_5GDU_WDC_Douglas_57_ADLP-ANJT_08012022_18_22_07","testDescription":"Ran from WFM","lsmName":"5G_IAU_21D","lsmVersion":"21.D.0","useCase":"pnp21DUsecase_10701550083_08012022,","status":"Success","userName":"superadmin","creationDate":"2022-08-01 18:24:14","useCaseDetails":null,"customerId":0,"id":6767,"wfmid":0,"useCaseSequence":0,"result":"","resultFilePath":"","fromDate":null,"toDate":null,"generateScriptPath":"Customer/40/PreMigration/10701550083/NEGrow/GenerateScript/6767/15000_5GIAU21D_pnp21DUsecase1070155008308012022_44789_CSVpnp21DmacroindoordistGEN2107015500835GDUWDCDouglas57ADLP-ANJT08012022_163358.sh","migStatusDesc":null},"ranAtpRunTestModel":null,"preMigStatus":"Completed","neGrowStatus":"Completed","inputRequired":"PMIG","preMigrationScriptPath":null,"preMigrationFileName":null,"filePathPre":"/home/user/Samsung/SMART/Customer/40/PreMigration/Output/VZW_WBV_mmWave_SNAP_CIQ_062422_v17.7/AU/ALL/21D/10701550083/","fileNamePre":"ALL_10701550083_5GDU_WDC_Douglas_57_ADLP-ANJT_08012022_18_22_07.zip","commPath":null,"envPath":null,"csvPath":null,"commZipName":null,"envZipName":null,"csvZipName":null,"preErrorFile":"/home/user/Samsung/SMART//ErrorLog/pre010701550083_08012022_18_22_00.log","neGrowErrorFile":"/home/user/Samsung/SMART//ErrorLog/negrow010701550083_08012022_18_22_00.log","migErrorFile":"/home/user/Samsung/SMART//ErrorLog/mig010701550083_08012022_18_22_00.log","siteName":"WDC_Douglas_57","siteReportStatus":"NotExecuted","siteReportId":null,"migStatus":"Completed","postErrorFile":"/home/user/Samsung/SMART//ErrorLog/post010701550083_08012022_18_22_00.log","postMigStatus":"InputsRequired","postMigrationRunTestModel":null},{"customerDetailsEntity":null,"enbId":"10701550082","neName":"10701550082_5GDU_WDC_Douglas_57_ADLP-ANJT","ciqName":"VZW_WBV_mmWave_SNAP_CIQ_062422_v17.7.xlsx","testName":"WFM_10701550082_5GDU_WDC_Douglas_57_ADLP-ANJT_08012022_18_21_05","testDescription":"","lsmName":null,"lsmVersion":null,"useCase":null,"status":"Completed","userName":"superadmin","creationDate":"2022-08-01 18:21:05","customerId":0,"id":2921,"result":null,"fromDate":null,"toDate":null,"migrationRunTestModel":null,"negrowRunTestModel":null,"ranAtpRunTestModel":null,"preMigStatus":"Failure","neGrowStatus":"CannotStart","inputRequired":"NEGROW","preMigrationScriptPath":null,"preMigrationFileName":null,"filePathPre":null,"fileNamePre":null,"commPath":null,"envPath":null,"csvPath":null,"commZipName":null,"envZipName":null,"csvZipName":null,"preErrorFile":"/home/user/Samsung/SMART//ErrorLog/pre010701550082_08012022_18_21_05.log","neGrowErrorFile":"/home/user/Samsung/SMART//ErrorLog/negrow010701550082_08012022_18_21_05.log","migErrorFile":"/home/user/Samsung/SMART//ErrorLog/mig010701550082_08012022_18_21_05.log","siteName":"WDC_Douglas_57","siteReportStatus":"NotExecuted","siteReportId":null,"migStatus":"CannotStart","postErrorFile":"/home/user/Samsung/SMART//ErrorLog/post010701550082_08012022_18_21_05.log","postMigStatus":"CannotStart","postMigrationRunTestModel":null},{"customerDetailsEntity":null,"enbId":"10701550083","neName":"10701550083_5GDU_WDC_Douglas_57_ADLP-ANJT","ciqName":"VZW_WBV_mmWave_SNAP_CIQ_062422_v17.7.xlsx","testName":"WFM_10701550083_5GDU_WDC_Douglas_57_ADLP-ANJT_08012022_18_21_05","testDescription":"","lsmName":null,"lsmVersion":null,"useCase":null,"status":"InProgress","userName":"superadmin","creationDate":"2022-08-01 18:21:05","customerId":0,"id":2922,"result":null,"fromDate":null,"toDate":null,"migrationRunTestModel":null,"negrowRunTestModel":null,"ranAtpRunTestModel":null,"preMigStatus":"InProgress","neGrowStatus":"NotYetStarted","inputRequired":"NEGROW","preMigrationScriptPath":null,"preMigrationFileName":null,"filePathPre":null,"fileNamePre":null,"commPath":null,"envPath":null,"csvPath":null,"commZipName":null,"envZipName":null,"csvZipName":null,"preErrorFile":"/home/user/Samsung/SMART//ErrorLog/pre010701550083_08012022_18_21_05.log","neGrowErrorFile":"/home/user/Samsung/SMART//ErrorLog/negrow010701550083_08012022_18_21_05.log","migErrorFile":"/home/user/Samsung/SMART//ErrorLog/mig010701550083_08012022_18_21_05.log","siteName":"WDC_Douglas_57","siteReportStatus":"NotExecuted","siteReportId":null,"migStatus":"NotYetStarted","postErrorFile":"/home/user/Samsung/SMART//ErrorLog/post010701550083_08012022_18_21_05.log","postMigStatus":"InputsRequired","postMigrationRunTestModel":null},{"customerDetailsEntity":null,"enbId":"10701580023","neName":"10701580023_5GDU_AMERICAN_TRUCKING_SC_ADLP-ANJT","ciqName":"VZW_WBV_mmWave_SNAP_CIQ_062422_v17.7.xlsx","testName":"WFM_10701580023_5GDU_AMERICAN_TRUCKING_SC_ADLP-ANJT_07282022_17_11_18","testDescription":"","lsmName":"5G_MM_5","lsmVersion":"21.D.0","useCase":null,"status":"Completed","userName":"superadmin","creationDate":"2022-07-28 17:11:18","customerId":0,"id":2917,"result":null,"fromDate":null,"toDate":null,"migrationRunTestModel":null,"negrowRunTestModel":{"customerDetailsEntity":null,"migrationType":"PreMigration","failedScript":"false","migrationSubType":"NEGrow","checklistFileName":"Verizon_USM_Migration Checklist_1.1.5.xlsx","neName":"10701580023_5GDU_AMERICAN_TRUCKING_SC_ADLP-ANJT","progressStatus":"Completed","ciqName":"VZW_WBV_mmWave_SNAP_CIQ_062422_v17.7.xlsx","outputFilepath":"Customer/40/PreMigration/10701580023/NEGrow/Output/97397_output.txt","testName":"WFM_10701580023_5GDU_AMERICAN_TRUCKING_SC_ADLP-ANJT_07282022_17_11_21","testDescription":"Ran from WFM","lsmName":"5G_MM_5","lsmVersion":"21.D.0","useCase":"pnp21DUsecase_10701580023_07282022,","status":"Success","userName":"superadmin","creationDate":"2022-07-28 17:11:35","useCaseDetails":null,"customerId":0,"id":6763,"wfmid":0,"useCaseSequence":0,"result":"","resultFilePath":"","fromDate":null,"toDate":null,"generateScriptPath":"Customer/40/PreMigration/10701580023/NEGrow/GenerateScript/6763/15000_5GMM5_pnp21DUsecase1070158002307282022_44776_CSVpnp21DmacroindoordistGEN2107015800235GDUAMERICANTRUCKINGSCADLP-ANJT07282022_163334.sh","migStatusDesc":null},"ranAtpRunTestModel":null,"preMigStatus":"Completed","neGrowStatus":"Completed","inputRequired":"MIG","preMigrationScriptPath":null,"preMigrationFileName":null,"filePathPre":"/home/user/Samsung/SMART/Customer/40/PreMigration/Output/VZW_WBV_mmWave_SNAP_CIQ_062422_v17.7/AU/ALL/21D/10701580023/","fileNamePre":"ALL_10701580023_5GDU_AMERICAN_TRUCKING_SC_ADLP-ANJT_07282022_17_11_21.zip","commPath":null,"envPath":null,"csvPath":null,"commZipName":null,"envZipName":null,"csvZipName":null,"preErrorFile":"/home/user/Samsung/SMART//ErrorLog/pre010701580023_07282022_17_11_18.log","neGrowErrorFile":"/home/user/Samsung/SMART//ErrorLog/negrow010701580023_07282022_17_11_18.log","migErrorFile":"/home/user/Samsung/SMART//ErrorLog/mig010701580023_07282022_17_11_18.log","siteName":"AMERICAN_TRUCKING_SC","siteReportStatus":"NotExecuted","siteReportId":null,"migStatus":"Failure","postErrorFile":"/home/user/Samsung/SMART//ErrorLog/post010701580023_07282022_17_11_18.log","postMigStatus":"InputsRequired","postMigrationRunTestModel":null},{"customerDetailsEntity":null,"enbId":"10701580024","neName":"10701580024_5GDU_AMERICAN_TRUCKING_SC_ADLP-ANJT","ciqName":"VZW_WBV_mmWave_SNAP_CIQ_062422_v17.7.xlsx","testName":"WFM_10701580024_5GDU_AMERICAN_TRUCKING_SC_ADLP-ANJT_07282022_17_11_18","testDescription":"","lsmName":"5G_MM_5","lsmVersion":"21.D.0","useCase":null,"status":"Completed","userName":"superadmin","creationDate":"2022-07-28 17:11:18","customerId":0,"id":2918,"result":null,"fromDate":null,"toDate":null,"migrationRunTestModel":null,"negrowRunTestModel":{"customerDetailsEntity":null,"migrationType":"PreMigration","failedScript":"false","migrationSubType":"NEGrow","checklistFileName":"Verizon_USM_Migration Checklist_1.1.5.xlsx","neName":"10701580024_5GDU_AMERICAN_TRUCKING_SC_ADLP-ANJT","progressStatus":"Completed","ciqName":"VZW_WBV_mmWave_SNAP_CIQ_062422_v17.7.xlsx","outputFilepath":"Customer/40/PreMigration/10701580024/NEGrow/Output/97397_output.txt","testName":"WFM_10701580024_5GDU_AMERICAN_TRUCKING_SC_ADLP-ANJT_07282022_17_11_21","testDescription":"Ran from WFM","lsmName":"5G_MM_5","lsmVersion":"21.D.0","useCase":"pnp21DUsecase_10701580024_07282022,","status":"Success","userName":"superadmin","creationDate":"2022-07-28 17:11:30","useCaseDetails":null,"customerId":0,"id":6761,"wfmid":0,"useCaseSequence":0,"result":"","resultFilePath":"","fromDate":null,"toDate":null,"generateScriptPath":"Customer/40/PreMigration/10701580024/NEGrow/GenerateScript/6761/15000_5GMM5_pnp21DUsecase1070158002407282022_44777_CSVpnp21DmacroindoordistGEN2107015800245GDUAMERICANTRUCKINGSCADLP-ANJT07282022_163336.sh","migStatusDesc":null},"ranAtpRunTestModel":null,"preMigStatus":"Completed","neGrowStatus":"Completed","inputRequired":"MIG","preMigrationScriptPath":null,"preMigrationFileName":null,"filePathPre":"/home/user/Samsung/SMART/Customer/40/PreMigration/Output/VZW_WBV_mmWave_SNAP_CIQ_062422_v17.7/AU/ALL/21D/10701580024/","fileNamePre":"ALL_10701580024_5GDU_AMERICAN_TRUCKING_SC_ADLP-ANJT_07282022_17_11_21.zip","commPath":null,"envPath":null,"csvPath":null,"commZipName":null,"envZipName":null,"csvZipName":null,"preErrorFile":"/home/user/Samsung/SMART//ErrorLog/pre010701580024_07282022_17_11_18.log","neGrowErrorFile":"/home/user/Samsung/SMART//ErrorLog/negrow010701580024_07282022_17_11_18.log","migErrorFile":"/home/user/Samsung/SMART//ErrorLog/mig010701580024_07282022_17_11_18.log","siteName":"AMERICAN_TRUCKING_SC","siteReportStatus":"NotExecuted","siteReportId":null,"migStatus":"Failure","postErrorFile":"/home/user/Samsung/SMART//ErrorLog/post010701580024_07282022_17_11_18.log","postMigStatus":"InputsRequired","postMigrationRunTestModel":null},{"customerDetailsEntity":null,"enbId":"10701580025","neName":"10701580025_5GDU_AMERICAN_TRUCKING_SC_ADLP-ANJT","ciqName":"VZW_WBV_mmWave_SNAP_CIQ_062422_v17.7.xlsx","testName":"WFM_10701580025_5GDU_AMERICAN_TRUCKING_SC_ADLP-ANJT_07282022_17_11_18","testDescription":"","lsmName":"5G_MM_5","lsmVersion":"21.D.0","useCase":null,"status":"Completed","userName":"superadmin","creationDate":"2022-07-28 17:11:18","customerId":0,"id":2919,"result":null,"fromDate":null,"toDate":null,"migrationRunTestModel":null,"negrowRunTestModel":{"customerDetailsEntity":null,"migrationType":"PreMigration","failedScript":"false","migrationSubType":"NEGrow","checklistFileName":"Verizon_USM_Migration Checklist_1.1.5.xlsx","neName":"10701580025_5GDU_AMERICAN_TRUCKING_SC_ADLP-ANJT","progressStatus":"Completed","ciqName":"VZW_WBV_mmWave_SNAP_CIQ_062422_v17.7.xlsx","outputFilepath":"Customer/40/PreMigration/10701580025/NEGrow/Output/97397_output.txt","testName":"WFM_10701580025_5GDU_AMERICAN_TRUCKING_SC_ADLP-ANJT_07282022_17_11_21","testDescription":"Ran from WFM","lsmName":"5G_MM_5","lsmVersion":"21.D.0","useCase":"pnp21DUsecase_10701580025_07282022,","status":"Success","userName":"superadmin","creationDate":"2022-07-28 17:11:40","useCaseDetails":null,"customerId":0,"id":6762,"wfmid":0,"useCaseSequence":0,"result":"","resultFilePath":"","fromDate":null,"toDate":null,"generateScriptPath":"Customer/40/PreMigration/10701580025/NEGrow/GenerateScript/6762/15000_5GMM5_pnp21DUsecase1070158002507282022_44778_CSVpnp21DmacroindoordistGEN2107015800255GDUAMERICANTRUCKINGSCADLP-ANJT07282022_163335.sh","migStatusDesc":null},"ranAtpRunTestModel":null,"preMigStatus":"Completed","neGrowStatus":"Completed","inputRequired":"MIG","preMigrationScriptPath":null,"preMigrationFileName":null,"filePathPre":"/home/user/Samsung/SMART/Customer/40/PreMigration/Output/VZW_WBV_mmWave_SNAP_CIQ_062422_v17.7/AU/ALL/21D/10701580025/","fileNamePre":"ALL_10701580025_5GDU_AMERICAN_TRUCKING_SC_ADLP-ANJT_07282022_17_11_21.zip","commPath":null,"envPath":null,"csvPath":null,"commZipName":null,"envZipName":null,"csvZipName":null,"preErrorFile":"/home/user/Samsung/SMART//ErrorLog/pre010701580025_07282022_17_11_18.log","neGrowErrorFile":"/home/user/Samsung/SMART//ErrorLog/negrow010701580025_07282022_17_11_18.log","migErrorFile":"/home/user/Samsung/SMART//ErrorLog/mig010701580025_07282022_17_11_18.log","siteName":"AMERICAN_TRUCKING_SC","siteReportStatus":"NotExecuted","siteReportId":null,"migStatus":"Failure","postErrorFile":"/home/user/Samsung/SMART//ErrorLog/post010701580025_07282022_17_11_18.log","postMigStatus":"InputsRequired","postMigrationRunTestModel":null}],"isInProgress":true,"status":"SUCCESS"}') :
                        this.programName == 'VZN-5G-DSS' ? JSON.parse('{"pageCount":1,"toDate":"07/28/2022","sessionId":"7032c5b6","getCiqList":[{"id":352,"programDetailsEntity":{"id":42,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-DSS","programDescription":"DSS","status":"Active","creationDate":"2022-06-13T05:17:59.000+0000","createdBy":"superadmin","sourceProgramId":null,"sourceprogramName":null},"ciqFileName":"VZW_TRI_DSS_CIQ_v2.38_20220126.xlsx","ciqFilePath":"Customer/42/PreMigration/Input/VZW_TRI_DSS_CIQ_v2.38_20220126/CIQ/","scriptFileName":"8891002001.zip,8891002141.zip,8891002123.zip,8891002036.zip","scriptFilePath":"Customer/42/PreMigration/Input/VZW_TRI_DSS_CIQ_v2.38_20220126/SCRIPT/","checklistFileName":"Verizon_USM_Migration Checklist_1.1.5.xlsx","checklistFilePath":"Customer/42/PreMigration/Input/VZW_TRI_DSS_CIQ_v2.38_20220126/CHECKLIST/","ciqVersion":"ORIGINAL","fileSourceType":"UPLOAD","uploadBy":"superadmin","remarks":"","creationDate":"2022-07-26T10:41:46.000+0000","fetchInfo":null}],"postmigusecaselist":[{"useCaseName":"Acpf","useCaseId":10243,"ucSleepInterval":"1000","executionSequence":1222,"scripts":[{"scriptId":35306,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"ACPF_Post_end-point-x2_REQUEST.xml","scriptExeSequence":1288}]},{"useCaseName":"DSS_ACPF","useCaseId":10355,"ucSleepInterval":"1000","executionSequence":15,"scripts":[{"scriptId":35884,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"DSS_ACPF_Post_end-point-x2.xml","scriptExeSequence":12345},{"scriptId":35885,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"DSS_ACPF_Post_gutran-cu-cell.xml","scriptExeSequence":12346}]},{"useCaseName":"DSS_all","useCaseId":10356,"ucSleepInterval":"1000","executionSequence":16,"scripts":[{"scriptId":35884,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"DSS_ACPF_Post_end-point-x2.xml","scriptExeSequence":12351},{"scriptId":35885,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"DSS_ACPF_Post_gutran-cu-cell.xml","scriptExeSequence":12352},{"scriptId":35886,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"DSS_ADPF_Post_end-point-dss.xml","scriptExeSequence":12347},{"scriptId":35887,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"DSS_ADPF_Post_end-point-f1c.xml","scriptExeSequence":12348},{"scriptId":35888,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"DSS_ADPF_Post_gutran-du-cell.xml","scriptExeSequence":12349},{"scriptId":35889,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"DSS_ADPF_Post_pod-entries.xml","scriptExeSequence":12350}]},{"useCaseName":"Dss_New","useCaseId":10675,"ucSleepInterval":"1000","executionSequence":140,"scripts":[{"scriptId":39178,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"DSS_ACPF-a1-a2-a3-audit.xml","scriptExeSequence":23456},{"scriptId":39179,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"DSS_ACPF-audit-input.xml","scriptExeSequence":23457},{"scriptId":39180,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"DSS_AUPF-audit-input.xml","scriptExeSequence":23458},{"scriptId":39182,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"DSS_FSU-audit-input.xml","scriptExeSequence":23459}]},{"useCaseName":"Dss_acpf2","useCaseId":10684,"ucSleepInterval":"1000","executionSequence":22,"scripts":[{"scriptId":39179,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"DSS_ACPF-audit-input.xml","scriptExeSequence":121332}]},{"useCaseName":"Dss_vdu","useCaseId":10686,"ucSleepInterval":"1000","executionSequence":26,"scripts":[{"scriptId":39183,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"DSS_vDU-audit-input.xml","scriptExeSequence":21213}]},{"useCaseName":"DSS_ACPFa1a2","useCaseId":10688,"ucSleepInterval":"1000","executionSequence":43,"scripts":[{"scriptId":39178,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"DSS_ACPF-a1-a2-a3-audit.xml","scriptExeSequence":14334}]},{"useCaseName":"Dss_enb","useCaseId":10739,"ucSleepInterval":"1000","executionSequence":187,"scripts":[{"scriptId":39368,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"DSS_eNB-endc-audit-input.xml","scriptExeSequence":13142}]},{"useCaseName":"AU_Usecase","useCaseId":16287,"ucSleepInterval":"1000","executionSequence":12121,"scripts":[{"scriptId":51303,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"eNB_ENDCAudit20B0SWVer.xml","scriptExeSequence":1235}]},{"useCaseName":"AU_usecase1","useCaseId":16288,"ucSleepInterval":"1000","executionSequence":1342,"scripts":[{"scriptId":51306,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"AU20A-ParamCheck.xml","scriptExeSequence":1133}]},{"useCaseName":"NEW_DSS","useCaseId":21099,"ucSleepInterval":"1000","executionSequence":54321,"scripts":[{"scriptId":63655,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"DSS_eNB_Audit.xml","scriptExeSequence":76554},{"scriptId":63654,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"DSS_FSU_Audit.xml","scriptExeSequence":86332},{"scriptId":63653,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"DSS_FSU_config_Audit.xml","scriptExeSequence":75342},{"scriptId":63652,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"DSS_vDU_config_Audit.xml","scriptExeSequence":55332},{"scriptId":63651,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"DSS_vDU_Audit.xml","scriptExeSequence":87554}]},{"useCaseName":"test","useCaseId":23017,"ucSleepInterval":"1000","executionSequence":76,"scripts":[{"scriptId":71471,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"DSS_vDU_ptpsync.xml","scriptExeSequence":1}]},{"useCaseName":"PreImpact_Audit_21D","useCaseId":26493,"ucSleepInterval":"1000","executionSequence":61,"scripts":[{"scriptId":97285,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"DSS_eNB_Impact_Audit.xml","scriptExeSequence":62},{"scriptId":97314,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"DSS_FSU_Impact_Audit.xml","scriptExeSequence":103}]},{"useCaseName":"PostImpact_Audit_21D","useCaseId":26494,"ucSleepInterval":"1000","executionSequence":63,"scripts":[{"scriptId":97286,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"DSS_eNB_Post_Impact_Audit.xml","scriptExeSequence":64},{"scriptId":97315,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"DSS_FSU_Post_Impact_Audit.xml","scriptExeSequence":104}]}],"fromDate":"07/21/2022","migusecaselist":[],"smVersion":[{"name":"20.A.0","id":44,"smNameList":[{"name":"Tiny","useCaseList":[{"useCaseName":"AU_Usecase","useCaseId":16287,"executionSequence":12121},{"useCaseName":"AU_usecase1","useCaseId":16288,"executionSequence":1342},{"useCaseName":"NEW_DSS","useCaseId":21099,"executionSequence":54321},{"useCaseName":"Acpf","useCaseId":10243,"executionSequence":1222},{"useCaseName":"DSS_ACPF","useCaseId":10355,"executionSequence":15},{"useCaseName":"DSS_all","useCaseId":10356,"executionSequence":16},{"useCaseName":"Dss_New","useCaseId":10675,"executionSequence":140},{"useCaseName":"Dss_acpf2","useCaseId":10684,"executionSequence":22},{"useCaseName":"Dss_vdu","useCaseId":10686,"executionSequence":26},{"useCaseName":"DSS_ACPFa1a2","useCaseId":10688,"executionSequence":43},{"useCaseName":"Dss_enb","useCaseId":10739,"executionSequence":187}],"id":102},{"name":"Medium","useCaseList":[{"useCaseName":"AU_Usecase","useCaseId":16287,"executionSequence":12121},{"useCaseName":"AU_usecase1","useCaseId":16288,"executionSequence":1342},{"useCaseName":"NEW_DSS","useCaseId":21099,"executionSequence":54321},{"useCaseName":"Acpf","useCaseId":10243,"executionSequence":1222},{"useCaseName":"DSS_ACPF","useCaseId":10355,"executionSequence":15},{"useCaseName":"DSS_all","useCaseId":10356,"executionSequence":16},{"useCaseName":"Dss_New","useCaseId":10675,"executionSequence":140},{"useCaseName":"Dss_acpf2","useCaseId":10684,"executionSequence":22},{"useCaseName":"Dss_vdu","useCaseId":10686,"executionSequence":26},{"useCaseName":"DSS_ACPFa1a2","useCaseId":10688,"executionSequence":43},{"useCaseName":"Dss_enb","useCaseId":10739,"executionSequence":187}],"id":103},{"name":"4G_DSS","id":119}]},{"name":"21.D.0","id":55,"smNameList":[{"name":"Ultane","useCaseList":[{"useCaseName":"AU_Usecase","useCaseId":16287,"executionSequence":12121},{"useCaseName":"AU_usecase1","useCaseId":16288,"executionSequence":1342},{"useCaseName":"NEW_DSS","useCaseId":21099,"executionSequence":54321},{"useCaseName":"Acpf","useCaseId":10243,"executionSequence":1222},{"useCaseName":"DSS_ACPF","useCaseId":10355,"executionSequence":15},{"useCaseName":"DSS_all","useCaseId":10356,"executionSequence":16},{"useCaseName":"Dss_New","useCaseId":10675,"executionSequence":140},{"useCaseName":"Dss_acpf2","useCaseId":10684,"executionSequence":22},{"useCaseName":"Dss_vdu","useCaseId":10686,"executionSequence":26},{"useCaseName":"DSS_ACPFa1a2","useCaseId":10688,"executionSequence":43},{"useCaseName":"Dss_enb","useCaseId":10739,"executionSequence":187}],"id":101}]}],"ranAtpUseCaseList":[],"negrowuseCaseLst":[],"serviceToken":"86282","runTestTableDetails":[{"customerDetailsEntity":null,"enbId":"8891002001","neName":"8891002001_5GDU_WhitehouseStation-WLMT","ciqName":"VZW_TRI_DSS_CIQ_v2.38_20220126.xlsx","testName":"WFM_8891002001_5GDU_WhitehouseStation-WLMT_07262022_22_15_24","testDescription":"","lsmName":"Ultane","lsmVersion":"21.D.0","useCase":null,"status":"Completed","userName":"superadmin","creationDate":"2022-07-26 22:15:24","customerId":0,"id":1521,"result":null,"fromDate":null,"toDate":null,"migrationRunTestModel":null,"negrowRunTestModel":{"customerDetailsEntity":null,"migrationType":"PreMigration","failedScript":"false","migrationSubType":"NEGrow","checklistFileName":"Verizon_USM_Migration Checklist_1.1.5.xlsx","neName":"8891002001_5GDU_WhitehouseStation-WLMT","progressStatus":"Completed","ciqName":"VZW_TRI_DSS_CIQ_v2.38_20220126.xlsx","outputFilepath":"Customer/42/PreMigration/8891002001/NEGrow/Output/95021_output.txt","testName":"WFM_8891002001_5GDU_WhitehouseStation-WLMT_07262022_22_16_15","testDescription":"Ran from WFM","lsmName":"Ultane","lsmVersion":"21.D.0","useCase":"pnpGrow_8891002001_07262022,","status":"Success","userName":"superadmin","creationDate":"2022-07-26 22:16:40","useCaseDetails":null,"customerId":0,"id":4241,"wfmid":0,"useCaseSequence":0,"result":"","resultFilePath":"","fromDate":null,"toDate":null,"generateScriptPath":"Customer/42/PreMigration/8891002001/NEGrow/GenerateScript/4241/4_Ultane_pnpGrow889100200107262022_27347_CSVpnpGrow88910020015GDUWhitehouseStation-WLMT0726202207262022_105672.sh","migStatusDesc":null},"ranAtpRunTestModel":null,"preMigStatus":"Completed","neGrowStatus":"Completed","inputRequired":"MIG","preMigrationScriptPath":null,"preMigrationFileName":null,"filePathPre":"/home/user/RCT/rctsoftware/Customer/42/PreMigration/Output/VZW_TRI_DSS_CIQ_v2.38_20220126/AU/ALL/21.D.0/8891002001/","fileNamePre":"ALL_8891002001_5GDU_WhitehouseStation-WLMT_07262022_22_16_15.zip","commPath":null,"envPath":null,"csvPath":null,"commZipName":null,"envZipName":null,"csvZipName":null,"preErrorFile":"/home/user/RCT/rctsoftware//ErrorLog/pre08891002001_07262022_22_15_24.log","neGrowErrorFile":"/home/user/RCT/rctsoftware//ErrorLog/negrow08891002001_07262022_22_15_24.log","migErrorFile":"/home/user/RCT/rctsoftware//ErrorLog/mig08891002001_07262022_22_15_24.log","siteName":null,"siteReportStatus":"Partial Save","siteReportId":1521,"migStatus":"InputsRequired","postErrorFile":"/home/user/RCT/rctsoftware//ErrorLog/post08891002001_07262022_22_15_24.log","postMigStatus":"InputsRequired","postMigrationRunTestModel":null},{"customerDetailsEntity":null,"enbId":"8891002001","neName":"8891002001_5GDU_WhitehouseStation-WLMT","ciqName":"VZW_TRI_DSS_CIQ_v2.38_20220126.xlsx","testName":"WFM_8891002001_5GDU_WhitehouseStation-WLMT_07262022_22_11_31","testDescription":"","lsmName":"Ultane","lsmVersion":"21.D.0","useCase":null,"status":"Completed","userName":"superadmin","creationDate":"2022-07-26 22:11:31","customerId":0,"id":1520,"result":null,"fromDate":null,"toDate":null,"migrationRunTestModel":null,"negrowRunTestModel":{"customerDetailsEntity":null,"migrationType":"PreMigration","failedScript":"false","migrationSubType":"NEGrow","checklistFileName":"Verizon_USM_Migration Checklist_1.1.5.xlsx","neName":"8891002001_5GDU_WhitehouseStation-WLMT","progressStatus":"Completed","ciqName":"VZW_TRI_DSS_CIQ_v2.38_20220126.xlsx","outputFilepath":"Customer/42/PreMigration/8891002001/NEGrow/Output/66590_output.txt","testName":"WFM_8891002001_5GDU_WhitehouseStation-WLMT_07262022_22_14_27","testDescription":"Ran from WFM","lsmName":"Ultane","lsmVersion":"21.D.0","useCase":"vDUGrow_8891002001_07262022,","status":"Success","userName":"superadmin","creationDate":"2022-07-26 22:14:50","useCaseDetails":null,"customerId":0,"id":4240,"wfmid":0,"useCaseSequence":0,"result":"","resultFilePath":"","fromDate":null,"toDate":null,"generateScriptPath":"Customer/42/PreMigration/8891002001/NEGrow/GenerateScript/4240/6_Ultane_vDUGrow889100200107262022_27342_CSVvDUGrow88910020015GDUWhitehouseStation-WLMT0726202207262022_105597.sh","migStatusDesc":null},"ranAtpRunTestModel":null,"preMigStatus":"Completed","neGrowStatus":"Completed","inputRequired":"MIG","preMigrationScriptPath":null,"preMigrationFileName":null,"filePathPre":"/home/user/RCT/rctsoftware/Customer/42/PreMigration/Output/VZW_TRI_DSS_CIQ_v2.38_20220126/AU/ALL/21.D.0/8891002001/","fileNamePre":"ALL_8891002001_5GDU_WhitehouseStation-WLMT_07262022_22_11_51.zip","commPath":null,"envPath":null,"csvPath":null,"commZipName":null,"envZipName":null,"csvZipName":null,"preErrorFile":"/home/user/RCT/rctsoftware//ErrorLog/pre08891002001_07262022_22_11_31.log","neGrowErrorFile":"/home/user/RCT/rctsoftware//ErrorLog/negrow08891002001_07262022_22_11_31.log","migErrorFile":"/home/user/RCT/rctsoftware//ErrorLog/mig08891002001_07262022_22_11_31.log","siteName":null,"siteReportStatus":"NotExecuted","siteReportId":null,"migStatus":"InputsRequired","postErrorFile":"/home/user/RCT/rctsoftware//ErrorLog/post08891002001_07262022_22_11_31.log","postMigStatus":"InputsRequired","postMigrationRunTestModel":null},{"customerDetailsEntity":null,"enbId":"8891002123","neName":"8891002123_5GDU_BRANCHBURG_4-WLMT","ciqName":"VZW_TRI_DSS_CIQ_v2.38_20220126.xlsx","testName":"WFM_8891002123_5GDU_BRANCHBURG_4-WLMT_07262022_18_45_03","testDescription":"","lsmName":null,"lsmVersion":null,"useCase":null,"status":"Completed","userName":"superadmin","creationDate":"2022-07-26 18:45:03","customerId":0,"id":1518,"result":null,"fromDate":null,"toDate":null,"migrationRunTestModel":null,"negrowRunTestModel":null,"ranAtpRunTestModel":null,"preMigStatus":"Failure","neGrowStatus":"CannotStart","inputRequired":"NEGROW","preMigrationScriptPath":null,"preMigrationFileName":null,"filePathPre":null,"fileNamePre":null,"commPath":null,"envPath":null,"csvPath":null,"commZipName":null,"envZipName":null,"csvZipName":null,"preErrorFile":"/home/user/RCT/rctsoftware//ErrorLog/pre08891002123_07262022_18_45_03.log","neGrowErrorFile":"/home/user/RCT/rctsoftware//ErrorLog/negrow08891002123_07262022_18_45_03.log","migErrorFile":"/home/user/RCT/rctsoftware//ErrorLog/mig08891002123_07262022_18_45_03.log","siteName":null,"siteReportStatus":"NotExecuted","siteReportId":null,"migStatus":"CannotStart","postErrorFile":"/home/user/RCT/rctsoftware//ErrorLog/post08891002123_07262022_18_45_03.log","postMigStatus":"CannotStart","postMigrationRunTestModel":null},{"customerDetailsEntity":null,"enbId":"8891002001","neName":"8891002001_5GDU_WhitehouseStation-WLMT","ciqName":"VZW_TRI_DSS_CIQ_v2.38_20220126.xlsx","testName":"WFM_8891002001_5GDU_WhitehouseStation-WLMT_07252022_12_21_00","testDescription":"","lsmName":"Ultane","lsmVersion":"21.D.0","useCase":null,"status":"Completed","userName":"superadmin","creationDate":"2022-07-25 12:21:00","customerId":0,"id":1514,"result":null,"fromDate":null,"toDate":null,"migrationRunTestModel":null,"negrowRunTestModel":null,"ranAtpRunTestModel":null,"preMigStatus":"Completed","neGrowStatus":"InputsRequired","inputRequired":"NEGROW","preMigrationScriptPath":null,"preMigrationFileName":null,"filePathPre":"/home/user/RCT/rctsoftware/Customer/42/PreMigration/Output/VZW_TRI_DSS_CIQ_v2.38_20220126/AU/ALL/21.D.0/8891002001/","fileNamePre":"ALL_8891002001_5GDU_WhitehouseStation-WLMT_07252022_12_21_19.zip","commPath":null,"envPath":null,"csvPath":null,"commZipName":null,"envZipName":null,"csvZipName":null,"preErrorFile":"/home/user/RCT/rctsoftware//ErrorLog/pre08891002001_07252022_12_21_00.log","neGrowErrorFile":"/home/user/RCT/rctsoftware//ErrorLog/negrow08891002001_07252022_12_21_00.log","migErrorFile":"/home/user/RCT/rctsoftware//ErrorLog/mig08891002001_07252022_12_21_00.log","siteName":null,"siteReportStatus":"NotExecuted","siteReportId":null,"migStatus":"InputsRequired","postErrorFile":"/home/user/RCT/rctsoftware//ErrorLog/post08891002001_07252022_12_21_00.log","postMigStatus":"InputsRequired","postMigrationRunTestModel":null}],"isInProgress":false,"status":"SUCCESS"}') : 
                        JSON.parse('{"pageCount":3,"toDate":"12/21/2022","sessionId":"6daec790","getCiqList":[{"id":615,"programDetailsEntity":{"id":38,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-FSU","programDescription":"FSU","status":"Active","creationDate":"2022-12-15T05:43:23.000+0000","createdBy":"superadmin","sourceProgramId":34,"sourceprogramName":"VZN-4G-USM-LIVE"},"ciqFileName":"VZW_NYM_FSU_CIQ_v1.36_20221026.xlsx","ciqFilePath":"Customer/38/PreMigration/Input/VZW_NYM_FSU_CIQ_v1.36_20221026/CIQ/","scriptFileName":"","scriptFilePath":"Customer/38/PreMigration/Input/VZW_NYM_FSU_CIQ_v1.36_20221026/SCRIPT/","checklistFileName":"","checklistFilePath":"","ciqVersion":"ORIGINAL","fileSourceType":"UPLOAD","uploadBy":"superadmin","remarks":"","creationDate":"2022-12-19T06:03:48.000+0000","fetchInfo":null}],"postmigusecaselist":[{"useCaseName":"opticdistance","useCaseId":17201,"ucSleepInterval":"1000","executionSequence":23,"scripts":[{"scriptId":53476,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"Audit4G_eNB_TxRfPower.xml","scriptExeSequence":1}]},{"useCaseName":"FsuPostMigration","useCaseId":17210,"ucSleepInterval":"1000","executionSequence":2,"scripts":[{"scriptId":53501,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"Audit4G_eNB_retrieve_cell_throughput.xml","scriptExeSequence":3},{"scriptId":53476,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"Audit4G_eNB_TxRfPower.xml","scriptExeSequence":1}]},{"useCaseName":"4G_FSU_Audit","useCaseId":39958,"ucSleepInterval":"1000","executionSequence":17,"scripts":[{"scriptId":138273,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"Audit4G_eNB_SerialNumber.xml","scriptExeSequence":10},{"scriptId":138272,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"Audit4G_eNB_TxRfPower.xml","scriptExeSequence":11},{"scriptId":138271,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"Audit4G_FSU_backup_db.xml","scriptExeSequence":12},{"scriptId":138270,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"Audit4G_FSU_PostChecks.xml","scriptExeSequence":13}]},{"useCaseName":"sfp_fsu","useCaseId":48783,"ucSleepInterval":"1000","executionSequence":451,"scripts":[{"scriptId":138270,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"Audit4G_FSU_PostChecks.xml","scriptExeSequence":13},{"scriptId":178252,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"Audit4G_FSU_Sfp_Inventory.xml","scriptExeSequence":12}]}],"preaudituseCaseLst":[{"useCaseName":"preaudit_usecase","useCaseId":47991,"ucSleepInterval":"1000","executionSequence":10,"scripts":[{"scriptId":175466,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"Audit4G_FSU_PostChecks.xml","scriptExeSequence":13}]}],"fromDate":"12/14/2022","migusecaselist":[],"smVersion":[{"name":"19.A.0","id":34},{"name":"21.D.0","id":47,"smNameList":[{"name":"nym21d","id":92},{"name":"4G_USM_LIVE","id":114}]},{"name":"21.D.0","id":61,"smNameList":[{"name":"4G_FSU_21D","id":159}]},{"name":"21.B.0","id":62,"smNameList":[{"name":"4G_FSU_NYM","id":160},{"name":"4G_FSU_NEmapping","id":162},{"name":"FSU_NE","id":165}]},{"name":"22.A.0","id":65,"smNameList":[{"name":"4G_FSU_22A","id":177},{"name":"nym","id":190}]},{"name":"21.A.1","id":68}],"ranAtpUseCaseList":[],"negrowuseCaseLst":[],"serviceToken":"74660","runTestTableDetails":[{"customerDetailsEntity":null,"enbId":"78267001","neName":"78267001_FSU_BREWSTER","integrationType":"NA","ciqName":"VZW_NYM_FSU_CIQ_v1.36_20221026.xlsx","testName":"WFM_78267001_FSU_BREWSTER_12212022_17_41_17","testDescription":"","lsmName":"4G_FSU_22A","lsmVersion":"22.A.0","useCase":null,"status":"Completed","userName":"superadmin","creationDate":"2022-12-21 17:41:17","customerId":0,"id":1369,"result":null,"fromDate":null,"toDate":null,"migrationRunTestModel":null,"negrowRunTestModel":null,"preAuditMigrationRunTestModel":null,"neStatusRunTestModel":null,"ranAtpRunTestModel":null,"preMigStatus":"Completed","neGrowStatus":"Failure","inputRequired":"NEGROW","preMigrationScriptPath":null,"preMigrationFileName":null,"filePathPre":null,"fileNamePre":null,"commPath":"/home/user/Samsung/SMART/Customer/38/PreMigration/Output/VZW_NYM_FSU_CIQ_v1.36_20221026/78267001/COMMISSIONING_SCRIPT/","envPath":"/home/user/Samsung/SMART/Customer/38/PreMigration/Output/VZW_NYM_FSU_CIQ_v1.36_20221026/78267001/ENV/","csvPath":"/home/user/Samsung/SMART/Customer/38/PreMigration/Output/VZW_NYM_FSU_CIQ_v1.36_20221026/78267001/CSV/","commZipName":"COMMISSION_SCRIPT_78267001_FSU_BREWSTER_12212022_17_41_20.zip","envZipName":"ENV_78267001_FSU_BREWSTER_12212022_17_41_17.zip","csvZipName":"CSV_78267001_FSU_BREWSTER_12212022_17_41_22.zip","preErrorFile":"/home/user/Samsung/SMART//ErrorLog/pre078267001_12212022_17_41_17.log","neGrowErrorFile":"/home/user/Samsung/SMART//ErrorLog/negrow078267001_12212022_17_41_17.log","preAuditErrorFile":"/home/user/Samsung/SMART//ErrorLog/preaudit078267001_12212022_17_41_17.log","neStatusErrorFile":"/home/user/Samsung/SMART//ErrorLog/nestatus078267001_12212022_17_41_17.log","migErrorFile":"/home/user/Samsung/SMART//ErrorLog/mig078267001_12212022_17_41_17.log","siteName":null,"siteReportStatus":"NotExecuted","siteReportId":null,"testInfo":null,"migStatus":"InputsRequired","postErrorFile":"/home/user/Samsung/SMART//ErrorLog/post078267001_12212022_17_41_17.log","postMigStatus":"InputsRequired","preAuditStatus":"InputsRequired","neStatus":"InputsRequired","postMigrationRunTestModel":null},{"customerDetailsEntity":null,"enbId":"78267001","neName":"78267001_FSU_BREWSTER","integrationType":"NA","ciqName":"VZW_NYM_FSU_CIQ_v1.36_20221026.xlsx","testName":"WFM_78267001_FSU_BREWSTER_12212022_17_41_17","testDescription":"","lsmName":"4G_FSU_22A","lsmVersion":"22.A.0","useCase":null,"status":"Completed","userName":"superadmin","creationDate":"2022-12-21 17:41:17","customerId":0,"id":1370,"result":null,"fromDate":null,"toDate":null,"migrationRunTestModel":null,"negrowRunTestModel":null,"preAuditMigrationRunTestModel":null,"neStatusRunTestModel":null,"ranAtpRunTestModel":null,"preMigStatus":"Completed","neGrowStatus":"Failure","inputRequired":"NEGROW","preMigrationScriptPath":null,"preMigrationFileName":null,"filePathPre":null,"fileNamePre":null,"commPath":"/home/user/Samsung/SMART/Customer/38/PreMigration/Output/VZW_NYM_FSU_CIQ_v1.36_20221026/78267001/COMMISSIONING_SCRIPT/","envPath":"/home/user/Samsung/SMART/Customer/38/PreMigration/Output/VZW_NYM_FSU_CIQ_v1.36_20221026/78267001/ENV/","csvPath":"/home/user/Samsung/SMART/Customer/38/PreMigration/Output/VZW_NYM_FSU_CIQ_v1.36_20221026/78267001/CSV/","commZipName":"COMMISSION_SCRIPT_78267001_FSU_BREWSTER_12212022_17_41_20.zip","envZipName":"ENV_78267001_FSU_BREWSTER_12212022_17_41_17.zip","csvZipName":"CSV_78267001_FSU_BREWSTER_12212022_17_41_22.zip","preErrorFile":"/home/user/Samsung/SMART//ErrorLog/pre078267001_12212022_17_41_17.log","neGrowErrorFile":"/home/user/Samsung/SMART//ErrorLog/negrow078267001_12212022_17_41_17.log","preAuditErrorFile":"/home/user/Samsung/SMART//ErrorLog/preaudit078267001_12212022_17_41_17.log","neStatusErrorFile":"/home/user/Samsung/SMART//ErrorLog/nestatus078267001_12212022_17_41_17.log","migErrorFile":"/home/user/Samsung/SMART//ErrorLog/mig078267001_12212022_17_41_17.log","siteName":null,"siteReportStatus":"NotExecuted","siteReportId":null,"testInfo":null,"migStatus":"InputsRequired","postErrorFile":"/home/user/Samsung/SMART//ErrorLog/post078267001_12212022_17_41_17.log","postMigStatus":"InputsRequired","preAuditStatus":"InputsRequired","neStatus":"InputsRequired","postMigrationRunTestModel":null},{"customerDetailsEntity":null,"enbId":"78267001","neName":"78267001_FSU_BREWSTER","integrationType":"NA","ciqName":"VZW_NYM_FSU_CIQ_v1.36_20221026.xlsx","testName":"WFM_78267001_FSU_BREWSTER_12212022_12_53_30","testDescription":"","lsmName":"4G_FSU_22A","lsmVersion":"22.A.0","useCase":null,"status":"Completed","userName":"superadmin","creationDate":"2022-12-21 12:53:30","customerId":0,"id":1340,"result":null,"fromDate":null,"toDate":null,"migrationRunTestModel":null,"negrowRunTestModel":null,"preAuditMigrationRunTestModel":{"customerDetailsEntity":null,"migrationType":"PreMigration","failedScript":"false","migrationSubType":"PREAUDIT","checklistFileName":"","neName":"78267001_FSU_BREWSTER","progressStatus":"Completed","ciqName":"VZW_NYM_FSU_CIQ_v1.36_20221026.xlsx","outputFilepath":"Customer/38/PreMigration/78267001/PREAUDIT/Output/92939_output.txt","testName":"WFM_78267001_FSU_BREWSTER_12212022_15_53_50","testDescription":"Ran from WFM","lsmName":"4G_FSU_22A","lsmVersion":"22.A.0","useCase":"preaudit_usecase,","status":"Failure","userName":"superadmin","creationDate":"2022-12-21 15:53:57","useCaseDetails":null,"customerId":0,"id":8111,"wfmid":0,"useCaseSequence":0,"result":"AUDIT_78267001_2022-12-21_15:53:51.html","resultFilePath":"Customer/38/PreMigration/78267001/PREAUDIT/Output","fromDate":null,"toDate":null,"generateScriptPath":"Customer/38/PreMigration/78267001/PREAUDIT/GenerateScript/8111/13_4GFSU22A_preauditusecase_47991_Audit4GFSUPostChecks_175466.sh","migStatusDesc":null,"ovUpdateStatus":null,"testInfo":null,"name":null},"neStatusRunTestModel":{"customerDetailsEntity":null,"migrationType":"PreMigration","failedScript":"false","migrationSubType":"NESTATUS","checklistFileName":"","neName":"78267001_FSU_BREWSTER","progressStatus":"Completed","ciqName":"VZW_NYM_FSU_CIQ_v1.36_20221026.xlsx","outputFilepath":"Customer/38/PreMigration/78267001/NESTATUS/Output/64773_output.txt","testName":"WFM_78267001_FSU_BREWSTER_12212022_12_53_30","testDescription":"Ran from WFM","lsmName":"4G_FSU_22A","lsmVersion":"22.A.0","useCase":"Fsu_up,","status":"Failure","userName":"superadmin","creationDate":"2022-12-21 12:53:35","useCaseDetails":null,"customerId":0,"id":8101,"wfmid":0,"useCaseSequence":0,"result":"NEUPSTATUS_78267001_64773.txt","resultFilePath":"Customer/38/PreMigration/78267001/NESTATUS/Output/NEUPSTATUS_78267001_64773.txt","fromDate":null,"toDate":null,"generateScriptPath":"Customer/38/PreMigration/78267001/NESTATUS/GenerateScript/8101/14_4GFSU22A_Fsuup_47990_4GFSUNEUPUSECASE_175465.sh,Customer/38/PreMigration/78267001/NESTATUS/GenerateScript/8101/16_4GFSU22A_Fsuup_47990_4GFSUFIRMWARECHECK_180085.sh","migStatusDesc":null,"ovUpdateStatus":null,"testInfo":null,"name":null},"ranAtpRunTestModel":null,"preMigStatus":"NotExecuted","neGrowStatus":"NotExecuted","inputRequired":"NEGROW","preMigrationScriptPath":null,"preMigrationFileName":null,"filePathPre":null,"fileNamePre":null,"commPath":null,"envPath":null,"csvPath":null,"commZipName":null,"envZipName":null,"csvZipName":null,"preErrorFile":"/home/user/Samsung/SMART//ErrorLog/pre078267001_12212022_12_53_30.log","neGrowErrorFile":"/home/user/Samsung/SMART//ErrorLog/negrow078267001_12212022_12_53_30.log","preAuditErrorFile":"/home/user/Samsung/SMART//ErrorLog/preaudit078267001_12212022_12_53_30.log","neStatusErrorFile":"/home/user/Samsung/SMART//ErrorLog/nestatus078267001_12212022_12_53_30.log","migErrorFile":"/home/user/Samsung/SMART//ErrorLog/mig078267001_12212022_12_53_30.log","siteName":null,"siteReportStatus":"NotExecuted","siteReportId":null,"testInfo":null,"migStatus":"InputsRequired","postErrorFile":"/home/user/Samsung/SMART//ErrorLog/post078267001_12212022_12_53_30.log","postMigStatus":"InputsRequired","preAuditStatus":"Failure","neStatus":"Failure","postMigrationRunTestModel":null},{"customerDetailsEntity":null,"enbId":"81018001","neName":"81018001_FSU_SHORE_PARKWAY_2","integrationType":"NA","ciqName":"VZW_NYM_FSU_CIQ_v1.36_20221026.xlsx","testName":"WFM_81018001_FSU_SHORE_PARKWAY_2_12212022_12_22_07","testDescription":"","lsmName":"4G_FSU_NYM","lsmVersion":"21.B.0","useCase":null,"status":"Completed","userName":"superadmin","creationDate":"2022-12-21 12:22:07","customerId":0,"id":1339,"result":null,"fromDate":null,"toDate":null,"migrationRunTestModel":{"customerDetailsEntity":null,"migrationType":"Migration","failedScript":"true","migrationSubType":"PreCheck","checklistFileName":"","neName":"81018001_FSU_SHORE_PARKWAY_2","progressStatus":"Completed","ciqName":"VZW_NYM_FSU_CIQ_v1.36_20221026.xlsx","outputFilepath":"Customer/38/Migration/81018001/PreCheck/Output/92704_output.txt","testName":"WFM_81018001_FSU_SHORE_PARKWAY_2_12212022_12_23_55","testDescription":"Ran from WFM","lsmName":"4G_FSU_NYM","lsmVersion":"21.B.0","useCase":"CommissionScriptUsecase_81018001_12212022,","status":"Failure","userName":"superadmin","creationDate":"2022-12-21 12:23:58","useCaseDetails":null,"customerId":0,"id":8093,"wfmid":0,"useCaseSequence":0,"result":"","resultFilePath":"","fromDate":null,"toDate":null,"generateScriptPath":"Customer/38/Migration/81018001/PreCheck/GenerateScript/8093/1_4GFSUNYM_CommissionScriptUsecase8101800112212022_49980_BASHCOMMadministrativeunlockCOMMADMINSTATESET81018001FSUSHOREPARKWAY212212022_180892.sh","migStatusDesc":null,"ovUpdateStatus":null,"testInfo":null,"name":null},"negrowRunTestModel":{"customerDetailsEntity":null,"migrationType":"PreMigration","failedScript":"true","migrationSubType":"NEGrow","checklistFileName":"","neName":"81018001_FSU_SHORE_PARKWAY_2","progressStatus":"Completed","ciqName":"VZW_NYM_FSU_CIQ_v1.36_20221026.xlsx","outputFilepath":"Customer/38/PreMigration/81018001/NEGrow/Output/95243_output.txt","testName":"WFM_81018001_FSU_SHORE_PARKWAY_2_12212022_12_22_11","testDescription":"Ran from WFM","lsmName":"4G_FSU_NYM","lsmVersion":"21.B.0","useCase":"GrowFSU_Usecase_81018001_12212022,","status":"Failure","userName":"superadmin","creationDate":"2022-12-21 12:22:15","useCaseDetails":null,"customerId":0,"id":8092,"wfmid":0,"useCaseSequence":0,"result":"","resultFilePath":"","fromDate":null,"toDate":null,"generateScriptPath":"Customer/38/PreMigration/81018001/NEGrow/GenerateScript/8092/1_4GFSUNYM_GrowFSUUsecase8101800112212022_49981_CSVFSUTEMPLATE81018001FSUSHOREPARKWAY21221202212212022_180894.sh","migStatusDesc":null,"ovUpdateStatus":null,"testInfo":null,"name":null},"preAuditMigrationRunTestModel":null,"neStatusRunTestModel":null,"ranAtpRunTestModel":null,"preMigStatus":"Completed","neGrowStatus":"Failure","inputRequired":"NESTATUS","preMigrationScriptPath":null,"preMigrationFileName":null,"filePathPre":null,"fileNamePre":null,"commPath":"/home/user/Samsung/SMART/Customer/38/PreMigration/Output/VZW_NYM_FSU_CIQ_v1.36_20221026/81018001/COMMISSIONING_SCRIPT/","envPath":"/home/user/Samsung/SMART/Customer/38/PreMigration/Output/VZW_NYM_FSU_CIQ_v1.36_20221026/81018001/ENV/","csvPath":"/home/user/Samsung/SMART/Customer/38/PreMigration/Output/VZW_NYM_FSU_CIQ_v1.36_20221026/81018001/CSV/","commZipName":"COMMISSION_SCRIPT_81018001_FSU_SHORE_PARKWAY_2_12212022_12_22_09.zip","envZipName":"ENV_81018001_FSU_SHORE_PARKWAY_2_12212022_12_22_07.zip","csvZipName":"CSV_81018001_FSU_SHORE_PARKWAY_2_12212022_12_22_11.zip","preErrorFile":"/home/user/Samsung/SMART//ErrorLog/pre081018001_12212022_12_22_07.log","neGrowErrorFile":"/home/user/Samsung/SMART//ErrorLog/negrow081018001_12212022_12_22_07.log","preAuditErrorFile":"/home/user/Samsung/SMART//ErrorLog/preaudit081018001_12212022_12_22_07.log","neStatusErrorFile":"/home/user/Samsung/SMART//ErrorLog/nestatus081018001_12212022_12_22_07.log","migErrorFile":"/home/user/Samsung/SMART//ErrorLog/mig081018001_12212022_12_22_07.log","siteName":null,"siteReportStatus":"InputsRequired","siteReportId":null,"testInfo":null,"migStatus":"Failure","postErrorFile":"/home/user/Samsung/SMART//ErrorLog/post081018001_12212022_12_22_07.log","postMigStatus":"Failure","preAuditStatus":"InputsRequired","neStatus":"InputsRequired","postMigrationRunTestModel":{"customerDetailsEntity":null,"migrationType":"PostMigration","failedScript":"false","migrationSubType":"Audit","checklistFileName":"","neName":"81018001_FSU_SHORE_PARKWAY_2","progressStatus":"Completed","ciqName":"VZW_NYM_FSU_CIQ_v1.36_20221026.xlsx","outputFilepath":"Customer/38/PostMigration/81018001/Audit/Output/82695_output.txt","testName":"WFM_81018001_FSU_SHORE_PARKWAY_2_12212022_12_26_50","testDescription":"Ran from WFM","lsmName":"4G_FSU_NYM","lsmVersion":"21.B.0","useCase":"4G_FSU_Audit,","status":"Failure","userName":"superadmin","creationDate":"2022-12-21 12:26:56","useCaseDetails":null,"customerId":0,"id":8094,"wfmid":0,"useCaseSequence":0,"result":"AUDIT_81018001_2022-12-21_12:26:52.html","resultFilePath":"Customer/38/PostMigration/81018001/Audit/Output","fromDate":null,"toDate":null,"generateScriptPath":"Customer/38/PostMigration/81018001/Audit/GenerateScript/8094/10_4GFSUNYM_4GFSUAudit_39958_Audit4GeNBSerialNumber_138273.sh,Customer/38/PostMigration/81018001/Audit/GenerateScript/8094/11_4GFSUNYM_4GFSUAudit_39958_Audit4GeNBTxRfPower_138272.sh,Customer/38/PostMigration/81018001/Audit/GenerateScript/8094/12_4GFSUNYM_4GFSUAudit_39958_Audit4GFSUbackupdb_138271.sh,Customer/38/PostMigration/81018001/Audit/GenerateScript/8094/13_4GFSUNYM_4GFSUAudit_39958_Audit4GFSUPostChecks_138270.sh","migStatusDesc":null,"ovUpdateStatus":null,"testInfo":null,"name":null}},{"customerDetailsEntity":null,"enbId":"78267001","neName":"78267001_FSU_BREWSTER","integrationType":"NA","ciqName":"VZW_NYM_FSU_CIQ_v1.36_20221026.xlsx","testName":"WFM_78267001_FSU_BREWSTER_12212022_12_17_30","testDescription":"","lsmName":"4G_FSU_22A","lsmVersion":"22.A.0","useCase":null,"status":"Completed","userName":"superadmin","creationDate":"2022-12-21 12:17:30","customerId":0,"id":1338,"result":null,"fromDate":null,"toDate":null,"migrationRunTestModel":null,"negrowRunTestModel":null,"preAuditMigrationRunTestModel":{"customerDetailsEntity":null,"migrationType":"PreMigration","failedScript":"false","migrationSubType":"PREAUDIT","checklistFileName":"","neName":"78267001_FSU_BREWSTER","progressStatus":"Completed","ciqName":"VZW_NYM_FSU_CIQ_v1.36_20221026.xlsx","outputFilepath":"Customer/38/PreMigration/78267001/PREAUDIT/Output/55806_output.txt","testName":"WFM_78267001_FSU_BREWSTER_12212022_12_17_31","testDescription":"Ran from WFM","lsmName":"4G_FSU_22A","lsmVersion":"22.A.0","useCase":"preaudit_usecase,","status":"Failure","userName":"superadmin","creationDate":"2022-12-21 12:17:34","useCaseDetails":null,"customerId":0,"id":8091,"wfmid":0,"useCaseSequence":0,"result":"AUDIT_78267001_2022-12-21_12:17:31.html","resultFilePath":"Customer/38/PreMigration/78267001/PREAUDIT/Output","fromDate":null,"toDate":null,"generateScriptPath":"Customer/38/PreMigration/78267001/PREAUDIT/GenerateScript/8091/13_4GFSU22A_preauditusecase_47991_Audit4GFSUPostChecks_175466.sh","migStatusDesc":null,"ovUpdateStatus":null,"testInfo":null,"name":null},"neStatusRunTestModel":null,"ranAtpRunTestModel":null,"preMigStatus":"NotExecuted","neGrowStatus":"NotExecuted","inputRequired":"NEGROW","preMigrationScriptPath":null,"preMigrationFileName":null,"filePathPre":null,"fileNamePre":null,"commPath":null,"envPath":null,"csvPath":null,"commZipName":null,"envZipName":null,"csvZipName":null,"preErrorFile":"/home/user/Samsung/SMART//ErrorLog/pre078267001_12212022_12_17_30.log","neGrowErrorFile":"/home/user/Samsung/SMART//ErrorLog/negrow078267001_12212022_12_17_30.log","preAuditErrorFile":"/home/user/Samsung/SMART//ErrorLog/preaudit078267001_12212022_12_17_30.log","neStatusErrorFile":"/home/user/Samsung/SMART//ErrorLog/nestatus078267001_12212022_12_17_30.log","migErrorFile":"/home/user/Samsung/SMART//ErrorLog/mig078267001_12212022_12_17_30.log","siteName":null,"siteReportStatus":"NotExecuted","siteReportId":null,"testInfo":null,"migStatus":"InputsRequired","postErrorFile":"/home/user/Samsung/SMART//ErrorLog/post078267001_12212022_12_17_30.log","postMigStatus":"InputsRequired","preAuditStatus":"Failure","neStatus":"NotExecuted","postMigrationRunTestModel":null},{"customerDetailsEntity":null,"enbId":"78267001","neName":"78267001_FSU_BREWSTER","integrationType":"NA","ciqName":"VZW_NYM_FSU_CIQ_v1.36_20221026.xlsx","testName":"WFM_78267001_FSU_BREWSTER_12212022_12_16_03","testDescription":"","lsmName":"4G_FSU_22A","lsmVersion":"22.A.0","useCase":null,"status":"Completed","userName":"superadmin","creationDate":"2022-12-21 12:16:03","customerId":0,"id":1337,"result":null,"fromDate":null,"toDate":null,"migrationRunTestModel":null,"negrowRunTestModel":null,"preAuditMigrationRunTestModel":null,"neStatusRunTestModel":null,"ranAtpRunTestModel":null,"preMigStatus":"NotExecuted","neGrowStatus":"NotExecuted","inputRequired":"NEGROW","preMigrationScriptPath":null,"preMigrationFileName":null,"filePathPre":null,"fileNamePre":null,"commPath":null,"envPath":null,"csvPath":null,"commZipName":null,"envZipName":null,"csvZipName":null,"preErrorFile":"/home/user/Samsung/SMART//ErrorLog/pre078267001_12212022_12_16_03.log","neGrowErrorFile":"/home/user/Samsung/SMART//ErrorLog/negrow078267001_12212022_12_16_03.log","preAuditErrorFile":"/home/user/Samsung/SMART//ErrorLog/preaudit078267001_12212022_12_16_03.log","neStatusErrorFile":"/home/user/Samsung/SMART//ErrorLog/nestatus078267001_12212022_12_16_03.log","migErrorFile":"/home/user/Samsung/SMART//ErrorLog/mig078267001_12212022_12_16_03.log","siteName":null,"siteReportStatus":"InputsRequired","siteReportId":null,"testInfo":null,"migStatus":"NotExecuted","postErrorFile":"/home/user/Samsung/SMART//ErrorLog/post078267001_12212022_12_16_03.log","postMigStatus":"Failure","preAuditStatus":"NotExecuted","neStatus":"NotExecuted","postMigrationRunTestModel":{"customerDetailsEntity":null,"migrationType":"PostMigration","failedScript":"false","migrationSubType":"Audit","checklistFileName":"","neName":"78267001_FSU_BREWSTER","progressStatus":"Completed","ciqName":"VZW_NYM_FSU_CIQ_v1.36_20221026.xlsx","outputFilepath":"Customer/38/PostMigration/78267001/Audit/Output/74216_output.txt","testName":"WFM_78267001_FSU_BREWSTER_12212022_12_16_03","testDescription":"Ran from WFM","lsmName":"4G_FSU_22A","lsmVersion":"22.A.0","useCase":"sfp_fsu,","status":"Failure","userName":"superadmin","creationDate":"2022-12-21 12:16:08","useCaseDetails":null,"customerId":0,"id":8090,"wfmid":0,"useCaseSequence":0,"result":"AUDIT_78267001_2022-12-21_12:16:04.html","resultFilePath":"Customer/38/PostMigration/78267001/Audit/Output","fromDate":null,"toDate":null,"generateScriptPath":"Customer/38/PostMigration/78267001/Audit/GenerateScript/8090/12_4GFSU22A_sfpfsu_48783_Audit4GFSUSfpInventory_178252.sh,Customer/38/PostMigration/78267001/Audit/GenerateScript/8090/13_4GFSU22A_sfpfsu_48783_Audit4GFSUPostChecks_138270.sh","migStatusDesc":null,"ovUpdateStatus":null,"testInfo":null,"name":null}},{"customerDetailsEntity":null,"enbId":"78267001","neName":"78267001_FSU_BREWSTER","integrationType":"NA","ciqName":"VZW_NYM_FSU_CIQ_v1.36_20221026.xlsx","testName":"WFM_78267001_FSU_BREWSTER_12192022_17_18_44","testDescription":"","lsmName":"4G_FSU_22A","lsmVersion":"22.A.0","useCase":null,"status":"Completed","userName":"superadmin","creationDate":"2022-12-19 17:18:44","customerId":0,"id":1331,"result":null,"fromDate":null,"toDate":null,"migrationRunTestModel":null,"negrowRunTestModel":null,"preAuditMigrationRunTestModel":null,"neStatusRunTestModel":null,"ranAtpRunTestModel":null,"preMigStatus":"NotExecuted","neGrowStatus":"NotExecuted","inputRequired":"NEGROW","preMigrationScriptPath":null,"preMigrationFileName":null,"filePathPre":null,"fileNamePre":null,"commPath":null,"envPath":null,"csvPath":null,"commZipName":null,"envZipName":null,"csvZipName":null,"preErrorFile":"/home/user/Samsung/SMART//ErrorLog/pre078267001_12192022_17_18_44.log","neGrowErrorFile":"/home/user/Samsung/SMART//ErrorLog/negrow078267001_12192022_17_18_44.log","preAuditErrorFile":"/home/user/Samsung/SMART//ErrorLog/preaudit078267001_12192022_17_18_44.log","neStatusErrorFile":"/home/user/Samsung/SMART//ErrorLog/nestatus078267001_12192022_17_18_44.log","migErrorFile":"/home/user/Samsung/SMART//ErrorLog/mig078267001_12192022_17_18_44.log","siteName":null,"siteReportStatus":"InputsRequired","siteReportId":null,"testInfo":null,"migStatus":"NotExecuted","postErrorFile":"/home/user/Samsung/SMART//ErrorLog/post078267001_12192022_17_18_44.log","postMigStatus":"Failure","preAuditStatus":"NotExecuted","neStatus":"NotExecuted","postMigrationRunTestModel":{"customerDetailsEntity":null,"migrationType":"PostMigration","failedScript":"false","migrationSubType":"Audit","checklistFileName":"","neName":"78267001_FSU_BREWSTER","progressStatus":"Completed","ciqName":"VZW_NYM_FSU_CIQ_v1.36_20221026.xlsx","outputFilepath":"Customer/38/PostMigration/78267001/Audit/Output/93579_output.txt","testName":"WFM_78267001_FSU_BREWSTER_12192022_17_18_45","testDescription":"Ran from WFM","lsmName":"4G_FSU_22A","lsmVersion":"22.A.0","useCase":"sfp_fsu,","status":"Failure","userName":"superadmin","creationDate":"2022-12-19 17:18:58","useCaseDetails":null,"customerId":0,"id":8076,"wfmid":0,"useCaseSequence":0,"result":"AUDIT_78267001_2022-12-19_17:18:55.html","resultFilePath":"Customer/38/PostMigration/78267001/Audit/Output","fromDate":null,"toDate":null,"generateScriptPath":"Customer/38/PostMigration/78267001/Audit/GenerateScript/8076/12_4GFSU22A_sfpfsu_48783_Audit4GFSUSfpInventory_178252.sh,Customer/38/PostMigration/78267001/Audit/GenerateScript/8076/13_4GFSU22A_sfpfsu_48783_Audit4GFSUPostChecks_138270.sh","migStatusDesc":null,"ovUpdateStatus":null,"testInfo":null,"name":null}},{"customerDetailsEntity":null,"enbId":"85298001","neName":"85298001_FSU_HAUPPAUGE","integrationType":"NA","ciqName":"VZW_NYM_FSU_CIQ_v1.36_20221026.xlsx","testName":"WFM_85298001_FSU_HAUPPAUGE_12192022_16_13_17","testDescription":"","lsmName":"4G_FSU_NYM","lsmVersion":"21.B.0","useCase":null,"status":"Completed","userName":"superadmin","creationDate":"2022-12-19 16:13:17","customerId":0,"id":1306,"result":null,"fromDate":null,"toDate":null,"migrationRunTestModel":null,"negrowRunTestModel":null,"preAuditMigrationRunTestModel":null,"neStatusRunTestModel":null,"ranAtpRunTestModel":null,"preMigStatus":"NotExecuted","neGrowStatus":"NotExecuted","inputRequired":"NEGROW","preMigrationScriptPath":null,"preMigrationFileName":null,"filePathPre":null,"fileNamePre":null,"commPath":null,"envPath":null,"csvPath":null,"commZipName":null,"envZipName":null,"csvZipName":null,"preErrorFile":"/home/user/Samsung/SMART//ErrorLog/pre085298001_12192022_16_13_17.log","neGrowErrorFile":"/home/user/Samsung/SMART//ErrorLog/negrow085298001_12192022_16_13_17.log","preAuditErrorFile":"/home/user/Samsung/SMART//ErrorLog/preaudit085298001_12192022_16_13_17.log","neStatusErrorFile":"/home/user/Samsung/SMART//ErrorLog/nestatus085298001_12192022_16_13_17.log","migErrorFile":"/home/user/Samsung/SMART//ErrorLog/mig085298001_12192022_16_13_17.log","siteName":null,"siteReportStatus":"InputsRequired","siteReportId":null,"testInfo":null,"migStatus":"NotExecuted","postErrorFile":"/home/user/Samsung/SMART//ErrorLog/post085298001_12192022_16_13_17.log","postMigStatus":"Failure","preAuditStatus":"NotExecuted","neStatus":"NotExecuted","postMigrationRunTestModel":{"customerDetailsEntity":null,"migrationType":"PostMigration","failedScript":"false","migrationSubType":"Audit","checklistFileName":"","neName":"85298001_FSU_HAUPPAUGE","progressStatus":"Completed","ciqName":"VZW_NYM_FSU_CIQ_v1.36_20221026.xlsx","outputFilepath":"Customer/38/PostMigration/85298001/Audit/Output/78126_output.txt","testName":"WFM_85298001_FSU_HAUPPAUGE_12192022_16_13_18","testDescription":"Ran from WFM","lsmName":"4G_FSU_NYM","lsmVersion":"21.B.0","useCase":"4G_FSU_Audit,","status":"Failure","userName":"superadmin","creationDate":"2022-12-19 16:13:28","useCaseDetails":null,"customerId":0,"id":8052,"wfmid":0,"useCaseSequence":0,"result":"AUDIT_85298001_2022-12-19_16:13:23.html","resultFilePath":"Customer/38/PostMigration/85298001/Audit/Output","fromDate":null,"toDate":null,"generateScriptPath":"Customer/38/PostMigration/85298001/Audit/GenerateScript/8052/10_4GFSUNYM_4GFSUAudit_39958_Audit4GeNBSerialNumber_138273.sh,Customer/38/PostMigration/85298001/Audit/GenerateScript/8052/11_4GFSUNYM_4GFSUAudit_39958_Audit4GeNBTxRfPower_138272.sh,Customer/38/PostMigration/85298001/Audit/GenerateScript/8052/12_4GFSUNYM_4GFSUAudit_39958_Audit4GFSUbackupdb_138271.sh,Customer/38/PostMigration/85298001/Audit/GenerateScript/8052/13_4GFSUNYM_4GFSUAudit_39958_Audit4GFSUPostChecks_138270.sh","migStatusDesc":null,"ovUpdateStatus":null,"testInfo":null,"name":null}},{"customerDetailsEntity":null,"enbId":"81308001","neName":"81308001_BERGEN_BEACH_L","integrationType":"NA","ciqName":"VZW_NYM_FSU_CIQ_v1.36_20221026.xlsx","testName":"WFM_81308001_BERGEN_BEACH_L_12192022_16_13_02","testDescription":"","lsmName":"4G_FSU_22A","lsmVersion":"22.A.0","useCase":null,"status":"Completed","userName":"superadmin","creationDate":"2022-12-19 16:13:02","customerId":0,"id":1303,"result":null,"fromDate":null,"toDate":null,"migrationRunTestModel":null,"negrowRunTestModel":{"customerDetailsEntity":null,"migrationType":"PreMigration","failedScript":"true","migrationSubType":"NEGrow","checklistFileName":"","neName":"81308001_BERGEN_BEACH_L","progressStatus":"Completed","ciqName":"VZW_NYM_FSU_CIQ_v1.36_20221026.xlsx","outputFilepath":"Customer/38/PreMigration/81308001/NEGrow/Output/92165_output.txt","testName":"WFM_81308001_BERGEN_BEACH_L_12192022_16_13_09","testDescription":"Ran from WFM","lsmName":"4G_FSU_22A","lsmVersion":"22.A.0","useCase":"GrowFSU_Usecase_81308001_12192022,","status":"Failure","userName":"superadmin","creationDate":"2022-12-19 16:13:13","useCaseDetails":null,"customerId":0,"id":8049,"wfmid":0,"useCaseSequence":0,"result":"","resultFilePath":"","fromDate":null,"toDate":null,"generateScriptPath":"Customer/38/PreMigration/81308001/NEGrow/GenerateScript/8049/1_4GFSU22A_GrowFSUUsecase8130800112192022_49838_CSVFSUTEMPLATE81308001BERGENBEACHL1219202212192022_180611.sh","migStatusDesc":null,"ovUpdateStatus":null,"testInfo":null,"name":null},"preAuditMigrationRunTestModel":null,"neStatusRunTestModel":null,"ranAtpRunTestModel":null,"preMigStatus":"Completed","neGrowStatus":"Failure","inputRequired":"NESTATUS","preMigrationScriptPath":null,"preMigrationFileName":null,"filePathPre":null,"fileNamePre":null,"commPath":"/home/user/Samsung/SMART/Customer/38/PreMigration/Output/VZW_NYM_FSU_CIQ_v1.36_20221026/81308001/COMMISSIONING_SCRIPT/","envPath":"/home/user/Samsung/SMART/Customer/38/PreMigration/Output/VZW_NYM_FSU_CIQ_v1.36_20221026/81308001/ENV/","csvPath":"/home/user/Samsung/SMART/Customer/38/PreMigration/Output/VZW_NYM_FSU_CIQ_v1.36_20221026/81308001/CSV/","commZipName":"COMMISSION_SCRIPT_81308001_BERGEN_BEACH_L_12192022_16_13_04.zip","envZipName":"ENV_81308001_BERGEN_BEACH_L_12192022_16_13_02.zip","csvZipName":"CSV_81308001_BERGEN_BEACH_L_12192022_16_13_08.zip","preErrorFile":"/home/user/Samsung/SMART//ErrorLog/pre081308001_12192022_16_13_02.log","neGrowErrorFile":"/home/user/Samsung/SMART//ErrorLog/negrow081308001_12192022_16_13_02.log","preAuditErrorFile":"/home/user/Samsung/SMART//ErrorLog/preaudit081308001_12192022_16_13_02.log","neStatusErrorFile":"/home/user/Samsung/SMART//ErrorLog/nestatus081308001_12192022_16_13_02.log","migErrorFile":"/home/user/Samsung/SMART//ErrorLog/mig081308001_12192022_16_13_02.log","siteName":null,"siteReportStatus":"NotExecuted","siteReportId":null,"testInfo":null,"migStatus":"InputsRequired","postErrorFile":"/home/user/Samsung/SMART//ErrorLog/post081308001_12192022_16_13_02.log","postMigStatus":"InputsRequired","preAuditStatus":"InputsRequired","neStatus":"InputsRequired","postMigrationRunTestModel":null},{"customerDetailsEntity":null,"enbId":"78267001","neName":"78267001_FSU_BREWSTER","integrationType":"NA","ciqName":"VZW_NYM_FSU_CIQ_v1.36_20221026.xlsx","testName":"WFM_78267001_FSU_BREWSTER_12192022_16_13_02","testDescription":"","lsmName":"4G_FSU_22A","lsmVersion":"22.A.0","useCase":null,"status":"Completed","userName":"superadmin","creationDate":"2022-12-19 16:13:02","customerId":0,"id":1304,"result":null,"fromDate":null,"toDate":null,"migrationRunTestModel":null,"negrowRunTestModel":{"customerDetailsEntity":null,"migrationType":"PreMigration","failedScript":"true","migrationSubType":"NEGrow","checklistFileName":"","neName":"78267001_FSU_BREWSTER","progressStatus":"Completed","ciqName":"VZW_NYM_FSU_CIQ_v1.36_20221026.xlsx","outputFilepath":"Customer/38/PreMigration/78267001/NEGrow/Output/92165_output.txt","testName":"WFM_78267001_FSU_BREWSTER_12192022_16_13_10","testDescription":"Ran from WFM","lsmName":"4G_FSU_22A","lsmVersion":"22.A.0","useCase":"GrowFSU_Usecase_78267001_12192022,","status":"Failure","userName":"superadmin","creationDate":"2022-12-19 16:13:13","useCaseDetails":null,"customerId":0,"id":8051,"wfmid":0,"useCaseSequence":0,"result":"","resultFilePath":"","fromDate":null,"toDate":null,"generateScriptPath":"Customer/38/PreMigration/78267001/NEGrow/GenerateScript/8051/1_4GFSU22A_GrowFSUUsecase7826700112192022_49840_CSVFSUTEMPLATE78267001FSUBREWSTER1219202212192022_180613.sh","migStatusDesc":null,"ovUpdateStatus":null,"testInfo":null,"name":null},"preAuditMigrationRunTestModel":null,"neStatusRunTestModel":null,"ranAtpRunTestModel":null,"preMigStatus":"Completed","neGrowStatus":"Failure","inputRequired":"NESTATUS","preMigrationScriptPath":null,"preMigrationFileName":null,"filePathPre":null,"fileNamePre":null,"commPath":"/home/user/Samsung/SMART/Customer/38/PreMigration/Output/VZW_NYM_FSU_CIQ_v1.36_20221026/78267001/COMMISSIONING_SCRIPT/","envPath":"/home/user/Samsung/SMART/Customer/38/PreMigration/Output/VZW_NYM_FSU_CIQ_v1.36_20221026/78267001/ENV/","csvPath":"/home/user/Samsung/SMART/Customer/38/PreMigration/Output/VZW_NYM_FSU_CIQ_v1.36_20221026/78267001/CSV/","commZipName":"COMMISSION_SCRIPT_78267001_FSU_BREWSTER_12192022_16_13_04.zip","envZipName":"ENV_78267001_FSU_BREWSTER_12192022_16_13_02.zip","csvZipName":"CSV_78267001_FSU_BREWSTER_12192022_16_13_10.zip","preErrorFile":"/home/user/Samsung/SMART//ErrorLog/pre078267001_12192022_16_13_02.log","neGrowErrorFile":"/home/user/Samsung/SMART//ErrorLog/negrow078267001_12192022_16_13_02.log","preAuditErrorFile":"/home/user/Samsung/SMART//ErrorLog/preaudit078267001_12192022_16_13_02.log","neStatusErrorFile":"/home/user/Samsung/SMART//ErrorLog/nestatus078267001_12192022_16_13_02.log","migErrorFile":"/home/user/Samsung/SMART//ErrorLog/mig078267001_12192022_16_13_02.log","siteName":null,"siteReportStatus":"NotExecuted","siteReportId":null,"testInfo":null,"migStatus":"InputsRequired","postErrorFile":"/home/user/Samsung/SMART//ErrorLog/post078267001_12192022_16_13_02.log","postMigStatus":"InputsRequired","preAuditStatus":"InputsRequired","neStatus":"InputsRequired","postMigrationRunTestModel":null}],"isInProgress":false,"status":"SUCCESS","neStatueuseCaseLst":[{"useCaseName":"Fsu_up","useCaseId":47990,"ucSleepInterval":"1000","executionSequence":4,"scripts":[{"scriptId":175465,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"4GFSU_NEUP_USECASE.xml","scriptExeSequence":14},{"scriptId":180085,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"4GFSU_FIRMWARECHECK.xml","scriptExeSequence":16}]}]}')
                    this.ovUpdate = this.programName == 'VZN-4G-USM-LIVE' ? true : false;
                    this.migrationStrategy = this.programName == 'VZN-4G-USM-LIVE' ? 'Legacy IP' : null;
                    this.totalPages = this.tableData.pageCount;
                    //this.nwTypeDetails = Object.keys(this.tableData.nwTypeInfo);
                    this.smVersion = this.tableData.smVersion;
                    this.ciqList = this.tableData.getCiqList;                    
                    if(this.ciqList.length >0){
                        // this.ciqDetails = this.ciqList[0];
                        let getSelectedCIQ = JSON.parse(sessionStorage.getItem("selectedCIQ"));
                        if (getSelectedCIQ) {
                            this.ciqDetails = getSelectedCIQ;
                        }
                        else {
                            this.ciqDetails = this.ciqList[0];
                            // Update Session storage for selectedCIQ
                            this.sharedService.updateSelectedCIQInSessionStorage(this.ciqDetails);
                        }
                        this.getNEList(this.ciqDetails);
                    }
                    this.getUseCase();
                    this.getNeGrowUseCase();
                    // this.getPostMigUseCase(this.tableData.postmigusecaselist);
                    // this.getPostMigUseCases(this.tableData.postmigusecaselist);
                    if(this.programName =="VZN-4G-FSU")
                       this.selectedNeVersion="19.A.0"
                    if(this.programName =="VZN-5G-DSS")
                        this.disableDropdownNG = false;
                    else
                        this.disableDropdownNG = false;

                    if(this.programName =="VZN-4G-FSU")
                        this.disableDropdown = false;
                    else
                        this.disableDropdown = false;
                
                    this.selectedNEItems = null;
                    //this.selectedItems =[];
                    this.ckeckedOrNot = true;
                    this.rfScriptFlag = true;
                    this.isItInProgress=this.tableData.isInProgress;
                    //console.log(this.isItInProgress);
                    let pageCount = [];
                    for (var i = 1; i <= this.tableData.pageCount; i++) {
                        pageCount.push(i);
                    }
                    this.pageRenge = pageCount;
                    this.pager = this.getPager(this.totalPages * this.pageSize, this.currentPage, this.pageRenge);


                    // To display table data
                    if (this.tableData.runTestTableDetails.length != 0) {
                        this.showNoDataFound = false;
                        this.tableShowHide = true;
                        setTimeout(() => {
                            let tableWidth = document.getElementById('runTestScrollHead').scrollWidth;
                            $(".runTestWrapper .scrollBody table#runTestScrollBody").css("min-width", (tableWidth) + "px");
                            $(".runTestWrapper .scrollHead table#runTestScrollHead").css("width", tableWidth + "px");


                            $(".runTestWrapper .scrollBody").on("scroll", function (event) {
                               // $(".formEditRow #formWrapper").css("margin-left", (event.target.scrollLeft - 5) + "px");
                                $(".runTestRow .form-control-fixed").css("right", (event.target.scrollLeft * -1) + "px");
                                $(".runTestWrapper .scrollHead table#runTestScrollHead").css("margin-left", (event.target.scrollLeft * -1) + "px");
                            });
                            
                                $(".runTestWrapper .scrollBody").css("max-height",(this.tableDataHeight - 10) + "px");
                            
                        }, 0);

                        // Scan the table data for an inputRequired status, so to show the popup for UseCases
                        // this.scanTableRow();
                    } else {
                        this.tableShowHide = false;
                        this.showNoDataFound = true;
                    }

                    if(this.isItInProgress)
                    {
                        // console.log("here");
                        //this.count=0;
 
                        // On every 10s refresh the table
                        if(!this.interval) {
                        //    console.log("here inside");

                            this.interval = setInterval( () => {
                                this.updateRunTestTable();
                            }, 10000 );
                        }
    
                    }
                    else
                    {
                        clearInterval( this.interval );
                        this.interval=null;

                        //console.log("there");
    
                    }
                    this.multipleDuo = (this.programName == 'VZN-4G-USM-LIVE' || this.programName == 'VZN-5G-MM') ? true : false;
                }, 1000); */
                //Please Comment while checkIn


            });
    }
    showFailureLog(content,key,type) {
        this.showWideRunningLog = false;
        this.rowData = {
            "id": key.id,
            "runTestName": key.testName,
            "checklistFileName": key.checklistFileName,
            "ciqName": key.ciqName,
            "sheetName": "",
            "neName":key.neName
        }
        
        this.showLoader = true;
        this.runtestService.getFailureLogs(this.sharedService.createServiceToken(), key,type)
            .subscribe(
                data => {
                    setTimeout(() => {
                        let jsonStatue = data.json();
                        this.showLoader = false;

                        if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
                            if(!this.sessionExpiredModalBlock){
                            this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
                            }
                        } else {

                            if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {
                                if (jsonStatue.status == "SUCCESS") {
                                    this.noCheckListDataVisibility = false;
                                    
                                    let index = 0;
                                    this.runningLogs = jsonStatue.errorLogs;
                                    // this.runningLogModalBlock = this.modalService.open(content, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView' });
                                    this.showRunningLogLauncher = false;
                                    this.showRunningLogContent = true;
                                    
                                    setTimeout(() => {
                                        var objDiv = document.getElementById("runningLogsPre");
                                        objDiv.scrollTop = objDiv.scrollHeight;
                                    }, 100);
                                } else {
                                    this.showLoader = false;
                                   // this.displayModel(jsonStatue.reason, "failureIcon");
                                }

                            }
                        }

                    }, 1000);
                },
                error => {
                    //Please Comment while checkIn
                     /* setTimeout(() => {
                        this.showLoader = false;

                        //let jsonStatue = {"runningLog":"asklj sa kljsalk\n asjgas  hs \n ajsghas \n asjighas ","sessionId":"19088022","serviceToken":"66957","status":"SUCCESS","reason":""};
                        // let jsonStatue = {"runningLog":"spawn ssh -o StrictHostKeyChecking=no -oCheckHostIP=no user@127.0.0.1\nuser@127.0.0.1's password: \nWelcome to Ubuntu 16.04.3 LTS (GNU/Linux 4.10.0-28-generic x86_64)\n\n * Documentation:  https://help.ubuntu.com\n * Management:     https://landscape.canonical.com\n * Support:        https://ubuntu.com/advantage\n\n277 packages can be updated.\n9 updates are security updates.\n\nLast login: Mon Jun 24 17:33:29 2019 from 127.0.0.1\n\nssh -o StrictHostKeyChecking=no -oCheckHostIP=no user@127.0.0.1\nuser@user-OptiPlex-7010:~$ ssh -o StrictHostKeyChecking=no -oCheckHostIP=no u\n<ssh -o StrictHostKeyChecking=no -oCheckHostIP=no us                         \b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\ber@127.0.0.1\nuser@127.0.0.1's password: \nWelcome to Ubuntu 16.04.3 LTS (GNU/Linux 4.10.0-28-generic x86_64)\n\n * Documentation:  https://help.ubuntu.com\n * Management:     https://landscape.canonical.com\n * Support:        https://ubuntu.com/advantage\n\n277 packages can be updated.\n9 updates are security updates.\n\nLast login: Mon Jun 24 17:33:57 2019 from 127.0.0.1\n\nuser@user-OptiPlex-7010:~$ sudo su \n[sudo] password for user: \nroot@user-OptiPlex-7010:/home/user# \nspawn ssh -o StrictHostKeyChecking=no -oCheckHostIP=no user@127.0.0.1\nuser@127.0.0.1's password: \nWelcome to Ubuntu 16.04.3 LTS (GNU/Linux 4.10.0-28-generic x86_64)\n\n * Documentation:  https://help.ubuntu.com\n * Management:     https://landscape.canonical.com\n * Support:        https://ubuntu.com/advantage\n\n277 packages can be updated.\n9 updates are security updates.\n\nLast login: Mon Jun 24 17:34:07 2019 from 127.0.0.1\n\nssh -o StrictHostKeyChecking=no -oCheckHostIP=no user@127.0.0.1\nuser@user-OptiPlex-7010:~$ ssh -o StrictHostKeyChecking=no -oCheckHostIP=no u\n<ssh -o StrictHostKeyChecking=no -oCheckHostIP=no us                         \b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\ber@127.0.0.1\nuser@127.0.0.1's password: \nWelcome to Ubuntu 16.04.3 LTS (GNU/Linux 4.10.0-28-generic x86_64)\n\n * Documentation:  https://help.ubuntu.com\n * Management:     https://landscape.canonical.com\n * Support:        https://ubuntu.com/advantage\n\n277 packages can be updated.\n9 updates are security updates.\n\nLast login: Mon Jun 24 17:36:21 2019 from 127.0.0.1\n\nuser@user-OptiPlex-7010:~$ sudo su \n[sudo] password for user: \nroot@user-OptiPlex-7010:/home/user# \nroot@user-OptiPlex-7010:/home/user# ls\nbhuvanasmartmom.zip\nbhuvana.tar.gz\nCPAN-2.16\nCPAN-2.16.tar.gz\nDaily Activity.ods\nDesktop\nDocuments\nDownloads\nDrive\neclipse-workspace\nexamples.desktop\nfinal_5c853c7a3fc6200014a7df80_885019.mp4\nftp\nIMG_20181207_153237.jpg\nIMG_20181207_153310.jpg\nIMG_20181207_153526.jpg\nIMG_20181207_153537.jpg\njdk\nJson Reqest Response for migration Page .odt\nMongo.json\nMusic\nmvn\nperl5\nPictures\nPublic\npycharm-community-2018.3\nratAtpintput.log\nRCT\nRCT_workspace\nruntestserviceimplfor manultesting.txt\nsnap\ntag\ntask sheet.ods\nTemplates\ntomcat\nVideos\nroot@user-OptiPlex-7010:/home/user# df\nFilesystem     1K-blocks     Used Available Use% Mounted on\nudev             4040124        0   4040124   0% /dev\ntmpfs             812716     9592    803124   2% /run\n/dev/sda7      255590924 57783408 184801132  24% /\ntmpfs            4063564    94996   3968568   3% /dev/shm\ntmpfs               5120        4      5116   1% /run/lock\ntmpfs            4063564        0   4063564   0% /sys/fs/cgroup\n/dev/loop0          4864     4864         0 100% /snap/notepad-plus-plus/191\n/dev/loop1        135424   135424         0 100% /snap/postman/81\n/dev/loop2         93184    93184         0 100% /snap/core/6405\n/dev/loop3         91392    91392         0 100% /snap/core/6673\n/dev/loop4        133760   133760         0 100% /snap/postman/80\n/dev/loop5        450688   450688         0 100% /snap/wine-platform/101\n/dev/loop6        450816   450816         0 100% /snap/wine-platform/103\n/dev/loop7          3840     3840         0 100% /snap/notepad-plus-plus/193\n/dev/loop8         55040    55040         0 100% /snap/core18/731\n/dev/loop9        450816   450816         0 100% /snap/wine-platform/105\n/dev/loop10        55040    55040         0 100% /snap/core18/782\n/dev/loop11        36224    36224         0 100% /snap/gtk-common-themes/1198\n/dev/loop12        93312    93312         0 100% /snap/core/6531\n/dev/loop13         3840     3840         0 100% /snap/notepad-plus-plus/195\n/dev/loop14        35712    35712         0 100% /snap/gtk-common-themes/1122\n/dev/loop15       132864   132864         0 100% /snap/postman/73\n/dev/sda5         369639    65188    280848  19% /boot\ntmpfs             812716       64    812652   1% /run/user/1000\nroot@user-OptiPlex-7010:/home/user# pwd\n/home/user\nroot@user-OptiPlex-7010:/home/user# ls\nbhuvanasmartmom.zip\nbhuvana.tar.gz\nCPAN-2.16\nCPAN-2.16.tar.gz\nDaily Activity.ods\nDesktop\nDocuments\nDownloads\nDrive\neclipse-workspace\nexamples.desktop\nfinal_5c853c7a3fc6200014a7df80_885019.mp4\nftp\nIMG_20181207_153237.jpg\nIMG_20181207_153310.jpg\nIMG_20181207_153526.jpg\nIMG_20181207_153537.jpg\njdk\nJson Reqest Response for migration Page .odt\nMongo.json\nMusic\nmvn\nperl5\nPictures\nPublic\npycharm-community-2018.3\nratAtpintput.log\nRCT\nRCT_workspace\nruntestserviceimplfor manultesting.txt\nsnap\ntag\ntask sheet.ods\nTemplates\ntomcat\nVideos\nroot@user-OptiPlex-7010:/home/user# \nroot@user-OptiPlex-7010:/home/user# \n","sessionId":"31e4c050","serviceToken":"63263","status":"SUCCESS","reason":""};
                        let jsonStatue =
                        {
                            "errorLogs": "PreMigration\n-------------------------------\nUpload the RF script folder for the 7090012298\n",
                            "sessionId": "348da7c6",
                            "serviceToken": "53596",
                            "status": "SUCCESS"
                        };
                        if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {

                            this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });

                        } else {
                            if (jsonStatue.status == "SUCCESS") {                              
                                this.noCheckListDataVisibility = false;
                            
                                let index = 0;
                                this.runningLogs = jsonStatue.errorLogs;
                                // this.runningLogModalBlock = this.modalService.open(content, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView' });
                                this.showRunningLogLauncher = false;
                                this.showRunningLogContent = true;
                                
                                setTimeout(() => {
                                    var objDiv = document.getElementById("runningLogsPre");
                                    objDiv.scrollTop = objDiv.scrollHeight;
                                }, 100);
                            }
                            else {
                                this.showLoader = false;
                                //this.displayModel(jsonStatue.reason, "failureIcon");
                            }
                        }
                    }, 1000);  */
                    //Please Comment while checkIn
                });
    }
    updateRunTestTable(showLoader = false) {
        showLoader ? this.showLoader = true : "";
        this.runtestService.getRunTest(this.searchStatus, this.searchCriteria, this.paginationDetails, this.sharedService.createServiceToken(), this.commissionType, this.showMySites).subscribe(
            data => {
                let jsonStatue = data.json();
                if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
                if(!this.sessionExpiredModalBlock){
                    this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, {
                        keyboard: false,
                        backdrop: 'static',
                        size: 'lg',
                        windowClass: 'session-modal'
                    });
                }

                } else {

                    if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {

                        if (jsonStatue.status == "SUCCESS") {
                            showLoader ? this.showLoader = false : "";
                            setTimeout(() => {
                                this.tableData = jsonStatue;
                                this.totalPages = this.tableData.pageCount;

                                let pageCount = [];
                                for (var i = 1; i <= this.tableData.pageCount; i++) {
                                    pageCount.push(i);
                                }
                                this.pageRenge = pageCount;
                                this.pager = this.getPager(this.totalPages * this.pageSize, this.currentPage, this.pageRenge);
                                this.isItInProgress=this.tableData.isInProgress;
                                // To display table data
                                if (this.tableData.runTestTableDetails.length != 0) {

                                    this.showNoDataFound = false;
                                    this.tableShowHide = true;
                                    $(".runTestWrapper .scrollBody").scrollLeft(0 );
                                    setTimeout(() => {
                                        let tableWidth = document.getElementById('runTestScrollHead').scrollWidth;
                                        $(".runTestWrapper .scrollBody table#runTestScrollBody").css("min-width", (tableWidth) + "px");
                                        $(".runTestWrapper .scrollHead table#runTestScrollHead").css("width", tableWidth + "px");
            
            
                                        $(".runTestWrapper .scrollBody").on("scroll", function (event) {
                                           // $(".formEditRow #formWrapper").css("margin-left", (event.target.scrollLeft - 5) + "px");
                                            $(".runTestRow .form-control-fixed").css("right", (event.target.scrollLeft * -1) + "px");
                                            $(".runTestWrapper .scrollHead table#runTestScrollHead").css("margin-left", (event.target.scrollLeft * -1) + "px");
                                        });
                                        
                                            
                                        if (this.expandSerchRow == false) {
                                            setTimeout(() => {
                                                $(".runTestWrapper .scrollBody").css("max-height", (this.tableDataHeight - $("#tabContentWrapper").height()) + "px");
                                            }, 0);
                                        } else {
                                            setTimeout(() => {
                                                $(".runTestWrapper .scrollBody").css("max-height", (this.tableDataHeight - $("#tabContentWrapper").height()) + "px");
                                            }, 0);
                                        }
                                        

                                    }, 0);
                                } else {
                                    this.tableShowHide = false;
                                    this.showNoDataFound = true;
                                }

                                if(this.isItInProgress)
                                {
                                   // console.log("here");
                                    // On every 10s refresh the table
                                    if(!this.interval) {
                                        this.interval = setInterval( () => {
                                            this.updateRunTestTable();
                                        }, 10000 );
                                    }
                
                                }
                                else
                                {
                                   //console.log(this.interval);
            
                                    clearInterval( this.interval );
                                    this.interval=null;
                                   // console.log(this.interval, "there");
                
                                }

                            }, 100);

                        }
                    }
                }
            },
            error => {
                //Please Comment while checkIn  

                 /* setTimeout(() => {
                    //NoData
                    // this.tableData = JSON.parse('{"sessionId":"529ed523","serviceToken":"91652","status":"SUCCESS","pageCount":3,"nwType":["type1","type2"],"lsmDetails":{"lsmName1":["v123","v321"],"lsmName2":["v456","v654"]},"runTestTableDetails":[]}');
                    //Data
                    //this.tableData = JSON.parse('{"nwTypeInfo":{"5G":{"v1.0.2":{"LSM 2":[]}},"4G":{"v1.0.1":{"LSM 1":[{"useCaseName":"user_snap_contains_true","useCaseId":5,"executionSequence":1},{"useCaseName":"user_snap_contains_fail","useCaseId":6,"executionSequence":1},{"useCaseName":"9>4096_pass","useCaseId":7,"executionSequence":1},{"useCaseName":"4096>9_pass","useCaseId":8,"executionSequence":1},{"useCaseName":"4096>9_fail","useCaseId":9,"executionSequence":1},{"useCaseName":"cli","useCaseId":10,"executionSequence":1},{"useCaseName":"cliRulecontainsPass","useCaseId":11,"executionSequence":1},{"useCaseName":"use11","useCaseId":12,"executionSequence":111},{"useCaseName":"usecase112","useCaseId":13,"executionSequence":112},{"useCaseName":"usecase113","useCaseId":14,"executionSequence":113},{"useCaseName":"usecase114","useCaseId":15,"executionSequence":114},{"useCaseName":"usecase115","useCaseId":16,"executionSequence":115},{"useCaseName":"usecase116","useCaseId":17,"executionSequence":116},{"useCaseName":"asdfasf","useCaseId":18,"executionSequence":222},{"useCaseName":"file ","useCaseId":19,"executionSequence":1},{"useCaseName":"Twofile","useCaseId":20,"executionSequence":1},{"useCaseName":"mnb","useCaseId":21,"executionSequence":1}]}}},"pageCount":4,"sessionId":"396f8b99","serviceToken":"89673","runTestTableDetails":[{"id":51,"testName":"dfsf","testDescription":"dfggf","nwType":"4G","lsmName":"LSM 1","lsmVersion":"v1.0.1","useCaseDetails":[{"useCaseName":"user_snap_contains_true","useCaseId":5,"executionSequence":2},{"useCaseName":"user_snap_contains_fail","useCaseId":6,"executionSequence":1},{"useCaseName":"9>4096_pass","useCaseId":7,"executionSequence":5}],"useCase":"Twofile","status":"PASS","userName":null,"creationDate":"2019-01-30T05:22:18.000+0000","result":null,"customerId":2},{"id":50,"testName":"gdrhd","testDescription":"rtheh","nwType":"4G","lsmName":"LSM 1","lsmVersion":"v1.0.1","useCase":"mnb","status":"FAIL","userName":null,"creationDate":"2019-01-25T11:15:21.000+0000","result":null,"customerId":2},{"id":49,"testName":"tyjkl","testDescription":"uytruy","nwType":"4G","lsmName":"LSM 1","lsmVersion":"v1.0.1","useCase":"mnb","status":"FAIL","userName":null,"creationDate":"2019-01-25T11:08:27.000+0000","result":null,"customerId":2},{"id":48,"testName":"retye","testDescription":"gyjgh","nwType":"4G","lsmName":"LSM 1","lsmVersion":"v1.0.1","useCase":"mnb","status":"FAIL","userName":null,"creationDate":"2019-01-25T11:05:58.000+0000","result":null,"customerId":2},{"id":47,"testName":"zzxcv","testDescription":"zxcv","nwType":"4G","lsmName":"LSM 1","lsmVersion":"v1.0.1","useCase":"Twofile","status":"PASS","userName":null,"creationDate":"2019-01-25T10:58:26.000+0000","result":null,"customerId":2},{"id":46,"testName":"asdfasf","testDescription":"asdfasdf","nwType":"4G","lsmName":"LSM 1","lsmVersion":"v1.0.1","useCase":"Twofile","status":"PASS","userName":null,"creationDate":"2019-01-25T10:56:42.000+0000","result":null,"customerId":2},{"id":45,"testName":"asdfsf","testDescription":"xfgedhg","nwType":"4G","lsmName":"LSM 1","lsmVersion":"v1.0.1","useCase":"file ","status":"PASS","userName":null,"creationDate":"2019-01-25T10:49:51.000+0000","result":null,"customerId":2},{"id":44,"testName":"sdrgdsf","testDescription":"sdfgfsdg","nwType":"4G","lsmName":"LSM 1","lsmVersion":"v1.0.1","useCase":"file ","status":"PASS","userName":null,"creationDate":"2019-01-25T10:27:19.000+0000","result":null,"customerId":2},{"id":43,"testName":"sdfgsdg","testDescription":"sdfgsdg","nwType":"4G","lsmName":"LSM 1","lsmVersion":"v1.0.1","useCase":"file ","status":"FAIL","userName":null,"creationDate":"2019-01-25T10:26:44.000+0000","result":null,"customerId":2},{"id":42,"testName":"asfsadf","testDescription":"asdsgf","nwType":"4G","lsmName":"LSM 1","lsmVersion":"v1.0.1","useCase":"file ","status":"PASS","userName":null,"creationDate":"2019-01-25T10:20:27.000+0000","result":null,"customerId":2}],"status":"SUCCESS"}');
                    // this.tableData = JSON.parse('{"nwTypeInfo":{"4G":{"V1.2":{"lsm3":[]}},"3G":{"V1.1":{"lsm1":[{"useCaseName":"usecase1","useCaseId":1,"executionSequence":1}],"lsm2":[]}}},"pageCount":0,"sessionId":"282a5cb7","serviceToken":"71271","runTestTableDetails":[],"status":"SUCCESS"}');
                    this.tableData = JSON.parse('{"fromDate":"03/08/2019","pageCount":1,"isInProgress":true,"smVersion":[{"name":"1.2.3","id":1,"smNameList":[{"name":"SM","id":7},{"name":"bhuvana","useCaseList":[{"useCaseName":"test","useCaseId":1,"executionSequence":1},{"useCaseName":"rtyy","useCaseId":2,"executionSequence":1},{"useCaseName":"2scripts","useCaseId":3,"executionSequence":1}],"id":8},{"name":"oneLSM","id":9}]}],"toDate":"03/15/2019","useCaseList":[{"useCaseName":"test","useCaseId":1,"executionSequence":1},{"useCaseName":"rtyy","useCaseId":2,"executionSequence":1},{"useCaseName":"2scripts","useCaseId":3,"executionSequence":1}],"sessionId":"19c4c64","serviceToken":"53531","getCiqList":[{"id":49,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T16:24:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T16:41:30.000+0000","createdBy":"superadmin"},"ciqFileName":"UNY-NE-VZ_CIQ_Ver2.82_03122019.xlsx","ciqFilePath":"/home/user/RCT/rctsoftware/Samsung/SMART/Customer/23/PreMigration/Input/UNY-NE-VZ_CIQ_Ver2.82_03122019/CIQ/","scriptFileName":"1_58154_LEXINGTON_12_MA.zip,1_6003_networdata123.zip,1_6013_networkcohyutrr.zip,1_6203_networkconfig.zip","scriptFilePath":"/home/user/RCT/rctsoftware/Samsung/SMART/Customer/23/PreMigration/Input/UNY-NE-VZ_CIQ_Ver2.82_03122019/SCRIPT/","checklistFileName":"","checklistFilePath":"","ciqVersion":"ORIGINAL","fileSourceType":"FETCH","uploadBy":"superadmin","remarks":"","creationDate":"2019-03-12T20:38:02.000+0000"},{"id":42,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T16:24:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T16:41:30.000+0000","createdBy":"superadmin"},"ciqFileName":"UNY-NE-VZ_CIQ_Ver2.82_01282019.xlsx","ciqFilePath":"/home/user/RCT/rctsoftware/Samsung/SMART/Customer/23/PreMigration/Input/UNY-NE-VZ_CIQ_Ver2.82_01282019/CIQ/","scriptFileName":"1_1111_LEXINGTON_12_MA.zip,1_12345_LEXINGTON_12_MA.zip,1_58154_LEXINGTON_12_MA.zip","scriptFilePath":"/home/user/RCT/rctsoftware/Samsung/SMART/Customer/23/PreMigration/Input/UNY-NE-VZ_CIQ_Ver2.82_01282019/SCRIPT/","checklistFileName":"Verizon_vLSM_Migration Checklist_1.0.3.xlsx","checklistFilePath":"/home/user/RCT/rctsoftware/Samsung/SMART/Customer/23/PreMigration/Input/UNY-NE-VZ_CIQ_Ver2.82_01282019/CHECKLIST/","ciqVersion":"ORIGINAL","fileSourceType":"UPLOAD","uploadBy":"superadmin","remarks":"sa","creationDate":"2019-03-13T15:25:02.000+0000"}],"runTestTableDetails":[{"id":5,"customerDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T16:24:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T16:41:30.000+0000","createdBy":"superadmin"},"outputFilepath":"/home/path_of_the_file","migrationType":"migration","migrationSubType":"commission","checklistFileName":"Verizon_vLSM_Migration Checklist_1.0.3.xlsx","neName":"056001_BOSTON_CONVENTION_CTR_DAS2","progressStatus":"Completed","ciqName":"UNY-NE-VZ_CIQ_Ver2.82_01282019.xlsx","testName":"sdafafs","testDescription":"sdaff","lsmName":"bhuvana","lsmVersion":"1.2.3","useCase":"test","status":"PASS","userName":null,"creationDate":"2019-03-15T17:38:16.000+0000","useCaseDetails":"test?1?1","customerId":2,"useCaseSequence":0},{"id":4,"customerDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T16:24:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T16:41:30.000+0000","createdBy":"superadmin"},"migrationType":"migration","migrationSubType":"commission","checklistFileName":"Verizon_vLSM_Migration Checklist_1.0.3.xlsx","neName":"056001_BOSTON_CONVENTION_CTR_DAS2","progressStatus":"InProgress","ciqName":"UNY-NE-VZ_CIQ_Ver2.82_01282019.xlsx","testName":"sadf","testDescription":"asdf","lsmName":"bhuvana","lsmVersion":"1.2.3","useCase":"test","status":"PASS","userName":null,"creationDate":"2019-03-15T17:34:55.000+0000","useCaseDetails":"test?1?1","customerId":2,"useCaseSequence":0},{"id":3,"customerDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T16:24:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T16:41:30.000+0000","createdBy":"superadmin"},"migrationType":"migration","migrationSubType":"commission","checklistFileName":"Verizon_vLSM_Migration Checklist_1.0.3.xlsx","neName":"056001_BOSTON_CONVENTION_CTR_DAS2","progressStatus":"InProgress","ciqName":"UNY-NE-VZ_CIQ_Ver2.82_01282019.xlsx","testName":"sfd","testDescription":"safd","lsmName":"bhuvana","lsmVersion":"1.2.3","useCase":"test","status":"PASS","userName":null,"creationDate":"2019-03-15T17:30:41.000+0000","useCaseDetails":"test?1?1","customerId":2,"useCaseSequence":0},{"id":2,"customerDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T16:24:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T16:41:30.000+0000","createdBy":"superadmin"},"migrationType":"migration","migrationSubType":"commission","checklistFileName":"Verizon_vLSM_Migration Checklist_1.0.3.xlsx","neName":"056001_BOSTON_CONVENTION_CTR_DAS2","progressStatus":"InProgress","ciqName":"UNY-NE-VZ_CIQ_Ver2.82_01282019.xlsx","testName":"asdfsfda","testDescription":"sdaffas","lsmName":"bhuvana","lsmVersion":"1.2.3","useCase":"test","status":"PASS","userName":null,"creationDate":"2019-03-15T15:45:03.000+0000","useCaseDetails":"test?1?1","customerId":2,"useCaseSequence":0},{"id":1,"customerDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T16:24:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T16:41:30.000+0000","createdBy":"superadmin"},"migrationType":"migration","migrationSubType":"commission","checklistFileName":"Verizon_vLSM_Migration Checklist_1.0.3.xlsx","neName":"056001_BOSTON_CONVENTION_CTR_DAS2","progressStatus":"Completed","ciqName":"UNY-NE-VZ_CIQ_Ver2.82_01282019.xlsx","testName":"sdff","testDescription":"dsfg","lsmName":"oneLSM","lsmVersion":"1.2.3","useCase":"2scripts","status":"PASS","userName":null,"creationDate":"2019-03-15T15:37:58.000+0000","useCaseDetails":"2scripts?3?1","customerId":2,"useCaseSequence":0}],"status":"SUCCESS"}');
                    this.totalPages = this.tableData.pageCount;

                    let pageCount = [];
                    for (var i = 1; i <= this.tableData.pageCount; i++) {
                        pageCount.push(i);
                    }
                    this.pageRenge = pageCount;
                    this.pager = this.getPager(this.totalPages * this.pageSize, this.currentPage, this.pageRenge);
                    this.isItInProgress=this.tableData.isInProgress;

                    showLoader ? this.showLoader = false : "";

                //    this.count++;
                //    if(this.count<10)
                //    {
                //        this.isItInProgress=this.tableData.isInProgress;
                //        console.log("set true");

                //   }else{
                //        this.isItInProgress=false;
                //        console.log("set false");

                //    } 
                    // To display table data
                    if (this.tableData.runTestTableDetails.length != 0) {
                        this.showNoDataFound = false;
                        this.tableShowHide = true;
                        $(".runTestWrapper .scrollBody").scrollLeft(0 );
                        setTimeout(() => {
                            let tableWidth = document.getElementById('runTestScrollHead').scrollWidth;
                            $(".runTestWrapper .scrollBody table#runTestScrollBody").css("min-width", (tableWidth) + "px");
                            $(".runTestWrapper .scrollHead table#runTestScrollHead").css("width", tableWidth + "px");


                            $(".runTestWrapper .scrollBody").on("scroll", function (event) {
                               // $(".formEditRow #formWrapper").css("margin-left", (event.target.scrollLeft - 5) + "px");
                                $(".runTestRow .form-control-fixed").css("right", (event.target.scrollLeft * -1) + "px");
                                $(".runTestWrapper .scrollHead table#runTestScrollHead").css("margin-left", (event.target.scrollLeft * -1) + "px");
                            });
                            
                                $(".runTestWrapper .scrollBody").css("max-height",(this.tableDataHeight - 10) + "px");
                            
                            
                        }, 0);
                    } else {
                        this.tableShowHide = false;
                        this.showNoDataFound = true;
                    }

                    if(this.isItInProgress)
                    {
                       // console.log("here");
                        // On every 10s refresh the table
                        if(!this.interval) {
                            this.interval = setInterval( () => {
                                this.updateRunTestTable();
                            }, 10000 );
                        }
    
                    }
                    else
                    {
                       //console.log(this.interval);

                        clearInterval( this.interval );
                        this.interval=null;
                       // console.log(this.interval, "there");
    
                    }
                }, 1000); */
                //Please Comment while checkIn


            });
    }
    searchTabBind() {
        let searchCrtra = {"searchUseCase": "", "searchVersion": "", "searchSMName": "", "searchSeq": "", "createdBy": ""};
        this.searchCriteria = searchCrtra;
        this.selSearchSMName = "";
        this.selSearchCiqName = "";
        this.selSearchVer ="";
        this.setMenuHighlight("search");
        this.showMySites = false;
        this.searchBlock = true;
        this.createNewForm = false;
        this.searchStatus = 'load';
        this.tableShowHide = true;
        this.fromDt="";
        this.getRunTest();
        // Close if edit form is in open state
        if (this.currentEditRow != undefined) {
            this.currentEditRow.className = "editRow";
        }
      //  this.editableFormArray = [];
      
        
    }

    /* Used to add new Airline config details 
     * @param : event
     * @retun : null
     */

    createNewTabBind() {
        this.showNoDataFound = false;
        this.tableShowHide = false;       
        this.searchBlock = false;
        this.createNewForm = true;
        this.searchStatus = 'load';
        this.selSearchSMName = "";
        this.selSearchCiqName = "";
        this.selSearchVer ="";
        this.versionDetails="";
        this.ciqDetails = [];
        this.nameDetails="";
        this.lsmNameDetails=[];
        this.selectedNEItems=null;
        this.dropdownNEList=[];
        this.setMenuHighlight("createNew");      
        setTimeout(() => {
            validator.performValidation(event, this.validationData, "create");
        }, 10);
        this.getRunTest();
       
    }

    /*
     * Used to dispaly search result based on selected criteria
     * @param : event
     * @retun : null
     */

    searchWFM(event) {

        setTimeout(() => {
            $("#dataWrapper").find(".scrollBody").scrollLeft(0);
        }, 0);

        // validator.performValidation(event, this.validationData, "search");
        setTimeout(() => {
            // if (this.isValidForm(event)) {
                if (!event.target.classList.contains('buttonDisabled')) {
                    this.showLoader = true;
                    this.tableShowHide = false;

                    // To hide the No Data Found and REMOVAL DETAILS Form
                    this.createNewForm = false;
                    this.showNoDataFound = false;

                    let currentForm = event.target.parentNode.parentNode.parentNode,
                        searchCrtra = {
                            "fromDate": currentForm.querySelector("#fromDate").value,
                            "toDate": currentForm.querySelector("#toDate").value,
                           // "testName": currentForm.querySelector("#searchName").value,
                          //  "lsmVersion":  /* this.selSearchVer ? this.selSearchVer.name : "", */currentForm.querySelector("#searchVersion").value,
                           // "lsmName":/*  this.selSearchSMName ? this.selSearchSMName.name : "", */currentForm.querySelector("#searchSMName").value,                            
                            "ciqName":/*  this.selSearchCiqName ? this.selSearchCiqName.ciqFileName : "", */currentForm.querySelector("#searchCiqName").value,
                            "neName": currentForm.querySelector("#searchNeName").value,
                            "preMigStatus": currentForm.querySelector("#searchPreMigStatus").value,
                            "neGrowStatus": currentForm.querySelector("#searchNeGrowStatus").value,
                            "MigStatus": currentForm.querySelector("#searchMigStatus").value,
                            "PostMigStatus": currentForm.querySelector("#searchPostMigStatus").value,
                            "siteReportStatus": currentForm.querySelector("#searchSiteReportStatus").value,
                            "userName": currentForm.querySelector("#searchUserName").value
                        };

                    if (searchCrtra.fromDate || searchCrtra.toDate || searchCrtra.ciqName || searchCrtra.neName || 
                        searchCrtra.preMigStatus || searchCrtra.neGrowStatus || searchCrtra.MigStatus || searchCrtra.PostMigStatus || 
                        searchCrtra.siteReportStatus || searchCrtra.userName) {
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
                    this.getRunTest();
                }
            // }
        }, 0);
    }
    getSearchCriteria(event) {
        if (!event.target.classList.contains('hiddenButton')) {
            let currentForm = event.target.parentNode.parentNode.parentNode,
            searchCrtra = {
                "fromDate": currentForm.querySelector("#fromDate").value,
                "toDate": currentForm.querySelector("#toDate").value,
                "ciqName":currentForm.querySelector("#searchCiqName").value,
                "neName": currentForm.querySelector("#searchNeName").value,
                "preMigStatus": currentForm.querySelector("#searchPreMigStatus").value,
                "neGrowStatus": currentForm.querySelector("#searchNeGrowStatus").value,
                "MigStatus": currentForm.querySelector("#searchMigStatus").value,
                "PostMigStatus": currentForm.querySelector("#searchPostMigStatus").value,
                "siteReportStatus": currentForm.querySelector("#searchSiteReportStatus").value,
                "userName": currentForm.querySelector("#searchUserName").value
            };

            if (searchCrtra.fromDate || searchCrtra.toDate || searchCrtra.ciqName || searchCrtra.neName || 
                searchCrtra.preMigStatus || searchCrtra.neGrowStatus || searchCrtra.MigStatus || searchCrtra.PostMigStatus || 
                searchCrtra.siteReportStatus || searchCrtra.userName) {
                this.searchStatus = "search";
            }
            else {
                this.searchStatus = "load";
            }
            this.searchCriteria = searchCrtra;
        }
    }
    
    showListOfUseCases(popover) {
        popover.open();
    }


   

    getLsmName(lsmSelectedVersion) { 
        // this.useCaseValue = [];
        // this.scriptValue =[];
        this.nameDetails = "";
        this.selSearchSMName ="";
        this.selSearchCiqName = "";
        this.lsmNameDetails = lsmSelectedVersion.smNameList;  
    }
       
    getUseCase(useCaseList = null) {        //lsmSelectedName
        this.useCaseValue = [];
        this.scriptValue =[];
        this.selectedItems = [];
        this.dropdownList = [];
        this.PAuseCaseValue = [];
        this.PAscriptValue = [];
        this.NSuseCaseValue = [];
        this.NSscriptValue = [];
        // let useCaseList = this.tableData.useCaseList;//lsmSelectedName.useCaseList;  
        if(!useCaseList) {
            let useCaseDefList = this.getDefaultUseCases();
            for (let itm of useCaseDefList) {
                let dropdownList = { item_id: itm, item_text: itm };
                this.dropdownList.push(dropdownList);
            }
           // this.selectedItems= this.dropdownList;
        }
        else {
            for (let itm of useCaseList) {
                let dropdownList = { item_id: itm, item_text: itm.useCaseName };
                this.dropdownList.push(dropdownList);
            }
        }
        
        this.dropdownSettings = {
            singleSelection: false,
            idField: 'item_id',
            textField: 'item_text',
            selectAllText: 'Select All',
            unSelectAllText: 'UnSelect All',
            itemsShowLimit: 1,
            allowSearchFilter: true
        };
    }


        
    getMigUseCase(useCaseList) {        //lsmSelectedName
        this.dropdownMigList = [];
      
            for (let itm of useCaseList) {
                let dropdownMigList = { item_id: itm, item_text: itm.useCaseName };
                this.dropdownMigList.push(dropdownMigList);
            }
        
        
        this.dropdownMigSettings = {
            singleSelection: false,
            idField: 'item_id',
            textField: 'item_text',
            selectAllText: 'Select All',
            unSelectAllText: 'UnSelect All',
            itemsShowLimit: 1,
            allowSearchFilter: true
        };
    }

    getNeStatusMigUseCase(useCaseList) {
        this.useCaseValue = [];
        this.scriptValue =[];
        this.selectedItems = [];
        this.useCaseListNE = [];
        this.NEuseCaseValue = [];
        this.NEscriptValue = [];

        for (let itm of useCaseList) {
            let dropdownList = { item_id: itm, item_text: itm.useCaseName };
            this.useCaseListNE.push(dropdownList);
        }
        
        this.dropdownSettings = {
            singleSelection: false,
            idField: 'item_id',
            textField: 'item_text',
            selectAllText: 'Select All',
            unSelectAllText: 'UnSelect All',
            itemsShowLimit: 1,
            allowSearchFilter: true
        };
    }
    getNeStatusMigUseCases(useCaseList) {        //lsmSelectedName
        this.dropdownNeStatusList = [];
        // let useCaseList = this.tableData.useCaseList;//lsmSelectedName.useCaseList;  
        
        for (let itm of useCaseList) {
            let dropdownNeStatusList = { item_id: itm, item_text: itm.useCaseName };
            this.dropdownNeStatusList.push(dropdownNeStatusList);
        }
        
        this.dropdownNEMigSettings = {
            singleSelection: false,
            idField: 'item_id',
            textField: 'item_text',
            selectAllText: 'Select All',
            unSelectAllText: 'UnSelect All',
            itemsShowLimit: 1,
            allowSearchFilter: true
        };
    }
    getPreAuditMigUseCase(useCaseList) {
        this.useCaseValue = [];
        this.scriptValue =[];
        this.selectedItems = [];
        this.useCaseListPA = [];
        this.PAuseCaseValue = [];
        this.PAscriptValue = [];
        this.NSuseCaseValue = [];
        this.NSscriptValue = [];

        for (let itm of useCaseList) {
            let dropdownList = { item_id: itm, item_text: itm.useCaseName };
            this.useCaseListPA.push(dropdownList);
        }
        
        this.dropdownSettings = {
            singleSelection: false,
            idField: 'item_id',
            textField: 'item_text',
            selectAllText: 'Select All',
            unSelectAllText: 'UnSelect All',
            itemsShowLimit: 1,
            allowSearchFilter: true
        };
    }
    getPreAuditMigUseCases(useCaseList) {        //lsmSelectedName
        this.dropdownPreAuditList = [];
        // let useCaseList = this.tableData.useCaseList;//lsmSelectedName.useCaseList;  
        
        for (let itm of useCaseList) {
            let dropdownPreAuditList = { item_id: itm, item_text: itm.useCaseName };
            this.dropdownPreAuditList.push(dropdownPreAuditList);
        }
        
        this.dropdownPAMigSettings = {
            singleSelection: false,
            idField: 'item_id',
            textField: 'item_text',
            selectAllText: 'Select All',
            unSelectAllText: 'UnSelect All',
            itemsShowLimit: 1,
            allowSearchFilter: true
        };
    }
    getPostMigUseCase(useCaseList) {        //lsmSelectedName
        this.useCaseValue = [];
        this.scriptValue =[];
        this.selectedItems = [];
        this.useCaseListPM = [];
        this.PAuseCaseValue = [];
        this.PAscriptValue = [];
        this.NSuseCaseValue = [];
        this.NSscriptValue = [];
        // let useCaseList = this.tableData.useCaseList;//lsmSelectedName.useCaseList;  
        
        for (let itm of useCaseList) {
            let dropdownList = { item_id: itm, item_text: itm.useCaseName };
            this.useCaseListPM.push(dropdownList);
        }
        
        this.dropdownSettings = {
            singleSelection: false,
            idField: 'item_id',
            textField: 'item_text',
            selectAllText: 'Select All',
            unSelectAllText: 'UnSelect All',
            itemsShowLimit: 1,
            allowSearchFilter: true
        };
    }

    getPostMigUseCases(useCaseList) {        //lsmSelectedName
        this.dropdownPMigList = [];
        // let useCaseList = this.tableData.useCaseList;//lsmSelectedName.useCaseList;  
        
        for (let itm of useCaseList) {
            let dropdownPMigList = { item_id: itm, item_text: itm.useCaseName };
            this.dropdownPMigList.push(dropdownPMigList);
        }
        
        this.dropdownPMigSettings = {
            singleSelection: false,
            idField: 'item_id',
            textField: 'item_text',
            selectAllText: 'Select All',
            unSelectAllText: 'UnSelect All',
            itemsShowLimit: 1,
            allowSearchFilter: true
        };
    }
    getNeGrowUseCase(useCaseList = null) {        //lsmSelectedName
        this.useCaseValue = [];
        this.scriptValue =[];
        this.selectedItemsNG = [];
        this.dropdownListNG = [];
        this.PAuseCaseValue = [];
        this.PAscriptValue = [];
        this.NSuseCaseValue = [];
        this.NSscriptValue = [];
        // let useCaseList = this.tableData.useCaseList;//lsmSelectedName.useCaseList;  
        if(!useCaseList) {
            let useCaseDefList = this.getDefaultUseNeGrowCases();
            for (let itm of useCaseDefList) {
                let dropdownListNG = { item_id: itm, item_text: itm };
                this.dropdownListNG.push(dropdownListNG);
            }
        }
        else {
            for (let itm of useCaseList) {
                let dropdownListNG = { item_id: itm, item_text: itm.useCaseName };
                this.dropdownListNG.push(dropdownListNG);
            }
        }
        
        if(this.programName != "VZN-4G-USM-LIVE")
        {
            this.dropdownSettings = {
                singleSelection: false,
                idField: 'item_id',
                textField: 'item_text',
                selectAllText: 'Select All',
                unSelectAllText: 'UnSelect All',
                itemsShowLimit: 1,
                allowSearchFilter: true,
                enableCheckAll: this.programName == 'VZN-5G-MM' ? false : true,
            };
        }
         else
        {
            this.dropdownSettings = {
                singleSelection: true,
                idField: 'item_id',
                textField: 'item_text',
                selectAllText: 'Select All',
                unSelectAllText: 'UnSelect All',
                itemsShowLimit: 1,
                allowSearchFilter: true
            };
         }
        
    }

    getNeGrowUseCases(useCaseList) {        //lsmSelectedName
        this.dropdownNGList = [];
     
            for (let itm of useCaseList) {
                let dropdownNGList = { item_id: itm, item_text: itm.useCaseName };
                this.dropdownNGList.push(dropdownNGList);
            }
        
        
        this.dropdownNGSettings = {
            singleSelection: false,
            idField: 'item_id',
            textField: 'item_text',
            selectAllText: 'Select All',
            unSelectAllText: 'UnSelect All',
            itemsShowLimit: 1,
            allowSearchFilter: true
        };
    }

    getDefaultUseNeGrowCases() {
        let useCases = [];
        switch(this.programName) {
            case "VZN-5G-MM":
                useCases = ["pnp","AUCaCell","AU", "DeleteNE", "NeCreationTime"];
                break;
            case "VZN-4G-USM-LIVE":
                // useCases = ["GrowEnb"];
                useCases = ["pnp", "GrowEnb", "GrowCell", "CA_Usecase", "DeleteNE", "NeCreationTime"];
                break;
            case "VZN-5G-CBAND":
                // useCases = ["GrowvDU"];
                useCases = ["pnpGrow_cband", "vDUCellGrow_cband", "vDUGrow_cband"];
                break;
            case "VZN-4G-FSU":
                useCases = ["GrowFSU", "DeleteNE", "NeCreationTime"];
                break;
            case "VZN-5G-DSS":
                useCases = ["pnpGrow","vDUCellGrow","vDUGrow"];
                break;
            default:
                useCases = ["GrowEnb"];
               // useCases = ["pnp","GrowEnb","GrowCell"];
        }
        return useCases;
    }

    getDefaultUseCases() {
        let useCases = [];
        switch(this.programName) {
            case "VZN-5G-MM":
                useCases = ["RF_Scripts_Usecase"];//[18Aug21]Removed as Arun requested ["Anchor_CSL_UseCase","CSL_Usecase","AU_Commision_Usecase","ACPF_A1A2_Config_Usecase","ENDC_X2_UseCase"];
                break;
            case "VZN-5G-DSS":
                useCases = ["Pre-Check_RF_Scripts_Usecase","Rollback_RF_Scripts_Usecase","Cutover_RF_Scripts_Usecase","Extended_Usecase"];
                break;
            case "VZN-5G-CBAND":
                useCases = ["Pre-Check_RF_Scripts_Usecase","Cutover_RF_Scripts_Usecase","Extended_Usecase"];
                break;
            case "VZN-4G-FSU":
                useCases = ["CommissionScriptUsecase"];
                break;
            default:
                useCases = ["RFUsecase", "EndcUsecase"];
        }
        
       
        return useCases;
    }

    getMigUseCases(isPostMig, rowKey = null, forSiteData = false, forSiteDataPopup = false) {
        this.selectedItems = [];
        let neList = !forSiteDataPopup ? this.selectedNEItems : this.selectedItemsNE;
        if (this.ciqDetails && neList && neList.length > 0 && !rowKey) {
            this.showLoader = true;
            let selectedNEs;
            if(forSiteData) {
                let dropdownListNE = [];
                for (let itm of neList) {
                    for (let item of itm.value) {
                        dropdownListNE.push(item.eNBId);
                    }
                }
                selectedNEs = dropdownListNE;
            }
            else if(forSiteDataPopup) {
                selectedNEs = this.selectedItemsNE.map((item) => item.item_id);
            }
            else {
                selectedNEs = neList.map((item) => item.eNBId);
            }
            this.runtestService.getMigUseCases(this.ciqDetails, selectedNEs, this.sharedService.createServiceToken(), this.commissionType, isPostMig)
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
                                        if(isPostMig == 'post') {
                                            this.getPostMigUseCase(jsonStatue.useCaseList);
                                            // this.getPostMigUseCases(jsonStatue.useCaseList);
                                        } else if (isPostMig == 'pre') {
                                            this.getPreAuditMigUseCase(jsonStatue.useCaseList);
                                        } else if (isPostMig == 'nes') {
                                            this.getNeStatusMigUseCase(jsonStatue.useCaseList);
                                        }
                                        else {
                                            this.getUseCase(jsonStatue.useCaseList);
                                        }
                                    } else {
                                        this.showLoader = false;
                                    }
                                }
                            }

                        }, 0);
                    },
                    error => {
                        //Please Comment while checkIn
                        /* setTimeout(() => {
                            this.showLoader = false;
                            let jsonStatue = JSON.parse('{"sessionId":"a462f16b","serviceToken":"52965","status":"SUCCESS","useCaseList":[{"useCaseName":"CommissionScri_cbrs_ptUsecase_70215_12042020","useCaseId":17154,"ucSleepInterval":"1000","executionSequence":9895776,"scripts":[{"scriptId":53605,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"BASH_COMM_NB-IoTAdd_COMM_070215_HOUGHTON_12042020.sh","scriptExeSequence":29}]},{"useCaseName":"CommissionScri_cbrs1_ptUsecase_70215_12042020","useCaseId":17154,"ucSleepInterval":"1000","executionSequence":9895776,"scripts":[{"scriptId":53605,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"BASH_COMM_NB-IoTAdd_COMM_070215_HOUGHTON_12042020.sh","scriptExeSequence":29}]},{"useCaseName":"CommissionScriptUs_fcc_ecase_70215_12042020","useCaseId":17154,"ucSleepInterval":"1000","executionSequence":9895776,"scripts":[{"scriptId":53605,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"BASH_COMM_NB-IoTAdd_COMM_070215_HOUGHTON_12042020.sh","scriptExeSequence":29}]},{"useCaseName":"CommissionScriptUs_fcc1_ecase_70215_12042020","useCaseId":17154,"ucSleepInterval":"1000","executionSequence":9895776,"scripts":[{"scriptId":53605,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"BASH_COMM_NB-IoTAdd_COMM_070215_HOUGHTON_12042020.sh","scriptExeSequence":29}]},{"useCaseName":"CommissionScriptUsecase_70215_12042020","useCaseId":17154,"ucSleepInterval":"1000","executionSequence":9895776,"scripts":[{"scriptId":53605,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"BASH_COMM_NB-IoTAdd_COMM_070215_HOUGHTON_12042020.sh","scriptExeSequence":29}]},{"useCaseName":"RFUsecase_70215_12042020","useCaseId":17153,"ucSleepInterval":"1000","executionSequence":9895775,"scripts":[]},{"useCaseName":"vDUInstantiationAudit","useCaseId":1688,"ucSleepInterval":"1000","executionSequence":56474,"scripts":[{"scriptId":53605,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"BASH_COMM_NB-IoTAdd_COMM_070215_HOUGHTON_14042020.sh","scriptExeSequence":28},{"scriptId":53615,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"script.sh","scriptExeSequence":29}]},{"useCaseName":"rssiImbalance_RangeCheck","useCaseId":1712,"ucSleepInterval":"1000","executionSequence":6473653,"scripts":[]},{"useCaseName":"mmuAuditReport","useCaseId":531,"ucSleepInterval":"1000","executionSequence":1,"scripts":[]}]}');
                            if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
                                this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
                            }
                            if (jsonStatue.status == "SUCCESS") {
                                if(isPostMig == 'post') {
                                    this.getPostMigUseCase(jsonStatue.useCaseList);
                                    // this.getPostMigUseCases(jsonStatue.useCaseList);
                                } else if (isPostMig == 'pre') {
                                    this.getPreAuditMigUseCase(jsonStatue.useCaseList);
                                } else if (isPostMig == 'nes') {
                                    this.getNeStatusMigUseCase(jsonStatue.useCaseList);
                                }
                                else {
                                    this.getUseCase(jsonStatue.useCaseList);
                                }
                            } else {
                                this.showLoader = false;
                            }
                        }, 0); */
                        //Please Comment while checkIn
                    });
        }
        else if(rowKey) {
            this.runtestService.getMigUseCases(rowKey.ciqDetails, [rowKey.enbId], this.sharedService.createServiceToken(), this.commissionType, isPostMig)
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
                                        if (isPostMig == 'post') {
                                            // this.getPostMigUseCase(jsonStatue.useCaseList);
                                            this.getPostMigUseCases(jsonStatue.useCaseList);
                                        } else if (isPostMig == 'pre'){
                                            this.getPreAuditMigUseCases(jsonStatue.useCaseList);
                                        } else if (isPostMig == 'nes') {
                                            this.getNeStatusMigUseCases(jsonStatue.useCaseList);
                                        }
                                        else {
                                            this.getUseCase(jsonStatue.useCaseList);
                                        }
                                    } else {
                                        this.showLoader = false;
                                    }
                                }
                            }

                        }, 0);
                    },
                    error => {
                        //Please Comment while checkIn
                        /* setTimeout(() => {
                            this.showLoader = false;
                            let jsonStatue = JSON.parse('{"useCaseList":[{"useCaseName":"RFUsecascbrse_123","useCaseId":1430,"ucSleepInterval":"1","executionSequence":64986,"scripts":[]},{"useCaseName":"AU_Checks_fccUsecase_10012020_123","useCaseId":1431,"ucSleepInterval":"1","executionSequence":64987,"scripts":[{"scriptId":8767,"scriptSleepInterval":"1","useGeneratedScript":"NO","scriptName":"BASH_COMM_NB-IoTAdd_COMM_070282_MTSO_06102020.sh","scriptExeSequence":117}]}],"sessionId":"5ace0fb8","serviceToken":"78917","status":"SUCCESS"}');
                            if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
                                this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
                            }
                            if (jsonStatue.status == "SUCCESS") {
                                if (isPostMig == 'post') {
                                    // this.getPostMigUseCase(jsonStatue.useCaseList);
                                    this.getPostMigUseCases(jsonStatue.useCaseList);
                                } else if (isPostMig == 'pre'){
                                    this.getPreAuditMigUseCases(jsonStatue.useCaseList);
                                } else if (isPostMig == 'nes') {
                                    this.getNeStatusMigUseCases(jsonStatue.useCaseList);
                                }
                                else {
                                    this.getUseCase(jsonStatue.useCaseList);
                                }
                            } else {
                                this.showLoader = false;
                            }
                        }, 0); */
                        //Please Comment while checkIn
                    });
        }
        else if(this.bulkNeCheck && this.rfScriptList) {
            let bulkNEList = this.rfScriptList && this.rfScriptList.length > 0 ? this.rfScriptList.split("\n") : null;
            // let selectedNEs = bulkNEList ? bulkNEList.join(',') : null;
            this.runtestService.getMigUseCases(this.ciqDetails, bulkNEList, this.sharedService.createServiceToken(), this.commissionType, isPostMig)
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
                                        if(isPostMig == 'post') {
                                            this.getPostMigUseCase(jsonStatue.useCaseList);
                                            // this.getPostMigUseCases(jsonStatue.useCaseList);
                                        } else if (isPostMig == 'pre') {
                                            this.getPreAuditMigUseCase(jsonStatue.useCaseList);
                                        }else if (isPostMig == 'nes') {
                                            this.getNeStatusMigUseCase(jsonStatue.useCaseList);
                                        }
                                        else {
                                            this.getUseCase(jsonStatue.useCaseList);
                                        }
                                    } else {
                                        this.showLoader = false;
                                    }
                                }
                            }

                        }, 0);
                    },
                    error => {
                        //Please Comment while checkIn
                        /* setTimeout(() => {
                            this.showLoader = false;
                            let jsonStatue = JSON.parse('{"useCaseList":[{"useCaseName":"RFUsecase","useCaseId":1430,"ucSleepInterval":"1","executionSequence":64986,"scripts":[]},{"useCaseName":"AU_Checks_Usecase_10012020","useCaseId":1431,"ucSleepInterval":"1","executionSequence":64987,"scripts":[{"scriptId":8767,"scriptSleepInterval":"1","useGeneratedScript":"NO","scriptName":"BASH_COMM_NB-IoTAdd_COMM_070282_MTSO_06102020.sh","scriptExeSequence":117}]}],"sessionId":"5ace0fb8","serviceToken":"78917","status":"SUCCESS"}');
                            if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
                                this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
                            }
                            if (jsonStatue.status == "SUCCESS") {
                                if(isPostMig == 'post') {
                                    this.getPostMigUseCase(jsonStatue.useCaseList);
                                    // this.getPostMigUseCases(jsonStatue.useCaseList);
                                } else if (isPostMig == 'pre') {
                                    this.getPreAuditMigUseCase(jsonStatue.useCaseList);
                                } else if (isPostMig == 'nes') {
                                    this.getNeStatusMigUseCase(jsonStatue.useCaseList);
                                }
                                else {
                                    this.getUseCase(jsonStatue.useCaseList);
                                }
                            } else {
                                this.showLoader = false;
                            }
                        }, 0); */
                        //Please Comment while checkIn
                    });
        }
        else {
            if(isPostMig){
                this.useCaseListPM = [];
                this.selectedItemsPMig = [];
                this.selectedRadioUnittems = [];
                this.isRSSIUseCaseSelected = false;
            }
            else {
                this.dropdownList = [];
            }
        }
    }


    getUseCases(type,key) {
        if(type=="NEGrow")
            this.commissionType="NEGrow";
        else
            this.commissionType="precheck";

        if(type != "PostMigration" && type != "preaudit"  && type != "nestatus"){
        if (key.ciqName) {
            // this.showLoader = true;  No Need to show loader as already shown in onChangeNE
            this.runtestService.getRowMigUseCases(key.ciqName, key.enbId, this.sharedService.createServiceToken(), this.commissionType,type)
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

                                        if(type == "NEGrow")
                                            this.getNeGrowUseCases(jsonStatue.useCaseList);
                                        if(type == "Migration")
                                             this.getMigUseCase(jsonStatue.useCaseList);
                                    } else {
                                        this.showLoader = false;
                                    }
                                }
                            }

                        }, 0);
                    },
                    error => {
                        //Please Comment while checkIn
                         /* setTimeout(() => {
                            this.showLoader = false;
                            let jsonStatue = JSON.parse('{"useCaseList":[{"useCaseName":"Pre-Check_RF_Scripts_Usecase","useCaseId":1430,"ucSleepInterval":"1","executionSequence":64986,"scripts":[]},{"useCaseName":"AU_Checks_Usecase_10012020","useCaseId":1431,"ucSleepInterval":"1","executionSequence":64987,"scripts":[{"scriptId":8767,"scriptSleepInterval":"1","useGeneratedScript":"NO","scriptName":"BASH_COMM_NB-IoTAdd_COMM_070282_MTSO_06102020.sh","scriptExeSequence":117}]}],"sessionId":"5ace0fb8","serviceToken":"78917","status":"SUCCESS"}');
                            if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
                                this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
                            }
                            if (jsonStatue.status == "SUCCESS") {
                                        if(type == "NEGrow")
                                            this.getNeGrowUseCases(jsonStatue.useCaseList);
                                         if(type == "Migration")
                                             this.getMigUseCase(jsonStatue.useCaseList);
                                       
                            } else {
                                this.showLoader = false;
                            }
                        }, 0);  */
                        //Please Comment while checkIn
                    });
        }
    
        else {
            this.dropdownNGList = [];
            this.dropdownMigList=[];
            this.dropdownPMigList=[];
            this.dropdownPreAuditList=[];
            this.dropdownNeStatusList=[];
        }
    }
    else {
        if(type=="PostMigration") {
            this.getMigUseCases('post', key);
        }  else if(type == "preaudit") {
            this.getMigUseCases('pre', key);
        } else {
            this.getMigUseCases('nes', key);
        }
    }
    }

    clearUseCaseModelData() {
        this.useCaseDropDownVisibility = 0;
        this.showStateUseCase = "";
        this.useCaseModelKey = null;

        this.dropdownNGList = [];
        this.dropdownMigList=[];

        this.selectedNGItems = [];
        this.selectedNGStatusItems = [];
        this.selectedMigItems = [];

        // clear useCase and script
        this.NGuseCaseValue = [];
        this.NSuseCaseValue = [];
        this.NSscriptValue = [];
        this.scriptValueNG = [];
        this.MiguseCaseValue = [];
        this.scriptValueMig = [];
        this.PMiguseCaseValue = [];
        this.PAMiguseCaseValue = [];
        this.scriptValuePMig = [];
        this.pascriptValuePMig = [];
        this.useCaseModelData = {};
        this.allScriptSelect = true;
        this.ranAtpFlagUseCase = false;
        this.isPRECHECKShow = false;
    }


    getNEList(selectedCiqName, updateSessionStorage = false){
        this.selectedNEItems =null;       
        this.selectedItems = [];
        if (this.ciqDetails) {
            this.showLoader = true;
            // Update the sessionStorage selected CIQ if CIQ list is getting changed from UI dropdown
            updateSessionStorage ? this.sharedService.updateSelectedCIQInSessionStorage(selectedCiqName) : "";
        if(this.programName != 'VZN-5G-MM'){
        this.runtestService.getNeListData(selectedCiqName.ciqFileName, this.sharedService.createServiceToken() )
            .subscribe(
                data => {
                    setTimeout(() => { 
                      let jsonStatue = data.json();                      
                      this.showLoader = false;
                          if(jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User"){
                           this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                          } else {
                            if( this.sharedService.isvalidServiceToken( parseInt(data.json().serviceToken,10) ) || data.json().sessionId == "408"){
                              if(jsonStatue.status == "SUCCESS"){   
                                this.dropdownNEList = [];
                                // this.dropdownList = [];
                                this.neNameList = jsonStatue.eNBList;
                                /* for (let itm of this.neNameList) {
                                  let dropdownList = { item_id: itm.eNBId, item_text: itm.eNBName };
                                  this.dropdownNEList.push(dropdownList);
                              } */
                              this.dropdownSettingsNE = {
                                  singleSelection: true,
                                  idField: 'item_id',
                                  textField: 'item_text',
                                  selectAllText: 'Select All',
                                  unSelectAllText: 'UnSelect All',
                                  itemsShowLimit: 1,
                                  allowSearchFilter: true,
                                  maxHeight: 300
                              };
                              }else{
                                this.showLoader = false;
                                this.neNameList = [];
                              }
                             }   
                          }
                                        
                    }, 1000);
                },
                error => {
                  //Please Comment while checkIn
                  /* setTimeout(() => { 
                    this.showLoader = false;
                    let jsonStatue= JSON.parse('{"eNBList":[{"eNBName":"061192_NORTHWOOD_LAKE_NH","eNBId":"61192"},{"eNBName":"061452_CONCORD_2_NH_HUB","eNBId":"61452"},{"eNBName":"073461_PRATTSBURGH","eNBId":"73461"},{"eNBName":"073462_East_Corning","eNBId":"73462"},{"eNBName":"073466_Howard","eNBId":"73466"},{"eNBName":"073474_Hornellsville","eNBId":"73474"},{"eNBName":"073484_ADDISON","eNBId":"73484"},{"eNBName":"072409_Press_Building","eNBId":"72409"},{"eNBName":"072412_Binghamton_DT","eNBId":"72412"},{"eNBName":"072413_SUNY_Binghamton","eNBId":"72413"},{"eNBName":"072415_Vestal","eNBId":"72415"},{"eNBName":"072416_Chenango","eNBId":"72416"},{"eNBName":"072417_Kirkwood","eNBId":"72417"},{"eNBName":"072419_Windsor","eNBId":"72419"},{"eNBName":"072424_CASTLE_CREEK","eNBId":"72424"},{"eNBName":"072425_Killawog","eNBId":"72425"},{"eNBName":"072426_East_Richford","eNBId":"72426"},{"eNBName":"072427_Caroline","eNBId":"72427"},{"eNBName":"072430_Owego_North","eNBId":"72430"},{"eNBName":"072431_Owego","eNBId":"72431"},{"eNBName":"072432_Apalachin","eNBId":"72432"},{"eNBName":"072433_Nichols","eNBId":"72433"},{"eNBName":"072442_CROCKER_CREEK","eNBId":"72442"},{"eNBName":"072443_MAINE_DT","eNBId":"72443"},{"eNBName":"072451_BELDEN","eNBId":"72451"},{"eNBName":"072452_TIOGA_CENTER","eNBId":"72452"},{"eNBName":"072454_CATATONK","eNBId":"72454"},{"eNBName":"072458_CHENANGO_DT","eNBId":"72458"},{"eNBName":"072478_Big_Flats","eNBId":"72478"},{"eNBName":"070033_POWERS_RD","eNBId":"70033"},{"eNBName":"070005_RTE_263_GETZVILLE","eNBId":"70005"},{"eNBName":"070562_BOWEN_RD","eNBId":"70562"},{"eNBName":"073313_FREY_RD","eNBId":"73313"},{"eNBName":"073326_BAKER_RD","eNBId":"73326"}],"sessionId":"2a7a3636","serviceToken":"79044","status":"SUCCESS"}');
                      if(jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User"){
                         this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                        }
                      if(jsonStatue.status == "SUCCESS"){
                        this.dropdownNEList = [];
                        // this.dropdownList = [];
                        this.neNameList = jsonStatue.eNBList;
                        // for (let itm of this.neNameList) {
                        //     let dropdownList = { item_id: itm.eNBId, item_text: itm.eNBName };
                        //     this.dropdownNEList.push(dropdownList);
                        // }
                        this.dropdownSettingsNE = {
                            singleSelection: true,
                            idField: 'item_id',
                            textField: 'item_text',
                            selectAllText: 'Select All',
                            unSelectAllText: 'UnSelect All',
                            itemsShowLimit: 1,
                            allowSearchFilter: true,
                            maxHeight: 300
                        };
                      }else{
                        this.showLoader = false;
                        this.neNameList = [];
                      }
                  }, 100); */
                  //Please Comment while checkIn
            });
        }
        else
        {
            this.runtestService.getNeSiteListData(selectedCiqName.ciqFileName, this.sharedService.createServiceToken() )
            .subscribe(
                data => {
                    setTimeout(() => { 
                      let jsonStatue = data.json();                      
                      this.showLoader = false;
                          if(jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User"){
                           this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                          } else {
                            if( this.sharedService.isvalidServiceToken( parseInt(data.json().serviceToken,10) ) || data.json().sessionId == "408"){
                              if(jsonStatue.status == "SUCCESS"){   
                                this.dropdownNEList = [];
                                // this.dropdownList = [];
                                this.neNameSiteList = jsonStatue.siteList;
                                /* for (let itm of this.neNameList) {
                                  let dropdownList = { item_id: itm.eNBId, item_text: itm.eNBName };
                                  this.dropdownNEList.push(dropdownList);
                              } */
                              this.dropdownSettingsNE = {
                                  singleSelection: true,
                                  idField: 'item_id',
                                  textField: 'item_text',
                                  selectAllText: 'Select All',
                                  unSelectAllText: 'UnSelect All',
                                  itemsShowLimit: 1,
                                  allowSearchFilter: true,
                                  maxHeight: 300
                              };
                              }else{
                                this.showLoader = false;
                                this.neNameList = [];
                              }
                             }   
                          }
                                        
                    }, 1000);
                },
                error => {
                  //Please Comment while checkIn
                  /* setTimeout(() => { 
                    this.showLoader = false;
                    let jsonStatue= JSON.parse(' {"siteList":{"CA_SAC_SACRAMENTODT_252":[{"eNBName":"03600010055_5GDU_CA_SAC_SACRAMENTODT_252","eNBId":"3600010055","siteName":"CA_SAC_SACRAMENTODT_252"},{"eNBName":"03600010056_5GDU_CA_SAC_SACRAMENTODT_252","eNBId":"3600010056","siteName":"CA_SAC_SACRAMENTODT_252"},{"eNBName":"03600010057_5GDU_CA_SAC_SACRAMENTODT_252","eNBId":"3600010057","siteName":"CA_SAC_SACRAMENTODT_252"}],"CA_SAC_OAKPARK_068":[{"eNBName":"03600210028_5GDU_CA_SAC_OAKPARK_068","eNBId":"3600210028","siteName":"CA_SAC_OAKPARK_068"},{"eNBName":"03600210029_5GDU_CA_SAC_OAKPARK_068","eNBId":"3600210029","siteName":"CA_SAC_OAKPARK_068"},{"eNBName":"03600210030_5GDU_CA_SAC_OAKPARK_068","eNBId":"3600210030","siteName":"CA_SAC_OAKPARK_068"}],"CA_SAC_SACRAMENTODT_214":[{"eNBName":"03600080076_5GDU_CA_SAC_SACRAMENTODT_214","eNBId":"3600080076","siteName":"CA_SAC_SACRAMENTODT_214"},{"eNBName":"03600080077_5GDU_CA_SAC_SACRAMENTODT_214","eNBId":"3600080077","siteName":"CA_SAC_SACRAMENTODT_214"}],"CA_SAC_PARKWAY_016":[{"eNBName":"03600210040_5GDU_CA_SAC_PARKWAY_016","eNBId":"3600210040","siteName":"CA_SAC_PARKWAY_016"},{"eNBName":"03600210041_5GDU_CA_SAC_PARKWAY_016","eNBId":"3600210041","siteName":"CA_SAC_PARKWAY_016"}],"CA_SAC_PARKWAY_018":[{"eNBName":"03600210094_5GDU_CA_SAC_PARKWAY_018","eNBId":"3600210094","siteName":"CA_SAC_PARKWAY_018"},{"eNBName":"03600210095_5GDU_CA_SAC_PARKWAY_018","eNBId":"3600210095","siteName":"CA_SAC_PARKWAY_018"},{"eNBName":"03600210096_5GDU_CA_SAC_PARKWAY_018","eNBId":"3600210096","siteName":"CA_SAC_PARKWAY_018"}],"CA_SAC_SACRAMENTODT_250":[{"eNBName":"03600010040_5GDU_CA_SAC_SACRAMENTODT_250","eNBId":"3600010040","siteName":"CA_SAC_SACRAMENTODT_250"},{"eNBName":"03600010041_5GDU_CA_SAC_SACRAMENTODT_250","eNBId":"3600010041","siteName":"CA_SAC_SACRAMENTODT_250"}],"CA_SAC_SACRAMENTODT_096":[{"eNBName":"03600080094_5GDU_CA_SAC_SACRAMENTODT_096","eNBId":"3600080094","siteName":"CA_SAC_SACRAMENTODT_096"},{"eNBName":"03600080095_5GDU_CA_SAC_SACRAMENTODT_096","eNBId":"3600080095","siteName":"CA_SAC_SACRAMENTODT_096"},{"eNBName":"03600080096_5GDU_CA_SAC_SACRAMENTODT_096","eNBId":"3600080096","siteName":"CA_SAC_SACRAMENTODT_096"}],"CA_SAC_CENTRALSAC_098":[{"eNBName":"03600080121_5GDU_CA_SAC_CENTRALSAC_098","eNBId":"3600080121","siteName":"CA_SAC_CENTRALSAC_098"},{"eNBName":"03600080122_5GDU_CA_SAC_CENTRALSAC_098","eNBId":"3600080122","siteName":"CA_SAC_CENTRALSAC_098"},{"eNBName":"03600080123_5GDU_CA_SAC_CENTRALSAC_098","eNBId":"3600080123","siteName":"CA_SAC_CENTRALSAC_098"}],"CA_SAC_CENTRALSAC_129":[{"eNBName":"03600360001_5GAU_CA_SAC_CENTRALSAC_129","eNBId":"3600360001","siteName":"CA_SAC_CENTRALSAC_129"},{"eNBName":"03600360002_5GAU_CA_SAC_CENTRALSAC_129","eNBId":"3600360002","siteName":"CA_SAC_CENTRALSAC_129"},{"eNBName":"03600360003_5GAU_CA_SAC_CENTRALSAC_129","eNBId":"3600360003","siteName":"CA_SAC_CENTRALSAC_129"}],"CA_SAC_WOODCREEK_058":[{"eNBName":"03600010133_5GDU_CA_SAC_WOODCREEK_058","eNBId":"3600010133","siteName":"CA_SAC_WOODCREEK_058"},{"eNBName":"03600010134_5GDU_CA_SAC_WOODCREEK_058","eNBId":"3600010134","siteName":"CA_SAC_WOODCREEK_058"},{"eNBName":"03600010135_5GDU_CA_SAC_WOODCREEK_058","eNBId":"3600010135","siteName":"CA_SAC_WOODCREEK_058"}],"SACRAMENTODT_5G091":[{"eNBName":"03600010010_5GAU_SACRAMENTODT_5G091","eNBId":"3600010010","siteName":"SACRAMENTODT_5G091"},{"eNBName":"03600010011_5GAU_SACRAMENTODT_5G091","eNBId":"3600010011","siteName":"SACRAMENTODT_5G091"}],"CA_SAC_LANDPARK_089":[{"eNBName":"03600050046_5GDU_CA_SAC_LANDPARK_089","eNBId":"3600050046","siteName":"CA_SAC_LANDPARK_089"},{"eNBName":"03600050047_5GDU_CA_SAC_LANDPARK_089","eNBId":"3600050047","siteName":"CA_SAC_LANDPARK_089"},{"eNBName":"03600050048_5GDU_CA_SAC_LANDPARK_089","eNBId":"3600050048","siteName":"CA_SAC_LANDPARK_089"}],"CA_SAC_WOODCREEK_017":[{"eNBName":"03600010148_5GDU_CA_SAC_WOODCREEK_017","eNBId":"3600010148","siteName":"CA_SAC_WOODCREEK_017"},{"eNBName":"03600010149_5GDU_CA_SAC_WOODCREEK_017","eNBId":"3600010149","siteName":"CA_SAC_WOODCREEK_017"},{"eNBName":"03600010150_5GDU_CA_SAC_WOODCREEK_017","eNBId":"3600010150","siteName":"CA_SAC_WOODCREEK_017"}],"CA_SAC_PARKWAY_012":[{"eNBName":"03600210043_5GDU_CA_SAC_PARKWAY_012","eNBId":"3600210043","siteName":"CA_SAC_PARKWAY_012"},{"eNBName":"03600210044_5GDU_CA_SAC_PARKWAY_012","eNBId":"3600210044","siteName":"CA_SAC_PARKWAY_012"},{"eNBName":"03600210045_5GDU_CA_SAC_PARKWAY_012","eNBId":"3600210045","siteName":"CA_SAC_PARKWAY_012"}],"CA_SAC_WESTPARK_058":[{"eNBName":"03600010121_5GDU_CA_SAC_WESTPARK_058","eNBId":"3600010121","siteName":"CA_SAC_WESTPARK_058"},{"eNBName":"03600010122_5GDU_CA_SAC_WESTPARK_058","eNBId":"3600010122","siteName":"CA_SAC_WESTPARK_058"},{"eNBName":"03600010123_5GDU_CA_SAC_WESTPARK_058","eNBId":"3600010123","siteName":"CA_SAC_WESTPARK_058"}],"CA_SAC_CENTRALSAC_127":[{"eNBName":"03600080091_5GDU_CA_SAC_CENTRALSAC_127","eNBId":"3600080091","siteName":"CA_SAC_CENTRALSAC_127"},{"eNBName":"03600080092_5GDU_CA_SAC_CENTRALSAC_127","eNBId":"3600080092","siteName":"CA_SAC_CENTRALSAC_127"},{"eNBName":"03600080093_5GDU_CA_SAC_CENTRALSAC_127","eNBId":"3600080093","siteName":"CA_SAC_CENTRALSAC_127"}],"CA_SAC_WOODCREEK_050":[{"eNBName":"03600010154_5GDU_CA_SAC_WOODCREEK_050","eNBId":"3600010154","siteName":"CA_SAC_WOODCREEK_050"},{"eNBName":"03600010155_5GDU_CA_SAC_WOODCREEK_050","eNBId":"3600010155","siteName":"CA_SAC_WOODCREEK_050"},{"eNBName":"03600010156_5GDU_CA_SAC_WOODCREEK_050","eNBId":"3600010156","siteName":"CA_SAC_WOODCREEK_050"}],"CA_SAC_SACRAMENTODT_526":[{"eNBName":"03600080088_5GDU_CA_SAC_SACRAMENTODT_526","eNBId":"3600080088","siteName":"CA_SAC_SACRAMENTODT_526"},{"eNBName":"03600080089_5GDU_CA_SAC_SACRAMENTODT_526","eNBId":"3600080089","siteName":"CA_SAC_SACRAMENTODT_526"},{"eNBName":"03600080090_5GDU_CA_SAC_SACRAMENTODT_526","eNBId":"3600080090","siteName":"CA_SAC_SACRAMENTODT_526"}],"CA_SAC_SACRAMENTODT_208":[{"eNBName":"03600010034_5GDU_CA_SAC_SACRAMENTODT_208","eNBId":"3600010034","siteName":"CA_SAC_SACRAMENTODT_208"},{"eNBName":"03600010035_5GDU_CA_SAC_SACRAMENTODT_208","eNBId":"3600010035","siteName":"CA_SAC_SACRAMENTODT_208"}],"CA_SAC_LANDPARK_009":[{"eNBName":"03600210001_5GAU_CA_SAC_LANDPARK_009","eNBId":"3600210001","siteName":"CA_SAC_LANDPARK_009"},{"eNBName":"03600210002_5GAU_CA_SAC_LANDPARK_009","eNBId":"3600210002","siteName":"CA_SAC_LANDPARK_009"},{"eNBName":"03600210003_5GAU_CA_SAC_LANDPARK_009","eNBId":"3600210003","siteName":"CA_SAC_LANDPARK_009"}],"CA_SAC_WOODCREEK_010":[{"eNBName":"03600010145_5GDU_CA_SAC_WOODCREEK_010","eNBId":"3600010145","siteName":"CA_SAC_WOODCREEK_010"},{"eNBName":"03600010146_5GDU_CA_SAC_WOODCREEK_010","eNBId":"3600010146","siteName":"CA_SAC_WOODCREEK_010"},{"eNBName":"03600010147_5GDU_CA_SAC_WOODCREEK_010","eNBId":"3600010147","siteName":"CA_SAC_WOODCREEK_010"}],"CA_SAC_LANDPARK_090":[{"eNBName":"03600210082_5GDU_CA_SAC_LANDPARK_090","eNBId":"3600210082","siteName":"CA_SAC_LANDPARK_090"},{"eNBName":"03600210083_5GDU_CA_SAC_LANDPARK_090","eNBId":"3600210083","siteName":"CA_SAC_LANDPARK_090"}],"CA_SAC_WOODCREEK_003":[{"eNBName":"03600010142_5GDU_CA_SAC_WOODCREEK_003","eNBId":"3600010142","siteName":"CA_SAC_WOODCREEK_003"},{"eNBName":"03600010143_5GDU_CA_SAC_WOODCREEK_003","eNBId":"3600010143","siteName":"CA_SAC_WOODCREEK_003"},{"eNBName":"03600010144_5GDU_CA_SAC_WOODCREEK_003","eNBId":"3600010144","siteName":"CA_SAC_WOODCREEK_003"}],"CA_SAC_WOODCREEK_049":[{"eNBName":"03600010130_5GDU_CA_SAC_WOODCREEK_049","eNBId":"3600010130","siteName":"CA_SAC_WOODCREEK_049"},{"eNBName":"03600010131_5GDU_CA_SAC_WOODCREEK_049","eNBId":"3600010131","siteName":"CA_SAC_WOODCREEK_049"},{"eNBName":"03600010132_5GDU_CA_SAC_WOODCREEK_049","eNBId":"3600010132","siteName":"CA_SAC_WOODCREEK_049"}],"CA_SAC_SOUTHSAC_026":[{"eNBName":"03600210097_5GDU_CA_SAC_SOUTHSAC_026","eNBId":"3600210097","siteName":"CA_SAC_SOUTHSAC_026"},{"eNBName":"03600210098_5GDU_CA_SAC_SOUTHSAC_026","eNBId":"3600210098","siteName":"CA_SAC_SOUTHSAC_026"},{"eNBName":"03600210099_5GDU_CA_SAC_SOUTHSAC_026","eNBId":"3600210099","siteName":"CA_SAC_SOUTHSAC_026"}],"CA_SAC_PARKWAY_023":[{"eNBName":"03600210037_5GDU_CA_SAC_PARKWAY_023","eNBId":"3600210037","siteName":"CA_SAC_PARKWAY_023"},{"eNBName":"03600210038_5GDU_CA_SAC_PARKWAY_023","eNBId":"3600210038","siteName":"CA_SAC_PARKWAY_023"}],"CA_SAC_TRUXEL_206":[{"eNBName":"03600480034_5GDU_CA_SAC_TRUXEL_206","eNBId":"3600480034","siteName":"CA_SAC_TRUXEL_206"},{"eNBName":"03600480035_5GDU_CA_SAC_TRUXEL_206","eNBId":"3600480035","siteName":"CA_SAC_TRUXEL_206"}],"CA_SAC_WOODCREEK_083":[{"eNBName":"03600010163_5GDU_CA_SAC_WOODCREEK_083","eNBId":"3600010163","siteName":"CA_SAC_WOODCREEK_083"},{"eNBName":"03600010164_5GDU_CA_SAC_WOODCREEK_083","eNBId":"3600010164","siteName":"CA_SAC_WOODCREEK_083"},{"eNBName":"03600010165_5GDU_CA_SAC_WOODCREEK_083","eNBId":"3600010165","siteName":"CA_SAC_WOODCREEK_083"}],"CA_SAC_HAGGINWOOD_136":[{"eNBName":"03600480043_5GDU_CA_SAC_HAGGINWOOD_136","eNBId":"3600480043","siteName":"CA_SAC_HAGGINWOOD_136"},{"eNBName":"03600480044_5GDU_CA_SAC_HAGGINWOOD_136","eNBId":"3600480044","siteName":"CA_SAC_HAGGINWOOD_136"},{"eNBName":"03600480045_5GDU_CA_SAC_HAGGINWOOD_136","eNBId":"3600480045","siteName":"CA_SAC_HAGGINWOOD_136"}],"CA_SAC_SACRAMENTODT_511":[{"eNBName":"03600080043_5GAU_CA_SAC_SACRAMENTODT_511","eNBId":"3600080043","siteName":"CA_SAC_SACRAMENTODT_511"},{"eNBName":"03600080044_5GAU_CA_SAC_SACRAMENTODT_511","eNBId":"3600080044","siteName":"CA_SAC_SACRAMENTODT_511"},{"eNBName":"03600080045_5GAU_CA_SAC_SACRAMENTODT_511","eNBId":"3600080045","siteName":"CA_SAC_SACRAMENTODT_511"}],"CA_SAC_SACRAMENTODT_236":[{"eNBName":"03600080082_5GDU_CA_SAC_SACRAMENTODT_236","eNBId":"3600080082","siteName":"CA_SAC_SACRAMENTODT_236"},{"eNBName":"03600080083_5GDU_CA_SAC_SACRAMENTODT_236","eNBId":"3600080083","siteName":"CA_SAC_SACRAMENTODT_236"},{"eNBName":"03600080084_5GDU_CA_SAC_SACRAMENTODT_236","eNBId":"3600080084","siteName":"CA_SAC_SACRAMENTODT_236"}],"CA_SAC_SACRAMENTODT_237":[{"eNBName":"03600080085_5GDU_CA_SAC_SACRAMENTODT_237","eNBId":"3600080085","siteName":"CA_SAC_SACRAMENTODT_237"},{"eNBName":"03600080086_5GDU_CA_SAC_SACRAMENTODT_237","eNBId":"3600080086","siteName":"CA_SAC_SACRAMENTODT_237"}],"CA_SAC_PARKWAY_038":[{"eNBName":"03600050049_5GDU_CA_SAC_PARKWAY_038","eNBId":"3600050049","siteName":"CA_SAC_PARKWAY_038"},{"eNBName":"03600050050_5GDU_CA_SAC_PARKWAY_038","eNBId":"3600050050","siteName":"CA_SAC_PARKWAY_038"}],"CA_SAC_OAKPARK_163":[{"eNBName":"03600210025_5GDU_CA_SAC_OAKPARK_163","eNBId":"3600210025","siteName":"CA_SAC_OAKPARK_163"},{"eNBName":"03600210026_5GDU_CA_SAC_OAKPARK_163","eNBId":"3600210026","siteName":"CA_SAC_OAKPARK_163"},{"eNBName":"03600210027_5GDU_CA_SAC_OAKPARK_163","eNBId":"3600210027","siteName":"CA_SAC_OAKPARK_163"}],"CA_SAC_HAGGINWOOD_172":[{"eNBName":"03600480040_5GDU_CA_SAC_HAGGINWOOD_172","eNBId":"3600480040","siteName":"CA_SAC_HAGGINWOOD_172"},{"eNBName":"03600480041_5GDU_CA_SAC_HAGGINWOOD_172","eNBId":"3600480041","siteName":"CA_SAC_HAGGINWOOD_172"}],"CA_SAC_LANDPARK_065":[{"eNBName":"03600050037_5GDU_CA_SAC_LANDPARK_065","eNBId":"3600050037","siteName":"CA_SAC_LANDPARK_065"},{"eNBName":"03600050038_5GDU_CA_SAC_LANDPARK_065","eNBId":"3600050038","siteName":"CA_SAC_LANDPARK_065"}],"CA_SAC_WOODCREEK_037":[{"eNBName":"03600010151_5GDU_CA_SAC_WOODCREEK_037","eNBId":"3600010151","siteName":"CA_SAC_WOODCREEK_037"},{"eNBName":"03600010152_5GDU_CA_SAC_WOODCREEK_037","eNBId":"3600010152","siteName":"CA_SAC_WOODCREEK_037"},{"eNBName":"03600010153_5GDU_CA_SAC_WOODCREEK_037","eNBId":"3600010153","siteName":"CA_SAC_WOODCREEK_037"}],"CA_SAC_GOLDEN1_342":[{"eNBName":"03600050004_5GAU_CA_SAC_GOLDEN1_342","eNBId":"3600050004","siteName":"CA_SAC_GOLDEN1_342"},{"eNBName":"03600050005_5GAU_CA_SAC_GOLDEN1_342","eNBId":"3600050005","siteName":"CA_SAC_GOLDEN1_342"}],"CA_SAC_SACRAMENTODT_228":[{"eNBName":"03600080079_5GDU_CA_SAC_SACRAMENTODT_228","eNBId":"3600080079","siteName":"CA_SAC_SACRAMENTODT_228"},{"eNBName":"03600080080_5GDU_CA_SAC_SACRAMENTODT_228","eNBId":"3600080080","siteName":"CA_SAC_SACRAMENTODT_228"},{"eNBName":"03600080081_5GDU_CA_SAC_SACRAMENTODT_228","eNBId":"3600080081","siteName":"CA_SAC_SACRAMENTODT_228"}],"CA_SAC_WESTPARK_110":[{"eNBName":"03600010136_5GDU_CA_SAC_WESTPARK_110","eNBId":"3600010136","siteName":"CA_SAC_WESTPARK_110"},{"eNBName":"03600010137_5GDU_CA_SAC_WESTPARK_110","eNBId":"3600010137","siteName":"CA_SAC_WESTPARK_110"},{"eNBName":"03600010138_5GDU_CA_SAC_WESTPARK_110","eNBId":"3600010138","siteName":"CA_SAC_WESTPARK_110"}],"CA_SAC_WESTPARK_111":[{"eNBName":"03600010139_5GDU_CA_SAC_WESTPARK_111","eNBId":"3600010139","siteName":"CA_SAC_WESTPARK_111"},{"eNBName":"03600010140_5GDU_CA_SAC_WESTPARK_111","eNBId":"3600010140","siteName":"CA_SAC_WESTPARK_111"},{"eNBName":"03600010141_5GDU_CA_SAC_WESTPARK_111","eNBId":"3600010141","siteName":"CA_SAC_WESTPARK_111"}],"CA_SAC_WOODCREEK_077":[{"eNBName":"03600010160_5GDU_CA_SAC_WOODCREEK_077","eNBId":"3600010160","siteName":"CA_SAC_WOODCREEK_077"},{"eNBName":"03600010161_5GDU_CA_SAC_WOODCREEK_077","eNBId":"3600010161","siteName":"CA_SAC_WOODCREEK_077"},{"eNBName":"03600010162_5GDU_CA_SAC_WOODCREEK_077","eNBId":"3600010162","siteName":"CA_SAC_WOODCREEK_077"}],"CA_SAC_POCKET_022":[{"eNBName":"03600050079_5GDU_CA_SAC_POCKET_022","eNBId":"3600050079","siteName":"CA_SAC_POCKET_022"},{"eNBName":"03600050080_5GDU_CA_SAC_POCKET_022","eNBId":"3600050080","siteName":"CA_SAC_POCKET_022"},{"eNBName":"03600050081_5GDU_CA_SAC_POCKET_022","eNBId":"3600050081","siteName":"CA_SAC_POCKET_022"}],"CA_SAC_PARKWAY_049":[{"eNBName":"03600210034_5GDU_CA_SAC_PARKWAY_049","eNBId":"3600210034","siteName":"CA_SAC_PARKWAY_049"},{"eNBName":"03600210035_5GDU_CA_SAC_PARKWAY_049","eNBId":"3600210035","siteName":"CA_SAC_PARKWAY_049"},{"eNBName":"03600210036_5GDU_CA_SAC_PARKWAY_049","eNBId":"3600210036","siteName":"CA_SAC_PARKWAY_049"}],"CA_SAC_LANDPARK_073":[{"eNBName":"03600210064_5GDU_CA_SAC_LANDPARK_073","eNBId":"3600210064","siteName":"CA_SAC_LANDPARK_073"},{"eNBName":"03600210065_5GDU_CA_SAC_LANDPARK_073","eNBId":"3600210065","siteName":"CA_SAC_LANDPARK_073"},{"eNBName":"03600210066_5GDU_CA_SAC_LANDPARK_073","eNBId":"3600210066","siteName":"CA_SAC_LANDPARK_073"}],"CA_SAC_WESTPARK_109":[{"eNBName":"03600010127_5GDU_CA_SAC_WESTPARK_109","eNBId":"3600010127","siteName":"CA_SAC_WESTPARK_109"},{"eNBName":"03600010128_5GDU_CA_SAC_WESTPARK_109","eNBId":"3600010128","siteName":"CA_SAC_WESTPARK_109"},{"eNBName":"03600010129_5GDU_CA_SAC_WESTPARK_109","eNBId":"3600010129","siteName":"CA_SAC_WESTPARK_109"}],"CA_SAC_LANDPARK_036":[{"eNBName":"03600210091_5GDU_CA_SAC_LANDPARK_036","eNBId":"3600210091","siteName":"CA_SAC_LANDPARK_036"},{"eNBName":"03600210092_5GDU_CA_SAC_LANDPARK_036","eNBId":"3600210092","siteName":"CA_SAC_LANDPARK_036"},{"eNBName":"03600210093_5GDU_CA_SAC_LANDPARK_036","eNBId":"3600210093","siteName":"CA_SAC_LANDPARK_036"}],"CA_SAC_WOODCREEK_062":[{"eNBName":"03600010157_5GDU_CA_SAC_WOODCREEK_062","eNBId":"3600010157","siteName":"CA_SAC_WOODCREEK_062"},{"eNBName":"03600010158_5GDU_CA_SAC_WOODCREEK_062","eNBId":"3600010158","siteName":"CA_SAC_WOODCREEK_062"},{"eNBName":"03600010159_5GDU_CA_SAC_WOODCREEK_062","eNBId":"3600010159","siteName":"CA_SAC_WOODCREEK_062"}],"CA_SAC_GOLDEN1_191":[{"eNBName":"03600050001_5GAU_CA_SAC_GOLDEN1_191","eNBId":"3600050001","siteName":"CA_SAC_GOLDEN1_191"},{"eNBName":"03600050002_5GAU_CA_SAC_GOLDEN1_191","eNBId":"3600050002","siteName":"CA_SAC_GOLDEN1_191"},{"eNBName":"03600050003_5GAU_CA_SAC_GOLDEN1_191","eNBId":"3600050003","siteName":"CA_SAC_GOLDEN1_191"}]},"sessionId":"964a8be1","serviceToken":"55861","status":"SUCCESS"}');
                      if(jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User"){
                         this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                        }
                      if(jsonStatue.status == "SUCCESS"){
                        this.dropdownNEList = [];
                        // this.dropdownList = [];
                        this.neNameSiteList = jsonStatue.siteList;
                        // for (let itm of this.neNameList) {
                        //     let dropdownList = { item_id: itm.eNBId, item_text: itm.eNBName };
                        //     this.dropdownNEList.push(dropdownList);
                        // }
                        this.dropdownSettingsNE = {
                            singleSelection: true,
                            idField: 'item_id',
                            textField: 'item_text',
                            selectAllText: 'Select All',
                            unSelectAllText: 'UnSelect All',
                            itemsShowLimit: 1,
                            allowSearchFilter: true,
                            maxHeight: 300
                        };
                      }else{
                        this.showLoader = false;
                        this.neNameList = [];
                      }
                  }, 100); */
                  //Please Comment while checkIn
            });

        }
        }
        else {
            this.dropdownNEList = [];
            // this.dropdownList = [];
        }
    
      }
/*     getUseCaseList() {
        let neDetails = [];
        this.useCaseValue = [];
        this.scriptValue = [];
        this.selectedItems = [];       
       this.showLoader = true;
        setTimeout(() => {
            for (let i of this.selectedNEItems) {
                let selNE = {
                    "neId": i.item_id,
                    "neName": i.item_text
                }
                neDetails.push(selNE);
            }
            this.runtestService.getUseCaseListData(this.ciqDetails.ciqFileName, neDetails, this.commissionType, this.sharedService.createServiceToken())
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
                                        this.dropdownList = [];
                                        let useCaseList = jsonStatue.useCaseList;
                                        for (let itm of useCaseList) {
                                            let dropdownList = { item_id: itm, item_text: itm.useCaseName };
                                            this.dropdownList.push(dropdownList);
                                        }
                                        this.dropdownSettings = {
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
                                        this.neNameList = [];
                                    }
                                }
                            }

                        }, 1000);
                    },
                    error => {
                        //Please Comment while checkIn
                        setTimeout(() => {
                            this.showLoader = false;
                            let jsonStatue = JSON.parse('{"useCaseList":[{"useCaseName":"test batch cmdsys","useCaseId":106,"ucSleepInterval":"1","executionSequence":3,"scripts":[{"scriptId":54,"scriptSleepInterval":"1","useGeneratedScript":"YES","scriptName":"1_73081_Site_Specific_Script-20190202_V1.txt","scriptExeSequence":1}]},{"useCaseName":"RFxmlUsecase","useCaseId":109,"ucSleepInterval":"1","executionSequence":15,"scripts":[{"scriptId":50,"scriptSleepInterval":"1","useGeneratedScript":"YES","scriptName":"ls.sh","scriptExeSequence":1}]}],"sessionId":"72c37ec2","serviceToken":"68407","status":"SUCCESS"}');
                            if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
                                this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
                            }
                            if (jsonStatue.status == "SUCCESS") {
                                this.dropdownList = [];
                                let useCaseList = jsonStatue.useCaseList;
                                for (let itm of useCaseList) {
                                    let dropdownList = { item_id: itm, item_text: itm.useCaseName };
                                    this.dropdownList.push(dropdownList);
                                }
                                this.dropdownSettings = {
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
                                this.neNameList = [];
                            }
                        }, 100);
                        //Please Comment while checkIn
                    });

        }, 0);

    } */
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

    closeModel() {        
        this.successModalBlock.close();
        
        if (this.viewModalBlock) {
            this.viewModalBlock.close();
        }       
        //this.ngOnInit();
        this.getRunTest();
    }

    /* validates current submitted form is valid and free from errors
     * @param : pass the event
     * @retun : boolean
     */

    isValidForm(event) {
        return ($(event.target).parents("form").find(".error-border").length == 0) ? true : false;
    }

    resetValues(event) {     
        this.runTestForm.nativeElement.reset();          
        this.selectedItems =[];
        this.selectedNEItems =null;
        this.ciqDetails = [];
        this.ckeckedOrNot = false;
        this.rfScriptFlag = false;
        this.ranAtpFlag=false;
        this.bulkNeCheck = false;
        this.supportCA = false;
        this.isPRECHECKShow = false;
    }

    /* itemSelectionChanged(event) {
        if (this.selectedItems.length > 0) {
            this.validationData.rules.location.customfunction = false;
            
        } else {
            this.validationData.rules.location.customfunction = true;
        }
        validator.performValidation(event, this.validationData, "save_update");
    } */
   /*  togglevalidation(isrequired){
      if(isrequired){
        this.validationData.rules.lsmVersion.required = true;
        this.validationData.rules.lsmName.required = true; 
        this.validationData.rules.neName.required = false;  
        this.validationData.rules.testName.required = false; 
        this.validationData.rules.ciqName.required = false;  
        this.validationData.rules.location.required = false; 
      }
    } */
    generate(event)
    {    
    if (this.selectedNEItems != null && this.selectedNEItems.length>0  ) {
        this.validationData.rules.neName.required = false;

    } else {
        this.validationData.rules.neName.required = true;
    } 

    if (this.ciqDetails != "" && this.ciqDetails != undefined && this.ciqDetails.ciqFileName) {
        this.validationData.rules.ciqName.required = false;

    } else {
        this.validationData.rules.ciqName.required = true;

    }
    this.validationData.rules.location.required = false;
    
    /* if (this.selectedNEIDs && this.selectedNEIDs.length > 0) {
      this.validationData.rules.neIDs.required = false;

    } else {
      this.validationData.rules.neIDs.required = true;
    } */
    validator.performValidation(event, this.validationData, "save_update");
    setTimeout(()=>{
        this.runtestService.generatedetails( this.sharedService.createServiceToken())
        .subscribe(
            data => {
                setTimeout(() => {
                    let jsonStatue = data.json();
                    if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
                        this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });

                    } else {
                        if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {
                            if (jsonStatue.status == "SUCCESS") {
                                this.showLoader = false;
                                this.message = jsonStatue.reason;
                                this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: "static", size: "lg", windowClass: "success-modal" });
                            } else {
                                this.showLoader = false;
                                this.displayModel(jsonStatue.reason, "failureIcon");
                            }
                        }
                    }
                }, 1000);
            },
            error => {

                //Please Comment while checkIn
               /*  let jsonStatue: any = { "reason": "COMMISSION SCRIPT File Generated Successfully", "sessionId": "61e3c597", "serviceToken": "68126", "status": "SUCCESS" };

                setTimeout(() => {
                    this.showLoader = false;
                    if (jsonStatue.status == "403") {
                        this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: "static", size: "lg", windowClass: "session-modal" });
                    }
                    else {
                        if (jsonStatue.status == "SUCCESS") {
                            this.message = jsonStatue.reason;
                            this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: "static", size: "lg", windowClass: "success-modal" });
                        } else {
                            this.displayModel(jsonStatue.reason, "failureIcon");
                        }
                    }    
                }, 1000); */
                //Please Comment while checkIn
            
            });
        }, 0);
    }

    submitRunTestForm(event,requestType) {
        //  if (this.selectedItems && this.selectedItems.length > 0) {
        //     this.validationData.rules.location.required = false;

        // } else {
        //     this.validationData.rules.location.required = true;

        // }
        // else {
        console.log(this.versionDetails)
        if(requestType == 'RUN_TEST'){
            if(this.bulkNeCheck) {
                this.validationData.rules.ciqName.required = false;
                this.validationData.rules.neName.required = false;
                this.validationData.rules.location.required = false;
            }
            else {
                if (this.selectedNEItems != null && this.selectedNEItems.length>0  ) {
                    this.validationData.rules.neName.required = false;

                } else {
                    this.validationData.rules.neName.required = true;
                } 

                if (this.ciqDetails != "" && this.ciqDetails != undefined && this.ciqDetails.ciqFileName) {
                    this.validationData.rules.ciqName.required = false;

                } else {
                    this.validationData.rules.ciqName.required = true;

                }
                this.validationData.rules.location.required = false;
            }
        }
      
        if(requestType == 'CHECK_CONNECTION'){
            if(this.versionDetails != null && this.versionDetails != undefined)
                this.validationData.rules.lsmVersion.required = false;
            else 
                this.validationData.rules.lsmVersion.required = true;

            if(this.nameDetails != null && this.nameDetails != undefined)
                this.validationData.rules.lsmName.required = true;
            else
                this.validationData.rules.lsmName.required = false;
            this.validationData.rules.neName.required = false;  
           // this.validationData.rules.testName.required = false; 
            this.validationData.rules.ciqName.required = false;  
           this.validationData.rules.location.required = false; 
        }        

        const formdata = new FormData();
        if (this.isRETUseCaseSelected) {
            let files: FileList = this.filePostRef.nativeElement.files,
                filenames = [];
            for (var i = 0; i < files.length; i++) {
                formdata.append("UPLOAD", files[i]);
                //formdata.append(ciqFiles[i].name, ciqFiles[i]);
                filenames.push(files[i].name);
            }
        }
        validator.performValidation(event, this.validationData, "save_update");
        setTimeout(() => {
            if (this.isValidForm(event)) {
                this.showLoader = true;
                let useCaseDetails = [], NEuseCaseDetails = [], PAuseCaseDetails = [] ,neDetails =[],scriptDetails=[], pascriptDetails=[], nescriptDetails = [],bulkNEData="";
                this.runTestDetails ={};
                if (this.useCaseValue && this.useCaseValue.length > 0) {
                    for (let i of this.useCaseValue) {
                        let selUseCase = {
                            "useCaseName": i.useCaseName,
                            "useCaseId": i.useCaseId,
                            "executionSequence": i.updatedExecution,
                            "ucSleepInterval": i.ucSleepInterval ? i.ucSleepInterval : ""
                        }
                        useCaseDetails.push(selUseCase);
                    }
                }
                if (this.scriptValue && this.scriptValue.length > 0) {
                    for (let i of this.scriptValue) {
                        let selScript = {
                            "useCaseName": i.useCaseName,
                            "scriptName": i.scriptName,
                            "scriptId": i.scriptId,
                            "scriptExeSequence": i.updatedExecution,
                            "scriptSleepInterval" : i.scriptSleepInterval,
                            "useGeneratedScript" : i.useGeneratedScript
                        }
                        scriptDetails.push(selScript);
                    }
                }
                if (this.PAuseCaseValue && this.PAuseCaseValue.length > 0) {
                    for (let i of this.PAuseCaseValue) {
                        let selUseCase = {
                            "useCaseName": i.useCaseName,
                            "useCaseId": i.useCaseId,
                            "executionSequence": i.updatedExecution,
                            "ucSleepInterval": i.ucSleepInterval ? i.ucSleepInterval : ""
                        }
                        PAuseCaseDetails.push(selUseCase);
                    }
                }
                if (this.PAscriptValue && this.PAscriptValue.length > 0) {
                    for (let i of this.PAscriptValue) {
                        let selScript = {
                            "useCaseName": i.useCaseName,
                            "scriptName": i.scriptName,
                            "scriptId": i.scriptId,
                            "scriptExeSequence": i.updatedExecution,
                            "scriptSleepInterval" : i.scriptSleepInterval,
                            "useGeneratedScript" : i.useGeneratedScript
                        }
                        pascriptDetails.push(selScript);
                    }
                }

                if (this.NEuseCaseValue && this.NEuseCaseValue.length > 0) {
                    for (let i of this.NEuseCaseValue) {
                        let selUseCase = {
                            "useCaseName": i.useCaseName,
                            "useCaseId": i.useCaseId,
                            "executionSequence": i.updatedExecution,
                            "ucSleepInterval": i.ucSleepInterval ? i.ucSleepInterval : ""
                        }
                        NEuseCaseDetails.push(selUseCase);
                    }
                }
                if (this.NEscriptValue && this.NEscriptValue.length > 0) {
                    for (let i of this.NEscriptValue) {
                        let selScript = {
                            "useCaseName": i.useCaseName,
                            "scriptName": i.scriptName,
                            "scriptId": i.scriptId,
                            "scriptExeSequence": i.updatedExecution,
                            "scriptSleepInterval" : i.scriptSleepInterval,
                            "useGeneratedScript" : i.useGeneratedScript
                        }
                        nescriptDetails.push(selScript);
                    }
                }
                // console.log(this.ranAtpFlag,useCaseDetails)
                // if(this.ranAtpFlag == true){
                //     let selUseCase = {
                //         "useCaseName": "Ran_Atp",
                //         "useCaseId": 64857,
                //         "executionSequence": 64857,
                //         "ucSleepInterval": 1000
                //     }
                //     useCaseDetails.push(selUseCase);
                // }
                // console.log("vjffjhjghhsh")
                /* let useCaseMig = [];
                for(let i = 0; i < this.selectedItems.length; i++) {
                    useCaseMig.push(this.selectedItems[i]);
                } */

                /* let useCasePostMig = [];
                for(let i = 0; i < this.selectedItemsPMig.length; i++) {
                    useCasePostMig.push(this.selectedItemsPMig[i].item_text);
                } */
                if(!this.bulkNeCheck) {
                    if(this.programName!='VZN-5G-MM'){
                        if(this.selectedNEItems) {
                            for(let item of this.selectedNEItems ){
                                let selNE = {
                                    "neId": item.eNBId,
                                    "neName": item.eNBName
                                }
                                neDetails.push(selNE);
                            }
                        }
                    }
                    else {
                        if(this.selectedItemsNE) {
                            for(let item of this.selectedItemsNE ){
                                let selNE = {
                                    "neId": item.item_id,
                                    "neName": item.item_text,
                                    "siteName": item.item_siteName
                                }
                                    neDetails.push(selNE);
                            }
                        }
                    }
                }
                else {
                    bulkNEData = this.rfScriptList && this.rfScriptList.length > 0 ? this.rfScriptList.split("\n") : null;
                }
                let currentForm = event.target.parentNode.parentNode,
                    runTestFormDetails = {
                       // "testname": currentForm.querySelector("#testName").value,                        
                        "lsmVersion": "",
                        "lsmName": "",
                        "lsmId":"",
                        "ciqName":this.ciqDetails ? this.ciqDetails.ciqFileName : "",
                        "checklistFileName": this.ciqDetails ? this.ciqDetails.checklistFileName : "",
                        "checklistFilePath": this.ciqDetails ? this.ciqDetails.checklistFilePath : "",
                        "neDetails":neDetails,
                        "useCase": this.selectedItems,
                        //"scripts" :scriptDetails,
                      //  "testDesc": currentForm.querySelector("#testDescription").value,
                        "password":"",
                        "currentPassword":this.ckeckedOrNot,
                        "rfScriptFlag": this.rfScriptFlag,
                        "prePostAuditFlag": this.prePostAuditFlag,
                        "ovUpdate": this.ovUpdate ? true : false,
                        "multipleDuo": this.multipleDuo == null ? false : this.multipleDuo
                        //"radioUnits" : this.selectedRadioUnittems && this.selectedRadioUnittems.length > 0 ? this.selectedRadioUnittems : null,
                    },
                    runTestFormDetailsPost = JSON.parse(JSON.stringify(runTestFormDetails)),
                    runTestFormDetailsNEGrow = JSON.parse(JSON.stringify(runTestFormDetails)),
                    runTestFormDetailsPreAudit = JSON.parse(JSON.stringify(runTestFormDetails)),
                    runTestFormDetailsNeStatus = JSON.parse(JSON.stringify(runTestFormDetails));
                    runTestFormDetailsPost.useCase = useCaseDetails;//useCasePostMig;
                    runTestFormDetailsPost.scripts = scriptDetails;
                    runTestFormDetailsNEGrow.useCase = this.selectedItemsNG; //Static UseCases from Vijay
                    runTestFormDetailsPreAudit.useCase= PAuseCaseDetails;
                    runTestFormDetailsPreAudit.scripts= pascriptDetails;
                    runTestFormDetailsNeStatus.useCase= NEuseCaseDetails;
                    runTestFormDetailsNeStatus.scripts= nescriptDetails;
                    runTestFormDetailsPost.prePostAuditFlag = false;
                    runTestFormDetailsNEGrow.prePostAuditFlag = false;
                    runTestFormDetailsPreAudit.prePostAuditFlag = false;
                    runTestFormDetailsNeStatus.prePostAuditFlag = false;
                    //  this.neVersion = this.selectedNeVersion?this.selectedNeVersion : null;
                     this.fsuType = this.selectedFSUType ? this.selectedFSUType : null;
                   

                    if(requestType == 'CHECK_CONNECTION'){
                        runTestFormDetails.lsmName=this.nameDetails.name;
                        runTestFormDetails.lsmVersion=this.versionDetails.name;
                        runTestFormDetails.lsmId=this.nameDetails.id;
                       
                    }
                if(runTestFormDetailsNEGrow.useCase && runTestFormDetailsNEGrow.useCase.length < 1 && 
                    runTestFormDetails.useCase && runTestFormDetails.useCase.length < 1 &&
                    (runTestFormDetailsPost.useCase && runTestFormDetailsPost.useCase.length > 0 || 
                    runTestFormDetailsPreAudit.useCase && runTestFormDetailsPreAudit.useCase.length > 0
                    || runTestFormDetailsNeStatus.useCase && runTestFormDetailsNeStatus.useCase.length > 0))
                    {
                        this.premigData = false;
                    }
                    else
                    this.premigData=true;

                this.runTestDetails = runTestFormDetails;
                this.runTestDetailsPost=runTestFormDetailsPost;
                this.runTestPreAudit=runTestFormDetailsPreAudit;
                this.runTestNeStatus=runTestFormDetailsNeStatus;
                this.runTestDetailsNEGrow=runTestFormDetailsNEGrow;
                if(this.programName !='VZN-5G-MM')
                    {
                        this.generateAllSites=false;
                    }
                   
                let migrationStrategy = this.programName == 'VZN-4G-USM-LIVE' ? this.migrationStrategy : null;
                this.runtestService.uploadRunTestDetails(this.sharedService.createServiceToken(),this.premigData,this.ranAtpFlag, runTestFormDetails,this.commissionType,requestType,runTestFormDetailsPost,runTestFormDetailsNEGrow,this.generateAllSites,this.selectedNEIDs, this.fsuType,this.paginationDetails, migrationStrategy, bulkNEData, this.supportCA, this.isRETUseCaseSelected, this.isRETUseCaseSelected ? formdata : null, runTestFormDetailsPreAudit, runTestFormDetailsNeStatus)
                    .subscribe(
                        data => {
                            let jsonStatue = data.json();
                            if(requestType == 'CHECK_CONNECTION'){
                                this.checkConnLogModalBlock.close();
                            }
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
                                        this.showLoader = false;
                                        
                                        if(jsonStatue.requestType == 'CHECK_CONNECTION'){                                           
                                            this.message = "Connection Check Successful";
                                        }else if(jsonStatue.requestType == 'GENERATE'){                                           
                                            this.message = "Script generated Successfully";
                                        }else{
                                            this.message = "WorkFlow Management started successfully!";
                                        }
                                        this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                                        this.resetValues(event);
                                    } else if (jsonStatue.status == "PROMPT") {
                                        this.showLoader = false;
                                        this.serverInfo = jsonStatue.password;
                                        this.requestType = jsonStatue.requestType;
                                        this.pswdModalBlock =  this.modalService.open(this.passwordModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal connectionPrompt' });                                 
                                    }else {
                                        if(jsonStatue.requestType == 'CHECK_CONNECTION'){
                                           
                                        }
                                        this.showLoader = false;
                            
                                        this.displayModel(jsonStatue.reason, "failureIcon");

                                    }
                                    
                                }
                            }
                        },
                        error => {
                            //Please Comment while checkIn

                            /* setTimeout(() => {

                                //this.showLoader = false;

                                let jsonStatue = {"password":{"serverName":"Sane","serverIp":"10.20.120.82"},"requestType":"CHECK_CONNECTION","sessionId":"2edb0f8c","serviceToken":"56139","status":"SUCCESS"};

                               if (jsonStatue.status == "SUCCESS") {
                                    this.showLoader = false;
                                   
                                    if(jsonStatue.requestType == 'CHECK_CONNECTION'){                                           
                                        this.message = "Connection Check Successful";
                                    }else if(jsonStatue.requestType == 'GENERATE'){                                           
                                        this.message = "Script generated Successfully";
                                    }else{
                                        this.message = "Run test started successfully!";
                                    }
                                    this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                                    this.resetValues(event);
                                } else  if (jsonStatue.status == "PROMPT") {
                                    this.showLoader = false;
                                    this.serverInfo = jsonStatue.password;
                                    this.requestType = jsonStatue.requestType;
                                    this.pswdModalBlock =  this.modalService.open(this.passwordModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal connectionPrompt' });                                 
                                }else{
                                    if(jsonStatue.requestType == 'CHECK_CONNECTION'){
                                           
                                    }
                                    this.showLoader = false;
                        
                                    //this.displayModel(jsonStatue.reason, "failureIcon");
                                }

                            }, 100); */

                            //Please Comment while checkIn   
                        });
            }
        }, 0);

    }
    removeItem(value){
        let neitems: any;

        neitems = Object.assign([], this.selectedItemsNE)
        const index: number = neitems.indexOf(value);
        neitems.splice(index, 1)

        this.selectedItemsNE = Object.assign([], neitems);
        // Get post migration use cases on Site Name popup change in list
        // if(this.programName == 'VZN-5G-DSS' || this.programName == 'VZN-5G-CBAND') {
        //     this.getMigUseCases('pre', null, false, true);
        //     this.getMigUseCases('post', null, false, true);
        // } else {
        //     this.getMigUseCases('post', null, false, true);
        // }
        this.getMigUseCases('pre', null, false, true);
        this.getMigUseCases('post', null, false, true);
        this.getMigUseCases('nes', null, false, true);
       // this.getMigUseCases(true, null, false, true);
       // this.getMigPAUseCases(true, null, false, true);
    }

    onChangeNEsOnSiteName() {
        if(this.programName == 'VZN-5G-MM'){
            // Get post migration use cases on Site Name popup dropdown change
            this.getMigUseCases('post', null, false, true);
            this.getMigUseCases('pre', null, false, true);
            this.getMigUseCases('nes', null, false, true);
            //this.getMigPAUseCases(true, null, false, true);
        }
    }
    
    onChangeNEs(event: any) {
        if(this.bulkNeCheck) {
            if(this.programName == 'VZN-5G-MM'){
                this.getMigUseCases('post', null, true);
                this.getMigUseCases('nes', null, true);
                this.getMigUseCases('pre', null, true);
               // this.getMigPAUseCases(true, null, false, true);
            }
            else {
                this.getMigUseCases('pre');
                this.getMigUseCases('post');
                this.getMigUseCases('nes');
                // if(this.programName == 'VZN-5G-DSS' || this.programName == 'VZN-5G-CBAND') {
                //     this.getMigUseCases('pre');
                //     this.getMigUseCases('post');
                //     this.getMigUseCases('nes');
                // } else {
                //     this.getMigUseCases('post');
                //     this.getMigUseCases('nes');
                // }
               // this.getMigPAUseCases(true);
            }
        }
        else if(this.selectedNEItems) {
            this.dropdownListNE=[];
            if(this.programName == 'VZN-5G-MM'){
                for (let itm of this.selectedNEItems) {
                 
                    for(let item of itm.value){
                       
                        let dropdownListNE = { item_id: item.eNBId, item_text: item.eNBName, item_siteName: itm.key };
                        
                        this.dropdownListNE.push(dropdownListNE);
                    }
                    // Select all items by default
                    this.selectedItemsNE = this.dropdownListNE;
                }
                // Get post migration use cases
                this.getMigUseCases('post', null, true);
                this.getMigUseCases('nes', null, true);
                this.getMigUseCases('pre', null, true);
               // this.getMigPAUseCases(true, null, true);
            }
            else {
                this.getMigUseCases('pre');
                this.getMigUseCases('post');
                this.getMigUseCases('nes');
                // if(this.programName == 'VZN-5G-DSS' || this.programName == 'VZN-5G-CBAND') {
                //     this.getMigUseCases('pre');
                //     this.getMigUseCases('post');
                //     this.getMigUseCases('nes');
                // } else {
                //     this.getMigUseCases('post');
                //     this.getMigUseCases('nes');
                // }
                
                //this.getMigUseCases(true);
            }
            
            // if(this.selectedNEItems.length==1){
                
            // if(this.programName!='VZN-5G-MM')
            //    this.selectedNEItemss=this.selectedNEItems[0];
            // else
            //     this.selectedNEItemss=this.selectedNEItems[0].value[0];

            //     console.log(this.selectedNEItemss)
            // this.showLoader = true;
            // this.runtestService.getNeConfigDetails(this.sharedService.createServiceToken(), this.ciqDetails, this.selectedNEItemss)
            //     .subscribe(
            //         data => {
            //             setTimeout(() => {
            //                 let jsonStatue = data.json();
            //                 this.showLoader = false;
            //                 if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
            //                     this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
            //                 } else {
            //                     if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {
            //                         if (jsonStatue.status == "SUCCESS") {
            //                             // this.versionDetails = jsonStatue.lsmVersion;
            //                             this.versionDetails = this.smVersion.filter((value) => value.name == jsonStatue.lsmVersion)[0];
            //                             if (this.versionDetails) {
            //                                 this.getLsmName(this.versionDetails);
            //                                 setTimeout(() => {
            //                                     this.nameDetails = this.lsmNameDetails.filter((value) => value.name == jsonStatue.lsmName)[0];
            //                                 }, 100);
            //                                 this.disabled=true;
            //                             }
            //                             else {
            //                                 this.versionDetails = "";
            //                                 this.nameDetails = "";
            //                                 this.disabled=false;
            //                             }
            //                             // this.getMigUseCases();
            //                         } else {
            //                             this.showLoader = false;
            //                         }
            //                     }
            //                 }

            //             }, 0);
            //         },
            //         error => {
            //             //Please Comment while checkIn
            //             setTimeout(() => {
            //                 this.showLoader = false;
            //                 let jsonStatue = JSON.parse('{"sessionId":"458fa5b1","serviceToken":60748,"lsmVersion":"20.A.0","lsmName":"test5g","lsmid":106,"status":"SUCCESS"}');
            //                 if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
            //                     this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
            //                 }
            //                 if (jsonStatue.status == "SUCCESS") {
            //                     // this.versionDetails = jsonStatue.lsmVersion;
            //                     this.versionDetails = this.smVersion.filter((value) => value.name == jsonStatue.lsmVersion)[0];
            //                     if (this.versionDetails) {
            //                         this.getLsmName(this.versionDetails);
            //                         setTimeout(() => {
            //                             this.nameDetails = this.lsmNameDetails.filter((value) => value.name == jsonStatue.lsmName)[0];
            //                         }, 100);
            //                         this.disabled=true;
            //                     }
            //                     else {
            //                         this.versionDetails = "";
            //                         this.nameDetails = "";
            //                         this.disabled=false;
            //                     }
            //                     // this.getMigUseCases();
            //                 } else {
            //                     this.showLoader = false;
            //                 }
            //             }, 100);
            //             //Please Comment while checkIn
            //         });
            //     }
            //     else {
            //         this.versionDetails = "";
            //         this.nameDetails = "";
            //         this.disabled=false;
            //     }
        }
        // else {
        //     this.versionDetails = "";
        //     this.nameDetails = "";
        //     this.disabled=false;
        // }
    }
    onChangePAUseCase(event: any) {
        setTimeout(() => {
            this.PAuseCaseValue = [];
            this.PAscriptValue =[];
            
            for (let i of this.selectedItemsPAMig) {
                let useCaseData = {
                    "useCaseName": i["item_text"],
                    "useCaseId": i["item_id"].useCaseId,
                    "actualExecution": i["item_id"].executionSequence,
                    "updatedExecution": i["item_id"].executionSequence,
                    "ucSleepInterval":i["item_id"].ucSleepInterval,
                    "oldValue": "",
                    "intervalOldValue":"",
                    "inEditMode": false,
                    "rowUpdate": false
                }
                this.PAuseCaseValue.push(useCaseData);
            }
            
            for (let scriptList of this.selectedItemsPAMig) {
                for(let script of scriptList["item_id"].scripts){
                    let scriptData = {
                        "useCaseName": scriptList["item_text"],
                        "scriptName":script.scriptName,
                        "scriptId":script.scriptId,
                        "actualExecution":script.scriptExeSequence,
                        "updatedExecution":script.scriptExeSequence,
                        "scriptSleepInterval":script.scriptSleepInterval,
                        "useGeneratedScript":script.useGeneratedScript,
                        "oldValue": "",
                        "intervalOldValue":"",
                        "useGeneratedScript_old":false,
                        "inEditMode": false,
                        "rowUpdate": false
                    }
                    this.PAscriptValue.push(scriptData);
                }              
                
            }
            //this.isRETUseCaseSelected = this.checkRETUsecaseAvl();
            // if(this.programName=='VZN-4G-USM-LIVE') {
            //     this.isRSSIUseCaseSelected = this.checkRSSIUsecaseAvl();
            //     this.isRSSIUseCaseSelected ? this.getRadioUnitList() : "";
            // } 
        }, 0);

    }
    
    onChangeNEUseCase(event: any) {
        setTimeout(() => {
            this.NEuseCaseValue = [];
            this.NEscriptValue =[];
            
            for (let i of this.selectedItemsNEMig) {
                let useCaseData = {
                    "useCaseName": i["item_text"],
                    "useCaseId": i["item_id"].useCaseId,
                    "actualExecution": i["item_id"].executionSequence,
                    "updatedExecution": i["item_id"].executionSequence,
                    "ucSleepInterval":i["item_id"].ucSleepInterval,
                    "oldValue": "",
                    "intervalOldValue":"",
                    "inEditMode": false,
                    "rowUpdate": false
                }
                this.NEuseCaseValue.push(useCaseData);
            }
            
            for (let scriptList of this.selectedItemsNEMig) {
                for(let script of scriptList["item_id"].scripts){
                    let scriptData = {
                        "useCaseName": scriptList["item_text"],
                        "scriptName":script.scriptName,
                        "scriptId":script.scriptId,
                        "actualExecution":script.scriptExeSequence,
                        "updatedExecution":script.scriptExeSequence,
                        "scriptSleepInterval":script.scriptSleepInterval,
                        "useGeneratedScript":script.useGeneratedScript,
                        "oldValue": "",
                        "intervalOldValue":"",
                        "useGeneratedScript_old":false,
                        "inEditMode": false,
                        "rowUpdate": false
                    }
                    this.NEscriptValue.push(scriptData);
                }              
                
            }
            //this.isRETUseCaseSelected = this.checkRETUsecaseAvl();
            // if(this.programName=='VZN-4G-USM-LIVE') {
            //     this.isRSSIUseCaseSelected = this.checkRSSIUsecaseAvl();
            //     this.isRSSIUseCaseSelected ? this.getRadioUnitList() : "";
            // } 
        }, 0);
    }
    onChangeUseCase(event: any, singleSelect = false) {
        singleSelect && this.updateUseCaseSelection(event);
        setTimeout(() => {
            this.useCaseValue = [];
            this.scriptValue =[];
            
            for (let i of this.selectedItemsPMig) {
                let useCaseData = {
                    "useCaseName": i["item_text"],
                    "useCaseId": i["item_id"].useCaseId,
                    "actualExecution": i["item_id"].executionSequence,
                    "updatedExecution": i["item_id"].executionSequence,
                    "ucSleepInterval":i["item_id"].ucSleepInterval,
                    "oldValue": "",
                    "intervalOldValue":"",
                    "inEditMode": false,
                    "rowUpdate": false
                }
                this.useCaseValue.push(useCaseData);
            }
            
            for (let scriptList of this.selectedItemsPMig) {
                for(let script of scriptList["item_id"].scripts){
                    let scriptData = {
                        "useCaseName": scriptList["item_text"],
                        "scriptName":script.scriptName,
                        "scriptId":script.scriptId,
                        "actualExecution":script.scriptExeSequence,
                        "updatedExecution":script.scriptExeSequence,
                        "scriptSleepInterval":script.scriptSleepInterval,
                        "useGeneratedScript":script.useGeneratedScript,
                        "oldValue": "",
                        "intervalOldValue":"",
                        "useGeneratedScript_old":false,
                        "inEditMode": false,
                        "rowUpdate": false
                    }
                    this.scriptValue.push(scriptData);
                }              
                
            }
            this.isRETUseCaseSelected = this.checkRETUsecaseAvl();
            // if(this.programName=='VZN-4G-USM-LIVE') {
            //     this.isRSSIUseCaseSelected = this.checkRSSIUsecaseAvl();
            //     this.isRSSIUseCaseSelected ? this.getRadioUnitList() : "";
            // } 
        }, 0);

    }
    updateUseCaseSelection(currSelectedItem) {
        if(currSelectedItem && (currSelectedItem["item_text"].indexOf("vDUInstantiation") >= 0)) {
            for(let useCaseItem of this.useCaseListPM) {
                if(useCaseItem["item_text"].indexOf("mmuAuditReport") >= 0) {
                    if (!this.checkUseCaseAvl("mmuAuditReport", this.selectedItemsPMig)) {
                        let selectedItem = this.selectedItemsPMig.map((item) => item);
                        selectedItem.push(useCaseItem);

                        this.selectedItemsPMig = selectedItem;
                        break;
                    }
                }
            }
        }
        if(this.programName == 'VZN-4G-USM-LIVE' && currSelectedItem && (currSelectedItem["item_text"].toLowerCase().indexOf("cbrs") >= 0)) {
            for(let useCaseItem of this.useCaseListPM) {
                if ((useCaseItem["item_text"].toLowerCase().indexOf("fcc") >= 0) && !this.checkUseCaseAvl(useCaseItem["item_text"], this.selectedItemsPMig)) {
                    let selectedItems = this.selectedItemsPMig.map((item) => item);
                    selectedItems.push(useCaseItem);
                    this.selectedItemsPMig = selectedItems;
                }
            }
        }
    }

    checkUseCaseAvl(useCaseText, selectedItems) {
        let isAvailableInUC = false;
        for (let useCaseItem of selectedItems) {
            if(useCaseItem["item_text"].indexOf(useCaseText) >= 0) {
                isAvailableInUC = true;
                break;
            }
        }
        return isAvailableInUC;
    }
    
    onUseCaseSelection(event: any) {
        setTimeout(() => {
            if(this.programName =="VZN-5G-MM" || this.programName =="VZN-4G-USM-LIVE") {
                if (this.selectedItemsNG.indexOf('pnp') > -1) {
                    this.selectedItemsNG = []
                    this.selectedItemsNG.push("pnp")
                }
            }
            
        }, 0);
    }

    onChangeMigUseCase(event: any) {
        setTimeout(() => {
            if(this.programName =="VZN-5G-DSS" || this.programName =="VZN-5G-CBAND") {
                if(this.selectedItems.length ==1){
                    for (let i of this.selectedItems) {
                        if( i.indexOf("Pre") >= 0) {
                          this.isPRECHECKShow = true;
                          break;
                        }
                      }
                } else {
                    this.isPRECHECKShow = false;
                }
            } else {
                this.isPRECHECKShow = false;
            }
            
        }, 0);
    }
    checkPreCheckUseCaseAvl() {
        let isAvailableInUC = false;
        if(this.MiguseCaseValue.length == 1) {
            for (let useCaseItem of this.MiguseCaseValue) {
         
                if(useCaseItem["useCaseName"].indexOf("Pre") >= 0) {
                    isAvailableInUC = true;
                    break;
                } 
            }
        } else {
            isAvailableInUC = false;
        }
       
        return isAvailableInUC;
    }
    checkRETUsecaseAvl() {
        let isAvailableInUC = false;
        for (let useCaseItem of this.useCaseValue) {
            if(useCaseItem["useCaseName"].indexOf("RET") >= 0) {
                isAvailableInUC = true;
                this.isRETDisabled = false;
                break;
            } else {
                this.isRETDisabled = true;
            }
        }
        return isAvailableInUC;
    }
    // checkRSSIUsecaseAvl() {
    //     let isAvailableInUC = false;
    //     for (let useCaseItem of this.useCaseValue) {
    //         if(useCaseItem["useCaseName"].indexOf("rssi") >= 0) {
    //             isAvailableInUC = true;
    //             this.isRSSIDisabled = false;
    //             break;
    //         } else {
    //             this.isRSSIDisabled = true;
    //             this.dropdownRadioUnitList = [];
    //             this.selectedRadioUnittems = [];
    //         }
    //     }
    //     return isAvailableInUC;
    // }
  
    onChangeNGUseCase(event: any) {
        setTimeout(() => {
          this.NGuseCaseValue = [];
          this.scriptValueNG = [];
    
          for (let i of this.selectedNGItems) {
            let useCaseData = {
              "useCaseName": i["item_text"],
              "useCaseId": i["item_id"].useCaseId,
              "actualExecution": i["item_id"].executionSequence,
              "updatedExecution": i["item_id"].executionSequence,
              "ucSleepInterval": i["item_id"].ucSleepInterval,
              "oldValue": "",
              "intervalOldValue": "",
              "inEditMode": false,
              "rowUpdate": false
            }
            this.NGuseCaseValue.push(useCaseData);
          }
          for (let scriptList of this.selectedNGItems) {
            for (let script of scriptList["item_id"].scripts) {
              let scriptData = {
                "useCaseName": scriptList["item_text"],
                "scriptName": script.scriptName,
                "scriptId": script.scriptId,
                "actualExecution": script.scriptExeSequence,
                "updatedExecution": script.scriptExeSequence,
                "scriptSleepInterval": script.scriptSleepInterval,
                "useGeneratedScript": script.useGeneratedScript,
                "oldValue": "",
                "intervalOldValue": "",
                "useGeneratedScript_old": false,
                "inEditMode": false,
                "rowUpdate": false
              }
              this.scriptValueNG.push(scriptData);
            }
    
          }
    
        }, 0);
    
      }

    updateAutoSelection(currSelectedItem) {
        if(this.programName == 'VZN-4G-USM-LIVE' && currSelectedItem && (currSelectedItem["item_text"].toLowerCase().indexOf("cbrs") >= 0)) {
            for(let useCaseItem of this.dropdownPMigList) {
                if ((useCaseItem["item_text"].toLowerCase().indexOf("fcc") >= 0) && !this.checkUseCaseAvl(useCaseItem["item_text"], this.selectedPMigItems)) {
                    let selectedItems = this.selectedPMigItems.map((item) => item);
                    selectedItems.push(useCaseItem);
                    this.selectedPMigItems = selectedItems;
                }
            }
        }
    }

    onChangePMigUseCase(event: any, singleSelect = false) {
        setTimeout(() => {
            this.PMiguseCaseValue = [];
            this.scriptValuePMig =[];
            singleSelect && this.updateAutoSelection(event)
            for (let i of this.selectedPMigItems) {
                let useCaseData = {
                    "useCaseName": i["item_text"],
                    "useCaseId": i["item_id"].useCaseId,
                    "actualExecution": i["item_id"].executionSequence,
                    "updatedExecution": i["item_id"].executionSequence,
                    "ucSleepInterval":i["item_id"].ucSleepInterval,
                    "oldValue": "",
                    "intervalOldValue":"",
                    "inEditMode": false,
                    "rowUpdate": false
                }
                this.PMiguseCaseValue.push(useCaseData);
            }
            
            for (let scriptList of this.selectedPMigItems) {
                for(let script of scriptList["item_id"].scripts){
                    let scriptData = {
                        "useCaseName": scriptList["item_text"],
                        "scriptName":script.scriptName,
                        "scriptId":script.scriptId,
                        "actualExecution":script.scriptExeSequence,
                        "updatedExecution":script.scriptExeSequence,
                        "scriptSleepInterval":script.scriptSleepInterval,
                        "useGeneratedScript":script.useGeneratedScript,
                        "oldValue": "",
                        "intervalOldValue":"",
                        "useGeneratedScript_old":false,
                        "inEditMode": false,
                        "rowUpdate": false
                    }
                    this.scriptValuePMig.push(scriptData);
                }              
                
            }
            
        }, 0);

    }
   
    onChangePAMigUseCase(event: any) {
        setTimeout(() => {
            this.PAMiguseCaseValue = [];
            this.pascriptValuePMig =[];
            this.PMiguseCaseValue = [];
            this.scriptValuePMig =[];
            for (let i of this.selectedPAMigItems) {
                let useCaseData = {
                    "useCaseName": i["item_text"],
                    "useCaseId": i["item_id"].useCaseId,
                    "actualExecution": i["item_id"].executionSequence,
                    "updatedExecution": i["item_id"].executionSequence,
                    "ucSleepInterval":i["item_id"].ucSleepInterval,
                    "oldValue": "",
                    "intervalOldValue":"",
                    "inEditMode": false,
                    "rowUpdate": false
                }
                this.PAMiguseCaseValue.push(useCaseData);
            }
            
            for (let scriptList of this.selectedPAMigItems) {
                for(let script of scriptList["item_id"].scripts){
                    let scriptData = {
                        "useCaseName": scriptList["item_text"],
                        "scriptName":script.scriptName,
                        "scriptId":script.scriptId,
                        "actualExecution":script.scriptExeSequence,
                        "updatedExecution":script.scriptExeSequence,
                        "scriptSleepInterval":script.scriptSleepInterval,
                        "useGeneratedScript":script.useGeneratedScript,
                        "oldValue": "",
                        "intervalOldValue":"",
                        "useGeneratedScript_old":false,
                        "inEditMode": false,
                        "rowUpdate": false
                    }
                    this.pascriptValuePMig.push(scriptData);
                }              
                
            }
            
        }, 0);

    }
    onChangeNEMigUseCase(event: any) {
        setTimeout(() => {
            this.NGuseCaseValue = [];
            this.scriptValueNG = [];
            this.PAMiguseCaseValue = [];
            this.pascriptValuePMig =[];
            this.PMiguseCaseValue = [];
            this.scriptValuePMig =[];
            this.NSuseCaseValue=[];
            this.NSscriptValue = [];
            for (let i of this.selectedNGStatusItems) {
                let useCaseData = {
                    "useCaseName": i["item_text"],
                    "useCaseId": i["item_id"].useCaseId,
                    "actualExecution": i["item_id"].executionSequence,
                    "updatedExecution": i["item_id"].executionSequence,
                    "ucSleepInterval":i["item_id"].ucSleepInterval,
                    "oldValue": "",
                    "intervalOldValue":"",
                    "inEditMode": false,
                    "rowUpdate": false
                }
                this.NSuseCaseValue.push(useCaseData);
            }
            
            for (let scriptList of this.selectedNGStatusItems) {
                for(let script of scriptList["item_id"].scripts){
                    let scriptData = {
                        "useCaseName": scriptList["item_text"],
                        "scriptName":script.scriptName,
                        "scriptId":script.scriptId,
                        "actualExecution":script.scriptExeSequence,
                        "updatedExecution":script.scriptExeSequence,
                        "scriptSleepInterval":script.scriptSleepInterval,
                        "useGeneratedScript":script.useGeneratedScript,
                        "oldValue": "",
                        "intervalOldValue":"",
                        "useGeneratedScript_old":false,
                        "inEditMode": false,
                        "rowUpdate": false
                    }
                    this.NSscriptValue.push(scriptData);
                }              
                
            }
            
        }, 0);

    }
    onChangeMigUseCaseModel(event: any) {
        setTimeout(() => {
            this.MiguseCaseValue = [];
            this.scriptValueMig =[];
            
            for (let i of this.selectedMigItems) {
                let useCaseData = {
                    "useCaseName": i["item_text"],
                    "useCaseId": i["item_id"].useCaseId,
                    "actualExecution": i["item_id"].executionSequence,
                    "updatedExecution": i["item_id"].executionSequence,
                    "ucSleepInterval":i["item_id"].ucSleepInterval,
                    "oldValue": "",
                    "intervalOldValue":"",
                    "inEditMode": false,
                    "rowUpdate": false
                }
                this.MiguseCaseValue.push(useCaseData);
            }
            // Changing hardcoded for VZN-5G-DSS program on request of Anupama Dated 22-OCT-2020
            for (let scriptList of this.selectedMigItems) {
                for(let script of scriptList["item_id"].scripts){
                    let scriptData = {
                        "useCaseName": scriptList["item_text"],
                        "scriptName":script.scriptName,
                        "scriptId":script.scriptId,
                        "actualExecution":script.scriptExeSequence,
                        "updatedExecution":this.programName == "VZN-5G-DSS" && (script.scriptName.startsWith("57") || script.scriptName.startsWith("7")) ? 0 : script.scriptExeSequence,
                        "scriptSleepInterval":script.scriptSleepInterval,
                        "useGeneratedScript":script.useGeneratedScript,
                        "oldValue": "",
                        "intervalOldValue":"",
                        "useGeneratedScript_old":false,
                        "inEditMode": false,
                        "rowUpdate": false
                    }
                    this.scriptValueMig.push(scriptData);
                }              
                
            }
            if(this.programName=='VZN-5G-DSS' || this.programName =="VZN-5G-CBAND") {
                
                this.isPRECHECKShow = this.checkPreCheckUseCaseAvl();
            } 
        }, 0);

    }

    viewUseCase(content, useCaseValue, scriptValue) { 
        if (useCaseValue) {
            for (var i = 0; i < useCaseValue.length; i++) {
                useCaseValue[i].oldValue = useCaseValue[i].updatedExecution;
                useCaseValue[i].intervalOldValue = useCaseValue[i].ucSleepInterval;
                useCaseValue[i].rowUpdate = false;
            }
        }       

        if (scriptValue){
            for (var i = 0; i < scriptValue.length; i++) {
                scriptValue[i].oldValue = scriptValue[i].updatedExecution; 
                scriptValue[i].intervalOldValue = scriptValue[i].scriptSleepInterval;
                scriptValue[i].useGeneratedScript_old = scriptValue[i].useGeneratedScript;
                scriptValue[i].rowUpdate = false;
            }
        }
        this.useCaseModelData = {"useCases": useCaseValue, "scripts" : scriptValue}
        this.checkIfAllScriptSelected();
        this.updateNumberOfSelectedScript();
        this.viewModalBlock = this.modalService.open(content, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView' });
        /*  setTimeout(() => {
            let tableWidth = document.getElementById('useCaseScrollHead').scrollWidth;
            $(".useCaseWrapper .scrollBody table#useCaseTable").css("min-width", (tableWidth) + "px");
            $(".useCaseWrapper .scrollHead table#useCaseScrollHead").css("width", tableWidth + "px");

             $(".useCaseWrapper .scrollBody").on("scroll", function (event) {
              
              $(".useCaseRow .form-control-fixed").css("right", (event.target.scrollLeft * -1) + "px");
                $(".useCaseWrapper .scrollHead table#useCaseScrollHead").css("margin-left", (event.target.scrollLeft * -1) + "px");
            });           
            $(".useCaseWrapper .scrollBody").css("max-height", (this.tableDataHeight - $("#tabContentWrapper").height()) + "px");                      
                   

        }, 100);  */
    }

    editRow(event, index,selTab) {
        if(selTab =='usecaseTab'){
            this.editMode = index;
        }else if(selTab =='scriptTab'){
            this.editScriptMode = index;
        }   
     
    }
    saveRow(event, index,key) {
        let validations = { "rules": {}, "messages": {} };
        validations.rules["updatedExeSeq_" + [index]] = { "required": true ,"customfunction":false};
        validations.rules["time_" + [index]] = { "required": true };
        validations.messages["updatedExeSeq_" + [index]] = { "required": "Value is required" ,"customfunction":"Value should be unique"};
        validations.messages["time_" + [index]] = { "required": "Value is required" };
        
        let rows = $(event.target).parents("#useCaseTable").find("tr"), exeVal = false;
       for (var i = 0; i < rows.length; i++) {
            if ($(rows[i]).find("td:eq(2)").text() == $("#updatedExeSeq_" + index).val()) {
                //$("#updatedExeSeq_" + index).next(".error-message-block").html("Value should be unique");
                validations.rules["updatedExeSeq_" + [index]].customfunction = true;
                exeVal = true;
            }
        }
        validator.performValidation(event, validations, "save_update");
        setTimeout(()=>{
            if (this.isValidForm(event) && !exeVal) {
                this.editMode = -1;            
                this.useCaseModelData["useCases"][index].oldValue = this.useCaseModelData["useCases"][index].updatedExecution;
                this.useCaseModelData["useCases"][index].intervalOldValue = this.useCaseModelData["useCases"][index].ucSleepInterval;
                this.useCaseModelData["useCases"][index].updatedExecution = Number($("#updatedExeSeq_" + index).val());
                this.useCaseModelData["useCases"][index].ucSleepInterval = $("#time_" + index).val();
                this.useCaseModelData["useCases"][index].rowUpdate = true;
            }
        },0)
     


    }
    saveScriptRow(event, index,key) {
        let validations = { "rules": {}, "messages": {} };
        validations.rules["updatedExeSeq_" + [index]] = { "required": true ,"customfunction":false};
        validations.rules["time_" + [index]] = { "required": true };
        validations.messages["updatedExeSeq_" + [index]] = { "required": "Value is required" ,"customfunction":"Value should be unique"};
        validations.messages["time_" + [index]] = { "required": "Value is required" };

        let rows = $(event.target).parents("#scriptTable").find("tr"), exeVal = false;
        if($("#updatedExeSeq_" + index).val() !=0){
            for (var i = 0; i < rows.length; i++) {
                if ($(rows[i]).find("td:eq(3)").text() == $("#updatedExeSeq_" + index).val()) {             
                   validations.rules["updatedExeSeq_" + [index]].customfunction = true;
                    exeVal = true;
                }
            }  
        }
       
        validator.performValidation(event, validations, "save_update");  
        setTimeout(()=>{
            let scriptValue = this.useCaseModelData["scripts"];
            if (this.isValidForm(event) && !exeVal) {        
                this.editScriptMode =-1;
                scriptValue[index].oldValue = scriptValue[index].updatedExecution;
                scriptValue[index].intervalOldValue = scriptValue[index].scriptSleepInterval;
                scriptValue[index].useGeneratedScript_old = scriptValue[index].useGeneratedScript
                scriptValue[index].updatedExecution = $("#updatedExeSeq_" + index).val();
                scriptValue[index].scriptSleepInterval = $("#time_" + index).val();
                scriptValue[index].useGeneratedScript = $("#useGenScript_" + index).prop('checked') ? 'YES':'NO';
                scriptValue[index].rowUpdate = true;
            }
         },0)
    }
    toggleUpExecSeq(event, index) {
        let scriptValue = this.useCaseModelData["scripts"];
        // console.log("Updated Exec " + index + " - " + this.scriptValue[index].updatedExecution);
        if(scriptValue[index].updatedExecution == 0) {
            scriptValue[index].updatedExecution = scriptValue[index].actualExecution;
            this.checkIfAllScriptSelected();
        }
        else {
            scriptValue[index].updatedExecution = 0;
            this.allScriptSelect = false;
        }
        this.updateNumberOfSelectedScript();
        scriptValue[index].rowUpdate = true;
    }
    toggleAllScriptExecSeq() {
        // this.allScriptSelect = !this.allScriptSelect;
        /* for(let i = 0; i < this.scriptValue.length; i++) {
            this.toggleUpExecSeq(null, i);
        } */
        let scriptValue = this.useCaseModelData["scripts"];
        if (this.allScriptSelect) {
            // SELECT-ALL
            scriptValue.forEach((curScriptObj) => curScriptObj.updatedExecution = curScriptObj.actualExecution);
        }
        else {
            // UNSELECT-ALL
            scriptValue.forEach((curScriptObj) => curScriptObj.updatedExecution = 0);
        }
        this.updateNumberOfSelectedScript();
    }
    toggleSitesTable() {
        if (!this.searchBlock) {
            // Remove all interval calls for table refresh
            clearInterval(this.interval);
            this.interval = null;
            // Called to reload the table data
            setTimeout(() => this.updateRunTestTable(true), 100);
        }
    }

    checkIfAllScriptSelected() {
        this.allScriptSelect = this.useCaseModelData["scripts"].every((curScriptObj) => curScriptObj.updatedExecution > 0);
    }
    updateNumberOfSelectedScript() {
        let totalScriptSelected = 0;
        if(this.allScriptSelect) {
            totalScriptSelected = this.useCaseModelData["scripts"].length;
        }
        else {
            let execScript = this.useCaseModelData["scripts"].filter(element => element.updatedExecution > 0);
            totalScriptSelected = execScript.length;
        }
        this.totalScriptSelected = totalScriptSelected;
    }
    
    cancelRow(selTab) {
        if(selTab =='usecaseTab'){
            this.editMode = -1;
        }else if(selTab =='scriptTab'){
            this.editScriptMode = -1;
        }
    }
    saveScript(c) {
        this.message = "Sequence Changed Successfully!";
        this.displayModel( this.message, "successIcon");
        this.allScriptSelect = true;
        this.viewModalBlock.close();
    }
    cancelScript() {   
        let useCaseValue = this.useCaseModelData["useCases"];
        let scriptValue = this.useCaseModelData["scripts"];
        for (var i = 0; i < this.useCaseModelData["useCases"].length; i++) {
            if (useCaseValue[i].rowUpdate == true) {
                useCaseValue[i].updatedExecution = useCaseValue[i].oldValue;
                useCaseValue[i].ucSleepInterval = useCaseValue[i].intervalOldValue;
                useCaseValue[i].rowUpdate = false;
            }
        }
        for (var i = 0; i < scriptValue.length; i++) {
            if (scriptValue[i].rowUpdate == true) {
                scriptValue[i].updatedExecution = scriptValue[i].oldValue;
                scriptValue[i].scriptSleepInterval = scriptValue[i].intervalOldValue;
                scriptValue[i].useGeneratedScript = scriptValue[i].useGeneratedScript_old;
                scriptValue[i].rowUpdate = false;
            }
        }
        this.viewModalBlock.close();
        this.editMode = -1;
        this.editScriptMode = -1;
        this.allScriptSelect = true;
        this.Scriptcount = null;
    }
    /*
    * On click delete button open a modal for confirmation for delete entire row
    * @param : 
    * @retun : null
    */
    deleteRow(event, deleteModal, rowId) {

        let deleteState = event.target;

        if (deleteState.className != "deleteRowDisabled") {

            this.modalService.open(deleteModal, {
                keyboard: false,
                backdrop: 'static',
                size: 'lg',
                windowClass: 'confirm-modal'
            }).result.then((result) => {
                this.closeResult = `Closed with: ${result}`;
            }, (reason) => {

                this.showLoader = true;

                this.runtestService.deleteRunTestData(rowId, this.sharedService.createServiceToken(), this.commissionType)
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
                                        this.message = "Data deleted successfully !";
                                        this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                                        this.resetValues(event);
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
                                    this.message = "Data deleted successfully !";
                                    this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                                    this.resetValues(event);
                                }
                            }, 1000); */


                            //Please Comment while checkIn
                        });
            });
        }
        $("#dataWrapper").find(".scrollBody").scrollLeft(0);

    }

    viewResult(content, key , wfmid = '', resColumn = '') {
        this.testName = key.testName;
        this.testId = key.id;
        this.wfmid = wfmid;
        this.resultColumn = resColumn;
        this.ciqName = key.ciqName;
        this.showLoader = true;
        this.selectedScriptName = "";
        this.runtestService.viewTestResult(this.sharedService.createServiceToken(), key.id,key.lsmName,key.neName)
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
                                this.showLoader = false;
                                this.pgmName = jsonStatue.programName;
                                this.smName = jsonStatue.SMName;
                                this.neName = jsonStatue.NEName;
                                this.isResultProcessCompleted = jsonStatue.isProcessCompleted
                                this.testResultsData = jsonStatue.useCaseResult;
                                this.tabularResultsData = JSON.parse(JSON.stringify(this.testResultsData))
                                this.successModalBlock1 = this.modalService.open(content, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView' });
                                this.failedScript = jsonStatue.failedScript;
                                if (jsonStatue.useCaseResult) {
                                    for (var i = 0; i < jsonStatue.useCaseResult.length; i++) {
                                        let scriptList = jsonStatue.useCaseResult[i].script_status;
                                        if (scriptList) {
                                            for (var j = 0; j < scriptList.length; j++) {
                                                this.scriptcompleted++;
                                            }
                                        }
                                    }
                                }
                                 this.Scriptcount = jsonStatue.Scriptcount;
                                if (!jsonStatue.isProcessCompleted) {
                                    if (!this.resultPopupInterval) {
                                        this.resultPopupInterval = setInterval(() => {
                                            this.updateViewTestResult(key);
                                        }, 10000);
                                    }
                                }
                                else {
                                    clearInterval( this.resultPopupInterval );
                                    this.resultPopupInterval=null;
                                }
                                setTimeout(() => {
                                    var objDiv = document.getElementById("reportBody");
                                    objDiv.scrollTop = objDiv.scrollHeight;
                                }, 100);
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
                    this.displayModel("No Data", "failureIcon");
                    //Please Comment while checkIn

                     /* setTimeout(() => {
     
                        this.showLoader = false;
     
                        //let jsonStatue = JSON.parse('{"programName":"VZN-4G-LEGACY","useCaseResult":[{"useCaseName":"test","useCaseId":1,"script_status":[{"scriptId":2,"File_Rules":[{"ruleDefinition":"FileRuleName : jhjhjk,FileName : hjghjg,SearchParam : jhghjghj,Status : Pass","ruleName":"jhjhjk","status":"FAIL"}],"Command_Rules":[{"ruleDefinition":"CommandRuleName : lsjCommandName : ls -lrtOperandColumnOne : OperandOne : rootOperandColumnTwo : OperandTwo : rootOperator : CONTAINSStatus : Pass","ruleName":"ls -lrt","status":"FAIL"},{}],"scriptName":"script1.sh"}]}],"sessionId":"3e6ff1c","serviceToken":"85575","status":"SUCCESS"}');
                        //let jsonStatue = JSON.parse('{"sessionId":"3e6ff1c","serviceToken":"85575","status":"SUCCESS","programName":"VZN-4G-LEGACY","useCaseResult":[{"useCaseName":"test","useCaseId":1,"script_status":[{"scriptId":2,"File_Rules":[{"ruleDefinition":"FileRuleName : jhjhjk,FileName : hjghjg,SearchParam : jhghjghj,Status : Pass","ruleName":"jhjhjk","status":"FAIL"}],"Command_Rules":[{"ruleDefinition":"CommandRuleName : lsjCommandName : ls -lrtOperandColumnOne : OperandOne : rootOperandColumnTwo : OperandTwo : rootOperator : CONTAINSStatus : Pass","ruleName":"ls -lrt","status":"FAIL"},{"ruleDefinition":"CommandRuleName : lsjCommandName : ls -lrtOperandColumnOne : OperandOne : rootOperandColumnTwo : OperandTwo : rootOperator : CONTAINSStatus : Pass","ruleName":"ls -lrt","status":"WARN"},{"ruleDefinition":"CommandRuleName : lsjCommandName : ls -lrtOperandColumnOne : OperandOne : rootOperandColumnTwo : OperandTwo : rootOperator : CONTAINSStatus : Pass","ruleName":"ls -lrt","status":"NO DATA"}],"scriptName":"script1.sh"}]}]}');
                        //command rules
                        //let jsonStatue = JSON.parse('{"SMName":"Chicago","programName":"SPT-4G-MIMO","NEName":"CHCKILZXBBULTE0516276","useCaseResult":[{"useCaseName":"test","useCaseId":72,"script_status":[{"scriptId":258,"Command_Rules":[{"ruleDefinition":"CommandRuleName : Eveining Rule1_5th June, CommandName : RTRV-GPS-INVT, OperandColumnOne : FW_ID,UNIT_ID,FAMILY_TYPE, OperandOne : 0,UCCM[0],UCCM, OperandColumnTwo : VERSION,SERIAL, OperandTwo : 1.0.0.1,RS525151735, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule1_5th June","status":"NO DATA"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"PASS"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"WARN"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"FAIL"}],"scriptName":"ls","scriptExeSeq":1}]}],"sessionId":"ecb5fcf3","serviceToken":"60021","status":"SUCCESS"}');

                        //shell rules
                        //let jsonStatue = JSON.parse('{"SMName":"Chicago","programName":"SPT-4G-MIMO","NEName":"CHCKILZXBBULTE0516276","useCaseResult":[{"useCaseName":"test","useCaseId":72,"script_status":[{"scriptId":258,"Shell_Rules":[{"ruleDefinition":"CommandRuleName : 17th June Shell Rule3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"FAIL"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rule3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"WARN"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rule3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"NO DATA"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rule3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"PASS"}],"scriptName":"ls","scriptExeSeq":1}]}],"sessionId":"ecb5fcf3","serviceToken":"60021","status":"SUCCESS"}');

                        //command and shell rules
                        // let jsonStatue = {"Scriptcount":10,"SMName":"4G-USM2","programName":"VZN-4G-USM-LIVE","failedScript":true,"NEName":"131605_MCKENNA_PARK","useCaseResult":[{"useCaseName":"CA_Usecase131605_02202022","useCaseId":25036,"script_status":[{"scriptId":77876,"scriptName":"BASH_1000_131605_eNB_0_disable-anr-20220128-1059_CA_Usecase_131605_02202022","scriptExeSeq":1,"XML_Rules":[{"ruleDefinition":"XmlRuleName : curl, RootName : rpc-reply, SubrootName : , ElementName : curl, ElementValue : error, Operator : CONTAINS, Status : Pass","ruleName":"curl","status":"NO DATA"}]},{"scriptId":77875,"scriptName":"BASH_1100_131605_eNB_0_lock-lte-cells-20220128-1059_CA_Usecase_131605_02202022","scriptExeSeq":2,"XML_Rules":[{"ruleDefinition":"XmlRuleName : curl, RootName : rpc-reply, SubrootName : , ElementName : curl, ElementValue : error, Operator : CONTAINS, Status : Pass","ruleName":"curl","status":"NO DATA"}]}]},{"useCaseName":"CA_Usecase131605_02202023","useCaseId":25037,"script_status":[{"scriptId":77876,"scriptName":"BASH_1000_131605_eNB_0_disable-anr-20220128-1059_CA_Usecase_131605_02202022","scriptExeSeq":1,"XML_Rules":[{"ruleDefinition":"XmlRuleName : curl, RootName : rpc-reply, SubrootName : , ElementName : curl, ElementValue : error, Operator : CONTAINS, Status : Pass","ruleName":"curl","status":"NO DATA"}]},{"scriptId":77875,"scriptName":"BASH_1100_131605_eNB_0_lock-lte-cells-20220128-1059_CA_Usecase_131605_02202022","scriptExeSeq":2,"XML_Rules":[{"ruleDefinition":"XmlRuleName : curl, RootName : rpc-reply, SubrootName : , ElementName : curl, ElementValue : error, Operator : CONTAINS, Status : Pass","ruleName":"curl","status":"NO DATA"}]}]}],"sessionId":"ddce69c","serviceToken":"70436","isProcessCompleted":true,"status":"SUCCESS","CurrentSeq":2,"reason":""};
                        let jsonStatue = {"Scriptcount":10,"SMName":"4G-USM2","programName":"VZN-4G-USM-LIVE","failedScript":true,"NEName":"131605_MCKENNA_PARK","useCaseResult":[{"useCaseName":"CA_Usecase131605_02202022","useCaseId":25036,"script_status":[{"scriptId":77876,"scriptName":"BASH_1000_131605_eNB_0_disable-anr-20220128-1059_CA_Usecase_131605_02202022","scriptExeSeq":1,"XML_Rules":[{"previousOutput":"\nExecuting 52320_56900100_ACPF_0_56273_index4-6-for-a2-20220316-1742_Pre-Check_RFUsecase_7891002267_10302022.sh\ncurl -X POST -k -g -6 -u 'ossuser:osspasswd' -H 'Content-Type \r: application/xml' -d '<nc:rpc xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0 \r\"><nc:edit-config><nc:target><nc:running/></nc:target><nc:default-operation>none \r</nc:default-operation><nc:config><gnbducnf:managed-element xmlns:gnbducnf=\"http \r://www.samsung.com/global/business/5GvRAN/ns/gnbducnf\"><gnbducnf:gnb-du-function \r><gnbducnf:gutran-du-cell><gnbducnf:gutran-du-cell-entries><gnbducnf:cell-identi \rty>3559</gnbducnf:cell-identity><gnbducnf:dss-interface-conf-idle nc:operation=\" \rmerge\"><gnbducnf:target-enb-cell-identity>14405905</gnbducnf:target-enb-cell-ide \rntity></gnbducnf:dss-interface-conf-idle></gnbducnf:gutran-du-cell-entries><gnbd \rucnf:gutran-du-cell-entries><gnbducnf:cell-identity>3575</gnbducnf:cell-identity \r><gnbducnf:dss-interface-conf-idle nc:operation=\"merge\"><gnbducnf:target-enb-cel \rl-identity>14405925</gnbducnf:target-enb-cell-identity></gnbducnf:dss-interface- \rconf-idle></gnbducnf:gutran-du-cell-entries></gnbducnf:gutran-du-cell></gnbducnf \r:gnb-du-function></gnbducnf:managed-element></nc:config></nc:edit-config></nc:rp \rc>' https://10.20.120.82:7443/oss/netconf/ADPF_5690022273\r\ncurl: (7) Failed to connect to 10.20.120.82 port 7443: Connection refused\r\nuser@BLTSP01687:~$","previousResult":"No Data","ruleDefinition":"XmlRuleName : curl, RootName : rpc-reply, SubrootName : , ElementName : curl, ElementValue : error, Operator : CONTAINS, Status : Pass","ruleName":"curl","status":"NO DATA"}]},{"scriptId":77875,"scriptName":"BASH_1100_131605_eNB_0_lock-lte-cells-20220128-1059_CA_Usecase_131605_02202022","scriptExeSeq":2,"XML_Rules":[{"ruleDefinition":"XmlRuleName : curl, RootName : rpc-reply, SubrootName : , ElementName : curl, ElementValue : error, Operator : CONTAINS, Status : Pass","ruleName":"curl","status":"NO DATA"}]}]},{"useCaseName":"CA_Usecase131605_02202023","useCaseId":25037,"script_status":[{"scriptId":77876,"scriptName":"BASH_1000_131605_eNB_0_disable-anr-20220128-1059_CA_Usecase_131605_02202022","scriptExeSeq":1,"XML_Rules":[{"ruleDefinition":"XmlRuleName : curl, RootName : rpc-reply, SubrootName : , ElementName : curl, ElementValue : error, Operator : CONTAINS, Status : Pass","ruleName":"curl","status":"NO DATA"}]},{"scriptId":77875,"scriptName":"BASH_1100_131605_eNB_0_lock-lte-cells-20220128-1059_CA_Usecase_131605_02202022","scriptExeSeq":2,"XML_Rules":[{"ruleDefinition":"XmlRuleName : curl, RootName : rpc-reply, SubrootName : , ElementName : curl, ElementValue : error, Operator : CONTAINS, Status : Pass","ruleName":"curl","status":"NO DATA"}]}]}],"sessionId":"ddce69c","serviceToken":"70436","isProcessCompleted":true,"status":"SUCCESS","CurrentSeq":2,"reason":""};
     
                        if (jsonStatue.status == "SUCCESS") {
                             this.showLoader = false;
                             this.pgmName = jsonStatue.programName;
                             this.smName = jsonStatue.SMName;
                             this.neName = jsonStatue.NEName;
                             this.isResultProcessCompleted = jsonStatue.isProcessCompleted
                             this.testResultsData = jsonStatue.useCaseResult;   
                             this.tabularResultsData = JSON.parse(JSON.stringify(this.testResultsData))
                             this.successModalBlock1 = this.modalService.open(content, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView' });                              
                             this.failedScript = jsonStatue.failedScript;
                            if (jsonStatue.useCaseResult) {
                                for (var i = 0; i < jsonStatue.useCaseResult.length; i++) {
                                    let scriptList = jsonStatue.useCaseResult[i].script_status;
                                    if (scriptList) {
                                        for (var j = 0; j < scriptList.length; j++) {
                                            this.scriptcompleted++;
                                        }
                                    }
                                }
                            }
                             this.Scriptcount = jsonStatue.Scriptcount;
                             if (!jsonStatue.isProcessCompleted) {
                                if (!this.resultPopupInterval) {
                                    this.resultPopupInterval = setInterval(() => {
                                        this.updateViewTestResult(key);
                                    }, 10000);
                                }
                            }
                            else {
                                clearInterval( this.resultPopupInterval );
                                this.resultPopupInterval=null;
                            }
                            setTimeout(() => {
                                var objDiv = document.getElementById("reportBody");
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

    updateViewTestResult(key) {
        this.testName = key.testName;
        this.testId = key.id;
        
        this.runtestService.viewTestResult(this.sharedService.createServiceToken(), key.id,key.lsmName,key.neName)
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
                                this.pgmName = jsonStatue.programName;
                                this.smName = jsonStatue.SMName;
                                this.neName = jsonStatue.NEName;
                                this.isResultProcessCompleted = jsonStatue.isProcessCompleted;
                                this.testResultsData = jsonStatue.useCaseResult;
                                this.tabularResultsData = JSON.parse(JSON.stringify(this.testResultsData));
                                this.failedScript = jsonStatue.failedScript;
                                if (jsonStatue.useCaseResult) {
                                    for (var i = 0; i < jsonStatue.useCaseResult.length; i++) {
                                        let scriptList = jsonStatue.useCaseResult[i].script_status;
                                        if (scriptList) {
                                            for (var j = 0; j < scriptList.length; j++) {
                                                this.scriptcompleted++;
                                            }
                                        }
                                    }
                                }
                                 this.Scriptcount = jsonStatue.Scriptcount;
                                if (jsonStatue.isProcessCompleted) {
                                    clearInterval(this.resultPopupInterval);
                                    this.resultPopupInterval = null;
                                }

                                setTimeout(() => {
                                    var objDiv = document.getElementById("reportBody");
                                    objDiv.scrollTop = objDiv.scrollHeight;
                                }, 100);
                            }
                        }
                    }
                },
                error => {
                    //Please Comment while checkIn

                     /* setTimeout(() => {
                        //command and shell rules
                        let jsonStatue = JSON.parse('{"sessionId":"312b576d","serviceToken":"51960","status":"SUCCESS","SMName":"Westboro_Tiny","programName":"VZN-4G-USM-LIVE","failedScript":"true","NEName":"070282_MTSO","useCaseResult":[{"useCaseName":"CommissionScriptUsecase_70282_04012020","useCaseId":670,"script_status":[{"scriptId":3020,"scriptName":"BASH_COMM_NB-IoTAdd_COMM_070282_MTSO_04012020","scriptExeSeq":29,"XML_Rules":[{"ruleDefinition":"XmlRuleName : curl, RootName : rpc-reply, SubrootName : , ElementName : curl, ElementValue : ok, Operator : CONTAINS, Status : Pass","ruleName":"curl","status":"NO DATA"}]}]},{"useCaseName":"CommissionScriptUsecase_70282_06092020","useCaseId":1638,"script_status":[{"scriptId":9791,"scriptName":"BASH_COMM_NB-IoTAdd_COMM_070282_MTSO_06092020","scriptExeSeq":117,"XML_Rules":[{"ruleDefinition":"XmlRuleName : curl, RootName : rpc-reply, SubrootName : , ElementName : curl, ElementValue : ok, Operator : CONTAINS, Status : Pass","ruleName":"curl","status":"NO DATA"}]}]},{"useCaseName":"CommissionScriptUsecase_70282_08092020","useCaseId":1632,"script_status":[{"scriptId":9791,"scriptName":"BASH_COil_MTSO_09092020","scriptExeSeq":117,"XML_Rules":[{"ruleDefinition":"XmlRuleName : curl, RootName : rpc-reply, SubrootName : , ElementName : curl, ElementValue : ok, Operator : CONTAINS, Status : Pass XmlRuleName : curl, RootName : rpc-reply, SubrootName : , ElementName :curl, ElementValue : ok, Operator : CONTAINS, Status : Pass","ruleName":"curl1","status":"NO DATA"}]}]}],"isProcessCompleted":false}');
                        // let jsonStatue = JSON.parse('{"sessionId":"ecb5fcf3","serviceToken":"60021","status":"SUCCESS","SMName":"Chicago","programName":"SPT-4G-MIMO","NEName":"CHCKILZXBBULTE0516276","useCaseResult":[],"isProcessCompleted":true}');
    
                        if (jsonStatue.status == "SUCCESS") {
                            this.pgmName = jsonStatue.programName;
                            this.smName = jsonStatue.SMName;
                            this.neName = jsonStatue.NEName;
                            this.isResultProcessCompleted = jsonStatue.isProcessCompleted;
                            this.testResultsData = jsonStatue.useCaseResult;   
                            this.tabularResultsData = JSON.parse(JSON.stringify(this.testResultsData));
                            this.failedScript = jsonStatue.failedScript;
                            if (jsonStatue.useCaseResult) {
                                for (var i = 0; i < jsonStatue.useCaseResult.length; i++) {
                                    let scriptList = jsonStatue.useCaseResult[i].script_status;
                                    if (scriptList) {
                                        for (var j = 0; j < scriptList.length; j++) {
                                            this.scriptcompleted++;
                                        }
                                    }
                                }
                            }
                             this.Scriptcount = jsonStatue.Scriptcount;
                            if (jsonStatue.isProcessCompleted) {
                                clearInterval(this.resultPopupInterval);
                                this.resultPopupInterval = null;
                            }

                            setTimeout(() => {
                                var objDiv = document.getElementById("reportBody");
                                if(objDiv) {
                                    objDiv.scrollTop = objDiv.scrollHeight;
                                }
                            }, 100);
                        }
     
                     }, 100); */

                    //Please Comment while checkIn   
                });
    }

    reRunContinueTest(type, runTestId, id, resultview = '') {
        let reRunScriptID = null;
        this.showInnerLoader = true;
        // this.testId
        //"runTestId":746,  "runType":"CONTINUE",  "reRunScriptID":null,  "skipScriptIds":"670_3020,730_9910"
        let skipScriptIds = null;
        /* this.testResultsData.forEach((useCase) => {
            useCase.script_status.forEach((script) => {
                let scriptInfo = useCase.useCaseId + "_" + script.scriptId;
                skipScriptIds.push(scriptInfo);
            });
        });
        if(type == 'RERUNSCRIPT') {
            //Prepare reRunScriptID
            reRunScriptID = skipScriptIds[skipScriptIds.length - 1];
            //Remove last element from skipScriptIds as it is included in reRunScriptID
            skipScriptIds.pop();
        } */
        this.runtestService.reRunContinueTest(this.sharedService.createServiceToken(), type, runTestId, skipScriptIds, reRunScriptID, this.commissionType, id, this.resultColumn)
            .subscribe(
                data => {
                    this.showInnerLoader = false;
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
                                this.selectedScriptName = "";
                                this.failedScript = false;
                                if(resultview == 'RESULT')
                                {
                                    this.successModalBlock1.close();
                                }
                                if(type == "RERUNSCRIPT") {
                                    this.message = "Retrying the failed script!";
                                }
                                else {
                                    this.message = "Continuing with the commissioning!";
                                }

                                this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                                // this.updateRunTestTable();
                                // this.closeModelViewResult();
                            }
                            else {
                               
                                if(resultview == 'RESULT')
                                {
                                    this.successModalBlock1.close();
                                }
                                this.displayModel(jsonStatue.reason, "failureIcon");
                            }
                        }
                    }
                },
                error => {
                    //Please Comment while checkIn

                    /* setTimeout(() => {
                        this.showInnerLoader = false;
                        let jsonStatue = JSON.parse('{"sessionId":"ecb5fcf3","serviceToken":"60021","status":"SUCCESS", "reason":""}');
    
                        if (jsonStatue.status == "SUCCESS") {
                            this.selectedScriptName = "";
                            this.failedScript = false;
                            if(type == "RERUNSCRIPT") {
                                this.message = "Retrying the failed script!";
                            }
                            else {
                                this.message = "Continuing with the failed script!";
                            }
                            this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                            // this.updateRunTestTable();
                            // this.closeModelViewResult();
                        }
                        else {
                            this.displayModel(jsonStatue.reason, "failureIcon");
                        }
     
                     }, 100); */

                    //Please Comment while checkIn   
                });
    }
    reRun(key) {
        this.testId = key.id;
        this.showLoader = true;

        this.runtestService.reRunTest(this.sharedService.createServiceToken(), key.id, key.useCaseDetails)
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
                                this.showLoader = false;
                                this.message = "Run test started successfully!";
                                this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                                this.resetValues(event);
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
                        let jsonStatue = JSON.parse('{ "reason": null, "sessionId": "5f3732a4", "serviceToken": "80356", "status": "SUCCESS" }');

                        if (jsonStatue.status == "SUCCESS") {
                            this.showLoader = false;
                            this.message = "Run test started successfully!";
                            this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                            this.resetValues(event);
                        } else {
                            this.showLoader = false;
                            this.displayModel(jsonStatue.reason, "failureIcon");
                        }
                    }, 100); */

                    //Please Comment while checkIn   
                });
    }

    showCheckList(content, key) {
        this.rowData = {
            "id": key.id,
            "runTestName": key.testName,
            "checklistFileName": key.checklistFileName,
            "ciqName": key.ciqName,
            "enodeName": key.neName,
            "sheetName" : ""
        }
        this.showLoader = true;
        this.runtestService.getChecklistSheetDetails(this.sharedService.createServiceToken(), this.rowData)
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
                      this.checklistSheetList = jsonStatue.SheetDetails;
                      if (this.checklistSheetList.length == 0) {
                        this.displayModel("No Data Found", "failureIcon");
                      } else {
                        let index = 0;
                        this.getAllChecklistDetails(this.checklistSheetList[0], index, content);
                        // this.checkListModalBlock = this.modalService.open(content, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView' });
                      }
                    } else {
                      this.showLoader = false;
                      this.displayModel(jsonStatue.reason, "failureIcon");
                    }
    
                  }
                }
    
              }, 1000);
            },
            error => {
              //Please Comment while checkIn
                /* setTimeout(() => {
                    this.showLoader = false;

                    let jsonStatue = { "SheetDetails": ["Migration_tab"], "sessionId": "19088022", "serviceToken": "66957", "status": "SUCCESS", "reason": "" };
                    if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {

                        this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });

                    } else {
                        if (jsonStatue.status == "SUCCESS") {
                            this.checklistSheetList = jsonStatue.SheetDetails;
                            if (this.checklistSheetList.length == 0) {
                                this.displayModel("No Data Found", "failureIcon");
                            } else {
                                let index = 0;
                                this.getAllChecklistDetails(this.checklistSheetList[0], index, content);
                                // this.checkListModalBlock = this.modalService.open(content, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView' });
                            }
                        }
                        else {
                            this.showLoader = false;
                            this.displayModel(jsonStatue.reason, "failureIcon");
                        }
                    }
                }, 1000); */
              //Please Comment while checkIn
            });
      }
      
      
       /*
       * Used to get the all EnodeB details as per checklist name selection
       * @param : sheetname, index, key
       * @retun : null
       */
      
        getAllChecklistDetails(sheetName, index, content){
            this.sheetHighlight = index;
            this.rowData.sheetName = sheetName;
            this.showLoader = true;
            this.runtestService.getDeatilsByChecklist(this.sharedService.createServiceToken(), this.paginationDetails, this.rowData)
                .subscribe(
                  data => {
                      setTimeout(() => {
                        let jsonStatue = data.json();
    
                        // this.tableData = data.json();
                        this.showLoader = false;
                            if(jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User"){
                           
                             this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                           
                            } else {
    
                              if( this.sharedService.isvalidServiceToken( parseInt(data.json().serviceToken,10) ) || data.json().sessionId == "408"){
                                if(jsonStatue.status == "SUCCESS"){
                                //   this.tableData = jsonStatue;
                                this.checkListModalBlock = this.modalService.open(content, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView' });
                                  this.totalPages = jsonStatue.pageCount;
                                  let pageCount = [];
                                  for (var i = 1; i <= jsonStatue.pageCount; i++) {
                                      pageCount.push(i);
                                  }
                                  this.pageRenge = pageCount;
                                  this.pager = this.getPager(this.totalPages*this.pageSize, this.currentPage, this.pageRenge);
    
                                  if(jsonStatue.SheetDisplayDetails.list.length == 0){
                                    this.noCheckListDataVisibility = true;
                                  }else{
                                     this.checklistTableData = jsonStatue.SheetDisplayDetails.list;
                                     this.noCheckListDataVisibility = false;
                                     setTimeout(() => {
                                        let tableWidth = document.getElementById('uploadDetails').scrollWidth;
                              
                                        $(".checkListWrapper .scrollBody table#checkListTable").css("min-width",(tableWidth) + "px");
                                        $(".checkListWrapper .scrollHead table#uploadDetails").css("width", tableWidth + "px");
              
                                    
                                        $(".checkListWrapper .scrollBody").on("scroll", function (event) {
                                           // $(".formEditRow #formWrapper").css("margin-left", (event.target.scrollLeft - 5) + "px");
                                          $(".checkListRow .form-control-fixed").css("right",(event.target.scrollLeft * -1) + "px");
                                            $(".checkListWrapper .scrollHead table#uploadDetails").css("margin-left",(event.target.scrollLeft * -1) + "px");
                                        });
    
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
                    /* setTimeout(() => {
                      this.showLoader = false;
    
                    //   let jsonStatue = {"pageCount":4,"SheetDisplayDetails":{"count":4,"list":[]},"sessionId":"19088022","serviceToken":"66957","status":"SUCCESS"};
                    //   let jsonStatue = {"pageCount":1,"SheetDisplayDetails":{"count":1,"list":[{"id":13172,"fileName":"CommCDU30_mMIMO_8.5.3_Checklist_V1.8.xlsx","sheetName":"D1 to D2 ClearWire","seqOrder":"4","ciq":null,"program":null,"enodeName":"CHCKILZXBBULTE0516276","runTestId":139,"stepIndex":139,"checkListMap":{"check":"","Pre_Checks":"Grab Master CIQ and Scripts from SNFTP Server","Remarks":""}},{"id":13173,"fileName":"CommCDU30_mMIMO_8.5.3_Checklist_V1.8.xlsx","sheetName":"D1 to D2 ClearWire","seqOrder":"4","ciq":null,"program":null,"enodeName":"CHCKILZXBBULTE0516276","runTestId":139,"stepIndex":140,"checkListMap":{"check":"","Pre_Checks":"Pre Audit of Legacy CSR","Remarks":""}},{"id":13174,"fileName":"CommCDU30_mMIMO_8.5.3_Checklist_V1.8.xlsx","sheetName":"D1 to D2 ClearWire","seqOrder":"4","ciq":null,"program":null,"enodeName":"CHCKILZXBBULTE0516276","runTestId":139,"stepIndex":141,"checkListMap":{"check":"","Pre_Checks":"Pre Audit of Legacy eNB","Remarks":""}},{"id":13175,"fileName":"CommCDU30_mMIMO_8.5.3_Checklist_V1.8.xlsx","sheetName":"D1 to D2 ClearWire","seqOrder":"4","ciq":null,"program":null,"enodeName":"CHCKILZXBBULTE0516276","runTestId":139,"stepIndex":142,"checkListMap":{"check":"","Pre_Checks":"Pre Audit of CDMA if exist","Remarks":""}}]},"sessionId":"59f0198b","serviceToken":"83392","status":"SUCCESS"};
                        let jsonStatue = {"pageCount":6,"SheetDisplayDetails":{"count":6,"list":[{"id":28469,"fileName":"Verizon_Migration Checklist 100418_version4.8 (2).xlsx","sheetName":"Migration","seqOrder":"1","ciq":null,"program":null,"enodeName":"056008_CAMBSIDE_GALL_MA","runTestId":242,"stepIndex":1,"checkListMap":{"check":"","ITEM":"","STEPS":"PreMigration Activities:    gather Input files  SS/CA\n   get Site Specific and CA from RF,   WINSCP to VM   from Xshell New Jersey Sane\n   cd RF/v0chand1        sftp \"username\"@[2001:4888:a03:2100:c0:fef:0:78]\n             mget <CA & Site Specific>\n             exit\n      cp /home/lsm/aceman/web/etc/batch/RF/v0chand1","NOTES":"","COMMENTS":"","Remarks":""}},{"id":28470,"fileName":"Verizon_Migration Checklist 100418_version4.8 (2).xlsx","sheetName":"Migration","seqOrder":"1","ciq":null,"program":null,"enodeName":"056008_CAMBSIDE_GALL_MA","runTestId":242,"stepIndex":2,"checkListMap":{"check":"","ITEM":"1","STEPS":"RECEIVE CIQ AND GROW eNB                      ","NOTES":"check latest ciq from local winscp 13.59.191.103","COMMENTS":"","Remarks":""}},{"id":28471,"fileName":"Verizon_Migration Checklist 100418_version4.8 (2).xlsx","sheetName":"Migration","seqOrder":"1","ciq":null,"program":null,"enodeName":"056008_CAMBSIDE_GALL_MA","runTestId":242,"stepIndex":3,"checkListMap":{"check":"","ITEM":"2","STEPS":"MRO/DAS-RFM SITE ONLY(applied immediately after eNB loading is completed)","NOTES":"D:\\Verizon CDU 30\\MOP\\MRO_DAS_Settings_for_ODAS_MOP\nYou only need to execute “1) DL_MAX_TX_PWR Change : “ section to update the internal PLD parameter. Whether or It is connetced to a DAS vendor unit present or not.\n\n1. When there is no active DAS Vendor unit on site connected to MRO/DAS RRH, then the input DL_Max_Tx_Pwr for the script will be same as the value used in Grow(CIQ:Col P)\n\n2. When there is a DAS Vendor Unit\nIn CIQ, it will indicate whether this procedure needs to be executed. Last column in CIQ has “DAS” to indicate if external DAS is present,\nand change needs to be made for those cells. (CIQ: Col AU)\n\nThe other 2 procedures in the MOP are now included in SSS. You can use the RTRV commands for troubleshooting\n\nwhere to find output power:.\nCIQ :Col P: Output Power (dBm)- To be used for grow.\nCIQ: Col AU: DAS Output Power- To be used for the MOP.","COMMENTS":"","Remarks":""}},{"id":28472,"fileName":"Verizon_Migration Checklist 100418_version4.8 (2).xlsx","sheetName":"Migration","seqOrder":"1","ciq":null,"program":null,"enodeName":"056008_CAMBSIDE_GALL_MA","runTestId":242,"stepIndex":4,"checkListMap":{"check":"","ITEM":"3","STEPS":"NEIGHBORS List Scripts\n1.  NbrList-NBR-ENB","NOTES":"Execute  script as soon as site is ENABLED","COMMENTS":"","Remarks":""}},{"id":28473,"fileName":"Verizon_Migration Checklist 100418_version4.8 (2).xlsx","sheetName":"Migration","seqOrder":"1","ciq":null,"program":null,"enodeName":"056008_CAMBSIDE_GALL_MA","runTestId":242,"stepIndex":5,"checkListMap":{"check":"","ITEM":"4","STEPS":"VERIFY AND UPGRADE THE FIRMWARE AND THE SOFTWARE  ","NOTES":"7.0.2.1","COMMENTS":"","Remarks":""}},{"id":28474,"fileName":"Verizon_Migration Checklist 100418_version4.8 (2).xlsx","sheetName":"Migration","seqOrder":"1","ciq":null,"program":null,"enodeName":"056008_CAMBSIDE_GALL_MA","runTestId":242,"stepIndex":6,"checkListMap":{"check":"","ITEM":"5","STEPS":"VERIFY no RRH Alarms exist","NOTES":"check to see if there's any RRH HW Mismatch","COMMENTS":"","Remarks":""}},{"id":28475,"fileName":"Verizon_Migration Checklist 100418_version4.8 (2).xlsx","sheetName":"Migration","seqOrder":"1","ciq":null,"program":null,"enodeName":"056008_CAMBSIDE_GALL_MA","runTestId":242,"stepIndex":7,"checkListMap":{"check":"","ITEM":"6","STEPS":"SFP audit","NOTES":"cd /home/lsm/Scripts/SfpFinder\nvi Conf/eNB.conf\n            edit add your enodeB\ncd Logs\ngrep FAILED *","COMMENTS":"","Remarks":""}},{"id":28476,"fileName":"Verizon_Migration Checklist 100418_version4.8 (2).xlsx","sheetName":"Migration","seqOrder":"1","ciq":null,"program":null,"enodeName":"056008_CAMBSIDE_GALL_MA","runTestId":242,"stepIndex":8,"checkListMap":{"check":"","ITEM":"7","STEPS":"FW Upgrade of ALU RRH\n ‘RTRV-ALURRH-SW’ If FW egual to 2n Commerical RU FW [n] ==> Continue with C&I process\n            else \n FW Upgrade of ALU RRH MOP. See RRH FW Upgrade Tab for Detail Table; D:\\Verizon CDU 30\\MOP","NOTES":"Check RRH FW Upgrade Tab for Details","COMMENTS":"","Remarks":""}},{"id":28477,"fileName":"Verizon_Migration Checklist 100418_version4.8 (2).xlsx","sheetName":"Migration","seqOrder":"1","ciq":null,"program":null,"enodeName":"056008_CAMBSIDE_GALL_MA","runTestId":242,"stepIndex":9,"checkListMap":{"check":"","ITEM":"8","STEPS":"RUN COMMISSIONING SCRIPT\n                   Commission script is created base on the CIQ. (CSL IP, NTP Server,Alias_Name CRTE-VLAN, RRH Alarm threshold)  \nPlease Verify Audit REPORT with CIQ","NOTES":"run via rancom tool, \nuncheck vbs to run auto.\nVerify AUDIT REPORT with CIQ. Modify any discrepancy or De-grow/RE-Grow.","COMMENTS":"","Remarks":""}},{"id":28478,"fileName":"Verizon_Migration Checklist 100418_version4.8 (2).xlsx","sheetName":"Migration","seqOrder":"1","ciq":null,"program":null,"enodeName":"056008_CAMBSIDE_GALL_MA","runTestId":242,"stepIndex":10,"checkListMap":{"check":"","ITEM":"9","STEPS":"UDA Alarm Build ( Provide by RF or FOPS )","NOTES":"D:\\Verizon CDU 30\\MOP\\MRO_DAS_Settings_for_ODAS_MOP","COMMENTS":"","Remarks":""}}]},"sessionId":"2d8c37e7","serviceToken":"78773","status":"SUCCESS"};
                        // let jsonStatue = {"pageCount":1,"SheetDisplayDetails":{"count":1,"list":[{"id":13362,"fileName":"CommCDU30_mMIMO_8.5.3_Checklist_V1.8.xlsx","sheetName":"D1 to D2 ClearWire","seqOrder":"4","ciq":null,"program":null,"enodeName":"CHCKILZXBBULTE0516276","runTestId":140,"stepIndex":139,"checkListMap":{"check":"","Pre_Checks":"Grab Master CIQ and Scripts from SNFTP Server","Remarks":""}},{"id":13363,"fileName":"CommCDU30_mMIMO_8.5.3_Checklist_V1.8.xlsx","sheetName":"D1 to D2 ClearWire","seqOrder":"4","ciq":null,"program":null,"enodeName":"CHCKILZXBBULTE0516276","runTestId":140,"stepIndex":140,"checkListMap":{"check":"","Pre_Checks":"Pre Audit of Legacy CSR","Remarks":""}},{"id":13364,"fileName":"CommCDU30_mMIMO_8.5.3_Checklist_V1.8.xlsx","sheetName":"D1 to D2 ClearWire","seqOrder":"4","ciq":null,"program":null,"enodeName":"CHCKILZXBBULTE0516276","runTestId":140,"stepIndex":141,"checkListMap":{"check":"","Pre_Checks":"Pre Audit of Legacy eNB","Remarks":""}},{"id":13365,"fileName":"CommCDU30_mMIMO_8.5.3_Checklist_V1.8.xlsx","sheetName":"D1 to D2 ClearWire","seqOrder":"4","ciq":null,"program":null,"enodeName":"CHCKILZXBBULTE0516276","runTestId":140,"stepIndex":142,"checkListMap":{"check":"","Pre_Checks":"Pre Audit of CDMA if exist","Remarks":""}},{"id":13366,"fileName":"CommCDU30_mMIMO_8.5.3_Checklist_V1.8.xlsx","sheetName":"D1 to D2 ClearWire","seqOrder":"4","ciq":null,"program":null,"enodeName":"CHCKILZXBBULTE0516276","runTestId":140,"stepIndex":143,"checkListMap":{"check":"","Pre_Checks":"PLD Backup (TDD/FDD) on Legacy site - FTP on Laptop","Remarks":""}}]},"sessionId":"c3bc9108","serviceToken":"63858","status":"SUCCESS"};
                        
                      if(jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User"){
                             this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                            }
                          this.checkListModalBlock = this.modalService.open(content, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView' });
                          this.totalPages = jsonStatue.pageCount;
                          let pageCount = [];
                          for (var i = 1; i <= jsonStatue.pageCount; i++) {
                              pageCount.push(i);
                          }
                          this.pageRenge = pageCount;
                          this.pager = this.getPager(this.totalPages*this.pageSize, this.currentPage, this.pageRenge);
    
                          
                          if(jsonStatue.SheetDisplayDetails.list.length == 0){
                            // this.checkListTable = false;
                            this.noCheckListDataVisibility = true;
                          }else{
                            // this.checkListTable = true;
                            this.checklistTableData = jsonStatue.SheetDisplayDetails.list;
                            this.noCheckListDataVisibility = false;
                            setTimeout(() => {
                              let tableWidth = document.getElementById('uploadDetails').scrollWidth;
                              
                              $(".checkListWrapper .scrollBody table#checkListTable").css("min-width",(tableWidth) + "px");
                              $(".checkListWrapper .scrollHead table#uploadDetails").css("width", tableWidth + "px");
    
                          
                              $(".checkListWrapper .scrollBody").on("scroll", function (event) {
                                 // $(".formEditRow #formWrapper").css("margin-left", (event.target.scrollLeft - 5) + "px");
                                 $(".checkListRow .form-control-fixed").css("right",(event.target.scrollLeft * -1) + "px");
                                  $(".checkListWrapper .scrollHead table#uploadDetails").css("margin-left",(event.target.scrollLeft * -1) + "px");
                              });
                              //$(".scrollBody").css("max-height",(this.windowScreenHieght/2) + "px");
                              //$(".scrollBody").css("max-height",(this.tableDataHeight - 10) + "px");
                              
                              },0);   
                            
                          }
                      
                    }, 1000); */
                  
                    //Please Comment while checkIn
            });
       }

    saveCheckList(c) {
        this.runtestService.updateCheckList(this.sharedService.createServiceToken(), this.rowData, this.checklistTableData)
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
                                this.showLoader = false;
                                this.checkListModalBlock.close();
                                this.displayModel( "Check List Updated Successfully", "successIcon");
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
    
                        let jsonStatue = JSON.parse('{"sessionId":"7e088256","serviceToken":"81749","status":"SUCCESS","reason":"abc.."}');
    
                        if (jsonStatue.status == "SUCCESS") {
                            this.showLoader = false;
                            this.checkListModalBlock.close();
                            this.displayModel("Check List Updated Successfully", "successIcon");
                        } else {
                            this.showLoader = false;
                            this.displayModel(jsonStatue.reason, "failureIcon");
                        }
    
                    }, 100); */

                    //Please Comment while checkIn   
                });
    }

    checkValue(index, enodeIndex, value) {
        this.checklistTableData[enodeIndex].checkListMap.check = value ? "true" : "";
    }

    getScriptList(usecase) {
        this.showOutput = false;
        this.scriptList = [];
        for (let i of this.testResultsData) {
            if (usecase == i.useCaseName) {
                this.scriptList = i.script_status;
            }
        }

    }

    getScriptOutput(event) {
        this.showOutput = false;
        let currentForm = $(event.target).parents("form"),
            useCaseName = currentForm.find('#usecaseSO :selected').val(),
            useCaseId = currentForm.find('#usecaseSO :selected').attr('id'),
            scriptName = currentForm.find('#script :selected').val(),
            scriptId = currentForm.find('#script :selected').attr('id');

        this.runtestService.scriptOutput(this.sharedService.createServiceToken(), this.testId, useCaseName, useCaseId, scriptName, scriptId)
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
                                this.showLoader = false;
                                this.showOutput = true;
                                this.scriptOutput = jsonStatue.output;

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
    
                        let jsonStatue = JSON.parse('{"sessionId":"7e088256","serviceToken":"81749","status":"SUCCESS","output":"abc.."}');
    
                       if (jsonStatue.status == "SUCCESS") {
                            this.showLoader = false;
                            this.showOutput = true;
                            this.scriptOutput= jsonStatue.output;
                        } else {
                            this.showLoader = false;
                            this.displayModel(jsonStatue.reason, "failureIcon");
    
                        }
    
                    }, 100); */

                    //Please Comment while checkIn   
                });

    }

    showRunningLog(content, key) {
        this.showWideRunningLog = false;
        this.rowData = {
            "id": key.id,
            "runTestName": key.testName,
            "checklistFileName": key.checklistFileName,
            "ciqName": key.ciqName,
            "sheetName": "",
            "neName":key.neName
        }
        this.showLoader = true;
        this.runtestService.getRunningLogs(this.sharedService.createServiceToken(), key.id)
            .subscribe(
                data => {
                    setTimeout(() => {
                        let jsonStatue = data.json();
                        this.showLoader = false;

                        if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
                            if(!this.sessionExpiredModalBlock){
                            this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
                            }
                        } else {

                            if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {
                                if (jsonStatue.status == "SUCCESS") {
                                    this.noCheckListDataVisibility = false;
                                    
                                    this.isProcessCompleted=jsonStatue.isProcessCompleted;                               
                                if(!this.isProcessCompleted)
                                {
                                    if(!this.runningLogInterval) {
                                        this.runningLogInterval = setInterval( () => {
                                            this.updateRunningLog();
                                        }, 1000 );
                                    }
                                }
                                else
                                {
                                    clearInterval( this.runningLogInterval );
                                    this.runningLogInterval=null;

                                    //console.log("there");
                
                                }

                                    let index = 0;
                                    this.runningLogs = jsonStatue.runningLog;
                                    // this.runningLogModalBlock = this.modalService.open(content, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView' });
                                    this.showRunningLogLauncher = false;
                                    this.showRunningLogContent = true;
                                    // Call the service to refresh on time interval of 10 sec.
                                    if(!this.runningLogInterval) {
                                        this.runningLogInterval = setInterval( () => {
                                            this.updateRunningLog();
                                        }, 1000 );
                                    }
                                    setTimeout(() => {
                                        var objDiv = document.getElementById("runningLogsPre");
                                        objDiv.scrollTop = objDiv.scrollHeight;
                                    }, 100);
                                } else {
                                    this.showLoader = false;
                                    this.displayModel(jsonStatue.reason, "failureIcon");
                                }

                            }
                        }

                    }, 1000);
                },
                error => {
                    //Please Comment while checkIn
                     /* setTimeout(() => {
                        this.showLoader = false;

                        //let jsonStatue = {"runningLog":"asklj sa kljsalk\n asjgas  hs \n ajsghas \n asjighas ","sessionId":"19088022","serviceToken":"66957","status":"SUCCESS","reason":""};
                        // let jsonStatue = {"runningLog":"spawn ssh -o StrictHostKeyChecking=no -oCheckHostIP=no user@127.0.0.1\nuser@127.0.0.1's password: \nWelcome to Ubuntu 16.04.3 LTS (GNU/Linux 4.10.0-28-generic x86_64)\n\n * Documentation:  https://help.ubuntu.com\n * Management:     https://landscape.canonical.com\n * Support:        https://ubuntu.com/advantage\n\n277 packages can be updated.\n9 updates are security updates.\n\nLast login: Mon Jun 24 17:33:29 2019 from 127.0.0.1\n\nssh -o StrictHostKeyChecking=no -oCheckHostIP=no user@127.0.0.1\nuser@user-OptiPlex-7010:~$ ssh -o StrictHostKeyChecking=no -oCheckHostIP=no u\n<ssh -o StrictHostKeyChecking=no -oCheckHostIP=no us                         \b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\ber@127.0.0.1\nuser@127.0.0.1's password: \nWelcome to Ubuntu 16.04.3 LTS (GNU/Linux 4.10.0-28-generic x86_64)\n\n * Documentation:  https://help.ubuntu.com\n * Management:     https://landscape.canonical.com\n * Support:        https://ubuntu.com/advantage\n\n277 packages can be updated.\n9 updates are security updates.\n\nLast login: Mon Jun 24 17:33:57 2019 from 127.0.0.1\n\nuser@user-OptiPlex-7010:~$ sudo su \n[sudo] password for user: \nroot@user-OptiPlex-7010:/home/user# \nspawn ssh -o StrictHostKeyChecking=no -oCheckHostIP=no user@127.0.0.1\nuser@127.0.0.1's password: \nWelcome to Ubuntu 16.04.3 LTS (GNU/Linux 4.10.0-28-generic x86_64)\n\n * Documentation:  https://help.ubuntu.com\n * Management:     https://landscape.canonical.com\n * Support:        https://ubuntu.com/advantage\n\n277 packages can be updated.\n9 updates are security updates.\n\nLast login: Mon Jun 24 17:34:07 2019 from 127.0.0.1\n\nssh -o StrictHostKeyChecking=no -oCheckHostIP=no user@127.0.0.1\nuser@user-OptiPlex-7010:~$ ssh -o StrictHostKeyChecking=no -oCheckHostIP=no u\n<ssh -o StrictHostKeyChecking=no -oCheckHostIP=no us                         \b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\ber@127.0.0.1\nuser@127.0.0.1's password: \nWelcome to Ubuntu 16.04.3 LTS (GNU/Linux 4.10.0-28-generic x86_64)\n\n * Documentation:  https://help.ubuntu.com\n * Management:     https://landscape.canonical.com\n * Support:        https://ubuntu.com/advantage\n\n277 packages can be updated.\n9 updates are security updates.\n\nLast login: Mon Jun 24 17:36:21 2019 from 127.0.0.1\n\nuser@user-OptiPlex-7010:~$ sudo su \n[sudo] password for user: \nroot@user-OptiPlex-7010:/home/user# \nroot@user-OptiPlex-7010:/home/user# ls\nbhuvanasmartmom.zip\nbhuvana.tar.gz\nCPAN-2.16\nCPAN-2.16.tar.gz\nDaily Activity.ods\nDesktop\nDocuments\nDownloads\nDrive\neclipse-workspace\nexamples.desktop\nfinal_5c853c7a3fc6200014a7df80_885019.mp4\nftp\nIMG_20181207_153237.jpg\nIMG_20181207_153310.jpg\nIMG_20181207_153526.jpg\nIMG_20181207_153537.jpg\njdk\nJson Reqest Response for migration Page .odt\nMongo.json\nMusic\nmvn\nperl5\nPictures\nPublic\npycharm-community-2018.3\nratAtpintput.log\nRCT\nRCT_workspace\nruntestserviceimplfor manultesting.txt\nsnap\ntag\ntask sheet.ods\nTemplates\ntomcat\nVideos\nroot@user-OptiPlex-7010:/home/user# df\nFilesystem     1K-blocks     Used Available Use% Mounted on\nudev             4040124        0   4040124   0% /dev\ntmpfs             812716     9592    803124   2% /run\n/dev/sda7      255590924 57783408 184801132  24% /\ntmpfs            4063564    94996   3968568   3% /dev/shm\ntmpfs               5120        4      5116   1% /run/lock\ntmpfs            4063564        0   4063564   0% /sys/fs/cgroup\n/dev/loop0          4864     4864         0 100% /snap/notepad-plus-plus/191\n/dev/loop1        135424   135424         0 100% /snap/postman/81\n/dev/loop2         93184    93184         0 100% /snap/core/6405\n/dev/loop3         91392    91392         0 100% /snap/core/6673\n/dev/loop4        133760   133760         0 100% /snap/postman/80\n/dev/loop5        450688   450688         0 100% /snap/wine-platform/101\n/dev/loop6        450816   450816         0 100% /snap/wine-platform/103\n/dev/loop7          3840     3840         0 100% /snap/notepad-plus-plus/193\n/dev/loop8         55040    55040         0 100% /snap/core18/731\n/dev/loop9        450816   450816         0 100% /snap/wine-platform/105\n/dev/loop10        55040    55040         0 100% /snap/core18/782\n/dev/loop11        36224    36224         0 100% /snap/gtk-common-themes/1198\n/dev/loop12        93312    93312         0 100% /snap/core/6531\n/dev/loop13         3840     3840         0 100% /snap/notepad-plus-plus/195\n/dev/loop14        35712    35712         0 100% /snap/gtk-common-themes/1122\n/dev/loop15       132864   132864         0 100% /snap/postman/73\n/dev/sda5         369639    65188    280848  19% /boot\ntmpfs             812716       64    812652   1% /run/user/1000\nroot@user-OptiPlex-7010:/home/user# pwd\n/home/user\nroot@user-OptiPlex-7010:/home/user# ls\nbhuvanasmartmom.zip\nbhuvana.tar.gz\nCPAN-2.16\nCPAN-2.16.tar.gz\nDaily Activity.ods\nDesktop\nDocuments\nDownloads\nDrive\neclipse-workspace\nexamples.desktop\nfinal_5c853c7a3fc6200014a7df80_885019.mp4\nftp\nIMG_20181207_153237.jpg\nIMG_20181207_153310.jpg\nIMG_20181207_153526.jpg\nIMG_20181207_153537.jpg\njdk\nJson Reqest Response for migration Page .odt\nMongo.json\nMusic\nmvn\nperl5\nPictures\nPublic\npycharm-community-2018.3\nratAtpintput.log\nRCT\nRCT_workspace\nruntestserviceimplfor manultesting.txt\nsnap\ntag\ntask sheet.ods\nTemplates\ntomcat\nVideos\nroot@user-OptiPlex-7010:/home/user# \nroot@user-OptiPlex-7010:/home/user# \n","sessionId":"31e4c050","serviceToken":"63263","status":"SUCCESS","reason":""};
                        let jsonStatue ={"runningLog":"spawn ssh -o StrictHostKeyChecking=no -oCheckHostIP=no user@10.20.120.93\nuser@10.20.120.93's password: \nWelcome to Ubuntu 16.04.5 LTS (GNU/Linux 4.15.0-43-generic x86_64)\n\n * Documentation:  https://help.ubuntu.com\n * Management:     https://landscape.canonical.com\n * Support:        https://ubuntu.com/advantage\n\n103 packages can be updated.\n8 updates are security updates.\n\nLast login: Mon Jul 22 12:40:27 2019 from 10.20.120.25\n\n-bash: /home/user: Is a directory\n","sessionId":"fa7e0c46","serviceToken":"60051","isProcessCompleted":false,"status":"SUCCESS","reason":""};
                        if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {

                            this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });

                        } else {
                            if (jsonStatue.status == "SUCCESS") {                              
                                this.noCheckListDataVisibility = false;
                            
                                let index = 0;
                                this.runningLogs = jsonStatue.runningLog;
                                // this.runningLogModalBlock = this.modalService.open(content, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView' });
                                this.showRunningLogLauncher = false;
                                this.showRunningLogContent = true;
                                // Call the service to refresh on time interval of 10 sec.
                                
                                this.isProcessCompleted=jsonStatue.isProcessCompleted;                               
                                if(!this.isProcessCompleted)
                                {
                                    if(!this.runningLogInterval) {
                                        this.runningLogInterval = setInterval( () => {
                                            this.updateRunningLog();
                                        }, 1000 );
                                    }
                                }
                                else
                                {
                                    clearInterval( this.runningLogInterval );
                                    this.runningLogInterval=null;

                                    //console.log("there");
                
                                }
                                
                                setTimeout(() => {
                                    var objDiv = document.getElementById("runningLogsPre");
                                    objDiv.scrollTop = objDiv.scrollHeight;
                                }, 100);
                            }
                            else {
                                this.showLoader = false;
                                this.displayModel(jsonStatue.reason, "failureIcon");
                            }
                        }
                    }, 1000);  */
                    //Please Comment while checkIn
                });
    }

    showResultLog(content, key) {
        this.showWideRunningLog = false;
        this.rowData = {
            "id": key.id,
            "runTestName": key.testName,
            "checklistFileName": key.checklistFileName,
            "ciqName": key.ciqName,
            "sheetName": "",
            "neName":key.neName
        }
        this.showLoader = true;
        this.runtestService.getResultLogs(this.sharedService.createServiceToken(), key.id)
            .subscribe(
                data => {
                    setTimeout(() => {
                        let jsonStatue = data.json();
                        this.showLoader = false;

                        if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
                            if(!this.sessionExpiredModalBlock){
                            this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
                            }
                        } else {

                            if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {
                                if (jsonStatue.status == "SUCCESS") {
                                    this.noCheckListDataVisibility = false;
                                    
                                    this.isProcessCompleted=jsonStatue.isProcessCompleted;                               
                                // if(!this.isProcessCompleted)
                                // {
                                //     if(!this.runningLogInterval) {
                                //         this.runningLogInterval = setInterval( () => {
                                //             this.updateRunningLog();
                                //         }, 1000 );
                                //     }
                                // }
                                // else
                                // {
                                //     clearInterval( this.runningLogInterval );
                                //     this.runningLogInterval=null;

                                //     //console.log("there");
                
                                // }

                                    let index = 0;
                                    this.runningLogs = jsonStatue.neOutputLog;
                                    // this.runningLogModalBlock = this.modalService.open(content, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView' });
                                    this.showRunningLogLauncher = false;
                                    this.showRunningLogContent = true;
                                    // Call the service to refresh on time interval of 10 sec.
                                    // if(!this.runningLogInterval) {
                                    //     this.runningLogInterval = setInterval( () => {
                                    //         this.updateRunningLog();
                                    //     }, 1000 );
                                    // }
                                    setTimeout(() => {
                                        var objDiv = document.getElementById("runningLogsPre");
                                        objDiv.scrollTop = objDiv.scrollHeight;
                                    }, 100);
                                } else {
                                    this.showLoader = false;
                                    this.displayModel(jsonStatue.reason, "failureIcon");
                                }

                            }
                        }

                    }, 1000);
                },
                error => {
                    //Please Comment while checkIn
                     /* setTimeout(() => {
                        this.showLoader = false;

                        //let jsonStatue = {"runningLog":"asklj sa kljsalk\n asjgas  hs \n ajsghas \n asjighas ","sessionId":"19088022","serviceToken":"66957","status":"SUCCESS","reason":""};
                        // let jsonStatue = {"runningLog":"spawn ssh -o StrictHostKeyChecking=no -oCheckHostIP=no user@127.0.0.1\nuser@127.0.0.1's password: \nWelcome to Ubuntu 16.04.3 LTS (GNU/Linux 4.10.0-28-generic x86_64)\n\n * Documentation:  https://help.ubuntu.com\n * Management:     https://landscape.canonical.com\n * Support:        https://ubuntu.com/advantage\n\n277 packages can be updated.\n9 updates are security updates.\n\nLast login: Mon Jun 24 17:33:29 2019 from 127.0.0.1\n\nssh -o StrictHostKeyChecking=no -oCheckHostIP=no user@127.0.0.1\nuser@user-OptiPlex-7010:~$ ssh -o StrictHostKeyChecking=no -oCheckHostIP=no u\n<ssh -o StrictHostKeyChecking=no -oCheckHostIP=no us                         \b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\ber@127.0.0.1\nuser@127.0.0.1's password: \nWelcome to Ubuntu 16.04.3 LTS (GNU/Linux 4.10.0-28-generic x86_64)\n\n * Documentation:  https://help.ubuntu.com\n * Management:     https://landscape.canonical.com\n * Support:        https://ubuntu.com/advantage\n\n277 packages can be updated.\n9 updates are security updates.\n\nLast login: Mon Jun 24 17:33:57 2019 from 127.0.0.1\n\nuser@user-OptiPlex-7010:~$ sudo su \n[sudo] password for user: \nroot@user-OptiPlex-7010:/home/user# \nspawn ssh -o StrictHostKeyChecking=no -oCheckHostIP=no user@127.0.0.1\nuser@127.0.0.1's password: \nWelcome to Ubuntu 16.04.3 LTS (GNU/Linux 4.10.0-28-generic x86_64)\n\n * Documentation:  https://help.ubuntu.com\n * Management:     https://landscape.canonical.com\n * Support:        https://ubuntu.com/advantage\n\n277 packages can be updated.\n9 updates are security updates.\n\nLast login: Mon Jun 24 17:34:07 2019 from 127.0.0.1\n\nssh -o StrictHostKeyChecking=no -oCheckHostIP=no user@127.0.0.1\nuser@user-OptiPlex-7010:~$ ssh -o StrictHostKeyChecking=no -oCheckHostIP=no u\n<ssh -o StrictHostKeyChecking=no -oCheckHostIP=no us                         \b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\ber@127.0.0.1\nuser@127.0.0.1's password: \nWelcome to Ubuntu 16.04.3 LTS (GNU/Linux 4.10.0-28-generic x86_64)\n\n * Documentation:  https://help.ubuntu.com\n * Management:     https://landscape.canonical.com\n * Support:        https://ubuntu.com/advantage\n\n277 packages can be updated.\n9 updates are security updates.\n\nLast login: Mon Jun 24 17:36:21 2019 from 127.0.0.1\n\nuser@user-OptiPlex-7010:~$ sudo su \n[sudo] password for user: \nroot@user-OptiPlex-7010:/home/user# \nroot@user-OptiPlex-7010:/home/user# ls\nbhuvanasmartmom.zip\nbhuvana.tar.gz\nCPAN-2.16\nCPAN-2.16.tar.gz\nDaily Activity.ods\nDesktop\nDocuments\nDownloads\nDrive\neclipse-workspace\nexamples.desktop\nfinal_5c853c7a3fc6200014a7df80_885019.mp4\nftp\nIMG_20181207_153237.jpg\nIMG_20181207_153310.jpg\nIMG_20181207_153526.jpg\nIMG_20181207_153537.jpg\njdk\nJson Reqest Response for migration Page .odt\nMongo.json\nMusic\nmvn\nperl5\nPictures\nPublic\npycharm-community-2018.3\nratAtpintput.log\nRCT\nRCT_workspace\nruntestserviceimplfor manultesting.txt\nsnap\ntag\ntask sheet.ods\nTemplates\ntomcat\nVideos\nroot@user-OptiPlex-7010:/home/user# df\nFilesystem     1K-blocks     Used Available Use% Mounted on\nudev             4040124        0   4040124   0% /dev\ntmpfs             812716     9592    803124   2% /run\n/dev/sda7      255590924 57783408 184801132  24% /\ntmpfs            4063564    94996   3968568   3% /dev/shm\ntmpfs               5120        4      5116   1% /run/lock\ntmpfs            4063564        0   4063564   0% /sys/fs/cgroup\n/dev/loop0          4864     4864         0 100% /snap/notepad-plus-plus/191\n/dev/loop1        135424   135424         0 100% /snap/postman/81\n/dev/loop2         93184    93184         0 100% /snap/core/6405\n/dev/loop3         91392    91392         0 100% /snap/core/6673\n/dev/loop4        133760   133760         0 100% /snap/postman/80\n/dev/loop5        450688   450688         0 100% /snap/wine-platform/101\n/dev/loop6        450816   450816         0 100% /snap/wine-platform/103\n/dev/loop7          3840     3840         0 100% /snap/notepad-plus-plus/193\n/dev/loop8         55040    55040         0 100% /snap/core18/731\n/dev/loop9        450816   450816         0 100% /snap/wine-platform/105\n/dev/loop10        55040    55040         0 100% /snap/core18/782\n/dev/loop11        36224    36224         0 100% /snap/gtk-common-themes/1198\n/dev/loop12        93312    93312         0 100% /snap/core/6531\n/dev/loop13         3840     3840         0 100% /snap/notepad-plus-plus/195\n/dev/loop14        35712    35712         0 100% /snap/gtk-common-themes/1122\n/dev/loop15       132864   132864         0 100% /snap/postman/73\n/dev/sda5         369639    65188    280848  19% /boot\ntmpfs             812716       64    812652   1% /run/user/1000\nroot@user-OptiPlex-7010:/home/user# pwd\n/home/user\nroot@user-OptiPlex-7010:/home/user# ls\nbhuvanasmartmom.zip\nbhuvana.tar.gz\nCPAN-2.16\nCPAN-2.16.tar.gz\nDaily Activity.ods\nDesktop\nDocuments\nDownloads\nDrive\neclipse-workspace\nexamples.desktop\nfinal_5c853c7a3fc6200014a7df80_885019.mp4\nftp\nIMG_20181207_153237.jpg\nIMG_20181207_153310.jpg\nIMG_20181207_153526.jpg\nIMG_20181207_153537.jpg\njdk\nJson Reqest Response for migration Page .odt\nMongo.json\nMusic\nmvn\nperl5\nPictures\nPublic\npycharm-community-2018.3\nratAtpintput.log\nRCT\nRCT_workspace\nruntestserviceimplfor manultesting.txt\nsnap\ntag\ntask sheet.ods\nTemplates\ntomcat\nVideos\nroot@user-OptiPlex-7010:/home/user# \nroot@user-OptiPlex-7010:/home/user# \n","sessionId":"31e4c050","serviceToken":"63263","status":"SUCCESS","reason":""};
                        let jsonStatue ={"neOutputLog":"spawn ssh -o StrictHostKeyChecking=no -oCheckHostIP=no user@10.20.120.93\nuser@10.20.120.93's password: \nWelcome to Ubuntu 16.04.5 LTS (GNU/Linux 4.15.0-43-generic x86_64)\n\n * Documentation:  https://help.ubuntu.com\n * Management:     https://landscape.canonical.com\n * Support:        https://ubuntu.com/advantage\n\n103 packages can be updated.\n8 updates are security updates.\n\nLast login: Mon Jul 22 12:40:27 2019 from 10.20.120.25\n\n-bash: /home/user: Is a directory\n","sessionId":"fa7e0c46","serviceToken":"60051","isProcessCompleted":false,"status":"SUCCESS","reason":""};
                        if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {

                            this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });

                        } else {
                            if (jsonStatue.status == "SUCCESS") {                              
                                this.noCheckListDataVisibility = false;
                            
                                let index = 0;
                                this.runningLogs = jsonStatue.neOutputLog;
                                // this.runningLogModalBlock = this.modalService.open(content, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView' });
                                this.showRunningLogLauncher = false;
                                this.showRunningLogContent = true;
                                // Call the service to refresh on time interval of 10 sec.
                                
                                this.isProcessCompleted=jsonStatue.isProcessCompleted;                               
                                // if(!this.isProcessCompleted)
                                // {
                                //     if(!this.runningLogInterval) {
                                //         this.runningLogInterval = setInterval( () => {
                                //             this.updateRunningLog();
                                //         }, 1000 );
                                //     }
                                // }
                                // else
                                // {
                                //     clearInterval( this.runningLogInterval );
                                //     this.runningLogInterval=null;

                                //     //console.log("there");
                
                                // }
                                
                                setTimeout(() => {
                                    var objDiv = document.getElementById("runningLogsPre");
                                    objDiv.scrollTop = objDiv.scrollHeight;
                                }, 100);
                            }
                            else {
                                this.showLoader = false;
                                this.displayModel(jsonStatue.reason, "failureIcon");
                            }
                        }
                    }, 1000);  */
                    //Please Comment while checkIn
                });
    }
    updateRunningLog() {
        // this.showInnerLoader = true;
        this.runtestService.getRunningLogs(this.sharedService.createServiceToken(), this.rowData.id)
            .subscribe(
                data => {
                    setTimeout(() => {
                        let jsonStatue = data.json();
                        // this.showInnerLoader = false;

                        if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
                            if(!this.sessionExpiredModalBlock){
                            this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
                            }
                        } else {

                            if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {
                                if (jsonStatue.status == "SUCCESS") {
                                    this.runningLogs = jsonStatue.runningLog;
                                    this.isProcessCompleted=jsonStatue.isProcessCompleted;                               
                                    if(this.isProcessCompleted)
                                    {
                                        clearInterval( this.runningLogInterval );
                                        this.runningLogInterval=null;
                                    }
                                    
                                    setTimeout(() => {
                                        var objDiv = document.getElementById("runningLogsPre");
                                        objDiv.scrollTop = objDiv.scrollHeight;
                                    }, 100);
                                } else {
                                    // this.showInnerLoader = false;
                                    this.displayModel(jsonStatue.reason, "failureIcon");
                                }

                            }
                        }

                    }, 1000);
                },
                error => {
                    //Please Comment while checkIn
                    /* setTimeout(() => {
                        // this.showInnerLoader = false;

                        //let jsonStatue = {"runningLog":"spawn ssh -o StrictHostKeyChecking=no -oCheckHostIP=no user@127.0.0.1\nuser@127.0.0.1's password: \nWelcome to Ubuntu 16.04.3 LTS (GNU/Linux 4.10.0-28-generic x86_64)\n\n * Documentation:  https://help.ubuntu.com\n * Management:     https://landscape.canonical.com\n * Support:        https://ubuntu.com/advantage\n\n277 packages can be updated.\n9 updates are security updates.\n\nLast login: Mon Jun 24 17:33:29 2019 from 127.0.0.1\n\nssh -o StrictHostKeyChecking=no -oCheckHostIP=no user@127.0.0.1\nuser@user-OptiPlex-7010:~$ ssh -o StrictHostKeyChecking=no -oCheckHostIP=no u\n<ssh -o StrictHostKeyChecking=no -oCheckHostIP=no us                         \b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\ber@127.0.0.1\nuser@127.0.0.1's password: \nWelcome to Ubuntu 16.04.3 LTS (GNU/Linux 4.10.0-28-generic x86_64)\n\n * Documentation:  https://help.ubuntu.com\n * Management:     https://landscape.canonical.com\n * Support:        https://ubuntu.com/advantage\n\n277 packages can be updated.\n9 updates are security updates.\n\nLast login: Mon Jun 24 17:33:57 2019 from 127.0.0.1\n\nuser@user-OptiPlex-7010:~$ sudo su \n[sudo] password for user: \nroot@user-OptiPlex-7010:/home/user# \nspawn ssh -o StrictHostKeyChecking=no -oCheckHostIP=no user@127.0.0.1\nuser@127.0.0.1's password: \nWelcome to Ubuntu 16.04.3 LTS (GNU/Linux 4.10.0-28-generic x86_64)\n\n * Documentation:  https://help.ubuntu.com\n * Management:     https://landscape.canonical.com\n * Support:        https://ubuntu.com/advantage\n\n277 packages can be updated.\n9 updates are security updates.\n\nLast login: Mon Jun 24 17:34:07 2019 from 127.0.0.1\n\nssh -o StrictHostKeyChecking=no -oCheckHostIP=no user@127.0.0.1\nuser@user-OptiPlex-7010:~$ ssh -o StrictHostKeyChecking=no -oCheckHostIP=no u\n<ssh -o StrictHostKeyChecking=no -oCheckHostIP=no us                         \b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\ber@127.0.0.1\nuser@127.0.0.1's password: \nWelcome to Ubuntu 16.04.3 LTS (GNU/Linux 4.10.0-28-generic x86_64)\n\n * Documentation:  https://help.ubuntu.com\n * Management:     https://landscape.canonical.com\n * Support:        https://ubuntu.com/advantage\n\n277 packages can be updated.\n9 updates are security updates.\n\nLast login: Mon Jun 24 17:36:21 2019 from 127.0.0.1\n\nuser@user-OptiPlex-7010:~$ sudo su \n[sudo] password for user: \nroot@user-OptiPlex-7010:/home/user# \nroot@user-OptiPlex-7010:/home/user# ls\nbhuvanasmartmom.zip\nbhuvana.tar.gz\nCPAN-2.16\nCPAN-2.16.tar.gz\nDaily Activity.ods\nDesktop\nDocuments\nDownloads\nDrive\neclipse-workspace\nexamples.desktop\nfinal_5c853c7a3fc6200014a7df80_885019.mp4\nftp\nIMG_20181207_153237.jpg\nIMG_20181207_153310.jpg\nIMG_20181207_153526.jpg\nIMG_20181207_153537.jpg\njdk\nJson Reqest Response for migration Page .odt\nMongo.json\nMusic\nmvn\nperl5\nPictures\nPublic\npycharm-community-2018.3\nratAtpintput.log\nRCT\nRCT_workspace\nruntestserviceimplfor manultesting.txt\nsnap\ntag\ntask sheet.ods\nTemplates\ntomcat\nVideos\nroot@user-OptiPlex-7010:/home/user# df\nFilesystem     1K-blocks     Used Available Use% Mounted on\nudev             4040124        0   4040124   0% /dev\ntmpfs             812716     9592    803124   2% /run\n/dev/sda7      255590924 57783408 184801132  24% /\ntmpfs            4063564    94996   3968568   3% /dev/shm\ntmpfs               5120        4      5116   1% /run/lock\ntmpfs            4063564        0   4063564   0% /sys/fs/cgroup\n/dev/loop0          4864     4864         0 100% /snap/notepad-plus-plus/191\n/dev/loop1        135424   135424         0 100% /snap/postman/81\n/dev/loop2         93184    93184         0 100% /snap/core/6405\n/dev/loop3         91392    91392         0 100% /snap/core/6673\n/dev/loop4        133760   133760         0 100% /snap/postman/80\n/dev/loop5        450688   450688         0 100% /snap/wine-platform/101\n/dev/loop6        450816   450816         0 100% /snap/wine-platform/103\n/dev/loop7          3840     3840         0 100% /snap/notepad-plus-plus/193\n/dev/loop8         55040    55040         0 100% /snap/core18/731\n/dev/loop9        450816   450816         0 100% /snap/wine-platform/105\n/dev/loop10        55040    55040         0 100% /snap/core18/782\n/dev/loop11        36224    36224         0 100% /snap/gtk-common-themes/1198\n/dev/loop12        93312    93312         0 100% /snap/core/6531\n/dev/loop13         3840     3840         0 100% /snap/notepad-plus-plus/195\n/dev/loop14        35712    35712         0 100% /snap/gtk-common-themes/1122\n/dev/loop15       132864   132864         0 100% /snap/postman/73\n/dev/sda5         369639    65188    280848  19% /boot\ntmpfs             812716       64    812652   1% /run/user/1000\nroot@user-OptiPlex-7010:/home/user# pwd\n/home/user\nroot@user-OptiPlex-7010:/home/user# ls\nbhuvanasmartmom.zip\nbhuvana.tar.gz\nCPAN-2.16\nCPAN-2.16.tar.gz\nDaily Activity.ods\nDesktop\nDocuments\nDownloads\nDrive\neclipse-workspace\nexamples.desktop\nfinal_5c853c7a3fc6200014a7df80_885019.mp4\nftp\nIMG_20181207_153237.jpg\nIMG_20181207_153310.jpg\nIMG_20181207_153526.jpg\nIMG_20181207_153537.jpg\njdk\nJson Reqest Response for migration Page .odt\nMongo.json\nMusic\nmvn\nperl5\nPictures\nPublic\npycharm-community-2018.3\nratAtpintput.log\nRCT\nRCT_workspace\nruntestserviceimplfor manultesting.txt\nsnap\ntag\ntask sheet.ods\nTemplates\ntomcat\nVideos\nroot@user-OptiPlex-7010:/home/user# \nroot@user-OptiPlex-7010:/home/user# \n","sessionId":"31e4c050","serviceToken":"63263","status":"SUCCESS","reason":""};
                        let jsonStatue ={"runningLog":"spawn ssh -o StrictHostKeyChecking=no -oCheckHostIP=no user@10.20.120.93\nuser@10.20.120.93's password: \nWelcome to Ubuntu 16.04.5 LTS (GNU/Linux 4.15.0-43-generic x86_64)\n\n * Documentation:  https://help.ubuntu.com\n * Management:     https://landscape.canonical.com\n * Support:        https://ubuntu.com/advantage\n\n103 packages can be updated.\n8 updates are security updates.\n\nLast login: Mon Jul 22 12:40:27 2019 from 10.20.120.25\n\n-bash: /home/user: Is a directory\n","sessionId":"fa7e0c46","serviceToken":"60051","isProcessCompleted":false,"status":"SUCCESS","reason":""};

                        if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {

                            this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });

                        } else {
                            if (jsonStatue.status == "SUCCESS") {                              
                                this.runningLogs = jsonStatue.runningLog;

                                this.isProcessCompleted=jsonStatue.isProcessCompleted;                               
                                if(this.isProcessCompleted)
                                {
                                    clearInterval( this.runningLogInterval );
                                    this.runningLogInterval=null;
                                }


                                setTimeout(() => {
                                    var objDiv = document.getElementById("runningLogsPre");
                                    objDiv.scrollTop = objDiv.scrollHeight;
                                }, 100);
                            }
                            else {
                                // this.showInnerLoader = false;
                                this.displayModel(jsonStatue.reason, "failureIcon");
                            }
                        }
                    }, 1000); */
                    //Please Comment while checkIn
                });
    }

    downloadLogs(key) {

        this.runtestService.downloadFile(this.sharedService.createServiceToken(), key)
            .subscribe(
                data => {
                    let fileName = key.neName + "_" + key.migrationSubType + "_" + key.testName + "_" + Math.round(new Date().getTime()/1000) + ".zip";

                    let blob = new Blob([data["_body"]], {
                        type: "application/octet-stream" //"text/plain;charset=utf-8"
                    });

                    FileSaver.saveAs(blob, fileName);
                },
                error => {
                    //Please Comment while checkIn
                    /* let jsonStatue: any = { "sessionId": "506db794", "reason": "Download Failed", "status": "SUCCESS", "serviceToken": "63524" };
                    
                    let fileName = key.neName + "_" + key.migrationSubType + "_" + key.testName + "_" + Math.round(new Date().getTime()/1000) + ".txt";

                    let blob = new Blob(["Hello, world!"], { 
                        type: "application/octet-stream" //"text/plain;charset=utf-8" 
                    });

                    FileSaver.saveAs(blob, fileName);

                    setTimeout(() => {
                        this.showLoader = false;
                        if (jsonStatue.status == "SUCCESS") {

                        } else {
                            this.displayModel(jsonStatue.reason, "failureIcon");
                        }

                    }, 1000); */
                    //Please Comment while checkIn
                });
    
      }


      downloadLogsRow(key) {

        this.runtestService.downloadFileRow(this.sharedService.createServiceToken(), key)
            .subscribe(
                data => {
                    let fileName = key.neName + "_" + key.migrationSubType + "_" + key.testName + "_" + Math.round(new Date().getTime()/1000) + ".zip";

                    let blob = new Blob([data["_body"]], {
                        type: "application/octet-stream" //"text/plain;charset=utf-8"
                    });

                    FileSaver.saveAs(blob, fileName);
                },
                error => {
                    //Please Comment while checkIn
                    /* let jsonStatue: any = { "sessionId": "506db794", "reason": "Download Failed", "status": "SUCCESS", "serviceToken": "63524" };
                    
                    let fileName = key.neName + "_" + key.migrationSubType + "_" + key.testName + "_" + Math.round(new Date().getTime()/1000) + ".txt";

                    let blob = new Blob(["Hello, world!"], { 
                        type: "application/octet-stream" //"text/plain;charset=utf-8" 
                    });

                    FileSaver.saveAs(blob, fileName);

                    setTimeout(() => {
                        this.showLoader = false;
                        if (jsonStatue.status == "SUCCESS") {

                        } else {
                            this.displayModel(jsonStatue.reason, "failureIcon");
                        }

                    }, 1000); */
                    //Please Comment while checkIn
                });
    
      }
      viewCheckConLog(content) {
        this.showLoader = true;
        this.runtestService.getConnectionLog(this.sharedService.createServiceToken(), this.commissionType)
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
                                    this.checkConnLogs = jsonStatue.connectionLog;
                                    this.checkConnLogModalBlock = this.modalService.open(content, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView' });
                                } else {
                                    this.showLoader = false;
                                    this.displayModel(jsonStatue.reason, "failureIcon");
                                }

                            }
                        }

                    }, 1000);
                },
                error => {
                    //Please Comment while checkIn
                    /* setTimeout(() => {
                        this.showLoader = false;

                        let jsonStatue = {"connectionLog":"asklj sa kljsalk\n asjgas  hs \n ajsghas \n asjighas ","sessionId":"19088022","serviceToken":"66957","status":"SUCCESS","reason":""};
                        if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {

                            this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });

                        } else {
                            if (jsonStatue.status == "SUCCESS") {                              
                                this.checkConnLogs = jsonStatue.connectionLog;
                                this.checkConnLogModalBlock = this.modalService.open(content, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView' });
                            }
                            else {
                                this.showLoader = false;
                                this.displayModel(jsonStatue.reason, "failureIcon");
                            }
                        }
                    }, 1000); */
                    //Please Comment while checkIn
                });

      }
      
      getNeList(content) {
        this.checkConnLogModalBlock = this.modalService.open(content, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView' });
      }


      getNetworkDetails(content) {
        this.checkConnLogModalBlock = this.modalService.open(content, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView' });
      }

    closeModelViewResult() {
        this.successModalBlock1.close();
        this.testResultsData = [];
        this.scriptOutput = "";
        this.useCaseSO = "";
        this.scriptList=[];
        this.showOutput=false;
        this.selectedScriptName = "";
        clearInterval( this.resultPopupInterval );
        this.resultPopupInterval=null;
        this.scriptcompleted = 0;
        this.Scriptcount = 0;
    }

    closeModelCheckList() {
        this.checkListModalBlock.close();
    }

    closeModelRunningLog() {
        this.runningLogModalBlock.close();
    }

    closeModelcheckConn() {
        this.checkConnLogModalBlock.close();
    }

    closeModelPassword() {
        this.pswdModalBlock.close();
    }

    closeModelUseCase() {
        //this.useCaseModelKey = null;
	    this.clearUseCaseModelData();
        this.selectedNGItems=[];
        this.selectedNGStatusItems=[];
        this.selectedMigItems=[];
        this.selectedPMigItems=[];
        this.selectedPAMigItems=[];
        this.useCaseModalBlock.close();
    }

    progressStatusCls(state) {
        let retClass = "";
        if(state == "InProgress") {
            retClass = "inProgress";
        }
        else if(state == "NotYetStarted") {
            retClass = "yetToStart";
        }
        else if(state == "Completed" || state == "Completion") {
            retClass = "finished";
        }
        else if(state == "Failure" || state == "Exception") {
            retClass = "failure";
        }
        else if(state == "InputsRequired") {
            retClass = "inputRequired";
        }
        else if(state == "StoppedByUser") {
            retClass = "stopped";
        }
        else if(state == "NotExecuted") {
            retClass = "notExecuted";
        }
        return retClass;
    }

    actionBtnDisabled(state) {
        let retClass = "";
        if(state == "InProgress" || state == "Completed") {
            retClass = "disabled";
        }
        return retClass;
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
            this.getRunTest();


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
            this.getRunTest();
        }, 0);

    }

    /*
    * On click sort header in table then sort the data ascending and decending
    * @param : columnName, event and current Index
    * @retun : null
    */

   changeSortingStatus(predicate, event, index) {
       //console.log(this.testResultsData[0].script_status[0].Shell_Rules.length);
       //console.log(this.testResultsData[0].script_status[0].XML_Rules? this.testResultsData[0].script_status[0].XML_Rules.lenth : "its undefined" );



        for(var i=0; i<this.tabularResultsData.length;i++)
        {
            for(var j=0;j<this.tabularResultsData[i].script_status.length;j++)
            {
                if(this.tabularResultsData[i].script_status[j].Command_Rules)
                {
                    this.sharedService.dynamicSort(predicate, event, index, this.tabularResultsData[i].script_status[j].Command_Rules);
                }

                if(this.tabularResultsData[i].script_status[j].File_Rules)
                {
                    this.sharedService.dynamicSort(predicate, event, index, this.tabularResultsData[i].script_status[j].File_Rules);
                }

                if(this.tabularResultsData[i].script_status[j].XML_Rules)
                {
                    this.sharedService.dynamicSort(predicate, event, index, this.tabularResultsData[i].script_status[j].XML_Rules);
                }

                if(this.tabularResultsData[i].script_status[j].Shell_Rules)
                {
                    this.sharedService.dynamicSort(predicate, event, index, this.tabularResultsData[i].script_status[j].Shell_Rules);
                }
         
            }

        }
    }


    changeSortingRules(predicate, event, index) {
        //console.log(this.testResultsData[0].script_status[0].Shell_Rules.length);
        //console.log(this.testResultsData[0].script_status[0].XML_Rules? this.testResultsData[0].script_status[0].XML_Rules.lenth : "its undefined" );
 
 
 
         for(var i=0; i<this.tabularResultsData.length;i++)
         {
             for(var j=0;j<this.tabularResultsData[i].script_status.length;j++)
             {
                 if(this.tabularResultsData[i].script_status[j].Command_Rules)
                 {
                     this.sharedService.dynamicSort(predicate, event, index, this.tabularResultsData[i].script_status[j].Command_Rules);
                 }
 
                 if(this.tabularResultsData[i].script_status[j].File_Rules)
                 {
                     this.sharedService.dynamicSort(predicate, event, index, this.tabularResultsData[i].script_status[j].File_Rules);
                 }
 
                 if(this.tabularResultsData[i].script_status[j].XML_Rules)
                 {
                     this.sharedService.dynamicSort(predicate, event, index, this.tabularResultsData[i].script_status[j].XML_Rules);
                 }
 
                 if(this.tabularResultsData[i].script_status[j].Shell_Rules)
                 {
                     this.sharedService.dynamicSort(predicate, event, index, this.tabularResultsData[i].script_status[j].Shell_Rules);
                 }
          
             }
 
         }
     }

    changeSortingUsecase(predicate, event, index) {
        this.sharedService.dynamicSort(predicate, event, index, this.tabularResultsData);
    }

    changeSortingScript(predicate, event, index) {
        for(var i=0; i<this.tabularResultsData.length;i++)
        {
            this.sharedService.dynamicSort(predicate, event, index, this.tabularResultsData[i].script_status);
        }
    }

    changeSortingExeSeq(predicate, event, index) {
        for(var i=0; i<this.tabularResultsData.length;i++)
        {
            this.sharedService.dynamicSort(predicate, event, index, this.tabularResultsData[i].script_status);
        }
    }

    changeSorting(predicate, event, index) {
        this.sharedService.dynamicSort(predicate, event, index, this.tableData.runTestTableDetails);
    }


    changeSortingScripts(predicate, event, index) {
        this.sharedService.dynamicSort(predicate, event, index, this.useCaseModelData["scripts"]);
    }


    changeSortingUsecases(predicate, event, index) {
        this.sharedService.dynamicSort(predicate, event, index, this.useCaseModelData["useCases"]);
    }


     /* Call onLoadService on change of Migration Type Radio Btn */
     onChangeLoad(value){
        // this.commissionType = value;      
       /*  if(this.createNewForm){
            this.bluePrintFormRef.resetForm();
            this.selectedNWType ="";
            this.selectedVersion ="";
            this.selectedLsmName ="";           
        }else if(this.searchBlock){
            //clear the search form
        } */
        if(this.generateAllSites==true)
            this.selectedNEIDs = [];
        this.versionDetails="";
        this.ciqDetails="";
        this.nameDetails="";
        this.lsmNameDetails=[];
        this.selectedNEItems=null;
        this.dropdownNEList=[];
        setTimeout(() => {
            this.getRunTest();
        }, 100);
    }
    /* On click of search highlight open record types
     * @param : current Tab Item (open/close)
     * @retun : null
     */

    setMenuHighlight(selectedElement) {
        this.searchTabRef.nativeElement.id = (selectedElement == "search") ? "activeTab" : "inactiveTab";
        this.createNewTabRef.nativeElement.id = (selectedElement == "createNew") ? "activeTab" : "inactiveTab";
    }
    connect(event) {
        this.validationData.rules["password"] = { "required": true };
        validator.performValidation(event, this.validationData, "save_update");
        let currentForm = event.target.parentNode.parentNode;
        let pswd = currentForm.querySelector("#password").value;
        this.runTestDetails.password = pswd;
        this.runTestDetailsPost.password =pswd;
        this.runTestDetailsNEGrow.password =pswd;

        setTimeout(() => {
            if (this.isValidForm(event)) {
                this.showLoader = true;
                this.pswdModalBlock.close();
                const formdata = new FormData();
                if (this.isRETUseCaseSelected) {
                    let files: FileList = this.filePostRef.nativeElement.files,
                        filenames = [];
                    for (var i = 0; i < files.length; i++) {
                        formdata.append("UPLOAD", files[i]);
                        //formdata.append(ciqFiles[i].name, ciqFiles[i]);
                        filenames.push(files[i].name);
                    }
                }            
                let migrationStrategy = this.programName == 'VZN-4G-USM-LIVE' ? this.migrationStrategy : null;  
                this.runtestService.uploadRunTestDetails(this.sharedService.createServiceToken(),this.premigData,this.ranAtpFlag, this.runTestDetails,this.commissionType,this.requestType,this.runTestDetailsPost,this.runTestDetailsNEGrow,this.generateAllSites,this.selectedNEIDs, this.fsuType,this.paginationDetails, migrationStrategy)
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
                                        this.showLoader = false;
                                       if(jsonStatue.requestType == 'CHECK_CONNECTION'){
                                            this.message = "Connection Check Successful";
                                        }else{
                                            this.message = "Run test started successfully!";
                                        }   
                                        this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                                        this.resetValues(event);
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
                                let jsonStatue = { "reason": "test", "requestType": "RUN_TEST", "sessionId": "9a1afbd4", "serviceToken": "67492", "status": "SUCCESS" };
                                if (jsonStatue.status == "SUCCESS") {
                                    this.showLoader = false;
                                    this.message = "Run test started successfully!";
                                    console.log(this.selectedItems)
                                    this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                                    this.resetValues(event);
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

    columnRun(event, key, type) {
        console.log(event,key,type)
        if(type != 'PreMigration') {
            this.validationData.rules["location"] = { "required": false };
            validator.performValidation(event, this.validationData, "save_update");
        }
        setTimeout(() => {
                this.showInnerLoader = true;
                /* if(type != 'PreMigration') {
                    this.closeModelUseCase();
                } */
                let state, PMiguseCaseDetails = [], PAMiguseCaseDetails = [], MiguseCaseDetails = [], NGuseCaseDetails = [], NEMiguseCaseDetails = [], neDetails = [], 
                scriptDetailsNG = [], scriptDetailsMig = [], scriptDetailsPMig = [], scriptDetailsPAMig = [], scriptDetailsNEMig = [];
                
                for (let i of this.NGuseCaseValue) {
                    let selUseCase = {
                        "useCaseName": i.useCaseName,
                        "useCaseId": i.useCaseId,
                        "executionSequence": i.updatedExecution,
                        "ucSleepInterval": i.ucSleepInterval ? i.ucSleepInterval : ""
                    }
                    NGuseCaseDetails.push(selUseCase);
                }

                for (let i of this.NSuseCaseValue) {
                    let selUseCase = {
                        "useCaseName": i.useCaseName,
                        "useCaseId": i.useCaseId,
                        "executionSequence": i.updatedExecution,
                        "ucSleepInterval": i.ucSleepInterval ? i.ucSleepInterval : ""
                    }
                    NEMiguseCaseDetails.push(selUseCase);
                }
                
                for (let i of this.PMiguseCaseValue) {
                    let selUseCase = {
                        "useCaseName": i.useCaseName,
                        "useCaseId": i.useCaseId,
                        "executionSequence": i.updatedExecution,
                        "ucSleepInterval": i.ucSleepInterval ? i.ucSleepInterval : ""
                    }
                    PMiguseCaseDetails.push(selUseCase);
                }
                for (let i of this.PAMiguseCaseValue) {
                    let selUseCase = {
                        "useCaseName": i.useCaseName,
                        "useCaseId": i.useCaseId,
                        "executionSequence": i.updatedExecution,
                        "ucSleepInterval": i.ucSleepInterval ? i.ucSleepInterval : ""
                    }
                    PAMiguseCaseDetails.push(selUseCase);
                }
                
                for (let i of this.MiguseCaseValue) {
                    let selUseCase = {
                        "useCaseName": i.useCaseName,
                        "useCaseId": i.useCaseId,
                        "executionSequence": i.updatedExecution,
                        "ucSleepInterval": i.ucSleepInterval ? i.ucSleepInterval : ""
                    }
                    MiguseCaseDetails.push(selUseCase);
                }
                for (let i of this.scriptValueNG) {
                    let selScript = {
                        "useCaseName": i.useCaseName,
                        "scriptName": i.scriptName,
                        "scriptId": i.scriptId,
                        "scriptExeSequence": i.updatedExecution,
                        "scriptSleepInterval" : i.scriptSleepInterval,
                        "useGeneratedScript" : i.useGeneratedScript
                    }
                    scriptDetailsNG.push(selScript);
                }
                for (let i of this.scriptValueMig) {
                    let selScript = {
                        "useCaseName": i.useCaseName,
                        "scriptName": i.scriptName,
                        "scriptId": i.scriptId,
                        "scriptExeSequence": i.updatedExecution,
                        "scriptSleepInterval" : i.scriptSleepInterval,
                        "useGeneratedScript" : i.useGeneratedScript
                    }
                    scriptDetailsMig.push(selScript);
                }
                for (let i of this.scriptValuePMig) {
                    let selScript = {
                        "useCaseName": i.useCaseName,
                        "scriptName": i.scriptName,
                        "scriptId": i.scriptId,
                        "scriptExeSequence": i.updatedExecution,
                        "scriptSleepInterval" : i.scriptSleepInterval,
                        "useGeneratedScript" : i.useGeneratedScript
                    }
                    scriptDetailsPMig.push(selScript);
                }

                for (let i of this.pascriptValuePMig) {
                    let selScript = {
                        "useCaseName": i.useCaseName,
                        "scriptName": i.scriptName,
                        "scriptId": i.scriptId,
                        "scriptExeSequence": i.updatedExecution,
                        "scriptSleepInterval" : i.scriptSleepInterval,
                        "useGeneratedScript" : i.useGeneratedScript
                    }
                    scriptDetailsPAMig.push(selScript);
                }

                for (let i of this.NSscriptValue) {
                    let selScript = {
                        "useCaseName": i.useCaseName,
                        "scriptName": i.scriptName,
                        "scriptId": i.scriptId,
                        "scriptExeSequence": i.updatedExecution,
                        "scriptSleepInterval" : i.scriptSleepInterval,
                        "useGeneratedScript" : i.useGeneratedScript
                    }
                    scriptDetailsNEMig.push(selScript);
                }
                let selNE = {
                    "neId": key.enbId,
                    "neName": key.neName
                }
                neDetails.push(selNE);   
               // let currentForm = event.target.parentNode.parentNode,
                  let  runTestFormDetails = {
                        "testname": key.testName,                        
                      //  "lsmVersion": "",
                      //  "lsmName": "",
                      //  "lsmId":"",
                        "ciqName":key.ciqName ? key.ciqName : "",
                        "checklistFileName":  key.migrationRunTestModel ? key.migrationRunTestModel.checklistFileName : "",
                        "checklistFilePath": "",
                        "neDetails":neDetails,
                        "useCase": MiguseCaseDetails,       
                        "scripts" :scriptDetailsMig,
                        "id":key.id,
                      //  "testDesc": currentForm.querySelector("#testDescription").value,
                        "password":"",
                        "currentPassword":this.ckeckedOrNot,
                        "rfScriptFlag": this.rfScriptFlag,
                        "prePostAuditFlag": this.prePostAuditFlag,
                        "ovUpdate": this.ovUpdate ? true : false,
                        "multipleDuo": this.multipleDuo == null ? false : this.multipleDuo
                    },



                    runTestFormDetailsPost = JSON.parse(JSON.stringify(runTestFormDetails)),
                    runTestFormDetailsNEGrow = JSON.parse(JSON.stringify(runTestFormDetails)),
                    runTestFormDetailsPreAudit = JSON.parse(JSON.stringify(runTestFormDetails)),
                    runTestFormDetailsNeStatus = JSON.parse(JSON.stringify(runTestFormDetails));
                    runTestFormDetailsPost.useCase = PMiguseCaseDetails;//useCasePostMig;
                    runTestFormDetailsPost.scripts = scriptDetailsPMig;
                    runTestFormDetailsPost.checklistFileName = key.postMigrationRunTestModel ? key.postMigrationRunTestModel.checklistFileName : "";
                    runTestFormDetailsNEGrow.useCase = NGuseCaseDetails; //Static UseCases from Vijay
                    runTestFormDetailsNEGrow.scripts = scriptDetailsNG;
                    runTestFormDetailsNEGrow.checklistFileName = key.negrowRunTestModel ? key.negrowRunTestModel.checklistFileName : ""
                    runTestFormDetailsPreAudit.useCase = PAMiguseCaseDetails;
                    runTestFormDetailsPreAudit.scripts = scriptDetailsPAMig;
                    runTestFormDetailsPreAudit.checklistFileName = key.preAuditMigrationRunTestModel ? key.preAuditMigrationRunTestModel.checklistFileName : "";
                    runTestFormDetailsNeStatus.useCase= NEMiguseCaseDetails;
                    runTestFormDetailsNeStatus.scripts= scriptDetailsNEMig;
                    runTestFormDetailsNeStatus.checklistFileName = key.neStatusRunTestModel ? key.neStatusRunTestModel.checklistFileName : "";
                    runTestFormDetailsPost.prePostAuditFlag = false;
                    runTestFormDetailsNEGrow.prePostAuditFlag = false;
                    runTestFormDetailsPreAudit.prePostAuditFlag = false;
                    runTestFormDetailsNeStatus.prePostAuditFlag = false;
                    if(type == "NEGrow")
                    {
                        if(key.neGrowStatus==='Completed')
                            state="rerunIndependent";
                        else
                        state="runIndependent";
                    }
                    else if(type == 'preaudit'){
                        if(key.preAuditStatus==='Completed')
                        state="rerunIndependent";
                        else
                        state="runIndependent";
                    }
                    else if(type == 'nestatus'){
                        if(key.neStatus==='Completed')
                        state="rerunIndependent";
                        else
                        state="runIndependent";
                    }
                    else if(type == "Migration")
                    {
                        if(key.migStatus==='Completed')
                            state="rerunIndependent";
                        else
                        state="runIndependent";
                    }
                    else if(key.type==='PostMigration'){
                        if(key.postMigStatus==='Completed')
                        state="rerunIndependent";
                        else
                        state="runIndependent";
                    }
                    else{
                            if(key.preMigStatus ==='Completed')
                                state="rerunIndependent";
                            else
                                state="runIndependent"
                         }
                   // let neVersion = this.selectedNeVersion?this.selectedNeVersion : null;   
                   console.log(runTestFormDetailsPost,runTestFormDetailsNEGrow,runTestFormDetails)  
                if(!(runTestFormDetailsNEGrow.useCase.length  == 0  && runTestFormDetails.useCase.length ==0 && runTestFormDetailsPost.useCase.length == 0 
                    && runTestFormDetailsPreAudit.useCase.length == 0 && runTestFormDetailsNeStatus.useCase.length == 0)) {  
                this.runtestService.independentRun(this.sharedService.createServiceToken(), state,runTestFormDetails,runTestFormDetailsNeStatus,runTestFormDetailsPost,
                runTestFormDetailsPreAudit,runTestFormDetailsNEGrow,this.selectedNEIDs,this.paginationDetails, type, this.ranAtpFlagUseCase)
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
                                        this.showInnerLoader = false;
                                        this.closeModelUseCase();
                                       if(jsonStatue.requestType == 'CHECK_CONNECTION'){
                                            this.message = "Connection Check Successful";
                                        }else{
                                            this.message = "Run started successfully!";
                                        }   
                                        this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                                        this.resetValues(event);
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
                                let jsonStatue = { "reason": "test", "requestType": "RUN_TEST", "sessionId": "9a1afbd4", "serviceToken": "67492", "status": "SUCCESS" };
                                if (jsonStatue.status == "SUCCESS") {
                                    this.closeModelUseCase();
                                    this.showInnerLoader = false;
                                    this.message = "Run test started successfully!";
                                    console.log(this.selectedItems)
                                    this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                                    this.resetValues(event);
                                } else {
                                    this.showInnerLoader = false;
                                    this.displayModel(jsonStatue.reason, "failureIcon");
                                }

                            }, 100); */

                            //Please Comment while checkIn   
                        });
                    }
                    else{
                        this.showInnerLoader = false;
                        this.closeModelUseCase();
                        this.displayModel("Please select a Usecase to Continue", "failureIcon");
                    }

        }, 100);

    }


    showListOfSCriptFiles (popover, key) {
        this.scriptFilesData = [];//key.checkListMap.script;
        this.p1 = popover;

        this.showInnerLoader = true;
        this.runtestService.getChecklistScriptDetails(this.sharedService.createServiceToken(), this.rowData, key.stepIndex)
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
                                this.showInnerLoader = false;
                                this.scriptFilesData = jsonStatue.scriptList;
                                popover.open();
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

                        let jsonStatue;
                        this.showInnerLoader = false;
                        if(key.stepIndex % 2 == 0){
                            jsonStatue = JSON.parse('{"scriptList":[{"id":86,"programDetailsEntity":{"id":1,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-05-31T09:20:26.000+0000","status":"Active","remarks":"","networkColor":"#0f2a79"},"programName":"VZN-4G-CDU30","programDescription":"CDU30","status":"Active","creationDate":"2019-05-06T09:48:34.000+0000","createdBy":"superadmin"},"checkListFileName":"Verizon_Migration Checklist 100418_version4.8 (2).xlsx","sheetName":"Migration","stepIndex":1,"scriptName":"13th June CLI Script1.sh","scriptExeSeq":109,"createdBy":"superadmin","creationDate":"2019-06-19 14:17:34"},{"id":83,"programDetailsEntity":{"id":1,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-05-31T09:20:26.000+0000","status":"Active","remarks":"","networkColor":"#0f2a79"},"programName":"VZN-4G-CDU30","programDescription":"CDU30","status":"Active","creationDate":"2019-05-06T09:48:34.000+0000","createdBy":"superadmin"},"checkListFileName":"Verizon_Migration Checklist 100418_version4.8 (2).xlsx","sheetName":"Migration","stepIndex":1,"scriptName":"13th June Script1.sh","scriptExeSeq":47,"createdBy":"superadmin","creationDate":"2019-06-19 14:17:33"},{"id":60,"programDetailsEntity":{"id":1,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-05-31T09:20:26.000+0000","status":"Active","remarks":"","networkColor":"#0f2a79"},"programName":"VZN-4G-CDU30","programDescription":"CDU30","status":"Active","creationDate":"2019-05-06T09:48:34.000+0000","createdBy":"superadmin"},"checkListFileName":"Verizon_Migration Checklist 100418_version4.8 (2).xlsx","sheetName":"Migration","stepIndex":1,"scriptName":"14th June CLI Script1.sh","scriptExeSeq":29,"createdBy":"superadmin","creationDate":"2019-06-19 14:17:33"},{"id":65,"programDetailsEntity":{"id":1,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-05-31T09:20:26.000+0000","status":"Active","remarks":"","networkColor":"#0f2a79"},"programName":"VZN-4G-CDU30","programDescription":"CDU30","status":"Active","creationDate":"2019-05-06T09:48:34.000+0000","createdBy":"superadmin"},"checkListFileName":"Verizon_Migration Checklist 100418_version4.8 (2).xlsx","sheetName":"Migration","stepIndex":1,"scriptName":"17th June CLI SCript1.sh","scriptExeSeq":65,"createdBy":"superadmin","creationDate":"2019-06-19 14:17:33"},{"id":82,"programDetailsEntity":{"id":1,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-05-31T09:20:26.000+0000","status":"Active","remarks":"","networkColor":"#0f2a79"},"programName":"VZN-4G-CDU30","programDescription":"CDU30","status":"Active","creationDate":"2019-05-06T09:48:34.000+0000","createdBy":"superadmin"},"checkListFileName":"Verizon_Migration Checklist 100418_version4.8 (2).xlsx","sheetName":"Migration","stepIndex":1,"scriptName":"18th June XML Script.xml","scriptExeSeq":88,"createdBy":"superadmin","creationDate":"2019-06-19 14:17:33"},{"id":67,"programDetailsEntity":{"id":1,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-05-31T09:20:26.000+0000","status":"Active","remarks":"","networkColor":"#0f2a79"},"programName":"VZN-4G-CDU30","programDescription":"CDU30","status":"Active","creationDate":"2019-05-06T09:48:34.000+0000","createdBy":"superadmin"},"checkListFileName":"Verizon_Migration Checklist 100418_version4.8 (2).xlsx","sheetName":"Migration","stepIndex":1,"scriptName":"june14 cli.sh","scriptExeSeq":23,"createdBy":"superadmin","creationDate":"2019-06-19 14:17:33"}],"sessionId":"f8976e61","serviceToken":"78232","status":"SUCCESS"}');
                        }
                        else {
                            jsonStatue = JSON.parse('{"sessionId":"7e088256","serviceToken":"81749","status":"SUCCESS","scriptList":[],"reason":""}');
                        }
                        // let jsonStatue = JSON.parse('{"sessionId":"7e088256","serviceToken":"81749","status":"SUCCESS","scriptList":[],"reason":""}');
                        // let jsonStatue = JSON.parse('{"scriptList":[{"id":86,"programDetailsEntity":{"id":1,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-05-31T09:20:26.000+0000","status":"Active","remarks":"","networkColor":"#0f2a79"},"programName":"VZN-4G-CDU30","programDescription":"CDU30","status":"Active","creationDate":"2019-05-06T09:48:34.000+0000","createdBy":"superadmin"},"checkListFileName":"Verizon_Migration Checklist 100418_version4.8 (2).xlsx","sheetName":"Migration","stepIndex":1,"scriptName":"13th June CLI Script1.sh","scriptExeSeq":109,"createdBy":"superadmin","creationDate":"2019-06-19 14:17:34"},{"id":83,"programDetailsEntity":{"id":1,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-05-31T09:20:26.000+0000","status":"Active","remarks":"","networkColor":"#0f2a79"},"programName":"VZN-4G-CDU30","programDescription":"CDU30","status":"Active","creationDate":"2019-05-06T09:48:34.000+0000","createdBy":"superadmin"},"checkListFileName":"Verizon_Migration Checklist 100418_version4.8 (2).xlsx","sheetName":"Migration","stepIndex":1,"scriptName":"13th June Script1.sh","scriptExeSeq":47,"createdBy":"superadmin","creationDate":"2019-06-19 14:17:33"},{"id":60,"programDetailsEntity":{"id":1,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-05-31T09:20:26.000+0000","status":"Active","remarks":"","networkColor":"#0f2a79"},"programName":"VZN-4G-CDU30","programDescription":"CDU30","status":"Active","creationDate":"2019-05-06T09:48:34.000+0000","createdBy":"superadmin"},"checkListFileName":"Verizon_Migration Checklist 100418_version4.8 (2).xlsx","sheetName":"Migration","stepIndex":1,"scriptName":"14th June CLI Script1.sh","scriptExeSeq":29,"createdBy":"superadmin","creationDate":"2019-06-19 14:17:33"},{"id":65,"programDetailsEntity":{"id":1,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-05-31T09:20:26.000+0000","status":"Active","remarks":"","networkColor":"#0f2a79"},"programName":"VZN-4G-CDU30","programDescription":"CDU30","status":"Active","creationDate":"2019-05-06T09:48:34.000+0000","createdBy":"superadmin"},"checkListFileName":"Verizon_Migration Checklist 100418_version4.8 (2).xlsx","sheetName":"Migration","stepIndex":1,"scriptName":"17th June CLI SCript1.sh","scriptExeSeq":65,"createdBy":"superadmin","creationDate":"2019-06-19 14:17:33"},{"id":82,"programDetailsEntity":{"id":1,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-05-31T09:20:26.000+0000","status":"Active","remarks":"","networkColor":"#0f2a79"},"programName":"VZN-4G-CDU30","programDescription":"CDU30","status":"Active","creationDate":"2019-05-06T09:48:34.000+0000","createdBy":"superadmin"},"checkListFileName":"Verizon_Migration Checklist 100418_version4.8 (2).xlsx","sheetName":"Migration","stepIndex":1,"scriptName":"18th June XML Script.xml","scriptExeSeq":88,"createdBy":"superadmin","creationDate":"2019-06-19 14:17:33"},{"id":67,"programDetailsEntity":{"id":1,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-05-31T09:20:26.000+0000","status":"Active","remarks":"","networkColor":"#0f2a79"},"programName":"VZN-4G-CDU30","programDescription":"CDU30","status":"Active","creationDate":"2019-05-06T09:48:34.000+0000","createdBy":"superadmin"},"checkListFileName":"Verizon_Migration Checklist 100418_version4.8 (2).xlsx","sheetName":"Migration","stepIndex":1,"scriptName":"june14 cli.sh","scriptExeSeq":23,"createdBy":"superadmin","creationDate":"2019-06-19 14:17:33"}],"sessionId":"f8976e61","serviceToken":"78232","status":"SUCCESS"}');

                        if (jsonStatue.status == "SUCCESS") {
                            this.showInnerLoader = false;
                            this.scriptFilesData = jsonStatue.scriptList;
                            popover.open();
                        } else {
                            this.showInnerLoader = false;
                            this.displayModel(jsonStatue.reason, "failureIcon");
                        }

                    }, 100); */

                    //Please Comment while checkIn   
                });
    }

    compareFn(o1: any, o2: any) {
        return o1 && o2 ? o1.id === o2.id : o1 === o2;
    }

    editCheckListRow(event, index) {
        this.editCLMode = index;
      
    }
    saveCheckListRow(event, index,key) {     
       key.value = $("#edited_Remarks").val();    
        this.checklistTableData[index].checkListMap.Remarks = $("#edited_Remarks").val();
        this.editCLMode = -1;
    }
    cancelCheckListRow(event){
        this.editCLMode = -1;
    }

    checkStatus(cmdStatus)
    {
        if(cmdStatus=='PASS')
        {
            this.statusCheck='PASS';
        }
        else if(cmdStatus!='PASS')
        {
            this.statusCheck='FAIL';
        }
    }
    showRunningLogContainer() {
        this.showRunningLogContent = true;
        this.showRunningLogLauncher = false;
    }
    hideRunningLogContainer() {
        this.showRunningLogContent = false;
        this.showRunningLogLauncher = true;
    }
    closeRunningLogContainer() {
        this.showRunningLogContent = false;
        this.showRunningLogLauncher = false;

        // Stop service call timer
        clearInterval(this.runningLogInterval);
        this.runningLogInterval = null;
    }


    showListOfRule(popover) {
        this.p2 = popover;
        popover.open();
    }

    wideRunningLog()
    {
        if(this.showWideRunningLog)
        {
            this.showWideRunningLog=false;
        }
        else{
            this.showWideRunningLog=true;

        }
    }

       copyFunction() {
        (document.getElementById('logArea') as HTMLInputElement).select();
        document.execCommand("copy");
        var x = document.getElementById("snackbar");
        x.className = "show";
        setTimeout(function(){ x.className = x.className.replace("show", ""); }, 3000);
      }


      downLoadFile(key) {

        this.runtestService.downloadResultFile(key,this.sharedService.createServiceToken())
        .subscribe(
            data => {
                let blob = new Blob([data["_body"]], {
                    type: "application/octet-stream"
                });
    
                FileSaver.saveAs(blob,key.globalFlieName);              
    
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


      downloadGeneratedScriptsRow(testName, key,type, ranatpFilePath = '') {
       
        this.runtestService.downloadGenScriptsRow(key,type,this.sharedService.createServiceToken(),ranatpFilePath)
        .subscribe(
            data => {
                let blob = new Blob([data["_body"]], {
                    type: "application/octet-stream"
                });
    
                let newFileName = "";
    
                
                    newFileName = testName + "_Scripts.zip"; 
              
    
                FileSaver.saveAs(blob,newFileName);
    
            },
            error => {
                //Please Comment while checkIn
                /* let jsonStatue: any = {"sessionId":"506db794","reason":"Download Failed","status":"SUCCESS","serviceToken":"63524"};
                let blob = new Blob([jsonStatue["_body"]], {
                    type: "application/octet-stream"
                });
    
                let newFileName = "";
    
                if (filePath.split(",").length > 1) {
                    newFileName = testName + "_Scripts.zip";
                }
                else {
                    newFileName = filePath.substring(filePath.lastIndexOf("/")+1,filePath.length);
                }
    
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

      downloadGeneratedScripts(testName, filePath, ranatpFilePath = '') {

        if(ranatpFilePath != '')
        {
            filePath = filePath + "," + ranatpFilePath;
        }
        this.runtestService.downloadGenScripts(filePath,this.sharedService.createServiceToken())
        .subscribe(
            data => {
                let blob = new Blob([data["_body"]], {
                    type: "application/octet-stream"
                });
    
                let newFileName = "";
    
                if(filePath.split(",").length > 1) {
                    newFileName = testName + "_Scripts.zip"; 
                }
                else {
                    newFileName = filePath.substring(filePath.lastIndexOf("/")+1,filePath.length);
                }
    
                FileSaver.saveAs(blob,newFileName);
    
            },
            error => {
                //Please Comment while checkIn
                /* let jsonStatue: any = {"sessionId":"506db794","reason":"Download Failed","status":"SUCCESS","serviceToken":"63524"};
                let blob = new Blob([jsonStatue["_body"]], {
                    type: "application/octet-stream"
                });
    
                let newFileName = "";
    
                if (filePath.split(",").length > 1) {
                    newFileName = testName + "_Scripts.zip";
                }
                else {
                    newFileName = filePath.substring(filePath.lastIndexOf("/")+1,filePath.length);
                }
    
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

    downloadResultTextPdf(testName) {
        const doc = new jsPDF();
        const specialElementHandlers = {
            '#editor': function (element, renderer) {
                return true;
            }
        };
        const textViewDisp: Element = document.getElementById('textViewDisp');
        doc.fromHTML(textViewDisp.innerHTML, 10, 10, {
            width: 150,
            'elementHandlers': specialElementHandlers
        });
        doc.save(testName + '_Result.pdf');
    }

    showIconsDesc(key) {
        let status = key ? key.progressStatus : null;
        let iconNum = 0;
        if(status) {
            switch(status) {
                case "InProgress":
                    iconNum += this.ICONS[1] + this.ICONS[3] + this.ICONS[5];
                    break;
                case "Failure":
                    iconNum += this.ICONS[1] + this.ICONS[2] + this.ICONS[3] + this.ICONS[4] + this.ICONS[5];
                    break;
                case "Completed":
                    iconNum += this.ICONS[0];
                    break;
                case "NotYetStarted":
                    break;
                case "InputsRequired":
                    iconNum += this.ICONS[0];
                    break;
            }
        }
        return iconNum;
    }

    showIcon(iconIndex, migrationType, status) {
        // [0 - "Play", 1 - "Runniing Log", 2- "CheckList", 3 - "Result", 4 - "DownloadLogs", 5 - "DownloadGenScript", 6 - View failure Log, 7 - Retry, 8 - Continue, 9 - Download Report, 10 - View Audit Issues]
        // return this.ICONS[iconIndex] & this.showIconsDesc(key);
        // let iconObj = {"Migration":{"YetToStart":{"0":false,"1":false,"2":false,"3":false,"4":false,"5":false},"InputRequired":{"0":true,"1":false,"2":false,"3":false,"4":false,"5":false},"InProgress":{"0":false,"1":true,"2":false,"3":true,"4":false,"5":true},"Completed":{"0":true,"1":false,"2":false,"3":false,"4":false,"5":false},"Failure":{"0":true,"1":true,"2":true,"3":true,"4":true,"5":true}},"PreMigration":{"YetToStart":{"0":false,"1":false,"2":false,"3":false,"4":false,"5":false},"InputRequired":{"0":true,"1":false,"2":false,"3":false,"4":false,"5":false},"InProgress":{"0":false,"1":false,"2":false,"3":false,"4":false,"5":false},"Completed":{"0":true,"1":false,"2":false,"3":false,"4":false,"5":false},"Failure":{"0":true,"1":false,"2":false,"3":false,"4":false,"5":true}},"PostMigration":{"YetToStart":{"0":false,"1":false,"2":false,"3":false,"4":false,"5":false},"InputRequired":{"0":true,"1":false,"2":false,"3":false,"4":false,"5":false},"InProgress":{"0":false,"1":true,"2":false,"3":false,"4":false,"5":true},"Completed":{"0":true,"1":false,"2":false,"3":false,"4":false,"5":false},"Failure":{"0":true,"1":true,"2":true,"3":true,"4":true,"5":true}}};
        let iconObj ={"Migration":{"InProgress":{"0":false,"1":true,"2":false,"3":true,"4":false,"5":true,"6":false,"7":false,"8":false,"9":false,"10":false},"Completed":{"0":true,"1":true,"2":false,"3":true,"4":true,"5":true,"6":false,"7":false,"8":false,"9":false,"10":false},"Failure":{"0":true,"1":true,"2":false,"3":true,"4":true,"5":true,"6":true,"7":true,"8":true,"9":false,"10":false},"NotYetStarted":{"0":false,"1":false,"2":false,"3":false,"4":false,"5":false,"6":false,"7":false,"8":false,"9":false,"10":false},"InputsRequired":{"0":true,"1":false,"2":false,"3":false,"4":false,"5":false,"6":false,"7":false,"8":false,"9":false,"10":false},"StoppedByUser":{"0":true,"1":false,"2":false,"3":false,"4":false,"5":false,"6":false,"7":false,"8":false,"9":false,"10":false}},"PreMigration":{"InProgress":{"0":false,"1":false,"2":false,"3":false,"4":false,"5":false,"6":false,"7":false,"8":false,"9":false,"10":false},"Completed":{"0":false,"1":false,"2":false,"3":false,"4":false,"5":true,"6":false,"7":false,"8":false,"9":false,"10":false},"Failure":{"0":false,"1":false,"2":false,"3":false,"4":false,"5":false,"6":true,"7":false,"8":false,"9":false,"10":false},"NotYetStarted":{"0":false,"1":false,"2":false,"3":false,"4":false,"5":false,"6":false,"7":false,"8":false,"9":false,"10":false},"InputsRequired":{"0":true,"1":false,"2":false,"3":false,"4":false,"5":false,"6":false,"7":false,"8":false,"9":false,"10":false},"StoppedByUser":{"0":true,"1":false,"2":false,"3":false,"4":false,"5":false,"6":false,"7":false,"8":false,"9":false,"10":false}},"PostMigration":{"InProgress":{"0":false,"1":true,"2":false,"3":false,"4":false,"5":true,"6":false,"7":false,"8":false,"9":false,"10":false},"Completed":{"0":true,"1":true,"2":false,"3":true,"4":true,"5":true,"6":false,"7":false,"8":false,"9":true,"10":true},"Failure":{"0":true,"1":true,"2":false,"3":true,"4":true,"5":true,"6":true,"7":false,"8":false,"9":true,"10":true},"NotYetStarted":{"0":false,"1":false,"2":false,"3":false,"4":false,"5":false,"6":false,"7":false,"8":false,"9":false,"10":false},"InputsRequired":{"0":true,"1":false,"2":false,"3":false,"4":false,"5":false,"6":false,"7":false,"8":false,"9":false,"10":false},"StoppedByUser":{"0":true,"1":false,"2":false,"3":false,"4":false,"5":false,"6":false,"7":false,"8":false,"9":false,"10":false}},"NEGrow":{"NotYetStarted":{"0":false,"1":false,"2":false,"3":false,"4":false,"5":false,"6":false,"7":false,"8":false,"9":false,"10":false},"InputsRequired":{"0":true,"1":false,"2":false,"3":false,"4":false,"5":false,"6":false,"7":false,"8":false,"9":false,"10":false},"InProgress":{"0":false,"1":true,"2":false,"3":true,"4":false,"5":true,"6":false,"7":false,"8":false,"9":false,"10":false},"Completed":{"0":true,"1":true,"2":false,"3":true,"4":true,"5":true,"6":false,"7":false,"8":false,"9":false,"10":false},"Failure":{"0":true,"1":true,"2":false,"3":true,"4":true,"5":true,"6":true,"7":false,"8":false,"9":false,"10":false},"StoppedByUser":{"0":true,"1":false,"2":false,"3":false,"4":false,"5":false,"6":false,"7":false,"8":false,"9":false,"10":false}},"preaudit":{"InProgress":{"0":false,"1":true,"2":false,"3":false,"4":false,"5":true,"6":false,"7":false,"8":false,"9":false,"10":false},"Completed":{"0":true,"1":true,"2":false,"3":true,"4":true,"5":true,"6":false,"7":false,"8":false,"9":true,"10":true},"Failure":{"0":true,"1":true,"2":false,"3":true,"4":true,"5":true,"6":true,"7":false,"8":false,"9":true,"10":true},"NotYetStarted":{"0":false,"1":false,"2":false,"3":false,"4":false,"5":false,"6":false,"7":false,"8":false,"9":false,"10":false},"InputsRequired":{"0":true,"1":false,"2":false,"3":false,"4":false,"5":false,"6":false,"7":false,"8":false,"9":false,"10":false},"StoppedByUser":{"0":true,"1":false,"2":false,"3":false,"4":false,"5":false,"6":false,"7":false,"8":false,"9":false,"10":false}},"nestatus":{"InProgress":{"0":false,"1":true,"2":false,"3":false,"4":false,"5":true,"6":false,"7":false,"8":false,"9":false,"10":false},"Completed":{"0":true,"1":true,"2":false,"3":true,"4":true,"5":true,"6":false,"7":false,"8":false,"9":true,"10":true},"Failure":{"0":true,"1":true,"2":false,"3":true,"4":true,"5":true,"6":true,"7":false,"8":false,"9":true,"10":true},"NotYetStarted":{"0":false,"1":false,"2":false,"3":false,"4":false,"5":false,"6":false,"7":false,"8":false,"9":false,"10":false},"InputsRequired":{"0":true,"1":false,"2":false,"3":false,"4":false,"5":false,"6":false,"7":false,"8":false,"9":false,"10":false},"StoppedByUser":{"0":true,"1":false,"2":false,"3":false,"4":false,"5":false,"6":false,"7":false,"8":false,"9":false,"10":false}}}

        let showIcon: Boolean = false;

        try {
            showIcon = iconObj[migrationType][status][iconIndex];
            
        }
        catch {

        }
        return showIcon;
    }

    showIconContainer(status) {
        let iconContainerIndex = 0; //0 - NONE, 1 - Only Run/Play icon container, 2 - All Icons container
        if (status) {
            if (status == "InputsRequired" || status == "StoppedByUser") {
                iconContainerIndex = 1;
            }
            else if (status == "NotYetStarted") {
                iconContainerIndex = 0;
            }
            else {
                iconContainerIndex = 2;
            }
        }
        return iconContainerIndex;
    }

    showUseCaseModal(type, key) {
        this.showStateUseCase = type;
        this.useCaseModelKey = key;
        this.ranAtpFlagUseCase = false;
        console.log(type,this.showStateUseCase )
        if(type == 'RE_RUN'){
            this.getUseCasesForReRun(key);
        }
        else
        {
             type ? this.getUseCases(type, key) : "";
        }
        setTimeout(() => {
            this.useCaseModalBlock = this.modalService.open(this.migPromptModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal connectionPrompt' });
        }, 0);
    }

    /* scanTableRow() {
        let tableRowData = this.tableData.runTestTableDetails;
        let STATE_USECASE = [4, 2, 1]; //[NEGrow, Migration, PostMigration]
        this.showStateUseCase = 0;
        for(let i = 0; i < tableRowData.length; i++) {
            // TODO- If negrow/migration/postmigration use case is available, no need to show it in popup
            console.log(tableRowData[i].testName);
            if(tableRowData[i].neGrowStatus == "InputsRequired") {
                //Show NEGRow use in case popup
                this.showStateUseCase = STATE_USECASE[0] + STATE_USECASE[1] + STATE_USECASE[2];
            }
            else if(tableRowData[i].migStatus == "InputsRequired") {
                //Show Migration use case popup
                this.showStateUseCase = STATE_USECASE[1] + STATE_USECASE[2];
            }
            else if(tableRowData[i].postMigStatus == "InputsRequired") {
                //Show Migration use case popup
                this.showStateUseCase = STATE_USECASE[2];
            }
            if(this.showStateUseCase) {
                setTimeout(() => {
                    this.pswdModalBlock = this.modalService.open(this.migPromptModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal connectionPrompt' });
                }, 0);
                break;
            }
        }
    } */

    stopRunningTest(key) {
        this.showLoader = true;

        this.runtestService.stopWFM(this.sharedService.createServiceToken(), key)
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
                                this.showLoader = false;
                                this.message = "WorkFlow stopped successfully!";
                                this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
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
                        let jsonStatue = JSON.parse('{"sessionId":"8bd2c9d3","serviceToken":58202,"status" :"SUCCESS","reason":"" }');

                        if (jsonStatue.status == "SUCCESS") {
                            this.showLoader = false;
                            this.message = "Workflow  stopped successfully!";
                            this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                        } else {
                            this.showLoader = false;
                            this.displayModel(jsonStatue.reason, "failureIcon");
                        }
                    }, 100); */

                    //Please Comment while checkIn   
                });
    }

    continueTest(key) {
        // this.getUseCasesFor()
        // Anayze the needed use case and get the use cases list
        // STATE_USECASE = [4, 2, 1]; //[NEGrow, Migration, PostMigration]preAuditStatus
        let showUseCaseVisibility = 0;
        if(key.neGrowStatus == "StoppedByUser" || key.neGrowStatus == "InputsRequired" || key.neGrowStatus == "NotYetStarted") {
            showUseCaseVisibility += this.STATE_USECASE[0];
        }
        if(key.migStatus == "StoppedByUser" || key.migStatus == "InputsRequired" || key.migStatus == "NotYetStarted") {
            showUseCaseVisibility += this.STATE_USECASE[1];
        }
        if(key.postMigStatus == "StoppedByUser" || key.postMigStatus == "InputsRequired" || key.postMigStatus == "NotYetStarted") {
            showUseCaseVisibility += this.STATE_USECASE[2];
        }
        if(key.preAuditStatus == "StoppedByUser" || key.preAuditStatus == "InputsRequired" || key.preAuditStatus == "NotYetStarted") {
            showUseCaseVisibility += this.STATE_USECASE[3];
        }
        if(key.neStatus == "StoppedByUser" || key.neStatus == "InputsRequired" || key.neStatus == "NotYetStarted") {
            showUseCaseVisibility += this.STATE_USECASE[4];
        }
       
        this.useCaseDropDownVisibility = showUseCaseVisibility;

        // this.showUseCaseModal("", key);
        this.getUseCasesForContinue(key);

    }
    
    getUseCasesForContinue(key) {
        if (this.checkBitwise(4)) {  //For NEGrow
            this.getUseCases("NEGrow", key);
        }
        if (this.checkBitwise(16)) {  //For Migration
            this.getUseCases("nestatus", key);
        }
        if (this.checkBitwise(8)) {  //For Migration
            this.getUseCases("preaudit", key);
        }
        if (this.checkBitwise(2)) {  //For Migration
            this.getUseCases("Migration", key);
        }
        if (this.checkBitwise(1)) {  //For Migration
            this.getUseCases("PostMigration", key);
        }

        this.showUseCaseModal("", key);

    }


    getUseCasesForReRun(key) {
        this.getUseCases("NEGrow", key);
        this.getUseCases("Migration", key);
        this.getUseCases("PostMigration", key);
        this.getUseCases("preaudit", key);
        this.getUseCases("nestatus", key);
        //this.showUseCaseModal("", key);
    }

    showContinueOrStop(key) {
        let returnIndex = 0;    //0 : None, 1 : Continue, 2: Stop
        if(key.neGrowStatus == "StoppedByUser" || key.preAuditStatus == "StoppedByUser" || key.migStatus == "StoppedByUser" || key.postMigStatus == "StoppedByUser" || key.neStatus == "StoppedByUser") {
            returnIndex = 1;
        }
        else if(key.neGrowStatus == "InProgress" || key.preAuditStatus == "InProgress" || key.migStatus == "InProgress" || key.postMigStatus == "InProgress" || key.neStatus == "InProgress") {
            returnIndex = 2;
        }

        return returnIndex;
    }

    checkBitwise(number) {
        return this.useCaseDropDownVisibility & number;
    }

    showRowIcon(rowIconType, key) {
        let showIcon = false;

        switch(rowIconType) {
            case "downloadLogs":
                if(key.neGrowStatus == "InProgress" || key.neGrowStatus == "Completed" || key.neGrowStatus == "StoppedByUser" || key.neGrowStatus == "Failure" ||
                key.preAuditStatus == "InProgress" || key.preAuditStatus == "Completed" || key.preAuditStatus == "StoppedByUser" || key.preAuditStatus == "Failure" ||
                key.neStatus == "InProgress" || key.neStatus == "Completed" || key.neStatus == "StoppedByUser" || key.neStatus == "Failure" ||
                    key.migStatus == "InProgress" || key.migStatus == "Completed" || key.migStatus == "StoppedByUser" || key.migStatus == "Failure" ||
                    key.postMigStatus == "InProgress" || key.postMigStatus == "Completed" || key.postMigStatus == "StoppedByUser" || key.postMigStatus == "Failure") {
                        showIcon = true;
                }
            case "downloadScript":
                if(key.neGrowStatus == "InProgress" || key.neGrowStatus == "Completed" || key.neGrowStatus == "StoppedByUser" || key.neGrowStatus == "Failure" ||
                key.preAuditStatus == "InProgress" || key.preAuditStatus == "Completed" || key.preAuditStatus == "StoppedByUser" || key.preAuditStatus == "Failure" ||
                key.neStatus == "InProgress" || key.neStatus == "Completed" || key.neStatus == "StoppedByUser" || key.neStatus == "Failure" ||
                key.migStatus == "InProgress" || key.migStatus == "Completed" || key.migStatus == "StoppedByUser" || key.migStatus == "Failure" ||
                key.postMigStatus == "InProgress" || key.postMigStatus == "Completed" || key.postMigStatus == "StoppedByUser" || key.postMigStatus == "Failure") {
                    showIcon = true;
            }
            break;
        }

        return showIcon;
    }

    downloadReports(pmModel) {
        //this.showLoader = true;
        if(pmModel) {

            this.runtestService.downloadDiffFile(pmModel.result, pmModel.resultFilePath, this.sharedService.createServiceToken())
                .subscribe(
                    data => {
                        let fileName = pmModel.result;//key.testName + "_Reports.zip";

                        let blob = new Blob([data["_body"]], {
                            type: "application/octet-stream" //"text/plain;charset=utf-8"
                        });

                        FileSaver.saveAs(blob, fileName);
                    },
                    error => {
                        //Please Comment while checkIn
                        /* let jsonStatue: any = { "sessionId": "506db794", "reason": "Download Failed", "status": "SUCCESS", "serviceToken": "63524" };

                        let fileName = pmModel.result;//key.testName + "_Reports.zip";

                        let blob = new Blob(["Hello, world!"], {
                            type: "application/octet-stream" //"text/plain;charset=utf-8" 
                        });

                        FileSaver.saveAs(blob, fileName);

                        setTimeout(() => {
                            this.showLoader = false;
                            if (jsonStatue.status == "SUCCESS") {

                            } else {
                                this.displayModel(jsonStatue.reason, "failureIcon");
                            }

                        }, 1000); */
                        //Please Comment while checkIn
                    });
        }
        else {
            this.displayModel("File not found", "failureIcon");
        }
    }

    openAuditIssues(auditIssuesModal, key) {
        this.auditIssueKey = key;
        this.showAuditIssues();
        this.auditIssuesBlock = this.modalService.open(auditIssuesModal, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView reportView' });
      }

      changeAuditTab(evt) {
        if(evt.nextId == "auditFailureTab") {
            this.showAuditIssues();
        } else {
            this.showAuditPassFail();
        }
        this.selectedAuditTab = evt.nextId;
      }

      downloadAuditReports() {
        if(this.selectedAuditTab == 'auditFailureTab') {
            this.downloadAuditReport();
        } else {
            this.downloadAuditPassFailReport();
        }
      }

      showAuditPassFail() {
        this.showInnerLoader = true;
        this.postAuditPassFailIssues = {};
        this.runtestService.getAuditPassFailReport(this.auditIssueKey, this.sharedService.createServiceToken())
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
                                        this.auditIssueNEInfo = {
                                            "neId": jsonStatue.neId,
                                            "neName": jsonStatue.neName
                                        };
                                        if (jsonStatue.passfailStatus && jsonStatue.passfailStatus.length > 0) {
                                            let passfailStatus = jsonStatue.passfailStatus[0]
                                            if (passfailStatus.auditNeRunSummary && passfailStatus.auditNeRunSummary.length > 0) {
                                                let auditNERunSummary = passfailStatus.auditNeRunSummary[0];
                                                this.postAuditPassFailIssues = auditNERunSummary.runTestParams;
                                            }
                                        }
                                    } else {
                                        this.showInnerLoader = false;
                                        this.displayModel(jsonStatue.reason, "failureIcon");
                                    }
                                }
                                else {
                                    this.showInnerLoader = false;
                                    this.displayModel("No Data", "failureIcon");
                                }
                            }
                        },
                        error => {
                            //Please Comment while checkIn

                            /* setTimeout(() => {
            
                                this.showInnerLoader = false;
            
                                // let jsonStatue = {"neId":"180872","sessionId":"f60ea9cf","neName":"180872_FAWN_MEADOWS_MS","serviceToken":"58603","passfailStatus":[{"testName":"CHECK FIRMWARE RELEASE TEST","auditPassFail":"pass"},{"testName":"SOFTWARE VERSION TEST","auditPassFail":"pass"}],"status":"SUCCESS","reason":""};
                                let jsonStatue = {"neId":"81068001","passfailStatus":[{"id":null,"neId":"81068001","creationDate":null,"neName":null,"tech":"VZN-4G-FSU","node":null,"auditNeRunSummary":[{"runId":"3346","creationDate":null,"runTestParams":{"connected-du-cpri-port-id":"pass","connected-enb-digital-unit-port-id":"fail","administrative-state":"pass","connected-enb-digital-unit-board-id":"fail","enb-ne-id":"fail","operational-mode":"pass","user-label":"fail","port-id":"pass","sw-version":"pass","alarm-type":"fail","du-cpri-port-mode":"pass"}}],"testName":null,"auditPassFail":null,"headerNames":null}],"neheaders":["connected-du-cpri-port-id","connected-enb-digital-unit-port-id","administrative-state","connected-enb-digital-unit-board-id","enb-ne-id","operational-mode","user-label","port-id","sw-version","alarm-type","du-cpri-port-mode"],"sessionId":"61ecf3a4","neName":"81068001_FSU_VETERANS_AND_E_68TH_E","serviceToken":"80625","status":"SUCCESS","reason":""};

                                if (jsonStatue.status == "SUCCESS") {
                                    this.showInnerLoader = false;
                                    this.auditIssueNEInfo = {
                                        "neId": jsonStatue.neId,
                                        "neName": jsonStatue.neName
                                    };
                                    if (jsonStatue.passfailStatus && jsonStatue.passfailStatus.length > 0) {
                                        let passfailStatus = jsonStatue.passfailStatus[0]
                                        if (passfailStatus.auditNeRunSummary && passfailStatus.auditNeRunSummary.length > 0) {
                                            let auditNERunSummary = passfailStatus.auditNeRunSummary[0];
                                            this.postAuditPassFailIssues = auditNERunSummary.runTestParams;
                                        }
                                    }
                                } else {
                                    this.showInnerLoader = false;
                                    this.displayModel(jsonStatue.reason, "failureIcon");
                                }
            
                            }, 100); */

                            //Please Comment while checkIn   
                        });
      }

    showAuditIssues() {
        this.showInnerLoader = true;
        this.runtestService.getAuditSummaryReport(this.auditIssueKey, this.sharedService.createServiceToken())
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
                                        this.auditIssueNEInfo = {
                                            "neId": jsonStatue.neId,
                                            "neName": jsonStatue.neName
                                        };
                                        this.postAuditIssues = jsonStatue.postAuditIssues ? jsonStatue.postAuditIssues : [];
                                    } else {
                                        this.showInnerLoader = false;
                                        this.displayModel(jsonStatue.reason, "failureIcon");
                                    }
                                }
                                else {
                                    this.showInnerLoader = false;
                                    this.displayModel("No Data", "failureIcon");
                                }
                            }
                        },
                        error => {
                            //Please Comment while checkIn

                            /* setTimeout(() => {
            
                                this.showInnerLoader = false;
            
                                let jsonStatue = {"neId":"87851","sessionId":"56e1fd6d","neName":"087851_Weehawken_Poly_eNB_01","serviceToken":"76766","postAuditIssues":[{"test":"To check if eNB is in the correct operational mode and its admin state","testName":"CELL STATUS OPERATION STATE TEST","yangCommand":"eutran-cell-fdd-tdd/operational-state","auditIssue":"cell-num : 18, operational-state : disabled\ncell-num : 28, operational-state : disabled\ncell-num : 38, operational-state : disabled\n","expectedResult":"[ON ALL CELLS]:\n operational-state: enabled","actionItem":"Ping MME\n Check routing information","remarks":""},{"test":"Check to see active calls","testName":"ACTIVE UE COUNT TEST","yangCommand":"/samsung4g-access-enbdu/managed-element/enb-function/eutran-generic-cell/eutran-cell-fdd-tdd/cell-call-count-status","auditIssue":"cell-num : 18, active-ue-count : 0\ncell-num : 28, active-ue-count : 0\ncell-num : 38, active-ue-count : 0\n","expectedResult":"active-ue-count > 0 [ON ALL CELLS]","actionItem":"Check if cell is tansmitting (Using test-tx-power).\nCheck if any  path is disabled.\nCheck alarms (e.g. VSWR, RSSI).\nFurther isolate if its an issue with one cell or all cells on a radio. Lock all cells on a radio/sector and start unlocking them one after other.","remarks":""},{"test":"To Check if all cells are unlocked","testName":"CELL STATUS ADMIN STATE TEST","yangCommand":"eutran-cell-fdd-tdd/administrative-state","auditIssue":"cell-num : 18, administrative-state : locked\ncell-num : 28, administrative-state : locked\ncell-num : 38, administrative-state : locked\n","expectedResult":"[ON ALL CELLS]:\n administrative-state: unlocked","actionItem":"If administrative-state is locked, unlock the cell","remarks":""},{"test":"Active alarms","testName":"ACTIVE ALARM TEST","yangCommand":"active-alarm","auditIssue":"Active Alarm Entries present","expectedResult":"Should be 0 alarms","actionItem":"Refer to Ask Me document for alarm. Refer to C&I database of alarm debugging and actions to debug it","remarks":""}],"status":"SUCCESS","reason":""};

                                if (jsonStatue.status == "SUCCESS") {
                                    this.showInnerLoader = false;
                                    this.auditIssueNEInfo = {
                                        "neId": jsonStatue.neId,
                                        "neName": jsonStatue.neName
                                    };
                                    this.postAuditIssues = jsonStatue.postAuditIssues ? jsonStatue.postAuditIssues : [];
                                } else {
                                    this.showInnerLoader = false;
                                    this.displayModel(jsonStatue.reason, "failureIcon");
                                }
            
                            }, 100); */

                            //Please Comment while checkIn   
                        });
      }
      closeModelAuditReport() {
        this.auditIssueNEInfo = null;
        this.postAuditIssues = [];
        this.postAuditPassFailIssues = {};
        this.auditIssuesBlock.close();
        this.showLoader = false;
      }
      
      downloadBulkSummaryReport() {
        document.getElementById('downloadBulkSearchButton').click();
        this.showLoader = true;
        this.runtestService.downloadBulkAuditReport(this.sharedService.createServiceToken(), this.paginationDetails, this.searchCriteria, this.searchStatus, true)
        .subscribe(
            data => {
                this.showLoader = false;
                let blob = new Blob([data["_body"]], {
                    type: "application/octet-stream"
                });
    
                let newFileName = "AuditPassFailSummaryReport_" + this.sharedService.getCurrentTimestamp() + ".xlsx";
    
                FileSaver.saveAs(blob,newFileName);
    
            },
            error => {
                this.showLoader = false;
                //Please Comment while checkIn
                /* let jsonStatue: any = {"sessionId":"506db794","reason":"Download Failed","status":"SUCCESS","serviceToken":"63524"};
                let blob = new Blob([jsonStatue["_body"]], {
                    type: "application/octet-stream"
                });
    
                let newFileName = "AuditPassFailSummaryReport_" + this.sharedService.getCurrentTimestamp() + ".xlsx";
    
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

      downloadBulkAuditReport() {
        document.getElementById('downloadBulkSearchButton').click();
        this.showLoader = true;
        this.runtestService.downloadBulkAuditReport(this.sharedService.createServiceToken(), this.paginationDetails, this.searchCriteria, this.searchStatus, false)
        .subscribe(
            data => {
                this.showLoader = false;
                let blob = new Blob([data["_body"]], {
                    type: "application/octet-stream"
                });
    
                let newFileName = "AuditSummaryReport_" + this.sharedService.getCurrentTimestamp() + ".zip";
    
                FileSaver.saveAs(blob,newFileName);
    
            },
            error => {
                this.showLoader = false;
                //Please Comment while checkIn
                /* let jsonStatue: any = {"sessionId":"506db794","reason":"Download Failed","status":"SUCCESS","serviceToken":"63524"};
                let blob = new Blob([jsonStatue["_body"]], {
                    type: "application/octet-stream"
                });
    
                let newFileName = "AuditSummaryReport_" + this.sharedService.getCurrentTimestamp() + ".zip";
    
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

      downloadAuditPassFailReport() {
        this.showInnerLoader = true;
        this.runtestService.downloadAuditPassFailReport(this.sharedService.createServiceToken(), this.auditIssueKey, this.auditIssueNEInfo)
        .subscribe(
            data => {
                let blob = new Blob([data["_body"]], {
                    type: "application/octet-stream"
                });
    
                let newFileName = this.auditIssueNEInfo.neName + "_AuditPassFailSummaryReport_" + this.sharedService.getCurrentTimestamp() + ".xlsx";
    
                FileSaver.saveAs(blob,newFileName);
    		this.showInnerLoader = false;
            },
            error => {
                //Please Comment while checkIn
                /* let jsonStatue: any = {"sessionId":"506db794","reason":"Download Failed","status":"SUCCESS","serviceToken":"63524"};
                let blob = new Blob([jsonStatue["_body"]], {
                    type: "application/octet-stream"
                });
    
                let newFileName = this.auditIssueNEInfo.neName + "_AuditPassFailSummaryReport_" + this.sharedService.getCurrentTimestamp() + ".xlsx";
    
                FileSaver.saveAs(blob,newFileName); 
                setTimeout(() => {
                    this.showInnerLoader = false;
                    if(jsonStatue.status == "SUCCESS"){
                    
                    } else {
                    this.displayModel(jsonStatue.reason,"failureIcon");  
                    }
                }, 1000); */
                //Please Comment while checkIn
            });
      }

      downloadAuditReport() {
        this.showInnerLoader = true;
        this.runtestService.downloadAuditSummaryReport(this.sharedService.createServiceToken(), this.postAuditIssues, this.auditIssueNEInfo)
        .subscribe(
            data => {
                let blob = new Blob([data["_body"]], {
                    type: "application/octet-stream"
                });
                let newFileName = this.auditIssueNEInfo.neName + "_AuditSummaryReport_" + this.sharedService.getCurrentTimestamp() + ".xlsx";
    
                FileSaver.saveAs(blob,newFileName);
                this.showInnerLoader = false;
            },
            error => {
                //Please Comment while checkIn
                /* let jsonStatue: any = {"sessionId":"506db794","reason":"Download Failed","status":"SUCCESS","serviceToken":"63524"};
                let blob = new Blob([jsonStatue["_body"]], {
                    type: "application/octet-stream"
                });
    
                let newFileName = this.auditIssueNEInfo.neName + "_AuditSummaryReport_" + this.sharedService.getCurrentTimestamp() + ".xlsx";
    
                FileSaver.saveAs(blob,newFileName); 
                setTimeout(() => {
                    this.showInnerLoader = false;
                    if(jsonStatue.status == "SUCCESS"){
                    
                    } else {
                    this.displayModel(jsonStatue.reason,"failureIcon");  
                    }
                }, 1000); */
                //Please Comment while checkIn
            });
      }

    getNEIDProgramBased() {
        let programName = JSON.parse(sessionStorage.getItem("selectedProgram")).programName;
        let neIdList = [];
        switch (programName) {
            case "VZN-5G-MM":
                neIdList = ["8903500097", "8903500093", "9906500024"];
                break;
            case "VZN-5G-DSS":
                neIdList = ["90022163", "90022239", "90022223"];
                break;
            case "VZN-5G-CBAND":
                neIdList = ["6895513542", "6895513170", "6895523772"];
                break;
            default:
                neIdList = ["61192", "61452", "73461"];
                break;
        }
        return neIdList;
    }

    viewScripts(content) {
        // this.scriptList = this.rfScriptList.split("\n");
        this.rfScriptListModel = this.rfScriptList;
        this.scriptListModalBlock = this.modalService.open(content, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView' });
    }
    updateScriptList() {
        this.rfScriptList = this.rfScriptListModel;
        this.closeModelScriptList();
    }
    closeModelScriptList() {
        this.scriptListModalBlock.close();
    }

    viewSiteReport(siteCompReportModal, key) {

        setTimeout(() => {  
            this.showLoader = true;
            let enbDetails = [];
            this.siteReportNE = {
                "title": this.programName != "VZN-5G-MM" ? key.neName : key.siteName,
                "ids": key.enbId
            };
            
            this.selectedTableRow = {
                "workFlowId": key.id,
                "siteId": key.siteReportId,
            }
            this.selectedReportCIQ = key.ciqName;

            /* if(this.programName != "VZN-5G-MM") {
                let tempObj = {
                    "enbName": key.neName,
                    "enbId": key.enbId
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
            } */
            let tempObj = {
                "enbName": key.neName,
                "enbId": key.enbId
            }
            enbDetails.push(tempObj);

            this.runtestService.viewSiteReportDetailsById(this.sharedService.createServiceToken(), key.siteReportId, key.ciqName, enbDetails)
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
                                    this.siteDetailsValues = jsonStatue.siteDetails ? jsonStatue.siteDetails : {};

                                    if(key.siteReportId || jsonStatue.isPartialReport) {
                                        this.reportData = jsonStatue.reportDetails;
                                        
                                        for(let index = 0; index < this.reportData.carriers.length; index++) {
                                            this.carriersFlag[this.reportData.carriers[index].likeforlike]["likeforlikeCheckBox"] = this.reportData.carriers[index].likeforlikeCheckBox == "Yes" ? true : false;
                                            this.carriersFlag[this.reportData.carriers[index].incremental]["incrementalCheckBox"] = this.reportData.carriers[index].incrementalCheckBox == "Yes" ? true : false;
                                        }

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
                                        for (let index = 0; index < this.troubleshootTimelineList.length; index++) {
                                            this.troubleshootTimelineList[index].siteDate = this.dateFormatHelper(this.troubleshootTimelineList[index].siteDate);
                                            // let siteTime = this.troubleshootTimelineList[index].siteTime;
                                            // this.troubleshootTimelineList[index].siteTimeHr = siteTime.split(":")[0];
                                            // this.troubleshootTimelineList[index].siteTimeMin = siteTime.split(":")[1];
                                        }

                                        this.reportData.reportDate = this.dateFormatHelper(this.reportData.reportDate);
                                        this.reportData.userName = key.userName;
                                        this.timelineIssuesList.issues = this.reportData["categoryDetails"];
                                        // if there no data in issue, create one empty row
                                        if(!this.timelineIssuesList.issues || this.timelineIssuesList.issues.length == 0) {
                                            this.resetReportTimelineIssueForm();
                                        }

                                        setTimeout(() => {
                                            // Set default project based on program
                                            if(!this.reportData["project"]) {
                                                this.reportData["project"] = this.programName == 'VZN-5G-MM' ? "SNAP 5G" : this.programName == 'VZN-5G-DSS' ? "LS3" : this.programName == 'VZN-5G-CBAND' ? "LS6" : this.programName == 'VZN-4G-FSU' ? "FSU Bypass" : "";
                                            }
                                        }, 100);
                                    }
                                    else {
                                        if(this.programName == "VZN-5G-MM") {
                                            this.siteReportNE = {
                                                "title": key.siteName,
                                                "ids": jsonStatue.neIDs
                                            };
                                        }
                                        this.setDefaultSiteData(key.userName);
                                    }
                                
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
                        //Please Comment while checkIn

                        /* setTimeout(() => {
        
                            this.showLoader = false;
                            // let jsonStatue = {"siteInputs":{"projects":["SNAP - 5G","BAU - 5G"],"swRelease":["20-C-0","21-A-0","21-B-0"],"market":["OPW","WBV","TRI","CGC","CTX","NYM","NE","UNY","SAC","NO","PEN FL","HOU TX"],"integType":["Hot Cut","NSB","FOA Support","Test/Lab support"],"status":["Cancelled 48 Hr.","Cancelled 24 Hr.","Failed ","Rolled Back","Pre Migration Successful","Migration Successful","Partial Complete"],"issueCategory":{"Antenna":["ANT-00 Uncommon Failure","ANT-01 VSWR alarms","ANT-02 Fails RSSI test(s)","ANT-03 Fails VSWR test(s)"],"Cancelled":["CAN-00 Uncommon Failure","CAN-01 No Conquest & XCM","CAN-02 Site Access","CAN-03 Resources (TC,FC,DT)","CAN-05 Permits","CAN-06 Missing Material","CAN-07 VZ 48 Hr. Notice","CAN-08 Missing GPS","CAN-09 Time Constraints","CAN-10 Pre Ex. Alarms","CAN-11 Transport"],"Configuration":["CNF-00 Uncommon Failure","CNF-01 RU FW not loaded or issue","CNF-02 Incorrect Parameter","CNF-03 Incorrect Fiber length","CNF-04 Incorrect MME Pool","CNF-05 FW/SW fail to load /activated","CNF-06 Missing Inter-VM routes on vCU","CNF-07 DHCP connection issue","CNF-08 vCU hosting the AU not instantiated by customer","CNF-09 Static routes not configured on USM"],"Call Testing":["CTE-00 Uncommon Failure","CTE-01 No UE For Call Test","CTE-02 Low DL throughput","CTE-03 Failed to Attach to 5g","CTE-04 Failed to Attach to 4g","CTE-05 Inter-RAT NBR not defined between FSM4 and 5G AU/vCU","CTE-06 Nearby Overpowering Site"],"Design":["DES-00 Uncommon Failure","DES-01 Mismatch between Design and Physical Install","DES-02 X2 Links OOS after X2/ENDC script","DES-03 Incorrect CIQ Fail to create GROW Profiles","DES-04 Incorrect IP Information in CIQ and respective RF scripts","DES-05 Alarms - Pre-existing on Equipment/Network","DES-06 RF Scripts missing or not created","DES-07 Incorrect Parameters  - does not follow the engineering rules","DES-08 Missing Parameters","DES-09 CIQ missing in RF FTP Server","DES-10 Designed Vs. Install (example: designed for GEN2 Vs. physical GEN1 installed)"],"Hardware":["HW-00 Uncommon Failure","HW-01 Site/Sector Down -Pre","HW-03 PowerShift - DC power per requirement at RRH end","HW-04 RETs Misconfigured","HW-05 RETs disabled or lost the connectivity","HW-06 GPS unit Faulty","HW-07 Faulty Radio","HW-08 Bad Router card","HW-09 Bad Fiber/SFP/ CPRI fiber wrong length, dirty, broken","HW-10 Bad RFM (RRH, MMU)","HW-11 Bad CDU-30, LCC, LMD","HW-12 Cancelled the site / has not enough time in MW","HW-13 CPRI Power Readings out of range","HW-14 Door/Cabinet/Fan alarm Issue","HW-15 SFP incorrect/Faulty on CDU side","HW-16 SFP incorrect/Faulty on Tower Top side","HW-17 Non Samsung Approved GPS Cable"],"Installation":["INS-00 Uncommon Failure","INS-01 Installation not complete or Incorrect Installation","INS-02 Unable to commission the node","INS-03 Backhaul not ready","INS-04 Chassis backplane issue. Not able to reseat the card","INS-05 No Show","INS-06 GPS Sync Issue","INS-07 No Power","INS-08 Power issue/ Alarm","INS-09 Missing Backhaul Fiber","INS-10 Missing GPS Cable","INS-11 Missing CPRI Fiber","INS-12 Fibers not Labeled","INS-13 Missing Backhaul SFP","INS-14 Missing CPRI SFP","INS-15 Missing Equipment at Site","INS-16 Access - FT couldn’t load ENV","INS-17 RU is not connected","INS-18 Missing Attenuators","INS-19 Fibers not plugged into Raycap/OVP/Junction box/CPRI Panel","INS-20 Fibers connected to incorrect ports on the Radio head","INS-21 Missing Raycap/OVP/Junction box/CPRI Panel"],"Site Readiness":["SRE-00 Uncommon Failure","SRE-01 Site location unknown","SRE-02 Bad AU","SRE-03 GC has no proper Tools for Troubleshooting","SRE-04 SANE Issue","SRE-05 Rescheduled/Cancelled by PM","SRE-06 Site is removed/relocated by customer","SRE-07 Chrome (web-browser) issue","SRE-08 DUO issue/timed out"],"C&I Delivery tool":["SRT-00 Uncommon Failure","SRT-01 Unable to load CIQ to SRCT","SRT-02 Sectors are grown on different software load","SRT-03 Grow profiles not matching with engineering rules","SRT-04 SRCT not reachable","SRT-05 DBs down","SRT-06 Unable to run pnp grow script","SRT-07 SRCT failed to grow sector(s) on USM","SRT-08 Script too big for SCRT to run","SRT-09 SRCT Connection Manager issue","SRT-10 Leading zero is being removed by SRT tool (4G)"],"Transport":["TPT-00 Uncommon Failure","TPT-01 CSR Router Issue / Customer did not complete Router configuration","TPT-02 Incorrect Gateway IP in CIQ","TPT-03 Bad Backhaul Fiber","TPT-04 No connection to vRAN","TPT-05 No connection to RRH","TPT-06 GPS issue on IXR","TPT-07 No light from the IXR ports","TPT-08 Telecom IP alarm","TPT-09 Router port in CIQ is already in use","TPT-10 4G backhaul loosing remote connectivity when 5G backhaul is connected","TPT-11 Incorrect Router port Information on CIQ","TPT-12 Missing Router card","TPT-13 Cells fail to restore"],"S// EMS":["USM-00 Uncommon Failure","USM-01 slow response","USM-02 eNB alarms are not synced","USM-03 GA software not staged","USM-04 vDU not instantiated","USM-05 Unable to access USM","USM-06 Duplicate gNB in multiple ACPFs","USM-07 Could not lock/unlock Cell","USM-08 vRAN not ready","USM-09 GA software issue"]},"timelineList":["Nokia AU Powered Down / ALU locked down and site unmanaged","Samsung AUs Reachable","Samsung AUs C&I Start","Samsung AUs C&I Finish","Samsung AUs Powered down for pole installation","Samsung AUs pingable post Installation","Handoff to RF for Testing","Basic Call Test Complete (ATP & DT Passed )","Total C&I Effort"]},"sessionId":"40b5f7a","serviceToken":"67720","status":"SUCCESS","reason":""};
                            let jsonStatue = this.networkType == '5G' ? (
                                this.programName == 'VZN-5G-MM' ? 
                                    {"reason":"","siteInputs":"{\"status\":[\"Cancelled 48 Hr.\",\"Cancelled 24 Hr.\",\"Failed \",\"Rolled Back\",\"Pre Migration Successful\",\"Migration Successful\",\"Partial Complete\",\"N/A\"],\"projects\":[\"SNAP 5G\",\"SNAP 4G & 5G\",\"SNAP 4G Hot Cut\",\"SNAP 4G Coverage/Capacity\",\"SNAP DSS/FSU\",\"NSB 4G\",\"NSB 5G\",\"LS3\",\"LS6\",\"BAU\",\"FSU Bypass\",\"N/A\"],\"swRelease\":[\"20-A-0\",\"20-C-0\",\"21-A-0\",\"N/A\"],\"region\":[\"OPW\",\"WBV\",\"TRI\",\"CGC\",\"CTX\",\"NYM\",\"NE\",\"UNY\",\"SAC\",\"NO\",\"PEN FL\",\"HOU TX\",\"N/A\"],\"integType\":[\"Hot Cut\",\"NSB\",\"FOA Support\",\"Test/Lab support\",\"N/A\"],\"vendorType\":[\"TWS\",\"Samsung\"],\"issueCategory\":{\"Antenna\":[\"ANT-00 Uncommon Failure\",\"ANT-01 VSWR/RSSI alarms\",\"ANT-02 Fails RSSI test(s)\",\"ANT-03 Fails VSWR test(s)\",\"ANT-04 Incorrect RFDS\",\"ANT-05 Defective Antenna\",\"ANT-06 BAD Hybrid cable\",\"ANT-07 BAD Diplexer\",\"N/A\"],\"Cancelled\":[\"CAN-00 Uncommon Failure\",\"CAN-01 No Conquest & XCM\",\"CAN-02 Site Access\",\"CAN-03 Resources (TC,FC,DT)\",\"CAN-04 Weather\",\"CAN-05 Permits\",\"CAN-06 Missing Material\",\"CAN-07 VZ 48 Hr. Notice\",\"CAN-08 Missing GPS\",\"CAN-09 Time Constraints\",\"CAN-10 Pre Ex. Alarms\",\"CAN-11 Transport\",\"CAN-12 No C&I Resources (SCH. 48 Hr. but CAN in 24Hr.)\",\"CAN-13 Construction Not Complete\",\"CAN-14 No Health Check from Fast Team\",\"CAN-15 NSB ENV Not Loaded\",\"CAN-16 Cancelled the site / has not enough time in MW\",\"CAN-17 Rescheduled/Cancelled by PM\",\"CAN-18 Root Metrics\",\"CAN-19 No N/A Support Vz\",\"CAN-20 Resources No Show\",\"N/A\"],\"Configuration\":[\"CNF-00 Uncommon Failure\",\"CNF-01 FW/SW fail to load /activated\",\"CNF-02 Missing Inter-VM routes on vCU\",\"CNF-03 DHCP connection issue\",\"CNF-04 vCU hosting the AU not instantiated by customer\",\"CNF-05 Static routes not configured on USM\",\"CNF-06 Swapped Sectors\",\"CNF-07 DES-02 X2 Links OOS after X2/ENDC script\",\"N/A\"],\"RET\":[\"RET -01 RET Motor Jam\",\"RET -02 RET Not Scanned using SBT\",\"RET -03 RET Not Scanned using AISG\",\"RET -04 RET Misconfigured (RFDS Correct) *\",\"RET -05 RETs Disabled or lost the connectivity\",\"N/A\"],\"Design\":[\"DES-00 Uncommon Failure\",\"DES-01 Mismatch between Design and Physical Install\",\"DES-02 Mismatch between Design and NE configuration\",\"DES-03 RF Scripts missing or not created\",\"DES-04 Incorrect Parameters  - does not follow the engineering rules\",\"DES-05 Missing Parameters\",\"DES-06 CIQ missing in RF FTP Server\",\"DES-07 Missing site information in OV\",\"DES-08 Missing RFDS\",\"DES-09 Missing RET form\",\"DES-10 Missing Conquest/XCM/CIQ for the site\",\"DES-11 Incorrect site information in OV\",\"DES-12 Incorrect RFDS\",\"DES-13 Incorrect RET form\",\"DES-14 Incorrect CIQ - Port Mapping\",\"DES-15 Incorrect CIQ - Power Settings\",\"DES-16 Incorrect CIQ - BW settings\",\"DES-17 Incorrect CIQ - MME Pool\",\"DES-18 Incorrect CIQ - Other (Not port mapping, power, BW settings or MME pool)\",\"DES-19 Incorrect IP Information in CIQ\",\"DES-20 RF Script Execution Failure\",\"N/A\"],\"Hardware\":[\"HW-00 Uncommon Failure \",\"HW-01 Critical condition of unknown cause (only issues that have been escalated and are under investigation)\",\"HW-02 PowerShift - Faulty or Misconfigured\",\"HW-03 GPS unit Faulty\",\"HW-04 Faulty Radio/ Antenna (RRH/MMU)\",\"HW-05 RRH ATP Power Test Failed\",\"HW-06 Bad AU\",\"HW-07 Bad Router card\",\"HW-08 Bad / Broken Fiber\",\"HW-09 Bad Chassis\",\"HW-10 CPRI Power Readings out of range\",\"HW-11 Door/Cabinet/Fan alarm Issue\",\"HW-12 SFP incorrect/Faulty on CDU30 or FSU\",\"HW-13 SFP incorrect/Faulty on Tower Top side\",\"HW-14 Non Samsung Approved GPS Cable\",\"HW-15 Bad Y Cable (between the FSU and the DU)\",\"HW-16 - UDA Alarms/issues\",\"HW-17 Bad SFP\",\"HW-18 CPRI fiber wrong length, dirty, broken\",\"HW-19 Dirty fiber\",\"HW-20 Bad LCC\",\"HW-21 Bad LMD\",\"HW-22 Bad FSU\",\"N/A\"],\"Installation\":[\"INS-00 Uncommon Failure\",\"INS-01 Faulty rectifier or insufficient rectifiers installed\",\"INS-02 Missing Backhaul Fiber\",\"INS-03 Missing GPS Cable\",\"INS-04 Missing CPRI Fiber\",\"INS-05 Fibers not Labeled\",\"INS-06 Missing Backhaul SFP\",\"INS-07 Missing CPRI SFP\",\"INS-08 Other Missing Materials\",\"INS-09 Missing Tools or Equipment necessary for installation\",\"INS-10 RU installation not complete\",\"INS-11 Missing Attenuators\",\"INS-12 Fibers not plugged into Raycap/OVP/Junction box/CPRI Panel\",\"INS-13 Fibers connected to incorrect ports on the Radio head\",\"INS-14 Missing Raycap/OVP/Junction box/CPRI Panel\",\"N/A\"],\"Site Readiness\":[\"SRE-00 Uncommon Failure\",\"SRE-01 Site Access Delay\",\"SRE-02 Resource Delay (TC,FC,DT)\",\"SRE-03 Resource Delay- C&I\",\"SRE-04 DUO/SANE Issue affecting SRCT or Engineer login\",\"SRE-05 Change in Planning, project logistics or priorities\",\"SRE-06 Non-C&I Related -Human Error\",\"SRE-07 Weather Delay\",\"SRE-08 Installation Delay\",\"SRE-09 Time Constraints (MW, sunset, etc.)\",\"SRE-10 Pre-existing alarms or conditions\",\"SRE-11 Missing Pre-existing health checks\",\"SRE-12 NSB ENV Not Loaded\",\"SRE-13 vRAN not ready \",\"SRE-14 vDU not instantiated\",\"SRE-15 C&I Related -Human Error\",\"N/A\"],\"C&I Delivery tool\":[\"SRT-00 Uncommon Failure \",\"SRT-01 Unable to load CIQ to SRCT\",\"SRT-02 Sectors are grown on different software load\",\"SRT-03 Bug in automation code\",\"SRT-04 SRCT not reachable\",\"SRT-05 DBs down\",\"SRT-06 Unable to run pnp grow script\",\"SRT-07 SRCT update needed\",\"SRT-08 Script too big for SCRT to run\",\"SRT-09 SRCT generated script execution failure\",\"SRT-10 Delayed/hang in progress during execution\",\"N/A\"],\"Transport\":[\"TPT-00 Uncommon Failure\",\"TPT-01 CSR Router Issue / Customer did not complete Router configuration\",\"TPT-02 No connection to vRAN\",\"TPT-03 Incorrect GTP/PTP configuration \",\"TPT-04 OAM Routing issue\",\"TPT-05 Telecom Routing issue\",\"N/A\"],\"S// EMS\":[\"USM-00 Uncommon Failure\",\"USM-01 slow response\",\"USM-02 eNB alarms are not synced\",\"USM-03 GA software not staged \",\"USM-04 Unable to access USM\",\"USM-05 Duplicate gNB in multiple ACPFs\",\"USM-06 Could not lock/unlock Cell\",\"USM-07 GA software issue\",\"USM-08 USM MAX Capacity\",\"USM-09 SAS Registration\",\"N/A\"],\"N/A\":[\"N/A\"]},\"issueTechnologyList\":[\"LTE\",\"mmW\",\"DSS\",\"CBRS\",\"LAA\",\"FSU\",\"N/A\"],\"timelineList\":[\"Nokia AU Powered Down / ALU locked down and site unmanaged\",\"Samsung AUs Reachable\",\"Samsung AUs C&I Start\",\"Samsung AUs C&I Finish\",\"Samsung AUs Powered down for pole installation\",\"Samsung AUs pingable post Installation\",\"Handoff to RF for Testing\",\"Basic Call Test Complete (ATP & DT Passed )\"],\"troubleshootTimelineList\":[\"Troubleshooting Time\",\"Total C&I Effort\"],\"reportStatus\":[\"Completion\",\"Exception\",\"N/A\"],\"typeOfEffort\":[\"1st Touch -1st Attempt \",\"1st Touch - 2nd Attempt \",\"2nd Touch 1st Attempt\",\"2nd Touch 2nd Attempt\",\"Hot Cut - 1st Attempt\",\"Hot Cut - 2nd Attempt\",\"Revisit - TS\",\"Prechecks\",\"Cutover\",\"N/A\"],\"criticalCheckList\":[{\"title\":\"Alarm Free\",\"options\":[\"Yes\",\"No\"],\"mandatory\":[\"No\"]},{\"title\":\"AU/Cells in state requested per market\",\"options\":[\"Yes\",\"No\"],\"mandatory\":[\"No\"]},{\"title\":\"TWAMP Ping test report attached\",\"options\":[\"Yes\",\"No\"],\"mandatory\":[\"No\"]},{\"title\":\"Basic Sanity Test All Pass\",\"options\":[\"Yes\",\"No\"],\"mandatory\":[\"No\"]},{\"title\":\"RF ATP Started\",\"options\":[\"Yes\",\"No\"],\"mandatory\":[\"No\"]},{\"title\":\"Follow-Up Required\",\"options\":[\"Yes\",\"No\"],\"mandatory\":[\"Yes\"]}],\"issueResolvedList\":[\"Yes\",\"No\",\"N/A\"],\"issueAttributeToList\":[\"Verizon\",\"Samsung\",\"Tool\",\"Process\",\"RF Design\",\"C&I\",\"Deployment\",\"N/A\"]}","sessionId":"19b14231","neIDs":"10701580023|10701580024|10701580025","serviceToken":"68421","isPartialReport":false,"siteDetails":{"eNodeBName":"","eNodeBSW":"","fsuSW":"","vDUSW":"","softWareRelease":"21.B.0-0201(AU.21B.P1.01)","fuzeProjId":"16554107","market":"WBV"},"status":"SUCCESS"}
                                    : 
                                    {"ciqName":"VZW_TRI_DSS_CIQ_v2.38_20220126.xlsx","reportDetails":{"vendorType":"Samsung","neId":"8891002001","typeOfEffort":"1st Touch -1st Attempt","eNodeBName":"WhitehouseStation","softWareRelease":null,"project":"LS3","categoryDetails":[{"issueId":null,"issue":"N/A","inIssueEditMode":false,"attribute":"N/A","category":"N/A","remarks":"","resolved":"N/A"}],"neName":"8891002001_5GDU_WhitehouseStation-WLMT","ovTicketNum":"43444434","troubleshootTimelineDetails":[{"siteTimeHr":"00","siteDate":"07-25-2022","siteTimeMin":"00","siteTime":"","timeLine":"Troubleshooting Time","remarks":""},{"siteTimeHr":"00","siteDate":"07-25-2022","siteTimeMin":"00","siteTime":"","timeLine":"Total C&I Effort","remarks":""}],"currCBANDIntegStatus":"","dssOpsATP":"Cancelled 48 Hr.","integrationType":"","reportDate":"07-28-2022","finalCBANDIntegStatus":"","lteCBRSCommComp":"","fsuIntegBypass":"","isCancellationReport":"Yes","siteReportStatus":"Completion","lteCommComp":"","fuzeProjId":"16309873","mmOpsATP":"","lteLAACommComp":"","postAuditIssues":[],"resAuditIssueCheck":"Yes","checkDetails":[{"checkPerformed":"","options":["Yes","No"],"title":"Alarm Free","mandatory":["No"],"remarks":""},{"checkPerformed":"","options":["Yes","No"],"title":"Pre Checks All Pass","mandatory":["No"],"remarks":""},{"checkPerformed":"","options":["Yes","No"],"title":"Post Checks All Pass","mandatory":["No"],"remarks":""},{"checkPerformed":"","options":["Yes","No"],"title":"Cross Anchor","mandatory":["No"],"remarks":""},{"checkPerformed":"","options":["Yes","No"],"title":"Concurent FSU Integration","mandatory":["No"],"remarks":""},{"checkPerformed":"","options":["Yes","No"],"title":"vDU Instantiation Correct","mandatory":["No"],"remarks":""},{"checkPerformed":"","options":["Yes","No"],"title":"Follow-Up Required","mandatory":["Yes"],"remarks":""}],"lteOpsATP":"","fsuIntegMultiplex":"","eNodeBSW":"21.B.0-0104(ENB.21B.P3.01)","carriers":[],"dssCommComp":"Cancelled 48 Hr.","userName":"superadmin","market":"OPW","mmCommComp":"","tcReleased":"Yes","vDUSW":"21.A.0-0103(VDU.21A.P3.00) ","lteLAAOpsATP":"","finalIntegStatus":"Cancelled 48 Hr.","timeLineDetails":[{"siteDate":"07-25-2022","siteTime":"","timeLine":"C&I Prechecks Start","remarks":""},{"siteDate":"07-25-2022","siteTime":"","timeLine":"C&I Integration Start","remarks":""},{"siteDate":"07-25-2022","siteTime":"","timeLine":"Audits Pass","remarks":""},{"siteDate":"07-25-2022","siteTime":"","timeLine":"C&I Integration Complete","remarks":""}],"lteCBRSOpsAtp":"","remarks":"","fsuSW":"21.B.0-0201(FSU.21B.P1.01)"},"sessionId":"7032c5b6","customerName":"","siteInputs":"{\"integType\":[\"Hot Cut\",\"Coverage\",\"Capacity\",\"NSB\",\"Carrier Add\",\"FSU Install\",\"DSS Cutover\",\"Troubleshooting\",\"N/A\"],\"issueAttributeToList\":[\"Verizon\",\"Samsung\",\"Tool\",\"Process\",\"RF Design\",\"C&I\",\"Deployment\",\"N/A\"],\"vendorType\":[\"TWS\",\"Samsung\"],\"issueCategory\":{\"Antenna\":[\"ANT-00 Uncommon Failure\",\"ANT-01 VSWR/RSSI alarms\",\"ANT-02 Fails RSSI test(s)\",\"ANT-03 Fails VSWR test(s)\",\"ANT-04 Incorrect RFDS\",\"ANT-05 Defective Antenna\",\"ANT-06 BAD Hybrid cable\",\"ANT-07 BAD Diplexer\",\"N/A\"],\"Cancelled\":[\"CAN-00 Uncommon Failure\",\"CAN-01 No Conquest & XCM\",\"CAN-02 Site Access\",\"CAN-03 Resources (TC,FC,DT)\",\"CAN-04 Weather\",\"CAN-05 Permits\",\"CAN-06 Missing Material\",\"CAN-07 VZ 48 Hr. Notice\",\"CAN-08 Missing GPS\",\"CAN-09 Time Constraints\",\"CAN-10 Pre Ex. Alarms\",\"CAN-11 Transport\",\"CAN-12 No C&I Resources (SCH. 48 Hr. but CAN in 24Hr.)\",\"CAN-13 Construction Not Complete\",\"CAN-14 No Health Check from Fast Team\",\"CAN-15 NSB ENV Not Loaded\",\"CAN-16 Cancelled the site / has not enough time in MW\",\"CAN-17 Rescheduled/Cancelled by PM\",\"CAN-18 Root Metrics\",\"CAN-19 No N/A Support Vz\",\"CAN-20 Resources No Show\",\"N/A\"],\"Configuration\":[\"CNF-00 Uncommon Failure\",\"CNF-01 FW/SW fail to load /activated\",\"CNF-02 Missing Inter-VM routes on vCU\",\"CNF-03 DHCP connection issue\",\"CNF-04 vCU hosting the AU not instantiated by customer\",\"CNF-05 Static routes not configured on USM\",\"CNF-06 Swapped Sectors\",\"CNF-07 DES-02 X2 Links OOS after X2/ENDC script\",\"N/A\"],\"RET\":[\"RET -01 RET Motor Jam\",\"RET -02 RET Not Scanned using SBT\",\"RET -03 RET Not Scanned using AISG\",\"RET -04 RET Misconfigured (RFDS Correct) *\",\"RET -05 RETs Disabled or lost the connectivity\",\"N/A\"],\"Design\":[\"DES-00 Uncommon Failure\",\"DES-01 Mismatch between Design and Physical Install\",\"DES-02 Mismatch between Design and NE configuration\",\"DES-03 RF Scripts missing or not created\",\"DES-04 Incorrect Parameters  - does not follow the engineering rules\",\"DES-05 Missing Parameters\",\"DES-06 CIQ missing in RF FTP Server\",\"DES-07 Missing site information in OV\",\"DES-08 Missing RFDS\",\"DES-09 Missing RET form\",\"DES-10 Missing Conquest/XCM/CIQ for the site\",\"DES-11 Incorrect site information in OV\",\"DES-12 Incorrect RFDS\",\"DES-13 Incorrect RET form\",\"DES-14 Incorrect CIQ - Port Mapping\",\"DES-15 Incorrect CIQ - Power Settings\",\"DES-16 Incorrect CIQ - BW settings\",\"DES-17 Incorrect CIQ - MME Pool\",\"DES-18 Incorrect CIQ - Other (Not port mapping, power, BW settings or MME pool)\",\"DES-19 Incorrect IP Information in CIQ\",\"DES-20 RF Script Execution Failure\",\"N/A\"],\"Hardware\":[\"HW-00 Uncommon Failure \",\"HW-01 Critical condition of unknown cause (only issues that have been escalated and are under investigation)\",\"HW-02 PowerShift - Faulty or Misconfigured\",\"HW-03 GPS unit Faulty\",\"HW-04 Faulty Radio/ Antenna (RRH/MMU)\",\"HW-05 RRH ATP Power Test Failed\",\"HW-06 Bad AU\",\"HW-07 Bad Router card\",\"HW-08 Bad / Broken Fiber\",\"HW-09 Bad Chassis\",\"HW-10 CPRI Power Readings out of range\",\"HW-11 Door/Cabinet/Fan alarm Issue\",\"HW-12 SFP incorrect/Faulty on CDU30 or FSU\",\"HW-13 SFP incorrect/Faulty on Tower Top side\",\"HW-14 Non Samsung Approved GPS Cable\",\"HW-15 Bad Y Cable (between the FSU and the DU)\",\"HW-16 - UDA Alarms/issues\",\"HW-17 Bad SFP\",\"HW-18 CPRI fiber wrong length, dirty, broken\",\"HW-19 Dirty fiber\",\"HW-20 Bad LCC\",\"HW-21 Bad LMD\",\"HW-22 Bad FSU\",\"N/A\"],\"Installation\":[\"INS-00 Uncommon Failure\",\"INS-01 Faulty rectifier or insufficient rectifiers installed\",\"INS-02 Missing Backhaul Fiber\",\"INS-03 Missing GPS Cable\",\"INS-04 Missing CPRI Fiber\",\"INS-05 Fibers not Labeled\",\"INS-06 Missing Backhaul SFP\",\"INS-07 Missing CPRI SFP\",\"INS-08 Other Missing Materials\",\"INS-09 Missing Tools or Equipment necessary for installation\",\"INS-10 RU installation not complete\",\"INS-11 Missing Attenuators\",\"INS-12 Fibers not plugged into Raycap/OVP/Junction box/CPRI Panel\",\"INS-13 Fibers connected to incorrect ports on the Radio head\",\"INS-14 Missing Raycap/OVP/Junction box/CPRI Panel\",\"N/A\"],\"Site Readiness\":[\"SRE-00 Uncommon Failure\",\"SRE-01 Site Access Delay\",\"SRE-02 Resource Delay (TC,FC,DT)\",\"SRE-03 Resource Delay- C&I\",\"SRE-04 DUO/SANE Issue affecting SRCT or Engineer login\",\"SRE-05 Change in Planning, project logistics or priorities\",\"SRE-06 Non-C&I Related -Human Error\",\"SRE-07 Weather Delay\",\"SRE-08 Installation Delay\",\"SRE-09 Time Constraints (MW, sunset, etc.)\",\"SRE-10 Pre-existing alarms or conditions\",\"SRE-11 Missing Pre-existing health checks\",\"SRE-12 NSB ENV Not Loaded\",\"SRE-13 vRAN not ready \",\"SRE-14 vDU not instantiated\",\"SRE-15 C&I Related -Human Error\",\"N/A\"],\"C&I Delivery tool\":[\"SRT-00 Uncommon Failure \",\"SRT-01 Unable to load CIQ to SRCT\",\"SRT-02 Sectors are grown on different software load\",\"SRT-03 Bug in automation code\",\"SRT-04 SRCT not reachable\",\"SRT-05 DBs down\",\"SRT-06 Unable to run pnp grow script\",\"SRT-07 SRCT update needed\",\"SRT-08 Script too big for SCRT to run\",\"SRT-09 SRCT generated script execution failure\",\"SRT-10 Delayed/hang in progress during execution\",\"N/A\"],\"Transport\":[\"TPT-00 Uncommon Failure\",\"TPT-01 CSR Router Issue / Customer did not complete Router configuration\",\"TPT-02 No connection to vRAN\",\"TPT-03 Incorrect GTP/PTP configuration \",\"TPT-04 OAM Routing issue\",\"TPT-05 Telecom Routing issue\",\"N/A\"],\"S// EMS\":[\"USM-00 Uncommon Failure\",\"USM-01 slow response\",\"USM-02 eNB alarms are not synced\",\"USM-03 GA software not staged \",\"USM-04 Unable to access USM\",\"USM-05 Duplicate gNB in multiple ACPFs\",\"USM-06 Could not lock/unlock Cell\",\"USM-07 GA software issue\",\"USM-08 USM MAX Capacity\",\"USM-09 SAS Registration\",\"N/A\"],\"N/A\":[\"N/A\"]},\"issueResolvedList\":[\"Yes\",\"No\",\"N/A\"],\"projects\":[\"SNAP 5G\",\"SNAP 4G & 5G\",\"SNAP 4G Hot Cut\",\"SNAP 4G Coverage/Capacity\",\"SNAP DSS/FSU\",\"NSB 4G\",\"NSB 5G\",\"LS3\",\"LS6\",\"BAU\",\"FSU Bypass\",\"N/A\"],\"region\":[\"OPW\",\"WBV\",\"TRI\",\"CGC\",\"CTX\",\"NYM\",\"NE\",\"UNY\",\"SAC\",\"NO\",\"PEN FL\",\"HOU TX\",\"N/A\"],\"reportStatus\":[\"Completion\",\"Exception\",\"N/A\"],\"status\":[\"Cancelled 48 Hr.\",\"Cancelled 24 Hr.\",\"Failed \",\"Rolled Back\",\"Pre Migration Successful\",\"Partial Complete\",\"Migration Successful\",\"N/A\"],\"swRelease\":[\"20.C.0\",\"21.A.0\",\"21.B.0\",\"N/A\"],\"timelineList\":[\"C&I Prechecks Start\",\"C&I Integration Start\",\"Audits Pass\",\"C&I Integration Complete\"],\"troubleshootTimelineList\":[\"Troubleshooting Time\",\"Total C&I Effort\"],\"typeOfEffort\":[\"1st Touch -1st Attempt \",\"1st Touch - 2nd Attempt \",\"2nd Touch 1st Attempt\",\"2nd Touch 2nd Attempt\",\"Hot Cut - 1st Attempt\",\"Hot Cut - 2nd Attempt\",\"Revisit - TS\",\"Prechecks\",\"Cutover\",\"N/A\"],\"criticalCheckList\":[{\"title\":\"Alarm Free\",\"options\":[\"Yes\",\"No\"],\"mandatory\":[\"No\"]},{\"title\":\"Pre Checks All Pass\",\"options\":[\"Yes\",\"No\"],\"mandatory\":[\"No\"]},{\"title\":\"Post Checks All Pass\",\"options\":[\"Yes\",\"No\"],\"mandatory\":[\"No\"]},{\"title\":\"Cross Anchor\",\"options\":[\"Yes\",\"No\"],\"mandatory\":[\"No\"]},{\"title\":\"Concurent FSU Integration\",\"options\":[\"Yes\",\"No\"],\"mandatory\":[\"No\"]},{\"title\":\"vDU Instantiation Correct\",\"options\":[\"Yes\",\"No\"],\"mandatory\":[\"No\"]},{\"title\":\"Follow-Up Required\",\"options\":[\"Yes\",\"No\"],\"mandatory\":[\"Yes\"]}],\"vDUSW\":[\"21A\",\"21BP3\",\"21BP3.01\",\"21BP4\",\"N/A\"],\"eNodeBSW\":[\"21AP3\",\"21BP3\",\"N/A\"],\"fsuSW\":[\"21AP2\",\"21BP1\",\"N/A\"]}","workFlowId":1521,"programName":"VZN-5G-DSS","customerId":1,"siteId":null,"serviceToken":"91428","isPartialReport":true,"siteDetails":{"eNodeBName":"WhitehouseStation","eNodeBSW":"21.B.0-0104(ENB.21B.P3.01)","fsuSW":"21.B.0-0201(FSU.21B.P1.01)","vDUSW":"21.A.0-0103(VDU.21A.P3.00) ","softWareRelease":null,"fuzeProjId":"16309873","market":"TRI"},"programId":42,"postAuditIssues":[],"status":"SUCCESS"}
                            ) : 
                            key.siteReportId ? {"isPartialReport":false,"ciqName":"NYM-VZ_CIQ_Ver_0.0.111_20220518.xlsx","reportDetails":{"preChkPass":"No","neId":"85079","typeOfEffort":"2nd Touch 2nd Attempt","project":"SNAP 4G Hot Cut","cellAdminStateIsPerCiq":"Yes","vDUInstCorrect":"No","neName":"085079_STEWART_AVE","ovTicketNum":"1","troubleshootTimelineDetails":[{"siteTimeHr":"01","siteDate":"05-27-2022","siteTimeMin":"00","siteTime":"01:00","timeLine":"Troubleshooting Time","remarks":""},{"siteTimeHr":"02","siteDate":"05-27-2022","siteTimeMin":"00","siteTime":"02:00","timeLine":"Total C&I Effort","remarks":""}],"alarmFree":"Yes","vDUDay2Complete":"No","currCBANDIntegStatus":"","dssOpsATP":"","basicSanityTestAllPass":"No","reportDate":"05-27-2022","finalCBANDIntegStatus":"","crossAnchor":"No","fsuIntegBypass":"","lteCommComp":"Failed","fuzeProjId":"145570","lteLAACommComp":"Cancelled 48 Hr.","preExistingAlarm":"Yes","postAuditIssues":[],"checkRSSI":"Yes","resAuditIssueCheck":"Yes","lteOpsATP":"Cancelled 24 Hr.","checkVSWR":"No","fsuIntegMultiplex":"","eNodeBSW":"","carriers":[{"likeforlike":"700","likeforlikeCheckBox":"Yes","incremental":"700","incrementalCheckBox":"Yes"},{"likeforlike":"850","likeforlikeCheckBox":"No","incremental":"850","incrementalCheckBox":"No"},{"likeforlike":"AWS","likeforlikeCheckBox":"Yes","incremental":"AWS","incrementalCheckBox":"Yes"},{"likeforlike":"PCS","likeforlikeCheckBox":"Yes","incremental":"PCS","incrementalCheckBox":"Yes"},{"likeforlike":"AWS3","likeforlikeCheckBox":"No","incremental":"AWS3","incrementalCheckBox":"No"},{"likeforlike":"CBRS","likeforlikeCheckBox":"No","incremental":"CBRS","incrementalCheckBox":"No"},{"likeforlike":"LAA","likeforlikeCheckBox":"No","incremental":"LAA","incrementalCheckBox":"No"}],"market":"WBV","mmCommComp":"","vDUSW":"","finalIntegStatus":"Cancelled 48 Hr.","timeLineDetails":[{"siteDate":"05-27-2022","siteTime":"NA","timeLine":"Nokia ENB unmanaged","remarks":""},{"siteDate":"05-27-2022","siteTime":"NA","timeLine":"Samsung CDU-30 pingable","remarks":""},{"siteDate":"05-27-2022","siteTime":"NA","timeLine":"Samsung CDU-30 C&I Start","remarks":""},{"siteDate":"05-27-2022","siteTime":"NA","timeLine":"Samsung CDU-30C&I End","remarks":""},{"siteDate":"05-27-2022","siteTime":"NA","timeLine":"Waiting on tower crew to complete work on radios","remarks":""},{"siteDate":"05-27-2022","siteTime":"NA","timeLine":"Detected all the radios,fw upgrade, OPS ATP","remarks":""},{"siteDate":"05-27-2022","siteTime":"NA","timeLine":"Unlocked sectors","remarks":""},{"siteDate":"05-27-2022","siteTime":"NA","timeLine":"Basic Call Test Complete","remarks":""}],"checkDetails":[{"checkPerformed":"No","title":"Alarm Free","remarks":"This is my remarks"},{"checkPerformed":"Yes","title":"RSSI Alarm Checked","remarks":"No way"}],"retRecived":"No","eNodeBName":"","atpAllPass":"No","softWareRelease":"21.D.0-0300(ENB.21D.P2.00)","followUpRequired":"No","categoryDetails":[{"issueId":null,"issue":"N/A","inIssueEditMode":false,"attribute":"N/A","category":"N/A","remarks":"","resolved":"N/A"}],"checkRSSIImbl":"No","checkSFP":"No","integrationType":"Coverage","lteCBRSCommComp":"Rolled Back","siteReportStatus":"Completion","mmOpsATP":"","preMigrationHealth":"Yes","checkFIBER":"No","twampPingTestRepAttached":"No","dssCommComp":"","userName":"superadmin","tcReleased":"No","fsuBypass":"No","rfAtpStarted":"No","lteLAAOpsATP":"Partial Complete","concFSUInteg":"No","allRetsScanned":"No","postChkPass":"No","lteCBRSOpsAtp":"Failed","onsiteConfirm":"No","allRetsLabeled":"No","remarks":"","auCellsReqperMarket":"No","fsuSW":""},"sessionId":"7afcbafa","customerName":"","siteInputs":"{\"status\":[\"Cancelled 48 Hr.\",\"Cancelled 24 Hr.\",\"Failed \",\"Rolled Back\",\"Pre Migration Successful\",\"Partial Complete\",\"Migration Successful\",\"N/A\"],\"projects\":[\"SNAP 5G\",\"SNAP 4G & 5G\",\"SNAP 4G Hot Cut\",\"SNAP 4G Coverage/Capacity\",\"SNAP DSS/FSU\",\"NSB 4G\",\"NSB 5G\",\"LS3\",\"LS6\",\"BAU\",\"FSU Bypass\",\"N/A\"],\"swRelease\":[\"20.C.0\",\"21.A.0\",\"21.B.0\",\"N/A\"],\"region\":[\"OPW\",\"WBV\",\"TRI\",\"CGC\",\"CTX\",\"NYM\",\"NE\",\"UNY\",\"SAC\",\"NO\",\"PEN FL\",\"HOU TX\",\"N/A\"],\"integType\":[\"Hot Cut\",\"Coverage\",\"Capacity\",\"NSB\",\"Carrier Add\",\"FSU Install\",\"DSS Cutover\",\"Troubleshooting\",\"N/A\"],\"issueCategory\":{\"N/A\":[\"N/A\"],\"Antenna\":[\"ANT-00 Uncommon Failure\",\"ANT-01 VSWR/RSSI alarms\",\"ANT-02 Fails RSSI test(s)\",\"ANT-03 Fails VSWR test(s)\",\"ANT-04 Incorrect RFDS\",\"ANT-05 Defective Antenna\",\"ANT-06 BAD Hybrid cable\",\"ANT-07 BAD Diplexer\",\"N/A\"],\"Cancelled\":[\"CAN-00 Uncommon Failure\",\"CAN-01 No Conquest & XCM\",\"CAN-02 Site Access\",\"CAN-03 Resources (TC,FC,DT)\",\"CAN-04 Weather\",\"CAN-05 Permits\",\"CAN-06 Missing Material\",\"CAN-07 VZ 48 Hr. Notice\",\"CAN-08 Missing GPS\",\"CAN-09 Time Constraints\",\"CAN-10 Pre Ex. Alarms\",\"CAN-11 Transport\",\"CAN-12 No C&I Resources (SCH. 48 Hr. but CAN in 24Hr.)\",\"CAN-13 Construction Not Complete\",\"CAN-14 No Health Check from Fast Team\",\"CAN-15 NSB ENV Not Loaded\",\"CAN-16 Cancelled the site / has not enough time in MW\",\"CAN-17 Rescheduled/Cancelled by PM\",\"CAN-18 Root Metrics\",\"CAN-19 No N/A Support Vz\",\"CAN-20 Resources No Show\",\"N/A\"],\"Configuration\":[\"CNF-00 Uncommon Failure\",\"CNF-01 FW/SW fail to load /activated\",\"CNF-02 Missing Inter-VM routes on vCU\",\"CNF-03 DHCP connection issue\",\"CNF-04 vCU hosting the AU not instantiated by customer\",\"CNF-05 Static routes not configured on USM\",\"CNF-06 Swapped Sectors\",\"CNF-07 DES-02 X2 Links OOS after X2/ENDC script\",\"N/A\"],\"RET\":[\"RET -01 RET Motor Jam\",\"RET -02 RET Not Scanned using SBT\",\"RET -03 RET Not Scanned using AISG\",\"RET -04 RET Misconfigured (RFDS Correct) *\",\"RET -05 RETs Disabled or lost the connectivity\",\"N/A\"],\"Design\":[\"DES-00 Uncommon Failure\",\"DES-01 Mismatch between Design and Physical Install\",\"DES-02 Mismatch between Design and NE configuration\",\"DES-03 RF Scripts missing or not created\",\"DES-04 Incorrect Parameters  - does not follow the engineering rules\",\"DES-05 Missing Parameters\",\"DES-06 CIQ missing in RF FTP Server\",\"DES-07 Missing site information in OV\",\"DES-08 Missing RFDS\",\"DES-09 Missing RET form\",\"DES-10 Missing Conquest/XCM/CIQ for the site\",\"DES-11 Incorrect site information in OV\",\"DES-12 Incorrect RFDS\",\"DES-13 Incorrect RET form\",\"DES-14 Incorrect CIQ - Port Mapping\",\"DES-15 Incorrect CIQ - Power Settings\",\"DES-16 Incorrect CIQ - BW settings\",\"DES-17 Incorrect CIQ - MME Pool\",\"DES-18 Incorrect CIQ - Other (Not port mapping, power, BW settings or MME pool)\",\"DES-19 Incorrect IP Information in CIQ\",\"DES-20 RF Script Execution Failure\",\"N/A\"],\"Hardware\":[\"HW-00 Uncommon Failure \",\"HW-01 Critical condition of unknown cause (only issues that have been escalated and are under investigation)\",\"HW-02 PowerShift - Faulty or Misconfigured\",\"HW-03 GPS unit Faulty\",\"HW-04 Faulty Radio/ Antenna (RRH/MMU)\",\"HW-05 RRH ATP Power Test Failed\",\"HW-06 Bad AU\",\"HW-07 Bad Router card\",\"HW-08 Bad / Broken Fiber\",\"HW-09 Bad Chassis\",\"HW-10 CPRI Power Readings out of range\",\"HW-11 Door/Cabinet/Fan alarm Issue\",\"HW-12 SFP incorrect/Faulty on CDU30 or FSU\",\"HW-13 SFP incorrect/Faulty on Tower Top side\",\"HW-14 Non Samsung Approved GPS Cable\",\"HW-15 Bad Y Cable (between the FSU and the DU)\",\"HW-16 - UDA Alarms/issues\",\"HW-17 Bad SFP\",\"HW-18 CPRI fiber wrong length, dirty, broken\",\"HW-19 Dirty fiber\",\"HW-20 Bad LCC\",\"HW-21 Bad LMD\",\"HW-22 Bad FSU\",\"N/A\"],\"Installation\":[\"INS-00 Uncommon Failure\",\"INS-01 Faulty rectifier or insufficient rectifiers installed\",\"INS-02 Missing Backhaul Fiber\",\"INS-03 Missing GPS Cable\",\"INS-04 Missing CPRI Fiber\",\"INS-05 Fibers not Labeled\",\"INS-06 Missing Backhaul SFP\",\"INS-07 Missing CPRI SFP\",\"INS-08 Other Missing Materials\",\"INS-09 Missing Tools or Equipment necessary for installation\",\"INS-10 RU installation not complete\",\"INS-11 Missing Attenuators\",\"INS-12 Fibers not plugged into Raycap/OVP/Junction box/CPRI Panel\",\"INS-13 Fibers connected to incorrect ports on the Radio head\",\"INS-14 Missing Raycap/OVP/Junction box/CPRI Panel\",\"N/A\"],\"Site Readiness\":[\"SRE-00 Uncommon Failure\",\"SRE-01 Site Access Delay\",\"SRE-02 Resource Delay (TC,FC,DT)\",\"SRE-03 Resource Delay- C&I\",\"SRE-04 DUO/SANE Issue affecting SRCT or Engineer login\",\"SRE-05 Change in Planning, project logistics or priorities\",\"SRE-06 Non-C&I Related -Human Error\",\"SRE-07 Weather Delay\",\"SRE-08 Installation Delay\",\"SRE-09 Time Constraints (MW, sunset, etc.)\",\"SRE-10 Pre-existing alarms or conditions\",\"SRE-11 Missing Pre-existing health checks\",\"SRE-12 NSB ENV Not Loaded\",\"SRE-13 vRAN not ready \",\"SRE-14 vDU not instantiated\",\"SRE-15 C&I Related -Human Error\",\"N/A\"],\"C&I Delivery tool\":[\"SRT-00 Uncommon Failure \",\"SRT-01 Unable to load CIQ to SRCT\",\"SRT-02 Sectors are grown on different software load\",\"SRT-03 Bug in automation code\",\"SRT-04 SRCT not reachable\",\"SRT-05 DBs down\",\"SRT-06 Unable to run pnp grow script\",\"SRT-07 SRCT update needed\",\"SRT-08 Script too big for SCRT to run\",\"SRT-09 SRCT generated script execution failure\",\"SRT-10 Delayed/hang in progress during execution\",\"N/A\"],\"Transport\":[\"TPT-00 Uncommon Failure\",\"TPT-01 CSR Router Issue / Customer did not complete Router configuration\",\"TPT-02 No connection to vRAN\",\"TPT-03 Incorrect GTP/PTP configuration \",\"TPT-04 OAM Routing issue\",\"TPT-05 Telecom Routing issue\",\"N/A\"],\"S// EMS\":[\"USM-00 Uncommon Failure\",\"USM-01 slow response\",\"USM-02 eNB alarms are not synced\",\"USM-03 GA software not staged \",\"USM-04 Unable to access USM\",\"USM-05 Duplicate gNB in multiple ACPFs\",\"USM-06 Could not lock/unlock Cell\",\"USM-07 GA software issue\",\"USM-08 USM MAX Capacity\",\"USM-09 SAS Registration\",\"N/A\"]},\"issueAttributeToList\":[\"Verizon\",\"Samsung\",\"Tool\",\"Process\",\"RF Design\",\"C&I\",\"Deployment\",\"N/A\"],\"issueResolvedList\":[\"Yes\",\"No\",\"N/A\"],\"timelineList\":[\"Nokia ENB unmanaged\",\"Samsung CDU-30 pingable\",\"Samsung CDU-30 C&I Start\",\"Samsung CDU-30C&I End\",\"Waiting on tower crew to complete work on radios\",\"Detected all the radios,fw upgrade, OPS ATP\",\"Unlocked sectors\",\"Basic Call Test Complete\"],\"troubleshootTimelineList\":[\"Troubleshooting Time\",\"Total C&I Effort\"],\"criticalCheckList\":[\"Alarm Free\",\"Cells Admin state is per CIQ\",\"RSSI Alarm Checked\",\"RSSI Imbalance Checked\",\"VSWR Checked\",\"Fiber Checked\",\"SFP Checked\",\"ATP all pass\",\"All RETs scanned\",\"FSU Bypass\",\"All RETs labeled (Accounting for every carrier)\",\"Follow-up Required\"],\"reportStatus\":[\"Completion\",\"Exception\",\"N/A\"],\"typeOfEffort\":[\"1st Touch -1st Attempt \",\"1st Touch - 2nd Attempt \",\"2nd Touch 1st Attempt\",\"2nd Touch 2nd Attempt\",\"Hot Cut - 1st Attempt\",\"Hot Cut - 2nd Attempt\",\"Revisit - TS\",\"Prechecks\",\"Cutover\",\"N/A\"]}","workFlowId":2479,"programName":"VZN-4G-USM-LIVE","customerId":1,"siteId":null,"serviceToken":"53929","programId":34,"postAuditIssues":[],"status":"SUCCESS"}
                            : 
                            {"isPartialReport":true,"ciqName":"Tri_State_Test_Sites_CIQ.xlsx","reportDetails":{"neId":"88123","ciCompletionStatus":"","atpAllPass":"No","softWareRelease":"","fuzeProjId":"12345","followUpRequired":"No","project":"","categoryDetails":[{"issueId":null,"issue":"","inIssueEditMode":true,"attribute":"","category":"","remarks":"","resolved":""}],"cellAdminStateIsPerCiq":"No","checkRSSI":"No","checkRSSIImbl":"Yes","checkVSWR":"Yes","checkFIBER":"Yes","checkSFP":"Yes","preMigrationHealth":false,"preExistingAlarm":false,"retRecived":false,"onsiteConfirm":false,"neName":"088123_Branchburg4","alarmFree":"Yes","basicSanityTestAllPass":"No","integrationType":"","reportDate":"09-23-2021","finalIntegStatus":"Failed","siteReportStatus":"Completion","postAuditIssues":[],"resAuditIssueCheck":"No","twampPingTestRepAttached":"No","carriers":[{"likeforlike":"700","likeforlikeCheckBox":"No","incremental":"700","incrementalCheckBox":"No"},{"likeforlike":"850","likeforlikeCheckBox":"Yes","incremental":"850","incrementalCheckBox":"No"},{"likeforlike":"AWS","likeforlikeCheckBox":"No","incremental":"AWS","incrementalCheckBox":"Yes"},{"likeforlike":"PCS","likeforlikeCheckBox":"No","incremental":"PCS","incrementalCheckBox":"No"},{"likeforlike":"AWS3","likeforlikeCheckBox":"No","incremental":"AWS3","incrementalCheckBox":"No"},{"likeforlike":"CBRS","likeforlikeCheckBox":"Yes","incremental":"CBRS","incrementalCheckBox":"No"},{"likeforlike":"LAA","likeforlikeCheckBox":"No","incremental":"LAA","incrementalCheckBox":"No"}],"market":"NYM","fsuBypass":"Yes","rfAtpStarted":"No","allRetsScanned":"No","timeLineDetails":[{"siteDate":"09-23-2021","siteTime":"","timeLine":"Nokia ENB unmanaged","remarks":""},{"siteDate":"09-23-2021","siteTime":"","timeLine":"Samsung CDU-30 pingable","remarks":""},{"siteDate":"09-23-2021","siteTime":"","timeLine":"Samsung CDU-30 C&I Start","remarks":""},{"siteDate":"09-23-2021","siteTime":"","timeLine":"Samsung CDU-30 C&I End","remarks":""},{"siteDate":"09-23-2021","siteTime":"","timeLine":"Waiting on tower crew to complete work on radios","remarks":""},{"siteDate":"09-23-2021","siteTime":"","timeLine":"Detected all the radios, fw upgrade, OPS ATP","remarks":""},{"siteDate":"09-23-2021","siteTime":"","timeLine":"Unlocked sectors","remarks":""},{"siteDate":"09-23-2021","siteTime":"","timeLine":"Basic Call Test Complete ","remarks":""}],"troubleshootTimelineDetails":[{"timeLine":"Troubleshooting Time","siteDate":"03-10-2022","siteTime":"23:03","remarks":""},{"timeLine":"Total C&I Effort","siteDate":"03-10-2022","siteTime":"01:59","remarks":""}],"CriticalCheckDetails":[{"checkPerformed":"No","title":"Alarm Free","remarks":"This is my remarks"},{"checkPerformed":"Yes","title":"RSSI Alarm Checked","remarks":"No way"}],"allRetsLabeled":"No","remarks":"Hello All","auCellsReqperMarket":"No","samsungUpStatus":"Migration Successful","cbrsStatus":"Rolled Back","laaStatus":"Failed","typeOfEffort":"Revisit - TS","eNodeBSW":"21BP3","fsuSW":"21AP2","vDUSW":"21BP3.01","eNodeBName":"Hello Shahid"},"sessionId":"6e1b7aa3","customerName":"","siteInputs":"{\"status\":[\"Cancelled 48 Hr.\",\"Cancelled 24 Hr.\",\"Failed \",\"Rolled Back\",\"Pre Migration Successful\",\"Migration Successful\",\"Partial Complete\"],\"projects\":[\"SNAP 5G\",\n    \"SNAP 4G & 5G\",\"SNAP 4G Hot Cut\",\"SNAP 4G Coverage/Capacity\",\"SNAP DSS/FSU\",\"NSB 4G\",\"NSB 5G\",\"LS3\",\"LS6\",\"BAU\",\"FSU Bypass\"],\"swRelease\":[\"20.C.0\",\"21.A.0\",\"21.B.0\"],\"region\":[\"OPW\",\"WBV\",\"TRI\",\"CGC\",\"CTX\",\"NYM\",\"NE\",\"UNY\",\"SAC\",\"NO\",\"PEN FL\",\"HOU TX\"],\"integType\":[\"Hot Cut\",\"Coverage\",\"Capacity\",\"NSB\",\"Carrier Add\",\"FSU Install\",\"DSS Cutover\",\"LS6 ENDC\",\"LS3 ENDC\"],\"issueCategory\":{\"Antenna\":[\"ANT-00 Uncommon Failure\",\"ANT-01 VSWR alarms\",\"ANT-02 Fails RSSI test(s)\",\"ANT-03 Fails VSWR test(s)\"],\"Cancelled\":[\"CAN-00 Uncommon Failure\",\"CAN-01 No Conquest & XCM\",\"CAN-02 Site Access\",\"CAN-03 Resources (TC,FC,DT)\",\"CAN-05 Permits\",\"CAN-06 Missing Material\",\"CAN-07 VZ 48 Hr. Notice\",\"CAN-08 Missing GPS\",\"CAN-09 Time Constraints\",\"CAN-10 Pre Ex. Alarms\",\"CAN-11 Transport\"],\"Configuration\":[\"CNF-00 Uncommon Failure\",\"CNF-01 RU FW not loaded or issue\",\"CNF-02 Incorrect Parameter\",\"CNF-03 Incorrect Fiber length\",\"CNF-04 Incorrect MME Pool\",\"CNF-05 FW/SW fail to load /activated\",\"CNF-06 Missing Inter-VM routes on vCU\",\"CNF-07 DHCP connection issue\",\"CNF-08 vCU hosting the AU not instantiated by customer\",\"CNF-09 Static routes not configured on USM\"],\"Call Testing\":[\"CTE-00 Uncommon Failure\",\"CTE-01 No UE For Call Test\",\"CTE-02 Low DL throughput\",\"CTE-03 Failed to Attach to 5g\",\"CTE-04 Failed to Attach to 4g\",\"CTE-05 Inter-RAT NBR not defined between FSM4 and 5G AU/vCU\",\"CTE-06 Nearby Overpowering Site\"],\"Design\":[\"DES-00 Uncommon Failure\",\"DES-01 Mismatch between Design and Physical Install\",\"DES-02 X2 Links OOS after X2/ENDC script\",\"DES-03 Incorrect CIQ Fail to create GROW Profiles\",\"DES-04 Incorrect IP Information in CIQ and respective RF scripts\",\"DES-05 Alarms - Pre-existing on Equipment/Network\",\"DES-06 RF Scripts missing or not created\",\"DES-07 Incorrect Parameters  - does not follow the engineering rules\",\"DES-08 Missing Parameters\",\"DES-09 CIQ missing in RF FTP Server\",\"DES-10 Designed Vs. Install (example: designed for GEN2 Vs. physical GEN1 installed)\"],\"Hardware\":[\"HW-00 Uncommon Failure\",\"HW-01 Site/Sector Down -Pre\",\"HW-03 PowerShift - DC power per requirement at RRH end\",\"HW-04 RETs Misconfigured\",\"HW-05 RETs disabled or lost the connectivity\",\"HW-06 GPS unit Faulty\",\"HW-07 Faulty Radio\",\"HW-08 Bad Router card\",\"HW-09 Bad Fiber/SFP/ CPRI fiber wrong length, dirty, broken\",\"HW-10 Bad RFM (RRH, MMU)\",\"HW-11 Bad CDU-30, LCC, LMD\",\"HW-12 Cancelled the site / has not enough time in MW\",\"HW-13 CPRI Power Readings out of range\",\"HW-14 Door/Cabinet/Fan alarm Issue\",\"HW-15 SFP incorrect/Faulty on CDU side\",\"HW-16 SFP incorrect/Faulty on Tower Top side\",\"HW-17 Non Samsung Approved GPS Cable\"],\"Installation\":[\"INS-00 Uncommon Failure\",\"INS-01 Installation not complete or Incorrect Installation\",\"INS-02 Unable to commission the node\",\"INS-03 Backhaul not ready\",\"INS-04 Chassis backplane issue. Not able to reseat the card\",\"INS-05 No Show\",\"INS-06 GPS Sync Issue\",\"INS-07 No Power\",\"INS-08 Power issue/ Alarm\",\"INS-09 Missing Backhaul Fiber\",\"INS-10 Missing GPS Cable\",\"INS-11 Missing CPRI Fiber\",\"INS-12 Fibers not Labeled\",\"INS-13 Missing Backhaul SFP\",\"INS-14 Missing CPRI SFP\",\"INS-15 Missing Equipment at Site\",\"INS-16 Access - FT couldn’t load ENV\",\"INS-17 RU is not connected\",\"INS-18 Missing Attenuators\",\"INS-19 Fibers not plugged into Raycap/OVP/Junction box/CPRI Panel\",\"INS-20 Fibers connected to incorrect ports on the Radio head\",\"INS-21 Missing Raycap/OVP/Junction box/CPRI Panel\"],\"Site Readiness\":[\"SRE-00 Uncommon Failure\",\"SRE-01 Site location unknown\",\"SRE-02 Bad AU\",\"SRE-03 GC has no proper Tools for Troubleshooting\",\"SRE-04 SANE Issue\",\"SRE-05 Rescheduled/Cancelled by PM\",\"SRE-06 Site is removed/relocated by customer\",\"SRE-07 Chrome (web-browser) issue\",\"SRE-08 DUO issue/timed out\"],\"C&I Delivery tool\":[\"SRT-00 Uncommon Failure\",\"SRT-01 Unable to load CIQ to SRCT\",\"SRT-02 Sectors are grown on different software load\",\"SRT-03 Grow profiles not matching with engineering rules\",\"SRT-04 SRCT not reachable\",\"SRT-05 DBs down\",\"SRT-06 Unable to run pnp grow script\",\"SRT-07 SRCT failed to grow sector(s) on USM\",\"SRT-08 Script too big for SCRT to run\",\"SRT-09 SRCT Connection Manager issue\",\"SRT-10 Leading zero is being removed by SRT tool (4G)\"],\"Transport\":[\"TPT-00 Uncommon Failure\",\"TPT-01 CSR Router Issue / Customer did not complete Router configuration\",\"TPT-02 Incorrect Gateway IP in CIQ\",\"TPT-03 Bad Backhaul Fiber\",\"TPT-04 No connection to vRAN\",\"TPT-05 No connection to RRH\",\"TPT-06 GPS issue on IXR\",\"TPT-07 No light from the IXR ports\",\"TPT-08 Telecom IP alarm\",\"TPT-09 Router port in CIQ is already in use\",\"TPT-10 4G backhaul loosing remote connectivity when 5G backhaul is connected\",\"TPT-11 Incorrect Router port Information on CIQ\",\"TPT-12 Missing Router card\",\"TPT-13 Cells fail to restore\"],\"S// EMS\":[\"USM-00 Uncommon Failure\",\"USM-01 slow response\",\"USM-02 eNB alarms are not synced\",\"USM-03 GA software not staged\",\"USM-04 vDU not instantiated\",\"USM-05 Unable to access USM\",\"USM-06 Duplicate gNB in multiple ACPFs\",\"USM-07 Could not lock/unlock Cell\",\"USM-08 vRAN not ready\",\"USM-09 GA software issue\"]},\"issueAttributeToList\":[\"Verizon\",\"Samsung\",\"Tool\",\"Process\",\"RF Design\",\"C&I\",\"Deployment\"],\"issueResolvedList\":[\"Yes\",\"No\"],\"timelineList\":[\"Nokia ENB unmanaged\",\"Samsung CDU-30 pingable\",\"Samsung CDU-30 C&I Start\",\"Samsung CDU-30 C&I End\",\"Waiting on tower crew to complete work on radios\",\"Detected all the radios, fw upgrade, OPS ATP\",\"Unlocked sectors\",\"Basic Call Test Complete \"],\"troubleshootTimelineList\":[\"Troubleshooting Time\",\"Total C&I Effort\"],\"criticalCheckList\":[\"Alarm Free\",\"Cells Admin state is per CIQ\",\"RSSI Alarm Checked\",\"RSSI Imbalance Checked\",\"VSWR Checked\",\"Fiber Checked\",\"SFP Checked\",\"ATP all pass\",\"All RETs scanned\",\"FSU Bypass\",\"All RETs labeled (Accounting for every carrier)\",\"Follow-up Required\"],\"checkPerformedData\":[\"Yes\",\"No\"],\"reportStatus\":[\"Completion\",\"Exception\"],\"typeOfEffort\":[\"1st Touch -1st Attempt\",\"1st Touch - 2nd Attempt\",\"2nd Touch 1st Attempt\",\"2nd Touch 2nd Attempt\",\"Hot Cut - 1st Attempt\",\"Hot Cut - 2nd Attempt\",\"Revisit - TS\",\"Prechecks\",\"Cutover\"],\"eNodeBSW\":[\"21AP3\",\"21BP3\"],\"fsuSW\":[\"21AP2\",\"21BP1\"],\"vDUSW\":[\"21A\",\"21BP3\",\"21BP3.01\",\"21BP4\"]}","programName":"VZN-4G-USM-LIVE","customerId":1,"siteId":null,"serviceToken":"69262","programId":34,"postAuditIssues":[],"status":"SUCCESS","siteDetails":{"eNodeBName":"NORTHRIDGE","eNodeBSW":"21.B.0-0104(ENB.21B.P3.01)","fsuSW":"21.B.0-0201(FSU.21B.P1.01) ","vDUSW":"21.B.0-0400(VDU.21B.P3.00)"}}

                            // this.reportData = {"alarmFree":"Yes","allRetsLabeled":"Yes","allRetsScanned":"No","atpAllPass":"No","auCellsReqperMarket":"No","basicSanityTestAllPass":"No","carriers":[{"likeforlike":"700","incremental":"700","likeforlikeCheckBox":"No","incrementalCheckBox":"No"},{"likeforlike":"850","incremental":"850","likeforlikeCheckBox":"No","incrementalCheckBox":"Yes"},{"likeforlike":"AW","incremental":"AW","likeforlikeCheckBox":"Yes","incrementalCheckBox":"No"},{"likeforlike":"PCS","incremental":"PCS","likeforlikeCheckBox":"Yes","incrementalCheckBox":"No"},{"likeforlike":"AWS","incremental":"AWS","likeforlikeCheckBox":"No","incrementalCheckBox":"Yes"},{"likeforlike":"CBRS","incremental":"CBRS","likeforlikeCheckBox":"No","incrementalCheckBox":"No"},{"likeforlike":"LAA","incremental":"LAA","likeforlikeCheckBox":"No","incrementalCheckBox":"No"}],"categoryDetails":[{"issueId":null,"category":"Transport","issue":"TPT-05 No connection to RRH","remarks":"","inIssueEditMode":false},{"issueId":null,"category":"Installation","issue":"INS-07 No Power","remarks":"Hello All","inIssueEditMode":false}],"cellAdminStateIsPerCiq":"Yes","ciCompletionStatus":"","finalIntegStatus":"Migration Successful","followUpRequired":"No","cbrsStatus":"Rolled Back","integrationType":"","samsungUpStatus":"Pre Migration Successful","market":"TRI","neId":"61452","neName":"061452_CONCORD_2_NH_HUB","postAuditIssues":[{"testName":"ENB State Check","yangCommand":"managed-element enb-function eutran-generic-cell eutran-cell-fdd-tdd usage-state","test":"To check if eNB is in the correct operational mode and its admin state","expectedResult":"operational-state: enabled, administrative-state: unlocked, operational-mode: normal-mode","actionItem":"Change administrative-state: unlocked.\n Change operational-mode: normal-mode. Check Cell state\n Check alarms if eNB continues to be disabled","auditIssue":"1. CellNum : 18 administrative-state : locked \n2. CellNum : 32 administrative-state : locked \n3. CellNum : 34 administrative-state : locked","remarks":""},{"testName":"ENB State Check","yangCommand":"managed-element enb-function eutran-generic-cell eutran-cell-fdd-tdd usage-state","test":"To check if eNB is in the correct operational mode and its admin state","expectedResult":"operational-state: enabled, administrative-state: unlocked, operational-mode: normal-mode","actionItem":"Change administrative-state: unlocked.\n Change operational-mode: normal-mode. Check Cell state\n Check alarms if eNB continues to be disabled","auditIssue":"1. CellNum : 18 operational-mode : growth-mode \n2. CellNum : 32 operational-mode : growth-mode \n3. CellNum : 34 operational-mode : growth-mode","remarks":""}],"project":"SNAP 4G Hot Cut","remarks":"","reportDate":"08-27-2021","resAuditIssueCheck":true,"rfAtpStarted":"No","siteReportStatus":"Exception","softWareRelease":"21-A-0","fuzeProjId":"12344","timeLineDetails":[{"timeLine":"Nokia ENB unmanaged","siteDate":"08-27-2021","siteTime":"2:33 AM","remarks":"Hello"},{"timeLine":"Samsung CDU-30 pingable","siteDate":"08-27-2021","siteTime":"","remarks":""},{"timeLine":"Samsung CDU-30 C&I Start","siteDate":"08-27-2021","siteTime":"","remarks":""},{"timeLine":"Waiting on tower crew to complete work on radios","siteDate":"08-27-2021","siteTime":"","remarks":""},{"timeLine":"Detected all the radios, fw upgrade, OPS ATP","siteDate":"08-27-2021","siteTime":"","remarks":""},{"timeLine":"Unlocked sectors","siteDate":"08-27-2021","siteTime":"","remarks":""},{"timeLine":"Basic Call Test Complete ","siteDate":"08-27-2021","siteTime":"","remarks":""}],"twampPingTestRepAttached":"Yes"};

                            if (jsonStatue.status == "SUCCESS") {
                                this.showLoader = false;
                                
                                this.reportFormOptions = JSON.parse(jsonStatue.siteInputs);

                                this.issuesCategory = Object.keys(this.reportFormOptions.issueCategory);
                                this.issueAttributeToList = this.reportFormOptions.issueAttributeToList;
                                this.issueTechnologyList = this.reportFormOptions.issueTechnologyList;
                                this.issueResolvedList = this.reportFormOptions.issueResolvedList;

                                this.postAuditIssues = jsonStatue.postAuditIssues ? jsonStatue.postAuditIssues : [];
                                this.siteDetailsValues = jsonStatue.siteDetails ? jsonStatue.siteDetails : {};
                                

                                if(key.siteReportId || jsonStatue.isPartialReport) {
                                    this.reportData = jsonStatue.reportDetails;
                                    
                                    for(let index = 0; index < this.reportData.carriers.length; index++) {
                                        this.carriersFlag[this.reportData.carriers[index].likeforlike]["likeforlikeCheckBox"] = this.reportData.carriers[index].likeforlikeCheckBox == "Yes" ? true : false;
                                        this.carriersFlag[this.reportData.carriers[index].incremental]["incrementalCheckBox"] = this.reportData.carriers[index].incrementalCheckBox == "Yes" ? true : false;
                                    }

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
                                    for (let index = 0; index < this.troubleshootTimelineList.length; index++) {
                                        this.troubleshootTimelineList[index].siteDate = new Date(this.troubleshootTimelineList[index].siteDate);
                                        // let siteTime = this.troubleshootTimelineList[index].siteTime;
                                        // this.troubleshootTimelineList[index].siteTimeHr = siteTime.split(":")[0];
                                        // this.troubleshootTimelineList[index].siteTimeMin = siteTime.split(":")[1];
                                    }

                                    this.reportData.reportDate = new Date(this.reportData.reportDate);
                                    this.reportData.userName = key.userName;
                                    this.timelineIssuesList.issues = this.reportData["categoryDetails"];
                                    // if there no data in issue, create one empty row
                                    if(!this.timelineIssuesList.issues || this.timelineIssuesList.issues.length == 0) {
                                        this.resetReportTimelineIssueForm();
                                    }
                                    setTimeout(() => {
                                        // Set default project based on program
                                        if(!this.reportData["project"]) {
                                            this.reportData["project"] = this.programName == 'VZN-5G-MM' ? "SNAP 5G" : this.programName == 'VZN-5G-DSS' ? "LS3" : this.programName == 'VZN-5G-CBAND' ? "LS6" : this.programName == 'VZN-4G-FSU' ? "FSU Bypass" : "";
                                        }
                                    }, 100);
                                }
                                else {
                                    if (this.programName == "VZN-5G-MM") {
                                        this.siteReportNE = {
                                            "title": key.siteName,
                                            "ids": jsonStatue.neIDs
                                        };
                                    }
                                    this.setDefaultSiteData(key.userName);
                                }

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

    setDefaultSiteData(userName) {
        this.resetReportData();
        this.reportData.reportDate = new Date();
        this.reportData.userName = userName;
        setTimeout(() => {
            let isInfoAvailable = true;
            for (let index = 0; index < this.postAuditIssues.length; index++) {
                if (this.postAuditIssues[index].errorCode.indexOf("_INFO") < 0) {
                    isInfoAvailable = false; //Exceptional case if _INFO is not in any one of them
                    break;
                }
            }
            this.reportData["siteReportStatus"] = isInfoAvailable ? "Completion" : "Exception";//(this.postAuditIssues && this.postAuditIssues.length > 0) ? "Exception" : "Completion";
            // Set default project based on program
            this.reportData["project"] = this.programName == 'VZN-5G-MM' ? "SNAP 5G" : this.programName == 'VZN-5G-DSS' ? "LS3" : this.programName == 'VZN-5G-CBAND' ? "LS6" : this.programName == 'VZN-4G-FSU' ? "FSU Bypass" : "";
            this.reportData["vendorType"] = "Samsung";
            
            // If not generated earlier, put the row neID
            this.reportData["neId"] = this.siteReportNE["ids"];
            this.reportData["neName"] = this.siteReportNE["title"];
            // Some fixed value to be shown
            // let siteDetailsValues = jsonStatue.siteDetails;
            if (this.siteDetailsValues) {
                let keysHavingValues = Object.keys(this.siteDetailsValues);
                for (let key of keysHavingValues) {
                    try {
                        this.reportData[key] = this.siteDetailsValues[key];
                    } catch (error) {

                    }
                }
            }
        }, 100);
        this.setTimeLineInitialData();
        this.setCriticalCheckInitialData();
        this.resetReportTimelineIssueForm();
    }

    resetReportData() {
        // this.isCancellationReport = false;
        this.reportData = {"neName":"","neId":"","reportDate":"","siteReportStatus":"","eNodeBName":"","eNodeBSW":"","fsuSW":"","vDUSW":"","project":"","softWareRelease":"","market":"","fuzeProjId":"","resAuditIssueCheck":false,"integrationType":"","vendorType":"","mmCommComp":"","mmOpsATP":"","dssCommComp":"","dssOpsATP":"","fsuIntegBypass":"","fsuIntegMultiplex":"","finalIntegStatus":"","currCBANDIntegStatus":"","finalCBANDIntegStatus":"","typeOfEffort":"","remarks":"","timeLineDetails":"","troubleshootTimelineDetails":"","categoryDetails":"","lteCommComp":"","lteCBRSCommComp":"","lteLAACommComp":"","lteOpsATP":"","lteCBRSOpsAtp":"","lteLAAOpsATP":"","tcReleased":"","ovTicketNum":""};
        this.carriersFlag = {"700":{"likeforlikeCheckBox":false,"incrementalCheckBox":false},"850":{"likeforlikeCheckBox":false,"incrementalCheckBox":false},"AWS":{"likeforlikeCheckBox":false,"incrementalCheckBox":false},"PCS":{"likeforlikeCheckBox":false,"incrementalCheckBox":false},"AWS3":{"likeforlikeCheckBox":false,"incrementalCheckBox":false},"CBRS":{"likeforlikeCheckBox":false,"incrementalCheckBox":false},"LAA":{"likeforlikeCheckBox":false,"incrementalCheckBox":false}};
        this.setCriticalCheckInitialData();
        this.resetReportTimelineIssueForm();
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
    saveTimelineRow(event, key, index1) {        
        // validator.performValidation(event, validations, "save_update");
        setTimeout(() => {
            // if (this.isValidScriptRowForm(event)) {              
                key.inTimelineEditMode = false;
                this.currTimelineEditScript = null;
            // }
        }, 1000);
    }
    isValidScriptRowForm(event) {
        return ($(event.target).parents("form#scriptRowForm").find(".error-border").length == 0) ? true : false;
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
            }
         });
        
     }
    cancelIssueRow(key, index) {
        key.inIssueEditMode = false;
        if (this.currIssueEditScript) {
            this.timelineIssuesList.issues[index] = this.currIssueEditScript;
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

    addIssueRow() {
        let tempObj = {
            "issueId": null,
            "category": "",
            "issue": "",
            "technology": "",
            "attribute":"",
            "resolved":"",
            "remarks":"",
            "inIssueEditMode": true
        };
        this.timelineIssuesList.issues.unshift(tempObj);
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

    closeModelSiteCompReport() {
        this.issuesCategory = null;
        this.siteReportNE = null;
        this.selectedTableRow = null;
        this.selectedReportCIQ = null;
        this.siteDetailsValues = {};
        this.siteCompReportBlock.close();
        this.resetReportData();
    }

    showHistoryModal(siteReportHistoryModal, key) {
        setTimeout(() => {
            this.showLoader = true;

            this.runtestService.getHistorySiteDetails(this.sharedService.createServiceToken(), key.enbId)
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
                                    this.siteReportHistory = jsonStatue.historyDetails;
                                    this.siteReportHistoryBlock = this.modalService.open(siteReportHistoryModal, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView reportView' });
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
        
                            let jsonStatue = {"historyDetails":[{"id":85,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-09-13T12:53:03.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"fileName":"SiteExceptionReport_8891003415_5GDU_IBS_BEDMINSTER_LAB_20210917_14_07_21.xlsx","filePath":"/home/bala/Samsung/SMART/Network_config_Details//","ciqFileName":"ciq1","neName":"061452_CONCORD_2_NH_HUB","remarks":"","packedBy":"superadmin","packedDate":"2021-09-17 14:07:30","searchStartDate":null,"searchEndDate":null,"siteReportStatus":"Exception","neId":"61452","siteName":null,"reportType":"SITE"},{"id":84,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-09-13T12:53:03.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"fileName":"SiteExceptionReport_8891003415_5GDU_IBS_BEDMINSTER_LAB_20220118_13_50_44.xlsx","filePath":"/home/bala/Samsung/SMART/Network_config_Details//","ciqFileName":"ciq1","neName":"061452_CONCORD_2_NH_HUB","remarks":"","packedBy":"superadmin","packedDate":"2021-09-17 13:55:03","searchStartDate":null,"searchEndDate":null,"siteReportStatus":"Exception","neId":"61452","siteName":null,"reportType":"SITE"},{"id":83,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-09-13T12:53:03.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"fileName":"SiteCompletionReport_20210917_13_51_14.xlsx","filePath":"/home/bala/Samsung/SMART/Network_config_Details//","ciqFileName":"ciq1","neName":"061452_CONCORD_2_NH_HUB","remarks":"","packedBy":"superadmin","packedDate":"2021-09-17 13:51:23","searchStartDate":null,"searchEndDate":null,"siteReportStatus":"Exception","neId":"61452","siteName":null,"reportType":"SITE"},{"id":73,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-09-13T12:53:03.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"fileName":"SiteCompletionReport_20210902_12_44_47.xlsx","filePath":"/home/bala/Samsung/SMART/Network_config_Details//","ciqFileName":"ciq1","neName":"061452_CONCORD_2_NH_HUB","remarks":"","packedBy":"superadmin","packedDate":"2021-09-02 12:45:02","searchStartDate":null,"searchEndDate":null,"siteReportStatus":"Exception","neId":"61452","siteName":null,"reportType":"SITE"},{"id":72,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-09-13T12:53:03.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"fileName":"SiteCompletionReport_20210902_12_04_05.xlsx","filePath":"/home/bala/Samsung/SMART/Network_config_Details//","ciqFileName":"ciq1","neName":"061452_CONCORD_2_NH_HUB","remarks":"","packedBy":"superadmin","packedDate":"2021-09-02 12:04:30","searchStartDate":null,"searchEndDate":null,"siteReportStatus":"Exception","neId":"61452","siteName":null,"reportType":"SITE"},{"id":70,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-09-13T12:53:03.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"fileName":"SiteCompletionReport_20210901_14_45_51.xlsx","filePath":"/home/bala/Samsung/SMART/Network_config_Details//","ciqFileName":"ciq1","neName":"061452_CONCORD_2_NH_HUB","remarks":"","packedBy":"superadmin","packedDate":"2021-09-01 14:45:53","searchStartDate":null,"searchEndDate":null,"siteReportStatus":"Exception","neId":"61452","siteName":null,"reportType":"SITE"},{"id":65,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-09-13T12:53:03.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"fileName":"SiteCompletionReport_20210831_15_45_18.xlsx","filePath":"/home/bala/Samsung/SMART/Network_config_Details//","ciqFileName":"ciq1","neName":"061452_CONCORD_2_NH_HUB","remarks":"","packedBy":"","packedDate":"2021-08-31 15:45:26","searchStartDate":null,"searchEndDate":null,"siteReportStatus":"Exception","neId":"61452","siteName":null,"reportType":"SITE"},{"id":64,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-09-13T12:53:03.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"fileName":"SiteCompletionReport_20210831_15_20_06.xlsx","filePath":"/home/bala/Samsung/SMART/Network_config_Details//","ciqFileName":"ciq1","neName":"061452_CONCORD_2_NH_HUB","remarks":"","packedBy":"","packedDate":"2021-08-31 15:20:07","searchStartDate":null,"searchEndDate":null,"siteReportStatus":"Exception","neId":"61452","siteName":null,"reportType":"SITE"},{"id":63,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-09-13T12:53:03.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"fileName":"SiteCompletionReport_20210831_15_18_53.xlsx","filePath":"/home/bala/Samsung/SMART/Network_config_Details//","ciqFileName":"ciq1","neName":"061452_CONCORD_2_NH_HUB","remarks":"","packedBy":"","packedDate":"2021-08-31 15:18:54","searchStartDate":null,"searchEndDate":null,"siteReportStatus":"Exception","neId":"61452","siteName":null,"reportType":"SITE"},{"id":62,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-09-13T12:53:03.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"fileName":"SiteCompletionReport_20210831_15_14_04.xlsx","filePath":"/home/bala/Samsung/SMART/Network_config_Details//","ciqFileName":"ciq1","neName":"061452_CONCORD_2_NH_HUB","remarks":"","packedBy":"","packedDate":"2021-08-31 15:14:12","searchStartDate":null,"searchEndDate":null,"siteReportStatus":"Exception","neId":"61452","siteName":null,"reportType":"SITE"},{"id":61,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-09-13T12:53:03.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"fileName":"SiteCompletionReport_20210831_14_42_12.xlsx","filePath":"/home/bala/Samsung/SMART/Network_config_Details//","ciqFileName":"ciq1","neName":"061452_CONCORD_2_NH_HUB","remarks":"","packedBy":"","packedDate":"2021-08-31 14:42:21","searchStartDate":null,"searchEndDate":null,"siteReportStatus":"Exception","neId":"61452","siteName":null,"reportType":"SITE"},{"id":60,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-09-13T12:53:03.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"fileName":"SiteCompletionReport_20210831_14_11_26.xlsx","filePath":"/home/bala/Samsung/SMART/Network_config_Details//","ciqFileName":"ciq1","neName":"061452_CONCORD_2_NH_HUB","remarks":"","packedBy":"","packedDate":"2021-08-31 14:11:30","searchStartDate":null,"searchEndDate":null,"siteReportStatus":"Exception","neId":"61452","siteName":null,"reportType":"SITE"},{"id":59,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-09-13T12:53:03.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"fileName":"SiteCompletionReport_20210831_12_46_21.xlsx","filePath":"/home/bala/Samsung/SMART/Network_config_Details//","ciqFileName":"ciq1","neName":"061452_CONCORD_2_NH_HUB","remarks":"","packedBy":"","packedDate":"2021-08-31 12:46:30","searchStartDate":null,"searchEndDate":null,"siteReportStatus":"Exception","neId":"61452","siteName":null,"reportType":"SITE"},{"id":56,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-09-13T12:53:03.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"fileName":"SiteCompletionReport_20210830_13_38_59.xlsx","filePath":"/home/bala/Samsung/SMART/Network_config_Details//","ciqFileName":"ciq1","neName":"061452_CONCORD_2_NH_HUB","remarks":"","packedBy":"","packedDate":"2021-08-30 13:39:00","searchStartDate":null,"searchEndDate":null,"siteReportStatus":"Exception","neId":"61452","siteName":null,"reportType":"SITE"},{"id":53,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-09-13T12:53:03.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"fileName":"SiteCompletionReport_20210830_10_11_40.xlsx","filePath":"/home/bala/Samsung/SMART/Network_config_Details//","ciqFileName":"ciq1","neName":"061452_CONCORD_2_NH_HUB","remarks":"","packedBy":"","packedDate":"2021-08-30 10:11:48","searchStartDate":null,"searchEndDate":null,"siteReportStatus":"Exception","neId":"61452","siteName":null,"reportType":"SITE"}],"sessionId":"b5cc2ed5","serviceToken":"93096","status":"SUCCESS","reason":""};

                            if (jsonStatue.status == "SUCCESS") {
                                this.showLoader = false;
                                this.siteReportHistory = jsonStatue.historyDetails;
                                this.siteReportHistoryBlock = this.modalService.open(siteReportHistoryModal, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView reportView' });
                            } else {
                                this.showLoader = false;
                                this.displayModel(jsonStatue.reason, "failureIcon");
                            }
        
                        }, 100); */

                        //Please Comment while checkIn   
                    });
        }, 100);
    }

    downloadReport(event) {
        this.prepareSiteReportData();

        this.runtestService.exportSiteReportDetails(this.reportData, this.sharedService.createServiceToken(), this.selectedReportCIQ, this.selectedTableRow )
            .subscribe(
                data => {
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
                    this.getRunTest();
                },
                error => {
                    //Please Comment while checkIn
                    /* let jsonStatue: any = {"sessionId":"506db794","reason":"Download Failed","status":"SUCCESS","serviceToken":"63524"};
                    let blob = new Blob([jsonStatue["_body"]], {
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
                    this.getRunTest();

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

    savePartialSiteReport() {
        this.prepareSiteReportData();
        this.showInnerLoader = true;
        this.runtestService.saveSiteReportDetails(this.reportData, this.sharedService.createServiceToken(), this.selectedReportCIQ, this.selectedTableRow )
        .subscribe(
            data => {
                setTimeout(() => {
                    this.showInnerLoader = false;
                    let jsonStatue = data.json();
                    if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {

                        if (jsonStatue.status == "SUCCESS") {
                            this.message = "Site report saved successfully !";
                            this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                        } else {
                            this.displayModel(jsonStatue.reason,"failureIcon"); 
                        }
                        this.closeModelSiteCompReport();
                    }
                }, 100);
            },
            error => {
                /* let jsonStatue: any = { "sessionId": "506db794", "reason": "", "status": "SUCCESS", "serviceToken": "63524" };
                setTimeout(() => {
                    this.showInnerLoader = false;
                    if (jsonStatue.status == "SUCCESS") {
                        this.message = "Site report saved successfully !";
                        this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: "static", size: 'lg', windowClass: "success-modal" });
                    } else {
                        this.displayModel(jsonStatue.reason, "failureIcon");
                    }
                    this.closeModelSiteCompReport();
                }, 3000); */
            }
        )
    }

    prepareSiteReportData() {
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
                "siteTime": this.reportData.isCancellationReport == "Yes"  ? "" : (this.timelineList[index].siteTimeNA ? "NA" : this.datePipe.transform(this.timelineList[index].siteTime, "HH:mm")),
                "remarks": this.timelineList[index].remarks,
            }
            timelineData.push(tempObj);
        }

        let troubleshootTimelineList = [];
        for(let index = 0; index < this.troubleshootTimelineList.length; index ++) {
            let tempObj = {
                "timeLine": this.troubleshootTimelineList[index].timeLine,
                "siteDate": this.datePipe.transform(this.troubleshootTimelineList[index].siteDate,"MM-dd-yyyy"),
                "siteTime": this.reportData.isCancellationReport == "Yes" ? "" : (this.sharedService.addLeadeingZero(this.troubleshootTimelineList[index].siteTimeHr) + ":" + this.sharedService.addLeadeingZero(this.troubleshootTimelineList[index].siteTimeMin)),
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
    }

    downloadSiteReportFileRow(key) {
        this.runtestService.downloadSiteReportFile(key,this.sharedService.createServiceToken())
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

    closeModelSiteReportHistory() {
        // this.auditIssueNEInfo = null;
        this.siteReportHistory = [];
        this.siteReportHistoryBlock.close();
    }

    onBulkSiteFocusOut(event: any){
        this.onChangeNEs(event);
    }

    clearSelectedNEs() {
        this.selectedNEItems = null;
        this.rfScriptList = "";
        this.selectedItemsPMig = [];
        this.selectedItemsPAMig=[];
        this.selectedItemsNEMig=[];
        this.selectedRadioUnittems = [];
        this.isRSSIUseCaseSelected = false;
        this.isRETDisabled = true;
        this.isRSSIDisabled = true;
        this.useCaseListPM = [];
        this.useCaseListPA = [];
        this.useCaseListNE = [];
        this.dropdownRadioUnitList = [];
        this.dropdownNeStatusList=[];
        this.migrationStrategy = 'Legacy IP';
    }

    showInProgressSitesModal(siteReportHistoryModal) {
        this.showLoader = true;
        this.runtestService.getUserNameList(this.sharedService.createServiceToken())
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
                                this.userList = [];//jsonStatue.userNameList;
                                let userNameList = jsonStatue.userNameList;
                                for (let itm of userNameList) {
                                    let userName = { item_id: itm, item_text: itm };
                                    this.userList.push(userName);
                                }
                                this.stopSiteBlock = this.modalService.open(siteReportHistoryModal, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView reportView' });
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
                    //Please Comment while checkIn

                    /* setTimeout(() => {
    
                        this.showLoader = false;
    
                        let jsonStatue = {"reason":null,"sessionId":"1bb2ec02","serviceToken":"75910","userNameList":["admin","Ratz5","Ratz4","Ratz3","Ratz2","Ratz1","Hendrix","Satch","Mishka","Anaisha","Mayurika","Ratz","ratul","bala","india","son","kimath","dennis"],"status":"SUCCESS"};

                        if (jsonStatue.status == "SUCCESS") {
                            this.showLoader = false;
                            this.userList = [];//jsonStatue.userNameList;
                            let userNameList = jsonStatue.userNameList;
                            for (let itm of userNameList) {
                                let userName = { item_id: itm, item_text: itm };
                                this.userList.push(userName);
                            }
                            this.stopSiteBlock = this.modalService.open(siteReportHistoryModal, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView reportView' });
                        } else {
                            this.showLoader = false;
                            this.displayModel(jsonStatue.reason, "failureIcon");
                        }
    
                    }, 100); */

                    //Please Comment while checkIn   
                });
    }


    showInProgressSites(refresh = true) {
        this.showInnerLoader = true;
        let fromDate = this.datePipe.transform(this.siteInProgressFromDt,"MM/dd/yyyy"); // On change Date,Update Row
        let toDate = this.datePipe.transform(this.siteInProgressToDt,"MM/dd/yyyy");
        this.runtestService.getInProgressSiteDetails(this.sharedService.createServiceToken(), this.selectedUser, fromDate, toDate, this.sityTypeRadio)
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
                                this.auditInprogressSites = jsonStatue.neListMap.map((item) => ({...item, "selected":false}));
                            } else {
                                this.showInnerLoader = false;
                                this.displayModel(jsonStatue.reason, "failureIcon");
                            }
                        }
                        else {
                            this.showInnerLoader = false;
                            this.displayModel("No Data", "failureIcon");
                        }
                    }
                },
                error => {
                    //Please Comment while checkIn

                    /* setTimeout(() => {
    
                        this.showInnerLoader = false;
    
                        let jsonStatue = refresh ? {"neListMap":[{"timeStamp":"2021-11-22 15:40:01.0","neId":"10196023369","wfmId":"730","neName":"10196023369_5GDU_FINLAND-WMTP","userName":"admin"},{"timeStamp":"2021-11-22 15:39:44.0","neId":"8891003194","wfmId":"724","neName":"8891003194_5GDU_SOMERVILLE5-BBTP","userName":"superadmin"},{"timeStamp":"2021-11-22 15:39:44.0","neId":"8891003415","wfmId":"725","neName":"8891003415_5GDU_IBS_BEDMINSTER_LAB","userName":"superadmin"},{"timeStamp":"2021-11-22 15:39:44.0","neId":"8993503228","wfmId":"726","neName":"8993503228_5GDU_Farmingdale-WLTP","userName":"superadmin"},{"timeStamp":"2021-11-22 15:39:44.0","neId":"9794613632","wfmId":"727","neName":"9794613632_5GDU_STRINESTOWN-NWCS","userName":"superadmin"}],"sessionId":"27eedc3b","serviceToken":"61522","status":"SUCCESS","reason":""}:
                        {"neListMap":[{"timeStamp":"2021-11-19 13:31:00.0","neId":"8891003415","wfmId":"709","neName":"8891003415_5GDU_IBS_BEDMINSTER_LAB","userName":"superadmin"},{"timeStamp":"2021-11-19 13:30:25.0","neId":"8891003194","wfmId":"708","neName":"8891003194_5GDU_SOMERVILLE5-BBTP","userName":"admin"}],"sessionId":"4ce41f3f","serviceToken":"75910","status":"SUCCESS","reason":""};

                        if (jsonStatue.status == "SUCCESS") {
                            this.showInnerLoader = false;
                            this.auditInprogressSites = jsonStatue.neListMap.map((item) => ({...item, "selected":false}));
                        } else {
                            this.showInnerLoader = false;
                            this.displayModel(jsonStatue.reason, "failureIcon");
                        }
    
                    }, 100); */

                    //Please Comment while checkIn   
                });
    }

    closeModelRunningSite() {
        this.sityTypeRadio = "inProgress";
        this.userList = [];
        this.selectedUser = [];
        this.auditInprogressSites = [];
        this.stopSiteBlock.close();
    }

    copyNeIds() {
        let copiedNeIds = this.auditInprogressSites.map((item) => item.neId)
        let copiedString = copiedNeIds.join("\n");
        // navigator.clipboard.writeText(copiedString);
        /* window.navigator.clipboard.writeText(copiedString).then(function () {
            console.log('Async: Copying to clipboard was successful!');
        }, function (err) {
            console.error('Async: Could not copy text: ', err);
        }); */

        const selBox = document.createElement('textarea');
        selBox.style.position = 'fixed';
        selBox.style.left = '0';
        selBox.style.top = '0';
        selBox.style.opacity = '0';
        selBox.value = copiedString;
        document.body.appendChild(selBox);
        selBox.focus();
        selBox.select();
        document.execCommand('copy');
        document.body.removeChild(selBox);

        var x = document.getElementById("snackbar");
        x.className = "show";
        setTimeout(function(){ x.className = x.className.replace("show", ""); }, 3000);
    }

    toggleAllSiteRowSelection() {
        this.auditInprogressSites.forEach((currItem) => currItem.selected = this.allSiteRowSelected);
    }
    toggleSiteRowSelection(index) {
        // this.auditInprogressSites.forEach((currItem) => currItem.selected = this.allSiteRowSelected);
        this.auditInprogressSites[index].selected = !this.auditInprogressSites[index].selected;
        this.checkIfAllSiteRowSelected();
    }
    checkIfAllSiteRowSelected() {
        this.allSiteRowSelected = this.auditInprogressSites.every((currItem) => currItem.selected);
    }
    getNoOfSelectedRows() {
        let selectedRows = this.auditInprogressSites ? this.auditInprogressSites.filter((item) => item.selected) : [];
        return selectedRows.length;
    }

    stopBulkSites() {
        if(this.getNoOfSelectedRows() == 0) {
            this.displayModel("Please select atleast one row", "failureIcon");
            return;
        }
        this.showInnerLoader = true;
        let selectedSitesToStop = this.auditInprogressSites.filter((item) => item.selected);
        this.runtestService.stopBulkNeData(this.sharedService.createServiceToken(), selectedSitesToStop)
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
                                this.message = "Bulk NE stopped successfully !";
                                this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                                this.allSiteRowSelected = false;
                                this.showInProgressSites(false);
                            } else {
                                this.showInnerLoader = false;
                                this.displayModel(jsonStatue.reason, "failureIcon");
                            }
                        }
                        else {
                            this.showInnerLoader = false;
                            this.displayModel("No Data", "failureIcon");
                        }
                    }
                },
                error => {
                    //Please Comment while checkIn

                    /* setTimeout(() => {
    
                        this.showInnerLoader = false;
    
                        let jsonStatue = {"neListMap":[{"timeStamp":"2021-11-19 13:31:00.0","neId":"8891003415","wfmId":"709","neName":"8891003415_5GDU_IBS_BEDMINSTER_LAB","userName":"superadmin"},{"timeStamp":"2021-11-19 13:30:25.0","neId":"8891003194","wfmId":"708","neName":"8891003194_5GDU_SOMERVILLE5-BBTP","userName":"admin"}],"sessionId":"4ce41f3f","serviceToken":"75910","status":"SUCCESS","reason":""};

                        if (jsonStatue.status == "SUCCESS") {
                            this.showInnerLoader = false;
                            this.message = "Bulk NE stopped successfully !";
                            this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                            this.allSiteRowSelected = false;
                            this.showInProgressSites(false);
                        } else {
                            this.showInnerLoader = false;
                            this.displayModel(jsonStatue.reason, "failureIcon");
                        }
    
                    }, 100); */

                    //Please Comment while checkIn   
                });
    }

    onChangeSiteTypeRadio() {
        this.selectedUser = [];
        this.auditInprogressSites = [];
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
        if(!time) {return ""}
        // Time in format 01:45 am
        let d = new Date();
        let timeData = time.split(" ");
        let timeArr = timeData[0].split(":");
        let isPM = (timeData[1] == 'PM' && timeArr[0] != '12') ? true : false;
        let hr = isPM ? parseInt(timeArr[0]) + 12 : timeArr[0];
        d.setHours(hr);
        d.setMinutes(timeArr[1]);
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

    /*
    * Used to validate the file
    * @param : event
    * @retun : null
    */
    chooseFile(event) {
        let files: FileList = event.target.files,
            invalidFilenames = [];
        for (var i = 0; i < files.length; i++) {
            //.tgz, .tar.gz, .zip
            if (files[i].name.indexOf('.xlsx') >= 0) {

            } else {
                invalidFilenames.push(files[i].name);
            }
        }
    }

    getRadioUnitList(){
          this.selectedRadioUnittems = [];
        //   let selectedNEs = this.selectedItemsNE.map((item) => item.item_id);
            let neDetails = [];
            for (let j of this.selectedNEItems) {
                let selNE = {
                    "eNBId": j.eNBId,
                    "eNBName": j.eNBName
                }
                neDetails.push(selNE);
            }
            if (neDetails && neDetails.length > 0) {
                this.showLoader = true;
                this.runtestService.getRadioUnitListData(this.ciqDetails.ciqFileName, neDetails, this.sharedService.createServiceToken())
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
                                          this.dropdownRadioUnitList = [];
                                          let bandNameList = jsonStatue.radioName;
                                          for (let itm of bandNameList) {
                                              let dropdownList = { item_id: itm, item_text: itm };
                                              this.dropdownRadioUnitList.push(dropdownList);
                                          }
                                          this.dropdownSettingsRadioUnit = {
                                              singleSelection: false,
                                              idField: 'item_id',
                                              textField: 'item_text',
                                              selectAllText: 'Select All',
                                              unSelectAllText: 'UnSelect All',
                                              itemsShowLimit: 1,
                                              allowSearchFilter: false
                                          };
                                          this.selectedRadioUnittems = bandNameList;
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
                              let jsonStatue = JSON.parse('{"radioName":["Band_1","Band_2","Band_3","Band_4","Band_5"],"sessionId":"aa5393be","serviceToken":"77360","status":"SUCCESS"}');
                              if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
                                  this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
                              }
                              if (jsonStatue.status == "SUCCESS") {
                                  this.dropdownRadioUnitList = [];
                                  let bandNameList = jsonStatue.radioName;
                                  for (let itm of bandNameList) {
                                      let dropdownList = { item_id: itm, item_text: itm };
                                      this.dropdownRadioUnitList.push(dropdownList);
                                  }
                                  this.dropdownSettingsRadioUnit = {
                                      singleSelection: false,
                                      idField: 'item_id',
                                      textField: 'item_text',
                                      selectAllText: 'Select All',
                                      unSelectAllText: 'UnSelect All',
                                      itemsShowLimit: 1,
                                      allowSearchFilter: false
                                  };
                                  this.selectedRadioUnittems = bandNameList;
                              } else {
                                  this.showLoader = false;
                              }
                          }, 100); */
                          //Please Comment while checkIn
                      });               
          }
          else {
            this.dropdownRadioUnitList = [];
            //this.dropdownList = [];
          }
    
      }

      downloadDiffFile(fileName, filePath) {
        this.runtestService.downloadDiffFile(fileName, filePath, this.sharedService.createServiceToken())
            .subscribe(
                data => {
                    
                    let blob = new Blob([data["_body"]], {
                        type: "application/octet-stream" //"text/plain;charset=utf-8"
                    });

                    FileSaver.saveAs(blob, fileName);
                },
                error => {
                    //Please Comment while checkIn
                    /*let jsonStatue: any = { "sessionId": "506db794", "reason": "Download Failed", "status": "SUCCESS", "serviceToken": "63524" };
                    
                    //let fileName = key.neName + "_" + key.migrationSubType + "_" + key.testName + "_" + Math.round(new Date().getTime()/1000) + ".txt";

                    let blob = new Blob(["Hello, world!"], { 
                        type: "application/octet-stream" //"text/plain;charset=utf-8" 
                    });

                    FileSaver.saveAs(blob, fileName);

                    setTimeout(() => {
                        this.showLoader = false;
                        if (jsonStatue.status == "SUCCESS") {

                        } else {
                            this.displayModel(jsonStatue.reason, "failureIcon");
                        }

                    }, 1000); */
                    //Please Comment while checkIn
                });
    }

    clearSiteReportFields() {
        let userName = this.reportData.userName;
        this.resetReportData();
        this.setDefaultSiteData(userName);
    }

    viewOVFailureMessageSiteReport(failureErrorModal, runTestModel, migType) {
        this.siteReportOV = true;
        this.ovRetryData = {
            runTestId: runTestModel.siteReportId,
            ciqName: runTestModel.ciqName,
            neName: runTestModel.neName,
            migType: migType,
            workFlowId: runTestModel.id,
        };
        this.showLoader = true;

        this.runtestService.viewOVFailureMessageSiteReport(this.sharedService.createServiceToken(), runTestModel.siteReportId, runTestModel.lsmName, runTestModel.neName, this.ovRetryData)
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
                        
                        let jsonStatue = {"reason":"","sessionId":"8909e5cf","serviceToken":"61626","status":"SUCCESS","messageSuccessInfo":"Connection success by 1st API. \nConnection success by 1st API. Connection success by 1st APIConnection success by 1st API\nConnection success by 1st API","messageFailureInfo":"Connection refused by 1st API. Connection refused by 1st APIConnection refused by 1st APIConnection refused by 1st APIConnection \nrefused by 1st APIConnection refused by 1st APIConnection refused by 1st APIConnection \nrefused by 1st API","milestones":[{"name": "16300.01","status": "pass"},{"name": "16300.02","status": "fail"},{"name": "16300.03","status": "pass"},{"name": "16300.04","status": "fail"},{"name":"16300","status": "pass"}],"history": "API connection failed\n"};

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

    viewOVFailureMessagae(failureErrorModal, runTestModel, migType) {
        this.siteReportOV = false;
        this.ovRetryData = {
            runTestId: runTestModel.id,
            ciqName: runTestModel.ciqName,
            neName: runTestModel.neName,
            migType: migType,
            useCaseName: runTestModel.useCase
        };
        this.showLoader = true;

        this.runtestService.viewOVFailureMessage(this.sharedService.createServiceToken(), runTestModel.id, runTestModel.lsmName, runTestModel.neName, this.ovRetryData)
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
                        
                        let jsonStatue = {"reason":"","sessionId":"8909e5cf","serviceToken":"61626","status":"SUCCESS","messageSuccessInfo":"Connection success by 1st API. \nConnection success by 1st API. Connection success by 1st APIConnection success by 1st API\nConnection success by 1st API","messageFailureInfo":"Connection refused by 1st API. Connection refused by 1st APIConnection refused by 1st APIConnection refused by 1st APIConnection \nrefused by 1st APIConnection refused by 1st APIConnection refused by 1st APIConnection \nrefused by 1st API","milestones":[{"name": "16300.01","status": "pass"},{"name": "16300.02","status": "fail"},{"name": "16300.03","status": "pass"},{"name": "16300.04","status": "fail"},{"name":"16300","status": "pass"}],"history": "API connection failed\n"};

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
    ovRetrySiteReport() {
        this.showInnerLoader = true;

        this.runtestService.retryMilestoneUpdateSiteReport(this.sharedService.createServiceToken(), this.ovRetryData, this.milestoneResult)
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
    ovRetry() {
        this.showInnerLoader = true;

        this.runtestService.retryMilestoneUpdate(this.sharedService.createServiceToken(), this.ovRetryData, this.milestoneResult)
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

    ovRetryFailedMilestone(failedMilestone: any) {
        this.showInnerLoader = true;

        this.runtestService.retryFailedMilestoneUpdate(this.sharedService.createServiceToken(), this.ovRetryData, failedMilestone)
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
        this.milestoneResult= [];
        this.historyResult = ""; 
        this.ovRetryData = {};
        this.siteReportOV = false;
        this.failureMsgBlock.close();
    }
    selectScriptFile(usecase, script) {
        this.selectedScript = script;
        this.selectedUsecase = usecase;
        this.selectedScriptName = '';
        let scriptBtn = document.getElementById("scriptUpload")
        scriptBtn.click()
    }
    chooseScriptFile(evt) {
        let files = evt.currentTarget.files[0]
        let formData = new FormData();
        this.selectedScriptName = files.name;
        formData.append('file', files);
        setTimeout(() => {
            this.showInnerLoader = true;
            this.runtestService.uploadScriptFile(formData, this.sharedService.createServiceToken(), this.selectedUsecase, this.selectedScript, this.smName, this.neName, this.testId, files, this.ciqName)
              .subscribe(
                data => {
                  let jsonStatue = data.json();
    		  this.showInnerLoader = false;
                  if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
                    this.selectedScriptName = "";
                    this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
                  } else {
                    if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {
                      if (jsonStatue.status == "SUCCESS") {
                        this.showInnerLoader = false;
                        this.displayModel(jsonStatue.reason, "successIcon");
                      } else {
                        this.showInnerLoader = false;
                        this.selectedScriptName = "";
                        this.displayModel(jsonStatue.reason, "failureIcon");
                      }
                    }
                  }
                },
                error => {
                  //Please Comment while checkIn
                  /* setTimeout(() => {
                    this.showInnerLoader = false;
                    let jsonStatue = { "sessionId": "e9004f23", "reason": "Script File Uploaded successfully !", "status": "SUCCESS", "serviceToken": "64438" };
    
                    if (jsonStatue.status == "SUCCESS") {
                      this.showInnerLoader = false;
                      this.displayModel(jsonStatue.reason, "successIcon");
                    } else {
                      this.selectedScriptName = "";
                      this.showInnerLoader = false;
                      this.displayModel("Failed to upload!", "failureIcon");
                    }
                  }, 100); */
                  //Please Comment while checkIn   
                });
          }, 0);
    }
    changeStatus() {
        let key = this.postMigModalKey;
        let wfmid = this.postWfmId;
        this.postMigModalKey = null;
        this.postWfmId =null;
        if(this.postMigStatus) {
            let comments = this.comments;
            this.showLoader = true;
            let criteria = {
                "id": key.id,
                "comments": comments,
                "status" : "Completed",
                "wfmid": wfmid
            }
            this.comments="";
            this.postMigStatus = false;
            this.showLoader = true;
            this.useCaseModalBlock.close();
        this.runtestService.postChangeStatus(this.sharedService.createServiceToken(),criteria)
            .subscribe(
                data => {
                    setTimeout(() => {
                        let jsonStatue = data.json();
                        this.showLoader = false;

                        if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
                            if(!this.sessionExpiredModalBlock){
                                console.log("inside if block");
                            this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
                            }
                        } else {

                            if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {
                                if (jsonStatue.status == "SUCCESS") {                     
                                    this.showLoader = false;
                                    this.displayModel(jsonStatue.reason, "successIcon");
                                    this.getRunTest();
                                } else {
                                    this.showLoader = false;
                                    this.displayModel(jsonStatue.reason, "failureIcon");
                                }

                            }
                        }

                    }, 1000);
                },
                error => {
                    //Please Comment while checkIn
                     /* setTimeout(() => {
                        this.showLoader = false;

                        
                        let jsonStatue = {
                          sessionId: "fa7e0c46",
                          serviceToken: "60051",
                          isProcessCompleted: false,
                          status: "SUCCESS",
                          reason: "It is Working",
                        };
                        if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {

                            this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });

                        } else {
                            if (jsonStatue.status == "SUCCESS") {                             
                                 this.showLoader = false;
                                 this.displayModel(jsonStatue.reason, "successIcon");
                                 this.getRunTest();
                            }
                            else {
                                this.showLoader = false;
                                this.displayModel(jsonStatue.reason, "failureIcon");
                            }
                        }
                    }, 1000); */
                    //Please Comment while checkIn
                });
        }
        
    }

    showStatusChangeModal(key,id) {
        this.postMigModalKey = key;
        this.postWfmId = id;
        setTimeout(() => {
            this.useCaseModalBlock = this.modalService.open(this.changeStatusModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal connectionPrompt' });
        }, 0);
    }

    uploadSiteReport(key) {
        this.runtestService.uploadSiteReportToOV(this.sharedService.createServiceToken(),key)
            .subscribe(
                data => {
                    setTimeout(() => {
                        let jsonStatue = data.json();
                        this.showLoader = false;

                        if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
                            if(!this.sessionExpiredModalBlock){
                                console.log("inside if block");
                            this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
                            }
                        } else {

                            if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {
                                if (jsonStatue.status == "SUCCESS") {                     
                                    this.showLoader = false;
                                    this.displayModel(jsonStatue.reason, "successIcon");
                                    this.getRunTest();
                                } else {
                                    this.showLoader = false;
                                    this.displayModel(jsonStatue.reason, "failureIcon");
                                }
                            }
                        }

                    }, 1000);
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

