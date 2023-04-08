import { ViewContainerRef, Input, ElementRef, Renderer, Component, OnInit, ViewChild, HostListener } from '@angular/core';
import { Router,NavigationEnd } from '@angular/router';
import { NgbModal, ModalDismissReasons, NgbTabsetConfig } from '@ng-bootstrap/ng-bootstrap';
import { PregrowService } from '../services/pregrow.service';
import { SharedService } from '../services/shared.service';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { filter, map, mergeMap } from 'rxjs/operators';
import { validator } from '../validation';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { RuntestService } from '../services/runtest.service';
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';
import { NgbPopover } from '@ng-bootstrap/ng-bootstrap';
import * as FileSaver from 'file-saver';
import * as _ from 'underscore';
import * as $ from 'jquery';
import * as jsPDF from 'jspdf';


@Component({
    selector: 'app-runtest',
    templateUrl: './runtest.component.html',
    styleUrls: ['./runtest.component.scss'],
    providers: [RuntestService]
})

export class RuntestComponent implements OnInit {
    STD_INTERVAL_DELAY:any = 30000;
    tabularResultsData:any;
    max = new Date();
    isProcessCompleted:any;
    isResultProcessCompleted:boolean;
    fromDt:any;
    statusCheck:any;
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
    ciqDetails:any;
    neDetails:any;
    testName:any;
    useCaseSO = "";
    scriptList = [];
    showOutput: boolean = false;
    scriptOutput: any;
    isItInProgress:boolean;
    useCaseValue = [];
    scriptValue = [];
    totalScriptSelected: number = 0;
    selectedLsm: any = ""
    // Following variables are used to dispaly success, confirm and failure model(s)
    showModelMessage: boolean = false;
    modelData: any;
    closeResult: string;
    currentEditRow: any;
    successModalBlock: any;
    successModalBlock1: any;
    failureMsgBlock: any;
    pswdModalBlock:any;
    viewModalBlock: any;
    message: any;
    testId: any;
    commissionType ="";
    ciqName: string = '';
    searchBlock: boolean = false;
    createNewForm: boolean = false;
     // To track activity
     searchStatus: string;
     searchCriteria: any;
     smVersion:any;
     ciqList:any;
     nameDetails:any ="";
     versionDetails:any = "";
    // To track activity
    navigationSubscription: any;
    ckeckedOrNot:boolean = false;
    rfScriptFlag: boolean = false;
    prePostAuditFlag: boolean = false;
    multipleDuo: boolean = false;
    selectedScriptName: string = '';
    selectedScript: any;
    selectedUsecase: any;
    ovUpdate: boolean = false;
    sessionExpiredModalBlock: any; // Helps to close/open the model window
    pager: any = {}; // pager Object
    //   testName : any;
    //   nwType : any;
    //   lsmVersion : any;
    //   useCase : any;
    //   lsmName:any;
    //   testDescription:any;
    dropdownList = [];
    dropdownNEList =[];

    selectedItems = [];
    selectedNEItems = [];
    dropdownSettings = {};
    dropdownSettingsNE = {};
    pgmName:any;
    smName:any;
    neName:any;
    serverInfo:any;
    runTestDetails:any;
    requestType:any;

    selSearchSMName:any;
    selSearchVer:any;
    selSearchCiqName;
    
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

    allScriptSelect: boolean = true;
    showMySites: boolean = false;
    failedScript: boolean = false;
    CurrentSeq: any;
    programChangeSubscription:any;
    networkType: any = "";
    programName: any = "";
    errorMsgResult: any;
    milestoneResult: any;
    historyResult: any;    
    userGroupDetails : any;

    ovRetryData: any;

    /* @ViewChild('p1') public p1: NgbPopover;
    @ViewChild('p2') public p2: NgbPopover; */
    p1: NgbPopover;
    p2: NgbPopover;
    wfmid: any;
    isPreCheckShow: boolean = false;
    scriptcompleted = 0;
    Scriptcount = 0;


    @HostListener('window:scroll', ['$event'])
    scrollHandler(event) {
        // console.log('open' + this.p1.isOpen());
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
                "required": false
            },
            "lsmName": {
                "required": false
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


    constructor(
        private element: ElementRef,
        private renderer: Renderer,
        private router: Router,
        private modalService: NgbModal,
        private runtestService: RuntestService,
        private sharedService: SharedService
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
        this.setMenuHighlight("createNew");
        this.searchStatus = 'load';
        this.selectedProgram = JSON.parse(sessionStorage.getItem("selectedProgram"));
        let paginationDetails = {
            "count": this.TableRowLength,
            "page": this.currentPage
        };
        this.selectedItems = [];
        this.ciqDetails = [];
        this.selectedNEItems = [];
        this.ckeckedOrNot = true;
        this.rfScriptFlag = true;
        this.prePostAuditFlag = false;
        this.multipleDuo = false;
        this.ovUpdate = (this.selectedProgram && this.selectedProgram.programName == 'VZN-4G-USM-LIVE') ? true : false;
        this.isPreCheckShow = false;
        this.userGroupDetails = JSON.parse(sessionStorage.loginDetails).userGroup;
        this.ovRetryData = {};
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
        this.ciqList = [];
        this.getRunTest();
        this.resetRunTestForm();

        /* // On every 10s refresh the table
        if(!this.interval) {
            this.interval = setInterval( () => {
                this.updateRunTestTable();
            }, 10000 );
        } */
        
        
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
                                
                                let getSelectedCIQ = JSON.parse(sessionStorage.getItem("selectedCIQ"));
                                if (this.ciqList.length > 0 && getSelectedCIQ) {
                                    this.ciqDetails = getSelectedCIQ;
                                }
                                else {
                                    this.ciqDetails = this.ciqList.length > 0 ? this.ciqList[0] : null;
                                    // Update Session storage for selectedCIQ
                                    this.sharedService.updateSelectedCIQInSessionStorage(this.ciqDetails);
                                }
                                this.getNEList(this.ciqDetails);
                                /* if (this.ciqList.length > 0) {
                                    this.ciqDetails = this.ciqList[0];
                                    this.getNEList(this.ciqDetails);
                                } */
                                // this.getUseCase();
                                this.selectedNEItems = [];
                                //this.selectedItems =[];
                                this.ckeckedOrNot = true;
                                this.rfScriptFlag = true;
                                this.prePostAuditFlag = false;
                                this.ovUpdate = (this.programName == 'VZN-4G-USM-LIVE') ? true : false;;
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
                                        }, this.STD_INTERVAL_DELAY );
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
                    // this.tableData = JSON.parse('{"sessionId":"529ed523","serviceToken":"91652","status":"SUCCESS","pageCount":3,"nwType":["type1","type2"],"lsmDetails":{"lsmName1":["v123","v321"],"lsmName2":["v456","v654"]},"runTestTableDetails":[]}');
                    //Data
                    //this.tableData = JSON.parse('{"nwTypeInfo":{"5G":{"v1.0.2":{"LSM 2":[]}},"4G":{"v1.0.1":{"LSM 1":[{"useCaseName":"user_snap_contains_true","useCaseId":5,"executionSequence":1},{"useCaseName":"user_snap_contains_fail","useCaseId":6,"executionSequence":1},{"useCaseName":"9>4096_pass","useCaseId":7,"executionSequence":1},{"useCaseName":"4096>9_pass","useCaseId":8,"executionSequence":1},{"useCaseName":"4096>9_fail","useCaseId":9,"executionSequence":1},{"useCaseName":"cli","useCaseId":10,"executionSequence":1},{"useCaseName":"cliRulecontainsPass","useCaseId":11,"executionSequence":1},{"useCaseName":"use11","useCaseId":12,"executionSequence":111},{"useCaseName":"usecase112","useCaseId":13,"executionSequence":112},{"useCaseName":"usecase113","useCaseId":14,"executionSequence":113},{"useCaseName":"usecase114","useCaseId":15,"executionSequence":114},{"useCaseName":"usecase115","useCaseId":16,"executionSequence":115},{"useCaseName":"usecase116","useCaseId":17,"executionSequence":116},{"useCaseName":"asdfasf","useCaseId":18,"executionSequence":222},{"useCaseName":"file ","useCaseId":19,"executionSequence":1},{"useCaseName":"Twofile","useCaseId":20,"executionSequence":1},{"useCaseName":"mnb","useCaseId":21,"executionSequence":1}]}}},"pageCount":4,"sessionId":"396f8b99","serviceToken":"89673","runTestTableDetails":[{"id":51,"testName":"dfsf","testDescription":"dfggf","nwType":"4G","lsmName":"LSM 1","lsmVersion":"v1.0.1","useCaseDetails":[{"useCaseName":"user_snap_contains_true","useCaseId":5,"executionSequence":2},{"useCaseName":"user_snap_contains_fail","useCaseId":6,"executionSequence":1},{"useCaseName":"9>4096_pass","useCaseId":7,"executionSequence":5}],"useCase":"Twofile","status":"PASS","userName":null,"creationDate":"2019-01-30T05:22:18.000+0000","result":null,"customerId":2},{"id":50,"testName":"gdrhd","testDescription":"rtheh","nwType":"4G","lsmName":"LSM 1","lsmVersion":"v1.0.1","useCase":"mnb","status":"FAIL","userName":null,"creationDate":"2019-01-25T11:15:21.000+0000","result":null,"customerId":2},{"id":49,"testName":"tyjkl","testDescription":"uytruy","nwType":"4G","lsmName":"LSM 1","lsmVersion":"v1.0.1","useCase":"mnb","status":"FAIL","userName":null,"creationDate":"2019-01-25T11:08:27.000+0000","result":null,"customerId":2},{"id":48,"testName":"retye","testDescription":"gyjgh","nwType":"4G","lsmName":"LSM 1","lsmVersion":"v1.0.1","useCase":"mnb","status":"FAIL","userName":null,"creationDate":"2019-01-25T11:05:58.000+0000","result":null,"customerId":2},{"id":47,"testName":"zzxcv","testDescription":"zxcv","nwType":"4G","lsmName":"LSM 1","lsmVersion":"v1.0.1","useCase":"Twofile","status":"PASS","userName":null,"creationDate":"2019-01-25T10:58:26.000+0000","result":null,"customerId":2},{"id":46,"testName":"asdfasf","testDescription":"asdfasdf","nwType":"4G","lsmName":"LSM 1","lsmVersion":"v1.0.1","useCase":"Twofile","status":"PASS","userName":null,"creationDate":"2019-01-25T10:56:42.000+0000","result":null,"customerId":2},{"id":45,"testName":"asdfsf","testDescription":"xfgedhg","nwType":"4G","lsmName":"LSM 1","lsmVersion":"v1.0.1","useCase":"file ","status":"PASS","userName":null,"creationDate":"2019-01-25T10:49:51.000+0000","result":null,"customerId":2},{"id":44,"testName":"sdrgdsf","testDescription":"sdfgfsdg","nwType":"4G","lsmName":"LSM 1","lsmVersion":"v1.0.1","useCase":"file ","status":"PASS","userName":null,"creationDate":"2019-01-25T10:27:19.000+0000","result":null,"customerId":2},{"id":43,"testName":"sdfgsdg","testDescription":"sdfgsdg","nwType":"4G","lsmName":"LSM 1","lsmVersion":"v1.0.1","useCase":"file ","status":"FAIL","userName":null,"creationDate":"2019-01-25T10:26:44.000+0000","result":null,"customerId":2},{"id":42,"testName":"asfsadf","testDescription":"asdsgf","nwType":"4G","lsmName":"LSM 1","lsmVersion":"v1.0.1","useCase":"file ","status":"PASS","userName":null,"creationDate":"2019-01-25T10:20:27.000+0000","result":null,"customerId":2}],"status":"SUCCESS"}');
                    // this.tableData = JSON.parse('{"nwTypeInfo":{"4G":{"V1.2":{"lsm3":[]}},"3G":{"V1.1":{"lsm1":[{"useCaseName":"usecase1","useCaseId":1,"executionSequence":1}],"lsm2":[]}}},"pageCount":0,"sessionId":"282a5cb7","serviceToken":"71271","runTestTableDetails":[],"status":"SUCCESS"}');
                    // this.tableData = JSON.parse('{"sessionId":"19c4c64","serviceToken":"53531","status":"SUCCESS","fromDate":"03/08/2019","pageCount":1,"isInProgress":true,"smVersion":[{"name":"1.2.3","id":1,"smNameList":[{"name":"SM","id":7},{"name":"bhuvana","id":8,"useCaseList":[{"useCaseName":"test","useCaseId":1,"executionSequence":1},{"useCaseName":"rtyy","useCaseId":2,"executionSequence":1},{"useCaseName":"2scripts","useCaseId":3,"executionSequence":1}]},{"name":"oneLSM","id":9}]}],"toDate":"03/15/2019","useCaseList":[{"useCaseName":"UC_MYSQL","useCaseId":42,"ucSleepInterval":"1","executionSequence":1,"scripts":[{"scriptId":31,"scriptSleepInterval":"1","scriptName":"pre_check.sh","scriptExeSequence":1,"useGeneratedScript":"NO"}]},{"useCaseName":"UC_Port Check","useCaseId":43,"executionSequence":1,"scripts":[{"scriptId":31,"scriptName":"pre_check.sh","scriptExeSequence":1,"scriptSleepInterval":"1","useGeneratedScript":"YES"}],"ucSleepInterval":"1"},{"useCaseName":"UC1","useCaseId":62,"executionSequence":1,"scripts":[{"scriptId":40,"scriptName":"18th March script.sh","scriptExeSequence":1,"scriptSleepInterval":"1","useGeneratedScript":"YES"}],"ucSleepInterval":"1"},{"useCaseName":"UC2","useCaseId":63,"ucSleepInterval":"1","executionSequence":1,"scripts":[{"scriptId":40,"scriptSleepInterval":"1","scriptName":"18th March script.sh","scriptExeSequence":1,"useGeneratedScript":"NO"}]},{"useCaseName":"UC3","useCaseId":64,"ucSleepInterval":"1","executionSequence":1,"scripts":[{"scriptId":40,"scriptSleepInterval":"1","scriptName":"18th March script.sh","scriptExeSequence":1,"useGeneratedScript":"YES"}]},{"useCaseName":"test","useCaseId":1,"executionSequence":1,"scripts":[{"scriptName":"sc1","scriptId":123,"scriptExeSequence":123,"scriptSleepInterval":30,"useGeneratedScript":"YES"},{"scriptName":"sc2","scriptId":234,"scriptExeSequence":234,"scriptSleepInterval":40,"useGeneratedScript":"NO"}],"ucSleepInterval":20},{"useCaseName":"rtyy","useCaseId":2,"executionSequence":1,"scripts":[{"scriptName":"sc1","scriptId":123,"scriptExeSequence":123,"useGeneratedScript":"YES"},{"scriptName":"sc2","scriptId":234,"scriptExeSequence":234,"useGeneratedScript":"NO"}]},{"useCaseName":"2scripts","useCaseId":3,"executionSequence":1,"scripts":[{"scriptName":"sc1","scriptId":123,"scriptExeSequence":123,"useGeneratedScript":"YES"},{"scriptName":"sc2","scriptId":234,"scriptExeSequence":234,"useGeneratedScript":"YES"}]}],"getCiqList":[{"id":49,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T16:24:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T16:41:30.000+0000","createdBy":"superadmin"},"ciqFileName":"UNY-NE-VZ_CIQ_Ver2.82_03122019.xlsx","ciqFilePath":"/home/user/RCT/rctsoftware/Samsung/SMART/Customer/23/PreMigration/Input/UNY-NE-VZ_CIQ_Ver2.82_03122019/CIQ/","scriptFileName":"1_58154_LEXINGTON_12_MA.zip,1_6003_networdata123.zip,1_6013_networkcohyutrr.zip,1_6203_networkconfig.zip","scriptFilePath":"/home/user/RCT/rctsoftware/Samsung/SMART/Customer/23/PreMigration/Input/UNY-NE-VZ_CIQ_Ver2.82_03122019/SCRIPT/","checklistFileName":"","checklistFilePath":"","ciqVersion":"ORIGINAL","fileSourceType":"FETCH","uploadBy":"superadmin","remarks":"","creationDate":"2019-03-12T20:38:02.000+0000"},{"id":42,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T16:24:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T16:41:30.000+0000","createdBy":"superadmin"},"ciqFileName":"UNY-NE-VZ_CIQ_Ver2.82_01282019.xlsx","ciqFilePath":"/home/user/RCT/rctsoftware/Samsung/SMART/Customer/23/PreMigration/Input/UNY-NE-VZ_CIQ_Ver2.82_01282019/CIQ/","scriptFileName":"1_1111_LEXINGTON_12_MA.zip,1_12345_LEXINGTON_12_MA.zip,1_58154_LEXINGTON_12_MA.zip","scriptFilePath":"/home/user/RCT/rctsoftware/Samsung/SMART/Customer/23/PreMigration/Input/UNY-NE-VZ_CIQ_Ver2.82_01282019/SCRIPT/","checklistFileName":"Verizon_vLSM_Migration Checklist_1.0.3.xlsx","checklistFilePath":"/home/user/RCT/rctsoftware/Samsung/SMART/Customer/23/PreMigration/Input/UNY-NE-VZ_CIQ_Ver2.82_01282019/CHECKLIST/","ciqVersion":"ORIGINAL","fileSourceType":"UPLOAD","uploadBy":"superadmin","remarks":"sa","creationDate":"2019-03-13T15:25:02.000+0000"}],"runTestTableDetails":[{"customerDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T16:24:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T16:41:30.000+0000","createdBy":"superadmin"},"migrationType":"migration","migrationSubType":"commission","checklistFileName":"Verizon_vLSM_Migration Checklist_1.0.3.xlsx","neName":"056001_BOSTON_CONVENTION_CTR_DAS2","progressStatus":"Completed","ciqName":"UNY-NE-VZ_CIQ_Ver2.82_01282019.xlsx","outputFilepath":"/home/path_of_the_file","testName":"Test1","testDescription":"sdaff","lsmName":"bhuvana","lsmVersion":"1.2.3","useCase":"test","status":"PASS","userName":null,"creationDate":"2019-03-15T17:38:16.000+0000","useCaseDetails":"test?1?1","customerId":2,"useCaseSequence":0,"id":5},{"customerDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T16:24:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T16:41:30.000+0000","createdBy":"superadmin"},"migrationType":"migration","migrationSubType":"commission","checklistFileName":"Verizon_vLSM_Migration Checklist_1.0.3.xlsx","neName":"056001_BOSTON_CONVENTION_CTR_DAS2","progressStatus":"InProgress","ciqName":"UNY-NE-VZ_CIQ_Ver2.82_01282019.xlsx","testName":"test2","testDescription":"asdf","lsmName":"bhuvana","lsmVersion":"1.2.3","useCase":"test","status":"PASS","userName":null,"creationDate":"2019-03-15T17:34:55.000+0000","useCaseDetails":"test?1?1","customerId":2,"useCaseSequence":0,"id":4},{"customerDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T16:24:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T16:41:30.000+0000","createdBy":"superadmin"},"migrationType":"migration","migrationSubType":"commission","checklistFileName":"Verizon_vLSM_Migration Checklist_1.0.3.xlsx","neName":"056001_BOSTON_CONVENTION_CTR_DAS2","progressStatus":"InProgress","ciqName":"UNY-NE-VZ_CIQ_Ver2.82_01282019.xlsx","testName":"sfd","testDescription":"safd","lsmName":"bhuvana","lsmVersion":"1.2.3","useCase":"test","status":"PASS","userName":null,"creationDate":"2019-03-15T17:30:41.000+0000","useCaseDetails":"test?1?1","customerId":2,"useCaseSequence":0,"id":3},{"customerDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T16:24:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T16:41:30.000+0000","createdBy":"superadmin"},"migrationType":"migration","migrationSubType":"commission","checklistFileName":"Verizon_vLSM_Migration Checklist_1.0.3.xlsx","neName":"056001_BOSTON_CONVENTION_CTR_DAS2","progressStatus":"Completed","ciqName":"UNY-NE-VZ_CIQ_Ver2.82_01282019.xlsx","testName":"1Test","testDescription":"sdaffas","lsmName":"bhuvana","lsmVersion":"1.2.3","useCase":"test","status":"PASS","userName":null,"creationDate":"2019-03-15T15:45:03.000+0000","useCaseDetails":"test?1?1","customerId":2,"useCaseSequence":0,"id":2},{"customerDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T16:24:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T16:41:30.000+0000","createdBy":"superadmin"},"migrationType":"migration","migrationSubType":"commission","checklistFileName":"Verizon_vLSM_Migration Checklist_1.0.3.xlsx","neName":"056001_BOSTON_CONVENTION_CTR_DAS2","progressStatus":"Completed","ciqName":"UNY-NE-VZ_CIQ_Ver2.82_01282019.xlsx","testName":"sdff","testDescription":"dsfg","lsmName":"oneLSM","lsmVersion":"1.2.3","useCase":"2scripts","status":"PASS","userName":null,"creationDate":"2019-03-15T15:37:58.000+0000","useCaseDetails":"2scripts?3?1","customerId":2,"useCaseSequence":0,"id":1}]}');
                    //this.tableData = JSON.parse('{"fromDate":"11/23/2020","pageCount":2,"smVersion":[{"name":"8.5.1","id":27,"smNameList":[{"name":"Westboro_Medium","id":83},{"name":"Windsor_Medium","id":84}]},{"name":"9.0.0","id":29,"smNameList":[{"name":"Rochester","useCaseList":[{"useCaseName":"oRan_USMAudit","useCaseId":534,"executionSequence":10},{"useCaseName":"RAN_ATP","useCaseId":601,"executionSequence":66},{"useCaseName":"RAN_ATP_Modified","useCaseId":614,"executionSequence":77},{"useCaseName":"RFusecase","useCaseId":1763,"executionSequence":6276372},{"useCaseName":"AllParams","useCaseId":531,"executionSequence":1},{"useCaseName":"vamsi","useCaseId":1053,"executionSequence":8796},{"useCaseName":"Test 11th May","useCaseId":1190,"executionSequence":3216},{"useCaseName":"vamsi usecase","useCaseId":1301,"executionSequence":64857},{"useCaseName":"28mayusecase","useCaseId":1566,"executionSequence":62713},{"useCaseName":"04_june use case","useCaseId":1615,"executionSequence":87986},{"useCaseName":"05_june use acse","useCaseId":1624,"executionSequence":546353},{"useCaseName":"15_june","useCaseId":1683,"executionSequence":657464},{"useCaseName":"zzzzzzzzz","useCaseId":1688,"executionSequence":56474},{"useCaseName":"cmmissionvamsi","useCaseId":1692,"executionSequence":6473653},{"useCaseName":"18_june","useCaseId":1697,"executionSequence":748364},{"useCaseName":"RFusecase","useCaseId":1762,"executionSequence":42746},{"useCaseName":"22_june usecase","useCaseId":1770,"executionSequence":673567},{"useCaseName":"27th July UC","useCaseId":8867,"executionSequence":9887877},{"useCaseName":"Post_Audit_11","useCaseId":10866,"executionSequence":9007}],"id":81},{"name":"Westboro_Tiny","useCaseList":[{"useCaseName":"oRan_USMAudit","useCaseId":534,"executionSequence":10},{"useCaseName":"RAN_ATP","useCaseId":601,"executionSequence":66},{"useCaseName":"RAN_ATP_Modified","useCaseId":614,"executionSequence":77},{"useCaseName":"RFusecase","useCaseId":1763,"executionSequence":6276372},{"useCaseName":"AllParams","useCaseId":531,"executionSequence":1},{"useCaseName":"vamsi","useCaseId":1053,"executionSequence":8796},{"useCaseName":"Test 11th May","useCaseId":1190,"executionSequence":3216},{"useCaseName":"vamsi usecase","useCaseId":1301,"executionSequence":64857},{"useCaseName":"28mayusecase","useCaseId":1566,"executionSequence":62713},{"useCaseName":"04_june use case","useCaseId":1615,"executionSequence":87986},{"useCaseName":"05_june use acse","useCaseId":1624,"executionSequence":546353},{"useCaseName":"15_june","useCaseId":1683,"executionSequence":657464},{"useCaseName":"zzzzzzzzz","useCaseId":1688,"executionSequence":56474},{"useCaseName":"cmmissionvamsi","useCaseId":1692,"executionSequence":6473653},{"useCaseName":"18_june","useCaseId":1697,"executionSequence":748364},{"useCaseName":"RFusecase","useCaseId":1762,"executionSequence":42746},{"useCaseName":"22_june usecase","useCaseId":1770,"executionSequence":673567},{"useCaseName":"27th July UC","useCaseId":8867,"executionSequence":9887877},{"useCaseName":"Post_Audit_11","useCaseId":10866,"executionSequence":9007}],"id":82},{"name":"TEST_NC","id":87}]},{"name":"8.5.2","id":30},{"name":"9.5.0","id":31,"smNameList":[{"name":"East_Syracuse","id":79}]},{"name":"20.A.0","id":38,"smNameList":[{"name":"Windsor_Medium","id":80},{"name":"East_Syracuse","id":93},{"name":"Rochester","id":95}]},{"name":"20.B.0","id":39,"smNameList":[{"name":"WestBorough_Medium","id":90},{"name":"Rochester_dr","id":94},{"name":"Rochester","id":96}]}],"toDate":"11/30/2020","useCaseList":[],"sessionId":"8909e5cf","serviceToken":"64284","getCiqList":[{"id":294,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2019-11-22T06:36:02.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"ciqFileName":"RatulTest-NE-VZ_CIQ_Ver3.7.999_11172020.xlsx","ciqFilePath":"Customer/34/PreMigration/Input/RatulTest-NE-VZ_CIQ_Ver3.7.999_11172020/CIQ/","scriptFileName":"61413.7z,70215.zip,70056.7z,73201.zip,70243.zip,70262.zip,70356.zip,72164.zip","scriptFilePath":"Customer/34/PreMigration/Input/RatulTest-NE-VZ_CIQ_Ver3.7.999_11172020/SCRIPT/","checklistFileName":"Verizon_USM_Migration Checklist_1.1.5(1)(5).xlsx","checklistFilePath":"Customer/34/PreMigration/Input/RatulTest-NE-VZ_CIQ_Ver3.7.999_11172020/CHECKLIST/","ciqVersion":"ORIGINAL","fileSourceType":"UPLOAD","uploadBy":"superadmin","remarks":"","creationDate":"2020-11-27T15:07:27.000+0000"}],"runTestTableDetails":[{"customerDetailsEntity":null,"migrationType":"Migration","failedScript":"true","migrationSubType":"PreCheck","checklistFileName":"Verizon_USM_Migration Checklist_1.1.5(1)(5).xlsx","neName":"070056_OKELL_ST","progressStatus":"Completed","ciqName":"RatulTest-NE-VZ_CIQ_Ver3.7.999_11172020.xlsx","outputFilepath":"Customer/34/Migration/70056/PreCheck/Output/64472_output.txt","testName":"30th_Nov_2","testDescription":"","lsmName":"Rochester","lsmVersion":"20.A.0","useCase":"CommissionScriptUsecase_70056_11302020,RFUsecase_70056_11302020","status":"Failure","userName":"superadmin","creationDate":"2020-11-30 13:34:44","useCaseDetails":null,"customerId":0,"id":980,"useCaseSequence":0,"result":"","resultFilePath":"","fromDate":null,"toDate":null,"generateScriptPath":"Customer/34/Migration/70056/PreCheck/GenerateScript/980/1_Rochester_RFUsecase7005611302020_10994_BASHRFNB-IoTAdd0-70056LockAllCells11302020_34991.sh,Customer/34/Migration/70056/PreCheck/GenerateScript/980/2_Rochester_RFUsecase7005611302020_10994_BASHRFNB-IoTAdd3-70056CellLevelChangesAWS211302020_34992.sh,Customer/34/Migration/70056/PreCheck/GenerateScript/980/3_Rochester_RFUsecase7005611302020_10994_BASHRFNB-IoTAdd5-70056-13NBR-EUTRANYANG2020030411302020_34993.sh,Customer/34/Migration/70056/PreCheck/GenerateScript/980/117_Rochester_CommissionScriptUsecase7005611302020_10995_BASHCOMMNB-IoTAddCOMM070056OKELLST11302020_34994.sh"},{"customerDetailsEntity":null,"migrationType":"Migration","failedScript":"true","migrationSubType":"PreCheck","checklistFileName":"Verizon_USM_Migration Checklist_1.1.5(1)(5).xlsx","neName":"070056_OKELL_ST","progressStatus":"Completed","ciqName":"RatulTest-NE-VZ_CIQ_Ver3.7.999_11172020.xlsx","outputFilepath":"Customer/34/Migration/70056/PreCheck/Output/71000_output.txt","testName":"30th Nov_1","testDescription":"","lsmName":"Rochester","lsmVersion":"20.A.0","useCase":"CommissionScriptUsecase_70056_11302020,RFUsecase_70056_11302020","status":"Failure","userName":"superadmin","creationDate":"2020-11-30 11:40:44","useCaseDetails":null,"customerId":0,"id":979,"useCaseSequence":0,"result":"","resultFilePath":"","fromDate":null,"toDate":null,"generateScriptPath":"Customer/34/Migration/70056/PreCheck/GenerateScript/979/1_Rochester_RFUsecase7005611302020_10994_BASHRFNB-IoTAdd0-70056LockAllCells11302020_34991.sh,Customer/34/Migration/70056/PreCheck/GenerateScript/979/2_Rochester_RFUsecase7005611302020_10994_BASHRFNB-IoTAdd3-70056CellLevelChangesAWS211302020_34992.sh,Customer/34/Migration/70056/PreCheck/GenerateScript/979/3_Rochester_RFUsecase7005611302020_10994_BASHRFNB-IoTAdd5-70056-13NBR-EUTRANYANG2020030411302020_34993.sh,Customer/34/Migration/70056/PreCheck/GenerateScript/979/117_Rochester_CommissionScriptUsecase7005611302020_10995_BASHCOMMNB-IoTAddCOMM070056OKELLST11302020_34994.sh"},{"customerDetailsEntity":null,"migrationType":"Migration","failedScript":"true","migrationSubType":"PreCheck","checklistFileName":"Verizon_USM_Migration Checklist_1.1.5(1)(5).xlsx","neName":"070056_OKELL_ST","progressStatus":"Completed","ciqName":"RatulTest-NE-VZ_CIQ_Ver3.7.999_11172020.xlsx","outputFilepath":"Customer/34/Migration/70056/PreCheck/Output/91876_output.txt","testName":"hghhhhh","testDescription":"","lsmName":"Rochester","lsmVersion":"20.A.0","useCase":"CommissionScriptUsecase_70056_11272020,RFUsecase_70056_11272020","status":"Failure","userName":"superadmin","creationDate":"2020-11-27 23:26:10","useCaseDetails":null,"customerId":0,"id":978,"useCaseSequence":0,"result":"","resultFilePath":"","fromDate":null,"toDate":null,"generateScriptPath":"Customer/34/Migration/70056/PreCheck/GenerateScript/978/1_Rochester_RFUsecase7005611272020_10989_BASHRFNB-IoTAdd0-70056LockAllCells11272020_34985.sh,Customer/34/Migration/70056/PreCheck/GenerateScript/978/2_Rochester_RFUsecase7005611272020_10989_BASHRFNB-IoTAdd3-70056CellLevelChangesAWS211272020_34986.sh,Customer/34/Migration/70056/PreCheck/GenerateScript/978/3_Rochester_RFUsecase7005611272020_10989_BASHRFNB-IoTAdd5-70056-13NBR-EUTRANYANG2020030411272020_34984.sh,Customer/34/Migration/70056/PreCheck/GenerateScript/978/117_Rochester_CommissionScriptUsecase7005611272020_10990_BASHCOMMNB-IoTAddCOMM070056OKELLST11272020_34987.sh"},{"customerDetailsEntity":null,"migrationType":"Migration","failedScript":"true","migrationSubType":"PreCheck","checklistFileName":"Verizon_USM_Migration Checklist_1.1.5(1)(5).xlsx","neName":"070056_OKELL_ST","progressStatus":"Completed","ciqName":"RatulTest-NE-VZ_CIQ_Ver3.7.999_11172020.xlsx","outputFilepath":"Customer/34/Migration/70056/PreCheck/Output/78971_output.txt","testName":"27th_3","testDescription":"","lsmName":"Rochester","lsmVersion":"20.A.0","useCase":"CommissionScriptUsecase_70056_11272020,RFUsecase_70056_11272020","status":"Failure","userName":"superadmin","creationDate":"2020-11-27 21:57:32","useCaseDetails":null,"customerId":0,"id":977,"useCaseSequence":0,"result":"","resultFilePath":"","fromDate":null,"toDate":null,"generateScriptPath":"Customer/34/Migration/70056/PreCheck/GenerateScript/977/1_Rochester_RFUsecase7005611272020_10989_BASHRFNB-IoTAdd0-70056LockAllCells11272020_34985.sh,Customer/34/Migration/70056/PreCheck/GenerateScript/977/2_Rochester_RFUsecase7005611272020_10989_BASHRFNB-IoTAdd3-70056CellLevelChangesAWS211272020_34986.sh,Customer/34/Migration/70056/PreCheck/GenerateScript/977/3_Rochester_RFUsecase7005611272020_10989_BASHRFNB-IoTAdd5-70056-13NBR-EUTRANYANG2020030411272020_34984.sh,Customer/34/Migration/70056/PreCheck/GenerateScript/977/117_Rochester_CommissionScriptUsecase7005611272020_10990_BASHCOMMNB-IoTAddCOMM070056OKELLST11272020_34987.sh"},{"customerDetailsEntity":null,"migrationType":"Migration","failedScript":"true","migrationSubType":"PreCheck","checklistFileName":"Verizon_USM_Migration Checklist_1.1.5(1)(5).xlsx","neName":"070056_OKELL_ST","progressStatus":"Completed","ciqName":"RatulTest-NE-VZ_CIQ_Ver3.7.999_11172020.xlsx","outputFilepath":"Customer/34/Migration/70056/PreCheck/Output/72694_output.txt","testName":"27th_2","testDescription":"","lsmName":"Rochester","lsmVersion":"20.A.0","useCase":"CommissionScriptUsecase_70056_11272020,RFUsecase_70056_11272020","status":"Failure","userName":"superadmin","creationDate":"2020-11-27 20:58:59","useCaseDetails":null,"customerId":0,"id":976,"useCaseSequence":0,"result":"","resultFilePath":"","fromDate":null,"toDate":null,"generateScriptPath":"Customer/34/Migration/70056/PreCheck/GenerateScript/976/1_Rochester_RFUsecase7005611272020_10989_BASHRFNB-IoTAdd0-70056LockAllCells11272020_34985.sh,Customer/34/Migration/70056/PreCheck/GenerateScript/976/2_Rochester_RFUsecase7005611272020_10989_BASHRFNB-IoTAdd3-70056CellLevelChangesAWS211272020_34986.sh,Customer/34/Migration/70056/PreCheck/GenerateScript/976/3_Rochester_RFUsecase7005611272020_10989_BASHRFNB-IoTAdd5-70056-13NBR-EUTRANYANG2020030411272020_34984.sh,Customer/34/Migration/70056/PreCheck/GenerateScript/976/117_Rochester_CommissionScriptUsecase7005611272020_10990_BASHCOMMNB-IoTAddCOMM070056OKELLST11272020_34987.sh"},{"customerDetailsEntity":null,"migrationType":"Migration","failedScript":"true","migrationSubType":"PreCheck","checklistFileName":"Verizon_USM_Migration Checklist_1.1.5(1)(5).xlsx","neName":"070056_OKELL_ST","progressStatus":"Completed","ciqName":"RatulTest-NE-VZ_CIQ_Ver3.7.999_11172020.xlsx","outputFilepath":"Customer/34/Migration/70056/PreCheck/Output/52375_output.txt","testName":"Ratz_27th_Nov1","testDescription":"","lsmName":"Rochester","lsmVersion":"20.A.0","useCase":"CommissionScriptUsecase_70056_11272020,RFUsecase_70056_11272020","status":"Failure","userName":"superadmin","creationDate":"2020-11-27 20:40:00","useCaseDetails":null,"customerId":0,"id":975,"useCaseSequence":0,"result":"","resultFilePath":"","fromDate":null,"toDate":null,"generateScriptPath":"Customer/34/Migration/70056/PreCheck/GenerateScript/975/1_Rochester_RFUsecase7005611272020_10989_BASHRFNB-IoTAdd0-70056LockAllCells11272020_34985.sh,Customer/34/Migration/70056/PreCheck/GenerateScript/975/2_Rochester_RFUsecase7005611272020_10989_BASHRFNB-IoTAdd3-70056CellLevelChangesAWS211272020_34986.sh,Customer/34/Migration/70056/PreCheck/GenerateScript/975/3_Rochester_RFUsecase7005611272020_10989_BASHRFNB-IoTAdd5-70056-13NBR-EUTRANYANG2020030411272020_34984.sh,Customer/34/Migration/70056/PreCheck/GenerateScript/975/117_Rochester_CommissionScriptUsecase7005611272020_10990_BASHCOMMNB-IoTAddCOMM070056OKELLST11272020_34987.sh"},{"customerDetailsEntity":null,"migrationType":"Migration","failedScript":"true","migrationSubType":"PreCheck","checklistFileName":"Verizon_USM_Migration Checklist_1.1.5(1)(5).xlsx","neName":"070056_OKELL_ST","progressStatus":"Completed","ciqName":"RatulTest-NE-VZ_CIQ_Ver3.7.999_11172020.xlsx","outputFilepath":"Customer/34/Migration/70056/PreCheck/Output/70979_output.txt","testName":"jhjdj","testDescription":"","lsmName":"Rochester","lsmVersion":"20.A.0","useCase":"CommissionScriptUsecase_70056_11272020,RFUsecase_70056_11272020","status":"Failure","userName":"superadmin","creationDate":"2020-11-27 20:17:43","useCaseDetails":null,"customerId":0,"id":974,"useCaseSequence":0,"result":"","resultFilePath":"","fromDate":null,"toDate":null,"generateScriptPath":"Customer/34/Migration/70056/PreCheck/GenerateScript/974/1_Rochester_RFUsecase7005611272020_10989_BASHRFNB-IoTAdd0-70056LockAllCells11272020_34985.sh,Customer/34/Migration/70056/PreCheck/GenerateScript/974/2_Rochester_RFUsecase7005611272020_10989_BASHRFNB-IoTAdd3-70056CellLevelChangesAWS211272020_34986.sh,Customer/34/Migration/70056/PreCheck/GenerateScript/974/3_Rochester_RFUsecase7005611272020_10989_BASHRFNB-IoTAdd5-70056-13NBR-EUTRANYANG2020030411272020_34984.sh,Customer/34/Migration/70056/PreCheck/GenerateScript/974/117_Rochester_CommissionScriptUsecase7005611272020_10990_BASHCOMMNB-IoTAddCOMM070056OKELLST11272020_34987.sh"},{"customerDetailsEntity":null,"migrationType":"Migration","failedScript":"false","migrationSubType":"PreCheck","checklistFileName":"Verizon_USM_Migration Checklist_1.1.5(1)(5).xlsx","neName":"070056_OKELL_ST","progressStatus":"Completed","ciqName":"RatulTest-NE-VZ_CIQ_Ver3.7.999_11172020.xlsx","outputFilepath":"Customer/34/Migration/70056/PreCheck/Output/66620_output.txt","testName":"jdfhjhjg","testDescription":"","lsmName":"Rochester","lsmVersion":"20.B.0","useCase":"CommissionScriptUsecase_70056_11262020,RFUsecase_70056_11262020","status":"Failure","userName":"superadmin","creationDate":"2020-11-26 20:01:26","useCaseDetails":null,"customerId":0,"id":972,"useCaseSequence":0,"result":"","resultFilePath":"","fromDate":null,"toDate":null,"generateScriptPath":"Customer/34/Migration/70056/PreCheck/GenerateScript/972/1_Rochester_RFUsecase7005611262020_10984_BASHRFNB-IoTAdd0-70056LockAllCells11262020_34977.sh,Customer/34/Migration/70056/PreCheck/GenerateScript/972/2_Rochester_RFUsecase7005611262020_10984_BASHRFNB-IoTAdd3-70056CellLevelChangesAWS211262020_34979.sh,Customer/34/Migration/70056/PreCheck/GenerateScript/972/3_Rochester_RFUsecase7005611262020_10984_BASHRFNB-IoTAdd5-70056-13NBR-EUTRANYANG2020030411262020_34978.sh,Customer/34/Migration/70056/PreCheck/GenerateScript/972/117_Rochester_CommissionScriptUsecase7005611262020_10985_BASHCOMMNB-IoTAddCOMM070056OKELLST11262020_34980.sh"},{"customerDetailsEntity":null,"migrationType":"Migration","failedScript":"false","migrationSubType":"PreCheck","checklistFileName":"Verizon_USM_Migration Checklist_1.1.5(1)(5).xlsx","neName":"070056_OKELL_ST","progressStatus":"Completed","ciqName":"RatulTest-NE-VZ_CIQ_Ver3.7.999_11172020.xlsx","outputFilepath":"Customer/34/Migration/70056/PreCheck/Output/84974_output.txt","testName":"kjkjkjkjkjk","testDescription":"","lsmName":"Rochester","lsmVersion":"20.B.0","useCase":"CommissionScriptUsecase_70056_11252020,RFUsecase_70056_11252020","status":"Failure","userName":"superadmin","creationDate":"2020-11-25 20:10:23","useCaseDetails":null,"customerId":0,"id":969,"useCaseSequence":0,"result":"","resultFilePath":"","fromDate":null,"toDate":null,"generateScriptPath":"Customer/34/Migration/70056/PreCheck/GenerateScript/969/1_Rochester_RFUsecase7005611252020_10974_BASHRFNB-IoTAdd0-70056LockAllCells11252020_34967.sh,Customer/34/Migration/70056/PreCheck/GenerateScript/969/2_Rochester_RFUsecase7005611252020_10974_BASHRFNB-IoTAdd3-70056CellLevelChangesAWS211252020_34966.sh,Customer/34/Migration/70056/PreCheck/GenerateScript/969/3_Rochester_RFUsecase7005611252020_10974_BASHRFNB-IoTAdd5-70056-13NBR-EUTRANYANG2020030411252020_34968.sh"},{"customerDetailsEntity":null,"migrationType":"Migration","failedScript":"true","migrationSubType":"PreCheck","checklistFileName":"Verizon_USM_Migration Checklist_1.1.5(1)(5).xlsx","neName":"070056_OKELL_ST","progressStatus":"Completed","ciqName":"RatulTest-NE-VZ_CIQ_Ver3.7.999_11172020.xlsx","outputFilepath":"Customer/34/Migration/70056/PreCheck/Output/82714_output.txt","testName":"jdhfhhgdfhgdfh","testDescription":"","lsmName":"Rochester","lsmVersion":"20.B.0","useCase":"CommissionScriptUsecase_70056_11252020,RFUsecase_70056_11252020","status":"Failure","userName":"superadmin","creationDate":"2020-11-25 18:25:02","useCaseDetails":null,"customerId":0,"id":963,"useCaseSequence":0,"result":"","resultFilePath":"","fromDate":null,"toDate":null,"generateScriptPath":"Customer/34/Migration/70056/PreCheck/GenerateScript/963/1_Rochester_RFUsecase7005611252020_10893_BASHRFNB-IoTAdd0-70056LockAllCells11252020_34655.sh,Customer/34/Migration/70056/PreCheck/GenerateScript/963/2_Rochester_RFUsecase7005611252020_10893_BASHRFNB-IoTAdd3-70056CellLevelChangesAWS211252020_34654.sh"}],"isInProgress":false,"status":"SUCCESS"}');
                    this.tableData = JSON.parse('{"fromDate":"06/14/2022","pageCount":7,"smVersion":[{"name":"20.A.0","id":44,"smNameList":[{"name":"Tiny","useCaseList":[{"useCaseName":"AU_Usecase","useCaseId":16287,"executionSequence":12121},{"useCaseName":"AU_usecase1","useCaseId":16288,"executionSequence":1342},{"useCaseName":"NEW_DSS","useCaseId":21099,"executionSequence":54321},{"useCaseName":"Acpf","useCaseId":10243,"executionSequence":1222},{"useCaseName":"DSS_ACPF","useCaseId":10355,"executionSequence":15},{"useCaseName":"DSS_all","useCaseId":10356,"executionSequence":16},{"useCaseName":"Dss_New","useCaseId":10675,"executionSequence":140},{"useCaseName":"Dss_acpf2","useCaseId":10684,"executionSequence":22},{"useCaseName":"Dss_vdu","useCaseId":10686,"executionSequence":26},{"useCaseName":"DSS_ACPFa1a2","useCaseId":10688,"executionSequence":43},{"useCaseName":"Dss_enb","useCaseId":10739,"executionSequence":187}],"id":102},{"name":"Medium","useCaseList":[{"useCaseName":"AU_Usecase","useCaseId":16287,"executionSequence":12121},{"useCaseName":"AU_usecase1","useCaseId":16288,"executionSequence":1342},{"useCaseName":"NEW_DSS","useCaseId":21099,"executionSequence":54321},{"useCaseName":"Acpf","useCaseId":10243,"executionSequence":1222},{"useCaseName":"DSS_ACPF","useCaseId":10355,"executionSequence":15},{"useCaseName":"DSS_all","useCaseId":10356,"executionSequence":16},{"useCaseName":"Dss_New","useCaseId":10675,"executionSequence":140},{"useCaseName":"Dss_acpf2","useCaseId":10684,"executionSequence":22},{"useCaseName":"Dss_vdu","useCaseId":10686,"executionSequence":26},{"useCaseName":"DSS_ACPFa1a2","useCaseId":10688,"executionSequence":43},{"useCaseName":"Dss_enb","useCaseId":10739,"executionSequence":187}],"id":103},{"name":"4G_DSS","id":119}]},{"name":"21.D.0","id":55,"smNameList":[{"name":"Ultane","useCaseList":[{"useCaseName":"AU_Usecase","useCaseId":16287,"executionSequence":12121},{"useCaseName":"AU_usecase1","useCaseId":16288,"executionSequence":1342},{"useCaseName":"NEW_DSS","useCaseId":21099,"executionSequence":54321},{"useCaseName":"Acpf","useCaseId":10243,"executionSequence":1222},{"useCaseName":"DSS_ACPF","useCaseId":10355,"executionSequence":15},{"useCaseName":"DSS_all","useCaseId":10356,"executionSequence":16},{"useCaseName":"Dss_New","useCaseId":10675,"executionSequence":140},{"useCaseName":"Dss_acpf2","useCaseId":10684,"executionSequence":22},{"useCaseName":"Dss_vdu","useCaseId":10686,"executionSequence":26},{"useCaseName":"DSS_ACPFa1a2","useCaseId":10688,"executionSequence":43},{"useCaseName":"Dss_enb","useCaseId":10739,"executionSequence":187}],"id":101}]}],"toDate":"06/21/2022","useCaseList":[],"sessionId":"3d5ed6b5","serviceToken":"95310","getCiqList":[{"id":352,"programDetailsEntity":{"id":42,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-DSS","programDescription":"DSS","status":"Active","creationDate":"2022-06-13T05:17:59.000+0000","createdBy":"superadmin","sourceProgramId":null,"sourceprogramName":null},"ciqFileName":"VZW_TRI_DSS_CIQ_v2.38_20220126.xlsx","ciqFilePath":"Customer/42/PreMigration/Input/VZW_TRI_DSS_CIQ_v2.38_20220126/CIQ/","scriptFileName":"8891002001.zip,8891002141.zip,8891002123.zip","scriptFilePath":"Customer/42/PreMigration/Input/VZW_TRI_DSS_CIQ_v2.38_20220126/SCRIPT/","checklistFileName":"Verizon_USM_Migration Checklist_1.1.5.xlsx","checklistFilePath":"Customer/42/PreMigration/Input/VZW_TRI_DSS_CIQ_v2.38_20220126/CHECKLIST/","ciqVersion":"ORIGINAL","fileSourceType":"UPLOAD","uploadBy":"superadmin","remarks":"","creationDate":"2022-06-20T18:01:23.000+0000","fetchInfo":null}],"runTestTableDetails":[{"customerDetailsEntity":null,"migrationType":"Migration","failedScript":"true","migrationSubType":"PreCheck","checklistFileName":"","neName":"8891002001_5GDU_WhitehouseStation-WLMT","progressStatus":"Completed","ciqName":"VZW_TRI_DSS_CIQ_v2.38_20220126.xlsx","outputFilepath":"Customer/42/Migration/8891002001/PreCheck/Output/57461_output.txt","testName":"WFM_8891002001_5GDU_WhitehouseStation-WLMT_06212022_11_26_02","testDescription":"Ran from WFM","lsmName":"Ultane","lsmVersion":"21.D.0","useCase":"Pre-Check_RF_Scripts_Usecase8891002001_06212022,","status":"Failure","ovUpdateStatus":"Failure","userName":"superadmin","creationDate":"2022-06-21 11:28:56","useCaseDetails":null,"customerId":0,"id":4072,"wfmid":1401,"useCaseSequence":0,"result":"","resultFilePath":"","fromDate":null,"toDate":null,"generateScriptPath":"Customer/42/Migration/8891002001/PreCheck/GenerateScript/4072/41520_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_4152025095162291vDU0250291dss-end-point-to-enb-20220304-1915Pre-CheckRFUsecase889100200106212022_98636.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4072/41530_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_4153025095162291vDU0250291spectrum-sharing-scheduler-20220304-1915Pre-CheckRFUsecase889100200106212022_98646.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4072/41540_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_4154025095162291vDU0250291dss-interface-20220304-1915Pre-CheckRFUsecase889100200106212022_98647.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4072/41550_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_4155025095162291vDU0250291vdu-cell-lock-20220304-1915Pre-CheckRFUsecase889100200106212022_98622.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4072/51100_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_51100250291001FSU0250291ecpri-port-to-vdu-20220304-1915Pre-CheckRFUsecase889100200106212022_98635.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4072/51200_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_51200250291001FSU0250291ecpri-interface0-20220304-1915Pre-CheckRFUsecase889100200106212022_98625.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4072/51300_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_51300250291001FSU0250291ecpri-interface1-20220304-1915Pre-CheckRFUsecase889100200106212022_98638.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4072/51400_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_51400250291001FSU0250291mplane-ip-for-ecpri-20220304-1915Pre-CheckRFUsecase889100200106212022_98645.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4072/51500_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_51500250291001FSU0250291o-du-port-20220304-1915Pre-CheckRFUsecase889100200106212022_98621.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4072/51600_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_51600250291001FSU0250291mplane-ip-for-vdu-20220304-1915Pre-CheckRFUsecase889100200106212022_98628.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4072/52100_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_52100250951600ACPF0250291nr-intra-nbr-20220304-1915Pre-CheckRFUsecase889100200106212022_98626.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4072/52310_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_52310250951600ACPF0250291index3-5-for-a1-20220304-1915Pre-CheckRFUsecase889100200106212022_98640.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4072/52320_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_52320250951600ACPF0250291index4-6-for-a2-20220304-1915Pre-CheckRFUsecase889100200106212022_98624.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4072/52330_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_52330250951600ACPF0250291index1-for-a2-20220304-1915Pre-CheckRFUsecase889100200106212022_98643.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4072/52340_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_52340250951600ACPF0250291a3-timer-for-index2-20220304-1915Pre-CheckRFUsecase889100200106212022_98641.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4072/52350_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_52350250951600ACPF0250291a1-threshold-for-index3-5-20220304-1915Pre-CheckRFUsecase889100200106212022_98632.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4072/52360_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_52360250951600ACPF0250291a2-threshold-for-a4-6-20220304-1915Pre-CheckRFUsecase889100200106212022_98637.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4072/52370_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_52370250951600ACPF0250291a2-threshold-for-index1-20220304-1915Pre-CheckRFUsecase889100200106212022_98620.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4072/53200_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_53200250951600ACPF0250291cu-colo-20220304-1915Pre-CheckRFUsecase889100200106212022_98633.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4072/53300_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_53300250951600ACPF0250291user-inactivity-timer-20220304-1915Pre-CheckRFUsecase889100200106212022_98644.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4072/54200_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_54200250951610AUPF0250291nr-secondary-rat-usage-report-20220304-1915Pre-CheckRFUsecase889100200106212022_98619.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4072/55100_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_5510025095162291vDU0250291qos-scheduling-conf-20220304-1915Pre-CheckRFUsecase889100200106212022_98629.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4072/55110_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_5511025095162291vDU0250291dmrs-pdsch-fdm-20220304-1915Pre-CheckRFUsecase889100200106212022_98631.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4072/55200_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_5520025095162291vDU0250291pdsch-pusch-resource-allocation-20220304-1915Pre-CheckRFUsecase889100200106212022_98630.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4072/55300_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_5530025095162291vDU0250291harq-support-20220304-1915Pre-CheckRFUsecase889100200106212022_98648.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4072/55400_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_5540025095162291vDU0250291ul-adpation-config-20220304-1915Pre-CheckRFUsecase889100200106212022_98642.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4072/55500_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_5550025095162291vDU0250291ptp-holdover-timer-threshold-20220304-1915Pre-CheckRFUsecase889100200106212022_98634.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4072/55720_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_5572025095162291vDU0250291prach-config-20220304-1915Pre-CheckRFUsecase889100200106212022_98627.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4072/55730_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_5573025095162291vDU0250291ssb-conf-20220304-1915Pre-CheckRFUsecase889100200106212022_98623.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4072/55740_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_5574025095162291vDU0250291ssb-conf-20220304-1915Pre-CheckRFUsecase889100200106212022_98639.sh"},{"customerDetailsEntity":null,"migrationType":"Migration","failedScript":"true","migrationSubType":"PreCheck","checklistFileName":"Verizon_USM_Migration Checklist_1.1.5.xlsx","neName":"8891002001_5GDU_WhitehouseStation-WLMT","progressStatus":"Completed","ciqName":"VZW_TRI_DSS_CIQ_v2.38_20220126.xlsx","outputFilepath":"Customer/42/Migration/8891002001/PreCheck/Output/83673_output.txt","testName":"fghfhfg","testDescription":"","lsmName":"Ultane","lsmVersion":"21.D.0","useCase":"Pre-Check_RF_Scripts_Usecase8891002001_06212022","status":"Failure","ovUpdateStatus":"Success","userName":"superadmin","creationDate":"2022-06-21 11:19:01","useCaseDetails":null,"customerId":0,"id":4071,"wfmid":0,"useCaseSequence":0,"result":"","resultFilePath":"","fromDate":null,"toDate":null,"generateScriptPath":"Customer/42/Migration/8891002001/PreCheck/GenerateScript/4071/41520_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_4152025095162291vDU0250291dss-end-point-to-enb-20220304-1915Pre-CheckRFUsecase889100200106212022_98636.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4071/41530_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_4153025095162291vDU0250291spectrum-sharing-scheduler-20220304-1915Pre-CheckRFUsecase889100200106212022_98646.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4071/41540_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_4154025095162291vDU0250291dss-interface-20220304-1915Pre-CheckRFUsecase889100200106212022_98647.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4071/41550_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_4155025095162291vDU0250291vdu-cell-lock-20220304-1915Pre-CheckRFUsecase889100200106212022_98622.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4071/51100_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_51100250291001FSU0250291ecpri-port-to-vdu-20220304-1915Pre-CheckRFUsecase889100200106212022_98635.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4071/51200_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_51200250291001FSU0250291ecpri-interface0-20220304-1915Pre-CheckRFUsecase889100200106212022_98625.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4071/51300_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_51300250291001FSU0250291ecpri-interface1-20220304-1915Pre-CheckRFUsecase889100200106212022_98638.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4071/51400_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_51400250291001FSU0250291mplane-ip-for-ecpri-20220304-1915Pre-CheckRFUsecase889100200106212022_98645.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4071/51500_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_51500250291001FSU0250291o-du-port-20220304-1915Pre-CheckRFUsecase889100200106212022_98621.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4071/51600_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_51600250291001FSU0250291mplane-ip-for-vdu-20220304-1915Pre-CheckRFUsecase889100200106212022_98628.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4071/52100_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_52100250951600ACPF0250291nr-intra-nbr-20220304-1915Pre-CheckRFUsecase889100200106212022_98626.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4071/52310_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_52310250951600ACPF0250291index3-5-for-a1-20220304-1915Pre-CheckRFUsecase889100200106212022_98640.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4071/52320_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_52320250951600ACPF0250291index4-6-for-a2-20220304-1915Pre-CheckRFUsecase889100200106212022_98624.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4071/52330_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_52330250951600ACPF0250291index1-for-a2-20220304-1915Pre-CheckRFUsecase889100200106212022_98643.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4071/52340_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_52340250951600ACPF0250291a3-timer-for-index2-20220304-1915Pre-CheckRFUsecase889100200106212022_98641.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4071/52350_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_52350250951600ACPF0250291a1-threshold-for-index3-5-20220304-1915Pre-CheckRFUsecase889100200106212022_98632.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4071/52360_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_52360250951600ACPF0250291a2-threshold-for-a4-6-20220304-1915Pre-CheckRFUsecase889100200106212022_98637.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4071/52370_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_52370250951600ACPF0250291a2-threshold-for-index1-20220304-1915Pre-CheckRFUsecase889100200106212022_98620.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4071/53200_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_53200250951600ACPF0250291cu-colo-20220304-1915Pre-CheckRFUsecase889100200106212022_98633.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4071/53300_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_53300250951600ACPF0250291user-inactivity-timer-20220304-1915Pre-CheckRFUsecase889100200106212022_98644.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4071/54200_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_54200250951610AUPF0250291nr-secondary-rat-usage-report-20220304-1915Pre-CheckRFUsecase889100200106212022_98619.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4071/55100_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_5510025095162291vDU0250291qos-scheduling-conf-20220304-1915Pre-CheckRFUsecase889100200106212022_98629.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4071/55110_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_5511025095162291vDU0250291dmrs-pdsch-fdm-20220304-1915Pre-CheckRFUsecase889100200106212022_98631.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4071/55200_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_5520025095162291vDU0250291pdsch-pusch-resource-allocation-20220304-1915Pre-CheckRFUsecase889100200106212022_98630.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4071/55300_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_5530025095162291vDU0250291harq-support-20220304-1915Pre-CheckRFUsecase889100200106212022_98648.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4071/55400_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_5540025095162291vDU0250291ul-adpation-config-20220304-1915Pre-CheckRFUsecase889100200106212022_98642.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4071/55500_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_5550025095162291vDU0250291ptp-holdover-timer-threshold-20220304-1915Pre-CheckRFUsecase889100200106212022_98634.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4071/55720_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_5572025095162291vDU0250291prach-config-20220304-1915Pre-CheckRFUsecase889100200106212022_98627.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4071/55730_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_5573025095162291vDU0250291ssb-conf-20220304-1915Pre-CheckRFUsecase889100200106212022_98623.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4071/55740_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26713_5574025095162291vDU0250291ssb-conf-20220304-1915Pre-CheckRFUsecase889100200106212022_98639.sh"},{"customerDetailsEntity":null,"migrationType":"Migration","failedScript":"true","migrationSubType":"PreCheck","checklistFileName":"","neName":"8891002001_5GDU_WhitehouseStation-WLMT","progressStatus":"Completed","ciqName":"VZW_TRI_DSS_CIQ_v2.38_20220126.xlsx","outputFilepath":"Customer/42/Migration/8891002001/PreCheck/Output/66520_output.txt","testName":"WFM_8891002001_5GDU_WhitehouseStation-WLMT_06212022_00_22_36","testDescription":"Ran from WFM","lsmName":"Ultane","lsmVersion":"21.D.0","useCase":"Pre-Check_RF_Scripts_Usecase8891002001_06212022,","status":"Failure","userName":"superadmin","creationDate":"2022-06-21 00:23:10","useCaseDetails":null,"customerId":0,"id":4049,"wfmid":1392,"useCaseSequence":0,"result":"","resultFilePath":"","fromDate":null,"toDate":null,"generateScriptPath":"Customer/42/Migration/8891002001/PreCheck/GenerateScript/4049/52370_Ultane_Pre-CheckRFScriptsUsecase889100200106212022_26692_52370250951600ACPF0250291a2-threshold-for-index1-20220304-1915Pre-CheckRFUsecase889100200106212022_98389.sh"},{"customerDetailsEntity":null,"migrationType":"Migration","failedScript":"true","migrationSubType":"PreCheck","checklistFileName":"Verizon_USM_Migration Checklist_1.1.5.xlsx","neName":"8891002141_5GDU_RaritanValleyCommunityCollege-WLMT","progressStatus":"Completed","ciqName":"VZW_TRI_DSS_CIQ_v2.38_20220126.xlsx","outputFilepath":"Customer/42/Migration/8891002141/PreCheck/Output/78897_output.txt","testName":"WFM_8891002141_5GDU_RaritanValleyCommunityCollege-WLMT_06202022_23_32_26","testDescription":"Ran from WFM","lsmName":"4G_DSS","lsmVersion":"20.A.0","useCase":"Pre-Check_RF_Scripts_Usecase8891002141_06202022,","status":"Failure","userName":"superadmin","creationDate":"2022-06-20 23:35:09","useCaseDetails":null,"customerId":0,"id":4046,"wfmid":1396,"useCaseSequence":0,"result":"","resultFilePath":"","fromDate":null,"toDate":null,"generateScriptPath":"Customer/42/Migration/8891002141/PreCheck/GenerateScript/4046/41520_4GDSS_Pre-CheckRFScriptsUsecase889100214106202022_26664_4152025095162291vDU0250291dss-end-point-to-enb-20220304-1915Pre-CheckRFUsecase889100214106202022_98083.sh,Customer/42/Migration/8891002141/PreCheck/GenerateScript/4046/41530_4GDSS_Pre-CheckRFScriptsUsecase889100214106202022_26664_4153025095162291vDU0250291spectrum-sharing-scheduler-20220304-1915Pre-CheckRFUsecase889100214106202022_98106.sh,Customer/42/Migration/8891002141/PreCheck/GenerateScript/4046/41540_4GDSS_Pre-CheckRFScriptsUsecase889100214106202022_26664_4154025095162291vDU0250291dss-interface-20220304-1915Pre-CheckRFUsecase889100214106202022_98082.sh,Customer/42/Migration/8891002141/PreCheck/GenerateScript/4046/41550_4GDSS_Pre-CheckRFScriptsUsecase889100214106202022_26664_4155025095162291vDU0250291vdu-cell-lock-20220304-1915Pre-CheckRFUsecase889100214106202022_98100.sh,Customer/42/Migration/8891002141/PreCheck/GenerateScript/4046/51100_4GDSS_Pre-CheckRFScriptsUsecase889100214106202022_26664_51100250291001FSU0250291ecpri-port-to-vdu-20220304-1915Pre-CheckRFUsecase889100214106202022_98091.sh,Customer/42/Migration/8891002141/PreCheck/GenerateScript/4046/51200_4GDSS_Pre-CheckRFScriptsUsecase889100214106202022_26664_51200250291001FSU0250291ecpri-interface0-20220304-1915Pre-CheckRFUsecase889100214106202022_98105.sh,Customer/42/Migration/8891002141/PreCheck/GenerateScript/4046/51300_4GDSS_Pre-CheckRFScriptsUsecase889100214106202022_26664_51300250291001FSU0250291ecpri-interface1-20220304-1915Pre-CheckRFUsecase889100214106202022_98101.sh,Customer/42/Migration/8891002141/PreCheck/GenerateScript/4046/51400_4GDSS_Pre-CheckRFScriptsUsecase889100214106202022_26664_51400250291001FSU0250291mplane-ip-for-ecpri-20220304-1915Pre-CheckRFUsecase889100214106202022_98096.sh,Customer/42/Migration/8891002141/PreCheck/GenerateScript/4046/51500_4GDSS_Pre-CheckRFScriptsUsecase889100214106202022_26664_51500250291001FSU0250291o-du-port-20220304-1915Pre-CheckRFUsecase889100214106202022_98085.sh,Customer/42/Migration/8891002141/PreCheck/GenerateScript/4046/51600_4GDSS_Pre-CheckRFScriptsUsecase889100214106202022_26664_51600250291001FSU0250291mplane-ip-for-vdu-20220304-1915Pre-CheckRFUsecase889100214106202022_98099.sh,Customer/42/Migration/8891002141/PreCheck/GenerateScript/4046/52100_4GDSS_Pre-CheckRFScriptsUsecase889100214106202022_26664_52100250951600ACPF0250291nr-intra-nbr-20220304-1915Pre-CheckRFUsecase889100214106202022_98094.sh,Customer/42/Migration/8891002141/PreCheck/GenerateScript/4046/52310_4GDSS_Pre-CheckRFScriptsUsecase889100214106202022_26664_52310250951600ACPF0250291index3-5-for-a1-20220304-1915Pre-CheckRFUsecase889100214106202022_98080.sh,Customer/42/Migration/8891002141/PreCheck/GenerateScript/4046/52320_4GDSS_Pre-CheckRFScriptsUsecase889100214106202022_26664_52320250951600ACPF0250291index4-6-for-a2-20220304-1915Pre-CheckRFUsecase889100214106202022_98092.sh,Customer/42/Migration/8891002141/PreCheck/GenerateScript/4046/52330_4GDSS_Pre-CheckRFScriptsUsecase889100214106202022_26664_52330250951600ACPF0250291index1-for-a2-20220304-1915Pre-CheckRFUsecase889100214106202022_98108.sh,Customer/42/Migration/8891002141/PreCheck/GenerateScript/4046/52340_4GDSS_Pre-CheckRFScriptsUsecase889100214106202022_26664_52340250951600ACPF0250291a3-timer-for-index2-20220304-1915Pre-CheckRFUsecase889100214106202022_98098.sh,Customer/42/Migration/8891002141/PreCheck/GenerateScript/4046/52350_4GDSS_Pre-CheckRFScriptsUsecase889100214106202022_26664_52350250951600ACPF0250291a1-threshold-for-index3-5-20220304-1915Pre-CheckRFUsecase889100214106202022_98084.sh,Customer/42/Migration/8891002141/PreCheck/GenerateScript/4046/52360_4GDSS_Pre-CheckRFScriptsUsecase889100214106202022_26664_52360250951600ACPF0250291a2-threshold-for-a4-6-20220304-1915Pre-CheckRFUsecase889100214106202022_98086.sh,Customer/42/Migration/8891002141/PreCheck/GenerateScript/4046/52370_4GDSS_Pre-CheckRFScriptsUsecase889100214106202022_26664_52370250951600ACPF0250291a2-threshold-for-index1-20220304-1915Pre-CheckRFUsecase889100214106202022_98088.sh,Customer/42/Migration/8891002141/PreCheck/GenerateScript/4046/53200_4GDSS_Pre-CheckRFScriptsUsecase889100214106202022_26664_53200250951600ACPF0250291cu-colo-20220304-1915Pre-CheckRFUsecase889100214106202022_98102.sh,Customer/42/Migration/8891002141/PreCheck/GenerateScript/4046/53300_4GDSS_Pre-CheckRFScriptsUsecase889100214106202022_26664_53300250951600ACPF0250291user-inactivity-timer-20220304-1915Pre-CheckRFUsecase889100214106202022_98107.sh,Customer/42/Migration/8891002141/PreCheck/GenerateScript/4046/54200_4GDSS_Pre-CheckRFScriptsUsecase889100214106202022_26664_54200250951610AUPF0250291nr-secondary-rat-usage-report-20220304-1915Pre-CheckRFUsecase889100214106202022_98081.sh,Customer/42/Migration/8891002141/PreCheck/GenerateScript/4046/55100_4GDSS_Pre-CheckRFScriptsUsecase889100214106202022_26664_5510025095162291vDU0250291qos-scheduling-conf-20220304-1915Pre-CheckRFUsecase889100214106202022_98087.sh,Customer/42/Migration/8891002141/PreCheck/GenerateScript/4046/55110_4GDSS_Pre-CheckRFScriptsUsecase889100214106202022_26664_5511025095162291vDU0250291dmrs-pdsch-fdm-20220304-1915Pre-CheckRFUsecase889100214106202022_98093.sh,Customer/42/Migration/8891002141/PreCheck/GenerateScript/4046/55200_4GDSS_Pre-CheckRFScriptsUsecase889100214106202022_26664_5520025095162291vDU0250291pdsch-pusch-resource-allocation-20220304-1915Pre-CheckRFUsecase889100214106202022_98097.sh,Customer/42/Migration/8891002141/PreCheck/GenerateScript/4046/55300_4GDSS_Pre-CheckRFScriptsUsecase889100214106202022_26664_5530025095162291vDU0250291harq-support-20220304-1915Pre-CheckRFUsecase889100214106202022_98090.sh,Customer/42/Migration/8891002141/PreCheck/GenerateScript/4046/55400_4GDSS_Pre-CheckRFScriptsUsecase889100214106202022_26664_5540025095162291vDU0250291ul-adpation-config-20220304-1915Pre-CheckRFUsecase889100214106202022_98109.sh,Customer/42/Migration/8891002141/PreCheck/GenerateScript/4046/55500_4GDSS_Pre-CheckRFScriptsUsecase889100214106202022_26664_5550025095162291vDU0250291ptp-holdover-timer-threshold-20220304-1915Pre-CheckRFUsecase889100214106202022_98095.sh,Customer/42/Migration/8891002141/PreCheck/GenerateScript/4046/55720_4GDSS_Pre-CheckRFScriptsUsecase889100214106202022_26664_5572025095162291vDU0250291prach-config-20220304-1915Pre-CheckRFUsecase889100214106202022_98104.sh,Customer/42/Migration/8891002141/PreCheck/GenerateScript/4046/55730_4GDSS_Pre-CheckRFScriptsUsecase889100214106202022_26664_5573025095162291vDU0250291ssb-conf-20220304-1915Pre-CheckRFUsecase889100214106202022_98089.sh,Customer/42/Migration/8891002141/PreCheck/GenerateScript/4046/55740_4GDSS_Pre-CheckRFScriptsUsecase889100214106202022_26664_5574025095162291vDU0250291ssb-conf-20220304-1915Pre-CheckRFUsecase889100214106202022_98103.sh"},{"customerDetailsEntity":null,"migrationType":"Migration","failedScript":"true","migrationSubType":"PreCheck","checklistFileName":"Verizon_USM_Migration Checklist_1.1.5.xlsx","neName":"8891002001_5GDU_WhitehouseStation-WLMT","progressStatus":"Completed","ciqName":"VZW_TRI_DSS_CIQ_v2.38_20220126.xlsx","outputFilepath":"Customer/42/Migration/8891002001/PreCheck/Output/63860_output.txt","testName":"sdfghjk","testDescription":"","lsmName":"Ultane","lsmVersion":"21.D.0","useCase":"Pre-Check_RF_Scripts_Usecase8891002001_06202022,Extended_Usecase8891002001_06202022","status":"Failure","userName":"superadmin","creationDate":"2022-06-20 22:22:40","useCaseDetails":null,"customerId":0,"id":4043,"wfmid":0,"useCaseSequence":0,"result":"","resultFilePath":"","fromDate":null,"toDate":null,"generateScriptPath":"Customer/42/Migration/8891002001/PreCheck/GenerateScript/4043/6040800_Ultane_ExtendedUsecase889100200106202022_26605_060408009100ACPF8819420210917-1847ExtendedUsecase889100200106202022_97721.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4043/6050500_Ultane_ExtendedUsecase889100200106202022_26605_060505008891003194vDU8819420210917-1847ExtendedUsecase889100200106202022_97720.sh"},{"customerDetailsEntity":null,"migrationType":"Migration","failedScript":"true","migrationSubType":"PreCheck","checklistFileName":"Verizon_USM_Migration Checklist_1.1.5.xlsx","neName":"8891002001_5GDU_WhitehouseStation-WLMT","progressStatus":"Completed","ciqName":"VZW_TRI_DSS_CIQ_v2.38_20220126.xlsx","outputFilepath":"Customer/42/Migration/8891002001/PreCheck/Output/54719_output.txt","testName":"erty","testDescription":"","lsmName":"Ultane","lsmVersion":"21.D.0","useCase":"Pre-Check_RF_Scripts_Usecase8891002001_06202022,Extended_Usecase8891002001_06202022","status":"Failure","userName":"superadmin","creationDate":"2022-06-20 22:20:50","useCaseDetails":null,"customerId":0,"id":4041,"wfmid":0,"useCaseSequence":0,"result":"","resultFilePath":"","fromDate":null,"toDate":null,"generateScriptPath":"Customer/42/Migration/8891002001/PreCheck/GenerateScript/4041/6010100_Ultane_ExtendedUsecase889100200106202022_26605_060101009100ACPF8819420210917-1847ExtendedUsecase889100200106202022_97725.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4041/6010200_Ultane_ExtendedUsecase889100200106202022_26605_060102009100ACPF8819420210917-1847ExtendedUsecase889100200106202022_97739.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4041/6010300_Ultane_ExtendedUsecase889100200106202022_26605_060103009100ACPF8819420210917-1847ExtendedUsecase889100200106202022_97729.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4041/6010400_Ultane_ExtendedUsecase889100200106202022_26605_060104009100ACPF8819420210917-1847ExtendedUsecase889100200106202022_97731.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4041/6010500_Ultane_ExtendedUsecase889100200106202022_26605_060105009100ACPF8819420210917-1847ExtendedUsecase889100200106202022_97733.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4041/6010600_Ultane_ExtendedUsecase889100200106202022_26605_060106009100ACPF8819420210917-1847ExtendedUsecase889100200106202022_97724.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4041/6020100_Ultane_ExtendedUsecase889100200106202022_26605_060201009100AUPF8819420210917-1847ExtendedUsecase889100200106202022_97727.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4041/6020200_Ultane_ExtendedUsecase889100200106202022_26605_060202009100AUPF8819420210917-1847ExtendedUsecase889100200106202022_97732.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4041/6020300_Ultane_ExtendedUsecase889100200106202022_26605_060203009100AUPF8819420210917-1847ExtendedUsecase889100200106202022_97730.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4041/6040100_Ultane_ExtendedUsecase889100200106202022_26605_060401009100ACPF8819420210917-1847ExtendedUsecase889100200106202022_97742.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4041/6040200_Ultane_ExtendedUsecase889100200106202022_26605_060402009100ACPF8819420210917-1847ExtendedUsecase889100200106202022_97734.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4041/6040300_Ultane_ExtendedUsecase889100200106202022_26605_060403009100ACPF8819420210917-1847ExtendedUsecase889100200106202022_97738.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4041/6040400_Ultane_ExtendedUsecase889100200106202022_26605_060404009100ACPF8819420210917-1847ExtendedUsecase889100200106202022_97736.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4041/6040500_Ultane_ExtendedUsecase889100200106202022_26605_060405009100ACPF8819420210917-1847ExtendedUsecase889100200106202022_97741.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4041/6040600_Ultane_ExtendedUsecase889100200106202022_26605_060406009100ACPF8819420210917-1847ExtendedUsecase889100200106202022_97722.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4041/6040700_Ultane_ExtendedUsecase889100200106202022_26605_060407009100ACPF8819420210917-1847ExtendedUsecase889100200106202022_97728.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4041/6040800_Ultane_ExtendedUsecase889100200106202022_26605_060408009100ACPF8819420210917-1847ExtendedUsecase889100200106202022_97721.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4041/6050100_Ultane_ExtendedUsecase889100200106202022_26605_060501008891003194vDU8819420210917-1847ExtendedUsecase889100200106202022_97740.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4041/6050200_Ultane_ExtendedUsecase889100200106202022_26605_060502008891003194vDU8819420210917-1847ExtendedUsecase889100200106202022_97726.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4041/6050300_Ultane_ExtendedUsecase889100200106202022_26605_060503008891003194vDU8819420210917-1847ExtendedUsecase889100200106202022_97743.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4041/6050400_Ultane_ExtendedUsecase889100200106202022_26605_060504008891003194vDU8819420210917-1847ExtendedUsecase889100200106202022_97735.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4041/6050500_Ultane_ExtendedUsecase889100200106202022_26605_060505008891003194vDU8819420210917-1847ExtendedUsecase889100200106202022_97720.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4041/7000000_Ultane_ExtendedUsecase889100200106202022_26605_070000008891003194vDU8819420210917-1847ExtendedUsecase889100200106202022_97723.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4041/7000100_Ultane_ExtendedUsecase889100200106202022_26605_070001008891003194vDU13197520210914-1634ExtendedUsecase889100200106202022_97737.sh"},{"customerDetailsEntity":null,"migrationType":"Migration","failedScript":"true","migrationSubType":"PreCheck","checklistFileName":"Verizon_USM_Migration Checklist_1.1.5.xlsx","neName":"8891002001_5GDU_WhitehouseStation-WLMT","progressStatus":"Completed","ciqName":"VZW_TRI_DSS_CIQ_v2.38_20220126.xlsx","outputFilepath":"Customer/42/Migration/8891002001/PreCheck/Output/77701_output.txt","testName":"test45678","testDescription":"","lsmName":"Ultane","lsmVersion":"21.D.0","useCase":"Pre-Check_RF_Scripts_Usecase8891002001_06172022,Extended_Usecase8891002001_06172022","status":"Failure(eNB PrePost Ok)","userName":"superadmin","creationDate":"2022-06-20 13:16:10","useCaseDetails":null,"customerId":0,"id":4025,"wfmid":0,"useCaseSequence":0,"result":"AUDIT_8891002001_2022-06-20_13:16:05.html","resultFilePath":"Customer/42/PostMigration/8891002001/Audit/Output","fromDate":null,"toDate":null,"generateScriptPath":"Customer/42/Migration/8891002001/PreCheck/GenerateScript/4025/6040400_Ultane_ExtendedUsecase889100200106172022_26556_060404009100ACPF8819420210917-1847ExtendedUsecase889100200106172022_97531.sh"},{"customerDetailsEntity":null,"migrationType":"Migration","failedScript":"true","migrationSubType":"PreCheck","checklistFileName":"Verizon_USM_Migration Checklist_1.1.5.xlsx","neName":"8891002001_5GDU_WhitehouseStation-WLMT","progressStatus":"Completed","ciqName":"VZW_TRI_DSS_CIQ_v2.38_20220126.xlsx","outputFilepath":"Customer/42/Migration/8891002001/PreCheck/Output/81278_output.txt","testName":"fsfsdfsf","testDescription":"","lsmName":"Ultane","lsmVersion":"21.D.0","useCase":"Pre-Check_RF_Scripts_Usecase8891002001_06172022,Extended_Usecase8891002001_06172022","status":"Failure","userName":"superadmin","creationDate":"2022-06-20 13:14:36","useCaseDetails":null,"customerId":0,"id":4019,"wfmid":0,"useCaseSequence":0,"result":"","resultFilePath":"","fromDate":null,"toDate":null,"generateScriptPath":"Customer/42/Migration/8891002001/PreCheck/GenerateScript/4019/6040400_Ultane_ExtendedUsecase889100200106172022_26556_060404009100ACPF8819420210917-1847ExtendedUsecase889100200106172022_97531.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4019/7000100_Ultane_ExtendedUsecase889100200106172022_26556_070001008891003194vDU13197520210914-1634ExtendedUsecase889100200106172022_97532.sh"},{"customerDetailsEntity":null,"migrationType":"Migration","failedScript":"true","migrationSubType":"PreCheck","checklistFileName":"Verizon_USM_Migration Checklist_1.1.5.xlsx","neName":"8891002001_5GDU_WhitehouseStation-WLMT","progressStatus":"Completed","ciqName":"VZW_TRI_DSS_CIQ_v2.38_20220126.xlsx","outputFilepath":"Customer/42/Migration/8891002001/PreCheck/Output/60387_output.txt","testName":"test4545","testDescription":"","lsmName":"Ultane","lsmVersion":"21.D.0","useCase":"Pre-Check_RF_Scripts_Usecase8891002001_06172022,Extended_Usecase8891002001_06172022","status":"Failure(eNB PrePost NOk)","userName":"superadmin","creationDate":"2022-06-20 13:02:06","useCaseDetails":null,"customerId":0,"id":4022,"wfmid":0,"useCaseSequence":0,"result":"AUDIT_8891002001_2022-06-20_13:02:00.html","resultFilePath":"Customer/42/PostMigration/8891002001/Audit/Output","fromDate":null,"toDate":null,"generateScriptPath":"Customer/42/Migration/8891002001/PreCheck/GenerateScript/4022/6040400_Ultane_ExtendedUsecase889100200106172022_26556_060404009100ACPF8819420210917-1847ExtendedUsecase889100200106172022_97531.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4022/7000100_Ultane_ExtendedUsecase889100200106172022_26556_070001008891003194vDU13197520210914-1634ExtendedUsecase889100200106172022_97532.sh"},{"customerDetailsEntity":null,"migrationType":"Migration","failedScript":"true","migrationSubType":"PreCheck","checklistFileName":"Verizon_USM_Migration Checklist_1.1.5.xlsx","neName":"8891002001_5GDU_WhitehouseStation-WLMT","progressStatus":"Completed","ciqName":"VZW_TRI_DSS_CIQ_v2.38_20220126.xlsx","outputFilepath":"Customer/42/Migration/8891002001/PreCheck/Output/82238_output.txt","testName":"ghfgh","testDescription":"","lsmName":"Ultane","lsmVersion":"21.D.0","useCase":"Pre-Check_RF_Scripts_Usecase8891002001_06172022,Extended_Usecase8891002001_06172022","status":"Failure(eNB PrePost NOk)","userName":"superadmin","creationDate":"2022-06-20 11:24:07","useCaseDetails":null,"customerId":0,"id":4016,"wfmid":0,"useCaseSequence":0,"result":"AUDIT_8891002001_2022-06-20_11:24:03.html","resultFilePath":"Customer/42/PostMigration/8891002001/Audit/Output","fromDate":null,"toDate":null,"generateScriptPath":"Customer/42/Migration/8891002001/PreCheck/GenerateScript/4016/6040400_Ultane_ExtendedUsecase889100200106172022_26556_060404009100ACPF8819420210917-1847ExtendedUsecase889100200106172022_97531.sh,Customer/42/Migration/8891002001/PreCheck/GenerateScript/4016/7000100_Ultane_ExtendedUsecase889100200106172022_26556_070001008891003194vDU13197520210914-1634ExtendedUsecase889100200106172022_97532.sh"}],"isInProgress":false,"status":"SUCCESS"}')
                    this.totalPages = this.tableData.pageCount;
                    //this.nwTypeDetails = Object.keys(this.tableData.nwTypeInfo);
                    this.smVersion = this.tableData.smVersion;
                    this.ciqList = this.tableData.getCiqList;
                    let getSelectedCIQ = JSON.parse(sessionStorage.getItem("selectedCIQ"));
                    if (this.ciqList.length > 0 && getSelectedCIQ) {
                        this.ciqDetails = getSelectedCIQ;
                    }
                    else {
                        this.ciqDetails = this.ciqList.length > 0 ? this.ciqList[0] : [];
                        // Update Session storage for selectedCIQ
                        this.sharedService.updateSelectedCIQInSessionStorage(this.ciqDetails);
                    }
                    this.getNEList(this.ciqDetails);

                //    if(this.ciqList.length >0){
                //         this.ciqDetails = this.ciqList[0];
                //         this.getNEList(this.ciqDetails);
                //    }
                    // this.getUseCase();
                    this.selectedNEItems = [];
                    //this.selectedItems =[];
                    this.ckeckedOrNot = true;
                    this.rfScriptFlag = true;
                    this.prePostAuditFlag = false;
                    this.ovUpdate = this.programName == 'VZN-4G-USM-LIVE' ? true : false;

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
                            }, this.STD_INTERVAL_DELAY );
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
                                    if(!this.interval && !this.resultPopupInterval) {
                                        this.interval = setInterval( () => {
                                            this.updateRunTestTable();
                                        }, this.STD_INTERVAL_DELAY );
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
                        if(!this.interval && !this.resultPopupInterval) {
                            this.interval = setInterval( () => {
                                this.updateRunTestTable();
                            }, this.STD_INTERVAL_DELAY );
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
        this.selectedNEItems = [];
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

    searchFileRules(event) {

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
                            "testName": currentForm.querySelector("#searchName").value,
                            "lsmVersion":  /* this.selSearchVer ? this.selSearchVer.name : "", */currentForm.querySelector("#searchVersion").value,
                            "lsmName":/*  this.selSearchSMName ? this.selSearchSMName.name : "", */currentForm.querySelector("#searchSMName").value,                            
                            "ciqName":/*  this.selSearchCiqName ? this.selSearchCiqName.ciqFileName : "", */currentForm.querySelector("#searchCiqName").value,
                            "neName": currentForm.querySelector("#searchNeName").value,
                            "userName": currentForm.querySelector("#searchUserName").value

                        };

                    if (searchCrtra.fromDate || searchCrtra.toDate || searchCrtra.testName || searchCrtra.lsmVersion ||searchCrtra.lsmName || searchCrtra.ciqName || searchCrtra.neName || searchCrtra.userName) {
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
       
    getUseCase(useCaseList) {        //lsmSelectedName
        this.useCaseValue = [];
        this.scriptValue =[];
        this.selectedItems = [];
        this.dropdownList = [];
        // let useCaseList = this.tableData.useCaseList;//lsmSelectedName.useCaseList;        
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
        

    }

    getMigUseCases() {
        this.selectedItems = [];
        if (this.ciqDetails && this.selectedNEItems && this.selectedNEItems.length > 0) {
            let selectedNEs = this.selectedNEItems.map((item) => item.eNBId);
            this.showLoader = true;
            this.runtestService.getMigUseCases(this.ciqDetails, selectedNEs, this.sharedService.createServiceToken(), this.commissionType)
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
                                        this.getUseCase(jsonStatue.useCaseList);
                                    } else {
                                        this.showLoader = false;
                                    }
                                }
                                // Call the load api again if any table row is in progress
                                if (this.isItInProgress) {
                                    setTimeout(() => this.updateRunTestTable(), 100);
                                }
                            }

                        }, 0);
                    },
                    error => {
                        //Please Comment while checkIn
                        /* setTimeout(() => {
                            this.showLoader = false;
                            // let jsonStatue = JSON.parse('{"useCaseList":[{"useCaseName":"RFUsecase_70282_06102020","useCaseId":1430,"ucSleepInterval":"1","executionSequence":64986,"scripts":[]},{"useCaseName":"CommissionScriptUsecase_70282_06102020","useCaseId":1431,"ucSleepInterval":"1","executionSequence":64987,"scripts":[{"scriptId":8767,"scriptSleepInterval":"1","useGeneratedScript":"NO","scriptName":"BASH_COMM_NB-IoTAdd_COMM_070282_MTSO_06102020.sh","scriptExeSequence":117}]}],"sessionId":"5ace0fb8","serviceToken":"78917","status":"SUCCESS"}');
                            let jsonStatue = JSON.parse('{"useCaseList":[{"useCaseName":"Pre_ENDC_X2_UseCase_07000020001_10222020","useCaseId":10431,"ucSleepInterval":"1000","executionSequence":9889383,"scripts":[{"scriptId":36257,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"20B_0_ENDC_70048_10222020.sh","scriptExeSequence":1300}]},{"useCaseName":"Anchor_CSL_UseCase_07000020001_10222020","useCaseId":10429,"ucSleepInterval":"1000","executionSequence":9889382,"scripts":[{"scriptId":36255,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"ANCHOR_CSL_7000020001_70048_10222020.sh","scriptExeSequence":1000}]},{"useCaseName":"CSL_Usecase_07000020001_10222020","useCaseId":10428,"ucSleepInterval":"1000","executionSequence":9889381,"scripts":[{"scriptId":36252,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"AU_CSL_7000020001_07000020001_10222020.sh","scriptExeSequence":700},{"scriptId":36253,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"ACPF_CSL_7000020001_07000020001_10222020.sh","scriptExeSequence":800},{"scriptId":36254,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"AUPF_CSL_7000020001_07000020001_10222020.sh","scriptExeSequence":600}]},{"useCaseName":"AU_Commision_Usecase_07000020001_10222020","useCaseId":10427,"ucSleepInterval":"1000","executionSequence":9889380,"scripts":[{"scriptId":36250,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"20A-AU-param-config-UNY-vDCM3-28ghz_07000020001_10222020.sh","scriptExeSequence":500},{"scriptId":36251,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"AU_ROUTE_07000020001_10222020.sh","scriptExeSequence":900}]},{"useCaseName":"ACPF_A1A2_Config_Usecase_07000020001_10222020","useCaseId":10426,"ucSleepInterval":"1000","executionSequence":9889379,"scripts":[{"scriptId":36248,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"AU_A1A2Create_07000020001_10222020.sh","scriptExeSequence":4000},{"scriptId":36249,"scriptSleepInterval":"1000","useGeneratedScript":"NO","scriptName":"AU_A1A2Config_07000020001_10222020.sh","scriptExeSequence":300}]},{"useCaseName":"5G-5G_NBR_RF_Scripts_Usecase07000020001_10222020","useCaseId":10425,"ucSleepInterval":"1000","executionSequence":9889378,"scripts":[]},{"useCaseName":"4G-5G_NBR_RF_Scripts_Usecase07000020001_10222020","useCaseId":10424,"ucSleepInterval":"1000","executionSequence":9889377,"scripts":[]}],"sessionId":"30c3e026","serviceToken":"53685","status":"SUCCESS"}');
                            if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
                                this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
                            }
                            if (jsonStatue.status == "SUCCESS") {
                                this.getUseCase(jsonStatue.useCaseList);
                            } else {
                                this.showLoader = false;
                            }
                            // Call the load api again if any table row is in progress
                            if (this.isItInProgress) {
                                setTimeout(() => this.updateRunTestTable(), 100);
                            }
                        }, 0); */
                        //Please Comment while checkIn
                    });
        }
        else {
            this.dropdownList = [];
        }
    }


    getNEList(selectedCiqName, updateSessionStorage = false){
        this.selectedNEItems = [];       
        this.selectedItems = [];
        // Update the sessionStorage selected CIQ if CIQ list is getting changed from UI dropdown
        updateSessionStorage ? this.sharedService.updateSelectedCIQInSessionStorage(selectedCiqName) : "";
        if (this.ciqDetails) {
            this.showLoader = true;
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
                                this.dropdownList = [];
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
                    let jsonStatue= JSON.parse('{"eNBList":[{"eNBName":"070215_HOUGHTON","eNBId":"070215"},{"eNBName":"061452_CONCORD_2_NH_HUB","eNBId":"61452"},{"eNBName":"073461_PRATTSBURGH","eNBId":"73461"},{"eNBName":"073462_East_Corning","eNBId":"73462"},{"eNBName":"073466_Howard","eNBId":"73466"},{"eNBName":"073474_Hornellsville","eNBId":"73474"},{"eNBName":"073484_ADDISON","eNBId":"73484"},{"eNBName":"072409_Press_Building","eNBId":"72409"},{"eNBName":"072412_Binghamton_DT","eNBId":"72412"},{"eNBName":"072413_SUNY_Binghamton","eNBId":"72413"},{"eNBName":"072415_Vestal","eNBId":"72415"},{"eNBName":"072416_Chenango","eNBId":"72416"},{"eNBName":"072417_Kirkwood","eNBId":"72417"},{"eNBName":"072419_Windsor","eNBId":"72419"},{"eNBName":"072424_CASTLE_CREEK","eNBId":"72424"},{"eNBName":"072425_Killawog","eNBId":"72425"},{"eNBName":"072426_East_Richford","eNBId":"72426"},{"eNBName":"072427_Caroline","eNBId":"72427"},{"eNBName":"072430_Owego_North","eNBId":"72430"},{"eNBName":"072431_Owego","eNBId":"72431"},{"eNBName":"072432_Apalachin","eNBId":"72432"},{"eNBName":"072433_Nichols","eNBId":"72433"},{"eNBName":"072442_CROCKER_CREEK","eNBId":"72442"},{"eNBName":"072443_MAINE_DT","eNBId":"72443"},{"eNBName":"072451_BELDEN","eNBId":"72451"},{"eNBName":"072452_TIOGA_CENTER","eNBId":"72452"},{"eNBName":"072454_CATATONK","eNBId":"72454"},{"eNBName":"072458_CHENANGO_DT","eNBId":"72458"},{"eNBName":"072478_Big_Flats","eNBId":"72478"},{"eNBName":"070033_POWERS_RD","eNBId":"70033"},{"eNBName":"070005_RTE_263_GETZVILLE","eNBId":"70005"},{"eNBName":"070562_BOWEN_RD","eNBId":"70562"},{"eNBName":"073313_FREY_RD","eNBId":"73313"},{"eNBName":"073326_BAKER_RD","eNBId":"73326"}],"sessionId":"2a7a3636","serviceToken":"79044","status":"SUCCESS"}');
                      if(jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User"){
                         this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                        }
                      if(jsonStatue.status == "SUCCESS"){
                        this.dropdownNEList = [];
                        this.dropdownList = [];
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
        else {
            this.dropdownNEList = [];
            this.dropdownList = [];
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
        this.selectedNEItems = [];
        this.ciqDetails = [];
        this.ckeckedOrNot = false;
        this.rfScriptFlag = false;
        this.prePostAuditFlag = false;
        this.ovUpdate = false;
        this.isPreCheckShow = false;
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
    

    submitRunTestForm(event,requestType) {
         if (this.selectedItems && this.selectedItems.length > 0) {
            this.validationData.rules.location.required = false;

        } else {
            this.validationData.rules.location.required = true;

        } 
        if (this.selectedNEItems && this.selectedNEItems.length > 0) {
            this.validationData.rules.neName.required = false;

        } else {
            this.validationData.rules.neName.required = true;
        } 

        if (this.ciqDetails != "" && this.ciqDetails != undefined && this.ciqDetails.ciqFileName) {
            this.validationData.rules.ciqName.required = false;

        } else {
            this.validationData.rules.ciqName.required = true;

        }

        if(requestType == 'CHECK_CONNECTION'){
            this.validationData.rules.lsmVersion.required = true;
            this.validationData.rules.lsmName.required = true; 
            this.validationData.rules.neName.required = false;  
            this.validationData.rules.testName.required = false; 
            this.validationData.rules.ciqName.required = false;  
            this.validationData.rules.location.required = false; 
        }else {
            this.validationData.rules.lsmVersion.required = false;
            this.validationData.rules.lsmName.required = false; 
           // this.validationData.rules.neName.required = true;  
            this.validationData.rules.testName.required = true; 
           // this.validationData.rules.ciqName.required = true;  
            //this.validationData.rules.location.required = true; 
        }        

        validator.performValidation(event, this.validationData, "save_update");
        setTimeout(() => {
            if (this.isValidForm(event)) {
                this.showLoader = true;
                let useCaseDetails = [],neDetails =[],scriptDetails=[];
                this.runTestDetails ={};
                for (let i of this.useCaseValue) {
                    let selUseCase = {
                        "useCaseName": i.useCaseName,
                        "useCaseId": i.useCaseId,
                        "executionSequence": i.updatedExecution,
                        "ucSleepInterval": i.ucSleepInterval ? i.ucSleepInterval : ""
                    }
                    useCaseDetails.push(selUseCase);
                }
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

                for (let j of this.selectedNEItems) {
                    let selNE = {
                        "neId": j.eNBId,
                        "neName": j.eNBName
                    }
                    neDetails.push(selNE);
                }
                /* if(this.selectedNEItems) {
                    let selNE = {
                        "neId": this.selectedNEItems.eNBId,
                        "neName": this.selectedNEItems.eNBName
                    }
                    neDetails.push(selNE);
                } */
                let currentForm = event.target.parentNode.parentNode,
                    runTestFormDetails = {
                        "testname": currentForm.querySelector("#testName").value,                        
                        "lsmVersion": this.versionDetails ? this.versionDetails.name : "",
                        "lsmName": this.nameDetails ? this.nameDetails.name : "",
                        "lsmId":this.nameDetails.id,
                        "ciqName":this.ciqDetails ? this.ciqDetails.ciqFileName : "",
                        "checklistFileName": this.ciqDetails ? this.ciqDetails.checklistFileName : "",
                        "checklistFilePath": this.ciqDetails ? this.ciqDetails.checklistFilePath : "",
                        "neDetails":neDetails,
                        "useCase": useCaseDetails,       
                        "scripts" :scriptDetails,
                        "testDesc": currentForm.querySelector("#testDescription").value,
                        "password":"",
                        "currentPassword":this.ckeckedOrNot,
                        "rfScriptFlag": this.rfScriptFlag,
                        "prePostAuditFlag": this.prePostAuditFlag,
                        "multipleDuo": this.multipleDuo == null ? false : this.multipleDuo,
                        "ovUpdate": this.ovUpdate
                    };
                    
                this.runTestDetails = runTestFormDetails;

                this.runtestService.uploadRunTestDetails(this.sharedService.createServiceToken(), runTestFormDetails,this.commissionType,requestType)
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
                                        }else if(jsonStatue.requestType == 'GENERATE'){                                           
                                            this.message = "Script generated Successfully";
                                        }else{
                                            this.message = "Run test started successfully!";
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

                                let jsonStatue = {"password":{"serverName":"Sane","serverIp":"10.20.120.82"},"requestType":"","sessionId":"2edb0f8c","serviceToken":"56139","status":"SUCCESS"};

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

    onChangeNEs(event: any) {
        // this.showLoader = true;
        this.versionDetails = "";
        this.nameDetails = "";
        this.getMigUseCases();

        // Clear the interval for load api when it is in result mode
        /* clearInterval( this.interval );
        this.interval=null; */

        /* switch(type) {
            case "ADD":
                this.selectedNEItems.push(event);
                break;
            case "REMOVE":
                this.selectedNEItems = this.selectedNEItems.filter(item => item.eNBId != event.value.eNBId);
                this.removeUseCasesForNE(event.value.eNBId);
                break;
        } */

        /* if(this.selectedNEItems && this.selectedNEItems.length > 0) {
            this.showLoader = true;
            // if(this.selectedNEItems.length == 1) {
                let selectedNEs = this.selectedNEItems.map((item) => item.eNBId);
                this.runtestService.getNeConfigDetails(this.sharedService.createServiceToken(), this.ciqDetails, selectedNEs)
                .subscribe(
                    data => {
                        setTimeout(() => {
                            let jsonStatue = data.json();
                            // this.showLoader = false;
                            if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
                                this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
                            } else {
                                if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {
                                    if (jsonStatue.status == "SUCCESS") {
                                        // this.versionDetails = jsonStatue.lsmVersion;
                                        this.versionDetails = this.smVersion.filter((value) => value.name == jsonStatue.lsmVersion)[0];
                                        if (this.versionDetails) {
                                            this.getLsmName(this.versionDetails);
                                            setTimeout(() => {
                                                this.nameDetails = this.lsmNameDetails.filter((value) => value.name == jsonStatue.lsmName)[0];
                                            }, 100);
                                        }
                                        else {
                                            this.versionDetails = "";
                                            this.nameDetails = "";
                                        }
                                        this.getMigUseCases();
                                    } else {
                                        this.showLoader = false;
                                    }
                                }
                            }

                        }, 0);
                    },
                    error => {
                        //Please Comment while checkIn
                        setTimeout(() => {
                            // this.showLoader = false;
                            let jsonStatue = JSON.parse('{"sessionId":"458fa5b1","serviceToken":60748,"lsmVersion":"20.A.0","lsmName":"Rochester","lsmid":84,"status":"SUCCESS"}');
                            if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
                                this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
                            }
                            if (jsonStatue.status == "SUCCESS") {
                                // this.versionDetails = jsonStatue.lsmVersion;
                                this.versionDetails = this.smVersion.filter((value) => value.name == jsonStatue.lsmVersion)[0];
                                if (this.versionDetails) {
                                    this.getLsmName(this.versionDetails);
                                    setTimeout(() => {
                                        this.nameDetails = this.lsmNameDetails.filter((value) => value.name == jsonStatue.lsmName)[0];
                                    }, 100);
                                }
                                else {
                                    this.versionDetails = "";
                                    this.nameDetails = "";
                                }
                                this.getMigUseCases();
                            } else {
                                this.showLoader = false;
                            }
                        }, 100);
                        //Please Comment while checkIn
                    });
            // }
            // else {
            //     this.showLoader = false;
            //     this.getMigUseCases();
            //     this.versionDetails = "";
            //     this.nameDetails = "";
            // }
            
        }
        else {
            this.versionDetails = "";
            this.nameDetails = "";
        } */
    }

    
    onChangeUseCase(event: any) {
        setTimeout(() => {
            this.useCaseValue = [];
            this.scriptValue =[];
            
            for (let i of this.selectedItems) {
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
            // Changing hardcoded for VZN-5G-DSS program on request of Anupama Dated 22-OCT-2020
            for (let scriptList of this.selectedItems) {
                for(let script of scriptList["item_id"].scripts){
                    let scriptData = {
                        "useCaseName": scriptList["item_text"],
                        "scriptName":script.scriptName,
                        "scriptId":script.scriptId,
                        "actualExecution":script.scriptExeSequence,
                        "updatedExecution":this.programName == "VZN-5G-DSS" && (script.scriptName.startsWith("57") || script.scriptName.startsWith("7")) ? 0 : this.programName == "VZN-5G-MM" && (script.scriptName.startsWith("ACPF_CSL") || script.scriptName.startsWith("AUPF_CSL")) ? 0 : script.scriptExeSequence, //this.programName == "VZN-5G-DSS" && (script.scriptName.startsWith("57") || script.scriptName.startsWith("7")) ? 0 : script.scriptExeSequence,"scriptSleepInterval":script.scriptSleepInterval,
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
            if (this.programName == 'VZN-5G-DSS' || this.programName =="VZN-5G-CBAND") {
                this.isPreCheckShow = this.checkPRECHECKRFUsecaseAvl();
            }
        }, 0);

    }

    checkPRECHECKRFUsecaseAvl() {
        let isAvailableInUC = false;
        if(this.useCaseValue.length == 1) {
            for (let useCaseItem of this.useCaseValue) {
         
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

    viewUseCase(content, key) {        
        for (var i = 0; i < this.useCaseValue.length; i++) {
            this.useCaseValue[i].oldValue = this.useCaseValue[i].updatedExecution;
            this.useCaseValue[i].intervalOldValue = this.useCaseValue[i].ucSleepInterval;
            this.useCaseValue[i].rowUpdate = false;
        }

        for (var i = 0; i < this.scriptValue.length; i++) {
            this.scriptValue[i].oldValue = this.scriptValue[i].updatedExecution; 
            this.scriptValue[i].intervalOldValue = this.scriptValue[i].scriptSleepInterval;
            this.scriptValue[i].useGeneratedScript_old = this.scriptValue[i].useGeneratedScript;
            this.scriptValue[i].rowUpdate = false;
        }
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
                this.useCaseValue[index].oldValue = this.useCaseValue[index].updatedExecution;
                this.useCaseValue[index].intervalOldValue = this.useCaseValue[index].ucSleepInterval;
                this.useCaseValue[index].updatedExecution = Number($("#updatedExeSeq_" + index).val());
                this.useCaseValue[index].ucSleepInterval = $("#time_" + index).val();
                this.useCaseValue[index].rowUpdate = true;
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
                if ($(rows[i]).find("td:eq(3)").text() == $("#updatedExeSeq_" + index).val() && 
                    $(rows[i]).find("td:eq(0)").text() == key.useCaseName) {
                   validations.rules["updatedExeSeq_" + [index]].customfunction = true;
                    exeVal = true;
                }
            }
        }
       
        validator.performValidation(event, validations, "save_update");  
        setTimeout(()=>{   
            if (this.isValidForm(event) && !exeVal) {        
                this.editScriptMode =-1;
                this.scriptValue[index].oldValue = this.scriptValue[index].updatedExecution;
                this.scriptValue[index].intervalOldValue = this.scriptValue[index].scriptSleepInterval;
                this.scriptValue[index].useGeneratedScript_old = this.scriptValue[index].useGeneratedScript
                this.scriptValue[index].updatedExecution = $("#updatedExeSeq_" + index).val();
                this.scriptValue[index].scriptSleepInterval = $("#time_" + index).val();
                this.scriptValue[index].useGeneratedScript = $("#useGenScript_" + index).prop('checked') ? 'YES':'NO';
                this.scriptValue[index].rowUpdate = true;
            }
         },0)
    }
    toggleUpExecSeq(event, index) {
        // console.log("Updated Exec " + index + " - " + this.scriptValue[index].updatedExecution);
        if(this.scriptValue[index].updatedExecution == 0) {
            this.scriptValue[index].updatedExecution = this.scriptValue[index].actualExecution;
            this.checkIfAllScriptSelected();
        }
        else {
            this.scriptValue[index].updatedExecution = 0;
            this.allScriptSelect = false;
        }
        this.updateNumberOfSelectedScript();
        this.scriptValue[index].rowUpdate = true;
    }
    toggleAllScriptExecSeq() {
        // this.allScriptSelect = !this.allScriptSelect;
        /* for(let i = 0; i < this.scriptValue.length; i++) {
            this.toggleUpExecSeq(null, i);
        } */
        
        if (this.allScriptSelect) {
            // SELECT-ALL
            this.scriptValue.forEach((curScriptObj) => curScriptObj.updatedExecution = curScriptObj.actualExecution);
        }
        else {
            // UNSELECT-ALL
            this.scriptValue.forEach((curScriptObj) => curScriptObj.updatedExecution = 0);
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
        this.allScriptSelect = this.scriptValue.every((curScriptObj) => curScriptObj.updatedExecution > 0);
    }
    updateNumberOfSelectedScript() {
        let totalScriptSelected = 0;
        if(this.allScriptSelect) {
            totalScriptSelected = this.scriptValue.length;
        }
        else {
            let execScript = this.scriptValue.filter(element => element.updatedExecution > 0);
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
        for (var i = 0; i < this.useCaseValue.length; i++) {
            if (this.useCaseValue[i].rowUpdate == true) {
                this.useCaseValue[i].updatedExecution = this.useCaseValue[i].oldValue;
                this.useCaseValue[i].ucSleepInterval = this.useCaseValue[i].intervalOldValue;
                this.useCaseValue[i].rowUpdate = false;
            }
        }
        for (var i = 0; i < this.scriptValue.length; i++) {
            if (this.scriptValue[i].rowUpdate == true) {
                this.scriptValue[i].updatedExecution = this.scriptValue[i].oldValue;
                this.scriptValue[i].scriptSleepInterval = this.scriptValue[i].intervalOldValue;
                this.scriptValue[i].useGeneratedScript = this.scriptValue[i].useGeneratedScript_old;
                this.scriptValue[i].rowUpdate = false;
            }
        }
        this.viewModalBlock.close();
        this.editMode = -1;
        this.editScriptMode = -1;
        this.allScriptSelect = true;
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

    viewResult(content, key) {
        // Clear the interval for load api when it is in result mode
        clearInterval( this.interval );
        this.interval=null;

        this.testName = key.testName;
        this.testId = key.id;
        this.wfmid = key.wfmid;
        this.showLoader = true;
        this.ciqName = key.ciqName;
        this.selectedScriptName = "";
        this.runtestService.viewTestResult(this.sharedService.createServiceToken(), key.id,key.lsmName,key.neName)
            .subscribe(
                data => {
                    let jsonStatue = data.json();
                    // {"timestamp":"2020-11-26T04:08:27.197+0000","status":500,"error":"Internal Server Error","message":"No message available","path":"/ruleResult"}
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
                                this.pgmName = jsonStatue.programName;
                                this.smName = jsonStatue.SMName;
                                this.neName = jsonStatue.NEName;
                                this.isResultProcessCompleted = jsonStatue.isProcessCompleted
                                this.testResultsData = jsonStatue.useCaseResult;
                                this.tabularResultsData = JSON.parse(JSON.stringify(this.testResultsData))
                                this.successModalBlock1 = this.modalService.open(content, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView' });
                                this.failedScript = jsonStatue.failedScript;
                                this.CurrentSeq=jsonStatue.CurrentSeq;
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
                                        }, this.STD_INTERVAL_DELAY);
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
                        //let jsonStatue = {"Scriptcount":10,"SMName":"4G-USM2","programName":"VZN-4G-USM-LIVE","failedScript":true,"NEName":"131605_MCKENNA_PARK","useCaseResult":[{"useCaseName":"CA_Usecase131605_02202022","useCaseId":25036,"script_status":[{"scriptId":77876,"scriptName":"BASH_1000_131605_eNB_0_disable-anr-20220128-1059_CA_Usecase_131605_02202022","scriptExeSeq":1,"XML_Rules":[{"ruleDefinition":"XmlRuleName : curl, RootName : rpc-reply, SubrootName : , ElementName : curl, ElementValue : error, Operator : CONTAINS, Status : Pass","ruleName":"curl","status":"NO DATA"}]},{"scriptId":77875,"scriptName":"BASH_1100_131605_eNB_0_lock-lte-cells-20220128-1059_CA_Usecase_131605_02202022","scriptExeSeq":2,"XML_Rules":[{"ruleDefinition":"XmlRuleName : curl, RootName : rpc-reply, SubrootName : , ElementName : curl, ElementValue : error, Operator : CONTAINS, Status : Pass","ruleName":"curl","status":"NO DATA"}]}]},{"useCaseName":"CA_Usecase131605_02202023","useCaseId":25037,"script_status":[{"scriptId":77876,"scriptName":"BASH_1000_131605_eNB_0_disable-anr-20220128-1059_CA_Usecase_131605_02202022","scriptExeSeq":1,"XML_Rules":[{"ruleDefinition":"XmlRuleName : curl, RootName : rpc-reply, SubrootName : , ElementName : curl, ElementValue : error, Operator : CONTAINS, Status : Pass","ruleName":"curl","status":"NO DATA"}]},{"scriptId":77875,"scriptName":"BASH_1100_131605_eNB_0_lock-lte-cells-20220128-1059_CA_Usecase_131605_02202022","scriptExeSeq":2,"XML_Rules":[{"ruleDefinition":"XmlRuleName : curl, RootName : rpc-reply, SubrootName : , ElementName : curl, ElementValue : error, Operator : CONTAINS, Status : Pass","ruleName":"curl","status":"NO DATA"}]}]}],"sessionId":"ddce69c","serviceToken":"70436","isProcessCompleted":true,"status":"SUCCESS","CurrentSeq":2,"reason":""};
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
                                    }, this.STD_INTERVAL_DELAY);
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

    reRunContinueTest(type,wfmid) {
        let reRunScriptID = null;
        this.showInnerLoader = true;
        // this.testId
        //"runTestId":746,  "runType":"CONTINUE",  "reRunScriptID":null,  "skipScriptIds":"670_3020,730_9910"
        let skipScriptIds = [];
        this.testResultsData.forEach((useCase) => {
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
        }
        if(wfmid != 0){
            reRunScriptID = null;
            skipScriptIds = null;
        }
        this.runtestService.reRunContinueTest(this.sharedService.createServiceToken(), type, this.testId, skipScriptIds, reRunScriptID, this.commissionType,wfmid, this.prePostAuditFlag, this.ovUpdate)
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
                                if(type == "RERUNSCRIPT") {
                                    this.message = "Retrying the failed script!";
                                }
                                else {
                                    this.message = "Continuing with the commissioning!";
                                }

                                this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                                this.updateRunTestTable();
                                this.closeModelViewResult();
                            }
                            else {
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
                            this.updateRunTestTable();
                            this.closeModelViewResult();
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
                    }, 1000); */
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
        // Call the load api again if any table row is in progress
        if (this.isItInProgress) {
            setTimeout(() => this.updateRunTestTable(), 100);
        }
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

    closeModelFailureMsg() {
        this.errorMsgResult = "";
        this.milestoneResult = [];
        this.historyResult = "";
        this.ovRetryData = {};
        this.failureMsgBlock.close();
    }

    isInProgress(state) {
        let retClass = "";
        if(state == "InProgress") {
            retClass = "inProgress"
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
        this.sharedService.dynamicSort(predicate, event, index, this.scriptValue);
    }


    changeSortingUsecases(predicate, event, index) {
        this.sharedService.dynamicSort(predicate, event, index, this.useCaseValue);
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
        this.versionDetails="";
        this.ciqDetails="";
        this.nameDetails="";
        this.lsmNameDetails=[];
        this.selectedNEItems = [];
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
        setTimeout(() => {
            if (this.isValidForm(event)) {
                this.showLoader = true;
                this.pswdModalBlock.close();              
                this.runtestService.uploadRunTestDetails(this.sharedService.createServiceToken(), this.runTestDetails, this.commissionType, this.requestType)
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

      downloadGeneratedScripts(testName, filePath) {

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
    
    viewFailureMessagae(failureErrorModal, key) {
        this.testName = key.testName;
        this.testId = key.id;
        this.showLoader = true;

        this.runtestService.viewFailureMessage(this.sharedService.createServiceToken(), key.id, key.lsmName, key.neName)
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

                                this.errorMsgResult = jsonStatue.messageInfo.filter(item => item && item["scriptName"] && item["scriptOutput"]);
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
                        
                        let jsonStatue = {"reason":"","sessionId":"8909e5cf","serviceToken":"61626","status":"SUCCESS","messageInfo":[{"saneIssue":"Issue with Sane password","scriptName":"BASH_RF_NB-IoTAdd_0-70056_Lock_All_Cells_11302020","scriptOutput":"Response\n<rpc- </rpc-reply>Response\n<rpc- </rpc-reply>Response\n<rpc- </rpc-reply>Response\n<rpc- </rpc-reply>Response\n<rpc- </rpc-reply>Response\n<rpc- </rpc-reply>Response\n<rpc- </rpc-reply>Response\n<rpc- </rpc-reply>Response\n<rpc- </rpc-reply>Response\n<rpc- </rpc-reply>Response\n<rpc- </rpc-reply>Response\n<rpc- </rpc-reply>Response\n<rpc- </rpc-reply>Response\n<rpc- </rpc-reply>Response\n<rpc- </rpc-reply>"},{"saneIssue":"Issue with Sane1 password","scriptName":"BASH_RF_NB-IoTAdd_3-70056_Cell_Level_Changes_AWS2_11302020","scriptOutput":"Response\n<rpc- </rpc-reply>Response\n<rpc- </rpc-reply>Response\n<rpc- </rpc-reply>Response\n<rpc- </rpc-reply>Response\n<rpc- </rpc-reply>Response\n<rpc- </rpc-reply>Response\n<rpc- </rpc-reply>Response\n<rpc- </rpc-reply>Response\n<rpc- </rpc-reply>Response\n<rpc- </rpc-reply>Response\n<rpc- </rpc-reply>Response\n<rpc- </rpc-reply>Response\n<rpc- </rpc-reply>Response\n<rpc- </rpc-reply>Response\n<rpc- </rpc-reply>"},{"saneIssue":"","scriptName":"BASH_RF_NB-IoTAdd_5-70056-13_NBR-EUTRAN_YANG_20200304_11302020","scriptOutput":"Response\n<rpc- </rpc-reply>Response\n<rpc- </rpc-reply>Response\n<rpc- </rpc-reply>"}]};
                        // let jsonStatue = {"reason":"","messageInfo":[{},{},{"saneIssue":"Check the Sane server Password and Please retry the script in Result window","scriptName":"41540_5790512162_vDU_0_57162_dss-interface-20201214-1253_vDU_RFUsecase_5790512162_12142020","scriptOutput":"Password:\nLast login: Mon Dec 14 13:02:50 2020 from 10.134.223.68\n\n\nv0adebab connected to txslsanepa10: \n\nWelcome to SANE, now connecting...\n[H[2J               *** S A N E (Secure Access to Network Elements) ***\n\n               1 ) Network Element Selection by Market \n\n               2 ) Network Element Selection by Vendor \n\n               3 ) Network Element Selection by NE Type \n\n               4 ) Network Element Selection by [ Abiodun's ] List\n\n               5 ) Direct Network Element Connection by NEID Number\n\n               6 ) Direct Network Element Connection by IP Address\n\n\n               t ) Toggle Network Element Display Mode [ NE_Name   ] \n\n               a ) About SANE\n\n               x ) Exit \n               \n \n\n\n\n                   Please Enter Selection: > [H[2J             *** S A N E (Secure Access to Network Elements) ***\n ______________________________________________________________________________ \n\n    MRKT:                   STATE:                      MSC:                  \n\n  VENDOR:                   NE-TYPE:                  \n\n                         Please Select: VENDOR  \n\n\n 1 ) Samsung                              \n\n  m ) Return to Main    p ) Return to Previous Menu    x ) Exit\n\n\n\n\n\n\n\n\n                   Please Enter Selection: > [H[2J             *** S A N E (Secure Access to Network Elements) ***\n ______________________________________________________________________________ \n\n    MRKT:                   STATE:                      MSC:                  \n\n  VENDOR: Samsung           NE-TYPE:                  \n\n                         Please Select: NE-TYPE  \n\n\n 01 ) 4G_Femto_FGW                          02 ) 4G_Femto_FGW-EMS                     \n 03 ) 4G_Femto_FIS                          04 ) 4G_Femto_MDB-BE                      \n 05 ) 4G_Femto_MDB-BE-DR                    06 ) 4G_Femto_MDB-FE                      \n 07 ) 4G_Femto_MDB-FE-DR                    08 ) 4G_Femto_Stats-Server                \n 09 ) 4G_Femto_VNFM_CLI                     10 ) 4G_Femto_iFeMs                       \n 11 ) 4G_Femto_iFeMs-DR                     12 ) 4G_Femto_sFeMs                       \n 13 ) 4G_Femto_vEMIC_CLI                    14 ) 4G_Femto_vEMIS_CLI                   \n 15 ) 4G_Femto_vFISAPP_CLI                  16 ) 4G_Femto_vFISDB_CLI                  \n 17 ) 4G_Femto_vFISDB_MaxScale_CLI          18 ) 4G_Femto_vFISEMSDB_CLI               \n 19 ) 4G_Femto_vFISEMSDB_MaxScale_CLI       20 ) 4G_Femto_vFISEMS_CLI                 \n 21 ) 4G_Femto_vIFEMS_CLI                   22 ) 4G_Femto_vMDBD_CLI                   \n 23 ) 4G_Femto_vMDBM_CLI                    24 ) 4G_Femto_vSFEMSD_CLI                 \n 25 ) 4G_Femto_vSFEMSM_CLI                  26 ) 4G_Femto_vSTAT_CLI                   \n 27 ) CMS_NFVO_CLI                          28 ) CMS_PIM                              \n 29 ) CMS_VNFM                              30 ) DCM                                  \n 31 ) JMS_EMS_CLI                           32 ) LSM-R                                \n 33 ) SA_CMO_VNFM_CLI                       34 ) SA_USM_DR_CLI                        \n 35 ) SA_USM_OP_CLI                         36 ) SA_VDM_IDM_CLI                       \n 37 ) SA_VDM_MDM_CLI                        38 ) SA_VDM_SDM_CLI                       \n 39 ) SA_VDM_UDM_CLI                        40 ) SA_VSM_CFM_CLI                       \n 41 ) SA_VSM_MCM_CLI                        42 ) SA_VSM_PSM_CLI                       \n 43 ) SA_VSO_SCM_CLI                        44 ) SA_VSO_SFC_CLI                       \n 45 ) SA_VSO_SPC_CLI                        46 ) SMS_EMS_CLI                          \n 47 ) Switch_CLI                            48 ) UBI_PCF-EMS                          \n 49 ) UBI_PCF_LCMA                          50 ) UBI_WGW_CLI                          \n 51 ) UBI_WGW_Linux                         52 ) UBI_WGW_WSS_EMS                      \n 53 ) UBI_WSS_CLI                           54 ) UBI_WSS_Linux                        \n 55 ) VoMA_CLI                             \n\n  m ) Return to Main    p ) Return to Previous Menu    x ) Exit\n\n                   Please Enter Selection: > [H[2J3             *** S A N E (Secure Access to Network Elements) ***\n ______________________________________________________________________________ \n\n    MRKT:                   STATE:                      MSC:                  \n\n  VENDOR: Samsung           NE-TYPE: LSM-R            \n\n                         Please Select: MRKT  \n\n\n 1 ) North East                            2 ) South Central                        \n\n\n  m ) Return to Main    p ) Return to Previous Menu    x ) Exit\n\n\n\n\n\n\n\n                   Please Enter Selection: > [H[2J             *** S A N E (Secure Access to Network Elements) ***\n ______________________________________________________________________________ \n\n    MRKT: North East        STATE:                      MSC:                  \n\n  VENDOR: Samsung           NE-TYPE: LSM-R            \n\n                         Please Select: STATE  \n\n\n 1 ) Connecticut                           2 ) Massachusetts                        \n 3 ) New York                             \n\n  m ) Return to Main    p ) Return to Previous Menu    x ) Exit\n\n\n\n\n\n\n\n                   Please Enter Selection: > [H[2J             *** S A N E (Secure Access to Network Elements) ***\n ______________________________________________________________________________ \n\n    MRKT: North East        STATE:   New York           MSC:                  \n\n  VENDOR: Samsung           NE-TYPE: LSM-R            \n\n                         Please Select: MSC  \n\n\n 1 ) Rochester                            \n\n  m ) Return to Main    p ) Return to Previous Menu    x ) Exit\n\n\n\n\n\n\n\n\n                   Please Enter Selection: > [H[2J             *** S A N E (Secure Access to Network Elements) ***\n ______________________________________________________________________________ \n\n    MRKT: North East        STATE:   New York           MSC: Rochester        \n\n  VENDOR: Samsung           NE-TYPE: LSM-R            \n\n                         Please Select: Network Element  \n\n\n 1 ) Roch_LSM_MS1                          2 ) Roch_LSM_MS2                         \n 3 ) Roch_LSM_MS3                          4 ) Roch_LSM_MS4                         \n 5 ) Roch_LSM_MS5                         \n\n\n  a ) Add a NE to [ Abiodun's ] Shortcut List\n  t ) Toggle Network Element Display Mode [ NE_Name   ]\n\n  m ) Return to Main    p ) Return to Previous Menu    x ) Exit\n\n\n                   Please Enter Selection: > [3;J[H[2J\n\n\n\n Now Connecting to Network Element: [ Roch_LSM_MS1 ] \n\n\n\n Please wait... \n\n\n\n\n           You are now Logged in!          \nvendgrp@HNRTNYCRLSM-MS01:~[?1034h[vendgrp@HNRTNYCRLSM-MS01 ~]$ ssh -o StrictHostKeyChecking=no -oCheckHostIP=no vsm@2001:4888:0a12:3143:0106:0292:0000:0100\nssh: connect to host 2001:4888:0a12:3143:0106:0292:0000:0100 port 22: Connection timed out\n\nvendgrp@HNRTNYCRLSM-MS01:~[vendgrp@HNRTNYCRLSM-MS01 ~]$ packet_write_wait: Connection to 198.226.62.4 port 22: Broken pipe\nsend: spawn id exp4 not open\n    while executing\n\"send \"nmslab123\\r\"\"\n    (file \"/opt/rct/Samsung/SMART/Customer/42/Migration/5790512162/PreCheck/GenerateScript//41540_WestBoroughMedium_vDURFScriptsUsecase579051216212142020_22772_4...\" line 64)\n"}],"sessionId":"1cf05e37","serviceToken":"83931","status":"SUCCESS"};

                        if (jsonStatue.status == "SUCCESS") {
                            this.showLoader = false;
                            
                            this.errorMsgResult = jsonStatue.messageInfo.filter(item => item && item["scriptName"] && item["scriptOutput"]);
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

    viewOVFailureMessagae(failureErrorModal, key) {
        this.ovRetryData = {
            runTestId: key.id,
            ciqName: key.ciqName,
            neName: key.neName,
            useCaseName: key.useCase
        };
        this.showLoader = true;

        this.runtestService.viewOVFailureMessage(this.sharedService.createServiceToken(), key.id, key.lsmName, key.neName, this.commissionType, key.useCase)
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

        this.runtestService.retryMilestoneUpdate(this.sharedService.createServiceToken(), this.ovRetryData, this.commissionType, this.milestoneResult)
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

        this.runtestService.retryFailedMilestoneUpdate(this.sharedService.createServiceToken(), this.ovRetryData, this.commissionType, failedMilestone)
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
    selectScriptFile(usecase, script) {
        this.selectedScript = script;
        this.selectedUsecase = usecase;
        this.selectedScriptName = '';
        let scriptBtn = document.getElementById("scriptUpload")
        scriptBtn.click();
    }
    chooseFile(evt) {
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
                        this.selectedScriptName = "";
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
}