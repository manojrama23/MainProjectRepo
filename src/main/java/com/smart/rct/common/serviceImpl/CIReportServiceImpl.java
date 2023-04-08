package com.smart.rct.common.serviceImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.smart.rct.common.entity.CustomerEntity;
import com.smart.rct.common.models.CIReportModelSprintNew;
import com.smart.rct.common.models.MarketModel;
import com.smart.rct.common.models.NeCommissionModel;
import com.smart.rct.common.models.SprintDailyModel;
import com.smart.rct.common.models.TotalCIReportModel;
import com.smart.rct.common.models.VlsmAndLsmReportModel;
import com.smart.rct.common.repository.CIReportRepository;
import com.smart.rct.common.service.CIReportService;
import com.smart.rct.constants.Constants;
import com.smart.rct.postmigration.entity.SchedulingSprintEntity;
import com.smart.rct.postmigration.entity.SchedulingVerizonEntity;

@Service
@Transactional
public class CIReportServiceImpl implements CIReportService {

	final static Logger logger = LoggerFactory.getLogger(CIReportServiceImpl.class);

	@Autowired
	CIReportRepository objCIReportRepository;

	/**
	 * This api will getSchedulingVerizonEntityListForCIReports
	 * 
	 * @return Map<String, Object>
	 */
	@Override
	public Map<String, Object> getSchedulingVerizonEntityListForCIReports() {

		Map<String, Object> objHashMapResult = new LinkedHashMap<>();
		TotalCIReportModel objTotalCIReportModel = new TotalCIReportModel();
		TotalCIReportModel objWeekTotalCIReportModel = new TotalCIReportModel();
		try {
			List<SchedulingVerizonEntity> objCountsList = objCIReportRepository
					.getSchedulingVerizonEntityListForCIReports();
			if (objCountsList != null && objCountsList.size() > 0) {
				long totalSites = 6400;
				long newAllMigratedsitesCount = objCountsList.stream()
						.filter(x -> ("Migrated".equalsIgnoreCase(x.getStatus()))
								&& ("New England".equalsIgnoreCase(x.getMarket())
										|| "Upstate New York".equalsIgnoreCase(x.getMarket())))
						.count();
				float percentageTotmigrated = (float) ((Double.valueOf(newAllMigratedsitesCount) * 100) / totalSites);
				objTotalCIReportModel.setName("Overall Sites Migrated");
				objTotalCIReportModel.setMigrtedSiteCount(newAllMigratedsitesCount);
				objTotalCIReportModel.setTotalCount(totalSites);
				objTotalCIReportModel.setPercenatgeOfMigrated(percentageTotmigrated);
				Set<String> marketNames = objCountsList.stream().map(x -> x.getMarket()).collect(Collectors.toSet());
				List<MarketModel> objListModel = new ArrayList<>();
				if (marketNames != null && marketNames.size() > 0) {
					for (String marketName : marketNames) {
						if ("New England".equalsIgnoreCase(marketName)) {
							MarketModel objMarketModel = new MarketModel();
							long totalEnglandSites = 4000;
							long newEnglandMigratedsitesCount = objCountsList.stream()
									.filter(x -> (marketName.equalsIgnoreCase(x.getMarket())
											&& "Migrated".equalsIgnoreCase(x.getStatus())))
									.count();
							float percentagemigratedEngland = (float) ((Double.valueOf(newEnglandMigratedsitesCount)
									* 100) / totalEnglandSites);
							objMarketModel.setMarketName(marketName);
							objMarketModel.setMigrtedSiteCount(newEnglandMigratedsitesCount);
							objMarketModel.setTotalCount(totalEnglandSites);
							objMarketModel.setPercenatgeOfMigrated(percentagemigratedEngland);

							objListModel.add(objMarketModel);
						}
						if ("Upstate New York".equalsIgnoreCase(marketName)) {
							MarketModel objMarketModel = new MarketModel();
							long totalUNYSites = 2400;

							long newUNYMigratedsitesCount = objCountsList.stream()
									.filter(x -> (marketName.equalsIgnoreCase(x.getMarket())
											&& "Migrated".equalsIgnoreCase(x.getStatus())))
									.count();
							float percentagemigratedUNY = (float) ((Double.valueOf(newUNYMigratedsitesCount) * 100)
									/ totalUNYSites);
							objMarketModel.setMarketName(marketName);
							objMarketModel.setMigrtedSiteCount(newUNYMigratedsitesCount);
							objMarketModel.setTotalCount(totalUNYSites);
							objMarketModel.setPercenatgeOfMigrated(percentagemigratedUNY);
							objListModel.add(objMarketModel);
						}
					}
					objTotalCIReportModel.setObjSiteList(objListModel);
				}
			}
			// current Week Migrated Data
			List<SchedulingVerizonEntity> objCurrentWeekList = objCIReportRepository
					.getSchedVerListCurrentWeekForCIReports();
			if (objCurrentWeekList != null && objCurrentWeekList.size() > 0) {

				long newAllWeekMigratedsitesCount = objCurrentWeekList.stream()
						.filter(x -> ("Migrated".equalsIgnoreCase(x.getStatus()))
								&& ("New England".equalsIgnoreCase(x.getMarket())
										|| "Upstate New York".equalsIgnoreCase(x.getMarket())))
						.count();
				objWeekTotalCIReportModel.setName(" Total Sites Migrated");
				objWeekTotalCIReportModel.setMigrtedSiteCount(newAllWeekMigratedsitesCount);
				Set<String> marketNamesWeek = objCurrentWeekList.stream().map(x -> x.getMarket())
						.collect(Collectors.toSet());
				List<MarketModel> objListModelWeek = new ArrayList<>();
				if (marketNamesWeek != null && marketNamesWeek.size() > 0) {
					for (String marketName : marketNamesWeek) {
						if ("New England".equalsIgnoreCase(marketName)) {
							MarketModel objMarketModel = new MarketModel();
							long newEnglandMigratedsitesCount = objCurrentWeekList.stream()
									.filter(x -> (marketName.equalsIgnoreCase(x.getMarket())
											&& "Migrated".equalsIgnoreCase(x.getStatus())))
									.count();
							objMarketModel.setMarketName(marketName);
							objMarketModel.setMigrtedSiteCount(newEnglandMigratedsitesCount);
							objListModelWeek.add(objMarketModel);
						}
						if ("Upstate New York".equalsIgnoreCase(marketName)) {
							MarketModel objMarketModel = new MarketModel();

							long newUNYMigratedsitesCount = objCurrentWeekList.stream()
									.filter(x -> (marketName.equalsIgnoreCase(x.getMarket())
											&& "Migrated".equalsIgnoreCase(x.getStatus())))
									.count();
							objMarketModel.setMarketName(marketName);
							objMarketModel.setMigrtedSiteCount(newUNYMigratedsitesCount);
							objListModelWeek.add(objMarketModel);
						}
					}
					objWeekTotalCIReportModel.setObjSiteList(objListModelWeek);
				}
			}
			// vlsm and lsm status
			long rehomeCount = 1193;
			long lsmCountTot = objCountsList.stream()
					.filter(x -> (("NO".equalsIgnoreCase(x.getVlsm()) || StringUtils.isEmpty(x.getVlsm()))
							&& "Migrated".equalsIgnoreCase(x.getStatus())))
					.count();
			long directvlsmCountTot = objCountsList.stream()
					.filter(x -> ("YES".equalsIgnoreCase(x.getVlsm()) && "Migrated".equalsIgnoreCase(x.getStatus())))
					.count();
			long totVlsmCount = rehomeCount + directvlsmCountTot;
			long totlsmCount = lsmCountTot - rehomeCount;
			// current Week Vlsm Lsm Data
			List<SchedulingVerizonEntity> objCurrentWeekVlsmList = objCIReportRepository
					.getSchedVerListCurrentWeekForVlsmLsmCIReports();
			long lsmMigCountcurrentWeek = objCurrentWeekVlsmList.stream()
					.filter(x -> (("NO".equalsIgnoreCase(x.getVlsm()) || StringUtils.isEmpty(x.getVlsm()))
							&& "Migrated".equalsIgnoreCase(x.getStatus())))
					.count();
			long vlsmMigCountcurrentWeek = objCurrentWeekVlsmList.stream()
					.filter(x -> ("YES".equalsIgnoreCase(x.getVlsm()) && "Migrated".equalsIgnoreCase(x.getStatus())))
					.count();
			long lsmRollBackCountcurrentWeek = objCurrentWeekVlsmList.stream()
					.filter(x -> (("NO".equalsIgnoreCase(x.getVlsm()) || StringUtils.isEmpty(x.getVlsm()))
							&& "Rolled Back".equalsIgnoreCase(x.getStatus())))
					.count();
			long vlsmRollBackCountcurrentWeek = objCurrentWeekVlsmList.stream()
					.filter(x -> ("YES".equalsIgnoreCase(x.getVlsm()) && "Rolled Back".equalsIgnoreCase(x.getStatus())))
					.count();
			VlsmAndLsmReportModel objVlsmAndLsmReportModel = new VlsmAndLsmReportModel();
			objVlsmAndLsmReportModel.setTotSiteLsm(totlsmCount);
			objVlsmAndLsmReportModel.setTotSiteVlsm(totVlsmCount);
			objVlsmAndLsmReportModel.setLsmMigCurrentWeek(lsmMigCountcurrentWeek);
			objVlsmAndLsmReportModel.setVlsmMigCurrentWeek(vlsmMigCountcurrentWeek);
			objVlsmAndLsmReportModel.setLsmRollBackCurrentWeek(lsmRollBackCountcurrentWeek);
			objVlsmAndLsmReportModel.setVlsmRollBackCurrentWeek(vlsmRollBackCountcurrentWeek);
			objHashMapResult.put("overAllData", objTotalCIReportModel);
			objHashMapResult.put("currentWeekData", objWeekTotalCIReportModel);
			objHashMapResult.put("vlsmandLsmDetails", objVlsmAndLsmReportModel);
		} catch (Exception e) {
			logger.error("Exception in CIReportServiceImpl.getSchedulingVerizonEntityListForCIReports(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return objHashMapResult;
	}

	/**
	 * This api will getSchedulingVerizonEntityListForCIReportsPeriod
	 * 
	 * @param fromDate,toDate
	 * @return Map<String, Object>
	 */
	@Override
	public Map<String, Object> getSchedulingVerizonEntityListForCIReportsPeriod(String fromDate, String toDate) {

		Map<String, Object> objHashMapResult = new LinkedHashMap<>();
		TotalCIReportModel objTotalCIReportModel = new TotalCIReportModel();
		TotalCIReportModel objWeekTotalCIReportModel = new TotalCIReportModel();
		try {
			List<SchedulingVerizonEntity> objCountsList = objCIReportRepository
					.getSchedulingVerizonEntityListForCIReports();
			if (objCountsList != null && objCountsList.size() > 0) {
				long totalSites = 6400;
				long newAllMigratedsitesCount = objCountsList.stream()
						.filter(x -> ("Migrated".equalsIgnoreCase(x.getStatus()))
								&& ("New England".equalsIgnoreCase(x.getMarket())
										|| "Upstate New York".equalsIgnoreCase(x.getMarket())))
						.count();
				float percentageTotmigrated = (float) ((Double.valueOf(newAllMigratedsitesCount) * 100) / totalSites);
				objTotalCIReportModel.setName("Overall Sites Migrated");
				objTotalCIReportModel.setMigrtedSiteCount(newAllMigratedsitesCount);
				objTotalCIReportModel.setTotalCount(totalSites);
				objTotalCIReportModel.setPercenatgeOfMigrated(percentageTotmigrated);
				Set<String> marketNames = objCountsList.stream().map(x -> x.getMarket()).collect(Collectors.toSet());
				List<MarketModel> objListModel = new ArrayList<>();
				if (marketNames != null && marketNames.size() > 0) {
					for (String marketName : marketNames) {
						if ("New England".equalsIgnoreCase(marketName)) {
							MarketModel objMarketModel = new MarketModel();
							long totalEnglandSites = 4000;
							long newEnglandMigratedsitesCount = objCountsList.stream()
									.filter(x -> (marketName.equalsIgnoreCase(x.getMarket())
											&& "Migrated".equalsIgnoreCase(x.getStatus())))
									.count();
							float percentagemigratedEngland = (float) ((Double.valueOf(newEnglandMigratedsitesCount)
									* 100) / totalEnglandSites);
							objMarketModel.setMarketName(marketName);
							objMarketModel.setMigrtedSiteCount(newEnglandMigratedsitesCount);
							objMarketModel.setTotalCount(totalEnglandSites);
							objMarketModel.setPercenatgeOfMigrated(percentagemigratedEngland);
							objListModel.add(objMarketModel);
						}
						if ("Upstate New York".equalsIgnoreCase(marketName)) {
							MarketModel objMarketModel = new MarketModel();
							long totalUNYSites = 2400;

							long newUNYMigratedsitesCount = objCountsList.stream()
									.filter(x -> (marketName.equalsIgnoreCase(x.getMarket())
											&& "Migrated".equalsIgnoreCase(x.getStatus())))
									.count();
							float percentagemigratedUNY = (float) ((Double.valueOf(newUNYMigratedsitesCount) * 100)
									/ totalUNYSites);
							objMarketModel.setMarketName(marketName);
							objMarketModel.setMigrtedSiteCount(newUNYMigratedsitesCount);
							objMarketModel.setTotalCount(totalUNYSites);
							objMarketModel.setPercenatgeOfMigrated(percentagemigratedUNY);
							objListModel.add(objMarketModel);
						}
					}
					objTotalCIReportModel.setObjSiteList(objListModel);
				}
			}
			// current range Migrated Data
			List<SchedulingVerizonEntity> objCurrentWeekList = objCIReportRepository
					.getSchedVerListForCIReportsPeriod(fromDate, toDate);
			if (objCurrentWeekList != null && objCurrentWeekList.size() > 0) {
				long newAllWeekMigratedsitesCount = objCurrentWeekList.stream()
						.filter(x -> ("Migrated".equalsIgnoreCase(x.getStatus()))
								&& ("New England".equalsIgnoreCase(x.getMarket())
										|| "Upstate New York".equalsIgnoreCase(x.getMarket())))
						.count();
				objWeekTotalCIReportModel.setName(" Total Sites Migrated");
				objWeekTotalCIReportModel.setMigrtedSiteCount(newAllWeekMigratedsitesCount);
				Set<String> marketNamesWeek = objCurrentWeekList.stream().map(x -> x.getMarket())
						.collect(Collectors.toSet());
				List<MarketModel> objListModelWeek = new ArrayList<>();
				if (marketNamesWeek != null && marketNamesWeek.size() > 0) {
					for (String marketName : marketNamesWeek) {
						if ("New England".equalsIgnoreCase(marketName)) {
							MarketModel objMarketModel = new MarketModel();
							long newEnglandMigratedsitesCount = objCurrentWeekList.stream()
									.filter(x -> (marketName.equalsIgnoreCase(x.getMarket())
											&& "Migrated".equalsIgnoreCase(x.getStatus())))
									.count();
							objMarketModel.setMarketName(marketName);
							objMarketModel.setMigrtedSiteCount(newEnglandMigratedsitesCount);
							objListModelWeek.add(objMarketModel);
						}
						if ("Upstate New York".equalsIgnoreCase(marketName)) {
							MarketModel objMarketModel = new MarketModel();

							long newUNYMigratedsitesCount = objCurrentWeekList.stream()
									.filter(x -> (marketName.equalsIgnoreCase(x.getMarket())
											&& "Migrated".equalsIgnoreCase(x.getStatus())))
									.count();
							objMarketModel.setMarketName(marketName);
							objMarketModel.setMigrtedSiteCount(newUNYMigratedsitesCount);
							objListModelWeek.add(objMarketModel);
						}
					}
					objWeekTotalCIReportModel.setObjSiteList(objListModelWeek);
				}
			}
			// vlsm and lsm status
			long rehomeCount = 1193;
			long lsmCountTot = objCountsList.stream()
					.filter(x -> (("NO".equalsIgnoreCase(x.getVlsm()) || StringUtils.isEmpty(x.getVlsm()))
							&& "Migrated".equalsIgnoreCase(x.getStatus())))
					.count();
			long directvlsmCountTot = objCountsList.stream()
					.filter(x -> ("YES".equalsIgnoreCase(x.getVlsm()) && "Migrated".equalsIgnoreCase(x.getStatus())))
					.count();
			long totVlsmCount = rehomeCount + directvlsmCountTot;
			long totlsmCount = lsmCountTot - rehomeCount;
			// current Week Vlsm Lsm Data
			List<SchedulingVerizonEntity> objCurrentWeekVlsmList = objCIReportRepository
					.getSchedVerListPeriodForVlsmLsmCIReports(fromDate, toDate);
			long lsmMigCountcurrentWeek = objCurrentWeekVlsmList.stream()
					.filter(x -> (("NO".equalsIgnoreCase(x.getVlsm()) || StringUtils.isEmpty(x.getVlsm()))
							&& "Migrated".equalsIgnoreCase(x.getStatus())))
					.count();
			long vlsmMigCountcurrentWeek = objCurrentWeekVlsmList.stream()
					.filter(x -> ("YES".equalsIgnoreCase(x.getVlsm()) && "Migrated".equalsIgnoreCase(x.getStatus())))
					.count();
			long lsmRollBackCountcurrentWeek = objCurrentWeekVlsmList.stream()
					.filter(x -> (("NO".equalsIgnoreCase(x.getVlsm()) || StringUtils.isEmpty(x.getVlsm()))
							&& "Rolled Back".equalsIgnoreCase(x.getStatus())))
					.count();
			long vlsmRollBackCountcurrentWeek = objCurrentWeekVlsmList.stream()
					.filter(x -> ("YES".equalsIgnoreCase(x.getVlsm()) && "Rolled Back".equalsIgnoreCase(x.getStatus())))
					.count();
			VlsmAndLsmReportModel objVlsmAndLsmReportModel = new VlsmAndLsmReportModel();
			objVlsmAndLsmReportModel.setTotSiteLsm(totlsmCount);
			objVlsmAndLsmReportModel.setTotSiteVlsm(totVlsmCount);
			objVlsmAndLsmReportModel.setLsmMigCurrentWeek(lsmMigCountcurrentWeek);
			objVlsmAndLsmReportModel.setVlsmMigCurrentWeek(vlsmMigCountcurrentWeek);
			objVlsmAndLsmReportModel.setLsmRollBackCurrentWeek(lsmRollBackCountcurrentWeek);
			objVlsmAndLsmReportModel.setVlsmRollBackCurrentWeek(vlsmRollBackCountcurrentWeek);
			objHashMapResult.put("overAllData", objTotalCIReportModel);
			objHashMapResult.put("currentWeekData", objWeekTotalCIReportModel);
			objHashMapResult.put("vlsmandLsmDetails", objVlsmAndLsmReportModel);
		} catch (Exception e) {
			logger.error("Exception in CIReportServiceImpl.getSchedulingVerizonEntityListForCIReportsPeriod(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return objHashMapResult;
	}

	/**
	 * This api will getSchedulingVerizonEntityListForCIReportsDailyWise
	 * 
	 * @param selctionDate
	 * @return Map<String, Object>
	 */
	@Override
	public Map<String, Object> getSchedulingVerizonEntityListForCIReportsDailyWise(String selctionDate) {

		Map<String, Object> objHashMapResult = new LinkedHashMap<>();
		TotalCIReportModel objTotalCIReportModel = new TotalCIReportModel();
		TotalCIReportModel objWeekTotalCIReportModel = new TotalCIReportModel();

		try {

			List<SchedulingVerizonEntity> objCountsList = objCIReportRepository
					.getSchedulingVerizonEntityListForCIReports();

			if (objCountsList != null && objCountsList.size() > 0) {
				long totalSites = 6400;
				long newAllMigratedsitesCount = objCountsList.stream()
						.filter(x -> ("Migrated".equalsIgnoreCase(x.getStatus()))
								&& ("New England".equalsIgnoreCase(x.getMarket())
										|| "Upstate New York".equalsIgnoreCase(x.getMarket())))
						.count();
				float percentageTotmigrated = (float) ((Double.valueOf(newAllMigratedsitesCount) * 100) / totalSites);

				objTotalCIReportModel.setName("Overall Sites Migrated");
				objTotalCIReportModel.setMigrtedSiteCount(newAllMigratedsitesCount);
				objTotalCIReportModel.setTotalCount(totalSites);
				objTotalCIReportModel.setPercenatgeOfMigrated(percentageTotmigrated);
				Set<String> marketNames = objCountsList.stream().map(x -> x.getMarket()).collect(Collectors.toSet());
				List<MarketModel> objListModel = new ArrayList<>();

				if (marketNames != null && marketNames.size() > 0) {
					for (String marketName : marketNames) {

						if ("New England".equalsIgnoreCase(marketName)) {
							MarketModel objMarketModel = new MarketModel();

							long totalEnglandSites = 4000;

							long newEnglandMigratedsitesCount = objCountsList.stream()
									.filter(x -> (marketName.equalsIgnoreCase(x.getMarket())
											&& "Migrated".equalsIgnoreCase(x.getStatus())))
									.count();
							float percentagemigratedEngland = (float) ((Double.valueOf(newEnglandMigratedsitesCount)
									* 100) / totalEnglandSites);
							objMarketModel.setMarketName(marketName);
							objMarketModel.setMigrtedSiteCount(newEnglandMigratedsitesCount);
							objMarketModel.setTotalCount(totalEnglandSites);
							objMarketModel.setPercenatgeOfMigrated(percentagemigratedEngland);

							objListModel.add(objMarketModel);

						}

						if ("Upstate New York".equalsIgnoreCase(marketName)) {
							MarketModel objMarketModel = new MarketModel();
							long totalUNYSites = 2400;

							long newUNYMigratedsitesCount = objCountsList.stream()
									.filter(x -> (marketName.equalsIgnoreCase(x.getMarket())
											&& "Migrated".equalsIgnoreCase(x.getStatus())))
									.count();

							float percentagemigratedUNY = (float) ((Double.valueOf(newUNYMigratedsitesCount) * 100)
									/ totalUNYSites);
							objMarketModel.setMarketName(marketName);
							objMarketModel.setMigrtedSiteCount(newUNYMigratedsitesCount);
							objMarketModel.setTotalCount(totalUNYSites);
							objMarketModel.setPercenatgeOfMigrated(percentagemigratedUNY);
							objListModel.add(objMarketModel);

						}
					}

					objTotalCIReportModel.setObjSiteList(objListModel);

				}

			}

			// DayWise Migrated Data
			List<SchedulingVerizonEntity> objCurrentDayList = objCIReportRepository
					.getSchedVerListForCIReportsDayWise(selctionDate);
			List<MarketModel> objListModelWeek = new ArrayList<>();
			if (objCurrentDayList != null && objCurrentDayList.size() > 0) {

				long newAllWeekMigratedsitesCount = objCurrentDayList.stream()
						.filter(x -> ("Migrated".equalsIgnoreCase(x.getStatus()))
								&& ("New England".equalsIgnoreCase(x.getMarket())
										|| "Upstate New York".equalsIgnoreCase(x.getMarket())))
						.count();

				objWeekTotalCIReportModel.setName(" Total Sites Migrated");
				objWeekTotalCIReportModel.setMigrtedSiteCount(newAllWeekMigratedsitesCount);

				Set<String> marketNamesWeek = objCurrentDayList.stream().map(x -> x.getMarket())
						.collect(Collectors.toSet());

				if (marketNamesWeek != null && marketNamesWeek.size() > 0) {
					for (String marketName : marketNamesWeek) {

						if ("New England".equalsIgnoreCase(marketName)) {
							MarketModel objMarketModel = new MarketModel();
							long newEnglandMigratedsitesCount = objCurrentDayList.stream()
									.filter(x -> (marketName.equalsIgnoreCase(x.getMarket())
											&& "Migrated".equalsIgnoreCase(x.getStatus())))
									.count();
							objMarketModel.setMarketName(marketName);
							objMarketModel.setMigrtedSiteCount(newEnglandMigratedsitesCount);
							objListModelWeek.add(objMarketModel);

						}

						if ("Upstate New York".equalsIgnoreCase(marketName)) {
							MarketModel objMarketModel = new MarketModel();

							long newUNYMigratedsitesCount = objCurrentDayList.stream()
									.filter(x -> (marketName.equalsIgnoreCase(x.getMarket())
											&& "Migrated".equalsIgnoreCase(x.getStatus())))
									.count();
							objMarketModel.setMarketName(marketName);
							objMarketModel.setMigrtedSiteCount(newUNYMigratedsitesCount);
							objListModelWeek.add(objMarketModel);

						}
					}

					if (!marketNamesWeek.contains("New England")) {
						MarketModel objMarketModel = new MarketModel();
						objMarketModel.setMarketName("New England");
						objListModelWeek.add(objMarketModel);

					}

					if (!marketNamesWeek.contains("Upstate New York")) {
						MarketModel objMarketModel = new MarketModel();
						objMarketModel.setMarketName("Upstate New York");
						objListModelWeek.add(objMarketModel);

					}

					objWeekTotalCIReportModel.setObjSiteList(objListModelWeek);

				}

			} else {
				MarketModel objMarketModelEng = new MarketModel();
				objMarketModelEng.setMarketName("New England");
				objListModelWeek.add(objMarketModelEng);

				MarketModel objMarketModelNy = new MarketModel();
				objMarketModelNy.setMarketName("Upstate New York");
				objListModelWeek.add(objMarketModelNy);
				objWeekTotalCIReportModel.setName(" Total Sites Migrated");
				objWeekTotalCIReportModel.setObjSiteList(objListModelWeek);

			}

			// vlsm and lsm status
			long rehomeCount = 1193;
			long lsmCountTot = objCountsList.stream()
					.filter(x -> (("NO".equalsIgnoreCase(x.getVlsm()) || StringUtils.isEmpty(x.getVlsm()))
							&& "Migrated".equalsIgnoreCase(x.getStatus())))
					.count();
			long directvlsmCountTot = objCountsList.stream()
					.filter(x -> ("YES".equalsIgnoreCase(x.getVlsm()) && "Migrated".equalsIgnoreCase(x.getStatus())))
					.count();

			long totVlsmCount = rehomeCount + directvlsmCountTot;
			long totlsmCount = lsmCountTot - rehomeCount;

			// Daily Vlsm Lsm Data
			List<SchedulingVerizonEntity> objCurrentWeekVlsmList = objCIReportRepository
					.getSchedVerListVlsmLsmCIReportsDailyWise(selctionDate);

			long lsmMigCountcurrentWeek = objCurrentWeekVlsmList.stream()
					.filter(x -> (("NO".equalsIgnoreCase(x.getVlsm()) || StringUtils.isEmpty(x.getVlsm()))
							&& "Migrated".equalsIgnoreCase(x.getStatus())))
					.count();
			long vlsmMigCountcurrentWeek = objCurrentWeekVlsmList.stream()
					.filter(x -> ("YES".equalsIgnoreCase(x.getVlsm()) && "Migrated".equalsIgnoreCase(x.getStatus())))
					.count();
			long lsmRollBackCountcurrentWeek = objCurrentWeekVlsmList.stream()
					.filter(x -> (("NO".equalsIgnoreCase(x.getVlsm()) || StringUtils.isEmpty(x.getVlsm()))
							&& "Rolled Back".equalsIgnoreCase(x.getStatus())))
					.count();
			long vlsmRollBackCountcurrentWeek = objCurrentWeekVlsmList.stream()
					.filter(x -> ("YES".equalsIgnoreCase(x.getVlsm()) && "Rolled Back".equalsIgnoreCase(x.getStatus())))
					.count();

			VlsmAndLsmReportModel objVlsmAndLsmReportModel = new VlsmAndLsmReportModel();
			objVlsmAndLsmReportModel.setTotSiteLsm(totlsmCount);
			objVlsmAndLsmReportModel.setTotSiteVlsm(totVlsmCount);
			objVlsmAndLsmReportModel.setLsmMigCurrentWeek(lsmMigCountcurrentWeek);
			objVlsmAndLsmReportModel.setVlsmMigCurrentWeek(vlsmMigCountcurrentWeek);
			objVlsmAndLsmReportModel.setLsmRollBackCurrentWeek(lsmRollBackCountcurrentWeek);
			objVlsmAndLsmReportModel.setVlsmRollBackCurrentWeek(vlsmRollBackCountcurrentWeek);

			objHashMapResult.put("overAllData", objTotalCIReportModel);
			objHashMapResult.put("currentWeekData", objWeekTotalCIReportModel);
			objHashMapResult.put("vlsmandLsmDetails", objVlsmAndLsmReportModel);
			objHashMapResult.put("rehomeCount", rehomeCount);

		} catch (Exception e) {
			logger.error("Exception in CIReportServiceImpl.getSchedulingVerizonEntityListForCIReportsDailyWise() :"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return objHashMapResult;
	}

	/**
	 * This api will getSchedulingSprintEntityListForCIReports
	 * 
	 * @return Map<String, Object>
	 */
	@Override
	public Map<String, Object> getSchedulingSprintEntityListForCIReports() {
		Map<String, Object> objHashMapResult = new LinkedHashMap<>();
		TotalCIReportModel objTotalCIReportModel = new TotalCIReportModel();
		TotalCIReportModel objWeekTotalCIReportModel = new TotalCIReportModel();
		try {
			List<SchedulingSprintEntity> objCountsList = objCIReportRepository
					.getSchedulingSprintEntityListForCIReports();

			if (objCountsList != null && objCountsList.size() > 0) {

				// for overall
				long totalsites = 1500;
				long totalFitSites = 24;
				long totalCentralSites = 1252;
				long totalWestSites = 224;

				// long totalMigrated=objCountsList.stream().filter(x ->
				// ("Migrated".equalsIgnoreCase(x.getStatus()))).count();

				Set<String> regoinsList = objCountsList.stream().map(x -> x.getRegion()).collect(Collectors.toSet());

				if (regoinsList != null && regoinsList.size() > 0) {

					long newAllMigratedsitesCount = objCountsList.stream().filter(
							x -> ("Migrated".equalsIgnoreCase(x.getStatus())) && ("FIT".equalsIgnoreCase(x.getRegion())
									|| "Central".equalsIgnoreCase(x.getRegion())
									|| "West".equalsIgnoreCase(x.getRegion())))
							.count();

					float percentageToOveralltmigrated = (float) ((Double.valueOf(newAllMigratedsitesCount) * 100)
							/ totalsites);

					objTotalCIReportModel.setName("Total Migration");
					objTotalCIReportModel.setMigrtedSiteCount(newAllMigratedsitesCount);
					objTotalCIReportModel.setPercenatgeOfMigrated(percentageToOveralltmigrated);
					objTotalCIReportModel.setTotalCount(totalsites);

					List<MarketModel> objListModel = new ArrayList<>();
					for (String region : regoinsList) {

						if ("FIT".equalsIgnoreCase(region)) {
							MarketModel objMarketModel = new MarketModel();

							long totalFitMigrated = objCountsList.stream()
									.filter(x -> ("Migrated".equalsIgnoreCase(x.getStatus())
											&& "FIT".equalsIgnoreCase(x.getRegion())))
									.count();
							float percentageToFitmigrated = (float) ((Double.valueOf(totalFitMigrated) * 100)
									/ totalFitSites);

							objMarketModel.setMarketName(region);
							objMarketModel.setTotalCount(totalFitSites);
							objMarketModel.setPercenatgeOfMigrated(percentageToFitmigrated);
							objMarketModel.setMigrtedSiteCount(totalFitMigrated);
							objListModel.add(objMarketModel);

						}

						if ("Central".equalsIgnoreCase(region)) {
							MarketModel objMarketModel = new MarketModel();

							long totalCentralMigrated = objCountsList.stream()
									.filter(x -> ("Migrated".equalsIgnoreCase(x.getStatus())
											&& "Central".equalsIgnoreCase(x.getRegion())))
									.count();
							float percentageToCentralmigrated = (float) ((Double.valueOf(totalCentralMigrated) * 100)
									/ totalCentralSites);

							objMarketModel.setMarketName(region);
							objMarketModel.setTotalCount(totalCentralSites);
							objMarketModel.setPercenatgeOfMigrated(percentageToCentralmigrated);
							objMarketModel.setMigrtedSiteCount(totalCentralMigrated);
							objListModel.add(objMarketModel);

						}

						if ("West".equalsIgnoreCase(region)) {
							MarketModel objMarketModel = new MarketModel();

							long totalWestMigrated = objCountsList.stream()
									.filter(x -> ("Migrated".equalsIgnoreCase(x.getStatus())
											&& "West".equalsIgnoreCase(x.getRegion())))
									.count();
							float percentageToWestmigrated = (float) ((Double.valueOf(totalWestMigrated) * 100)
									/ totalWestSites);

							objMarketModel.setMarketName(region);
							objMarketModel.setTotalCount(totalWestSites);
							objMarketModel.setPercenatgeOfMigrated(percentageToWestmigrated);
							objMarketModel.setMigrtedSiteCount(totalWestMigrated);
							objListModel.add(objMarketModel);

						}

					}

					if (!regoinsList.contains("FIT")) {
						MarketModel objMarketModel = new MarketModel();
						objMarketModel.setMarketName("FIT");
						objMarketModel.setTotalCount(totalFitSites);
						objListModel.add(objMarketModel);
					}

					if (!regoinsList.contains("Central")) {
						MarketModel objMarketModel = new MarketModel();
						objMarketModel.setMarketName("Central");
						objMarketModel.setTotalCount(totalCentralSites);
						objListModel.add(objMarketModel);
					}

					if (!regoinsList.contains("West")) {
						MarketModel objMarketModel = new MarketModel();
						objMarketModel.setMarketName("West");
						objMarketModel.setTotalCount(totalWestSites);
						objListModel.add(objMarketModel);
					}

					objTotalCIReportModel.setObjSiteList(objListModel);
				}

			}

			// for current week
			List<SchedulingSprintEntity> objCurrentWeekList = objCIReportRepository
					.getSchedsprListCurrentWeekForCIReports();

			if (objCurrentWeekList != null && objCurrentWeekList.size() > 0) {
				Set<String> regoinsList = objCountsList.stream().map(x -> x.getRegion()).collect(Collectors.toSet());

				if (regoinsList != null && regoinsList.size() > 0) {

					long newAllMigratedsitesCount = objCurrentWeekList.stream().filter(
							x -> ("Migrated".equalsIgnoreCase(x.getStatus())) && ("FIT".equalsIgnoreCase(x.getRegion())
									|| "Central".equalsIgnoreCase(x.getRegion())
									|| "West".equalsIgnoreCase(x.getRegion())))
							.count();

					objWeekTotalCIReportModel.setName("Migration this week");
					objWeekTotalCIReportModel.setMigrtedSiteCount(newAllMigratedsitesCount);

					List<MarketModel> objListModel = new ArrayList<>();
					for (String region : regoinsList) {

						if ("FIT".equalsIgnoreCase(region)) {
							MarketModel objMarketModel = new MarketModel();

							long totalFitMigrated = objCurrentWeekList.stream()
									.filter(x -> ("Migrated".equalsIgnoreCase(x.getStatus())
											&& "FIT".equalsIgnoreCase(x.getRegion())))
									.count();

							objMarketModel.setMarketName(region);

							objMarketModel.setMigrtedSiteCount(totalFitMigrated);
							objListModel.add(objMarketModel);

						}

						if ("Central".equalsIgnoreCase(region)) {
							MarketModel objMarketModel = new MarketModel();

							long totalCentralMigrated = objCurrentWeekList.stream()
									.filter(x -> ("Migrated".equalsIgnoreCase(x.getStatus())
											&& "Central".equalsIgnoreCase(x.getRegion())))
									.count();

							objMarketModel.setMarketName(region);
							objMarketModel.setMigrtedSiteCount(totalCentralMigrated);
							objListModel.add(objMarketModel);

						}

						if ("West".equalsIgnoreCase(region)) {
							MarketModel objMarketModel = new MarketModel();

							long totalWestMigrated = objCurrentWeekList.stream()
									.filter(x -> ("Migrated".equalsIgnoreCase(x.getStatus())
											&& "West".equalsIgnoreCase(x.getRegion())))
									.count();

							objMarketModel.setMarketName(region);
							objMarketModel.setMigrtedSiteCount(totalWestMigrated);
							objListModel.add(objMarketModel);

						}

					}

					objWeekTotalCIReportModel.setObjSiteList(objListModel);
				}

			}

			objHashMapResult.put("overAllData", objTotalCIReportModel);
			objHashMapResult.put("currentWeekData", objWeekTotalCIReportModel);

		} catch (Exception e) {
			logger.error("Exception in CIReportServiceImpl.getSchedulingSprintEntityListForCIReports(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}

		return objHashMapResult;

	}

	/**
	 * This api will getSchedulingSprintEntityListForCIReportsPeriod
	 * 
	 * @param fromDate,toDate
	 * @return Map<String, Object>
	 */
	@Override
	public Map<String, Object> getSchedulingSprintEntityListForCIReportsPeriod(String fromDate, String toDate) {
		Map<String, Object> objHashMapResult = new LinkedHashMap<>();
		TotalCIReportModel objTotalCIReportModel = new TotalCIReportModel();
		TotalCIReportModel objWeekTotalCIReportModel = new TotalCIReportModel();
		try {
			List<SchedulingSprintEntity> objCountsList = objCIReportRepository
					.getSchedulingSprintEntityListForCIReports();

			if (objCountsList != null && objCountsList.size() > 0) {

				// for overall
				long totalsites = 1500;
				long totalFitSites = 24;
				long totalCentralSites = 1252;
				long totalWestSites = 224;

				// long totalMigrated=objCountsList.stream().filter(x ->
				// ("Migrated".equalsIgnoreCase(x.getStatus()))).count();

				Set<String> regoinsList = objCountsList.stream().map(x -> x.getRegion()).collect(Collectors.toSet());

				if (regoinsList != null && regoinsList.size() > 0) {

					long newAllMigratedsitesCount = objCountsList.stream().filter(
							x -> ("Migrated".equalsIgnoreCase(x.getStatus())) && ("FIT".equalsIgnoreCase(x.getRegion())
									|| "Central".equalsIgnoreCase(x.getRegion())
									|| "West".equalsIgnoreCase(x.getRegion())))
							.count();

					float percentageToOveralltmigrated = (float) ((Double.valueOf(newAllMigratedsitesCount) * 100)
							/ totalsites);

					objTotalCIReportModel.setName("Total Migration");
					objTotalCIReportModel.setMigrtedSiteCount(newAllMigratedsitesCount);
					objTotalCIReportModel.setPercenatgeOfMigrated(percentageToOveralltmigrated);
					objTotalCIReportModel.setTotalCount(totalsites);

					List<MarketModel> objListModel = new ArrayList<>();
					for (String region : regoinsList) {

						if ("FIT".equalsIgnoreCase(region)) {
							MarketModel objMarketModel = new MarketModel();

							long totalFitMigrated = objCountsList.stream()
									.filter(x -> ("Migrated".equalsIgnoreCase(x.getStatus())
											&& "FIT".equalsIgnoreCase(x.getRegion())))
									.count();
							float percentageToFitmigrated = (float) ((Double.valueOf(totalFitMigrated) * 100)
									/ totalFitSites);

							objMarketModel.setMarketName(region);
							objMarketModel.setTotalCount(totalFitSites);
							objMarketModel.setPercenatgeOfMigrated(percentageToFitmigrated);
							objMarketModel.setMigrtedSiteCount(totalFitMigrated);
							objListModel.add(objMarketModel);

						}

						if ("Central".equalsIgnoreCase(region)) {
							MarketModel objMarketModel = new MarketModel();

							long totalCentralMigrated = objCountsList.stream()
									.filter(x -> ("Migrated".equalsIgnoreCase(x.getStatus())
											&& "Central".equalsIgnoreCase(x.getRegion())))
									.count();
							float percentageToCentralmigrated = (float) ((Double.valueOf(totalCentralMigrated) * 100)
									/ totalCentralSites);

							objMarketModel.setMarketName(region);
							objMarketModel.setTotalCount(totalCentralSites);
							objMarketModel.setPercenatgeOfMigrated(percentageToCentralmigrated);
							objMarketModel.setMigrtedSiteCount(totalCentralMigrated);
							objListModel.add(objMarketModel);

						}

						if ("West".equalsIgnoreCase(region)) {
							MarketModel objMarketModel = new MarketModel();

							long totalWestMigrated = objCountsList.stream()
									.filter(x -> ("Migrated".equalsIgnoreCase(x.getStatus())
											&& "West".equalsIgnoreCase(x.getRegion())))
									.count();
							float percentageToWestmigrated = (float) ((Double.valueOf(totalWestMigrated) * 100)
									/ totalWestSites);

							objMarketModel.setMarketName(region);
							objMarketModel.setTotalCount(totalWestSites);
							objMarketModel.setPercenatgeOfMigrated(percentageToWestmigrated);
							objMarketModel.setMigrtedSiteCount(totalWestMigrated);
							objListModel.add(objMarketModel);

						}

					}

					if (!regoinsList.contains("FIT")) {
						MarketModel objMarketModel = new MarketModel();
						objMarketModel.setMarketName("FIT");
						objMarketModel.setTotalCount(totalFitSites);
						objListModel.add(objMarketModel);
					}

					if (!regoinsList.contains("Central")) {
						MarketModel objMarketModel = new MarketModel();
						objMarketModel.setMarketName("Central");
						objMarketModel.setTotalCount(totalCentralSites);
						objListModel.add(objMarketModel);
					}

					if (!regoinsList.contains("West")) {
						MarketModel objMarketModel = new MarketModel();
						objMarketModel.setMarketName("West");
						objMarketModel.setTotalCount(totalWestSites);
						objListModel.add(objMarketModel);
					}
					objTotalCIReportModel.setObjSiteList(objListModel);
				}

			}

			// for duration
			List<SchedulingSprintEntity> objCurrentWeekList = objCIReportRepository
					.getSchedsprListPeriodForCIReports(fromDate, toDate);

			if (objCurrentWeekList != null && objCurrentWeekList.size() > 0) {
				Set<String> regoinsList = objCountsList.stream().map(x -> x.getRegion()).collect(Collectors.toSet());

				if (regoinsList != null && regoinsList.size() > 0) {

					long newAllMigratedsitesCount = objCurrentWeekList.stream().filter(
							x -> ("Migrated".equalsIgnoreCase(x.getStatus())) && ("FIT".equalsIgnoreCase(x.getRegion())
									|| "Central".equalsIgnoreCase(x.getRegion())
									|| "West".equalsIgnoreCase(x.getRegion())))
							.count();

					objWeekTotalCIReportModel.setName("Migration this Duration");
					objWeekTotalCIReportModel.setMigrtedSiteCount(newAllMigratedsitesCount);

					List<MarketModel> objListModel = new ArrayList<>();
					for (String region : regoinsList) {

						if ("FIT".equalsIgnoreCase(region)) {
							MarketModel objMarketModel = new MarketModel();

							long totalFitMigrated = objCurrentWeekList.stream()
									.filter(x -> ("Migrated".equalsIgnoreCase(x.getStatus())
											&& "FIT".equalsIgnoreCase(x.getRegion())))
									.count();

							objMarketModel.setMarketName(region);

							objMarketModel.setMigrtedSiteCount(totalFitMigrated);
							objListModel.add(objMarketModel);

						}

						if ("Central".equalsIgnoreCase(region)) {
							MarketModel objMarketModel = new MarketModel();

							long totalCentralMigrated = objCurrentWeekList.stream()
									.filter(x -> ("Migrated".equalsIgnoreCase(x.getStatus())
											&& "Central".equalsIgnoreCase(x.getRegion())))
									.count();

							objMarketModel.setMarketName(region);
							objMarketModel.setMigrtedSiteCount(totalCentralMigrated);
							objListModel.add(objMarketModel);

						}

						if ("West".equalsIgnoreCase(region)) {
							MarketModel objMarketModel = new MarketModel();

							long totalWestMigrated = objCurrentWeekList.stream()
									.filter(x -> ("Migrated".equalsIgnoreCase(x.getStatus())
											&& "West".equalsIgnoreCase(x.getRegion())))
									.count();

							objMarketModel.setMarketName(region);
							objMarketModel.setMigrtedSiteCount(totalWestMigrated);
							objListModel.add(objMarketModel);

						}

					}

					objWeekTotalCIReportModel.setObjSiteList(objListModel);
				}

			}

			objHashMapResult.put("overAllData", objTotalCIReportModel);
			objHashMapResult.put("currentWeekData", objWeekTotalCIReportModel);

		} catch (Exception e) {
			logger.error("Exception in CIReportServiceImpl.getSchedulingSprintEntityListForCIReportsPeriod(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}

		return objHashMapResult;

	}

	/**
	 * This api will getSchedulingSprintEntityListForCIReportsDailyWise
	 * 
	 * @param date
	 * @return Map<String, Object>
	 */
	@Override
	public Map<String, Object> getSchedulingSprintEntityListForCIReportsDailyWise(String date) {
		Map<String, Object> objHashMapResult = new LinkedHashMap<>();
		TotalCIReportModel objTotalCIReportModel = new TotalCIReportModel();
		TotalCIReportModel objWeekTotalCIReportModel = new TotalCIReportModel();
		try {
			List<SchedulingSprintEntity> objCountsList = objCIReportRepository
					.getSchedulingSprintEntityListForCIReports();

			if (objCountsList != null && objCountsList.size() > 0) {

				// for overall
				long totalsites = 1500;
				long totalFitSites = 24;
				long totalCentralSites = 1252;
				long totalWestSites = 224;

				// long totalMigrated=objCountsList.stream().filter(x ->
				// ("Migrated".equalsIgnoreCase(x.getStatus()))).count();

				Set<String> regoinsList = objCountsList.stream().map(x -> x.getRegion()).collect(Collectors.toSet());

				if (regoinsList != null && regoinsList.size() > 0) {

					long newAllMigratedsitesCount = objCountsList.stream().filter(
							x -> ("Migrated".equalsIgnoreCase(x.getStatus())) && ("FIT".equalsIgnoreCase(x.getRegion())
									|| "Central".equalsIgnoreCase(x.getRegion())
									|| "West".equalsIgnoreCase(x.getRegion())))
							.count();
					// hardcode
					newAllMigratedsitesCount = newAllMigratedsitesCount + totalFitSites;

					float percentageToOveralltmigrated = (float) ((Double.valueOf(newAllMigratedsitesCount) * 100)
							/ totalsites);

					objTotalCIReportModel.setName("Total Migration");
					objTotalCIReportModel.setMigrtedSiteCount(newAllMigratedsitesCount);
					objTotalCIReportModel.setPercenatgeOfMigrated(percentageToOveralltmigrated);
					objTotalCIReportModel.setTotalCount(totalsites);

					List<MarketModel> objListModel = new ArrayList<>();
					for (String region : regoinsList) {

						if ("FIT".equalsIgnoreCase(region)) {
							MarketModel objMarketModel = new MarketModel();

							long totalFitMigrated = objCountsList.stream()
									.filter(x -> ("Migrated".equalsIgnoreCase(x.getStatus())
											&& "FIT".equalsIgnoreCase(x.getRegion())))
									.count();
							totalFitMigrated = totalFitSites;
							float percentageToFitmigrated = (float) ((Double.valueOf(totalFitMigrated) * 100)
									/ totalFitSites);

							objMarketModel.setMarketName(region);
							objMarketModel.setTotalCount(totalFitSites);
							objMarketModel.setPercenatgeOfMigrated(percentageToFitmigrated);
							objMarketModel.setMigrtedSiteCount(totalFitMigrated);
							objListModel.add(objMarketModel);

						}

						if ("Central".equalsIgnoreCase(region)) {
							MarketModel objMarketModel = new MarketModel();

							long totalCentralMigrated = objCountsList.stream()
									.filter(x -> ("Migrated".equalsIgnoreCase(x.getStatus())
											&& "Central".equalsIgnoreCase(x.getRegion())))
									.count();
							float percentageToCentralmigrated = (float) ((Double.valueOf(totalCentralMigrated) * 100)
									/ totalCentralSites);

							objMarketModel.setMarketName(region);
							objMarketModel.setTotalCount(totalCentralSites);
							objMarketModel.setPercenatgeOfMigrated(percentageToCentralmigrated);
							objMarketModel.setMigrtedSiteCount(totalCentralMigrated);
							objListModel.add(objMarketModel);

						}

						if ("West".equalsIgnoreCase(region)) {
							MarketModel objMarketModel = new MarketModel();

							long totalWestMigrated = objCountsList.stream()
									.filter(x -> ("Migrated".equalsIgnoreCase(x.getStatus())
											&& "West".equalsIgnoreCase(x.getRegion())))
									.count();
							float percentageToWestmigrated = (float) ((Double.valueOf(totalWestMigrated) * 100)
									/ totalWestSites);

							objMarketModel.setMarketName(region);
							objMarketModel.setTotalCount(totalWestSites);
							objMarketModel.setPercenatgeOfMigrated(percentageToWestmigrated);
							objMarketModel.setMigrtedSiteCount(totalWestMigrated);
							objListModel.add(objMarketModel);

						}

					}

					if (!regoinsList.contains("FIT")) {
						MarketModel objMarketModel = new MarketModel();
						objMarketModel.setMarketName("FIT");
						objMarketModel.setTotalCount(totalFitSites);
						objMarketModel.setPercenatgeOfMigrated(100);
						objMarketModel.setMigrtedSiteCount(totalFitSites);
						objListModel.add(objMarketModel);
					}

					if (!regoinsList.contains("Central")) {
						MarketModel objMarketModel = new MarketModel();
						objMarketModel.setMarketName("Central");
						objMarketModel.setTotalCount(totalCentralSites);
						objListModel.add(objMarketModel);
					}

					if (!regoinsList.contains("West")) {
						MarketModel objMarketModel = new MarketModel();
						objMarketModel.setMarketName("West");
						objMarketModel.setTotalCount(totalWestSites);
						objListModel.add(objMarketModel);
					}
					objTotalCIReportModel.setObjSiteList(objListModel);
				}

			}

			// for daily Wise
			List<SchedulingSprintEntity> objCurrentDailyList = objCIReportRepository
					.getSchedsprListDayWiseForCIReports(date);
			SprintDailyModel objSprintDailyModel = new SprintDailyModel();
			if (objCurrentDailyList != null && objCurrentDailyList.size() > 0) {

				long newAllMigratedsitesCount = objCountsList.stream()
						.filter(x -> ("Migrated".equalsIgnoreCase(x.getStatus()))
								&& ("FIT".equalsIgnoreCase(x.getRegion()) || "Central".equalsIgnoreCase(x.getRegion())
										|| "West".equalsIgnoreCase(x.getRegion())))
						.count();

				long newAllCancelled = objCountsList.stream()
						.filter(x -> ("Cancelled".equalsIgnoreCase(x.getStatus()))
								&& ("FIT".equalsIgnoreCase(x.getRegion()) || "Central".equalsIgnoreCase(x.getRegion())
										|| "West".equalsIgnoreCase(x.getRegion())))
						.count();

				long newAllinProgress = objCountsList.stream()
						.filter(x -> ("In Progress".equalsIgnoreCase(x.getStatus()))
								&& ("FIT".equalsIgnoreCase(x.getRegion()) || "Central".equalsIgnoreCase(x.getRegion())
										|| "West".equalsIgnoreCase(x.getRegion())))
						.count();

				objSprintDailyModel.setMigratedCount(newAllMigratedsitesCount);
				objSprintDailyModel.setCancelled(newAllCancelled);
				objSprintDailyModel.setInProgress(newAllinProgress);
				objSprintDailyModel.setAttempted(objCurrentDailyList.size());

			}

			/*
			 * if (objCurrentDailyList != null && objCurrentDailyList.size() > 0) {
			 * Set<String> regoinsList = objCurrentDailyList.stream().map(x ->
			 * x.getRegion()) .collect(Collectors.toSet());
			 * 
			 * if (regoinsList != null && regoinsList.size() > 0) {
			 * 
			 * long newAllMigratedsitesCount = objCurrentDailyList.stream().filter( x ->
			 * ("Migrated".equalsIgnoreCase(x.getStatus())) &&
			 * ("FIT".equalsIgnoreCase(x.getRegion()) ||
			 * "Central".equalsIgnoreCase(x.getRegion()) ||
			 * "West".equalsIgnoreCase(x.getRegion()))) .count();
			 * 
			 * objWeekTotalCIReportModel.setName("Migration Sites");
			 * objWeekTotalCIReportModel.setMigrtedSiteCount(newAllMigratedsitesCount);
			 * 
			 * List<MarketModel> objListModel = new ArrayList<>(); for (String region :
			 * regoinsList) {
			 * 
			 * if ("FIT".equalsIgnoreCase(region)) { MarketModel objMarketModel = new
			 * MarketModel();
			 * 
			 * long totalFitMigrated = objCurrentDailyList.stream() .filter(x ->
			 * ("Migrated".equalsIgnoreCase(x.getStatus()) &&
			 * "FIT".equalsIgnoreCase(x.getRegion()))) .count();
			 * 
			 * objMarketModel.setMarketName(region);
			 * objMarketModel.setMigrtedSiteCount(totalFitMigrated);
			 * objListModel.add(objMarketModel);
			 * 
			 * }
			 * 
			 * if ("Central".equalsIgnoreCase(region)) { MarketModel objMarketModel = new
			 * MarketModel();
			 * 
			 * long totalCentralMigrated = objCurrentDailyList.stream() .filter(x ->
			 * ("Migrated".equalsIgnoreCase(x.getStatus()) &&
			 * "Central".equalsIgnoreCase(x.getRegion()))) .count();
			 * 
			 * objMarketModel.setMarketName(region);
			 * objMarketModel.setMigrtedSiteCount(totalCentralMigrated);
			 * objListModel.add(objMarketModel);
			 * 
			 * }
			 * 
			 * if ("West".equalsIgnoreCase(region)) { MarketModel objMarketModel = new
			 * MarketModel();
			 * 
			 * long totalWestMigrated = objCurrentDailyList.stream() .filter(x ->
			 * ("Migrated".equalsIgnoreCase(x.getStatus()) &&
			 * "West".equalsIgnoreCase(x.getRegion()))) .count();
			 * 
			 * objMarketModel.setMarketName(region);
			 * objMarketModel.setMigrtedSiteCount(totalWestMigrated);
			 * objListModel.add(objMarketModel);
			 * 
			 * }
			 * 
			 * }
			 * 
			 * 
			 * if (!regoinsList.contains("FIT")) { MarketModel objMarketModel = new
			 * MarketModel(); objMarketModel.setMarketName("FIT");
			 * 
			 * objListModel.add(objMarketModel); }
			 * 
			 * if (!regoinsList.contains("Central")) { MarketModel objMarketModel = new
			 * MarketModel(); objMarketModel.setMarketName("Central");
			 * objListModel.add(objMarketModel); }
			 * 
			 * if (!regoinsList.contains("West")) { MarketModel objMarketModel = new
			 * MarketModel(); objMarketModel.setMarketName("West");
			 * objListModel.add(objMarketModel); }
			 * objWeekTotalCIReportModel.setObjSiteList(objListModel); }
			 * 
			 * }else { List<MarketModel> objListModel = new ArrayList<>(); MarketModel
			 * objMarketModelFit = new MarketModel();
			 * objMarketModelFit.setMarketName("FIT");
			 * 
			 * objListModel.add(objMarketModelFit);
			 * 
			 * MarketModel objMarketModelCen = new MarketModel();
			 * objMarketModelCen.setMarketName("Central");
			 * objListModel.add(objMarketModelCen);
			 * 
			 * MarketModel objMarketModelWest = new MarketModel();
			 * objMarketModelWest.setMarketName("West");
			 * objListModel.add(objMarketModelWest);
			 * objWeekTotalCIReportModel.setObjSiteList(objListModel);
			 * 
			 * }
			 */

			objHashMapResult.put("overAllData", objTotalCIReportModel);
			objHashMapResult.put("currentWeekData", objSprintDailyModel);

		} catch (Exception e) {
			logger.error("Exception in CIReportServiceImpl.getSchedulingSprintEntityListForCIReportsDailyWise(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}

		return objHashMapResult;

	}

	/**
	 * This api will getSchedulingDashBoardCIReports
	 * 
	 * @param customerEntities
	 * @return Map<String, Object>
	 */
	@Override
	public Map<String, Object> getSchedulingDashBoardCIReports(List<CustomerEntity> customerEntities) {
		Map<String, Object> objHashMapResult = new LinkedHashMap<>();
		TotalCIReportModel objTotalMigVerReportModel = new TotalCIReportModel();
		TotalCIReportModel objTotalMigsprintReportModel = new TotalCIReportModel();
		long totalVersites = 6400;
		long totalSprisites = 1500;
		List<NeCommissionModel> cirReportList = new ArrayList<NeCommissionModel>();
		List<String> objCustomersNames = new ArrayList<String>();
		List<String> totalObjList = new ArrayList<String>();
		List<String> migratedList = new ArrayList<String>();
		List<String> totalPercDataList = new ArrayList<String>();
		List<String> migratedPercDataList = new ArrayList<String>();
		try {

			if (customerEntities != null && customerEntities.size() > 0) {
				for (CustomerEntity customerEntity : customerEntities) {
					if (Constants.VZN_CUSTOMER_ID == customerEntity.getId()) {

						List<SchedulingVerizonEntity> objCountsVerizonList = objCIReportRepository
								.getSchedulingVerizonEntityListForCIReports();
						long verizonTotMig = 0;
						float percentageTotmigrated = 0;
						if (objCountsVerizonList != null && objCountsVerizonList.size() > 0) {

							verizonTotMig = objCountsVerizonList.stream()
									.filter(x -> ("Migrated".equalsIgnoreCase(x.getStatus()))
											&& ("New England".equalsIgnoreCase(x.getMarket())
													|| "Upstate New York".equalsIgnoreCase(x.getMarket())))
									.count();

							percentageTotmigrated = (float) ((Double.valueOf(verizonTotMig) * 100) / totalVersites);

							objTotalMigVerReportModel.setMigrtedSiteCount(verizonTotMig);
							objTotalMigVerReportModel.setPercenatgeOfMigrated(percentageTotmigrated);

						}
						objTotalMigVerReportModel.setTotalCount(totalVersites);

						// objHashMapResult.put("versionDetails", objTotalMigVerReportModel);
						totalPercDataList.add("100");
						// migratedPercDataList.add(CommonUtil.decimalFormtter(percentageTotmigrated));
						// migratedPercDataList.add(CommonUtil.decimalFormtter(percentageTotmigrated));
						migratedPercDataList.add(String.valueOf(percentageTotmigrated));
						totalObjList.add(String.valueOf(totalVersites));
						migratedList.add(String.valueOf(verizonTotMig));
						objCustomersNames.add(customerEntity.getCustomerName());

					} else if (Constants.SPT_CUSTOMER_ID == customerEntity.getId()) {
						List<SchedulingSprintEntity> objCountsSprintList = objCIReportRepository
								.getSchedulingSprintEntityListForCIReports();
						long sprintTotMig = 0;
						long totalFitSites = 24;
						float percentageTotmigrated = 0;
						if (objCountsSprintList != null && objCountsSprintList.size() > 0) {
							sprintTotMig = objCountsSprintList.stream()
									.filter(x -> ("Migrated".equalsIgnoreCase(x.getStatus()))
											&& ("FIT".equalsIgnoreCase(x.getRegion())
													|| "Central".equalsIgnoreCase(x.getRegion())
													|| "West".equalsIgnoreCase(x.getRegion())))
									.count();
							sprintTotMig = sprintTotMig + totalFitSites; //As we have considered fit sites all are migrated and no more we will get the info about it
							percentageTotmigrated = (float) ((Double.valueOf(sprintTotMig) * 100) / totalSprisites);
							objTotalMigsprintReportModel.setMigrtedSiteCount(sprintTotMig);
							objTotalMigsprintReportModel.setPercenatgeOfMigrated(percentageTotmigrated);
						}

						objTotalMigsprintReportModel.setTotalCount(totalSprisites);
						// objHashMapResult.put("sprintDetails", objTotalMigsprintReportModel);
						totalPercDataList.add("100");
						// migratedPercDataList.add(String.valueOf(CommonUtil.decimalFormtter(percentageTotmigrated)));
						// migratedPercDataList.add(String.valueOf(CommonUtil.decimalFormtter(percentageTotmigrated)));
						migratedPercDataList.add(String.valueOf(percentageTotmigrated));
						totalObjList.add(String.valueOf(totalSprisites));
						migratedList.add(String.valueOf(sprintTotMig));
						objCustomersNames.add(customerEntity.getCustomerName());
					} else {
						totalObjList.add("0");
						migratedList.add("0");
						objCustomersNames.add(customerEntity.getCustomerName());
						migratedPercDataList.add("0");
						totalPercDataList.add("0");
					}
				}
			}
			NeCommissionModel cir = new NeCommissionModel();
			cir.setLabel("Planned");
			cir.setBackgroundColor("#00a9d4");
			cir.setData(totalObjList);
			cir.setPercData(totalPercDataList);
			cirReportList.add(cir);

			cir = new NeCommissionModel();

			cir.setLabel("Migrated");
			cir.setBackgroundColor("#fdc844");
			cir.setData(migratedList);
			cir.setPercData(migratedPercDataList);
			cirReportList.add(cir);

			objHashMapResult.put("datasets", cirReportList);
			objHashMapResult.put("labels", objCustomersNames);
		} catch (Exception e) {
			logger.error("Exception in CIReportServiceImpl.getSchedulingDashBoardCIReports(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}

		return objHashMapResult;
	}

	/**
	 * This api will getSchedulingDashBoardMarketCIReports
	 * 
	 * @param customerEntities
	 * @return Map<String, Object>
	 */
	@Override
	public Map<String, Object> getSchedulingDashBoardMarketCIReports(List<CustomerEntity> customerEntities) {
		Map<String, Object> objHashMapResult = new LinkedHashMap<>();
		boolean vznEnglandMarket = false;
		boolean vznUNYMarket = false;

		boolean sptFitRegion = false;
		boolean sptCentralRegion = false;
		boolean sptWestRegion = false;

		try {
			if (customerEntities != null && customerEntities.size() > 0) {
				for (CustomerEntity customerEntity : customerEntities) {
					List<String> totalObjList = new ArrayList<String>();
					List<String> migratedList = new ArrayList<String>();
					List<String> totalPercDataList = new ArrayList<String>();
					List<String> migratedPercDataList = new ArrayList<String>();
					Map<String, Object> customerbasedData = new LinkedHashMap<>();
					List<String> objMarketNames = new ArrayList<String>();
					List<NeCommissionModel> cirReportList = new ArrayList<NeCommissionModel>();

					if (Constants.VZN_CUSTOMER_ID == customerEntity.getId()) {
						// Verizon Market Based Info
						long totalEnglandSites = 4000;
						long totalUNYSites = 2400;

						List<SchedulingVerizonEntity> vznList = objCIReportRepository
								.getSchedulingVerizonEntityListForCIReports();

						if (vznList != null && vznList.size() > 0) {
							Set<String> marketNames = vznList.stream().map(x -> x.getMarket())
									.collect(Collectors.toSet());
							if (marketNames != null && marketNames.size() > 0) {
								for (String marketName : marketNames) {

									if ("New England".equalsIgnoreCase(marketName)) {
										long newEnglandMigratedsitesCount = vznList.stream()
												.filter(x -> (marketName.equalsIgnoreCase(x.getMarket())
														&& "Migrated".equalsIgnoreCase(x.getStatus())))
												.count();
										float percentagemigratedEngland = (float) ((Double
												.valueOf(newEnglandMigratedsitesCount) * 100) / totalEnglandSites);
										objMarketNames.add(marketName);
										totalObjList.add(String.valueOf(totalEnglandSites));
										migratedList.add(String.valueOf(newEnglandMigratedsitesCount));
										migratedPercDataList.add(String.valueOf(percentagemigratedEngland));
										totalPercDataList.add("100");
										vznEnglandMarket = true;
									}

									if ("Upstate New York".equalsIgnoreCase(marketName)) {
										long newUNYMigratedsitesCount = vznList.stream()
												.filter(x -> (marketName.equalsIgnoreCase(x.getMarket())
														&& "Migrated".equalsIgnoreCase(x.getStatus())))
												.count();
										float percentagemigratedUNY = (float) ((Double.valueOf(newUNYMigratedsitesCount)
												* 100) / totalUNYSites);
										objMarketNames.add(marketName);
										totalObjList.add(String.valueOf(totalUNYSites));
										migratedList.add(String.valueOf(newUNYMigratedsitesCount));
										migratedPercDataList.add(String.valueOf(percentagemigratedUNY));
										totalPercDataList.add("100");
										vznUNYMarket = true;
									}
								}
							}

						}

						if (!vznEnglandMarket) {
							objMarketNames.add("New England");
							totalObjList.add(String.valueOf(totalEnglandSites));
							migratedList.add("0");
							migratedPercDataList.add("0");
							totalPercDataList.add("0");
						}
						if (!vznUNYMarket) {
							objMarketNames.add("Upstate New York");
							totalObjList.add(String.valueOf(totalUNYSites));
							migratedList.add("0");
							migratedPercDataList.add("0");
							totalPercDataList.add("0");
						}
						NeCommissionModel cir = new NeCommissionModel();
						cir.setLabel("Planned");
						cir.setBackgroundColor("#00a9d4");
						cir.setData(totalObjList);
						cir.setPercData(totalPercDataList);
						cirReportList.add(cir);

						cir = new NeCommissionModel();

						cir.setLabel("Migrated");
						cir.setBackgroundColor("#88ce00");
						cir.setData(migratedList);
						cir.setPercData(migratedPercDataList);
						cirReportList.add(cir);

						customerbasedData.put("datasets", cirReportList);
						customerbasedData.put("labels", objMarketNames);
						objHashMapResult.put(String.valueOf(customerEntity.getId()), customerbasedData);

					} else if (Constants.SPT_CUSTOMER_ID == customerEntity.getId()) {
						// Sprint Region Based Info
						List<SchedulingSprintEntity> sptList = objCIReportRepository
								.getSchedulingSprintEntityListForCIReports();

						long totalFitSites = 24;
						long totalCentralSites = 1252;
						long totalWestSites = 224;

						long totalFitMigrated = totalFitSites;
						totalCentralSites = totalCentralSites + totalFitSites; // Fit needs to be moved into central

						if (sptList != null && sptList.size() > 0) {
							Set<String> regoinsList = sptList.stream().map(x -> x.getRegion())
									.collect(Collectors.toSet());

							if (regoinsList != null && regoinsList.size() > 0) {
								for (String region : regoinsList) {
									if ("FIT".equalsIgnoreCase(region)) {
										/*
										 * float percentageToFitmigrated = (float) ((Double.valueOf(totalFitMigrated)
										 * 100) / totalFitSites); objMarketNames.add(region);
										 * totalObjList.add(String.valueOf(totalFitSites));
										 * migratedList.add(String.valueOf(totalFitMigrated));
										 * migratedPercDataList.add(String.valueOf(percentageToFitmigrated));
										 * totalPercDataList.add("100"); sptFitRegion = true;
										 */
									}
									if ("Central".equalsIgnoreCase(region)) {
										long totalCentralMigrated = sptList.stream()
												.filter(x -> ("Migrated".equalsIgnoreCase(x.getStatus())
														&& "Central".equalsIgnoreCase(x.getRegion())))
												.count();

										totalCentralMigrated = totalCentralMigrated + totalFitMigrated; // Fit needs to
																										// be moved into
																										// central
										float percentageToCentralmigrated = (float) ((Double
												.valueOf(totalCentralMigrated) * 100) / totalCentralSites);
										objMarketNames.add(region);
										totalObjList.add(String.valueOf(totalCentralSites));
										migratedList.add(String.valueOf(totalCentralMigrated));
										migratedPercDataList.add(String.valueOf(percentageToCentralmigrated));
										totalPercDataList.add("100");
										sptCentralRegion = true;
									}
									if ("West".equalsIgnoreCase(region)) {
										long totalWestMigrated = sptList.stream()
												.filter(x -> ("Migrated".equalsIgnoreCase(x.getStatus())
														&& "West".equalsIgnoreCase(x.getRegion())))
												.count();
										float percentageToWestmigrated = (float) ((Double.valueOf(totalWestMigrated)
												* 100) / totalWestSites);
										objMarketNames.add(region);
										totalObjList.add(String.valueOf(totalWestSites));
										migratedList.add(String.valueOf(totalWestMigrated));
										migratedPercDataList.add(String.valueOf(percentageToWestmigrated));
										totalPercDataList.add("100");
										sptWestRegion = true;
									}
								}

							}
						}

						if (!sptFitRegion) {
							/*
							 * objMarketNames.add("FIT"); float percentageToFitmigrated = (float)
							 * ((Double.valueOf(totalFitMigrated) * 100) / totalFitSites);
							 * totalObjList.add(String.valueOf(totalFitSites));
							 * migratedList.add(String.valueOf(totalFitMigrated));
							 * migratedPercDataList.add(String.valueOf(percentageToFitmigrated));
							 * totalPercDataList.add("100");
							 */
						}
						if (!sptCentralRegion) {
							objMarketNames.add("Central");
							totalObjList.add(String.valueOf(totalCentralSites));
							migratedList.add("0");
							migratedPercDataList.add("0");
							totalPercDataList.add("0");
						}
						if (!sptWestRegion) {
							objMarketNames.add("West");
							totalObjList.add(String.valueOf(totalWestSites));
							migratedList.add("0");
							migratedPercDataList.add("0");
							totalPercDataList.add("0");
						}

						NeCommissionModel cir = new NeCommissionModel();
						cir.setLabel("Planned");
						cir.setBackgroundColor("#00a9d4");
						cir.setData(totalObjList);
						cir.setPercData(totalPercDataList);
						cirReportList.add(cir);

						cir = new NeCommissionModel();

						cir.setLabel("Migrated");
						cir.setBackgroundColor("#88ce00");
						cir.setData(migratedList);
						cir.setPercData(migratedPercDataList);
						cirReportList.add(cir);

						customerbasedData.put("datasets", cirReportList);
						customerbasedData.put("labels", objMarketNames);
						objHashMapResult.put(String.valueOf(customerEntity.getId()), customerbasedData);
					} else {
						NeCommissionModel cir = new NeCommissionModel();
						cir.setLabel("Planned");
						cir.setBackgroundColor("#00a9d4");
						cir.setData(totalObjList);
						cir.setPercData(totalPercDataList);
						cirReportList.add(cir);

						cir = new NeCommissionModel();

						cir.setLabel("Migrated");
						cir.setBackgroundColor("#88ce00");
						cir.setData(migratedList);
						cir.setPercData(migratedPercDataList);
						cirReportList.add(cir);

						customerbasedData.put("datasets", cirReportList);
						customerbasedData.put("labels", objMarketNames);
						objHashMapResult.put(String.valueOf(customerEntity.getId()), customerbasedData);
					}
				}

			}
		} catch (Exception e) {
			logger.error("Exception in CIReportServiceImpl.getSchedulingDashBoardMarketCIReports(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return objHashMapResult;
	}

	/**
	 * This api will getSchedulingVerizonEntityListForCIReportsFriday
	 * 
	 * @return Map<String, Object>
	 */
	@Override
	public Map<String, Object> getSchedulingVerizonEntityListForCIReportsFriday() {

		Map<String, Object> objHashMapResult = new LinkedHashMap<>();
		TotalCIReportModel objTotalCIReportModel = new TotalCIReportModel();
		TotalCIReportModel objWeekTotalCIReportModel = new TotalCIReportModel();

		List<MarketModel> objVlsmListModel = new ArrayList<>();
		List<MarketModel> objCarrirAddsListModel = new ArrayList<>();
		List<MarketModel> objConverAddsListModel = new ArrayList<>();

		try {

			List<SchedulingVerizonEntity> objCountsList = objCIReportRepository
					.getSchedulingVerizonEntityListForCIReportsFriday();

			if (objCountsList != null && objCountsList.size() > 0) {
				long totalSites = 6400;
				long newAllMigratedsitesCount = objCountsList.stream()
						.filter(x -> ("Migrated".equalsIgnoreCase(x.getStatus()))
								&& ("New England".equalsIgnoreCase(x.getMarket())
										|| "Upstate New York".equalsIgnoreCase(x.getMarket())))
						.count();
				float percentageTotmigrated = (float) ((Double.valueOf(newAllMigratedsitesCount) * 100) / totalSites);

				objTotalCIReportModel.setName("Overall Sites Migrated");
				objTotalCIReportModel.setMigrtedSiteCount(newAllMigratedsitesCount);
				objTotalCIReportModel.setTotalCount(totalSites);
				objTotalCIReportModel.setPercenatgeOfMigrated(percentageTotmigrated);
				Set<String> marketNames = objCountsList.stream().map(x -> x.getMarket()).collect(Collectors.toSet());
				List<MarketModel> objListModel = new ArrayList<>();

				if (marketNames != null && marketNames.size() > 0) {
					for (String marketName : marketNames) {

						if ("New England".equalsIgnoreCase(marketName)) {
							MarketModel objMarketModel = new MarketModel();

							long totalEnglandSites = 4000;

							long newEnglandMigratedsitesCount = objCountsList.stream()
									.filter(x -> (marketName.equalsIgnoreCase(x.getMarket())
											&& "Migrated".equalsIgnoreCase(x.getStatus())))
									.count();
							float percentagemigratedEngland = (float) ((Double.valueOf(newEnglandMigratedsitesCount)
									* 100) / totalEnglandSites);
							objMarketModel.setMarketName(marketName);
							objMarketModel.setMigrtedSiteCount(newEnglandMigratedsitesCount);
							objMarketModel.setTotalCount(totalEnglandSites);
							objMarketModel.setPercenatgeOfMigrated(percentagemigratedEngland);

							// for vlsm
							MarketModel objVlsmMarketModel = new MarketModel();
							long vlsmCountTotNewEngland = objCountsList.stream()
									.filter(x -> ("YES".equalsIgnoreCase(x.getVlsm())
											&& "Migrated".equalsIgnoreCase(x.getStatus())
											&& marketName.equalsIgnoreCase(x.getMarket())))
									.count();

							objVlsmMarketModel.setMarketName(marketName);
							objVlsmMarketModel.setTotalCount(vlsmCountTotNewEngland);

							// for carierAdds
							MarketModel objCarierAddsMarketModel = new MarketModel();
							long CarierCountTotNewEngland = objCountsList.stream()
									.filter(x -> ("Carrier Add".equalsIgnoreCase(x.getStatus())
											&& marketName.equalsIgnoreCase(x.getMarket())))
									.count();

							objCarierAddsMarketModel.setMarketName(marketName);
							objCarierAddsMarketModel.setTotalCount(CarierCountTotNewEngland);

							// for coversation
							MarketModel objcoversationMarketModel = new MarketModel();
							long coversationCountTotNewEngland = objCountsList.stream()
									.filter(x -> ("Conversion".equalsIgnoreCase(x.getStatus())
											&& marketName.equalsIgnoreCase(x.getMarket())))
									.count();

							objcoversationMarketModel.setMarketName(marketName);
							objcoversationMarketModel.setTotalCount(coversationCountTotNewEngland);

							objVlsmListModel.add(objVlsmMarketModel);
							objCarrirAddsListModel.add(objCarierAddsMarketModel);
							objConverAddsListModel.add(objcoversationMarketModel);
							objListModel.add(objMarketModel);

						}

						if ("Upstate New York".equalsIgnoreCase(marketName)) {
							MarketModel objMarketModel = new MarketModel();

							long totalUNYSites = 2400;

							long newUNYMigratedsitesCount = objCountsList.stream()
									.filter(x -> (marketName.equalsIgnoreCase(x.getMarket())
											&& "Migrated".equalsIgnoreCase(x.getStatus())))
									.count();

							float percentagemigratedUNY = (float) ((Double.valueOf(newUNYMigratedsitesCount) * 100)
									/ totalUNYSites);
							objMarketModel.setMarketName(marketName);
							objMarketModel.setMigrtedSiteCount(newUNYMigratedsitesCount);
							objMarketModel.setTotalCount(totalUNYSites);
							objMarketModel.setPercenatgeOfMigrated(percentagemigratedUNY);

							// for vlsm
							MarketModel objVlsmMarketModel = new MarketModel();
							long vlsmCountTotUny = objCountsList.stream()
									.filter(x -> ("YES".equalsIgnoreCase(x.getVlsm())
											&& "Migrated".equalsIgnoreCase(x.getStatus())
											&& marketName.equalsIgnoreCase(x.getMarket())))
									.count();

							objVlsmMarketModel.setMarketName(marketName);
							objVlsmMarketModel.setTotalCount(vlsmCountTotUny);

							// for carierAdds
							MarketModel objCarierAddsMarketModel = new MarketModel();
							long CarierCountTotUny = objCountsList.stream()
									.filter(x -> ("Carrier Add".equalsIgnoreCase(x.getStatus())
											&& marketName.equalsIgnoreCase(x.getMarket())))
									.count();

							objCarierAddsMarketModel.setMarketName(marketName);
							objCarierAddsMarketModel.setTotalCount(CarierCountTotUny);

							// for coversation
							MarketModel objcoversationMarketModel = new MarketModel();
							long coversationCountTotUny = objCountsList.stream()
									.filter(x -> ("Conversion".equalsIgnoreCase(x.getStatus())
											&& marketName.equalsIgnoreCase(x.getMarket())))
									.count();

							objcoversationMarketModel.setMarketName(marketName);
							objcoversationMarketModel.setTotalCount(coversationCountTotUny);

							objVlsmListModel.add(objVlsmMarketModel);
							objCarrirAddsListModel.add(objCarierAddsMarketModel);
							objConverAddsListModel.add(objcoversationMarketModel);
							objListModel.add(objMarketModel);

						}
					}

					objTotalCIReportModel.setObjSiteList(objListModel);

				}

			}

			// current Week Migrated Data
			List<SchedulingVerizonEntity> objCurrentWeekList = objCIReportRepository
					.getSchedVerListCurrentWeekForCIReports();

			/*
			 * objCountsList.stream() .filter(x ->
			 * "Week 11".equalsIgnoreCase(x.getWeek())).collect(Collectors.toList());
			 */

			if (objCurrentWeekList != null && objCurrentWeekList.size() > 0) {

				long newAllWeekMigratedsitesCount = objCurrentWeekList.stream()
						.filter(x -> ("Migrated".equalsIgnoreCase(x.getStatus()))
								&& ("New England".equalsIgnoreCase(x.getMarket())
										|| "Upstate New York".equalsIgnoreCase(x.getMarket())))
						.count();

				objWeekTotalCIReportModel.setName(" Total sites completed");
				objWeekTotalCIReportModel.setMigrtedSiteCount(newAllWeekMigratedsitesCount);

				Set<String> marketNamesWeek = objCurrentWeekList.stream().map(x -> x.getMarket())
						.collect(Collectors.toSet());
				List<MarketModel> objListModelWeek = new ArrayList<>();

				if (marketNamesWeek != null && marketNamesWeek.size() > 0) {
					for (String marketName : marketNamesWeek) {

						if ("New England".equalsIgnoreCase(marketName)) {
							MarketModel objMarketModel = new MarketModel();
							long newEnglandMigratedsitesCount = objCurrentWeekList.stream()
									.filter(x -> (marketName.equalsIgnoreCase(x.getMarket())
											&& "Migrated".equalsIgnoreCase(x.getStatus())))
									.count();
							objMarketModel.setMarketName(marketName);
							objMarketModel.setMigrtedSiteCount(newEnglandMigratedsitesCount);
							objListModelWeek.add(objMarketModel);

						}

						if ("Upstate New York".equalsIgnoreCase(marketName)) {
							MarketModel objMarketModel = new MarketModel();

							long newUNYMigratedsitesCount = objCurrentWeekList.stream()
									.filter(x -> (marketName.equalsIgnoreCase(x.getMarket())
											&& "Migrated".equalsIgnoreCase(x.getStatus())))
									.count();
							objMarketModel.setMarketName(marketName);
							objMarketModel.setMigrtedSiteCount(newUNYMigratedsitesCount);
							objListModelWeek.add(objMarketModel);

						}
					}

					objWeekTotalCIReportModel.setObjSiteList(objListModelWeek);

				}

			}

			objHashMapResult.put("overAllData", objTotalCIReportModel);
			objHashMapResult.put("currentWeekData", objWeekTotalCIReportModel);
			objHashMapResult.put("vlsmTotDetails", objVlsmListModel);
			objHashMapResult.put("crrierTotDetails", objCarrirAddsListModel);
			objHashMapResult.put("ConverTotDetails", objConverAddsListModel);

		} catch (Exception e) {
			logger.error("Exception in CIReportServiceImpl.getSchedulingVerizonEntityListForCIReportsFriday(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return objHashMapResult;
	}

	/**
	 * This api will getSchedulingVerizonEntityListForCIReportsPeriodFriday
	 * 
	 * @param fromDate,toDate
	 * @return Map<String, Object>
	 */
	@Override
	public Map<String, Object> getSchedulingVerizonEntityListForCIReportsPeriodFriday(String fromDate, String toDate) {

		Map<String, Object> objHashMapResult = new LinkedHashMap<>();
		TotalCIReportModel objTotalCIReportModel = new TotalCIReportModel();
		TotalCIReportModel objWeekTotalCIReportModel = new TotalCIReportModel();

		List<MarketModel> objVlsmListModel = new ArrayList<>();
		List<MarketModel> objCarrirAddsListModel = new ArrayList<>();
		List<MarketModel> objConverAddsListModel = new ArrayList<>();

		try {

			List<SchedulingVerizonEntity> objCountsList = objCIReportRepository
					.getSchedulingVerizonEntityListForCIReportsFriday();

			if (objCountsList != null && objCountsList.size() > 0) {
				long totalSites = 6400;
				long newAllMigratedsitesCount = objCountsList.stream()
						.filter(x -> ("Migrated".equalsIgnoreCase(x.getStatus()))
								&& ("New England".equalsIgnoreCase(x.getMarket())
										|| "Upstate New York".equalsIgnoreCase(x.getMarket())))
						.count();
				float percentageTotmigrated = (float) ((Double.valueOf(newAllMigratedsitesCount) * 100) / totalSites);

				objTotalCIReportModel.setName("Overall Sites Migrated");
				objTotalCIReportModel.setMigrtedSiteCount(newAllMigratedsitesCount);
				objTotalCIReportModel.setTotalCount(totalSites);
				objTotalCIReportModel.setPercenatgeOfMigrated(percentageTotmigrated);
				Set<String> marketNames = objCountsList.stream().map(x -> x.getMarket()).collect(Collectors.toSet());
				List<MarketModel> objListModel = new ArrayList<>();

				if (marketNames != null && marketNames.size() > 0) {
					for (String marketName : marketNames) {

						if ("New England".equalsIgnoreCase(marketName)) {
							MarketModel objMarketModel = new MarketModel();

							long totalEnglandSites = 4000;

							long newEnglandMigratedsitesCount = objCountsList.stream()
									.filter(x -> (marketName.equalsIgnoreCase(x.getMarket())
											&& "Migrated".equalsIgnoreCase(x.getStatus())))
									.count();
							float percentagemigratedEngland = (float) ((Double.valueOf(newEnglandMigratedsitesCount)
									* 100) / totalEnglandSites);
							objMarketModel.setMarketName(marketName);
							objMarketModel.setMigrtedSiteCount(newEnglandMigratedsitesCount);
							objMarketModel.setTotalCount(totalEnglandSites);
							objMarketModel.setPercenatgeOfMigrated(percentagemigratedEngland);

							// for vlsm
							MarketModel objVlsmMarketModel = new MarketModel();
							long vlsmCountTotNewEngland = objCountsList.stream()
									.filter(x -> ("YES".equalsIgnoreCase(x.getVlsm())
											&& "Migrated".equalsIgnoreCase(x.getStatus())
											&& marketName.equalsIgnoreCase(x.getMarket())))
									.count();

							objVlsmMarketModel.setMarketName(marketName);
							objVlsmMarketModel.setTotalCount(vlsmCountTotNewEngland);

							// for carierAdds
							MarketModel objCarierAddsMarketModel = new MarketModel();
							long CarierCountTotNewEngland = objCountsList.stream()
									.filter(x -> ("Carrier Add".equalsIgnoreCase(x.getStatus())
											&& marketName.equalsIgnoreCase(x.getMarket())))
									.count();

							objCarierAddsMarketModel.setMarketName(marketName);
							objCarierAddsMarketModel.setTotalCount(CarierCountTotNewEngland);

							// for coversation
							MarketModel objcoversationMarketModel = new MarketModel();
							long coversationCountTotNewEngland = objCountsList.stream()
									.filter(x -> ("Conversion".equalsIgnoreCase(x.getStatus())
											&& marketName.equalsIgnoreCase(x.getMarket())))
									.count();

							objcoversationMarketModel.setMarketName(marketName);
							objcoversationMarketModel.setTotalCount(coversationCountTotNewEngland);

							objVlsmListModel.add(objVlsmMarketModel);
							objCarrirAddsListModel.add(objCarierAddsMarketModel);
							objConverAddsListModel.add(objcoversationMarketModel);
							objListModel.add(objMarketModel);

						}

						if ("Upstate New York".equalsIgnoreCase(marketName)) {
							MarketModel objMarketModel = new MarketModel();

							long totalUNYSites = 2400;

							long newUNYMigratedsitesCount = objCountsList.stream()
									.filter(x -> (marketName.equalsIgnoreCase(x.getMarket())
											&& "Migrated".equalsIgnoreCase(x.getStatus())))
									.count();

							float percentagemigratedUNY = (float) ((Double.valueOf(newUNYMigratedsitesCount) * 100)
									/ totalUNYSites);
							objMarketModel.setMarketName(marketName);
							objMarketModel.setMigrtedSiteCount(newUNYMigratedsitesCount);
							objMarketModel.setTotalCount(totalUNYSites);
							objMarketModel.setPercenatgeOfMigrated(percentagemigratedUNY);

							// for vlsm
							MarketModel objVlsmMarketModel = new MarketModel();
							long vlsmCountTotUny = objCountsList.stream()
									.filter(x -> ("YES".equalsIgnoreCase(x.getVlsm())
											&& "Migrated".equalsIgnoreCase(x.getStatus())
											&& marketName.equalsIgnoreCase(x.getMarket())))
									.count();

							objVlsmMarketModel.setMarketName(marketName);
							objVlsmMarketModel.setTotalCount(vlsmCountTotUny);

							// for carierAdds
							MarketModel objCarierAddsMarketModel = new MarketModel();
							long CarierCountTotUny = objCountsList.stream()
									.filter(x -> ("Carrier Add".equalsIgnoreCase(x.getStatus())
											&& marketName.equalsIgnoreCase(x.getMarket())))
									.count();

							objCarierAddsMarketModel.setMarketName(marketName);
							objCarierAddsMarketModel.setTotalCount(CarierCountTotUny);

							// for coversation
							MarketModel objcoversationMarketModel = new MarketModel();
							long coversationCountTotUny = objCountsList.stream()
									.filter(x -> ("Conversion".equalsIgnoreCase(x.getStatus())
											&& marketName.equalsIgnoreCase(x.getMarket())))
									.count();

							objcoversationMarketModel.setMarketName(marketName);
							objcoversationMarketModel.setTotalCount(coversationCountTotUny);

							objVlsmListModel.add(objVlsmMarketModel);
							objCarrirAddsListModel.add(objCarierAddsMarketModel);
							objConverAddsListModel.add(objcoversationMarketModel);
							objListModel.add(objMarketModel);

						}
					}

					objTotalCIReportModel.setObjSiteList(objListModel);

				}

			}

			// current range Migrated Data
			List<SchedulingVerizonEntity> objCurrentWeekList = objCIReportRepository
					.getSchedVerListForCIReportsPeriod(fromDate, toDate);

			if (objCurrentWeekList != null && objCurrentWeekList.size() > 0) {

				long newAllWeekMigratedsitesCount = objCurrentWeekList.stream()
						.filter(x -> ("Migrated".equalsIgnoreCase(x.getStatus()))
								&& ("New England".equalsIgnoreCase(x.getMarket())
										|| "Upstate New York".equalsIgnoreCase(x.getMarket())))
						.count();

				objWeekTotalCIReportModel.setName(" Total Sites Migrated");
				objWeekTotalCIReportModel.setMigrtedSiteCount(newAllWeekMigratedsitesCount);

				Set<String> marketNamesWeek = objCurrentWeekList.stream().map(x -> x.getMarket())
						.collect(Collectors.toSet());
				List<MarketModel> objListModelWeek = new ArrayList<>();

				if (marketNamesWeek != null && marketNamesWeek.size() > 0) {
					for (String marketName : marketNamesWeek) {

						if ("New England".equalsIgnoreCase(marketName)) {
							MarketModel objMarketModel = new MarketModel();
							long newEnglandMigratedsitesCount = objCurrentWeekList.stream()
									.filter(x -> (marketName.equalsIgnoreCase(x.getMarket())
											&& "Migrated".equalsIgnoreCase(x.getStatus())))
									.count();
							objMarketModel.setMarketName(marketName);
							objMarketModel.setMigrtedSiteCount(newEnglandMigratedsitesCount);
							objListModelWeek.add(objMarketModel);

						}

						if ("Upstate New York".equalsIgnoreCase(marketName)) {
							MarketModel objMarketModel = new MarketModel();

							long newUNYMigratedsitesCount = objCurrentWeekList.stream()
									.filter(x -> (marketName.equalsIgnoreCase(x.getMarket())
											&& "Migrated".equalsIgnoreCase(x.getStatus())))
									.count();
							objMarketModel.setMarketName(marketName);
							objMarketModel.setMigrtedSiteCount(newUNYMigratedsitesCount);
							objListModelWeek.add(objMarketModel);

						}
					}

					objWeekTotalCIReportModel.setObjSiteList(objListModelWeek);

				}

			}

			objHashMapResult.put("overAllData", objTotalCIReportModel);
			objHashMapResult.put("currentWeekData", objWeekTotalCIReportModel);
			objHashMapResult.put("vlsmTotDetails", objVlsmListModel);
			objHashMapResult.put("crrierTotDetails", objCarrirAddsListModel);
			objHashMapResult.put("ConverTotDetails", objConverAddsListModel);

		} catch (Exception e) {
			logger.error("Exception in CIReportServiceImpl.getSchedulingVerizonEntityListForCIReportsPeriodFriday(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return objHashMapResult;
	}

	/**
	 * This api will getSchedulingSprintEntityListForCIReportsMonday
	 * 
	 * @return Map<String, Object>
	 */
	@Override
	public Map<String, Object> getSchedulingSprintEntityListForCIReportsMonday() {
		Map<String, Object> objHashMapResult = new LinkedHashMap<>();
		CIReportModelSprintNew objTotalCIReportModelCommercial = new CIReportModelSprintNew();
		CIReportModelSprintNew objTotalCIReportModelFit = new CIReportModelSprintNew();
		CIReportModelSprintNew objTotalCIReportModelOverall = new CIReportModelSprintNew();
		CIReportModelSprintNew objTotalCIReportModelweekWise = new CIReportModelSprintNew();
		try {
			List<SchedulingSprintEntity> objCountsList = objCIReportRepository
					.getSchedulingSprintEntityListForCIReportsMonday();

			if (objCountsList != null && objCountsList.size() > 0) {

				// for overall
				long totalsites = 1500;
				long Commersialsites = 1476;
				long totalFitSites = 24;
				long totalCentralSites = 1252;
				long totalWestSites = 224;

				// long totalMigrated=objCountsList.stream().filter(x ->
				// ("Migrated".equalsIgnoreCase(x.getStatus()))).count();

				Set<String> regoinsList = objCountsList.stream().map(x -> x.getRegion()).collect(Collectors.toSet());

				if (regoinsList != null && regoinsList.size() > 0) {
					/// commercial
					long newAllMigratedsitesCountCommercial = objCountsList.stream()
							.filter(x -> ("Migrated".equalsIgnoreCase(x.getStatus()))
									&& ("Central".equalsIgnoreCase(x.getRegion())
											|| "West".equalsIgnoreCase(x.getRegion())))
							.count();

					float percentageToOveralltmigrated = (float) ((Double.valueOf(newAllMigratedsitesCountCommercial)
							* 100) / totalsites);
					long remaingCommersial = Commersialsites - newAllMigratedsitesCountCommercial;
					objTotalCIReportModelCommercial.setMigrtedSiteCount(newAllMigratedsitesCountCommercial);
					objTotalCIReportModelCommercial.setPercenatgeOfMigrated(percentageToOveralltmigrated);
					objTotalCIReportModelCommercial.setTotalCount(Commersialsites);
					objTotalCIReportModelCommercial.setRemaining(remaingCommersial);

					/// Fit

					long newAllMigratedsitesCountFit = objCountsList.stream()
							.filter(x -> ("Migrated".equalsIgnoreCase(x.getStatus()))
									&& ("FIT".equalsIgnoreCase(x.getRegion())))
							.count();
					// hardcode
					newAllMigratedsitesCountFit = totalFitSites;

					float percentageToOveralltmigratedFit = (float) ((Double.valueOf(newAllMigratedsitesCountFit) * 100)
							/ totalFitSites);
					long remaingCommersialFit = totalFitSites - newAllMigratedsitesCountFit;
					objTotalCIReportModelFit.setMigrtedSiteCount(newAllMigratedsitesCountFit);
					objTotalCIReportModelFit.setPercenatgeOfMigrated(percentageToOveralltmigratedFit);
					objTotalCIReportModelFit.setTotalCount(totalFitSites);
					objTotalCIReportModelFit.setRemaining(remaingCommersialFit);

					// overall

					long newAllMigratedsitesCountOverAll = objCountsList.stream().filter(
							x -> ("Migrated".equalsIgnoreCase(x.getStatus())) && ("FIT".equalsIgnoreCase(x.getRegion())
									|| "Central".equalsIgnoreCase(x.getRegion())
									|| "West".equalsIgnoreCase(x.getRegion())))
							.count();
					// hard code
					newAllMigratedsitesCountOverAll = newAllMigratedsitesCountOverAll + newAllMigratedsitesCountFit;
					long newAllMigratedsitesCountOverAllInprogress = objCountsList.stream()
							.filter(x -> ("In Progress".equalsIgnoreCase(x.getStatus()))
									&& ("FIT".equalsIgnoreCase(x.getRegion())
											|| "Central".equalsIgnoreCase(x.getRegion())
											|| "West".equalsIgnoreCase(x.getRegion())))
							.count();

					long remaingOverAll = totalsites - newAllMigratedsitesCountOverAll
							- newAllMigratedsitesCountOverAllInprogress;
					objTotalCIReportModelOverall.setMigrtedSiteCount(newAllMigratedsitesCountOverAll);
					// objTotalCIReportModelCommercial.setPercenatgeOfMigrated(percentageToOveralltmigrated);
					objTotalCIReportModelOverall.setTotalCount(totalsites);
					objTotalCIReportModelOverall.setRemaining(remaingOverAll);
					objTotalCIReportModelOverall.setInProgress(newAllMigratedsitesCountOverAllInprogress);

					// for week

					List<SchedulingSprintEntity> objCurrentWeekList = objCIReportRepository
							.getSchedulingSprintEntityListForCIReportsMonday();

					long newAllMigratedsitesCountWeekMigrated = objCurrentWeekList.stream().filter(
							x -> ("Migrated".equalsIgnoreCase(x.getStatus())) && ("FIT".equalsIgnoreCase(x.getRegion())
									|| "Central".equalsIgnoreCase(x.getRegion())
									|| "West".equalsIgnoreCase(x.getRegion())))
							.count();

					long newAllMigratedsitesCountWeekCancel = objCurrentWeekList.stream().filter(
							x -> ("Cancelled".equalsIgnoreCase(x.getStatus())) && ("FIT".equalsIgnoreCase(x.getRegion())
									|| "Central".equalsIgnoreCase(x.getRegion())
									|| "West".equalsIgnoreCase(x.getRegion())))
							.count();

					objTotalCIReportModelweekWise.setMigrtedSiteCount(newAllMigratedsitesCountWeekMigrated);
					objTotalCIReportModelweekWise.setCancelation(newAllMigratedsitesCountWeekCancel);

					objHashMapResult.put("commercialData", objTotalCIReportModelCommercial);
					objHashMapResult.put("FitData", objTotalCIReportModelFit);
					objHashMapResult.put("overallData", objTotalCIReportModelOverall);
					objHashMapResult.put("WeekData", objTotalCIReportModelweekWise);
				}
			}

		} catch (Exception e) {
			logger.error("Exception in CIReportServiceImpl.getSchedulingSprintEntityListForCIReportsMonday(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}

		return objHashMapResult;

	}

	/**
	 * This api will getSchedulingSprintEntityListForCIReportsMondayPeriod
	 * 
	 * @param fromDate,toDate
	 * @return Map<String, Object>
	 */
	@Override
	public Map<String, Object> getSchedulingSprintEntityListForCIReportsMondayPeriod(String fromDate, String toDate) {
		Map<String, Object> objHashMapResult = new LinkedHashMap<>();
		CIReportModelSprintNew objTotalCIReportModelCommercial = new CIReportModelSprintNew();
		CIReportModelSprintNew objTotalCIReportModelFit = new CIReportModelSprintNew();
		CIReportModelSprintNew objTotalCIReportModelOverall = new CIReportModelSprintNew();
		CIReportModelSprintNew objTotalCIReportModelweekWise = new CIReportModelSprintNew();
		try {
			List<SchedulingSprintEntity> objCountsList = objCIReportRepository
					.getSchedulingSprintEntityListForCIReportsMonday();

			if (objCountsList != null && objCountsList.size() > 0) {

				// for overall
				long totalsites = 1500;
				long Commersialsites = 1476;
				long totalFitSites = 24;
				long totalCentralSites = 1252;
				long totalWestSites = 224;

				// long totalMigrated=objCountsList.stream().filter(x ->
				// ("Migrated".equalsIgnoreCase(x.getStatus()))).count();

				Set<String> regoinsList = objCountsList.stream().map(x -> x.getRegion()).collect(Collectors.toSet());

				if (regoinsList != null && regoinsList.size() > 0) {
					/// commercial
					long newAllMigratedsitesCountCommercial = objCountsList.stream()
							.filter(x -> ("Migrated".equalsIgnoreCase(x.getStatus()))
									&& ("Central".equalsIgnoreCase(x.getRegion())
											|| "West".equalsIgnoreCase(x.getRegion())))
							.count();

					float percentageToOveralltmigrated = (float) ((Double.valueOf(newAllMigratedsitesCountCommercial)
							* 100) / totalsites);
					long remaingCommersial = Commersialsites - newAllMigratedsitesCountCommercial;
					objTotalCIReportModelCommercial.setMigrtedSiteCount(newAllMigratedsitesCountCommercial);
					objTotalCIReportModelCommercial.setPercenatgeOfMigrated(percentageToOveralltmigrated);
					objTotalCIReportModelCommercial.setTotalCount(Commersialsites);
					objTotalCIReportModelCommercial.setRemaining(remaingCommersial);

					/// Fit

					long newAllMigratedsitesCountFit = objCountsList.stream()
							.filter(x -> ("Migrated".equalsIgnoreCase(x.getStatus()))
									&& ("FIT".equalsIgnoreCase(x.getRegion())))
							.count();
					// hardcode
					newAllMigratedsitesCountFit = totalFitSites;

					float percentageToOveralltmigratedFit = (float) ((Double.valueOf(newAllMigratedsitesCountFit) * 100)
							/ totalFitSites);
					long remaingCommersialFit = totalFitSites - newAllMigratedsitesCountFit;
					objTotalCIReportModelFit.setMigrtedSiteCount(newAllMigratedsitesCountFit);
					objTotalCIReportModelFit.setPercenatgeOfMigrated(percentageToOveralltmigratedFit);
					objTotalCIReportModelFit.setTotalCount(totalFitSites);
					objTotalCIReportModelFit.setRemaining(remaingCommersialFit);

					// overall

					long newAllMigratedsitesCountOverAll = objCountsList.stream().filter(
							x -> ("Migrated".equalsIgnoreCase(x.getStatus())) && ("FIT".equalsIgnoreCase(x.getRegion())
									|| "Central".equalsIgnoreCase(x.getRegion())
									|| "West".equalsIgnoreCase(x.getRegion())))
							.count();
					// hard code
					newAllMigratedsitesCountOverAll = newAllMigratedsitesCountOverAll + newAllMigratedsitesCountFit;
					long newAllMigratedsitesCountOverAllInprogress = objCountsList.stream()
							.filter(x -> ("In Progress".equalsIgnoreCase(x.getStatus()))
									&& ("FIT".equalsIgnoreCase(x.getRegion())
											|| "Central".equalsIgnoreCase(x.getRegion())
											|| "West".equalsIgnoreCase(x.getRegion())))
							.count();

					long remaingOverAll = totalsites - newAllMigratedsitesCountOverAll
							- newAllMigratedsitesCountOverAllInprogress;
					
					
					percentageToOveralltmigrated = (float) ((Double.valueOf(newAllMigratedsitesCountOverAll) * 100)
							/ totalsites);
					
					objTotalCIReportModelOverall.setMigrtedSiteCount(newAllMigratedsitesCountOverAll);
					objTotalCIReportModelOverall.setPercenatgeOfMigrated(percentageToOveralltmigrated);
					objTotalCIReportModelOverall.setTotalCount(totalsites);
					objTotalCIReportModelOverall.setRemaining(remaingOverAll);
					objTotalCIReportModelOverall.setInProgress(newAllMigratedsitesCountOverAllInprogress);

					// for week
					// for duration
					List<SchedulingSprintEntity> objCurrentWeekList = objCIReportRepository
							.getSchedsprListPeriodForCIReportsMonday(fromDate, toDate);
					/*
					 * List<SchedulingSprintEntity> objCurrentWeekList = objCIReportRepository
					 * .getSchedulingSprintEntityListForCIReportsMonday();
					 */

					long newAllMigratedsitesCountWeekMigrated = objCurrentWeekList.stream().filter(
							x -> ("Migrated".equalsIgnoreCase(x.getStatus())) && ("FIT".equalsIgnoreCase(x.getRegion())
									|| "Central".equalsIgnoreCase(x.getRegion())
									|| "West".equalsIgnoreCase(x.getRegion())))
							.count();

					long newAllMigratedsitesCountWeekCancel = objCurrentWeekList.stream().filter(
							x -> ("Cancelled".equalsIgnoreCase(x.getStatus())) && ("FIT".equalsIgnoreCase(x.getRegion())
									|| "Central".equalsIgnoreCase(x.getRegion())
									|| "West".equalsIgnoreCase(x.getRegion())))
							.count();

					objTotalCIReportModelweekWise.setMigrtedSiteCount(newAllMigratedsitesCountWeekMigrated);
					objTotalCIReportModelweekWise.setCancelation(newAllMigratedsitesCountWeekCancel);

					objHashMapResult.put("commercialData", objTotalCIReportModelCommercial);
					objHashMapResult.put("FitData", objTotalCIReportModelFit);
					objHashMapResult.put("overallData", objTotalCIReportModelOverall);
					objHashMapResult.put("WeekData", objTotalCIReportModelweekWise);
				}
			}

		} catch (Exception e) {
			logger.error("Exception in CIReportServiceImpl.getSchedulingSprintEntityListForCIReportsMondayPeriod(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}

		return objHashMapResult;

	}

	/**
	 * This api will getSchedulingSprintEntityListForCIReportsMondayPeriod
	 * 
	 * @param customerEntities
	 * @return Map<String, Object>
	 */
	@Override
	public Map<String, Object> getSchedulingDashBoardMonthly(List<CustomerEntity> customerEntities) {
		Map<String, Object> objHashMapResult = new LinkedHashMap<>();
		List<String> objMarketNames = new ArrayList<String>();
		List<NeCommissionModel> cirReportList = new ArrayList<NeCommissionModel>();

		LocalDate now = LocalDate.now(); // today Date
		try {

			List<String> labels = new ArrayList<>();
			labels.add("Previous Data");
			List<SchedulingVerizonEntity> vznList = objCIReportRepository.getSchedulingVerizonEntityListForMonthly();
			NeCommissionModel cirMont = new NeCommissionModel();
			List<String> data = new ArrayList<>();
			for (int i = 3; i >= 1; i++) {

				LocalDate earlier = now.minusMonths(i);
				String month = earlier.getMonth().toString().substring(0, 3);
				String year = String.valueOf(earlier.getYear());

				long totaDataMonthWise = vznList.stream()
						.filter(x -> (month.equalsIgnoreCase(x.getMonth()) && year.equalsIgnoreCase(x.getYear())))
						.count();
				long migraDataMonthWise = vznList
						.stream().filter(x -> (month.equalsIgnoreCase(x.getMonth())
								&& year.equalsIgnoreCase(x.getYear()) && "Migrated".equalsIgnoreCase(x.getStatus())))
						.count();
				long cancelDataMonthWise = vznList
						.stream().filter(x -> (month.equalsIgnoreCase(x.getMonth())
								&& year.equalsIgnoreCase(x.getYear()) && "Cancelled".equalsIgnoreCase(x.getStatus())))
						.count();
				long rollBackDataMonthWise = vznList
						.stream().filter(x -> (month.equalsIgnoreCase(x.getMonth())
								&& year.equalsIgnoreCase(x.getYear()) && "Rolled Back".equalsIgnoreCase(x.getStatus())))
						.count();
				long coversionDataMonthWise = vznList
						.stream().filter(x -> (month.equalsIgnoreCase(x.getMonth())
								&& year.equalsIgnoreCase(x.getYear()) && "Conversion".equalsIgnoreCase(x.getStatus())))
						.count();
				long carrierAddDataMonthWise = vznList
						.stream().filter(x -> (month.equalsIgnoreCase(x.getMonth())
								&& year.equalsIgnoreCase(x.getYear()) && "Carrier Add".equalsIgnoreCase(x.getStatus())))
						.count();

				labels.add(month);
			}

			// for sprint
			List<SchedulingSprintEntity> sprintList = objCIReportRepository.getSchedulingSprintEntityListForCIMonthly();
			for (int i = 1; i <= 3; i++) {

				LocalDate earlier = now.minusMonths(i);
				String month = earlier.getMonth().toString().substring(0, 3);
				String year = String.valueOf(earlier.getYear());

				long totaDataMonthWise = sprintList.stream()
						.filter(x -> (month.equalsIgnoreCase(x.getMonth()) && year.equalsIgnoreCase(x.getYear())))
						.count();
				long migraDataMonthWise = sprintList
						.stream().filter(x -> (month.equalsIgnoreCase(x.getMonth())
								&& year.equalsIgnoreCase(x.getYear()) && "Migrated".equalsIgnoreCase(x.getStatus())))
						.count();
				long cancelDataMonthWise = sprintList
						.stream().filter(x -> (month.equalsIgnoreCase(x.getMonth())
								&& year.equalsIgnoreCase(x.getYear()) && "Cancelled".equalsIgnoreCase(x.getStatus())))
						.count();

			}

		} catch (Exception e) {
			logger.error("Exception in CIReportServiceImpl.getSchedulingDashBoardMonthly(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return objHashMapResult;
	}

}
