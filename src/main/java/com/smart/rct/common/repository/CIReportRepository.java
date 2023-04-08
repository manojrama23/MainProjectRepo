package com.smart.rct.common.repository;

import java.util.List;

import com.smart.rct.postmigration.entity.SchedulingSprintEntity;
import com.smart.rct.postmigration.entity.SchedulingVerizonEntity;

public interface CIReportRepository {

	List<SchedulingVerizonEntity> getSchedulingVerizonEntityListForCIReports();

	List<SchedulingSprintEntity> getSchedulingSprintEntityListForCIReports();

	List<SchedulingVerizonEntity> getSchedVerListCurrentWeekForCIReports();

	List<SchedulingVerizonEntity> getSchedVerListCurrentWeekForVlsmLsmCIReports();

	List<SchedulingSprintEntity> getSchedsprListCurrentWeekForCIReports();

	List<SchedulingVerizonEntity> getSchedVerListForCIReportsPeriod(String fromDate, String toDate);

	List<SchedulingVerizonEntity> getSchedVerListPeriodForVlsmLsmCIReports(String fromDate, String toDate);

	List<SchedulingSprintEntity> getSchedsprListPeriodForCIReports(String fromDate, String toDate);

	List<SchedulingSprintEntity> getSchedsprListDayWiseForCIReports(String date);

	List<SchedulingVerizonEntity> getSchedVerListForCIReportsDayWise(String date);

	List<SchedulingVerizonEntity> getSchedVerListVlsmLsmCIReportsDailyWise(String date);
	
	List<SchedulingVerizonEntity> getSchedulingVerizonEntityListForMonthly();
	
	List<SchedulingSprintEntity> getSchedulingSprintEntityListForCIMonthly();
	
	List<SchedulingVerizonEntity> getSchedulingVerizonEntityListForCIReportsFriday();
	
	List<SchedulingSprintEntity> getSchedulingSprintEntityListForCIReportsMonday() ;
	
	List<SchedulingSprintEntity> getSchedsprListCurrentWeekForCIReportsMonday();
	
	List<SchedulingSprintEntity> getSchedsprListPeriodForCIReportsMonday(String fromDate,String toDate);

}
