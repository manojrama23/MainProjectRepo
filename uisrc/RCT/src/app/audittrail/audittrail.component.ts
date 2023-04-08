import { ViewContainerRef, Input, ElementRef, Renderer, Component, OnInit, ViewChild, HostListener } from '@angular/core';
import { Router} from '@angular/router';
import { NgbModal, ModalDismissReasons} from '@ng-bootstrap/ng-bootstrap';
import { AudittrailService } from '../services/audittrail.service';
import { SharedService } from '../services/shared.service';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { filter, map, mergeMap} from 'rxjs/operators';
import { validator } from '../validation';
import {platformBrowserDynamic} from '@angular/platform-browser-dynamic';

import * as $ from 'jquery';
import * as _ from 'underscore';
@Component({
  selector: 'rct-audittrail',
  templateUrl: './audittrail.component.html',
  styleUrls: ['./audittrail.component.scss'],
  providers: [AudittrailService]
})
  export class AudittrailComponent implements OnInit {
  showLoader:boolean = true;
  showNoDataFound: boolean;
  tableData:any;
  closeResult:string;
  noDataVisibility :boolean = false;
  showModelMessage: boolean = false;
  scrollLeft:any;
  messageType: any;
  modelData :any;
  sessionExpiredModalBlock : any; // Helps to close/open the model window
  successModalBlock : any;
  message : any;
  tableShowHide :boolean = false;
  tableDataHeight:any;
  searchStatus: string;
  searchCriteria: any;
  currentPage: any; // for pagination
  viewType: any; // for pagination
  pageSize: any; // for pagination
  totalPages: any; // for pagination
  TableRowLength: any; // for pagination
  paginationDetails: any; // for pagination
  pageRenge: any; // for pagination
  paginationDisabbled: boolean = false;
  pager: any = {}; // pager Object
  eventNameList:any;
  getEventSubNameListData:any;
  getActionNameListData:any;
  getUserNameListData:any;
  getEventSubNameList = [];
  getActionNameList = [];
  getUserNameList = [];
  max = new Date();
  fromDate:any;
  fromDt:any;
  toDate:any;
  
  validationData : any = {
            "rules": {
                "toDate": {
                  "customfunction": false 
                }
            },
            "messages": {
                "toDate": {
                  "customfunction": "TO DATE should be greater than <br>FROM DATE"
                }
            }
          };
  
  // What to clone
  @ViewChild('clone') template;

  // Where to insert the cloned content
  @ViewChild('container', {read:ViewContainerRef}) container;


  @ViewChild('confirmModal') confirmModalRef: ElementRef;
  @ViewChild('sessionExpiredModal') sessionExpiredModalRef: ElementRef;
  @ViewChild('successModal') successModalRef: ElementRef;
  @ViewChild('searchForm') searchForm;


  
  constructor(
    private element: ElementRef,
    private renderer: Renderer,
    private router:Router,
    private modalService: NgbModal,
    private audittrailService: AudittrailService,
    private sharedService: SharedService
    ) {}

  ngOnInit() {
    this.fromDate = "";
    this.showLoader = true;
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
    this.searchStatus = "load";
    this.searchForm.nativeElement.reset();
    this.getAllauditTrailDetails();
    
  }



  getAllauditTrailDetails(){
    this.showLoader = true;
    this.tableShowHide = false;
    this.audittrailService.getAuditTrailDetails(this.searchCriteria, this.searchStatus, this.paginationDetails, this.sharedService.createServiceToken() )
        .subscribe(
            data => {
                setTimeout(() => { 
                  let jsonStatue = data.json();

                  this.tableData = data.json();
                  this.showLoader = false;
                  
                  
                      if(jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User"){
                     
                       this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                     
                      } else {

                        if( this.sharedService.isvalidServiceToken( parseInt(data.json().serviceToken,10) ) || data.json().sessionId == "408"){
                          if(jsonStatue.status == "SUCCESS"){
                            this.tableData = jsonStatue;
                            this.totalPages = this.tableData.pageCount;
                            let pageCount = [];
                            for (var i = 1; i <= this.tableData.pageCount; i++) {
                                pageCount.push(i);
                            }
                            this.pageRenge = pageCount;
                            this.pager = this.getPager(this.totalPages*this.pageSize, this.currentPage, this.pageRenge);

                            this.eventNameList = this.tableData.eventNameList;
                            if(this.tableData.auditTrailDetails.length == 0){
                              this.tableShowHide = false;
                              this.noDataVisibility = true;
                            }else{
                               this.tableShowHide = true;
                               this.noDataVisibility = false;
                               setTimeout(() => { 
                                let tableWidth = document.getElementById('SystemManagerDetails').scrollWidth;
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
              /* setTimeout(() => { 
                this.showLoader = false;
                this.tableData = {"sessionId":"70226453","serviceToken":"83595","status":"SUCCESS","pageCount":5,"auditTrailDetails":[{"id":3,"eventName":"MIRJAPUR","eventSubName":"Mirzapur season 1","actionPerformed":"Millionaire carpet exporter","userName":"KALIN BHAIYA","eventDescription":"King of Mirzapur","dateTime":"2018-12-05T14:40:07.000+0000"},{"id":3,"eventName":"MIRJAPUR","eventSubName":"Mirzapur season 1","actionPerformed":"Inherit his father's legacy","userName":"MUNNA BHAIYA","eventDescription":"King of Mirzapur","dateTime":"2018-12-05T14:40:07.000+0000"}],"eventNameList":["CONFIGURATIONS","PRE MIGRATION","MIGRATION","POST MIGRATION"]};
                  if(this.tableData.sessionId == "408" || this.tableData.status == "Invalid User"){
                       this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                      } 
                 
                    this.totalPages = this.tableData.pageCount;
                    let pageCount = [];
                    for (var i = 1; i <= this.tableData.pageCount; i++) {
                        pageCount.push(i);
                    }
                    this.pageRenge = pageCount;
                    this.pager = this.getPager(this.totalPages*this.pageSize, this.currentPage, this.pageRenge);
                    this.eventNameList = this.tableData.eventNameList;
                  
                    if(this.tableData.auditTrailDetails.length == 0){
                      this.tableShowHide = false;
                      this.noDataVisibility = true;
                    }else{
                      
                      this.tableShowHide = true;
                      this.noDataVisibility = false;
                      setTimeout(() => { 
                        let tableWidth = document.getElementById('SystemManagerDetails').scrollWidth;
                        console.log("tablewidth" +tableWidth);
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

  viewAuditTrailDetails(event){
    let toDt:any = $("#toDate").val(),
        fromDt:any =  $("#fromDate").val();        
   if(Date.parse(toDt) < Date.parse(fromDt)){
      this.validationData.rules.toDate.customfunction = true;
      
  } else {
      this.validationData.rules.toDate.customfunction = false;
  }   
    validator.performValidation(event, this.validationData, "save_update");
    setTimeout(() => {
    if (this.isValidForm(event)) {
      this.showLoader = true;
      this.tableShowHide = false;
      let currentEditedForm = event.target.parentNode.parentNode,
          searchCriteria = {
                "eventName": currentEditedForm.querySelector("#eventName").value,
                "eventSubName": currentEditedForm.querySelector("#eventSubName").value,
                "action": currentEditedForm.querySelector("#action").value,
                "userName": currentEditedForm.querySelector("#userName").value,
                "fromDate": currentEditedForm.querySelector("#fromDate").value,
                "toDate": currentEditedForm.querySelector("#toDate").value
          };
      this.searchStatus = "search";
      this.searchCriteria = searchCriteria;
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
      this.getAllauditTrailDetails();
  }
},0);
}

  

  /* validates current submitted form is valid and free from errors
   * @param : pass the event
   * @retun : boolean
   */

  isValidForm(event){
    return ( $(event.target).parents("form").find(".error-border").length == 0) ? true : false;
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
            this.pager = this.getPager(this.totalPages*this.pageSize, this.currentPage, this.pageRenge);
            this.paginationDisabbled = false;
            // Hide all the form/Table/Nodatafound5
            this.tableShowHide = false;
            this.showNoDataFound = false;
            this.getAllauditTrailDetails();


        }, 0);



    };

 onChangeTableRowLength(event) {
        this.showLoader = true;
        this.pageSize = event.target.value;

        this.currentPage = 1;

        let paginationDetails = {
            "count": parseInt(this.pageSize),
            "page": this.currentPage
        };

        this.paginationDetails = paginationDetails;
        this.paginationDisabbled = false;
        // Hide all the form/Table/Nodatafound5
        this.tableShowHide = false;
        this.showNoDataFound = false;
        setTimeout(() => {
            this.showLoader = false;
            $("#dataWrapper").find(".scrollBody").scrollLeft(0);
            this.getAllauditTrailDetails();
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

  clearSearchFrom() {
    this.searchForm.nativeElement.reset();  
}

  closeModel(){
      this.successModalBlock.close();
      this.ngOnInit();
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
  /*
   * On click sort header in table then sort the data ascending and decending
   * @param : columnName, event and current Index
   * @retun : null
   */

  changeSorting(predicate, event, index){
    this.sharedService.dynamicSort(predicate, event, index, this.tableData.auditTrailDetails);
  }

  /*
   * Used to get the EventSubNameList
   * @param : eventName
   * @retun : null
   */
 
  getEventSubName(eventName){
      this.showLoader = true;
      this.getEventSubNameList = [];
      this.getActionNameList = [];
      this.getUserNameList = [];
      this.audittrailService.getEventSubNameDetails(eventName, this.sharedService.createServiceToken() )
        .subscribe(
            data => {
                setTimeout(() => { 
                  let jsonStatue = data.json();
                  this.getEventSubNameListData = data.json();
                      if(jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User"){
                       this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                      } else {
                        if( this.sharedService.isvalidServiceToken( parseInt(data.json().serviceToken,10) ) || data.json().sessionId == "408"){
                          if(jsonStatue.status == "SUCCESS"){
                            this.getEventSubNameList = this.getEventSubNameListData.eventSubNameList;
                            this.showLoader = false;
                          }else{
                            this.showLoader = false;
                            this.getEventSubNameList = [];
                          }
                         }   
                      }
                                    
                }, 1000);
            },
            error => {
              //Please Comment while checkIn
              /*setTimeout(() => { 
                this.showLoader = false;
                this.getEventSubNameListData = {"sessionId":"70226453","serviceToken":"83595","status":"SUCCESS","eventSubNameList":["USER MANAGEMENT","SYSTEM MANAGER CONFIG","GENERAL CONFIG"]};
                  if(this.getEventSubNameListData.sessionId == "408" || this.getEventSubNameListData.status == "Invalid User"){
                     this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                    }
                  if(this.getEventSubNameListData.status == "SUCCESS"){
                    this.getEventSubNameList = this.getEventSubNameListData.eventSubNameList;
                    console.log(this.getEventSubNameList);
                  }else{
                    this.showLoader = false;
                    this.getEventSubNameList = [];
                  }
              }, 100);*/
              //Please Comment while checkIn
        });
         
  }

  /*
   * Used to get the ActionNameList
   * @param : eventName, eventSubName
   * @retun : null
   */
 
  getActionName(eventName, eventSubName){
    this.showLoader = true;
    this.getActionNameList = [];
    this.getUserNameList = [];
    this.audittrailService.getEventActionDetails(eventName, eventSubName, this.sharedService.createServiceToken() )
        .subscribe(
            data => {
                setTimeout(() => { 
                  let jsonStatue = data.json();
                  this.getActionNameListData = data.json();
                      if(jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User"){
                       this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                      } else {
                        if( this.sharedService.isvalidServiceToken( parseInt(data.json().serviceToken,10) ) || data.json().sessionId == "408"){
                          if(jsonStatue.status == "SUCCESS"){
                            this.getActionNameList = this.getActionNameListData.eventActionList;
                            this.showLoader = false;
                          }else{
                            this.showLoader = false;
                            this.getActionNameList = [];
                          }
                         }   
                      }
                                    
                }, 1000);
            },
            error => {
              //Please Comment while checkIn
              /*setTimeout(() => { 
                this.showLoader = false;
                this.getActionNameListData = {"sessionId":"43851724","eventActionList":["Save"],"serviceToken":"75136","status":"SUCCESS"};
                  if(this.getActionNameListData.sessionId == "408" || this.getActionNameListData.status == "Invalid User"){
                     this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                    }
                  if(this.getActionNameListData.status == "SUCCESS"){
                    this.getActionNameList = this.getActionNameListData.eventActionList;
                    console.log(this.getActionNameList);
                  }else{
                    this.showLoader = false;
                    this.getActionNameList = [];
                  }
              }, 100);*/
              //Please Comment while checkIn
        });
  }

   /*
   * Used to get the eventUserList
   * @param : eventName, eventSubName, action
   * @retun : null
   */
  getUserName(eventName, eventSubName, action){
    this.showLoader = true;
    this.getUserNameList = [];
    this.audittrailService.getEventUserNameDetails(eventName, eventSubName, action, this.sharedService.createServiceToken() )
        .subscribe(
            data => {
                setTimeout(() => { 
                  let jsonStatue = data.json();
                  this.getUserNameListData = data.json();
                      if(jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User"){
                       this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                      } else {
                        if( this.sharedService.isvalidServiceToken( parseInt(data.json().serviceToken,10) ) || data.json().sessionId == "408"){
                          if(jsonStatue.status == "SUCCESS"){
                            this.getUserNameList = this.getUserNameListData.eventUserList;
                            this.showLoader = false;
                          }else{
                            this.showLoader = false;
                            this.getUserNameList = [];
                          }
                         }   
                      }                                    
                }, 1000);
            },
            error => {
              //Please Comment while checkIn
              /*setTimeout(() => { 
                this.showLoader = false;
                this.getUserNameListData = {"sessionId":"70226453","serviceToken":"83595","status":"SUCCESS","eventUserList":["Kamlesh","Ratul","Ravindra"]};
                  if(this.getUserNameListData.sessionId == "408" || this.getUserNameListData.status == "Invalid User"){
                     this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                    }
                  if(this.getUserNameListData.status == "SUCCESS"){
                    this.getUserNameList = this.getUserNameListData.eventUserList;
                    console.log(this.getUserNameList);
                  }else{
                    this.showLoader = false;
                    this.getUserNameList = [];
                  }
              }, 100);*/
              //Please Comment while checkIn
        });
  }
  
}

