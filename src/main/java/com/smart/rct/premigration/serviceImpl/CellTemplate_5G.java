package com.smart.rct.premigration.serviceImpl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class CellTemplate_5G {
	public ArrayList<String> gndbAuId = new ArrayList<>();
	public String ciq = "";
	public int gnbidx;
	public String version;
	public String timestamp;
	public String growfolder; 
	public ArrayList<HashMap<String, String>> necessaryA;
	public ArrayList<String> ciqData = new ArrayList<String>();
	public ArrayList<String> scrubData = new ArrayList<String>();

	public ArrayList<String> generateCellTemplate(ArrayList<String> ciqDataTemp, String ver, String outputdir, String gnodebID,String auType)
	{
		ArrayList<String> filename = new ArrayList<>();
		ciqData = ciqDataTemp;
		version = ver;
		growfolder = outputdir;
		if(gnodebID.charAt(0)=='0')
		{
			gnodebID = gnodebID.substring(1);
		}
		gndbAuId.add(gnodebID);
		
		//Creating Folder structure and deboglof file
		File f1 = new File(growfolder);
		f1.mkdirs();
					
		Format f = new SimpleDateFormat("MMddyyyy_HH_mm_ss");
		timestamp = f.format(new Date());
						
		//System.out.println(gndbAuId);
		//Populating necessary data in ArrayList
		populate_Necessary_Data();
		
		//Filling ScrubData
		getScrubData();
		
		for(gnbidx=0; gnbidx < gndbAuId.size(); gnbidx++)
		{
			ArrayList<HashMap<String, Object>> cellDataA = new ArrayList<>();
			
			//To get required data for gndb
			populate_Data(cellDataA);
			
			//to print the data
			//print_cellData(cellDataA);
			
			//int id = Integer.parseInt(cellDataA.get(0).get("neid").toString().substring(cellDataA.get(0).get("neid").toString().length() - 4));
			//System.out.println(id);
			
			//Creating CellTemplate
			//System.out.println("Creating Cell_Template \n\n");
			if(version.contains("19"))
			{
				createCellTemplate_19AP3(cellDataA);
			}
			else if(version.contains("20"))
			{
				filename = createCellTemplate_20A(cellDataA,auType);
			}
			else if(version.contains("21")) {
				filename = createCellTemplate_21A(cellDataA,auType);
			}
			else if(version.contains("22")) {
				filename = createCellTemplate_22(cellDataA,auType);
			}
		}
		return filename;
	}
	
	private void mydie(String msg)
	{
		System.out.println(msg);
		
	}
	
	private void populate_Necessary_Data()
	{
		necessaryA = new ArrayList<>();
		String tags[] = {"market", "neid", "nrpci", "gndbid", "nrfreq", "prachrsi", "aumimoconf", "cc0num", "cc1num", "cc2num", "cc3num", "cc4num", "cc5num", "cc6num", "cc7num", 
				"cc0arfcn", "cc1arfcn", "cc2arfcn", "cc3arfcn", "cc4arfcn", "cc5arfcn", "cc6arfcn", "cc7arfcn", "cc0bw", "cc1bw", "cc2bw", "cc3bw", "cc4bw", "cc5bw", "cc6bw",
				"cc7bw", "sitetype", "beambookType"};
		String headers[] = {"MARKET", "NE_ID_AU", "NR_PCI", "GNODEB_AUID", "NR_FREQ_BAND", "PRACH_ROOT_SEQUENCE_INDEX", "AUMIMOCONFIGURATION", "CC0_CELL_NUM", "CC1_CELL_NUM",
				"CC2_CELL_NUM", "CC3_CELL_NUM", "CC4_CELL_NUM", "CC5_CELL_NUM", "CC6_CELL_NUM", "CC7_CELL_NUM", "CC0_NR_ARFCN", "CC1_NR_ARFCN",
				"CC2_NR_ARFCN", "CC3_NR_ARFCN", "CC4_NR_ARFCN", "CC5_NR_ARFCN", "CC6_NR_ARFCN", "CC7_NR_ARFCN", "CC0_NR_BANDWIDTH", "CC1_NR_BANDWIDTH",
				"CC2_NR_BANDWIDTH", "CC3_NR_BANDWIDTH", "CC4_NR_BANDWIDTH", "CC5_NR_BANDWIDTH", "CC6_NR_BANDWIDTH", "CC7_NR_BANDWIDTH", "SITETYPE", "IAU_MOUNT_TYPE" };
		String poss[] = {"-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", 
				"-1", "-1", "-1", "-1", "-1", "-1", "-1" , "-1"};
		
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
						if(necessaryA.get(i).get("tag").equals("gndbid"))
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
				if(gndbAuId.get(gnbidx).equals(lineA[find_necessaryA_idx("gndbid")]))
				{
					HashMap<String,Object> cellDataH = new HashMap<>();
					ArrayList<String> ccnum = new ArrayList<>();
					ArrayList<String> ccarfcn = new ArrayList<>();
					ArrayList<String> ccbw = new ArrayList<>();
					
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
					
					index = find_necessaryA_idx("nrpci");
					if(index != -1)
					{
						cellDataH.put("nrpci", lineA[index]);
					}
					
					index = find_necessaryA_idx("gndbid");
					if(index != -1)
					{
						cellDataH.put("gndbid", lineA[index]);
					}
					
					index = find_necessaryA_idx("nrfreq");
					if(index != -1)
					{
						cellDataH.put("nrfreq", lineA[index]);
					}
					
					index = find_necessaryA_idx("prachrsi");
					if(index != -1)
					{
						cellDataH.put("prachrsi", lineA[index]);
					}
					
					index = find_necessaryA_idx("aumimoconf");
					if(index != -1)
					{
						cellDataH.put("aumimoconf", lineA[index]);
					}
					
					index = find_necessaryA_idx("sitetype");
					if(index != -1)
					{
						cellDataH.put("sitetype", lineA[index]);
					}
					
					index = find_necessaryA_idx("beambookType");
					if(index != -1)
					{
						cellDataH.put("beambookType", lineA[index]);
					}
					
					for(int i=0;i<8;i++)
					{
						index = find_necessaryA_idx("cc"+i+"num");
						ccnum.add(lineA[index]);
					}
					
					for(int i=0;i<8;i++)
					{
						index = find_necessaryA_idx("cc"+i+"arfcn");
						ccarfcn.add(lineA[index]);
					}
					
					for(int i=0;i<8;i++)
					{
						index = find_necessaryA_idx("cc"+i+"bw");
						ccbw.add(lineA[index]);
					}
					
					cellDataH.put("ccnum",ccnum);
					cellDataH.put("ccarfcn",ccarfcn);
					cellDataH.put("ccbw", ccbw);
					
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
	private  void print_cellData(ArrayList<HashMap<String, Object>> cellDataRef)
	{
		System.out.println("Input for cell Template for gnodeb" + gndbAuId.get(gnbidx));
		for(int i=0;i<cellDataRef.size();i++)
		{
			HashMap<String,Object> cellDataH = cellDataRef.get(i);
			System.out.println("MARKET : " + cellDataH.get("market")+"\n");
			System.out.println("NE_ID_AU : " + cellDataH.get("neid")+"\n");
			System.out.println("NR_PCI : " + cellDataH.get("nrpci")+"\n");
			System.out.println("GNODEB_AUID : " + cellDataH.get("gndbid")+"\n");
			System.out.println("NR_FREQ_BAND : " + cellDataH.get("nrfreq")+"\n");
			System.out.println("PRACH_ROOT_SEQUENCE_INDEX : " + cellDataH.get("prachrsi")+"\n");
			System.out.println("AUMIMOCONFIGURATION : " + cellDataH.get("aumimoconf")+"\n");
			System.out.println("CC_NUM : " + (ArrayList<String>)cellDataH.get("ccnum")+"\n");
			System.out.println("CC_ARFCN : " + (ArrayList<String>)cellDataH.get("ccarfcn")+"\n");
			System.out.println("CC_BANDWIDTH : " + (ArrayList<String>)cellDataH.get("ccbw")+"\n");
		}
	}

	@SuppressWarnings("unchecked")
	private void createCellTemplate_19AP3(ArrayList<HashMap<String, Object>> cellDataRef)
	{
		for(int i=0;i<cellDataRef.size();i++)
		{
			HashMap<String,Object> cellDataH = cellDataRef.get(i);
			//System.out.println(cellDataH);
			
				String growfile1 = growfolder + "/AU_CaCell_19AP3_" + gndbAuId.get(gnbidx) + "_" + timestamp + ".csv";
				
				PrintWriter pw1 =null;
				
				try
				{		
					
					File f1 =  new File(growfile1);
					f1.createNewFile();
					pw1 = new PrintWriter(new BufferedWriter(new FileWriter(f1)));
					
					pw1.print("\"@CELL_INFORMATION\"\n");
					
					pw1.print("\"State\",\"Sector ID\",\"Carrier ID\",\"Cell Num\",\"NR Physical Cell ID\",\"NR Arfcn\",\"NR Bandwidth\",\"NR Frequency Band\",\"DL Antenna Count\",\"UL Antenna Count\",\"Number of Rx Paths per RU\",\"PRACH RSI\",\"PRACH ZCZC\",\"Tracking Area Code Usage\",\"Tracking Area Code\"\n");
					String neid = new Integer(Integer.parseInt(cellDataH.get("neid").toString().substring(cellDataH.get("neid").toString().length() - 4))).toString();
					String nrfreq = cellDataH.get("nrfreq").toString().replaceAll("[^0-9]", "");
					String[] antennainfo = cellDataH.get("aumimoconf").toString().split("\\D");
					
					for(int k=0;k<8;k++)
					{	
						if(((ArrayList<String>)cellDataH.get("ccarfcn")).get(k).equals("TBD"))
							break;
						pw1.print("\"ADD\",\"" + neid + "\",\"" + ((ArrayList<String>)cellDataH.get("ccnum")).get(k) + "\",\"" + ((ArrayList<String>)cellDataH.get("ccnum")).get(k) + "\",\"" +
								cellDataH.get("nrpci") + "\",\"" + ((ArrayList<String>)cellDataH.get("ccarfcn")).get(k) + "\",\"nr-bandwidth-" + ((ArrayList<String>)cellDataH.get("ccbw")).get(k) + "\",\"" +
								nrfreq + "\",\"dl-antenna-count-" + antennainfo[0] + "tx\",\"ul-antenna-count-" + antennainfo[1] + "rx\",\""+ antennainfo[1] + "\",\"" + cellDataH.get("prachrsi") +"\"," +
								"\"0\",\"not-use\",\"1\"\n");
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
	}

	@SuppressWarnings("unchecked")
	private ArrayList<String> createCellTemplate_20A(ArrayList<HashMap<String, Object>> cellDataRef,String auType)
	{
		ArrayList<String> filename = new ArrayList<>();
		for(int i=0;i<cellDataRef.size();i++)
		{
			HashMap<String,Object> cellDataH = cellDataRef.get(i);
			//System.out.println(cellDataH);
			String growfile1 = growfolder + "/AU_CaCell_" + version + "_" +auType+"_"+ gndbAuId.get(gnbidx) + "_" + timestamp + ".csv";
			filename.add("AU_CaCell_" +version + "_" +auType+"_"  + gndbAuId.get(gnbidx) + "_" + timestamp + ".csv");
			
			PrintWriter pw1 =null;
			String siteType = cellDataH.get("sitetype").toString();
			
			try
			{		
				
				File f1 =  new File(growfile1);
				f1.createNewFile();
				pw1 = new PrintWriter(new BufferedWriter(new FileWriter(f1)));
				
				if(siteType.equalsIgnoreCase("IB") && (auType.isEmpty() || auType.equalsIgnoreCase("TBD"))){
					pw1.print("\"@CELL_INFORMATION\"\n");
					
					pw1.print("\"State\",\"Sector ID\",\"Carrier ID\",\"Cell Num\",\"NR Frequency Band\",\"NR DL Arfcn\",\"NR UL Arfcn\",\"NR DL Bandwidth\",\"NR UL Bandwidth\",\"NR Physical Cell ID\",\"DL Antenna Count\",\"UL Antenna Count\",\"Number of Rx Paths per RU\",\"PRACH RSI\",\"PRACH ZCZC\",\"PRACH Configuration Index\",\"PRACH SSB Per RO\",\"Tracking Area Code Usage\",\"Tracking Area Code\",\"Beambook Type\"\n");
					String neid = "";
					if(cellDataH.get("neid").toString().length() > 4) {
						neid = new Integer(Integer.parseInt(cellDataH.get("neid").toString().substring(cellDataH.get("neid").toString().length() - 4))).toString();
					} else {
						neid = cellDataH.get("neid").toString();
					}
					String nrfreq = cellDataH.get("nrfreq").toString().replaceAll("[^0-9]", "");
					String[] antennainfo = cellDataH.get("aumimoconf").toString().split("\\D");
					String beamBookType = "ceiling-type";
					if(!cellDataH.get("beambookType").toString().equalsIgnoreCase("TBD")) {
						beamBookType = cellDataH.get("beambookType").toString();
					}
					for(int k=0;k<8;k++)
					{	
						if(((ArrayList<String>)cellDataH.get("ccarfcn")).get(k).equals("TBD"))
							break;
						pw1.print("\"ADD\",\""+ neid +"\",\""+ ((ArrayList<String>)cellDataH.get("ccnum")).get(k) +"\",\""
							+ ((ArrayList<String>)cellDataH.get("ccnum")).get(k) +"\",\""+nrfreq +"\",\""+ ((ArrayList<String>)cellDataH.get("ccarfcn")).get(k) 
							+"\",\""+ ((ArrayList<String>)cellDataH.get("ccarfcn")).get(k) +"\",\"nr-bandwidth-100\",\"nr-bandwidth-100\",\""
							+ cellDataH.get("nrpci") +"\",\"dl-antenna-count-" + antennainfo[0] + "tx\",\"ul-antenna-count-" + antennainfo[1] 
							+ "rx\",\""+ antennainfo[1] +"\",\"\",\"12\",\"189\",\"ssb-per-ro-one-choice\",\"not-use\",\"4096\",\""	+ beamBookType + "\"\n");
						
						

					}
						
					pw1.print("\"@SON_INFORMATION\"\n");
					pw1.print("\"Initial PCI\",\"Initial RSI\",\"Initial NCRT\"\n");
					pw1.print("\"off\",\"off\",\"off\"");
				}
				else if(version.contains("20C"))
					
				{
					pw1.print("\"@CELL_INFORMATION\"\n");
					
					pw1.print("\"State\",\"Sector ID\",\"Carrier ID\",\"Cell Num\",\"NR Frequency Band\",\"NR DL Arfcn\",\"NR UL Arfcn\",\"NR DL Bandwidth\",\"NR UL Bandwidth\",\"NR Physical Cell ID\",\"DL Antenna Count\",\"UL Antenna Count\",\"Number of Rx Paths per RU\",\"PRACH RSI\",\"PRACH ZCZC\",\"PRACH Configuration Index\",\"PRACH SSB Per RO\",\"Number of Tx SSB\",\"Tracking Area Code Usage\",\"Tracking Area Code\",\"Beambook Type\"\n");
					
					String neid = "";
					if(cellDataH.get("neid").toString().length() > 4) {
						neid = new Integer(Integer.parseInt(cellDataH.get("neid").toString().substring(cellDataH.get("neid").toString().length() - 4))).toString();
					} else {
						neid = cellDataH.get("neid").toString();
					}String nrfreq = cellDataH.get("nrfreq").toString().replaceAll("[^0-9]", "");
					String[] antennainfo = cellDataH.get("aumimoconf").toString().split("\\D");
						
					for(int k=0;k<8;k++)
					{	
						if(((ArrayList<String>)cellDataH.get("ccarfcn")).get(k).equals("TBD"))
							break;
						pw1.print("\"ADD\",\""+ neid +"\",\""+ ((ArrayList<String>)cellDataH.get("ccnum")).get(k) +"\",\""+ ((ArrayList<String>)cellDataH.get("ccnum")).get(k) +"\",\""+nrfreq +"\",\""+ ((ArrayList<String>)cellDataH.get("ccarfcn")).get(k) +"\",\""+ ((ArrayList<String>)cellDataH.get("ccarfcn")).get(k) +"\",\"nr-bandwidth-100\",\"nr-bandwidth-100\",\""+ cellDataH.get("nrpci") +"\",\"dl-antenna-count-" + antennainfo[0] + "tx\",\"ul-antenna-count-" + antennainfo[1] + "rx\",\""+ antennainfo[1] +"\",\""+ cellDataH.get("prachrsi") +"\",\"0\",\"194\",\"ssb-per-ro-two-choice\",\"56\",\"not-use\",\"1\",\"v40h120\"\n");
						
						

					}
						
					pw1.print("\"@SON_INFORMATION\"\n");
					pw1.print("\"Initial PCI\",\"Initial RSI\",\"Initial NCRT\"\n");
					pw1.print("\"off\",\"off\",\"off\"");
				}else {
					pw1.print("\"@CELL_INFORMATION\"\n");
					
					pw1.print("\"State\",\"Sector ID\",\"Carrier ID\",\"Cell Num\",\"NR Physical Cell ID\",\"NR DL Arfcn\",\"NR UL Arfcn\",\"NR Bandwidth\",\"NR Frequency Band\",\"DL Antenna Count\",\"UL Antenna Count\",\"Number of Rx Paths per RU\",\"PRACH RSI\",\"PRACH ZCZC\",\"Tracking Area Code Usage\",\"Tracking Area Code\",\"Beambook Type\"\n");
					
					String neid = "";
					if(cellDataH.get("neid").toString().length() > 4) {
						neid = new Integer(Integer.parseInt(cellDataH.get("neid").toString().substring(cellDataH.get("neid").toString().length() - 4))).toString();
					} else {
						neid = cellDataH.get("neid").toString();
					}
					String nrfreq = cellDataH.get("nrfreq").toString().replaceAll("[^0-9]", "");
					String[] antennainfo = cellDataH.get("aumimoconf").toString().split("\\D");
						
					for(int k=0;k<8;k++)
					{	
						if(((ArrayList<String>)cellDataH.get("ccarfcn")).get(k).equals("TBD"))
							break;
						pw1.print("\"ADD\",\"" + neid + "\",\"" + ((ArrayList<String>)cellDataH.get("ccnum")).get(k) + "\",\"" + ((ArrayList<String>)cellDataH.get("ccnum")).get(k) + "\",\"" +
								cellDataH.get("nrpci") + "\",\"" + ((ArrayList<String>)cellDataH.get("ccarfcn")).get(k) + "\",\"" + ((ArrayList<String>)cellDataH.get("ccarfcn")).get(k) + "\",\"nr-bandwidth-" + ((ArrayList<String>)cellDataH.get("ccbw")).get(k) + "\",\"" +
								nrfreq + "\",\"dl-antenna-count-" + antennainfo[0] + "tx\",\"ul-antenna-count-" + antennainfo[1] + "rx\",\""+ antennainfo[1] + "\",\"" + cellDataH.get("prachrsi") +"\"," +
								"\"0\",\"not-use\",\"1\",\"v40h120\"\n");
						
						

					}
					
						
						
					pw1.print("\"@SON_INFORMATION\"\n");
					pw1.print("\"Initial PCI\",\"Initial RSI\",\"Initial NCRT\"\n");
					pw1.print("\"off\",\"off\",\"off\"");
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
		return filename;
	}
	
	
	@SuppressWarnings("unchecked")
	private ArrayList<String> createCellTemplate_21A(ArrayList<HashMap<String, Object>> cellDataRef,String auType)
	{
		ArrayList<String> filename = new ArrayList<>();
		for(int i=0;i<cellDataRef.size();i++)
		{
			HashMap<String,Object> cellDataH = cellDataRef.get(i);
			//System.out.println(cellDataH);
			String growfile1 = growfolder + "/AU_CaCell_" + version + "_" +auType+"_"+ gndbAuId.get(gnbidx) + "_" + timestamp + ".csv";
			filename.add("AU_CaCell_" +version + "_" +auType+"_"  + gndbAuId.get(gnbidx) + "_" + timestamp + ".csv");
			String siteType = cellDataH.get("sitetype").toString();
			PrintWriter pw1 =null;
			
			try
			{		
				
				File f1 =  new File(growfile1);
				f1.createNewFile();
				pw1 = new PrintWriter(new BufferedWriter(new FileWriter(f1)));
				
				if(siteType.equalsIgnoreCase("IB") && (auType.isEmpty() || auType.equalsIgnoreCase("TBD"))){
					pw1.print("\"@CELL_INFORMATION\"\n");
					if(version.contains("21D")) {
						pw1.print("\"State\",\"Sector ID\",\"Carrier ID\",\"Cell Num\",\"NR Frequency Band\",\"NR DL Arfcn\",\"NR UL Arfcn\",\"NR DL Bandwidth\",\"NR UL Bandwidth\",\"NR Physical Cell ID\",\"DL Antenna Count\",\"UL Antenna Count\",\"Number of Rx Paths per RU\",\"PRACH RSI\",\"PRACH ZCZC\",\"PRACH Configuration Index\",\"PRACH SSB Per RO\",\"PRACH RB Offset Auto Configuration\",\"Number of Tx SSB\",\"Tracking Area Code Usage\",\"Tracking Area Code\",\"Beambook Type\"\n");
					}else {
					pw1.print("\"State\",\"Sector ID\",\"Carrier ID\",\"Cell Num\",\"NR Frequency Band\",\"NR DL Arfcn\",\"NR UL Arfcn\",\"NR DL Bandwidth\",\"NR UL Bandwidth\",\"NR Physical Cell ID\",\"DL Antenna Count\",\"UL Antenna Count\",\"Number of Rx Paths per RU\",\"PRACH RSI\",\"PRACH ZCZC\",\"PRACH Configuration Index\",\"PRACH SSB Per RO\",\"Tracking Area Code Usage\",\"Tracking Area Code\",\"Beambook Type\"\n");
					}
					String neid = "";
					if(cellDataH.get("neid").toString().length() > 4) {
						neid = new Integer(Integer.parseInt(cellDataH.get("neid").toString().substring(cellDataH.get("neid").toString().length() - 4))).toString();
					} else {
						neid = cellDataH.get("neid").toString();
					}
					String nrfreq = cellDataH.get("nrfreq").toString().replaceAll("[^0-9]", "");
					String[] antennainfo = cellDataH.get("aumimoconf").toString().split("\\D");
					String beamBookType = "ceiling-type";
					if(!cellDataH.get("beambookType").toString().equalsIgnoreCase("TBD")) {
						beamBookType = cellDataH.get("beambookType").toString();
					}	
					for(int k=0;k<8;k++)
					{	
						if(((ArrayList<String>)cellDataH.get("ccarfcn")).get(k).equals("TBD"))
							break;
						if(version.contains("21D")) {
							String TxSSB="";
							if(cellDataH.get("nrfreq").equals("n261")) {
								TxSSB= "16";
							}
							else if (cellDataH.get("nrfreq").equals("n260")) {
								TxSSB="32";
							}
						pw1.print("\"ADD\",\""+ neid +"\",\""+ ((ArrayList<String>)cellDataH.get("ccnum")).get(k) +"\",\""+ ((ArrayList<String>)cellDataH.get("ccnum")).get(k) +"\",\""+nrfreq +"\",\""+ ((ArrayList<String>)cellDataH.get("ccarfcn")).get(k) +"\",\""+ ((ArrayList<String>)cellDataH.get("ccarfcn")).get(k) +"\",\"nr-bandwidth-100\",\"nr-bandwidth-100\",\""+ cellDataH.get("nrpci") +"\",\"dl-antenna-count-" + antennainfo[0] + "tx\",\"ul-antenna-count-" + antennainfo[1] + "rx\",\""+ antennainfo[1] +"\",\"prach-root-sequence-index-auto\",\"10\",\"189\",\"ssb-per-ro-one-choice\",\"auto-prach-rb-offset-off\",\"" + TxSSB +"\",\"not-use\",\"1\",\"" + beamBookType + "\"\n");
						}else {
						pw1.print("\"ADD\",\""+ neid +"\",\""+ ((ArrayList<String>)cellDataH.get("ccnum")).get(k) +"\",\""+ ((ArrayList<String>)cellDataH.get("ccnum")).get(k) +"\",\""+nrfreq +"\",\""+ ((ArrayList<String>)cellDataH.get("ccarfcn")).get(k) +"\",\""+ ((ArrayList<String>)cellDataH.get("ccarfcn")).get(k) +"\",\"nr-bandwidth-100\",\"nr-bandwidth-100\",\""+ cellDataH.get("nrpci") +"\",\"dl-antenna-count-" + antennainfo[0] + "tx\",\"ul-antenna-count-" + antennainfo[1] + "rx\",\""+ antennainfo[1] +"\",\"\",\"12\",\"189\",\"ssb-per-ro-one-choice\",\"not-use\",\"4096\",\"" + beamBookType + "\"\n");
						}
						

					}
					if(version.contains("21D")) {
						pw1.print("\"@SON_INFORMATION\"\n");
						pw1.print("\"Initial PCI\",\"Initial RSI\",\"Initial NCRT\",\"Initial LTE NCRT\"\n");
						pw1.print("\"off\",\"off\",\"off\",\"off\"");
						
					}else {
						pw1.print("\"@SON_INFORMATION\"\n");
						pw1.print("\"Initial PCI\",\"Initial RSI\",\"Initial NCRT\"\n");
						pw1.print("\"off\",\"off\",\"off\"");
					}
				}else if(version.contains("21A") || version.contains("21B") )
					
				{
					pw1.print("\"@CELL_INFORMATION\"\n");
					
					pw1.print("\"State\",\"Sector ID\",\"Carrier ID\",\"Cell Num\",\"NR Frequency Band\",\"NR DL Arfcn\",\"NR UL Arfcn\",\"NR DL Bandwidth\",\"NR UL Bandwidth\",\"NR Physical Cell ID\",\"DL Antenna Count\",\"UL Antenna Count\",\"Number of Rx Paths per RU\",\"PRACH RSI\",\"PRACH ZCZC\",\"PRACH Configuration Index\",\"PRACH SSB Per RO\",\"Number of Tx SSB\",\"Tracking Area Code Usage\",\"Tracking Area Code\",\"Beambook Type\"\n");
					
					String neid = "";
					if(cellDataH.get("neid").toString().length() > 4) {
						neid = new Integer(Integer.parseInt(cellDataH.get("neid").toString().substring(cellDataH.get("neid").toString().length() - 4))).toString();
					} else {
						neid = cellDataH.get("neid").toString();
					}
					String nrfreq = cellDataH.get("nrfreq").toString().replaceAll("[^0-9]", "");
					String[] antennainfo = cellDataH.get("aumimoconf").toString().split("\\D");
						
					for(int k=0;k<8;k++)
					{	
						if(((ArrayList<String>)cellDataH.get("ccarfcn")).get(k).equals("TBD"))
							break;
						//pw1.print("\"ADD\",\""+ neid +"\",\""+ ((ArrayList<String>)cellDataH.get("ccnum")).get(k) +"\",\""+ ((ArrayList<String>)cellDataH.get("ccnum")).get(k) +"\",\""+nrfreq +"\",\""+ ((ArrayList<String>)cellDataH.get("ccarfcn")).get(k) +"\",\""+ ((ArrayList<String>)cellDataH.get("ccarfcn")).get(k) +"\",\"nr-bandwidth-100\",\"nr-bandwidth-100\",\""+ cellDataH.get("nrpci") +"\",\"dl-antenna-count-" + antennainfo[0] + "tx\",\"ul-antenna-count-" + antennainfo[1] + "rx\",\""+ antennainfo[1] +"\",\""+ cellDataH.get("prachrsi") +"\",\"0\",\"194\",\"ssb-per-ro-two-choice\",\"56\",\"not-use\",\"1\",\"v40h120\"\n");
						pw1.print("\"ADD\",\""+ neid +"\",\""+ ((ArrayList<String>)cellDataH.get("ccnum")).get(k) +"\",\""+ ((ArrayList<String>)cellDataH.get("ccnum")).get(k) +"\",\""+nrfreq +"\",\""+ ((ArrayList<String>)cellDataH.get("ccarfcn")).get(k) +"\",\""+ ((ArrayList<String>)cellDataH.get("ccarfcn")).get(k) +"\",\"nr-bandwidth-100\",\"nr-bandwidth-100\",\""+ cellDataH.get("nrpci") +"\",\"dl-antenna-count-" + antennainfo[0] + "tx\",\"ul-antenna-count-" + antennainfo[1] + "rx\",\""+ antennainfo[1] +"\",\"\",\"0\",\"194\",\"ssb-per-ro-two-choice\",\"56\",\"not-use\",\"1\",\"v40h120\"\n");

					}
						
					pw1.print("\"@SON_INFORMATION\"\n");
					pw1.print("\"Initial PCI\",\"Initial RSI\",\"Initial NCRT\"\n");
					pw1.print("\"off\",\"off\",\"off\"");
				}else if(version.contains("21C"))
					
				{
					pw1.print("\"@CELL_INFORMATION\"\n");
					
					pw1.print("\"State\",\"Sector ID\",\"Carrier ID\",\"Cell Num\",\"NR Frequency Band\",\"NR DL Arfcn\",\"NR UL Arfcn\",\"NR DL Bandwidth\",\"NR UL Bandwidth\",\"NR Physical Cell ID\",\"DL Antenna Count\",\"UL Antenna Count\",\"Number of Rx Paths per RU\",\"PRACH RSI\",\"PRACH ZCZC\",\"PRACH Configuration Index\",\"PRACH SSB Per RO\",\"Number of Tx SSB\",\"Tracking Area Code Usage\",\"Tracking Area Code\",\"Beambook Type\"\n");
					
					String neid = "";
					if(cellDataH.get("neid").toString().length() > 4) {
						neid = new Integer(Integer.parseInt(cellDataH.get("neid").toString().substring(cellDataH.get("neid").toString().length() - 4))).toString();
					} else {
						neid = cellDataH.get("neid").toString();
					}
					String nrfreq = cellDataH.get("nrfreq").toString().replaceAll("[^0-9]", "");
					String[] antennainfo = cellDataH.get("aumimoconf").toString().split("\\D");
						
					for(int k=0;k<8;k++)
					{	
						if(((ArrayList<String>)cellDataH.get("ccarfcn")).get(k).equals("TBD"))
							break;
						//pw1.print("\"ADD\",\""+ neid +"\",\""+ ((ArrayList<String>)cellDataH.get("ccnum")).get(k) +"\",\""+ ((ArrayList<String>)cellDataH.get("ccnum")).get(k) +"\",\""+nrfreq +"\",\""+ ((ArrayList<String>)cellDataH.get("ccarfcn")).get(k) +"\",\""+ ((ArrayList<String>)cellDataH.get("ccarfcn")).get(k) +"\",\"nr-bandwidth-100\",\"nr-bandwidth-100\",\""+ cellDataH.get("nrpci") +"\",\"dl-antenna-count-" + antennainfo[0] + "tx\",\"ul-antenna-count-" + antennainfo[1] + "rx\",\""+ antennainfo[1] +"\",\""+ cellDataH.get("prachrsi") +"\",\"0\",\"194\",\"ssb-per-ro-two-choice\",\"56\",\"not-use\",\"1\",\"v40h120\"\n");
						pw1.print("\"ADD\",\""+ neid +"\",\""+ ((ArrayList<String>)cellDataH.get("ccnum")).get(k) +"\",\""+ ((ArrayList<String>)cellDataH.get("ccnum")).get(k) +"\",\""+nrfreq +"\",\""+ ((ArrayList<String>)cellDataH.get("ccarfcn")).get(k) +"\",\""+ ((ArrayList<String>)cellDataH.get("ccarfcn")).get(k) +"\",\"nr-bandwidth-100\",\"nr-bandwidth-100\",\""+ cellDataH.get("nrpci") +"\",\"dl-antenna-count-" + antennainfo[0] + "tx\",\"ul-antenna-count-" + antennainfo[1] + "rx\",\""+ antennainfo[1] +"\",\"\",\"0\",\"194\",\"ssb-per-ro-two-choice\",\"56\",\"not-use\",\"1\",\"v40h120\"\n");

					}
						
					pw1.print("\"@SON_INFORMATION\"\n");
					pw1.print("\"Initial PCI\",\"Initial RSI\",\"Initial NCRT\",\"Initial LTE NCRT\"\n");
					pw1.print("\"off\",\"off\",\"off\",\"off\"");
				}
				else if(version.contains("21D"))
					
				{
					pw1.print("\"@CELL_INFORMATION\"\n");
					
					pw1.print("\"State\",\"Sector ID\",\"Carrier ID\",\"Cell Num\",\"NR Frequency Band\",\"NR DL Arfcn\",\"NR UL Arfcn\",\"NR DL Bandwidth\",\"NR UL Bandwidth\",\"NR Physical Cell ID\",\"DL Antenna Count\",\"UL Antenna Count\",\"Number of Rx Paths per RU\",\"PRACH RSI\",\"PRACH ZCZC\",\"PRACH Configuration Index\",\"PRACH SSB Per RO\",\"PRACH RB Offset Auto Configuration\",\"Number of Tx SSB\",\"Tracking Area Code Usage\",\"Tracking Area Code\",\"Beambook Type\"\n");
					
					String neid = "";
					if(cellDataH.get("neid").toString().length() > 4) {
						neid = new Integer(Integer.parseInt(cellDataH.get("neid").toString().substring(cellDataH.get("neid").toString().length() - 4))).toString();
					} else {
						neid = cellDataH.get("neid").toString();
					}
					String nrfreq = cellDataH.get("nrfreq").toString().replaceAll("[^0-9]", "");
					String[] antennainfo = cellDataH.get("aumimoconf").toString().split("\\D");
						
					for(int k=0;k<8;k++)
					{	
						if(((ArrayList<String>)cellDataH.get("ccarfcn")).get(k).equals("TBD"))
							break;
						//pw1.print("\"ADD\",\""+ neid +"\",\""+ ((ArrayList<String>)cellDataH.get("ccnum")).get(k) +"\",\""+ ((ArrayList<String>)cellDataH.get("ccnum")).get(k) +"\",\""+nrfreq +"\",\""+ ((ArrayList<String>)cellDataH.get("ccarfcn")).get(k) +"\",\""+ ((ArrayList<String>)cellDataH.get("ccarfcn")).get(k) +"\",\"nr-bandwidth-100\",\"nr-bandwidth-100\",\""+ cellDataH.get("nrpci") +"\",\"dl-antenna-count-" + antennainfo[0] + "tx\",\"ul-antenna-count-" + antennainfo[1] + "rx\",\""+ antennainfo[1] +"\",\""+ cellDataH.get("prachrsi") +"\",\"0\",\"194\",\"ssb-per-ro-two-choice\",\"56\",\"not-use\",\"1\",\"v40h120\"\n");
						pw1.print("\"ADD\",\""+ neid +"\",\""+ ((ArrayList<String>)cellDataH.get("ccnum")).get(k) +"\",\""+ ((ArrayList<String>)cellDataH.get("ccnum")).get(k) +"\",\""+nrfreq +"\",\""+ ((ArrayList<String>)cellDataH.get("ccarfcn")).get(k) +"\",\""+ ((ArrayList<String>)cellDataH.get("ccarfcn")).get(k) +"\",\"nr-bandwidth-100\",\"nr-bandwidth-100\",\""+ cellDataH.get("nrpci") +"\",\"dl-antenna-count-" + antennainfo[0] + "tx\",\"ul-antenna-count-" + antennainfo[1] + "rx\",\""+ antennainfo[1] +"\",\"\",\"0\",\"194\",\"ssb-per-ro-two-choice\",\"auto-prach-rb-offset-on-multiple-regions\",\"56\",\"not-use\",\"1\",\"v40h120\"\n");

					}
						
					pw1.print("\"@SON_INFORMATION\"\n");
					pw1.print("\"Initial PCI\",\"Initial RSI\",\"Initial NCRT\",\"Initial LTE NCRT\"\n");
					pw1.print("\"off\",\"off\",\"off\",\"off\"");
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
		return filename;
	}
	
	//22 support
	@SuppressWarnings("unchecked")
	private ArrayList<String> createCellTemplate_22(ArrayList<HashMap<String, Object>> cellDataRef,String auType)
	{
		ArrayList<String> filename = new ArrayList<>();
		for(int i=0;i<cellDataRef.size();i++)
		{
			HashMap<String,Object> cellDataH = cellDataRef.get(i);
			//System.out.println(cellDataH);
			String growfile1 = growfolder + "/AU_CaCell_" + version + "_" +auType+"_"+ gndbAuId.get(gnbidx) + "_" + timestamp + ".csv";
			filename.add("AU_CaCell_" +version + "_" +auType+"_"  + gndbAuId.get(gnbidx) + "_" + timestamp + ".csv");
			String siteType = cellDataH.get("sitetype").toString();
			PrintWriter pw1 =null;
			
			try
			{		
				
				File f1 =  new File(growfile1);
				f1.createNewFile();
				pw1 = new PrintWriter(new BufferedWriter(new FileWriter(f1)));
				
				if(siteType.equalsIgnoreCase("IB") && (auType.isEmpty() || auType.equalsIgnoreCase("TBD"))){
					pw1.print("\"@CELL_INFORMATION\"\n");
//					if(version.contains("21D")) {
						pw1.print("\"State\",\"Sector ID\",\"Carrier ID\",\"Cell Num\",\"NR Frequency Band\",\"NR DL Arfcn\",\"NR UL Arfcn\",\"NR DL Bandwidth\",\"NR UL Bandwidth\",\"NR Physical Cell ID\",\"DL Antenna Count\",\"UL Antenna Count\",\"Number of Rx Paths per RU\",\"PRACH RSI\",\"PRACH ZCZC\",\"PRACH Configuration Index\",\"PRACH SSB Per RO\",\"PRACH RB Offset Auto Configuration\",\"Number of Tx SSB\",\"Tracking Area Code Usage\",\"Tracking Area Code\",\"Beambook Type\",\"Cell Coverage\"\n");
//					}else {
//					pw1.print("\"State\",\"Sector ID\",\"Carrier ID\",\"Cell Num\",\"NR Frequency Band\",\"NR DL Arfcn\",\"NR UL Arfcn\",\"NR DL Bandwidth\",\"NR UL Bandwidth\",\"NR Physical Cell ID\",\"DL Antenna Count\",\"UL Antenna Count\",\"Number of Rx Paths per RU\",\"PRACH RSI\",\"PRACH ZCZC\",\"PRACH Configuration Index\",\"PRACH SSB Per RO\",\"Tracking Area Code Usage\",\"Tracking Area Code\",\"Beambook Type\"\n");
//					}
					String neid = "";
					if(cellDataH.get("neid").toString().length() > 4) {
						neid = new Integer(Integer.parseInt(cellDataH.get("neid").toString().substring(cellDataH.get("neid").toString().length() - 4))).toString();
					} else {
						neid = cellDataH.get("neid").toString();
					}
					String nrfreq = cellDataH.get("nrfreq").toString().replaceAll("[^0-9]", "");
					String[] antennainfo = cellDataH.get("aumimoconf").toString().split("\\D");
					String beamBookType = "ceiling-type";
					if(!cellDataH.get("beambookType").toString().equalsIgnoreCase("TBD")) {
						beamBookType = cellDataH.get("beambookType").toString();
					}	
					for(int k=0;k<8;k++)
					{	
						if(((ArrayList<String>)cellDataH.get("ccarfcn")).get(k).equals("TBD"))
							break;
//						if(version.contains("21D")) {
							String TxSSB="";
							if(cellDataH.get("nrfreq").equals("n261")) {
								TxSSB= "16";
							}
							else if (cellDataH.get("nrfreq").equals("n260")) {
								TxSSB="32";
							}
						pw1.print("\"ADD\",\""+ neid +"\",\""+ ((ArrayList<String>)cellDataH.get("ccnum")).get(k) +"\",\""+ ((ArrayList<String>)cellDataH.get("ccnum")).get(k) +"\",\""+nrfreq +"\",\""+ ((ArrayList<String>)cellDataH.get("ccarfcn")).get(k) +"\",\""+ ((ArrayList<String>)cellDataH.get("ccarfcn")).get(k) +"\",\"nr-bandwidth-100\",\"nr-bandwidth-100\",\""+ cellDataH.get("nrpci") +"\",\"dl-antenna-count-" + antennainfo[0] + "tx\",\"ul-antenna-count-" + antennainfo[1] + "rx\",\""+ antennainfo[1] +"\",\"prach-root-sequence-index-auto\",\"10\",\"189\",\"ssb-per-ro-one-choice\",\"auto-prach-rb-offset-off\",\"" + TxSSB +"\",\"not-use\",\"1\",\"" + beamBookType + "\",\"\"\n");
//						}else {
//						pw1.print("\"ADD\",\""+ neid +"\",\""+ ((ArrayList<String>)cellDataH.get("ccnum")).get(k) +"\",\""+ ((ArrayList<String>)cellDataH.get("ccnum")).get(k) +"\",\""+nrfreq +"\",\""+ ((ArrayList<String>)cellDataH.get("ccarfcn")).get(k) +"\",\""+ ((ArrayList<String>)cellDataH.get("ccarfcn")).get(k) +"\",\"nr-bandwidth-100\",\"nr-bandwidth-100\",\""+ cellDataH.get("nrpci") +"\",\"dl-antenna-count-" + antennainfo[0] + "tx\",\"ul-antenna-count-" + antennainfo[1] + "rx\",\""+ antennainfo[1] +"\",\"\",\"12\",\"189\",\"ssb-per-ro-one-choice\",\"not-use\",\"4096\",\"" + beamBookType + "\"\n");
//						}
						

					}
//					if(version.contains("21D")) {
						pw1.print("\"@SON_INFORMATION\"\n");
						pw1.print("\"Initial PCI\",\"Initial RSI\",\"Initial NCRT\",\"Initial LTE NCRT\",\"Initial ZCZC\",\"Initial PRACH Config Index\"\n");
						pw1.print("\"off\",\"off\",\"off\",\"off\",\"off\",\"off\"");
						
//					}else {
//						pw1.print("\"@SON_INFORMATION\"\n");
//						pw1.print("\"Initial PCI\",\"Initial RSI\",\"Initial NCRT\"\n");
//						pw1.print("\"off\",\"off\",\"off\"");
//					}
				}
				else if(version.contains("22A"))
					
				{
					pw1.print("\"@CELL_INFORMATION\"\n");
					
					pw1.print("\"State\",\"Sector ID\",\"Carrier ID\",\"Cell Num\",\"NR Frequency Band\",\"NR DL Arfcn\",\"NR UL Arfcn\",\"NR DL Bandwidth\","
							+ "\"NR UL Bandwidth\",\"NR Physical Cell ID\",\"DL Antenna Count\",\"UL Antenna Count\",\"Number of Rx Paths per RU\",\"PRACH RSI\","
							+ "\"PRACH ZCZC\",\"PRACH Configuration Index\",\"PRACH SSB Per RO\",\"PRACH RB Offset Auto Configuration\",\"Number of Tx SSB\",\"Tracking Area Code Usage\",\"Tracking Area Code\",\"Beambook Type\"\n");
					
					String neid = "";
					if(cellDataH.get("neid").toString().length() > 4) {
						neid = new Integer(Integer.parseInt(cellDataH.get("neid").toString().substring(cellDataH.get("neid").toString().length() - 4))).toString();
					} else {
						neid = cellDataH.get("neid").toString();
					}
					String nrfreq = cellDataH.get("nrfreq").toString().replaceAll("[^0-9]", "");
					String[] antennainfo = cellDataH.get("aumimoconf").toString().split("\\D");
						
					for(int k=0;k<8;k++)
					{	
						if(((ArrayList<String>)cellDataH.get("ccarfcn")).get(k).equals("TBD"))
							break;
						//pw1.print("\"ADD\",\""+ neid +"\",\""+ ((ArrayList<String>)cellDataH.get("ccnum")).get(k) +"\",\""+ ((ArrayList<String>)cellDataH.get("ccnum")).get(k) +"\",\""+nrfreq +"\",\""+ ((ArrayList<String>)cellDataH.get("ccarfcn")).get(k) +"\",\""+ ((ArrayList<String>)cellDataH.get("ccarfcn")).get(k) +"\",\"nr-bandwidth-100\",\"nr-bandwidth-100\",\""+ cellDataH.get("nrpci") +"\",\"dl-antenna-count-" + antennainfo[0] + "tx\",\"ul-antenna-count-" + antennainfo[1] + "rx\",\""+ antennainfo[1] +"\",\""+ cellDataH.get("prachrsi") +"\",\"0\",\"194\",\"ssb-per-ro-two-choice\",\"56\",\"not-use\",\"1\",\"v40h120\"\n");
//						pw1.print("\"ADD\",\""+ neid +"\",\""+ ((ArrayList<String>)cellDataH.get("ccnum")).get(k) +"\",\""+ ((ArrayList<String>)cellDataH.get("ccnum")).get(k) 
//								+"\",\""+nrfreq +"\",\""+ ((ArrayList<String>)cellDataH.get("ccarfcn")).get(k) +"\",\""+ ((ArrayList<String>)cellDataH.get("ccarfcn")).get(k) 
//								+"\",\"nr-bandwidth-100\",\"nr-bandwidth-100\",\""+ cellDataH.get("nrpci") +"\",\"dl-antenna-count-" + antennainfo[0] + "tx\",\"ul-antenna-count-" 
//								+ antennainfo[1] + "rx\",\""+ antennainfo[1] +"\",\"\",\"0\",\"194\",\"ssb-per-ro-two-choice\",\"auto-prach-rb-offset-on-multiple-regions\",\"56\",\"not-use\",\"1\",\"v40h120\"\n");

						pw1.print("\"ADD\",\""+ neid +"\",\""+ ((ArrayList<String>)cellDataH.get("ccnum")).get(k) +"\",\""+ ((ArrayList<String>)cellDataH.get("ccnum")).get(k) 
								+"\",\""+nrfreq +"\",\""+ ((ArrayList<String>)cellDataH.get("ccarfcn")).get(k) +"\",\""+ ((ArrayList<String>)cellDataH.get("ccarfcn")).get(k) 
								+"\",\"nr-bandwidth-100\",\"nr-bandwidth-100\",\""+ cellDataH.get("nrpci") +"\",\"dl-antenna-count-" + antennainfo[0] + "tx\",\"ul-antenna-count-" 
								+ antennainfo[1] + "rx\",\""+ antennainfo[1] +"\",\"\",\"0\",\"194\",\"ssb-per-ro-two-choice\",\"auto-prach-rb-offset-on-multiple-regions\",\"56\",\"not-use\",\"1\",\"v40h120\"\n");

					}
						
					pw1.print("\"@SON_INFORMATION\"\n");
					pw1.print("\"Initial PCI\",\"Initial RSI\",\"Initial NCRT\",\"Initial LTE NCRT\"\n");
					pw1.print("\"off\",\"off\",\"off\",\"off\"");
				}else if (version.contains("22C"))

				{
					pw1.print("\"@CELL_INFORMATION\"\n");

					pw1.print(
							"\"State\",\"Sector ID\",\"Carrier ID\",\"Cell Num\",\"Cell Resource ID\",\"NR Frequency Band\",\"NR DL Arfcn\",\"NR UL Arfcn\",\"NR DL Bandwidth\","
									+ "\"NR UL Bandwidth\",\"NR Physical Cell ID\",\"DL Antenna Count\",\"UL Antenna Count\",\"Number of Rx Paths per RU\",\"Cell Path Type\",\"PRACH RSI\","
									+ "\"PRACH ZCZC\",\"PRACH Configuration Index\",\"PRACH SSB Per RO\",\"PRACH RB Offset Auto Configuration\",\"Number of Tx SSB\",\"Tracking Area Code Usage\",\"Tracking Area Code\",\"Beambook Type\",\"Cell Coverage\"\n");

					String neid = "";
					if (cellDataH.get("neid").toString().length() > 4) {
						neid = new Integer(Integer.parseInt(cellDataH.get("neid").toString()
								.substring(cellDataH.get("neid").toString().length() - 4))).toString();
					} else {
						neid = cellDataH.get("neid").toString();
					}
					String nrfreq = cellDataH.get("nrfreq").toString().replaceAll("[^0-9]", "");
					String[] antennainfo = cellDataH.get("aumimoconf").toString().split("\\D");

					for (int k = 0; k < 8; k++) {
						if (((ArrayList<String>) cellDataH.get("ccarfcn")).get(k).equals("TBD"))
							break;

						pw1.print("\"ADD\",\"" + neid + "\",\"" + ((ArrayList<String>) cellDataH.get("ccnum")).get(k)
								+ "\",\"" + ((ArrayList<String>) cellDataH.get("ccnum")).get(k) + "\",\"-1\",\""
								+ nrfreq + "\",\"" + ((ArrayList<String>) cellDataH.get("ccarfcn")).get(k) + "\",\""
								+ ((ArrayList<String>) cellDataH.get("ccarfcn")).get(k)
								+ "\",\"nr-bandwidth-100\",\"nr-bandwidth-100\",\"" + cellDataH.get("nrpci")
								+ "\",\"dl-antenna-count-" + antennainfo[0] + "tx\",\"ul-antenna-count-"
								+ antennainfo[1] + "rx\",\"" + antennainfo[1]
								+ "\",\"select-path-all\",\"\",\"0\",\"194\",\"ssb-per-ro-two-choice\",\"auto-prach-rb-offset-on-multiple-regions\",\"56\",\"not-use\",\"1\",\"v40h120\",\"\"\n");

					}

					pw1.print("\"@SON_INFORMATION\"\n");
					pw1.print(
							"\"Initial PCI\",\"Initial RSI\",\"Initial NCRT\",\"Initial LTE NCRT\",\"Initial ZCZC\",\"Initial PRACH Config Index\"\n");
					pw1.print("\"off\",\"off\",\"off\",\"off\",\"off\",\"off\"");
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
		return filename;
	}

}
