import { ViewContainerRef, Input, ElementRef, Renderer, Component, OnInit, ViewChild, HostListener } from '@angular/core';
import { Router, NavigationEnd} from '@angular/router';
import { NgbModal, ModalDismissReasons} from '@ng-bootstrap/ng-bootstrap';
import { CiquploadService } from '../services/ciqupload.service';
import { TimerObservable } from "rxjs/observable/TimerObservable";
import { SharedService } from '../services/shared.service';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { filter, map, mergeMap} from 'rxjs/operators';
import { validator } from '../validation';
import {platformBrowserDynamic} from '@angular/platform-browser-dynamic';
import * as FileSaver from 'file-saver';

import * as $ from 'jquery';
import * as _ from 'underscore';
import { ReportsService } from '../services/reports.service';

declare var Chart: any;
declare var AmCharts : any;
declare var jstz: any;
@Component({
  selector: 'rct-ciqupload',
  templateUrl: './ciqupload.component.html',
  styleUrls: ['./ciqupload.component.scss'],
  providers: [CiquploadService]
})
  export class CiquploadComponent implements OnInit {
    scriptFilesData: any;
    fetchRemarks: any;
    fetchCheckList: any;
    fetchScriptServer: any;
    fetchScriptServerIp: any;
    fetchCiqServerIp: any;
    fetchCiqServer: any;
    retriveCiq:boolean = true;
    searchBlock:boolean = false;
  uploadFiletableShowHide:boolean;
  uploadFile:any;
  uploadFileStatus:any;
  successFilesList = [];
  chartShowHide :boolean = false;
  showNoDataFound: boolean;
  showLoader:boolean = true;
  tableData:any;
  max = new Date();
  closeResult:string;
  privilegeSetting : object;
  noDataVisibility :boolean = false;
  showModelMessage: boolean = false;
  messageType: any;
  modelData :any;
  sessionExpiredModalBlock : any; // Helps to close/open the model window
  successModalBlock : any;
  fetchInfoBlock: any;
  message : any;
  aircraftOffloadBlock:any;
  timezone: any;
  tableShowHide :boolean = false;
  ovCkeckedOrNot:boolean = false;
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
  aginationShowHide: boolean;
  pager: any = {}; // pager Object
  netWorkDetails:any;
  lsmVersionList = [];
  networkType:any;
  networkTypeId:any;
  lsmVersion:any;
  radioOperation = "";
  fetch:any;
  remarks:any;
  ckeckedOrNot:any;
  navigationSubscription: any;
  searchStatus: string;
  searchCriteria: any;
   downLoadImage:boolean;
  showPopOver:boolean;
  ciqFileType:boolean = false;
  listFileType:boolean = false;
  scriptFileType:boolean = false;
  editScriptType:boolean = false;
  editListType:boolean = false;
  fromDt:any;
  toDate:any;
  scriptFileNameList: any;
  deleteScriptFileList: any = [];
  programChangeSubscription: any;
  programListCallInterval: any;
  selectedMarkets = [];
  dropdownSettings = {};
  marketDropDownList = [];
  scriptListModalBlock: any;
  scriptList: any = [];
  rfScriptList: any;
  rfScriptListModel: any;
  fetchInfoMsg: any;
  ovOverallInteraction:any;
  currentLoggedInUserGroup: any;
  importSelectedSites: boolean = false;

  validationData: any = {
        "rules": {
            "ciqUpload": {
                "required": true
            },
            "uploadScript":{
                "required": false
            },
            "uploadCheckList":{
                "required": false
            }, 
            "fromDate":{
                "required": false
            },
            "toDate":{
                "required": false
            },
            "ciqFileName":{
                "required": false
            }, 
            "scriptFileName":{
                "required": false
            }, 
            "checklistFileName":{
                "required": false
            }, 
            "ciqVersion":{
                "required": false
            }, 
            "fileSourceType":{
                "required": false
            },
            "uploadBy":{
                "required": false
            },
            "market": {
                "required": false
            },
            "scriptList": {
                "required": false
            }
        },
        "messages": {
            "ciqUpload": {
                "required": "Please Select Files"
            },
            "uploadScript":{
                "required": "Please Select Files"
            },
            "uploadCheckList":{
                "required": "Please Select Files" 
            }, 
            "fromDate":{
                "required": "Please Select date range"
            },
            "toDate":{
                "required": "Please Select date range"
            },
            "ciqFileName":{
                "required": "Please select CIQ Name"
            }, 
            "scriptFileName":{
                "required": "Please select Script Name"
            }, 
            "checklistFileName":{
                "required": "Please select Check List"
            }, 
            "ciqVersion":{
                "required": "Please select CIQ Version"
            }, 
            "fileSourceType":{
                "required": "Please select Types"
            },
            "uploadBy":{
                "required": "Please select User NAME"
            },
            "market": {
                "required": "Market is required"
            },
            "scriptList": {
                "required": "RFScript is required"
            }
        }
    };

  @ViewChild('searchTab') searchTabRef: ElementRef;
  @ViewChild('retrieveTab') retrieveTabRef: ElementRef;
  @ViewChild('confirmModal') confirmModalRef: ElementRef;
  @ViewChild('sessionExpiredModal') sessionExpiredModalRef: ElementRef;
  @ViewChild('successModal') successModalRef: ElementRef;
  @ViewChild('ciqUpload') filePostRef: ElementRef;
  @ViewChild('uploadCheckList') fileCheckListRef: ElementRef;
  @ViewChild('uploadScript')  fileScriptRef: ElementRef;
  @ViewChild('editUploadCheckList') editCheckListFileRef: ElementRef;
  @ViewChild('editUploadScript')  editScriptFileRef: ElementRef;
  @ViewChild('searchForm') searchForm;
  @ViewChild('retrieveCiqForm') retrieveCiqForm;


    private timer;
    private subscription: Subscription;
    programName: any;
    fetchRfDb: any;
    constructor(
    private element: ElementRef,
    private renderer: Renderer,
    private router:Router,
    private modalService: NgbModal,
    private reportsService:ReportsService,
    private ciquploadService: CiquploadService,
    private sharedService: SharedService    
    ) {
        /* this.navigationSubscription = this.router.events.subscribe((e: any) => {
            // If it is a NavigationEnd event re-initalise the component
            if (e instanceof NavigationEnd) {
                console.log("Constructor : "+ e);
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
  loadInitialData(){
      //For Pagination
    this.uploadFiletableShowHide = false;
    this.ovCkeckedOrNot = false;
    this.radioOperation = "UPLOAD";
    this.searchStatus = 'load';
    this.currentPage = 1;
    this.totalPages = 1;
    this.TableRowLength = 10;
    this.pageSize = 10;
    this.editableFormArray = [];
     let paginationDetails = {
          "count": this.TableRowLength,
          "page": this.currentPage
      };
    this.paginationDetails = paginationDetails;  
    let tz = jstz.determine();
    this.timezone = tz.name();

    this.dropdownSettings = {
        singleSelection: false,
        idField: 'item_id',
        textField: 'item_text',
        selectAllText: 'Select All',
        unSelectAllText: 'UnSelect All',
        itemsShowLimit: 1,
        allowSearchFilter: true
    };
    this.selectedMarkets = [];
    this.rfScriptList = "";
    this.currentLoggedInUserGroup = JSON.parse(sessionStorage.loginDetails).userGroup;
    this.programName = JSON.parse(sessionStorage.getItem("selectedProgram")).programName;    
    this.showLoader = true;
    this.getUploadDetails();
    this.resetCiqForm();
    this.timer = TimerObservable.create(2000, 2000);
    this.setMenuHighlight("retrieveCiq");
    this.retriveCiq = true;
    this.searchBlock= false;
    this.scriptFileNameList = [];
    this.deleteScriptFileList = [];
    this.importSelectedSites = false;
  }
  clearSearchFrom() {
    this.searchForm.nativeElement.reset();  
}

retrieveCiq() {
    this.retriveCiq = true;
    this.searchBlock= false;
    this.ovCkeckedOrNot = false;
    this.setMenuHighlight("retrieveCiq");
    this.editableFormArray = [];
    this.searchStatus = "load";
    this.searchCriteria = null;
    this.getUploadDetails();
} 
searchCiq() {
    this.retriveCiq = false;
    this.searchBlock= true;
    this.ovCkeckedOrNot = false;
    this.setMenuHighlight("searchCiq");
    this.searchStatus = "load";
    this.searchCriteria = null;
    this.getUploadDetails(true);
    this.editableFormArray = [];
    setTimeout(() => {
        this.searchForm.nativeElement.reset();
    }, 0);
    

}
 
resetCiqForm() {
    setTimeout(() => {
       
        this.retrieveCiqForm.nativeElement.reset();
    }, 0);
}

getUploadDetails(isSearch = false){
  this.tableShowHide = false;
  $("#remarks").val("");
  this.showLoader = true;
  $("#dataWrapper").find(".scrollBody").scrollLeft(0);
  this.ciquploadService.getuploadetails(isSearch ? "" : this.radioOperation, this.searchStatus, this.searchCriteria, this.sharedService.createServiceToken(),this.paginationDetails)
        .subscribe(
            data => {
                setTimeout(() => {
                  let jsonStatue = data.json();

                  this.tableData = data.json();
                  this.showLoader = false;
                  
                  
                      if(jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User"){                     
                        if(!this.sessionExpiredModalBlock){
                            this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, {
                                keyboard: false,
                                backdrop: 'static',
                                size: 'lg',
                                windowClass: 'session-modal'
                            });
                        }
                     
                      } else {

                        if( this.sharedService.isvalidServiceToken( parseInt(data.json().serviceToken,10) ) || data.json().sessionId == "408"){
                          if(jsonStatue.status == "SUCCESS"){
                            this.tableData = jsonStatue;
                            this.privilegeSetting = this.tableData.privilegeSetting;
                             this.totalPages = this.tableData.pageCount;
                             this.ovOverallInteraction = this.tableData.ovOverallInteraction;
                             this.fetchRfDb = this.tableData.fetchFromRfDb;
                                let pageCount = [];
                                for (var i = 1; i <= this.tableData.pageCount; i++) {
                                    pageCount.push(i);
                                }
                                this.pageRenge = pageCount;
                                this.pager = this.getPager(this.totalPages*this.pageSize, this.currentPage, this.pageRenge);
                                this.netWorkDetails = this.tableData.netWorkDetails;
                            
                            this.marketDropDownList = [];
                            this.selectedMarkets = [];
                            let marketList = this.tableData.marketList;
                            for (let itm of  marketList ? marketList.split("\n") : []) {
                                let marketDropDownList = { item_id: itm, item_text: itm };
                                this.marketDropDownList.push(marketDropDownList);
                                // By default, all market should be selected
                                // this.selectedMarkets.push(itm);
                            }

                            if(this.tableData.ciqUploadAuditTrailDetModels.length == 0){
                              this.tableShowHide = false;
                              this.noDataVisibility = true;
                            }else{
                              this.tableShowHide = true;
                               this.noDataVisibility = false;
                               setTimeout(() => {
                                let tableWidth = document.getElementById('uploadDetails').scrollWidth;
                                        $(".scrollBody table").css("min-width",(tableWidth) + "px");
                                        $(".scrollHead table").css("width", tableWidth + "px");

                                    
                                        $(".scrollBody").on("scroll", function (event) {
                                            $(".formEditRow #formWrapper").css("margin-left", (event.target.scrollLeft - 5) + "px");
                                            $(".form-control-fixed").css("right",(event.target.scrollLeft * -1) + "px");
                                            $(".scrollHead table").css("margin-left",(event.target.scrollLeft * -1) + "px");
                                        });
                                        //$(".scrollBody").css("max-height",(this.tableDataHeight - 10) + "px");

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
           /*    setTimeout(() => {
                this.showLoader = false;
                // this.tableData = {"ciqUploadAuditTrailDetModels":[{"id":7,"fileName":"FDMIMO_FIT_CIQ_08242018_SprintTemplate.xlsx","networkType":"3G","lsmVersion":"11","uploadBy":"admin","remarks":"uploaded successfull","creationDate":"2018-12-06 12:30:35"},{"id":8,"fileName":"FDMIMO_FIT_CIQ_1.xlsx","networkType":"4G","lsmVersion":"44","uploadBy":"admin","remarks":"uploaded successfull","creationDate":"2018-12-06 12:30:35"}],"sessionId":"91fce146","serviceToken":"93046","status":"SUCCESS","pageCount":1,"netWorkDetails":[{"id":1,"networkType":"3G","lsmVersionList":["11","45"]},{"id":2,"networkType":"4G","lsmVersionList":["22","33"]}]};
                 //this.tableData = {"sessionId":"3c28a9df","serviceToken":"72957","status":"SUCCESS","pageCount":1,"ciqNetworkConfigId":6,"ciqName":["UNY-NE-VZ_CIQ_Ver2.82_02252019.xlsx","UNY-NE-VZ_CIQ_Ver2.82_01282019.xlsx"],"searchEndDate":"02/26/2019","ciqServerIp":"10.20.120.50","ciqVersion":["ORIGINAL"],"type":["FETCH","UPLOAD"],"userName":["superadmin"],"scriptServer":"scr_1_1_1","searchStartDate":"02/19/2019","scriptServerIp":"10.20.120.50","ciqServer":"Ciq_1_1_1","scriptNetworkConfigId":5,"checkList":["","Check_list_Test.xlsx"],"marketList":"UPNY\nNew England\nUpstate NY","scriptName":["1_58154_LEXINGTON_12_MA.zip,1_6003_networdata123.zip,1_6013_networkcohyutrr.zip,1_6203_networkconfig.zip,","1_1111_LEXINGTON_12_MA.zip"],"ciqUploadAuditTrailDetModels":[{"id":35,"programDetailsEntity":{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":null,"status":"InActive","creationDate":"2019-02-11T09:24:41.000+0000","createdBy":"superadmin"},"programName":null,"ciqFileName":"UNY-NE-VZ_CIQ_Ver2.82_01282019.xlsx","ciqFilePath":"/home/user/RCT/rctsoftware/SMART/Customer/2/PreMigration/Input/UNY-NE-VZ_CIQ_Ver2.82_01282019/CIQ/","scriptFileName":"1_1111_LEXINGTON_12_MA.zip","scriptFilePath":"/home/user/RCT/rctsoftware/SMART/Customer/2/PreMigration/Input/UNY-NE-VZ_CIQ_Ver2.82_01282019/SCRIPT/","checklistFileName":"Check_list_Test.xlsx","checklistFilePath":"/home/user/RCT/rctsoftware/SMART/Customer/2/PreMigration/Input/UNY-NE-VZ_CIQ_Ver2.82_01282019/CHECKLIST/","ciqVersion":"ORIGINAL","fileSourceType":"UPLOAD","uploadBy":"superadmin","remarks":"aaadada","creationDate":"2019-02-26 12:22:35","searchStartDate":null,"searchEndDate":null,"fromDate":null,"toDate":null},{"id":36,"programDetailsEntity":{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":null,"status":"InActive","creationDate":"2019-02-11T09:24:41.000+0000","createdBy":"superadmin"},"programName":null,"ciqFileName":"UNY-NE-VZ_CIQ_Ver2.82_02252019.xlsx","ciqFilePath":"/home/user/RCT/rctsoftware/SMART/Customer/2/PreMigration/Input/UNY-NE-VZ_CIQ_Ver2.82_02252019/CIQ/","scriptFileName":"1_58154_LEXINGTON_12_MA.zip,1_6003_networdata123.zip,1_6013_networkcohyutrr.zip,1_6203_networkconfig.zip","scriptFilePath":"/home/user/RCT/rctsoftware/SMART/Customer/2/PreMigration/Input/UNY-NE-VZ_CIQ_Ver2.82_02252019/SCRIPT/","checklistFileName":"","checklistFilePath":"","ciqVersion":"ORIGINAL","fileSourceType":"FETCH","uploadBy":"superadmin","remarks":"","creationDate":"2019-02-26 12:50:36","searchStartDate":null,"searchEndDate":null,"fromDate":null,"toDate":null}]};
               // this.tableData = {"pageCount":1,"ciqNetworkConfigId":133,"ciqName":["4G_CIQ_2021_06_03_2021_06_10_01.xlsx","TRI-VZ_CIQ_Ver_0.0.09_20210225_DUPLICATE_DSP_IN_GROW_TEMPLATE.xlsx","SOC-VZ_CIQ_Ver_0.0.07_20201214.xlsx","TRI-VZ_CIQ_Ver_0.0.09_20210225.xlsx","Pittsburgh_IODT_4G-CIQ _BridgevilleSDX_v4.xlsx","4G_CIQ_2021_06_14_2021_06_21_01.xlsx","SOC-VZ_CIQ_Ver_0.0.13_20210303.xlsx","4G_CIQ_2021_06_04_2021_06_11_01.xlsx","SamsungCIQtemplate_Offshore_Test.xlsx","SamsungCIQ_1062020_VZW_4G_CTXARLINGTONMSC_001Rev3.xlsx","4G_CIQ_2021_06_02_2021_06_09_02.xlsx","CTX-VZ_CIQ_Ver_0.0.13_20210208.xlsx","4G_CIQ_2021_06_15_2021_06_22_01.xlsx","AthithyaUNY-NE-VZ_CIQ_Ver3.7.96_11022020(3).xlsx","SOC-VZ_CIQ_Ver_0.0.13_20210303_noDSP.xlsx","WBV-VZ_CIQ_Ver_0.0.03_20201214.xlsx","CTX-VZ_CIQ_Ver_0.0.12_20210121.xlsx","SOC-VZ_CIQ_Ver_0.0.12_20210302_HIGH_POWER.xlsx","4G_CIQ_2021_06_14_2021_06_21_02.xlsx","HGC-VZ_CIQ_Ver_0.0.6_20201214.xlsx","SOC-VZ_CIQ_Ver_0.0.20_20210330_New_Commission_Script.xlsx","4G_CIQ_2021_06_15_2021_06_22_02.xlsx","AthithyaUNY-NE-VZ_CIQ_Ver3.7.96_11022020.xlsx","CTX-VZ_CIQ_Ver_0.0.06_20201214.xlsx","WBV-VZ_CIQ_Ver_0.0.03_20201214(1).xlsx","CTX-VZ_CIQ_Ver_0.0.10_20210105(1).xlsx","4G_CIQ_2021_06_14_2021_06_21_03.xlsx","TRI-VZ_CIQ_Ver_0.0.04_20201214.xlsx","HGC-VZ_CIQ_Ver_0.0.11_20210211.xlsx"],"searchEndDate":"06/15/2021","ciqServerIp":"10.20.120.72","ciqVersion":["ORIGINAL"],"sessionId":"630b2bb5","type":["OV- Auto Fetch","OV- Force Fetch","UPLOAD"],"userName":["","superadmin"],"marketList":"UPNY\nNew England\nCTX","scriptServer":"Fetch NE","searchStartDate":"06/08/2021","scriptServerIp":"10.20.120.72","ovOverallInteraction":"ON","ciqServer":"Fetch NE","scriptNetworkConfigId":125,"checkList":["","Verizon_USM_Migration Checklist_1.1.5(1)(5).xlsx","Verizon_USM_Migration Checklist_1.1.5.xlsx"],"scriptName":["117965.zip,117966.zip,117967.zip","139647.zip,134742.zip","88001.zip"],"serviceToken":"83404","ciqUploadAuditTrailDetModels":[{"id":547,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-05-03T07:21:24.000+0000","createdBy":"admin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"programName":null,"ciqFileName":"4G_CIQ_2021_06_15_2021_06_22_02.xlsx","ciqFilePath":"Customer/34/PreMigration/Input/4G_CIQ_2021_06_15_2021_06_22_02/CIQ/","scriptFileName":"139647.zip,134742.zip","scriptFilePath":"Customer/34/PreMigration/Input/4G_CIQ_2021_06_15_2021_06_22_02/SCRIPT","checklistFileName":"","checklistFilePath":"","ciqVersion":"ORIGINAL","fileSourceType":"OV- Auto Fetch","uploadBy":"","remarks":"Uploaded through Fetch Automation Functionality","creationDate":"2021-06-15 07:04:13","searchStartDate":null,"searchEndDate":null,"fromDate":null,"toDate":null,"fetchInfo":null},{"id":546,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-05-03T07:21:24.000+0000","createdBy":"admin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"programName":null,"ciqFileName":"4G_CIQ_2021_06_15_2021_06_22_01.xlsx","ciqFilePath":"Customer/34/PreMigration/Input/4G_CIQ_2021_06_15_2021_06_22_01/CIQ/","scriptFileName":"139647.zip,134742.zip","scriptFilePath":"Customer/34/PreMigration/Input/4G_CIQ_2021_06_15_2021_06_22_01/SCRIPT","checklistFileName":"","checklistFilePath":"","ciqVersion":"ORIGINAL","fileSourceType":"OV- Auto Fetch","uploadBy":"","remarks":"Uploaded through Fetch Automation Functionality","creationDate":"2021-06-15 07:03:33","searchStartDate":null,"searchEndDate":null,"fromDate":null,"toDate":null,"fetchInfo":null},{"id":545,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-05-03T07:21:24.000+0000","createdBy":"admin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"programName":null,"ciqFileName":"4G_CIQ_2021_06_14_2021_06_21_03.xlsx","ciqFilePath":"Customer/34/PreMigration/Input/4G_CIQ_2021_06_14_2021_06_21_03/CIQ/","scriptFileName":"139647.zip,134742.zip","scriptFilePath":"Customer/34/PreMigration/Input/4G_CIQ_2021_06_14_2021_06_21_03/SCRIPT","checklistFileName":"","checklistFilePath":"","ciqVersion":"ORIGINAL","fileSourceType":"OV- Auto Fetch","uploadBy":"","remarks":"Uploaded through Fetch Automation Functionality","creationDate":"2021-06-14 19:01:31","searchStartDate":null,"searchEndDate":null,"fromDate":null,"toDate":null,"fetchInfo":null},{"id":544,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-05-03T07:21:24.000+0000","createdBy":"admin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"programName":null,"ciqFileName":"4G_CIQ_2021_06_14_2021_06_21_02.xlsx","ciqFilePath":"Customer/34/PreMigration/Input/4G_CIQ_2021_06_14_2021_06_21_02/CIQ/","scriptFileName":"139647.zip,134742.zip","scriptFilePath":"Customer/34/PreMigration/Input/4G_CIQ_2021_06_14_2021_06_21_02/SCRIPT","checklistFileName":"","checklistFilePath":"","ciqVersion":"ORIGINAL","fileSourceType":"OV- Auto Fetch","uploadBy":"","remarks":"Uploaded through Fetch Automation Functionality","creationDate":"2021-06-14 19:00:51","searchStartDate":null,"searchEndDate":null,"fromDate":null,"toDate":null,"fetchInfo":null},{"id":542,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-05-03T07:21:24.000+0000","createdBy":"admin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"programName":null,"ciqFileName":"4G_CIQ_2021_06_14_2021_06_21_01.xlsx","ciqFilePath":"Customer/34/PreMigration/Input/4G_CIQ_2021_06_14_2021_06_21_01/CIQ/","scriptFileName":"139647.zip,134742.zip","scriptFilePath":"Customer/34/PreMigration/Input/4G_CIQ_2021_06_14_2021_06_21_01/SCRIPT","checklistFileName":"","checklistFilePath":"","ciqVersion":"ORIGINAL","fileSourceType":"OV- Force Fetch","uploadBy":"","remarks":"Uploaded through Force Fetch Functionality","creationDate":"2021-06-14 12:53:56","searchStartDate":null,"searchEndDate":null,"fromDate":null,"toDate":null,"fetchInfo":null}],"status":"SUCCESS"};
                this.tableData = {"pageCount":1,"ciqNetworkConfigId":133,"ciqName":["4G_CIQ_2021_06_03_2021_06_10_01.xlsx","TRI-VZ_CIQ_Ver_0.0.09_20210225_DUPLICATE_DSP_IN_GROW_TEMPLATE.xlsx","SOC-VZ_CIQ_Ver_0.0.07_20201214.xlsx","TRI-VZ_CIQ_Ver_0.0.09_20210225.xlsx","Pittsburgh_IODT_4G-CIQ _BridgevilleSDX_v4.xlsx","4G_CIQ_2021_06_14_2021_06_21_01.xlsx","SOC-VZ_CIQ_Ver_0.0.13_20210303.xlsx","4G_CIQ_2021_06_04_2021_06_11_01.xlsx","SamsungCIQtemplate_Offshore_Test.xlsx","SamsungCIQ_1062020_VZW_4G_CTXARLINGTONMSC_001Rev3.xlsx","4G_CIQ_2021_06_02_2021_06_09_02.xlsx","CTX-VZ_CIQ_Ver_0.0.13_20210208.xlsx","4G_CIQ_2021_06_15_2021_06_22_01.xlsx","AthithyaUNY-NE-VZ_CIQ_Ver3.7.96_11022020(3).xlsx","SOC-VZ_CIQ_Ver_0.0.13_20210303_noDSP.xlsx","WBV-VZ_CIQ_Ver_0.0.03_20201214.xlsx","CTX-VZ_CIQ_Ver_0.0.12_20210121.xlsx","SOC-VZ_CIQ_Ver_0.0.12_20210302_HIGH_POWER.xlsx","4G_CIQ_2021_06_14_2021_06_21_02.xlsx","HGC-VZ_CIQ_Ver_0.0.6_20201214.xlsx","SOC-VZ_CIQ_Ver_0.0.20_20210330_New_Commission_Script.xlsx","4G_CIQ_2021_06_15_2021_06_22_02.xlsx","AthithyaUNY-NE-VZ_CIQ_Ver3.7.96_11022020.xlsx","CTX-VZ_CIQ_Ver_0.0.06_20201214.xlsx","WBV-VZ_CIQ_Ver_0.0.03_20201214(1).xlsx","CTX-VZ_CIQ_Ver_0.0.10_20210105(1).xlsx","4G_CIQ_2021_06_14_2021_06_21_03.xlsx","TRI-VZ_CIQ_Ver_0.0.04_20201214.xlsx","HGC-VZ_CIQ_Ver_0.0.11_20210211.xlsx"],"searchEndDate":"06/15/2021","ciqServerIp":"10.20.120.72","ciqVersion":["ORIGINAL"],"sessionId":"630b2bb5","type":["OV- Auto Fetch","OV- Force Fetch","UPLOAD"],"userName":["","superadmin"],"marketList":"UPNY\nNew England\nCTX","scriptServer":"Fetch NE","searchStartDate":"06/08/2021","scriptServerIp":"10.20.120.72","ovOverallInteraction":"ON","ciqServer":"Fetch NE","scriptNetworkConfigId":125,"checkList":["","Verizon_USM_Migration Checklist_1.1.5(1)(5).xlsx","Verizon_USM_Migration Checklist_1.1.5.xlsx"],"scriptName":["117965.zip,117966.zip,117967.zip","139647.zip,134742.zip","88001.zip"],"serviceToken":"83404","ciqUploadAuditTrailDetModels":[{"id":547,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-05-03T07:21:24.000+0000","createdBy":"admin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"programName":null,"ciqFileName":"4G_CIQ_2021_06_15_2021_06_22_02.xlsx","ciqFilePath":"Customer/34/PreMigration/Input/4G_CIQ_2021_06_15_2021_06_22_02/CIQ/","scriptFileName":"139647.zip,134742.zip","scriptFilePath":"Customer/34/PreMigration/Input/4G_CIQ_2021_06_15_2021_06_22_02/SCRIPT","checklistFileName":"","checklistFilePath":"","ciqVersion":"ORIGINAL","fileSourceType":"UPLOAD","uploadBy":"","remarks":"Uploaded through Fetch Automation Functionality","creationDate":"2021-06-15 07:04:13","searchStartDate":null,"searchEndDate":null,"fromDate":null,"toDate":null,"fetchInfo":"Fetch FAILED Due to Failed to unzip the file"},{"id":546,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-05-03T07:21:24.000+0000","createdBy":"admin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"programName":null,"ciqFileName":"4G_CIQ_2021_06_15_2021_06_22_01.xlsx","ciqFilePath":"Customer/34/PreMigration/Input/4G_CIQ_2021_06_15_2021_06_22_01/CIQ/","scriptFileName":"139647.zip,134742.zip","scriptFilePath":"Customer/34/PreMigration/Input/4G_CIQ_2021_06_15_2021_06_22_01/SCRIPT","checklistFileName":"","checklistFilePath":"","ciqVersion":"ORIGINAL","fileSourceType":"UPLOAD","uploadBy":"","remarks":"Uploaded through Fetch Automation Functionality","creationDate":"2021-06-15 07:03:33","searchStartDate":null,"searchEndDate":null,"fromDate":null,"toDate":null,"fetchInfo":"Fetch FAILED Due to Failed to unzip the file"},{"id":545,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-05-03T07:21:24.000+0000","createdBy":"admin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"programName":null,"ciqFileName":"4G_CIQ_2021_06_14_2021_06_21_03.xlsx","ciqFilePath":"Customer/34/PreMigration/Input/4G_CIQ_2021_06_14_2021_06_21_03/CIQ/","scriptFileName":"139647.zip,134742.zip","scriptFilePath":"Customer/34/PreMigration/Input/4G_CIQ_2021_06_14_2021_06_21_03/SCRIPT","checklistFileName":"","checklistFilePath":"","ciqVersion":"ORIGINAL","fileSourceType":"UPLOAD","uploadBy":"","remarks":"Uploaded through Fetch Automation Functionality","creationDate":"2021-06-14 19:01:31","searchStartDate":null,"searchEndDate":null,"fromDate":null,"toDate":null,"fetchInfo":"Success"},{"id":544,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-05-03T07:21:24.000+0000","createdBy":"admin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"programName":null,"ciqFileName":"4G_CIQ_2021_06_14_2021_06_21_02.xlsx","ciqFilePath":"Customer/34/PreMigration/Input/4G_CIQ_2021_06_14_2021_06_21_02/CIQ/","scriptFileName":"139647.zip,134742.zip","scriptFilePath":"Customer/34/PreMigration/Input/4G_CIQ_2021_06_14_2021_06_21_02/SCRIPT","checklistFileName":"","checklistFilePath":"","ciqVersion":"ORIGINAL","fileSourceType":"UPLOAD","uploadBy":"","remarks":"Uploaded through Fetch Automation Functionality","creationDate":"2021-06-14 19:00:51","searchStartDate":null,"searchEndDate":null,"fromDate":null,"toDate":null,"fetchInfo":null},{"id":542,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-05-03T07:21:24.000+0000","createdBy":"admin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"programName":null,"ciqFileName":"4G_CIQ_2021_06_14_2021_06_21_01.xlsx","ciqFilePath":"Customer/34/PreMigration/Input/4G_CIQ_2021_06_14_2021_06_21_01/CIQ/","scriptFileName":"139647.zip,134742.zip","scriptFilePath":"Customer/34/PreMigration/Input/4G_CIQ_2021_06_14_2021_06_21_01/SCRIPT","checklistFileName":"","checklistFilePath":"","ciqVersion":"ORIGINAL","fileSourceType":"UPLOAD","uploadBy":"","remarks":"Uploaded through Force Fetch Functionality","creationDate":"2021-06-14 12:53:56","searchStartDate":null,"searchEndDate":null,"fromDate":null,"toDate":null,"fetchInfo":"not found"}],"status":"SUCCESS"};
                if(this.tableData.status == "SUCCESS"){
                  this.totalPages = this.tableData.pageCount;
                  this.ovOverallInteraction = this.tableData.ovOverallInteraction;
                    let pageCount = [];
                    for (var i = 1; i <= this.tableData.pageCount; i++) {
                        pageCount.push(i);
                    }
                    this.pageRenge = pageCount;
                    this.pager = this.getPager(this.totalPages*this.pageSize, this.currentPage, this.pageRenge);
                    if(this.tableData.sessionId == "408" || this.tableData.status == "Invalid User"){
                       this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                    }
                 
                    this.privilegeSetting = {"USER_NAME":true};

                    this.marketDropDownList = [];
                    this.selectedMarkets = [];
                    let marketList = this.tableData.marketList;
                    for (let itm of  marketList ? marketList.split("\n") : []) {
                        let marketDropDownList = { item_id: itm, item_text: itm };
                        this.marketDropDownList.push(marketDropDownList);
                        // By default, all market should be selected
                        // this.selectedMarkets.push(itm);
                    }
                  
                    if(this.tableData.ciqUploadAuditTrailDetModels.length == 0){
                      this.tableShowHide = false;
                      this.noDataVisibility = true;
                    }else{
                      this.tableShowHide = true;
                      this.noDataVisibility = false;
                      setTimeout(() => {
                        let tableWidth = document.getElementById('uploadDetails').scrollWidth;
                        
                        $(".scrollBody table").css("min-width",(tableWidth) + "px");
                        $(".scrollHead table").css("width", tableWidth + "px");

                    
                        $(".scrollBody").on("scroll", function (event) {
                            $(".formEditRow #formWrapper").css("margin-left", (event.target.scrollLeft - 5) + "px");
                            $(".form-control-fixed").css("right",(event.target.scrollLeft * -1) + "px");
                            $(".scrollHead table").css("margin-left",(event.target.scrollLeft * -1) + "px");
                        });
                        //$(".scrollBody").css("max-height",(this.windowScreenHieght/2) + "px");
                        //$(".scrollBody").css("max-height",(this.tableDataHeight - 10) + "px");
                       
                        },0);                      
                    }
                }else{
                    this.showLoader = false;
                    this.displayModel(this.tableData.reason,"failureIcon");   
                  }

              }, 1000); */
            
              //Please Comment while checkIn
        });
}
 
uploadOrFetch(radioOperation){
    this.getUploadDetails();
}

showListOfSCriptFiles (popover, key) {
    popover.open();
    let fileName = key.scriptFileName;
    this.scriptFilesData = fileName ? fileName.substring().split(',') : [];
   /*  if(this.scriptFilesData.length > 1) {
        this.showPopOver = true;
        this.downLoadImage = false;
    } else {
        this.downLoadImage = true;
        this.showPopOver = false;
    } */
    
}

deleteFromScriptList(event, index, scriptFile) {
    // event.preventDefault();
    this.scriptFileNameList.splice(index, 1);
    this.deleteScriptFileList.push(scriptFile);
}

searchCiqData(event) {

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
                this.retriveCiq = false;
                this.showNoDataFound = false;

                let currentForm = event.target.parentNode.parentNode.parentNode,                   
                    searchCrtra = {
                        "fromDate": currentForm.querySelector("#fromDate").value,
                        "toDate": currentForm.querySelector("#toDate").value,
                        "ciqFileName": currentForm.querySelector("#ciqName").value,
                        "scriptFileName": currentForm.querySelector("#scriptName").value,
                        "checkList": currentForm.querySelector("#checkList").value,
                        "ciqVersion": currentForm.querySelector("#ciqVersion").value,
                        "fileSourceType": currentForm.querySelector("#types").value,
                        "uploadBy": currentForm.querySelector("#userName").value
                    };

                /* if (searchCrtra.searchBy && searchCrtra.searchParameter) {
                    this.searchStatus = "search";
                } */
                /* if (searchCrtra.fromDate || searchCrtra.toDate || searchCrtra.ciqFileName || searchCrtra.scriptFileName || searchCrtra.checkList || searchCrtra.ciqVersion || searchCrtra.fileSourceType || searchCrtra.uploadedBy) {
                    this.searchStatus = "search";
                }
                else {
                    this.searchStatus = "load";
                } */
                this.searchStatus = "search";

                this.searchCriteria = searchCrtra;

                this.currentPage = 1;
                let paginationDetails = {
                    "count": this.pageSize,
                    "page": this.currentPage
                };

                this.paginationDetails = paginationDetails;
                // TO get the searched data
                this.getUploadDetails();
            }
        }
    }, 0);
}


fetchCiqDeatailsFile(event,confirmFetchModal,activation) {
    if(!this.ovCkeckedOrNot && !this.importSelectedSites){
        if (this.radioOperation == 'UPLOAD' || (this.selectedMarkets && this.selectedMarkets.length > 0)) {
            this.validationData.rules.market.required = false;
            this.validationData.rules.scriptList.required = false;
        }else
        this.validationData.rules.market.required = true;
    }else{
        if (this.selectedMarkets && this.selectedMarkets.length > 0){
            this.validationData.rules.market.required = false;
        }
        else{
        this.validationData.rules.market.required = true;
        }
        if (this.rfScriptList){
            this.validationData.rules.scriptList.required = false;
        }
        else
        this.validationData.rules.scriptList.required = true;
    }

    let currentEvent = event.target.parentNode.parentNode.parentNode;
    let rfScriptFieldVal = currentEvent.querySelector("#scriptList").value;
    let rfScriptList = rfScriptFieldVal && rfScriptFieldVal.length > 0 ? rfScriptFieldVal.split("\n") : null;
    // let market = this.selectedMarkets.map((item) => item.item_text);
    
    let fetchFormDetails = {
        "rfScriptList":rfScriptList,
        "market":this.selectedMarkets,
        "remarks":currentEvent.querySelector("#remarks").value,
        "checkList":this.ckeckedOrNot,
        "scriptServer": this.radioOperation == 'IMPORT' ? '' : currentEvent.querySelector("#scriptServer").value,
        "scriptServerIp": this.radioOperation == 'IMPORT' ? '' : currentEvent.querySelector("#scriptServerIp").value,
        "ciqServerIp": this.radioOperation == 'IMPORT' ? '' : currentEvent.querySelector("#ciqServerIp").value,
        "ciqNetworkConfigId":this.tableData.ciqNetworkConfigId,
        "scriptNetworkConfigId":this.tableData.scriptNetworkConfigId,
        "ciqServer": this.radioOperation == 'IMPORT' ? '' : currentEvent.querySelector("#ciqServer").value,
    };
    let allowDuplicate = false;
    validator.performValidation(event, this.validationData, "save_update");
    if(!this.ovCkeckedOrNot)
    {
    setTimeout(() => {
        if (this.isValidForm(event)) {
            this.showLoader = true;
            this.ciquploadService.fetchCiqDetails(fetchFormDetails,allowDuplicate, this.sharedService.createServiceToken(),activation,this.radioOperation, this.importSelectedSites)
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
                                this.showLoader = false;                                  
                                if (jsonStatue.status == "CONFIRM") {
                                this.modalService.open(confirmFetchModal,{keyboard: false, backdrop: 'static', size:'lg', windowClass:'confirm-modal'}).result.then((result) => {
                                    this.closeResult = `Closed with: ${result}`;
                                  }, (reason) => {
                                    // UploadFileStatus *******************************************
                                    //this.filePostRef.nativeElement.value = "";
                                    //*********************************************
                                      this.showLoader = false;
                                      this.confirmFetchCiqDeatailsFile(event,activation);
                                  
                                  });


                                } else if (jsonStatue.status == "SUCCESS") {
                                    this.showLoader = false;
                                    $("#remarks").val("");
                                    this.ckeckedOrNot = "";
                                    this.displayModel(jsonStatue.reason,"successIcon", jsonStatue.infoLog);
                                    this.getUploadDetails();
                                    
                                } else {
                                    this.showLoader = false;
                                    this.displayModel(jsonStatue.reason, "failureIcon");                                    
                                    $("#remarks").val("");
                                    this.ckeckedOrNot = "";
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
                                "reason": "Fetched successfully !",
                                "status": "SUCCESS",
                                "infoLog": "This is a information message for this reason.",
                                // "status": "CONFIRM",
                                "serviceToken": "64438"
                            };

                            if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {

                            this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, {
                                keyboard: false,
                                backdrop: 'static',
                                size: 'lg',
                                windowClass: 'session-modal'
                            });
                        } else {
                            this.showLoader = false;                                  
                            if (jsonStatue.status == "CONFIRM") {
                            this.modalService.open(confirmFetchModal,{keyboard: false, backdrop: 'static', size:'lg', windowClass:'confirm-modal'}).result.then((result) => {
                                this.closeResult = `Closed with: ${result}`;
                              }, (reason) => {
                                  this.showLoader = false;
                                  this.confirmFetchCiqDeatailsFile(event,activation);
                              
                              });


                            } else if (jsonStatue.status == "SUCCESS") {
                                this.showLoader = false;
                                $("#remarks").val("");
                                this.ckeckedOrNot = "";
                                
                                //*********************************************
                                this.displayModel(jsonStatue.reason,"successIcon", jsonStatue.infoLog);
                                this.getUploadDetails();
                                
                            } else {
                                this.showLoader = false;
                                this.displayModel(jsonStatue.reason, "failureIcon");
                                $("#remarks").val("");
                                this.ckeckedOrNot = "";
                            }
                        }

                        }, 100); */

                        //Please Comment while checkIn   
                    });
        }
    }, 0);

    }
    else if( this.ovCkeckedOrNot )
    {
            setTimeout(() => {
              // this.sharedService.userNavigation = true; //un block user navigation
              if (this.isValidForm(event)) {
              this.showLoader = true;
              this.ciquploadService.forceFetch(fetchFormDetails,allowDuplicate, this.sharedService.createServiceToken(),activation)
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
                         // this.getOvDetails();
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
                    }, 1000); */
                    //Please Comment while checkIn
                  });
                }
            }, 0);  
    }
       
} 
resetData(event){
    this.ovCkeckedOrNot = false;
}

uplaodCiqDeatailsFile(event, confirmModal,activation) {
    if(this.ovCkeckedOrNot){
    this.validationData.rules.uploadScript.required = true; 
    }
    else{
    this.validationData.rules.uploadScript.required = false; 
    }



    this.remarks = event.target.parentNode.parentNode.parentNode.querySelector("#remarks").value;
    /* this.networkType = event.target.parentNode.parentNode.parentNode.querySelector("#networkType").value;
    this.networkTypeId = $("#networkType option:selected")[0].id;                
    this.lsmVersion = event.target.parentNode.parentNode.parentNode.querySelector("#lsmVersion").value; */
    const formdata = new FormData();
    let ciqFiles: FileList = this.filePostRef.nativeElement.files;
    let checkListFile: FileList = this.fileCheckListRef.nativeElement.files;
    let scriptFile : FileList = this.fileScriptRef.nativeElement.files;
    let scriptfiles : String = "";
    let filenames = [];
    for (var i = 0; i < ciqFiles.length; i++) {
        formdata.append( "CIQ", ciqFiles[i]);
        //formdata.append(ciqFiles[i].name, ciqFiles[i]);
        filenames.push(ciqFiles[i].name);
    }

    for (var i = 0; i < checkListFile.length; i++) {
        formdata.append( "CHECKLIST", checkListFile[i]);
        //formdata.append(checkListFile[i].name, checkListFile[i]);
        filenames.push(checkListFile[i].name);
    }

    for (var i = 0; i < scriptFile.length; i++) {
        formdata.append( "SCRIPTFILE", scriptFile[i]);
        //formdata.append(scriptFile[i].name, scriptFile[i]);
        filenames.push(scriptFile[i].name);

        scriptfiles = scriptfiles + "," + scriptFile[i].name.substring(0,scriptFile[i].name.lastIndexOf("."));
      //  scriptfiles.push(scriptFile[i].name.substring(0,scriptFile[i].name.length-4));
    }
    scriptfiles =scriptfiles.substring(1,scriptfiles.length)
    let allowDuplicate = false;
    validator.performValidation(event, this.validationData, "save_update");
    setTimeout(() => {
        if (this.isValidForm(event)) {
            this.showLoader = true;
            // Just to keep server awake, calling this api on every 45 seconds
            this.programListCallInterval = setInterval(() => {
                this.getProgramList();
            }, 45000);
            this.ciquploadService.uplaodFile(this.remarks, formdata, filenames.toString(), allowDuplicate, this.sharedService.createServiceToken(), this.timezone,activation,this.ovCkeckedOrNot,scriptfiles)
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
                                this.showLoader = false;                                  
                                if (jsonStatue.status == "CONFIRM") {
                                this.modalService.open(confirmModal,{keyboard: false, backdrop: 'static', size:'lg', windowClass:'confirm-modal'}).result.then((result) => {
                                    this.closeResult = `Closed with: ${result}`;
                                  }, (reason) => {
                                    // UploadFileStatus *******************************************
                                    //this.filePostRef.nativeElement.value = "";
                                    //*********************************************
                                      this.showLoader = false;
                                      this.confirmUplaodCiqDeatailsFile(activation);
                                  
                                  });


                                } else if (jsonStatue.status == "SUCCESS") {
                                    this.showLoader = false;
                                    clearInterval( this.programListCallInterval );
                                    this.programListCallInterval=null;

                                    this.filePostRef.nativeElement.value = "";
                                    this.fileCheckListRef.nativeElement.value = "";
                                    this.fileScriptRef.nativeElement.value = "";
                                    $("#remarks").val("");
                                    /* $("#networkType").val("");
                                    $("#lsmVersion").val(""); */
                                    //*********************************************
                                    this.displayModel("File Uploaded successfully !","successIcon");
                                    this.getUploadDetails();
                                    
                                } else {
                                    this.showLoader = false;
                                    clearInterval( this.programListCallInterval );
                                    this.programListCallInterval=null;
                                    this.displayModel(jsonStatue.reason, "failureIcon");
                                    this.filePostRef.nativeElement.value = "";
                                    this.fileCheckListRef.nativeElement.value = "";
                                    this.fileScriptRef.nativeElement.value = "";
                                    $("#remarks").val("");
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

                            if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {

                            this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, {
                                keyboard: false,
                                backdrop: 'static',
                                size: 'lg',
                                windowClass: 'session-modal'
                            });
                        } else {

                                this.showLoader = false;                                  
                                if (jsonStatue.status == "CONFIRM") {
                                this.modalService.open(confirmModal,{keyboard: false, backdrop: 'static', size:'lg', windowClass:'confirm-modal'}).result.then((result) => {
                                    this.closeResult = `Closed with: ${result}`;
                                  }, (reason) => {
                                    // UploadFileStatus *******************************************
                                    //this.filePostRef.nativeElement.value = "";
                                    //*********************************************
                                      this.showLoader = false;
                                      this.confirmUplaodCiqDeatailsFile(activation);
                                  
                                  });


                                } else if (jsonStatue.status == "SUCCESS") {
                                    this.showLoader = false;
                                    clearInterval( this.programListCallInterval );
                                    this.programListCallInterval=null;
                                    this.filePostRef.nativeElement.value = "";
                                    this.fileCheckListRef.nativeElement.value = "";
                                    this.fileScriptRef.nativeElement.value = "";
                                    $("#remarks").val("");
                                    
                                    //*********************************************
                                    this.displayModel("File Uploaded successfully !","successIcon");
                                    this.getUploadDetails();
                                    
                                } else {
                                    this.showLoader = false;
                                    clearInterval( this.programListCallInterval );
                                    this.programListCallInterval=null;
                                    this.displayModel(jsonStatue.reason, "failureIcon");
                                    this.filePostRef.nativeElement.value = "";
                                    this.fileCheckListRef.nativeElement.value = "";
                                    this.fileScriptRef.nativeElement.value = "";
                                    $("#remarks").val("");
                                }
                        }

                        }, 100); */

                        //Please Comment while checkIn   
                    });
        }
    }, 0);

}

/**
 * Just to keep server awake, calling this api on every 45 seconds
 */
getProgramList() {
    this.sharedService.getProgramList()
        .subscribe(
            data => {
              setTimeout(() => {
                let jsonStatue = data.json();
                    if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {

                      if (jsonStatue.status == "SUCCESS") {
                        
                        } else {
                            // this.displayModel(jsonStatue.status, "failureIcon");
                        }
                    }
                }, 100);
            },
            error => {
                //Please Comment while checkIn

                /* setTimeout(() => {
                    let jsonStatue = JSON.parse('{"sessionId":"9a349dc","serviceToken":"70206","status":"SUCCESS"}');

                    if (jsonStatue.status == "SUCCESS") {
                        console.log("getProgramList() : Called Successful, just to keep server awake");
                    } else {
                        console.log("getProgramList() : Called Failed, just to keep server awake");
                        // this.displayModel(jsonStatue.status, "failureIcon");
                    }
                }, 100); */

                //Please Comment while checkIn   
            });
}

  onChangeNetworkType(networkType){
    this.lsmVersionList = [];
    let lsmVersionList = [];
    $.each(this.netWorkDetails, function (key, value) {      
      if(value.networkType == networkType){
        lsmVersionList = value.lsmVersionList;
      }
    });
    this.lsmVersionList = lsmVersionList;
    
  }

  /*
     * Used to validate the file
     * @param : message
     * @retun : null
     */
    uploadStateTar(event,fieldName) {        
        let files: FileList = event.target.files,
            invalidFilenames = [];
        for (var i = 0; i < files.length; i++) {
            //.tgz, .tar.gz, .zip
            if (files[i].name.indexOf('.xlsx') >= 0) {
                if (fieldName == 'CIQ') {
                    this.ciqFileType = false;
                } else if (fieldName = 'CHECKLIST') {
                    this.listFileType = false;
                }
            } else {
                // invalidFilenames.push(files[i].name);   
                if (fieldName == 'CIQ') {
                    this.ciqFileType = true;
                } else if (fieldName = 'CHECKLIST') {
                    this.listFileType = true;
                }
            }
        }
            
           /*  if(invalidFilenames.length > 0){  
                $(event.target).parents("form").find("#upload").attr("disabled", "disabled").addClass("buttonDisabledCreate");
                $(event.target).parents("form").find("#uploadAndActivate").attr("disabled", "disabled").addClass("buttonDisabledCreate");
                $(event.target).parents("form").find(".dbImportButtonDisabled").removeClass("displayNone");
            }else{
                $(event.target).parents("form").find("#upload").removeAttr("disabled").removeClass("buttonDisabledCreate");
                $(event.target).parents("form").find("#uploadAndActivate").removeAttr("disabled").removeClass("buttonDisabledCreate");
                $(event.target).parents("form").find(".dbImportButtonDisabled").addClass("displayNone");
            }
         */
    };
    uploadZipAndTar(event) {
        let files: FileList = event.target.files,
            invalidFilenames = [];
            for (var i = 0; i < files.length; i++) {
                //.tgz, .tar.gz, .zip
                if(files[i].name.indexOf('.zip') >= 0 || files[i].name.indexOf('.7z') >= 0 || files[i].name.indexOf('.txt') >= 0){
                    this.scriptFileType = false; 
                }else{
                    invalidFilenames.push(files[i].name);   
                    this.scriptFileType = true;  
                }
            }
            
           /*  if(invalidFilenames.length > 0){
                $(event.target).parents("form").find("#upload").attr("disabled", "disabled").addClass("buttonDisabledCreate");
                $(event.target).parents("form").find("#uploadAndActivate").attr("disabled", "disabled").addClass("buttonDisabledCreate");
                $(event.target).parents("form").find(".dbImportButtonDisabled").removeClass("displayNone");
            }else{
                $(event.target).parents("form").find("#upload").removeAttr("disabled").removeClass("buttonDisabledCreate");
                $(event.target).parents("form").find("#uploadAndActivate").removeAttr("disabled").removeClass("buttonDisabledCreate");
                $(event.target).parents("form").find(".dbImportButtonDisabled").addClass("displayNone");
            } */
        
    };

    confirmUplaodCiqDeatailsFile(activation) {
        
        const formdata = new FormData();
        let ciqFiles: FileList = this.filePostRef.nativeElement.files;
    let checkListFile: FileList = this.fileCheckListRef.nativeElement.files;
    let scriptFile : FileList = this.fileScriptRef.nativeElement.files;
    let scriptfiles : String = "";
    let filenames = [];
    for (var i = 0; i < ciqFiles.length; i++) {
        formdata.append( "CIQ", ciqFiles[i]);
        //formdata.append(ciqFiles[i].name, ciqFiles[i]);
        filenames.push(ciqFiles[i].name);
    }

    for (var i = 0; i < checkListFile.length; i++) {
        formdata.append( "CHECKLIST ", checkListFile[i]);
        //formdata.append(checkListFile[i].name, checkListFile[i]);
        filenames.push(checkListFile[i].name);
    }

    for (var i = 0; i < scriptFile.length; i++) {
        formdata.append( "SCRIPTFILE", scriptFile[i]);
        //formdata.append(scriptFile[i].name, scriptFile[i]);
        filenames.push(scriptFile[i].name);

        scriptfiles = scriptfiles + "," + scriptFile[i].name.substring(0,scriptFile[i].name.lastIndexOf("."));
       // scriptfiles.push(scriptFile[i].name.substring(0,scriptFile[i].name.length-4));
    }
    scriptfiles = scriptfiles.substring(1,scriptfiles.length)
        let allowDuplicate = true;
        setTimeout(() => {
                this.showLoader = true;
                this.ciquploadService.uplaodFile(this.remarks, formdata, filenames.toString(), allowDuplicate, this.sharedService.createServiceToken(), this.timezone, activation,this.ovCkeckedOrNot,scriptfiles)
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
                                        clearInterval( this.programListCallInterval );
                                        this.programListCallInterval=null;
                                        this.filePostRef.nativeElement.value = "";
                                        this.fileCheckListRef.nativeElement.value = "";
                                        this.fileScriptRef.nativeElement.value = "";
                                        $("#remarks").val("");
                                        /* $("#networkType").val("");
                                        $("#lsmVersion").val(""); */
                                        //*********************************************
                                        this.displayModel("File Uploaded successfully !","successIcon");
                                        this.getUploadDetails();
                                       
                                        
                                    } else {
                                        this.showLoader = false;
                                        clearInterval( this.programListCallInterval );
                                        this.programListCallInterval=null;
                                        this.displayModel(jsonStatue.reason, "failureIcon");
                                        this.filePostRef.nativeElement.value = "";
                                        this.fileCheckListRef.nativeElement.value = "";
                                        this.fileScriptRef.nativeElement.value = "";
                                        $("#remarks").val("");
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
                                    
                                    "serviceToken": "64438"
                                };

                                if (jsonStatue.status == "SUCCESS") {
                                    this.showLoader = false;
                                    clearInterval( this.programListCallInterval );
                                    this.programListCallInterval=null;
                                    // UploadFileStatus *******************************************
                                    this.filePostRef.nativeElement.value = "";
                                    this.fileCheckListRef.nativeElement.value = "";
                                    this.fileScriptRef.nativeElement.value = "";
                                    //*********************************************
                                    this.displayModel("File Uploaded successfully !" ,"successIcon");
                                    this.getUploadDetails();
                                    // this.uploadCiqFilesStatus();
                                    //this.subscription = this.timer.subscribe(data => {
                                      //  this.uploadCiqFilesStatus();
                                    //});

                                } else {

                                    this.showLoader = false;
                                    clearInterval( this.programListCallInterval );
                                    this.programListCallInterval=null;
                                    this.displayModel(jsonStatue.reason, "failureIcon");
                                    this.filePostRef.nativeElement.value = "";
                                    this.fileCheckListRef.nativeElement.value = "";
                                    this.fileScriptRef.nativeElement.value = "";
                                }

                            }, 100); */

                            //Please Comment while checkIn   
                        });
            
        }, 0);

    }

    confirmFetchCiqDeatailsFile(event,activation) {
        let currentEvent = event.target.parentNode.parentNode.parentNode,
    fetchFormDetails = {
        "remarks":currentEvent.querySelector("#remarks").value,
        "checkList":this.ckeckedOrNot,
        "scriptServer":currentEvent.querySelector("#scriptServer").value,
        "scriptServerIp":currentEvent.querySelector("#scriptServerIp").value,
        "ciqServerIp":currentEvent.querySelector("#ciqServerIp").value,
        "ciqNetworkConfigId":this.tableData.ciqNetworkConfigId,
        "scriptNetworkConfigId":this.tableData.scriptNetworkConfigId,
        "ciqServer":currentEvent.querySelector("#ciqServer").value,
    };
        let allowDuplicate = true;
        setTimeout(() => {
                this.showLoader = true;
                this.ciquploadService.fetchCiqDetails(fetchFormDetails,allowDuplicate, this.sharedService.createServiceToken(),activation,this.radioOperation, this.importSelectedSites)
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
                                        $("#remarks").val("");
                                        this.displayModel("File Uploaded successfully !","successIcon");
                                        this.getUploadDetails();
                                       
                                        
                                    } else {
                                        this.showLoader = false;
                                        this.displayModel(jsonStatue.reason, "failureIcon");
                                        this.filePostRef.nativeElement.value = "";
                                        this.fileCheckListRef.nativeElement.value = "";
                                        this.fileScriptRef.nativeElement.value = "";
                                        $("#remarks").val("");
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
                                    
                                    "serviceToken": "64438"
                                };

                                if (jsonStatue.status == "SUCCESS") {
                                    this.showLoader = false;
                                    this.displayModel("File Uploaded successfully !" ,"successIcon");
                                    this.getUploadDetails();

                                } else {

                                    this.showLoader = false;
                                    this.displayModel(jsonStatue.reason, "failureIcon");
                                }

                            }, 100); */

                            //Please Comment while checkIn   
                        });
            
        }, 0);

    }

    

    cancelModel(c){
        this.filePostRef ? this.filePostRef.nativeElement.value = "" : "";
        this.fileCheckListRef ? this.fileCheckListRef.nativeElement.value = "" : "";
        this.fileScriptRef ? this.fileScriptRef.nativeElement.value = "" : "";
        c('Close click');
    }
    
   
 

    /* validates current submitted form is valid and free from errors
     * @param : pass the event
     * @retun : boolean
     */

    isValidForm(event) {
        return ($(event.target).parents("form").find(".error-border").length == 0) ? true : false;
    }

 
 
 /*
   * on click of edit row create a blueprint and append next to the current row
   * @param : current row event , current row json object and row index
   * @retun : null
   */

  editRow(event, key, index) {

//    this.cancelCreateNew(event);

    let editState : any = event.target,
        parentForm : any,
        location : any,       
        locationPrev : any,
        airlineName : any;
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
        //validator.performValidation(event, this.validationData, "edit");

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
      this.scriptFileNameList = key.scriptFileName.split(",");
      this.deleteScriptFileList = [];
    }
/*
   * On click of update button in edit then send data to server and close the block
   * @param : null
   * @retun : null
   */

  updateEditRow(index, key, event){
    //validator.performValidation(event, this.validationData, "save_update");

    const formdata = new FormData();    
    let checkListFile: FileList = this.editCheckListFileRef.nativeElement.files;
    let scriptFile : FileList = this.editScriptFileRef.nativeElement.files;
    let filenames = [];   
    for (var i = 0; i < checkListFile.length; i++) {
        formdata.append( "CHECKLIST", checkListFile[i]);        
        filenames.push(checkListFile[i].name);
    }

    for (var i = 0; i < scriptFile.length; i++) {
        formdata.append( "SCRIPTFILE", scriptFile[i]);        
        filenames.push(scriptFile[i].name);
    }

    setTimeout(() => {
      if(this.isValidForm(event)){
        this.showLoader = true;
        let currentEditedForm = event.target.parentNode.parentNode,
             ciqDetails = {
                  "id": key.id,
                  "ciqFilePath":key.ciqFilePath,
                  "scriptFilePath":key.scriptFilePath,
                  "checklistFilePath":key.checklistFilePath,
                  "ciqFileName": currentEditedForm.querySelector("#ciqFileName").value,
                  "scriptFileName": currentEditedForm.querySelector("#scriptFileName").value,
                  "checklistFileName": currentEditedForm.querySelector("#checklistFileName").value,
                  "uploadBy": currentEditedForm.querySelector("#uploadBy").value,
                  "fileSourceType": currentEditedForm.querySelector("#fileSourceType").value,
                  "remarks": currentEditedForm.querySelector("#remarks").value,
                  "creationDate": currentEditedForm.querySelector("#creationDate").value,
                  "ciqVersion": currentEditedForm.querySelector("#ciqVersion").value,
                  "programDetailsEntity":key.programDetailsEntity
             };
        
        this.ciquploadService.updateCiqDetails(ciqDetails,key, this.sharedService.createServiceToken(),formdata, this.deleteScriptFileList.join(","))
        .subscribe(
            data => {
                let jsonStatue = data.json();
                this.showLoader = false;

                if(jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User"){
            
                  this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
            
                } else {
            
                  if( this.sharedService.isvalidServiceToken( parseInt(data.json().serviceToken,10) ) || data.json().sessionId == "408"){               
                      if(jsonStatue.status == "SUCCESS"){
                         this.message = "CIQ details is updated successfully !";
                         this.successModalBlock = this.modalService.open(this.successModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'success-modal'});

                      //  this.displayModel("User details updated successfully !","successIcon");
                         this.cancelEditRow(ciqDetails.id, '');
                       // this.createForm = false;
                      } else {
                        this.displayModel(jsonStatue.reason,"failureIcon");  
                      }
                  }   
                }
                
            },  
            error => {
              //Please Comment while checkIn
           /*  let jsonStatue: any = {"sessionId":"506db794","reason":"Updation Failed","status":"SUCCESS","serviceToken":"63524"};
             setTimeout(() => {
                this.showLoader = false;
                if(jsonStatue.status == "SUCCESS"){
                 this.message = "CIQ details is updated successfully !";
                this.successModalBlock = this.modalService.open(this.successModalRef,{keyboard: false, backdrop: "static",size:'lg',windowClass:"success-modal"});
                 // this.displayModel("User details updated successfully !","successIcon");
                  //this.createForm = false;
                  this.cancelEditRow(ciqDetails.id, '');
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
   * On click delete row open a modal for confirmation
   * @param : content, userName
   * @retun : null
   */

    deleteRow(confirmModal,key,event) {
        if (event.target.className != "deleteRowDisabled") {
      this.modalService.open(confirmModal,{keyboard: false, backdrop: 'static', size:'lg', windowClass:'confirm-modal'}).result.then((result) => {
        this.closeResult = `Closed with: ${result}`;
      }, (reason) => {
        this.showLoader = true;

        this.ciquploadService.deleteUserDetails(key, this.sharedService.createServiceToken())
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
                               this.message = "CIQ details deleted successfully!";
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
                  /* setTimeout(() => {
                      this.showLoader = false;
                     let jsonStatue = {"reason":"CIQ details deleted failed","sessionId":"5f3732a4","serviceToken":"80356","status":"FAILED"};
                    if(jsonStatue.status == "SUCCESS"){
                      this.message = "CIQ details deleted successfully!";
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
                "count": parseInt(this.pageSize),
                "page": parseInt(page)
            };

            this.paginationDetails = paginationDetails;
            this.pager = this.getPager(this.totalPages*this.pageSize, this.currentPage, this.pageRenge);
            this.paginationDisabbled = false;
            // Hide all the form/Table/Nodatafound5
            this.tableShowHide = false;
            this.showNoDataFound = false;
            this.getUploadDetails();


        }, 0);



    };

 onChangeTableRowLength(event) {
        this.showLoader = true;
        this.pageSize = event.target.value;

        this.currentPage = 1;

        let paginationDetails = {
            "count": parseInt(this.pageSize),
            "page": parseInt(this.currentPage)
        };

        this.paginationDetails = paginationDetails;
        this.paginationDisabbled = false;
        // Hide all the form/Table/Nodatafound5
        this.tableShowHide = false;
        this.showNoDataFound = false;
        setTimeout(() => {
            this.showLoader = false;
            $("#dataWrapper").find(".scrollBody").scrollLeft(0);
            this.getUploadDetails();
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
  displayModel(message:string, messageType:string, infoLog:string = ""){
    this.messageType = messageType;
    this.showModelMessage = true;
    this.modelData = {
      "message" : message,
      "infoLog" : infoLog,
      "modelType" : messageType
    };

    setTimeout(() => {
      this.showModelMessage = false;
      
    }, 10);
  }
  /*
   * on click of edit row enable corresponding
   * @param : current row index
   * @retun : boolean
   */

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
      this.ngOnInit();
  }
  closeModelFetchInfo() {
      this.fetchInfoBlock.close();
      this.fetchInfoMsg = "";
  }
  closeErrorPage(){
      $("header,.page-wrapper,#footerWrapper").removeClass('displayNone');
      $("#errorPage").addClass('displayNone');
  }
   serverError(error) {
    if(error.status == 500 || error.status == 0){
        $("header,.page-wrapper,#footerWrapper").addClass('displayNone');
        $("#errorPage").removeClass('displayNone');
    }
  }

  downLoadFile(fileName, filePath, ciqFilePath = "") {

    this.ciquploadService.downloadFile(fileName,filePath,this.sharedService.createServiceToken())
    .subscribe(
        data => {
            let blob = new Blob([data["_body"]], {
                type: "application/octet-stream"
            });

            let newFileName = "";

            if(fileName.split(",").length > 1) {
                newFileName = ciqFilePath + "_RF_Scripts.zip"; 
            }
            else {
                newFileName = fileName;
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

            if (fileName.split(",").length > 1) {
                newFileName = ciqFilePath + "_RF_Scripts.zip";
            }
            else {
                newFileName = fileName;
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

  changeSorting(predicate, event, index){
    this.sharedService.dynamicSort(predicate, event, index, this.tableData.ciqUploadAuditTrailDetModels);
  }

  ngOnDestroy() {
    this.programChangeSubscription.unsubscribe();
        if (this.subscription != undefined) {
            this.subscription.unsubscribe();
        }   
    // avoid memory leaks here by cleaning up after ourselves. If we
    
    // don't then we will continue to run our ngOnInit()
    
    // method on every navigationEnd event.
    
    if (this.navigationSubscription) {
    
    this.navigationSubscription.unsubscribe();
    
    }
    
    }
    setMenuHighlight(selectedElement) {
        this.searchTabRef.nativeElement.id = (selectedElement == "searchCiq") ? "activeTab" : "inactiveTab";
        this.retrieveTabRef.nativeElement.id = (selectedElement == "retrieveCiq") ? "activeTab" : "inactiveTab";
    }

    validateUploadFile(event,fieldName) {        
        let files: FileList = event.target.files;              
            
        for (var i = 0; i < files.length; i++) {
            if(fieldName == 'SCRIPT'){
                if(files[i].name.indexOf('.zip') >= 0 || files[i].name.indexOf('.7z') >= 0 || files[i].name.indexOf('.txt') >= 0){
                    this.editScriptType = false;  
                }else{               
                    this.editScriptType = true;  
                }
            }else if (fieldName == 'CHECKLIST'){
                if(files[i].name.indexOf('.xlsx') >= 0){
                    this.editListType = false;  
                }else{               
                    this.editListType = true;  
                }
            }
          
        }
          
    }
    viewScripts(content) {
        // this.scriptList = this.rfScriptList.split("\n");
        this.rfScriptListModel = this.rfScriptList;
        this.scriptListModalBlock = this.modalService.open(content, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView' });
    }
    updateScriptList() {
        this.rfScriptList = this.rfScriptListModel;
        this.closeModelScriptList();
    }
    closeModelScriptList() {
        this.scriptListModalBlock.close();
    }

    viewFetchInfo(fetchInfoModal, fetchInfo) {
        this.fetchInfoMsg = fetchInfo;
        this.fetchInfoBlock = this.modalService.open(fetchInfoModal, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView' });
    }
    
    getNEIDProgramBased() {
        let programName = JSON.parse(sessionStorage.getItem("selectedProgram")).programName;
        let neIdList = [];
        switch(programName) {
            case "VZN-5G-MM":
                neIdList = ["8903500097", "8903500093", "9906500024"];
                break;
          case "VZN-5G-DSS":
                neIdList = ["90022163", "90022239", "90022223"];
                break;
            case "VZN-5G-CBAND":
                neIdList = ["6895513542", "6895513170", "6895523772"];
                break;
            default:
                neIdList = ["61192", "61452", "73461"];
                break;
      }
      return neIdList;

    }
    
    iserrorFetchInfo(fetchinfo) {
        let errorCases = ['not found', 'fail'];
        let fetchInfoString = fetchinfo ? fetchinfo.toLowerCase() : "";
        let retClass = "successheighlighted";
        for(let index = 0; index < errorCases.length; index++) {
            if (fetchInfoString.indexOf(errorCases[index]) >= 0) {
                retClass = "errorheighlighted";
                break;
            }
        }
        return retClass;
    }
}
