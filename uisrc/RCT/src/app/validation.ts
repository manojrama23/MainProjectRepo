import * as $ from 'jquery';

export var validator = validator || {};

validator.validationData = {};
validator.rulesCount = 0;
validator.currentRule = {};


  /* triggered validation during createNew, edit, update and save
   * @param : event, validation Data and action
   * @retun : null
   */

	validator.performValidation = function(event, validationData, action){
		validator.validationData = validationData;
		validator.mapBinding(event, action);
	}

  /* bind events for current form HTML elements
   * @param : event and action
   * @retun : null
   */

	validator.mapBinding = function(event, action){
		
		var currentForm,
			currentFormElements;
		if(action == "create"){
			currentForm = $(".bluePrintFormWrapper").find("form")[0];
		} else if(action == "save_update"){
			currentForm = $(event.target).parents("form")[0];
		} else if(action == "edit"){
			currentForm = $(event.target).parents("form")[0];
		} else if(action == "search"){
			currentForm = $(event.target).parents("form")[0];
		}else if(action == "save"){
			currentForm = $(".changePswd").find("form")[0];
		}
		currentFormElements = currentForm.querySelectorAll("input,select,textarea,button");	
		$("body").on("blur change focusout keyup",$(currentFormElements),function(event){
		if( event.which != 9 ) {
        	validator.validateFieldData(event);
    	}
			
		});

		$("body").on("click",".validateForm",function(event){
			validator.validateFormData(event, currentForm);
		});
	}

  /* trigger validation for currently modified field
   * @param : event
   * @retun : null
   */

	validator.validateFieldData = function(event){
		validator.currentRule = validator.validationData.rules[event.target.id];

		for(validator.rulesCount = 0; validator.rulesCount < Object.keys(validator.currentRule).length; validator.rulesCount++){
			validator.traverseValidation(Object.keys(validator.currentRule)[validator.rulesCount], validator.currentRule[Object.keys(validator.currentRule)[validator.rulesCount]], event, event.target.id, event.target.value,"element");
		}
	}

  /* trigger validation for currently modified form elements
   * @param : event and currentForm
   * @retun : null
   */

	validator.validateFormData = function(event, currentForm){
		for (var comp = 0; comp < Object.keys(validator.validationData.rules).length; comp++) {

			validator.currentRule = validator.validationData.rules[Object.keys(validator.validationData.rules)[comp]];

			for(validator.rulesCount = 0; validator.rulesCount < Object.keys(validator.currentRule).length; validator.rulesCount++){
            	validator.traverseValidation(Object.keys(validator.currentRule)[validator.rulesCount], validator.currentRule[Object.keys(validator.currentRule)[validator.rulesCount]], event, Object.keys(validator.validationData.rules)[comp], $(currentForm).find("#"+Object.keys(validator.validationData.rules)[comp]).val(),"form");
			}
        }
	}

  /* select the validation type based on rules and start validate
   * @param : rules key, rules value, event, currentElement, currentValue and type
   * @retun : null
   */

	validator.traverseValidation = function (key, value, event, currentElement, currentValue, type){
		switch (key) {
	        case "required":
	        validator.required(key, value, event, currentElement, currentValue, type);
	        break;
	        case "minlength":
	        validator.minlength(key, value, event, currentElement, currentValue, type);
	        break;
	        case "maxlength":
	        validator.maxlength(key, value, event, currentElement, currentValue, type);
	        break;
	        case "pattern":
	        validator.pattern(key, value, event, currentElement, currentValue, type);
	        break;
	        case "customfunction":
	        validator.customfunction(key, value, event, currentElement, currentValue, type);
	        break;
	        case "range":
	        validator.range(key, value, event, currentElement, currentValue, type);
	        break;
	        case "compareField":
	        validator.compareField(key, value, event, currentElement, currentValue, type);
	        break;
	        case "compareEqualField":
	        validator.compareEqualField(key, value, event, currentElement, currentValue, type);
	        break;
	        case "compareNotEqualField":
            validator.compareNotEqualField(key, value, event, currentElement, currentValue, type);
            break;	        
	    }
	}

  /* validate field against rule type required
   * @param : rules key, rules value, event, currentElement, currentValue and type
   * @retun : null
   */

	validator.required = function(key, value, event, currentElement, currentValue, type){
		var currentElementValue = currentValue;
		if(	value && (currentElementValue == "" ||  currentElementValue == null ||  currentElementValue == undefined )){
			validator.displayMessage(key, value, event, currentElement, type);
		} else {
			validator.removeMessage(key, value, event, currentElement, type);
		}
	}

  /* validate field against rule type minlength
   * @param : rules key, rules value, event, currentElement, currentValue and type
   * @retun : null
   */

	validator.minlength = function(key, value, event, currentElement, currentValue, type){
		if (currentValue.length < value) {
			validator.displayMessage(key, value, event, currentElement, type);
		} else {
			validator.removeMessage(key, value, event, currentElement, type);
		}
	}

  /* validate field against rule type maxlength
   * @param : rules key, rules value, event, currentElement, currentValue and type
   * @retun : null
   */

	validator.maxlength = function(key, value, event, currentElement, currentValue, type){
		if (currentValue.length > value) {
			validator.displayMessage(key, value, event, currentElement, type);
		} else {
			validator.removeMessage(key, value, event, currentElement, type);
		}
	}

  /* validate field against rule type pattern
   * @param : rules key, rules value, event, currentElement, currentValue and type
   * @retun : null
   */

	validator.pattern = function(key, value, event, currentElement, currentValue, type){
		if (value.test(currentValue)) {
		    validator.removeMessage(key, value, event, currentElement, type);
		} else {
			validator.displayMessage(key, value, event, currentElement, type);
		}
	}

  /* validate field against rule type customfunction
   * @param : rules key, rules value, event, currentElement, currentValue and type
   * @retun : null
   */

	validator.customfunction = function(key, value, event, currentElement, currentValue, type){
		if (value) {
		    validator.displayMessage(key, value, event, currentElement, type);
		} else {
			validator.removeMessage(key, value, event, currentElement, type);
		}
	}

  /* validate field against rule type range
   * @param : rules key, rules value, event, currentElement, currentValue and type
   * @retun : null
   */

	validator.range = function(key, value, event, currentElement, currentValue, type){
		var currentRange = value.split(",");
		if(currentValue < currentRange[0] || currentValue > currentRange[1]){
			validator.displayMessage(key, value, event, currentElement, type);
		} else {
			validator.removeMessage(key, value, event, currentElement, type);
		}
	}

  /* validate field against comparision field
   * @param : rules key, rules value, event, currentElement, currentValue and type
   * @retun : null
   */

	validator.compareField = function(key, value, event, currentElement, currentValue, type){
		var currentElementValue = currentValue,
			currentForm = $(event.target).parents("form");

		if(	currentElementValue != $(currentForm).find(value).val() ){
			validator.displayMessage(key, value, event, currentElement, type);
		} else {
			validator.removeMessage(key, value, event, currentElement, type);
		}
	}	


  /* display error message below corresponding HTML element
   * @param : rules key, rules value, event, currentElement and type
   * @retun : null
   */
	validator.displayMessage = function(key, value, event, currentElement, type){
		let currentTarget : any;
		if(type == "element"){
			currentTarget = $(event.target);
			currentTarget.addClass("error-border");
			currentTarget.next("div").html( validator.validationData.messages[currentElement][key] );
			validator.rulesCount = validator.currentRule.length;
		} else {
			currentTarget = $(event.target).parents("form");
			currentTarget.find("#"+currentElement).addClass("error-border");
			currentTarget.find("#"+currentElement).next("div").html( validator.validationData.messages[currentElement][key] );
			validator.rulesCount = validator.currentRule.length;
		}
	}

  /* remove error message for corresponding HTML element
   * @param : rules key, rules value, event, currentElement and type
   * @retun : null
   */
	validator.removeMessage = function(key, value, event, currentElement, type){
		let currentTarget : any;
		if(type == "element"){
			currentTarget = $(event.target);
			currentTarget.removeClass("error-border");
			currentTarget.next("div").html("");
		} else {
			currentTarget = $(event.target).parents("form");
			currentTarget.find("#"+currentElement).removeClass("error-border");
			currentTarget.find("#"+currentElement).next("div").html("");
		}
	}

	validator.removeFormValidation = function (formElementClass, validationData) {
		let currentForm = $("." + formElementClass);
		for (var comp = 0; comp < Object.keys(validationData.rules).length; comp++) {
			let currentElement = Object.keys(validationData.rules)[comp];
			currentForm.find("#" + currentElement).removeClass("error-border");
			let errorMsgElement = currentForm.find("#" + currentElement).next("div")
			errorMsgElement ? errorMsgElement.html("") : "";
			// currentForm.find("#" + currentElement).next("div").html("");
		}
	}

	validator.compareEqualField = function(key, value, event, currentElement, currentValue, type){
		var currentElementValue = currentValue,
			currentForm = $(event.target).parents("form");
			
		if(	currentElementValue == $(currentForm).find(value).val() ){
			validator.displayMessage(key, value, event, currentElement, type);
		} else {
			validator.removeMessage(key, value, event, currentElement, type);
		}
	}

	validator.compareNotEqualField = function(key, value, event, currentElement, currentValue, type){
        var currentElementValue = currentValue,
            currentForm = $(event.target).parents("form");
        if(currentElementValue != "" || currentElementValue != null || currentElementValue != undefined){
            if(    currentElementValue == $(currentForm).find(value).val() ){
            validator.displayMessage(key, value, event, currentElement, type);
            }
            else {
            validator.removeMessage(key, value, event, currentElement, type);
        }
        }
    }   	