import { ViewContainerRef, Input, ElementRef, Renderer, Component, OnInit, ViewChild, HostListener } from '@angular/core';
import { Router,NavigationEnd } from '@angular/router';
import { NgbModal, ModalDismissReasons, NgbAccordion, NgbAccordionConfig } from '@ng-bootstrap/ng-bootstrap';
import { SnrreportsService } from '../services/snrreports.service';
import { SharedService } from '../services/shared.service';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { filter, map, mergeMap, concat } from 'rxjs/operators';
import { validator } from '../validation';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import * as FileSaver from 'file-saver';
import * as $ from 'jquery';
import * as _ from 'underscore';
import { DatePipe } from '@angular/common';

@Component({
    selector: 'app-snrreports',
    templateUrl: './snrreports.component.html',
    styleUrls: ['./snrreports.component.scss']  
})
export class SnrreportsComponent implements OnInit {
    commericialData: any;
    fitData: any;
    sprintOverAllData: any;
    sprintWeekData: any;
    wpConversionData: any;
    wpTotCarrierData: any;
    weekPeriodicDataVlsm: any;
    fromDt: string;
    toDt: any;
    selectedDate: any;
    cList: any;
    selCustName: any;
    cId: any;
    currentUser: any;
    vlsmandLsmDetails: any;
    currentWeekData: any;
  overAllData: any;
  paginationDisabbled: boolean = false;
  NeName:any;
  tableEdit:boolean = false;
  addtableFormArray = [];
  updatedDetails={};
  searchStartDate:any;
  searchEndDate:any;
  selNeVersion:any;
  neVersionList:any;
  successModalBlock: any;
  searchStatus: string;
  navigationSubscription: any;
  currentPage: any; // for pagination
  remarks: any;
  totalPages: any; // for pagination
  TableRowLength: any; // for pagination
  pageSize: any; // for pagination
  paginationDetails: any; // for pagination
  showLoader: boolean = true;
  tableDataHeight: any;
  searchCiqList: any;
  showModelMessage: boolean = false;
  messageType: any;
  modelData: any;
  editableFormArray = [];
  tableShowHide: boolean = false;
  sessionExpiredModalBlock: any; // Helps to close/open the model window
  tableData: any;
  noDataVisibility: boolean = false;
  pager: any = {}; // pager Object
  message: any;
  marketList:any;
  pageRenge: any; // for pagination
  neVersion:any;
  dropDownList:any;
  neNameList:any;
  selNeMarket:any;
  neConfigTypeList:any;
  searchBlock: boolean = false;
  neConfigBlock: boolean = false;
  searchCriteria: any;
  selNeName:any;
  notForSprint: boolean = false;
  forSupAdmin: boolean = false;
  selectDuration = 'Daily';

  validationData: any = {
    "rules": {
      "customerName": {
        "required": true
      },
      "neVersion": {
        "required": true
      },
      "neName": {
        "required": true
      },
      "siteConfigType": {
        "required": true
      }

    },
    "messages": {
      "customerName": {
        "required": "Please select Custmer Name"
      },
      "neVersion": {
        "required": "Please select NE Version"
      },
      "neName": {
        "required": "Please select NE Name"
      },
      "siteConfigType": {
        "required": "Please select NE Config Type"
      }
    }
  };


  @ViewChild('neConfigTab') neConfigTabRef: ElementRef;
  @ViewChild('searchTab') searchTabRef: ElementRef;
  @ViewChild('confirmModal') confirmModalRef: ElementRef;
  @ViewChild('bluePrintForm') bluePrintForm;
  @ViewChild('searchForm') searchForm;
  @ViewChild('sessionExpiredModal') sessionExpiredModalRef: ElementRef;
  @ViewChild('successModal') successModalRef: ElementRef;

  constructor(
    private element: ElementRef,
    private renderer: Renderer,
    private router: Router,
    private modalService: NgbModal,
    private snrReportsService: SnrreportsService,
    private sharedService: SharedService,
    private datePipe: DatePipe
    
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
    console.log(this.cId);
    let searchCrtra = { "enbId": "", "siteConfigType": "", "searchStartDate": "", "searchEndDate": "" };
    this.searchCriteria = searchCrtra;
    this.neConfigBlock =true;
    this.searchBlock = false;
    this.searchStatus = 'load';
    this.currentPage = 1;
    this.totalPages = 1;
    this.selCustName = '--Select--';
    this.TableRowLength = 10;
    this.pageSize = 10;
    this.selectedDate= this.datePipe.transform(new Date(),"MM/dd/yyyy");
    let paginationDetails = {
      "count": parseInt(this.TableRowLength, 10),
      "page": this.currentPage
    };
    this.paginationDetails = paginationDetails;

    this.currentUser = JSON.parse(sessionStorage.loginDetails).userGroup;
      
      if (this.currentUser != "Super Administrator") {
        this.selectedDate=new Date(this.selectedDate);
        this.selCustName = JSON.parse(sessionStorage.selectedCustomerList);
        //this.selCustName=2;
        this.getSnRReports();
      }else{
        this.selectedDate=new Date(this.selectedDate);
        this.getCustomerIdList();
      }
  }


  ngOnDestroy() {
    // avoid memory leaks here by cleaning up after ourselves. If we  
    // don't then we will continue to run our ngOnInit()   
    // method on every navigationEnd event.
    if (this.navigationSubscription) {
      this.navigationSubscription.unsubscribe();
    }
  }
 
  getSnRReports() {
    this.showLoader = true;
    this.selectedDate=new Date(this.selectedDate) ;    
    this.snrReportsService.getSnRReports(this.sharedService.createServiceToken(),this.selCustName.id,this.selectDuration,this.datePipe.transform( this.selectedDate,"MM/dd/yyyy"))
      .subscribe(
        data => { 
          setTimeout(() => {
            let jsonStatue = data.json();
            this.showLoader = false;

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
                  this.forSupAdmin= true;
                    if(this.selCustName == 2) {
                        this.notForSprint = true;
                      } else if(this.selCustName == 4) {
                        this.notForSprint = false;
                      }

                  this.tableData = jsonStatue;
                  this.totalPages = this.tableData.pageCount;
                  this.overAllData = this.tableData.totCIReportDetails.overAllData;
                  this.currentWeekData = this.tableData.totCIReportDetails.currentWeekData;
                  this.vlsmandLsmDetails = this.tableData.totCIReportDetails.vlsmandLsmDetails;
                  this.weekPeriodicDataVlsm = this.tableData.totCIReportDetails.vlsmTotDetails;
                  this.wpTotCarrierData= this.tableData.totCIReportDetails.crrierTotDetails;
                  this.wpConversionData= this.tableData.totCIReportDetails.ConverTotDetails;
                  this.commericialData = this.tableData.totCIReportDetails.commercialData;
                    this.fitData = this.tableData.totCIReportDetails.FitData;
                    this.sprintOverAllData = this.tableData.totCIReportDetails.overallData;
                    this.sprintWeekData = this.tableData.totCIReportDetails.WeekData;
                    this.tableShowHide = true;
                    this.noDataVisibility = false;
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
            //verizon Data daily data
            this.tableData ={"totCIReportDetails":{"overAllData":{"name":"Overall Sites Migrated successfully","migrtedSiteCount":2603,"totalCount":6400,"percenatgeOfMigrated":40.671875,"remaining":0,"objSiteList":[{"marketName":"New England","migrtedSiteCount":1824,"totalCount":4000,"percenatgeOfMigrated":45.6},{"marketName":"Upstate New York","migrtedSiteCount":779,"totalCount":2400,"percenatgeOfMigrated":32.458332}]},"currentWeekData":{"name":null,"migrtedSiteCount":0,"totalCount":0,"percenatgeOfMigrated":0,"remaining":0,"objSiteList":null},"vlsmandLsmDetails":{"totSiteLsm":973,"totSiteVlsm":1630,"lsmMigCurrentWeek":0,"vlsmMigCurrentWeek":0,"lsmRollBackCurrentWeek":0,"vlsmRollBackCurrentWeek":0},"rehomeCount":1605},"sessionId":"5576bac5","serviceToken":"90928","status":"SUCCESS"};
            //verizon Data daily and Periodically data
            //this.tableData = {"sessionId":"2b07ac35","serviceToken":"74053","status":"SUCCESS","totCIReportDetails":{"overAllData":{"name":"Overall Sites Migrated","migrtedSiteCount":"P2603","totalCount":"P6400","percenatgeOfMigrated":"P40.671875","objSiteList":[{"marketName":"PNew England","migrtedSiteCount":"P1824","totalCount":"P4000","percenatgeOfMigrated":"P45.6"},{"marketName":"PUpstate New York","migrtedSiteCount":"P779","totalCount":"P2400","percenatgeOfMigrated":"P32.458332"}]},"currentWeekData":{"name":"PTotal Sites Migrated successfully this week","migrtedSiteCount":"P50","totalCount":"P0","percenatgeOfMigrated":"P0","objSiteList":[{"marketName":"New England","migrtedSiteCount":30,"totalCount":0,"percenatgeOfMigrated":0},{"marketName":"Upstate New York","migrtedSiteCount":20,"totalCount":0,"percenatgeOfMigrated":0}]},"vlsmTotDetails":[{"marketName":"New England","migrtedSiteCount":0,"totalCount":12,"percenatgeOfMigrated":0},{"marketName":"Upstate New York","migrtedSiteCount":0,"totalCount":13,"percenatgeOfMigrated":0}],"crrierTotDetails":[{"marketName":"New England","migrtedSiteCount":0,"totalCount":1,"percenatgeOfMigrated":0},{"marketName":"Upstate New York","migrtedSiteCount":0,"totalCount":11,"percenatgeOfMigrated":0}],"ConverTotDetails":[{"marketName":"New England","migrtedSiteCount":0,"totalCount":1,"percenatgeOfMigrated":0},{"marketName":"Upstate New York","migrtedSiteCount":0,"totalCount":11,"percenatgeOfMigrated":0}]}};
            // Sprint Data daily data
            //this.tableData ={"totCIReportDetails":{"overAllData":{"name":"Total Migration","migrtedSiteCount":148,"totalCount":1500,"percenatgeOfMigrated":9.866667,"remaining":0,"objSiteList":[{"marketName":"West","migrtedSiteCount":26,"totalCount":224,"percenatgeOfMigrated":11.607142},{"marketName":"Central","migrtedSiteCount":98,"totalCount":1252,"percenatgeOfMigrated":7.827476},{"marketName":"FIT","migrtedSiteCount":24,"totalCount":24,"percenatgeOfMigrated":100}]},"currentWeekData":{"attempted":0,"migratedCount":0,"inProgress":0,"cancelled":0}},"sessionId":"80c8b235","serviceToken":"79146","status":"SUCCESS"};
            
            //weekly and periodically sprint
            //this.tableData ={"totCIReportDetails":{"commercialData":{"migrtedSiteCount":124,"totalCount":1476,"percenatgeOfMigrated":8.266666,"remaining":1352,"inProgress":0,"cancelation":0},"FitData":{"migrtedSiteCount":0,"totalCount":24,"percenatgeOfMigrated":8.266666,"remaining":24,"inProgress":0,"cancelation":0},"overallData":{"migrtedSiteCount":124,"totalCount":1500,"percenatgeOfMigrated":0,"remaining":1375,"inProgress":1,"cancelation":0},"WeekData":{"migrtedSiteCount":23,"totalCount":0,"percenatgeOfMigrated":0,"remaining":0,"inProgress":0,"cancelation":0}},"sessionId":"20d67fa8","serviceToken":"86200","status":"SUCCESS"};

            this.totalPages = this.tableData.pageCount;
            this.forSupAdmin= true;
            if(this.selCustName == 2) {
                this.notForSprint = true;
              } else if(this.selCustName == 4) {
                this.notForSprint = false;
              }
              this.overAllData = this.tableData.totCIReportDetails.overAllData;
              this.currentWeekData = this.tableData.totCIReportDetails.currentWeekData;
              this.vlsmandLsmDetails = this.tableData.totCIReportDetails.vlsmandLsmDetails;
              this.weekPeriodicDataVlsm = this.tableData.totCIReportDetails.vlsmTotDetails;
              this.wpTotCarrierData= this.tableData.totCIReportDetails.crrierTotDetails;
              this.wpConversionData= this.tableData.totCIReportDetails.ConverTotDetails;
              this.commericialData = this.tableData.totCIReportDetails.commercialData;
              this.fitData = this.tableData.totCIReportDetails.FitData;
              this.sprintOverAllData = this.tableData.totCIReportDetails.overallData;
              this.sprintWeekData = this.tableData.totCIReportDetails.WeekData;
            if (this.tableData.sessionId == "408" || this.tableData.status == "Invalid User") {
              this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
            }
              this.tableShowHide = true;
              this.noDataVisibility = false;

          }, 1000); */

          //Please Comment while checkIn
        });
  }

  getCustomerIdList(){

    this.showLoader = true;
    this.snrReportsService.getCustomerIdList(this.sharedService.createServiceToken())
    .subscribe(
      data => {
          setTimeout(() => {
              let jsonStatue = data.json();
              this.showLoader = false;
  
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
                    this.cList = jsonStatue.CustomerList.customerlist;                                        
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
          //verizon Data
          this.tableData ={"status": "SUCCESS","sessionId":null,"serviceToken":null,"CustomerList":{"customerlist":[{"id":2,"customerName":"Verizon","iconPath":"/customer/verizon_icon.png","status":"Active","customerShortName":"VZN","customerDetails":[{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T13:24:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-03-18T20:58:41.000+0000","createdBy":"admin"},{"id":24,"networkTypeDetailsEntity":{"id":3,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T11:03:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"VZN-5G-VLSM","programDescription":"VLSM","status":"Active","creationDate":"2019-02-27T13:42:33.000+0000","createdBy":"superadmin"}]},{"id":3,"customerName":"AT&T","iconPath":"/customer/at&t_icon.png","status":"Active","customerShortName":"AT&T","customerDetails":[{"id":25,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T13:24:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"AT&T-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T13:43:26.000+0000","createdBy":"superadmin"},{"id":26,"networkTypeDetailsEntity":{"id":3,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T11:03:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"AT&T-5G-VLSM","programDescription":"VLSM","status":"Active","creationDate":"2019-02-27T13:44:05.000+0000","createdBy":"superadmin"}]},{"id":4,"customerName":"Sprint","iconPath":"/customer/sprint_icon.png","status":"Active","customerShortName":"SPT","customerDetails":[{"id":27,"networkTypeDetailsEntity":{"id":3,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T11:03:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"SPT-5G-MIMO","programDescription":"MIMO_NORMAL","status":"Active","creationDate":"2019-03-14T15:19:24.000+0000","createdBy":"superadmin"},{"id":28,"networkTypeDetailsEntity":{"id":3,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T11:03:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"SPT-5G-MIMO_CLWR","programDescription":"MIMO_CLWR","status":"Active","creationDate":"2019-03-14T15:19:41.000+0000","createdBy":"superadmin"}]}]}};
          
          if (this.tableData.sessionId == "408" || this.tableData.status == "Invalid User") {
            this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
          }
          if (this.tableData.status == "SUCCESS") {
            this.cList = this.tableData.CustomerList.customerlist;
            console.log(this.cList);
          } else {
            this.showLoader = false;
            this.displayModel(this.tableData.reason, "failureIcon");
          }

        }, 1000); */
        //Please Comment while checkIn
      });
  }

  onChangeOfCName(selCustName){
        if(selCustName.customerName && this.selectDuration == 'Daily'){
            this.selectedDate=new Date(this.selectedDate) ;
            console.log(selCustName.customerName,this.selectDuration);
            this.snrGoClick(event,selCustName.id,this.selectDuration,this.fromDt,this.toDt,this.selectedDate);
        } else {
            
        }
  }

  onDateChange(selectDuration) {
      if(selectDuration == 'Daily' && this.selCustName.id){
        this.selectedDate=new Date(this.selectedDate);
        this.snrGoClick(event,this.selCustName.id,selectDuration,this.fromDt,this.toDt,this.selectedDate);
      } else if(selectDuration == 'Weekly' && this.selCustName.id){
        this.snrGoClick(event,this.selCustName.id,selectDuration,this.fromDt,this.toDt,this.selectedDate);
      } else if(selectDuration == 'Periodic') {
        this.toDt = "";
        this.fromDt = "";
      }    
  }
  

  snrGoClick(event,selCustName,selectDuration,fromDate,toDate,selectedDate) {
      selectedDate = this.datePipe.transform(selectedDate,"MM/dd/yyyy");
      if(selectedDate) {
        selectedDate
      } else{
        selectedDate = null;
      }
    let fromDt,toDt;
    if(fromDate & toDate){
      fromDt = this.datePipe.transform(fromDate,"MM/dd/yyyy"); // On change Date,Update Row
      toDt = this.datePipe.transform(toDate,"MM/dd/yyyy");
    }else{
      fromDt = null;//Loading Page fromDate and toDate is null
      toDt = null;    
    }
      validator.performValidation(event, this.validationData, "create");
    setTimeout(() => {
        if (this.isValidForm(event)) {
            this.showLoader = true;
            this.snrReportsService.snrGoClick(this.sharedService.createServiceToken(),selCustName,selectDuration,fromDt,toDt,selectedDate)
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
                        this.forSupAdmin= true;
                      this.tableData = jsonStatue;
                      this.overAllData = this.tableData.totCIReportDetails.overAllData;
                      this.currentWeekData = this.tableData.totCIReportDetails.currentWeekData;
                      this.vlsmandLsmDetails = this.tableData.totCIReportDetails.vlsmandLsmDetails;
                      this.weekPeriodicDataVlsm = this.tableData.totCIReportDetails.vlsmTotDetails;
                      this.wpTotCarrierData= this.tableData.totCIReportDetails.crrierTotDetails;
                      this.wpConversionData= this.tableData.totCIReportDetails.ConverTotDetails;
                      this.commericialData = this.tableData.totCIReportDetails.commercialData;
              this.fitData = this.tableData.totCIReportDetails.FitData;
              this.sprintOverAllData = this.tableData.totCIReportDetails.overallData;
              this.sprintWeekData = this.tableData.totCIReportDetails.WeekData;
                      if(selCustName == 2) {
                        this.notForSprint = true;
                      } else if(selCustName == 4) {
                        this.notForSprint = false;
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
            
            //verizon Data daily data
            this.tableData ={"totCIReportDetails":{"overAllData":{"name":"Overall Sites Migrated successfully","migrtedSiteCount":2603,"totalCount":6400,"percenatgeOfMigrated":40.671875,"remaining":0,"objSiteList":[{"marketName":"New England","migrtedSiteCount":1824,"totalCount":4000,"percenatgeOfMigrated":45.6},{"marketName":"Upstate New York","migrtedSiteCount":779,"totalCount":2400,"percenatgeOfMigrated":32.458332}]},"currentWeekData":{"name":null,"migrtedSiteCount":0,"totalCount":0,"percenatgeOfMigrated":0,"remaining":0,"objSiteList":null},"vlsmandLsmDetails":{"totSiteLsm":973,"totSiteVlsm":1630,"lsmMigCurrentWeek":0,"vlsmMigCurrentWeek":0,"lsmRollBackCurrentWeek":0,"vlsmRollBackCurrentWeek":0},"rehomeCount":1605},"sessionId":"5576bac5","serviceToken":"90928","status":"SUCCESS"};
            //verizon Data weekly and Periodically data
            //this.tableData = {"sessionId":"2b07ac35","serviceToken":"74053","status":"SUCCESS","totCIReportDetails":{"overAllData":{"name":"Overall Sites Migrated successfully","migrtedSiteCount":"P2603","totalCount":"P6400","percenatgeOfMigrated":"P40.671875","objSiteList":[{"marketName":"PNew England","migrtedSiteCount":"P1824","totalCount":"P4000","percenatgeOfMigrated":"P45.6"},{"marketName":"PUpstate New York","migrtedSiteCount":"P779","totalCount":"P2400","percenatgeOfMigrated":"P32.458332"}]},"currentWeekData":{"name":"PTotal Sites Migrated this week","migrtedSiteCount":"P50","totalCount":"P0","percenatgeOfMigrated":"P0","objSiteList":[{"marketName":"New England","migrtedSiteCount":30,"totalCount":0,"percenatgeOfMigrated":0},{"marketName":"Upstate New York","migrtedSiteCount":20,"totalCount":0,"percenatgeOfMigrated":0}]},"vlsmTotDetails":[{"marketName":"New England","migrtedSiteCount":0,"totalCount":12,"percenatgeOfMigrated":0},{"marketName":"Upstate New York","migrtedSiteCount":0,"totalCount":13,"percenatgeOfMigrated":0}],"crrierTotDetails":[{"marketName":"New England","migrtedSiteCount":0,"totalCount":1,"percenatgeOfMigrated":0},{"marketName":"Upstate New York","migrtedSiteCount":0,"totalCount":11,"percenatgeOfMigrated":0}],"ConverTotDetails":[{"marketName":"New England","migrtedSiteCount":0,"totalCount":1,"percenatgeOfMigrated":0},{"marketName":"Upstate New York","migrtedSiteCount":0,"totalCount":11,"percenatgeOfMigrated":0}]}};
            // Sprint Data daily data
            //this.tableData = {"totCIReportDetails":{"overAllData":{"name":"Total Migration","migrtedSiteCount":148,"totalCount":1500,"percenatgeOfMigrated":9.866667,"remaining":0,"objSiteList":[{"marketName":"West","migrtedSiteCount":26,"totalCount":224,"percenatgeOfMigrated":11.607142},{"marketName":"Central","migrtedSiteCount":98,"totalCount":1252,"percenatgeOfMigrated":7.827476},{"marketName":"FIT","migrtedSiteCount":24,"totalCount":24,"percenatgeOfMigrated":100}]},"currentWeekData":{"attempted":0,"migratedCount":0,"inProgress":0,"cancelled":0}},"sessionId":"80c8b235","serviceToken":"79146","status":"SUCCESS"};
            //this.tableData ={"totCIReportDetails":{"overAllData":{"name":"Total Migration","migrtedSiteCount":5,"totalCount":1500,"percenatgeOfMigrated":0.33333334,"objSiteList":[{"marketName":"FIT","migrtedSiteCount":2,"totalCount":24,"percenatgeOfMigrated":8.333333},{"marketName":"West","migrtedSiteCount":1,"totalCount":224,"percenatgeOfMigrated":0.44642857},{"marketName":"Central","migrtedSiteCount":2,"totalCount":1252,"percenatgeOfMigrated":0.15974441}]},"currentWeekData":{"name":"Migration this week","migrtedSiteCount":0,"totalCount":0,"percenatgeOfMigrated":0,"objSiteList":[{"marketName":"FIT","migrtedSiteCount":1,"totalCount":0,"percenatgeOfMigrated":0},{"marketName":"West","migrtedSiteCount":1,"totalCount":0,"percenatgeOfMigrated":0},{"marketName":"Central","migrtedSiteCount":2,"totalCount":0,"percenatgeOfMigrated":0}]}},"sessionId":"1f99e4a1","serviceToken":"85656","status":"SUCCESS"};
            //this.tableData = {"totCIReportDetails":{"commercialData":{"migrtedSiteCount":124,"totalCount":1476,"percenatgeOfMigrated":8.266666,"remaining":1352,"inProgress":0,"cancelation":0},"FitData":{"migrtedSiteCount":0,"totalCount":24,"percenatgeOfMigrated":8.266666,"remaining":24,"inProgress":0,"cancelation":0},"overallData":{"migrtedSiteCount":124,"totalCount":1500,"percenatgeOfMigrated":0,"remaining":1375,"inProgress":1,"cancelation":0},"WeekData":{"migrtedSiteCount":23,"totalCount":0,"percenatgeOfMigrated":0,"remaining":0,"inProgress":0,"cancelation":0}},"sessionId":"20d67fa8","serviceToken":"86200","status":"SUCCESS"};
            this.forSupAdmin= true;
            if(selCustName == 2) {
                this.notForSprint = true;
              } else if(selCustName == 4) {
                this.notForSprint = false;
              }
            this.totalPages = this.tableData.pageCount;
            this.overAllData = this.tableData.totCIReportDetails.overAllData;
            this.currentWeekData = this.tableData.totCIReportDetails.currentWeekData;
            this.vlsmandLsmDetails = this.tableData.totCIReportDetails.vlsmandLsmDetails;
            this.weekPeriodicDataVlsm = this.tableData.totCIReportDetails.vlsmTotDetails;
            this.wpTotCarrierData= this.tableData.totCIReportDetails.crrierTotDetails;
            this.wpConversionData= this.tableData.totCIReportDetails.ConverTotDetails;
            this.commericialData = this.tableData.totCIReportDetails.commercialData;
              this.fitData = this.tableData.totCIReportDetails.FitData;
              this.sprintOverAllData = this.tableData.totCIReportDetails.overallData;
              this.sprintWeekData = this.tableData.totCIReportDetails.WeekData;
            if (this.tableData.sessionId == "408" || this.tableData.status == "Invalid User") {
              this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
            }
              this.tableShowHide = true;
              this.noDataVisibility = false;

          }, 1000); */
          //Please Comment while checkIn
        });
      }

      }, 0);
      
  }



  displayModel(message: string, messageType: string) {
    this.messageType = messageType;
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
    this.ngOnInit();
  }

  isValidForm(event) {
    return ($(event.target).parents("form").find(".error-border").length == 0) ? true : false;
  }

  closeAndLogout() {
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

}

