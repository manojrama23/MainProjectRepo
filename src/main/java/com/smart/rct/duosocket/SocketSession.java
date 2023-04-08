package com.smart.rct.duosocket;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.smart.rct.constants.Constants;
import com.smart.rct.constants.XmlCommandsConstants;

import net.sf.expectit.Expect;
import net.sf.expectit.ExpectBuilder;
import net.sf.expectit.matcher.Matchers;

public class SocketSession {
	
	@Autowired
	final static Logger logger = LoggerFactory.getLogger(SocketSession.class);
	
	Session session = null;
	Channel channel = null;
	private String user;
	private String password;
	private String serverip;
	private AtomicBoolean isSessionInUse = new AtomicBoolean();
	public SocketSession(String user, String password, String serverip) {
		this.user = user;
		this.password = password;
		this.serverip = serverip;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject connectSession(String path, String userPrompt, String superUserPrompt) {
		JSONObject result = new JSONObject();
		StringBuilder expectOutput = new StringBuilder();
		try {
			JSch jsch = new JSch();
			this.session = jsch.getSession(this.user, this.serverip);
			this.session.setConfig("MaxAuthTries", "3");
			this.session.setPassword(this.password);
			this.session.setConfig("StrictHostKeyChecking", "no");
			this.session.connect(60 * 1000);
			this.channel = this.session.openChannel("shell");
			
			Expect expect = new ExpectBuilder().withInputs(this.channel.getInputStream()).withEchoInput(expectOutput)
					.withOutput(this.channel.getOutputStream()).withTimeout(3, TimeUnit.MINUTES).withExceptionOnFailure()
					.build();
			this.channel.connect();
			String[] pathList = path.split(",");
			String expectOutput1;
			for(int i=0; i<pathList.length; i++) {
				expectOutput1 = expect.expect(Matchers.regexp(userPrompt)).getBefore();
				System.out.println(userPrompt + "-------------" + pathList[i]);
				expect.sendLine(pathList[i]);
			}
			String match = "\\$ $|Tool]|lsm]";
			expectOutput1 = expect.expect(Matchers.regexp(match)).getBefore();
			System.out.println(expectOutput1);
			result.put("expectOutput", expectOutput.toString());
			expect.close();
			result.put("status", Constants.SUCCESS);
		} catch(Exception e){
			logger.error("Exception in connectSession() SocketSession : " + ExceptionUtils.getFullStackTrace(e));
			result.put("status", Constants.FAIL);
			if(e.getMessage()!=null) {
				if(e.getMessage().toUpperCase().contains("AUTH FAIL")) {
					result.put("reason", Constants.DUO_AUTHENTICATION_FAILURE);
				} else if(e.getMessage().toUpperCase().contains("JAVA.NET.UNKNOWNHOSTEXCEPTION")) {
					result.put("reason", Constants.DUO_UNKNOWN_HOST);
				} else {
					result.put("reason", e.getMessage());
				}
			} else {
				result.put("reason", "DUO SESSION IS NOT CONNECTED");
			}
			if(!expectOutput.toString().isEmpty()) {
				result.put("expectOutput", expectOutput.toString());
			}
		}
		return result;
	}
	
	//mmu audit
	@SuppressWarnings("unchecked")
	public JSONObject connectSessionMMU(String path, String userPrompt, String superUserPrompt) {
		JSONObject result = new JSONObject();
		StringBuilder expectOutput = new StringBuilder();
		try {
			JSch jsch = new JSch();
			this.session = jsch.getSession(this.user, this.serverip);
			this.session.setConfig("MaxAuthTries", "3");
			this.session.setPassword(this.password);
			this.session.setConfig("StrictHostKeyChecking", "no");
			this.session.connect(60 * 1000);
			this.channel = this.session.openChannel("shell");
			
			Expect expect = new ExpectBuilder().withInputs(this.channel.getInputStream()).withEchoInput(expectOutput)
					.withOutput(this.channel.getOutputStream()).withTimeout(3, TimeUnit.MINUTES).withExceptionOnFailure()
					.build();
			this.channel.connect();
			String[] pathList = path.split(",");
			String expectOutput1;
			for(int i=0; i<pathList.length; i++) {
				expectOutput1 = expect.expect(Matchers.regexp(userPrompt)).getBefore();
				System.out.println(userPrompt + "-------------" + pathList[i]);
				expect.sendLine(pathList[i]);
			}
			String abs = "\\$ $|Tool]|lsm]";
			expectOutput1 = expect.expect(Matchers.regexp(abs)).getBefore();
			System.out.println(expectOutput1);
			result.put("expectOutput", expectOutput.toString());
			expect.close();
			result.put("status", Constants.SUCCESS);
		} catch(Exception e){
			logger.error("Exception in connectSession() SocketSession : " + ExceptionUtils.getFullStackTrace(e));
			result.put("status", Constants.FAIL);
			if(e.getMessage()!=null) {
				if(e.getMessage().toUpperCase().contains("AUTH FAIL")) {
					result.put("reason", Constants.DUO_AUTHENTICATION_FAILURE);
				} else if(e.getMessage().toUpperCase().contains("JAVA.NET.UNKNOWNHOSTEXCEPTION")) {
					result.put("reason", Constants.DUO_UNKNOWN_HOST);
				} else {
					result.put("reason", e.getMessage());
				}
			} else {
				result.put("reason", "DUO SESSION IS NOT CONNECTED");
			}
			if(!expectOutput.toString().isEmpty()) {
				result.put("expectOutput", expectOutput.toString());
			}
		}
		return result;
	}
	
	
	
	
	public boolean disconnectSession() {
		boolean status = false;
		try {
			if(this.channel.isConnected()) {
				this.channel.disconnect();
			}
			if(this.session.isConnected()) {
				this.session.disconnect();
			}
			status = true;
		} catch(Exception e){
			logger.error("Exception in disconnectSession() SocketSession : " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}
	
	public boolean isConnectedSession() {
		boolean status = false;
		try {
			status = this.session.isConnected() && this.channel.isConnected();
		} catch(Exception e){
			logger.error("Exception in isConnectedSession() SocketSession : " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}
	
	public String runCommand(String command) {
		String result = "";
		StringBuilder expectOutput = new StringBuilder();
		try {
			
			Expect expect = new ExpectBuilder().withInputs(this.channel.getInputStream()).withEchoInput(expectOutput)
					.withOutput(this.channel.getOutputStream()).withTimeout(10, TimeUnit.MINUTES).withExceptionOnFailure()
					.build();
			expect.sendLine(command);
			String match = "\\$ $|Tool]|lsm]";
			String serverMessage = expect.expect(Matchers.regexp(match)).getBefore();
			result = expectOutput.toString();
			expect.close();
		} catch(Exception e){
			logger.error("Exception in runCommand() SocketSession : " + ExceptionUtils.getFullStackTrace(e));
			String expectResult = "";
			if(!expectOutput.toString().isEmpty()) {
				expectResult = expectOutput.toString() + "\n";
			}
			if(e.getMessage()!=null) {
				result = "DUO ERROR: " + "\n" + expectResult + e.getMessage();
			} else {
				result = "DUO ERROR: " + "\n" + expectResult + "Curl Command execution Failed";
			}
		}
		return result;
	}
	
	
	public String getFsuLog(String vsmUser, String vsmpassword, String fsuUserName, String fsuPassword, String vsmIp, String fsuIp,
			String expect1 , String expect2, String expect3, String expect4, long timeout, long expectDelay, String expect5, String command3,
			String command4) {
		String result = "";
		StringBuilder expectOutput = new StringBuilder();
		try {
			String command1 = "ssh -o StrictHostKeyChecking=no -o CheckHostIP=no " + vsmUser + "@" + vsmIp;
			String command2 = "ssh -o StrictHostKeyChecking=no -o CheckHostIP=no " + fsuUserName + "@" + fsuIp;
			Expect expect = new ExpectBuilder().withInputs(this.channel.getInputStream()).withEchoInput(expectOutput)
					.withOutput(this.channel.getOutputStream()).withTimeout(timeout, TimeUnit.SECONDS).withExceptionOnFailure()
					.build();
			expect.sendLine(command1);
			expect.expect(Matchers.regexp(expect1));
			TimeUnit.SECONDS.sleep(expectDelay);
			
			expect.sendLine(vsmpassword);
			expect.expect(Matchers.regexp(expect2));
			TimeUnit.SECONDS.sleep(expectDelay);
			
			expect.sendLine(command2);
			expect.expect(Matchers.regexp(expect3));
			TimeUnit.SECONDS.sleep(expectDelay);
			
			expect.sendLine(fsuPassword);			
			expect.expect(Matchers.regexp(expect4));
			TimeUnit.SECONDS.sleep(expectDelay);
			
			expect.send(command3 + "\r");
			expect.expect(Matchers.regexp(expect5));
			TimeUnit.SECONDS.sleep(expectDelay);
			
			expect.send(command4 + "\r");
			expect.expect(Matchers.regexp("\\$ $|Tool]|lsm]"));
			TimeUnit.SECONDS.sleep(expectDelay);
					
			result = expectOutput.toString();
			expect.close();
			logger.error("FSU Type Command : " + result);
		} catch(Exception e){
			logger.error("Exception in runCommand() SocketSession : " + ExceptionUtils.getFullStackTrace(e));
			String expectResult = "";
			this.disconnectSession();
			if(!expectOutput.toString().isEmpty()) {
				expectResult = expectOutput.toString() + "\n";
			}
			if(e.getMessage()!=null) {
				result = "DUO ERROR: " + "\n" + expectResult + e.getMessage();
			} else {
				result = "DUO ERROR: " + "\n" + expectResult + "Curl Command execution Failed";
			}
			logger.error("FSU Type Command : " + result);
		}
		return result;
	}
	
	//mmu audit 
	
	public String getMMU(String vsmUser, String vsmpassword, String mmuUserName, String mmuPassword, String vsmIp, String mmuIp,
			String expect1 , String expect2,String expect3,String expect4, long timeout, long expectDelay,
			String command3, String command4, String neid) {
		
		String result = "";
		StringBuilder expectOutput = new StringBuilder();
		
		try {
//			String command1 = "ssh -o StrictHostKeyChecking=no -o CheckHostIP=no " + vsmUser + "@" + vsmIp;
//			String command2 = "ssh -o StrictHostKeyChecking=no -o CheckHostIP=no " + mmuUserName + "@" +  mmuIp;
//			String command1 = vsmUser + "@" + vsmIp;
//			String command2 = mmuUserName + "@" + mmuIp;
			String command5 = "mmu-audit.sh" + " " + neid + " " + 0;
			String command6 = "mmu-audit.sh" + " " + neid + " " + 1;
			String command7 = "mmu-audit.sh" + " " + neid + " " + 2;
			String command8 = "exit";
			String command9 = "exit";
			String command10 = "x";
			String ab = "\\$ $|Tool]|lsm]";
			
			Expect expect = new ExpectBuilder().withInputs(this.channel.getInputStream()).withEchoInput(expectOutput)
					.withOutput(this.channel.getOutputStream()).withTimeout(timeout, TimeUnit.SECONDS).withExceptionOnFailure()
					.build();
						
//			expect.sendLine(command1);
//			expect.expect(Matchers.regexp(expect1));
//			TimeUnit.SECONDS.sleep(expectDelay);
//			
//			expect.sendLine(vsmpassword);
//			expect.expect(Matchers.regexp(expect2));
//			TimeUnit.SECONDS.sleep(expectDelay);
//			
//			expect.sendLine(command2);
//			expect.expect(Matchers.regexp(expect3));
//			TimeUnit.SECONDS.sleep(expectDelay);
//			
//			expect.sendLine(mmuPassword);			
//			expect.expect(Matchers.regexp(expect4));
//			TimeUnit.SECONDS.sleep(expectDelay);
			
//			expect.sendLine(command3 + "\r");			
//			expect.expect(Matchers.regexp(expect5));
//			TimeUnit.SECONDS.sleep(expectDelay);
			
//			expect.send(command4 + "\r");
//			expect.expect(Matchers.regexp("\\$ $"));
//			TimeUnit.SECONDS.sleep(expectDelay);
			
			expect.sendLine(command5);			
			expect.expect(Matchers.regexp(ab));
			TimeUnit.SECONDS.sleep(expectDelay);
			
			expect.sendLine(command6);			
			expect.expect(Matchers.regexp(ab));
			TimeUnit.SECONDS.sleep(expectDelay);
			
			expect.sendLine(command7);			
			expect.expect(Matchers.regexp(ab));
			TimeUnit.SECONDS.sleep(expectDelay);
			
//			expect.sendLine(command8);			
//			expect.expect(Matchers.regexp("\\$ $"));
//			TimeUnit.SECONDS.sleep(expectDelay);
//			
//			expect.sendLine(command9);			
//			expect.expect(Matchers.regexp("\\$ Selection: >"));
//			TimeUnit.SECONDS.sleep(expectDelay);
//			
//			expect.sendLine(command10);			
//			expect.expect(Matchers.regexp("\\$ #"));
//			TimeUnit.SECONDS.sleep(expectDelay);
//							
			result = expectOutput.toString();
			expect.close();
			logger.error("MMU Type Command : " + result);
			
		} catch(Exception e){
			logger.error("Exception in runCommand() SocketSession : " + ExceptionUtils.getFullStackTrace(e));
			String expectResult = "";
			this.disconnectSession();
			if(!expectOutput.toString().isEmpty()) {
				expectResult = expectOutput.toString() + "\n";
			}
			if(e.getMessage()!=null) {
				result = "DUO ERROR: " + "\n" + expectResult + e.getMessage();
			} else {
				result = "DUO ERROR: " + "\n" + expectResult + "Execution Failed";
			}
			logger.error("Command : " + result);
		}
		
		return result;
	}
	
	
	
	public boolean getIsSessionInUse() {
		return this.isSessionInUse.get();
	}
	
		public void setIsSessionInUse(boolean value) {
			this.isSessionInUse.set(value);
		}
	
		public String getRSSITest(String vsmUser, String vsmpassword, String rssiUserName, String rssiPassword, String vsmIp,
				String rssiIp, String expect1, String expect2, String expect3, String expect4, long timeout,
			long expectDelay, String command3, String command4, String neId, String fileName) {

		String result = "";
		StringBuilder expectOutput = new StringBuilder();

		try {
			String ab = "\\$ $|Tool]|lsm]";

			String radioU = "";

/*			if (!(newRadios.size() == size)) {
				for (int i = 0; i < newRadios.size() - 1; i++) {
					radioU = radioU + newRadios.get(i) + ",";
				}
				radioU = radioU + newRadios.get(newRadios.size() - 1);
			} else {
				radioU = "";
			}
			*/
			
			String command1 ;
			
			if(fileName == null) {
				fileName = "";
			}
			
			if(fileName.equals("")) {
				command1= "rssi_imbalance_tool.pl" + " " + vsmIp + " eNB_" + neId + " " + radioU;
			} else {
				command1= "rssi_imbalance_tool.pl" + " " + vsmIp + " eNB_" + neId + " " + fileName +" "+ radioU;
			}

			Expect expect = new ExpectBuilder().withInputs(this.channel.getInputStream()).withEchoInput(expectOutput)
					.withOutput(this.channel.getOutputStream()).withTimeout(timeout, TimeUnit.SECONDS)
					.withExceptionOnFailure().build();

			expect.sendLine(command1);
			expect.expect(Matchers.regexp(ab));
			TimeUnit.SECONDS.sleep(expectDelay);
			result = expectOutput.toString();
			expect.close();
			logger.error("RSSI Type Command : " + result);

		} catch (Exception e) {
			logger.error("Exception in runCommand() SocketSession : " + ExceptionUtils.getFullStackTrace(e));
			String expectResult = "";
			this.disconnectSession();
			if (!expectOutput.toString().isEmpty()) {
				expectResult = expectOutput.toString() + "\n";
			}
			if (e.getMessage() != null) {
				result = "DUO ERROR: " + "\n" + expectResult + e.getMessage();
			} else {
				result = expectResult + "Execution Failed";
			}
			logger.error("Command : " + result);
		}

		return result;

	}

	@SuppressWarnings("unchecked")
	public JSONObject connectSessionRSSI(String path, String userPrompt, String superUserPrompt) {
		JSONObject result = new JSONObject();
		StringBuilder expectOutput = new StringBuilder();
		try {
			JSch jsch = new JSch();
			this.session = jsch.getSession(this.user, this.serverip);
			this.session.setConfig("MaxAuthTries", "3");
			this.session.setPassword(this.password);
			this.session.setConfig("StrictHostKeyChecking", "no");
			this.session.connect(60 * 1000);
			this.channel = this.session.openChannel("shell");
			
			Expect expect = new ExpectBuilder().withInputs(this.channel.getInputStream()).withEchoInput(expectOutput)
					.withOutput(this.channel.getOutputStream()).withTimeout(3, TimeUnit.MINUTES).withExceptionOnFailure()
					.build();
			this.channel.connect();
			String[] pathList = path.split(",");
			String expectOutput1;
			for(int i=0; i<pathList.length; i++) {
				expectOutput1 = expect.expect(Matchers.regexp(userPrompt)).getBefore();
				System.out.println(userPrompt + "-------------" + pathList[i]);
				expect.sendLine(pathList[i]);
			}
			String abs = "\\$ $|Tool]|lsm]";
			expectOutput1 = expect.expect(Matchers.regexp(abs)).getBefore();
			System.out.println(expectOutput1);
			result.put("expectOutput", expectOutput.toString());
			expect.close();
			result.put("status", Constants.SUCCESS);
		} catch(Exception e){
			logger.error("Exception in connectSession() SocketSession : " + ExceptionUtils.getFullStackTrace(e));
			result.put("status", Constants.FAIL);
			if(e.getMessage()!=null) {
				if(e.getMessage().toUpperCase().contains("AUTH FAIL")) {
					result.put("reason", Constants.DUO_AUTHENTICATION_FAILURE);
				} else if(e.getMessage().toUpperCase().contains("JAVA.NET.UNKNOWNHOSTEXCEPTION")) {
					result.put("reason", Constants.DUO_UNKNOWN_HOST);
				} else {
					result.put("reason", e.getMessage());
				}
			} else {
				result.put("reason", "DUO SESSION IS NOT CONNECTED");
			}
			if(!expectOutput.toString().isEmpty()) {
				result.put("expectOutput", expectOutput.toString());
			}
		}
		return result;
	}

	public String getCbandMh1IpTest(String mH1CbandFilePath, String expect1, String expect2, long timeout,
			long expectDelay, String expect3, String exitCommand, String neId,String scriptName) {

		String result = "";
		StringBuilder expectOutput = new StringBuilder();

		try {
			if(neId.length()==10) {
				neId="0"+neId;
			}
			String command2="";
			if(scriptName.contains(XmlCommandsConstants.CBAND_VDU_MH1IPFETCH)) {
				command2	="cat"+" "+"/home/lsm/kubeconfig/CBAND/"+neId+".conf";
			}else if(scriptName.contains(XmlCommandsConstants.DSS_VDU_MH1IPFETCH)){
				command2	="cat"+" "+"/home/lsm/kubeconfig/DSS/"+neId+".conf";	
			}

			Expect expect = new ExpectBuilder().withInputs(this.channel.getInputStream()).withEchoInput(expectOutput)
					.withOutput(this.channel.getOutputStream()).withTimeout(timeout, TimeUnit.SECONDS)
					.withExceptionOnFailure().build();
			//cat /home/lsm/kubeconfig/CBAND/13392013057.conf
			/*expect.sendLine(mH1CbandFilePath);
			expect.expect(Matchers.regexp(expect1));
			TimeUnit.SECONDS.sleep(expectDelay);*/
			
			expect.sendLine(command2);
			expect.expect(Matchers.regexp(expect3));
			TimeUnit.SECONDS.sleep(expectDelay);
			
			/*expect.sendLine(exitCommand);
			expect.expect(Matchers.regexp(expect3));
			TimeUnit.SECONDS.sleep(expectDelay);*/
			
			result = expectOutput.toString();
			expect.close();
			logger.error("mh1 Type Command : " + result);

		} catch (Exception e) {
			logger.error("Exception in runCommand() SocketSession : " + ExceptionUtils.getFullStackTrace(e));
			String expectResult = "";
			this.disconnectSession();
			if (!expectOutput.toString().isEmpty()) {
				expectResult = expectOutput.toString() + "\n";
			}
			if (e.getMessage() != null) {
				result = "DUO ERROR: " + "\n" + expectResult + e.getMessage();
			} else {
				result = expectResult + "Execution Failed";
			}
			logger.error("Command : " + result);
		}

		return result;

	}
	
public String getCbandMMuAuditReportingOutput(String dBpath, long timeout, long expectDelay, String neId, String date, String cdPath) {
		
		String result = "";
		StringBuilder expectOutput = new StringBuilder();
		
		try {
			if(neId.length()==10) {
				neId="0"+neId;
			}
			
			String command1 = "grep"+" "+neId+" "+ cdPath +"MMU_Audit_Report_fh"+"0"+"_"+date+".csv";
			String command2 = "grep"+" "+neId+" "+ cdPath +"MMU_Audit_Report_fh"+"1"+"_"+date+".csv";
			String command3 = "grep"+" "+neId+" "+ cdPath +"MMU_Audit_Report_fh"+"2"+"_"+date+".csv";
			/*String command1 = "grep"+" "+neId+" "+"home/lsm/adi/"+date+"/Reports/MMU_Audit_Report_fh"+"0"+"_"+date+".csv";
			String command2 = "grep"+" "+neId+" "+"home/lsm/adi/"+date+"/Reports/MMU_Audit_Report_fh"+"1"+"_"+date+".csv";
			String command3 = "grep"+" "+neId+" "+"home/lsm/adi/"+date+"/Reports/MMU_Audit_Report_fh"+"2"+"_"+date+".csv";*/
			
			
			String ab = "\\$ $|Tool]|lsm]";
			
			Expect expect = new ExpectBuilder().withInputs(this.channel.getInputStream()).withEchoInput(expectOutput)
					.withOutput(this.channel.getOutputStream()).withTimeout(timeout, TimeUnit.SECONDS).withExceptionOnFailure()
					.build();
									
			expect.sendLine(command1);			
			expect.expect(Matchers.regexp(ab));
			TimeUnit.SECONDS.sleep(expectDelay);
			
			expect.sendLine(command2);			
			expect.expect(Matchers.regexp(ab));
			TimeUnit.SECONDS.sleep(expectDelay);
			
			
			expect.sendLine(command3);			
			expect.expect(Matchers.regexp(ab));
			TimeUnit.SECONDS.sleep(expectDelay);
								
			result = expectOutput.toString();
			expect.close();
			logger.error("MMU Type Command : " + result);
			
		} catch(Exception e){
			logger.error("Exception in runCommand() SocketSession : " + ExceptionUtils.getFullStackTrace(e));
			String expectResult = "";
			this.disconnectSession();
			if(!expectOutput.toString().isEmpty()) {
				expectResult = expectOutput.toString() + "\n";
			}
			if(e.getMessage()!=null) {
				result = "DUO ERROR: " + "\n" + expectResult + e.getMessage();
			} else {
				result = "DUO ERROR: " + "\n" + expectResult + "Execution Failed";
			}
			logger.error("Command : " + result);
		}
		
		return result;
	}
}