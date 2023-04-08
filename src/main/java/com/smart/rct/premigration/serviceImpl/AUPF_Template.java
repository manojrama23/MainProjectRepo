package com.smart.rct.premigration.serviceImpl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class AUPF_Template {

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

	public String generateAupfTemplate(ArrayList<String> ciqDataTemp, String ver, String outputdir, String gnodebID, String relversion)
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
			if(version.contains("20"))
			{
				auFilename = createCellTemplate_20A(cellDataA);
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
		String tags[] = {"market", "neidaupf", "network", "neidaupfname", "gnbcuname", "gndbid", "gndbauid" };
		String headers[] = {"MARKET", "NE_ID_AUPF", "NETWORK", "NE_ID_AUPF_NAME", "GNB_CU_NAME", "GNODEB_ID", "GNODEB_AUID" };
		String poss[] = {"-1", "-1", "-1", "-1", "-1", "-1", "-1" };
		
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
					
					index = find_necessaryA_idx("neidaupf");
					if(index != -1)
					{
						cellDataH.put("neidaupf", lineA[index]);
					}
					
					index = find_necessaryA_idx("network");
					if(index != -1)
					{
						cellDataH.put("network", lineA[index]);
					}
					
					index = find_necessaryA_idx("gndbauid");
					if(index != -1)
					{
						cellDataH.put("gndbauid", lineA[index]);
					}
					
					index = find_necessaryA_idx("neidaupfname");
					if(index != -1)
					{
						cellDataH.put("neidaupfname", lineA[index]);
					}
					
					index = find_necessaryA_idx("gndbid");
					if(index != -1)
					{
						cellDataH.put("gndbid", lineA[index]);
					}
					
					index = find_necessaryA_idx("gnbcuname");
					if(index != -1)
					{
						cellDataH.put("gnbcuname", lineA[index]);
					}
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

	@SuppressWarnings("unchecked")
	private String createCellTemplate_20A(ArrayList<HashMap<String, Object>> cellDataRef)
	{
		String growfile1 = growfolder + "/AUPF_" + version + "_" + cellDataRef.get(0).get("neidaupf") + ".csv";
		for(int i=0;i<cellDataRef.size();i++)
		{
			HashMap<String,Object> cellDataH = cellDataRef.get(i);
			//System.out.println(cellDataH);
			String ver = "";
			if(version.contains("20A"))
			{
				ver = "20.A.0";
			}
			else if(version.contains("20B"))
			{
				ver = "20.B.0";
			}
			
			PrintWriter pw1 =null;
			
			try
			{		
				
				File f1 =  new File(growfile1);
				f1.createNewFile();
				pw1 = new PrintWriter(new BufferedWriter(new FileWriter(f1)));
				
				pw1.print("\"@AUPF\"\n");				
				pw1.print("\"NE ID\",\"VNF ID\",\"NE Type\",\"NE Version\",\"Release Version\",\"Network\",\"NE Name\",\"local time off\"\n");				
				pw1.print("\"" + cellDataH.get("neidaupf") + "\",\"TBD\",\"gnb_cu_up\",\"" + ver +"\",\"" + relver + "\",\"" + cellDataH.get("network") + "\",\"" + cellDataH.get("neidaupfname") + "\",\"0\"\n");
				
				pw1.print("\"@SERVER_INFORMATION\"\n");
				pw1.print("\"NE ID\",\"CFM\",\"PSM\"\n");
				pw1.print("\"\",\"\",\"\"\n");
				
				pw1.print("\"@GNB_CU_UP_INFORMATION\"\n");
				pw1.print("\"NE ID\",\"gNB CU UP ID\",\"gNB CU UP Name\"\n");
				pw1.print("\"" + cellDataH.get("neidaupf") + "\",\"" + cellDataH.get("gndbid") + "\",\"" + cellDataH.get("gnbcuname")+ "\"\n");
				
				pw1.print("\"@ENDPOINT_E1_INFORMATION\"\n");
				pw1.print("\"NE ID\",\"Endpoint E1 IP address\",\"gNB ID\"\n");
				pw1.print("\"" + cellDataH.get("neidaupf") + "\",\"TBD\",\"" + cellDataH.get("gndbid") + "\"\n");
				
				pw1.print("\"@GEO_REDUNDANCY_INFORMATION\"\n");
				pw1.print("\"NE ID\",\"Data Center ID\",\"HAC Flag\",\"Primary HAC IP Address\",\"Secondary HAC IP Address\",\"HAC Reconnection Timeout\",\"HAC Registration Timeout\"\n");
				pw1.print("\"\",\"\",\"\",\"\",\"\",\"\",\"\"");
				
				
			
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
		return "AUPF_" + version + "_" + cellDataRef.get(0).get("neidaupf") + ".csv";
	}

}

