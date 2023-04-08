package com.smart.rct.migration.service;



import org.apache.poi.xssf.usermodel.XSSFSheet;


public interface RetService {

	public boolean saveRETform(XSSFSheet sheet, String fileN, Integer programId, String userName, String enbId,
			int uniqueId);

}
