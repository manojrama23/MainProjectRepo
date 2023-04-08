package com.smart.rct.common.repository;

import java.util.List;

import com.smart.rct.common.entity.NeVersionEntity;
import com.smart.rct.common.models.NeVersionModel;

public interface NeVersionRepository {

	public boolean createNeVersion(NeVersionEntity neEntity);

	public List<NeVersionEntity> getNeVersionList(NeVersionModel neVersionModel);

	public boolean deleteNeVersionDetails(int neVersionDetailId);

	public NeVersionEntity getNeVersionById(int neVersionDetailId);

	boolean duplicateNeVersion(NeVersionModel neVersionModel);

}
