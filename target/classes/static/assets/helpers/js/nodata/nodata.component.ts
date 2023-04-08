import { Component, OnInit, Input , SecurityContext} from '@angular/core';
import {DomSanitizer, SafeHtml} from '@angular/platform-browser';

@Component({
  selector: 'rct-nodata',
  templateUrl: './nodata.component.html',
  styleUrls: ['./nodata.component.scss']
})
export class NodataComponent implements OnInit {

  public title: string = "No Data Found";
  public manipulatedSubTitle: string;
  
  @Input() public subTitle:string;

  constructor(

    private _sanitizer: DomSanitizer

    ) {
  }

  ngOnInit() {
  }

  ngOnChanges (){

     this.manipulatedSubTitle = this._sanitizer.sanitize(SecurityContext.HTML,this.subTitle);
  }
}
