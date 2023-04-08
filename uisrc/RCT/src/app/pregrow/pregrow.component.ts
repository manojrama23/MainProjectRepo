import { ViewContainerRef, Input, ElementRef, Renderer, Component, OnInit, ViewChild, HostListener } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { NgbModal, ModalDismissReasons} from '@ng-bootstrap/ng-bootstrap';
import { PregrowService } from '../services/pregrow.service';
import { SharedService } from '../services/shared.service';
import { validator } from '../validation';
import { DatePipe } from '@angular/common';
import * as $ from 'jquery';
import * as _ from 'underscore';

@Component({
  selector: 'rct-pregrow',
  templateUrl: './pregrow.component.html',
  styleUrls: ['./pregrow.component.scss'],
  providers: [PregrowService]
})
  export class PregrowComponent implements OnInit {     
    testId: any;
    ckeckedOrNot: boolean;
    showLoader:boolean = true;
    tableData:any;    
    closeResult:string;    
    noDataVisibility :boolean = false;
    showModelMessage: boolean = false;
    messageType: any;
    modelData :any;
    sessionExpiredModalBlock : any; // Helps to close/open the model window
    successModalBlock : any;
    message : any; 
    tableShowHide :boolean = false;
    tableDataHeight:any;
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
    toggle = false;
    lsmList:any;
    growBlock:boolean=false;
    searchBlock:boolean=false;
    fromDt:any;
    reportBlock:boolean = false;
    scriptBlock:boolean = false;
    showNoDataFound: boolean;
    searchStatus: string;
    searchCriteria: any;
    currentEditRow: any;
    max = new Date();
    fromDate:any;
    toDate:any;
    preGrowModal: any;
    testResultsData: any;
    showOutput: boolean = false;
    scriptOutput: any;
    useCaseSO = "";
    scriptList = [];
    errMessage:boolean= false;
    ciqNEFileDetails:any;
    nesListData: any;
    getNeslist:any;
    getCiqList:any=[];
    ciqListData;any;
    lsmVersionDetails: any;
    selectedVersion: any = "";
    selectedLsmName: any = "";
    lsmNameDetails: any;
    scriptData: any;
    useCaseList:any;
    searchCiqList:any;
    searchsmNameList:any;
    searchneNameList:any;
    searchuseCaseList:any;
    searchsmversionList:any;
    navigationSubscription: any;
    programChangeSubscription:any;
  validationData: any = {
        "rules": {
            "lsmName": {
                "required": true
            },
            "fromDate": {
                "required": true
            }, 
            "toDate": {
                "required": true
            }, 
            "ciqFileName": {
                "required": true
            }, 
            "neName": {
                "required": true
            }, 
            "growTemplate": {
                "required": true
            }, 
            "smVersion": {
                "required": true
            }, 
            "smName": {
                "required": true
            }, 
            "useCaseName": {
                "required": true
            }, 
            "remarks": {
                "required": false
            }, 
            "searchCiqFileName": {
                "required": false
            }, 
            "searchNeName": {
                "required": false
            }, 
            "searchGrowTemplate": {
                "required": false
            }, 
            "searchSmVersion": {
                "required": false
            }, 
            "searchSmName": {
                "required": false
            }, 
            "searchUseCaseName": {
                "required": false
            }
        },
        "messages": {
            "lsmName": {
                "required": "Please Select LSM Name"
            },
            "fromDate": {
                "required": "Please select date range"
            }, 
            "toDate": {
                "required": "Please select date range"
            }, 
            "ciqFileName": {
                "required": "CIQ Name is required"
            }, 
            "neName": {
                "required": "NE Name is required"
            }, 
            "growTemplate": {
                "required": "Grow Template is required"
            }, 
            "smVersion": {
                "required": "SM Version is required"
            }, 
            "smName": {
                "required": "SM Name is required"
            }, 
            "useCaseName": {
                "required": "Use Case is required"
            }, 
            "remarks": {
                "required": "Remarks is required"
            }, 
            "searchCiqFileName": {
                "required": "CIQ Name is required"
            }, 
            "searchNeName": {
                "required": "NE Name is required"
            }, 
            "searchGrowTemplate": {
                "required": "Grow Template is required"
            }, 
            "searchSmVersion": {
                "required": "SM Version is required"
            }, 
            "searchSmName": {
                "required": "SM Name is required"
            }, 
            "searchUseCaseName": {
                "required": "Use Case is required"
            }
        }
    };
  @ViewChild('searchTab') searchTabRef;
  @ViewChild('growTab') growTabRef: ElementRef;
  @ViewChild('reportTab') reportTabRef;
  @ViewChild('scriptTab') scriptTabRef;
  @ViewChild('confirmModal') confirmModalRef: ElementRef;
  @ViewChild('sessionExpiredModal') sessionExpiredModalRef: ElementRef;
  @ViewChild('successModal') successModalRef: ElementRef;
  @ViewChild('searchForm') searchForm;
  @ViewChild('growForm') growForm;

  
  constructor(
    private element: ElementRef,
    private renderer: Renderer,
    private router:Router,
    private modalService: NgbModal,
    private pregrowService: PregrowService,
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
        if(pgm){          
            this.loadInitialData();
      }
    });
  }
  loadInitialData(){
    this.currentPage = 1;
    this.totalPages = 1;
    this.TableRowLength = 10;
    this.pageSize = 10;
     let paginationDetails = {
          "count": parseInt(this.TableRowLength,10),
          "page": this.currentPage
      };
    this.searchStatus = 'load';
    this.growBlock=true;
    this.searchBlock=false;
    this.selectedVersion = "";
    this.selectedLsmName = "";

    this.paginationDetails = paginationDetails;  
    this.showLoader = true;
    this.noDataVisibility = true;
    //this.getCiqListData(this.fromDate,this.toDate);
    //this.getPreGrowDetails();
    this.setMenuHighlight("grow");     
    /* this.fromDate = new Date( this.tableData.searchStartDate);
    this.toDate = new Date(this.tableData.searchEndDate); */ 
    this.resetGrowForm();
    this.growTabBind();
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
  
  resetGrowForm() {
    setTimeout(() => {
        this.growForm.nativeElement.reset();
    }, 0);
}


clearSearchFrom() {
    this.searchForm.nativeElement.reset();  
}

getCiqListData(fromDate,toDate){
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
    this.pregrowService.getCiqListData(this.sharedService.createServiceToken(),fromDt,toDt,this.searchStatus)
    .subscribe(
        data => {
            setTimeout(() => { 
              let jsonStatue = data.json();
              this.ciqListData = data.json();
              this.showLoader = false;
                  if(jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User"){
                   this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                  } else {
                    if( this.sharedService.isvalidServiceToken( parseInt(data.json().serviceToken,10) ) || data.json().sessionId == "408"){
                      if(jsonStatue.status == "SUCCESS"){
                        this.fromDate = new Date( this.ciqListData.fromDate);
                        this.toDate = new Date(this.ciqListData.toDate);
                        
                        this.getCiqList = this.ciqListData.getCiqList;
                      }else{
                        this.showLoader = false;
                        this.getCiqList = [];
                      }
                     }   
                  }
                                
            }, 1000);
        },
        error => {
          //Please Comment while checkIn
          /* setTimeout(() => { 
            this.showLoader = false;
            //no data
            this.ciqListData = {"fromDate":"02/13/2019","toDate":"02/20/2019","sessionId":"76acbc02","serviceToken":"66358","getCiqList":[],"status":"SUCCESS"};
            //this.ciqListData ={"fromDate":"02/13/2019","toDate":"02/20/2019","sessionId":"76acbc02","serviceToken":"66358","getCiqList":[{"id":2,"programDetailsEntity":{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":null,"status":"InActive","creationDate":"2019-02-11T09:24:41.000+0000","createdBy":"superadmin"},"ciqFileName":"Complete CH52XC421 STA CDU30 2.5 TDD mMIMO for BBU No_ 1.xlsx","ciqFilePath":"/home/user/Documents/","scriptFileName":"Script.zip","scriptFilePath":"/home/user/RCT/rctsoftware/SMART/Customer/3/PreMigration/Input/SCRIPT/Script.zip","checklistFileName":"network_config_details.xlsx","checklistFilePath":"/home/user/RCT/rctsoftware/SMART/Customer/3/PreMigration/Input/CHECKLIST/network_config_details.xlsx","ciqVersion":"ORIGINAL","fileSourceType":"UPLOAD","uploadBy":"ADMIN","remarks":"Testing....","creationDate":"2019-02-19T05:53:11.000+0000"}],"status":"SUCCESS"};
              if(this.ciqListData.sessionId == "408" || this.ciqListData.status == "Invalid User"){
                 this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                }
              if(this.ciqListData.status == "SUCCESS"){
                this.fromDate = new Date( this.ciqListData.fromDate);
                this.toDate = new Date(this.ciqListData.toDate);
                
                this.getCiqList = this.ciqListData.getCiqList;
              }else{
                this.showLoader = false;
                this.getCiqList = [];
              }
          }, 1000); */
          //Please Comment while checkIn
        });
  }


  getPreGrowDetails(){    
    this.showLoader = true;
    $("#dataWrapper").find(".scrollBody").scrollLeft(0);
    this.pregrowService.getPreGrowDetails(this.sharedService.createServiceToken(),this.paginationDetails,this.searchStatus, this.searchCriteria,)
          .subscribe(
              data => {
                  setTimeout(() => {
                    let jsonStatue = data.json();  
                    
                    this.showLoader = false; 
                     if(jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User"){
                        if(!this.sessionExpiredModalBlock){
                         this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                        }
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
                            //   this.lsmList = this.tableData.lsmDetails;
                            this.fromDate = new Date( this.tableData.searchStartDate);
                            this.toDate = new Date(this.tableData.searchEndDate);
                    
                            this.getCiqList = this.tableData.ciqList;
                            if(this.getCiqList.length > 0){
                              this.ciqNEFileDetails = this.getCiqList[0];
                              this.getNEdata();
                            }   
                            this.lsmVersionDetails = Object.keys(this.tableData.versionList);
                            this.useCaseList = this.tableData.useCaseList;
                             /* Loading search dropdown list */

                             this.searchCiqList = this.tableData.ciqSearchList;
                             this.searchneNameList = this.tableData.neNameSearchList;
                             this.searchsmNameList = this.tableData.smNameSearchList;
                             this.searchuseCaseList = this.tableData.usecaseSearchList;
                             this.searchsmversionList = this.tableData.smVersionSearchList;

                              if(this.tableData.neGrowdetails.length == 0){
                                this.tableShowHide = false;
                                this.noDataVisibility = true;
                              }else{
                                this.tableShowHide = true;
                                 this.noDataVisibility = false;
                                 setTimeout(() => {
                                  let tableWidth = document.getElementById('preGrowDetails').scrollWidth;
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
                //   this.tableData = {"sessionId":"edb022c","serviceToken":"77574","status":"SUCCESS","pageCount":1,"growTemplate":["gh","vubh","fj"],"csvAuditTrailDetModels":[{"id":2,"growingName":"fj","csvFileName":"gj","ciqFileName":"dj","neName":"gj","smId":null,"smName":"LSM","smVersion":"1.2.3","description":"gj","growingDate":"2019-02-15 16:32:23","growPerformedBy":"test","useCaseName":null,"useCaseId":null,"customerId":null,"customerName":null,"status":"jgf","csvFilePath":null,"searchStartDate":null,"searchEndDate":null,"programDetailsEntity":{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":null,"status":"InActive","creationDate":"2019-02-11T09:24:41.000+0000","createdBy":"superadmin"},"useCaseBuilderEntity":{"id":1,"networkTypeDetailsEntity":{"id":1,"networkType":"All","createdBy":"admin","caretedDate":null,"status":"Active","remarks":"active","networkColor":null},"lsmEntity":{"id":8,"lsmName":"LSM 1","lsmIp":"10.20.120.199","lsmVersion":"v 1.0.1","createdBy":"admin","lsmUserName":"Admin","lsmPassword":"root123","creationDate":"2018-12-18T10:33:32.000+0000","status":"Active","remarks":"test","programName":"","neType":"","bucket":"","networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"}},"useCaseName":"usecase1","useCount":2,"useCaseBuilderParamEntity":[],"customerId":2,"useCaseDescription":"fvhb","executionSequence":1},"lsmEntity":{"id":8,"lsmName":"LSM 1","lsmIp":"10.20.120.199","lsmVersion":"v 1.0.1","createdBy":"admin","lsmUserName":"Admin","lsmPassword":"root123","creationDate":"2018-12-18T10:33:32.000+0000","status":"Active","remarks":"test","programName":"","neType":"","bucket":"","networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"}},"neTypeEntity":{"id":4,"neType":"LSM"},"neVersionEntity":{"id":1,"programDetailsEntity":{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":null,"status":"InActive","creationDate":"2019-02-11T09:24:41.000+0000","createdBy":"superadmin"},"neVersion":"1.2.3","status":"Active","createdBy":"superadmin","creationDate":"2019-02-15T11:02:23.000+0000"},"remarks":"remakrs01"},{"id":1,"growingName":"vubh","csvFileName":"csv","ciqFileName":"CIQ","neName":"vbh","smId":null,"smName":null,"smVersion":null,"description":"fgjf","growingDate":"2019-02-15 16:32:23","growPerformedBy":"supriya","useCaseName":null,"useCaseId":null,"customerId":null,"customerName":null,"status":"hvbo","csvFilePath":null,"searchStartDate":null,"searchEndDate":null,"programDetailsEntity":{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":null,"status":"InActive","creationDate":"2019-02-11T09:24:41.000+0000","createdBy":"superadmin"},"useCaseBuilderEntity":{"id":1,"networkTypeDetailsEntity":{"id":1,"networkType":"All","createdBy":"admin","caretedDate":null,"status":"Active","remarks":"active","networkColor":null},"lsmEntity":{"id":8,"lsmName":"LSM 1","lsmIp":"10.20.120.199","lsmVersion":"v 1.0.1","createdBy":"admin","lsmUserName":"Admin","lsmPassword":"root123","creationDate":"2018-12-18T10:33:32.000+0000","status":"Active","remarks":"test","programName":"","neType":"","bucket":"","networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"}},"useCaseName":"usecase1","useCount":2,"useCaseBuilderParamEntity":[],"customerId":2,"useCaseDescription":"fvhb","executionSequence":1},"lsmEntity":{"id":8,"lsmName":"LSM 1","lsmIp":"10.20.120.199","lsmVersion":"v 1.0.1","createdBy":"admin","lsmUserName":"Admin","lsmPassword":"root123","creationDate":"2018-12-18T10:33:32.000+0000","status":"Active","remarks":"test","programName":"","neType":"","bucket":"","networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"}},"neTypeEntity":{"id":4,"neType":"LSM"},"neVersionEntity":{"id":2,"programDetailsEntity":{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":null,"status":"InActive","creationDate":"2019-02-11T09:24:41.000+0000","createdBy":"superadmin"},"neVersion":"1.0.0","status":"Active","createdBy":"admin","creationDate":"2019-02-15T11:02:23.000+0000"},"remarks":""},{"id":3,"growingName":"gh","csvFileName":"","ciqFileName":"ght","neName":"jg","smId":null,"smName":null,"smVersion":null,"description":"gj","growingDate":"2019-02-15 16:32:23","growPerformedBy":"test1","useCaseName":null,"useCaseId":null,"customerId":null,"customerName":null,"status":"dh","csvFilePath":null,"searchStartDate":null,"searchEndDate":null,"programDetailsEntity":{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":null,"status":"InActive","creationDate":"2019-02-11T09:24:41.000+0000","createdBy":"superadmin"},"useCaseBuilderEntity":{"id":1,"networkTypeDetailsEntity":{"id":1,"networkType":"All","createdBy":"admin","caretedDate":null,"status":"Active","remarks":"active","networkColor":null},"lsmEntity":{"id":8,"lsmName":"LSM 1","lsmIp":"10.20.120.199","lsmVersion":"v 1.0.1","createdBy":"admin","lsmUserName":"Admin","lsmPassword":"root123","creationDate":"2018-12-18T10:33:32.000+0000","status":"Active","remarks":"test","programName":"","neType":"","bucket":"","networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"}},"useCaseName":"usecase1","useCount":2,"useCaseBuilderParamEntity":[],"customerId":2,"useCaseDescription":"fvhb","executionSequence":1},"lsmEntity":{"id":8,"lsmName":"LSM 1","lsmIp":"10.20.120.199","lsmVersion":"v 1.0.1","createdBy":"admin","lsmUserName":"Admin","lsmPassword":"root123","creationDate":"2018-12-18T10:33:32.000+0000","status":"Active","remarks":"test","programName":"","neType":"","bucket":"","networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"}},"neTypeEntity":{"id":1,"neType":"CIQ SERVER"},"neVersionEntity":{"id":2,"programDetailsEntity":{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":null,"status":"InActive","creationDate":"2019-02-11T09:24:41.000+0000","createdBy":"superadmin"},"neVersion":"1.0.0","status":"Active","createdBy":"admin","creationDate":"2019-02-15T11:02:23.000+0000"},"remarks":""}],"ciqName":["ght","dj","CIQ"],"searchEndDate":"02/21/2019","smName":["CIQ SERVER","LSM"],"neName":["gj","jg","vbh"],"searchStartDate":"02/14/2019","useCase":["usecase1"],"smVersion":["1.2.3","1.0.0"]};
                this.tableData ={"ciqSearchList":["Test1","Test"],"pageCount":1,"neNameSearchList":["ne","ne1"],"searchEndDate":"03/07/2019","useCaseList":[{"id":1,"networkConfigEntity":{"id":1,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"neTypeEntity":{"id":1,"neType":"CIQ SERVER"},"neName":"CIQ_Bala","neVersionEntity":{"id":1,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"neVersion":"1.2.3","status":"Active","createdBy":"superadmin","creationDate":"2019-02-15T11:02:23.000+0000"},"neIp":"10.20.120.50","neUserName":"user","nePassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-02-28T10:47:48.000+0000","status":"Active","remarks":"Bala sys as CIQ server","neDetails":[]},"useCaseName":"test","remarks":"test","useCount":5,"useCaseCreationDate":"2019-03-05T08:11:30.000+0000","createdBy":"admin","useCaseBuilderParamEntity":[],"customerId":2,"migrationType":"test","subType":"test","customerDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"executionSequence":1}],"sessionId":"d120e8a4","neGrowdetails":[{"id":1,"growingName":"ciq","csvFileName":"csv","ciqFileName":"Test","neName":"ne","smId":null,"smName":"lsm","smVersion":"1.0.0","description":null,"growingDate":"2019-03-05 16:32:23","growPerformedBy":"admin","useCaseName":"usecase","useCaseId":null,"customerId":null,"customerName":null,"status":"Active","csvFilePath":null,"searchStartDate":null,"searchEndDate":null,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"useCaseBuilderEntity":null,"lsmEntity":null,"neTypeEntity":null,"neVersionEntity":null,"remarks":"test"},{"id":2,"growingName":"ciq","csvFileName":"csv","ciqFileName":"Test1","neName":"ne1","smId":null,"smName":"lsm1","smVersion":"1.1.0","description":null,"growingDate":"2019-03-05 16:32:23","growPerformedBy":"superadmin","useCaseName":"usecase1","useCaseId":null,"customerId":null,"customerName":null,"status":"Active","csvFilePath":null,"searchStartDate":null,"searchEndDate":null,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"useCaseBuilderEntity":null,"lsmEntity":null,"neTypeEntity":null,"neVersionEntity":null,"remarks":"test"}],"searchStartDate":"02/28/2019","smNameSearchList":["lsm","lsm1"],"versionList":{"1.3.1":["Script_bala"],"1.2.3":["CIQ_Bala"]},"ciqList":[{"id":48,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"ciqFileName":"test","ciqFilePath":"/home/user","scriptFileName":"test","scriptFilePath":"/home","checklistFileName":"test1","checklistFilePath":"/home/user","ciqVersion":"1.0.2","fileSourceType":"upload","uploadBy":"admin","remarks":"test","creationDate":"2019-03-05T12:18:11.000+0000"}],"usecaseSearchList":["usecase","usecase1"],"serviceToken":"77195","smVersionSearchList":["1.0.0","1.1.0"],"status":"SUCCESS"};
                    this.totalPages = this.tableData.pageCount;
                      let pageCount = [];
                      for (var i = 1; i <= this.tableData.pageCount; i++) {
                          pageCount.push(i);
                      }
                      this.pageRenge = pageCount;
                      this.pager = this.getPager(this.totalPages*this.pageSize, this.currentPage, this.pageRenge);
                  
                    if(this.tableData.sessionId == "408" || this.tableData.status == "Invalid User"){
                         this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                        }
                      
                  
                    this.fromDate = new Date( this.tableData.searchStartDate);
                    this.toDate = new Date(this.tableData.searchEndDate);
                    
                    this.getCiqList = this.tableData.ciqList;
                    if(this.getCiqList.length > 0){
                      this.ciqNEFileDetails = this.getCiqList[0];
                      this.getNEdata();
                    }   
                      this.lsmVersionDetails = Object.keys(this.tableData.versionList);
                      this.useCaseList = this.tableData.useCaseList;

                    //   Loading search dropdown list 

                      this.searchCiqList = this.tableData.ciqSearchList;
                      this.searchneNameList = this.tableData.neNameSearchList;
                      this.searchsmNameList = this.tableData.smNameSearchList;
                      this.searchuseCaseList = this.tableData.usecaseSearchList;
                      this.searchsmversionList = this.tableData.smVersionSearchList;

                      if(this.tableData.neGrowdetails.length == 0){
                        this.tableShowHide = false;
                        this.noDataVisibility = true;
                      }else{
                        
                        this.tableShowHide = true;
                        this.noDataVisibility = false;

                        setTimeout(() => {
                          let tableWidth = document.getElementById('preGrowDetails').scrollWidth;
                          
                          $(".scrollBody table").css("min-width",(tableWidth) + "px");
                          $(".scrollHead table").css("width", (tableWidth) + "px");
  
                      
                          $(".scrollBody").on("scroll", function (event) {
                              $(".formEditRow #formWrapper").css("margin-left", (event.target.scrollLeft - 5) + "px");
                              $(".form-control-fixed").css("right",(event.target.scrollLeft * -1) + "px");
                              $(".scrollHead table").css("margin-left",(event.target.scrollLeft * -1) + "px");
                          });
                                                   
                            },0);                      
                      }
                  
                }, 1000); */
               
                //Please Comment while checkIn
          });
  }

  getNEdata(){
    this.tableShowHide = false;
    this.showLoader = true;
    this.pregrowService.getNeListData(this.ciqNEFileDetails.ciqFileName, this.sharedService.createServiceToken() )
        .subscribe(
            data => {
                setTimeout(() => { 
                  let jsonStatue = data.json();
                  this.nesListData = data.json();
                  this.showLoader = false;
                      if(jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User"){
                       this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                      } else {
                        if( this.sharedService.isvalidServiceToken( parseInt(data.json().serviceToken,10) ) || data.json().sessionId == "408"){
                          if(this.nesListData.status == "SUCCESS"){
                            this.tableShowHide = true;
                              this.getNeslist = this.nesListData.eNBList;
                          }else{
                            this.showLoader = false;
                            this.getNeslist = [];
                          }
                         }   
                      }
                                    
                }, 1000);
            },
            error => {
              //Please Comment while checkIn
             /*  setTimeout(() => { 
                this.showLoader = false;
                this.nesListData = JSON.parse('{"eNBList":[{"eNBName":"CHCKILQUBBULTE0518578","eNBId":"518578"}],"sessionId":"aa5393be","serviceToken":"77360","status":"SUCCESS"}');
                  if(this.nesListData.sessionId == "408" || this.nesListData.status == "Invalid User"){
                     this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                    }
                  if(this.nesListData.status == "SUCCESS"){
                    this.tableShowHide = true;
                      this.getNeslist = this.nesListData.eNBList;
                  }else{
                    this.showLoader = false;
                    this.getNeslist = [];
                  }
              }, 100); */
              //Please Comment while checkIn
        });

  }
  
  getLsmName(lsmSelectedVersion) {
    this.selectedLsmName ="";
    // this.selSearchSMName ="";
    // this.lsmNameDetails = [];
    this.lsmNameDetails = this.tableData.versionList[lsmSelectedVersion];
}

/* getScriptList(lsmSelectedVersion) {
     
    this.scriptData = Object.values(this.tableData.versionList[lsmSelectedVersion.selectedLsmName]);

} */
growTabBind(){
    this.showNoDataFound = false;
    this.growBlock=true;
    this.searchBlock=false;
    this.searchStatus = 'load';
    this.selectedVersion = "";
    this.selectedLsmName = "";
    this.setMenuHighlight("grow");
    this.editableFormArray = [];
    this.getPreGrowDetails();
}
searchTabBind(){
    let searchCrtra = {  "ciqFileName": "", "neName": "", "growingName":"ciq", "smVersion":"", "smName":"", "useCaseName":"",  "searchStartDate": "", "searchEndDate": "" };
    this.searchCriteria = searchCrtra;    
    this.growBlock=false;
    this.searchBlock=true;   
    this.searchStatus = 'load';
    this.setMenuHighlight("search");
   $("#fromDate").val('');
   $("#toDate").val('');
   $("#searchNeName").val('');                          
   $("#searchCiqFileName").val('');
   $("#searchGrowTemplate").val('');
   $("#searchSmVersion").val('');
   $("#searchSmName").val('');
   $("#searchUseCaseName").val('');                         
    this.getPreGrowDetails();
    if (this.currentEditRow != undefined) {
        this.currentEditRow.className = "editRow";
    }
    this.editableFormArray = [];
    this.searchForm.nativeElement.reset();

}

reportTabBind() {
    this.reportBlock = true;
    this.scriptBlock = false;
    this.setReportAndScriptTabsHighLight("report");

}

scriptTabBind() {
    this.reportBlock = false;
    this.scriptBlock = true;
    this.setReportAndScriptTabsHighLight("script");
}

checkConnection(event) {

}

searchNeGrow(event) {
    if (!event.target.classList.contains('buttonDisabled')) {
        this.tableShowHide = false;
        setTimeout(() => {
            $("#dataWrapper").find(".scrollBody").scrollLeft(0);
        }, 0);

   
        setTimeout(() => {
            if (this.isValidForm(event)) {                
                    this.showLoader = true;

                    // To hide the No Data Found and REMOVAL DETAILS Form
                    this.growBlock = false;
                    this.showNoDataFound = false;

                    let currentForm = event.target.parentNode.parentNode.parentNode,
                        searchCrtra = {        
                            "searchStartDate": currentForm.querySelector("#searchStartDate").value,
                            "searchEndDate":currentForm.querySelector("#searchEndDate").value,
                            "neName":currentForm.querySelector("#searchNeName").value,                           
                            "ciqFileName":currentForm.querySelector("#searchCiqFileName").value,
                            "growTemplate":currentForm.querySelector("#searchGrowTemplate").value,
                            "searchSmVersion":currentForm.querySelector("#searchSmVersion").value,
                            "smName":currentForm.querySelector("#searchSmName").value,
                            "useCaseName":currentForm.querySelector("#searchUseCaseName").value                           
                        };

                    if (searchCrtra.searchStartDate || searchCrtra.searchEndDate || searchCrtra.neName || searchCrtra.ciqFileName || searchCrtra.growTemplate || searchCrtra.searchSmVersion || searchCrtra.smName || searchCrtra.useCaseName) {
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
                    this.getPreGrowDetails();              
            }
        }, 0);
    }

}

updateNeGrowDetails(event) {

}

grow(event) {
    this.showLoader = true;
    validator.performValidation(event, this.validationData, "save_update");
    setTimeout(() => {

        if (this.isValidForm(event)) {

            this.showLoader = true;
            let currentForm = event.target.parentNode.parentNode.parentNode,
                growData = {
                    "fromDate": currentForm.querySelector("#fromDate").value,
                    "toDate":currentForm.querySelector("#toDate").value,
                    "neName":currentForm.querySelector("#neName").value,                           
                    "ciqFileName":currentForm.querySelector("#ciqFileName").value,
                    "growTemplate":currentForm.querySelector("#growTemplate").value,
                    "searchSmVersion":currentForm.querySelector("#smVersion").value,
                    "smName":currentForm.querySelector("#smName").value,
                    "useCaseName":currentForm.querySelector("#useCaseName").value,                           
                    "remarks":currentForm.querySelector("#remarks").value,
                    "useCurrentPassWord":this.ckeckedOrNot
                };
                this.pregrowService.growNe(growData, this.sharedService.createServiceToken())
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
                                        this.message = "Successfull!";
                                        this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
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
                            this.searchTabBind();
                            setTimeout(() => {
                              this.showLoader = false;
                              this.message = "Successfully!";
                              this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
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

preGrowRow(event, key, index, preGrowContent) {
    this.testId = key.id;
    this.showLoader = true;    
    this.pregrowService.viewTestResult(this.sharedService.createServiceToken(), key.id)
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
                            this.testResultsData = jsonStatue.useCaseResult;
                            this.preGrowModal = this.modalService.open( preGrowContent, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'testResultPopUp modalWidth scriptView' } );
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
 
                     let jsonStatue = JSON.parse('{"sessionId":"7e088256","serviceToken":"81749","status":"SUCCESS","useCaseResult":[{"useCaseName":"UC1","script_status":[{"scriptName":"script1","scriptId":4,"rule_status":[{"ruleName":"cmd1","status":"Success"}]},{"scriptName":"script2","scriptId":4,"rule_status":[{"ruleName":"cmd2","status":"Success"},{"ruleName":"cmd2","status":"Success"}]}],"useCaseId":1},{"useCaseName":"UC2","useCaseId":2,"script_status":[{"scriptName":"script1","scriptId":4,"rule_status":[{"ruleName":"cmd1","status":"Success"}]},{"scriptName":"script2","scriptId":4,"rule_status":[{"ruleName":"cmd2","status":"Success"}]}]}]}');
 
                    if (jsonStatue.status == "SUCCESS") {
                         this.showLoader = false;
                         this.testResultsData = jsonStatue.useCaseResult;   
                         this.preGrowModal = this.modalService.open( preGrowContent, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'testResultPopUp modalWidth scriptView' } );
                     } else {
                         this.showLoader = false;
                         this.displayModel(jsonStatue.status, "failureIcon");
 
                     }
 
                 }, 100); */

                //Please Comment while checkIn   
            });
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
    
      updateEditRow(index, identifier, event){
        validator.performValidation(event, this.validationData, "save_update");
        setTimeout(() => {
          if(this.isValidForm(event)){
            this.showLoader = true;
            let currentEditedForm = event.target.parentNode.parentNode,
            neGrowdetails = {
                      "id": identifier,
                      "fileName": currentEditedForm.querySelector("#fileName").value,
                      "status": currentEditedForm.querySelector("#status").value
                 };
            this.pregrowService.updatePreGrowDetails(neGrowdetails, this.sharedService.createServiceToken())
            .subscribe(
                data => {
                    let jsonStatue = data.json();
                    this.showLoader = false;
    
                    if(jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User"){
                
                      this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                
                    } else {
                
                      if( this.sharedService.isvalidServiceToken( parseInt(data.json().serviceToken,10) ) || data.json().sessionId == "408"){               
                          if(jsonStatue.status == "SUCCESS"){
                             this.message = "Generate Details updated successfully";
                             this.successModalBlock = this.modalService.open(this.successModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'success-modal'});
    
                        
                             this.cancelEditRow(neGrowdetails.id, '');
                           
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
                     this.message = "Generate Details updated successfully";
                    this.successModalBlock = this.modalService.open(this.successModalRef,{keyboard: false, backdrop: "static",size:'lg',windowClass:"success-modal"});
                     
                      this.cancelEditRow(neGrowdetails.id, '');
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
    
        deleteRow(confirmModal, userName, id, fileName,event) {
            if (event.target.className != "deleteRowDisabled") {
          this.modalService.open(confirmModal,{keyboard: false, backdrop: 'static', size:'lg', windowClass:'confirm-modal'}).result.then((result) => {
            this.closeResult = `Closed with: ${result}`;
          }, (reason) => {
            this.showLoader = true;
    
            this.pregrowService.deleteFileDetails(id, fileName, this.sharedService.createServiceToken())
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
                     /*  setTimeout(() => {
                          this.showLoader = false;
                         let jsonStatue = {"reason":"Generate details deletion failed","sessionId":"5f3732a4","serviceToken":"80356","status":"FAILED"};
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
                    "page": page
                };
    
                this.paginationDetails = paginationDetails;
                this.pager = this.getPager(this.totalPages*this.pageSize, this.currentPage, this.pageRenge);
                this.paginationDisabbled = false;
                // Hide all the form/Table/Nodatafound5
                this.tableShowHide = false;
                
                this.getPreGrowDetails();
    
    
            }, 0);
    
    
    
        };
    
     onChangeTableRowLength(event) {
            this.showLoader = true;
            this.pageSize = event.target.value;
    
            this.currentPage = 1;
    
            let paginationDetails = {
                "count": parseInt(this.pageSize,10),
                "page": this.currentPage
            };
    
            this.paginationDetails = paginationDetails;
            this.paginationDisabbled = false;
            // Hide all the form/Table/Nodatafound5
            this.tableShowHide = false;
            
            setTimeout(() => {
                this.showLoader = false;
                $("#dataWrapper").find(".scrollBody").scrollLeft(0);
                this.getPreGrowDetails();
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
  checkFormEnable(index){
    let indexValue = this.editableFormArray.indexOf(index);
    return indexValue >= 0?true:false;
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


  closeModel(){
    this.successModalBlock.close();
    //this.ngOnInit();
    this.getPreGrowDetails();
}
   /*
   * On click sort header in table then sort the data ascending and decending
   * @param : columnName, event and current Index
   * @retun : null
   */

  changeSorting(predicate, event, index){
    this.sharedService.dynamicSort(predicate, event, index, this.tableData.neGrowdetails);
  }

    /* validates current submitted form is valid and free from errors
     * @param : pass the event
     * @retun : boolean
     */

    isValidForm(event) {
        return ($(event.target).parents("form").find(".error-border").length == 0) ? true : false;
    }

    setMenuHighlight(selectedElement) {
        this.searchTabRef.nativeElement.id = (selectedElement == "search") ? "activeTab" : "inactiveTab";
        this.growTabRef.nativeElement.id = (selectedElement == "grow") ? "activeTab" : "inactiveTab";
        
    }

    setReportAndScriptTabsHighLight(selectedTab) {
        this.reportTabRef.nativeElement.id = (selectedTab == "report") ? "activeTab" : "inactiveTab"; 
        this.scriptTabRef.nativeElement.id = (selectedTab == "script") ? "activeTab" : "inactiveTab";
    }

    closeModelViewResult() {
        this.preGrowModal.close();
        /* this.testResultsData = [];
        this.scriptOutput = "";
        this.useCaseSO = ""; */
    }

  /*   getScriptList(usecase) {
        this.scriptList = [];
        for (let i of this.testResultsData) {
            if (usecase == i.useCaseName) {
                this.scriptList = i.script_status;
            }
        }

    }
 */
    getScriptOutput(event) {
        this.showOutput = false;
        let currentForm = $(event.target).parents("form"),
            useCaseName = currentForm.find('#usecaseSO :selected').val(),
            useCaseId = currentForm.find('#usecaseSO :selected').attr('id'),
            scriptName = currentForm.find('#script :selected').val(),
            scriptId = currentForm.find('#script :selected').attr('id');

        this.pregrowService.scriptOutput(this.sharedService.createServiceToken(), this.testId, useCaseName, useCaseId, scriptName, scriptId)
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

    onChangeDate(){
        this.errMessage = false;
        if(this.fromDate && this.toDate){
          //this.getCiqListData(this.fromDate,this.toDate);
          this.ngOnInit();
        }else{
          this.errMessage = true;
        }
      }
}
