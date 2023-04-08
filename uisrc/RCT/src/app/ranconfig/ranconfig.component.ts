import { ViewContainerRef, Input, ElementRef, Renderer, Component, OnInit, ViewChild, HostListener } from '@angular/core';
import { Router, NavigationEnd} from '@angular/router';
import { NgbModal, ModalDismissReasons} from '@ng-bootstrap/ng-bootstrap';
import { RanconfigService } from '../services/ranconfig.service';
import { SharedService } from '../services/shared.service';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { filter, map, mergeMap} from 'rxjs/operators';
import { validator } from '../validation';
import {platformBrowserDynamic} from '@angular/platform-browser-dynamic';
import { DatePipe } from '@angular/common';
import * as FileSaver from 'file-saver';
import * as $ from 'jquery';
import * as _ from 'underscore';
@Component({
  selector: 'rct-ranconfig',
  templateUrl: './ranconfig.component.html',
  styleUrls: ['./ranconfig.component.scss'],
  providers: [RanconfigService]
})
  export class RanconfigComponent implements OnInit {
  showLoader:boolean = true;
  showNoDataFound: boolean;
  searchStatus: string;
  objectKeys = Object.keys;
  serachBy:any;
  searchCriteria: any;
  tableData:any;
  eNodeMapData:any;
  tableShowHide :boolean = false;
  eNodeBTableShowHide :boolean = false;
  treeShowHide :boolean = false;
  closeResult:string;
  privilegeSetting : object;
  noDataVisibility :boolean = false;
  noDataVisibilityENodeB :boolean = false;
  showModelMessage: boolean = false;
  messageType: any;
  modelData :any;
  sessionExpiredModalBlock : any; // Helps to close/open the model window
  successModalBlock : any;
  message : any;
  aircraftOffloadBlock:any;
  timezone: any;
  searchByCiqBlock: boolean = false;
  searchByNesBlock: boolean = false;
  getCiqList:any;
  getNeslist:any;
  tableDataHeight:any;
  ciqListData:any;
  nesListData:any;
  editMode:boolean = false;
  eNBList:any;
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
  viewModalBlock:any;
  ciqSheetList= [];
  ciqTableData:any;
  ciqFileDetails:any;
  ciqNEFileDetails:any;
  nes:any;
  selSheet:any;
  selSubSheet:any;  
  sheetHighlight:any;  
  ciqSheetHighlight:any;
  sheetDispTab:boolean =false;
  errMessage:boolean= false;
  navigationSubscription: any;
  sheetDetails:any;
  subSheetList:any =[];
  validateDetails:any;
  showForm:boolean =false;
  ciqNameConfig:object;
  searchFormField : any = {};
  searchExpand: boolean = false;
  programChangeSubscription:any;
  networkType: any = "";
  programName: any = "";
  supportCA: boolean;
  migrationStrategy: string = "Legacy IP";
  validationData: any = {
        "rules": {
            "ciqName": {
                "required": true
            },
            "nes": {
                "required": true
            }

        },
        "messages": {
            "ciqName": {
                "required": "Please Select Ciq Name"
            },
            "nes": {
                "required": "Please Select eNodeB Name"
            }
        }
    };
  @ViewChild('confirmModal') confirmModalRef: ElementRef;
  @ViewChild('sessionExpiredModal') sessionExpiredModalRef: ElementRef;
  @ViewChild('successModal') successModalRef: ElementRef;
  @ViewChild('viewModal') viewModalRef: ElementRef;
  @ViewChild('filePost') filePostRef: ElementRef;
  @ViewChild('searchTab') searchTabRef: ElementRef;
  @ViewChild('createNewTab') createNewTabRef: ElementRef;
  @ViewChild('runTestForm') searchForm;
  
  
  constructor(
    private element: ElementRef,
    private renderer: Renderer,
    private router:Router,
    private modalService: NgbModal,
    private ranconfigService: RanconfigService,
    private sharedService: SharedService,
    private datePipe: DatePipe
    ) {
     /*  this.navigationSubscription = this.router.events.subscribe((e: any) => {
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
        // Clear Session storage for selectedCIQ on new program selection
        this.sharedService.updateSelectedCIQInSessionStorage(null);
        this.loadInitialData();
      }
    });
  }
  loadInitialData() {
    this.selectedLsmVersion = "";
    this.selectedNetworkType = "";
    this.selectedNetworkTypeId = "";
    this.searchStatus = 'load';
    this.searchCriteria = null;
    this.searchFormField = {};
    this.serachBy = "";
    this.fileName = "";
    this.fileId = "";
    this.enbId = "";
    this.enbName = "";
    this.showLoader = true;
    this.searchByCiqBlock = true;
    this.searchByNesBlock = false;
    this.getCiqList = [];
    this.getNeslist = [];
    this.subSheetList = [];
    this.ciqFileDetails = [];

    this.currentPage = 1;
    this.totalPages = 1;
    this.TableRowLength = 10;
    this.pageSize = 10;

    let paginationDetails = {
      "count": this.TableRowLength,
      "page": this.currentPage
    };
    this.paginationDetails = paginationDetails;
    this.noDataVisibility = true;
    this.setMenuHighlight("ciq");
    this.sheetDispTab = false;
    this.tableShowHide = false;
    this.treeShowHide = false;
    setTimeout(() => this.supportCA = false, 100);
    this.ciqNameConfig = {
      displayKey: "ciqFileName",
      search: true,
      height: '200px',
      placeholder: '--Select--',
      customComparator: () => { },
      //   limitTo: this.getCiqList.length, 
      moreText: 'more',
      noResultsFound: 'No results found!',
      searchPlaceholder: 'Search',
      searchOnKey: 'ciqFileName',
    }
    this.getCiqListData(this.fromDate, this.toDate);
    this.migrationStrategy = "Legacy IP";
  }
  
  getCiqListData(fromDate,toDate){
    this.showLoader = true;
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
    this.ranconfigService.getCiqListData(this.sharedService.createServiceToken(),fromDt,toDt,this.searchStatus)
    .subscribe(
        data => {
            setTimeout(() => { 
              let jsonStatue = data.json();
              this.ciqListData = data.json();
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
                        this.fromDate = new Date( this.ciqListData.fromDate);
                        this.toDate = new Date(this.ciqListData.toDate);
                        
                        this.networkType = JSON.parse(sessionStorage.getItem("selectedProgram")).networkTypeDetailsEntity.networkType; //this.ciqListData.getCiqList[0].programDetailsEntity.networkTypeDetailsEntity.networkType;
                        this.programName = JSON.parse(sessionStorage.getItem("selectedProgram")).programName; 
                        this.getCiqList = this.ciqListData.getCiqList;
                        
                        let getSelectedCIQ = JSON.parse(sessionStorage.getItem("selectedCIQ"));
                        if (this.getCiqList.length > 0 && getSelectedCIQ) {
                          this.ciqFileDetails = getSelectedCIQ;
                        }
                        else {
                          this.ciqFileDetails = this.getCiqList.length > 0 ? this.getCiqList[0] : null;
                          // Update Session storage for selectedCIQ
                          this.sharedService.updateSelectedCIQInSessionStorage(this.ciqFileDetails);
                        }

                        // this.ciqFileDetails = this.getCiqList[0];
                        if(this.searchByNesBlock){
                          this.ciqNEFileDetails = this.ciqFileDetails ;//this.getCiqList[0];
                          this.getNEdata(false, true);
                        }
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
            //this.ciqListData = {"sessionId":"b98fb19a","serviceToken":"52298","getCiqList":[{"id":27,"fileName":"FDMIMO_FIT_CIQ_08242018_SprintTemplate2.xlsx","uploadBy":"admin","customerId":2,"remarks":"Kamlesh is testing","creationDate":"2018-12-24T10:17:16.000+0000","lsmVersion":"3","networkType":"4G"},{"id":28,"fileName":"FDMIMO_FIT_CIQ_08242018_SprintTemplate_extra1.xlsx","uploadBy":"superadmin","customerId":2,"remarks":"Superadmin","creationDate":"2018-12-24T11:39:07.000+0000","lsmVersion":"LSM25","networkType":"4G"}],"status":"SUCCESS"};
            this.ciqListData ={"fromDate":"05/29/2020","toDate":"06/05/2020","sessionId":"2b53bed9","serviceToken":"70444","getCiqList":[{"id":119,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"5G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2019-11-22T06:36:02.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"ciqFileName":"UNY-NE-VZ_CIQ_Ver3.6.84_06022020.xlsx","ciqFilePath":"Customer/34/PreMigration/Input/UNY-NE-VZ_CIQ_Ver3.6.84_06022020/CIQ/","scriptFileName":"70243.zip,,70215.zip,,70282.zip","scriptFilePath":"Customer/34/PreMigration/Input/UNY-NE-VZ_CIQ_Ver3.6.84_06022020/SCRIPT/","checklistFileName":"Verizon_USM_Migration Checklist_1.1.5(1)(5).xlsx","checklistFilePath":"Customer/34/PreMigration/Input/UNY-NE-VZ_CIQ_Ver3.6.84_06022020/CHECKLIST/","ciqVersion":"ORIGINAL","fileSourceType":"UPLOAD","uploadBy":"superadmin","remarks":"","creationDate":"2020-06-04T15:04:35.000+0000"},{"id":118,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2019-11-22T06:36:02.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"ciqFileName":"UNY-NE-VZ_CIQ_Ver3.6.82_06012020.xlsx","ciqFilePath":"Customer/34/PreMigration/Input/UNY-NE-VZ_CIQ_Ver3.6.82_06012020/CIQ/","scriptFileName":"70215.zip,,70243.zip","scriptFilePath":"Customer/34/PreMigration/Input/UNY-NE-VZ_CIQ_Ver3.6.82_06012020/SCRIPT/","checklistFileName":"Verizon_USM_Migration Checklist_1.1.5(1)(5).xlsx","checklistFilePath":"Customer/34/PreMigration/Input/UNY-NE-VZ_CIQ_Ver3.6.82_06012020/CHECKLIST/","ciqVersion":"ORIGINAL","fileSourceType":"UPLOAD","uploadBy":"superadmin","remarks":"Shahid","creationDate":"2020-06-03T11:24:24.000+0000"}],"status":"SUCCESS"};
              if(this.ciqListData.sessionId == "408" || this.ciqListData.status == "Invalid User"){
                 this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                }
              if(this.ciqListData.status == "SUCCESS"){
                this.fromDate = new Date( this.ciqListData.fromDate);
                this.toDate = new Date(this.ciqListData.toDate);
                this.networkType = JSON.parse(sessionStorage.getItem("selectedProgram")).networkTypeDetailsEntity.networkType; //this.ciqListData.getCiqList[0].programDetailsEntity.networkTypeDetailsEntity.networkType;
                this.programName = JSON.parse(sessionStorage.getItem("selectedProgram")).programName; 

                this.getCiqList = this.ciqListData.getCiqList;

                let getSelectedCIQ = JSON.parse(sessionStorage.getItem("selectedCIQ"));
                if (this.getCiqList.length > 0 && getSelectedCIQ) {
                  this.ciqFileDetails = getSelectedCIQ;
                }
                else {
                  this.ciqFileDetails = this.getCiqList.length > 0 ? this.getCiqList[0] : null;
                  // Update Session storage for selectedCIQ
                  this.sharedService.updateSelectedCIQInSessionStorage(this.ciqNEFileDetails);
                }

                // this.ciqFileDetails = this.getCiqList[0];
                if(this.searchByNesBlock){
                this.ciqNEFileDetails = this.ciqFileDetails;//this.getCiqList[0];
                this.getNEdata();
                }
              }else{
                this.showLoader = false;
                this.getCiqList = [];
              } 
              
          }, 1000); */
          //Please Comment while checkIn
        });
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
  /*
   * Used to dispaly the CIQ Details Tab
   * @param : event
   * @retun : null
   */
  searchByCiq(){
      this.selectedLsmVersion = "";
      this.selectedNetworkType = "";
      this.selectedNetworkTypeId = "";
      this.treeShowHide = false;
      this.tableShowHide = false;
      this.eNodeBTableShowHide = false;
      this.noDataVisibility = true;
      this.searchByCiqBlock = true;
      this.searchByNesBlock = false;
      this.setMenuHighlight("ciq");
      /* this.ciqFileDetails="";
      this.ciqNEFileDetails = ""; */
      this.sheetDispTab = false;
      this.subSheetList = [];
      this.searchStatus = "load";
      this.searchCriteria = null;
      this.supportCA = false;
      this.searchFormField = {};

  }

  searchRanConfig(event) {
    this.searchStatus = "search";
    this.searchCriteria = this.searchFormField;
    this.currentPage = 1;
    let paginationDetails = {
        "count": parseInt(this.pageSize),
        "page": parseInt(this.currentPage)
    };
    this.paginationDetails = paginationDetails; 
    this.getAllCiqDetails();
  }

  clearSearchFrom() {
    this.searchForm.nativeElement.reset();  
  }

  /*
   * Used to dispaly the eNodeB Details Tab
   * @param : event
   * @retun : null
   */
  searchByNes(){
      this.selectedLsmVersion = "";
      this.selectedNetworkType = "";
      this.selectedNetworkTypeId = "";
      this.nes =null;
      this.noDataVisibility = false;
      this.treeShowHide = false;
      this.tableShowHide = false;
      this.eNodeBTableShowHide = false;
      this.noDataVisibility = true;
      this.searchByCiqBlock = false;
      this.searchByNesBlock = true;
      this.setMenuHighlight("nes");
      this.sheetDispTab = false; 
      this.subSheetList = [];
      this.ciqFileDetails="";
      this.ciqNEFileDetails = [];  
      this.getCiqListData(this.fromDate,this.toDate);  
      this.showForm = false;
      this.supportCA = false;
  } 

  changeSubSheet() {
    this.searchStatus = 'load';
    this.searchCriteria = null;
    this.searchFormField = {};
    this.getAllCiqDetails()
  }

  /*
   * Used to get the eNodeB list while select ciq name
   * @param : fileName, fileId
   * @retun : null
   */
  getNEdata(updateSessionStorage = false, updateFromNE = false){
    this.eNodeBTableShowHide = false;
    
    if(updateSessionStorage) {
      // Update the sessionStorage selected CIQ if CIQ list is getting changed from UI dropdown
      updateFromNE ? this.sharedService.updateSelectedCIQInSessionStorage(this.ciqNEFileDetails) : this.sharedService.updateSelectedCIQInSessionStorage(this.ciqFileDetails);
    }
    if(updateFromNE && this.ciqNEFileDetails) {
    this.showLoader = true;
    this.getNeslist = [];

    this.ranconfigService.getNeListData(this.ciqNEFileDetails.ciqFileName, this.sharedService.createServiceToken() )
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
                            this.eNodeBTableShowHide = true;
                            this.getNeslist = this.nesListData.eNBList;
                            /* let getNeslistStore = this.nesListData.eNBList;
                            for (let itm of getNeslistStore) {
                              let dropdownList = { item_id: itm.eNBId, item_text: itm.eNBName };
                              this.getNeslist.push(dropdownList);
                          } */
                          this.dropdownSettings = {
                              singleSelection: true,
                              idField: 'item_id',
                              textField: 'item_text',
                              itemsShowLimit: 1,
                              allowSearchFilter: true,
                              searchPlaceholderText:"Search",
                              clearSearchFilter:true,
                              maxHeight: 300
                          };

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
              /* setTimeout(() => { 
                this.showLoader = false;
                this.nesListData = JSON.parse('{"eNBList":[{"eNBName":"061192_NORTHWOOD_LAKE_NH","eNBId":"61192"},{"eNBName":"061452_CONCORD_2_NH_HUB","eNBId":"61452"},{"eNBName":"073461_PRATTSBURGH","eNBId":"73461"},{"eNBName":"073462_East_Corning","eNBId":"73462"},{"eNBName":"073466_Howard","eNBId":"73466"},{"eNBName":"073474_Hornellsville","eNBId":"73474"},{"eNBName":"073484_ADDISON","eNBId":"73484"},{"eNBName":"072409_Press_Building","eNBId":"72409"},{"eNBName":"072412_Binghamton_DT","eNBId":"72412"},{"eNBName":"072413_SUNY_Binghamton","eNBId":"72413"},{"eNBName":"072415_Vestal","eNBId":"72415"},{"eNBName":"072416_Chenango","eNBId":"72416"},{"eNBName":"072417_Kirkwood","eNBId":"72417"},{"eNBName":"072419_Windsor","eNBId":"72419"},{"eNBName":"072424_CASTLE_CREEK","eNBId":"72424"},{"eNBName":"072425_Killawog","eNBId":"72425"},{"eNBName":"072426_East_Richford","eNBId":"72426"},{"eNBName":"072427_Caroline","eNBId":"72427"},{"eNBName":"072430_Owego_North","eNBId":"72430"},{"eNBName":"072431_Owego","eNBId":"72431"},{"eNBName":"072432_Apalachin","eNBId":"72432"},{"eNBName":"072433_Nichols","eNBId":"72433"},{"eNBName":"072442_CROCKER_CREEK","eNBId":"72442"},{"eNBName":"072443_MAINE_DT","eNBId":"72443"},{"eNBName":"072451_BELDEN","eNBId":"72451"},{"eNBName":"072452_TIOGA_CENTER","eNBId":"72452"},{"eNBName":"072454_CATATONK","eNBId":"72454"},{"eNBName":"072458_CHENANGO_DT","eNBId":"72458"},{"eNBName":"072478_Big_Flats","eNBId":"72478"},{"eNBName":"070033_POWERS_RD","eNBId":"70033"},{"eNBName":"070005_RTE_263_GETZVILLE","eNBId":"70005"},{"eNBName":"070562_BOWEN_RD","eNBId":"70562"},{"eNBName":"073313_FREY_RD","eNBId":"73313"},{"eNBName":"073326_BAKER_RD","eNBId":"73326"}],"sessionId":"2a7a3636","serviceToken":"79044","status":"SUCCESS"}');
                  if(this.nesListData.sessionId == "408" || this.nesListData.status == "Invalid User"){
                     this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                    }
                  if(this.nesListData.status == "SUCCESS"){
                    this.eNodeBTableShowHide = true;
                    this.getNeslist = this.nesListData.eNBList;
                  //   let getNeslistStore = this.nesListData.eNBList;
                  //   for (let itm of getNeslistStore) {
                  //     let dropdownList = { item_id: itm.eNBId, item_text: itm.eNBName };
                  //     this.getNeslist.push(dropdownList);
                  // }
                  this.dropdownSettings = {
                      singleSelection: true,
                      idField: 'item_id',
                      textField: 'item_text',
                      itemsShowLimit: 1,
                      allowSearchFilter: true,
                      searchPlaceholderText:"Search",
                      clearSearchFilter:true,
                      maxHeight: 300
                  };            
                      
                  }else{
                    this.showLoader = false;
                    this.getNeslist = [];
                  }
              }, 100); */
              //Please Comment while checkIn
        });
      }
      else
      {
        // this.nesListData = JSON.parse('{"eNBList":[{"eNBName":"061192_NORTHWOOD_LAKE_NH","eNBId":"61192"},{"eNBName":"061452_CONCORD_2_NH_HUB","eNBId":"61452"},{"eNBName":"073461_PRATTSBURGH","eNBId":"73461"},{"eNBName":"073462_East_Corning","eNBId":"73462"},{"eNBName":"073466_Howard","eNBId":"73466"},{"eNBName":"073474_Hornellsville","eNBId":"73474"},{"eNBName":"073484_ADDISON","eNBId":"73484"},{"eNBName":"072409_Press_Building","eNBId":"72409"},{"eNBName":"072412_Binghamton_DT","eNBId":"72412"},{"eNBName":"072413_SUNY_Binghamton","eNBId":"72413"},{"eNBName":"072415_Vestal","eNBId":"72415"},{"eNBName":"072416_Chenango","eNBId":"72416"},{"eNBName":"072417_Kirkwood","eNBId":"72417"},{"eNBName":"072419_Windsor","eNBId":"72419"},{"eNBName":"072424_CASTLE_CREEK","eNBId":"72424"},{"eNBName":"072425_Killawog","eNBId":"72425"},{"eNBName":"072426_East_Richford","eNBId":"72426"},{"eNBName":"072427_Caroline","eNBId":"72427"},{"eNBName":"072430_Owego_North","eNBId":"72430"},{"eNBName":"072431_Owego","eNBId":"72431"},{"eNBName":"072432_Apalachin","eNBId":"72432"},{"eNBName":"072433_Nichols","eNBId":"72433"},{"eNBName":"072442_CROCKER_CREEK","eNBId":"72442"},{"eNBName":"072443_MAINE_DT","eNBId":"72443"},{"eNBName":"072451_BELDEN","eNBId":"72451"},{"eNBName":"072452_TIOGA_CENTER","eNBId":"72452"},{"eNBName":"072454_CATATONK","eNBId":"72454"},{"eNBName":"072458_CHENANGO_DT","eNBId":"72458"},{"eNBName":"072478_Big_Flats","eNBId":"72478"},{"eNBName":"070033_POWERS_RD","eNBId":"70033"},{"eNBName":"070005_RTE_263_GETZVILLE","eNBId":"70005"},{"eNBName":"070562_BOWEN_RD","eNBId":"70562"},{"eNBName":"073313_FREY_RD","eNBId":"73313"},{"eNBName":"073326_BAKER_RD","eNBId":"73326"}],"sessionId":"2a7a3636","serviceToken":"79044","status":"SUCCESS"}');
        // this.getNeslist = this.nesListData.eNBList;
      this.getNeslist=[];
      }
  }
  
  /*
   * Used to serach by ciq name and display the CIQ details
   * @param : event, serachBy
   * @retun : null
   */
  
  getDeatilsByCiq(event, serachBy){
    if (this.ciqFileDetails !="" && this.ciqFileDetails != undefined &&  this.ciqFileDetails.ciqFileName) {
      this.validationData.rules.ciqName.required = false;

    } else {
        this.validationData.rules.ciqName.required = true;

    } 
    this.showForm = false;
    this.sheetDispTab = false;
    this.tableShowHide = false;
    this.eNodeBTableShowHide = false;
    this.serachBy = serachBy;    
    // this.searchStatus = "search";
    this.searchStatus = "load";
    this.searchCriteria = null;
    this.searchFormField = {};
    this.currentPage = 1;
    let paginationDetails = {
                  "count": parseInt(this.pageSize),
                  "page": parseInt(this.currentPage)
                 };

    this.paginationDetails = paginationDetails;
    // TO get the searched data
    validator.performValidation(event, this.validationData, "search");   
        setTimeout(() => {
            if (this.isValidForm(event)) {
              this.fileName = this.ciqFileDetails.ciqFileName;
              this.fileId = this.ciqFileDetails.id;
                this.showLoader = true;                  
                  this.getCiqSheetDetails();
                }
        }, 0);

    }
  getCiqSheetDetails() {
    this.sheetDispTab = true;
    this.showLoader = true;
    this.ranconfigService.getCiqSheetDetails(this.sharedService.createServiceToken(),this.fileName)
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
                  this.sheetDetails = jsonStatue.SheetDetails;
                  this.ciqSheetList = this.objectKeys(jsonStatue.SheetDetails);                   
                  if (this.ciqSheetList.length == 0) {                    
                    this.noDataVisibility = true;
                  } else {                    
                    this.noDataVisibility = false;                     
                    this.getSubSheetList(this.ciqSheetList[0],0);
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
            //this.tableData = {"sessionId":"fee236ab","serviceToken":"78928","status":"SUCCESS","pageCount":2,"ciqUploadDetails":[{"id":451,"fileName":"FDMIMO_FIT_CIQ_08242018_SprintTemplate2.xlsx","ciqMap":{"BBU_Type":"","Cascade":"CH13XC046","Market":"Chicago","Market_ID":1,"LSMR_Name":"chglsmr03lems1","Network_Site_Code":"","eNB_ID":"516790","eNodeB_Name":"CHCGILFCBBULTE0516790","Latitude":"","Longitude":"","TAC_Decimal":"3F13","RRH_Top_Bottom":"","Band":"41","Sector_ID":"1","Cell_ID":"3","EUTRAN_Cell_Global_Id":"","PCI":"55","RSI":"288","Bandwidth":"20MHz","Start_EARFCN":"40994","DL_EARFCN":"41094","UL_EARFCN":"41094","Tx_Diversity":"64T","Initiate_SON":"","Carrier_Aggregation":"","Frame_Config_Version":"","Antenna_Model":"","Antenna_Vendor":"","Azimuth":"","Electrical_Tilt":"","Mechanical_Tilt":"","FDD/TDD":"TDD","DL_Center_Freq_MHz":"2640.4","UL_Center_Freq_MHz":"2640.4","Rx_Diveristy":"64R","Tx_Path_Assignment":"64T_ALL","Attenuation_Path":"0","RS_Boost":"0","eNB_OAM_VLAN":"34","eNB_OAM_VLAN_prefix_(/30)":"30","OAM_GW_IP":"111.3.152.169","eNB_OAM_IP":"111.3.152.170","eNB_S&B_VLAN":"42","eNB_S&B_VLAN_prefix_(/30)":"30","S&B_GW_IP":"10.202.198.169","eNB_S&B_IP":"10.202.198.170","Current_BH_Port":"1","CDU30_BH_Port":"1","Cabinet_(Indoor_Outdoor)":"ADV_OUTDOOR_DIST_TYPE","IOMONITOR_COUNT":"1","EAIU_Type":"EAIU4-U","EAIU_SN":"SE2C408026","Expansion_Cabinet":"No","RRH_Type":"MTP02P_410","MAU-ID":"0","No*_of_ALD":"1","MME_Info_1":"10.158.212.4|0.0.0.0|310|120","MME_Info_2":"10.156.35.44|0.0.0.0|310|120","MME_Info_3":"10.158.212.20|0.0.0.0|310|120","MME_Info_4":"10.156.34.28|0.0.0.0|310|120","MME_Info_5":"10.88.212.81|10.88.212.82|312|530","MME_Info_6":"10.88.152.33|0.0.0.0|310|120","MME_Info_7":"10.88.212.33|0.0.0.0|310|120","Comments":""}},{"id":452,"fileName":"FDMIMO_FIT_CIQ_08242018_SprintTemplate2.xlsx","ciqMap":{"BBU_Type":"","Cascade":"CH13XC046","Market":"Chicago","Market_ID":2,"LSMR_Name":"chglsmr03lems1","Network_Site_Code":"","eNB_ID":"516790","eNodeB_Name":"CHCGILFCBBULTE0516790","Latitude":"","Longitude":"","TAC_Decimal":"3F13","RRH_Top_Bottom":"","Band":"41","Sector_ID":"2","Cell_ID":"4","EUTRAN_Cell_Global_Id":"","PCI":"54","RSI":"296","Bandwidth":"20MHz","Start_EARFCN":"40994","DL_EARFCN":"41094","UL_EARFCN":"41094","Tx_Diversity":"64T","Initiate_SON":"","Carrier_Aggregation":"","Frame_Config_Version":"","Antenna_Model":"","Antenna_Vendor":"","Azimuth":"","Electrical_Tilt":"","Mechanical_Tilt":"","FDD/TDD":"TDD","DL_Center_Freq_MHz":"2640.4","UL_Center_Freq_MHz":"2640.4","Rx_Diveristy":"64R","Tx_Path_Assignment":"64T_ALL","Attenuation_Path":"0","RS_Boost":"0","eNB_OAM_VLAN":"34","eNB_OAM_VLAN_prefix_(/30)":"30","OAM_GW_IP":"111.3.152.169","eNB_OAM_IP":"111.3.152.170","eNB_S&B_VLAN":"42","eNB_S&B_VLAN_prefix_(/30)":"30","S&B_GW_IP":"10.202.198.169","eNB_S&B_IP":"10.202.198.170","Current_BH_Port":"1","CDU30_BH_Port":"1","Cabinet_(Indoor_Outdoor)":"ADV_OUTDOOR_DIST_TYPE","IOMONITOR_COUNT":"1","EAIU_Type":"EAIU4-U","EAIU_SN":"SE2C408026","Expansion_Cabinet":"No","RRH_Type":"MTP02P_410","MAU-ID":"1","No*_of_ALD":"1","MME_Info_1":"10.158.212.4|0.0.0.0|310|120","MME_Info_2":"10.156.35.44|0.0.0.0|310|120","MME_Info_3":"10.158.212.20|0.0.0.0|310|120","MME_Info_4":"10.156.34.28|0.0.0.0|310|120","MME_Info_5":"10.88.212.81|10.88.212.82|312|530","MME_Info_6":"10.88.152.33|0.0.0.0|310|120","MME_Info_7":"10.88.212.33|0.0.0.0|310|120","Comments":""}},{"id":453,"fileName":"FDMIMO_FIT_CIQ_08242018_SprintTemplate2.xlsx","ciqMap":{"BBU_Type":"","Cascade":"CH13XC046","Market":"Chicago","Market_ID":3,"LSMR_Name":"chglsmr03lems1","Network_Site_Code":"","eNB_ID":"516790","eNodeB_Name":"CHCGILFCBBULTE0516790","Latitude":"","Longitude":"","TAC_Decimal":"3F13","RRH_Top_Bottom":"","Band":"41","Sector_ID":"3","Cell_ID":"5","EUTRAN_Cell_Global_Id":"","PCI":"56","RSI":"304","Bandwidth":"20MHz","Start_EARFCN":"40994","DL_EARFCN":"41094","UL_EARFCN":"41094","Tx_Diversity":"64T","Initiate_SON":"","Carrier_Aggregation":"","Frame_Config_Version":"","Antenna_Model":"","Antenna_Vendor":"","Azimuth":"","Electrical_Tilt":"","Mechanical_Tilt":"","FDD/TDD":"TDD","DL_Center_Freq_MHz":"2640.4","UL_Center_Freq_MHz":"2640.4","Rx_Diveristy":"64R","Tx_Path_Assignment":"64T_ALL","Attenuation_Path":"0","RS_Boost":"0","eNB_OAM_VLAN":"34","eNB_OAM_VLAN_prefix_(/30)":"30","OAM_GW_IP":"111.3.152.169","eNB_OAM_IP":"111.3.152.170","eNB_S&B_VLAN":"42","eNB_S&B_VLAN_prefix_(/30)":"30","S&B_GW_IP":"10.202.198.169","eNB_S&B_IP":"10.202.198.170","Current_BH_Port":"1","CDU30_BH_Port":"1","Cabinet_(Indoor_Outdoor)":"ADV_OUTDOOR_DIST_TYPE","IOMONITOR_COUNT":"1","EAIU_Type":"EAIU4-U","EAIU_SN":"SE2C408026","Expansion_Cabinet":"No","RRH_Type":"MTP02P_410","MAU-ID":"2","No*_of_ALD":"1","MME_Info_1":"10.158.212.4|0.0.0.0|310|120","MME_Info_2":"10.156.35.44|0.0.0.0|310|120","MME_Info_3":"10.158.212.20|0.0.0.0|310|120","MME_Info_4":"10.156.34.28|0.0.0.0|310|120","MME_Info_5":"10.88.212.81|10.88.212.82|312|530","MME_Info_6":"10.88.152.33|0.0.0.0|310|120","MME_Info_7":"10.88.212.33|0.0.0.0|310|120","Comments":""}},{"id":454,"fileName":"FDMIMO_FIT_CIQ_08242018_SprintTemplate2.xlsx","ciqMap":{"BBU_Type":"","Cascade":"CH13XC046","Market":"Chicago","Market_ID":4,"LSMR_Name":"chglsmr03lems1","Network_Site_Code":"","eNB_ID":"516790","eNodeB_Name":"CHCGILFCBBULTE0516790","Latitude":"","Longitude":"","TAC_Decimal":"3F13","RRH_Top_Bottom":"","Band":"41","Sector_ID":"1","Cell_ID":"12","EUTRAN_Cell_Global_Id":"","PCI":"55","RSI":"288","Bandwidth":"20MHz","Start_EARFCN":"41192","DL_EARFCN":"41292","UL_EARFCN":"41292","Tx_Diversity":"64T","Initiate_SON":"","Carrier_Aggregation":"","Frame_Config_Version":"","Antenna_Model":"","Antenna_Vendor":"","Azimuth":"","Electrical_Tilt":"","Mechanical_Tilt":"","FDD/TDD":"TDD","DL_Center_Freq_MHz":"2660.2","UL_Center_Freq_MHz":"2660.2","Rx_Diveristy":"64R","Tx_Path_Assignment":"64T_ALL","Attenuation_Path":"0","RS_Boost":"0","eNB_OAM_VLAN":"34","eNB_OAM_VLAN_prefix_(/30)":"30","OAM_GW_IP":"111.3.152.169","eNB_OAM_IP":"111.3.152.170","eNB_S&B_VLAN":"42","eNB_S&B_VLAN_prefix_(/30)":"30","S&B_GW_IP":"10.202.198.169","eNB_S&B_IP":"10.202.198.170","Current_BH_Port":"1","CDU30_BH_Port":"1","Cabinet_(Indoor_Outdoor)":"ADV_OUTDOOR_DIST_TYPE","IOMONITOR_COUNT":"1","EAIU_Type":"EAIU4-U","EAIU_SN":"SE2C408026","Expansion_Cabinet":"No","RRH_Type":"MTP02P_410","MAU-ID":"0","No*_of_ALD":"1","MME_Info_1":"10.158.212.4|0.0.0.0|310|120","MME_Info_2":"10.156.35.44|0.0.0.0|310|120","MME_Info_3":"10.158.212.20|0.0.0.0|310|120","MME_Info_4":"10.156.34.28|0.0.0.0|310|120","MME_Info_5":"10.88.212.81|10.88.212.82|312|530","MME_Info_6":"10.88.152.33|0.0.0.0|310|120","MME_Info_7":"10.88.212.33|0.0.0.0|310|120","Comments":""}},{"id":455,"fileName":"FDMIMO_FIT_CIQ_08242018_SprintTemplate2.xlsx","ciqMap":{"BBU_Type":"","Cascade":"CH13XC046","Market":"Chicago","Market_ID":5,"LSMR_Name":"chglsmr03lems1","Network_Site_Code":"","eNB_ID":"516790","eNodeB_Name":"CHCGILFCBBULTE0516790","Latitude":"","Longitude":"","TAC_Decimal":"3F13","RRH_Top_Bottom":"","Band":"41","Sector_ID":"2","Cell_ID":"13","EUTRAN_Cell_Global_Id":"","PCI":"54","RSI":"296","Bandwidth":"20MHz","Start_EARFCN":"41192","DL_EARFCN":"41292","UL_EARFCN":"41292","Tx_Diversity":"64T","Initiate_SON":"","Carrier_Aggregation":"","Frame_Config_Version":"","Antenna_Model":"","Antenna_Vendor":"","Azimuth":"","Electrical_Tilt":"","Mechanical_Tilt":"","FDD/TDD":"TDD","DL_Center_Freq_MHz":"2660.2","UL_Center_Freq_MHz":"2660.2","Rx_Diveristy":"64R","Tx_Path_Assignment":"64T_ALL","Attenuation_Path":"0","RS_Boost":"0","eNB_OAM_VLAN":"34","eNB_OAM_VLAN_prefix_(/30)":"30","OAM_GW_IP":"111.3.152.169","eNB_OAM_IP":"111.3.152.170","eNB_S&B_VLAN":"42","eNB_S&B_VLAN_prefix_(/30)":"30","S&B_GW_IP":"10.202.198.169","eNB_S&B_IP":"10.202.198.170","Current_BH_Port":"1","CDU30_BH_Port":"1","Cabinet_(Indoor_Outdoor)":"ADV_OUTDOOR_DIST_TYPE","IOMONITOR_COUNT":"1","EAIU_Type":"EAIU4-U","EAIU_SN":"SE2C408026","Expansion_Cabinet":"No","RRH_Type":"MTP02P_410","MAU-ID":"1","No*_of_ALD":"1","MME_Info_1":"10.158.212.4|0.0.0.0|310|120","MME_Info_2":"10.156.35.44|0.0.0.0|310|120","MME_Info_3":"10.158.212.20|0.0.0.0|310|120","MME_Info_4":"10.156.34.28|0.0.0.0|310|120","MME_Info_5":"10.88.212.81|10.88.212.82|312|530","MME_Info_6":"10.88.152.33|0.0.0.0|310|120","MME_Info_7":"10.88.212.33|0.0.0.0|310|120","Comments":""}},{"id":456,"fileName":"FDMIMO_FIT_CIQ_08242018_SprintTemplate2.xlsx","ciqMap":{"BBU_Type":"","Cascade":"CH13XC046","Market":"Chicago","Market_ID":6,"LSMR_Name":"chglsmr03lems1","Network_Site_Code":"","eNB_ID":"516790","eNodeB_Name":"CHCGILFCBBULTE0516790","Latitude":"","Longitude":"","TAC_Decimal":"3F13","RRH_Top_Bottom":"","Band":"41","Sector_ID":"3","Cell_ID":"14","EUTRAN_Cell_Global_Id":"","PCI":"56","RSI":"304","Bandwidth":"20MHz","Start_EARFCN":"41192","DL_EARFCN":"41292","UL_EARFCN":"41292","Tx_Diversity":"64T","Initiate_SON":"","Carrier_Aggregation":"","Frame_Config_Version":"","Antenna_Model":"","Antenna_Vendor":"","Azimuth":"","Electrical_Tilt":"","Mechanical_Tilt":"","FDD/TDD":"TDD","DL_Center_Freq_MHz":"2660.2","UL_Center_Freq_MHz":"2660.2","Rx_Diveristy":"64R","Tx_Path_Assignment":"64T_ALL","Attenuation_Path":"0","RS_Boost":"0","eNB_OAM_VLAN":"34","eNB_OAM_VLAN_prefix_(/30)":"30","OAM_GW_IP":"111.3.152.169","eNB_OAM_IP":"111.3.152.170","eNB_S&B_VLAN":"42","eNB_S&B_VLAN_prefix_(/30)":"30","S&B_GW_IP":"10.202.198.169","eNB_S&B_IP":"10.202.198.170","Current_BH_Port":"1","CDU30_BH_Port":"1","Cabinet_(Indoor_Outdoor)":"ADV_OUTDOOR_DIST_TYPE","IOMONITOR_COUNT":"1","EAIU_Type":"EAIU4-U","EAIU_SN":"SE2C408026","Expansion_Cabinet":"No","RRH_Type":"MTP02P_410","MAU-ID":"2","No*_of_ALD":"1","MME_Info_1":"10.158.212.4|0.0.0.0|310|120","MME_Info_2":"10.156.35.44|0.0.0.0|310|120","MME_Info_3":"10.158.212.20|0.0.0.0|310|120","MME_Info_4":"10.156.34.28|0.0.0.0|310|120","MME_Info_5":"10.88.212.81|10.88.212.82|312|530","MME_Info_6":"10.88.152.33|0.0.0.0|310|120","MME_Info_7":"10.88.212.33|0.0.0.0|310|120","Comments":""}}]};
            let jsonStatue = {"SheetDetails":{"New England CIQ":["A","B"],"MME IP'S":["New England"],"IP PLAN":[]},"sessionId":"b7c9bff0","serviceToken":"87585","status":"SUCCESS"};
            if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {

              this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });

            } else {
              this.sheetDetails = jsonStatue.SheetDetails;
              this.ciqSheetList = this.objectKeys(jsonStatue.SheetDetails);              
              
              if (this.ciqSheetList.length == 0) {               
                this.noDataVisibility = true;
              } else {
                this.noDataVisibility = false;                
                this.currentPage = 1;                                             
                this.getSubSheetList(this.ciqSheetList[0],0);
                
              }
            }
          }, 1000);  */

          //Please Comment while checkIn
        });
  }
  getSubSheetList(sheetName,index){
    this.currentPage = 1;  
    this.searchStatus = "load";
    this.searchCriteria = null;
    this.searchFormField = {};
    let paginationDetails = {
      "count": parseInt(this.pageSize),
      "page": parseInt(this.currentPage)
     };

    this.paginationDetails = paginationDetails;
    this.selSheet = sheetName;     
    this.sheetHighlight = index;    
    this.subSheetList = this.sheetDetails[sheetName];
    this.selSubSheet = this.subSheetList[0] ? this.subSheetList[0]:"";
    this.getAllCiqDetails();
  }
  
   /*
   * Used to get the all EnodeB details as per ciq name selection
   * @param : event, serachBy
   * @retun : null
   */
  
   getAllCiqDetails(){
        this.showForm = true;
        this.tableShowHide = false;         
        let eNBList = [];
        for(let itm of this.selectedNes){
          let eNB = {eNBId:parseInt(itm.item_id) ,eNBName:itm.item_text};
          eNBList.push(eNB);  
        }
        this.showLoader = true;
        this.ranconfigService.getDeatilsByCiq(this.sharedService.createServiceToken(),this.fileName,this.selSheet,this.selSubSheet, this.paginationDetails, this.searchStatus, this.searchCriteria)
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

                              if(this.tableData.SheetDisplayDetails.list.length == 0){
                                this.tableShowHide = false;
                                this.noDataVisibility = true;
                              }else{
                                this.ciqTableData = this.tableData.SheetDisplayDetails.list;
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
                  //this.tableData = {"sessionId":"fee236ab","serviceToken":"78928","status":"SUCCESS","pageCount":2,"ciqUploadDetails":[{"id":451,"fileName":"FDMIMO_FIT_CIQ_08242018_SprintTemplate2.xlsx","ciqMap":{"BBU_Type":"","Cascade":"CH13XC046","Market":"Chicago","Market_ID":1,"LSMR_Name":"chglsmr03lems1","Network_Site_Code":"","eNB_ID":"516790","eNodeB_Name":"CHCGILFCBBULTE0516790","Latitude":"","Longitude":"","TAC_Decimal":"3F13","RRH_Top_Bottom":"","Band":"41","Sector_ID":"1","Cell_ID":"3","EUTRAN_Cell_Global_Id":"","PCI":"55","RSI":"288","Bandwidth":"20MHz","Start_EARFCN":"40994","DL_EARFCN":"41094","UL_EARFCN":"41094","Tx_Diversity":"64T","Initiate_SON":"","Carrier_Aggregation":"","Frame_Config_Version":"","Antenna_Model":"","Antenna_Vendor":"","Azimuth":"","Electrical_Tilt":"","Mechanical_Tilt":"","FDD/TDD":"TDD","DL_Center_Freq_MHz":"2640.4","UL_Center_Freq_MHz":"2640.4","Rx_Diveristy":"64R","Tx_Path_Assignment":"64T_ALL","Attenuation_Path":"0","RS_Boost":"0","eNB_OAM_VLAN":"34","eNB_OAM_VLAN_prefix_(/30)":"30","OAM_GW_IP":"111.3.152.169","eNB_OAM_IP":"111.3.152.170","eNB_S&B_VLAN":"42","eNB_S&B_VLAN_prefix_(/30)":"30","S&B_GW_IP":"10.202.198.169","eNB_S&B_IP":"10.202.198.170","Current_BH_Port":"1","CDU30_BH_Port":"1","Cabinet_(Indoor_Outdoor)":"ADV_OUTDOOR_DIST_TYPE","IOMONITOR_COUNT":"1","EAIU_Type":"EAIU4-U","EAIU_SN":"SE2C408026","Expansion_Cabinet":"No","RRH_Type":"MTP02P_410","MAU-ID":"0","No*_of_ALD":"1","MME_Info_1":"10.158.212.4|0.0.0.0|310|120","MME_Info_2":"10.156.35.44|0.0.0.0|310|120","MME_Info_3":"10.158.212.20|0.0.0.0|310|120","MME_Info_4":"10.156.34.28|0.0.0.0|310|120","MME_Info_5":"10.88.212.81|10.88.212.82|312|530","MME_Info_6":"10.88.152.33|0.0.0.0|310|120","MME_Info_7":"10.88.212.33|0.0.0.0|310|120","Comments":""}},{"id":452,"fileName":"FDMIMO_FIT_CIQ_08242018_SprintTemplate2.xlsx","ciqMap":{"BBU_Type":"","Cascade":"CH13XC046","Market":"Chicago","Market_ID":2,"LSMR_Name":"chglsmr03lems1","Network_Site_Code":"","eNB_ID":"516790","eNodeB_Name":"CHCGILFCBBULTE0516790","Latitude":"","Longitude":"","TAC_Decimal":"3F13","RRH_Top_Bottom":"","Band":"41","Sector_ID":"2","Cell_ID":"4","EUTRAN_Cell_Global_Id":"","PCI":"54","RSI":"296","Bandwidth":"20MHz","Start_EARFCN":"40994","DL_EARFCN":"41094","UL_EARFCN":"41094","Tx_Diversity":"64T","Initiate_SON":"","Carrier_Aggregation":"","Frame_Config_Version":"","Antenna_Model":"","Antenna_Vendor":"","Azimuth":"","Electrical_Tilt":"","Mechanical_Tilt":"","FDD/TDD":"TDD","DL_Center_Freq_MHz":"2640.4","UL_Center_Freq_MHz":"2640.4","Rx_Diveristy":"64R","Tx_Path_Assignment":"64T_ALL","Attenuation_Path":"0","RS_Boost":"0","eNB_OAM_VLAN":"34","eNB_OAM_VLAN_prefix_(/30)":"30","OAM_GW_IP":"111.3.152.169","eNB_OAM_IP":"111.3.152.170","eNB_S&B_VLAN":"42","eNB_S&B_VLAN_prefix_(/30)":"30","S&B_GW_IP":"10.202.198.169","eNB_S&B_IP":"10.202.198.170","Current_BH_Port":"1","CDU30_BH_Port":"1","Cabinet_(Indoor_Outdoor)":"ADV_OUTDOOR_DIST_TYPE","IOMONITOR_COUNT":"1","EAIU_Type":"EAIU4-U","EAIU_SN":"SE2C408026","Expansion_Cabinet":"No","RRH_Type":"MTP02P_410","MAU-ID":"1","No*_of_ALD":"1","MME_Info_1":"10.158.212.4|0.0.0.0|310|120","MME_Info_2":"10.156.35.44|0.0.0.0|310|120","MME_Info_3":"10.158.212.20|0.0.0.0|310|120","MME_Info_4":"10.156.34.28|0.0.0.0|310|120","MME_Info_5":"10.88.212.81|10.88.212.82|312|530","MME_Info_6":"10.88.152.33|0.0.0.0|310|120","MME_Info_7":"10.88.212.33|0.0.0.0|310|120","Comments":""}},{"id":453,"fileName":"FDMIMO_FIT_CIQ_08242018_SprintTemplate2.xlsx","ciqMap":{"BBU_Type":"","Cascade":"CH13XC046","Market":"Chicago","Market_ID":3,"LSMR_Name":"chglsmr03lems1","Network_Site_Code":"","eNB_ID":"516790","eNodeB_Name":"CHCGILFCBBULTE0516790","Latitude":"","Longitude":"","TAC_Decimal":"3F13","RRH_Top_Bottom":"","Band":"41","Sector_ID":"3","Cell_ID":"5","EUTRAN_Cell_Global_Id":"","PCI":"56","RSI":"304","Bandwidth":"20MHz","Start_EARFCN":"40994","DL_EARFCN":"41094","UL_EARFCN":"41094","Tx_Diversity":"64T","Initiate_SON":"","Carrier_Aggregation":"","Frame_Config_Version":"","Antenna_Model":"","Antenna_Vendor":"","Azimuth":"","Electrical_Tilt":"","Mechanical_Tilt":"","FDD/TDD":"TDD","DL_Center_Freq_MHz":"2640.4","UL_Center_Freq_MHz":"2640.4","Rx_Diveristy":"64R","Tx_Path_Assignment":"64T_ALL","Attenuation_Path":"0","RS_Boost":"0","eNB_OAM_VLAN":"34","eNB_OAM_VLAN_prefix_(/30)":"30","OAM_GW_IP":"111.3.152.169","eNB_OAM_IP":"111.3.152.170","eNB_S&B_VLAN":"42","eNB_S&B_VLAN_prefix_(/30)":"30","S&B_GW_IP":"10.202.198.169","eNB_S&B_IP":"10.202.198.170","Current_BH_Port":"1","CDU30_BH_Port":"1","Cabinet_(Indoor_Outdoor)":"ADV_OUTDOOR_DIST_TYPE","IOMONITOR_COUNT":"1","EAIU_Type":"EAIU4-U","EAIU_SN":"SE2C408026","Expansion_Cabinet":"No","RRH_Type":"MTP02P_410","MAU-ID":"2","No*_of_ALD":"1","MME_Info_1":"10.158.212.4|0.0.0.0|310|120","MME_Info_2":"10.156.35.44|0.0.0.0|310|120","MME_Info_3":"10.158.212.20|0.0.0.0|310|120","MME_Info_4":"10.156.34.28|0.0.0.0|310|120","MME_Info_5":"10.88.212.81|10.88.212.82|312|530","MME_Info_6":"10.88.152.33|0.0.0.0|310|120","MME_Info_7":"10.88.212.33|0.0.0.0|310|120","Comments":""}},{"id":454,"fileName":"FDMIMO_FIT_CIQ_08242018_SprintTemplate2.xlsx","ciqMap":{"BBU_Type":"","Cascade":"CH13XC046","Market":"Chicago","Market_ID":4,"LSMR_Name":"chglsmr03lems1","Network_Site_Code":"","eNB_ID":"516790","eNodeB_Name":"CHCGILFCBBULTE0516790","Latitude":"","Longitude":"","TAC_Decimal":"3F13","RRH_Top_Bottom":"","Band":"41","Sector_ID":"1","Cell_ID":"12","EUTRAN_Cell_Global_Id":"","PCI":"55","RSI":"288","Bandwidth":"20MHz","Start_EARFCN":"41192","DL_EARFCN":"41292","UL_EARFCN":"41292","Tx_Diversity":"64T","Initiate_SON":"","Carrier_Aggregation":"","Frame_Config_Version":"","Antenna_Model":"","Antenna_Vendor":"","Azimuth":"","Electrical_Tilt":"","Mechanical_Tilt":"","FDD/TDD":"TDD","DL_Center_Freq_MHz":"2660.2","UL_Center_Freq_MHz":"2660.2","Rx_Diveristy":"64R","Tx_Path_Assignment":"64T_ALL","Attenuation_Path":"0","RS_Boost":"0","eNB_OAM_VLAN":"34","eNB_OAM_VLAN_prefix_(/30)":"30","OAM_GW_IP":"111.3.152.169","eNB_OAM_IP":"111.3.152.170","eNB_S&B_VLAN":"42","eNB_S&B_VLAN_prefix_(/30)":"30","S&B_GW_IP":"10.202.198.169","eNB_S&B_IP":"10.202.198.170","Current_BH_Port":"1","CDU30_BH_Port":"1","Cabinet_(Indoor_Outdoor)":"ADV_OUTDOOR_DIST_TYPE","IOMONITOR_COUNT":"1","EAIU_Type":"EAIU4-U","EAIU_SN":"SE2C408026","Expansion_Cabinet":"No","RRH_Type":"MTP02P_410","MAU-ID":"0","No*_of_ALD":"1","MME_Info_1":"10.158.212.4|0.0.0.0|310|120","MME_Info_2":"10.156.35.44|0.0.0.0|310|120","MME_Info_3":"10.158.212.20|0.0.0.0|310|120","MME_Info_4":"10.156.34.28|0.0.0.0|310|120","MME_Info_5":"10.88.212.81|10.88.212.82|312|530","MME_Info_6":"10.88.152.33|0.0.0.0|310|120","MME_Info_7":"10.88.212.33|0.0.0.0|310|120","Comments":""}},{"id":455,"fileName":"FDMIMO_FIT_CIQ_08242018_SprintTemplate2.xlsx","ciqMap":{"BBU_Type":"","Cascade":"CH13XC046","Market":"Chicago","Market_ID":5,"LSMR_Name":"chglsmr03lems1","Network_Site_Code":"","eNB_ID":"516790","eNodeB_Name":"CHCGILFCBBULTE0516790","Latitude":"","Longitude":"","TAC_Decimal":"3F13","RRH_Top_Bottom":"","Band":"41","Sector_ID":"2","Cell_ID":"13","EUTRAN_Cell_Global_Id":"","PCI":"54","RSI":"296","Bandwidth":"20MHz","Start_EARFCN":"41192","DL_EARFCN":"41292","UL_EARFCN":"41292","Tx_Diversity":"64T","Initiate_SON":"","Carrier_Aggregation":"","Frame_Config_Version":"","Antenna_Model":"","Antenna_Vendor":"","Azimuth":"","Electrical_Tilt":"","Mechanical_Tilt":"","FDD/TDD":"TDD","DL_Center_Freq_MHz":"2660.2","UL_Center_Freq_MHz":"2660.2","Rx_Diveristy":"64R","Tx_Path_Assignment":"64T_ALL","Attenuation_Path":"0","RS_Boost":"0","eNB_OAM_VLAN":"34","eNB_OAM_VLAN_prefix_(/30)":"30","OAM_GW_IP":"111.3.152.169","eNB_OAM_IP":"111.3.152.170","eNB_S&B_VLAN":"42","eNB_S&B_VLAN_prefix_(/30)":"30","S&B_GW_IP":"10.202.198.169","eNB_S&B_IP":"10.202.198.170","Current_BH_Port":"1","CDU30_BH_Port":"1","Cabinet_(Indoor_Outdoor)":"ADV_OUTDOOR_DIST_TYPE","IOMONITOR_COUNT":"1","EAIU_Type":"EAIU4-U","EAIU_SN":"SE2C408026","Expansion_Cabinet":"No","RRH_Type":"MTP02P_410","MAU-ID":"1","No*_of_ALD":"1","MME_Info_1":"10.158.212.4|0.0.0.0|310|120","MME_Info_2":"10.156.35.44|0.0.0.0|310|120","MME_Info_3":"10.158.212.20|0.0.0.0|310|120","MME_Info_4":"10.156.34.28|0.0.0.0|310|120","MME_Info_5":"10.88.212.81|10.88.212.82|312|530","MME_Info_6":"10.88.152.33|0.0.0.0|310|120","MME_Info_7":"10.88.212.33|0.0.0.0|310|120","Comments":""}},{"id":456,"fileName":"FDMIMO_FIT_CIQ_08242018_SprintTemplate2.xlsx","ciqMap":{"BBU_Type":"","Cascade":"CH13XC046","Market":"Chicago","Market_ID":6,"LSMR_Name":"chglsmr03lems1","Network_Site_Code":"","eNB_ID":"516790","eNodeB_Name":"CHCGILFCBBULTE0516790","Latitude":"","Longitude":"","TAC_Decimal":"3F13","RRH_Top_Bottom":"","Band":"41","Sector_ID":"3","Cell_ID":"14","EUTRAN_Cell_Global_Id":"","PCI":"56","RSI":"304","Bandwidth":"20MHz","Start_EARFCN":"41192","DL_EARFCN":"41292","UL_EARFCN":"41292","Tx_Diversity":"64T","Initiate_SON":"","Carrier_Aggregation":"","Frame_Config_Version":"","Antenna_Model":"","Antenna_Vendor":"","Azimuth":"","Electrical_Tilt":"","Mechanical_Tilt":"","FDD/TDD":"TDD","DL_Center_Freq_MHz":"2660.2","UL_Center_Freq_MHz":"2660.2","Rx_Diveristy":"64R","Tx_Path_Assignment":"64T_ALL","Attenuation_Path":"0","RS_Boost":"0","eNB_OAM_VLAN":"34","eNB_OAM_VLAN_prefix_(/30)":"30","OAM_GW_IP":"111.3.152.169","eNB_OAM_IP":"111.3.152.170","eNB_S&B_VLAN":"42","eNB_S&B_VLAN_prefix_(/30)":"30","S&B_GW_IP":"10.202.198.169","eNB_S&B_IP":"10.202.198.170","Current_BH_Port":"1","CDU30_BH_Port":"1","Cabinet_(Indoor_Outdoor)":"ADV_OUTDOOR_DIST_TYPE","IOMONITOR_COUNT":"1","EAIU_Type":"EAIU4-U","EAIU_SN":"SE2C408026","Expansion_Cabinet":"No","RRH_Type":"MTP02P_410","MAU-ID":"2","No*_of_ALD":"1","MME_Info_1":"10.158.212.4|0.0.0.0|310|120","MME_Info_2":"10.156.35.44|0.0.0.0|310|120","MME_Info_3":"10.158.212.20|0.0.0.0|310|120","MME_Info_4":"10.156.34.28|0.0.0.0|310|120","MME_Info_5":"10.88.212.81|10.88.212.82|312|530","MME_Info_6":"10.88.152.33|0.0.0.0|310|120","MME_Info_7":"10.88.212.33|0.0.0.0|310|120","Comments":""}}]};
                  //OLD json                 
                  if(this.selSheet == 'New England CIQ'){
                  this.tableData = JSON.parse('{"pageCount":2,"SheetDisplayDetails":{"count":2,"list":[{"id":1,"fileName":"UNY-NE-VZ_CIQ_Ver2.82_01282019.xlsx","eNBId":"57307","eNBName":"057307_PLYMOUTH_7_MA","sheetName":"New England CIQ","seqOrder":"1","subSheetName":null,"subSheetAliasName":null,"sheetAliasName":"CIQNewEngland","sheetId":null,"ciqMap":{"Market":{"headerName":"Market","headerValue":"New England"},"eNB_Name":{"headerName":"eNB_Name","headerValue":"057307_PLYMOUTH_7_MA"},"Samsung_eNB_ID":{"headerName":"Samsung_eNB_ID","headerValue":"57307"},"Cell_ID":{"headerName":"Cell_ID","headerValue":"1"},"TAC":{"headerName":"TAC","headerValue":"14592"},"PCI":{"headerName":"PCI","headerValue":"151"},"RACH":{"headerName":"RACH","headerValue":"210"},"BandName":{"headerName":"BandName","headerValue":"700MHz"},"Bandwidth(MHz)":{"headerName":"Bandwidth(MHz)","headerValue":"10MHz"},"EARFCN_DL":{"headerName":"EARFCN_DL","headerValue":"5230"},"EARFCN_UL":{"headerName":"EARFCN_UL","headerValue":"23230"},"Output_Power(dBm)":{"headerName":"Output_Power_(dBm)","headerValue":"47.8"},"CPRI_Port_Assignment":{"headerName":"CPRI_Port_Assignment","headerValue":"0"},"Tx_Diversity":{"headerName":"Tx_Diversity","headerValue":"2"},"Rx_Diveristy":{"headerName":"Rx_Diveristy","headerValue":"4"},"Electrical_Tilt":{"headerName":"Electrical_Tilt","headerValue":"3"},"RRH_Type":{"headerName":"RRH_Type","headerValue":"B13RRH4x30"},"Card_Count_per_eNB":{"headerName":"Card_Count_per_eNB","headerValue":"1"},"Deployment":{"headerName":"Deployment","headerValue":"Open CPRI"},"RRH_Code":{"headerName":"RRH_Code","headerValue":"3JR53386AA"},"Market_CLLI_Code":{"headerName":"Market_CLLI_Code","headerValue":"WRXBMAWA_002_LTE"},"aliasName":{"headerName":"aliasName","headerValue":"057307_1;MC;"},"antennaPathDelayDL":{"headerName":"antennaPathDelayDL","headerValue":"10"},"antennaPathDelayUL":{"headerName":"antennaPathDelayUL","headerValue":"10"},"antennaPathDelayDL(m)":{"headerName":"antennaPathDelayDL_(m)","headerValue":"2"},"antennaPathDelayUL(m)":{"headerName":"antennaPathDelayUL_(m)","headerValue":"2"},"DAS":{"headerName":"DAS","headerValue":""},"DAS_OUTPUT_POWER":{"headerName":"DAS_OUTPUT_POWER","headerValue":""},"NB-IoT_TAC":{"headerName":"NB-IoT_TAC","headerValue":"14720"}}},{"id":2,"fileName":"UNY-NE-VZ_CIQ_Ver2.82_01282019.xlsx","eNBId":"57307","eNBName":"057307_PLYMOUTH_7_MA","sheetName":"New England CIQ","seqOrder":"1","subSheetName":null,"subSheetAliasName":null,"sheetAliasName":"CIQNewEngland","sheetId":null,"ciqMap":{"Market":{"headerName":"Market","headerValue":"New England"},"eNB_Name":{"headerName":"eNB_Name","headerValue":"057307_PLYMOUTH_7_MA"},"Samsung_eNB_ID":{"headerName":"Samsung_eNB_ID","headerValue":"57307"},"Cell_ID":{"headerName":"Cell_ID","headerValue":"2"},"TAC":{"headerName":"TAC","headerValue":"14592"},"PCI":{"headerName":"PCI","headerValue":"150"},"RACH":{"headerName":"RACH","headerValue":"210"},"BandName":{"headerName":"BandName","headerValue":"700MHz"},"Bandwidth(MHz)":{"headerName":"Bandwidth(MHz)","headerValue":"10MHz"},"EARFCN_DL":{"headerName":"EARFCN_DL","headerValue":"5230"},"EARFCN_UL":{"headerName":"EARFCN_UL","headerValue":"23230"},"Output_Power(dBm)":{"headerName":"Output_Power_(dBm)","headerValue":"47.8"},"CPRI_Port_Assignment":{"headerName":"CPRI_Port_Assignment","headerValue":"1"},"Tx_Diversity":{"headerName":"Tx_Diversity","headerValue":"2"},"Rx_Diveristy":{"headerName":"Rx_Diveristy","headerValue":"4"},"Electrical_Tilt":{"headerName":"Electrical_Tilt","headerValue":"2"},"RRH_Type":{"headerName":"RRH_Type","headerValue":"B13RRH4x30"},"Card_Count_per_eNB":{"headerName":"Card_Count_per_eNB","headerValue":"1"},"Deployment":{"headerName":"Deployment","headerValue":"Open CPRI"},"RRH_Code":{"headerName":"RRH_Code","headerValue":"3JR53386AA"},"Market_CLLI_Code":{"headerName":"Market_CLLI_Code","headerValue":"WRXBMAWA_002_LTE"},"aliasName":{"headerName":"aliasName","headerValue":"057307_2;MC;"},"antennaPathDelayDL":{"headerName":"antennaPathDelayDL","headerValue":"10"},"antennaPathDelayUL":{"headerName":"antennaPathDelayUL","headerValue":"10"},"antennaPathDelayDL(m)":{"headerName":"antennaPathDelayDL_(m)","headerValue":"2"},"antennaPathDelayUL(m)":{"headerName":"antennaPathDelayUL_(m)","headerValue":"2"},"DAS":{"headerName":"DAS","headerValue":""},"DAS_OUTPUT_POWER":{"headerName":"DAS_OUTPUT_POWER","headerValue":""},"NB-IoT_TAC":{"headerName":"NB-IoT_TAC","headerValue":"14720"}}},{"id":3,"fileName":"UNY-NE-VZ_CIQ_Ver2.82_01282019.xlsx","eNBId":"57307","eNBName":"057307_PLYMOUTH_7_MA","sheetName":"New England CIQ","seqOrder":"1","subSheetName":null,"subSheetAliasName":null,"sheetAliasName":"CIQNewEngland","sheetId":null,"ciqMap":{"Market":{"headerName":"Market","headerValue":"New England"},"eNB_Name":{"headerName":"eNB_Name","headerValue":"057307_PLYMOUTH_7_MA"},"Samsung_eNB_ID":{"headerName":"Samsung_eNB_ID","headerValue":"57307"},"Cell_ID":{"headerName":"Cell_ID","headerValue":"3"},"TAC":{"headerName":"TAC","headerValue":"14592"},"PCI":{"headerName":"PCI","headerValue":"152"},"RACH":{"headerName":"RACH","headerValue":"210"},"BandName":{"headerName":"BandName","headerValue":"700MHz"},"Bandwidth(MHz)":{"headerName":"Bandwidth(MHz)","headerValue":"10MHz"},"EARFCN_DL":{"headerName":"EARFCN_DL","headerValue":"5230"},"EARFCN_UL":{"headerName":"EARFCN_UL","headerValue":"23230"},"Output_Power(dBm)":{"headerName":"Output_Power_(dBm)","headerValue":"47.8"},"CPRI_Port_Assignment":{"headerName":"CPRI_Port_Assignment","headerValue":"2"},"Tx_Diversity":{"headerName":"Tx_Diversity","headerValue":"2"},"Rx_Diveristy":{"headerName":"Rx_Diveristy","headerValue":"4"},"Electrical_Tilt":{"headerName":"Electrical_Tilt","headerValue":"2"},"RRH_Type":{"headerName":"RRH_Type","headerValue":"B13RRH4x30"},"Card_Count_per_eNB":{"headerName":"Card_Count_per_eNB","headerValue":"1"},"Deployment":{"headerName":"Deployment","headerValue":"Open CPRI"},"RRH_Code":{"headerName":"RRH_Code","headerValue":"3JR53386AA"},"Market_CLLI_Code":{"headerName":"Market_CLLI_Code","headerValue":"WRXBMAWA_002_LTE"},"aliasName":{"headerName":"aliasName","headerValue":"057307_3;MC;"},"antennaPathDelayDL":{"headerName":"antennaPathDelayDL","headerValue":"10"},"antennaPathDelayUL":{"headerName":"antennaPathDelayUL","headerValue":"10"},"antennaPathDelayDL(m)":{"headerName":"antennaPathDelayDL_(m)","headerValue":"2"},"antennaPathDelayUL(m)":{"headerName":"antennaPathDelayUL_(m)","headerValue":"2"},"DAS":{"headerName":"DAS","headerValue":""},"DAS_OUTPUT_POWER":{"headerName":"DAS_OUTPUT_POWER","headerValue":""},"NB-IoT_TAC":{"headerName":"NB-IoT_TAC","headerValue":"14720"}}},{"id":4,"fileName":"UNY-NE-VZ_CIQ_Ver2.82_01282019.xlsx","eNBId":"57307","eNBName":"057307_PLYMOUTH_7_MA","sheetName":"New England CIQ","seqOrder":"1","subSheetName":null,"subSheetAliasName":null,"sheetAliasName":"CIQNewEngland","sheetId":null,"ciqMap":{"Market":{"headerName":"Market","headerValue":"New England"},"eNB_Name":{"headerName":"eNB_Name","headerValue":"057307_PLYMOUTH_7_MA"},"Samsung_eNB_ID":{"headerName":"Samsung_eNB_ID","headerValue":"57307"},"Cell_ID":{"headerName":"Cell_ID","headerValue":"12"},"TAC":{"headerName":"TAC","headerValue":"14592"},"PCI":{"headerName":"PCI","headerValue":"151"},"RACH":{"headerName":"RACH","headerValue":"210"},"BandName":{"headerName":"BandName","headerValue":"AWS-1"},"Bandwidth(MHz)":{"headerName":"Bandwidth(MHz)","headerValue":"20MHz"},"EARFCN_DL":{"headerName":"EARFCN_DL","headerValue":"2050"},"EARFCN_UL":{"headerName":"EARFCN_UL","headerValue":"20050"},"Output_Power(dBm)":{"headerName":"Output_Power_(dBm)","headerValue":"46.5"},"CPRI_Port_Assignment":{"headerName":"CPRI_Port_Assignment","headerValue":"3"},"Tx_Diversity":{"headerName":"Tx_Diversity","headerValue":"4"},"Rx_Diveristy":{"headerName":"Rx_Diveristy","headerValue":"4"},"Electrical_Tilt":{"headerName":"Electrical_Tilt","headerValue":"1"},"RRH_Type":{"headerName":"RRH_Type","headerValue":"B66aRRH4x45"},"Card_Count_per_eNB":{"headerName":"Card_Count_per_eNB","headerValue":"1"},"Deployment":{"headerName":"Deployment","headerValue":"Open CPRI"},"RRH_Code":{"headerName":"RRH_Code","headerValue":"3JR59011AA"},"Market_CLLI_Code":{"headerName":"Market_CLLI_Code","headerValue":"WRXBMAWA_002_LTE"},"aliasName":{"headerName":"aliasName","headerValue":"057307_1_2;MC;"},"antennaPathDelayDL":{"headerName":"antennaPathDelayDL","headerValue":"9"},"antennaPathDelayUL":{"headerName":"antennaPathDelayUL","headerValue":"9"},"antennaPathDelayDL(m)":{"headerName":"antennaPathDelayDL_(m)","headerValue":"2"},"antennaPathDelayUL(m)":{"headerName":"antennaPathDelayUL_(m)","headerValue":"2"},"DAS":{"headerName":"DAS","headerValue":""},"DAS_OUTPUT_POWER":{"headerName":"DAS_OUTPUT_POWER","headerValue":""},"NB-IoT_TAC":{"headerName":"NB-IoT_TAC","headerValue":""}}},{"id":5,"fileName":"UNY-NE-VZ_CIQ_Ver2.82_01282019.xlsx","eNBId":"57307","eNBName":"057307_PLYMOUTH_7_MA","sheetName":"New England CIQ","seqOrder":"1","subSheetName":null,"subSheetAliasName":null,"sheetAliasName":"CIQNewEngland","sheetId":null,"ciqMap":{"Market":{"headerName":"Market","headerValue":"New England"},"eNB_Name":{"headerName":"eNB_Name","headerValue":"057307_PLYMOUTH_7_MA"},"Samsung_eNB_ID":{"headerName":"Samsung_eNB_ID","headerValue":"57307"},"Cell_ID":{"headerName":"Cell_ID","headerValue":"22"},"TAC":{"headerName":"TAC","headerValue":"14592"},"PCI":{"headerName":"PCI","headerValue":"150"},"RACH":{"headerName":"RACH","headerValue":"210"},"BandName":{"headerName":"BandName","headerValue":"AWS-1"},"Bandwidth(MHz)":{"headerName":"Bandwidth(MHz)","headerValue":"20MHz"},"EARFCN_DL":{"headerName":"EARFCN_DL","headerValue":"2050"},"EARFCN_UL":{"headerName":"EARFCN_UL","headerValue":"20050"},"Output_Power(dBm)":{"headerName":"Output_Power_(dBm)","headerValue":"46.5"},"CPRI_Port_Assignment":{"headerName":"CPRI_Port_Assignment","headerValue":"4"},"Tx_Diversity":{"headerName":"Tx_Diversity","headerValue":"4"},"Rx_Diveristy":{"headerName":"Rx_Diveristy","headerValue":"4"},"Electrical_Tilt":{"headerName":"Electrical_Tilt","headerValue":"1"},"RRH_Type":{"headerName":"RRH_Type","headerValue":"B66aRRH4x45"},"Card_Count_per_eNB":{"headerName":"Card_Count_per_eNB","headerValue":"1"},"Deployment":{"headerName":"Deployment","headerValue":"Open CPRI"},"RRH_Code":{"headerName":"RRH_Code","headerValue":"3JR59011AA"},"Market_CLLI_Code":{"headerName":"Market_CLLI_Code","headerValue":"WRXBMAWA_002_LTE"},"aliasName":{"headerName":"aliasName","headerValue":"057307_2_2;MC;"},"antennaPathDelayDL":{"headerName":"antennaPathDelayDL","headerValue":"9"},"antennaPathDelayUL":{"headerName":"antennaPathDelayUL","headerValue":"9"},"antennaPathDelayDL(m)":{"headerName":"antennaPathDelayDL_(m)","headerValue":"2"},"antennaPathDelayUL(m)":{"headerName":"antennaPathDelayUL_(m)","headerValue":"2"},"DAS":{"headerName":"DAS","headerValue":""},"DAS_OUTPUT_POWER":{"headerName":"DAS_OUTPUT_POWER","headerValue":""},"NB-IoT_TAC":{"headerName":"NB-IoT_TAC","headerValue":""}}},{"id":6,"fileName":"UNY-NE-VZ_CIQ_Ver2.82_01282019.xlsx","eNBId":"57307","eNBName":"057307_PLYMOUTH_7_MA","sheetName":"New England CIQ","seqOrder":"1","subSheetName":null,"subSheetAliasName":null,"sheetAliasName":"CIQNewEngland","sheetId":null,"ciqMap":{"Market":{"headerName":"Market","headerValue":"New England"},"eNB_Name":{"headerName":"eNB_Name","headerValue":"057307_PLYMOUTH_7_MA"},"Samsung_eNB_ID":{"headerName":"Samsung_eNB_ID","headerValue":"57307"},"Cell_ID":{"headerName":"Cell_ID","headerValue":"32"},"TAC":{"headerName":"TAC","headerValue":"14592"},"PCI":{"headerName":"PCI","headerValue":"152"},"RACH":{"headerName":"RACH","headerValue":"210"},"BandName":{"headerName":"BandName","headerValue":"AWS-1"},"Bandwidth(MHz)":{"headerName":"Bandwidth(MHz)","headerValue":"20MHz"},"EARFCN_DL":{"headerName":"EARFCN_DL","headerValue":"2050"},"EARFCN_UL":{"headerName":"EARFCN_UL","headerValue":"20050"},"Output_Power(dBm)":{"headerName":"Output_Power_(dBm)","headerValue":"46.5"},"CPRI_Port_Assignment":{"headerName":"CPRI_Port_Assignment","headerValue":"5"},"Tx_Diversity":{"headerName":"Tx_Diversity","headerValue":"4"},"Rx_Diveristy":{"headerName":"Rx_Diveristy","headerValue":"4"},"Electrical_Tilt":{"headerName":"Electrical_Tilt","headerValue":"1"},"RRH_Type":{"headerName":"RRH_Type","headerValue":"B66aRRH4x45"},"Card_Count_per_eNB":{"headerName":"Card_Count_per_eNB","headerValue":"1"},"Deployment":{"headerName":"Deployment","headerValue":"Open CPRI"},"RRH_Code":{"headerName":"RRH_Code","headerValue":"3JR59011AA"},"Market_CLLI_Code":{"headerName":"Market_CLLI_Code","headerValue":"WRXBMAWA_002_LTE"},"aliasName":{"headerName":"aliasName","headerValue":"057307_3_2;MC;"},"antennaPathDelayDL":{"headerName":"antennaPathDelayDL","headerValue":"9"},"antennaPathDelayUL":{"headerName":"antennaPathDelayUL","headerValue":"9"},"antennaPathDelayDL(m)":{"headerName":"antennaPathDelayDL_(m)","headerValue":"2"},"antennaPathDelayUL(m)":{"headerName":"antennaPathDelayUL_(m)","headerValue":"2"},"DAS":{"headerName":"DAS","headerValue":""},"DAS_OUTPUT_POWER":{"headerName":"DAS_OUTPUT_POWER","headerValue":""},"NB-IoT_TAC":{"headerName":"NB-IoT_TAC","headerValue":""}}},{"id":7,"fileName":"UNY-NE-VZ_CIQ_Ver2.82_01282019.xlsx","eNBId":"59292","eNBName":"059292_MENDON_2_MA","sheetName":"New England CIQ","seqOrder":"1","subSheetName":null,"subSheetAliasName":null,"sheetAliasName":"CIQNewEngland","sheetId":null,"ciqMap":{"Market":{"headerName":"Market","headerValue":"New England"},"eNB_Name":{"headerName":"eNB_Name","headerValue":"059292_MENDON_2_MA"},"Samsung_eNB_ID":{"headerName":"Samsung_eNB_ID","headerValue":"59292"},"Cell_ID":{"headerName":"Cell_ID","headerValue":"1"},"TAC":{"headerName":"TAC","headerValue":"15106"},"PCI":{"headerName":"PCI","headerValue":"182"},"RACH":{"headerName":"RACH","headerValue":"110"},"BandName":{"headerName":"BandName","headerValue":"700MHz"},"Bandwidth(MHz)":{"headerName":"Bandwidth(MHz)","headerValue":"10MHz"},"EARFCN_DL":{"headerName":"EARFCN_DL","headerValue":"5230"},"EARFCN_UL":{"headerName":"EARFCN_UL","headerValue":"23230"},"Output_Power(dBm)":{"headerName":"Output_Power_(dBm)","headerValue":"47.8"},"CPRI_Port_Assignment":{"headerName":"CPRI_Port_Assignment","headerValue":"0"},"Tx_Diversity":{"headerName":"Tx_Diversity","headerValue":"2"},"Rx_Diveristy":{"headerName":"Rx_Diveristy","headerValue":"4"},"Electrical_Tilt":{"headerName":"Electrical_Tilt","headerValue":"0"},"RRH_Type":{"headerName":"RRH_Type","headerValue":"B13RRH4x30"},"Card_Count_per_eNB":{"headerName":"Card_Count_per_eNB","headerValue":"1"},"Deployment":{"headerName":"Deployment","headerValue":"Open CPRI"},"RRH_Code":{"headerName":"RRH_Code","headerValue":"3JR53386AA"},"Market_CLLI_Code":{"headerName":"Market_CLLI_Code","headerValue":"WSBOMAGJ_001_LTE"},"aliasName":{"headerName":"aliasName","headerValue":"059292_1;MC;"},"antennaPathDelayDL":{"headerName":"antennaPathDelayDL","headerValue":"7"},"antennaPathDelayUL":{"headerName":"antennaPathDelayUL","headerValue":"7.25"},"antennaPathDelayDL(m)":{"headerName":"antennaPathDelayDL_(m)","headerValue":"1"},"antennaPathDelayUL(m)":{"headerName":"antennaPathDelayUL_(m)","headerValue":"1"},"DAS":{"headerName":"DAS","headerValue":""},"DAS_OUTPUT_POWER":{"headerName":"DAS_OUTPUT_POWER","headerValue":""},"NB-IoT_TAC":{"headerName":"NB-IoT_TAC","headerValue":"15234"}}},{"id":8,"fileName":"UNY-NE-VZ_CIQ_Ver2.82_01282019.xlsx","eNBId":"59292","eNBName":"059292_MENDON_2_MA","sheetName":"New England CIQ","seqOrder":"1","subSheetName":null,"subSheetAliasName":null,"sheetAliasName":"CIQNewEngland","sheetId":null,"ciqMap":{"Market":{"headerName":"Market","headerValue":"New England"},"eNB_Name":{"headerName":"eNB_Name","headerValue":"059292_MENDON_2_MA"},"Samsung_eNB_ID":{"headerName":"Samsung_eNB_ID","headerValue":"59292"},"Cell_ID":{"headerName":"Cell_ID","headerValue":"2"},"TAC":{"headerName":"TAC","headerValue":"15106"},"PCI":{"headerName":"PCI","headerValue":"181"},"RACH":{"headerName":"RACH","headerValue":"110"},"BandName":{"headerName":"BandName","headerValue":"700MHz"},"Bandwidth(MHz)":{"headerName":"Bandwidth(MHz)","headerValue":"10MHz"},"EARFCN_DL":{"headerName":"EARFCN_DL","headerValue":"5230"},"EARFCN_UL":{"headerName":"EARFCN_UL","headerValue":"23230"},"Output_Power(dBm)":{"headerName":"Output_Power_(dBm)","headerValue":"47.8"},"CPRI_Port_Assignment":{"headerName":"CPRI_Port_Assignment","headerValue":"1"},"Tx_Diversity":{"headerName":"Tx_Diversity","headerValue":"2"},"Rx_Diveristy":{"headerName":"Rx_Diveristy","headerValue":"4"},"Electrical_Tilt":{"headerName":"Electrical_Tilt","headerValue":"6"},"RRH_Type":{"headerName":"RRH_Type","headerValue":"B13RRH4x30"},"Card_Count_per_eNB":{"headerName":"Card_Count_per_eNB","headerValue":"1"},"Deployment":{"headerName":"Deployment","headerValue":"Open CPRI"},"RRH_Code":{"headerName":"RRH_Code","headerValue":"3JR53386AA"},"Market_CLLI_Code":{"headerName":"Market_CLLI_Code","headerValue":"WSBOMAGJ_001_LTE"},"aliasName":{"headerName":"aliasName","headerValue":"059292_2;MC;"},"antennaPathDelayDL":{"headerName":"antennaPathDelayDL","headerValue":"7"},"antennaPathDelayUL":{"headerName":"antennaPathDelayUL","headerValue":"7.5"},"antennaPathDelayDL(m)":{"headerName":"antennaPathDelayDL_(m)","headerValue":"1"},"antennaPathDelayUL(m)":{"headerName":"antennaPathDelayUL_(m)","headerValue":"2"},"DAS":{"headerName":"DAS","headerValue":""},"DAS_OUTPUT_POWER":{"headerName":"DAS_OUTPUT_POWER","headerValue":""},"NB-IoT_TAC":{"headerName":"NB-IoT_TAC","headerValue":"15234"}}},{"id":9,"fileName":"UNY-NE-VZ_CIQ_Ver2.82_01282019.xlsx","eNBId":"59292","eNBName":"059292_MENDON_2_MA","sheetName":"New England CIQ","seqOrder":"1","subSheetName":null,"subSheetAliasName":null,"sheetAliasName":"CIQNewEngland","sheetId":null,"ciqMap":{"Market":{"headerName":"Market","headerValue":"New England"},"eNB_Name":{"headerName":"eNB_Name","headerValue":"059292_MENDON_2_MA"},"Samsung_eNB_ID":{"headerName":"Samsung_eNB_ID","headerValue":"59292"},"Cell_ID":{"headerName":"Cell_ID","headerValue":"3"},"TAC":{"headerName":"TAC","headerValue":"15106"},"PCI":{"headerName":"PCI","headerValue":"180"},"RACH":{"headerName":"RACH","headerValue":"110"},"BandName":{"headerName":"BandName","headerValue":"700MHz"},"Bandwidth(MHz)":{"headerName":"Bandwidth(MHz)","headerValue":"10MHz"},"EARFCN_DL":{"headerName":"EARFCN_DL","headerValue":"5230"},"EARFCN_UL":{"headerName":"EARFCN_UL","headerValue":"23230"},"Output_Power(dBm)":{"headerName":"Output_Power_(dBm)","headerValue":"47.8"},"CPRI_Port_Assignment":{"headerName":"CPRI_Port_Assignment","headerValue":"2"},"Tx_Diversity":{"headerName":"Tx_Diversity","headerValue":"2"},"Rx_Diveristy":{"headerName":"Rx_Diveristy","headerValue":"4"},"Electrical_Tilt":{"headerName":"Electrical_Tilt","headerValue":"3"},"RRH_Type":{"headerName":"RRH_Type","headerValue":"B13RRH4x30"},"Card_Count_per_eNB":{"headerName":"Card_Count_per_eNB","headerValue":"1"},"Deployment":{"headerName":"Deployment","headerValue":"Open CPRI"},"RRH_Code":{"headerName":"RRH_Code","headerValue":"3JR53386AA"},"Market_CLLI_Code":{"headerName":"Market_CLLI_Code","headerValue":"WSBOMAGJ_001_LTE"},"aliasName":{"headerName":"aliasName","headerValue":"059292_3;MC;"},"antennaPathDelayDL":{"headerName":"antennaPathDelayDL","headerValue":"7.5"},"antennaPathDelayUL":{"headerName":"antennaPathDelayUL","headerValue":"7.25"},"antennaPathDelayDL(m)":{"headerName":"antennaPathDelayDL_(m)","headerValue":"2"},"antennaPathDelayUL(m)":{"headerName":"antennaPathDelayUL_(m)","headerValue":"1"},"DAS":{"headerName":"DAS","headerValue":""},"DAS_OUTPUT_POWER":{"headerName":"DAS_OUTPUT_POWER","headerValue":""},"NB-IoT_TAC":{"headerName":"NB-IoT_TAC","headerValue":"15234"}}},{"id":10,"fileName":"UNY-NE-VZ_CIQ_Ver2.82_01282019.xlsx","eNBId":"59292","eNBName":"059292_MENDON_2_MA","sheetName":"New England CIQ","seqOrder":"1","subSheetName":null,"subSheetAliasName":null,"sheetAliasName":"CIQNewEngland","sheetId":null,"ciqMap":{"Market":{"headerName":"Market","headerValue":"New England"},"eNB_Name":{"headerName":"eNB_Name","headerValue":"059292_MENDON_2_MA"},"Samsung_eNB_ID":{"headerName":"Samsung_eNB_ID","headerValue":"59292"},"Cell_ID":{"headerName":"Cell_ID","headerValue":"12"},"TAC":{"headerName":"TAC","headerValue":"15106"},"PCI":{"headerName":"PCI","headerValue":"182"},"RACH":{"headerName":"RACH","headerValue":"110"},"BandName":{"headerName":"BandName","headerValue":"AWS-1"},"Bandwidth(MHz)":{"headerName":"Bandwidth(MHz)","headerValue":"20MHz"},"EARFCN_DL":{"headerName":"EARFCN_DL","headerValue":"2050"},"EARFCN_UL":{"headerName":"EARFCN_UL","headerValue":"20050"},"Output_Power(dBm)":{"headerName":"Output_Power_(dBm)","headerValue":"47.8"},"CPRI_Port_Assignment":{"headerName":"CPRI_Port_Assignment","headerValue":"3"},"Tx_Diversity":{"headerName":"Tx_Diversity","headerValue":"2"},"Rx_Diveristy":{"headerName":"Rx_Diveristy","headerValue":"4"},"Electrical_Tilt":{"headerName":"Electrical_Tilt","headerValue":"0"},"RRH_Type":{"headerName":"RRH_Type","headerValue":"B4_RRH2x60-4R_L35"},"Card_Count_per_eNB":{"headerName":"Card_Count_per_eNB","headerValue":"1"},"Deployment":{"headerName":"Deployment","headerValue":"Open CPRI"},"RRH_Code":{"headerName":"RRH_Code","headerValue":"3JR52709AA"},"Market_CLLI_Code":{"headerName":"Market_CLLI_Code","headerValue":"WSBOMAGJ_001_LTE"},"aliasName":{"headerName":"aliasName","headerValue":"059292_1_2;MC;"},"antennaPathDelayDL":{"headerName":"antennaPathDelayDL","headerValue":"7"},"antennaPathDelayUL":{"headerName":"antennaPathDelayUL","headerValue":"7"},"antennaPathDelayDL(m)":{"headerName":"antennaPathDelayDL_(m)","headerValue":"1"},"antennaPathDelayUL(m)":{"headerName":"antennaPathDelayUL_(m)","headerValue":"1"},"DAS":{"headerName":"DAS","headerValue":""},"DAS_OUTPUT_POWER":{"headerName":"DAS_OUTPUT_POWER","headerValue":""},"NB-IoT_TAC":{"headerName":"NB-IoT_TAC","headerValue":""}}}]},"sessionId":"1d1333dc","serviceToken":"66366","status":"SUCCESS"}');
                  }else{
                  this.tableData =JSON.parse('{"pageCount":1,"SheetDisplayDetails":{"count":1,"list":[{"id":41793,"fileName":"UNY-NE-VZ_CIQ_Ver2.91_04012019.xlsx","eNBId":null,"eNBName":null,"sheetName":"MME IP\'S","seqOrder":"3","subSheetName":"UNY","subSheetAliasName":"MMEIPS_UNY","sheetAliasName":"MMEIPS","sheetId":1,"ciqMap":{"MME":{"headerName":"MME","headerValue":"me 070"},"IP_Address":{"headerName":"IP_Address","headerValue":"2001:4888:2011:5001:01b1:028a:0000:0000"}}},{"id":41794,"fileName":"UNY-NE-VZ_CIQ_Ver2.91_04012019.xlsx","eNBId":null,"eNBName":null,"sheetName":"MME IP\'S","seqOrder":"3","subSheetName":"UNY","subSheetAliasName":"MMEIPS_UNY","sheetAliasName":"MMEIPS","sheetId":2,"ciqMap":{"MME":{"headerName":"MME","headerValue":"me 071"},"IP_Address":{"headerName":"IP_Address","headerValue":"2001:4888:2011:6001:01b4:028a:0000:0000"}}},{"id":41795,"fileName":"UNY-NE-VZ_CIQ_Ver2.91_04012019.xlsx","eNBId":null,"eNBName":null,"sheetName":"MME IP\'S","seqOrder":"3","subSheetName":"UNY","subSheetAliasName":"MMEIPS_UNY","sheetAliasName":"MMEIPS","sheetId":3,"ciqMap":{"MME":{"headerName":"MME","headerValue":"me 072"},"IP_Address":{"headerName":"IP_Address","headerValue":"2001:4888:2011:5001:01b1:028a:0000:0001"}}},{"id":41796,"fileName":"UNY-NE-VZ_CIQ_Ver2.91_04012019.xlsx","eNBId":null,"eNBName":null,"sheetName":"MME IP\'S","seqOrder":"3","subSheetName":"UNY","subSheetAliasName":"MMEIPS_UNY","sheetAliasName":"MMEIPS","sheetId":4,"ciqMap":{"MME":{"headerName":"MME","headerValue":"me 073"},"IP_Address":{"headerName":"IP_Address","headerValue":"2001:4888:2011:6001:01b4:028a:0000:0001"}}},{"id":41797,"fileName":"UNY-NE-VZ_CIQ_Ver2.91_04012019.xlsx","eNBId":null,"eNBName":null,"sheetName":"MME IP\'S","seqOrder":"3","subSheetName":"UNY","subSheetAliasName":"MMEIPS_UNY","sheetAliasName":"MMEIPS","sheetId":5,"ciqMap":{"MME":{"headerName":"MME","headerValue":""},"IP_Address":{"headerName":"IP_Address","headerValue":"2001:4888:2011:5092:01b1:028a:0000:0002"}}},{"id":41798,"fileName":"UNY-NE-VZ_CIQ_Ver2.91_04012019.xlsx","eNBId":null,"eNBName":null,"sheetName":"MME IP\'S","seqOrder":"3","subSheetName":"UNY","subSheetAliasName":"MMEIPS_UNY","sheetAliasName":"MMEIPS","sheetId":6,"ciqMap":{"MME":{"headerName":"MME","headerValue":""},"IP_Address":{"headerName":"IP_Address","headerValue":"2001:4888:2011:5092:01b1:028a:0000:0022"}}},{"id":41799,"fileName":"UNY-NE-VZ_CIQ_Ver2.91_04012019.xlsx","eNBId":null,"eNBName":null,"sheetName":"MME IP\'S","seqOrder":"3","subSheetName":"UNY","subSheetAliasName":"MMEIPS_UNY","sheetAliasName":"MMEIPS","sheetId":7,"ciqMap":{"MME":{"headerName":"MME","headerValue":""},"IP_Address":{"headerName":"IP_Address","headerValue":"2001:4888:2011:6092:01b4:028a:0000:0002"}}},{"id":41800,"fileName":"UNY-NE-VZ_CIQ_Ver2.91_04012019.xlsx","eNBId":null,"eNBName":null,"sheetName":"MME IP\'S","seqOrder":"3","subSheetName":"UNY","subSheetAliasName":"MMEIPS_UNY","sheetAliasName":"MMEIPS","sheetId":8,"ciqMap":{"MME":{"headerName":"MME","headerValue":""},"IP_Address":{"headerName":"IP_Address","headerValue":"2001:4888:2011:6092:01b4:028a:0000:0022"}}}]},"sessionId":"b4ab1ab6","serviceToken":"72696","status":"SUCCESS"}');
                  }
                                
                
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

                      
                      if(this.tableData.SheetDisplayDetails.list.length == 0){
                      
                        this.tableShowHide = false;
                        this.noDataVisibility = true;
                      }else{
                        this.ciqTableData = this.tableData.SheetDisplayDetails.list;
                        console.log(this.objectKeys(this.ciqTableData[0].ciqMap).length);
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
                          $(".scrollBody").css("max-height",(this.tableDataHeight - 10) + "px");
                          
                          },0);   
                        
                      }
                  
                }, 1000); */
              
                //Please Comment while checkIn
        });
   }

   /*
   * Used to serach by ciq name and eNodeB for display the eNodeB menuList
   * @param : event, serachBy
   * @retun : null
   */
   getDeatilsByEnb(event, serachBy, nes){
     if (this.ciqNEFileDetails != "" && this.ciqNEFileDetails != undefined && this.ciqNEFileDetails.ciqFileName) {
       this.validationData.rules.ciqName.required = false;

     } else {
       this.validationData.rules.ciqName.required = true;

     }

     // TO get the searched data
     if (this.nes) {
       this.validationData.rules.nes.required = false;

     } else {
       this.validationData.rules.nes.required = true;
     }
     validator.performValidation(event, this.validationData, "search");     
    this.tableShowHide = false;
    this.treeShowHide = false;
    this.eNodeBTableShowHide = false;
    this.noDataVisibility = false;
    this.noDataVisibilityENodeB = true;
    this.serachBy = serachBy;
    this.fileName = this.ciqNEFileDetails.ciqFileName;
    this.fileId = this.ciqNEFileDetails.id;
    this.enbName = this.nes.eNBName;
    this.enbId = this.nes.eNBId;
    /* this.selectedLsmVersion = event.target.parentNode.parentNode.parentNode.parentNode.querySelector("#ciqName").options[event.target.parentNode.parentNode.parentNode.parentNode.querySelector("#ciqName").selectedIndex].getAttribute('data-lsmVersion');
    this.selectedNetworkType = event.target.parentNode.parentNode.parentNode.parentNode.querySelector("#ciqName").options[event.target.parentNode.parentNode.parentNode.parentNode.querySelector("#ciqName").selectedIndex].getAttribute('data-networkType');
    this.selectedNetworkTypeId = event.target.parentNode.parentNode.parentNode.parentNode.querySelector("#ciqName").options[event.target.parentNode.parentNode.parentNode.parentNode.querySelector("#ciqName").selectedIndex].getAttribute('data-networkTypeId'); */

    this.searchStatus = "search";
    
        setTimeout(() => {
            if (this.isValidForm(event)) {
                this.showLoader = true;
                  this.getAllEnbMenuList();
                }
        }, 0);

    }

   /*
   * Used to get the selected EnodeB details as per ciq file and eNodeB selection
   * @param : null
   * @retun : null
   */
   getAllEnbMenuList(){
        let eNBList = [];
        // for(let itm of this.nes){
          let eNB = {eNBId:parseInt(this.nes.eNBId) ,eNBName:this.nes.eNBName};
          eNBList.push(eNB);  
        // }
        this.dropdownSettings = {
            singleSelection: true,
            idField: 'item_id',
            textField: 'item_text',
            itemsShowLimit: 1,
            allowSearchFilter: true,
            searchPlaceholderText:"Search",
            clearSearchFilter:true,
            maxHeight: 300
        };
        this.showLoader = true;

        this.ranconfigService.getDeatilsByEnb(this.fileName, this.fileId, this.enbName, this.enbId, this.sharedService.createServiceToken())
             .subscribe(
              data => {
                  setTimeout(() => {
                    let jsonStatue = data.json();

                    this.enbMenuListDetails = data.json();
                    this.showLoader = false;
                        if(jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User"){
                       
                         this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                       
                        } else {

                          if( this.sharedService.isvalidServiceToken( parseInt(data.json().serviceToken,10) ) || data.json().sessionId == "408"){
                            if(jsonStatue.status == "SUCCESS"){
                              this.enbMenuListDetails = jsonStatue;
                              if(this.enbMenuListDetails.enbMenuList.length == 0){
                                this.treeShowHide = false;
                              }else{
                                 this.treeShowHide = true;
                                 this.getEnodeBDetails(0,this.enbMenuListDetails.enbMenuList[0]);
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
                  this.enbMenuListDetails = {"enbMenuList":["SYS_INFO","MARKET_INFO","GPS","BANDWIDTH"],"sessionId":"2c42db62","serviceToken":"58339","status":"SUCCESS"};
                    if(this.enbMenuListDetails.sessionId == "408" || this.enbMenuListDetails.status == "Invalid User"){
                         this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                        }                      
                      if(this.enbMenuListDetails.enbMenuList.length == 0){
                        this.treeShowHide = false;
                      }else{
                        this.treeShowHide = true;
                        this.getEnodeBDetails(0,this.enbMenuListDetails.enbMenuList[0]);
                        //this.noDataVisibilityENodeB = true;
                        setTimeout(() => {
                          let enodeBMenuListHeight = $(".page-wrapper").height() + $(".mainHead").height() + $(".nav-pills").height() + $("#formWrapper").height()+22;
                          $("#enodeBMenuList").css("height", enodeBMenuListHeight + "px");
                        }, 0);                        
                      }                  
                }, 1000); */
              
                //Please Comment while checkIn
        });
   }

   /*
   * Used to get the All EnodeB Menu List details
   * @param : null
   * @retun : null
   */
   
   getEnodeBDetails(index, menuName){
    this.menuName = menuName;
     /* $(".enodeBMenuListData").css("background-color", "#dce2e6")
      $(".enodeBMenuListData").css("color", "#0a0a0a")
       $(event.target).css("background-color", "#5161AC")
      $(event.target).css("color", "#fff") */

      this.ciqSheetHighlight = index;
      
      this.currentPage = 1;
      this.totalPages = 1;
      this.TableRowLength = 10;
      this.pageSize = 10;
       let paginationDetails = {
            "count": this.TableRowLength,
            "page": this.currentPage
        };
      this.paginationDetails = paginationDetails;  
        setTimeout(() => {
            this.getAllEnodeBDetails()
        }, 0);

   }
   
   getAllEnodeBDetails(){
    this.showLoader = true;
    this.eNodeBTableShowHide = false;
    this.ranconfigService.getDeatilsOfEnodeList(this.menuName, this.fileName, this.fileId, this.enbName, this.enbId, this.paginationDetails, this.sharedService.createServiceToken())
             .subscribe(
              data => {
                  setTimeout(() => {
                    let jsonStatue = data.json();

                    this.eNodeMapData = data.json();
                    this.showLoader = false;
                        if(jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User"){
                       
                         this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                       
                        } else {

                          if( this.sharedService.isvalidServiceToken( parseInt(data.json().serviceToken,10) ) || data.json().sessionId == "408"){
                            if(jsonStatue.status == "SUCCESS"){
                              this.eNodeMapData = jsonStatue;
                              this.totalPages = this.eNodeMapData.pageCount;
                              let pageCount = [];
                              for (var i = 1; i <= this.eNodeMapData.pageCount; i++) {
                                  pageCount.push(i);
                              }
                              this.pageRenge = pageCount;
                              this.pager = this.getPager(this.totalPages*this.pageSize, this.currentPage, this.pageRenge);

                              if(this.eNodeMapData.eNodeMapDetails.length == 0){
                                this.eNodeBTableShowHide = false;
                                this.noDataVisibilityENodeB = true;
                              }else{
                                 this.eNodeBTableShowHide = true;
                                 this.noDataVisibilityENodeB = false;
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
                /* setTimeout(() => {
                  this.showLoader = false;
                  //NoData
                  //this.eNodeMapData = {"sessionId":"f650973e","serviceToken":"70547","status":"SUCCESS","pageCount":"2","eNodeMapDetails":[]};
                  // DATA
                  //this.eNodeMapData = {"sessionId":"ee0b16dd","serviceToken":"61041","eNodeMapDetails":[{"id":1,"fileName":"UNY-NE-VZ_CIQ_Ver2.82_01282019Multiple.xlsx","eNBId":"56001","eNBName":"056001_BOSTON_CONVENTION_CTR_DAS2","sheetName":"New England CIQ","seqOrder":"1","ciqMap":{"Market":{"headerName":"Market","headerValue":"New England"},"Market_CLLI_Code":{"headerName":"Market_CLLI_Code","headerValue":"WRXBMAWA_001_LTE"}},"subSheetName":null,"subSheetAliasName":null,"sheetAliasName":"CIQNewEngland"},{"id":2,"fileName":"UNY-NE-VZ_CIQ_Ver2.82_01282019Multiple.xlsx","eNBId":"56001","eNBName":"056001_BOSTON_CONVENTION_CTR_DAS2","sheetName":"New England CIQ","seqOrder":"1","ciqMap":{"Market":{"headerName":"Market","headerValue":"New England"},"Market_CLLI_Code":{"headerName":"Market_CLLI_Code","headerValue":"WRXBMAWA_001_LTE"}},"subSheetName":null,"subSheetAliasName":null,"sheetAliasName":"CIQNewEngland"},{"id":3,"fileName":"UNY-NE-VZ_CIQ_Ver2.82_01282019Multiple.xlsx","eNBId":"56001","eNBName":"056001_BOSTON_CONVENTION_CTR_DAS2","sheetName":"New England CIQ","seqOrder":"1","ciqMap":{"Market":{"headerName":"Market","headerValue":"New England"},"Market_CLLI_Code":{"headerName":"Market_CLLI_Code","headerValue":"WRXBMAWA_001_LTE"}},"subSheetName":null,"subSheetAliasName":null,"sheetAliasName":"CIQNewEngland"},{"id":4,"fileName":"UNY-NE-VZ_CIQ_Ver2.82_01282019Multiple.xlsx","eNBId":"56001","eNBName":"056001_BOSTON_CONVENTION_CTR_DAS2","sheetName":"New England CIQ","seqOrder":"1","ciqMap":{"Market":{"headerName":"Market","headerValue":"New England"},"Market_CLLI_Code":{"headerName":"Market_CLLI_Code","headerValue":"WRXBMAWA_001_LTE"}},"subSheetName":null,"subSheetAliasName":null,"sheetAliasName":"CIQNewEngland"},{"id":5,"fileName":"UNY-NE-VZ_CIQ_Ver2.82_01282019Multiple.xlsx","eNBId":"56001","eNBName":"056001_BOSTON_CONVENTION_CTR_DAS2","sheetName":"New England CIQ","seqOrder":"1","ciqMap":{"Market":{"headerName":"Market","headerValue":"New England"},"Market_CLLI_Code":{"headerName":"Market_CLLI_Code","headerValue":"WRXBMAWA_001_LTE"}},"subSheetName":null,"subSheetAliasName":null,"sheetAliasName":"CIQNewEngland"},{"id":6,"fileName":"UNY-NE-VZ_CIQ_Ver2.82_01282019Multiple.xlsx","eNBId":"56001","eNBName":"056001_BOSTON_CONVENTION_CTR_DAS2","sheetName":"New England CIQ","seqOrder":"1","ciqMap":{"Market":{"headerName":"Market","headerValue":"New England"},"Market_CLLI_Code":{"headerName":"Market_CLLI_Code","headerValue":"WRXBMAWA_001_LTE"}},"subSheetName":null,"subSheetAliasName":null,"sheetAliasName":"CIQNewEngland"},{"id":7,"fileName":"UNY-NE-VZ_CIQ_Ver2.82_01282019Multiple.xlsx","eNBId":"56001","eNBName":"056001_BOSTON_CONVENTION_CTR_DAS2","sheetName":"New England CIQ","seqOrder":"1","ciqMap":{"Market":{"headerName":"Market","headerValue":"New England"},"Market_CLLI_Code":{"headerName":"Market_CLLI_Code","headerValue":"WRXBMAWA_001_LTE"}},"subSheetName":null,"subSheetAliasName":null,"sheetAliasName":"CIQNewEngland"},{"id":8,"fileName":"UNY-NE-VZ_CIQ_Ver2.82_01282019Multiple.xlsx","eNBId":"56001","eNBName":"056001_BOSTON_CONVENTION_CTR_DAS2","sheetName":"New England CIQ","seqOrder":"1","ciqMap":{"Market":{"headerName":"Market","headerValue":"New England"},"Market_CLLI_Code":{"headerName":"Market_CLLI_Code","headerValue":"WRXBMAWA_001_LTE"}},"subSheetName":null,"subSheetAliasName":null,"sheetAliasName":"CIQNewEngland"},{"id":9,"fileName":"UNY-NE-VZ_CIQ_Ver2.82_01282019Multiple.xlsx","eNBId":"56001","eNBName":"056001_BOSTON_CONVENTION_CTR_DAS2","sheetName":"New England CIQ","seqOrder":"1","ciqMap":{"Market":{"headerName":"Market","headerValue":"New England"},"Market_CLLI_Code":{"headerName":"Market_CLLI_Code","headerValue":"WRXBMAWA_001_LTE"}},"subSheetName":null,"subSheetAliasName":null,"sheetAliasName":"CIQNewEngland"}],"pageCount":2,"status":"SUCCESS"};
                  this.eNodeMapData = {"eNodeMapDetails":[{"id":1,"fileName":"CH52XC108_517945_CLWR.xlsx","eNBId":"517945","eNBName":"CHCJILONBBULTE0517945","sheetName":"FDD_TDD","seqOrder":"1","subSheetName":null,"subSheetAliasName":null,"sheetAliasName":"FDD_TDD","sheetId":1,"ciqMap":{"Latitude":{"headerName":"Latitude","headerValue":"41.84975"},"Latitude_NS":{"headerName":"Latitude_NS","headerValue":"N 041:50:59.100"},"Longitude":{"headerName":"Longitude","headerValue":"-87.67306"},"Longitude_WS":{"headerName":"Longitude_WS","headerValue":"W 087:40:23.010"}}},{"id":2,"fileName":"CH52XC108_517945_CLWR.xlsx","eNBId":"517945","eNBName":"CHCJILONBBULTE0517945","sheetName":"FDD_TDD","seqOrder":"1","subSheetName":null,"subSheetAliasName":null,"sheetAliasName":"FDD_TDD","sheetId":2,"ciqMap":{"Latitude":{"headerName":"Latitude","headerValue":"41.84975"},"Latitude_NS":{"headerName":"Latitude_NS","headerValue":"N 041:50:59.100"},"Longitude":{"headerName":"Longitude","headerValue":"-87.67306"},"Longitude_WS":{"headerName":"Longitude_WS","headerValue":"W 087:40:23.010"}}},{"id":3,"fileName":"CH52XC108_517945_CLWR.xlsx","eNBId":"517945","eNBName":"CHCJILONBBULTE0517945","sheetName":"FDD_TDD","seqOrder":"1","subSheetName":null,"subSheetAliasName":null,"sheetAliasName":"FDD_TDD","sheetId":3,"ciqMap":{"Latitude":{"headerName":"Latitude","headerValue":"41.84975"},"Latitude_NS":{"headerName":"Latitude_NS","headerValue":"N 041:50:59.100"},"Longitude":{"headerName":"Longitude","headerValue":"-87.67306"},"Longitude_WS":{"headerName":"Longitude_WS","headerValue":"W 087:40:23.010"}}},{"id":4,"fileName":"CH52XC108_517945_CLWR.xlsx","eNBId":"517945","eNBName":"CHCJILONBBULTE0517945","sheetName":"FDD_TDD","seqOrder":"1","subSheetName":null,"subSheetAliasName":null,"sheetAliasName":"FDD_TDD","sheetId":4,"ciqMap":{"Latitude":{"headerName":"Latitude","headerValue":"41.84975"},"Latitude_NS":{"headerName":"Latitude_NS","headerValue":"N 041:50:59.100"},"Longitude":{"headerName":"Longitude","headerValue":"-87.67306"},"Longitude_WS":{"headerName":"Longitude_WS","headerValue":"W 087:40:23.010"}}},{"id":5,"fileName":"CH52XC108_517945_CLWR.xlsx","eNBId":"517945","eNBName":"CHCJILONBBULTE0517945","sheetName":"FDD_TDD","seqOrder":"1","subSheetName":null,"subSheetAliasName":null,"sheetAliasName":"FDD_TDD","sheetId":5,"ciqMap":{"Latitude":{"headerName":"Latitude","headerValue":"41.84975"},"Latitude_NS":{"headerName":"Latitude_NS","headerValue":"N 041:50:59.100"},"Longitude":{"headerName":"Longitude","headerValue":"-87.67306"},"Longitude_WS":{"headerName":"Longitude_WS","headerValue":"W 087:40:23.010"}}},{"id":6,"fileName":"CH52XC108_517945_CLWR.xlsx","eNBId":"517945","eNBName":"CHCJILONBBULTE0517945","sheetName":"FDD_TDD","seqOrder":"1","subSheetName":null,"subSheetAliasName":null,"sheetAliasName":"FDD_TDD","sheetId":6,"ciqMap":{"Latitude":{"headerName":"Latitude","headerValue":"41.84975"},"Latitude_NS":{"headerName":"Latitude_NS","headerValue":"N 041:50:59.100"},"Longitude":{"headerName":"Longitude","headerValue":"-87.67306"},"Longitude_WS":{"headerName":"Longitude_WS","headerValue":"W 087:40:23.010"}}},{"id":7,"fileName":"CH52XC108_517945_CLWR.xlsx","eNBId":"517945","eNBName":"CHCJILONBBULTE0517945","sheetName":"FDD_TDD","seqOrder":"1","subSheetName":null,"subSheetAliasName":null,"sheetAliasName":"FDD_TDD","sheetId":7,"ciqMap":{"Latitude":{"headerName":"Latitude","headerValue":"41.84975"},"Latitude_NS":{"headerName":"Latitude_NS","headerValue":"N 041:50:59.100"},"Longitude":{"headerName":"Longitude","headerValue":"-87.67306"},"Longitude_WS":{"headerName":"Longitude_WS","headerValue":"W 087:40:23.010"}}},{"id":8,"fileName":"CH52XC108_517945_CLWR.xlsx","eNBId":"517945","eNBName":"CHCJILONBBULTE0517945","sheetName":"FDD_TDD","seqOrder":"1","subSheetName":null,"subSheetAliasName":null,"sheetAliasName":"FDD_TDD","sheetId":8,"ciqMap":{"Latitude":{"headerName":"Latitude","headerValue":"41.84975"},"Latitude_NS":{"headerName":"Latitude_NS","headerValue":"N 041:50:59.100"},"Longitude":{"headerName":"Longitude","headerValue":"-87.67306"},"Longitude_WS":{"headerName":"Longitude_WS","headerValue":"W 087:40:23.010"}}},{"id":9,"fileName":"CH52XC108_517945_CLWR.xlsx","eNBId":"517945","eNBName":"CHCJILONBBULTE0517945","sheetName":"FDD_TDD","seqOrder":"1","subSheetName":null,"subSheetAliasName":null,"sheetAliasName":"FDD_TDD","sheetId":9,"ciqMap":{"Latitude":{"headerName":"Latitude","headerValue":"41.84975"},"Latitude_NS":{"headerName":"Latitude_NS","headerValue":"N 041:50:59.100"},"Longitude":{"headerName":"Longitude","headerValue":"-87.67306"},"Longitude_WS":{"headerName":"Longitude_WS","headerValue":"W 087:40:23.010"}}},{"id":10,"fileName":"CH52XC108_517945_CLWR.xlsx","eNBId":"517945","eNBName":"CHCJILONBBULTE0517945","sheetName":"FDD_TDD","seqOrder":"1","subSheetName":null,"subSheetAliasName":null,"sheetAliasName":"FDD_TDD","sheetId":1,"ciqMap":{"Latitude":{"headerName":"Latitude","headerValue":"41.84975"},"Latitude_NS":{"headerName":"Latitude_NS","headerValue":"N 041:50:59.100"},"Longitude":{"headerName":"Longitude","headerValue":"-87.67306"},"Longitude_WS":{"headerName":"Longitude_WS","headerValue":"W 087:40:23.010"}}}],"pageCount":2,"sessionId":"3ba7d2da","serviceToken":"72241","status":"SUCCESS"};
                    if(this.eNodeMapData.sessionId == "408" || this.eNodeMapData.status == "Invalid User"){
                         this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                        }
                      this.totalPages = this.eNodeMapData.pageCount;
                      let pageCount = [];
                      for (var i = 1; i <= this.eNodeMapData.pageCount; i++) {
                          pageCount.push(i);
                      }
                      this.pageRenge = pageCount;
                      this.pager = this.getPager(this.totalPages*this.pageSize, this.currentPage, this.pageRenge);

                      
                      if(this.eNodeMapData.eNodeMapDetails.length == 0){
                        this.eNodeBTableShowHide = false;
                        this.noDataVisibilityENodeB = true;
                      }else{
                        this.eNodeBTableShowHide = true;
                        this.noDataVisibilityENodeB = false;
                        setTimeout(() => {
                            let tableWidth = document.getElementById('uploadDetails').scrollWidth;
                          
                            $(".scrollBody table").css("min-width",(tableWidth) + "px");
                            $(".scrollHead table").css("width", tableWidth + "px");
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
    
   /*
   * Used to edit the eNodeB details
   * @param : null
   * @retun : null
   */
   
  editRow(event, key, index) {   
    
    let editState : any = event.target,
        total = $(event.target).parents("tr").find("td"),
        inputData = '';
    this.editSaveStage = editState.className;
    if(editState.className == "editRow" ){
        this.tableEdit = true;        
        $("#systemMgrData").find("input").remove()
        $("#systemMgrData").find("br").remove()
        $.each(total, function(key, value) {
            if ((key !== total.length)) {
                //$(value).html('<input class="form-control form-control-ciq form-control-ciqInput" id="'+$(value).find("div").attr("id")+'" value="'+$(value).find("div").text()+'"/>')
                if($(value).find("div").attr("id") == "rowId"){
                  if($(value).find("div").text() == ""){
                  inputData = '<br><input class="form-control form-control-mini"  id="'+$(value).find("div").attr("id")+'" value="'+$(value).find("div").text()+'" disabled="disabled"/>';
                  }else{
                    inputData = '<input class="form-control form-control-mini"  id="'+$(value).find("div").attr("id")+'" value="'+$(value).find("div").text()+'" disabled="disabled"/>';
                  }
                }
                else if(($(value).find("div").attr("id") == "nodeName") || ($(value).find("div").attr("id") == "eNB_ID") || ($(value).find("div").attr("id") == "eNodeB_Name")){
                    inputData = '<input class="form-control form-control-ciq form-control-ciqInput" data-parentkey="'+$(value).find("div").attr("data-parentkey")+'" id="'+$(value).find("div").attr("id")+'" value="'+$(value).find("div").text()+'" disabled="disabled"/>';
                }else{
                    if($(value).find("div").text() == ""){
                     inputData = '<br><input class="form-control form-control-ciq form-control-ciqInput" data-parentkey="'+$(value).find("div").attr("data-parentkey")+'" id="'+$(value).find("div").attr("id")+'" value="'+$(value).find("div").text()+'"/>';  
                    }else{
                      inputData = '<input class="form-control form-control-ciq form-control-ciqInput" data-parentkey="'+$(value).find("div").attr("data-parentkey")+'" id="'+$(value).find("div").attr("id")+'" value="'+$(value).find("div").text()+'"/>';
                    }
                }
                $(value).append(inputData);
            }
        })
        $(".saveRow").attr("class","editRow");
        if( editState.className != "editRowDisabled"){ //enable click only if it is enabled
          editState.className = "saveRow";
          
          if(this.searchByCiqBlock == true){
            editState.parentNode.querySelector(".deleteRow").className = "cancelRow";
          }else{
            editState.parentNode.querySelector(".deleteRowDisabled").className = "cancelRow";
          }
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
        let ciqMap = {},
            ciqDetails = {},
            ciqAuditDetails ={};
      $.each(total, function (key, value) {
        if ($(value).find("input").attr("id") == "rowId") {
          //No Need to send Row ID . Row ID is used for displaying 
        }else if ($(value).find("input").attr("id") != undefined) {
          ciqMap[$(value).find("input").attr("data-parentkey")] = { "headerName": $(value).find("input").attr("id"), "headerValue": $(value).find("input").val() }

        }

      })
        
        if(this.searchByCiqBlock == true){
         ciqDetails = {"ciqMap":ciqMap,"id":key.id,"fileName":key.fileName,"sheetName":key.sheetName,"eNBId":key.eNBId, "eNBName":key.eNBName,"subSheetName":key.subSheetName,"subSheetAliasName":key.subSheetAliasName,"sheetAliasName":key.sheetAliasName,"sheetId":key.sheetId};
         ciqAuditDetails = this.ciqFileDetails;
        }
        if(this.searchByNesBlock == true){
         ciqDetails = {"ciqMap":ciqMap,"id":key.id,"fileName":key.fileName,"sheetName":key.sheetName,"eNBId":key.eNBId, "eNBName":key.eNBName,"subSheetName":key.subSheetName,"subSheetAliasName":key.subSheetAliasName,"sheetAliasName":key.sheetAliasName,"menuName":this.menuName,"sheetId":key.sheetId};
         ciqAuditDetails = this.ciqNEFileDetails;
        }
        this.ranconfigService.updateENodeBDetails(this.searchByCiqBlock, ciqDetails, this.sharedService.createServiceToken(),ciqAuditDetails,this.fileName)
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
                         this.message = "eNodeB details updated successfully !";
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
             /*  let jsonStatue: any = {"sessionId":"506db794","reason":"Updation Failed","status":"SUCCESS","serviceToken":"63524"};
               setTimeout(() => { 
                    this.showLoader = false;
                    if(jsonStatue.status == "SUCCESS"){
                     this.message = "eNodeB details updated successfully !";
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
   * Used to delete the eNodeB details
   * @param : confirmModal, id, event
   * @retun : null
   */
   
  deleteRow(confirmModal, id, event) {
      if(event.target.className == "deleteRow"){
          this.modalService.open(confirmModal,{keyboard: false, backdrop: 'static', size:'lg', windowClass:'confirm-modal'}).result.then((result) => {
          this.closeResult = `Closed with: ${result}`;
        }, (reason) => {
          this.showLoader = true;

          this.ranconfigService.deleteENodeBDetails(this.ciqFileDetails, id, this.sharedService.createServiceToken())
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
                                 this.message = "eNodeB deleted successfully!";
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
                     /*  setTimeout(() => { 
                          this.showLoader = false;
                         let jsonStatue = {"reason":null,"sessionId":"5f3732a4","serviceToken":"80356","status":"SUCCESS"};
                        if(jsonStatue.status == "SUCCESS"){
                          this.message = "eNodeB row deleted successfully!";
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
          if(this.searchByCiqBlock == true){
            event.target.className = "deleteRow";  
          }
          if(this.searchByCiqBlock == false){
            event.target.className = "deleteRowDisabled";  
          }
          event.target.previousSibling.className = "editRow";
          this.tableEdit = false;
      }
      
  }

  /*
   * Used to add the eNodeB list for perticular eNodeB 
   * @param : event, key, index
   * @retun : null
   */
   
  addRow(event, key, index) {
    let editState : any = event.target,
        total = $(event.target).parents("tr").find("td"),
        inputData = '';
    if (editState.className == "cloneRow") {
      this.tableEdit = true;
      $("#systemMgrData").find("input").remove()
      $("#systemMgrData").find("br").remove()
      $.each(total, function (key, value) {
        if ((key !== total.length - 1)) {
          //$(value).html('<input class="form-control form-control-ciq form-control-ciqInput" id="'+$(value).find("div").attr("id")+'" value="'+$(value).find("div").text()+'"/>')
          if ($(value).find("div").attr("id") == "rowId") {
            if ($(value).find("div").text() == "") {
              inputData = '<br><input class="form-control form-control-mini"  id="' + $(value).find("div").attr("id") + '" value="' + $(value).find("div").text() + '" disabled="disabled"/>';
            } else {
              inputData = '<input class="form-control form-control-mini"  id="' + $(value).find("div").attr("id") + '" value="' + $(value).find("div").text() + '" disabled="disabled"/>';
            }
          }
          else if (($(value).find("div").attr("id") == "nodeName") || ($(value).find("div").attr("id") == "eNB_ID") || ($(value).find("div").attr("id") == "eNodeB_Name")) {
            inputData = '<input class="form-control form-control-ciq form-control-ciqInput" data-parentkey="' + $(value).find("div").attr("data-parentkey") + '" id="' + $(value).find("div").attr("id") + '" value="' + $(value).find("div").text() + '" disabled="disabled"/>';
          } else {
            if ($(value).find("div").text() == "") {
              inputData = '<br><input class="form-control form-control-ciq form-control-ciqInput" data-parentkey="' + $(value).find("div").attr("data-parentkey") + '" id="' + $(value).find("div").attr("id") + '" value="' + $(value).find("div").text() + '"/>';
            } else {
              inputData = '<input class="form-control form-control-ciq form-control-ciqInput" data-parentkey="' + $(value).find("div").attr("data-parentkey") + '"  id="' + $(value).find("div").attr("id") + '" value="' + $(value).find("div").text() + '"/>';
            }
          }

          $(value).append(inputData);
        }
      })
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
      this.showLoader = true;
      let ciqMap = {},
        ciqDetails = {};
      $.each(total, function (key, value) {
        if ($(value).find("input").attr("id") == "rowId") {
          //No Need to send Row ID . Row ID is used for displaying 
        }else if ($(value).find("input").attr("id") != undefined) {
          ciqMap[$(value).find("input").attr("data-parentkey")] = { "headerName": $(value).find("input").attr("id"), "headerValue": $(value).find("input").val() }
        }
      })

      if (this.searchByCiqBlock == true) {
        ciqDetails = { "id": null, "ciqMap": ciqMap, "fileName": key.fileName, "sheetName": key.sheetName, "eNBId": key.eNBId, "eNBName": key.eNBName, "subSheetName": key.subSheetName, "subSheetAliasName": key.subSheetAliasName, "sheetAliasName": key.sheetAliasName, "sheetId": key.sheetId };
      }
      if (this.searchByNesBlock == true) {
        ciqDetails = { "id": null, "ciqMap": ciqMap, "fileName": key.fileName, "sheetName": key.sheetName, "eNBId": key.eNBId, "eNBName": key.eNBName, "subSheetName": key.subSheetName, "subSheetAliasName": key.subSheetAliasName, "sheetAliasName": key.sheetAliasName, "menuName": this.menuName, "sheetId": key.sheetId };
      }
      this.ranconfigService.createENodeBDetails(this.searchByCiqBlock, ciqDetails, this.sharedService.createServiceToken(), this.ciqFileDetails)
        .subscribe(
          data => {
            let jsonStatue = data.json();
            this.showLoader = false;

            if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {

              this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });

            } else {

              if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {
                if (jsonStatue.status == "SUCCESS") {
                  this.editableFormArray = [];
                  this.addtableFormArray = [];
                  this.message = "eNodeB details created successfully !";
                  this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                  // this.createForm = false;
                  this.tableEdit = false;
                } else {
                  this.displayModel(jsonStatue.reason, "failureIcon");
                }
              }
            }

          },
          error => {
            //Please Comment while checkIn
           /*   let jsonStatue: any = {"sessionId":"506db794","reason":"Updation Failed","status":"SUCCESS","serviceToken":"63524"};
             setTimeout(() => { 
                   this.showLoader = false;
                   if(jsonStatue.status == "SUCCESS"){
                    this.message = "eNodeB details created successfully !";
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
  
  generateFile(event){
    this.showLoader = true;
    let generateValue = {};
    if(this.searchByCiqBlock == true){
      generateValue = {
        fileName:this.fileName, fileId:this.fileId
      }
    }else{
      generateValue = {
        fileName:this.fileName, fileId:this.fileId, enbName:this.enbName, enbId:this.enbId
      }
    }
    if(this.tableEdit){
      this.showLoader = false;
      this.displayModel("Unable to Generate as Edit is in progress","failureIcon");  
    }else{
      let integrationType = this.programName == 'VZN-4G-USM-LIVE' && this.searchByNesBlock ? this.migrationStrategy : null;
    this.ranconfigService.generateCsvFile(this.searchByCiqBlock, generateValue, this.sharedService.createServiceToken(), integrationType , this.programName == "VZN-4G-USM-LIVE" ? this.supportCA : null )
        .subscribe(
            data => {
                setTimeout(() => { 
                  let jsonStatue = data.json();
                      if(jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User"){
                       this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                      } else {
                        if( this.sharedService.isvalidServiceToken( parseInt(data.json().serviceToken,10) ) || data.json().sessionId == "408"){
                          if(jsonStatue.status == "SUCCESS"){
                            this.showLoader = false;
                            this.message = "Files Generated Successfully!";
                            this.displayModel(this.message,"successIcon");  
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
            /*   setTimeout(() => { 
                this.showLoader = false;
                let jsonStatue = {"sessionId":"15f815ee","serviceToken":"51615","status":"SUCCESS","reason":"Failure"};
                  if(jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User"){
                     this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                    }else{
                  if(jsonStatue.status == "SUCCESS"){
                    this.message = "Files Generated Successfully!";
                    this.displayModel(this.message,"successIcon");  
                  }else{
                    this.showLoader = false;
                    this.displayModel(jsonStatue.reason,"failureIcon");  
                  }
                }
              }, 1000); */
              //Please Comment while checkIn
        });
      }
  }



  validateFile(event){
    this.showLoader = true;
    let validateValue = {};
    if(this.searchByCiqBlock == true){
      validateValue = {
        fileName:this.fileName, fileId:this.fileId
      }
    }else{
      validateValue = {
        fileName:this.fileName, fileId:this.fileId, enbName:this.enbName, enbId:this.enbId
      }
    }
    if(this.tableEdit){
      this.showLoader = false;
      this.displayModel("Unable to Validate as Edit is in progress","failureIcon");  
    }else{
    this.ranconfigService.validateFile(this.searchByCiqBlock, validateValue, this.sharedService.createServiceToken() )
        .subscribe(
            data => {
                setTimeout(() => { 
                  let jsonStatue = data.json();
                      if(jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User"){
                       this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                      } else {
                        if( this.sharedService.isvalidServiceToken( parseInt(data.json().serviceToken,10) ) || data.json().sessionId == "408"){
                          if(jsonStatue.status == "SUCCESS"){
                            this.showLoader = false;
                            this.message = "Files Validated Successfully!";
                            this.displayModel(this.message,"successIcon");  
                          }else{                                                      
                            this.validateDetails = jsonStatue.errorDetails;
                            setTimeout(()=>{
                              this.showLoader = false;
                             this.viewModalBlock = this.modalService.open( this.viewModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth validateView' });
                            },100);
                             
                          }
                         }   
                      }
                                    
                }, 1000);
            },
            error => {
                //Please Comment while checkIn
                /* setTimeout(() => {
                    this.showLoader = false;
                    let jsonStatue = {"reason":"Validations Fail","sessionId":"cd700fc2","serviceToken":"59817","status":"FAILED","errorDetails":[{"rowId":1,"sheetName":"New England CIQ","subSheetName":null,"cellId":"1","enbName":"057120_MILTON_2_MA","propertyName":"Tx_Diversity","errorMessage":"Tx_Diversity lessThan or equal the value of Rx_Diveristy"}]};
                    if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
                        this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
                    } else {
                        if (jsonStatue.status == "SUCCESS") {
                            this.message = "Files Validated Successfully!";
                            this.displayModel(this.message, "successIcon");
                        } else {

                            this.validateDetails = jsonStatue.errorDetails;
                            setTimeout(() => {
                                this.showLoader = false;
                                this.viewModalBlock = this.modalService.open(this.viewModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth validateView' });

                            }, 100);
                        }
                    }
                }, 1000); */
                //Please Comment while checkIn
            });
      }
  }



  /*
   * Used to close the success model 
   * @param : event, key, index
   * @retun : null
   */
  
  closeModel(){
      this.successModalBlock.close();
      //this.ngOnInit();
      if(this.searchByCiqBlock == true){        
          //this.getAllCiqDetails();  
          this.ngOnInit();          
        }
        if(this.searchByNesBlock == true){
          //this.getAllEnodeBDetails();  
          this.searchByNes();
          
        }
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
            if(this.searchByCiqBlock == true){              
             this.getAllCiqDetails();  
            }
            if(this.searchByNesBlock == true){
              this.getAllEnodeBDetails();  
            }


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
            if(this.searchByCiqBlock == true){              
              this.getAllCiqDetails();  
            }
            if(this.searchByNesBlock == true){
              this.getAllEnodeBDetails();  
            }
      
            
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

    setMenuHighlight(selectedElement) {
        this.searchTabRef.nativeElement.id = (selectedElement == "ciq") ? "activeTab" : "inactiveTab";
        this.createNewTabRef.nativeElement.id = (selectedElement == "nes") ? "activeTab" : "inactiveTab";
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
    onChangeDateNE(){
      this.errMessage = false;
      if(this.fromDate && this.toDate){
        //this.getCiqListData(this.fromDate,this.toDate); 
        this.showLoader = true;
        this.searchByNes();       
      }else{
        this.errMessage = true;
      }
    }
    closeView()
    {
      this.viewModalBlock.close();
    }
    downloadErrorData()
    {
      setTimeout(()=>{
        var ele = document.getElementById('demo-table');        
        var test = ele.innerText.toString().replace(/,/g , "  ");
        var blob = new Blob([test], {
          type: "application/octet-stream"
            });
            FileSaver.saveAs(blob, "errorDetails.xls");//errorDetails.xlsx
        
      },0);
    
    }
}
