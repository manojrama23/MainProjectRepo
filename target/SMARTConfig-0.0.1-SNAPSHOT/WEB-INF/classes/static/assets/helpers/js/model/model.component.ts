import { Component, OnInit , Input, SecurityContext} from '@angular/core';
import {NgbModal, NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {DomSanitizer, SafeHtml} from '@angular/platform-browser';

@Component({
  selector: 'rct-model',
  templateUrl: './model.component.html',
  styleUrls: ['./model.component.scss'],
  providers:  [ NgbActiveModal ]
})
export class ModelComponent implements OnInit {

  public manipulatedMessage: string;
  public manipulatedInfo: string;
  public modelType: string; 
  private modelRefrence: any = {};


  @Input() public message: string;
  @Input() public setModelType: string;

  constructor(
   
    private modalService: NgbModal,
    
  ) {}

  ngOnInit() {
  }

  ngOnChanges (){
    let extractedMessage = JSON.parse(JSON.stringify(this.message));
    this.manipulatedMessage = extractedMessage.message;
    this.manipulatedInfo = extractedMessage.infoLog || "";
    this.modelType = extractedMessage.modelType; 

    document.getElementById("openModal").click();
  } 

  public open(content){

    this.modelRefrence = this.modalService.open(content,{ windowClass: 'failure-modal',keyboard: false, backdrop: 'static' , size: 'lg'});

  }

  public closeModel(){
    //location.reload();
    console.log(this.modelType);
    /*if(this.modelType == "successIcon"){
    //  location.reload();
     //  this.showLoader = true;
    }*/
    this.modelRefrence.close();
  }
}
