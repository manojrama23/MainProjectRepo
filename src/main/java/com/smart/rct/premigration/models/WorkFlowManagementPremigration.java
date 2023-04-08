package com.smart.rct.premigration.models;

import java.util.ArrayList;
import java.util.List;

public class WorkFlowManagementPremigration {
	
    private List<String>  usecases = new ArrayList();

	private String fileName;
	 
	private String filePath;

	

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	

	   public List<String> getUsecases() {
		return usecases;
	}

	public void setUsecases(List<String> usecases) {
		this.usecases = usecases;
	}

}
