import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.json.simple.parser.ParseException;



public class Test {
	static final int MAX_CHAR = 256; 
	public static void main(String args[]) throws ParseException, IOException{
		/*String outputFileName="/home/user/Swetha/RCT/rctsoftware/Samsung/SMART/Customer/26/Migration/55/PreCheck/Output/73144_PreAuditofeNB_CDU308T8RPreVerificationv2_10012019_01_04_03.txt";
		//outputFileName="/home/user/Downloads/73144_PreAuditofeNB_CDU308T8RPreVerificationv2_10012019_01_04_03.txt";
		String tempOutputFilePath = "/home/user/Downloads/Output.txt";
		String cmd  = "sed '/.\\x08/d' "+outputFileName+" > "+tempOutputFilePath+"";
		String[] cmdExecution = { "/bin/sh", "-c", cmd};
		Process process = Runtime.getRuntime().exec(cmdExecution);
		
		String mvcmd  = "mv "+tempOutputFilePath+" "+outputFileName;
		String[] mvcmdExecution = { "/bin/sh", "-c", mvcmd};
		process = Runtime.getRuntime().exec(mvcmdExecution);
		System.out.println("Done");
		*/
		/*String srcFilePath= "/Customer/30//PostMigration/400/Audit/Output/Test1_400_000400_ROCKY_HILL_4_CT/";
		String[] zipFileName = srcFilePath.split("/");
		int fileLength = zipFileName.length;
		String sZipFileName = zipFileName[fileLength - 1];
		System.out.println(sZipFileName);*/
		try{
		File file = new File("/home/user/Swetha/RCT/rctsoftware/Samsung/SMART/Customer/30/postmigration/57120/AUDIT/Output/abcd_57120_057120_MILTON_2_MA");
		FileUtils.deleteDirectory(file);
		System.out.println("Done");
		}catch(Exception ex){
			ex.printStackTrace();
		}
		//FileUtil.deleteFileOrFolder("/home/user/Swetha/RCT/rctsoftware/Samsung/SMART/Customer/30/PostMigration/57120/AUDIT/Output/abcd_57120_057120_MILTON_2_MA");
		
		
		
		/*String json1 ="{\"scripts\":[\"ShellScript\",\"BatchFile\",\"VbsFile\"],\"connLocation\":{\"SM\":{\"terminals\":[{\"terminalName\":\"cmd_sys\",\"termUsername\":\"root\",\"termPassword\":\"root123\",\"prompt\":\"$\"},{\"terminalName\":\"bash\",\"termUsername\":\"root\",\"termPassword\":\"root123\",\"prompt\":\"$\"},{\"terminalName\":\"bsm\",\"termUsername\":\"cdmaone\",\"termPassword\":\"bsmpwd\",\"prompt\":\"$\"},{\"terminalName\":\"csr\",\"termUsername\":\"csrusername\",\"termPassword\":\"csrpwd\",\"prompt\":\"$\"}],\"username\":\"user\",\"password\":\"root123\"},\"NE\":{\"terminals\":[{\"terminalName\":\"cli\",\"termUsername\":\"ROOT\",\"termPassword\":\"ROOT\",\"prompt\":\"eNB_enbId]\"},{\"terminalName\":\"bash\",\"termUsername\":\"ROOT\",\"termPassword\":\"ROOT\",\"prompt\":\"root@*\"}],\"username\":\"lteuser\",\"password\":\"samsunglte\",\"sudoPassword\":\"S@msung1te\"}}}";
		JSONObject objData = CommonUtil.parseDataToJSON(json1);
		JSONArray scripts = (JSONArray) objData.get("scripts");
		JSONObject connLocation = (JSONObject) objData.get("connLocation");
		JSONObject SM = (JSONObject) connLocation.get("SM");
		JSONObject NE = (JSONObject) connLocation.get("NE");
		JSONArray terminals = (JSONArray) SM.get("terminals");
		JSONArray terminalsObj = new JSONArray();
		String terminalName = "";
		for (int i = 0; i < terminals.size(); i++) {
			objData = (JSONObject) terminals.get(i);
			terminalName = (String) objData.get("terminalName");
			if (!terminalName.equalsIgnoreCase("bsm") && !terminalName.equalsIgnoreCase("csr")) {
				terminalsObj.add(terminals.get(i));
			}
		}
		
		JSONObject ConnectionScriptObj = new JSONObject();
		JSONObject connLocationObj =  new JSONObject();
		JSONObject SMObj =  new JSONObject();
		SMObj.put("terminals", terminalsObj);
		connLocationObj.put("SM", SMObj);
		connLocationObj.put("NE", NE);
		ConnectionScriptObj.put("connLocation", connLocationObj);
		ConnectionScriptObj.put("scripts", scripts);
		System.out.println(ConnectionScriptObj);*/
	}
	
}
