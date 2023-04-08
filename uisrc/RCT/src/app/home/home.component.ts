import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'rct-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  userGroup:any;	
  selectedCustomer:any;
  userGroupDetails:any;
  selectedCustomerList:any
  constructor() { }

  ngOnInit() {
  	this.userGroup = JSON.parse(sessionStorage.loginDetails).userGroup;
    this.userGroupDetails = JSON.parse(sessionStorage.loginDetails);
  }

  	selectedCustomerName(selectedCustomerName){
		  for (let itm of this.userGroupDetails.customerList) {
        if(itm.customerName == selectedCustomerName){
          sessionStorage.setItem('selectedCustomerList', JSON.stringify({
              "customerName":itm.customerName,
              "id":itm.id
          }));
        }
      }
	 }
}

