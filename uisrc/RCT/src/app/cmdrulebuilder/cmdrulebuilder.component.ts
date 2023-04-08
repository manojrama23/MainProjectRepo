import { ViewContainerRef, Input, ElementRef, Renderer, Component, OnInit, ViewChild, HostListener } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { NgbModal, ModalDismissReasons } from '@ng-bootstrap/ng-bootstrap';
import { PregrowService } from '../services/pregrow.service';
import { SharedService } from '../services/shared.service';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { filter, map, mergeMap } from 'rxjs/operators';
import { validator } from '../validation';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { CmdrulebuilderService } from '../services/cmdrulebuilder.service';
//import * as FileSaver from 'file-saver';
import * as _ from 'underscore';
import * as $ from 'jquery';


@Component({
    selector: 'app-cmdrulebuilder',
    templateUrl: './cmdrulebuilder.component.html',
    styleUrls: ['./cmdrulebuilder.component.scss'],
    providers: [CmdrulebuilderService]
})

export class CmdrulebuilderComponent implements OnInit {
    xmlSuccessModalBlock: any;
    rootElemData: any;
    currActiveTab: any;
    currEditElement: any = null;
    currEditRoot: any = null;
    rootAndElementData: any;
    editableRootFormArray = [];
    tableData: any;
    showNoDataFound: boolean;
    tableShowHide: boolean;
    shellTableData: boolean;
    ciqShellOrXmlBlock:boolean;
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
    fileRootRowId:any;
    // Following variables are used to dispaly success, confirm and failure model(s)
    showModelMessage: boolean = false;
    modelData: any;
    closeResult: string;
    currentEditRow: any;
    // To track activity
    searchStatus: string;
    migrationType ="";
    ciqShellOrXml = "";
    subType:any;
    status:any = "";;
    operator:any = "";;
    searchCriteria: any;
    sessionExpiredModalBlock: any; // Helps to close/open the model window
    pager: any = {}; // pager Object
    message: any;
    successModalBlock: any;
    successModalDelBlock:any;
    shellSuccessModalBlock: any;
    successModalCiq:any;
    shellSuccessModalDelBlock: any;
    navigationSubscription: any;
    shellSearchBlock: boolean = false;
    shellCreateBlock:boolean = false;
    xmlSearchBlock:boolean = false;
    xmlCreateBlock:boolean = false;
    rootAndElementTabs:boolean;
    editIndex: number = -1;
    rootFinalData: any = [];
    elementFinalData: any = [];
    enableAddBtn:boolean = false
    formWidth:any;
    scrollLeft:any;
    cmdOperator: any = [">=", ">", "<=", "<", "==", "!=", "CONTAINS"];
    prompts:any;
    selectedSearchPrompt:any;
    selectedPrompt:any;
    prompt:any;
    delimiter: string = "|";

    programChangeSubscription:any;


    validationData: any = {
        "rules": {
            "ruleName": {
                "required": true,
                "minlength": 3
            },
            "cmdName": {
                "required": true
            },
            "operand1Values": {
                "required": true,
                "customfunction": false
            },
            "operand1ColumnNames": {
                "required": false
            },
            "operator": {
                "required": true
            },
            "operand2Values": {
                "required": true,
                "customfunction": false
            },
            "operand2ColumnNames": {
                "required": false
            },
            "prompt": {
                "required": true
            },
            "status": {
                "required": true
            },
            "remarks": {
                "required": false
            },
            "createdBy":{
                "required": true
            },
            "searchParams": {
                "required": true
            },
            "searchBy": {
                "required": false
            },
            "paramsSearch": {
                "required": false
            },
            "searchRuleName":{
                "required": false 
            }, 
            "searchPrompt":{
                "required": false 
            },
            "searchOperand2":{
                "required": false 
            },  
            "searchOperand1":{
                "required": false 
            },  
            "searchCmdName":{
                "required": false 
            },
            'xmlRuleName':{
                "required": true 
            },
            'xmlRootName':{
                "required": true 
            },
            'xmlSubRootName':{
                "required": false 
            },
            'xmlCreatedBy':{
                "required": false 
            },
            "xmlSearchRuleName":{
                "required": false 
            },
            "xmlSearchRootName":{
                "required": false 
            },
            "xmlSearchSubRootName":{
                "required": false 
            },
            "xmlSearchCreatedBy":{
                "required": false 
            }
        },
        "messages": {
            "ruleName": {
                "required": "Rule Name is required",
                "minlength": "min length should not be lesser than 3 characters"
            },
            "cmdName": {
                "required": "Command is required"
            },
            "operand1Values": {
                "required": "Operand 1 is required",
                "customfunction": "No. of values should be same as in column"
            },
            "operand1ColumnNames": {
                "required": "Operand 1 column name is required"
            },
            "operator": {
                "required": "Operator is required"
            },
            "operand2Values": {
                "required": "Operand 2 is required",
                "customfunction": "No. of values should be same as in column"
            },
            "operand2ColumnNames": {
                "required": "Operand 2 column name is required"
            },
            "prompt": {
                "required": "PROMPT is required"
            },
            "status": {
                "required": "Status is required"
            },
            "remarks": {
                "required": "Command Description is required"
            },
            "createdBy":{
                "required": "Created By is required"
            },
            "searchParams": {
                "required": "Search Parameter is required"
            },
            "searchBy": {
                "required": "SearchBy is required"
            },
            "paramsSearch": {
                "required": "Search Parameter is required"
            },
            "searchRuleName":{
                "required": "Rule Name is required" 
            }, 
            "searchPrompt":{
                "required": "Prompt is required" 
            },
            "searchOperand2":{
                "required": "Opearand2 is required" 
            },  
            "searchOperand1":{
                "required": "Opearand1 is required" 
            },  
            "searchCmdName":{
                "required":  "Command Name is required"
            },
            'xmlRuleName':{
                "required":  "Rule Name is required"
            },
            'xmlRootName':{
                "required":  "Root Name is required"
            },
            'xmlSubRootName':{
                "required":  "Sub Root Name is required"
            },
            'xmlCreatedBy':{
                "required":  "Created By is required"
            },
            "xmlSearchRuleName":{
                "required": "Rule Name is required" 
            },
            "xmlSearchRootName":{
                "required": "Root Name is required" 
            },
            "xmlSearchSubRootName":{
                "required": "Sub Root Name is required" 
            },
            "xmlSearchCreatedBy":{
                "required": "Created By is required" 
            }
        }
    };

    @ViewChild('searchTab') searchTabRef;
    @ViewChild('createNewTab') createNewTabRef;
    @ViewChild('bluePrintForm') bluePrintFormRef;
    @ViewChild('xmlBluePrintForm') xmlBluePrintFormRef;
    @ViewChild('searchForm') searchFormRef;
    @ViewChild('sessionExpiredModal') sessionExpiredModalRef: ElementRef;
    @ViewChild('successModal') successModalRef: ElementRef;
    @ViewChild('xmlDelModal') successModalDelRef:ElementRef;
    @ViewChild('xmlSuccessModal') xmlSuccessModalRef :ElementRef;
    @ViewChild('shellDelModal') shellSuccessModalDelRef:ElementRef;
    @ViewChild('shellSuccessModal') shellSuccessModalRef :ElementRef;
    @ViewChild('successModalCiq') successModalCiqRef :ElementRef;
    @ViewChild('rootDetailsAddInlineRow') rootDetailsTemplate;
    @ViewChild('elementDetailsAddInlineRow') elementDetailsTemplate;
    @ViewChild('rootDetailsContainer') rootDetailsContainer;
    @ViewChild('elementsDetailsContainer') elementsDetailsContainer;
    constructor(
        private element: ElementRef,
        private renderer: Renderer,
        private router: Router,
        private modalService: NgbModal,
        private cmdrulebuilderService: CmdrulebuilderService,
        private sharedService: SharedService
    ) {
        /* this.navigationSubscription = this.router.events.subscribe((e: any) => {
            // If it is a NavigationEnd event re-initalise the component
            // console.log("e");
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
        this.searchBlock = true;
        this.createNewForm = false;
        this.shellSearchBlock = false;
        this.shellCreateBlock = false;
        this.xmlSearchBlock = false;
        this.xmlCreateBlock = false;
        this.rootAndElementTabs = false;
        this.searchStatus = 'load';
        this.showLoader = true;
        this.setMenuHighlight("search");
        this.validationData.rules.searchBy.required = false;
        this.validationData.rules.paramsSearch.required = false;
        //For Pagination
        this.currentPage = 1;
        this.totalPages = 1;
        this.TableRowLength = 10;
        this.pageSize = 10;
        this.editableFormArray = [];
        this.rootElementJsonData();
        this.migrationType = "premigration";
        this.ciqShellOrXml = 'ciqShell';
        if (this.migrationType == 'migration') {
            this.subType = "PRECHECK";
        } else if (this.migrationType == 'postmigration') {
            this.subType = "AUDIT";
        } else {
            this.subType = "";
        }


        let paginationDetails = {
            "count": this.TableRowLength,
            "page": this.currentPage
        };

        this.paginationDetails = paginationDetails;
        // Get all Airline Config Details on page start

        this.getAllCmdRules();
        this.resetSearchForm();
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
    resetSearchForm() {
        setTimeout(() => {
            this.searchFormRef.nativeElement.reset();
        }, 0);
    }

    searchTabBind() {
        let searchCrtra = { "partName": "", "melCategory": "", "policyDuedate": "" };
        this.searchCriteria = searchCrtra;

        this.setMenuHighlight("search");
        this.searchBlock = true;
        this.createNewForm = false;
        this.searchStatus = 'load';

        //this.searchTabRef.resetForm();

        if(this.ciqShellOrXml == 'xml') {
            this.xmlSearchBlock = true;
            this.xmlCreateBlock = false;
            this.rootAndElementTabs = false;
            this.getAllXmlData();
        } else if(this.ciqShellOrXml == 'shell') {
            this.shellSearchBlock = true;
            this.shellCreateBlock = false;
            this.getAllShellData();
        } else {
            this.selectedPrompt="";
            this.getAllCmdRules();
        }
        
        // Close if edit form is in open state
        if (this.currentEditRow != undefined) {
            this.currentEditRow.className = "editRow";
        }
        this.editableFormArray = [];
        this.editableRootFormArray = [];
        this.searchFormRef.nativeElement.reset();

        setTimeout(() => {
            validator.performValidation(event, this.validationData, "search");
        }, 10);
    }

    /* Used to add new Airline config details 
     * @param : event
     * @retun : null
     */

    createNewTabBind() {
        this.showNoDataFound = false;
        this.tableShowHide = false;
        this.shellTableData = false;
        this.ciqShellOrXmlBlock = false;
        this.searchBlock = false;
        this.createNewForm = true;
        this.selectedPrompt="";

        if(this.ciqShellOrXml == 'xml') {
            this.xmlSearchBlock = false;
            this.xmlCreateBlock = true;
            this.createNewForm = false;
            this.rootAndElementTabs = true;
            this.rootFinalData=[];            
            this.elementFinalData=[];
            this.rootAndElementData['elementsDetailsData'] =[];
            this.rootAndElementData['rootDetailsData'] = [];
            this.rootAndElementData.rootDetailsData[0] = {
                          "rootKey": "",
                          "rootValue": "",
                          "rootId": "",
                          "inRootEditMode": true
            }            
            this.rootAndElementData.elementsDetailsData[0] = {
                    "elementName": "",
                    "operator": "",
                    "elementValue": "",
                    "elementId": "",
                    "inElementEditMode": true
            }      
        } else if(this.ciqShellOrXml == 'shell') {
            this.xmlSearchBlock = false;
            this.xmlCreateBlock = false;
            this.createNewForm = false;
            this.rootAndElementTabs = false;
            this.shellSearchBlock = false;
            this.shellCreateBlock = true;
        }
       
        this.setMenuHighlight("createNew");
        this.validationData.rules.searchBy.required = false;
        this.validationData.rules.paramsSearch.required = false;
        setTimeout(() => {
            this.currActiveTab = "tab-element";
            validator.performValidation(event, this.validationData, "create");
        }, 10);
    }

    searchByChanged(value) {
        $("#paramsSearch").val("");
        this.searchParamsChanged(value);   
        if (value) {
            validator.performValidation(event, this.validationData, "search");
            this.validationData.rules.paramsSearch.required = true;
        }
        else {
            this.validationData.rules.paramsSearch.required = false;
        }

    }


  clearSearchFrom() {
    if(this.searchFormRef) {
        this.searchFormRef.nativeElement.reset();
    }
    if(this.xmlBluePrintFormRef) {
        this.xmlBluePrintFormRef.nativeElement.reset();
    }
}

    searchParamsChanged(value) {
        if (value) {
            validator.performValidation(event, this.validationData, "search");
            this.validationData.rules.searchBy.required = true;
        }
        else {
            this.validationData.rules.searchBy.required = false;
        }
    }

    /*
     * Used to dispaly search result based on selected criteria
     * @param : event
     * @retun : null
     */

    searchCmdRules(event,page="cmd") {
        this.tableShowHide = false;
        this.shellTableData = false;
        this.ciqShellOrXmlBlock = false;
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

                    let currentForm = event.target.parentNode.parentNode.parentNode,
                        searchCrtra = {    
                            "ruleName": currentForm.querySelector("#searchRuleName").value,
                            "cmdName": currentForm.querySelector("#searchCmdName").value,
                            "operand1": currentForm.querySelector("#searchOperand1").value,
                            "operand2": currentForm.querySelector("#searchOperand2").value,
                            "prompt":currentForm.querySelector("#searchPrompt").value,
                            "status":currentForm.querySelector("#ruleStatus").value

                        };
                    if (searchCrtra.ruleName || searchCrtra.cmdName || searchCrtra.operand1 || searchCrtra.operand2 || searchCrtra.prompt || searchCrtra.status) {
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
                    if(page=="cmd")
                    {
                        this.getAllCmdRules();
                    }
                    else if(page="shell")
                    {
                        this.getAllShellData();
                    }
                }
            }
        }, 0);
    }

    searchXmlCmdRules(event) {

        this.tableShowHide = false;
        this.shellTableData = false;
        this.ciqShellOrXmlBlock = true;
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

                    let currentForm = event.target.parentNode.parentNode.parentNode,
                        searchCrtra = {     
                            "ruleName": currentForm.querySelector("#xmlSearchRuleName").value,
                            "cmdName": currentForm.querySelector("#searchCmdName").value,
                            "rootName": currentForm.querySelector("#xmlSearchRootName").value,
                            "subRootName": currentForm.querySelector("#xmlSearchSubRootName").value,
                            "prompt": currentForm.querySelector("#searchPrompt").value,
                            "status": currentForm.querySelector("#ruleStatus").value,
                            "createdBy": currentForm.querySelector("#xmlSearchCreatedBy").value
                        };
                    if (searchCrtra.ruleName || searchCrtra.cmdName || searchCrtra.rootName || searchCrtra.subRootName || searchCrtra.prompt || searchCrtra.status || searchCrtra.createdBy) {
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
                    this.getAllXmlData();
                }
            }
        }, 0);
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
    checkRootElemFormEnable(index) {
        let indexValue = this.editableRootFormArray.indexOf(index);
        return indexValue >= 0 ? true : false;
    }


    /*
     * Used to display the table data on load/by default
     * @param : repairStation,userName,reflect (edit/delete)
     * @retun : null
     */

    getAllCmdRules() {
        this.showLoader = true;
        $("#dataWrapper").find(".scrollBody").scrollLeft(0);
        this.cmdrulebuilderService.getAllCmdRules(this.migrationType,this.subType, this.searchStatus, this.searchCriteria, this.paginationDetails, this.sharedService.createServiceToken()).subscribe(
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
                                let pageCount = [];
                                for (var i = 1; i <= this.tableData.pageCount; i++) {
                                    pageCount.push(i);
                                }
                                this.pageRenge = pageCount;
                                this.pager = this.getPager(this.totalPages * this.pageSize, this.currentPage, this.pageRenge);

                                this.prompts = jsonStatue.prompt ? jsonStatue.prompt.promptList : [];
                                if(this.searchBlock){
                                // To display table data
                                if (this.tableData.cmdRuleBuilderData.length != 0) {

                                    this.showNoDataFound = false;
                                    this.tableShowHide = true;
                                    this.shellTableData = false;
                                    this.ciqShellOrXmlBlock = false;
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
                                    this.tableShowHide = false;
                                    this.shellTableData = false;
                                    this.ciqShellOrXmlBlock = false;
                                    this.showNoDataFound = true;
                                }
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

                 /*this.showLoader = true;
                setTimeout(() => {

                    this.showLoader = false;
                    //NoData
                    //   this.tableData = JSON.parse('{"sessionId":"7e088256","serviceToken":84933,"pageCount":4,"cmdRuleBuilderData":[],"status":"Success"}');
                    //Data
                    this.tableData = JSON.parse('{"sessionId":"293bbfa8","serviceToken":"95385","pageCount":1,"prompt":{"promptList":["[enbname]","[MSMA]","vsmuser@*LMD1","root@*LMD1","-MCMASTER","$","user@BLTSP02518:"]},"cmdRuleBuilderData":[{"id":1,"ruleName":"Test Rule Name","cmdName":"ls","operand1Values":"0,0,1","operand1ColumnNames":"A,B,C","operator":"CONTAINS","operand2Values":"d,e,f","operand2ColumnNames":"D,E,F","status":"Warn","timeStamp":"2019-05-17 12:45:10","useCount":0,"createdBy":"Bhuvana","remarks":"TestData","prompt":"$","loopType":"AtleastOne"}],"status":"SUCCESS"}');
                    // this.tableData = JSON.parse('{"reason":"Prompt template JSON is wrong","pageCount":0,"sessionId":"9008ddba","serviceToken":"88782","cmdRuleBuilderData":[],"status":"FAILED"}');
                    
                    if (this.tableData.status == "SUCCESS") {

                        this.totalPages = this.tableData.pageCount;
                        let pageCount = [];
                        for (var i = 1; i <= this.tableData.pageCount; i++) {
                            pageCount.push(i);
                        }
                        this.pageRenge = pageCount;
                        this.pager = this.getPager(this.totalPages * this.pageSize, this.currentPage, this.pageRenge);
                        // this.prompts = this.tableData.prompt.promptList;
                        this.prompts = this.tableData.prompt ? this.tableData.prompt.promptList : [];
                        if (this.searchBlock) {
                            // To display table data
                            if (this.tableData.cmdRuleBuilderData.length != 0) {
                                this.showNoDataFound = false;
                                this.tableShowHide = true;
                                this.shellTableData = false;
                                this.ciqShellOrXmlBlock = false;
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
                                this.tableShowHide = false;
                                this.shellTableData = false;
                                this.ciqShellOrXmlBlock = false;
                                this.showNoDataFound = true;
                            }
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
    createCmdRule(event) {

        validator.performValidation(event, this.validationData, "save_update");
        let currentForm = event.target.parentNode.parentNode,
                    fileRuleFormData = {
                        "ruleName": currentForm.querySelector("#ruleName").value,
                        "cmdName": currentForm.querySelector("#cmdName").value,
                        "operand1Values": currentForm.querySelector("#operand1Values").value,
                        "operand1ColumnNames": currentForm.querySelector("#operand1ColumnNames").value,
                        "operator": currentForm.querySelector("#operator").value,
                        "operand2Values": currentForm.querySelector("#operand2Values").value,
                        "operand2ColumnNames": currentForm.querySelector("#operand2ColumnNames").value,
                        "prompt":currentForm.querySelector("#prompt").value,
                        "loopType":currentForm.querySelector("#loopType").value,
                        "status": currentForm.querySelector("#status").value,
                        "remarks": currentForm.querySelector("#remarks").value
                    };
        this.checkValidation(fileRuleFormData);
        setTimeout(() => {

            if (this.isValidForm(event)) {

                this.showLoader = true;
                /* let currentForm = event.target.parentNode.parentNode,
                    fileRuleFormData = {
                        "ruleName": currentForm.querySelector("#ruleName").value,
                        "cmdName": currentForm.querySelector("#cmdName").value,
                        "operand1Values": currentForm.querySelector("#operand1Values").value,
                        "operand1ColumnNames": currentForm.querySelector("#operand1ColumnNames").value,
                        "operator": currentForm.querySelector("#operator").value,
                        "operand2Values": currentForm.querySelector("#operand2Values").value,
                        "operand2ColumnNames": currentForm.querySelector("#operand2ColumnNames").value,
                        "prompt":currentForm.querySelector("#prompt").value,
                        "loopType":currentForm.querySelector("#loopType").value,
                        "status": currentForm.querySelector("#status").value,
                        "remarks": currentForm.querySelector("#remarks").value
                    }; */
                this.cmdrulebuilderService.createCmdRule(this.migrationType, this.subType, fileRuleFormData, this.sharedService.createServiceToken())
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
                                        //this.searchTabBind();
                                        setTimeout(() => {
                                            this.showLoader = false;
                                            this.message = "CLI Command Rule created successfully!";
                                            this.successModalCiq= this.modalService.open(this.successModalCiqRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });                                            
                                            this.searchStatus = "load";
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
                            /* setTimeout(() => {
              
                              this.showLoader = false;
              
                              let jsonStatue = { "sessionId": "e9004f23", "reason": null, "status": "SUCCESS", "serviceToken": "64438" };
              
                              if (jsonStatue.status == "SUCCESS") {
                                //this.searchTabBind();
                                setTimeout(() => {
                                    this.showLoader = false;
                                    this.message = "CLI Command Rule created successfully!";
                                    this.successModalCiq= this.modalService.open(this.successModalCiqRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });                                            
                                    this.searchStatus = "load";
                                }, 1000);
              
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




    /*
     * on click of edit row create a blueprint and append next to the current row
     * @param : current row event , current row json object and row index
     * @retun : null
     */

    editRow(event, key, index) {
        //console.log(key);
        this.fileRuleRowId = key.id;
        let editState: any = event.target;

        $(".editRowDisabled").attr("class","editRow");
        $(".deleteRowDisabled").attr("class","deleteRow");
        if (editState.className != "editRowDisabled") { //enable click only if it is enabled

            if (!document.querySelector("#searchButton").classList.contains('buttonDisabled')) {
                // Disable search button while editing 
                document.querySelector("#searchButton").classList.add("buttonDisabled");
                this.paginationDisabbled = true;
            }

            editState.className = "editRowDisabled";
            $(editState).next().attr("class", "deleteRowDisabled") // Disable delete on edit row        
            /* // If any edit form is opend then close the form and enable edit button
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

            setTimeout(() => {
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

        if (currentEditedForm && currentEditedForm.lastElementChild) {
            currentEditedForm.lastElementChild.lastElementChild.children[0].className = "editRow";
        }


        // Enable search button 
        document.querySelector("#searchButton").classList.remove("buttonDisabled");
        this.paginationDisabbled = false;

    }
    
    cancelXmlEditRow(index, identifier) {
        $(".editRowDisabled").attr("class", "editRow");
        $(".deleteRowDisabled").attr("class","deleteRow");
        let currentEditedForm = document.querySelector(".row_id_" + identifier);

        this.editableRootFormArray.splice(this.editableRootFormArray.indexOf(index), 1);

        this.checkRootElemFormEnable(index); //TODO : need to recheck this function

        if(currentEditedForm && currentEditedForm.lastElementChild) {
            currentEditedForm.lastElementChild.lastElementChild.children[0].className = "editRow";
        }


        // Enable search button 
        document.querySelector("#xmlSearchButton").classList.remove("buttonDisabled");
        this.paginationDisabbled = false;
    }

    /*
     * On click delete button open a modal for confirmation for delete entire row
     * @param : content, userName
     * @retun : null
     */
    deleteRow(event, confirmModal, rowId) {

        let deleteState = event.target;

        if (deleteState.className != "deleteRowDisabled") {

            this.modalService.open(confirmModal, {
                keyboard: false,
                backdrop: 'static',
                size: 'lg',
                windowClass: 'confirm-modal'
            }).result.then((result) => {
                this.closeResult = `Closed with: ${result}`;
            }, (reason) => {

                this.showLoader = true;

                this.cmdrulebuilderService.deleteCmdRuleDeta(rowId, this.sharedService.createServiceToken())
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
                                        this.message = "Command Rule deleted successfully!";
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
                                  this.message = "Command Rule deleted successfully!";
                                  this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                              }
                            }, 1000); */
                            //Please Comment while checkIn
                        });
            });
        }
    }

    deleteXmlCmdRow(event, confirmModal, rowId) {

        let deleteState = event.target;

        if (deleteState.className != "deleteRowDisabled") {

            this.modalService.open(confirmModal, {
                keyboard: false,
                backdrop: 'static',
                size: 'lg',
                windowClass: 'confirm-modal'
            }).result.then((result) => {
                this.closeResult = `Closed with: ${result}`;
            }, (reason) => {

                this.showLoader = true;

                this.cmdrulebuilderService.deleteXmlCmdRow(rowId, this.sharedService.createServiceToken())
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
                                        this.message = "XML Command Rule deleted successfully!";
                                        this.successModalDelBlock = this.modalService.open(this.successModalDelRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
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
                                  this.message = "XML Command Rule deleted successfully!";
                                  this.successModalDelBlock = this.modalService.open(this.successModalDelRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
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
    updateCmdRuleDetails(event) {
        setTimeout(() => {
            $("#dataWrapper").find(".scrollBody").scrollLeft(0);
        }, 0);

        validator.performValidation(event, this.validationData, "save_update");
        let currentForm = event.target.parentNode.parentNode,
            fileRuleFormData = {
                "id": this.fileRuleRowId,
                "ruleName": currentForm.querySelector("#ruleName").value,
                "cmdName": currentForm.querySelector("#cmdName").value,
                "operand1Values": currentForm.querySelector("#operand1Values").value,
                "operand1ColumnNames": currentForm.querySelector("#operand1ColumnNames").value,
                "operator": currentForm.querySelector("#operator").value,
                "operand2Values": currentForm.querySelector("#operand2Values").value,
                "operand2ColumnNames": currentForm.querySelector("#operand2ColumnNames").value,
                "prompt":currentForm.querySelector("#prompt").value,
                "loopType":currentForm.querySelector("#loopType").value,
                "status": currentForm.querySelector("#status").value,
                "remarks": currentForm.querySelector("#remarks").value
                                        
            };
        this.checkValidation(fileRuleFormData);
        setTimeout(() => {
            if (this.isValidForm(event)) {
                this.showLoader = true;

                /* let currentForm = event.target.parentNode.parentNode,
                    fileRuleFormData = {
                        "id": this.fileRuleRowId,
                         "ruleName": currentForm.querySelector("#ruleName").value,
                        "cmdName": currentForm.querySelector("#cmdName").value,
                        "operand1Values": currentForm.querySelector("#operand1Values").value,
                        "operand1ColumnNames": currentForm.querySelector("#operand1ColumnNames").value,
                        "operator": currentForm.querySelector("#operator").value,
                        "operand2Values": currentForm.querySelector("#operand2Values").value,
                        "operand2ColumnNames": currentForm.querySelector("#operand2ColumnNames").value,
                        "prompt":currentForm.querySelector("#prompt").value,
                        "loopType":currentForm.querySelector("#loopType").value,
                        "status": currentForm.querySelector("#status").value,
                        "remarks": currentForm.querySelector("#remarks").value
                                                
                    }; */

                this.cmdrulebuilderService.updateCmdRuleDetails(this.migrationType, this.subType,fileRuleFormData, this.sharedService.createServiceToken())
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

                                        document.querySelector("#searchButton").classList.remove("buttonDisabled");
                                        this.message = "Command Rule updated successfully!";
                                        this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                                        // Hide all the form/Table/Nodatafound
                                        this.tableShowHide = false;
                                        this.shellTableData = false;
                                        this.ciqShellOrXmlBlock = false;
                                        this.showNoDataFound = false;
                                        this.createNewForm = false;

                                        // Cancel current edit form
                                        this.cancelEditRow(fileRuleFormData.id, '');

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
                              document.querySelector("#searchButton").classList.remove("buttonDisabled");
                              this.showLoader = true;
                              setTimeout(() => {
                                this.showLoader = false;
                                this.message = "Command Rule updated successfully!";
                                  this.successModalBlock = this.modalService.open(this.successModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'success-modal'});
                                // Cancel current edit form
                                this.cancelEditRow(fileRuleFormData.id, '');
                              }, 1000);
                            } else {
                              this.displayModel(jsonStatue.reason, "failureIcon");
                            } */
                            //Please Comment while checkIn
                        });
            }
        }, 0);
    }
    /* on Update of xml table */
    updateXmlCmdRuleDetails(event) {
        setTimeout(() => {
            $("#dataWrapper").find(".scrollBody").scrollLeft(0);
        }, 0);

        validator.performValidation(event, this.validationData, "save_update");


        if(this.rootAndElementData.rootDetailsData.length >0 ){
            for(let i=0; i< this.rootAndElementData.rootDetailsData.length; i++){
                if(this.rootAndElementData.rootDetailsData[i].inRootEditMode){
                    this.rootAndElementData.rootDetailsData.splice(i,1);
                }
            }
            this.rootAndElementData.rootDetailsData = this.rootAndElementData.rootDetailsData; // Sending fileRules data if exists
        } else {                    
            this.rootAndElementData.rootDetailsData = []; // Sending Empty array if no rules is added
        }

        if(this.rootAndElementData.elementsDetailsData.length >0 ){
            for(let i=0; i< this.rootAndElementData.elementsDetailsData.length; i++){
                if(this.rootAndElementData.elementsDetailsData[i].inElementEditMode){
                    this.rootAndElementData.elementsDetailsData.splice(i,1);
                }
            }
            this.rootAndElementData.elementsDetailsData = this.rootAndElementData.elementsDetailsData; // Sending fileRules data if exists
        } else {                    
            this.rootAndElementData.elementsDetailsData = []; // Sending Empty array if no rules is added
        } 
            
             
        setTimeout(() => {
            if (this.isValidForm(event)) {
                this.showLoader = true;

                let currentForm = event.target.parentNode.parentNode,
                    xmlTableFormData = {
                        "id": this.fileRootRowId,
                        "ruleName": currentForm.querySelector("#xmlRuleName").value,
                        "cmdName": currentForm.querySelector("#cmdName").value,
                        "rootName":currentForm.querySelector("#xmlRootName").value,
                        "subRootName":currentForm.querySelector("#xmlSubRootName").value,
                        "loopType": currentForm.querySelector("#loopType").value,
                        "prompt":currentForm.querySelector("#prompt").value,
                        "status":currentForm.querySelector("#status").value,
                        "remarks":currentForm.querySelector("#xmlRemarks").value,
                        "createdBy":currentForm.querySelector("#xmlCreatedBy").value,
                        "rootDetails": this.rootAndElementData.rootDetailsData,
                        "elementDetails":this.rootAndElementData.elementsDetailsData
                    };

                this.cmdrulebuilderService.updateXmlCmdRuleDetails(this.migrationType, this.subType,xmlTableFormData, this.sharedService.createServiceToken())
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

                                        document.querySelector("#xmlSearchButton").classList.remove("buttonDisabled");
                                        this.message = "XML Command Rule updated successfully!";
                                        this.xmlSuccessModalBlock = this.modalService.open(this.xmlSuccessModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                                        // Hide all the form/Table/Nodatafound
                                        this.tableShowHide = false;
                                        this.shellTableData = false;
                                        this.ciqShellOrXmlBlock = false;
                                        this.showNoDataFound = false;
                                        this.createNewForm = false;

                                        // Cancel current edit form
                                        this.cancelXmlEditRow(xmlTableFormData.id, '');

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
                              document.querySelector("#xmlSearchButton").classList.remove("buttonDisabled");
                              this.showLoader = true;
                              setTimeout(() => {
                                this.showLoader = false;
                                this.message = "XML Command Rule updated successfully!";
                                this.xmlSuccessModalBlock = this.modalService.open(this.xmlSuccessModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                                // Cancel current edit form
                                this.cancelXmlEditRow(xmlTableFormData.id, '');
                              }, 1000);
                            } else {
                              this.displayModel(jsonStatue.reason, "failureIcon");
                            } */
                            //Please Comment while checkIn
                        });
            }
        }, 0);
    }


    /* On click of search highlight open record types
     * @param : current Tab Item (open/close)
     * @retun : null
     */

    setMenuHighlight(selectedElement) {
        this.searchTabRef.nativeElement.id = (selectedElement == "search") ? "activeTab" : "inactiveTab";
        this.createNewTabRef.nativeElement.id = (selectedElement == "createNew") ? "activeTab" : "inactiveTab";
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
            if(this.ciqShellOrXml == 'ciqShell'){
                setTimeout(() => {
                    this.showLoader = false;
                    $("#dataWrapper").find(".scrollBody").scrollLeft(0);
                    this.getAllCmdRules();
                }, 0);
            }
            if(this.ciqShellOrXml == 'shell'){
                setTimeout(() => {
                    this.showLoader = false;
                    $("#dataWrapper").find(".scrollBody").scrollLeft(0);
                    this.getAllShellData();
                }, 0);
            }
            if(this.ciqShellOrXml == 'xml'){
                setTimeout(() => {
                    this.showLoader = false;
                    $("#dataWrapper").find(".scrollBody").scrollLeft(0);
                    this.getAllXmlData();
                }, 0);
            }


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
        // this.ngOnInit();
        this.getAllCmdRules();
        this.resetSearchForm();
    }
    closeCiqModel() {
        this.successModalCiq.close();
        this.searchTabBind();
    }
    closeModelXmlDel() {
        this.successModalDelBlock.close();
        this.getAllXmlData();
    }
    
    closeXmlModel() {
        this.xmlSuccessModalBlock.close();
        this.getAllXmlData();
    }

    closeShellModel() {
        this.shellSuccessModalBlock.close();
        this.getAllShellData();
    }
    closeModelShellDel() {
        this.shellSuccessModalDelBlock.close();
        this.getAllShellData();
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
        if(this.ciqShellOrXml == 'ciqShell'){
            setTimeout(() => {
                this.showLoader = false;
                $("#dataWrapper").find(".scrollBody").scrollLeft(0);
                this.getAllCmdRules();
            }, 0);
        }
        if(this.ciqShellOrXml == 'shell'){
            setTimeout(() => {
                this.showLoader = false;
                $("#dataWrapper").find(".scrollBody").scrollLeft(0);
                this.getAllShellData();
            }, 0);
        }
        if(this.ciqShellOrXml == 'xml'){
            setTimeout(() => {
                this.showLoader = false;
                $("#dataWrapper").find(".scrollBody").scrollLeft(0);
                this.getAllXmlData();
            }, 0);
        }


        

    }

    /*
     * On click sort header in table then sort the data ascending and decending
     * @param : columnName, event and current Index
     * @retun : null
     */

    changeSorting(predicate, event, index,tablename) {
        if(tablename=='xml')
        {
            this.sharedService.dynamicSort(predicate, event, index, this.tableData.xmlRuleDetail);
        }
        else if (tablename=='shell')
        {
            this.sharedService.dynamicSort(predicate, event, index, this.tableData.cmdRuleBuilderData);

        }else if (tablename=='ciq')
        {
            this.sharedService.dynamicSort(predicate, event, index, this.tableData.cmdRuleBuilderData);

        }

    }

/* Call onLoadService on change of Migration Type Radio Btn */
onChangeLoad(value){
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
    if((this.migrationType || this.subType) && this.ciqShellOrXml == 'xml' ) {
        this.tableShowHide = false;
        //this.xmlSearchBlock = true;
        this.searchTabBind();
        this.shellTableData = false;
        this.ciqShellOrXmlBlock = true;
        this.rootAndElementTabs = false;
        this.xmlBluePrintFormRef.nativeElement.reset();
        this.rootDetailsContainer.nativeElement.reset();
        this.elementsDetailsContainer.nativeElement.reset();
        this.getAllXmlData();
    } else if((this.migrationType || this.subType) && this.ciqShellOrXml == 'ciqShell') {
        this.searchTabBind();
        this.selectedPrompt="";
        this.xmlSearchBlock = false;
        this.shellTableData = false;
        this.getAllCmdRules();
    } else if((this.migrationType || this.subType) && this.ciqShellOrXml == 'shell') {
        this.searchTabBind();
        this.shellSearchBlock = false;
        this.getAllShellData();
    }


   // this.setMenuHighlight("search");    
    if(this.createNewForm){ 
        $('#ruleName').val('');
        $('#cmdName').val('');
        $('#operand1').val('');
        $('#operand1ColumnName').val('');
        $('#operator').val('');
        $('#operand2').val('');
        $('#operand2ColumnName').val('');
        $('#status').val('');
        $('#remarks').val('');
    }else if(this.searchBlock){        
        $('#searchRuleName').val('');
        $('#searchCmdName').val('');
        $('#searchOperand1').val('');
        $('#searchOperand2').val('');       
        $('#searchPrompt').val('');
    }
    
}
/* Call onLoadService on change of SubType */
onChangeType(value){
   
    this.subType = value;
    if(this.createNewForm){
        this.bluePrintFormRef.nativeElement.reset();
    }else if(this.searchBlock){
        this.searchFormRef.nativeElement.reset();
    }

    if(this.migrationType && this.ciqShellOrXml == 'xml' && this.subType ) {
        this.tableShowHide = false;
        this.xmlSearchBlock = true;
        this.ciqShellOrXmlBlock = true;
        this.getAllXmlData();
        this.xmlBluePrintFormRef.nativeElement.reset();
    }else if(this.migrationType && this.ciqShellOrXml == 'shell' && this.subType ) {
        this.tableShowHide = false;
        this.shellSearchBlock = true;
        this.ciqShellOrXmlBlock = true;
        this.getAllShellData();
    }else if(this.migrationType) {
        this.selectedPrompt="";
        this.xmlSearchBlock = false;
        this.getAllCmdRules();
    }
}

cliShellXmlRadioToggle(value) {    
    if(value == 'ciqShell') {
        this.xmlSearchBlock = false;
        this.searchBlock = true;
        this.rootAndElementTabs= false;
        this.xmlCreateBlock= false;
        this.selectedSearchPrompt="";
        this.shellSearchBlock = false;
        this.shellCreateBlock = false;
        this.setMenuHighlight("search");
        this.getAllCmdRules();
    }else if(value == 'shell') {
        this.getAllShellData();
    }else if(value == 'xml') {        
        this.getAllXmlData();
    }
}

getAllShellData() {
    this.showLoader = true;
    this.tableShowHide = false;
    this.shellTableData = false;
    this.ciqShellOrXmlBlock = false;
    this.searchBlock = false;
    this.createNewForm = false;
    this.shellSearchBlock = true;
    this.shellCreateBlock = false;
    this.xmlSearchBlock = false;
    this.xmlCreateBlock = false;
    this.ciqShellOrXml == 'xml';
    this.setMenuHighlight("search");
    this.cmdrulebuilderService.getAllShellData(this.migrationType, this.subType, this.searchStatus, this.searchCriteria, this.paginationDetails, this.sharedService.createServiceToken()).subscribe(
        data => {
            let jsonStatue = data.json();
            if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
                if (this.sessionExpiredModalBlock) {
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
                            let pageCount = [];
                            for (var i = 1; i <= this.tableData.pageCount; i++) {
                                pageCount.push(i);
                            }
                            this.pageRenge = pageCount;
                            this.pager = this.getPager(this.totalPages * this.pageSize, this.currentPage, this.pageRenge);

                            this.prompts = jsonStatue.prompt ? jsonStatue.prompt.promptList : [];
                            // To display table data
                            if (this.tableData.cmdRuleBuilderData.length != 0) {

                                this.showNoDataFound = false;
                                this.tableShowHide = false;
                                this.shellTableData = true;
                                this.ciqShellOrXmlBlock = false;
                                setTimeout(() => {
                                    let tableWidth = document.getElementById('melConfigDetails').scrollWidth;
                                    $(".scrollBody table#removalDetailsData").css("min-width", (tableWidth) + "px");
                                    $(".scrollHead table#melConfigDetails").css("width", tableWidth + "px");


                                    $(".scrollBody").on("scroll", function (event) {
                                        $("#removalDetailsData .formEditRow #formWrapper").css("margin-left", (event.target.scrollLeft - 5) + "px");
                                        $(".shellRow .form-control-fixed").css("right", (event.target.scrollLeft * -1) + "px");
                                        $(".scrollHead table#melConfigDetails").css("margin-left", (event.target.scrollLeft * -1) + "px");
                                    });
                                    $(".scrollBody").css("max-height", (this.tableDataHeight - 10) + "px");
                                }, 0);
                            } else {
                                this.tableShowHide = false;
                                this.shellTableData = false;
                                this.ciqShellOrXmlBlock = false;
                                this.showNoDataFound = true;
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

             /*this.showLoader = true;
            setTimeout(() => {

                this.showLoader = false;
                //NoData
                //   this.tableData = JSON.parse('{"sessionId":"293bbfa8","serviceToken":"95385","pageCount":1,"prompt":{"promptList":["[enbname]","[MSMA]","vsmuser@*LMD1","root@*LMD1","-MCMASTER","$","user@BLTSP02518:"]},"cmdRuleBuilderData":[],"status":"SUCCESS"}');
                //Data
                //this.tableData = JSON.parse('{"sessionId":"293bbfa8","serviceToken":"95385","pageCount":1,"status":"SUCCESS","prompt":{"promptList":["[enbname]","[MSMA]","vsmuser@*LMD1","root@*LMD1","-MCMASTER","$","user@BLTSP02518:"]},"cmdRuleBuilderData":[{"id":11,"ruleName":"Shell Rule Name","cmdName":"ls","resultType":"Line","operand1Values":"0,0,1","operand1ColumnNames":"A,B,C","operator":"","operand2Values":"","operand2ColumnNames":"","status":"Warn","timeStamp":"2019-05-17 12:45:10","useCount":0,"createdBy":"Shahid","remarks":"ShellTestNoData","prompt":"$","loopType":"All"},{"id":11,"ruleName":"Shell Name","cmdName":"ls","resultType":"Table","operand1Values":"0,0,1","operand1ColumnNames":"A,B,C","operator":"CONTAINS","operand2Values":"d,e,f","operand2ColumnNames":"D,E,F","status":"Warn","timeStamp":"2019-05-17 12:45:10","useCount":0,"createdBy":"Noor","remarks":"ShellTestData","prompt":"$","loopType":"AtleastOne"}]}');
                this.tableData = JSON.parse('{"sessionId":"293bbfa8","serviceToken":"95385","pageCount":1,"status":"SUCCESS","prompt":{"promptList":["[enbname]","[MSMA]","vsmuser@*LMD1","root@*LMD1","-MCMASTER","$","user@BLTSP02518:"]},"cmdRuleBuilderData":[{"id":11,"ruleName":"Shell Rule Name","cmdName":"ls","resultType":"Line","operand1Values":"0,0,1","operand1ColumnNames":"A,B,C","operator":"","operand2Values":"","operand2ColumnNames":"","status":"Warn","timeStamp":"2019-05-17 12:45:10","useCount":0,"createdBy":"Shahid","remarks":"ShellTestNoData","prompt":"$","loopType":"All"},{"id":23,"ruleName":"Shell Rule Name","cmdName":"ls","resultType":"Line","operand1Values":"0,0,1","operand1ColumnNames":"A,B,C","operator":"","operand2Values":"","operand2ColumnNames":"","status":"Warn","timeStamp":"2019-05-17 12:45:10","useCount":0,"createdBy":"Shahid","remarks":"ShellTestNoData","prompt":"$","loopType":"All"},{"id":22,"ruleName":"Shell Rule Name","cmdName":"ls","resultType":"Line","operand1Values":"0,0,1","operand1ColumnNames":"A,B,C","operator":"","operand2Values":"","operand2ColumnNames":"","status":"Warn","timeStamp":"2019-05-17 12:45:10","useCount":0,"createdBy":"Shahid","remarks":"ShellTestNoData","prompt":"$","loopType":"All"},{"id":21,"ruleName":"Shell Rule Name","cmdName":"ls","resultType":"Line","operand1Values":"0,0,1","operand1ColumnNames":"A,B,C","operator":"","operand2Values":"","operand2ColumnNames":"","status":"Warn","timeStamp":"2019-05-17 12:45:10","useCount":0,"createdBy":"Shahid","remarks":"ShellTestNoData","prompt":"$","loopType":"All"},{"id":19,"ruleName":"Shell Rule Name","cmdName":"ls","resultType":"Line","operand1Values":"0,0,1","operand1ColumnNames":"A,B,C","operator":"","operand2Values":"","operand2ColumnNames":"","status":"Warn","timeStamp":"2019-05-17 12:45:10","useCount":0,"createdBy":"Shahid","remarks":"ShellTestNoData","prompt":"$","loopType":"All"},{"id":18,"ruleName":"Shell Rule Name","cmdName":"ls","resultType":"Line","operand1Values":"0,0,1","operand1ColumnNames":"A,B,C","operator":"","operand2Values":"","operand2ColumnNames":"","status":"Warn","timeStamp":"2019-05-17 12:45:10","useCount":0,"createdBy":"Shahid","remarks":"ShellTestNoData","prompt":"$","loopType":"All"},{"id":17,"ruleName":"Shell Rule Name","cmdName":"ls","resultType":"Line","operand1Values":"0,0,1","operand1ColumnNames":"A,B,C","operator":"","operand2Values":"","operand2ColumnNames":"","status":"Warn","timeStamp":"2019-05-17 12:45:10","useCount":0,"createdBy":"Shahid","remarks":"ShellTestNoData","prompt":"$","loopType":"All"},{"id":16,"ruleName":"Shell Rule Name","cmdName":"ls","resultType":"Line","operand1Values":"0,0,1","operand1ColumnNames":"A,B,C","operator":"","operand2Values":"","operand2ColumnNames":"","status":"Warn","timeStamp":"2019-05-17 12:45:10","useCount":0,"createdBy":"Shahid","remarks":"ShellTestNoData","prompt":"$","loopType":"All"},{"id":15,"ruleName":"Shell Rule Name","cmdName":"ls","resultType":"Line","operand1Values":"0,0,1","operand1ColumnNames":"A,B,C","operator":"","operand2Values":"","operand2ColumnNames":"","status":"Warn","timeStamp":"2019-05-17 12:45:10","useCount":0,"createdBy":"Shahid","remarks":"ShellTestNoData","prompt":"$","loopType":"All"},{"id":14,"ruleName":"Shell Rule Name","cmdName":"ls","resultType":"Line","operand1Values":"0,0,1","operand1ColumnNames":"A,B,C","operator":"","operand2Values":"","operand2ColumnNames":"","status":"Warn","timeStamp":"2019-05-17 12:45:10","useCount":0,"createdBy":"Shahid","remarks":"ShellTestNoData","prompt":"$","loopType":"All"},{"id":13,"ruleName":"Shell Rule Name","cmdName":"ls","resultType":"Line","operand1Values":"0,0,1","operand1ColumnNames":"A,B,C","operator":"","operand2Values":"","operand2ColumnNames":"","status":"Warn","timeStamp":"2019-05-17 12:45:10","useCount":0,"createdBy":"Shahid","remarks":"ShellTestNoData","prompt":"$","loopType":"All"},{"id":12,"ruleName":"Shell Name","cmdName":"ls","resultType":"Table","operand1Values":"0,0,1","operand1ColumnNames":"A,B,C","operator":"CONTAINS","operand2Values":"d,e,f","operand2ColumnNames":"D,E,F","status":"Warn","timeStamp":"2019-05-17 12:45:10","useCount":0,"createdBy":"Noor","remarks":"ShellTestData","prompt":"$","loopType":"AtleastOne"}]}');

                this.totalPages = this.tableData.pageCount;
                let pageCount = [];
                for (var i = 1; i <= this.tableData.pageCount; i++) {
                    pageCount.push(i);
                }
                this.pageRenge = pageCount;
                this.pager = this.getPager(this.totalPages * this.pageSize, this.currentPage, this.pageRenge);
                // this.prompts = this.tableData.prompt.promptList;
                this.prompts = this.tableData.prompt ? this.tableData.prompt.promptList : [];
                // To display table data
                if (this.tableData.cmdRuleBuilderData.length != 0) {
                    this.showNoDataFound = false;
                    this.tableShowHide = false;
                    this.shellTableData = true;
                    this.ciqShellOrXmlBlock = false;
                    setTimeout(() => {
                        let tableWidth = document.getElementById('melConfigDetails').scrollWidth;
                        $(".scrollBody table#removalDetailsData").css("min-width", (tableWidth) + "px");
                        $(".scrollHead table#melConfigDetails").css("width", tableWidth + "px");


                        $(".scrollBody").on("scroll", function (event) {
                            $("#removalDetailsData .formEditRow #formWrapper").css("margin-left", (event.target.scrollLeft - 5) + "px");
                            $(".shellRow .form-control-fixed").css("right", (event.target.scrollLeft * -1) + "px");
                            $(".scrollHead table#melConfigDetails").css("margin-left", (event.target.scrollLeft * -1) + "px");
                        });
                        $(".scrollBody").css("max-height", (this.tableDataHeight - 10) + "px");
                    }, 0);
                } else {
                    this.tableShowHide = false;
                    this.shellTableData = false;
                    this.ciqShellOrXmlBlock = false;
                    this.showNoDataFound = true;
                }
            }, 1000); */
            //Please Comment while checkIn
        });
}

    /* Used to add new Airline config details 
     * @param : event
     * @retun : null
     */
    createShellRule(event) {

        validator.performValidation(event, this.validationData, "save_update");
        let currentForm = event.target.parentNode.parentNode,
                    fileRuleFormData = {
                        "ruleName": currentForm.querySelector("#ruleName").value,
                        "cmdName": currentForm.querySelector("#cmdName").value,
                        "resultType": currentForm.querySelector("#resultType").value,
                        "operand1Values": currentForm.querySelector("#operand1Values").value,
                        "operand1ColumnNames": currentForm.querySelector("#operand1ColumnNames").value,
                        "operator": currentForm.querySelector("#operator")? currentForm.querySelector("#operator").value : "",
                        "operand2Values": currentForm.querySelector("#operand2Values") ? currentForm.querySelector("#operand2Values").value : "",
                        "operand2ColumnNames": currentForm.querySelector("#operand2ColumnNames") ? currentForm.querySelector("#operand2ColumnNames").value : "",
                        "prompt":currentForm.querySelector("#prompt").value,
                        "loopType":currentForm.querySelector("#loopType").value,
                        "status": currentForm.querySelector("#status").value,
                        "remarks": currentForm.querySelector("#remarks").value
                    };
        this.checkValidation(fileRuleFormData, "Shell");
        setTimeout(() => {

            if (this.isValidForm(event)) {

                this.showLoader = true;
                /* let currentForm = event.target.parentNode.parentNode,
                    fileRuleFormData = {
                        "ruleName": currentForm.querySelector("#ruleName").value,
                        "cmdName": currentForm.querySelector("#cmdName").value,
                        "resultType": currentForm.querySelector("#resultType").value,
                        "operand1Values": currentForm.querySelector("#operand1Values").value,
                        "operand1ColumnNames": currentForm.querySelector("#operand1ColumnNames").value,
                        "operator": currentForm.querySelector("#operator").value,
                        "operand2Values": currentForm.querySelector("#operand2Values").value,
                        "operand2ColumnNames": currentForm.querySelector("#operand2ColumnNames").value,
                        "prompt":currentForm.querySelector("#prompt").value,
                        "loopType":currentForm.querySelector("#loopType").value,
                        "status": currentForm.querySelector("#status").value,
                        "remarks": currentForm.querySelector("#remarks").value
                    }; */
                this.cmdrulebuilderService.createShellRule(this.migrationType, this.subType, fileRuleFormData, this.sharedService.createServiceToken())
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
                                        //this.searchTabBind();
                                        setTimeout(() => {
                                            this.showLoader = false;
                                            this.message = "Shell Command Rule created successfully!";
                                            this.shellSuccessModalBlock = this.modalService.open(this.shellSuccessModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });                                            
                                            this.searchStatus = "load";
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
                            /* setTimeout(() => {
              
                              this.showLoader = false;
              
                              let jsonStatue = { "sessionId": "e9004f23", "reason": null, "status": "SUCCESS", "serviceToken": "64438" };
              
                              if (jsonStatue.status == "SUCCESS") {
                                //this.searchTabBind();
                                setTimeout(() => {
                                    this.showLoader = false;
                                    this.message = "Shell Command Rule created successfully!";
                                    this.shellSuccessModalBlock = this.modalService.open(this.shellSuccessModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });                                            
                                    this.searchStatus = "load";
                                }, 1000);
              
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

    /*
     * Used to update airline config details
     * @param : event
     * @retun : null
     */
    updateShellCmdRuleDetails(event) {
        setTimeout(() => {
            $("#dataWrapper").find(".scrollBody").scrollLeft(0);
        }, 0);

        validator.performValidation(event, this.validationData, "save_update");
        let currentForm = event.target.parentNode.parentNode,
            fileRuleFormData = {
                "id": this.fileRuleRowId,
                "ruleName": currentForm.querySelector("#ruleName").value,
                "cmdName": currentForm.querySelector("#cmdName").value,
                "resultType": currentForm.querySelector("#resultType").value,
                "operand1Values": currentForm.querySelector("#operand1Values").value,
                "operand1ColumnNames": currentForm.querySelector("#operand1ColumnNames").value,
                "operator": currentForm.querySelector("#operator") ? currentForm.querySelector("#operator").value : "",
                "operand2Values": currentForm.querySelector("#operand2Values") ? currentForm.querySelector("#operand2Values").value : "",
                "operand2ColumnNames": currentForm.querySelector("#operand2ColumnNames") ? currentForm.querySelector("#operand2ColumnNames").value : "",
                "prompt":currentForm.querySelector("#prompt").value,
                "loopType":currentForm.querySelector("#loopType").value,
                "status": currentForm.querySelector("#status").value,
                "remarks": currentForm.querySelector("#remarks").value
            };
        this.checkValidation(fileRuleFormData, "Shell");
        setTimeout(() => {
            if (this.isValidForm(event)) {
                this.showLoader = true;

                /* let currentForm = event.target.parentNode.parentNode,
                    fileRuleFormData = {
                        "id": this.fileRuleRowId,
                        "ruleName": currentForm.querySelector("#ruleName").value,
                        "cmdName": currentForm.querySelector("#cmdName").value,
                        "resultType": currentForm.querySelector("#resultType").value,
                        "operand1Values": currentForm.querySelector("#operand1Values").value,
                        "operand1ColumnNames": currentForm.querySelector("#operand1ColumnNames").value,
                        "operator": currentForm.querySelector("#operator") ? currentForm.querySelector("#operator").value : "",
                        "operand2Values": currentForm.querySelector("#operand2Values") ? currentForm.querySelector("#operand2Values").value : "",
                        "operand2ColumnNames": currentForm.querySelector("#operand2ColumnNames") ? currentForm.querySelector("#operand2ColumnNames").value : "",
                        "prompt":currentForm.querySelector("#prompt").value,
                        "loopType":currentForm.querySelector("#loopType").value,
                        "status": currentForm.querySelector("#status").value,
                        "remarks": currentForm.querySelector("#remarks").value
                    }; */

                this.cmdrulebuilderService.updateShellCmdRuleDetails(this.migrationType, this.subType,fileRuleFormData, this.sharedService.createServiceToken())
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

                                        document.querySelector("#searchButton").classList.remove("buttonDisabled");
                                        this.message = "Shell Command Rule updated successfully!";
                                        this.shellSuccessModalBlock = this.modalService.open(this.shellSuccessModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                                        // Hide all the form/Table/Nodatafound
                                        this.tableShowHide = false;
                                        this.shellTableData = false;
                                        this.ciqShellOrXmlBlock = false;
                                        this.showNoDataFound = false;
                                        this.createNewForm = false;

                                        // Cancel current edit form
                                        this.cancelEditRow(fileRuleFormData.id, '');

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
                                document.querySelector("#searchButton").classList.remove("buttonDisabled");
                                this.showLoader = true;
                                setTimeout(() => {
                                    this.showLoader = false;
                                    this.message = "Shell Command Rule updated successfully!";
                                    this.shellSuccessModalBlock = this.modalService.open(this.shellSuccessModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                                    // Cancel current edit form
                                    this.cancelEditRow(fileRuleFormData.id, '');
                                }, 1000);
                            } else {
                                this.displayModel(jsonStatue.reason, "failureIcon");
                            } */
                            //Please Comment while checkIn
                        });
            }
        }, 0);
    }

    /*
     * On click delete button open a modal for confirmation for delete entire row
     * @param : content, userName
     * @retun : null
     */
    deleteShellRow(event, confirmModal, rowId) {

        let deleteState = event.target;

        if (deleteState.className != "deleteRowDisabled") {

            this.modalService.open(confirmModal, {
                keyboard: false,
                backdrop: 'static',
                size: 'lg',
                windowClass: 'confirm-modal'
            }).result.then((result) => {
                this.closeResult = `Closed with: ${result}`;
            }, (reason) => {

                this.showLoader = true;

                this.cmdrulebuilderService.deleteShellRow(rowId, this.sharedService.createServiceToken())
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
                                        this.message = "Shell Command Rule deleted successfully!";
                                        this.shellSuccessModalDelBlock = this.modalService.open(this.shellSuccessModalDelRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
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
                                  this.message = "Shell Command Rule deleted successfully!";
                                  this.shellSuccessModalDelBlock = this.modalService.open(this.shellSuccessModalDelRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                              }
                            }, 1000); */
                            //Please Comment while checkIn
                        });
            });
        }
    }

getAllXmlData() {
    $("#dataWrapper").find(".scrollBody").scrollLeft(0);
    this.showLoader = true;
    this.tableShowHide = false;
    this.shellTableData = false;
    this.ciqShellOrXmlBlock = true;
    this.searchBlock = false;
    this.createNewForm = false;
    this.shellSearchBlock = false;
    this.shellCreateBlock = false;
    this.xmlSearchBlock = true;
    this.xmlCreateBlock = false;
    this.ciqShellOrXml == 'xml';
    this.setMenuHighlight("search");
    this.cmdrulebuilderService.getAllXmlData(this.migrationType,this.subType, this.searchStatus, this.searchCriteria, this.paginationDetails, this.sharedService.createServiceToken()).subscribe(
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
                            let pageCount = [];
                            for (var i = 1; i <= this.tableData.pageCount; i++) {
                                pageCount.push(i);
                            }
                            this.pageRenge = pageCount;
                            this.pager = this.getPager(this.totalPages * this.pageSize, this.currentPage, this.pageRenge);
                            this.prompts = this.tableData.prompt ? this.tableData.prompt.promptList : [];

                            // To display table data
                            if (this.tableData.xmlRuleDetail.length != 0) {

                                this.showNoDataFound = false;;
                                this.ciqShellOrXmlBlock = true;
                                setTimeout(() => {
                                    let tableWidth = document.getElementById('melConfigDetails').scrollWidth;
                                    $(".scrollBody table#removalDetailsData").css("min-width", (tableWidth) + "px");
                                    $(".scrollHead table#melConfigDetails").css("width", tableWidth + "px");
            
            
                                    $(".scrollBody").on("scroll", function (event) {
                                        $("#removalDetailsData .formEditRow #formWrapper").css("margin-left", (event.target.scrollLeft - 5) + "px");
                                        $(".xmlRow .form-control-fixed").css("right", (event.target.scrollLeft * -1) + "px");
                                        $(".scrollHead table#melConfigDetails").css("margin-left", (event.target.scrollLeft * -1) + "px");
                                    });
                                    $(".scrollBody").css("max-height", (this.tableDataHeight - 10) + "px");
        
                                }, 0);
                            } else {
                                this.showNoDataFound = true;
                                this.ciqShellOrXmlBlock = false;
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

             /*this.showLoader = true;
            setTimeout(() => {

                this.showLoader = false;
                //Data
                //this.tableData = JSON.parse('{"sessionId":"7e088256","serviceToken":84933,"pageCount":4,"xmldata":[{"remarks":"remarks01","rootName":"rootName01","ruleName":"RC_1","subRootName":"subRootName01","id":1,"createdBy":"ADMIN","timeStamp":"21-02-2019"},{"remarks":"remarks02","rootName":"rootName02","ruleName":"RC_2","subRootName":"subRootName02","id":2,"createdBy":"USER","timeStamp":"22-02-2019"},{"remarks":"remarks03","rootName":"rootName03","ruleName":"RC_3","subRootName":"subRootName03","id":3,"createdBy":"ADMIN","timeStamp":"23-02-2019"}],"status":"Success"}');
                // this.tableData = JSON.parse('{"sessionId":"53b7e86","serviceToken":"63108","pageCount":1,"prompt":{"promptList":["[enbname]","[MSMA]","vsmuser@*LMD1","root@*LMD1","-MCMASTER","$","user@BLTSP02518:"]},"xmlRuleDetail":[{"id":"2","ruleName":"fdg","cmdName":"hello123","rootName":"fdsg","subRootName":"dsfg","loopType":"All","remarks":"sdfg","prompt":"$","status":"Warn","rootDetails":[{"rootId":"2","rootKey":"4","rootValue":"dsfg"}],"elementDetails":[],"createdBy":"superadmin","timeStamp":"2019-04-01 11:54:47","migrationType":null},{"id":"1","ruleName":"Test","cmdName":"hello123","rootName":"test","subRootName":"test","loopType":"AtleastOne","remarks":"","prompt":"$","status":"Warn","rootDetails":[{"rootId":"1","rootKey":"1","rootValue":"45"}],"elementDetails":[],"createdBy":"superadmin","timeStamp":"2019-04-01 11:49:47","migrationType":null}],"status":"SUCCESS"}');
                this.tableData = JSON.parse('{"pageCount":2,"sessionId":"a7afbe53","serviceToken":"83545","prompt":{"promptList":["[enbname]","[MSMA]","vsmuser@*LMD1","root@*LMD1","-MCMASTER","$","vsmuser@116_00_00_LMD1_0","user@BLTSP02518:","[eNB_712832] ","[eNB_enbId]","#","[enbname]","[enbId]","[MCMA0:/home/vsm]","user@user-OptiPlex-3046:","root@user-OptiPlex-9020:/home/user# "]},"xmlRuleDetail":[{"id":"54","ruleName":"ipv6_address_ip_get_type","rootName":"external-interfaces","subRootName":"ipv6-address","remarks":"","rootDetails":[],"elementDetails":[{"elementId":"97","elementName":"ip-get-type","operator":"CONTAINS","elementValue":"static"}],"createdBy":"superadmin","timeStamp":"2019-10-14 19:59:35","migrationType":null,"loopType":"All","cmdName":"show managed-element ip-system ip-interface external-interfaces ipv6-address | display xml","prompt":"vsmuser@116_00_00_LMD1_0","status":"Pass"},{"id":"65","ruleName":"ipv6_static_route_prefix","rootName":"ip-route","subRootName":"ipv6-static-route","remarks":"","rootDetails":[],"elementDetails":[{"elementId":"106","elementName":"prefix","operator":"CONTAINS","elementValue":"2404:180:1004:1:cf:500:3030"},{"elementId":"105","elementName":"prefix","operator":"CONTAINS","elementValue":"/128"}],"createdBy":"superadmin","timeStamp":"2019-10-14 19:58:49","migrationType":null,"loopType":"AtleastOne","cmdName":"show managed-element ip-system ip-route ipv6-route ipv6-static-route | display xml","prompt":"vsmuser@116_00_00_LMD1_0","status":"Pass"},{"id":"53","ruleName":"ipv6_address_prefix_length","rootName":"external-interfaces","subRootName":"ipv6-address","remarks":"","rootDetails":[],"elementDetails":[{"elementId":"96","elementName":"prefix-length","operator":"CONTAINS","elementValue":"64"}],"createdBy":"superadmin","timeStamp":"2019-10-14 16:38:42","migrationType":null,"loopType":"All","cmdName":"show managed-element ip-system ip-interface external-interfaces ipv6-address | display xml","prompt":"vsmuser@116_00_00_LMD1_0","status":"Pass"},{"id":"57","ruleName":"ipv6_addr_signal_s2_true_false","rootName":"external-interfaces","subRootName":"ipv6-address","remarks":"","rootDetails":[],"elementDetails":[{"elementId":"110","elementName":"signal-s2","operator":"CONTAINS","elementValue":"none"}],"createdBy":"superadmin","timeStamp":"2019-10-14 16:36:15","migrationType":null,"loopType":"AtleastOne","cmdName":"show managed-element ip-system ip-interface external-interfaces ipv6-address | display xml","prompt":"vsmuser@116_00_00_LMD1_0","status":"Fail"},{"id":"67","ruleName":"retrieve-ntp_status_primary","rootName":"ntp-status","subRootName":"","remarks":"","rootDetails":[],"elementDetails":[{"elementId":"108","elementName":"server-type","operator":"CONTAINS","elementValue":"primary-server"}],"createdBy":"superadmin","timeStamp":"2019-10-13 19:42:56","migrationType":null,"loopType":"AtleastOne","cmdName":"request retrieve-ntp-status | display xml","prompt":"vsmuser@116_00_00_LMD1_0","status":"Pass"},{"id":"68","ruleName":"retrieve-ntp_status_secondary","rootName":"ntp-status","subRootName":"","remarks":"","rootDetails":[],"elementDetails":[{"elementId":"109","elementName":"server-type","operator":"CONTAINS","elementValue":"secondary-server"}],"createdBy":"superadmin","timeStamp":"2019-10-13 19:42:25","migrationType":null,"loopType":"AtleastOne","cmdName":"request retrieve-ntp-status | display xml","prompt":"vsmuser@116_00_00_LMD1_0","status":"Pass"},{"id":"66","ruleName":"cell-fdd-tdd usage-state","rootName":"eutran-generic-cell","subRootName":"eutran-cell-fdd-tdd","remarks":"","rootDetails":[],"elementDetails":[{"elementId":"107","elementName":"usage-state","operator":"CONTAINS","elementValue":"active"}],"createdBy":"superadmin","timeStamp":"2019-10-13 19:35:46","migrationType":null,"loopType":"All","cmdName":"show managed-element enb-function eutran-generic-cell eutran-cell-fdd-tdd usage-state | display xml","prompt":"vsmuser@116_00_00_LMD1_0","status":"Pass"},{"id":"62","ruleName":"fdd_tdd_operational_state","rootName":"eutran-generic-cell","subRootName":"eutran-cell-fdd-tdd","remarks":"","rootDetails":[],"elementDetails":[{"elementId":"102","elementName":"operational-state","operator":"CONTAINS","elementValue":"enabled"}],"createdBy":"superadmin","timeStamp":"2019-10-13 19:31:10","migrationType":null,"loopType":"All","cmdName":"show managed-element enb-function eutran-generic-cell eutran-cell-fdd-tdd operational-state | display xml","prompt":"vsmuser@116_00_00_LMD1_0","status":"Pass"},{"id":"64","ruleName":"emergency-access-ue-count","rootName":"eutran-generic-cell","subRootName":"eutran-cell-fdd-tdd","remarks":"","rootDetails":[],"elementDetails":[{"elementId":"104","elementName":"emergency-access-ue-count","operator":"==","elementValue":"0"}],"createdBy":"superadmin","timeStamp":"2019-10-13 19:27:04","migrationType":null,"loopType":"All","cmdName":"show managed-element enb-function eutran-generic-cell eutran-cell-fdd-tdd cell-call-count-status | display xml","prompt":"vsmuser@116_00_00_LMD1_0","status":"Warn"},{"id":"63","ruleName":"active_ue_count","rootName":"eutran-generic-cell","subRootName":"eutran-cell-fdd-tdd","remarks":"","rootDetails":[],"elementDetails":[{"elementId":"103","elementName":"active-ue-count","operator":"ANY","elementValue":"0, 3"}],"createdBy":"superadmin","timeStamp":"2019-10-13 19:23:34","migrationType":null,"loopType":"All","cmdName":"show managed-element enb-function eutran-generic-cell eutran-cell-fdd-tdd cell-call-count-status | display xml","prompt":"vsmuser@116_00_00_LMD1_0","status":"Pass"}],"status":"SUCCESS"}');
                this.totalPages = this.tableData.pageCount;
                let pageCount = [];
                for (var i = 1; i <= this.tableData.pageCount; i++) {
                    pageCount.push(i);
                }
                this.pageRenge = pageCount;
                this.pager = this.getPager(this.totalPages * this.pageSize, this.currentPage, this.pageRenge);
                this.prompts = this.tableData.prompt ? this.tableData.prompt.promptList : [];


                // To display table data
                if (this.tableData.xmlRuleDetail.length != 0) {
                    this.showNoDataFound = false;
                    this.ciqShellOrXmlBlock = true;
                    setTimeout(() => {
                        let tableWidth = document.getElementById('melConfigDetails').scrollWidth;
                        $(".scrollBody table#removalDetailsData").css("min-width", (tableWidth) + "px");
                        $(".scrollHead table#melConfigDetails").css("width", tableWidth + "px");


                        $(" .scrollBody").on("scroll", function (event) {
                            $("#removalDetailsData .formEditRow #formWrapper").css("margin-left", (event.target.scrollLeft - 5) + "px");
                            $(".xmlRow .form-control-fixed").css("right", (event.target.scrollLeft * -1) + "px");
                            $(".scrollHead table#melConfigDetails").css("margin-left", (event.target.scrollLeft * -1) + "px");
                        });
                        $(".scrollBody").css("max-height", (this.tableDataHeight - 10) + "px");

                    }, 0);
                    
                } else {
                    this.showNoDataFound = true;
                    this.ciqShellOrXmlBlock = false;
                }

            }, 1000); */
            //Please Comment while checkIn


        });
}

editRootRow(event, key, index) {
    this.currActiveTab ='tab-element';
    this.rootAndElementTabs= true;
        this.fileRootRowId = key.id;
        let editState: any = event.target;
        $(".editRowDisabled").attr("class", "editRow");
        $(".deleteRowDisabled").attr("class","deleteRow");
        if (editState.className != "editRowDisabled") { //enable click only if it is enabled

            if (!document.querySelector("#xmlSearchButton").classList.contains('buttonDisabled')) {
                // Disable search button while editing 
                document.querySelector("#xmlSearchButton").classList.add("buttonDisabled");
                this.paginationDisabbled = true;
            }

            editState.className = "editRowDisabled";
            $(editState).next().attr("class", "deleteRowDisabled") // Disable delete on edit row        

            /* // If any edit form is opend then close the form and enable edit button
            if (this.currentEditRow != undefined) {
                this.currentEditRow.className = "editRow";
            }

            this.currentEditRow = event.target; */

            // To enable one edit form at a time in table
            if (this.editableRootFormArray.length >= 1) {
                this.editableRootFormArray = [];
                this.editableRootFormArray.push(index);
            } else {
                this.editableRootFormArray.push(index);
            }

            this.sharedService.userNavigation = false; //block user navigation

            setTimeout(() => {
                //this.editRootRowTable(event, key, index);

                this.formWidth = this.element.nativeElement.querySelector('#tableWrapper').clientWidth - 30 + "px";
                this.scrollLeft = this.element.nativeElement.querySelector('.scrollBody').scrollLeft + "px";
                //map validation for fields
                $('.formEditRow #xmlRuleName').val(key.ruleName);
                $('.formEditRow #cmdName').val(key.cmdName);
                $('.formEditRow #xmlRootName').val(key.rootName);
                $('.formEditRow #xmlSubRootName').val(key.subRootName);
                $('.formEditRow #loopType').val(key.loopType);
                $('.formEditRow #prompt').val(key.prompt);
                $('.formEditRow #status').val(key.status);
                $('.formEditRow #xmlRemarks').val(key.remarks);
                $('.formEditRow #xmlCreatedBy').val(key.createdBy);
                this.rootAndElementData.rootDetailsData = key.rootDetails;
                this.rootAndElementData.elementsDetailsData = key.elementDetails;               
                
                validator.performValidation(event, this.validationData, "edit");
            }, 100);
            
        }

}

editRootOrElem(event, key, index, cmdDetails) {

    let errorMsg = "edit is in progress. Please save or cancel the current editing row.";
    if (cmdDetails == 'root') {
        if (!this.checkRowInEditMode(cmdDetails)) {
            //Save old data to use when canceled
            this.currEditRoot = JSON.parse(JSON.stringify(this.rootAndElementData.rootDetailsData[index]));  //Making deep copy
            key.inRootEditMode = true;
        }
        else {
            this.displayModel("File Rule " + errorMsg, "failureIcon");
        }
    } else if (cmdDetails == 'element') {
        if (!this.checkRowInEditMode(cmdDetails)) {
            //Save old data to use when canceled
            this.currEditElement = JSON.parse(JSON.stringify(this.rootAndElementData.elementsDetailsData[index]));  //Making deep copy              
            key.inElementEditMode = true;
        }
        else {
            this.displayModel("CLI/Shell " + errorMsg, "failureIcon");
        }
    }
}

checkRowInEditMode(cmdDetails) {
    let isInEditMode = false;
    if (cmdDetails == 'root') {            
        for (let i = 0; i < this.rootAndElementData.rootDetailsData.length; i++) {
            if (this.rootAndElementData.rootDetailsData[i].inRootEditMode == true) {
                isInEditMode = true;
                break;
            }
        }
    }
    else if(cmdDetails == 'element') {
        for (let i = 0; i < this.rootAndElementData.elementsDetailsData.length; i++) {
            if (this.rootAndElementData.elementsDetailsData[i].inxmlEditMode == true) {
                isInEditMode = true;
                break;
            }
        }
    }
    return isInEditMode;
}

deleteRootOrElem(confirmModal, key, index,ruleName) {
    console.log(ruleName);
     this.modalService.open(confirmModal, {keyboard: false,backdrop: 'static',size: 'lg',windowClass: 'confirm-modal'}).result.then((result) => {
         this.closeResult = `Closed with: ${result}`;
     }, (reason) => {
         if (ruleName == 'root') {
             this.rootAndElementData.rootDetailsData.splice(index, 1);
             if (this.rootAndElementData.rootDetailsData.length == 0) {
                this.rootAndElementData.rootDetailsData[0] = {
                          "rootKey": "",
                          "rootValue": "",
                          "rootId": "",
                          "inRootEditMode": true
                }
            }
         } else if (ruleName == 'element') {
             this.rootAndElementData.elementsDetailsData.splice(index, 1);
             if (this.rootAndElementData.elementsDetailsData.length == 0) {
                this.rootAndElementData.elementsDetailsData[0] = {
                    "elementName": "",
                    "operator": "",
                    "elementValue": "",
                    "elementId": "",
                    "inElementEditMode": true
                }
            }
         }   
     });
    
 }


 addRootOrElementDetails(event) {
     if(this.currActiveTab == 'tab-element') {
        let tempObj = 
        {
          "rootKey": "",
          "rootValue": "",
          "rootId": null,
          "inRootEditMode": true        
      
        };
        this.rootAndElementData['rootDetailsData'].push(tempObj);
     }else{
        let tempObj1 = {
            "elementName": "",
            "operator": "",
            "elementValue": "",
            "elementId": null,
            "inElementEditMode": true
        };
        this.rootAndElementData['elementsDetailsData'].push(tempObj1);
     }
    
    
 }

 rootElementJsonData() {
    this.rootAndElementData = 
    {
        "rootDetailsData": [
            {
            "rootKey": "",
            "rootValue": "",
            "rootId": null,
            "inRootEditMode": true
            }
        ], 
        "elementsDetailsData": [
            {
            "elementName": "",
            "operator": "",
            "elementValue": "",
            "elementId": null,
            "inElementEditMode": true
            }
        ]
    }
 }

 saveRootRow(event, key, index1) {
    let validations = {
        "rules": {},
        "messages": {}
    };
    validations.rules["tableRootKey_" + [index1]] = { "required": true };
    validations.rules["tableRootValue_" + [index1]] = { "required": true};  
    validations.messages["tableRootKey_" + [index1]] = { "required": "Root Key is required" };
    validations.messages["tableRootValue_" + [index1]] = { "required": "Root Value is required" };
   
    validator.performValidation(event, validations, "save_update");
    console.log(this.currEditRoot);
    setTimeout(() => {
        if (this.isValidRootRowForm(event)) {                
            key.inRootEditMode = false;
            this.currEditRoot = null;          
        }
    }, 1000);
}

saveElementRow(event, key, index2) {
    let validations = {
        "rules": {},
        "messages": {}
    };
    
    // validations.rules["tableElementName_" + [index2]] = { "required": true };
    validations.rules["tableOperator_" + [index2]] = { "required": true};
    //validations.rules["tableElementValue_" + [index2]] = { "required": true}; 
    
    if(key.operator === "TEMPLATE"){ 
      var flagElement = false,temp;      
        try {
            temp = JSON.parse(key.elementValue);
        } catch(e) {
            flagElement = true // error in the above string (in this case, yes)!
        }  

        if(!flagElement && toString.call(temp) === '[object Object]'){
            validations.rules["tableElementValue_" + [index2]] = {"required": true,"customfunction":false}; 
            validations.messages["tableElementValue_" + [index2]] = { "required": "Element Value is required","customfunction" :"Element Value should be JSON" };
        }else{
            validations.rules["tableElementValue_" + [index2]] = {"required": true,"customfunction":true}; 
            validations.messages["tableElementValue_" + [index2]] = { "required": "Element Value is required","customfunction" :"Element Value should be JSON" };
        }
    }else {        
         validations.rules["tableElementValue_" + [index2]] = {"required": true,}; 
         validations.messages["tableElementValue_" + [index2]] = { "required": "Element Value is required"};        
    }

    // validations.messages["tableElementName_" + [index2]] = { "required": "Element Name is required" };
    validations.messages["tableOperator_" + [index2]] = { "required": "Operator is required" };   
  
   
    validator.performValidation(event, validations, "save_update");
    
    setTimeout(() => {
        if (this.isValidElementRowForm(event)) {                
            key.inElementEditMode = false;
            this.currEditElement = null;          
        }
    }, 1000);
}

isValidRootRowForm(event) {
    return ($(event.target).parents("form#rootDetailsTab").find(".error-border").length == 0) ? true : false;
}

isValidElementRowForm(event) {
    return ($(event.target).parents("form#elementDetails").find(".error-border").length == 0) ? true : false;
}  

cancelElemRow(event, key, index) { 
    key.inElementEditMode = false;        
    if (this.currEditElement) {            
        this.rootAndElementData.elementsDetailsData[index] = this.currEditElement;
        this.currEditElement = null;         
    }
    else {
        this.rootAndElementData.elementsDetailsData.splice(index, 1);
        // If there is no row added to script list, add an empty row
        if (this.rootAndElementData.elementsDetailsData.length == 0) {
            this.rootAndElementData.elementsDetailsData[0] = {
                "elementName": "",
                "operator": "",
                "elementValue": "",
                "elementId": "",
                "inElementEditMode": true
            }
        }          
    }
}

cancelRootRow(event, key, index) { 
    key.inRootEditMode = false;        
    if (this.currEditRoot) {            
        this.rootAndElementData.rootDetailsData[index] = this.currEditRoot;
        this.currEditRoot = null;         
    }
    else {
        this.rootAndElementData.rootDetailsData.splice(index, 1);
        // If there is no row added to script list, add an empty row
        if (this.rootAndElementData.rootDetailsData.length == 0) {
            this.rootAndElementData.rootDetailsData[0] = {
                      "rootKey": "",
                      "rootValue": "",
                      "rootId": "",
                      "inRootEditMode": true
            }
        }          
    }
}

saveRootAndElementData(event,index) { 

    if(this.rootAndElementData.rootDetailsData.length >0 ){
        for(let i=0; i< this.rootAndElementData.rootDetailsData.length; i++){
            if(this.rootAndElementData.rootDetailsData[i].inRootEditMode){
                this.rootAndElementData.rootDetailsData.splice(i,1);
            }
        }
        this.rootAndElementData.rootDetailsData = this.rootAndElementData.rootDetailsData; // Sending fileRules data if exists
    } else {                    
        this.rootAndElementData.rootDetailsData = []; // Sending Empty array if no rules is added
    }

    if(this.rootAndElementData.elementsDetailsData.length >0 ){
        for(let i=0; i< this.rootAndElementData.elementsDetailsData.length; i++){
            if(this.rootAndElementData.elementsDetailsData[i].inElementEditMode){
                this.rootAndElementData.elementsDetailsData.splice(i,1);
            }
        }
        this.rootAndElementData.elementsDetailsData = this.rootAndElementData.elementsDetailsData; // Sending fileRules data if exists
    } else {                    
        this.rootAndElementData.elementsDetailsData = []; // Sending Empty array if no rules is added
    }              
            
            this.rootFinalData.push(this.rootAndElementData.rootDetailsData);
            this.elementFinalData.push(this.rootAndElementData.elementsDetailsData);
 
    validator.performValidation(event, this.validationData, "save_update");
    setTimeout(() => {
        if (this.isValidForm(event)) {                
            this.showLoader = true;
            let cmdRuleXmlCreateData = {                        
                    "ruleName": $("#xmlRuleName").val(),
                    "cmdName": $("#cmdName").val(),
                    "rootName": $("#xmlRootName").val(),
                    "subRootName": $("#xmlSubRootName").val(),
                    "loopType": $("#loopType").val(),
                    "prompt":$("#prompt").val(),
                    "status":$("#status").val(),
                    "remarks": $("#xmlRemarks").val(),
                    "rootDetails": this.rootAndElementData.rootDetailsData,
                    "elementDetails":this.rootAndElementData.elementsDetailsData
                };
            this.cmdrulebuilderService.saveRootAndElementData(cmdRuleXmlCreateData, this.sharedService.createServiceToken(),this.migrationType,this.subType)
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
                                    this.searchTabBind();
                                    setTimeout(() => {
                                        this.showLoader = false;
                                        //this.displayModel("File Rule created successfully !", "successIcon");
                                        this.message = "XML Command Rule created successfully!";
                                        this.xmlSuccessModalBlock = this.modalService.open(this.xmlSuccessModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                                        // Hide all the form/Table/Nodatafound
                                        this.searchStatus = "load";
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
                         /*setTimeout(() => {
                            this.showLoader = false;

                            // let jsonStatue = { "sessionId": "e9004f23", "reason": null, "status": "SUCCESS", "serviceToken": "64438" };
                            let jsonStatue = {"reason":"Command Rule created successfully","sessionId":"837ec4f1","serviceToken":"96888","ram":"SUCCESS","status":"SUCCESS"};
                            if (jsonStatue.status == "SUCCESS") {
                                this.searchTabBind();
                                setTimeout(() => {
                                    this.showLoader = false;
                                    // this.displayModel("File Rule created successfully !", "successIcon");
                                    this.message = "XML Command Rule created successfully!";
                                    this.xmlSuccessModalBlock = this.modalService.open(this.xmlSuccessModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                                    // Hide all the form/Table/Nodatafound
                                    this.searchStatus = "load";
                                }, 100);
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
 cancelCmdRule(event) {
    this.rootElementJsonData();
    this.searchTabBind();
    this.rootAndElementTabs = false;
 }

 isDisabledClass(useCount = 0) {
    let retClass = "";
    if(this.currActiveTab == 'tab-element'){
        this.enableAddBtn = true;
        if (useCount == 0) {
        for (let i = 0; i < this.rootAndElementData.rootDetailsData.length; i++) {                
            if (this.rootAndElementData.rootDetailsData[i].inRootEditMode) {                      
                retClass = "buttonDisabled";  
                this.enableAddBtn = false;                  
                break;
            }           
        }            
        } else {
            retClass = "buttonDisabled";          
        }
        return retClass;
    } else {
        this.enableAddBtn = true;
        if (useCount == 0) {
        for (let i = 0; i < this.rootAndElementData.elementsDetailsData.length; i++) {                
            if (this.rootAndElementData.elementsDetailsData[i].inElementEditMode) {                      
                retClass = "buttonDisabled";  
                this.enableAddBtn = false;                  
                break;
            }           
        }            
        } else {
            retClass = "buttonDisabled";          
        }
        return retClass;
    }
    
    
}

  onTabChange (event) {
      this.currActiveTab = event.activeId;
      if (this.rootAndElementData.elementsDetailsData.length == 0) {
        this.rootAndElementData.elementsDetailsData[0] = {
            "elementName": "",
            "operator": "",
            "elementValue": "",
            "elementId": "",
            "inElementEditMode": true
        }
    }   

    if (this.rootAndElementData.rootDetailsData.length == 0) {
        this.rootAndElementData.rootDetailsData[0] = {
                  "rootKey": "",
                  "rootValue": "",
                  "rootId": "",
                  "inRootEditMode": true
        }
    }

    if(this.currActiveTab == 'tab-root') {
        this.rootDetailsContainer.nativeElement.reset();
    } else {
        this.elementsDetailsContainer.nativeElement.reset();
    }
  }

  checkValidation(fileRuleFormData, type = '') {
    let allOpsEmpty: boolean = false;
    // if any one of Op1 Column or Op1 value are filled other one is required
    if(fileRuleFormData.operand1Values || fileRuleFormData.operand1ColumnNames) {
        this.validationData.rules.operand1Values.required = true;
        this.validationData.rules.operand1ColumnNames.required = true;
        allOpsEmpty = false;
        if(fileRuleFormData.operand1Values.split(this.delimiter).length == fileRuleFormData.operand1ColumnNames.split(this.delimiter).length) {
            //No. of items are same
            this.validationData.rules.operand1Values.customfunction = false;
        }
        else {
            if(type == "Shell" && fileRuleFormData.resultType == "Line") {
                this.validationData.rules.operand1Values.customfunction = false;
            }
            else {
                this.validationData.rules.operand1Values.customfunction = true;
            }
        }
    }
    else {
        this.validationData.rules.operand1Values.required = false;
        this.validationData.rules.operand1Values.customfunction = false;
        this.validationData.rules.operand1ColumnNames.required = false;
        allOpsEmpty = true;
    }

    // if any of Op2 Column, Op2 value or Operator are filled other fields are required
    if(fileRuleFormData.operand2Values || fileRuleFormData.operand2ColumnNames || fileRuleFormData.operator) {
        this.validationData.rules.operand2Values.required = true;
        this.validationData.rules.operand2ColumnNames.required = true;
        this.validationData.rules.operator.required = true;
        allOpsEmpty = false;
        if(fileRuleFormData.operand2Values.split(this.delimiter).length == fileRuleFormData.operand2ColumnNames.split(this.delimiter).length) {
            //No. of items are same
            this.validationData.rules.operand2Values.customfunction = false;
        }
        else {
            this.validationData.rules.operand2Values.customfunction = true;
        }
    }
    else {
        this.validationData.rules.operand2Values.required = false;
        this.validationData.rules.operand2Values.customfunction = false;
        this.validationData.rules.operand2ColumnNames.required = false;
        this.validationData.rules.operator.required = false;
        allOpsEmpty = allOpsEmpty && true;
    }

    // Atlease one set of Op1 or Op2 fields are required
    if(allOpsEmpty) {
        this.validationData.rules.operand1Values.required = true;
        this.validationData.rules.operand1ColumnNames.required = true;
        this.validationData.rules.operand2Values.required = true;
        this.validationData.rules.operand2ColumnNames.required = true;
        this.validationData.rules.operator.required = true;
    }

    // Number of item in column should be equal to number of values
    /* if(fileRuleFormData.operand1Values || fileRuleFormData.operand1ColumnNames) {
        if(fileRuleFormData.operand1Values.split(",").length == fileRuleFormData.operand1ColumnNames.split(",").length) {
            //No. of items are same
        }
        else {

        }
    } */

}

}


