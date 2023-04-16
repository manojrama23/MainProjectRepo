import { ViewContainerRef, Input, ElementRef, Renderer, Component, OnInit, ViewChild } from '@angular/core';
import { Router} from '@angular/router';
import { NgbModal, ModalDismissReasons} from '@ng-bootstrap/ng-bootstrap';
import { DashboardService } from '../services/dashboard.service';
import { SharedService } from '../services/shared.service';
import { Observable, Subscription } from 'rxjs';
import { filter, map, mergeMap} from 'rxjs/operators';
import { icon, latLng, Map, marker, point, polyline, tileLayer, featureGroup, LatLngBounds } from 'leaflet';
/* import {featureGroup, latLng, tileLayer, polygon, marker, Icon, LatLngBounds} from 'leaflet'; */
import 'rxjs/add/observable/timer';
import { validator } from '../validation';
import {platformBrowserDynamic} from '@angular/platform-browser-dynamic';
//import { IMultiSelectOption, IMultiSelectSettings, IMultiSelectTexts} from 'angular-2-dropdown-multiselect';
import { DatePipe } from '@angular/common';

import * as $ from 'jquery';
declare var Chart: any;
declare var AmCharts : any;
@Component({
  selector: 'rct-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
  providers: [DashboardService]
})
  export class DashboardComponent implements OnInit {
    currentUser: any;
    selectedCustIs:any;
    cpuData: any;
    customerIcons: any;
  //chartShowHide :boolean = false;
  sub: Subscription;
  showLoader:boolean = true;
  showInnerLoader:boolean = false;
  dashBoardData:any;
  closeResult:string;
  privilegeSetting : object;
  noDataVisibility :boolean = false;
  showModelMessage: boolean = false;
  messageType: any;
  modelData :any;
  sessionExpiredModalBlock : any; // Helps to close/open the model window
  successModalBlock : any;
  message : any;
  details = [];
  detailsText :boolean = true;
  chartArea :boolean = true;
  
  totalMemory : any;
  freeMemory : any;
  diskTotalSpace : any;
  diskFreeSpace : any;
  usedMemory : any;
  freeMemoryPercentage : any;
  usedMemoryPercentage : any;
  usedDiskSpace : any;
  freeDiskSpacePercentage : any;
  usedDiskSpacePercentage : any;
  activeUsersCount:any;
  activeSessionsCount:any;
  activeUsersList: any;
  supportedCustomer:any;
  frequencies =[];

  completedSites: any;
  notCompletedSites: any;
  inProgressSites: any;
  market1: any;
  market2: any;
  market3: any;
  market4: any;
  market5: any;
  market6: any;
  market7: any;
  market8: any;
  market9: any;
  tech1: any;
  tech2: any;
  tech3: any;
  tech4: any;
  tech5: any;
  scheduledPercentage: any;
  cancelledPercentage:any;
  
  healthBlock: boolean = false;
  candiBlock: boolean = false;

  scriptDataOutput: any;
  custID:any;

  map: any;
  mapMarker: any;
  options = {};
  layer = [];
  mapCenter:any;
  mapViewModalBlock: any;

  searchCustomerList: any = [];
  marketList: any = [];
  allMarketList: any = [];
  max = new Date();
  fromDt:any;
  toDate:any;
  searchStatus: any = "load";
  searchCriteria: any;
  errMessage:boolean= false;
  errMessageDate:boolean= false;
  maxSearchDateRange:number = 30;
  activeUserBlock: any;
  
  @ViewChild('confirmModal') confirmModalRef: ElementRef;
  @ViewChild('sessionExpiredModal') sessionExpiredModalRef: ElementRef;
  @ViewChild('successModal') successModalRef: ElementRef;
  @ViewChild('healthTab') healthTabRef: ElementRef;
  @ViewChild('candiTab') candiTabRef: ElementRef;

  
  constructor(
    private element: ElementRef,
    private renderer: Renderer,
    private router:Router,
    private modalService: NgbModal,
    private dashboardService: DashboardService,
    private sharedService: SharedService,
    private datePipe: DatePipe
  ) {}

  ngOnInit() {
    this.healthBlock=false;
    this.candiBlock=true;
    this.showLoader = true;
    this.setMenuHighlight("candi");
    this.currentUser = JSON.parse(sessionStorage.loginDetails).userGroup;
    this.populateFrequencyArr();
    this.dashboardService.getDashBoardDetails( this.sharedService.createServiceToken() )
        .subscribe(
            data => {
                setTimeout(() => { 
                  let jsonStatue = data.json();

                  this.dashBoardData = data.json();
                  this.showLoader = false;
                  
                  
                      if(jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User"){
                     
                       this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                     
                      } else {

                        if( this.sharedService.isvalidServiceToken( parseInt(data.json().serviceToken,10) ) || data.json().sessionId == "408"){
                          if(jsonStatue.status == "SUCCESS"){
                            this.selectedCustIs = this.dashBoardData.customerList[0].id;                            
                            if(!this.dashBoardData.dashBoardCountDetails){
                              //this.chartShowHide = false;
                              this.noDataVisibility = true;
                            }else{
                              this.totalMemory = Math.round(this.dashBoardData.dashBoardCountDetails.totalMemory.replace('GB' ,'') * 10 ) / 10 + ' GB';
                              this.freeMemory = Math.round(this.dashBoardData.dashBoardCountDetails.freeMemory.replace('GB' ,'') * 10 ) / 10 + ' GB';
                              this.usedMemory =  Math.round(this.dashBoardData.dashBoardCountDetails.usedMemory.replace('GB' ,'') * 10 ) / 10 + ' GB';
                              this.freeMemoryPercentage = this.dashBoardData.dashBoardCountDetails.freeMemoryPercentage;
                              this.usedMemoryPercentage = this.dashBoardData.dashBoardCountDetails.usedMemoryPercentage;                      
                              this.diskTotalSpace = Math.round(this.dashBoardData.dashBoardCountDetails.diskTotalSpace.replace('GB' ,'') * 10 ) / 10 + ' GB';
                              this.diskFreeSpace = Math.round(this.dashBoardData.dashBoardCountDetails.diskFreeSpace.replace('GB' ,'') * 10 ) / 10 + ' GB';
                              this.usedDiskSpace = Math.round(this.dashBoardData.dashBoardCountDetails.usedDiskSpace.replace('GB' ,'') * 10 ) / 10 + ' GB';
                              this.freeDiskSpacePercentage = this.dashBoardData.dashBoardCountDetails.freeDiskSpacePercentage;
                              this.usedDiskSpacePercentage = this.dashBoardData.dashBoardCountDetails.usedDiskSpacePercentage;
                              this.activeSessionsCount = this.dashBoardData.dashBoardCountDetails.activeSessionsCount;
                              this.activeUsersCount = this.dashBoardData.dashBoardCountDetails.activeUsersCount;
                              this.supportedCustomer = this.dashBoardData.customerList.length;
                              this.customerIcons = this.dashBoardData.customerList;
                              this.scheduledPercentage=this.dashBoardData.dashBoardCountDetails.scheduledPercentage; 
                              this.cancelledPercentage=this.dashBoardData.dashBoardCountDetails.cancelledPercentage;                             

                              //this.chartShowHide = true;
                              this.noDataVisibility = false;
                              /* this.showDiskUsageChart(this.usedDiskSpacePercentage, this.freeDiskSpacePercentage);
                              this.showMemoryUsageChart(this.usedMemoryPercentage, this.freeMemoryPercentage);
                              this.showNECommissionChart(this.dashBoardData.barChartData.datasets,this.dashBoardData.barChartData.labels);
                              this.showCommissioningReport(this.dashBoardData.repChartData.datasets,this.dashBoardData.repChartData.labels); */
                              this.candiTabBind();

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
                //this.dashBoardData = {"sessionId":"aea3843c","serviceToken":"73449","status":"SUCCESS","barChartData":{"datasets":[{"label":"4G","backgroundColor":"green","data":["5","0","0"]},{"label":"5G","backgroundColor":"blue","data":["0","5","5"]},{"label":"6G_2","backgroundColor":"aqua","data":["0","0","0"]},{"label":"7G","backgroundColor":"yellow","data":["0","0","0"]},{"label":"3G","backgroundColor":"orange","data":["0","0","0"]}],"labels":["Verizon","AT&T","Sprint"]},"dashBoardCountDetails":{"activeUsersCount":"1","totalMemory":"8.310632448 GB","freeMemory":"0.133791744 GB","usedMemory":"8.176840704 GB","freeMemoryPercentage":"1.6098864 %","usedMemoryPercentage":"98.390114 %","diskTotalSpace":"968.231333888 GB","diskFreeSpace":"895.420444672 GB","usedDiskSpace":"72.810889216 GB","freeDiskSpacePercentage":"92.48001 %","usedDiskSpacePercentage":"7.519989 %"}};
                this.dashBoardData = {"reasonsChartData":{"datasets":[{"label":"","backgroundColor":"#5678ff","data":["580","648","560","706","866"],"percData":null}],"labels":["reason1","reason2","reason3","reason4","reason5"]},"marketBarChartData":{"2":{"datasets":[{"label":"Planned","backgroundColor":"#00a9d4","data":["4000","2400"],"percData":["100","100"]},{"label":"Migrated","backgroundColor":"#88ce00","data":["1762","813"],"percData":["44.05","33.875"]}],"labels":["New England","Upstate New York"]},"3":{"datasets":[{"label":"Planned","backgroundColor":"#00a9d4","data":[],"percData":[]},{"label":"Migrated","backgroundColor":"#88ce00","data":[],"percData":[]}],"labels":[]},"4":{"datasets":[{"label":"Planned","backgroundColor":"#00a9d4","data":["224","1252","24"],"percData":["100","100","0"]},{"label":"Migrated","backgroundColor":"#88ce00","data":["26","98","0"],"percData":["11.607142","7.827476","0"]}],"labels":["West","Central","FIT"]}},"repChartData":{"datasets":[{"label":"","backgroundColor":"#108369","data":["580","648","560","706","866"],"percData":null}],"labels":["BRVZN 4G Legacy","VZN-4G","SPT-4G","VZN-5G","AT&T-4G"]},"customerList":[{"id":2,"customerName":"Verizon","iconPath":"/customer/verizon_ 03222019_18_53_36_icon.png","status":"Active","customerShortName":"VZN","customerDetails":[{"id":1,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"VZN-4G-CDU30","programDescription":"CDU30","status":"Active","creationDate":"2019-03-30T12:04:43.000+0000","createdBy":"superadmin"},{"id":2,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"VZN-4G-VLSM","programDescription":"VLSM","status":"Active","creationDate":"2019-03-22T10:03:50.000+0000","createdBy":"superadmin"},{"id":11,"networkTypeDetailsEntity":{"id":3,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#0e183b"},"programName":"VZN-5G-NR","programDescription":"NR","status":"Active","creationDate":"2019-03-22T13:23:58.000+0000","createdBy":"superadmin"}]},{"id":3,"customerName":"AT&T","iconPath":"/customer/at&t_ 03222019_16_12_35_icon.png","status":"Active","customerShortName":"ATT","customerDetails":[{"id":12,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"ATT-4G-CBRS","programDescription":"CBRS","status":"Active","creationDate":"2019-03-22T10:43:18.000+0000","createdBy":"superadmin"},{"id":13,"networkTypeDetailsEntity":{"id":3,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#0e183b"},"programName":"ATT-5G-PICO","programDescription":"PICO","status":"Active","creationDate":"2019-03-22T10:43:41.000+0000","createdBy":"superadmin"}]},{"id":4,"customerName":"Sprint","iconPath":"/customer/sprint_ 03222019_18_55_22_icon.png","status":"Active","customerShortName":"SPT","customerDetails":[{"id":3,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-MIMO","programDescription":"MIMO","status":"Active","creationDate":"2019-04-04T13:32:06.000+0000","createdBy":"superadmin"},{"id":4,"networkTypeDetailsEntity":{"id":2,"networkType":"4G","createdBy":"superadmin","caretedDate":"2019-02-15T07:54:21.000+0000","status":"Active","remarks":"","networkColor":"#5161ac"},"programName":"SPT-4G-CDU30","programDescription":"CDU30","status":"Active","creationDate":"2019-04-04T13:32:44.000+0000","createdBy":"superadmin"},{"id":6,"networkTypeDetailsEntity":{"id":3,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-01-23T05:33:34.000+0000","status":"Active","remarks":"","networkColor":"#0e183b"},"programName":"SPT-5G-FPGA","programDescription":"FPGA","status":"Active","creationDate":"2019-03-22T09:51:03.000+0000","createdBy":"superadmin"}]}],"sessionId":"ad31969d","serviceToken":"51642","barChartData":{"datasets":[{"label":"Planned","backgroundColor":"#00a9d4","data":["6400","0","1500"],"percData":["100","0","100"]},{"label":"Migrated","backgroundColor":"#fdc844","data":["2575","0","124"],"percData":["40.234375","0","8.266666"]}],"labels":["Verizon","AT&T","Sprint"]},"dashBoardCountDetails":{"activeUsersCount":"1","totalMemory":"8.227110912 GB","freeMemory":"0.205533184 GB","usedMemory":"8.021577728 GB","freeMemoryPercentage":"2.4982426 %","usedMemoryPercentage":"97.501755 %","diskTotalSpace":"399.76822784 GB","diskFreeSpace":"318.104518656 GB","usedDiskSpace":"81.663709184 GB","freeDiskSpacePercentage":"79.572235 %","usedDiskSpacePercentage":"20.427763 %","scheduledPercentage":"58","cancelledPercentage":"42"},"status":"SUCCESS"};
                  if(this.dashBoardData.sessionId == "408" || this.dashBoardData.status == "Invalid User"){
                       this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                      } 
                      this.selectedCustIs = this.dashBoardData.customerList[0].id;
                    if(!this.dashBoardData.dashBoardCountDetails){
                      //this.chartShowHide = false;
                      this.noDataVisibility = true;
                    }else{
                      this.totalMemory = Math.round(this.dashBoardData.dashBoardCountDetails.totalMemory.replace('GB' ,'') * 10 ) / 10 + ' GB';
                      this.freeMemory = Math.round(this.dashBoardData.dashBoardCountDetails.freeMemory.replace('GB' ,'') * 10 ) / 10 + ' GB';
                      this.usedMemory =  Math.round(this.dashBoardData.dashBoardCountDetails.usedMemory.replace('GB' ,'') * 10 ) / 10 + ' GB';
                      this.freeMemoryPercentage = this.dashBoardData.dashBoardCountDetails.freeMemoryPercentage;
                      this.usedMemoryPercentage = this.dashBoardData.dashBoardCountDetails.usedMemoryPercentage;                      
                      this.diskTotalSpace = Math.round(this.dashBoardData.dashBoardCountDetails.diskTotalSpace.replace('GB' ,'') * 10 ) / 10 + ' GB';
                      this.diskFreeSpace = Math.round(this.dashBoardData.dashBoardCountDetails.diskFreeSpace.replace('GB' ,'') * 10 ) / 10 + ' GB';
                      this.usedDiskSpace = Math.round(this.dashBoardData.dashBoardCountDetails.usedDiskSpace.replace('GB' ,'') * 10 ) / 10 + ' GB';
                      this.freeDiskSpacePercentage = this.dashBoardData.dashBoardCountDetails.freeDiskSpacePercentage;
                      this.usedDiskSpacePercentage = this.dashBoardData.dashBoardCountDetails.usedDiskSpacePercentage;
                      this.activeUsersCount = this.dashBoardData.dashBoardCountDetails.activeUsersCount;
                      this.supportedCustomer = this.dashBoardData.customerList.length; 
                      this.customerIcons = this.dashBoardData.customerList;
                      this.scheduledPercentage=this.dashBoardData.dashBoardCountDetails.scheduledPercentage;
                      this.cancelledPercentage=this.dashBoardData.dashBoardCountDetails.cancelledPercentage;

                      //this.chartShowHide = true;
                      this.noDataVisibility = false;
                      //this.healthTabBind();
                      this.candiTabBind();
                    }

              }, 1000); */
              //Please Comment while checkIn
        });
    /*setTimeout(() => { 
    	this.showLoader = false;
    },1000); */
      
  }

  onChangeDate() {
      this.errMessage = false;
      this.errMessageDate = false;
      if (this.fromDt && this.toDate) {
          this.errMessage = false;
          let timeDiff = this.toDate.getTime() - this.fromDt.getTime();
          let daysDiff = timeDiff / (1000 * 3600 * 24);
          if(daysDiff > this.maxSearchDateRange) {
              console.log("Please select date range maximum 30 days!! " + daysDiff);
              this.errMessageDate = true;
          }
          else {
              console.log("Date diff is = " + daysDiff);
              this.errMessageDate = false;
          }
      } else {
          this.errMessage = true;
      }
  }

  getLayerInfo(markers) {
      // let jsonData = {"reason":null,"pageCount":2,"markers":[{"id":1,"lat":13.041664,"lon":77.6134879,"info":"Hello All - 1"},{"id":2,"lat":13.0459521,"lon":77.616852,"info":"Hello All - 2"},{"id":2,"lat":13.0464301,"lon":77.6181657,"info":"Hello All - 3"},{"id":2,"lat":13.0408386,"lon":77.6198892,"info":"Hello All - 4"}],"sessionId":"832332df","serviceToken":"81591","status":"SUCCESS"};

      let layers = [];

      for (let i = 0; i < markers.length; i++) {
          layers.push(marker([markers[i].latitude, markers[i].longitude], {
              icon: icon({
                  iconSize: [25, 41],
                  iconAnchor: [13, 41],
                  iconUrl: 'leaflet/marker-icon.png',//./assets/images/favicon.png
                  shadowUrl: 'leaflet/marker-shadow.png',
                  tooltipAnchor: [15, -20]
              }),
              riseOnHover: true
          }).bindTooltip(markers[i].information, {
              permanent: false,
              opacity: 0.8,
              direction: 'auto'
          }));
      }
      return layers;
  }

  showMapView(content) {
      this.getMapData(content);
  }

  searchMapView(event) {
      if (!this.errMessage && !this.errMessageDate) {
          let currentForm = event.target.parentNode.parentNode.parentNode,
              searchData = {
                  "searchStartDate": currentForm.querySelector("#fromDate").value,
                  "searchEndDate": currentForm.querySelector("#toDate").value,
                  "customerId" : $("#searchCustomer" + " option:selected").attr("id") ? parseInt($("#searchCustomer" + " option:selected").attr("id")) : null,
                  "market": currentForm.querySelector("#searchMarket").value
              };
          this.gerSearchedMapData(searchData);
      }
  }

  getMapData(content, fromDate = null, toDate = null) {
      this.options = {
          layers: [
              tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                  attribution: ''
              })
          ],
          zoom: 7,
          center: latLng([38.82292, -104.75962])
      };

      this.showLoader = true;

      this.dashboardService.getMapData(this.sharedService.createServiceToken(), this.searchStatus, this.searchCriteria)
          .subscribe(
              data => {
                  setTimeout(() => {
                      this.showLoader = false;
                      let jsonStatue = data.json();
                      if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
                          this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: "static", size: "lg", windowClass: "session-modal" });
                      } else {
                          if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {
                              this.fromDt = new Date( jsonStatue.searchStartDate);
                              this.toDate = new Date(jsonStatue.searchEndDate);
                              this.searchCustomerList = jsonStatue.customerList;
                              this.marketList = jsonStatue.market;
                              this.allMarketList = jsonStatue.market;
                              this.layer = this.getLayerInfo(jsonStatue.markers);
                              this.mapViewModalBlock = this.modalService.open(content, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView' });
                          }
                      }
                  }, 1000);
              },
              error => {

                  //Please Comment while checkIn
                  /* setTimeout(() => {
                      //Data
                      let jsonStatue = {"searchStartDate":"10/25/2018","searchEndDate":"09/24/2019","sessionId":"2a025ab8","serviceToken":"75706","status":"SUCCESS","markers":[{"latitude":"41.90767","information":"ENB NAME: CHCBILGQBBULTE0517416<br/>CELL ID: 3<br/>N/W TYPE: 4G<br/>MARKET: Chicago<br/>LATITUDE: 41.90767<br/>LONGITUDE: -87.62804<br/>COMMISSION DATE: 17/09/2019","longitude":"-87.62804"},{"latitude":"41.8897","information":"ENB NAME: CHCGILGHBBULTE0516796<br/>CELL ID: 3<br/>N/W TYPE: 4G<br/>MARKET: Chicago<br/>LATITUDE: 41.8897<br/>LONGITUDE: -87.63752<br/>COMMISSION DATE: 17/08/2019","longitude":"-87.63752"},{"latitude":"41.8707","information":"ENB NAME: CHCYILXYBBULTE0517483<br/>CELL ID: 3<br/>N/W TYPE: 4G<br/>MARKET: Chicago<br/>LATITUDE: 41.8707<br/>LONGITUDE: -87.66581<br/>COMMISSION DATE: 17/09/2019","longitude":"-87.66581"},{"latitude":"41.89846","information":"ENB NAME: CHCJILEFBBULTE0516792<br/>CELL ID: 3<br/>N/W TYPE: 4G<br/>MARKET: Chicago<br/>LATITUDE: 41.89846<br/>LONGITUDE: -87.62167<br/>COMMISSION DATE: 17/08/2019","longitude":"-87.62167"},{"latitude":"41.84719","information":"ENB NAME: CHCIILUMBBULTE0516271<br/>CELL ID: 3<br/>N/W TYPE: 4G<br/>MARKET: Chicago<br/>LATITUDE: 41.84719<br/>LONGITUDE: -87.62455<br/>COMMISSION DATE: 17/08/2019","longitude":"-87.62455"},{"latitude":"41.88444","information":"ENB NAME: CHCVILECBBULTE0517180<br/>CELL ID: 3<br/>N/W TYPE: 4G<br/>MARKET: Chicago<br/>LATITUDE: 41.88444<br/>LONGITUDE: -87.67699<br/>COMMISSION DATE: 17/09/2019","longitude":"-87.67699"},{"latitude":"41.85536","information":"ENB NAME: CHDDIL09BBULTE0516824<br/>CELL ID: 3<br/>N/W TYPE: 4G<br/>MARKET: Chicago<br/>LATITUDE: 41.85536<br/>LONGITUDE: -87.61895<br/>COMMISSION DATE: 17/09/2019","longitude":"-87.61895"},{"latitude":"41.8691","information":"ENB NAME: CHCIILQYBBULTE0517516<br/>CELL ID: 3<br/>N/W TYPE: 4G<br/>MARKET: Chicago<br/>LATITUDE: 41.8691<br/>LONGITUDE: -87.69329<br/>COMMISSION DATE: 17/09/2019","longitude":"-87.69329"},{"latitude":"41.87627","information":"ENB NAME: CHCGILGCBBULTE0516267<br/>CELL ID: 3<br/>N/W TYPE: 4G<br/>MARKET: Chicago<br/>LATITUDE: 41.87627<br/>LONGITUDE: -87.63314<br/>COMMISSION DATE: 17/08/2019","longitude":"-87.63314"},{"latitude":"41.87906","information":"ENB NAME: CHCGILLKBBULTE0517432<br/>CELL ID: 3<br/>N/W TYPE: 4G<br/>MARKET: Chicago<br/>LATITUDE: 41.87906<br/>LONGITUDE: -87.63106<br/>COMMISSION DATE: 17/09/2019","longitude":"-87.63106"},{"latitude":"41.85551","information":"ENB NAME: CHCJIL08BBULTE0517435<br/>CELL ID: 3<br/>N/W TYPE: 4G<br/>MARKET: Chicago<br/>LATITUDE: 41.85551<br/>LONGITUDE: -87.69148<br/>COMMISSION DATE: 17/09/2019","longitude":"-87.69148"},{"latitude":"41.87823","information":"ENB NAME: CHCGILHFBBULTE0517434<br/>CELL ID: 3<br/>N/W TYPE: 4G<br/>MARKET: Chicago<br/>LATITUDE: 41.87823<br/>LONGITUDE: -87.63733<br/>COMMISSION DATE: 17/09/2019","longitude":"-87.63733"},{"latitude":"41.88512","information":"ENB NAME: CHCIILWRBBULTE0516383<br/>CELL ID: 3<br/>N/W TYPE: 4G<br/>MARKET: Chicago<br/>LATITUDE: 41.88512<br/>LONGITUDE: -87.63721<br/>COMMISSION DATE: 17/08/2019","longitude":"-87.63721"},{"latitude":"41.87676","information":"ENB NAME: CHCJILOBBBULTE0516263<br/>CELL ID: 3<br/>N/W TYPE: 4G<br/>MARKET: Chicago<br/>LATITUDE: 41.87676<br/>LONGITUDE: -87.62958<br/>COMMISSION DATE: 17/09/2019","longitude":"-87.62958"},{"latitude":"41.89502","information":"ENB NAME: CHCJILEZBBULTE0517431<br/>CELL ID: 3<br/>N/W TYPE: 4G<br/>MARKET: Chicago<br/>LATITUDE: 41.89502<br/>LONGITUDE: -87.63547<br/>COMMISSION DATE: 17/09/2019","longitude":"-87.63547"},{"latitude":"41.88278","information":"ENB NAME: CHCGILYZBBULTE0516265<br/>CELL ID: 3<br/>N/W TYPE: 4G<br/>MARKET: Chicago<br/>LATITUDE: 41.88278<br/>LONGITUDE: -87.64485<br/>COMMISSION DATE: 17/08/2019","longitude":"-87.64485"},{"latitude":"41.89378","information":"ENB NAME: CHCUILRZBBULTE0517298<br/>CELL ID: 3<br/>N/W TYPE: 4G<br/>MARKET: Chicago<br/>LATITUDE: 41.89378<br/>LONGITUDE: -87.6272<br/>COMMISSION DATE: 17/09/2019","longitude":"-87.6272"},{"latitude":"41.89209","information":"ENB NAME: CHCIILZKBBULTE0516260<br/>CELL ID: 3<br/>N/W TYPE: 4G<br/>MARKET: Chicago<br/>LATITUDE: 41.89209<br/>LONGITUDE: -87.63321<br/>COMMISSION DATE: 17/08/2019","longitude":"-87.63321"},{"latitude":"41.88939","information":"ENB NAME: CHCKILTWBBULTE0516261<br/>CELL ID: 3<br/>N/W TYPE: 4G<br/>MARKET: Chicago<br/>LATITUDE: 41.88939<br/>LONGITUDE: -87.65146<br/>COMMISSION DATE: 17/08/2019","longitude":"-87.65146"},{"latitude":"41.89639","information":"ENB NAME: CHCXIL85BBULTE0516938<br/>CELL ID: 3<br/>N/W TYPE: 4G<br/>MARKET: Chicago<br/>LATITUDE: 41.89639<br/>LONGITUDE: -87.61882<br/>COMMISSION DATE: 17/09/2019","longitude":"-87.61882"},{"latitude":"41.90419","information":"ENB NAME: CHCKILVRBBULTE0516419<br/>CELL ID: 3<br/>N/W TYPE: 4G<br/>MARKET: Chicago<br/>LATITUDE: 41.90419<br/>LONGITUDE: -87.6672<br/>COMMISSION DATE: 17/09/2019","longitude":"-87.6672"},{"latitude":"41.89027","information":"ENB NAME: CHCGIL55BBULTE0516937<br/>CELL ID: 3<br/>N/W TYPE: 4G<br/>MARKET: Chicago<br/>LATITUDE: 41.89027<br/>LONGITUDE: -87.6283<br/>COMMISSION DATE: 17/08/2019","longitude":"-87.6283"}],"customerList":[{"market":["New England","Upstate New York"],"id":2,"customerName":"Verizon"},{"market":["Milwaukee","Central Illinois","Chicago","Central Iowa","Indianpolis","Cleveland","Columbus","Toledo"],"id":4,"customerName":"Sprint"}],"market":["Milwaukee","Central Illinois","Chicago","Central Iowa","Indianpolis","Upstate New York","Cleveland","New England","Columbus","Toledo"]};
                      // No Data
                      // let jsonStatue = {"searchStartDate":"10/20/2018","searchEndDate":"09/19/2019","market":["Milwaukee","North Wisconsin","Central Illinois","Chicago"],"sessionId":"f0bf0c68","serviceToken":"87146","status":"SUCCESS","markers":[]};
                      this.showLoader = false;
                      if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
                          this.sessionExpiredModalBlock = this.modalService.open(
                              this.sessionExpiredModalRef,
                              {
                                  keyboard: false,
                                  backdrop: "static",
                                  size: "lg",
                                  windowClass: "session-modal"
                              }
                          );
                      } else {
                          this.fromDt = new Date(jsonStatue.searchStartDate);
                          this.toDate = new Date(jsonStatue.searchEndDate);
                          this.searchCustomerList = jsonStatue.customerList;
                          this.marketList = jsonStatue.market;
                          this.allMarketList = jsonStatue.market;
                          this.layer = this.getLayerInfo(jsonStatue.markers);
                          this.mapViewModalBlock = this.modalService.open(content, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView' });
                      }
                  }, 1000); */
                  //Please Comment while checkIn
              }
          );
  }

  gerSearchedMapData(searchData) {
      this.options = {
          layers: [
              tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                  attribution: ''
              })
          ],
          zoom: 7,
          center: latLng([38.82292, -104.75962])
      };
      
      this.showInnerLoader = true;

      if (searchData.searchStartDate && searchData.searchEndDate) {
          searchData.searchStartDate = this.datePipe.transform(searchData.searchStartDate, "MM/dd/yyyy"); // On change Date,Update Row
          searchData.searchEndDate = this.datePipe.transform(searchData.searchEndDate, "MM/dd/yyyy");
          this.searchStatus = "search";
      } else {
          searchData.searchStartDate = null;//Loading Page fromDate and toDate is null
          searchData.searchEndDate = null;
          this.searchStatus = "load";
      }

      this.searchCriteria = searchData;

      this.dashboardService.getMapData(this.sharedService.createServiceToken(), this.searchStatus, this.searchCriteria)
          .subscribe(
              data => {
                  setTimeout(() => {
                      this.showInnerLoader = false;
                      let jsonStatue = data.json();
                      if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
                          this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: "static", size: "lg", windowClass: "session-modal" });
                      } else {
                          if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {
                              this.layer = this.getLayerInfo(jsonStatue.markers);
              this.onMapReady(this.map);
                          }
                      }
                  }, 1000);
              },
              error => {

                  //Please Comment while checkIn
                  /* setTimeout(() => {
                      let jsonStatue = {"searchStartDate":"10/25/2018","searchEndDate":"09/24/2019","sessionId":"2a025ab8","serviceToken":"75706","markers":[{"latitude":"41.8691","information":"ENB NAME: CHCIILQYBBULTE0517516<br/>CELL ID: 3<br/>N/W TYPE: 4G<br/>MARKET: Chicago<br/>LATITUDE: 41.8691<br/>LONGITUDE: -87.69329<br/>COMMISSION DATE: 17/09/2019","longitude":"-87.69329"},{"latitude":"41.87627","information":"ENB NAME: CHCGILGCBBULTE0516267<br/>CELL ID: 3<br/>N/W TYPE: 4G<br/>MARKET: Chicago<br/>LATITUDE: 41.87627<br/>LONGITUDE: -87.63314<br/>COMMISSION DATE: 17/08/2019","longitude":"-87.63314"},{"latitude":"41.87906","information":"ENB NAME: CHCGILLKBBULTE0517432<br/>CELL ID: 3<br/>N/W TYPE: 4G<br/>MARKET: Chicago<br/>LATITUDE: 41.87906<br/>LONGITUDE: -87.63106<br/>COMMISSION DATE: 17/09/2019","longitude":"-87.63106"},{"latitude":"41.85551","information":"ENB NAME: CHCJIL08BBULTE0517435<br/>CELL ID: 3<br/>N/W TYPE: 4G<br/>MARKET: Chicago<br/>LATITUDE: 41.85551<br/>LONGITUDE: -87.69148<br/>COMMISSION DATE: 17/09/2019","longitude":"-87.69148"},{"latitude":"41.87823","information":"ENB NAME: CHCGILHFBBULTE0517434<br/>CELL ID: 3<br/>N/W TYPE: 4G<br/>MARKET: Chicago<br/>LATITUDE: 41.87823<br/>LONGITUDE: -87.63733<br/>COMMISSION DATE: 17/09/2019","longitude":"-87.63733"},{"latitude":"41.88512","information":"ENB NAME: CHCIILWRBBULTE0516383<br/>CELL ID: 3<br/>N/W TYPE: 4G<br/>MARKET: Chicago<br/>LATITUDE: 41.88512<br/>LONGITUDE: -87.63721<br/>COMMISSION DATE: 17/08/2019","longitude":"-87.63721"},{"latitude":"41.87676","information":"ENB NAME: CHCJILOBBBULTE0516263<br/>CELL ID: 3<br/>N/W TYPE: 4G<br/>MARKET: Chicago<br/>LATITUDE: 41.87676<br/>LONGITUDE: -87.62958<br/>COMMISSION DATE: 17/09/2019","longitude":"-87.62958"}],"status":"SUCCESS","market":["Milwaukee","North Wisconsin","Central Illinois","Chicago","Central Iowa","West Iowa/Nebraska","buffalo","Cincinnati","Indianpolis","Upstate New York","Cleveland","West Michigan","Ft. Wayne/South Bend","Western Pennsylvania","New England","Columbus","East Michigan","Toledo","Central Pennsylvania","Pittsburgh"],"customerList":[{"market":["New England","Upstate New York"],"id":2,"customerName":"Verizon"},{"market":["Milwaukee","North Wisconsin","Central Illinois","Chicago","Central Iowa","West Iowa/Nebraska","buffalo","Cincinnati","Indianpolis","Cleveland","West Michigan","Ft. Wayne/South Bend","Western Pennsylvania","Columbus","East Michigan","Toledo","Central Pennsylvania","Pittsburgh"],"id":4,"customerName":"Sprint"}]};
                      this.showInnerLoader = false;
                      if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {
                          this.sessionExpiredModalBlock = this.modalService.open(
                              this.sessionExpiredModalRef,
                              {
                                  keyboard: false,
                                  backdrop: "static",
                                  size: "lg",
                                  windowClass: "session-modal"
                              }
                          );
                      } else {
                          this.layer = this.getLayerInfo(jsonStatue.markers);
                          this.onMapReady(this.map);
                      }
                  }, 1000); */
                  //Please Comment while checkIn
              }
          );
  }

  onMapReady(map: Map) {
      this.map = map;
      if (this.layer && this.layer.length > 0) {
          const group = featureGroup(this.layer);
          group.addTo(this.map);
          // console.log("fitBounds" + map);
          this.map.fitBounds(group.getBounds());
      }
      setTimeout(() => {
          this.map.invalidateSize();
      }, 0);
  }

  onChangeCustomer(custId) {
      if (custId) {
          // const selectedCustomer = this.searchCustomerList.filter(customer => customer.id == custId);
          const selectedCustomer = this.searchCustomerList.find(customer => customer.id == custId);
          this.marketList = selectedCustomer.market;
      }
      else {
          this.marketList = this.allMarketList;
      }
  }

  closeModelMapView() {
    this.options = {};
    this.fromDt = "";
    this.toDate = "";
    this.mapViewModalBlock.close();
  }

  // getCustGraph(selectedCustIs) {
  //     $('#reasonsReport').find('canvas').remove();
  //       this.showReasonsChart(this.dashBoardData.marketBarChartData[selectedCustIs].datasets,this.dashBoardData.marketBarChartData[selectedCustIs].labels);
  // }

  getCpuUsageData() {
    this.sub = Observable.timer(0, 5000)
    .subscribe((val) => { this.cpuUsageData(); });	
  }
  ngOnDestroy() {
      if(this.sub) {
        this.sub.unsubscribe();
      }
  }
  getUserTrend(frequency) {
	$('#trendChart').find('canvas').remove();
	this.showTrendChart();
	}
  cpuUsageData() {		
          this.dashboardService.cpuUsageData(this.sharedService.createServiceToken())
        .subscribe(
          data => {
            setTimeout(() => {
              this.cpuData = data.json();
              if (this.cpuData.sessionId == "408" || this.cpuData.status == "Invalid User") {
                              this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false,backdrop: "static",size: "lg",windowClass: "session-modal"}
                );
              } else {
                if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {
                                  this.scriptDataOutput = this.cpuData.cpuUsage;
                }
              }
            }, 1000);
          },
          error => {

            //Please Comment while checkIn
            /* setTimeout(() => {
              this.cpuData = {"cpuUsage":"    USER   PID %CPU %MEM    VSZ   RSS TTY      STAT START   TIME COMMAND\n       0     1  0.0  0.0 185580  6200 ?        Ss   10:50   0:01 /sbin/init splash\n       0     2  0.0  0.0      0     0 ?        S    10:50   0:00 [kthreadd]\n       0     4  0.0  0.0      0     0 ?        S<   10:50   0:00 [kworker/0:0H]\n       0     6  0.0  0.0      0     0 ?        S    10:50   0:00 [ksoftirqd/0]\n       0     7  0.0  0.0      0     0 ?        S    10:50   0:17 [rcu_sched]\n       0     8  0.0  0.0      0     0 ?        S    10:50   0:00 [rcu_bh]\n       0     9  0.0  0.0      0     0 ?        S    10:50   0:00 [migration/0]\n       0    10  0.0  0.0      0     0 ?        S<   10:50   0:00 [lru-add-drain]\n       0    11  0.0  0.0      0     0 ?        S    10:50   0:00 [watchdog/0]\n       0    12  0.0  0.0      0     0 ?        S    10:50   0:00 [cpuhp/0]\n       0    13  0.0  0.0      0     0 ?        S    10:50   0:00 [cpuhp/1]\n       0    14  0.0  0.0      0     0 ?        S    10:50   0:00 [watchdog/1]\n       0    15  0.0  0.0      0     0 ?        S    10:50   0:00 [migration/1]\n       0    16  0.0  0.0      0     0 ?        S    10:50   0:00 [ksoftirqd/1]\n       0    19  0.0  0.0      0     0 ?        S    10:50   0:00 [cpuhp/2]\n       0    20  0.0  0.0      0     0 ?        S    10:50   0:00 [watchdog/2]\n       0    21  0.0  0.0      0     0 ?        S    10:50   0:00 [migration/2]\n       0    22  0.0  0.0      0     0 ?        S    10:50   0:00 [ksoftirqd/2]\n       0    25  0.0  0.0      0     0 ?        S    10:50   0:00 [cpuhp/3]\n       0    26  0.0  0.0      0     0 ?        S    10:50   0:00 [watchdog/3]\n       0    27  0.0  0.0      0     0 ?        S    10:50   0:00 [migration/3]\n       0    28  0.0  0.0      0     0 ?        S    10:50   0:00 [ksoftirqd/3]\n       0    31  0.0  0.0      0     0 ?        S    10:50   0:00 [kdevtmpfs]\n       0    32  0.0  0.0      0     0 ?        S<   10:50   0:00 [netns]\n       0    33  0.0  0.0      0     0 ?        S    10:50   0:00 [khungtaskd]\n       0    34  0.0  0.0      0     0 ?        S    10:50   0:00 [oom_reaper]\n       0    35  0.0  0.0      0     0 ?        S<   10:50   0:00 [writeback]\n       0    36  0.0  0.0      0     0 ?        S    10:50   0:00 [kcompactd0]\n       0    37  0.0  0.0      0     0 ?        SN   10:50   0:00 [ksmd]\n       0    38  0.0  0.0      0     0 ?        SN   10:50   0:01 [khugepaged]\n       0    39  0.0  0.0      0     0 ?        S<   10:50   0:00 [crypto]\n       0    40  0.0  0.0      0     0 ?        S<   10:50   0:00 [kintegrityd]\n       0    41  0.0  0.0      0     0 ?        S<   10:50   0:00 [bioset]\n       0    42  0.0  0.0      0     0 ?        S<   10:50   0:00 [kblockd]\n       0    47  0.0  0.0      0     0 ?        S<   10:50   0:00 [ata_sff]\n       0    48  0.0  0.0      0     0 ?        S<   10:50   0:00 [md]\n       0    49  0.0  0.0      0     0 ?        S<   10:50   0:00 [devfreq_wq]\n       0    50  0.0  0.0      0     0 ?        S<   10:50   0:00 [watchdogd]\n       0    53  0.0  0.0      0     0 ?        S    10:50   0:00 [kauditd]\n       0    54  0.0  0.0      0     0 ?        S    10:50   0:00 [kswapd0]\n       0    55  0.0  0.0      0     0 ?        S<   10:50   0:00 [vmstat]\n       0    56  0.0  0.0      0     0 ?        S<   10:50   0:00 [bioset]\n       0    57  0.0  0.0      0     0 ?        S    10:50   0:00 [ecryptfs-kthrea]\n       0    97  0.0  0.0      0     0 ?        S<   10:50   0:00 [kthrotld]\n       0    98  0.0  0.0      0     0 ?        S<   10:50   0:00 [acpi_thermal_pm]\n       0   100  0.0  0.0      0     0 ?        S<   10:50   0:00 [bioset]\n       0   101  0.0  0.0      0     0 ?        S<   10:50   0:00 [bioset]\n       0   102  0.0  0.0      0     0 ?        S<   10:50   0:00 [bioset]\n       0   103  0.0  0.0      0     0 ?        S<   10:50   0:00 [bioset]\n       0   104  0.0  0.0      0     0 ?        S<   10:50   0:00 [bioset]\n       0   105  0.0  0.0      0     0 ?        S<   10:50   0:00 [bioset]\n       0   106  0.0  0.0      0     0 ?        S<   10:50   0:00 [bioset]\n       0   107  0.0  0.0      0     0 ?        S<   10:50   0:00 [bioset]\n       0   111  0.0  0.0      0     0 ?        S<   10:50   0:00 [ipv6_addrconf]\n       0   134  0.0  0.0      0     0 ?        S<   10:50   0:00 [charger_manager]\n       0   175  0.0  0.0      0     0 ?        S<   10:50   0:00 [ttm_swap]\n       0   176  0.0  0.0      0     0 ?        S    10:50   0:00 [scsi_eh_0]\n       0   177  0.0  0.0      0     0 ?        S<   10:50   0:00 [scsi_tmf_0]\n       0   178  0.0  0.0      0     0 ?        S    10:50   0:00 [scsi_eh_1]\n       0   179  0.0  0.0      0     0 ?        S<   10:50   0:00 [scsi_tmf_1]\n       0   180  0.0  0.0      0     0 ?        S    10:50   0:00 [scsi_eh_2]\n       0   181  0.0  0.0      0     0 ?        S<   10:50   0:00 [scsi_tmf_2]\n       0   182  0.0  0.0      0     0 ?        S    10:50   0:00 [scsi_eh_3]\n       0   183  0.0  0.0      0     0 ?        S<   10:50   0:00 [scsi_tmf_3]\n       0   187  0.0  0.0      0     0 ?        S    10:50   0:00 [i915/signal:0]\n       0   188  0.0  0.0      0     0 ?        S    10:50   0:00 [i915/signal:1]\n       0   189  0.0  0.0      0     0 ?        S    10:50   0:00 [i915/signal:2]\n       0   190  0.0  0.0      0     0 ?        S    10:50   0:00 [i915/signal:4]\n       0   192  0.0  0.0      0     0 ?        S<   10:50   0:00 [bioset]\n       0   195  0.0  0.0      0     0 ?        S<   10:50   0:01 [kworker/0:1H]\n       0   223  0.0  0.0      0     0 ?        S<   10:50   0:02 [kworker/2:1H]\n       0   224  0.0  0.0      0     0 ?        S    10:50   0:01 [jbd2/sda7-8]\n       0   225  0.0  0.0      0     0 ?        S<   10:50   0:00 [ext4-rsv-conver]\n       0   254  0.0  0.0  35388  4552 ?        Ss   10:50   0:00 /lib/systemd/systemd-journald\n       0   281  0.0  0.0  46424  5160 ?        Ss   10:50   0:00 /lib/systemd/systemd-udevd\n       0   298  0.0  0.0      0     0 ?        S<   10:50   0:00 [loop0]\n       0   299  0.0  0.0      0     0 ?        S<   10:50   0:00 [loop1]\n       0   304  0.0  0.0      0     0 ?        S<   10:50   0:00 [loop2]\n       0   307  0.0  0.0      0     0 ?        S<   10:50   0:00 [loop3]\n       0   310  0.0  0.0      0     0 ?        S<   10:50   0:00 [loop4]\n       0   313  0.0  0.0      0     0 ?        S<   10:50   0:00 [loop5]\n       0   316  0.0  0.0      0     0 ?        S<   10:50   0:00 [loop6]\n       0   319  0.0  0.0      0     0 ?        S<   10:50   0:00 [loop7]\n       0   324  0.0  0.0      0     0 ?        S<   10:50   0:00 [bioset]\n       0   327  0.0  0.0      0     0 ?        S<   10:50   0:00 [loop8]\n       0   331  0.0  0.0      0     0 ?        S<   10:50   0:00 [bioset]\n       0   333  0.0  0.0      0     0 ?        S<   10:50   0:00 [loop9]\n       0   341  0.0  0.0      0     0 ?        S<   10:50   0:00 [bioset]\n       0   342  0.0  0.0      0     0 ?        S<   10:50   0:00 [loop10]\n       0   343  0.0  0.0      0     0 ?        S<   10:50   0:00 [bioset]\n       0   344  0.0  0.0      0     0 ?        S<   10:50   0:00 [loop11]\n       0   347  0.0  0.0      0     0 ?        S<   10:50   0:00 [bioset]\n       0   348  0.0  0.0      0     0 ?        S<   10:50   0:00 [loop12]\n       0   349  0.0  0.0      0     0 ?        S<   10:50   0:00 [bioset]\n       0   350  0.0  0.0      0     0 ?        S<   10:50   0:00 [loop13]\n       0   352  0.0  0.0      0     0 ?        S<   10:50   0:00 [bioset]\n       0   353  0.0  0.0      0     0 ?        S<   10:50   0:00 [loop14]\n       0   425  0.0  0.0      0     0 ?        S    10:50   0:00 [irq/132-mei_me]\n       0   823  0.0  0.0      0     0 ?        S    10:50   0:00 [jbd2/sda5-8]\n       0   824  0.0  0.0      0     0 ?        S<   10:50   0:00 [ext4-rsv-conver]\n     100   860  0.0  0.0 102380  2556 ?        Ssl  10:50   0:00 /lib/systemd/systemd-timesyncd\n       0  1011  0.0  0.0  29872  1612 ?        Ss   10:50   0:00 /sbin/cgmanager -m name=systemd\n     106  1017  0.0  0.0  44316  5136 ?        Ss   10:50   0:00 /usr/bin/dbus-daemon --system --address=systemd: --nofork --nopidfile --systemd-activation\n       0  1029  0.0  0.1 456336 15976 ?        Ssl  10:50   0:00 /usr/sbin/NetworkManager --no-daemon\n       0  1032  0.0  0.0  36072  2856 ?        Ss   10:50   0:00 /usr/sbin/cron -f\n       0  1034  0.0  0.1 173496  8552 ?        Ssl  10:50   0:01 /usr/sbin/thermald --no-daemon --dbus-enable\n       0  1038  0.0  0.1 337360  8712 ?        Ssl  10:50   0:00 /usr/sbin/ModemManager\n     104  1044  0.0  0.0 256388  3532 ?        Ssl  10:50   0:00 /usr/sbin/rsyslogd -n\n       0  1053  0.0  0.0   4392   716 ?        Ss   10:50   0:00 /usr/sbin/acpid\n       0  1054  0.0  0.0  28656  3200 ?        Ss   10:50   0:00 /lib/systemd/systemd-logind\n       0  1055  0.0  0.0 282940  6220 ?        Ssl  10:50   0:00 /usr/lib/accountsservice/accounts-daemon\n     111  1056  0.0  0.0  44912  3148 ?        Ss   10:50   0:00 avahi-daemon: running [BLTSP01671.local]\n       0  1057  0.0  0.3 386008 31720 ?        Ssl  10:50   0:02 /usr/lib/snapd/snapd\n       0  1180  0.0  0.0 276676  6120 ?        SLsl 10:50   0:00 /usr/sbin/lightdm\n     111  1185  0.0  0.0  44780   332 ?        S    10:50   0:00 avahi-daemon: chroot helper\n       0  1187  0.0  0.0  19536  2264 ?        Ss   10:50   0:01 /usr/sbin/irqbalance --pid=/var/run/irqbalance.pid\n       0  1207  1.8  1.1 934388 92400 tty7     Rsl+ 10:50   5:41 /usr/lib/xorg/Xorg -core :0 -seat seat0 -auth /var/run/lightdm/root/:0 -nolisten tcp vt7 -novtswitch\n       0  1209  0.0  0.1 288836  8576 ?        Ssl  10:50   0:00 /usr/lib/policykit-1/polkitd --no-debug\n     123  1230  0.5  0.7 288516 59116 ?        Ssl  10:50   1:43 /usr/bin/mongod --quiet --config /etc/mongod.conf\n     122  1395  0.0  2.0 1000588 167080 ?      Ssl  10:50   0:10 /usr/sbin/mysqld\n       0  1398  0.0  0.0  65508  5404 ?        Ss   10:50   0:00 /usr/sbin/sshd -D\n       0  1402  0.0  0.0      0     0 ?        S<   10:50   0:00 [bioset]\n   65534  1448  0.0  0.0  59928  3936 ?        S    10:50   0:00 /usr/sbin/dnsmasq --no-resolv --keep-in-foreground --no-hosts --bind-interfaces --pid-file=/var/run/NetworkManager/dnsmasq.pid --listen-address=127.0.1.1 --cache-size=0 --conf-file=/dev/null --proxy-dnssec --enable-dbus=org.freedesktop.NetworkManager.dnsmasq --conf-dir=/etc/NetworkManager/dnsmasq.d\n     109  1585  0.0  0.1 373988 12372 ?        Ssl  10:50   0:00 /usr/bin/whoopsie -f\n       0  1590  0.0  0.0  23000  1716 tty1     Ss+  10:50   0:00 /sbin/agetty --noclear tty1 linux\n       0  1656  0.0  0.0 234432  6184 ?        Sl   10:50   0:00 lightdm --session-child 12 19\n       0  1775  0.0  0.0  31952  4312 ?        Ss   10:50   0:00 /usr/lib/bluetooth/bluetoothd\n     118  1839  0.0  0.0 183540  3140 ?        SNsl 10:50   0:00 /usr/lib/rtkit/rtkit-daemon\n       0  1847  0.0  0.1 354292  9724 ?        Ssl  10:50   0:00 /usr/lib/upower/upowerd\n     113  1861  0.0  0.1 308176 10808 ?        Ssl  10:50   0:00 /usr/lib/colord/colord\n    1000  1887  0.0  0.0  45276  4672 ?        Ss   10:51   0:00 /lib/systemd/systemd --user\n    1000  1889  0.0  0.0  65720  2296 ?        S    10:51   0:00 (sd-pam)\n    1000  1894  0.0  0.0 213008  7876 ?        SLl  10:51   0:00 /usr/bin/gnome-keyring-daemon --daemonize --login\n    1000  1896  0.0  0.0  53524  4620 ?        Ss   10:51   0:00 /sbin/upstart --user\n    1000  2053  0.0  0.0  39924   280 ?        S    10:51   0:00 upstart-udev-bridge --daemon --user\n    1000  2062  0.0  0.0  43908  4316 ?        Ss   10:51   0:03 dbus-daemon --fork --session --address=unix:abstract=/tmp/dbus-mDNLeuQCJs\n    1000  2074  0.0  0.1  93408  9756 ?        Ss   10:51   0:00 /usr/lib/x86_64-linux-gnu/hud/window-stack-bridge\n    1000  2107  0.0  0.0  39856   292 ?        S    10:51   0:00 upstart-dbus-bridge --daemon --system --user --bus-name system\n    1000  2109  0.0  0.0  39856   292 ?        S    10:51   0:00 upstart-dbus-bridge --daemon --session --user --bus-name session\n    1000  2121  0.0  0.0  48348   380 ?        S    10:51   0:00 upstart-file-bridge --daemon --user\n    1000  2248  0.0  0.3 531256 27128 ?        Ssl  10:51   0:03 /usr/lib/x86_64-linux-gnu/bamf/bamfdaemon\n    1000  2249  0.0  0.0 173600   716 ?        Ss   10:51   0:00 gpg-agent --homedir /home/user/.gnupg --use-standard-socket --daemon\n    1000  2255  0.0  0.0 281612  6220 ?        Sl   10:51   0:00 /usr/lib/gvfs/gvfsd\n    1000  2260  0.0  0.0 406856  7280 ?        Sl   10:51   0:00 /usr/lib/gvfs/gvfsd-fuse /run/user/1000/gvfs -f -o big_writes\n    1000  2277  0.1  1.1 555400 96056 ?        Ssl  10:51   0:22 /usr/lib/x86_64-linux-gnu/hud/hud-service\n    1000  2279  0.0  0.4 861136 34176 ?        Ssl  10:51   0:00 /usr/lib/unity-settings-daemon/unity-settings-daemon\n    1000  2300  0.0  0.0 338000  5696 ?        Ssl  10:51   0:00 /usr/lib/at-spi2-core/at-spi-bus-launcher --launch-immediately\n    1000  2301  0.0  0.2 640460 16988 ?        Ssl  10:51   0:00 /usr/lib/gnome-session/gnome-session-binary --session=ubuntu\n    1000  2305  0.0  0.5 654180 41720 ?        Ssl  10:51   0:12 /usr/lib/x86_64-linux-gnu/unity/unity-panel-service\n    1000  2310  0.0  0.0  42888  3896 ?        S    10:51   0:00 /usr/bin/dbus-daemon --config-file=/etc/at-spi2/accessibility.conf --nofork --print-address 3\n    1000  2328  0.0  0.0 206880  6432 ?        Sl   10:51   0:02 /usr/lib/at-spi2-core/at-spi2-registryd --use-gnome-session\n    1000  2332  0.0  0.0 435212  7012 ?        Ssl  10:51   0:00 /usr/lib/x86_64-linux-gnu/indicator-messages/indicator-messages-service\n    1000  2333  0.0  0.0 414180  5484 ?        Ssl  10:51   0:00 /usr/lib/x86_64-linux-gnu/indicator-bluetooth/indicator-bluetooth-service\n    1000  2334  0.0  0.1 509672  9776 ?        Ssl  10:51   0:00 /usr/lib/x86_64-linux-gnu/indicator-power/indicator-power-service\n    1000  2335  0.0  0.2 1239996 16540 ?       Ssl  10:51   0:00 /usr/lib/x86_64-linux-gnu/indicator-datetime/indicator-datetime-service\n    1000  2340  0.0  0.3 795492 25940 ?        Ssl  10:51   0:00 /usr/lib/x86_64-linux-gnu/indicator-keyboard/indicator-keyboard-service --use-gtk\n    1000  2349  0.0  0.1 670324 10152 ?        Ssl  10:51   0:00 /usr/lib/x86_64-linux-gnu/indicator-sound/indicator-sound-service\n    1000  2350  0.0  0.2 554528 24040 ?        Ssl  10:51   0:00 /usr/lib/x86_64-linux-gnu/indicator-printers/indicator-printers-service\n    1000  2360  0.0  0.1 959936  8636 ?        Ssl  10:51   0:00 /usr/lib/x86_64-linux-gnu/indicator-session/indicator-session-service\n    1000  2362  0.0  0.1 403144 13168 ?        Ssl  10:51   0:00 /usr/lib/x86_64-linux-gnu/indicator-application/indicator-application-service\n    1000  2393  0.0  0.1 515940 11688 ?        S<l  10:51   0:02 /usr/bin/pulseaudio --start --log-target=syslog\n    1000  2422  0.0  0.0 178756  4948 ?        Sl   10:51   0:00 /usr/lib/dconf/dconf-service\n    1000  2435  0.0  0.2 1184412 23420 ?       Sl   10:51   0:00 /usr/lib/evolution/evolution-source-registry\n    1000  2452  1.2  1.4 1658980 120008 ?      Ssl  10:51   3:55 compiz\n    1000  2467  0.0  0.7 870036 61880 ?        Sl   10:51   0:00 /usr/lib/evolution/evolution-calendar-factory\n    1000  2477  0.0  0.6 823060 50100 ?        Sl   10:51   0:00 /usr/lib/evolution/evolution-calendar-factory-subprocess --factory contacts --bus-name org.gnome.evolution.dataserver.Subprocess.Backend.Calendarx2467x2 --own-path /org/gnome/evolution/dataserver/Subprocess/Backend/Calendar/2467/2\n    1000  2487  0.0  0.2 697628 19944 ?        Sl   10:51   0:00 /usr/lib/evolution/evolution-addressbook-factory\n    1000  2489  0.0  0.6 1069580 49628 ?       Sl   10:51   0:00 /usr/lib/evolution/evolution-calendar-factory-subprocess --factory local --bus-name org.gnome.evolution.dataserver.Subprocess.Backend.Calendarx2467x3 --own-path /org/gnome/evolution/dataserver/Subprocess/Backend/Calendar/2467/3\n    1000  2506  0.0  0.2 846856 18132 ?        Sl   10:51   0:00 /usr/lib/evolution/evolution-addressbook-factory-subprocess --factory local --bus-name org.gnome.evolution.dataserver.Subprocess.Backend.AddressBookx2487x2 --own-path /org/gnome/evolution/dataserver/Subprocess/Backend/AddressBook/2487/2\n    1000  2533  0.0  0.2 584056 23008 ?        Sl   10:51   0:00 /usr/lib/unity-settings-daemon/unity-fallback-mount-helper\n    1000  2535  0.0  0.3 491924 30296 ?        Sl   10:51   0:00 /usr/lib/policykit-1-gnome/polkit-gnome-authentication-agent-1\n    1000  2536  0.0  1.2 1362392 97944 ?       Sl   10:51   0:01 /usr/bin/gnome-software --gapplication-service\n    1000  2537  0.0  0.7 1450492 63944 ?       Sl   10:51   0:09 nautilus -n\n    1000  2543  0.0  0.4 743904 33300 ?        Sl   10:51   0:00 nm-applet\n    1000  2551  0.0  0.0 292504  7520 ?        Sl   10:51   0:00 /usr/lib/gvfs/gvfs-udisks2-volume-monitor\n       0  2566  0.0  0.1 367536 10660 ?        Ssl  10:51   0:01 /usr/lib/udisks2/udisksd --no-debug\n    1000  2576  0.0  0.0 271120  7548 ?        Sl   10:51   0:00 /usr/lib/gvfs/gvfs-mtp-volume-monitor\n    1000  2584  0.0  0.1 410672  8736 ?        Sl   10:51   0:00 /usr/lib/gvfs/gvfs-afc-volume-monitor\n    1000  2590  0.0  0.0 264592  4988 ?        Sl   10:51   0:00 /usr/lib/gvfs/gvfs-goa-volume-monitor\n    1000  2595  0.0  0.0 278780  4880 ?        Sl   10:51   0:00 /usr/lib/gvfs/gvfs-gphoto2-volume-monitor\n    1000  2626  0.0  0.0 431340  6572 ?        Sl   10:51   0:00 /usr/lib/gvfs/gvfsd-trash --spawner :1.5 /org/gtk/gvfs/exec_spaw/0\n    1000  2640  0.0  0.0 193304  5532 ?        Sl   10:51   0:00 /usr/lib/gvfs/gvfsd-metadata\n    1000  2646  6.5 23.0 7488732 1848584 ?     Sl   10:51  19:54 /usr/bin/java -jar /home/user/Desktop/Eclipse_oxygen/eclipse/plugins/org.eclipse.equinox.launcher_1.3.200.v20160914-0716.jar -os linux -ws gtk -arch x86_64 -showsplash -launcher /home/user/Desktop/Eclipse_oxygen/eclipse/eclipse -clean -name Eclipse -clean --launcher.library /home/user/Desktop/Eclipse_oxygen/eclipse/plugins/org.eclipse.equinox.launcher.gtk.linux.x86_64_1.1.400.v20160914-0716/eclipse_1617.so -startup /home/user/Desktop/Eclipse_oxygen/eclipse/plugins/org.eclipse.equinox.launcher_1.3.200.v20160914-0716.jar --launcher.overrideVmargs -exitdata 198000 -vm /usr/bin/java -vmargs -jar /home/user/Desktop/Eclipse_oxygen/eclipse/plugins/org.eclipse.equinox.launcher_1.3.200.v20160914-0716.jar\n    1000  2679  0.0  0.2 520576 20648 ?        Sl   10:51   0:01 zeitgeist-datahub\n    1000  2686  0.0  0.0   4500   848 ?        S    10:51   0:00 /bin/sh -c /usr/lib/x86_64-linux-gnu/zeitgeist/zeitgeist-maybe-vacuum; /usr/bin/zeitgeist-daemon\n    1000  2690  0.0  0.0 410284  7704 ?        Sl   10:51   0:00 /usr/bin/zeitgeist-daemon\n    1000  2697  0.0  0.1 317224 15048 ?        Sl   10:51   0:00 /usr/lib/x86_64-linux-gnu/zeitgeist-fts\n    1000  2734  2.4  3.6 1231156 290644 ?      SLl  10:51   7:18 /opt/google/chrome/chrome\n    1000  2741  0.0  0.0  14508   524 ?        S    10:51   0:00 cat\n    1000  2742  0.0  0.0  14508   692 ?        S    10:51   0:00 cat\n    1000  2745  0.0  0.6 436076 55260 ?        S    10:51   0:00 /opt/google/chrome/chrome --type=zygote --enable-crash-reporter=439f12ff-0f81-4f52-bbfc-86f83e3e1189,\n    1000  2746  0.0  0.0  25732  4084 ?        S    10:51   0:00 /opt/google/chrome/nacl_helper\n    1000  2749  0.0  0.1 436076 14436 ?        S    10:51   0:00 /opt/google/chrome/chrome --type=zygote --enable-crash-reporter=439f12ff-0f81-4f52-bbfc-86f83e3e1189,\n    1000  2803  0.9  2.0 1004940 166244 ?      Sl   10:51   2:51 /proc/self/exe --type=gpu-process --field-trial-handle=20435763207720620,13684982378402628872,131072 --enable-crash-reporter=439f12ff-0f81-4f52-bbfc-86f83e3e1189, --gpu-preferences=KAAAAAAAAACAAABAAQAAAAAAAAAAAGAAAAAAAAAAAAAIAAAAAAAAAAgAAAAAAAAA --enable-crash-reporter=439f12ff-0f81-4f52-bbfc-86f83e3e1189, --service-request-channel-token=9742768983344917615\n    1000  2827  0.0  0.4 497432 33276 ?        Sl   10:51   0:01 /usr/lib/x86_64-linux-gnu/notify-osd\n    1000  2957  0.0  0.3 531236 25316 ?        Sl   10:52   0:00 update-notifier\n    1000  3010  0.0  1.0 715556 86928 ?        Sl   10:52   0:01 /opt/google/chrome/chrome --type=renderer --field-trial-handle=20435763207720620,13684982378402628872,131072 --service-pipe-token=13140026615199322692 --lang=en-GB --enable-crash-reporter=439f12ff-0f81-4f52-bbfc-86f83e3e1189, --enable-offline-auto-reload --enable-offline-auto-reload-visible-only --num-raster-threads=2 --enable-main-frame-before-activation --service-request-channel-token=13140026615199322692 --renderer-client-id=9 --shared-files=v8_context_snapshot_data:100,v8_natives_data:101\n    1000  3099  0.0  1.7 963452 137640 ?       Sl   10:52   0:05 /opt/google/chrome/chrome --type=renderer --field-trial-handle=20435763207720620,13684982378402628872,131072 --service-pipe-token=9514916110040837675 --lang=en-GB --enable-crash-reporter=439f12ff-0f81-4f52-bbfc-86f83e3e1189, --enable-offline-auto-reload --enable-offline-auto-reload-visible-only --num-raster-threads=2 --enable-main-frame-before-activation --service-request-channel-token=9514916110040837675 --renderer-client-id=11 --shared-files=v8_context_snapshot_data:100,v8_natives_data:101\n    1000  3613  0.0  0.9 699384 73444 ?        Sl   10:52   0:00 /opt/google/chrome/chrome --type=renderer --field-trial-handle=20435763207720620,13684982378402628872,131072 --service-pipe-token=7781419993421436450 --lang=en-GB --enable-crash-reporter=439f12ff-0f81-4f52-bbfc-86f83e3e1189, --enable-offline-auto-reload --enable-offline-auto-reload-visible-only --num-raster-threads=2 --enable-main-frame-before-activation --service-request-channel-token=7781419993421436450 --renderer-client-id=17 --shared-files=v8_context_snapshot_data:100,v8_natives_data:101\n    1000  3625  0.0  0.8 706936 72296 ?        Sl   10:52   0:00 /opt/google/chrome/chrome --type=renderer --field-trial-handle=20435763207720620,13684982378402628872,131072 --service-pipe-token=15298426209321414617 --lang=en-GB --enable-crash-reporter=439f12ff-0f81-4f52-bbfc-86f83e3e1189, --enable-offline-auto-reload --enable-offline-auto-reload-visible-only --num-raster-threads=2 --enable-main-frame-before-activation --service-request-channel-token=15298426209321414617 --renderer-client-id=18 --shared-files=v8_context_snapshot_data:100,v8_natives_data:101\n    1000  3783  0.7  4.9 1145052 396900 ?      Sl   10:53   2:18 /opt/google/chrome/chrome --type=renderer --field-trial-handle=20435763207720620,13684982378402628872,131072 --service-pipe-token=4218645776972988344 --lang=en-GB --enable-crash-reporter=439f12ff-0f81-4f52-bbfc-86f83e3e1189, --enable-offline-auto-reload --enable-offline-auto-reload-visible-only --num-raster-threads=2 --enable-main-frame-before-activation --service-request-channel-token=4218645776972988344 --renderer-client-id=20 --shared-files=v8_context_snapshot_data:100,v8_natives_data:101\n    1000  3816  0.0  0.0 442508  6920 ?        Sl   10:53   0:00 /usr/lib/x86_64-linux-gnu/deja-dup/deja-dup-monitor\n    1000  3981  0.1  1.1 722204 88596 ?        Sl   10:53   0:35 /opt/google/chrome/chrome --type=renderer --field-trial-handle=20435763207720620,13684982378402628872,131072 --service-pipe-token=10745823238137410856 --lang=en-GB --enable-crash-reporter=439f12ff-0f81-4f52-bbfc-86f83e3e1189, --enable-offline-auto-reload --enable-offline-auto-reload-visible-only --num-raster-threads=2 --enable-main-frame-before-activation --service-request-channel-token=10745823238137410856 --renderer-client-id=32 --shared-files=v8_context_snapshot_data:100,v8_natives_data:101\n    1000  4022  0.0  0.4 520040 39280 ?        Sl   10:53   0:07 /usr/lib/gnome-terminal/gnome-terminal-server\n    1000  4026  0.0  0.0  30020  5568 pts/5    Ss+  10:53   0:00 bash\n    1000  4124  0.0  0.0 433728  6660 ?        Sl   10:54   0:00 /usr/lib/gvfs/gvfsd-network --spawner :1.5 /org/gtk/gvfs/exec_spaw/1\n    1000  4141  0.0  0.2 818800 16512 ?        Sl   10:54   0:00 /usr/lib/gvfs/gvfsd-smb-browse --spawner :1.5 /org/gtk/gvfs/exec_spaw/4\n    1000  4153  0.0  0.0 442512  6664 ?        Sl   10:54   0:00 /usr/lib/gvfs/gvfsd-dnssd --spawner :1.5 /org/gtk/gvfs/exec_spaw/5\n       0  4714  0.0  0.1 274812  9424 ?        Ssl  10:55   0:00 /usr/sbin/cups-browsed\n    1000  4829  0.4  2.6 980196 210400 ?       Sl   10:56   1:24 /opt/google/chrome/chrome --type=renderer --field-trial-handle=20435763207720620,13684982378402628872,131072 --service-pipe-token=12906469983383819764 --lang=en-GB --enable-crash-reporter=439f12ff-0f81-4f52-bbfc-86f83e3e1189, --enable-offline-auto-reload --enable-offline-auto-reload-visible-only --num-raster-threads=2 --enable-main-frame-before-activation --service-request-channel-token=12906469983383819764 --renderer-client-id=35 --shared-files=v8_context_snapshot_data:100,v8_natives_data:101\n     107  5136  0.0  0.0  35220  1276 ?        Ss   11:00   0:00 /usr/sbin/uuidd --socket-activation\n       0  5313  0.0  0.0      0     0 ?        S<   11:03   0:00 [kworker/3:2H]\n    1000  5342  0.0  0.6 2197360 49012 ?       Sl   11:04   0:00 /usr/lib/x86_64-linux-gnu/webkit2gtk-4.0/WebKitNetworkProcess 307\n    1000  5347  0.0  0.4 1892528 36112 ?       Sl   11:04   0:00 /usr/lib/x86_64-linux-gnu/webkit2gtk-4.0/WebKitWebProcess 310\n       0  5473  0.0  0.0      0     0 ?        S    11:06   0:00 [kworker/2:0]\n       0  5966  0.0  0.0      0     0 ?        S    11:45   0:00 [kworker/3:0]\n       0  6060  0.0  0.2 173852 19424 ?        Sl   11:46   0:00 /usr/bin/python3 /usr/lib/system-service/system-service-d\n       0  6129  0.0  0.0      0     0 ?        S<   11:46   0:03 [kworker/3:1H]\n       0  6683  0.0  0.0  16120  3740 ?        S    11:50   0:00 /sbin/dhclient -d -q -sf /usr/lib/NetworkManager/nm-dhcp-helper -pf /var/run/dhclient-enp0s31f6.pid -lf /var/lib/NetworkManager/dhclient-65bd9a3f-46a4-4f25-be7d-bc885d91825d-enp0s31f6.lease -cf /var/lib/NetworkManager/dhclient-enp0s31f6.conf enp0s31f6\n    1000  7300  0.3  1.5 927640 122596 ?       Sl   11:58   0:51 /opt/google/chrome/chrome --type=renderer --field-trial-handle=20435763207720620,13684982378402628872,131072 --service-pipe-token=9688333331503312352 --lang=en-GB --enable-crash-reporter=439f12ff-0f81-4f52-bbfc-86f83e3e1189, --enable-offline-auto-reload --enable-offline-auto-reload-visible-only --num-raster-threads=2 --enable-main-frame-before-activation --service-request-channel-token=9688333331503312352 --renderer-client-id=83 --shared-files=v8_context_snapshot_data:100,v8_natives_data:101\n    1000  7768  0.1  1.6 816776 130760 ?       Sl   12:03   0:24 /opt/google/chrome/chrome --type=renderer --field-trial-handle=20435763207720620,13684982378402628872,131072 --service-pipe-token=17525786841262689964 --lang=en-GB --enable-crash-reporter=439f12ff-0f81-4f52-bbfc-86f83e3e1189, --enable-offline-auto-reload --enable-offline-auto-reload-visible-only --num-raster-threads=2 --enable-main-frame-before-activation --service-request-channel-token=17525786841262689964 --renderer-client-id=96 --shared-files=v8_context_snapshot_data:100,v8_natives_data:101\n    1000  8412  0.0  0.8 2208508 69112 ?       Sl   12:11   0:00 /usr/lib/x86_64-linux-gnu/webkit2gtk-4.0/WebKitWebProcess 147\n       0  8662  0.0  0.0      0     0 ?        S<   12:12   0:00 [kworker/2:2H]\n       0  8898  0.0  0.0      0     0 ?        S<   12:15   0:00 [kworker/1:2H]\n       0  9866  0.0  0.0      0     0 ?        S    12:25   0:00 [kworker/1:1]\n       0 10865  0.0  0.0      0     0 ?        S    12:31   0:00 [kworker/1:2]\n    1000 11315  0.0  1.0 712648 86084 ?        Sl   12:35   0:00 /opt/google/chrome/chrome --type=renderer --field-trial-handle=20435763207720620,13684982378402628872,131072 --service-pipe-token=13910575154837427264 --lang=en-GB --enable-crash-reporter=439f12ff-0f81-4f52-bbfc-86f83e3e1189, --enable-offline-auto-reload --enable-offline-auto-reload-visible-only --num-raster-threads=2 --enable-main-frame-before-activation --service-request-channel-token=13910575154837427264 --renderer-client-id=131 --shared-files=v8_context_snapshot_data:100,v8_natives_data:101\n       0 11332  0.0  0.0      0     0 ?        S    12:35   0:00 [kworker/2:2]\n    1000 11992  0.5 12.0 5702564 964428 pts/5  Sl   12:40   1:04 /home/user/jdk/bin/java -Djava.util.logging.config.file=/home/user/tomcat/apache-tomcat-8.5.35/conf/logging.properties -Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager -Djdk.tls.ephemeralDHKeySize=2048 -Djava.protocol.handler.pkgs=org.apache.catalina.webresources -Dorg.apache.catalina.security.SecurityListener.UMASK=0027 -Dignore.endorsed.dirs= -classpath /home/user/tomcat/apache-tomcat-8.5.35/bin/bootstrap.jar:/home/user/tomcat/apache-tomcat-8.5.35/bin/tomcat-juli.jar -Dcatalina.base=/home/user/tomcat/apache-tomcat-8.5.35 -Dcatalina.home=/home/user/tomcat/apache-tomcat-8.5.35 -Djava.io.tmpdir=/home/user/tomcat/apache-tomcat-8.5.35/temp org.apache.catalina.startup.Bootstrap start\n       0 12114  0.0  0.0      0     0 ?        S    12:42   0:00 [kworker/0:1]\n       0 12654  0.0  0.0      0     0 ?        S    14:51   0:00 [kworker/u8:2]\n       0 12872  0.0  0.0      0     0 ?        S<   15:43   0:00 [kworker/1:1H]\n       0 12906  0.0  0.0      0     0 ?        S    15:44   0:00 [kworker/3:2]\n       0 12915  0.0  0.0      0     0 ?        S    15:44   0:00 [kworker/u8:3]\n       0 12952  0.0  0.0      0     0 ?        S    15:44   0:00 [kworker/0:0]\n       0 13002  0.0  0.0      0     0 ?        S    15:49   0:00 [kworker/u8:0]\n       0 13022  0.0  0.0      0     0 ?        S<   15:50   0:00 [kworker/1:0H]\n    1000 13153 16.8 15.0 35324972 1211980 ?    Sl   15:51   0:33 /opt/google/chrome/chrome --type=renderer --field-trial-handle=20435763207720620,13684982378402628872,131072 --service-pipe-token=13454980561575944244 --lang=en-GB --enable-crash-reporter=439f12ff-0f81-4f52-bbfc-86f83e3e1189, --extension-process --enable-offline-auto-reload --enable-offline-auto-reload-visible-only --num-raster-threads=2 --enable-main-frame-before-activation --service-request-channel-token=13454980561575944244 --renderer-client-id=142 --shared-files=v8_context_snapshot_data:100,v8_natives_data:101\n       0 13168  0.0  0.0      0     0 ?        S    15:51   0:00 [kworker/1:0]\n    1000 13287  0.2  0.4 660508 39608 ?        Sl   15:51   0:00 gedit /home/user/Desktop/site_data_json\n       0 13462  0.0  0.0      0     0 ?        S    15:51   0:00 [kworker/3:1]\n       0 13466  0.0  0.0      0     0 ?        S    15:51   0:00 [kworker/2:1]\n    1000 13522  0.0  0.5 661832 47792 ?        Sl   15:53   0:00 /opt/google/chrome/chrome --type=renderer --field-trial-handle=20435763207720620,13684982378402628872,131072 --service-pipe-token=15881053713265026479 --lang=en-GB --enable-crash-reporter=439f12ff-0f81-4f52-bbfc-86f83e3e1189, --enable-offline-auto-reload --enable-offline-auto-reload-visible-only --num-raster-threads=2 --enable-main-frame-before-activation --service-request-channel-token=15881053713265026479 --renderer-client-id=148 --shared-files=v8_context_snapshot_data:100,v8_natives_data:101\n    1000 13546  0.0  0.0  44428  3264 pts/5    R    15:54   0:00 ps -augx -n 10\n","sessionId":"5eecbb3","serviceToken":"68061","status":"SUCCESS"};
              if (this.cpuData.sessionId == "408" ||this.cpuData.status == "Invalid User") {
                this.sessionExpiredModalBlock = this.modalService.open(
                  this.sessionExpiredModalRef,
                  {
                    keyboard: false,
                    backdrop: "static",
                    size: "lg",
                    windowClass: "session-modal"
                  }
                );
              } else {
                                  this.scriptDataOutput = this.cpuData.cpuUsage;
                                  this.showLoader = false;
              }
            }, 1000); */
            //Please Comment while checkIn
          }
        );
  }

  healthTabBind() {
    this.setMenuHighlight("health");
    this.healthBlock = true;
    this.candiBlock=false;
    this.showLoader=true;
    //this.chartShowHide = true;
    this.getCpuUsageData();
    setTimeout(()=>{
      this.showLoader =false;
    this.showDiskUsageChart(this.usedDiskSpacePercentage, this.freeDiskSpacePercentage);
    this.showMemoryUsageChart(this.usedMemoryPercentage, this.freeMemoryPercentage);
    // this.showNECommissionChart(this.dashBoardData.barChartData.datasets, this.dashBoardData.barChartData.labels);
    },1000)
  }

  candiTabBind() {
    this.setMenuHighlight("candi");
    this.healthBlock = false;
    this.candiBlock=true;
    this.showLoader =true;
    //this.chartShowHide = true;
    if(this.sub) {
        this.sub.unsubscribe();
     }
    setTimeout(()=>{
      this.showLoader =false;
      this.completedSites = 20;
      this.notCompletedSites = 30;
      this.inProgressSites = 50;
      this.market1 = 10;
      this.market2 = 15;
      this.market3 = 15;
      this.market4 = 10;
      this.market5 = 20;
      this.market6 = 10;
      this.market7 = 5;
      this.market8 = 10;
      this.market9 = 5;
      this.tech1 = 15;
      this.tech2 = 25;
      this.tech3 = 25;
      this.tech4 = 15;
      this.tech5 = 20;
      this.showCommissioningReport(this.tech1, this.tech2, this.tech3, this.tech4, this.tech5);
      this.showNECommissionChart(this.completedSites, this.notCompletedSites, this.inProgressSites);
    //   this.showScheduleChart(this.scheduledPercentage, this.cancelledPercentage);
      this.showReasonsChart(this.market1, this.market2, this.market3, this.market4, this.market5, this.market6, this.market7, this.market8, this.market9);
      
      // this.showNECommissionChart(this.dashBoardData.barChartData.datasets, this.dashBoardData.barChartData.labels);
      // this.showReasonsChart(this.dashBoardData.marketBarChartData[this.selectedCustIs].datasets, this.dashBoardData.marketBarChartData[this.selectedCustIs].labels);
      // this.showReasonsChart(this.market1, this.market2, this.market3, this.market4, this.market5, this.market6, this.market7, this.market8, this.market9);
    },1000)
  }

  setMenuHighlight(selectedElement) {
    this.healthTabRef.nativeElement.id = (selectedElement == "health") ? "activeTab" : "inactiveTab";
    this.candiTabRef.nativeElement.id = (selectedElement == "candi") ? "activeTab" : "inactiveTab";
  }
  populateFrequencyArr() {
		this.frequencies.push("TODAY");
		this.frequencies.push("YESTERDAY");
		this.frequencies.push("THIS WEEK");
		this.frequencies.push("LAST WEEK");
		this.frequencies.push("LAST MONTH");
		this.frequencies.push("CUSTOM");
	}

  showDiskUsageChart(usedDiskSpacePercentage, freeDiskSpacePercentage){

      let chartCanvas : any = document.createElement('canvas'),
          chart : any,
          stackedCtx : any,
          maxValue;
      let config = {
        type: 'doughnut',
        data: {
            datasets: [{
                data: [usedDiskSpacePercentage.replace(" %",""), freeDiskSpacePercentage.replace(" %","")],
                backgroundColor: ["#0e183b","#5161AC"],
                borderWidth: [0, 0]
            }],
            labels: ["USED","AVAILABLE"]
        },
        options: {
            responsive: false,
            legend: {
                position: 'bottom',
                labels:{
                  boxWidth:12
                }
            },
            title: {
                display: true,
                //text: 'DISK USAGE'
            },
            animation: {
                animateScale: false,
                animateRotate: false
            }
            
        }
    };           
      chartCanvas.width = 260;
      chartCanvas.height = 260;
      stackedCtx = chartCanvas.getContext('2d');
      //this.chartShowHide = true;
      chart = new Chart(stackedCtx, config);
      console.log(chart);
      setTimeout(() => { 
        document.getElementById("chartBlockDisk").appendChild( chartCanvas );
        if($("#chartBlockDisk").find("canvas")[1]){
          $("#chartBlockDisk").find("canvas")[1].remove();
        }
        //document.getElementById("chartdiv").appendChild( chartCanvas );
       }, 100);

  }
  
  showMemoryUsageChart(usedMemoryPercentage, freeMemoryPercentage){

      let chartCanvas : any = document.createElement('canvas'),
          chart : any,
          stackedCtx : any,
          maxValue;
      
      let config = {
        type: 'doughnut',
        data: {
            datasets: [{
                data: [usedMemoryPercentage.replace(" %",""), freeMemoryPercentage.replace(" %","")],
                backgroundColor: ["#0e183b","#5161AC"],
                borderWidth: [0, 0]
            }],
            labels: ["USED","AVAILABLE"]
        },
        options: {
            responsive: false,
            legend: {
                position: 'bottom',
                labels:{
                  boxWidth:12
                }
            },
            title: {
                display: true,
                //text: 'MEMORY USAGE'
            },
            animation: {
                animateScale: false,
                animateRotate: false
            }
        }
    }; 
         
      chartCanvas.width = 260;
      chartCanvas.height = 260;
      stackedCtx = chartCanvas.getContext('2d');
      //this.chartShowHide = true;
      chart = new Chart(stackedCtx, config);
      setTimeout(() => { 
        //document.getElementById("chartBlock_bar").appendChild( chartCanvas );
        document.getElementById("chartBlockMemory").appendChild( chartCanvas );
        if($("#chartBlockMemory").find("canvas")[1]){
          $("#chartBlockMemory").find("canvas")[1].remove();
        }
       }, 100);

  }
  
  // Completion Pie Chart
  showNECommissionChart(completedSites, notCompletedSites, inProgressSites){

    let chartCanvas : any = document.createElement('canvas'),
        chart : any,
        stackedCtx : any,
        maxValue;
    
    let config = {
      type: 'pie',
      data: {
          // datasets: dataset,
          // labels: labels
          datasets: [{
            data: [completedSites, notCompletedSites, inProgressSites],
            backgroundColor: ["#0e183b","#5161AC", "#a7b3e9"],
            borderWidth: [0, 0, 0]
        }],
        labels: ["COMPLETED","NOT COMPLETED", "IN PROGRESS"]
      },
      options: {
          responsive: true,
          // layout: {
          //   padding: {
          //       left: 20
          //   }
          // },
          legend: {
              position: 'bottom',
              labels:{
                boxWidth:10
              }
            
          },
          // scales: {
          //   yAxes: [{
          //     ticks: {
          //       beginAtZero : true,
                
          //       fontColor:'#5E5C5C',
          //       fontSize: 10                
          //     }
          //   }],
          //   xAxes:[{
          //     barThickness:35,
          //     ticks:{
          //       autoSkip: false,
          //       maxRotation: 15,
          //       minRotation: 0,
          //       fontSize:11,
          //       fontStyle: "bold" ,
          //       fontColor: "#09113b"
          //     }
          //   }]
          // },      
          title: {
              display: true,
              //text: 'MEMORY USAGE'
          },
          animation: {
            animateScale: false,
            animateRotate: false
            // duration: 500,
            // easing: "easeOutQuart",
            // onComplete: function () {
            //     var ctx = this.chart.ctx;
            //     ctx.font = Chart.helpers.fontString(10, 'bold', Chart.defaults.global.defaultFontFamily);
            //     ctx.textAlign = 'center';
            //     ctx.textBaseline = 'bottom';

            //     this.data.datasets.forEach(function (dataset) {
            //         for (var i = 0; i < dataset.data.length; i++) {
            //             var model = dataset._meta[Object.keys(dataset._meta)[0]].data[i]._model,
            //                 scale_max = dataset._meta[Object.keys(dataset._meta)[0]].data[i]._yScale.maxHeight;
            //             ctx.fillStyle = '#292b2c';
            //             var y_pos = model.y - 5;
            //             // Make sure data value does not get overflown and hidden
            //             // when the bar's value is too close to max value of scale
            //             // Note: The y value is reverse, it counts from top down
            //             if ((scale_max - model.y) / scale_max >= 0.93)
            //                 y_pos = model.y + 20;
            //             if(parseFloat(dataset.percData[i]).toFixed(2) != "0.00" && parseFloat(dataset.percData[i]).toFixed(2) != "100.00") {
            //                 ctx.fillText(parseFloat(dataset.percData[i]).toFixed(2) + "%", model.x + 1, y_pos);
            //             }
            //         }
            //     });
            // }
        }
          // labels:{fontColor:'blue'}    
      }
  };         
    chartCanvas.width = 450; 
    chartCanvas.height = 330; 
    stackedCtx = chartCanvas.getContext('2d');
    //this.chartShowHide = true;
    chart = new Chart(stackedCtx, config);
    setTimeout(() => { 
      //document.getElementById("chartBlock_bar").appendChild( chartCanvas );
      document.getElementById("barChart").appendChild( chartCanvas );
      if($("#barChart").find("canvas")[1]){
        $("#barChart").find("canvas")[1].remove();
      }
     }, 100);

  }
  
  //show user trend
  showTrendChart() {

		let chartCanvas: any = document.createElement('canvas'),
			chart: any,
			stackedCtx: any,
			maxValue;

		let config = {
			type: 'line',

			data: {
				// datasets:dataset,// [{"label":"Scheduled","backgroundColor":"#0e183b","data":["540","700","866","300"]},{"label":"Migrated","backgroundColor":"#5161AC","data":["600","560","934","350"]},{"label":"Cancelled","backgroundColor":"#a7b3e9","data":["220","200","400","200"]}],
				// labels:labels    //["Jan 18","Feb 18","Mar 18", "Apr 18"]

				labels: ['10:30AM', '11:00AM', '11:30AM', '12:00PM',
					'12:30PM', '13:00PM', '13:30PM', '14:00PM'],
				datasets: [
					{
						label: "ACTIVE USERS",
						data: ['67', '50', '72', '75', '85',
							'90', '82', '76'],
						backgroundColor: 'blue',
						fill: false
					},
					{
						label: "ACTIVE SESSIONS",
						data: ['78', '60', '86', '96', '98',
							'100', '122', '130'],
						backgroundColor: 'limegreen',
						fill: false
					}
				]
			},
			options: {
				responsive: true,
				layout: {
					padding: {
						left: 20
					}
				},
				legend: {
					position: 'top',
					align: 'right',
					labels: {
						boxWidth: 10
					}

				},
				// scales: {
				//	yAxes: [{
				//	ticks: {
				// 	beginAtZero : true,
				//	fontColor:'#5E5C5C',
				//	fontSize: 10                
				//	}
				//	}],
				//	xAxes:[{
				//		barThickness:35,
				//		ticks:{
				//		autoSkip: false,
				//		maxRotation: 15,
				//		minRotation: 0,
				//		fontSize:11,
				//		fontStyle: "bold",
				//		fontColor: "#09113b"
				//		}
				//	}]
				//	},      
				title: {
					display: true,
					//text: 'MEMORY USAGE'
				},
				//	animation: {
				//		duration: 500,
				//		easing: "easeOutQuart",
				//		onComplete: function () {
				//		var ctx = this.chart.ctx;
				//		ctx.font = Chart.helpers.fontString(10, 'bold', Chart.defaults.global.defaultFontFamily);
				//		ctx.textAlign = 'center';
				//		ctx.textBaseline = 'bottom';

				//		this.data.datasets.forEach(function (dataset) {
				//			for (var i = 0; i < dataset.data.length; i++) {
				//					var model = dataset._meta[Object.keys(dataset._meta)[0]].data[i]._model,
				//					scale_max = dataset._meta[Object.keys(dataset._meta)[0]].data[i]._yScale.maxHeight;
				//					ctx.fillStyle = '#292b2c';
				//					var y_pos = model.y - 5;
				//					// Make sure data value does not get overflown and hidden
				//					// when the bar's value is too close to max value of scale
				//					// Note: The y value is reverse, it counts from top down
				//				if ((scale_max - model.y) / scale_max >= 0.93)
				//						y_pos = model.y + 20;
				//				if(parseFloat(dataset.percData[i]).toFixed(2) != "0.00" && parseFloat(dataset.percData[i]).toFixed(2) != "100.00") {
				//						ctx.fillText(parseFloat(dataset.percData[i]).toFixed(2) + "%", model.x + 1, y_pos);
				//				}
				//				}
				//			});
				//		}
				//	},
				labels: { fontColor: 'blue' }
			}
		};


		chartCanvas.width = 450;
		chartCanvas.height = 330;
		stackedCtx = chartCanvas.getContext('2d');
		//this.chartShowHide = true;
		chart = new Chart(stackedCtx, config);
		setTimeout(() => {
			//document.getElementById("chartBlock_bar").appendChild( chartCanvas );
			document.getElementById("trendChart").appendChild(chartCanvas);
			if ($("#trendChart").find("canvas")[1]) {
				$("#trendChart").find("canvas")[1].remove();
			}
		}, 100);

	}


  // Technology Pie Chart
  showCommissioningReport(tech1, tech2, tech3, tech4, tech5){

    let chartCanvas : any = document.createElement('canvas'),
        chart : any,
        stackedCtx : any,
        maxValue;
    
    let config = {
      type: 'pie',
      data: {
          // datasets: dataset,
          // labels:labels    
          datasets: [{
            data: [tech1, tech2, tech3, tech4, tech5],
            backgroundColor: ["#9B3192","#FF6361", "#F7B7A3", "#FFA600", "#2B0B3F"],
            borderWidth: [0, 0, 0, 0, 0]
        }],
        labels: ["4G USM LIVE", "4G FSU", "5G MM", "5G DSS", "5G CBAND"]
      },      
      options: {
          responsive: true,
          legend: {
              position: 'bottom',
              labels:{
                boxWidth:10
              }
            
          },
          // scales: {
          //   yAxes: [{
          //     ticks: {
          //       beginAtZero : true,
          //       stepSize: 200,
          //       fontColor:'#5E5C5C',
          //       fontSize: 10                
          //     }
          //   }],
          //   xAxes:[{
          //     barThickness:25,
          //     ticks:{
          //       autoSkip: false,
          //       maxRotation: 15,
          //       minRotation: 15,
          //       fontSize:10               
          //     }
          //   }]                   
          // },      
          title: {
              display: true,
              //text: 'MEMORY USAGE'
          },
          animation: {
            animateScale: false,
            animateRotate: false
          }
          // labels:{fontColor:'blue'}    
      }
  };         
    chartCanvas.width = 450; 
    chartCanvas.height = 330; 
    stackedCtx = chartCanvas.getContext('2d');
    //this.chartShowHide = true;
    chart = new Chart(stackedCtx, config);
    setTimeout(() => { 
      //document.getElementById("chartBlock_bar").appendChild( chartCanvas );
      document.getElementById("commissioningReport").appendChild( chartCanvas );
      if($("#commissioningReport").find("canvas")[1]){
        $("#commissioningReport").find("canvas")[1].remove();
      }
     }, 100);

  }
  
  showScheduleChart(scheduledPercentage, cancelledPercentage){

    let chartCanvas : any = document.createElement('canvas'),
        chart : any,
        stackedCtx : any,
        maxValue;
    
    let config = {
      type: 'doughnut',
      data: {
          datasets: [{
              data: [76, 100, 24],//[scheduledPercentage.replace(" %",""), cancelledPercentage.replace(" %","")],
              backgroundColor: ["#0e183b","#5161AC", "#a7b3e9"],
              borderWidth: [0, 0, 0]
          }],
          labels: ["Scheduled", "Migrated", "Cancelled"]
      },
      options: {
          responsive: false,
          legend: {
              position: 'bottom',
              labels:{
                boxWidth:12
              }
          },
          title: {
              display: true,
              //text: 'MEMORY USAGE'
          },
          animation: {
              animateScale: false,
              animateRotate: false
          }
      }
  }; 
       
    chartCanvas.width = 260;
    chartCanvas.height = 260;
    stackedCtx = chartCanvas.getContext('2d');
    //this.chartShowHide = true;
    chart = new Chart(stackedCtx, config);
    setTimeout(() => { 
      //document.getElementById("chartBlock_bar").appendChild( chartCanvas );
      document.getElementById("chartBlockSchedule").appendChild( chartCanvas );
      if($("#chartBlockSchedule").find("canvas")[1]){
        $("#chartBlockSchedule").find("canvas")[1].remove();
      }
     }, 100);
  }

  // Market Pie Chart
  showReasonsChart(market1, market2, market3, market4, market5, market6, market7, market8, market9){

    let chartCanvas : any = document.createElement('canvas'),
        chart : any,
        stackedCtx : any,
        maxValue;
    
    let config = {
      type: 'pie',
      
      data: {
          // datasets:dataset,// [{"label":"Scheduled","backgroundColor":"#0e183b","data":["540","700","866","300"]},{"label":"Migrated","backgroundColor":"#5161AC","data":["600","560","934","350"]},{"label":"Cancelled","backgroundColor":"#a7b3e9","data":["220","200","400","200"]}],
          // labels:labels    //["Jan 18","Feb 18","Mar 18", "Apr 18"]
          datasets: [{
            data: [market1, market2, market3, market4, market5, market6, market7, market8, market9],
            backgroundColor: ["#3C9D4E","#7031AC", "#C94D6D", "#E4BF58", "#4174C9", "#041E42", "#FEAE65", "#FF6361", "#003F5C"],
            borderWidth: [0, 0, 0, 0, 0, 0, 0, 0, 0]
        }],
        labels: ["CTX", "HGC", "NE", "NYM", "OPW", "SOC", "TRI", "WBV", "UNY"]
      },      
      options: {
          responsive: true,
          // layout: {
          //     padding: {
          //         left: 20
          //     }
          // },
          legend: {
              position: 'bottom',
              labels:{
                boxWidth:10
              }
            
          },
          // scales: {
          //   yAxes: [{
          //     ticks: {
          //       beginAtZero : true,
          //       fontColor:'#5E5C5C',
          //       fontSize: 10                
          //     }
          //   }],
          //   xAxes:[{
          //     barThickness:35,
          //     ticks:{
          //       autoSkip: false,
          //       maxRotation: 15,
          //       minRotation: 0,
          //       fontSize:11,
          //       fontStyle: "bold",
          //       fontColor: "#09113b"
          //     }
          //   }]
          // },      
          title: {
              display: true,
              //text: 'MEMORY USAGE'
          },
          animation: {
            animateScale: false,
            animateRotate: false
              // duration: 500,
              // easing: "easeOutQuart",
              // onComplete: function () {
              //     var ctx = this.chart.ctx;
              //     ctx.font = Chart.helpers.fontString(10, 'bold', Chart.defaults.global.defaultFontFamily);
              //     ctx.textAlign = 'center';
              //     ctx.textBaseline = 'bottom';

              //     this.data.datasets.forEach(function (dataset) {
              //         for (var i = 0; i < dataset.data.length; i++) {
              //             var model = dataset._meta[Object.keys(dataset._meta)[0]].data[i]._model,
              //                 scale_max = dataset._meta[Object.keys(dataset._meta)[0]].data[i]._yScale.maxHeight;
              //             ctx.fillStyle = '#292b2c';
              //             var y_pos = model.y - 5;
              //             // Make sure data value does not get overflown and hidden
              //             // when the bar's value is too close to max value of scale
              //             // Note: The y value is reverse, it counts from top down
              //             if ((scale_max - model.y) / scale_max >= 0.93)
              //                 y_pos = model.y + 20;
              //             if(parseFloat(dataset.percData[i]).toFixed(2) != "0.00" && parseFloat(dataset.percData[i]).toFixed(2) != "100.00") {
              //                 ctx.fillText(parseFloat(dataset.percData[i]).toFixed(2) + "%", model.x + 1, y_pos);
              //             }
              //         }
              //     });
              // }
          }
          // labels:{fontColor:'blue'}    
      }
  };         
    chartCanvas.width = 450; 
    chartCanvas.height = 330; 
    stackedCtx = chartCanvas.getContext('2d');
    //this.chartShowHide = true;
    chart = new Chart(stackedCtx, config);
    setTimeout(() => { 
      //document.getElementById("chartBlock_bar").appendChild( chartCanvas );
      document.getElementById("reasonsReport").appendChild( chartCanvas );
      if($("#reasonsReport").find("canvas")[1]){
        $("#reasonsReport").find("canvas")[1].remove();
      }
    }, 100);

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

  viewActiveUsers(activeUserModal) {
    this.showLoader = true;

    this.dashboardService.getActiveUsers(this.sharedService.createServiceToken())
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
                }
                else {
                    if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {

                        if (jsonStatue.status == "SUCCESS") {
                            this.showLoader = false;

                            this.activeUsersList = jsonStatue.userDetails;
                            this.activeUserBlock = this.modalService.open(activeUserModal, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView' });
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
                    
                    let jsonStatue = {"reason":"","sessionId":"ebe0d38c","serviceToken":"85857","userDetails":[{"id":3,"userName":"superadmin","roleId":2,"customerId":1,"lastLoginTime":"2016-02-12 16:25:51","serviceToken":"55021","loginDate":"12-02-2016 16:39:19","createdBy":"superadmin","programName":["All"],"role":"Super Administrator","userFullName":"Super Administrator","emailId":"arun.ravindranath123@ltts.com","lastAccessedTime":1455275400754,"tokenKey":"ebe0d38c"}],"status":"SUCCESS"};

                    if (jsonStatue.status == "SUCCESS") {
                        this.showLoader = false;
                        
                        this.activeUsersList = jsonStatue.userDetails;
                        this.activeUserBlock = this.modalService.open(activeUserModal, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal modalWidth scriptView' });
                    } else {
                        this.showLoader = false;
                        this.displayModel(jsonStatue.reason, "failureIcon");

                    }

                }, 100); */

                //Please Comment while checkIn   
            });
  }
  closeModelActiveUsers() {
    this.activeUserBlock.close();
  }
  
}