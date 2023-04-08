import { ViewContainerRef, Input, ElementRef, Renderer, Component, OnInit, ViewChild, HostListener } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { NgbModal, ModalDismissReasons, NgbTabsetConfig } from '@ng-bootstrap/ng-bootstrap';
import { PregrowService } from '../services/pregrow.service';
import { SharedService } from '../services/shared.service';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { filter, map, mergeMap } from 'rxjs/operators';
import { validator } from '../validation';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { RanatpService } from '../services/ranatp.service';
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';
import { NgbPopover } from '@ng-bootstrap/ng-bootstrap';
import * as FileSaver from 'file-saver';
import * as _ from 'underscore';
import * as $ from 'jquery';

@Component({
    selector: 'app-ranatp',
    templateUrl: './ranatp.component.html',
    styleUrls: ['./ranatp.component.scss'],
    providers: [RanatpService]
})
export class RanatpComponent implements OnInit {
    STD_INTERVAL_DELAY:any = 30000;
    max = new Date();
    isProcessCompleted:any;
    fromDt:any;
    ciqConfig:object;
    tableData: any;
    tabularResultsData:any;
    statusCheck:any;
    editMode: number = -1;
    editCLMode:number =-1;
    editScriptMode : number = -1;
    showNoDataFound: boolean;
    tableShowHide: boolean;
    checkListTable:boolean = false;
    showLoader: boolean = true;
    showInnerLoader: boolean = false;
    tableDataHeight: any;
    isItInProgress:boolean;
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
    neNameList:any =[];
    ciqDetails:any;
    neDetails:any;
    testName :any;
    useCaseSO = "";
    scriptList = [];
    showOutput: boolean = false;
    scriptOutput: any;

    useCaseValue = [];
    scriptValue = [];
    selectedLsm: any = ""
    // Following variables are used to dispaly success, confirm and failure model(s)
    showModelMessage: boolean = false;
    modelData: any;
    closeResult: string;
    currentEditRow: any;
    successModalBlock: any;
    successModalBlock1: any;
    pswdModalBlock:any;
    viewModalBlock: any;
    message: any;
    testId: any;
    commissionType ="";
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

    dropdownBandList = [];

    selectedItems = [];
    selectedNEItems =null;
    selectedBandItems =[];
    dropdownSettings = {};
    dropdownSettingsNE = {};
    dropdownSettingsBand = {};
    pgmName:any;
    smName:any;
    neName:any;
    serverInfo:any;
    runTestDetails:any;
    requestType:any;
    showWideRunningLog:boolean;
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
    selectedProgram:any;

    // Running Log
    runningLogs: any;
    runningLogModalBlock: any;
    showRunningLogLauncher: boolean;
    showRunningLogContent: boolean;

    // Check Connection Log
    checkConnLogs: any;
    checkConnLogModalBlock: any;

    interval: any;
    runningLogInterval: any;
    scriptFilesData: any;
    programChangeSubscription:any;

    showMySites: boolean = false;

    // @ViewChild('p1') public p1: NgbPopover;
    p1: NgbPopover;
    p2: NgbPopover;

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
            "bandName": {
                "required": true 
            },
            "filePost": {
                "required": false
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
            "bandName": {
                "required": "Band Name is required"
            },
            "filePost": {
                "required": "Input File is required"
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
    @ViewChild('filePost') filePostRef: ElementRef;
    @ViewChild('searchForm') searchForm;

    constructor(
        private element: ElementRef,
        private renderer: Renderer,
        private router: Router,
        private modalService: NgbModal,
        private ranatpService: RanatpService,
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
                if(pgm['networkTypeDetailsEntity'].networkType != '5G' && pgm['programName'] != 'VZN-4G-FSU') {
                    this.loadInitialData();
                }
                else {
                    this.router.navigate(['/audit']);
                }
            }
        });
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
        this.selectedNEItems = null;
        this.selectedBandItems = [];
        this.ckeckedOrNot = true;

        this.paginationDetails = paginationDetails;
        this.ciqConfig = {
            displayKey: "ciqFileName",
            search: true, 
            height: '200px',
             placeholder: '--Select--', 
            customComparator: () => { },
            //limitTo: this.ciqList.length, 
            moreText: 'more',            
            noResultsFound: 'No results found!',
           searchPlaceholder: 'Search', 
           searchOnKey: 'ciqFileName',
          }
        this.ciqList = [];
        this.getRunTest();
        this.resetForm();

        // On every 10s refresh the table
        if(!this.interval) {
            this.interval = setInterval( () => {
                this.updateRunTestTable();
            }, this.STD_INTERVAL_DELAY );
        }
        
        
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
    }
    clearSearchFrom() {
        this.searchForm.nativeElement.reset();  
    }
      
    /*
   * Used to display the table data on load/by default
   * @param : repairStation,userName,reflect (edit/delete)
   * @retun : null
   */
  resetForm() {
    setTimeout(() => {
        this.runTestForm.nativeElement.reset();
    }, 0);
    // remove validation messages
    validator.removeFormValidation("bluePrintFormWrapper", this.validationData);
}
    getRunTest() {
        
        this.showLoader = true;
        $("#dataWrapper").find(".scrollBody").scrollLeft(0);
        this.ranatpService.getRunTest(this.searchStatus, this.searchCriteria, this.paginationDetails, this.sharedService.createServiceToken(), this.commissionType, this.showMySites).subscribe(
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

                            setTimeout(() => {
                                this.showLoader = false;
                                this.tableData = jsonStatue;
                                this.totalPages = this.tableData.pageCount;
                                this.smVersion = this.tableData.smVersion;
                                this.ciqList = this.tableData.getCiqList;
                                
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
                                this.getUseCase();
                                this.selectedNEItems = null;
                                this.selectedBandItems = [];
								//this.selectedItems =[];
                                this.ckeckedOrNot = true;
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
                    //NoData
                    // this.tableData = JSON.parse('{"sessionId":"529ed523","serviceToken":"91652","status":"SUCCESS","pageCount":3,"nwType":["type1","type2"],"lsmDetails":{"lsmName1":["v123","v321"],"lsmName2":["v456","v654"]},"runTestTableDetails":[]}');
                    //Data
                    //this.tableData = JSON.parse('{"nwTypeInfo":{"5G":{"v1.0.2":{"LSM 2":[]}},"4G":{"v1.0.1":{"LSM 1":[{"useCaseName":"user_snap_contains_true","useCaseId":5,"executionSequence":1},{"useCaseName":"user_snap_contains_fail","useCaseId":6,"executionSequence":1},{"useCaseName":"9>4096_pass","useCaseId":7,"executionSequence":1},{"useCaseName":"4096>9_pass","useCaseId":8,"executionSequence":1},{"useCaseName":"4096>9_fail","useCaseId":9,"executionSequence":1},{"useCaseName":"cli","useCaseId":10,"executionSequence":1},{"useCaseName":"cliRulecontainsPass","useCaseId":11,"executionSequence":1},{"useCaseName":"use11","useCaseId":12,"executionSequence":111},{"useCaseName":"usecase112","useCaseId":13,"executionSequence":112},{"useCaseName":"usecase113","useCaseId":14,"executionSequence":113},{"useCaseName":"usecase114","useCaseId":15,"executionSequence":114},{"useCaseName":"usecase115","useCaseId":16,"executionSequence":115},{"useCaseName":"usecase116","useCaseId":17,"executionSequence":116},{"useCaseName":"asdfasf","useCaseId":18,"executionSequence":222},{"useCaseName":"file ","useCaseId":19,"executionSequence":1},{"useCaseName":"Twofile","useCaseId":20,"executionSequence":1},{"useCaseName":"mnb","useCaseId":21,"executionSequence":1}]}}},"pageCount":4,"sessionId":"396f8b99","serviceToken":"89673","runTestTableDetails":[{"id":51,"testName":"dfsf","testDescription":"dfggf","nwType":"4G","lsmName":"LSM 1","lsmVersion":"v1.0.1","useCaseDetails":[{"useCaseName":"user_snap_contains_true","useCaseId":5,"executionSequence":2},{"useCaseName":"user_snap_contains_fail","useCaseId":6,"executionSequence":1},{"useCaseName":"9>4096_pass","useCaseId":7,"executionSequence":5}],"useCase":"Twofile","status":"PASS","userName":null,"creationDate":"2019-01-30T05:22:18.000+0000","result":null,"customerId":2},{"id":50,"testName":"gdrhd","testDescription":"rtheh","nwType":"4G","lsmName":"LSM 1","lsmVersion":"v1.0.1","useCase":"mnb","status":"FAIL","userName":null,"creationDate":"2019-01-25T11:15:21.000+0000","result":null,"customerId":2},{"id":49,"testName":"tyjkl","testDescription":"uytruy","nwType":"4G","lsmName":"LSM 1","lsmVersion":"v1.0.1","useCase":"mnb","status":"FAIL","userName":null,"creationDate":"2019-01-25T11:08:27.000+0000","result":null,"customerId":2},{"id":48,"testName":"retye","testDescription":"gyjgh","nwType":"4G","lsmName":"LSM 1","lsmVersion":"v1.0.1","useCase":"mnb","status":"FAIL","userName":null,"creationDate":"2019-01-25T11:05:58.000+0000","result":null,"customerId":2},{"id":47,"testName":"zzxcv","testDescription":"zxcv","nwType":"4G","lsmName":"LSM 1","lsmVersion":"v1.0.1","useCase":"Twofile","status":"PASS","userName":null,"creationDate":"2019-01-25T10:58:26.000+0000","result":null,"customerId":2},{"id":46,"testName":"asdfasf","testDescription":"asdfasdf","nwType":"4G","lsmName":"LSM 1","lsmVersion":"v1.0.1","useCase":"Twofile","status":"PASS","userName":null,"creationDate":"2019-01-25T10:56:42.000+0000","result":null,"customerId":2},{"id":45,"testName":"asdfsf","testDescription":"xfgedhg","nwType":"4G","lsmName":"LSM 1","lsmVersion":"v1.0.1","useCase":"file ","status":"PASS","userName":null,"creationDate":"2019-01-25T10:49:51.000+0000","result":null,"customerId":2},{"id":44,"testName":"sdrgdsf","testDescription":"sdfgfsdg","nwType":"4G","lsmName":"LSM 1","lsmVersion":"v1.0.1","useCase":"file ","status":"PASS","userName":null,"creationDate":"2019-01-25T10:27:19.000+0000","result":null,"customerId":2},{"id":43,"testName":"sdfgsdg","testDescription":"sdfgsdg","nwType":"4G","lsmName":"LSM 1","lsmVersion":"v1.0.1","useCase":"file ","status":"FAIL","userName":null,"creationDate":"2019-01-25T10:26:44.000+0000","result":null,"customerId":2},{"id":42,"testName":"asfsadf","testDescription":"asdsgf","nwType":"4G","lsmName":"LSM 1","lsmVersion":"v1.0.1","useCase":"file ","status":"PASS","userName":null,"creationDate":"2019-01-25T10:20:27.000+0000","result":null,"customerId":2}],"status":"SUCCESS"}');
                    // this.tableData = JSON.parse('{"nwTypeInfo":{"4G":{"V1.2":{"lsm3":[]}},"3G":{"V1.1":{"lsm1":[{"useCaseName":"usecase1","useCaseId":1,"executionSequence":1}],"lsm2":[]}}},"pageCount":0,"sessionId":"282a5cb7","serviceToken":"71271","runTestTableDetails":[],"status":"SUCCESS"}');
                    //this.tableData = JSON.parse('{"sessionId":"19c4c64","serviceToken":"53531","status":"SUCCESS","fromDate":"03/08/2019","pageCount":1,"isInProgress":true,"smVersion":[{"name":"1.2.3","id":1,"smNameList":[{"name":"SM","id":7},{"name":"bhuvana","id":8,"useCaseList":[{"useCaseName":"test","useCaseId":1,"executionSequence":1},{"useCaseName":"rtyy","useCaseId":2,"executionSequence":1},{"useCaseName":"2scripts","useCaseId":3,"executionSequence":1}]},{"name":"oneLSM","id":9}]}],"toDate":"03/15/2019","useCaseList":[{"useCaseName":"UC_MYSQL","useCaseId":42,"ucSleepInterval":"1","executionSequence":1,"scripts":[{"scriptId":31,"scriptSleepInterval":"1","scriptName":"pre_check.sh","scriptExeSequence":1,"useGeneratedScript":"NO"}]},{"useCaseName":"UC_Port Check","useCaseId":43,"executionSequence":1,"scripts":[{"scriptId":31,"scriptName":"pre_check.sh","scriptExeSequence":1,"scriptSleepInterval":"1","useGeneratedScript":"YES"}],"ucSleepInterval":"1"},{"useCaseName":"UC1","useCaseId":62,"executionSequence":1,"scripts":[{"scriptId":40,"scriptName":"18th March script.sh","scriptExeSequence":1,"scriptSleepInterval":"1","useGeneratedScript":"YES"}],"ucSleepInterval":"1"},{"useCaseName":"UC2","useCaseId":63,"ucSleepInterval":"1","executionSequence":1,"scripts":[{"scriptId":40,"scriptSleepInterval":"1","scriptName":"18th March script.sh","scriptExeSequence":1,"useGeneratedScript":"NO"}]},{"useCaseName":"UC3","useCaseId":64,"ucSleepInterval":"1","executionSequence":1,"scripts":[{"scriptId":40,"scriptSleepInterval":"1","scriptName":"18th March script.sh","scriptExeSequence":1,"useGeneratedScript":"YES"}]},{"useCaseName":"test","useCaseId":1,"executionSequence":1,"scripts":[{"scriptName":"sc1","scriptId":123,"scriptExeSequence":123,"scriptSleepInterval":30,"useGeneratedScript":"YES"},{"scriptName":"sc2","scriptId":234,"scriptExeSequence":234,"scriptSleepInterval":40,"useGeneratedScript":"NO"}],"ucSleepInterval":20},{"useCaseName":"rtyy","useCaseId":2,"executionSequence":1,"scripts":[{"scriptName":"sc1","scriptId":123,"scriptExeSequence":123,"useGeneratedScript":"YES"},{"scriptName":"sc2","scriptId":234,"scriptExeSequence":234,"useGeneratedScript":"NO"}]},{"useCaseName":"2scripts","useCaseId":3,"executionSequence":1,"scripts":[{"scriptName":"sc1","scriptId":123,"scriptExeSequence":123,"useGeneratedScript":"YES"},{"scriptName":"sc2","scriptId":234,"scriptExeSequence":234,"useGeneratedScript":"YES"}]}],"getCiqList":[{"id":49,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T16:24:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T16:41:30.000+0000","createdBy":"superadmin"},"ciqFileName":"UNY-NE-VZ_CIQ_Ver2.82_03122019.xlsx","ciqFilePath":"/home/user/RCT/rctsoftware/Samsung/SMART/Customer/23/PreMigration/Input/UNY-NE-VZ_CIQ_Ver2.82_03122019/CIQ/","scriptFileName":"1_58154_LEXINGTON_12_MA.zip,1_6003_networdata123.zip,1_6013_networkcohyutrr.zip,1_6203_networkconfig.zip","scriptFilePath":"/home/user/RCT/rctsoftware/Samsung/SMART/Customer/23/PreMigration/Input/UNY-NE-VZ_CIQ_Ver2.82_03122019/SCRIPT/","checklistFileName":"","checklistFilePath":"","ciqVersion":"ORIGINAL","fileSourceType":"FETCH","uploadBy":"superadmin","remarks":"","creationDate":"2019-03-12T20:38:02.000+0000"},{"id":42,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T16:24:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T16:41:30.000+0000","createdBy":"superadmin"},"ciqFileName":"UNY-NE-VZ_CIQ_Ver2.82_01282019.xlsx","ciqFilePath":"/home/user/RCT/rctsoftware/Samsung/SMART/Customer/23/PreMigration/Input/UNY-NE-VZ_CIQ_Ver2.82_01282019/CIQ/","scriptFileName":"1_1111_LEXINGTON_12_MA.zip,1_12345_LEXINGTON_12_MA.zip,1_58154_LEXINGTON_12_MA.zip","scriptFilePath":"/home/user/RCT/rctsoftware/Samsung/SMART/Customer/23/PreMigration/Input/UNY-NE-VZ_CIQ_Ver2.82_01282019/SCRIPT/","checklistFileName":"Verizon_vLSM_Migration Checklist_1.0.3.xlsx","checklistFilePath":"/home/user/RCT/rctsoftware/Samsung/SMART/Customer/23/PreMigration/Input/UNY-NE-VZ_CIQ_Ver2.82_01282019/CHECKLIST/","ciqVersion":"ORIGINAL","fileSourceType":"UPLOAD","uploadBy":"superadmin","remarks":"sa","creationDate":"2019-03-13T15:25:02.000+0000"}],"runTestTableDetails":[{"customerDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T16:24:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T16:41:30.000+0000","createdBy":"superadmin"},"migrationType":"migration","migrationSubType":"commission","checklistFileName":"Verizon_vLSM_Migration Checklist_1.0.3.xlsx","neName":"056001_BOSTON_CONVENTION_CTR_DAS2","progressStatus":"Completed","ciqName":"UNY-NE-VZ_CIQ_Ver2.82_01282019.xlsx","outputFilepath":"/home/path_of_the_file","testName":"Test1","testDescription":"sdaff","lsmName":"bhuvana","lsmVersion":"1.2.3","useCase":"test","status":"PASS","userName":null,"creationDate":"2019-03-15T17:38:16.000+0000","useCaseDetails":"test?1?1","customerId":2,"useCaseSequence":0,"id":5},{"customerDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T16:24:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T16:41:30.000+0000","createdBy":"superadmin"},"migrationType":"migration","migrationSubType":"commission","checklistFileName":"Verizon_vLSM_Migration Checklist_1.0.3.xlsx","neName":"056001_BOSTON_CONVENTION_CTR_DAS2","progressStatus":"InProgress","ciqName":"UNY-NE-VZ_CIQ_Ver2.82_01282019.xlsx","testName":"test2","testDescription":"asdf","lsmName":"bhuvana","lsmVersion":"1.2.3","useCase":"test","status":"PASS","userName":null,"creationDate":"2019-03-15T17:34:55.000+0000","useCaseDetails":"test?1?1","customerId":2,"useCaseSequence":0,"id":4},{"customerDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T16:24:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T16:41:30.000+0000","createdBy":"superadmin"},"migrationType":"migration","migrationSubType":"commission","checklistFileName":"Verizon_vLSM_Migration Checklist_1.0.3.xlsx","neName":"056001_BOSTON_CONVENTION_CTR_DAS2","progressStatus":"InProgress","ciqName":"UNY-NE-VZ_CIQ_Ver2.82_01282019.xlsx","testName":"sfd","testDescription":"safd","lsmName":"bhuvana","lsmVersion":"1.2.3","useCase":"test","status":"PASS","userName":null,"creationDate":"2019-03-15T17:30:41.000+0000","useCaseDetails":"test?1?1","customerId":2,"useCaseSequence":0,"id":3},{"customerDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T16:24:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T16:41:30.000+0000","createdBy":"superadmin"},"migrationType":"migration","migrationSubType":"commission","checklistFileName":"Verizon_vLSM_Migration Checklist_1.0.3.xlsx","neName":"056001_BOSTON_CONVENTION_CTR_DAS2","progressStatus":"Completed","ciqName":"UNY-NE-VZ_CIQ_Ver2.82_01282019.xlsx","testName":"1Test","testDescription":"sdaffas","lsmName":"bhuvana","lsmVersion":"1.2.3","useCase":"test","status":"PASS","userName":null,"creationDate":"2019-03-15T15:45:03.000+0000","useCaseDetails":"test?1?1","customerId":2,"useCaseSequence":0,"id":2},{"customerDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T16:24:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T16:41:30.000+0000","createdBy":"superadmin"},"migrationType":"migration","migrationSubType":"commission","checklistFileName":"Verizon_vLSM_Migration Checklist_1.0.3.xlsx","neName":"056001_BOSTON_CONVENTION_CTR_DAS2","progressStatus":"Completed","ciqName":"UNY-NE-VZ_CIQ_Ver2.82_01282019.xlsx","testName":"sdff","testDescription":"dsfg","lsmName":"oneLSM","lsmVersion":"1.2.3","useCase":"2scripts","status":"PASS","userName":null,"creationDate":"2019-03-15T15:37:58.000+0000","useCaseDetails":"2scripts?3?1","customerId":2,"useCaseSequence":0,"id":1}]}');
                    //this.tableData = JSON.parse('{"fromDate":"06/16/2019","pageCount":1,"smVersion":[{"name":"7.0.2","id":10,"smNameList":[{"name":"WSBO TINY - MSMA0","id":24},{"name":"MSM03","id":25},{"name":"MSME_007","id":34},{"name":"Audit NE","id":35},{"name":"MSMO5","useCaseList":[{"useCaseName":"Check_MYSQL","useCaseId":44,"executionSequence":1},{"useCaseName":"Pre_folder Check","useCaseId":45,"executionSequence":1},{"useCaseName":"Post_folder_check","useCaseId":46,"executionSequence":1},{"useCaseName":"App_Port_Check","useCaseId":47,"executionSequence":1},{"useCaseName":"UC1","useCaseId":67,"executionSequence":2},{"useCaseName":"Audit UC1","useCaseId":197,"executionSequence":55444},{"useCaseName":"UC for Defect CLI","useCaseId":211,"executionSequence":5675},{"useCaseName":"18th June Shell UC1","useCaseId":212,"executionSequence":5455},{"useCaseName":"19th June CLI UC1","useCaseId":217,"executionSequence":23233},{"useCaseName":"20th June_USE CASE1","useCaseId":224,"executionSequence":45455},{"useCaseName":"UC21st June","useCaseId":226,"executionSequence":1223132},{"useCaseName":"21st June UC1_Retest","useCaseId":230,"executionSequence":565656},{"useCaseName":"25th June Mix UC1","useCaseId":231,"executionSequence":1211},{"useCaseName":"Dev Test_UC1","useCaseId":232,"executionSequence":1233},{"useCaseName":"Dev Test_UC2","useCaseId":233,"executionSequence":5454},{"useCaseName":"Dev Test_UC3","useCaseId":234,"executionSequence":56561},{"useCaseName":"Dev Test_UC4","useCaseId":235,"executionSequence":5444},{"useCaseName":"Dev Test_UC6","useCaseId":236,"executionSequence":67778},{"useCaseName":"Dev Test_UC8","useCaseId":238,"executionSequence":5666},{"useCaseName":"Dev Test_UC9","useCaseId":239,"executionSequence":12411},{"useCaseName":"26th June Shell UC1","useCaseId":242,"executionSequence":3223},{"useCaseName":"UC28th June","useCaseId":259,"executionSequence":544554},{"useCaseName":"28th June Shell UC1","useCaseId":260,"executionSequence":656}],"id":40},{"name":"24th April NE","useCaseList":[{"useCaseName":"Check_MYSQL","useCaseId":44,"executionSequence":1},{"useCaseName":"Pre_folder Check","useCaseId":45,"executionSequence":1},{"useCaseName":"Post_folder_check","useCaseId":46,"executionSequence":1},{"useCaseName":"App_Port_Check","useCaseId":47,"executionSequence":1},{"useCaseName":"UC1","useCaseId":67,"executionSequence":2},{"useCaseName":"Audit UC1","useCaseId":197,"executionSequence":55444},{"useCaseName":"UC for Defect CLI","useCaseId":211,"executionSequence":5675},{"useCaseName":"18th June Shell UC1","useCaseId":212,"executionSequence":5455},{"useCaseName":"19th June CLI UC1","useCaseId":217,"executionSequence":23233},{"useCaseName":"20th June_USE CASE1","useCaseId":224,"executionSequence":45455},{"useCaseName":"UC21st June","useCaseId":226,"executionSequence":1223132},{"useCaseName":"21st June UC1_Retest","useCaseId":230,"executionSequence":565656},{"useCaseName":"25th June Mix UC1","useCaseId":231,"executionSequence":1211},{"useCaseName":"Dev Test_UC1","useCaseId":232,"executionSequence":1233},{"useCaseName":"Dev Test_UC2","useCaseId":233,"executionSequence":5454},{"useCaseName":"Dev Test_UC3","useCaseId":234,"executionSequence":56561},{"useCaseName":"Dev Test_UC4","useCaseId":235,"executionSequence":5444},{"useCaseName":"Dev Test_UC6","useCaseId":236,"executionSequence":67778},{"useCaseName":"Dev Test_UC8","useCaseId":238,"executionSequence":5666},{"useCaseName":"Dev Test_UC9","useCaseId":239,"executionSequence":12411},{"useCaseName":"26th June Shell UC1","useCaseId":242,"executionSequence":3223},{"useCaseName":"UC28th June","useCaseId":259,"executionSequence":544554},{"useCaseName":"28th June Shell UC1","useCaseId":260,"executionSequence":656}],"id":41}]}],"toDate":"06/28/2019","useCaseList":[{"useCaseName":"Audit UC1","useCaseId":197,"ucSleepInterval":"1","executionSequence":55444,"scripts":[{"scriptId":507,"scriptSleepInterval":"1","useGeneratedScript":"YES","scriptName":"14th June CLI Script.sh","scriptExeSequence":6}]},{"useCaseName":"UC for Defect CLI","useCaseId":211,"ucSleepInterval":"1","executionSequence":5675,"scripts":[{"scriptId":513,"scriptSleepInterval":"1","useGeneratedScript":"YES","scriptName":"17th June CLI SCript1.sh","scriptExeSequence":65}]},{"useCaseName":"18th June Shell UC1","useCaseId":212,"ucSleepInterval":"1","executionSequence":5455,"scripts":[{"scriptId":514,"scriptSleepInterval":"1","useGeneratedScript":"YES","scriptName":"14th June Shell Script1.sh","scriptExeSequence":30}]}],"sessionId":"9931c854","serviceToken":"68352","getCiqList":[{"id":11,"programDetailsEntity":{"id":1,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-05-31T09:20:26.000+0000","status":"Active","remarks":"","networkColor":"#0f2a79"},"programName":"VZN-4G-CDU30","programDescription":"CDU30","status":"Active","creationDate":"2019-06-28T05:42:41.000+0000","createdBy":"superadmin"},"ciqFileName":"UNY-NE-VZ_CIQ_Ver2.91_04012019.xlsx","ciqFilePath":"Customer/1/PreMigration/Input/UNY-NE-VZ_CIQ_Ver2.91_04012019/CIQ/","scriptFileName":"1_57170_LEXINGTON_12_MA.zip,1_57170_LEXINGTON_12_MA.zip,1_57170_LEXINGTON_12_MA.zip","scriptFilePath":"Customer/1/PreMigration/Input/UNY-NE-VZ_CIQ_Ver2.91_04012019/SCRIPT/","checklistFileName":"Verizon_Migration Checklist 100418_version4.8 (2).xlsx","checklistFilePath":"Customer/1/PreMigration/Input/UNY-NE-VZ_CIQ_Ver2.91_04012019/CHECKLIST/","ciqVersion":"ORIGINAL","fileSourceType":"UPLOAD","uploadBy":"superadmin","remarks":"Upload VZN-4G-CDU30","creationDate":"2019-06-25T07:27:32.000+0000"}],"runTestTableDetails":[{"customerDetailsEntity":null,"migrationType":"PostMigration","migrationSubType":"Audit","checklistFileName":"Verizon_Migration Checklist 100418_version4.8 (2).xlsx","neName":"056008_CAMBSIDE_GALL_MA","progressStatus":"Completed","ciqName":"UNY-NE-VZ_CIQ_Ver2.91_04012019.xlsx","outputFilepath":"Customer/1/PostMigration/56008/Audit/Output/75672_output.txt","testName":"18th June Shell Audit Test1","testDescription":"","lsmName":"24th April NE","lsmVersion":"7.0.2","useCase":"18th June Shell UC1","status":"FAIL","userName":null,"creationDate":"2019-06-18 16:39:23","useCaseDetails":null,"customerId":0,"id":227,"useCaseSequence":0,"result":"AUDIT_56008_2019-06-18_16:38:07.html","resultFilePath":"Customer/1/PostMigration/56008/Audit/Output"},{"customerDetailsEntity":null,"migrationType":"PostMigration","migrationSubType":"Audit","checklistFileName":"Verizon_Migration Checklist 100418_version4.8 (2).xlsx","neName":"056008_CAMBSIDE_GALL_MA","progressStatus":"Completed","ciqName":"UNY-NE-VZ_CIQ_Ver2.91_04012019.xlsx","outputFilepath":"Customer/1/PostMigration/56008/Audit/Output/84724_output.txt","testName":"Audit 18th June Test1","testDescription":"","lsmName":"24th April NE","lsmVersion":"7.0.2","useCase":"Audit UC1","status":"FAIL","userName":null,"creationDate":"2019-06-18 15:34:53","useCaseDetails":null,"customerId":0,"id":225,"useCaseSequence":0,"result":"AUDIT_56008_2019-06-18_15:32:54.html","resultFilePath":"Customer/1/PostMigration/56008/Audit/Output"},{"customerDetailsEntity":null,"migrationType":"PostMigration","migrationSubType":"Audit","checklistFileName":"Verizon_Migration Checklist 100418_version4.8 (2).xlsx","neName":"056569_SOUTH_BOSTON_ODAS_4","progressStatus":"Completed","ciqName":"UNY-NE-VZ_CIQ_Ver2.91_04012019.xlsx","outputFilepath":"Customer/1/PostMigration/56569/Audit/Output/97338_output.txt","testName":"Test","testDescription":"","lsmName":"24th April NE","lsmVersion":"7.0.2","useCase":"Audit UC1","status":"FAIL","userName":null,"creationDate":"2019-06-18 14:54:44","useCaseDetails":null,"customerId":0,"id":224,"useCaseSequence":0,"result":"AUDIT_56569_2019-06-18_14:52:48.html","resultFilePath":"Customer/1/PostMigration/56569/Audit/Output"},{"customerDetailsEntity":null,"migrationType":"PostMigration","migrationSubType":"Audit","checklistFileName":"Verizon_Migration Checklist 100418_version4.8 (2).xlsx","neName":"056008_CAMBSIDE_GALL_MA","progressStatus":"Completed","ciqName":"UNY-NE-VZ_CIQ_Ver2.91_04012019.xlsx","outputFilepath":"Customer/1/PostMigration/56008/Audit/Output/50162_output.txt","testName":"18th June Audit Test1","testDescription":"","lsmName":"24th April NE","lsmVersion":"7.0.2","useCase":"UC for Defect CLI","status":"FAIL","userName":null,"creationDate":"2019-06-18 14:40:32","useCaseDetails":null,"customerId":0,"id":223,"useCaseSequence":0,"result":"AUDIT_56008_2019-06-18_14:38:31.html","resultFilePath":"Customer/1/PostMigration/56008/Audit/Output"},{"customerDetailsEntity":null,"migrationType":"PostMigration","migrationSubType":"Audit","checklistFileName":"Verizon_Migration Checklist 100418_version4.8 (2).xlsx","neName":"056008_CAMBSIDE_GALL_MA","progressStatus":"Completed","ciqName":"UNY-NE-VZ_CIQ_Ver2.91_04012019.xlsx","outputFilepath":"Customer/1/PostMigration/56008/Audit/Output/56001_output.txt","testName":"Audit Test1_17th June","testDescription":"","lsmName":"24th April NE","lsmVersion":"7.0.2","useCase":"UC for Defect CLI","status":"FAIL","userName":null,"creationDate":"2019-06-17 15:48:36","useCaseDetails":null,"customerId":0,"id":216,"useCaseSequence":0,"result":"AUDIT_56008_2019-06-17_15:48:36.html","resultFilePath":"Customer/1/PostMigration/56008/Audit/Output"}],"isInProgress":true,"status":"SUCCESS"}');
                    this.tableData = JSON.parse('{"fromDate":"07/07/2019","toDate":"07/19/2019","sessionId":"e75177e3","serviceToken":"84199","getCiqList":[{"id":19,"programDetailsEntity":{"id":17,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-05-31T09:20:26.000+0000","status":"Active","remarks":"","networkColor":"#0f2a79"},"programName":"SPT-4G-MIMO","programDescription":"MIMO","status":"Active","creationDate":"2019-07-18T06:44:00.000+0000","createdBy":"superadmin","sourceProgramId":16,"sourceprogramName":"SPT-4G-MIMO_1"},"ciqFileName":"MM_National_CIQ_Master_04012019.xlsx","ciqFilePath":"Customer/17/PreMigration/Input/MM_National_CIQ_Master_04012019/CIQ/","scriptFileName":"CB54XC132_AkronLSMUMS4_549138_MMIMO_Mig_PUT_Script_20190226.txt,CH03HO117_AkronLSMUMS5_581717_MMIMO_Mig_PUT_Script_20190225.txt,CB03XC057_AkronLSMUMS4_548965_MMIMO_Mig_PUT_Script_20190409.txt","scriptFilePath":"Customer/17/PreMigration/Input/MM_National_CIQ_Master_04012019/SCRIPT/","checklistFileName":"CommCDU30_mMIMO_8.5.3_Checklist_V2.7.xlsx","checklistFilePath":"Customer/17/PreMigration/Input/MM_National_CIQ_Master_04012019/CHECKLIST/","ciqVersion":"ORIGINAL","fileSourceType":"FETCH","uploadBy":"superadmin","remarks":"","creationDate":"2019-07-18T06:48:42.000+0000"}],"status":"SUCCESS","pageCount":1,"smVersion":[{"name":"9.5.0","id":12,"smNameList":[{"name":"East_Syracuse","useCaseList":[{"useCaseName":"UC","useCaseId":286,"executionSequence":1}],"id":50},{"name":"West_Syracuse","useCaseList":[{"useCaseName":"UC1","useCaseId":287,"executionSequence":1},{"useCaseName":"UC2","useCaseId":281,"executionSequence":2}],"id":52}]}],"useCaseList":[{"useCaseName":"UC1","useCaseId":287,"ucSleepInterval":"1","executionSequence":1,"scripts":[{"scriptId":755,"scriptSleepInterval":"1","useGeneratedScript":"YES","scriptName":"Sprint_Testing_Rules.txt","scriptExeSequence":3},{"scriptId":757,"scriptSleepInterval":"1","useGeneratedScript":"YES","scriptName":"Sprint_Testing_Rules_19th_1.txt","scriptExeSequence":1},{"scriptId":758,"scriptSleepInterval":"1","useGeneratedScript":"YES","scriptName":"Sprint_Testing_Rules_19th_2.txt","scriptExeSequence":2}]}],"runTestTableDetails":[{"customerDetailsEntity":null,"migrationType":"PostMigration","migrationSubType":"Audit","checklistFileName":"CommCDU30_mMIMO_8.5.3_Checklist_V2.7.xlsx","neName":"CHCKILZXBBULTE0516276","progressStatus":"Completed","ciqName":"MM_National_CIQ_Master_04012019.xlsx","outputFilepath":"Customer/17/PostMigration/516276/Audit/Output/80783_output.txt","testName":"Audit5","testDescription":"","lsmName":"L","lsmVersion":"8.5.3","useCase":"UC1","status":"Success","userName":null,"creationDate":"2019-07-19 15:33:56","useCaseDetails":null,"customerId":0,"id":381,"useCaseSequence":0,"result":"AUDIT_516276_2019-07-19_15:32:56.html","resultFilePath":"Customer/17/PostMigration/516276/Audit/Output","fromDate":null,"toDate":null,"generateScriptPath":"Customer/17/PostMigration/516276/Audit/GenerateScript//1_L_UC1_287_SprintTestingRules19th1_757.sh,Customer/17/PostMigration/516276/Audit/GenerateScript//2_L_UC1_287_SprintTestingRules19th2_758.sh,Customer/17/PostMigration/516276/Audit/GenerateScript//3_L_UC1_287_SprintTestingRules_755.sh"},{"customerDetailsEntity":null,"migrationType":"PostMigration","migrationSubType":"Audit","checklistFileName":"CommCDU30_mMIMO_8.5.3_Checklist_V2.7.xlsx","neName":"CHCKILZXBBULTE0516276","progressStatus":"Completed","ciqName":"MM_National_CIQ_Master_04012019.xlsx","outputFilepath":"Customer/17/PostMigration/516276/Audit/Output/84622_output.txt","testName":"Audit4","testDescription":"","lsmName":"L","lsmVersion":"8.5.3","useCase":"UC1","status":"Success","userName":null,"creationDate":"2019-07-19 15:24:04","useCaseDetails":null,"customerId":0,"id":379,"useCaseSequence":0,"result":"AUDIT_516276_2019-07-19_15:23:05.html","resultFilePath":"Customer/17/PostMigration/516276/Audit/Output","fromDate":null,"toDate":null,"generateScriptPath":"Customer/17/PostMigration/516276/Audit/GenerateScript//1_L_UC1_287_SprintTestingRules_755.sh"}],"isInProgress":false}');

                    this.totalPages = this.tableData.pageCount;
                    //this.nwTypeDetails = Object.keys(this.tableData.nwTypeInfo);
                    this.smVersion = this.tableData.smVersion;
                    this.ciqList = this.tableData.getCiqList;                    

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

                    // if (this.ciqList.length > 0) {
                    //     this.ciqDetails = this.ciqList[0];
                    //     this.getNEList(this.ciqDetails);
                    // }
                    this.getUseCase();
                    this.selectedNEItems = null;
                    this.selectedBandItems = [];
					//this.selectedItems =[];
                    this.ckeckedOrNot = true;
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
                }, 1000); */
                //Please Comment while checkIn


            });
    }
    updateRunTestTable(showLoader = false) {
        showLoader ? this.showLoader = true : "";
        this.ranatpService.getRunTest(this.searchStatus, this.searchCriteria, this.paginationDetails, this.sharedService.createServiceToken(), this.commissionType, this.showMySites).subscribe(
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
                    this.tableData = JSON.parse('{"fromDate":"03/08/2019","pageCount":1,"smVersion":[{"name":"1.2.3","id":1,"smNameList":[{"name":"SM","id":7},{"name":"bhuvana","useCaseList":[{"useCaseName":"test","useCaseId":1,"executionSequence":1},{"useCaseName":"rtyy","useCaseId":2,"executionSequence":1},{"useCaseName":"2scripts","useCaseId":3,"executionSequence":1}],"id":8},{"name":"oneLSM","id":9}]}],"toDate":"03/15/2019","useCaseList":[{"useCaseName":"test","useCaseId":1,"executionSequence":1},{"useCaseName":"rtyy","useCaseId":2,"executionSequence":1},{"useCaseName":"2scripts","useCaseId":3,"executionSequence":1}],"sessionId":"19c4c64","serviceToken":"53531","getCiqList":[{"id":49,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T16:24:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T16:41:30.000+0000","createdBy":"superadmin"},"ciqFileName":"UNY-NE-VZ_CIQ_Ver2.82_03122019.xlsx","ciqFilePath":"/home/user/RCT/rctsoftware/Samsung/SMART/Customer/23/PreMigration/Input/UNY-NE-VZ_CIQ_Ver2.82_03122019/CIQ/","scriptFileName":"1_58154_LEXINGTON_12_MA.zip,1_6003_networdata123.zip,1_6013_networkcohyutrr.zip,1_6203_networkconfig.zip","scriptFilePath":"/home/user/RCT/rctsoftware/Samsung/SMART/Customer/23/PreMigration/Input/UNY-NE-VZ_CIQ_Ver2.82_03122019/SCRIPT/","checklistFileName":"","checklistFilePath":"","ciqVersion":"ORIGINAL","fileSourceType":"FETCH","uploadBy":"superadmin","remarks":"","creationDate":"2019-03-12T20:38:02.000+0000"},{"id":42,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T16:24:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T16:41:30.000+0000","createdBy":"superadmin"},"ciqFileName":"UNY-NE-VZ_CIQ_Ver2.82_01282019.xlsx","ciqFilePath":"/home/user/RCT/rctsoftware/Samsung/SMART/Customer/23/PreMigration/Input/UNY-NE-VZ_CIQ_Ver2.82_01282019/CIQ/","scriptFileName":"1_1111_LEXINGTON_12_MA.zip,1_12345_LEXINGTON_12_MA.zip,1_58154_LEXINGTON_12_MA.zip","scriptFilePath":"/home/user/RCT/rctsoftware/Samsung/SMART/Customer/23/PreMigration/Input/UNY-NE-VZ_CIQ_Ver2.82_01282019/SCRIPT/","checklistFileName":"Verizon_vLSM_Migration Checklist_1.0.3.xlsx","checklistFilePath":"/home/user/RCT/rctsoftware/Samsung/SMART/Customer/23/PreMigration/Input/UNY-NE-VZ_CIQ_Ver2.82_01282019/CHECKLIST/","ciqVersion":"ORIGINAL","fileSourceType":"UPLOAD","uploadBy":"superadmin","remarks":"sa","creationDate":"2019-03-13T15:25:02.000+0000"}],"runTestTableDetails":[{"id":5,"customerDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T16:24:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T16:41:30.000+0000","createdBy":"superadmin"},"outputFilepath":"/home/path_of_the_file","migrationType":"migration","migrationSubType":"commission","checklistFileName":"Verizon_vLSM_Migration Checklist_1.0.3.xlsx","neName":"056001_BOSTON_CONVENTION_CTR_DAS2","progressStatus":"InProgress","ciqName":"UNY-NE-VZ_CIQ_Ver2.82_01282019.xlsx","testName":"sdafafs","testDescription":"sdaff","lsmName":"bhuvana","lsmVersion":"1.2.3","useCase":"test","status":"PASS","userName":null,"creationDate":"2019-03-15T17:38:16.000+0000","useCaseDetails":"test?1?1","customerId":2,"useCaseSequence":0},{"id":4,"customerDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T16:24:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T16:41:30.000+0000","createdBy":"superadmin"},"migrationType":"migration","migrationSubType":"commission","checklistFileName":"Verizon_vLSM_Migration Checklist_1.0.3.xlsx","neName":"056001_BOSTON_CONVENTION_CTR_DAS2","progressStatus":"InProgress","ciqName":"UNY-NE-VZ_CIQ_Ver2.82_01282019.xlsx","testName":"sadf","testDescription":"asdf","lsmName":"bhuvana","lsmVersion":"1.2.3","useCase":"test","status":"PASS","userName":null,"creationDate":"2019-03-15T17:34:55.000+0000","useCaseDetails":"test?1?1","customerId":2,"useCaseSequence":0},{"id":3,"customerDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T16:24:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T16:41:30.000+0000","createdBy":"superadmin"},"migrationType":"migration","migrationSubType":"commission","checklistFileName":"Verizon_vLSM_Migration Checklist_1.0.3.xlsx","neName":"056001_BOSTON_CONVENTION_CTR_DAS2","progressStatus":"InProgress","ciqName":"UNY-NE-VZ_CIQ_Ver2.82_01282019.xlsx","testName":"sfd","testDescription":"safd","lsmName":"bhuvana","lsmVersion":"1.2.3","useCase":"test","status":"PASS","userName":null,"creationDate":"2019-03-15T17:30:41.000+0000","useCaseDetails":"test?1?1","customerId":2,"useCaseSequence":0},{"id":2,"customerDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T16:24:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T16:41:30.000+0000","createdBy":"superadmin"},"migrationType":"migration","migrationSubType":"commission","checklistFileName":"Verizon_vLSM_Migration Checklist_1.0.3.xlsx","neName":"056001_BOSTON_CONVENTION_CTR_DAS2","progressStatus":"InProgress","ciqName":"UNY-NE-VZ_CIQ_Ver2.82_01282019.xlsx","testName":"asdfsfda","testDescription":"sdaffas","lsmName":"bhuvana","lsmVersion":"1.2.3","useCase":"test","status":"PASS","userName":null,"creationDate":"2019-03-15T15:45:03.000+0000","useCaseDetails":"test?1?1","customerId":2,"useCaseSequence":0},{"id":1,"customerDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T16:24:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T16:41:30.000+0000","createdBy":"superadmin"},"migrationType":"migration","migrationSubType":"commission","checklistFileName":"Verizon_vLSM_Migration Checklist_1.0.3.xlsx","neName":"056001_BOSTON_CONVENTION_CTR_DAS2","progressStatus":"Completed","ciqName":"UNY-NE-VZ_CIQ_Ver2.82_01282019.xlsx","testName":"sdff","testDescription":"dsfg","lsmName":"oneLSM","lsmVersion":"1.2.3","useCase":"2scripts","status":"PASS","userName":null,"creationDate":"2019-03-15T15:37:58.000+0000","useCaseDetails":"2scripts?3?1","customerId":2,"useCaseSequence":0}],"status":"SUCCESS"}');
                    this.totalPages = this.tableData.pageCount;

                    let pageCount = [];
                    for (var i = 1; i <= this.tableData.pageCount; i++) {
                        pageCount.push(i);
                    }
                    this.pageRenge = pageCount;
                    this.pager = this.getPager(this.totalPages * this.pageSize, this.currentPage, this.pageRenge);
                    this.isItInProgress=this.tableData.isInProgress;

                    showLoader ? this.showLoader = false : "";

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
        this.fromDt="";
        this.tableShowHide = true;
        this.getRunTest();
        // Close if edit form is in open state
        if (this.currentEditRow != undefined) {
            this.currentEditRow.className = "editRow";
        }
      //  this.editableFormArray = [];
      this.searchForm.nativeElement.reset();

        
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
        this.ciqDetails = [];
        
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
                            "lsmVersion": /* this.selSearchVer ? this.selSearchVer.name : "", */ currentForm.querySelector("#searchVersion").value,
                            "lsmName": /* this.selSearchSMName ? this.selSearchSMName.name : "", */ currentForm.querySelector("#searchSMName").value,                            
                            // "ciqName": this.selSearchCiqName ? this.selSearchCiqName.ciqFileName : "",//currentForm.querySelector("#searchCiqName").value,
                            "ciqName": currentForm.querySelector("#searchCiqName").value,
                            "neName": currentForm.querySelector("#searchNeName").value
                        };

                    if (searchCrtra.fromDate || searchCrtra.toDate || searchCrtra.testName || searchCrtra.lsmVersion ||searchCrtra.lsmName || searchCrtra.ciqName || searchCrtra.neName) {
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
    
    getUseCase() {        //lsmSelectedName
        this.useCaseValue = [];
        this.scriptValue =[];
        this.selectedItems = [];
        this.dropdownList = [];        
        let useCaseList = this.tableData.useCaseList;//lsmSelectedName.useCaseList;
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
   
    getNEList(selectedCiqName, updateSessionStorage = false){ 
        if(this.ciqDetails)
        {
        this.selectedNEItems =[];       
        this.showLoader = true;
        // Update the sessionStorage selected CIQ if CIQ list is getting changed from UI dropdown
        updateSessionStorage ? this.sharedService.updateSelectedCIQInSessionStorage(selectedCiqName) : "";
        this.ranatpService.getNeListData(selectedCiqName.ciqFileName, this.sharedService.createServiceToken() )
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
                          this.neNameList = jsonStatue.eNBList;
                        //   for (let itm of this.neNameList) {
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
                            allowSearchFilter: true
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
            this.dropdownNEList = [];  
        }
      }
      /**
       * 
       * This function will call two APIs, one to get NE Config details which will auto select NE Version and NE NAME
       * and other one is to get the Band List
       */
      
      onChangeNEs(event){
        //   this.getNeConfigDetails();

          //   get BandList
          this.selectedBandItems = [];
          /* let neDetails = [];
          this.useCaseValue = [];
          this.scriptValue = [];
          this.selectedItems = [];  */
          if (this.selectedNEItems) {
              this.showLoader = true;
              this.ranatpService.getBandListData(this.ciqDetails.ciqFileName, this.selectedNEItems, this.sharedService.createServiceToken())
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
                                          this.dropdownBandList = [];
                                          let bandNameList = jsonStatue.bandName;
                                          for (let itm of bandNameList) {
                                              let dropdownList = { item_id: itm, item_text: itm };
                                              this.dropdownBandList.push(dropdownList);
                                          }
                                          this.dropdownSettingsBand = {
                                              singleSelection: false,
                                              idField: 'item_id',
                                              textField: 'item_text',
                                              selectAllText: 'Select All',
                                              unSelectAllText: 'UnSelect All',
                                              itemsShowLimit: 1,
                                              allowSearchFilter: false
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
                              let jsonStatue = JSON.parse('{"bandName":["Band_1","Band_2","Band_3","Band_4","Band_5"],"sessionId":"aa5393be","serviceToken":"77360","status":"SUCCESS"}');
                              if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
                                  this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
                              }
                              if (jsonStatue.status == "SUCCESS") {
                                  this.dropdownBandList = [];
                                  let bandNameList = jsonStatue.bandName;
                                  for (let itm of bandNameList) {
                                      let dropdownList = { item_id: itm, item_text: itm };
                                      this.dropdownBandList.push(dropdownList);
                                  }
                                  this.dropdownSettingsBand = {
                                      singleSelection: false,
                                      idField: 'item_id',
                                      textField: 'item_text',
                                      selectAllText: 'Select All',
                                      unSelectAllText: 'UnSelect All',
                                      itemsShowLimit: 1,
                                      allowSearchFilter: false
                                  };
                              } else {
                                  this.showLoader = false;
                              }
                          }, 100); */
                          //Please Comment while checkIn
                      });

            /*Service for usecase List*/
            /* for (let i of this.selectedNEItems) {
                let selNE = {
                    "neId": i.item_id,
                    "neName": i.item_text
                }
                neDetails.push(selNE);
            }
            this.ranatpService.getUseCaseListData(this.ciqDetails.ciqFileName, neDetails, this.sharedService.createServiceToken())
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
                    }); */
                
          }
          else {
            this.dropdownBandList = [];
            //this.dropdownList = [];
          }
    
      }

      getNeConfigDetails() {
        if(this.selectedNEItems) {
            // this.showLoader = true;  No Need to show loader as already shown in onChangeNE
            this.ranatpService.getNeConfigDetails(this.sharedService.createServiceToken(), this.ciqDetails, this.selectedNEItems)
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
        
                                    } else {
                                        // this.showLoader = false;
                                    }
                                }
                            }

                        }, 0);
                    },
                    error => {
                        //Please Comment while checkIn
                        /* setTimeout(() => {
                            this.showLoader = false;
                            let jsonStatue = JSON.parse('{"sessionId":"458fa5b1","serviceToken":60748,"lsmVersion":"9.5.0","lsmName":"West_Syracuse","lsmid":84,"status":"SUCCESS"}');
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

                            } else {
                                this.showLoader = false;
                            }
                        }, 100); */
                        //Please Comment while checkIn
                    });
        }
        else {
            this.versionDetails = "";
            this.nameDetails = "";
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
            if (files[i].name.indexOf('.sh') >= 0 || files[i].name.indexOf('.py') >= 0) {

            } else {
                invalidFilenames.push(files[i].name);
            }
        }

        /*   if (invalidFilenames.length > 0) {
              $(event.target).parents("form").find("#upload").attr("disabled", "disabled").addClass("buttonDisabledCreate");
              $(event.target).parents("form").find(".dbImportButtonDisabled").removeClass("displayNone");
          } else {
              $(event.target).parents("form").find("#upload").removeAttr("disabled").removeClass("buttonDisabledCreate");
              $(event.target).parents("form").find(".dbImportButtonDisabled").addClass("displayNone");
          } */

    };

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
        this.selectedBandItems = [];
        this.ckeckedOrNot = false;
        this.ciqDetails=[];
    
    }

    /* itemSelectionChanged(event) {
        if (this.selectedItems.length > 0) {
            this.validationData.rules.location.customfunction = false;
            
        } else {
            this.validationData.rules.location.customfunction = true;
        }
        validator.performValidation(event, this.validationData, "save_update");
    } */

    submitRunTestForm(event,requestType) {
        if (this.selectedItems && this.selectedItems.length > 0) {
            this.validationData.rules.location.required = false;
        } else {
            this.validationData.rules.location.required = true;
        }
        if (this.selectedNEItems) {
            this.validationData.rules.neName.required = false;

        } else {
            this.validationData.rules.neName.required = true;
        }
        if (this.selectedBandItems && this.selectedBandItems.length > 0) {
            this.validationData.rules.bandName.required = false;
        }
        else {
            this.validationData.rules.bandName.required = true;
        }
        if (this.ciqDetails !="" && this.ciqDetails != undefined &&  this.ciqDetails.ciqFileName) {
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
            this.validationData.rules.bandName.required = false;
            // this.validationData.rules.filePost.required = false;
        }else {
            this.validationData.rules.lsmVersion.required = false;
            this.validationData.rules.lsmName.required = false; 
           // this.validationData.rules.neName.required = true;  
            this.validationData.rules.testName.required = true; 
          //  this.validationData.rules.ciqName.required = true;  
            //this.validationData.rules.location.required = true;
            // this.validationData.rules.bandName.required = true;
            // this.validationData.rules.filePost.required = true; //Not a mandatory field now 04/08/2020
        }

        const formdata = new FormData();
        let files: FileList = this.filePostRef.nativeElement.files,
            filenames = [];
        for (var i = 0; i < files.length; i++) {
            formdata.append("UPLOAD", files[i]);
            //formdata.append(ciqFiles[i].name, ciqFiles[i]);
            filenames.push(files[i].name);
        }
       

        validator.performValidation(event, this.validationData, "save_update");
        setTimeout(() => {
            if (this.isValidForm(event)) {
                this.showLoader = true;
                let useCaseDetails = [], neDetails = [], bandDetails = [],scriptDetails=[];;
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
                if(this.selectedNEItems) {
                    let selNE = {
                        "neId": this.selectedNEItems.eNBId,
                        "neName": this.selectedNEItems.eNBName
                    }
                    neDetails.push(selNE);
                }
                /* for(let i of this.selectedBandItems) {
                    bandDetails.push(i.item_text);
                } */
                let currentForm = event.target.parentNode.parentNode,
                    runTestFormDetails = {
                        "testname": currentForm.querySelector("#testName").value,                        
                        "lsmVersion": this.versionDetails ? this.versionDetails.name : "",
                        "lsmName": this.nameDetails ? this.nameDetails.name : "",
                        "lsmId":this.nameDetails ? this.nameDetails.id : "",
                        "ciqName":this.ciqDetails ? this.ciqDetails.ciqFileName : "",
                        "checklistFileName": this.ciqDetails ? this.ciqDetails.checklistFileName : "",
                        "checklistFilePath": this.ciqDetails ? this.ciqDetails.checklistFilePath : "",
                        "neDetails":neDetails,
                        "bandName" : this.selectedBandItems,
                        "useCase": useCaseDetails,
						"scripts" :scriptDetails,
                        "testDesc": currentForm.querySelector("#testDescription").value,
                        "password":"",
                        "currentPassword":this.ckeckedOrNot
                    };
                    
                this.runTestDetails = runTestFormDetails;

                this.ranatpService.uploadRunTestDetails(this.sharedService.createServiceToken(), runTestFormDetails, formdata, this.commissionType,requestType)
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
                                        this.serverInfo = jsonStatue.password;
                                        this.requestType = jsonStatue.requestType;
                                        this.pswdModalBlock =  this.modalService.open(this.passwordModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal connectionPrompt' });                                 
                                    }else {
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

                                let jsonStatue = {"password":{"serverName":"sane Ratul","serverIp":"10.20.120.82"},"requestType":"RUN_TEST","sessionId":"9a1afbd4","serviceToken":"67492","status":"PROMPT"};

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
                                    this.serverInfo = jsonStatue.password;
                                    this.requestType = jsonStatue.requestType;
                                    this.pswdModalBlock =  this.modalService.open(this.passwordModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal connectionPrompt' });                                 
                                }else{
                                    this.showLoader = false;
                                   // this.displayModel(jsonStatue.reason, "failureIcon");

                                }

                            }, 100); */

                            //Please Comment while checkIn   
                        });
            }
        }, 0);

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
            for (let scriptList of this.selectedItems) {
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
            
        }, 0);

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
        this.viewModalBlock = this.modalService.open(content, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView' });
         setTimeout(() => {
            let tableWidth = document.getElementById('useCaseScrollHead').scrollWidth;
            $(".useCaseWrapper .scrollBody table#useCaseTable").css("min-width", (tableWidth) + "px");
            $(".useCaseWrapper .scrollHead table#useCaseScrollHead").css("width", tableWidth + "px");

             $(".useCaseWrapper .scrollBody").on("scroll", function (event) {
               /* $(".formEditRow #formWrapper").css("margin-left", (event.target.scrollLeft - 5) + "px"); */
               $(".useCaseRow .form-control-fixed").css("right", (event.target.scrollLeft * -1) + "px");
                $(".useCaseWrapper .scrollHead table#useCaseScrollHead").css("margin-left", (event.target.scrollLeft * -1) + "px");
            });           
            $(".useCaseWrapper .scrollBody").css("max-height", (this.tableDataHeight - $("#tabContentWrapper").height()) + "px");           
                   

        }, 100); 
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
                if ($(rows[i]).find("td:eq(3)").text() == $("#updatedExeSeq_" + index).val()) {             
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
        }
        else {
            this.scriptValue[index].updatedExecution = 0;
        }
        this.scriptValue[index].rowUpdate = true;
    }
    toggleSitesTable() {
        // Remove all interval calls for table refresh
        clearInterval( this.interval );
        this.interval = null;
        // Called to reload the table data
        setTimeout(() => this.updateRunTestTable(true), 100);
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

                this.ranatpService.deleteRunTestData(rowId, this.sharedService.createServiceToken(), this.commissionType)
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
                                        this.message = "Data deleted successfully!";
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
                                    this.message = "Data deleted successfully!";
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
        // console.log(key);
        // Clear the interval for load api when it is in result mode
        clearInterval( this.interval );
        this.interval=null;

        this.testName = key.testName;
        this.testId = key.id;
        this.showLoader = true;
        
        this.ranatpService.viewTestResult(this.sharedService.createServiceToken(), key.id,key.lsmName,key.neName)
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
                                this.testResultsData = jsonStatue.useCaseResult;
                                this.tabularResultsData = JSON.parse(JSON.stringify(this.testResultsData))
                                this.successModalBlock1 = this.modalService.open(content, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView' });
                            } else {
                                this.showLoader = false;
                                this.displayModel(jsonStatue.status, "failureIcon");

                            }
                        }
                    }
                },
                error => {
                    //Please Comment while checkIn

                     /* setTimeout(() => {
     
                         this.showLoader = false;
     
                         //let jsonStatue = JSON.parse('{"programName":"VZN-4G-LEGACY","useCaseResult":[{"useCaseName":"test","useCaseId":1,"script_status":[{"scriptId":2,"File_Rules":[{"ruleDefinition":"FileRuleName : jhjhjk,FileName : hjghjg,SearchParam : jhghjghj,Status : Pass","ruleName":"jhjhjk","status":"FAIL"},{"ruleDefinition":"FileRuleName : jhjhjk,FileName : hjghjg,SearchParam : jhghjghj,Status : Pass","ruleName":"jhjhjk","status":"WARN"},{"ruleDefinition":"FileRuleName : jhjhjk,FileName : hjghjg,SearchParam : jhghjghj,Status : Pass","ruleName":"jhjhjk","status":"NO DATA"}],"Command_Rules":[{"ruleDefinition":"CommandRuleName : lsjCommandName : ls -lrtOperandColumnOne : OperandOne : rootOperandColumnTwo : OperandTwo : rootOperator : CONTAINSStatus : Pass","ruleName":"ls -lrt","status":"FAIL"},{}],"scriptName":"script1.sh"}]}],"sessionId":"3e6ff1c","serviceToken":"85575","status":"SUCCESS"}');
                        // let jsonStatue = JSON.parse('{"SMName":"Chicago","programName":"SPT-4G-MIMO","NEName":"CHCKILZXBBULTE0516276","useCaseResult":[{"useCaseName":"test","useCaseId":72,"script_status":[{"scriptId":258,"Command_Rules":[{"ruleDefinition":"CommandRuleName : Eveining Rule1_5th June, CommandName : RTRV-GPS-INVT, OperandColumnOne : FW_ID,UNIT_ID,FAMILY_TYPE, OperandOne : 0,UCCM[0],UCCM, OperandColumnTwo : VERSION,SERIAL, OperandTwo : 1.0.0.1,RS525151735, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule1_5th June","status":"NO DATA"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"PASS"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"WARN"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"FAIL"}],"scriptName":"ls","scriptExeSeq":1}]}],"sessionId":"ecb5fcf3","serviceToken":"60021","status":"SUCCESS"}');

                         //command and shell rules
                         //let jsonStatue = JSON.parse('{"SMName":"Chicago","programName":"SPT-4G-MIMO","NEName":"CHCKILZXBBULTE0516276","useCaseResult":[{"useCaseName":"test","useCaseId":72,"script_status":[{"scriptId":258,"Shell_Rules":[{"ruleDefinition":"CommandRuleName : 17th June Shell Rule3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"FAIL"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rule3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"WARN"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rule3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"NO DATA"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rule3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"PASS"}],"Command_Rules":[{"ruleDefinition":"CommandRuleName : Eveining Rule1_5th June, CommandName : RTRV-GPS-INVT, OperandColumnOne : FW_ID,UNIT_ID,FAMILY_TYPE, OperandOne : 0,UCCM[0],UCCM, OperandColumnTwo : VERSION,SERIAL, OperandTwo : 1.0.0.1,RS525151735, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule1_5th June","status":"NO DATA"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"PASS"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"WARN"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"FAIL"}],"scriptName":"ls","scriptExeSeq":1}]}],"sessionId":"ecb5fcf3","serviceToken":"60021","status":"SUCCESS"}');
                         let jsonStatue = JSON.parse('{"sessionId":"ecb5fcf3","serviceToken":"60021","status":"SUCCESS","SMName":"Chicago","programName":"SPT-4G-MIMO","NEName":"CHCKILZXBBULTE0516276","useCaseResult":[{"useCaseName":"test","useCaseId":72,"script_status":[{"scriptId":258,"scriptName":"ls","scriptExeSeq":21,"Shell_Rules":[{"ruleDefinition":"CommandRuleName : 17th June Shell Rule233, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"717th June Shell Rule3","status":"FAIL"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rul45e3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"147th June Shell Rule3","status":"PASS"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rul765e3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"517th June Shell Rule3","status":"WARN"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rule55673, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"517th June Shell Rule3","status":"PASS"},{"ruleDefinition":"CommandRuleName : 17th June Shell R67ule3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"1574th June Shell Rule3","status":"WARN"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rul678e3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"14567th June Shell Rule3","status":"FAIL"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rul456e3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"1677th June Shell Rule3","status":"WARN"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rule673, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"1677th June Shell Rule3","status":"WARN"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rul9e3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"1387th June Shell Rule3","status":"NO DATA"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rule083, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"1897th June Shell Rule3","status":"PASS"}],"Command_Rules":[{"ruleDefinition":"CommandRuleName : Eveining Rule1_5th June, CommandName : RTRV-GPS-INVT, OperandColumnOne : FW_ID,UNIT_ID,FAMILY_TYPE, OperandOne : 0,UCCM[0],UCCM, OperandColumnTwo : VERSION,SERIAL, OperandTwo : 1.0.0.1,RS525151735, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule1_5th June","status":"PASS"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"FAIL"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"NO DATA"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"FAIL"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"NO DATA"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"PASS"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"FAIL"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"PASS"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"PASS"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"WARN"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"PASS"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"WARN"}]},{"scriptId":258,"Shell_Rules":[{"ruleDefinition":"CommandRuleName : 17th June Shell Rule3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"FAIL"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rule3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"WARN"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rule3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"PASS"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rule3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"WARN"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rule3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"PASS"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rule3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"WARN"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rule3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"NO DATA"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rule3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"NO DATA"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rule3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"NO DATA"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rule3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"PASS"}],"Command_Rules":[{"ruleDefinition":"CommandRuleName : Eveining Rule1_5th June, CommandName : RTRV-GPS-INVT, OperandColumnOne : FW_ID,UNIT_ID,FAMILY_TYPE, OperandOne : 0,UCCM[0],UCCM, OperandColumnTwo : VERSION,SERIAL, OperandTwo : 1.0.0.1,RS525151735, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule1_5th June","status":"NO DATA"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"FAIL"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"WARN"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"FAIL"}],"scriptName":"ls","scriptExeSeq":1}]},{"useCaseName":"748 test","useCaseId":72,"script_status":[{"scriptId":258,"scriptName":"ls","scriptExeSeq":1,"Shell_Rules":[{"ruleDefinition":"CommandRuleName : 17th June Shell Rule233, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"FAIL"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rul45e3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"PASS"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rul765e3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"WARN"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rule55673, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"PASS"},{"ruleDefinition":"CommandRuleName : 17th June Shell R67ule3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"WARN"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rul678e3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"FAIL"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rul456e3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"WARN"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rule673, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"WARN"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rul9e3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"NO DATA"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rule083, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"PASS"}],"Command_Rules":[{"ruleDefinition":"CommandRuleName : Eveining Rule1_5th June, CommandName : RTRV-GPS-INVT, OperandColumnOne : FW_ID,UNIT_ID,FAMILY_TYPE, OperandOne : 0,UCCM[0],UCCM, OperandColumnTwo : VERSION,SERIAL, OperandTwo : 1.0.0.1,RS525151735, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule1_5th June","status":"PASS"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"FAIL"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"NO DATA"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"FAIL"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"NO DATA"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"PASS"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"FAIL"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"PASS"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"PASS"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"WARN"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"PASS"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"WARN"}]},{"scriptId":258,"Shell_Rules":[{"ruleDefinition":"CommandRuleName : 17th June Shell Rule3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"FAIL"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rule3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"WARN"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rule3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"PASS"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rule3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"WARN"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rule3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"PASS"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rule3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"WARN"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rule3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"NO DATA"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rule3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"NO DATA"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rule3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"NO DATA"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rule3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"PASS"}],"Command_Rules":[{"ruleDefinition":"CommandRuleName : Eveining Rule1_5th June, CommandName : RTRV-GPS-INVT, OperandColumnOne : FW_ID,UNIT_ID,FAMILY_TYPE, OperandOne : 0,UCCM[0],UCCM, OperandColumnTwo : VERSION,SERIAL, OperandTwo : 1.0.0.1,RS525151735, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule1_5th June","status":"NO DATA"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"FAIL"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"WARN"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"FAIL"}],"scriptName":"pwd","scriptExeSeq":1}]},{"useCaseName":"123 test","useCaseId":72,"script_status":[{"scriptId":258,"scriptName":"12svn st","scriptExeSeq":1,"Shell_Rules":[{"ruleDefinition":"CommandRuleName : 17th June Shell Rule233, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"FAIL"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rul45e3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"PASS"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rul765e3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"WARN"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rule55673, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"PASS"},{"ruleDefinition":"CommandRuleName : 17th June Shell R67ule3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"WARN"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rul678e3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"FAIL"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rul456e3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"WARN"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rule673, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"WARN"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rul9e3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"NO DATA"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rule083, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"PASS"}],"Command_Rules":[{"ruleDefinition":"CommandRuleName : Eveining Rule1_5th June, CommandName : RTRV-GPS-INVT, OperandColumnOne : FW_ID,UNIT_ID,FAMILY_TYPE, OperandOne : 0,UCCM[0],UCCM, OperandColumnTwo : VERSION,SERIAL, OperandTwo : 1.0.0.1,RS525151735, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule1_5th June","status":"PASS"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"FAIL"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"NO DATA"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"FAIL"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"NO DATA"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"PASS"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"FAIL"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"PASS"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"PASS"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"WARN"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"PASS"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"WARN"}]},{"scriptId":258,"Shell_Rules":[{"ruleDefinition":"CommandRuleName : 17th June Shell Rule3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"FAIL"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rule3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"WARN"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rule3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"PASS"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rule3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"WARN"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rule3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"PASS"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rule3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"WARN"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rule3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"NO DATA"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rule3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"NO DATA"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rule3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"NO DATA"},{"ruleDefinition":"CommandRuleName : 17th June Shell Rule3, CommandName : ls -lrt, OperandColumnOne : drwxrwxrwx, OperandOne : Jimi Hendrix, OperandColumnTwo : , OperandTwo : , Operator : , Status : Pass","ruleName":"17th June Shell Rule3","status":"PASS"}],"Command_Rules":[{"ruleDefinition":"CommandRuleName : Eveining Rule1_5th June, CommandName : RTRV-GPS-INVT, OperandColumnOne : FW_ID,UNIT_ID,FAMILY_TYPE, OperandOne : 0,UCCM[0],UCCM, OperandColumnTwo : VERSION,SERIAL, OperandTwo : 1.0.0.1,RS525151735, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule1_5th June","status":"NO DATA"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"FAIL"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"WARN"},{"ruleDefinition":"CommandRuleName : Eveining Rule2_5th June, CommandName : RTRV-SYS-SIGIP, OperandColumnOne : VR_ID,SIG_IPVER,S1_PRI_IPV4_ADDR, OperandOne : 0,IPV4,10.149.241.106, OperandColumnTwo : S1_SEC_IPV4_ADDR,X2_PRI_IPV4_ADDR, OperandTwo : 0.0.0.0,10.149.241.106, Operator : CONTAINS, Status : Pass","ruleName":"Eveining Rule2_5th June","status":"FAIL"}],"scriptName":"10svn st","scriptExeSeq":1}]}]}');

     
                        if (jsonStatue.status == "SUCCESS") {
                             this.showLoader = false;
                             this.pgmName = jsonStatue.programName;
                             this.smName = jsonStatue.SMName;
                                this.neName = jsonStatue.NEName;
                             this.testResultsData = jsonStatue.useCaseResult;   
                             this.tabularResultsData = JSON.parse(JSON.stringify(this.testResultsData));
                             this.successModalBlock1 = this.modalService.open(content, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView' });                              
                         } else {
                             this.showLoader = false;
                             this.displayModel(jsonStatue.status, "failureIcon");
     
                         }
     
                     }, 100); */

                    //Please Comment while checkIn   
                });


    }

    reRun(key) {
        this.testId = key.id;
        this.showLoader = true;

        this.ranatpService.reRunTest(this.sharedService.createServiceToken(), key.id, key.useCaseDetails)
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
                                this.displayModel(jsonStatue.status, "failureIcon");

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
                            this.displayModel(jsonStatue.status, "failureIcon");
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
        this.ranatpService.getChecklistSheetDetails(this.sharedService.createServiceToken(), this.rowData)
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
            this.ranatpService.getDeatilsByChecklist(this.sharedService.createServiceToken(), this.paginationDetails, this.rowData)
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
                    //   let jsonStatue = {"sessionId":"19088022","serviceToken":"66957","status":"SUCCESS","pageCount":4,"SheetDisplayDetails":{"count":4,"list":[{"id":1,"fileName":"Verizon_vLSM_Migration Checklist_1.0.3.xlsx","sheetName":"Migration-Checklist","seqOrder":"1","ciq":null,"program":null,"enodeId":null,"checkListMap":{"check":false,"INDEX":"1","TASK":"PreMigration Activities: \n1. eNB NBR relation, EUTRAN NBR Relation and Site specific scripts from RF\n2. CIQ Verification\n3. Prepare eNB Grow and Cell Grow templates\n4. Prepare commissioing script\n5. Prepare env file","PROCEDURE":"","Notes":"","Start_Time":"NA","End_Time":"NA","Remarks":"Hello World"}},{"id":2,"fileName":"Verizon_vLSM_Migration Checklist_1.0.3.xlsx","sheetName":"Migration-Checklist","seqOrder":"1","ciq":null,"program":null,"enodeId":null,"checkListMap":{"check":true,"INDEX":"2","TASK":"RECEIVE CIQ AND GROW eNB ","PROCEDURE":"check latest ciq from local winscp 13.59.191.103","Notes":"Please change the eNB Grow Profile for LTE IP address to “FALSE”. This will prevent neighbor eNBs from communicating with this eNB.\nExample:\nInterface: ge_0_0_1.301 (Note: 301 is S1,X2 C/U plane vlan)\nSignal S1 = false\nSignal X2 = false\nBearer S1 = false\nBearer X2 = false","Start_Time":"","End_Time":"","Remarks":"Hello "}}]}};
                    let jsonStatue = {"pageCount":6,"SheetDisplayDetails":{"count":6,"list":[{"id":28469,"fileName":"Verizon_Migration Checklist 100418_version4.8 (2).xlsx","sheetName":"Migration","seqOrder":"1","ciq":null,"program":null,"enodeName":"056008_CAMBSIDE_GALL_MA","runTestId":242,"stepIndex":1,"checkListMap":{"check":"","ITEM":"","STEPS":"PreMigration Activities:    gather Input files  SS/CA\n   get Site Specific and CA from RF,   WINSCP to VM   from Xshell New Jersey Sane\n   cd RF/v0chand1        sftp \"username\"@[2001:4888:a03:2100:c0:fef:0:78]\n             mget <CA & Site Specific>\n             exit\n      cp /home/lsm/aceman/web/etc/batch/RF/v0chand1","NOTES":"","COMMENTS":"","Remarks":""}},{"id":28470,"fileName":"Verizon_Migration Checklist 100418_version4.8 (2).xlsx","sheetName":"Migration","seqOrder":"1","ciq":null,"program":null,"enodeName":"056008_CAMBSIDE_GALL_MA","runTestId":242,"stepIndex":2,"checkListMap":{"check":"","ITEM":"1","STEPS":"RECEIVE CIQ AND GROW eNB                      ","NOTES":"check latest ciq from local winscp 13.59.191.103","COMMENTS":"","Remarks":""}},{"id":28471,"fileName":"Verizon_Migration Checklist 100418_version4.8 (2).xlsx","sheetName":"Migration","seqOrder":"1","ciq":null,"program":null,"enodeName":"056008_CAMBSIDE_GALL_MA","runTestId":242,"stepIndex":3,"checkListMap":{"check":"","ITEM":"2","STEPS":"MRO/DAS-RFM SITE ONLY(applied immediately after eNB loading is completed)","NOTES":"D:\\Verizon CDU 30\\MOP\\MRO_DAS_Settings_for_ODAS_MOP\nYou only need to execute “1) DL_MAX_TX_PWR Change : “ section to update the internal PLD parameter. Whether or It is connetced to a DAS vendor unit present or not.\n\n1. When there is no active DAS Vendor unit on site connected to MRO/DAS RRH, then the input DL_Max_Tx_Pwr for the script will be same as the value used in Grow(CIQ:Col P)\n\n2. When there is a DAS Vendor Unit\nIn CIQ, it will indicate whether this procedure needs to be executed. Last column in CIQ has “DAS” to indicate if external DAS is present,\nand change needs to be made for those cells. (CIQ: Col AU)\n\nThe other 2 procedures in the MOP are now included in SSS. You can use the RTRV commands for troubleshooting\n\nwhere to find output power:.\nCIQ :Col P: Output Power (dBm)- To be used for grow.\nCIQ: Col AU: DAS Output Power- To be used for the MOP.","COMMENTS":"","Remarks":""}},{"id":28472,"fileName":"Verizon_Migration Checklist 100418_version4.8 (2).xlsx","sheetName":"Migration","seqOrder":"1","ciq":null,"program":null,"enodeName":"056008_CAMBSIDE_GALL_MA","runTestId":242,"stepIndex":4,"checkListMap":{"check":"","ITEM":"3","STEPS":"NEIGHBORS List Scripts\n1.  NbrList-NBR-ENB","NOTES":"Execute  script as soon as site is ENABLED","COMMENTS":"","Remarks":""}},{"id":28473,"fileName":"Verizon_Migration Checklist 100418_version4.8 (2).xlsx","sheetName":"Migration","seqOrder":"1","ciq":null,"program":null,"enodeName":"056008_CAMBSIDE_GALL_MA","runTestId":242,"stepIndex":5,"checkListMap":{"check":"","ITEM":"4","STEPS":"VERIFY AND UPGRADE THE FIRMWARE AND THE SOFTWARE  ","NOTES":"7.0.2.1","COMMENTS":"","Remarks":""}},{"id":28474,"fileName":"Verizon_Migration Checklist 100418_version4.8 (2).xlsx","sheetName":"Migration","seqOrder":"1","ciq":null,"program":null,"enodeName":"056008_CAMBSIDE_GALL_MA","runTestId":242,"stepIndex":6,"checkListMap":{"check":"","ITEM":"5","STEPS":"VERIFY no RRH Alarms exist","NOTES":"check to see if there's any RRH HW Mismatch","COMMENTS":"","Remarks":""}},{"id":28475,"fileName":"Verizon_Migration Checklist 100418_version4.8 (2).xlsx","sheetName":"Migration","seqOrder":"1","ciq":null,"program":null,"enodeName":"056008_CAMBSIDE_GALL_MA","runTestId":242,"stepIndex":7,"checkListMap":{"check":"","ITEM":"6","STEPS":"SFP audit","NOTES":"cd /home/lsm/Scripts/SfpFinder\nvi Conf/eNB.conf\n            edit add your enodeB\ncd Logs\ngrep FAILED *","COMMENTS":"","Remarks":""}},{"id":28476,"fileName":"Verizon_Migration Checklist 100418_version4.8 (2).xlsx","sheetName":"Migration","seqOrder":"1","ciq":null,"program":null,"enodeName":"056008_CAMBSIDE_GALL_MA","runTestId":242,"stepIndex":8,"checkListMap":{"check":"","ITEM":"7","STEPS":"FW Upgrade of ALU RRH\n ‘RTRV-ALURRH-SW’ If FW egual to 2n Commerical RU FW [n] ==> Continue with C&I process\n            else \n FW Upgrade of ALU RRH MOP. See RRH FW Upgrade Tab for Detail Table; D:\\Verizon CDU 30\\MOP","NOTES":"Check RRH FW Upgrade Tab for Details","COMMENTS":"","Remarks":""}},{"id":28477,"fileName":"Verizon_Migration Checklist 100418_version4.8 (2).xlsx","sheetName":"Migration","seqOrder":"1","ciq":null,"program":null,"enodeName":"056008_CAMBSIDE_GALL_MA","runTestId":242,"stepIndex":9,"checkListMap":{"check":"","ITEM":"8","STEPS":"RUN COMMISSIONING SCRIPT\n                   Commission script is created base on the CIQ. (CSL IP, NTP Server,Alias_Name CRTE-VLAN, RRH Alarm threshold)  \nPlease Verify Audit REPORT with CIQ","NOTES":"run via rancom tool, \nuncheck vbs to run auto.\nVerify AUDIT REPORT with CIQ. Modify any discrepancy or De-grow/RE-Grow.","COMMENTS":"","Remarks":""}},{"id":28478,"fileName":"Verizon_Migration Checklist 100418_version4.8 (2).xlsx","sheetName":"Migration","seqOrder":"1","ciq":null,"program":null,"enodeName":"056008_CAMBSIDE_GALL_MA","runTestId":242,"stepIndex":10,"checkListMap":{"check":"","ITEM":"9","STEPS":"UDA Alarm Build ( Provide by RF or FOPS )","NOTES":"D:\\Verizon CDU 30\\MOP\\MRO_DAS_Settings_for_ODAS_MOP","COMMENTS":"","Remarks":""}}]},"sessionId":"2d8c37e7","serviceToken":"78773","status":"SUCCESS"};
    
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
        this.ranatpService.updateCheckList(this.sharedService.createServiceToken(), this.rowData, this.checklistTableData)
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

        this.ranatpService.scriptOutput(this.sharedService.createServiceToken(), this.testId, useCaseName, useCaseId, scriptName, scriptId)
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
                                this.displayModel(jsonStatue.status, "failureIcon");

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
                            this.displayModel(jsonStatue.status, "failureIcon");
    
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
        this.ranatpService.getRunningLogs(this.sharedService.createServiceToken(), key.id)
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

                        let jsonStatue = {"runningLog":"asklj sa kljsalk\n asjgas  hs \n ajsghas \n asjighas ","sessionId":"19088022","serviceToken":"66957","status":"SUCCESS","reason":""};
                        if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {

                            this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });

                        } else {
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
                                if (!this.runningLogInterval) {
                                    this.runningLogInterval = setInterval(() => {
                                        this.updateRunningLog();
                                    }, 1000);
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
        this.ranatpService.getRunningLogs(this.sharedService.createServiceToken(), this.rowData.id)
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
                        this.showInnerLoader = false;

                        let jsonStatue = {"runningLog":"spawn ssh -o StrictHostKeyChecking=no -oCheckHostIP=no user@127.0.0.1\nuser@127.0.0.1's password: \nWelcome to Ubuntu 16.04.3 LTS (GNU/Linux 4.10.0-28-generic x86_64)\n\n * Documentation:  https://help.ubuntu.com\n * Management:     https://landscape.canonical.com\n * Support:        https://ubuntu.com/advantage\n\n277 packages can be updated.\n9 updates are security updates.\n\nLast login: Mon Jun 24 17:33:29 2019 from 127.0.0.1\n\nssh -o StrictHostKeyChecking=no -oCheckHostIP=no user@127.0.0.1\nuser@user-OptiPlex-7010:~$ ssh -o StrictHostKeyChecking=no -oCheckHostIP=no u\n<ssh -o StrictHostKeyChecking=no -oCheckHostIP=no us                         \b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\ber@127.0.0.1\nuser@127.0.0.1's password: \nWelcome to Ubuntu 16.04.3 LTS (GNU/Linux 4.10.0-28-generic x86_64)\n\n * Documentation:  https://help.ubuntu.com\n * Management:     https://landscape.canonical.com\n * Support:        https://ubuntu.com/advantage\n\n277 packages can be updated.\n9 updates are security updates.\n\nLast login: Mon Jun 24 17:33:57 2019 from 127.0.0.1\n\nuser@user-OptiPlex-7010:~$ sudo su \n[sudo] password for user: \nroot@user-OptiPlex-7010:/home/user# \nspawn ssh -o StrictHostKeyChecking=no -oCheckHostIP=no user@127.0.0.1\nuser@127.0.0.1's password: \nWelcome to Ubuntu 16.04.3 LTS (GNU/Linux 4.10.0-28-generic x86_64)\n\n * Documentation:  https://help.ubuntu.com\n * Management:     https://landscape.canonical.com\n * Support:        https://ubuntu.com/advantage\n\n277 packages can be updated.\n9 updates are security updates.\n\nLast login: Mon Jun 24 17:34:07 2019 from 127.0.0.1\n\nssh -o StrictHostKeyChecking=no -oCheckHostIP=no user@127.0.0.1\nuser@user-OptiPlex-7010:~$ ssh -o StrictHostKeyChecking=no -oCheckHostIP=no u\n<ssh -o StrictHostKeyChecking=no -oCheckHostIP=no us                         \b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\ber@127.0.0.1\nuser@127.0.0.1's password: \nWelcome to Ubuntu 16.04.3 LTS (GNU/Linux 4.10.0-28-generic x86_64)\n\n * Documentation:  https://help.ubuntu.com\n * Management:     https://landscape.canonical.com\n * Support:        https://ubuntu.com/advantage\n\n277 packages can be updated.\n9 updates are security updates.\n\nLast login: Mon Jun 24 17:36:21 2019 from 127.0.0.1\n\nuser@user-OptiPlex-7010:~$ sudo su \n[sudo] password for user: \nroot@user-OptiPlex-7010:/home/user# \nroot@user-OptiPlex-7010:/home/user# ls\nbhuvanasmartmom.zip\nbhuvana.tar.gz\nCPAN-2.16\nCPAN-2.16.tar.gz\nDaily Activity.ods\nDesktop\nDocuments\nDownloads\nDrive\neclipse-workspace\nexamples.desktop\nfinal_5c853c7a3fc6200014a7df80_885019.mp4\nftp\nIMG_20181207_153237.jpg\nIMG_20181207_153310.jpg\nIMG_20181207_153526.jpg\nIMG_20181207_153537.jpg\njdk\nJson Reqest Response for migration Page .odt\nMongo.json\nMusic\nmvn\nperl5\nPictures\nPublic\npycharm-community-2018.3\nratAtpintput.log\nRCT\nRCT_workspace\nruntestserviceimplfor manultesting.txt\nsnap\ntag\ntask sheet.ods\nTemplates\ntomcat\nVideos\nroot@user-OptiPlex-7010:/home/user# df\nFilesystem     1K-blocks     Used Available Use% Mounted on\nudev             4040124        0   4040124   0% /dev\ntmpfs             812716     9592    803124   2% /run\n/dev/sda7      255590924 57783408 184801132  24% /\ntmpfs            4063564    94996   3968568   3% /dev/shm\ntmpfs               5120        4      5116   1% /run/lock\ntmpfs            4063564        0   4063564   0% /sys/fs/cgroup\n/dev/loop0          4864     4864         0 100% /snap/notepad-plus-plus/191\n/dev/loop1        135424   135424         0 100% /snap/postman/81\n/dev/loop2         93184    93184         0 100% /snap/core/6405\n/dev/loop3         91392    91392         0 100% /snap/core/6673\n/dev/loop4        133760   133760         0 100% /snap/postman/80\n/dev/loop5        450688   450688         0 100% /snap/wine-platform/101\n/dev/loop6        450816   450816         0 100% /snap/wine-platform/103\n/dev/loop7          3840     3840         0 100% /snap/notepad-plus-plus/193\n/dev/loop8         55040    55040         0 100% /snap/core18/731\n/dev/loop9        450816   450816         0 100% /snap/wine-platform/105\n/dev/loop10        55040    55040         0 100% /snap/core18/782\n/dev/loop11        36224    36224         0 100% /snap/gtk-common-themes/1198\n/dev/loop12        93312    93312         0 100% /snap/core/6531\n/dev/loop13         3840     3840         0 100% /snap/notepad-plus-plus/195\n/dev/loop14        35712    35712         0 100% /snap/gtk-common-themes/1122\n/dev/loop15       132864   132864         0 100% /snap/postman/73\n/dev/sda5         369639    65188    280848  19% /boot\ntmpfs             812716       64    812652   1% /run/user/1000\nroot@user-OptiPlex-7010:/home/user# pwd\n/home/user\nroot@user-OptiPlex-7010:/home/user# ls\nbhuvanasmartmom.zip\nbhuvana.tar.gz\nCPAN-2.16\nCPAN-2.16.tar.gz\nDaily Activity.ods\nDesktop\nDocuments\nDownloads\nDrive\neclipse-workspace\nexamples.desktop\nfinal_5c853c7a3fc6200014a7df80_885019.mp4\nftp\nIMG_20181207_153237.jpg\nIMG_20181207_153310.jpg\nIMG_20181207_153526.jpg\nIMG_20181207_153537.jpg\njdk\nJson Reqest Response for migration Page .odt\nMongo.json\nMusic\nmvn\nperl5\nPictures\nPublic\npycharm-community-2018.3\nratAtpintput.log\nRCT\nRCT_workspace\nruntestserviceimplfor manultesting.txt\nsnap\ntag\ntask sheet.ods\nTemplates\ntomcat\nVideos\nroot@user-OptiPlex-7010:/home/user# \nroot@user-OptiPlex-7010:/home/user# \n","sessionId":"31e4c050","serviceToken":"63263","status":"SUCCESS","reason":""};
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
                                this.showInnerLoader = false;
                                this.displayModel(jsonStatue.reason, "failureIcon");
                            }
                        }
                    }, 1000); */
                    //Please Comment while checkIn
                });
    }

    downloadLogs(key) {

        this.ranatpService.downloadFile(this.sharedService.createServiceToken(), key)
            .subscribe(
                data => {
                    let fileName = key.neName + "_" + key.migrationSubType + "_" + key.testName + "_" + Math.round(new Date().getTime() / 1000) + ".zip";

                    let blob = new Blob([data["_body"]], {
                        type: "application/octet-stream"    //"text/plain;charset=utf-8"
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

    downloadResult(fileName,filePath) {

        this.ranatpService.downloadResultFile(fileName,filePath,this.sharedService.createServiceToken())
        .subscribe(
            data => {
                let blob = new Blob([data["_body"]], {
                    type: "application/octet-stream"
                });
    
                FileSaver.saveAs(blob,fileName);              
    
            },
            error => {
                //Please Comment while checkIn
                /* let jsonStatue: any = {"sessionId":"506db794","reason":"Download Failed","status":"SUCCESS","serviceToken":"63524"};
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


   /*  downLoadResultFile(key) {
        this.showLoader = true;
        this.ranatpService.downLoadResultFile(this.sharedService.createServiceToken(), key)
            .subscribe(
                data => {
                    this.showLoader = false;
                    let fileName = key.result;
                    let blob = new Blob([data["_body"]], {
                        type: "application/octet-stream"
                    });

                    FileSaver.saveAs(blob, fileName);

                },
                error => {
                    //Please Comment while checkIn
                    let jsonStatue: any = { "sessionId": "506db794", "reason": "Download Failed", "status": "SUCCESS", "serviceToken": "63524" };
                    this.showLoader = false;
                    let fileName = key.result;
                    let blob = new Blob(["Hello World"], {
                        type: "application/octet-stream"
                    });

                    FileSaver.saveAs(blob, fileName);

                    setTimeout(() => {
                        this.showLoader = false;
                        if (jsonStatue.status == "SUCCESS") {

                        } else {
                            this.displayModel(jsonStatue.reason, "failureIcon");
                        }

                    }, 1000);
                    //Please Comment while checkIn

                });
    } */

      viewCheckConLog(content) {
        this.showLoader = true;
        this.ranatpService.getConnectionLog(this.sharedService.createServiceToken())
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

    changeSorting(predicate, event, index) {
        this.sharedService.dynamicSort(predicate, event, index, this.tableData.runTestTableDetails);
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
                const formdata = new FormData();
                let files: FileList = this.filePostRef.nativeElement.files,
                    filenames = [];
                for (var i = 0; i < files.length; i++) {
                    formdata.append("UPLOAD", files[i]);
                    //formdata.append(ciqFiles[i].name, ciqFiles[i]);
                    filenames.push(files[i].name);
                }
                this.ranatpService.uploadRunTestDetails(this.sharedService.createServiceToken(), this.runTestDetails, formdata, this.commissionType, this.requestType)
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
                                            this.message = "Connection Successful";
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

    showListOfSCriptFiles(popover, key) {
        this.scriptFilesData = [];//key.checkListMap.script;
        this.p1 = popover;

        this.showInnerLoader = true;
        this.ranatpService.getChecklistScriptDetails(this.sharedService.createServiceToken(), this.rowData, key.stepIndex)
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

                        this.showInnerLoader = false;

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
        console.log(key);
        key.value = $("#Remarks").val();
        this.checklistTableData[index].checkListMap.Remarks = $("#Remarks").val();
        this.editCLMode = -1;
    }
    cancelCheckListRow(event){
        this.editCLMode = -1;
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

    showListOfRule(popover) {
        this.p2 = popover;
        popover.open();
    }

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
 

 
     changeSortingScripts(predicate, event, index) {
         this.sharedService.dynamicSort(predicate, event, index, this.scriptValue);
     }
 
 
     changeSortingUsecases(predicate, event, index) {
         this.sharedService.dynamicSort(predicate, event, index, this.useCaseValue);
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

        this.ranatpService.downloadGenScripts(filePath,this.sharedService.createServiceToken())
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

}
