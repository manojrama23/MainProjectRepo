package com.smart.rct.common.service;

import java.util.List;

import com.smart.rct.common.entity.NeVersionEntity;
import com.smart.rct.common.models.NeVersionModel;
import com.smart.rct.exception.RctException;

public interface NeVersionService {

	public boolean createNeVersion(NeVersionEntity neEntity);

	public List<NeVersionEntity> getNeVersionList(NeVersionModel neVersionModel);

	public boolean deleteNeVersionDetails(int neVersionDetailId) throws RctException;

	public boolean duplicateNeVersion(NeVersionModel neVersionModel);

	public NeVersionEntity getNeVersionById(int neVersionDetailId);

}
