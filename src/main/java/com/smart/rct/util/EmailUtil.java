package com.smart.rct.util;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.smart.rct.exception.RctException;

@Component
public class EmailUtil {

	static final Logger logger = LoggerFactory.getLogger(EmailUtil.class);

	@Autowired
	private JavaMailSender sender;

	/**
	 * TO send updated password to corresponding email
	 * 
	 * @param toList
	 * @param ccList
	 * @param bccList
	 * @param subject
	 * @param bodyText
	 * @param html
	 * @return
	 * @throws MessagingException
	 * @throws FdtException
	 * @throws InterruptedException
	 * @throws TimeoutException
	 */
	
	public boolean sendEmail(String[] toList, String[] ccList, String[] bccList, String subject, String bodyText , boolean html) throws MessagingException, RctException, InterruptedException, TimeoutException {
		boolean status=false; 
		try { 
		 logger.info("sendEmail() toList:"+Arrays.toString(toList)+", ccList:"+ccList+", bccList:"+bccList+", subject:"+subject+", bodyText: "+bodyText);
		  MimeMessage message =sender.createMimeMessage();
		  MimeMessageHelper helper = new MimeMessageHelper(message);
		  helper.setTo(toList); 
		  if(ccList != null && ccList.length > 0){
			for (String cc : ccList) {
				helper.addCc(cc);
			}
		  }
		  if(bccList != null && bccList.length > 0){
			for (String bcc : bccList) {
				helper.addBcc(bcc);
			}
		  }
		  helper.setText(bodyText, html);
		  helper.setSubject(subject); 
		  sender.send(message);
		  status = true; 
		} catch (MessagingException e) { 
		  logger.error("Exception sendEmail ::::" + ExceptionUtils.getFullStackTrace(e));
		  status = false; 
		} 
	   return status;
	}
	
	
	
	public boolean sendEmail(String[] toList, String[] ccList, String[] bccList, String subject, String bodyText ,File attachment, String attachmentName, boolean html) throws MessagingException, RctException, InterruptedException, TimeoutException {
		boolean status=false; 
		try { 
		  logger.info("sendEmail() toList:"+Arrays.toString(toList)+", ccList:"+ccList+", bccList:"+bccList+", subject:"+subject+", bodyText: "+bodyText);
		  MimeMessage message =sender.createMimeMessage();
		  MimeMessageHelper helper = new MimeMessageHelper(message, true); 
		  helper.setTo(toList); 
		  if(ccList != null && ccList.length > 0){
			for (String cc : ccList) {
				helper.addCc(cc);
			}
		  }
		  if(bccList != null && bccList.length > 0){
			for (String bcc : bccList) {
				helper.addBcc(bcc);
			}
		  }
		  helper.setText(bodyText, html);
		  helper.setSubject(subject); 
		  if(attachment!=null){
			  helper.addAttachment(attachmentName, attachment);
		  }
		  sender.send(message);
		  status = true; 
		} catch (MessagingException e) { 
			logger.error("Exception sendEmail ::::" + ExceptionUtils.getFullStackTrace(e));
		  status = false; 
		} 
	   return status;
	}
}
