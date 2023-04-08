package com.smart.rct.configuration;

import java.io.File;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeoutException;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.CustomerEntity;
import com.smart.rct.common.entity.ReportsEntity;
import com.smart.rct.common.models.CustomerDetailsModel;
import com.smart.rct.common.models.ProgramTemplateModel;
import com.smart.rct.common.service.CustomerService;
import com.smart.rct.constants.Constants;
import com.smart.rct.exception.RctException;
import com.smart.rct.postmigration.models.ReportsModel;
import com.smart.rct.postmigration.service.ReportsService;
import com.smart.rct.usermanagement.entity.UserSessionPool;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.EmailUtil;
import com.smart.rct.util.FileUtil;
import com.smart.rct.util.LoadPropertyFiles;

@Component
public class ScheduledConfig {

	@Autowired
	EmailUtil email;

	@Autowired
	CustomerService customerService;

	@Autowired
	ReportsService reportsService;

	/// Daily
	@Scheduled(cron = "0 7 * * * ?")
	public void Daily() {

		List<ProgramTemplateModel> configDetailModelListt = new ArrayList<ProgramTemplateModel>();
		String scheduleEnable = null;
		String scheduleTime = null;
		String scheduleFrequency = null;
		configDetailModelListt = customerService.getSnrConfigList(configDetailModelListt);
		for(ProgramTemplateModel template : configDetailModelListt) {
			if(template.getLabel().equals("SCHEDULE ENABLE")) {
				scheduleEnable = template.getValue();
			}if(template.getLabel().equals("SCHEDULE FREQUENCY")) {
				scheduleFrequency = template.getValue();
			}if(template.getLabel().equals("SCHEDULE TIME")) {
				scheduleTime = template.getValue();
			}
		}
		if (scheduleEnable!=null && scheduleEnable.equals("ON")) {
			if (scheduleFrequency.equals("Daily")) {
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
				Date now = new Date();
				String strDate = sdf.format(now);

				if (strDate.equals(scheduleTime)) {
					Calendar cal = Calendar.getInstance();
					DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");

					cal.add(Calendar.DATE, -1);

					List<CustomerDetailsEntity> programList = null;
					List<String> filter = new ArrayList<>();
					boolean status;
					ReportsModel reportsModel = new ReportsModel();
					List<CustomerEntity> custList = null;
					try {
						custList = customerService.getCustomerList(false, false);
						reportsModel.setFromDate(dateFormat.format(cal.getTime()));
						for (int i = 0; i < custList.size(); i++) {
							List<ProgramTemplateModel> configDetailModelList = new ArrayList<ProgramTemplateModel>();
							List<ProgramTemplateModel> configDetailList = new ArrayList<>();
							CustomerDetailsModel customerDetailsModel = new CustomerDetailsModel();
							CustomerEntity customerEntity = new CustomerEntity();
							if (!custList.get(i).getCustomerDetails().isEmpty())
								customerEntity.setId(custList.get(i).getCustomerDetails().get(0).getCustomerEntity()
										.getCustomerDetails().get(0).getCustomerEntity().getId());
							customerDetailsModel.setCustomerEntity(customerEntity);
							programList = customerService.getCustomerDetailsList(customerDetailsModel);
							configDetailModelList = customerService.getProgTemplateDetails(configDetailModelList,
									programList, "s&r");
							for (ProgramTemplateModel temp : configDetailModelList) {
								if (temp.getLabel().contains("MAIL_CONFIGURATION") && temp.getLabel().equals("MAIL_CONFIGURATION"))
									configDetailList.add(temp);
							}
							for (int j = 0; j < configDetailList.size(); j++) {
								if (!custList.get(i).getCustomerDetails().isEmpty()) {
									status = reportsService.getDetailsToCreateExcel(
											custList.get(i).getCustomerDetails().get(0).getCustomerEntity()
													.getCustomerDetails().get(0).getCustomerEntity().getId(),
											0, 0,
											configDetailList.get(j).getProgramDetailsEntity().getProgramName(),
											reportsModel, filter, "email");
									if (status) {
										if (configDetailList.get(j).getValue() != null) {
											sendEmail(configDetailList.get(j).getValue(),
													configDetailList.get(j).getProgramDetailsEntity().getProgramName());
										}
										else {
											System.out.println("No mail id configured");
										}
									} else {
										System.out.println("No Data Found to Send");
									}
								}
							}
						}
					} catch (Exception e) {

						e.printStackTrace();
					}
				}
				//System.out.println("Fixed Rate scheduler:: for Daily " + strDate);
			}
		}
	}

	public void sendEmail(String emailIds, String pgName) {
		StringBuilder filePath = new StringBuilder();
		String fileName = null;

		if (pgName.contains("MM")) {
			fileName = "/SRCT_Service_Delivery_Report_5G.xlsx";
		} else if (pgName.contains("USM-LIVE")) {
			fileName = "/SRCT_Service_Delivery_Report_4G.xlsx";
		} else if (pgName.contains("DSS")) {
			fileName = "/SRCT_Service_Delivery_Report_DSS.xlsx";
		} else if (pgName.contains("FSU")) {
			fileName = "/SRCT_Service_Delivery_Report_4G_FSU.xlsx";
		} else
			fileName = "";

		if (!fileName.equals("")) {

			filePath.append(LoadPropertyFiles.getInstance().getProperty(Constants.BASE_PATH))
					.append(Constants.OVERALL_REPORTS_DETAILS).append(fileName);

			String emailId = emailIds;
			File attachment = new File(filePath.toString());

			try {
				if (attachment.exists()) {
					String[] toList = emailId.split(",");
					StringBuilder bodyText = new StringBuilder();
					bodyText.append("Hi" + ",");
					bodyText.append("<br/><br/>");
					bodyText.append("Please find the attached Reports for " + pgName);
					bodyText.append("<br/><br/>");
					bodyText.append("Regards");
					bodyText.append("<br/>");
					bodyText.append("SMART Administrator");

					String subject = "Generated " + pgName + " REPORTS " + "";
					email.sendEmail(toList, null, null, subject, bodyText.toString(), attachment, attachment.getName(),
							true);
					if (CommonUtil.isValidObject(filePath)) {
						FileUtil.deleteFileOrFolder(filePath.toString());
						// FileUtil.deleteFileOrFolder(zipFilepath);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// Weekly
	@Scheduled(cron = "0 * * * * FRI")
	public void Weekly(){
		List<ProgramTemplateModel> configDetailModelListt = new ArrayList<ProgramTemplateModel>();
		String scheduleEnable = null;
		String scheduleTime = null;
		String scheduleFrequency = null;
		configDetailModelListt = customerService.getSnrConfigList(configDetailModelListt);
		for(ProgramTemplateModel template : configDetailModelListt) {
			if(template.getLabel().equals("SCHEDULE ENABLE")) {
				scheduleEnable = template.getValue();
			}if(template.getLabel().equals("SCHEDULE FREQUENCY")) {
				scheduleFrequency = template.getValue();
			}if(template.getLabel().equals("SCHEDULE TIME")) {
				scheduleTime = template.getValue();
			}
		}
		if (scheduleEnable!=null && scheduleEnable.equals("ON")) {
			if (scheduleFrequency.equals("Weekly")) {
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
				Date now = new Date();
				String strDate = sdf.format(now);

				if (strDate.equals(scheduleTime)) {
					Calendar cal = Calendar.getInstance();
					DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");

					cal.add(Calendar.DATE, -7);

					List<CustomerDetailsEntity> programList = null;
					List<String> filter = new ArrayList<>();
					boolean status;
					ReportsModel reportsModel = new ReportsModel();
					List<CustomerEntity> custList = null;
					try {
						custList = customerService.getCustomerList(false, false);
						reportsModel.setFromDate(dateFormat.format(cal.getTime()));
						for (int i = 0; i < custList.size(); i++) {
							List<ProgramTemplateModel> configDetailModelList = new ArrayList<ProgramTemplateModel>();
							List<ProgramTemplateModel> configDetailList = new ArrayList<>();
							CustomerDetailsModel customerDetailsModel = new CustomerDetailsModel();
							CustomerEntity customerEntity = new CustomerEntity();
							if (!custList.get(i).getCustomerDetails().isEmpty())
								customerEntity.setId(custList.get(i).getCustomerDetails().get(0).getCustomerEntity()
										.getCustomerDetails().get(0).getCustomerEntity().getId());
							customerDetailsModel.setCustomerEntity(customerEntity);
							programList = customerService.getCustomerDetailsList(customerDetailsModel);
							configDetailModelList = customerService.getProgTemplateDetails(configDetailModelList,
									programList, "s&r");
							for (ProgramTemplateModel temp : configDetailModelList) {
								if (temp.getLabel().contains("MAIL_CONFIGURATION") && temp.getLabel().equals("MAIL_CONFIGURATION"))
									configDetailList.add(temp);
							}
							for (int j = 0; j < configDetailList.size(); j++) {
								if (!custList.get(i).getCustomerDetails().isEmpty()) {
									status = reportsService.getDetailsToCreateExcel(
											custList.get(i).getCustomerDetails().get(0).getCustomerEntity()
													.getCustomerDetails().get(0).getCustomerEntity().getId(),
											0, 0,
											configDetailList.get(j).getProgramDetailsEntity().getProgramName(),
											reportsModel, filter, "email");
									if (status) {
										if (configDetailList.get(j).getValue() != null) {
											sendEmail(configDetailList.get(j).getValue(),
													configDetailList.get(j).getProgramDetailsEntity().getProgramName());
										}
										else {
											System.out.println("No mail id configured");
										}
									} else {
										System.out.println("No Data Found to Send");
									}
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();					}
				}
				//System.out.println("Fixed Rate scheduler:: for Weekly " + strDate);
			}
		}
	}

	// Monthly
	@Scheduled(cron = "0 * * 1 * ?")
	public void Monthly(){
		List<ProgramTemplateModel> configDetailModelListt = new ArrayList<ProgramTemplateModel>();
		String scheduleEnable = null;
		String scheduleTime = null;
		String scheduleFrequency = null;
		configDetailModelListt = customerService.getSnrConfigList(configDetailModelListt);
		for(ProgramTemplateModel template : configDetailModelListt) {
			if(template.getLabel().equals("SCHEDULE ENABLE")) {
				scheduleEnable = template.getValue();
			}if(template.getLabel().equals("SCHEDULE FREQUENCY")) {
				scheduleFrequency = template.getValue();
			}if(template.getLabel().equals("SCHEDULE TIME")) {
				scheduleTime = template.getValue();
			}
		}
		if (scheduleEnable!=null && scheduleEnable.equals("ON")) {
			if (scheduleFrequency.equals("Monthly")) {
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
				Date now = new Date();
				String strDate = sdf.format(now);

				if (strDate.equals(scheduleTime)) {
					Calendar cal = Calendar.getInstance();
					DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
					 //System.out.println("Today's date is "+dateFormat.format(cal.getTime()));

					cal.add(Calendar.DATE, -30);
					 //System.out.println("Yesterday's date was "+dateFormat.format(cal.getTime()));

					List<CustomerDetailsEntity> programList = null;
					List<String> filter = new ArrayList<>();
					boolean status;
					ReportsModel reportsModel = new ReportsModel();
					List<CustomerEntity> custList = null;
					custList = customerService.getCustomerList(false, false);
					reportsModel.setFromDate(dateFormat.format(cal.getTime()));
					try {
						for (int i = 0; i < custList.size(); i++) {
							List<ProgramTemplateModel> configDetailModelList = new ArrayList<ProgramTemplateModel>();
							CustomerDetailsModel customerDetailsModel = new CustomerDetailsModel();
							List<ProgramTemplateModel> configDetailList = new ArrayList<>();
							CustomerEntity customerEntity = new CustomerEntity();
							if (!custList.get(i).getCustomerDetails().isEmpty())
								customerEntity.setId(custList.get(i).getCustomerDetails().get(0).getCustomerEntity()
										.getCustomerDetails().get(0).getCustomerEntity().getId());
							customerDetailsModel.setCustomerEntity(customerEntity);
							programList = customerService.getCustomerDetailsList(customerDetailsModel);
							configDetailModelList = customerService.getProgTemplateDetails(configDetailModelList,
									programList, "s&r");
							for (ProgramTemplateModel temp : configDetailModelList) {
								if (temp.getLabel().contains("MAIL_CONFIGURATION") && temp.getLabel().equals("MAIL_CONFIGURATION"))
									configDetailList.add(temp);
							}
							for (int j = 0; j < configDetailList.size(); j++) {
								if (!custList.get(i).getCustomerDetails().isEmpty()) {
									status = reportsService.getDetailsToCreateExcel(
											custList.get(i).getCustomerDetails().get(0).getCustomerEntity()
													.getCustomerDetails().get(0).getCustomerEntity().getId(),
											0, 0,
											configDetailList.get(j).getProgramDetailsEntity().getProgramName(),
											reportsModel, filter, "email");
									if (status) {
										if (configDetailList.get(j).getValue() != null) {
											sendEmail(configDetailList.get(j).getValue(),
													configDetailList.get(j).getProgramDetailsEntity().getProgramName());
										}
										else {
											System.out.println("No mail id configured");
										}
									} else {
										System.out.println("No Data Found to Send");
									}
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();					}
				}
				//System.out.println("Fixed Rate scheduler:: for Monthly " + strDate);
			}
		}

	}
}