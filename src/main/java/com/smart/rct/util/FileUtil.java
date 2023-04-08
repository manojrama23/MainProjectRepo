package com.smart.rct.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.codehaus.plexus.archiver.tar.TarEntry;
import org.codehaus.plexus.archiver.tar.TarInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.smart.rct.common.entity.NetworkConfigDetailsEntity;
import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.common.models.FetchDetailsModel;
import com.smart.rct.constants.Constants;
import com.smart.rct.exception.RctException;

public class FileUtil {

	private String dir = null;
	private List<String> fileList = new ArrayList<String>();
	private String fileName = null;

	final static Logger logger = LoggerFactory.getLogger(FileUtil.class);

	/**
	 * delete the file or folder
	 * 
	 * @param fileWithPath
	 * @return
	 */
	public static boolean deleteFileOrFolder(String fileWithPath) {

		boolean stat = false;
		try {
			File file = new File(fileWithPath);
			if (file.isDirectory()) {
				String[] files = file.list();
				for (String fle : files) {
					File currentFile = new File(file.getPath(), fle);
					if (currentFile.isDirectory()) {
						stat = deleteFileOrFolder(currentFile.toString());
					} else {
						stat = currentFile.delete();
					}
				}
				stat = file.delete();// delete folder once sub folder/files are
										// deleted
			} else {
				stat = file.delete();
			}
		} catch (Exception e) {
			logger.error("Handled - Exception in deleteFileOrFolder :" + ExceptionUtils.getMessage(e));
		}
		return stat;
	}

	/**
	 * return file directory
	 * 
	 * @param fileName
	 * @param partialFileDir
	 * @return
	 */
	public static String getFileDirectory(String fileName, String partialFileDir) {

		String dir = null;
		try {
			FileUtil fileUtil = new FileUtil();
			dir = fileUtil.getFileDir(fileName, partialFileDir);
		} catch (Exception e) {
			logger.error("Handled - Exception in getFileDirectory :" + ExceptionUtils.getMessage(e));
		}
		return dir;
	}

	private String getFileDir(String fileName, String partialFileDir) {

		try {
			File fileDir = new File(partialFileDir);
			File[] list = fileDir.listFiles();
			if (list != null) {
				for (File fle : list) {
					if (fle.isDirectory()) {
						dir = getFileDir(fileName, fle.toString());
					} else if (fileName.equalsIgnoreCase(fle.getName())) {
						return fle.getParentFile().getPath();
					}
				}
			}
		} catch (Exception e) {
			logger.error("Handled - Exception in getFileDir :" + ExceptionUtils.getMessage(e));
		}
		return dir;
	}

	/**
	 * Return list of file matching file name
	 * 
	 * @param fileName
	 * @param completeFileDir
	 * @return
	 */
	public static String getFilePathMatchingFileName(String fileName, String completeFileDir) {

		String filePath = null;
		try {
			File fileDir = new File(completeFileDir);
			File[] list = fileDir.listFiles();
			if (list != null) {
				for (File fle : list) {
					if (fle.getName().equals(fileName)) {
						return fle.getAbsolutePath();
					}
				}
			}
		} catch (Exception e) {
			logger.error("Handled - Exception in getFilePathMatchingFileName :" + ExceptionUtils.getMessage(e));
		}
		return filePath;
	}

	/**
	 * Return list of file matching file name
	 * 
	 * @param partialFileName
	 * @param completeFileDir
	 * @return
	 */
	// Return list of file matching file name
	public static List<String> getFilePathListMatchingFileName(String partialFileName, String completeFileDir) {

		List<String> filePathList = null;
		try {
			File fileDir = new File(completeFileDir);
			File[] list = fileDir.listFiles();
			filePathList = new ArrayList<String>();
			if (list != null) {
				for (File fle : list) {
					if (fle.getName().contains(partialFileName)) {
						filePathList.add(fle.getAbsolutePath());
					}
				}
			}
		} catch (Exception e) {
			logger.error("Handled - Exception in getFilePathMatchingFileName :" + ExceptionUtils.getMessage(e));
		}
		return filePathList;
	}

	/**
	 * copy file to given location
	 * 
	 * @param fileWithLocation
	 * @param newFileLocation
	 * @throws Exception
	 */
	public static void copyFileToLocation(String fileWithLocation, String newFileLocation) throws Exception {

		try {
			File sourceLocation = new File(fileWithLocation);
			File targetLocation = new File(newFileLocation);
			copyDirectory(sourceLocation, targetLocation);
		} catch (Exception e) {
			logger.error("Failed to copyFileToLocation  ::" + ExceptionUtils.getFullStackTrace(e));
			throw e;
		}
	}

	private static void copyDirectory(File sourceLocation, File targetLocation) throws Exception {
		InputStream in = null;
		OutputStream out = null;

		try {
			if (sourceLocation.isDirectory()) {
				if (!targetLocation.exists()) {
					targetLocation.mkdir();
				}
				String[] children = sourceLocation.list();
				for (int i = 0; i < children.length; i++) {
					copyDirectory(new File(sourceLocation, children[i]), new File(targetLocation, children[i]));
				}
			} else {

				in = new FileInputStream(sourceLocation);
				out = new FileOutputStream(targetLocation);

				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
			}
		} catch (Exception e) {
			logger.error("Failed to copyDirectory  ::" + ExceptionUtils.getFullStackTrace(e));

		} finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}
	}

	/**
	 * create folder if not exists
	 * 
	 * @param folderPath
	 * @throws RmtException
	 */
	public static void createFolder(String folderPath) throws RctException {

		try {
			File folder = new File(folderPath);
			if (!folder.exists()) {
				folder.mkdir();
			}
		} catch (Exception e) {
			logger.error("Failed to copyDirectory  ::" + ExceptionUtils.getFullStackTrace(e));
			throw e;
		}
	}

	/**
	 * return list of files
	 * 
	 * @param filePath
	 * @return
	 */
	public static List<String> getFileListFromDirectory(String filePath) {

		FileUtil fileUtil = new FileUtil();
		fileUtil.getFileListFromDir(filePath);
		return fileUtil.fileList;
	}

	// called from getFileListFromDirectory
	private void getFileListFromDir(String filePath) {

		try {
			File folder = new File(filePath);
			if (folder.isDirectory()) {
				File[] list = folder.listFiles();
				for (File file : list) {
					if (!file.isDirectory()) {
						fileList.add(file.toString());
					} else {
						getFileListFromDir(file.toString());
					}
				}
			}
		} catch (Exception e) {
			logger.error("Handled - Exception in getFileListFromDirectory :" + ExceptionUtils.getMessage(e));
		}
	}

	/**
	 * excludefileTypes - Files to be excluded Ex : "dat", "sh", ...
	 * 
	 * @param filePath
	 * @param excludefileTypes
	 * @return
	 */
	// excludefileTypes - Files to be excluded Ex : "dat", "sh", ...
	public static List<String> getFileListFromDirectory(String filePath, String[] excludefileTypes) {

		List<String> fileList = null;
		List<String> completefileList = null;
		List<String> excludefileList = null;
		try {
			completefileList = getFileListFromDirectory(filePath);
			if (excludefileTypes != null) {
				excludefileList = Arrays.asList(excludefileTypes);
				if (excludefileList.size() > 0) {
					fileList = new ArrayList<String>();
					for (String file : completefileList) {
						if (!excludefileList.contains(getFileExtension(file))) {
							fileList.add(file);
						}
					}
				} else {
					return completefileList;
				}
			} else {
				return completefileList;
			}
		} catch (Exception e) {
			logger.error("Handled - Exception in getFileListFromDirectory :" + ExceptionUtils.getMessage(e));
		}
		return fileList;
	}

	/**
	 * return file name without extension
	 * 
	 * @param completeFileName
	 * @return
	 */
	public static String getFileNameWithoutExtension(String completeFileName) {

		String fileName = null;
		try {
			if (completeFileName != null) {
				fileName = completeFileName.split("\\.(?=[^\\.]+$)")[0];
			}
		} catch (Exception e) {
			logger.error("Failed to getFileNameWithoutExtension ::" + ExceptionUtils.getFullStackTrace(e));
		}
		return fileName;
	}

	/**
	 * return string file extension
	 * 
	 * @param completeFileName
	 * @return
	 */
	public static String getFileExtension(String completeFileName) {

		String fileExt = null;
		try {
			if (completeFileName != null) {
				fileExt = completeFileName.split("\\.(?=[^\\.]+$)")[1];
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			logger.error("Handled - Exception in getFileExtension :" + ExceptionUtils.getMessage(e));
			return "";
		} catch (Exception e) {
			logger.error("Handled - Exception in getFileExtension :" + ExceptionUtils.getMessage(e));
		}
		return fileExt;
	}

	/**
	 * return filename
	 * 
	 * @param completeFilePath
	 * @return
	 */
	public static String getFileNameFromCompleteFilePath(String completeFilePath) {

		String fileName = null;
		try {
			if (completeFilePath != null) {
				String[] filePathArr = completeFilePath.split("/");
				fileName = filePathArr[filePathArr.length - 1];
			}
		} catch (Exception e) {
			logger.error("Handled - Exception in getFileNameFromCompletePath :" + ExceptionUtils.getMessage(e));
		}
		return fileName;
	}

	/**
	 * return filelists
	 * 
	 * @param completePathFileList
	 * @return
	 */
	public static List<String> getFileListWithoutCompletePath(List<String> completePathFileList) {

		List<String> fileList = null;
		try {
			fileList = new ArrayList<String>();
			for (String file : completePathFileList) {
				fileList.add(getFileNameFromCompleteFilePath(file));
			}
		} catch (Exception e) {
			logger.error("Handled - Exception in getFileListWithoutCompletePath :" + ExceptionUtils.getMessage(e));
		}
		return fileList;
	}

	/**
	 * 
	 * @param file
	 * @param uploadPath
	 * @throws RmtException
	 */
	public static void uploadMultipartFile(MultipartFile file, String uploadPath) throws RctException {

		OutputStream out = null;
		InputStream filecontent = null;
		try {
			String fileName = file.getOriginalFilename();
			out = new FileOutputStream(new File(uploadPath + fileName));
			filecontent = file.getInputStream();

			int read = 0;
			final byte[] bytes = new byte[1024];

			while ((read = filecontent.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}

			Runtime.getRuntime().exec("chmod -R 777 " + uploadPath);
		} catch (Exception e) {
			logger.error("Failed to uploadMultipartFile  ::" + ExceptionUtils.getFullStackTrace(e));
			throw new RctException("Failed to upload Multipart File(s) " + ExceptionUtils.getMessage(e));
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (filecontent != null) {
					filecontent.close();
				}
			} catch (IOException e) {
				logger.error("Exception in Finally", e);
			}
		}
	}

	/**
	 * rename the directory
	 * 
	 * @param sourcePath
	 * @param newSourcePath
	 * @return
	 */
	public static boolean renameDir(String sourcePath, String newSourcePath) {
		boolean status = false;
		try {
			File file = new File(sourcePath);
			if (file.isDirectory()) {
				File newfile = new File(newSourcePath);
				return file.renameTo(newfile);
			}
		} catch (Exception e) {
			logger.error("Exception renameDir :" + ExceptionUtils.getMessage(e));
		}
		return status;
	}

	/**
	 * compare the directory
	 * 
	 * @param dirName
	 * @param dirPath
	 * @return
	 */
	public static boolean hasMatchingDir(String dirName, String dirPath) {
		try {
			StringBuilder sb = new StringBuilder();
			String matchingDir = sb.append(dirPath).append(dirName).toString();
			File fileDir = new File(dirPath);
			File[] list = fileDir.listFiles();
			if (list != null) {
				for (File fle : list) {
					if (fle.isDirectory()) {
						if (fle.toString().equals(matchingDir)) {
							return true;
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("Handled - Exception in getFileDir :" + ExceptionUtils.getMessage(e));
		}
		return false;
	}

	/**
	 * create directory
	 * 
	 * @param dirPath
	 * @return
	 * @throws IOException
	 */
	public static boolean createDirectory(String dirPath) throws IOException {

		File destFolder = new File(dirPath);
		File parentDir = destFolder.getCanonicalFile();
		if (!parentDir.exists()) {
			File file = new File(dirPath);
			if (file.mkdirs()) {
				return true;
			}

		}
		return false;
	}

	/**
	 * return boolean value based on filename and dirpath
	 * 
	 * @param fileName
	 * @param dirPath
	 * @return
	 */
	public static boolean hasMatchingFile(String fileName, String dirPath) {
		try {
			File fileDir = new File(dirPath);
			File[] list = fileDir.listFiles();
			if (list != null) {
				for (File fle : list) {
					if (fle.isFile()) {
						if (fle.getName().equals(fileName)) {
							return true;
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("Handled - Exception in hasMatchingFile :" + ExceptionUtils.getMessage(e));
		}
		return false;
	}

	public static StringBuilder getBasePath() {
		StringBuilder baselruPath = new StringBuilder();
		String basePath = LoadPropertyFiles.getInstance().getProperty(Constants.BASE_PATH);
		// String lruExcelPath =
		// LoadPropertyFiles.getInstance().getProperty("LRU_DETAILS");
		baselruPath.setLength(0);
		baselruPath.append(basePath);
		// basePiPath.append(lruExcelPath);
		return baselruPath;
	}

	/**
	 * Private iterative method to find .txt file inside folder
	 * 
	 * @param zipFilePath
	 */
	private String findTextFile(String zipFilePath) {
		try {
			File textFilePath = new File(zipFilePath);
			File listOfFile[] = textFilePath.listFiles();

			for (File textFile : listOfFile) {
				if (!textFile.isDirectory()) {
					String fileNameUnderZip = textFile.getName();
					String fileExtension1 = fileNameUnderZip.substring(fileNameUnderZip.lastIndexOf("."));
					if (fileExtension1.equalsIgnoreCase(".txt")) {
						return fileNameUnderZip;
					}
				} else {
					StringBuilder sb = new StringBuilder();
					sb.append(zipFilePath).append(textFile.getName()).append("/");
					fileName = findTextFile(sb.toString());
				}
			}
		} catch (Exception e) {
			logger.error("Exception findTextFile :" + ExceptionUtils.getFullStackTrace(e));
		}
		return fileName;
	}

	/**
	 * 
	 * @param zipFilePath
	 * @return
	 */
	public static String findTextFileName(String zipFilePath) {
		String filename = null;
		try {
			FileUtil fu = new FileUtil();
			filename = fu.findTextFile(zipFilePath);
			fu = null;
		} catch (Exception e) {
			logger.error("Exception findTextFileName :" + ExceptionUtils.getFullStackTrace(e));
		}
		return filename;
	}

	public static void transferMultipartFile(MultipartFile file, String uploadPath) throws RctException {

		try {
			File uploadFile = new File(uploadPath+File.separator+file.getOriginalFilename());
			file.transferTo(uploadFile);
			Runtime.getRuntime().exec("chmod -R 777 " + uploadPath);
		} catch (Exception e) {
			logger.error("Failed to transferMultipartFile  ::" + ExceptionUtils.getFullStackTrace(e));
			throw new RctException("Failed to upload Multipart File(s) " + ExceptionUtils.getMessage(e));

		}
	}
	
	public static void unzipFile(String sourcePath, String destinationPath,boolean untarWithFolderStructure) throws RctException {
		ZipFile zipFile = null;
		OutputStream out = null;
		List<String> dirList= new ArrayList<String>();
		try {
			zipFile = new ZipFile(sourcePath);
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				File entryDestination = new File(destinationPath, entry.getName());
				if (entry.isDirectory()) {
					entryDestination.mkdirs();
					dirList.add(entry.getName());
				} else {
					entryDestination.getParentFile().mkdirs();
					InputStream in = zipFile.getInputStream(entry);
					out = new FileOutputStream(entryDestination);
					IOUtils.copy(in, out);
					IOUtils.closeQuietly(in);
				}
			}
			 if(!untarWithFolderStructure && dirList != null && dirList.size() > 0){
				 for(String dir : dirList){
				 FileUtils.copyDirectory(new File(destinationPath+"/"+dir), new File(destinationPath));
				 FileUtil.deleteFileOrFolder(destinationPath+"/"+dir);
				 }
			 }
			Runtime.getRuntime().exec("chmod -R 777 " + destinationPath);
		} catch (Exception e) {
			logger.error("Falied while unziping '" + sourcePath + "' file :" + ExceptionUtils.getMessage(e));
			throw new RctException("Failed to Unzip File(s) " + ExceptionUtils.getMessage(e));
		} finally {
			try {
				if (zipFile != null)
					zipFile.close();

				if (out != null)
					out.close();
			} catch (IOException e) {
				logger.error("Falied while closing file resource :" + ExceptionUtils.getMessage(e));
			}
		}
	}

	
	public static void unzipTarFile(String sourcePath, String destinationPath,boolean untarWithFolderStructure) throws RctException {
		TarInputStream tin = null;
		List<String> dirList= new ArrayList<String>();
		try{
			  tin = new TarInputStream(new GZIPInputStream(new FileInputStream(new File(sourcePath))));
		      TarEntry tarEntry = tin.getNextEntry();
		      if(new File(destinationPath).exists()){
			      while (tarEntry != null){
			         File destPath = new File(destinationPath + File.separatorChar + tarEntry.getName());
			    	 if(tarEntry.isDirectory()){
			    		 destPath.mkdir();
			    		 dirList.add(tarEntry.getName());
			         }else{
			        	 FileOutputStream fout = new FileOutputStream(destPath);
				         tin.copyEntryContents(fout);
				         fout.close();
			         }
			         tarEntry = tin.getNextEntry();
			      }
			      tin.close();
			      if(!untarWithFolderStructure && dirList != null && dirList.size() > 0){
					for(String dir : dirList){
						FileUtils.copyDirectory(new File(destinationPath+"/"+dir), new File(destinationPath));
						FileUtil.deleteFileOrFolder(destinationPath+"/"+dir);
					}
				 }
		      }
            Runtime.getRuntime().exec("chmod -R 777 " + destinationPath);
        } catch (Exception e) {
			logger.error("Falied while unzipTarFile '" + sourcePath + "' file :" + ExceptionUtils.getMessage(e));
			throw new RctException("Failed to unzipTarFile File(s) " + ExceptionUtils.getMessage(e));
		} finally {
			try {
				if (tin != null)
					tin.close();
			} catch (IOException e) {
				logger.error("Falied while closing file resource :" + ExceptionUtils.getMessage(e));
			}
		}
	}

	public static void untar7zipFile(String sourcePath, String destinationPath, boolean untarWithFolderStructure) throws RctException {
		OutputStream out = null;
		SevenZFile sevenZFile = null;
		List<String> dirList= new ArrayList<String>();
		logger.info("untar7zipFile called sourcePath: "+sourcePath+", destinationPath:"+destinationPath+", untarWithFolderStructure: "+untarWithFolderStructure);
		try {
			sevenZFile = new SevenZFile(new File(sourcePath));
			SevenZArchiveEntry entry;
			 while ((entry = sevenZFile.getNextEntry()) != null) {
				
				File entryDestination = new File(destinationPath, entry.getName());
				if (entry.isDirectory()) {
					entryDestination.mkdirs();
					dirList.add(entry.getName());
				} else {
					entryDestination.getParentFile().mkdirs();
					out = new FileOutputStream(entryDestination);
					byte[] content = new byte[(int) entry.getSize()];
					sevenZFile.read(content, 0, content.length);
					out.write(content);
					out.close();
				}
			}
			 if(!untarWithFolderStructure && dirList != null && dirList.size() > 0){
				 for(String dir : dirList){
				 FileUtils.copyDirectory(new File(destinationPath+"/"+dir), new File(destinationPath));
				 FileUtil.deleteFileOrFolder(destinationPath+"/"+dir);
				 }
			 }
			sevenZFile.close();
			Runtime.getRuntime().exec("chmod -R 777 " + destinationPath);
		} catch (Exception e) {
			logger.error("Falied while untar7zipFile '" + sourcePath + "' file :" + ExceptionUtils.getMessage(e));
			throw new RctException("Failed to Unzip File(s) " + ExceptionUtils.getMessage(e));
		} finally {
			try {
				if (sevenZFile != null)
					sevenZFile.close();

				if (out != null)
					out.close();
			} catch (IOException e) {
				logger.error("Falied while closing file resource :" + ExceptionUtils.getMessage(e));
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> fetchFileFromServer(Map<String, Object> serverInfo) throws RctException {
		Map<String, Object> objMap = new HashMap<String, Object>();
		String ip = null;
	    int port = 0;
	    String userName = null;
	    String password = null;
	    String sourcePath = null;
	    String destinationPath = null;
	    String fileName = null;
	    List<String> fileList=new ArrayList<String>();
	    try {
			ip = serverInfo.get("ip").toString();
			port = (int) serverInfo.get("port");
			userName = serverInfo.get("userName").toString();
			password = serverInfo.get("password").toString();
			sourcePath = serverInfo.get("sourcePath").toString();
			destinationPath = serverInfo.get("destinationPath").toString();
			fileName = serverInfo.get("fileName").toString();
			if(CommonUtil.isValidObject(ip) && CommonUtil.isValidObject(port) && CommonUtil.isValidObject(userName) &&
			CommonUtil.isValidObject(password) && CommonUtil.isValidObject(sourcePath) && CommonUtil.isValidObject(destinationPath) && CommonUtil.isValidObject(fileName)){
			JSch jsch = new JSch();
			logger.info("FileUtil.fetchFileFromServer() connecting to IP: "+ip+", sourcePath :"+sourcePath+", fileName :"+fileName);
		    Session session = jsch.getSession(userName, ip, port);
		    session.setPassword(password);
		    session.setConfig("StrictHostKeyChecking", "no");
		    session.connect();
		    ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
		    sftpChannel.connect();
		   
		    sftpChannel.get(sourcePath+Constants.SEPARATOR+fileName, destinationPath);
		    
		    Vector<ChannelSftp.LsEntry> list = sftpChannel.ls(sourcePath+Constants.SEPARATOR+fileName);
		    for(ChannelSftp.LsEntry entry : list) {
		    	sftpChannel.get(sourcePath+Constants.SEPARATOR+entry.getFilename(), destinationPath + entry.getFilename());
		    	File f = new File(destinationPath+entry.getFilename());
			    if(f.exists()) { 
			    	objMap.put("status", Constants.SUCCESS);
			    	fileList.add(entry.getFilename());
			    }else{
			    	objMap.put("status", Constants.FAIL);
			    }
		    }

		    objMap.put("fileList", fileList);
		    sftpChannel.disconnect();
		    session.disconnect();
			}else{
				objMap.put("status", Constants.FAIL);
				objMap.put("reason", "Required Info Not Exist");
			}
		} catch (Exception e) {
			objMap.put("status", Constants.FAIL);
			objMap.put("reason", e.getMessage());
			logger.error("Exception  fetchFileFromServer() in  FileUtil:" + ExceptionUtils.getFullStackTrace(e));
		}
		return objMap;
	}
	
	private static void includeConnectingMethods(StringBuilder sb, NetworkConfigDetailsEntity neDetails) {
		try{
			if (neDetails!= null && neDetails.getPath() != null && neDetails.getPath().length() != 0) {
			String path = neDetails.getPath();
			String[] pathLst = path.split(",");
			logger.info("includeConnectingMethods() pathLst length(): "+pathLst.length);
			if (pathLst.length != 0) {
				sb.append("send \"PS1='SMART_TOOL:~$ '\"\n");
				for (int i = 0; i <= pathLst.length; i++) {
					// try {
					sb.append("expect \"$\" \n");
					sb.append(pathLst[i]);
					// Thread.sleep(2000); // sleep for 2 seconds
					sb.append("expect \"$\" \n");
					/*
					 * } catch (InterruptedException e) { e.printStackTrace(); }
					 */
				}
				sb.append("\n");
			}
		}
		}catch(Exception e){
			logger.error("Exception  includeConnectingMethods() in  FileUtil:" + ExceptionUtils.getFullStackTrace(e));
		}
	}
	
	private static void generateExpectScriptToFetchFileFromServer(NetworkConfigEntity networkConfigEntity, Map<String, Object> serverInfo, String filePath) {
		try{
		StringBuilder sb = buildConnectionToFetchFileFromServer(networkConfigEntity, serverInfo);
		String s = new String(sb);
		FileOutputStream os = null;	
		File fileobj = new File(filePath.toString());
        os = new FileOutputStream(fileobj);
        os.write(s.getBytes(), 0, s.length());
        os.close();
		}catch(Exception e){
			logger.error("Exception  generateExpectScriptToFetchFileFromServer() in  FileUtil:" + ExceptionUtils.getFullStackTrace(e));
		}
	}
	
	private static StringBuilder buildConnectionToFetchFileFromServer(NetworkConfigEntity networkConfigEntity, Map<String, Object> fileInfo) {
		String sourcePath = null;
	    String fileName = null;
	    NetworkConfigDetailsEntity scpNeDetails = null;
	    StringBuilder sb = new StringBuilder();
	    try {
		boolean first = true;
		List<NetworkConfigDetailsEntity> neDetailsLst = networkConfigEntity.getNeDetails();
		Collections.sort(neDetailsLst, Comparator.comparing(NetworkConfigDetailsEntity::getStep));
		sourcePath = fileInfo.get("sourcePath").toString();
		fileName = fileInfo.get("fileName").toString();
		for (NetworkConfigDetailsEntity neDetails : neDetailsLst) {
			
				if (first) {
					sb.append("#!/usr/bin/expect\n");
					sb.append("#!/bin/sh\n");
					sb.append("\n");
					sb.append("spawn ssh -o StrictHostKeyChecking=no -oCheckHostIP=no " + neDetails.getServerUserName()+ "@" + neDetails.getServerIp() + "\n");
					sb.append("set prompt \":|#|\\\\\\$\"\n");
					//sb.append("interact -o -nobuffer -re $prompt return\n");
					sb.append("send \"" + neDetails.getServerPassword() + "\\r\"\n");
					//sb.append("interact -o -nobuffer -re $prompt return\n");
					sb.append("expect \"$\" \n");
					sb.append("send \"mkdir "+sourcePath+""+"\\r\" \n");
					sb.append("expect \"$\" \n");
					sb.append("\n");
					includeConnectingMethods(sb, neDetails);
					first = false;
				} else {
					sb.append("send \"ssh -o StrictHostKeyChecking=no -oCheckHostIP=no " + neDetails.getServerUserName()
							+ "@" + neDetails.getServerIp() + "\\r\" \n");
					//sb.append("interact -o -nobuffer -re $prompt return\n");
					sb.append("expect \"word:\" {send \"" + neDetails.getServerPassword() + "\\r\"} \n");
					//sb.append("interact -o -nobuffer -re $prompt return\n");
					sb.append("expect \"$\" \n");
					sb.append("send \"mkdir "+sourcePath+""+"\\r\" \n");
					sb.append("expect \"$\" \n");
					sb.append("\n");
					includeConnectingMethods(sb, neDetails);
				}

			}
		
			sb.append("send \"ssh -o StrictHostKeyChecking=no -oCheckHostIP=no " + networkConfigEntity.getNeUserName()
			+ "@" + networkConfigEntity.getNeIp() + "\\r\" \n");
			//sb.append("interact -o -nobuffer -re $prompt return\n");
			sb.append("expect \"word:\" {send \"" + networkConfigEntity.getNePassword() + "\\r\"} \n");
			//sb.append("interact -o -nobuffer -re $prompt return\n");
			sb.append("expect \"$\" \n");
			sb.append("\n");
			includeConnectingMethods(sb, null);


			// To fetch the file using scp Connectivity
			for (int j = neDetailsLst.size() - 1; j >= 0; j--) {
				scpNeDetails = neDetailsLst.get(j);
				sb.append("send \"scp -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null " + sourcePath+Constants.SEPARATOR+fileName + " "
						+ scpNeDetails.getServerUserName() + "@" + scpNeDetails.getServerIp() + ":" + sourcePath
						+ "\\r\" \n");
				//sb.append("interact -o -nobuffer -re $prompt return\n");
				sb.append("expect \"word:\" {send \"" + scpNeDetails.getServerPassword() + "\\r\"} \n");
				//sb.append("interact -o -nobuffer -re $prompt return\n");
				sb.append("send \"" + "exit" + "\\r\"\n");
				sb.append("expect \"$\" \n");
				sb.append("\n");
			}
			
			
			} catch (Exception e) {
			logger.error("Exception  buildConnectionToFetchFileFromServer() in  FileUtil:" + ExceptionUtils.getFullStackTrace(e));
		}
		return sb;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> fetchFileFromServer(NetworkConfigEntity networkConfigEntity, Map<String, Object> fileInfo,String  marketName,FetchDetailsModel fetchDetailsModel,String fileType) throws RctException {
		Map<String, Object> objMap = new HashMap<String, Object>();
		String ip = null;
	    int port = 0;
	    String userName = null;
	    String password = null;
	    String sourcePath = null;
	    String destinationPath = null;
	    String fileName = null;
		 List<String> fileList=new ArrayList<String>();
	    try {
	    	
	    	List<NetworkConfigDetailsEntity> neDetailsLst = networkConfigEntity.getNeDetails();
			port = (int) fileInfo.get("port");
			sourcePath = fileInfo.get("sourcePath").toString();
			destinationPath = fileInfo.get("destinationPath").toString();
			fileName = fileInfo.get("fileName").toString();
			sourcePath=getSourcePath(sourcePath,marketName,fileType);
			if(CommonUtil.isValidObject(networkConfigEntity) && CommonUtil.isValidObject(sourcePath) && CommonUtil.isValidObject(destinationPath) && CommonUtil.isValidObject(fileName)){
				
				if(CommonUtil.isValidObject(neDetailsLst) && neDetailsLst != null && neDetailsLst.size() > 0){
					StringBuilder fileDirPath = new StringBuilder();
					fileDirPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
				    .append(LoadPropertyFiles.getInstance().getProperty(Constants.MULTI_HOP_SCRIPT_FOLDER));
					
				    File dir = new File(fileDirPath.toString());
					if (!dir.exists()) {
						FileUtil.createDirectory(fileDirPath.toString());
					}
					StringBuilder filePath = new StringBuilder();
					filePath.append(fileDirPath.toString()).append(Constants.MULTI_HOP_SCRIPT_FILE);	
				    
					generateExpectScriptToFetchFileFromServer(networkConfigEntity, fileInfo, filePath.toString());
				    
				    File file = new File(filePath.toString());
				    if(!file.exists()){
				    	objMap.put("status", Constants.FAIL);
						objMap.put("reason", "Failed To Build Connection Script To Fetch File From Server");
						logger.info(objMap.get("reason").toString());
						return objMap;
				    }else{
				    	logger.info("fetchFileFromServer() script file found: "+filePath);
				    }
				    file.setExecutable(true,true);
				   
				    Process process = Runtime.getRuntime().exec(filePath.toString());
				    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				    String line = "";
				    while ((line = reader.readLine()) != null) {	
				        logger.info("fetchFileFromServer logger line: "+line);
				    }
					
				    ip = neDetailsLst.get(0).getServerIp();
					userName =  neDetailsLst.get(0).getServerUserName();
					password = neDetailsLst.get(0).getServerPassword();
		    	}else{
		    		ip = networkConfigEntity.getNeIp();
					userName = networkConfigEntity.getNeUserName();
					password = networkConfigEntity.getNePassword();
		    	}
			
			
			JSch jsch = new JSch();
			logger.info("FileUtil.fetchFileFromServer() connecting to IP: "+ip+", sourcePath :"+sourcePath+", fileName :"+fileName);
		    Session session = jsch.getSession(userName, ip, port);
		    session.setPassword(password);
		    session.setConfig("StrictHostKeyChecking", "no");
		    session.connect();
		    ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
		    sftpChannel.connect();
		   
		    //sftpChannel.get(sourcePath.trim()+Constants.SEPARATOR+fileName, destinationPath);
//		    if("XLSX".equals(FileUtil.getFileExtension(fileName))){
//    			objMap.put("status", Constants.FAIL);
//    			objMap.put("reason", "CIQ format is not correct");
//    		//	break;
//    		}
		    if("CIQ".equalsIgnoreCase(fileType))
		    {
		    Vector<ChannelSftp.LsEntry> list1 = sftpChannel.ls(sourcePath.trim()+Constants.SEPARATOR);
		    boolean status = false;
		    for(ChannelSftp.LsEntry entry : list1) {
	    		if("XLSX".equals(FilenameUtils.getExtension(entry.getFilename()))){
    			objMap.put("status", Constants.FAIL);
    			objMap.put("reason", "CIQ format is not correct");
    			break;
    		}
		    }
		    }
		   Vector<ChannelSftp.LsEntry> list = new Vector<ChannelSftp.LsEntry>();
		   
		   if(fileName.contains(","))
		   {
			  String[] scriptExtention= fileName.split(",");
			   if(StringUtils.isNotEmpty(scriptExtention[0]))
			   {
				   list=sftpChannel.ls(sourcePath.trim()+Constants.SEPARATOR+scriptExtention[0]);
				   
				   if(StringUtils.isNotEmpty(scriptExtention[1]))
				   {
					   Vector<ChannelSftp.LsEntry>  secondlist=sftpChannel.ls(sourcePath.trim()+Constants.SEPARATOR+scriptExtention[1]);
					   if(!ObjectUtils.isEmpty(secondlist))
					   {
						   list.addAll(secondlist);
					   }
					   
				   }
				   
			   }
		   }else {
			   list = sftpChannel.ls(sourcePath.trim()+Constants.SEPARATOR+fileName);
		   }
		   
		   
		   
		    for(ChannelSftp.LsEntry entry : list) {
		    	if("RF_SCRIPTS".equalsIgnoreCase(fileType))
		    	{
		    		if(!ObjectUtils.isEmpty(fetchDetailsModel.getRfScriptList()) && scriptNameExist(fetchDetailsModel.getRfScriptList(), entry.getFilename()))
		    		{
		    		sftpChannel.get(sourcePath+Constants.SEPARATOR+entry.getFilename(), destinationPath + entry.getFilename());
			    	File f = new File(destinationPath+entry.getFilename());
				    if(f.exists()) { 
				    	objMap.put("status", Constants.SUCCESS);
				    	fileList.add(entry.getFilename());
				    }else{
				    	objMap.put("status", Constants.FAIL);
				    }
				    }
		    	}else {
//		    		if("XLSX".equals(FileUtil.getFileExtension(entry.getFilename()))){
//		    			objMap.put("status", Constants.FAIL);
//		    			objMap.put("reason", "CIQ format is not correct");
//		    			break;
//		    		}
		    		
		    		if ("CIQ".equalsIgnoreCase(fileType)&& list.size() > 1) {
		    			objMap.put("status", Constants.FAIL);
		    			objMap.put("reason", "Multiple CIQ Files in the path");
		    			break;
		    			}
		    		
		    	sftpChannel.get(sourcePath+Constants.SEPARATOR+entry.getFilename(), destinationPath + entry.getFilename());
		    	File f = new File(destinationPath+entry.getFilename());
			    if(f.exists()) { 
			    	objMap.put("status", Constants.SUCCESS);
			    	fileList.add(entry.getFilename());
			    }else{
			    	objMap.put("status", Constants.FAIL);
			    }
		    	}
		    }
		    if ("CIQ".equalsIgnoreCase(fileType) && (list.size()<1) && !objMap.containsKey("reason")) {
    			objMap.put("status", Constants.FAIL);
    			objMap.put("reason", "No such file");
    			}
		    objMap.put("fileList", fileList);
		    sftpChannel.disconnect();
		    session.disconnect();
			}else{
				objMap.put("status", Constants.FAIL);
				objMap.put("reason", "Required Info Not Exist");
			}
		} catch (Exception e) {
			objMap.put("status", Constants.FAIL);
			objMap.put("reason", e.getMessage());
			logger.error("Exception  fetchFileFromServer() in  FileUtil:" + ExceptionUtils.getFullStackTrace(e));
		}
		return objMap;
	}

	private static String getSourcePath(String sourcePath, String marketName, String fileType) {
		// TODO Auto-generated method stub
		StringBuilder sourcePathBuilder = new StringBuilder();
		sourcePathBuilder.append(sourcePath);
		sourcePathBuilder.append(File.separator);
		sourcePathBuilder.append(marketName);
		sourcePathBuilder.append(File.separator);
		sourcePathBuilder.append(fileType);
		sourcePathBuilder.append(File.separator);
		return sourcePathBuilder.toString();
	}

	private static boolean scriptNameExist(List<String> scriptDetails, String originalScriptName) {
		// TODO Auto-generated method stub
		boolean scriptExist = false;
		try {
			for (String scriptName : scriptDetails) {
				String withOutExtentionFile=FilenameUtils.removeExtension(
						originalScriptName);
				//System.out.println(originalScriptName);
				if (withOutExtentionFile.contains("_ENDC")) {
					withOutExtentionFile=withOutExtentionFile.substring(0,withOutExtentionFile.indexOf("_"));
							
				}
				if (StringUtils.isNotEmpty(scriptName)&& withOutExtentionFile.equalsIgnoreCase(scriptName)) {
					scriptExist = true;
					break;
				}
			}
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return scriptExist;
	}
}
