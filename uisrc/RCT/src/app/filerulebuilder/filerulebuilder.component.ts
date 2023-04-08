import { ViewContainerRef, Input, ElementRef, Renderer, Component, OnInit, ViewChild, HostListener } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { NgbModal, ModalDismissReasons } from '@ng-bootstrap/ng-bootstrap';
import { PregrowService } from '../services/pregrow.service';
import { SharedService } from '../services/shared.service';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { filter, map, mergeMap } from 'rxjs/operators';
import { validator } from '../validation';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { FilerulebuilderService } from '../services/filerulebuilder.service';
//import * as FileSaver from 'file-saver';
import * as _ from 'underscore';
import * as $ from 'jquery';

@Component({
    selector: 'app-filerulebuilder',
    templateUrl: './filerulebuilder.component.html',
    styleUrls: ['./filerulebuilder.component.scss'],
    providers: [FilerulebuilderService]
})

export class FilerulebuilderComponent implements OnInit {
    tableData: any;
    showNoDataFound: boolean;
    tableShowHide: boolean;
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
    migrationType: string ="premigration";
    subType:any;
    status:any = "";;
    operator:any = "";;
    searchCriteria: any;
    sessionExpiredModalBlock: any; // Helps to close/open the model window
    pager: any = {}; // pager Object
    message: any;
    successModalBlock: any;
    navigationSubscription: any;


    validationData: any = {
        "rules": {
            "ruleName": {
                "required": true,
                "minlength": 3
            },
            "fileName": {
                "required": true
            },
            "searchParameter": {
                "required": true
            },
            "status": {
                "required": true
            },
            "searchRuleName":{
                "required": false
            },
            "searchFileName": {
                "required": false
            },
            "searchSearchParameter": {
                "required": false
            },
            "searchStatus": {
                "required": false
            },
            "createdBy":{
                "required": false
            }

        },
        "messages": {
            "ruleName": {
                "required": "Rule Name is required",
                "minlength": "min length should not be lesser than 3 characters"
            },
            "fileName": {
                "required": "File Name is required"
            },
            "searchParameter": {
                "required": "Search Parameter is required"
            },
            "status": {
                "required": "Status is required"
            },
            "searchRuleName":{
                "required": "Rule Name is required"
            },
            "searchFileName": {
                "required": "File Name is required"
            },
            "searchSearchParameter": {
                "required": "Search Parameter is required"
            },
            "searchStatus": {
                "required": "Status is required"
            },
            "createdBy":{
                "required": "Created By is required"
            }
        }
    };

    @ViewChild('searchTab') searchTabRef;
    @ViewChild('createNewTab') createNewTabRef;
    @ViewChild('bluePrintForm') bluePrintFormRef;
    @ViewChild('searchForm') searchFormRef;
    @ViewChild('sessionExpiredModal') sessionExpiredModalRef: ElementRef;
    @ViewChild('successModal') successModalRef: ElementRef;
    constructor(
        private element: ElementRef,
        private renderer: Renderer,
        private router: Router,
        private modalService: NgbModal,
        private filerulebuilderService: FilerulebuilderService,
        private sharedService: SharedService
    ) {
        this.navigationSubscription = this.router.events.subscribe((e: any) => {
            // If it is a NavigationEnd event re-initalise the component
            if (e instanceof NavigationEnd) {
                console.log("Constructor : " + e);
                this.ngOnInit();
            }
        });
    }

    ngOnInit() {
        this.tableDataHeight = (($("#contentWrapper").height()) - ($("#container").height() + $(".mainHead").height() + $(".nav").height() + 50));
        this.searchBlock = true;
        this.createNewForm = false;
        this.searchStatus = 'load';
        this.showLoader = true;
        this.setMenuHighlight("search");
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
        // Get all Airline Config Details on page start

        this.getAllFileRules();

        this.resetSearchForm();

    }

    ngOnDestroy() {
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

        this.getAllFileRules();
        // Close if edit form is in open state
        if (this.currentEditRow != undefined) {
            this.currentEditRow.className = "editRow";
        }
        this.editableFormArray = [];
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
        this.searchBlock = false;
        this.createNewForm = true;
        this.setMenuHighlight("createNew");
        setTimeout(() => {
            validator.performValidation(event, this.validationData, "create");
        }, 10);
    }

    /*
     * Used to dispaly search result based on selected criteria
     * @param : event
     * @retun : null
     */

    searchFileRules(event) {
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

                    let currentForm = event.target.parentNode.parentNode.parentNode,
                        searchCrtra = {    
                            "ruleName": currentForm.querySelector("#searchRuleName").value,
                            "fileName": currentForm.querySelector("#searchFileName").value,
                            "searchParameter": currentForm.querySelector("#searchSearchParameter").value,
                            "status": currentForm.querySelector("#searchStatus").value,
                            "createdBy": currentForm.querySelector("#createdBy").value
                        };
                    if (searchCrtra.ruleName || searchCrtra.fileName || searchCrtra.searchParameter || searchCrtra.status || searchCrtra.createdBy) {
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
                    this.getAllFileRules();
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


    /*
     * Used to display the table data on load/by default
     * @param : repairStation,userName,reflect (edit/delete)
     * @retun : null
     */

    getAllFileRules() {
        this.showLoader = true;
        this.filerulebuilderService.getAllFileRules(this.migrationType,this.subType, this.searchStatus, this.searchCriteria, this.paginationDetails, this.sharedService.createServiceToken()).subscribe(
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

                                // To display table data
                                if (this.tableData.fileRuleBuilderData.length != 0) {

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
                    //   this.tableData = JSON.parse('{"sessionId":"7e088256","serviceToken":84933,"pageCount":4,"fileRuleBuilderData":[],"status":"Success"}');
                    //Data
                    this.tableData = JSON.parse('{"sessionId":"7e088256","serviceToken":84933,"pageCount":4,"fileRuleBuilderData":[{"status":"Pass","searchParameter":"searchParameter01","fileName":"fileName01","ruleName":"RC_2","id":1,"createdBy":"ADMIN","timeStamp":"21-02-2019","remarks":"Hello wrold"},{"status":"Fail","searchParameter":"searchParameter02","fileName":"fileName02","ruleName":"RC_2","id":2,"createdBy":"User","timeStamp":"22-02-2019","remarks":"Android Pie is sweet!"},{"status":"Pass","searchParameter":"searchParameter03","fileName":"fileName03","ruleName":"RC_3","id":3,"createdBy":"ADMIN","timeStamp":"23-02-2019","remarks":"Apple is sweet!"}],"status":"Success"}');

                    this.totalPages = this.tableData.pageCount;
                    let pageCount = [];
                    for (var i = 1; i <= this.tableData.pageCount; i++) {
                        pageCount.push(i);
                    }
                    this.pageRenge = pageCount;
                    this.pager = this.getPager(this.totalPages * this.pageSize, this.currentPage, this.pageRenge);


                    // To display table data
                    if (this.tableData.fileRuleBuilderData.length != 0) {
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
                        this.tableShowHide = false;
                        this.showNoDataFound = true;
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
    createFileRule(event) {
        let attachedFileName = "";

        validator.performValidation(event, this.validationData, "save_update");
        setTimeout(() => {

            if (this.isValidForm(event)) {

                this.showLoader = true;
                let currentForm = event.target.parentNode.parentNode,
                    fileRuleFormData = {
                        "ruleName": currentForm.querySelector("#ruleName").value,
                        "fileName": currentForm.querySelector("#fileName").value,                        
                        "searchParameter": currentForm.querySelector("#searchParameter").value,
                        "status": currentForm.querySelector("#status").value,
                        "remarks": currentForm.querySelector("#remarks").value
                    };
                this.filerulebuilderService.createFileRule(this.migrationType, this.subType, fileRuleFormData, this.sharedService.createServiceToken())
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
                                            this.displayModel("File Rule created successfully!","successIcon");
                                            /* this.message = "File Rule created successfully!";
                                            this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' }); */
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

                           /*  setTimeout(() => {
              
                              this.showLoader = false;
              
                              let jsonStatue = { "sessionId": "e9004f23", "reason": null, "status": "SUCCESS", "serviceToken": "64438" };
              
                              if (jsonStatue.status == "SUCCESS") {
                                this.searchTabBind();
                                setTimeout(() => {
                                  this.showLoader = false;
                                  this.displayModel("File Rule created successfully!","successIcon");
                                  //this.message = "File Rule created successfully!";
                                  //this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
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




    /*
     * on click of edit row create a blueprint and append next to the current row
     * @param : current row event , current row json object and row index
     * @retun : null
     */

    editRow(event, key, index) {
        console.log(key);
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

        /*     // If any edit form is opend then close the form and enable edit button
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

        currentEditedForm.lastElementChild.lastElementChild.children[0].className = "editRow";


        // Enable search button 
        document.querySelector("#searchButton").classList.remove("buttonDisabled");
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

                this.filerulebuilderService.deleteFileRuleDeta(rowId, this.sharedService.createServiceToken())
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
                                        this.message = "File Rule deleted successfully!";
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
                                  this.message = "File Rule deleted successfully!";
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
    updateFileRuleDetails(event) {
        setTimeout(() => {
            $("#dataWrapper").find(".scrollBody").scrollLeft(0);
        }, 0);

        validator.performValidation(event, this.validationData, "save_update");
        setTimeout(() => {
            if (this.isValidForm(event)) {
                this.showLoader = true;

                let currentForm = event.target.parentNode.parentNode,
                    fileRuleFormData = {
                        "id": this.fileRuleRowId,
                        "ruleName": currentForm.querySelector("#ruleName").value,
                        "fileName": currentForm.querySelector("#fileName").value,                        
                        "searchParameter": currentForm.querySelector("#searchParameter").value,
                        "status": currentForm.querySelector("#status").value,
                        "createdBy": currentForm.querySelector("#createdBy").value,
                        "timeStamp": currentForm.querySelector("#timeStamp").value,
                        "remarks": currentForm.querySelector("#remarks").value

                    };

                this.filerulebuilderService.updateFileRuleDetails(this.migrationType, this.subType,fileRuleFormData, this.sharedService.createServiceToken())
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
                                        this.message = "File Rule updated successfully!";
                                        this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                                        // Hide all the form/Table/Nodatafound
                                        this.tableShowHide = false;
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
                                this.message = "File Rule updated successfully!";
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
            this.getAllFileRules();


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
            this.getAllFileRules();
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

/* Call onLoadService on change of Migration Type Radio Btn */
onChangeLoad(value){
    this.migrationType = value;
    if(this.migrationType == 'migration'){
        this.subType ="PRECHECK";
    }else if (this.migrationType =='postmigration'){
        this.subType ="AUDIT";
    }else{
        this.subType ="";
    }
    if(this.createNewForm){ 
        $('#ruleName').val('');
        $('#fileName').val('');
        $('#searchParameter').val('');
        $('#status').val('');
    }else if(this.searchBlock){
        $('#searchRuleName').val('');
        $('#searchFileName').val('');
        $('#searchSearchParameter').val('');
        $('#searchStatus').val('');
        $('#createdBy').val('');
    }
    this.getAllFileRules();
}
/* Call onLoadService on change of SubType */
onChangeType(value){
   
    this.subType = value;
    if(this.createNewForm){
        this.bluePrintFormRef.resetForm();
    }
    this.getAllFileRules();
}




}




