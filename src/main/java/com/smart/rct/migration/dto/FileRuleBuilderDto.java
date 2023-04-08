package com.smart.rct.migration.dto;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.smart.rct.migration.entity.FileRuleBuilderEntity;
import com.smart.rct.migration.model.FileRuleBuilderModel;
import com.smart.rct.migration.repository.FileRuleBuilderRepository;

@Component
public class FileRuleBuilderDto {
	final static Logger logger = LoggerFactory.getLogger(FileRuleBuilderDto.class);

	/**
	 * 
	 * @param fileRuleBuilderModel
	 * @return
	 */

	/*
	 * @Autowired FileRuleBuilderRepository fileRuleBuilderRepository;
	 */

	@Autowired
	FileRuleBuilderRepository fileRuleBuilderRepository;

	public FileRuleBuilderEntity getFileRuleBuilderEntity(FileRuleBuilderModel fileRuleBuilderModel) {

		FileRuleBuilderEntity fileRuleBuilderEntity = null;
		try {
			if (fileRuleBuilderModel != null) {
				fileRuleBuilderEntity = new FileRuleBuilderEntity();
				if (fileRuleBuilderModel.getId() != null && fileRuleBuilderModel.getId() != 0) {
					fileRuleBuilderEntity.setId(Integer.valueOf(fileRuleBuilderModel.getId()));
				}
				fileRuleBuilderEntity.setRuleName(fileRuleBuilderModel.getRuleName());
				fileRuleBuilderEntity.setSearchParameter(fileRuleBuilderModel.getSearchParameter());
				fileRuleBuilderEntity.setFileName(fileRuleBuilderModel.getFileName());
				fileRuleBuilderEntity.setStatus(fileRuleBuilderModel.getStatus());
			}
		} catch (Exception e) {
			logger.error("Excpetion getFileRuleBuilderEntity : " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return fileRuleBuilderEntity;
	}

}
