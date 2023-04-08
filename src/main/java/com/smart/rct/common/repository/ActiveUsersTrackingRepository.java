package com.smart.rct.common.repository;

import java.util.List;
import java.util.Map;

import com.smart.rct.common.entity.ActiveUsersTracking;
import com.smart.rct.common.entity.AuditTrailEntity;
import com.smart.rct.common.models.AuditTrailModel;

public interface ActiveUsersTrackingRepository {

	boolean savedetail(ActiveUsersTracking activeUserTracking);

	Map<String, Object> getActiveUsers(int page, int count);



}
