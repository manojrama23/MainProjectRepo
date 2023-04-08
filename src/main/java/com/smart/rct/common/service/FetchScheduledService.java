package com.smart.rct.common.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.smart.rct.common.entity.ActiveUsersTracking;
import com.smart.rct.common.entity.AutoFecthTriggerEntity;
import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.CustomerEntity;
import com.smart.rct.common.entity.ProgramTemplateEntity;
import com.smart.rct.common.models.OvAutomationModel;
import com.smart.rct.common.models.ProgramTemplateModel;
import com.smart.rct.configuration.DailyOvScheduleConfig;
import com.smart.rct.constants.Constants;
import com.smart.rct.premigration.repository.FetchProcessRepository;
import com.smart.rct.premigration.service.FetchProcessService;
import com.smart.rct.usermanagement.entity.UserDetailsEntity;
import com.smart.rct.usermanagement.service.UserActionService;
import com.smart.rct.util.DateUtil;
import com.smart.rct.util.GlobalStatusMap;

@Service
public class FetchScheduledService {
	final static Logger logger = LoggerFactory.getLogger(FetchScheduledService.class);
	@Autowired
	FetchProcessRepository fetchProcessRepository;

	@Autowired
	FetchProcessService fetchProcessService;

	@Autowired
	OvScheduledTaskService ovScheduledTaskService;

	@Autowired
	DailyOvScheduleConfig dailyOvScheduleConfig;

	@Autowired
	CustomerService customerService;
	
	@Autowired
	ActiveUsersTrackingService as;
	
	@Autowired
	UserActionService userActionService;

	@EventListener(ApplicationReadyEvent.class)
	public void autoServerRestartCallusmLive() {
		AtomicBoolean statusFetch = new AtomicBoolean();
		String ovAutomation = null;
		String ovOverallInteraction = null;
		List<ProgramTemplateModel> configDetailModelList = new ArrayList<ProgramTemplateModel>();
		configDetailModelList = customerService.getOvTemplateDetails(configDetailModelList, "general");
		for (ProgramTemplateModel template : configDetailModelList) {
			if (template.getLabel().equals("OV AUTOMATION"))
				ovAutomation = template.getValue();
			if (template.getLabel().equals("OV OVERALL INTERACTION"))
				ovOverallInteraction = template.getValue();
		}
		if (ovAutomation != null && ovAutomation.equals("ON") && ovOverallInteraction!=null && ovOverallInteraction.equals("ON")) {
			CustomerDetailsEntity programmeEntity = fetchProcessRepository.getProgrammeDetails(Constants.USM_LIVE_4G);
			ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();
			programTemplateModel.setProgramDetailsEntity(programmeEntity);
			programTemplateModel.setLabel(Constants.FETCH_SCHEDULE);
			ProgramTemplateEntity programTemplateEntity = fetchProcessRepository
					.getFetchTimeProgaramTemplate(programTemplateModel);
			if (!ObjectUtils.isEmpty(programTemplateEntity)
					&& StringUtils.isNotEmpty(programTemplateEntity.getValue())) {

				if (!"OFF".equalsIgnoreCase(programTemplateEntity.getValue())) {

					AutoFecthTriggerEntity autoFecthTriggerEntity = ovScheduledTaskService
							.getAutoFetchDetails(Constants.USM_LIVE_4G);
					String toDayDate = DateUtil.dateToString(new Date(), Constants.YYYY_MM_DD);
					String slotData = programTemplateEntity.getValue();
					List<String> finalTimeSlots = Arrays.asList(slotData.replaceAll(":00", "").split(","));
					String currentHour = String.valueOf(new Date().getHours());
					if (!ObjectUtils.isEmpty(autoFecthTriggerEntity)
							&& toDayDate.equalsIgnoreCase(autoFecthTriggerEntity.getScheduledDate())
							&& StringUtils.isNotEmpty(autoFecthTriggerEntity.getScheduledHours())) {
						List<String> fetchedHoursList = Arrays
								.asList(autoFecthTriggerEntity.getScheduledHours().split(","));

						for (String Hours : finalTimeSlots) {
							if (!fetchedHoursList.contains(Hours)) {
								int scheduleHour = Integer.parseInt(Hours);// 7
								int presentHour = Integer.parseInt(currentHour);// 7.30 or 8
								if (scheduleHour <= presentHour) {
									fetchTriggerDetails(programmeEntity, Hours);
									statusFetch.getAndSet(true);
								}
							}

						}
					} else {
						for (String Hours : finalTimeSlots) {
							try {
								int scheduleHour = Integer.parseInt(Hours);
								int presentHour = Integer.parseInt(currentHour);
								if (scheduleHour <= presentHour) {
									fetchTriggerDetails(programmeEntity, Hours);
									statusFetch.getAndSet(true);
								}
							} catch (NumberFormatException e) {
								//logger.error("Exception in FetchScheduledService " + ExceptionUtils.getFullStackTrace(e));
							}


						}
					}

				}

			}

			if (statusFetch.get()) {
				JSONObject statusOfOv = fetchProcessService.getOvFetchDetails("OV- Auto Fetch",
						"Uploaded through Fetch Automation Functionality", Constants.USM_LIVE_4G,"");
				if (statusOfOv.containsKey("statusCode")
						&& "200".equalsIgnoreCase(statusOfOv.get("statusCode").toString())) {
					dailyOvScheduleConfig.OvScheduledTasksExcution(null, programmeEntity,"OV- Auto Fetch");
				}
			}
		}
	}

	@EventListener(ApplicationReadyEvent.class)
	public void autoServerRestartCallMMProgramme() {
		AtomicBoolean statusFetch = new AtomicBoolean();
		String ovAutomation = null;
		String ovOverallInteraction = null;
		// OvAutomationModel ovAutomationModel =
		// customerService.getOvAutomationTemplate();
		List<ProgramTemplateModel> configDetailModelList = new ArrayList<ProgramTemplateModel>();
		configDetailModelList = customerService.getOvTemplateDetails(configDetailModelList, "general");
		for (ProgramTemplateModel template : configDetailModelList) {
			if (template.getLabel().equals("OV AUTOMATION"))
				ovAutomation = template.getValue();
			if (template.getLabel().equals("OV OVERALL INTERACTION"))
				ovOverallInteraction = template.getValue();
		}
		if (ovAutomation != null && ovAutomation.equals("ON") && ovOverallInteraction!=null && ovOverallInteraction.equals("ON")) {
			CustomerDetailsEntity programmeEntity = fetchProcessRepository.getProgrammeDetails(Constants.MM_5G);
			ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();
			programTemplateModel.setProgramDetailsEntity(programmeEntity);
			programTemplateModel.setLabel(Constants.FETCH_SCHEDULE);
			ProgramTemplateEntity programTemplateEntity = fetchProcessRepository
					.getFetchTimeProgaramTemplate(programTemplateModel);
			if (!ObjectUtils.isEmpty(programTemplateEntity)
					&& StringUtils.isNotEmpty(programTemplateEntity.getValue())) {

				if (!"OFF".equalsIgnoreCase(programTemplateEntity.getValue())) {

					AutoFecthTriggerEntity autoFecthTriggerEntity = ovScheduledTaskService
							.getAutoFetchDetails(Constants.MM_5G);
					String toDayDate = DateUtil.dateToString(new Date(), Constants.YYYY_MM_DD);
					String slotData = programTemplateEntity.getValue();
					List<String> finalTimeSlots = Arrays.asList(slotData.replaceAll(":00", "").split(","));
					String currentHour = String.valueOf(new Date().getHours());
					if (!ObjectUtils.isEmpty(autoFecthTriggerEntity)
							&& toDayDate.equalsIgnoreCase(autoFecthTriggerEntity.getScheduledDate())
							&& StringUtils.isNotEmpty(autoFecthTriggerEntity.getScheduledHours())) {
						List<String> fetchedHoursList = Arrays
								.asList(autoFecthTriggerEntity.getScheduledHours().split(","));

						for (String Hours : finalTimeSlots) {
							if (!fetchedHoursList.contains(Hours)) {
								int scheduleHour = Integer.parseInt(Hours);// 7
								int presentHour = Integer.parseInt(currentHour);// 7.30 or 8
								if (scheduleHour <= presentHour) {
									fetchTriggerDetails(programmeEntity, Hours);
									statusFetch.getAndSet(true);
								}
							}

						}
					} else {
						for (String Hours : finalTimeSlots) {
							try {
								int scheduleHour = Integer.parseInt(Hours);
								int presentHour = Integer.parseInt(currentHour);
								if (scheduleHour <= presentHour) {
									fetchTriggerDetails(programmeEntity, Hours);
									statusFetch.getAndSet(true);
								}
							} catch(NumberFormatException e) {
								//logger.error("Exception in FetchScheduledService "+ ExceptionUtils.getFullStackTrace(e));
							}

						}
					}

				}

			}

			if (statusFetch.get()) {
				JSONObject statusOfOv = fetchProcessService.getOvFetchDetails("OV- Auto Fetch",
						"Uploaded through Fetch Automation Functionality", Constants.MM_5G,"");
				if (statusOfOv.containsKey("statusCode")
						&& "200".equalsIgnoreCase(statusOfOv.get("statusCode").toString())) {
					dailyOvScheduleConfig.OvScheduledTasksExcution(null, programmeEntity,"OV- Auto Fetch");
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Scheduled(cron = "0 0/15 0-23 * * *")
	public void schedulePreMigration() {
		
	}
	@SuppressWarnings("deprecation")
	@Scheduled(cron = "0 0/30 0-23 * * *")
	public void addActiveUsers() {
		List<UserDetailsEntity> userDetailsEntity = userActionService.getUserList(Constants.ACTIVE);
		ActiveUsersTracking au = new ActiveUsersTracking();
		au.setActiveUsers(userDetailsEntity.size());
		au.setActiveSessions(GlobalStatusMap.loginUsersDetails.size());
		au.setTimestamp(new Timestamp(System.currentTimeMillis()).toString());
		as.savedetail(au);
		try {
			logger.info("Test Controller");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		//return null;
		
	}

	@SuppressWarnings("deprecation")
	@Scheduled(cron = "0 0/15 0-23 * * *")
	public void fetchSchdueldJob4GUsm() {
		String ovAutomation = null;
		String ovOverallInteraction = null;
		// OvAutomationModel ovAutomationModel =
		// customerService.getOvAutomationTemplate();
		List<ProgramTemplateModel> configDetailModelList = new ArrayList<ProgramTemplateModel>();
		configDetailModelList = customerService.getOvTemplateDetails(configDetailModelList, "general");
		for (ProgramTemplateModel template : configDetailModelList) {
			if (template.getLabel().equals("OV AUTOMATION"))
				ovAutomation = template.getValue();
			if (template.getLabel().equals("OV OVERALL INTERACTION"))
				ovOverallInteraction = template.getValue();
		}
		if (ovAutomation != null && ovAutomation.equals("ON") && ovOverallInteraction!=null && ovOverallInteraction.equals("ON")) {
			CustomerDetailsEntity programmeEntity = fetchProcessRepository.getProgrammeDetails(Constants.USM_LIVE_4G);
			ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();
			programTemplateModel.setProgramDetailsEntity(programmeEntity);
			programTemplateModel.setLabel(Constants.FETCH_SCHEDULE);
			ProgramTemplateEntity programTemplateEntity = fetchProcessRepository
					.getFetchTimeProgaramTemplate(programTemplateModel);
			if (!ObjectUtils.isEmpty(programTemplateEntity)
					&& StringUtils.isNotEmpty(programTemplateEntity.getValue())) {

				if (!"OFF".equalsIgnoreCase(programTemplateEntity.getValue())) {
					String slotData = programTemplateEntity.getValue();

					//List<String> finalTimeSlots = Arrays.asList(slotData.replaceAll(":00", "").split(","));
					List<String> finalTimeSlots = Arrays.asList(slotData.split(","));
					String currentHour = String.valueOf(new Date().getHours());
					String currentMinute = String.valueOf(new Date().getMinutes());
					if(currentHour.equals("0")) {
						currentHour = "00";
					}
					if(currentMinute.equals("0")) {
						currentHour = currentHour + ":00";
					} else {
						currentHour = currentHour + ":" + currentMinute;
					}
					
					if (finalTimeSlots.contains(currentHour)) {
						fetchTriggerDetails(programmeEntity, currentHour);
						JSONObject statusOfOv = fetchProcessService.getOvFetchDetails("OV- Auto Fetch",
								"Uploaded through Fetch Automation Functionality", Constants.USM_LIVE_4G,"");
						if (statusOfOv.containsKey("statusCode")
								&& "200".equalsIgnoreCase(statusOfOv.get("statusCode").toString())) {
							dailyOvScheduleConfig.OvScheduledTasksExcution(null, programmeEntity,"OV- Auto Fetch");
						}
					}

				}

			}
		}
	}

	@SuppressWarnings("deprecation")
	@Scheduled(cron = "0 0/15 0-23 * * *")
	public void fetchSchdueldJob5GUsm() {
		String ovAutomation = null;
		String ovOverallInteraction = null;
		// OvAutomationModel ovAutomationModel =
		// customerService.getOvAutomationTemplate();
		List<ProgramTemplateModel> configDetailModelList = new ArrayList<ProgramTemplateModel>();
		configDetailModelList = customerService.getOvTemplateDetails(configDetailModelList, "general");
		for (ProgramTemplateModel template : configDetailModelList) {
			if (template.getLabel().equals("OV AUTOMATION"))
				ovAutomation = template.getValue();
			if (template.getLabel().equals("OV OVERALL INTERACTION"))
				ovOverallInteraction = template.getValue();
		}
		if (ovAutomation != null && ovAutomation.equals("ON") && ovOverallInteraction!=null && ovOverallInteraction.equals("ON")) {
			CustomerDetailsEntity programmeEntity = fetchProcessRepository.getProgrammeDetails(Constants.MM_5G);
			ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();
			programTemplateModel.setProgramDetailsEntity(programmeEntity);
			programTemplateModel.setLabel(Constants.FETCH_SCHEDULE);
			ProgramTemplateEntity programTemplateEntity = fetchProcessRepository
					.getFetchTimeProgaramTemplate(programTemplateModel);
			if (!ObjectUtils.isEmpty(programTemplateEntity)
					&& StringUtils.isNotEmpty(programTemplateEntity.getValue())) {

				if (!"OFF".equalsIgnoreCase(programTemplateEntity.getValue())) {
					String slotData = programTemplateEntity.getValue();

					//List<String> finalTimeSlots = Arrays.asList(slotData.replaceAll(":00", "").split(","));
					List<String> finalTimeSlots = Arrays.asList(slotData.split(","));
					String currentHour = String.valueOf(new Date().getHours());
					String currentMinute = String.valueOf(new Date().getMinutes());
					if(currentMinute.equals("0")) {
						currentHour = currentHour + ":00";
					} else {
						currentHour = currentHour + ":" + currentMinute;
					}
					
					if (finalTimeSlots.contains(currentHour)) {
						fetchTriggerDetails(programmeEntity, currentHour);
						JSONObject statusOfOv = fetchProcessService.getOvFetchDetails("OV- Auto Fetch",
								"Uploaded through Fetch Automation Functionality", Constants.MM_5G,"");
						if (statusOfOv.containsKey("statusCode")
								&& "200".equalsIgnoreCase(statusOfOv.get("statusCode").toString())) {
							dailyOvScheduleConfig.OvScheduledTasksExcution(null, programmeEntity,"OV- Auto Fetch");
						}
					}

				}

			}
		}
	}

	@SuppressWarnings("deprecation")
	@Scheduled(cron = "0 0 0-23 * * *")
	public void fetchSchdueldJob5GDss() {
		String ovAutomation = null;
		String ovOverallInteraction = null;
		// OvAutomationModel ovAutomationModel =
		// customerService.getOvAutomationTemplate();
		List<ProgramTemplateModel> configDetailModelList = new ArrayList<ProgramTemplateModel>();
		configDetailModelList = customerService.getOvTemplateDetails(configDetailModelList, "general");
		for (ProgramTemplateModel template : configDetailModelList) {
			if (template.getLabel().equals("OV AUTOMATION"))
				ovAutomation = template.getValue();
			if (template.getLabel().equals("OV OVERALL INTERACTION"))
				ovOverallInteraction = template.getValue();
		}
		if (ovAutomation != null && ovAutomation.equals("ON") && ovOverallInteraction!=null && ovOverallInteraction.equals("ON")) {
			CustomerDetailsEntity programmeEntity = fetchProcessRepository.getProgrammeDetails(Constants.DSS_5G);
			ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();
			programTemplateModel.setProgramDetailsEntity(programmeEntity);
			programTemplateModel.setLabel(Constants.FETCH_SCHEDULE);
			ProgramTemplateEntity programTemplateEntity = fetchProcessRepository
					.getFetchTimeProgaramTemplate(programTemplateModel);
			if (!ObjectUtils.isEmpty(programTemplateEntity)
					&& StringUtils.isNotEmpty(programTemplateEntity.getValue())) {

				if (!"OFF".equalsIgnoreCase(programTemplateEntity.getValue())) {
					String slotData = programTemplateEntity.getValue();

					List<String> finalTimeSlots = Arrays.asList(slotData.replaceAll(":00", "").split(","));

					String currentHour = String.valueOf(new Date().getHours());
					if (finalTimeSlots.contains(currentHour)) {
						fetchTriggerDetails(programmeEntity, currentHour);
						JSONObject statusOfOv = fetchProcessService.getOvFetchDetails("OV- Auto Fetch",
								"Uploaded through Fetch Automation Functionality", Constants.DSS_5G,"");
						if (statusOfOv.containsKey("statusCode")
								&& "200".equalsIgnoreCase(statusOfOv.get("statusCode").toString())) {
							dailyOvScheduleConfig.OvScheduledTasksExcution(null, programmeEntity,"OV- Auto Fetch");
						}
					}
				}

			}
		}
	}

	@SuppressWarnings("deprecation")
	@Scheduled(cron = "0 0/15 0-23 * * *")
	public void fetchSchdueldJob4GFsu() {
		String ovAutomation = null;
		String ovOverallInteraction = null;
		// OvAutomationModel ovAutomationModel =
		// customerService.getOvAutomationTemplate();
		List<ProgramTemplateModel> configDetailModelList = new ArrayList<ProgramTemplateModel>();
		configDetailModelList = customerService.getOvTemplateDetails(configDetailModelList, "general");
		for (ProgramTemplateModel template : configDetailModelList) {
			if (template.getLabel().equals("OV AUTOMATION"))
				ovAutomation = template.getValue();
			if (template.getLabel().equals("OV OVERALL INTERACTION"))
				ovOverallInteraction = template.getValue();
		}
		if (ovAutomation != null && ovAutomation.equals("ON") && ovOverallInteraction!=null && ovOverallInteraction.equals("ON")) {
			CustomerDetailsEntity programmeEntity = fetchProcessRepository.getProgrammeDetails(Constants.FSU_4G);
			ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();
			programTemplateModel.setProgramDetailsEntity(programmeEntity);
			programTemplateModel.setLabel(Constants.FETCH_SCHEDULE);
			ProgramTemplateEntity programTemplateEntity = fetchProcessRepository
					.getFetchTimeProgaramTemplate(programTemplateModel);
			if (!ObjectUtils.isEmpty(programTemplateEntity)
					&& StringUtils.isNotEmpty(programTemplateEntity.getValue())) {

				if (!"OFF".equalsIgnoreCase(programTemplateEntity.getValue())) {
					String slotData = programTemplateEntity.getValue();

					//List<String> finalTimeSlots = Arrays.asList(slotData.replaceAll(":00", "").split(","));
					List<String> finalTimeSlots = Arrays.asList(slotData.split(","));
					String currentHour = String.valueOf(new Date().getHours());
					String currentMinute = String.valueOf(new Date().getMinutes());
					if(currentMinute.equals("0")) {
						currentHour = currentHour + ":00";
					} else {
						currentHour = currentHour + ":" + currentMinute;
					}

					if (finalTimeSlots.contains(currentHour)) {
						fetchTriggerDetails(programmeEntity, currentHour);
						JSONObject statusOfOv = fetchProcessService.getOvFetchDetails("OV- Auto Fetch",
								"Uploaded through Fetch Automation Functionality", Constants.FSU_4G,"");
						if (statusOfOv.containsKey("statusCode")
								&& "200".equalsIgnoreCase(statusOfOv.get("statusCode").toString())) {
							dailyOvScheduleConfig.OvScheduledTasksExcution(null, programmeEntity,"OV- Auto Fetch");
						}
					}
				}

			}
		}
	}

	@SuppressWarnings("deprecation")
	@Scheduled(cron = "0 0 0-23 * * *")
	public void fetchSchdueldJobCband() {
		String ovAutomation = null;
		String ovOverallInteraction = null;
		// OvAutomationModel ovAutomationModel =
		// customerService.getOvAutomationTemplate();
		List<ProgramTemplateModel> configDetailModelList = new ArrayList<ProgramTemplateModel>();
		configDetailModelList = customerService.getOvTemplateDetails(configDetailModelList, "general");
		for (ProgramTemplateModel template : configDetailModelList) {
			if (template.getLabel().equals("OV AUTOMATION"))
				ovAutomation = template.getValue();
			if (template.getLabel().equals("OV OVERALL INTERACTION"))
				ovOverallInteraction = template.getValue();
		}
		if (ovAutomation != null && ovAutomation.equals("ON") && ovOverallInteraction!=null && ovOverallInteraction.equals("ON")) {
			CustomerDetailsEntity programmeEntity = fetchProcessRepository.getProgrammeDetails(Constants.CBAND_5G);
			ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();
			programTemplateModel.setProgramDetailsEntity(programmeEntity);
			programTemplateModel.setLabel(Constants.FETCH_SCHEDULE);
			ProgramTemplateEntity programTemplateEntity = fetchProcessRepository
					.getFetchTimeProgaramTemplate(programTemplateModel);
			if (!ObjectUtils.isEmpty(programTemplateEntity)
					&& StringUtils.isNotEmpty(programTemplateEntity.getValue())) {

				if (!"OFF".equalsIgnoreCase(programTemplateEntity.getValue())) {
					String slotData = programTemplateEntity.getValue();

					List<String> finalTimeSlots = Arrays.asList(slotData.replaceAll(":00", "").split(","));

					String currentHour = String.valueOf(new Date().getHours());
					if (finalTimeSlots.contains(currentHour)) {
						fetchTriggerDetails(programmeEntity, currentHour);
						JSONObject statusOfOv = fetchProcessService.getOvFetchDetails("OV- Auto Fetch",
								"Uploaded through Fetch Automation Functionality", Constants.CBAND_5G,"");
						if (statusOfOv.containsKey("statusCode")
								&& "200".equalsIgnoreCase(statusOfOv.get("statusCode").toString())) {
							dailyOvScheduleConfig.OvScheduledTasksExcution(null, programmeEntity,"OV- Auto Fetch");
						}
					}
				}

			}
		}
	}

	public synchronized void fetchTriggerDetails(CustomerDetailsEntity programmeEntity, String scheduleHour) {
		try {
			AutoFecthTriggerEntity autoFecthTriggerEntity = ovScheduledTaskService
					.getAutoFetchDetails(programmeEntity.getProgramName());
			String toDayDate = DateUtil.dateToString(new Date(), Constants.YYYY_MM_DD);
			if (!ObjectUtils.isEmpty(autoFecthTriggerEntity)) {
				if (toDayDate.equalsIgnoreCase(autoFecthTriggerEntity.getScheduledDate())) {
					String scheduledHoursDetails = autoFecthTriggerEntity.getScheduledHours();
					if (StringUtils.isNotEmpty(scheduledHoursDetails)) {
						StringBuilder builder = new StringBuilder();
						builder.append(scheduledHoursDetails);
						builder.append(",");
						builder.append(scheduleHour);
						autoFecthTriggerEntity.setScheduledHours(builder.toString());
						ovScheduledTaskService.mergeAutoFetchDetails(autoFecthTriggerEntity);

					}
				} else {
					autoFecthTriggerEntity.setScheduledHours(scheduleHour);
					ovScheduledTaskService.mergeAutoFetchDetails(autoFecthTriggerEntity);
				}

			} else {
				AutoFecthTriggerEntity autonewFecthTriggerEntity = new AutoFecthTriggerEntity();
				autonewFecthTriggerEntity.setCustomerDetailsEntity(programmeEntity);
				autonewFecthTriggerEntity.setScheduledDate(toDayDate);
				autonewFecthTriggerEntity.setScheduledHours(scheduleHour);
				ovScheduledTaskService.mergeAutoFetchDetails(autonewFecthTriggerEntity);
			}
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}

	}

	@EventListener(ApplicationReadyEvent.class)
	public void autoServerRestartCallDssProgramme() {
		AtomicBoolean statusFetch = new AtomicBoolean();
		String ovAutomation = null;
		String ovOverallInteraction = null;
		// OvAutomationModel ovAutomationModel =
		// customerService.getOvAutomationTemplate();
		List<ProgramTemplateModel> configDetailModelList = new ArrayList<ProgramTemplateModel>();
		configDetailModelList = customerService.getOvTemplateDetails(configDetailModelList, "general");
		for (ProgramTemplateModel template : configDetailModelList) {
			if (template.getLabel().equals("OV AUTOMATION"))
				ovAutomation = template.getValue();
			if (template.getLabel().equals("OV OVERALL INTERACTION"))
				ovOverallInteraction = template.getValue();
		}
		if (ovAutomation != null && ovAutomation.equals("ON") && ovOverallInteraction!=null && ovOverallInteraction.equals("ON")) {
			CustomerDetailsEntity programmeEntity = fetchProcessRepository.getProgrammeDetails(Constants.DSS_5G);
			ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();
			programTemplateModel.setProgramDetailsEntity(programmeEntity);
			programTemplateModel.setLabel(Constants.FETCH_SCHEDULE);
			ProgramTemplateEntity programTemplateEntity = fetchProcessRepository
					.getFetchTimeProgaramTemplate(programTemplateModel);
			if (!ObjectUtils.isEmpty(programTemplateEntity)
					&& StringUtils.isNotEmpty(programTemplateEntity.getValue())) {

				if (!"OFF".equalsIgnoreCase(programTemplateEntity.getValue())) {

					AutoFecthTriggerEntity autoFecthTriggerEntity = ovScheduledTaskService
							.getAutoFetchDetails(Constants.DSS_5G);
					String toDayDate = DateUtil.dateToString(new Date(), Constants.YYYY_MM_DD);
					String slotData = programTemplateEntity.getValue();
					List<String> finalTimeSlots = Arrays.asList(slotData.replaceAll(":00", "").split(","));
					String currentHour = String.valueOf(new Date().getHours());
					if (!ObjectUtils.isEmpty(autoFecthTriggerEntity)
							&& toDayDate.equalsIgnoreCase(autoFecthTriggerEntity.getScheduledDate())
							&& StringUtils.isNotEmpty(autoFecthTriggerEntity.getScheduledHours())) {
						List<String> fetchedHoursList = Arrays
								.asList(autoFecthTriggerEntity.getScheduledHours().split(","));

						for (String Hours : finalTimeSlots) {
							if (!fetchedHoursList.contains(Hours)) {
								int scheduleHour = Integer.parseInt(Hours);// 7
								int presentHour = Integer.parseInt(currentHour);// 7.30 or 8
								if (scheduleHour <= presentHour) {
									fetchTriggerDetails(programmeEntity, Hours);
									statusFetch.getAndSet(true);
								}
							}

						}
					} else {
						for (String Hours : finalTimeSlots) {
							int scheduleHour = Integer.parseInt(Hours);
							int presentHour = Integer.parseInt(currentHour);
							if (scheduleHour <= presentHour) {
								fetchTriggerDetails(programmeEntity, Hours);
								statusFetch.getAndSet(true);
							}

						}
					}

				}

			}

			if (statusFetch.get()) {
				JSONObject statusOfOv = fetchProcessService.getOvFetchDetails("OV- Auto Fetch",
						"Uploaded through Fetch Automation Functionality", Constants.DSS_5G,"");
				if (statusOfOv.containsKey("statusCode")
						&& "200".equalsIgnoreCase(statusOfOv.get("statusCode").toString())) {
					dailyOvScheduleConfig.OvScheduledTasksExcution(null, programmeEntity,"OV- Auto Fetch");
				}
			}
		}
	}

	@EventListener(ApplicationReadyEvent.class)
	public void autoServerRestartCallFsuProgramme() {
		AtomicBoolean statusFetch = new AtomicBoolean();
		String ovAutomation = null;
		String ovOverallInteraction = null;
		// OvAutomationModel ovAutomationModel =
		// customerService.getOvAutomationTemplate();
		List<ProgramTemplateModel> configDetailModelList = new ArrayList<ProgramTemplateModel>();
		configDetailModelList = customerService.getOvTemplateDetails(configDetailModelList, "general");
		for (ProgramTemplateModel template : configDetailModelList) {
			if (template.getLabel().equals("OV AUTOMATION"))
				ovAutomation = template.getValue();
			if (template.getLabel().equals("OV OVERALL INTERACTION"))
				ovOverallInteraction = template.getValue();
		}
		if (ovAutomation != null && ovAutomation.equals("ON") && ovOverallInteraction!=null && ovOverallInteraction.equals("ON")) {
			CustomerDetailsEntity programmeEntity = fetchProcessRepository.getProgrammeDetails(Constants.FSU_4G);
			ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();
			programTemplateModel.setProgramDetailsEntity(programmeEntity);
			programTemplateModel.setLabel(Constants.FETCH_SCHEDULE);
			ProgramTemplateEntity programTemplateEntity = fetchProcessRepository
					.getFetchTimeProgaramTemplate(programTemplateModel);
			if (!ObjectUtils.isEmpty(programTemplateEntity)
					&& StringUtils.isNotEmpty(programTemplateEntity.getValue())) {

				if (!"OFF".equalsIgnoreCase(programTemplateEntity.getValue())) {

					AutoFecthTriggerEntity autoFecthTriggerEntity = ovScheduledTaskService
							.getAutoFetchDetails(Constants.FSU_4G);
					String toDayDate = DateUtil.dateToString(new Date(), Constants.YYYY_MM_DD);
					String slotData = programTemplateEntity.getValue();
					List<String> finalTimeSlots = Arrays.asList(slotData.replaceAll(":00", "").split(","));
					String currentHour = String.valueOf(new Date().getHours());
					if (!ObjectUtils.isEmpty(autoFecthTriggerEntity)
							&& toDayDate.equalsIgnoreCase(autoFecthTriggerEntity.getScheduledDate())
							&& StringUtils.isNotEmpty(autoFecthTriggerEntity.getScheduledHours())) {
						List<String> fetchedHoursList = Arrays
								.asList(autoFecthTriggerEntity.getScheduledHours().split(","));

						for (String Hours : finalTimeSlots) {
							if (!fetchedHoursList.contains(Hours)) {
								int scheduleHour = Integer.parseInt(Hours);// 7
								int presentHour = Integer.parseInt(currentHour);// 7.30 or 8
								if (scheduleHour <= presentHour) {
									fetchTriggerDetails(programmeEntity, Hours);
									statusFetch.getAndSet(true);
								}
							}

						}
					} else {
						for (String Hours : finalTimeSlots) {
							int scheduleHour = Integer.parseInt(Hours);
							int presentHour = Integer.parseInt(currentHour);
							if (scheduleHour <= presentHour) {
								fetchTriggerDetails(programmeEntity, Hours);
								statusFetch.getAndSet(true);
							}

						}
					}

				}

			}

			if (statusFetch.get()) {
				JSONObject statusOfOv = fetchProcessService.getOvFetchDetails("OV- Auto Fetch",
						"Uploaded through Fetch Automation Functionality", Constants.FSU_4G,"");
				if (statusOfOv.containsKey("statusCode")
						&& "200".equalsIgnoreCase(statusOfOv.get("statusCode").toString())) {
					dailyOvScheduleConfig.OvScheduledTasksExcution(null, programmeEntity,"OV- Auto Fetch");
				}
			}
		}
	}

	@EventListener(ApplicationReadyEvent.class)
	public void autoServerRestartCallCbandProgramme() {
		AtomicBoolean statusFetch = new AtomicBoolean();
		String ovAutomation = null;
		String ovOverallInteraction = null;
		// OvAutomationModel ovAutomationModel =
		// customerService.getOvAutomationTemplate();
		List<ProgramTemplateModel> configDetailModelList = new ArrayList<ProgramTemplateModel>();
		configDetailModelList = customerService.getOvTemplateDetails(configDetailModelList, "general");
		for (ProgramTemplateModel template : configDetailModelList) {
			if (template.getLabel().equals("OV AUTOMATION"))
				ovAutomation = template.getValue();
			if (template.getLabel().equals("OV OVERALL INTERACTION"))
				ovOverallInteraction = template.getValue();
		}
		if (ovAutomation != null && ovAutomation.equals("ON") && ovOverallInteraction!=null && ovOverallInteraction.equals("ON")) {
			CustomerDetailsEntity programmeEntity = fetchProcessRepository.getProgrammeDetails(Constants.CBAND_5G);
			ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();
			programTemplateModel.setProgramDetailsEntity(programmeEntity);
			programTemplateModel.setLabel(Constants.FETCH_SCHEDULE);
			ProgramTemplateEntity programTemplateEntity = fetchProcessRepository
					.getFetchTimeProgaramTemplate(programTemplateModel);
			if (!ObjectUtils.isEmpty(programTemplateEntity)
					&& StringUtils.isNotEmpty(programTemplateEntity.getValue())) {

				if (!"OFF".equalsIgnoreCase(programTemplateEntity.getValue())) {

					AutoFecthTriggerEntity autoFecthTriggerEntity = ovScheduledTaskService
							.getAutoFetchDetails(Constants.CBAND_5G);
					String toDayDate = DateUtil.dateToString(new Date(), Constants.YYYY_MM_DD);
					String slotData = programTemplateEntity.getValue();
					List<String> finalTimeSlots = Arrays.asList(slotData.replaceAll(":00", "").split(","));
					String currentHour = String.valueOf(new Date().getHours());
					if (!ObjectUtils.isEmpty(autoFecthTriggerEntity)
							&& toDayDate.equalsIgnoreCase(autoFecthTriggerEntity.getScheduledDate())
							&& StringUtils.isNotEmpty(autoFecthTriggerEntity.getScheduledHours())) {
						List<String> fetchedHoursList = Arrays
								.asList(autoFecthTriggerEntity.getScheduledHours().split(","));

						for (String Hours : finalTimeSlots) {
							if (!fetchedHoursList.contains(Hours)) {
								int scheduleHour = Integer.parseInt(Hours);// 7
								int presentHour = Integer.parseInt(currentHour);// 7.30 or 8
								if (scheduleHour <= presentHour) {
									fetchTriggerDetails(programmeEntity, Hours);
									statusFetch.getAndSet(true);
								}
							}

						}
					} else {
						for (String Hours : finalTimeSlots) {
							int scheduleHour = Integer.parseInt(Hours);
							int presentHour = Integer.parseInt(currentHour);
							if (scheduleHour <= presentHour) {
								fetchTriggerDetails(programmeEntity, Hours);
								statusFetch.getAndSet(true);
							}

						}
					}

				}

			}

			if (statusFetch.get()) {
				JSONObject statusOfOv = fetchProcessService.getOvFetchDetails("OV- Auto Fetch",
						"Uploaded through Fetch Automation Functionality", Constants.CBAND_5G,"");
				if (statusOfOv.containsKey("statusCode")
						&& "200".equalsIgnoreCase(statusOfOv.get("statusCode").toString())) {
					dailyOvScheduleConfig.OvScheduledTasksExcution(null, programmeEntity,"OV- Auto Fetch");
				}
			}
		}
	}
}