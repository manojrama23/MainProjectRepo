package com.smart.rct.migration.dto;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.smart.rct.migration.entity.CmdRuleBuilderEntity;
import com.smart.rct.migration.model.CmdRuleBuilderModel;
@Component
public class CmdRuleBuilderDetailsDto {
	
	final static Logger logger = LoggerFactory.getLogger(CmdRuleBuilderDetailsDto.class);
	
	public CmdRuleBuilderEntity getCmdRuleBuilderDetailsEntity(CmdRuleBuilderModel cmdRuleBuilderModel)
	{

		CmdRuleBuilderEntity cmdRuleBuilderEntity = null;
		try
		{
			if(cmdRuleBuilderModel != null)
			{
				cmdRuleBuilderEntity = new CmdRuleBuilderEntity();
				if(cmdRuleBuilderModel.getId() != null && cmdRuleBuilderModel.getId() != 0)
				{
					cmdRuleBuilderEntity.setId(Integer.valueOf(cmdRuleBuilderModel.getId()));
				}
				cmdRuleBuilderEntity.setRuleName(cmdRuleBuilderModel.getRuleName());
				cmdRuleBuilderEntity.setCmdName(cmdRuleBuilderModel.getCmdName());
				cmdRuleBuilderEntity.setOperand1Values(cmdRuleBuilderModel.getOperand1Values());
				cmdRuleBuilderEntity.setOperand1ColumnNames(cmdRuleBuilderModel.getOperand1ColumnNames());
				cmdRuleBuilderEntity.setRemarks(cmdRuleBuilderModel.getRemarks());
				//cmdRuleBuilderEntity.setCommandDescription(cmdRuleBuilderModel.getRemarks());
				cmdRuleBuilderEntity.setOperator(cmdRuleBuilderModel.getOperator());
				cmdRuleBuilderEntity.setOperand2Values(cmdRuleBuilderModel.getOperand2Values());
				cmdRuleBuilderEntity.setOperand2ColumnNames(cmdRuleBuilderModel.getOperand2ColumnNames());
				cmdRuleBuilderEntity.setStatus(cmdRuleBuilderModel.getStatus());
				cmdRuleBuilderEntity.setPrompt(cmdRuleBuilderModel.getPrompt());
				cmdRuleBuilderEntity.setLoopType(cmdRuleBuilderModel.getLoopType());
			}
		}
		catch(Exception e)
		{
			logger.error("Excpetion getCmdRuleDetailsEntity : " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return cmdRuleBuilderEntity;
	}

}
