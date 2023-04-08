import { BrowserModule, Title } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormGroup, FormControl, FormsModule, ReactiveFormsModule, FormArray, FormBuilder, Validators, NgModel } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { HttpModule } from '@angular/http';
import { LocationStrategy, HashLocationStrategy, DatePipe } from '@angular/common';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { filter, map, mergeMap } from 'rxjs/operators';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { ChartsModule } from 'ng4-charts/ng4-charts';
import { TreeModule } from 'angular-tree-component';
import { SelectDropDownModule } from 'ngx-select-dropdown';
import { LeafletModule } from '@asymmetrik/ngx-leaflet';
import { HttpClientModule } from "@angular/common/http";
import { NgIdleKeepaliveModule } from '@ng-idle/keepalive'; // this includes the core NgIdleModule but includes keepalive providers for easy wireup
import { MomentModule } from 'angular2-moment'; // optional, provides moment-style pipes for date formatting
//import { ChartsModule } from 'ng2-charts'
import * as $ from 'jquery';
import * as FileSaver from 'file-saver';

//import { DateTimePickerModule} from 'ng-pick-datetime';
import { OwlDateTimeModule, OwlNativeDateTimeModule } from 'ng-pick-datetime';
import { NgSelectModule } from '@ng-select/ng-select';
import { NgJsonEditorModule } from 'ang-jsoneditor';
//import { MultiselectDropdownModule } from 'angular-2-dropdown-multiselect';
//import { AgmCoreModule } from '@agm/core';
/*
 * import necessary components needed for application
 */

import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { HeaderComponent } from './header/header.component';
import { HomeComponent } from './home/home.component';
import { FooterComponent } from './footer/footer.component';
import { LoaderComponent } from '../assets/helpers/js/loader/loader.component';
import { ModelComponent } from '../assets/helpers/js/model/model.component';
import { NodataComponent } from '../assets/helpers/js/nodata/nodata.component';
import { UsermanagementComponent } from './usermanagement/usermanagement.component';
/*
 * import shared services needed accross all components
 */
import { SharedService } from './services/shared.service';
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';
import { DashboardComponent } from './dashboard/dashboard.component';
import { SystemmanagerconfigComponent } from './systemmanagerconfig/systemmanagerconfig.component';
import { GeneralconfigComponent } from './generalconfig/generalconfig.component';
import { CiquploadComponent } from './ciqupload/ciqupload.component';
import { AudittrailComponent } from './audittrail/audittrail.component';
import { RanconfigComponent } from './ranconfig/ranconfig.component';
import { PregenerateComponent } from './pregenerate/pregenerate.component';
import { PregrowComponent } from './pregrow/pregrow.component';
import { RuntestComponent } from './runtest/runtest.component';
import { UploadScriptComponent } from './upload-script/upload-script.component';
import { FilerulebuilderComponent } from './filerulebuilder/filerulebuilder.component'
import { CmdrulebuilderComponent } from './cmdrulebuilder/cmdrulebuilder.component';
import { UsecasebuilderComponent } from './usecasebuilder/usecasebuilder.component';
import { ChecklistComponent } from './checklist/checklist.component';
import { ObjectKeysPipe, ObjectValuesPipe, ObjectKeyValuesPipe } from './pipes/key-value.pipe';
import { RanatpComponent } from './ranatp/ranatp.component';
import { AuditpostComponent } from './auditpost/auditpost.component';
import { NemappingComponent } from './nemapping/nemapping.component';
import { SitedataComponent } from './sitedata/sitedata.component';
import { EodreportsComponent }from './eodreports/eodreports.component';
import { SchedulingComponent }from './scheduling/scheduling.component';
import { OverallreportsComponent }from './overallreports/overallreports.component';
import { SnrreportsComponent } from './snrreports/snrreports.component';
import { NegrowComponent } from './negrow/negrow.component';
import { WorkflowmgmtComponent } from './workflowmgmt/workflowmgmt.component';
import { ReportsComponent } from './reports/reports.component';
import { PreauditComponent } from './preaudit/preaudit.component';
import { NestatusComponent } from './nestatus/nestatus.component';


//import { PendingChangesGuard } from './guard/pending-changes.guard';
//define project Routes

const appRoutes: Routes = [
    { path: '', component: LoginComponent, pathMatch: 'full', data: { title: 'S-RCT - LOGIN' } },
    { path: 'home', component: HomeComponent, data: { title: 'S-RCT - HOME' } },
    { path: 'dashboard', component: DashboardComponent, data: { title: 'S-RCT - DASHBOARD' }/*, canDeactivate: [PendingChangesGuard]*/ },
    { path: 'usermanagement', component: UsermanagementComponent, data: { title: 'S-RCT - USER MANAGEMENT' }/*, canDeactivate: [PendingChangesGuard]*/ },
    { path: 'networkconfig', component: SystemmanagerconfigComponent, data: { title: 'S-RCT - NETWORK CONFIG' }/*, canDeactivate: [PendingChangesGuard]*/ },
    { path: 'generalconfig', component: GeneralconfigComponent, data: { title: 'S-RCT - GENERAL CONFIG' }/*, canDeactivate: [PendingChangesGuard]*/ },
    { path: 'audittrail', component: AudittrailComponent, data: { title: 'S-RCT - AUDIT TRAIL' }/*, canDeactivate: [PendingChangesGuard]*/ },
    { path: 'ciqupload', component: CiquploadComponent, data: { title: 'S-RCT - CIQ UPLOAD' }/*, canDeactivate: [PendingChangesGuard]*/ },
    { path: 'nemapping', component: NemappingComponent, data: { title: 'S-RCT - NE MAPPING' }/*, canDeactivate: [PendingChangesGuard]*/ },
    { path: 'ranconfig', component: RanconfigComponent, data: { title: 'S-RCT - RAN CONFIG' }/*, canDeactivate: [PendingChangesGuard]*/ },
    { path: 'checklist', component: ChecklistComponent, data: { title: 'S-RCT - CHECK LIST' }/*, canDeactivate: [PendingChangesGuard]*/ },
    { path: 'pregenerate', component: PregenerateComponent, data: { title: 'S-RCT - GENERATE' }/*, canDeactivate: [PendingChangesGuard]*/ },
    { path: 'negrow', component: NegrowComponent, data: { title: 'S-RCT - NE GROW' }/*, canDeactivate: [PendingChangesGuard]*/ },
    { path: 'runtest', component: RuntestComponent, data: { title: 'S-RCT - RUN TEST' }/*, canDeactivate: [PendingChangesGuard]*/ },
    { path: 'uploadScript', component: UploadScriptComponent, data: { title: 'S-RCT - UPLOAD SCRIPT'}/*, canDeactivate: [PendingChangesGuard]*/ },    
    { path: 'cmdrulebuilder', component: CmdrulebuilderComponent, data: { title: 'S-RCT - COMMAND RULE BUILDER' }/*, canDeactivate: [PendingChangesGuard]*/ },
    { path: 'usecasebuilder', component: UsecasebuilderComponent, data: { title: 'S-RCT - USE CASE BUILDER' } },
    { path: 'ranatp', component: RanatpComponent, data: { title: 'S-RCT - RAN ATP' } },
    { path: 'sitedata', component: SitedataComponent, data: { title: 'S-RCT - SITE DATA' } },
    { path: 'audit', component: AuditpostComponent, data: { title: 'S-RCT - AUDIT' } },
    { path: 'preaudit', component: PreauditComponent, data: { title: 'S-RCT - PRE AUDIT' } },
    { path: 'negrowstatus', component: NestatusComponent, data: { title: 'S-RCT - NE STATUS' } },
    { path: 'eodreports',component: EodreportsComponent,data: { title:'S-RCT - EOD REPORTS' } },
    { path: 'scheduling',component: SchedulingComponent,data: { title:'S-RCT - SCHEDULING' } },
    { path: 'overallreports',component: OverallreportsComponent,data: { title:'S-RCT - OVERALL REPORTS' } },
    { path: 'reports',component: SnrreportsComponent,data: { title:'S-RCT - REPORTS' } },

    { path: 'workflowmgmt',component: WorkflowmgmtComponent,data: { title:'S-RCT - WORK FLOW MANAGEMENT' } },
    { path: 'report',component: ReportsComponent,data: { title:'S-RCT - S&R' } }
];

@NgModule({

    //declare all the component in a project 

    declarations: [
        AppComponent,
        LoginComponent,
        HeaderComponent,
        HomeComponent,
        FooterComponent,
        LoaderComponent,
        ModelComponent,
        NodataComponent,
        DashboardComponent,
        UsermanagementComponent,
        SystemmanagerconfigComponent,
        GeneralconfigComponent,
        CiquploadComponent,
        AudittrailComponent,
        RanconfigComponent,
        PregenerateComponent,
        PregrowComponent,
        RuntestComponent,
        UploadScriptComponent,        
        FilerulebuilderComponent,
        CmdrulebuilderComponent,
        UsecasebuilderComponent,
        ChecklistComponent,
        ObjectKeysPipe,
        ObjectValuesPipe,
        ObjectKeyValuesPipe,
        RanatpComponent,
        AuditpostComponent,
        NemappingComponent,
        SitedataComponent,
        EodreportsComponent,
        SchedulingComponent,
        OverallreportsComponent,
        SnrreportsComponent,
        NegrowComponent,
        WorkflowmgmtComponent,
        ReportsComponent,
        PreauditComponent,
        NestatusComponent
    ],

    // import required modules

    imports: [
        BrowserModule, BrowserAnimationsModule, /*DateTimePickerModule*/OwlDateTimeModule, OwlNativeDateTimeModule, FormsModule, ReactiveFormsModule, RouterModule.forRoot(appRoutes, {onSameUrlNavigation: 'reload'}), BrowserModule, NgbModule.forRoot(), NgMultiSelectDropDownModule.forRoot(), HttpModule, TreeModule, ChartsModule,SelectDropDownModule, LeafletModule.forRoot(), NgSelectModule, 
        HttpClientModule, MomentModule, NgIdleKeepaliveModule.forRoot(), NgJsonEditorModule
    ],
    providers: [Title, SharedService, { provide: LocationStrategy, useClass: HashLocationStrategy }, DatePipe],

    // bootstrap a component 

    bootstrap: [AppComponent]
})

export class AppModule { }

//platformBrowserDynamic().bootstrapModule(AppModule);
