/**
 * @fileoverview added by tsickle
 * @suppress {checkTypes} checked by tsc
 */
/**
 * @record
 */
export function IDropdownSettings() { }
function IDropdownSettings_tsickle_Closure_declarations() {
    /** @type {?|undefined} */
    IDropdownSettings.prototype.singleSelection;
    /** @type {?|undefined} */
    IDropdownSettings.prototype.idField;
    /** @type {?|undefined} */
    IDropdownSettings.prototype.textField;
    /** @type {?|undefined} */
    IDropdownSettings.prototype.enableCheckAll;
    /** @type {?|undefined} */
    IDropdownSettings.prototype.selectAllText;
    /** @type {?|undefined} */
    IDropdownSettings.prototype.unSelectAllText;
    /** @type {?|undefined} */
    IDropdownSettings.prototype.allowSearchFilter;
    /** @type {?|undefined} */
    IDropdownSettings.prototype.clearSearchFilter;
    /** @type {?|undefined} */
    IDropdownSettings.prototype.maxHeight;
    /** @type {?|undefined} */
    IDropdownSettings.prototype.itemsShowLimit;
    /** @type {?|undefined} */
    IDropdownSettings.prototype.limitSelection;
    /** @type {?|undefined} */
    IDropdownSettings.prototype.searchPlaceholderText;
    /** @type {?|undefined} */
    IDropdownSettings.prototype.noDataAvailablePlaceholderText;
    /** @type {?|undefined} */
    IDropdownSettings.prototype.closeDropDownOnSelection;
    /** @type {?|undefined} */
    IDropdownSettings.prototype.showSelectedItemsAtTop;
    /** @type {?|undefined} */
    IDropdownSettings.prototype.classes;
}
export class ListItem {
    /**
     * @param {?} source
     */
    constructor(source) {
        if (typeof source === 'string') {
            this.id = this.text = source;
        }
        if (typeof source === 'object') {
            this.id = source.id;
            this.text = source.text;
        }
    }
}
function ListItem_tsickle_Closure_declarations() {
    /** @type {?} */
    ListItem.prototype.id;
    /** @type {?} */
    ListItem.prototype.text;
}

//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoibXVsdGlzZWxlY3QubW9kZWwuanMiLCJzb3VyY2VSb290Ijoibmc6Ly9uZy1tdWx0aXNlbGVjdC1kcm9wZG93bi8iLCJzb3VyY2VzIjpbIm11bHRpc2VsZWN0Lm1vZGVsLnRzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiI7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7OztBQW1CQSxNQUFNOzs7O2dCQUllLE1BQVc7UUFDNUIsRUFBRSxDQUFDLENBQUMsT0FBTyxNQUFNLEtBQUssUUFBUSxDQUFDLENBQUMsQ0FBQztZQUMvQixJQUFJLENBQUMsRUFBRSxHQUFHLElBQUksQ0FBQyxJQUFJLEdBQUcsTUFBTSxDQUFDO1NBQzlCO1FBQ0QsRUFBRSxDQUFDLENBQUMsT0FBTyxNQUFNLEtBQUssUUFBUSxDQUFDLENBQUMsQ0FBQztZQUMvQixJQUFJLENBQUMsRUFBRSxHQUFHLE1BQU0sQ0FBQyxFQUFFLENBQUM7WUFDcEIsSUFBSSxDQUFDLElBQUksR0FBRyxNQUFNLENBQUMsSUFBSSxDQUFDO1NBQ3pCOztDQUVKIiwic291cmNlc0NvbnRlbnQiOlsiZXhwb3J0IGludGVyZmFjZSBJRHJvcGRvd25TZXR0aW5ncyB7XHJcbiAgc2luZ2xlU2VsZWN0aW9uPzogYm9vbGVhbjtcclxuICBpZEZpZWxkPzogc3RyaW5nO1xyXG4gIHRleHRGaWVsZD86IHN0cmluZztcclxuICBlbmFibGVDaGVja0FsbD86IGJvb2xlYW47XHJcbiAgc2VsZWN0QWxsVGV4dD86IHN0cmluZztcclxuICB1blNlbGVjdEFsbFRleHQ/OiBzdHJpbmc7XHJcbiAgYWxsb3dTZWFyY2hGaWx0ZXI/OiBib29sZWFuO1xyXG4gIGNsZWFyU2VhcmNoRmlsdGVyPzogYm9vbGVhbjtcclxuICBtYXhIZWlnaHQ/OiBudW1iZXI7XHJcbiAgaXRlbXNTaG93TGltaXQ/OiBudW1iZXI7XHJcbiAgbGltaXRTZWxlY3Rpb24/OiBudW1iZXI7XHJcbiAgc2VhcmNoUGxhY2Vob2xkZXJUZXh0Pzogc3RyaW5nO1xyXG4gIG5vRGF0YUF2YWlsYWJsZVBsYWNlaG9sZGVyVGV4dD86IHN0cmluZztcclxuICBjbG9zZURyb3BEb3duT25TZWxlY3Rpb24/OiBib29sZWFuO1xyXG4gIHNob3dTZWxlY3RlZEl0ZW1zQXRUb3A/OiBib29sZWFuO1xyXG4gIGNsYXNzZXM/OiBzdHJpbmc7XHJcbn1cclxuXHJcbmV4cG9ydCBjbGFzcyBMaXN0SXRlbSB7XHJcbiAgaWQ6IFN0cmluZztcclxuICB0ZXh0OiBTdHJpbmc7XHJcblxyXG4gIHB1YmxpYyBjb25zdHJ1Y3Rvcihzb3VyY2U6IGFueSkge1xyXG4gICAgaWYgKHR5cGVvZiBzb3VyY2UgPT09ICdzdHJpbmcnKSB7XHJcbiAgICAgIHRoaXMuaWQgPSB0aGlzLnRleHQgPSBzb3VyY2U7XHJcbiAgICB9XHJcbiAgICBpZiAodHlwZW9mIHNvdXJjZSA9PT0gJ29iamVjdCcpIHtcclxuICAgICAgdGhpcy5pZCA9IHNvdXJjZS5pZDtcclxuICAgICAgdGhpcy50ZXh0ID0gc291cmNlLnRleHQ7XHJcbiAgICB9XHJcbiAgfVxyXG59Il19