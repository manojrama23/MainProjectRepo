import { ViewContainerRef, Input, ElementRef, Renderer, Component, OnInit, ViewChild, HostListener } from '@angular/core';
import { NgbModal, ModalDismissReasons } from '@ng-bootstrap/ng-bootstrap';
import { ChecklistService } from '../services/checklist.service';
import { SharedService } from '../services/shared.service';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { filter, map, mergeMap } from 'rxjs/operators';
import { validator } from '../validation';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { Router, NavigationEnd } from '@angular/router';
import { DatePipe } from '@angular/common';
import * as $ from 'jquery';
import * as _ from 'underscore';
import { KeyedRead } from '@angular/compiler';

@Component({
    selector: 'rct-checklist',
    templateUrl: './checklist.component.html',
    styleUrls: ['./checklist.component.scss'],
    providers: [ChecklistService]
})
export class ChecklistComponent implements OnInit {
  navigationSubscription: any;
  showLoader:boolean = true;
  showNoDataFound: boolean;
  searchStatus: string;
  objectKeys = Object.keys;
  serachBy:any;
  searchCriteria: any;
  sheetDispTab:boolean =false;
  tableData:any;
 /*  eNodeMapData:any; */
  tableShowHide :boolean = false;
/*   eNodeBTableShowHide :boolean = false; */
  treeShowHide :boolean = false;
  closeResult:string;
  privilegeSetting : object;
  noDataVisibility :boolean = false;
/*   noDataVisibilityENodeB :boolean = false; */
  showModelMessage: boolean = false;
  messageType: any;
  modelData :any;
  sessionExpiredModalBlock : any; // Helps to close/open the model window
  successModalBlock : any;
  message : any;
  aircraftOffloadBlock:any;
  timezone: any;
  searchByChecklistBlock: boolean = false;
/*   searchByNesBlock: boolean = false; */
  getChecklistList:any;
  getCheckList:any;
/*   getNeslist:any; */
  tableDataHeight:any;
  checklistListData:any;
  ciqListData:any;
  nesListData:any;
  editMode: number = -1;
 /*  eNBList:any; */
  pageCount: any; // for pagination
  currentPage: any; // for pagination
  pageSize: any; // for pagination
  totalPages: any; // for pagination
  TableRowLength: any; // for pagination
  paginationDetails: any; // for pagination
  pageRenge: any; // for pagination
  paginationDisabbled: boolean = false;
  pager: any = {}; // pager Object
  fileName:any;
  fileId:any;
  enbId:any;
  enbName:any;
  editableFormArray = [];
  addtableFormArray = [];
  mOptions = [];
  dropdownSettings = {};
  selectedNes = [];
  editSaveStage:any;
  enbMenuListDetails:any;
  menuName:any;
  selectedLsmVersion:any;
  selectedNetworkType:any;
  selectedNetworkTypeId:any;
  tableEdit:boolean = false;
  max = new Date();
  fromDate:any;
  toDate:any;
  checklistSheetList= [];
  checklistTableData:any;
  checklistDetails:any;
  sheetName:any;
  sheetHighlight:any;
  firstSheet:any;
  getCiqList:any;
  ciqFileName:any;
  getChecklist:any;
  errMessage:boolean= false;
  selectedProgram:any;
  ciqNameConfig:object;
  ciqFileDetails:any = "";
  scriptDetails: any = [];
  scriptDetailsModalBlock: any;
  checkListStepIndex: any = "";
  checkListConfigType: any = "";
  programChangeSubscription: any;

  validationData: any = {
        "rules": {
            "ciqName": {
                "required": true
            },
             "fromDate": {
                "required": true
            }
            ,
             "toDate": {
                "required": true
            }
        },
        "messages": {
            "ciqName": {
                "required": "Please Select CIQ Name"
            },
             "fromDate": {
                "required": "" //Please Select Start Date
            },
            "toDate": {
               "required": "" //Please Select End Date
           }
        }
    };
    @ViewChild('confirmModal') confirmModalRef: ElementRef;
    @ViewChild('sessionExpiredModal') sessionExpiredModalRef: ElementRef;
    @ViewChild('successModal') successModalRef: ElementRef;

    constructor(
        private element: ElementRef,
        private renderer: Renderer,
        private router: Router,
        private modalService: NgbModal,
        private checklistService: ChecklistService,
        private sharedService: SharedService,
        private datePipe: DatePipe
    ) {
      /* this.navigationSubscription = this.router.events.subscribe((e:any) => {        
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
          if (pgm) {
            this.loadInitialData();
          }
      });

  }
  loadInitialData(){
        this.currentPage = 1;
        this.totalPages = 1;
        this.TableRowLength = 2;
        this.pageSize = 10;
        this.ciqFileDetails= [];
        this.searchStatus = 'load';
        let paginationDetails = {
            "count": this.TableRowLength,
            "page": this.currentPage
        };
        this.showLoader = true;
        this.sheetDispTab = false;
        this.getCiqList = [];
        this.paginationDetails = paginationDetails;
        this.noDataVisibility = true;
        this.tableShowHide  = false;  
        this.ciqNameConfig = {
          displayKey: "ciqFileName", 
          search: true,
          height: '200px', 
          placeholder: '--Select--', 
          customComparator: () => { },
          //limitTo: this.getCiqList.length, 
          moreText: 'more',                    
          noResultsFound: 'No results found!',
          searchPlaceholder: 'Search', 
          searchOnKey: 'ciqFileName',
      }
        this.getCheckListData(this.fromDate,this.toDate);

      }


      getCheckListData(fromDate,toDate){
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

        
        this.checklistService.getCheckListData(this.sharedService.createServiceToken(),fromDt,toDt, this.searchStatus)
            .subscribe(
                data => {
                    setTimeout(() => {
                      
                        let jsonStatue = data.json();
                        this.checklistListData = data.json();
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
                                  this.fromDate = new Date(this.checklistListData.fromDate);
                                  this.toDate = new Date(this.checklistListData.toDate);
                                this.getCiqList =this.checklistListData.getChecklistList;
                                if(this.getCiqList.length > 0)
                                 this.ciqFileDetails = this.getCiqList[0];
                                } else {
                                    this.showLoader = false;
                                    this.getCiqList = [];
                                }                               
                            }
                        }

                    }, 1000);
                },
                error => {
                    //Please Comment while checkIn
                   /*  setTimeout(() => {
                        this.showLoader = false;
                        this.checklistListData = {"fromDate":"02/20/2019","toDate":"02/27/2019","sessionId":"c5697031","serviceToken":"96830","getChecklistList":[{"id":4,"programDetailsEntity":{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":null,"status":"InActive","creationDate":"2019-02-11T09:24:41.000+0000","createdBy":"superadmin"},"ciqFileName":"MODIFIED_Complete CH52XC421 STA CDU30 2.5 TDD mMIMO for BBU No_ 1.xlsx","ciqFilePath":"/home/user/Documents/","scriptFileName":"Script.zip","scriptFilePath":"/home/user/RCT/rctsoftware/SMART/Customer/3/PreMigration/Input/SCRIPT/Script.zip","checklistFileName":"network_config_details.xlsx","checklistFilePath":"/home/user/RCT/rctsoftware/SMART/Customer/3/PreMigration/Input/CHECKLIST/network_config_details.xlsx","ciqVersion":"MODIFIED","fileSourceType":"UPLOAD","uploadBy":"admin","remarks":"Testing....","creationDate":"2019-02-26T09:21:33.000+0000"},{"id":4,"programDetailsEntity":{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":null,"status":"InActive","creationDate":"2019-02-11T09:24:41.000+0000","createdBy":"superadmin"},"ciqFileName":"MODIFIED_Complete CH52XC421 STA CDU30 2.5 TDD mMIMO for BBU No_ 12.xlsx","ciqFilePath":"/home/user/Documents/","scriptFileName":"Script.zip","scriptFilePath":"/home/user/RCT/rctsoftware/SMART/Customer/3/PreMigration/Input/SCRIPT/Script.zip","checklistFileName":"network_config_details12.xlsx","checklistFilePath":"/home/user/RCT/rctsoftware/SMART/Customer/3/PreMigration/Input/CHECKLIST/network_config_details.xlsx","ciqVersion":"MODIFIED","fileSourceType":"UPLOAD","uploadBy":"admin","remarks":"Testing....","creationDate":"2019-02-26T09:21:33.000+0000"},{"id":4,"programDetailsEntity":{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":null,"status":"InActive","creationDate":"2019-02-11T09:24:41.000+0000","createdBy":"superadmin"},"ciqFileName":"MODIFIED_Complete CH52XC421 STA CDU30 2.5 TDD mMIMO for BBU No_ 1345.xlsx","ciqFilePath":"/home/user/Documents/","scriptFileName":"Script.zip","scriptFilePath":"/home/user/RCT/rctsoftware/SMART/Customer/3/PreMigration/Input/SCRIPT/Script.zip","checklistFileName":"network_config_details3453.xlsx","checklistFilePath":"/home/user/RCT/rctsoftware/SMART/Customer/3/PreMigration/Input/CHECKLIST/network_config_details.xlsx","ciqVersion":"MODIFIED","fileSourceType":"UPLOAD","uploadBy":"admin","remarks":"Testing....","creationDate":"2019-02-26T09:21:33.000+0000"}],"status":"SUCCESS"};
                        
                        if (this.checklistListData.status == "SUCCESS") {

                            this.fromDate = new Date(this.checklistListData.fromDate);
                            this.toDate = new Date(this.checklistListData.toDate);
                          this.getCiqList =this.checklistListData.getChecklistList;
                          if(this.getCiqList.length > 0)
                                 this.ciqFileDetails = this.getCiqList[0];
                        } else {
                            this.showLoader = false;
                            this.getCiqList = [];
                        }
                        
                    }, 1000); */
                    //Please Comment while checkIn
                }); 

              }



             /*
   * Used to serach by checklist name and display the CIQ details
   * @param : event, serachBy
   * @retun : null
   */
  getDeatilsByChecklist(event){
    if (this.ciqFileDetails !="" && this.ciqFileDetails != undefined &&  this.ciqFileDetails.ciqFileName) {
      this.validationData.rules.ciqName.required = false;

  } else {
      this.validationData.rules.ciqName.required = true;

  }
  validator.performValidation(event, this.validationData, "search");
    this.tableShowHide = false;    
    //this.fileName = this.checklistDetails.ciqFileName;
    //this.fileName = this.checklistDetails.checklistFileName;
    this.fileName = this.checklistListData.checklistFileName;
   
    this.currentPage = 1;
    let paginationDetails = {
                  "count": parseInt(this.pageSize),
                  "page": parseInt(this.currentPage)
                 };

    this.paginationDetails = paginationDetails;
    if((!this.ciqFileDetails.ciqFileName) || (!this.fromDate)  || (!this.toDate) )
    {
      this.checklistSheetList= [];
    }
    // TO get the searched data
   
        setTimeout(() => {
            if (this.isValidForm(event)) {
                this.showLoader = true;
                  //this.getAllChecklistDetails();
                  this.getChecklistSheetDetails();
                }
        }, 0);

    }

    ngOnDestroy() {
      // avoid memory leaks here by cleaning up after ourselves. If we 
      // don't then we will continue to run our ngOnInit()
      // method on every navigationEnd event.
      this.programChangeSubscription.unsubscribe();
      if (this.navigationSubscription) {
        this.navigationSubscription.unsubscribe();
      }
    }

  

  getChecklistSheetDetails() {    
    this.showLoader = true;
    this.checklistService.getChecklistSheetDetails(this.sharedService.createServiceToken(),this.fileName,this.ciqFileDetails.checklistFileName,this.ciqFileDetails.ciqFileName)
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
                  this.checklistSheetList = jsonStatue.SheetDetails;
                  if (this.checklistSheetList.length == 0) {                    
                    this.noDataVisibility = true;
                  } else {
                    this.sheetDispTab = false;
                    this.noDataVisibility = false;
                    let index =0;
                    this.getAllChecklistDetails(this.checklistSheetList[0],index);
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
         /*  setTimeout(() => {
            this.showLoader = false;
            this.ciqFileName=this.ciqFileDetails.ciqFileName;
            
            //this.tableData = {"sessionId":"fee236ab","serviceToken":"78928","status":"SUCCESS","pageCount":2,"checklistUploadDetails":[{"id":451,"fileName":"FDMIMO_FIT_CIQ_08242018_SprintTemplate2.xlsx","checklistMap":{"BBU_Type":"","Cascade":"CH13XC046","Market":"Chicago","Market_ID":1,"LSMR_Name":"chglsmr03lems1","Network_Site_Code":"","eNB_ID":"516790","eNodeB_Name":"CHCGILFCBBULTE0516790","Latitude":"","Longitude":"","TAC_Decimal":"3F13","RRH_Top_Bottom":"","Band":"41","Sector_ID":"1","Cell_ID":"3","EUTRAN_Cell_Global_Id":"","PCI":"55","RSI":"288","Bandwidth":"20MHz","Start_EARFCN":"40994","DL_EARFCN":"41094","UL_EARFCN":"41094","Tx_Diversity":"64T","Initiate_SON":"","Carrier_Aggregation":"","Frame_Config_Version":"","Antenna_Model":"","Antenna_Vendor":"","Azimuth":"","Electrical_Tilt":"","Mechanical_Tilt":"","FDD/TDD":"TDD","DL_Center_Freq_MHz":"2640.4","UL_Center_Freq_MHz":"2640.4","Rx_Diveristy":"64R","Tx_Path_Assignment":"64T_ALL","Attenuation_Path":"0","RS_Boost":"0","eNB_OAM_VLAN":"34","eNB_OAM_VLAN_prefix_(/30)":"30","OAM_GW_IP":"111.3.152.169","eNB_OAM_IP":"111.3.152.170","eNB_S&B_VLAN":"42","eNB_S&B_VLAN_prefix_(/30)":"30","S&B_GW_IP":"10.202.198.169","eNB_S&B_IP":"10.202.198.170","Current_BH_Port":"1","CDU30_BH_Port":"1","Cabinet_(Indoor_Outdoor)":"ADV_OUTDOOR_DIST_TYPE","IOMONITOR_COUNT":"1","EAIU_Type":"EAIU4-U","EAIU_SN":"SE2C408026","Expansion_Cabinet":"No","RRH_Type":"MTP02P_410","MAU-ID":"0","No*_of_ALD":"1","MME_Info_1":"10.158.212.4|0.0.0.0|310|120","MME_Info_2":"10.156.35.44|0.0.0.0|310|120","MME_Info_3":"10.158.212.20|0.0.0.0|310|120","MME_Info_4":"10.156.34.28|0.0.0.0|310|120","MME_Info_5":"10.88.212.81|10.88.212.82|312|530","MME_Info_6":"10.88.152.33|0.0.0.0|310|120","MME_Info_7":"10.88.212.33|0.0.0.0|310|120","Comments":""}},{"id":452,"fileName":"FDMIMO_FIT_CIQ_08242018_SprintTemplate2.xlsx","checklistMap":{"BBU_Type":"","Cascade":"CH13XC046","Market":"Chicago","Market_ID":2,"LSMR_Name":"chglsmr03lems1","Network_Site_Code":"","eNB_ID":"516790","eNodeB_Name":"CHCGILFCBBULTE0516790","Latitude":"","Longitude":"","TAC_Decimal":"3F13","RRH_Top_Bottom":"","Band":"41","Sector_ID":"2","Cell_ID":"4","EUTRAN_Cell_Global_Id":"","PCI":"54","RSI":"296","Bandwidth":"20MHz","Start_EARFCN":"40994","DL_EARFCN":"41094","UL_EARFCN":"41094","Tx_Diversity":"64T","Initiate_SON":"","Carrier_Aggregation":"","Frame_Config_Version":"","Antenna_Model":"","Antenna_Vendor":"","Azimuth":"","Electrical_Tilt":"","Mechanical_Tilt":"","FDD/TDD":"TDD","DL_Center_Freq_MHz":"2640.4","UL_Center_Freq_MHz":"2640.4","Rx_Diveristy":"64R","Tx_Path_Assignment":"64T_ALL","Attenuation_Path":"0","RS_Boost":"0","eNB_OAM_VLAN":"34","eNB_OAM_VLAN_prefix_(/30)":"30","OAM_GW_IP":"111.3.152.169","eNB_OAM_IP":"111.3.152.170","eNB_S&B_VLAN":"42","eNB_S&B_VLAN_prefix_(/30)":"30","S&B_GW_IP":"10.202.198.169","eNB_S&B_IP":"10.202.198.170","Current_BH_Port":"1","CDU30_BH_Port":"1","Cabinet_(Indoor_Outdoor)":"ADV_OUTDOOR_DIST_TYPE","IOMONITOR_COUNT":"1","EAIU_Type":"EAIU4-U","EAIU_SN":"SE2C408026","Expansion_Cabinet":"No","RRH_Type":"MTP02P_410","MAU-ID":"1","No*_of_ALD":"1","MME_Info_1":"10.158.212.4|0.0.0.0|310|120","MME_Info_2":"10.156.35.44|0.0.0.0|310|120","MME_Info_3":"10.158.212.20|0.0.0.0|310|120","MME_Info_4":"10.156.34.28|0.0.0.0|310|120","MME_Info_5":"10.88.212.81|10.88.212.82|312|530","MME_Info_6":"10.88.152.33|0.0.0.0|310|120","MME_Info_7":"10.88.212.33|0.0.0.0|310|120","Comments":""}},{"id":453,"fileName":"FDMIMO_FIT_CIQ_08242018_SprintTemplate2.xlsx","checklistMap":{"BBU_Type":"","Cascade":"CH13XC046","Market":"Chicago","Market_ID":3,"LSMR_Name":"chglsmr03lems1","Network_Site_Code":"","eNB_ID":"516790","eNodeB_Name":"CHCGILFCBBULTE0516790","Latitude":"","Longitude":"","TAC_Decimal":"3F13","RRH_Top_Bottom":"","Band":"41","Sector_ID":"3","Cell_ID":"5","EUTRAN_Cell_Global_Id":"","PCI":"56","RSI":"304","Bandwidth":"20MHz","Start_EARFCN":"40994","DL_EARFCN":"41094","UL_EARFCN":"41094","Tx_Diversity":"64T","Initiate_SON":"","Carrier_Aggregation":"","Frame_Config_Version":"","Antenna_Model":"","Antenna_Vendor":"","Azimuth":"","Electrical_Tilt":"","Mechanical_Tilt":"","FDD/TDD":"TDD","DL_Center_Freq_MHz":"2640.4","UL_Center_Freq_MHz":"2640.4","Rx_Diveristy":"64R","Tx_Path_Assignment":"64T_ALL","Attenuation_Path":"0","RS_Boost":"0","eNB_OAM_VLAN":"34","eNB_OAM_VLAN_prefix_(/30)":"30","OAM_GW_IP":"111.3.152.169","eNB_OAM_IP":"111.3.152.170","eNB_S&B_VLAN":"42","eNB_S&B_VLAN_prefix_(/30)":"30","S&B_GW_IP":"10.202.198.169","eNB_S&B_IP":"10.202.198.170","Current_BH_Port":"1","CDU30_BH_Port":"1","Cabinet_(Indoor_Outdoor)":"ADV_OUTDOOR_DIST_TYPE","IOMONITOR_COUNT":"1","EAIU_Type":"EAIU4-U","EAIU_SN":"SE2C408026","Expansion_Cabinet":"No","RRH_Type":"MTP02P_410","MAU-ID":"2","No*_of_ALD":"1","MME_Info_1":"10.158.212.4|0.0.0.0|310|120","MME_Info_2":"10.156.35.44|0.0.0.0|310|120","MME_Info_3":"10.158.212.20|0.0.0.0|310|120","MME_Info_4":"10.156.34.28|0.0.0.0|310|120","MME_Info_5":"10.88.212.81|10.88.212.82|312|530","MME_Info_6":"10.88.152.33|0.0.0.0|310|120","MME_Info_7":"10.88.212.33|0.0.0.0|310|120","Comments":""}},{"id":454,"fileName":"FDMIMO_FIT_CIQ_08242018_SprintTemplate2.xlsx","checklistMap":{"BBU_Type":"","Cascade":"CH13XC046","Market":"Chicago","Market_ID":4,"LSMR_Name":"chglsmr03lems1","Network_Site_Code":"","eNB_ID":"516790","eNodeB_Name":"CHCGILFCBBULTE0516790","Latitude":"","Longitude":"","TAC_Decimal":"3F13","RRH_Top_Bottom":"","Band":"41","Sector_ID":"1","Cell_ID":"12","EUTRAN_Cell_Global_Id":"","PCI":"55","RSI":"288","Bandwidth":"20MHz","Start_EARFCN":"41192","DL_EARFCN":"41292","UL_EARFCN":"41292","Tx_Diversity":"64T","Initiate_SON":"","Carrier_Aggregation":"","Frame_Config_Version":"","Antenna_Model":"","Antenna_Vendor":"","Azimuth":"","Electrical_Tilt":"","Mechanical_Tilt":"","FDD/TDD":"TDD","DL_Center_Freq_MHz":"2660.2","UL_Center_Freq_MHz":"2660.2","Rx_Diveristy":"64R","Tx_Path_Assignment":"64T_ALL","Attenuation_Path":"0","RS_Boost":"0","eNB_OAM_VLAN":"34","eNB_OAM_VLAN_prefix_(/30)":"30","OAM_GW_IP":"111.3.152.169","eNB_OAM_IP":"111.3.152.170","eNB_S&B_VLAN":"42","eNB_S&B_VLAN_prefix_(/30)":"30","S&B_GW_IP":"10.202.198.169","eNB_S&B_IP":"10.202.198.170","Current_BH_Port":"1","CDU30_BH_Port":"1","Cabinet_(Indoor_Outdoor)":"ADV_OUTDOOR_DIST_TYPE","IOMONITOR_COUNT":"1","EAIU_Type":"EAIU4-U","EAIU_SN":"SE2C408026","Expansion_Cabinet":"No","RRH_Type":"MTP02P_410","MAU-ID":"0","No*_of_ALD":"1","MME_Info_1":"10.158.212.4|0.0.0.0|310|120","MME_Info_2":"10.156.35.44|0.0.0.0|310|120","MME_Info_3":"10.158.212.20|0.0.0.0|310|120","MME_Info_4":"10.156.34.28|0.0.0.0|310|120","MME_Info_5":"10.88.212.81|10.88.212.82|312|530","MME_Info_6":"10.88.152.33|0.0.0.0|310|120","MME_Info_7":"10.88.212.33|0.0.0.0|310|120","Comments":""}},{"id":455,"fileName":"FDMIMO_FIT_CIQ_08242018_SprintTemplate2.xlsx","checklistMap":{"BBU_Type":"","Cascade":"CH13XC046","Market":"Chicago","Market_ID":5,"LSMR_Name":"chglsmr03lems1","Network_Site_Code":"","eNB_ID":"516790","eNodeB_Name":"CHCGILFCBBULTE0516790","Latitude":"","Longitude":"","TAC_Decimal":"3F13","RRH_Top_Bottom":"","Band":"41","Sector_ID":"2","Cell_ID":"13","EUTRAN_Cell_Global_Id":"","PCI":"54","RSI":"296","Bandwidth":"20MHz","Start_EARFCN":"41192","DL_EARFCN":"41292","UL_EARFCN":"41292","Tx_Diversity":"64T","Initiate_SON":"","Carrier_Aggregation":"","Frame_Config_Version":"","Antenna_Model":"","Antenna_Vendor":"","Azimuth":"","Electrical_Tilt":"","Mechanical_Tilt":"","FDD/TDD":"TDD","DL_Center_Freq_MHz":"2660.2","UL_Center_Freq_MHz":"2660.2","Rx_Diveristy":"64R","Tx_Path_Assignment":"64T_ALL","Attenuation_Path":"0","RS_Boost":"0","eNB_OAM_VLAN":"34","eNB_OAM_VLAN_prefix_(/30)":"30","OAM_GW_IP":"111.3.152.169","eNB_OAM_IP":"111.3.152.170","eNB_S&B_VLAN":"42","eNB_S&B_VLAN_prefix_(/30)":"30","S&B_GW_IP":"10.202.198.169","eNB_S&B_IP":"10.202.198.170","Current_BH_Port":"1","CDU30_BH_Port":"1","Cabinet_(Indoor_Outdoor)":"ADV_OUTDOOR_DIST_TYPE","IOMONITOR_COUNT":"1","EAIU_Type":"EAIU4-U","EAIU_SN":"SE2C408026","Expansion_Cabinet":"No","RRH_Type":"MTP02P_410","MAU-ID":"1","No*_of_ALD":"1","MME_Info_1":"10.158.212.4|0.0.0.0|310|120","MME_Info_2":"10.156.35.44|0.0.0.0|310|120","MME_Info_3":"10.158.212.20|0.0.0.0|310|120","MME_Info_4":"10.156.34.28|0.0.0.0|310|120","MME_Info_5":"10.88.212.81|10.88.212.82|312|530","MME_Info_6":"10.88.152.33|0.0.0.0|310|120","MME_Info_7":"10.88.212.33|0.0.0.0|310|120","Comments":""}},{"id":456,"fileName":"FDMIMO_FIT_CIQ_08242018_SprintTemplate2.xlsx","checklistMap":{"BBU_Type":"","Cascade":"CH13XC046","Market":"Chicago","Market_ID":6,"LSMR_Name":"chglsmr03lems1","Network_Site_Code":"","eNB_ID":"516790","eNodeB_Name":"CHCGILFCBBULTE0516790","Latitude":"","Longitude":"","TAC_Decimal":"3F13","RRH_Top_Bottom":"","Band":"41","Sector_ID":"3","Cell_ID":"14","EUTRAN_Cell_Global_Id":"","PCI":"56","RSI":"304","Bandwidth":"20MHz","Start_EARFCN":"41192","DL_EARFCN":"41292","UL_EARFCN":"41292","Tx_Diversity":"64T","Initiate_SON":"","Carrier_Aggregation":"","Frame_Config_Version":"","Antenna_Model":"","Antenna_Vendor":"","Azimuth":"","Electrical_Tilt":"","Mechanical_Tilt":"","FDD/TDD":"TDD","DL_Center_Freq_MHz":"2660.2","UL_Center_Freq_MHz":"2660.2","Rx_Diveristy":"64R","Tx_Path_Assignment":"64T_ALL","Attenuation_Path":"0","RS_Boost":"0","eNB_OAM_VLAN":"34","eNB_OAM_VLAN_prefix_(/30)":"30","OAM_GW_IP":"111.3.152.169","eNB_OAM_IP":"111.3.152.170","eNB_S&B_VLAN":"42","eNB_S&B_VLAN_prefix_(/30)":"30","S&B_GW_IP":"10.202.198.169","eNB_S&B_IP":"10.202.198.170","Current_BH_Port":"1","CDU30_BH_Port":"1","Cabinet_(Indoor_Outdoor)":"ADV_OUTDOOR_DIST_TYPE","IOMONITOR_COUNT":"1","EAIU_Type":"EAIU4-U","EAIU_SN":"SE2C408026","Expansion_Cabinet":"No","RRH_Type":"MTP02P_410","MAU-ID":"2","No*_of_ALD":"1","MME_Info_1":"10.158.212.4|0.0.0.0|310|120","MME_Info_2":"10.156.35.44|0.0.0.0|310|120","MME_Info_3":"10.158.212.20|0.0.0.0|310|120","MME_Info_4":"10.156.34.28|0.0.0.0|310|120","MME_Info_5":"10.88.212.81|10.88.212.82|312|530","MME_Info_6":"10.88.152.33|0.0.0.0|310|120","MME_Info_7":"10.88.212.33|0.0.0.0|310|120","Comments":""}}]};
            let jsonStatue = {"SheetDetails":["Migration-Checklist"],"sessionId":"6fe0c9ce","serviceToken":"59521","status":"SUCCESS"};
            if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {

              this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });

            } else {
              this.checklistSheetList = jsonStatue.SheetDetails;
              if (this.checklistSheetList.length == 0) {
               
                this.noDataVisibility = true;
              } else {
                this.sheetDispTab = false;     
                this.noDataVisibility = false;
                this.firstSheet = this.checklistSheetList[0];              
                this.getAllChecklistDetails(this.firstSheet,0); 
                
              }
            }
          }, 1000); */

          //Please Comment while checkIn
        });
  }
  
  
   /*
   * Used to get the all EnodeB details as per checklist name selection
   * @param : event, serachBy
   * @retun : null
   */
  
   getAllChecklistDetails(sheetName,index){      
    this.sheetHighlight = index;
        let eNBList = [];
        for(let itm of this.selectedNes){
          let eNB = {eNBId:parseInt(itm.item_id) ,eNBName:itm.item_text};
          eNBList.push(eNB);  
        }
        this.sheetName=sheetName;
        this.showLoader = true;
        this.sheetDispTab = true;    
        this.checklistService.getDeatilsByChecklist(this.sharedService.createServiceToken(),this.fileName,this.sheetName, this.paginationDetails,this.ciqFileDetails.checklistFileName,this.ciqFileDetails.ciqFileName)
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
                              this.privilegeSetting = this.tableData.privilegeSetting;
                              this.totalPages = this.tableData.pageCount;
                              let pageCount = [];
                              for (var i = 1; i <= this.tableData.pageCount; i++) {
                                  pageCount.push(i);
                              }
                              this.pageRenge = pageCount;
                              this.pager = this.getPager(this.totalPages*this.pageSize, this.currentPage, this.pageRenge);

                              if(this.tableData.SheetDisplayDetails.length == 0){
                                this.tableShowHide = false;
                                this.noDataVisibility = true;
                              }else{
                                 this.tableShowHide = true;
                                 this.checklistTableData = this.tableData.SheetDisplayDetails.list;
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

                    //this.tableData = {"sessionId":"7879a553","serviceToken":"64905","status":"SUCCESS","pageCount":4,"SheetDisplayDetails":{"count":1,"list":[{"id":1,"fileName":"Complete CH52XC421 STA CDU30 2.5 TDD mMIMO for BBU No_ 1.xlsx","eNBId":null,"eNBName":null,"sheetName":"FDD_TDD","seqOrder":"1","checklistMap":{"task":"Premigration Activties\n1. eNB NBR relation\n2. CIQ Verification\n3. Prepare eNB grow","procedure":"check lates ciq from local ","notes":"Please change eNB Grow Profile for LTE IP","Start Time":"01/21/2019 12:10 AM PST","End Time":"01/21/2019 03:10 AM PST"}},{"id":2,"fileName":"Complete CH52XC421 STA CDU30 2.5 TDD mMIMO for BBU No_ 1.xlsx","eNBId":null,"eNBName":null,"sheetName":"FDD_TDD","seqOrder":"1","checklistMap":{"task":"Premigration Activties\n1. eNB NBR relation\n2. CIQ Verification\n3. Prepare eNB grow","procedure":"check lates ciq from local ","notes":"Please change eNB Grow Profile for LTE IP","Start Time":"01/21/2019 12:10 AM PST","End Time":"01/21/2019 03:10 AM PST"}},{"id":3,"fileName":"Complete CH52XC421 STA CDU30 2.5 TDD mMIMO for BBU No_ 1.xlsx","eNBId":null,"eNBName":null,"sheetName":"FDD_TDD","seqOrder":"1","checklistMap":{"task":"Premigration Activties\n1. eNB NBR relation\n2. CIQ Verification\n3. Prepare eNB grow","procedure":"check lates ciq from local ","notes":"Please change eNB Grow Profile for LTE IP","Start Time":"01/21/2019 12:10 AM PST","End Time":"01/21/2019 03:10 AM PST"}},{"id":4,"fileName":"Complete CH52XC421 STA CDU30 2.5 TDD mMIMO for BBU No_ 1.xlsx","eNBId":null,"eNBName":null,"sheetName":"FDD_TDD","seqOrder":"1","checklistMap":{"task":"Premigration Activties\n1. eNB NBR relation\n2. CIQ Verification\n3. Prepare eNB grow","procedure":"check lates ciq from local ","notes":"Please change eNB Grow Profile for LTE IP","Start Time":"01/21/2019 12:10 AM PST","End Time":"01/21/2019 03:10 AM PST"}},{"id":5,"fileName":"Complete CH52XC421 STA CDU30 2.5 TDD mMIMO for BBU No_ 1.xlsx","eNBId":null,"eNBName":null,"sheetName":"FDD_TDD","seqOrder":"1","checklistMap":{"task":"Premigration Activties\n1. eNB NBR relation\n2. CIQ Verification\n3. Prepare eNB grow","procedure":"check lates ciq from local ","notes":"Please change eNB Grow Profile for LTE IP","Start Time":"01/21/2019 12:10 AM PST","End Time":"01/21/2019 03:10 AM PST"}},{"id":6,"fileName":"Complete CH52XC421 STA CDU30 2.5 TDD mMIMO for BBU No_ 1.xlsx","eNBId":null,"eNBName":null,"sheetName":"FDD_TDD","seqOrder":"1","checklistMap":{"task":"Premigration Activties\n1. eNB NBR relation\n2. CIQ Verification\n3. Prepare eNB grow","procedure":"check lates ciq from local ","notes":"Please change eNB Grow Profile for LTE IP","Start Time":"01/21/2019 12:10 AM PST","End Time":"01/21/2019 03:10 AM PST"}},{"id":7,"fileName":"Complete CH52XC421 STA CDU30 2.5 TDD mMIMO for BBU No_ 1.xlsx","eNBId":null,"eNBName":null,"sheetName":"FDD_TDD","seqOrder":"1","checklistMap":{"task":"Premigration Activties\n1. eNB NBR relation\n2. CIQ Verification\n3. Prepare eNB grow","procedure":"check lates ciq from local ","notes":"Please change eNB Grow Profile for LTE IP","Start Time":"01/21/2019 12:10 AM PST","End Time":"01/21/2019 03:10 AM PST"}},{"id":8,"fileName":"Complete CH52XC421 STA CDU30 2.5 TDD mMIMO for BBU No_ 1.xlsx","eNBId":null,"eNBName":null,"sheetName":"FDD_TDD","seqOrder":"1","checklistMap":{"task":"Premigration Activties\n1. eNB NBR relation\n2. CIQ Verification\n3. Prepare eNB grow","procedure":"check lates ciq from local ","notes":"Please change eNB Grow Profile for LTE IP","Start Time":"01/21/2019 12:10 AM PST","End Time":"01/21/2019 03:10 AM PST"}},{"id":9,"fileName":"Complete CH52XC421 STA CDU30 2.5 TDD mMIMO for BBU No_ 1.xlsx","eNBId":null,"eNBName":null,"sheetName":"FDD_TDD","seqOrder":"1","checklistMap":{"task":"Premigration Activties\n1. eNB NBR relation\n2. CIQ Verification\n3. Prepare eNB grow","procedure":"check lates ciq from local ","notes":"Please change eNB Grow Profile for LTE IP","Start Time":"01/21/2019 12:10 AM PST","End Time":"01/21/2019 03:10 AM PST"}}]}};

                    // this.tableData = {"sessionId":"ac6883d2","serviceToken":"58047","pageCount":4,"SheetDisplayDetails":{"count":4,"list":[{"id":1,"fileName":"Verizon_vLSM_Migration Checklist_1.0.3.xlsx","sheetName":"Migration-Checklist","seqOrder":"1","checkListMap":{"INDEX":"1","TASK":"task_PreMigration Activities:    \n1. eNB NBR relation, EUTRAN NBR Relation and Site specific scripts from RF\n2. CIQ Verification\n3. Prepare eNB Grow and Cell Grow templates\n4. Prepare commissioing script\n5. Prepare env file","PROCEDURE":"procedure","Notes":"Notes","Start_Time":"startdate","End_Time":"enddate"}},{"id":2,"fileName":"Verizon_vLSM_Migration Checklist_1.0.3.xlsx","sheetName":"Migration-Checklist","seqOrder":"1","checkListMap":{"INDEX":"2","TASK":"RECEIVE CIQ AND GROW eNB    ","PROCEDURE":"check latest ciq from local winscp 13.59.191.103","Notes":"Please change the eNB Grow Profile for LTE IP address to “FALSE”.   This will prevent neighbor eNBs from communicating with this eNB.\nExample:\nInterface: ge_0_0_1.301 (Note: 301 is S1,X2 C/U plane vlan)\nSignal S1 = false\nSignal X2 = false\nBearer S1 = false\nBearer X2 = false","Start_Time":"","End_Time":""}},{"id":3,"fileName":"Verizon_vLSM_Migration Checklist_1.0.3.xlsx","sheetName":"Migration-Checklist","seqOrder":"1","checkListMap":{"INDEX":"3","TASK":"Grow cells as per CIQ","PROCEDURE":"Needs to done after the site is visible in vLSM and initialized","Notes":"where to find output power:\nCIQ :Col P: Output Power (dBm)- To be used for grow.\nCIQ: Col AU: DAS Output Power- To be used for the MOP.","Start_Time":"","End_Time":""}},{"id":4,"fileName":"Verizon_vLSM_Migration Checklist_1.0.3.xlsx","sheetName":"Migration-Checklist","seqOrder":"1","checkListMap":{"INDEX":"4","TASK":"Applicable to MRO/DAS-RFM SITE ONLY","PROCEDURE":"D:\\Verizon CDU 30\\MOP\\MRO_DAS_Settings_for_ODAS_MOP_vLSM_v1.0.pdf\n1. dl-max-tx-power\nSelect edit-config,change the dl-max-tx-power on a specific radio type dl-max-tx-power in Yang module and provide desired radio unit information.\nconnected-digital-unit-board-id=0\nconnected-digital-unit-port-id=0 (change as per the radio on which upgrade is being done)\ncascade-radio-unit-id=0\ndl-max-tx-pwr=CIQ Col. P.\n2. How to change the cell-dl-total-power value:\n Select edit config and input the desired cell-dl-total-power value to correspond desired RS power value. Hit Apply XML and will see the value changed.\n3. Fiber Delay Compensation Changes\nSelect edit-config and input the desired value for tx-rx repeater delay on port 0 based on fiber length. Hit Apply Xml and execute the command. Under response tree you will see the value changed. Important: Make sure to lock the cell when making the changes for tx-rx repeater delay values.\nNote: Thumb rule to set this delay is 1nsec/meter. Eg: for 10km delay to be set is 10,000nsec; 15km delay to be set is 75,000nsec, 20km delay to be set is 100,000nsec\n4.Antenna UL Gain - change-TTLNA-Info:\nconnected-digital-unit-board-id=0\nconnected-digital-unit-port-id=0 (change as per the radio on which upgrade is being done)\ncascade-radio-unit-id=0\npath-id= corresponds to the diversity of MRO/DAS-RFM. If 2T, then configure GAIN parameter on Path ID 1, and 2)\ngain=30","Notes":"To be applied immediately after eNB loading is completed\n1. When there is no active DAS Vendor unit on site connected to MRO/DAS RRH, then the input DL_Max_Tx_Pwr for the script will be same as the value used in Grow(CIQ:Col P)\n\nwhere to find output power in CIQ:\nCIQ :Col P: Output Power (dBm)- To be used for grow.\nCIQ: Col AT: DAS Output Power- To be used for the MOP.","Start_Time":"","End_Time":""}},{"id":5,"fileName":"Verizon_vLSM_Migration Checklist_1.0.3.xlsx","sheetName":"Migration-Checklist","seqOrder":"1","checkListMap":{"INDEX":"5","TASK":"Run the eNB neighbor script - NbrList-NBR-ENB","PROCEDURE":"Execute  script as soon as site is ENABLED","Notes":"","Start_Time":"","End_Time":""}},{"id":6,"fileName":"Verizon_vLSM_Migration Checklist_1.0.3.xlsx","sheetName":"Migration-Checklist","seqOrder":"1","checkListMap":{"INDEX":"6","TASK":"VERIFY AND UPGRADE THE FIRMWARE AND THE SOFTWARE  ","PROCEDURE":"1. Log in LSM web client as one of administrators, then go to “Configuration -> Software” menu on LSM. Select the target eNB, Click 'search', Click 'verify'.\n2. Select options as followings and click apply:\n- Package Version: 8.0.0\n- Type: Firmware\n- HW Type: ALL\n- Location: RUNNING\n3. Check if the Result in [Verification Information] is NOK (It is supposed to be NOK if it’s using previous version – SLR 8.0.1)\n4. click Compare icon of each NEs to check FW versions. After checking versions, clieck on [Update].\n5. Check the FW list again, and click on [Download],select the following options and apply:\n- Command Type: Download + Activation\n- Reboot: Checked.\n6. Check the status of downloading. When it inidcates 100%, close the window and wait for restarting system.","Notes":"SLR8.0.1_MACRO_VZW_efix01.1_PATCH_181219.tar.gz","Start_Time":"","End_Time":""}},{"id":7,"fileName":"Verizon_vLSM_Migration Checklist_1.0.3.xlsx","sheetName":"Migration-Checklist","seqOrder":"1","checkListMap":{"INDEX":"7","TASK":"Install the NB SW package","PROCEDURE":"SW packages should be upgraded by initializing system:\nGo to vLSM CONFIGURATION -> Yang Browser -> ① Select a Target eNB ② YANG=“initialize-system”③ Click on [RPC]","Notes":"1. Is the result of “initialize-system” OK? (see RESPONSE section)\n2. Compare current alarm with previous checked alarm","Start_Time":"","End_Time":""}},{"id":8,"fileName":"Verizon_vLSM_Migration Checklist_1.0.3.xlsx","sheetName":"Migration-Checklist","seqOrder":"1","checkListMap":{"INDEX":"8","TASK":"Verify the SW and FW","PROCEDURE":"1. FW VERIFICATION: LSM CONFIGURATION-> Software-> ① Verify  ② Select a Target ③ Click [Search]  ④ Click [Verify] button\n2. ① Check Verification Condition. \n- Package Version=8.0.0, Release Version=r-01, Type=Software  ② Click [Apply] button\n3. SW VERIFICATION: LSM CONFIGURATION-> Software-> ① Verify ② Select a Target ③ Click [Search] ④ Click [Verify] button again\n4. Check Verification Condition. \n- Package Version=8.0.0, Type=Firmware, Location=RUNNING ② Click [Apply] button","Notes":"Check Result status in Verification Information Tab  for FW and SW is OK","Start_Time":"","End_Time":""}},{"id":9,"fileName":"Verizon_vLSM_Migration Checklist_1.0.3.xlsx","sheetName":"Migration-Checklist","seqOrder":"1","checkListMap":{"INDEX":"9","TASK":"Run the xml Commissioing script","PROCEDURE":"Run via Yang browser","Notes":"","Start_Time":"","End_Time":""}},{"id":10,"fileName":"Verizon_vLSM_Migration Checklist_1.0.3.xlsx","sheetName":"Migration-Checklist","seqOrder":"1","checkListMap":{"INDEX":"10","TASK":"Run the xml scripts ENB_Level_Changes","PROCEDURE":"Run via Yang browser","Notes":"","Start_Time":"","End_Time":""}}]},"status":"SUCCESS"};
                    this.tableData =  {"sessionId":"4f28821d","serviceToken":"88391","SheetDisplayDetails":{"list":[{"id":1,"fileName":"CommCDU30_mMIMO_8.5.3_Checklist_V4.5.xlsx","sheetName":"mMIMO_FDD-TDD_PUT Method","seqOrder":"1","subSheetName":"Pre Checks - Post Checks","configType":"FDD+TDD Put1","checkListMap":{"Pre_Checks":"Check the PCS UL/DL Bandwidth for 1900 cells (0,9,18,1,10,19), if they are 15 MHz and SW load is 7.0.3 or 7.1.0 then stop and cancel migration otherwise proceed to next step. Any cells with 15 MHz & SW load 8.5.3 should be ok to proceed.","NOTES":"RTRC-CELL-IDLE","<Cascade>":""}},{"id":2,"fileName":"CommCDU30_mMIMO_8.5.3_Checklist_V4.5.xlsx","sheetName":"mMIMO_FDD-TDD_PUT Method","seqOrder":"1","subSheetName":"Pre Checks - Post Checks","configType":"FDD+TDD Put2","checkListMap":{"Pre_Checks":"Check if cells that needs to be MIMO are on 4T4R and see if any target cells number are shared between all pre-existing 3 sectors. If so then this cannot considered as hot swap","NOTES":"Check If Pre-Exisitng Cells # 3,12,21 are not shared among all sectors before starting De-grow\nCheck If  Pre-Exisitng Cells # 4,13,22 are not shared among all sectors before starting De-grow\nCheck If  Pre-Exisitng Cells # 5,14,23 are not shared among all sectors before starting De-grow","<Cascade>":""}},{"id":3,"fileName":"CommCDU30_mMIMO_8.5.3_Checklist_V4.5.xlsx","sheetName":"mMIMO_FDD-TDD_PUT Method","seqOrder":"1","subSheetName":"Pre Checks - Post Checks","configType":"FDD+TDD Put3","checkListMap":{"Pre_Checks":"Grab Master CIQ and Scripts from SNFTP Server","NOTES":"Scripts - /SNFTP/MIMO/Migration Scripts\nCIQ - /SNFTP/MIMO/CIQ/Master_CIQ","<Cascade>":""}}]},"status":"SUCCESS"};
                    if(this.tableData.sessionId == "408" || this.tableData.status == "Invalid User"){
                        this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                    }
                    this.totalPages = this.tableData.pageCount;
                    let pageCount = [];
                    for (var i = 1; i <= this.tableData.pageCount; i++) {
                        pageCount.push(i);
                    }
                    this.pageRenge = pageCount;
                    this.pager = this.getPager(this.totalPages * this.pageSize, this.currentPage, this.pageRenge);


                    if (this.tableData.SheetDisplayDetails.length == 0) {

                        this.tableShowHide = false;
                        this.noDataVisibility = true;
                    } else {
                        this.checklistTableData = this.tableData.SheetDisplayDetails.list;
                        this.tableShowHide = true;
                        this.noDataVisibility = false;
                        setTimeout(() => {
                            let tableWidth = document.getElementById('uploadDetails').scrollWidth;

                            $(".scrollBody table").css("min-width", (tableWidth) + "px");
                            $(".scrollHead table").css("width", tableWidth + "px");


                            $(".scrollBody").on("scroll", function (event) {
                                $(".formEditRow #formWrapper").css("margin-left", (event.target.scrollLeft - 5) + "px");
                                $(".form-control-fixed").css("right", (event.target.scrollLeft * -1) + "px");
                                $(".scrollHead table").css("margin-left", (event.target.scrollLeft * -1) + "px");
                            });
                            //$(".scrollBody").css("max-height",(this.windowScreenHieght/2) + "px");
                            //$(".scrollBody").css("max-height",(this.tableDataHeight - 10) + "px");

                        }, 0);

                    }
                  
                }, 1000); */
              
                //Please Comment while checkIn
        });
   }

   editRow(event, key, index) {
     console.log(key);
    //this.fileName=key.fileName;
    let editState : any = event.target,
        total = $(event.target).parents("tr").find("td"),
        inputData = '';
    this.editSaveStage = editState.className;
    if(editState.className == "editRow" ){
        this.tableEdit = true;        
        $("#systemMgrData").find("input").remove()
        $("#systemMgrData").find("br").remove()
        $.each(total, function(key, value) {
            if ((key !== total.length - 1)) {
                //$(value).html('<input class="form-control form-control-ciq form-control-ciqInput" id="'+$(value).find("div").attr("id")+'" value="'+$(value).find("div").text()+'"/>')
                if(($(value).find("div").attr("id") == "nodeName") || ($(value).find("div").attr("id") == "eNB_ID") || ($(value).find("div").attr("id") == "eNodeB_Name")){
                    inputData = '<input class="form-control form-control-ciq form-control-ciqInput" id="'+$(value).find("div").attr("id")+'" value="'+$(value).find("div").text()+'" disabled="disabled"/>';
                }else{
                    if($(value).find("div").text() == ""){
                    inputData = '<br><input class="form-control form-control-ciq form-control-ciqInput" id="'+$(value).find("div").attr("id")+'" value="'+$(value).find("div").text()+'"/>';  
                    }else{
                      inputData = '<input class="form-control form-control-ciq form-control-ciqInput" id="'+$(value).find("div").attr("id")+'" value="'+$(value).find("div").text()+'"/>';
                    }
                }
                $(value).append(inputData);
            }
        })
        $(".saveRow").attr("class","editRow");
        if( editState.className != "editRowDisabled"){ //enable click only if it is enabled
          editState.className = "saveRow";
          
          
            editState.parentNode.querySelector(".deleteRow").className = "cancelRow";
          
          $(".cloneRow").attr("class","cloneRowDisabled");
          // To enable one edit form at a time in table
          if(this.editableFormArray.length >= 1){
              this.editableFormArray = [];
              this.editableFormArray.push(index);
          } else {
             this.editableFormArray.push(index);
          }
          console.log(this.editableFormArray[0]);
        }
    }else if(editState.className != "editRowDisabled" ){      
        this.showLoader = true;
        let checkListMap = {},
            checklistDetails = {};
        $.each(total, function(key, value) {
               // if($(value).find("input").attr("id") != 'nodeName'){
                  if($(value).find("input").attr("id") != undefined){
                    checkListMap[$(value).find("input").attr("id")] = $(value).find("input").val();
                  }
               // } 
         })
        
        /* if(this.searchByChecklistBlock == true) */{
         checklistDetails = {"checkListMap":checkListMap,"fileName":key.fileName,"id":key.id,"sheetName":key.sheetName,"seqOrder":key.seqOrder,"checklistAuditDetail":this.checklistDetails};
        }
/*         if(this.searchByNesBlock == true){
         checklistDetails = {"id":key.id, "checkListMap":checkListMap, "fileName":this.fileName, "eNBId":key.eNBId, "eNBName":key.eNBName,"menuName":this.menuName};
        } */
        this.checklistService.saveCheckListFileDetaiils(this.searchByChecklistBlock, checklistDetails, this.sharedService.createServiceToken(),this.ciqFileDetails.checklistFileName,this.ciqFileDetails.ciqFileName)
        .subscribe(
            data => {
                let jsonStatue = data.json();
                this.showLoader = false;

                if(jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User"){
            
                  this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
            
                } else {
            
                  if( this.sharedService.isvalidServiceToken( parseInt(data.json().serviceToken,10) ) || data.json().sessionId == "408"){               
                      if(jsonStatue.status == "SUCCESS"){
                         this.editableFormArray = [];
                         this.addtableFormArray = [];
                         this.message = "Checklist details updated successfully !";
                         this.successModalBlock = this.modalService.open(this.successModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'success-modal'});
                       // this.createForm = false;
                       this.tableEdit = false;
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
                     this.message = "Checklist details updated successfully !";
                     this.successModalBlock = this.modalService.open(this.successModalRef,{keyboard: false, backdrop: "static",size:'lg',windowClass:"success-modal"});
                     this.tableEdit = false;
                    } else {
                      this.displayModel(jsonStatue.reason,"failureIcon");  
                    }
              
                  }, 1000); */
              //Please Comment while checkIn
            });
        
    }
    
  }

   /*
   * Used to delete the Checklist details
   * @param : confirmModal, id, event
   * @retun : null
   */
   
  deleteRow(confirmModal, id, event) {
      if(event.target.className == "deleteRow"){
          this.modalService.open(confirmModal,{keyboard: false, backdrop: 'static', size:'lg', windowClass:'confirm-modal'}).result.then((result) => {
          this.closeResult = `Closed with: ${result}`;
        }, (reason) => {
          this.showLoader = true;

          this.checklistService.deleteChecklistDetails(this.fileName, id, this.sharedService.createServiceToken(),this.ciqFileDetails.checklistFileName,this.ciqFileDetails.ciqFileName)
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
                                 this.editableFormArray = [];
                                 this.addtableFormArray = [];
                                 this.message = "Checklist deleted successfully!";
                                 this.successModalBlock = this.modalService.open(this.successModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'success-modal'});
                                //this.displayModel("User details deleted successfully!","successIcon");
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
                         let jsonStatue = {"reason": null,"sessionId":"a4c77a9c","serviceToken":"92619","status":"SUCCESS"};
                        if(jsonStatue.status == "SUCCESS"){
                          this.message = "Checklist row deleted successfully!";
                          this.successModalBlock = this.modalService.open(this.successModalRef,{keyboard: false, backdrop: "static",size:'lg',windowClass:"success-modal"});
                        } else {
                         
                            this.displayModel(jsonStatue.reason,"failureIcon");  
                        } 
                      }, 1000); */
                    //Please Comment while checkIn
                     
                        //this.alertService.error(error);TODO : This need to implement
                });
        });  
      }else{
          $("#systemMgrData").find("input").remove();
          $("#systemMgrData").find("br").remove();
          $(".editRowDisabled").attr("class","editRow");
          $(".cloneRowDisabled").attr("class","cloneRow");
          $(".saveRow").attr("class","cloneRow");
          this.editableFormArray = [];
          this.addtableFormArray = [];
          if(this.searchByChecklistBlock == true){
            event.target.className = "deleteRow";  
          }
          if(this.searchByChecklistBlock == false){
            event.target.className = "deleteRowDisabled";  
          }
          event.target.previousSibling.className = "editRow";
          this.tableEdit = false;
      }
      
  }

    showScriptSeqModel(event, key, index, content) {
        this.checkListStepIndex = key.id;
        this.checkListConfigType = key.configType;
        this.checklistService.getChecklistScriptDetails(this.sharedService.createServiceToken(), this.checkListStepIndex, this.ciqFileDetails.checklistFileName, this.ciqFileDetails.ciqFileName, this.sheetName)
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
                                let scriptDetails = jsonStatue.scriptList;
                                
                                this.scriptDetails = [];
                                for (let scriptRow of scriptDetails) {
                                    let scriptDetailsData = {
                                        "id": scriptRow.id,
                                        "scriptName": scriptRow.scriptName,
                                        "scriptExeSeq": scriptRow.scriptExeSeq,
                                        "scriptName_old": "",
                                        "scriptExeSeq_old": "",
                                        "inEditMode": false,
                                        "rowUpdate": false
                                    }
                                    this.scriptDetails.push(scriptDetailsData);
                                }
                                this.scriptDetailsModalBlock = this.modalService.open(content, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView' });
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

                        let jsonStatue = JSON.parse('{"sessionId":"7e088256","serviceToken":"81749","status":"SUCCESS","scriptList":[{"id":2,"scriptName":"Script1","scriptExeSeq":4},{"id":1,"scriptName":"Script2","scriptExeSeq":7}],"reason":""}');

                        if (jsonStatue.status == "SUCCESS") {
                            this.showLoader = false;
                            let scriptDetails = jsonStatue.scriptList;
                            
                            this.scriptDetails = [];
                            for (let scriptRow of scriptDetails) {
                                let scriptDetailsData = {
                                    "id": scriptRow.id,
                                    "scriptName": scriptRow.scriptName,
                                    "scriptExeSeq": scriptRow.scriptExeSeq,
                                    "scriptName_old": "",
                                    "scriptExeSeq_old":"",
                                    "inEditMode": false,
                                    "rowUpdate": false
                                }
                                this.scriptDetails.push(scriptDetailsData);
                            }
                            this.scriptDetailsModalBlock = this.modalService.open(content, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView' });
                        } else {
                            this.showLoader = false;
                            this.displayModel(jsonStatue.reason, "failureIcon");
                        }

                    }, 100); */

                    //Please Comment while checkIn   
                });
    }

    saveScriptRow(event, index) {
        let validations = { "rules": {}, "messages": {} };
        validations.rules["scriptName_" + [index]] = { "required": true, "customfunction": false };
        validations.rules["scriptExeSeq_" + [index]] = { "required": true, "customfunction": false, "pattern":/^([0-9]*[1-9][0-9]*)$/ };
        validations.messages["scriptName_" + [index]] = { "required": "Value is required", "customfunction": "Value should be unique" };
        validations.messages["scriptExeSeq_" + [index]] = { "required": "Value is required", "customfunction": "Value should be unique", "pattern":"Please enter a valid value" };

        let rows = $(event.target).parents("#scriptDetailTable").find("tr"), exeVal = false;
        for (var i = 0; i < rows.length; i++) {
            if ($(rows[i]).find("td:eq(0)").text() == $("#scriptName_" + index).val()) {
                //$("#updatedExeSeq_" + index).next(".error-message-block").html("Value should be unique");
                validations.rules["scriptName_" + [index]].customfunction = true;
                exeVal = true;
            }
            if ($(rows[i]).find("td:eq(1)").text() == $("#scriptExeSeq_" + index).val()) {
                //$("#updatedExeSeq_" + index).next(".error-message-block").html("Value should be unique");
                validations.rules["scriptExeSeq_" + [index]].customfunction = true;
                exeVal = exeVal && true;
            }
        }

        validator.performValidation(event, validations, "save_update");
        setTimeout(() => {
            if (this.isValidForm(event) && !exeVal) {
                this.scriptDetails[index].rowUpdate = true;
                this.editMode = -1;

                if(this.scriptDetails[index].id == "") {
                    this.scriptDetails[index].id = null;    //Change the value from empty to null
                }
                this.scriptDetails[index].scriptName = $("#scriptName_" + index).val();
                this.scriptDetails[index].scriptExeSeq = Number($("#scriptExeSeq_" + index).val());
                this.scriptDetails[index].rowUpdate = true;
            }
        }, 0)

        // this.scriptDetailsModalBlock.close();
    }

    editScriptRow(event, index) {
        for(let i = 0; i < this.scriptDetails.length; i++) {
            this.cancelScriptRow(null, i);
        }
        this.editMode = index;
    }

    addScriptRow() {
        let scriptDetailsData = {
            "id": "",
            "scriptName": "",
            "scriptExeSeq": "",
            "scriptName_old": "",
            "scriptExeSeq_old":"",
            "inEditMode": true,
            "rowUpdate": false
        }
        // this.scriptDetails.push(scriptDetailsData);
        this.scriptDetails.unshift(scriptDetailsData);
        this.editMode = 0;//this.scriptDetails.length - 1;  //Enable edit in row (newly added row)
    }

    deleteScriptRow(event, index) {
        this.scriptDetails.splice(index, 1);
    }

    cancelScriptRow(event, index) {
        this.editMode = -1;
        if(this.scriptDetails[index].id == "") {
            this.scriptDetails.splice(index, 1);
        }
    }

    saveScriptDetails() {
        let configType = this.checkListConfigType;
        this.checklistService.saveChecklistScriptDetails(this.sharedService.createServiceToken(), this.checkListStepIndex, this.ciqFileDetails.checklistFileName, this.ciqFileDetails.ciqFileName, this.scriptDetails, this.sheetName, configType)
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
                                this.editMode = -1;
                                this.message = "Script Sequence Details Saved Successfully!";
                                this.displayModel(this.message, "successIcon");
                                this.checkListStepIndex = "";
                                this.checkListConfigType = "";
                                this.scriptDetailsModalBlock.close();
                            } else {
                                this.showLoader = false;
                                this.editMode = -1;
                                this.displayModel(jsonStatue.reason, "failureIcon");

                            }
                        }
                    }
                },
                error => {
                    //Please Comment while checkIn

                    /* setTimeout(() => {

                        this.showLoader = false;

                        let jsonStatue = JSON.parse('{"sessionId":"7e088256","serviceToken":"81749","status":"SUCCESS","reason":""}');

                        if (jsonStatue.status == "SUCCESS") {
                            this.showLoader = false;
                            this.editMode = -1;
                            this.message = "Script Sequence Details Saved Successfully!";
                            this.displayModel(this.message, "successIcon");
                            this.checkListStepIndex = "";
                            this.checkListConfigType = "";
                            this.scriptDetailsModalBlock.close();
                        } else {
                            this.showLoader = false;
                            this.editMode = -1;
                            this.displayModel(jsonStatue.reason, "failureIcon");
                        }

                    }, 100); */

                    //Please Comment while checkIn   
                });
    }

    cancelScriptDetails() {
        for (var i = 0; i < this.scriptDetails.length; i++) {
            if (this.scriptDetails[i].rowUpdate == true) {
                this.scriptDetails[i].scriptName = this.scriptDetails[i].scriptName_old;
                this.scriptDetails[i].scriptExeSeq = this.scriptDetails[i].scriptExeSeq_old;
                this.scriptDetails[i].rowUpdate = false;
            }
        }

        this.editMode = -1;
        this.checkListStepIndex = "";
        this.checkListConfigType = "";
        this.scriptDetailsModalBlock.close();
    }

  /*
   * Used to add the Checklist list for perticular Checklist 
   * @param : event, key, index
   * @retun : null
   */
   
  addRow(event, key, index) {
    let editState : any = event.target,
        total = $(event.target).parents("tr").find("td"),
        inputData = '';
    if(editState.className == "cloneRow" ){
        this.tableEdit = true;
        $("#systemMgrData").find("input").remove()
        $("#systemMgrData").find("br").remove()
        $.each(total, function(key, value) {
            if ((key !== total.length - 1)) {
                //$(value).html('<input class="form-control form-control-ciq form-control-ciqInput" id="'+$(value).find("div").attr("id")+'" value="'+$(value).find("div").text()+'"/>')
               /*  if(($(value).find("div").attr("id") == "nodeName") || ($(value).find("div").attr("id") == "eNB_ID") || ($(value).find("div").attr("id") == "eNodeB_Name")){
                    inputData = '<input class="form-control form-control-ciq form-control-ciqInput" id="'+$(value).find("div").attr("id")+'" value="'+$(value).find("div").text()+'" disabled="disabled"/>';
                }else{ */
                  //console.log()
                    if($(value).find("div").text() == ""){
                    inputData = '<br><input class="form-control form-control-ciq form-control-ciqInput" id="'+$(value).find("div").attr("id")+'" value="'+$(value).find("div").text()+'"/>';  
                    }else{
                      inputData = '<input class="form-control form-control-ciq form-control-ciqInput" id="'+$(value).find("div").attr("id")+'" value="'+$(value).find("div").text()+'"/>';
                    }
                //}
                
                $(value).append(inputData);
            }
        })
        if( editState.className != "cloneRowDisabled"){ //enable click only if it is enabled
          editState.className = "saveRow";
          editState.parentNode.querySelector(".editRow").className = "editRowDisabled";
          editState.parentNode.querySelector(".deleteRow").className = "cancelRow";
          $(".cloneRow").attr("class","cloneRowDisabled");
          $(".editRow").attr("class","editRowDisabled");
          // To enable one edit form at a time in table
          if(this.addtableFormArray.length >= 1){
              this.addtableFormArray = [];
              this.addtableFormArray.push(index);
          } else {
             this.addtableFormArray.push(index);
          }
        }
    }else if(editState.className != "cloneRowDisabled" ){        
        this.showLoader = true;
        let checkListMap = {},
            checklistDetails = {};
        $.each(total, function(key, value) {
               // if($(value).find("input").attr("id") != 'nodeName'){
                  if($(value).find("input").attr("id") != undefined){
                    checkListMap[$(value).find("input").attr("id")] = $(value).find("input").val();
                  }
               // } 
         })

            /* if(this.searchByChecklistBlock == true) */{
            // checklistDetails = {"id":null, "checkListMap":checkListMap, "fileName":this.fileName};
            checklistDetails = {"id":null, "checkListMap":checkListMap,"fileName":key.fileName,"sheetName":key.sheetName,"seqOrder":key.seqOrder,"checklistAuditDetail":this.checklistDetails};
          }
/*             if(this.searchByNesBlock == true){
             checklistDetails = {"id":null, "checkListMap":checkListMap, "fileName":this.fileName, "eNBId":key.eNBId, "eNBName":key.eNBName,"menuName":this.menuName};
            } */

        
        this.checklistService.saveCheckListFileDetaiils(this.searchByChecklistBlock, checklistDetails, this.sharedService.createServiceToken(),this.ciqFileDetails.checklistFileName,this.ciqFileDetails.ciqFileName)
        .subscribe(
            data => {
                let jsonStatue = data.json();
                this.showLoader = false;

                if(jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User"){
            
                  this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
            
                } else {
            
                  if( this.sharedService.isvalidServiceToken( parseInt(data.json().serviceToken,10) ) || data.json().sessionId == "408"){               
                      if(jsonStatue.status == "SUCCESS"){
                         this.editableFormArray = [];
                         this.addtableFormArray = [];
                         this.message = "Checklist details created successfully !";
                         this.successModalBlock = this.modalService.open(this.successModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'success-modal'});
                       // this.createForm = false;
                       this.tableEdit = false;
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
                     this.message = "Checklist details created successfully !";
                     this.successModalBlock = this.modalService.open(this.successModalRef,{keyboard: false, backdrop: "static",size:'lg',windowClass:"success-modal"});
                     this.tableEdit = false;
                    } else {
                      this.displayModel(jsonStatue.reason,"failureIcon");  
                    }
              
                  }, 1000); */
              //Please Comment while checkIn
            });
        
    }
    
  }
  /*
   * Used to Generate CSV File
   * @param : event, key, index
   * @retun : null
   */
 /*
   * Used to close the success model 
   * @param : event, key, index
   * @retun : null
   */
  
    closeModel() {
        this.successModalBlock.close();
        //this.ngOnInit();
        this.getAllChecklistDetails(this.sheetName, 0);
    }

  /* validates current submitted form is valid and free from errors
   * @param : pass the event
   * @retun : boolean
   */

  isValidForm(event) {
      return ($(event.target).parents("form").find(".error-border").length == 0) ? true : false;
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
                 
           this.getAllChecklistDetails(this.sheetName,0);  
          
         


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
          this.getAllChecklistDetails(this.sheetName,0);  
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

  onChangeDate(){
    this.errMessage = false;
    if(this.fromDate && this.toDate){
      //this.getCheckListData(this.fromDate,this.toDate);
      this.showLoader=true;
      this.ciqFileDetails= [];
      this.getCheckListData(this.fromDate,this.toDate);
      //this.ngOnInit();
    }else{
      this.errMessage = true;
    }
  }

  changeSorting(predicate, event, index) {
    this.sharedService.dynamicSort(predicate, event, index, this.scriptDetails);
}

}