package com.smart.rct.common.serviceImpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.smart.rct.common.service.GenerateSystemReportService;
import com.smart.rct.constants.Constants;
import com.smart.rct.migration.serviceImpl.RunTestServiceImpl;
import com.smart.rct.util.FileUtil;
import com.smart.rct.util.LoadPropertyFiles;

@Service
public class GenerateSystemReportServiceImpl implements GenerateSystemReportService{

	final static Logger logger = LoggerFactory.getLogger(RunTestServiceImpl.class);
	
	@SuppressWarnings("unchecked")
	@Override
	public void generatesysReport() throws Exception {
		
		ExecutorService executorservice = Executors.newFixedThreadPool(1);
		executorservice.submit(() -> {
			while(true) {
				long timer  = 15 * 60 * 1000;
				//Output sar Directory
				String outputdir =  LoadPropertyFiles.getInstance().getProperty("BASE_PATH") + Constants.SEPARATOR + "SystemReport";
				File f = new File(outputdir);
				if(!f.exists()) {
					f.mkdir();
				}
				
				//For Sar bash file
				String sarsh = outputdir + Constants.SEPARATOR + "sar.sh";
				f = new File(sarsh);
				if(!f.exists()) {
					if(f.createNewFile()) {
						OutputStream os = new FileOutputStream(sarsh, true);
						String sarinput = "mpstat -P ALL\n";
						os.write(sarinput.getBytes(), 0, sarinput.length());
						sarinput = "sar -r 1 1";
						os.write(sarinput.getBytes(), 0, sarinput.length());
						os.close();
					}
				}
				
				//Timer File
				String sartimeFile = outputdir + Constants.SEPARATOR + "sarTimer.txt";
				f = new File(sartimeFile);
				if(!f.exists()) {
					if(f.createNewFile()) {
						JSONObject sartimeJson = new JSONObject();
						sartimeJson.put("intervalInMinutes", 15);
						OutputStream os = new FileOutputStream(sartimeFile, true);
						os.write(sartimeJson.toString().getBytes(), 0, sartimeJson.toString().length());
						os.close();
					}			
				} else {
					BufferedReader br = new BufferedReader(new FileReader(f));
					JSONObject sartimeJson = null;
					String st;
					while((st=br.readLine()) != null) {
						JSONParser parser = new JSONParser();
						try {
							sartimeJson = (JSONObject) parser.parse(st);
						} catch (Exception e) {
							logger.error("Exception in GenerateSystemReportServiceImpl.generatesysReport(): "
									+ ExceptionUtils.getFullStackTrace(e));
						}
					}
					br.close();
					if(sartimeJson != null) {
						timer = ((long) sartimeJson.get("intervalInMinutes")) * 60 * 1000;
					}
				}
				
				//Log Directory
				String logFiledir = outputdir + Constants.SEPARATOR + "Log";
				f = new File(logFiledir);
				if(!f.exists()) {
					f.mkdir();
				}
				
				String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
				String logFileName = logFiledir +  Constants.SEPARATOR + "SarReport_" + timeStamp + ".txt";
				f = new File(logFileName);
				if(f.exists()) {
					//Size in MB;
					if(f.length() > 1024*1024*10) {
						String backupLogFiledir = outputdir + Constants.SEPARATOR + "Backup";
						File backupfile = new File(backupLogFiledir);
						if(!backupfile.exists()) {
							backupfile.mkdir();
						}
						String timeStampt = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date());
						String backupLogFileName = backupLogFiledir +  Constants.SEPARATOR + "SarReport_" + timeStampt + ".txt";
						FileUtil.copyFileToLocation(logFileName, backupLogFileName);
						FileUtil.deleteFileOrFolder(logFileName);
					}
				}
				executeCommand(logFileName, sarsh);
				Thread.sleep(timer);
			}
		});
	}
	
	public void executeCommand(String outputFileName, String sarfile) {
		Process p = null;
		OutputStream os = null;
		try {
			String cmdExe = "sh " + sarfile;
			String[] cmdToExec = { "/bin/sh", "-c", cmdExe };
			Runtime.getRuntime().exec("chmod -R 777 " + cmdExe);


			p = Runtime.getRuntime().exec(cmdToExec);


			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			
			os = new FileOutputStream(outputFileName, true);
			String seperator = "-----------------------------------------------------------------------------------------------------------------------\n";
			os.write(seperator.getBytes(), 0, seperator.length());
			
			if (reader != null) {
				String line;
				while ((line = reader.readLine()) != null) {
					String lineData = line + "\n";

					String resultString = lineData.replaceAll("\\[\\dm", "").replaceAll("\\[[\\d;]*m", "")
							.replaceAll("\\u0007", "").replaceAll("\\u001B", "").replaceAll("\\]\\d;", "")
							.replaceAll("[*\\[]K", "");
					os.write(resultString.getBytes(), 0, resultString.length());
				}
			} 
			if (error != null) {
				String line;
				while ((line = error.readLine()) != null) {
					String lineData = line + "\n";

					String resultString = lineData.replaceAll("\\[\\dm", "").replaceAll("\\[[\\d;]*m", "")
							.replaceAll("\\u0007", "").replaceAll("\\u001B", "").replaceAll("\\]\\d;", "")
							.replaceAll("[*\\[]K", "");
					os.write(resultString.getBytes(), 0, resultString.length());
				}
			}
		} catch(Exception e){
			logger.error("Exception GenerateSystemReportServiceImpl in executeCommand() " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			if(os!=null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
