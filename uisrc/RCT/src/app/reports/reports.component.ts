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
import { ReportsService } from '../services/reports.service';

@Component({
  selector: 'app-reports',
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.scss']
})
export class ReportsComponent implements OnInit {

  custList: any;
  tableEdit: boolean = false;
  currEditedRowVal: any;
  editableEODFormArray = [];
  addtableFormArray = [];
  selectedItems = [];
  editIndex: number = -1;
  regionList: any;
  searchBlock: boolean = false;
  auditBlock: boolean = false;
  failureReportBlock: boolean = false;
  createForm: boolean = true;
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
  min = new Date();
  curr = this.min.setDate(this.min.getDate() - 9);
  fromDate: any;
  fromDts:any;
  selectedDays:any;
  toDates:any;
  toDate: any;
  editableFormArray = [];
  totalPages: any; // for pagination
  tableData: any;
  programDropDownList: any;
  reportTableData:any;
  tableShowHide: boolean = false;
  reportTableShowHide:boolean = true;
  searchDetails: any;
  tableDataHeight: any;
  pageRenge: any; // for pagination
  noDataVisibility: boolean = false;
  noAuditReportsData: boolean = false;
  currentPage: any; // for pagination
  paginationDetails: any; // for pagination
  paginationDisabbled: boolean = false;
  TableRowLength: any; // for pagination
  pageSize: any; // for pagination
  pager: any = {}; // pager Object
  closeResult: string;
  custId: any;
  selectedProgramName : any = "";
  selProgramNameTemp : any = "";
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
  scheduleList = [];

  envModalData : any;
  envInfoModalBlock: any;
  ovInteractionRefreshInterval: any;
  STD_REFRESH_INTERVAL:any = 60000;
  searchFormField : any = {};
  searchExpand: boolean = false;
  failureSearchCriteria = {fromDate :"",toDate : "",neId : "",noOfDays : 0, programName: []};
  auditSearchCriteria = {fromDate :"",toDate : "",neName : "",neId : "",runTestId : null,siteName : "",programName : "", status : ""};
  searchColumnList = [{"label":"NE ID","key":"neId"},{"label":"SITE NAME","key":"siteName"},{"label":"Fetch Date","key":"fetchDate"},{"label":"Fetch Status","key":"fetchRemarks"},{"label":"CIQ generation Date","key":"ciqGenerationDate"},{"label":"CIQ Name","key":"ciqName"},{"label":"Premig-Grow Status","key":"preMigGrowStatus"},{"label":"Premig-Grow Generation Date","key":"preMigGrowGenerationDate"},{"label":"Premig ENV - Status","key":"envStatus"},{"label":"ENV Filename","key":"envFileName"},{"label":"Premig ENV Generation Date","key":"envGenerationDate"},{"label":"ENV Export Status","key":"envExportStatus"},{"label":"NE Grow Staus","key":"neGrowStatus"},{"label":"Migration Start Date","key":"migrationScheduledTime"},{"label":"Migration Completed Date","key":"migrationCompleteTime"},{"label":"Migration Status","key":"migStatus"},{"label":"OV TRACKER ID","key":"trackerId"},{"label":"OV WORKPLAN ID","key":"workPlanID"},{"label":"MIGRATION STRATEGY","key":"integrationType"}];
  vDartReportCheck: boolean = false;
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
  validationData1: any = {
    "rules": {
      "searchRunId":{
        "customfunction":false
      }
    },
    "messages": {
      "searchRunId": {
          "customfunction" : "Run Id should be a number"
      }
    }
  };

  @ViewChild('confirmModal') confirmModalRef: ElementRef;
  @ViewChild('bluePrintForm') bluePrintForm;
  // @ViewChild('searchForm') searchForm;
  @ViewChild('neversionContainer', { read: ViewContainerRef }) neversionContainer;
  @ViewChild('createTab') createTabRef: ElementRef;
  @ViewChild('searchTab') searchTabRef: ElementRef;
  @ViewChild('auditTab') auditTabRef: ElementRef;
  @ViewChild('failureReportTab') failureReportTabRef: ElementRef;
  @ViewChild('sessionExpiredModal') sessionExpiredModalRef: ElementRef;
  @ViewChild('successModal') successModalRef: ElementRef;
  @ViewChild('filePost') filePostRef: ElementRef;
  @ViewChild('auditForm') auditForm;
  @ViewChild('failureReportForm') failureReportForm;
  @ViewChild('searchFormOV') searchForm;
  progName: any;
  programname: any;
  filter: any;
  dropdownSettings: { singleSelection: boolean; idField: string; textField: string; selectAllText: string; unSelectAllText: string; itemsShowLimit: number; allowSearchFilter: boolean; };
  selprogNameList: any;
  dropdownSettings1 = {
    singleSelection: false,
    idField: 'item_id',
    textField: 'item_text',
    selectAllText: 'Select All',
    unSelectAllText: 'UnSelect All',
    itemsShowLimit: 1,
    allowSearchFilter: true
};
  filterShowHide: boolean;
  selprogName: any;
  selfetchdays: any;
  searchCriteria: { userName: string; userFullName: string; roleId: string; customerId: string; programNamehidden: string; emailId: string; status: string; createdBy: string; };
  tableShowwHide: boolean;
  ovData: any;
  jsonType: any;
  order_number: any;
  preMigGrowGenerationDate: any;
  preMigGrowStatus: any;
  envGenerationDate: any;
  envStatus: any;
  fetchRemarks: any;
  selectedfetchdays: any;
  



  constructor(
    private overallReportsService: OverallreportsService,
    private element: ElementRef,
    private reportsService:ReportsService,
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
    this.auditBlock = false;
    this.failureReportBlock = false;
    this.createForm = false;
    this.totalPages = 1;
    this.currentPage = 1;
    this.TableRowLength = 10;
    this.pageSize = 10;
    this.selCustName = "";
    this.tableShowwHide = false;
    this.showLoader = false;
    this.selectedItems = [];
    this.setMenuHighlight("search");
    this.searchStatus = 'load';
    this.editableFormArray = [];
    this.searchCriteria = null;
    this.selectedProgramName = "";
    this.selProgramNameTemp = "";
    this.envModalData = "";
    this.programDropDownList = [];
    this.vDartReportCheck = false;
    let paginationDetails = {
      "count": parseInt(this.TableRowLength, 10),
      "page": this.currentPage
    };
    this.paginationDetails = paginationDetails;
    this.currentUser = JSON.parse(sessionStorage.loginDetails).userGroup;
    this.selprogNameList = [];
    this.getOvDetails();
    this.getCustomerList();


    //JSON.parse(sessionStorage.selectedCustomerList).id,
  }

  loadData(){
    this.totalPages = 1;
    this.currentPage = 1;
    this.TableRowLength = 10;
    this.pageSize = 10;
    this.selCustName = "";
    this.tableShowHide = false;
    this.showLoader = false;
    this.searchStatus = 'load';
    this.editableFormArray = [];
    this.searchCriteria = null;
    let paginationDetails = {
      "count": parseInt(this.TableRowLength, 10),
      "page": this.currentPage
    };
    this.paginationDetails = paginationDetails;

  }
  searchTabBind() {
    this.setMenuHighlight("search");
    this.createForm = false;
    this.searchBlock = true;
    this.auditBlock = false;
    this.failureReportBlock = false;
    this.tableShowwHide= true;
    this.selprogName = "";
    this.selfetchdays = "";
    this.selectedProgramName = "";
    this.selProgramNameTemp = "";
    this.custId = "";
    this.editableFormArray = [];
    this.searchCriteria = null;
    this.loadData();
    this.getOvDetails();
  }

  createNew(event){
    this.searchBlock = false;
    this.createForm = true;
    this.auditBlock = false;
    this.failureReportBlock = false;
    this.searchStatus = 'load';
    this.searchCriteria = null;
    this.setMenuHighlight("create");
    this.showLoader = true;
    this.selprogName = "";
    this.selfetchdays ="";
    this.selectedProgramName = "";
    this.selProgramNameTemp = "";
    this.custId = "";
    this.editableFormArray = [];
    if (this.currentUser != "Super Administrator") {

      this.custId = JSON.parse(sessionStorage.selectedCustomerList).id;
      this.selCustName = JSON.parse(sessionStorage.selectedCustomerList).id;
      //this.custId=2;
      this.getOverallReportsDetails();
      //this.showLoader = true;
    }
    // Stops the refresh of OV Table
    clearInterval(this.ovInteractionRefreshInterval);
    this.ovInteractionRefreshInterval = null;

    this.loadData();   
    this.getCustomerList();
  }

  auditTabBind() {
    this.setMenuHighlight("audit");
    this.createForm = false;
    this.searchBlock = false;
    this.failureReportBlock = false; 
    this.auditBlock = true;
    this.reportTableShowHide=true;
    this.noAuditReportsData = true;
    this.getReports(this.auditSearchCriteria);  
  }
  failureReportTabBind() {
    this.setMenuHighlight("failureReport");
    this.createForm = false;
    this.searchBlock = false;
    this.auditBlock = false;
    this.failureReportBlock = true; 
  }
  setMenuHighlight(selectedElement) {
    this.createTabRef.nativeElement.id = (selectedElement == "create") ? "activeTab" : "inactiveTab";
    //  this.createTabRef.nativeElement.id = (selectedElement == "create") ? "activeTab" : "inactiveTab";
    this.searchTabRef.nativeElement.id = (selectedElement == "search") ? "activeTab" : "inactiveTab";
    this.auditTabRef.nativeElement.id = (selectedElement == "audit") ? "activeTab" : "inactiveTab";
    this.failureReportTabRef.nativeElement.id = (selectedElement == "failureReport") ? "activeTab" : "inactiveTab";
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
    clearInterval(this.ovInteractionRefreshInterval);
    this.ovInteractionRefreshInterval = null;
  }


  clearSearchFrom() {
    if(this.searchForm) {
      this.searchForm.nativeElement.reset();
    }
    this.searchStatus = "load";
    this.searchCriteria = null;
    this.searchFormField = {};
  }


  getOverallReportsDetails() {
    this.tableShowHide = false;
    this.editIndex = -1;
    this.showLoader = true;
    $("#dataWrapper").find(".scrollBody").scrollLeft(0);
    this.reportsService.getOverallReportsDetails(this.searchStatus, this.searchDetails, this.sharedService.createServiceToken(), this.paginationDetails, this.custId,this.selectedProgramName)
      .subscribe(
        data => {
          setTimeout(() => {
            if (this.custId == 2) {
              this.editableEODFormArray = [];

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

                     this.showLoader = false;
                     this.totalPages = jsonStatue.pageCount;
                     if(this.selectedProgramName.includes("MM"))
                     {
                      this.filter = jsonStatue.Mm;
                      this.selectedItems = this.filter;
                     }
                     else if(this.selectedProgramName.includes("DSS"))
                     {
                      this.filter = jsonStatue.Dss;
                      this.selectedItems = this.filter;

                     }
                     else if(this.selectedProgramName.includes("USM-LIVE"))
                     {
                      this.filter = jsonStatue.program_4g;
                      this.selectedItems = this.filter;

                     }
                     else if(this.selectedProgramName.includes("FSU"))
                     {
                      this.filter = jsonStatue.program_4g_fsu;
                      this.selectedItems = this.filter;

                     }
                     else
                     {
                      this.filter = jsonStatue.All;
                      this.selectedItems = this.filter;
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
                    let pageCount = [];
                    for (var i = 1; i <= jsonStatue.pageCount; i++) {
                      pageCount.push(i);
                    }
                    this.pageRenge = pageCount;
                    this.pager = this.getPager(this.totalPages * this.pageSize, this.currentPage, this.pageRenge);

                    if (jsonStatue.overallVerizonModelList.length == 0) {
                      this.tableShowHide = false;
                      this.noDataVisibility = true;
                      this.filterShowHide = false;
                    } else {
                      this.verizonData = jsonStatue.overallVerizonModelList;
                      this.tableShowHide = true;
                      this.noDataVisibility = false;
                      this.filterShowHide = true;

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

              this.tableData = {"Mm":["START DATE","END DATE","MARKET","CIQ_NAME","NE_NAME","NE_ID","RAN_ENGINEER","PRE_MIG_ENV","PRE_GROW_TEMPLATE","PRE_COMM_SCRIPT","PRE_MIG_ENDC","NE_GROW_PNP","NE_GROW_AUCaCell","NE_GROW_AU","MIG_CACELL","MIG_AU","MIG_COMMISION_SCRIPT","MIG_ACPF_A1A2","MIG_CSL","MIG_ENDC_X2","MIG_ANCHOR_CSL","MIG_NBR","POST_MIG_ACPF_AUDIT","POST_MIG_AUPF_AUDIT","POST_AU_AUDIT","POST_ENDC_AUDIT","SITE_DATA_STATUS","REMARKS","PROGRAM_NAME"],"All":["START DATE","END DATE","MARKET","CIQ_NAME","NE_NAME","NE_ID","RAN_ENGINEER","PRE_MIG_ENV","PRE_GROW_TEMPLATE","PRE_COMM_SCRIPT","PRE_MIG_ENDC","NE_GROW_PNP","NE_GROW_AUCaCell","NE_GROW_AU","NE_GROW_ENB","NE_GROW_CELL","MIG_CACELL","MIG_AU","MIG_COMMISION_SCRIPT","MIG_ACPF_A1A2","MIG_CSL","MIG_ENDC_X2","MIG_ANCHOR_CSL","MIG_NBR","MIG_RF","MIG_PRE_CHECK_RF","MIG_CUTOVER_RF","MIG_ROLLBACK_RF","POST_AU_AUDIT","POST_ENDC_AUDIT","POST_MIG_ATP","POST_MIG_AUDIT","POST_MIG_VDU_AUDIT","POST_MIG_eNB_AUDIT","POST_MIG_AUPF_AUDIT","POST_MIG_ACPF_AUDIT","POST_MIG_FSU_AUDIT","SITE_DATA_STATUS","REMARKS","PROGRAM_NAME"],"pageCount":1,"Dss":["START DATE","END DATE","MARKET","CIQ_NAME","NE_NAME","NE_ID","RAN_ENGINEER","PRE_MIG_ENV","PRE_GROW_TEMPLATE","PRE_COMM_SCRIPT","PRE_MIG_ENDC","MIG_PRE_CHECK_RF","MIG_CUTOVER_RF","MIG_ROLLBACK_RF","POST_MIG_VDU_AUDIT","POST_MIG_eNB_AUDIT","POST_MIG_AUPF_AUDIT","POST_MIG_ACPF_AUDIT","POST_MIG_FSU_AUDIT","SITE_DATA_STATUS","REMARKS","PROGRAM_NAME"],"sessionId":"9680568c","serviceToken":"78839","overallVerizonModelList":[{"reportsMap":{"Start Date":{"headerName":"START DATE","headerValue":null},"End Date":{"headerName":"END DATE","headerValue":null},"Market":{"headerName":"MARKET","headerValue":"houstan"},"Ciq Name":{"headerName":"CIQ_NAME","headerValue":"Sacremento"},"Ne Name":{"headerName":"NE_NAME","headerValue":null},"Ne Id":{"headerName":"NE_ID","headerValue":"1200059121"},"Engineer":{"headerName":"RAN_ENGINEER","headerValue":null},"PreMig Env":{"headerName":"PRE_MIG_ENV","headerValue":"Complted"},"PreMig Grow":{"headerName":"PRE_GROW_TEMPLATE","headerValue":null},"PreMig Commission":{"headerName":"PRE_COMM_SCRIPT","headerValue":null},"PreMig Endc":{"headerName":"PRE_MIG_ENDC","headerValue":null},"Ne grow Pnp":{"headerName":"NE_GROW_PNP","headerValue":null},"Ne grow AucaCell":{"headerName":"NE_GROW_AUCaCell","headerValue":null},"Ne grow Au":{"headerName":"NE_GROW_AU","headerValue":null},"Ne grow Enb":{"headerName":"NE_GROW_ENB","headerValue":null},"Ne Grow Cell":{"headerName":"NE_GROW_CELL","headerValue":null},"Mig Cacell":{"headerName":"MIG_CACELL","headerValue":null},"Mig Au":{"headerName":"MIG_AU","headerValue":null},"Mig Commission":{"headerName":"MIG_COMMISION_SCRIPT","headerValue":null},"Mig Acpf":{"headerName":"MIG_ACPF_A1A2","headerValue":null},"Mig Csl":{"headerName":"MIG_CSL","headerValue":null},"Mig Endc":{"headerName":"MIG_ENDC_X2","headerValue":null},"Mig Anchor":{"headerName":"MIG_ANCHOR_CSL","headerValue":null},"Mig NBR":{"headerName":"MIG_NBR","headerValue":null},"Mig RF":{"headerName":"MIG_RF","headerValue":null},"Mig PreCheck":{"headerName":"MIG_PRE_CHECK_RF","headerValue":null},"Mig Cutover":{"headerName":"MIG_CUTOVER_RF","headerValue":null},"Mig RollBack":{"headerName":"MIG_ROLLBACK_RF","headerValue":null},"PostMig ACPF":{"headerName":"POST_AU_AUDIT","headerValue":null},"PostMig AUPF":{"headerName":"POST_ENDC_AUDIT","headerValue":null},"PostMig AU":{"headerName":"POST_MIG_ATP","headerValue":null},"PostMig ENDC":{"headerName":"POST_MIG_AUDIT","headerValue":null},"PostMig ATP":{"headerName":"POST_MIG_VDU_AUDIT","headerValue":null},"PostMig Audit":{"headerName":"POST_MIG_eNB_AUDIT","headerValue":null},"PostMig VDU Audit":{"headerName":"POST_MIG_AUPF_AUDIT","headerValue":null},"PostMig ENB":{"headerName":"POST_MIG_ACPF_AUDIT","headerValue":null},"PostMig FSU Audit":{"headerName":"POST_MIG_FSU_AUDIT","headerValue":null},"Site Data":{"headerName":"SITE_DATA_STATUS","headerValue":null},"Remarks":{"headerName":"REMARKS","headerValue":null},"Program Name":{"headerName":"PROGRAM_NAME","headerValue":"VZN-5G-MM"}}},{"reportsMap":{"Start Date":{"headerName":"START DATE","headerValue":null},"End Date":{"headerName":"END DATE","headerValue":null},"Market":{"headerName":"MARKET","headerValue":"ALLMarket"},"Ciq Name":{"headerName":"CIQ_NAME","headerValue":"Sacremento"},"Ne Name":{"headerName":"NE_NAME","headerValue":null},"Ne Id":{"headerName":"NE_ID","headerValue":"1200059122"},"Engineer":{"headerName":"RAN_ENGINEER","headerValue":null},"PreMig Env":{"headerName":"PRE_MIG_ENV","headerValue":"Complted"},"PreMig Grow":{"headerName":"PRE_GROW_TEMPLATE","headerValue":null},"PreMig Commission":{"headerName":"PRE_COMM_SCRIPT","headerValue":null},"PreMig Endc":{"headerName":"PRE_MIG_ENDC","headerValue":null},"Ne grow Pnp":{"headerName":"NE_GROW_PNP","headerValue":null},"Ne grow AucaCell":{"headerName":"NE_GROW_AUCaCell","headerValue":null},"Ne grow Au":{"headerName":"NE_GROW_AU","headerValue":null},"Ne grow Enb":{"headerName":"NE_GROW_ENB","headerValue":null},"Ne Grow Cell":{"headerName":"NE_GROW_CELL","headerValue":null},"Mig Cacell":{"headerName":"MIG_CACELL","headerValue":null},"Mig Au":{"headerName":"MIG_AU","headerValue":null},"Mig Commission":{"headerName":"MIG_COMMISION_SCRIPT","headerValue":null},"Mig Acpf":{"headerName":"MIG_ACPF_A1A2","headerValue":null},"Mig Csl":{"headerName":"MIG_CSL","headerValue":null},"Mig Endc":{"headerName":"MIG_ENDC_X2","headerValue":null},"Mig Anchor":{"headerName":"MIG_ANCHOR_CSL","headerValue":null},"Mig NBR":{"headerName":"MIG_NBR","headerValue":null},"Mig RF":{"headerName":"MIG_RF","headerValue":null},"Mig PreCheck":{"headerName":"MIG_PRE_CHECK_RF","headerValue":null},"Mig Cutover":{"headerName":"MIG_CUTOVER_RF","headerValue":null},"Mig RollBack":{"headerName":"MIG_ROLLBACK_RF","headerValue":null},"PostMig ACPF":{"headerName":"POST_AU_AUDIT","headerValue":null},"PostMig AUPF":{"headerName":"POST_ENDC_AUDIT","headerValue":null},"PostMig AU":{"headerName":"POST_MIG_ATP","headerValue":null},"PostMig ENDC":{"headerName":"POST_MIG_AUDIT","headerValue":null},"PostMig ATP":{"headerName":"POST_MIG_VDU_AUDIT","headerValue":null},"PostMig Audit":{"headerName":"POST_MIG_eNB_AUDIT","headerValue":null},"PostMig VDU Audit":{"headerName":"POST_MIG_AUPF_AUDIT","headerValue":null},"PostMig ENB":{"headerName":"POST_MIG_ACPF_AUDIT","headerValue":null},"PostMig FSU Audit":{"headerName":"POST_MIG_FSU_AUDIT","headerValue":null},"Site Data":{"headerName":"SITE_DATA_STATUS","headerValue":null},"Remarks":{"headerName":"REMARKS","headerValue":null},"Program Name":{"headerName":"PROGRAM_NAME","headerValue":"VZN-4G-USM-LIVE"}}},{"reportsMap":{"Start Date":{"headerName":"START DATE","headerValue":null},"End Date":{"headerName":"END DATE","headerValue":null},"Market":{"headerName":"MARKET","headerValue":"Nola"},"Ciq Name":{"headerName":"CIQ_NAME","headerValue":"Sacremento"},"Ne Name":{"headerName":"NE_NAME","headerValue":null},"Ne Id":{"headerName":"NE_ID","headerValue":"1200059123"},"Engineer":{"headerName":"RAN_ENGINEER","headerValue":null},"PreMig Env":{"headerName":"PRE_MIG_ENV","headerValue":"Complted"},"PreMig Grow":{"headerName":"PRE_GROW_TEMPLATE","headerValue":null},"PreMig Commission":{"headerName":"PRE_COMM_SCRIPT","headerValue":null},"PreMig Endc":{"headerName":"PRE_MIG_ENDC","headerValue":null},"Ne grow Pnp":{"headerName":"NE_GROW_PNP","headerValue":null},"Ne grow AucaCell":{"headerName":"NE_GROW_AUCaCell","headerValue":null},"Ne grow Au":{"headerName":"NE_GROW_AU","headerValue":null},"Ne grow Enb":{"headerName":"NE_GROW_ENB","headerValue":null},"Ne Grow Cell":{"headerName":"NE_GROW_CELL","headerValue":null},"Mig Cacell":{"headerName":"MIG_CACELL","headerValue":null},"Mig Au":{"headerName":"MIG_AU","headerValue":null},"Mig Commission":{"headerName":"MIG_COMMISION_SCRIPT","headerValue":null},"Mig Acpf":{"headerName":"MIG_ACPF_A1A2","headerValue":null},"Mig Csl":{"headerName":"MIG_CSL","headerValue":null},"Mig Endc":{"headerName":"MIG_ENDC_X2","headerValue":null},"Mig Anchor":{"headerName":"MIG_ANCHOR_CSL","headerValue":null},"Mig NBR":{"headerName":"MIG_NBR","headerValue":null},"Mig RF":{"headerName":"MIG_RF","headerValue":null},"Mig PreCheck":{"headerName":"MIG_PRE_CHECK_RF","headerValue":null},"Mig Cutover":{"headerName":"MIG_CUTOVER_RF","headerValue":null},"Mig RollBack":{"headerName":"MIG_ROLLBACK_RF","headerValue":null},"PostMig ACPF":{"headerName":"POST_AU_AUDIT","headerValue":null},"PostMig AUPF":{"headerName":"POST_ENDC_AUDIT","headerValue":null},"PostMig AU":{"headerName":"POST_MIG_ATP","headerValue":null},"PostMig ENDC":{"headerName":"POST_MIG_AUDIT","headerValue":null},"PostMig ATP":{"headerName":"POST_MIG_VDU_AUDIT","headerValue":null},"PostMig Audit":{"headerName":"POST_MIG_eNB_AUDIT","headerValue":null},"PostMig VDU Audit":{"headerName":"POST_MIG_AUPF_AUDIT","headerValue":null},"PostMig ENB":{"headerName":"POST_MIG_ACPF_AUDIT","headerValue":null},"PostMig FSU Audit":{"headerName":"POST_MIG_FSU_AUDIT","headerValue":null},"Site Data":{"headerName":"SITE_DATA_STATUS","headerValue":null},"Remarks":{"headerName":"REMARKS","headerValue":null},"Program Name":{"headerName":"PROGRAM_NAME","headerValue":"VZN-5G-DSS"}}}],"program_4g":["START DATE","END DATE","MARKET","CIQ_NAME","NE_NAME","NE_ID","RAN_ENGINEER","PRE_MIG_ENV","PRE_GROW_TEMPLATE","PRE_COMMISSION_SCRIPT","NE_GROW_PNP","NE_GROW_ENB","NE_GROW_CELL","MIG_RF","MIG_COMMISION_SCRIPT","POST_MIG_ATP","POST_MIG_AUDIT","SITE_DATA_STATUS","REMARKS","PROGRAM_NAME"],"status":"SUCCESS"};
              this.totalPages = this.tableData.pageCount;
              if(this.selectedProgramName.includes("MM"))
              {
               this.filter = this.tableData.Mm;
               this.selectedItems = this.filter;
              }
              else if(this.selectedProgramName.includes("DSS"))
              {
               this.filter = this.tableData.Dss;
               this.selectedItems = this.filter;

              }
              else if(this.selectedProgramName.includes("USM-LIVE"))
              {
                this.filter = this.tableData.program_4g;
                this.selectedItems = this.filter;
              }
              else if(this.selectedProgramName.includes("FSU"))
              {
                this.filter = this.tableData.program_4g_fsu;
                this.selectedItems = this.filter;

              }
              else
              {
               this.filter = this.tableData.All;
               this.selectedItems = this.filter;
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
                this.filterShowHide = false;

              } else {
                this.verizonData = this.tableData.overallVerizonModelList;
                this.tableShowHide = true;
                this.noDataVisibility = false;
                this.filterShowHide = true;

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

  getOvDetails() {
    this.tableShowwHide = true;
    this.editIndex = -1;
    this.showLoader = true;
    $("#dataWrapper").find(".scrollBody").scrollLeft(0);
    this.reportsService.getOvDetails(this.searchStatus,this.sharedService.createServiceToken(), this.paginationDetails,this.selectedProgramName, this.searchCriteria)
      .subscribe(
        data => {
          setTimeout(() => {
              let jsonStatue = data.json();
              this.tableData = jsonStatue;
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

                     this.showLoader = false;
                     this.totalPages = jsonStatue.pageCount;
                    let pageCount = [];
                    for (var i = 1; i <= jsonStatue.pageCount; i++) {
                      pageCount.push(i);
                    }
                    this.pageRenge = pageCount;
                    this.pager = this.getPager(this.totalPages * this.pageSize, this.currentPage, this.pageRenge);

                    if (jsonStatue.ovScheduledDetails.length == 0) {
                      this.tableShowwHide = false;
                      this.noDataVisibility = true;
                    } else {
                      this.ovData = jsonStatue.ovScheduledDetails;
                      this.tableShowwHide = true;
                      this.noDataVisibility = false;

                      // Refresh the OV table on every STD_REFRESH_INTERVAL
                      if (!this.ovInteractionRefreshInterval) {
                        this.ovInteractionRefreshInterval = setInterval(() => {
                          this.updateOVTable();
                        }, this.STD_REFRESH_INTERVAL);
                      }

                      setTimeout(() => {
                        let tableWidth = document.getElementById('ovScheduledDetails').scrollWidth;
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
                    this.createScheduleJosn(this.tableData.fetchDays);
                  } else {
                    this.showLoader = false;
                    this.displayModel(jsonStatue.reason, "failureIcon");
                  }

                }
                else{
                  
                    this.showLoader = false;
                    this.displayModel(jsonStatue.reason,"failureIcon");  
                   
                }
              }

          }, 1000);
        },
        error => {
          //Please Comment while checkIn
           /* setTimeout(() => {
            
              this.showLoader = false;

              // this.tableData = {"pageCount":1,"ovScheduledDetails":[{"id":14,"customerDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-04-23T07:55:01.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"ciqName":"CIQ_20210430_140500.xlxs","neId":"134742","fetchDate":"2021-04-30T09:05:25.000+0000","premigrationScheduledTime":"2021-04-10","migrationScheduledTime":"2021-04-15","neGrowScheduledTime":"2021-04-12","postmigrationAuditScheduledTime":"2021-04-15","ranAtpScheduledTime":"2021-04-15","envFileExportScheduledTime":"2021-04-11","premigrationReScheduledTime":"2021-04-13","migrationReScheduledTime":null,"neGrowReScheduledTime":"2021-04-13","postmigrationAuditReScheduledTime":null,"ranAtpReScheduledTime":null,"envFileExportReScheduledTime":"2021-04-13","trackerId":"1001724179","orderNumber":null,"workPlanID":"100136988","preMigStatus":"SCHEDULED","neGrowStatus":"SCHEDULED","migStatus":"SCHEDULED","postMigAuditStatus":"SCHEDULED","postMigRanAtpStatus":"SCHEDULED","envExportStatus":"SCHEDULED","envFileName":null,"envFilePath":null,"workFlowManagementEntity":null,"ciqFilePath":"/home/bala/Samsung/SMART/Customer/34/PreMigration/Input/CIQ/"},{"id":15,"customerDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-04-23T07:55:01.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"ciqName":"CIQ_20210430_140500.xlxs","neId":"140149","fetchDate":"2021-04-30T09:05:25.000+0000","premigrationScheduledTime":"2021-04-10","migrationScheduledTime":"2021-04-15","neGrowScheduledTime":"2021-04-12","postmigrationAuditScheduledTime":"2021-04-15","ranAtpScheduledTime":"2021-04-15","envFileExportScheduledTime":"2021-04-11","premigrationReScheduledTime":"2021-04-13","migrationReScheduledTime":null,"neGrowReScheduledTime":"2021-04-13","postmigrationAuditReScheduledTime":null,"ranAtpReScheduledTime":null,"envFileExportReScheduledTime":"2021-04-13","trackerId":"1001724136","orderNumber":null,"workPlanID":"100136988","preMigStatus":"SCHEDULED","neGrowStatus":"SCHEDULED","migStatus":"SCHEDULED","postMigAuditStatus":"SCHEDULED","postMigRanAtpStatus":"SCHEDULED","envExportStatus":"SCHEDULED","envFileName":null,"envFilePath":null,"workFlowManagementEntity":null,"ciqFilePath":"/home/bala/Samsung/SMART/Customer/34/PreMigration/Input/CIQ/"},{"id":16,"customerDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-04-23T07:55:01.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"ciqName":"","neId":"139650","fetchDate":"2021-04-30T09:05:25.000+0000","premigrationScheduledTime":"2021-04-10","migrationScheduledTime":"2021-04-15","neGrowScheduledTime":"2021-04-12","postmigrationAuditScheduledTime":"2021-04-15","ranAtpScheduledTime":"2021-04-15","envFileExportScheduledTime":"2021-04-11","premigrationReScheduledTime":"2021-04-13","migrationReScheduledTime":null,"neGrowReScheduledTime":"2021-04-13","postmigrationAuditReScheduledTime":null,"ranAtpReScheduledTime":null,"envFileExportReScheduledTime":"2021-04-13","trackerId":"1001724136","orderNumber":null,"workPlanID":"100136988","preMigStatus":"SCHEDULED","neGrowStatus":"SCHEDULED","migStatus":"SCHEDULED","postMigAuditStatus":"SCHEDULED","postMigRanAtpStatus":"SCHEDULED","envExportStatus":"SCHEDULED","envFileName":null,"envFilePath":null,"workFlowManagementEntity":null,"ciqFilePath":""},{"id":17,"customerDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-04-23T07:55:01.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"ciqName":"CIQ_20210430_140500.xlxs","neId":"140159","fetchDate":"2021-04-30T09:05:25.000+0000","premigrationScheduledTime":"2021-04-10","migrationScheduledTime":"2021-04-15","neGrowScheduledTime":"2021-04-12","postmigrationAuditScheduledTime":"2021-04-15","ranAtpScheduledTime":"2021-04-15","envFileExportScheduledTime":"2021-04-11","premigrationReScheduledTime":"2021-04-13","migrationReScheduledTime":null,"neGrowReScheduledTime":"2021-04-13","postmigrationAuditReScheduledTime":null,"ranAtpReScheduledTime":null,"envFileExportReScheduledTime":"2021-04-13","trackerId":"1001724137","orderNumber":null,"workPlanID":"100136988","preMigStatus":"SCHEDULED","neGrowStatus":"SCHEDULED","migStatus":"SCHEDULED","postMigAuditStatus":"SCHEDULED","postMigRanAtpStatus":"SCHEDULED","envExportStatus":"SCHEDULED","envFileName":null,"envFilePath":null,"workFlowManagementEntity":null,"ciqFilePath":"/home/bala/Samsung/SMART/Customer/34/PreMigration/Input/CIQ/"},{"id":18,"customerDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-04-23T07:55:01.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"ciqName":"CIQ_20210430_140500.xlxs","neId":"101381","fetchDate":"2021-04-30T09:05:25.000+0000","premigrationScheduledTime":"2021-04-10","migrationScheduledTime":"2021-04-15","neGrowScheduledTime":"2021-04-12","postmigrationAuditScheduledTime":"2021-04-15","ranAtpScheduledTime":"2021-04-15","envFileExportScheduledTime":"2021-04-11","premigrationReScheduledTime":"2021-04-13","migrationReScheduledTime":null,"neGrowReScheduledTime":"2021-04-13","postmigrationAuditReScheduledTime":null,"ranAtpReScheduledTime":null,"envFileExportReScheduledTime":"2021-04-13","trackerId":"1001724137","orderNumber":null,"workPlanID":"100136988","preMigStatus":"SCHEDULED","neGrowStatus":"SCHEDULED","migStatus":"SCHEDULED","postMigAuditStatus":"SCHEDULED","postMigRanAtpStatus":"SCHEDULED","envExportStatus":"SCHEDULED","envFileName":null,"envFilePath":null,"workFlowManagementEntity":null,"ciqFilePath":"/home/bala/Samsung/SMART/Customer/34/PreMigration/Input/CIQ/"},{"id":13,"customerDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-04-23T07:55:01.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"ciqName":"CIQ_20210413_12:55:00.xlxs","neId":"139647","fetchDate":"2021-04-30T09:05:22.000+0000","premigrationScheduledTime":"2021-04-10","migrationScheduledTime":"2021-04-15","neGrowScheduledTime":"2021-04-12","postmigrationAuditScheduledTime":"2021-04-15","ranAtpScheduledTime":"2021-04-15","envFileExportScheduledTime":"2021-04-11","premigrationReScheduledTime":"2021-04-13","migrationReScheduledTime":null,"neGrowReScheduledTime":"2021-04-13","postmigrationAuditReScheduledTime":null,"ranAtpReScheduledTime":null,"envFileExportReScheduledTime":"2021-04-13","trackerId":"1001724179","orderNumber":null,"workPlanID":"100136988","preMigStatus":"SCHEDULED","neGrowStatus":"SCHEDULED","migStatus":"SCHEDULED","postMigAuditStatus":"SCHEDULED","postMigRanAtpStatus":"SCHEDULED","envExportStatus":"SCHEDULED","envFileName":null,"envFilePath":null,"workFlowManagementEntity":null,"ciqFilePath":"/home/bala/Samsung/SMART/Customer/34/PreMigration/Input/CIQ/"}],"sessionId":"893001f6","serviceToken":"93565","status":"SUCCESS"};
              // let jsonStatue = {"pageCount":1,"ovScheduledDetails":[{"id":14,"customerDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-04-23T07:55:01.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"ciqName":"","neId":"134742","fetchDate":"2021-04-30T09:05:25.000+0000","premigrationScheduledTime":"2021-04-10","migrationScheduledTime":"2021-04-15","neGrowScheduledTime":"2021-04-12","postmigrationAuditScheduledTime":"2021-04-15","ranAtpScheduledTime":"2021-04-15","envFileExportScheduledTime":"2021-04-11","premigrationReScheduledTime":"2021-04-13","migrationReScheduledTime":null,"neGrowReScheduledTime":"2021-04-13","postmigrationAuditReScheduledTime":null,"ranAtpReScheduledTime":null,"envFileExportReScheduledTime":"2021-04-13","trackerId":"1001724179","orderNumber":null,"workPlanID":"100136988","preMigStatus":"SCHEDULED","neGrowStatus":"SCHEDULED","migStatus":"SCHEDULED","postMigAuditStatus":"SCHEDULED","postMigRanAtpStatus":"SCHEDULED","envExportStatus":"SCHEDULED","envFileName":null,"envFilePath":null,"workFlowManagementEntity":null,"ciqFilePath":""},{"id":15,"customerDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-04-23T07:55:01.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"ciqName":"","neId":"140149","fetchDate":"2021-04-30T09:05:25.000+0000","premigrationScheduledTime":"2021-04-10","migrationScheduledTime":"2021-04-15","neGrowScheduledTime":"2021-04-12","postmigrationAuditScheduledTime":"2021-04-15","ranAtpScheduledTime":"2021-04-15","envFileExportScheduledTime":"2021-04-11","premigrationReScheduledTime":"2021-04-13","migrationReScheduledTime":null,"neGrowReScheduledTime":"2021-04-13","postmigrationAuditReScheduledTime":null,"ranAtpReScheduledTime":null,"envFileExportReScheduledTime":"2021-04-13","trackerId":"1001724136","orderNumber":null,"workPlanID":"100136988","preMigStatus":"SCHEDULED","neGrowStatus":"SCHEDULED","migStatus":"SCHEDULED","postMigAuditStatus":"SCHEDULED","postMigRanAtpStatus":"SCHEDULED","envExportStatus":"SCHEDULED","envFileName":null,"envFilePath":null,"workFlowManagementEntity":null,"ciqFilePath":""},{"id":16,"customerDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-04-23T07:55:01.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"ciqName":"","neId":"139650","fetchDate":"2021-04-30T09:05:25.000+0000","premigrationScheduledTime":"2021-04-10","migrationScheduledTime":"2021-04-15","neGrowScheduledTime":"2021-04-12","postmigrationAuditScheduledTime":"2021-04-15","ranAtpScheduledTime":"2021-04-15","envFileExportScheduledTime":"2021-04-11","premigrationReScheduledTime":"2021-04-13","migrationReScheduledTime":null,"neGrowReScheduledTime":"2021-04-13","postmigrationAuditReScheduledTime":null,"ranAtpReScheduledTime":null,"envFileExportReScheduledTime":"2021-04-13","trackerId":"1001724136","orderNumber":null,"workPlanID":"100136988","preMigStatus":"SCHEDULED","neGrowStatus":"SCHEDULED","migStatus":"SCHEDULED","postMigAuditStatus":"SCHEDULED","postMigRanAtpStatus":"SCHEDULED","envExportStatus":"SCHEDULED","envFileName":null,"envFilePath":null,"workFlowManagementEntity":null,"ciqFilePath":""},{"id":17,"customerDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-04-23T07:55:01.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"ciqName":"","neId":"140159","fetchDate":"2021-04-30T09:05:25.000+0000","premigrationScheduledTime":"2021-04-10","migrationScheduledTime":"2021-04-15","neGrowScheduledTime":"2021-04-12","postmigrationAuditScheduledTime":"2021-04-15","ranAtpScheduledTime":"2021-04-15","envFileExportScheduledTime":"2021-04-11","premigrationReScheduledTime":"2021-04-13","migrationReScheduledTime":null,"neGrowReScheduledTime":"2021-04-13","postmigrationAuditReScheduledTime":null,"ranAtpReScheduledTime":null,"envFileExportReScheduledTime":"2021-04-13","trackerId":"1001724137","orderNumber":null,"workPlanID":"100136988","preMigStatus":"SCHEDULED","neGrowStatus":"SCHEDULED","migStatus":"SCHEDULED","postMigAuditStatus":"SCHEDULED","postMigRanAtpStatus":"SCHEDULED","envExportStatus":"SCHEDULED","envFileName":null,"envFilePath":null,"workFlowManagementEntity":null,"ciqFilePath":""},{"id":18,"customerDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-04-23T07:55:01.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"ciqName":"","neId":"101381","fetchDate":"2021-04-30T09:05:25.000+0000","premigrationScheduledTime":"2021-04-10","migrationScheduledTime":"2021-04-15","neGrowScheduledTime":"2021-04-12","postmigrationAuditScheduledTime":"2021-04-15","ranAtpScheduledTime":"2021-04-15","envFileExportScheduledTime":"2021-04-11","premigrationReScheduledTime":"2021-04-13","migrationReScheduledTime":null,"neGrowReScheduledTime":"2021-04-13","postmigrationAuditReScheduledTime":null,"ranAtpReScheduledTime":null,"envFileExportReScheduledTime":"2021-04-13","trackerId":"1001724137","orderNumber":null,"workPlanID":"100136988","preMigStatus":"SCHEDULED","neGrowStatus":"SCHEDULED","migStatus":"SCHEDULED","postMigAuditStatus":"SCHEDULED","postMigRanAtpStatus":"SCHEDULED","envExportStatus":"SCHEDULED","envFileName":null,"envFilePath":null,"workFlowManagementEntity":null,"ciqFilePath":""},{"id":13,"customerDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-04-23T07:55:01.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"ciqName":"","neId":"139647","fetchDate":"2021-04-30T09:05:22.000+0000","premigrationScheduledTime":"2021-04-10","migrationScheduledTime":"2021-04-15","neGrowScheduledTime":"2021-04-12","postmigrationAuditScheduledTime":"2021-04-15","ranAtpScheduledTime":"2021-04-15","envFileExportScheduledTime":"2021-04-11","premigrationReScheduledTime":"2021-04-13","migrationReScheduledTime":null,"neGrowReScheduledTime":"2021-04-13","postmigrationAuditReScheduledTime":null,"ranAtpReScheduledTime":null,"envFileExportReScheduledTime":"2021-04-13","trackerId":"1001724179","orderNumber":null,"workPlanID":"100136988","preMigStatus":"SCHEDULED","neGrowStatus":"SCHEDULED","migStatus":"SCHEDULED","postMigAuditStatus":"SCHEDULED","postMigRanAtpStatus":"SCHEDULED","envExportStatus":"SCHEDULED","envFileName":null,"envFilePath":null,"workFlowManagementEntity":null,"ciqFilePath":""}],"sessionId":"be310d60","serviceToken":"61569","status":"SUCCESS","reason":""};
              let jsonStatue = {"reason":null,"pageCount":1,"ovScheduledDetails":[{"id":5,"customerDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-12T20:02:48.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neId":"139647","ciqName":"4G_CIQ_2021_08_19_2021_08_24_02.xlsx","ciqUpdateJson":null,"ciqGenerationDate":"2021-08-19 14:17","fetchDate":"2021-08-19 14:17","premigrationScheduledTime":"2021-08-18","neGrowScheduledTime":"2021-08-20","migrationScheduledTime":"2021-08-23","postmigrationAuditScheduledTime":"OFF","ranAtpScheduledTime":"OFF","envFileExportScheduledTime":null,"premigrationReScheduledTime":"2021-08-22","migrationReScheduledTime":null,"neGrowReScheduledTime":"2021-08-22","postmigrationAuditReScheduledTime":null,"ranAtpReScheduledTime":null,"envFileExportReScheduledTime":null,"trackerId":"1001724602","orderNumber":"6500","workPlanID":"100137572","preMigStatus":"SCHEDULED","neGrowStatus":"SCHEDULED","siteName":"WATSONTOWN","envExportStatus":null,"envStatus":"SCHEDULED","envGenerationDate":null,"envFileName":null,"envFilePath":null,"envStatusJson":null,"envUploadJson":null,"ciqFilePath":"Customer/34/PreMigration/Input/4G_CIQ_2021_08_19_2021_08_24_02/CIQ/","preMigGrowJson":null,"preMigGrowStatus":"SCHEDULED","preMigGrowGenerationDate":null,"fetchRemarks":"COMPLETED","fetchDetailsJson":"REQUEST\nhttps://localhost:8040/api/v3/trackor_types/project/trackors?view=L:SRCT&filter=L:SRCT\nRESPONSE\n{\"P_ENODEB_ID_FZ\":null,\"TRACKOR_ID\":1001724602,\"P_EXISTING_TECHNOLOGIES\":\"4G\",\"P_ENB_ID\":null,\"WorkPackage.WPK_SPMS_WORK_PACKAGE_ID\":\"AC-DE-003\",\"TRACKOR_CLASS_ID\":\"Verizon SNAP\",\"Candidate.C_GLOBAL_ENODEB_ID_FZ\":null,\"P_TOWER_MOD\":null,\"Market.M_REGION\":\"Philadelphia Tri-State\",\"P_LAA_FINAL\":\"0\",\"P_PROJECT_TECHNOLOGIES\":\"4G\",\"P_ENODEB_ID_ST\":null,\"P_PROJECT_SITE_TYPE\":\"Macro\",\"P_MIGRATION_STRATEGY\":null,\"WorkPackage.WPK_WORK_PACKAGE_DESCRIPTION\":\"Macro SNAP - 1st Touch\",\"P_FUZE_PROJECT_ID_FZ\":\"16326413\",\"P_PROJECT_STATUS\":\"Active\",\"P_CBRS_FINAL\":\"0\",\"P_5G_MMW_FINAL\":\"1\",\"P_PROJECT_TYPE\":\"Turnkey Swap - Macro\",\"P_TECHNOLOGY_TYPE\":\"4G\",\"TRACKOR_KEY\":\"S000736-A-001\",\"Candidate.C_CANDIDATE_NAME\":\"WATSONTOWN\",\"P_5G_CBAND_FINAL\":\"0\",\"P_LS3_CIQ_VALIDATED_F\":null,\"P_SPMS_SITE_ID\":\"453335\",\"P_5G_DSS_FINAL\":\"0\",\"Candidate.C_GLOBAL_ENODEB_ID\":null,\"P_ANCHORS_DSS\":\"Unknown\",\"P_SAMSUNG_DUX\":\"13302020095\",\"P_ANCHORS_UWB\":\"Unknown\",\"P_LS3_FSU_CIQ_COMPLETED_F\":null,\"P_VDU_ID\":null,\"P_LTE_CARRIER_ADD_SCOPE\":\"0\",\"Market.TRACKOR_KEY\":\"Central Pennsylvania\",\"P_4G_LTE_FINAL\":\"1\",\"P_RELATED_ENB_IDS\":\"S000736-A-E001 (139647)\"}\nREQUEST\nhttps://localhost:8040/api/v3/wps?trackor_id=1001724602&page=1\nRESPONSE\n{\"template_name\":\"Project Workplan\",\"proj_finish_date\":\"2022-01-17\",\"name\":\"Project Workplan\",\"active\":\"true\",\"template_id\":\"100128819\",\"id\":\"100137572\",\"proj_start_date\":\"2020-12-22\",\"trackor_id\":\"1001725232\"}\nREQUEST\nhttps://localhost:8040/api/v3/wps/100137572/tasks\nRESPONSE\n{\"projected_start_date\":\"2021-08-11\",\"predecessors\":\"7000-2\",\"early_start_date\":\"2021-08-12\",\"early_finish_date\":\"2021-08-13\",\"actual_start_date\":null,\"late_start_date\":\"2021-12-03\",\"order_number\":6500,\"successors\":null,\"wbs\":null,\"discipline\":\"SD\",\"promised_start_date\":null,\"workplan_id\":100137572,\"actual_finish_date\":null,\"required\":false,\"duration\":1,\"dynamic_dates\":[{\"label\":\"DependentTask\",\"date_type_id\":1002132,\"start_date\":null,\"finish_date\":null},{\"label\":\"Month 1\",\"date_type_id\":1002128,\"start_date\":\"2021-08-02\",\"finish_date\":\"2021-08-03\"},{\"label\":\"Month 2\",\"date_type_id\":1002129,\"start_date\":\"2021-08-02\",\"finish_date\":\"2021-08-03\"},{\"label\":\"Month 3\",\"date_type_id\":1002130,\"start_date\":\"2021-08-02\",\"finish_date\":\"2021-08-03\"},{\"label\":\"Month 4\",\"date_type_id\":1002131,\"start_date\":\"2021-04-21\",\"finish_date\":\"2021-05-15\"},{\"label\":\"Week 1\",\"date_type_id\":1002124,\"start_date\":\"2021-08-02\",\"finish_date\":\"2021-08-03\"},{\"label\":\"Week 2\",\"date_type_id\":1002125,\"start_date\":\"2021-08-02\",\"finish_date\":\"2021-08-03\"},{\"label\":\"Week 3\",\"date_type_id\":1002126,\"start_date\":\"2021-08-02\",\"finish_date\":\"2021-08-03\"},{\"label\":\"Week 4\",\"date_type_id\":1002127,\"start_date\":\"2021-08-02\",\"finish_date\":\"2021-08-03\"}],\"promised_finish_date\":null,\"projected_finish_date\":\"2021-08-23\",\"id\":10012041161,\"late_finish_date\":\"2021-12-06\",\"block_calc\":false,\"comments\":null,\"task_window\":1,\"percent_complete\":null,\"na\":false,\"baseline_finish_date\":\"2021-08-13\",\"milestone\":false,\"baseline_start_date\":\"2021-08-12\"}\n\nRFDB Status Json\n{\"projected_start_date\":\"2021-07-26\",\"predecessors\":\"5300,7000-7\",\"early_start_date\":\"2021-08-26\",\"early_finish_date\":\"2021-09-09\",\"actual_start_date\":\"2021-07-26\",\"late_start_date\":\"2021-12-06\",\"order_number\":5510,\"successors\":\"5520FS,5550FS,5600FS\",\"wbs\":null,\"discipline\":\"SD\",\"promised_start_date\":null,\"workplan_id\":100137572,\"actual_finish_date\":\"2021-07-26\",\"required\":false,\"duration\":1,\"dynamic_dates\":[{\"label\":\"DependentTask\",\"date_type_id\":1002132,\"start_date\":null,\"finish_date\":null},{\"label\":\"Month 1\",\"date_type_id\":1002128,\"start_date\":\"2021-07-26\",\"finish_date\":\"2021-07-27\"},{\"label\":\"Month 2\",\"date_type_id\":1002129,\"start_date\":\"2021-07-26\",\"finish_date\":\"2021-07-27\"},{\"label\":\"Month 3\",\"date_type_id\":1002130,\"start_date\":\"2021-07-26\",\"finish_date\":\"2021-07-27\"},{\"label\":\"Month 4\",\"date_type_id\":1002131,\"start_date\":\"2022-01-03\",\"finish_date\":\"2022-01-17\"},{\"label\":\"Week 1\",\"date_type_id\":1002124,\"start_date\":\"2021-07-26\",\"finish_date\":\"2021-07-27\"},{\"label\":\"Week 2\",\"date_type_id\":1002125,\"start_date\":\"2021-07-26\",\"finish_date\":\"2021-07-27\"},{\"label\":\"Week 3\",\"date_type_id\":1002126,\"start_date\":\"2021-07-26\",\"finish_date\":\"2021-07-27\"},{\"label\":\"Week 4\",\"date_type_id\":1002127,\"start_date\":\"2021-07-26\",\"finish_date\":\"2021-07-27\"}],\"promised_finish_date\":null,\"projected_finish_date\":\"2021-07-27\",\"id\":10012041160,\"late_finish_date\":\"2021-12-20\",\"block_calc\":false,\"comments\":null,\"task_window\":1,\"percent_complete\":null,\"na\":false,\"baseline_finish_date\":\"2021-09-09\",\"milestone\":false,\"baseline_start_date\":\"2021-08-26\"}\n\nRFSCRIPTS Status Json\n{\"projected_start_date\":\"2021-04-01\",\"predecessors\":\"5510\",\"early_start_date\":\"2021-09-09\",\"early_finish_date\":\"2021-09-10\",\"actual_start_date\":\"2021-04-01\",\"late_start_date\":\"2022-01-12\",\"order_number\":5520,\"successors\":\"5600FS\",\"wbs\":null,\"discipline\":\"SD\",\"promised_start_date\":null,\"workplan_id\":100137572,\"actual_finish_date\":\"2021-04-02\",\"required\":false,\"duration\":1,\"dynamic_dates\":[{\"label\":\"DependentTask\",\"date_type_id\":1002132,\"start_date\":null,\"finish_date\":null},{\"label\":\"Month 1\",\"date_type_id\":1002128,\"start_date\":\"2021-04-01\",\"finish_date\":\"2021-04-02\"},{\"label\":\"Month 2\",\"date_type_id\":1002129,\"start_date\":\"2021-04-01\",\"finish_date\":\"2021-04-02\"},{\"label\":\"Month 3\",\"date_type_id\":1002130,\"start_date\":\"2021-04-01\",\"finish_date\":\"2021-04-02\"},{\"label\":\"Month 4\",\"date_type_id\":1002131,\"start_date\":\"2022-01-17\",\"finish_date\":\"2022-01-18\"},{\"label\":\"Week 1\",\"date_type_id\":1002124,\"start_date\":\"2021-04-01\",\"finish_date\":\"2021-04-02\"},{\"label\":\"Week 2\",\"date_type_id\":1002125,\"start_date\":\"2021-04-01\",\"finish_date\":\"2021-04-02\"},{\"label\":\"Week 3\",\"date_type_id\":1002126,\"start_date\":\"2021-04-01\",\"finish_date\":\"2021-04-02\"},{\"label\":\"Week 4\",\"date_type_id\":1002127,\"start_date\":\"2021-04-01\",\"finish_date\":\"2021-04-02\"}],\"promised_finish_date\":null,\"projected_finish_date\":\"2021-04-02\",\"id\":10012041179,\"late_finish_date\":\"2022-01-13\",\"block_calc\":false,\"comments\":null,\"task_window\":1,\"percent_complete\":null,\"na\":false,\"baseline_finish_date\":\"2021-09-10\",\"milestone\":false,\"baseline_start_date\":\"2021-09-09\"}\n\nCIQ Status Json\n{\"projected_start_date\":\"2021-04-01\",\"predecessors\":\"5510\",\"early_start_date\":\"2021-09-09\",\"early_finish_date\":\"2021-09-10\",\"actual_start_date\":\"2021-04-01\",\"late_start_date\":\"2021-12-24\",\"order_number\":5550,\"successors\":\"5560FS,5600FS\",\"wbs\":null,\"discipline\":\"SD\",\"promised_start_date\":null,\"workplan_id\":100137572,\"actual_finish_date\":\"2021-04-02\",\"required\":false,\"duration\":1,\"dynamic_dates\":[{\"label\":\"DependentTask\",\"date_type_id\":1002132,\"start_date\":null,\"finish_date\":null},{\"label\":\"Month 1\",\"date_type_id\":1002128,\"start_date\":\"2021-04-01\",\"finish_date\":\"2021-04-02\"},{\"label\":\"Month 2\",\"date_type_id\":1002129,\"start_date\":\"2021-04-01\",\"finish_date\":\"2021-04-02\"},{\"label\":\"Month 3\",\"date_type_id\":1002130,\"start_date\":\"2021-04-01\",\"finish_date\":\"2021-04-02\"},{\"label\":\"Month 4\",\"date_type_id\":1002131,\"start_date\":\"2022-01-17\",\"finish_date\":\"2022-01-18\"},{\"label\":\"Week 1\",\"date_type_id\":1002124,\"start_date\":\"2021-04-01\",\"finish_date\":\"2021-04-02\"},{\"label\":\"Week 2\",\"date_type_id\":1002125,\"start_date\":\"2021-04-01\",\"finish_date\":\"2021-04-02\"},{\"label\":\"Week 3\",\"date_type_id\":1002126,\"start_date\":\"2021-04-01\",\"finish_date\":\"2021-04-02\"},{\"label\":\"Week 4\",\"date_type_id\":1002127,\"start_date\":\"2021-04-01\",\"finish_date\":\"2021-04-02\"}],\"promised_finish_date\":null,\"projected_finish_date\":\"2021-04-02\",\"id\":10012041265,\"late_finish_date\":\"2021-12-27\",\"block_calc\":false,\"comments\":null,\"task_window\":1,\"percent_complete\":null,\"na\":false,\"baseline_finish_date\":\"2021-09-10\",\"milestone\":false,\"baseline_start_date\":\"2021-09-09\"}\n","preErrorFile":null,"negrowErrorFile":null,"migErrorFile":null,"wfmid":null,"postErrorFile":null,"migStatus":"SCHEDULED","postMigAuditStatus":"NOT SCHEDULED","postMigRanAtPStatus":"NOT SCHEDULED"},{"id":6,"customerDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-12T20:02:48.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neId":"134742","ciqName":null,"ciqUpdateJson":null,"ciqGenerationDate":null,"fetchDate":"2021-08-19 14:17","premigrationScheduledTime":"2021-08-18","neGrowScheduledTime":"2021-08-20","migrationScheduledTime":"2021-08-23","postmigrationAuditScheduledTime":"OFF","ranAtpScheduledTime":"OFF","envFileExportScheduledTime":null,"premigrationReScheduledTime":"2021-08-22","migrationReScheduledTime":null,"neGrowReScheduledTime":"2021-08-22","postmigrationAuditReScheduledTime":null,"ranAtpReScheduledTime":null,"envFileExportReScheduledTime":null,"trackerId":"1001724626","orderNumber":null,"workPlanID":"100137572","preMigStatus":"CANCELED","neGrowStatus":"CANCELED","siteName":"Catawba Island II","envExportStatus":null,"envStatus":"CANCELED","envGenerationDate":null,"envFileName":null,"envFilePath":null,"envStatusJson":null,"envUploadJson":null,"ciqFilePath":null,"preMigGrowJson":null,"preMigGrowStatus":"CANCELED","preMigGrowGenerationDate":null,"fetchRemarks":"Failed, Data not present in CIQ or RF DB","fetchDetailsJson":null,"preErrorFile":null,"negrowErrorFile":null,"migErrorFile":null,"wfmid":null,"postErrorFile":null,"migStatus":"CANCELED","postMigAuditStatus":"CANCELED","postMigRanAtPStatus":"CANCELED"}],"sessionId":"29f56e5e","serviceToken":"99165","status":"SUCCESS"};
              this.tableData = jsonStatue;
              if (jsonStatue.status == "SUCCESS") {

                this.showLoader = false;
                this.totalPages = jsonStatue.pageCount;
               let pageCount = [];
               for (var i = 1; i <= jsonStatue.pageCount; i++) {
                 pageCount.push(i);
               }
               this.pageRenge = pageCount;
               this.pager = this.getPager(this.totalPages * this.pageSize, this.currentPage, this.pageRenge);

               if (jsonStatue.ovScheduledDetails.length == 0) {
                 this.tableShowwHide = false;
                 this.noDataVisibility = true;
                 //this.filterShowHide = false;
               } else {
                 this.ovData = jsonStatue.ovScheduledDetails;
                 this.tableShowwHide = true;
                 this.noDataVisibility = false;
                // this.filterShowHide = true;

                // Refresh the OV table on every STD_REFRESH_INTERVAL
                if(!this.ovInteractionRefreshInterval) {
                  this.ovInteractionRefreshInterval = setInterval( () => {
                      this.updateOVTable();
                  }, this.STD_REFRESH_INTERVAL );
                }

                setTimeout(() => {
                  let tableWidth = document.getElementById('ovScheduledDetails').scrollWidth;
                  $(".scrollBody table").css("min-width", (tableWidth) + "px");
                  $(".scrollHead table").css("width", tableWidth + "px");


                  $(".scrollBody").on("scroll", function (event) {
                    $(".formEditRow #formWrapper").css("margin-left", (event.target.scrollLeft - 5) + "px");
                    $(".form-control-fixed").css("right", (event.target.scrollLeft * -1) + "px");
                    $(".scrollHead table").css("margin-left", (event.target.scrollLeft * -1) + "px");
                  });
                  $(".scrollBody").css("max-height", (this.tableDataHeight - 10) + "px");

                }, 100);
               }

             } else {
               this.showLoader = false;
               this.displayModel(jsonStatue.reason, "failureIcon");
             }
          }, 1000); */

          //Please Comment while checkIn
        });
  }

  updateOVTable() {
    this.tableShowwHide = true;
    $("#dataWrapper").find(".scrollBody").scrollLeft(0);
    this.reportsService.getOvDetails(this.searchStatus, this.sharedService.createServiceToken(), this.paginationDetails, this.selectedProgramName, this.searchCriteria)
      .subscribe(
        data => {
          setTimeout(() => {
            let jsonStatue = data.json();
            this.tableData = jsonStatue;
            if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
              if (!this.sessionExpiredModalBlock) {
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
                  this.totalPages = jsonStatue.pageCount;
                  let pageCount = [];
                  for (var i = 1; i <= jsonStatue.pageCount; i++) {
                    pageCount.push(i);
                  }
                  this.pageRenge = pageCount;
                  this.pager = this.getPager(this.totalPages * this.pageSize, this.currentPage, this.pageRenge);

                  if (jsonStatue.ovScheduledDetails.length == 0) {
                    this.tableShowwHide = false;
                    this.noDataVisibility = true;
                  } else {
                    this.ovData = jsonStatue.ovScheduledDetails;
                    this.tableShowwHide = true;
                    this.noDataVisibility = false;

                    setTimeout(() => {
                      let tableWidth = document.getElementById('ovScheduledDetails').scrollWidth;
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
              else {

                this.showLoader = false;
                this.displayModel(jsonStatue.reason, "failureIcon");

              }
            }

          }, 1000);
        },
        error => {
          //Please Comment while checkIn
          /* setTimeout(() => {

            this.showLoader = false;

            let jsonStatue = {"pageCount":1,"ovScheduledDetails":[{"id":1,"customerDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-05-03T07:21:24.000+0000","createdBy":"admin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neId":"139647","ciqName":"4G_CIQ_2021_05_25_2021_05_30_0.xlsx","ciqUpdateJson":"Request:{\"id\":\"1001724179\",\"actual_finish_date\":\"2021-05-20\"}\nResponse:{\"tracker_id\":\"1001724179\",\"workplan_name\":\"Project Workplan\",\"order_number\":\"5560\"}","ciqGenerationDate":"2021-05-20 18:39","fetchDate":"2021-05-25 07:00","premigrationScheduledTime":"2021-05-20","neGrowScheduledTime":"2021-05-23","migrationScheduledTime":"2021-05-25","postmigrationAuditScheduledTime":"OFF","ranAtpScheduledTime":"OFF","envFileExportScheduledTime":null,"premigrationReScheduledTime":null,"migrationReScheduledTime":null,"neGrowReScheduledTime":null,"postmigrationAuditReScheduledTime":null,"ranAtpReScheduledTime":null,"envFileExportReScheduledTime":null,"trackerId":"1001724179","orderNumber":"6500","workPlanID":"100136988","preMigStatus":"Completed","neGrowStatus":"SCHEDULED","siteName":null,"envExportStatus":"Uploaded to OV and OV ACK","envStatus":"Completed","envGenerationDate":"2021-05-20 18:39","envFileName":"ENV_TX_ARLINGTONMSC_001_05202021.txt","envFilePath":"Customer/34/PreMigration/Output/4G_CIQ_2021_05_20_2021_05_25_01/139647/ENV/","envStatusJson":"Request:{\"id\":\"1001724179\",\"actual_finish_date\":\"2021-05-20\"}\nResponse:{\"tracker_id\":\"1001724179\",\"workplan_name\":\"Project Workplan\",\"order_number\":\"5575\"}","envUploadJson":"Request:https://localhost:8040/api/v3/trackor/1001724179/file/P_ENV_FILE?file_name=ENV_TX_ARLINGTONMSC_001_05202021.txt\nResponse:200","ciqFilePath":"Customer/34/PreMigration/Input/4G_CIQ_2021_05_25_2021_05_30_0/CIQ/","preMigGrowJson":"Request:{\"id\":\"1001724179\",\"actual_finish_date\":\"2021-05-20\"}\nResponse:{\"tracker_id\":\"1001724179\",\"workplan_name\":\"Project Workplan\",\"order_number\":\"5570\"}","preMigGrowStatus":"Completed","preMigGrowGenerationDate":"2021-05-20 18:39","fetchRemarks":"COMPLETED","migStatus":"SCHEDULED","postMigAuditStatus":"NOT SCHEDULED","postMigRanAtPStatus":"NOT SCHEDULED"},{"id":2,"customerDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-05-03T07:21:24.000+0000","createdBy":"admin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neId":"139650","ciqName":"4G_CIQ_2021_05_25_2021_05_30_0.xlsx","ciqUpdateJson":"Request:{\"id\":\"1001724190\",\"actual_finish_date\":\"2021-05-20\"}\nResponse:{\"tracker_id\":\"1001724190\",\"workplan_name\":\"Project Workplan\",\"order_number\":\"5560\"}","ciqGenerationDate":"2021-05-20 18:39","fetchDate":"2021-05-25 07:00","premigrationScheduledTime":"2021-05-20","neGrowScheduledTime":"2021-05-23","migrationScheduledTime":"2021-05-25","postmigrationAuditScheduledTime":"OFF","ranAtpScheduledTime":"OFF","envFileExportScheduledTime":null,"premigrationReScheduledTime":null,"migrationReScheduledTime":null,"neGrowReScheduledTime":null,"postmigrationAuditReScheduledTime":null,"ranAtpReScheduledTime":null,"envFileExportReScheduledTime":null,"trackerId":"1001724190","orderNumber":"6500","workPlanID":"100136988","preMigStatus":"Completed","neGrowStatus":"SCHEDULED","siteName":null,"envExportStatus":"Uploaded to OV and OV ACK","envStatus":"Completed","envGenerationDate":"2021-05-20 18:39","envFileName":"ENV_TX_ARLINGTONMSC_004_05202021.txt","envFilePath":"Customer/34/PreMigration/Output/4G_CIQ_2021_05_20_2021_05_25_01/139650/ENV/","envStatusJson":"Request:{\"id\":\"1001724190\",\"actual_finish_date\":\"2021-05-20\"}\nResponse:{\"tracker_id\":\"1001724190\",\"workplan_name\":\"Project Workplan\",\"order_number\":\"5575\"}","envUploadJson":"Request:https://localhost:8040/api/v3/trackor/1001724190/file/P_ENV_FILE?file_name=ENV_TX_ARLINGTONMSC_004_05202021.txt\nResponse:200","ciqFilePath":"Customer/34/PreMigration/Input/4G_CIQ_2021_05_25_2021_05_30_0/CIQ/","preMigGrowJson":"Request:{\"id\":\"1001724190\",\"actual_finish_date\":\"2021-05-20\"}\nResponse:{\"tracker_id\":\"1001724190\",\"workplan_name\":\"Project Workplan\",\"order_number\":\"5570\"}","preMigGrowStatus":"Completed","preMigGrowGenerationDate":"2021-05-20 18:39","fetchRemarks":"COMPLETED","migStatus":"SCHEDULED","postMigAuditStatus":"NOT SCHEDULED","postMigRanAtPStatus":"NOT SCHEDULED"},{"id":3,"customerDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-05-03T07:21:24.000+0000","createdBy":"admin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neId":"134742","ciqName":"4G_CIQ_2021_05_25_2021_05_30_0.xlsx","ciqUpdateJson":"Request:{\"id\":\"1001724150\",\"actual_finish_date\":\"2021-05-20\"}\nResponse:{\"tracker_id\":\"1001724150\",\"workplan_name\":\"Project Workplan\",\"order_number\":\"5560\"}","ciqGenerationDate":"2021-05-20 18:39","fetchDate":"2021-05-25 07:00","premigrationScheduledTime":"2021-05-20","neGrowScheduledTime":"2021-05-23","migrationScheduledTime":"2021-05-25","postmigrationAuditScheduledTime":"OFF","ranAtpScheduledTime":"OFF","envFileExportScheduledTime":null,"premigrationReScheduledTime":null,"migrationReScheduledTime":null,"neGrowReScheduledTime":null,"postmigrationAuditReScheduledTime":null,"ranAtpReScheduledTime":null,"envFileExportReScheduledTime":null,"trackerId":"1001724150","orderNumber":"6500","workPlanID":"100136988","preMigStatus":"Completed","neGrowStatus":"Success","siteName":null,"envExportStatus":"Uploaded to OV and OV ACK","envStatus":"Completed","envGenerationDate":"2021-05-20 18:39","envFileName":"ENV_TX_ARLINGTONMSC_002_05202021.txt","envFilePath":"Customer/34/PreMigration/Output/4G_CIQ_2021_05_20_2021_05_25_01/134742/ENV/","envStatusJson":"Request:{\"id\":\"1001724150\",\"actual_finish_date\":\"2021-05-20\"}\nResponse:{\"tracker_id\":\"1001724150\",\"workplan_name\":\"Project Workplan\",\"order_number\":\"5575\"}","envUploadJson":"Request:https://localhost:8040/api/v3/trackor/1001724150/file/P_ENV_FILE?file_name=ENV_TX_ARLINGTONMSC_002_05202021.txt\nResponse:200","ciqFilePath":"Customer/34/PreMigration/Input/4G_CIQ_2021_05_25_2021_05_30_0/CIQ/","preMigGrowJson":"Request:{\"id\":\"1001724150\",\"actual_finish_date\":\"2021-05-20\"}\nResponse:{\"tracker_id\":\"1001724150\",\"workplan_name\":\"Project Workplan\",\"order_number\":\"5570\"}","preMigGrowStatus":"Completed","preMigGrowGenerationDate":"2021-05-20 18:39","fetchRemarks":"COMPLETED","migStatus":"SCHEDULED","postMigAuditStatus":"NOT SCHEDULED","postMigRanAtPStatus":"NOT SCHEDULED"},{"id":4,"customerDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-05-03T07:21:24.000+0000","createdBy":"admin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neId":"140149","ciqName":"4G_CIQ_2021_05_25_2021_05_30_0.xlsx","ciqUpdateJson":"Request:{\"id\":\"1001724140\",\"actual_finish_date\":\"2021-05-20\"}\nResponse:{\"tracker_id\":\"1001724140\",\"workplan_name\":\"Project Workplan\",\"order_number\":\"5560\"}","ciqGenerationDate":"2021-05-20 18:39","fetchDate":"2021-05-25 07:00","premigrationScheduledTime":"2021-05-20","neGrowScheduledTime":"2021-05-23","migrationScheduledTime":"2021-05-25","postmigrationAuditScheduledTime":"OFF","ranAtpScheduledTime":"OFF","envFileExportScheduledTime":null,"premigrationReScheduledTime":null,"migrationReScheduledTime":null,"neGrowReScheduledTime":null,"postmigrationAuditReScheduledTime":null,"ranAtpReScheduledTime":null,"envFileExportReScheduledTime":null,"trackerId":"1001724140","orderNumber":"6500","workPlanID":"100136988","preMigStatus":"Completed","neGrowStatus":"SCHEDULED","siteName":null,"envExportStatus":"Uploaded to OV and OV ACK","envStatus":"Completed","envGenerationDate":"2021-05-20 18:39","envFileName":"ENV_TX_ARLINGTONMSC_003_05202021.txt","envFilePath":"Customer/34/PreMigration/Output/4G_CIQ_2021_05_20_2021_05_25_01/140149/ENV/","envStatusJson":"Request:{\"id\":\"1001724140\",\"actual_finish_date\":\"2021-05-20\"}\nResponse:{\"tracker_id\":\"1001724140\",\"workplan_name\":\"Project Workplan\",\"order_number\":\"5575\"}","envUploadJson":"Request:https://localhost:8040/api/v3/trackor/1001724140/file/P_ENV_FILE?file_name=ENV_TX_ARLINGTONMSC_003_05202021.txt\nResponse:200","ciqFilePath":"Customer/34/PreMigration/Input/4G_CIQ_2021_05_25_2021_05_30_0/CIQ/","preMigGrowJson":"Request:{\"id\":\"1001724140\",\"actual_finish_date\":\"2021-05-20\"}\nResponse:{\"tracker_id\":\"1001724140\",\"workplan_name\":\"Project Workplan\",\"order_number\":\"5570\"}","preMigGrowStatus":"Completed","preMigGrowGenerationDate":"2021-05-20 18:39","fetchRemarks":"COMPLETED","migStatus":"SCHEDULED","postMigAuditStatus":"NOT SCHEDULED","postMigRanAtPStatus":"NOT SCHEDULED"}],"sessionId":"48104190","serviceToken":"61035","status":"SUCCESS","reason":""};
            // let jsonStatue = {"pageCount":1,"sessionId":"be310d60","serviceToken":"61569","status":"SUCCESS","ovScheduledDetails":[{"id":14,"customerDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-04-23T07:55:01.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"ciqName":"No CIQ","neId":"134742","fetchDate":"2021-04-30T09:05:25.000+0000","premigrationScheduledTime":"2021-04-10","migrationScheduledTime":"2021-04-15","neGrowScheduledTime":"2021-04-12","postmigrationAuditScheduledTime":"2021-04-15","ranAtpScheduledTime":"2021-04-15","envFileExportScheduledTime":"2021-04-11","premigrationReScheduledTime":"2021-04-13","migrationReScheduledTime":null,"neGrowReScheduledTime":"2021-04-13","postmigrationAuditReScheduledTime":null,"ranAtpReScheduledTime":null,"envFileExportReScheduledTime":"2021-04-13","trackerId":"1001724179","orderNumber":null,"workPlanID":"100136988","preMigStatus":"SCHEDULED","neGrowStatus":"SCHEDULED","migStatus":"SCHEDULED","postMigAuditStatus":"SCHEDULED","postMigRanAtpStatus":"SCHEDULED","envExportStatus":"SCHEDULED","envFileName":null,"envFilePath":null,"workFlowManagementEntity":null,"ciqFilePath":""},{"id":15,"customerDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-04-23T07:55:01.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"ciqName":"","neId":"140149","fetchDate":"2021-04-30T09:05:25.000+0000","premigrationScheduledTime":"2021-04-10","migrationScheduledTime":"2021-04-15","neGrowScheduledTime":"2021-04-12","postmigrationAuditScheduledTime":"2021-04-15","ranAtpScheduledTime":"2021-04-15","envFileExportScheduledTime":"2021-04-11","premigrationReScheduledTime":"2021-04-13","migrationReScheduledTime":null,"neGrowReScheduledTime":"2021-04-13","postmigrationAuditReScheduledTime":null,"ranAtpReScheduledTime":null,"envFileExportReScheduledTime":"2021-04-13","trackerId":"1001724136","orderNumber":null,"workPlanID":"100136988","preMigStatus":"SCHEDULED","neGrowStatus":"SCHEDULED","migStatus":"SCHEDULED","postMigAuditStatus":"SCHEDULED","postMigRanAtpStatus":"SCHEDULED","envExportStatus":"SCHEDULED","envFileName":null,"envFilePath":null,"workFlowManagementEntity":null,"ciqFilePath":""},{"id":16,"customerDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-04-23T07:55:01.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"ciqName":"","neId":"139650","fetchDate":"2021-04-30T09:05:25.000+0000","premigrationScheduledTime":"2021-04-10","migrationScheduledTime":"2021-04-15","neGrowScheduledTime":"2021-04-12","postmigrationAuditScheduledTime":"2021-04-15","ranAtpScheduledTime":"2021-04-15","envFileExportScheduledTime":"2021-04-11","premigrationReScheduledTime":"2021-04-13","migrationReScheduledTime":null,"neGrowReScheduledTime":"2021-04-13","postmigrationAuditReScheduledTime":null,"ranAtpReScheduledTime":null,"envFileExportReScheduledTime":"2021-04-13","trackerId":"1001724136","orderNumber":null,"workPlanID":"100136988","preMigStatus":"SCHEDULED","neGrowStatus":"SCHEDULED","migStatus":"SCHEDULED","postMigAuditStatus":"SCHEDULED","postMigRanAtpStatus":"SCHEDULED","envExportStatus":"SCHEDULED","envFileName":null,"envFilePath":null,"workFlowManagementEntity":null,"ciqFilePath":""}],"reason":""};
            this.tableData = jsonStatue;
            if (jsonStatue.status == "SUCCESS") {

              this.totalPages = jsonStatue.pageCount;
              let pageCount = [];
              for (var i = 1; i <= jsonStatue.pageCount; i++) {
                pageCount.push(i);
              }
              this.pageRenge = pageCount;
              this.pager = this.getPager(this.totalPages * this.pageSize, this.currentPage, this.pageRenge);

              if (jsonStatue.ovScheduledDetails.length == 0) {
                this.tableShowwHide = false;
                this.noDataVisibility = true;
                //this.filterShowHide = false;
              } else {
                this.ovData = jsonStatue.ovScheduledDetails;
                this.tableShowwHide = true;
                this.noDataVisibility = false;
                // this.filterShowHide = true;

                setTimeout(() => {
                  let tableWidth = document.getElementById('ovScheduledDetails').scrollWidth;
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
              this.displayModel(jsonStatue.reason, "failureIcon");
            }
          }, 1000);*/ 

          //Please Comment while checkIn
        });
  }

  onItemSelect(event:any){
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
  onDeSelect(event:any){
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
                "fromDate" : currentForm.querySelector("#searchForecastStartDate")?currentForm.querySelector("#searchForecastStartDate").value: null,
                "toDate" : currentForm.querySelector("#searchForecastEndDate") ? currentForm.querySelector("#searchForecastEndDate").value : null,
                //"forecastStartDate": currentForm.querySelector("#searchForecastStartDate").value,
                //"forecastEndDate": currentForm.querySelector("#searchForecastEndDate").value,
                "market": currentForm.querySelector("#searchMarket").value,
                "enbId": currentForm.querySelector("#searchEnbId").value,
                "neName": currentForm.querySelector("#searchEnbName").value
                //"status": currentForm.querySelector("#searchStatus").value,

              };
              
            if (searchCrtra.fromDate || searchCrtra.toDate || searchCrtra.market || searchCrtra.enbId || searchCrtra.neName) {
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
    this.reportsService.downloadFile(this.sharedService.createServiceToken(), this.custId, this.paginationDetails,this.selectedProgramName,this.searchDetails, this.searchStatus,this.selectedItems)
      .subscribe(
        data => {
          this.showLoader = false;
          let blob = new Blob([data["_body"]], {
            type: "application/octet-stream"
          });

          FileSaver.saveAs(blob, "OverallReportDetails.zip");

        },
        error => {
          //Please Comment while checkIn
         /*  let jsonStatue: any = { "sessionId": "506db794", "reason": "Download successful!", "status": "SUCCESS", "serviceToken": "63524" };
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

  downloadCiqFile(fileName,filepath) {
    this.showLoader = true;
    this.reportsService.downloadCiqFile(fileName,filepath,this.sharedService.createServiceToken())
      .subscribe(
        data => {
          this.showLoader = false;
          let blob = new Blob([data["_body"]], {
            type: "application/octet-stream"
          });

          FileSaver.saveAs(blob, fileName);

        },
        error => {
          //Please Comment while checkIn
         /*  let jsonStatue: any = { "sessionId": "506db794", "reason": "Download successful!", "status": "SUCCESS", "serviceToken": "63524" };
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

            /*   setTimeout(() => {
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

      // this.getOverallReportsDetails();
      if(this.searchBlock) {
        this.getOvDetails();
      }
      else if(this.auditBlock){
        this.getReports(this.auditSearchCriteria);
      }
      else {
        this.getOverallReportsDetails();
      }


    }, 0);



  };



  onChangeTableRowLength(event) {
    this.showLoader = true;
    this.pageSize = event.target.value;

    this.currentPage = 1;

    let paginationDetails = {
      "count": parseInt(this.pageSize,10),
      "page": parseInt(this.currentPage)
    };

    this.paginationDetails = paginationDetails;
    this.paginationDisabbled = false;
    // Hide all the form/Table/Nodatafound5
    this.tableShowHide = false;

    setTimeout(() => {
      this.showLoader = false;
      $("#dataWrapper").find(".scrollBody").scrollLeft(0);
      // this.getOverallReportsDetails();
      if(this.searchBlock) {
        this.getOvDetails();
      }
      else {
        this.getOverallReportsDetails();
      }
    }, 0);

  }

  onChangePagination(event) {
    this.showLoader = true;
    this.pageSize = event.target.value;

    this.currentPage = 1;

    let paginationDetails = {
      "count": parseInt(this.pageSize,10),
      "page": parseInt(this.currentPage)
    };

    this.paginationDetails = paginationDetails;
    this.paginationDisabbled = false;
    this.reportTableShowHide = false;
    this.showLoader = false;
    $("#dataWrapper").find(".scrollBody").scrollLeft(0);
    this.getReports(this.auditSearchCriteria);
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

  editRow(event, key, index) {

    //    this.cancelCreateNew(event);
  //  console.log(event,key,index)
        clearInterval(this.ovInteractionRefreshInterval);
       // this.ovInteractionRefreshInterval = null;

       console.log(this.ovInteractionRefreshInterval);

        this.preMigGrowGenerationDate = key.preMigGrowGenerationDate;
        this. preMigGrowStatus = key.preMigGrowStatus;

        this.envGenerationDate = key.envGenerationDate;
        this.envStatus = key.envStatus;

        this.envGenerationDate = key.ciqGenerationDate;
        this.fetchRemarks = key.fetchRemarks;

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
  updateEditRow(index, key, event){
    validator.performValidation(event, this.validationData, "save_update");

    setTimeout(() => {
      if(this.isValidForm(event)){
          console.log(key);
        this.showLoader = true;
        let currentEditedForm = event.target.parentNode.parentNode,
        generateInfoAuditDetails = {
                  "id": key.id,
                  "preMigGrowGenerationDate":currentEditedForm.querySelector("#preMigGrowGenerationDate").value,
                  "envGenerationDate":currentEditedForm.querySelector("#envGenerationDate").value,
                  "ciqGenerationDate":currentEditedForm.querySelector("#ciqGenerationDate").value,
                  "trackerId": key.trackerId,
                  "neId": key.neId,
                 // "filePath": key.filePath,
                  //"fileType": key.fileType,
                  //"ciqFileName": key.ciqFileName,
                  // "neName": key.neName,
                  // "generatedBy": key.generatedBy,
                  // "generationDate": key.generationDate,
                  // "searchStartDate": key.searchStartDate,
                  // "searchEndDate": key.searchEndDate,
                  // "remarks": currentEditedForm.querySelector("#remarks").value
                  //"status": currentEditedForm.querySelector("#status").value
             }; 
        this.reportsService.updateGenerateDetails(key,generateInfoAuditDetails, this.sharedService.createServiceToken())
        .subscribe(
            data => {
                let jsonStatue = data.json();
                this.showLoader = false;

                if(jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User"){
            
                  this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
            
                } else {
            
                  if( this.sharedService.isvalidServiceToken( parseInt(data.json().serviceToken,10) ) || data.json().sessionId == "408"){               
                      if(jsonStatue.status == "SUCCESS"){
                         this.message = jsonStatue.reason;
                         this.successModalBlock = this.modalService.open(this.successModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'success-modal'});

                     
                         this.cancelEditRow(key.id, '');
                       
                      } else {
                        this.displayModel(jsonStatue.reason,"failureIcon");  
                      }
                  }   
                }
                
            },  
            error => {
              //Please Comment while checkIn
          /*   let jsonStatue: any = {"reason":"Generated Audit Details Updated Successfully!","sessionId":"c49f47af","serviceToken":"61799","status":"SUCCESS"};
             setTimeout(() => {
                this.showLoader = false;
                if(jsonStatue.status == "SUCCESS"){
                 this.message = jsonStatue.reason;
                this.successModalBlock = this.modalService.open(this.successModalRef,{keyboard: false, backdrop: "static",size:'lg',windowClass:"success-modal"});
                 // this.displayModel("User details updated successfully !","successIcon");
                  //this.createForm = false;
                  this.cancelEditRow(key.id, '');
                } else {
                  this.displayModel(jsonStatue.reason,"failureIcon");  
                }
          
              }, 1000); */
              //Please Comment while checkIn
            });
        }
      }, 0);
  }
  cancelEditRow(index, identifier){

    this.ovInteractionRefreshInterval = setInterval(() => {
      this.updateOVTable();
    }, this.STD_REFRESH_INTERVAL);

    console.log("intervl"+this.ovInteractionRefreshInterval);

    $(".editRowDisabled").attr("class", "editRow");
    $(".deleteRowDisabled").attr("class","deleteRow");
    let currentEditedForm = document.querySelector(".row_id_"+identifier);

    this.editableFormArray.splice(this.editableFormArray.indexOf(index), 1);

    this.checkFormEnable(index); //TODO : need to recheck this function

    currentEditedForm.lastElementChild.lastElementChild.children[0].className = "editRow";

    // Enable search button 
    document.querySelector("#searchButton").classList.remove("buttonDisabled");
    this.paginationDisabbled = false;
        
  }

  checkFormEnable(index) {
    let indexValue = this.editableFormArray.indexOf(index);
    console.log(indexValue)
    return indexValue >= 0 ? true : false;
  } 
  
  custNameChange(selCustName) {
    this.editableEODFormArray = [];
    this.selectedItems = [];
    this.selectedProgramName = "";
    this.selProgramNameTemp = "";
    this.selprogName = "";
    this.selfetchdays = "";
    this.editIndex = -1;
    this.searchDetails = {};
    this.searchStatus = "load";
    this.searchCriteria = null;
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

  fetchDaysChange(selfetchdays){
    this.selectedfetchdays = selfetchdays;
  }
  progNameChange(selprogName, type) {
    this.editableEODFormArray = [];
    this.selectedItems = [];
    this.editIndex = -1;
    this.searchDetails = {};
    this.selfetchdays = "";
    this.searchStatus = "load";
    this.searchCriteria = null;

    this.selectedProgramName = selprogName;
    this.selProgramNameTemp = selprogName;
    this.showLoader = true;
    if (type == 'reports') {
      this.getOverallReportsDetails();
    }
    else {
      this.getOvDetails();
    }
    this.clearSearchFrom();
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
            /*   let jsonStatue: any = { "sessionId": "506db794", "reason": "Updation Failed", "status": "SUCCESS", "serviceToken": "63524" };
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

              }, 1000);*/
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
           /*    let jsonStatue: any = { "sessionId": "506db794", "reason": "Updation Failed", "status": "SUCCESS", "serviceToken": "63524" };
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

  getSelectedDays(fromDt, toDt) {
    let d1: any = new Date(fromDt);
    let d2: any = new Date(toDt);
    let diffTime: any = Math.abs(d2 - d1);
    let diffDays: any = Math.ceil(diffTime / (1000*60*60*24));
    console.log("Selected Days: " + diffDays)
    return diffDays;
  }

  getCustomerList() {
    //validator.performValidation(event, this.validationData, "save_update");
    setTimeout(() => {

      // this.sharedService.userNavigation = true; //un block user navigation
      this.programDropDownList = [];
      this.reportsService.getCustomerIdList(this.sharedService.createServiceToken())
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

                  this.progName = currentData.allProgramList;
                  this.programDropDownList = [];
                  for(let itm of this.progName) {
                    let prgmData = { item_id: itm.programName, item_text: itm.programName };
                    this.programDropDownList.push(prgmData);
                  }

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
              this.tableData = { "allProgramList": [{ "id": 27, "networkTypeDetailsEntity": { "id": 6, "networkType": "4G", "createdBy": "superadmin", "caretedDate": "2020-03-17T11:55:56.000+0000", "status": "Active", "remarks": "", "networkColor": "#ec8cab" }, "programName": "SPT-4G-CDU30-Latest", "programDescription": "CDU30-Latest", "status": "Active", "creationDate": "2019-09-30T13:47:38.000+0000", "createdBy": "superadmin", "sourceProgramId": 20, "sourceprogramName": "SPT-4G-CDU30" }, { "id": 26, "networkTypeDetailsEntity": { "id": 6, "networkType": "4G", "createdBy": "superadmin", "caretedDate": "2020-03-17T11:55:56.000+0000", "status": "Active", "remarks": "", "networkColor": "#ec8cab" }, "programName": "SPT-4G-MIMO-Latest", "programDescription": "MIMO-Latest", "status": "Active", "creationDate": "2019-09-27T14:16:53.000+0000", "createdBy": "superadmin", "sourceProgramId": 24, "sourceprogramName": "SPT-4G-MIMO-Latest1" }, { "id": 35, "networkTypeDetailsEntity": { "id": 6, "networkType": "4G", "createdBy": "superadmin", "caretedDate": "2020-03-17T11:55:56.000+0000", "status": "Active", "remarks": "", "networkColor": "#ec8cab" }, "programName": "SPT-4G-MIMO-Testing", "programDescription": "MIMO-Testing", "status": "Active", "creationDate": "2020-08-12T10:13:09.000+0000", "createdBy": "superadmin", "sourceProgramId": 26, "sourceprogramName": "SPT-4G-MIMO-Latest" }, { "id": 38, "networkTypeDetailsEntity": { "id": 6, "networkType": "4G", "createdBy": "superadmin", "caretedDate": "2020-03-17T11:55:56.000+0000", "status": "Active", "remarks": "", "networkColor": "#ec8cab" }, "programName": "VZN-4G-FSU", "programDescription": "FSU", "status": "Active", "creationDate": "2020-06-05T01:33:48.000+0000", "createdBy": "superadmin", "sourceProgramId": 34, "sourceprogramName": "VZN-4G-USM-LIVE" }, { "id": 36, "networkTypeDetailsEntity": { "id": 6, "networkType": "4G", "createdBy": "superadmin", "caretedDate": "2020-03-17T11:55:56.000+0000", "status": "Active", "remarks": "", "networkColor": "#ec8cab" }, "programName": "VZN-4G-USM-LAB", "programDescription": "LAB", "status": "Active", "creationDate": "2020-05-04T13:07:17.000+0000", "createdBy": "superadmin", "sourceProgramId": 34, "sourceprogramName": "VZN-4G-USM-LIVE" }, { "id": 30, "networkTypeDetailsEntity": { "id": 6, "networkType": "4G", "createdBy": "superadmin", "caretedDate": "2020-03-17T11:55:56.000+0000", "status": "Active", "remarks": "", "networkColor": "#ec8cab" }, "programName": "VZN-4G-USM-Latest", "programDescription": "USM-Latest", "status": "Active", "creationDate": "2019-10-10T05:55:57.000+0000", "createdBy": "superadmin", "sourceProgramId": 28, "sourceprogramName": "VZN-4G-USM-TEST" }, { "id": 34, "networkTypeDetailsEntity": { "id": 6, "networkType": "4G", "createdBy": "superadmin", "caretedDate": "2020-03-17T11:55:56.000+0000", "status": "Active", "remarks": "", "networkColor": "#ec8cab" }, "programName": "VZN-4G-USM-LIVE", "programDescription": "LIVE", "status": "Active", "creationDate": "2019-11-22T06:36:02.000+0000", "createdBy": "superadmin", "sourceProgramId": 30, "sourceprogramName": "VZN-4G-USM-Latest" }, { "id": 41, "networkTypeDetailsEntity": { "id": 7, "networkType": "5G", "createdBy": "superadmin", "caretedDate": "2019-09-04T06:47:55.000+0000", "status": "Active", "remarks": "", "networkColor": "#827fbb" }, "programName": "VZN-5G-DSS", "programDescription": "DSS", "status": "Active", "creationDate": "2020-11-08T13:46:03.000+0000", "createdBy": "superadmin", "sourceProgramId": 34, "sourceprogramName": "VZN-4G-USM-LIVE" }, { "id": 40, "networkTypeDetailsEntity": { "id": 7, "networkType": "5G", "createdBy": "superadmin", "caretedDate": "2019-09-04T06:47:55.000+0000", "status": "Active", "remarks": "", "networkColor": "#827fbb" }, "programName": "VZN-5G-MM", "programDescription": "MM", "status": "Active", "creationDate": "2020-08-27T06:50:11.000+0000", "createdBy": "superadmin", "sourceProgramId": 34, "sourceprogramName": "VZN-4G-USM-LIVE" }], "sessionId": "54d2e202", "serviceToken": "51084", "CustomerList": { "customerlist": [{ "id": 4, "customerName": "Sprint", "iconPath": "/customer/sprint_ 08282019_14_30_38_icon.png", "status": "Active", "customerShortName": "SPT", "customerDetails": [{ "id": 26, "networkTypeDetailsEntity": { "id": 6, "networkType": "4G", "createdBy": "superadmin", "caretedDate": "2020-03-17T11:55:56.000+0000", "status": "Active", "remarks": "", "networkColor": "#ec8cab" }, "programName": "SPT-4G-MIMO-Latest", "programDescription": "MIMO-Latest", "status": "Active", "creationDate": "2019-09-27T14:16:53.000+0000", "createdBy": "superadmin", "sourceProgramId": 24, "sourceprogramName": "SPT-4G-MIMO-Latest1" }, { "id": 27, "networkTypeDetailsEntity": { "id": 6, "networkType": "4G", "createdBy": "superadmin", "caretedDate": "2020-03-17T11:55:56.000+0000", "status": "Active", "remarks": "", "networkColor": "#ec8cab" }, "programName": "SPT-4G-CDU30-Latest", "programDescription": "CDU30-Latest", "status": "Active", "creationDate": "2019-09-30T13:47:38.000+0000", "createdBy": "superadmin", "sourceProgramId": 20, "sourceprogramName": "SPT-4G-CDU30" }, { "id": 35, "networkTypeDetailsEntity": { "id": 6, "networkType": "4G", "createdBy": "superadmin", "caretedDate": "2020-03-17T11:55:56.000+0000", "status": "Active", "remarks": "", "networkColor": "#ec8cab" }, "programName": "SPT-4G-MIMO-Testing", "programDescription": "MIMO-Testing", "status": "Active", "creationDate": "2020-08-12T10:13:09.000+0000", "createdBy": "superadmin", "sourceProgramId": 26, "sourceprogramName": "SPT-4G-MIMO-Latest" }] }, { "id": 2, "customerName": "Verizon", "iconPath": "/customer/verizon_ 08262019_12_41_21_icon.png", "status": "Active", "customerShortName": "VZN", "customerDetails": [{ "id": 30, "networkTypeDetailsEntity": { "id": 6, "networkType": "4G", "createdBy": "superadmin", "caretedDate": "2020-03-17T11:55:56.000+0000", "status": "Active", "remarks": "", "networkColor": "#ec8cab" }, "programName": "VZN-4G-USM-Latest", "programDescription": "USM-Latest", "status": "Active", "creationDate": "2019-10-10T05:55:57.000+0000", "createdBy": "superadmin", "sourceProgramId": 28, "sourceprogramName": "VZN-4G-USM-TEST" }, { "id": 34, "networkTypeDetailsEntity": { "id": 6, "networkType": "4G", "createdBy": "superadmin", "caretedDate": "2020-03-17T11:55:56.000+0000", "status": "Active", "remarks": "", "networkColor": "#ec8cab" }, "programName": "VZN-4G-USM-LIVE", "programDescription": "LIVE", "status": "Active", "creationDate": "2019-11-22T06:36:02.000+0000", "createdBy": "superadmin", "sourceProgramId": 30, "sourceprogramName": "VZN-4G-USM-Latest" }, { "id": 36, "networkTypeDetailsEntity": { "id": 6, "networkType": "4G", "createdBy": "superadmin", "caretedDate": "2020-03-17T11:55:56.000+0000", "status": "Active", "remarks": "", "networkColor": "#ec8cab" }, "programName": "VZN-4G-USM-LAB", "programDescription": "LAB", "status": "Active", "creationDate": "2020-05-04T13:07:17.000+0000", "createdBy": "superadmin", "sourceProgramId": 34, "sourceprogramName": "VZN-4G-USM-LIVE" }, { "id": 38, "networkTypeDetailsEntity": { "id": 6, "networkType": "4G", "createdBy": "superadmin", "caretedDate": "2020-03-17T11:55:56.000+0000", "status": "Active", "remarks": "", "networkColor": "#ec8cab" }, "programName": "VZN-4G-FSU", "programDescription": "FSU", "status": "Active", "creationDate": "2020-06-05T01:33:48.000+0000", "createdBy": "superadmin", "sourceProgramId": 34, "sourceprogramName": "VZN-4G-USM-LIVE" }, { "id": 40, "networkTypeDetailsEntity": { "id": 7, "networkType": "5G", "createdBy": "superadmin", "caretedDate": "2019-09-04T06:47:55.000+0000", "status": "Active", "remarks": "", "networkColor": "#827fbb" }, "programName": "VZN-5G-MM", "programDescription": "MM", "status": "Active", "creationDate": "2020-08-27T06:50:11.000+0000", "createdBy": "superadmin", "sourceProgramId": 34, "sourceprogramName": "VZN-4G-USM-LIVE" }, { "id": 41, "networkTypeDetailsEntity": { "id": 7, "networkType": "5G", "createdBy": "superadmin", "caretedDate": "2019-09-04T06:47:55.000+0000", "status": "Active", "remarks": "", "networkColor": "#827fbb" }, "programName": "VZN-5G-DSS", "programDescription": "DSS", "status": "Active", "creationDate": "2020-11-08T13:46:03.000+0000", "createdBy": "superadmin", "sourceProgramId": 34, "sourceprogramName": "VZN-4G-USM-LIVE" }] }] }, "status": "SUCCESS" };
              this.custList = this.tableData.CustomerList.customerlist;
              this.progName = this.tableData.allProgramList;
              this.programDropDownList = [];
              for(let itm of this.progName) {
                let prgmData = { item_id: itm.programName, item_text: itm.programName };
                this.programDropDownList.push(prgmData);
              }
              console.log(this.progName);
            }, 1000); */
            //Please Comment while checkIn
          });
    }, 0);
  }
  
  /*forcefetch(){
    this.showLoader = true;
    setTimeout(() => {

      // this.sharedService.userNavigation = true; //un block user navigation

      this.reportsService.forceFetch(this.sharedService.createServiceToken())
        .subscribe(
          data => {

            let currentData = data.json();
            this.showLoader = false;

            if (currentData.sessionId == "408" || currentData.status == "Invalid User") {

              this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
            
           } else {

              if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {
                if (currentData.status == "SUCCESS") {
                  this.message = "Fetched successfully!";
                  this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                  this.getOvDetails();
                } else {
                  this.displayModel(currentData.reason, "failureIcon");
                }
              }
            }

          },
          error => {
            //Please Comment while checkIn
          /*  setTimeout(() => {
              this.showLoader = false;
              //this.tableData = {"reason":"OV Username OR Password is Wrong!","status":"FAILED"};
              this.tableData = {"status":"SUCCESS"};
              if(this.tableData.status == "SUCCESS"){
              this.message = "Fetched successfully!";
              this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });             
               this.getOvDetails();
              }
              else{
                this.displayModel(this.tableData.reason, "failureIcon")
              }
            }, 1000); 
            //Please Comment while checkIn
          });
    }, 0);
  }*/
  runSchedule(){
    this.showLoader = true;
    setTimeout(() => {
      this.reportsService.runSchedule(this.sharedService.createServiceToken(),this.selectedProgramName,this.selectedfetchdays)
        .subscribe(
          data => {
            let currentData = data.json();
            this.showLoader = false;
            if (currentData.sessionId == "408" || currentData.status == "Invalid User") {
              this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
           } else {

              if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {
                if (currentData.status == "SUCCESS") {
                  this.message = "Executed successfully!";
                  this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                  this.getOvDetails();
                } else {
                  this.displayModel(currentData.reason, "failureIcon");
                }
              }
            }

          },
          error => {
            //Please Comment while checkIn
            /* setTimeout(() => {
              this.showLoader = false;
              //this.tableData = {"reason":"OV Username OR Password is Wrong!","status":"FAILED"};
              this.tableData = { "status": "SUCCESS" };
              if (this.tableData.status == "SUCCESS") {
                this.message = "Scheduled successfully!";
                this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                this.getOvDetails();
              }
              else {
                this.displayModel("Scheduled Failed", "failureIcon")
              }
            }, 1000); */
            //Please Comment while checkIn
          });

    }, 0);
  }
  changeSorting(predicate, event, index){
     this.sharedService.dynamicSort(predicate, event, index, this.verizonData);
  }
  changeSortingg(predicate, event, index){
    this.sharedService.dynamicSort(predicate, event, index, this.ovData);
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

  showOvInteractionJSON(modalTemplate, data,type) {
    this.envModalData = data ? data : "No Data Found!";
    this.jsonType = type;
    if(type != 'ENV FILE UPLOAD' && type!='FETCH')
    {
    let id = data.split(":");
    this.order_number = id[id.length-1].replace("\"","").replaceAll('"}',"");
    }
    else
      this.order_number = "";
    this.envInfoModalBlock = this.modalService.open(modalTemplate, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView' });
  }

  closeEnvInfoModal() {
    this.envModalData= "";
    this.envInfoModalBlock.close();
  }

  searchOVDashboard() {
    // console.log("SHAHID", this.searchFormField);
  
    let searchValuesList = this.searchFormField ? Object.values(this.searchFormField) : [];
    let valueFound = false;
    for(let item in searchValuesList) {
      if(item) {
        valueFound = true;
        break;
      }
    }
    this.searchStatus = valueFound ? "search" : "load";
    this.searchCriteria = this.searchFormField;
    this.getOvDetails();
  }

  createScheduleJosn(fetchDays = 5) {
    this.scheduleList =[];
    for(let i = 0; i < fetchDays; i++) {
      let tempObj = {
        "label": "D-"+i, "disabled": false
      }
      this.scheduleList.push(tempObj);
    }
  }

  showFailureLog(modalTemplate,key,type) {
    this.showLoader = true;
    this.reportsService.getFailureLogs(this.sharedService.createServiceToken(), key,type)
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
                                let index = 0;
                                this.envModalData = jsonStatue.errorLogs;
                                this.jsonType = 'Failure log';
                                this.envInfoModalBlock = this.modalService.open(modalTemplate, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView' });
                            } else {
                                this.showLoader = false;
                               // this.displayModel(jsonStatue.reason, "failureIcon");
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
                    let jsonStatue =
                    {
                        "errorLogs": "PreMigration\n-------------------------------\nUpload the RF script folder for the 7090012298\n",
                        "sessionId": "348da7c6",
                        "serviceToken": "53596",
                        "status": "SUCCESS"
                    };
                    if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {

                        this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });

                    } else {
                        if (jsonStatue.status == "SUCCESS") {                              
                            this.noCheckListDataVisibility = false;
                        
                            let index = 0;
                            this.runningLogs = jsonStatue.errorLogs;
                            // this.runningLogModalBlock = this.modalService.open(content, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView' });
                            this.showRunningLogLauncher = false;
                            this.showRunningLogContent = true;
                            // Call the service to refresh on time interval of 10 sec.
                            
                            //this.isProcessCompleted=jsonStatue.isProcessCompleted;                               
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
                            //this.displayModel(jsonStatue.reason, "failureIcon");
                        }
                    }
                }, 1000);  */
                //Please Comment while checkIn
            });
}
  ExportFile() {
    this.showLoader = true;
    this.reportsService.ExportFile(this.sharedService.createServiceToken(), this.searchCriteria, this.searchStatus, this.paginationDetails)
      .subscribe(
        data => {
          this.showLoader = false;
          let blob = new Blob([data["_body"]], {
            type: "application/octet-stream"
          });

          FileSaver.saveAs(blob, "OVDetails.xlsx");

        },
        error => {
          //Please Comment while checkIn
          /* let jsonStatue: any = { "sessionId": "506db794", "reason": "Download Failed", "status": "SUCCESS", "serviceToken": "63524" };
          data => {
            this.showLoader = false;
            let blob = new Blob([data["_body"]], {
              type: "application/octet-stream"
            });

            FileSaver.saveAs(blob, "NetworkConfig.xlsx");
          }
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


  deleteRow(event, deleteModal, rowId) {

    let deleteState = event.target;

  //  if (deleteState.className != "deleteRowDisabled") {

        this.modalService.open(deleteModal, {
            keyboard: false,
            backdrop: 'static',
            size: 'lg',
            windowClass: 'confirm-modal'
        }).result.then((result) => {
            this.closeResult = `Closed with: ${result}`;
        }, (reason) => {

            this.showLoader = true;

            this.reportsService.deleteRunTestData(rowId, this.sharedService.createServiceToken())
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
                                   // this.resetValues(event);
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
  //  }
    $("#dataWrapper").find(".scrollBody").scrollLeft(0);

}

copyFunction() {
  (document.getElementById('logArea') as HTMLInputElement).select();
  document.execCommand("copy");
  var x = document.getElementById("snackbar");
  x.className = "show";
  setTimeout(function(){ x.className = x.className.replace("show", ""); }, 3000);
}

searchAuditReport(event: { target: { classList: { contains: (arg0: string) => any; }; parentNode: { parentNode:  any;  }; }; }){
  
  let currentForm = event.target.parentNode.parentNode;
  let runTestId = currentForm.querySelector("#searchRunId").value;
  let pattern = /^\d+$/
  if(runTestId == "" || (pattern.test(runTestId))){
    this.validationData1.rules.searchRunId.customfunction = false;
  }else{
    this.validationData1.rules.searchRunId.customfunction = true;
  }
  if(runTestId == "") runTestId = null;
  if(pattern.test(runTestId)) runTestId = parseInt(runTestId);
  validator.performValidation(event, this.validationData1, "search");
  setTimeout(() => {
  if(this.isValidForm(event)){
    this.auditSearchCriteria = {
      fromDate : currentForm.querySelector("#fromDate").value,
      toDate : currentForm.querySelector("#toDate").value,
      neName : currentForm.querySelector("#searchNeName").value,
      neId : currentForm.querySelector("#searchNeId").value,
      runTestId : runTestId as number,
      siteName : currentForm.querySelector("#searchSiteName").value,
      programName : currentForm.querySelector("#programName").value,
      status : currentForm.querySelector("#searchStatus").value
    }
      this.getReports(this.auditSearchCriteria);
  }
},0);
}

clearAuditForm() {
  this.auditForm.nativeElement.reset();
  this.auditSearchCriteria = {fromDate :"",toDate : "",neName : "",neId : "",runTestId : null,siteName : "",programName : "", status : ""};
  this.getReports(this.auditSearchCriteria); 
}


clearFailureReportForm() {
  this.failureReportForm.nativeElement.reset();
  this.failureSearchCriteria = {fromDate :"",toDate : "",noOfDays : 0,neId : "", programName: []};
}

getReports(searchCriteria:any) {
  this.showLoader = true;
    this.reportsService.getAuditReportDetails(this.sharedService.createServiceToken(),this.paginationDetails,searchCriteria)
      .subscribe(
        data => {
          setTimeout(() => {
            let jsonStatue = data.json();
            this.tableData = jsonStatue;
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

                   this.showLoader = false;
                   this.totalPages = jsonStatue.pageCount;
                  let pageCount = [];
                  for (var i = 1; i <= jsonStatue.pageCount; i++) {
                    pageCount.push(i);
                  }
                  this.pageRenge = pageCount;
                  this.pager = this.getPager(this.totalPages * this.pageSize, this.currentPage, this.pageRenge);

                  if (jsonStatue.auditStatusDetails == null || jsonStatue.auditStatusDetails.length == 0) {
                    this.reportTableShowHide = false;
                    this.noAuditReportsData = true;
                  } else {
                    this.noAuditReportsData = false;
                    this.reportTableData = {"auditReportDetails":jsonStatue.auditStatusDetails};
                    this.reportTableShowHide = true;
                    

                    setTimeout(() => {
                      let tableWidth = document.getElementById('auditReportTable').scrollWidth;
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
              else{
                
                  this.showLoader = false;
                  this.displayModel(jsonStatue.reason,"failureIcon");  
                 
              }
            }

        }, 1000);

        },
        error => {
          //Please Comment while checkIn
         /* let currentData = { "sessionId": "fff0e445", "serviceToken": "74388", "status": "SUCCESS" };
          setTimeout(() => {
            this.showLoader = false;
            this.reportTableShowHide= true;
            this.noAuditReportsData = false;
            this.reportTableData = { "auditReportDetails": [
              {id:1,timeStamp:"07-07-2010 to 10-10-2012",userName:'admin',programName:"dummy",status:"active",neId:1234,neName:"NE dummy name",siteName:"Site dummy",report:"report.xls"},
              {id:2,timeStamp:"07-07-2010 to 10-10-2012",userName:'admin',programName:"dummy",status:"inactive",neId:1234,neName:"NE dummy name",siteName:"Site dummy",report:"report.xls"},
              {id:3,timeStamp:"07-07-2010 to 10-10-2012",userName:'admin',programName:"dummy",status:"active",neId:1234,neName:"NE dummy name",siteName:"Site dummy",report:"report.xls"},
              {id:4,timeStamp:"07-07-2010 to 10-10-2012",userName:'admin',programName:"dummy",status:"inactive",neId:1234,neName:"NE dummy name",siteName:"Site dummy",report:"report.xls"},
              {id:5,timeStamp:"07-07-2010 to 10-10-2012",userName:'admin',programName:"dummy",status:"active",neId:1234,neName:"NE dummy name",siteName:"Site dummy",report:"report.xls"}] };
            console.log("Sample Data");
          }, 1000);
          setTimeout(() => {
            let tableWidth = document.getElementById('auditReportTable').scrollWidth;
            $(".scrollBody table").css("min-width", (tableWidth) + "px");
            $(".scrollHead table").css("width", tableWidth + "px");


            $(".scrollBody").on("scroll", function (event) {
              $(".formEditRow #formWrapper").css("margin-left", (event.target.scrollLeft - 5) + "px");
              $(".form-control-fixed").css("right", (event.target.scrollLeft * -1) + "px");
              $(".scrollHead table").css("margin-left", (event.target.scrollLeft * -1) + "px");
            });
            $(".scrollBody").css("max-height", (this.tableDataHeight - 10) + "px");

          }, 0); */
          //Please Comment while checkIn
        });
}
downloadBulkReport() {
  this.showLoader = true;
  this.reportsService.bulkReportDownload(this.sharedService.createServiceToken())
    .subscribe(
      data => {
        this.showLoader = false;
        let blob = new Blob([data["_body"]], {
          type: "application/octet-stream"
        });

        FileSaver.saveAs(blob, "AuditCriticalParamBulkReport.xlsx");

      },
      error => {
        //Please Comment while checkIn
        /* let jsonStatue: any = { "sessionId": "506db794", "reason": "Download Failed", "status": "SUCCESS", "serviceToken": "63524" };
        (          data: { [x: string]: BlobPart; }) => {
          this.showLoader = false;
          let blob = new Blob([data["_body"]], {
            type: "application/octet-stream"
          });

          FileSaver.saveAs(blob, "AuditSummaryBulkReport.xlsx");
        }
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
    getFailureReport(event: { target: { classList: { contains: (arg0: string) => any; }; parentNode: { parentNode:  any;  }; }; }){
      if (this.selprogNameList.length == 0) {
        this.displayModel("Please select at least one program.", "failureIcon");
        return;
      }
      let currentForm = event.target.parentNode.parentNode;
      this.failureSearchCriteria = {
        fromDate: currentForm.querySelector("#fromDates").value,
        toDate: currentForm.querySelector("#toDates").value,
        neId: currentForm.querySelector("#neId").value,
        noOfDays: parseInt(currentForm.querySelector("#selectDays").value),
        programName: this.selprogNameList
      };
      if (this.failureSearchCriteria.fromDate && this.failureSearchCriteria.toDate) {
        let selectedDays = this.getSelectedDays(this.failureSearchCriteria.fromDate, this.failureSearchCriteria.toDate);
        if (selectedDays > 10) {
          this.displayModel("You can select maximum 10 days.", "failureIcon");
          return;
        }
      }
      this.showLoader = true;
      this.reportsService
        .getFailureReport(
          this.sharedService.createServiceToken(),
          this.failureSearchCriteria
        )
        .subscribe(
          (data) => {
            this.showLoader = false;
            let blob = new Blob([data["_body"]], {
              type: "application/octet-stream",
            });
            
            let contName = data.headers.get('content-disposition');
            let filName = contName.split('filename=')[1];
            FileSaver.saveAs(blob, `MigScriptFailureReport${filName.slice(0, filName.length - 1)}.xlsx`);
          },
          (error) => {
            this.showLoader = false;
            if (error.status == '9202') {
              this.displayModel("No Data found.", "failureIcon");
            }
            //Please Comment while checkIn
            /* let jsonStatue: any = { "sessionId": "506db794", "reason": "Download Failed", "status": "SUCCESS", "serviceToken": "63524" };
        (          data: { [x: string]: BlobPart; }) => {
          this.showLoader = false;
          let blob = new Blob([data["_body"]], {
            type: "application/octet-stream"
          });

          FileSaver.saveAs(blob, "MigrationScriptFailureReport.xlsx");
        }
        setTimeout(() => {
          this.showLoader = false;
          if (jsonStatue.status == "SUCCESS") {

          } else {
            this.displayModel(jsonStatue.reason, "failureIcon");
          }

        }, 1000); */
            //Please Comment while checkIn
          }
        );
  }
  clearSelect(){
    //console.log("cleared selection");
    this.selectedDays = 0;
  }

  clearDate(){
    //console.log("cleared date");
    this.fromDts = "";
    this.toDates = "";
  }
  
  downloadVDartReport(event: { target: { classList: { contains: (arg0: string) => any; }; parentNode: { parentNode:  any;  }; }; }) {
      if (this.selprogName != 'VZN-5G-DSS' && this.selprogName != 'VZN-5G-CBAND' && this.selprogName != 'VZN-4G-FSU' && this.selprogName != 'VZN-4G-USM-LIVE') {
        this.displayModel("VDart download not applicable only for selected program", "failureIcon");
        return;
      }
      let currentForm = event.target.parentNode.parentNode;
      let vDartCriteria = {fromDate :"",toDate : "",neName : "",neId : "",programName : ""};
      validator.performValidation(event, this.validationData1, "search");
      setTimeout(() => {
      if(this.isValidForm(event)){
          vDartCriteria = {
            fromDate : currentForm.querySelector("#fromDate").value,
            toDate : currentForm.querySelector("#toDate").value,
            neName : currentForm.querySelector("#searchNeName").value,
            neId : currentForm.querySelector("#searchNeId").value,
            programName : currentForm.querySelector("#programName").value,
          }

          this.showLoader = true;
          this.reportsService.getVdartDailyStatusDetails(this.sharedService.createServiceToken(), vDartCriteria).subscribe(data => {
            this.showLoader = false;
            let blob = new Blob([data["_body"]], {
              type: "application/octet-stream"
            });

            FileSaver.saveAs(blob, "VdartDailyStatusDetails.xlsx");
          },
          error => {
            //Please Comment while checkIn
            /* let jsonStatue: any = { "sessionId": "506db794", "reason": "Download Failed", "status": "SUCCESS", "serviceToken": "63524" };
            (          data: { [x: string]: BlobPart; }) => {
              this.showLoader = false;
              let blob = new Blob([data["_body"]], {
                type: "application/octet-stream"
              });

              FileSaver.saveAs(blob, "AuditSummaryBulkReport.xlsx");
            }
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
      },0);
    } 
}