import { Injectable, EventEmitter } from '@angular/core';
import { Http, Headers, Response, ResponseContentType } from '@angular/http';
import * as $ from 'jquery';
import { Subject } from 'rxjs/Subject';
import { Observable } from 'rxjs';
@Injectable()
export class SharedService {
  
  STD_INTERVAL_DELAY:any = 10000;
  //if user in middle of edit or create new then block navigation
  userNavigation: boolean = true;
  isLoginState : boolean = false;
    
  programSubscription: any;
  selectedProgram: any;

  private servicemgmt = [];

  headers: any;
  private messageSource = new Subject();
  private userLoggedIn = new Subject<boolean>();
  private isDuoConnected = new Subject<boolean>();
  private neIdsConnected = new Subject<any>();
  duoConnInterval: any;
  idleTimeoutEvent = new EventEmitter();

  currentPgm = this.messageSource.asObservable();

    constructor(private http: Http) {
        this.headers = new Headers({ "content-type": "application/json" });
        this.userLoggedIn.next(false);
        this.isDuoConnected.next(false);
        this.neIdsConnected.next([]);
        // this.startDuoConnectionCheck(); // Start and Stop from component ngOnInit and ngOnDestroy respectively
    }
    changeProgram(selPgm) {
      this.messageSource.next(selPgm);  
    }
    setUserLoggedIn(userLoggedIn:boolean) {
      this.userLoggedIn.next(userLoggedIn);
    }
    getUserLoggedIn() : Observable<boolean> {
      return this.userLoggedIn.asObservable();
    }

  /*
   * Create a new random service token .
   * @param : null
   * @retun : serviceToken
   */
	 
	createServiceToken() {
	 var serviceToken = Math.floor(Math.random() * (50000 - 100000) + 100000);
	 this.servicemgmt.push(serviceToken);
	 return serviceToken;
	};

  /*
   * Validating the service token from service mgmt array. 
   * @param : serviceToken
   * @retun : boolean
   */
	 
	isvalidServiceToken(serviceToken){	 
	 if(this.servicemgmt.indexOf(serviceToken) >= 0){
		this.servicemgmt.splice($.inArray(serviceToken, this.servicemgmt),1);
		return true;
	 }	
	 return false;
    };

  /*
   * Enable and disable (create New and Delete) button based on priviledge
   * @param : privilege Object and currentViewScope
   * @retun : null
   */
	
	privilegeSetting(privilege, currentViewScope){
		if(Object.keys(privilege).length > 0){
			currentViewScope.createbtn = "buttonDisabled";
			currentViewScope.deleteRows = "deleteRowDisabled";
		} else {
			currentViewScope.deleteRows = "deleteRow";
		}
	};

  /*
   * On click sort header in table then sort the data ascending and decending
   * @param : columnName, event, current Index and current Scope
   * @retun : sorted array
   */

	dynamicSort(predicate, event, index, currentScope, parents = "") {
      let tableSort = document.querySelectorAll(".sorting"),
          tableHeaders,
          rowClassNames,
          sortType = "ascending";

      for (tableHeaders = 0; tableHeaders < tableSort.length; tableHeaders++) {
          rowClassNames = tableSort[tableHeaders];

          if(tableHeaders == index){

            if(rowClassNames.className.indexOf("asc") > 0){
              rowClassNames.className = rowClassNames.className.replace("sorting asc","sorting desc");
              sortType = "descending";
            } else if(rowClassNames.className.indexOf("desc") > 0){
              rowClassNames.className = rowClassNames.className.replace("sorting desc","sorting asc");
              sortType = "ascending";
            } else {
              rowClassNames.className = rowClassNames.className.replace("sorting","sorting asc");
              sortType = "ascending";
            }
          } else {
            rowClassNames.className = tableSort[tableHeaders].className.replace("asc","").replace("desc","");  
          }
      }

      if(sortType == "ascending"){
        currentScope.sort(function (a,b) {
            let result = 0;
            if(parents) {
                if(a[parents] && b[parents]) {
                    if (isNaN(a[predicate]) && isNaN(b[predicate])) {
                        result = (a[parents][predicate].toLowerCase() < b[parents][predicate].toLowerCase()) ? -1 : (a[parents][predicate].toLowerCase() > b[parents][predicate].toLowerCase()) ? 1 : 0;
                    }
                    else {
                        result = (parseInt(a[parents][predicate]) < parseInt(b[parents][predicate])) ? -1 : (parseInt(a[parents][predicate]) > parseInt(b[parents][predicate])) ? 1 : 0;
                    }
                }
                else if(a[parents]) {
                    result = 1;
                }
                else {
                    result = -1;
                }
            }
            else {
                if (a[predicate] && b[predicate]) {
                    if (isNaN(a[predicate]) && isNaN(b[predicate])) {
                        result = (a[predicate].toLowerCase() < b[predicate].toLowerCase()) ? -1 : (a[predicate].toLowerCase() > b[predicate].toLowerCase()) ? 1 : 0;
                    }
                    else {
                        result = (parseInt(a[predicate]) < parseInt(b[predicate])) ? -1 : (parseInt(a[predicate]) > parseInt(b[predicate])) ? 1 : 0;
                    }
                }
                else if(a[predicate]) {
                    result = 1;
                }
                else {
                    result = -1;
                }
            }
          return result;
        });        
      } else {
        currentScope.reverse();
      }      
    }
      
      getProgramList() {
        return this.http.post('/getProgramList', JSON.stringify({
            "serviceToken": this.createServiceToken(),
            "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
        }), { headers: this.headers });
      }
      logout() {
        return this.http.post('/logoutAction', JSON.stringify({
          "serviceToken": this.createServiceToken(),
          "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId
        }), { headers: this.headers });
      }

      updateProgramListInSessionStorage(programNamesList) {
        this.programSubscription = programNamesList;
        
        let loginDetails = JSON.parse(sessionStorage.getItem("loginDetails"));
        loginDetails.programSubscription = programNamesList;
        sessionStorage.setItem('loginDetails', JSON.stringify(loginDetails));

        // Get if currently selected program is available in the list, 
        // then select select same, else select first one
        let selectedProgId = JSON.parse(sessionStorage.getItem("selectedProgram")).id;
        let index = programNamesList.findIndex(x => x.id == selectedProgId);

        if(index == -1) {
            sessionStorage.setItem('selectedProgram', JSON.stringify(programNamesList[0]));
            this.selectedProgram = programNamesList[0];
        }
        else {
            this.selectedProgram = programNamesList[index]
        }
    }

    updateSelectedCIQInSessionStorage(ciq) {
      sessionStorage.setItem('selectedCIQ', JSON.stringify(ciq));
    }
    
    emitIdleTimeOutEvent(timeOut) {
      this.idleTimeoutEvent.emit(timeOut);
    }
    
    startDuoConnectionCheck() {
      this.getDuoSessionStatus();
      // On every 10s refresh the status
      if (!this.duoConnInterval) {
        this.duoConnInterval = setInterval(() => {
          this.getDuoSessionStatus();
        }, this.STD_INTERVAL_DELAY);
      }
    }

    stopDuoConnectionCheck() {
      clearInterval( this.duoConnInterval );
      this.duoConnInterval = null;
    }

    getDuoSessionStatus() {
      this.getDuoSessionStatusService()
        .subscribe(
          data => {
            setTimeout(() => {
              let jsonStatue = data.json();
              if (this.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {
                this.setIsDuoSessionConnected(jsonStatue.isDuoSessionConnected);
                this.setNeIDsConnected(jsonStatue.neIdsConnected);
              }
            }, 100);
          },
          error => {
            //Please Comment while checkIn

            /* setTimeout(() => {
              let jsonStatue;// = JSON.parse('{"isDuoSessionConnected":true,"sessionId":"f85d3b21","serviceToken":"85947"}');
              let randomTemp = Math.floor(Math.random() * 2);
              console.log("DUO Connected : " + randomTemp);
              if(randomTemp % 2 == 0) {
                jsonStatue = JSON.parse('{"isDuoSessionConnected":true,"sessionId":"f85d3b21","serviceToken":"85947","neIdsConnected":["user_12345","user_54321"]}');
              }
              else {
                jsonStatue = JSON.parse('{"isDuoSessionConnected":false,"sessionId":"f85d3b21","serviceToken":"85947"}');
              }
              this.setIsDuoSessionConnected(jsonStatue.isDuoSessionConnected);
              this.setNeIDsConnected(jsonStatue.neIdsConnected);
            }, 100); */

            //Please Comment while checkIn   
          });
    }

    getDuoSessionStatusService() {
      return this.http.post('/getDuoSessionStatus', JSON.stringify({
        "serviceToken": this.createServiceToken(),
        "sessionId": JSON.parse(sessionStorage.getItem("loginDetails")).sessionId,
      }), { headers: this.headers });
    }

    setIsDuoSessionConnected(duoSessionConnected:boolean) {
      this.isDuoConnected.next(duoSessionConnected);
    }
    setNeIDsConnected(neIdsConnected:any) {
      this.neIdsConnected.next(neIdsConnected);
    }
    getIsDuoSessionConnected() : Observable<boolean> {
      return this.isDuoConnected.asObservable();
    }
    getNeIDsConnected() : Observable<any> {
      return this.neIdsConnected.asObservable();
    }

    getCurrentTimestamp() {
      let dt = new Date();
      let year = dt.getFullYear().toString();
      let month = this.addLeadeingZero((dt.getMonth() + 1).toString());
      let date = this.addLeadeingZero(dt.getDate().toString());
      let hr = this.addLeadeingZero(dt.getHours().toString());
      let min = this.addLeadeingZero(dt.getMinutes().toString());
      let sec = this.addLeadeingZero(dt.getSeconds().toString());
      
      return (year + month + date + "_" + hr + "_" + min + "_" + sec);
    }
    
    /**
     * Get time in HH:MM format from a passed date
     * @param dt
     * @returns HH:MM format string of time
     */
    getTimeFromDate(dt:Date) {
      let hr = this.addLeadeingZero(dt.getHours().toString());
      let min = this.addLeadeingZero(dt.getMinutes().toString());

      return hr + ":" + min
    }

    /**
     * 
     * @param value
     * Add leading zero if value is single digit
     */
    addLeadeingZero(value) {
      if(value.toString().length == 1) {
        value = "0" + value;
      }
      return value;
    }

    camelCase(str) {
      let retString = "";
      if (str) {
        let tempStr = str.toLowerCase();
        retString = tempStr[0].toUpperCase() + tempStr.slice(1);
      }
      return retString;
    }

}
