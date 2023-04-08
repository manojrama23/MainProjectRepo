package com.smart.rct.premigration.serviceImpl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class AuTemplate {

	public ArrayList<String> gndbAuId = new ArrayList<>();
	public String ciq = "";
	public String relver;
	public int gnbidx;
	public String version;
	public String timestamp;
	public String growfolder; 
	public ArrayList<HashMap<String, String>> necessaryA;
	public ArrayList<String> ciqData = new ArrayList<String>();
	public ArrayList<String> scrubData = new ArrayList<String>();

	public String generateAuTemplate(ArrayList<String> ciqDataTemp, String ver, String outputdir, String gnodebID, String relversion,String auType)
	{
		ciqData = ciqDataTemp;
		version = ver;
		growfolder = outputdir;
		if(gnodebID.charAt(0)=='0')
		{
			gnodebID = gnodebID.substring(1);
		}
		gndbAuId.add(gnodebID);
		relver = relversion;
		String auFilename = "";
		//Creating Folder structure
		File f1 = new File(growfolder);
		f1.mkdirs();
					
		Format f = new SimpleDateFormat("MMddyyyy_HH_mm_ss");
		timestamp = f.format(new Timestamp(System.currentTimeMillis()));
				
		//Populating necessary data in ArrayList
		populate_Necessary_Data();
		
		//Filling ScrubData
		getScrubData();
		
	
		
		for(gnbidx=0; gnbidx < gndbAuId.size(); gnbidx++)
		{
			ArrayList<HashMap<String, Object>> cellDataA = new ArrayList<>();
			
			//To get required data for gndb
			populate_Data(cellDataA);
			
			//System.out.println(cellDataA);
			
			//Creating CellTemplate
			//System.out.println("Creating Cell_Template \n\n");
			if(version.contains("20") || version.contains("21"))
			{
				auFilename = createCellTemplate_20A(cellDataA,auType);
			}
			
			//22A
			if(version.contains("22")) {
				auFilename = createCellTemplate_22A(cellDataA,auType);
			}
		}
		return auFilename;
		
	}
	
	
	
	private void mydie(String msg)
	{
		System.out.println(msg);
	}
	
	private void populate_Necessary_Data()
	{
		necessaryA = new ArrayList<>();
		String tags[] = {"market", "neid", "network", "gndbauid", "vzwsite", "gndbid", "remoteip", "oamid", "ranid", "oamip", "ranip", "oamprelen", "ranprelen", "ranroutepre1", 
				"ranroutepre2", "rangate", "site", "latitude", "longitude", "height", "auname", "nrfreq","clock_source","au_type" , "market_type","RAN_IP_Address" };
		String headers[] = {"MARKET", "NE_ID_AU", "NETWORK", "GNODEB_AUID", "VZW_SITE_NAME", "GNODEB_ID", "REMOTEIPADDRESS", "OAMVLANID", "RANVLANID",
				"OAMIP", "RANIPADDRESS", "OAMPREFIXLENGTH", "RANPREFIXLENGTH", "RANROUTEPREFIX1", "RANROUTEPREFIX2", "RANGATEWAY", "SITETYPE", "LATITUDE", 
				"LONGITUDE", "HEIGHT", "GNB_AU_NAME", "NR_FREQ_BAND","CLOCK_SOURCE","AU_TYPE", "MARKET_TYPE","RAN_IP_ADDRESS" };
		String poss[] = {"-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1","-1","-1","-1", "-1" ,"-1"};
		
		for(int i=0; i<tags.length ; i++)
		{
			HashMap<String, String> hm = new HashMap<>();
			hm.put("tag", tags[i]);
			hm.put("header", headers[i]);
			hm.put("poss", poss[i]);
			
			necessaryA.add(hm);
		}
		
	}
 
	private void getScrubData()
	{
		try
		{
			StringBuilder sb = new StringBuilder();
			int linenumber = 0;
			int length = ciqData.get(0).split(",").length;
			for(int k=0;k<ciqData.size();k++)
			{
				String[] lineA;
				if(k==0)
				{
					lineA = ciqData.get(k).replaceAll("\\s", "").toUpperCase().split(",");
				}
				else
				{
					lineA = ciqData.get(k).split(",");
				}
					
				if(lineA.length < length)
				{
					break;
				}
				if(linenumber == 0)
				{
					linenumber = lineA.length;
				}
				if(lineA[0].equals("MARKET"))
				{
					for(int n = 0; n < necessaryA.size(); n++)
					{
						for(int i=0; i<lineA.length; i++)
						{
							
							if(necessaryA.get(n).get("header").equals(lineA[i]))
							{
								necessaryA.get(n).replace("poss", Integer.toString(i));
							}
				
						}
					}
					
					for(int i=0; i<necessaryA.size(); i++)
					{							
						if(!necessaryA.get(i).get("poss").equals("-1"))
						{
							sb.append(lineA[Integer.parseInt(necessaryA.get(i).get("poss"))]);
							
							if(!(i == necessaryA.size()-1))
							{
								sb.append(",");
							}
						}
						if(i == necessaryA.size()-1)
						{
							scrubData.add(sb.toString());
						}
					}					
				}
				
				boolean writelineboo = false;
				String writeline = "";
				for(int i=0; i<necessaryA.size(); i++)
				{
					if(!necessaryA.get(i).get("poss").equals("-1"))
					{
						if(necessaryA.get(i).get("tag").equals("gndbauid"))
						{
							for(int j=0; j<gndbAuId.size(); j++)
							{
								String gnb = lineA[Integer.parseInt(necessaryA.get(i).get("poss"))];
								if(gnb.charAt(0) == '0')
								{
									gnb = gnb.substring(1);
									lineA[Integer.parseInt(necessaryA.get(i).get("poss"))] = gnb;
								}
								if(gndbAuId.get(j).equals(gnb))
								{
									writelineboo = true;
								}
								
							}
						}
						
						writeline = writeline + lineA[Integer.parseInt(necessaryA.get(i).get("poss"))];
											
						if((i != necessaryA.size() - 1))
						{
							writeline = writeline + ",";
						}
					}
				}
				
				if(writelineboo)
					scrubData.add(writeline);
			}
		}
		catch(Exception e)
		{
			mydie(e.toString());
		}
	}

	private void populate_Data(ArrayList<HashMap<String, Object>> cellDataRef)
	{	
		try
		{			
			for(int k=0;k<scrubData.size();k++)
			{
				
				String[] lineA = scrubData.get(k).split(",");
				if(gndbAuId.get(gnbidx).equals(lineA[find_necessaryA_idx("gndbauid")]))
				{
					HashMap<String,Object> cellDataH = new HashMap<>();
					
					int index = find_necessaryA_idx("market");
					if(index != -1)
					{
						cellDataH.put("market", lineA[index]);
					}
					
					index = find_necessaryA_idx("neid");
					if(index != -1)
					{
						cellDataH.put("neid", lineA[index]);
					}
					
					index = find_necessaryA_idx("network");
					if(index != -1)
					{
						cellDataH.put("network", lineA[index].replaceAll("\\s", "_"));
					}
					
					index = find_necessaryA_idx("gndbauid");
					if(index != -1)
					{
						String gnbAuId = lineA[index].trim().replaceAll("^0+(?!$)", "");
						if(gnbAuId.length()<11) {
							gnbAuId = "0" + gnbAuId;
						}
						cellDataH.put("gndbauid", gnbAuId);
					}
					
					index = find_necessaryA_idx("vzwsite");
					if(index != -1)
					{
						cellDataH.put("vzwsite", lineA[index]);
					}
					
					index = find_necessaryA_idx("gndbid");
					if(index != -1)
					{
						cellDataH.put("gndbid", lineA[index]);
					}
					
					index = find_necessaryA_idx("remoteip");
					if(index != -1)
					{
						cellDataH.put("remoteip", lineA[index]);
					}
					
					index = find_necessaryA_idx("oamid");
					if(index != -1)
					{
						cellDataH.put("oamid", lineA[index]);
					}
					
					index = find_necessaryA_idx("ranid");
					if(index != -1)
					{
						cellDataH.put("ranid", lineA[index]);
					}
					
					index = find_necessaryA_idx("oamip");
					if(index != -1)
					{
						cellDataH.put("oamip", lineA[index]);
					}
					
					index = find_necessaryA_idx("nrfreq");
					if(index != -1)
					{
						cellDataH.put("nrfreq", lineA[index]);
					}
					
					index = find_necessaryA_idx("ranip");
					if(index != -1)
					{
						cellDataH.put("ranip", lineA[index]);
					}
					
					index = find_necessaryA_idx("oamprelen");
					if(index != -1)
					{
						cellDataH.put("oamprelen", lineA[index]);
					}
					
					index = find_necessaryA_idx("ranprelen");
					if(index != -1)
					{
						cellDataH.put("ranprelen", lineA[index]);
					}
					
					index = find_necessaryA_idx("ranroutepre1");
					if(index != -1)
					{
						cellDataH.put("ranroutepre1", lineA[index]);
					}
					
					index = find_necessaryA_idx("ranroutepre2");
					if(index != -1)
					{
						cellDataH.put("ranroutepre2", lineA[index]);
					}
					
					index = find_necessaryA_idx("rangate");
					if(index != -1)
					{
						cellDataH.put("rangate", lineA[index]);
					}
					
					index = find_necessaryA_idx("site");
					if(index != -1)
					{
						cellDataH.put("site", lineA[index]);
					}
					
					index = find_necessaryA_idx("latitude");
					if(index != -1)
					{
						if(lineA[index].equals("TBD"))
						{
							cellDataH.put("latitude", "");
						}
						else
						{
							String lat = getLatLong(Double.valueOf(lineA[index]),"lat");
							//cellDataH.put("latitude", lineA[index]);
							cellDataH.put("latitude", lat.toString());
						}
						
					}
					
					index = find_necessaryA_idx("longitude");
					if(index != -1)
					{
						if(lineA[index].equals("TBD"))
						{
							cellDataH.put("longitude", "");
						}
						else
						{
							String lng = getLatLong(Double.valueOf(lineA[index]),"lng");
							//cellDataH.put("longitude", lineA[index]);
							cellDataH.put("longitude", lng.toString());
						}
					}
					
					index = find_necessaryA_idx("height");
					if(index != -1)
					{
						if(lineA[index].equals("TBD"))
						{
							cellDataH.put("height", "");
						}
						else
						{
//							String[] str = lineA[index].split("\\.");
//							StringBuilder res = new StringBuilder();
//							res.append(str[0]);
//							if(str.length > 1)
//							{
//								if(str[1].length() > 2)
//								{
//									res.append(".");
//									res.append(str[1].substring(0, 2));
//								}
//								else
//								{
//									res.append(".");
//									res.append(str[1]);
//								}
//							}
//							res.append("m");
//							cellDataH.put("height", res.toString());
							Double heig = Double.valueOf(lineA[index]);
							Double he = heig/3.2808;
							String height = String.format("%.2f", he);
							StringBuilder sb = new StringBuilder();
							sb.append(height.toString());
							sb.append("m");
							cellDataH.put("height", sb.toString());
							
						}
					}
					
					index = find_necessaryA_idx("auname");
					if(index != -1)
					{
						cellDataH.put("auname", lineA[index]);
					}
					
					index = find_necessaryA_idx("clock_source");
					if(index != -1)
					{
						if(lineA[index].equals("TBD"))
						{
							cellDataH.put("clock_source", "");
						}
						else
						{
							cellDataH.put("clock_source", lineA[index]);
						}
					}
					
					index = find_necessaryA_idx("au_type");
					if(index != -1)
					{
						if(lineA[index].equals("TBD"))
						{
							cellDataH.put("au_type", "");
						}
						else
						{
							cellDataH.put("au_type", lineA[index]);
						}
					}
					
					index = find_necessaryA_idx("market_type");
					if(index != -1)
					{
						cellDataH.put("market_type", lineA[index]);
					}
					
					index = find_necessaryA_idx("RAN_IP_Address");
					if(index != -1)
					{
						cellDataH.put("RAN_IP_Address", lineA[index]);
					}
					//System.out.println("Heelo:" + cellDataH);
					cellDataRef.add(cellDataH);
				}
			}
		}
		catch (Exception e) {
			mydie(e.toString());
		}
	}
	
	private int find_necessaryA_idx(String ttag)
	{
		for(int i=0; i<necessaryA.size(); i++)
		{
			if(necessaryA.get(i).get("poss").equals("-1"))
			{
				if(necessaryA.get(i).get("tag").equals(ttag))
				{
					return -1;
				}
			}
			if(necessaryA.get(i).get("tag").equals(ttag))
			{
				return i;
			}
		}
		return -1;
	}
	
	public String getLatLong(double dd,String type ) {
		StringBuilder sb = new StringBuilder();
		if(type.equalsIgnoreCase("lat")) {
		String latResult = (dd >= 0)? "N" : "S";
		sb.append(latResult);
		sb.append(" ");
		}else {
		String lngResult = (dd >= 0)? "E" : "W";
		sb.append(lngResult);
		sb.append(" ");
		}
		Double ddNew = Math.abs(dd);
		Double degree=Math.floor(ddNew);
		
		sb.append(String.valueOf(String.format("%03d", degree.intValue())));
		
	//	sb.append(String.valueOf(degree.intValue()));
		sb.append(":");
		Double min=Math.floor((ddNew - degree) * 60);
		
		sb.append(String.valueOf(String.format("%02d",min.intValue())));
	//	sb.append(String.valueOf(min.intValue()));
		sb.append(":");
		Double valSec = Math.floor((ddNew - degree - min / 60) * 3600 * 1000) / 1000;
		DecimalFormat df = new DecimalFormat("00.000");
		String data=df.format(valSec);
		sb.append(data.toString());
		//sb.append(String.valueOf(String.format("%.3f",valSec)));
		//sb.append(String.valueOf(valSec));
		return sb.toString();
		}

	@SuppressWarnings("unchecked")
	private String createCellTemplate_20A(ArrayList<HashMap<String, Object>> cellDataRef,String auType)
	{
		String growfile1 = growfolder + "/AU_"+ version +"_"+auType+"_" + gndbAuId.get(gnbidx) + "_" + timestamp + ".csv";
		for(int i=0;i<cellDataRef.size();i++)
		{
			HashMap<String,Object> cellDataH = cellDataRef.get(i);
			//System.out.println(cellDataH);
			String ver =  "";
			if(version.contains("20A"))
			{
				ver = "20.A.0";
			}
			else if(version.contains("20B"))
			{
				ver = "20.B.0";
			}else if(version.contains("20C"))
			{
				ver = "20.C.0";
			}
			else if(version.contains("21A")) {
				ver = "21.A.0";
			}else if(version.contains("21B")) {
				ver = "21.B.0";
			}else if(version.contains("21C")) {
				ver = "21.C.0";
			}else if(version.contains("21D")) {
				ver = "21.D.0";
			}
			
			PrintWriter pw1 =null;
			try
			{		
				
				File f1 =  new File(growfile1);
				f1.createNewFile();
				pw1 = new PrintWriter(new BufferedWriter(new FileWriter(f1)));
				String siteType = cellDataH.get("site").toString();
				/*if(cellDataH.get("market_type").toString().equalsIgnoreCase("SNAP")) {
					relver = "r_0201";
				} else {
					relver = "r_0200";
				}*/
				if(siteType.equalsIgnoreCase("IB") && (auType.isEmpty() || auType.equalsIgnoreCase("TBD")) && ver.equals("20.C.0")) {

					pw1.print("\"@DU\"\n");				
					pw1.print("\"NE ID\",\"NE Type\",\"NE Version\",\"Release Version\",\"Network\",\"NE Name\",\"GPL Version\",\"AdministrativeState\",\"gNB ID\",\"gNB ID Length\",\"gNB DU ID\",\"gNB DU Name\",\"Endpoint CU IP address\",\"Local Time Offset\",\"NE Serial Number\",\"FW Auto Fusing\"\n");				
					pw1.print("\"" + cellDataH.get("neid") +"\",\"gnb_au_sc\",\"" + ver + "\",\"" + relver + "\",\"" + cellDataH.get("network") + "\",\"GROW_" + cellDataH.get("auname") + "\",\"\",\"unlocked\",\"" +
							cellDataH.get("gndbid") + "\",\"22\",\"" + cellDataH.get("gndbauid") + "\",\""+ cellDataH.get("auname") + "\",\"" + cellDataH.get("remoteip") + "\",\"0\",\"\",\"off\"\n");
					pw1.print("\"@SERVER_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CFM\",\"PSM\"\n");
					pw1.print("\"\",\"\",\"\"\n");
					pw1.print("\"@MAIN_BOARD_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"Unit Type\",\"Unit ID\",\"Board Type\"\n");					
					pw1.print("\"" + cellDataH.get("neid") + "\",\"fmp\",\"0\",\"g7dc-a10\"\n");
					
				
					
					
					pw1.print("\"@CLOCK_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"Clock Source ID\",\"Clock Source\",\"Priority Level\",\"Quality Level\"\n");
					if(cellDataH.get("clock_source").equals("PTP"))
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"1\",\"ieee1588-phasetype\",\"1\",\"prc\"\n");
					}
					else
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"gps-type\",\"1\",\"prc\"\n");
					}
									
					pw1.print("\"@PTP_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"IP Version\",\"First Master IP\",\"Second Master IP\",\"Clock Profile\",\"PTP Domain\"\n");
					if(cellDataH.get("clock_source").equals("PTP"))
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"\",\"\",\"\",\"itu-g8275-1\",\"24\"\n");
					}
					else
					{
						pw1.print("\"\",\"\",\"\",\"\",\"\",\"\"\n");
					}
					
					
					pw1.print("\"@PORT_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"Port ID\",\"Port AdministrativeState\",\"Connect Type\",\"UDE Type\",\"MTU\",\"Speed Duplex\",\"Fec Mode\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"unlocked\",\"backhaul\",\"ude-none\",\"1500\",\"\",\"off\"\n");
					
					pw1.print("\"@VIRTUAL_ROUTING_INFORMATION\"\n");
					pw1.print("\"NE ID\"\n");
					pw1.print("\"\"\n");
					String oamprelen = cellDataH.get("oamprelen").toString().replace("/", "");
					String ranprelen = cellDataH.get("ranprelen").toString().replace("/", "");
					String ranroutepre1 =  cellDataH.get("ranroutepre1").toString().split("/")[0];
					String ranroutepre2 =  cellDataH.get("ranroutepre2").toString().split("/")[0];
					
					pw1.print("\"@IP_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CPU ID\",\"External Interface Name\",\"IP Address\",\"IP Prefix Length\",\"IP Get Type\",\"Management\",\"Control\",\"Bearer\",\"IEEE1588\"\n");
					if(cellDataH.get("clock_source").equals("PTP"))
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("oamid") + "\",\"" + cellDataH.get("oamip") + "\",\"" + oamprelen + "\",\"static\",\"true\",\"false\",\"false\",\"true\"\n");
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("ranid") + "\",\"" + cellDataH.get("ranip") + "\",\"" + ranprelen + "\",\"static\",\"false\",\"true\",\"true\",\"true\"\n");					
					}
					else
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("oamid") + "\",\"" + cellDataH.get("oamip") + "\",\"" + oamprelen + "\",\"static\",\"true\",\"false\",\"false\",\"false\"\n");
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("ranid") + "\",\"" + cellDataH.get("ranip") + "\",\"" + ranprelen + "\",\"static\",\"false\",\"true\",\"true\",\"false\"\n");					
					}
					
					pw1.print("\"@VLAN_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CPU ID\",\"VLAN Interface Name\",\"VLAN ID\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0\",\"" + cellDataH.get("oamid") +"\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0\",\"" + cellDataH.get("ranid") +"\"\n");
					
					pw1.print("\"@LAG_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CPU ID\",\"LAG ID\",\"LAG Interface Name\"\n");
					pw1.print("\"\",\"\",\"\",\"\"\n");
					
					pw1.print("\"@ROUTE_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CPU ID\",\"IP Prefix\",\"IP Prefix Length\",\"IP Gateway\",\"Route Interface Name\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"" + ranroutepre1 + "\",\"" + ranprelen + "\",\"" + cellDataH.get("rangate") + "\",\"-\"\n" );
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"" + ranroutepre2 + "\",\"" + ranprelen + "\",\"" + cellDataH.get("rangate") + "\",\"-\"\n" );
					
					pw1.print("\"@SYSTEM_LOCATION_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"User Defined Mode\",\"Latitude\",\"Longitude\",\"Height\"\n");
					if(cellDataH.get("clock_source").equals("PTP"))
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"true\",\"" + cellDataH.get("latitude") + "\",\"" + cellDataH.get("longitude") + "\",\"" + cellDataH.get("height") + "\"");
					}
					else
					{
						pw1.print("\"\",\"false\",\"\",\"\",\"\"");
					}
					
					pw1.print("\n\"@RU_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"Start Frequency\"\n");
					pw1.print("\"\",\"\"\n");
					
					pw1.print("\"@IPSEC_INFORMATION\"\n");
					pw1.print(
							"\"NE ID\",\"CPU ID\",\"Interface Name1\",\"Peer IP Version\",\"First Peer IP\",\"Second Peer IP\",\"Inner IP Version\",\"Tunnel Mode\",\"Interface Name2\",\"Interface Name3\",\"Crypto Algorithm\",\"Hash Algorithm\",\"Local ID Type\",\"Local ID\"\n");

					pw1.print("\"\",\"0\",\"-\",\"ipv4\",\"\",\"\",\"ipv4\",\"client\",\"-\",\"-\",\"aes128\",\"sha1\",\"auto\",\"-\"\n");

					pw1.print("\"@PKI_INFORMATION\"\n");

					pw1.print(
							"\"NE ID\",\"CPU ID\",\"IP Address\",\"FQDN\",\"Port\",\"Path\",\"DN\",\"DN Domain\",\"CA DN\",\"Hash Algorithm\"\n");
					pw1.print("\"\",\"0\",\"\",\"\",\"80\",\"-\",\"-\",\"samsung.com\",\"-\",\"sha256\"");
					
				
				} 
				
//*********************************************** support  for  21 B for  IAU  Template***********************************************//
				else if(siteType.equalsIgnoreCase("IB") && (auType.isEmpty() || auType.equalsIgnoreCase("TBD")) && ver.equals("21.B.0")) {
					String autoFusing = "on";
					pw1.print("\"@DU\"\n");				
					pw1.print("\"NE ID\",\"NE Type\",\"NE Version\",\"Release Version\",\"Network\",\"NE Name\",\"GPL Version\",\"AdministrativeState\",\"gNB ID\",\"gNB ID Length\",\"gNB DU ID\",\"gNB DU Name\",\"Endpoint CU IP address\",\"Local Time Offset\",\"NE Serial Number\",\"FW Auto Fusing\"\n");				
					pw1.print("\"" + cellDataH.get("neid") +"\",\"gnb_au_sc\",\"" + ver + "\",\"" + relver + "\",\"" + cellDataH.get("network") + "\",\"GROW_" + cellDataH.get("auname") + "\",\"\",\"unlocked\",\"" +
							cellDataH.get("gndbid") + "\",\"22\",\"" + cellDataH.get("gndbauid") + "\",\""+ cellDataH.get("auname") + "\",\"" + cellDataH.get("remoteip") + "\",\"0\",\"\",\"on\"\n");
					pw1.print("\"@SERVER_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CFM\",\"PSM\"\n");
					pw1.print("\"\",\"\",\"\"\n");
					pw1.print("\"@MAIN_BOARD_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"Unit Type\",\"Unit ID\",\"Board Type\"\n");					
					pw1.print("\"" + cellDataH.get("neid") + "\",\"fmp\",\"0\",\"g7dc-a10\"\n");
					
				
					
					
					pw1.print("\"@CLOCK_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"Clock Source ID\",\"Clock Source\",\"Priority Level\",\"Quality Level\"\n");
					if(cellDataH.get("clock_source").equals("PTP"))
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"1\",\"ieee1588-phasetype\",\"1\",\"prc\"\n");
					}
					else
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"gps-type\",\"1\",\"prc\"\n");
					}
									
					pw1.print("\"@PTP_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"IP Version\",\"First Master IP\",\"Second Master IP\",\"Clock Profile\",\"PTP Domain\"\n");
					if(cellDataH.get("clock_source").equals("PTP"))
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"\",\"\",\"\",\"itu-g8275-1\",\"24\"\n");
					}
					else
					{
						pw1.print("\"\",\"\",\"\",\"\",\"\",\"\"\n");
					}
					
					
					pw1.print("\"@PORT_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"Port ID\",\"VR ID\",\"Port AdministrativeState\",\"Connect Type\",\"UDE Type\",\"MTU\",\"Speed Duplex\",\"Fec Mode\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"unlocked\",\"backhaul\",\"ude-none\",\"1500\",\"\",\"off\"\n");
					
					pw1.print("\"@VIRTUAL_ROUTING_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"VR ID\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\"\n");
					String oamprelen = cellDataH.get("oamprelen").toString().replace("/", "");
					String ranprelen = cellDataH.get("ranprelen").toString().replace("/", "");
					String ranroutepre1 =  cellDataH.get("ranroutepre1").toString().split("/")[0];
					String ranroutepre2 =  cellDataH.get("ranroutepre2").toString().split("/")[0];
					
					pw1.print("\"@IP_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CPU ID\",\"External Interface Name\",\"IP Address\",\"IP Prefix Length\",\"IP Get Type\",\"Management\",\"Control\",\"Bearer\",\"IEEE1588\"\n");
					if(cellDataH.get("clock_source").equals("PTP"))
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("oamid") + "\",\"" + cellDataH.get("oamip") + "\",\"" + oamprelen + "\",\"static\",\"true\",\"false\",\"false\",\"true\"\n");
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("ranid") + "\",\"" + cellDataH.get("ranip") + "\",\"" + ranprelen + "\",\"static\",\"false\",\"true\",\"true\",\"true\"\n");					
					}
					else
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("oamid") + "\",\"" + cellDataH.get("oamip") + "\",\"" + oamprelen + "\",\"static\",\"true\",\"false\",\"false\",\"false\"\n");
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("ranid") + "\",\"" + cellDataH.get("ranip") + "\",\"" + ranprelen + "\",\"static\",\"false\",\"true\",\"true\",\"false\"\n");					
					}
					
					pw1.print("\"@VLAN_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CPU ID\",\"VLAN Interface Name\",\"VLAN ID\",\"VR ID\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0\",\"" + cellDataH.get("oamid") +"\",\"0\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0\",\"" + cellDataH.get("ranid") +"\",\"0\"\n");
					
					pw1.print("\"@LAG_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CPU ID\",\"LAG Interface Name\",\"LAG ID\",\"VR ID\"\n");
					pw1.print("\"\",\"\",\"\",\"1\",\"0\"\n");
					
					//pw1.print("\"@ROUTE_INFORMATION\"\n");
					/*pw1.print("\"NE ID\",\"CPU ID\",\"VR ID\",\"Routr Interface Name\",\"IP Prefix\",\"IP Prefix Length\",\"IP Gateway\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"-\",\"" + ranroutepre1 + "\",\"" + ranprelen + "\",\"" + cellDataH.get("rangate") + "\"\n" );
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"-\",\"" + ranroutepre2 + "\",\"" + ranprelen + "\",\"" + cellDataH.get("rangate") + "\",\n" );
					*/
					//pw1.print("\"NE ID\",\"CPU ID\",\"VR ID\",\"IP Prefix\",\"IP Prefix Length\",\"IP Gateway\",\"Route Interface Name\"\n");
					//pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"" + ranroutepre1 + "\",\"" + ranprelen + "\",\"" + cellDataH.get("rangate") + "\",\"-\"\n" );
					//pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"" + ranroutepre2 + "\",\"" + ranprelen + "\",\"" + cellDataH.get("rangate") + "\",\n" );
					
					pw1.print("\"@ROUTE_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CPU ID\",\"VR ID\",\"Route Interface Name\",\"IP Prefix\",\"IP Prefix Length\",\"IP Gateway\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"-\",\"" + ranroutepre1 + "\",\"" + ranprelen + "\",\"" + cellDataH.get("rangate") + "\"\n" );
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"-\",\"" + ranroutepre2 + "\",\"" + ranprelen + "\",\"" + cellDataH.get("rangate") + "\"\n" );
					
					pw1.print("\"@SYSTEM_LOCATION_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"User Defined Mode\",\"Latitude\",\"Longitude\",\"Height\"\n");
					if(cellDataH.get("clock_source").equals("PTP"))
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"true\",\"" + cellDataH.get("latitude") + "\",\"" + cellDataH.get("longitude") + "\",\"" + cellDataH.get("height") + "\"");
					}
					else
					{
						pw1.print("\"\",\"false\",\"\",\"\",\"\"");
					}
					
					pw1.print("\n\"@RU_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"Start Frequency\"\n");
					pw1.print("\"" + cellDataH.get("neid") +"\",\"2750000\"\n");
					
					pw1.print("\"@IPSEC_INFORMATION\"\n");
					pw1.print(
							"\"NE ID\",\"CPU ID\",\"VR ID\",\"Interface Name1\",\"Peer IP Version\",\"First Peer IP\",\"Second Peer IP\",\"Inner IP Version\",\"Tunnel Mode\",\"Interface Name2\",\"Interface Name3\",\"Interface Name4\",\"Interface Name5\",\"Interface Name6\",\"Crypto Algorithm\",\"Hash Algorithm\",\"Local ID Type\",\"Local ID\"\n");

					pw1.print("\"\",\"\",\"0\",\"-\",\"ipv4\",\"\",\"\",\"ipv4\",\"client\",\"-\",\"-\",\"-\",\"-\",\"-\",\"aes128\",\"sha1\",\"auto\",\"-\"\n");

					pw1.print("\"@PKI_INFORMATION\"\n");

					pw1.print(
							"\"NE ID\",\"CPU ID\",\"IP Address\",\"FQDN\",\"Port\",\"Path\",\"DN\",\"DN Domain\",\"CA DN\",\"Hash Algorithm\"\n");
					pw1.print("\"\",\"0\",\"\",\"\",\"80\",\"-\",\"-\",\"samsung.com\",\"-\",\"sha256\"");
					
				
				} 
				
				
				
				
				
//*********************************************** support  for  21 B for  IAU  Template***********************************************//
				else if(siteType.equalsIgnoreCase("IB") && (auType.isEmpty() || auType.equalsIgnoreCase("TBD")) && ver.equals("21.D.0")) {
					String autoFusing = "on";
					pw1.print("\"@DU\"\n");				
					pw1.print("\"NE ID\",\"NE Type\",\"NE Version\",\"Release Version\",\"Network\",\"NE Name\",\"GPL Version\",\"AdministrativeState\",\"gNB ID\",\"gNB ID Length\",\"gNB DU ID\",\"gNB DU Name\",\"Endpoint CU IP address\",\"Local Time Offset\",\"NE Serial Number\",\"FW Auto Fusing\"\n");				
					pw1.print("\"" + cellDataH.get("neid") +"\",\"gnb_au_sc\",\"" + ver + "\",\"" + relver + "\",\"" + cellDataH.get("network") + "\",\"GROW_" + cellDataH.get("auname") + "\",\"\",\"unlocked\",\"" +
							cellDataH.get("gndbid") + "\",\"22\",\"" + cellDataH.get("gndbauid") + "\",\""+ cellDataH.get("auname") + "\",\"" + cellDataH.get("remoteip") + "\",\"0\",\"\",\"on\"\n");
					pw1.print("\"@SERVER_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CFM\",\"PSM\"\n");
					pw1.print("\"\",\"\",\"\"\n");
					pw1.print("\"@MAIN_BOARD_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"Unit Type\",\"Unit ID\",\"Board Type\"\n");
					String Btype="";
					if(cellDataH.get("nrfreq").equals("n261")) {
						Btype="g7dc-a10";
					}
					else if(cellDataH.get("nrfreq").equals("n260")) {
						Btype="g7dc-a14";
					}
					pw1.print("\"" + cellDataH.get("neid") + "\",\"fmp\",\"0\",\"" + Btype +"\"\n");
					
				
					
					
					pw1.print("\"@CLOCK_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"Clock Source ID\",\"Clock Source\",\"Priority Level\",\"Quality Level\"\n");
					if(cellDataH.get("clock_source").equals("PTP"))
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"1\",\"ieee1588-phasetype\",\"1\",\"prc\"\n");
					}
					else
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"gps-type\",\"1\",\"prc\"\n");
					}
									
					pw1.print("\"@PTP_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"IP Version\",\"First Master IP\",\"Second Master IP\",\"Clock Profile\",\"PTP Domain\"\n");
					if(cellDataH.get("clock_source").equals("PTP"))
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"\",\"\",\"\",\"itu-g8275-1\",\"24\"\n");
					}
					else
					{
						pw1.print("\"\",\"\",\"\",\"\",\"\",\"\"\n");
					}
					
					
					pw1.print("\"@PORT_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"Port ID\",\"VR ID\",\"Port AdministrativeState\",\"Connect Type\",\"UDE Type\",\"MTU\",\"Speed Duplex\",\"Fec Mode\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"unlocked\",\"backhaul\",\"ude-none\",\"1500\",\"\",\"off\"\n");
					
					pw1.print("\"@VIRTUAL_ROUTING_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"VR ID\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\"\n");
					String oamprelen = cellDataH.get("oamprelen").toString().replace("/", "");
					String ranprelen = cellDataH.get("ranprelen").toString().replace("/", "");
					String ranroutepre1 =  cellDataH.get("ranroutepre1").toString().split("/")[0];
					String ranroutepre2 =  cellDataH.get("ranroutepre2").toString().split("/")[0];
					
					pw1.print("\"@IP_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CPU ID\",\"External Interface Name\",\"IP Address\",\"IP Prefix Length\",\"IP Get Type\",\"Management\",\"Control\",\"Bearer\",\"IEEE1588\"\n");
					if(cellDataH.get("clock_source").equals("PTP"))
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("oamid") + "\",\"" + cellDataH.get("oamip") + "\",\"" + oamprelen + "\",\"static\",\"true\",\"false\",\"false\",\"true\"\n");
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("ranid") + "\",\"" + cellDataH.get("ranip") + "\",\"" + ranprelen + "\",\"static\",\"false\",\"true\",\"true\",\"true\"\n");					
					}
					else
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("oamid") + "\",\"" + cellDataH.get("oamip") + "\",\"" + oamprelen + "\",\"static\",\"true\",\"false\",\"false\",\"false\"\n");
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("ranid") + "\",\"" + cellDataH.get("ranip") + "\",\"" + ranprelen + "\",\"static\",\"false\",\"true\",\"true\",\"false\"\n");					
					}
					
					pw1.print("\"@VLAN_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CPU ID\",\"VLAN Interface Name\",\"VLAN ID\",\"VR ID\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0\",\"" + cellDataH.get("oamid") +"\",\"0\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0\",\"" + cellDataH.get("ranid") +"\",\"0\"\n");
					
					pw1.print("\"@LAG_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CPU ID\",\"LAG Interface Name\",\"LAG ID\",\"VR ID\"\n");
					pw1.print("\"\",\"\",\"\",\"1\",\"0\"\n");
					
					//pw1.print("\"@ROUTE_INFORMATION\"\n");
					/*pw1.print("\"NE ID\",\"CPU ID\",\"VR ID\",\"Routr Interface Name\",\"IP Prefix\",\"IP Prefix Length\",\"IP Gateway\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"-\",\"" + ranroutepre1 + "\",\"" + ranprelen + "\",\"" + cellDataH.get("rangate") + "\"\n" );
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"-\",\"" + ranroutepre2 + "\",\"" + ranprelen + "\",\"" + cellDataH.get("rangate") + "\",\n" );
					*/
					//pw1.print("\"NE ID\",\"CPU ID\",\"VR ID\",\"IP Prefix\",\"IP Prefix Length\",\"IP Gateway\",\"Route Interface Name\"\n");
					//pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"" + ranroutepre1 + "\",\"" + ranprelen + "\",\"" + cellDataH.get("rangate") + "\",\"-\"\n" );
					//pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"" + ranroutepre2 + "\",\"" + ranprelen + "\",\"" + cellDataH.get("rangate") + "\",\n" );
					
					pw1.print("\"@ROUTE_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CPU ID\",\"VR ID\",\"Route Interface Name\",\"IP Prefix\",\"IP Prefix Length\",\"IP Gateway\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"-\",\"" + ranroutepre1 + "\",\"" + ranprelen + "\",\"" + cellDataH.get("rangate") + "\"\n" );
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"-\",\"" + ranroutepre2 + "\",\"" + ranprelen + "\",\"" + cellDataH.get("rangate") + "\"\n" );
					
					pw1.print("\"@SYSTEM_LOCATION_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"User Defined Mode\",\"Latitude\",\"Longitude\",\"Height\"\n");
					if(cellDataH.get("clock_source").equals("PTP"))
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"true\",\"" + cellDataH.get("latitude") + "\",\"" + cellDataH.get("longitude") + "\",\"" + cellDataH.get("height") + "\"");
					}
					else
					{
						pw1.print("\"\",\"false\",\"\",\"\",\"\"");
					}
					/*
					pw1.print("\n\"@RU_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"Start Frequency\"\n");
					pw1.print("\"" + cellDataH.get("neid") +"\",\"\"\n");*/
					
					pw1.print("\n\"@IPSEC_INFORMATION\"\n");
					pw1.print(
							"\"NE ID\",\"CPU ID\",\"VR ID\",\"Interface Name1\",\"Peer IP Version\",\"First Peer IP\",\"Second Peer IP\",\"Inner IP Version\",\"Tunnel Mode\",\"Interface Name2\",\"Interface Name3\",\"Interface Name4\",\"Interface Name5\",\"Interface Name6\",\"Crypto Algorithm\",\"Hash Algorithm\",\"Local ID Type\",\"Local ID\"\n");

					pw1.print("\"\",\"\",\"0\",\"-\",\"ipv4\",\"\",\"\",\"ipv4\",\"client\",\"-\",\"-\",\"-\",\"-\",\"-\",\"aes128\",\"sha1\",\"auto\",\"-\"\n");

					pw1.print("\"@PKI_INFORMATION\"\n");

					pw1.print(
							"\"NE ID\",\"CPU ID\",\"IP Address\",\"FQDN\",\"Port\",\"Path\",\"DN\",\"DN Domain\",\"CA DN\",\"Hash Algorithm\"\n");
					pw1.print("\"\",\"0\",\"\",\"\",\"80\",\"-\",\"-\",\"samsung.com\",\"-\",\"sha256\"");
					
				
				} 
			
				
				else if (siteType.equalsIgnoreCase("IB") && (auType.isEmpty() || auType.equalsIgnoreCase("TBD"))) {

					
					pw1.print("\"@DU\"\n");				
					pw1.print("\"NE ID\",\"NE Type\",\"NE Version\",\"Release Version\",\"Network\",\"NE Name\",\"GPL Version\",\"AdministrativeState\",\"gNB ID\",\"gNB ID Length\",\"gNB DU ID\",\"gNB DU Name\",\"Endpoint CU IP address\",\"Local Time Offset\",\"NE Serial Number\",\"FW Auto Fusing\"\n");				
					pw1.print("\"" + cellDataH.get("neid") +"\",\"gnb_au_sc\",\"" + ver + "\",\"" + relver + "\",\"" + cellDataH.get("network") + "\",\"GROW_" + cellDataH.get("auname") + "\",\"\",\"unlocked\",\"" +
							cellDataH.get("gndbid") + "\",\"22\",\"" + cellDataH.get("gndbauid") + "\",\""+ cellDataH.get("auname") + "\",\"" + cellDataH.get("remoteip") + "\",\"0\",\"\",\"off\"\n");
					pw1.print("\"@SERVER_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CFM\",\"PSM\"\n");
					pw1.print("\"\",\"\",\"\"\n");
					pw1.print("\"@MAIN_BOARD_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"Unit Type\",\"Unit ID\",\"Board Type\"\n");					
					pw1.print("\"" + cellDataH.get("neid") + "\",\"fmp\",\"0\",\"g7dc-a10\"\n");
					
				
					
					
					pw1.print("\"@CLOCK_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"Clock Source ID\",\"Clock Source\",\"Priority Level\",\"Quality Level\"\n");
					if(cellDataH.get("clock_source").equals("PTP"))
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"1\",\"ieee1588-phasetype\",\"1\",\"prc\"\n");
					}
					else
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"gps-type\",\"1\",\"prc\"\n");
					}
									
					pw1.print("\"@PTP_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"IP Version\",\"First Master IP\",\"Second Master IP\",\"Clock Profile\",\"PTP Domain\"\n");
					if(cellDataH.get("clock_source").equals("PTP"))
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"\",\"\",\"\",\"itu-g8275-1\",\"24\"\n");
					}
					else
					{
						pw1.print("\"\",\"\",\"\",\"\",\"\",\"\"\n");
					}
					
					
					pw1.print("\"@PORT_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"Port ID\",\"VR ID\",\"Port AdministrativeState\",\"Connect Type\",\"UDE Type\",\"MTU\",\"Speed Duplex\",\"Fec Mode\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"unlocked\",\"backhaul\",\"ude-none\",\"1500\",\"\",\"off\"\n");
					
					pw1.print("\"@VIRTUAL_ROUTING_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"VR ID\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\"\n");
					String oamprelen = cellDataH.get("oamprelen").toString().replace("/", "");
					String ranprelen = cellDataH.get("ranprelen").toString().replace("/", "");
					String ranroutepre1 =  cellDataH.get("ranroutepre1").toString().split("/")[0];
					String ranroutepre2 =  cellDataH.get("ranroutepre2").toString().split("/")[0];
					
					pw1.print("\"@IP_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CPU ID\",\"External Interface Name\",\"IP Address\",\"IP Prefix Length\",\"IP Get Type\",\"Management\",\"Control\",\"Bearer\",\"IEEE1588\"\n");
					if(cellDataH.get("clock_source").equals("PTP"))
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("oamid") + "\",\"" + cellDataH.get("oamip") + "\",\"" + oamprelen + "\",\"static\",\"true\",\"false\",\"false\",\"true\"\n");
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("ranid") + "\",\"" + cellDataH.get("ranip") + "\",\"" + ranprelen + "\",\"static\",\"false\",\"true\",\"true\",\"true\"\n");					
					}
					else
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("oamid") + "\",\"" + cellDataH.get("oamip") + "\",\"" + oamprelen + "\",\"static\",\"true\",\"false\",\"false\",\"false\"\n");
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("ranid") + "\",\"" + cellDataH.get("ranip") + "\",\"" + ranprelen + "\",\"static\",\"false\",\"true\",\"true\",\"false\"\n");					
					}
					
					pw1.print("\"@VLAN_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CPU ID\",\"VLAN Interface Name\",\"VLAN ID\",\"VR ID\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0\",\"" + cellDataH.get("oamid") +"\",\"0\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0\",\"" + cellDataH.get("ranid") +"\",\"0\"\n");
					
					pw1.print("\"@LAG_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CPU ID\",\"LAG ID\",\"VR ID\",\"LAG Interface Name\"\n");
					pw1.print("\"\",\"0\",\"\",\"0\",\"\"\n");
					
					pw1.print("\"@ROUTE_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CPU ID\",\"VR ID\",\"IP Prefix\",\"IP Prefix Length\",\"IP Gateway\",\"Route Interface Name\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"" + ranroutepre1 + "\",\"" + ranprelen + "\",\"" + cellDataH.get("rangate") + "\",\"-\"\n" );
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"" + ranroutepre2 + "\",\"" + ranprelen + "\",\"" + cellDataH.get("rangate") + "\",\"-\"\n" );
					
					pw1.print("\"@SYSTEM_LOCATION_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"User Defined Mode\",\"Latitude\",\"Longitude\",\"Height\"\n");
					if(cellDataH.get("clock_source").equals("PTP"))
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"true\",\"" + cellDataH.get("latitude") + "\",\"" + cellDataH.get("longitude") + "\",\"" + cellDataH.get("height") + "\"");
					}
					else
					{
						pw1.print("\"\",\"false\",\"\",\"\",\"\"");
					}
					
					pw1.print("\n\"@RU_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"Start Frequency\"\n");
					pw1.print("\"\",\"\"\n");
					
					pw1.print("\"@IPSEC_INFORMATION\"\n");
					pw1.print(
							"\"NE ID\",\"CPU ID\",\"VR ID\",\"Interface Name1\",\"Peer IP Version\",\"First Peer IP\",\"Second Peer IP\",\"Inner IP Version\",\"Tunnel Mode\",\"Interface Name2\",\"Interface Name3\",\"Crypto Algorithm\",\"Hash Algorithm\",\"Local ID Type\",\"Local ID\"\n");

					pw1.print("\"\",\"0\",\"0\",\"-\",\"ipv4\",\"\",\"\",\"ipv4\",\"client\",\"-\",\"-\",\"aes128\",\"sha1\",\"auto\",\"-\"\n");

					pw1.print("\"@PKI_INFORMATION\"\n");

					pw1.print(
							"\"NE ID\",\"CPU ID\",\"IP Address\",\"FQDN\",\"Port\",\"Path\",\"DN\",\"DN Domain\",\"CA DN\",\"Hash Algorithm\"\n");
					pw1.print("\"\",\"0\",\"\",\"\",\"80\",\"-\",\"-\",\"samsung.com\",\"-\",\"sha256\"");
				} else if(version.contains("20C"))
				{
					pw1.print("\"@DU\"\n");				
					pw1.print("\"NE ID\",\"NE Type\",\"NE Version\",\"Release Version\",\"Network\",\"NE Name\",\"GPL Version\",\"AdministrativeState\",\"gNB ID\",\"gNB ID Length\",\"gNB DU ID\",\"gNB DU Name\",\"Endpoint CU IP address\",\"Local Time Offset\",\"NE Serial Number\",\"FW Auto Fusing\"\n");				
					pw1.print("\"" + cellDataH.get("neid") +"\",\"gnb_au\",\"" + ver + "\",\"" + relver + "\",\"" + cellDataH.get("network") + "\",\"GROW_" + cellDataH.get("auname") + "\",\"\",\"unlocked\",\"" +
							cellDataH.get("gndbid") + "\",\"22\",\"" + cellDataH.get("gndbauid") + "\",\""+ cellDataH.get("auname") + "\",\"" + cellDataH.get("remoteip") + "\",\"0\",\"\",\"off\"\n");
					pw1.print("\"@SERVER_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CFM\",\"PSM\"\n");
					pw1.print("\"\",\"\",\"\"\n");
					pw1.print("\"@MAIN_BOARD_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"Unit Type\",\"Unit ID\",\"Board Type\"\n");
					
					if(cellDataH.get("nrfreq").equals("n261"))
						
					{
						if( cellDataH.get("au_type").equals("GEN1"))
						{
							pw1.print("\"" + cellDataH.get("neid") + "\",\"fmp\",\"0\",\"g7da\"\n");
						}else {
							pw1.print("\"" + cellDataH.get("neid") + "\",\"fmp\",\"0\",\"g7dd-c01\"\n");
						}
					}else {
						pw1.print("\"" + cellDataH.get("neid") + "\",\"fmp\",\"0\",\"g7da-c01\"\n");
					}
					
				
					
					
					pw1.print("\"@CLOCK_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"Clock Source ID\",\"Clock Source\",\"Priority Level\",\"Quality Level\"\n");
					if(cellDataH.get("clock_source").equals("PTP"))
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"1\",\"ieee1588-phasetype\",\"1\",\"prc\"\n");
					}
					else
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"gps-type\",\"1\",\"prc\"\n");
					}
									
					pw1.print("\"@PTP_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"IP Version\",\"First Master IP\",\"Second Master IP\",\"Clock Profile\",\"PTP Domain\"\n");
					if(cellDataH.get("clock_source").equals("PTP"))
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"\",\"\",\"\",\"itu-g8275-1\",\"24\"\n");
					}
					else
					{
						pw1.print("\"\",\"\",\"\",\"\",\"\",\"\"\n");
					}
					
					
					pw1.print("\"@PORT_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"Port ID\",\"VR ID\",\"Port AdministrativeState\",\"Connect Type\",\"UDE Type\",\"MTU\",\"Speed Duplex\",\"Fec Mode\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"unlocked\",\"backhaul\",\"ude-none\",\"1500\",\"\",\"off\"\n");
					
					pw1.print("\"@VIRTUAL_ROUTING_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"VR ID\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\"\n");
					String oamprelen = cellDataH.get("oamprelen").toString().replace("/", "");
					String ranprelen = cellDataH.get("ranprelen").toString().replace("/", "");
					String ranroutepre1 =  cellDataH.get("ranroutepre1").toString().split("/")[0];
					String ranroutepre2 =  cellDataH.get("ranroutepre2").toString().split("/")[0];
					
					pw1.print("\"@IP_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CPU ID\",\"External Interface Name\",\"IP Address\",\"IP Prefix Length\",\"IP Get Type\",\"Management\",\"Control\",\"Bearer\",\"IEEE1588\"\n");
					if(cellDataH.get("clock_source").equals("PTP"))
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("oamid") + "\",\"" + cellDataH.get("oamip") + "\",\"" + oamprelen + "\",\"static\",\"true\",\"false\",\"false\",\"true\"\n");
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("ranid") + "\",\"" + cellDataH.get("ranip") + "\",\"" + ranprelen + "\",\"static\",\"false\",\"true\",\"true\",\"true\"\n");					
					}
					else
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("oamid") + "\",\"" + cellDataH.get("oamip") + "\",\"" + oamprelen + "\",\"static\",\"true\",\"false\",\"false\",\"false\"\n");
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("ranid") + "\",\"" + cellDataH.get("ranip") + "\",\"" + ranprelen + "\",\"static\",\"false\",\"true\",\"true\",\"false\"\n");					
					}
					
					pw1.print("\"@VLAN_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CPU ID\",\"VLAN Interface Name\",\"VLAN ID\",\"VR ID\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0\",\"" + cellDataH.get("oamid") +"\",\"0\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0\",\"" + cellDataH.get("ranid") +"\",\"0\"\n");
					
					pw1.print("\"@LAG_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CPU ID\",\"LAG ID\",\"VR ID\",\"LAG Interface Name\"\n");
					pw1.print("\"\",\"0\",\"\",\"0\",\"\"\n");
					
					pw1.print("\"@ROUTE_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CPU ID\",\"VR ID\",\"IP Prefix\",\"IP Prefix Length\",\"IP Gateway\",\"Route Interface Name\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"" + ranroutepre1 + "\",\"" + ranprelen + "\",\"" + cellDataH.get("rangate") + "\",\"-\"\n" );
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"" + ranroutepre2 + "\",\"" + ranprelen + "\",\"" + cellDataH.get("rangate") + "\",\"-\"\n" );
					
					pw1.print("\"@SYSTEM_LOCATION_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"User Defined Mode\",\"Latitude\",\"Longitude\",\"Height\"\n");
					if(cellDataH.get("clock_source").equals("PTP"))
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"true\",\"" + cellDataH.get("latitude") + "\",\"" + cellDataH.get("longitude") + "\",\"" + cellDataH.get("height") + "\"");
					}
					else
					{
						pw1.print("\"\",\"false\",\"\",\"\",\"\"");
					}
					
					if(cellDataH.get("nrfreq").equals("n260"))
					{
						pw1.print("\n\"@RU_INFORMATION\"\n");
						pw1.print("\"NE ID\",\"Start Frequency\"\n");
						pw1.print("\"" + cellDataH.get("neid") + "\",\"3750000\"\n");
					}else if(cellDataH.get("nrfreq").equals("n261"))
					{
						pw1.print("\n\"@RU_INFORMATION\"\n");
						pw1.print("\"NE ID\",\"Start Frequency\"\n");
						pw1.print("\"" + cellDataH.get("neid") + "\",\"2750000\"\n");
					}
					pw1.print("\"@IPSEC_INFORMATION\"\n");
					pw1.print(
							"\"NE ID\",\"CPU ID\",\"VR ID\",\"Interface Name1\",\"Peer IP Version\",\"First Peer IP\",\"Second Peer IP\",\"Inner IP Version\",\"Tunnel Mode\",\"Interface Name2\",\"Interface Name3\",\"Crypto Algorithm\",\"Hash Algorithm\",\"Local ID Type\",\"Local ID\"\n");

					pw1.print("\"\",\"\",\"-\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\"\n");

					pw1.print("\"@PKI_INFORMATION\"\n");

					pw1.print(
							"\"NE ID\",\"CPU ID\",\"IP Address\",\"FQDN\",\"Port\",\"Path\",\"DN\",\"DN Domain\",\"CA DN\",\"Hash Algorithm\"\n");
					pw1.print("\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\"");
					
				}
				else if(version.contains("21A") || version.contains("21B"))
				{
					String autoFusing = "off";
					if(version.contains("21B")) {
						autoFusing = "on";
					}
					pw1.print("\"@DU\"\n");				
					pw1.print("\"NE ID\",\"NE Type\",\"NE Version\",\"Release Version\",\"Network\",\"NE Name\",\"GPL Version\",\"AdministrativeState\",\"gNB ID\",\"gNB ID Length\",\"gNB DU ID\",\"gNB DU Name\",\"Endpoint CU IP address\",\"Local Time Offset\",\"NE Serial Number\",\"FW Auto Fusing\"\n");				
					pw1.print("\"" + cellDataH.get("neid") +"\",\"gnb_au\",\"" + ver + "\",\"" + relver + "\",\"" + cellDataH.get("network") + "\",\"GROW_" + cellDataH.get("auname") + "\",\"\",\"unlocked\",\"" +
							cellDataH.get("gndbid") + "\",\"22\",\"" + cellDataH.get("gndbauid") + "\",\""+ cellDataH.get("auname") + "\",\"" + cellDataH.get("remoteip") + "\",\"0\",\"\",\"" + autoFusing + "\"\n");
					pw1.print("\"@SERVER_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CFM\",\"PSM\"\n");
                                        pw1.print("\"\",\"\",\"\"\n");

					/*if(cellDataH.get("nrfreq").equals("n261"))
						
					{
						if( cellDataH.get("au_type").equals("GEN1"))
						{
							pw1.print("\"\",\"\",\"\"\n");
						}else {
							pw1.print("\"" + cellDataH.get("neid") + "\",\"CFMA0\",\"PSMA0\"\n");
						}
					}else {
						if( cellDataH.get("au_type").equals("GEN1"))
						{
							pw1.print("\"\",\"\",\"\"\n");
						}
						else {
							pw1.print("\"" + cellDataH.get("neid") + "\",\"CFMA0\",\"PSMA0\"\n");
						}
					}*/
					pw1.print("\"@MAIN_BOARD_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"Unit Type\",\"Unit ID\",\"Board Type\"\n");
					
					// 261 for 28ghz
					if(cellDataH.get("nrfreq").equals("n261"))
						
					{
						if( cellDataH.get("au_type").equals("GEN1"))
						{
							pw1.print("\"" + cellDataH.get("neid") + "\",\"fmp\",\"0\",\"g7da\"\n");
						}else {
							pw1.print("\"" + cellDataH.get("neid") + "\",\"fmp\",\"0\",\"g7dd-c01\"\n");
						}
					}else {
						if( cellDataH.get("au_type").equals("GEN1"))
						{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"fmp\",\"0\",\"g7da-c01\"\n");
						}
						else {
							pw1.print("\"" + cellDataH.get("neid") + "\",\"fmp\",\"0\",\"g7dd-c02\"\n");
						}
					}
					
					
					pw1.print("\"@CLOCK_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"Clock Source ID\",\"Clock Source\",\"Priority Level\",\"Quality Level\"\n");
					if(cellDataH.get("clock_source").equals("PTP"))
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"1\",\"ieee1588-phasetype\",\"1\",\"prc\"\n");
					}
					else
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"gps-type\",\"1\",\"prc\"\n");
					}
									
					pw1.print("\"@PTP_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"IP Version\",\"First Master IP\",\"Second Master IP\",\"Clock Profile\",\"PTP Domain\"\n");
					if(cellDataH.get("clock_source").equals("PTP"))
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"\",\"\",\"\",\"itu-g8275-1\",\"24\"\n");
					}
					else
					{
						pw1.print("\"\",\"\",\"\",\"\",\"\",\"\"\n");
					}
					
					
					pw1.print("\"@PORT_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"Port ID\",\"VR ID\",\"Port AdministrativeState\",\"Connect Type\",\"UDE Type\",\"MTU\",\"Speed Duplex\",\"Fec Mode\"\n");
					if(cellDataH.get("nrfreq").equals("n261")) {
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"unlocked\",\"backhaul\",\"ude-none\",\"1500\",\"\",\"off\"\n");
					}
					else
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"unlocked\",\"backhaul\",\"ude-none\",\"1500\",\"s10g-full\",\"off\"\n");
					}
					
					pw1.print("\"@VIRTUAL_ROUTING_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"VR ID\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\"\n");
					String oamprelen = cellDataH.get("oamprelen").toString().replace("/", "");
					String ranprelen = cellDataH.get("ranprelen").toString().replace("/", "");
					String ranroutepre1 =  cellDataH.get("ranroutepre1").toString().split("/")[0];
					String ranroutepre2 =  cellDataH.get("ranroutepre2").toString().split("/")[0];
					
					pw1.print("\"@IP_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CPU ID\",\"External Interface Name\",\"IP Address\",\"IP Prefix Length\",\"IP Get Type\",\"Management\",\"Control\",\"Bearer\",\"IEEE1588\"\n");
					if(cellDataH.get("clock_source").equals("PTP"))
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("oamid") + "\",\"" + cellDataH.get("oamip") + "\",\"" + oamprelen + "\",\"static\",\"true\",\"false\",\"false\",\"true\"\n");
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("ranid") + "\",\"" + cellDataH.get("ranip") + "\",\"" + ranprelen + "\",\"static\",\"false\",\"true\",\"true\",\"true\"\n");					
					}
					else
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("oamid") + "\",\"" + cellDataH.get("oamip") + "\",\"" + oamprelen + "\",\"static\",\"true\",\"false\",\"false\",\"false\"\n");
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("ranid") + "\",\"" + cellDataH.get("ranip") + "\",\"" + ranprelen + "\",\"static\",\"false\",\"true\",\"true\",\"false\"\n");					
					}
					
					pw1.print("\"@VLAN_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CPU ID\",\"VLAN Interface Name\",\"VLAN ID\",\"VR ID\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0\",\"" + cellDataH.get("oamid") +"\",\"0\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0\",\"" + cellDataH.get("ranid") +"\",\"0\"\n");
					
					pw1.print("\"@LAG_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CPU ID\",\"LAG ID\",\"VR ID\",\"LAG Interface Name\"\n");
					pw1.print("\"\",\"\",\"\",\"\",\"\"\n");
					
					pw1.print("\"@ROUTE_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CPU ID\",\"VR ID\",\"IP Prefix\",\"IP Prefix Length\",\"IP Gateway\",\"Route Interface Name\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"" + ranroutepre1 + "\",\"" + ranprelen + "\",\"" + cellDataH.get("rangate") + "\",\"-\"\n" );
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"" + ranroutepre2 + "\",\"" + ranprelen + "\",\"" + cellDataH.get("rangate") + "\",\"-\"\n" );
					
					pw1.print("\"@SYSTEM_LOCATION_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"User Defined Mode\",\"Latitude\",\"Longitude\",\"Height\"\n");
					if(cellDataH.get("clock_source").equals("PTP"))
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"true\",\"" + cellDataH.get("latitude") + "\",\"" + cellDataH.get("longitude") + "\",\"" + cellDataH.get("height") + "\"");
					}
					else
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"false\",\"" + cellDataH.get("latitude") + "\",\"" + cellDataH.get("longitude") + "\",\"" + cellDataH.get("height") + "\"");
						//pw1.print("\"\",\"false\",\"\",\"\",\"\"");
					}
					
					if(cellDataH.get("nrfreq").equals("n260"))
					{
						pw1.print("\n\"@RU_INFORMATION\"\n");
						pw1.print("\"NE ID\",\"Start Frequency\"\n");
						pw1.print("\"" + cellDataH.get("neid") + "\",\"3750000\"\n");
					}else if(cellDataH.get("nrfreq").equals("n261"))
					{
						pw1.print("\n\"@RU_INFORMATION\"\n");
						pw1.print("\"NE ID\",\"Start Frequency\"\n");
						pw1.print("\"" + cellDataH.get("neid") + "\",\"2750000\"\n");
					}
					pw1.print("\"@IPSEC_INFORMATION\"\n");
					pw1.print(
							"\"NE ID\",\"CPU ID\",\"VR ID\",\"Interface Name1\",\"Peer IP Version\",\"First Peer IP\",\"Second Peer IP\",\"Inner IP Version\",\"Tunnel Mode\",\"Interface Name2\",\"Interface Name3\",\"Crypto Algorithm\",\"Hash Algorithm\",\"Local ID Type\",\"Local ID\"\n");

					pw1.print("\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\"\n");

					pw1.print("\"@PKI_INFORMATION\"\n");

					pw1.print(
							"\"NE ID\",\"CPU ID\",\"IP Address\",\"FQDN\",\"Port\",\"Path\",\"DN\",\"DN Domain\",\"CA DN\",\"Hash Algorithm\"\n");
					pw1.print("\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\"");
					
				} else if (version.contains("21C")) {

					String autoFusing = "on";
					pw1.print("\"@DU\"\n");				
					pw1.print("\"NE ID\",\"NE Type\",\"NE Version\",\"Release Version\",\"Network\",\"NE Name\",\"GPL Version\",\"AdministrativeState\",\"gNB ID\",\"gNB ID Length\",\"gNB DU ID\",\"gNB DU Name\",\"Endpoint CU IP address\",\"Local Time Offset\",\"NE Serial Number\",\"FW Auto Fusing\",\"Maximum Supportable Coverage\"\n");				
					pw1.print("\"" + cellDataH.get("neid") +"\",\"gnb_au\",\"" + ver + "\",\"" + relver + "\",\"" + cellDataH.get("network") + "\",\"GROW_" + cellDataH.get("auname") + "\",\"\",\"unlocked\",\"" +
							cellDataH.get("gndbid") + "\",\"22\",\"" + cellDataH.get("gndbauid") + "\",\""+ cellDataH.get("auname") + "\",\"" + cellDataH.get("remoteip") + "\",\"0\",\"\",\"" + autoFusing + "\",\"normal-coverage\"\n");
					pw1.print("\"@SERVER_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CFM\",\"PSM\"\n");
                                        pw1.print("\"" + cellDataH.get("neid") +"\",\"CFMA0\",\"PSMA0\"\n");

					/*if(cellDataH.get("nrfreq").equals("n261"))
						
					{
						if( cellDataH.get("au_type").equals("GEN1"))
						{
							pw1.print("\"\",\"\",\"\"\n");
						}else {
							pw1.print("\"" + cellDataH.get("neid") + "\",\"CFMA0\",\"PSMA0\"\n");
						}
					}else {
						if( cellDataH.get("au_type").equals("GEN1"))
						{
							pw1.print("\"\",\"\",\"\"\n");
						}
						else {
							pw1.print("\"" + cellDataH.get("neid") + "\",\"CFMA0\",\"PSMA0\"\n");
						}
					}*/
					pw1.print("\"@MAIN_BOARD_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"Unit Type\",\"Unit ID\",\"Board Type\"\n");
					
					// 261 for 28ghz
					if(cellDataH.get("nrfreq").equals("n261"))
						
					{
						if( cellDataH.get("au_type").equals("GEN1"))
						{
							pw1.print("\"" + cellDataH.get("neid") + "\",\"fmp\",\"0\",\"g7da\"\n");
						}else {
							pw1.print("\"" + cellDataH.get("neid") + "\",\"fmp\",\"0\",\"g7dd-c01\"\n");
						}
					}else {
						if( cellDataH.get("au_type").equals("GEN1"))
						{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"fmp\",\"0\",\"g7da-c01\"\n");
						}
						else {
							pw1.print("\"" + cellDataH.get("neid") + "\",\"fmp\",\"0\",\"g7dd-c02\"\n");
						}
					}
					
					
					pw1.print("\"@CLOCK_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"Clock Source ID\",\"Clock Source\",\"Priority Level\",\"Quality Level\"\n");
					if(cellDataH.get("clock_source").equals("PTP"))
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"1\",\"ieee1588-phasetype\",\"1\",\"prc\"\n");
					}
					else
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"gps-type\",\"1\",\"prc\"\n");
					}
									
					pw1.print("\"@PTP_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"IP Version\",\"First Master IP\",\"Second Master IP\",\"Clock Profile\",\"PTP Domain\"\n");
					if(cellDataH.get("clock_source").equals("PTP"))
					{
						pw1.print("\"\",\"ipv4\",\"0.0.0.0\",\"0.0.0.0\",\"telecom-2008\",\"0\"\n");
					}
					else
					{
						pw1.print("\"\",\"ipv4\",\"0.0.0.0\",\"0.0.0.0\",\"telecom-2008\",\"0\"\n");
					}
					
					
					pw1.print("\"@PORT_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"Port ID\",\"VR ID\",\"Port AdministrativeState\",\"Connect Type\",\"UDE Type\",\"MTU\",\"Speed Duplex\",\"Fec Mode\"\n");
					if(cellDataH.get("nrfreq").equals("n261")) {
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"unlocked\",\"backhaul\",\"ude-none\",\"1500\",\"\",\"off\"\n");
					}
					else
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"unlocked\",\"backhaul\",\"ude-none\",\"1500\",\"s10g-full\",\"off\"\n");
					}
					
					pw1.print("\"@VIRTUAL_ROUTING_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"VR ID\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\"\n");
					String oamprelen = cellDataH.get("oamprelen").toString().replace("/", "");
					String ranprelen = cellDataH.get("ranprelen").toString().replace("/", "");
					String ranroutepre1 =  cellDataH.get("ranroutepre1").toString().split("/")[0];
					String ranroutepre2 =  cellDataH.get("ranroutepre2").toString().split("/")[0];
					
					pw1.print("\"@IP_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CPU ID\",\"External Interface Name\",\"IP Address\",\"IP Prefix Length\",\"IP Get Type\",\"Management\",\"Control\",\"Bearer\",\"IEEE1588\"\n");
					if(cellDataH.get("clock_source").equals("PTP"))
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("oamid") + "\",\"" + cellDataH.get("oamip") + "\",\"" + oamprelen + "\",\"static\",\"true\",\"false\",\"false\",\"true\"\n");
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("ranid") + "\",\"" + cellDataH.get("ranip") + "\",\"" + ranprelen + "\",\"static\",\"false\",\"true\",\"true\",\"true\"\n");					
					}
					else
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("oamid") + "\",\"" + cellDataH.get("oamip") + "\",\"" + oamprelen + "\",\"static\",\"true\",\"false\",\"false\",\"false\"\n");
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("ranid") + "\",\"" + cellDataH.get("ranip") + "\",\"" + ranprelen + "\",\"static\",\"false\",\"true\",\"true\",\"false\"\n");					
					}
					
					pw1.print("\"@VLAN_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CPU ID\",\"VLAN Interface Name\",\"VLAN ID\",\"VR ID\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0\",\"" + cellDataH.get("oamid") +"\",\"0\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0\",\"" + cellDataH.get("ranid") +"\",\"0\"\n");
					
					pw1.print("\"@LAG_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CPU ID\",\"LAG ID\",\"VR ID\",\"LAG Interface Name\"\n");
					pw1.print("\"\",\"\",\"\",\"\",\"\"\n");
					
					pw1.print("\"@ROUTE_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CPU ID\",\"VR ID\",\"IP Prefix\",\"IP Prefix Length\",\"IP Gateway\",\"Route Interface Name\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"" + ranroutepre1 + "\",\"" + ranprelen + "\",\"" + cellDataH.get("rangate") + "\",\"-\"\n" );
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"" + ranroutepre2 + "\",\"" + ranprelen + "\",\"" + cellDataH.get("rangate") + "\",\"-\"\n" );
					
					pw1.print("\"@SYSTEM_LOCATION_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"User Defined Mode\",\"Latitude\",\"Longitude\",\"Height\"\n");
					if(cellDataH.get("clock_source").equals("PTP"))
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"true\",\"" + cellDataH.get("latitude") + "\",\"" + cellDataH.get("longitude") + "\",\"" + cellDataH.get("height") + "\"");
					}
					else
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"false\",\"" + cellDataH.get("latitude") + "\",\"" + cellDataH.get("longitude") + "\",\"" + cellDataH.get("height") + "\"");
						//pw1.print("\"\",\"false\",\"\",\"\",\"\"");
					}
					
					if(cellDataH.get("nrfreq").equals("n260"))
					{
						pw1.print("\n\"@RU_INFORMATION\"\n");
						pw1.print("\"NE ID\",\"Start Frequency\"\n");
						pw1.print("\"" + cellDataH.get("neid") + "\",\"3750000\"\n");
					}else if(cellDataH.get("nrfreq").equals("n261"))
					{
						pw1.print("\n\"@RU_INFORMATION\"\n");
						pw1.print("\"NE ID\",\"Start Frequency\"\n");
						pw1.print("\"" + cellDataH.get("neid") + "\",\"2750000\"\n");
					}
					/*pw1.print("\"@IPSEC_INFORMATION\"\n");
					pw1.print(
							"\"NE ID\",\"CPU ID\",\"VR ID\",\"Interface Name1\",\"Peer IP Version\",\"First Peer IP\",\"Second Peer IP\",\"Inner IP Version\",\"Tunnel Mode\",\"Interface Name2\",\"Interface Name3\",\"Crypto Algorithm\",\"Hash Algorithm\",\"Local ID Type\",\"Local ID\"\n");

					pw1.print("\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\"\n");

					pw1.print("\"@PKI_INFORMATION\"\n");

					pw1.print(
							"\"NE ID\",\"CPU ID\",\"IP Address\",\"FQDN\",\"Port\",\"Path\",\"DN\",\"DN Domain\",\"CA DN\",\"Hash Algorithm\"\n");
					pw1.print("\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\"");*/
					
					pw1.print("\"@SON_INFORMATION\"\n");
					pw1.print("\"Initial PCI\",\"Initial RSI\",\"Initial NCRT\",\"Initial LTE NCRT\"\n");
					pw1.print("\"off\",\"off\",\"off\",\"off\"\n");
				
				}else if(version.contains("21D"))
				{
					String autoFusing = "on";
					
					pw1.print("\"@DU\"\n");				
					pw1.print("\"NE ID\",\"NE Type\",\"NE Version\",\"Release Version\",\"Network\",\"NE Name\",\"GPL Version\",\"AdministrativeState\",\"gNB ID\",\"gNB ID Length\",\"gNB DU ID\",\"gNB DU Name\",\"Endpoint CU IP address\",\"Local Time Offset\",\"NE Serial Number\",\"FW Auto Fusing\",\"Maximum Supportable Coverage\",\"A6G PUCCH CSI Coverage Enhancement\",\"A6G PUCCH HARQ Coverage Enhancement\"\n");				
					pw1.print("\"" + cellDataH.get("neid") +"\",\"gnb_au\",\"" + ver + "\",\"" + relver + "\",\"" + cellDataH.get("network") + "\",\"GROW_" + cellDataH.get("auname") + "\",\"\",\"unlocked\",\"" +
							cellDataH.get("gndbid") + "\",\"22\",\"" + cellDataH.get("gndbauid") + "\",\""+ cellDataH.get("auname") + "\",\"" + cellDataH.get("remoteip") + "\",\"0\",\"\",\"" + autoFusing + "\",\"normal-coverage\",\"a6-pucch-csi-coverage-enh-disable\",\"a6-pucch-harq-coverage-enh-disable\"\n");
					pw1.print("\"@SERVER_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CFM\",\"PSM\"\n");
                                        pw1.print("\"\",\"\",\"\"\n");

					
					pw1.print("\"@MAIN_BOARD_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"Unit Type\",\"Unit ID\",\"Board Type\"\n");
					
					// 261 for 28ghz
					if(cellDataH.get("nrfreq").equals("n261"))
						
					{
						if( cellDataH.get("au_type").equals("GEN1"))
						{
							pw1.print("\"" + cellDataH.get("neid") + "\",\"fmp\",\"0\",\"g7da\"\n");
						}else {
							pw1.print("\"" + cellDataH.get("neid") + "\",\"fmp\",\"0\",\"g7dd-c01\"\n");
						}
					}else {
						if( cellDataH.get("au_type").equals("GEN1"))
						{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"fmp\",\"0\",\"g7da-c01\"\n");
						}
						else {
							pw1.print("\"" + cellDataH.get("neid") + "\",\"fmp\",\"0\",\"g7dd-c02\"\n");
						}
					}
					
					
					pw1.print("\"@CLOCK_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"Clock Source ID\",\"Clock Source\",\"Priority Level\",\"Quality Level\"\n");
					if(cellDataH.get("clock_source").equals("PTP"))
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"1\",\"ieee1588-phasetype\",\"1\",\"prc\"\n");
					}
					else
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"gps-type\",\"1\",\"prc\"\n");
					}
									
					pw1.print("\"@PTP_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"IP Version\",\"First Master IP\",\"Second Master IP\",\"Clock Profile\",\"PTP Domain\"\n");
					if(cellDataH.get("clock_source").equals("PTP"))
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"\",\"\",\"\",\"itu-g8275-1\",\"24\"\n");
					}
					else
					{
						pw1.print("\"\",\"\",\"\",\"\",\"\",\"\"\n");
					}
					String SpeedDuplex="s10g-full";
					/*if(cellDataH.get("clock_source").equals("GPS")) {
						SpeedDuplex="s10g-full";
					}*/
					
					pw1.print("\"@PORT_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"Port ID\",\"VR ID\",\"Port AdministrativeState\",\"Connect Type\",\"UDE Type\",\"MTU\",\"Speed Duplex\",\"Fec Mode\"\n");
					if(cellDataH.get("nrfreq").equals("n261")) {
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"unlocked\",\"backhaul\",\"ude-none\",\"1500\",\"" + SpeedDuplex +"\",\"off\"\n");
					}
					else
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"unlocked\",\"backhaul\",\"ude-none\",\"1500\",\"" + SpeedDuplex + "\",\"off\"\n");
					}
					
					pw1.print("\"@VIRTUAL_ROUTING_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"VR ID\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\"\n");
					String oamprelen = cellDataH.get("oamprelen").toString().replace("/", "");
					String ranprelen = cellDataH.get("ranprelen").toString().replace("/", "");
					String ranroutepre1 =  cellDataH.get("ranroutepre1").toString().split("/")[0];
					String ranroutepre2 =  cellDataH.get("ranroutepre2").toString().split("/")[0];
					
					pw1.print("\"@IP_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CPU ID\",\"External Interface Name\",\"IP Address\",\"IP Prefix Length\",\"IP Get Type\",\"Management\",\"Control\",\"Bearer\",\"IEEE1588\"\n");
					if(cellDataH.get("clock_source").equals("PTP"))
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("oamid") + "\",\"" + cellDataH.get("oamip") + "\",\"" + oamprelen + "\",\"static\",\"true\",\"false\",\"false\",\"true\"\n");
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("ranid") + "\",\"" + cellDataH.get("ranip") + "\",\"" + ranprelen + "\",\"static\",\"false\",\"true\",\"true\",\"true\"\n");					
					}
					else
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("oamid") + "\",\"" + cellDataH.get("oamip") + "\",\"" + oamprelen + "\",\"static\",\"true\",\"false\",\"false\",\"false\"\n");
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("ranid") + "\",\"" + cellDataH.get("ranip") + "\",\"" + ranprelen + "\",\"static\",\"false\",\"true\",\"true\",\"false\"\n");					
					}
					
					pw1.print("\"@VLAN_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CPU ID\",\"VLAN Interface Name\",\"VLAN ID\",\"VR ID\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0\",\"" + cellDataH.get("oamid") +"\",\"0\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0\",\"" + cellDataH.get("ranid") +"\",\"0\"\n");
					
					pw1.print("\"@LAG_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CPU ID\",\"LAG ID\",\"VR ID\",\"LAG Interface Name\"\n");
					pw1.print("\"\",\"\",\"\",\"\",\"\"\n");
					
					pw1.print("\"@ROUTE_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CPU ID\",\"VR ID\",\"IP Prefix\",\"IP Prefix Length\",\"IP Gateway\",\"Route Interface Name\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"" + ranroutepre1 + "\",\"" + ranprelen + "\",\"" + cellDataH.get("rangate") + "\",\"-\"\n" );
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"" + ranroutepre2 + "\",\"" + ranprelen + "\",\"" + cellDataH.get("rangate") + "\",\"-\"\n" );
					
					pw1.print("\"@SYSTEM_LOCATION_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"User Defined Mode\",\"Latitude\",\"Longitude\",\"Height\"\n");
					if(cellDataH.get("clock_source").equals("PTP"))
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"true\",\"" + cellDataH.get("latitude") + "\",\"" + cellDataH.get("longitude") + "\",\"" + cellDataH.get("height") + "\"");
					}
					else
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"false\",\"" + cellDataH.get("latitude") + "\",\"" + cellDataH.get("longitude") + "\",\"" + cellDataH.get("height") + "\"");
						//pw1.print("\"\",\"false\",\"\",\"\",\"\"");
					}
					
					if(cellDataH.get("nrfreq").equals("n260"))
					{
						pw1.print("\n\"@RU_INFORMATION\"\n");
						pw1.print("\"NE ID\",\"Start Frequency\",\"Uncertainty Semi Major\",\"Uncertainty Semi Minor\",\"Orientation Of Major Axis\",\"Uncertainty Altitude\",\"Confidence\"\n");
						pw1.print("\"" + cellDataH.get("neid") + "\",\"3750000\",\"8\",\"8\",\"0\",\"0\",\"100\"\n");
					}else if(cellDataH.get("nrfreq").equals("n261"))
					{
						pw1.print("\n\"@RU_INFORMATION\"\n");
						pw1.print("\"NE ID\",\"Start Frequency\",\"Uncertainty Semi Major\",\"Uncertainty Semi Minor\",\"Orientation Of Major Axis\",\"Uncertainty Altitude\",\"Confidence\"\n");
						pw1.print("\"" + cellDataH.get("neid") + "\",\"2750000\",\"8\",\"8\",\"0\",\"0\",\"100\"\n");
					}
					
				}
				else {
				
				pw1.print("\"@DU\"\n");				
				pw1.print("\"NE ID\",\"NE Type\",\"NE Version\",\"Release Version\",\"Network\",\"NE Name\",\"AdministrativeState\",\"gNB ID\",\"gNB ID Length\",\"gNB DU ID\",\"gNB DU Name\",\"Endpoint CU IP address\",\"Time Offset\",\"NE Serial Number\"\n");				
				pw1.print("\"" + cellDataH.get("neid") +"\",\"gnb_au\",\"" + ver + "\",\"" + relver + "\",\"" + cellDataH.get("network") + "\",\"GROW_" + cellDataH.get("auname") + "\",\"unlocked\",\"" +
						cellDataH.get("gndbid") + "\",\"22\",\"" + cellDataH.get("gndbauid") + "\",\""+ cellDataH.get("auname") + "\",\"" + cellDataH.get("remoteip") + "\",\"0\",\"\"\n");
				
				pw1.print("\"@MAIN_BOARD_INFORMATION\"\n");
				pw1.print("\"NE ID\",\"Unit Type\",\"Unit ID\",\"Board Type\"\n");
				if(cellDataH.get("nrfreq").equals("n260"))
				{
					pw1.print("\"" + cellDataH.get("neid") + "\",\"fmp\",\"0\",\"g7da-c01\"\n");
				}
				else
				{
					pw1.print("\"" + cellDataH.get("neid") + "\",\"fmp\",\"0\",\"g7da\"\n");
				}
				
				
				pw1.print("\"@CLOCK_INFORMATION\"\n");
				pw1.print("\"NE ID\",\"Clock Source ID\",\"Clock Source\",\"Priority Level\",\"Quality Level\"\n");
				if(cellDataH.get("clock_source").equals("PTP"))
				{
					pw1.print("\"" + cellDataH.get("neid") + "\",\"1\",\"ieee1588-phasetype\",\"1\",\"prc\"\n");
				}
				else
				{
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"gps-type\",\"1\",\"prc\"\n");
				}
								
				pw1.print("\"@PTP_INFORMATION\"\n");
				pw1.print("\"NE ID\",\"IP Version\",\"First Master IP\",\"Second Master IP\",\"Clock Profile\",\"PTP Domain\"\n");
				if(cellDataH.get("clock_source").equals("PTP"))
				{
					pw1.print("\"" + cellDataH.get("neid") + "\",\"\",\"\",\"\",\"itu-g8275-1\",\"24\"\n");
				}
				else
				{
					pw1.print("\"\",\"\",\"\",\"\",\"\",\"\"\n");
				}
				
				
				pw1.print("\"@PORT_INFORMATION\"\n");
				pw1.print("\"NE ID\",\"Port ID\",\"Port AdministrativeState\",\"Connect Type\",\"UDE Type\",\"MTU\",\"Speed Duplex\",\"Fec Mode\"\n");
				pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"unlocked\",\"backhaul\",\"ude-none\",\"1500\",\"\",\"off\"\n");
				
				
				String oamprelen = cellDataH.get("oamprelen").toString().replace("/", "");
				String ranprelen = cellDataH.get("ranprelen").toString().replace("/", "");
				String ranroutepre1 =  cellDataH.get("ranroutepre1").toString().split("/")[0];
				String ranroutepre2 =  cellDataH.get("ranroutepre2").toString().split("/")[0];
				
				pw1.print("\"@IP_INFORMATION\"\n");
				pw1.print("\"NE ID\",\"CPU ID\",\"External Interface Name\",\"IP Address\",\"IP Prefix Length\",\"IP Get Type\",\"Management\",\"Control\",\"Bearer\",\"IEEE1588\"\n");
				if(cellDataH.get("clock_source").equals("PTP"))
				{
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("oamid") + "\",\"" + cellDataH.get("oamip") + "\",\"" + oamprelen + "\",\"static\",\"true\",\"false\",\"false\",\"true\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("ranid") + "\",\"" + cellDataH.get("ranip") + "\",\"" + ranprelen + "\",\"static\",\"false\",\"true\",\"true\",\"true\"\n");					
				}
				else
				{
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("oamid") + "\",\"" + cellDataH.get("oamip") + "\",\"" + oamprelen + "\",\"static\",\"true\",\"false\",\"false\",\"false\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("ranid") + "\",\"" + cellDataH.get("ranip") + "\",\"" + ranprelen + "\",\"static\",\"false\",\"true\",\"true\",\"false\"\n");					
				}
				
				pw1.print("\"@VLAN_INFORMATION\"\n");
				pw1.print("\"NE ID\",\"CPU ID\",\"VLAN Interface Name\",\"VLAN ID\"\n");
				pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0\",\"" + cellDataH.get("oamid") +"\"\n");
				pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0\",\"" + cellDataH.get("ranid") +"\"\n");
				
				pw1.print("\"@LAG_INFORMATION\"\n");
				pw1.print("\"NE ID\",\"CPU ID\",\"LAG Interface Name\",\"LAG ID\"\n");
				pw1.print("\"\",\"\",\"\",\"\"\n");
				
				pw1.print("\"@ROUTE_INFORMATION\"\n");
				pw1.print("\"NE ID\",\"CPU ID\",\"IP Prefix\",\"IP Prefix Length\",\"IP Gateway\",\"Route Interface Name\"\n");
				pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"" + ranroutepre1 + "\",\"" + ranprelen + "\",\"" + cellDataH.get("rangate") + "\",\"-\"\n" );
				pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"" + ranroutepre2 + "\",\"" + ranprelen + "\",\"" + cellDataH.get("rangate") + "\",\"-\"\n" );
				
				pw1.print("\"@SYSTEM_LOCATION_INFORMATION\"\n");
				pw1.print("\"NE ID\",\"User Defined Mode\",\"Latitude\",\"Longitude\",\"Height\"\n");
				if(cellDataH.get("clock_source").equals("PTP"))
				{
					pw1.print("\"" + cellDataH.get("neid") + "\",\"true\",\"" + cellDataH.get("latitude") + "\",\"" + cellDataH.get("longitude") + "\",\"" + cellDataH.get("height") + "\"");
				}
				else
				{
					pw1.print("\"\",\"false\",\"\",\"\",\"\"");
				}
				
				if(cellDataH.get("nrfreq").equals("n260"))
				{
					pw1.print("\n\"@RU_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"Start Frequency\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"3750000\"");
				}
				
				}
			
			}
			catch(Exception e)
			{
				mydie(e.toString());
			}
			finally
			{
				if(pw1!=null)
				{
					pw1.flush();
					pw1.close();
				}					
			}
			
		}
		return "AU_"+ version +"_"+auType+"_" + gndbAuId.get(gnbidx) + "_" + timestamp + ".csv";
	}
	
	//support for 22A
	@SuppressWarnings("unchecked")
	private String createCellTemplate_22A(ArrayList<HashMap<String, Object>> cellDataRef,String auType) {
		
		String growfile1 = growfolder + "/AU_"+ version +"_"+auType+"_" + gndbAuId.get(gnbidx) + "_" + timestamp + ".csv";
		
		for(int i=0;i<cellDataRef.size();i++)
		{
			HashMap<String,Object> cellDataH = cellDataRef.get(i);
			//System.out.println(cellDataH);
			String ver =  "";
			if(version.contains("22A"))
			{
				ver = "22.A.0";
			}else if(version.contains("22C"))
			{
				ver = "22.C.0";
			}
		
			PrintWriter pw1 =null;
			try
			{		
				
				File f1 =  new File(growfile1);
				f1.createNewFile();
				pw1 = new PrintWriter(new BufferedWriter(new FileWriter(f1)));
				String siteType = cellDataH.get("site").toString();
				
				//IAU
				if(siteType.equalsIgnoreCase("IB") && (auType.isEmpty() || auType.equalsIgnoreCase("TBD")) && ver.contains("22")) {
					String autoFusing = "on";
					pw1.print("\"@DU\"\n");				
					pw1.print("\"NE ID\",\"NE Type\",\"NE Version\",\"Release Version\",\"Network\",\"NE Name\",\"GPL Version\",\"Administrative State\",\"gNB ID\",\"gNB ID Length\",\"gNB DU ID\",\"gNB DU Name\",\"Endpoint CU IP address\",\"Local Time Offset\",\"NE Serial Number\",\"FW Auto Fusing\",\"A6G PUCCH CSI Coverage Enhancement\",\"A6G PUCCH HARQ Coverage Enhancement\"\n");				
					pw1.print("\"" + cellDataH.get("neid") +"\",\"gnb_au_sc\",\"" + ver + "\",\"" + relver + "\",\"" + cellDataH.get("network") + "\",\"GROW_" + cellDataH.get("auname") + "\",\"\",\"unlocked\",\"" +
							cellDataH.get("gndbid") + "\",\"22\",\"" + cellDataH.get("gndbauid") + "\",\""+ cellDataH.get("auname") + "\",\"" + cellDataH.get("remoteip") + "\",\"0\",\"\",\"on\",\"a6-pucch-csi-coverage-enh-disable\",\"a6-pucch-harq-coverage-enh-enable\"\n");
					pw1.print("\"@SERVER_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CFM\",\"PSM\"\n");
					pw1.print("\"\",\"\",\"\"\n");
					pw1.print("\"@MAIN_BOARD_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"Unit Type\",\"Unit ID\",\"Board Type\"\n");
					String Btype="";
					if(cellDataH.get("nrfreq").equals("n261")) {
						Btype="g7dc-a10";
					}
					else if(cellDataH.get("nrfreq").equals("n260")) {
						Btype="g7dc-a14";
					}
					pw1.print("\"" + cellDataH.get("neid") + "\",\"fmp\",\"0\",\"" + Btype +"\"\n");
					
				
					
					
					pw1.print("\"@CLOCK_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"Clock Source ID\",\"Clock Source\",\"Priority Level\",\"Quality Level\"\n");
					if(cellDataH.get("clock_source").equals("PTP"))
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"1\",\"ieee1588-phasetype\",\"1\",\"prc\"\n");
					}
					else
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"gps-type\",\"1\",\"prc\"\n");
					}
									
					pw1.print("\"@PTP_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"IP Version\",\"First Master IP\",\"Second Master IP\",\"Clock Profile\",\"PTP Domain\",\"PTP Hybrid Mode\"\n");
					if(cellDataH.get("clock_source").equals("PTP"))
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"\",\"\",\"\",\"itu-g8275-1\",\"24\",\"disable\"\n");
					}
					else
					{
						pw1.print("\"\",\"\",\"\",\"\",\"\",\"\"\n");
					}
					
					
					pw1.print("\"@PORT_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"Port ID\",\"VR ID\",\"Port Administrative State\",\"Connect Type\",\"UDE Type\",\"MTU\",\"Speed Duplex\",\"Fec Mode\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"unlocked\",\"backhaul\",\"ude-none\",\"1500\",\"\",\"off\"\n");
					
					pw1.print("\"@VIRTUAL_ROUTING_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"VR ID\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\"\n");
					String oamprelen = cellDataH.get("oamprelen").toString().replace("/", "");
					String ranprelen = cellDataH.get("ranprelen").toString().replace("/", "");
					String ranroutepre1 =  cellDataH.get("ranroutepre1").toString().split("/")[0];
					String ranroutepre2 =  cellDataH.get("ranroutepre2").toString().split("/")[0];
					
					pw1.print("\"@IP_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CPU ID\",\"External Interface Name\",\"IP Address\",\"IP Prefix Length\",\"IP Get Type\",\"Management\",\"Control\",\"Bearer\",\"IEEE1588\"\n");
					if(cellDataH.get("clock_source").equals("PTP"))
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("oamid") + "\",\"" + cellDataH.get("oamip") + "\",\"" + oamprelen + "\",\"static\",\"true\",\"false\",\"false\",\"true\"\n");
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("ranid") + "\",\"" + cellDataH.get("ranip") + "\",\"" + ranprelen + "\",\"static\",\"false\",\"true\",\"true\",\"false\"\n");					
					}
					else
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("oamid") + "\",\"" + cellDataH.get("oamip") + "\",\"" + oamprelen + "\",\"static\",\"true\",\"false\",\"false\",\"false\"\n");
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("ranid") + "\",\"" + cellDataH.get("ranip") + "\",\"" + ranprelen + "\",\"static\",\"false\",\"true\",\"true\",\"false\"\n");					
					}
					
					pw1.print("\"@VLAN_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CPU ID\",\"VLAN Interface Name\",\"VLAN ID\",\"VR ID\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0\",\"" + cellDataH.get("oamid") +"\",\"0\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0\",\"" + cellDataH.get("ranid") +"\",\"0\"\n");
					
					pw1.print("\"@LAG_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CPU ID\",\"LAG Interface Name\",\"LAG ID\",\"VR ID\"\n");
					pw1.print("\"\",\"\",\"\",\"1\",\"0\"\n");
					
					//pw1.print("\"@ROUTE_INFORMATION\"\n");
					/*pw1.print("\"NE ID\",\"CPU ID\",\"VR ID\",\"Routr Interface Name\",\"IP Prefix\",\"IP Prefix Length\",\"IP Gateway\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"-\",\"" + ranroutepre1 + "\",\"" + ranprelen + "\",\"" + cellDataH.get("rangate") + "\"\n" );
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"-\",\"" + ranroutepre2 + "\",\"" + ranprelen + "\",\"" + cellDataH.get("rangate") + "\",\n" );
					*/
					//pw1.print("\"NE ID\",\"CPU ID\",\"VR ID\",\"IP Prefix\",\"IP Prefix Length\",\"IP Gateway\",\"Route Interface Name\"\n");
					//pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"" + ranroutepre1 + "\",\"" + ranprelen + "\",\"" + cellDataH.get("rangate") + "\",\"-\"\n" );
					//pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"" + ranroutepre2 + "\",\"" + ranprelen + "\",\"" + cellDataH.get("rangate") + "\",\n" );
					
					pw1.print("\"@ROUTE_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CPU ID\",\"VR ID\",\"Route Interface Name\",\"IP Prefix\",\"IP Prefix Length\",\"IP Gateway\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"-\",\"" + ranroutepre1 + "\",\"" + ranprelen + "\",\"" + cellDataH.get("rangate") + "\"\n" );
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"-\",\"" + ranroutepre2 + "\",\"" + ranprelen + "\",\"" + cellDataH.get("rangate") + "\"\n" );
					
					pw1.print("\"@SYSTEM_LOCATION_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"User Defined Mode\",\"Latitude\",\"Longitude\",\"Height\"\n");
					if(cellDataH.get("clock_source").equals("PTP"))
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"true\",\"" + cellDataH.get("latitude") + "\",\"" + cellDataH.get("longitude") + "\",\"" + cellDataH.get("height") + "\"");
					}
					else
					{
						pw1.print("\"\",\"false\",\"\",\"\",\"\"");
					}
					/*
					pw1.print("\n\"@RU_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"Start Frequency\"\n");
					pw1.print("\"" + cellDataH.get("neid") +"\",\"\"\n");*/
					
					
					pw1.print("\n\"@IPSEC_INFORMATION\"\n");
					pw1.print(
							"\"NE ID\",\"CPU ID\",\"VR ID\",\"Interface Name1\",\"Peer IP Version\",\"First Peer IP\",\"Second Peer IP\",\"Inner IP Version\",\"Tunnel Mode\",\"Interface Name2\",\"Interface Name3\",\"Interface Name4\",\"Interface Name5\",\"Interface Name6\",\"Crypto Algorithm\",\"Hash Algorithm\",\"Local ID Type\",\"Local ID\"\n");

					pw1.print("\"\",\"\",\"0\",\"-\",\"ipv4\",\"\",\"\",\"ipv4\",\"client\",\"-\",\"-\",\"-\",\"-\",\"-\",\"aes128\",\"sha1\",\"auto\",\"-\"\n");

					pw1.print("\"@PKI_INFORMATION\"\n");

					pw1.print(
							"\"NE ID\",\"CPU ID\",\"IP Address\",\"FQDN\",\"Port\",\"Path\",\"DN\",\"DN Domain\",\"CA DN\",\"Hash Algorithm\"\n");
					pw1.print("\"\",\"0\",\"\",\"\",\"80\",\"-\",\"-\",\"samsung.com\",\"-\",\"sha256\"\n");
					
//					pw1.print("\"@CSL_TCE_INFORMATION\"\n");
//					pw1.print(
//							"\"NE ID\",\"CSL TCE Server IP Address\",\"CSL TCE Server Port\",\"CSL TCE Option\"\n");
//					pw1.print("\"\",\"2404:180:1002:1:dc22::10\",\"50021\",\"normal-and-abnormal-and-intra-ho-call\"\n");
					
				
				} 
				else if(version.contains("22A"))
				{
					String autoFusing = "on";
					
					pw1.print("\"@DU\"\n");				
					pw1.print("\"NE ID\",\"NE Type\",\"NE Version\",\"Release Version\",\"Network\",\"NE Name\",\"GPL Version\",\"AdministrativeState\",\"gNB ID\",\"gNB ID Length\",\"gNB DU ID\",\"gNB DU Name\",\"Endpoint CU IP address\",\"Local Time Offset\",\"NE Serial Number\",\"FW Auto Fusing\",\"Maximum Supportable Coverage\",\"A6G PUCCH CSI Coverage Enhancement\",\"A6G PUCCH HARQ Coverage Enhancement\"\n");				
					pw1.print("\"" + cellDataH.get("neid") +"\",\"gnb_au\",\"" + ver + "\",\"" + relver + "\",\"" + cellDataH.get("network") + "\",\"GROW_" + cellDataH.get("auname") + "\",\"\",\"unlocked\",\"" +
							cellDataH.get("gndbid") + "\",\"22\",\"" + cellDataH.get("gndbauid") + "\",\""+ cellDataH.get("auname") + "\",\"" + cellDataH.get("remoteip") + "\",\"0\",\"\",\"" + autoFusing + "\",\"normal-coverage\",\"a6-pucch-csi-coverage-enh-disable\",\"a6-pucch-harq-coverage-enh-disable\"\n");
					pw1.print("\"@SERVER_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CFM\",\"PSM\"\n");
                                        pw1.print("\"\",\"\",\"\"\n");

					
					pw1.print("\"@MAIN_BOARD_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"Unit Type\",\"Unit ID\",\"Board Type\"\n");
					
					// 261 for 28ghz
					if(cellDataH.get("nrfreq").equals("n261"))
						
					{
						if( cellDataH.get("au_type").equals("GEN1"))
						{
							pw1.print("\"" + cellDataH.get("neid") + "\",\"fmp\",\"0\",\"g7da\"\n");
						}else {
							pw1.print("\"" + cellDataH.get("neid") + "\",\"fmp\",\"0\",\"g7dd-c01\"\n");
						}
					}else {
						if( cellDataH.get("au_type").equals("GEN1"))
						{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"fmp\",\"0\",\"g7da-c01\"\n");
						}
						else {
							pw1.print("\"" + cellDataH.get("neid") + "\",\"fmp\",\"0\",\"g7dd-c02\"\n");
						}
					}
					
					
					pw1.print("\"@CLOCK_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"Clock Source ID\",\"Clock Source\",\"Priority Level\",\"Quality Level\"\n");
					if(cellDataH.get("clock_source").equals("PTP"))
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"1\",\"ieee1588-phasetype\",\"1\",\"prc\"\n");
					}
					else
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"gps-type\",\"1\",\"prc\"\n");
					}
									
					pw1.print("\"@PTP_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"IP Version\",\"First Master IP\",\"Second Master IP\",\"Clock Profile\",\"PTP Domain\"\n");
					if(cellDataH.get("clock_source").equals("PTP"))
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"\",\"\",\"\",\"itu-g8275-1\",\"24\"\n");
					}
					else
					{
						pw1.print("\"\",\"\",\"\",\"\",\"\",\"\"\n");
					}
					String SpeedDuplex="s10g-full";
					
					
					pw1.print("\"@PORT_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"Port ID\",\"VR ID\",\"Port AdministrativeState\",\"Connect Type\",\"UDE Type\",\"MTU\",\"Speed Duplex\",\"Fec Mode\"\n");
					if(cellDataH.get("nrfreq").equals("n261")) {
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"unlocked\",\"backhaul\",\"ude-none\",\"1500\",\"" + SpeedDuplex +"\",\"off\"\n");
					}
					else
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"unlocked\",\"backhaul\",\"ude-none\",\"1500\",\"" + SpeedDuplex + "\",\"off\"\n");
					}
					
					pw1.print("\"@VIRTUAL_ROUTING_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"VR ID\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\"\n");
					String oamprelen = cellDataH.get("oamprelen").toString().replace("/", "");
					String ranprelen = cellDataH.get("ranprelen").toString().replace("/", "");
					String ranroutepre1 =  cellDataH.get("ranroutepre1").toString().split("/")[0];
					String ranroutepre2 =  cellDataH.get("ranroutepre2").toString().split("/")[0];
					
					pw1.print("\"@IP_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CPU ID\",\"External Interface Name\",\"IP Address\",\"IP Prefix Length\",\"IP Get Type\",\"Management\",\"Control\",\"Bearer\",\"IEEE1588\"\n");
					if(cellDataH.get("clock_source").equals("PTP"))
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("oamid") + "\",\"" + cellDataH.get("oamip") + "\",\"" + oamprelen + "\",\"static\",\"true\",\"false\",\"false\",\"true\"\n");
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("ranid") + "\",\"" + cellDataH.get("ranip") + "\",\"" + ranprelen + "\",\"static\",\"false\",\"true\",\"true\",\"true\"\n");					
					}
					else
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("oamid") + "\",\"" + cellDataH.get("oamip") + "\",\"" + oamprelen + "\",\"static\",\"true\",\"false\",\"false\",\"false\"\n");
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("ranid") + "\",\"" + cellDataH.get("ranip") + "\",\"" + ranprelen + "\",\"static\",\"false\",\"true\",\"true\",\"false\"\n");					
					}
					
					pw1.print("\"@VLAN_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CPU ID\",\"VLAN Interface Name\",\"VLAN ID\",\"VR ID\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0\",\"" + cellDataH.get("oamid") +"\",\"0\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0\",\"" + cellDataH.get("ranid") +"\",\"0\"\n");
					
					pw1.print("\"@LAG_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CPU ID\",\"LAG ID\",\"VR ID\",\"LAG Interface Name\"\n");
					pw1.print("\"\",\"\",\"\",\"\",\"\"\n");
					
					pw1.print("\"@ROUTE_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CPU ID\",\"VR ID\",\"IP Prefix\",\"IP Prefix Length\",\"IP Gateway\",\"Route Interface Name\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"" + ranroutepre1 + "\",\"" + ranprelen + "\",\"" + cellDataH.get("rangate") + "\",\"-\"\n" );
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"" + ranroutepre2 + "\",\"" + ranprelen + "\",\"" + cellDataH.get("rangate") + "\",\"-\"\n" );
					
					pw1.print("\"@SYSTEM_LOCATION_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"User Defined Mode\",\"Latitude\",\"Longitude\",\"Height\"\n");
					if(cellDataH.get("clock_source").equals("PTP"))
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"true\",\"" + cellDataH.get("latitude") + "\",\"" + cellDataH.get("longitude") + "\",\"" + cellDataH.get("height") + "\"");
					}
					else
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"false\",\"" + cellDataH.get("latitude") + "\",\"" + cellDataH.get("longitude") + "\",\"" + cellDataH.get("height") + "\"");
						//pw1.print("\"\",\"false\",\"\",\"\",\"\"");
					}
					
					if(cellDataH.get("nrfreq").equals("n260"))
					{
						pw1.print("\n\"@RU_INFORMATION\"\n");
						pw1.print("\"NE ID\",\"Start Frequency\",\"Uncertainty Semi Major\",\"Uncertainty Semi Minor\",\"Orientation Of Major Axis\",\"Uncertainty Altitude\",\"Confidence\"\n");
						pw1.print("\"" + cellDataH.get("neid") + "\",\"3750000\",\"8\",\"8\",\"0\",\"0\",\"100\"\n");
					}else if(cellDataH.get("nrfreq").equals("n261"))
					{
						pw1.print("\n\"@RU_INFORMATION\"\n");
						pw1.print("\"NE ID\",\"Start Frequency\",\"Uncertainty Semi Major\",\"Uncertainty Semi Minor\",\"Orientation Of Major Axis\",\"Uncertainty Altitude\",\"Confidence\"\n");
						pw1.print("\"" + cellDataH.get("neid") + "\",\"2750000\",\"8\",\"8\",\"0\",\"0\",\"100\"\n");
					}
					
					/*pw1.print("\"@IPSEC_INFORMATION\"\n");
					pw1.print(
							"\"NE ID\",\"CPU ID\",\"VR ID\",\"Interface Name1\",\"Peer IP Version\",\"First Peer IP\",\"Second Peer IP\",\"Inner IP Version\","
							+ "\"Tunnel Mode\",\"Interface Name2\",\"Interface Name3\",\"Interface Name4\",\"Interface Name5\",\"Interface Name6\",\"Crypto Algorithm\","
							+ "\"Hash Algorithm\",\"Local ID Type\",\"Local ID\"\n");

//					pw1.print("\"" + cellDataH.get("neid") +"\",\"\",\"0\",\"-\",\"ipv4\",\"\",\"\",\"ipv4\",\"client\",\"-\",\"-\",\"-\",\"-\",\"-\",\"aes-128-cbc\",\"sha1\",\"auto\",\"-\"\n");

					pw1.print("\"" + "\",\"\",\"0\",\"-\",\"ipv4\",\"\",\"\",\"ipv4\",\"client\",\"-\",\"-\",\"-\",\"-\",\"-\",\"aes-128-cbc\",\"sha1\",\"auto\",\"-\"\n");

					
					pw1.print("\"@PKI_INFORMATION\"\n");

					pw1.print(
							"\"NE ID\",\"CPU ID\",\"IP Address\",\"FQDN\",\"Port\",\"Path\",\"DN\",\"DN Domain\",\"CA DN\",\"Hash Algorithm\"\n");
//					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"\",\"\",\"80\",\"-\",\"-\",\"samsung.com\",\"-\",\"sha256\"\n");
					
					pw1.print("\"" + "\",\"0\",\"\",\"\",\"80\",\"-\",\"-\",\"samsung.com\",\"-\",\"sha256\"\n");
					
					pw1.print("\"@CSL_TCE_INFORMATION\"\n");
					pw1.print(
							"\"NE ID\",\"CSL TCE Server IP Address\",\"CSL TCE Server Port\",\"CSL TCE Option\"\n");
					pw1.print("\"\",\"\",\"50002\",\"abnormal-call-only\"\n");
					*/
				} else if(version.contains("22C"))
				{
					String autoFusing = "on";
					
					pw1.print("\"@DU\"\n");				
					pw1.print("\"NE ID\",\"NE Type\",\"NE Version\",\"Release Version\",\"Network\",\"NE Name\",\"GPL Version\",\"Administrative State\",\"gNB ID\",\"gNB ID Length\",\"gNB DU ID\",\"gNB DU Name\",\"Endpoint CU IP address\",\"Local Time Offset\",\"NE Serial Number\",\"FW Auto Fusing\",\"Maximum Supportable Coverage\",\"A6G PUCCH CSI Coverage Enhancement\",\"A6G PUCCH HARQ Coverage Enhancement\"\n");				
					pw1.print("\"" + cellDataH.get("neid") +"\",\"gnb_au\",\"" + ver + "\",\"" + relver + "\",\"" + cellDataH.get("network") + "\",\"GROW_" + cellDataH.get("auname") + "\",\"\",\"unlocked\",\"" +
							cellDataH.get("gndbid") + "\",\"22\",\"" + cellDataH.get("gndbauid") + "\",\""+ cellDataH.get("auname") + "\",\"" + cellDataH.get("remoteip") + "\",\"0\",\"\",\"" + autoFusing + "\",\"normal-coverage\",\"a6-pucch-csi-coverage-enh-disable\",\"a6-pucch-harq-coverage-enh-disable\"\n");
					pw1.print("\"@SERVER_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CFM\",\"PSM\"\n");
                                        pw1.print("\"\",\"\",\"\"\n");

					
					pw1.print("\"@MAIN_BOARD_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"Unit Type\",\"Unit ID\",\"Board Type\"\n");
					
					if(cellDataH.get("nrfreq").equals("n261"))
						
					{
						if( cellDataH.get("au_type").equals("GEN1"))
						{
							pw1.print("\"" + cellDataH.get("neid") + "\",\"fmp\",\"0\",\"g7da\"\n");
						}else {
							pw1.print("\"" + cellDataH.get("neid") + "\",\"fmp\",\"0\",\"g7dd-c01\"\n");
						}
					}else {
						if( cellDataH.get("au_type").equals("GEN1"))
						{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"fmp\",\"0\",\"g7da-c01\"\n");
						}
						else {
							pw1.print("\"" + cellDataH.get("neid") + "\",\"fmp\",\"0\",\"g7dd-c02\"\n");
						}
					}
					
					
					pw1.print("\"@CLOCK_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"Clock Source ID\",\"Clock Source\",\"Priority Level\",\"Quality Level\"\n");
					if(cellDataH.get("clock_source").equals("PTP"))
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"1\",\"ieee1588-phasetype\",\"1\",\"prc\"\n");
					}
					else
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"gps-type\",\"1\",\"prc\"\n");
					}
									
					pw1.print("\"@PTP_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"IP Version\",\"First Master IP\",\"Second Master IP\",\"Clock Profile\",\"PTP Domain\",\"PTP Hybrid Mode\"\n");
					if(cellDataH.get("clock_source").equals("PTP"))
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"\",\"\",\"\",\"itu-g8275-1\",\"24\",\"disable\"\n");
					}
					else
					{
						pw1.print("\"\",\"\",\"\",\"\",\"\",\"\",\"\"\n");
					}
					String SpeedDuplex="s10g-full";
					/*if(cellDataH.get("clock_source").equals("GPS")) {
						SpeedDuplex="s10g-full";
					}*/
					
					pw1.print("\"@PORT_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"Port ID\",\"VR ID\",\"Port Administrative State\",\"Connect Type\",\"UDE Type\",\"MTU\",\"Speed Duplex\",\"Fec Mode\"\n");
					if(cellDataH.get("nrfreq").equals("n261")) {
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"unlocked\",\"backhaul\",\"ude-none\",\"1500\",\"" + SpeedDuplex +"\",\"off\"\n");
					}
					else
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"unlocked\",\"backhaul\",\"ude-none\",\"1500\",\"" + SpeedDuplex + "\",\"off\"\n");
					}
					
					pw1.print("\"@VIRTUAL_ROUTING_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"VR ID\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\"\n");
					String oamprelen = cellDataH.get("oamprelen").toString().replace("/", "");
					String ranprelen = cellDataH.get("ranprelen").toString().replace("/", "");
					String ranroutepre1 =  cellDataH.get("ranroutepre1").toString().split("/")[0];
					String ranroutepre2 =  cellDataH.get("ranroutepre2").toString().split("/")[0];
					
					pw1.print("\"@IP_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CPU ID\",\"External Interface Name\",\"IP Address\",\"IP Prefix Length\",\"IP Get Type\",\"Management\",\"Control\",\"Bearer\",\"IEEE1588\"\n");
					if(cellDataH.get("clock_source").equals("PTP"))
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("oamid") + "\",\"" + cellDataH.get("oamip") + "\",\"" + oamprelen + "\",\"static\",\"true\",\"false\",\"false\",\"true\"\n");
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("ranid") + "\",\"" + cellDataH.get("ranip") + "\",\"" + ranprelen + "\",\"static\",\"false\",\"true\",\"true\",\"false\"\n");					
					}
					else
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("oamid") + "\",\"" + cellDataH.get("oamip") + "\",\"" + oamprelen + "\",\"static\",\"true\",\"false\",\"false\",\"false\"\n");
						pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0." + cellDataH.get("ranid") + "\",\"" + cellDataH.get("ranip") + "\",\"" + ranprelen + "\",\"static\",\"false\",\"true\",\"true\",\"false\"\n");					
					}
					
					pw1.print("\"@VLAN_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CPU ID\",\"VLAN Interface Name\",\"VLAN ID\",\"VR ID\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0\",\"" + cellDataH.get("oamid") +"\",\"0\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"xe_0_0_0\",\"" + cellDataH.get("ranid") +"\",\"0\"\n");
					
					pw1.print("\"@LAG_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CPU ID\",\"LAG ID\",\"VR ID\",\"LAG Interface Name\"\n");
					pw1.print("\"\",\"\",\"\",\"\",\"\"\n");
					
					pw1.print("\"@ROUTE_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"CPU ID\",\"VR ID\",\"IP Prefix\",\"IP Prefix Length\",\"IP Gateway\",\"Route Interface Name\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"" + ranroutepre1 + "\",\"" + ranprelen + "\",\"" + cellDataH.get("rangate") + "\",\"-\"\n" );
					pw1.print("\"" + cellDataH.get("neid") + "\",\"0\",\"0\",\"" + ranroutepre2 + "\",\"" + ranprelen + "\",\"" + cellDataH.get("rangate") + "\",\"-\"\n" );
					
					
					pw1.print("\"@SYSTEM_LOCATION_INFORMATION\"\n");
					pw1.print("\"NE ID\",\"User Defined Mode\",\"Latitude\",\"Longitude\",\"Height\"\n");
					if(cellDataH.get("clock_source").equals("PTP"))
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"true\",\"" + cellDataH.get("latitude") + "\",\"" + cellDataH.get("longitude") + "\",\"" + cellDataH.get("height") + "\"");
					}
					else
					{
						pw1.print("\"" + cellDataH.get("neid") + "\",\"false\",\"" + cellDataH.get("latitude") + "\",\"" + cellDataH.get("longitude") + "\",\"" + cellDataH.get("height") + "\"");
					}
					
					if(cellDataH.get("nrfreq").equals("n260"))
					{
						pw1.print("\n\"@RU_INFORMATION\"\n");
						pw1.print("\"NE ID\",\"Start Frequency\",\"Second Start Frequency\",\"Uncertainty Semi Major\",\"Uncertainty Semi Minor\",\"Orientation Of Major Axis\",\"Uncertainty Altitude\",\"Confidence\"\n");
						pw1.print("\"" + cellDataH.get("neid") + "\",\"3750000\",\"0\",\"8\",\"8\",\"0\",\"0\",\"100\"\n");
					}else if(cellDataH.get("nrfreq").equals("n261"))
					{
						pw1.print("\n\"@RU_INFORMATION\"\n");
						pw1.print("\"NE ID\",\"Start Frequency\",\"Second Start Frequency\",\"Uncertainty Semi Major\",\"Uncertainty Semi Minor\",\"Orientation Of Major Axis\",\"Uncertainty Altitude\",\"Confidence\"\n");
						pw1.print("\"" + cellDataH.get("neid") + "\",\"2750000\",\"0\",\"8\",\"8\",\"0\",\"0\",\"100\"\n");
					}
					pw1.print("\"@SLICE_NETWORK_DU_IP_INFORMATION\"\n");
					pw1.print(
							"\"NE ID\",\"Slice Index\",\"CPU ID\",\"Bearer IP Version\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"not-configured\",\"0\",\"ipv6\"\n");
					
					pw1.print("\"@SLICE_NETWORK_DU_BEARER_IP_INFORMATION\"\n");
					pw1.print(
							"\"NE ID\",\"Slice Index\",\"CPU ID\",\"Bearer IPV4 Address\",\"Bearer IPV6 Address\"\n");
					pw1.print("\"" + cellDataH.get("neid") + "\",\"not-configured\",\"0\",\"\",\"" + cellDataH.get("RAN_IP_Address") + "\"\n");
					
				}
			
			
			} catch(Exception e)
			{
				mydie(e.toString());
			}
			finally
			{
				if(pw1!=null)
				{
					pw1.flush();
					pw1.close();
				}					
			}
		}
		
		return "AU_"+ version +"_"+auType+"_" + gndbAuId.get(gnbidx) + "_" + timestamp + ".csv";
	}

}
