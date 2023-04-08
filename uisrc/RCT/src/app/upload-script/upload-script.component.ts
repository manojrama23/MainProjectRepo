import { ViewContainerRef, Input, ElementRef, Renderer, Component, OnInit, ViewChild, HostListener } from '@angular/core';
//import { Router } from '@angular/router';
import { Router, NavigationEnd } from '@angular/router';
import { NgbModal, ModalDismissReasons } from '@ng-bootstrap/ng-bootstrap';
import { SharedService } from '../services/shared.service';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { filter, map, mergeMap } from 'rxjs/operators';
import { validator } from '../validation';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { UploadscriptService } from '../services/uploadscriptservice.service';
import 'rxjs/add/operator/timeout';
import * as FileSaver from 'file-saver';
import * as _ from 'underscore';
import { NgbPopover } from '@ng-bootstrap/ng-bootstrap';
import * as $ from 'jquery';
import { summaryFileName } from '@angular/compiler/src/aot/util';
declare var jstz: any;

@Component({
    selector: 'app-upload-script',
    templateUrl: './upload-script.component.html',
    styleUrls: ['./upload-script.component.scss'],
    providers: [UploadscriptService]
})
export class UploadScriptComponent implements OnInit {
    /* termPswd: any;
    termUserName: any; */
    utilPswd: any;
    utilUserName: any;
    argumentDetailsList:any;
    scriptType:any;
    utilities: any;
    tableData: any;
    showNoDataFound: boolean;
    tableShowHide: boolean;
    showLoader: boolean = false;
    uploadBlock: boolean = false;
   // createBlock:boolean=false;
    searchBlock: boolean = false;
    scriptData: any;
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
    uploadRowId: any;
    lsmVersionDetails: any;
    currentEditRow: any;
    fileDetails: any;
    timezone: any;
    viewFileName: any;
    lsmNameDetails: any;
    terminals:any;
    nwTypeDetails: any;
    migrationType: string ="premigration";
    subType:any;
    selectedType:any=""
    selectedVersion: any = ""
    selectedLsmName: any = "";
    selectedUtil : any = "";
    selectedScript : any = "";
    selectedTerminal : any = "";
    lsmSelectedVersion:any="";
    nwtypeVal: any;
    lsmNameVal: any;
    lsmVersionVal: any;
    remarksVal: any;
    arguments: any;
    selUtility:any;
    nameDetails:any
    searchCriteria: any;
    searchStatus: string;
    navigationSubscription: any;
    // Following variables are used to dispaly success, confirm and failure model(s)
    showModelMessage: boolean = false;
    modelData: any;
    closeResult: string;
    successModalBlock: any;
    deleteUploadScriptBlock:any;
    message: any;
    sessionExpiredModalBlock: any; // Helps to close/open the model window
    pager: any = {}; // pager Object
    searchState:any = "";
    state:any="";
    p1: NgbPopover;
    prompt:any;
    max = new Date();
    fromDate:any;
    toDate:any;

    programChangeSubscription:any;

    validationData: any = {
        "rules": {
            "nwType": {
                "required": true,

            },
            "lsmName": {
                "required": false
            },
            "lsmVersion": {
                "required": false
            },
            "filePost": {
                "required": true
            },
            "UploadedBy": {
                "required": false
            },
            "remarks": {
                "required": false
            },
            "scriptType": {
                "required": true
            },
            "utility": {
                "required": true
            },
            "terminals": {
                "required": true
            },
            "connectionLocation": {
                "required": true
            },
            "connectionTerminal1": {
                "required": true
            }


        },
        "messages": {
            "nwType": {
                "required": "N/W TYPE is required",

            },
            "lsmName": {
                "required": "LSM NAME is required"
            },
            "lsmVersion": {
                "required": "LSM VERSION is required"
            },
            "filePost": {
                "required": "FILE is required"
            },
            "UploadedBy": {
                "required": "UPLOADED is required"
            },
            "scriptType": {
                "required": "SCRIPT TYPE is required"
            },
            "utility": {
                "required": "TERMINAL is required"
            },
            "terminals": {
                "required": "UTILITY is required"
            },
            "connectionLocation": {
                "required": "TERMINAL is required"
            },
            "connectionTerminal1": {
                "required": "UTILITY is required"
            }
            
        }
    };




    @ViewChild('uploadTab') uploadTabRef: ElementRef;
   // @ViewChild('createTab') createTabRef: ElementRef;
    @ViewChild('searchTab') searchTabRef: ElementRef;
    @ViewChild('bluePrintForm') bluePrintFormRef: ElementRef;
    @ViewChild('sessionExpiredModal') sessionExpiredModalRef: ElementRef;
    @ViewChild('filePost') filePostRef: ElementRef;
    @ViewChild('confirmModal') confirmModalRef: ElementRef;
    @ViewChild('deleteModal') deleteModalRef: ElementRef;
    @ViewChild('successModal') successModalRef: ElementRef;
    @ViewChild('deleteUploadScriptModal') deleteUploadScriptModalRef:ElementRef;
    @ViewChild('searchForm') searchForm;



    constructor(
        private element: ElementRef,
        private renderer: Renderer,
        private router: Router,
        private modalService: NgbModal,
        private UploadscriptService: UploadscriptService,
        private sharedService: SharedService

    ) {
        /* this.navigationSubscription = this.router.events.subscribe((e: any) => {
            // If it is a NavigationEnd event re-initalise the component
            if (e instanceof NavigationEnd) {
                console.log("Constructor : " + e);
                this.pageAlreadyCalled = false;
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
    loadInitialData() {

        this.tableDataHeight = (($("#contentWrapper").height()) - ($("#container").height() + $(".mainHead").height() + $(".nav").height() + 50));
        this.uploadBlock = false;
        //  this.createBlock=false;
        this.searchBlock = true;
        this.searchStatus = 'load';
        //   this.showLoader = true;
        this.setMenuHighlight("search");
        //For Pagination
        this.currentPage = 1;
        this.totalPages = 1;
        this.TableRowLength = 10;
        this.pageSize = 10;
        this.selectedVersion = "";
        this.lsmNameDetails = [];
        // this.utilities = [];
        this.terminals = [];
        this.editableFormArray = [];

        let paginationDetails = {
            "count": this.TableRowLength,
            "page": this.currentPage
        };

        this.paginationDetails = paginationDetails;
        let tz = jstz.determine();
        this.timezone = tz.name();
        // Get all upload script Details on page start
        this.getUploadScript();
        this.resetSearchForm();

        /* setTimeout(() => {
            // this.showLoader = false;
            validator.performValidation(event, this.validationData, "save_update");
        }, 1000); */
    }

    /*
    * on click of edit row enable corresponding
    * @param : current row index
    * @retun : boolean
    */

   ngOnDestroy() {
    this.programChangeSubscription.unsubscribe();
    // avoid memory leaks here by cleaning up after ourselves. If we  
    // don't then we will continue to run our ngOnInit()   
    // method on every navigationEnd event.
    if (this.navigationSubscription) {
        this.navigationSubscription.unsubscribe();
         }
    }
    resetSearchForm() {
        setTimeout(() => {
            this.searchForm.nativeElement.reset();
        }, 0);
    }
   uploadTabBind() {
    this.showNoDataFound = false;
    this.tableShowHide = false;
    this.searchBlock = false;
   // this.createBlock=false;
    this.uploadBlock = true;
    this.searchStatus = 'load';
    this.setMenuHighlight("upload");
    this.selectedVersion = "";
    this.selectedLsmName = "";
    this.selectedUtil = "";
    this.selectedScript = "";
    this.selectedTerminal= "";
    this.lsmNameDetails=[];
    this.terminals=[];
    setTimeout(() => {
        validator.performValidation(event, this.validationData, "upload");
    }, 10);
}
   /* createTabBind() {
    this.showNoDataFound = false;
    this.tableShowHide = false;
    this.searchBlock = false;
    this.uploadBlock = false;
    this.createBlock=true;
    this.searchStatus = 'load';
    this.setMenuHighlight("create");
    this.validationData.rules.searchBy.required = false;
    this.validationData.rules.paramsSearch.required = false;
    setTimeout(() => {
        validator.performValidation(event, this.validationData, "create");
    }, 10);
} */
   searchTabBind() {
    let searchCrtra = { "fileName": "", "State": "", "UploadedBy": "", "fromDate": "", "toDate": "" };
    this.searchCriteria = searchCrtra;

    this.setMenuHighlight("search");
    this.searchBlock = true;
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
    }, 10);
}


clearSearchFrom() {
    this.searchForm.nativeElement.reset();  
}

searchFileRules(event) {

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
                this.uploadBlock = false;
                this.showNoDataFound = false;

                let currentForm = event.target.parentNode.parentNode.parentNode,
                    searchCrtra = {
                        "fileName": currentForm.querySelector("#fileName").value,
                        "searchState": currentForm.querySelector("#searchState").value,
                        "UploadedBy": currentForm.querySelector("#UploadedBy").value,
                        "fromDate": currentForm.querySelector("#fromDate").value,
                        "toDate": currentForm.querySelector("#toDate").value
                        
                    };

                if (searchCrtra.fileName || searchCrtra.searchState || searchCrtra.UploadedBy || searchCrtra.fromDate || searchCrtra.toDate) {
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
                //this.getUploadScript();
                this.searchUploadScript();
            }
        }
    }, 0);
}



    checkFormEnable(index) {
        let indexValue = this.editableFormArray.indexOf(index);
        return indexValue >= 0 ? true : false;
    }


    /*
     * Used to display the table data on load/by default
     * @param : 
     * @retun : null
     */
    onChangeLoad(value){
        this.setMenuHighlight('search');
        this.searchBlock = true;
        this.uploadBlock = false;
        let migrationType = value;
        if(value == 'migration'){
            this.subType ="PRECHECK";
        }else if (value =='postmigration'){
            this.subType ="AUDIT";
        }else if (value =='preaudit'){
            //migrationType = "premigration"
            this.subType ="PREAUDIT";
        }else if(value == "nestatus"){
            this.subType ="NESTATUS";
        }else{
            this.subType ="";
        }
        this.migrationType = migrationType;
        if(this.uploadBlock){
            // this.bluePrintFormRef.resetForm();
            //this.selectedNWType ="";
            this.selectedVersion ="";
           // this.lsmSelectedVersion="";
            this.selectedLsmName ="";
            this.selectedScript = "";  
            this.selectedUtil="";
            this.lsmNameDetails=[];
            this.terminals=[];
            this.selectedTerminal = "";    
        }else if(this.searchBlock){
            //clear the search form
            this.resetSearchForm();
        }
        this.getUploadScript();
    }

    onChangeType(value){
        this.setMenuHighlight('search');
        this.searchBlock = true;
        this.uploadBlock = false;       
        this.subType = value;
        if(this.uploadBlock){
        //    this.bluePrintFormRef.resetForm();
            //this.selectedNWType ="";
            this.selectedVersion ="";
           //this.lsmSelectedVersion="";
            this.selectedLsmName ="";
            this.selectedScript = "";
            this.selectedUtil="";
            this.lsmNameDetails=[];
            this.terminals=[];
            this.selectedTerminal = "";
        }
        this.getUploadScript();
    }

    getUploadScript() {

        this.showLoader = true;
        $("#dataWrapper").find(".scrollBody").scrollLeft(0);
        this.UploadscriptService.getUploadScript( this.searchStatus, this.searchCriteria, this.paginationDetails,this.sharedService.createServiceToken(),this.migrationType, this.subType).subscribe(
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
                                this.argumentDetailsList=this.tableData.argumentDetails;
                                this.totalPages = this.tableData.pageCount;
                                //this.nwTypeDetails = Object.keys(this.tableData.nwTypeInfo);
                                this.lsmVersionDetails = Object.keys(this.tableData.lsmInfo);
                                if(this.tableData.ConnectionScriptType != null) {
                                    this.scriptType = this.tableData.ConnectionScriptType.scripts;
                                    // this.terminals = Object.keys(this.tableData.ConnectionScriptType.connLocation); 
                                   this.utilities = Object.keys(this.tableData.ConnectionScriptType.connLocation); 
                                }                             
                    

                                let pageCount = [];
                                for (var i = 1; i <= this.tableData.pageCount; i++) {
                                    pageCount.push(i);
                                }
                                this.pageRenge = pageCount;
                                this.pager = this.getPager(this.totalPages * this.pageSize, this.currentPage, this.pageRenge);

                                if(this.searchBlock){
                                // To display table data
                                if (this.tableData.uploadScriptTableDetails.length != 0) {

                                    this.showNoDataFound = false;
                                    this.tableShowHide = true;
                                    

                                    setTimeout(() => {
                                        let tableWidth = document.getElementById('melConfigDetails').scrollWidth;
                                        $(".scrollBody table").css("min-width", (tableWidth) + "px");
                                        $(".scrollHead table").css("width", tableWidth + "px");


                                        $(".scrollBody").on("scroll", function (event) {
                                            $(".formEditRow #formWrapper").css("margin-left", (event.target.scrollLeft - 5) + "px");
                                            $(".form-control-fixed").css("right", (event.target.scrollLeft * -1) + "px");
                                            $(".scrollHead table").css("margin-left", (event.target.scrollLeft * -1) + "px");
                                        });

                                        if (this.expandSerchRow == false) {
                                            setTimeout(() => {
                                                $(".scrollBody").css("max-height", (this.tableDataHeight - $("#tabContentWrapper").height()) + "px");
                                            }, 0);
                                        } else {
                                            setTimeout(() => {
                                                $(".scrollBody").css("max-height", (this.tableDataHeight - $("#tabContentWrapper").height()) + "px");
                                            }, 0);
                                        }

                                    }, 0);
                                } else {
                                    this.tableShowHide = false;
                                    this.showNoDataFound = true;
                                }
                            }
                            else {
                                this.tableShowHide = false;
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
                    // this.tableData = JSON.parse('{"sessionId":"529ed523","serviceToken":"91652","status":"SUCCESS","pageCount":3,"nwType":["type1","type2"],"lsmDetails":{"lsmName1":["v123","v321"],"lsmName2":["v456","v654"]},"uploadScriptTableDetails":[]}');
                    //Data
                    //this.tableData = JSON.parse('{"sessionId":"bc444948","serviceToken":"53538","status":"SUCCESS","pageCount":1,"uploadScriptTableDetails":[{"id":null,"lsmVersion":"1.1","fileName":"test.sh","filePath":null,"uploadedBy":"john","remarks":"dfdf","useCount":0,"nwType":null,"lsmName":"lsm2","customerId":0,"creationDate":"2019-02-06 15:44:09.0","program":null,"migrationType":null,"state":"UPLOADED","scriptType":"XML","prompt":"PROMPT1","connectionTerminalPwd":"testUPdated","connectionLocationPwd":"testLocPassword","connectionTerminalUserName":"termUserName","connectionLocationUserName":"connLocUsernanme","connectionLocation":"SM","connectionTerminal":"curl"}],"lsmInfo":{"1.2.3":{"CIQ_Bala":[{"scriptName":"test.sh","scriptFileId":"14"}],"Script_bala":[]}},"ConnectionScriptType":{"scripts":["XML","ShellScript","BatchFile"],"connLocation":{"SM":{"terminals":[{"terminalName":"curl","termUsername":"test","termPassword":"test"},{"terminalName":"bash","termUsername":"test","termPassword":"test"}],"username":"test","password":"test"},"NE":{"terminals":[{"terminalName":"Confd-cli","termUsername":"test","termPassword":"test"},{"terminalName":"bash","termUsername":"test","termPassword":"test"}],"username":"test","password":"test"}}},"promptList":["PROMPT1","PROMPT2","PROMPT3"]}');
                    //this.tableData = JSON.parse('{"sessionId":"bbbda74a","serviceToken":"54033","status":"SUCCESS","pageCount":2,"uploadScriptTableDetails":[{"id":31,"lsmVersion":"7.0.2","fileName":"pre_check.sh","filePath":"/Customer/1/migration/PRECHECK/10/40/","uploadedBy":"superadmin","remarks":"","useCount":1,"nwType":null,"lsmName":"MSMO5","customerId":0,"creationDate":"2019-04-05 12:07:44","program":"VZN-4G-CDU30","migrationType":"migration","state":"Uploaded","scriptType":"ShellScript","prompt":"PROMPT1","connectionTerminalPwd":"lW4xz0ihIh862ej2q+Okzw==","connectionLocationPwd":"lW4xz0ihIh862ej2q+Okzw==","connectionTerminalUserName":"test","connectionLocationUserName":"test","connectionLocation":"SM","connectionTerminal":{"terminalName":"cmdx","termPassword":"user","termUsername":"root123","prompt":"MSMAcmdx"},"neList":null,"customerDetailsEntity":null,"neVersion":null,"subType":"PRECHECK"}],"lsmInfo":{"7.0.2":{"WSBO TINY - MSMA0":[],"MSM03":[],"Audit NE":[],"Migration NE":[],"MSME_007":[],"MSMO5":[{"scriptName":"pre_check.sh","scriptFileId":"31"}],"9th April test":[]}},"ConnectionScriptType":{"scripts":["ShellScript","BatchFile"],"connLocation":{"SM":{"terminals":[{"terminalName":"cmd_sys","termUsername":"root123","termPassword":"user","prompt":"MSMACmd"},{"terminalName":"bash","termUsername":"root123","termPassword":"user","prompt":"MSMAbash"},{"terminalName":"cmdx","termPassword":"user","termUsername":"root123","prompt":"MSMAcmdx"}],"username":"root123","password":"user"},"NE":{"terminals":[{"terminalName":"cli","termUsername":"root123","termPassword":"user","prompt":"MSMACli"},{"terminalName":"bash","termUsername":"root123","termPassword":"user","prompt":"MSMABash"}],"username":"root123","password":"user"}}}}');
                    // this.tableData = JSON.parse('{"sessionId":"8ade1590","serviceToken":"99062","status":"FAIL","reason":"Failed to load data","pageCount":0,"lsmInfo":{"7.0.2":{"24th April NE":[],"Script Server":[],"CIQ_SERVER":[],"WSBO TINY - MSMA0":[],"MSM03":[],"Audit NE":[],"MSME_007":[],"MSMO5":[]}},"uploadScriptTableDetails":[],"ConnectionScriptType":{"scripts":["ShellScript","BatchFile"],"connLocation":{"NE":{"password":"root123","terminals":[{"terminalName":"cli","termPassword":"root123","termUsername":"user","prompt":"root@BLTSP02518"},{"terminalName":"bash","termPassword":"root123","termUsername":"user","prompt":"root@BLTSP02518"}],"username":"user"},"SM":{"password":"root123","terminals":[{"terminalName":"cmd_sys","termPassword":"root123","termUsername":"user","prompt":"root@BLTSP02518"},{"terminalName":"bash","termPassword":"root123","termUsername":"user","prompt":"root@BLTSP02518"},{"terminalName":"cmdx","termPassword":"root123","termUsername":"user","prompt":"root@BLTSP02518"}],"username":"user"}}},"argumentDetails":{"LSM_USERNAME":"Lsm User Name","LSM_PWD":"Lsm Password","CASCADE_ID":"Network Element Cascade ID","AD_ID":"Logged In User Name","MARKET":"Market Name","JUMP_SANE":"Network Configurtaion Jump Sane IP","LSM_IP":"Network Configurtaion LSM IP","NE_ID":"Network Element ID","JUMP_BOX_IP":"Network Configurtaion Jump Box IP","VLSM_IP":"Network Configurtaion VLSM IP","CDU_IP":"Network Element IP","MCMA_IP":"Network Configurtaion  MCMA IP","DIR":"Ran ATP File Generation Path","RS_IP":"Network Configurtaion RS IP","NE_NAME":"Network Element  Name","MSMA_IP":"Network Configurtaion  MSMA IP","VLSM_RS_IP":"Network Configurtaion VLSM RS IP","IS_LAB":"LAB/LIVE Environment","UNQ_ID":"Unique ID"}}');
                    this.tableData = JSON.parse('{"pageCount":1,"lsmInfo":{"8.5.3":{"Demo NE":[{"scriptName":"june24_ls.sh","scriptFileId":"103"},{"scriptName":"Aug6.sh","scriptFileId":"158"}],"Akron LSMU MS8":[{"scriptName":"june24_ls.sh","scriptFileId":"103"},{"scriptName":"Aug6.sh","scriptFileId":"158"}]}},"uploadScriptTableDetails":[{"id":158,"lsmVersion":null,"fileName":"Aug6.sh","filePath":"/Customer/3/PostMigration/RanATP/","uploadedBy":"superadmin","remarks":"","useCount":0,"nwType":null,"lsmName":null,"customerId":0,"creationDate":"2019-08-09 12:24:32","program":"SPT-4G-MIMO","migrationType":"postmigration","state":"Uploaded","neList":null,"customerDetailsEntity":null,"neVersion":null,"subType":"RANATP","scriptType":"ShellScript","connectionLocation":"NE","connectionLocationUserName":"u","connectionLocationPwd":"ksj1yVOlHHQSrsXu4K8oRw==","connectionTerminal":null,"connectionTerminalUserName":null,"connectionTerminalPwd":null,"connectionTerminalDetails":{"connectionTerminal":{"terminalName":"bash","termUsername":"u","termPassword":"CJrcbP7a8L8WWQQWbeecJvAutfPZmadfvKbQRWwaWaAttfWEkS+m0zBuSZhEGtoHMkoNPHTYw5kiBinhMOXEkeZa9Qxkrgv97ls+2bp4wyxbF5O3LD9eZq1InYnqcBDM8tIbS+pynvLsGGF0zWW/ObJvQOm1RAluA0tD05D9WBybkJrnuScdLpcDwtknTNTnZN0GbUQUOTF8z5IEHafmMg==","prompt":"$"}},"prompt":null,"arguments":"a,b","sudoPassword":null},{"id":103,"lsmVersion":null,"fileName":"june24_ls.sh","filePath":"/Customer/3/PostMigration/RanATP/","uploadedBy":"superadmin","remarks":"","useCount":4,"nwType":null,"lsmName":null,"customerId":0,"creationDate":"2019-06-25 10:25:15","program":"SPT-4G-MIMO","migrationType":"PostMigration","state":"Uploaded","neList":null,"customerDetailsEntity":null,"neVersion":null,"subType":"RanATP","scriptType":"ShellScript","connectionLocation":"NE","connectionLocationUserName":"test","connectionLocationPwd":"lW4xz0ihIh862ej2q+Okzw==","connectionTerminal":null,"connectionTerminalUserName":null,"connectionTerminalPwd":null,"connectionTerminalDetails":{"connectionTerminal":{"terminalName":"bash","termUsername":"test","termPassword":"lW4xz0ihIh862ej2q+Okzw==","prompt":"$"}},"prompt":null,"arguments":"","sudoPassword":null}],"ConnectionScriptType":{"scripts":["ShellScript","BatchFile"],"connLocation":{"NE":{"password":"root123","sudoPassword":"root123","terminals":[{"terminalName":"cli","termPassword":"root123","termUsername":"u","prompt":"$"},{"terminalName":"bash","termPassword":"root123","termUsername":"u","prompt":"$"}],"username":"u"},"SM":{"password":"root123","terminals":[{"terminalName":"cmd_sys","termPassword":"root123","termUsername":"u","prompt":"$"},{"terminalName":"bash","termPassword":"root123","termUsername":"u","prompt":"$"}],"username":"u"}}},"sessionId":"3854e6a8","serviceToken":"65757","argumentDetails":{"CASCADE_ID":"Network Element Cascade ID","AD_ID":"Logged In User Name","LSM_IP":"Network Configuration LSM IP","NE_ID":"Network Element ID","VLSM_IP":"Network Configuration VLSM IP","CDU_IP":"Network Element IP","RS_IP":"Network Configuration RS IP","OPS_ATP_INPUT_FILE":"Ran ATP Input Log File Name","NE_NAME":"Network Element  Name","CREDENTIALS":"Network Config Credentials","IS_LAB":"LAB/LIVE Environment","SANE_OPTIONS":"Network Sane Options","HOP_STRING":"Network Hopping String","JUMP_SANE_IP":"Network Configuration Jump Sane IP","PUT_SERVER_IP":"Network Configuration PUT SERVER IP","UNQ_ID":"Unique ID","LSM_USERNAME":"Lsm User Name","LSM_PWD":"Lsm Password","MARKET":"Market Name","JUMP_BOX_IP":"Network Configuration Jump Box IP","MCMA_IP":"Network Configuration  MCMA IP","DIR":"Ran ATP File Generation Path","MSMA_IP":"Network Configuration  MSMA IP","VLSM_RS_IP":"Network Configuration VLSM RS IP","BANDS":"BANDS","EXCEL_FILE":"Ran ATP Output Excel File Name"},"status":"SUCCESS"}');
                    if (this.tableData.status == "SUCCESS") {
                        this.argumentDetailsList = this.tableData.argumentDetails;
                        //this.argumentDetailsList = [];

                        console.log(this.argumentDetailsList);
                        this.totalPages = this.tableData.pageCount;
                        this.lsmVersionDetails = Object.keys(this.tableData.lsmInfo);
                        if (this.tableData.ConnectionScriptType != null) {
                            this.scriptType = this.tableData.ConnectionScriptType.scripts;
                            // this.terminals = Object.keys(this.tableData.ConnectionScriptType.connLocation); 
                            this.utilities = Object.keys(this.tableData.ConnectionScriptType.connLocation);
                        }


                        let pageCount = [];
                        for (var i = 1; i <= this.tableData.pageCount; i++) {
                            pageCount.push(i);
                        }
                        this.pageRenge = pageCount;
                        this.pager = this.getPager(this.totalPages * this.pageSize, this.currentPage, this.pageRenge);

                        if (this.searchBlock) {
                            // To display table data
                            if (this.tableData.uploadScriptTableDetails.length != 0) {
                                this.showNoDataFound = false;
                                this.tableShowHide = true;

                                setTimeout(() => {
                                    let tableWidth = document.getElementById('melConfigDetails').scrollWidth;
                                    $(".scrollBody table").css("min-width", (tableWidth) + "px");
                                    $(".scrollHead table").css("width", tableWidth + "px");


                                    $(".scrollBody").on("scroll", function (event) {
                                        $(".formEditRow #formWrapper").css("margin-left", (event.target.scrollLeft - 5) + "px");
                                        $(".form-control-fixed").css("right", (event.target.scrollLeft * -1) + "px");
                                        $(".scrollHead table").css("margin-left", (event.target.scrollLeft * -1) + "px");
                                    });

                                    if (this.expandSerchRow == false) {
                                        setTimeout(() => {
                                            $(".scrollBody").css("max-height", (this.tableDataHeight - $("#tabContentWrapper").height()) + "px");
                                        }, 0);
                                    } else {
                                        setTimeout(() => {
                                            $(".scrollBody").css("max-height", (this.tableDataHeight - $("#tabContentWrapper").height()) + "px");
                                        }, 0);
                                    }

                                }, 0);
                            } else {
                                this.tableShowHide = false;
                                this.showNoDataFound = true;
                            }
                        }
                        else {
                            this.tableShowHide = false;
                        }
                    }
                    else {
                        this.showLoader = false;
                        this.displayModel(this.tableData.reason, "failureIcon");
                    }
                }, 1000); */
                //Please Comment while checkIn


            });
    }

    searchUploadScript() {

        this.showLoader = true;
        this.UploadscriptService.searchUploadScript( this.searchStatus, this.searchCriteria, this.paginationDetails,this.sharedService.createServiceToken(), this.migrationType, this.subType).subscribe(
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

                            setTimeout(() => {
                                this.showLoader = false;
                                this.tableData = jsonStatue;

                                this.totalPages = this.tableData.pageCount;
                                //this.nwTypeDetails = Object.keys(this.tableData.nwTypeInfo);
                              //  this.lsmVersionDetails = Object.keys(this.tableData.lsmInfo);
                    

                                let pageCount = [];
                                for (var i = 1; i <= this.tableData.pageCount; i++) {
                                    pageCount.push(i);
                                }
                                this.pageRenge = pageCount;
                                this.pager = this.getPager(this.totalPages * this.pageSize, this.currentPage, this.pageRenge);

                                if(this.searchBlock){
                                // To display table data
                                if (this.tableData.uploadScriptTableDetails.length != 0) {

                                    this.showNoDataFound = false;
                                    this.tableShowHide = true;

                                    setTimeout(() => {
                                        let tableWidth = document.getElementById('melConfigDetails').scrollWidth;
                                        $(".scrollBody table").css("min-width", (tableWidth) + "px");
                                        $(".scrollHead table").css("width", tableWidth + "px");


                                        $(".scrollBody").on("scroll", function (event) {
                                            $(".formEditRow #formWrapper").css("margin-left", (event.target.scrollLeft - 5) + "px");
                                            $(".form-control-fixed").css("right", (event.target.scrollLeft * -1) + "px");
                                            $(".scrollHead table").css("margin-left", (event.target.scrollLeft * -1) + "px");
                                        });

                                        if (this.expandSerchRow == false) {
                                            setTimeout(() => {
                                                $(".scrollBody").css("max-height", (this.tableDataHeight - $("#tabContentWrapper").height()) + "px");
                                            }, 0);
                                        } else {
                                            setTimeout(() => {
                                                $(".scrollBody").css("max-height", (this.tableDataHeight - $("#tabContentWrapper").height()) + "px");
                                            }, 0);
                                        }

                                    }, 0);
                                } else {
                                    this.tableShowHide = false;
                                    this.showNoDataFound = true;
                                }
                            }
                            else {
                                this.tableShowHide = false;
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
                    // this.tableData = JSON.parse('{"sessionId":"529ed523","serviceToken":"91652","status":"SUCCESS","pageCount":3,"nwType":["type1","type2"],"lsmDetails":{"lsmName1":["v123","v321"],"lsmName2":["v456","v654"]},"uploadScriptTableDetails":[]}');
                    //Data
                    // this.tableData = JSON.parse('{"pageCount":1,"sessionId":"bc444948","serviceToken":"53538","uploadScriptTableDetails":[{"id":null,"lsmVersion":null,"fileName":"test.sh","filePath":null,"uploadedBy":"john","remarks":"dfdf","useCount":0,"nwType":null,"lsmName":null,"customerId":0,"creationDate":"2019-02-06 15:44:09.0","program":null,"migrationType":null,"state":"UPLOADED"}],"status":"SUCCESS"}');
                    this.tableData = JSON.parse('{"pageCount":1,"sessionId":"bc444948","serviceToken":"53538","uploadScriptTableDetails":[{"id":null,"lsmVersion":"1.1","fileName":"test.sh","filePath":null,"uploadedBy":"john","remarks":"dfdf","useCount":0,"nwType":null,"lsmName":"lsm2","customerId":0,"creationDate":"2019-02-06 15:44:09.0","program":null,"migrationType":null,"state":"UPLOADED"}],"status":"SUCCESS","lsmInfo":{"1.2.3":{"CIQ_Bala":[{"scriptName":"test.sh","scriptFileId":"14"}],"Script_bala":[]}}}');
                    //this.tableData = JSON.parse('{"sessionId":"c935ae4d","serviceToken":68375,"pagination":{"count":10,"page":1},"customerName":"","customerId":1,"searchDetails":{"programDetailsEntity":{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"3G","createdBy":"superadmin","caretedDate":"2019-02-15T19:34:37.000+0000","status":"Active","remarks":"","networkColor":"#4cca09"},"programName":"VZN-4G-LEGACY","programDescription":null,"status":"InActive","creationDate":"2018-12-28T06:39:17.000+0000","createdBy":"admin"},"neTypeEntity":{"id":1,"neType":"CIQ SERVER"},"neName":"","neVersion":"","loginTypeEntity":{"id":2,"loginType":"FTP"},"status":"Active"},"searchStatus":"search"}');

                    
                    console.log(this.tableData);
                    this.totalPages = this.tableData.pageCount;
                   // this.nwTypeDetails = Object.keys(this.tableData.nwTypeInfo);
                  // this.lsmVersionDetails = Object.keys(this.tableData.lsmInfo);
                    
                   
                    
                    let pageCount = [];
                    for (var i = 1; i <= this.tableData.pageCount; i++) {
                        pageCount.push(i);
                    }
                    this.pageRenge = pageCount;
                    this.pager = this.getPager(this.totalPages * this.pageSize, this.currentPage, this.pageRenge);

                    if(this.searchBlock){
                    // To display table data
                    if (this.tableData.uploadScriptTableDetails.length != 0) {
                        this.showNoDataFound = false;
                        this.tableShowHide = true;
                        setTimeout(() => {
                            let tableWidth = document.getElementById('melConfigDetails').scrollWidth;
                            $(".scrollBody table").css("min-width", (tableWidth) + "px");
                            $(".scrollHead table").css("width", tableWidth + "px");


                            $(".scrollBody").on("scroll", function (event) {
                                $(".formEditRow #formWrapper").css("margin-left", (event.target.scrollLeft - 5) + "px");
                                $(".form-control-fixed").css("right", (event.target.scrollLeft * -1) + "px");
                                $(".scrollHead table").css("margin-left", (event.target.scrollLeft * -1) + "px");
                            });

                            setTimeout(() => {
                                $(".scrollBody").css("max-height", (this.tableDataHeight - $("#tabContentWrapper").height()) + "px");
                            }, 0);
                        }, 0);
                    } else {
                        this.tableShowHide =false;
                        this.showNoDataFound = true;
                    }
                }
                else {
                    this.tableShowHide = false;
                }
                }, 1000); */
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

    closeModel() {
        this.successModalBlock.close();
        // this.ngOnInit();
        this.searchTabBind();
        //this.getUploadScript();
    }

    closeDeleteModal(){
        this.deleteUploadScriptBlock.close();
        this.searchTabBind();
    }
    /* validates current submitted form is valid and free from errors
     * @param : pass the event
     * @retun : boolean
     */

    isValidForm(event) {
        return ($(event.target).parents("form").find(".error-border").length == 0) ? true : false;
    }


    /* getLsmVersion(nwSelectedType) {
        this.lsmVersionDetails = [];
        this.selectedVersion = "";
        this.lsmNameDetails = [];
        if (nwSelectedType) {
            this.lsmVersionDetails = Object.keys(this.tableData.nwTypeInfo[nwSelectedType]);
        }
    } */

    /* getScriptType(selectedTerminal) {
        //this.selectedTerminal = "";
        this.utilities = [];
        this.utilities = Object.values(this.tableData.ConnectionScriptType.connLocation[selectedTerminal].utilities);
    } */

    getScriptType(selectedUtil) {
        //this.selectedUtil = "";
        if(selectedUtil)
        {
            this.terminals = [];
            this.terminals = Object.values(this.tableData.ConnectionScriptType.connLocation[selectedUtil].terminals);
        }

    }


    getLsmName(lsmSelectedVersion) {
        this.selectedLsmName = "";
        this.lsmNameDetails = [];
        this.lsmNameDetails = Object.keys(this.tableData.lsmInfo[lsmSelectedVersion]);
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

    confirmUploadedDetails(event) {

        const formdata = new FormData();
        let files: FileList = this.filePostRef.nativeElement.files,
            filenames = [];
        formdata.append("UPLOAD", files[0]);
        filenames.push(files[0].name);
        let allowDuplicate = true;
        setTimeout(() => {
            this.showLoader = true;
            let currentForm = event.target.parentNode.parentNode;

            // this.nwtypeVal = currentForm.querySelector("#nwType").value;
            this.lsmNameVal = currentForm.querySelector("#lsmName").value;
            this.lsmVersionVal = currentForm.querySelector("#lsmVersion").value;
            this.remarksVal = currentForm.querySelector("#remarks").value;
            this.arguments = currentForm.querySelector("#arguments").value;

            /* this.selectedTerminal= currentForm.querySelector("#terminals").value;
            if(this.selectedTerminal) {
                this.termUserName = this.tableData.ConnectionScriptType.connLocation[this.selectedTerminal].username;
                this.termPswd = this.tableData.ConnectionScriptType.connLocation[this.selectedTerminal].password;  
            } */  
            this.selUtility= currentForm.querySelector("#utility").value;
            let sudoPassword = "";
            if(this.selUtility) {
                this.utilUserName = this.tableData.ConnectionScriptType.connLocation[this.selUtility].username;
                this.utilPswd = this.tableData.ConnectionScriptType.connLocation[this.selUtility].password;
                sudoPassword = this.tableData.ConnectionScriptType.connLocation[this.selUtility].sudoPassword;  
            }        
            this.prompt=this.selectedTerminal.prompt;

            this.UploadscriptService.uploadDetails(formdata, filenames.toString(), allowDuplicate, this.sharedService.createServiceToken(), this.lsmNameVal, this.lsmVersionVal, this.remarksVal,this.migrationType, this.subType, this.selectedScript, this.selUtility,this.selectedTerminal,this.utilUserName,this.utilPswd,this.prompt, this.arguments, sudoPassword)
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
                                    this.filePostRef.nativeElement.value = "";

                                    this.message = "Details Uploaded successfully!";
                                    this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                                    this.resetValues(event)
                                } else {
                                    this.showLoader = false;
                                    this.displayModel(jsonStatue.reason, "failureIcon");
                                    this.filePostRef.nativeElement.value = "";
                                }
                            }
                        }
                    },
                    error => {
                        //Please Comment while checkIn

                        /* setTimeout(() => {
 
                             this.showLoader = false;
 
                             let jsonStatue = {
                                 "sessionId": "e9004f23",
                                 "reason": "File Uploaded successfully !",
                                 "status": "SUCCESS",
                                 //"reason": "File already exist, Do you want reupload!",
                                 "serviceToken": "64438"
                             };
 
                             if (jsonStatue.status == "SUCCESS") {
                                 this.showLoader = false;
                                 // UploadFileStatus *******************************************
                                 this.filePostRef.nativeElement.value = "";                                
                                 this.message = "Details Uploaded successfully!";
                                 this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                                 this.resetValues(event)
 
                             } else {
 
                                 this.showLoader = false;
                                 this.displayModel(jsonStatue.reason, "failureIcon");
                                 this.filePostRef.nativeElement.value = "";
                             }
 
                         }, 100); */

                        //Please Comment while checkIn   
                    });

        }, 0);

    }

    resetValues(event) {
        let currentForm = event.target.parentNode.parentNode;

        currentForm.querySelector("#filePost").value = "";
        currentForm.querySelector("#scriptType").value = "";
        currentForm.querySelector("#utility").value = "";
        currentForm.querySelector("#terminals").value = "";
        currentForm.querySelector("#lsmVersion").value = "";
        currentForm.querySelector("#lsmName").value = "";
        currentForm.querySelector("#remarks").value = "";



    }
    /*
     * Used to validate all the entered details 
     * @param : event,confirmModal
     * @retun : null
   */

    uploadAllDetails(event, confirmModal) {

        const formdata = new FormData();
        let files: FileList = this.filePostRef.nativeElement.files,
            filenames = [];
        for (var i = 0; i < files.length; i++) {
            formdata.append( "UPLOAD", files[i]);
            //formdata.append(ciqFiles[i].name, ciqFiles[i]);
            filenames.push(files[i].name);
        }
       // formdata.append("UPLOAD", files[0]);
        //filenames.push(files[0].name);

        let allowDuplicate = false;
        validator.performValidation(event, this.validationData, "save_update");
        setTimeout(() => {
            if (this.isValidForm(event)) {
                this.showLoader = true;
                let currentForm = event.target.parentNode.parentNode;

                // this.nwtypeVal = currentForm.querySelector("#nwType").value;
                this.lsmNameVal = currentForm.querySelector("#lsmName").value;
                this.lsmVersionVal = currentForm.querySelector("#lsmVersion").value;
                this.remarksVal = currentForm.querySelector("#remarks").value;
                this.arguments = currentForm.querySelector("#arguments").value;
                /* this.selectedTerminal= currentForm.querySelector("#utility").value;
                if(this.selectedTerminal) {
                    this.termUserName = this.tableData.ConnectionScriptType.connLocation[this.selectedTerminal].username;
                    this.termPswd = this.tableData.ConnectionScriptType.connLocation[this.selectedTerminal].password;                           
                }   */      
                this.selUtility= currentForm.querySelector("#utility").value;
                let sudoPassword = "";
                if(this.selUtility) {
                    this.utilUserName = this.tableData.ConnectionScriptType.connLocation[this.selUtility].username;
                    this.utilPswd = this.tableData.ConnectionScriptType.connLocation[this.selUtility].password;                           
                    sudoPassword = this.tableData.ConnectionScriptType.connLocation[this.selUtility].sudoPassword;
                }         
                this.prompt=this.selectedTerminal.prompt;
                
                this.UploadscriptService.uploadDetails(formdata, filenames.toString(), allowDuplicate, this.sharedService.createServiceToken(), this.lsmNameVal, this.lsmVersionVal, this.remarksVal,this.migrationType, this.subType, this.selectedScript, this.selUtility,this.selectedTerminal,this.utilUserName,this.utilPswd,this.prompt, this.arguments, sudoPassword)
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

                                    if (jsonStatue.status == "CONFIRM") {
                                        this.showLoader = false;
                                        this.modalService.open(confirmModal, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal' }).result.then((result) => {
                                            this.closeResult = `Closed with: ${result}`;
                                            this.resetValues(event)
                                        }, (reason) => {
                                            // UploadFileStatus *******************************************
                                            //this.filePostRef.nativeElement.value = "";
                                            //*********************************************
                                            this.confirmUploadedDetails(event);
                                        });


                                    } else if (jsonStatue.status == "SUCCESS") {
                                        this.showLoader = false;
                                        this.filePostRef.nativeElement.value = "";
                                        this.message = "Details Uploaded successfully!";
                                        this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                                        this.resetValues(event)
                                    } else {
                                        this.showLoader = false;
                                        this.displayModel(jsonStatue.reason, "failureIcon");
                                        this.filePostRef.nativeElement.value = "";
                                    }
                                }
                            }
                        },
                        error => {
                            //Please Comment while checkIn

                            /* setTimeout(() => {

                                this.showLoader = false;

                                let jsonStatue = {
                                    "sessionId": "e9004f23",
                                    "reason": "File Uploaded successfully !",
                                    //"status": "SUCCESS",
                                    "status": "CONFIRM",
                                    "serviceToken": "64438"
                                };

                                if (jsonStatue.status == "CONFIRM") {
                                    this.modalService.open(confirmModal, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal' }).result.then((result) => {
                                        this.closeResult = `Closed with: ${result}`;
                                        this.resetValues(event)
                                    }, (reason) => {
                                        // UploadFileStatus *******************************************
                                        //this.filePostRef.nativeElement.value = "";
                                        //*********************************************
                                        this.confirmUploadedDetails(event);
                                    });
                                } else if (jsonStatue.status == "SUCCESS") {
                                    this.showLoader = false;
                                    this.filePostRef.nativeElement.value = "";                                   
                                    this.message = "Details Uploaded successfully!";
                                    this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                                    this.resetValues(event)
                                } else {
                                    this.showLoader = false;
                                    this.displayModel(jsonStatue.reason, "failureIcon");
                                    this.filePostRef.nativeElement.value = "";
                                }

                            }, 100); */
                            //Please Comment while checkIn   
                        });
            }
        }, 0);

    }

    /*
     * Used to update uploaded data details
     * @param : event
     * @retun : null
     */
    updateUploadDetails(event, key) {
        setTimeout(() => {
            $("#dataWrapper").find(".scrollBody").scrollLeft(0);
        }, 0);

        validator.performValidation(event, this.validationData, "save_update");
        setTimeout(() => {
            if (this.isValidForm(event)) {
                this.showLoader = true;
                let currentForm = event.target.parentNode.parentNode,
                    uploadScriptFormData = {
                        "id": this.uploadRowId,
                        // "nwType": currentForm.querySelector("#nwType").value,
                        "lsmName": key.lsmName,
                        "lsmVersion":key.lsmVersion,
                        "remarks": currentForm.querySelector("#remarks").value,
                        "arguments" : currentForm.querySelector("#arguments").value,
                        "uploadedBy": key.uploadedBy,
                        "fileName": key.fileName,
                        "migrationType":this.migrationType == "preaudit" ? "premigration" : this.migrationType == "nestatus" ? "premigration" : this.migrationType,
                        "filePath": key.filePath,
                        "state":key.state,
                        "creationDate":key.creationDate,
                        "scriptType":currentForm.querySelector("#scriptType").value,
                        "connectionLocation":currentForm.querySelector("#connectionLocation").value,
                        "connectionLocationUserName":this.tableData.ConnectionScriptType.connLocation[currentForm.querySelector("#connectionLocation").value].username,
                        "connectionLocationPwd":this.tableData.ConnectionScriptType.connLocation[currentForm.querySelector("#connectionLocation").value].password,
                        
                        "connectionTerminal":this.selectedTerminal.terminalName,
                        "connectionTerminalUserName":this.selectedTerminal.termUsername,
                        "connectionTerminalPwd":this.selectedTerminal.termPassword,
                        "prompt":this.selectedTerminal.prompt,
                        "sudoPassword" : this.tableData.ConnectionScriptType.connLocation[currentForm.querySelector("#connectionLocation").value].sudoPassword
                        //"editable": key.editable
                    }; 
                    //console.log(this.selectedTerminal.prompt);

                this.UploadscriptService.updateUploadDetails(uploadScriptFormData, this.sharedService.createServiceToken(), this.migrationType, this.subType)
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

                                        //this.displayModel("File Rule updated successfully !", "successIcon");
                                        this.paginationDisabbled = false;
                                        // Hide all the form/Table/Nodatafound
                                        this.tableShowHide = false;
                                        this.showNoDataFound = false;

                                        this.message = "Details updated successfully!";
                                        this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });

                                        // Cancel current edit form
                                        this.cancelEditRow(uploadScriptFormData.id, '');


                                    } else {

                                        this.displayModel(jsonStatue.reason, "failureIcon");
                                        this.paginationDisabbled = true;

                                    }
                                }
                            }
                        },
                        error => {
                            //Please Comment while checkIn

                            /* let jsonStatue = { "sessionId": "72e938b8", "reason": null, "status": "SUCCESS", "serviceToken": "81307" };
                            if (jsonStatue.status == "SUCCESS") {

                                this.paginationDisabbled = false;
                                this.showLoader = true;
                                setTimeout(() => {
                                    this.showLoader = false;
                                    this.message = "Details updated successfully!";
                                    this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                                    // Cancel current edit form
                                    this.cancelEditRow(uploadScriptFormData.id, '');
                                }, 1000);
                            } else {
                                this.paginationDisabbled = true;
                                this.displayModel(jsonStatue.reason, "failureIcon");
                            } */
                            //Please Comment while checkIn

                        });
            }
        }, 0);
    }


    /*
     * on click of edit row create a blueprint and append next to the current row
     * @param : current row event , current row json object and row index
     * @retun : null
     */

    editRow(event, key, index) {
        //console.log(key);
        this.uploadRowId = key.id;
        let editState: any = event.target;

        $(".editRowDisabled").attr("class","editRow");
        $(".deleteRowDisabled").attr("class","deleteRow");
        if (editState.className != "editRowDisabled") { //enable click only if it is enabled

            editState.className = "editRowDisabled";
            $(editState).next().attr("class", "deleteRowDisabled") // Disable delete on edit row   
          /*   // If any edit form is opend then close the form and enable edit button
            if (this.currentEditRow != undefined) {

                this.currentEditRow.className = "editRow";
            }

            this.currentEditRow = event.target; */

            // To enable one edit form at a time in table
            if (this.editableFormArray.length >= 1) {
                this.editableFormArray = [];
                this.editableFormArray.push(index);
            } else {
                this.editableFormArray.push(index);
            }

            this.sharedService.userNavigation = false; //block user navigation

            /* if (key.nwType) {
                this.getLsmVersion(key.nwType);
            } */

            if (key.connectionLocation) {
                this.getScriptType(key.connectionLocation)
            }
            
            setTimeout(() => {
                this.selectedTerminal = key.connectionTerminalDetails.connectionTerminal;

                this.editRowInTable(event, key, index);
                //map validation for fields
                validator.performValidation(event, this.validationData, "edit");
            }, 100);
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

            // To set 
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

        currentEditedForm.lastElementChild.lastElementChild.children[1].className = "editRow";


        // Enable search button 
        //document.querySelector("#searchButton").classList.remove("buttonDisabled");

        this.paginationDisabbled = false;

    }

    viewRowScript(content, key) {
        this.scriptData = {
            "fileName": key.fileName,
            "filePath": key.filePath,
            "id": key.id
        }
        // this.modalService.open(content, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView' });
        this.viewFileName = key.fileName


        this.showLoader = true;
        this.UploadscriptService.viewScriptContent(this.scriptData, this.sharedService.createServiceToken(), this.migrationType, this.subType)
            .subscribe(
                data => {
                    let jsonStatue = data.json();
                    this.showLoader = false;

                    if (jsonStatue.status == "403") {
                        this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: "static", size: "lg", windowClass: "session-modal" });
                    } else {
                        if (
                            this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {
                            if (jsonStatue.status == "SUCCESS") {
                                this.scriptData.scriptFileContent = jsonStatue.scriptFileContent;
                                this.modalService.open(content, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView' });
                            } else {
                                this.displayModel(jsonStatue.reason, "failureIcon");
                            }
                        }
                    }
                },
                error => {

                    //Please Comment while checkIn
                    /* let jsonStatue: any = { "sessionId": "5cbb8b6e", "serviceToken": 84624, "scriptFileContent": " ls -lrt ", "status": "SUCCESS" };
                    setTimeout(() => {
                        this.showLoader = false;
                        if (jsonStatue.status == "403") {
                            this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: "static", size: "lg", windowClass: "session-modal" });
                        }
                        else {
                            if (jsonStatue.status == "SUCCESS") {
                                this.scriptData.scriptFileContent = jsonStatue.scriptFileContent;
                                this.modalService.open(content, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView' });
                            } else {
                                this.displayModel(jsonStatue.reason, "failureIcon");
                            }
                        }

                    }, 1000);  */
                    //Please Comment while checkIn
                }
            );
    }

    saveScript(c) {
        this.showLoader = true;
        this.UploadscriptService.saveScriptContent(this.scriptData, this.sharedService.createServiceToken(), this.migrationType, this.subType)
            .subscribe(
                data => {
                    let jsonStatue = data.json();
                    this.showLoader = false;
                    c("Close");

                    if (jsonStatue.status == "403") {
                        this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: "static", size: "lg", windowClass: "session-modal" });
                    } else {
                        if (
                            this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {
                            if (jsonStatue.status == "SUCCESS") {
                                this.message = "Script saved successfully!";
                                this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: "static", size: "lg", windowClass: "success-modal" });
                            } else {
                                this.displayModel(jsonStatue.reason, "failureIcon");
                            }
                        }
                    }
                },
                error => {

                    //Please Comment while checkIn
                    /* let jsonStatue: any = { "status": "SUCCESS", "sessionId": "7e088256", "serviceToken": 91252, "reason": "Use case script updated successfully" };
                    c("Close");
                    setTimeout(() => {
                        this.showLoader = false;
                        if (jsonStatue.status == "403") {
                            this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: "static", size: "lg", windowClass: "session-modal" });
                        }
                        else {
                            if (jsonStatue.status == "SUCCESS") {
                                this.message = "Script saved successfully!";
                                this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: "static", size: "lg", windowClass: "success-modal" });
                            } else {
                                this.displayModel(jsonStatue.reason, "failureIcon");
                            }
                        }

                    }, 1000); */
                    //Please Comment while checkIn
                }
            );
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

                this.UploadscriptService.deleteUploadData(rowId, this.sharedService.createServiceToken(),this.migrationType, this.subType)
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
                                        this.message = "Uploaded data deleted successfully!";
                                        this.deleteUploadScriptBlock = this.modalService.open(this.deleteUploadScriptModalRef,{keyboard:false,backdrop:'static',size:'lg',windowClass:'success-modal'});
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
                                    this.message = "Uploaded data deleted successfully!";
                                    this.deleteUploadScriptBlock = this.modalService.open(this.deleteUploadScriptModalRef,{keyboard:false,backdrop:'static',size:'lg',windowClass:'success-modal'});                                    
                                }
                            }, 1000); */
                            //Please Comment while checkIn
                        });
            });
        }
    }

    downLoadFile(fileName, filePath) {

        this.UploadscriptService.downloadFile(fileName,filePath,this.sharedService.createServiceToken())
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
    setMenuHighlight(selectedElement) {
        this.uploadTabRef.nativeElement.id = (selectedElement == "upload") ? "activeTab" : "inactiveTab";
      //  this.createTabRef.nativeElement.id = (selectedElement == "create") ? "activeTab" : "inactiveTab";
        this.searchTabRef.nativeElement.id = (selectedElement == "search") ? "activeTab" : "inactiveTab";
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
            this.getUploadScript();


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
            this.getUploadScript();
        }, 0);

    }


       
    showListOfUseCases(popover) {
        popover.open();
    }

    /*
     * On click sort header in table then sort the data ascending and decending
     * @param : columnName, event and current Index
     * @retun : null
     */

    changeSorting(predicate, event, index) {
        this.sharedService.dynamicSort(predicate, event, index, this.tableData.uploadScriptTableDetails);
    }


    compareFn(o1: any, o2: any) {
        //console.log(o1,o2);
        return o1 && o2 ? o1.terminalName === o2.terminalName : o1 === o2;
    }

}


