package com.smart.rct.migration.dto;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.smart.rct.common.entity.NetworkTypeDetailsEntity;
import com.smart.rct.common.repository.LsmRepository;
import com.smart.rct.common.repository.NetworkTypeDetailsRepository;
import com.smart.rct.migration.entity.UploadFileEntity;
import com.smart.rct.migration.model.UploadFileModel;
import com.smart.rct.util.PasswordCrypt;

@Component
public class UploadFileDto {
	final static Logger logger = LoggerFactory.getLogger(UploadFileDto.class);
	
	/**
	 * 
	 * @param fileRuleBuilderModel
	 * @return
	 */
	@Autowired
	NetworkTypeDetailsRepository networkTypeDetailsRepository;
	@Autowired
	LsmRepository lsmRepository;
	
	public UploadFileEntity getFileRuleBuilderEntity(UploadFileModel uploadFileModel) {

		UploadFileEntity uploadFileEntity = null;
		try {
			if (uploadFileModel != null) {
				uploadFileEntity = new UploadFileEntity();
				if (uploadFileModel.getId() != null && uploadFileModel.getId() != 0) {
					uploadFileEntity.setId(Integer.valueOf(uploadFileModel.getId()));
				}
				uploadFileEntity.setUseCount(uploadFileModel.getUseCount());
				uploadFileEntity.setFileName(uploadFileModel.getFileName());
				uploadFileEntity.setFilePath(uploadFileModel.getFilePath());
				uploadFileEntity.setRemarks(uploadFileModel.getRemarks());
				uploadFileEntity.setUploadedBy(uploadFileModel.getUploadedBy());
				uploadFileEntity.setId(uploadFileModel.getId());
				uploadFileEntity.setCustomerId(uploadFileModel.getCustomerId());
				uploadFileEntity.setMigrationType(uploadFileModel.getMigrationType());
				uploadFileEntity.setProgram(uploadFileModel.getProgram());
				uploadFileEntity.setState(uploadFileModel.getState());
				uploadFileEntity.setSubType(uploadFileModel.getSubType());
				
				uploadFileEntity.setNeListEntity(uploadFileModel.getNeList());
				uploadFileEntity.setCustomerDetailsEntity(uploadFileModel.getCustomerDetailsEntity());
				uploadFileEntity.setNeVersion(uploadFileModel.getNeVersion());
				//NetworkTypeDetailsEntity objNetworkTypeDetailsEntity=new NetworkTypeDetailsEntity();
				//objNetworkTypeDetailsEntity.setId(uploadFileModel.getNwTypeDetailsId());
				//NetworkTypeDetailsEntity ue = networkTypeDetailsRepository.getNetworkTypeByName(uploadFileModel.getNwType());
				
				uploadFileEntity.setScriptType(uploadFileModel.getScriptType());
				uploadFileEntity.setConnectionLocation(uploadFileModel.getConnectionLocation());
				uploadFileEntity.setConnectionLocationUserName(uploadFileModel.getConnectionLocationUserName());
				
				if(uploadFileModel.getConnectionLocationPwd()!=null && !uploadFileModel.getConnectionLocationPwd().trim().isEmpty()) {
					uploadFileEntity.setConnectionLocationPwd(PasswordCrypt.encrypt(uploadFileModel.getConnectionLocationPwd()));
				}else {
					uploadFileEntity.setConnectionLocationPwd("");
				}
				uploadFileEntity.setConnectionTerminal(uploadFileModel.getConnectionTerminal());
				uploadFileEntity.setConnectionTerminalUserName(uploadFileModel.getConnectionTerminalUserName());
				
				if(uploadFileModel.getConnectionTerminalPwd()!=null && !uploadFileModel.getConnectionTerminalPwd().trim().isEmpty()) {
					uploadFileEntity.setConnectionTerminalPwd(PasswordCrypt.encrypt(uploadFileModel.getConnectionTerminalPwd()));
				}else {
					uploadFileEntity.setConnectionTerminalPwd("");
				}
				if(uploadFileModel.getSudoPassword()!=null && !uploadFileModel.getSudoPassword().trim().isEmpty()) {
					uploadFileEntity.setSudoPassword(PasswordCrypt.encrypt(uploadFileModel.getSudoPassword()));
				}else {
					uploadFileEntity.setSudoPassword("");
				}
				
				uploadFileEntity.setPrompt(uploadFileModel.getPrompt());
				uploadFileEntity.setArguments(uploadFileModel.getArguments());
				
				//uploadFileEntity.setNetworkTypeDetailsEntity(ue);
				
				//uploadFileEntity.setLsmEntity(lsmRepository.getLsmEntity(uploadFileModel.getLsmVersion(),uploadFileModel.getLsmName(),ue.getId()));
			
			}
		} catch (Exception e) {
			logger.error("Excpetion getFileRuleBuilderEntity : " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return uploadFileEntity;
	}

}
