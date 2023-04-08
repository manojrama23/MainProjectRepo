import { ViewContainerRef, Input, ElementRef, Renderer, Component, OnInit, ViewChild, HostListener } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal, ModalDismissReasons, NgbAccordion, NgbAccordionConfig } from '@ng-bootstrap/ng-bootstrap';
import { GeneralconfigService } from '../services/generalconfig.service';
import { SharedService } from '../services/shared.service';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { filter, map, mergeMap, concat } from 'rxjs/operators';
import { validator } from '../validation';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { JsonEditorComponent, JsonEditorOptions } from 'ang-jsoneditor';

import * as $ from 'jquery';
declare var Chart: any;
declare var AmCharts: any;

@Component({
  selector: 'rct-generalconfig',
  templateUrl: './generalconfig.component.html',
  styleUrls: ['./generalconfig.component.scss'],
  providers: [GeneralconfigService]
})
export class GeneralconfigComponent implements OnInit {
  selectedNWOpt: any;
  tabAccessRestrict: any;
  test: any;
  editImageMode: number = -1;
  editFileValid: boolean = true;
  chartShowHide: boolean = false;
  editIndex: number = -1;
  editCustIndex: number = -1;
  addValue: boolean = false;
  objectKeys = Object.keys;
  createForm: boolean = false;
  showLoader: boolean = true;
  tableData: any;
  editMode: boolean = false;
  networkData: any;
  neversionData: any;
  customerData: any;
  customerList = [];
  closeResult: string;
  privilegeSetting: object;
  noDataVisibility: boolean = false;
  newNoDataWrapper: boolean = false;
  showModelMessage: boolean = false;
  messageType: any;
  modelData: any;
  sessionExpiredModalBlock: any; // Helps to close/open the model window
  successModalBlock: any;
  message: any;
  properties: any;
  val: any;
  paramter: any;
  sessiontimeout: any;
  miscParameter: any;
  months: any;
  editableFormArray = [];
  tableDataHeight: any;
  currentEditRow: any;
  lruformData: any;
  settingBlock: boolean = false;
  nwtypeBlock: boolean = false;
  neversionBlock: boolean = false;
  customerCtBlock: boolean = false;
  customerDetails = [];
  networkTypeList = [];
  versionList = [];
  networkInfo = "";
  newRowIndex: any;
  activeIds: string[] = [];
  selectedTab: any;
  cShortName: any;
  neType: any;
  searchPgmName: any;
  programNamesList: any = [];
  allProgramNamesList: any = [];
  pgmNameOnly: any;
  programDetailsEntity: any = "";
  selectedNeType: any = "";
  custShortName: any = "";

  migrationType: string ="general";
  allowedFileExts = ["png", "jpeg", "jpg", "bmp", "gif", "tif", "tiff"];
  selectedSch = [];
  scheduleList = [];
  selectedSchTime = [];
  scheduledTime: Date;
  timeList = [];//[{"label":"OFF","disabled":false},{"label":"7:00","disabled":false},{"label":"8:00","disabled":false},{"label":"9:00","disabled":false},{"label":"10:00","disabled":false},{"label":"11:00","disabled":false},{"label":"12:00","disabled":false},{"label":"13:00","disabled":false},{"label":"14:00","disabled":false},{"label":"15:00","disabled":false},{"label":"16:00","disabled":false},{"label":"17:00","disabled":false},{"label":"18:00","disabled":false},{"label":"19:00","disabled":false},{"label":"20:00","disabled":false},{"label":"21:00","disabled":false},{"label":"22:00","disabled":false},{"label":"23:00","disabled":false},{"label":"24:00","disabled":false},{"label":"00:00","disabled":false},{"label":"01:00","disabled":false},{"label":"02:00","disabled":false},{"label":"03:00","disabled":false},{"label":"04:00","disabled":false},{"label":"05:00","disabled":false},{"label":"06:00","disabled":false}];
  editorOptions: JsonEditorOptions;
  jsonData: any;
  jsonDataBlock: any;

  validationData: any = {
    "rules": {
      "config": {
        "required": true,
        "pattern": /^[0-9]+$/

      },
      "config2": {
        "required": true,
      },
      "imageInput": {
        "required": true,
        "customfunction": true
      },
      "custName": {
        "required": true
      },
      "custShortName": {
        "required": true
      },
      "schedule": {
        "required": true
      },
      "scheduleTime": {
        "required": true
      }
    },
    "messages": {
      "config": {
        "required": "Value is required",
        "pattern": "Please enter the valid value"
      },
      "config2": {
        "required": "Value is required",
        //"pattern": "Please enter the valid value"
      },
      "imageInput": {
        "required": "Please Select Logo",
        "customfunction": "Please select valid image file"
      },
      "custName": {
        "required": "Customer Name is required"

      },
      "custShortName": {
        "required": "Customer Short Name is required"
      },
      "schedule": {
        "required": "Schedule is required"
      },
      "scheduleTime": {
        "required": "Schedule Time is required"
      }
    }
  };

  // What to clone
  @ViewChild('cloneAddInlineTableRow') template;
  // Where to insert the cloned content
  @ViewChild('container', { read: ViewContainerRef }) container;

  // What to clone
  @ViewChild('neVersionAddInlineRow') neversionTemplate;
  // Where to insert the cloned content
  @ViewChild('neversionContainer', { read: ViewContainerRef }) neversionContainer;


  // What to clone
  @ViewChild('networkAddInlineRow') networkTemplate;
  // Where to insert the cloned content
  @ViewChild('networkContainer', { read: ViewContainerRef }) networkContainer;


  @ViewChild('confirmModal') confirmModalRef: ElementRef;
  @ViewChild('sessionExpiredModal') sessionExpiredModalRef: ElementRef;
  @ViewChild('successModal') successModalRef: ElementRef;
  /*  @ViewChild('settingTab') settingTabRef: ElementRef;
   @ViewChild('nwTypeTab') nwTypeTabRef: ElementRef;
   @ViewChild('customeCtTab') customeCtTabRef: ElementRef; */
  @ViewChild('imageInput') imageInputRef: ElementRef;
  @ViewChild('editor') editor: JsonEditorComponent;
  selectedFetchTime: any;
  selectedUseCases: any;
  dropdownList: any[];
  dropdownListNG: any[];


  constructor(public config1: NgbAccordionConfig,
    private element: ElementRef,
    private renderer: Renderer,
    private router: Router,
    private modalService: NgbModal,
    private generalconfigService: GeneralconfigService,
    private sharedService: SharedService
  ) {
    config1.closeOthers = true;
    this.editorOptions = new JsonEditorOptions();
    this.editorOptions.modes = ['code', 'text', 'tree', 'view']; // set all allowed modes
    this.editorOptions.mode = "code";
  }

  ngOnInit() {
    this.tabAccessRestrict = JSON.parse(sessionStorage.getItem("loginDetails")).userGroup;
    this.settingBlock = false;
    this.custShortName = "";
    this.jsonData = null;
    /* this.scheduleList = [
      {"label": "OFF", "disabled": false}, {"label": "D-0", "disabled": false}, {"label": "D-1", "disabled": false}, {"label": "D-2", "disabled": false}, {"label": "D-3", "disabled": false}, {"label": "D-4", "disabled": false}, {"label": "D-5", "disabled": false}
    ]; */
    this.timeList = this.generateTimeList(15);
    if (this.tabAccessRestrict == 'Commission Engineer' || this.tabAccessRestrict == 'Commission Manager') {
      this.settingDisp();
      this.selectedTab = "setting";
      /*   this.nwTypeDisp();
        this.selectedTab = "neversion"; */
    } else {
      this.getNetworkDetails();
      this.selectedTab = "nwtype";
    }

  }
  settingDisp() {
    this.editableFormArray = [];
    this.settingBlock = false;
    this.nwtypeBlock = false;
    this.neversionBlock = false;
    this.customerCtBlock = false;
    this.getMiscConfigDetails();
    this.selectedTab = "setting";
  }

  nwTypeDisp() {
    this.editableFormArray = [];
    this.settingBlock = false;
    this.nwtypeBlock = false;
    this.neversionBlock = false;
    this.customerCtBlock = false;
    this.getNetworkDetails();
    this.selectedTab = "nwtype";
  }

  NeVersionDisp() {
    this.editableFormArray = [];
    this.settingBlock = false;
    this.nwtypeBlock = false;
    this.neversionBlock = false;
    this.customerCtBlock = false;
    this.getneVersionDetails();
    this.selectedTab = "neversion";
  }
  customeCtDisp() {
    this.activeIds = [];
    this.createForm = false;
    this.editableFormArray = [];
    this.customerDetails = [];
    this.settingBlock = false;
    this.nwtypeBlock = false;
    this.neversionBlock = false;
    this.customerCtBlock = false;
    this.custShortName = "";
    this.getCustomerDetails();
    this.selectedTab = "customerct";
  }

  getMiscConfigDetails() {
    this.showLoader = true;
    this.generalconfigService.getConfigDetails(this.sharedService.createServiceToken(), this.migrationType)
      .subscribe(
        data => {
          setTimeout(() => {
            let jsonStatue = data.json();
            this.showLoader = false;
            if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
              this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
            } else {
              this.tableData = jsonStatue;
              if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {

                if (this.tableData.status == "SUCCESS") {
                  if (this.tableData.ConfigDetails.length == 0) {
                    this.settingBlock = false;
                    this.newNoDataWrapper = true;
                  } else {
                    this.settingBlock = true;
                    this.newNoDataWrapper = false;
                    this.sessiontimeout = jsonStatue.ConfigDetails.sessionTimeout;
                    // To display table data
                    setTimeout(() => {
                      let tableWidth = document.getElementById('removalDetails').scrollWidth;
                      $(".scrollBody table").css("min-width", (tableWidth) + "px");
                      $(".scrollHead table").css("width", tableWidth + "px");
                      $(".scrollBody").on("scroll", function (event) {
                        $(".formEditRow #formWrapper").css("margin-left", (event.target.scrollLeft - 0) + "px");
                        $(".form-control-fixed").css("right", (event.target.scrollLeft * -1) + "px");
                        $(".scrollHead table").css("margin-left", (event.target.scrollLeft * -1) + "px");
                      });
                    }, 0);
                  }
                  this.createScheduleJosn(this.tableData.fetchDays);
                } else {
                  this.showLoader = false;
                  this.displayModel(jsonStatue.reason, "failureIcon");
                }

              }

            }
          }, 1000);
        },
        error => {
          this.serverError(error);
          //Please Comment while checkIn
            setTimeout(() => {
             this.showLoader = false;
             // no data 
            // this.tableData = JSON.parse('{"ConfigDetails":[],"sessionId":"b62e5ee4","serviceToken":"78710","status":"SUCCESS"}');
            //  this.tableData = {"ConfigDetails":[{"id":null,"programDetailsEntity":null,"customerEntity":null,"label":"Session Time Out (in minutes)","value":"60","type":"GENERAL"},{"id":null,"programDetailsEntity":null,"customerEntity":null,"label":"Tool Deployment","value":"Live","type":"GENERAL"},{"id":null,"programDetailsEntity":null,"customerEntity":null,"label":"History (in days)","value":"7","type":"GENERAL"},{"id":1,"programDetailsEntity":{"id":1,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"VZN-4G-CDU30","programDescription":"CDU30","status":"Active","creationDate":"2019-04-01T10:35:23.000+0000","createdBy":"superadmin"},"customerEntity":null,"label":"CIQ_VALIDATE_TEMPLATE","value":"{\n  \"sheets\": [\n    {\n      \"sheetName\": \"New England CIQ\",\n      \"enbIdColumnHeaderName\": \"D\",\n      \"enbNameColumnHeaderName\": \"B\",\n      \"columns\": [\n        {\n          \"columnName\": \"Market\",\n          \"columnHeaderName\": \"A\",\n          \"columnAliasName\": \"Market\"\n        },\n        {\n          \"columnName\": \"eNB Name\",\n          \"columnHeaderName\": \"B\",\n          \"columnAliasName\": \"eNB_Name\"\n        },\n        {\n          \"columnName\": \"Samsung eNB ID\",\n          \"columnHeaderName\": \"D\",\n          \"columnAliasName\": \"Samsung_eNB_ID\"\n        },\n        {\n          \"columnName\": \"Cell ID\",\n          \"columnHeaderName\": \"F\",\n          \"columnAliasName\": \"Cell_ID\"\n        },\n        {\n          \"columnName\": \"TAC\",\n          \"columnHeaderName\": \"I\",\n          \"columnAliasName\": \"TAC\"\n        },\n        {\n          \"columnName\": \"PCI\",\n          \"columnHeaderName\": \"J\",\n          \"columnAliasName\": \"PCI\"\n        },\n        {\n          \"columnName\": \"RACH\",\n          \"columnHeaderName\": \"K\",\n          \"columnAliasName\": \"RACH\"\n        },\n        {\n          \"columnName\": \"BandName\",\n          \"columnHeaderName\": \"L\",\n          \"columnAliasName\": \"BandName\"\n        },\n        {\n          \"columnName\": \"Bandwidth(MHz)\",\n          \"columnHeaderName\": \"M\",\n          \"columnAliasName\": \"Bandwidth(MHz)\"\n        },\n        {\n          \"columnName\": \"EARFCN DL\",\n          \"columnHeaderName\": \"N\",\n          \"columnAliasName\": \"EARFCN_DL\"\n        },\n        {\n          \"columnName\": \"EARFCN UL\",\n          \"columnHeaderName\": \"O\",\n          \"columnAliasName\": \"EARFCN_UL\"\n        },\n        {\n          \"columnName\": \"Output Power (dBm)\",\n          \"columnHeaderName\": \"P\",\n          \"columnAliasName\": \"Output_Power(dBm)\"\n        },\n        {\n          \"columnName\": \"CPRI Port Assignment\",\n          \"columnHeaderName\": \"Q\",\n          \"columnAliasName\": \"CPRI_Port_Assignment\"\n        },\n        {\n          \"columnName\": \"Tx Diversity\",\n          \"columnHeaderName\": \"R\",\n          \"columnAliasName\": \"Tx_Diversity\"\n        },\n        {\n          \"columnName\": \"Rx Diveristy\",\n          \"columnHeaderName\": \"S\",\n          \"columnAliasName\": \"Rx_Diveristy\"\n        },\n        {\n          \"columnName\": \"Electrical Tilt\",\n          \"columnHeaderName\": \"V\",\n          \"columnAliasName\": \"Electrical_Tilt\"\n        },\n        {\n          \"columnName\": \"RRH Type\",\n          \"columnHeaderName\": \"Y\",\n          \"columnAliasName\": \"RRH_Type\"\n        },\n        {\n          \"columnName\": \"Card Count per eNB\",\n          \"columnHeaderName\": \"Z\",\n          \"columnAliasName\": \"Card_Count_per_eNB\"\n        },\n        {\n          \"columnName\": \"Deployment\",\n          \"columnHeaderName\": \"AB\",\n          \"columnAliasName\": \"Deployment\"\n        },\n        {\n          \"columnName\": \"RRH Code\",\n          \"columnHeaderName\": \"AC\",\n          \"columnAliasName\": \"RRH_Code\"\n        },\n        {\n          \"columnName\": \"Market CLLI Code\",\n          \"columnHeaderName\": \"AE\",\n          \"columnAliasName\": \"Market_CLLI_Code\"\n        },\n        {\n          \"columnName\": \"aliasName\",\n          \"columnHeaderName\": \"AF\",\n          \"columnAliasName\": \"aliasName\"\n        },\n        {\n          \"columnName\": \"antennaPathDelayDL\",\n          \"columnHeaderName\": \"AG\",\n          \"columnAliasName\": \"antennaPathDelayDL\"\n        },\n        {\n          \"columnName\": \"antennaPathDelayUL\",\n          \"columnHeaderName\": \"AH\",\n          \"columnAliasName\": \"antennaPathDelayUL\"\n        },\n        {\n          \"columnName\": \"antennaPathDelayDL (m)\",\n          \"columnHeaderName\": \"AI\",\n          \"columnAliasName\": \"antennaPathDelayDL(m)\"\n        },\n        {\n          \"columnName\": \"antennaPathDelayUL (m)\",\n          \"columnHeaderName\": \"AJ\",\n          \"columnAliasName\": \"antennaPathDelayUL(m)\"\n        },\n        {\n          \"columnName\": \"DAS OUTPUT POWER\",\n          \"columnHeaderName\": \"AT\",\n          \"columnAliasName\": \"DAS_OUTPUT_POWER\"\n        },\n        {\n          \"columnName\": \"DAS\",\n          \"columnHeaderName\": \"AS\",\n          \"columnAliasName\": \"DAS\"\n        },\n        {\n          \"columnName\": \"NB-IoT TAC\",\n          \"columnHeaderName\": \"AU\",\n          \"columnAliasName\": \"NB-IoT_TAC\"\n        }\n      ],\n      \"seqOrder\": 1,\n      \"sheetType\": \"normal\",\n      \"headerRow\": 2,\n      \"readingRange\": null,\n      \"subSheetName\": null,\n      \"sheetAliasName\": \"CIQNewEngland\",\n      \"subSheetAliasName\": null\n    },\n    {\n      \"sheetName\": \"IP PLAN\",\n      \"enbIdColumnHeaderName\": \"C\",\n      \"enbNameColumnHeaderName\": \"B\",\n      \"columns\": [\n        {\n          \"columnName\": \"Market\",\n          \"columnHeaderName\": \"A\",\n          \"columnAliasName\": \"Market\"\n        },\n        {\n          \"columnName\": \"eNB Name\",\n          \"columnHeaderName\": \"B\",\n          \"columnAliasName\": \"eNB_Name\"\n        },\n        {\n          \"columnName\": \"eNB ID\",\n          \"columnHeaderName\": \"C\",\n          \"columnAliasName\": \"eNB_ID\"\n        },\n        {\n          \"columnName\": \"VLAN\",\n          \"columnHeaderName\": \"D\",\n          \"columnAliasName\": \"VLAN\"\n        },\n        {\n          \"columnName\": \"eNB OAM VLAN\",\n          \"columnHeaderName\": \"E\",\n          \"columnAliasName\": \"eNB_OAM_VLAN\"\n        },\n        {\n          \"columnName\": \"eNB OAM/S&B VLAN prefix (/30)\",\n          \"columnHeaderName\": \"G\",\n          \"columnAliasName\": \"eNB_OAM/S&B_VLAN_prefix(/30)\"\n        },\n        {\n          \"columnName\": \"eNB OAM IP& eNB S&B IP\",\n          \"columnHeaderName\": \"H\",\n          \"columnAliasName\": \"eNB_OAM_IP&eNB_S&B_IP\"\n        },\n        {\n          \"columnName\": \"OAM Gateway IP /eNB S&B Gateway IP\",\n          \"columnHeaderName\": \"I\",\n          \"columnAliasName\": \"OAM_Gateway_IP_/eNB_S&B_Gateway IP\"\n        }\n      ],\n      \"seqOrder\": 2,\n      \"sheetType\": \"normal\",\n      \"headerRow\": 1,\n      \"readingRange\": null,\n      \"subSheetName\": null,\n      \"sheetAliasName\": \"IPPLAN\",\n      \"subSheetAliasName\": null\n    },\n    {\n      \"sheetName\": \"MME IP'S\",\n      \"sheetAliasName\": \"MMEIPS\",\n      \"enbIdColumnHeaderName\": null,\n      \"enbNameColumnHeaderName\": null,\n      \"columns\": [\n        {\n          \"columnName\": \"MME\",\n          \"columnHeaderName\": \"A\",\n          \"columnAliasName\": \"MME\"\n        },\n        {\n          \"columnName\": \"IP Address\",\n          \"columnHeaderName\": \"B\",\n          \"columnAliasName\": \"IP_Address\"\n        }\n      ],\n      \"seqOrder\": 3,\n      \"sheetType\": \"multiple\",\n      \"headerRow\": 2,\n      \"readingRange\": \"3-16\",\n      \"subSheetName\": \"New England\",\n      \"subSheetAliasName\": \"MMEIPS_New_England\"\n    },\n    {\n      \"sheetName\": \"MME IP'S\",\n      \"sheetAliasName\": \"MMEIPS\",\n      \"enbIdColumnHeaderName\": null,\n      \"enbNameColumnHeaderName\": null,\n      \"columns\": [\n        {\n          \"columnName\": \"MME\",\n          \"columnHeaderName\": \"A\",\n          \"columnAliasName\": \"MME\"\n        },\n        {\n          \"columnName\": \"IP Address\",\n          \"columnHeaderName\": \"B\",\n          \"columnAliasName\": \"IP_Address\"\n        }\n      ],\n      \"seqOrder\": 3,\n      \"sheetType\": \"multiple\",\n      \"headerRow\": 19,\n      \"readingRange\": \"20-27\",\n      \"subSheetName\": \"UNY\",\n      \"subSheetAliasName\": \"MMEIPS_UNY\"\n    },\n    {\n      \"sheetName\": \"Upstate NY CIQ\",\n      \"enbIdColumnHeaderName\": \"D\",\n      \"enbNameColumnHeaderName\": \"B\",\n      \"columns\": [\n        {\n          \"columnName\": \"Market\",\n          \"columnHeaderName\": \"A\",\n          \"columnAliasName\": \"Market\"\n        },\n        {\n          \"columnName\": \"eNB Name\",\n          \"columnHeaderName\": \"B\",\n          \"columnAliasName\": \"eNB_Name\"\n        },\n        {\n          \"columnName\": \"Samsung eNB ID\",\n          \"columnHeaderName\": \"D\",\n          \"columnAliasName\": \"Samsung_eNB_ID\"\n        },\n        {\n          \"columnName\": \"Cell ID\",\n          \"columnHeaderName\": \"F\",\n          \"columnAliasName\": \"Cell_ID\"\n        },\n        {\n          \"columnName\": \"TAC\",\n          \"columnHeaderName\": \"I\",\n          \"columnAliasName\": \"TAC\"\n        },\n        {\n          \"columnName\": \"PCI\",\n          \"columnHeaderName\": \"J\",\n          \"columnAliasName\": \"PCI\"\n        },\n        {\n          \"columnName\": \"RACH\",\n          \"columnHeaderName\": \"K\",\n          \"columnAliasName\": \"RACH\"\n        },\n        {\n          \"columnName\": \"BandName\",\n          \"columnHeaderName\": \"L\",\n          \"columnAliasName\": \"BandName\"\n        },\n        {\n          \"columnName\": \"Bandwidth(MHz)\",\n          \"columnHeaderName\": \"M\",\n          \"columnAliasName\": \"Bandwidth(MHz)\"\n        },\n        {\n          \"columnName\": \"EARFCN DL\",\n          \"columnHeaderName\": \"N\",\n          \"columnAliasName\": \"EARFCN_DL\"\n        },\n        {\n          \"columnName\": \"EARFCN UL\",\n          \"columnHeaderName\": \"O\",\n          \"columnAliasName\": \"EARFCN_UL\"\n        },\n        {\n          \"columnName\": \"Output Power (dBm)\",\n          \"columnHeaderName\": \"P\",\n          \"columnAliasName\": \"Output_Power(dBm)\"\n        },\n        {\n          \"columnName\": \"CPRI Port Assignment\",\n          \"columnHeaderName\": \"Q\",\n          \"columnAliasName\": \"CPRI_Port_Assignment\"\n        },\n        {\n          \"columnName\": \"Tx Diversity\",\n          \"columnHeaderName\": \"R\",\n          \"columnAliasName\": \"Tx_Diversity\"\n        },\n        {\n          \"columnName\": \"Rx Diveristy\",\n          \"columnHeaderName\": \"S\",\n          \"columnAliasName\": \"Rx_Diveristy\"\n        },\n        {\n          \"columnName\": \"Electrical Tilt\",\n          \"columnHeaderName\": \"V\",\n          \"columnAliasName\": \"Electrical_Tilt\"\n        },\n        {\n          \"columnName\": \"RRH Type\",\n          \"columnHeaderName\": \"Y\",\n          \"columnAliasName\": \"RRH_Type\"\n        },\n        {\n          \"columnName\": \"Card Count per eNB\",\n          \"columnHeaderName\": \"Z\",\n          \"columnAliasName\": \"Card_Count_per_eNB\"\n        },\n        {\n          \"columnName\": \"Deployment\",\n          \"columnHeaderName\": \"AB\",\n          \"columnAliasName\": \"Deployment\"\n        },\n        {\n          \"columnName\": \"RRH Code\",\n          \"columnHeaderName\": \"AC\",\n          \"columnAliasName\": \"RRH_Code\"\n        },\n        {\n          \"columnName\": \"Market CLLI Code\",\n          \"columnHeaderName\": \"AE\",\n          \"columnAliasName\": \"Market_CLLI_Code\"\n        },\n        {\n          \"columnName\": \"aliasName\",\n          \"columnHeaderName\": \"AG\",\n          \"columnAliasName\": \"aliasName\"\n        },\n        {\n          \"columnName\": \"antennaPathDelayDL\",\n          \"columnHeaderName\": \"AH\",\n          \"columnAliasName\": \"antennaPathDelayDL\"\n        },\n        {\n          \"columnName\": \"antennaPathDelayUL\",\n          \"columnHeaderName\": \"AI\",\n          \"columnAliasName\": \"antennaPathDelayUL\"\n        },\n        {\n          \"columnName\": \"antennaPathDelayDL (m)\",\n          \"columnHeaderName\": \"AJ\",\n          \"columnAliasName\": \"antennaPathDelayDL(m)\"\n        },\n        {\n          \"columnName\": \"antennaPathDelayUL (m)\",\n          \"columnHeaderName\": \"AK\",\n          \"columnAliasName\": \"antennaPathDelayUL(m)\"\n        },\n        {\n          \"columnName\": \"DAS OUTPUT POWER\",\n          \"columnHeaderName\": \"AU\",\n          \"columnAliasName\": \"DAS_OUTPUT_POWER\"\n        },\n        {\n          \"columnName\": \"DAS\",\n          \"columnHeaderName\": \"AT\",\n          \"columnAliasName\": \"DAS\"\n        },\n        {\n          \"columnName\": \"NB-IoT TAC\",\n          \"columnHeaderName\": \"AV\",\n          \"columnAliasName\": \"NB-IoT_TAC\"\n        }\n      ],\n      \"seqOrder\": 4,\n      \"sheetType\": \"normal\",\n      \"headerRow\": 2,\n      \"readingRange\": null,\n      \"subSheetName\": null,\n      \"sheetAliasName\": \"CIQUpstateNY\",\n      \"subSheetAliasName\": null\n    }\n  ]\n}","type":"PROGRAM TEMPLATE"},{"id":2,"programDetailsEntity":{"id":1,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"VZN-4G-CDU30","programDescription":"CDU30","status":"Active","creationDate":"2019-04-01T10:35:23.000+0000","createdBy":"superadmin"},"customerEntity":null,"label":"CHECK_LIST_VALIDATE_TEMPLATE","value":"{\n  \"sheets\": [\n    {\n      \"sheetName\": \"Migration\",\n      \"columns\": [\n        {\n          \"columnName\": \"ITEM\",\n          \"validationRule\": \"valid\",\n          \"columnHeaderName\": \"A\"\n        },\n        {\n          \"columnName\": \"STEPS\",\n          \"validationRule\": \"valid\",\n          \"columnHeaderName\": \"B\"\n        },\n        {\n          \"columnName\": \"NOTES\",\n          \"validationRule\": \"valid\",\n          \"columnHeaderName\": \"C\"\n        },\n        {\n          \"columnName\": \"COMMENTS\",\n          \"validationRule\": \"valid\",\n          \"columnHeaderName\": \"D\"\n        }\n      ],\n      \"seqOrder\": 1,\n      \"sheetType\": \"normal\",\n      \"headerRow\": 2,\n      \"readingRange\": null,\n      \"subSheetName\": null\n    }\n   \n  ]\n}","type":"PROGRAM TEMPLATE"},{"id":3,"programDetailsEntity":{"id":1,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"VZN-4G-CDU30","programDescription":"CDU30","status":"Active","creationDate":"2019-04-01T10:35:23.000+0000","createdBy":"superadmin"},"customerEntity":null,"label":"ENB_MENU_TEMPLATE","value":"{\n  \"ciqMenu\": [\n    {\n      \"subMenu\": [\n        \"Cell_ID\",\n        \"TAC\",\n        \"Tx_Diversity\"\n      ],\n      \"menuName\": \"SYS_INFO\",\n      \"sheetAliasName\": \"CIQNewEngland\",\n      \"subSheetAliasName\": null\n    },\n    {\n      \"subMenu\": [\n        \"Market\",\n        \"Market_CLLI_Code\"\n      ],\n      \"menuName\": \"MARKET_INFO\",\n      \"sheetAliasName\": \"CIQNewEngland\",\n      \"subSheetAliasName\": null\n    },\n    {\n      \"subMenu\": [\n        \"VLAN\",\n        \"eNB_OAM_VLAN\"\n      ],\n      \"menuName\": \"IPDETAILS\",\n      \"sheetAliasName\": \"IPPLAN\",\n      \"subSheetAliasName\": null\n    },\n    {\n      \"subMenu\": [\n        \"CPRI_Port_Assignment\",\n        \"antennaPathDelayDL(m)\"\n      ],\n      \"menuName\": \"PORT DETAILS\",\n      \"sheetAliasName\": \"CIQNewEngland\",\n      \"subSheetAliasName\":null\n    },\n    \n    {\n      \"subMenu\": [\n        \"BandName\",\n        \"Bandwidth(MHz)\"\n      ],\n      \"menuName\": \"BAND DETAILS\",\n      \"sheetAliasName\": \"CIQNewEngland\",\n      \"subSheetAliasName\":null\n    }\n  ]\n}","type":"PROGRAM TEMPLATE"},{"id":4,"programDetailsEntity":{"id":1,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"VZN-4G-CDU30","programDescription":"CDU30","status":"Active","creationDate":"2019-04-01T10:35:23.000+0000","createdBy":"superadmin"},"customerEntity":null,"label":"CIQ_FILE_PATH","value":"/home/user","type":"PROGRAM TEMPLATE"},{"id":5,"programDetailsEntity":{"id":1,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"VZN-4G-CDU30","programDescription":"CDU30","status":"Active","creationDate":"2019-04-01T10:35:23.000+0000","createdBy":"superadmin"},"customerEntity":null,"label":"CIQ_NAME_TEMPLATE","value":"UNY-NE-VZ_CIQ_Ver*.xlsx","type":"PROGRAM TEMPLATE"},{"id":6,"programDetailsEntity":{"id":1,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"VZN-4G-CDU30","programDescription":"CDU30","status":"Active","creationDate":"2019-04-01T10:35:23.000+0000","createdBy":"superadmin"},"customerEntity":null,"label":"SCRIPT_FILE_PATH","value":"/home/user","type":"PROGRAM TEMPLATE"},{"id":7,"programDetailsEntity":{"id":1,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"VZN-4G-CDU30","programDescription":"CDU30","status":"Active","creationDate":"2019-04-01T10:35:23.000+0000","createdBy":"superadmin"},"customerEntity":null,"label":"SCRIPT_NAME_TEMPLATE","value":"1_*.zip","type":"PROGRAM TEMPLATE"},{"id":46,"programDetailsEntity":{"id":1,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"VZN-4G-CDU30","programDescription":"CDU30","status":"Active","creationDate":"2019-04-01T10:35:23.000+0000","createdBy":"superadmin"},"customerEntity":null,"label":"PARAMETERS_VALIDATE_TEMPLATE","value":"","type":"PROGRAM TEMPLATE"},{"id":61,"programDetailsEntity":{"id":1,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"VZN-4G-CDU30","programDescription":"CDU30","status":"Active","creationDate":"2019-04-01T10:35:23.000+0000","createdBy":"superadmin"},"customerEntity":null,"label":"SCRIPT_STORE_TEMPLATE","value":"","type":"PROGRAM TEMPLATE"},{"id":14,"programDetailsEntity":{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"VZN-4G-VLSM","programDescription":"VLSM","status":"Active","creationDate":"2019-03-22T10:18:37.000+0000","createdBy":"superadmin"},"customerEntity":null,"label":"CIQ_VALIDATE_TEMPLATE","value":"{\n  \"sheets\": [\n    {\n      \"sheetName\": \"New England CIQ\",\n      \"enbIdColumnHeaderName\": \"D\",\n      \"enbNameColumnHeaderName\": \"B\",\n      \"columns\": [\n        {\n          \"columnName\": \"Market\",\n          \"columnHeaderName\": \"A\",\n          \"columnAliasName\": \"Market\"\n        },\n        {\n          \"columnName\": \"eNB Name\",\n          \"columnHeaderName\": \"B\",\n          \"columnAliasName\": \"eNB_Name\"\n        },\n        {\n          \"columnName\": \"Samsung eNB ID\",\n          \"columnHeaderName\": \"D\",\n          \"columnAliasName\": \"Samsung_eNB_ID\"\n        },\n        {\n          \"columnName\": \"Cell ID\",\n          \"columnHeaderName\": \"F\",\n          \"columnAliasName\": \"Cell_ID\"\n        },\n        {\n          \"columnName\": \"TAC\",\n          \"columnHeaderName\": \"I\",\n          \"columnAliasName\": \"TAC\"\n        },\n        {\n          \"columnName\": \"PCI\",\n          \"columnHeaderName\": \"J\",\n          \"columnAliasName\": \"PCI\"\n        },\n        {\n          \"columnName\": \"RACH\",\n          \"columnHeaderName\": \"K\",\n          \"columnAliasName\": \"RACH\"\n        },\n        {\n          \"columnName\": \"BandName\",\n          \"columnHeaderName\": \"L\",\n          \"columnAliasName\": \"BandName\"\n        },\n        {\n          \"columnName\": \"Bandwidth(MHz)\",\n          \"columnHeaderName\": \"M\",\n          \"columnAliasName\": \"Bandwidth(MHz)\"\n        },\n        {\n          \"columnName\": \"EARFCN DL\",\n          \"columnHeaderName\": \"N\",\n          \"columnAliasName\": \"EARFCN_DL\"\n        },\n        {\n          \"columnName\": \"EARFCN UL\",\n          \"columnHeaderName\": \"O\",\n          \"columnAliasName\": \"EARFCN_UL\"\n        },\n        {\n          \"columnName\": \"Output Power (dBm)\",\n          \"columnHeaderName\": \"P\",\n          \"columnAliasName\": \"Output_Power(dBm)\"\n        },\n        {\n          \"columnName\": \"CPRI Port Assignment\",\n          \"columnHeaderName\": \"Q\",\n          \"columnAliasName\": \"CPRI_Port_Assignment\"\n        },\n        {\n          \"columnName\": \"Tx Diversity\",\n          \"columnHeaderName\": \"R\",\n          \"columnAliasName\": \"Tx_Diversity\"\n        },\n        {\n          \"columnName\": \"Rx Diveristy\",\n          \"columnHeaderName\": \"S\",\n          \"columnAliasName\": \"Rx_Diveristy\"\n        },\n        {\n          \"columnName\": \"Electrical Tilt\",\n          \"columnHeaderName\": \"V\",\n          \"columnAliasName\": \"Electrical_Tilt\"\n        },\n        {\n          \"columnName\": \"RRH Type\",\n          \"columnHeaderName\": \"Y\",\n          \"columnAliasName\": \"RRH_Type\"\n        },\n        {\n          \"columnName\": \"Card Count per eNB\",\n          \"columnHeaderName\": \"Z\",\n          \"columnAliasName\": \"Card_Count_per_eNB\"\n        },\n        {\n          \"columnName\": \"Deployment\",\n          \"columnHeaderName\": \"AB\",\n          \"columnAliasName\": \"Deployment\"\n        },\n        {\n          \"columnName\": \"RRH Code\",\n          \"columnHeaderName\": \"AC\",\n          \"columnAliasName\": \"RRH_Code\"\n        },\n        {\n          \"columnName\": \"Market CLLI Code\",\n          \"columnHeaderName\": \"AE\",\n          \"columnAliasName\": \"Market_CLLI_Code\"\n        },\n        {\n          \"columnName\": \"aliasName\",\n          \"columnHeaderName\": \"AF\",\n          \"columnAliasName\": \"aliasName\"\n        },\n        {\n          \"columnName\": \"antennaPathDelayDL\",\n          \"columnHeaderName\": \"AG\",\n          \"columnAliasName\": \"antennaPathDelayDL\"\n        },\n        {\n          \"columnName\": \"antennaPathDelayUL\",\n          \"columnHeaderName\": \"AH\",\n          \"columnAliasName\": \"antennaPathDelayUL\"\n        },\n        {\n          \"columnName\": \"antennaPathDelayDL (m)\",\n          \"columnHeaderName\": \"AI\",\n          \"columnAliasName\": \"antennaPathDelayDL(m)\"\n        },\n        {\n          \"columnName\": \"antennaPathDelayUL (m)\",\n          \"columnHeaderName\": \"AJ\",\n          \"columnAliasName\": \"antennaPathDelayUL(m)\"\n        },\n        {\n          \"columnName\": \"DAS OUTPUT POWER\",\n          \"columnHeaderName\": \"AT\",\n          \"columnAliasName\": \"DAS_OUTPUT_POWER\"\n        },\n        {\n          \"columnName\": \"DAS\",\n          \"columnHeaderName\": \"AS\",\n          \"columnAliasName\": \"DAS\"\n        },\n        {\n          \"columnName\": \"NB-IoT TAC\",\n          \"columnHeaderName\": \"AU\",\n          \"columnAliasName\": \"NB-IoT_TAC\"\n        }\n      ],\n      \"seqOrder\": 1,\n      \"sheetType\": \"normal\",\n      \"headerRow\": 2,\n      \"readingRange\": null,\n      \"subSheetName\": null,\n      \"sheetAliasName\": \"CIQNewEngland\",\n      \"subSheetAliasName\": null\n    },\n    {\n      \"sheetName\": \"IP PLAN\",\n      \"enbIdColumnHeaderName\": \"C\",\n      \"enbNameColumnHeaderName\": \"B\",\n      \"columns\": [\n        {\n          \"columnName\": \"Market\",\n          \"columnHeaderName\": \"A\",\n          \"columnAliasName\": \"Market\"\n        },\n        {\n          \"columnName\": \"eNB Name\",\n          \"columnHeaderName\": \"B\",\n          \"columnAliasName\": \"eNB_Name\"\n        },\n        {\n          \"columnName\": \"eNB ID\",\n          \"columnHeaderName\": \"C\",\n          \"columnAliasName\": \"eNB_ID\"\n        },\n        {\n          \"columnName\": \"VLAN\",\n          \"columnHeaderName\": \"D\",\n          \"columnAliasName\": \"VLAN\"\n        },\n        {\n          \"columnName\": \"eNB OAM VLAN\",\n          \"columnHeaderName\": \"E\",\n          \"columnAliasName\": \"eNB_OAM_VLAN\"\n        },\n        {\n          \"columnName\": \"eNB OAM/S&B VLAN prefix (/30)\",\n          \"columnHeaderName\": \"G\",\n          \"columnAliasName\": \"eNB_OAM/S&B_VLAN_prefix(/30)\"\n        },\n        {\n          \"columnName\": \"eNB OAM IP& eNB S&B IP\",\n          \"columnHeaderName\": \"H\",\n          \"columnAliasName\": \"eNB_OAM_IP&eNB_S&B_IP\"\n        },\n        {\n          \"columnName\": \"OAM Gateway IP /eNB S&B Gateway IP\",\n          \"columnHeaderName\": \"I\",\n          \"columnAliasName\": \"OAM_Gateway_IP_/eNB_S&B_Gateway IP\"\n        }\n      ],\n      \"seqOrder\": 2,\n      \"sheetType\": \"normal\",\n      \"headerRow\": 1,\n      \"readingRange\": null,\n      \"subSheetName\": null,\n      \"sheetAliasName\": \"IPPLAN\",\n      \"subSheetAliasName\": null\n    },\n    {\n      \"sheetName\": \"MME IP'S\",\n      \"sheetAliasName\": \"MMEIPS\",\n      \"enbIdColumnHeaderName\": null,\n      \"enbNameColumnHeaderName\": null,\n      \"columns\": [\n        {\n          \"columnName\": \"MME\",\n          \"columnHeaderName\": \"A\",\n          \"columnAliasName\": \"MME\"\n        },\n        {\n          \"columnName\": \"IP Address\",\n          \"columnHeaderName\": \"B\",\n          \"columnAliasName\": \"IP_Address\"\n        }\n      ],\n      \"seqOrder\": 3,\n      \"sheetType\": \"multiple\",\n      \"headerRow\": 2,\n      \"readingRange\": \"3-16\",\n      \"subSheetName\": \"New England\",\n      \"subSheetAliasName\": \"MMEIPS_New_England\"\n    },\n    {\n      \"sheetName\": \"MME IP'S\",\n      \"sheetAliasName\": \"MMEIPS\",\n      \"enbIdColumnHeaderName\": null,\n      \"enbNameColumnHeaderName\": null,\n      \"columns\": [\n        {\n          \"columnName\": \"MME\",\n          \"columnHeaderName\": \"A\",\n          \"columnAliasName\": \"MME\"\n        },\n        {\n          \"columnName\": \"IP Address\",\n          \"columnHeaderName\": \"B\",\n          \"columnAliasName\": \"IP_Address\"\n        }\n      ],\n      \"seqOrder\": 3,\n      \"sheetType\": \"multiple\",\n      \"headerRow\": 19,\n      \"readingRange\": \"20-27\",\n      \"subSheetName\": \"UNY\",\n      \"subSheetAliasName\": \"MMEIPS_UNY\"\n    },\n    {\n      \"sheetName\": \"Upstate NY CIQ\",\n      \"enbIdColumnHeaderName\": \"D\",\n      \"enbNameColumnHeaderName\": \"B\",\n      \"columns\": [\n        {\n          \"columnName\": \"Market\",\n          \"columnHeaderName\": \"A\",\n          \"columnAliasName\": \"Market\"\n        },\n        {\n          \"columnName\": \"eNB Name\",\n          \"columnHeaderName\": \"B\",\n          \"columnAliasName\": \"eNB_Name\"\n        },\n        {\n          \"columnName\": \"Samsung eNB ID\",\n          \"columnHeaderName\": \"D\",\n          \"columnAliasName\": \"Samsung_eNB_ID\"\n        },\n        {\n          \"columnName\": \"Cell ID\",\n          \"columnHeaderName\": \"F\",\n          \"columnAliasName\": \"Cell_ID\"\n        },\n        {\n          \"columnName\": \"TAC\",\n          \"columnHeaderName\": \"I\",\n          \"columnAliasName\": \"TAC\"\n        },\n        {\n          \"columnName\": \"PCI\",\n          \"columnHeaderName\": \"J\",\n          \"columnAliasName\": \"PCI\"\n        },\n        {\n          \"columnName\": \"RACH\",\n          \"columnHeaderName\": \"K\",\n          \"columnAliasName\": \"RACH\"\n        },\n        {\n          \"columnName\": \"BandName\",\n          \"columnHeaderName\": \"L\",\n          \"columnAliasName\": \"BandName\"\n        },\n        {\n          \"columnName\": \"Bandwidth(MHz)\",\n          \"columnHeaderName\": \"M\",\n          \"columnAliasName\": \"Bandwidth(MHz)\"\n        },\n        {\n          \"columnName\": \"EARFCN DL\",\n          \"columnHeaderName\": \"N\",\n          \"columnAliasName\": \"EARFCN_DL\"\n        },\n        {\n          \"columnName\": \"EARFCN UL\",\n          \"columnHeaderName\": \"O\",\n          \"columnAliasName\": \"EARFCN_UL\"\n        },\n        {\n          \"columnName\": \"Output Power (dBm)\",\n          \"columnHeaderName\": \"P\",\n          \"columnAliasName\": \"Output_Power(dBm)\"\n        },\n        {\n          \"columnName\": \"CPRI Port Assignment\",\n          \"columnHeaderName\": \"Q\",\n          \"columnAliasName\": \"CPRI_Port_Assignment\"\n        },\n        {\n          \"columnName\": \"Tx Diversity\",\n          \"columnHeaderName\": \"R\",\n          \"columnAliasName\": \"Tx_Diversity\"\n        },\n        {\n          \"columnName\": \"Rx Diveristy\",\n          \"columnHeaderName\": \"S\",\n          \"columnAliasName\": \"Rx_Diveristy\"\n        },\n        {\n          \"columnName\": \"Electrical Tilt\",\n          \"columnHeaderName\": \"V\",\n          \"columnAliasName\": \"Electrical_Tilt\"\n        },\n        {\n          \"columnName\": \"RRH Type\",\n          \"columnHeaderName\": \"Y\",\n          \"columnAliasName\": \"RRH_Type\"\n        },\n        {\n          \"columnName\": \"Card Count per eNB\",\n          \"columnHeaderName\": \"Z\",\n          \"columnAliasName\": \"Card_Count_per_eNB\"\n        },\n        {\n          \"columnName\": \"Deployment\",\n          \"columnHeaderName\": \"AB\",\n          \"columnAliasName\": \"Deployment\"\n        },\n        {\n          \"columnName\": \"RRH Code\",\n          \"columnHeaderName\": \"AC\",\n          \"columnAliasName\": \"RRH_Code\"\n        },\n        {\n          \"columnName\": \"Market CLLI Code\",\n          \"columnHeaderName\": \"AE\",\n          \"columnAliasName\": \"Market_CLLI_Code\"\n        },\n        {\n          \"columnName\": \"aliasName\",\n          \"columnHeaderName\": \"AG\",\n          \"columnAliasName\": \"aliasName\"\n        },\n        {\n          \"columnName\": \"antennaPathDelayDL\",\n          \"columnHeaderName\": \"AH\",\n          \"columnAliasName\": \"antennaPathDelayDL\"\n        },\n        {\n          \"columnName\": \"antennaPathDelayUL\",\n          \"columnHeaderName\": \"AI\",\n          \"columnAliasName\": \"antennaPathDelayUL\"\n        },\n        {\n          \"columnName\": \"antennaPathDelayDL (m)\",\n          \"columnHeaderName\": \"AJ\",\n          \"columnAliasName\": \"antennaPathDelayDL(m)\"\n        },\n        {\n          \"columnName\": \"antennaPathDelayUL (m)\",\n          \"columnHeaderName\": \"AK\",\n          \"columnAliasName\": \"antennaPathDelayUL(m)\"\n        },\n        {\n          \"columnName\": \"DAS OUTPUT POWER\",\n          \"columnHeaderName\": \"AU\",\n          \"columnAliasName\": \"DAS_OUTPUT_POWER\"\n        },\n        {\n          \"columnName\": \"DAS\",\n          \"columnHeaderName\": \"AT\",\n          \"columnAliasName\": \"DAS\"\n        },\n        {\n          \"columnName\": \"NB-IoT TAC\",\n          \"columnHeaderName\": \"AV\",\n          \"columnAliasName\": \"NB-IoT_TAC\"\n        }\n      ],\n      \"seqOrder\": 1,\n      \"sheetType\": \"normal\",\n      \"headerRow\": 2,\n      \"readingRange\": null,\n      \"subSheetName\": null,\n      \"sheetAliasName\": \"CIQUpstateNY\",\n      \"subSheetAliasName\": null\n    }\n  ]\n}","type":"PROGRAM TEMPLATE"},{"id":15,"programDetailsEntity":{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"VZN-4G-VLSM","programDescription":"VLSM","status":"Active","creationDate":"2019-03-22T10:18:37.000+0000","createdBy":"superadmin"},"customerEntity":null,"label":"CHECK_LIST_VALIDATE_TEMPLATE","value":"{\n  \"sheets\": [\n    {\n      \"sheetName\": \"Migration-Checklist\",\n      \"columns\": [\n        {\n          \"columnName\": \"INDEX\",\n          \"validationRule\": \"valid\",\n          \"columnHeaderName\": \"A\"\n        },\n        {\n          \"columnName\": \"TASK\",\n          \"validationRule\": \"valid\",\n          \"columnHeaderName\": \"B\"\n        },\n        {\n          \"columnName\": \"PROCEDURE\",\n          \"validationRule\": \"valid\",\n          \"columnHeaderName\": \"C\"\n        },\n        {\n          \"columnName\": \"Notes\",\n          \"validationRule\": \"valid\",\n          \"columnHeaderName\": \"D\"\n        },\n        {\n          \"columnName\": \"Start Time\",\n          \"validationRule\": \"valid\",\n          \"columnHeaderName\": \"E\"\n        },\n        {\n          \"columnName\": \"End Time\",\n          \"validationRule\": \"valid\",\n          \"columnHeaderName\": \"F\"\n        },\n        {\n          \"columnName\": \"Estimated working time \\n(mins)\",\n          \"validationRule\": \"valid\",\n          \"columnHeaderName\": \"G\"\n        }\n      ],\n      \"seqOrder\": 1,\n      \"sheetType\": \"normal\",\n      \"headerRow\": 2,\n      \"readingRange\": null,\n      \"subSheetName\": null\n    }\n  ]\n}","type":"PROGRAM TEMPLATE"},{"id":16,"programDetailsEntity":{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"VZN-4G-VLSM","programDescription":"VLSM","status":"Active","creationDate":"2019-03-22T10:18:37.000+0000","createdBy":"superadmin"},"customerEntity":null,"label":"ENB_MENU_TEMPLATE","value":"{\n  \"ciqMenu\": [\n    {\n      \"subMenu\": [\n        \"Cell_ID\",\n        \"TAC\",\n        \"Tx_Diversity\"\n      ],\n      \"menuName\": \"SYS_INFO\",\n      \"sheetAliasName\": \"CIQNewEngland\",\n      \"subSheetAliasName\": null\n    },\n    {\n      \"subMenu\": [\n        \"Market\",\n        \"Market_CLLI_Code\"\n      ],\n      \"menuName\": \"MARKET_INFO\",\n      \"sheetAliasName\": \"CIQNewEngland\",\n      \"subSheetAliasName\": null\n    },\n    {\n      \"subMenu\": [\n        \"VLAN\",\n        \"eNB_OAM_VLAN\"\n      ],\n      \"menuName\": \"IPDETAILS\",\n      \"sheetAliasName\": \"IPPLAN\",\n      \"subSheetAliasName\": null\n    },\n    {\n      \"subMenu\": [\n        \"CPRI_Port_Assignment\",\n        \"antennaPathDelayDL(m)\"\n      ],\n      \"menuName\": \"PORT DETAILS\",\n      \"sheetAliasName\": \"CIQNewEngland\",\n      \"subSheetAliasName\":null\n    },\n    \n    {\n      \"subMenu\": [\n        \"BandName\",\n        \"Bandwidth(MHz)\"\n      ],\n      \"menuName\": \"BAND DETAILS\",\n      \"sheetAliasName\": \"CIQNewEngland\",\n      \"subSheetAliasName\":null\n    }\n  ]\n}\n","type":"PROGRAM TEMPLATE"},{"id":17,"programDetailsEntity":{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"VZN-4G-VLSM","programDescription":"VLSM","status":"Active","creationDate":"2019-03-22T10:18:37.000+0000","createdBy":"superadmin"},"customerEntity":null,"label":"CIQ_FILE_PATH","value":"/home/user","type":"PROGRAM TEMPLATE"},{"id":18,"programDetailsEntity":{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"VZN-4G-VLSM","programDescription":"VLSM","status":"Active","creationDate":"2019-03-22T10:18:37.000+0000","createdBy":"superadmin"},"customerEntity":null,"label":"CIQ_NAME_TEMPLATE","value":"UNY-NE-VZ_CIQ_Ver*.xlsx","type":"PROGRAM TEMPLATE"},{"id":19,"programDetailsEntity":{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"VZN-4G-VLSM","programDescription":"VLSM","status":"Active","creationDate":"2019-03-22T10:18:37.000+0000","createdBy":"superadmin"},"customerEntity":null,"label":"SCRIPT_FILE_PATH","value":"/home/user","type":"PROGRAM TEMPLATE"},{"id":20,"programDetailsEntity":{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"VZN-4G-VLSM","programDescription":"VLSM","status":"Active","creationDate":"2019-03-22T10:18:37.000+0000","createdBy":"superadmin"},"customerEntity":null,"label":"SCRIPT_NAME_TEMPLATE","value":"1_*.zip","type":"PROGRAM TEMPLATE"},{"id":47,"programDetailsEntity":{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"VZN-4G-VLSM","programDescription":"VLSM","status":"Active","creationDate":"2019-03-22T10:18:37.000+0000","createdBy":"superadmin"},"customerEntity":null,"label":"PARAMETERS_VALIDATE_TEMPLATE","value":"","type":"PROGRAM TEMPLATE"},{"id":8,"programDetailsEntity":{"id":3,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-MIMO","programDescription":"MIMO","status":"Active","creationDate":"2019-03-22T10:19:15.000+0000","createdBy":"superadmin"},"customerEntity":null,"label":"CIQ_VALIDATE_TEMPLATE","value":"{\"sheets\":[{\"sheetName\":\"CIQ\",\"enbIdColumnHeaderName\":\"P\",\"enbNameColumnHeaderName\":\"Q\",\"columns\":[{\"columnName\":\"BBU_Type\",\"columnHeaderName\":\"K\",\"columnAliasName\":\"BBU_Type\"},{\"columnName\":\"Cascade\",\"columnHeaderName\":\"C\",\"columnAliasName\":\"Cascade\"},{\"columnName\":\"Market\",\"columnHeaderName\":\"A\",\"columnAliasName\":\"Market\"},{\"columnName\":\"Market_ID\",\"columnHeaderName\":\"B\",\"columnAliasName\":\"Market_ID\"},{\"columnName\":\"LSMR_Name\",\"columnHeaderName\":\"L\",\"columnAliasName\":\"LSMR_Name\"},{\"columnName\":\"LSM_IP_Address_NorthBound\",\"columnHeaderName\":\"M\",\"columnAliasName\":\"LSM_IP_Address_NorthBound\"},{\"columnName\":\"Network_Site_Code\",\"columnHeaderName\":\"O\",\"columnAliasName\":\"Network_Site_Code\"},{\"columnName\":\"LSM_IP_Address_SouthBound\",\"columnHeaderName\":\"N\",\"columnAliasName\":\"LSM_IP_Address_SouthBound\"},{\"columnName\":\"eNB_ID\",\"columnHeaderName\":\"P\",\"columnAliasName\":\"eNB_ID\"},{\"columnName\":\"eNodeB_Name\",\"columnHeaderName\":\"Q\",\"columnAliasName\":\"eNodeB_Name\"},{\"columnName\":\"Cabinet\",\"columnHeaderName\":\"R\",\"columnAliasName\":\"Cabinet\"},{\"columnName\":\"Latitude\",\"columnHeaderName\":\"S\",\"columnAliasName\":\"Latitude\"},{\"columnName\":\"Latitude_NS\",\"columnHeaderName\":\"T\",\"columnAliasName\":\"Latitude_NS\"},{\"columnName\":\"Longitude\",\"columnHeaderName\":\"U\",\"columnAliasName\":\"Longitude\"},{\"columnName\":\"Longitude_WS\",\"columnHeaderName\":\"V\",\"columnAliasName\":\"Longitude_WS\"},{\"columnName\":\"TAC_Decimal\",\"columnHeaderName\":\"W\",\"columnAliasName\":\"TAC_Decimal\"},{\"columnName\":\"TAC_Hex\",\"columnHeaderName\":\"X\",\"columnAliasName\":\"TAC_Hex\"},{\"columnName\":\"No_Of_RRH\",\"columnHeaderName\":\"Y\",\"columnAliasName\":\"No_Of_RRH\"},{\"columnName\":\"RRH_Model\",\"columnHeaderName\":\"Z\",\"columnAliasName\":\"RRH_Model\"},{\"columnName\":\"RRH_Top_Bottom\",\"columnHeaderName\":\"AA\",\"columnAliasName\":\"RRH_Top_Bottom\"},{\"columnName\":\"Band\",\"columnHeaderName\":\"AB\",\"columnAliasName\":\"Band\"},{\"columnName\":\"Sector_ID\",\"columnHeaderName\":\"AC\",\"columnAliasName\":\"Sector_ID\"},{\"columnName\":\"Cell_ID\",\"columnHeaderName\":\"AD\",\"columnAliasName\":\"Cell_ID\"},{\"columnName\":\"EUTRAN_Cell_Global_Id\",\"columnHeaderName\":\"AF\",\"columnAliasName\":\"EUTRAN_Cell_Global_Id\"},{\"columnName\":\"PCI\",\"columnHeaderName\":\"AG\",\"columnAliasName\":\"PCI\"},{\"columnName\":\"RSI\",\"columnHeaderName\":\"AH\",\"columnAliasName\":\"RSI\"},{\"columnName\":\"Bandwidth\",\"columnHeaderName\":\"AI\",\"columnAliasName\":\"Bandwidth\"},{\"columnName\":\"Start_EARFCN\",\"columnHeaderName\":\"AJ\",\"columnAliasName\":\"Start_EARFCN\"},{\"columnName\":\"DL_EARFCN\",\"columnHeaderName\":\"AK\",\"columnAliasName\":\"DL_EARFCN\"},{\"columnName\":\"UL_EARFCN\",\"columnHeaderName\":\"AL\",\"columnAliasName\":\"UL_EARFCN\"},{\"columnName\":\"Diversity\",\"columnHeaderName\":\"AM\",\"columnAliasName\":\"Diversity\"},{\"columnName\":\"Initiate_SON\",\"columnHeaderName\":\"AN\",\"columnAliasName\":\"Initiate_SON\"},{\"columnName\":\"Carrier_Aggregation\",\"columnHeaderName\":\"AO\",\"columnAliasName\":\"Carrier_Aggregation\"},{\"columnName\":\"Frame_Config_Version\",\"columnHeaderName\":\"AP\",\"columnAliasName\":\"Frame_Config_Version\"},{\"columnName\":\"Antenna_Model\",\"columnHeaderName\":\"AQ\",\"columnAliasName\":\"Antenna_Model\"},{\"columnName\":\"Antenna_Vendor\",\"columnHeaderName\":\"AR\",\"columnAliasName\":\"Antenna_Vendor\"},{\"columnName\":\"Azimuth\",\"columnHeaderName\":\"AS\",\"columnAliasName\":\"Azimuth\"},{\"columnName\":\"Electrical_Tilt\",\"columnHeaderName\":\"AT\",\"columnAliasName\":\"Electrical_Tilt\"},{\"columnName\":\"Mechanical_Tilt\",\"columnHeaderName\":\"AU\",\"columnAliasName\":\"Mechanical_Tilt\"},{\"columnName\":\"Network_Mask\",\"columnHeaderName\":\"AV\",\"columnAliasName\":\"Network_Mask\"},{\"columnName\":\"CSR_HostName\",\"columnHeaderName\":\"AW\",\"columnAliasName\":\"CSR_HostName\"},{\"columnName\":\"CSR_Type\",\"columnHeaderName\":\"AX\",\"columnAliasName\":\"CSR_Type\"},{\"columnName\":\"CSR_Port\",\"columnHeaderName\":\"AY\",\"columnAliasName\":\"CSR_Port\"},{\"columnName\":\"Fiber_SFP_Needed\",\"columnHeaderName\":\"AZ\",\"columnAliasName\":\"Fiber_SFP_Needed\"},{\"columnName\":\"eNB_OAM_VLAN\",\"columnHeaderName\":\"BA\",\"columnAliasName\":\"eNB_OAM_VLAN\"},{\"columnName\":\"CSR_OAM_IP\",\"columnHeaderName\":\"BB\",\"columnAliasName\":\"CSR_OAM_IP\"},{\"columnName\":\"eNB_OAM_IP\",\"columnHeaderName\":\"BC\",\"columnAliasName\":\"eNB_OAM_IP\"},{\"columnName\":\"eNB_S_B_VLAN\",\"columnHeaderName\":\"BD\",\"columnAliasName\":\"eNB_S_B_VLAN\"},{\"columnName\":\"CSR_S_B_IP\",\"columnHeaderName\":\"BE\",\"columnAliasName\":\"CSR_S_B_IP\"},{\"columnName\":\"eNB_S_B_IP\",\"columnHeaderName\":\"BF\",\"columnAliasName\":\"eNB_S_B_IP\"},{\"columnName\":\"IP_Static_Route_1\",\"columnHeaderName\":\"BG\",\"columnAliasName\":\"IP_Static_Route_1\"},{\"columnName\":\"IP_Static_Route_2\",\"columnHeaderName\":\"BH\",\"columnAliasName\":\"IP_Static_Route_2\"},{\"columnName\":\"IP_Static_Route_3\",\"columnHeaderName\":\"BI\",\"columnAliasName\":\"IP_Static_Route_3\"},{\"columnName\":\"IP_Static_Route_4\",\"columnHeaderName\":\"BJ\",\"columnAliasName\":\"IP_Static_Route_4\"},{\"columnName\":\"IP_Static_Route_5\",\"columnHeaderName\":\"BK\",\"columnAliasName\":\"IP_Static_Route_5\"},{\"columnName\":\"IP_Static_Route_6\",\"columnHeaderName\":\"BL\",\"columnAliasName\":\"IP_Static_Route_6\"},{\"columnName\":\"IP_Static_Route_7\",\"columnHeaderName\":\"BM\",\"columnAliasName\":\"IP_Static_Route_7\"},{\"columnName\":\"IP_Static_Route_8\",\"columnHeaderName\":\"BN\",\"columnAliasName\":\"IP_Static_Route_8\"}],\"seqOrder\":1,\"sheetType\":\"normal\",\"headerRow\":1,\"readingRange\":null,\"subSheetName\":null,\"sheetAliasName\":\"FDD_TDD\",\"subSheetAliasName\":null}]}","type":"PROGRAM TEMPLATE"},{"id":9,"programDetailsEntity":{"id":3,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-MIMO","programDescription":"MIMO","status":"Active","creationDate":"2019-03-22T10:19:15.000+0000","createdBy":"superadmin"},"customerEntity":null,"label":"CHECK_LIST_VALIDATE_TEMPLATE","value":"{\"sheets\":[{\"sheetName\":\"mMIMO_FDD-TDD_PUT Method\",\"columns\":[{\"columnName\":\"Pre Checks\",\"columnHeaderName\":\"B\",\"validationRule\":\"valid\"},{\"columnName\":\"NOTES\",\"columnHeaderName\":\"C\",\"validationRule\":\"valid\"},{\"columnName\":\"<Cascade>\",\"columnHeaderName\":\"D\",\"validationRule\":\"valid\"}],\"seqOrder\":1,\"sheetType\":\"multiple\",\"headerRow\":5,\"readingRange\":\"6-9\",\"subSheetName\":\"Pre Checks\"},{\"sheetName\":\"mMIMO_FDD-TDD_PUT Method\",\"subSheetName\":\"Firmware Upgrade to 7.1.1 \\nSoftware Upgrade to 7.1.0\",\"columns\":[{\"columnName\":\"Firmware Upgrade to 7.1.1 \\nSoftware Upgrade to 7.1.0\",\"validationRule\":\"valid\",\"columnHeaderName\":\"B\"},{\"columnName\":\"NOTES\",\"validationRule\":\"valid\",\"columnHeaderName\":\"C\"},{\"columnName\":\"<Cascade>\",\"validationRule\":\"valid\",\"columnHeaderName\":\"D\"}],\"seqOrder\":1,\"sheetType\":\"multiple\",\"headerRow\":14,\"readingRange\":\"15-21\"},{\"sheetName\":\"mMIMO_FDD Only_Scripts Method\",\"subSheetName\":\"Pre Checks\",\"columns\":[{\"columnName\":\"Pre Checks\",\"validationRule\":\"valid\",\"columnHeaderName\":\"B\"},{\"columnName\":\"NOTES\",\"validationRule\":\"valid\",\"columnHeaderName\":\"C\"},{\"columnName\":\"<Cascade>\",\"validationRule\":\"valid\",\"columnHeaderName\":\"D\"}],\"seqOrder\":2,\"sheetType\":\"multiple\",\"headerRow\":5,\"readingRange\":\"6-9\"}]}","type":"PROGRAM TEMPLATE"},{"id":10,"programDetailsEntity":{"id":3,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-MIMO","programDescription":"MIMO","status":"Active","creationDate":"2019-03-22T10:19:15.000+0000","createdBy":"superadmin"},"customerEntity":null,"label":"ENB_MENU_TEMPLATE","value":"{\n  \"ciqMenu\": [\n    {\n      \"subMenu\": [\n        \"BBU_Type\",\n        \"Cascade\"\n      ],\n      \"menuName\": \"SYS_INFO\",\n      \"sheetAliasName\": \"FDD_TDD\",\n      \"subSheetAliasName\": null\n    },\n    {\n      \"subMenu\": [\n        \"Market\",\n        \"Market_ID\"\n      ],\n      \"menuName\": \"MARKET_INFO\",\n      \"sheetAliasName\": \"FDD_TDD\",\n      \"subSheetAliasName\": null\n    },\n    {\n      \"subMenu\": [\n        \"LSM_IP_Address_NorthBound\",\n        \"LSM_IP_Address_SouthBound\",\n        \"Network_Site_Code\"\n      ],\n      \"menuName\": \"IPDETAILS\",\n      \"sheetAliasName\": \"FDD_TDD\",\n      \"subSheetAliasName\": null\n    },\n    {\n     \"subMenu\": [\n        \"CSR_Port\"\n      ],\n      \"menuName\": \"PORT DETAILS\",\n      \"sheetAliasName\": \"FDD_TDD\",\n      \"subSheetAliasName\": null\n    },\n    {\n      \"subMenu\": [\n        \"Latitude\",\n        \"Latitude_NS\",\n        \"Longitude\",\n        \"Longitude_WS\"\n      ],\n      \"menuName\": \"BAND DETAILS\",\n      \"sheetAliasName\": \"FDD_TDD\",\n      \"subSheetAliasName\": null\n    }\n  ]\n}","type":"PROGRAM TEMPLATE"},{"id":44,"programDetailsEntity":{"id":3,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-MIMO","programDescription":"MIMO","status":"Active","creationDate":"2019-03-22T10:19:15.000+0000","createdBy":"superadmin"},"customerEntity":null,"label":"PARAMETERS_VALIDATE_TEMPLATE","value":"","type":"PROGRAM TEMPLATE"},{"id":11,"programDetailsEntity":{"id":4,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-CLWR_CONV","programDescription":"CLWR_CONV","status":"Active","creationDate":"2019-03-20T14:12:06.000+0000","createdBy":"superadmin"},"customerEntity":null,"label":"CIQ_VALIDATE_TEMPLATE","value":"{\n  \"sheets\": [\n    {\n      \"sheetName\": \"FDD_TDD\",\n      \"enbIdColumnHeaderName\": \"I\",\n      \"enbNameColumnHeaderName\": \"J\",\n      \"columns\": [\n        {\n          \"columnName\": \"BBU_Type\",\n          \"columnHeaderName\": \"A\",\n          \"columnAliasName\": \"BBU_Type\"\n        },\n        {\n          \"columnName\": \"Cascade\",\n          \"columnHeaderName\": \"B\",\n          \"columnAliasName\": \"Cascade\"\n        },\n        {\n          \"columnName\": \"Market\",\n          \"columnHeaderName\": \"C\",\n          \"columnAliasName\": \"Market\"\n        },\n        {\n          \"columnName\": \"Market_ID\",\n          \"columnHeaderName\": \"D\",\n          \"columnAliasName\": \"Market_ID\"\n        },\n        {\n          \"columnName\": \"LSMR_Name\",\n          \"columnHeaderName\": \"E\",\n          \"columnAliasName\": \"LSMR_Name\"\n        },\n        {\n          \"columnName\": \"LSM_IP_Address_NorthBound\",\n          \"columnHeaderName\": \"F\",\n          \"columnAliasName\": \"LSM_IP_Address_NorthBound\"\n        },\n        {\n          \"columnName\": \"Network_Site_Code\",\n          \"columnHeaderName\": \"G\",\n          \"columnAliasName\": \"Network_Site_Code\"\n        },\n        {\n          \"columnName\": \"LSM_IP_Address_SouthBound\",\n          \"columnHeaderName\": \"H\",\n          \"columnAliasName\": \"LSM_IP_Address_SouthBound\"\n        },\n        {\n          \"columnName\": \"eNB_ID\",\n          \"columnHeaderName\": \"I\",\n          \"columnAliasName\": \"eNB_ID\"\n        },\n        {\n          \"columnName\": \"eNodeB_Name\",\n          \"columnHeaderName\": \"J\",\n          \"columnAliasName\": \"eNodeB_Name\"\n        },\n        {\n          \"columnName\": \"Cabinet\",\n          \"columnHeaderName\": \"K\",\n          \"columnAliasName\": \"Cabinet\"\n        },\n        {\n          \"columnName\": \"Latitude\",\n          \"columnHeaderName\": \"L\",\n          \"columnAliasName\": \"Latitude\"\n        },\n        {\n          \"columnName\": \"Latitude_NS\",\n          \"columnHeaderName\": \"M\",\n          \"columnAliasName\": \"Latitude_NS\"\n        },\n        {\n          \"columnName\": \"Longitude\",\n          \"columnHeaderName\": \"N\",\n          \"columnAliasName\": \"Longitude\"\n        },\n        {\n          \"columnName\": \"Longitude_WS\",\n          \"columnHeaderName\": \"O\",\n          \"columnAliasName\": \"Longitude_WS\"\n        },\n        {\n          \"columnName\": \"TAC_Decimal\",\n          \"columnHeaderName\": \"P\",\n          \"columnAliasName\": \"TAC_Decimal\"\n        },\n        {\n          \"columnName\": \"TAC_Hex\",\n          \"columnHeaderName\": \"Q\",\n          \"columnAliasName\": \"TAC_Hex\"\n        },\n        {\n          \"columnName\": \"No_Of_RRH\",\n          \"columnHeaderName\": \"R\",\n          \"columnAliasName\": \"No_Of_RRH\"\n        },\n        {\n          \"columnName\": \"RRH_Model\",\n          \"columnHeaderName\": \"S\",\n          \"columnAliasName\": \"RRH_Model\"\n        },\n        {\n          \"columnName\": \"RRH_Top_Bottom\",\n          \"columnHeaderName\": \"T\",\n          \"columnAliasName\": \"RRH_Top_Bottom\"\n        },\n        {\n          \"columnName\": \"Band\",\n          \"columnHeaderName\": \"U\",\n          \"columnAliasName\": \"Band\"\n        },\n        {\n          \"columnName\": \"Sector_ID\",\n          \"columnHeaderName\": \"V\",\n          \"columnAliasName\": \"Sector_ID\"\n        },\n        {\n          \"columnName\": \"Cell_ID\",\n          \"columnHeaderName\": \"W\",\n          \"columnAliasName\": \"Cell_ID\"\n        },\n        {\n          \"columnName\": \"EUTRAN_Cell_Global_Id\",\n          \"columnHeaderName\": \"X\",\n          \"columnAliasName\": \"EUTRAN_Cell_Global_Id\"\n        },\n        {\n          \"columnName\": \"PCI\",\n          \"columnHeaderName\": \"Y\",\n          \"columnAliasName\": \"PCI\"\n        },\n        {\n          \"columnName\": \"RSI\",\n          \"columnHeaderName\": \"Z\",\n          \"columnAliasName\": \"RSI\"\n        },\n        {\n          \"columnName\": \"Bandwidth\",\n          \"columnHeaderName\": \"AA\",\n          \"columnAliasName\": \"Bandwidth\"\n        },\n        {\n          \"columnName\": \"Start_EARFCN\",\n          \"columnHeaderName\": \"AB\",\n          \"columnAliasName\": \"Start_EARFCN\"\n        },\n        {\n          \"columnName\": \"DL_EARFCN\",\n          \"columnHeaderName\": \"AC\",\n          \"columnAliasName\": \"DL_EARFCN\"\n        },\n        {\n          \"columnName\": \"UL_EARFCN\",\n          \"columnHeaderName\": \"AD\",\n          \"columnAliasName\": \"UL_EARFCN\"\n        },\n        {\n          \"columnName\": \"Diversity\",\n          \"columnHeaderName\": \"AE\",\n          \"columnAliasName\": \"Diversity\"\n        },\n        {\n          \"columnName\": \"Initiate_SON\",\n          \"columnHeaderName\": \"AF\",\n          \"columnAliasName\": \"Initiate_SON\"\n        },\n        {\n          \"columnName\": \"Carrier_Aggregation\",\n          \"columnHeaderName\": \"AG\",\n          \"columnAliasName\": \"Carrier_Aggregation\"\n        },\n        {\n          \"columnName\": \"Frame_Config_Version\",\n          \"columnHeaderName\": \"AH\",\n          \"columnAliasName\": \"Frame_Config_Version\"\n        },\n        {\n          \"columnName\": \"Antenna_Model\",\n          \"columnHeaderName\": \"AI\",\n          \"columnAliasName\": \"Antenna_Model\"\n        },\n        {\n          \"columnName\": \"Antenna_Vendor\",\n          \"columnHeaderName\": \"AJ\",\n          \"columnAliasName\": \"Antenna_Vendor\"\n        },\n        {\n          \"columnName\": \"Azimuth\",\n          \"columnHeaderName\": \"AK\",\n          \"columnAliasName\": \"Azimuth\"\n        },\n        {\n          \"columnName\": \"Electrical_Tilt\",\n          \"columnHeaderName\": \"AL\",\n          \"columnAliasName\": \"Electrical_Tilt\"\n        },\n        {\n          \"columnName\": \"Mechanical_Tilt\",\n          \"columnHeaderName\": \"AM\",\n          \"columnAliasName\": \"Mechanical_Tilt\"\n        },\n        {\n          \"columnName\": \"Network_Mask\",\n          \"columnHeaderName\": \"AN\",\n          \"columnAliasName\": \"Network_Mask\"\n        },\n        {\n          \"columnName\": \"CSR_HostName\",\n          \"columnHeaderName\": \"AO\",\n          \"columnAliasName\": \"CSR_HostName\"\n        },\n        {\n          \"columnName\": \"CSR_Type\",\n          \"columnHeaderName\": \"AP\",\n          \"columnAliasName\": \"CSR_Type\"\n        },\n        {\n          \"columnName\": \"CSR_Port\",\n          \"columnHeaderName\": \"AQ\",\n          \"columnAliasName\": \"CSR_Port\"\n        },\n        {\n          \"columnName\": \"Fiber_SFP_Needed\",\n          \"columnHeaderName\": \"AR\",\n          \"columnAliasName\": \"Fiber_SFP_Needed\"\n        },\n        {\n          \"columnName\": \"eNB_OAM_VLAN\",\n          \"columnHeaderName\": \"AS\",\n          \"columnAliasName\": \"eNB_OAM_VLAN\"\n        },\n        {\n          \"columnName\": \"CSR_OAM_IP\",\n          \"columnHeaderName\": \"AT\",\n          \"columnAliasName\": \"CSR_OAM_IP\"\n        },\n        {\n          \"columnName\": \"eNB_OAM_IP\",\n          \"columnHeaderName\": \"AU\",\n          \"columnAliasName\": \"eNB_OAM_IP\"\n        },\n        {\n          \"columnName\": \"eNB_S_B_VLAN\",\n          \"columnHeaderName\": \"AV\",\n          \"columnAliasName\": \"eNB_S_B_VLAN\"\n        },\n        {\n          \"columnName\": \"CSR_S_B_IP\",\n          \"columnHeaderName\": \"AW\",\n          \"columnAliasName\": \"CSR_S_B_IP\"\n        },\n        {\n          \"columnName\": \"eNB_S_B_IP\",\n          \"columnHeaderName\": \"AX\",\n          \"columnAliasName\": \"eNB_S_B_IP\"\n        },\n        {\n          \"columnName\": \"IP_Static_Route_1\",\n          \"columnHeaderName\": \"AY\",\n          \"columnAliasName\": \"IP_Static_Route_1\"\n        },\n        {\n          \"columnName\": \"IP_Static_Route_2\",\n          \"columnHeaderName\": \"AZ\",\n          \"columnAliasName\": \"IP_Static_Route_2\"\n        },\n        {\n          \"columnName\": \"IP_Static_Route_3\",\n          \"columnHeaderName\": \"BA\",\n          \"columnAliasName\": \"IP_Static_Route_3\"\n        },\n        {\n          \"columnName\": \"IP_Static_Route_4\",\n          \"columnHeaderName\": \"BB\",\n          \"columnAliasName\": \"IP_Static_Route_4\"\n        },\n        {\n          \"columnName\": \"IP_Static_Route_5\",\n          \"columnHeaderName\": \"BC\",\n          \"columnAliasName\": \"IP_Static_Route_5\"\n        },\n        {\n          \"columnName\": \"IP_Static_Route_6\",\n          \"columnHeaderName\": \"BD\",\n          \"columnAliasName\": \"IP_Static_Route_6\"\n        },\n        {\n          \"columnName\": \"IP_Static_Route_7\",\n          \"columnHeaderName\": \"BE\",\n          \"columnAliasName\": \"IP_Static_Route_7\"\n        },\n        {\n          \"columnName\": \"IP_Static_Route_8\",\n          \"columnHeaderName\": \"BF\",\n          \"columnAliasName\": \"IP_Static_Route_8\"\n        },\n        {\n          \"columnName\": \"D1 LSM_Name\",\n          \"columnHeaderName\": \"BG\",\n          \"columnAliasName\": \"D1_LSM_Name\"\n        },\n        {\n          \"columnName\": \"D1 LSM_IP_Address\",\n          \"columnHeaderName\": \"BH\",\n          \"columnAliasName\": \"D1_LSM_IP_Address\"\n        },\n        {\n          \"columnName\": \"D1 Augment ID\",\n          \"columnHeaderName\": \"BI\",\n          \"columnAliasName\": \"D1_Augment_ID\"\n        },\n        {\n          \"columnName\": \"D1 eNB_ID\",\n          \"columnHeaderName\": \"BJ\",\n          \"columnAliasName\": \"D1_eNB_ID\"\n        },\n        {\n          \"columnName\": \"D1 eNB OAM IP\",\n          \"columnHeaderName\": \"BK\",\n          \"columnAliasName\": \"D1_eNB_OAM_IP\"\n        },\n        {\n          \"columnName\": \"D1 CSR OAM IP\",\n          \"columnHeaderName\": \"BL\",\n          \"columnAliasName\": \"D1_CSR_OAM_IP\"\n        },\n        {\n          \"columnName\": \"MME Info 1\",\n          \"columnHeaderName\": \"BM\",\n          \"columnAliasName\": \"MME_Info_1\"\n        },\n        {\n          \"columnName\": \"MME Info 2\",\n          \"columnHeaderName\": \"BN\",\n          \"columnAliasName\": \"MME_Info_2\"\n        },\n        {\n          \"columnName\": \"MME Info 3\",\n          \"columnHeaderName\": \"BO\",\n          \"columnAliasName\": \"MME_Info_3\"\n        },\n        {\n          \"columnName\": \"MME Info 4\",\n          \"columnHeaderName\": \"BP\",\n          \"columnAliasName\": \"MME_Info_4\"\n        },\n        {\n          \"columnName\": \"MME Info 5\",\n          \"columnHeaderName\": \"BQ\",\n          \"columnAliasName\": \"MME_Info_5\"\n        },\n        {\n          \"columnName\": \"MME Info 6\",\n          \"columnHeaderName\": \"BR\",\n          \"columnAliasName\": \"MME_Info_6\"\n        },\n        {\n          \"columnName\": \"MME Info 7\",\n          \"columnHeaderName\": \"BS\",\n          \"columnAliasName\": \"MME_Info_7\"\n        },\n        {\n          \"columnName\": \"MME Info 8\",\n          \"columnHeaderName\": \"BT\",\n          \"columnAliasName\": \"MME_Info_8\"\n        },\n        {\n          \"columnName\": \"MME Info 9\",\n          \"columnHeaderName\": \"BU\",\n          \"columnAliasName\": \"MME_Info_9\"\n        },\n        {\n          \"columnName\": \"MME Info 10\",\n          \"columnHeaderName\": \"BV\",\n          \"columnAliasName\": \"MME_Info_10\"\n        },\n        {\n          \"columnName\": \"MME Info 11\",\n          \"columnHeaderName\": \"BW\",\n          \"columnAliasName\": \"MME_Info_11\"\n        },\n        {\n          \"columnName\": \"MME Info 12\",\n          \"columnHeaderName\": \"BX\",\n          \"columnAliasName\": \"MME_Info_12\"\n        },\n        {\n          \"columnName\": \"MME Info 13\",\n          \"columnHeaderName\": \"BY\",\n          \"columnAliasName\": \"MME_Info_13\"\n        }\n      ],\n      \"seqOrder\": 1,\n      \"sheetType\": \"normal\",\n      \"headerRow\": 1,\n      \"readingRange\": null,\n      \"subSheetName\": null,\n      \"sheetAliasName\": \"FDD_TDD\",\n      \"subSheetAliasName\": null\n    }\n  ]\n}\n","type":"PROGRAM TEMPLATE"},{"id":12,"programDetailsEntity":{"id":4,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-CLWR_CONV","programDescription":"CLWR_CONV","status":"Active","creationDate":"2019-03-20T14:12:06.000+0000","createdBy":"superadmin"},"customerEntity":null,"label":"CHECK_LIST_VALIDATE_TEMPLATE","value":"{\n  \"sheets\": [\n    {\n      \"sheetName\": \"mMIMO_FDD-TDD8.5.3 ScriptMethod\",\n      \"columns\": [\n        {\n          \"columnName\": \"Pre Checks\",\n          \"columnHeaderName\": \"B\",\n          \"validationRule\": \"valid\"\n        },\n        {\n          \"columnName\": \"NOTES\",\n          \"columnHeaderName\": \"C\",\n          \"validationRule\": \"valid\"\n        },\n        {\n          \"columnName\": \"<Cascade>\",\n          \"columnHeaderName\": \"D\",\n          \"validationRule\": \"valid\"\n        }\n      ],\n      \"seqOrder\": 1,\n      \"sheetType\": \"multiple\",\n      \"headerRow\": 1,\n      \"readingRange\": \"2-45\",\n      \"subSheetName\": \"Pre Checks - Post Checks\"\n    },\n    {\n      \"sheetName\": \"mMIMO_FDD Only_Scripts Method\",\n      \"columns\": [\n        {\n          \"columnName\": \"Pre Checks\",\n          \"columnHeaderName\": \"B\",\n          \"validationRule\": \"valid\"\n        },\n        {\n          \"columnName\": \"NOTES\",\n          \"columnHeaderName\": \"C\",\n          \"validationRule\": \"valid\"\n        },\n        {\n          \"columnName\": \"<Cascade>\",\n          \"columnHeaderName\": \"D\",\n          \"validationRule\": \"valid\"\n        }\n      ],\n      \"seqOrder\": 3,\n      \"sheetType\": \"multiple\",\n      \"headerRow\": 2,\n      \"readingRange\": \"3-47\",\n      \"subSheetName\": \"Pre Checks - Post Checks\"\n    },\n    {\n      \"sheetName\": \"New Site Build\",\n      \"columns\": [\n        {\n          \"columnName\": \"Pre Checks\",\n          \"columnHeaderName\": \"B\",\n          \"validationRule\": \"valid\"\n        },\n        {\n          \"columnName\": \"NOTES\",\n          \"columnHeaderName\": \"C\",\n          \"validationRule\": \"valid\"\n        },\n        {\n          \"columnName\": \"<Cascade>\",\n          \"columnHeaderName\": \"D\",\n          \"validationRule\": \"valid\"\n        }\n      ],\n      \"seqOrder\": 3,\n      \"sheetType\": \"multiple\",\n      \"headerRow\": 4,\n      \"readingRange\": \"5-54\",\n      \"subSheetName\": \"Pre Checks - Post Checks\"\n    },\n    {\n      \"sheetName\": \"D1 to D2 ClearWire\",\n      \"columns\": [\n        {\n          \"columnName\": \"Pre Checks\",\n          \"columnHeaderName\": \"B\",\n          \"validationRule\": \"valid\"\n        },\n        {\n          \"columnName\": \"NOTES\",\n          \"columnHeaderName\": \"C\",\n          \"validationRule\": \"valid\"\n        },\n        {\n          \"columnName\": \"<Cascade>\",\n          \"columnHeaderName\": \"D\",\n          \"validationRule\": \"valid\"\n        }\n      ],\n      \"seqOrder\": 4,\n      \"sheetType\": \"multiple\",\n      \"headerRow\": 3,\n      \"readingRange\": \"4-56\",\n      \"subSheetName\": \"Pre Checks - Post Checks\"\n    }\n  ]\n}","type":"PROGRAM TEMPLATE"},{"id":13,"programDetailsEntity":{"id":4,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-CLWR_CONV","programDescription":"CLWR_CONV","status":"Active","creationDate":"2019-03-20T14:12:06.000+0000","createdBy":"superadmin"},"customerEntity":null,"label":"ENB_MENU_TEMPLATE","value":"{\n  \"ciqMenu\": [\n    {\n      \"subMenu\": [\n        \"BBU_Type\",\n        \"Cascade\"\n      ],\n      \"menuName\": \"SYS_INFO\",\n      \"sheetAliasName\": \"FDD_TDD\",\n      \"subSheetAliasName\": null\n    },\n    {\n      \"subMenu\": [\n        \"Market\",\n        \"Market_ID\"\n      ],\n      \"menuName\": \"MARKET_INFO\",\n      \"sheetAliasName\": \"FDD_TDD\",\n      \"subSheetAliasName\": null\n    },\n    {\n      \"subMenu\": [\n        \"LSM_IP_Address_NorthBound\",\n        \"LSM_IP_Address_SouthBound\",\n        \"Network_Site_Code\"\n      ],\n      \"menuName\": \"IPDETAILS\",\n      \"sheetAliasName\": \"FDD_TDD\",\n      \"subSheetAliasName\": null\n    },\n    {\n      \"subMenu\": [\n        \"CSR_Port\"\n      ],\n      \"menuName\": \"PORT DETAILS\",\n      \"sheetAliasName\": \"FDD_TDD\",\n      \"subSheetAliasName\": null\n    },\n    {\n      \"subMenu\": [\n        \"Latitude\",\n        \"Latitude_NS\",\n        \"Longitude\",\n        \"Longitude_WS\"\n      ],\n      \"menuName\": \"BAND DETAILS\",\n      \"sheetAliasName\": \"FDD_TDD\",\n      \"subSheetAliasName\": null\n    }\n  ]\n}","type":"PROGRAM TEMPLATE"},{"id":45,"programDetailsEntity":{"id":4,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-CLWR_CONV","programDescription":"CLWR_CONV","status":"Active","creationDate":"2019-03-20T14:12:06.000+0000","createdBy":"superadmin"},"customerEntity":null,"label":"PARAMETERS_VALIDATE_TEMPLATE","value":"","type":"PROGRAM TEMPLATE"},{"id":56,"programDetailsEntity":{"id":26,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"VZN-4G-Test","programDescription":"Test","status":"Active","creationDate":"2019-04-01T10:33:31.000+0000","createdBy":"superadmin"},"customerEntity":null,"label":"CIQ_VALIDATE_TEMPLATE","value":"","type":"PROGRAM TEMPLATE"},{"id":57,"programDetailsEntity":{"id":26,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"VZN-4G-Test","programDescription":"Test","status":"Active","creationDate":"2019-04-01T10:33:31.000+0000","createdBy":"superadmin"},"customerEntity":null,"label":"CHECK_LIST_VALIDATE_TEMPLATE","value":"","type":"PROGRAM TEMPLATE"},{"id":58,"programDetailsEntity":{"id":26,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"VZN-4G-Test","programDescription":"Test","status":"Active","creationDate":"2019-04-01T10:33:31.000+0000","createdBy":"superadmin"},"customerEntity":null,"label":"ENB_MENU_TEMPLATE","value":"","type":"PROGRAM TEMPLATE"},{"id":59,"programDetailsEntity":{"id":26,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"VZN-4G-Test","programDescription":"Test","status":"Active","creationDate":"2019-04-01T10:33:31.000+0000","createdBy":"superadmin"},"customerEntity":null,"label":"PARAMETERS_VALIDATE_TEMPLATE","value":"","type":"PROGRAM TEMPLATE"},{"id":60,"programDetailsEntity":{"id":26,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"VZN-4G-Test","programDescription":"Test","status":"Active","creationDate":"2019-04-01T10:33:31.000+0000","createdBy":"superadmin"},"customerEntity":null,"label":"SCRIPT_STORE_TEMPLATE","value":"","type":"PROGRAM TEMPLATE"},{"id":5,"programDetailsEntity":null,"customerEntity":{"id":2,"customerName":"Verizon","iconPath":"/customer/verizon_ 03282019_12_04_19_icon.png","status":"Active","customerShortName":"VZN","customerDetails":[{"id":1,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"VZN-4G-CDU30","programDescription":"CDU30","status":"Active","creationDate":"2019-04-01T10:35:23.000+0000","createdBy":"superadmin"},{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"VZN-4G-VLSM","programDescription":"VLSM","status":"Active","creationDate":"2019-03-22T10:18:37.000+0000","createdBy":"superadmin"},{"id":26,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"VZN-4G-Test","programDescription":"Test","status":"Active","creationDate":"2019-04-01T10:33:31.000+0000","createdBy":"superadmin"}]},"label":"SCHEDULING_MARKET","value":"{\"MarketData\":{\"market\":[\"Upstate New York\",\"New England\"]}}","type":"S & R"},{"id":1,"programDetailsEntity":null,"customerEntity":{"id":4,"customerName":"Sprint","iconPath":"/customer/sprint_ 03222019_15_49_01_icon.png","status":"Active","customerShortName":"SPT","customerDetails":[{"id":3,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-MIMO","programDescription":"MIMO","status":"Active","creationDate":"2019-03-22T10:19:15.000+0000","createdBy":"superadmin"},{"id":3,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-MIMO","programDescription":"MIMO","status":"Active","creationDate":"2019-03-22T10:19:15.000+0000","createdBy":"superadmin"},{"id":3,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-MIMO","programDescription":"MIMO","status":"Active","creationDate":"2019-03-22T10:19:15.000+0000","createdBy":"superadmin"},{"id":3,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-MIMO","programDescription":"MIMO","status":"Active","creationDate":"2019-03-22T10:19:15.000+0000","createdBy":"superadmin"},{"id":3,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-MIMO","programDescription":"MIMO","status":"Active","creationDate":"2019-03-22T10:19:15.000+0000","createdBy":"superadmin"},{"id":4,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-CLWR_CONV","programDescription":"CLWR_CONV","status":"Active","creationDate":"2019-03-20T14:12:06.000+0000","createdBy":"superadmin"},{"id":4,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-CLWR_CONV","programDescription":"CLWR_CONV","status":"Active","creationDate":"2019-03-20T14:12:06.000+0000","createdBy":"superadmin"},{"id":4,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-CLWR_CONV","programDescription":"CLWR_CONV","status":"Active","creationDate":"2019-03-20T14:12:06.000+0000","createdBy":"superadmin"},{"id":4,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-CLWR_CONV","programDescription":"CLWR_CONV","status":"Active","creationDate":"2019-03-20T14:12:06.000+0000","createdBy":"superadmin"},{"id":4,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-CLWR_CONV","programDescription":"CLWR_CONV","status":"Active","creationDate":"2019-03-20T14:12:06.000+0000","createdBy":"superadmin"}]},"label":"SCHEDULING_MARKET","value":"{\"MarketData\":{\"market\":[\"buffalo\",\"Central Illinois\",\"Central Iowa\",\"Central Pennsylvania\",\"Chicago\",\"Cincinnati\",\"Cleveland\",\"Columbus\",\"East Michigan\",\"Ft. Wayne/South Bend\",\"Indianpolis\",\"Milwaukee\",\"North Wisconsin\",\"Pittsburgh\",\"Toledo\",\"West Iowa/Nebraska\",\"West Michigan\",\"Western Pennsylvania\"]}}","type":"S & R"},{"id":6,"programDetailsEntity":null,"customerEntity":{"id":4,"customerName":"Sprint","iconPath":"/customer/sprint_ 03222019_15_49_01_icon.png","status":"Active","customerShortName":"SPT","customerDetails":[{"id":3,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-MIMO","programDescription":"MIMO","status":"Active","creationDate":"2019-03-22T10:19:15.000+0000","createdBy":"superadmin"},{"id":3,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-MIMO","programDescription":"MIMO","status":"Active","creationDate":"2019-03-22T10:19:15.000+0000","createdBy":"superadmin"},{"id":3,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-MIMO","programDescription":"MIMO","status":"Active","creationDate":"2019-03-22T10:19:15.000+0000","createdBy":"superadmin"},{"id":3,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-MIMO","programDescription":"MIMO","status":"Active","creationDate":"2019-03-22T10:19:15.000+0000","createdBy":"superadmin"},{"id":3,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-MIMO","programDescription":"MIMO","status":"Active","creationDate":"2019-03-22T10:19:15.000+0000","createdBy":"superadmin"},{"id":4,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-CLWR_CONV","programDescription":"CLWR_CONV","status":"Active","creationDate":"2019-03-20T14:12:06.000+0000","createdBy":"superadmin"},{"id":4,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-CLWR_CONV","programDescription":"CLWR_CONV","status":"Active","creationDate":"2019-03-20T14:12:06.000+0000","createdBy":"superadmin"},{"id":4,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-CLWR_CONV","programDescription":"CLWR_CONV","status":"Active","creationDate":"2019-03-20T14:12:06.000+0000","createdBy":"superadmin"},{"id":4,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-CLWR_CONV","programDescription":"CLWR_CONV","status":"Active","creationDate":"2019-03-20T14:12:06.000+0000","createdBy":"superadmin"},{"id":4,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-CLWR_CONV","programDescription":"CLWR_CONV","status":"Active","creationDate":"2019-03-20T14:12:06.000+0000","createdBy":"superadmin"}]},"label":"SCHEDULING_REGION","value":"{\"RegionData\":{\"region\":[\"Central\",\"West\"]}}","type":"S & R"},{"id":7,"programDetailsEntity":null,"customerEntity":{"id":4,"customerName":"Sprint","iconPath":"/customer/sprint_ 03222019_15_49_01_icon.png","status":"Active","customerShortName":"SPT","customerDetails":[{"id":3,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-MIMO","programDescription":"MIMO","status":"Active","creationDate":"2019-03-22T10:19:15.000+0000","createdBy":"superadmin"},{"id":3,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-MIMO","programDescription":"MIMO","status":"Active","creationDate":"2019-03-22T10:19:15.000+0000","createdBy":"superadmin"},{"id":3,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-MIMO","programDescription":"MIMO","status":"Active","creationDate":"2019-03-22T10:19:15.000+0000","createdBy":"superadmin"},{"id":3,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-MIMO","programDescription":"MIMO","status":"Active","creationDate":"2019-03-22T10:19:15.000+0000","createdBy":"superadmin"},{"id":3,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-MIMO","programDescription":"MIMO","status":"Active","creationDate":"2019-03-22T10:19:15.000+0000","createdBy":"superadmin"},{"id":4,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-CLWR_CONV","programDescription":"CLWR_CONV","status":"Active","creationDate":"2019-03-20T14:12:06.000+0000","createdBy":"superadmin"},{"id":4,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-CLWR_CONV","programDescription":"CLWR_CONV","status":"Active","creationDate":"2019-03-20T14:12:06.000+0000","createdBy":"superadmin"},{"id":4,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-CLWR_CONV","programDescription":"CLWR_CONV","status":"Active","creationDate":"2019-03-20T14:12:06.000+0000","createdBy":"superadmin"},{"id":4,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-CLWR_CONV","programDescription":"CLWR_CONV","status":"Active","creationDate":"2019-03-20T14:12:06.000+0000","createdBy":"superadmin"},{"id":4,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-CLWR_CONV","programDescription":"CLWR_CONV","status":"Active","creationDate":"2019-03-20T14:12:06.000+0000","createdBy":"superadmin"}]},"label":"SCHEDULING_FEREGION","value":"{\"FeregionData\":{\"feRegion\":[\"FECentral\",\"FEWest\"]}}","type":"S & R"},{"id":8,"programDetailsEntity":null,"customerEntity":{"id":4,"customerName":"Sprint","iconPath":"/customer/sprint_ 03222019_15_49_01_icon.png","status":"Active","customerShortName":"SPT","customerDetails":[{"id":3,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-MIMO","programDescription":"MIMO","status":"Active","creationDate":"2019-03-22T10:19:15.000+0000","createdBy":"superadmin"},{"id":3,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-MIMO","programDescription":"MIMO","status":"Active","creationDate":"2019-03-22T10:19:15.000+0000","createdBy":"superadmin"},{"id":3,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-MIMO","programDescription":"MIMO","status":"Active","creationDate":"2019-03-22T10:19:15.000+0000","createdBy":"superadmin"},{"id":3,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-MIMO","programDescription":"MIMO","status":"Active","creationDate":"2019-03-22T10:19:15.000+0000","createdBy":"superadmin"},{"id":3,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-MIMO","programDescription":"MIMO","status":"Active","creationDate":"2019-03-22T10:19:15.000+0000","createdBy":"superadmin"},{"id":4,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-CLWR_CONV","programDescription":"CLWR_CONV","status":"Active","creationDate":"2019-03-20T14:12:06.000+0000","createdBy":"superadmin"},{"id":4,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-CLWR_CONV","programDescription":"CLWR_CONV","status":"Active","creationDate":"2019-03-20T14:12:06.000+0000","createdBy":"superadmin"},{"id":4,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-CLWR_CONV","programDescription":"CLWR_CONV","status":"Active","creationDate":"2019-03-20T14:12:06.000+0000","createdBy":"superadmin"},{"id":4,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-CLWR_CONV","programDescription":"CLWR_CONV","status":"Active","creationDate":"2019-03-20T14:12:06.000+0000","createdBy":"superadmin"},{"id":4,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-CLWR_CONV","programDescription":"CLWR_CONV","status":"Active","creationDate":"2019-03-20T14:12:06.000+0000","createdBy":"superadmin"}]},"label":"SCHEDULING_FE_NIGHT","value":"{}","type":"S & R"},{"id":9,"programDetailsEntity":null,"customerEntity":{"id":4,"customerName":"Sprint","iconPath":"/customer/sprint_ 03222019_15_49_01_icon.png","status":"Active","customerShortName":"SPT","customerDetails":[{"id":3,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-MIMO","programDescription":"MIMO","status":"Active","creationDate":"2019-03-22T10:19:15.000+0000","createdBy":"superadmin"},{"id":3,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-MIMO","programDescription":"MIMO","status":"Active","creationDate":"2019-03-22T10:19:15.000+0000","createdBy":"superadmin"},{"id":3,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-MIMO","programDescription":"MIMO","status":"Active","creationDate":"2019-03-22T10:19:15.000+0000","createdBy":"superadmin"},{"id":3,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-MIMO","programDescription":"MIMO","status":"Active","creationDate":"2019-03-22T10:19:15.000+0000","createdBy":"superadmin"},{"id":3,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-MIMO","programDescription":"MIMO","status":"Active","creationDate":"2019-03-22T10:19:15.000+0000","createdBy":"superadmin"},{"id":4,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-CLWR_CONV","programDescription":"CLWR_CONV","status":"Active","creationDate":"2019-03-20T14:12:06.000+0000","createdBy":"superadmin"},{"id":4,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-CLWR_CONV","programDescription":"CLWR_CONV","status":"Active","creationDate":"2019-03-20T14:12:06.000+0000","createdBy":"superadmin"},{"id":4,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-CLWR_CONV","programDescription":"CLWR_CONV","status":"Active","creationDate":"2019-03-20T14:12:06.000+0000","createdBy":"superadmin"},{"id":4,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-CLWR_CONV","programDescription":"CLWR_CONV","status":"Active","creationDate":"2019-03-20T14:12:06.000+0000","createdBy":"superadmin"},{"id":4,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-CLWR_CONV","programDescription":"CLWR_CONV","status":"Active","creationDate":"2019-03-20T14:12:06.000+0000","createdBy":"superadmin"}]},"label":"SCHEDULING_FE_DAY","value":"{}","type":"S & R"}],"sessionId":"196234ec","serviceToken":"58975","status":"SUCCESS"};
              //SnR
              this.tableData = {"sessionId":"d5eeae44","serviceToken":"79792","status":"SUCCESS","ConfigDetails":[{"id":449,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-11-24T14:45:38.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"customerEntity":null,"label":"MAIL_CONFIGURATION","value":"","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":""},{"id":450,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-11-24T14:45:38.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"customerEntity":null,"label":"FETCH_SCHEDULE","value":"OFF","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":"time"},{"id":451,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-11-24T14:45:38.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"customerEntity":null,"label":"PREMIGRATION_SCHEDULE","value":"D-0","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":"dropdown"},{"id":452,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-11-24T14:45:38.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"customerEntity":null,"label":"NE_GROW_SCHEDULE","value":"D-0,D-2|19:56","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":"dropdown"},{"id":453,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-11-24T14:45:38.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"customerEntity":null,"label":"MIGRATION_SCHEDULE","value":"D-0","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":"dropdown"},{"id":454,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-11-24T14:45:38.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"customerEntity":null,"label":"POST_MIGRATION_AUDIT_SCHEDULE","value":"D-0","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":"dropdown"},{"id":455,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-11-24T14:45:38.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"customerEntity":null,"label":"POST_MIGRATION_RANATP_SCHEDULE","value":"OFF","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":"dropdown"},{"id":456,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-11-24T14:45:38.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"customerEntity":null,"label":"NE_GROW_AUTOMATION","value":"ON","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":""},{"id":457,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-11-24T14:45:38.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"customerEntity":null,"label":"FETCH_FROM_RFDB","value":"OFF","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":""},{"id":463,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-11-24T14:45:38.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"customerEntity":null,"label":"MIGRATION_USECASES","value":"CommissionScriptUsecase,RFUsecase","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":""},{"id":464,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-11-24T14:45:38.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"customerEntity":null,"label":"NE_GROW_USECASES","value":"GrowEnb","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":""},{"id":507,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-11-24T14:45:38.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"customerEntity":null,"label":"FETCH_DATE","value":"","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":""},{"id":512,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-11-24T14:45:38.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"customerEntity":null,"label":"KEY_VALUES","value":"{\"KeyValues\":{\"MigrationCI\":\"ON\",\"growElements\":\"ON\",\"generatENV\":\"ON\",\"CIQIntegration\":\"ON\"}}","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":""},{"id":516,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-11-24T14:45:38.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"customerEntity":null,"label":"POST_MIG_USECASES","value":"OcnsTest_Usecase,OranAudit_Usecase","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":""},{"id":439,"programDetailsEntity":{"id":40,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-MM","programDescription":"MM","status":"Active","creationDate":"2021-11-25T11:06:07.000+0000","createdBy":"superadmin","sourceProgramId":34,"sourceprogramName":"VZN-4G-USM-LIVE"},"customerEntity":null,"label":"MAIL_CONFIGURATION","value":"","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":""},{"id":440,"programDetailsEntity":{"id":40,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-MM","programDescription":"MM","status":"Active","creationDate":"2021-11-25T11:06:07.000+0000","createdBy":"superadmin","sourceProgramId":34,"sourceprogramName":"VZN-4G-USM-LIVE"},"customerEntity":null,"label":"FETCH_SCHEDULE","value":"OFF","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":"time"},{"id":441,"programDetailsEntity":{"id":40,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-MM","programDescription":"MM","status":"Active","creationDate":"2021-11-25T11:06:07.000+0000","createdBy":"superadmin","sourceProgramId":34,"sourceprogramName":"VZN-4G-USM-LIVE"},"customerEntity":null,"label":"PREMIGRATION_SCHEDULE","value":"D-0","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":"dropdown"},{"id":442,"programDetailsEntity":{"id":40,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-MM","programDescription":"MM","status":"Active","creationDate":"2021-11-25T11:06:07.000+0000","createdBy":"superadmin","sourceProgramId":34,"sourceprogramName":"VZN-4G-USM-LIVE"},"customerEntity":null,"label":"NE_GROW_SCHEDULE","value":"D-0","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":"dropdown"},{"id":443,"programDetailsEntity":{"id":40,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-MM","programDescription":"MM","status":"Active","creationDate":"2021-11-25T11:06:07.000+0000","createdBy":"superadmin","sourceProgramId":34,"sourceprogramName":"VZN-4G-USM-LIVE"},"customerEntity":null,"label":"MIGRATION_SCHEDULE","value":"D-0","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":"dropdown"},{"id":444,"programDetailsEntity":{"id":40,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-MM","programDescription":"MM","status":"Active","creationDate":"2021-11-25T11:06:07.000+0000","createdBy":"superadmin","sourceProgramId":34,"sourceprogramName":"VZN-4G-USM-LIVE"},"customerEntity":null,"label":"POST_MIGRATION_AUDIT_SCHEDULE","value":"D-0","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":"dropdown"},{"id":445,"programDetailsEntity":{"id":40,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-MM","programDescription":"MM","status":"Active","creationDate":"2021-11-25T11:06:07.000+0000","createdBy":"superadmin","sourceProgramId":34,"sourceprogramName":"VZN-4G-USM-LIVE"},"customerEntity":null,"label":"POST_MIGRATION_RANATP_SCHEDULE","value":"","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":"dropdown"},{"id":446,"programDetailsEntity":{"id":40,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-MM","programDescription":"MM","status":"Active","creationDate":"2021-11-25T11:06:07.000+0000","createdBy":"superadmin","sourceProgramId":34,"sourceprogramName":"VZN-4G-USM-LIVE"},"customerEntity":null,"label":"NE_GROW_AUTOMATION","value":"ON","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":""},{"id":447,"programDetailsEntity":{"id":40,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-MM","programDescription":"MM","status":"Active","creationDate":"2021-11-25T11:06:07.000+0000","createdBy":"superadmin","sourceProgramId":34,"sourceprogramName":"VZN-4G-USM-LIVE"},"customerEntity":null,"label":"FETCH_FROM_RFDB","value":"OFF","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":""},{"id":465,"programDetailsEntity":{"id":40,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-MM","programDescription":"MM","status":"Active","creationDate":"2021-11-25T11:06:07.000+0000","createdBy":"superadmin","sourceProgramId":34,"sourceprogramName":"VZN-4G-USM-LIVE"},"customerEntity":null,"label":"MIGRATION_USECASES","value":"CSL_Usecase,RF_Scripts_Usecase,Anchor_CSL_UseCase","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":""},{"id":466,"programDetailsEntity":{"id":40,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-MM","programDescription":"MM","status":"Active","creationDate":"2021-11-25T11:06:07.000+0000","createdBy":"superadmin","sourceProgramId":34,"sourceprogramName":"VZN-4G-USM-LIVE"},"customerEntity":null,"label":"NE_GROW_USECASES","value":"pnp,AUCaCell","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":""},{"id":510,"programDetailsEntity":{"id":40,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-MM","programDescription":"MM","status":"Active","creationDate":"2021-11-25T11:06:07.000+0000","createdBy":"superadmin","sourceProgramId":34,"sourceprogramName":"VZN-4G-USM-LIVE"},"customerEntity":null,"label":"FETCH_DATE","value":"","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":""},{"id":511,"programDetailsEntity":{"id":40,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-MM","programDescription":"MM","status":"Active","creationDate":"2021-11-25T11:06:07.000+0000","createdBy":"superadmin","sourceProgramId":34,"sourceprogramName":"VZN-4G-USM-LIVE"},"customerEntity":null,"label":"KEY_VALUES","value":"{\"KeyValues\":{\"MigrationCI\":\"ON\",\"growElements\":\"OFF\",\"generatENV\":\"ON\",\"CIQIntegration\":\"ON\"}}","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":""},{"id":517,"programDetailsEntity":{"id":40,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-MM","programDescription":"MM","status":"Active","creationDate":"2021-11-25T11:06:07.000+0000","createdBy":"superadmin","sourceProgramId":34,"sourceprogramName":"VZN-4G-USM-LIVE"},"customerEntity":null,"label":"POST_MIG_USECASES","value":"ACPF_Usecase_21B,TWAMP_Usecase_21B,AU_Usecase_21B","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":""},{"id":492,"programDetailsEntity":{"id":42,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-DSS","programDescription":"DSS","status":"Active","creationDate":"2021-11-25T11:07:05.000+0000","createdBy":"superadmin","sourceProgramId":null,"sourceprogramName":null},"customerEntity":null,"label":"MAIL_CONFIGURATION","value":"","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":""},{"id":493,"programDetailsEntity":{"id":42,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-DSS","programDescription":"DSS","status":"Active","creationDate":"2021-11-25T11:07:05.000+0000","createdBy":"superadmin","sourceProgramId":null,"sourceprogramName":null},"customerEntity":null,"label":"FETCH_SCHEDULE","value":"OFF","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":"time"},{"id":494,"programDetailsEntity":{"id":42,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-DSS","programDescription":"DSS","status":"Active","creationDate":"2021-11-25T11:07:05.000+0000","createdBy":"superadmin","sourceProgramId":null,"sourceprogramName":null},"customerEntity":null,"label":"PREMIGRATION_SCHEDULE","value":"D-0","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":"dropdown"},{"id":495,"programDetailsEntity":{"id":42,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-DSS","programDescription":"DSS","status":"Active","creationDate":"2021-11-25T11:07:05.000+0000","createdBy":"superadmin","sourceProgramId":null,"sourceprogramName":null},"customerEntity":null,"label":"NE_GROW_SCHEDULE","value":"","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":"dropdown"},{"id":496,"programDetailsEntity":{"id":42,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-DSS","programDescription":"DSS","status":"Active","creationDate":"2021-11-25T11:07:05.000+0000","createdBy":"superadmin","sourceProgramId":null,"sourceprogramName":null},"customerEntity":null,"label":"MIGRATION_SCHEDULE","value":"D-0","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":"dropdown"},{"id":497,"programDetailsEntity":{"id":42,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-DSS","programDescription":"DSS","status":"Active","creationDate":"2021-11-25T11:07:05.000+0000","createdBy":"superadmin","sourceProgramId":null,"sourceprogramName":null},"customerEntity":null,"label":"POST_MIGRATION_AUDIT_SCHEDULE","value":"D-0","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":"dropdown"},{"id":498,"programDetailsEntity":{"id":42,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-DSS","programDescription":"DSS","status":"Active","creationDate":"2021-11-25T11:07:05.000+0000","createdBy":"superadmin","sourceProgramId":null,"sourceprogramName":null},"customerEntity":null,"label":"POST_MIGRATION_RANATP_SCHEDULE","value":"","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":"dropdown"},{"id":499,"programDetailsEntity":{"id":42,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-DSS","programDescription":"DSS","status":"Active","creationDate":"2021-11-25T11:07:05.000+0000","createdBy":"superadmin","sourceProgramId":null,"sourceprogramName":null},"customerEntity":null,"label":"NE_GROW_AUTOMATION","value":"ON","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":""},{"id":500,"programDetailsEntity":{"id":42,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-DSS","programDescription":"DSS","status":"Active","creationDate":"2021-11-25T11:07:05.000+0000","createdBy":"superadmin","sourceProgramId":null,"sourceprogramName":null},"customerEntity":null,"label":"FETCH_FROM_RFDB","value":"","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":""},{"id":501,"programDetailsEntity":{"id":42,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-DSS","programDescription":"DSS","status":"Active","creationDate":"2021-11-25T11:07:05.000+0000","createdBy":"superadmin","sourceProgramId":null,"sourceprogramName":null},"customerEntity":null,"label":"FETCH_DATE","value":"D-0","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":""},{"id":502,"programDetailsEntity":{"id":42,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-DSS","programDescription":"DSS","status":"Active","creationDate":"2021-11-25T11:07:05.000+0000","createdBy":"superadmin","sourceProgramId":null,"sourceprogramName":null},"customerEntity":null,"label":"MIGRATION_USECASES","value":"Pre-Check_RF_Scripts_Usecase,Cutover_RF_Scripts_Usecase","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":""},{"id":503,"programDetailsEntity":{"id":42,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-DSS","programDescription":"DSS","status":"Active","creationDate":"2021-11-25T11:07:05.000+0000","createdBy":"superadmin","sourceProgramId":null,"sourceprogramName":null},"customerEntity":null,"label":"NE_GROW_USECASES","value":"vDUCellGrow,pnpGrow","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":""},{"id":515,"programDetailsEntity":{"id":42,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-DSS","programDescription":"DSS","status":"Active","creationDate":"2021-11-25T11:07:05.000+0000","createdBy":"superadmin","sourceProgramId":null,"sourceprogramName":null},"customerEntity":null,"label":"KEY_VALUES","value":"{\"KeyValues\":{\"MigrationCI\":\"ON\",\"growElements\":\"OFF\",\"generatENV\":\"OFF\",\"CIQIntegration\":\"ON\"}}","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":""},{"id":519,"programDetailsEntity":{"id":42,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-DSS","programDescription":"DSS","status":"Active","creationDate":"2021-11-25T11:07:05.000+0000","createdBy":"superadmin","sourceProgramId":null,"sourceprogramName":null},"customerEntity":null,"label":"POST_MIG_USECASES","value":"vDUInstantiationAudit,PostMigrationAudit","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":""},{"id":479,"programDetailsEntity":{"id":44,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-CBAND","programDescription":"CBAND","status":"Active","creationDate":"2021-11-25T11:06:54.000+0000","createdBy":"superadmin","sourceProgramId":42,"sourceprogramName":"VZN-5G-DSS"},"customerEntity":null,"label":"MAIL_CONFIGURATION","value":"","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":""},{"id":480,"programDetailsEntity":{"id":44,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-CBAND","programDescription":"CBAND","status":"Active","creationDate":"2021-11-25T11:06:54.000+0000","createdBy":"superadmin","sourceProgramId":42,"sourceprogramName":"VZN-5G-DSS"},"customerEntity":null,"label":"FETCH_SCHEDULE","value":"OFF","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":"time"},{"id":481,"programDetailsEntity":{"id":44,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-CBAND","programDescription":"CBAND","status":"Active","creationDate":"2021-11-25T11:06:54.000+0000","createdBy":"superadmin","sourceProgramId":42,"sourceprogramName":"VZN-5G-DSS"},"customerEntity":null,"label":"PREMIGRATION_SCHEDULE","value":"D-0","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":"dropdown"},{"id":482,"programDetailsEntity":{"id":44,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-CBAND","programDescription":"CBAND","status":"Active","creationDate":"2021-11-25T11:06:54.000+0000","createdBy":"superadmin","sourceProgramId":42,"sourceprogramName":"VZN-5G-DSS"},"customerEntity":null,"label":"NE_GROW_SCHEDULE","value":"","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":"dropdown"},{"id":483,"programDetailsEntity":{"id":44,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-CBAND","programDescription":"CBAND","status":"Active","creationDate":"2021-11-25T11:06:54.000+0000","createdBy":"superadmin","sourceProgramId":42,"sourceprogramName":"VZN-5G-DSS"},"customerEntity":null,"label":"MIGRATION_SCHEDULE","value":"D-0","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":"dropdown"},{"id":484,"programDetailsEntity":{"id":44,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-CBAND","programDescription":"CBAND","status":"Active","creationDate":"2021-11-25T11:06:54.000+0000","createdBy":"superadmin","sourceProgramId":42,"sourceprogramName":"VZN-5G-DSS"},"customerEntity":null,"label":"POST_MIGRATION_AUDIT_SCHEDULE","value":"D-0","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":"dropdown"},{"id":485,"programDetailsEntity":{"id":44,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-CBAND","programDescription":"CBAND","status":"Active","creationDate":"2021-11-25T11:06:54.000+0000","createdBy":"superadmin","sourceProgramId":42,"sourceprogramName":"VZN-5G-DSS"},"customerEntity":null,"label":"POST_MIGRATION_RANATP_SCHEDULE","value":"","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":"dropdown"},{"id":486,"programDetailsEntity":{"id":44,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-CBAND","programDescription":"CBAND","status":"Active","creationDate":"2021-11-25T11:06:54.000+0000","createdBy":"superadmin","sourceProgramId":42,"sourceprogramName":"VZN-5G-DSS"},"customerEntity":null,"label":"NE_GROW_AUTOMATION","value":"ON","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":""},{"id":487,"programDetailsEntity":{"id":44,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-CBAND","programDescription":"CBAND","status":"Active","creationDate":"2021-11-25T11:06:54.000+0000","createdBy":"superadmin","sourceProgramId":42,"sourceprogramName":"VZN-5G-DSS"},"customerEntity":null,"label":"FETCH_FROM_RFDB","value":"OFF","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":""},{"id":488,"programDetailsEntity":{"id":44,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-CBAND","programDescription":"CBAND","status":"Active","creationDate":"2021-11-25T11:06:54.000+0000","createdBy":"superadmin","sourceProgramId":42,"sourceprogramName":"VZN-5G-DSS"},"customerEntity":null,"label":"FETCH_DATE","value":"D-0","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":""},{"id":489,"programDetailsEntity":{"id":44,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-CBAND","programDescription":"CBAND","status":"Active","creationDate":"2021-11-25T11:06:54.000+0000","createdBy":"superadmin","sourceProgramId":42,"sourceprogramName":"VZN-5G-DSS"},"customerEntity":null,"label":"MIGRATION_USECASES","value":"Pre-Check_RF_Scripts_Usecase,Cutover_RF_Scripts_Usecase,Extended_Usecase","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":""},{"id":490,"programDetailsEntity":{"id":44,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-CBAND","programDescription":"CBAND","status":"Active","creationDate":"2021-11-25T11:06:54.000+0000","createdBy":"superadmin","sourceProgramId":42,"sourceprogramName":"VZN-5G-DSS"},"customerEntity":null,"label":"NE_GROW_USECASES","value":"GrowvDU","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":""},{"id":513,"programDetailsEntity":{"id":44,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-CBAND","programDescription":"CBAND","status":"Active","creationDate":"2021-11-25T11:06:54.000+0000","createdBy":"superadmin","sourceProgramId":42,"sourceprogramName":"VZN-5G-DSS"},"customerEntity":null,"label":"KEY_VALUES","value":"{\"KeyValues\":{\"MigrationCI\":\"ON\",\"growElements\":\"ON\",\"generatENV\":\"ON\",\"CIQIntegration\":\"ON\"}}","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":""},{"id":518,"programDetailsEntity":{"id":44,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-CBAND","programDescription":"CBAND","status":"Active","creationDate":"2021-11-25T11:06:54.000+0000","createdBy":"superadmin","sourceProgramId":42,"sourceprogramName":"VZN-5G-DSS"},"customerEntity":null,"label":"POST_MIG_USECASES","value":"PostMigrationAudit","type":"PROGRAM TEMPLATE","configType":"s&r","inputType":""},{"id":5,"programDetailsEntity":null,"customerEntity":{"id":2,"customerName":"Verizon","iconPath":"/customer/verizon_ 08262019_12_41_21_icon.png","status":"Active","customerShortName":"VZN","customerDetails":[{"id":30,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-Latest","programDescription":"USM-Latest","status":"Active","creationDate":"2019-10-10T05:55:57.000+0000","createdBy":"superadmin","sourceProgramId":28,"sourceprogramName":"VZN-4G-USM-TEST"},{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2021-11-24T14:45:38.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},{"id":36,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LAB","programDescription":"LAB","status":"Active","creationDate":"2020-05-04T13:07:17.000+0000","createdBy":"superadmin","sourceProgramId":34,"sourceprogramName":"VZN-4G-USM-LIVE"},{"id":38,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#ec8cab"},"programName":"VZN-4G-FSU","programDescription":"FSU","status":"Active","creationDate":"2020-06-05T01:33:48.000+0000","createdBy":"superadmin","sourceProgramId":34,"sourceprogramName":"VZN-4G-USM-LIVE"},{"id":40,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-MM","programDescription":"MM","status":"Active","creationDate":"2021-11-25T11:06:07.000+0000","createdBy":"superadmin","sourceProgramId":34,"sourceprogramName":"VZN-4G-USM-LIVE"},{"id":42,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-DSS","programDescription":"DSS","status":"Active","creationDate":"2021-11-25T11:07:05.000+0000","createdBy":"superadmin","sourceProgramId":null,"sourceprogramName":null},{"id":44,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-CBAND","programDescription":"CBAND","status":"Active","creationDate":"2021-11-25T11:06:54.000+0000","createdBy":"superadmin","sourceProgramId":42,"sourceprogramName":"VZN-5G-DSS"}]},"label":"SCHEDULING_MARKET","value":"{\"MarketData\":{\"market\":[\"Upstate New York\",\"New England\"]}}","type":"S & R","configType":"s&r","inputType":null}],"fetchDays":"5"};
              //General
              // this.tableData = {"ConfigDetails":[{"id":null,"programDetailsEntity":null,"customerEntity":null,"label":"Session Time Out (in minutes)","value":"60","type":"GENERAL","configType":"general"},{"id":null,"programDetailsEntity":null,"customerEntity":null,"label":"Tool Deployment","value":"Live","type":"GENERAL","configType":"general"},{"id":null,"programDetailsEntity":null,"customerEntity":null,"label":"History (in days)","value":"7","type":"GENERAL","configType":"general"},{"id":null,"programDetailsEntity":null,"customerEntity":null,"label":"DUO AUTHENTICATION","value":"OFF","type":"GENERAL","configType":"general"},{"id":null,"programDetailsEntity":null,"customerEntity":null,"label":"CIQ TYPE","value":"NEW","type":"GENERAL","configType":"general"}],"sessionId":"7ed5e92c","serviceToken":"94148","status":"SUCCESS"};
             
             if(this.tableData.status == "SUCCESS"){
                 if(this.tableData.ConfigDetails.length == 0){
                     this.settingBlock = false;
                     this.newNoDataWrapper = true;
                 } else {
                     this.settingBlock = true;
                     this.newNoDataWrapper = false;
                     this.sessiontimeout = this.tableData.ConfigDetails.sessionTimeout;
                     // To display table data
                     setTimeout(() => {
                     let tableWidth = document.getElementById('removalDetails').scrollWidth;
                     $(".scrollBody table").css("min-width", (tableWidth) + "px");
                     $(".scrollHead table").css("width", tableWidth + "px");
                     $(".scrollBody").on("scroll", function (event) {
                     $(".formEditRow #formWrapper").css("margin-left", (event.target.scrollLeft - 0) + "px");
                     $(".form-control-fixed").css("right", (event.target.scrollLeft * -1) + "px");
                     $(".scrollHead table").css("margin-left", (event.target.scrollLeft * -1) + "px");
                     });
                     }, 0);
                 }
                 this.createScheduleJosn(this.tableData.fetchDays);
             }else{
                 this.showLoader= false;
                 this.displayModel(this.tableData.reason, "failureIcon");
             }        
           }, 1000); 
          //Please Comment while checkIn
        });
  }

  onChangeLoad(value){
    this.migrationType = value;
    this.getMiscConfigDetails();
}

  formPgmName(custShortName) {

  }

  getNetworkDetails() {
    this.editIndex = -1;
    this.showLoader = true;
    this.generalconfigService.getNetworkTypeDetails(this.sharedService.createServiceToken())
      .subscribe(
        data => {
          setTimeout(() => {
            let jsonStatue = data.json();
            this.showLoader = false;
            this.networkData = jsonStatue;
            if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
              this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
            } else {

              if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {

                if (jsonStatue.status == "SUCCESS") {
                  if (this.networkData.networkTypeDetails.length == 0) {
                    this.nwtypeBlock = false;
                    this.newNoDataWrapper = true;
                  } else {
                    this.nwtypeBlock = true;
                    this.newNoDataWrapper = false;

                    setTimeout(() => {
                      let tableWidth = document.getElementById('networkDetails').scrollWidth;
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

              }

            }
          }, 1000);
        },
        error => {
          this.serverError(error);

          //Please Comment while checkIn
            setTimeout(() => {
              this.showLoader = false;
              // no data 
              //this.networkData = JSON.parse('{"networkTypeDetails":[],"sessionId":"d4e02826","serviceToken":"95198","status":"SUCCESS"}');
              this.networkData = JSON.parse('{"networkTypeDetails":[{"id":1,"networkType":"3G","createdBy":"admin","caretedDate":"2018-12-10T07:34:27.000+0000","status":"Active","remarks":"fdfd"},{"id":2,"networkType":"4G","createdBy":"admin","caretedDate":"2018-12-10T07:34:34.000+0000","status":"Active","remarks":"fdfd"}],"sessionId":"d4e02826","serviceToken":"95198","status":"SUCCESS"}');
  
              if (this.networkData.networkTypeDetails.length == 0) {
                this.nwtypeBlock = false;
                this.newNoDataWrapper = true;
                
              } else {
                this.nwtypeBlock = true;
                this.newNoDataWrapper = false;
                // To display table data
                setTimeout(() => {
                  let tableWidth = document.getElementById('networkDetails').scrollWidth;
                  $(".scrollBody table").css("min-width", (tableWidth) + "px");
                  $(".scrollHead table").css("width", tableWidth + "px");
                  $(".scrollBody").on("scroll", function (event) {
                    $(".formEditRow #formWrapper").css("margin-left", (event.target.scrollLeft - 0) + "px");
                    $(".form-control-fixed").css("right", (event.target.scrollLeft * -1) + "px");
                    $(".scrollHead table").css("margin-left", (event.target.scrollLeft * -1) + "px");
                  });
                }, 0);
              }
  
            }, 1000); 
          //Please Comment while checkIn
        });
  }

  getneVersionDetails() {
    this.editIndex = -1;
    this.showLoader = true;
    $("#dataWrapper").find(".scrollBody").scrollLeft(0);
    this.generalconfigService.getNeVersionDetails(this.sharedService.createServiceToken())
      .subscribe(
        data => {
          setTimeout(() => {
            let jsonStatue = data.json();
            this.showLoader = false;
            this.neversionData = jsonStatue;
            if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
              this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
            } else {

              if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {
                this.programNamesList = this.neversionData.programNamesList;
                // this.programNamesList  = JSON.parse(sessionStorage.getItem("loginDetails")).programSubscription;
                if (jsonStatue.status == "SUCCESS") {
                  if (this.neversionData.neVersionDetails.length == 0) {
                    this.neversionBlock = false;
                    this.newNoDataWrapper = true;

                  } else {
                    this.neversionBlock = true;
                    this.newNoDataWrapper = false;

                    setTimeout(() => {
                      let tableWidth = document.getElementById('neVersionDetails').scrollWidth;
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

              }

            }
          }, 1000);
        },
        error => {
          this.serverError(error);

          //Please Comment while checkIn
          /* setTimeout(() => {
            this.showLoader = false;
            // no data 
            //this.neversionData = JSON.parse('{"programNamesList":[],"neVersionDetails":[],"sessionId":"6686a6bf","serviceToken":"52526","status":"SUCCESS"}');
            this.neversionData = JSON.parse('{"programNamesList":[{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},{"id":24,"networkTypeDetailsEntity":{"id":3,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"VZN-5G-VLSM","programDescription":"VLSM","status":"Active","creationDate":"2019-03-04T11:57:13.000+0000","createdBy":"superadmin"},{"id":25,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"AT&T-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-03-07T07:15:07.000+0000","createdBy":"superadmin"},{"id":26,"networkTypeDetailsEntity":{"id":3,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"AT&T-5G-VLSM","programDescription":"VLSM","status":"Active","creationDate":"2019-02-27T08:14:05.000+0000","createdBy":"superadmin"},{"id":27,"networkTypeDetailsEntity":{"id":3,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"T1-5G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-28T06:09:38.000+0000","createdBy":"superadmin"},{"id":28,"networkTypeDetailsEntity":{"id":3,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"TS2-5G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-28T06:38:12.000+0000","createdBy":"superadmin"},{"id":29,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"T1-4G-qwerty","programDescription":"qwerty","status":"Active","creationDate":"2019-03-04T10:55:43.000+0000","createdBy":"superadmin"},{"id":30,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"TS2-4G-TestLogo","programDescription":"TestLogo","status":"Active","creationDate":"2019-03-04T12:11:58.000+0000","createdBy":"superadmin"},{"id":31,"networkTypeDetailsEntity":{"id":3,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"TST3-5G-teslLegacy","programDescription":"asasa","status":"Active","creationDate":"2019-03-05T14:08:39.000+0000","createdBy":"superadmin"},{"id":32,"networkTypeDetailsEntity":{"id":3,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"TST4-5G-test4","programDescription":"qwwee","status":"Active","creationDate":"2019-03-05T14:19:24.000+0000","createdBy":"superadmin"},{"id":33,"networkTypeDetailsEntity":{"id":3,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"TST4-5G-test5","programDescription":"asdssd","status":"Active","creationDate":"2019-03-07T06:57:48.000+0000","createdBy":"superadmin"},{"id":34,"networkTypeDetailsEntity":{"id":3,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"SPT-5G-TESTING","programDescription":"","status":"Active","creationDate":"2019-03-08T05:32:58.000+0000","createdBy":"superadmin"}],"neVersionDetails":[{"id":1,"programDetailsEntity":{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-02-27T08:11:30.000+0000","createdBy":"superadmin"},"neVersion":"1.2.3","releaseVersion":"2","status":"Active","createdBy":"superadmin","creationDate":"2019-02-15T11:02:23.000+0000"},{"id":2,"programDetailsEntity":{"id":24,"networkTypeDetailsEntity":{"id":3,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"VZN-5G-VLSM","programDescription":"VLSM","status":"Active","creationDate":"2019-03-04T11:57:13.000+0000","createdBy":"superadmin"},"neVersion":"1.3.1","status":"Active","createdBy":"superadmin","creationDate":"2019-02-15T11:02:23.000+0000"},{"id":3,"programDetailsEntity":{"id":25,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"AT&T-4G-LEGACY","programDescription":"LEGACY","status":"Active","creationDate":"2019-03-07T07:15:07.000+0000","createdBy":"superadmin"},"neVersion":"1.5.1","status":"Active","createdBy":"superadmin","creationDate":"2019-02-15T11:02:23.000+0000"},{"id":4,"programDetailsEntity":{"id":26,"networkTypeDetailsEntity":{"id":3,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"AT&T-5G-VLSM","programDescription":"VLSM","status":"Active","creationDate":"2019-02-27T08:14:05.000+0000","createdBy":"superadmin"},"neVersion":"1.5.6","status":"Active","createdBy":"superadmin","creationDate":"2019-02-15T11:02:23.000+0000"}],"sessionId":"6686a6bf","serviceToken":"52526","status":"SUCCESS"}');

            this.programNamesList = this.neversionData.programNamesList;
            // this.programNamesList  = JSON.parse(sessionStorage.getItem("loginDetails")).programSubscription;
            if (this.neversionData.neVersionDetails.length == 0) {
              this.neversionBlock = false;
              this.newNoDataWrapper = true;
            } else {
              this.neversionBlock = true;
              this.newNoDataWrapper = false;
              // To display table data
              setTimeout(() => {
                let tableWidth = document.getElementById('neVersionDetails').scrollWidth;
                $(".scrollBody table").css("min-width", (tableWidth) + "px");
                $(".scrollHead table").css("width", tableWidth + "px");
                $(".scrollBody").on("scroll", function (event) {
                  $(".formEditRow #formWrapper").css("margin-left", (event.target.scrollLeft - 0) + "px");
                  $(".form-control-fixed").css("right", (event.target.scrollLeft * -1) + "px");
                  $(".scrollHead table").css("margin-left", (event.target.scrollLeft * -1) + "px");
                });
              }, 0);
            }

          }, 1000); */
          //Please Comment while checkIn
        });
  }

  getCustomerDetails() {
    this.editCustIndex = -1;
    this.showLoader = true;
    this.generalconfigService.getCustomerDetails(this.sharedService.createServiceToken())
      .subscribe(
        data => {
          setTimeout(() => {
            let jsonStatue = data.json();
            this.showLoader = false;
            this.customerData = jsonStatue;
            if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
              this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
            } else {

              if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {

                if (jsonStatue.status == "SUCCESS") {
                    this.customerList = this.customerData.customerList;
                    this.networkTypeList = this.customerData.netWorkDetails;
                    this.allProgramNamesList = this.customerData.allProgramList;

                  if (this.customerData.customerList.length == 0) {
                    this.customerCtBlock = false;
                    this.newNoDataWrapper = true;
                  } else {
                    this.customerCtBlock = true;
                    this.newNoDataWrapper = false;
                    
                    setTimeout(() => {
                      let tableWidth = document.getElementById('customerDetails').scrollWidth;
                      $(".scrollBody #customerData").css("min-width", (tableWidth) + "px");
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

              }

            }
          }, 1000);
        },
        error => {
          this.serverError(error);

          //Please Comment while checkIn
          /* setTimeout(() => {
            this.showLoader = false;
            // no data 
            //this.customerData = JSON.parse('{"netWorkDetails":[],"customerList":[],"sessionId":"67754ca2","serviceToken":"57674","status":"SUCCESS"}');
            // this.customerData = JSON.parse('{"sessionId":"67754ca2","serviceToken":"57674","status":"SUCCESS","netWorkDetails":[{"id":2,"networkType":"4G","createdBy":"admin","caretedDate":"2019-01-23T05:33:27.000+0000","status":"InActive","remarks":"","networkColor":"#a7aea7"},{"id":4,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},{"id":10,"networkType":"6G_2","createdBy":"admin","caretedDate":"2019-01-23T05:33:41.000+0000","status":"Active","remarks":"","networkColor":"#bf16e1"},{"id":11,"networkType":"7G","createdBy":"admin","caretedDate":"2019-01-23T05:33:46.000+0000","status":"Active","remarks":"","networkColor":"#281d71"},{"id":12,"networkType":"3G","createdBy":"admin","caretedDate":"2019-01-23T05:33:51.000+0000","status":"Active","remarks":"","networkColor":"#914152"}],"customerList":[{"id":2,"customerName":"Verizon","iconPath":"/customer/verizon_icon.png","status":"InActive","customerShortName":"VZN","customerDetails":[{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"admin","caretedDate":"2019-01-23T05:33:27.000+0000","status":"Active","remarks":"","networkColor":"#a7aea7"},"programName":"VZN-4G-LEGACY","programDescription":"program Description01","status":"InActive","creationDate":"2019-02-11T09:24:41.000+0000","createdBy":"superadmin"},{"id":3,"networkTypeDetailsEntity":{"id":4,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"VZN-5G-LEGACY","programDescription":"program Description02","status":"Active","creationDate":"2019-02-11T09:25:27.000+0000","createdBy":"superadmin"}]},{"id":3,"customerName":"AT&T","iconPath":"/customer/at&t_icon.png","status":"Active","customerShortName":"AT&T","customerDetails":[{"id":4,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"admin","caretedDate":"2019-01-23T05:33:27.000+0000","status":"Active","remarks":"","networkColor":"#a7aea7"},"programName":"AT&T-4G-LEGACY","programDescription":"program Description03","status":"Active","creationDate":"2019-02-11T09:25:34.000+0000","createdBy":"superadmin"},{"id":5,"networkTypeDetailsEntity":{"id":4,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"AT&T-5G-LEGACY","programDescription":"program Description04","status":"Active","creationDate":"2019-02-11T09:25:39.000+0000","createdBy":"superadmin"}]},{"id":4,"customerName":"Sprint","iconPath":"/customer/sprint_icon.png","status":"Active","customerShortName":"SPRNT","customerDetails":[{"id":6,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"admin","caretedDate":"2019-01-23T05:33:27.000+0000","status":"Active","remarks":"","networkColor":"#a7aea7"},"programName":"SPT-4G-LEGACY","programDescription":"program Description05","status":"Active","creationDate":"2019-02-11T09:25:44.000+0000","createdBy":"superadmin"}]}]}');
            this.customerData = JSON.parse('{"allProgramList":[{"id":1,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-05-31T09:20:26.000+0000","status":"Active","remarks":"","networkColor":"#0f2a79"},"programName":"VZN-4G-CDU30","programDescription":"CDU30","status":"Active","creationDate":"2019-07-08T12:09:50.000+0000","createdBy":"superadmin","sourceProgramId":null,"sourceprogramName":null},{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-05-31T09:20:26.000+0000","status":"Active","remarks":"","networkColor":"#0f2a79"},"programName":"VZN-4G-VLSM","programDescription":"VLSM","status":"Active","creationDate":"2019-07-09T07:13:36.000+0000","createdBy":"superadmin","sourceProgramId":null,"sourceprogramName":null},{"id":3,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-05-31T09:20:26.000+0000","status":"Active","remarks":"","networkColor":"#0f2a79"},"programName":"SPT-4G-MIMO","programDescription":"MIMO","status":"InActive","creationDate":"2019-07-09T07:13:58.000+0000","createdBy":"superadmin","sourceProgramId":null,"sourceprogramName":null},{"id":4,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-05-31T09:20:26.000+0000","status":"Active","remarks":"","networkColor":"#0f2a79"},"programName":"SPT-4G-CDU30","programDescription":"CDU30","status":"InActive","creationDate":"2019-07-09T07:30:15.000+0000","createdBy":"superadmin","sourceProgramId":null,"sourceprogramName":null},{"id":6,"networkTypeDetailsEntity":{"id":3,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#0e183b"},"programName":"SPT-5G-FPGA","programDescription":"FPGA","status":"InActive","creationDate":"2019-07-09T07:29:23.000+0000","createdBy":"superadmin","sourceProgramId":null,"sourceprogramName":null},{"id":12,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-05-31T09:20:26.000+0000","status":"Active","remarks":"","networkColor":"#0f2a79"},"programName":"ATT-4G-CBRS","programDescription":"CBRS","status":"InActive","creationDate":"2019-07-09T07:13:45.000+0000","createdBy":"superadmin","sourceProgramId":null,"sourceprogramName":null},{"id":13,"networkTypeDetailsEntity":{"id":3,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#0e183b"},"programName":"ATT-5G-PICO","programDescription":"PICO","status":"InActive","creationDate":"2019-07-09T07:13:51.000+0000","createdBy":"superadmin","sourceProgramId":null,"sourceprogramName":null},{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-05-31T09:20:26.000+0000","status":"Active","remarks":"","networkColor":"#0f2a79"},"programName":"TST1-4G-123","programDescription":"123","status":"Active","creationDate":"2019-07-09T10:35:47.000+0000","createdBy":"superadmin","sourceProgramId":null,"sourceprogramName":null},{"id":24,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-05-31T09:20:26.000+0000","status":"Active","remarks":"","networkColor":"#0f2a79"},"programName":"TST-4G-efg","programDescription":"efg","status":"Active","creationDate":"2019-07-10T10:11:49.000+0000","createdBy":"superadmin","sourceProgramId":null,"sourceprogramName":null},{"id":26,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-05-31T09:20:26.000+0000","status":"Active","remarks":"","networkColor":"#0f2a79"},"programName":"TST-4G-abc","programDescription":"abc","status":"Active","creationDate":"2019-07-10T10:13:16.000+0000","createdBy":"superadmin","sourceProgramId":4,"sourceprogramName":"SPT-4G-CDU30"}],"netWorkDetails":[{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-05-31T09:20:26.000+0000","status":"Active","remarks":"","networkColor":"#0f2a79"},{"id":3,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#0e183b"}],"programNamesList":[{"id":1,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-05-31T09:20:26.000+0000","status":"Active","remarks":"","networkColor":"#0f2a79"},"programName":"VZN-4G-CDU30","programDescription":"CDU30","status":"Active","creationDate":"2019-07-08T12:09:50.000+0000","createdBy":"superadmin","sourceProgramId":null,"sourceprogramName":null},{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-05-31T09:20:26.000+0000","status":"Active","remarks":"","networkColor":"#0f2a79"},"programName":"VZN-4G-VLSM","programDescription":"VLSM","status":"Active","creationDate":"2019-07-09T07:13:36.000+0000","createdBy":"superadmin","sourceProgramId":null,"sourceprogramName":null},{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-05-31T09:20:26.000+0000","status":"Active","remarks":"","networkColor":"#0f2a79"},"programName":"TST1-4G-123","programDescription":"123","status":"Active","creationDate":"2019-07-09T10:35:47.000+0000","createdBy":"superadmin","sourceProgramId":null,"sourceprogramName":null},{"id":24,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-05-31T09:20:26.000+0000","status":"Active","remarks":"","networkColor":"#0f2a79"},"programName":"TST-4G-efg","programDescription":"efg","status":"Active","creationDate":"2019-07-10T10:11:49.000+0000","createdBy":"superadmin","sourceProgramId":null,"sourceprogramName":null},{"id":26,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-05-31T09:20:26.000+0000","status":"Active","remarks":"","networkColor":"#0f2a79"},"programName":"TST-4G-abc","programDescription":"abc","status":"Active","creationDate":"2019-07-10T10:13:16.000+0000","createdBy":"superadmin","sourceProgramId":4,"sourceprogramName":"SPT-4G-CDU30"}],"customerList":[{"id":2,"customerName":"Verizon","iconPath":"/customer/verizon_ 07082019_11_40_05_icon.png","status":"Active","customerShortName":"VZN","customerDetails":[{"id":1,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-05-31T09:20:26.000+0000","status":"Active","remarks":"","networkColor":"#0f2a79"},"programName":"VZN-4G-CDU30","programDescription":"CDU30","status":"Active","creationDate":"2019-07-08T12:09:50.000+0000","createdBy":"superadmin","sourceProgramId":null,"sourceprogramName":null},{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-05-31T09:20:26.000+0000","status":"Active","remarks":"","networkColor":"#0f2a79"},"programName":"VZN-4G-VLSM","programDescription":"VLSM","status":"Active","creationDate":"2019-07-09T07:13:36.000+0000","createdBy":"superadmin","sourceProgramId":null,"sourceprogramName":null}]},{"id":3,"customerName":"AT&T","iconPath":"/customer/at&t_ 07082019_11_40_11_icon.png","status":"Active","customerShortName":"ATT","customerDetails":[{"id":12,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-05-31T09:20:26.000+0000","status":"Active","remarks":"","networkColor":"#0f2a79"},"programName":"ATT-4G-CBRS","programDescription":"CBRS","status":"InActive","creationDate":"2019-07-09T07:13:45.000+0000","createdBy":"superadmin","sourceProgramId":null,"sourceprogramName":null},{"id":13,"networkTypeDetailsEntity":{"id":3,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#0e183b"},"programName":"ATT-5G-PICO","programDescription":"PICO","status":"InActive","creationDate":"2019-07-09T07:13:51.000+0000","createdBy":"superadmin","sourceProgramId":null,"sourceprogramName":null}]},{"id":4,"customerName":"Sprint","iconPath":"/customer/sprint_ 07082019_11_40_18_icon.png","status":"Active","customerShortName":"SPT","customerDetails":[{"id":3,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-05-31T09:20:26.000+0000","status":"Active","remarks":"","networkColor":"#0f2a79"},"programName":"SPT-4G-MIMO","programDescription":"MIMO","status":"InActive","creationDate":"2019-07-09T07:13:58.000+0000","createdBy":"superadmin","sourceProgramId":null,"sourceprogramName":null},{"id":4,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-05-31T09:20:26.000+0000","status":"Active","remarks":"","networkColor":"#0f2a79"},"programName":"SPT-4G-CDU30","programDescription":"CDU30","status":"InActive","creationDate":"2019-07-09T07:30:15.000+0000","createdBy":"superadmin","sourceProgramId":null,"sourceprogramName":null},{"id":6,"networkTypeDetailsEntity":{"id":3,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#0e183b"},"programName":"SPT-5G-FPGA","programDescription":"FPGA","status":"InActive","creationDate":"2019-07-09T07:29:23.000+0000","createdBy":"superadmin","sourceProgramId":null,"sourceprogramName":null}]},{"id":8,"customerName":"Test_1","iconPath":"/customer/test_1_ 07092019_16_05_47_icon.png","status":"Active","customerShortName":"TST1","customerDetails":[{"id":23,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-05-31T09:20:26.000+0000","status":"Active","remarks":"","networkColor":"#0f2a79"},"programName":"TST1-4G-123","programDescription":"123","status":"Active","creationDate":"2019-07-09T10:35:47.000+0000","createdBy":"superadmin","sourceProgramId":null,"sourceprogramName":null},{"id":24,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-05-31T09:20:26.000+0000","status":"Active","remarks":"","networkColor":"#0f2a79"},"programName":"TST-4G-efg","programDescription":"efg","status":"Active","creationDate":"2019-07-10T10:11:49.000+0000","createdBy":"superadmin","sourceProgramId":null,"sourceprogramName":null},{"id":26,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-05-31T09:20:26.000+0000","status":"Active","remarks":"","networkColor":"#0f2a79"},"programName":"TST-4G-abc","programDescription":"abc","status":"Active","creationDate":"2019-07-10T10:13:16.000+0000","createdBy":"superadmin","sourceProgramId":4,"sourceprogramName":"SPT-4G-CDU30"}]}],"sessionId":"7cde7773","serviceToken":"89818","status":"SUCCESS"}');
            this.customerList = this.customerData.customerList;
            this.networkTypeList = this.customerData.netWorkDetails;
            this.allProgramNamesList = this.customerData.allProgramList;


            if (this.customerList.length == 0) {
              this.customerCtBlock = false;
              this.newNoDataWrapper = true;
            } else {
              this.customerCtBlock = true;
              this.newNoDataWrapper = false;


              // To display table data
              setTimeout(() => {
                let tableWidth = document.getElementById('customerDetails').scrollWidth;
                $(".scrollBody #customerData").css("min-width", (tableWidth) + "px");
                $(".scrollHead table").css("width", tableWidth + "px");
                console.log(tableWidth);
                $(".scrollBody").on("scroll", function (event) {
                  $(".formEditRow #formWrapper").css("margin-left", (event.target.scrollLeft - 0) + "px");
                  $(".form-control-fixed").css("right", (event.target.scrollLeft * -1) + "px");
                  $(".scrollHead table").css("margin-left", (event.target.scrollLeft * -1) + "px");
                });
              }, 0);
            }

          }, 1000); */
          //Please Comment while checkIn
        });
  }

  switchClicked(event, key, index) {
    event.stopPropagation();
    this.showLoader = true;
    this.customerList[index].status = $("#useGenScript_" + index).prop('checked') ? 'InActive' : 'Active';
    //this.scriptValue[index].useGeneratedScript = $("#useGenScript_" + index).prop('checked') ? 'YES':'NO';
    // status = status == "Active" ? status = "InActive" : "Active";
    let customerDetails = {
      "id": key.id,
      "customerName": key.customerName,
      "customerShortName": key.customerShortName,
      "status": key.status
    }

    this.generalconfigService.addCustomer(customerDetails, this.sharedService.createServiceToken())
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
                  if(key.status=='Active')
                  {
                    this.message = "Customer activated successfully !";
                  }
                  else if(key.status=='InActive')
                  {
                   this.message = "Customer deactivated successfully !";
                  }
                  this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                  this.getCustomerDetails();
                  // this.createForm = false;
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
              if(key.status=='Active')
              {
                this.message = "Customer activated successfully !";
              }
              else if(key.status=='InActive')
              {
                this.message = "Customer deactivated successfully !";
              }
              this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: "static", size: 'lg', windowClass: "success-modal" });
              this.getCustomerDetails();
            } else {
              this.displayModel(jsonStatue.reason, "failureIcon");
            }

          }, 1000); */
          //Please Comment while checkIn
        });
  }

  generateScheduleTime(str) {
    // 14:34
    let time = str ? str.split(":") : "";
    let hr = time ? time[0] : "00";
    let min = time ? time[1] : "00";
    let todayDate = new Date();
    return new Date(todayDate.getFullYear(), todayDate.getMonth() + 1, todayDate.getDate() + 1, hr, min, 0);
  }

  editRow(event, key, index) {
    let editState = event.target;
    $(editState).parents('tbody').find('.editRowDisabled').removeClass('editRowDisabled').addClass('editRow');
    if (editState.className != "editRowDisabled") { //enable click only if it is enabled
      editState.className = "editRowDisabled";

      // If any edit form is opend then close the form and enable edit button
      if (this.currentEditRow != undefined) {

        this.currentEditRow.className = "editRow";
      }

      // To enable one edit form at a time in table
      if (this.editableFormArray.length >= 1) {
        this.editableFormArray = [];
        this.editableFormArray.push(index);
      } else {
        this.editableFormArray.push(index);
      }
      this.miscParameter = key;
      this.sharedService.userNavigation = false; //block user navigation
      if(key.inputType == "dropdown") {

        // key.value = "D-0, D-2|14:34";
        let schDays = key.value ? key.value.split("|") : "";
        this.selectedSch = schDays ? schDays[0].trim().split(",") : [];
        //this.scheduledTime = this.generateScheduleTime(schDays[1]);
        this.onChangeSchedule(this.selectedSch);
      } else if(key.inputType == "time") {
        this.selectedSchTime = key.value ? key.value.trim().split(",") : [];
        this.onChangeScheduleTime(this.selectedSchTime);
      }

      if(key.label== "MIGRATION_USECASES"){
        this.dropdownList = [];
        // let useCaseList = this.tableData.useCaseList;//lsmSelectedName.useCaseList;  
            this.selectedUseCases = key.value ? key.value.trim().split(",") : [];
            let useCaseDefList = this.getDefaultUseCases(key.programDetailsEntity.programName);
            for (let itm of useCaseDefList) {
              //  let dropdownList = { item_id: itm, item_text: itm };
                this.dropdownList.push(itm);
            
           // this.selectedItems= this.dropdownList;
        }
      }else if (key.label== "NE_GROW_USECASES"){
        this.dropdownList = [];
        // let useCaseList = this.tableData.useCaseList;//lsmSelectedName.useCaseList;  
        this.selectedUseCases = key.value ? key.value.trim().split(",") : [];
            let useCaseDefList = this.getDefaultUseNeGrowCases(key.programDetailsEntity.programName);
            for (let itm of useCaseDefList) {
               // let dropdownListNG = { item_id: itm, item_text: itm };
                this.dropdownList.push(itm);
            
        }
      }
      else if(key.label== "POST_MIG_USECASES") {
        this.dropdownList = [];
        // let useCaseList = this.tableData.useCaseList;//lsmSelectedName.useCaseList;  
            this.selectedUseCases = key.value ? key.value.trim().split(",") : [];
            let useCaseDefList = this.getDefaultUseCasesPostMigration(key.programDetailsEntity.programName);
            for (let itm of useCaseDefList) {
              //  let dropdownList = { item_id: itm, item_text: itm };
                this.dropdownList.push(itm);
           // this.selectedItems= this.dropdownList;
        }
      }
      
    this.jsonData = null;
      try {
        this.jsonData = JSON.parse(key.value); //.replace( /[\r\n]+/gm, "" ).replace(/\ /g, "")
        if(typeof this.jsonData != "object") {
          this.jsonData = null;
        }
      }catch(err) {

      }
      setTimeout(() => {
        this.editRowInTable(event, key, index);
      }, 30);
    }
  }


  editNeVersionRow(event, key, index) {
    $(".createbtn").addClass("buttonDisabled");
    let editState: any = event.target;

    if (editState.className == "editRow") {
      this.editIndex = index;
      this.programDetailsEntity = key.programDetailsEntity;
      $("#neversionData").find("br").remove();
      $(".saveRow").attr("class", "editRow");
      //$(".saveRow").addClass("validateForm");
      $(".cancelRow").attr("class", "deleteRow");
      if (editState.className != "editRowDisabled") { //enable click only if it is enabled
        editState.className = "saveRow";
        editState.nextSibling.className = "cancelRow";
        // To enable one edit form at a time in table
        if (this.editableFormArray.length >= 1) {
          this.editableFormArray = [];
          this.editableFormArray.push(index);
        } else {
          this.editableFormArray.push(index);
        }
      }

    } else if (editState.className != "editRowDisabled") {
      $(".saveRow").addClass("validateForm");
      let validations = this.validationData;
      if (index == "new") {
        this.editIndex = index;
        validations.rules["programName_" + [index]] = { "required": true };
        validations.messages["programName_" + [index]] = { "required": "programName is required" };
        validations.rules["netType_" + [index]] = { "required": true };
        validations.messages["netType_" + [index]] = { "required": "Network Type is required" };
        validations.rules["relVer_" + [index]] = { "required": true };
        validations.messages["relVer_" + [index]] = { "required": "Release Version is required" };
      } else {
        validations.rules["programName_" + [index]] = { "required": true };
        validations.messages["programName_" + [index]] = { "required": "programName is required" };
        validations.rules["netType_" + [index]] = { "required": true };
        validations.messages["netType_" + [index]] = { "required": "Network Type is required" };
        validations.rules["relVer_" + [index]] = { "required": true };
        validations.messages["relVer_" + [index]] = { "required": "Release Version is required" };
      }

      this.validationData = validations;

      validator.performValidation(event, this.validationData, "save_update");
      setTimeout(() => {
        if (this.isValidForm(event)) {
          this.showLoader = true;
          this.element.nativeElement.querySelector('.createbtn').classList.remove('buttonDisabled');
          let neVersionDetails = {
            "programDetailsEntity": this.programDetailsEntity,
            "id": key ? key.id : null,
            "neVersion": $(event.target).parents("tr").find("td input.netType").val(),//currentTarget.find("td select#networkTypeEdit_"+index).val();
            "releaseVersion": $(event.target).parents("tr").find("td input.relVer").val(),
            "status": $(event.target).parents("tr").find("td select.status").val()
          };
          this.generalconfigService.updateNeVersion(neVersionDetails, this.sharedService.createServiceToken())
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
                        this.message = neVersionDetails.id ? "NE Version details updated successfully !" : "NE Version details created successfully !";
                        this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                        this.neversionContainer.clear();
                        this.getneVersionDetails();
                      } else {
                        this.displayModel(jsonStatue.reason, "failureIcon");
                      }
                    }
                  }
                }, 1000);
              },
              error => {
                //Please Comment while checkIn
                /* let jsonStatue: any = {"sessionId":"506db794","reason":"Updation Failed","status":"SUCCESS","serviceToken":"63524"};
                setTimeout(() => { 
                this.showLoader = false;
                if(jsonStatue.status == "SUCCESS"){
                  this.message = neVersionDetails.id ? "NE Version details updated successfully !" : "NE Version details created successfully !";
                  this.successModalBlock = this.modalService.open(this.successModalRef,{keyboard: false, backdrop: "static",size:'lg',windowClass:"success-modal"});
                  this.neversionContainer.clear();
                  this.getneVersionDetails();
                } else {
                  this.displayModel(jsonStatue.reason,"failureIcon");  
                }
      
                }, 1000); */
                //Please Comment while checkIn
              });

        }

      }, 0);
    }

  }

  editNetTypeRow(event, key, index) {
    $(".createbtn").addClass("buttonDisabled");
    let editState: any = event.target;

    if (editState.className == "editRow") {
      this.editIndex = index;
      $("#networkData").find("br").remove();
      $(".saveRow").attr("class", "editRow");
      //$(".saveRow").addClass("validateForm");
      $(".cancelRow").attr("class", "deleteRow");
      if (editState.className != "editRowDisabled") { //enable click only if it is enabled
        editState.className = "saveRow";
        editState.nextSibling.className = "cancelRow";
        // To enable one edit form at a time in table
        if (this.editableFormArray.length >= 1) {
          this.editableFormArray = [];
          this.editableFormArray.push(index);
        } else {
          this.editableFormArray.push(index);
        }
      }

    } else if (editState.className != "editRowDisabled") {
      $(".saveRow").addClass("validateForm");
      let validations = this.validationData;

      validations.rules["netType_" + [index]] = { "required": true };
      validations.messages["netType_" + [index]] = { "required": "NetworkType is required" };
      this.validationData = validations;

      validator.performValidation(event, this.validationData, "save_update");
      setTimeout(() => {
        if (this.isValidForm(event)) {
          this.showLoader = true;
          this.element.nativeElement.querySelector('.createbtn').classList.remove('buttonDisabled');
          let networkDetails = {
            "networkType": $(event.target).parents("tr").find("td input").val(),
            "id": key
          };
          this.generalconfigService.updateNetworkType(networkDetails, this.sharedService.createServiceToken())
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
                        this.message = networkDetails.id ? "Network Type updated successfully !" : "Network Type created successfully !";
                        this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                        this.networkContainer.clear();
                        this.getNetworkDetails();
                      } else {
                        this.displayModel(jsonStatue.reason, "failureIcon");
                      }
                    }
                  }
                }, 1000);
              },
              error => {
                //Please Comment while checkIn
                /*   let jsonStatue: any = {"sessionId":"506db794","reason":"Updation Failed","status":"SUCCESS","serviceToken":"63524"};
                  setTimeout(() => { 
                  this.showLoader = false;
                  if(jsonStatue.status == "SUCCESS"){
                    this.message = networkDetails.id ? "Network Type updated successfully !" : "Network Type created successfully !";
                    this.successModalBlock = this.modalService.open(this.successModalRef,{keyboard: false, backdrop: "static",size:'lg',windowClass:"success-modal"});
                    this.networkContainer.clear();
                    this.getNetworkDetails();
                  } else {
                    this.displayModel(jsonStatue.reason,"failureIcon");  
                  }
        
                  }, 1000); */
                //Please Comment while checkIn
              });

        }

      }, 0);
    }

  }



  editCustomerRow(event, key, index, pgmName, status="Active") {
    if (pgmName) {
      this.cShortName = pgmName.split('-')[0];
      this.pgmNameOnly = pgmName.split('-').slice(2).join("-");
      /* this.neType = pgmName.split('-')[1]*/
      // this.pgmNameOnly = pgmName.split('-')[2];
    }
    document.querySelector("#addButton").className = "addRowDisabled";
    let editState: any = event.target;
    this.cancelCreateNew(event);

    if (editState.className == "editRow") {
      this.editCustIndex = key.id;
      this.selectedNeType = key.networkTypeDetailsEntity;
      /* if (key.networkType) {
        this.getVersion(key.networkType)
      } */
      /* $("#customerData").find("br").remove(); */
      $(".saveRow").attr("class", "editRow");
      $(".cancelRow").attr("class", "deleteRow");
      if (editState.className != "editRowDisabled") { //enable click only if it is enabled
        editState.className = "saveRow";
        editState.nextSibling.className = "cancelRow";
        // To enable one edit form at a time in table
        if (this.editableFormArray.length >= 1) {
          this.editableFormArray = [];
          this.editableFormArray.push(index);
        } else {
          this.editableFormArray.push(index);
        }
      }

    } else if (editState.className != "editRowDisabled") {
      $(".saveRow").addClass("validateForm");
      let validations = { "rules": {}, "messages": {} },
        currentTarget = $(event.target).parents("tr.inlineRow"),
        rows = $(event.target).parents("#customerDetailsTable").find("tr.inlineRow"),
        currentRow = [], netTypeExist = false;

      validations.rules["networkTypeEdit_" + [index]] = { "required": true };
      validations.rules["lsmVersionEdit_" + [index]] = { "required": true };
      /* validations.rules["statusEdit_" + [index]] = { "required": true }; */

      validations.messages["networkTypeEdit_" + [index]] = { "required": "NetworkType is required" };
      validations.messages["lsmVersionEdit_" + [index]] = { "required": "Program Name is required" };
      /* validations.messages["statusEdit_" + [index]] = { "required": "Status is required" }; */

      validator.performValidation(event, validations, "save_update");

      /* Unique Network Type and Version validation*/
      currentRow["type"] = currentTarget.find("td select#networkTypeEdit_" + index).val();
      currentRow["version"] = currentTarget.find("td select#lsmVersionEdit_" + index).val();

      for (var i = 0; i < rows.length; i++) {
        if (($(rows[i]).find("td:eq(0)").text() == currentRow["type"]) && ($(rows[i]).find("td:eq(1)").text() == currentRow["version"])) {
          this.displayModel("NetworkType & LSMVersion Exists", "failureIcon");
          netTypeExist = true;
        }
      }

      let srcProg = null;
      
      if(key.sourceProgramId) {
        srcProg = {
            "sourceProgramId": key.sourceProgramId,
            "sourceprogramName" : key.sourceprogramName
        }
      }

      setTimeout(() => {
        if (this.isValidForm(event) && !netTypeExist) {
          this.showLoader = true;
          let neTypeId = (currentTarget.find("td select#networkTypeEdit_" + index + " option:selected").attr("id")) ? currentTarget.find("td select#networkTypeEdit_" + index + " option:selected").attr("id") : null;
          let custName = $(event.target).parents("tr.imageRow").find("#customerName").val();
          let cshortNameAndNeType = currentTarget.find("td span#cshortNameAndNeType_" + index).text();
          if(!srcProg) {
            srcProg = {
                "sourceProgramId": (currentTarget.find("td select#srcProgEdit_" + index + " option:selected").attr("id")) ? parseInt(currentTarget.find("td select#srcProgEdit_" + index + " option:selected").attr("id")) : null,
                "sourceprogramName" : (currentTarget.find("td select#srcProgEdit_" + index).val()) ? currentTarget.find("td select#srcProgEdit_" + index).val() : null
            }
          }
          
          let srcProgramId = (currentTarget.find("td select#srcProgEdit_" + index + " option:selected").attr("id")) ? currentTarget.find("td select#srcProgEdit_" + index + " option:selected").attr("id") : null;
          let programId = (currentTarget.attr("id").split("row_id_").pop() != "") ? currentTarget.attr("id").split("row_id_").pop() : null;
          let customerDetails = {

            "id": $(event.target).parents("tr.imageRow").attr("id").split("row_id_").pop(),
            "customerName": custName,
            "customerShortName": this.cShortName,
            "status": status,
            "customerDetails": [
              {
                "id": programId,//(currentTarget.attr("id").split("row_id_").pop() != "") ? currentTarget.attr("id").split("row_id_").pop() : null,
                "status": currentTarget.find("td select#statusEdit_" + index).val(),
                "networkTypeDetailsEntity": {
                  "networkType": currentTarget.find("td select#networkTypeEdit_" + index).val(),
                  "id": neTypeId
                },
                "programName": cshortNameAndNeType + currentTarget.find("td input#lsmVersionEdit_" + index).val(),
                "programDescription": currentTarget.find("td textarea#programDescription_" + index).val(),
                "sourceProgramId" : srcProg.sourceProgramId,//srcProgramId ? parseInt(srcProgramId) : null,
                "sourceprogramName": srcProg.sourceprogramName,//currentTarget.find("td select#srcProgEdit_" + index).val()

              }
            ]
          }

          this.generalconfigService.addCustomer(customerDetails, this.sharedService.createServiceToken())
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
                        this.message = "Customer details updated successfully !";
                        this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                        this.getCustomerDetails();
                        // this.createForm = false;
                      } else {
                        this.displayModel(jsonStatue.reason, "failureIcon");
                      }
                    }
                  }
                }, 1000);
              },
              error => {
                //Please Comment while checkIn
                /*  let jsonStatue: any = {"sessionId":"506db794","reason":"Updation Failed","status":"SUCCESS","serviceToken":"63524"};
                   setTimeout(() => { 
                       this.showLoader = false;
                       if(jsonStatue.status == "SUCCESS"){
                         this.message = "Customer details updated successfully !";
                         this.successModalBlock = this.modalService.open(this.successModalRef,{keyboard: false, backdrop: "static",size:'lg',windowClass:"success-modal"});                        
                         this.getCustomerDetails();
                       } else {
                         this.displayModel(jsonStatue.reason,"failureIcon");  
                       }
                       
                     }, 1000); */
                //Please Comment while checkIn
              });
        }

      }, 0);
    }
  }
  saveCustomer(event) {
    let validations = this.validationData;
    //Validating  rows
    for (var i = 0; i < this.customerDetails.length; i++) {
      validations.rules["networkType_" + [i]] = { "required": true };
      validations.rules["lsmVersion_" + [i]] = { "required": true };
      /* validations.rules["status_" + [i]] = { "required": true }; */

      validations.messages["networkType_" + [i]] = { "required": "NetworkType is required" };
      validations.messages["lsmVersion_" + [i]] = { "required": "Program Name is required" };
      /* validations.messages["status_" + [i]] = { "required": "Status is required" }; */
    }
    this.validationData = validations;
    validator.performValidation(event, this.validationData, "save_update");

    const formdata = new FormData();
    let files: FileList = this.imageInputRef.nativeElement.files;
    for (var i = 0; i < files.length; i++) {
      formdata.append("icon", files[0]);
      formdata.append(files[i].name, files[i]);
    }
    setTimeout(() => {
      if (this.isValidForm(event)) {
        this.showLoader = true;
        this.element.nativeElement.querySelector('.createbtn').classList.remove('buttonDisabled');
        let currentForm = $(event.target).parents("form").find("#createNewCustomer"), custDetails = [];
        let custShortName = currentForm.find("#custShortName").val();
        console.log(custShortName);
        $.each(currentForm.find(".inlineTable tbody tr"), function (key, value) {
          let networkType = $(value).find("td select#networkType_" + key).val();
          let networkTypeId = ($(value).find("td select#networkType_" + key + " option:selected").attr("id")) ? $(value).find("td select#networkType_" + key + " option:selected").attr("id") : null;
          let rowDetails = {
            "id": null,
            "networkTypeDetailsEntity": {
              "id": networkTypeId,
              "networkType": networkType
            },
            "programName": custShortName + "-" + networkType + "-" + $(value).find("td input#lsmVersion_" + key).val(),
            "programDescription": $(value).find("td textarea#programDescription_" + key).val(),
            "status": $(value).find("td select#status_" + key).val(),
            "sourceProgramId": ($(value).find("td select#sourceProg_" + key + " option:selected").attr("id")) ? parseInt($(value).find("td select#sourceProg_" + key + " option:selected").attr("id")) : null,
            "sourceprogramName" : ($(value).find("td select#sourceProg_" + key).val()) ? $(value).find("td select#sourceProg_" + key).val() : null
          }
          custDetails.push(rowDetails);
        });

        let customerInfo = {
          "id": null,
          "customerShortName": currentForm.find("#custShortName").val(),
          "customerName": currentForm.find("#custName").val(),
          "status": "Active",
          "customerDetails": custDetails
        };
        this.sharedService.userNavigation = true; //un block user navigation

        this.generalconfigService.saveCustomer(customerInfo, formdata, this.sharedService.createServiceToken())
          .subscribe(
            data => {
              let currentData = data.json();
              setTimeout(() => {
                this.showLoader = false;
                if (currentData.sessionId == "408" || currentData.status == "Invalid User") {
                  this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
                } else {
                  if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {
                    if (currentData.status == "SUCCESS") {
                      this.message = "Customer created successfully!";
                      this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                      this.getCustomerDetails();
                      //this.displayModel("User created successfully!","successIcon");
                      // this.createForm = false;
                      this.cancelCreateNew(event);
                    } else if (currentData.status == "FAILED") {
                      this.displayModel(currentData.reason, "failureIcon");
                    }
                  }
                }
              }, 1000);
            },
            error => {
              //Please Comment while checkIn
              /* let currentData = {"sessionId":"408","reason":"failed to update","status":"SUCCESS","serviceToken":"81749"};
              setTimeout(() => {
                this.showLoader = false;
                if(currentData.status=="SUCCESS"){
                  this.message = "Customer created successfully!";
                  this.successModalBlock = this.modalService.open(this.successModalRef,{windowClass: 'success-modal',keyboard: false, backdrop: 'static' , size: 'lg'});
                  this.cancelCreateNew(event);
                } else if(currentData.status=="FAILED"){
                  this.displayModel(currentData.reason,"failureIcon");
                }   
              }, 1000); */
              //Please Comment while checkIn
            });
      }
    }, 0);
  }

  deleteCustomerRow(confirmModal, id, event, index) {
    if (event.target.className == "deleteRow") {
      this.modalService.open(confirmModal, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal' }).result.then((result) => {
        this.closeResult = `Closed with: ${result}`;
      }, (reason) => {
        this.showLoader = true;
        this.generalconfigService.deleteCustomerRow(id, this.sharedService.createServiceToken())
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
                    this.message = "Customer details deleted successfully!";
                    this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                    this.getCustomerDetails();
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
                 let jsonStatue = {"reason":"Network Type deletion failed","sessionId":"5f3732a4","serviceToken":"80356","status":"SUCCESS"};
                if(jsonStatue.status == "SUCCESS"){
                  this.message = "Customer details deleted successfully!";
                  this.successModalBlock = this.modalService.open(this.successModalRef,{keyboard: false, backdrop: "static",size:'lg',windowClass:"success-modal"});
                } else {
                    this.displayModel(jsonStatue.reason,"failureIcon");  
                }
              }, 1000); */
              //Please Comment while checkIn

              //this.alertService.error(error);TODO : This need to implement
            });
      });
    } else if (event.target.className == "cancelRow") {
      this.editCustIndex = -1;
      event.target.className = "deleteRow";
      event.target.previousSibling.className = "editRow";
      document.querySelector("#addButton").className = "addRow";

    }
  }

  /*
   * on click of edit bind the current row data in the input components
   * @param : current row event and current row json object
   * @retun : null
   */

  editRowInTable = function (event, key, index) {
    // console.log(index);
    let currentEditedForm = document.querySelector("#editedRow" + index),
      currFormEle = 0,
      currentElement;
      document.querySelector(".formEditRow").scrollIntoView({ behavior: 'smooth', block: 'center' });

    for (currFormEle = 0; currFormEle < Object.keys(key).length; currFormEle++) {
      currentElement = currentEditedForm.querySelector('#' + Object.keys(key)[currFormEle]);
      if (currentElement) {
        currentElement.value = key[Object.keys(key)[currFormEle]];
      }
      // To do: Display attached document
    }
  }

  updateConfigCheckPoint(event, index, key) {
    if (key.type == "GENERAL" || key.type == "GENERAL OV") {
      this.updateConfigWithoutID(event, index, key);
    } else {
      this.updateConfigWithID(event, index, key);
    }
  }

  updateConfigWithoutID(event, index, key) {

    setTimeout(() => {
      $("#dataWrapper").find(".scrollBody").scrollLeft(0);
    }, 0);

    let type = "";
    validator.performValidation(event, this.validationData, "save_update");
    if (this.miscParameter.label == "Session Time Out (in minutes)") {
      type = "sessionDetails";
    } else if (this.miscParameter.label == "Number Of Active Sessions Per User") {
      type = "sessionsPerUser";
    } else if (this.miscParameter.label == "Total Active Sessions") {
      type = "totalActiveSessions";
    } else if (this.miscParameter.label == "Tool Deployment") {
      type = "Deployment";
    } else if (this.miscParameter.label == "History (in days)") {
      type = "actionPerformed";
    } else if (this.miscParameter.label == "DUO AUTHENTICATION") {
      type = "DUO AUTHENTICATION";
    }else if (this.miscParameter.label == "SCHEDULE ENABLE") {
      type = "SCHEDULE ENABLE";
    }else if (this.miscParameter.label == "SCHEDULE TIME") {
      type = "SCHEDULE TIME";
    }else if (this.miscParameter.label == "SCHEDULE FREQUENCY") {
      type = "SCHEDULE FREQUENCY";
    } else if (this.miscParameter.label == "CIQ TYPE") {
      type = "ciqType";
    }
    else
    {
      type="GENERAL OV";
    }


    setTimeout(() => {
      if (this.isValidForm(event)) {
        try {
          this.jsonData ? this.editor.get() : "";
        } catch (error) {
          this.displayModel("JSON format issue!", "failureIcon");
          return;
        }
        this.showLoader = true;
        if (this.miscParameter.label == "Session Time Out (in minutes)") {
          let currentForm = event.target.parentNode.parentNode
          this.lruformData = {
            "sessiontimeout": currentForm.querySelector("#config").value
          }
        } else if (this.miscParameter.label == "Tool Deployment") {
          let currentForm = event.target.parentNode.parentNode
          this.lruformData = {
            "deploymenttype": currentForm.querySelector("#configDrpDwn").value
          }
        } else if (this.miscParameter.label == "History (in days)") {
          let currentForm = event.target.parentNode.parentNode
          this.lruformData = {
            "dynamicParam": currentForm.querySelector("#config").value
          }
        } else if (this.miscParameter.type == "GENERAL OV" && this.miscParameter.label != "OV PASSWORD" && this.miscParameter.label != "OV No. Of fetch Days" && this.miscParameter.label != "OV OVERALL INTERACTION" && this.miscParameter.label != "OV AUTOMATION" && this.miscParameter.label != "RFDB PASSWORD") {
          let currentForm = event.target.parentNode.parentNode
          this.lruformData = {
            "id":this.miscParameter.id,
            "label":this.miscParameter.label,
            //"value": currentForm.querySelector("#config1").value,
            "value": this.jsonData ? JSON.stringify(this.editor.get()) : currentForm.querySelector("#config1").value,
            "configType":this.miscParameter.configType,
          }
        }
        else if (this.miscParameter.label == "OV PASSWORD"|| this.miscParameter.label =="RFDB PASSWORD") {
          let currentForm = event.target.parentNode.parentNode
          this.lruformData = {
            "id":this.miscParameter.id,
            "label":this.miscParameter.label,
            "value": currentForm.querySelector("#config2").value,
            "configType":this.miscParameter.configType,
          }
        }else if (this.miscParameter.label == "OV OVERALL INTERACTION") {
          let currentForm = event.target.parentNode.parentNode
          this.lruformData = {
            "id":this.miscParameter.id,
            "label":this.miscParameter.label,
            "value": currentForm.querySelector("#configDrpDwn").value,
            "configType":this.miscParameter.configType
          }
        }else if (this.miscParameter.label == "OV AUTOMATION") {
          let currentForm = event.target.parentNode.parentNode
          this.lruformData = {
            "id":this.miscParameter.id,
            "label":this.miscParameter.label,
            "value": currentForm.querySelector("#configDrpDwn").value,
            "configType":this.miscParameter.configType
          }
        }else if (this.miscParameter.label == "OV No. Of fetch Days") {
          let currentForm = event.target.parentNode.parentNode
          this.lruformData = {
            "id":this.miscParameter.id,
            "label":this.miscParameter.label,
            "value": currentForm.querySelector("#config").value,
            "configType":this.miscParameter.configType
          }
        } else if (this.miscParameter.label == "DUO AUTHENTICATION") {
          let currentForm = event.target.parentNode.parentNode;
          this.lruformData = {
            "value": currentForm.querySelector("#duoConfigDrpDwn").value,
            "label": this.miscParameter.label,
            "configType": this.miscParameter.configType
          }
        }else if (this.miscParameter.label == "SCHEDULE ENABLE") {
          let currentForm = event.target.parentNode.parentNode;
          this.lruformData = {
            "value": currentForm.querySelector("#configDrpDwn").value,
            "label": this.miscParameter.label,
            "configType": this.miscParameter.configType
          }
        }else if (this.miscParameter.label == "SCHEDULE TIME") {
          let currentForm = event.target.parentNode.parentNode;
          this.lruformData = {
            "value": currentForm.querySelector("#config3").value,
            "label": this.miscParameter.label,
            "configType": this.miscParameter.configType
          }
        }else if (this.miscParameter.label == "SCHEDULE FREQUENCY") {
          let currentForm = event.target.parentNode.parentNode;
          this.lruformData = {
            "value": currentForm.querySelector("#configSnrDrpDwn").value,
            "label": this.miscParameter.label,
            "configType": this.miscParameter.configType
          }
        } else if (this.miscParameter.label == "CIQ TYPE") {
          let currentForm = event.target.parentNode.parentNode;
          this.lruformData = {
            "dynamicParam": currentForm.querySelector("#ciqTypeDrpDwn").value
          }
        } else {
          let currentForm = event.target.parentNode.parentNode
          this.lruformData = {
            // "dynamicParam": currentForm.querySelector("#config1").value
            "dynamicParam": this.jsonData ? JSON.stringify(this.editor.get()) : currentForm.querySelector("#config1").value
          }
        }


        this.generalconfigService.updateConfigWithoutID(this.lruformData, this.sharedService.createServiceToken(), type)
          .subscribe(
            data => {

              let jsonStatue = data.json();
              $("header,.page-wrapper,#footerWrapper").removeClass('displayNone');
              $("#errorPage").addClass('displayNone');
              if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
                this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
              } else {
                if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {
                  if (jsonStatue.status == "SUCCESS") {
                    this.showLoader = false;
                    if (this.miscParameter.label == "Session Time Out (in minutes)") {
                      let loginDetails = JSON.parse(sessionStorage.getItem("loginDetails"));
                      loginDetails.timeOut = isNaN(parseInt(this.lruformData.sessiontimeout)) ? 3600 : parseInt(this.lruformData.sessiontimeout) * 60  //Convert into seconds
                      sessionStorage.setItem("loginDetails", JSON.stringify(loginDetails));
                      this.sharedService.emitIdleTimeOutEvent(loginDetails.timeOut); //Convert to Seconds
                    }
                    this.displayModel("General Configuration Details updated successfully !", "successIcon");
                    this.getMiscConfigDetails();
                    // Cancle edit form
                    setTimeout(() => {
                      // Cancle edit form
                      this.cancelEditRow(event,index, key.id);
                   }, 100);
                  } else {
                    this.showLoader = false;
                    this.displayModel(jsonStatue.reason, "failureIcon");
                  }
                }
              }

            },
            error => {
              this.serverError(error);

              //Please Comment while checkIn

              /* let jsonStatue = {"sessionId":"48c61726","reason":null,"status":"SUCCESS","serviceToken":"74609"};
 
              if(jsonStatue.status == "SUCCESS"){
                this.showLoader = false;
                if (this.miscParameter.label == "Session Time Out (in minutes)") {
                  let loginDetails = JSON.parse(sessionStorage.getItem("loginDetails"));
                  loginDetails.timeOut = this.lruformData.sessiontimeout;
                  sessionStorage.setItem("loginDetails", JSON.stringify(loginDetails));
                  this.sharedService.emitIdleTimeOutEvent(parseInt(loginDetails.timeOut) * 60); //Convert to Seconds
                }
                    this.displayModel("Misc Configuration Details updated successfully!","successIcon");
                     this.getMiscConfigDetails();
                 setTimeout(() => {
                    // Cancle edit form
                    this.cancelEditRow(event,index, key.id);
 
                 }, 100);
 
              } else {
 
                  this.showLoader = false;
                  this.displayModel(jsonStatue.reason,"failureIcon");
              } */

              //Please Comment while checkIn
            });
      }
    }, 0);

  }


  updateConfigWithID(event, index, key) {
    setTimeout(() => {
      $("#dataWrapper").find(".scrollBody").scrollLeft(0);
    }, 0);

    if(this.selectedSch.length > 0) {
      this.validationData.rules.schedule.required = false;
    }
    if(this.selectedSchTime.length > 0) {
      this.validationData.rules.scheduleTime.required = false;
    }

    validator.performValidation(event, this.validationData, "save_update");
    setTimeout(() => {
      if (this.isValidForm(event)) {
        try {
          this.jsonData ? this.editor.get() : "";
        } catch (error) {
          this.displayModel("JSON format issue!", "failureIcon");
          return;
        }
        this.showLoader = true;
        /*  if (this.miscParameter.label == "Session Time Out (in minutes)") {
           let currentForm = event.target.parentNode.parentNode
           this.lruformData = {
             "sessiontimeout": currentForm.querySelector("#config").value
           }
         } else if (this.miscParameter.label == "Tool Deployment") {
           let currentForm = event.target.parentNode.parentNode
           this.lruformData = {
             "deploymenttype": currentForm.querySelector("#configDrpDwn").value
           }
         } else if (this.miscParameter.label == "History (in days)") {
           let currentForm = event.target.parentNode.parentNode
           this.lruformData = {
             "dynamicParam": currentForm.querySelector("#config").value
           }
         } else {
           let currentForm = event.target.parentNode.parentNode
           this.lruformData = {
             "dynamicParam": currentForm.querySelector("#config1").value
           }
         } */

        if (this.miscParameter.type == 'PROGRAM TEMPLATE') {
          if(this.miscParameter.inputType) {
            /*if(this.miscParameter.label == 'NE_GROW_SCHEDULE' || this.miscParameter.label == 'MIGRATION_SCHEDULE' || this.miscParameter.label == 'POST_MIGRATION_AUDIT_SCHEDULE') {
              let dataValue = this.selectedSch.join(",") + "|" + this.sharedService.getTimeFromDate(this.scheduledTime);
              this.lruformData = {
                "dynamicParam": dataValue
              }
            }
            else {
              let dataValue = this.miscParameter.inputType == "dropdown" ? this.selectedSch : this.selectedSchTime;
              this.lruformData = {
                "dynamicParam": dataValue.join(",")
              }
            }*/
            let dataValue = this.miscParameter.inputType == "dropdown" ? this.selectedSch : this.selectedSchTime;
              this.lruformData = {
                "dynamicParam": dataValue.join(",")
              }
          } 
          else {
            if (this.miscParameter.label == "FETCH_FROM_RFDB") {
              let currentForm = event.target.parentNode.parentNode;
              this.lruformData = {
                "dynamicParam": currentForm.querySelector("#duoConfigDrpDwn").value,
                // "label": this.miscParameter.label,
                // "configType": this.miscParameter.configType
              }
            }else if(this.miscParameter.label == "NE_GROW_AUTOMATION"){
              let currentForm = event.target.parentNode.parentNode;
              this.lruformData = {
                "dynamicParam": currentForm.querySelector("#duoConfigDrpDwn").value,
                // "label": this.miscParameter.label,
                // "configType": this.miscParameter.configType
              }
            }else if(this.miscParameter.label == "SUPPORT_CA"){
              let currentForm = event.target.parentNode.parentNode;
              this.lruformData = {
                "dynamicParam": currentForm.querySelector("#duoConfigDrpDwn").value,
                // "label": this.miscParameter.label,
                // "configType": this.miscParameter.configType
              }
            }
            else if(this.miscParameter.label == "FETCH_DATE"){
              let currentForm = event.target.parentNode.parentNode;
              this.lruformData = {
                "dynamicParam": this.selectedFetchTime,
                // "label": this.miscParameter.label,
                // "configType": this.miscParameter.configType
              }
            }else if(this.miscParameter.label == "MIGRATION_USECASES" || this.miscParameter.label == "NE_GROW_USECASES" || this.miscParameter.label == "POST_MIG_USECASES"){
                let dataValue = this.selectedUseCases;
                this.lruformData = {
                  "dynamicParam": dataValue.join(",")
                }
            }else if (this.miscParameter.label == "MAIL_CONFIGURATION") {
              let currentForm = event.target.parentNode.parentNode;
              this.lruformData = {
                "dynamicParam": currentForm.querySelector("#config3").value,
                // "label": this.miscParameter.label,
                // "configType": this.miscParameter.configType
              }
            } else {
              let currentForm = event.target.parentNode.parentNode
              this.lruformData = {
                // "dynamicParam": currentForm.querySelector("#config1").value
                "dynamicParam": this.jsonData ? JSON.stringify(this.editor.get()) : currentForm.querySelector("#config1").value
              }
            }
          }

        } else if (this.miscParameter.type == 'S & R') {
          if(this.miscParameter.inputType) {
            let dataValue = this.miscParameter.inputType == "dropdown" ? this.selectedSch : this.selectedSchTime;
            this.lruformData = {
              "dynamicParam": dataValue.join(",")
            }
          }
          else {
            let currentForm = event.target.parentNode.parentNode
            this.lruformData = {
              // "dynamicParam": currentForm.querySelector("#config1").value
              "dynamicParam": this.jsonData ? JSON.stringify(this.editor.get()) : currentForm.querySelector("#config1").value
            }
          }
        }

        this.generalconfigService.updateConfigWithID(this.lruformData, this.sharedService.createServiceToken(), this.miscParameter.type, key)
          .subscribe(
            data => {

              let jsonStatue = data.json();
              $("header,.page-wrapper,#footerWrapper").removeClass('displayNone');
              $("#errorPage").addClass('displayNone');
              if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
                this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
              } else {
                if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {
                  if (jsonStatue.status == "SUCCESS") {
                    this.showLoader = false;
                    this.displayModel("General Configuration Details updated successfully !", "successIcon");
                    this.getMiscConfigDetails();
                    // Cancle edit form
                    setTimeout(() => {
                      // Cancle edit form
                      this.cancelEditRow(event,index, key.id);
                   }, 100);
                  } else {
                    this.showLoader = false;
                    this.displayModel(jsonStatue.reason, "failureIcon");
                  }
                }
              }

            },
            error => {
              this.serverError(error);

              //Please Comment while checkIn

              /* let jsonStatue = {"sessionId":"48c61726","reason":null,"status":"SUCCESS","serviceToken":"74609"};

              if(jsonStatue.status == "SUCCESS"){
                this.showLoader = false;
                this.displayModel("Misc Configuration Details updated successfully!","successIcon");
                this.getMiscConfigDetails();
                 setTimeout(() => {
                    // Cancle edit form
                    this.cancelEditRow(event,index, key.id);
                 }, 100);

              } else {

                  this.showLoader = false;
                  this.displayModel(jsonStatue.reason,"failureIcon");
              } */

              //Please Comment while checkIn
            });
      }
    }, 0);
  }
  /*
        * On click delete row open a modal for confirmation
        * @param : content, userName
        * @retun : null
        */

  deleteRow(confirmModal, id, event, index) {
    if (event.target.className == "deleteRow") {
      this.modalService.open(confirmModal, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal' }).result.then((result) => {
        this.closeResult = `Closed with: ${result}`;
      }, (reason) => {
        this.showLoader = true;

        this.generalconfigService.deleteNetworkType(id, this.sharedService.createServiceToken())
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
                    this.message = "Network Type deleted successfully!";
                    this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                    this.getNetworkDetails();
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
                 let jsonStatue = {"reason":"Network Type deletion failed","sessionId":"5f3732a4","serviceToken":"80356","status":"FAILED"};
                if(jsonStatue.status == "SUCCESS"){
                  this.message = "Network Type deleted successfully!";
                  this.successModalBlock = this.modalService.open(this.successModalRef,{keyboard: false, backdrop: "static",size:'lg',windowClass:"success-modal"});
                } else {
                    this.displayModel(jsonStatue.reason,"failureIcon");  
                }
              }, 1000); */
              //Please Comment while checkIn

              //this.alertService.error(error);TODO : This need to implement
            });
      });
    } else if (event.target.className == "cancelRow") {
      this.editIndex = -1;
      event.target.className = "deleteRow";
      event.target.previousSibling.className = "editRow";
      document.querySelector(".createbtn").className = document.querySelector(".createbtn").className.replace("buttonDisabled", "");
    }
  }
  /* validates current submitted form is valid and free from errors
   * @param : pass the event
   * @retun : boolean
   */
  deleteNeVersionRow(confirmModal, id, event, index) {
    if (event.target.className == "deleteRow") {
      this.modalService.open(confirmModal, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal' }).result.then((result) => {
        this.closeResult = `Closed with: ${result}`;
      }, (reason) => {
        this.showLoader = true;

        this.generalconfigService.deleteNeVersion(id, this.sharedService.createServiceToken())
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
                    this.message = "NeVersion details deleted successfully!";
                    this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                    this.getneVersionDetails();
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
                 let jsonStatue = {"reason": "NE Version Details Deleted Successfully","sessionId":"5f3732a4","serviceToken":"80356","status":"SUCCESS"};
                if(jsonStatue.status == "SUCCESS"){
                  this.message = "NeVersion details deleted successfully!";
                  this.successModalBlock = this.modalService.open(this.successModalRef,{keyboard: false, backdrop: "static",size:'lg',windowClass:"success-modal"});
                } else {
                    this.displayModel(jsonStatue.reason,"failureIcon");  
                }
              }, 1000); */
              //Please Comment while checkIn

              //this.alertService.error(error);TODO : This need to implement
            });
      });
    } else if (event.target.className == "cancelRow") {
      this.editIndex = -1;
      event.target.className = "deleteRow";
      event.target.previousSibling.className = "editRow";
      document.querySelector(".createbtn").className = document.querySelector(".createbtn").className.replace("buttonDisabled", "");
    }
  }

  isValidForm(event) {
    return ($(event.target).parents("form").find(".error-border").length == 0) ? true : false;
  }

  cancelEditRow(event, index, identifier) {
    //console.log(identifier)
    let currentEditedForm = document.querySelector("#row_id_" + index);
    document.querySelector("#row_id_" + index).scrollIntoView({ behavior: 'smooth', block: 'start' });

    this.editableFormArray.splice(this.editableFormArray.indexOf(index), 1);

    this.checkFormEnable(index); //TODO : need to recheck this function

    currentEditedForm.lastElementChild.lastElementChild.children[0].className = "editRow";
    this.selectedSch = [];
    this.selectedSchTime = [];
    this.scheduledTime = null;
  }
  serverError(error) {
    if (error.status == 500 || error.status == 0) {
      $("header,.page-wrapper,#footerWrapper").addClass('displayNone');
      $("#errorPage").removeClass('displayNone');
    }
  }


  checkFormEnable(index) {
    let indexValue = this.editableFormArray.indexOf(index);
    return indexValue >= 0 ? true : false;
  }
  /*
    * Used to dispaly the status messages like SUCCESS/FAILURE on view
    * @param : message, messageType (successIcon/failureIcon)
    * @retun : null
    */

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
  }

  /* setMenuHighlight(selectedElement) {    
    selectedElement == "setting"? this.settingTabRef.nativeElement.classList.add('activeTab') : this.settingTabRef.nativeElement.classList.add('inactiveTab');
    selectedElement == "nwtype" ? this.settingTabRef.nativeElement.classList.add('activeTab') : this.settingTabRef.nativeElement.classList.add('inactiveTab');
    selectedElement == "customerct"? this.settingTabRef.nativeElement.classList.add('activeTab') : this.settingTabRef.nativeElement.classList.add('inactiveTab');
  } */
  createNew(event) {
    this.customerCtBlock = true;
    this.newNoDataWrapper = false;
    this.activeIds = [];//All panels closed
    let currentTarget = event.target;
    if (currentTarget.className.indexOf('buttonDisabled') < 0) {
      this.createForm = true;
      let customerDetail = {
        "networkType": "",
        "programName": "",
        "status": ""
      }
      this.customerDetails.push(customerDetail);
      setTimeout(() => {
        let tableWidth = document.getElementById('newCustomerTable').scrollWidth;
        $(".scrollBody #newCustomerData").css("min-width", (tableWidth) + "px");
        $(".scrollHead #newCustomerTable").css("width", tableWidth + "px");
        console.log(tableWidth);
        $(".scrollBody").on("scroll", function (event) {
          $(".formEditRow #formWrapper").css("margin-left", (event.target.scrollLeft - 0) + "px");
          $(".form-control-fixed").css("right", (event.target.scrollLeft * -1) + "px");
          $(".scrollHead #newCustomerTable").css("margin-left", (event.target.scrollLeft * -1) + "px");
        });
      }, 0);
      currentTarget.className += " buttonDisabled";
    }

  }
  cancelCreateNew(event) {
    this.createForm = false;
    this.customerDetails = [];
    this.custShortName = "";
    if(document.querySelector(".createbtn")) {
        document.querySelector(".createbtn").className = document.querySelector(".createbtn").className.replace("buttonDisabled", "");
    }
    if (this.customerData.customerList.length == 0) {
      this.customerCtBlock = false;
      this.newNoDataWrapper = true;
    } else {
      this.customerCtBlock = true;
      this.newNoDataWrapper = false;
    }
  }
  addInlineRow(event) {
    let customerDetail = {
      "networkType": "",
      "programName": "",
      "lsmVersionList": [],
      "status": ""
    }
    this.customerDetails.push(customerDetail);
  }
  addInlineTableRow(event, key) {
    $("#dataWrapper").find(".scrollBody").scrollLeft(0);
    this.selectedNeType = "";
    if (event.target.className.indexOf('addRowDisabled') < 0) {
      $(event.target).parents("#tableWrapper").find(".editRow").attr("class", "editRowDisabled");
      $(event.target).parents("#tableWrapper").find(".deleteRow").attr("class", "deleteRowDisabled");
      this.newRowIndex = $('#customerDetailsTable tbody tr').length;
      this.cShortName = key.customerShortName;
      this.container.createEmbeddedView(this.template);
      event.target.className = "addRowDisabled";
    }

  }
  removeRow(index) {
    console.log(index);
    $(event.target).closest('tr').remove();
    this.customerDetails.splice(index, 1);
  }

  onFileChange(event) {
    let reader = new FileReader();
    let files: FileList = this.imageInputRef.nativeElement.files;
    const img = new Image();
    img.src = window.URL.createObjectURL(files[0]);
    reader.readAsDataURL(files[0]);
    let fileName = files[0].name;
    
    let fileNameSplit = fileName.split(".");
    let fileExt = fileNameSplit[fileNameSplit.length - 1];
    if(this.allowedFileExts.indexOf(fileExt) >= 0) {
        this.validationData.rules.imageInput.customfunction = false;
    }
    else {
        this.validationData.rules.imageInput.customfunction = true;
    }

  }
  editImageSel(event, key) {
    let reader = new FileReader();
    let files: FileList = this.imageInputRef.nativeElement.files;
    const img = new Image();
    img.src = window.URL.createObjectURL(files[0]);
    reader.readAsDataURL(files[0]);
    this.editImageMode = key.id;
  }
  saveImage(event, custImageInfo) {
    this.showLoader = true;
    let editState: any = event.target,
      customerId = custImageInfo.id,
      customerName = custImageInfo.customerName;

    const formdata = new FormData();
    let files: FileList = this.imageInputRef.nativeElement.files;

    for (var i = 0; i < files.length; i++) {
      formdata.append("icon", files[0]);
      formdata.append(files[i].name, files[i]);

    }
    let fileName = files[0].name;
    // let allowedFileExts = ["png", "jpeg", "jpg", "bmp", "gif", "tif", "tiff"];
    let fileNameSplit = fileName.split(".");
    let fileExt = fileNameSplit[fileNameSplit.length - 1];
    if(this.allowedFileExts.indexOf(fileExt) >= 0) {
        this.editFileValid = true;
    }
    else {
        this.editFileValid = false;
    }

    if(this.editFileValid) {
        this.generalconfigService.saveImage(formdata, this.sharedService.createServiceToken(), customerId, customerName)
        .subscribe(
            data => {
            let jsonStatue = data.json();
            this.showLoader = false;
            if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
                this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
            } else {
                if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {
                if (jsonStatue.status == "SUCCESS") {
                    this.message = "Image changed  successfully !";
                    this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                    this.editImageMode = -1;
                    this.getCustomerDetails();
                    // this.createForm = false;
                } else {
                    this.displayModel(jsonStatue.reason, "failureIcon");
                }
                }
            }

            },
            error => {
            //Please Comment while checkIn
            let jsonStatue: any = {"sessionId":"506db794","reason":"Updation Failed","status":"SUCCESS","serviceToken":"63524"};
            /* setTimeout(() => {
                this.showLoader = false;
                if(jsonStatue.status == "SUCCESS"){
                    this.message = "Image changed  successfully";
                    this.successModalBlock = this.modalService.open(this.successModalRef,{keyboard: false, backdrop: "static",size:'lg',windowClass:"success-modal"});
                    this.editImageMode = -1;
                    this.getCustomerDetails();
                } else {
                    this.displayModel(jsonStatue.reason,"failureIcon");
                }
            
            }, 1000); */
            //Please Comment while checkIn
            });
        }
        else {
            this.showLoader = false;
            this.editImageMode = -1;
            this.displayModel("Please select valid image file!","failureIcon");
        }
  }
  getVersion(type) {
    this.versionList = [];
    for (let i of this.networkTypeList) {
      if (i.networkType == type) {
        this.versionList = i.lsmVersionList;
      }
    }
  }

  selectedNWType(type, index): any { }
  selectedNeVersionType(type, index): any { }



  getVersionList(type, index) {
    /* this.versionList = [];
      for (let i of this.networkTypeList) {
        if (i.networkType == type) {
          this.versionList= i.lsmVersionList;
        }
    
    } */
    for (let i of this.networkTypeList) {
      if (i.networkType == type) {
        this.customerDetails[index].lsmVersionList = i.lsmVersionList;
      }
    }
    /* for (let i of this.programNamesList) {
      if (i.networkType == type) {
        this.customerDetails[index].lsmVersionList = i.lsmVersionList;
      }
  } */
  }
  accordionChange(event, index) {
    console.log(index);
    this.editableFormArray = [];
  }

  addNetworkType(event) {
    this.nwtypeBlock = true;
    this.newNoDataWrapper = false;
    setTimeout(() => {
      if (event.target.className.indexOf('buttonDisabled') < 0) {
        $(".editRow").attr("class", "editRowDisabled");
        $(".deleteRow").attr("class", "deleteRowDisabled");
        this.networkContainer.createEmbeddedView(this.networkTemplate);
        event.target.className += " buttonDisabled";
      }
    }, 0);

  }

  addNeVersion(event) {
    this.neversionBlock = true;
    this.newNoDataWrapper = false;
    setTimeout(() => {
      if (event.target.className.indexOf('buttonDisabled') < 0) {
        $(".editRow").attr("class", "editRowDisabled");
        $(".deleteRow").attr("class", "deleteRowDisabled");
        this.neversionContainer.createEmbeddedView(this.neversionTemplate);
        this.programDetailsEntity = "";
        event.target.className += " buttonDisabled";
      }
    }, 0);
  }

  cancelNetworkRow(event, index) {
    document.querySelector(".createbtn").className = document.querySelector(".createbtn").className.replace("buttonDisabled", "");
    $(".editRowDisabled").attr("class", "editRow");
    $(".deleteRowDisabled").attr("class", "deleteRow");
    if (this.networkData.networkTypeDetails == 0) {
      this.nwtypeBlock = false;
      this.newNoDataWrapper = true;
    } else {
      this.nwtypeBlock = true;
      this.newNoDataWrapper = false;
    }
    this.networkContainer.clear();
  }

  cancelNeVersionRow(event, index) {
    document.querySelector(".createbtn").className = document.querySelector(".createbtn").className.replace("buttonDisabled", "");
    $(".editRowDisabled").attr("class", "editRow");
    $(".deleteRowDisabled").attr("class", "deleteRow");
    if (this.neversionData.neVersionDetails.length == 0) {
      this.neversionBlock = false;
      this.newNoDataWrapper = true;
    } else {
      this.neversionBlock = true;
      this.newNoDataWrapper = false;
    }
    this.neversionContainer.clear();
  }

  cancelCustomerRow(event) {
    document.querySelector("#addButton").className = "addRow";
    $(".editRowDisabled").attr("class", "editRow");
    $(".deleteRowDisabled").attr("class", "deleteRow");
    this.container.clear();


  }

  accordianWrapperOpen(event) {
    this.activeIds = event.panelId;
    setTimeout(() => {
      this.editableFormArray = [];
      this.editCustIndex = -1;
      let tableWidth = document.getElementById('customerDetails').scrollWidth;
      $(".scrollBody #customerData").css("min-width", (tableWidth) + "px");
      $(".scrollHead table").css("width", tableWidth + "px");
      console.log(tableWidth);

      $(".scrollBody").on("scroll", function (event) {
        $(".formEditRow #formWrapper").css("margin-left", (event.target.scrollLeft - 5) + "px");
        $(".form-control-fixed").css("right", (event.target.scrollLeft * -1) + "px");
        $(".scrollHead table").css("margin-left", (event.target.scrollLeft * -1) + "px");
      });
      $(".scrollBody").css("max-height", (this.tableDataHeight - 10) + "px");
    }, 0);

  }

  deleteCustomer(confirmModal, id) {
    event.stopPropagation();
    this.modalService.open(confirmModal, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal' }).result.then((result) => {
      this.closeResult = `Closed with: ${result}`;
    }, (reason) => {
      this.showLoader = true;
      this.generalconfigService.deleteCustomer(id, this.sharedService.createServiceToken())
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
                  this.message = "Customer deleted successfully!";
                  this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                  this.getCustomerDetails();
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
                let jsonStatue = {"reason":"Network Type deletion failed","sessionId":"5f3732a4","serviceToken":"80356","status":"FAILED"};
               if(jsonStatue.status == "SUCCESS"){
                 this.message = "Customer deleted successfully!";
                 this.successModalBlock = this.modalService.open(this.successModalRef,{keyboard: false, backdrop: "static",size:'lg',windowClass:"success-modal"});
                 this.getCustomerDetails();
               } else {
                   this.displayModel(jsonStatue.reason,"failureIcon");  
               }
             }, 1000);  */
            //Please Comment while checkIn

            //this.alertService.error(error);TODO : This need to implement
          });
    });
  }

  changeSorting(predicate, event, index, tablename, parent=""){
    if(tablename=='networkTypeDetails')
    {
      this.sharedService.dynamicSort(predicate, event, index, this.networkData.networkTypeDetails, parent);

    }
    else if(tablename=='neVersionDetails')
    {
      this.sharedService.dynamicSort(predicate, event, index, this.neversionData.neVersionDetails, parent);
    }
  }

  compareFn(o1: any, o2: any) {
    return o1 && o2 ? o1.id === o2.id : o1 === o2;
  }

  getDefaultUseNeGrowCases(programName) {
    let useCases = [];
    switch(programName) {
        case "VZN-5G-MM":
            useCases = ["pnp","AUCaCell","AU"];
            break;
        case "VZN-4G-USM-LIVE":
            useCases = ["GrowEnb","pnp","GrowCell", "CA_Usecase", "DeleteNE", "NeCreationTime"];
            break;
        case "VZN-5G-CBAND":
            useCases = ["vDUpnp","vDUcell","vDUGrow"];
            break;  
	case "VZN-5G-DSS":
            useCases = ["pnpGrow","vDUCellGrow","vDUGrow"];
            break;
        case "VZN-4G-FSU":
              useCases = ["GrowFSU", "DeleteNE", "NeCreationTime"];
              break;    
        default:
            useCases = ["GrowEnb"];
           
    }
    return useCases;
}
getDefaultUseCases(programName) {
  let useCases = [];
  switch(programName) {
      case "VZN-5G-MM":
          useCases = ["Anchor_CSL_UseCase","CSL_Usecase","AU_Commision_Usecase","ACPF_A1A2_Config_Usecase","RF_Scripts_Usecase","ENDC_X2_UseCase",];
          break;
      case "VZN-5G-DSS":
          useCases = ["Pre-Check_RF_Scripts_Usecase","Rollback_RF_Scripts_Usecase","Cutover_RF_Scripts_Usecase"];
          break;
      case "VZN-5G-CBAND":
          useCases = ["Pre-Check_RF_Scripts_Usecase"];
          break;
      case "VZN-4G-USM-LIVE":
          useCases = ["RFUsecase","EndcUsecase"];
          break;
      default:
          useCases = ["CommissionScriptUsecase", "RFUsecase"];
  }
  return useCases;
}

getDefaultUseCasesPostMigration(programName) {
  let useCases = [];
  switch(programName) {
      case "VZN-5G-MM":
          useCases = ["ACPF_Usecase_21B","TWAMP_Usecase_21B","AU_Usecase_21B","AUPF_Usecase_21B","ENDC_Usecase_21B"];
          break;
      case "VZN-5G-DSS":
          useCases = ["vDUInstantiationAudit","PostMigrationAudit","vDUGrowAudit"];
          break;
      case "VZN-5G-CBAND":
          useCases = ["vDUInstantiationAudit","PostMigrationAudit"];
          break;
      default:
          useCases = ["OcnsTest_Usecase","OranAudit_Usecase"];
  }
  return useCases;
}
  createScheduleJosn(fetchDays = 5) {
    this.scheduleList = [{"label": "OFF", "disabled": false}];
    // {"label": "OFF", "disabled": false}
    for(let i = 0; i < fetchDays; i++) {
      let tempObj = {
        "label": "D-"+i, "disabled": false
      }
      this.scheduleList.push(tempObj);
    }
  }
  onChangeSchedule(selectedSch) {
    if(selectedSch.indexOf("OFF") > -1) {
      this.scheduleList[0].disabled = false;
      // console.log(this.scheduleList);
      for(let i = 1; i < this.scheduleList.length; i++) {
        // Starting from index 1, which is not OFF, and disable all parameter
        this.scheduleList[i].disabled = true;
      }
    }
    else {
      if(selectedSch.length == 0) {
        for(let i = 0; i < this.scheduleList.length; i++) {
          // enable all parameter
          this.scheduleList[i].disabled = false;
        }
      }
      else {
        this.scheduleList[0].disabled = true;
        for(let i = 1; i < this.scheduleList.length; i++) {
          // Starting from index 1, which is not OFF, and enable all parameter
          this.scheduleList[i].disabled = false;
        }
      }
    }
  }
  onChangeScheduleTime(selectedSchTime) {
    if(selectedSchTime.indexOf("OFF") > -1) {
      // console.log(this.timeList);
      this.timeList[0].disabled = false;
      for(let i = 1; i < this.timeList.length; i++) {
        // Starting from index 1, which is not OFF, and disable all parameter
        this.timeList[i].disabled = true;
      }
    }
    else {
      if(selectedSchTime.length == 0) {
        for(let i = 0; i < this.timeList.length; i++) {
          // enable all parameter
          this.timeList[i].disabled = false;
        }
      }
      else {
        this.timeList[0].disabled = true;
        for(let i = 1; i < this.timeList.length; i++) {
          // Starting from index 1, which is not OFF, and enable all parameter
          this.timeList[i].disabled = false;
        }
      }
    }
  }

  generateTimeList(interval) {
    var result = [];
    var start = new Date(1, 1, 1, 7, 0);
    var end = new Date(1, 1, 2, 7, 0);
    result.push({
      "label": "OFF",
      "disabled": false
    });
    for (var d = start; d < end; d.setMinutes(d.getMinutes() + interval)) {
      result.push(this.format(d));
    }
    return result
  }

  format(inputDate) {
    var hours = inputDate.getHours();
    var minutes = inputDate.getMinutes();

    hours = hours < 10 ? ("0" + hours) : hours;
    minutes = minutes < 10 ? ("0" + minutes) : minutes;
    let tempObj = {
      "label": hours + ":" + minutes,
      "disabled": false
    }
    return tempObj;
  }

}
