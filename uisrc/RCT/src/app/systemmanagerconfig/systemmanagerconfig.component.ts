import { ViewContainerRef, Input, ElementRef, Renderer, Component, OnInit, ViewChild, HostListener } from '@angular/core';
import { Router} from '@angular/router';
import { NgbModal, ModalDismissReasons} from '@ng-bootstrap/ng-bootstrap';
import { SystemmanagerconfigService } from '../services/systemmanagerconfig.service';
import { SharedService } from '../services/shared.service';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { filter, map, mergeMap} from 'rxjs/operators';
import { validator } from '../validation';
import {platformBrowserDynamic} from '@angular/platform-browser-dynamic';
import * as FileSaver from 'file-saver';
import * as $ from 'jquery';
import * as _ from 'underscore';
@Component({
  selector: 'rct-systemmanagerconfig',
  templateUrl: './systemmanagerconfig.component.html',
  styleUrls: ['./systemmanagerconfig.component.scss'],
  providers: [SystemmanagerconfigService]
})

  export class SystemmanagerconfigComponent implements OnInit {
  chartShowHide :boolean = false;
  showLoader:boolean = true;
  showNoDataFound: boolean;
  editableFormArray = [];
  editableServerFormArray = [];
  tableData:any;  
  closeResult:string;
  privilegeSetting : object;
  noDataVisibility :boolean = false;
  showModelMessage: boolean = false;
  formWidth:any;
  scrollLeft:any;
  createForm: boolean = false;
  messageType: any;
  searchBlockHieght :any;
  modelData :any;
  sessionExpiredModalBlock : any; // Helps to close/open the model window
  successModalBlock : any;
  message : any;
  tableShowHide :boolean = false;
  tableDataHeight:any;
  pageCount: any; // for pagination
  currentPage: any; // for pagination
  pageSize: any; // for pagination
  totalPages: any; // for pagination
  TableRowLength: any; // for pagination
  paginationDetails: any; // for pagination
  pageRenge: any; // for pagination
  paginationDisabbled: boolean = false;
  pager: any = {}; // pager Object  
  searchCriteria: any;
  searchStatus: string;
  currentEditRow: any;
  searchBlock: boolean = false;
  serverList:any =[];
  serverData:any =[];
  serverTable:boolean =false;
  serverCreateForm:boolean = false;
  currEditScript: any = null;
  neTypeEntity:any;
  neVersionEntity:any;
  neRelVersion:any;
  programDetailsEntity:any;
  loginTypeEntity:any;
  searchNeName:any;
  searchNeType:any ="";
  searchNeVersion:any = "";
  searchLoginType:any ="";
  searchPgmName:any ="";
  searchMarketName:any;
  serverTypeList: any =[];
  neTypeList : any =[];
  loginTypeList : any =[];
  programNamesList: any =[];
  neVersionList:any =[];
  neRelVersionList:any = [];
  neNameList :any =[];
  serverType:any;
  serverLoginType:any;
  neMarketList:any=[]; 
  neMarketEntity:any=[];
  neRsIPEntity:any = "";
  validateServer :boolean = false;
  updateIp : boolean;
  pgmNameConfig:object;
  validationData : any = {
            "rules": {
                "programName": {
                    "required": true
                    
                },
                "neMarket": {
                    "required":false,
                },
                "neType": {
                    "required": true
                },
                "neName":{
                    "required": true
                },
                "neVersion": {
                    "required": false
                },
                "relVersion": {
                    "required": false
                },
                "neIp": {
                    "required": true,
                    // "pattern": /^(?:[0-9]{1,3}\.){3}[0-9]{1,3}$/
                },
                "neRsIp": {
                    "required": false,
                   
                },
                "neUserName": {
                    "required": true,
                },
                "nePassword": {
                    "required": true,
                }, 
                "cnfrmPswd": {
                    "required": true,
                    "compareField" : "#nePassword"
                },
                "loginType":{
                    "required": true 
                },
                "neUserPrompt": {
                    "required": true
                },
                "neSuperUserPrompt": {
                    "required": true
                }
            },
            "messages": {
                "programName": {
                    "required": "ProgramName is required"                    
                }, 
                "neMarket": {
                    "required": "NE MARKET Type is required",
                },
                "neType": {
                    "required": "NE Type is required"
                },
                "neName":{
                    "required": "NE Name is required"
                },
                "neVersion": {
                    "required": "NE version is required"
                },
                "relVersion": {
                    "required": "Release version is required"
                },
                "neIp": {
                    "required": "IP Address is required",
                    // "pattern":  "Invalid IP Address"
                },
                "neRsIp": {
                    "required": "RS IP Address is required"
                  
                },
                "neUserName": {
                    "required": "NE user name is required"
                },
                "nePassword": {
                    "required": "Password is required"
                },
                "cnfrmPswd": {
                    "required": "Confirm Password is required",
                    "compareField" : "Password and Confirm Password not matching"
                },
                "loginType":{
                    "required": "Login Type is required",
                },
                "neUserPrompt": {
                    "required": "User prompt is required"
                },
                "neSuperUserPrompt": {
                    "required": "Super user prompt is required"
                }
            }
          };
    serverValidations: any = {
        "rules": {
            "step": {
                "required": true,
                "customfunction":false,
                "pattern":/^[0-9]*$/
            },
            "serverType": {
                "required": true
            },
            "serverName": {
                "required": true,
                "minlength" : 3
            },
            "serverIp": {
                "required": true,
                "pattern": /^(?:[0-9]{1,3}\.){3}[0-9]{1,3}$/
            },
            "serverUserName": {
                "required": true
            },
            "serverPassword": {
                "required": true
            },
            "serverCnfrmPswd": {
                "required": true,
                "compareField": "#serverPassword"
            },
            "serverLoginType": {
                "required": true
            },
            "userPrompt": {
                "required": true
            },
            "superUserPrompt": {
                "required": true
            }
            
        },
        "messages": {
            "step": {
                "required": "Step is required",
                "customfunction":"Step should be unique",
                "pattern":"Enter Numeric Value"
            },
            "serverType": {
                "required": "Server Type is required"
            },
            "serverName": {
                "required": "Server Name is required",
                "minlength" : "min length should not be lesser than 3 characters"
            },
            "serverIp": {
                "required": "IP Address is required",
                "pattern":  "Invalid IP Address"
            },
            "serverUserName": {
                "required": "User Name is required"
            },
            "serverPassword": {
                "required": "Password is required"
            },
            "serverCnfrmPswd": {
                "required": "Confirm Password is required",
                "compareField" : "Password and Confirm Password not matching"
            },
            "serverLoginType": {
                "required": "Login Type is required"
            },
            "userPrompt": {
                "required": "User prompt is required"
            },
            "superUserPrompt": {
                "required": "Super user prompt is required"
            }
        }
    };
   
  
  // What to clone
  @ViewChild('networkConfigForm') networkConfigTemplate;

  // Where to insert the cloned content
  @ViewChild('container', {read:ViewContainerRef}) container;
  @ViewChild('searchTab') searchTabRef: ElementRef;
  @ViewChild('createNewTab') createNewTabRef: ElementRef;

  @ViewChild('confirmModal') confirmModalRef: ElementRef;
  @ViewChild('sessionExpiredModal') sessionExpiredModalRef: ElementRef;
  @ViewChild('successModal') successModalRef: ElementRef;
  @ViewChild('filePost') filePostRef: ElementRef;
  @ViewChild('searchForm') searchForm;


  
  constructor(
    private element: ElementRef,
    private renderer: Renderer,
    private router:Router,
    private modalService: NgbModal,
    private systemManagerConfigService: SystemmanagerconfigService,
    private sharedService: SharedService
    ) {}

  ngOnInit() {
    this.showLoader = true;
     //For Pagination
    this.currentPage = 1;
    this.totalPages = 1;
    this.TableRowLength = 10;
    this.pageSize = 10;
     let paginationDetails = {
          "count": this.TableRowLength,
          "page": this.currentPage
      };
    this.paginationDetails = paginationDetails;  
    this.updateIp=false;
    this.searchStatus = 'load';
    this.container.clear();
    this.createForm = false;
    this.searchBlock = true;    
    this.sharedService.userNavigation = true;//unblock user navigation
    this.editableFormArray = [];
    this.editableServerFormArray = [];
    this.clearSearchFields();
    this.setMenuHighlight("search");
    
    this.getAllLSMDetails();   
   
    
  }
  getAllLSMDetails(){
    this.showLoader = true;
    this.tableShowHide = false;
    $("#dataWrapper").find(".scrollBody").scrollLeft(0);
    this.systemManagerConfigService.getLSMDetails(this.searchStatus, this.searchCriteria,this.paginationDetails, this.sharedService.createServiceToken() )
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

                            
                            this.serverTypeList = this.tableData.serverTypeList;
                            this.neTypeList = this.tableData.neTypeList;
                            this.loginTypeList = this.tableData.loginTypeList;
                            this.programNamesList = this.tableData.programNamesList;
                            this.neMarketList = this.tableData.neMarketList;
                            this.neNameList = this.tableData.neNameList;
                            this.neMarketList=this.tableData.neMarketList;
                            // this.neVersionList = this.tableData.neversionList;
                            this.pgmNameConfig = {
                                displayKey: "programName", //if objects array passed which key to be displayed defaults to description
                                search: true, //true/false for the search functionlity defaults to false,
                                height: '200px', //height of the list so that if there are more no of items it can show a scroll defaults to auto. With auto height scroll will never appear
                                placeholder: '--Select--', // text to be displayed when no item is selected defaults to Select,
                                customComparator: () => { },// a custom function using which user wants to sort the items. default is undefined and Array.sort() will be used in that case,
                                limitTo: this.programNamesList.length, // a number thats limits the no of options displayed in the UI similar to angular's limitTo pipe
                                moreText: 'more', // text to be displayed whenmore than one items are selected like Option 1 + 5 more                    
                                noResultsFound: 'No results found!',// text to be displayed when no items are found while searching
                                searchPlaceholder: 'Search', // label thats displayed in search input,
                                searchOnKey: 'programName',// key on which search should be performed this will be selective search. if undefined this will be extensive search on all keys
                            }
                            
                            if(this.tableData.networkConfigList.length == 0){
                              this.tableShowHide = false;
                              this.noDataVisibility = true;
                            }else{
                              this.tableShowHide = true;
                               this.noDataVisibility = false;
                               setTimeout(() => { 
                                let tableWidth = document.getElementById('SystemManagerDetails').scrollWidth;
                                    $(".networkTable .scrollBody table").css("min-width",(tableWidth) + "px");
                                    $(".networkTable .scrollHead table").css("width", tableWidth + "px");

                                
                                    $(".networkTable .scrollBody").on("scroll", function (event) {
                                        $(".formEditRow .networkWrapper").css("margin-left", (event.target.scrollLeft - 5) + "px");
                                        $(".networkRow .form-control-fixed").css("right",(event.target.scrollLeft * -1) + "px");
                                        $(".networkTable .scrollHead table#SystemManagerDetails").css("margin-left",(event.target.scrollLeft * -1) + "px");
                                    });
                                    $(".networkTable .scrollBody").css("max-height",(this.tableDataHeight - 10) + "px");

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
               
                
                // this.tableData = {"networkConfigList":[],"pageCount":0,"programNamesList":[{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"admin","caretedDate":"2019-01-23T05:33:27.000+0000","status":"Active","remarks":"","networkColor":"#a7aea7"},"programName":"VZN-4G-LEGACY","status":"InActive","creationDate":"2019-02-11T09:24:41.000+0000","createdBy":"superadmin"},{"id":3,"networkTypeDetailsEntity":{"id":4,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"VZN-5G-LEGACY","status":"Active","creationDate":"2019-02-11T09:25:27.000+0000","createdBy":"superadmin"},{"id":4,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"admin","caretedDate":"2019-01-23T05:33:27.000+0000","status":"Active","remarks":"","networkColor":"#a7aea7"},"programName":"AT&T-4G-LEGACY","status":"Active","creationDate":"2019-02-11T09:25:34.000+0000","createdBy":"superadmin"},{"id":5,"networkTypeDetailsEntity":{"id":4,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"AT&T-5G-LEGACY","status":"Active","creationDate":"2019-02-11T09:25:39.000+0000","createdBy":"superadmin"},{"id":6,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"admin","caretedDate":"2019-01-23T05:33:27.000+0000","status":"Active","remarks":"","networkColor":"#a7aea7"},"programName":"SPT-4G-LEGACY","status":"Active","creationDate":"2019-02-11T09:25:44.000+0000","createdBy":"superadmin"},{"id":9,"networkTypeDetailsEntity":{"id":12,"networkType":"3G","createdBy":"admin","caretedDate":"2019-01-23T05:33:51.000+0000","status":"Active","remarks":"","networkColor":"#914152"},"programName":"TST2_3G_L","status":"Inactive","creationDate":"2019-02-11T09:26:24.000+0000","createdBy":"superadmin"},{"id":10,"networkTypeDetailsEntity":{"id":4,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"TST3-5G-MIMO","status":"Inactive","creationDate":"2019-02-11T09:34:41.000+0000","createdBy":"superadmin"},{"id":11,"networkTypeDetailsEntity":{"id":4,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"TST4-5G-MIMO","status":"Inactive","creationDate":"2019-02-11T09:37:57.000+0000","createdBy":"superadmin"},{"id":12,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"admin","caretedDate":"2019-01-23T05:33:27.000+0000","status":"Active","remarks":"","networkColor":"#a7aea7"},"programName":"TST1-4G-LEGACY-1","status":"Active","creationDate":"2019-02-12T12:46:18.000+0000","createdBy":"superadmin"}],"serverTypeList":[{"id":1,"serverType":"JUMP BOX"},{"id":2,"serverType":"SANE"}],"neTypeList":[{"id":1,"neType":"CIQ SERVER"},{"id":2,"neType":"SCRIPT SERVER"},{"id":3,"neType":"PUT SERVER"},{"id":4,"neType":"LSM"},{"id":5,"neType":"VLSM"}],"neNameList":[],"sessionId":"a58331ba","serviceToken":"65372","neversionList":[],"loginTypeList":[{"id":1,"loginType":"SSH"},{"id":2,"loginType":"FTP"},{"id":3,"loginType":"SFTP"}],"status":"SUCCESS"};
                // this.tableData = {"sessionId":"363343b2","serviceToken":"86688","networkConfigList":[{"id":1,"programDetailsEntity":{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":null,"status":"InActive","creationDate":"2019-02-11T09:24:41.000+0000","createdBy":"superadmin"},"neTypeEntity":{"id":1,"neType":"CIQ SERVER"},"neName":"Ne_1_1_1","neVersionEntity":{"id":1,"programDetailsEntity":{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":null,"status":"InActive","creationDate":"2019-02-11T09:24:41.000+0000","createdBy":"superadmin"},"neVersion":"1.2.3","status":"Active","createdBy":"superadmin","creationDate":"2019-02-15T11:02:23.000+0000"},"neIp":"10.0.0.0","neUserName":"admin","nePassword":"admin","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-02-19 12:21","status":"Active","remarks":"admin","neDetails":[{"id":1,"step":1,"serverTypeEntity":{"id":1,"serverType":"JUMP BOX"},"serverName":"S_1_1","serverIp":"10.1.1","serverUserName":"admin","serverPassword":"admin","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-02-19 12:21","path":"1.0","networkConfigEntity":null},{"id":2,"step":1,"serverTypeEntity":{"id":1,"serverType":"JUMP BOX"},"serverName":"S_1_1","serverIp":"10.1.1","serverUserName":"admin","serverPassword":"admin","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-02-19 12:21","path":"1.0","networkConfigEntity":null}]},{"id":5,"programDetailsEntity":{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":null,"status":"InActive","creationDate":"2019-02-11T09:24:41.000+0000","createdBy":"superadmin"},"neTypeEntity":{"id":1,"neType":"CIQ SERVER"},"neName":"Ne_3_5_1","neVersionEntity":{"id":1,"programDetailsEntity":{"id":3,"networkTypeDetailsEntity":{"id":4,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"VZN-5G-LEGACY","programDescription":null,"status":"Active","creationDate":"2019-02-11T09:25:27.000+0000","createdBy":"superadmin"},"neVersion":"1.2.3","status":"Active","createdBy":"superadmin","creationDate":"2019-02-15T11:02:23.000+0000"},"neIp":"10.0.0.0","neUserName":"admin","nePassword":"admin","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-02-19 12:21","status":"Active","remarks":"testing","neDetails":[{"id":5,"step":1,"serverTypeEntity":{"id":1,"serverType":"JUMP BOX"},"serverName":"Sane1_1","serverIp":"0.0.0.0","serverUserName":"admin","serverPassword":"admin","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-02-19 12:21","path":"1,2,3","networkConfigEntity":null}]},{"id":2,"programDetailsEntity":{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":null,"status":"InActive","creationDate":"2019-02-11T09:24:41.000+0000","createdBy":"superadmin"},"neTypeEntity":{"id":1,"neType":"CIQ SERVER"},"neName":"Ne_1_2_1","neVersionEntity":{"id":1,"programDetailsEntity":{"id":3,"networkTypeDetailsEntity":{"id":4,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"VZN-5G-LEGACY","programDescription":null,"status":"Active","creationDate":"2019-02-11T09:25:27.000+0000","createdBy":"superadmin"},"neVersion":"1.2.3","status":"Active","createdBy":"superadmin","creationDate":"2019-02-15T11:02:23.000+0000"},"neIp":"10.0.0.0","neUserName":"admin","nePassword":"admin","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-02-19 12:21","status":"Active","remarks":"admin","neDetails":[]},{"id":6,"programDetailsEntity":{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":null,"status":"InActive","creationDate":"2019-02-11T09:24:41.000+0000","createdBy":"superadmin"},"neTypeEntity":{"id":1,"neType":"CIQ SERVER"},"neName":"Ciq_1_1_1","neVersionEntity":{"id":1,"programDetailsEntity":{"id":3,"networkTypeDetailsEntity":{"id":4,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"VZN-5G-LEGACY","programDescription":null,"status":"Active","creationDate":"2019-02-11T09:25:27.000+0000","createdBy":"superadmin"},"neVersion":"1.2.3","status":"Active","createdBy":"superadmin","creationDate":"2019-02-15T11:02:23.000+0000"},"neIp":"10.6.7.7","neUserName":"admin","nePassword":"admin","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-02-19 12:21","status":"Active","remarks":"testing","neDetails":[{"id":6,"step":2,"serverTypeEntity":{"id":1,"serverType":"JUMP BOX"},"serverName":"Jump1_1","serverIp":"0.1.1.1","serverUserName":"admin","serverPassword":"admin","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-02-19 12:21","path":"7,8,9","networkConfigEntity":null}]},{"id":3,"programDetailsEntity":{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":null,"status":"InActive","creationDate":"2019-02-11T09:24:41.000+0000","createdBy":"superadmin"},"neTypeEntity":{"id":1,"neType":"CIQ SERVER"},"neName":"Ne_1_3_1","neVersionEntity":{"id":1,"programDetailsEntity":{"id":3,"networkTypeDetailsEntity":{"id":4,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"VZN-5G-LEGACY","programDescription":null,"status":"Active","creationDate":"2019-02-11T09:25:27.000+0000","createdBy":"superadmin"},"neVersion":"1.2.3","status":"Active","createdBy":"superadmin","creationDate":"2019-02-15T11:02:23.000+0000"},"neIp":"10.0.0.0","neUserName":"admin","nePassword":"admin","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-02-19 12:21","status":"Active","remarks":"admin","neDetails":[{"id":3,"step":2,"serverTypeEntity":{"id":1,"serverType":"JUMP BOX"},"serverName":"S_1_1","serverIp":"10.1.1","serverUserName":"admin","serverPassword":"admin","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-02-19 12:21","path":"1.0","networkConfigEntity":null}]},{"id":4,"programDetailsEntity":{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":null,"status":"InActive","creationDate":"2019-02-11T09:24:41.000+0000","createdBy":"superadmin"},"neTypeEntity":{"id":1,"neType":"CIQ SERVER"},"neName":"Ne_1_4_1","neVersionEntity":{"id":1,"programDetailsEntity":{"id":3,"networkTypeDetailsEntity":{"id":4,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"VZN-5G-LEGACY","programDescription":null,"status":"Active","creationDate":"2019-02-11T09:25:27.000+0000","createdBy":"superadmin"},"neVersion":"1.2.3","status":"Active","createdBy":"superadmin","creationDate":"2019-02-15T11:02:23.000+0000"},"neIp":"10.0.0.0","neUserName":"admin","nePassword":"admin","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-02-19 12:21","status":"Active","remarks":"admin","neDetails":[{"id":4,"step":3,"serverTypeEntity":{"id":1,"serverType":"JUMP BOX"},"serverName":"S_1_1","serverIp":"10.1.1","serverUserName":"admin","serverPassword":"admin","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-02-19 12:21","path":"1.0","networkConfigEntity":null}]}],"pageCount":1,"programNamesList":[{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","status":"InActive","creationDate":"2019-02-11T09:24:41.000+0000","createdBy":"superadmin","programDescription":null},{"id":3,"networkTypeDetailsEntity":{"id":4,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"VZN-5G-LEGACY","status":"Active","creationDate":"2019-02-11T09:25:27.000+0000","createdBy":"superadmin","programDescription":null},{"id":4,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"AT&T-4G-LEGACY","status":"Active","creationDate":"2019-02-11T09:25:34.000+0000","createdBy":"superadmin","programDescription":null},{"id":5,"networkTypeDetailsEntity":{"id":4,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"AT&T-5G-LEGACY","status":"Active","creationDate":"2019-02-11T09:25:39.000+0000","createdBy":"superadmin","programDescription":null},{"id":6,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"SPT-4G-LEGACY","status":"Active","creationDate":"2019-02-11T09:25:44.000+0000","createdBy":"superadmin","programDescription":null},{"id":9,"networkTypeDetailsEntity":{"id":12,"networkType":"3G","createdBy":"admin","caretedDate":"2019-01-23T05:33:51.000+0000","status":"Active","remarks":"","networkColor":"#914152"},"programName":"TST2_3G_L","status":"Inactive","creationDate":"2019-02-11T09:26:24.000+0000","createdBy":"superadmin","programDescription":null},{"id":10,"networkTypeDetailsEntity":{"id":4,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"TST3-5G-MIMO","status":"Inactive","creationDate":"2019-02-11T09:34:41.000+0000","createdBy":"superadmin","programDescription":null},{"id":11,"networkTypeDetailsEntity":{"id":4,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"TST4-5G-MIMO","status":"Inactive","creationDate":"2019-02-11T09:37:57.000+0000","createdBy":"superadmin","programDescription":null},{"id":12,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"TST1-4G-LEGACY-1","status":"Active","creationDate":"2019-02-12T12:46:18.000+0000","createdBy":"superadmin","programDescription":null},{"id":13,"networkTypeDetailsEntity":{"id":10,"networkType":"6G_2","createdBy":"admin","caretedDate":"2019-01-23T05:33:41.000+0000","status":"Active","remarks":"","networkColor":"#bf16e1"},"programName":"tst2-6G_2-leg","programDescription":"leg for tst2","status":"Active","creationDate":"2019-02-14T10:48:21.000+0000","createdBy":"superadmin"},{"id":14,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"Tst6-4G-dfdfds","programDescription":"dfdfsd","status":"Active","creationDate":"2019-02-14T13:02:46.000+0000","createdBy":"superadmin"},{"id":15,"networkTypeDetailsEntity":{"id":4,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"Tst7-5G-qdqd","programDescription":"qdqd","status":"Active","creationDate":"2019-02-14T13:03:22.000+0000","createdBy":"superadmin"}],"serverTypeList":[{"id":1,"serverType":"JUMP BOX"},{"id":2,"serverType":"SANE"}],"neTypeList":[{"id":1,"neType":"CIQ SERVER"},{"id":2,"neType":"SCRIPT SERVER"},{"id":3,"neType":"PUT SERVER"},{"id":4,"neType":"LSM"},{"id":5,"neType":"VLSM"}],"neNameList":["Ne_1_3_1","Ne_1_2_1","Ne_1_1_1","Ne_1_4_1","Ne_3_5_1","Ciq_1_1_1"],"neversionList":[{"id":1,"programDetailsEntity":{"id":2,"networkTypeDetailsEntity":{"id":4,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"VZN-4G-LEGACY","programDescription":null,"status":"Active","creationDate":"2019-02-11T09:25:27.000+0000","createdBy":"superadmin"},"neVersion":"1.2.3","status":"Active","createdBy":"superadmin","creationDate":"2019-02-15T11:02:23.000+0000"}],"loginTypeList":[{"id":1,"loginType":"SSH"},{"id":2,"loginType":"FTP"},{"id":3,"loginType":"SFTP"}],"status":"SUCCESS"};
                // this.tableData = {"networkConfigList":[{"id":8,"programDetailsEntity":{"id":9,"networkTypeDetailsEntity":{"id":12,"networkType":"3G","createdBy":"admin","caretedDate":"2019-01-23T05:33:51.000+0000","status":"Active","remarks":"","networkColor":"#914152"},"programName":"TST2_3G_L","programDescription":null,"status":"Inactive","creationDate":"2019-02-11T09:26:24.000+0000","createdBy":"superadmin"},"neTypeEntity":{"id":1,"neType":"CIQ SERVER"},"neName":"ciq_1","neVersionEntity":null,"neIp":"10.0.0.0","neUserName":"admin","nePassword":"admin","loginTypeEntity":{"id":3,"loginType":"SFTP"},"createdBy":"superadmin","creationDate":"2019-02-19 18:01","status":"Active","remarks":"ssvs","neDetails":[]},{"id":1,"programDetailsEntity":{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":null,"status":"InActive","creationDate":"2019-02-11T09:24:41.000+0000","createdBy":"superadmin"},"neTypeEntity":{"id":1,"neType":"CIQ SERVER"},"neName":"Ne_1_1_1","neVersionEntity":{"id":1,"programDetailsEntity":{"id":3,"networkTypeDetailsEntity":{"id":4,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"VZN-5G-LEGACY","programDescription":null,"status":"Active","creationDate":"2019-02-11T09:25:27.000+0000","createdBy":"superadmin"},"neVersion":"1.2.3","status":"Active","createdBy":"superadmin","creationDate":"2019-02-15T11:02:23.000+0000"},"neIp":"10.0.0.0","neUserName":"admin","nePassword":"admin","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-02-19 12:21","status":"Active","remarks":"admin","neDetails":[{"id":1,"step":1,"serverTypeEntity":{"id":1,"serverType":"JUMP BOX"},"serverName":"S_1_1","serverIp":"10.1.1","serverUserName":"admin","serverPassword":"admin","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-02-19 12:21","path":"1.0","networkConfigEntity":null},{"id":2,"step":1,"serverTypeEntity":{"id":1,"serverType":"JUMP BOX"},"serverName":"S_1_1","serverIp":"10.1.1","serverUserName":"admin","serverPassword":"admin","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-02-19 12:21","path":"1.0","networkConfigEntity":null}]},{"id":5,"programDetailsEntity":{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":null,"status":"InActive","creationDate":"2019-02-11T09:24:41.000+0000","createdBy":"superadmin"},"neTypeEntity":{"id":1,"neType":"CIQ SERVER"},"neName":"Ne_3_5_1","neVersionEntity":{"id":1,"programDetailsEntity":{"id":3,"networkTypeDetailsEntity":{"id":4,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"VZN-5G-LEGACY","programDescription":null,"status":"Active","creationDate":"2019-02-11T09:25:27.000+0000","createdBy":"superadmin"},"neVersion":"1.2.3","status":"Active","createdBy":"superadmin","creationDate":"2019-02-15T11:02:23.000+0000"},"neIp":"10.0.0.0","neUserName":"admin","nePassword":"admin","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-02-19 12:21","status":"Active","remarks":"testing","neDetails":[{"id":5,"step":1,"serverTypeEntity":{"id":1,"serverType":"JUMP BOX"},"serverName":"Sane1_1","serverIp":"0.0.0.0","serverUserName":"admin","serverPassword":"admin","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-02-19 12:21","path":"1,2,3","networkConfigEntity":null}]},{"id":2,"programDetailsEntity":{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":null,"status":"InActive","creationDate":"2019-02-11T09:24:41.000+0000","createdBy":"superadmin"},"neTypeEntity":{"id":1,"neType":"CIQ SERVER"},"neName":"Ne_1_2_1","neVersionEntity":{"id":1,"programDetailsEntity":{"id":3,"networkTypeDetailsEntity":{"id":4,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"VZN-5G-LEGACY","programDescription":null,"status":"Active","creationDate":"2019-02-11T09:25:27.000+0000","createdBy":"superadmin"},"neVersion":"1.2.3","status":"Active","createdBy":"superadmin","creationDate":"2019-02-15T11:02:23.000+0000"},"neIp":"10.0.0.0","neUserName":"admin","nePassword":"admin","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-02-19 12:21","status":"Active","remarks":"admin","neDetails":[]},{"id":6,"programDetailsEntity":{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":null,"status":"InActive","creationDate":"2019-02-11T09:24:41.000+0000","createdBy":"superadmin"},"neTypeEntity":{"id":1,"neType":"CIQ SERVER"},"neName":"Ciq_1_1_1","neVersionEntity":{"id":1,"programDetailsEntity":{"id":3,"networkTypeDetailsEntity":{"id":4,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"VZN-5G-LEGACY","programDescription":null,"status":"Active","creationDate":"2019-02-11T09:25:27.000+0000","createdBy":"superadmin"},"neVersion":"1.2.3","status":"Active","createdBy":"superadmin","creationDate":"2019-02-15T11:02:23.000+0000"},"neIp":"10.6.7.7","neUserName":"admin","nePassword":"admin","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-02-19 12:21","status":"Active","remarks":"testing","neDetails":[{"id":6,"step":2,"serverTypeEntity":{"id":1,"serverType":"JUMP BOX"},"serverName":"Jump1_1","serverIp":"0.1.1.1","serverUserName":"admin","serverPassword":"admin","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-02-19 12:21","path":"7,8,9","networkConfigEntity":null}]},{"id":3,"programDetailsEntity":{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":null,"status":"InActive","creationDate":"2019-02-11T09:24:41.000+0000","createdBy":"superadmin"},"neTypeEntity":{"id":1,"neType":"CIQ SERVER"},"neName":"Ne_1_3_1","neVersionEntity":{"id":1,"programDetailsEntity":{"id":3,"networkTypeDetailsEntity":{"id":4,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"VZN-5G-LEGACY","programDescription":null,"status":"Active","creationDate":"2019-02-11T09:25:27.000+0000","createdBy":"superadmin"},"neVersion":"1.2.3","status":"Active","createdBy":"superadmin","creationDate":"2019-02-15T11:02:23.000+0000"},"neIp":"10.0.0.0","neUserName":"admin","nePassword":"admin","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-02-19 12:21","status":"Active","remarks":"admin","neDetails":[{"id":3,"step":2,"serverTypeEntity":{"id":1,"serverType":"JUMP BOX"},"serverName":"S_1_1","serverIp":"10.1.1","serverUserName":"admin","serverPassword":"admin","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-02-19 12:21","path":"1.0","networkConfigEntity":null}]},{"id":4,"programDetailsEntity":{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":null,"status":"InActive","creationDate":"2019-02-11T09:24:41.000+0000","createdBy":"superadmin"},"neTypeEntity":{"id":1,"neType":"CIQ SERVER"},"neName":"Ne_1_4_1","neVersionEntity":{"id":1,"programDetailsEntity":{"id":3,"networkTypeDetailsEntity":{"id":4,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"VZN-5G-LEGACY","programDescription":null,"status":"Active","creationDate":"2019-02-11T09:25:27.000+0000","createdBy":"superadmin"},"neVersion":"1.2.3","status":"Active","createdBy":"superadmin","creationDate":"2019-02-15T11:02:23.000+0000"},"neIp":"10.0.0.0","neUserName":"admin","nePassword":"admin","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-02-19 12:21","status":"Active","remarks":"admin","neDetails":[{"id":4,"step":3,"serverTypeEntity":{"id":1,"serverType":"JUMP BOX"},"serverName":"S_1_1","serverIp":"10.1.1","serverUserName":"admin","serverPassword":"admin","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-02-19 12:21","path":"1.0","networkConfigEntity":null}]}],"pageCount":1,"programNamesList":[{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"VZN-4G-LEGACY","programDescription":null,"status":"InActive","creationDate":"2019-02-11T09:24:41.000+0000","createdBy":"superadmin"},{"id":3,"networkTypeDetailsEntity":{"id":4,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"VZN-5G-LEGACY","programDescription":null,"status":"Active","creationDate":"2019-02-11T09:25:27.000+0000","createdBy":"superadmin"},{"id":4,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"AT&T-4G-LEGACY","programDescription":null,"status":"Active","creationDate":"2019-02-11T09:25:34.000+0000","createdBy":"superadmin"},{"id":5,"networkTypeDetailsEntity":{"id":4,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"AT&T-5G-LEGACY","programDescription":null,"status":"Active","creationDate":"2019-02-11T09:25:39.000+0000","createdBy":"superadmin"},{"id":6,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"SPT-4G-LEGACY","programDescription":null,"status":"Active","creationDate":"2019-02-11T09:25:44.000+0000","createdBy":"superadmin"},{"id":9,"networkTypeDetailsEntity":{"id":12,"networkType":"3G","createdBy":"admin","caretedDate":"2019-01-23T05:33:51.000+0000","status":"Active","remarks":"","networkColor":"#914152"},"programName":"TST2_3G_L","programDescription":null,"status":"Inactive","creationDate":"2019-02-11T09:26:24.000+0000","createdBy":"superadmin"},{"id":10,"networkTypeDetailsEntity":{"id":4,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"TST3-5G-MIMO","programDescription":null,"status":"Inactive","creationDate":"2019-02-11T09:34:41.000+0000","createdBy":"superadmin"},{"id":11,"networkTypeDetailsEntity":{"id":4,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"TST4-5G-MIMO","programDescription":null,"status":"Inactive","creationDate":"2019-02-11T09:37:57.000+0000","createdBy":"superadmin"},{"id":12,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"TST1-4G-LEGACY-1","programDescription":null,"status":"Active","creationDate":"2019-02-12T12:46:18.000+0000","createdBy":"superadmin"},{"id":13,"networkTypeDetailsEntity":{"id":10,"networkType":"6G_2","createdBy":"admin","caretedDate":"2019-01-23T05:33:41.000+0000","status":"Active","remarks":"","networkColor":"#bf16e1"},"programName":"tst2-6G_2-leg","programDescription":"leg for tst2","status":"Active","creationDate":"2019-02-14T10:48:21.000+0000","createdBy":"superadmin"},{"id":14,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#f51dc1"},"programName":"Tst6-4G-dfdfds","programDescription":"dfdfsd","status":"Active","creationDate":"2019-02-14T13:02:46.000+0000","createdBy":"superadmin"},{"id":15,"networkTypeDetailsEntity":{"id":4,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"Tst7-5G-qdqd","programDescription":"qdqd","status":"Active","creationDate":"2019-02-14T13:03:22.000+0000","createdBy":"superadmin"}],"serverTypeList":[{"id":1,"serverType":"JUMP BOX"},{"id":2,"serverType":"SANE"}],"neTypeList":[{"id":1,"neType":"CIQ SERVER"},{"id":2,"neType":"SCRIPT SERVER"},{"id":3,"neType":"PUT SERVER"},{"id":4,"neType":"LSM"},{"id":5,"neType":"VLSM"}],"neNameList":["Ne_1_3_1","Ne_1_2_1","Ne_1_1_1","ciq_1","Ne_1_4_1","Ne_3_5_1","Ciq_1_1_1"],"sessionId":"738c63bb","serviceToken":"93079","neversionList":[{"id":1,"programDetailsEntity":{"id":3,"networkTypeDetailsEntity":{"id":4,"networkType":"5G","createdBy":"admin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#baba97"},"programName":"VZN-5G-LEGACY","programDescription":null,"status":"Active","creationDate":"2019-02-11T09:25:27.000+0000","createdBy":"superadmin"},"neVersion":"1.2.3","status":"Active","createdBy":"superadmin","creationDate":"2019-02-15T11:02:23.000+0000"}],"loginTypeList":[{"id":1,"loginType":"SSH"},{"id":2,"loginType":"FTP"},{"id":3,"loginType":"SFTP"}],"status":"SUCCESS"};
                this.tableData = {"pageCount":2,"sessionId":"c7fc6b97","status":"SUCCESS","networkConfigList":[{"id":87,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-08-26T00:38:56.000+0000","status":"Active","remarks":"","networkColor":"#b1c208"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2019-11-21T23:06:02.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neMarket":"New England","neTypeEntity":{"id":8,"neType":"USM"},"neName":"Test NE","neVersionEntity":{"id":27,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-08-26T00:38:56.000+0000","status":"Active","remarks":"","networkColor":"#b1c208"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2019-11-21T23:06:02.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neVersion":"8.5.1","status":"Active","createdBy":"superadmin","creationDate":"2019-11-21T23:07:38.000+0000","releaseVersion":"r-01"},"neIp":"10.20.120.82","neRelVersion":"r-01","neRsIp":"10.20.120.82","neUserName":"user","nePassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-12-12 12:22","status":"Active","neUserPrompt":"rctuser1","neSuperUserPrompt":"rctuser1","remarks":"","neDetails":[],"neVersion":null},{"id":86,"programDetailsEntity":{"id":35,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-08-26T00:38:56.000+0000","status":"Active","remarks":"","networkColor":"#b1c208"},"programName":"SPT-4G-MIMO-Testing","programDescription":"MIMO-Testing","status":"Active","creationDate":"2019-11-26T22:57:29.000+0000","createdBy":"superadmin","sourceProgramId":26,"sourceprogramName":"SPT-4G-MIMO-Latest"},"neMarket":"Chicago","neTypeEntity":{"id":4,"neType":"LSM"},"neName":"Chicago LSMR 3","neVersionEntity":{"id":28,"programDetailsEntity":{"id":35,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-08-26T00:38:56.000+0000","status":"Active","remarks":"","networkColor":"#b1c208"},"programName":"SPT-4G-MIMO-Testing","programDescription":"MIMO-Testing","status":"Active","creationDate":"2019-11-26T22:57:29.000+0000","createdBy":"superadmin","sourceProgramId":26,"sourceprogramName":"SPT-4G-MIMO-Latest"},"neVersion":"8.5.3","status":"Active","createdBy":"superadmin","creationDate":"2019-11-26T22:58:34.000+0000","releaseVersion":"r-03"},"neIp":"172.22.82.114","neRsIp":"20.1.1.44","neUserName":"ciuser123","nePassword":"S@msung1p","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-11-27 12:12","status":"Active","neUserPrompt":"rctuser2","neSuperUserPrompt":"rctuser2","remarks":"","neDetails":[],"neVersion":null},{"id":82,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-08-26T00:38:56.000+0000","status":"Active","remarks":"","networkColor":"#b1c208"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2019-11-21T23:06:02.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neMarket":"New England","neTypeEntity":{"id":8,"neType":"USM"},"neName":"Westboro_Tiny","neVersionEntity":{"id":27,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-08-26T00:38:56.000+0000","status":"Active","remarks":"","networkColor":"#b1c208"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2019-11-21T23:06:02.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neVersion":"8.5.1","status":"Active","createdBy":"superadmin","creationDate":"2019-11-21T23:07:38.000+0000","releaseVersion":"r-01"},"neIp":"2001:4888:A12:3143:106:0292:0000:0000","neRsIp":"2001:4888:2a10:5030:0123:0292:0000:0100","neUserName":"vsm","nePassword":"nmslab123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-11-22 19:53","status":"Active","neUserPrompt":"rctuser3","neSuperUserPrompt":"rctuser3","remarks":"","neDetails":[{"id":78,"step":1,"serverTypeEntity":{"id":1,"serverType":"JUMP BOX"},"serverName":"Jump Box Server","serverIp":"172.22.82.182","serverUserName":"rctuser","serverPassword":"Rctuser","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-11-22 19:53","path":"","userPrompt":"rctuser","superUserPrompt":"rctuser","networkConfigEntity":null},{"id":79,"step":2,"serverTypeEntity":{"id":2,"serverType":"SANE"},"serverName":"Sane Server","serverIp":"198.226.62.4","serverUserName":"v0bhatad","serverPassword":"S0mmer@1","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-11-22 19:53","path":"2,1,32,1,3,1,1","userPrompt":"Please Enter Selection: >","superUserPrompt":"vendgrp","networkConfigEntity":null}],"neVersion":null},{"id":79,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-08-26T00:38:56.000+0000","status":"Active","remarks":"","networkColor":"#b1c208"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2019-11-21T23:06:02.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neMarket":"New England","neTypeEntity":{"id":8,"neType":"USM"},"neName":"East_Syracuse","neVersionEntity":{"id":27,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-08-26T00:38:56.000+0000","status":"Active","remarks":"","networkColor":"#b1c208"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2019-11-21T23:06:02.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neVersion":"8.5.1","status":"Active","createdBy":"superadmin","creationDate":"2019-11-21T23:07:38.000+0000","releaseVersion":"r-01"},"neIp":"2001:4888:0a18:3143:01b1:0292:0000:0100","neRsIp":"2001:4888:2a1f:5030:01b1:0292:0000:0100","neUserName":"vsm","nePassword":"nmslab123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-11-22 19:16","status":"Active","remarks":"","neUserPrompt":"rctuser4","neSuperUserPrompt":"rctuser4","neDetails":[{"id":68,"step":1,"serverTypeEntity":{"id":1,"serverType":"JUMP BOX"},"serverName":"Jump Box Server","serverIp":"172.22.82.182","serverUserName":"rctuser","serverPassword":"Rctuser","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-11-22 19:16","path":"","userPrompt":"rctuser","superUserPrompt":"rctuser","networkConfigEntity":null},{"id":69,"step":2,"serverTypeEntity":{"id":2,"serverType":"SANE"},"serverName":"Sane Server","serverIp":"198.226.62.4","serverUserName":"v0bhatad","serverPassword":"S0mmer@1","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-11-22 19:16","path":"2,1,32,1,3,1,1","userPrompt":"Please Enter Selection: >","superUserPrompt":"vendgrp","networkConfigEntity":null}],"neVersion":null},{"id":80,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-08-26T00:38:56.000+0000","status":"Active","remarks":"","networkColor":"#b1c208"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2019-11-21T23:06:02.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neMarket":"New England","neTypeEntity":{"id":8,"neType":"USM"},"neName":"Rochester_Dr","neVersionEntity":{"id":27,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-08-26T00:38:56.000+0000","status":"Active","remarks":"","networkColor":"#b1c208"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2019-11-21T23:06:02.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neVersion":"8.5.1","status":"Active","createdBy":"superadmin","creationDate":"2019-11-21T23:07:38.000+0000","releaseVersion":"r-01"},"neIp":"2001:4888:0a18:3143:01b1:0292:0000:0201","neRsIp":"2001:4888:2a1f:5030:01b1:0292:0000:0201","neUserName":"vsm","nePassword":"nmslab123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-11-22 18:43","status":"Active","remarks":"","neUserPrompt":"rctuser5","neSuperUserPrompt":"rctuser5","neDetails":[{"id":70,"step":1,"serverTypeEntity":{"id":1,"serverType":"JUMP BOX"},"serverName":"Jump Box Server","serverIp":"172.22.82.182","serverUserName":"rctuser","serverPassword":"Rctuser","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-11-22 18:43","path":"","userPrompt":"rctuser","superUserPrompt":"rctuser","networkConfigEntity":null},{"id":71,"step":2,"serverTypeEntity":{"id":2,"serverType":"SANE"},"serverName":"Sane Server","serverIp":"198.226.62.4","serverUserName":"v0bhatad","serverPassword":"S0mmer@1","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-11-22 18:43","path":"2,1,32,1,3,1,1","userPrompt":"Please Enter Selection: >","superUserPrompt":"vendgrp","networkConfigEntity":null}],"neVersion":null},{"id":81,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-08-26T00:38:56.000+0000","status":"Active","remarks":"","networkColor":"#b1c208"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2019-11-21T23:06:02.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neMarket":"New England","neTypeEntity":{"id":8,"neType":"USM"},"neName":"Rochester","neVersionEntity":{"id":27,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-08-26T00:38:56.000+0000","status":"Active","remarks":"","networkColor":"#b1c208"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2019-11-21T23:06:02.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neVersion":"8.5.1","status":"Active","createdBy":"superadmin","creationDate":"2019-11-21T23:07:38.000+0000","releaseVersion":"r-01"},"neIp":"2001:4888:0a19:3143:01b4:0292:0000:0100","neRsIp":"2001:4888:2a1f:6030:01b4:0292:0000:0100","neUserName":"vsm","nePassword":"nmslab123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-11-22 18:42","status":"Active","remarks":"","neUserPrompt":"rctuser6","neSuperUserPrompt":"rctuser6","neDetails":[{"id":72,"step":1,"serverTypeEntity":{"id":1,"serverType":"JUMP BOX"},"serverName":"Jump Box Server","serverIp":"172.22.82.182","serverUserName":"rctuser","serverPassword":"Rctuser","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-11-22 18:42","path":"","userPrompt":"rctuser","superUserPrompt":"rctuser","networkConfigEntity":null},{"id":73,"step":2,"serverTypeEntity":{"id":2,"serverType":"SANE"},"serverName":"Sane Server","serverIp":"198.226.62.4","serverUserName":"v0bhatad","serverPassword":"S0mmer@1","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-11-22 18:42","path":"2,1,32,1,3,1,1","userPrompt":"Please Enter Selection: >","superUserPrompt":"vendgrp","networkConfigEntity":null}],"neVersion":null},{"id":84,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-08-26T00:38:56.000+0000","status":"Active","remarks":"","networkColor":"#b1c208"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2019-11-21T23:06:02.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neMarket":"New England","neTypeEntity":{"id":8,"neType":"USM"},"neName":"Windsor_Medium","neVersionEntity":{"id":27,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-08-26T00:38:56.000+0000","status":"Active","remarks":"","networkColor":"#b1c208"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2019-11-21T23:06:02.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neVersion":"8.5.1","status":"Active","createdBy":"superadmin","creationDate":"2019-11-21T23:07:38.000+0000","releaseVersion":"r-01"},"neIp":"2001:4888:0a13:3143:0123:0292:0000:0100","neRsIp":"2001:4888:2a10:5030:0123:0292:0000:0100","neUserName":"vsm","nePassword":"nmslab123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-11-22 18:42","status":"Active","remarks":"","neUserPrompt":"rctuser7","neSuperUserPrompt":"rctuser7","neDetails":[{"id":74,"step":1,"serverTypeEntity":{"id":1,"serverType":"JUMP BOX"},"serverName":"Jump Box Server","serverIp":"172.22.82.182","serverUserName":"rctuser","serverPassword":"Rctuser","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-11-22 18:42","path":"","userPrompt":"rctuser","superUserPrompt":"rctuser","networkConfigEntity":null},{"id":75,"step":2,"serverTypeEntity":{"id":2,"serverType":"SANE"},"serverName":"Sane Server","serverIp":"198.226.62.4","serverUserName":"v0bhatad","serverPassword":"S0mmer@1","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-11-22 18:42","path":"2,1,32,1,3,1,1","userPrompt":"Please Enter Selection: >","superUserPrompt":"vendgrp","networkConfigEntity":null}],"neVersion":null},{"id":83,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-08-26T00:38:56.000+0000","status":"Active","remarks":"","networkColor":"#b1c208"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2019-11-21T23:06:02.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neMarket":"New England","neTypeEntity":{"id":8,"neType":"USM"},"neName":"Westboro_Medium","neVersionEntity":{"id":27,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-08-26T00:38:56.000+0000","status":"Active","remarks":"","networkColor":"#b1c208"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2019-11-21T23:06:02.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neVersion":"8.5.1","status":"Active","createdBy":"superadmin","creationDate":"2019-11-21T23:07:38.000+0000","releaseVersion":"r-01"},"neIp":"2001:4888:0a12:3143:0106:0292:0000:0100","neRsIp":"2001:4888:2a10:0030:0106:0292:0000:0100","neUserName":"vsm","nePassword":"nmslab123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-11-22 18:42","status":"Active","remarks":"","neUserPrompt":"rctuser8","neSuperUserPrompt":"rctuser8","neDetails":[{"id":76,"step":1,"serverTypeEntity":{"id":1,"serverType":"JUMP BOX"},"serverName":"Jump Box Server","serverIp":"172.22.82.182","serverUserName":"rctuser","serverPassword":"Rctuser","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-11-22 18:42","path":"","userPrompt":"rctuser","superUserPrompt":"rctuser","networkConfigEntity":null},{"id":77,"step":2,"serverTypeEntity":{"id":2,"serverType":"SANE"},"serverName":"Sane Server","serverIp":"198.226.62.4","serverUserName":"v0bhatad","serverPassword":"S0mmer@1","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-11-22 18:42","path":"2,1,32,1,3,1,1","userPrompt":"Please Enter Selection: >","superUserPrompt":"vendgrp","networkConfigEntity":null}],"neVersion":null},{"id":78,"programDetailsEntity":{"id":30,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-08-26T00:38:56.000+0000","status":"Active","remarks":"","networkColor":"#b1c208"},"programName":"VZN-4G-USM-Latest","programDescription":"USM-Latest","status":"Active","creationDate":"2019-10-09T22:25:57.000+0000","createdBy":"superadmin","sourceProgramId":28,"sourceprogramName":"VZN-4G-USM-TEST"},"neMarket":"New England","neTypeEntity":{"id":8,"neType":"USM"},"neName":"USM_WINDSOR_MEDIUM_Latest","neVersionEntity":{"id":26,"programDetailsEntity":{"id":30,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-08-26T00:38:56.000+0000","status":"Active","remarks":"","networkColor":"#b1c208"},"programName":"VZN-4G-USM-Latest","programDescription":"USM-Latest","status":"Active","creationDate":"2019-10-09T22:25:57.000+0000","createdBy":"superadmin","sourceProgramId":28,"sourceprogramName":"VZN-4G-USM-TEST"},"neVersion":"8.5.1","status":"Active","createdBy":"superadmin","creationDate":"2019-11-15T03:10:38.000+0000","releaseVersion":"r-01,r-03"},"neIp":"2001:4888:0a18:3143:01b1:0292:0000:0100","neRsIp":"2001:4888:0a18:3143:01b1:0292:0000:0100","neUserName":"vsm","nePassword":"nmslab123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-11-19 12:25","status":"Active","remarks":"","neUserPrompt":"rctuser9","neSuperUserPrompt":"rctuser9","neDetails":[{"id":66,"step":1,"serverTypeEntity":{"id":1,"serverType":"JUMP BOX"},"serverName":"Jump Server","serverIp":"172.22.82.182","serverUserName":"rctuser","serverPassword":"Rctuser","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-11-19 12:25","path":"","userPrompt":"rctuser","superUserPrompt":"rctuser","networkConfigEntity":null},{"id":67,"step":2,"serverTypeEntity":{"id":2,"serverType":"SANE"},"serverName":"Sane Server","serverIp":"198.226.62.4","serverUserName":"v0bhatad","serverPassword":"S0mmer@1","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-11-19 12:25","path":"2,1,32,1,3,1,1","userPrompt":"Please Enter Selection: >","superUserPrompt":"vendgrp","networkConfigEntity":null}],"neVersion":null},{"id":77,"programDetailsEntity":{"id":30,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-08-26T00:38:56.000+0000","status":"Active","remarks":"","networkColor":"#b1c208"},"programName":"VZN-4G-USM-Latest","programDescription":"USM-Latest","status":"Active","creationDate":"2019-10-09T22:25:57.000+0000","createdBy":"superadmin","sourceProgramId":28,"sourceprogramName":"VZN-4G-USM-TEST"},"neMarket":"New England","neTypeEntity":{"id":8,"neType":"USM"},"neName":"USM_WINDSOR_MEDIUM","neVersionEntity":{"id":25,"programDetailsEntity":{"id":30,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-08-26T00:38:56.000+0000","status":"Active","remarks":"","networkColor":"#b1c208"},"programName":"VZN-4G-USM-Latest","programDescription":"USM-Latest","status":"Active","creationDate":"2019-10-09T22:25:57.000+0000","createdBy":"superadmin","sourceProgramId":28,"sourceprogramName":"VZN-4G-USM-TEST"},"neVersion":"8.0.0","status":"Active","createdBy":"superadmin","creationDate":"2019-10-09T23:00:32.000+0000","releaseVersion":"r-01"},"neIp":"192.168.114.101","neRsIp":" 2404:0180:1004:0001:cf:500:3030:10","neUserName":"vsm","nePassword":"nmslab123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-10-10 17:42","status":"Active","remarks":"","neUserPrompt":"rctuser10","neSuperUserPrompt":"rctuser10","neDetails":[{"id":64,"step":1,"serverTypeEntity":{"id":1,"serverType":"JUMP BOX"},"serverName":"Hypervisor","serverIp":"172.22.82.25","serverUserName":"root","serverPassword":"root123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-10-10 17:42","path":"","userPrompt":"","superUserPrompt":"","networkConfigEntity":null},{"id":65,"step":2,"serverTypeEntity":{"id":1,"serverType":"JUMP BOX"},"serverName":"Director","serverIp":"10.59.10.121","serverUserName":"rantester1","serverPassword":"Samsung123","loginTypeEntity":{"id":1,"loginType":"SSH"},"createdBy":"superadmin","creationDate":"2019-10-10 17:42","path":"","userPrompt":"","superUserPrompt":"","networkConfigEntity":null}],"neVersion":null}],"programNamesList":[{"id":27,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-08-26T00:38:56.000+0000","status":"Active","remarks":"","networkColor":"#b1c208"},"programName":"SPT-4G-CDU30-Latest","programDescription":"CDU30-Latest","status":"Active","creationDate":"2019-09-30T06:17:38.000+0000","createdBy":"superadmin","sourceProgramId":20,"sourceprogramName":"SPT-4G-CDU30"},{"id":26,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-08-26T00:38:56.000+0000","status":"Active","remarks":"","networkColor":"#b1c208"},"programName":"SPT-4G-MIMO-Latest","programDescription":"MIMO-Latest","status":"Active","creationDate":"2019-09-27T07:46:53.000+0000","createdBy":"superadmin","sourceProgramId":24,"sourceprogramName":"SPT-4G-MIMO-Latest1"},{"id":35,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-08-26T00:38:56.000+0000","status":"Active","remarks":"","networkColor":"#b1c208"},"programName":"SPT-4G-MIMO-Testing","programDescription":"MIMO-Testing","status":"Active","creationDate":"2019-11-26T22:57:29.000+0000","createdBy":"superadmin","sourceProgramId":26,"sourceprogramName":"SPT-4G-MIMO-Latest"},{"id":30,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-08-26T00:38:56.000+0000","status":"Active","remarks":"","networkColor":"#b1c208"},"programName":"VZN-4G-USM-Latest","programDescription":"USM-Latest","status":"Active","creationDate":"2019-10-09T22:25:57.000+0000","createdBy":"superadmin","sourceProgramId":28,"sourceprogramName":"VZN-4G-USM-TEST"},{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-08-26T00:38:56.000+0000","status":"Active","remarks":"","networkColor":"#b1c208"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2019-11-21T23:06:02.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"}],"neMarketList":["Chicago","New England"],"serverTypeList":[{"id":1,"serverType":"JUMP BOX"},{"id":2,"serverType":"SANE"},{"id":3,"serverType":"VLSM MCMA"},{"id":4,"serverType":"PASS SERVER"}],"neTypeList":[{"id":1,"neType":"CIQ SERVER"},{"id":2,"neType":"SCRIPT SERVER"},{"id":3,"neType":"PUT SERVER"},{"id":4,"neType":"LSM"},{"id":5,"neType":"VLSM"},{"id":6,"neType":"BSM"},{"id":7,"neType":"CSR"},{"id":8,"neType":"USM"}],"neNameList":["East_Syracuse","Rochester_Dr","Chicago LSMR 3","Rochester","Chicago LSMR 4","USM_WINDSOR_MEDIUM_Latest","Test NE","Westboro_Medium","USM_WINDSOR_MEDIUM","Windsor_Medium","Westboro_Tiny"],"neversionList":[{"id":28,"programDetailsEntity":{"id":35,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-08-26T00:38:56.000+0000","status":"Active","remarks":"","networkColor":"#b1c208"},"programName":"VZN-4G-USM-LIVE","programDescription":"USM-LIVE","status":"Active","creationDate":"2019-11-26T22:57:29.000+0000","createdBy":"superadmin","sourceProgramId":26,"sourceprogramName":"SPT-4G-MIMO-Latest"},"neVersion":"8.5.3","status":"Active","createdBy":"superadmin","creationDate":"2019-11-26T22:58:34.000+0000","releaseVersion":"r-03,r-04,r-01"},{"id":27,"programDetailsEntity":{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-08-26T00:38:56.000+0000","status":"Active","remarks":"","networkColor":"#b1c208"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2019-11-21T23:06:02.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},"neVersion":"8.5.1","status":"Active","createdBy":"superadmin","creationDate":"2019-11-21T23:07:38.000+0000","releaseVersion":"r-01"},{"id":26,"programDetailsEntity":{"id":30,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-08-26T00:38:56.000+0000","status":"Active","remarks":"","networkColor":"#b1c208"},"programName":"VZN-4G-USM-Latest","programDescription":"USM-Latest","status":"Active","creationDate":"2019-10-09T22:25:57.000+0000","createdBy":"superadmin","sourceProgramId":28,"sourceprogramName":"VZN-4G-USM-TEST"},"neVersion":"8.5.1","status":"Active","createdBy":"superadmin","creationDate":"2019-11-15T03:10:38.000+0000","releaseVersion":"r-01"},{"id":25,"programDetailsEntity":{"id":30,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-08-26T00:38:56.000+0000","status":"Active","remarks":"","networkColor":"#b1c208"},"programName":"VZN-4G-USM-Latest","programDescription":"USM-Latest","status":"Active","creationDate":"2019-10-09T22:25:57.000+0000","createdBy":"superadmin","sourceProgramId":28,"sourceprogramName":"VZN-4G-USM-TEST"},"neVersion":"8.0.0","status":"Active","createdBy":"superadmin","creationDate":"2019-10-09T23:00:32.000+0000","releaseVersion":"r-01"},{"id":22,"programDetailsEntity":{"id":27,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-08-26T00:38:56.000+0000","status":"Active","remarks":"","networkColor":"#b1c208"},"programName":"SPT-4G-CDU30-Latest","programDescription":"CDU30-Latest","status":"Active","creationDate":"2019-09-30T06:17:38.000+0000","createdBy":"superadmin","sourceProgramId":20,"sourceprogramName":"SPT-4G-CDU30"},"neVersion":"8.5.3","status":"Active","createdBy":"superadmin","creationDate":"2019-10-03T02:02:03.000+0000","releaseVersion":"r-03"},{"id":21,"programDetailsEntity":{"id":26,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-08-26T00:38:56.000+0000","status":"Active","remarks":"","networkColor":"#b1c208"},"programName":"SPT-4G-MIMO-Latest","programDescription":"MIMO-Latest","status":"Active","creationDate":"2019-09-27T07:46:53.000+0000","createdBy":"superadmin","sourceProgramId":24,"sourceprogramName":"SPT-4G-MIMO-Latest1"},"neVersion":"8.5.3","status":"Active","createdBy":"superadmin","creationDate":"2019-09-27T09:05:46.000+0000","releaseVersion":"r-03"}],"loginTypeList":[{"id":1,"loginType":"SSH"},{"id":2,"loginType":"TELNET"}],"serviceToken":"76254"};
                  if(this.tableData.sessionId == "408" || this.tableData.status == "Invalid User"){
                       this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                      } 
                 
                    this.privilegeSetting = {"USER_NAME":true};
                    this.totalPages = this.tableData.pageCount;
                    let pageCount = [];
                    for (var i = 1; i <= this.tableData.pageCount; i++) {
                        pageCount.push(i);
                    }
                    this.pageRenge = pageCount;
                    this.pager = this.getPager(this.totalPages*this.pageSize, this.currentPage, this.pageRenge);
                    
                    this.serverTypeList = this.tableData.serverTypeList;
                    this.neTypeList = this.tableData.neTypeList;
                    this.loginTypeList = this.tableData.loginTypeList;
                    this.programNamesList = this.tableData.programNamesList;
                    this.neNameList = this.tableData.neNameList;
                    this.neMarketList=this.tableData.neMarketList;
                    // this.neVersionList = this.tableData.neversionList;
                    

                    this.pgmNameConfig = {
                        displayKey: "programName", //if objects array passed which key to be displayed defaults to description
                        search: true, //true/false for the search functionlity defaults to false,
                        height: '200px', //height of the list so that if there are more no of items it can show a scroll defaults to auto. With auto height scroll will never appear
                        placeholder: '--Select--', // text to be displayed when no item is selected defaults to Select,
                        customComparator: () => { },// a custom function using which user wants to sort the items. default is undefined and Array.sort() will be used in that case,
                        limitTo: this.programNamesList.length, // a number thats limits the no of options displayed in the UI similar to angular's limitTo pipe
                        moreText: 'more', // text to be displayed whenmore than one items are selected like Option 1 + 5 more                    
                        noResultsFound: 'No results found!',// text to be displayed when no items are found while searching
                        searchPlaceholder: 'Search', // label thats displayed in search input,
                        searchOnKey: 'programName',// key on which search should be performed this will be selective search. if undefined this will be extensive search on all keys
                    }

                    if(this.tableData.networkConfigList.length == 0){
                      this.tableShowHide = false;
                      this.noDataVisibility = true;
                    }else{

                      this.tableShowHide = true;
                      this.noDataVisibility = false;
                      setTimeout(() => { 
                        let tableWidth = document.getElementById('SystemManagerDetails').scrollWidth;
                        $(".networkTable .scrollBody table").css("min-width",(tableWidth) + "px");
                        $(".networkTable .scrollHead table").css("width", tableWidth + "px");

                    
                        $(".networkTable .scrollBody").on("scroll", function (event) {
                            $(".formEditRow .networkWrapper").css("margin-left", (event.target.scrollLeft - 5) + "px");
                            $(".networkRow .form-control-fixed").css("right",(event.target.scrollLeft * -1) + "px");
                            $(".networkTable .scrollHead table#SystemManagerDetails").css("margin-left",(event.target.scrollLeft * -1) + "px");
                        });
                        $(".networkTable .scrollBody").css("max-height",(this.tableDataHeight - 10) + "px");

                        

                        },0);                      
                    }
                  

              }, 1000); */
              //Please Comment while checkIn
        }); 

  }

  loadNEVersionList(progName) {
    this.neVersionList = [];
    this.neRelVersionList = [];
    this.neRelVersion = "";
    if(progName){
        for(let neVersion of this.tableData.neversionList) {
            if(neVersion.programDetailsEntity.id == progName.id) {
                this.neVersionList.push(neVersion);
            }
        }
    }
  }

  onChangeNEVersion(neVersion) {
    this.neRelVersionList = [];
    this.neRelVersion = "";
    this.neRelVersionList = neVersion.releaseVersion.split(",");
  }


  updateip(){
      if(this.updateIp==false)
        this.updateIp=true;
     else
        this.updateIp=false;; 
  }
  clearSearchFrom() {
    this.searchForm.nativeElement.reset();  
}
  
    searchTabBind() {
        this.container.clear();
        let searchCrtra = { };
        this.searchCriteria = searchCrtra;

        this.setMenuHighlight("search");
        this.searchBlock = true;
        this.createForm = false;
        this.searchStatus = 'load';
        this.serverCreateForm = false;
        this.getAllLSMDetails();
        // Close if edit form is in open state
        if (this.currentEditRow != undefined) {
            this.currentEditRow.className = "editRow";
        }
        this.editableFormArray = [];
        this.searchForm.nativeElement.reset();
        this.clearSearchFields();
    }
    clearSearchFields() {
        this.searchPgmName = "";
        this.searchNeType = "";
        this.searchNeVersion = "";
        this.neMarketEntity = "";
        this.searchLoginType = "";

    }

      /* Used to add new Airline config details 
     * @param : event
     * @retun : null
     */

    createNewTabBind() {   
        this.container.clear();
        this.container.createEmbeddedView(this.networkConfigTemplate);    
        this.showNoDataFound = false;
        this.tableShowHide = false;
        this.searchBlock = false;
        this.createForm = true;
        this.setMenuHighlight("createNew");
        this.resetServerList(); 
        this.serverCreateForm = false;
        this.serverTable = false;     
        this.noDataVisibility = false;   
        this.serverData =[];   
        this.noDataVisibility = false; 
        this.neTypeEntity ="";
        this.neVersionEntity = "";
        this.neRelVersion = "";
        this.programDetailsEntity="";
        this.loginTypeEntity ="";
        this.neMarketEntity = "";
        this.neRsIPEntity = "";
        
    }

   
     /*
     * Used to dispaly search result based on selected criteria
     * @param : event
     * @retun : null
     */

    searchNE(event) {
    if (!event.target.classList.contains('buttonDisabled')) {
        this.tableShowHide = false;
        setTimeout(() => {
            $("#dataWrapper").find(".scrollBody").scrollLeft(0);
        }, 0);

   
        setTimeout(() => {
            if (this.isValidForm(event)) {                
                    this.showLoader = true;

                    // To hide the No Data Found and REMOVAL DETAILS Form
                   this.createForm = false;
                    this.showNoDataFound = false;

                    let currentForm = event.target.parentNode.parentNode.parentNode,
                        searchCrtra = {
                            "programDetailsEntity": this.searchPgmName ? this.searchPgmName : null,
                            "neMarket":currentForm.querySelector("#searchMarketName").value,
                            "neTypeEntity":this.searchNeType ? this.searchNeType: null ,
                            "neName":currentForm.querySelector("#searchNeName").value,
                            "neVersion":this.searchNeVersion ? this.searchNeVersion :null,//currentForm.querySelector("#searchNeVersion").value,
                            "loginTypeEntity":this.searchLoginType ? this.searchLoginType :null,
                            "status":currentForm.querySelector("#searchStatus").value
                            
                        };

                    if (searchCrtra.programDetailsEntity || searchCrtra.neTypeEntity || searchCrtra.neMarket || searchCrtra.neName || searchCrtra.neVersion || searchCrtra.loginTypeEntity || searchCrtra.status) {
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
                    this.getAllLSMDetails();   
                     
            }
        }, 0);
    }
    }

  /*
   * On click create new copy the blue print and show the form
   * @param : pass the event
   * @retun : null
   */

  /* createNew(event){    
    this.createForm = true;
    if(this.element.nativeElement.querySelector('#tableWrapper')){
      this.formWidth = this.element.nativeElement.querySelector('#tableWrapper').clientWidth  + "px";  
    }
    if(this.element.nativeElement.querySelector('.scrollBody')){
      this.scrollLeft = this.element.nativeElement.querySelector('.scrollBody').scrollLeft + "px";   
    }
    
    // Close if any edit form is opend
    this.editableFormArray = [];

    // Enable all edit buttons
    $(".editRowDisabled").attr("class","editRow");   
      let currentTarget = event.target;
     
        this.sharedService.userNavigation = false;//block user navigation
          
        /* if (currentTarget.className.indexOf('buttonDisabled') < 0 ) {
          this.container.createEmbeddedView(this.template); */
          //this.sharedService.userNavigation = false;//block user navigation
          //currentTarget.className += " buttonDisabled";
          /* setTimeout(() => { 
            validator.performValidation(event, this.validationData, "create");
           }, 10);
      //}
      
      setTimeout(() => { 
        this.searchBlockHieght = $(".bluePrintFormWrapper").height();  
        $(".scrollBody").css("max-height",(this.tableDataHeight - this.searchBlockHieght) -30 + "px");

      },0);
          
         

  } */
 
  /* On click of search highlight open record types
     * @param : current Tab Item (open/close)
     * @retun : null
     */

    setMenuHighlight(selectedElement) {
        this.searchTabRef.nativeElement.id = (selectedElement == "search") ? "activeTab" : "inactiveTab";
        this.createNewTabRef.nativeElement.id = (selectedElement == "createNew") ? "activeTab" : "inactiveTab";
    }

   /* On click create new save take the data from the form and form json
   * @param : pass the event
   * @retun : null
   */

  addNewLsmDetail(event){   
    if (this.programDetailsEntity !="" && this.programDetailsEntity != undefined &&  this.programDetailsEntity.programName) {
        this.validationData.rules.programName.required = false;

    } else {
        this.validationData.rules.programName.required = true;

    }  
      this.onChangeLoad();
    validator.performValidation(event, this.validationData, "save_update");  
   
    setTimeout(() => { 
        if (this.isValidForm(event)) {
            if (this.neTypeEntity.neType == 'VLSM') {
                if (this.serverData.filter(e => e.serverTypeEntity.serverType === 'VLSM MCMA').length > 0) {
                    this.validateServer = false;
                } else {
                    this.validateServer = true;
                    this.displayModel("Please add MCMA Server", "failureIcon");
                }
            } else {
                this.validateServer = false;
            }
            if (!this.validateServer) {
                this.showLoader = true;
                let currentForm = event.target.parentNode.parentNode,
                    networkConfigList = {
                        "id": null,
                        "programDetailsEntity": this.programDetailsEntity,
                        "neMarket": currentForm.querySelector("#neMarket").value,
                        "neTypeEntity": this.neTypeEntity,
                        "neName": currentForm.querySelector("#neName").value,
                        "neVersionEntity": this.neVersionEntity ? this.neVersionEntity : null,//currentForm.querySelector("#neVersion").value
                        "neRelVersion": this.neRelVersion ? this.neRelVersion : null,
                        "neIp": currentForm.querySelector("#neIp").value,
                        "neRsIp": currentForm.querySelector("#neRsIp").value,
                        "neUserName": currentForm.querySelector("#neUserName").value,
                        "nePassword": currentForm.querySelector("#nePassword").value,
                        "loginTypeEntity": this.loginTypeEntity,
                        "status": currentForm.querySelector("#status").value,
                        "remarks": currentForm.querySelector("#remarks").value,
                        "neDetails": this.serverData,
                        "neUserPrompt": currentForm.querySelector("#neUserPrompt").value,
                        "neSuperUserPrompt": currentForm.querySelector("#neSuperUserPrompt").value
                    }
                this.sharedService.userNavigation = true; //un block user navigation

                this.systemManagerConfigService.createLsm(networkConfigList, this.sharedService.createServiceToken(),this.updateIp)
                    .subscribe(
                        data => {

                            let currentData = data.json();
                            this.showLoader = false;

                            if (currentData.sessionId == "408" || currentData.status == "Invalid User") {

                                this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
                            } else {

                                if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {

                                    if (currentData.status == "SUCCESS") {
                                        this.message = "Network Config details created successfully!";
                                        this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                                        //this.displayModel("User created successfully!","successIcon");
                                        // this.createForm = false;
                                        this.searchStatus = "load";
                                        //this.cancelCreateNew(event);
                                    } else if (currentData.status == "FAILED") {
                                        this.displayModel(currentData.reason, "failureIcon");
                                    }
                                }
                            }

                        },
                        error => {
                            //Please Comment while checkIn
                            /* let currentData = { "reason": "Data Import Failed: LSM Name,LSM Version And NETWORK TYPE are already exists", "sessionId": "ca947c3e", "serviceToken": "98038", "status": "SUCCESS" };
                            setTimeout(() => {
                                this.showLoader = false;
                                if (currentData.status == "SUCCESS") {
                                    this.message = "Network Config details created successfully!";
                                    this.successModalBlock = this.modalService.open(this.successModalRef, { windowClass: 'success-modal', keyboard: false, backdrop: 'static', size: 'lg' });
                                    this.searchStatus = "load";
                                } else if (currentData.status == "FAILED") {
                                    this.displayModel(currentData.reason, "failureIcon");
                                }
                            }, 1000); */
                            //Please Comment while checkIn
                        });
            }
        }
    },0);    
  }

  /*
   * On session expired close the webpage and logout
   * @param : null
   * @retun : null
   */

  cancelCreateNew(event){
    this.searchTabBind();
  }


    /*
   * on click of edit row create a blueprint and append next to the current row
   * @param : current row event , current row json object and row index
   * @retun : null
   */
    editRow(event, key, index) {
        
        let editState: any = event.target,
            parentForm: any,
            location: any,
            locationPrev: any,
            airlineName: any;
        $(".editRowDisabled").attr("class", "editRow");
        $(".deleteRowDisabled").attr("class","deleteRow");
        if (editState.className != "editRowDisabled") { //enable click only if it is enabled
            editState.className = "editRowDisabled";
            $(editState).next().attr("class", "deleteRowDisabled") // Disable delete on edit row            
            if (!document.querySelector("#searchButton").classList.contains('buttonDisabled')) {
                // Disable search button while editing 
                document.querySelector("#searchButton").classList.add("buttonDisabled");
                this.paginationDisabbled = true;           }


            // To enable one edit form at a time in table
            if (this.editableFormArray.length >= 1) {
                this.editableFormArray = [];
                this.editableFormArray.push(index);
            } else {
                this.editableFormArray.push(index);
            }
            this.serverData =[];
            this.sharedService.userNavigation = false; //block user navigation
            console.log(key.neDetails);

            if(key.programDetailsEntity) {
                this.loadNEVersionList(key.programDetailsEntity);
            }
            if(key.neVersionEntity) {
                this.onChangeNEVersion(key.neVersionEntity);
            }
            this.programDetailsEntity ="";
            setTimeout(() => {
                this.serverData= key.neDetails // copy serverdetails into local array
                this.editRowInTable(event, key, index);
                this.programDetailsEntity = key.programDetailsEntity;
                this.neTypeEntity = key.neTypeEntity;               
                this.neVersionEntity = key.neVersionEntity ? key.neVersionEntity : "";
                this.neRelVersion = key.neRelVersion;
                this.loginTypeEntity = key.loginTypeEntity;
                parentForm = document.querySelector("#editedRow" + index);                
               
                 //map validation for fields
                validator.performValidation(event, this.validationData, "edit");

            }, 0);
             setTimeout(() => {
                 let tableWidth = document.getElementById('serverTableDetails').scrollWidth;
                $(".inlineTable .scrollBody table").css("min-width", (tableWidth) + "px");
                $(".inlineTable .scrollHead table").css("width", tableWidth + "px"); 


               /*   $(".inlineTable .scrollBody").on("scroll", function (event) {
                    $("#serverTableData .formEditRow #formWrapper").css("margin-left", (event.target.scrollLeft - 5) + "px");
                    $("#serverTableData .form-control-fixed").css("right", (event.target.scrollLeft * -1) + "px");
                    $(".inlineTable .scrollHead table").css("margin-left", (event.target.scrollLeft * -1) + "px");
                }); */
              //  $(".inlineTable .scrollBody").css("max-height", (this.tableDataHeight - 10) + "px"); 

            }, 0);
 
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
   * on click of cancel edit then close the current edited form
   * @param : index, identifier
   * @retun : null
   */

  cancelEditRow(index, identifier){
    $(".editRowDisabled").attr("class","editRow");
    $(".deleteRowDisabled").attr("class","deleteRow");
    let currentEditedForm = document.querySelector(".row_id_"+identifier);
    //this.editableFormArray.splice(this.editableFormArray.indexOf(index), 1);
    this.editableFormArray = [];
    this.checkFormEnable(index); //TODO : need to recheck this function
    this.sharedService.userNavigation = true; //un block user navigation
    currentEditedForm.lastElementChild.lastElementChild.children[0].className = "editRow";

    document.querySelector("#searchButton").classList.remove("buttonDisabled");
    this.paginationDisabbled = false;
    
    this.serverData =[];
    

  }

  /*
   * On click delete row open a modal for confirmation
   * @param : content, userName
   * @retun : null
   */

    deleteRow(confirmModal, lsm,event) {
    if (event.target.className != "deleteRowDisabled") {
      this.modalService.open(confirmModal,{keyboard: false, backdrop: 'static', size:'lg', windowClass:'confirm-modal'}).result.then((result) => {
        this.closeResult = `Closed with: ${result}`;
      }, (reason) => {
        this.showLoader = true;

        this.systemManagerConfigService.deleteUserDetails(lsm.id, this.sharedService.createServiceToken())
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
                               this.message = "Network Config details deleted successfully!";
                               this.successModalBlock = this.modalService.open(this.successModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'success-modal'});
                          } else {                           
                              this.displayModel(jsonStatue.reason,"failureIcon");  
                          } 
                        }
                    }
                  },
                  error => {
                   //Please Comment while checkIn
                /*   setTimeout(() => {
                      this.showLoader = false;
                     let jsonStatue = {"reason":"N/W Config Details Deleted Successfully","sessionId":"9bbade93","serviceToken":"86860","status":"SUCCESS"};
                    if(jsonStatue.status == "SUCCESS"){
                      this.message = "Network Config details deleted successfully!";
                      this.successModalBlock = this.modalService.open(this.successModalRef,{keyboard: false, backdrop: "static",size:'lg',windowClass:"success-modal"});
                    } else {
                        this.displayModel(jsonStatue.reason,"failureIcon");  
                    }
                  }, 1000);  */
                  //Please Comment while checkIn
                   
                      //this.alertService.error(error);TODO : This need to implement
              });
      });
    }
  }


  /*
   * On click of update button in edit then send data to server and close the block
   * @param : null
   * @retun : null
   */

  updateEditRow(index, identifier, event){     
    if (this.programDetailsEntity !="" && this.programDetailsEntity != undefined &&  this.programDetailsEntity.programName) {
        this.validationData.rules.programName.required = false;

    } else {
        this.validationData.rules.programName.required = true;

    }  
    this.onChangeLoad();

    validator.performValidation(event, this.validationData, "save_update");
    setTimeout(() => { 
      if(this.isValidForm(event)){
          if (this.neTypeEntity.neType == 'VLSM') {
              if (this.serverData.filter(e => e.serverTypeEntity.serverType === 'VLSM MCMA').length > 0) {
                  this.validateServer = false;
              } else {
                  this.validateServer = true;
                  this.displayModel("Please add MCMA Server", "failureIcon");
              }
          } else {
              this.validateServer = false;
          }
          if (!this.validateServer) {
              this.showLoader = true;
              let currentEditedForm = event.target.parentNode.parentNode,
                  lsmDetails = {
                      "id": identifier,
                      "programDetailsEntity": this.programDetailsEntity,
                      "neTypeEntity": this.neTypeEntity,
                      "neMarket": currentEditedForm.querySelector("#neMarket").value,
                      "neName": currentEditedForm.querySelector("#neName").value,
                      "neVersionEntity": this.neVersionEntity ? this.neVersionEntity : null,//currentEditedForm.querySelector("#neVersion").value,
                      "neRelVersion": this.neRelVersion ? this.neRelVersion : null,
                      "neIp": currentEditedForm.querySelector("#neIp").value,
                      "neRsIp": currentEditedForm.querySelector("#neRsIp").value,
                      "neUserName": currentEditedForm.querySelector("#neUserName").value,
                      "nePassword": currentEditedForm.querySelector("#nePassword").value,
                      "loginTypeEntity": this.loginTypeEntity,
                      "status": currentEditedForm.querySelector("#status").value,
                      "remarks": currentEditedForm.querySelector("#remarks").value,
                      "neDetails": this.serverData,
                      "neUserPrompt": currentEditedForm.querySelector("#neUserPrompt").value,
                      "neSuperUserPrompt": currentEditedForm.querySelector("#neSuperUserPrompt").value
                  };


              this.systemManagerConfigService.updateLsmDetail(lsmDetails, this.sharedService.createServiceToken(),this.updateIp)
                  .subscribe(
                      data => {
                          let jsonStatue = data.json();
                          this.showLoader = false;

                          if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {

                              this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });

                          } else {

                              if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {
                                  if (jsonStatue.status == "SUCCESS") {
                                      this.message = "Network Config details updated successfully !";
                                      this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });

                                      //  this.displayModel("User details updated successfully !","successIcon");
                                      this.cancelEditRow(index, lsmDetails.id);
                                      // this.createForm = false;
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
                               this.message = "Network Config details updated successfully !";
                              this.successModalBlock = this.modalService.open(this.successModalRef,{keyboard: false, backdrop: "static",size:'lg',windowClass:"success-modal"});
                               // this.displayModel("User details updated successfully !","successIcon");
                                //this.createForm = false;
                                this.cancelEditRow(index,lsmDetails.id);
                              } else {
                                this.displayModel(jsonStatue.reason,"failureIcon");  
                              }
                        
                            }, 1000); */
                          //Please Comment while checkIn
                      });
          }
      }
      
      }, 0);
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

    checkServerFormEnable(index){
        let indexValue = this.editableServerFormArray.indexOf(index);
        return indexValue >= 0?true:false;
     }
   

  /* validates current submitted form is valid and free from errors
   * @param : pass the event
   * @retun : boolean
   */

  isValidForm(event){
    return ( $(event.target).parents("form").find(".error-border").length == 0) ? true : false;
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
                "page": page
            };

            this.paginationDetails = paginationDetails;
            this.pager = this.getPager(this.totalPages*this.pageSize, this.currentPage, this.pageRenge);
            this.paginationDisabbled = false;
            // Hide all the form/Table/Nodatafound5
            this.tableShowHide = false;
            this.showNoDataFound = false;
            this.getAllLSMDetails();


        }, 0);



    };

    onChangeLoad() {
        if ( this.neTypeEntity.neType == 'LSM'  || this.neTypeEntity.neType == 'VLSM' || this.neTypeEntity.neType == 'BSM' || this.neTypeEntity.neType == 'CSR' || this.neTypeEntity.neType == 'USM' || this.neTypeEntity.neType == 'AU' || this.neTypeEntity.neType == 'ACPF' || this.neTypeEntity.neType == 'AUPF' ) {
            this.validationData.rules.neMarket.required = true;
            this.validationData.rules.neVersion.required = true;
            this.validationData.rules.relVersion.required = true;
            this.validationData.rules.neRsIp.required = true;
          /*   this.validationData.rules.neRsIp["pattern"] = /^(?:[0-9]{1,3}\.){3}[0-9]{1,3}$/;
            this.validationData.messages.neRsIp["pattern"] ="Invalid IP Address";   */          
        } else {
            this.validationData.rules.neMarket.required =false;
            this.validationData.rules.neVersion.required = false;
            this.validationData.rules.relVersion.required = false;
            this.validationData.rules.neRsIp.required =false;   
           /*  delete this.validationData.rules.neRsIp.pattern;            
            delete this.validationData.messages.neRsIp.pattern; */
        }   
             
    }
    
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
        this.showNoDataFound = false;
        setTimeout(() => {
            this.showLoader = false;
            $("#dataWrapper").find(".scrollBody").scrollLeft(0);
            this.getAllLSMDetails();
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

  closeModel(){
      this.successModalBlock.close();
      this.ngOnInit();
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
   * On click sort header in table then sort the data ascending and decending
   * @param : columnName, event and current Index
   * @retun : null
   */

  changeSorting(predicate, event, index,tablename, parent=""){
      if(tablename=='SystemManagerDetails')
      {
        this.sharedService.dynamicSort(predicate, event, index, this.tableData.networkConfigList, parent);

      }
      else if(tablename=='serverTableDetails')
      {
        this.sharedService.dynamicSort(predicate, event, index, this.serverData, parent);

      }
  }
  uploadStateTar(event)
  {
    const formdata = new FormData();
    let files: FileList = this.filePostRef.nativeElement.files,
        validFileType = false;    
    for (var i = 0; i < files.length; i++) {        
        if(files[i].name.indexOf('.xlsx') >= 0){
            validFileType = true;
            formdata.append( "networkConfigFile", files[0]);
            formdata.append(files[i].name, files[i]);
        }else{
            validFileType = false;
            this.displayModel("Invalid file type..... Supports .xlsx format", "failureIcon");
        }
    }
    
   if(validFileType){
    setTimeout(() => {
      this.showLoader = true;
      this.systemManagerConfigService.uploadFile(formdata,this.sharedService.createServiceToken())
          .subscribe(
              data => {
                  let jsonStatue = data.json();

                  if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
                      this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, {keyboard: false,backdrop: 'static',size: 'lg',windowClass: 'session-modal'});
                  } else {
                      if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {
                          if (jsonStatue.status == "SUCCESS") {
                              this.showLoader = false;
                              this.filePostRef.nativeElement.value = "";
                              //*********************************************
                              this.displayModel("N/W Config Details Imported Successfully!","successIcon");
                              this.ngOnInit(); 
                          } else {
                              this.showLoader = false;
                              this.displayModel(jsonStatue.reason, "failureIcon");
                              this.filePostRef.nativeElement.value = "";
                          }
                      }
                  }
              }, 
              error => {
                  //Please Comment while checkIn

                   /* setTimeout(() => {
                      this.showLoader = false;
                      let jsonStatue = {"sessionId": "e9004f23","reason": "N/W Config Details Imported Successfully!","status": "SUCCESS","serviceToken": "64438"};

                      if (jsonStatue.status == "SUCCESS") {
                          this.showLoader = false;
                          // UploadFileStatus *******************************************
                          this.filePostRef.nativeElement.value = "";
                          //*********************************************
                          this.displayModel("N/W Config Details Imported Successfully!" ,"successIcon");
                          this.ngOnInit(); 
                      } else {
                          this.showLoader = false;
                          this.displayModel(jsonStatue.reason, "failureIcon");
                          this.filePostRef.nativeElement.value = "";
                      }

                  }, 100); */
                   //Please Comment while checkIn   
              });
  
    }, 0);
   }
  }  
    
  downloadFile() {
    this.showLoader = true;
    this.systemManagerConfigService.downloadFile(this.sharedService.createServiceToken(), this.searchCriteria, this.searchStatus)
        .subscribe(
            data => {
                this.showLoader = false;
                let blob = new Blob([data["_body"]], {
                    type: "application/octet-stream"
                });

                FileSaver.saveAs(blob,"NetworkConfig.xlsx");              

            },
            error => {
                //Please Comment while checkIn
               /*  let jsonStatue: any = {"sessionId":"506db794","reason":"Download Failed","status":"SUCCESS","serviceToken":"63524"};
                data => {
                     this.showLoader = false;
                    let blob = new Blob([data["_body"]], {
                        type: "application/octet-stream"
                    });

                    FileSaver.saveAs(blob,"NetworkConfig.xlsx"); 
                }
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
  isDisabled(){      
      if(this.serverCreateForm || this.editableServerFormArray.length >= 1 ){
          return true;
      }
      return false;
  }
  resetServerList(){
      this.serverList = [
          {
              "id": null,
              "step": "",
              "serverTypeEntity":"",
              "serverName": "",
              "serverIp": "",
              "serverUserName": "",
              "serverPassword": "",
              "serverCnfrmPswd": "",
              "loginTypeEntity":"",
              "path": ""
          }
      ]
  }
  addServerForm(event){ 
      if(!this.isDisabled()) {
        this.resetServerList();
        // Close if any edit form is opend
        this.editableServerFormArray = [];
      
        let currentTarget = event.target;
        this.sharedService.userNavigation = false;//block user navigation
        this.serverCreateForm = true;
      }
  }
  saveServerDetails(event,index){
   
    validator.performValidation(event, this.serverValidations, "save_update"); 
    this.serverValidations.rules["step"].customfunction = false;
    //Search for duplicate step in server
    for (let i = 0; i < this.serverData.length; i++) {
        if (this.serverData[i].step == this.serverList[index].step) {
            this.serverValidations.rules["step"].customfunction = true;            
            break;
        }
    }      
    setTimeout(() => {
        if (this.isValidForm(event)) {             
            this.serverCreateForm = false;           
            this.serverData.push(this.serverList[index]); 
            this.serverTable = true;
            this.currEditScript = null;            
          
        }
    },1000);   
  
  }
  cancelServerDetails(event){
    this.serverCreateForm = false; 
    this.isDisabled();    
  }
  editServerRow(event, key, index) { 
      this.cancelServerDetails(event);
      let editState: any = event.target;
      $("#serverTableData .editRowDisabled").attr("class", "editRow");
      $("#serverTableData .deleteRowDisabled").attr("class", "deleteRow");
      if (editState.className != "editRowDisabled") { //enable click only if it is enabled
          editState.className = "editRowDisabled";    
          $(editState).next().attr("class", "deleteRowDisabled") // Disable delete on edit row       
          // To enable one edit form at a time in table
          if (this.editableServerFormArray.length >= 1) {
              this.editableServerFormArray = [];
              this.editableServerFormArray.push(index);
          } else {
              this.editableServerFormArray.push(index);
          }
          this.sharedService.userNavigation = false; //block user navigation
          
          setTimeout(() => {
              this.serverType =key.serverTypeEntity.serverType
              this.serverLoginType = key.loginTypeEntity.loginType;
              this.editRowInTable(event, key, index); 
               //map validation for fields
            //  validator.performValidation(event, this.validationData, "edit");
         
          }, 0);
          
          
      }
   
    }
    deleteServerRow(confirmModal,key,index,event){ 
        if (event.target.className != "deleteRowDisabled") {       
        this.modalService.open(confirmModal,{keyboard: false, backdrop: 'static', size:'lg', windowClass:'confirm-modal'}).result.then((result) => {
            this.closeResult = `Closed with: ${result}`;
          }, (reason) => {                
                this.serverData.splice(index,1);           
          });
        }
    }
    updateServerEditRow(event,index,identifier){
        let currentEditedForm = event.target.parentNode.parentNode;
        this.serverValidations.rules["step"].customfunction = false;
        //Search for duplicate step in server
        for (let i = 0; i < this.serverData.length; i++) {
            if (index != i && this.serverData[i].step == currentEditedForm.querySelector("#step").value) {
                this.serverValidations.rules["step"].customfunction = true;            
                break;
            }
        }   
        validator.performValidation(event, this.serverValidations, "save_update");
        setTimeout(() => { 
           if(this.isValidForm(event)){               
                let userDetails = {     
                    "id": identifier,
                    "step": currentEditedForm.querySelector("#step").value,
                    "serverTypeEntity": {
                        "serverType": currentEditedForm.querySelector("#serverType").value,
                        "id": $("#serverType option:selected")[0].id
                    },
                    "serverName": currentEditedForm.querySelector("#serverName").value,
                    "serverIp": currentEditedForm.querySelector("#serverIp").value,
                    "serverUserName": currentEditedForm.querySelector("#serverUserName").value,
                    "serverPassword": currentEditedForm.querySelector("#serverPassword").value,
                    "loginTypeEntity": {
                        "loginType": currentEditedForm.querySelector("#serverLoginType").value,
                        "id": $("#serverLoginType option:selected")[0].id
                    },
                    "path": currentEditedForm.querySelector("#path").value,
                    "userPrompt": currentEditedForm.querySelector("#userPrompt").value,
                    "superUserPrompt": currentEditedForm.querySelector("#superUserPrompt").value,
                };
                this.serverData[index] = userDetails;                
                this.cancelServerEditRow(index,userDetails.id);
             
            }
        },1000);
        
    }
    cancelServerEditRow(index,identifier){    
        $("#serverTableData .editRowDisabled").attr("class","editRow");     
        $("#serverTableData .deleteRowDisabled").attr("class","deleteRow");     
        let currentEditedForm = document.querySelector("#serverTableData .row_id_"+identifier);        
        this.editableServerFormArray = [];
        this.checkServerFormEnable(index); //TODO : need to recheck this function
        this.sharedService.userNavigation = true; //un block user navigation
        currentEditedForm.lastElementChild.lastElementChild.children[0].className = "editRow";
      
      }
      compareFn(o1: any, o2: any) {
        return o1 && o2 ? o1.id === o2.id : o1 === o2;

    }
    
}
