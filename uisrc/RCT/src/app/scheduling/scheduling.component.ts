import { ViewContainerRef, Input, ElementRef, Renderer, Component, OnInit, ViewChild, HostListener, SystemJsNgModuleLoader } from '@angular/core';
// import { Router} from '@angular/router';
import { Router, NavigationEnd } from '@angular/router';
import { NgbModal, ModalDismissReasons } from '@ng-bootstrap/ng-bootstrap';
import { SchedulingService } from '../services/scheduling.service';
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
import { keyframes } from '@angular/animations';

@Component({
  selector: 'app-scheduling',
  templateUrl: './scheduling.component.html',
  styleUrls: ['./scheduling.component.scss'],
  providers: [SchedulingService]
})
export class SchedulingComponent implements OnInit {
  tableEdit: boolean = false;
  custList: any;
  feRegionList: any;
  currEditedRowVal: any;
  editableFormArray = [];
  userList: any;
  regionList: any;
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
  editIndex: number = -1;
  schedulingVerizonModelList: any;
  forecastStartDate: any;
  compDate: any;
  feArrivalTime: any;
  sprintStartDate: any;
  ciStartTime: any;
  ciEndTime: any;
  createMarketList: any;
  createRegionList: any;
  createFeregionList: any;
  day:any;
  month:any;
  year:any;
  week:any;
  quarter:any;
  createFeDayList:any;
  createFeNightList:any;
  // createSprintSchedulingform:any;
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
      },
      "sprintRegion": {
        "required": true
      },
      "sprintMarket": {
        "required": true
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
      "compDate": {
        "required": "Comp Date is required",
      },
      "sprintRegion": {
        "required": "Region is required"
      },
      "sprintMarket": {
        "required": "Market is required",
      }
    }
  };

  // What to clone
  @ViewChild('neVersionAddInlineRow') neversionTemplate;
  // Where to insert the cloned content
  @ViewChild('neversionContainer', { read: ViewContainerRef }) neversionContainer;


  @ViewChild('searchTab') searchTabRef: ElementRef;
  @ViewChild('confirmModal') confirmModalRef: ElementRef;
  @ViewChild('bluePrintForm') bluePrintForm;
  @ViewChild('createSprintSchedulingform') createSprintSchedulingform;
  @ViewChild('searchForm') searchForm;
  @ViewChild('createTab') createTabRef: ElementRef;
  @ViewChild('sessionExpiredModal') sessionExpiredModalRef: ElementRef;
  @ViewChild('successModal') successModalRef: ElementRef;
  @ViewChild('filePost') filePostRef: ElementRef;



  constructor(
    private element: ElementRef,
    private schedulingService: SchedulingService,
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
      this.getSchedulingDetails();
      this.showLoader = true;
    }


    this.getCustomerList();

    //JSON.parse(sessionStorage.selectedCustomerList).id,
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
    this.searchBlock = true;
    this.sprintStartDate = "";
    this.forecastStartDate = "";
    this.compDate = "";
    this.feArrivalTime = "";
    this.ciStartTime = "";
    this.ciEndTime = "";
    this.createBlock = false;
    this.searchStatus = 'load';
    this.setMenuHighlight("search");
    this.currentPage = 1;
    if (this.currentUser != "Super Administrator") {

      this.custId = JSON.parse(sessionStorage.selectedCustomerList).id;
      //this.custId=2;
      this.getSchedulingDetails();
      // this.showLoader = true;
    }
    else if (this.selCustName != "") {

      this.getSchedulingDetails();

    }

    this.bluePrintForm.nativeElement.reset();

  }


  createTabBind() {
    this.searchBlock = false;
    this.createBlock = true;
    this.setMenuHighlight("create");

  }

  getSchedulingDetails() {

    this.tableShowHide = false;
    this.editIndex = -1;
    this.showLoader = true;
    $("#dataWrapper").find(".scrollBody").scrollLeft(0);
    this.schedulingService.getSchedulingDetails(this.searchStatus, this.searchDetails, this.sharedService.createServiceToken(), this.paginationDetails, this.custId)
      .subscribe(
        data => {
          setTimeout(() => {
            if (this.custId == 2) {
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

                    if (jsonStatue.schedulingVerizonModelList.length == 0) {
                      this.tableShowHide = false;
                      this.noDataVisibility = true;
                    } else {
                      this.verizonData = jsonStatue.schedulingVerizonModelList;
                      this.tableShowHide = true;
                      this.noDataVisibility = false;
                      setTimeout(() => {
                        let tableWidth = document.getElementById('schedulingVerizonModelList').scrollWidth;
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
              let jsonStatue = data.json();


              //this.enbNameList=this.tableData.enodebName; 
              if (jsonStatue.sessionId == "408" || jsonStatue.status == "Invalid User") {

                this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });

              } else {

                if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {
                  if (jsonStatue.status == "SUCCESS") {
                    this.showLoader = false;
                    this.tableData = jsonStatue;
                    this.userList = jsonStatue.username;
                    this.marketList = jsonStatue.market;
                    this.regionList = jsonStatue.region;
                    this.createMarketList = jsonStatue.marketDetailsList.market;
                    this.createRegionList = jsonStatue.regionDetailsList.region;
                    this.createFeregionList = jsonStatue.feregionDetailsList.feRegion;
                    this.feRegionList = jsonStatue.feregionDetailsList.feRegion;
                    this.createFeDayList=jsonStatue.fenightDetailsList.feNight;
                    this.createFeNightList=jsonStatue.fedayDetailsList.feDay;
                    this.totalPages = this.tableData.pageCount;


                    let pageCount = [];
                    for (var i = 1; i <= this.tableData.pageCount; i++) {
                      pageCount.push(i);
                    }
                    this.pageRenge = pageCount;
                    this.pager = this.getPager(this.totalPages * this.pageSize, this.currentPage, this.pageRenge);

                    if (this.tableData.schedulingSprintEntity.length == 0) {
                      this.tableShowHide = false;
                      this.noDataVisibility = true;
                    } else {
                      this.sprintData = this.tableData.schedulingSprintEntity;
                      this.tableShowHide = true;
                      this.noDataVisibility = false;
                      setTimeout(() => {
                        let tableWidth = document.getElementById('schedulingSprintEntity').scrollWidth;
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
            if (this.custId == 2) {
              this.showLoader = false;
              //this.tableData = {"pageCount":1,"schedulingVerizonModelList":[{"id":4,"forecastStartDate":"2019-03-20 19:35:19","compDate":"VLSM","market":"Upstate NY","enbId":"71071","enbName":"QUEENSBURY","growRequest":"Y","growCompleted":"Y","ciqPresent":"Y","envCompleted":"Y","standardNonStandard":"Standard","carriers":"AWS","uda":"","softwareLevels":"","feArrivalTime":"","ciStartTime":null,"dtHandoff":"","ciEndTime":null,"canRollComp":null,"traffic":"","alarmPresent":null,"ciEngineer":"B","ft":"Y Mohamed","dt":"Oluwaseun","notes":"Bridge #5"},{"id":5,"forecastStartDate":"2019-03-23 11:56:44","compDate":"vLSM","market":"New England","enbId":"71071","enbName":"QUEENSBURY","growRequest":"Y","growCompleted":"DAVID 3/14","ciqPresent":"Y","envCompleted":"Y","standardNonStandard":"Standard","carriers":"aws","uda":"","softwareLevels":"","feArrivalTime":"","ciStartTime":"","dtHandoff":"","ciEndTime":"","canRollComp":"","traffic":"","alarmPresent":"","ciEngineer":"B","ft":"Y Mohammed","dt":"oluwasen","notes":"Bridge #5"}],"sessionId":"fff0e445","serviceToken":"74388","status":"SUCCESS"};
              //this.tableData = {"sessionId":"fff0e445","serviceToken":"74388","pageCount":1,"schedulingVerizonModelList":[{"id":4,"forecastStartDate":"2019-03-20 19:35:19","compDate":"VLSM","market":"Upstate NY","enbId":"71071","enbName":"QUEENSBURY","growRequest":"Y","growCompleted":"Y","ciqPresent":"Y","envCompleted":"Y","standardNonStandard":"Standard","carriers":"AWS","uda":"qwert","softwareLevels":"asd","feArrivalTime":"sdfs","ciStartTime":"sdf","dtHandoff":"xcv","ciEndTime":"nulldf","canRollComp":"nudfgdll","traffic":"xcvd","alarmPresent":"nullcxv","ciEngineer":"B","ft":"Y Mohamed","dt":"Oluwaseun","notes":"Bridge #5"},{"id":5,"forecastStartDate":"2019-03-23 11:56:44","compDate":"vLSM","market":"New England","enbId":"71071","enbName":"QUEENSBURY","growRequest":"Y","growCompleted":"DAVID 3/14","ciqPresent":"Y","envCompleted":"Y","standardNonStandard":"Standard","carriers":"aws","uda":"sdffgh","softwareLevels":"sadfdg","feArrivalTime":"Zxxc","ciStartTime":"asdas","dtHandoff":"xcvdf","ciEndTime":"asdf","canRollComp":"asdw3","traffic":"xzvds","alarmPresent":"sdgfb","ciEngineer":"B","ft":"Y Mohammed","dt":"oluwasen","notes":"Bridge #5"}],"status":"SUCCESS"};
              this.tableData = { "market": ["", "AWS2 Alpha and Beta locked for T-Mob Spectrum swap", "AWS2 1_3, 2_3, & 3_3 LOCKED per email from Chandra Kallem Fri 5/18/2018 5:08 PM Fwd: Sectors carriers that needs to be locked after migration.", "See details below.", "AWS-1 Gamma RRH DC INPUT FAIL alarm", "There is Low Gain Alarm on Beta AWS on Path 1 but it is still taking calls.", "Site was cancelled by FOPS due to GPS cabling issues. Lorenzo made the call.", "Upstate New York", "No alarms;  All carriers are now taking traffic. UDA alarms were temporarily not equipped as requested by FE Jim pending the completion of the alarm terminations.", "No issue", "AWS2 2_3 & 3_3 LOCKED per email from Chandra Kallem Fri 5/18/2018 5:08 PM Fwd: Sectors carriers that needs to be locked after migration", "New England", "PCS carrier migration was completed and taking traffic. All carriers are now taking traffic", "Re-run GP, CA and Site specific scripts. All carriers are taking traffic", "Site has no alarms and taking traffic. DT on-going", "No issues.", "VzW requested drive test prior to migration", "test sreeraj", "GPS Cable not working. Rolled back by FOPS, Garrett Poor made the call.", "No Issue"], "enodebName": ["", "Z1909", "Z1908", "Z1907", "Z1906", "Z1901", "Z1900", "Z1905", "Z1904", "Z1903", "Z1902", "Z1919", "Z1918", "Z1917", "Z1912", "Z1911", "Z1910", "Z1916", "Z1915", "Z1914", "Z1913", "Z1929", "Z1928", "Z1923", "Z1922", "Z1921", "Z1920", "Z1927", "Z1926", "Z1925", "Z1924", "Z1930", "Z1939", "Z1934", "Z1933", "Z1932", "Z1931", "Z1938", "Z1937", "Z1936", "Z1935", "Z1941", "Z1940", "Z1029", "Z1035", "Z2366", "Z1034", "Z2365", "Z1033", "Z2364", "Z1032", "Z2363", "Z1039", "Z1038", "Z2369", "Z1037", "Z2368", "Z1036", "Z2367", "Z1031", "Z2362", "Z1030", "Z2361", "Z2360", "Z1046", "Z2377", "Z1045", "Z2376", "Z1044", "Z2375", "Z1043", "Z2374", "Z1049", "Z1048", "Z2379", "Z1047", "Z2378", "Z1042", "Z2373", "Z1041", "Z2372", "Z1040", "Z2371", "Z2370", "Z1057", "Z2388", "Z1056", "Z2387", "Z1055", "Z2386", "Z1054", "Z2385", "Z1059", "Z1058", "Z2389", "Z2380", "Z1053", "Z2384", "Z1052", "Z2383", "Z1051", "Z2382", "Z1050", "Z2381", "Z1068", "Z2399", "Z1067", "Z2398", "Z1066", "Z2397", "Z1065", "Z2396", "Z1069", "Z1060", "Z2391", "Z2390", "Z1064", "Z2395", "Z1063", "Z2394", "Z1062", "Z2393", "Z1061", "Z2392", "Z803", "Z802", "Z805", "Z804", "Z807", "Z806", "Z809", "Z808", "Z1079", "Z1078", "Z1077", "Z1076", "Z810", "Z812", "Z811", "Z1071", "Z1070", "Z1075", "Z1074", "Z1073", "Z1072", "Z1089", "Z1088", "Z1087", "Z801", "Z800", "Z1082", "Z1081", "Z1080", "Z1086", "Z1085", "Z1084", "Z1083", "Z825", "Z824", "Z827", "Z826", "Z829", "Z828", "Z1099", "Z830", "Z1098", "Z832", "Z831", "Z834", "Z833", "Z1093", "Z1092", "Z1091", "Z1090", "Z1097", "Z1096", "Z1095", "Z1094", "Z814", "Z813", "Z816", "Z815", "Z818", "Z817", "Z819", "Z821", "Z820", "Z823", "Z822", "Z847", "Z846", "Z849", "Z848", "Z850", "Z852", "Z851", "Z2403", "Z854", "Z2402", "Z853", "Z2401", "Z856", "Z2400", "Z855", "Z2407", "Z836", "Z2406", "Z835", "Z2405", "Z838", "Z2404", "Z837", "Z839", "Z2409", "Z2408", "Z2410", "Z841", "Z840", "Z2414", "Z843", "Z2413", "Z842", "Z2412", "Z845", "Z2411", "Z844", "Z2418", "Z869", "Z2417", "Z868", "Z2416", "Z2415", "Z2419", "Z2421", "Z872", "Z2420", "Z871", "Z874", "Z873", "Z2425", "Z876", "Z2424", "Z875", "Z2423", "Z878", "Z2422", "Z877", "Z870", "Z2429", "Z858", "Z2428", "Z857", "Z2427", "Z2426", "Z859", "Z1101", "Z2432", "Z861", "Z1100", "Z2431", "Z860", "Z2430", "Z863", "Z862", "Z1105", "Z2436", "Z865", "Z1104", "Z2435", "Z864", "Z1103", "Z2434", "Z867", "Z1102", "Z2433", "Z866", "Z1109", "Z1108", "Z2439", "Z1107", "Z2438", "Z1106", "Z2437", "Z1112", "Z2443", "Z894", "Z1111", "Z2442", "Z893", "Z1110", "Z2441", "Z896", "Z2440", "Z895", "Z1116", "Z2447", "Z898", "Z1115", "Z2446", "Z897", "Z1114", "Z2445", "Z1113", "Z2444", "Z899", "Z890", "Z892", "Z891", "Z1119", "Z879", "Z1118", "Z2449", "Z1117", "Z2448", "Z1123", "Z2454", "Z883", "Z1122", "Z2453", "Z882", "Z1121", "Z2452", "Z885", "Z1120", "Z2451", "Z884", "Z1127", "Z2458", "Z887", "Z1126", "Z2457", "Z886", "Z1125", "Z2456", "Z889", "Z1124", "Z2455", "Z888", "Z2450", "Z881", "Z880", "Z1129", "Z1128", "Z2459", "Z1134", "Z2465", "Z1133", "Z2464", "Z1132", "Z2463", "Z1131", "Z2462", "Z1138", "Z2469", "Z1137", "Z2468", "Z1136", "Z2467", "Z1135", "Z2466", "Z1130", "Z2461", "Z2460", "Z1139", "Z1145", "Z2476", "Z1144", "Z2475", "Z1143", "Z2474", "Z1142", "Z2473", "Z1149", "Z1148", "Z2479", "Z1147", "Z2478", "Z1146", "Z2477", "Z1141", "Z2472", "Z1140", "Z2471", "Z2470", "Z1945", "Z1944", "Z1943", "Z1942", "Z1949", "Z1948", "Z1947", "Z1946", "Z1952", "Z1951", "Z1950", "Z1956", "Z1955", "Z1954", "Z1953", "Z1959", "Z1958", "Z1957", "Z1963", "Z1962", "Z1961", "Z1960", "Z1967", "Z1966", "Z1965", "Z1964", "Z1969", "Z1968", "Z1970", "Z1974", "Z1973", "Z1972", "Z1971", "Z1978", "Z1977", "Z1976", "Z1975", "Z1979", "Z1981", "Z1980", "Z1985", "Z1984", "Z1983", "Z1982", "072588_MONTOUR_FALLS", "Z1989", "Z1988", "Z1987", "Z1986", "Z1992", "Z1991", "Z1990", "Z1996", "Z1995", "Z1994", "Z1993", "Z1999", "Z1998", "Z1997", "test", "Z1809", "Z1808", "Z1807", "Z1802", "Z1801", "Z1800", "Z1806", "Z1805", "Z1804", "Z1803", "Z1819", "Z1818", "Z1813", "Z1812", "Z1811", "Z1810", "Z1817", "Z1816", "Z1815", "Z1814", "Z1820", "Z2239", "Z2245", "Z2244", "Z2243", "Z2242", "Z2249", "Z2248", "Z2247", "Z2246", "Z2241", "Z2240", "Z2256", "Z2255", "Z2254", "Z2253", "Z2259", "Z2258", "Z2257", "Z2252", "Z2251", "Z2250", "Z909", "Z902", "Z901", "Z904", "Z903", "Z906", "Z905", "Z908", "Z907", "Z2267", "Z2266", "Z2265", "Z2264", "Z2269", "Z911", "Z2268", "Z910", "Z2263", "Z2262", "Z2261", "Z2260", "Z2278", "Z2277", "Z2276", "Z2275", "Z900", "Z2279", "Z2270", "Z2274", "Z2273", "Z2272", "Z2271", "Z924", "Z923", "Z926", "Z925", "Z928", "Z927", "Z929", "Z2289", "Z2288", "Z2287", "Z2286", "Z931", "Z930", "Z933", "Z932", "Z2281", "Z2280", "Z2285", "Z2284", "Z2283", "Z2282", "Z913", "Z912", "Z915", "Z914", "Z917", "Z916", "Z919", "Z918", "Z2299", "Z2298", "Z2297", "Z920", "Z922", "Z921", "Z2292", "Z2291", "Z2290", "Z2296", "Z2295", "Z2294", "Z2293", "Z946", "Z945", "Z948", "Z947", "Z949", "Z951", "Z950", "Z953", "Z952", "Z955", "Z954", "Z935", "Z934", "Z937", "Z936", "Z939", "Z938", "Z940", "Z942", "Z941", "Z944", "Z943", "Z968", "Z967", "Z969", "Z971", "Z970", "Z973", "Z972", "Z975", "Z974", "Z977", "Z976", "Z957", "Z956", "Z959", "Z958", "Z960", "Z962", "Z961", "Z964", "Z963", "Z966", "Z965", "Z989", "Z2300", "Z993", "Z992", "Z995", "Z994", "Z2304", "Z997", "Z2303", "Z996", "Z2302", "Z999", "Z2301", "Z998", "Z991", "Z990", "Z2308", "Z979", "Z2307", "Z978", "Z2306", "Z2305", "Z2309", "Z2311", "Z982", "Z2310", "Z981", "Z984", "Z983", "Z2315", "Z986", "Z2314", "Z985", "Z2313", "Z988", "Z2312", "Z987", "Z980", "Z2319", "Z2318", "Z2317", "Z2316", "Z2322", "Z2321", "Z2320", "Z2326", "Z2325", "Z2324", "Z2323", "Z2329", "Z2328", "Z2327", "Z1002", "Z2333", "Z1001", "Z2332", "Z1000", "Z2331", "Z2330", "Z1006", "Z2337", "Z1005", "Z2336", "Z1004", "Z2335", "Z1003", "Z2334", "Z1009", "Z1008", "Z2339", "Z1007", "Z2338", "Z1013", "Z2344", "Z1012", "Z2343", "Z1011", "Z2342", "Z1010", "Z2341", "Z1017", "Z2348", "071269_TICONDEROGA", "Z1016", "Z2347", "Z1015", "Z2346", "Z1014", "Z2345", "Z2340", "Z1019", "Z1018", "Z2349", "Z1024", "Z2355", "Z1023", "Z2354", "Z1022", "Z2353", "Z1021", "Z2352", "Z1028", "Z2359", "Z1027", "Z2358", "Z1026", "Z2357", "Z1025", "Z2356", "Z1020", "Z2351", "Z2350", "Z1829", "Z1824", "Z1823", "Z1822", "Z1821", "Z1828", "Z1827", "Z1826", "Z1825", "Z1831", "Z1830", "Z1835", "Z1834", "Z1833", "Z1832", "Z1839", "Z1838", "Z1837", "Z1836", "Z1842", "Z1841", "Z1840", "Z1846", "Z109", "Z1845", "Z1844", "Z1843", "Z1849", "Z1848", "Z1847", "Z113", "Z112", "Z115", "Z114", "Z117", "Z1853", "Z116", "Z1852", "Z119", "Z1851", "Z118", "Z1850", "Z111", "Z110", "Z1857", "Z1856", "Z1855", "Z1854", "Z1859", "Z1858", "Z102", "Z1860", "Z101", "Z104", "Z103", "Z106", "Z1864", "Z105", "Z1863", "Z108", "Z1862", "Z107", "Z1861", "Z100", "Z1868", "Z1867", "Z1866", "Z1865", "Z1869", "Z135", "Z1871", "Z134", "Z1870", "Z137", "Z136", "Z139", "Z1875", "Z138", "Z1874", "Z1873", "Z1872", "Z131", "Z130", "Z133", "Z132", "Z1879", "Z1878", "Z1877", "Z1876", "Z124", "Z1882", "Z123", "Z1881", "Z126", "Z1880", "Z125", "Z128", "Z1886", "Z127", "Z1885", "Z1884", "Z129", "Z1883", "Z120", "Z122", "Z121", "Z1889", "Z1888", "Z1887", "Z157", "Z1893", "Z156", "Z1892", "Z159", "Z1891", "Z158", "Z1890", "Z1897", "Z1896", "Z1895", "Z1894", "Z151", "Z150", "Z153", "Z152", "Z155", "Z154", "Z1899", "Z1898", "Z146", "Z145", "Z148", "Z147", "Z149", "Z140", "Z142", "Z141", "Z144", "Z143", "Z179", "Z178", "Z3056", "Z3059", "Z171", "Z3052", "Z170", "Z3053", "Z173", "Z3054", "Z172", "Z3055", "Z175", "Z174", "Z177", "Z3050", "Z176", "Z3051", "Z168", "Z167", "Z169", "Z3067", "Z3068", "Z3069", "Z160", "Z3063", "Z3064", "Z162", "Z3065", "Z161", "Z3066", "Z164", "Z163", "Z3060", "Z166", "Z3061", "Z165", "Z3062", "Z191", "Z190", "Z3078", "Z3079", "Z193", "Z3074", "Z192", "Z3075", "Z195", "Z3076", "Z194", "Z3077", "Z197", "Z3070", "Z196", "Z3071", "Z199", "Z3072", "Z198", "Z3073", "Z3080", "Z180", "Z189", "Z3089", "Z182", "Z3085", "Z181", "Z3086", "Z184", "Z3087", "Z183", "Z3088", "Z186", "Z3081", "Z185", "Z3082", "Z188", "Z3083", "Z187", "Z3084", "Z3090", "Z3091", "Z3096", "Z3097", "Z3098", "Z3099", "Z3092", "Z3093", "Z3094", "Z3095", "Z1277", "Z1276", "Z1275", "Z1274", "Z1279", "Z1278", "Z1273", "Z1272", "Z1271", "Z1270", "Z1288", "Z1287", "Z1286", "Z1285", "Z1289", "Z1280", "Z1284", "Z1283", "Z1282", "Z1281", "Z1299", "Z1298", "Z1297", "Z1296", "Z1291", "Z1290", "Z1295", "Z1294", "Z1293", "Z1292", "Z3005", "Z3006", "Z3007", "Z3008", "Z3001", "Z3002", "Z3003", "Z3004", "Z3000", "Z3009", "Z3016", "Z3017", "Z3018", "Z3019", "Z3012", "Z3013", "Z3014", "Z3015", "Z3010", "Z3011", "Z3027", "Z3028", "Z3029", "Z3023", "Z3024", "Z3025", "Z3026", "Z3020", "Z3021", "Z3022", "Z3038", "Z3039", "Z3034", "Z3035", "Z3036", "Z3037", "Z3030", "Z3031", "Z3032", "Z3033", "Z3049", "Z3045", "Z3046", "Z3047", "Z3048", "Z3041", "Z3042", "Z3043", "Z3044", "Z3040", "Z1307", "Z2638", "Z1306", "Z2637", "Z1305", "Z2636", "Z1304", "Z2635", "Z1309", "Z1308", "Z2639", "Z1310", "Z2641", "Z2640", "Z1314", "Z2645", "Z1313", "Z2644", "Z1312", "Z2643", "Z1311", "Z2642", "Z1318", "Z2649", "Z1317", "Z2648", "Z1316", "Z2647", "Z1315", "Z2646", "Z1319", "Z1321", "Z2652", "Z1320", "Z2651", "Z2650", "Z1325", "Z2656", "Z1324", "Z2655", "Z1323", "Z2654", "Z1322", "Z2653", "Z1329", "Z1328", "Z2659", "Z1327", "Z2658", "Z1326", "Z2657", "Z1332", "Z2663", "Z1331", "Z2662", "Z1330", "Z2661", "Z2660", "Z1336", "Z2667", "Z1335", "Z2666", "Z1334", "Z2665", "Z1333", "Z2664", "Z1339", "Z1338", "Z2669", "Z1337", "Z2668", "Z1343", "Z2674", "Z1342", "Z2673", "Z1341", "Z2672", "Z1340", "Z2671", "Z1347", "Z2678", "Z1346", "Z2677", "Z1345", "Z2676", "Z1344", "Z2675", "Z2670", "Z1349", "Z1348", "Z2679", "Z1354", "Z2685", "Z1353", "Z2684", "Z1352", "Z2683", "Z1351", "Z2682", "Z1358", "Z2689", "Z1357", "Z2688", "Z1356", "Z2687", "Z1355", "Z2686", "Z1350", "Z2681", "Z2680", "Z1359", "Z1365", "Z2696", "Z1364", "Z2695", "Z1363", "Z2694", "Z1362", "Z2693", "Z1369", "Z1368", "Z2699", "Z1367", "Z2698", "Z1366", "Z2697", "Z1361", "Z2692", "Z1360", "Z2691", "Z2690", "Z1376", "Z1375", "Z1374", "Z1373", "Z1379", "Z1378", "Z1377", "Z1372", "Z1371", "Z1370", "Z1387", "Z1386", "Z1385", "Z1384", "Z1389", "Z1388", "Z1383", "Z1382", "Z1381", "Z1380", "Z209", "Z208", "Z212", "Z211", "Z214", "Z213", "Z216", "Z215", "Z218", "Z217", "Z3", "Z4", "Z5", "Z6", "Z7", "Z210", "Z8", "Z9", "Z201", "Z200", "Z203", "Z202", "Z205", "Z204", "Z207", "Z206", "Z230", "Z231", "Z219", "Z223", "Z222", "Z225", "Z224", "Z227", "Z226", "Z229", "Z228", "Z221", "Z220", "Z2601", "Z2600", "Z2605", "Z2604", "Z2603", "Z2602", "Z2609", "Z2608", "Z2607", "Z2606", "Z2612", "Z2611", "Z2610", "Z2616", "Z2615", "Z2614", "Z2613", "Z2619", "Z2618", "Z2617", "Z278", "Z277", "Z279", "Z2623", "Z2622", "Z2621", "Z2620", "Z270", "Z272", "Z271", "Z274", "Z273", "Z276", "Z275", "Z2627", "Z2626", "Z2625", "Z2624", "Z2629", "Z2628", "Z2630", "Z267", "Z266", "Z269", "Z268", "Z1303", "Z2634", "Z1302", "Z2633", "Z1301", "Z2632", "Z1300", "Z2631", "Z290", "Z299", "Z292", "Z291", "Z294", "Z293", "Z296", "Z295", "Z298", "Z297", "Z289", "Z288", "Z281", "Z280", "Z283", "Z282", "Z285", "Z284", "Z287", "Z286", "Z1156", "Z2487", "Z1155", "Z2486", "Z1154", "Z2485", "Z1153", "Z2484", "Z1159", "Z1158", "Z2489", "Z1157", "Z2488", "Z1152", "Z2483", "Z1151", "Z2482", "Z1150", "Z2481", "Z2480", "Z1167", "Z2498", "Z1166", "Z2497", "Z1165", "Z2496", "Z1164", "Z2495", "Z1169", "Z1168", "Z2499", "Z2490", "Z1163", "Z2494", "Z1162", "Z2493", "Z1161", "Z2492", "Z1160", "Z2491", "Z1178", "Z1177", "Z1176", "Z1175", "Z1179", "Z1170", "Z1174", "Z1173", "Z1172", "Z1171", "Z1189", "Z1188", "Z1187", "Z1186", "Z1181", "Z1180", "Z1185", "Z1184", "Z1183", "Z1182", "Z1199", "Z1198", "Z1197", "Z1192", "Z1191", "Z1190", "Z1196", "Z1195", "Z1194", "Z1193", "Z2517", "Z2516", "Z2515", "Z2514", "Z2519", "Z2518", "Z2520", "Z2524", "Z2523", "Z2522", "Z2521", "Z2528", "Z2527", "Z2526", "Z2525", "Z2529", "Z1200", "Z2531", "Z2530", "Z1204", "Z2535", "Z1203", "Z2534", "Z1202", "Z2533", "Z1201", "Z2532", "Z1208", "Z2539", "Z1207", "Z2538", "Z1206", "Z2537", "Z1205", "Z2536", "Z1209", "Z1211", "Z2542", "Z1210", "Z2541", "Z2540", "Z1215", "Z2546", "Z1214", "Z2545", "Z1213", "Z2544", "Z1212", "Z2543", "Z1219", "Z1218", "Z2549", "Z1217", "Z2548", "Z1216", "Z2547", "Z1222", "Z2553", "Z1221", "Z2552", "Z1220", "Z2551", "Z2550", "Z1226", "Z2557", "Z1225", "Z2556", "Z1224", "Z2555", "Z1223", "Z2554", "Z1229", "Z1228", "Z2559", "Z1227", "Z2558", "Z1233", "Z2564", "Z1232", "Z2563", "Z1231", "Z2562", "Z1230", "Z2561", "Z1237", "Z2568", "Z1236", "Z2567", "Z1235", "Z2566", "Z1234", "Z2565", "Z2560", "Z1239", "Z1238", "Z2569", "Z1244", "Z2575", "Z1243", "Z2574", "Z1242", "Z2573", "Z1241", "Z2572", "Z1248", "Z2579", "Z1247", "Z2578", "Z1246", "Z2577", "Z1245", "Z2576", "Z1240", "Z2571", "Z2570", "Z308", "Z307", "Z1249", "Z309", "Z1255", "Z2586", "Z311", "Z1254", "Z2585", "Z310", "Z1253", "Z2584", "Z313", "Z1252", "Z2583", "Z312", "Z1259", "Z315", "Z1258", "Z2589", "Z314", "Z1257", "Z2588", "Z317", "Z1256", "Z2587", "Z316", "Z1251", "Z2582", "Z1250", "Z2581", "Z2580", "Z1266", "Z2597", "Z300", "Z1265", "Z2596", "Z1264", "Z2595", "Z302", "Z1263", "Z2594", "Z301", "Z304", "Z1269", "Z303", "Z1268", "Z2599", "Z306", "Z1267", "Z2598", "Z305", "Z1262", "Z2593", "Z1261", "Z2592", "Z1260", "Z2591", "Z2590", "Z329", "Z333", "Z332", "Z335", "Z334", "Z337", "Z336", "Z339", "Z338", "Z331", "Z330", "Z319", "Z318", "Z322", "Z321", "Z324", "Z323", "Z326", "Z325", "Z328", "Z327", "Z320", "Z355", "Z354", "Z357", "Z356", "Z359", "Z358", "Z351", "Z350", "Z353", "Z352", "Z344", "Z343", "Z346", "Z345", "Z348", "Z347", "Z349", "Z340", "Z342", "Z341", "Z377", "Z376", "Z379", "Z378", "Z371", "Z370", "Z373", "Z372", "Z375", "Z374", "Z366", "Z365", "Z368", "Z367", "Z369", "Z360", "Z362", "Z361", "Z364", "Z363", "Z399", "Z398", "Z2502", "Z2501", "Z2500", "Z391", "Z390", "Z393", "Z392", "Z395", "Z394", "Z397", "Z396", "Z2506", "Z2505", "Z2504", "Z2503", "Z2509", "Z2508", "Z2507", "Z388", "Z387", "Z389", "Z2513", "Z2512", "Z2511", "Z2510", "Z380", "Z382", "Z381", "Z384", "Z383", "Z386", "Z385", "Z3207", "Z3208", "Z3209", "Z3214", "Z3215", "Z3216", "Z3217", "Z3210", "Z3211", "Z3212", "Z3213", "Z3218", "Z3219", "Z3225", "Z3226", "Z3227", "Z3228", "Z3221", "Z3222", "Z3223", "Z3224", "Z3220", "Z3229", "Z3236", "Z3237", "Z3238", "Z3239", "Z3232", "Z3233", "Z3234", "Z3235", "Z3230", "Z3231", "Z3247", "Z3248", "Z3249", "Z3243", "Z3244", "Z3245", "Z3246", "Z3240", "Z3241", "Z3242", "Z3258", "Z3259", "Z3254", "Z3255", "Z3256", "Z3257", "Z3250", "Z3251", "Z3252", "Z3253", "Z3269", "Z3265", "Z3266", "Z3267", "Z3268", "Z3261", "Z3262", "Z3263", "Z3264", "Z3260", "Z3276", "Z3277", "Z3278", "Z3272", "Z3273", "Z3274", "Z3275", "Z3270", "Z3271", "Z1549", "Z1548", "Z2879", "Z1547", "Z2878", "Z1546", "Z2877", "Z1552", "Z2883", "Z1551", "Z2882", "Z1550", "Z2881", "Z2880", "Z1556", "Z2887", "Z1555", "Z2886", "Z1554", "Z2885", "Z1553", "Z2884", "Z1559", "Z1558", "Z2889", "Z1557", "Z2888", "Z1563", "Z2894", "Z1562", "Z2893", "Z1561", "Z2892", "Z1560", "Z2891", "Z1567", "Z2898", "Z1566", "Z2897", "Z1565", "Z2896", "Z1564", "Z2895", "Z2890", "Z1569", "Z1568", "Z2899", "Z1574", "Z1573", "Z1572", "Z1571", "Z1578", "Z1577", "Z1576", "Z1575", "Z1570", "Z1579", "Z1585", "Z1584", "Z1583", "Z1582", "Z1589", "Z1588", "Z1587", "Z1586", "Z1581", "Z1580", "Z407", "Z406", "Z409", "Z408", "Z1596", "Z410", "Z1595", "Z1594", "Z412", "Z1593", "Z411", "Z414", "Z1599", "Z413", "Z1598", "Z416", "Z1597", "Z415", "Z1592", "Z1591", "Z1590", "Z401", "Z400", "Z403", "Z402", "Z405", "Z404", "Z429", "Z428", "Z432", "Z431", "Z434", "Z433", "Z436", "Z435", "Z438", "Z437", "Z430", "Z418", "Z417", "Z419", "Z421", "Z420", "Z423", "Z422", "Z425", "Z424", "Z427", "Z426", "Z454", "Z453", "Z456", "Z455", "Z458", "Z457", "Z459", "Z450", "Z452", "Z451", "Z2809", "Z2808", "Z2803", "Z2802", "Z439", "Z2801", "Z2800", "Z2807", "Z2806", "Z2805", "Z2804", "Z443", "Z442", "Z445", "Z444", "Z2810", "Z447", "Z446", "Z449", "Z448", "Z441", "Z440", "Z2819", "Z2814", "Z2813", "Z2812", "Z2811", "Z2818", "Z2817", "Z2816", "Z2815", "Z476", "Z475", "Z478", "Z477", "Z2821", "Z2820", "Z479", "Z470", "Z472", "Z471", "Z474", "Z473", "Z2825", "Z2824", "Z2823", "Z2822", "Z2829", "Z2828", "Z2827", "Z2826", "Z465", "Z464", "Z467", "Z466", "Z1501", "Z2832", "Z469", "Z1500", "Z2831", "Z468", "Z2830", "Z461", "Z460", "Z463", "Z462", "Z1505", "Z2836", "Z1504", "Z2835", "Z1503", "Z2834", "Z1502", "Z2833", "Z1509", "Z1508", "Z2839", "Z1507", "Z2838", "Z1506", "Z2837", "Z498", "Z497", "Z499", "Z1512", "Z2843", "Z1511", "Z2842", "Z1510", "Z2841", "Z2840", "Z490", "Z492", "Z491", "Z494", "Z493", "Z496", "Z495", "Z1516", "Z2847", "Z1515", "Z2846", "Z1514", "Z2845", "Z1513", "Z2844", "Z1519", "Z1518", "Z2849", "Z1517", "Z2848", "Z2850", "Z487", "Z486", "Z489", "Z488", "Z1523", "Z2854", "Z1522", "Z2853", "Z1521", "Z2852", "Z1520", "Z2851", "Z481", "Z480", "Z483", "Z482", "Z485", "Z484", "Z1527", "Z2858", "Z1526", "Z2857", "Z1525", "Z2856", "Z1524", "Z2855", "Z1529", "Z1528", "Z2859", "Z1530", "Z2861", "Z2860", "Z1534", "Z2865", "Z1533", "Z2864", "Z1532", "Z2863", "Z1531", "Z2862", "Z1538", "Z2869", "Z1537", "Z2868", "Z1536", "Z2867", "Z1535", "Z2866", "Z1539", "Z1541", "Z2872", "Z1540", "Z2871", "Z2870", "Z1545", "Z2876", "Z1544", "Z2875", "Z1543", "Z2874", "Z1542", "Z2873", "Z3177", "Z3178", "Z3179", "Z3173", "Z3174", "Z3175", "Z3176", "Z3170", "Z3171", "Z3172", "Z3188", "Z3189", "Z3184", "Z3185", "Z3186", "Z3187", "Z3180", "Z3181", "Z3182", "Z3183", "Z3190", "Z3199", "Z3195", "Z3196", "Z3197", "Z3198", "Z3191", "Z3192", "Z3193", "Z3194", "Z1398", "Z1397", "Z1396", "Z1395", "Z1399", "Z1390", "Z1394", "Z1393", "Z1392", "Z1391", "Z3104", "Z3105", "Z3106", "Z3107", "Z3100", "Z3101", "Z3102", "Z3103", "Z3108", "Z3109", "Z3115", "Z3116", "Z3117", "Z3118", "Z3111", "Z3112", "Z3113", "Z3114", "Z3110", "Z3119", "Z3126", "Z3127", "Z3128", "Z3129", "Z3122", "Z3123", "Z3124", "Z3125", "Z3120", "Z3121", "Z3137", "Z3138", "Z3139", "Z3133", "Z3134", "Z3135", "Z3136", "Z3130", "Z3131", "Z3132", "Z3148", "Z3149", "Z3144", "Z3145", "Z3146", "Z3147", "Z3140", "Z3141", "Z3142", "Z3143", "Z3159", "Z3155", "Z3156", "Z3157", "Z3158", "Z3151", "Z3152", "Z3153", "Z3154", "Z3150", "Z3166", "Z3167", "Z3168", "Z3169", "Z3162", "Z3163", "Z3164", "Z3165", "Z3160", "Z3161", "Z1428", "Z2759", "Z1427", "Z2758", "Z1426", "Z2757", "Z1425", "Z2756", "Z1429", "Z1431", "Z2762", "Z1430", "Z2761", "Z2760", "Z1435", "Z2766", "Z1434", "Z2765", "Z1433", "Z2764", "Z1432", "Z2763", "Z1439", "Z1438", "Z2769", "Z1437", "Z2768", "Z1436", "Z2767", "Z1442", "Z2773", "Z1441", "Z2772", "Z1440", "Z2771", "Z2770", "Z1446", "Z2777", "Z1445", "Z2776", "Z1444", "Z2775", "Z1443", "Z2774", "Z506", "Z1449", "Z505", "Z1448", "Z2779", "Z508", "Z1447", "Z2778", "Z507", "Z509", "Z1453", "Z2784", "Z1452", "Z2783", "Z1451", "Z2782", "Z511", "Z1450", "Z2781", "Z510", "Z1457", "Z2788", "Z513", "Z1456", "Z2787", "Z512", "Z1455", "Z2786", "Z515", "Z1454", "Z2785", "Z514", "Z2780", "Z1459", "Z1458", "Z2789", "Z1464", "Z2795", "Z1463", "Z2794", "Z1462", "Z2793", "Z500", "Z1461", "Z2792", "Z1468", "Z2799", "Z502", "Z1467", "Z2798", "Z501", "Z1466", "Z2797", "Z504", "Z1465", "Z2796", "Z503", "Z1460", "Z2791", "Z2790", "Z528", "Z527", "Z1469", "Z529", "Z1475", "Z531", "Z1474", "Z530", "Z1473", "Z533", "Z1472", "Z532", "Z1479", "Z535", "Z1478", "Z534", "Z1477", "Z537", "Z1476", "Z536", "Z1471", "Z1470", "Z517", "Z516", "Z519", "Z518", "Z1486", "Z520", "Z1485", "Z1484", "Z522", "Z1483", "Z521", "Z524", "Z1489", "Z523", "Z1488", "Z526", "Z1487", "Z525", "Z1482", "Z1481", "Z1480", "Z549", "Z1497", "Z553", "Z1496", "Z552", "Z1495", "Z555", "Z1494", "Z554", "Z557", "Z556", "Z1499", "Z559", "Z1498", "Z558", "Z1493", "Z1492", "Z1491", "Z551", "Z1490", "Z550", "Z539", "Z538", "Z3203", "Z542", "Z3204", "Z541", "Z3205", "Z544", "Z3206", "Z543", "Z546", "Z3200", "Z545", "Z3201", "Z548", "Z3202", "Z547", "Z540", "Z575", "Z574", "Z577", "Z576", "Z579", "Z578", "Z571", "Z570", "Z573", "Z572", "Z564", "Z563", "Z566", "Z565", "Z568", "Z567", "Z569", "Z560", "Z562", "Z561", "Z597", "Z596", "Z599", "Z598", "Z2700", "Z591", "Z590", "Z593", "Z592", "Z595", "Z594", "Z2709", "Z2704", "Z2703", "Z2702", "Z2701", "Z2708", "Z2707", "Z2706", "Z2705", "Z586", "Z585", "Z588", "Z587", "Z2711", "Z2710", "Z589", "Z580", "Z582", "Z581", "Z584", "Z583", "Z2715", "Z2714", "Z2713", "Z2712", "Z2719", "Z2718", "Z2717", "Z2716", "Z2722", "Z2721", "Z2720", "Z2726", "Z2725", "Z2724", "Z2723", "Z2729", "Z2728", "Z2727", "Z1402", "Z2733", "Z1401", "Z2732", "Z1400", "Z2731", "Z2730", "Z1406", "Z2737", "Z1405", "Z2736", "Z1404", "Z2735", "Z1403", "Z2734", "Z1409", "Z1408", "Z2739", "Z1407", "Z2738", "Z2740", "Z1413", "Z2744", "Z1412", "Z2743", "Z1411", "Z2742", "Z1410", "Z2741", "Z1417", "Z2748", "Z1416", "Z2747", "Z1415", "Z2746", "Z1414", "Z2745", "Z1419", "Z1418", "Z2749", "Z1420", "Z2751", "Z2750", "Z1424", "Z2755", "Z1423", "Z2754", "Z1422", "Z2753", "Z1421", "Z2752", "Z10", "Z12", "Z11", "Z14", "Z13", "Z16", "Z15", "Z18", "Z17", "Z19", "Z21", "Z20", "Z23", "Z22", "Z25", "Z24", "Z27", "Z26", "Z29", "Z28", "Z30", "Z32", "Z31", "Z34", "Z33", "Z36", "Z35", "Z38", "Z37", "Z39", "Z41", "Z40", "Z43", "Z42", "Z45", "Z44", "Z47", "Z46", "Z49", "Z48", "Z50", "Z52", "Z51", "Z54", "Z53", "Z56", "Z55", "Z58", "Z57", "Z59", "Z61", "Z60", "Z63", "Z62", "Z65", "Z64", "Z67", "Z66", "Z69", "Z68", "Z70", "Z72", "Z71", "Z74", "Z73", "Z76", "Z75", "Z78", "Z77", "Z79", "Z81", "Z80", "Z83", "Z82", "Z85", "Z84", "Z87", "Z86", "Z89", "Z88", "Z90", "Z92", "Z91", "Z94", "Z93", "Z96", "Z95", "Z98", "Z97", "Z99", "Z2119", "Z2118", "Z2124", "Z2123", "Z2122", "Z2121", "Z2128", "Z2127", "Z2126", "Z2125", "Z2120", "Z2129", "Z2135", "Z2134", "Z2133", "Z2132", "Z2139", "Z2138", "Z2137", "Z2136", "Z2131", "Z2130", "Z2146", "Z2145", "Z2144", "Z2143", "Z2149", "Z2148", "Z2147", "Z2142", "Z2141", "Z2140", "Z2157", "Z2156", "Z2155", "Z2154", "Z2159", "Z2158", "Z2153", "Z2152", "Z2151", "Z2150", "Z2168", "Z2167", "Z2166", "Z2165", "Z2169", "Z2160", "Z2164", "Z2163", "Z2162", "Z2161", "Z2179", "Z2178", "Z2177", "Z2176", "Z2171", "Z2170", "Z2175", "Z2174", "Z2173", "Z2172", "Z2189", "Z2188", "Z2187", "Z2182", "Z2181", "Z2180", "Z2186", "Z2185", "Z2184", "Z2183", "Z2199", "Z2198", "Z2193", "Z2192", "Z2191", "Z2190", "Z2197", "Z2196", "Z2195", "Z2194", "Z605", "Z604", "Z1789", "Z607", "Z1788", "Z606", "Z609", "Z608", "Z1794", "Z1793", "Z1792", "Z610", "Z1791", "Z1798", "Z612", "Z1797", "Z611", "Z1796", "Z614", "Z1795", "Z613", "Z1790", "Z1799", "Z601", "Z600", "Z603", "Z602", "Z627", "Z626", "Z629", "Z628", "Z630", "Z632", "Z631", "Z634", "Z633", "Z636", "Z635", "Z616", "Z615", "Z618", "Z617", "Z619", "Z621", "Z620", "Z623", "Z622", "Z625", "Z624", "Z649", "Z648", "Z2201", "Z652", "Z2200", "Z651", "Z654", "Z653", "Z2205", "Z656", "Z2204", "Z655", "Z2203", "Z658", "Z2202", "Z657", "Z650", "Z2209", "Z638", "Z2208", "Z637", "Z2207", "Z2206", "Z639", "Z2212", "Z641", "Z2211", "Z640", "Z2210", "Z643", "Z642", "Z2216", "Z645", "Z2215", "Z644", "Z2214", "Z647", "Z2213", "Z646", "Z2219", "Z2218", "Z2217", "Z2223", "Z674", "Z2222", "Z673", "Z2221", "Z676", "Z2220", "Z675", "Z2227", "Z678", "Z2226", "Z677", "Z2225", "Z2224", "Z679", "Z670", "Z672", "Z671", "Z659", "Z2229", "Z2228", "Z2234", "Z663", "Z2233", "Z662", "Z2232", "Z665", "Z2231", "Z664", "Z2238", "Z667", "Z2237", "Z666", "Z2236", "Z669", "Z2235", "Z668", "Z2230", "Z661", "Z660", "Z1709", "Z1708", "Z1703", "Z1702", "Z1701", "Z1700", "Z1707", "Z1706", "Z1705", "Z1704", "Z696", "Z695", "Z698", "Z697", "Z1710", "Z699", "Z690", "Z692", "Z691", "Z694", "Z693", "Z1719", "Z1714", "Z1713", "Z1712", "Z1711", "Z1718", "Z1717", "Z1716", "Z1715", "Z685", "Z684", "Z687", "Z686", "Z1721", "Z689", "Z1720", "Z688", "Z681", "Z680", "Z683", "Z682", "Z1725", "Z1724", "Z1723", "Z1722", "Z1729", "Z1728", "Z1727", "Z1726", "Z1732", "Z1731", "Z1730", "Z1736", "Z1735", "Z1734", "Z1733", "Z1739", "Z1738", "Z1737", "Z1743", "Z1742", "Z1741", "Z1740", "Z1747", "Z1746", "Z1745", "Z1744", "Z1749", "Z1748", "Z1750", "Z1754", "Z1753", "Z1752", "Z1751", "Z1758", "Z1757", "Z1756", "Z1755", "Z1759", "Z1761", "Z1760", "Z1765", "Z1764", "Z1763", "Z1762", "Z1769", "Z1768", "Z1767", "Z1766", "Z1772", "Z1771", "Z1770", "Z1776", "Z1775", "Z1774", "Z1773", "Z1779", "Z1778", "Z1777", "Z1783", "Z1782", "Z1781", "Z1780", "Z1787", "Z1786", "Z1785", "Z1784", "Z2089", "Z2088", "Z2083", "Z2082", "Z2081", "Z2080", "Z2087", "Z2086", "Z2085", "Z2084", "Z2090", "Z2099", "Z2094", "Z2093", "Z2092", "Z2091", "Z2098", "Z2097", "Z2096", "Z2095", "Z2909", "Z2908", "Z2907", "Z2902", "Z2901", "Z2900", "Z2906", "Z2905", "Z2904", "Z2903", "Z2003", "Z2002", "Z2001", "Z2000", "Z2007", "Z2006", "Z2005", "Z2004", "Z2009", "Z2008", "Z2014", "Z2013", "Z2012", "Z2011", "Z2018", "Z2017", "Z2016", "Z2015", "Z2010", "Z2019", "Z2025", "Z2024", "Z2023", "Z2022", "Z2029", "Z2028", "Z2027", "Z2026", "Z2021", "Z2020", "Z2036", "Z2035", "Z2034", "Z2033", "Z2039", "Z2038", "Z2037", "Z2032", "Z2031", "Z2030", "Z2047", "Z2046", "Z2045", "Z2044", "Z2049", "Z2048", "Z2043", "Z2042", "Z2041", "Z2040", "Z2058", "Z2057", "Z2056", "Z2055", "Z2059", "Z2050", "Z2054", "Z2053", "Z2052", "Z2051", "Z704", "Z703", "Z706", "Z705", "Z708", "Z707", "Z709", "Z2069", "Z2068", "Z2067", "Z2066", "Z711", "Z710", "Z713", "Z712", "Z2061", "Z2060", "Z2065", "Z2064", "Z2063", "Z2062", "Z2079", "Z2078", "Z2077", "Z700", "Z702", "Z701", "Z2072", "Z2071", "Z2070", "Z2076", "Z2075", "Z2074", "Z2073", "Z726", "Z1669", "Z725", "Z1668", "Z2999", "Z728", "Z1667", "Z2998", "Z727", "Z729", "Z1673", "Z1672", "Z1671", "Z731", "Z1670", "Z730", "Z1677", "Z733", "Z1676", "Z732", "Z1675", "Z735", "Z1674", "Z734", "Z715", "Z714", "Z1679", "Z717", "Z1678", "Z716", "Z719", "Z718", "Z1684", "Z1683", "Z1682", "Z720", "Z1681", "Z1688", "Z722", "Z1687", "Z721", "Z1686", "Z724", "Z1685", "Z723", "Z1680", "Z748", "Z747", "Z1689", "Z749", "Z1695", "Z751", "Z1694", "Z750", "Z1693", "Z753", "Z1692", "Z752", "Z1699", "Z755", "Z1698", "Z754", "Z1697", "Z757", "Z1696", "Z756", "Z1691", "Z1690", "Z737", "Z736", "Z739", "Z738", "Z740", "Z742", "Z741", "Z744", "Z743", "Z746", "Z745", "Z769", "Z773", "Z772", "Z775", "Z774", "Z777", "Z776", "Z779", "Z778", "Z771", "Z770", "Z759", "Z758", "Z762", "Z761", "Z764", "Z763", "Z766", "Z765", "Z768", "Z767", "Z760", "Z2102", "Z795", "Z2101", "Z794", "Z2100", "Z797", "Z796", "Z2106", "Z799", "Z2105", "Z798", "Z2104", "Z2103", "Z791", "Z790", "Z793", "Z792", "Z2109", "Z2108", "Z2107", "Z2113", "Z784", "Z2112", "Z783", "Z2111", "Z786", "Z2110", "Z785", "Z2117", "Z788", "Z2116", "Z787", "Z2115", "Z2114", "Z789", "Z780", "Z782", "Z781", "Z2919", "Z2918", "Z2913", "Z2912", "Z2911", "Z2910", "Z2917", "Z2916", "Z2915", "Z2914", "Z2920", "Z2929", "Z2924", "Z2923", "Z2922", "Z2921", "Z2928", "Z2927", "Z2926", "Z2925", "Z1600", "Z2931", "Z2930", "Z1609", "Z1604", "Z2935", "Z1603", "Z2934", "Z1602", "Z2933", "Z1601", "Z2932", "Z1608", "Z2939", "Z1607", "Z2938", "Z1606", "Z2937", "Z1605", "Z2936", "Z1611", "Z2942", "Z1610", "Z2941", "Z2940", "Z1615", "Z2946", "Z1614", "Z2945", "Z1613", "Z2944", "Z1612", "Z2943", "Z1619", "Z1618", "Z2949", "Z1617", "Z2948", "Z1616", "Z2947", "Z1622", "Z2953", "Z1621", "Z2952", "Z1620", "Z2951", "Z2950", "Z1626", "Z2957", "Z1625", "Z2956", "Z1624", "Z2955", "Z1623", "Z2954", "Z1629", "Z1628", "Z2959", "Z1627", "Z2958", "Z2960", "Z1633", "Z2964", "Z1632", "Z2963", "Z1631", "Z2962", "Z1630", "Z2961", "Z1637", "Z2968", "Z1636", "Z2967", "Z1635", "Z2966", "Z1634", "Z2965", "Z1639", "Z1638", "Z2969", "Z1640", "Z2971", "Z2970", "Z1644", "Z2975", "Z1643", "Z2974", "Z1642", "Z2973", "Z1641", "Z2972", "Z1648", "Z2979", "Z1647", "Z2978", "Z1646", "Z2977", "Z1645", "Z2976", "Z1649", "Z1651", "Z2982", "Z1650", "Z2981", "Z2980", "Z1655", "Z2986", "Z1654", "Z2985", "Z1653", "Z2984", "Z1652", "Z2983", "Z1659", "Z1658", "Z2989", "Z1657", "Z2988", "Z1656", "Z2987", "Z1662", "Z2993", "Z1661", "Z2992", "Z1660", "Z2991", "Z2990", "Z1666", "Z2997", "Z1665", "Z2996", "Z1664", "Z2995", "Z1663", "Z2994"], "pageCount": 328, "comboBoxListDetails": { "market": ["Upstate New York", "New England"], "region": null, "feRegion": null }, "enodebId": ["AB1972", "AB1973", "AB1974", "AB1975", "AB1970", "AB1971", "AB1976", "AB1977", "AB1978", "AB1979", "AB1961", "AB1962", "AB1963", "AB1964", "AB1960", "AB1969", "AB1965", "AB1966", "AB1967", "AB1968", "AB1994", "AB1995", "AB1996", "AB1997", "AB1990", "AB1991", "AB1992", "AB1993", "AB1998", "AB1999", "AB1983", "AB1984", "AB1985", "AB1986", "AB1980", "AB1981", "AB1982", "AB1987", "AB1988", "AB1989", "AB1930", "AB1931", "AB1936", "AB1937", "AB1938", "AB1939", "AB1932", "AB1933", "AB1934", "AB1935", "AB1920", "AB1925", "AB1926", "AB1927", "AB1928", "AB1921", "AB1922", "AB1923", "AB1924", "AB1929", "AB1950", "AB1951", "AB1952", "AB1953", "AB1958", "AB1959", "AB1954", "AB1955", "AB1956", "AB1957", "AB1940", "AB1941", "AB1942", "AB1947", "AB1948", "AB1949", "AB1943", "AB1944", "AB1945", "AB1946", "AB2423", "AB2424", "AB2425", "AB2426", "AB2420", "AB2421", "AB2422", "AB2427", "AB2428", "AB2429", "AB2412", "AB498", "AB2413", "AB499", "AB2414", "AB496", "AB2415", "AB497", "AB2410", "AB2411", "AB2416", "AB2417", "AB2418", "AB2419", "AB490", "AB491", "AB494", "AB495", "AB492", "AB493", "AB1114", "AB2445", "AB1115", "AB2446", "AB1116", "AB2447", "AB1117", "AB2448", "AB1110", "AB2441", "AB1111", "AB2442", "AB1112", "AB2443", "AB1113", "AB2444", "AB1118", "AB2449", "AB1119", "AB2450", "AB1120", "AB2451", "AB1103", "AB2434", "AB1104", "AB2435", "AB1105", "AB2436", "AB1106", "AB2437", "AB2430", "AB1100", "AB2431", "AB1101", "AB2432", "AB1102", "AB2433", "AB1107", "AB2438", "AB1108", "AB2439", "AB1109", "AB2440", "AB465", "AB466", "AB463", "AB464", "AB469", "AB467", "AB468", "AB461", "AB462", "AB460", "AB454", "AB455", "AB452", "AB453", "AB458", "AB459", "AB456", "AB457", "AB450", "AB451", "AB2401", "AB487", "AB2402", "AB488", "AB2403", "AB485", "AB2404", "AB486", "AB489", "AB2400", "AB2409", "AB2405", "AB2406", "AB2407", "AB2408", "AB480", "AB483", "AB484", "AB481", "AB482", "AB476", "AB477", "AB474", "AB475", "AB478", "AB479", "AB472", "AB473", "AB470", "AB471", "AB1059", "AB421", "AB422", "AB420", "AB1055", "AB2386", "AB425", "AB1056", "AB2387", "AB426", "AB1057", "AB2388", "AB423", "AB1058", "AB2389", "AB424", "AB429", "AB427", "AB428", "AB1062", "AB2393", "AB1063", "AB2394", "AB1064", "AB2395", "AB1065", "AB2396", "AB2390", "AB1060", "AB2391", "AB1061", "AB2392", "AB1048", "AB2379", "AB410", "AB1049", "AB411", "AB1044", "AB2375", "AB414", "AB1045", "AB2376", "AB415", "AB1046", "AB2377", "AB412", "AB1047", "AB2378", "AB413", "AB418", "AB419", "AB416", "AB417", "AB1051", "AB2382", "AB1052", "AB2383", "AB1053", "AB2384", "AB1054", "AB2385", "AB2380", "AB1050", "AB2381", "AB443", "AB444", "AB441", "AB442", "AB1077", "AB447", "AB1078", "AB448", "AB1079", "AB445", "AB446", "AB449", "AB1084", "AB1085", "AB1086", "AB1087", "AB1080", "AB1081", "AB440", "AB1082", "AB1083", "AB432", "AB433", "AB430", "AB431", "AB1066", "AB2397", "AB436", "AB1067", "AB2398", "AB437", "AB1068", "AB2399", "AB434", "AB1069", "AB435", "AB438", "AB439", "AB1073", "AB1074", "AB1075", "AB1076", "AB1070", "AB1071", "AB1072", "AB1015", "AB2346", "AB1016", "AB2347", "AB1017", "AB2348", "AB1018", "AB2349", "AB1011", "AB2342", "AB1012", "AB2343", "AB1013", "AB2344", "AB1014", "AB2345", "AB1019", "AB2350", "AB1020", "AB2351", "AB1021", "AB2352", "AB1004", "AB2335", "AB1005", "AB2336", "AB1006", "AB2337", "AB1007", "AB2338", "AB1000", "AB2331", "AB1001", "AB2332", "AB1002", "AB2333", "AB1003", "AB2334", "AB1008", "AB2339", "AB1009", "AB2340", "AB1010", "AB2341", "AB1037", "AB2368", "AB1038", "AB2369", "AB400", "AB1039", "AB1033", "AB2364", "AB403", "AB1034", "AB2365", "AB404", "AB1035", "AB2366", "AB401", "AB1036", "AB2367", "AB402", "AB407", "AB408", "AB405", "AB406", "AB409", "AB1040", "AB2371", "AB1041", "AB2372", "AB1042", "AB2373", "AB1043", "AB2374", "AB2370", "AB1026", "AB2357", "AB1027", "AB2358", "AB1028", "AB2359", "AB1029", "AB1022", "AB2353", "AB1023", "AB2354", "AB1024", "AB2355", "AB1025", "AB2356", "AB2360", "AB1030", "AB2361", "AB1031", "AB2362", "AB1032", "AB2363", "AB1914", "AB1915", "AB1916", "AB1917", "AB1910", "AB1911", "AB1912", "AB1913", "AB1918", "AB1919", "AB1903", "AB1904", "AB1905", "AB1906", "AB1900", "AB1901", "AB1902", "AB1907", "AB1908", "AB1909", "AB1099", "AB1088", "AB1089", "AB1090", "AB1095", "AB1096", "AB1097", "AB1098", "AB1091", "AB1092", "AB1093", "AB1094", "test", "AB1213", "AB2544", "AB1214", "AB2545", "AB1215", "AB2546", "AB1216", "AB2547", "AB2540", "AB1210", "AB2541", "AB1211", "AB2542", "AB1212", "AB2543", "AB1217", "AB2548", "AB1218", "AB2549", "AB1219", "AB2550", "AB1202", "AB2533", "AB1203", "AB2534", "AB1204", "AB2535", "AB1205", "AB2536", "AB2530", "AB1200", "AB2531", "AB1201", "AB2532", "AB1206", "AB2537", "AB1207", "AB2538", "AB1208", "AB2539", "AB1209", "AB1235", "AB2566", "AB1236", "AB2567", "AB1237", "AB2568", "AB1238", "AB2569", "AB1231", "AB2562", "AB1232", "AB2563", "AB1233", "AB2564", "AB1234", "AB2565", "AB1239", "AB2570", "AB1240", "AB2571", "AB1241", "AB2572", "AB1224", "AB2555", "AB1225", "AB2556", "AB1226", "AB2557", "AB1227", "AB2558", "AB1220", "AB2551", "AB1221", "AB2552", "AB1222", "AB2553", "AB1223", "AB2554", "AB1228", "AB2559", "AB1229", "AB2560", "AB1230", "AB2561", "AB2500", "AB586", "AB2501", "AB587", "AB2502", "AB584", "AB2503", "AB585", "AB588", "AB589", "AB2508", "AB2509", "AB2504", "AB2505", "AB2506", "AB2507", "AB582", "AB583", "AB580", "AB581", "AB575", "AB576", "AB573", "AB574", "AB579", "AB577", "AB578", "AB571", "AB572", "AB570", "AB2522", "AB2523", "AB2524", "AB2525", "AB2520", "AB2521", "AB2526", "AB2527", "AB2528", "AB2529", "AB2511", "AB597", "AB2512", "AB598", "AB2513", "AB595", "AB2514", "AB596", "AB599", "AB2510", "AB2519", "AB2515", "AB2516", "AB2517", "AB2518", "AB590", "AB593", "AB594", "AB591", "AB592", "AB542", "AB543", "AB540", "AB541", "AB1176", "AB546", "AB1177", "AB547", "AB1178", "AB544", "AB1179", "AB545", "AB548", "AB549", "AB1183", "AB1184", "AB1185", "AB1186", "AB1180", "AB1181", "AB1182", "AB1169", "AB531", "AB532", "AB530", "AB1165", "AB2496", "AB535", "AB1166", "AB2497", "AB536", "AB1167", "AB2498", "AB533", "AB1168", "AB2499", "AB534", "AB539", "AB537", "AB538", "AB1172", "AB1173", "AB1174", "AB1175", "AB1170", "AB1171", "AB564", "AB565", "AB562", "AB563", "AB1198", "AB568", "AB1199", "AB569", "AB566", "AB567", "AB560", "AB561", "AB553", "AB554", "AB551", "AB552", "AB1187", "AB557", "AB1188", "AB558", "AB1189", "AB555", "AB556", "AB559", "AB1194", "AB1195", "AB1196", "AB1197", "AB1190", "AB1191", "AB550", "AB1192", "AB1193", "AB1136", "AB2467", "AB1137", "AB2468", "AB1138", "AB2469", "AB1139", "AB1132", "AB2463", "AB502", "AB1133", "AB2464", "AB503", "AB1134", "AB2465", "AB500", "AB1135", "AB2466", "AB501", "AB506", "AB507", "AB504", "AB505", "AB508", "AB509", "AB2470", "AB1140", "AB2471", "AB1141", "AB2472", "AB1142", "AB2473", "AB1125", "AB2456", "AB1126", "AB2457", "AB1127", "AB2458", "AB1128", "AB2459", "AB1121", "AB2452", "AB1122", "AB2453", "AB1123", "AB2454", "AB1124", "AB2455", "AB1129", "AB2460", "AB1130", "AB2461", "AB1131", "AB2462", "AB1158", "AB2489", "AB520", "AB1159", "AB521", "AB1154", "AB2485", "AB524", "AB1155", "AB2486", "AB525", "AB1156", "AB2487", "AB522", "AB1157", "AB2488", "AB523", "AB528", "AB529", "AB526", "AB527", "AB1161", "AB2492", "AB1162", "AB2493", "AB1163", "AB2494", "AB1164", "AB2495", "AB2490", "AB1160", "AB2491", "AB1147", "AB2478", "AB1148", "AB2479", "AB510", "AB1149", "AB1143", "AB2474", "AB513", "AB1144", "AB2475", "AB514", "AB1145", "AB2476", "AB511", "AB1146", "AB2477", "AB512", "AB517", "AB518", "AB515", "AB516", "AB519", "AB1150", "AB2481", "AB1151", "AB2482", "AB1152", "AB2483", "AB1153", "AB2484", "AB2480", "AB2600", "AB2601", "AB2602", "AB2607", "AB2608", "AB2609", "AB2603", "AB2604", "AB2605", "AB2606", "AB1334", "AB2665", "AB1335", "AB2666", "AB1336", "AB2667", "AB1337", "AB2668", "AB1330", "AB2661", "AB1331", "AB2662", "AB1332", "AB2663", "AB1333", "AB2664", "AB1338", "AB2669", "AB1339", "AB2670", "AB1340", "AB2671", "AB1323", "AB2654", "AB1324", "AB2655", "AB1325", "AB2656", "AB1326", "AB2657", "AB2650", "AB1320", "AB2651", "AB1321", "AB2652", "AB1322", "AB2653", "AB1327", "AB2658", "AB1328", "AB2659", "AB1329", "AB2660", "AB1356", "AB2687", "AB1357", "AB2688", "AB1358", "AB2689", "AB1359", "AB1352", "AB2683", "AB1353", "AB2684", "AB1354", "AB2685", "AB1355", "AB2686", "AB2690", "AB1360", "AB2691", "AB1361", "AB2692", "AB1362", "AB2693", "AB1345", "AB2676", "AB1346", "AB2677", "AB1347", "AB2678", "AB1348", "AB2679", "AB1341", "AB2672", "AB1342", "AB2673", "AB1343", "AB2674", "AB1344", "AB2675", "AB1349", "AB2680", "AB1350", "AB2681", "AB1351", "AB2682", "AB2621", "AB2622", "AB2623", "AB2624", "AB2620", "AB2629", "AB2625", "AB2626", "AB2627", "AB2628", "AB2610", "AB696", "AB2611", "AB697", "AB2612", "AB694", "AB2613", "AB695", "AB698", "AB699", "AB2618", "AB2619", "AB2614", "AB2615", "AB2616", "AB2617", "AB692", "AB693", "AB690", "AB691", "AB1312", "AB2643", "AB1313", "AB2644", "AB1314", "AB2645", "AB1315", "AB2646", "AB2640", "AB1310", "AB2641", "AB1311", "AB2642", "AB1316", "AB2647", "AB1317", "AB2648", "AB1318", "AB2649", "AB1319", "AB1301", "AB2632", "AB1302", "AB2633", "No", "AB1303", "AB2634", "AB1304", "AB2635", "AB2630", "AB1300", "AB2631", "AB1309", "AB1305", "AB2636", "AB1306", "AB2637", "AB1307", "AB2638", "AB1308", "AB2639", "AB663", "AB20", "AB664", "AB661", "AB662", "AB1297", "AB667", "AB1298", "AB668", "AB1299", "AB665", "AB666", "AB669", "AB18", "AB19", "AB16", "AB17", "AB14", "AB3000", "AB15", "AB3001", "AB12", "AB13", "AB660", "AB10", "AB11", "AB30", "AB652", "AB31", "AB653", "AB650", "AB651", "AB1286", "AB656", "AB1287", "AB657", "AB1288", "AB654", "AB1289", "AB655", "AB658", "AB659", "AB29", "AB1293", "AB27", "AB1294", "AB28", "AB1295", "AB25", "AB1296", "AB26", "AB23", "AB1290", "AB24", "AB1291", "AB21", "AB1292", "AB22", "AB3017", "AB41", "AB685", "AB3018", "AB42", "AB686", "AB3019", "AB683", "AB40", "AB684", "AB3013", "AB689", "AB3014", "AB3015", "AB687", "AB3016", "AB688", "AB3020", "AB38", "AB3021", "AB39", "AB3022", "AB36", "AB3023", "AB37", "AB34", "AB681", "AB35", "AB682", "AB32", "AB33", "AB680", "AB3006", "AB52", "AB674", "AB3007", "AB53", "AB675", "AB3008", "AB50", "AB672", "AB3009", "AB51", "AB673", "AB3002", "AB678", "AB3003", "AB679", "AB3004", "AB676", "AB3005", "AB677", "AB49", "AB3010", "AB3011", "AB47", "AB3012", "AB48", "AB45", "AB670", "AB46", "AB671", "AB43", "AB44", "AB1257", "AB2588", "AB63", "AB1258", "AB2589", "AB620", "AB64", "AB1259", "AB61", "AB62", "AB1253", "AB2584", "AB623", "AB1254", "AB2585", "AB60", "AB624", "AB1255", "AB2586", "AB621", "AB1256", "AB2587", "AB622", "AB627", "AB628", "AB625", "AB626", "AB629", "AB1260", "AB2591", "AB1261", "AB2592", "AB1262", "AB2593", "AB58", "AB1263", "AB2594", "AB59", "AB56", "AB57", "AB54", "AB2590", "AB55", "AB1246", "AB2577", "AB74", "AB1247", "AB2578", "AB75", "AB1248", "AB2579", "AB72", "AB1249", "AB73", "AB1242", "AB2573", "AB612", "AB70", "AB1243", "AB2574", "AB613", "AB71", "AB1244", "AB2575", "AB610", "AB1245", "AB2576", "AB611", "AB616", "AB617", "AB614", "AB615", "AB618", "AB619", "AB2580", "AB1250", "AB2581", "AB1251", "AB2582", "AB69", "AB1252", "AB2583", "AB67", "AB68", "AB65", "AB66", "AB1279", "AB641", "AB85", "AB642", "AB86", "AB83", "AB640", "AB84", "AB1275", "AB645", "AB81", "AB1276", "AB646", "AB82", "AB1277", "AB643", "AB1278", "AB644", "AB80", "AB649", "AB647", "AB648", "AB1282", "AB1283", "AB1284", "AB1285", "AB78", "AB79", "AB1280", "AB76", "AB1281", "AB77", "AB1268", "AB2599", "AB630", "AB96", "AB1269", "AB631", "AB97", "AB94", "AB95", "AB1264", "AB2595", "AB634", "AB92", "AB1265", "AB2596", "AB635", "AB93", "AB1266", "AB2597", "AB632", "AB90", "AB1267", "AB2598", "AB633", "AB91", "AB638", "AB639", "AB636", "AB637", "AB1271", "AB1272", "AB1273", "AB1274", "AB89", "AB87", "AB1270", "AB88", "AB3079", "AB3080", "AB3081", "AB3086", "AB3087", "AB3088", "AB3089", "AB3082", "AB3083", "AB3084", "AB98", "AB3085", "AB99", "AB3068", "AB3069", "AB3070", "AB3075", "AB3076", "AB3077", "AB3078", "AB3071", "AB3072", "AB3073", "AB3074", "AB601", "AB602", "AB600", "AB605", "AB606", "AB603", "AB604", "AB609", "AB607", "AB608", "AB3090", "AB3091", "AB3092", "AB3097", "AB3098", "AB3099", "AB3093", "AB3094", "AB3095", "AB3096", "AB3039", "AB3035", "AB3036", "AB3037", "AB3038", "AB3042", "AB3043", "AB3044", "AB3045", "AB3040", "AB3041", "AB3028", "AB3029", "AB3024", "AB3025", "AB3026", "AB3027", "AB3031", "AB3032", "AB3033", "AB3034", "AB3030", "AB3057", "AB3058", "AB3059", "AB3064", "AB3065", "AB3066", "AB3067", "AB3060", "AB3061", "AB3062", "AB3063", "AB3046", "AB3047", "AB3048", "AB3049", "AB3053", "AB3054", "AB3055", "AB3056", "AB3050", "AB3051", "AB3052", "AB2700", "AB2701", "AB2706", "AB2707", "AB2708", "AB2709", "AB2702", "AB2703", "AB2704", "AB2705", "AB2720", "AB2721", "AB2722", "AB2723", "AB2728", "AB2729", "AB2724", "AB2725", "AB2726", "AB2727", "AB2710", "AB2711", "AB2712", "AB2717", "AB2718", "AB2719", "AB2713", "AB2714", "AB2715", "AB2716", "AB1455", "AB2786", "AB1456", "AB2787", "AB1457", "AB2788", "AB1458", "AB2789", "AB1451", "AB2782", "AB1452", "AB2783", "AB1453", "AB2784", "AB1454", "AB2785", "AB1459", "AB2790", "AB1460", "AB2791", "AB1461", "AB2792", "AB1444", "AB2775", "AB1445", "AB2776", "AB1446", "AB2777", "AB1447", "AB2778", "AB1440", "AB2771", "AB1441", "AB2772", "AB1442", "AB2773", "AB1443", "AB2774", "AB1448", "AB2779", "AB1449", "AB2780", "AB1450", "AB2781", "AB1477", "AB1478", "AB1479", "AB1473", "AB1474", "AB1475", "AB1476", "AB1480", "AB1481", "AB1482", "AB1483", "AB1466", "AB2797", "AB1467", "AB2798", "AB1468", "AB2799", "AB1469", "AB1462", "AB2793", "AB1463", "AB2794", "AB1464", "AB2795", "AB1465", "AB2796", "AB1470", "AB1471", "AB1472", "AB1411", "AB2742", "AB1412", "AB2743", "AB1413", "AB2744", "AB1414", "AB2745", "AB2740", "AB1410", "AB2741", "AB1419", "AB1415", "AB2746", "AB1416", "AB2747", "AB1417", "AB2748", "AB1418", "AB2749", "AB1400", "AB2731", "AB1401", "AB2732", "AB1402", "AB2733", "AB1403", "AB2734", "AB2730", "AB1408", "AB2739", "AB1409", "AB1404", "AB2735", "AB1405", "AB2736", "AB1406", "AB2737", "AB1407", "AB2738", "AB1433", "AB2764", "AB1434", "AB2765", "AB1435", "AB2766", "AB1436", "AB2767", "AB2760", "AB1430", "AB2761", "AB1431", "AB2762", "AB1432", "AB2763", "AB1437", "AB2768", "AB1438", "AB2769", "AB1439", "AB2770", "AB1422", "AB2753", "AB1423", "AB2754", "AB1424", "AB2755", "AB1425", "AB2756", "AB2750", "AB1420", "AB2751", "AB1421", "AB2752", "AB1426", "AB2757", "AB1427", "AB2758", "AB1428", "AB2759", "AB1429", "AB3116", "AB784", "AB3117", "AB785", "AB3118", "AB782", "AB3119", "AB783", "AB3112", "AB788", "AB3113", "AB789", "AB3114", "AB786", "AB3115", "AB787", "AB3120", "AB3121", "AB3122", "AB780", "AB781", "AB3105", "AB773", "AB3106", "AB774", "AB3107", "AB771", "AB3108", "AB772", "AB3101", "AB777", "AB3102", "AB778", "AB3103", "AB775", "AB3104", "AB776", "AB779", "AB3109", "AB3110", "AB3111", "AB770", "AB3138", "AB3139", "AB3134", "AB3135", "AB3136", "AB3137", "AB3141", "AB3142", "AB3143", "AB3144", "AB3140", "AB3127", "AB795", "AB3128", "AB796", "AB3129", "AB793", "AB794", "AB3123", "AB799", "AB3124", "AB3125", "AB797", "AB3126", "AB798", "AB3130", "AB3131", "AB3132", "AB3133", "AB791", "AB792", "AB790", "AB1378", "AB740", "AB1379", "AB741", "AB1374", "AB744", "AB1375", "AB745", "AB1376", "AB742", "AB1377", "AB743", "AB748", "AB749", "AB746", "AB747", "AB1381", "AB1382", "AB1383", "AB1384", "AB1380", "AB1367", "AB2698", "AB1368", "AB2699", "AB730", "AB1369", "AB1363", "AB2694", "AB733", "AB1364", "AB2695", "AB734", "AB1365", "AB2696", "AB731", "AB1366", "AB2697", "AB732", "AB737", "AB738", "AB735", "AB736", "AB739", "AB1370", "AB1371", "AB1372", "AB1373", "AB762", "AB763", "AB760", "AB761", "AB1396", "AB766", "AB1397", "AB767", "AB1398", "AB764", "AB1399", "AB765", "AB768", "AB769", "AB3100", "AB1389", "AB751", "AB752", "AB750", "AB1385", "AB755", "AB1386", "AB756", "AB1387", "AB753", "AB1388", "AB754", "AB759", "AB757", "AB758", "AB1392", "AB1393", "AB1394", "AB1395", "AB1390", "AB1391", "AB700", "AB701", "AB704", "AB705", "AB702", "AB703", "AB708", "AB709", "AB706", "AB707", "AB3189", "AB3190", "AB3191", "AB3196", "AB3197", "AB3198", "AB3199", "AB3192", "AB3193", "AB3194", "AB3195", "AB722", "AB723", "AB720", "AB721", "AB726", "AB727", "AB724", "AB725", "AB728", "AB729", "AB711", "AB712", "AB710", "AB715", "AB716", "AB713", "AB714", "AB719", "AB717", "AB718", "AB3156", "AB3157", "AB3158", "AB3159", "AB3163", "AB3164", "AB3165", "AB3166", "AB3160", "AB3161", "AB3162", "AB3149", "AB3145", "AB3146", "AB3147", "AB3148", "AB3152", "AB3153", "AB3154", "AB3155", "AB3150", "AB3151", "AB3178", "AB3179", "AB3180", "AB3185", "AB3186", "AB3187", "AB3188", "AB3181", "AB3182", "AB3183", "AB3184", "AB3167", "AB3168", "AB3169", "AB3174", "AB3175", "AB3176", "AB3177", "AB3170", "AB3171", "AB3172", "AB3173", "AB2820", "AB2821", "AB2822", "AB2827", "AB2828", "AB2829", "AB2823", "AB2824", "AB2825", "AB2826", "AB2810", "AB2811", "AB2816", "AB2817", "AB2818", "AB2819", "AB2812", "AB2813", "AB2814", "AB2815", "AB1510", "AB2841", "AB1511", "AB2842", "AB1512", "AB2843", "AB1513", "AB2844", "AB2840", "AB1518", "AB2849", "AB1519", "AB1514", "AB2845", "AB1515", "AB2846", "AB1516", "AB2847", "AB1517", "AB2848", "AB2830", "AB1500", "AB2831", "AB1501", "AB2832", "AB1502", "AB2833", "AB1507", "AB2838", "AB1508", "AB2839", "AB1509", "AB1503", "AB2834", "AB1504", "AB2835", "AB1505", "AB2836", "AB1506", "AB2837", "AB2800", "AB2805", "AB2806", "AB2807", "AB2808", "AB2801", "AB2802", "AB2803", "AB2804", "AB2809", "AB1576", "AB1577", "AB1578", "AB1579", "AB1572", "AB1573", "AB1574", "AB1575", "AB1580", "AB1581", "AB1582", "AB1565", "AB2896", "AB1566", "AB2897", "AB1567", "AB2898", "AB1568", "AB2899", "AB1561", "AB2892", "AB1562", "AB2893", "AB1563", "AB2894", "AB1564", "AB2895", "AB1569", "AB1570", "AB1571", "AB1598", "AB1599", "AB1594", "AB1595", "AB1596", "AB1597", "AB1587", "AB1588", "AB1589", "AB1583", "AB1584", "AB1585", "AB1586", "AB1590", "AB1591", "AB1592", "AB1593", "AB1532", "AB2863", "AB1533", "AB2864", "AB1534", "AB2865", "AB1535", "AB2866", "AB2860", "AB1530", "AB2861", "AB1531", "AB2862", "AB1536", "AB2867", "AB1537", "AB2868", "AB1538", "AB2869", "AB1539", "AB1521", "AB2852", "AB1522", "AB2853", "AB1523", "AB2854", "AB1524", "AB2855", "AB2850", "AB1520", "AB2851", "AB1529", "AB1525", "AB2856", "AB1526", "AB2857", "AB1527", "AB2858", "AB1528", "AB2859", "AB1554", "AB2885", "AB1555", "AB2886", "AB1556", "AB2887", "AB1557", "AB2888", "AB1550", "AB2881", "AB1551", "AB2882", "AB1552", "AB2883", "AB1553", "AB2884", "AB1558", "AB2889", "AB1559", "AB2890", "AB1560", "AB2891", "AB1543", "AB2874", "AB1544", "AB2875", "AB1545", "AB2876", "AB1546", "AB2877", "AB2870", "AB1540", "AB2871", "AB1541", "AB2872", "AB1542", "AB2873", "AB1547", "AB2878", "AB1548", "AB2879", "AB1549", "AB2880", "AB3238", "AB3237", "AB3239", "AB3234", "AB3233", "AB3236", "AB3235", "AB3241", "AB3240", "AB3243", "AB3242", "AB3227", "AB894", "AB3226", "AB895", "AB3229", "AB892", "AB3228", "AB893", "AB3223", "AB898", "AB3222", "AB899", "AB3225", "AB896", "AB3224", "AB897", "AB3230", "AB3232", "AB3231", "AB890", "AB891", "AB3259", "AB3256", "AB3255", "AB3258", "AB3257", "AB3263", "AB3262", "AB3265", "AB3264", "AB3261", "AB3260", "AB3249", "AB3248", "AB3245", "AB3244", "AB3247", "AB3246", "AB3252", "AB3251", "AB3254", "AB3253", "AB3250", "AB1499", "AB861", "AB862", "AB860", "AB1495", "AB865", "AB1496", "AB866", "AB1497", "AB863", "AB1498", "AB864", "AB869", "AB867", "AB868", "AB1488", "AB850", "AB1489", "AB851", "AB1484", "AB854", "AB1485", "AB855", "AB1486", "AB852", "AB1487", "AB853", "AB858", "AB859", "AB856", "AB857", "AB1491", "AB1492", "AB1493", "AB1494", "AB1490", "AB3216", "AB883", "AB3215", "AB884", "AB3218", "AB881", "AB3217", "AB882", "AB3212", "AB887", "AB3211", "AB888", "AB3214", "AB885", "AB3213", "AB886", "AB889", "AB3219", "AB3221", "AB3220", "AB880", "AB3205", "AB872", "AB3204", "AB873", "AB3207", "AB870", "AB3206", "AB871", "AB3201", "AB876", "AB3200", "AB877", "AB3203", "AB874", "AB3202", "AB875", "AB878", "AB879", "AB3209", "AB3208", "AB3210", "AB821", "AB822", "AB820", "AB825", "AB826", "AB823", "AB824", "AB829", "AB827", "AB828", "AB810", "AB811", "AB814", "AB815", "AB812", "AB813", "AB818", "AB819", "AB816", "AB817", "AB809", "AB840", "AB843", "AB844", "AB841", "AB842", "AB847", "AB848", "AB845", "AB846", "AB849", "AB832", "AB833", "AB830", "AB831", "AB836", "AB837", "AB834", "AB835", "AB838", "AB839", "AB3278", "AB3277", "AB3267", "AB3266", "AB3269", "AB3268", "AB3274", "AB3273", "AB3276", "AB3275", "AB3270", "AB3272", "AB3271", "AB800", "AB803", "AB804", "AB801", "AB802", "AB807", "AB808", "AB805", "AB806", "AB2940", "AB1610", "AB2941", "AB1611", "AB2942", "AB1612", "AB2943", "AB1617", "AB2948", "AB1618", "AB2949", "AB1619", "AB1613", "AB2944", "AB1614", "AB2945", "AB1615", "AB2946", "AB1616", "AB2947", "AB2930", "AB1600", "AB2931", "AB1601", "AB2932", "AB1606", "AB2937", "AB1607", "AB2938", "AB1608", "AB2939", "AB1609", "AB1602", "AB2933", "AB1603", "AB2934", "AB1604", "AB2935", "AB1605", "AB2936", "AB1631", "AB2962", "AB1632", "AB2963", "AB1633", "AB2964", "AB1634", "AB2965", "AB2960", "AB1630", "AB2961", "AB1639", "AB1635", "AB2966", "AB1636", "AB2967", "AB1637", "AB2968", "AB1638", "AB2969", "AB1620", "AB2951", "AB1621", "AB2952", "AB1622", "AB2953", "AB1623", "AB2954", "AB2950", "AB1628", "AB2959", "AB1629", "AB1624", "AB2955", "AB1625", "AB2956", "AB1626", "AB2957", "AB1627", "AB2958", "AB188", "AB189", "AB2904", "AB2905", "AB2906", "AB2907", "AB2900", "AB2901", "AB2902", "AB2903", "AB2908", "AB2909", "AB182", "AB183", "AB180", "AB181", "AB186", "AB187", "AB184", "AB185", "AB179", "AB177", "AB178", "AB171", "AB172", "AB170", "AB175", "AB176", "AB173", "AB174", "AB2920", "AB2921", "AB2926", "AB2927", "AB2928", "AB2929", "AB2922", "AB2923", "AB2924", "AB2925", "AB199", "AB2910", "AB2915", "AB2916", "AB2917", "AB2918", "AB2911", "AB2912", "AB2913", "AB2914", "AB2919", "AB190", "AB193", "AB194", "AB191", "AB192", "AB197", "AB198", "AB195", "AB196", "AB146", "AB1697", "AB147", "AB1698", "AB144", "AB1699", "AB145", "AB1693", "AB1694", "AB148", "AB1695", "AB149", "AB1696", "AB142", "AB143", "AB140", "AB141", "AB135", "AB1686", "AB136", "AB1687", "AB133", "AB1688", "AB134", "AB1689", "AB139", "AB1682", "AB1683", "AB137", "AB1684", "AB138", "AB1685", "AB1690", "AB1691", "AB1692", "AB131", "AB132", "AB130", "AB168", "AB169", "AB166", "AB167", "AB160", "AB161", "AB164", "AB165", "AB162", "AB163", "AB157", "AB158", "AB155", "AB156", "AB159", "AB150", "AB153", "AB154", "AB151", "AB152", "AB102", "AB1653", "AB2984", "AB103", "AB1654", "AB2985", "AB100", "AB1655", "AB2986", "AB101", "AB1656", "AB2987", "AB106", "AB2980", "AB107", "AB1650", "AB2981", "AB104", "AB1651", "AB2982", "AB105", "AB1652", "AB2983", "AB108", "AB109", "AB1657", "AB2988", "AB1658", "AB2989", "AB1659", "AB2990", "AB1642", "AB2973", "AB1643", "AB2974", "AB1644", "AB2975", "AB1645", "AB2976", "AB2970", "AB1640", "AB2971", "AB1641", "AB2972", "AB1646", "AB2977", "AB1647", "AB2978", "AB1648", "AB2979", "AB1649", "AB124", "AB1675", "AB125", "AB1676", "AB122", "AB1677", "AB123", "AB1678", "AB128", "AB1671", "AB129", "AB1672", "AB126", "AB1673", "AB127", "AB1674", "AB1679", "AB1680", "AB1681", "AB120", "AB121", "AB113", "AB1664", "AB2995", "AB114", "AB1665", "AB2996", "AB111", "AB1666", "AB2997", "AB112", "AB1667", "AB2998", "AB117", "AB1660", "AB2991", "AB118", "AB1661", "AB2992", "AB115", "AB1662", "AB2993", "AB116", "AB1663", "AB2994", "AB119", "AB1668", "AB2999", "AB1669", "AB1670", "AB110", "AB2027", "AB2028", "AB2029", "AB2023", "AB2024", "AB2025", "AB2026", "AB2030", "AB2031", "AB2032", "AB2033", "AB2016", "AB2017", "AB2018", "AB2019", "AB2012", "AB2013", "AB2014", "AB2015", "AB2020", "AB2021", "AB2022", "AB2049", "AB2045", "AB2046", "AB2047", "AB2048", "AB2052", "AB2053", "AB2054", "AB2055", "AB2050", "AB2051", "AB2038", "AB2039", "AB2034", "AB2035", "AB2036", "AB2037", "AB2041", "AB2042", "AB2043", "AB2044", "AB2040", "AB982", "AB983", "AB980", "AB981", "AB986", "AB987", "AB984", "AB985", "AB988", "AB989", "AB971", "AB972", "AB970", "AB975", "AB976", "AB973", "AB974", "AB979", "AB977", "AB978", "AB2005", "AB2006", "AB2007", "AB2008", "AB2001", "AB2002", "AB2003", "AB2004", "AB2009", "AB2010", "AB2011", "AB993", "AB994", "AB991", "AB992", "AB997", "AB998", "AB995", "AB996", "AB999", "AB2000", "AB990", "AB942", "AB943", "AB940", "AB941", "AB946", "AB947", "AB944", "AB945", "AB948", "AB949", "AB931", "AB932", "AB930", "AB935", "AB936", "AB933", "AB934", "AB939", "AB937", "AB938", "AB960", "AB961", "AB964", "AB965", "AB962", "AB963", "AB968", "AB969", "AB966", "AB967", "AB950", "AB953", "AB954", "AB951", "AB952", "AB957", "AB958", "AB955", "AB956", "AB959", "AB2067", "AB2068", "AB2069", "AB902", "AB903", "AB900", "AB901", "AB906", "AB907", "AB904", "AB905", "AB2074", "AB2075", "AB2076", "AB2077", "AB2070", "AB2071", "AB2072", "AB2073", "AB2056", "AB2057", "AB2058", "AB2059", "AB2063", "AB2064", "AB2065", "AB2066", "AB2060", "AB2061", "AB2062", "AB2089", "AB920", "AB921", "AB924", "AB925", "AB922", "AB923", "AB928", "AB929", "AB926", "AB927", "AB2090", "AB919", "AB2091", "AB2096", "AB2097", "AB2098", "AB2099", "AB2092", "AB2093", "AB2094", "AB2095", "AB2078", "AB2079", "AB910", "AB913", "AB914", "AB911", "AB912", "AB917", "AB918", "AB915", "AB916", "AB908", "AB2080", "AB909", "AB2085", "AB2086", "AB2087", "AB2088", "AB2081", "AB2082", "AB2083", "AB2084", "AB1730", "AB1731", "AB1732", "AB1733", "AB1738", "AB1739", "AB1734", "AB1735", "AB1736", "AB1737", "AB1720", "AB1721", "AB1722", "AB1727", "AB1728", "AB1729", "AB1723", "AB1724", "AB1725", "AB1726", "AB1752", "AB1753", "AB1754", "AB1755", "AB1750", "AB1751", "AB1756", "AB1757", "AB1758", "AB1759", "AB1741", "AB1742", "AB1743", "AB1744", "AB1740", "AB1749", "AB1745", "AB1746", "AB1747", "AB1748", "AB298", "AB299", "AB292", "AB293", "AB290", "AB291", "AB296", "AB297", "AB294", "AB295", "AB1710", "AB1711", "AB1716", "AB1717", "AB1718", "AB1719", "AB1712", "AB1713", "AB1714", "AB1715", "AB1700", "AB1705", "AB1706", "AB1707", "AB1708", "AB1701", "AB1702", "AB1703", "AB1704", "AB1709", "AB267", "AB268", "AB266", "AB269", "AB2203", "AB289", "AB2204", "AB2205", "AB287", "AB2206", "AB288", "AB2200", "AB2201", "AB2202", "AB2207", "AB2208", "AB2209", "AB281", "AB282", "AB280", "AB285", "AB286", "AB283", "AB284", "AB278", "AB279", "AB276", "AB277", "AB270", "AB271", "AB274", "AB275", "AB272", "AB273", "AB1774", "AB223", "AB1775", "AB224", "AB1776", "AB221", "AB1777", "AB222", "AB1770", "AB227", "AB1771", "AB228", "AB1772", "AB225", "AB1773", "AB226", "AB229", "AB1778", "AB1779", "AB1780", "AB220", "AB1763", "AB212", "AB1764", "AB213", "AB1765", "AB210", "AB1766", "AB211", "AB216", "AB1760", "AB217", "AB1761", "AB214", "AB1762", "AB215", "AB218", "AB219", "AB1767", "AB1768", "AB1769", "AB1796", "AB1797", "AB1798", "AB1799", "AB1792", "AB1793", "AB1794", "AB1795", "AB1785", "AB1786", "AB1787", "AB1788", "AB1781", "AB1782", "AB1783", "AB1784", "AB1789", "AB1790", "AB1791", "AB230", "AB231", "AB2148", "AB2149", "AB2144", "AB2145", "AB2146", "AB2147", "AB2151", "AB2152", "AB2153", "AB2154", "AB2150", "AB2137", "AB2138", "AB2139", "AB2133", "AB2134", "AB2135", "AB2136", "AB2140", "AB2141", "AB2142", "AB2143", "AB201", "AB202", "AB200", "AB205", "AB2166", "AB206", "AB2167", "AB203", "AB2168", "AB204", "AB2169", "AB209", "AB207", "AB208", "AB2173", "AB2174", "AB2175", "AB2176", "AB2170", "AB2171", "AB2172", "AB2159", "AB2155", "AB2156", "AB2157", "AB2158", "AB2162", "AB2163", "AB2164", "AB2165", "AB2160", "AB2161", "AB2104", "AB2105", "AB2106", "AB2107", "AB2100", "AB2101", "AB2102", "AB2103", "AB2108", "AB2109", "AB2110", "AB2126", "AB2127", "AB2128", "AB2129", "AB2122", "AB2123", "AB2124", "AB2125", "AB2130", "AB2131", "AB2132", "AB2115", "AB2116", "AB2117", "AB2118", "AB2111", "AB2112", "AB2113", "AB2114", "AB2119", "AB2120", "AB2121", "AB2188", "AB2189", "AB2190", "AB2195", "AB2196", "AB2197", "AB2198", "AB2191", "AB2192", "AB2193", "AB2194", "AB2177", "AB2178", "AB2179", "AB2184", "AB2185", "AB2186", "AB2187", "AB2180", "AB2181", "AB2182", "AB2183", "AB2199", "AB1851", "AB1852", "AB1853", "AB1854", "AB1850", "AB1859", "AB1855", "AB1856", "AB1857", "AB1858", "AB1840", "AB1841", "AB1842", "AB1843", "AB1848", "AB1849", "AB1844", "AB1845", "AB1846", "AB1847", "AB3", "AB4", "AB5", "AB6", "AB7", "AB8", "AB9", "AB1873", "AB1874", "AB1875", "AB1876", "AB1870", "AB1871", "AB1872", "AB1877", "AB1878", "AB1879", "AB1862", "AB1863", "AB1864", "AB1865", "AB1860", "AB1861", "AB1866", "AB1867", "AB1868", "AB1869", "AB1810", "AB1815", "AB1816", "AB1817", "AB1818", "AB1811", "AB1812", "AB1813", "AB1814", "AB1819", "AB1804", "AB1805", "AB1806", "AB1807", "AB1800", "AB1801", "AB1802", "AB1803", "AB1808", "AB1809", "AB1830", "AB1831", "AB1832", "AB1837", "AB1838", "AB1839", "AB1833", "AB1834", "AB1835", "AB1836", "AB1820", "AB1821", "AB1826", "AB1827", "AB1828", "AB1829", "AB1822", "AB1823", "AB1824", "AB1825", "AB2302", "AB388", "AB2303", "AB389", "AB2304", "AB386", "AB2305", "AB387", "AB2300", "AB2301", "AB2306", "AB2307", "AB2308", "AB2309", "AB380", "AB381", "AB384", "AB385", "AB382", "AB383", "AB377", "AB378", "AB375", "AB376", "AB379", "AB370", "AB373", "AB374", "AB371", "AB372", "AB2324", "AB2325", "AB2326", "AB2327", "AB2320", "AB2321", "AB2322", "AB2323", "AB2328", "AB2329", "AB2330", "AB2313", "AB399", "AB2314", "AB2315", "AB397", "AB2316", "AB398", "AB2310", "AB2311", "AB2312", "AB2317", "AB2318", "AB2319", "AB391", "AB392", "AB390", "AB395", "AB396", "AB393", "AB394", "AB1895", "AB344", "AB1896", "AB345", "AB1897", "AB342", "AB1898", "AB343", "AB1891", "AB348", "AB1892", "AB349", "AB1893", "AB346", "AB1894", "AB347", "AB1899", "AB340", "AB341", "AB1884", "AB333", "AB1885", "AB334", "AB1886", "AB331", "AB1887", "AB332", "AB1880", "AB337", "AB1881", "AB338", "AB1882", "AB335", "AB1883", "AB336", "AB339", "AB1888", "AB1889", "AB1890", "AB330", "AB366", "AB367", "AB364", "AB365", "AB368", "AB369", "AB362", "AB363", "AB360", "AB361", "AB355", "AB356", "AB353", "AB354", "AB359", "AB357", "AB358", "AB351", "AB352", "AB350", "AB2269", "AB300", "AB301", "AB2265", "AB304", "AB2266", "AB305", "AB2267", "AB302", "AB2268", "AB303", "AB308", "AB309", "AB306", "AB307", "AB2272", "AB2273", "AB2274", "AB2275", "AB2270", "AB2271", "AB2258", "AB2259", "AB2254", "AB2255", "AB2256", "AB2257", "AB2261", "AB2262", "AB2263", "AB2264", "AB2260", "AB322", "AB323", "AB320", "AB321", "AB2287", "AB326", "AB2288", "AB327", "AB2289", "AB324", "AB325", "AB328", "AB329", "AB2294", "AB2295", "AB2296", "AB2297", "AB2290", "AB2291", "AB2292", "AB2293", "AB311", "AB312", "AB310", "AB2276", "AB315", "AB2277", "AB316", "AB2278", "AB313", "AB2279", "AB314", "AB319", "AB317", "AB318", "AB2283", "AB2284", "AB2285", "AB2286", "AB2280", "AB2281", "AB2282", "AB2225", "AB2226", "AB2227", "AB2228", "AB2221", "AB2222", "AB2223", "AB2224", "AB2229", "AB2230", "AB2231", "AB2214", "AB2215", "AB2216", "AB2217", "AB2210", "AB2211", "AB2212", "AB2213", "AB2218", "AB2219", "AB2220", "AB2247", "AB2248", "AB2249", "AB2243", "AB2244", "AB2245", "AB2246", "AB2250", "AB2251", "AB2252", "AB2253", "AB2236", "AB2237", "AB2238", "AB2239", "AB2232", "AB2233", "AB2234", "AB2235", "AB2240", "AB2241", "AB2242", "AB2298", "AB2299"], "schedulingVerizonModelList": [{ "id": 1, "forecastStartDate": null, "compDate": null, "market": "test sreeraj", "enbId": "test", "enbName": "test", "growRequest": null, "growCompleted": null, "ciqPresent": null, "envCompleted": null, "standardNonStandard": null, "carriers": null, "uda": null, "softwareLevels": null, "feArrivalTime": null, "ciStartTime": null, "dtHandoff": null, "ciEndTime": null, "startTime": null, "canRollComp": null, "traffic": null, "alarmPresent": null, "ciEngineer": null, "ft": null, "dt": null, "notes": null, "column1": null, "totalLookup": "1", "ranEngineer": null, "status": null, "revisit": null, "vlsm": null, "endTime": null, "comments": null, "issue": null, "ci": null, "nonCi": null, "ald": null, "week": null, "month": null, "status2": null, "quarter": null, "year": null, "rule1": null, "rule2": null, "day": null }, { "id": 2, "forecastStartDate": "2020-02-03", "compDate": "2020-02-03", "market": "New England", "enbId": "AB3", "enbName": "Z3", "growRequest": "", "growCompleted": "", "ciqPresent": "", "envCompleted": null, "standardNonStandard": null, "carriers": null, "uda": null, "softwareLevels": null, "feArrivalTime": null, "ciStartTime": null, "dtHandoff": null, "ciEndTime": null, "startTime": "", "canRollComp": null, "traffic": null, "alarmPresent": null, "ciEngineer": null, "ft": null, "dt": null, "notes": null, "column1": null, "totalLookup": "", "ranEngineer": "Rene", "status": "Rolled Back", "revisit": "", "vlsm": "", "endTime": "", "comments": "ALURRH FW not tested", "issue": "", "ci": "No", "nonCi": "No", "ald": "", "week": "Week 13", "month": "", "status2": "", "quarter": "", "year": "", "rule1": null, "rule2": null, "day": null }, { "id": 3, "forecastStartDate": "2020-02-03", "compDate": "2020-02-03", "market": "Upstate New York", "enbId": "AB4", "enbName": "Z4", "growRequest": "", "growCompleted": "", "ciqPresent": "", "envCompleted": null, "standardNonStandard": null, "carriers": null, "uda": null, "softwareLevels": null, "feArrivalTime": null, "ciStartTime": null, "dtHandoff": null, "ciEndTime": null, "startTime": "", "canRollComp": null, "traffic": null, "alarmPresent": null, "ciEngineer": null, "ft": null, "dt": null, "notes": null, "column1": null, "totalLookup": "", "ranEngineer": "Rene", "status": "Rolled Back", "revisit": "", "vlsm": "", "endTime": "", "comments": "ALURRH FW not tested", "issue": "", "ci": "No", "nonCi": "No", "ald": "", "week": "Week 13", "month": "", "status2": "", "quarter": "", "year": "", "rule1": null, "rule2": null, "day": null }, { "id": 4, "forecastStartDate": "2020-03-03", "compDate": "2020-03-03", "market": "New England", "enbId": "AB5", "enbName": "Z5", "growRequest": "", "growCompleted": "", "ciqPresent": "", "envCompleted": null, "standardNonStandard": null, "carriers": null, "uda": null, "softwareLevels": null, "feArrivalTime": null, "ciStartTime": null, "dtHandoff": null, "ciEndTime": null, "startTime": "", "canRollComp": null, "traffic": null, "alarmPresent": null, "ciEngineer": null, "ft": null, "dt": null, "notes": null, "column1": null, "totalLookup": "", "ranEngineer": "Rene", "status": "Migrated", "revisit": "", "vlsm": "", "endTime": "", "comments": "No Issue", "issue": "", "ci": "No", "nonCi": "No", "ald": "", "week": "Week 13", "month": "", "status2": "", "quarter": "", "year": "", "rule1": null, "rule2": null, "day": null }, { "id": 5, "forecastStartDate": "2018-04-04", "compDate": "2018-04-04", "market": "New England", "enbId": "AB6", "enbName": "Z6", "growRequest": "", "growCompleted": "", "ciqPresent": "", "envCompleted": null, "standardNonStandard": null, "carriers": null, "uda": null, "softwareLevels": null, "feArrivalTime": null, "ciStartTime": null, "dtHandoff": null, "ciEndTime": null, "startTime": "", "canRollComp": null, "traffic": null, "alarmPresent": null, "ciEngineer": null, "ft": null, "dt": null, "notes": null, "column1": null, "totalLookup": "", "ranEngineer": "Rene", "status": "Migrated", "revisit": "", "vlsm": "", "endTime": "", "comments": "No Issue", "issue": "", "ci": "No", "nonCi": "No", "ald": "", "week": "Week 14", "month": "", "status2": "", "quarter": "", "year": "", "rule1": null, "rule2": null, "day": null }, { "id": 6, "forecastStartDate": "2018-04-04", "compDate": "2018-04-04", "market": "Upstate New York", "enbId": "AB7", "enbName": "Z7", "growRequest": "", "growCompleted": "", "ciqPresent": "", "envCompleted": null, "standardNonStandard": null, "carriers": null, "uda": null, "softwareLevels": null, "feArrivalTime": null, "ciStartTime": null, "dtHandoff": null, "ciEndTime": null, "startTime": "", "canRollComp": null, "traffic": null, "alarmPresent": null, "ciEngineer": null, "ft": null, "dt": null, "notes": null, "column1": null, "totalLookup": "", "ranEngineer": "Rene", "status": "Migrated", "revisit": "", "vlsm": "", "endTime": "", "comments": "2nd AWS not trasmitting, Engineering to check issue", "issue": "", "ci": "No", "nonCi": "No", "ald": "", "week": "Week 14", "month": "", "status2": "", "quarter": "", "year": "", "rule1": null, "rule2": null, "day": null }, { "id": 7, "forecastStartDate": "2018-09-04", "compDate": "2018-09-04", "market": "New England", "enbId": "AB8", "enbName": "Z8", "growRequest": "", "growCompleted": "", "ciqPresent": "", "envCompleted": null, "standardNonStandard": null, "carriers": null, "uda": null, "softwareLevels": null, "feArrivalTime": null, "ciStartTime": null, "dtHandoff": null, "ciEndTime": null, "startTime": "", "canRollComp": null, "traffic": null, "alarmPresent": null, "ciEngineer": null, "ft": null, "dt": null, "notes": null, "column1": null, "totalLookup": "", "ranEngineer": "Rene", "status": "Migrated", "revisit": "", "vlsm": "", "endTime": "", "comments": "No Issue", "issue": "", "ci": "No", "nonCi": "No", "ald": "", "week": "Week 15", "month": "", "status2": "", "quarter": "", "year": "", "rule1": null, "rule2": null, "day": null }, { "id": 8, "forecastStartDate": "2018-09-04", "compDate": "2018-09-04", "market": "Upstate New York", "enbId": "AB9", "enbName": "Z9", "growRequest": "", "growCompleted": "", "ciqPresent": "", "envCompleted": null, "standardNonStandard": null, "carriers": null, "uda": null, "softwareLevels": null, "feArrivalTime": null, "ciStartTime": null, "dtHandoff": null, "ciEndTime": null, "startTime": "", "canRollComp": null, "traffic": null, "alarmPresent": null, "ciEngineer": null, "ft": null, "dt": null, "notes": null, "column1": null, "totalLookup": "", "ranEngineer": "Rene", "status": "Migrated", "revisit": "", "vlsm": "", "endTime": "", "comments": "No Issue", "issue": "", "ci": "No", "nonCi": "No", "ald": "", "week": "Week 15", "month": "", "status2": "", "quarter": "", "year": "", "rule1": null, "rule2": null, "day": null }, { "id": 9, "forecastStartDate": "2018-10-04", "compDate": "2018-10-04", "market": "New England", "enbId": "AB10", "enbName": "Z10", "growRequest": "", "growCompleted": "", "ciqPresent": "", "envCompleted": null, "standardNonStandard": null, "carriers": null, "uda": null, "softwareLevels": null, "feArrivalTime": null, "ciStartTime": null, "dtHandoff": null, "ciEndTime": null, "startTime": "", "canRollComp": null, "traffic": null, "alarmPresent": null, "ciEngineer": null, "ft": null, "dt": null, "notes": null, "column1": null, "totalLookup": "", "ranEngineer": "", "status": "Cancelled", "revisit": "", "vlsm": "", "endTime": "", "comments": "HW Issue", "issue": "", "ci": "No", "nonCi": "No", "ald": "", "week": "Week 15", "month": "", "status2": "", "quarter": "", "year": "", "rule1": null, "rule2": null, "day": null }, { "id": 10, "forecastStartDate": "2018-10-04", "compDate": "2018-10-04", "market": "Upstate New York", "enbId": "AB11", "enbName": "Z11", "growRequest": "", "growCompleted": "", "ciqPresent": "", "envCompleted": null, "standardNonStandard": null, "carriers": null, "uda": null, "softwareLevels": null, "feArrivalTime": null, "ciStartTime": null, "dtHandoff": null, "ciEndTime": null, "startTime": "", "canRollComp": null, "traffic": null, "alarmPresent": null, "ciEngineer": null, "ft": null, "dt": null, "notes": null, "column1": null, "totalLookup": "", "ranEngineer": "Hassan", "status": "Migrated", "revisit": "", "vlsm": "", "endTime": "", "comments": "No Issue", "issue": "", "ci": "No", "nonCi": "No", "ald": "", "week": "Week 15", "month": "", "status2": "", "quarter": "", "year": "", "rule1": null, "rule2": null, "day": null }], "sessionId": "c2634029", "serviceToken": "90039", "username": ["Arun", "admin", "Ratul "], "status": "SUCCESS" };
              //this.tableData = {"sessionId":"17d564d8","serviceToken":71384,"schedulingDetails":{"id":null,"region":"3/14/2019","market":"date","cascade":"New England","ciEngineerNight":"71071","bridgeOne":"QUEENSBURY","feRegion":"Y","feNight":"DAVID 3/14","ciEngineerDay":"Y","bridge":"Y","feDay":"Standard","notes":"aws","status":"","startDate":""},"customerName":"Verizon","customerId":2};
              this.totalPages = this.tableData.pageCount;
              this.marketList = this.tableData.market;
              this.enbIdList = this.tableData.enodebId;
              this.enbNameList = this.tableData.enodebName;
              this.createMarketList = this.tableData.comboBoxListDetails.market;

              this.userList = this.tableData.username;

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
              if (this.tableData.schedulingVerizonModelList.length == 0) {
                this.tableShowHide = false;
                this.noDataVisibility = true;
              } else {
                this.verizonData = this.tableData.schedulingVerizonModelList;
                this.tableShowHide = true;
                this.noDataVisibility = false;
                setTimeout(() => {
                  let tableWidth = document.getElementById('schedulingVerizonModelList').scrollWidth;

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
              this.showLoader = false;
              //this.tableData = {"pageCount":1,"schedulingVerizonModelList":[{"id":4,"forecastStartDate":"2019-03-20 19:35:19","compDate":"VLSM","market":"Upstate NY","enbId":"71071","enbName":"QUEENSBURY","growRequest":"Y","growCompleted":"Y","ciqPresent":"Y","envCompleted":"Y","standardNonStandard":"Standard","carriers":"AWS","uda":"","softwareLevels":"","feArrivalTime":"","ciStartTime":null,"dtHandoff":"","ciEndTime":null,"canRollComp":null,"traffic":"","alarmPresent":null,"ciEngineer":"B","ft":"Y Mohamed","dt":"Oluwaseun","notes":"Bridge #5"},{"id":5,"forecastStartDate":"2019-03-23 11:56:44","compDate":"vLSM","market":"New England","enbId":"71071","enbName":"QUEENSBURY","growRequest":"Y","growCompleted":"DAVID 3/14","ciqPresent":"Y","envCompleted":"Y","standardNonStandard":"Standard","carriers":"aws","uda":"","softwareLevels":"","feArrivalTime":"","ciStartTime":"","dtHandoff":"","ciEndTime":"","canRollComp":"","traffic":"","alarmPresent":"","ciEngineer":"B","ft":"Y Mohammed","dt":"oluwasen","notes":"Bridge #5"}],"sessionId":"fff0e445","serviceToken":"74388","status":"SUCCESS"};
              //this.tableData = {"sessionId":"fff0e445","serviceToken":"74388","pageCount":1,"schedulingVerizonModelList":[{"id":4,"forecastStartDate":"2019-03-20 19:35:19","compDate":"VLSM","market":"Upstate NY","enbId":"71071","enbName":"QUEENSBURY","growRequest":"Y","growCompleted":"Y","ciqPresent":"Y","envCompleted":"Y","standardNonStandard":"Standard","carriers":"AWS","uda":"qwert","softwareLevels":"asd","feArrivalTime":"sdfs","ciStartTime":"sdf","dtHandoff":"xcv","ciEndTime":"nulldf","canRollComp":"nudfgdll","traffic":"xcvd","alarmPresent":"nullcxv","ciEngineer":"B","ft":"Y Mohamed","dt":"Oluwaseun","notes":"Bridge #5"},{"id":5,"forecastStartDate":"2019-03-23 11:56:44","compDate":"vLSM","market":"New England","enbId":"71071","enbName":"QUEENSBURY","growRequest":"Y","growCompleted":"DAVID 3/14","ciqPresent":"Y","envCompleted":"Y","standardNonStandard":"Standard","carriers":"aws","uda":"sdffgh","softwareLevels":"sadfdg","feArrivalTime":"Zxxc","ciStartTime":"asdas","dtHandoff":"xcvdf","ciEndTime":"asdf","canRollComp":"asdw3","traffic":"xzvds","alarmPresent":"sdgfb","ciEngineer":"B","ft":"Y Mohammed","dt":"oluwasen","notes":"Bridge #5"}],"status":"SUCCESS"};
              //this.tableData = {"market":["New England"],"enodebName":["QUEENSBURY"],"pageCount":1,"enodebId":["71071"],"schedulingVerizonModelList":[{"id":6,"forecastStartDate":"2019-03-23 17:10:23","compDate":null,"market":"New England","enbId":"71071","enbName":"QUEENSBURY","growRequest":"Y","growCompleted":"DAVID 3/14","ciqPresent":"Y","envCompleted":"Y","standardNonStandard":"Standard","carriers":"aws","uda":"","softwareLevels":"","feArrivalTime":"7:40","ciStartTime":"10:20","dtHandoff":"","ciEndTime":"12:30","canRollComp":"","traffic":"","alarmPresent":"","ciEngineer":"B","ft":"Y Mohammed","dt":"oluwasen","notes":"Bridge #5","column1":null,"totalLookup":null,"ranEngineer":null,"status":null,"revisit":null,"vlsm":null,"endTime":null,"comments":null,"issue":null,"ci":null,"nonCi":null,"ald":null,"week":null,"month":null,"status2":null,"quarter":null,"year":null}],"sessionId":"4c9489d6","serviceToken":"73353","status":"SUCCESS"};

              // this.tableData = { "schedulingSprintEntity": [{ "id": 2, "region": "y", "market": "y", "ciEngineerNight": "y", "bridgeOne": "y", "feRegion": "y", "feNight": "y", "ciEngineerDay": "y", "bridge": "y", "feDay": "y", "notes": "y", "status": "y", "startDate": "2019-03-20T14:01:45.000+0000", "cascade": "y", "day": null, "week": null, "month": null, "qtr": null, "year": null, "type": null, "siteRevisit": null, "goldenCluster": null, "actualMigrationStartDate": null, "compDate": null, "enbId": null, "fiveG": null, "typeOne": null, "tvw": null, "currentSoftware": null, "scriptsRan": null, "dspImplemented": null, "ciEngineerOne": null, "ciStartTimeOne": null, "ciEndTimeOne": null, "feOne": null, "feContactInfoOne": null, "feArrivalTimeOne": null, "ciEngineerTwo": null, "ciStartTimeTwo": null, "ciEndTimeTwo": null, "feTwo": null, "feContactInfoTwo": null, "feArrivalTimeTwo": null, "gc": null, "gcArrivalTime": null, "putTool": null, "scriptErrors": null, "reasonCode": null, "ciIssue": null, "nonCiIssue": null, "engineerOneNotes": null, "engineerTwoNotes": null }], "pageCount": 1, "sessionId": "cb656863", "serviceToken": "67332", "status": "SUCCESS" };
              this.tableData = {"fenightDetailsList":{"market":null,"region":null,"feRegion":null,"feNight":["iam","cool"],"feDay":null},"fedayDetailsList":{"market":null,"region":null,"feRegion":null,"feNight":null,"feDay":["hello","world"]},"market":["","Milwaukee","West Washington","South Bay","Central Illinois","Ft. Wayne / South Bend","Oregon/SW Washington","Chicago","Cincinnati","Oregon / SW Washington","West Michigan","Colorado","FIT Chicago","Columbus","East Michigan","Indianapolis","Pittsburgh"],"schedulingSprintEntity":[{"id":1051,"region":"Central","market":"Chicago","cascade":"CH03XC022","ciEngineerNight":null,"bridgeOne":null,"feRegion":"FECentral","feNight":null,"ciEngineerDay":null,"bridge":null,"feDay":null,"notes":null,"status":null,"startDate":"2019-01-11","day":null,"week":null,"month":null,"qtr":null,"year":null,"type":null,"siteRevisit":null,"goldenCluster":null,"actualMigrationStartDate":null,"compDate":"2019-02-11","enbId":"516265","fiveG":null,"typeOne":null,"tvw":null,"currentSoftware":"","scriptsRan":null,"dspImplemented":"","ciEngineerOne":"Daron Sy","ciStartTimeOne":"0.0625","ciEndTimeOne":"0.3333333333","feOne":"Latoya","feContactInfoOne":"","feArrivalTimeOne":"2:20:00 AM","ciEngineerTwo":"Vasudha Vanga","ciStartTimeTwo":"8:00:00 AM","ciEndTimeTwo":null,"feTwo":"","feContactInfoTwo":"","feArrivalTimeTwo":"","gc":"CCSI","gcArrivalTime":"5:00:00 AM","putTool":"No","scriptErrors":null,"reasonCode":null,"ciIssue":null,"nonCiIssue":null,"engineerOneNotes":null,"engineerTwoNotes":null,"circuitbreakerStart":null,"circuitbreakerEnd":null,"alphaStartTime":null,"alphaEndTime":null,"betaStartTime":null,"betaEndTime":null,"gammaStartTime":null,"gammaEndTime":null},{"id":1052,"region":"Central","market":"Chicago","cascade":"CH03XC010","ciEngineerNight":null,"bridgeOne":null,"feRegion":"FECentral","feNight":null,"ciEngineerDay":null,"bridge":null,"feDay":null,"notes":null,"status":null,"startDate":"2019-03-11","day":null,"week":null,"month":null,"qtr":null,"year":null,"type":null,"siteRevisit":null,"goldenCluster":null,"actualMigrationStartDate":null,"compDate":"2019-03-11","enbId":"516260","fiveG":null,"typeOne":null,"tvw":null,"currentSoftware":"","scriptsRan":null,"dspImplemented":"","ciEngineerOne":"Thang Pham","ciStartTimeOne":"0.0416666667","ciEndTimeOne":"0.3125","feOne":"LaToya Norwood","feContactInfoOne":"","feArrivalTimeOne":"1:45:00 AM","ciEngineerTwo":"Vasudha Vanga","ciStartTimeTwo":"7:30:00 AM","ciEndTimeTwo":null,"feTwo":"","feContactInfoTwo":"","feArrivalTimeTwo":"","gc":"CCSI","gcArrivalTime":"5:45:00 AM","putTool":"No","scriptErrors":null,"reasonCode":null,"ciIssue":null,"nonCiIssue":null,"engineerOneNotes":null,"engineerTwoNotes":null,"circuitbreakerStart":null,"circuitbreakerEnd":null,"alphaStartTime":null,"alphaEndTime":null,"betaStartTime":null,"betaEndTime":null,"gammaStartTime":null,"gammaEndTime":null},{"id":1053,"region":"Central","market":"Chicago","cascade":"CH13XC048","ciEngineerNight":null,"bridgeOne":null,"feRegion":"FECentral","feNight":null,"ciEngineerDay":null,"bridge":null,"feDay":null,"notes":null,"status":null,"startDate":"2019-03-11","day":null,"week":null,"month":null,"qtr":null,"year":null,"type":null,"siteRevisit":null,"goldenCluster":null,"actualMigrationStartDate":null,"compDate":"2019-03-11","enbId":"516792","fiveG":null,"typeOne":null,"tvw":null,"currentSoftware":"","scriptsRan":null,"dspImplemented":"","ciEngineerOne":"Matthew Hermez","ciStartTimeOne":"0.0416666667","ciEndTimeOne":"0.3263888889","feOne":"Raul Garcia","feContactInfoOne":"","feArrivalTimeOne":"1:09:00 AM","ciEngineerTwo":"Mark Serverson","ciStartTimeTwo":"7:50:00 AM","ciEndTimeTwo":null,"feTwo":"","feContactInfoTwo":"","feArrivalTimeTwo":"","gc":"CCSI","gcArrivalTime":"5:22:00 AM","putTool":"No","scriptErrors":null,"reasonCode":null,"ciIssue":null,"nonCiIssue":null,"engineerOneNotes":null,"engineerTwoNotes":null,"circuitbreakerStart":null,"circuitbreakerEnd":null,"alphaStartTime":null,"alphaEndTime":null,"betaStartTime":null,"betaEndTime":null,"gammaStartTime":null,"gammaEndTime":null},{"id":1054,"region":"Central","market":"Chicago","cascade":"CH03XC028","ciEngineerNight":null,"bridgeOne":null,"feRegion":"FECentral","feNight":null,"ciEngineerDay":null,"bridge":null,"feDay":null,"notes":null,"status":null,"startDate":"2019-04-11","day":null,"week":null,"month":null,"qtr":null,"year":null,"type":null,"siteRevisit":null,"goldenCluster":null,"actualMigrationStartDate":null,"compDate":"2019-04-11","enbId":"516267","fiveG":null,"typeOne":null,"tvw":null,"currentSoftware":"","scriptsRan":null,"dspImplemented":"","ciEngineerOne":"Daron Sy","ciStartTimeOne":"0","ciEndTimeOne":"0.3333333333","feOne":"Kyle Olejinzcak","feContactInfoOne":"","feArrivalTimeOne":"1:15:00 AM","ciEngineerTwo":"Vasudha Vanga","ciStartTimeTwo":"8:00:00 AM","ciEndTimeTwo":null,"feTwo":"","feContactInfoTwo":"","feArrivalTimeTwo":"","gc":"CCSI","gcArrivalTime":"5:00:00 AM","putTool":"No","scriptErrors":null,"reasonCode":null,"ciIssue":null,"nonCiIssue":null,"engineerOneNotes":null,"engineerTwoNotes":null,"circuitbreakerStart":null,"circuitbreakerEnd":null,"alphaStartTime":null,"alphaEndTime":null,"betaStartTime":null,"betaEndTime":null,"gammaStartTime":null,"gammaEndTime":null},{"id":1055,"region":"Central","market":"Chicago","cascade":"CH03XC244","ciEngineerNight":null,"bridgeOne":null,"feRegion":"FECentral","feNight":null,"ciEngineerDay":null,"bridge":null,"feDay":null,"notes":null,"status":null,"startDate":"2019-08-11","day":null,"week":null,"month":null,"qtr":null,"year":null,"type":null,"siteRevisit":null,"goldenCluster":null,"actualMigrationStartDate":null,"compDate":"2019-08-11","enbId":"516383","fiveG":null,"typeOne":null,"tvw":null,"currentSoftware":"","scriptsRan":null,"dspImplemented":"","ciEngineerOne":"Daron Sy","ciStartTimeOne":"0","ciEndTimeOne":"0.3333333333","feOne":"Mayowa Adeoye","feContactInfoOne":"","feArrivalTimeOne":"1:00:00 AM","ciEngineerTwo":"Matthew Hermez","ciStartTimeTwo":"8:00:00 AM","ciEndTimeTwo":null,"feTwo":"","feContactInfoTwo":"","feArrivalTimeTwo":"","gc":"CCSI","gcArrivalTime":"5:00:00 AM","putTool":"No","scriptErrors":null,"reasonCode":null,"ciIssue":null,"nonCiIssue":null,"engineerOneNotes":null,"engineerTwoNotes":null,"circuitbreakerStart":null,"circuitbreakerEnd":null,"alphaStartTime":null,"alphaEndTime":null,"betaStartTime":null,"betaEndTime":null,"gammaStartTime":null,"gammaEndTime":null},{"id":1056,"region":"Central","market":"Chicago","cascade":"CH54XC961","ciEngineerNight":null,"bridgeOne":null,"feRegion":"FECentral","feNight":null,"ciEngineerDay":null,"bridge":null,"feDay":null,"notes":null,"status":null,"startDate":"2019-08-11","day":null,"week":null,"month":null,"qtr":null,"year":null,"type":null,"siteRevisit":null,"goldenCluster":null,"actualMigrationStartDate":null,"compDate":"2019-08-11","enbId":"516937","fiveG":null,"typeOne":null,"tvw":null,"currentSoftware":"","scriptsRan":null,"dspImplemented":"","ciEngineerOne":"Asad Inayatullah","ciStartTimeOne":"0.0694444444","ciEndTimeOne":"0.3645833333","feOne":"Frank Douse","feContactInfoOne":"","feArrivalTimeOne":"1:35:00 AM","ciEngineerTwo":"Vasudha Vanga","ciStartTimeTwo":"8:45:00 AM","ciEndTimeTwo":null,"feTwo":"","feContactInfoTwo":"","feArrivalTimeTwo":"","gc":"CCSI","gcArrivalTime":"5:00:00 AM","putTool":"No","scriptErrors":null,"reasonCode":null,"ciIssue":null,"nonCiIssue":null,"engineerOneNotes":null,"engineerTwoNotes":null,"circuitbreakerStart":null,"circuitbreakerEnd":null,"alphaStartTime":null,"alphaEndTime":null,"betaStartTime":null,"betaEndTime":null,"gammaStartTime":null,"gammaEndTime":null},{"id":1057,"region":"Central","market":"Chicago","cascade":"CH03XC043","ciEngineerNight":null,"bridgeOne":null,"feRegion":"FECentral","feNight":null,"ciEngineerDay":null,"bridge":null,"feDay":null,"notes":null,"status":null,"startDate":"2020-02-11","day":null,"week":null,"month":null,"qtr":null,"year":null,"type":null,"siteRevisit":null,"goldenCluster":null,"actualMigrationStartDate":null,"compDate":"2020-03-11","enbId":"516271","fiveG":null,"typeOne":null,"tvw":null,"currentSoftware":"","scriptsRan":null,"dspImplemented":"","ciEngineerOne":"Asad Inayatullah","ciStartTimeOne":"0.0416666667","ciEndTimeOne":"0.3291666667","feOne":"Kyle Olejinzcak","feContactInfoOne":"","feArrivalTimeOne":"1:00:00 AM","ciEngineerTwo":"Matthew Hermez","ciStartTimeTwo":"7:55:00 AM","ciEndTimeTwo":null,"feTwo":"","feContactInfoTwo":"","feArrivalTimeTwo":"","gc":"CCSI","gcArrivalTime":"5:00:00 AM","putTool":"No","scriptErrors":null,"reasonCode":null,"ciIssue":null,"nonCiIssue":null,"engineerOneNotes":null,"engineerTwoNotes":null,"circuitbreakerStart":null,"circuitbreakerEnd":null,"alphaStartTime":null,"alphaEndTime":null,"betaStartTime":null,"betaEndTime":null,"gammaStartTime":null,"gammaEndTime":null},{"id":1058,"region":"Central","market":"Chicago","cascade":"CH03XC013","ciEngineerNight":null,"bridgeOne":null,"feRegion":"FECentral","feNight":null,"ciEngineerDay":null,"bridge":null,"feDay":null,"notes":null,"status":null,"startDate":"2020-03-11","day":null,"week":null,"month":null,"qtr":null,"year":null,"type":null,"siteRevisit":null,"goldenCluster":null,"actualMigrationStartDate":null,"compDate":null,"enbId":"516261","fiveG":null,"typeOne":null,"tvw":null,"currentSoftware":"","scriptsRan":null,"dspImplemented":"","ciEngineerOne":"Daron Sy","ciStartTimeOne":"0.9166666667","ciEndTimeOne":"0.1041666667","feOne":"Eugenio Pena","feContactInfoOne":"","feArrivalTimeOne":"1:00:00 AM","ciEngineerTwo":"Vasudha Vanga","ciStartTimeTwo":"","ciEndTimeTwo":null,"feTwo":"","feContactInfoTwo":"","feArrivalTimeTwo":"","gc":"CCSI","gcArrivalTime":"","putTool":"No","scriptErrors":null,"reasonCode":null,"ciIssue":null,"nonCiIssue":null,"engineerOneNotes":null,"engineerTwoNotes":null,"circuitbreakerStart":null,"circuitbreakerEnd":null,"alphaStartTime":null,"alphaEndTime":null,"betaStartTime":null,"betaEndTime":null,"gammaStartTime":null,"gammaEndTime":null},{"id":1059,"region":"Central","market":"Chicago","cascade":"CH13XC052","ciEngineerNight":null,"bridgeOne":null,"feRegion":"FECentral","feNight":null,"ciEngineerDay":null,"bridge":null,"feDay":null,"notes":null,"status":null,"startDate":"2020-04-11","day":null,"week":null,"month":null,"qtr":null,"year":null,"type":null,"siteRevisit":null,"goldenCluster":null,"actualMigrationStartDate":null,"compDate":"2020-05-11","enbId":"516796","fiveG":null,"typeOne":null,"tvw":null,"currentSoftware":"","scriptsRan":null,"dspImplemented":"","ciEngineerOne":"Zaid Abdulqader","ciStartTimeOne":"0.9166666667","ciEndTimeOne":"0.3222222222","feOne":"Alfaonso Hemandez","feContactInfoOne":"925-858-4322","feArrivalTimeOne":"12:35:00 PM","ciEngineerTwo":"Vasudha Vanga","ciStartTimeTwo":"7:45:00 AM","ciEndTimeTwo":null,"feTwo":"","feContactInfoTwo":"","feArrivalTimeTwo":"","gc":"CCSI","gcArrivalTime":"3:30:00 AM","putTool":"No","scriptErrors":null,"reasonCode":null,"ciIssue":null,"nonCiIssue":null,"engineerOneNotes":null,"engineerTwoNotes":null,"circuitbreakerStart":null,"circuitbreakerEnd":null,"alphaStartTime":null,"alphaEndTime":null,"betaStartTime":null,"betaEndTime":null,"gammaStartTime":null,"gammaEndTime":null},{"id":1060,"region":"Central","market":"Chicago","cascade":"CH54XC962","ciEngineerNight":null,"bridgeOne":null,"feRegion":"FECentral","feNight":null,"ciEngineerDay":null,"bridge":null,"feDay":null,"notes":null,"status":null,"startDate":"2020-04-11","day":null,"week":null,"month":null,"qtr":null,"year":null,"type":null,"siteRevisit":null,"goldenCluster":null,"actualMigrationStartDate":null,"compDate":"2020-04-11","enbId":"516938","fiveG":null,"typeOne":null,"tvw":null,"currentSoftware":"","scriptsRan":null,"dspImplemented":"","ciEngineerOne":"Daron Sy","ciStartTimeOne":"0.9166666667","ciEndTimeOne":"0.3333333333","feOne":"Mayowa Adeoye","feContactInfoOne":"","feArrivalTimeOne":"1:15:00 AM","ciEngineerTwo":"Mark Serverson","ciStartTimeTwo":"8:00:00 AM","ciEndTimeTwo":null,"feTwo":"","feContactInfoTwo":"","feArrivalTimeTwo":"","gc":"CCSI","gcArrivalTime":"5:00:00 AM","putTool":"No","scriptErrors":null,"reasonCode":null,"ciIssue":null,"nonCiIssue":null,"engineerOneNotes":null,"engineerTwoNotes":null,"circuitbreakerStart":null,"circuitbreakerEnd":null,"alphaStartTime":null,"alphaEndTime":null,"betaStartTime":null,"betaEndTime":null,"gammaStartTime":null,"gammaEndTime":null}],"pageCount":23,"marketDetailsList":{"market":["buffalo","Central Illinois","Central Iowa","Central Pennsylvania","Chicago","Cincinnati","Cleveland","Columbus","East Michigan","Ft. Wayne/South Bend","Indianpolis","Milwaukee","North Wisconsin","Pittsburgh","Toledo","West Iowa/Nebraska","West Michigan","Western Pennsylvania"],"region":null,"feRegion":null},"regionDetailsList":{"market":null,"region":["Central","West"],"feRegion":null},"sessionId":"c2634029","feregionDetailsList":{"market":null,"region":null,"feRegion":["FECentral","FEWest"]},"serviceToken":"66916","region":["FIT Central","West","Central"],"username":["supriya"],"status":"SUCCESS"};
              //this.totalPages = this.tableData.pageCount;
              this.marketList = this.tableData.market;
              this.regionList = this.tableData.region;
              this.feRegionList = this.tableData.feRegion;
              this.createMarketList = this.tableData.marketDetailsList.market;
              this.createRegionList = this.tableData.regionDetailsList.region;
              this.createFeregionList = this.tableData.feregionDetailsList.feRegion;
              this.createFeDayList=this.tableData.fenightDetailsList.feNight;
              this.createFeNightList=this.tableData.fedayDetailsList.feDay;


              //this.enbNameList=this.tableData.enodebName;
              //console.log(this.marketList);
              //this.custId=this.tableData.
              this.userList = this.tableData.username;

              let pageCount = [];
              for (var i = 1; i <= this.tableData.pageCount; i++) {
                pageCount.push(i);
              }
              this.pageRenge = pageCount;
              this.pager = this.getPager(this.totalPages * this.pageSize, this.currentPage, this.pageRenge);
              if (this.tableData.sessionId == "408" || this.tableData.status == "Invalid User") {
                this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
              }
              if (this.tableData.schedulingSprintEntity.length == 0) {
                this.tableShowHide = false;
                this.noDataVisibility = true;
              } else {
                this.sprintData = this.tableData.schedulingSprintEntity;
                this.tableShowHide = true;
                this.noDataVisibility = false;
                setTimeout(() => {
                  let tableWidth = document.getElementById('schedulingSprintEntity').scrollWidth;

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
                //"forecastStartDate": currentForm.querySelector("#searchForecastStartDate").value,
                //"forecastEndDate": currentForm.querySelector("#searchForecastEndDate").value,
                "compDate" : this.datePipe.transform(currentForm.querySelector("#searchCompStartDate").value, 'dd/MM/yyyy'),
                "compEndDate" : this.datePipe.transform(currentForm.querySelector("#searchCompEndDate").value, 'dd/MM/yyyy'),
                //"compDate": currentForm.querySelector("#searchCompStartDate").value,
                //"compEndDate": currentForm.querySelector("#searchCompEndDate").value,
                "market": currentForm.querySelector("#searchMarket").value,
                "enbId": currentForm.querySelector("#searchEnbId").value,
                "enbName": currentForm.querySelector("#searchEnbName").value,
                "carriers": currentForm.querySelector("#searchCarriers").value,
                "alarmPresent": currentForm.querySelector("#searchAlarmPresent").value,

              };

            if (searchCrtra.forecastStartDate || searchCrtra.forecastEndDate || searchCrtra.compDate || searchCrtra.compEndDate || searchCrtra.market || searchCrtra.enbId || searchCrtra.enbName || searchCrtra.carriers || searchCrtra.alarmPresent) {
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
                "startDate" : this.datePipe.transform(currentForm.querySelector("#searchStartDate").value, 'dd/MM/yyyy'),
                "endDate" : this.datePipe.transform(currentForm.querySelector("#searchEndDate").value, 'dd/MM/yyyy'),
                //"startDate": currentForm.querySelector("#searchStartDate").value,
                //"endDate": currentForm.querySelector("#searchEndDate").value,
                "region": currentForm.querySelector("#searchRegion").value,
                "market": currentForm.querySelector("#searchMarket").value,
                "cascade": currentForm.querySelector("#searchCascade").value,


              };

            if (searchCrtra.startDate || searchCrtra.endDate || searchCrtra.region || searchCrtra.market || searchCrtra.cascade) {
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
          this.getSchedulingDetails();
        }
      }
    }, 0);
  }



  addNewSchedule(event) {
    validator.performValidation(event, this.validationData, "save_update");
    setTimeout(() => {
      if (this.isValidForm(event)) {
        this.showLoader = true;

        let schedulingDetails;
        if (this.custId == 2) {
          let currentForm = event.target.parentNode.parentNode;
          schedulingDetails = {
            "id": null,
            "forecastStartDate" : this.datePipe.transform(currentForm.querySelector("#forecastStartDate").value, 'dd/MM/yyyy'),
            "compDate" : this.datePipe.transform(currentForm.querySelector("#compDate").value, 'dd/MM/yyyy'),
            //"forecastStartDate": currentForm.querySelector("#forecastStartDate").value,
            //"compDate": currentForm.querySelector("#compDate").value,
            "market": currentForm.querySelector("#market").value,
            "enbId": currentForm.querySelector("#enbId").value,
            "enbName": currentForm.querySelector("#enbName").value,
            "growRequest": currentForm.querySelector("#growRequest").value,
            "growCompleted": currentForm.querySelector("#growCompleted").value,
            "ciqPresent": currentForm.querySelector("#ciqPresent").value,
            "envCompleted": currentForm.querySelector("#envCompleted").value,
            "standardNonStandard": currentForm.querySelector("#standardNonStandard").value,
            "carriers": currentForm.querySelector("#carriers").value,
            "uda": currentForm.querySelector("#uda").value,
            "softwareLevels": currentForm.querySelector("#softwareLevels").value,
            "feArrivalTime": currentForm.querySelector("#feArrivalTime").value,
            "ciStartTime": currentForm.querySelector("#ciStartTime").value,
            "dtHandoff": currentForm.querySelector("#dtHandoff").value,
            "ciEndTime": currentForm.querySelector("#ciEndTime").value,
            "canRollComp": currentForm.querySelector("#canRollComp").value,
            "traffic": currentForm.querySelector("#traffic").value,
            "alarmPresent": currentForm.querySelector("#alarmPresent").value,
            "ciEngineer": currentForm.querySelector("#ciEngineer").value,
            "ft": currentForm.querySelector("#ft").value,
            "dt": currentForm.querySelector("#dt").value,
            "notes": currentForm.querySelector("#notes").value,
            "day":this.day,
            "month":this.month,
            "year":this.year,
            "week":this.week,
            "quarter":this.quarter

          }
        }

        else if (this.custId == 4) {
          let currentForm = event.target.parentNode.parentNode;
          schedulingDetails = {
            "id": null,
            "startDate" : this.datePipe.transform(currentForm.querySelector("#sprintStartDate").value, 'dd/MM/yyyy'),
            //"startDate": currentForm.querySelector("#sprintStartDate").value,
            "region": currentForm.querySelector("#sprintRegion").value,
            "market": currentForm.querySelector("#sprintMarket").value,
            "cascade": currentForm.querySelector("#sprintCascade").value,
            "ciEngineerNight": currentForm.querySelector("#sprintCiEng1").value,
            "bridgeOne": currentForm.querySelector("#sprintBridge1").value,
            "feRegion": currentForm.querySelector("#sprintFeRegion").value,
            "feNight": currentForm.querySelector("#sprintFe1Night").value,
            "ciEngineerDay": currentForm.querySelector("#sprintCiEng2").value,
            "bridge": currentForm.querySelector("#sprintBridge2").value,
            "feDay": currentForm.querySelector("#sprintFe2Day").value,
            "notes": currentForm.querySelector("#sprintNotes").value,
            "status": currentForm.querySelector("#sprintRemarks").value
          }
        }

        //console.log(schedulingDetails);

        /*    
  */


        // this.sharedService.userNavigation = true; //un block user navigation

        this.schedulingService.saveSchedule(schedulingDetails, this.sharedService.createServiceToken(), this.custId)
          .subscribe(
            data => {

              let currentData = data.json();
              this.showLoader = false;

              if (currentData.sessionId == "408" || currentData.status == "Invalid User") {

                this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'session-modal' });
              } else {

                if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {

                  if (currentData.status == "SUCCESS") {
                    this.message = "Scheduling details created successfully!";
                    this.displayModel(this.message, "successIcon");
                    // this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                    this.searchTabBind();
                    this.sprintStartDate = "";
                    this.forecastStartDate = "";
                    this.compDate = "";
                    this.feArrivalTime = "";
                    this.ciStartTime = "";
                    this.ciEndTime = "";
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
                  this.message = "Scheduling details created successfully!";
                  this.displayModel(this.message, "successIcon");
                  // this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
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
    this.schedulingService.downloadFile(this.sharedService.createServiceToken(), this.custId, this.searchDetails, this.searchStatus)
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
        this.schedulingService.uploadFile(formdata, this.sharedService.createServiceToken(), this.custId)
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
                    this.getSchedulingDetails();
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
                  this.getSchedulingDetails();
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
  clearSearchFrom() {
    this.bluePrintForm.nativeElement.reset();  
}

  closeModel() {
    this.successModalBlock.close();
    //this.ngOnInit();
    // this.getSchedulingDetails();
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

      this.getSchedulingDetails();


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
      this.getSchedulingDetails();
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



  editNeVersionRow(event, key, index) {
    $(".createbtn").addClass("buttonDisabled");
    let editState: any = event.target;

    if (editState.className == "editRow") {
      this.editIndex = index;
      this.currEditedRowVal = JSON.parse(JSON.stringify(key)); //storing the current value
      // this.programDetailsEntity = key.programDetailsEntity;
      $("#neversionData").find("br").remove();
      $(".saveRow").attr("class", "editRow");
      //$(".saveRow").addClass("validateForm");
      $(".cancelRow").attr("class", "deleteRow");
      if (editState.className != "editRowDisabled") { //enable click only if it is enabled
        editState.className = "saveRow";
        editState.parentNode.querySelector(".deleteRow").className = "cancelRow";
        // editState.nextSibling.className = "cancelRow";
        $(".cloneRow").attr("class", "cloneRowDisabled");
        // To enable one edit form at a time in table
        if (this.editableFormArray.length >= 1) {
          this.editableFormArray = [];
          this.editableFormArray.push(index);
        } else {
          this.editableFormArray.push(index);
        }
      }

    } else if (editState.className != "editRowDisabled") {
      // $(".saveRow").addClass("validateForm");
      //  let validations = this.validationData;
      /*  if(index == "new"){
          this.editIndex = index;
          validations.rules["programName_" + [index]] = { "required": true };
          validations.messages["programName_" + [index]] = { "required": "programName is required" };
          validations.rules["netType_" + [index]] = { "required": true };
          validations.messages["netType_" + [index]] = { "required": "Network Type is required" };
          validations.rules["relVer_" + [index]] = { "required": true };
          validations.messages["relVer_" + [index]] = { "required": "Release Version is required" };
       }else{
          validations.rules["programName_" + [index]] = { "required": true };
          validations.messages["programName_" + [index]] = { "required": "programName is required" };
          validations.rules["netType_" + [index]] = { "required": true };
          validations.messages["netType_" + [index]] = { "required": "Network Type is required" };
          validations.rules["relVer_" + [index]] = { "required": true };
          validations.messages["relVer_" + [index]] = { "required": "Release Version is required" };
       } */

      //  this.validationData = validations;

      //validator.performValidation(event,  this.validationData, "save_update");
      setTimeout(() => {
        if (this.isValidForm(event)) {
          this.showLoader = true;
          // this.element.nativeElement.querySelector('.createbtn').classList.remove('buttonDisabled');
          let schedulingDetails = {}
          if (this.custId == 2) {
            //Verizon
            let currentForm = event.target.parentNode.parentNode;
            //console.log(currentForm.querySelector("#editFeArrivalTime").value);
            //console.log((this.datePipe.transform("10:25 AM", 'hh:mm a'))? "yes":"no" );
            key.forecastStartDate = this.datePipe.transform(key.forecastStartDate, 'dd/MM/yyyy');
            key.compDate = this.datePipe.transform(key.compDate, 'dd/MM/yyyy');
            //key.feArrivalTime = this.datePipe.transform(key.feArrivalTime, 'hh:mm a');
            //key.ciStartTime = this.datePipe.transform(key.ciStartTime, 'hh:mm a');
            //key.ciEndTime = this.datePipe.transform(key.ciEndTime, 'hh:mm a');
            schedulingDetails = key;
          } else if (this.custId == 4) {
            //Sprint
          
            key.startDate = this.datePipe.transform(key.startDate, 'dd/MM/yyyy');
            schedulingDetails = key;
          }
          this.schedulingService.saveSchedule(schedulingDetails, this.sharedService.createServiceToken(), this.custId)
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
                        this.message = "Scheduling details saved successfully!";
                        //this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });                  
                        this.displayModel(this.message, "successIcon");
                        this.getSchedulingDetails();
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
                    this.message = "Scheduling details saved successfully!";
                    // this.successModalBlock = this.modalService.open(this.successModalRef,{keyboard: false, backdrop: "static",size:'lg',windowClass:"success-modal"});
                    this.displayModel(this.message, "successIcon");
                    this.getSchedulingDetails();
                  } else {
                    this.displayModel(jsonStatue.reason, "failureIcon");
                  }

                }, 1000); */
                //Please Comment while checkIn
              });

        }

      }, 0);
    }

  }

  deleteNeVersionRow(confirmModal, id, event, index) {
    if (event.target.className == "deleteRow") {
      this.modalService.open(confirmModal, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'confirm-modal' }).result.then((result) => {
        this.closeResult = `Closed with: ${result}`;
      }, (reason) => {
        this.showLoader = true;

        this.schedulingService.deleteSchedulingDetails(id, this.sharedService.createServiceToken(), this.custId)
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
                    this.message = "Scheduling details deleted successfully!";
                    // this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                    this.displayModel(this.message, "successIcon");
                    this.getSchedulingDetails();
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
                let jsonStatue = { "reason": "Scheduling Details Deleted Successfully", "sessionId": "5f3732a4", "serviceToken": "80356", "status": "SUCCESS" };
                if (jsonStatue.status == "SUCCESS") {
                  this.message = "Scheduling details deleted successfully!";
                  // this.successModalBlock = this.modalService.open(this.successModalRef,{keyboard: false, backdrop: "static",size:'lg',windowClass:"success-modal"});
                  this.displayModel(this.message, "successIcon");
                  this.getSchedulingDetails();
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


    }
  }

  addCloneRow(event, key, index) {

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
        let schedulingDetails = {};

        if (this.custId == 2) {
          //Verizon
          key.id=null;
          
          key.forecastStartDate = this.datePipe.transform(key.forecastStartDate, 'dd/MM/yyyy');
          key.compDate = this.datePipe.transform(key.compDate, 'dd/MM/yyyy');
          //key.feArrivalTime = this.datePipe.transform(key.feArrivalTime, 'hh:mm a');
          //key.ciStartTime = this.datePipe.transform(key.ciStartTime, 'hh:mm a');
          //key.ciEndTime = this.datePipe.transform(key.ciEndTime, 'hh:mm a');
          schedulingDetails = key;


        } else if (this.custId == 4) {
          //Sprint
          key.id=null;
          key.startDate = this.datePipe.transform(key.startDate, 'dd/MM/yyyy');
          schedulingDetails = key;
        }
        this.schedulingService.saveSchedule(schedulingDetails, this.sharedService.createServiceToken(), this.custId)
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
                      this.message = "Scheduling details saved successfully!";
                      // this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });                  
                      this.displayModel(this.message, "successIcon");
                      this.getSchedulingDetails();
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
                  this.message = "Scheduling details saved successfully!";
                  //this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });                  
                  this.displayModel(this.message, "successIcon");
                  this.getSchedulingDetails();
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


  cancelEditRow(index, identifier) {
    $(".editRowDisabled").attr("class", "editRow");
    let currentEditedForm = document.querySelector("#row_id_" + identifier);
    //this.editableFormArray.splice(this.editableFormArray.indexOf(index), 1);
    this.editableFormArray = [];
    this.paginationDisabbled = false;
    this.checkFormEnable(index); //TODO : need to recheck this function
    this.sharedService.userNavigation = true; //un block user navigation
    currentEditedForm.lastElementChild.lastElementChild.children[0].className = "editRow";
    // this.verizonData[index] = this.currEditedRowVal; // According to customer revert to actual data

  }

  checkFormEnable(index) {
    let indexValue = this.editableFormArray.indexOf(index);
    return indexValue >= 0 ? true : false;
  }

  cancelCreateNew(event) {
    this.searchTabBind();
  }


  custNameChange(selCustName) {
    this.editableFormArray = [];
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
      this.getSchedulingDetails();
    }

  }


  getCustomerList() {
    //validator.performValidation(event, this.validationData, "save_update");
    setTimeout(() => {

      // this.sharedService.userNavigation = true; //un block user navigation

      this.schedulingService.getCustomerIdList(this.sharedService.createServiceToken())
        .subscribe(
          data => {

            let currentData = data.json();
            this.showLoader = false;

            if (currentData.sessionId == "408" || currentData.status == "Invalid User") {
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
