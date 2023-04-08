package com.smart.rct.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.smart.rct.constants.Constants;

@Component
public class DateUtil {

	final static Logger logger = LoggerFactory.getLogger(DateUtil.class);

	/**
	 * This method converts String format of Date to Date format.
	 * 
	 * @param pStrDate
	 * @param dateFormat
	 * @return Date format of passed string date for valid date format of string
	 *         and dateformat else it returns null.
	 * @throws ParseException
	 */
	public static Date stringToDate(String dateString, String dateFormat) {
		Date date = null;
		try {
			if (dateString != null && !"".equals(dateString)) {
				SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
				date = sdf.parse(dateString);
			}
		} catch (Exception e) {
			 logger.error("Exception stringToDate ::::" +ExceptionUtils.getFullStackTrace(e));
		}
		return date;
	}
	public static Date stringToDateEndTime(String dateString, String dateFormat) {
		Date date = null;
		try {
			if (dateString != null && !"".equals(dateString)) {
				SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
				date = sdf.parse(dateString);
				
				Calendar calendar = Calendar.getInstance();
			    calendar.setTime(date);
			    calendar.add(Calendar.HOUR, 23);
			    calendar.add(Calendar.MINUTE, 59);
			    calendar.add(Calendar.SECOND, 59);
			    date=calendar.getTime();
			}
		} catch (Exception e) {
			logger.error("Exception stringToDateEndTime ::::" +ExceptionUtils.getFullStackTrace(e));
		}
		return date;
	}


	/**
	 * This method converts Date to String format of Date.
	 * 
	 * @param date
	 * @param dateFormat
	 * @return string format of passed date for valid input of date and
	 *         dateformat else it returns an empty string.
	 */
	public static String dateToString(Date date, String dateFormat) {
		String dateString = null;
		try {
			if (date != null && !"".equals(date)) {
				SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
				dateString = sdf.format(date);
			}
		} catch (Exception e) {
			logger.error("Exception dateToString ::::" +ExceptionUtils.getFullStackTrace(e));
		}
		return dateString;
	}

	/**
	 * return data in string format
	 * 
	 * @param dateString
	 * @param dateStrFormat
	 * @param expectedDateStrFormat
	 * @return
	 */
	public static String getDateStringInFormat(String dateString, String dateStrFormat, String expectedDateStrFormat) {
		Date date = null;
		String dateStr = null;
		try {
			if (dateString != null && !"".equals(dateString)) {
				SimpleDateFormat sdf = new SimpleDateFormat(dateStrFormat);
				date = sdf.parse(dateString);
				dateStr = dateToString(date, expectedDateStrFormat);
			}
		} catch (Exception e) {
			logger.error("Exception getDateStringInFormat ::::" +ExceptionUtils.getFullStackTrace(e));
		}
		return dateStr;
	}

	/**
	 * This method converts date from given user timezone to UTC. It uses
	 * java.util.Calendar methods to convert dates and for handling Daylight
	 * saving time (DST) or Summer time.
	 * 
	 * @param date
	 * @param fromTimeZone
	 * @return converted date.
	 * @throws Exception
	 */
	public static Date convertDateFromUserTimeZonetoUTC(String dateTime, TimeZone fromTimeZone, String dateFormat)
			throws Exception {
		Date date = null;
		try {
			if (dateTime != null) {
				date = stringToDate(dateTime, dateFormat);
			} else {
				date = new Date();
			}
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeZone(TimeZone.getTimeZone(fromTimeZone.getID()));
			calendar.setTime(date);
			calendar.add(Calendar.MILLISECOND, fromTimeZone.getRawOffset() * -1);
			if (fromTimeZone.inDaylightTime(calendar.getTime())) {
				calendar.add(Calendar.MILLISECOND, calendar.getTimeZone().getDSTSavings() * -1);
			}
			date = calendar.getTime();
		} catch (Exception e) {
			logger.error("Exception convertDateFromUserTimeZonetoUTC ::::" +ExceptionUtils.getFullStackTrace(e));
		}
		return date;
	}

	/**
	 * This method converts Date in UTC timezone to Date for given timezone. It
	 * uses java.util.Calendar methods to convert dates and for handling
	 * Daylight saving time (DST)(also called as Summer time).
	 * 
	 * @param date
	 * @param toTimeZone
	 * @return converted date.
	 */
	public static Date convertDateFromUTCtoUserTimeZone(Date date, String timeZone) {
		Date userDate = null;
		try {
			if (date != null) {
				TimeZone toTimeZone = DateUtil.getUserTimeZone(timeZone);
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeZone(TimeZone.getTimeZone(Constants.GMT));
				calendar.setTime(date);
				calendar.add(Calendar.MILLISECOND, toTimeZone.getRawOffset());
				if (toTimeZone.inDaylightTime(calendar.getTime())) {
					calendar.add(Calendar.MILLISECOND, toTimeZone.getDSTSavings());
				}
				userDate = calendar.getTime();
			}
		} catch (Exception e) {
			logger.error("Exception convertDateFromUTCtoUserTimeZone ::::" +ExceptionUtils.getFullStackTrace(e));
		}
		return userDate;
	}

	/**
	 * This method will return the User defined Timezone object
	 * 
	 * @param timeZone
	 * @return
	 */
	public static TimeZone getUserTimeZone(String timeZone) {
		TimeZone userTime = null;
		try {
		String[] zoneIds = TimeZone.getAvailableIDs();
		for (int i = 0; i < zoneIds.length; i++) {
			String defaultTimeZone = zoneIds[i];
			if (timeZone.equals(defaultTimeZone)) {
				userTime = TimeZone.getTimeZone(zoneIds[i]);
				break;
			}
		}
		} catch (Exception e) {
			logger.error("Exception getUserTimeZone ::::" +ExceptionUtils.getFullStackTrace(e));
		}
		return userTime;
	}

	/**
	 * Check if a date is before Given date
	 * 
	 * @param dateBeforeStr
	 * @param dateAfterStr
	 * @param dateFormat
	 */
	public static boolean isDateBeforeGivenDate(String dateBeforeStr, String dateAfterStr, String dateFormat) {
		try {
			Date dateBefore = stringToDate(dateBeforeStr, dateFormat);
			Date dateAfter = stringToDate(dateAfterStr, dateFormat);
			return dateBefore.before(dateAfter);
		} catch (Exception e) {
			logger.error("Exception isDateBeforeGivenDate ::::" +ExceptionUtils.getFullStackTrace(e));
		}
		return false;
	}

	/**
	 * This Method convert Posix i.e date in milisecond to Date
	 * 
	 * @param dateInMilisecond
	 * @param fromTimeZone
	 * @param dateFormat
	 * @return
	 */
	public static Date convertPosixToUtcDate(String posixEpochString, TimeZone fromTimeZone, String dateFormat) {
		Date date = null;
		try {
			// Unix seconds
			long posixEpoch = Long.parseLong(posixEpochString);
			// convert seconds to milliseconds
			date = new Date(posixEpoch * 1000L);
			// format of the date
			SimpleDateFormat jdf = new SimpleDateFormat(dateFormat);
			jdf.setTimeZone(fromTimeZone);
			String dateInString = jdf.format(date);
			date = stringToDate(dateInString, dateFormat);

		} catch (Exception e) {
			logger.error("Exception convertPosixToUtcDate ::::" +ExceptionUtils.getFullStackTrace(e));
		}
		return date;
	}

	/**
	 * This Method returns Date in UTC
	 * 
	 * @return Date
	 */
	public static Date getCurrentDateUTC() {
		Date date = null;
		try {
			SimpleDateFormat dateFormatGmt = new SimpleDateFormat(Constants.YYYY_MM_DD_HH_MM_SS);
			dateFormatGmt.setTimeZone(TimeZone.getTimeZone(Constants.GMT));

			SimpleDateFormat dateFormatLocal = new SimpleDateFormat(Constants.YYYY_MM_DD_HH_MM_SS);
			date = dateFormatLocal.parse(dateFormatGmt.format(new Date()));
		} catch (Exception e) {
			logger.error("Exception getCurrentDateUTC ::::" +ExceptionUtils.getFullStackTrace(e));
		}
		return date;
	}

}
