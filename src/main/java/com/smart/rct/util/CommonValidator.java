package com.smart.rct.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.smart.rct.common.models.CiqMapValuesModel;
import com.smart.rct.common.models.ErrorDisplayModel;
import com.smart.rct.common.models.ValidationTemplateColumnModel;
import com.smart.rct.premigration.models.CIQDetailsModel;
import com.smart.rct.premigration.serviceImpl.FileUploadServiceImpl;

@Component
public class CommonValidator {

	private static Logger logger = LoggerFactory.getLogger(FileUploadServiceImpl.class);

	public void validate(ValidationTemplateColumnModel objValTemColModel, CIQDetailsModel ciqDetailsModel,
			List<ErrorDisplayModel> objErrorMap) {
		LinkedHashMap<String, CiqMapValuesModel> ciqMap = ciqDetailsModel.getCiqMap();
		CiqMapValuesModel ciqMapValuesModel = ciqMap.get(objValTemColModel.getColumnName());

		// data type empty check
		if (StringUtils.isNotEmpty(objValTemColModel.getDataType())
				&& RegexConstant.IPV6_IPV4.equalsIgnoreCase(objValTemColModel.getDataType())) {
			String headerValue = ciqMapValuesModel.getHeaderValue();
			boolean validateIP = validateIP(headerValue);
			if (!validateIP) {
				String errMsg = objValTemColModel.getColumnName() + " of data:" + headerValue
						+ " failed IPV4/IPV6 validation";
				getErrorObject(ciqDetailsModel, headerValue, objValTemColModel.getColumnName(), errMsg, objErrorMap);
			}

		} else {
			String headerValue = ciqMapValuesModel.getHeaderValue();
			// value empty check
			if (StringUtils.isNotEmpty(headerValue)) {
				String dataType = objValTemColModel.getDataType();
				String errMsg = objValTemColModel.getColumnName() + " not satisfy the default validation";
				String regex = RegexConstant.DEFAULT_REGEX;
				if (RegexConstant.ALPHA.equalsIgnoreCase(dataType)) {
					regex = RegexConstant.ALPHA_REGEX;
					errMsg = objValTemColModel.getColumnName() + " should be only characters";
				} else if (RegexConstant.ALPHA_NUMERIC.equalsIgnoreCase(dataType)) {
					errMsg = objValTemColModel.getColumnName() + " should be only characters and numeric";
					regex = RegexConstant.ALPHAB_NUMERIC_REGEX;
				} else if (RegexConstant.NUMERIC.equalsIgnoreCase(dataType)) {
					errMsg = objValTemColModel.getColumnName() + " should be only numeric";
					regex = RegexConstant.NUMERIC_REGEX;
				} else if (RegexConstant.OTHER.equalsIgnoreCase(dataType)) {
					regex = RegexConstant.DEFAULT_REGEX;
				} else {
					// validation
					if (StringUtils.isNotEmpty(objValTemColModel.getRegexPattern())) {
						regex = objValTemColModel.getRegexPattern();
						errMsg = objValTemColModel.getColumnName() + " not satisfy the input validation:" + regex;
					}
				}

				Pattern pattren = Pattern.compile(regex);
				Matcher matcher = pattren.matcher(headerValue);
				boolean matches = matcher.matches();
				if (!matches) {
					getErrorObject(ciqDetailsModel, headerValue, objValTemColModel.getColumnName(), errMsg,
							objErrorMap);
				} else {
					if (!validateMaxLen(objValTemColModel, headerValue)) {
						errMsg = objValTemColModel.getColumnName() + " of data:" + headerValue + " should be lesser Than "
								+ objValTemColModel.getMaxLen();
						getErrorObject(ciqDetailsModel, headerValue, objValTemColModel.getColumnName(), errMsg,
								objErrorMap);
					} else if (!validateMinLen(objValTemColModel, headerValue)) {
						errMsg = objValTemColModel.getColumnName() + " of data:" + headerValue
								+ " should be greater Than " + objValTemColModel.getMinLen();
						getErrorObject(ciqDetailsModel, headerValue, objValTemColModel.getColumnName(), errMsg,
								objErrorMap);
					}
				}
			} else {
				if (StringUtils.isNotEmpty(objValTemColModel.getMandatory())
						&& "yes".equalsIgnoreCase(objValTemColModel.getMandatory())) {
					String errMsg = objValTemColModel.getColumnName() + " data should not be empty";
					getErrorObject(ciqDetailsModel, headerValue, objValTemColModel.getColumnName(), errMsg,
							objErrorMap);
				}
			}
		}

	}

	private void getErrorObject(CIQDetailsModel ciqDetailsModel, String headerValue, String columnName, String errorMsg,
			List<ErrorDisplayModel> objErrorMap) {
		ErrorDisplayModel objErrorDisplayModel = new ErrorDisplayModel();
		objErrorDisplayModel.setRowId(ciqDetailsModel.getSheetId());
		objErrorDisplayModel.setSheetName(ciqDetailsModel.getSheetName());
		objErrorDisplayModel.setSubSheetName(ciqDetailsModel.getSubSheetName());
		objErrorDisplayModel.setCellId(headerValue);
		objErrorDisplayModel.setEnbName(ciqDetailsModel.geteNBName());

		objErrorDisplayModel.setPropertyName(columnName);
		objErrorDisplayModel.setErrorMessage(errorMsg);
		objErrorMap.add(objErrorDisplayModel);
	}

	public boolean validateMinLen(ValidationTemplateColumnModel objValTemColModel, String value) {
		boolean flag = true;
		if (StringUtils.isNotEmpty(objValTemColModel.getMinLen())) {
			try {
				int minLen = Integer.parseInt(objValTemColModel.getMinLen());
				if (value.length() < minLen) {
					flag = false;
				}
			} catch (Exception e) {
				logger.error("Number parse exception:" + objValTemColModel.getMinLen());
			}
		}
		return flag;
	}

	public boolean validateMaxLen(ValidationTemplateColumnModel objValTemColModel, String value) {
		boolean flag = true;
		if (StringUtils.isNotEmpty(objValTemColModel.getMaxLen())) {
			try {
				int maxLen = Integer.parseInt(objValTemColModel.getMaxLen());
				if (value.length() > maxLen) {
					flag = false;
				}
			} catch (Exception e) {
				logger.error("Number parse exception:" + objValTemColModel.getMaxLen());
			}
		}
		return flag;
	}

	public boolean validdateEmpty(String data) {
		return StringUtils.isNotEmpty(data);
	}

	public boolean validateIP(String IP) {
		return InetAddressValidator.getInstance().isValid(IP);
	}
}
