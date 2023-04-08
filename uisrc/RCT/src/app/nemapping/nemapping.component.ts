import { ViewContainerRef, Input, ElementRef, Renderer, Component, OnInit, ViewChild, HostListener, SystemJsNgModuleLoader } from '@angular/core';
// import { Router} from '@angular/router';
import { Router, NavigationEnd } from '@angular/router';
import { NgbModal, ModalDismissReasons } from '@ng-bootstrap/ng-bootstrap';
import { NemappingService } from '../services/nemapping.service';
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
  selector: 'app-nemapping',
  templateUrl: './nemapping.component.html',
  styleUrls: ['./nemapping.component.scss']
  //providers: [NemappingService]
})
export class NemappingComponent implements OnInit {

  paginationDisabbled: boolean = false;
  NeName:any;
  enbSbIp:any;
  enbSbVlan:any;
  btsIp:any;
  enbOamIp:any;
  enbVlanId:any;
  btsId:any;
  bsmIp:any;
  enbId:any;
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
  showNoDataFound: boolean;
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
  neMarket:any;
  neConfigTypeList:any;
  searchBlock: boolean = false;
  neConfigBlock: boolean = false;
  searchCriteria: any;
  selNeName:any;
  selsiteConfigType:any;
  eNBDisable :any;
  currentProgramName: any;
  programName: any;
  neConfigType="";
  max = new Date();
  programChangeSubscription:any;
  networkType: any = "";

  validationData: any = {
    "rules": {
      "neMarket": {
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
      },
      "enbSbIp":{
        "required":false
      },
      "enbSbVlan":{
        "required":false
      },
      "btsIp":{
        "required":false
      },
      "enbOamIp":{
        "required":false
      },
      "enbVlanId":{
        "required":false
      },
      "btsId":{
        "required":false
      },
      "bsmIp":{
        "required":false
      }

    },
    "messages": {
      "neMarket": {
        "required": "Please select Market"
      },
      "neVersion": {
        "required": "Please select NE Version"
      },
      "neName": {
        "required": "Please select NE Name"
      },
      "siteConfigType": {
        "required": "Please select NE Config Type"
      },
      "enbSbIp":{
        "required": "Please enter eNB SB IP"
      },
      "enbSbVlan":{
        "required": "Please enter eNB SB VLAN"
      },
      "btsIp":{
        "required": "Please enter BTS IP"
      },
      "enbOamIp":{
        "required": "Please enter eNB OAM IP"
      },
      "enbVlanId":{
        "required": "Please enter eNB VLAN ID"
      },
      "btsId":{
        "required": "Please enter BTS ID"
      },
      "bsmIp":{
        "required": "Please enter BSM IP"
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
    private nemappingService: NemappingService,
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
          // Clear Session storage for selectedCIQ on new program selection
          this.sharedService.updateSelectedCIQInSessionStorage(null);
          this.loadInitialData();
      }
    });
  }

  loadInitialData(){
    let searchCrtra = { "enbId": "", "siteConfigType": "", "searchStartDate": "", "searchEndDate": "" };
    this.searchCriteria = searchCrtra;
    this.neConfigBlock =true;
    this.searchBlock = false;
    this.searchStatus = 'load';
    this.currentPage = 1;
    this.totalPages = 1;
    this.TableRowLength = 10;
    this.pageSize = 10;
    this.editableFormArray = [];
    this.selNeMarket="";
    // this.neMarket="";
    // this.neVersion="";
    // this.enbId=[];
    // this.getCiqList="";
    let paginationDetails = {
      "count": parseInt(this.TableRowLength, 10),
      "page": this.currentPage
    };
    this.paginationDetails = paginationDetails;
    this.programName = JSON.parse(sessionStorage.getItem("selectedProgram")).programName;
    this.bluePrintForm.nativeElement.reset();
    this.showLoader = true;
    //this.getNeslist=[];
    this.getNeMappingDetails();
    //this.getCiqListData(this.fromDate,this.toDate);

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
  clearSearchFrom() {
    this.bluePrintForm.nativeElement.reset();  
}
  getNeMappingDetails() {

    this.tableShowHide = false;

    this.showLoader = true;
    $("#dataWrapper").find(".scrollBody").scrollLeft(0);
    this.nemappingService.getNeMappingDetails(this.searchStatus,this.searchCriteria, this.sharedService.createServiceToken(), this.paginationDetails)
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
                  this.tableData = jsonStatue;
                  this.totalPages = this.tableData.pageCount;
                  this.dropDownList=this.tableData.dropDownList;
                  this.marketList=Object.keys(this.tableData.dropDownList);
                  this.neConfigTypeList = this.tableData.neConfigTypeList;

                  this.networkType = JSON.parse(sessionStorage.getItem("selectedProgram")).networkTypeDetailsEntity.networkType; //this.tableData.neMappingDetails[0].programDetailsEntity.networkTypeDetailsEntity.networkType;
                  if(this.networkType == '5G' ) {
                    this.validationData.rules.siteConfigType["required"] = false;
                  }
                  else {
                    this.validationData.rules.siteConfigType["required"] = true;
                  }
                  /* for (i = 0; i < this.tableData.neConfigTypeList.length; i++) {
                    if (this.tableData.neConfigTypeList[i] == "NB-IoT No")
                      this.neConfigType = "NB-IoT No";
                  } */
                  /* this.searchStartDate = new Date(this.tableData.searchStartDate);
                  this.searchEndDate = new Date(this.tableData.searchEndDate); */

                  let pageCount = [];
                  for (var i = 1; i <= this.tableData.pageCount; i++) {
                    pageCount.push(i);
                  }
                  this.pageRenge = pageCount;
                  this.pager = this.getPager(this.totalPages * this.pageSize, this.currentPage, this.pageRenge);

                  if (this.tableData.neMappingDetails.length == 0) {
                    this.tableShowHide = false;
                    this.noDataVisibility = true;
                  } else {

                    this.tableShowHide = true;
                    this.noDataVisibility = false;
                    setTimeout(() => {
                      let tableWidth = document.getElementById('neMappingDetails').scrollWidth;
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

          }, 1000);
        },
        error => {
          //Please Comment while checkIn
          /* setTimeout(() => {
            this.showLoader = false;
            // this.tableData ={"sessionId":"a4d70abd","serviceToken":"61006","status":"SUCCESS","pageCount":1,"searchEndDate":"03/04/2019","searchStartDate":"02/27/2019","programGenerateFileDetails":[{"id":1,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"env":"Active","csv":"Active","commissionScript":"Active"}],"csvAuditTrailDetModels":[{"id":1,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"fileName":"UNY-NE-VZ_CIQ_Ver2.82_0128201920195228075215.txt","filePath":"/home/user/RCT/rctsoftware/SMART/Customer/23/PreMigration/Output/UNY-NE-VZ_CIQ_Ver2.82_01282019/ENV/","fileType":"Commissioning Script","ciqFileName":"UNY-NE-VZ_CIQ_Ver2.82_01282019.xlsx","neName":"056001_BOSTON_CONVENTION_CTR_DAS2","generatedBy":"superadmin","generationDate":"2019-02-28 19:52:15","searchStartDate":null,"searchEndDate":null,"remarks":"qwertyui"},{"id":2,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"fileName":"UNY-NE-VZ_CIQ_Ver2.82_0128201920191801111809.txt","filePath":"/home/user/RCT/rctsoftware/SMART/Customer/23/PreMigration/Output/UNY-NE-VZ_CIQ_Ver2.82_01282019/ENV/","fileType":null,"ciqFileName":"UNY-NE-VZ_CIQ_Ver2.82_01282019.xlsx","neName":"072715_Foster_Pond_Hub_BBU2","generatedBy":"admin","generationDate":"2019-03-01 11:18:09","searchStartDate":null,"searchEndDate":null,"remarks":null},{"id":4,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"fileName":"UNY-NE-VZ_CIQ_Ver2.82_0128201920191901111934.txt","filePath":"/home/user/RCT/rctsoftware/SMART/Customer/23/PreMigration/Output/UNY-NE-VZ_CIQ_Ver2.82_01282019/ENV/","fileType":null,"ciqFileName":"UNY-NE-VZ_CIQ_Ver2.82_01282019.xlsx","neName":"072715_Foster_Pond_Hub_BBU2","generatedBy":"admin","generationDate":"2019-03-01 11:19:34","searchStartDate":null,"searchEndDate":null,"remarks":null},{"id":5,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"fileName":"UNY-NE-VZ_CIQ_Ver2.82_0128201920192001112039.txt","filePath":"/home/user/RCT/rctsoftware/SMART/Customer/23/PreMigration/Output/UNY-NE-VZ_CIQ_Ver2.82_01282019/ENV/","fileType":null,"ciqFileName":"UNY-NE-VZ_CIQ_Ver2.82_01282019.xlsx","neName":"056001_BOSTON_CONVENTION_CTR_DAS2","generatedBy":"admin","generationDate":"2019-03-01 11:20:39","searchStartDate":null,"searchEndDate":null,"remarks":null},{"id":6,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"fileName":"UNY-NE-VZ_CIQ_Ver2.82_0128201920192101112143.txt","filePath":"/home/user/RCT/rctsoftware/SMART/Customer/23/PreMigration/Output/UNY-NE-VZ_CIQ_Ver2.82_01282019/ENV/","fileType":null,"ciqFileName":"UNY-NE-VZ_CIQ_Ver2.82_01282019.xlsx","neName":"056003_MBTA_1_DH_MA","generatedBy":"admin","generationDate":"2019-03-01 11:21:43","searchStartDate":null,"searchEndDate":null,"remarks":null},{"id":7,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"fileName":"UNY-NE-VZ_CIQ_Ver2.82_0128201920192801112834.txt","filePath":"/home/user/RCT/rctsoftware/SMART/Customer/23/PreMigration/Output/UNY-NE-VZ_CIQ_Ver2.82_01282019/ENV/","fileType":null,"ciqFileName":"UNY-NE-VZ_CIQ_Ver2.82_01282019.xlsx","neName":"056003_MBTA_1_DH_MA","generatedBy":"admin","generationDate":"2019-03-01 11:28:35","searchStartDate":null,"searchEndDate":null,"remarks":null}],"ciqList":["UNY-NE-VZ_CIQ_Ver2.82_01282019.xlsx","frh"]};   
            //this.tableData= {"status":"SUCCESS","pagecount":1,"neMappingDetails":[{"id":3,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"enbId":56001,"networkConfigEntity":{"id":1,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"neMarket":null,"neTypeEntity":{"id":1,"neType":"CIQ SERVER"},"neName":"CIQ_Bala","neVersionEntity":null,"neIp":"10.20.120.50","neRsIp":null,"neUserName":"user","nePassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-02-28T10:47:48.000+0000","status":"Active","remarks":"Balasys as CIQ server","neDetails":[]},"siteConfigType":""},{"id":4,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"enbId":56002,"networkConfigEntity":null,"siteConfigType":"Test"}]};
            //this.tableData = { "pageCount": 1, "neMappingDetails": [{ "id": 3, "programDetailsEntity": { "id": 23, "networkTypeDetailsEntity": { "id": 2, "networkType": "4G", "createdBy": "superadmin", "caretedDate": "2019-02-15T07:54:21.000+0000", "status": "Active", "remarks": "", "networkColor": "#f51dc1" }, "programName": "VZN-4G-LEGACY", "programDescription": "LEGACY", "status": "Active", "creationDate": "2019-02-27T08:11:30.000+0000", "createdBy": "superadmin" }, "enbId": 56001, "networkConfigEntity": { "id": 1, "programDetailsEntity": { "id": 23, "networkTypeDetailsEntity": { "id": 2, "networkType": "4G", "createdBy": "superadmin", "caretedDate": "2019-02-15T07:54:21.000+0000", "status": "Active", "remarks": "", "networkColor": "#f51dc1" }, "programName": "VZN-4G-LEGACY", "programDescription": "LEGACY", "status": "Active", "creationDate": "2019-02-27T08:11:30.000+0000", "createdBy": "superadmin" }, "neMarket": "England", "neTypeEntity": { "id": 1, "neType": "CIQ SERVER" }, "neName": "CIQ_Bala", "neVersionEntity": { "id": 1, "programDetailsEntity": { "id": 23, "networkTypeDetailsEntity": { "id": 2, "networkType": "4G", "createdBy": "superadmin", "caretedDate": "2019-02-15T07:54:21.000+0000", "status": "Active", "remarks": "", "networkColor": "#f51dc1" }, "programName": "VZN-4G-LEGACY", "programDescription": "LEGACY", "status": "Active", "creationDate": "2019-02-27T08:11:30.000+0000", "createdBy": "superadmin" }, "neVersion": "1.2.3", "status": "Active", "createdBy": "superadmin", "creationDate": "2019-02-15T11:02:23.000+0000" }, "neIp": "10.20.120.50", "neRsIp": null, "neUserName": "user", "nePassword": "root123", "loginTypeEntity": { "id": 1, "loginType": "SSH" }, "createdBy": "superadmin", "creationDate": "2019-02-28T10:47:48.000+0000", "status": "Active", "remarks": "Bala sys as CIQ server", "neDetails": [] }, "siteConfigType": "" }, { "id": 4, "programDetailsEntity": { "id": 23, "networkTypeDetailsEntity": { "id": 2, "networkType": "4G", "createdBy": "superadmin", "caretedDate": "2019-02-15T07:54:21.000+0000", "status": "Active", "remarks": "", "networkColor": "#f51dc1" }, "programName": "VZN-4G-LEGACY", "programDescription": "LEGACY", "status": "Active", "creationDate": "2019-02-27T08:11:30.000+0000", "createdBy": "superadmin" }, "enbId": 56002, "networkConfigEntity": null, "siteConfigType": "Test" }], "status": "SUCCESS" };
            //this.tableData= {"pageCount":1,"neMappingDetails":[{"id":3,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"enbId":56001,"networkConfigEntity":{"id":1,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"neMarket":"England","neTypeEntity":{"id":1,"neType":"CIQ SERVER"},"neName":"CIQ_Bala","neVersionEntity":{"id":1,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"neVersion":"1.2.3","status":"Active","createdBy":"superadmin","creationDate":"2019-02-15T11:02:23.000+0000"},"neIp":"10.20.120.50","neRsIp":null,"neUserName":"user","nePassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-02-28T10:47:48.000+0000","status":"Active","remarks":"Bala sys as CIQ server","neDetails":[]},"siteConfigType":""},{"id":3,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"enbId":56001,"networkConfigEntity":{"id":1,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"neMarket":"England","neTypeEntity":{"id":1,"neType":"CIQ SERVER"},"neName":"CIQ_Bala","neVersionEntity":{"id":1,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"neVersion":"1.2.3","status":"Active","createdBy":"superadmin","creationDate":"2019-02-15T11:02:23.000+0000"},"neIp":"10.20.120.50","neRsIp":null,"neUserName":"user","nePassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-02-28T10:47:48.000+0000","status":"Active","remarks":"Bala sys as CIQ server","neDetails":[]},"siteConfigType":""},{"id":3,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"enbId":56001,"networkConfigEntity":{"id":1,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"neMarket":"England","neTypeEntity":{"id":1,"neType":"CIQ SERVER"},"neName":"CIQ_Bala","neVersionEntity":{"id":1,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"neVersion":"1.2.3","status":"Active","createdBy":"superadmin","creationDate":"2019-02-15T11:02:23.000+0000"},"neIp":"10.20.120.50","neRsIp":null,"neUserName":"user","nePassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-02-28T10:47:48.000+0000","status":"Active","remarks":"Bala sys as CIQ server","neDetails":[]},"siteConfigType":""}],"status":"SUCCESS"};
            //this.tableData = {"neMappingDetails":[{"id":3,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"enbId":56001,"networkConfigEntity":{"id":1,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"neMarket":"England","neTypeEntity":{"id":1,"neType":"CIQ SERVER"},"neName":"CIQ_Bala","neVersionEntity":{"id":1,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"neVersion":"1.2.3","status":"Active","createdBy":"superadmin","creationDate":"2019-02-15T11:02:23.000+0000"},"neIp":"10.20.120.50","neRsIp":null,"neUserName":"user","nePassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-02-28T10:47:48.000+0000","status":"Active","remarks":"Bala sys as CIQ server","neDetails":[]},"siteConfigType":"TDD Only"},{"id":3,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"enbId":56001,"networkConfigEntity":{"id":1,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"neMarket":"England","neTypeEntity":{"id":1,"neType":"CIQ SERVER"},"neName":"CIQ_Bala","neVersionEntity":{"id":1,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"neVersion":"1.2.3","status":"Active","createdBy":"superadmin","creationDate":"2019-02-15T11:02:23.000+0000"},"neIp":"10.20.120.50","neRsIp":null,"neUserName":"user","nePassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-02-28T10:47:48.000+0000","status":"Active","remarks":"Bala sys as CIQ server","neDetails":[]},"siteConfigType":"TDD+FDD"},{"id":3,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"enbId":56001,"networkConfigEntity":{"id":1,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"neMarket":"England","neTypeEntity":{"id":1,"neType":"CIQ SERVER"},"neName":"CIQ_Bala","neVersionEntity":{"id":1,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"neVersion":"1.2.3","status":"Active","createdBy":"superadmin","creationDate":"2019-02-15T11:02:23.000+0000"},"neIp":"10.20.120.50","neRsIp":null,"neUserName":"user","nePassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-02-28T10:47:48.000+0000","status":"Active","remarks":"Bala sys as CIQ server","neDetails":[]},"siteConfigType":"FDD Only"}],"status":"SUCCESS","pageCount":1,"dropDownList":{"New England":{"1.3.1":["CIQ_abc","CIQ_567"]},"England":{"1.2.3":["CIQ_Bala","CIQ123"]}},"neConfigTypeList":["TDD Only","TDD+FDD","FDD Only"]};
            //this.tableData ={"searchStartDate":"03/09/2019","pageCount":1,"dropDownList":{"Upstate NY":{"1.2.3":[{"id":7,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"neMarket":"Upstate NY","neTypeEntity":{"id":4,"neType":"LSM"},"neName":"SM","neVersionEntity":{"id":1,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"neVersion":"1.2.3","status":"Active","createdBy":"superadmin","creationDate":"2019-02-15T11:02:23.000+0000"},"neIp":"10.1.1.1","neRsIp":"10.1.1.2","neUserName":"user","nePassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-03-13T09:57:07.000+0000","status":"Active","remarks":"Upstate NY lsm","neDetails":[{"id":8,"step":1,"serverTypeEntity":{"id":2,"serverType":"SANE"},"serverName":"sane 1","serverIp":"10.1.1.1","serverUserName":"user","serverPassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-03-13T09:57:07.000+0000","path":"1,2,3"}]}]},"New England":{"1.2.3":[{"id":3,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"neMarket":"New England","neTypeEntity":{"id":1,"neType":"CIQ SERVER"},"neName":"ciq_naveen_sadiya_bala","neVersionEntity":{"id":1,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"neVersion":"1.2.3","status":"Active","createdBy":"superadmin","creationDate":"2019-02-15T11:02:23.000+0000"},"neIp":"10.20.120.50","neRsIp":null,"neUserName":"user","nePassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-03-12T12:07:39.000+0000","status":"Inactive","remarks":"naveen_sadiya_bala_sys","neDetails":[{"id":4,"step":1,"serverTypeEntity":{"id":1,"serverType":"JUMP BOX"},"serverName":"naveen_sys","serverIp":"10.20.120.93","serverUserName":"user","serverPassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-03-12T12:07:39.000+0000","path":""},{"id":5,"step":2,"serverTypeEntity":{"id":1,"serverType":"JUMP BOX"},"serverName":"sadiyah_sysy","serverIp":"10.20.120.12","serverUserName":"user","serverPassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-03-12T12:07:39.000+0000","path":""}]}],"1.3.1":[{"id":1,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"neMarket":"New England","neTypeEntity":{"id":1,"neType":"CIQ SERVER"},"neName":"ciq_sachin_bala","neVersionEntity":{"id":2,"programDetailsEntity":{"id":24,"networkTypeDetailsEntity":{"id":3,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"VZN-5G-VLSM","programDescription":"VLSM","status":"Active","creationDate":"2019-02-27T08:12:33.000+0000","createdBy":"superadmin"},"neVersion":"1.3.1","status":"Active","createdBy":"superadmin","creationDate":"2019-02-15T11:02:23.000+0000"},"neIp":"10.20.120.50","neRsIp":null,"neUserName":"user","nePassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-03-12T11:50:37.000+0000","status":"Inactive","remarks":"Bala sys as CIQ server","neDetails":[{"id":2,"step":1,"serverTypeEntity":{"id":1,"serverType":"JUMP BOX"},"serverName":"sachin_sys","serverIp":"10.20.120.69","serverUserName":"user","serverPassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-03-12T11:50:37.000+0000","path":""}]},{"id":2,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"neMarket":"New England","neTypeEntity":{"id":2,"neType":"SCRIPT SERVER"},"neName":"script_sachin_bala","neVersionEntity":{"id":2,"programDetailsEntity":{"id":24,"networkTypeDetailsEntity":{"id":3,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"VZN-5G-VLSM","programDescription":"VLSM","status":"Active","creationDate":"2019-02-27T08:12:33.000+0000","createdBy":"superadmin"},"neVersion":"1.3.1","status":"Active","createdBy":"superadmin","creationDate":"2019-02-15T11:02:23.000+0000"},"neIp":"10.20.120.50","neRsIp":null,"neUserName":"user","nePassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-03-12T11:50:17.000+0000","status":"Inactive","remarks":"Bala sys as script server","neDetails":[{"id":3,"step":1,"serverTypeEntity":{"id":1,"serverType":"JUMP BOX"},"serverName":"Sachin_sysy","serverIp":"10.20.120.69","serverUserName":"user","serverPassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-03-12T11:50:17.000+0000","path":""}]}]},"England":{"1.2.3":[{"id":4,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"neMarket":"England","neTypeEntity":{"id":2,"neType":"SCRIPT SERVER"},"neName":"script_naveen_sadiya_bala","neVersionEntity":{"id":1,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"neVersion":"1.2.3","status":"Active","createdBy":"superadmin","creationDate":"2019-02-15T11:02:23.000+0000"},"neIp":"10.20.120.50","neRsIp":null,"neUserName":"user","nePassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-03-12T12:07:28.000+0000","status":"Inactive","remarks":"naveen_sadiya_bala_sys","neDetails":[{"id":6,"step":1,"serverTypeEntity":{"id":1,"serverType":"JUMP BOX"},"serverName":"naveen sys","serverIp":"10.20.120.93","serverUserName":"user","serverPassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-03-12T12:07:28.000+0000","path":""},{"id":7,"step":2,"serverTypeEntity":{"id":1,"serverType":"JUMP BOX"},"serverName":"sadiyah_sys","serverIp":"10.20.120.12","serverUserName":"user","serverPassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-03-12T12:07:28.000+0000","path":""}]},{"id":5,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"neMarket":"England","neTypeEntity":{"id":1,"neType":"CIQ SERVER"},"neName":"ciq_bala","neVersionEntity":{"id":1,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"neVersion":"1.2.3","status":"Active","createdBy":"superadmin","creationDate":"2019-02-15T11:02:23.000+0000"},"neIp":"10.20.120.50","neRsIp":null,"neUserName":"user","nePassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-03-12T12:06:44.000+0000","status":"Active","remarks":"ciq_bala","neDetails":[]},{"id":6,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"neMarket":"England","neTypeEntity":{"id":2,"neType":"SCRIPT SERVER"},"neName":"script_bala","neVersionEntity":{"id":1,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"neVersion":"1.2.3","status":"Active","createdBy":"superadmin","creationDate":"2019-02-15T11:02:23.000+0000"},"neIp":"10.20.120.50","neRsIp":null,"neUserName":"user","nePassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-03-12T12:07:13.000+0000","status":"Active","remarks":"script_bala","neDetails":[]}]}},"searchEndDate":"03/16/2019","neMappingDetails":[{"id":3,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"enbId":"56001","networkConfigEntity":{"id":1,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"neMarket":"New England","neTypeEntity":{"id":1,"neType":"CIQ SERVER"},"neName":"ciq_sachin_bala","neVersionEntity":{"id":2,"programDetailsEntity":{"id":24,"networkTypeDetailsEntity":{"id":3,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"VZN-5G-VLSM","programDescription":"VLSM","status":"Active","creationDate":"2019-02-27T08:12:33.000+0000","createdBy":"superadmin"},"neVersion":"1.3.1","status":"Active","createdBy":"superadmin","creationDate":"2019-02-15T11:02:23.000+0000"},"neIp":"10.20.120.50","neRsIp":null,"neUserName":"user","nePassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-03-12T11:50:37.000+0000","status":"Inactive","remarks":"Bala sys as CIQ server","neDetails":[{"id":2,"step":1,"serverTypeEntity":{"id":1,"serverType":"JUMP BOX"},"serverName":"sachin_sys","serverIp":"10.20.120.69","serverUserName":"user","serverPassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-03-12T11:50:37.000+0000","path":""}]},"siteConfigType":"VZN-4G-LSM","creationDate":"2019-03-12T10:47:48.000+0000"},{"id":4,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"enbId":"56002","networkConfigEntity":{"id":2,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"neMarket":"New England","neTypeEntity":{"id":2,"neType":"SCRIPT SERVER"},"neName":"script_sachin_bala","neVersionEntity":{"id":2,"programDetailsEntity":{"id":24,"networkTypeDetailsEntity":{"id":3,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"VZN-5G-VLSM","programDescription":"VLSM","status":"Active","creationDate":"2019-02-27T08:12:33.000+0000","createdBy":"superadmin"},"neVersion":"1.3.1","status":"Active","createdBy":"superadmin","creationDate":"2019-02-15T11:02:23.000+0000"},"neIp":"10.20.120.50","neRsIp":null,"neUserName":"user","nePassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-03-12T11:50:17.000+0000","status":"Inactive","remarks":"Bala sys as script server","neDetails":[{"id":3,"step":1,"serverTypeEntity":{"id":1,"serverType":"JUMP BOX"},"serverName":"Sachin_sysy","serverIp":"10.20.120.69","serverUserName":"user","serverPassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-03-12T11:50:17.000+0000","path":""}]},"siteConfigType":"Test","creationDate":"2019-03-13T10:47:48.000+0000"},{"id":5,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"enbId":"56002","networkConfigEntity":{"id":3,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"neMarket":"New England","neTypeEntity":{"id":1,"neType":"CIQ SERVER"},"neName":"ciq_naveen_sadiya_bala","neVersionEntity":{"id":1,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"neVersion":"1.2.3","status":"Active","createdBy":"superadmin","creationDate":"2019-02-15T11:02:23.000+0000"},"neIp":"10.20.120.50","neRsIp":null,"neUserName":"user","nePassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-03-12T12:07:39.000+0000","status":"Inactive","remarks":"naveen_sadiya_bala_sys","neDetails":[{"id":4,"step":1,"serverTypeEntity":{"id":1,"serverType":"JUMP BOX"},"serverName":"naveen_sys","serverIp":"10.20.120.93","serverUserName":"user","serverPassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-03-12T12:07:39.000+0000","path":""},{"id":5,"step":2,"serverTypeEntity":{"id":1,"serverType":"JUMP BOX"},"serverName":"sadiyah_sysy","serverIp":"10.20.120.12","serverUserName":"user","serverPassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-03-12T12:07:39.000+0000","path":""}]},"siteConfigType":"VLSM","creationDate":"2019-03-14T10:47:48.000+0000"},{"id":6,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"enbId":"56001","networkConfigEntity":{"id":4,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"neMarket":"England","neTypeEntity":{"id":2,"neType":"SCRIPT SERVER"},"neName":"script_naveen_sadiya_bala","neVersionEntity":{"id":1,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"neVersion":"1.2.3","status":"Active","createdBy":"superadmin","creationDate":"2019-02-15T11:02:23.000+0000"},"neIp":"10.20.120.50","neRsIp":null,"neUserName":"user","nePassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-03-12T12:07:28.000+0000","status":"Inactive","remarks":"naveen_sadiya_bala_sys","neDetails":[{"id":6,"step":1,"serverTypeEntity":{"id":1,"serverType":"JUMP BOX"},"serverName":"naveen sys","serverIp":"10.20.120.93","serverUserName":"user","serverPassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-03-12T12:07:28.000+0000","path":""},{"id":7,"step":2,"serverTypeEntity":{"id":1,"serverType":"JUMP BOX"},"serverName":"sadiyah_sys","serverIp":"10.20.120.12","serverUserName":"user","serverPassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-03-12T12:07:28.000+0000","path":""}]},"siteConfigType":"VZN-5G-LEGACY","creationDate":"2019-03-15T10:47:48.000+0000"}],"neConfigTypeList":[],"status":"SUCCESS"}
            //this.tableData = {"sessionId":"50213613","serviceToken":"97442","searchStartDate":"03/09/2019","pageCount":618,"dropDownList":{"Upstate NY":{"1.2.3":[{"id":7,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"neMarket":"Upstate NY","neTypeEntity":{"id":4,"neType":"LSM"},"neName":"SM","neVersionEntity":{"id":1,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"neVersion":"1.2.3","status":"Active","createdBy":"superadmin","creationDate":"2019-02-15T11:02:23.000+0000"},"neIp":"10.1.1.1","neRsIp":"10.1.1.2","neUserName":"user","nePassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-03-13T09:57:07.000+0000","status":"Active","remarks":"Upstate NY lsm","neDetails":[{"id":8,"step":1,"serverTypeEntity":{"id":2,"serverType":"SANE"},"serverName":"sane 1","serverIp":"10.1.1.1","serverUserName":"user","serverPassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-03-13T09:57:07.000+0000","path":"1,2,3"}]}]}},"searchEndDate":"03/16/2019","neMappingDetails":[{"id":1,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"enbId":"56001","networkConfigEntity":{"neMarket":"Upstate NY","neName":"SM","neVersionEntity":{"id":1,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"}}}},"siteConfigType":"FDD Only","creationDate":"2019-03-16T10:33:38.000+0000"},{"id":2,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"enbId":"56002","networkConfigEntity":null,"siteConfigType":"TDD Only","creationDate":"2019-03-16T10:33:38.000+0000"},{"id":3,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"enbId":"56003","networkConfigEntity":null,"siteConfigType":null,"creationDate":"2019-03-16T10:33:38.000+0000"},{"id":4,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"enbId":"56004","networkConfigEntity":null,"siteConfigType":null,"creationDate":"2019-03-16T10:33:38.000+0000"},{"id":5,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"enbId":"56005","networkConfigEntity":null,"siteConfigType":null,"creationDate":"2019-03-16T10:33:38.000+0000"},{"id":6,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"enbId":"56006","networkConfigEntity":null,"siteConfigType":null,"creationDate":"2019-03-16T10:33:38.000+0000"},{"id":7,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"enbId":"56007","networkConfigEntity":null,"siteConfigType":null,"creationDate":"2019-03-16T10:33:38.000+0000"},{"id":8,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"enbId":"72714","networkConfigEntity":null,"siteConfigType":null,"creationDate":"2019-03-16T10:33:38.000+0000"},{"id":9,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"enbId":"72715","networkConfigEntity":null,"siteConfigType":null,"creationDate":"2019-03-16T10:33:38.000+0000"},{"id":10,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"enbId":"72716","networkConfigEntity":null,"siteConfigType":null,"creationDate":"2019-03-16T10:33:38.000+0000"}],"neConfigTypeList":["FDD Only","TDD Only"],"status":"SUCCESS"};
            this.tableData = {"pageCount":653,"dropDownList":{"UPNY":{"20.A.0":[{"id":122,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neMarket":"UPNY","neTypeEntity":{"id":8,"neType":"USM"},"neName":"4G_Test","neVersionEntity":{"id":38,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neVersion":"20.A.0","status":"StandBy","createdBy":"superadmin","creationDate":"2021-01-25T11:30:04.000+0000","releaseVersion":"r_0100"},"neIp":"10.20.120.11","neRsIp":"10.20.120.11","neRelVersion":null,"neUserName":"user","nePassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"admin","creationDate":"2020-12-08T10:51:36.000+0000","status":"Active","remarks":"","neUserPrompt":"user","neSuperUserPrompt":"user","neDetails":[{"id":85,"step":2,"serverTypeEntity":{"id":2,"serverType":"SANE"},"serverName":"Aathi","serverIp":"10.20.120.183","serverUserName":"user","serverPassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"admin","creationDate":"2020-12-08T10:51:36.000+0000","path":"","userPrompt":"user","superUserPrompt":"user"}]}],"20.B.0":[{"id":121,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neMarket":"UPNY","neTypeEntity":{"id":8,"neType":"USM"},"neName":"4G_Test","neVersionEntity":{"id":39,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neVersion":"20.B.0","status":"Active","createdBy":"superadmin","creationDate":"2020-07-20T04:48:55.000+0000","releaseVersion":"r_0100"},"neIp":"10.20.120.183","neRsIp":"10.20.120.183","neRelVersion":null,"neUserName":"user","nePassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2020-11-09T06:47:23.000+0000","status":"Active","remarks":"","neUserPrompt":"user","neSuperUserPrompt":"user","neDetails":[]}]},"Upstate NY":{"9.5.0":[{"id":79,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neMarket":"Upstate NY","neTypeEntity":{"id":8,"neType":"USM"},"neName":"East_Syracuse","neVersionEntity":{"id":31,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neVersion":"9.5.0","status":"Active","createdBy":"superadmin","creationDate":"2020-04-08T09:44:36.000+0000","releaseVersion":"r-03"},"neIp":"2001:4888:0a18:3143:01b1:0292:0000:0100","neRsIp":"2001:4888:2a1f:5030:01b1:0292:0000:0100","neRelVersion":null,"neUserName":"vsm","nePassword":"nmslab123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2020-04-08T09:45:05.000+0000","status":"Active","remarks":"","neUserPrompt":"user","neSuperUserPrompt":"superuser","neDetails":[{"id":68,"step":1,"serverTypeEntity":{"id":1,"serverType":"JUMP BOX"},"serverName":"Jump Box Server","serverIp":"172.22.82.182","serverUserName":"rctuser","serverPassword":"Rctuser","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2020-04-08T09:45:05.000+0000","path":"","userPrompt":"rctuser","superUserPrompt":"rctuser"},{"id":69,"step":2,"serverTypeEntity":{"id":2,"serverType":"SANE"},"serverName":"Sane Server","serverIp":"198.226.62.4","serverUserName":"v0bhatad","serverPassword":"S0mmer@1","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2020-04-08T09:45:05.000+0000","path":"2,1,32,1,3,1,1","userPrompt":"Please Enter Selection: >","superUserPrompt":"vendgrp"}]}],"9.0.0":[{"id":81,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neMarket":"Upstate NY","neTypeEntity":{"id":8,"neType":"USM"},"neName":"Rochester","neVersionEntity":{"id":29,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neVersion":"9.0.0","status":"Active","createdBy":"superadmin","creationDate":"2020-03-30T07:18:16.000+0000","releaseVersion":"r-03"},"neIp":"2001:4888:0a19:3143:01b4:0292:0000:0100","neRsIp":"2001:4888:2a1f:6030:01b4:0292:0000:0100","neRelVersion":null,"neUserName":"vsm","nePassword":"nmslab123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2020-05-08T13:02:51.000+0000","status":"Active","remarks":"","neUserPrompt":"user","neSuperUserPrompt":"superadmin","neDetails":[{"id":72,"step":1,"serverTypeEntity":{"id":1,"serverType":"JUMP BOX"},"serverName":"Jump Box Server","serverIp":"172.22.82.182","serverUserName":"rctuser","serverPassword":"Rctuser","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2020-05-08T13:02:51.000+0000","path":"","userPrompt":"rctuser","superUserPrompt":"rctuser"},{"id":73,"step":2,"serverTypeEntity":{"id":2,"serverType":"SANE"},"serverName":"Sane Server","serverIp":"198.226.62.4","serverUserName":"v0bhatad","serverPassword":"S0mmer@1","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2020-05-08T13:02:51.000+0000","path":"2,1,32,1,3,1,1","userPrompt":"Please Enter Selection: >","superUserPrompt":"vendgrp"}]},{"id":82,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neMarket":"Upstate NY","neTypeEntity":{"id":8,"neType":"USM"},"neName":"Westboro_Tiny","neVersionEntity":{"id":29,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neVersion":"9.0.0","status":"Active","createdBy":"superadmin","creationDate":"2020-03-30T07:18:16.000+0000","releaseVersion":"r-03"},"neIp":"2001:4888:A12:3143:106:0292:0000:0000","neRsIp":"2001:4888:2a10:5030:0123:0292:0000:0100","neRelVersion":null,"neUserName":"vsm","nePassword":"nmslab123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2020-04-08T11:34:01.000+0000","status":"Active","remarks":"","neUserPrompt":"user","neSuperUserPrompt":"superadmin","neDetails":[{"id":78,"step":1,"serverTypeEntity":{"id":1,"serverType":"JUMP BOX"},"serverName":"Jump Box Server","serverIp":"172.22.82.182","serverUserName":"rctuser","serverPassword":"Rctuser","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2020-04-08T11:34:01.000+0000","path":"","userPrompt":"rctuser","superUserPrompt":"rctuser"},{"id":79,"step":2,"serverTypeEntity":{"id":2,"serverType":"SANE"},"serverName":"Sane Server","serverIp":"198.226.62.4","serverUserName":"v0bhatad","serverPassword":"S0mmer@1","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2020-04-08T11:34:01.000+0000","path":"2,1,32,1,3,1,1","userPrompt":"Please Enter Selection: >","superUserPrompt":"vendgrp"}]},{"id":87,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neMarket":"Upstate NY","neTypeEntity":{"id":8,"neType":"USM"},"neName":"TEST_NC","neVersionEntity":{"id":29,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neVersion":"9.0.0","status":"Active","createdBy":"superadmin","creationDate":"2020-03-30T07:18:16.000+0000","releaseVersion":"r-03"},"neIp":"10.20.120.153","neRsIp":"10.20.120.153","neRelVersion":null,"neUserName":"user","nePassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2020-07-02T10:15:05.000+0000","status":"Active","remarks":"","neUserPrompt":"user","neSuperUserPrompt":"user","neDetails":[]}],"20.A.0":[{"id":93,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neMarket":"Upstate NY","neTypeEntity":{"id":8,"neType":"USM"},"neName":"Westborough","neVersionEntity":{"id":38,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neVersion":"20.A.0","status":"StandBy","createdBy":"superadmin","creationDate":"2021-01-25T11:30:04.000+0000","releaseVersion":"r_0100"},"neIp":"10.20.120.153","neRsIp":"10.20.120.153","neRelVersion":null,"neUserName":"superadmin","nePassword":"admin","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2020-07-16T04:54:58.000+0000","status":"Active","remarks":"","neUserPrompt":"user","neSuperUserPrompt":"superadmin","neDetails":[]},{"id":95,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neMarket":"Upstate NY","neTypeEntity":{"id":8,"neType":"USM"},"neName":"Rochester","neVersionEntity":{"id":38,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neVersion":"20.A.0","status":"StandBy","createdBy":"superadmin","creationDate":"2021-01-25T11:30:04.000+0000","releaseVersion":"r_0100"},"neIp":"2001:4888:0a19:3143:01b4:0292:0000:0100","neRsIp":"2001:4888:2a1f:6030:01b4:0292:0000:0100","neRelVersion":null,"neUserName":"user","nePassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2020-07-29T06:59:11.000+0000","status":"Active","remarks":"","neUserPrompt":"user","neSuperUserPrompt":"user","neDetails":[]}],"20.B.0":[{"id":94,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neMarket":"Upstate NY","neTypeEntity":{"id":8,"neType":"USM"},"neName":"Rochester_dr","neVersionEntity":{"id":39,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neVersion":"20.B.0","status":"Active","createdBy":"superadmin","creationDate":"2020-07-20T04:48:55.000+0000","releaseVersion":"r_0100"},"neIp":"10.20.120.153","neRsIp":"10.20.120.153","neRelVersion":null,"neUserName":"superadmin","nePassword":"admin","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2020-07-20T04:55:45.000+0000","status":"Active","remarks":"","neUserPrompt":"user","neSuperUserPrompt":"superadmin","neDetails":[]},{"id":96,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neMarket":"Upstate NY","neTypeEntity":{"id":8,"neType":"USM"},"neName":"Rochester","neVersionEntity":{"id":39,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neVersion":"20.B.0","status":"Active","createdBy":"superadmin","creationDate":"2020-07-20T04:48:55.000+0000","releaseVersion":"r_0100"},"neIp":"2001:4888:0a19:3143:01b4:0292:0000:0100","neRsIp":"2001:4888:2a1f:6030:01b4:0292:0000:0100","neRelVersion":null,"neUserName":"user","nePassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2020-07-29T07:03:16.000+0000","status":"Active","remarks":"","neUserPrompt":"user","neSuperUserPrompt":"user","neDetails":[]}]},"NE":{"20.A.0":[{"id":119,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neMarket":"NE","neTypeEntity":{"id":8,"neType":"USM"},"neName":"4G_Test","neVersionEntity":{"id":38,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neVersion":"20.A.0","status":"StandBy","createdBy":"superadmin","creationDate":"2021-01-25T11:30:04.000+0000","releaseVersion":"r_0100"},"neIp":"10.20.120.183","neRsIp":"10.20.120.183","neRelVersion":null,"neUserName":"user","nePassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2020-11-09T06:27:02.000+0000","status":"Active","remarks":"","neUserPrompt":"user","neSuperUserPrompt":"user","neDetails":[]},{"id":135,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neMarket":"NE","neTypeEntity":{"id":8,"neType":"USM"},"neName":"4G_Test","neVersionEntity":{"id":43,"programDetailsEntity":{"id":41,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-DSS","programDescription":"DSS","status":"Active","creationDate":"2021-06-01T08:27:25.000+0000","createdBy":"superadmin","sourceProgramId":null,"sourceprogramName":null},"neVersion":"20.A.0","status":"Active","createdBy":"superadmin","creationDate":"2020-11-05T14:29:37.000+0000","releaseVersion":"r_0101"},"neIp":"10.20.120.183","neRsIp":"10.20.120.183","neRelVersion":null,"neUserName":"user","nePassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2020-11-19T07:36:26.000+0000","status":"Active","remarks":"","neUserPrompt":"user","neSuperUserPrompt":"user","neDetails":[]}],"20.B.0":[{"id":120,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neMarket":"NE","neTypeEntity":{"id":8,"neType":"USM"},"neName":"4G_Test","neVersionEntity":{"id":39,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neVersion":"20.B.0","status":"Active","createdBy":"superadmin","creationDate":"2020-07-20T04:48:55.000+0000","releaseVersion":"r_0100"},"neIp":"10.20.120.183","neRsIp":"10.20.120.183","neRelVersion":null,"neUserName":"user","nePassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2020-11-09T06:44:56.000+0000","status":"Active","remarks":"","neUserPrompt":"user","neSuperUserPrompt":"user","neDetails":[]},{"id":134,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neMarket":"NE","neTypeEntity":{"id":8,"neType":"USM"},"neName":"4G_Test","neVersionEntity":{"id":44,"programDetailsEntity":{"id":41,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-DSS","programDescription":"DSS","status":"Active","creationDate":"2021-06-01T08:27:25.000+0000","createdBy":"superadmin","sourceProgramId":null,"sourceprogramName":null},"neVersion":"20.B.0","status":"Active","createdBy":"superadmin","creationDate":"2020-11-05T14:29:19.000+0000","releaseVersion":"r_0101"},"neIp":"10.20.120.183","neRsIp":"10.20.120.183","neRelVersion":null,"neUserName":"user","nePassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2020-11-19T07:36:26.000+0000","status":"Active","remarks":"","neUserPrompt":"user","neSuperUserPrompt":"user","neDetails":[]}]},"New England":{"9.0.0":[{"id":80,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neMarket":"New England","neTypeEntity":{"id":8,"neType":"USM"},"neName":"Rochester_Dr","neVersionEntity":{"id":29,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neVersion":"9.0.0","status":"Active","createdBy":"superadmin","creationDate":"2020-03-30T07:18:16.000+0000","releaseVersion":"r-03"},"neIp":"2001:4888:0a18:3143:01b1:0292:0000:0201","neRsIp":"2001:4888:2a1f:5030:01b1:0292:0000:0201","neRelVersion":null,"neUserName":"vsm","nePassword":"nmslab123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2020-06-01T11:19:16.000+0000","status":"Active","remarks":"","neUserPrompt":"user","neSuperUserPrompt":"user","neDetails":[{"id":70,"step":1,"serverTypeEntity":{"id":1,"serverType":"JUMP BOX"},"serverName":"Jump Box Server","serverIp":"172.22.82.182","serverUserName":"rctuser","serverPassword":"Rctuser","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2020-06-01T11:19:16.000+0000","path":"","userPrompt":"rctuser","superUserPrompt":"rctuser"},{"id":71,"step":2,"serverTypeEntity":{"id":2,"serverType":"SANE"},"serverName":"Sane Server","serverIp":"198.226.62.4","serverUserName":"v0bhatad","serverPassword":"S0mmer@1","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2020-06-01T11:19:16.000+0000","path":"2,1,32,1,3,1,1","userPrompt":"Please Enter Selection: >","superUserPrompt":"vendgrp"}]}],"8.5.1":[{"id":83,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neMarket":"New England","neTypeEntity":{"id":8,"neType":"USM"},"neName":"Westboro_Medium","neVersionEntity":{"id":27,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neVersion":"8.5.1","status":"Active","createdBy":"superadmin","creationDate":"2020-03-30T07:32:23.000+0000","releaseVersion":"r-03"},"neIp":"2001:4888:0a12:3143:0106:0292:0000:0100","neRsIp":"2001:4888:2a10:0030:0106:0292:0000:0100","neRelVersion":null,"neUserName":"vsm","nePassword":"nmslab123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-11-22T13:12:40.000+0000","status":"Active","remarks":"","neUserPrompt":"","neSuperUserPrompt":"","neDetails":[{"id":76,"step":1,"serverTypeEntity":{"id":1,"serverType":"JUMP BOX"},"serverName":"Jump Box Server","serverIp":"172.22.82.182","serverUserName":"rctuser","serverPassword":"Rctuser","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-11-22T13:12:40.000+0000","path":"","userPrompt":"rctuser","superUserPrompt":"rctuser"},{"id":77,"step":2,"serverTypeEntity":{"id":2,"serverType":"SANE"},"serverName":"Sane Server","serverIp":"198.226.62.4","serverUserName":"v0bhatad","serverPassword":"S0mmer@1","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-11-22T13:12:40.000+0000","path":"2,1,32,1,3,1,1","userPrompt":"Please Enter Selection: >","superUserPrompt":"vendgrp"}]},{"id":84,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neMarket":"New England","neTypeEntity":{"id":8,"neType":"USM"},"neName":"Windsor_Medium","neVersionEntity":{"id":27,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neVersion":"8.5.1","status":"Active","createdBy":"superadmin","creationDate":"2020-03-30T07:32:23.000+0000","releaseVersion":"r-03"},"neIp":"2001:4888:0a13:3143:0123:0292:0000:0100","neRsIp":"2001:4888:2a10:5030:0123:0292:0000:0100","neRelVersion":null,"neUserName":"vsm","nePassword":"nmslab123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-11-22T13:12:50.000+0000","status":"Active","remarks":"","neUserPrompt":"","neSuperUserPrompt":"","neDetails":[{"id":74,"step":1,"serverTypeEntity":{"id":1,"serverType":"JUMP BOX"},"serverName":"Jump Box Server","serverIp":"172.22.82.182","serverUserName":"rctuser","serverPassword":"Rctuser","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-11-22T13:12:50.000+0000","path":"","userPrompt":"rctuser","superUserPrompt":"rctuser"},{"id":75,"step":2,"serverTypeEntity":{"id":2,"serverType":"SANE"},"serverName":"Sane Server","serverIp":"198.226.62.4","serverUserName":"v0bhatad","serverPassword":"S0mmer@1","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-11-22T13:12:50.000+0000","path":"2,1,32,1,3,1,1","userPrompt":"Please Enter Selection: >","superUserPrompt":"vendgrp"}]},{"id":90,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neMarket":"New England","neTypeEntity":{"id":8,"neType":"USM"},"neName":"Training NE","neVersionEntity":{"id":27,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neVersion":"8.5.1","status":"Active","createdBy":"superadmin","creationDate":"2020-03-30T07:32:23.000+0000","releaseVersion":"r-03"},"neIp":"10.20.121.52","neRsIp":"10.20.121.52","neRelVersion":null,"neUserName":"user","nePassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2020-04-15T06:16:01.000+0000","status":"Active","remarks":"","neUserPrompt":"user","neSuperUserPrompt":"superuser","neDetails":[]}]}},"neMappingDetails":[{"id":79570,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"enbId":"139647","siteName":null,"networkConfigEntity":null,"siteConfigType":null,"enbSbIp":null,"enbSbVlan":null,"btsIp":null,"enbOamIp":null,"enbVlanId":null,"btsId":null,"bsmIp":null,"creationDate":"2021-09-06T13:20:29.000+0000","ciqName":"CTX-VZ_CIQ_Ver_0.0.07_20201214 (2).xlsx"},{"id":79571,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"enbId":"139650","siteName":null,"networkConfigEntity":null,"siteConfigType":null,"enbSbIp":null,"enbSbVlan":null,"btsIp":null,"enbOamIp":null,"enbVlanId":null,"btsId":null,"bsmIp":null,"creationDate":"2021-09-06T13:20:29.000+0000","ciqName":"CTX-VZ_CIQ_Ver_0.0.07_20201214 (2).xlsx"},{"id":79572,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"enbId":"134742","siteName":null,"networkConfigEntity":null,"siteConfigType":null,"enbSbIp":null,"enbSbVlan":null,"btsIp":null,"enbOamIp":null,"enbVlanId":null,"btsId":null,"bsmIp":null,"creationDate":"2021-09-06T13:20:29.000+0000","ciqName":"CTX-VZ_CIQ_Ver_0.0.07_20201214 (2).xlsx"},{"id":79567,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"enbId":"132974","siteName":null,"networkConfigEntity":null,"siteConfigType":null,"enbSbIp":null,"enbSbVlan":null,"btsIp":null,"enbOamIp":null,"enbVlanId":null,"btsId":null,"bsmIp":null,"creationDate":"2021-09-06T13:20:28.000+0000","ciqName":"CTX-VZ_CIQ_Ver_0.0.07_20201214 (2).xlsx"},{"id":79568,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"enbId":"140159","siteName":null,"networkConfigEntity":null,"siteConfigType":null,"enbSbIp":null,"enbSbVlan":null,"btsIp":null,"enbOamIp":null,"enbVlanId":null,"btsId":null,"bsmIp":null,"creationDate":"2021-09-06T13:20:28.000+0000","ciqName":"CTX-VZ_CIQ_Ver_0.0.07_20201214 (2).xlsx"},{"id":79569,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"enbId":"140149","siteName":null,"networkConfigEntity":null,"siteConfigType":null,"enbSbIp":null,"enbSbVlan":null,"btsIp":null,"enbOamIp":null,"enbVlanId":null,"btsId":null,"bsmIp":null,"creationDate":"2021-09-06T13:20:28.000+0000","ciqName":"CTX-VZ_CIQ_Ver_0.0.07_20201214 (2).xlsx"},{"id":79566,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"enbId":"106195","siteName":null,"networkConfigEntity":{"id":122,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neMarket":"UPNY","neTypeEntity":{"id":8,"neType":"USM"},"neName":"4G_Test","neVersionEntity":{"id":38,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neVersion":"20.A.0","status":"StandBy","createdBy":"superadmin","creationDate":"2021-01-25T11:30:04.000+0000","releaseVersion":"r_0100"},"neIp":"10.20.120.11","neRsIp":"10.20.120.11","neRelVersion":null,"neUserName":"user","nePassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"admin","creationDate":"2020-12-08T10:51:36.000+0000","status":"Active","remarks":"","neUserPrompt":"user","neSuperUserPrompt":"user","neDetails":[{"id":85,"step":2,"serverTypeEntity":{"id":2,"serverType":"SANE"},"serverName":"Aathi","serverIp":"10.20.120.183","serverUserName":"user","serverPassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"admin","creationDate":"2020-12-08T10:51:36.000+0000","path":"","userPrompt":"user","superUserPrompt":"user"}]},"siteConfigType":"NB-IoT Add","enbSbIp":"","enbSbVlan":"","btsIp":"","enbOamIp":"","enbVlanId":"","btsId":"","bsmIp":"","creationDate":"2021-03-05T14:50:12.000+0000","ciqName":null},{"id":79562,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"enbId":"117966","siteName":null,"networkConfigEntity":{"id":119,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neMarket":"NE","neTypeEntity":{"id":8,"neType":"USM"},"neName":"4G_Test","neVersionEntity":{"id":38,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neVersion":"20.A.0","status":"StandBy","createdBy":"superadmin","creationDate":"2021-01-25T11:30:04.000+0000","releaseVersion":"r_0100"},"neIp":"10.20.120.183","neRsIp":"10.20.120.183","neRelVersion":null,"neUserName":"user","nePassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2020-11-09T06:27:02.000+0000","status":"Active","remarks":"","neUserPrompt":"user","neSuperUserPrompt":"user","neDetails":[]},"siteConfigType":"NB-IoT Add","enbSbIp":"","enbSbVlan":"","btsIp":"","enbOamIp":"","enbVlanId":"","btsId":"","bsmIp":"","creationDate":"2021-02-23T10:32:09.000+0000","ciqName":null},{"id":79558,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"enbId":"121157","siteName":null,"networkConfigEntity":{"id":121,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neMarket":"UPNY","neTypeEntity":{"id":8,"neType":"USM"},"neName":"4G_Test","neVersionEntity":{"id":39,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neVersion":"20.B.0","status":"Active","createdBy":"superadmin","creationDate":"2020-07-20T04:48:55.000+0000","releaseVersion":"r_0100"},"neIp":"10.20.120.183","neRsIp":"10.20.120.183","neRelVersion":null,"neUserName":"user","nePassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2020-11-09T06:47:23.000+0000","status":"Active","remarks":"","neUserPrompt":"user","neSuperUserPrompt":"user","neDetails":[]},"siteConfigType":"NB-IoT Add","enbSbIp":"","enbSbVlan":"","btsIp":"","enbOamIp":"","enbVlanId":"","btsId":"","bsmIp":"","creationDate":"2021-01-27T13:10:52.000+0000","ciqName":null},{"id":51874,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"enbId":"70215","siteName":null,"networkConfigEntity":{"id":95,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neMarket":"Upstate NY","neTypeEntity":{"id":8,"neType":"USM"},"neName":"Rochester","neVersionEntity":{"id":38,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-08-23T11:45:12.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neVersion":"20.A.0","status":"StandBy","createdBy":"superadmin","creationDate":"2021-01-25T11:30:04.000+0000","releaseVersion":"r_0100"},"neIp":"2001:4888:0a19:3143:01b4:0292:0000:0100","neRsIp":"2001:4888:2a1f:6030:01b4:0292:0000:0100","neRelVersion":null,"neUserName":"user","nePassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2020-07-29T06:59:11.000+0000","status":"Active","remarks":"","neUserPrompt":"user","neSuperUserPrompt":"user","neDetails":[]},"siteConfigType":"NB-IoT Add","enbSbIp":"","enbSbVlan":"","btsIp":"","enbOamIp":"","enbVlanId":"","btsId":"","bsmIp":"","creationDate":"2021-01-25T11:35:30.000+0000","ciqName":null}],"sessionId":"48390f21","neConfigTypeList":["NB-IoT No","NB-IoT Only","NB-IoT Add"],"serviceToken":"61053","status":"SUCCESS"};
            this.totalPages = this.tableData.pageCount;
            this.dropDownList=this.tableData.dropDownList;
            this.marketList=Object.keys(this.tableData.dropDownList);
            this.neConfigTypeList=this.tableData.neConfigTypeList;
            
            // this.searchStartDate=new Date(this.tableData.searchStartDate);
            // this.searchEndDate=new Date(this.tableData.searchEndDate);
            this.currentProgramName=this.tableData.neMappingDetails[0].programDetailsEntity.programName;
            this.networkType = JSON.parse(sessionStorage.getItem("selectedProgram")).networkTypeDetailsEntity.networkType; //this.tableData.neMappingDetails[0].programDetailsEntity.networkTypeDetailsEntity.networkType;
            if(this.networkType == '5G' ) {
              this.validationData.rules.siteConfigType["required"] = false;
            }
            else {
              this.validationData.rules.siteConfigType["required"] = true;
            }
            //console.log(this.currentProgramName);
            let pageCount = [];
            for (var i = 1; i <= this.tableData.pageCount; i++) {
              pageCount.push(i);
            }
            this.pageRenge = pageCount;
            this.pager = this.getPager(this.totalPages * this.pageSize, this.currentPage, this.pageRenge);
            if (this.tableData.sessionId == "408" || this.tableData.status == "Invalid User") {
              this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
            }
            if (this.tableData.neMappingDetails.length == 0) {
              this.tableShowHide = false;
              this.noDataVisibility = true;
            } else {
              this.tableShowHide = true;
              this.noDataVisibility = false;
              setTimeout(() => {
                let tableWidth = document.getElementById('neMappingDetails').scrollWidth;

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

          }, 1000); */

          //Please Comment while checkIn
        });
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


/* console.log(sessionStorage); */
//console.log(sessionStorage.selectedProgram);
      this.selNeMarket="";
      this.selNeVersion="";
      this.selNeName="";
      this.selsiteConfigType="";
      this.enbSbIp ="";
      this.enbSbVlan="";
      this.btsIp="";
      this.enbOamIp="";
      this.enbVlanId="";
      this.btsId ="";
      this.bsmIp ="";

      let editState: any = event.target,i,
      parentForm: any;
    $(".editRowDisabled").attr("class", "editRow");
    /* for (i = 0; i < this.neConfigTypeList.length; i++) {
      if (this.neConfigTypeList[i] == "NB-IoT No" && this.selsiteConfigType=="")
        this.selsiteConfigType = "NB-IoT No";
    } */
    if (editState.className != "editRowDisabled") { //enable click only if it is enabled
      editState.className = "editRowDisabled";
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
        
        this.selNeMarket = key.networkConfigEntity.neMarket;
        if(this.selNeMarket){
          this.getNeVersion(this.selNeMarket);
        }
        this.selNeVersion = key.networkConfigEntity.neVersionEntity.neVersion;
        if(this.selNeVersion){
          this.getNeName(this.selNeVersion);
        }
        setTimeout(()=>{
          this.selNeName = key.networkConfigEntity;
          this.selsiteConfigType = key.siteConfigType;
         
          if(this.selsiteConfigType){
            this.neConfigTypeChange();
           }
        },10)
       
     
        parentForm = document.querySelector("#editedRow" + index);



        //map validation for fields
       // validator.performValidation(event, this.validationData, "edit");

      }, 0);
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

      if(currentElement){
        let currFormEleValue = "";
            if (key[Object.keys(key)[currFormEle]] == null) {
                currFormEleValue = "";
            } else {
                currFormEleValue = key[Object.keys(key)[currFormEle]];
            }                
            currentElement.value = currFormEleValue;
      }

      //TO DO for checkbox
    }
  }
  /*
     * On click of update button in edit then send data to server and close the block
     * @param : null
     * @retun : null
     */

  updateEditRow(index, key, event) {
    validator.performValidation(event, this.validationData, "save_update");

    setTimeout(() => {
      if (this.isValidForm(event)) {
        this.showLoader = true;
         let currentEditedForm = event.target.parentNode.parentNode,
         updatedDetails = {
              "id":key.id,
              "enbId": currentEditedForm.querySelector("#enbId").value,
              "neMarket": currentEditedForm.querySelector("#neMarket").value,
              "neVersion": currentEditedForm.querySelector("#neVersion").value,
              "networkConfigEntity": this.selNeName,
              "siteConfigType": currentEditedForm.querySelector("#siteConfigType").value,
              "enbSbIp": currentEditedForm.querySelector("#enbSbIp").value,
              "enbSbVlan": currentEditedForm.querySelector("#enbSbVlan").value,
              "btsIp": currentEditedForm.querySelector("#btsIp").value,
              "enbOamIp": currentEditedForm.querySelector("#enbOamIp").value,
              "enbVlanId": currentEditedForm.querySelector("#enbVlanId").value,
              "btsId": currentEditedForm.querySelector("#btsId").value,
              "bsmIp": currentEditedForm.querySelector("#bsmIp").value,
              "ciqName": currentEditedForm.querySelector("#ciqName").value,
       }; 
        this.nemappingService.updateNemappingDetails(updatedDetails,this.sharedService.createServiceToken())
          .subscribe(
            data => {
              let jsonStatue = data.json();
              this.showLoader = false;

              if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {

                this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });

              } else {

                if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {
                  if (jsonStatue.status == "SUCCESS") {
                    this.message = jsonStatue.reason;
                    this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });


                    this.cancelEditRow(key.id, '');

                  } else {
                    this.displayModel(jsonStatue.reason, "failureIcon");
                  }
                }
              }

            },
            error => {
              //Please Comment while checkIn
              /* let jsonStatue: any = { "reason": "NE Mapping Details Updated Successfully!", "sessionId": "c49f47af", "serviceToken": "61799", "status": "SUCCESS" };
              setTimeout(() => {
                this.showLoader = false;
                if (jsonStatue.status == "SUCCESS") {
                  this.message = jsonStatue.reason;
                  this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: "static", size: 'lg', windowClass: "success-modal" });
                  //this.displayModel("User details updated successfully !","successIcon");
                  //this.createForm = false;
                  this.cancelEditRow(key.id, '');
                } else {
                  this.displayModel(jsonStatue.reason, "failureIcon");
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

   
  searchMappingDetails(event) {

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
  
                let currentForm = event.target.parentNode.parentNode.parentNode,
                    searchCrtra = {
                        "enbId": currentForm.querySelector("#searchEnodeB").value,
                        "siteConfigType": currentForm.querySelector("#searchConfigType") ? currentForm.querySelector("#searchConfigType").value : null,
                        "searchStartDate": currentForm.querySelector("#searchStartDate").value,
                        "searchEndDate": currentForm.querySelector("#searchEndDate").value
                         
                    };
  
                if (searchCrtra.enbId || searchCrtra.siteConfigType || searchCrtra.searchStartDate || searchCrtra.searchEndDate) {
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
                this.getNeMappingDetails();
            }
        }
    }, 0);
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

  changeSorting(predicate, event, index){
    this.sharedService.dynamicSort(predicate, event, index, this.tableData.neMappingDetails);
  }
  
  getNeVersion(selNeMarket)
  {
      this.neVersion=Object.keys(this.dropDownList[selNeMarket]);
      this.neVersionList=this.dropDownList[selNeMarket];
      this.selNeVersion="";
      this.selNeName="";
  }
  
  getNeName(selNeVersion)
  {
   this.neNameList=this.neVersionList[selNeVersion];
   //this.neNameList=this.neVersionList[selNeVersion];
    
  }
  compareFn(o1: any, o2: any) {
    return o1 && o2 ? o1.id === o2.id : o1 === o2;

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
          "count": parseInt(this.pageSize,10),
          "page": parseInt(page)
      };

      this.paginationDetails = paginationDetails;
      this.pager = this.getPager(this.totalPages*this.pageSize, this.currentPage, this.pageRenge);
      this.paginationDisabbled = false;
      // Hide all the form/Table/Nodatafound5
      this.tableShowHide = false;
      
      this.getNeMappingDetails();


  }, 0);



};

onChangeTableRowLength(event) {
  this.showLoader = true;
  this.pageSize = parseInt(event.target.value);

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
      this.getNeMappingDetails();
  }, 0);

}

  neConfigTypeChange()
  {
    this.enbSbIp ="";
    this.enbSbVlan="";
    this.btsIp="";
    this.enbOamIp="";
    this.enbVlanId="";
    this.btsId="";
    this.bsmIp="";
    if(this.selsiteConfigType == 'FDD Only'){
    //  this.eNBDisable = false;
      this.validationData.rules.enbSbIp.required = true;
      this.validationData.rules.enbSbVlan.required = true;
      this.validationData.rules.btsIp.required = true;
      this.validationData.rules.enbOamIp.required = true;
      this.validationData.rules.enbVlanId.required = true;
      this.validationData.rules.btsId.required = true;
      this.validationData.rules.bsmIp.required = true;
    } else if (this.selsiteConfigType == 'FDD+TDD Script'|| this.selsiteConfigType == 'FDD+TDD Put' || this.selsiteConfigType == 'New Site' ) {
        this.validationData.rules.btsId.required = true;
        this.validationData.rules.bsmIp.required = true;
        this.validationData.rules.enbSbIp.required = false;
        this.validationData.rules.enbSbVlan.required = false;
        this.validationData.rules.btsIp.required = false;
        this.validationData.rules.enbOamIp.required = false;
        this.validationData.rules.enbVlanId.required = false;
    } 
    else{
    //  this.eNBDisable = true;
    this.validationData.rules.enbSbIp.required = false;
    this.validationData.rules.enbSbVlan.required = false;
    this.validationData.rules.btsIp.required = false;
    this.validationData.rules.enbOamIp.required = false;
    this.validationData.rules.enbVlanId.required = false;
    this.validationData.rules.btsId.required = false;
    this.validationData.rules.bsmIp.required = false;
    }
  }
}
