package com.smart.rct.common.models;

import java.util.List;

public class EnbTemplateModel {
	
	private String menuName;
	private String sheetAliasName;
	private String subSheetAliasName;
	private List<String> subMenu;
	
	public String getMenuName() {
		return menuName;
	}
	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}
	public String getSheetAliasName() {
		return sheetAliasName;
	}
	public void setSheetAliasName(String sheetAliasName) {
		this.sheetAliasName = sheetAliasName;
	}
	public String getSubSheetAliasName() {
		return subSheetAliasName;
	}
	public void setSubSheetAliasName(String subSheetAliasName) {
		this.subSheetAliasName = subSheetAliasName;
	}
	public List<String> getSubMenu() {
		return subMenu;
	}
	public void setSubMenu(List<String> subMenu) {
		this.subMenu = subMenu;
	}
	
	
	

}
