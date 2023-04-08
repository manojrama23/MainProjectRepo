package com.smart.rct.common.service;

import java.util.List;
import java.util.Map;

import com.smart.rct.common.entity.CustomerEntity;

public interface CIReportService {

	Map<String, Object> getSchedulingVerizonEntityListForCIReports();

	Map<String, Object> getSchedulingSprintEntityListForCIReports();

	Map<String, Object> getSchedulingDashBoardCIReports(List<CustomerEntity> customerEntities);

	Map<String, Object> getSchedulingVerizonEntityListForCIReportsPeriod(String fromDate, String toDate);

	Map<String, Object> getSchedulingSprintEntityListForCIReportsPeriod(String fromDate, String toDate);

	Map<String, Object> getSchedulingSprintEntityListForCIReportsDailyWise(String date);

	Map<String, Object> getSchedulingVerizonEntityListForCIReportsDailyWise(String selctionDate);

	Map<String, Object> getSchedulingDashBoardMarketCIReports(List<CustomerEntity> customerEntities);

	Map<String, Object> getSchedulingDashBoardMonthly(List<CustomerEntity> customerEntities);

	Map<String, Object> getSchedulingVerizonEntityListForCIReportsFriday();

	Map<String, Object> getSchedulingVerizonEntityListForCIReportsPeriodFriday(String fromDate, String toDate);

	Map<String, Object> getSchedulingSprintEntityListForCIReportsMonday();

	Map<String, Object> getSchedulingSprintEntityListForCIReportsMondayPeriod(String fromDate, String toDate);
}
