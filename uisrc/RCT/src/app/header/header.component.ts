import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import {NgbModal, ModalDismissReasons, NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {Router} from '@angular/router';
import { SharedService } from '../services/shared.service';
import {AuthenticationService} from '../services/authentication.service';
import { config } from '../config';
import { validator } from '../validation';
import {ChangePassword} from '../login/changePassword';
import * as FileSaver from 'file-saver';
import * as $ from 'jquery';
import { filter, map, mergeMap} from 'rxjs/operators';
/*import 'rxjs/add/operator/filter';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/mergeMap';*/

@Component({
  selector: 'rct-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
  providers:  [ AuthenticationService, NgbActiveModal ]
})

export class HeaderComponent implements OnInit {

  private isLoggedIn: boolean = false;
  private appConfig : object;

  menuRctmanageAdministrator : boolean;
  menuRepairAdministrator : boolean;
  menuAnalyticsAdministrator : boolean;
  
  menuRctmanageProduct : boolean;
  menuRepairProduct : boolean;
  menuAnalyticsProduct : boolean;

  navigationModalObj : any;
  newRoute : string;
  mainMenu: boolean = false;


  mainMenuCredentials : any;
  userGroupDetails : any;
  userNameDetails : any;
  subMenuItems : any;
  rctSnapShot: any;
  sessionExpiredModalBlock : any; // Helps to close/open the model window
  programSubscription: any;
  selectedProgram: any;
  showProgramList: boolean;
  showDuoConnectionStatus: boolean = false;
  networkType: any = "";
  programName: any = "";
  neIdsConnected: any = [];

 validationData : any = {
            "rules": {
                "currentPswd": {
                    "required": true
                },
                "newPswd": {
                    "required": true
                },
                "cnfrmPswd": {
                    "required": true,
                    "compareField" : "#newPswd"
                }
            },
            "messages": {
                "currentPswd": {
                    "required": "Current Password is required"   
                },
                "newPswd": {
                    "required": "New password is required"
                },
                "cnfrmPswd": {
                    "required": "Confirm Password is required",
                    "compareField": "New password and Confirm Password not matching"
                }
            }
          };

  @ViewChild('navigationConfirmModal') navigationConfirmModalRef: ElementRef;
  @ViewChild('sessionExpiredModal') sessionExpiredModalRef: ElementRef;
  
  // Following variables are used in change password model
  chngPwd: any = {};   
  chngPwdValidationMessage: string = "";
  showValidationMessage: boolean =false;
  chngePwdModalRef: any; // Helps to close/open the model window

  // Following variables are used to dispaly success, confirm and failure model(s)
  showModelMessage: boolean = false;
  modelData :any;
  
  duoSessionConnected:boolean;

  constructor( 
      private router:Router,
      private activeModal: NgbActiveModal,
      private modalService: NgbModal,
      private authenticationService: AuthenticationService,
      private sharedService: SharedService
  ) { }

  ngOnInit() {
    if( this.isValidNavigation() ){
        //main menu and submenu credentials to display
        this.mainMenuCredentials = config.credentials.mainMenu[JSON.parse(sessionStorage.loginDetails).userGroup];
        this.userGroupDetails = JSON.parse(sessionStorage.loginDetails).userGroup;
        this.userNameDetails = JSON.parse(sessionStorage.loginDetails).userName;
        this.rctSnapShot = JSON.parse(sessionStorage.loginDetails).rctSnapShot;

        this.programSubscription = JSON.parse(sessionStorage.loginDetails).programSubscription;
        // this.selectedProgram = JSON.parse(sessionStorage.selectedProgram);
        this.selectedProgram = sessionStorage.selectedProgram.length > 0 ? JSON.parse(sessionStorage.selectedProgram) : null;

        this.sharedService.getIsDuoSessionConnected().subscribe(duoSessionConnected => {
          this.duoSessionConnected = duoSessionConnected;
        })
        this.sharedService.getNeIDsConnected().subscribe(neIdsConnected => {
          this.neIdsConnected = neIdsConnected;
        })
    } else {
      this.logout();
    }
    console.log(this.userGroupDetails);
  }

 /* highlightMainMenu( currentMenu, currentMenuMapping ){

    let currentUrl : string = this.router.url.replace("/","");

    if(currentMenu == config.menuHighlight[currentUrl]){
      this.subMenuItems = config.credentials.subMenu[JSON.parse(sessionStorage.loginDetails).userGroup][currentMenuMapping];
      return "active";
    } 
    return "Inactive";
  }*/
    highlightMainMenu(currentMenu, currentMenuMapping) {
        let currentUrl: string = this.router.url.replace("/", "");
        this.networkType = JSON.parse(sessionStorage.getItem("selectedProgram")) ? JSON.parse(sessionStorage.getItem("selectedProgram")).networkTypeDetailsEntity.networkType : "";
        this.programName = JSON.parse(sessionStorage.getItem("selectedProgram")) ? this.programName = JSON.parse(sessionStorage.getItem("selectedProgram")).programName : "";
        if (currentMenu == config.menuHighlight[currentUrl]) {
            // for hide mainmenu and submenu in home page
            if (currentUrl == "home") {
                $("#mainMenu").addClass("displayNone");
                $("#subMenuBlock").addClass("visibilityHidden");
            } else {
                $("#mainMenu").removeClass("displayNone");
                $("#subMenuBlock").removeClass("visibilityHidden");
            }
            this.subMenuItems = config.credentials.subMenu[JSON.parse(sessionStorage.loginDetails).userGroup][currentMenuMapping];
            //Don't show Program list on Configuration tab, PostMigration -> EOD Reports and S&R tab
            // if (currentMenu == "dashboard" || currentMenu == "usermanagement" || currentMenu == "eodreports" || currentMenu == "scheduling" || currentMenu == "overallreports" || currentMenu == "reports") {
            if (config.showPrograms[currentUrl]) {
                this.showProgramList = true;
            }
            else {
                this.showProgramList = false;
            }

            if (config.showDuoConnectionStatus[currentUrl]) {
              this.showDuoConnectionStatus = true;
            }
            else {
              this.showDuoConnectionStatus = false;
            }
            return "active";
        }

        return "Inactive";
    }

    reloadPage() {
      
        // this.sharedService.selectedProgram = this.selectedProgram;
        sessionStorage.setItem('selectedProgram', JSON.stringify(this.sharedService.selectedProgram));
      	this.sharedService.changeProgram(this.sharedService.selectedProgram);
      //this.router.navigate([this.router.url]);
    }
  routeToLandingPage() {
    if (this.userGroupDetails == 'Default User' ) {
        this.router.navigate(["/usermanagement"]);
    }
    /* else if (this.userGroupDetails == 'Super Administrator') {
        this.router.navigate(["/home"]);
    } */
    else {
        this.router.navigate(["/dashboard"]);
        this.showProgramList = false;
        this.showDuoConnectionStatus = false;
        this.subMenuItems = [];
    }
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
            "home" : "home",
            "runtest" : "migration"
            
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
   * On click of main menu trigger the navigation manually
   * @param : Navigation url as string
   * @retun : null
   */

  mainMenuNavigation(navigation, navigationConfirmModal){
    this.newRoute = navigation;

    if(this.sharedService.userNavigation){
      this.router.navigate([navigation]);
    } else {
      this.navigationModalObj = this.modalService.open(navigationConfirmModal,{keyboard: false, backdrop: 'static',windowClass: 'confirm-modal', size:'lg'});
    }
  }

  /*
   * On click of navigation confirmation reset the flag, close modal and move to corresponding page
   * @param : null
   * @retun : null
   */

  dismissModal(){
    this.sharedService.userNavigation = true;
    this.navigationModalObj.close();
    this.router.navigate([this.newRoute]);
  }

  isValidForm(event){
    console.log("11",$(event.target).parents("form").find(".error-border"));
    return ( $(event.target).parents("form").find(".error-border").length == 0) ? true : false;
  }
  
/*
   * On click change password link open a modal popup
   * @param : content
   * @retun : null
   */

   
   openChangePasswordModal(content) {    
      $(".onclick-menu-change").blur();
      this.chngPwd="";
      this.chngePwdModalRef = this.modalService.open(content,{ windowClass: 'password-modal', keyboard: false, backdrop: 'static', size:'lg' });
      
      // To reset validation message(s) while opaning change password modal 
      this.chngPwdValidationMessage = "";
      validator.performValidation(event, this.validationData, "save");
  }

/*
   * On click save user password will be modified/updated
   * @param : null
   * @retun : json object
   */

  changePassword(content, event){

    validator.performValidation(event, this.validationData, "search");

    setTimeout(() => { 
      if(this.isValidForm(event)){
      //this.showValidationMessage = true;
        let currentForm = event.target.parentNode.parentNode,
              forgotPassword = {
                          "currentPwd": currentForm.querySelector("#currentPswd").value,
                          "newPswd": currentForm.querySelector("#newPswd").value,
                          "cnfrmPswd": currentForm.querySelector("#cnfrmPswd").value
                         },
              saveStatus;
              
        if(currentForm.querySelector("#newPswd").value == currentForm.querySelector("#cnfrmPswd").value){
          this.authenticationService.updatePassword(forgotPassword, this.sharedService.createServiceToken())
          .subscribe( data => {

                let jsonStatue = data.json().status;
      
                  if(data.json().sessionId == "408" || jsonStatue == "Invalid User"){
                      this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
                  } else {

                  if( this.sharedService.isvalidServiceToken( parseInt(data.json().serviceToken,10) ) || data.json().sessionId == "408"){

                    if(jsonStatue == "SUCCESS"){
                        this.displayModel("Password updated successfully!","successIcon");
                        this.chngePwdModalRef.close();
                    } else {
                        //this.displayModel(data.json().reason,"failureIcon");
                        this.showValidationMessage = true;
                        this.chngPwdValidationMessage = data.json().reason;
                    }
                  }  
                }  
                
                /* else{

                  console.log("Failed to check servise token");
                }*/
              },error => {
                   /*let jsonStatue = "SUCCESS";            
                    
                  if(jsonStatue == "SUCCESS")
                  {
                      this.displayModel("Password updated successfully!","successIcon");
                      this.chngePwdModalRef.close();
                  }
                  else 
                  {
                      //this.displayModel(data.json().reason,"failureIcon");
                      this.showValidationMessage = true;
                      this.chngPwdValidationMessage = "Not a registered email ID";
                      console.log(this.chngPwdValidationMessage);

                  }*/
          });
        }
      }
    },0);
  }


 /*
   * Used to dispaly the status messages like SUCCESS/FAILURE on view
   * @param : message, messageType (successIcon/failureIcon)
   * @retun : null
   */
  
  displayModel(message:string,messageType:string){

    this.showModelMessage = true;
    this.modelData = {
      "message" : message,
      "modelType" : messageType
    };

    setTimeout(() => { 
      this.showModelMessage = false;
    }, 10);
  }


  /*
   * On click of logout clear all the sessionStorage and move to index page
   * @param : null
   * @retun : null
   */

  logout(){
    this.authenticationService.logout(this.sharedService.createServiceToken())
    .subscribe( data => {
        let jsonStatue = data.json().status;
          if(data.json().sessionId == "408" || jsonStatue == "Invalid User"){
                this.sessionExpiredModalBlock = this.modalService.open(this.sessionExpiredModalRef,{keyboard: false, backdrop: 'static',size:'lg',windowClass:'session-modal'});
            } else {

            if( this.sharedService.isvalidServiceToken( parseInt(data.json().serviceToken,10) ) || data.json().sessionId == "408"){

              if(jsonStatue == "SUCCESS"){
                  this.isLoggedIn = false;
                  this.sharedService.setUserLoggedIn(false);
                  sessionStorage.clear();
                  this.router.navigate(['/']);
              }
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

  downloadHelp() {
      let fileName = "User_Manual.pdf";
      let filePath = "COMMON/Help/";

    this.authenticationService.downloadFile(fileName, filePath, this.sharedService.createServiceToken())
        .subscribe(
            data => {
                let blob = new Blob([data["_body"]], {
                    type: "application/octet-stream"
                });
    
                FileSaver.saveAs(blob, fileName);              
    
            },
            error => {
                //Please Comment while checkIn
                /* let jsonStatue: any = {"sessionId":"506db794","reason":"Download Failed","status":"SUCCESS","serviceToken":"63524"};
                    let blob = new Blob([jsonStatue["_body"]], {
                        type: "application/octet-stream"
                    });
    
                    FileSaver.saveAs(blob,fileName);
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

    compareFn(o1: any, o2: any) {
        return o1 && o2 ? o1.id === o2.id : o1 === o2;
    }

}
