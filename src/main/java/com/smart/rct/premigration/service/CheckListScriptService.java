package com.smart.rct.premigration.service;

import java.util.List;
import java.util.Set;

import com.smart.rct.exception.RctException;
import com.smart.rct.premigration.entity.CheckListScriptDetEntity;
import com.smart.rct.premigration.models.CheckListScriptDetModel;

public interface CheckListScriptService {

	public List<CheckListScriptDetEntity> getCheckListBasedScriptExecutionDetails(CheckListScriptDetModel checkListScriptDetModel);
	public boolean isDuplicateSeqExist(CheckListScriptDetModel checkListScriptDetModel);
	public boolean isDuplicateScriptNameExist(CheckListScriptDetModel checkListScriptDetModel);
	public CheckListScriptDetEntity saveCheckListBasedScriptExecutionDetails(CheckListScriptDetEntity checkListScriptDetEntity) throws RctException;
	public boolean deleteCheckListBasedScriptExecutionDetails(Integer  checkListScriptDetId);
	boolean deleteCheckListBasedScriptExecutionDetails(CheckListScriptDetModel checkListScriptDetModel,Set<Integer> idList);
}
