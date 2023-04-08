package com.smart.rct.common.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smart.rct.common.entity.DuoGeneralConfigEntity;
import com.smart.rct.common.models.ProgramTemplateModel;
import com.smart.rct.common.repository.DuoGeneralConfigRepository;
import com.smart.rct.common.service.DuoGeneralConfigService;
import com.smart.rct.constants.Constants;

@Service
public class DuoGeneralConfigServiceImpl implements DuoGeneralConfigService{
	
	final static Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

	@Autowired
	DuoGeneralConfigRepository duoGeneralConfigRepository;

	@Override
	public List<ProgramTemplateModel> getDuoGeneralConfigList() {
		List<DuoGeneralConfigEntity> objList = null;
		List<ProgramTemplateModel> configList = new ArrayList<>();
		try {
			objList = duoGeneralConfigRepository.getDuoGeneralConfigList();
			if (objList != null && !objList.isEmpty()) {
				ProgramTemplateModel configDetailModel;
				for (DuoGeneralConfigEntity templateEntity : objList) {
					configDetailModel = new ProgramTemplateModel();
					configDetailModel.setId(templateEntity.getId());
					configDetailModel.setLabel(templateEntity.getLabel());
					configDetailModel.setValue(templateEntity.getValue());
					configDetailModel.setType("GENERAL");
					configDetailModel.setConfigType(Constants.CONFIG_TYPE_GENERAL);
					configList.add(configDetailModel);
				}
			}
		} catch (Exception e) {
			logger.error("Exception in CustomerServiceImpl.getDuoGeneralConfigList(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return configList;
	}
	
	@Override
	public boolean saveDuoGeneralConfigEntity(DuoGeneralConfigEntity duoGeneralConfigEntity) {
		boolean status = false;
		try {
			status = duoGeneralConfigRepository.saveDuoGeneralConfigEntity(duoGeneralConfigEntity);
		} catch (Exception e) {
			status = false;
			// TODO: handle exception
			logger.error("Exception in CustomerServiceImpl.saveDuoGeneralConfigEntity(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}
}
