import { ViewContainerRef, Input, ElementRef, Renderer, Component, OnInit, ViewChild, HostListener, SystemJsNgModuleLoader } from '@angular/core';
// import { Router} from '@angular/router';
import { Router, NavigationEnd } from '@angular/router';
import { NgbModal, ModalDismissReasons } from '@ng-bootstrap/ng-bootstrap';
import { OverallreportsService } from '../services/overallreports.service';
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
@Component({
  selector: 'app-overallreports',
  templateUrl: './overallreports.component.html',
  styleUrls: ['./overallreports.component.scss'],
  providers: [OverallreportsService]
})
export class OverallreportsComponent implements OnInit {
  custList: any;
  tableEdit: boolean = false;
  currEditedRowVal: any;
  editableEODFormArray = [];
  addtableFormArray = [];
  editIndex: number = -1;
  regionList: any;
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
  userList: any;
  createMarketList: any;
  createRegionList: any;
  createFeregionList: any;

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
      "compDate": {
        "required": true,
      }
    },
    "messages": {
      "forecastStartDate": {
        "required": "ProgramName is required"
      },
      "market": {
        "required": "Market is required",
      },
      "enbId": {
        "required": "ENODEB ID is required",
      },
      "enbName": {
        "required": "ENODEB NAME is required",
      },
      "compDate": {
        "required": "ENODEB NAME is required",
      }
    }
  };

  @ViewChild('confirmModal') confirmModalRef: ElementRef;
  @ViewChild('bluePrintForm') bluePrintForm;
  @ViewChild('searchForm') searchForm;
  @ViewChild('neversionContainer', { read: ViewContainerRef }) neversionContainer;

  @ViewChild('sessionExpiredModal') sessionExpiredModalRef: ElementRef;
  @ViewChild('successModal') successModalRef: ElementRef;
  @ViewChild('filePost') filePostRef: ElementRef;



  constructor(
    private overallReportsService: OverallreportsService,
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
      this.getOverallReportsDetails();
      //this.showLoader = true;
    }

    this.getCustomerList();


    //JSON.parse(sessionStorage.selectedCustomerList).id,
  }



clearSearchButton()
{
  this.bluePrintForm.nativeElement.reset();
  this.totalPages = 1;
  this.currentPage = 1;
  this.TableRowLength = 10;
  this.pageSize = 10;
  this.tableShowHide = false;
  this.showLoader = false;
  let paginationDetails = {
    "count": parseInt(this.TableRowLength, 10),
    "page": this.currentPage
  };
  this.paginationDetails = paginationDetails;

  this.getOverallReportsDetails();
}


  ngOnDestroy() {
    // avoid memory leaks here by cleaning up after ourselves. If we  
    // don't then we will continue to run our ngOnInit()   
    // method on every navigationEnd event.
    if (this.navigationSubscription) {
      this.navigationSubscription.unsubscribe();
    }
  }


  clearSearchFrom() {
    this.bluePrintForm.nativeElement.reset();  
}


  getOverallReportsDetails() {

    this.tableShowHide = false;
    this.editIndex = -1;
    this.showLoader = true;
    $("#dataWrapper").find(".scrollBody").scrollLeft(0);
    this.overallReportsService.getOverallReportsDetails(this.searchStatus, this.searchDetails, this.sharedService.createServiceToken(), this.paginationDetails, this.custId)
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
                    this.createMarketList = jsonStatue.comboBoxListDetails.market;



                    let pageCount = [];
                    for (var i = 1; i <= jsonStatue.pageCount; i++) {
                      pageCount.push(i);
                    }
                    this.pageRenge = pageCount;
                    this.pager = this.getPager(this.totalPages * this.pageSize, this.currentPage, this.pageRenge);

                    if (jsonStatue.overallVerizonModelList.length == 0) {
                      this.tableShowHide = false;
                      this.noDataVisibility = true;
                    } else {
                      this.verizonData = jsonStatue.overallVerizonModelList;
                      this.tableShowHide = true;
                      this.noDataVisibility = false;
                      setTimeout(() => {
                        let tableWidth = document.getElementById('overallVerizonModelList').scrollWidth;
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
              this.userList = jsonStatue.username;

              this.marketList = jsonStatue.market;
              this.regionList = jsonStatue.region;
              this.createMarketList = jsonStatue.marketDetailsList.market;
              this.createRegionList = jsonStatue.regionDetailsList.region;
              this.createFeregionList = jsonStatue.feregionDetailsList.feRegion;
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

                    if (this.tableData.overallSprintEntity.length == 0) {
                      this.tableShowHide = false;
                      this.noDataVisibility = true;
                    } else {
                      this.sprintData = this.tableData.overallSprintEntity;
                      this.tableShowHide = true;
                      this.noDataVisibility = false;
                      setTimeout(() => {
                        let tableWidth = document.getElementById('overallSprintEntity').scrollWidth;
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
            else {
              // Other Customers 
              this.noDataVisibility = true;
              this.showLoader = false;
            }


          }, 1000);
        },
        error => {
          //Please Comment while checkIn
          /* setTimeout(() => {
            this.editableEODFormArray = [];
            if (this.custId == 2) {
              this.showLoader = false;

              this.tableData = {"sessionId":"919e8f6d","serviceToken":"95851","status":"SUCCESS","pageCount":1,"comboBoxListDetails":{"market":["Upstate New York","New England"],"region":null,"feRegion":null},"market":["test","New England","New England_test import","test market_sreeraj create"],"enodebName":["Test","test name create","test1"],"enodebId":["71017","74101","test "],"username":["son","lakisha","kimath","john","supriya"],"overallVerizonModelList":[{"id":234,"forecastStartDate":"2019-03-27 00:00:00","compDate":"2019-03-30 00:00","market":"New England","enbId":"71017","enbName":"Test","growRequest":"YES","growCompleted":"","ciqPresent":"YES","envCompleted":"YES","standardNonStandard":"STANDARD","carriers":"","uda":"","softwareLevels":"","feArrivalTime":"6:49 PM","ciStartTime":"","dtHandoff":"","ciEndTime":"","canRollComp":"","traffic":"","alarmPresent":"","ciEngineer":"","ft":"","dt":"","notes":"","column1":null,"totalLookup":null,"ranEngineer":null,"status":null,"revisit":null,"vlsm":null,"endTime":null,"comments":null,"issue":null,"ci":null,"nonCi":null,"ald":null,"week":null,"month":null,"status2":null,"quarter":null,"year":null,"rule1":null,"rule2":null,"day":null},{"id":235,"forecastStartDate":"2019-03-05 00:00:00","compDate":"2019-03-27 00:00","market":"test","enbId":"74101","enbName":"test1","growRequest":"YES","growCompleted":"","ciqPresent":"YES","envCompleted":"NO","standardNonStandard":"STANDARD","carriers":"","uda":"","softwareLevels":"","feArrivalTime":"","ciStartTime":"","dtHandoff":"","ciEndTime":"","canRollComp":"CANCELLED","traffic":"","alarmPresent":"","ciEngineer":"lakisha","ft":"kimath","dt":"john","notes":"","column1":null,"totalLookup":null,"ranEngineer":null,"status":null,"revisit":null,"vlsm":null,"endTime":null,"comments":null,"issue":null,"ci":null,"nonCi":null,"ald":null,"week":null,"month":null,"status2":null,"quarter":null,"year":null,"rule1":null,"rule2":null,"day":null},{"id":246,"forecastStartDate":"2019-03-27 00:00:00","compDate":"2019-03-27 00:00","market":"test market_sreeraj create","enbId":"test ","enbName":"test name create","growRequest":"","growCompleted":"","ciqPresent":"","envCompleted":"","standardNonStandard":"","carriers":"","uda":"","softwareLevels":"","feArrivalTime":"","ciStartTime":"","dtHandoff":"","ciEndTime":"","canRollComp":"","traffic":"","alarmPresent":"","ciEngineer":"","ft":"","dt":"","notes":"","column1":null,"totalLookup":null,"ranEngineer":null,"status":null,"revisit":null,"vlsm":null,"endTime":null,"comments":null,"issue":null,"ci":null,"nonCi":null,"ald":null,"week":null,"month":null,"status2":null,"quarter":null,"year":null,"rule1":null,"rule2":null,"day":null},{"id":248,"forecastStartDate":null,"compDate":null,"market":"New England_test import","enbId":"71017","enbName":"Test","growRequest":"YES","growCompleted":"","ciqPresent":"YES","envCompleted":"YES","standardNonStandard":"STANDARD","carriers":"","uda":"","softwareLevels":"","feArrivalTime":"6:49 PM","ciStartTime":"","dtHandoff":"","ciEndTime":"","canRollComp":"","traffic":"CANCELLED","alarmPresent":"","ciEngineer":"","ft":"lakisha","dt":"kimath","notes":"john","column1":null,"totalLookup":null,"ranEngineer":null,"status":null,"revisit":null,"vlsm":null,"endTime":null,"comments":null,"issue":null,"ci":null,"nonCi":null,"ald":null,"week":null,"month":null,"status2":null,"quarter":null,"year":null,"rule1":null,"rule2":null,"day":null}]};
              this.totalPages = this.tableData.pageCount;

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
              if (this.tableData.overallVerizonModelList.length == 0) {
                this.tableShowHide = false;
                this.noDataVisibility = true;
              } else {
                this.verizonData = this.tableData.overallVerizonModelList;
                this.tableShowHide = true;
                this.noDataVisibility = false;
                setTimeout(() => {
                  let tableWidth = document.getElementById('overallVerizonModelList').scrollWidth;

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
              // this.tableData = {"pageCount":2,"marketDetailsList":{"market":["buffalo","Central Illinois","Central Iowa","Central Pennsylvania","Chicago","Cincinnati","Cleveland","Columbus","East Michigan","Ft. Wayne/South Bend","Indianpolis","Milwaukee","North Wisconsin","Pittsburgh","Toledo","West Iowa/Nebraska","West Michigan","Western Pennsylvania"],"region":null,"feRegion":null},"regionDetailsList":{"market":null,"region":["Central","West"],"feRegion":null},"feregionDetailsList":{"market":null,"region":null,"feRegion":["FECentral","FEWest"]},"sessionId":"2d35e968","serviceToken":"67656","overallSprintEntity":[{"id":25,"forecastStartDate":null,"compDate":null,"market":"New England","enbId":"71017.0","enbName":"QUEENSBURY_Import_overall edit ","growRequest":"NO","growCompleted":"YES","ciqPresent":"YES","envCompleted":null,"standardNonStandard":null,"carriers":null,"uda":null,"softwareLevels":null,"feArrivalTime":null,"ciStartTime":null,"dtHandoff":null,"ciEndTime":null,"canRollComp":null,"traffic":null,"alarmPresent":null,"ciEngineer":null,"ft":null,"dt":null,"notes":null,"column1":null,"totalLookup":"overall edit ","ranEngineer":"Ratul ","status":null,"revisit":null,"vlsm":null,"comments":null,"issue":null,"ci":null,"nonCi":null,"ald":null,"week":null,"month":null,"status2":null,"quarter":null,"year":null,"rule1":null,"rule2":null,"day":null},{"id":27,"forecastStartDate":null,"compDate":null,"market":"New England","enbId":"71017_clone","enbName":"QUEENSBURY_Import test","growRequest":"NO","growCompleted":"YES","ciqPresent":"YES","envCompleted":"YES","standardNonStandard":"STANDARD","carriers":"Test","uda":"uda","softwareLevels":null,"feArrivalTime":"8:29 PM","ciStartTime":"8:29 PM","dtHandoff":"dt","ciEndTime":"8:29 PM","canRollComp":"ROLLEDBACK","traffic":"traffic","alarmPresent":"YES","ciEngineer":"Arun","ft":"admin","dt":"Ratul ","notes":"","column1":null,"totalLookup":null,"ranEngineer":null,"status":null,"revisit":null,"vlsm":null,"comments":null,"issue":null,"ci":null,"nonCi":null,"ald":null,"week":null,"month":null,"status2":null,"quarter":null,"year":null,"rule1":null,"rule2":null,"day":null},{"id":28,"forecastStartDate":null,"compDate":null,"market":"New England","enbId":"71017.0","enbName":"QUEENSBURY_Import test_clone","growRequest":"YES","growCompleted":"YES","ciqPresent":"YES","envCompleted":null,"standardNonStandard":null,"carriers":null,"uda":null,"softwareLevels":null,"feArrivalTime":null,"ciStartTime":"8:29 PM","dtHandoff":null,"ciEndTime":"8:29 PM","canRollComp":null,"traffic":null,"alarmPresent":null,"ciEngineer":null,"ft":null,"dt":null,"notes":null,"column1":null,"totalLookup":null,"ranEngineer":null,"status":null,"revisit":null,"vlsm":null,"comments":null,"issue":null,"ci":null,"nonCi":null,"ald":null,"week":null,"month":null,"status2":null,"quarter":null,"year":null,"rule1":null,"rule2":null,"day":null},{"id":29,"forecastStartDate":null,"compDate":null,"market":"New England","enbId":"71017_clone_edit01edit 02","enbName":"QUEENSBURY_Import test_clone2","growRequest":"NO","growCompleted":"YES","ciqPresent":"YES","envCompleted":"YES","standardNonStandard":"STANDARD","carriers":"Test","uda":"uda","softwareLevels":null,"feArrivalTime":"8:29 PM","ciStartTime":"8:29 PM","dtHandoff":"dt","ciEndTime":"8:29 PM","canRollComp":"ROLLEDBACK","traffic":"traffic","alarmPresent":"YES","ciEngineer":"Arun","ft":"admin","dt":"Ratul ","notes":"","column1":null,"totalLookup":null,"ranEngineer":null,"status":null,"revisit":null,"vlsm":null,"comments":null,"issue":null,"ci":null,"nonCi":null,"ald":null,"week":null,"month":null,"status2":null,"quarter":null,"year":null,"rule1":null,"rule2":null,"day":null},{"id":30,"forecastStartDate":"2019-03-28T00:00:00.000+0000","compDate":"2019-03-28T00:00:00.000+0000","market":"fgfgfgfgfg","enbId":"gdgf","enbName":"fgfgf","growRequest":"","growCompleted":"","ciqPresent":"","envCompleted":"","standardNonStandard":"","carriers":"","uda":"","softwareLevels":"","feArrivalTime":"","ciStartTime":"","dtHandoff":"","ciEndTime":"","canRollComp":"","traffic":"","alarmPresent":"","ciEngineer":"","ft":"","dt":"","notes":"","column1":null,"totalLookup":null,"ranEngineer":null,"status":null,"revisit":null,"vlsm":null,"comments":null,"issue":null,"ci":null,"nonCi":null,"ald":null,"week":null,"month":null,"status2":null,"quarter":null,"year":null,"rule1":null,"rule2":null,"day":null},{"id":31,"forecastStartDate":"2019-03-05T00:00:00.000+0000","compDate":"2019-03-28T00:00:00.000+0000","market":"hegd","enbId":"frg","enbName":"hfh","growRequest":"YES","growCompleted":"YES","ciqPresent":"YES","envCompleted":"","standardNonStandard":"","carriers":"","uda":"","softwareLevels":"","feArrivalTime":"","ciStartTime":"","dtHandoff":"","ciEndTime":"","canRollComp":"","traffic":"","alarmPresent":"","ciEngineer":"","ft":"","dt":"","notes":"","column1":null,"totalLookup":null,"ranEngineer":null,"status":null,"revisit":null,"vlsm":null,"comments":null,"issue":null,"ci":null,"nonCi":null,"ald":null,"week":null,"month":null,"status2":null,"quarter":null,"year":null,"rule1":null,"rule2":null,"day":null},{"id":32,"forecastStartDate":null,"compDate":null,"market":"New England","enbId":"71017.0","enbName":"QUEENSBURY_clone_overall edit ","growRequest":"NO","growCompleted":"YES","ciqPresent":"YES","envCompleted":null,"standardNonStandard":null,"carriers":null,"uda":null,"softwareLevels":null,"feArrivalTime":null,"ciStartTime":null,"dtHandoff":null,"ciEndTime":null,"canRollComp":null,"traffic":null,"alarmPresent":null,"ciEngineer":null,"ft":null,"dt":null,"notes":null,"column1":null,"totalLookup":"overall edit ","ranEngineer":"Ratul ","status":null,"revisit":null,"vlsm":null,"comments":null,"issue":null,"ci":null,"nonCi":null,"ald":null,"week":null,"month":null,"status2":null,"quarter":null,"year":null,"rule1":null,"rule2":null,"day":null},{"id":33,"forecastStartDate":"2019-03-28T00:00:00.000+0000","compDate":"2019-03-28T00:00:00.000+0000","market":"aA","enbId":"AaAaA","enbName":"Aa","growRequest":"","growCompleted":"","ciqPresent":"","envCompleted":"","standardNonStandard":"","carriers":"","uda":"","softwareLevels":"","feArrivalTime":"","ciStartTime":"","dtHandoff":"","ciEndTime":"","canRollComp":"","traffic":"","alarmPresent":"","ciEngineer":"","ft":"","dt":"","notes":"","column1":null,"totalLookup":null,"ranEngineer":null,"status":null,"revisit":null,"vlsm":null,"comments":null,"issue":null,"ci":null,"nonCi":null,"ald":null,"week":null,"month":null,"status2":null,"quarter":null,"year":null,"rule1":null,"rule2":null,"day":null},{"id":34,"forecastStartDate":"2019-03-28T00:00:00.000+0000","compDate":"2019-03-28T00:00:00.000+0000","market":"DSDSD","enbId":"SASAS","enbName":"SADSDSD","growRequest":"","growCompleted":"","ciqPresent":"","envCompleted":"","standardNonStandard":"","carriers":"","uda":"","softwareLevels":"","feArrivalTime":"","ciStartTime":"","dtHandoff":"","ciEndTime":"","canRollComp":"","traffic":"","alarmPresent":"","ciEngineer":"","ft":"","dt":"","notes":"","column1":null,"totalLookup":null,"ranEngineer":null,"status":null,"revisit":null,"vlsm":null,"comments":null,"issue":null,"ci":null,"nonCi":null,"ald":null,"week":null,"month":null,"status2":null,"quarter":null,"year":null,"rule1":null,"rule2":null,"day":null},{"id":35,"forecastStartDate":"2019-03-28T00:00:00.000+0000","compDate":"2019-03-28T00:00:00.000+0000","market":"New England","enbId":"SASAS","enbName":"SADSDSDSDSDSDSDSD","growRequest":"","growCompleted":"","ciqPresent":"","envCompleted":"","standardNonStandard":"","carriers":"","uda":"","softwareLevels":"","feArrivalTime":"","ciStartTime":"","dtHandoff":"","ciEndTime":"","canRollComp":"","traffic":"","alarmPresent":"","ciEngineer":"","ft":"","dt":"","notes":"","column1":null,"totalLookup":null,"ranEngineer":null,"status":null,"revisit":null,"vlsm":null,"comments":null,"issue":null,"ci":null,"nonCi":null,"ald":null,"week":null,"month":null,"status2":null,"quarter":null,"year":null,"rule1":null,"rule2":null,"day":null}],"username":["supriya"],"status":"SUCCESS"};
              this.tableData = {"sessionId":"5ea0bd6d","serviceToken":"99829","fenightDetailsList":{"market":null,"region":null,"feRegion":null,"feNight":[],"feDay":null},"pageCount":52,"enodebId":["","516798","540941","517405","517198","517435","540971","517434","517277","516928","598066","549138"],"feregionDetailsList":{"market":null,"region":null,"feRegion":["FECentral","FEWest"],"feNight":null,"feDay":null},"fedayDetailsList":{"market":null,"region":null,"feRegion":null,"feNight":null,"feDay":[]},"market":["Milwaukee","West Washington","South Bay","Central Illinois","Ft. Wayne / South Bend","Oregon/SW Washington","chglsmr04lems1","Chicago","Cincinnati","Oregon / SW Washington","chglsmr03lems1","West Michigan","Colorado","FIT Chicago","Columbus","akrlsmu01lep02","East Michigan","Indianapolis","Pittsburgh"],"marketDetailsList":{"market":["buffalo","Central Illinois","Central Iowa","Central Pennsylvania","Chicago","Cincinnati","Cleveland","Columbus","East Michigan","Ft. Wayne/South Bend","Indianpolis","Milwaukee","North Wisconsin","Pittsburgh","Toledo","West Iowa/Nebraska","West Michigan","Western Pennsylvania"],"region":null,"feRegion":null,"feNight":null,"feDay":null},"regionDetailsList":{"market":null,"region":["Central","West"],"feRegion":null,"feNight":null,"feDay":null},"region":["FIT Central","West","Central"],"overallSprintEntity":[{"id":256,"region":"","market":"Chicago","creationDate":null,"cascade":"CH80XC014","ciEngineerNight":null,"bridgeOne":null,"feRegion":"","feNight":null,"ciEngineerDay":null,"bridge":null,"feDay":null,"notes":null,"status":"RV/Migrated","startDate":null,"endDate":null,"startEndDate":"2019-03-05T00:00:00.000+0000","day":null,"week":null,"month":null,"qtr":null,"year":null,"type":"","eodType":"","siteRevisit":null,"goldenCluster":null,"actualMigrationStartDate":null,"compEndDate":null,"enbId":"517277","fiveG":null,"typeOne":null,"tvw":null,"currentSoftware":"","scriptsRan":"","dspImplemented":"","ciEngineerOne":"","ciStartTimeOne":"","ciEndTimeOne":"","feOne":"","feContactInfoOne":"","feArrivalTimeOne":"","ciEngineerTwo":"","ciStartTimeTwo":"","ciEndTimeTwo":"","feTwo":"","feContactInfoTwo":"","feArrivalTimeTwo":"","ciEngineerThree":"","ciStartTimeThree":"","ciEndTimeThree":"","feThree":"","feContactInfoThree":"","feArrivalTimeThree":"","gc":"","gcArrivalTime":"","putTool":"","scriptErrors":"","reasonCode":null,"ciIssue":"","nonCiIssue":"","engineerOneNotes":"","engineerTwoNotes":"","engineerThreeNotes":"","circuitbreakerStart":null,"circuitbreakerEnd":null,"alphaStartTime":"","alphaEndTime":"","betaStartTime":"","betaEndTime":"","gammaStartTime":"","gammaEndTime":"","scheduleDate":"2018-01-02T00:00:00.000+0000","compDate":null,"dtOrMw":"","tcName":"","tcContactInfo":"","resolution":"Hello","nvtfNoHarm":""},{"id":512,"region":"","market":"Chicago","creationDate":null,"cascade":"CH80XC014","ciEngineerNight":null,"bridgeOne":null,"feRegion":"","feNight":null,"ciEngineerDay":null,"bridge":null,"feDay":null,"notes":null,"status":"Canceled","startDate":null,"endDate":null,"startEndDate":null,"day":null,"week":null,"month":null,"qtr":null,"year":null,"type":"","eodType":"","siteRevisit":null,"goldenCluster":null,"actualMigrationStartDate":null,"compEndDate":null,"enbId":"517277","fiveG":null,"typeOne":null,"tvw":null,"currentSoftware":"","scriptsRan":"","dspImplemented":"","ciEngineerOne":"","ciStartTimeOne":"","ciEndTimeOne":"","feOne":"","feContactInfoOne":"","feArrivalTimeOne":"","ciEngineerTwo":"","ciStartTimeTwo":"","ciEndTimeTwo":"","feTwo":"","feContactInfoTwo":"","feArrivalTimeTwo":"","ciEngineerThree":"c1","ciStartTimeThree":"c2","ciEndTimeThree":"c3","feThree":"f1","feContactInfoThree":"f2","feArrivalTimeThree":"f3","gc":"","gcArrivalTime":"","putTool":"","scriptErrors":"","reasonCode":null,"ciIssue":"","nonCiIssue":"","engineerOneNotes":"","engineerTwoNotes":"","engineerThreeNotes":"Good notes","circuitbreakerStart":null,"circuitbreakerEnd":null,"alphaStartTime":"","alphaEndTime":"","betaStartTime":"","betaEndTime":"","gammaStartTime":"","gammaEndTime":"","scheduleDate":null,"compDate":null,"dtOrMw":"MW","tcName":"tcName","tcContactInfo":"tcContactInfo","resolution":"resolution","nvtfNoHarm":"nvtfNoHarm"},{"id":1,"region":"Central","market":"Chicago","creationDate":null,"cascade":"CH03XC022","ciEngineerNight":null,"bridgeOne":null,"feRegion":"FECentral","feNight":null,"ciEngineerDay":null,"bridge":null,"feDay":null,"notes":null,"status":"Migrated","startDate":"2018-11-13","endDate":null,"startEndDate":null,"day":null,"week":null,"month":null,"qtr":null,"year":null,"type":"","eodType":"","siteRevisit":null,"goldenCluster":null,"actualMigrationStartDate":null,"compEndDate":null,"enbId":"516265","fiveG":null,"typeOne":null,"tvw":null,"currentSoftware":"","scriptsRan":"Yes","dspImplemented":"","ciEngineerOne":"Daron Sy","ciStartTimeOne":"0.0625","ciEndTimeOne":"0.3333333333","feOne":"Latoya","feContactInfoOne":"","feArrivalTimeOne":"2:20:00 AM","ciEngineerTwo":"Vasudha Vanga","ciStartTimeTwo":"8:00:00 AM","ciEndTimeTwo":"12:20:00 PM","feTwo":"","feContactInfoTwo":"","feArrivalTimeTwo":"","ciEngineerThree":"","ciStartTimeThree":"","ciEndTimeThree":"","feThree":"","feContactInfoThree":"","feArrivalTimeThree":"","gc":"CCSI","gcArrivalTime":"5:00:00 AM","putTool":"No","scriptErrors":"Yes","reasonCode":null,"ciIssue":"No Issue","nonCiIssue":"No Issue","engineerOneNotes":"Site was handoff to day shift Vasudha Vanga to continue with the migration. After the SW upgraded to 7.0.3 r-04 load, All the TDD cells went down after the activation . \nDiscovered MAU FW Package as not pushed during SW/FW activation.  FW/SW Verification showed OK but MAU FW was missing from deployed packages\nUpgraded FW/SW using latest MOP and MAU FW upgrade was successful\n7.0.3 > (FW 7.1.1 / SW 7.1.0) > MMIMO > SW 7.1.1\nRemoved SW/FW issue from issue tab.","engineerTwoNotes":"","engineerThreeNotes":"","circuitbreakerStart":null,"circuitbreakerEnd":null,"alphaStartTime":"","alphaEndTime":"","betaStartTime":"","betaEndTime":"","gammaStartTime":"","gammaEndTime":"","scheduleDate":null,"compDate":"2018-11-14","dtOrMw":"","tcName":"","tcContactInfo":"","resolution":"","nvtfNoHarm":""}],"username":["matthew","thang","zaid","dykes"],"status":"SUCCESS"};
              this.totalPages = this.tableData.pageCount;
              this.marketList = this.tableData.market;
              this.userList = this.tableData.username;
              this.regionList = this.tableData.region;
              this.createMarketList = this.tableData.marketDetailsList.market;
              this.createRegionList = this.tableData.regionDetailsList.region;
              this.createFeregionList = this.tableData.feregionDetailsList.feRegion;
              //this.marketList=this.tableData.market;
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
              if (this.tableData.overallSprintEntity.length == 0) {
                this.tableShowHide = false;
                this.noDataVisibility = true;
              } else {
                this.sprintData = this.tableData.overallSprintEntity;
                this.tableShowHide = true;
                this.noDataVisibility = false;
                setTimeout(() => {
                  let tableWidth = document.getElementById('overallSprintEntity').scrollWidth;

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







  searchOverallReportDetails(event) {

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
                //"forecastStartDate": currentForm.querySelector("#searchForecastStartDate").value,
                //"forecastEndDate": currentForm.querySelector("#searchForecastEndDate").value,
                "market": currentForm.querySelector("#searchMarket").value,
                "enbId": currentForm.querySelector("#searchEnbId").value,
                "enbName": currentForm.querySelector("#searchEnbName").value,
                "status": currentForm.querySelector("#searchStatus").value,

              };

            if (searchCrtra.forecastStartDate || searchCrtra.forecastEndDate || searchCrtra.market || searchCrtra.enbId || searchCrtra.enbName || searchCrtra.status) {
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
                "startDate" : this.datePipe.transform(currentForm.querySelector("#searchStartStartDate").value, 'dd/MM/yyyy'),
                "startEndDate" : this.datePipe.transform(currentForm.querySelector("#searchStartEndDate").value, 'dd/MM/yyyy'),
                "compDate" : this.datePipe.transform(currentForm.querySelector("#searchCompStartDate").value, 'dd/MM/yyyy'),
                "compEndDate" : this.datePipe.transform(currentForm.querySelector("#searchCompEndDate").value, 'dd/MM/yyyy'),
                //"startDate": currentForm.querySelector("#searchStartStartDate").value,
                //"startEndDate": currentForm.querySelector("#searchStartEndDate").value,
                //"compDate": currentForm.querySelector("#searchCompStartDate").value,
                //"compEndDate": currentForm.querySelector("#searchCompEndDate").value,
                "region": currentForm.querySelector("#searchRegion").value,
                "market": currentForm.querySelector("#searchMarket").value,
                "cascade": currentForm.querySelector("#searchCascade").value,
                "enbId": currentForm.querySelector("#searchEnbId").value,
                //"overallType": currentForm.querySelector("#searchOverallType").value,
                "eodType": currentForm.querySelector("#searchEodType").value,
              };

            if (searchCrtra.startDate || searchCrtra.startEndDate || searchCrtra.compDate || searchCrtra.compEndDate || searchCrtra.region || searchCrtra.market || searchCrtra.cascade || searchCrtra.enbId || searchCrtra.eodType) {
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
          this.getOverallReportsDetails();
        }
      }
    }, 0);
  }




  downloadFile() {
    this.showLoader = true;
    this.overallReportsService.downloadFile(this.sharedService.createServiceToken(), this.custId, this.searchDetails, this.searchStatus)
      .subscribe(
        data => {
          this.showLoader = false;
          let blob = new Blob([data["_body"]], {
            type: "application/octet-stream"
          });

          FileSaver.saveAs(blob, "OverallReportDetails.xlsx");

        },
        error => {
          //Please Comment while checkIn
          /* let jsonStatue: any = { "sessionId": "506db794", "reason": "Download successful!", "status": "SUCCESS", "serviceToken": "63524" };
          data => {
            this.showLoader = false;
            let blob = new Blob([data["_body"]], {
              type: "application/octet-stream"
            });

            FileSaver.saveAs(blob, "OverallReportDetails.xlsx");
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
        formdata.append("overallReportsFile", files[0]);
        formdata.append(files[i].name, files[i]);
      } else {
        validFileType = false;
        this.displayModel("Invalid file type..... Supports .xlsx format", "failureIcon");
      }
    }

    if (validFileType) {
      setTimeout(() => {
        this.showLoader = true;
        this.overallReportsService.uploadFile(formdata, this.sharedService.createServiceToken(), this.custId)
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
                    this.getOverallReportsDetails();
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
                  this.getOverallReportsDetails();
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
    //  this.getOverallReportsDetails();
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

      this.getOverallReportsDetails();


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
      this.getOverallReportsDetails();
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
    //this.editableEODFormArray.splice(this.editableEODFormArray.indexOf(index), 1);
    this.editableEODFormArray = [];
    this.paginationDisabbled = false;
    this.checkFormEnable(index); //TODO : need to recheck this function
    this.sharedService.userNavigation = true; //un block user navigation
    currentEditedForm.lastElementChild.lastElementChild.children[0].className = "editRow";
  }

  checkFormEnable(index) {
    let indexValue = this.editableEODFormArray.indexOf(index);
    return indexValue >= 0 ? true : false;
  }



  custNameChange(selCustName) {
    this.editableEODFormArray = [];
    this.editIndex = -1;
    this.searchDetails = {};
    this.searchStatus = "load";
    if (selCustName == "") {
      this.custId = selCustName;
      this.showLoader = true;
      this.ngOnInit();
    }
    else {
      this.custId = selCustName;
      this.getOverallReportsDetails();
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
        let overallDetails = {};

        if (this.custId == 2) {
          //Verizon
          key.forecastStartDate = this.datePipe.transform(key.forecastStartDate, 'dd/MM/yyyy');
          //key.ciStartTime = this.datePipe.transform(key.ciStartTime, 'hh:mm a');
          //key.ciEndTime = this.datePipe.transform(key.ciEndTime, 'hh:mm a');
          overallDetails = key;
        } else if (this.custId == 4) {
          //Sprint
          key.startDate = this.datePipe.transform(key.startDate, 'dd/MM/yyyy');
          key.compDate = this.datePipe.transform(key.compDate, 'dd/MM/yyyy');
          key.scheduleDate = this.datePipe.transform(key.scheduleDate, 'dd/MM/yyyy');
          //key.ciStartTimeOne = this.datePipe.transform(key.ciStartTimeOne, 'hh:mm a');
          //key.ciEndTimeOne = this.datePipe.transform(key.ciEndTimeOne, 'hh:mm a');
          //key.feArrivalTimeOne = this.datePipe.transform(key.feArrivalTimeOne, 'hh:mm a');
          //key.ciEndTimeTwo = this.datePipe.transform(key.ciEndTimeTwo, 'hh:mm a');
          //key.ciStartTimeTwo = this.datePipe.transform(key.ciStartTimeTwo, 'hh:mm a');
          //key.feArrivalTimeTwo = this.datePipe.transform(key.feArrivalTimeTwo, 'hh:mm a');
          //key.gcArrivalTime = this.datePipe.transform(key.gcArrivalTime, 'hh:mm a');
          //key.alphaStartTime = this.datePipe.transform(key.alphaStartTime, 'hh:mm a');
          //key.alphaEndTime = this.datePipe.transform(key.alphaEndTime, 'hh:mm a');
          //key.betaStartTime = this.datePipe.transform(key.betaStartTime, 'hh:mm a');
          //key.betaEndTime = this.datePipe.transform(key.betaEndTime, 'hh:mm a');
          //key.gammaStartTime = this.datePipe.transform(key.gammaStartTime, 'hh:mm a');
          //key.gammaEndTime = this.datePipe.transform(key.gammaEndTime, 'hh:mm a');
          overallDetails = key;
        }
        this.overallReportsService.saveEodReportsDetails(overallDetails, this.sharedService.createServiceToken(), this.custId)
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
                      this.message = "Overall Report saved successfully!";
                      //  this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });                  
                      //this.createVerizonForm.clear();     
                      this.displayModel(this.message, "successIcon");
                      this.getOverallReportsDetails();
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
                  this.message = "Overall Report saved successfully!";
                  // this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });                  
                  //this.createVerizonForm.clear();  
                  this.displayModel(this.message, "successIcon");
                  this.getOverallReportsDetails();
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

        this.overallReportsService.deleteNeVersion(id, this.sharedService.createServiceToken(), this.custId)
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
                    this.message = "Overall Report deleted successfully!";
                    this.displayModel(this.message, "successIcon");
                    this.getOverallReportsDetails();
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
                  this.message = "Overall Report deleted successfully!";
                  this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: "static", size: 'lg', windowClass: "success-modal" });
                  this.getOverallReportsDetails();
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
        let overallDetails = {};

        if (this.custId == 2) {
          //Verizon
          key.id=null;
          key.forecastStartDate = this.datePipe.transform(key.forecastStartDate, 'dd/MM/yyyy');
          overallDetails = key;
        } else if (this.custId == 4) {
          //Sprint
          key.id=null;
          key.startDate = this.datePipe.transform(key.startDate, 'dd/MM/yyyy');
          key.compDate = this.datePipe.transform(key.compDate, 'dd/MM/yyyy');
          overallDetails = key;
        }
        
        this.overallReportsService.saveEodReportsDetails(overallDetails, this.sharedService.createServiceToken(), this.custId)
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
                      this.message = "Overall Report saved successfully!";
                      // this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });                  
                      //this.createVerizonForm.clear();                         
                      this.displayModel(this.message, "successIcon");
                      this.getOverallReportsDetails();
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
                  this.message = "Overall Report saved successfully!";
                  //this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });                  
                  // this.createVerizonForm.clear();  
                  this.displayModel(this.message, "successIcon");
                  this.getOverallReportsDetails();
                } else {
                  this.displayModel(jsonStatue.reason, "failureIcon");
                }

              }, 1000); */
              //Please Comment while checkIn
            });

        // }

      }, 0);
      console.log(key);
        console.log(cloneKey);
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

      this.overallReportsService.getCustomerIdList(this.sharedService.createServiceToken())
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

  onChangeDate(key) {
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
    dt = new Date(key.forecastStartDate.getFullYear(), 0, 1);
    key.week = "Week "+Math.ceil((((key.forecastStartDate - dt) / 86400000) + dt.getDay() + 1) / 7);
    key.month = month[key.forecastStartDate.getMonth()];
    key.day= days[key.forecastStartDate.getDay()];
    key.year= key.forecastStartDate.getFullYear();
    if(key.month=="Jan" || key.month=="Feb" || key.month=="Mar")
    {
      key.quarter="Qtr1"
    }
    else if(key.month=="Apr" || key.month=="May" || key.month=="Jun")
    {
      key.quarter="Qtr2"
    }
    else if(key.month=="Jul" || key.month=="Aug" || key.month=="Sep")
    {
      key.quarter="Qtr3"
    }
    else if(key.month=="Oct" || key.month=="Nov" || key.month=="Dec")
    {
      key.quarter="Qtr4"
    }

    //console.log("Week " + week);
  }



}
