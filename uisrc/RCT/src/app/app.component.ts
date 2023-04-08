import { Component, OnInit, HostListener,ViewChild,ElementRef} from '@angular/core';
import {Router, NavigationStart, NavigationEnd, ActivatedRoute} from '@angular/router';
import { NgbModal, ModalDismissReasons, NgbTabsetConfig } from '@ng-bootstrap/ng-bootstrap';
import { SharedService } from '../app/services/shared.service';
import { config } from './config';
import { Title } from '@angular/platform-browser';
//import { Observable } from 'rxjs/Observable';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { filter, map, mergeMap} from 'rxjs/operators';
import { RouterModule, Routes } from '@angular/router';
import { Idle, DEFAULT_INTERRUPTSOURCES } from '@ng-idle/core';
import { Keepalive } from '@ng-idle/keepalive';
import { GeneralconfigComponent } from './generalconfig/generalconfig.component';


@Component({
  selector: 'rct-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})



export class AppComponent {
  
  showModelMessage: boolean = false;
  successModalBlock: any;
  IdleModalBlock: any;
  modelData: any;
  message:any;
  isLoginState : boolean = false;
  private isLoggedIn: boolean = false;
  private TIMEDOUT_PERIOD = 10; // sets an message timeout of 10 seconds

  idleState = 'Not started.';
  timedOut = false;
  lastPing?: Date = null;
  showWarning: boolean = false;
  //@HostListener('window:beforeunload', ['$event'])
  /*beforeUnloadHander(event) {
      return false;
      window.sessionStorage.clear();
  }*/
  @ViewChild('successModal') successModalRef: ElementRef;

  constructor(
    private modalService: NgbModal,
  	private router:Router,
    private titleService: Title,
  	private activatedRoute: ActivatedRoute, 
    private sharedService: SharedService,
    private idle: Idle, private keepalive: Keepalive
  ) {
    this.initializeIdleStateTracker();
  }

  initializeIdleStateTracker() {
    let idleTime = 60;  //Set default time as 60 seconds
    
    this.idle.setIdle(idleTime);
    // sets a message timeout period of 10 seconds. after (idleTime + 10) seconds of inactivity, the user will be considered timed out.
    this.idle.setTimeout(this.TIMEDOUT_PERIOD);
    // sets the default interrupts, in this case, things like clicks, scrolls, touches to the document
    this.idle.setInterrupts(DEFAULT_INTERRUPTSOURCES);

    this.idle.onIdleEnd.subscribe(() => { 
      this.idleState = 'No longer idle.';
      console.log(this.idleState);
      this.reset();
    });
    
    this.idle.onTimeout.subscribe(() => {
      // this.IdleModalBlock.close();
      this.showWarning = false;
      this.idleState = 'Timed out!';
      this.timedOut = true;
      console.log(this.idleState);
      this.logout();
    });
    
    this.idle.onIdleStart.subscribe(() => {
        this.idleState = 'You\'ve gone idle!';
        console.log(this.idleState);
        this.showWarning = true;
    });
    
    this.idle.onTimeoutWarning.subscribe((countdown) => {
      this.idleState = 'You will time out in ' + countdown + ' seconds!'
      console.log(this.idleState);
    });

    // sets the ping interval to 15 seconds
    this.keepalive.interval(15);

    this.keepalive.onPing.subscribe(() => this.lastPing = new Date());

    this.sharedService.getUserLoggedIn().subscribe(userLoggedIn => {
      if (userLoggedIn) {
        this.idle.watch()
        this.timedOut = false;
      } else {
        this.idle.stop();
      }
    })
  }

  reset() {
    this.idle.watch();
    this.idleState = 'Started.';
    this.timedOut = false;
    this.showWarning = false;
  }

  setIdleTimeFromConfig(timeOut) {
    console.log("TimeOut set as : " + timeOut + " seconds before showing warning message for " + this.TIMEDOUT_PERIOD + " more seconds");
    this.idle.setIdle(timeOut);
    this.reset();
  }

  ngOnInit() {
    	this.isLoginState = this.sharedService.isLoginState;
    	this.sharedService.idleTimeoutEvent.subscribe((timeOut) => this.setIdleTimeFromConfig(timeOut - this.TIMEDOUT_PERIOD));
        /*
        * Get page title and set in after page navigation end
        * @param : null
        * @retun : null
        */
        this.router.events.pipe(
          filter(event => event instanceof NavigationEnd),
          map(() => this.activatedRoute),
          map((route) => {
            while (route.firstChild) route = route.firstChild;
            return route;
          }),
          filter((route) => route.outlet === 'primary'),
          mergeMap((route) => route.data))
          .subscribe((event) => this.titleService.setTitle(event['title']));

       /*
        * show or hide header based on page url  
        * @param : null
        * @retun : null
        */

         this.router.events
          .subscribe((event) => {
            if (event instanceof NavigationStart) {

              if( this.isValidNavigation() ){
              
                  var loginDetails = JSON.parse(sessionStorage.getItem("loginDetails"));

                  if (loginDetails) {
                      if ( JSON.parse(sessionStorage.getItem("loginDetails")).userName != "") {
                        this.isLoginState = true;
                        this.isLoggedIn = true;

                        //Call the sharedservice to update current program List
                        let currentUrl: string = event.url.replace("/", "");
                        if (config.showPrograms[currentUrl]) {
                          this.updateProgramList();
                        }
                      }

                      this.sharedService.emitIdleTimeOutEvent(loginDetails.timeOut);

                      //1. If valid User who already had loggedIn then dont navigate to LogIn Screen
                      if(this.isLoggedIn && event.url == "/") {
                      	this.isLoginState = true;
                        this.router.navigate(['/login']);
                        return false;
                      }
                  }else{
                      //2. If new user/User who already logged out is trying to navigate to otherscreens then show only logIn screen
                      if(!this.isLoggedIn && event.url != "/"){
                      	this.isLoginState = false;
                        this.router.navigate(['/']);
                        return true;
                      }
                  }

                  if(event.url == "/"){
                    this.sharedService.isLoginState = false;
                    this.isLoginState = false;
                  } else {
                    this.sharedService.isLoginState = true;
                    this.isLoginState = true;
                  }
              } else {
                this.logout();
              }
              
            }
        });
  }

  /*
   * check whether user navigation is valid
   * @param : null
   * @retun : boolean
   */

    isValidNavigation() {
      let url : string = window.location.pathname.replace("/",""),
          subMenuConfigState = false;

      if(url == "" || url == "/"){
        return true;
      }

      let mainMenuLinking = {
            "dashboard" : "dash",
            "usermanagement" : "configuration",
            "ciqupload" : "premigration",
            "home" : "home"
          },
          currentLink = config.menuHighlight[url],
          mainMenuConfig = config.credentials.mainMenu[JSON.parse(sessionStorage.loginDetails).userGroup][mainMenuLinking[currentLink]],
          subMenuConfig = config.credentials.subMenu[JSON.parse(sessionStorage.loginDetails).userGroup][mainMenuLinking[currentLink]];

      for(var menus = 0; menus < subMenuConfig.length; menus++){
        if(url == subMenuConfig[menus].link){
          subMenuConfigState = true;
          menus = subMenuConfig.length; 
        }
      }

      if( mainMenuConfig || subMenuConfigState ){
        return true;
      }
      
      return false;
    };

  /*
   * On click of logout clear all the sessionStorage and move to index page
   * @param : null
   * @retun : null
   */

  /* logout(){
    this.isLoggedIn = false;
    sessionStorage.clear();
    this.router.navigate(['/']);  
  } */

  logout(){
    this.sharedService.logout()
    .subscribe( data => {
        let jsonStatue = data.json().status;
            if( this.sharedService.isvalidServiceToken( parseInt(data.json().serviceToken,10) ) || data.json().sessionId == "408"){

              if(jsonStatue == "SUCCESS"){
                  this.isLoggedIn = false;
                  this.sharedService.setUserLoggedIn(false);
                  sessionStorage.clear();
                  this.router.navigate(['/']);
              }
            }
        },error => {
            let jsonStatue = "SUCCESS";
              
            if(jsonStatue == "SUCCESS"){
                this.isLoggedIn = false;
                  sessionStorage.clear();
                  this.sharedService.setUserLoggedIn(false);
                  this.router.navigate(['/']);
            }
        });
  }

  
  updateProgramList() {
    this.sharedService.getProgramList()
        .subscribe(
            data => {
              setTimeout(() => {
                let jsonStatue = data.json();
                    if (this.sharedService.isvalidServiceToken(parseInt(data.json().serviceToken, 10)) || data.json().sessionId == "408") {

                      if (jsonStatue.status == "SUCCESS") {
                        if (jsonStatue.programNamesList.length == 0) {
                          this.displayModel("No active customers available. Please activate atleast one customer.", "failureIcon");
  
                          // this.message = "Run test started successfully!";
                          // this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                          let currentUrl: string = this.router.url.replace("/", "");
                          if (currentUrl != "generalconfig") {
                            this.router.navigate(['/generalconfig']);
                          }
  
                        }
                        else
                        {
                          // Update shared service Program List  
                          this.sharedService.updateProgramListInSessionStorage(jsonStatue.programNamesList);
                        }
                        } else {
                            // this.displayModel(jsonStatue.status, "failureIcon");
                        }
                    }
                  }, 100);
            },
            error => {
                //Please Comment while checkIn

                /* setTimeout(() => {
                    let jsonStatue = JSON.parse('{"sessionId":"1e573a3b","serviceToken":"64934","status":"SUCCESS","programNamesList":[{"id":27,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"SPT-4G-CDU30-Latest","programDescription":"CDU30-Latest","status":"Active","creationDate":"2019-09-30T13:47:38.000+0000","createdBy":"superadmin","sourceProgramId":20,"sourceprogramName":"SPT-4G-CDU30"},{"id":26,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"SPT-4G-MIMO-Latest","programDescription":"MIMO-Latest","status":"Active","creationDate":"2019-09-27T14:16:53.000+0000","createdBy":"superadmin","sourceProgramId":24,"sourceprogramName":"SPT-4G-MIMO-Latest1"},{"id":35,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"SPT-4G-MIMO-Testing","programDescription":"MIMO-Testing","status":"Active","creationDate":"2020-08-12T10:13:09.000+0000","createdBy":"superadmin","sourceProgramId":26,"sourceprogramName":"SPT-4G-MIMO-Latest"},{"id":43,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-CBAND","programDescription":"","status":"Active","creationDate":"2021-01-28T06:50:20.000+0000","createdBy":"superadmin","sourceProgramId":null,"sourceprogramName":null},{"id":38,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-FSU","programDescription":"FSU","status":"Active","creationDate":"2020-06-05T01:33:48.000+0000","createdBy":"superadmin","sourceProgramId":34,"sourceprogramName":"VZN-4G-USM-LIVE"},{"id":36,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LAB","programDescription":"LAB","status":"Active","creationDate":"2020-05-04T13:07:17.000+0000","createdBy":"superadmin","sourceProgramId":34,"sourceprogramName":"VZN-4G-USM-LIVE"},{"id":30,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-Latest","programDescription":"USM-Latest","status":"Active","creationDate":"2019-10-10T05:55:57.000+0000","createdBy":"superadmin","sourceProgramId":28,"sourceprogramName":"VZN-4G-USM-TEST"},{"id":34,"networkTypeDetailsEntity":{"id":6,"networkType":"4G","createdBy":"superadmin","caretedDate":"2020-03-17T11:55:56.000+0000","status":"Active","remarks":"","networkColor":"#ec8cab"},"programName":"VZN-4G-USM-LIVE","programDescription":"LIVE","status":"Active","creationDate":"2019-11-22T06:36:02.000+0000","createdBy":"superadmin","sourceProgramId":30,"sourceprogramName":"VZN-4G-USM-Latest"},{"id":42,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-DSS","programDescription":"DSS","status":"Active","creationDate":"2020-10-16T04:41:24.000+0000","createdBy":"superadmin","sourceProgramId":null,"sourceprogramName":null},{"id":40,"networkTypeDetailsEntity":{"id":7,"networkType":"5G","createdBy":"superadmin","caretedDate":"2019-09-04T06:47:55.000+0000","status":"Active","remarks":"Ran from WFM","networkColor":"#827fbb"},"programName":"VZN-5G-MM","programDescription":"MM","status":"Active","creationDate":"2020-08-27T06:50:11.000+0000","createdBy":"superadmin","sourceProgramId":34,"sourceprogramName":"VZN-4G-USM-LIVE"}]}');
                    // let jsonStatue = JSON.parse('{"sessionId":"9a349dc","serviceToken":"70206","status":"SUCCESS","programNamesList":[]}');

                    if (jsonStatue.status == "SUCCESS") {
                      if (jsonStatue.programNamesList.length == 0) {
                        this.displayModel("No active customers available. Please activate atleast one customer.", "failureIcon");

                        // this.message = "Run test started successfully!";
                        // this.successModalBlock = this.modalService.open(this.successModalRef, { keyboard: false, backdrop: 'static', size: 'lg', windowClass: 'success-modal' });
                        let currentUrl: string = this.router.url.replace("/", "");
                        if (currentUrl != "generalconfig") {
                          this.router.navigate(['/generalconfig']);
                        }

                      }
                      else
                      {
                        // Update shared service Program List  
                        this.sharedService.updateProgramListInSessionStorage(jsonStatue.programNamesList);
                      }
                        
                    } else {
                        // this.displayModel(jsonStatue.status, "failureIcon");
                    }
                }, 100); */

                //Please Comment while checkIn   
            });
  }


  displayModel(message: string, messageType: string) {

    this.showModelMessage = true;
    this.modelData = {
        "message": message,
        "modelType": messageType
    };

    setTimeout(() => {
        this.showModelMessage = false;
    }, 10);
}



closeModel() {        
  this.successModalBlock.close();

}


}

/*import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'RCT';
}*/

