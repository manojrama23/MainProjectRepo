import { ViewContainerRef, Input, ElementRef, Renderer, Component, OnInit, ViewChild, HostListener, SystemJsNgModuleLoader } from '@angular/core';
// import { Router} from '@angular/router';
import { Router, NavigationEnd } from '@angular/router';
import { NgbModal, ModalDismissReasons } from '@ng-bootstrap/ng-bootstrap';
import { EodreportsService } from '../services/eodreports.service';
import { SharedService } from '../services/shared.service';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { filter, map, mergeMap } from 'rxjs/operators';
import { validator } from '../validation';
import { DatePipe } from '@angular/common';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import * as FileSaver from 'file-saver';
import * as $ from 'jquery';
import * as _ from 'underscore';
import { CloseScrollStrategy } from '@angular/cdk/overlay';
import { eventNames } from 'cluster';



@Component({
  selector: 'app-eodreports',
  templateUrl: './eodreports.component.html',
  styleUrls: ['./eodreports.component.scss'],
  providers: [EodreportsService]


})
export class EodreportsComponent implements OnInit {
  custList: any;
  currEditedRowVal: any;
  tableEdit: boolean = false;
  editIndex: number = -1;
  userList: any;
  regionList: any;
  editableFormArray = [];
  editableEODFormArray = [];
  addtableFormArray = [];
  searchBlock: any;
  createBlock: any;
  navigationSubscription: any;
  searchStatus: string;
  showLoader: boolean = false;
  sessionExpiredModalBlock: any; // Helps to close/open the model window
  successModalBlock: any;
  messageType: any;
  showModelMessage: boolean = false;
  modelData: any;
  message: any;
  max = new Date();
  fromDate: any;
  toDate: any;
  totalPages: any; // for pagination
  tableData: any;
  tableShowHide: boolean = false;
  searchDetails: any;
  tableDataHeight: any;
  pageRenge: any; // for pagination
  noDataVisibility: boolean = false;
  currentPage: any; // for pagination
  paginationDetails: any; // for pagination
  paginationDisabbled: boolean = false;
  TableRowLength: any; // for pagination
  pageSize: any; // for pagination
  pager: any = {}; // pager Object
  closeResult: string;
  custId: any;
  marketList: any;
  enbIdList: any;
  enbNameList: any;
  verizonData: any;
  sprintData: any;
  currentUser: any;
  selCustName: any;
  custName: any;
  sprintStartDate: any;
  sprintCompDate: any;
  forecastStartDate: any;
  ciStartTime: any;
  ciEndTime: any;
  createMarketList:any;
  createRegionList:any;
  createFeregionList:any;
  day:any;
  month:any;
  year:any;
  week:any;
  quarter:any;

  validationData: any = {
    "rules": {
      "forecastStartDate": {
        "required": true

      },
      "market": {
        "required": true,
      },
      "enbId": {
        "required": true,
      },
      "enbName": {
        "required": true,
      },
      "sprintStartDate": {
        "required": true,
      },
      "sprintCompDate": {
        "required": true,
      },
      "sprintRegion": {
        "required": true,
      },
      "sprintMarket": {
        "required": true,
      },
      "sprintenbid": {
        "required": true,
      }
    },
    "messages": {
      "forecastStartDate": {
        "required": "Forecast StartDate is required"
      },
      "market": {
        "required": "Market is required",
      },
      "enbId": {
        "required": "NE ID is required",
      },
      "enbName": {
        "required": "NE NAME is required",
      },
      "sprintStartDate": {
        "required": "Start Date is required",
      },
      "sprintCompDate": {
        "required": "Comp Date is required",
      },
      "sprintRegion": {
        "required": "Region is required",
      },
      "sprintMarket": {
        "required": "Market is required",
      },
      "sprintenbid": {
        "required": "NE ID is required",
      }
    }
  };


  @ViewChild('searchTab') searchTabRef: ElementRef;
  @ViewChild('confirmModal') confirmModalRef: ElementRef;
  @ViewChild('bluePrintForm') bluePrintForm;
  @ViewChild('searchForm') searchForm;
  @ViewChild('createVerizonEodReports') createVerizonForm;
  @ViewChild('createTab') createTabRef: ElementRef;
  @ViewChild('sessionExpiredModal') sessionExpiredModalRef: ElementRef;
  @ViewChild('successModal') successModalRef: ElementRef;
  @ViewChild('neversionContainer', { read: ViewContainerRef }) neversionContainer;
  @ViewChild('filePost') filePostRef: ElementRef;


  constructor(
    private element: ElementRef,
    private eodReportsService: EodreportsService,
    private router: Router,
    private modalService: NgbModal,
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


    let searchCrtra = { "enbId": "", "siteConfigType": "", "searchStartDate": "", "searchEndDate": "" };
    this.searchDetails = searchCrtra;
    this.searchBlock = true;
    this.createBlock = false;
    this.totalPages = 1;
    this.currentPage = 1;
    this.TableRowLength = 10;
    this.pageSize = 10;
    this.selCustName = "";
    this.tableShowHide = false;
    this.showLoader = false;
    this.setMenuHighlight("search");
    //console.log(sessionStorage.loginDetails);
    this.searchStatus = 'load';
    let paginationDetails = {
      "count": parseInt(this.TableRowLength, 10),
      "page": this.currentPage
    };
    this.paginationDetails = paginationDetails;
    this.currentUser = JSON.parse(sessionStorage.loginDetails).userGroup;
    if (this.currentUser != "Super Administrator") {

      this.custId = JSON.parse(sessionStorage.selectedCustomerList).id;
      this.selCustName = JSON.parse(sessionStorage.selectedCustomerList).id;
      //this.custId=2;
      this.getEodReportsDetails();
      this.showLoader = true;
    }
    this.getCustomerList();

  }




  ngOnDestroy() {
    // avoid memory leaks here by cleaning up after ourselves. If we  
    // don't then we will continue to run our ngOnInit()   
    // method on every navigationEnd event.
    if (this.navigationSubscription) {
      this.navigationSubscription.unsubscribe();
    }
  }

  searchTabBind() {
    this.sprintStartDate = "";
    this.sprintCompDate = "";
    this.forecastStartDate = "";
    this.ciStartTime = "";
    this.ciEndTime = "";
    this.editableEODFormArray = [];
    this.searchBlock = true;
    this.createBlock = false;
    this.searchStatus = 'load';
    this.setMenuHighlight("search");
    this.currentPage = 1;
    if (this.currentUser != "Super Administrator") {

      this.custId = JSON.parse(sessionStorage.selectedCustomerList).id;
      //this.custId=2;
      this.getEodReportsDetails();
      // this.showLoader = true;
    }
    else {
      if (this.selCustName != "")
        this.getEodReportsDetails();

    }

    this.bluePrintForm.nativeElement.reset();


  }


  createTabBind() {
    this.editableEODFormArray = [];
    this.searchBlock = false;
    this.createBlock = true;
    this.setMenuHighlight("create");

  }


  clearSearchFrom() {
    this.bluePrintForm.nativeElement.reset();  
}


  getEodReportsDetails() {

    this.tableShowHide = false;
    this.editIndex = -1;
    this.showLoader = true;
    $("#dataWrapper").find(".scrollBody").scrollLeft(0);
    this.eodReportsService.getEodReportsDetails(this.searchStatus, this.searchDetails, this.sharedService.createServiceToken(), this.paginationDetails, this.custId)
      .subscribe(
        data => {
          setTimeout(() => {
            if (this.custId == 2) {
              this.editableEODFormArray = [];
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


                    this.showLoader = false;
                    this.marketList = jsonStatue.market;
                    this.enbIdList = jsonStatue.enodebId;
                    this.enbNameList = jsonStatue.enodebName;
                    this.totalPages = jsonStatue.pageCount;
                    this.userList = jsonStatue.username;
                    this.createMarketList = jsonStatue.comboBoxListDetails ? jsonStatue.comboBoxListDetails.market : [];

                    let pageCount = [];
                    for (var i = 1; i <= jsonStatue.pageCount; i++) {
                      pageCount.push(i);
                    }
                    this.pageRenge = pageCount;
                    this.pager = this.getPager(this.totalPages * this.pageSize, this.currentPage, this.pageRenge);

                    if (jsonStatue.eodVerizonModelList.length == 0) {
                      this.tableShowHide = false;
                      this.noDataVisibility = true;
                    } else {
                      this.verizonData = jsonStatue.eodVerizonModelList;
                      this.tableShowHide = true;
                      this.noDataVisibility = false;
                      setTimeout(() => {
                        let tableWidth = document.getElementById('eodVerizonModelList').scrollWidth;
                        $(".scrollBody table").css("min-width", (tableWidth) + "px");
                        $(".scrollHead table").css("width", tableWidth + "px");


                        $(".scrollBody").on("scroll", function (event) {
                          $(".formEditRow #formWrapper").css("margin-left", (event.target.scrollLeft - 5) + "px");
                          $(".form-control-fixed").css("right", (event.target.scrollLeft * -1) + "px");
                          $(".scrollHead table").css("margin-left", (event.target.scrollLeft * -1) + "px");
                        });
                        $(".scrollBody").css("max-height", (this.tableDataHeight - 10) + "px");

                      }, 0);
                    }

                  } else {
                    this.showLoader = false;
                    this.displayModel(jsonStatue.reason, "failureIcon");
                  }

                }
              }
            }
            else if (this.custId == 4) {
              this.editableEODFormArray = [];
              let jsonStatue = data.json();

              this.marketList = jsonStatue.market;
              this.regionList = jsonStatue.region;
              this.createMarketList = jsonStatue.marketDetailsList ? jsonStatue.marketDetailsList.market : [];
              this.createRegionList = jsonStatue.regionDetailsList ? jsonStatue.regionDetailsList.region : [];

              //this.enbIdList=this.tableData.enodebId;
              //this.enbNameList=this.tableData.enodebName; 
              if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {

                this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });

              } else {

                if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {
                  if (jsonStatue.status == "SUCCESS") {
                    this.showLoader = false;
                    this.tableData = jsonStatue;
                    this.totalPages = this.tableData.pageCount;



                    let pageCount = [];
                    for (var i = 1; i <= this.tableData.pageCount; i++) {
                      pageCount.push(i);
                    }
                    this.pageRenge = pageCount;
                    this.pager = this.getPager(this.totalPages * this.pageSize, this.currentPage, this.pageRenge);

                    if (this.tableData.eodSprintEntity.length == 0) {
                      this.tableShowHide = false;
                      this.noDataVisibility = true;
                    } else {
                      this.sprintData = this.tableData.eodSprintEntity;
                      this.tableShowHide = true;
                      this.noDataVisibility = false;
                      setTimeout(() => {
                        let tableWidth = document.getElementById('eodSprintEntity').scrollWidth;
                        $(".scrollBody table").css("min-width", (tableWidth) + "px");
                        $(".scrollHead table").css("width", tableWidth + "px");


                        $(".scrollBody").on("scroll", function (event) {
                          $(".formEditRow #formWrapper").css("margin-left", (event.target.scrollLeft - 5) + "px");
                          $(".form-control-fixed").css("right", (event.target.scrollLeft * -1) + "px");
                          $(".scrollHead table").css("margin-left", (event.target.scrollLeft * -1) + "px");
                        });
                        $(".scrollBody").css("max-height", (this.tableDataHeight - 10) + "px");

                      }, 0);
                    }

                  } else {
                    this.showLoader = false;
                    this.displayModel(jsonStatue.reason, "failureIcon");
                  }

                }
              }

            } else {
              // Other Customers 
              this.noDataVisibility = true;
              this.showLoader = false;

            }


          }, 1000);
        },
        error => {
          //Please Comment while checkIn
          /* setTimeout(() => {
            if (this.custId == 2) {
              this.editableEODFormArray = [];
              this.showLoader = false;

              this.tableData = { "market": ["New England", "England", "asdasd", "New England sreeraj"], "enodebName": ["1234", "TEST", "sdsd"], "eodVerizonModelList": [{ "id": 1, "forecastStartDate": "2019-03-25", "forecastEndDate": null, "compDate": null, "compEndDate": null, "compStartDate": null, "market": "New England", "enbId": "71707", "enbName": "TEST", "growRequest": "NO", "growCompleted": "", "ciqPresent": "YES", "envCompleted": "YES", "standardNonStandard": "STANDARD", "carriers": "", "uda": "", "softwareLevels": "", "feArrivalTime": "", "ciStartTime": "", "dtHandoff": "", "ciEndTime": "", "startTime": null, "canRollComp": "CANCELLED", "traffic": "", "alarmPresent": "", "ciEngineer": "", "ft": "", "dt": "", "notes": "", "column1": null, "totalLookup": null, "ranEngineer": null, "status": null, "revisit": null, "vlsm": null, "endTime": null, "comments": null, "issue": null, "ci": null, "nonCi": null, "ald": null, "week": null, "month": null, "status2": null, "quarter": null, "year": null, "rule1": null, "rule2": null, "day": null }, { "id": 4, "forecastStartDate": "2019-03-24", "forecastEndDate": null, "compDate": "2019-03-24", "compEndDate": null, "compStartDate": null, "market": "asdasd", "enbId": "sdsd", "enbName": "sdsd", "growRequest": "", "growCompleted": "", "ciqPresent": "", "envCompleted": null, "standardNonStandard": null, "carriers": null, "uda": null, "softwareLevels": null, "feArrivalTime": null, "ciStartTime": null, "dtHandoff": null, "ciEndTime": null, "startTime": null, "canRollComp": null, "traffic": null, "alarmPresent": null, "ciEngineer": null, "ft": null, "dt": null, "notes": null, "column1": null, "totalLookup": null, "ranEngineer": null, "status": null, "revisit": null, "vlsm": null, "endTime": null, "comments": null, "issue": null, "ci": null, "nonCi": null, "ald": null, "week": null, "month": null, "status2": null, "quarter": null, "year": null, "rule1": null, "rule2": null, "day": null }, { "id": 5, "forecastStartDate": "2019-03-27", "forecastEndDate": null, "compDate": null, "compEndDate": null, "compStartDate": null, "market": "New England", "enbId": "71707", "enbName": "TEST", "growRequest": "NO", "growCompleted": "", "ciqPresent": "YES", "envCompleted": "YES", "standardNonStandard": "STANDARD", "carriers": "", "uda": "", "softwareLevels": "", "feArrivalTime": "", "ciStartTime": "", "dtHandoff": "", "ciEndTime": "", "startTime": null, "canRollComp": "CANCELLED", "traffic": "", "alarmPresent": "", "ciEngineer": "", "ft": "", "dt": "", "notes": "", "column1": null, "totalLookup": null, "ranEngineer": null, "status": null, "revisit": null, "vlsm": null, "endTime": null, "comments": null, "issue": null, "ci": null, "nonCi": null, "ald": null, "week": null, "month": null, "status2": null, "quarter": null, "year": null, "rule1": null, "rule2": null, "day": null }, { "id": 6, "forecastStartDate": "2019-03-27", "forecastEndDate": null, "compDate": null, "compEndDate": null, "compStartDate": null, "market": "New England", "enbId": "71707", "enbName": "TEST", "growRequest": "NO", "growCompleted": "", "ciqPresent": "YES", "envCompleted": "YES", "standardNonStandard": "STANDARD", "carriers": "", "uda": "", "softwareLevels": "", "feArrivalTime": "", "ciStartTime": "", "dtHandoff": "", "ciEndTime": "", "startTime": null, "canRollComp": "CANCELLED", "traffic": "", "alarmPresent": "", "ciEngineer": "", "ft": "", "dt": "", "notes": "", "column1": null, "totalLookup": null, "ranEngineer": null, "status": null, "revisit": null, "vlsm": null, "endTime": null, "comments": null, "issue": null, "ci": null, "nonCi": null, "ald": null, "week": null, "month": null, "status2": null, "quarter": null, "year": null, "rule1": null, "rule2": null, "day": null }, { "id": 7, "forecastStartDate": "2019-03-27", "forecastEndDate": null, "compDate": null, "compEndDate": null, "compStartDate": null, "market": "asdasd", "enbId": "sdsd", "enbName": "sdsd", "growRequest": "", "growCompleted": "", "ciqPresent": "", "envCompleted": "", "standardNonStandard": "", "carriers": "", "uda": "", "softwareLevels": "", "feArrivalTime": "", "ciStartTime": "", "dtHandoff": "", "ciEndTime": "", "startTime": null, "canRollComp": "", "traffic": "", "alarmPresent": "", "ciEngineer": "", "ft": "", "dt": "", "notes": "", "column1": null, "totalLookup": null, "ranEngineer": null, "status": null, "revisit": null, "vlsm": null, "endTime": null, "comments": null, "issue": null, "ci": null, "nonCi": null, "ald": null, "week": null, "month": null, "status2": null, "quarter": null, "year": null, "rule1": null, "rule2": null, "day": null }, { "id": 8, "forecastStartDate": "2019-03-27", "forecastEndDate": null, "compDate": null, "compEndDate": null, "compStartDate": null, "market": "New England", "enbId": "71707", "enbName": "TEST", "growRequest": "NO", "growCompleted": "", "ciqPresent": "YES", "envCompleted": "YES", "standardNonStandard": "STANDARD", "carriers": "", "uda": "", "softwareLevels": "", "feArrivalTime": "", "ciStartTime": "", "dtHandoff": "", "ciEndTime": "", "startTime": null, "canRollComp": "CANCELLED", "traffic": "", "alarmPresent": "", "ciEngineer": "", "ft": "", "dt": "", "notes": "", "column1": null, "totalLookup": null, "ranEngineer": null, "status": null, "revisit": null, "vlsm": null, "endTime": null, "comments": null, "issue": null, "ci": null, "nonCi": null, "ald": null, "week": null, "month": null, "status2": null, "quarter": null, "year": null, "rule1": null, "rule2": null, "day": null }, { "id": 9, "forecastStartDate": "2019-03-27", "forecastEndDate": null, "compDate": null, "compEndDate": null, "compStartDate": null, "market": "New England sreeraj", "enbId": "71707", "enbName": "TEST", "growRequest": "NO", "growCompleted": "", "ciqPresent": "YES", "envCompleted": "YES", "standardNonStandard": "STANDARD", "carriers": "", "uda": "", "softwareLevels": "", "feArrivalTime": "", "ciStartTime": "", "dtHandoff": "", "ciEndTime": "", "startTime": null, "canRollComp": "CANCELLED", "traffic": null, "alarmPresent": null, "ciEngineer": null, "ft": null, "dt": null, "notes": null, "column1": null, "totalLookup": null, "ranEngineer": null, "status": null, "revisit": null, "vlsm": null, "endTime": null, "comments": null, "issue": null, "ci": null, "nonCi": null, "ald": null, "week": null, "month": null, "status2": null, "quarter": null, "year": null, "rule1": null, "rule2": null, "day": null }, { "id": 10, "forecastStartDate": "2019-03-27", "forecastEndDate": null, "compDate": null, "compEndDate": null, "compStartDate": null, "market": null, "enbId": null, "enbName": null, "growRequest": null, "growCompleted": null, "ciqPresent": null, "envCompleted": null, "standardNonStandard": null, "carriers": null, "uda": null, "softwareLevels": null, "feArrivalTime": null, "ciStartTime": null, "dtHandoff": null, "ciEndTime": null, "startTime": null, "canRollComp": null, "traffic": null, "alarmPresent": null, "ciEngineer": null, "ft": null, "dt": null, "notes": null, "column1": null, "totalLookup": null, "ranEngineer": null, "status": null, "revisit": null, "vlsm": null, "endTime": null, "comments": null, "issue": null, "ci": null, "nonCi": null, "ald": null, "week": null, "month": null, "status2": null, "quarter": null, "year": null, "rule1": null, "rule2": null, "day": null }, { "id": 11, "forecastStartDate": "2019-03-27", "forecastEndDate": null, "compDate": null, "compEndDate": null, "compStartDate": null, "market": null, "enbId": null, "enbName": null, "growRequest": null, "growCompleted": null, "ciqPresent": null, "envCompleted": null, "standardNonStandard": null, "carriers": null, "uda": null, "softwareLevels": null, "feArrivalTime": null, "ciStartTime": null, "dtHandoff": null, "ciEndTime": null, "startTime": null, "canRollComp": null, "traffic": null, "alarmPresent": null, "ciEngineer": null, "ft": null, "dt": null, "notes": null, "column1": null, "totalLookup": null, "ranEngineer": null, "status": null, "revisit": null, "vlsm": null, "endTime": null, "comments": null, "issue": null, "ci": null, "nonCi": null, "ald": null, "week": null, "month": null, "status2": null, "quarter": null, "year": null, "rule1": null, "rule2": null, "day": null }, { "id": 12, "forecastStartDate": "2019-03-28", "forecastEndDate": null, "compDate": "2019-03-28", "compEndDate": null, "compStartDate": null, "market": null, "enbId": null, "enbName": null, "growRequest": null, "growCompleted": null, "ciqPresent": null, "envCompleted": null, "standardNonStandard": null, "carriers": null, "uda": null, "softwareLevels": null, "feArrivalTime": null, "ciStartTime": null, "dtHandoff": null, "ciEndTime": null, "startTime": null, "canRollComp": null, "traffic": null, "alarmPresent": null, "ciEngineer": null, "ft": null, "dt": null, "notes": null, "column1": null, "totalLookup": null, "ranEngineer": null, "status": null, "revisit": null, "vlsm": null, "endTime": null, "comments": null, "issue": null, "ci": null, "nonCi": null, "ald": null, "week": null, "month": null, "status2": null, "quarter": null, "year": null, "rule1": null, "rule2": null, "day": null }], "pageCount": 3, "comboBoxListDetails": { "market": ["Upstate New York", "New England"], "region": null, "feRegion": null }, "enodebId": ["sdsd", "71707", "71071"], "sessionId": "d8d5c575", "serviceToken": "98604", "username": ["comm1", "admin", "supriya"], "status": "SUCCESS" };

              this.totalPages = this.tableData.pageCount;
              this.marketList = this.tableData.market;
              this.enbIdList = this.tableData.enodebId;
              this.enbNameList = this.tableData.enodebName;
              this.userList = this.tableData.username;
              this.createMarketList = this.tableData.comboBoxListDetails.market;

              //console.log(this.marketList);
              //this.custId=this.tableData.
              let pageCount = [];
              for (var i = 1; i <= this.tableData.pageCount; i++) {
                pageCount.push(i);
              }
              this.pageRenge = pageCount;
              this.pager = this.getPager(this.totalPages * this.pageSize, this.currentPage, this.pageRenge);
              if (this.tableData.sessionId == "408" || this.tableData.status == "Invalid User") {
                this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
              }
              if (this.tableData.eodVerizonModelList.length == 0) {
                this.tableShowHide = false;
                this.noDataVisibility = true;
              } else {
                this.verizonData = this.tableData.eodVerizonModelList;
                this.tableShowHide = true;
                this.noDataVisibility = false;
                setTimeout(() => {
                  let tableWidth = document.getElementById('eodVerizonModelList').scrollWidth;

                  $(".scrollBody table").css("min-width", (tableWidth) + "px");
                  $(".scrollHead table").css("width", tableWidth + "px");


                  $(".scrollBody").on("scroll", function (event) {
                    $(".formEditRow #formWrapper").css("margin-left", (event.target.scrollLeft - 5) + "px");
                    $(".form-control-fixed").css("right", (event.target.scrollLeft * -1) + "px");
                    $(".scrollHead table").css("margin-left", (event.target.scrollLeft * -1) + "px");
                  });
                  //$(".scrollBody").css("max-height",(this.windowScreenHieght/2) + "px");
                  $(".scrollBody").css("max-height", (this.tableDataHeight - 10) + "px");



                }, 0);

              }
            }
            else if (this.custId == 4) {
              this.editableEODFormArray = [];
              this.showLoader = false;

              this.tableData = { "market": ["", "Milwaukee", "West Washington", "South Bay", "Central Illinois", "Ft. Wayne / South Bend", "Oregon/SW Washington", "Chicago", "Cincinnati", "Oregon / SW Washington", "West Michigan", "Colorado", "FIT Chicago", "Columbus", "East Michigan", "Indianapolis", "Pittsburgh"], "pageCount": 23, "marketDetailsList": { "market": ["buffalo", "Central Illinois", "Central Iowa", "Central Pennsylvania", "Chicago", "Cincinnati", "Cleveland", "Columbus", "East Michigan", "Ft. Wayne/South Bend", "Indianpolis", "Milwaukee", "North Wisconsin", "Pittsburgh", "Toledo", "West Iowa/Nebraska", "West Michigan", "Western Pennsylvania"], "region": null, "feRegion": null }, "regionDetailsList": { "market": null, "region": ["Central", "West"], "feRegion": null }, "eodSprintEntity": [{ "id": 1051, "region": "Central", "market": "Chicago", "cascade": "CH03XC022", "ciEngineerNight": null, "bridgeOne": null, "feRegion": "FECentral", "feNight": null, "ciEngineerDay": null, "bridge": null, "feDay": null, "notes": null, "status": null, "startDate": "2019-01-11", "startEndDate": null, "day": null, "week": null, "month": null, "qtr": null, "year": null, "type": null, "siteRevisit": null, "goldenCluster": null, "actualMigrationStartDate": null, "compDate": "2019-02-11", "compEndDate": null, "enbId": "516265", "fiveG": null, "typeOne": null, "tvw": null, "currentSoftware": "", "scriptsRan": null, "dspImplemented": "", "ciEngineerOne": "Daron Sy", "ciStartTimeOne": "0.0625", "ciEndTimeOne": "0.3333333333", "feOne": "Latoya", "feContactInfoOne": "", "feArrivalTimeOne": "2:20:00 AM", "ciEngineerTwo": "Vasudha Vanga", "ciStartTimeTwo": "8:00:00 AM", "ciEndTimeTwo": null, "feTwo": "", "feContactInfoTwo": "", "feArrivalTimeTwo": "", "gc": "CCSI", "gcArrivalTime": "5:00:00 AM", "putTool": "No", "scriptErrors": null, "reasonCode": null, "ciIssue": null, "nonCiIssue": null, "engineerOneNotes": null, "engineerTwoNotes": null, "circuitbreakerStart": null, "circuitbreakerEnd": null, "alphaStartTime": null, "alphaEndTime": null, "betaStartTime": null, "betaEndTime": null, "gammaStartTime": null, "gammaEndTime": null }, { "id": 1052, "region": "Central", "market": "Chicago", "cascade": "CH03XC010", "ciEngineerNight": null, "bridgeOne": null, "feRegion": "FECentral", "feNight": null, "ciEngineerDay": null, "bridge": null, "feDay": null, "notes": null, "status": null, "startDate": "2019-03-11", "startEndDate": null, "day": null, "week": null, "month": null, "qtr": null, "year": null, "type": null, "siteRevisit": null, "goldenCluster": null, "actualMigrationStartDate": null, "compDate": "2019-03-11", "compEndDate": null, "enbId": "516260", "fiveG": null, "typeOne": null, "tvw": null, "currentSoftware": "", "scriptsRan": null, "dspImplemented": "", "ciEngineerOne": "Thang Pham", "ciStartTimeOne": "0.0416666667", "ciEndTimeOne": "0.3125", "feOne": "LaToya Norwood", "feContactInfoOne": "", "feArrivalTimeOne": "1:45:00 AM", "ciEngineerTwo": "Vasudha Vanga", "ciStartTimeTwo": "7:30:00 AM", "ciEndTimeTwo": null, "feTwo": "", "feContactInfoTwo": "", "feArrivalTimeTwo": "", "gc": "CCSI", "gcArrivalTime": "5:45:00 AM", "putTool": "No", "scriptErrors": null, "reasonCode": null, "ciIssue": null, "nonCiIssue": null, "engineerOneNotes": null, "engineerTwoNotes": null, "circuitbreakerStart": null, "circuitbreakerEnd": null, "alphaStartTime": null, "alphaEndTime": null, "betaStartTime": null, "betaEndTime": null, "gammaStartTime": null, "gammaEndTime": null }, { "id": 1053, "region": "Central", "market": "Chicago", "cascade": "CH13XC048", "ciEngineerNight": null, "bridgeOne": null, "feRegion": "FECentral", "feNight": null, "ciEngineerDay": null, "bridge": null, "feDay": null, "notes": null, "status": null, "startDate": "2019-03-11", "startEndDate": null, "day": null, "week": null, "month": null, "qtr": null, "year": null, "type": null, "siteRevisit": null, "goldenCluster": null, "actualMigrationStartDate": null, "compDate": "2019-03-11", "compEndDate": null, "enbId": "516792", "fiveG": null, "typeOne": null, "tvw": null, "currentSoftware": "", "scriptsRan": null, "dspImplemented": "", "ciEngineerOne": "Matthew Hermez", "ciStartTimeOne": "0.0416666667", "ciEndTimeOne": "0.3263888889", "feOne": "Raul Garcia", "feContactInfoOne": "", "feArrivalTimeOne": "1:09:00 AM", "ciEngineerTwo": "Mark Serverson", "ciStartTimeTwo": "7:50:00 AM", "ciEndTimeTwo": null, "feTwo": "", "feContactInfoTwo": "", "feArrivalTimeTwo": "", "gc": "CCSI", "gcArrivalTime": "5:22:00 AM", "putTool": "No", "scriptErrors": null, "reasonCode": null, "ciIssue": null, "nonCiIssue": null, "engineerOneNotes": null, "engineerTwoNotes": null, "circuitbreakerStart": null, "circuitbreakerEnd": null, "alphaStartTime": null, "alphaEndTime": null, "betaStartTime": null, "betaEndTime": null, "gammaStartTime": null, "gammaEndTime": null }, { "id": 1054, "region": "Central", "market": "Chicago", "cascade": "CH03XC028", "ciEngineerNight": null, "bridgeOne": null, "feRegion": "FECentral", "feNight": null, "ciEngineerDay": null, "bridge": null, "feDay": null, "notes": null, "status": null, "startDate": "2019-04-11", "startEndDate": null, "day": null, "week": null, "month": null, "qtr": null, "year": null, "type": null, "siteRevisit": null, "goldenCluster": null, "actualMigrationStartDate": null, "compDate": "2019-04-11", "compEndDate": null, "enbId": "516267", "fiveG": null, "typeOne": null, "tvw": null, "currentSoftware": "", "scriptsRan": null, "dspImplemented": "", "ciEngineerOne": "Daron Sy", "ciStartTimeOne": "0", "ciEndTimeOne": "0.3333333333", "feOne": "Kyle Olejinzcak", "feContactInfoOne": "", "feArrivalTimeOne": "1:15:00 AM", "ciEngineerTwo": "Vasudha Vanga", "ciStartTimeTwo": "8:00:00 AM", "ciEndTimeTwo": null, "feTwo": "", "feContactInfoTwo": "", "feArrivalTimeTwo": "", "gc": "CCSI", "gcArrivalTime": "5:00:00 AM", "putTool": "No", "scriptErrors": null, "reasonCode": null, "ciIssue": null, "nonCiIssue": null, "engineerOneNotes": null, "engineerTwoNotes": null, "circuitbreakerStart": null, "circuitbreakerEnd": null, "alphaStartTime": null, "alphaEndTime": null, "betaStartTime": null, "betaEndTime": null, "gammaStartTime": null, "gammaEndTime": null }, { "id": 1055, "region": "Central", "market": "Chicago", "cascade": "CH03XC244", "ciEngineerNight": null, "bridgeOne": null, "feRegion": "FECentral", "feNight": null, "ciEngineerDay": null, "bridge": null, "feDay": null, "notes": null, "status": null, "startDate": "2019-08-11", "startEndDate": null, "day": null, "week": null, "month": null, "qtr": null, "year": null, "type": null, "siteRevisit": null, "goldenCluster": null, "actualMigrationStartDate": null, "compDate": "2019-08-11", "compEndDate": null, "enbId": "516383", "fiveG": null, "typeOne": null, "tvw": null, "currentSoftware": "", "scriptsRan": null, "dspImplemented": "", "ciEngineerOne": "Daron Sy", "ciStartTimeOne": "0", "ciEndTimeOne": "0.3333333333", "feOne": "Mayowa Adeoye", "feContactInfoOne": "", "feArrivalTimeOne": "1:00:00 AM", "ciEngineerTwo": "Matthew Hermez", "ciStartTimeTwo": "8:00:00 AM", "ciEndTimeTwo": null, "feTwo": "", "feContactInfoTwo": "", "feArrivalTimeTwo": "", "gc": "CCSI", "gcArrivalTime": "5:00:00 AM", "putTool": "No", "scriptErrors": null, "reasonCode": null, "ciIssue": null, "nonCiIssue": null, "engineerOneNotes": null, "engineerTwoNotes": null, "circuitbreakerStart": null, "circuitbreakerEnd": null, "alphaStartTime": null, "alphaEndTime": null, "betaStartTime": null, "betaEndTime": null, "gammaStartTime": null, "gammaEndTime": null }, { "id": 1056, "region": "Central", "market": "Chicago", "cascade": "CH54XC961", "ciEngineerNight": null, "bridgeOne": null, "feRegion": "FECentral", "feNight": null, "ciEngineerDay": null, "bridge": null, "feDay": null, "notes": null, "status": null, "startDate": "2019-08-11", "startEndDate": null, "day": null, "week": null, "month": null, "qtr": null, "year": null, "type": null, "siteRevisit": null, "goldenCluster": null, "actualMigrationStartDate": null, "compDate": "2019-08-11", "compEndDate": null, "enbId": "516937", "fiveG": null, "typeOne": null, "tvw": null, "currentSoftware": "", "scriptsRan": null, "dspImplemented": "", "ciEngineerOne": "Asad Inayatullah", "ciStartTimeOne": "0.0694444444", "ciEndTimeOne": "0.3645833333", "feOne": "Frank Douse", "feContactInfoOne": "", "feArrivalTimeOne": "1:35:00 AM", "ciEngineerTwo": "Vasudha Vanga", "ciStartTimeTwo": "8:45:00 AM", "ciEndTimeTwo": null, "feTwo": "", "feContactInfoTwo": "", "feArrivalTimeTwo": "", "gc": "CCSI", "gcArrivalTime": "5:00:00 AM", "putTool": "No", "scriptErrors": null, "reasonCode": null, "ciIssue": null, "nonCiIssue": null, "engineerOneNotes": null, "engineerTwoNotes": null, "circuitbreakerStart": null, "circuitbreakerEnd": null, "alphaStartTime": null, "alphaEndTime": null, "betaStartTime": null, "betaEndTime": null, "gammaStartTime": null, "gammaEndTime": null }, { "id": 1057, "region": "Central", "market": "Chicago", "cascade": "CH03XC043", "ciEngineerNight": null, "bridgeOne": null, "feRegion": "FECentral", "feNight": null, "ciEngineerDay": null, "bridge": null, "feDay": null, "notes": null, "status": null, "startDate": "2020-02-11", "startEndDate": null, "day": null, "week": null, "month": null, "qtr": null, "year": null, "type": null, "siteRevisit": null, "goldenCluster": null, "actualMigrationStartDate": null, "compDate": "2020-03-11", "compEndDate": null, "enbId": "516271", "fiveG": null, "typeOne": null, "tvw": null, "currentSoftware": "", "scriptsRan": null, "dspImplemented": "", "ciEngineerOne": "Asad Inayatullah", "ciStartTimeOne": "0.0416666667", "ciEndTimeOne": "0.3291666667", "feOne": "Kyle Olejinzcak", "feContactInfoOne": "", "feArrivalTimeOne": "1:00:00 AM", "ciEngineerTwo": "Matthew Hermez", "ciStartTimeTwo": "7:55:00 AM", "ciEndTimeTwo": null, "feTwo": "", "feContactInfoTwo": "", "feArrivalTimeTwo": "", "gc": "CCSI", "gcArrivalTime": "5:00:00 AM", "putTool": "No", "scriptErrors": null, "reasonCode": null, "ciIssue": null, "nonCiIssue": null, "engineerOneNotes": null, "engineerTwoNotes": null, "circuitbreakerStart": null, "circuitbreakerEnd": null, "alphaStartTime": null, "alphaEndTime": null, "betaStartTime": null, "betaEndTime": null, "gammaStartTime": null, "gammaEndTime": null }, { "id": 1058, "region": "Central", "market": "Chicago", "cascade": "CH03XC013", "ciEngineerNight": null, "bridgeOne": null, "feRegion": "FECentral", "feNight": null, "ciEngineerDay": null, "bridge": null, "feDay": null, "notes": null, "status": null, "startDate": "2020-03-11", "startEndDate": null, "day": null, "week": null, "month": null, "qtr": null, "year": null, "type": null, "siteRevisit": null, "goldenCluster": null, "actualMigrationStartDate": null, "compDate": null, "compEndDate": null, "enbId": "516261", "fiveG": null, "typeOne": null, "tvw": null, "currentSoftware": "", "scriptsRan": null, "dspImplemented": "", "ciEngineerOne": "Daron Sy", "ciStartTimeOne": "0.9166666667", "ciEndTimeOne": "0.1041666667", "feOne": "Eugenio Pena", "feContactInfoOne": "", "feArrivalTimeOne": "1:00:00 AM", "ciEngineerTwo": "Vasudha Vanga", "ciStartTimeTwo": "", "ciEndTimeTwo": null, "feTwo": "", "feContactInfoTwo": "", "feArrivalTimeTwo": "", "gc": "CCSI", "gcArrivalTime": "", "putTool": "No", "scriptErrors": null, "reasonCode": null, "ciIssue": null, "nonCiIssue": null, "engineerOneNotes": null, "engineerTwoNotes": null, "circuitbreakerStart": null, "circuitbreakerEnd": null, "alphaStartTime": null, "alphaEndTime": null, "betaStartTime": null, "betaEndTime": null, "gammaStartTime": null, "gammaEndTime": null }, { "id": 1059, "region": "Central", "market": "Chicago", "cascade": "CH13XC052", "ciEngineerNight": null, "bridgeOne": null, "feRegion": "FECentral", "feNight": null, "ciEngineerDay": null, "bridge": null, "feDay": null, "notes": null, "status": null, "startDate": "2020-04-11", "startEndDate": null, "day": null, "week": null, "month": null, "qtr": null, "year": null, "type": null, "siteRevisit": null, "goldenCluster": null, "actualMigrationStartDate": null, "compDate": "2020-05-11", "compEndDate": null, "enbId": "516796", "fiveG": null, "typeOne": null, "tvw": null, "currentSoftware": "", "scriptsRan": null, "dspImplemented": "", "ciEngineerOne": "Zaid Abdulqader", "ciStartTimeOne": "0.9166666667", "ciEndTimeOne": "0.3222222222", "feOne": "Alfaonso Hemandez", "feContactInfoOne": "925-858-4322", "feArrivalTimeOne": "12:35:00 PM", "ciEngineerTwo": "Vasudha Vanga", "ciStartTimeTwo": "7:45:00 AM", "ciEndTimeTwo": null, "feTwo": "", "feContactInfoTwo": "", "feArrivalTimeTwo": "", "gc": "CCSI", "gcArrivalTime": "3:30:00 AM", "putTool": "No", "scriptErrors": null, "reasonCode": null, "ciIssue": null, "nonCiIssue": null, "engineerOneNotes": null, "engineerTwoNotes": null, "circuitbreakerStart": null, "circuitbreakerEnd": null, "alphaStartTime": null, "alphaEndTime": null, "betaStartTime": null, "betaEndTime": null, "gammaStartTime": null, "gammaEndTime": null }, { "id": 1060, "region": "Central", "market": "Chicago", "cascade": "CH54XC962", "ciEngineerNight": null, "bridgeOne": null, "feRegion": "FECentral", "feNight": null, "ciEngineerDay": null, "bridge": null, "feDay": null, "notes": null, "status": null, "startDate": "2020-04-11", "startEndDate": null, "day": null, "week": null, "month": null, "qtr": null, "year": null, "type": null, "siteRevisit": null, "goldenCluster": null, "actualMigrationStartDate": null, "compDate": "2020-04-11", "compEndDate": null, "enbId": "516938", "fiveG": null, "typeOne": null, "tvw": null, "currentSoftware": "", "scriptsRan": null, "dspImplemented": "", "ciEngineerOne": "Daron Sy", "ciStartTimeOne": "0.9166666667", "ciEndTimeOne": "0.3333333333", "feOne": "Mayowa Adeoye", "feContactInfoOne": "", "feArrivalTimeOne": "1:15:00 AM", "ciEngineerTwo": "Mark Serverson", "ciStartTimeTwo": "8:00:00 AM", "ciEndTimeTwo": null, "feTwo": "", "feContactInfoTwo": "", "feArrivalTimeTwo": "", "gc": "CCSI", "gcArrivalTime": "5:00:00 AM", "putTool": "No", "scriptErrors": null, "reasonCode": null, "ciIssue": null, "nonCiIssue": null, "engineerOneNotes": null, "engineerTwoNotes": null, "circuitbreakerStart": null, "circuitbreakerEnd": null, "alphaStartTime": null, "alphaEndTime": null, "betaStartTime": null, "betaEndTime": null, "gammaStartTime": null, "gammaEndTime": null }], "sessionId": "70ba6a87", "serviceToken": "95652", "region": ["FIT Central", "West", "Central"], "username": [], "status": "SUCCESS" };
              //this.totalPages = this.tableData.pageCount;
              this.marketList = this.tableData.market;
              this.regionList = this.tableData.region;
              this.createMarketList = this.tableData.marketDetailsList.market;
              this.createRegionList = this.tableData.regionDetailsList.region;


              //this.enbIdList=this.tableData.enodebId;
              //this.enbNameList=this.tableData.enodebName;
              //console.log(this.marketList);
              //this.custId=this.tableData.
              let pageCount = [];
              for (var i = 1; i <= this.tableData.pageCount; i++) {
                pageCount.push(i);
              }
              this.pageRenge = pageCount;
              this.pager = this.getPager(this.totalPages * this.pageSize, this.currentPage, this.pageRenge);
              if (this.tableData.sessionId == "408" || this.tableData.status == "Invalid User") {
                this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
              }
              if (this.tableData.eodSprintEntity.length == 0) {
                this.tableShowHide = false;
                this.noDataVisibility = true;
              } else {
                this.sprintData = this.tableData.eodSprintEntity;
                this.tableShowHide = true;
                this.noDataVisibility = false;
                setTimeout(() => {
                  let tableWidth = document.getElementById('eodSprintEntity').scrollWidth;

                  $(".scrollBody table").css("min-width", (tableWidth) + "px");
                  $(".scrollHead table").css("width", tableWidth + "px");


                  $(".scrollBody").on("scroll", function (event) {
                    $(".formEditRow #formWrapper").css("margin-left", (event.target.scrollLeft - 5) + "px");
                    $(".form-control-fixed").css("right", (event.target.scrollLeft * -1) + "px");
                    $(".scrollHead table").css("margin-left", (event.target.scrollLeft * -1) + "px");
                  });
                  //$(".scrollBody").css("max-height",(this.windowScreenHieght/2) + "px");
                  $(".scrollBody").css("max-height", (this.tableDataHeight - 10) + "px");



                }, 0);
              }
            } else {
              // Other Customers 
              this.noDataVisibility = true;
              this.showLoader = false;
            }


          }, 1000); */

          //Please Comment while checkIn
        });
  }







  searchSchedulingDetails(event) {

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
          this.noDataVisibility = false;

          if (this.custId == 2) {
            let currentForm = event.target.parentNode.parentNode.parentNode,
              searchCrtra = {
                "forecastStartDate" : this.datePipe.transform(currentForm.querySelector("#searchForecastStartDate").value, 'dd/MM/yyyy'),
                "forecastEndDate" : this.datePipe.transform(currentForm.querySelector("#searchForecastEndDate").value, 'dd/MM/yyyy'),
                //"forecastEndDate": currentForm.querySelector("#searchForecastEndDate").value,
                "market": currentForm.querySelector("#searchMarket").value,
                "enbId": currentForm.querySelector("#searchEnbId").value,
                "enbName": currentForm.querySelector("#searchEnbName").value,
                "carriers": currentForm.querySelector("#searchCarriers").value,
                "alarmPresent": currentForm.querySelector("#searchAlarmPresent").value,

              };

            if (searchCrtra.forecastStartDate || searchCrtra.forecastEndDate || searchCrtra.market || searchCrtra.enbId || searchCrtra.enbName || searchCrtra.carriers || searchCrtra.alarmPresent) {
              this.searchStatus = "search";
            }
            else {
              this.searchStatus = "load";
            }



            this.searchDetails = searchCrtra;

          }
          else if (this.custId == 4) {
            let currentForm = event.target.parentNode.parentNode.parentNode,
              searchCrtra = {
                "startDate" : this.datePipe.transform(currentForm.querySelector("#searchAmStartDate").value, 'dd/MM/yyyy'),
                "startEndDate" : this.datePipe.transform(currentForm.querySelector("#searchAmEndDate").value, 'dd/MM/yyyy'),
                "compDate" : this.datePipe.transform(currentForm.querySelector("#searchCompStartDate").value, 'dd/MM/yyyy'),
                "compEndDate" : this.datePipe.transform(currentForm.querySelector("#searchCompEndDate").value, 'dd/MM/yyyy'),
                //"startDate": currentForm.querySelector("#searchAmStartDate").value,
                //"startEndDate": currentForm.querySelector("#searchAmEndDate").value,
                //"compDate": currentForm.querySelector("#searchCompStartDate").value,
                //"compEndDate": currentForm.querySelector("#searchCompEndDate").value,
                "region": currentForm.querySelector("#searchRegion").value,
                "market": currentForm.querySelector("#searchMarket").value,
                "cascade": currentForm.querySelector("#searchCascade").value,


              };

            if (searchCrtra.startDate || searchCrtra.startEndDate || searchCrtra.compDate || searchCrtra.compEndDate || searchCrtra.region || searchCrtra.market || searchCrtra.cascade) {
              this.searchStatus = "search";
            }
            else {
              this.searchStatus = "load";
            }



            this.searchDetails = searchCrtra;
          }



          this.currentPage = 1;
          let paginationDetails = {
            "count": this.pageSize,
            "page": this.currentPage
          };

          this.paginationDetails = paginationDetails;
          // TO get the searched data
          this.getEodReportsDetails();
        }
      }
    }, 0);
  }



  addNewSchedule(event) {
    validator.performValidation(event, this.validationData, "save_update");
    setTimeout(() => {
      if (this.isValidForm(event)) {
        this.showLoader = true;
        let eodReportsDetails;
        if (this.custId == 2) {
          let currentForm = event.target.parentNode.parentNode;
          eodReportsDetails = {
            "id": null,
            "totalLookup": currentForm.querySelector("#totalLookup").value,
            "forecastStartDate" : this.datePipe.transform(currentForm.querySelector("#forecastStartDate").value, 'dd/MM/yyyy'),
            //"forecastStartDate": currentForm.querySelector("#forecastStartDate").value,
            "market": currentForm.querySelector("#market").value,
            "enbName": currentForm.querySelector("#enbName").value,
            "enbId": currentForm.querySelector("#enbId").value,
            "ranEngineer": currentForm.querySelector("#ranEngineer").value,
            "growRequest": currentForm.querySelector("#growRequest").value,
            "growCompleted": currentForm.querySelector("#growCompleted").value,
            "ciqPresent": currentForm.querySelector("#ciqPresent").value,
            "status": currentForm.querySelector("#status").value,
            "revisit": currentForm.querySelector("#revisit").value,
            "vlsm": currentForm.querySelector("#vlsm").value,
            "ciStartTime": currentForm.querySelector("#ciStartTime").value,
            "ciEndTime": currentForm.querySelector("#ciEndTime").value,
            "ald": currentForm.querySelector("#ald").value,
            "issue": currentForm.querySelector("#issue").value,
            "ci": currentForm.querySelector("#ci").value,
            "nonCi": currentForm.querySelector("#nonCi").value,
            "comments": currentForm.querySelector("#comments").value,
            "day":this.day,
            "month":this.month,
            "year":this.year,
            "week":this.week,
            "quarter":this.quarter
          }
        }

        else if (this.custId == 4) {
          let currentForm = event.target.parentNode.parentNode;
          eodReportsDetails = {
            "id": null,
            "actualMigrationStartDate" : this.datePipe.transform(currentForm.querySelector("#sprintStartDate").value, 'dd/MM/yyyy'),
            "compDate" : this.datePipe.transform(currentForm.querySelector("#sprintCompDate").value, 'dd/MM/yyyy'),
            //"actualMigrationStartDate": currentForm.querySelector("#sprintStartDate").value,
            //"compDate": currentForm.querySelector("#sprintCompDate").value,
            "region": currentForm.querySelector("#sprintRegion").value,
            "market": currentForm.querySelector("#sprintMarket").value,
            "enbId": currentForm.querySelector("#sprintenbid").value,
            "cascade": currentForm.querySelector("#sprintCascade").value,
            "type": currentForm.querySelector("#sprintType").value,
            "currentSoftware": currentForm.querySelector("#sprintCurrentSoftware").value,
            "scriptsRan": currentForm.querySelector("#scriptsRan").value,
            "dspImplemented": currentForm.querySelector("#dspImplemented").value,
            "putTool": currentForm.querySelector("#putTool").value,
            "scriptErrors": currentForm.querySelector("#scriptErrors").value

          }
        }

        //console.log(schedulingDetails);

        /*    
  */


        // this.sharedService.userNavigation = true; //un block user navigation

        this.eodReportsService.saveEodReportsDetails(eodReportsDetails, this.sharedService.createServiceToken(), this.custId)
          .subscribe(
            data => {

              let currentData = data.json();
              this.showLoader = false;

              if (currentData.sessionId == "408" || currentData.status == "Invalid User") {

                this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
              } else {

                if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {

                  if (currentData.status == "SUCCESS") {
                    this.message = "EOD Report created successfully!";
                    //this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                    this.displayModel(this.message, "successIcon");

                    this.searchTabBind();
                    this.searchStatus = "load";
                  } else if (currentData.status == "FAILED") {
                    this.displayModel("Failed to create", "failureIcon");
                  }
                }
              }

            },
            error => {
              //Please Comment while checkIn
              /* let currentData = { "sessionId": "fff0e445", "serviceToken": "74388", "status": "SUCCESS" };
              setTimeout(() => {
                this.showLoader = false;
                if (currentData.status == "SUCCESS") {
                  this.message = "EOD Report created successfully!";
                  //  this.successModalBlock = this.modalService.open(this.successModalRef, { windowClass: 'success-modal', keyboard: false, backdrop: 'static', size: 'lg' });
                  this.displayModel(this.message, "successIcon");

                  this.searchTabBind();
                  this.searchStatus = "load";
                } else if (currentData.status == "FAILED") {
                  this.displayModel("Failed to create", "failureIcon");
                }
              }, 1000); */
              //Please Comment while checkIn
            });
      }
    }, 0);
  }







  downloadFile() {
    this.showLoader = true;
    this.eodReportsService.downloadFile(this.sharedService.createServiceToken(), this.custId, this.searchDetails, this.searchStatus)
      .subscribe(
        data => {
          this.showLoader = false;
          let blob = new Blob([data["_body"]], {
            type: "application/octet-stream"
          });

          FileSaver.saveAs(blob, "SchedulingDetails.xlsx");

        },
        error => {
          //Please Comment while checkIn
          /* let jsonStatue: any = { "sessionId": "506db794", "reason": "Download successful!", "status": "SUCCESS", "serviceToken": "63524" };
          data => {
            this.showLoader = false;
            let blob = new Blob([data["_body"]], {
              type: "application/octet-stream"
            });

            FileSaver.saveAs(blob, "SchedulingDetails.xlsx");
          }
          setTimeout(() => {
            this.showLoader = false;
            if (jsonStatue.status == "SUCCESS") {

            } else {
              this.displayModel("Failed to download!", "failureIcon");
            }

          }, 1000); */
          //Please Comment while checkIn

        });


  }

  uploadStateTar(event) {
    const formdata = new FormData();
    let files: FileList = this.filePostRef.nativeElement.files,
      validFileType = false;
    for (var i = 0; i < files.length; i++) {
      if (files[i].name.indexOf('.xlsx') >= 0) {
        validFileType = true;
        formdata.append("schedulingFile", files[0]);
        formdata.append(files[i].name, files[i]);
      } else {
        validFileType = false;
        this.displayModel("Invalid file type..... Supports .xlsx format", "failureIcon");
      }
    }

    if (validFileType) {
      setTimeout(() => {
        this.showLoader = true;
        this.eodReportsService.uploadFile(formdata, this.sharedService.createServiceToken(), this.custId)
          .subscribe(
            data => {
              let jsonStatue = data.json();

              if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
                this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
              } else {
                if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {
                  if (jsonStatue.status == "SUCCESS") {
                    this.showLoader = false;
                    this.filePostRef.nativeElement.value = "";
                    //*********************************************
                    this.displayModel("File Imported successfully !", "successIcon");
                    this.ngOnInit();
                  } else {
                    this.showLoader = false;
                    this.displayModel("Failed to upload!", "failureIcon");
                    this.filePostRef.nativeElement.value = "";
                  }
                }
              }
            },
            error => {
              //Please Comment while checkIn

              /* setTimeout(() => {
                this.showLoader = false;
                let jsonStatue = { "sessionId": "e9004f23", "reason": "File Imported successfully !", "status": "SUCCESS", "serviceToken": "64438" };

                if (jsonStatue.status == "SUCCESS") {
                  this.showLoader = false;
                  // UploadFileStatus *******************************************
                  this.filePostRef.nativeElement.value = "";
                  //*********************************************
                  this.displayModel("File Imported successfully !", "successIcon");
                  this.ngOnInit();
                } else {
                  this.showLoader = false;
                  this.displayModel("Failed to upload!", "failureIcon");
                  this.filePostRef.nativeElement.value = "";
                }

              }, 100); */
              //Please Comment while checkIn   
            });

      }, 0);
    }
  }

  setMenuHighlight(selectedElement) {
    this.searchTabRef.nativeElement.id = (selectedElement == "search") ? "activeTab" : "inactiveTab";
    this.createTabRef.nativeElement.id = (selectedElement == "create") ? "activeTab" : "inactiveTab";
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

  closeModel() {
    this.successModalBlock.close();
    //this.ngOnInit();
    //this.getEodReportsDetails();
  }

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
        "count": parseInt(this.pageSize, 10),
        "page": parseInt(page)
      };

      this.paginationDetails = paginationDetails;
      this.pager = this.getPager(this.totalPages * this.pageSize, this.currentPage, this.pageRenge);
      this.paginationDisabbled = false;
      // Hide all the form/Table/Nodatafound5
      this.tableShowHide = false;

      this.getEodReportsDetails();


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

    setTimeout(() => {
      this.showLoader = false;
      $("#dataWrapper").find(".scrollBody").scrollLeft(0);
      this.getEodReportsDetails();
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

  cancelEditRow(index, identifier) {
    $(".editRowDisabled").attr("class", "editRow");
    let currentEditedForm = document.querySelector("#row_id_" + identifier);
    //this.editableFormArray.splice(this.editableFormArray.indexOf(index), 1);
    this.editableFormArray = [];
    this.paginationDisabbled = false;
    this.checkFormEnable(index); //TODO : need to recheck this function
    this.sharedService.userNavigation = true; //un block user navigation
    currentEditedForm.lastElementChild.lastElementChild.children[0].className = "editRow";
  }

  checkFormEnable(index) {
    let indexValue = this.editableFormArray.indexOf(index);
    return indexValue >= 0 ? true : false;
  }

  cancelCreateNew(event) {
    this.searchTabBind();
  }


  custNameChange(selCustName) {
    this.editableEODFormArray = [];
    this.editIndex = -1;
    this.searchDetails = {};
    this.searchStatus = "load";

    if (selCustName == "") {
      this.editableEODFormArray = [];
      this.custId = selCustName;
      this.showLoader = true;
      this.ngOnInit();
    }
    else {
      this.editableEODFormArray = [];
      this.custId = selCustName;
      this.getEodReportsDetails();
    }



  }



  changeSorting(predicate, event, index){
    if(this.selCustName=='2')
    {
      this.sharedService.dynamicSort(predicate, event, index, this.verizonData);

    }
    else if(this.selCustName=='4')
    {
      this.sharedService.dynamicSort(predicate, event, index, this.sprintData);
    }
  }
  

  editNeVersionRow(event, key, index) {
    $(".createbtn").addClass("buttonDisabled");
    let editState: any = event.target;

    if (editState.className == "editRow") {
      this.editIndex = index;
      this.currEditedRowVal = JSON.parse(JSON.stringify(key)); //storing the current value
      // this.programDetailsEntity = key.programDetailsEntity;
      $("#verizonTableData").find("br").remove();
      $(".saveRow").attr("class", "editRow");
      //$(".saveRow").addClass("validateForm");
      $(".cancelRow").attr("class", "deleteRow");
      if (editState.className != "editRowDisabled") { //enable click only if it is enabled
        editState.className = "saveRow";
        editState.parentNode.querySelector(".deleteRow").className = "cancelRow";
        // editState.nextSibling.className = "cancelRow";
        $(".cloneRow").attr("class", "cloneRowDisabled");
        // To enable one edit form at a time in table
        if (this.editableEODFormArray.length >= 1) {
          this.editableEODFormArray = [];
          this.editableEODFormArray.push(index);
        } else {
          this.editableEODFormArray.push(index);
        }
      }

    } else if (editState.className != "editRowDisabled") {

      setTimeout(() => {
        // if (this.isValidForm(event)) {

        this.showLoader = true;
        let eodDetails = {};

        if (this.custId == 2) {
          //Verizon
          key.forecastStartDate = this.datePipe.transform(key.forecastStartDate, 'dd/MM/yyyy');
          key.compDate = this.datePipe.transform(key.compDate, 'dd/MM/yyyy');
          //key.ciStartTime = this.datePipe.transform(key.ciStartTime, 'hh:mm a');
          //key.ciEndTime = this.datePipe.transform(key.ciEndTime, 'hh:mm a');
          eodDetails = key;

        } else if (this.custId == 4) {
          //Sprint
          key.actualMigrationStartDate = this.datePipe.transform(key.actualMigrationStartDate, 'dd/MM/yyyy');
          key.compDate = this.datePipe.transform(key.compDate, 'dd/MM/yyyy');
          eodDetails = key;
        }
        this.eodReportsService.saveEodReportsDetails(eodDetails, this.sharedService.createServiceToken(), this.custId)
          .subscribe(
            data => {
              let jsonStatue = data.json();
              setTimeout(() => {
                this.showLoader = false;
                if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
                  this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
                } else {
                  if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {

                    if (jsonStatue.status == "SUCCESS") {
                      this.editableEODFormArray = [];
                      this.addtableFormArray = [];
                      this.message = "EOD Report saved successfully!";
                      // this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });                  
                      //this.createVerizonForm.clear();    
                      this.displayModel(this.message, "successIcon");
                      this.getEodReportsDetails();
                    } else {
                      this.displayModel(jsonStatue.reason, "failureIcon");
                    }
                  }
                }
              }, 1000);
            },
            error => {
              //Please Comment while checkIn
              /* let jsonStatue: any = { "sessionId": "506db794", "reason": "Updation Failed", "status": "SUCCESS", "serviceToken": "63524" };
              setTimeout(() => {
                this.showLoader = false;
                if (jsonStatue.status == "SUCCESS") {
                  this.editableEODFormArray = [];
                  this.addtableFormArray = [];
                  this.message = "EOD Report saved successfully!";
                  // this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });                  
                  //this.createVerizonForm.clear();  
                  this.displayModel(this.message, "successIcon");
                  this.getEodReportsDetails();
                } else {
                  this.displayModel(jsonStatue.reason, "failureIcon");
                }

              }, 1000); */
              //Please Comment while checkIn
            });

        // }

      }, 0);
    }

  }

  deleteNeVersionRow(confirmModal, id, event, index) {
    if (event.target.className == "deleteRow") {
      this.modalService.open(confirmModal, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal' }).result.then((result) => {
        this.closeResult = `Closed with: ${result}`;
      }, (reason) => {
        this.showLoader = true;

        this.eodReportsService.deleteNeVersion(id, this.sharedService.createServiceToken(), this.custId)
          .subscribe(
            data => {
              let jsonStatue = data.json();
              setTimeout(() => {
                this.showLoader = false;
              }, 2000);

              if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
                this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });

              } else {
                if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {

                  if (jsonStatue.status == "SUCCESS") {
                    this.message = "EOD Report deleted successfully!";
                    this.displayModel(this.message, "successIcon");
                    this.getEodReportsDetails();
                  } else {
                    this.displayModel(jsonStatue.reason, "failureIcon");
                  }
                }
              }
              console.log("im here success :) ");

            },
            error => {
              //Please Comment while checkIn
              /* setTimeout(() => {
                this.showLoader = false;
                let jsonStatue = { "reason": "NE Version Details Deleted Successfully", "sessionId": "5f3732a4", "serviceToken": "80356", "status": "SUCCESS" };
                if (jsonStatue.status == "SUCCESS") {
                  this.message = "EOD Report deleted successfully!";
                  this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: "static", size: 'lg', windowClass: "success-modal" });
                  this.getEodReportsDetails();
                } else {
                  this.displayModel(jsonStatue.reason, "failureIcon");
                }
              }, 1000); */
              //Please Comment while checkIn

              //this.alertService.error(error);TODO : This need to implement
            });
      });
    } else if (event.target.className == "cancelRow") {
      this.editIndex = -1;
      if (this.custId == 2) {
        this.verizonData[index] = this.currEditedRowVal; // According to customer revert to actual data

      }
      else if (this.custId == 4) {
        this.sprintData[index] = this.currEditedRowVal; // According to customer revert to actual data

      }
      $(".cloneRowDisabled").attr("class", "cloneRow");
      $(".saveRow").attr("class", "cloneRow");
      event.target.className = "deleteRow";
      event.target.previousSibling.className = "editRow";
      setTimeout(() => {
        $(".scrollBody").scrollLeft($(".scrollBody").scrollLeft() + 1);
        // $(".form-control-fixed").css("right", ($(".form-control-fixed").offset().left * -1) + "px");
      }, 0);
      // document.querySelector(".createbtn").className = document.querySelector(".createbtn").className.replace("buttonDisabled", "");
    }
  }

  addEodReportRow(event, key, index) {


    let editState: any = event.target, cloneKey = {};

    if (editState.className == "cloneRow") {
      cloneKey = JSON.parse(JSON.stringify(key));
      if (this.custId == 2) {
        this.verizonData.splice(index, 0, cloneKey);

      }
      else if (this.custId == 4) {
        this.sprintData.splice(index, 0, cloneKey);

      }
      this.editIndex = index + 1;
      if (editState.className != "cloneRowDisabled") { //enable click only if it is enabled
        editState.className = "saveRow";
        editState.parentNode.querySelector(".editRow").className = "editRowDisabled";
        editState.parentNode.querySelector(".deleteRow").className = "cancelRow";
        $(".cloneRow").attr("class", "cloneRowDisabled");
        $(".editRow").attr("class", "editRowDisabled");
        // To enable one edit form at a time in table
        if (this.addtableFormArray.length >= 1) {
          this.addtableFormArray = [];
          this.addtableFormArray.push(index);
        } else {
          this.addtableFormArray.push(index);
        }
      }




    } else if (editState.className != "cloneRowDisabled") {
      this.editIndex = -1;
      setTimeout(() => {
        // if (this.isValidForm(event)) {

        this.showLoader = true;
        let eodDetails = {};

        if (this.custId == 2) {
          //Verizon
          key.id=null;
          key.forecastStartDate = this.datePipe.transform(key.forecastStartDate, 'dd/MM/yyyy');
          key.compDate = this.datePipe.transform(key.compDate, 'dd/MM/yyyy');
          eodDetails = key;
        } else if (this.custId == 4) {
          //Sprint
          key.id=null;
          key.actualMigrationStartDate = this.datePipe.transform(key.actualMigrationStartDate, 'dd/MM/yyyy');
          key.compDate = this.datePipe.transform(key.compDate, 'dd/MM/yyyy');
          eodDetails = key;
        }
        this.eodReportsService.saveEodReportsDetails(eodDetails, this.sharedService.createServiceToken(), this.custId)
          .subscribe(
            data => {
              let jsonStatue = data.json();
              setTimeout(() => {
                this.showLoader = false;
                if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
                  this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
                } else {
                  if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {

                    if (jsonStatue.status == "SUCCESS") {
                      this.message = "EOD Report saved successfully!";
                      // this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });                  
                      //this.createVerizonForm.clear();                         
                      //this.getEodReportsDetails(); 
                      this.displayModel(this.message, "successIcon");
                      this.getEodReportsDetails();
                    } else {
                      this.displayModel(jsonStatue.reason, "failureIcon");
                    }
                  }
                }
              }, 1000);
            },
            error => {
              //Please Comment while checkIn
              /* let jsonStatue: any = { "sessionId": "506db794", "reason": "Updation Failed", "status": "SUCCESS", "serviceToken": "63524" };
              setTimeout(() => {
                this.showLoader = false;
                if (jsonStatue.status == "SUCCESS") {
                  this.message = "EOD Report saved successfully!";
                  // this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });                  
                  // this.createVerizonForm.clear();  
                  this.displayModel(this.message, "successIcon");
                  this.getEodReportsDetails();
                } else {
                  this.displayModel(jsonStatue.reason, "failureIcon");
                }

              }, 1000); */
              //Please Comment while checkIn
            });

        // }

      }, 0);
    }







  }


  cancelNeVersionRow(event, index) {
    document.querySelector(".createbtn").className = document.querySelector(".createbtn").className.replace("buttonDisabled", "");
    $(".editRowDisabled").attr("class", "editRow");
    $(".deleteRowDisabled").attr("class", "deleteRow");
    /* if(this.neversionData.neVersionDetails.length == 0 ){
        this.neversionBlock= false;
        this.newNoDataWrapper = true;
    } else {
        this.neversionBlock= true;
        this.newNoDataWrapper = false;
    } */
    this.neversionContainer.clear();
  }




  getCustomerList() {
    //validator.performValidation(event, this.validationData, "save_update");
    setTimeout(() => {

      // this.sharedService.userNavigation = true; //un block user navigation

      this.eodReportsService.getCustomerIdList(this.sharedService.createServiceToken())
        .subscribe(
          data => {

            let currentData = data.json();
            this.showLoader = false;

            if (currentData.sessionId == "408" || currentData.status == "Invalid User") {

              this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
            } else {

              if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {

                if (currentData.status == "SUCCESS") {
                  this.custList = currentData.CustomerList.customerlist;


                } else {
                  this.displayModel(currentData.reason, "failureIcon")
                }
              }
            }

          },
          error => {
            //Please Comment while checkIn
            /* let currentData = { "sessionId": "fff0e445", "serviceToken": "74388", "status": "SUCCESS" };
            setTimeout(() => {
              this.showLoader = false;
              this.tableData = { "sessionId": null, "serviceToken": null, "CustomerList": { "customerlist": [{ "id": 1, "customerName": "All", "iconPath": " ", "status": "Active", "customerShortName": null, "customerDetails": [] }, { "id": 2, "customerName": "Verizon", "iconPath": "/customer/verizon_icon.png", "status": "Active", "customerShortName": "VZN", "customerDetails": [{ "id": 23, "networkTypeDetailsEntity": { "id": 2, "networkType": "4G", "createdBy": "superadmin", "caretedDate": "2019-02-15T13:24:21.000+0000", "status": "Active", "remarks": "", "networkColor": "#f51dc1" }, "programName": "VZN-4G-LEGACY", "programDescription": "LEGACY", "status": "Active", "creationDate": "2019-03-18T20:58:41.000+0000", "createdBy": "admin" }, { "id": 24, "networkTypeDetailsEntity": { "id": 3, "networkType": "5G", "createdBy": "admin", "caretedDate": "2019-01-23T11:03:34.000+0000", "status": "Active", "remarks": "", "networkColor": "#baba97" }, "programName": "VZN-5G-VLSM", "programDescription": "VLSM", "status": "Active", "creationDate": "2019-02-27T13:42:33.000+0000", "createdBy": "superadmin" }] }, { "id": 3, "customerName": "AT&T", "iconPath": "/customer/at&t_icon.png", "status": "Active", "customerShortName": "AT&T", "customerDetails": [{ "id": 25, "networkTypeDetailsEntity": { "id": 2, "networkType": "4G", "createdBy": "superadmin", "caretedDate": "2019-02-15T13:24:21.000+0000", "status": "Active", "remarks": "", "networkColor": "#f51dc1" }, "programName": "AT&T-4G-LEGACY", "programDescription": "LEGACY", "status": "Active", "creationDate": "2019-02-27T13:43:26.000+0000", "createdBy": "superadmin" }, { "id": 26, "networkTypeDetailsEntity": { "id": 3, "networkType": "5G", "createdBy": "admin", "caretedDate": "2019-01-23T11:03:34.000+0000", "status": "Active", "remarks": "", "networkColor": "#baba97" }, "programName": "AT&T-5G-VLSM", "programDescription": "VLSM", "status": "Active", "creationDate": "2019-02-27T13:44:05.000+0000", "createdBy": "superadmin" }] }, { "id": 4, "customerName": "Sprint", "iconPath": "/customer/sprint_icon.png", "status": "Active", "customerShortName": "SPT", "customerDetails": [{ "id": 27, "networkTypeDetailsEntity": { "id": 3, "networkType": "5G", "createdBy": "admin", "caretedDate": "2019-01-23T11:03:34.000+0000", "status": "Active", "remarks": "", "networkColor": "#baba97" }, "programName": "SPT-5G-MIMO", "programDescription": "MIMO_NORMAL", "status": "Active", "creationDate": "2019-03-14T15:19:24.000+0000", "createdBy": "superadmin" }, { "id": 28, "networkTypeDetailsEntity": { "id": 3, "networkType": "5G", "createdBy": "admin", "caretedDate": "2019-01-23T11:03:34.000+0000", "status": "Active", "remarks": "", "networkColor": "#baba97" }, "programName": "SPT-5G-MIMO_CLWR", "programDescription": "MIMO_CLWR", "status": "Active", "creationDate": "2019-03-14T15:19:41.000+0000", "createdBy": "superadmin" }] }] } };

              this.custList = this.tableData.CustomerList.customerlist;

            }, 1000); */
            //Please Comment while checkIn
          });

    }, 0);
  }



  onChangeDate(forecastStartDate) {
    let month = [], days = [];
    month[0] = "Jan";
    month[1] = "Feb";
    month[2] = "Mar";
    month[3] = "Apr";
    month[4] = "May";
    month[5] = "Jun";
    month[6] = "Jul";
    month[7] = "Aug";
    month[8] = "Sep";
    month[9] = "Oct";
    month[10] = "Nov";
    month[11] = "Dec";
    days[0] = "Sun";
    days[1] = "Mon";
    days[2] = "Tue";
    days[3] = "Wed";
    days[4] = "Thu";
    days[5] = "Fri";
    days[6] = "Sat";

    

    let dt: any;
    dt = new Date(forecastStartDate.getFullYear(), 0, 1);
    this.week = "Week "+Math.ceil((((forecastStartDate - dt) / 86400000) + dt.getDay() + 1) / 7);
    this.month = month[forecastStartDate.getMonth()];
    this.day= days[forecastStartDate.getDay()];
    this.year= forecastStartDate.getFullYear();
    if(this.month=="Jan" || this.month=="Feb" || this.month=="Mar")
    {
      this.quarter="Qtr1"
    }
    else if(this.month=="Apr" || this.month=="May" || this.month=="Jun")
    {
      this.quarter="Qtr2"
    }
    else if(this.month=="Jul" || this.month=="Aug" || this.month=="Sep")
    {
      this.quarter="Qtr3"
    }
    else if(this.month=="Oct" || this.month=="Nov" || this.month=="Dec")
    {
      this.quarter="Qtr4"
    }

    //console.log("Week " + week);
  }








}
