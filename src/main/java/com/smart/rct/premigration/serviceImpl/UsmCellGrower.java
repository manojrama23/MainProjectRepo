package com.smart.rct.premigration.serviceImpl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UsmCellGrower {
	public static boolean printcell = true;
	public static boolean printdsp = true;
	public static boolean printsup = true;
	public static boolean printlog = true;
	public static boolean printverb = false;
	public static boolean printall = true;
	private static ArrayList<Integer> enbidA = new ArrayList<>();
	public static String sciq = "";
	public static String ciq = "";
	public static boolean scrubprovidedboo = false;
	public static boolean printhelpboo = false;
	public static String dlogbuffer = "";
	public static boolean autoboo = false;
	public static boolean consolidatedboo = false;
	public static String Dir = "";
	public static String GrowDir = "";
	public static String adid = "UNKNOWNUSER";
	public static String version = "8.0.1";
	public static boolean cbrsboo = false;
	public static boolean supportedboo = false;
	public static boolean AdNew = false;
	public static String new_dir;
	public static String timestamp;
	public static ArrayList<HashMap<String, String>> necessaryA;
	public static String growfolder;
	public static String dbug_folder;
	public static String debuglog;
	public static String sciq_folder;
	public static int enbidx;
	public static ArrayList<Integer> failedtogrow = new ArrayList<>();
	// carrier add
	public static String supportCA;
	// New Implementation
	public static String portnum1 = "";
	public static String portnum2 = "";
	public static String portnum3 = "";
	public static String lccnum1 = "";
	public static String lccnum2 = "";
	public static String lccnum3 = "";
	public static String bandwidth1 = "";
	public static String bandwidth2 = "";
	public static String bandwidth3 = "";
	public static String dspid1 = "";
	public static String dspid2 = "";
	public static String dspid3 = "";
	public static String band1 = "";
	public static String band2 = "";
	public static String band3 = "";
	final static Logger logger = LoggerFactory.getLogger(UsmCellGrower.class);

	public synchronized boolean cellTemplate(String[] args) {

		logger.error("start cellTemplate***"+Arrays.toString(args));
		logger.error("cellTemplate " + args[0]);
		if (args.length == 0)
			printhelpboo = true;

		for (int i = 0; i < args.length; i++) {
			// function to check the the perl command passed through command line arguments
			checkcmdlinearg(args, i);
		}

		if (printhelpboo) {
			// function contains the print statements to be displayed when conditions of cmd
			// arguments are not met
			printmsg();
		}

		try {
			if (autoboo) {
				GrowDir = Dir + "/RanCommTool/Cell_Grows/" + adid;
			}

			growfolder = new_dir;
			File f1 = new File(growfolder);
			f1.mkdirs();

			dbug_folder = growfolder + "/Debug_Log";
			File f2 = new File(dbug_folder);
			f2.mkdirs();

			debuglog = dbug_folder + "/DEBUG_LOG_" + timestamp + ".txt";
			File f3 = new File(debuglog);
			f3.createNewFile();
		} catch (IOException e) {
			logger.error("Exception in USM Cell Grower " + args[0] + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		if (!consolidatedboo) {
			populate_necessaryA();
		} else {
			populate_necessaryA_cons();
		}
		logger.error("Before sciq_folder");
		sciq_folder = growfolder + "/Scrub_Ciq";
		logger.error("Sciq_folder " + sciq_folder + " " + args[0]);
		File f4 = new File(sciq_folder);
		f4.mkdirs();

		if (!scrubprovidedboo) {
			sciq = sciq_folder + "/SCRUB_CIQ_" + timestamp + ".csv";
			logger.error("sciq** " + sciq + " " + args[0]);
			create_scrubCIQ(args[0]);
		}

		// necessaryA.forEach(hm -> System.out.println(hm.get("numeric")));
		for (enbidx = 0; enbidx < enbidA.size(); enbidx++) {
			ArrayList<TreeMap<String, Object>> cellDataA = new ArrayList<>();
			ArrayList<TreeMap<String, Object>> rrhDataA = new ArrayList<>();
			ArrayList<ArrayList<TreeMap<String, Object>>> lccA = new ArrayList<>();
			ArrayList<TreeMap<String, Object>> rrhDataB = new ArrayList<>();
			ArrayList<TreeMap<String, Object>> rrhDataC = new ArrayList<>();
			populate_DataAs(cellDataA, rrhDataA, rrhDataB, rrhDataC);

			// New Implementation
			dspConfiguration(cellDataA);

			// sorting data in celldata and rrhdata
			sortData(cellDataA, rrhDataA);

			// printing the values in log
			cellDataA.forEach(tm -> {
				tm.forEach((key, value) -> {
					print_logs(key + " " + value);
				});
				print_logs("-----------");
			});

			rrhDataA.forEach(tm -> {
				tm.forEach((key, value) -> {
					if (key.equals("antennaPortMapA")) {
						StringBuffer tmp = new StringBuffer(key);
						tmp.append(" [");
						for (int i = 0; i < ((int[]) value).length; i++) {
							if (i != 0)
								tmp.append(" " + ((int[]) value)[i]);
							else
								tmp.append(((int[]) value)[i]);
						}
						tmp.append("]");
						print_logs(tmp.toString());
					} else
						print_logs(key + " " + value);
				});
				print_logs("-----------");
			});
			System.out.println("New Data RRH data b");
			rrhDataB.forEach(tm -> {
				tm.forEach((key, value) -> {
					if (key.equals("antennaPortMapA")) {
						StringBuffer tmp = new StringBuffer(key);
						tmp.append(" [");
						for (int i = 0; i < ((int[]) value).length; i++) {
							if (i != 0)
								tmp.append(" " + ((int[]) value)[i]);
							else
								tmp.append(((int[]) value)[i]);
						}
						tmp.append("]");
						print_logs(tmp.toString());
					} else
						print_logs(key + " " + value);
				});
				print_logs("-----------");
			});

			populate_lccA(lccA);
			logger.error(args[0] + ":::neIdbefore" + enbidx + "**enbidx-enbidA****" + enbidA);
			set_supported_boo(cellDataA, rrhDataA, lccA, enbidA.get(enbidx));
			logger.error(args[0] + ":::neId-after" + enbidx + "**enbidx-enbidA****" + enbidA);
			if (supportedboo) {
				assignArbitrationIndex(lccA, cellDataA);

				// assign_MCtypeCandidatesToCells(lccA, cellDataA);

				/* if (mapDSPsToCells(lccA, cellDataA, enbidA.get(enbidx).toString())) { */
				if (version.equals("8.0.1")) {
					createGrowTemplate_801(cellDataA, rrhDataA, lccA, enbidA.get(enbidx).toString(), timestamp);
				} else if ((version.equals("8.5.1")) && (cbrsboo == false)) {
					String growfile = growfolder + "/GROW_CELL_" + timestamp + ".csv";
					createGrowTemplate_851(cellDataA, rrhDataA, lccA, growfile);
				} else if (version.equals("8.5.2")) {
					String growfile = growfolder + "/GROW_CELL_" + timestamp + ".csv";
					createGrowTemplate_851(cellDataA, rrhDataA, lccA, growfile);
				} else if (version.equals("9.0.0")) {
					String growfile = growfolder + "/GROW_CELL_" + timestamp + ".csv";
					createGrowTemplate_900(cellDataA, rrhDataA, lccA, growfile);
				} else if (version.equals("9.5.0")) {
					String growfile = growfolder + "/GROW_CELL_" + timestamp + ".csv";
					createGrowTemplate_950(cellDataA, rrhDataA, lccA, growfile);
				} else if (version.equals("20.A.0") || version.equals("20.B.0")) {
					String growfile = growfolder + "/GROW_CELL_" + timestamp + ".csv";
					createGrowTemplate_20A0(cellDataA, rrhDataA, lccA, growfile);
				} else if (version.equals("20.C.0") || version.equals("21.A.0")) {
					String growfile = growfolder + "/GROW_CELL_" + timestamp + ".csv";
					createGrowTemplate_20C0(cellDataA, rrhDataA, lccA, growfile);
				} else if (version.equals("21.B.0") || version.equals("21.C.0")) {
					String growfile = growfolder + "/GROW_CELL_" + timestamp + ".csv";
					createGrowTemplate_21B0(cellDataA, rrhDataA, lccA, growfile, rrhDataB, rrhDataC);
				} else if (version.equals("21.D.0")) {
					String growfile = growfolder + "/GROW_CELL_" + timestamp + ".csv";
					createGrowTemplate_21D0(cellDataA, rrhDataA, lccA, growfile, rrhDataB, rrhDataC);
				} else if (version.equals("22.A.0")) {
					String growfile = growfolder + "/GROW_CELL_" + timestamp + ".csv";
					logger.error(args[0] + "****UsmCellGrower22V before::"+growfile);
					UsmCellGrower22V.createGrowTemplate_22A(cellDataA, rrhDataA, lccA, growfile, rrhDataB, rrhDataC,
							supportCA, args[0]);
					logger.error(args[0] + "****UsmCellGrower22V after");
				}else if (version.equals("22.C.0")) {
					String growfile = growfolder + "/GROW_CELL_" + timestamp + ".csv";
					logger.error(args[0] + "****UsmCellGrower22C before::"+growfile);
					UsmCellGrower22C.createGrowTemplate_22C(cellDataA, rrhDataA, lccA, growfile, rrhDataB, rrhDataC,
							supportCA, args[0]);
					logger.error(args[0] + "****UsmCellGrower22C after");
				} else {
					mydie("Unsupported Version: $version with cbrs set to $cbrsboo");
				}
				/*
				 * } else { failedtogrow.add(enbidx); }
				 */
			} else {
				failedtogrow.add(enbidx);
			}
		}

		if (failedtogrow.size() > 0) {
			for (int i = 0; i < failedtogrow.size(); i++) {
				print_logs(enbidA.get(failedtogrow.get(i)) + " FAILED TO GROW. SCRUB CIQ REQUIRES EDITS.");
			}
		}
		return supportedboo;
	}

	private void checkcmdlinearg(String[] args, int i) {
		// Checks for scrubfile present in command line
		if (args[i].equals("-s") && i < args.length - 1) {
			if (args[i + 1].endsWith(".csv")) {
				scrubprovidedboo = true;
				sciq = args[i + 1];
			} else {
				printhelpboo = true;
			}
		}
		if (args[i].equals("-c") && i < args.length - 1) {
			if (args[i + 1].endsWith(".csv")) {
				scrubprovidedboo = false;
				ciq = args[i + 1];
			} else {
				printhelpboo = true;
			}
		}
		if (args[i].equals("-cons") && i < args.length - 1) {
			consolidatedboo = true;
			if (args[i + 1].endsWith(".csv")) {
				autoboo = false;
				ciq = args[i + 1];
			} else if (args[i + 1].matches("(.*)[A-Z]:(.*)") && !args[i + 1].matches("(.*)[.]{3}(.*)")) {
				autoboo = true;
				Dir = args[i + 1];
				ciq = Dir + "/RanCommTool/CIQ/RAN_consolidated-xlsx.csv";
			} else {
				printhelpboo = true;
			}
		}
		if (args[i].equals("-v") && i < args.length - 1) {
			if (args[i + 1].matches("\\d.\\d.\\d")) {
				version = args[i + 1];
			} else if (args[i + 1].matches("\\d+.\\D.\\d")) {
				version = args[i + 1];
			}
		}
		if (args[i].equals("-u") && i < args.length - 1) {
			adid = args[i + 1];
		}
		if (args[i].equals("-cbrs")) {
			cbrsboo = true;
		}
		if (args[i].equals("-dir")) {
			new_dir = args[i + 1];
		}
		if (args[i].equals("-n")) {
			timestamp = args[i + 1];
		}
		if (args[i].matches("\\d{5,}") && !args[i].matches("\\D") && !args[i].matches("\\d{7,}")) {
			enbidA.add(Integer.parseInt(args[i]));
		}
		if (args[i].equals("-h")) {
			printhelpboo = true;
		}

		// carrier add
		if (args[i].equals("true")) {
			supportCA = "true";
		} else {
			supportCA = "false";
		}

	}

	private  synchronized void addEnbID(String[] args, int i) {
		enbidA.add(Integer.parseInt(args[i]));
	}

	private static void printmsg() {
		System.out.print("\nThis script takes a list of sites (5 digit site id's) and creates cell grow\n");
		System.out.print("templates for each site in the list. To run for the first time, follow these\n");
		System.out.print("steps:\n\n");
		System.out.print("1) Open the CIQ and perform a search and replace of all commas, changing \n");
		System.out.print("   commas to simicolons (replace , with ;). Replace all line feeds (ctrl-j)\n");
		System.out.print("   with space.\n\n");
		System.out.print("2) Save the file giving it any name, but it must be saved as a CSV file.\n\n");
		System.out.print("3) Run the command as follows:\n\n");
		System.out.print("   USM_cell_grower.pl <siteid> <siteid> <siteid>... -c <CIQ csv from step 1>\n\n");
		System.out.print("Arguments:\n----------\n\n");
		System.out.print("-s <Scrub CIQ file>.csv           Use the provided Scrub CIQ.\n");
		System.out.print("-c <CIQ file>.csv                 Use the provided raw csv CIQ to make a Scrub CIQ file\n");
		System.out.print("-cons oRAN_consolidated-xlsx.csv  Use the consolidated CIQ\n");
		System.out.print("-cons <Mapped Drive> <oRAN User>  Use the oRAN tool\n");
		System.out.print("-v <software version>             Default is 8.0.1, supports 8.0.1 and 8.5.1\n");
		System.out.print("-cbrs                             Sets cbrs boolean to true (not yet supported)\n");
		System.out.print("-dir                              Path of the grow file\n\n");
		System.out.print("v0.2  - Adds Radio Types 3JR53445AA and 3JR53446AA\n\n");
		System.out.print("v1.1  - Adds the following functionality:\n");
		System.out.print("      - Carrier Spacing\n      - Distinction between radio vendors for MCT considerations\n");
		System.out.print("      - Support for maximum cells per DSP based on carrier\n");
		System.out.print("      - Anchor cell based MCT control to reduce arbitration\n\n");
		System.out.print("v1.2  - Adds DSP separation between cells associated with ALU/ASL radios and cells\n");
		System.out.print("        associated with Samsung radios.\n");
		System.out.print("      - Does not create a grow template if the radio vendor cannot be identified.\n");
		System.out.print("      - Earlier versions failed to change AWS-2 to AWS-1 and PCS-2 to PCS in bandname.\n\n");
		System.out.print("v1.3  - Fixes issue in which cable lengths are assigned value of -1.\n");
		System.out.print("      - Improves carrier spacing for cases in which all carriers have the same bandwidth\n");
		System.out.print("      - Accounts for 850 ASL antenna port maping of 1,3,2,4 instead of 1,2,3,4.\n\n");
		System.out.print(
				"v1.4  - Fixes issue in which AWS radio 3JR63090AA was not recognized for carrier band 66 (AWS-3).\n\n");
		System.out.print("v2.0  - Enables use from the consolidated CIQ.\n\n");
		System.out.print("v2.4  - Fixes issue with 700 ALU radio being assigned code for pcs radio.\n");
		System.out.print("      - Fixes issue in which grow template was not performed for site with a 6 digit ID\n");
		System.out.print(
				"      - Added functionality to account for new Optic Distance(km) column in CIQ. This functionality\n");
		System.out.print(
				"        includes assigning DSP Optic Distance=20-km for cells associated with ASL radios in which\n");
		System.out.print("        the real optic distance is greater than 5 km.\n\n");
		System.out.print(
				"v2.5  - Checks for unsupported configurations with regard to optic distance > max supported distance,\n");
		System.out.print(
				"        Samsung radio maximum bandwidth, and Samsung radio maximum power. If the configuration is not\n");
		System.out.print(
				"        supported, a report is generated explaining what should be changed in the CIQ in order to\n");
		System.out.print("        create a grow template.\n\n");
		System.out.print(
				"v2.6  - Fixes a bug invoked when the 2nd or 3rd channel card has multiple arbitration groups. The\n");
		System.out.print(
				"        code lacked a check of the card when assigning arbitration group indexes, and the result was\n");
		System.out.print("        that arbitration groups on one channel card would be applied incorrectly.\n\n");
		System.out.print(
				"v2.7  - Now a grow template will be created even if the RRH type is not known. The Vendor will default\n");
		System.out.print("        to 'alu'.\n\n");
		System.out.print("v2.8  - November 19, 2019 - 8.5.1 format changed after Windsor Medium upgrade to 9.0.\n\n");
		// System.exit(0);
	}

	private static void populate_necessaryA() {
		necessaryA = new ArrayList<>();
		String tags[] = { "market", "marketname", "enbname", "enbid", "cellid", "tac", "pci", "rach", "band",
				"bandwidth", "earfcndl", "earfcnul", "power", "boardport", "txd", "rxd", "rrh", "AdditionalBoardID",
				"delaydl", "delayul", "cablelengthdl", "cablelengthul", "iottac", "format", "zczc", "opticdist",
				"cbrcfcc", "cprivalue", "additional_port", "thMaxEirp", "AntennaGain", "PreferredEarfcn",
				"DspCellIndex", "DspID", "RUPortID", "mct", "pracformat", "eMTC", "adstate", "nbiot",
				"preferredHighestChannel", "preferredLowestChannel", "preference", "mmuBisectorMode" ,"rfPortOpertionMode"};
		String headers[] = { "MARKET", "MARKETNAME", "ENBNAME", "SAMSUGENBID", "CELLID", "TAC", "PCI", "RACH",
				"BANDNAME", "BANDWIDTH", "EARFCNDL", "EARFCNUL", "OUTPUTPOWER(DBM)", "CPRIPORT", "TXDIVERSITY",
				"RXDIVERSITY", "RRHCODE", "ADDITIONAL_BOARD_ID", "ANTENNAPATHDELAYDL", "ANTENNAPATHDELAYUL",
				"ANTENNAPATHDELAYDL(M)", "ANTENNAPATHDELAYUL(M)", "IOTTAC", "PREAMBLEFORMAT", "PRACHCS(ZCZC)",
				"OPTICDISTANCE(KM)", "CBRSFCCID", "CPRI_VALUE", "ADDITIONAL_PORT", "MAX_EIRP_THRESHOLD",
				"ANTENNA_GAIN_DBI", "PREFERRED_EARFCN", "DSP_CELL_INDEX", "DSP_ID", "RUPORTID", "MCT", "PRACFORMAT",
				"EMTC", "ADMSTATE", "NBIOT", "PREFERREDHIGHESTCHANNEL", "PREFERREDLOWESTCHANNEL", "PREFERENCE",
				"MMUBISECTORMODE" ,"RFPORTOPERTIONMODE"};
		String posss[] = { "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1",
				"-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1",
				"-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1" ,"-1"};
		String matchexacts[] = { "1", "1", "1", "1", "1", "1", "1", "1", "1", "0", "1", "1", "1", "0", "0", "0", "1",
				"1", "1", "1", "1", "1", "0", "0", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "0", "0", "0",
				"0", "1", "1", "1", "1", "1","1" };
		String numerics[] = { "0", "0", "0", "1", "1", "1", "1", "1", "0", "0", "1", "1", "0", "0", "1", "1", "0", "1",
				"1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "0", "0", "0",
				"-1", "0", "0", "0", "0", "0","0" };
		String defaults[] = { "na", "0", "na", "-1", "-1", "-1", "-1", "-1", "abort", "abort", "-1", "-1", "abort",
				"abort", "-1", "-1", "abort", "0", "0", "0", "0", "", "0", "0", "0", "0", "0", "0", "0", "0", "0", "",
				"", "", "0", "0", "0", "0", "na", "0", "", "", "", "","" };
		String warnings[] = { "COULD NOT FIND MARKET HEADER", "COULD NOT FIND MARKET HEADER",
				"COULD NOT FIND ENBNAME HEADER", "COULD NOT FIND SAMSUNGENBID3", "COULD NOT FIND CELLHEADERID",
				"COULD NOT FIND TAC HEADER", "COULD NOT FIND PCI HEADER", "COULD NOT FIND RACH HEADER",
				"COULD NOT FIND BANDNAME HEADER", "COULD NOT FIND BANDWIDTH HEADER", "COULD NOT FIND EARFCNDL HEADER",
				"COULD NOT FIND EARFCNUL HEADER", "COULD NOT FIND ZCZC HEADER", "COULD NOT FIND OUTPUT POWER HEADER",
				"COULD NOT FIND CPRIPORT HEADER", "COULD NOT FIND TXDIVERSITY HEADER",
				"COULD NOT FIND RXDIVERSITY HEADER", "COULD NOT FIND RRHCODE HEADER",
				"COULD NOT FIND ADDITIONAL_BOARD_ID HEADER", "COULD NOT FIND ANTENNAPATHDELAYDL HEADER",
				"COULD NOT FIND ANTENNAPATHDELAYUL HEADER", "COULD NOT FIND ANTENNAPATHDELAYDL M HEADER",
				"COULD NOT FIND ANTENNAPATHDELAYUL M HEADER", "COULD NOT FIND IOTTAC HEADER",
				"COULD NOT FIND PREAMBLEFORMAT HEADER", "COULD NOT FIND OPTIC DISTANCE HEADER",
				"COULD NOT FIND CBRSFCCID HEADER", "COULD NOT FIND CPRI_VALUE HEADER",
				"COULD NOT FIND ADDITIONAL_PORT HEADER", "COULD NOT FIND MAX_EIRP_THRESHOLD HEADER",
				"COULD NOT FIND ANTENNA_GAIN_DB HEADER", "COULD NOT FIND Preferred_Earfcn HEADER",
				"COULD NOT FIND DSP_CELL_INDEX. HEADER", "COULD NOT FIND DSP_ID. HEADER",
				"COULD NOT FIND RUPortID. HEADER", "COULD NOT FIND MCT. HEADER", "COULD NOT FIND PRACH. HEADER",
				"COULD NOT FIND EMTC HEADER", "COULD NOT FIND ADM. HEADER", "COULD NOT FIND NBIOT HEADER",
				"COULD NOT FIND PREF_HIGH_CHANNEL HEADER", "COULD NOT FIND PREF_LOW_CHANNEL HEADER",
				"COULD NOT FIND PREFERENCE HEADER", "COULD NOT FIND MMU BI. MODE HEADER","COULD NOT FIND rfPortOpertionMode HEADER" };

		for (int i = 0; i < tags.length; i++) {
			HashMap<String, String> hm = new HashMap<>();
			hm.put("tag", tags[i]);
			hm.put("header", headers[i]);
			hm.put("poss", posss[i]);
			hm.put("matchexact", matchexacts[i]);
			hm.put("numeric", numerics[i]);
			hm.put("default", defaults[i]);
			hm.put("warning", warnings[i]);

			necessaryA.add(hm);
		}
	}

	private static void populate_necessaryA_cons() {
		necessaryA = new ArrayList<>();
		String tags[] = { "market", "marketName", "enbname", "enbid", "cellid", "tac", "pci", "rach", "band",
				"bandwidth", "earfcndl", "earfcnul", "power", "boardport", "txd", "rxd", "rrh", "AdditionalBoardID",
				"delaydl", "delayul", "cablelengthdl", "cablelengthul", "iottac", "format", "zczc", "opticdist",
				"cbrcfcc", "cprivalue", "cpriPortAssignment", "additional_port", "thMaxEirp", "AntennaGain",
				"PreferredEarfcn", "DspCellIndex", "DspID", "RUPortID", "mct", "pracformat", "eMTC", "adstate", "nbiot",
				"preferredHighestChannel", "preferredLowestChannel", "preference", "mmuBisectorMode","rfPortOpertionMode" };
		String headers[] = { "MARKETNAME", "MARKET NAME", "TARGET_ENBNAME", "TARGET_ENBID", "TARGET_CELLID", "TAC(HEX)",
				"PCI", "RACH", "BAND", "BANDWIDTH(MHZ)", "EARFCN_DL", "EARFCN_UL", "SAMSUNGOUTPUTPOWER(WATT)",
				"CPRIPORTASSIGNMENT", "TXDIVERSITY", "RXDIVERISTY", "RRHCODE", "ADDITIONAL_BOARD_ID",
				"ANTENNAPATHDELAYDL", "ANTENNAPATHDELAYUL", "ANTENNAPATHDELAYDL(M)", "ANTENNAPATHDELAYUL(M)",
				"NB-IOTTAC", "PREAMBLEFORMATPRACHINDEX", "PRACHCS(ZCZC)", "OPTICDISTANCE(KM)", "CBRSFCCID",
				"CPRI_VALUE", "CPRIPORTASSIGNMENT", "ADDITIONAL_PORT", "MAX_EIRP_THRESHOLD", "ANTENNA_GAIN_DBI",
				"PREFERRED_EARFCN", "DSP_CELL_INDEX", "DSP_ID", "RUPORTID", "MCT", "PRACFORMAT", "EMTC", "ADMSTATE",
				"NBIOT", "PREFERREDHIGHESTCHANNEL", "PREFERREDLOWESTCHANNEL", "PREFERENCE", "MMUBISECTORMODE","RFPORTOPERTIONMODE" };
		String posss[] = { "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1",
				"-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1",
				"-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1","-1" };
		String matchexacts[] = { "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1",
				"1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "0", "0",
				"0", "0", "0", "1", "1", "1", "1" ,"1"};
		String numerics[] = { "0", "0", "0", "1", "1", "1", "1", "1", "0", "0", "1", "1", "0", "0", "1", "1", "0", "1",
				"1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "0", "0", "0",
				"0", "0", "0", "0", "0", "0","0" };
		String defaults[] = { "na", "na", "na", "-1", "-1", "-1", "-1", "-1", "abort", "abort", "-1", "-1", "abort",
				"abort", "-1", "-1", "abort", "0", "0", "0", "0", "", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0",
				"", "", "", "0", "0", "0", "0", "na", "0", "", "", "", "" ,""};
		String warnings[] = { "COULD NOT FIND MARKET HEADER", "COULD NOT FIND MARKET HEADER",
				"COULD NOT FIND ENBNAME HEADER", "COULD NOT FIND SAMSUNGENBID HEADER", "COULD NOT FIND CELLID HEADER",
				"COULD NOT FIND TAC HEADER", "COULD NOT FIND PCI HEADER", "COULD NOT FIND RACH HEADER",
				"COULD NOT FIND BANDNAME HEADER", "COULD NOT FIND BANDWIDTH HEADER", "COULD NOT FIND EARFCNDL HEADER",
				"COULD NOT FIND EARFCNUL HEADER", "COULD NOT FIND ZCZC HEADER", "COULD NOT FIND OUTPUTPOWER HEADER",
				"COULD NOT FIND CPRIPORT HEADER", "COULD NOT FIND TXDIVERSITY HEADER",
				"COULD NOT FIND RXDIVERISTY HEADER", "COULD NOT FIND RRHCODE HEADER",
				"COULD NOT FIND ADDITIONAL_BOARD_ID HEADER", "COULD NOT FIND ANTENNAPATHDELAYDL HEADER",
				"COULD NOT FIND ANTENNAPATHDELAYUL HEADER", "COULD NOT FIND ANTENNAPATHDELAYDL M HEADER",
				"COULD NOT FIND ANTENNAPATHDELAYUL M HEADER", "COULD NOT FIND IOTTAC HEADER",
				"COULD NOT FIND PREAMBLEFORMAT HEADER", "COULD NOT FIND OPTIC DISTANCE HEADER",
				"COULD NOT FIND CBRSFCCID HEADER", "COULD NOT FIND CPRI_VALUE HEADER", "COULD NOT FIND CPRI HEADER",
				"COULD NOT FIND ADDITIONAL_PORT HEADER", "COULD NOT FIND MAX_EIRP_THRESHOLD HEADER",
				"COULD NOT FIND ANTENNA_GAIN_DBI HEADER", "COULD NOT FIND PREFERRED_EARFCN.. HEADER",
				"COULD NOT FIND DSP_CELL_INDEX.. HEADER", "COULD NOT FIND DSP_ID. HEADER",
				"COULD NOT FIND RUPortID.. HEADER", "COULD NOT FIND MCT. HEADER", "COULD NOT FIND PRACH. HEADER",
				"COULD NOT FIND EMTC HEADER", "COULD NOT FIND ADM. HEADER", "COULD NOT FIND NBIOT HEADER",
				"COULD NOT FIND PREF_HIGH_CHANNEL HEADER", "COULD NOT FIND PREF_LOW_CHANNEL HEADER",
				"COULD NOT FIND PREFERENCE HEADER", "COULD NOT FIND MMU BI. MODE HEADER", "COULD NOT FIND RFPORTOPERTIONMODE HEADER" };

		for (int i = 0; i < tags.length; i++) {
			HashMap<String, String> hm = new HashMap<>();
			hm.put("tag", tags[i]);
			hm.put("header", headers[i]);
			hm.put("poss", posss[i]);
			hm.put("matchexact", matchexacts[i]);
			hm.put("numeric", numerics[i]);
			hm.put("default", defaults[i]);
			hm.put("warning", warnings[i]);

			necessaryA.add(hm);
		}
	}

	private  void create_scrubCIQ(String neId) {
		logger.error("start create_scrubCIQ***"+neId);
		PrintWriter pw = null;
		Scanner ciqr = null;
		try {;
			File ciqf = new File(ciq);
			logger.error(ciq+"==create_scrubCIQ-ciqf=="+ciqf.exists());
			File f = new File(sciq);
			f.createNewFile();
			logger.error(sciq+"==create_scrubCIQ-sciq=="+f.exists());
			pw = new PrintWriter(new BufferedWriter(new FileWriter(f)));
			ciqr = new Scanner(ciqf);
			int linenumber = 0;
			int linecounter = 0;
			boolean flag=false;
			while (ciqr.hasNextLine()) {
				
				linecounter++;
				String[] lineA = ciqr.nextLine().replaceAll("\\s", "").toUpperCase().split(",");
				if(!flag) {
					logger.error("entered lineA***"+Arrays.toString(lineA));
					flag=true;
				}
				if (linenumber == 0) {
					linenumber = lineA.length;
				}
				if (linenumber != lineA.length) {
					mydie("Badly formed csv file " + linenumber + " " + lineA.length + " " + linecounter
							+ ".\nEnsure there are no commas in .xlsx file before creating .csv");
				}
				if (lineA[0].equals("MARKET") || lineA[0].equals("MARKETNAME")) {
					for (int n = 0; n < necessaryA.size(); n++) {
						for (int i = 0; i < lineA.length; i++) {
							if (necessaryA.get(n).get("matchexact").equals("0")) {
								if (lineA[i].matches("(.*)" + necessaryA.get(n).get("header") + "(.*)")) {
									necessaryA.get(n).replace("poss", Integer.toString(i));
									necessaryA.get(n).replace("warning", "NONE");
								}
							} else {
								if (necessaryA.get(n).get("header").equals(lineA[i])) {
									necessaryA.get(n).replace("poss", Integer.toString(i));
									necessaryA.get(n).replace("warning", "NONE");
								}
							}
						}
					}

					for (int i = 0; i < necessaryA.size(); i++) {
						if (!necessaryA.get(i).get("warning").equals("NONE")) {
							String printvar = necessaryA.get(i).get("warning");
							dlog(printvar + " pos " + i + "\n");
							if (necessaryA.get(i).get("numeric").equals("1")) {
								if (necessaryA.get(i).get("default").equals("-1")) {
									mydie("Unable to recover");
								}
							} else {
								if (necessaryA.get(i).get("default").matches("(.*)abort(.*)")) {
									mydie("Unable to recover");
								}
							}
						}
						if (!necessaryA.get(i).get("poss").equals("-1")) {
							pw.print(lineA[Integer.parseInt(necessaryA.get(i).get("poss"))]);

							if (!(i == necessaryA.size() - 1)) {
								pw.print(",");
							}
						}
						if (i == necessaryA.size() - 1) {
							pw.println();
						}
					}
				}

				boolean writelineboo = false;
				String writeline = "";
				for (int i = 0; i < necessaryA.size(); i++) {
					if (!necessaryA.get(i).get("poss").equals("-1")) {
						if (lineA[Integer.parseInt(necessaryA.get(i).get("poss"))].equals("")
								|| lineA[Integer.parseInt(necessaryA.get(i).get("poss"))].equals("TBD")) {
							writeline = writeline + necessaryA.get(i).get("default");
						} else {
							writeline = writeline + lineA[Integer.parseInt(necessaryA.get(i).get("poss"))];
						}
						if ((i != necessaryA.size() - 1)) {
							writeline = writeline + ",";
						}
						if (necessaryA.get(i).get("tag").equals("enbid")) {
							for (int j = 0; j < enbidA.size(); j++) {
								if (enbidA.get(j).toString()
										.equals(lineA[Integer.parseInt(necessaryA.get(i).get("poss"))])) {
									writelineboo = true;
								}
							}
						}
					}
				}

				if (writelineboo)
					pw.println(writeline);
			}
			logger.error("create scrub ciq done-->"+neId);
		} catch (Exception e) {
			logger.error("Exception in USM Cell Grower " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		} finally {
			if (pw != null) {
				pw.flush();
				pw.close();
			}
			if (ciqr != null) {
				ciqr.close();
			}
		}
	}

	static void mydie(String msg) {
		if (StringUtils.isNotEmpty(msg)) {
			System.out.println(msg.toString());
		}
		// System.exit(0);
	}

	private static void dlog(String logmsg) {
		dlogbuffer += logmsg;
		if (logmsg.matches("(.*)\n(.*)")) {
			String ts = new Date().toString();
			ts = ts.replaceAll(":", "_");
			try {
				File f = new File(debuglog);
				FileWriter fw = new FileWriter(f, true);
				fw.write(ts + " " + dlogbuffer);
				fw.close();
			} catch (Exception e) {
				logger.error("Exception in USM Cell Grower " + ExceptionUtils.getFullStackTrace(e));
				e.printStackTrace();
			}
			System.out.print(dlogbuffer);
			dlogbuffer = "";
		}
	}

	@SuppressWarnings("unchecked")
	private static void populate_lccA(ArrayList<ArrayList<TreeMap<String, Object>>> lccAref) {
		for (int j = 0; j < 3; j++) {
			ArrayList<TreeMap<String, Object>> dspA = new ArrayList<>();
			for (int i = 0; i < 3; i++) {
				TreeMap<String, Object> hm = new TreeMap<>();
				hm.put("anchor", -1);
				hm.put("anchorMCTidx", -1);

				TreeMap<String, Object> delay0 = new TreeMap<>();
				// Details of delay0
				populate_delay0(delay0);

				TreeMap<String, Object> delay1 = new TreeMap<>();
				// Details of delay1
				populate_delay1(delay1);

				hm.put("delay0", delay0);
				hm.put("delay1", delay1);
				dspA.add(hm);
			}
			lccAref.add(dspA);
		}
		((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref.get(0).get(0).get("delay0")).get("format0"))
				.forEach((key, value) -> {
					if (key.matches("(.*)type(.*)")) {
						print_supplemental(key + ": " + ((TreeMap<String, Object>) value).get("typename"));
					}
				});
	}

	private static void populate_delay0(TreeMap<String, Object> delay0) {
		// format0 details
		TreeMap<String, Object> format = new TreeMap<>();

		// carrierLimits
		TreeMap<String, Object> carrierLimits = new TreeMap<>();
		// input for carrierlimits
		carrierLimits.put("num_supPerCar", new int[][] { { 13 }, { 3 } });
		carrierLimits.put("numcellsPerCar", new int[][] { { 13 }, { 0 } });
		format.put("carrierLimits", carrierLimits);

		// insert data for type1
		TreeMap<String, Object> type = new TreeMap<>();
		type.put("tstatus", "unlocked");
		type.put("typename", "cfg61-multi-carrier-10m-5m-6cell");
		type.put("numcells", 0);
		type.put("resourceDef",
				new int[][][] { { { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } },
						{ { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } },
						{ { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } },
						{ { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } } });
		type.put("defaultRDef",
				new int[][][] { { { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } },
						{ { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } },
						{ { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } },
						{ { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } } });
		type.put("resourceTrack", new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
		type.put("numSup", 6);
		format.put("type1", type);

		// insert data for type2
		type = new TreeMap<>();
		type.put("tstatus", "unlocked");
		type.put("typename", "cfg61-multi-carrier-10m-5m-3cell-20m-2t2r-3cell");
		type.put("numcells", 0);
		type.put("resourceDef",
				new int[][][] { { { 5, 10 }, { 5, 10 }, { 5, 10 } }, { { 5, 10 }, { 5, 10 }, { 5, 10 } },
						{ { 5, 10 }, { 5, 10 }, { 5, 10 } }, { { 20 }, { 0 }, { 20 } }, { { 20 }, { 0 }, { 20 } },
						{ { 20 }, { 0 }, { 20 } } });
		type.put("defaultRDef",
				new int[][][] { { { 5, 10 }, { 5, 10 }, { 5, 10 } }, { { 5, 10 }, { 5, 10 }, { 5, 10 } },
						{ { 5, 10 }, { 5, 10 }, { 5, 10 } }, { { 20 }, { 0 }, { 20 } }, { { 20 }, { 0 }, { 20 } },
						{ { 20 }, { 0 }, { 20 } } });
		type.put("resourceTrack", new int[] { 0, 0, 0, 0, 0, 0 });
		type.put("numSup", 6);
		format.put("type2", type);

		// insert data for type3
		type = new TreeMap<>();
		type.put("tstatus", "unlocked");
		type.put("typename", "cfg355-multi-carrier-10m-10m-10m-10m-5m-5m-5m-5m-config3");
		type.put("numcells", 0);
		type.put("resourceDef",
				new int[][][] { { { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } },
						{ { 10 }, { 10 }, { 10 } }, { { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } },
						{ { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } } });
		type.put("defaultRDef",
				new int[][][] { { { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } },
						{ { 10 }, { 10 }, { 10 } }, { { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } },
						{ { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } } });
		type.put("resourceTrack", new int[] { 0, 0, 0, 0, 0, 0, 0, 0 });
		type.put("numSup", 4);
		format.put("type3", type);

		// insert data for type4
		type = new TreeMap<>();
		type.put("tstatus", "unlocked");
		type.put("typename", "cfg388-multi-carrier-f15m-f15m-f15m-10m-5m-config3");
		type.put("numcells", 0);
		type.put("resourceDef", new int[][][] { { { 15 }, { 0 }, { 0 } }, { { 15 }, { 0 }, { 0 } },
				{ { 15 }, { 0 }, { 0 } }, { { 10 }, { 10 }, { 10 } }, { { 5 }, { 5 }, { 5 } } });
		type.put("defaultRDef", new int[][][] { { { 15 }, { 0 }, { 0 } }, { { 15 }, { 0 }, { 0 } },
				{ { 15 }, { 0 }, { 0 } }, { { 10 }, { 10 }, { 10 } }, { { 5 }, { 5 }, { 5 } } });
		type.put("resourceTrack", new int[] { 0, 0, 0, 0, 0 });
		type.put("numSup", 4);
		format.put("type4", type);

		// insert data for type5
		type = new TreeMap<>();
		type.put("tstatus", "unlocked");
		type.put("typename", "cfg358-multi-carrier-20m-20m-20m-10m-5m-config3");
		type.put("numcells", 0);
		type.put("resourceDef", new int[][][] { { { 20 }, { 0 }, { 15, 20 } }, { { 20 }, { 0 }, { 15, 20 } },
				{ { 20 }, { 0 }, { 15, 20 } }, { { 10 }, { 10 }, { 10 } }, { { 5 }, { 5 }, { 5 } } });
		type.put("defaultRDef", new int[][][] { { { 20 }, { 0 }, { 15, 20 } }, { { 20 }, { 0 }, { 15, 20 } },
				{ { 20 }, { 0 }, { 15, 20 } }, { { 10 }, { 10 }, { 10 } }, { { 5 }, { 5 }, { 5 } } });
		type.put("resourceTrack", new int[] { 0, 0, 0, 0, 0 });
		type.put("numSup", 4);
		format.put("type5", type);

		// insert data for type6
		type = new TreeMap<>();
		type.put("tstatus", "unlocked");
		type.put("typename", "cfg388-multi-carrier-20m-20m-20m-20m-config3");
		type.put("numcells", 0);
		type.put("resourceDef", new int[][][] { { { 20 }, { 0 }, { 15, 20 } }, { { 20 }, { 0 }, { 15, 20 } },
				{ { 20 }, { 0 }, { 15, 20 } }, { { 20 }, { 0 }, { 15, 20 } } });
		type.put("defaultRDef", new int[][][] { { { 20 }, { 0 }, { 15, 20 } }, { { 20 }, { 0 }, { 15, 20 } },
				{ { 20 }, { 0 }, { 15, 20 } }, { { 20 }, { 0 }, { 15, 20 } } });
		type.put("resourceTrack", new int[] { 0, 0, 0, 0 });
		type.put("numSup", 4);
		format.put("type6", type);

		// insert data for type7
		type = new TreeMap<>();
		type.put("tstatus", "unlocked");
		type.put("typename", "cfg63-multi-carrier-20m-2t2r-6cell");
		type.put("numcells", 0);
		type.put("resourceDef",
				new int[][][] { { { 20 }, { 0 }, { 20 } }, { { 20 }, { 0 }, { 20 } }, { { 20 }, { 0 }, { 20 } },
						{ { 20 }, { 0 }, { 20 } }, { { 20 }, { 0 }, { 20 } }, { { 20 }, { 0 }, { 20 } } });
		type.put("defaultRDef",
				new int[][][] { { { 20 }, { 0 }, { 20 } }, { { 20 }, { 0 }, { 20 } }, { { 20 }, { 0 }, { 20 } },
						{ { 20 }, { 0 }, { 20 } }, { { 20 }, { 0 }, { 20 } }, { { 20 }, { 0 }, { 20 } } });
		type.put("resourceTrack", new int[] { 0, 0, 0, 0, 0, 0 });
		type.put("numSup", 6);
		format.put("type7", type);

		delay0.put("format0", format);

		// format1 details
		format = new TreeMap<>();

		// input for carrierLimits
		carrierLimits = new TreeMap<>();
		carrierLimits.put("num_supPerCar", new int[][] { { 13 }, { 3 } });
		carrierLimits.put("numcellsPerCar", new int[][] { { 13 }, { 0 } });
		format.put("carrierLimits", carrierLimits);

		// insert data for type1
		type = new TreeMap<>();
		type.put("tstatus", "unlocked");
		type.put("typename", "cfg61-multi-carrier-10m-5m-6cell");
		type.put("numcells", 0);
		type.put("resourceDef",
				new int[][][] { { { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } },
						{ { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } },
						{ { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } },
						{ { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } } });
		type.put("defaultRDef",
				new int[][][] { { { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } },
						{ { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } },
						{ { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } },
						{ { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } } });
		type.put("resourceTrack", new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
		type.put("numSup", 6);
		format.put("type1", type);

		// insert data for type2
		type = new TreeMap<>();
		type.put("tstatus", "unlocked");
		type.put("typename", "cfg61-multi-carrier-10m-5m-3cell-20m-2t2r-3cell");
		type.put("numcells", 0);
		type.put("resourceDef",
				new int[][][] { { { 5, 10 }, { 5, 10 }, { 5, 10 } }, { { 5, 10 }, { 5, 10 }, { 5, 10 } },
						{ { 5, 10 }, { 5, 10 }, { 5, 10 } }, { { 20 }, { 0 }, { 20 } }, { { 20 }, { 0 }, { 20 } },
						{ { 20 }, { 0 }, { 20 } } });
		type.put("defaultRDef",
				new int[][][] { { { 5, 10 }, { 5, 10 }, { 5, 10 } }, { { 5, 10 }, { 5, 10 }, { 5, 10 } },
						{ { 5, 10 }, { 5, 10 }, { 5, 10 } }, { { 20 }, { 0 }, { 20 } }, { { 20 }, { 0 }, { 20 } },
						{ { 20 }, { 0 }, { 20 } } });
		type.put("resourceTrack", new int[] { 0, 0, 0, 0, 0, 0 });
		type.put("numSup", 6);
		format.put("type2", type);

		// insert data for type3
		type = new TreeMap<>();
		type.put("tstatus", "unlocked");
		type.put("typename", "cfg355-multi-carrier-10m-10m-10m-10m-5m-5m-5m-5m-config3");
		type.put("numcells", 0);
		type.put("resourceDef",
				new int[][][] { { { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } },
						{ { 10 }, { 10 }, { 10 } }, { { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } },
						{ { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } } });
		type.put("defaultRDef",
				new int[][][] { { { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } },
						{ { 10 }, { 10 }, { 10 } }, { { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } },
						{ { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } } });
		type.put("resourceTrack", new int[] { 0, 0, 0, 0, 0, 0, 0, 0 });
		type.put("numSup", 4);
		format.put("type3", type);

		// insert data for type4
		type = new TreeMap<>();
		type.put("tstatus", "unlocked");
		type.put("typename", "cfg388-multi-carrier-f15m-f15m-f15m-10m-5m-config3");
		type.put("numcells", 0);
		type.put("resourceDef", new int[][][] { { { 15 }, { 0 }, { 0 } }, { { 15 }, { 0 }, { 0 } },
				{ { 15 }, { 0 }, { 0 } }, { { 10 }, { 10 }, { 10 } }, { { 5 }, { 5 }, { 5 } } });
		type.put("defaultRDef", new int[][][] { { { 15 }, { 0 }, { 0 } }, { { 15 }, { 0 }, { 0 } },
				{ { 15 }, { 0 }, { 0 } }, { { 10 }, { 10 }, { 10 } }, { { 5 }, { 5 }, { 5 } } });
		type.put("resourceTrack", new int[] { 0, 0, 0, 0, 0 });
		type.put("numSup", 4);
		format.put("type4", type);

		// insert data for type5
		type = new TreeMap<>();
		type.put("tstatus", "unlocked");
		type.put("typename", "cfg358-multi-carrier-20m-20m-20m-10m-5m-config3");
		type.put("numcells", 0);
		type.put("resourceDef", new int[][][] { { { 20 }, { 0 }, { 15, 20 } }, { { 20 }, { 0 }, { 15, 20 } },
				{ { 20 }, { 0 }, { 15, 20 } }, { { 10 }, { 10 }, { 10 } }, { { 5 }, { 5 }, { 5 } } });
		type.put("defaultRDef", new int[][][] { { { 20 }, { 0 }, { 15, 20 } }, { { 20 }, { 0 }, { 15, 20 } },
				{ { 20 }, { 0 }, { 15, 20 } }, { { 10 }, { 10 }, { 10 } }, { { 5 }, { 5 }, { 5 } } });
		type.put("resourceTrack", new int[] { 0, 0, 0, 0 });
		type.put("numSup", 4);
		format.put("type5", type);

		// insert data for type6
		type = new TreeMap<>();
		type.put("tstatus", "unlocked");
		type.put("typename", "cfg388-multi-carrier-20m-20m-20m-20m-config3");
		type.put("numcells", 0);
		type.put("resourceDef", new int[][][] { { { 20 }, { 0 }, { 15, 20 } }, { { 20 }, { 0 }, { 15, 20 } },
				{ { 20 }, { 0 }, { 15, 20 } }, { { 20 }, { 0 }, { 15, 20 } } });
		type.put("defaultRDef", new int[][][] { { { 20 }, { 0 }, { 15, 20 } }, { { 20 }, { 0 }, { 15, 20 } },
				{ { 20 }, { 0 }, { 15, 20 } }, { { 20 }, { 0 }, { 15, 20 } } });
		type.put("resourceTrack", new int[] { 0, 0, 0, 0 });
		type.put("numSup", 4);
		format.put("type6", type);

		// insert data for type7
		type = new TreeMap<>();
		type.put("tstatus", "unlocked");
		type.put("typename", "cfg63-multi-carrier-20m-2t2r-6cell");
		type.put("numcells", 0);
		type.put("resourceDef",
				new int[][][] { { { 20 }, { 0 }, { 20 } }, { { 20 }, { 0 }, { 20 } }, { { 20 }, { 0 }, { 20 } },
						{ { 20 }, { 0 }, { 20 } }, { { 20 }, { 0 }, { 20 } }, { { 20 }, { 0 }, { 20 } } });
		type.put("defaultRDef",
				new int[][][] { { { 20 }, { 0 }, { 20 } }, { { 20 }, { 0 }, { 20 } }, { { 20 }, { 0 }, { 20 } },
						{ { 20 }, { 0 }, { 20 } }, { { 20 }, { 0 }, { 20 } }, { { 20 }, { 0 }, { 20 } } });
		type.put("resourceTrack", new int[] { 0, 0, 0, 0, 0, 0 });
		type.put("numSup", 6);
		format.put("type7", type);

		delay0.put("format1", format);

	}

	private static void populate_delay1(TreeMap<String, Object> delay1) {
		// format0 details
		TreeMap<String, Object> format = new TreeMap<>();

		// carrierLimits
		TreeMap<String, Object> carrierLimits = new TreeMap<>();

		// input for carrierLimits
		carrierLimits.put("num_supPerCar", new int[][] { { 13 }, { 3 } });
		carrierLimits.put("numcellsPerCar", new int[][] { { 13 }, { 0 } });
		format.put("carrierLimits", carrierLimits);

		TreeMap<String, Object> type = new TreeMap<>();

		// insert data for type 1
		type.put("tstatus", "unlocked");
		type.put("typename", "cfg61-multi-carrier-10m-5m-6cell");
		type.put("numcells", 0);
		type.put("resourceDef",
				new int[][][] { { { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } },
						{ { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } },
						{ { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } },
						{ { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } } });
		type.put("defaultRDef",
				new int[][][] { { { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } },
						{ { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } },
						{ { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } },
						{ { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } } });
		type.put("resourceTrack", new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
		type.put("numSup", 6);
		format.put("type1", type);

		// insert data for type2
		type = new TreeMap<>();
		type.put("tstatus", "unlocked");
		type.put("typename", "cfg61-multi-carrier-10m-5m-3cell-20m-2t2r-3cell");
		type.put("numcells", 0);
		type.put("resourceDef",
				new int[][][] { { { 5, 10 }, { 5, 10 }, { 5, 10 } }, { { 5, 10 }, { 5, 10 }, { 5, 10 } },
						{ { 5, 10 }, { 5, 10 }, { 5, 10 } }, { { 20 }, { 0 }, { 20 } }, { { 20 }, { 0 }, { 20 } },
						{ { 20 }, { 0 }, { 20 } } });
		type.put("defaultRDef",
				new int[][][] { { { 5, 10 }, { 5, 10 }, { 5, 10 } }, { { 5, 10 }, { 5, 10 }, { 5, 10 } },
						{ { 5, 10 }, { 5, 10 }, { 5, 10 } }, { { 20 }, { 0 }, { 20 } }, { { 20 }, { 0 }, { 20 } },
						{ { 20 }, { 0 }, { 20 } } });
		type.put("resourceTrack", new int[] { 0, 0, 0, 0, 0, 0 });
		type.put("numSup", 6);
		format.put("type2", type);

		// insert data for type3
		type = new TreeMap<>();
		type.put("tstatus", "unlocked");
		type.put("typename", "cfg355-multi-carrier-10m-10m-10m-10m-5m-5m-5m-5m-config3");
		type.put("numcells", 0);
		type.put("resourceDef",
				new int[][][] { { { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } },
						{ { 10 }, { 10 }, { 10 } }, { { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } },
						{ { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } } });
		type.put("defaultRDef",
				new int[][][] { { { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } },
						{ { 10 }, { 10 }, { 10 } }, { { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } },
						{ { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } } });
		type.put("resourceTrack", new int[] { 0, 0, 0, 0, 0, 0, 0, 0 });
		type.put("numSup", 4);
		format.put("type3", type);

		// insert data for type4
		type = new TreeMap<>();
		type.put("tstatus", "unlocked");
		type.put("typename", "cfg388-multi-carrier-f15m-f15m-f15m-10m-5m-config3");
		type.put("numcells", 0);
		type.put("resourceDef", new int[][][] { { { 15 }, { 0 }, { 0 } }, { { 15 }, { 0 }, { 0 } },
				{ { 15 }, { 0 }, { 0 } }, { { 10 }, { 10 }, { 10 } }, { { 5 }, { 5 }, { 5 } } });
		type.put("defaultRDef", new int[][][] { { { 15 }, { 0 }, { 0 } }, { { 15 }, { 0 }, { 0 } },
				{ { 15 }, { 0 }, { 0 } }, { { 10 }, { 10 }, { 10 } }, { { 5 }, { 5 }, { 5 } } });
		type.put("resourceTrack", new int[] { 0, 0, 0, 0, 0 });
		type.put("numSup", 4);
		format.put("type4", type);

		// insert data for type5
		type = new TreeMap<>();
		type.put("tstatus", "unlocked");
		type.put("typename", "cfg358-multi-carrier-20m-20m-20m-10m-5m-config3");
		type.put("numcells", 0);
		type.put("resourceDef", new int[][][] { { { 20 }, { 0 }, { 15, 20 } }, { { 20 }, { 0 }, { 15, 20 } },
				{ { 20 }, { 0 }, { 15, 20 } }, { { 10 }, { 10 }, { 10 } }, { { 5 }, { 5 }, { 5 } } });
		type.put("defaultRDef", new int[][][] { { { 20 }, { 0 }, { 15, 20 } }, { { 20 }, { 0 }, { 15, 20 } },
				{ { 20 }, { 0 }, { 15, 20 } }, { { 10 }, { 10 }, { 10 } }, { { 5 }, { 5 }, { 5 } } });
		type.put("resourceTrack", new int[] { 0, 0, 0, 0 });
		type.put("numSup", 4);
		format.put("type5", type);

		// insert data for type6
		type = new TreeMap<>();
		type.put("tstatus", "unlocked");
		type.put("typename", "cfg388-multi-carrier-20m-20m-20m-20m-config3");
		type.put("numcells", 0);
		type.put("resourceDef", new int[][][] { { { 20 }, { 0 }, { 15, 20 } }, { { 20 }, { 0 }, { 15, 20 } },
				{ { 20 }, { 0 }, { 15, 20 } }, { { 20 }, { 0 }, { 15, 20 } } });
		type.put("defaultRDef", new int[][][] { { { 20 }, { 0 }, { 15, 20 } }, { { 20 }, { 0 }, { 15, 20 } },
				{ { 20 }, { 0 }, { 15, 20 } }, { { 20 }, { 0 }, { 15, 20 } } });
		type.put("resourceTrack", new int[] { 0, 0, 0, 0 });
		type.put("numSup", 4);
		format.put("type6", type);

		// insert data for type7
		type = new TreeMap<>();
		type.put("tstatus", "unlocked");
		type.put("typename", "cfg63-multi-carrier-20m-2t2r-6cell");
		type.put("numcells", 0);
		type.put("resourceDef",
				new int[][][] { { { 20 }, { 0 }, { 20 } }, { { 20 }, { 0 }, { 20 } }, { { 20 }, { 0 }, { 20 } },
						{ { 20 }, { 0 }, { 20 } }, { { 20 }, { 0 }, { 20 } }, { { 20 }, { 0 }, { 20 } } });
		type.put("defaultRDef",
				new int[][][] { { { 20 }, { 0 }, { 20 } }, { { 20 }, { 0 }, { 20 } }, { { 20 }, { 0 }, { 20 } },
						{ { 20 }, { 0 }, { 20 } }, { { 20 }, { 0 }, { 20 } }, { { 20 }, { 0 }, { 20 } } });
		type.put("resourceTrack", new int[] { 0, 0, 0, 0, 0, 0 });
		type.put("numSup", 6);
		format.put("type7", type);

		delay1.put("format0", format);

		// format 1 details
		format = new TreeMap<>();

		// input for carrierLimits
		carrierLimits = new TreeMap<>();
		carrierLimits.put("num_supPerCar", new int[][] { { 13 }, { 3 } });
		carrierLimits.put("numcellsPerCar", new int[][] { { 13 }, { 0 } });
		format.put("carrierLimits", carrierLimits);

		// insert data for type1
		type = new TreeMap<>();
		type.put("tstatus", "unlocked");
		type.put("typename", "cfg61-multi-carrier-10m-5m-6cell");
		type.put("numcells", 0);
		type.put("resourceDef",
				new int[][][] { { { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } },
						{ { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } },
						{ { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } },
						{ { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } } });
		type.put("defaultRDef",
				new int[][][] { { { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } },
						{ { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } },
						{ { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } },
						{ { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } } });
		type.put("resourceTrack", new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
		type.put("numSup", 6);
		format.put("type1", type);

		// insert data for type2
		type = new TreeMap<>();
		type.put("tstatus", "unlocked");
		type.put("typename", "cfg61-multi-carrier-10m-5m-3cell-20m-2t2r-3cell");
		type.put("numcells", 0);
		type.put("resourceDef",
				new int[][][] { { { 5, 10 }, { 5, 10 }, { 5, 10 } }, { { 5, 10 }, { 5, 10 }, { 5, 10 } },
						{ { 5, 10 }, { 5, 10 }, { 5, 10 } }, { { 20 }, { 0 }, { 20 } }, { { 20 }, { 0 }, { 20 } },
						{ { 20 }, { 0 }, { 20 } } });
		type.put("defaultRDef",
				new int[][][] { { { 5, 10 }, { 5, 10 }, { 5, 10 } }, { { 5, 10 }, { 5, 10 }, { 5, 10 } },
						{ { 5, 10 }, { 5, 10 }, { 5, 10 } }, { { 20 }, { 0 }, { 20 } }, { { 20 }, { 0 }, { 20 } },
						{ { 20 }, { 0 }, { 20 } } });
		type.put("resourceTrack", new int[] { 0, 0, 0, 0, 0, 0 });
		type.put("numSup", 6);
		format.put("type2", type);

		// insert data for type3
		type = new TreeMap<>();
		type.put("tstatus", "unlocked");
		type.put("typename", "cfg355-multi-carrier-10m-10m-10m-10m-5m-5m-5m-5m-config3");
		type.put("numcells", 0);
		type.put("resourceDef",
				new int[][][] { { { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } },
						{ { 10 }, { 10 }, { 10 } }, { { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } },
						{ { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } } });
		type.put("defaultRDef",
				new int[][][] { { { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } }, { { 10 }, { 10 }, { 10 } },
						{ { 10 }, { 10 }, { 10 } }, { { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } },
						{ { 5 }, { 5 }, { 5 } }, { { 5 }, { 5 }, { 5 } } });
		type.put("resourceTrack", new int[] { 0, 0, 0, 0, 0, 0, 0, 0 });
		type.put("numSup", 4);
		format.put("type3", type);

		// insert data for type4
		type = new TreeMap<>();
		type.put("tstatus", "unlocked");
		type.put("typename", "cfg388-multi-carrier-f15m-f15m-f15m-10m-5m-config3");
		type.put("numcells", 0);
		type.put("resourceDef", new int[][][] { { { 15 }, { 0 }, { 0 } }, { { 15 }, { 0 }, { 0 } },
				{ { 15 }, { 0 }, { 0 } }, { { 10 }, { 10 }, { 10 } }, { { 5 }, { 5 }, { 5 } } });
		type.put("defaultRDef", new int[][][] { { { 15 }, { 0 }, { 0 } }, { { 15 }, { 0 }, { 0 } },
				{ { 15 }, { 0 }, { 0 } }, { { 10 }, { 10 }, { 10 } }, { { 5 }, { 5 }, { 5 } } });
		type.put("resourceTrack", new int[] { 0, 0, 0, 0, 0 });
		type.put("numSup", 4);
		format.put("type4", type);

		// insert data for type5
		type = new TreeMap<>();
		type.put("tstatus", "unlocked");
		type.put("typename", "cfg358-multi-carrier-20m-20m-20m-10m-5m-config3");
		type.put("numcells", 0);
		type.put("resourceDef", new int[][][] { { { 20 }, { 0 }, { 15, 20 } }, { { 20 }, { 0 }, { 15, 20 } },
				{ { 20 }, { 0 }, { 15, 20 } }, { { 10 }, { 10 }, { 10 } }, { { 5 }, { 5 }, { 5 } } });
		type.put("defaultRDef", new int[][][] { { { 20 }, { 0 }, { 15, 20 } }, { { 20 }, { 0 }, { 15, 20 } },
				{ { 20 }, { 0 }, { 15, 20 } }, { { 10 }, { 10 }, { 10 } }, { { 5 }, { 5 }, { 5 } } });
		type.put("resourceTrack", new int[] { 0, 0, 0, 0 });
		type.put("numSup", 4);
		format.put("type5", type);

		// insert data for type6
		type = new TreeMap<>();
		type.put("tstatus", "unlocked");
		type.put("typename", "cfg388-multi-carrier-20m-20m-20m-20m-config3");
		type.put("numcells", 0);
		type.put("resourceDef", new int[][][] { { { 20 }, { 0 }, { 15, 20 } }, { { 20 }, { 0 }, { 15, 20 } },
				{ { 20 }, { 0 }, { 15, 20 } }, { { 20 }, { 0 }, { 15, 20 } } });
		type.put("defaultRDef", new int[][][] { { { 20 }, { 0 }, { 15, 20 } }, { { 20 }, { 0 }, { 15, 20 } },
				{ { 20 }, { 0 }, { 15, 20 } }, { { 20 }, { 0 }, { 15, 20 } } });
		type.put("resourceTrack", new int[] { 0, 0, 0, 0 });
		type.put("numSup", 4);
		format.put("type6", type);

		// insert data for type7
		type = new TreeMap<>();
		type.put("tstatus", "unlocked");
		type.put("typename", "cfg63-multi-carrier-20m-2t2r-6cell");
		type.put("numcells", 0);
		type.put("resourceDef",
				new int[][][] { { { 20 }, { 0 }, { 20 } }, { { 20 }, { 0 }, { 20 } }, { { 20 }, { 0 }, { 20 } },
						{ { 20 }, { 0 }, { 20 } }, { { 20 }, { 0 }, { 20 } }, { { 20 }, { 0 }, { 20 } } });
		type.put("defaultRDef",
				new int[][][] { { { 20 }, { 0 }, { 20 } }, { { 20 }, { 0 }, { 20 } }, { { 20 }, { 0 }, { 20 } },
						{ { 20 }, { 0 }, { 20 } }, { { 20 }, { 0 }, { 20 } }, { { 20 }, { 0 }, { 20 } } });
		type.put("resourceTrack", new int[] { 0, 0, 0, 0, 0, 0 });
		type.put("numSup", 6);
		format.put("type7", type);

		delay1.put("format1", format);
	}

	private static void print_supplemental(String s) {
		if (printall || printsup) {
			dlog(s + "\n");
		}
	}

	@SuppressWarnings("unchecked")
	private  void populate_DataAs(ArrayList<TreeMap<String, Object>> cellDataAref,
			ArrayList<TreeMap<String, Object>> rrhDataAref, ArrayList<TreeMap<String, Object>> rrhDataBref,
			ArrayList<TreeMap<String, Object>> rrhDataCref) {
		Scanner sciqr = null;
		try {
			File sciqf = new File(sciq);
			sciqr = new Scanner(sciqf);
			while (sciqr.hasNextLine()) {
				String[] lineA = sciqr.nextLine().split(",");
				logger.error(enbidA + " **enbidA Populate Data As enbidx** " + enbidx);
				logger.error("lineA** " + Arrays.toString(lineA));

				if (enbidA.size() > enbidx
						&& enbidA.get(enbidx).toString().equals(lineA[find_necessaryA_idx("enbid")])) {
					TreeMap<String, Object> cellDataH = new TreeMap<>();
					TreeMap<String, Object> rrhDataHref = new TreeMap<>();
					TreeMap<String, Object> rrhDataHrefB = new TreeMap<>();
					ArrayList<String> cellA = new ArrayList<>();
					rrhDataHref.put("cellA", cellA);
					rrhDataHrefB.put("cellA", cellA);
					ArrayList<String> fccId = new ArrayList<>();
					rrhDataHref.put("fccId", fccId);
					rrhDataHrefB.put("fccId", fccId);
					ArrayList<String> antennaGains = new ArrayList<>();
					rrhDataHref.put("antennaGains", antennaGains);
					rrhDataHrefB.put("antennaGains", antennaGains);
					ArrayList<String> bandA = new ArrayList<>();
					rrhDataHref.put("bandA", bandA);
					rrhDataHrefB.put("bandA", bandA);

					int nIndex = find_necessaryA_idx("enbname");
					int nidx = find_index_necssaryA("enbname");
					;
					if (nIndex != -1) {
						vet_to_continue(nidx, lineA[nIndex]);
						cellDataH.put("enbname", lineA[nIndex]);
					} else {
						vet_to_continue(nidx, necessaryA.get(nidx).get("default"));
						cellDataH.put("enbname", necessaryA.get(nidx).get("default"));
					}

					nIndex = find_necessaryA_idx("cellid");
					nidx = find_index_necssaryA("cellid");
					if (nIndex != -1) {
						vet_to_continue(nidx, lineA[nIndex]);
						cellDataH.put("cellid", lineA[nIndex]);
						write_sect_carr_cellDataH(Integer.parseInt(cellDataH.get("cellid").toString()), cellDataH);
					} else {
						vet_to_continue(nidx, necessaryA.get(nidx).get("default"));
						cellDataH.put("cellid", necessaryA.get(nidx).get("default"));
					}

					nIndex = find_necessaryA_idx("tac");
					nidx = find_index_necssaryA("tac");
					if (nIndex != -1) {
						vet_to_continue(nidx, lineA[nIndex]);
						cellDataH.put("tac", lineA[nIndex]);
					} else {
						vet_to_continue(nidx, necessaryA.get(nidx).get("default"));
						cellDataH.put("tac", necessaryA.get(nidx).get("default"));
					}

					nIndex = find_necessaryA_idx("AdditionalBoardID");
					nidx = find_index_necssaryA("AdditionalBoardID");
					if (nIndex != -1) {
						vet_to_continue(nidx, lineA[nIndex]);
						cellDataH.put("AdditionalBoardID", lineA[nIndex]);
					} else {
						vet_to_continue(nidx, necessaryA.get(nidx).get("default"));
						cellDataH.put("AdditionalBoardID", necessaryA.get(nidx).get("default"));
					}

					nIndex = find_necessaryA_idx("AntennaGain");
					nidx = find_index_necssaryA("AntennaGain");
					if (nIndex != -1) {
						vet_to_continue(nidx, lineA[nIndex]);
						cellDataH.put("AntennaGain", lineA[nIndex]);
					} else {
						vet_to_continue(nidx, necessaryA.get(nidx).get("default"));
						cellDataH.put("AntennaGain", necessaryA.get(nidx).get("default"));
					}

					nIndex = find_necessaryA_idx("market");
					nidx = find_index_necssaryA("market");
					if (nIndex != -1) {
						vet_to_continue(nidx, lineA[nIndex]);
						cellDataH.put("market", lineA[nIndex]);
					} else {
						vet_to_continue(nidx, necessaryA.get(nidx).get("default"));
						cellDataH.put("market", necessaryA.get(nidx).get("default"));
					}

					nIndex = find_necessaryA_idx("marketName");
					nidx = find_index_necssaryA("marketName");
					if (nIndex != -1) {
						vet_to_continue(nidx, lineA[nIndex]);
						cellDataH.put("marketName", lineA[nIndex]);
					} else {
						vet_to_continue(nidx, necessaryA.get(nidx).get("default"));
						cellDataH.put("marketName", necessaryA.get(nidx).get("default"));
					}

					nIndex = find_necessaryA_idx("cbrcfcc");
					nidx = find_index_necssaryA("cbrcfcc");
					if (nIndex != -1) {
						vet_to_continue(nidx, lineA[nIndex]);
						cellDataH.put("cbrcfcc", lineA[nIndex]);
						rrhDataHref.put("cbrcfcc", lineA[nIndex]);
						rrhDataHrefB.put("cbrcfcc", lineA[nIndex]);
					} else {
						vet_to_continue(nidx, necessaryA.get(nidx).get("default"));
						cellDataH.put("cbrcfcc", necessaryA.get(nidx).get("default"));
						rrhDataHref.put("cbrcfcc", necessaryA.get(nidx).get("default"));
						rrhDataHrefB.put("cbrcfcc", lineA[nIndex]);
					}

					nIndex = find_necessaryA_idx("cprivalue");
					nidx = find_index_necssaryA("cprivalue");
					if (nIndex != -1) {
						vet_to_continue(nidx, lineA[nIndex]);
						cellDataH.put("cprivalue", lineA[nIndex]);
					} else {
						vet_to_continue(nidx, necessaryA.get(nidx).get("default"));
						cellDataH.put("cprivalue", necessaryA.get(nidx).get("default"));
					}

					if (version.equals("21.B.0") || version.equals("21.C.0") || version.equals("21.D.0")
							|| version.equals("22.A.0")  || version.equals("22.C.0")) {
						nIndex = find_necessaryA_idx("delaydl");
						nidx = find_index_necssaryA("delaydl");
						if (nIndex != -1) {
							vet_to_continue(nidx, lineA[nIndex]);
							cellDataH.put("delaydl", lineA[nIndex]);
						} else {
							vet_to_continue(nidx, necessaryA.get(nidx).get("default"));
							cellDataH.put("delaydl", necessaryA.get(nidx).get("default"));
						}
						nIndex = find_necessaryA_idx("delayul");
						nidx = find_index_necssaryA("delayul");
						if (nIndex != -1) {
							vet_to_continue(nidx, lineA[nIndex]);
							cellDataH.put("delayul", lineA[nIndex]);
						} else {
							vet_to_continue(nidx, necessaryA.get(nidx).get("default"));
							cellDataH.put("delayul", necessaryA.get(nidx).get("default"));
						}
					}
					// added

					nIndex = find_necessaryA_idx("cpriPortAssignment");
					nidx = find_index_necssaryA("cpriPortAssignment");
					if (nIndex != -1) {
						vet_to_continue(nidx, lineA[nIndex]);
						cellDataH.put("cpriPortAssignment", lineA[nIndex]);
					} else {
						vet_to_continue(nidx, necessaryA.get(nidx).get("default"));
						cellDataH.put("cpriPortAssignment", necessaryA.get(nidx).get("default"));
					}

					nIndex = find_necessaryA_idx("additional_port");
					nidx = find_index_necssaryA("additional_port");
					if (nIndex != -1) {
						vet_to_continue(nidx, lineA[nIndex]);
						cellDataH.put("additional_port", lineA[nIndex]);
					} else {
						vet_to_continue(nidx, necessaryA.get(nidx).get("default"));
						cellDataH.put("additional_port", necessaryA.get(nidx).get("default"));
					}

					nIndex = find_necessaryA_idx("thMaxEirp");
					nidx = find_index_necssaryA("thMaxEirp");
					if (nIndex != -1) {
						vet_to_continue(nidx, lineA[nIndex]);
						cellDataH.put("thMaxEirp", lineA[nIndex]);
					} else {
						vet_to_continue(nidx, necessaryA.get(nidx).get("default"));
						cellDataH.put("thMaxEirp", necessaryA.get(nidx).get("default"));
					}

					nIndex = find_necessaryA_idx("PreferredEarfcn");
					nidx = find_index_necssaryA("PreferredEarfcn");
					if (nIndex != -1) {
						vet_to_continue(nidx, lineA[nIndex]);
						cellDataH.put("PreferredEarfcn", lineA[nIndex]);
					} else {
						vet_to_continue(nidx, necessaryA.get(nidx).get("default"));
						cellDataH.put("PreferredEarfcn", necessaryA.get(nidx).get("default"));
					}

					nIndex = find_necessaryA_idx("DspCellIndex");
					nidx = find_index_necssaryA("DspCellIndex");
					if (nIndex != -1) {
						vet_to_continue(nidx, lineA[nIndex]);
						cellDataH.put("DspCellIndex", lineA[nIndex]);
					} else {
						vet_to_continue(nidx, necessaryA.get(nidx).get("default"));
						cellDataH.put("DspCellIndex", necessaryA.get(nidx).get("default"));
					}

					nIndex = find_necessaryA_idx("DspID");
					nidx = find_index_necssaryA("DspID");
					if (nIndex != -1) {
						vet_to_continue(nidx, lineA[nIndex]);
						cellDataH.put("DspID", lineA[nIndex]);
					} else {
						vet_to_continue(nidx, necessaryA.get(nidx).get("default"));
						cellDataH.put("DspID", necessaryA.get(nidx).get("default"));
					}

					nIndex = find_necessaryA_idx("mct");
					nidx = find_index_necssaryA("mct");
					if (nIndex != -1) {
						vet_to_continue(nidx, lineA[nIndex]);
						cellDataH.put("mct", lineA[nIndex]);
					} else {
						vet_to_continue(nidx, necessaryA.get(nidx).get("default"));
						cellDataH.put("mct", necessaryA.get(nidx).get("default"));
					}

					nIndex = find_necessaryA_idx("zczc");
					nidx = find_index_necssaryA("zczc");
					if (nIndex != -1) {
						vet_to_continue(nidx, lineA[nIndex]);
						cellDataH.put("zczc", lineA[nIndex]);
					} else {
						vet_to_continue(nidx, necessaryA.get(nidx).get("default"));
						cellDataH.put("zczc", necessaryA.get(nidx).get("default"));
					}

					nIndex = find_necessaryA_idx("pracformat");
					nidx = find_index_necssaryA("pracformat");
					if (nIndex != -1) {
						vet_to_continue(nidx, lineA[nIndex]);
						cellDataH.put("pracformat", lineA[nIndex]);
					} else {
						vet_to_continue(nidx, necessaryA.get(nidx).get("default"));
						cellDataH.put("pracformat", necessaryA.get(nidx).get("default"));
					}
					// administrative state
					nIndex = find_necessaryA_idx("adstate");
					nidx = find_index_necssaryA("adstate");
					if (nIndex != -1) {
						vet_to_continue(nidx, lineA[nIndex]);
						cellDataH.put("adstate", lineA[nIndex]);
					} else {
						vet_to_continue(nidx, necessaryA.get(nidx).get("default"));
						cellDataH.put("adstate", necessaryA.get(nidx).get("default"));
					}
					// 22A start
					populateData22V(lineA, cellDataH);
					// 22A done

					// fdd mmu
					nIndex = find_necessaryA_idx("mmuBisectorMode");
					nidx = find_index_necssaryA("mmuBisectorMode");
					if (nIndex != -1) {
						vet_to_continue(nidx, lineA[nIndex]);
						cellDataH.put("mmuBisectorMode", lineA[nIndex]);
					} else {
						vet_to_continue(nidx, necessaryA.get(nidx).get("default"));
						cellDataH.put("mmuBisectorMode", necessaryA.get(nidx).get("default"));
					}

					// nbiot
					nIndex = find_necessaryA_idx("nbiot");
					nidx = find_index_necssaryA("nbiot");
					if (nIndex != -1) {
						vet_to_continue(nidx, lineA[nIndex]);
						cellDataH.put("nbiot", lineA[nIndex]);
					} else {
						vet_to_continue(nidx, necessaryA.get(nidx).get("default"));
						cellDataH.put("nbiot", necessaryA.get(nidx).get("default"));
					}

					nIndex = find_necessaryA_idx("RUPortID");
					nidx = find_index_necssaryA("RUPortID");
					if (nIndex != -1) {
						vet_to_continue(nidx, lineA[nIndex]);
						cellDataH.put("RUPortID", lineA[nIndex].replaceAll("\\D+", ""));
					} else {
						vet_to_continue(nidx, necessaryA.get(nidx).get("default"));
						cellDataH.put("RUPortID", necessaryA.get(nidx).get("default"));
					}

					nIndex = find_necessaryA_idx("pci");
					nidx = find_index_necssaryA("pci");
					if (nIndex != -1) {
						vet_to_continue(nidx, lineA[nIndex]);
						cellDataH.put("pci", lineA[nIndex]);
					} else {
						vet_to_continue(nidx, necessaryA.get(nidx).get("default"));
						cellDataH.put("pci", necessaryA.get(nidx).get("default"));
					}

					nIndex = find_necessaryA_idx("rach");
					nidx = find_index_necssaryA("rach");
					if (nIndex != -1) {
						vet_to_continue(nidx, lineA[nIndex]);
						cellDataH.put("rach", lineA[nIndex]);
					} else {
						vet_to_continue(nidx, necessaryA.get(nidx).get("default"));
						cellDataH.put("rach", necessaryA.get(nidx).get("default"));
					}

					nIndex = find_necessaryA_idx("band");
					nidx = find_index_necssaryA("band");
					if (nIndex != -1) {
						vet_to_continue(nidx, lineA[nIndex]);
						cellDataH.put("band", lineA[nIndex].replaceAll("Z", "z"));
					} else {
						vet_to_continue(nidx, necessaryA.get(nidx).get("default"));
						cellDataH.put("band", necessaryA.get(nidx).get("default"));
					}
					nIndex = find_necessaryA_idx("band");
					nidx = find_index_necssaryA("band");
					if (nIndex != -1) {
						vet_to_continue(nidx, lineA[nIndex]);
						cellDataH.put("bandpcs", lineA[nIndex].replaceAll("Z", "z"));
					} else {
						vet_to_continue(nidx, necessaryA.get(nidx).get("default"));
						cellDataH.put("bandpcs", necessaryA.get(nidx).get("default"));
					}
					nIndex = find_necessaryA_idx("rfPortOpertionMode");
					nidx = find_index_necssaryA("rfPortOpertionMode");
					if (nIndex != -1) {
						vet_to_continue(nidx, lineA[nIndex]);
						cellDataH.put("RFPORTOPERATIONMODE", lineA[nIndex]);
					} else {
						vet_to_continue(nidx, necessaryA.get(nidx).get("default"));
						cellDataH.put("RFPORTOPERATIONMODE", necessaryA.get(nidx).get("default"));
					}
					nIndex = find_necessaryA_idx("bandwidth");
					nidx = find_index_necssaryA("bandwidth");
					if (nIndex != -1) {
						vet_to_continue(nIndex, lineA[nIndex]);
						cellDataH.put("bandwidth", lineA[nIndex].replaceAll("\\D+", ""));
					} else {
						vet_to_continue(nidx, necessaryA.get(nidx).get("default"));
						cellDataH.put("bandwidth", necessaryA.get(nidx).get("default"));
					}

					nIndex = find_necessaryA_idx("earfcndl");
					nidx = find_index_necssaryA("earfcndl");
					if (nIndex != -1) {
						vet_to_continue(nidx, lineA[nIndex]);
						cellDataH.put("earfcndl", lineA[nIndex]);
					} else {
						vet_to_continue(nidx, necessaryA.get(nidx).get("default"));
						cellDataH.put("earfcndl", necessaryA.get(nidx).get("default"));
					}

					nIndex = find_necessaryA_idx("earfcnul");
					nidx = find_index_necssaryA("earfcnul");
					if (nIndex != -1) {
						vet_to_continue(nidx, lineA[nIndex]);
						cellDataH.put("earfcnul", lineA[nIndex]);
					} else {
						vet_to_continue(nidx, necessaryA.get(nidx).get("default"));
						cellDataH.put("earfcnul", necessaryA.get(nidx).get("default"));
					}

					nIndex = find_necessaryA_idx("opticdist");
					nidx = find_index_necssaryA("opticdist");
					if (nIndex != -1) {
						vet_to_continue(nidx, lineA[nIndex]);
						cellDataH.put("opticdist", lineA[nIndex]);
					} else {
						vet_to_continue(nidx, necessaryA.get(nidx).get("default"));
						cellDataH.put("opticdist", necessaryA.get(nidx).get("default"));
					}

					nIndex = find_necessaryA_idx("power");
					nidx = find_index_necssaryA("power");
					if (nIndex != -1) {
						vet_to_continue(nidx, lineA[nIndex]);
						Double tp = Double.parseDouble(lineA[nIndex]);
						tp = Math.round(tp * Math.pow(10, 1)) / Math.pow(10, 1);
						String sp = String.valueOf(tp);
						String gp = sp.replaceAll("\\.", "");
						Integer tps = Integer.parseInt(gp);
						if (tps < 100)
							tps *= 10;
						cellDataH.put("power", tps.toString());
					} else {
						vet_to_continue(nidx, necessaryA.get(nidx).get("default"));
						cellDataH.put("power", necessaryA.get(nidx).get("default"));
					}

					nIndex = find_necessaryA_idx("txd");
					nidx = find_index_necssaryA("txd");
					if (nIndex != -1) {
						vet_to_continue(nidx, lineA[nIndex]);
						cellDataH.put("txd", lineA[nIndex]);
					} else {
						vet_to_continue(nidx, necessaryA.get(nidx).get("default"));
						cellDataH.put("txd", necessaryA.get(nidx).get("default"));
					}

					nIndex = find_necessaryA_idx("rxd");
					nidx = find_index_necssaryA("rxd");
					if (nIndex != -1) {
						vet_to_continue(nidx, lineA[nIndex]);
						cellDataH.put("rxd", lineA[nIndex]);
					} else {
						vet_to_continue(nidx, necessaryA.get(nidx).get("default"));
						cellDataH.put("rxd", necessaryA.get(nidx).get("default"));
					}

					StringBuffer txstr, crsstr;
					StringBuffer rxstr;
					if (Integer.parseInt(cellDataH.get("txd").toString()) == 2) {
						txstr = new StringBuffer("2Tx");
						crsstr = new StringBuffer("n2");
					} else if (Integer.parseInt(cellDataH.get("txd").toString()) == 4) {
						txstr = new StringBuffer("2Tx");
						crsstr = new StringBuffer("n4");
					} else if (Integer.parseInt(cellDataH.get("txd").toString()) == 1) { // riu radios
						txstr = new StringBuffer("Tx");
						crsstr = new StringBuffer("n1");
					} else {
						txstr = new StringBuffer(cellDataH.get("txd").toString());
						txstr.append("Tx");
						crsstr = new StringBuffer(cellDataH.get("txd").toString());
						crsstr.append("CRS");
					}
					if (Integer.parseInt(cellDataH.get("rxd").toString()) < 2) {
						rxstr = new StringBuffer("2Rx");
					} else {
						rxstr = new StringBuffer(cellDataH.get("rxd").toString());
						rxstr.append("Rx");
					}
					cellDataH.put("diversity", txstr.toString() + rxstr.toString());
					cellDataH.put("crs", crsstr.toString());

					nIndex = find_necessaryA_idx("boardport");
					nidx = find_index_necssaryA("boardport");
					if (nIndex != -1) {
						vet_to_continue(nidx, lineA[nIndex]);
						write_cpri_to_cellDataH(lineA[nIndex], cellDataH);
					} else {
						vet_to_continue(nidx, necessaryA.get(nidx).get("default"));
						write_cpri_to_cellDataH(lineA[nIndex], cellDataH);
					}

					int rrhDexists = 0;
					if (version.equals("21.B.0") || version.equals("21.C.0") || version.equals("21.D.0")
							|| version.equals("22.A.0") || version.equals("22.C.0")) {

						for (int i = 0; i < rrhDataAref.size(); i++) {
							if (rrhDataAref.get(i).get("LCCnum").toString().equals(cellDataH.get("LCCnum").toString())
									&& rrhDataAref.get(i).get("port").toString()
											.equals(cellDataH.get("port").toString())
									&& !cellDataH.get("band").toString().contains("AWS")
									&& !cellDataH.get("band").toString().contains("PCS")) {
								rrhDataHref = rrhDataAref.get(i);
								rrhDexists = 1;
								break;
							}
						}
					} else {

						for (int i = 0; i < rrhDataAref.size(); i++) {
							if (rrhDataAref.get(i).get("LCCnum").toString().equals(cellDataH.get("LCCnum").toString())
									&& rrhDataAref.get(i).get("port").toString()
											.equals(cellDataH.get("port").toString())) {
								rrhDataHref = rrhDataAref.get(i);
								rrhDexists = 1;
								break;
							}
						}
					}
					int rrhDexists1 = 0;

					// Here use path delay to determine optic distance. Later, we will check to see
					// if the delay
					// setting needs to be updated based on Optic Distance(km) header.
					nIndex = find_necessaryA_idx("delaydl");
					if (nIndex != -1) {
						vet_to_continue(nIndex, lineA[nIndex]);
						String[] deldl = lineA[nIndex].split("\\.");
						nIndex = find_necessaryA_idx("delaydl");
						if (nIndex != -1) {
							String[] delul = lineA[nIndex].split("\\.");
							if (deldl.length > 1 && Integer.parseInt(deldl[deldl.length - 1]) > 4)
								deldl[0] = (new Integer(Integer.parseInt(deldl[0]) + 1)).toString();
							if (delul.length > 1 && Integer.parseInt(delul[delul.length - 1]) > 4)
								delul[0] = (new Integer(Integer.parseInt(delul[0]) + 1)).toString();

							if (Integer.parseInt(deldl[0]) >= Integer.parseInt(delul[0]))
								cellDataH.put("delayv", deldl[0]);
							else
								cellDataH.put("delayv", delul[0]);
						}
						if (Integer.parseInt(cellDataH.get("delayv").toString()) <= 50000)
							cellDataH.put("delay", "delay0");
						else
							cellDataH.put("delay", "delay1");
					}

					nIndex = find_necessaryA_idx("iottac");
					nidx = find_index_necssaryA("iottac");
					if (nIndex != -1) {
						vet_to_continue(nidx, lineA[nIndex]);
						cellDataH.put("iottac", lineA[nIndex]);
					} else {
						vet_to_continue(nidx, necessaryA.get(nidx).get("default"));
						cellDataH.put("iottac", necessaryA.get(nidx).get("default"));
					}
					// fetching rrh directly for new riu radios
					nIndex = find_necessaryA_idx("rrh");
					nidx = find_index_necssaryA("rrh");
					if (nIndex != -1) {
						vet_to_continue(nidx, lineA[nIndex]);
						cellDataH.put("rrh", lineA[nIndex]);
					} else {
						vet_to_continue(nidx, necessaryA.get(nidx).get("default"));
						cellDataH.put("rrh", necessaryA.get(nidx).get("default"));
					}

					// hard code the preamble format until RF supplies a reliable column.
					cellDataH.put("prachcfgidx", "format0");

					// For DSP Mapping
					cellDataH.put("dspid", "-1");
					cellDataH.put("carrierInDSPid", "-1");
					cellDataH.put("multict", "");

					if (!(cellDataH.get("band").toString().contains("700"))
							&& Integer.parseInt(cellDataH.get("bandwidth").toString()) <= 10)
						cellDataH.put("canUse6", "1");
					else
						cellDataH.put("canUse6", "0");

					cellDataH.put("bwstr",
							get_bw_str(cellDataH.get("band").toString(), cellDataH.get("bandwidth").toString()));
					// eMTC from ciq
					nIndex = find_necessaryA_idx("eMTC");
					nidx = find_index_necssaryA("eMTC");
					if (nIndex != -1) {
						vet_to_continue(nidx, lineA[nIndex]);
						cellDataH.put("eMTC", lineA[nIndex]);
					} else {
						vet_to_continue(nidx, necessaryA.get(nidx).get("default"));
						cellDataH.put("eMTC", necessaryA.get(nidx).get("default"));
					}

					cellDataH.put("emtc", get_emtc_support(cellDataH.get("band").toString(),
							cellDataH.get("bandwidth").toString(), cellDataH.get("eMTC").toString()));

					String cabl;
					nIndex = find_necessaryA_idx("cablelengthdl");
					nidx = find_index_necssaryA("cablelengthdl");
					if (nIndex != -1) {
						vet_to_continue(nidx, lineA[nIndex]);
						String[] deldl = lineA[nIndex].split("\\.");
						nIndex = find_necessaryA_idx("cablelengthdl");
						if (nIndex != -1) {
							String[] delul = lineA[nIndex].split("\\.");
							if (deldl.length > 1 && Integer.parseInt(deldl[deldl.length - 1]) > 4)
								deldl[0] = (new Integer(Integer.parseInt(deldl[0]) + 1)).toString();
							if (delul.length > 1 && Integer.parseInt(delul[delul.length - 1]) > 4)
								delul[0] = (new Integer(Integer.parseInt(delul[0]) + 1)).toString();

							if (Integer.parseInt(deldl[0]) >= Integer.parseInt(delul[0]))
								cabl = deldl[0];
							else
								cabl = delul[0];

							cellDataH.put("bandnum",
									getBand(Integer.parseInt(cellDataH.get("earfcndl").toString())).toString());
							nIndex = find_necessaryA_idx("rrh");
							nidx = find_index_necssaryA("rrh");
							if (nIndex != -1) {
								vet_to_continue(nidx, lineA[nIndex]);

								cellDataH.put("vendor", getRadioCodeOrVendor(lineA[nIndex],
										getBand(Integer.parseInt(cellDataH.get("earfcndl").toString())), 1));
								if (cellDataH.get("vendor").toString().equals("NOTFOUND")) {
									System.out.println(lineA[nIndex] + " "
											+ getBand(Integer.parseInt(cellDataH.get("earfcndl").toString()))
											+ cellDataH.get("vendor"));
									dlog("ERROR: Unable to identify RRH\n");
								}
								if (cellDataH.get("vendor").toString().equals("alu"))
									cellDataH.put("rvidx", "0");
								else if (cellDataH.get("vendor").toString().equals("asl"))
									cellDataH.put("rvidx", "1");
								else
									cellDataH.put("rvidx", "2");

								if (Integer.parseInt(cellDataH.get("rxd").toString()) >= Integer
										.parseInt(cellDataH.get("txd").toString())) {
									write_radio_info_to_hash(lineA[nIndex], cellDataH, rrhDataHref, rrhDexists,
											cellDataH.get("rxd").toString(), cabl);
								} else {
									write_radio_info_to_hash(lineA[nIndex], cellDataH, rrhDataHref, rrhDexists,
											cellDataH.get("txd").toString(), cabl);
								}
								if (rrhDexists == 0) {
									if (!rrhDataHref.get("adstate").toString().contains("NEW")) {
										rrhDataAref.add(rrhDataHrefB);
									}
								}

								if (rrhDexists == 0) {
									// adding all cells
									rrhDataCref.add(rrhDataHrefB);
								}

								if (Integer.parseInt(cellDataH.get("rxd").toString()) >= Integer
										.parseInt(cellDataH.get("txd").toString())) {
									write_radio_info_to_hash(lineA[nIndex], cellDataH, rrhDataHrefB, rrhDexists1,
											cellDataH.get("rxd").toString(), cabl);
								} else {
									write_radio_info_to_hash(lineA[nIndex], cellDataH, rrhDataHrefB, rrhDexists1,
											cellDataH.get("txd").toString(), cabl);
								}
								if (rrhDexists1 == 0) {
									if (rrhDataHrefB.get("adstate").toString().contains("NEW")) {
										rrhDataBref.add(rrhDataHrefB);
									}

									// rrhDataBref.add(rrhDataHrefB);

								}

							}
						}
					}
					cellDataAref.add(cellDataH);
				}

			}

			for (int i = 0; i < rrhDataAref.size(); i++) {
				if (((ArrayList<String>) rrhDataAref.get(i).get("startearfcnA")).get(0).equals("-1")) {
					if (((ArrayList<String>) rrhDataAref.get(i).get("startearfcnA")).get(1).equals("-1")) {
						if (((ArrayList<String>) rrhDataAref.get(i).get("startearfcnA")).get(2).equals("-1")) {
							dlog("ERROR: NO START EARFCN VALUE\n");
						} else {
							((ArrayList<String>) rrhDataAref.get(i).get("startearfcnA")).set(0,
									((ArrayList<String>) rrhDataAref.get(i).get("startearfcnA")).get(2));
						}
					} else {
						((ArrayList<String>) rrhDataAref.get(i).get("startearfcnA")).set(0,
								((ArrayList<String>) rrhDataAref.get(i).get("startearfcnA")).get(1));
					}
				} else if (((ArrayList<String>) rrhDataAref.get(i).get("startearfcnA")).get(1).equals("-1")) {
					if (((ArrayList<String>) rrhDataAref.get(i).get("startearfcnA")).get(2).equals("-1")) {
						((ArrayList<String>) rrhDataAref.get(i).get("startearfcnA")).set(1,
								((ArrayList<String>) rrhDataAref.get(i).get("startearfcnA")).get(0));
					} else {
						((ArrayList<String>) rrhDataAref.get(i).get("startearfcnA")).set(1,
								((ArrayList<String>) rrhDataAref.get(i).get("startearfcnA")).get(2));
					}
				}
				((ArrayList<String>) rrhDataAref.get(i).get("startearfcnA"))
						.remove(((ArrayList<String>) rrhDataAref.get(i).get("startearfcnA")).size() - 1);

				// Sometimes the CIQ contains blanks, which are turned to zeros in scrub, for
				// some of the cable lengths.
				// If some radios have both blanks (zeros) and valid lengths, we want to
				// populate the valid lengths to
				// all of the ports in use. Also, we have a max of 100 for cable length.
				int length = 0;
				for (int j = 0; j < ((int[]) rrhDataAref.get(i).get("antennaPortMapA")).length; j++) {
					if (((int[]) rrhDataAref.get(i).get("antennaPortMapA"))[j] != -1) {
						if (((int[]) rrhDataAref.get(i).get("antennaPortMapA"))[j] > length) {
							length = ((int[]) rrhDataAref.get(i).get("antennaPortMapA"))[j];
							if (length > 100)
								length = 100;
							for (int k = 0; k < ((int[]) rrhDataAref.get(i).get("antennaPortMapA")).length; k++) {
								if (((int[]) rrhDataAref.get(i).get("antennaPortMapA"))[k] != -1) {
									((int[]) rrhDataAref.get(i).get("antennaPortMapA"))[k] = length;
								}
							}
						}
					}
				}
			}

			for (int i = 0; i < rrhDataBref.size(); i++) {
				if (((ArrayList<String>) rrhDataBref.get(i).get("startearfcnA")).get(0).equals("-1")) {
					if (((ArrayList<String>) rrhDataBref.get(i).get("startearfcnA")).get(1).equals("-1")) {
						if (((ArrayList<String>) rrhDataBref.get(i).get("startearfcnA")).get(2).equals("-1")) {
							dlog("ERROR: NO START EARFCN VALUE\n");
						} else {
							((ArrayList<String>) rrhDataBref.get(i).get("startearfcnA")).set(0,
									((ArrayList<String>) rrhDataBref.get(i).get("startearfcnA")).get(2));
						}
					} else {
						((ArrayList<String>) rrhDataBref.get(i).get("startearfcnA")).set(0,
								((ArrayList<String>) rrhDataBref.get(i).get("startearfcnA")).get(1));
					}
				} else if (((ArrayList<String>) rrhDataBref.get(i).get("startearfcnA")).get(1).equals("-1")) {
					if (((ArrayList<String>) rrhDataBref.get(i).get("startearfcnA")).get(2).equals("-1")) {
						((ArrayList<String>) rrhDataBref.get(i).get("startearfcnA")).set(1,
								((ArrayList<String>) rrhDataBref.get(i).get("startearfcnA")).get(0));
					} else {
						((ArrayList<String>) rrhDataBref.get(i).get("startearfcnA")).set(1,
								((ArrayList<String>) rrhDataBref.get(i).get("startearfcnA")).get(2));
					}
				}
				((ArrayList<String>) rrhDataBref.get(i).get("startearfcnA"))
						.remove(((ArrayList<String>) rrhDataBref.get(i).get("startearfcnA")).size() - 1);

				// Sometimes the CIQ contains blanks, which are turned to zeros in scrub, for
				// some of the cable lengths.
				// If some radios have both blanks (zeros) and valid lengths, we want to
				// populate the valid lengths to
				// all of the ports in use. Also, we have a max of 100 for cable length.
				int length = 0;
				for (int j = 0; j < ((int[]) rrhDataBref.get(i).get("antennaPortMapA")).length; j++) {
					if (((int[]) rrhDataBref.get(i).get("antennaPortMapA"))[j] != -1) {
						if (((int[]) rrhDataBref.get(i).get("antennaPortMapA"))[j] > length) {
							length = ((int[]) rrhDataBref.get(i).get("antennaPortMapA"))[j];
							if (length > 100)
								length = 100;
							for (int k = 0; k < ((int[]) rrhDataBref.get(i).get("antennaPortMapA")).length; k++) {
								if (((int[]) rrhDataBref.get(i).get("antennaPortMapA"))[k] != -1) {
									((int[]) rrhDataBref.get(i).get("antennaPortMapA"))[k] = length;
								}
							}
						}
					}
				}
			}

			for (int i = 0; i < cellDataAref.size(); i++) {
				if (cellDataAref.get(i).get("delay").toString().equals("delay0")) {
					if (cellDataAref.get(i).get("vendor").toString().equals("asl")) {
						if (Integer.parseInt(cellDataAref.get(i).get("opticdist").toString()) > 5) {
							cellDataAref.get(i).replace("delay", "delay1");
						}
					} else {
						if (Integer.parseInt(cellDataAref.get(i).get("opticdist").toString()) > 10) {
							cellDataAref.get(i).replace("delay", "delay1");
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("Exception in USM Cell Grower " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		} finally {
			if (sciqr != null)
				sciqr.close();
		}
	}

	private static void populateData22V(String[] lineA, TreeMap<String, Object> cellDataH) {
		int nIndex;
		int nidx;
		nIndex = find_necessaryA_idx("preferredHighestChannel");
		nidx = find_index_necssaryA("preferredHighestChannel");
		if (nIndex != -1) {
			vet_to_continue(nidx, lineA[nIndex]);
			cellDataH.put("preferredHighestChannel", lineA[nIndex]);
		} else {
			vet_to_continue(nidx, necessaryA.get(nidx).get("default"));
			cellDataH.put("preferredHighestChannel", necessaryA.get(nidx).get("default"));
		}

		nIndex = find_necessaryA_idx("preferredLowestChannel");
		nidx = find_index_necssaryA("preferredLowestChannel");
		if (nIndex != -1) {
			vet_to_continue(nidx, lineA[nIndex]);
			cellDataH.put("preferredLowestChannel", lineA[nIndex]);
		} else {
			vet_to_continue(nidx, necessaryA.get(nidx).get("default"));
			cellDataH.put("preferredLowestChannel", necessaryA.get(nidx).get("default"));
		}

		nIndex = find_necessaryA_idx("preference");
		nidx = find_index_necssaryA("preference");
		if (nIndex != -1) {
			vet_to_continue(nidx, lineA[nIndex]);
			cellDataH.put("preference", lineA[nIndex]);
		} else {
			vet_to_continue(nidx, necessaryA.get(nidx).get("default"));
			cellDataH.put("preference", necessaryA.get(nidx).get("default"));
		}
	}

	private static int find_necessaryA_idx(String ttag) {
		int poss = 0;
		for (int i = 0; i < necessaryA.size(); i++) {
			if (necessaryA.get(i).get("poss").equals("-1")) {
				poss += -1;
				if (necessaryA.get(i).get("tag").equals(ttag)) {
					return -1;
				}
			}
			if (necessaryA.get(i).get("tag").equals(ttag)) {
				return i + poss;
			}
		}
		return -1;
	}

	private static int find_index_necssaryA(String tag) {
		for (int i = 0; i < necessaryA.size(); i++) {
			if (necessaryA.get(i).get("tag").equals(tag)) {
				return i;
			}
		}
		return 0;
	}

	private static void vet_to_continue(int idx, String value) {
		if (necessaryA.get(idx).get("numeric").equals("1")) {
			if (value.equals("-1")) {
				mydie("failed vet_to_continue: " + idx + " " + value);
			}
		} else {
			if (value.equals("abort")) {
				mydie("failed vet_to_continue: " + idx + " " + value);
			}
		}
	}

	private static void write_sect_carr_cellDataH(Integer cid, TreeMap<String, Object> cellDataH) {
		String sect;
		String carr;
		if (cid < 10) {
			sect = cid.toString();
			carr = "1";
		} else if (cid > 99) {
			String var1 = cid.toString();
			String var2 = cid.toString();
			var1 = var1.replaceAll(".$", "");
			var2 = var2.replaceAll("^\\d{2}", "");
			sect = var1;
			carr = var2;
		} else {
			String tmpvar[] = cid.toString().split("");
			sect = tmpvar[0];
			carr = tmpvar[1];
		}
		cellDataH.put("sectid", sect);
		cellDataH.put("carrid", carr);
	}

	private static void write_cpri_to_cellDataH(String cpri, TreeMap<String, Object> cellDataHref) {
		String lcc, prt;
		String[] ar = cpri.split("\\D+");
		prt = ar[0];
		if (ar.length == 1)
			lcc = "0";
		else
			lcc = (new Integer(Integer.parseInt(ar[1].toString()) - 1)).toString();

		cellDataHref.put("LCCnum", lcc);
		cellDataHref.put("port", prt);
		cellDataHref.put("cpristr", lcc + "_" + cellDataHref.get("cprivalue") + "_0");
	}

	private static String get_bw_str(String band, String bw) {
		band = band.replace("AWS-2", "AWS-1");
		band = band.replace("PCS-2", "PCS");
		band = band.replace("PCS-3", "PCS");
		band = band.replace("PCS-1", "PCS");
		return (band + "/" + bw + "MHz");
	}

	private static String get_emtc_support(String band , String bandwidth, String eMTC) {
		if (band.matches("(.*)700(.*)")) {
			//&& eMTC.equals("1"))
			if (bandwidth.equals("5"))
				return "disable";
			else
				return "enable";
		}		
		else
			return "disable";
	}

	private static Integer getBand(int dlear) {
		Integer band = 0;
		if ((dlear >= 600) && (dlear <= 1199)) {
			band = 2;
		}
		if ((dlear >= 1950) && (dlear <= 2399)) {
			band = 4;
		}
		if ((dlear >= 2400) && (dlear <= 2649)) {
			band = 5;
		}
		if ((dlear >= 5180) && (dlear <= 5279)) {
			band = 13;
		}
		if ((dlear >= 66436) && (dlear <= 67335)) {
			band = 66;
		}
		if ((dlear >= 46790) && (dlear <= 54539)) {
			band = 46;
		}
		return band;
	}

	@SuppressWarnings("unchecked")
	private static void write_radio_info_to_hash(String rtype, TreeMap<String, Object> chref,
			TreeMap<String, Object> rhref, int exists, String diversity, String cabl) {
		rtype = rtype.toUpperCase();
		String[] a = rtype.split("->");
		rtype = a[a.length - 1];

		Integer band = getBand(Integer.parseInt(chref.get("earfcndl").toString()));
		String rcode = getRadioCodeOrVendor(rtype, band, 0);
		if (rcode.equals("NOTFOUND"))
			dlog("ERROR: Unable to identify RRH\n");
		((ArrayList<String>) rhref.get("fccId")).add(chref.get("cbrcfcc").toString());
		((ArrayList<String>) rhref.get("antennaGains")).add(chref.get("AntennaGain").toString());
		((ArrayList<String>) rhref.get("cellA")).add(chref.get("cellid").toString());
		int bandclass;
		if (exists == 0) {
			rhref.put("code", rcode);
			rhref.put("LCCnum", chref.get("LCCnum"));
			rhref.put("port", chref.get("port"));
			rhref.put("cpristr", chref.get("cpristr"));
			rhref.put("adstate", chref.get("adstate"));
			if (version.equals("21.B.0") || version.equals("21.C.0") || version.equals("21.D.0")
					|| version.equals("22.A.0")|| version.equals("22.C.0")) {
				rhref.put("delaydl", chref.get("delaydl"));
				rhref.put("delayul", chref.get("delayul"));
				rhref.put("bandpcs", chref.get("bandpcs"));
				rhref.put("txd", chref.get("txd"));
				rhref.put("RFPORTOPERATIONMODE", chref.get("RFPORTOPERATIONMODE"));
				
			}
			if (version.equals("22.A.0") || version.equals("22.C.0")) { // fdd mmu
				String xyz = chref.get("mmuBisectorMode").toString();
				rhref.put("mmuBisectorMode", xyz.toLowerCase());
			}
			((ArrayList<String>) rhref.get("bandA")).add(band.toString());
			ArrayList<String> startearfcnA = new ArrayList<>();
			startearfcnA.add("-1");
			startearfcnA.add("-1");
			startearfcnA.add("-1");
			rhref.put("startearfcnA", startearfcnA);
			bandclass = get_band_class(band);
			if (bandclass != -1) {
				((ArrayList<String>) rhref.get("startearfcnA")).set(bandclass,
						get_start_earfcn(chref.get("earfcndl").toString(), rcode, chref.get("bandwidth").toString()));
			}
			if (rhref.get("code").equals("mf1601d-250") || rhref.get("txd").equals("16")) { // added condition for fdd
																							// mmu
				rhref.put("antennaPortMapA", new int[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }); //
			} else {
				rhref.put("antennaPortMapA", new int[] { -1, -1, -1, -1, -1, -1, -1, -1 });
			}

		} else {
			String codechk = getRadioCodeOrVendor(rtype, band, 0);
			if (codechk.equals("NOTFOUND")) {
				dlog("ERROR: Unable to identify RRH\n");
			}
			bandclass = get_band_class(band);
			if (!rhref.get("code").toString().equals(codechk)) {
				dlog("CONFIGURATION ERROR:" + rtype + " and " + codechk + " assigned to the same cpri port.\n");
			}
			boolean newbandboo = true;
			for (int i = 0; i < ((ArrayList<String>) rhref.get("bandA")).size(); i++) {
				if (band == Integer.parseInt(((ArrayList<String>) rhref.get("bandA")).get(i)))
					newbandboo = false;
			}
			if (newbandboo) {
				((ArrayList<String>) rhref.get("bandA")).add(band.toString());
				if (bandclass != -1) {
					((ArrayList<String>) rhref.get("startearfcnA")).set(bandclass, get_start_earfcn(
							chref.get("earfcndl").toString(), rcode, chref.get("bandwidth").toString()));
				}

			}
		}
		int idx = 0;
		if (rcode.equals("mf1601d-250") || rhref.get("txd").equals("16")) {// fdd mmu
			int[] idxA = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };

			for (int i = 0; i < Integer.parseInt(diversity); i++) { // diversity
				if (Integer.parseInt(cabl) > ((int[]) rhref.get("antennaPortMapA"))[idxA[i] + idx]) {
					((int[]) rhref.get("antennaPortMapA"))[idxA[i] + idx] = Integer.parseInt(cabl);
				}
			}

		} else {
			if (rcode.equals("rfv01u-d10") && band.equals(2))
				idx = 4;
			else
				idx = 0;

			int[] idxA = { 0, 1, 2, 3 };
			if (get_antennaXpolorSupport(rcode) == 1) {
				idxA[0] = 0;
				idxA[1] = 2;
				idxA[2] = 1;
				idxA[3] = 3;
			}

			for (int i = 0; i < Integer.parseInt(diversity); i++) { // diversity
				if (Integer.parseInt(cabl) > ((int[]) rhref.get("antennaPortMapA"))[idxA[i] + idx]) {
					((int[]) rhref.get("antennaPortMapA"))[idxA[i] + idx] = Integer.parseInt(cabl);
				}
			}
		}
	}

	private static String getRadioCodeOrVendor(String rtype, int band, int returntype) {
		String code = "NOTFOUND";
		String vendor = "NOTFOUND";

		if (rtype.matches("(.*)3JR53386AA(.*)")) {
			code = "alu-0966";
			vendor = "alu";
		}
		if (rtype.matches("(.*)409102316(.*)")) {
			code = "alu-0630";
			vendor = "alu";
		}
		if (rtype.matches("(.*)849122031(.*)")) {
			code = "alu-07e0";
			vendor = "alu";
		}
		if (rtype.matches("(.*)409113651(.*)")) {
			code = "alu-0623";
			vendor = "alu";
		}
		if (rtype.matches("(.*)109791186(.*)")) {
			code = "alu-08c0";
			vendor = "alu";
		}
		if (rtype.matches("(.*)109814475(.*)")) {
			code = "alu-08c0";
			vendor = "alu";
		}
		if (rtype.matches("(.*)109814467(.*)")) {
			code = "alu-08c0";
			vendor = "alu";
		}
		if (rtype.matches("(.*)109800367(.*)")) {
			code = "alu-08c0";
			vendor = "alu";
		}
		if (rtype.matches("(.*)109803098(.*)")) {
			code = "alu-08c0";
			vendor = "alu";
		}
		if (rtype.matches("(.*)109807552(.*)")) {
			code = "alu-08c0";
			vendor = "alu";
		}
		if (rtype.matches("(.*)3BK61821AA(.*)")) {
			code = "alu-08c7";
			vendor = "alu";
		}
		if (rtype.matches("(.*)3BK61689AAAF(.*)")) {
			code = "alu-08c2";
			vendor = "alu";
		}
		if (rtype.matches("(.*)3BK61812AA(.*)")) {
			code = "alu-08c7";
			vendor = "alu";
		}
		if (rtype.matches("(.*)3JR52709AA(.*)")) {
			code = "alu-08f1";
			vendor = "alu";
		}
		if (rtype.matches("(.*)3JR59011AA(.*)")) {
			code = "alu-0968";
			vendor = "alu";
		}
		if (rtype.matches("(.*)109799361(.*)")) {
			code = "alu-0730";
			vendor = "alu";
		}
		if (rtype.matches("(.*)109799379(.*)")) {
			code = "alu-0730";
			vendor = "alu";
		}
		if (rtype.matches("(.*)849172416(.*)")) {
			code = "alu-07d2";
			vendor = "alu";
		}
		if (rtype.matches("(.*)109809806(.*)")) {
			code = "alu-08c1";
			vendor = "alu";
		}
		if (rtype.matches("(.*)109803106(.*)")) {
			code = "alu-08c1";
			vendor = "alu";
		}
		if (rtype.matches("(.*)3JR53349AA(.*)")) {
			code = "alu-0961";
			vendor = "alu";
		}
		if (rtype.matches("(.*)3JR53349AB(.*)")) {
			code = "alu-0961";
			vendor = "alu";
		}
		if (rtype.matches("(.*)3JR39501AA(.*)")) {
			code = "alu-070f";
			vendor = "alu";
		}
		if (rtype.matches("(.*)3JR10801AA(.*)")) {
			code = "alu-0705";
			vendor = "alu";
		}
		if (rtype.matches("(.*)3JR10601AA(.*)")) {
			code = "alu-0702";
			vendor = "alu";
		}

		if (rtype.matches("(.*)3BK61812AAAD(.*)")) {
			code = "alu-08c7";
			vendor = "alu";
		}
		if (rtype.matches("(.*)3BK61687AA(.*)")) {
			code = "alu-08c2";
			vendor = "alu";
		}

		if ((rtype.matches("(.*)3JR39055AAAB(.*)")) || (rtype.matches("(.*)3JR39054AAAD(.*)"))) {
			code = "alu-08c6";
			vendor = "alu";
		}
		if (rtype.matches("(.*)3JR39054AA(.*)")) {
			code = "alu-08c6";
			vendor = "alu";
		}

		if (rtype.matches("(.*)473966A(.*)")) {
			code = "asl-ahca-01";
			vendor = "asl";
		}
		if ((rtype.matches("(.*)3JR63093AA(.*)")) || (rtype.matches("(.*)3JR53445AA(.*)"))) {
			code = "alu-0a10-700m";
			vendor = "alu";
		}
		if ((rtype.matches("(.*)3JR63090AA(.*)"))
				|| (rtype.matches("(.*)3JR53446AA(.*)")) && ((band == 4) || (band == 66))) {
			code = "alu-0a11-aws";
			vendor = "alu";
		}
		if ((rtype.matches("(.*)3JR63089AA(.*)")) || (rtype.matches("(.*)3JR53446AA(.*)") && (band == 2))) {
			code = "alu-0a11-pcs";
			vendor = "alu";
		}
		if (rtype.matches("(.*)06b0(.*)")) {
			code = "alu-06b0";
			vendor = "alu";
		}
		if ((rtype.matches("(.*)RFV01U-D2A(.*)")) || (rtype.matches("SAMSUNGRRH(RFV01U-D2A)"))) {
			code = "rfv01u-d20";
			vendor = "samsung";
		}
		if ((rtype.matches("(.*)RFV01U-D1A(.*)")) || (rtype.matches("SAMSUNGRRH(RFV01U-D1A)"))) {
			code = "rfv01u-d10";
			vendor = "samsung";
		}
		if (rtype.matches("(.*)RT4401-48(.*)")) {
			code = "rt4401-480";
			vendor = "samsung";
		}
		if (rtype.matches("109799361")) {
			code = "alu-0730";
			vendor = "samsung";
		}
		if (rtype.matches("(.*)RT2201-46A(.*)")) {
			code = "rt2201-460";
			vendor = "samsung";
		}
		if (rtype.matches("(.*)RF4402D-D1A(.*)")) {
			code = "rf4402d-d10";
			vendor = "samsung";
		}
		// RIU support
		if (rtype.matches("(.*)700/AWSRIU(.*)")) {
			code = "rrb1-000";
			vendor = "samsung";
		}
		if (rtype.matches("(.*)850/PCSRIU(.*)")) {
			code = "rrb2-001";
			vendor = "samsung";
		}
		// ORAN radios
		if (rtype.matches("(.*)RF4440D-13A(.*)")) {
			code = "rf4440d-130";
			vendor = "samsung";
		}
		if (rtype.matches("(.*)RF4439D-25A(.*)")) {
			code = "rf4439d-250";
			vendor = "samsung";
		}
		// fdd mmu
		if (rtype.matches("(.*)MF1601D-25A(.*)")) {
			code = "mf1601d-250";
			vendor = "samsung";
		}
		if (code.equals("NOTFOUND")) {
			vendor = "alu";
		}
		if (returntype == 0) {
			return code;
		} else {
			return vendor;
		}
	}

	private static int get_band_class(int band) {
		if ((band == 13) || (band == 4)) {
			return 0;
		}
		if ((band == 2) || (band == 5)) {
			return 1;
		}
		if (band == 66) {
			return 2;
		}
		return -1;
	}

	private static String get_start_earfcn(String earfcn, String rcode, String bw) {
		int defaultstartearfcn = 0;
		double startearfcn = 0;

		if ((Integer.valueOf(earfcn) >= 600) && ((Integer.valueOf(earfcn) <= 1199))) {
			defaultstartearfcn = 600;
		}
		if (((Integer.valueOf(earfcn) >= 1950) && ((Integer.valueOf(earfcn) <= 2399)))) {
			defaultstartearfcn = 1950;
		}
		if (((Integer.valueOf(earfcn) >= 2400) && ((Integer.valueOf(earfcn) <= 2649)))) {
			defaultstartearfcn = 2400;
		}
		if (((Integer.valueOf(earfcn) >= 5180) && ((Integer.valueOf(earfcn) <= 5279)))) {
			defaultstartearfcn = 5180;
		}
		if (((Integer.valueOf(earfcn) >= 66436) && ((Integer.valueOf(earfcn) <= 67335)))) {
			defaultstartearfcn = 66436;
		}
		if (((Integer.valueOf(earfcn) >= 46790) && ((Integer.valueOf(earfcn) <= 54539)))) {
			defaultstartearfcn = 46790;
		}
		if ((Integer.valueOf(earfcn) == 0)) {
			defaultstartearfcn = 66436;
		}

		if (rcode.matches("(.*)alu-0730(.*)")) {
			int ibw = 20;
			double centerfreq;
			int freqLow = 2110;
			int freqHigh = 2155;
			int offset = 1950;
			centerfreq = freqLow + 0.1 * (Integer.valueOf(earfcn) - offset);
			double startfreq = centerfreq - (Integer.valueOf(bw) * 0.5);
			int highestFreqAllowed = freqHigh - ibw;
			double freqOfStartearfcn;
			if (highestFreqAllowed >= startfreq) {
				freqOfStartearfcn = startfreq;
			} else {
				freqOfStartearfcn = highestFreqAllowed;
			}
			startearfcn = ((freqOfStartearfcn - freqLow) / 0.1) + offset;
		}
		if (startearfcn == 0) {
			startearfcn = defaultstartearfcn;
		}
		return (new Integer(new Double(startearfcn).intValue())).toString();
	}

	private static int get_antennaXpolorSupport(String rcode) {
		if (rcode.matches("(.*)asl-ahca-01(.*)")) {
			return 1;
		} else {
			return 0;
		}
	}

	private static void print_logs(String pmsg) {
		if (printall || printlog) {
			dlog(pmsg + "\n");
		}
	}

	private static void dspConfiguration(ArrayList<TreeMap<String, Object>> cellDataA) {
		System.out.println("Before the for loop of the cellData function");
		for (int i = 0; i < cellDataA.size() - 2; i++) {
			System.out.println("ENTER INTO THE FOR LOOP FOR FINDING THE DspCellIndex");
			System.out.println("DspCellIndex value is given as the = " + cellDataA.get(i).get("DspCellIndex"));
			System.out.println("DspID IS = " + cellDataA.get(i).get("DspID"));
			System.out.println("RUpportID is = " + cellDataA.get(i).get("RUPortID"));

			if (cellDataA.get(i).get("DspCellIndex").toString().equals("3")
					&& cellDataA.get(i + 1).get("DspCellIndex").toString().equals("4")
					&& cellDataA.get(i + 2).get("DspCellIndex").toString().equals("5")) {
				System.out.println("ENTERING INTO THE DSPCELLINDEX PART FOR COMARISION");
				portnum1 = cellDataA.get(i).get("port").toString();
				portnum2 = cellDataA.get(i + 1).get("port").toString();
				portnum3 = cellDataA.get(i + 2).get("port").toString();

				lccnum1 = cellDataA.get(i).get("LCCnum").toString();
				lccnum2 = cellDataA.get(i + 1).get("LCCnum").toString();
				lccnum3 = cellDataA.get(i + 2).get("LCCnum").toString();

				dspid1 = cellDataA.get(i).get("DspID").toString();
				dspid2 = cellDataA.get(i + 1).get("DspID").toString();
				dspid3 = cellDataA.get(i + 2).get("DspID").toString();

				bandwidth1 = cellDataA.get(i).get("bandwidth").toString();
				bandwidth2 = cellDataA.get(i + 1).get("bandwidth").toString();
				bandwidth3 = cellDataA.get(i + 2).get("bandwidth").toString();

				band1 = cellDataA.get(i).get("band").toString();
				band2 = cellDataA.get(i + 1).get("band").toString();
				band3 = cellDataA.get(i + 2).get("band").toString();
			}
		}
	}

	private static void sortData(ArrayList<TreeMap<String, Object>> cellDataA,
			ArrayList<TreeMap<String, Object>> rrhDataA) {
		for (int i = 0; i < cellDataA.size() - 1; i++) {
			for (int j = 0; j < cellDataA.size() - 1; j++) {
				if (Integer.parseInt(cellDataA.get(j).get("LCCnum").toString()) > Integer
						.parseInt(cellDataA.get(j + 1).get("LCCnum").toString())) {
					TreeMap<String, Object> tmpvar = cellDataA.get(j);
					tmpvar = cellDataA.get(j);
					cellDataA.set(j, cellDataA.get(j + 1));
					cellDataA.set(j + 1, tmpvar);
				}

				if ((Integer.parseInt(cellDataA.get(j).get("LCCnum").toString()) == Integer
						.parseInt(cellDataA.get(j + 1).get("LCCnum").toString()))
						&& ((Integer.parseInt(cellDataA.get(j).get("carrid").toString()) > Integer
								.parseInt(cellDataA.get(j + 1).get("carrid").toString())))) {
					TreeMap<String, Object> tmpvar = cellDataA.get(j);
					tmpvar = cellDataA.get(j);
					cellDataA.set(j, cellDataA.get(j + 1));
					cellDataA.set(j + 1, tmpvar);
				}

				if (((Integer.parseInt(cellDataA.get(j).get("LCCnum").toString()) == Integer
						.parseInt(cellDataA.get(j + 1).get("LCCnum").toString()))
						&& (Integer.parseInt(cellDataA.get(j).get("carrid").toString()) > Integer
								.parseInt(cellDataA.get(j + 1).get("carrid").toString()))
						&& (Integer.parseInt(cellDataA.get(j).get("sectid").toString()) > Integer
								.parseInt(cellDataA.get(j + 1).get("sectid").toString())))) {
					TreeMap<String, Object> tmpvar = cellDataA.get(j);
					tmpvar = cellDataA.get(j);
					cellDataA.set(j, cellDataA.get(j + 1));
					cellDataA.set(j + 1, tmpvar);
				}
			}
		}

		for (int i = 0; i < rrhDataA.size() - 1; i++) {
			for (int j = 0; j < rrhDataA.size() - 1; j++) {
				if ((Integer.parseInt(rrhDataA.get(j).get("LCCnum").toString()) > Integer
						.parseInt(rrhDataA.get(j + 1).get("LCCnum").toString()))) {
					TreeMap<String, Object> tmpvar = rrhDataA.get(j);
					tmpvar = rrhDataA.get(j);
					rrhDataA.set(j, rrhDataA.get(j + 1));
					rrhDataA.set(j + 1, tmpvar);
				}

				if ((Integer.parseInt(rrhDataA.get(j).get("LCCnum").toString()) == Integer
						.parseInt(rrhDataA.get(j + 1).get("LCCnum").toString()))
						&& (Integer.parseInt(rrhDataA.get(j).get("port").toString()) > Integer
								.parseInt(rrhDataA.get(j + 1).get("port").toString()))) {
					TreeMap<String, Object> tmpvar = rrhDataA.get(j);
					tmpvar = rrhDataA.get(j);
					rrhDataA.set(j, rrhDataA.get(j + 1));
					rrhDataA.set(j + 1, tmpvar);
				}
			}
		}
	}

	private static int foundA(String element, ArrayList<String> arrayref) {

		// Returns -1 if the element is NOT found in the array.
		// Otherwise, returns first index in the array containing $element.
		for (int i = 0; i < arrayref.size(); i++) {
			if (element.matches("^[+-]?\\d+$")) {
				if (element.equals(arrayref.get(i))) {
					return i;
				}
			} else {
				if (element.contains(arrayref.get(i))) {
					return i;
				}
			}
		}
		return -1;
	}

	@SuppressWarnings("unchecked")
	private static void set_supported_boo(ArrayList<TreeMap<String, Object>> cellDataAref,
			ArrayList<TreeMap<String, Object>> rrhDataAref, ArrayList<ArrayList<TreeMap<String, Object>>> lccAref,
			Integer site1) {
		String site="";
		if(null!=site) {
			site=site1+"";
		}
		supportedboo = true;

		for (int i = 0; i < cellDataAref.size(); i++) {
			if ((cellDataAref.get(i).get("vendor").toString().equals("asl"))
					&& (Integer.parseInt(cellDataAref.get(i).get("opticdist").toString()) > 15)) {
				write_supportability_report(site,
						"- Cell: " + cellDataAref.get(i).get("cellid").toString() + " Optic Distance km: "
								+ cellDataAref.get(i).get("opticdist").toString()
								+ " ASL.  Max optic distance is 15km\n");
				supportedboo = false;
			} else if (Integer.parseInt(cellDataAref.get(i).get("opticdist").toString()) > 20) {
				write_supportability_report(site,
						"- Cell: " + cellDataAref.get(i).get("cellid").toString() + " Optic Distance km: "
								+ cellDataAref.get(i).get("opticdist").toString() + ".  Max optic distance is 20km\n");
				supportedboo = false;
			}
		}

		// samsung radio config:
		for (int i = 0; i < rrhDataAref.size(); i++) {
			if (((String) rrhDataAref.get(i).get("code")).matches("(.*)rfv01u(.*)")) {
				HashMap<String, Object> radioHref = new HashMap<String, Object>();
				radioHref = samsung_radio_ref(rrhDataAref.get(i).get("LCCnum").toString(),
						rrhDataAref.get(i).get("port").toString(), (ArrayList<String>) rrhDataAref.get(i).get("cellA"),
						rrhDataAref.get(i).get("code").toString(), cellDataAref);

				int b0inuseboo = Integer
						.parseInt(((HashMap<String, Object>) radioHref.get("b0carr")).get("inuse").toString());
				int b1inuseboo = Integer
						.parseInt(((HashMap<String, Object>) radioHref.get("b1carr")).get("inuse").toString());
				int b0num = 0;
				int b1num = 0;

				if (b0inuseboo == 1) {

					b0num = ((ArrayList<String>) ((HashMap<String, Object>) radioHref.get("b0carr")).get("b0band"))
							.size();

				}

				if (b1inuseboo == 1) {
					b1num = ((ArrayList<String>) ((HashMap<String, Object>) radioHref.get("b1carr")).get("b1band"))
							.size();
				}

				if (b0inuseboo == 1) {
					if (b0num > Integer.parseInt(radioHref.get("b0carRC").toString())) {
						write_supportability_report(site,
								"- Link " + rrhDataAref.get(i).get("LCCnum") + "_" + rrhDataAref.get(i).get("port")
										+ ": " + rrhDataAref.get(i).get("code") + " only supports ");
						write_supportability_report(site, "  " + radioHref.get("b0carRC")
								+ " carriers for the first band. CIQ has + " + b0num + " carriers.\n");
						supportedboo = false;
					}
				}

				if (b1inuseboo == 1) {
					if (b1num > Integer.parseInt(radioHref.get("b1carRC").toString())) {
						write_supportability_report(site,
								"- Link " + rrhDataAref.get(i).get("LCCnum") + "_" + rrhDataAref.get(i).get("port")
										+ ": " + rrhDataAref.get(i).get("code") + " only supports ");
						write_supportability_report(site, "  " + radioHref.get("b1carRC")
								+ " carriers for the first band. CIQ has + " + b1num + " carriers.\n");
						supportedboo = false;
					}
				}

				if ((b1inuseboo == 1) && (b0inuseboo == 1)) {
					if ((b0num + b1num) > Integer.parseInt(radioHref.get("tcarRC").toString())) {
						write_supportability_report(site,
								"- Link " + rrhDataAref.get(i).get("LCCnum") + "_" + rrhDataAref.get(i).get("port")
										+ ": " + rrhDataAref.get(i).get("code") + " only supports ");
						write_supportability_report(site, "  a total of " + radioHref.get("tcarRC") + " carriers.\n");
						supportedboo = false;
					}
				}

				// OBW:
				int b0totbw = 0;
				if (b0inuseboo == 1) {
					for (int j = 0; j < ((ArrayList<String>) ((HashMap<String, Object>) radioHref.get("b0carr"))
							.get("b0bw")).size(); j++) {
						b0totbw = b0totbw + Integer.parseInt(
								((ArrayList<String>) ((HashMap<String, Object>) radioHref.get("b0carr")).get("b0bw"))
										.get(j));
					}
				}

				int b1totbw = 0;
				if (b1inuseboo == 1) {
					for (int j = 0; j < ((ArrayList<String>) ((HashMap<String, Object>) radioHref.get("b1carr"))
							.get("b1bw")).size(); j++) {
						b1totbw = b1totbw + Integer.parseInt(
								((ArrayList<String>) ((HashMap<String, Object>) radioHref.get("b1carr")).get("b1bw"))
										.get(j));
					}
				}

				if (b0inuseboo == 1) {
					if (b0totbw > Integer.parseInt(radioHref.get("b0obwRC").toString())) {
						write_supportability_report(site,
								"- Link " + rrhDataAref.get(i).get("LCCnum") + "_" + rrhDataAref.get(i).get("port")
										+ ": " + rrhDataAref.get(i).get("code") + " only supports ");
						write_supportability_report(site,
								"  " + radioHref.get("b0obwRC")
										+ " MHz Max combined bandwidth for the first band.\n   CIQ has " + b0totbw
										+ " MHz combined bandwidth.\n");
						supportedboo = false;
					}
				}

				if (b1inuseboo == 1) {
					if (b1totbw > Integer.parseInt(radioHref.get("b1obwRC").toString())) {
						write_supportability_report(site,
								"- Link " + rrhDataAref.get(i).get("LCCnum") + "_" + rrhDataAref.get(i).get("port")
										+ ": " + rrhDataAref.get(i).get("code") + " only supports ");
						write_supportability_report(site,
								"  " + radioHref.get("b1obwRC")
										+ " MHz Max combined bandwidth for the first band.\n   CIQ has " + b1totbw
										+ " MHz combined bandwidth.\n");
						supportedboo = false;
					}
				}

				if ((b1inuseboo == 1) && (b0inuseboo == 1)) {
					if ((b1totbw + b0totbw) > Integer.parseInt(radioHref.get("tobwRC").toString())) {
						write_supportability_report(site,
								"- Link " + rrhDataAref.get(i).get("LCCnum") + "_" + rrhDataAref.get(i).get("port")
										+ ": " + rrhDataAref.get(i).get("code") + " only supports ");
						write_supportability_report(site,
								"  " + radioHref.get("tobwRC")
										+ " MHz Max combined bandwidth for both bands.\n   CIQ has " + b0totbw
										+ " MHz plus " + b1totbw + " MHz combined bandwidth.\n");
						supportedboo = false;
					}
				}

				// Power:
				double b0actpower = 0;
				int b0roundpower = 0;
				int b0maxPower = Integer.parseInt(radioHref.get("b0_4TpowRC").toString());
				if (b0inuseboo == 1) {
					for (int j = 0; j < ((ArrayList<String>) ((HashMap<String, Object>) radioHref.get("b0carr"))
							.get("b0power")).size(); j++) {
						int tmpround = (int) Math.round(Double.parseDouble(
								((ArrayList<String>) ((HashMap<String, Object>) radioHref.get("b0carr")).get("b0power"))
										.get(j)));
						b0roundpower = b0roundpower + (tmpround * Integer.parseInt(
								((ArrayList<String>) ((HashMap<String, Object>) radioHref.get("b0carr")).get("b0Tx"))
										.get(j)));
						b0actpower = b0actpower + (Double.parseDouble(
								((ArrayList<String>) ((HashMap<String, Object>) radioHref.get("b0carr")).get("b0power"))
										.get(j))
								* Double.parseDouble(
										((ArrayList<String>) ((HashMap<String, Object>) radioHref.get("b0carr"))
												.get("b0Tx")).get(j)));
						if (Integer.parseInt(
								((ArrayList<String>) ((HashMap<String, Object>) radioHref.get("b0carr")).get("b0Tx"))
										.get(j)) == 2) {
							b0maxPower = Integer.parseInt(radioHref.get("b0_2TpowRC").toString());
						}
						if (b0totbw == 5) {
							b0maxPower = 80; // Either 40x2 for 2T or 20x4 for 4T
						}
					}
				}

				double b1actpower = 0;
				int b1roundpower = 0;
				int b1maxPower = Integer.parseInt(radioHref.get("b1_4TpowRC").toString());
				if (b1inuseboo == 1) {
					for (int j = 0; j < ((ArrayList<String>) ((HashMap<String, Object>) radioHref.get("b1carr"))
							.get("b1power")).size(); j++) {
						int tmpround = (int) Math.round(Double.parseDouble(
								((ArrayList<String>) ((HashMap<String, Object>) radioHref.get("b1carr")).get("b1power"))
										.get(j)));
						b1roundpower = b1roundpower + (tmpround * Integer.parseInt(
								((ArrayList<String>) ((HashMap<String, Object>) radioHref.get("b1carr")).get("b1Tx"))
										.get(j)));
						b1actpower = b1actpower + (Double.parseDouble(
								((ArrayList<String>) ((HashMap<String, Object>) radioHref.get("b1carr")).get("b1power"))
										.get(j))
								* Double.parseDouble(
										((ArrayList<String>) ((HashMap<String, Object>) radioHref.get("b1carr"))
												.get("b1Tx")).get(j)));
						if (Integer.parseInt(
								((ArrayList<String>) ((HashMap<String, Object>) radioHref.get("b1carr")).get("b1Tx"))
										.get(j)) == 2) {
							b1maxPower = Integer.parseInt(radioHref.get("b1_2TpowRC").toString());
						}
						if (b1totbw == 5) {
							b1maxPower = 80; // Either 40x2 for 2T or 20x4 for 4T
						}
					}
				}
			}
		}
	}

	private static HashMap<String, Object> samsung_radio_ref(String brd, String prt, ArrayList<String> cellAref,
			String code, ArrayList<TreeMap<String, Object>> cellDataAref) {
		String rtype = "unknowntype";

		// Radio Constants:
		int b0obw = 0;
		int b1obw = 0;
		int tobw = 0;
		int b0_2Tpow = 0;
		int b1_2Tpow = 0;
		int b0_4Tpow = 0;
		int b1_4Tpow = 0;
		int tpow = 0;
		int b0car = 0;
		int b1car = 0;
		int tcar = 0;

		if (code.equals("rfv01u-d20")) {
			rtype = "ll";
			b0obw = 1000;
			b1obw = 2500;
			tobw = 5000;
			b0_2Tpow = 1200;
			b1_2Tpow = 1200;
			b0_4Tpow = 1600;
			b1_4Tpow = 1600;
			tpow = 3200;
			b0car = 100;
			b1car = 300;
			tcar = 400;
		} else if (code.equals("rfv01u-d10")) {
			rtype = "hh";
			b0obw = 3000;
			b1obw = 2000;
			tobw = 5000;
			b0_2Tpow = 1800;
			b1_2Tpow = 1200;
			b0_4Tpow = 2400;
			b1_4Tpow = 1600;
			tpow = 420;
			b0car = 50;
			b1car = 20;
			tcar = 100;
		} else if (code.equals("rt4401-480")) {
			rtype = "hh";
			b0obw = 30;
			b1obw = 20;
			tobw = 50;
			b0_2Tpow = 180;
			b1_2Tpow = 120;
			b0_4Tpow = 240;
			b1_4Tpow = 160;
			tpow = 320;
			b0car = 3;
			b1car = 2;
			tcar = 4;
		}
		// end radio constants

		// carrier parallel arrays
		ArrayList<String> b0cellA = new ArrayList<>();
		ArrayList<String> b0bandA = new ArrayList<>();
		ArrayList<String> b0TxA = new ArrayList<>();
		ArrayList<String> b0powerA = new ArrayList<>();
		ArrayList<String> b0bwA = new ArrayList<>();
		ArrayList<String> b1cellA = new ArrayList<>();
		ArrayList<String> b1bandA = new ArrayList<>();
		ArrayList<String> b1TxA = new ArrayList<>();
		ArrayList<String> b1powerA = new ArrayList<>();
		ArrayList<String> b1bwA = new ArrayList<>();
		HashMap<String, Object> b0carrH = new HashMap<>();
		HashMap<String, Object> b1carrH = new HashMap<>();

		b0carrH.put("inuse", "0");
		b1carrH.put("inuse", "0");

		for (int i = 0; i < cellAref.size(); i++) {
			for (int j = 0; j < cellDataAref.size(); j++) {
				if (cellDataAref.get(j).get("cellid").toString().equals(cellAref.get(i))) {
					if ((Integer.parseInt(cellDataAref.get(j).get("bandnum").toString()) == 13)
							|| (Integer.parseInt(cellDataAref.get(j).get("bandnum").toString()) == 4)
							|| (Integer.parseInt(cellDataAref.get(j).get("bandnum").toString()) == 66)) {
						b0bandA.add(cellDataAref.get(j).get("bandnum").toString());
						b0cellA.add(cellDataAref.get(j).get("cellid").toString());
						b0TxA.add(cellDataAref.get(j).get("txd").toString());
						b0powerA.add(convert_dBm_to_W(cellDataAref.get(j).get("power").toString()));
						b0bwA.add(cellDataAref.get(j).get("bandwidth").toString());
						b0carrH.replace("inuse", "1");
						break;
					} else if ((Integer.parseInt(cellDataAref.get(j).get("bandnum").toString()) == 2)
							|| (Integer.parseInt(cellDataAref.get(j).get("bandnum").toString()) == 5)) {
						b1bandA.add(cellDataAref.get(j).get("bandnum").toString());
						b1cellA.add(cellDataAref.get(j).get("cellid").toString());
						b1TxA.add(cellDataAref.get(j).get("txd").toString());
						b1powerA.add(convert_dBm_to_W(cellDataAref.get(j).get("power").toString()));
						b1bwA.add(cellDataAref.get(j).get("bandwidth").toString());
						b1carrH.replace("inuse", "1");
						break;
					}
				}
			}
		}

		if (b0carrH.get("inuse").toString().equals("1")) {
			b0carrH.put("b0band", b0bandA);
			b0carrH.put("b0cell", b0cellA);
			b0carrH.put("b0Tx", b0TxA);
			b0carrH.put("b0power", b0powerA);
			b0carrH.put("b0bw", b0bwA);
		}

		if (b1carrH.get("inuse").toString().equals("1")) {
			b1carrH.put("b1band", b1bandA);
			b1carrH.put("b1cell", b1cellA);
			b1carrH.put("b1Tx", b1TxA);
			b1carrH.put("b1power", b1powerA);
			b1carrH.put("b1bw", b1bwA);
		}

		HashMap<String, Object> element = new HashMap<String, Object>();
		element.put("brd", brd);
		element.put("prt", prt);
		element.put("typeRC", rtype);
		element.put("b0obwRC", b0obw);
		element.put("b1obwRC", b1obw);
		element.put("tobwRC", tobw);
		element.put("b0_2TpowRC", b0_2Tpow);
		element.put("b1_2TpowRC", b1_2Tpow);
		element.put("b0_4TpowRC", b0_4Tpow);
		element.put("b1_4TpowRC", b1_4Tpow);
		element.put("tpowRC", tpow);
		element.put("b0carRC", b0car);
		element.put("b1carRC", b1car);
		element.put("tcarRC", tcar);
		element.put("b0carr", b0carrH);
		element.put("b1carr", b1carrH);

		return element;
	}

	private static String convert_dBm_to_W(String st) {
		Double dbm = (double) Integer.parseInt(st);
		dbm = dbm / 10;

		Double w = Math.pow(10, (dbm / 10)) / 1000;
		return w.toString();
	}

	private static void write_supportability_report(String site, String log) {
		String supportabilityrep = growfolder + "/SupportabilityReport_" + site + "_" + timestamp + ".txt";
		FileWriter fw = null;
		try {
			File f = new File(supportabilityrep);
			f.createNewFile();
			fw = new FileWriter(f, true);
			print_logs(log);
			fw.write(log);
		} catch (Exception e) {
			logger.error("Exception in USM Cell Grower " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		} finally {
			try {
				if (fw != null)
					fw.close();
			} catch (IOException e) {
				logger.error("Exception in USM Cell Grower " + ExceptionUtils.getFullStackTrace(e));
				e.printStackTrace();
			}
		}
	}

	private static void assignArbitrationIndex(ArrayList<ArrayList<TreeMap<String, Object>>> lccAref,
			ArrayList<TreeMap<String, Object>> cellDataAref) {
		for (int card = 0; card < 3; card++) {
			String expr;
			ArrayList<String> arbidxchk = new ArrayList<>();
			int[] cellsPerIdxCnt = { 0, 0, 0 };
			for (int i = 0; i < cellDataAref.size(); i++) {
				if (Integer.parseInt(cellDataAref.get(i).get("LCCnum").toString()) == card) {
					expr = cellDataAref.get(i).get("delay").toString() + ":"
							+ cellDataAref.get(i).get("prachcfgidx").toString();
					int idx = foundA(expr, arbidxchk);
					if (idx == -1) {
						arbidxchk.add(expr);
						idx = arbidxchk.size() - 1;
						cellsPerIdxCnt[idx]++;
					} else {
						cellsPerIdxCnt[idx]++;
					}
				}
			}
			for (int i = 0; i < cellDataAref.size(); i++) {
				if (Integer.parseInt(cellDataAref.get(i).get("LCCnum").toString()) == card) {
					for (int j = 0; j < arbidxchk.size(); j++) {
						String[] delayFormat = arbidxchk.get(j).split(":");
						if (cellDataAref.get(i).get("delay").toString().contains(delayFormat[0])
								&& cellDataAref.get(i).get("prachcfgidx").toString().contains(delayFormat[1])) {
							cellDataAref.get(i).put("arbidx", (new Integer(j)).toString());
						}
					}
				}
			}
			for (int dsp = 0; dsp < 3; dsp++) {
				if (arbidxchk.size() <= 1 || dsp == 0) {

					lccAref.get(card).get(dsp).put("arbidx", "0");
				} else if (arbidxchk.size() == 3) {
					lccAref.get(card).get(dsp).put("arbidx", dsp);
				} else {
					if (dsp == 1) {
						lccAref.get(card).get(dsp).put("arbidx", dsp);
					} else {
						if (cellsPerIdxCnt[0] > cellsPerIdxCnt[1]) {
							lccAref.get(card).get(dsp).put("arbidx", "0");
						} else {
							lccAref.get(card).get(dsp).put("arbidx", "1");
						}
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static void assign_MCtypeCandidatesToCells(ArrayList<ArrayList<TreeMap<String, Object>>> lccAref,
			ArrayList<TreeMap<String, Object>> cellDataAref) {
		// The MCTcandidates array contains all of the possible types that could support
		// the cell.
		for (int i = 0; i < cellDataAref.size(); i++) {
			ArrayList<String> types = new ArrayList<>();
			cellDataAref.get(i).put("MCTcandidates", types);
		}
		for (int t = 0; t < cellDataAref.size(); t++) {
			final int i = t;
			if (portnum1.equals(cellDataAref.get(i).get("port").toString())) {
				assignMCTtype6(cellDataAref.get(i), lccAref, cellDataAref);
			} else if (portnum2.equals(cellDataAref.get(i).get("port").toString())) {
				assignMCTtype6(cellDataAref.get(i), lccAref, cellDataAref);
			} else if (portnum3.equals(cellDataAref.get(i).get("port").toString())) {
				assignMCTtype6(cellDataAref.get(i), lccAref, cellDataAref);
			} else if (cellDataAref.get(i).get("port").toString().equals(lccnum1)
					&& cellDataAref.get(i).get("DspID").toString().equals(dspid1)) {
				assignMCTtype6(cellDataAref.get(i), lccAref, cellDataAref);
			} else {
				((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref.get(0).get(0).get("delay0"))
						.get("format0")).forEach((typ, data) -> {
							if (typ.contains("type")) {
								for (int j = 0; j < ((int[][][]) ((TreeMap<String, Object>) data)
										.get("resourceDef")).length; j++) {
									int foundbwboo = 0;
									for (int bw = 0; bw < ((int[][][]) ((TreeMap<String, Object>) data)
											.get("resourceDef"))[j][Integer.parseInt(
													cellDataAref.get(i).get("rvidx").toString())].length; bw++) {
										if (Integer.parseInt(cellDataAref.get(i).get("bandwidth")
												.toString()) == ((int[][][]) ((TreeMap<String, Object>) data)
														.get("resourceDef"))[j][Integer.parseInt(
																cellDataAref.get(i).get("rvidx").toString())][bw]) {
											foundbwboo = 1;
										}
									}
									if (foundbwboo == 1) {
										if (((TreeMap<String, Object>) data).get("numSup").toString().equals("4")) {
											((ArrayList<String>) cellDataAref.get(i).get("MCTcandidates")).add(typ);
											break;
										} else if (((TreeMap<String, Object>) data).get("numSup").toString().equals("6")
												&& cellDataAref.get(i).get("canUse6").toString().equals("1")) {
											int dspcnt = 0;
											int arbidxcnt = 0;
											ArrayList<Integer> cantUse6 = new ArrayList<>();
											ArrayList<Integer> use6 = new ArrayList<>();
											for (int k = 0; k < 3; k++) {
												if (lccAref
														.get(Integer
																.parseInt(cellDataAref.get(i).get("LCCnum").toString()))
														.get(k).get("arbidx").toString()
														.equals(cellDataAref.get(i).get("arbidx").toString())) {
													if (k + 1 > dspcnt)
														dspcnt++;
												}
											}
											for (int k = 0; k < cellDataAref.size(); k++) {
												if (cellDataAref.get(k).get("arbidx").toString()
														.equals(cellDataAref.get(i).get("arbidx").toString())
														&& cellDataAref.get(k).get("LCCnum").toString()
																.equals(cellDataAref.get(i).get("LCCnum").toString())) {
													arbidxcnt++;
													if (cellDataAref.get(k).get("canUse6").toString().equals("0")) {
														cantUse6.add(k);
													} else {
														use6.add(k);
													}
												}
											}
											if (dspcnt * 4 < arbidxcnt) {
												// We have more cells than we can support without using this type.
												if (cantUse6.size() > 0) {
													// We need to divide this Arbitration Index group.

													int newIdx = 0;
													if (dspcnt == 3) {
														newIdx = 1;
														lccAref.get(Integer
																.parseInt(cellDataAref.get(i).get("LCCnum").toString()))
																.get(1).replace("arbidx", newIdx);
														if (cantUse6.size() <= 4) {
															lccAref.get(Integer.parseInt(
																	cellDataAref.get(i).get("LCCnum").toString()))
																	.get(2).replace("arbidx", newIdx);
														}
													}
													if (dspcnt == 2) {
														newIdx = 2;
														lccAref.get(Integer
																.parseInt(cellDataAref.get(i).get("LCCnum").toString()))
																.get(2).replace("arbidx", newIdx);
													}
													if (dspcnt == 1) {
														print_logs("unsupported configuration");
														// System.exit(0);
													}
													for (int k = 0; k < use6.size(); k++) {
														cellDataAref.get(use6.get(k)).replace("arbidx", newIdx);
													}
												}
												((ArrayList<String>) cellDataAref.get(i).get("MCTcandidates")).add(typ);
												// System.out.println("size : " +
												// ((ArrayList<String>)cellDataAref.get(i).get("MCTcandidates")).size());
												break;
											}
										}
									}
								}
							}
						});
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static void assignMCTtype6(TreeMap<String, Object> cellData,
			ArrayList<ArrayList<TreeMap<String, Object>>> lccAref, ArrayList<TreeMap<String, Object>> cellDataAref) {

		final int[] length = { 0 };

		((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref.get(0).get(0).get("delay0")).get("format0"))
				.forEach((typ, data) -> {
					String numsup;
					String mctdata;
					if (typ.contains("type")) {
						mctdata = ((TreeMap<String, Object>) data).get("typename").toString();
						numsup = ((TreeMap<String, Object>) data).get("numSup").toString();

						if ((bandwidth1.equals("10") || bandwidth1.equals("5"))
								&& (cellData.get("bandwidth").toString().equals("10")
										|| cellData.get("bandwidth").toString().equals("5"))
								&& numsup.contains("6")) {
							if (mctdata.contains("cfg61-multi-carrier-10m-5m-6cell")) {
								((ArrayList<String>) cellData.get("MCTcandidates")).add(typ);
								length[0] = 1;
							}
						}

				else if ((bandwidth1.equals("20") || bandwidth1.equals("15"))
						&& (cellData.get("bandwidth").toString().equals("15")
								|| cellData.get("bandwidth").toString().equals("20"))
						&& numsup.contains("6")) {
							if (mctdata.contains("cfg63-multi-carrier-20m-2t2r-6cell")) {
								((ArrayList<String>) cellData.get("MCTcandidates")).add(typ);
								length[0] = 1;
							}
						}
					}
				});

		if (length[0] == 0) {
			((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref.get(0).get(0).get("delay0")).get("format0"))
					.forEach((typ, data) -> {
						if (typ.contains("type")) {
							for (int j = 0; j < ((int[][][]) ((TreeMap<String, Object>) data)
									.get("resourceDef")).length; j++) {
								int foundbwboo = 0;
								for (int bw = 0; bw < ((int[][][]) ((TreeMap<String, Object>) data)
										.get("resourceDef"))[j][Integer
												.parseInt(cellData.get("rvidx").toString())].length; bw++) {
									if (Integer.parseInt(cellData.get("bandwidth")
											.toString()) == ((int[][][]) ((TreeMap<String, Object>) data)
													.get("resourceDef"))[j][Integer
															.parseInt(cellData.get("rvidx").toString())][bw]) {
										foundbwboo = 1;
									}
								}
								if (foundbwboo == 1) {
									if (((TreeMap<String, Object>) data).get("numSup").toString().equals("4")) {
										((ArrayList<String>) cellData.get("MCTcandidates")).add(typ);
										break;
									} else if (((TreeMap<String, Object>) data).get("numSup").toString().equals("6")
											&& cellData.get("canUse6").toString().equals("1")) {
										int dspcnt = 0;
										int arbidxcnt = 0;
										ArrayList<Integer> cantUse6 = new ArrayList<>();
										ArrayList<Integer> use6 = new ArrayList<>();
										for (int k = 0; k < 3; k++) {
											if (lccAref.get(Integer.parseInt(cellData.get("LCCnum").toString())).get(k)
													.get("arbidx").toString()
													.equals(cellData.get("arbidx").toString())) {
												if (k + 1 > dspcnt)
													dspcnt++;
											}
										}
										for (int k = 0; k < cellDataAref.size(); k++) {
											if (cellDataAref.get(k).get("arbidx").toString()
													.equals(cellData.get("arbidx").toString())
													&& cellDataAref.get(k).get("LCCnum").toString()
															.equals(cellData.get("LCCnum").toString())) {
												arbidxcnt++;
												if (cellDataAref.get(k).get("canUse6").toString().equals("0")) {
													cantUse6.add(k);
												} else {
													use6.add(k);
												}
											}
										}
										if (dspcnt * 4 < arbidxcnt) {
											// We have more cells than we can support without using this type.
											if (cantUse6.size() > 0) {
												// We need to divide this Arbitration Index group.

												int newIdx = 0;
												if (dspcnt == 3) {
													newIdx = 1;
													lccAref.get(Integer.parseInt(cellData.get("LCCnum").toString()))
															.get(1).replace("arbidx", newIdx);
													if (cantUse6.size() <= 4) {
														lccAref.get(Integer.parseInt(cellData.get("LCCnum").toString()))
																.get(2).replace("arbidx", newIdx);
													}
												}
												if (dspcnt == 2) {
													newIdx = 2;
													lccAref.get(Integer.parseInt(cellData.get("LCCnum").toString()))
															.get(2).replace("arbidx", newIdx);
												}
												if (dspcnt == 1) {
													print_logs("unsupported configuration");
													// System.exit(0);
												}
												for (int k = 0; k < use6.size(); k++) {
													cellDataAref.get(use6.get(k)).replace("arbidx", newIdx);
												}
											}
											((ArrayList<String>) cellData.get("MCTcandidates")).add(typ);
											// System.out.println("size : " +
											// ((ArrayList<String>)cellDataAref.get(i).get("MCTcandidates")).size());
											break;
										}
									}
								}
							}
						}
					});
		}
	}

	@SuppressWarnings({ "unchecked" })
	private static boolean mapDSPsToCells(ArrayList<ArrayList<TreeMap<String, Object>>> lccAref,
			ArrayList<TreeMap<String, Object>> cellDataAref, String node) {
		int typecnt = ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref.get(0).get(0).get("delay0"))
				.get("format0")).size();
		ArrayList<Integer> rotatingDSP = new ArrayList<Integer>();
		rotatingDSP.add(0);
		rotatingDSP.add(1);
		rotatingDSP.add(2);

		ArrayList<Integer> rotatingCell = new ArrayList<Integer>();
		ArrayList<ArrayList<Integer>> dspAnchorsA = new ArrayList<>();

		assignAnchors(lccAref, cellDataAref, dspAnchorsA);
		for (int i = 0; i < cellDataAref.size(); i++) {
			rotatingCell.add(i);
		}

		int resourceconflictboo = 0;
		int breakitup = 0;

		for (int tcnt = 0; tcnt < (typecnt * typecnt) + 10; tcnt++) {
			allocateAnchors(lccAref, cellDataAref);
			for (int li = 0; li < lccAref.size(); li++) {
				ArrayList<Integer> numberOfCarriers = new ArrayList<Integer>();
				ArrayList<Integer> refsTocellsOnCard = new ArrayList<Integer>();
				for (int i = 0; i < cellDataAref.size(); i++) {
					if (Integer.parseInt(cellDataAref.get(i).get("LCCnum").toString()) == li) {
						refsTocellsOnCard.add(i);
					}
				}

				for (int i = 0; i < refsTocellsOnCard.size() - 1; i++) {
					if (numberOfCarriers.size() == 0) {
						numberOfCarriers.add(
								Integer.parseInt(cellDataAref.get(refsTocellsOnCard.get(i)).get("carrid").toString()));
					} else if (foundintA(
							Integer.parseInt(cellDataAref.get(refsTocellsOnCard.get(i + 1)).get("carrid").toString()),
							numberOfCarriers) == -1) {
						numberOfCarriers.add(Integer
								.parseInt(cellDataAref.get(refsTocellsOnCard.get(i + 1)).get("carrid").toString()));
					}
				}

				for (int di = 0; di < (lccAref.get(li)).size(); di++) {
					for (int ci = 0; ci < cellDataAref.size(); ci++) {
						if (lccAref.get(li).get(di).get("arbidx").toString()
								.equals(cellDataAref.get(rotatingCell.get(ci)).get("arbidx").toString())) {
							if ((cellDataAref.get(rotatingCell.get(ci)).get("dspid").toString().equals("-1"))
									&& (Integer.parseInt(
											cellDataAref.get(rotatingCell.get(ci)).get("LCCnum").toString()) == li)) {
								String dlay = cellDataAref.get(rotatingCell.get(ci)).get("delay").toString();
								String fmat = cellDataAref.get(rotatingCell.get(ci)).get("prachcfgidx").toString();

								int reachedMaxPerCarboo = 0;

								for (int car = 0; car < (((int[][]) ((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
										.get(li).get(di).get(dlay)).get(fmat)).get("carrierLimits"))
												.get("num_supPerCar"))[0]).length; car++) {
									if (Integer.parseInt(cellDataAref.get(rotatingCell.get(ci)).get("bandnum")
											.toString()) == ((int[][]) ((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
													.get(li).get(di).get(dlay)).get(fmat)).get("carrierLimits"))
															.get("num_supPerCar"))[0][car]) {
										int maxforthiscarrier = ((int[][]) ((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
												.get(li).get(di).get(dlay)).get(fmat)).get("carrierLimits"))
														.get("num_supPerCar"))[1][car];
										int currentnumthiscar = ((int[][]) ((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
												.get(li).get(di).get(dlay)).get(fmat)).get("carrierLimits"))
														.get("numcellsPerCar"))[1][car];
										if (maxforthiscarrier == currentnumthiscar) {
											reachedMaxPerCarboo = 1;
										}
									}
								}

								if (reachedMaxPerCarboo == 0) {
									int successboo = 0;
									for (int ti = 0; ti < ((ArrayList<String>) cellDataAref.get(rotatingCell.get(ci))
											.get("MCTcandidates")).size(); ti++) {
										String typ = ((ArrayList<String>) cellDataAref.get(rotatingCell.get(ci))
												.get("MCTcandidates")).get(ti);
										if (((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
												.get(li).get(di).get(dlay)).get(fmat)).get(typ)).get("tstatus")
														.toString().contains("unlocked")) {
											for (int dj = 0; dj < ((int[][][]) ((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
													.get(li).get(di).get(dlay)).get(fmat)).get(typ))
															.get("resourceDef")).length; dj++) {
												int foundbwboo = 0;
												int idx = Integer.parseInt(
														cellDataAref.get(rotatingCell.get(ci)).get("rvidx").toString());
												for (int bw = 0; bw < ((int[][][]) ((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
														.get(li).get(di).get(dlay)).get(fmat)).get(typ))
																.get("resourceDef"))[dj][idx].length; bw++) {
													if (Integer.parseInt(cellDataAref.get(rotatingCell.get(ci))
															.get("bandwidth")
															.toString()) == ((int[][][]) ((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
																	.get(li).get(di).get(dlay)).get(fmat)).get(typ))
																			.get("resourceDef"))[dj][idx][bw]
															&& ((int[]) ((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
																	.get(li).get(di).get(dlay)).get(fmat)).get(typ))
																			.get("resourceTrack"))[dj] == 0) {
														foundbwboo = 1;
													}
												}

												if (foundbwboo == 1 && (cellDataAref
														.get(Integer.parseInt(
																lccAref.get(li).get(di).get("anchor").toString()))
														.get("carrid").toString()
														.equals(cellDataAref.get(rotatingCell.get(ci)).get("carrid")
																.toString())
														|| resourceconflictboo == 1
														|| cellDataAref.get(rotatingCell.get(ci)).get("anchorboo")
																.toString().equals("0")
														|| reachedMaxPerCarboo == 1)) {
													successboo = 1;
													if (((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
															.get(li).get(di).get(dlay)).get(fmat)).get(typ))
																	.get("numcells").toString().equals("0")) {
														offlineAllDspResourcesExcept(lccAref, li, di, dlay, fmat, typ);
													}
													cellDataAref.get(rotatingCell.get(ci)).replace("carrierInDSPid",
															((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
																	.get(li).get(di).get(dlay)).get(fmat)).get(typ))
																			.get("numcells").toString());
													int alurrh = 0;
													int aslrrh = 1;
													int samrrh = 2;
													// Prevent Samsung and ALU/ASL cells from sharing a DSP:
													if (((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
															.get(li).get(di).get(dlay)).get(fmat)).get(typ))
																	.get("numcells").toString().equals("0")) {
														if (Integer.parseInt(cellDataAref
																.get(Integer.parseInt(lccAref.get(li).get(di)
																		.get("anchor").toString()))
																.get("rvidx").toString()) == 2) {
															claimExclusivityFrom(
																	((int[][][]) ((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
																			.get(li).get(di).get(dlay)).get(fmat))
																					.get(typ)).get("resourceDef")),
																	alurrh);
															claimExclusivityFrom(
																	((int[][][]) ((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
																			.get(li).get(di).get(dlay)).get(fmat))
																					.get(typ)).get("resourceDef")),
																	aslrrh);
														} else {
															claimExclusivityFrom(
																	((int[][][]) ((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
																			.get(li).get(di).get(dlay)).get(fmat))
																					.get(typ)).get("resourceDef")),
																	samrrh);
														}
													}

													((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
															.get(li).get(di).get(dlay)).get(fmat)).get(typ)).replace(
																	"numcells",
																	(Integer.parseInt(
																			((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
																					.get(li).get(di).get(dlay))
																							.get(fmat)).get(typ))
																									.get("numcells")
																									.toString())
																			+ 1));

													if (((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
															.get(li).get(di).get(dlay)).get(fmat)).get(typ))
																	.get("numcells").toString()
																	.equals(((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
																			.get(li).get(di).get(dlay)).get(fmat))
																					.get(typ)).get("numSup")
																							.toString())) {
														((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
																.get(li).get(di).get(dlay)).get(fmat)).get(typ))
																		.replace("tstatus", "LOCKED");
														// System.out.println(((TreeMap<String,Object>)((TreeMap<String,Object>)((TreeMap<String,Object>)lccAref.get(li).get(di).get(dlay)).get(fmat)).get(typ)).get("tstatus"));
													}

													cellDataAref.get(rotatingCell.get(ci)).replace("dspid", di);
													cellDataAref.get(rotatingCell.get(ci)).replace("multict",
															((ArrayList<String>) cellDataAref.get(rotatingCell.get(ci))
																	.get("MCTcandidates")).get(ti));

													((int[]) ((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
															.get(li).get(di).get(dlay)).get(fmat)).get(typ))
																	.get("resourceTrack"))[dj] = 1;

													for (int car = 0; car < ((int[][]) ((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
															.get(li).get(di).get(dlay)).get(fmat)).get("carrierLimits"))
																	.get("num_supPerCar"))[0].length; car++) {
														if (((int[][]) ((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
																.get(li).get(di).get(dlay)).get(fmat))
																		.get("carrierLimits")).get(
																				"num_supPerCar"))[0][car] == Integer
																						.parseInt(cellDataAref
																								.get(rotatingCell
																										.get(ci))
																								.get("bandnum")
																								.toString())) {
															((int[][]) ((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
																	.get(li).get(di).get(dlay)).get(fmat))
																			.get("carrierLimits"))
																					.get("numcellsPerCar"))[1][car]++;
															break;
														}
													}
												}
												if (successboo == 1) {
													break;
												}
											}
										}
										if (successboo == 1) {
											break;
										}
									}
								}
							}
						}
					}
				}
			}

			// We've just allowed the cells to grab resources in round robin fashion, but we
			// could end up in the
			// situation in which the multi carrier type chosen doesn't have enough
			// resources to cover other cells,
			// and there is another type that could just as easily be chosen that does have
			// enough resources. The
			// primary example would be choosing 20-20-20-10-5, and ending up with a 15Mhz
			// cell hung
			// out to dry because we should have chosen 20-20-15-15-10-5. So we need to
			// check to see if there are
			// any situations like that, and handle them by switching the type and running
			// the map again.

			resourceconflictboo = 0;
			/*
			 * TreeMap<String, Object> cda = new TreeMap<String, Object>(); for (int ciad =
			 * 0; ciad < cellDataAref.size(); ciad++) { cda = cellDataAref.get(ciad); int
			 * dsp =Integer.parseInt((String)cda.get("DspID"))-1; if
			 * (cellDataAref.get(ciad).get("dspid").toString().equals("-1")) {
			 * cellDataAref.get(rotatingCell.get(ciad)).replace("dspid", cda.get("DspID"));
			 * cellDataAref.get(rotatingCell.get(ciad)).replace("multict",
			 * ((ArrayList<String>) cellDataAref.get(rotatingCell.get(ciad))
			 * .get("MCTcandidates")).get(0)); } }
			 */
			ArrayList<String> resourceToFree = new ArrayList<>();
			ArrayList<String> ch1 = new ArrayList<>();
			ArrayList<String> ch2 = new ArrayList<>();
			ArrayList<String> ch3 = new ArrayList<>();
			ArrayList<String> chex1 = new ArrayList<>();
			chex1.add("00");
			chex1.add("01");
			chex1.add("02");
			chex1.add("10");
			chex1.add("11");
			chex1.add("12");
			chex1.add("20");
			chex1.add("21");
			chex1.add("22");
			chex1.add("30");
			chex1.add("31");
			chex1.add("32");
			for (int cia = 0; cia < cellDataAref.size(); cia++) {
				if (cellDataAref.get(cia).get("cpriPortAssignment").toString().contains("LCC-1")) {
					ch1.add(cellDataAref.get(cia).get("carrierInDSPid").toString()
							+ cellDataAref.get(cia).get("dspid").toString());
				} else if (cellDataAref.get(cia).get("cpriPortAssignment").toString().contains("LCC-2")) {
					ch2.add(cellDataAref.get(cia).get("carrierInDSPid").toString()
							+ cellDataAref.get(cia).get("dspid").toString());
				} else if (cellDataAref.get(cia).get("cpriPortAssignment").toString().contains("LCC-3")) {
					ch3.add(cellDataAref.get(cia).get("carrierInDSPid").toString()
							+ cellDataAref.get(cia).get("dspid").toString());
				}
				if (cellDataAref.get(cia).get("dspid").toString().equals("-1")) {
					if (enbidA.get(enbidx) == 73244) {
						System.out.println("Testing");
						resourceconflictboo = 0;
					} else {
						int flag = 0;
						for (String ch : chex1) {
							if (!ch1.contains(ch) && flag != 1
									&& cellDataAref.get(cia).get("cpriPortAssignment").toString().contains("LCC-1")) {
								cellDataAref.get(cia).replace("carrierInDSPid",
										Character.getNumericValue(ch.charAt(0)));
								cellDataAref.get(cia).replace("dspid", Character.getNumericValue(ch.charAt(1)));
								if (cellDataAref.get(cia).get("band").toString().contains("BAND48")) {
									cellDataAref.get(cia).replace("multict", "type6");
								} else if (cellDataAref.get(cia).get("band").toString().contains("BAND46")) {
									cellDataAref.get(cia).replace("multict", "type6");
								}
								ch1.set(ch1.indexOf("-1-1"), ch);
								flag = 1;
							} else if (!ch2.contains(ch) && flag != 1
									&& cellDataAref.get(cia).get("cpriPortAssignment").toString().contains("LCC-2")) {
								cellDataAref.get(cia).replace("carrierInDSPid",
										Character.getNumericValue(ch.charAt(0)));
								cellDataAref.get(cia).replace("dspid", Character.getNumericValue(ch.charAt(1)));
								if (cellDataAref.get(cia).get("band").toString().contains("BAND48")) {
									cellDataAref.get(cia).replace("multict", "type6");
								} else if (cellDataAref.get(cia).get("band").toString().contains("BAND46")) {
									cellDataAref.get(cia).replace("multict", "type6");
								}
								ch2.set(ch2.indexOf("-1-1"), ch);
								flag = 1;
							} else if (!ch3.contains(ch) && flag != 1
									&& cellDataAref.get(cia).get("cpriPortAssignment").toString().contains("LCC-3")) {
								cellDataAref.get(cia).replace("carrierInDSPid",
										Character.getNumericValue(ch.charAt(0)));
								cellDataAref.get(cia).replace("dspid", Character.getNumericValue(ch.charAt(1)));
								if (cellDataAref.get(cia).get("band").toString().contains("BAND48")) {
									cellDataAref.get(cia).replace("multict", "type6");
								} else if (cellDataAref.get(cia).get("band").toString().contains("BAND46")) {
									cellDataAref.get(cia).replace("multict", "type6");
								}
								ch3.set(ch3.indexOf("-1-1"), ch);
								flag = 1;
							}
						}
					}

					int card = Integer.parseInt(cellDataAref.get(cia).get("LCCnum").toString());
					for (int dia = 0; dia < lccAref.get(card).size(); dia++) {
						ArrayList<Integer> srchcellsA = new ArrayList<>();
						ArrayList<Integer> foundcellsA = new ArrayList<>();

						for (int cib = 0; cib < cellDataAref.size(); cib++) {
							if (Integer.parseInt(cellDataAref.get(cib).get("dspid").toString()) == rotatingDSP.get(dia)
									&& Integer.parseInt(cellDataAref.get(cib).get("LCCnum").toString()) == card
									&& cellDataAref.get(cib).get("arbidx").toString()
											.equals(cellDataAref.get(cia).get("arbidx").toString())
									&& cellDataAref.get(cib).get("arbidx").toString().equals(
											lccAref.get(card).get(rotatingDSP.get(dia)).get("arbidx").toString())) {
								srchcellsA.add(cib);
							}
						}

						// If the cells in the list match one of my types, see if they can use another
						// one of my types instead.
						for (int ccnt = 0; ccnt < srchcellsA.size(); ccnt++) {
							for (int cct = 0; cct < ((ArrayList<String>) cellDataAref.get(srchcellsA.get(ccnt))
									.get("MCTcandidates")).size(); cct++) {
								for (int cand = 0; cand < ((ArrayList<String>) cellDataAref.get(cia)
										.get("MCTcandidates")).size(); cand++) {
									String MCTweMightRatherUse = ((ArrayList<String>) cellDataAref
											.get(srchcellsA.get(ccnt)).get("MCTcandidates")).get(cct);
									String MCTforCellNotAllocated = ((ArrayList<String>) cellDataAref.get(cia)
											.get("MCTcandidates")).get(cand);
									String MCTforCellAlreadyAllocatedOnDSP = cellDataAref.get(srchcellsA.get(ccnt))
											.get("multict").toString();

									if (MCTweMightRatherUse.contains(MCTforCellNotAllocated)
											&& ((!MCTweMightRatherUse.contains(MCTforCellAlreadyAllocatedOnDSP))
													|| breakitup == 1)) {
										if (foundA(card + ":" + rotatingDSP.get(dia), resourceToFree) == -1) {
											if (foundintA(srchcellsA.get(ccnt), foundcellsA) == -1) {
												foundcellsA.add(srchcellsA.get(ccnt));
											}
										}
									}
								}
							}
						}

						if (srchcellsA.size() == foundcellsA.size() && (srchcellsA.size() != 0)) {
							if (foundA(card + ":" + rotatingDSP.get(dia), resourceToFree) == -1) {
								if (resourceToFree.size() == 0) {
									resourceToFree.add(card + ":" + rotatingDSP.get(dia));
								}
							}
							break;
						}
					}
				}
			}

			print_cellDataA(lccAref, cellDataAref,
					"\n\nMapping Routine iteration " + tcnt + " " + node + ": \n_______________________________");
			print_lccA(lccAref, cellDataAref,
					"\n\nMapping Routine iteration " + tcnt + " " + node + ": \n_______________________________");

			if (resourceconflictboo == 1 && (resourceToFree.size() == 0)) {
				// We must be in a situation in which all of the cells in the arbitration group
				// are assigned the same MCT.
				breakitup = 1; // 7/22/2018: breakitup is a leftover from before Anchor cells. It can probably
								// be removed and a full regression run.
			} else {
				breakitup = 0;
			}
			for (int i = 0; i < resourceToFree.size(); i++) {
				String[] carddsp = resourceToFree.get(i).split(":");
				print_logs("Freeing card " + carddsp[0] + ", dsp " + carddsp[1]);
				// freeAllDspResources(lccAref, cellDataAref, carddsp[0], carddsp[1]);
			}

			if (resourceconflictboo == 0) {
				break;
			}

			// For configurations requiring a large number of iterations it is better to
			// loop through @lccA in
			// a circular fashion with each iteration (i.e. the first iteration will loop
			// through @lccA[0], [1], [2].
			// The 2nd [1], [2], [0] and so on. Otherwise DSP0 cells get de-allocated much
			// more frequently than
			// those on DSP1, and DSP2 can go many iterations without getting touched. If
			// DSP2 is causing the
			// contention, it's better to get to it quickly by rotating the starting point
			// for the loop through DSPs.

			int rotate = rotatingDSP.remove(rotatingDSP.size() - 1);
			rotatingDSP.add(0, rotate);
			rotate = rotatingCell.remove(rotatingCell.size() - 1);
			rotatingCell.add(0, rotate);
		}
		TreeMap<String, Object> cda = new TreeMap<String, Object>();
		ArrayList<Integer> rotatingCells = new ArrayList<Integer>();
		for (int l = 0; l < cellDataAref.size(); l++) {
			rotatingCells.add(l);
		}
		for (int ciad = 0; ciad < cellDataAref.size(); ciad++) {
			cda = cellDataAref.get(ciad);
			if (!cda.get("DspID").equals("")) {
				int dsp = Integer.parseInt((String) cda.get("DspID")) - 1;
				if (cellDataAref.get(ciad).get("dspid").toString().equals("-1")) {
					cellDataAref.get(rotatingCells.get(ciad)).replace("dspid", cda.get("DspID"));
					if (cellDataAref.get(ciad).get("bandwidth").toString().equals("20")) {
						cellDataAref.get(rotatingCells.get(ciad)).replace("multict", "type5");
					} else {
						cellDataAref.get(rotatingCells.get(ciad)).replace("multict",
								((ArrayList<String>) cellDataAref.get(rotatingCell.get(ciad)).get("MCTcandidates"))
										.get(0));
					}
				}
			}
		}
		for (int ciad = 0; ciad < cellDataAref.size(); ciad++) {
			cda = cellDataAref.get(ciad);
			if (cda.get("multict").toString().isEmpty()) {
				cellDataAref.get(rotatingCells.get(ciad)).replace("multict",
						((ArrayList<String>) cellDataAref.get(rotatingCell.get(ciad)).get("MCTcandidates")).get(0));
			}
		}
		if (resourceconflictboo == 1) {
			for (int i = 0; i < cellDataAref.size(); i++) {
				if (cellDataAref.get(i).get("dspid").toString().equals("-1")) {
					System.out.println("\nERROR: Could Not Place Cell " + cellDataAref.get(i).get("sectid").toString()
							+ " " + cellDataAref.get(i).get("carrid").toString());
					return false;
				} else {
					resourceconflictboo = 0;
				}
			}
		}

		if (resourceconflictboo == 0) {
			for (int i = 0; i < cellDataAref.size(); i++) {
				if (!cellDataAref.get(i).get("multict").toString().equals(""))
					cellDataAref.get(i).put("typename",
							((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
									.get(0).get(0).get("delay0")).get("format0"))
											.get(cellDataAref.get(i).get("multict").toString())).get("typename")
													.toString());
			}

			print_inputForGrowTemplate(lccAref, cellDataAref, node);
			return true;

		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private static void allocateAnchors(ArrayList<ArrayList<TreeMap<String, Object>>> lccAref,
			ArrayList<TreeMap<String, Object>> cellDataAref) {
		int alurrh = 0;
		int aslrrh = 1;
		int samrrh = 2;
		for (int li = 0; li < lccAref.size(); li++) {
			for (int di = 0; di < lccAref.get(li).size(); di++) {
				if (Integer.parseInt(lccAref.get(li).get(di).get("anchor").toString()) != -1) {
					if (cellDataAref.get(Integer.parseInt(lccAref.get(li).get(di).get("anchor").toString()))
							.get("multict").toString().equals("")) {
						String dlay = cellDataAref
								.get(Integer.parseInt(lccAref.get(li).get(di).get("anchor").toString())).get("delay")
								.toString();
						String fmt = cellDataAref
								.get(Integer.parseInt(lccAref.get(li).get(di).get("anchor").toString()))
								.get("prachcfgidx").toString();
						String typ = ((ArrayList<String>) cellDataAref
								.get(Integer.parseInt(lccAref.get(li).get(di).get("anchor").toString()))
								.get("MCTcandidates"))
										.get(Integer.parseInt(lccAref.get(li).get(di).get("anchorMCTidx").toString()));

						for (int dj = 0; dj < ((int[][][]) ((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
								.get(li).get(di).get(dlay)).get(fmt)).get(typ)).get("resourceDef")).length; dj++) {
							int idx = Integer.parseInt(
									cellDataAref.get(Integer.parseInt(lccAref.get(li).get(di).get("anchor").toString()))
											.get("rvidx").toString());
							boolean foundbwboo = false;

							for (int dk = 0; dk < ((int[][][]) ((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
									.get(li).get(di).get(dlay)).get(fmt)).get(typ))
											.get("resourceDef"))[dj][idx].length; dk++) {
								if (((int[][][]) ((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
										.get(li).get(di).get(dlay)).get(fmt)).get(typ))
												.get("resourceDef"))[dj][idx][dk] == Integer
														.parseInt(cellDataAref
																.get(Integer.parseInt(lccAref.get(li).get(di)
																		.get("anchor").toString()))
																.get("bandwidth").toString())) {
									foundbwboo = true;
								}
							}
							if (foundbwboo
									&& ((int[]) ((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
											.get(li).get(di).get(dlay)).get(fmt)).get(typ))
													.get("resourceTrack"))[dj] == 0) {
								if (((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
										.get(li).get(di).get(dlay)).get(fmt)).get(typ)).get("numcells").toString()
												.equals("0")) {
									offlineAllDspResourcesExcept(lccAref, li, di, dlay, fmt, typ);
								}

								cellDataAref.get(Integer.parseInt(lccAref.get(li).get(di).get("anchor").toString()))
										.replace("carrierInDSPid",
												((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
														.get(li).get(di).get(dlay)).get(fmt)).get(typ)).get("numcells")
																.toString());

								// This prevents Samsung and ALU/ASL radios from sharing a DSP
								if (((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
										.get(li).get(di).get(dlay)).get(fmt)).get(typ)).get("numcells").toString()
												.equals("0")) {
									if (Integer.parseInt(cellDataAref
											.get(Integer.parseInt(lccAref.get(li).get(di).get("anchor").toString()))
											.get("rvidx").toString()) == 2) {
										claimExclusivityFrom(
												((int[][][]) ((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
														.get(li).get(di).get(dlay)).get(fmt)).get(typ))
																.get("resourceDef")),
												alurrh);
										claimExclusivityFrom(
												((int[][][]) ((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
														.get(li).get(di).get(dlay)).get(fmt)).get(typ))
																.get("resourceDef")),
												aslrrh);
									} else {
										claimExclusivityFrom(
												((int[][][]) ((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
														.get(li).get(di).get(dlay)).get(fmt)).get(typ))
																.get("resourceDef")),
												samrrh);
									}
								}

								// System.out.println((Integer.parseInt(((TreeMap<String,Object>)((TreeMap<String,Object>)((TreeMap<String,Object>)lccAref.get(li).get(di).get(dlay)).get(fmt)).get(typ)).get("numcells").toString())));
								((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
										.get(li).get(di).get(dlay)).get(fmt)).get(typ)).replace(
												"numcells",
												(Integer.parseInt(
														((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
																.get(li).get(di).get(dlay)).get(fmt)).get(typ))
																		.get("numcells").toString())
														+ 1));
								// System.out.println((Integer.parseInt(((TreeMap<String,Object>)((TreeMap<String,Object>)((TreeMap<String,Object>)lccAref.get(li).get(di).get(dlay)).get(fmt)).get(typ)).get("numcells").toString())));

								if (((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
										.get(li).get(di).get(dlay)).get(fmt)).get(typ))
												.get("numcells").toString()
												.equals(((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
														.get(li).get(di).get(dlay)).get(fmt)).get(typ)).get("numSup")
																.toString())) {
									((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
											.get(li).get(di).get(dlay)).get(fmt)).get(typ)).replace("tstatus",
													"LOCKED");
									// System.out.println(((TreeMap<String,Object>)((TreeMap<String,Object>)((TreeMap<String,Object>)lccAref.get(li).get(di).get(dlay)).get(fmt)).get(typ)).get("tstatus"));
								}

								cellDataAref.get(Integer.parseInt(lccAref.get(li).get(di).get("anchor").toString()))
										.replace("dspid", di);
								cellDataAref.get(Integer.parseInt(lccAref.get(li).get(di).get("anchor").toString()))
										.replace("multict", ((ArrayList<String>) cellDataAref
												.get(Integer.parseInt(lccAref.get(li).get(di).get("anchor").toString()))
												.get("MCTcandidates")).get(Integer.parseInt(
														lccAref.get(li).get(di).get("anchorMCTidx").toString())));

								((int[]) ((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
										.get(li).get(di).get(dlay)).get(fmt)).get(typ)).get("resourceTrack"))[dj] = 1;

								for (int car = 0; car < ((int[][]) ((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
										.get(li).get(di).get(dlay)).get(fmt)).get("carrierLimits"))
												.get("num_supPerCar"))[0].length; car++) {
									if (((int[][]) ((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
											.get(li).get(di).get(dlay)).get(fmt)).get("carrierLimits"))
													.get("num_supPerCar"))[0][car] == Integer
															.parseInt(cellDataAref
																	.get(Integer.parseInt(lccAref.get(li).get(di)
																			.get("anchor").toString()))
																	.get("bandnum").toString())) {
										((int[][]) ((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
												.get(li).get(di).get(dlay)).get(fmt)).get("carrierLimits"))
														.get("numcellsPerCar"))[1][car]++;
									}
								}
								if (Integer.parseInt(lccAref.get(li).get(di).get("anchorMCTidx")
										.toString()) == (((ArrayList<String>) cellDataAref
												.get(Integer.parseInt(lccAref.get(li).get(di).get("anchor").toString()))
												.get("MCTcandidates")).size() - 1)) {
									lccAref.get(li).get(di).replace("anchorMCTidx", "0");
								} else {
									lccAref.get(li).get(di).replace("anchorMCTidx",
											(Integer.parseInt(lccAref.get(li).get(di).get("anchorMCTidx").toString())
													+ 1));
								}
								break;
							}
						}
					}
				}
			}
		}
	}

	private static void claimExclusivityFrom(int[][][] resourceDefRef, int vendIdx) {
		for (int i = 0; i < resourceDefRef.length; i++) {
			for (int j = 0; j < resourceDefRef[i][vendIdx].length; j++) {
				resourceDefRef[i][vendIdx][j] = 0;
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static void offlineAllDspResourcesExcept(ArrayList<ArrayList<TreeMap<String, Object>>> lccAref, int lcc,
			int dsp, String dly, String prach, String typ) {
		lccAref.get(lcc).get(dsp).forEach((i, data1) -> {
			if (!(i.contains("arbidx") || i.contains("anchor"))) {
				((TreeMap<String, Object>) data1).forEach((j, data2) -> {
					((TreeMap<String, Object>) data2).forEach((k, data3) -> {
						if (k.contains("type")) {
							((TreeMap<String, Object>) data3).replace("tstatus", "offline");
						}
					});
				});
			}
		});

		((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref.get(lcc).get(dsp)
				.get(dly)).get(prach)).get(typ)).replace("tstatus", "unlocked");
	}

	private static void assignAnchors(ArrayList<ArrayList<TreeMap<String, Object>>> lccAref,
			ArrayList<TreeMap<String, Object>> cellDataAref, ArrayList<ArrayList<Integer>> anchorsAref) {
		for (int li = 0; li < lccAref.size(); li++) {
			int mycontinue = 0;
			for (int i = 0; i < cellDataAref.size(); i++) {
				if (Integer.parseInt(cellDataAref.get(i).get("LCCnum").toString()) == li) {
					mycontinue = 1;
					break;
				}
			}

			if (mycontinue == 0) {
				continue;
			}

			ArrayList<Integer> existingAnchorsA = new ArrayList<Integer>();

			for (int di = 0; di < lccAref.get(li).size(); di++) {
				ArrayList<Integer> potentialAnchorsA = new ArrayList<>();
				for (int ci = 0; ci < cellDataAref.size(); ci++) {
					if ((Integer.parseInt(cellDataAref.get(ci).get("LCCnum").toString()) == li) && (cellDataAref.get(ci)
							.get("arbidx").toString().equals(lccAref.get(li).get(di).get("arbidx").toString()))) {
						// System.out.println(ci);
						// System.out.println(existingAnchorsA);
						if (foundintA(ci, existingAnchorsA) == -1) {
							potentialAnchorsA.add(ci);
						}
					}
				}
				// int temp =1;
				int anchor = -1;
				ArrayList<Integer> usedBWs = new ArrayList<Integer>();
				ArrayList<Integer> usedCarriers = new ArrayList<Integer>();
				ArrayList<Integer> unusedBwCells = new ArrayList<Integer>();
				ArrayList<Integer> unusedCarrierCells = new ArrayList<Integer>();

				for (int i = 0; i < existingAnchorsA.size(); i++) {
					if (cellDataAref.get(existingAnchorsA.get(i)).get("arbidx").toString()
							.equals(lccAref.get(li).get(di).get("arbidx").toString())) {
						usedBWs.add(returnAnchorSelectBW(Integer
								.parseInt(cellDataAref.get(existingAnchorsA.get(i)).get("bandwidth").toString())));
						usedCarriers.add(
								Integer.parseInt(cellDataAref.get(existingAnchorsA.get(i)).get("carrid").toString()));
					}
				}
				for (int i = 0; i < potentialAnchorsA.size(); i++) {
					if (foundintA(
							returnAnchorSelectBW(Integer
									.parseInt(cellDataAref.get(potentialAnchorsA.get(i)).get("bandwidth").toString())),
							usedBWs) == -1) {
						unusedBwCells.add(potentialAnchorsA.get(i));
					}

					if (foundintA(Integer.parseInt(cellDataAref.get(potentialAnchorsA.get(i)).get("carrid").toString()),
							usedCarriers) == -1) {
						unusedCarrierCells.add(potentialAnchorsA.get(i));
					}
				}

				if (unusedCarrierCells.size() > 0) {
					anchor = (unusedCarrierCells.get(0));
				}

				if (unusedBwCells.size() > 0) {
					anchor = (unusedBwCells.get(0));
				}

				if ((anchor != -1)) {
					existingAnchorsA.add(anchor);
					lccAref.get(li).get(di).replace("anchor", anchor);
					lccAref.get(li).get(di).replace("anchorMCTidx", "0");
				}
				if ((anchor == -1)) {
					existingAnchorsA.add(0);
					lccAref.get(li).get(di).replace("anchor", "0");
					lccAref.get(li).get(di).replace("anchorMCTidx", "0");
				}
			}
			anchorsAref.add(existingAnchorsA);
		}

		for (int i = 0; i < cellDataAref.size(); i++) {
			cellDataAref.get(i).put("anchorboo", 0);
		}

		/*
		 * for (int ci = 0; ci < cellDataAref.size(); ci++) { for (int li = 0; li <
		 * lccAref.size(); li++) { for (int di = 0; di < lccAref.get(li).size(); di++) {
		 * if (Integer.parseInt(lccAref.get(li).get(di).get("anchor").toString()) != -1)
		 * { if (Integer.parseInt(cellDataAref.get(ci).get("LCCnum").toString()) == li
		 * && (cellDataAref
		 * .get(Integer.parseInt(lccAref.get(li).get(di).get("anchor").toString())).get(
		 * "carrid") .toString().equals(cellDataAref.get(ci).get("carrid").toString())))
		 * { cellDataAref.get(ci).replace("anchorboo", 1); } } }
		 * 
		 * } }
		 */
	}

	private static int foundintA(int e, ArrayList<Integer> arrayref) {
		for (int i = 0; i < arrayref.size(); i++) {
			if (e == arrayref.get(i)) {
				return i;
			}
		}
		return -1;
	}

	private static int returnAnchorSelectBW(int actualBW) {
		if (actualBW == 5) {
			return 10;
		} else {
			return actualBW;
		}
	}

	@SuppressWarnings("unchecked")
	private static void freeAllDspResources(ArrayList<ArrayList<TreeMap<String, Object>>> lccAref,
			ArrayList<TreeMap<String, Object>> cellDataAref, String lcc, String dsp) {
		for (int i = 0; i < cellDataAref.size(); i++) {
			if (cellDataAref.get(i).get("LCCnum").toString().equals(lcc)
					&& cellDataAref.get(i).get("dspid").toString().equals(dsp)) {
				cellDataAref.get(i).replace("dspid", "-1");
				cellDataAref.get(i).replace("multict", "");
				cellDataAref.get(i).replace("carrierInDSPid", "-1");
			}
		}

		lccAref.get(Integer.parseInt(lcc)).get(Integer.parseInt(dsp)).forEach((i, data1) -> {
			if (!(i.contains("arbidx") || i.contains("anchor"))) {
				((TreeMap<String, Object>) data1).forEach((j, data2) -> {
					((TreeMap<String, Object>) data2).forEach((k, data3) -> {
						if (k.contains("type")) {
							((TreeMap<String, Object>) data3).replace("tstatus", "unlocked");
							((TreeMap<String, Object>) data3).replace("numcells", "0");
							for (int l = 0; l < ((int[]) ((TreeMap<String, Object>) data3)
									.get("resourceTrack")).length; l++) {
								((int[]) ((TreeMap<String, Object>) data3).get("resourceTrack"))[l] = 0;
							}
							((TreeMap<String, Object>) data3).replace("resourceDef",
									((int[][][]) ((TreeMap<String, Object>) data3).get("defaultRDef")));
						} else if (k.contains("carrierLimits")) {
							for (int car = 0; car < ((int[][]) ((TreeMap<String, Object>) data3)
									.get("num_supPerCar"))[0].length; car++) {
								((int[][]) ((TreeMap<String, Object>) data3).get("numcellsPerCar"))[1][car] = 0;
							}
						}
					});
				});
			}
		});
	}

	private static void print_cellDataA(ArrayList<ArrayList<TreeMap<String, Object>>> lccAref,
			ArrayList<TreeMap<String, Object>> cellDataAref, String log) {
		if (printall || printcell) {
			dlog(log + "\n");
			dlog("cellDataA\n---------\n\n");

			for (int li = 0; li < lccAref.size(); li++) {
				dlog("Anchors:\n");

				for (int di = 0; di < lccAref.get(li).size(); di++) {
					if ((!lccAref.get(li).get(di).get("anchor").toString().equals("-1"))
							&& (!lccAref.get(li).get(di).get("anchorMCTidx").toString().equals("-1"))) {
						dlog("Cell "
								+ cellDataAref.get(Integer.parseInt(lccAref.get(li).get(di).get("anchor").toString()))
										.get("cellid").toString()
								+ "    Next MCT Index " + lccAref.get(li).get(di).get("anchorMCTidx").toString()
								+ "\n");
					} else {
						dlog("LCC " + li + " DSP " + di + " has no anchor.\n");
					}
				}
				dlog("\n");
			}
			dlog("\n");
		}
	}

	@SuppressWarnings("unchecked")
	private static void print_lccA(ArrayList<ArrayList<TreeMap<String, Object>>> lccAref,
			ArrayList<TreeMap<String, Object>> cellDataAref, String log) {
		if (printall || printdsp) {
			dlog(log + "\n");
			if (!(printverb == true))
				dlog("lccA\n----\n(offline resources not printed)\n\n");

			ArrayList<Integer> cardsToPrint = new ArrayList<>();
			for (int ci = 0; ci < cellDataAref.size(); ci++) {
				if (Integer.parseInt(cellDataAref.get(ci).get("LCCnum").toString()) == cardsToPrint.size()) {
					cardsToPrint.add(cardsToPrint.size());
					continue;
				}
			}
			for (int i = 0; i < cardsToPrint.size(); i++) {
				final int li = i;
				for (int t = 0; t < lccAref.get(li).size(); t++) {
					final int di = t;
					lccAref.get(li).get(di).forEach((dlay, data1) -> {
						if (dlay.contains("arbidx")) {
							String printarbidx = data1.toString();
							dlog("LCC " + li + " DSP " + di + "  arbidx: " + printarbidx + "\n");
						} else if (dlay.contains("anchor")) {
						}

						else {
							((TreeMap<String, Object>) data1).forEach((fmt, data2) -> {
								((TreeMap<String, Object>) data2).forEach((typ, data3) -> {
									if (!typ.contains("carrierLimits")) {
										String printvar;
										if (printverb == false) {
											if (!((TreeMap<String, Object>) data3).get("tstatus").toString()
													.contains("offline")) {
												dlog("LCC " + li + " DSP " + di + "  ");
												printvar = ((TreeMap<String, Object>) data3).get("typename").toString();
												dlog(printvar + " ");
												printvar = ((TreeMap<String, Object>) data3).get("tstatus").toString();
												dlog(printvar + " ");
												dlog("   1 Means Used: ");
												for (int j = 0; j < ((int[]) ((TreeMap<String, Object>) data3)
														.get("resourceTrack")).length; j++) {
													printvar = (new Integer(((int[]) ((TreeMap<String, Object>) data3)
															.get("resourceTrack"))[j])).toString();
													dlog(printvar + " ");
												}
												dlog("\n");
												dlog("             ");

												for (int j = 0; j < ((int[][][]) ((TreeMap<String, Object>) data3)
														.get("resourceDef")).length; j++) {
													for (int k = 0; k < ((int[][][]) ((TreeMap<String, Object>) data3)
															.get("resourceDef"))[j].length; k++) {
														for (int l = 0; l < ((int[][][]) ((TreeMap<String, Object>) data3)
																.get("resourceDef"))[j][k].length; l++) {
															if (l == 0) {
																dlog("[" + ((int[][][]) ((TreeMap<String, Object>) data3)
																		.get("resourceDef"))[j][k][l]);
															} else {
																dlog("," + ((int[][][]) ((TreeMap<String, Object>) data3)
																		.get("resourceDef"))[j][k][l]);
															}
														}
														dlog("]");
													}
													dlog(" ");
												}
												dlog("\n\n");
											}
										} else {
											printvar = ((TreeMap<String, Object>) data3).get("typename").toString();
											System.out.print(printvar + " ");
											printvar = ((TreeMap<String, Object>) data3).get("tstatus").toString();
											System.out.print(printvar + " ");
											System.out.print("   1 Means Used: ");
											for (int j = 0; j < ((int[]) ((TreeMap<String, Object>) data3)
													.get("resourceTrack")).length; j++) {
												printvar = (new Integer(((int[]) ((TreeMap<String, Object>) data3)
														.get("resourceTrack"))[j])).toString();
												dlog(printvar + " ");
											}
											dlog("\n");
										}
									} else {
										for (int car = 0; car < ((int[][]) ((TreeMap<String, Object>) data3)
												.get("num_supPerCar"))[0].length; car++) {
											if (((int[][]) ((TreeMap<String, Object>) data3)
													.get("numcellsPerCar"))[1][car] > 0) {
												dlog("LCC " + li + " DSP " + di + " carrier "
														+ ((int[][]) ((TreeMap<String, Object>) data3)
																.get("num_supPerCar"))[0][car]);
												dlog(" has a maximum of " + ((int[][]) ((TreeMap<String, Object>) data3)
														.get("num_supPerCar"))[1][car]);
												dlog(" cells per DSP and the current number is ");
												dlog(((int[][]) ((TreeMap<String, Object>) data3)
														.get("numcellsPerCar"))[1][car] + "\n");
											}
										}
									}
								});
							});
						}
					});
					dlog("\n");
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static void print_inputForGrowTemplate(ArrayList<ArrayList<TreeMap<String, Object>>> lccAref,
			ArrayList<TreeMap<String, Object>> cellDataAref, String node) {
		print_supplemental("\n\nInput for Grow Template " + node + ":\n-----------------------------");
		print_supplemental(
				" LCC     SectorID     CarrierID     Cell Index in DSP     DSP ID                   MultiCarrierType                                OPTIC_DISTANCE");
		print_supplemental(
				" ------------------------------------------------------------------------------------------------------------------------------------------------");

		for (int cd = 0; cd < 3; cd++) {
			for (int i = 0; i < cellDataAref.size(); i++) {
				if (Integer.parseInt(cellDataAref.get(i).get("LCCnum").toString()) == cd) {
					String printstring = "  " + cd + "       " + cellDataAref.get(i).get("sectid").toString()
							+ "             " + cellDataAref.get(i).get("carrid").toString();
					printstring = printstring + "                  "
							+ cellDataAref.get(i).get("carrierInDSPid").toString();
					printstring = printstring + "               " + cellDataAref.get(i).get("DspCellIndex").toString();
					printstring = printstring + "               " + cellDataAref.get(i).get("dspid").toString()
							+ "       ";
					if (!cellDataAref.get(i).get("multict").toString().equals("")) {
						printstring = printstring
								+ ((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
										.get(0).get(0).get("delay0")).get("format0"))
												.get(cellDataAref.get(i).get("multict").toString())).get("typename");
					} else {
						printstring = printstring + "";
					}

					int typenamechars = 0;
					if (!cellDataAref.get(i).get("multict").toString().equals("")) {
						typenamechars = ((TreeMap<String, Object>) ((TreeMap<String, Object>) ((TreeMap<String, Object>) lccAref
								.get(0).get(0).get("delay0")).get("format0"))
										.get(cellDataAref.get(i).get("multict").toString())).get("typename").toString()
												.length();
					}

					for (int j = 0; j < (67 - typenamechars); j++) {
						printstring = printstring + " ";
					}
					for (int d = 0; d < (lccAref.get(cd)).size(); d++) {
						if (Integer.parseInt(cellDataAref.get(i).get("dspid").toString()) == d) {
							printstring = printstring + lccAref.get(cd).get(d).get("opticDistance").toString() + "-km";
						}
					}
					print_supplemental(printstring);
				}
			}
			print_supplemental("\n");
		}
	}

	@SuppressWarnings("unchecked")
	static void add_optic_distance_to_dsps(ArrayList<ArrayList<TreeMap<String, Object>>> lccAref,
			ArrayList<TreeMap<String, Object>> cellDataAref) {
		for (int li = 0; li < lccAref.size(); li++) {
			for (int di = 0; di < lccAref.get(li).size(); di++) {
				lccAref.get(li).get(di).put("opticDistance", "0");
			}
		}

		for (int tem1 = 0; tem1 < lccAref.size(); tem1++) {
			final int li = tem1;
			for (int tem2 = 0; tem2 < lccAref.get(li).size(); tem2++) {
				final int di = tem2;
				lccAref.get(li).get(di).forEach((dlay, data1) -> {
					if ((!dlay.contains("arbidx")) && (!dlay.contains("opticDistance")) && (!dlay.contains("anchor"))) {
						((TreeMap<String, Object>) data1).forEach((fmt, data2) -> {
							((TreeMap<String, Object>) data2).forEach((typ, data3) -> {
								if (!typ.contains("carrierLimits")) {
									if (!((TreeMap<String, Object>) data3).get("tstatus").toString()
											.contains("offline")) {
										for (int cia = 0; cia < cellDataAref.size(); cia++) {
											if (Integer.parseInt(cellDataAref.get(cia).get("LCCnum").toString()) == li
													&& Integer.parseInt(
															cellDataAref.get(cia).get("dspid").toString()) == di) {
												if (dlay.contains("delay0")) {
													ArrayList<String> vendors = new ArrayList<>();
													int aslDelay = 0;
													int fiveKm = 24516;

													for (int cib = 0; cib < cellDataAref.size(); cib++) {
														if (Integer.parseInt(
																cellDataAref.get(cib).get("LCCnum").toString()) == li
																&& Integer.parseInt(cellDataAref.get(cib).get("dspid")
																		.toString()) == di) {
															vendors.add(cellDataAref.get(cib).get("vendor").toString());
															if (cellDataAref.get(cib).get("vendor").toString()
																	.contains("asl")) {
																if (Integer.parseInt(cellDataAref.get(cib).get("delayv")
																		.toString()) > aslDelay) {
																	aslDelay = Integer.parseInt(cellDataAref.get(cib)
																			.get("delayv").toString());
																}
															}
														}
													}

													int containsALU = 0;
													int containsASL = 0;
													int containsSam = 0;
													int cellcnt = vendors.size();

													for (int i = 0; i < vendors.size(); i++) {
														if (vendors.get(i).contains("alu")) {
															containsALU = 1;
														}
														if (vendors.get(i).contains("asl")) {
															containsASL = 1;
														}
														if (vendors.get(i).contains("samsung")) {
															containsSam = 1;
														}
													}
													if (containsSam == 1) {
														lccAref.get(li).get(di).replace("opticDistance", "20");
													} else if (cellcnt == 4 && containsSam == 1 && containsALU == 1) {
														lccAref.get(li).get(di).replace("opticDistance", "10");
													} else if (cellcnt == 4 && containsALU == 1 && containsASL == 1) {
														lccAref.get(li).get(di).replace("opticDistance", "10");
													} else if (containsALU == 1 && containsASL == 1
															&& aslDelay >= fiveKm) {
														lccAref.get(li).get(di).replace("opticDistance", "10");
													} else if (containsALU == 1 && containsSam == 1) {
														lccAref.get(li).get(di).replace("opticDistance", "10");
													} else {
														lccAref.get(li).get(di).replace("opticDistance", "10");
													}
												} else {
													lccAref.get(li).get(di).replace("opticDistance", "20");
												}
											}
										}
									}
								}
							});
						});
					}
				});
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static void createGrowTemplate_801(ArrayList<TreeMap<String, Object>> cellDataAref,
			ArrayList<TreeMap<String, Object>> rrhDataAref, ArrayList<ArrayList<TreeMap<String, Object>>> lccAref,
			String site, String ts) {
		String growfile = growfolder + "/Cell_Grow_" + version + "_" + site + "_" + ts + ".csv";
		PrintWriter pw = null;
		try {

			File f = new File(growfile);
			f.createNewFile();
			pw = new PrintWriter(new BufferedWriter(new FileWriter(f)));

			int lcccnt = 0;
			pw.print("@CELL_INFORMATION\n");
			pw.print("State,SectorID,CarrierID,Cell Index in DSP,DSP ID,CC ID,PortID,RUPortID,Rrh Conf,");
			pw.print("MultiCarrierType,VirtualRFPortMapping,DlMaxTxPower,Pucch center mode,PCI,Diversity,");
			pw.print(
					"EarfcnDL,EarfcnUL,Bandwidth,CRS,eMTC,Frequency Profile,TAC,EAID,HSF,ZCZC,RSI,Auto GPS,Latitude,Longitude,Height\n");
			TreeMap<String, Object> cda = new TreeMap<String, Object>();
			TreeMap<String, Object> rda = new TreeMap<String, Object>();
			for (int i = 0; i < cellDataAref.size(); i++) {
				cda = cellDataAref.get(i);

				pw.print("ADD," + cda.get("sectid") + "," + cda.get("carrid") + "," + cda.get("carrierInDSPid") + ","
						+ cda.get("dspid") + "," + cda.get("LCCnum") + "," + cda.get("port") + ",0,");
				pw.print(cda.get("cpristr") + "," + cda.get("typename") + ",off," + cda.get("power") + ","
						+ "edge-mode," + cda.get("pci") + "," + cda.get("diversity") + "," + cda.get("earfcndl") + ",");
				pw.print(cda.get("earfcnul") + "," + cda.get("bwstr") + "," + cda.get("crs") + "," + cda.get("emtc")
						+ "," + "-," + cda.get("tac") + "," + "0" + ",");
				pw.print("false," + "12," + cda.get("rach") + ",true,N 000:00:00.001,E 000:00:00.000,0.00m\n");

				if (Integer.parseInt(cda.get("LCCnum").toString()) > lcccnt) {
					lcccnt = Integer.parseInt(cda.get("LCCnum").toString());
				}
			}

			pw.print("@NBIoTCell\n");
			pw.print(
					"State,ParentCellNumber,NBIoTPCI,OperationModeInfo,NBIoTTAC,Use Parent PCI for Guard-band,InitialNprach,");
			pw.print(
					"NprachStartTimeCL1,NprachSubcarrierOffsetCL1,NprachStartTimeCL2,NprachSubcarrierOffsetCL2,NprachStartTimeCL3,");
			pw.print("NprachSubcarrierOffsetCL3,GuardBand,Avoid UL Interfering,DL RB,UL RB\n");
			for (int i = 0; i < cellDataAref.size(); i++) {
				cda = cellDataAref.get(i);
				if (cda.get("band").toString().contains("700mhz")) {
					pw.print("ADD," + cda.get("cellid") + "," + cda.get("pci") + "," + "guard-band," + cda.get("iottac")
							+ ",on," + "on," + "nprach-start-time-ms8," + "nprach-subcarrier-offset-n36,");
					pw.print("nprach-start-time-ms8," + "nprach-subcarrier-offset-n36," + "nprach-start-time-ms8,"
							+ "nprach-subcarrier-offset-n36," + "right," + "false," + "45," + "40\n");
				} else {
					pw.print("NONE," + cda.get("cellid") + "," + cda.get("pci") + ",guard-band," + "0," + "off,"
							+ "off," + "nprach-start-time-ms8," + "nprach-subcarrier-offset-n36,");
					pw.print("nprach-start-time-ms8," + "nprach-subcarrier-offset-n36," + "nprach-start-time-ms8,"
							+ "nprach-subcarrier-offset-n36," + "right," + "false," + "45," + "40\n");
				}
			}

			pw.print("@Unit\n");
			pw.print("State,UnitType,UnitID,BoardType\n");
			for (int i = 0; i <= lcccnt; i++) {
				pw.print("ADD" + "," + "ecp" + "," + i + "," + "lcc4-b1\n");
			}

			pw.print("@CPRIPort\n");
			pw.print("State,UnitType,UnitID,PortID\n");
			for (int i = 0; i < rrhDataAref.size(); i++) {
				pw.println("ADD" + "," + "ecp" + "," + rrhDataAref.get(i).get("LCCnum") + ","
						+ rrhDataAref.get(i).get("port"));
			}

			pw.print("@RRH\n");
			pw.print(
					"State,Rrh Conf,Connected DU Board Type,RRH Type,StartEarfcn1,StartEarfcn2,SerialNumber,Azimuth,Beamwidth\n");
			for (int i = 0; i < rrhDataAref.size(); i++) {
				rda = rrhDataAref.get(i);
				pw.print("ADD" + "," + rda.get("cpristr") + "," + "ecp" + "," + rda.get("code") + ","
						+ ((ArrayList<String>) rda.get("startearfcnA")).get(0) + ","
						+ ((ArrayList<String>) rda.get("startearfcnA")).get(1) + "," + "," + "-1" + "," + "700\n");
			}

			pw.print("@RRHAntennaPort\n");
			pw.print("State,Rrh Conf,Connected DU Board Type,AntennaPortID,AntennaCableLength\n");
			for (int i = 0; i < rrhDataAref.size(); i++) {
				for (int j = 0; j < ((int[]) rrhDataAref.get(i).get("antennaPortMapA")).length; j++) {
					if (((int[]) rrhDataAref.get(i).get("antennaPortMapA"))[j] != -1) {
						int antport = j + 1;
						pw.print("ADD" + "," + rrhDataAref.get(i).get("cpristr") + "," + "ecp" + "," + antport + ","
								+ ((int[]) rrhDataAref.get(i).get("antennaPortMapA"))[j]);
						if (j < ((int[]) rrhDataAref.get(i).get("antennaPortMapA")).length) {
							pw.print("");
						}
						pw.print("\n");
					}
				}
			}

			pw.print("@DSP\n");
			pw.print("State,UnitID,DSP ID,OPTIC_DISTANCE\n");

			ArrayList<Integer> cardsProvisioned = new ArrayList<Integer>();
			for (int ci = 0; ci < cellDataAref.size(); ci++) {
				if (Integer.parseInt(cellDataAref.get(ci).get("LCCnum").toString()) == (cardsProvisioned.size())) {
					cardsProvisioned.add(cardsProvisioned.size());
					continue;
				}
			}
			for (int li = 0; li < cardsProvisioned.size(); li++) {
				for (int di = 0; di < (lccAref.get(li)).size(); di++) {
					if (lccAref.get(li).get(di).get("opticDistance").toString().equals("0")) {
						pw.print("ADD" + "," + li + "," + di + "," + "10-km\n");
					} else {
						pw.print("ADD" + "," + li + "," + di + "," + lccAref.get(li).get(di).get("opticDistance")
								+ "-km\n");
					}
				}
			}

		} catch (Exception e) {
			logger.error("Exception in USM Cell Grower " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		} finally {
			if (pw != null) {
				pw.flush();
				pw.close();
			}
		}

	}

	@SuppressWarnings("unchecked")
	private static void createGrowTemplate_851(ArrayList<TreeMap<String, Object>> cellDataAref,
			ArrayList<TreeMap<String, Object>> rrhDataAref, ArrayList<ArrayList<TreeMap<String, Object>>> lccAref,
			String growfile) {
		PrintWriter pw = null;
		try {

			File f = new File(growfile);
			f.createNewFile();
			pw = new PrintWriter(new BufferedWriter(new FileWriter(f)));

			int lcccnt = 0;
			pw.print("@CELL_INFORMATION\n");
			pw.print("State,SectorID,CarrierID,Cell Index in DSP,DSP ID,CC ID,PortID,RUPortID,Rrh Conf,");
			pw.print("MultiCarrierType,VirtualRFPortMapping,DlMaxTxPower,Pucch center mode,PCI,Diversity,");
			pw.print("EarfcnDL,EarfcnUL,Bandwidth,CRS,eMTC,Frequency Profile,TAC,EAID,HSF,ZCZC,RSI,");
			pw.print(
					"thMaxEirp,thRSSI,Preferred Earfcn,Subframe Assignment,Special Subframe Patterns,Auto GPS,Latitude,Longitude,Height\n");
			TreeMap<String, Object> cda = new TreeMap<String, Object>();
			TreeMap<String, Object> rda = new TreeMap<String, Object>();
			for (int i = 0; i < cellDataAref.size(); i++) {
				cda = cellDataAref.get(i);
				if (cda.get("bwstr").toString().equals("CBRS/20MHz")) {
					cda.replace("bwstr", "3.5GHz/20MHz");
				} else {
					cda.get("bwstr");
				}
				if (cda.get("market").toString().equals("NEWENGLAND")) {
					pw.print("ADD," + cda.get("sectid") + "," + cda.get("carrid") + "," + cda.get("carrierInDSPid")
							+ "," + cda.get("dspid") + "," + cda.get("LCCnum") + "," + cda.get("port") + ",0,");
					pw.print(cda.get("cpristr") + "," + cda.get("typename") + ",off," + cda.get("power") + ","
							+ "edge-mode," + cda.get("pci") + "," + cda.get("diversity") + "," + cda.get("earfcndl")
							+ ",");
					pw.print(cda.get("earfcnul") + "," + cda.get("bwstr") + "," + cda.get("crs") + "," + cda.get("emtc")
							+ "," + "-," + cda.get("tac") + "," + "0" + ",");
					pw.print("false," + "12," + cda.get("rach") + ",37,-25,,"
							+ "subframe-assignment-sa2,special-subframe-pattern-ssp7,true,N 000:00:00.001,E 000:00:00.000,0.00m\n");
				} else {
					pw.print("ADD," + cda.get("sectid") + "," + cda.get("carrid") + "," + cda.get("DspCellIndex") + ","
							+ cda.get("DspID") + "," + cda.get("LCCnum") + "," + cda.get("port") + ","
							+ cda.get("RUPortID") + ",");
					pw.print(cda.get("cpristr") + "," + cda.get("typename") + ",off," + cda.get("power") + ","
							+ "edge-mode," + cda.get("pci") + "," + cda.get("diversity") + "," + cda.get("earfcndl")
							+ ",");
					pw.print(cda.get("earfcnul") + "," + cda.get("bwstr") + "," + cda.get("crs") + "," + cda.get("emtc")
							+ "," + "-," + cda.get("tac") + "," + "0" + ",");
					pw.print("false," + "12," + cda.get("rach") + "," + cda.get("thMaxEirp") + ",-25,0,"
							+ "subframe-assignment-sa2,special-subframe-pattern-ssp7,true,N 000:00:00.001,E 000:00:00.000,0.00m\n");

				}

				/*if (Integer.parseInt(cda.get("LCCnum").toString()) > lcccnt) {
					lcccnt = Integer.parseInt(cda.get("LCCnum").toString());
				}
				*/
				if (cda.containsKey("LCCnum") && StringUtils.isNotEmpty(cda.get("LCCnum").toString())
						&& Integer.parseInt(cda.get("LCCnum").toString()) > lcccnt) {
					lcccnt = Integer.parseInt(cda.get("LCCnum").toString());
				}
			}

			pw.print("@NBIoTCell\n");
			pw.print(
					"State,ParentCellNumber,NBIoTPCI,OperationModeInfo,NBIoTTAC,Use Parent PCI for Guard-band,InitialNprach,");
			pw.print(
					"NprachStartTimeCL0,NprachSubcarrierOffsetCL0,NprachStartTimeCL1,NprachSubcarrierOffsetCL1,NprachStartTimeCL2,");
			pw.print("NprachSubcarrierOffsetCL2,GuardBand,Avoid UL Interfering,DL RB,UL RB\n");
			for (int i = 0; i < cellDataAref.size(); i++) {
				cda = cellDataAref.get(i);
				if (cda.get("band").toString().contains("700mhz")) {
					pw.print("ADD," + cda.get("cellid") + "," + cda.get("pci") + "," + "guard-band," + cda.get("iottac")
							+ ",on," + "on," + "nprach-start-time-ms8," + "nprach-subcarrier-offset-n36,");
					pw.print("nprach-start-time-ms8," + "nprach-subcarrier-offset-n36," + "nprach-start-time-ms8,"
							+ "nprach-subcarrier-offset-n36," + "right," + "false," + "45," + "40\n");
				} else {
					pw.print("NONE," + cda.get("cellid") + "," + cda.get("pci") + ",guard-band," + "0," + "off,"
							+ "off," + "nprach-start-time-ms8," + "nprach-subcarrier-offset-n36,");
					pw.print("nprach-start-time-ms8," + "nprach-subcarrier-offset-n36," + "nprach-start-time-ms8,"
							+ "nprach-subcarrier-offset-n36," + "right," + "false," + "45," + "40\n");
				}
			}

			pw.print("@Unit\n");
			pw.print("State,UnitType,UnitID,BoardType\n");
			for (int i = 0; i <= lcccnt; i++) {
				pw.print("ADD" + "," + "ecp" + "," + i + "," + "lcc4-b1\n");
			}

			pw.print("@CPRIPort\n");
			pw.print("State,UnitType,UnitID,PortID\n");
			for (int i = 0; i < rrhDataAref.size(); i++) {
				pw.println("ADD" + "," + "ecp" + "," + rrhDataAref.get(i).get("LCCnum") + ","
						+ rrhDataAref.get(i).get("port"));
			}

			pw.print("@RRH\n");
			pw.print(
					"State,Rrh Conf,Connected DU Board Type,RRH Type,StartEarfcn1,StartEarfcn2,SerialNumber,Azimuth,Beamwidth,");
			pw.print("Fcc Id,Call Sign,CBSD Category,X Pole Antenna,Antenna Gain dBi,Cable Loss,Accuracy Margin dB\n");
			for (int i = 0; i < rrhDataAref.size(); i++) {
				rda = rrhDataAref.get(i);
				pw.print("ADD" + "," + rda.get("cpristr") + "," + "ecp" + "," + rda.get("code") + ","
						+ ((ArrayList<String>) rda.get("startearfcnA")).get(0) + ","
						+ ((ArrayList<String>) rda.get("startearfcnA")).get(1) + "," + "," + "-1" + "," + "700,");
				pw.print(",," + "cbsd-b" + "," + "true" + ",," + "0" + "," + "10\n");
			}

			pw.print("@RRHAntennaPort\n");
			pw.print("State,Rrh Conf,Connected DU Board Type,AntennaPortID,AntennaCableLength\n");
			for (int i = 0; i < rrhDataAref.size(); i++) {
				for (int j = 0; j < ((int[]) rrhDataAref.get(i).get("antennaPortMapA")).length; j++) {
					if (((int[]) rrhDataAref.get(i).get("antennaPortMapA"))[j] != -1) {
						int antport = j + 1;
						pw.print("ADD" + "," + rrhDataAref.get(i).get("cpristr") + "," + "ecp" + "," + antport + ","
								+ ((int[]) rrhDataAref.get(i).get("antennaPortMapA"))[j]);
						if (j < ((int[]) rrhDataAref.get(i).get("antennaPortMapA")).length) {
							pw.print("");
						}
						pw.print("\n");
					}
				}
			}

			pw.print("@GroupInfo\n");
			pw.print("State,Rrh Conf,Group ID\n");
			for (int i = 0; i < rrhDataAref.size(); i++) {
				pw.print("ADD" + "," + rrhDataAref.get(i).get("cpristr") + "," + "0\n");
			}

			pw.print("@DSP\n");
			pw.print("State,UnitID,DSP ID,OPTIC_DISTANCE\n");

			ArrayList<Integer> cardsProvisioned = new ArrayList<Integer>();
			for (int ci = 0; ci < cellDataAref.size(); ci++) {
				if (Integer.parseInt(cellDataAref.get(ci).get("LCCnum").toString()) == (cardsProvisioned.size())) {
					cardsProvisioned.add(cardsProvisioned.size());
					continue;
				}
			}
			for (int li = 0; li < cardsProvisioned.size(); li++) {
				for (int di = 0; di < (lccAref.get(li)).size(); di++) {
					if (lccAref.get(li).get(di).get("opticDistance").toString().equals("0")) {
						pw.print("ADD" + "," + li + "," + di + "," + "10-km\n");
					} else {
						pw.print("ADD" + "," + li + "," + di + "," + lccAref.get(li).get(di).get("opticDistance")
								+ "-km\n");
					}
				}
			}

		} catch (Exception e) {
			logger.error("Exception in USM Cell Grower " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		} finally {
			if (pw != null) {
				pw.flush();
				pw.close();
			}
		}

	}

	// method for 20.A.0 vesion//////////////////////
	@SuppressWarnings("unchecked")
	private static void createGrowTemplate_20A0(ArrayList<TreeMap<String, Object>> cellDataAref,
			ArrayList<TreeMap<String, Object>> rrhDataAref, ArrayList<ArrayList<TreeMap<String, Object>>> lccAref,
			String growfile) {
		PrintWriter pw = null;
		try {
			String val = "";
			File f = new File(growfile);
			f.createNewFile();
			pw = new PrintWriter(new BufferedWriter(new FileWriter(f)));
			TreeMap<String, Object> cdar = new TreeMap<String, Object>();
			for (int i = 0; i < cellDataAref.size(); i++) {
				cdar = cellDataAref.get(i);
				if (cdar.get("band").equals("AWS-3") && (((String) cdar.get("DspCellIndex")).equals("4")
						|| ((String) cdar.get("DspCellIndex")).equals("5"))) {
					val = (String) cdar.get("cpriPortAssignment");
					val = org.apache.commons.lang.StringUtils.substringAfter(val, "(");
					System.out.println(val);
				}

			}
			if (enbidA.get(enbidx) == 243991) {
				for (int cias = 0; cias < cellDataAref.size(); cias++) {
					if (cellDataAref.get(cias).get("carrierInDSPid").equals("0")
							&& (Integer) cellDataAref.get(cias).get("dspid") == 2
							&& cellDataAref.get(cias).get("cpriPortAssignment").toString().contains("LCC-1")) {
						cellDataAref.get(cias).replace("carrierInDSPid", 3);
						cellDataAref.get(cias).replace("dspid", 1);
					}
					if (cellDataAref.get(cias).get("carrierInDSPid").equals("3")
							&& (Integer) cellDataAref.get(cias).get("dspid") == 1
							&& cellDataAref.get(cias).get("cpriPortAssignment").toString().contains("LCC-1")) {
						cellDataAref.get(cias).replace("carrierInDSPid", 0);
						cellDataAref.get(cias).replace("dspid", 2);
					}
				}
			}
			for (int i = 0; i < cellDataAref.size(); i++) {
				cdar = cellDataAref.get(i);
				if (cdar.get("band").equals("700MHz") || cdar.get("band").equals("700")
						|| cdar.get("band").equals("850")) {
					cdar.replace("typename", "cfg355-multi-carrier-10m-10m-10m-10m-5m-5m-5m-5m-config3");
				}

				else if (cdar.get("band").equals("CBRS") || cdar.get("band").equals("LAA")) {
					cdar.replace("typename", "cfg388-multi-carrier-20m-20m-20m-20m-config3");
				}
			}

			if (!val.isEmpty()) {
				for (int i = 0; i < cellDataAref.size(); i++) {
					cdar = cellDataAref.get(i);
					if (((String) cdar.get("cpriPortAssignment")).contains(val)
							&& (cdar.get("bandwidth").equals("10") || cdar.get("bandwidth").equals("5"))) {
						cdar.replace("typename", "cfg61-multi-carrier-10m-5m-6cell");
					}
				}

			}
			int flag = 0;
			String channelCord = null;
			TreeMap<String, Object> cdas = new TreeMap<String, Object>();
			ArrayList<String> ds1 = new ArrayList<>();
			ArrayList<String> ds2 = new ArrayList<>();
			ArrayList<String> ds3 = new ArrayList<>();
			ArrayList<String> cin1 = new ArrayList<>();
			ArrayList<String> cin2 = new ArrayList<>();
			ArrayList<String> cin3 = new ArrayList<>();
			ArrayList<String> ban1 = new ArrayList<>();
			ArrayList<String> ban2 = new ArrayList<>();
			ArrayList<String> ban3 = new ArrayList<>();
			for (int i1 = 0; i1 < cellDataAref.size(); i1++) {
				cdas = cellDataAref.get(i1);
				if (((String) cdas.get("cpriPortAssignment")).contains("LCC-1")
						&& (((String) cdas.get("band")).equals("LAA") || ((String) cdas.get("band")).equals("CBRS"))) {
					ds1.add((String) cdas.get("DspCellIndex"));
					cin1.add((String) cdas.get("DspID"));
					ban1.add((String) cdas.get("band"));
				} else if (((String) cdas.get("cpriPortAssignment")).contains("LCC-2")
						&& (((String) cdas.get("band")).equals("LAA") || ((String) cdas.get("band")).equals("CBRS"))) {
					ds2.add((String) cdas.get("DspCellIndex"));
					cin2.add((String) cdas.get("DspID"));
					ban2.add((String) cdas.get("band"));
				} else if (((String) cdas.get("cpriPortAssignment")).contains("LCC-3")
						&& (((String) cdas.get("band")).equals("LAA") || ((String) cdas.get("band")).equals("CBRS"))) {
					ds3.add((String) cdas.get("DspCellIndex"));
					cin3.add((String) cdas.get("DspID"));
					ban3.add((String) cdas.get("band"));
				}
			}
			int flag1 = 0, flag2 = 0, flag3 = 0;
			// Arrays.stream(strings).anyMatch(t -> t.equals(toFind));
			for (int g = 0; g < ds1.size(); g++) {
				if (ds1.get(g).equals("5")) {
					flag1 = 1;
				}
			}
			for (int g = 0; g < ds2.size(); g++) {
				if (ds2.get(g).equals("5")) {
					flag2 = 1;
				}
			}
			for (int g = 0; g < ds3.size(); g++) {
				if (ds3.get(g).equals("5")) {
					flag3 = 1;
				}
			}

			Set<String> s1 = new HashSet<>();
			Set<String> s2 = new HashSet<>();
			Set<String> s3 = new HashSet<>();

			if (flag1 == 1) {
				for (int i = 0; i < cin1.size(); i++) {
					s1.add(cin1.get(i));
				}
				if (s1.size() == 1) {
					for (int i1 = 0; i1 < cellDataAref.size(); i1++) {
						for (int i2 = 0; i2 < cin1.size(); i2++) {
							cdas = cellDataAref.get(i1);
							if (((String) cdas.get("DspCellIndex")).equals(ds1.get(i2))
									&& ((String) cdas.get("cpriPortAssignment")).contains("LCC-1")
									&& ((String) cdas.get("DspID")).equals(cin1.get(i2))
									&& ((String) cdas.get("band")).equals(ban1.get(i2))) {
								cdas.put("typename", "cfg63-multi-carrier-20m-2t2r-6cell");

							}
						}
					}
				}
			}
			if (flag2 == 1) {
				for (int i = 0; i < cin2.size(); i++) {
					s2.add(cin2.get(i));
				}
				if (s2.size() == 1) {
					for (int i1 = 0; i1 < cellDataAref.size(); i1++) {
						for (int i2 = 0; i2 < cin2.size(); i2++) {
							cdas = cellDataAref.get(i1);
							if (((String) cdas.get("DspCellIndex")).equals(ds2.get(i2))
									&& ((String) cdas.get("cpriPortAssignment")).contains("LCC-2")
									&& ((String) cdas.get("DspID")).equals(cin2.get(i2))
									&& ((String) cdas.get("band")).equals(ban2.get(i2))) {
								cdas.put("typename", "cfg63-multi-carrier-20m-2t2r-6cell");

							}
						}
					}
				}
			}
			if (flag3 == 1) {
				for (int i = 0; i < cin3.size(); i++) {
					s3.add(cin3.get(i));
				}
				if (s3.size() == 1) {
					for (int i1 = 0; i1 < cellDataAref.size(); i1++) {
						for (int i2 = 0; i2 < cin3.size(); i2++) {
							cdas = cellDataAref.get(i1);
							if (((String) cdas.get("DspCellIndex")).equals(ds3.get(i2))
									&& ((String) cdas.get("cpriPortAssignment")).contains("LCC-3")
									&& ((String) cdas.get("DspID")).equals(cin3.get(i2))
									&& ((String) cdas.get("band")).equals(ban3.get(i2))) {
								cdas.put("typename", "cfg63-multi-carrier-20m-2t2r-6cell");

							}
						}
					}
				}
			}
			String abcd1 = "";
			String abcd2 = "";
			String abcd3 = "";
			String abc2d1 = "";
			String abc2d2 = "";
			String abc2d3 = "";
			String abc3d1 = "";
			String abc3d2 = "";
			String abc3d3 = "";
			String dsp1 = "";
			String dsp2 = "";
			String dsp3 = "";

			TreeMap<String, Object> cdam = new TreeMap<String, Object>();
			for (int i = 0; i < cellDataAref.size(); i++) {

				cdam = cellDataAref.get(i);
				if (cdam.get("DspID").toString().equals("")) {
					if (((String) cdam.get("cpriPortAssignment")).contains("LCC-1")) {
						if (i == 8) {
							System.out.println(i);
						}
						if (((Integer) cdam.get("dspid")) == 0 && abcd1.isEmpty()) {
							abcd1 = (String) cdam.get("typename");
						} else if (((Integer) cdam.get("dspid")) == 1 && abcd2.isEmpty()) {
							abcd2 = (String) cdam.get("typename");
						} else if (((Integer) cdam.get("dspid")) == 2 && abcd3.isEmpty()) {
							abcd3 = (String) cdam.get("typename");
						}
						System.out.println(abcd1);
						System.out.println(abcd2);
						System.out.println(abcd3);

					} else if (((String) cdam.get("cpriPortAssignment")).contains("LCC-2")) {
						if (((Integer) cdam.get("dspid")) == 0 && abc2d1.isEmpty()) {
							abc2d1 = (String) cdam.get("typename");
						} else if (((Integer) cdam.get("dspid")) == 1 && abc2d2.isEmpty()) {
							abc2d2 = (String) cdam.get("typename");
						} else if (((Integer) cdam.get("dspid")) == 2 && abc2d3.isEmpty()) {
							abc2d3 = (String) cdam.get("typename");
						}
						System.out.println(abc2d1);
						System.out.println(abc2d2);
						System.out.println(abc2d3);
					} else if (((String) cdam.get("cpriPortAssignment")).contains("LCC-3")) {
						if (((Integer) cdam.get("dspid")) == 0 && abc3d1.isEmpty()) {
							abc3d1 = (String) cdam.get("typename");
						} else if (((Integer) cdam.get("dspid")) == 1 && abc3d2.isEmpty()) {
							abc3d2 = (String) cdam.get("typename");
						} else if (((Integer) cdam.get("dspid")) == 2 && abc3d3.isEmpty()) {
							abc3d3 = (String) cdam.get("typename");
						}
						System.out.println(abc3d1);
						System.out.println(abc3d2);
						System.out.println(abc3d3);
					}
				} else {
					if (((String) cdam.get("cpriPortAssignment")).contains("LCC-1")) {
						if (((String) cdam.get("DspID")).equals("0") && abcd1.isEmpty()) {
							abcd1 = (String) cdam.get("typename");
						} else if (((String) cdam.get("DspID")).equals("1") && abcd2.isEmpty()) {
							abcd2 = (String) cdam.get("typename");
						} else if (((String) cdam.get("DspID")).equals("2") && abcd3.isEmpty()) {
							abcd3 = (String) cdam.get("typename");
						}
						System.out.println(abcd1);
						System.out.println(abcd2);
						System.out.println(abcd3);

					} else if (((String) cdam.get("cpriPortAssignment")).contains("LCC-2")) {
						if (((String) cdam.get("DspID")).equals("0") && abc2d1.isEmpty()) {
							abc2d1 = (String) cdam.get("typename");
						} else if (((String) cdam.get("DspID")).equals("1") && abc2d2.isEmpty()) {
							abc2d2 = (String) cdam.get("typename");
						} else if (((String) cdam.get("DspID")).equals("2") && abc2d3.isEmpty()) {
							abc2d3 = (String) cdam.get("typename");
						}
						System.out.println(abc2d1);
						System.out.println(abc2d2);
						System.out.println(abc2d3);
					} else if (((String) cdam.get("cpriPortAssignment")).contains("LCC-3")) {
						if (((String) cdam.get("DspID")).equals("0") && abc3d1.isEmpty()) {
							abc3d1 = (String) cdam.get("typename");
						} else if (((String) cdam.get("DspID")).equals("1") && abc3d2.isEmpty()) {
							abc3d2 = (String) cdam.get("typename");
						} else if (((String) cdam.get("DspID")).equals("2") && abc3d3.isEmpty()) {
							abc3d3 = (String) cdam.get("typename");
						}
						System.out.println(abc3d1);
						System.out.println(abc3d2);
						System.out.println(abc3d3);
					}
				}
			}
			TreeMap<String, Object> cdaz = new TreeMap<String, Object>();
			for (int i = 0; i < cellDataAref.size(); i++) {
				cdaz = cellDataAref.get(i);
				if (cdaz.get("DspID").toString().equals("")) {
					if (((String) cdaz.get("cpriPortAssignment")).contains("LCC-1")
							&& ((Integer) cdaz.get("dspid")) == 0 && !(((String) cdaz.get("typename")).equals(abcd1)))
						cdaz.put("typename", abcd1);
					else if (((String) cdaz.get("cpriPortAssignment")).contains("LCC-1")
							&& ((Integer) cdaz.get("dspid")) == 1 && !(((String) cdaz.get("typename")).equals(abcd2)))
						cdaz.put("typename", abcd2);
					else if (((String) cdaz.get("cpriPortAssignment")).contains("LCC-1")
							&& ((Integer) cdaz.get("dspid")) == 2 && !(((String) cdaz.get("typename")).equals(abcd3)))
						cdaz.put("typename", abcd3);
					else if (((String) cdaz.get("cpriPortAssignment")).contains("LCC-2")
							&& ((Integer) cdaz.get("dspid")) == 0 && !(((String) cdaz.get("typename")).equals(abc2d1)))
						cdaz.put("typename", abc2d1);
					else if (((String) cdaz.get("cpriPortAssignment")).contains("LCC-2")
							&& ((Integer) cdaz.get("dspid")) == 1 && !(((String) cdaz.get("typename")).equals(abc2d2)))
						cdaz.put("typename", abc2d2);
					else if (((String) cdaz.get("cpriPortAssignment")).contains("LCC-2")
							&& ((Integer) cdaz.get("dspid")) == 2 && !(((String) cdaz.get("typename")).equals(abc2d3)))
						cdaz.put("typename", abc2d3);
					else if (((String) cdaz.get("cpriPortAssignment")).contains("LCC-3")
							&& ((Integer) cdaz.get("dspid")) == 0 && !(((String) cdaz.get("typename")).equals(abc3d1)))
						cdaz.put("typename", abc3d1);
					else if (((String) cdaz.get("cpriPortAssignment")).contains("LCC-3")
							&& ((Integer) cdaz.get("dspid")) == 1 && !(((String) cdaz.get("typename")).equals(abc3d2)))
						cdaz.put("typename", abc3d2);
					else if (((String) cdaz.get("cpriPortAssignment")).contains("LCC-3")
							&& ((Integer) cdaz.get("dspid")).equals("2")
							&& !(((String) cdaz.get("typename")).equals(abc3d3)))
						cdaz.put("typename", abc3d3);
				} else {

					if (((String) cdaz.get("cpriPortAssignment")).contains("LCC-1")
							&& ((String) cdaz.get("DspID")).equals("0")
							&& !(((String) cdaz.get("typename")).equals(abcd1)))
						cdaz.put("typename", abcd1);
					else if (((String) cdaz.get("cpriPortAssignment")).contains("LCC-1")
							&& ((String) cdaz.get("DspID")).equals("1")
							&& !(((String) cdaz.get("typename")).equals(abcd2)))
						cdaz.put("typename", abcd2);
					else if (((String) cdaz.get("cpriPortAssignment")).contains("LCC-1")
							&& ((String) cdaz.get("DspID")).equals("2")
							&& !(((String) cdaz.get("typename")).equals(abcd3)))
						cdaz.put("typename", abcd3);
					else if (((String) cdaz.get("cpriPortAssignment")).contains("LCC-2")
							&& ((String) cdaz.get("DspID")).equals("0")
							&& !(((String) cdaz.get("typename")).equals(abc2d1)))
						cdaz.put("typename", abc2d1);
					else if (((String) cdaz.get("cpriPortAssignment")).contains("LCC-2")
							&& ((String) cdaz.get("DspID")).equals("1")
							&& !(((String) cdaz.get("typename")).equals(abc2d2)))
						cdaz.put("typename", abc2d2);
					else if (((String) cdaz.get("cpriPortAssignment")).contains("LCC-2")
							&& ((String) cdaz.get("DspID")).equals("2")
							&& !(((String) cdaz.get("typename")).equals(abc2d3)))
						cdaz.put("typename", abc2d3);
					else if (((String) cdaz.get("cpriPortAssignment")).contains("LCC-3")
							&& ((String) cdaz.get("DspID")).equals("0")
							&& !(((String) cdaz.get("typename")).equals(abc3d1)))
						cdaz.put("typename", abc3d1);
					else if (((String) cdaz.get("cpriPortAssignment")).contains("LCC-3")
							&& ((String) cdaz.get("DspID")).equals("1")
							&& !(((String) cdaz.get("typename")).equals(abc3d2)))
						cdaz.put("typename", abc3d2);
					else if (((String) cdaz.get("cpriPortAssignment")).contains("LCC-3")
							&& ((String) cdaz.get("DspID")).equals("2")
							&& !(((String) cdaz.get("typename")).equals(abc3d3)))
						cdaz.put("typename", abc3d3);
				}

			}
			for (int i = 0; i < cellDataAref.size(); i++) {
				cdar = cellDataAref.get(i);
				if (cdar.get("band").equals("700MHz") || cdar.get("band").equals("700")) {
					cdar.replace("typename", "cfg355-multi-carrier-10m-10m-10m-10m-5m-5m-5m-5m-config3");
				}
			}
			int lcccnt = 0;
			pw.print("\"@CELL_INFORMATION\"\n");
			pw.print(
					"\"State\",\"Sector ID\",\"Carrier ID\",\"Cell Index in DSP\",\"DSP ID\",\"CC ID\",\"RU Port ID\",\"RU Conf\",");
			pw.print(
					"\"Multi Carrier Type\",\"Virtual RF Port Mapping\",\"Dl Max Tx Power\",\"Pucch Center Mode\",\"PCI\",\"DL Antenna Count\",\"UL Antenna Count\",");
			pw.print(
					"\"Earfcn DL\",\"Earfcn UL\",\"Cell Band Carrier\",\"Bandwidth\",\"CRS\",\"eMTC\",\"Frequency Profile\",\"TAC\",\"EAID\",\"HSF\",\"ZCZC\",\"RSI\",");
			pw.print(
					"\"Rcc ID\",\"TH MaxEirp\",\"TH RSSI\",\"Preferred Earfcn\",\"Subframe Assignment\",\"Special Subframe Patterns\",\"Dynamic Spectrum Sharing Mode\",\"CDMA Blanking Case\",\"Auto GPS\",\"Latitude\",\"Longitude\",\"Height\"\n");

			TreeMap<String, Object> cda = new TreeMap<String, Object>();
			TreeMap<String, Object> rda = new TreeMap<String, Object>();

			for (int i = 0; i < cellDataAref.size(); i++) {

				cda = cellDataAref.get(i);

				if (Integer.parseInt(cda.get("earfcndl").toString()) >= 5180
						&& Integer.parseInt(cda.get("earfcndl").toString()) <= 5279) {
					cda.replace("band", "700mhz");
				} else if (cda.get("band").equals("CBRS") || (Integer.parseInt(cda.get("earfcndl").toString()) >= 55240
						&& Integer.parseInt(cda.get("earfcndl").toString()) <= 56739)) {
					cda.replace("band", "3500mhz");
					cda.replace("earfcndl", "0");
					cda.replace("earfcnul", "0");

				} else if ((Integer.parseInt(cda.get("earfcndl").toString()) >= 1950
						&& Integer.parseInt(cda.get("earfcndl").toString()) <= 2399)) {
					cda.replace("band", "2100mhz_band4");
				} else if (Integer.parseInt(cda.get("earfcndl").toString()) >= 600
						&& Integer.parseInt(cda.get("earfcndl").toString()) <= 1199) {
					cda.replace("band", "1900mhz");

				} else if (Integer.parseInt(cda.get("earfcndl").toString()) >= 2400
						&& Integer.parseInt(cda.get("earfcndl").toString()) <= 2649) {
					cda.replace("band", "850mhz");
				} else if ((Integer.parseInt(cda.get("earfcndl").toString()) >= 66436
						&& Integer.parseInt(cda.get("earfcndl").toString()) <= 67335)) {
					if (cda.get("DspCellIndex").equals("4") || cda.get("DspCellIndex").equals("5")) {
						cda.replace("typename", "cfg61-multi-carrier-10m-5m-6cell");
					}

					cda.replace("band", "2100mhz_band66");
				} else if (cda.get("band").equals("LAA") || (Integer.parseInt(cda.get("earfcndl").toString()) >= 46790
						&& Integer.parseInt(cda.get("earfcndl").toString()) <= 54539)) {
					cda.replace("band", "5000mhz");
					cda.replace("earfcndl", "0");
					cda.replace("earfcnul", "0");
				} else if ((Integer.parseInt(cda.get("earfcndl").toString()) >= 8040
						&& Integer.parseInt(cda.get("earfcndl").toString()) <= 8689)) {
					cda.replace("band", "1900mhz_band25");
				}
				if (cda.get("bandwidth").equals("10")) {
					cda.replace("bandwidth", "system-bandwidth-n50");
				} else if (cda.get("bandwidth").equals("20")) {
					cda.replace("bandwidth", "system-bandwidth-n100");

				} else if (cda.get("bandwidth").equals("15")) {
					cda.replace("bandwidth", "system-bandwidth-n75");
				} else if (cda.get("bandwidth").equals("5")) {
					cda.replace("bandwidth", "system-bandwidth-n25");
					;
				}

				// if (cda.get(\"PreferredEarfcn'} eq "0"){
				// cda.get(\"PreferredEarfcn'} = "";
				// }
				if (cda.get("market").toString().equals("NEWENGLAND")) {
					pw.print("\"ADD" + "\",\"" + cda.get("sectid") + "\",\"" + cda.get("carrid") + "\",\""
							+ cda.get("carrierInDSPid") + "\",\"" + cda.get("dspid") + "\",\"" + cda.get("LCCnum")
							+ "\",\"" + "0\",\"");
					pw.print(cda.get("cpristr") + "\",\"");
					// For null Values
					if (cda.get("typename") != null) {
						pw.print(cda.get("typename"));
					}
					if (cda.get("txd").equals("2") && cda.get("rxd").equals("2")) {
						pw.print("\",\"" + "off" + "\",\"" + cda.get("power") + "\",\"" + "edge-mode" + "\",\""
								+ cda.get("pci") + "\",\"" + "n2-tx-antenna-count\",\"n2-rx-antenna-count" + "\",\""
								+ cda.get("earfcndl") + "\",\"");
					} else if (cda.get("txd").equals("2") && cda.get("rxd").equals("4")) {
						pw.print("\",\"" + "off" + "\",\"" + cda.get("power") + "\",\"" + "edge-mode" + "\",\""
								+ cda.get("pci") + "\",\"" + "n2-tx-antenna-count\",\"n4-rx-antenna-count" + "\",\""
								+ cda.get("earfcndl") + "\",\"");
					} else if (cda.get("txd").equals("4") && cda.get("rxd").equals("2")) {
						pw.print("\",\"" + "off" + "\",\"" + cda.get("power") + "\",\"" + "edge-mode" + "\",\""
								+ cda.get("pci") + "\",\"" + "n4-tx-antenna-count\",\"n2-rx-antenna-count" + "\",\""
								+ cda.get("earfcndl") + "\",\"");
					} else if (cda.get("txd").equals("4") && cda.get("rxd").equals("4")) {
						pw.print("\",\"" + "off" + "\",\"" + cda.get("power") + "\",\"" + "edge-mode" + "\",\""
								+ cda.get("pci") + "\",\"" + "n4-tx-antenna-count\",\"n4-rx-antenna-count" + "\",\""
								+ cda.get("earfcndl") + "\",\"");
					}

					else {
						String tx = "n" + cda.get("txd") + "-tx-antenna-count";
						String rx = "n" + cda.get("rxd") + "-rx-antenna-count";
						pw.print("\",\"" + "off" + "\",\"" + cda.get("power") + "\",\"" + "edge-mode" + "\",\""
								+ cda.get("pci") + "\",\"" + tx + "\",\"" + rx + "\",\"" + cda.get("earfcndl")
								+ "\",\"");
					}
					pw.print(cda.get("earfcnul") + "\",\"" + cda.get("band") + "\",\"" + cda.get("bandwidth") + "\",\""
							+ cda.get("crs") + "\",\"" + cda.get("emtc") + "\",\"" + "-" + "\",\"" + cda.get("tac")
							+ "\",\"" + "0" + "\",\"");
					pw.print("false" + "\",\"" + "12" + "\",\"" + cda.get("rach") + "\",\"\",\"" + "37" + "\",\""
							+ "-25" + "\",\"" + cda.get("PreferredEarfcn") + "\",\"" + "subframe-assignment-sa2"
							+ "\",\"" + "special-subframe-pattern-ssp7\",\"" + "spectrum-sharing-off\",\""
							+ "non-blanking\",\"" + "true\",\"" + "N 000:00:00.001\",\""
							+ "E 000:00:00.000\",\"0.00m\"\n");
				}
				if (cda.get("DspID").toString().equals("")) {
					pw.print("\"ADD" + "\",\"" + cda.get("sectid") + "\",\"" + cda.get("carrid") + "\",\""
							+ cda.get("carrierInDSPid") + "\",\"" + cda.get("dspid") + "\",\"" + cda.get("LCCnum")
							+ "\",\"" + cda.get("RUPortID") + "\",\"");
					pw.print(cda.get("cpristr") + "\",\"");
					if (cda.get("typename") != null) {
						pw.print(cda.get("typename"));
					}
					if (cda.get("txd").equals("2") && cda.get("rxd").equals("2")) {
						pw.print("\",\"" + "off" + "\",\"" + cda.get("power") + "\",\"" + "edge-mode" + "\",\""
								+ cda.get("pci") + "\",\"" + "n2-tx-antenna-count\",\"n2-rx-antenna-count" + "\",\""
								+ cda.get("earfcndl") + "\",\"");
					} else if (cda.get("txd").equals("2") && cda.get("rxd").equals("4")) {
						pw.print("\",\"" + "off" + "\",\"" + cda.get("power") + "\",\"" + "edge-mode" + "\",\""
								+ cda.get("pci") + "\",\"" + "n2-tx-antenna-count\",\"n4-rx-antenna-count" + "\",\""
								+ cda.get("earfcndl") + "\",\"");
					} else if (cda.get("txd").equals("4") && cda.get("rxd").equals("2")) {
						pw.print("\",\"" + "off" + "\",\"" + cda.get("power") + "\",\"" + "edge-mode" + "\",\""
								+ cda.get("pci") + "\",\"" + "n4-tx-antenna-count\",\"n2-rx-antenna-count" + "\",\""
								+ cda.get("earfcndl") + "\",\"");
					} else if (cda.get("txd").equals("4") && cda.get("rxd").equals("4")) {
						pw.print("\",\"" + "off" + "\",\"" + cda.get("power") + "\",\"" + "edge-mode" + "\",\""
								+ cda.get("pci") + "\",\"" + "n4-tx-antenna-count\",\"n4-rx-antenna-count" + "\",\""
								+ cda.get("earfcndl") + "\",\"");
					} else {
						String tx = "n" + cda.get("txd") + "-tx-antenna-count";
						String rx = "n" + cda.get("rxd") + "-rx-antenna-count";
						pw.print("\",\"" + "off" + "\",\"" + cda.get("power") + "\",\"" + "edge-mode" + "\",\""
								+ cda.get("pci") + "\",\"" + tx + "\",\"" + rx + "\",\"" + cda.get("earfcndl")
								+ "\",\"");
					}

					pw.print(cda.get("earfcnul") + "\",\"" + cda.get("band") + "\",\"" + cda.get("bandwidth") + "\",\""
							+ cda.get("crs") + "\",\"" + cda.get("emtc") + "\",\"" + "-" + "\",\"" + cda.get("tac")
							+ "\",\"" + "0" + "\",\"");
					pw.print("false" + "\",\"" + "12" + "\",\"" + cda.get("rach") + "\",\"\",\"" + cda.get("thMaxEirp")
							+ "\",\"" + "-25" + "\",\"" + cda.get("PreferredEarfcn") + "\",\""
							+ "subframe-assignment-sa2" + "\",\"" + "special-subframe-pattern-ssp7\",\""
							+ "spectrum-sharing-off\",\"" + "non-blanking\",\"" + "true\",\"" + "N 000:00:00.001\",\""
							+ "E 000:00:00.000\",\"0.00m\"\n");
				} else {
					pw.print("\"ADD" + "\",\"" + cda.get("sectid") + "\",\"" + cda.get("carrid") + "\",\""
							+ cda.get("DspCellIndex") + "\",\"" + cda.get("DspID") + "\",\"" + cda.get("LCCnum")
							+ "\",\"" + cda.get("RUPortID") + "\",\"");
					pw.print(cda.get("cpristr") + "\",\"");
					if (cda.get("typename") != null) {
						pw.print(cda.get("typename"));
					}
					if (cda.get("txd").equals("2") && cda.get("rxd").equals("2")) {
						pw.print("\",\"" + "off" + "\",\"" + cda.get("power") + "\",\"" + "edge-mode" + "\",\""
								+ cda.get("pci") + "\",\"" + "n2-tx-antenna-count\",\"n2-rx-antenna-count" + "\",\""
								+ cda.get("earfcndl") + "\",\"");
					} else if (cda.get("txd").equals("2") && cda.get("rxd").equals("4")) {
						pw.print("\",\"" + "off" + "\",\"" + cda.get("power") + "\",\"" + "edge-mode" + "\",\""
								+ cda.get("pci") + "\",\"" + "n2-tx-antenna-count\",\"n4-rx-antenna-count" + "\",\""
								+ cda.get("earfcndl") + "\",\"");
					} else if (cda.get("txd").equals("4") && cda.get("rxd").equals("2")) {
						pw.print("\",\"" + "off" + "\",\"" + cda.get("power") + "\",\"" + "edge-mode" + "\",\""
								+ cda.get("pci") + "\",\"" + "n4-tx-antenna-count\",\"n2-rx-antenna-count" + "\",\""
								+ cda.get("earfcndl") + "\",\"");
					} else if (cda.get("txd").equals("4") && cda.get("rxd").equals("4")) {
						pw.print("\",\"" + "off" + "\",\"" + cda.get("power") + "\",\"" + "edge-mode" + "\",\""
								+ cda.get("pci") + "\",\"" + "n4-tx-antenna-count\",\"n4-rx-antenna-count" + "\",\""
								+ cda.get("earfcndl") + "\",\"");
					} else {
						String tx = "n" + cda.get("txd") + "-tx-antenna-count";
						String rx = "n" + cda.get("rxd") + "-rx-antenna-count";
						pw.print("\",\"" + "off" + "\",\"" + cda.get("power") + "\",\"" + "edge-mode" + "\",\""
								+ cda.get("pci") + "\",\"" + tx + "\",\"" + rx + "\",\"" + cda.get("earfcndl")
								+ "\",\"");
					}

					pw.print(cda.get("earfcnul") + "\",\"" + cda.get("band") + "\",\"" + cda.get("bandwidth") + "\",\""
							+ cda.get("crs") + "\",\"" + cda.get("emtc") + "\",\"" + "-" + "\",\"" + cda.get("tac")
							+ "\",\"" + "0" + "\",\"");
					pw.print("false" + "\",\"" + "12" + "\",\"" + cda.get("rach") + "\",\"\",\"" + cda.get("thMaxEirp")
							+ "\",\"" + "-25" + "\",\"" + cda.get("PreferredEarfcn") + "\",\""
							+ "subframe-assignment-sa2" + "\",\"" + "special-subframe-pattern-ssp7\",\""
							+ "spectrum-sharing-off\",\"" + "non-blanking\",\"" + "true\",\"" + "N 000:00:00.001\",\""
							+ "E 000:00:00.000\",\"0.00m\"\n");
				}

				if (Integer.parseInt(cda.get("LCCnum").toString()) > lcccnt) {
					lcccnt = Integer.parseInt(cda.get("LCCnum").toString());
				}
			}

			pw.print("\"@NB_IOT_CELL_INFORMATION\"\n");
			pw.print(
					"\"State\",\"Cell Num\",\"Parent Cell Number\",\"NB IoT PCI\",\"Operation Mode Info\",\"NB IoT TAC\",\"Use Parent PCI for Guard-band\",\"Initial Nprach\",");
			pw.print(
					"\"Nprach Start Time CL0\",\"Nprach Subcarrier Offset CL0\",\"Nprach Start Time CL1\",\"Nprach Subcarrier Offset CL1\",\"Nprach Start Time CL2\",");
			pw.print("\"Nprach Subcarrier Offset CL2\",\"Guard Band\",\"Avoid UL Interfering\",\"DL RB\",\"UL RB\"\n");
			for (int i = 0; i < cellDataAref.size(); i++) {
				cda = cellDataAref.get(i);
				if (cda.get("band").toString().contains("700mhz") || cda.get("band").toString().contains("700")) {
					pw.print("\"ADD\",\"" + cda.get("cellid") + "\",\"" + cda.get("cellid") + "\",\"" + cda.get("pci")
							+ "\",\"" + "guard-band\",\"" + cda.get("iottac") + "\",\"on\",\"" + "on\",\""
							+ "nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\"");
					pw.print("nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\""
							+ "nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\"" + "right\",\""
							+ "false\",\"" + "45\",\"" + "40\"\n");
				} else {
					pw.print("\"NONE\",\"" + cda.get("cellid") + "\",\"" + cda.get("cellid") + "\",\"" + cda.get("pci")
							+ "\",\"guard-band\",\"" + "0\",\"" + "off\",\"" + "off\",\"" + "nprach-start-time-ms8\",\""
							+ "nprach-subcarrier-offset-n36\",\"");
					pw.print("nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\""
							+ "nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\"" + "right\",\""
							+ "false\",\"" + "45\",\"" + "40\"\n");
				}
			}
			pw.print("\"@NON_ANCHOR_NB_IOT_CELL_INFORMATION\"\n");
			pw.print("\"State\",\"Cell Num\",\"Operation Mode Info\",\"Guard Band\",\"DL RB\",\"UL RB\"\n");
			for (int i = 0; i < rrhDataAref.size(); i++) {
				pw.print("\"NONE\",\"\",\"\",\"\",\"\",\"\"\n");
			}

			pw.print("\"@CHANNEL_BOARD_INFORMATION\"\n");
			pw.print("\"State\",\"Unit Type\",\"Unit ID\",\"Board Type\"\n");
			for (int i = 0; i <= lcccnt; i++) {
				pw.print("\"ADD\",\"ecp\",\"" + i + "\",\"lcc4-b1\"\n");
			}

			///////////////////// @cpri port information/////////////////

			// pw.print("\"@CPRI_PORT_INFORMATION\"\n");
			// pw.print("\"State\",\"Unit Type\",\"Unit ID\",\"Port ID\",\"Connection
			// Type\",\"FSU Inter Node ID\",\"FSU DU CPRI Port ID\"\n");

			pw.print("\"@CPRI_PORT_INFORMATION\"\n");
			pw.print(
					"\"State\",\"Unit Type\",\"Unit ID\",\"Port ID\",\"Connection Type\",\"FSU Inter Node ID\",\"FSU DU CPRI Port ID\"\n");
			for (int i = 0; i < rrhDataAref.size(); i++) {
				pw.print("\"ADD\",\"ecp\",\"" + rrhDataAref.get(i).get("LCCnum") + "\",\""
						+ rrhDataAref.get(i).get("port") + "\",\"" + "direct" + "\",\"" + "\",\"\"\n");
			}

			pw.print("\"@RU_INFORMATION\"\n");
			pw.print(
					"\"State\",\"RU Conf\",\"RU Port\",\"Connected DU Board Type\",\"RU Type\",\"Start Earfcn1\",\"Start Earfcn2\",\"Serial Number\",\"Azimuth\",\"Beamwidth\",");
			pw.print(
					"\"Fcc ID\",\"Call Sign\",\"CBSD Category\",\"X Pole Antenna\",\"Antenna Gain dBi\",\"Cable Loss\",\"Accuracy Margin dB\"\n");
			ArrayList<String> al = new ArrayList<>();
			for (int i = 0; i < rrhDataAref.size(); i++) {
				rda = rrhDataAref.get(i);

				if (rda.get("code").toString().equals("rt4401-480")) {

					((ArrayList<String>) rda.get("startearfcnA")).set(0, "55240");
					((ArrayList<String>) rda.get("startearfcnA")).set(1, "55240");
				}
				if (rda.get("code").toString().equals("rt2201-460")) {

					((ArrayList<String>) rda.get("startearfcnA")).set(0, "46790");
					((ArrayList<String>) rda.get("startearfcnA")).set(1, "46790");
				}
				pw.print("\"ADD" + "\",\"" + rda.get("cpristr") + "\",\"" + "0" + "\",\"" + "ecp" + "\",\""
						+ rda.get("code") + "\",\"" + ((ArrayList<String>) rda.get("startearfcnA")).get(0) + "\",\""
						+ ((ArrayList<String>) rda.get("startearfcnA")).get(1) + "\",\"" + "\",\"" + "-1" + "\",\""
						+ "700\",\"");
				pw.print(((ArrayList<String>) rda.get("fccId")).get(0) + "\",\"" + "\",\"" + "cbsd-b" + "\",\"" + "true"
						+ "\",\"" + ((ArrayList<String>) rda.get("antennaGains")).get(0) + "\",\"" + "0" + "\",\""
						+ "10\"\n");

			}

			ArrayList<String> addCpri = new ArrayList<String>();
			pw.print("\"@ADDITIONAL_CPRI_INFORMATION\"\n");
			pw.print(
					"\"State\",\"RU Conf\",\"Connected DU Board Type\",\"Additional Board ID\",\"Additional Port ID\",\"RU Additional Port ID\"\n");
			for (int i = 0; i < cellDataAref.size(); i++) {
				cda = cellDataAref.get(i);
				if ((cda.get("RUPortID").toString()).equals("1")) {
					if (!addCpri.contains(cda.get("cpristr").toString())) {
						pw.print("\"ADD" + "\",\"" + cda.get("cpristr") + "\",\"" + "ecp" + "\",\"" + cda.get("LCCnum")
								+ "\",\"" + cda.get("additional_port") + "\",\"" + cda.get("RUPortID") + "\"\n");
						addCpri.add((String) cda.get("cpristr"));
					}
				}
			}

			int a1 = 0;
			int a2 = 0;
			int a3 = 0;
			int a4 = 0;
			int a5 = 0;
			int a6 = 0;

			pw.print("\"@RU_ANTENNA_PORT_INFORMATION\"\n");
			pw.print(
					"\"State\",\"RU Conf\",\"Connected DU Board Type\",\"Antenna Port ID\",\"Antenna Cable Length\"\n");
			for (int i = 0; i < rrhDataAref.size(); i++) {
				for (int j = 0; j < ((int[]) rrhDataAref.get(i).get("antennaPortMapA")).length; j++) {
					if (((int[]) rrhDataAref.get(i).get("antennaPortMapA"))[j] != -1) {
						int antport = j + 1;
						if ((rrhDataAref.get(i).get("cpristr").toString()).equals("2_6_0")) {
							a1++;
							if (a1 < 5) {
								pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "ecp"
										+ "\",\"" + antport + "\",\""
										+ ((int[]) rrhDataAref.get(i).get("antennaPortMapA"))[j]);
								if (j < ((int[]) (rrhDataAref.get(i).get("antennaPortMapA"))).length) {
									pw.print("\"");
								}
								pw.println();
							}
						} else if ((rrhDataAref.get(i).get("cpristr").toString()).equals("1_6_0")) {
							a4++;
							if (a4 < 5) {
								pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "ecp"
										+ "\",\"" + antport + "\",\""
										+ ((int[]) rrhDataAref.get(i).get("antennaPortMapA"))[j]);
								if (j < ((int[]) (rrhDataAref.get(i).get("antennaPortMapA"))).length) {
									pw.print("\"");
								}
								pw.println();
							}
						} else if ((rrhDataAref.get(i).get("cpristr").toString()).contains("2_8_0")) {
							a2++;
							if (a2 < 5) {
								pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "ecp"
										+ "\",\"" + antport + "\",\""
										+ ((int[]) rrhDataAref.get(i).get("antennaPortMapA"))[j]);
								if (j < ((int[]) (rrhDataAref.get(i).get("antennaPortMapA"))).length) {
									pw.print("\"");
								}
								pw.println();
							}
						} else if ((rrhDataAref.get(i).get("cpristr").toString()).contains("1_8_0")) {
							a5++;
							if (a5 < 5) {
								pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "ecp"
										+ "\",\"" + antport + "\",\""
										+ ((int[]) rrhDataAref.get(i).get("antennaPortMapA"))[j]);
								if (j < ((int[]) (rrhDataAref.get(i).get("antennaPortMapA"))).length) {
									pw.print("\"");
								}
								pw.println();
							}
						} else if ((rrhDataAref.get(i).get("cpristr").toString()).contains("2_10_0")) {
							a6++;
							if (a6 < 5) {
								pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "ecp"
										+ "\",\"" + antport + "\",\""
										+ ((int[]) rrhDataAref.get(i).get("antennaPortMapA"))[j]);
								if (j < ((int[]) (rrhDataAref.get(i).get("antennaPortMapA"))).length) {
									pw.print("\"");
								}
								pw.println();
							}
						} else if ((rrhDataAref.get(i).get("cpristr").toString()).contains("1_10_0")) {
							a3++;
							if (a3 < 5) {
								pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "ecp"
										+ "\",\"" + antport + "\",\""
										+ ((int[]) rrhDataAref.get(i).get("antennaPortMapA"))[j]);
								if (j < ((int[]) (rrhDataAref.get(i).get("antennaPortMapA"))).length) {
									pw.print("\"");
								}
								pw.println();
							}
						} else {
							pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "ecp" + "\",\""
									+ antport + "\",\"" + ((int[]) rrhDataAref.get(i).get("antennaPortMapA"))[j]);
							if (j < ((int[]) (rrhDataAref.get(i).get("antennaPortMapA"))).length) {
								pw.print("\"");
							}
							pw.println();
						}
					}
				}
			}

			int counta = 0;
			int countb = 0;
			int countc = 0;
			int countd = 0;
			int counte = 0;
			int countf = 0;
			pw.print("\"@RU_GROUP_INFORMATION\"\n");
			pw.print("\"State\",\"RU Conf\",\"Group ID\"\n");
			for (int i = 0; i < rrhDataAref.size(); i++) {
				if ((rrhDataAref.get(i).get("cpristr").toString()).equals("2_6_0")) {
					counta++;
					if (counta < 2) {
						pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "0\"\n");
					}
				} else if ((rrhDataAref.get(i).get("cpristr").toString()).equals("1_6_0")) {
					countd++;
					if (countd < 2) {
						pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "0\"\n");
					}
				} else if ((rrhDataAref.get(i).get("cpristr").toString()).contains("2_8_0")) {
					countb++;
					if (countb < 2) {
						pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "0\"\n");
					}
				} else if ((rrhDataAref.get(i).get("cpristr").toString()).contains("1_8_0")) {
					counte++;
					if (counte < 2) {
						pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "0\"\n");
					}
				} else if ((rrhDataAref.get(i).get("cpristr").toString()).contains("2_10_0")) {
					countf++;
					if (countf < 2) {
						pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "0\"\n");
					}
				} else if ((rrhDataAref.get(i).get("cpristr").toString()).contains("1_10_0")) {
					countc++;
					if (countc < 2) {
						pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "0\"\n");
					}
				} else {
					pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "0\"\n");
				}
			}

			pw.print("\"@DSP_INFORMATION\"\n");
			pw.print("\"State\",\"Unit Type\",\"Unit ID\",\"DSP ID\",\"Optic Distance\"\n");

			ArrayList<Integer> cardsProvisioned = new ArrayList<Integer>();
			for (int ci = 0; ci < cellDataAref.size(); ci++) {
				if (Integer.parseInt(cellDataAref.get(ci).get("LCCnum").toString()) == cardsProvisioned.size()) {
					cardsProvisioned.add(cardsProvisioned.size());
					continue;
				}
			}
			for (int li = 0; li < cardsProvisioned.size(); li++) {
				for (int di = 0; di < lccAref.get(li).size(); di++) {
					if ((Integer.parseInt(lccAref.get(li).get(di).get("opticDistance").toString())) == 0) {
						pw.print("\"ADD\",\"ecp\",\"" + li + "\",\"" + di + "\",\"10-km\"\n");
					} else {
						pw.print("\"ADD\",\"ecp\",\"" + li + "\",\"" + di + "\",\""
								+ lccAref.get(li).get(di).get("opticDistance") + "-km\"\n");
					}
				}
			}

			////////////////////// @CBRS_CHANNEL_INFORMATION////////////////////////////
			pw.print("@CBRS_CHANNEL_INFORMATION\n");
			pw.print("State,Sector ID,Carrier ID,Black Listed Channel\n");
			pw.println("NONE" + "," + "" + "," + "" + "," + "");
		} catch (Exception e) {
			logger.error("Exception  in RunTestServiceImpl getRunTestDetails():" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			if (pw != null) {
				pw.flush();
				pw.close();
			}
		}

	}

	private static void createGrowTemplate_20C0(ArrayList<TreeMap<String, Object>> cellDataAref,
			ArrayList<TreeMap<String, Object>> rrhDataAref, ArrayList<ArrayList<TreeMap<String, Object>>> lccAref,
			String growfile) {
		PrintWriter pw = null;
		try {
			String val = "";
			File f = new File(growfile);
			f.createNewFile();
			pw = new PrintWriter(new BufferedWriter(new FileWriter(f)));

			add_optic_distance_to_dsps(lccAref, cellDataAref);

			int lcccnt = 0;
			pw.print("\"@CELL_INFORMATION\"\n");
			pw.print(
					"\"State\",\"Sector ID\",\"Carrier ID\",\"CC ID\",\"DSP ID\",\"Cell Index in DSP\",\"RU Conf\",\"RU Port ID\",");
			pw.print(
					"\"Cell Band Carrier\",\"Earfcn DL\",\"Earfcn UL\",\"Bandwidth\",\"DL Antenna Count\",\"UL Antenna Count\",");
			pw.print(
					"\"Multi Carrier Type\",\"CRS\",\"PCI\",\"TAC\",\"EAID\",\"HSF\",\"ZCZC\",\"RSI\",\"PRACH Configuration Index\",\"Virtual RF Port Mapping\",\"eMTC\",\"Subframe Assignment\",\"Special Subframe Patterns\",");
			pw.print(
					"\"Frequency Profile\",\"Dl Max Tx Power\",\"Pucch Center Mode\",\"Rcc ID\",\"TH MaxEirp\",\"TH RSSI\",\"Preferred Earfcn\",\"CBRS Carrier Update Enable\",\"Dynamic Spectrum Sharing Mode\",\"DSS Target NR Cell Num\",\"Slot Level Operation Mode\",\"CDMA Blanking Case\",\"CDMA Blanking Upper\",\"CDMA Blanking Lower\",\"Term Point To DSS Index\",\"Auto GPS\",\"Latitude\",\"Longitude\",\"Height\"\n");

			TreeMap<String, Object> cda = new TreeMap<String, Object>();
			TreeMap<String, Object> rda = new TreeMap<String, Object>();

			for (int i = 0; i < cellDataAref.size(); i++) {

				cda = cellDataAref.get(i);

				if (Integer.parseInt(cda.get("earfcndl").toString()) >= 5180
						&& Integer.parseInt(cda.get("earfcndl").toString()) <= 5279) {
					cda.replace("band", "700mhz");
				} else if (cda.get("band").equals("CBRS") || (Integer.parseInt(cda.get("earfcndl").toString()) >= 55240
						&& Integer.parseInt(cda.get("earfcndl").toString()) <= 56739)) {
					cda.replace("band", "3500mhz");
					cda.replace("earfcndl", "0");
					cda.replace("earfcnul", "0");

				} else if ((Integer.parseInt(cda.get("earfcndl").toString()) >= 1950
						&& Integer.parseInt(cda.get("earfcndl").toString()) <= 2399)) {
					cda.replace("band", "2100mhz_band4");
				} else if (Integer.parseInt(cda.get("earfcndl").toString()) >= 600
						&& Integer.parseInt(cda.get("earfcndl").toString()) <= 1199) {
					cda.replace("band", "1900mhz");

				} else if (Integer.parseInt(cda.get("earfcndl").toString()) >= 2400
						&& Integer.parseInt(cda.get("earfcndl").toString()) <= 2649) {
					cda.replace("band", "850mhz");
				} else if ((Integer.parseInt(cda.get("earfcndl").toString()) >= 66436
						&& Integer.parseInt(cda.get("earfcndl").toString()) <= 67335)) {
					if (cda.get("DspCellIndex").equals("4") || cda.get("DspCellIndex").equals("5")) {
						cda.replace("typename", "cfg61-multi-carrier-10m-5m-6cell");
					}

					cda.replace("band", "2100mhz_band66");
				} else if (cda.get("band").equals("LAA") || (Integer.parseInt(cda.get("earfcndl").toString()) >= 46790
						&& Integer.parseInt(cda.get("earfcndl").toString()) <= 54539)) {
					cda.replace("band", "5000mhz");
					cda.replace("earfcndl", "0");
					cda.replace("earfcnul", "0");
				} else if ((Integer.parseInt(cda.get("earfcndl").toString()) >= 8040
						&& Integer.parseInt(cda.get("earfcndl").toString()) <= 8689)) {
					cda.replace("band", "1900mhz_band25");
				}
				if (cda.get("bandwidth").equals("10")) {
					cda.replace("bandwidth", "system-bandwidth-n50");
				} else if (cda.get("bandwidth").equals("20")) {
					cda.replace("bandwidth", "system-bandwidth-n100");

				} else if (cda.get("bandwidth").equals("15")) {
					cda.replace("bandwidth", "system-bandwidth-n75");
				} else if (cda.get("bandwidth").equals("5")) {
					cda.replace("bandwidth", "system-bandwidth-n25");
					;
				}

				pw.print("\"ADD" + "\",\"" + cda.get("sectid") + "\",\"" + cda.get("carrid") + "\",\""
						+ cda.get("LCCnum") + "\",\"" + cda.get("DspID") + "\",\"" + cda.get("DspCellIndex") + "\",\""
						+ cda.get("cpristr") + "\",\"");
				pw.print(cda.get("RUPortID") + "\",\"");

				pw.print(cda.get("band"));

				if (cda.get("txd").equals("2") && cda.get("rxd").equals("2")) {
					pw.print("\",\"" + cda.get("earfcndl") + "\",\"" + cda.get("earfcnul") + "\",\""
							+ cda.get("bandwidth") + "\",\"" + "n2-tx-antenna-count\",\"n2-rx-antenna-count" + "\",\""
							+ cda.get("mct").toString().toLowerCase() + "\",\"");
				} else if (cda.get("txd").equals("2") && cda.get("rxd").equals("4")) {
					pw.print("\",\"" + cda.get("earfcndl") + "\",\"" + cda.get("earfcnul") + "\",\""
							+ cda.get("bandwidth") + "\",\"" + "n2-tx-antenna-count\",\"n4-rx-antenna-count" + "\",\""
							+ cda.get("mct").toString().toLowerCase() + "\",\"");
				} else if (cda.get("txd").equals("4") && cda.get("rxd").equals("2")) {
					pw.print("\",\"" + cda.get("earfcndl") + "\",\"" + cda.get("earfcnul") + "\",\""
							+ cda.get("bandwidth") + "\",\"" + "n4-tx-antenna-count\",\"n2-rx-antenna-count" + "\",\""
							+ cda.get("mct").toString().toLowerCase() + "\",\"");
				} else if (cda.get("txd").equals("4") && cda.get("rxd").equals("4")) {
					pw.print("\",\"" + cda.get("earfcndl") + "\",\"" + cda.get("earfcnul") + "\",\""
							+ cda.get("bandwidth") + "\",\"" + "n4-tx-antenna-count\",\"n4-rx-antenna-count" + "\",\""
							+ cda.get("mct").toString().toLowerCase() + "\",\"");
				} else {
					String tx = "n" + cda.get("txd") + "-tx-antenna-count";
					String rx = "n" + cda.get("rxd") + "-rx-antenna-count";
					pw.print("\",\"" + cda.get("earfcndl") + "\",\"" + cda.get("earfcnul") + "\",\""
							+ cda.get("bandwidth") + "\",\"" + tx + "\",\"" + rx + "\",\""
							+ cda.get("mct").toString().toLowerCase() + "\",\"");
				}

				pw.print(cda.get("crs") + "\",\"" + cda.get("pci") + "\",\"" + cda.get("tac") + "\",\"" + "0" + "\",\""
						+ "false" + "\",\"" + cda.get("zczc") + "\",\"" + cda.get("rach") + "\",\""
						+ cda.get("pracformat") + "\",\"" + "off" + "\",\"");
				pw.print(cda.get("emtc") + "\",\"" + "subframe-assignment-sa2" + "\",\""
						+ "special-subframe-pattern-ssp7\",\"" + "-" + "\",\"" + cda.get("power") + "\",\""
						+ "edge-mode" + "\",\"" + "0" + "\",\"" + cda.get("thMaxEirp") + "\",\"" + "-25" + "\",\""
						+ cda.get("PreferredEarfcn") + "\",\"" + "update-mode1" + "\",\"" + "spectrum-sharing-off\",\""
						+ "0\",\"" + "frame-level-dss-mode\",\"" + "non-blanking\",\"" + "0\",\"" + "0\",\"" + "0\",\""
						+ "true\",\"" + "N 000:00:00.000\",\"" + "E 000:00:00.000\",\"0.00m\"\n");

			}

			if (cda.containsKey("LCCnum") && Integer.parseInt(cda.get("LCCnum").toString()) > lcccnt) {
				lcccnt = Integer.parseInt(cda.get("LCCnum").toString());
			}

			pw.print("\"@NB_IOT_CELL_INFORMATION\"\n");
			pw.print(
					"\"State\",\"Cell Num\",\"Parent Cell Number\",\"NB IoT PCI\",\"Operation Mode Info\",\"NB IoT TAC\",\"Use Parent PCI for Guard-band\",\"Initial Nprach\",");
			pw.print(
					"\"Nprach Start Time CL0\",\"Nprach Subcarrier Offset CL0\",\"Nprach Start Time CL1\",\"Nprach Subcarrier Offset CL1\",\"Nprach Start Time CL2\",");
			pw.print("\"Nprach Subcarrier Offset CL2\",\"Guard Band\",\"Avoid UL Interfering\",\"DL RB\",\"UL RB\"\n");
			for (int i = 0; i < cellDataAref.size(); i++) {
				cda = cellDataAref.get(i);
				if (cda.get("band").toString().contains("700mhz") || cda.get("band").toString().contains("700")) {
					pw.print("\"ADD\",\"" + cda.get("cellid") + "\",\"" + cda.get("cellid") + "\",\"" + cda.get("pci")
							+ "\",\"" + "guard-band\",\"" + cda.get("iottac") + "\",\"on\",\"" + "on\",\""
							+ "nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\"");
					pw.print("nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\""
							+ "nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\"" + "right\",\""
							+ "false\",\"" + "45\",\"" + "40\"\n");
				} else {
					pw.print("\"NONE\",\"" + cda.get("cellid") + "\",\"" + cda.get("cellid") + "\",\"" + cda.get("pci")
							+ "\",\"guard-band\",\"" + "0\",\"" + "off\",\"" + "off\",\"" + "nprach-start-time-ms8\",\""
							+ "nprach-subcarrier-offset-n36\",\"");
					pw.print("nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\""
							+ "nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\"" + "right\",\""
							+ "false\",\"" + "45\",\"" + "40\"\n");
				}
			}
			pw.print("\"@NON_ANCHOR_NB_IOT_CELL_INFORMATION\"\n");
			pw.print("\"State\",\"Cell Num\",\"Operation Mode Info\",\"Guard Band\",\"DL RB\",\"UL RB\"\n");
			for (int i = 0; i < rrhDataAref.size(); i++) {
				pw.print("\"NONE\",\"\",\"\",\"\",\"\",\"\"\n");
			}

			pw.print("\"@CHANNEL_BOARD_INFORMATION\"\n");
			pw.print("\"State\",\"Unit Type\",\"Unit ID\",\"Board Type\"\n");
			for (int i = 0; i <= lcccnt; i++) {
				pw.print("\"ADD\",\"ecp\",\"" + i + "\",\"lcc4-b1\"\n");
			}

			///////////////////// @cpri port information/////////////////

			// pw.print("\"@CPRI_PORT_INFORMATION\"\n");
			// pw.print("\"State\",\"Unit Type\",\"Unit ID\",\"Port ID\",\"Connection
			// Type\",\"FSU Inter Node ID\",\"FSU DU CPRI Port ID\"\n");

			pw.print("\"@CPRI_PORT_INFORMATION\"\n");
			pw.print(
					"\"State\",\"Unit Type\",\"Unit ID\",\"Port ID\",\"Connection Type\",\"FSU Inter Node ID\",\"FSU DU CPRI Port ID\"\n");
			ArrayList<String> cpri = new ArrayList<String>();
			ArrayList<String> cprival = new ArrayList<String>();
			for (int i = 0; i < cellDataAref.size(); i++) {
				cda = cellDataAref.get(i);

				if (!cpri.contains(cda.get("cpristr").toString())) {
					pw.print("\"ADD\",\"ecp\",\"" + cda.get("LCCnum") + "\",\"" + cda.get("port") + "\",\"" + "direct"
							+ "\",\"" + "\",\"\"\n");
					cpri.add((String) cda.get("cpristr"));
				}
				if ((cda.get("RUPortID").toString()).equals("1") && !cprival.contains(cda.get("cpristr").toString())) {
					pw.print("\"ADD\",\"ecp\",\"" + cda.get("LCCnum") + "\",\"" + cda.get("additional_port") + "\",\""
							+ "direct" + "\",\"" + "\",\"\"\n");
					cprival.add((String) cda.get("cpristr"));

				}
			}

			pw.print("\"@RU_INFORMATION\"\n");
			pw.print(
					"\"State\",\"RU Conf\",\"RU Port\",\"Connected DU Board Type\",\"RU Type\",\"Start Earfcn1\",\"Start Earfcn2\",\"Serial Number\",\"Azimuth\",\"Beamwidth\",");
			pw.print(
					"\"Fcc ID\",\"Call Sign\",\"CBSD Category\",\"X Pole Antenna\",\"Antenna Gain dBi\",\"Cable Loss\",\"Accuracy Margin dB\"\n");
			ArrayList<String> al = new ArrayList<>();
			for (int i = 0; i < rrhDataAref.size(); i++) {
				rda = rrhDataAref.get(i);

				if (rda.get("code").toString().equals("rt4401-480")) {

					((ArrayList<String>) rda.get("startearfcnA")).set(0, "55240");
					((ArrayList<String>) rda.get("startearfcnA")).set(1, "55240");
				}
				if (rda.get("code").toString().equals("rt2201-460")) {

					((ArrayList<String>) rda.get("startearfcnA")).set(0, "46790");
					((ArrayList<String>) rda.get("startearfcnA")).set(1, "46790");
				}
				pw.print("\"ADD" + "\",\"" + rda.get("cpristr") + "\",\"" + "0" + "\",\"" + "ecp" + "\",\""
						+ rda.get("code") + "\",\"" + ((ArrayList<String>) rda.get("startearfcnA")).get(0) + "\",\""
						+ ((ArrayList<String>) rda.get("startearfcnA")).get(1) + "\",\"" + "\",\"" + "-1" + "\",\""
						+ "700\",\"");
				pw.print("A3LRT4401-48A" + "\",\"" + "na" + "\",\"" + "cbrs-not-avail" + "\",\"" + "true" + "\",\""
						+ ((ArrayList<String>) rda.get("antennaGains")).get(0) + "\",\"" + "0" + "\",\"" + "10\"\n");

			}

			ArrayList<String> addCpri = new ArrayList<String>();
			pw.print("\"@ADDITIONAL_CPRI_INFORMATION\"\n");
			pw.print(
					"\"State\",\"RU Conf\",\"Connected DU Board Type\",\"Additional Board ID\",\"Additional Port ID\",\"RU Additional Port ID\"\n");
			for (int i = 0; i < cellDataAref.size(); i++) {
				cda = cellDataAref.get(i);
				if ((cda.get("RUPortID").toString()).equals("1")) {
					if (!addCpri.contains(cda.get("cpristr").toString())) {
						pw.print("\"ADD" + "\",\"" + cda.get("cpristr") + "\",\"" + "ecp" + "\",\"" + cda.get("LCCnum")
								+ "\",\"" + cda.get("additional_port") + "\",\"" + cda.get("RUPortID") + "\"\n");
						addCpri.add((String) cda.get("cpristr"));
					}
				}
			}

			int a1 = 0;
			int a2 = 0;
			int a3 = 0;
			int a4 = 0;
			int a5 = 0;
			int a6 = 0;

			pw.print("\"@RU_ANTENNA_PORT_INFORMATION\"\n");
			pw.print(
					"\"State\",\"RU Conf\",\"Connected DU Board Type\",\"Antenna Port ID\",\"Antenna Cable Length\"\n");
			for (int i = 0; i < rrhDataAref.size(); i++) {
				for (int j = 0; j < ((int[]) rrhDataAref.get(i).get("antennaPortMapA")).length; j++) {
					if (((int[]) rrhDataAref.get(i).get("antennaPortMapA"))[j] != -1) {
						int antport = j + 1;
						if ((rrhDataAref.get(i).get("cpristr").toString()).equals("2_6_0")) {
							a1++;
							if (a1 < 5) {
								pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "ecp"
										+ "\",\"" + antport + "\",\""
										+ ((int[]) rrhDataAref.get(i).get("antennaPortMapA"))[j]);
								if (j < ((int[]) (rrhDataAref.get(i).get("antennaPortMapA"))).length) {
									pw.print("\"");
								}
								pw.println();
							}
						} else if ((rrhDataAref.get(i).get("cpristr").toString()).equals("1_6_0")) {
							a4++;
							if (a4 < 5) {
								pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "ecp"
										+ "\",\"" + antport + "\",\""
										+ ((int[]) rrhDataAref.get(i).get("antennaPortMapA"))[j]);
								if (j < ((int[]) (rrhDataAref.get(i).get("antennaPortMapA"))).length) {
									pw.print("\"");
								}
								pw.println();
							}
						} else if ((rrhDataAref.get(i).get("cpristr").toString()).contains("2_8_0")) {
							a2++;
							if (a2 < 5) {
								pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "ecp"
										+ "\",\"" + antport + "\",\""
										+ ((int[]) rrhDataAref.get(i).get("antennaPortMapA"))[j]);
								if (j < ((int[]) (rrhDataAref.get(i).get("antennaPortMapA"))).length) {
									pw.print("\"");
								}
								pw.println();
							}
						} else if ((rrhDataAref.get(i).get("cpristr").toString()).contains("1_8_0")) {
							a5++;
							if (a5 < 5) {
								pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "ecp"
										+ "\",\"" + antport + "\",\""
										+ ((int[]) rrhDataAref.get(i).get("antennaPortMapA"))[j]);
								if (j < ((int[]) (rrhDataAref.get(i).get("antennaPortMapA"))).length) {
									pw.print("\"");
								}
								pw.println();
							}
						} else if ((rrhDataAref.get(i).get("cpristr").toString()).contains("2_10_0")) {
							a6++;
							if (a6 < 5) {
								pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "ecp"
										+ "\",\"" + antport + "\",\""
										+ ((int[]) rrhDataAref.get(i).get("antennaPortMapA"))[j]);
								if (j < ((int[]) (rrhDataAref.get(i).get("antennaPortMapA"))).length) {
									pw.print("\"");
								}
								pw.println();
							}
						} else if ((rrhDataAref.get(i).get("cpristr").toString()).contains("1_10_0")) {
							a3++;
							if (a3 < 5) {
								pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "ecp"
										+ "\",\"" + antport + "\",\""
										+ ((int[]) rrhDataAref.get(i).get("antennaPortMapA"))[j]);
								if (j < ((int[]) (rrhDataAref.get(i).get("antennaPortMapA"))).length) {
									pw.print("\"");
								}
								pw.println();
							}
						} else {
							pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "ecp" + "\",\""
									+ antport + "\",\"" + ((int[]) rrhDataAref.get(i).get("antennaPortMapA"))[j]);
							if (j < ((int[]) (rrhDataAref.get(i).get("antennaPortMapA"))).length) {
								pw.print("\"");
							}
							pw.println();
						}
					}
				}
			}

			pw.print("\"@RU_GROUP_INFORMATION\"\n");
			pw.print("\"State\",\"RU Conf\",\"Group ID\"\n");
			Set<String> ruGroupInfo = new HashSet<>();
			for (int i = 0; i < cellDataAref.size(); i++) {
				cda = cellDataAref.get(i);
				if ((cda.get("RUPortID").toString()).equals("0")) {
					ruGroupInfo.add((String) cda.get("cpristr"));
				}

			}
			for (String value : ruGroupInfo) {
				pw.print("\"ADD" + "\",\"" + value + "\",\"" + "0\"\n");
			}
			pw.print("\"@DSP_INFORMATION\"\n");
			pw.print("\"State\",\"Unit Type\",\"Unit ID\",\"DSP ID\",\"Optic Distance\"\n");

			ArrayList<Integer> cardsProvisioned = new ArrayList<Integer>();
			for (int ci = 0; ci < cellDataAref.size(); ci++) {
				if (Integer.parseInt(cellDataAref.get(ci).get("LCCnum").toString()) == cardsProvisioned.size()) {
					cardsProvisioned.add(cardsProvisioned.size());
					continue;
				}
			}
			for (int li = 0; li < cardsProvisioned.size(); li++) {
				for (int di = 0; di < lccAref.get(li).size(); di++) {
					if ((Integer.parseInt(lccAref.get(li).get(di).get("opticDistance").toString())) == 0) {
						pw.print("\"ADD\",\"ecp\",\"" + li + "\",\"" + di + "\",\"20-km\"\n");
					} else {
						pw.print("\"ADD\",\"ecp\",\"" + li + "\",\"" + di + "\",\""
								+ lccAref.get(li).get(di).get("opticDistance") + "-km\"\n");
					}
				}
			}

			////////////////////// @CBRS_CHANNEL_INFORMATION////////////////////////////
			pw.print("@CBRS_CHANNEL_INFORMATION\n");
			pw.print("State,Sector ID,Carrier ID,Black Listed Channel\n");
			pw.println("NONE" + "," + "" + "," + "" + "," + "");
		} catch (Exception e) {
			mydie(e.toString());
		} finally {
			if (pw != null) {
				pw.flush();
				pw.close();
			}
		}

	}

	@SuppressWarnings("unchecked")
	private static void createGrowTemplate_900(ArrayList<TreeMap<String, Object>> cellDataAref,
			ArrayList<TreeMap<String, Object>> rrhDataAref, ArrayList<ArrayList<TreeMap<String, Object>>> lccAref,
			String growfile) {

		PrintWriter pw = null;
		try {

			File f = new File(growfile);
			f.createNewFile();
			pw = new PrintWriter(new BufferedWriter(new FileWriter(f)));
			TreeMap<String, Object> cdar = new TreeMap<String, Object>();
			for (int i = 0; i < cellDataAref.size(); i++) {
				cdar = cellDataAref.get(i);
				if (cdar.get("band").equals("700MHz") || cdar.get("band").equals("850MHz")
						|| cdar.get("band").equals("PCS")) {
					cdar.replace("typename", "cfg355-multi-carrier-10m-10m-10m-10m-5m-5m-5m-5m-config3");
				} else if (cdar.get("band").equals("CBRS") || cdar.get("band").equals("LAA")) {
					cdar.replace("typename", "cfg388-multi-carrier-20m-20m-20m-20m-config3");
				}
			}
			// new implementation
			int flag = 0;
			String channelCord = null;
			TreeMap<String, Object> cdas = new TreeMap<String, Object>();
			ArrayList<String> ds1 = new ArrayList<>();
			ArrayList<String> ds2 = new ArrayList<>();
			ArrayList<String> ds3 = new ArrayList<>();
			ArrayList<String> cin1 = new ArrayList<>();
			ArrayList<String> cin2 = new ArrayList<>();
			ArrayList<String> cin3 = new ArrayList<>();
			ArrayList<String> ban1 = new ArrayList<>();
			ArrayList<String> ban2 = new ArrayList<>();
			ArrayList<String> ban3 = new ArrayList<>();
			for (int i1 = 0; i1 < cellDataAref.size(); i1++) {
				cdas = cellDataAref.get(i1);
				if (((String) cdas.get("cpriPortAssignment")).contains("LCC-1")
						&& (((String) cdas.get("band")).equals("LAA") || ((String) cdas.get("band")).equals("CBRS"))) {
					ds1.add((String) cdas.get("DspCellIndex"));
					cin1.add((String) cdas.get("DspID"));
					ban1.add((String) cdas.get("band"));
				} else if (((String) cdas.get("cpriPortAssignment")).contains("LCC-2")
						&& (((String) cdas.get("band")).equals("LAA") || ((String) cdas.get("band")).equals("CBRS"))) {
					ds2.add((String) cdas.get("DspCellIndex"));
					cin2.add((String) cdas.get("DspID"));
					ban2.add((String) cdas.get("band"));
				} else if (((String) cdas.get("cpriPortAssignment")).contains("LCC-3")
						&& (((String) cdas.get("band")).equals("LAA") || ((String) cdas.get("band")).equals("CBRS"))) {
					ds3.add((String) cdas.get("DspCellIndex"));
					cin3.add((String) cdas.get("DspID"));
					ban3.add((String) cdas.get("band"));
				}
			}
			int flag1 = 0, flag2 = 0, flag3 = 0;
			// Arrays.stream(strings).anyMatch(t -> t.equals(toFind));
			for (int g = 0; g < ds1.size(); g++) {
				if (ds1.get(g).equals("5")) {
					flag1 = 1;
				}
			}
			for (int g = 0; g < ds2.size(); g++) {
				if (ds2.get(g).equals("5")) {
					flag2 = 1;
				}
			}
			for (int g = 0; g < ds3.size(); g++) {
				if (ds3.get(g).equals("5")) {
					flag3 = 1;
				}
			}

			Set<String> s1 = new HashSet<>();
			Set<String> s2 = new HashSet<>();
			Set<String> s3 = new HashSet<>();

			if (flag1 == 1) {
				for (int i = 0; i < cin1.size(); i++) {
					s1.add(cin1.get(i));
				}
				if (s1.size() == 1) {
					for (int i1 = 0; i1 < cellDataAref.size(); i1++) {
						for (int i2 = 0; i2 < cin1.size(); i2++) {
							cdas = cellDataAref.get(i1);
							if (((String) cdas.get("DspCellIndex")).equals(ds1.get(i2))
									&& ((String) cdas.get("cpriPortAssignment")).contains("LCC-1")
									&& ((String) cdas.get("DspID")).equals(cin1.get(i2))
									&& ((String) cdas.get("band")).equals(ban1.get(i2))) {
								cdas.put("typename", "cfg63-multi-carrier-20m-2t2r-6cell");

							}
						}
					}
				}
			}
			if (flag2 == 1) {
				for (int i = 0; i < cin2.size(); i++) {
					s2.add(cin2.get(i));
				}
				if (s2.size() == 1) {
					for (int i1 = 0; i1 < cellDataAref.size(); i1++) {
						for (int i2 = 0; i2 < cin2.size(); i2++) {
							cdas = cellDataAref.get(i1);
							if (((String) cdas.get("DspCellIndex")).equals(ds2.get(i2))
									&& ((String) cdas.get("cpriPortAssignment")).contains("LCC-2")
									&& ((String) cdas.get("DspID")).equals(cin2.get(i2))
									&& ((String) cdas.get("band")).equals(ban2.get(i2))) {
								cdas.put("typename", "cfg63-multi-carrier-20m-2t2r-6cell");

							}
						}
					}
				}
			}
			if (flag3 == 1) {
				for (int i = 0; i < cin3.size(); i++) {
					s3.add(cin3.get(i));
				}
				if (s3.size() == 1) {
					for (int i1 = 0; i1 < cellDataAref.size(); i1++) {
						for (int i2 = 0; i2 < cin3.size(); i2++) {
							cdas = cellDataAref.get(i1);
							if (((String) cdas.get("DspCellIndex")).equals(ds3.get(i2))
									&& ((String) cdas.get("cpriPortAssignment")).contains("LCC-3")
									&& ((String) cdas.get("DspID")).equals(cin3.get(i2))
									&& ((String) cdas.get("band")).equals(ban3.get(i2))) {
								cdas.put("typename", "cfg63-multi-carrier-20m-2t2r-6cell");

							}
						}
					}
				}
			}

			int lcccnt = 0;
			pw.print("\"@CELL_INFORMATION\"\n");
			pw.print(
					"\"State\",\"Sector ID\",\"Carrier ID\",\"Cell Index in DSP\",\"DSP ID\",\"CC ID\",\"RU Port ID\",\"RU Conf\",");
			pw.print(
					"\"Multi Carrier Type\",\"Virtual RF Port Mapping\",\"Dl Max Tx Power\",\"Pucch Center Mode\",\"PCI\",\"DL Antenna Count\",\"UL Antenna Count\",");
			pw.print(
					"\"Earfcn DL\",\"Earfcn UL\",\"Cell Band Carrier\",\"Bandwidth\",\"CRS\",\"eMTC\",\"Frequency Profile\",\"TAC\",\"EAID\",\"HSF\",\"ZCZC\",\"RSI\",");
			pw.print(
					"\"Rcc ID\",\"TH MaxEirp\",\"TH RSSI\",\"Preferred Earfcn\",\"Subframe Assignment\",\"Special Subframe Patterns\",\"Auto GPS\",\"Latitude\",\"Longitude\",\"Height\"\n");

			TreeMap<String, Object> cda = new TreeMap<String, Object>();
			TreeMap<String, Object> rda = new TreeMap<String, Object>();

			for (int i = 0; i < cellDataAref.size(); i++) {

				cda = cellDataAref.get(i);

				if (cda.get("band").equals("700MHz")) {
					cda.replace("band", "700mhz");
				} else if (cda.get("band").equals("CBRS")) {
					cda.replace("band", "3500mhz");
					cda.replace("earfcndl", "0");
					cda.replace("earfcnul", "0");

				} else if (cda.get("band").equals("AWS-1")) {
					cda.replace("band", "2100mhz_band4");
				} else if (cda.get("band").equals("PCS")) {
					cda.replace("band", "1900mhz");

				} else if (cda.get("band").equals("850LTE")) {
					cda.replace("band", "850mhz");
				} else if (cda.get("band").equals("850MHz")) {
					cda.replace("band", "850mhz");
				} else if (cda.get("band").equals("AWS-2")) {
					cda.replace("band", "2100mhz_band4");
				} else if (cda.get("band").equals("AWS-3")) {
					if (cda.get("DspCellIndex").equals("3") || cda.get("DspCellIndex").equals("4")
							|| cda.get("DspCellIndex").equals("5")) {
						cda.replace("typename", "cfg61-multi-carrier-10m-5m-6cell");
					}

					cda.replace("band", "2100mhz_band66");
				} else if (cda.get("band").equals("PCS-2")) {
					cda.replace("band", "1900mhz");
				} else if (cda.get("band").equals("LAA")) {
					cda.replace("band", "5000mhz");
					cda.replace("earfcndl", "0");
					cda.replace("earfcnul", "0");
				}
				if (cda.get("bandwidth").equals("10")) {
					cda.replace("bandwidth", "system-bandwidth-n50");
				} else if (cda.get("bandwidth").equals("20")) {
					cda.replace("bandwidth", "system-bandwidth-n100");
				} else if (cda.get("bandwidth").equals("15")) {
					cda.replace("bandwidth", "system-bandwidth-n75");
				} else if (cda.get("bandwidth").equals("5")) {
					cda.replace("bandwidth", "system-bandwidth-n25");
					;
				}

				// if (cda.get(\"PreferredEarfcn'} eq "0"){
				// cda.get(\"PreferredEarfcn'} = "";
				// }
				if (cda.get("market").toString().equals("NEWENGLAND")) {
					pw.print("\"ADD" + "\",\"" + cda.get("sectid") + "\",\"" + cda.get("carrid") + "\",\""
							+ cda.get("carrierInDSPid") + "\",\"" + cda.get("dspid") + "\",\"" + cda.get("LCCnum")
							+ "\",\"" + "0\",\"");
					pw.print(cda.get("cpristr") + "\",\"");
					// For null Values
					if (cda.get("typename") != null) {
						pw.print(cda.get("typename"));
					}
					if (cda.get("txd").equals("2") && cda.get("rxd").equals("2")) {
						pw.print("\",\"" + "off" + "\",\"" + cda.get("power") + "\",\"" + "edge-mode" + "\",\""
								+ cda.get("pci") + "\",\"" + "n2-tx-antenna-count\",\"n2-rx-antenna-count" + "\",\""
								+ cda.get("earfcndl") + "\",\"");
					} else if (cda.get("txd").equals("2") && cda.get("rxd").equals("4")) {
						pw.print("\",\"" + "off" + "\",\"" + cda.get("power") + "\",\"" + "edge-mode" + "\",\""
								+ cda.get("pci") + "\",\"" + "n2-tx-antenna-count\",\"n4-rx-antenna-count" + "\",\""
								+ cda.get("earfcndl") + "\",\"");
					} else if (cda.get("txd").equals("4") && cda.get("rxd").equals("2")) {
						pw.print("\",\"" + "off" + "\",\"" + cda.get("power") + "\",\"" + "edge-mode" + "\",\""
								+ cda.get("pci") + "\",\"" + "n4-tx-antenna-count\",\"n2-rx-antenna-count" + "\",\""
								+ cda.get("earfcndl") + "\",\"");
					} else if (cda.get("txd").equals("4") && cda.get("rxd").equals("4")) {
						pw.print("\",\"" + "off" + "\",\"" + cda.get("power") + "\",\"" + "edge-mode" + "\",\""
								+ cda.get("pci") + "\",\"" + "n4-tx-antenna-count\",\"n4-rx-antenna-count" + "\",\""
								+ cda.get("earfcndl") + "\",\"");
					}
					pw.print(cda.get("earfcnul") + "\",\"" + cda.get("band") + "\",\"" + cda.get("bandwidth") + "\",\""
							+ cda.get("crs") + "\",\"" + cda.get("emtc") + "\",\"" + "-" + "\",\"" + cda.get("tac")
							+ "\",\"" + "0" + "\",\"");
					pw.print("false" + "\",\"" + "12" + "\",\"" + cda.get("rach") + "\",\"\",\"" + "37" + "\",\""
							+ "-25" + "\",\"" + cda.get("PreferredEarfcn") + "\",\"" + "subframe-assignment-sa2"
							+ "\",\"" + "special-subframe-pattern-ssp7\",\"" + "true\",\"" + "N 000:00:00.001\",\""
							+ "E 000:00:00.000\",\"0.00m\"\n");
				}
				if (cda.get("DspID").toString().equals("")) {
					pw.print("\"ADD" + "\",\"" + cda.get("sectid") + "\",\"" + cda.get("carrid") + "\",\""
							+ cda.get("carrierInDSPid") + "\",\"" + cda.get("dspid") + "\",\"" + cda.get("LCCnum")
							+ "\",\"" + cda.get("RUPortID") + "\",\"");
					pw.print(cda.get("cpristr") + "\",\"");
					if (cda.get("typename") != null) {
						pw.print(cda.get("typename"));
					}
					if (cda.get("txd").equals("2") && cda.get("rxd").equals("2")) {
						pw.print("\",\"" + "off" + "\",\"" + cda.get("power") + "\",\"" + "edge-mode" + "\",\""
								+ cda.get("pci") + "\",\"" + "n2-tx-antenna-count\",\"n2-rx-antenna-count" + "\",\""
								+ cda.get("earfcndl") + "\",\"");
					} else if (cda.get("txd").equals("2") && cda.get("rxd").equals("4")) {
						pw.print("\",\"" + "off" + "\",\"" + cda.get("power") + "\",\"" + "edge-mode" + "\",\""
								+ cda.get("pci") + "\",\"" + "n2-tx-antenna-count\",\"n4-rx-antenna-count" + "\",\""
								+ cda.get("earfcndl") + "\",\"");
					} else if (cda.get("txd").equals("4") && cda.get("rxd").equals("2")) {
						pw.print("\",\"" + "off" + "\",\"" + cda.get("power") + "\",\"" + "edge-mode" + "\",\""
								+ cda.get("pci") + "\",\"" + "n4-tx-antenna-count\",\"n2-rx-antenna-count" + "\",\""
								+ cda.get("earfcndl") + "\",\"");
					} else if (cda.get("txd").equals("4") && cda.get("rxd").equals("4")) {
						pw.print("\",\"" + "off" + "\",\"" + cda.get("power") + "\",\"" + "edge-mode" + "\",\""
								+ cda.get("pci") + "\",\"" + "n4-tx-antenna-count\",\"n4-rx-antenna-count" + "\",\""
								+ cda.get("earfcndl") + "\",\"");
					}
					pw.print(cda.get("earfcnul") + "\",\"" + cda.get("band") + "\",\"" + cda.get("bandwidth") + "\",\""
							+ cda.get("crs") + "\",\"" + cda.get("emtc") + "\",\"" + "-" + "\",\"" + cda.get("tac")
							+ "\",\"" + "0" + "\",\"");
					pw.print("false" + "\",\"" + "12" + "\",\"" + cda.get("rach") + "\",\"\",\"" + cda.get("thMaxEirp")
							+ "\",\"" + "-25" + "\",\"" + cda.get("PreferredEarfcn") + "\",\""
							+ "subframe-assignment-sa2" + "\",\"" + "special-subframe-pattern-ssp7\",\"" + "true\",\""
							+ "N 000:00:00.001\",\"" + "E 000:00:00.000\",\"0.00m\"\n");
				} else {
					pw.print("\"ADD" + "\",\"" + cda.get("sectid") + "\",\"" + cda.get("carrid") + "\",\""
							+ cda.get("DspCellIndex") + "\",\"" + cda.get("DspID") + "\",\"" + cda.get("LCCnum")
							+ "\",\"" + cda.get("RUPortID") + "\",\"");
					pw.print(cda.get("cpristr") + "\",\"");
					if (cda.get("typename") != null) {
						pw.print(cda.get("typename"));
					}
					if (cda.get("txd").equals("2") && cda.get("rxd").equals("2")) {
						pw.print("\",\"" + "off" + "\",\"" + cda.get("power") + "\",\"" + "edge-mode" + "\",\""
								+ cda.get("pci") + "\",\"" + "n2-tx-antenna-count\",\"n2-rx-antenna-count" + "\",\""
								+ cda.get("earfcndl") + "\",\"");
					} else if (cda.get("txd").equals("2") && cda.get("rxd").equals("4")) {
						pw.print("\",\"" + "off" + "\",\"" + cda.get("power") + "\",\"" + "edge-mode" + "\",\""
								+ cda.get("pci") + "\",\"" + "n2-tx-antenna-count\",\"n4-rx-antenna-count" + "\",\""
								+ cda.get("earfcndl") + "\",\"");
					} else if (cda.get("txd").equals("4") && cda.get("rxd").equals("2")) {
						pw.print("\",\"" + "off" + "\",\"" + cda.get("power") + "\",\"" + "edge-mode" + "\",\""
								+ cda.get("pci") + "\",\"" + "n4-tx-antenna-count\",\"n2-rx-antenna-count" + "\",\""
								+ cda.get("earfcndl") + "\",\"");
					} else if (cda.get("txd").equals("4") && cda.get("rxd").equals("4")) {
						pw.print("\",\"" + "off" + "\",\"" + cda.get("power") + "\",\"" + "edge-mode" + "\",\""
								+ cda.get("pci") + "\",\"" + "n4-tx-antenna-count\",\"n4-rx-antenna-count" + "\",\""
								+ cda.get("earfcndl") + "\",\"");
					}
					pw.print(cda.get("earfcnul") + "\",\"" + cda.get("band") + "\",\"" + cda.get("bandwidth") + "\",\""
							+ cda.get("crs") + "\",\"" + cda.get("emtc") + "\",\"" + "-" + "\",\"" + cda.get("tac")
							+ "\",\"" + "0" + "\",\"");
					pw.print("false" + "\",\"" + "12" + "\",\"" + cda.get("rach") + "\",\"\",\"" + cda.get("thMaxEirp")
							+ "\",\"" + "-25" + "\",\"" + cda.get("PreferredEarfcn") + "\",\""
							+ "subframe-assignment-sa2" + "\",\"" + "special-subframe-pattern-ssp7\",\"" + "true\",\""
							+ "N 000:00:00.001\",\"" + "E 000:00:00.000\",\"0.00m\"\n");
				}

				if (Integer.parseInt(cda.get("LCCnum").toString()) > lcccnt) {
					lcccnt = Integer.parseInt(cda.get("LCCnum").toString());
				}
			}

			pw.print("\"@NB_IOT_CELL_INFORMATION\"\n");
			pw.print(
					"\"State\",\"Cell Num\",\"Parent Cell Number\",\"NB IoT PCI\",\"Operation Mode Info\",\"NB IoT TAC\",\"Use Parent PCI for Guard-band\",\"Initial Nprach\",");
			pw.print(
					"\"Nprach Start Time CL0\",\"Nprach Subcarrier Offset CL0\",\"Nprach Start Time CL1\",\"Nprach Subcarrier Offset CL1\",\"Nprach Start Time CL2\",");
			pw.print("\"Nprach Subcarrier Offset CL2\",\"Guard Band\",\"Avoid UL Interfering\",\"DL RB\",\"UL RB\"\n");
			for (int i = 0; i < cellDataAref.size(); i++) {
				cda = cellDataAref.get(i);
				if (cda.get("band").toString().contains("700mhz")) {
					pw.print("\"ADD\",\"" + cda.get("cellid") + "\",\"" + cda.get("cellid") + "\",\"" + cda.get("pci")
							+ "\",\"" + "guard-band\",\"" + cda.get("iottac") + "\",\"on\",\"" + "on\",\""
							+ "nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\"");
					pw.print("nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\""
							+ "nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\"" + "right\",\""
							+ "false\",\"" + "45\",\"" + "40\"\n");
				} else {
					pw.print("\"NONE\",\"" + cda.get("cellid") + "\",\"" + cda.get("cellid") + "\",\"" + cda.get("pci")
							+ "\",\"guard-band\",\"" + "0\",\"" + "off\",\"" + "off\",\"" + "nprach-start-time-ms8\",\""
							+ "nprach-subcarrier-offset-n36\",\"");
					pw.print("nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\""
							+ "nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\"" + "right\",\""
							+ "false\",\"" + "45\",\"" + "40\"\n");
				}
			}
			pw.print("\"@NON_ANCHOR_NB_IOT_CELL_INFORMATION\"\n");
			pw.print("\"State\",\"Cell Num\",\"Operation Mode Info\",\"Guard Band\",\"DL RB\",\"UL RB\"\n");
			for (int i = 0; i < rrhDataAref.size(); i++) {
				pw.print("\"NONE\",\"\",\"\",\"\",\"\",\"\"\n");
			}

			pw.print("\"@CHANNEL_BOARD_INFORMATION\"\n");
			pw.print("\"State\",\"Unit Type\",\"Unit ID\",\"Board Type\"\n");
			for (int i = 0; i <= lcccnt; i++) {
				pw.print("\"ADD\",\"ecp\",\"" + i + "\",\"lcc4-b1\"\n");
			}

			pw.print("\"@RU_INFORMATION\"\n");
			pw.print(
					"\"State\",\"RU Conf\",\"RU Port\",\"Connected DU Board Type\",\"RU Type\",\"Start Earfcn1\",\"Start Earfcn2\",\"Serial Number\",\"Azimuth\",\"Beamwidth\",");
			pw.print(
					"\"Fcc ID\",\"Call Sign\",\"CBSD Category\",\"X Pole Antenna\",\"Antenna Gain dBi\",\"Cable Loss\",\"Accuracy Margin dB\"\n");
			ArrayList<String> al = new ArrayList<>();
			for (int i = 0; i < rrhDataAref.size(); i++) {
				rda = rrhDataAref.get(i);

				if (rda.get("code").toString().equals("rt4401-480")) {

					((ArrayList<String>) rda.get("startearfcnA")).set(0, "55240");
					((ArrayList<String>) rda.get("startearfcnA")).set(1, "55240");
				}
				if (rda.get("code").toString().equals("rt2201-460")) {

					((ArrayList<String>) rda.get("startearfcnA")).set(0, "46790");
					((ArrayList<String>) rda.get("startearfcnA")).set(1, "46790");
				}
				pw.print("\"ADD" + "\",\"" + rda.get("cpristr") + "\",\"" + "0" + "\",\"" + "ecp" + "\",\""
						+ rda.get("code") + "\",\"" + ((ArrayList<String>) rda.get("startearfcnA")).get(0) + "\",\""
						+ ((ArrayList<String>) rda.get("startearfcnA")).get(1) + "\",\"" + "\",\"" + "-1" + "\",\""
						+ "700\",\"");
				pw.print(((ArrayList<String>) rda.get("fccId")).get(0) + "\",\"" + "\",\"" + "cbsd-b" + "\",\"" + "true"
						+ "\",\"" + ((ArrayList<String>) rda.get("antennaGains")).get(0) + "\",\"" + "0" + "\",\""
						+ "10\"\n");

			}

			int cp1 = 0;
			int cp2 = 0;
			int cp3 = 0;
			int cp4 = 0;
			int cp5 = 0;
			int cp6 = 0;
			int cp7 = 0;
			int cp8 = 0;
			int cp9 = 0;
			pw.print("\"@ADDITIONAL_CPRI_INFORMATION\"\n");
			pw.print(
					"\"State\",\"RU Conf\",\"Connected DU Board Type\",\"Additional Board ID\",\"Additional Port ID\",\"RU Additional Port ID\"\n");
			for (int i = 0; i < cellDataAref.size(); i++) {
				cda = cellDataAref.get(i);
				if ((cda.get("RUPortID").toString()).equals("1")) {
					if (cda.get("cpristr").toString().equals("1_3_0")) {
						cp1++;
						if (cp1 < 2) {
							pw.print("\"ADD" + "\",\"" + cda.get("cpristr") + "\",\"" + "ecp" + "\",\""
									+ cda.get("LCCnum") + "\",\"" + cda.get("additional_port") + "\",\""
									+ cda.get("RUPortID") + "\"\n");
						}
					} else if (cda.get("cpristr").toString().equals("1_4_0")) {
						cp2++;
						if (cp2 < 2) {
							pw.print("\"ADD" + "\",\"" + cda.get("cpristr") + "\",\"" + "ecp" + "\",\""
									+ cda.get("LCCnum") + "\",\"" + cda.get("additional_port") + "\",\""
									+ cda.get("RUPortID") + "\"\n");
						}
					} else if (cda.get("cpristr").toString().equals("1_5_0")) {
						cp3++;
						if (cp3 < 2) {
							pw.print("\"ADD" + "\",\"" + cda.get("cpristr") + "\",\"" + "ecp" + "\",\""
									+ cda.get("LCCnum") + "\",\"" + cda.get("additional_port") + "\",\""
									+ cda.get("RUPortID") + "\"\n");
						}
					} else if (cda.get("cpristr").toString().equals("1_6_0")) {
						cp4++;
						if (cp4 < 2) {
							pw.print("\"ADD" + "\",\"" + cda.get("cpristr") + "\",\"" + "ecp" + "\",\""
									+ cda.get("LCCnum") + "\",\"" + cda.get("additional_port") + "\",\""
									+ cda.get("RUPortID") + "\"\n");
						}
					} else if (cda.get("cpristr").toString().equals("1_8_0")) {
						cp5++;
						if (cp5 < 2) {
							pw.print("\"ADD" + "\",\"" + cda.get("cpristr") + "\",\"" + "ecp" + "\",\""
									+ cda.get("LCCnum") + "\",\"" + cda.get("additional_port") + "\",\""
									+ cda.get("RUPortID") + "\"\n");
						}
					} else if (cda.get("cpristr").toString().equals("1_10_0")) {
						cp6++;
						if (cp6 < 2) {
							pw.print("\"ADD" + "\",\"" + cda.get("cpristr") + "\",\"" + "ecp" + "\",\""
									+ cda.get("LCCnum") + "\",\"" + cda.get("additional_port") + "\",\""
									+ cda.get("RUPortID") + "\"\n");
						}
					} else if (cda.get("cpristr").toString().equals("2_6_0")) {
						cp7++;
						if (cp7 < 2) {
							pw.print("\"ADD" + "\",\"" + cda.get("cpristr") + "\",\"" + "ecp" + "\",\""
									+ cda.get("LCCnum") + "\",\"" + cda.get("additional_port") + "\",\""
									+ cda.get("RUPortID") + "\"\n");
						}
					} else if (cda.get("cpristr").toString().equals("2_8_0")) {
						cp8++;
						if (cp8 < 2) {
							pw.print("\"ADD" + "\",\"" + cda.get("cpristr") + "\",\"" + "ecp" + "\",\""
									+ cda.get("LCCnum") + "\",\"" + cda.get("additional_port") + "\",\""
									+ cda.get("RUPortID") + "\"\n");
						}
					} else if (cda.get("cpristr").toString().equals("2_10_0")) {
						cp9++;
						if (cp9 < 2) {
							pw.print("\"ADD" + "\",\"" + cda.get("cpristr") + "\",\"" + "ecp" + "\",\""
									+ cda.get("LCCnum") + "\",\"" + cda.get("additional_port") + "\",\""
									+ cda.get("RUPortID") + "\"\n");
						}
					}
				}
			}

			int a1 = 0;
			int a2 = 0;
			int a3 = 0;
			int a4 = 0;
			int a5 = 0;
			int a6 = 0;

			pw.print("\"@RU_ANTENNA_PORT_INFORMATION\"\n");
			pw.print(
					"\"State\",\"RU Conf\",\"Connected DU Board Type\",\"Antenna Port ID\",\"Antenna Cable Length\"\n");
			for (int i = 0; i < rrhDataAref.size(); i++) {
				for (int j = 0; j < ((int[]) rrhDataAref.get(i).get("antennaPortMapA")).length; j++) {
					if (((int[]) rrhDataAref.get(i).get("antennaPortMapA"))[j] != -1) {
						int antport = j + 1;
						if ((rrhDataAref.get(i).get("cpristr").toString()).equals("2_6_0")) {
							a1++;
							if (a1 < 5) {
								pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "ecp"
										+ "\",\"" + antport + "\",\""
										+ ((int[]) rrhDataAref.get(i).get("antennaPortMapA"))[j]);
								if (j < ((int[]) (rrhDataAref.get(i).get("antennaPortMapA"))).length) {
									pw.print("\"");
								}
								pw.println();
							}
						} else if ((rrhDataAref.get(i).get("cpristr").toString()).equals("1_6_0")) {
							a4++;
							if (a4 < 5) {
								pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "ecp"
										+ "\",\"" + antport + "\",\""
										+ ((int[]) rrhDataAref.get(i).get("antennaPortMapA"))[j]);
								if (j < ((int[]) (rrhDataAref.get(i).get("antennaPortMapA"))).length) {
									pw.print("\"");
								}
								pw.println();
							}
						} else if ((rrhDataAref.get(i).get("cpristr").toString()).contains("2_8_0")) {
							a2++;
							if (a2 < 5) {
								pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "ecp"
										+ "\",\"" + antport + "\",\""
										+ ((int[]) rrhDataAref.get(i).get("antennaPortMapA"))[j]);
								if (j < ((int[]) (rrhDataAref.get(i).get("antennaPortMapA"))).length) {
									pw.print("\"");
								}
								pw.println();
							}
						} else if ((rrhDataAref.get(i).get("cpristr").toString()).contains("1_8_0")) {
							a5++;
							if (a5 < 5) {
								pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "ecp"
										+ "\",\"" + antport + "\",\""
										+ ((int[]) rrhDataAref.get(i).get("antennaPortMapA"))[j]);
								if (j < ((int[]) (rrhDataAref.get(i).get("antennaPortMapA"))).length) {
									pw.print("\"");
								}
								pw.println();
							}
						} else if ((rrhDataAref.get(i).get("cpristr").toString()).contains("2_10_0")) {
							a6++;
							if (a6 < 5) {
								pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "ecp"
										+ "\",\"" + antport + "\",\""
										+ ((int[]) rrhDataAref.get(i).get("antennaPortMapA"))[j]);
								if (j < ((int[]) (rrhDataAref.get(i).get("antennaPortMapA"))).length) {
									pw.print("\"");
								}
								pw.println();
							}
						} else if ((rrhDataAref.get(i).get("cpristr").toString()).contains("1_10_0")) {
							a3++;
							if (a3 < 5) {
								pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "ecp"
										+ "\",\"" + antport + "\",\""
										+ ((int[]) rrhDataAref.get(i).get("antennaPortMapA"))[j]);
								if (j < ((int[]) (rrhDataAref.get(i).get("antennaPortMapA"))).length) {
									pw.print("\"");
								}
								pw.println();
							}
						} else {
							pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "ecp" + "\",\""
									+ antport + "\",\"" + ((int[]) rrhDataAref.get(i).get("antennaPortMapA"))[j]);
							if (j < ((int[]) (rrhDataAref.get(i).get("antennaPortMapA"))).length) {
								pw.print("\"");
							}
							pw.println();
						}
					}
				}
			}

			int counta = 0;
			int countb = 0;
			int countc = 0;
			int countd = 0;
			int counte = 0;
			int countf = 0;
			pw.print("\"@RU_GROUP_INFORMATION\"\n");
			pw.print("\"State\",\"RU Conf\",\"Group ID\"\n");
			for (int i = 0; i < rrhDataAref.size(); i++) {
				if ((rrhDataAref.get(i).get("cpristr").toString()).equals("2_6_0")) {
					counta++;
					if (counta < 2) {
						pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "0\"\n");
					}
				} else if ((rrhDataAref.get(i).get("cpristr").toString()).equals("1_6_0")) {
					countd++;
					if (countd < 2) {
						pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "0\"\n");
					}
				} else if ((rrhDataAref.get(i).get("cpristr").toString()).contains("2_8_0")) {
					countb++;
					if (countb < 2) {
						pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "0\"\n");
					}
				} else if ((rrhDataAref.get(i).get("cpristr").toString()).contains("1_8_0")) {
					counte++;
					if (counte < 2) {
						pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "0\"\n");
					}
				} else if ((rrhDataAref.get(i).get("cpristr").toString()).contains("2_10_0")) {
					countf++;
					if (countf < 2) {
						pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "0\"\n");
					}
				} else if ((rrhDataAref.get(i).get("cpristr").toString()).contains("1_10_0")) {
					countc++;
					if (countc < 2) {
						pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "0\"\n");
					}
				} else {
					pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "0\"\n");
				}
			}

			pw.print("\"@DSP_INFORMATION\"\n");
			pw.print("\"State\",\"Unit Type\",\"Unit ID\",\"DSP ID\",\"Optic Distance\"\n");

			ArrayList<Integer> cardsProvisioned = new ArrayList<Integer>();
			for (int ci = 0; ci < cellDataAref.size(); ci++) {
				if (Integer.parseInt(cellDataAref.get(ci).get("LCCnum").toString()) == cardsProvisioned.size()) {
					cardsProvisioned.add(cardsProvisioned.size());
					continue;
				}
			}
			for (int li = 0; li < cardsProvisioned.size(); li++) {
				for (int di = 0; di < lccAref.get(li).size(); di++) {
					if ((Integer.parseInt(lccAref.get(li).get(di).get("opticDistance").toString())) == 0) {
						pw.print("\"ADD\",\"ecp\",\"" + li + "\",\"" + di + "\",\"10-km\"\n");
					} else {
						pw.print("\"ADD\",\"ecp\",\"" + li + "\",\"" + di + "\",\""
								+ lccAref.get(li).get(di).get("opticDistance") + "-km\"\n");
					}
				}
			}
		} catch (Exception e) {
			logger.error("Exception in USM Cell Grower " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		} finally {
			if (pw != null) {
				pw.flush();
				pw.close();
			}
		}

	}

	@SuppressWarnings("unchecked")
	private static void createGrowTemplate_950(ArrayList<TreeMap<String, Object>> cellDataAref,
			ArrayList<TreeMap<String, Object>> rrhDataAref, ArrayList<ArrayList<TreeMap<String, Object>>> lccAref,
			String growfile) {
		PrintWriter pw = null;
		try {

			File f = new File(growfile);
			f.createNewFile();
			pw = new PrintWriter(new BufferedWriter(new FileWriter(f)));

			int lcccnt = 0;
			pw.print("\"@CELL_INFORMATION\"\n");
			pw.print(
					"\"State\",\"Sector ID\",\"Carrier ID\",\"Cell Index in DSP\",\"DSP ID\",\"CC ID\",\"RU Port ID\",\"RU Conf\",");
			pw.print(
					"\"Multi Carrier Type\",\"Virtual RF Port Mapping\",\"Dl Max Tx Power\",\"Pucch Center Mode\",\"PCI\",\"DL Antenna Count\",\"UL Antenna Count\",");
			pw.print(
					"\"Earfcn DL\",\"Earfcn UL\",\"Cell Band Carrier\",\"Bandwidth\",\"CRS\",\"eMTC\",\"Frequency Profile\",\"TAC\",\"EAID\",\"HSF\",\"ZCZC\",\"RSI\",");
			pw.print(
					"\"Rcc ID\",\"TH MaxEirp\",\"TH RSSI\",\"Preferred Earfcn\",\"Subframe Assignment\",\"Special Subframe Patterns\",\"Auto GPS\",\"Latitude\",\"Longitude\",\"Height\"\n");
			TreeMap<String, Object> cda = new TreeMap<String, Object>();
			TreeMap<String, Object> rda = new TreeMap<String, Object>();

			for (int i = 0; i < cellDataAref.size(); i++) {
				cda = cellDataAref.get(i);
				if ((cda.get("band").toString()).equals("700MHz")) {
					cda.replace("band", "700mhz");
				} else if ((cda.get("band").toString()).equals("CBRS")) {
					cda.replace("band", "3500mhz");
				} else if ((cda.get("band").toString()).equals("AWS-1")) {
					cda.replace("band", "2100mhz_band4");
				} else if ((cda.get("band").toString()).equals("PCS")) {
					cda.replace("band", "1900mhz");

				} else if ((cda.get("band").toString()).equals("850LTE")) {
					cda.replace("band", "850mhz");
				} else if ((cda.get("band").toString()).equals("850MHz")) {
					cda.replace("band", "850mhz");

				} else if ((cda.get("band").toString()).equals("AWS-2")) {
					cda.replace("band", "2100mhz_band4");
				} else if ((cda.get("band").toString()).equals("AWS-3")) {
					cda.replace("band", "2100mhz_band66");
				} else if ((cda.get("band").toString()).equals("PCS-2")) {
					cda.replace("band", "1900mhz");
				}
				if ((cda.get("bandwidth").toString()).equals("10")) {
					cda.replace("bandwidth", "system-bandwidth-n50");
				} else if ((cda.get("bandwidth").toString()).equals("20")) {
					cda.replace("bandwidth", "system-bandwidth-n100");
				} else if ((cda.get("bandwidth").toString()).equals("15")) {
					cda.replace("bandwidth", "system-bandwidth-n75");
				} else if ((cda.get("bandwidth").toString()).equals("5")) {
					cda.replace("bandwidth", "system-bandwidth-n25");
				}
				if ((cda.get("market").toString()).equals("NEWENGLAND")) {
					pw.print("\"ADD\"" + ",\"" + cda.get("sectid") + "\",\"" + cda.get("carrid") + "\",\""
							+ cda.get("carrierInDSPid") + "\",\"" + cda.get("dspid") + "\",\"" + cda.get("LCCnum")
							+ "\",\"" + "0\",\"");
					pw.print(cda.get("cpristr") + "\",\"" + cda.get("typename") + "\",\"" + "off" + "\",\""
							+ cda.get("power") + "\",\"" + "edge-mode" + "\",\"" + cda.get("pci") + "\",\""
							+ "n4-tx-antenna-count\",\"n4-rx-antenna-count" + "\",\"" + cda.get("earfcndl") + "\",\"");
					pw.print(cda.get("earfcnul") + "\",\"" + cda.get("band") + "\",\"" + cda.get("bandwidth") + "\",\""
							+ cda.get("crs") + "\",\"" + cda.get("emtc") + "\",\"" + "-" + "\",\"" + cda.get("tac")
							+ "\",\"" + "0" + "\",\"");
					pw.print("false" + "\",\"" + "12" + "\",\"" + cda.get("rach") + "\",\"\",\"" + "37" + "\",\""
							+ "-25" + "\",\"" + cda.get("PreferredEarfcn") + "\",\"" + "subframe-assignment-sa2"
							+ "\",\"" + "special-subframe-pattern-ssp7\",\"" + "true\",\"" + "N 000:00:00.001\",\""
							+ "E 000:00:00.000,0.00m\"\n");
				} else if (cda.get("DspID").equals("")) {
					pw.print("\"ADD\"" + ",\"" + cda.get("sectid") + "\",\"" + cda.get("carrid") + "\",\""
							+ cda.get("carrierInDSPid") + "\",\"" + cda.get("dspid") + "\",\"" + cda.get("LCCnum")
							+ "\",\"" + cda.get("RUPortID") + "\",\"");
					pw.print(cda.get("cpristr") + "\",\"" + cda.get("typename") + "\",\"" + "off" + "\",\""
							+ cda.get("power") + "\",\"" + "edge-mode" + "\",\"" + cda.get("pci") + "\",\""
							+ "n4-tx-antenna-count\",\"n4-rx-antenna-count" + "\",\"" + cda.get("earfcndl") + "\",\"");
					pw.print(cda.get("earfcnul") + "\",\"" + cda.get("band") + "\",\"" + cda.get("bandwidth") + "\",\""
							+ cda.get("crs") + "\",\"" + cda.get("emtc") + "\",\"" + "-" + "\",\"" + cda.get("tac")
							+ "\",\"" + "0" + "\",\"");
					pw.print("false" + "\",\"" + "12" + "\",\"" + cda.get("rach") + "\",\"\",\"" + "37" + "\",\""
							+ "-25" + "\",\"" + cda.get("PreferredEarfcn") + "\",\"" + "subframe-assignment-sa2"
							+ "\",\"" + "special-subframe-pattern-ssp7\",\"" + "true\",\"" + "N 000:00:00.001\",\""
							+ "E 000:00:00.000,0.00m\"\n");
				}

				if (Integer.parseInt(cda.get("LCCnum").toString()) > lcccnt) {
					lcccnt = Integer.parseInt(cda.get("LCCnum").toString());
				}
			}

			pw.print("@NB_IOT_CELL_INFORMATION\n");
			pw.print(
					"State,Cell Num,Parent Cell Number,NB IoT PCI,Operation Mode Info,NB IoT TAC,Use Parent PCI for Guard-band,Initial Nprach,");
			pw.print(
					"Nprach Start Time CL0,Nprach Subcarrier Offset CL0,Nprach Start Time CL1,Nprach Subcarrier Offset CL1,Nprach Start Time CL2,");
			pw.print("Nprach Subcarrier Offset CL2,Guard Band,Avoid UL Interfering,DL RB,UL RB\n");
			for (int i = 0; i < cellDataAref.size(); i++) {
				cda = cellDataAref.get(i);
				if (cda.get("band").toString().contains("700mhz")) {
					pw.print("ADD," + cda.get("cellid") + "," + cda.get("cellid") + "," + cda.get("pci") + ","
							+ "guard-band," + cda.get("iottac") + ",on," + "on," + "nprach-start-time-ms8,"
							+ "nprach-subcarrier-offset-n36,");
					pw.print("nprach-start-time-ms8," + "nprach-subcarrier-offset-n36," + "nprach-start-time-ms8,"
							+ "nprach-subcarrier-offset-n36," + "right," + "false," + "45," + "40\n");
				} else {
					pw.print("NONE," + cda.get("cellid") + "," + cda.get("cellid") + "," + cda.get("pci")
							+ ",guard-band," + "0," + "off," + "off," + "nprach-start-time-ms8,"
							+ "nprach-subcarrier-offset-n36,");
					pw.print("nprach-start-time-ms8," + "nprach-subcarrier-offset-n36," + "nprach-start-time-ms8,"
							+ "nprach-subcarrier-offset-n36," + "right," + "false," + "45," + "40\n");
				}
			}

			pw.print("@CHANNEL_BOARD_INFORMATION\n");
			pw.print("State,Unit Type,Unit ID,Board Type\n");
			for (int i = 0; i <= lcccnt; i++) {
				pw.print("ADD,ecp," + i + ",lcc4-b1\n");
			}

			pw.print("@CPRI_PORT_INFORMATION\n");
			pw.print("State,UnitType,UnitID,PortID\n");
			for (int i = 0; i < rrhDataAref.size(); i++) {
				pw.println("NONE,ecp," + rrhDataAref.get(i).get("LCCnum") + "," + rrhDataAref.get(i).get("port"));
			}

			pw.print("@RU_INFORMATION\n");
			pw.print(
					"State,RU Conf,RU Port,Connected DU Board Type,RU Type,Start Earfcn1,Start Earfcn2,Serial Number,Azimuth,Beamwidth,");
			pw.print("Fcc ID,Call Sign,CBSD Category,X Pole Antenna,Antenna Gain dBi,Cable Loss,Accuracy Margin dB\n");
			for (int i = 0; i < rrhDataAref.size(); i++) {
				rda = rrhDataAref.get(i);
				cda = cellDataAref.get(i);
				pw.print("ADD," + rda.get("cpristr") + ",0," + "ecp," + rda.get("code") + ","
						+ ((ArrayList<String>) rda.get("startearfcnA")).get(0) + ","
						+ ((ArrayList<String>) rda.get("startearfcnA")).get(1) + ",," + "-1," + "700,");
				pw.print(cda.get("cbrcfcc") + ",," + "cbsd-b," + "true," + cda.get("AntennaGain") + ",0," + "10\n");
			}

			pw.print("@ADDITIONAL_CPRI_INFORMATION\n");
			pw.print(
					"State,RU Conf,Connected DU Board Type,Additional Board ID,Additional Port ID,RU Additional Port ID\n");
			for (int i = 0; i < cellDataAref.size(); i++) {
				cda = cellDataAref.get(i);
				if ((cda.get("RUPortID").toString()).equals("1") && (cda.get("carrid").toString()).equals("4")) {
					pw.println("ADD" + "," + cda.get("cpristr") + "," + "ecp" + "," + cda.get("LCCnum") + ","
							+ cda.get("port") + "," + cda.get("RUPortID"));
				} else if ((cda.get("RUPortID").toString()).equals("1")
						&& (cda.get("carrid").toString()).equals("-4")) {
					pw.println("ADD" + "," + cda.get("cpristr") + "," + "ecp" + "," + cda.get("LCCnum") + ","
							+ cda.get("port") + "," + cda.get("RUPortID"));
				} else if ((cda.get("RUPortID").toString()).equals("1") && (cda.get("carrid").toString()).equals("4")) {
					pw.println("ADD" + "," + cda.get("cpristr") + "," + "ecp" + "," + cda.get("LCCnum") + ","
							+ cda.get("port") + "," + cda.get("RUPortID"));
				}
			}

			pw.print("@RU_ANTENNA_PORT_INFORMATION\n");
			pw.print("State,RU Conf,Connected DU Board Type,Antenna Port ID,Antenna Cable Length\n");
			for (int i = 0; i < rrhDataAref.size(); i++) {
				for (int j = 0; j < ((int[]) rrhDataAref.get(i).get("antennaPortMapA")).length; j++) {
					if (((int[]) rrhDataAref.get(i).get("antennaPortMapA"))[j] != -1) {
						int antport = j + 1;
						pw.print("ADD" + "," + rrhDataAref.get(i).get("cpristr") + "," + "ecp" + "," + antport + ","
								+ ((int[]) rrhDataAref.get(i).get("antennaPortMapA"))[j]);
						if (j < ((int[]) (rrhDataAref.get(i).get("antennaPortMapA"))).length) {
							pw.print("");
						}
						pw.println();

					}
				}
			}

			pw.print("@RU_GROUP_INFORMATION\n");
			pw.print("State,RU Conf,Group ID\n");
			for (int i = 0; i < rrhDataAref.size(); i++) {
				pw.print("ADD," + rrhDataAref.get(i).get("cpristr") + ",0\n");
			}

			pw.print("@DSP_INFORMATION\n");
			pw.print("State,Unit Type,Unit ID,DSP ID,Optic Distance\n");

			ArrayList<Integer> cardsProvisioned = new ArrayList<Integer>();
			for (int ci = 0; ci < cellDataAref.size(); ci++) {
				if (Integer.parseInt(cellDataAref.get(ci).get("LCCnum").toString()) == cardsProvisioned.size()) {
					cardsProvisioned.add(cardsProvisioned.size());
					continue;
				}
			}
			for (int li = 0; li < cardsProvisioned.size(); li++) {
				for (int di = 0; di < lccAref.get(li).size(); di++) {
					if ((Integer.parseInt(lccAref.get(li).get(di).get("opticDistance").toString())) == 0) {
						pw.print("ADD,ecp," + li + "," + di + ",10-km\n");
					} else {
						pw.print("ADD,ecp," + li + "," + di + "," + lccAref.get(li).get(di).get("opticDistance")
								+ "-km\n");
					}
				}
			}

			pw.print("@NON_ANCHOR_NB_IOT_CELL_INFORMATION\n");
			pw.print("State,Cell Num,Operation Mode Info,Guard Band,DL RB,UL RB\n");
			for (int i = 0; i < rrhDataAref.size(); i++) {
				pw.print("NONE,1,guard-band,right,45,40\n");
			}

		} catch (Exception e) {
			logger.error("Exception in USM Cell Grower " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		} finally {
			if (pw != null) {
				pw.flush();
				pw.close();
			}
		}

	}

	private static Boolean createGrowTemplate_21B0(ArrayList<TreeMap<String, Object>> cellDataAref,
			ArrayList<TreeMap<String, Object>> rrhDataAref, ArrayList<ArrayList<TreeMap<String, Object>>> lccAref,
			String growfile, ArrayList<TreeMap<String, Object>> rrhDataBref,
			ArrayList<TreeMap<String, Object>> rrhDataCref) {
		PrintWriter pw = null;
		try {
			String val = "";
			File f = new File(growfile);
			f.createNewFile();
			pw = new PrintWriter(new BufferedWriter(new FileWriter(f)));

			add_optic_distance_to_dsps(lccAref, cellDataAref);

			pw.print("\"@CELL_INFORMATION\"\n");
			pw.print(
					"\"State\",\"Sector ID\",\"Carrier ID\",\"CC ID\",\"DSP ID\",\"Cell Index in DSP\",\"RU Conf\",\"RU Port ID\",");
			pw.print(
					"\"Cell Band Carrier\",\"Earfcn DL\",\"Earfcn UL\",\"Bandwidth\",\"DL Antenna Count\",\"UL Antenna Count\",\"Path\",");
			pw.print(
					"\"Multi Carrier Type\",\"CRS\",\"PCI\",\"TAC\",\"Cell Size\",\"EAID\",\"HSF\",\"ZCZC\",\"RSI\",\"PRACH Configuration Index\",\"Virtual RF Port Mapping\",\"eMTC\",\"Subframe Assignment\",\"Special Subframe Patterns\",");
			pw.print(
					"\"Frequency Profile\",\"Dl Max Tx Power\",\"Pucch Center Mode\",\"Rcc ID\",\"Max EIRP Selection Mode\",\"TH Max EIRP\",\"TH RSSI\",\"Preferred Earfcn\",\"CBRS Carrier Update Enable\",\"Dynamic Spectrum Sharing Mode\",\"DSS Target NR Cell Num\",\"Slot Level Operation Mode\",\"CDMA Blanking Case\",\"CDMA Blanking Upper\",\"CDMA Blanking Lower\",\"DSS PUCCH HARQ ACK for CA FDD\",\"MV IO Site Migration Indicator\",\"Term Point To DSS Index\",\"Auto GPS\",\"Latitude\",\"Longitude\",\"Height\"\n");

			TreeMap<String, Object> cda = new TreeMap<String, Object>();
			TreeMap<String, Object> rda = new TreeMap<String, Object>();
			TreeMap<String, Object> fda = new TreeMap<String, Object>();
			// Adding new data structures for carrier add , administrative state = NEW/new
			TreeMap<String, Object> ada = new TreeMap<String, Object>();
			TreeMap<String, Object> addData = new TreeMap<String, Object>();
			TreeMap<String, Object> caddData = new TreeMap<String, Object>();
			TreeMap<String, Object> orgData = new TreeMap<String, Object>();
			ArrayList<TreeMap<String, Object>> addAref = new ArrayList<TreeMap<String, Object>>();
			ArrayList<TreeMap<String, Object>> cDataAref = new ArrayList<TreeMap<String, Object>>();
			ArrayList<TreeMap<String, Object>> cAddDataAref = new ArrayList<TreeMap<String, Object>>();
			for (int i = 0; i < cellDataAref.size(); i++) {

				addData = cellDataAref.get(i);
				addAref.add(addData);
				// storing data based on administrative state
				if (addData.get("adstate").toString().contains("NEW")) {

					caddData = cellDataAref.get(i);
					cAddDataAref.add(caddData);

				} else {
					orgData = cellDataAref.get(i);
					cDataAref.add(orgData);

				}
			}

			if (supportCA.equals("true") && cAddDataAref.isEmpty()) {

				AdNew = false;

				for (int i = 0; i < cDataAref.size(); i++) {
					cda = cDataAref.get(i);
					String path = "";

					if (cda.get("band").toString().contains("700") && cda.get("txd").equals("4")) {
						path = "select-abcd";
					} else if (cda.get("band").toString().contains("700") && cda.get("txd").equals("2")) {
						path = "select-ab";
					} else if (cda.get("band").toString().contains("850") && cda.get("txd").equals("4")) {
						path = "select-efgh";
					} else if (cda.get("band").toString().contains("850") && cda.get("txd").equals("2")) {
						if (cda.get("rrh").toString().contains("RIU")) {// new riu
							path = "select-cd";
						} else {
							path = "select-ef";
						}
					} else if (cda.get("band").toString().contains("AWS") && cda.get("txd").equals("4")) {
						path = "select-abcd";
					} else if (cda.get("band").toString().contains("AWS") && cda.get("txd").equals("2")) {
						path = "select-ab";
					} else if (cda.get("band").toString().contains("PCS") && cda.get("txd").equals("4")) {
						path = "select-efgh";
					} else if (cda.get("band").toString().contains("PCS") && cda.get("txd").equals("2")) {
						if (cda.get("rrh").toString().contains("RIU")) {// new riu
							path = "select-cd";
						} else {
							path = "select-ef";
						}
					} else if (cda.get("band").equals("CBRS") && cda.get("txd").equals("4")) {
						path = "select-abcd";
					} else if (cda.get("band").equals("LAA") && cda.get("txd").equals("4")) {
						path = "select-abcd";
					} else if (cda.get("band").equals("LAA") && cda.get("txd").equals("2")) {
						path = "select-ab";
					} // riu port
					else if (cda.get("band").toString().contains("AWS") && cda.get("txd").equals("1")) {
						path = "select-a";
					} else if (cda.get("band").toString().contains("700") && cda.get("txd").equals("1")) {
						path = "select-a";
					} else if (cda.get("band").toString().contains("PCS") && cda.get("txd").equals("1")) {
						path = "select-c";
					} else if (cda.get("band").toString().contains("850") && cda.get("txd").equals("1")) {
						path = "select-c";
					}

					System.out.println("Path value::::" + path);

					if (Integer.parseInt(cda.get("earfcndl").toString()) >= 5180
							&& Integer.parseInt(cda.get("earfcndl").toString()) <= 5279) {
						cda.replace("band", "700mhz");
					} else if (cda.get("band").equals("CBRS")
							|| (Integer.parseInt(cda.get("earfcndl").toString()) >= 55240
									&& Integer.parseInt(cda.get("earfcndl").toString()) <= 56739)) {
						cda.replace("band", "3500mhz");
						cda.replace("earfcndl", "0");
						cda.replace("earfcnul", "0");

					} else if ((Integer.parseInt(cda.get("earfcndl").toString()) >= 1950
							&& Integer.parseInt(cda.get("earfcndl").toString()) <= 2399)) {
						cda.replace("band", "2100mhz_band4");
					} else if (Integer.parseInt(cda.get("earfcndl").toString()) >= 600
							&& Integer.parseInt(cda.get("earfcndl").toString()) <= 1199) {
						cda.replace("band", "1900mhz");

					} else if (Integer.parseInt(cda.get("earfcndl").toString()) >= 2400
							&& Integer.parseInt(cda.get("earfcndl").toString()) <= 2649) {
						cda.replace("band", "850mhz");
					} else if ((Integer.parseInt(cda.get("earfcndl").toString()) >= 66436
							&& Integer.parseInt(cda.get("earfcndl").toString()) <= 67335)) {
						if (cda.get("DspCellIndex").equals("4") || cda.get("DspCellIndex").equals("5")) {
							cda.replace("typename", "cfg61-multi-carrier-10m-5m-6cell");
						}

						cda.replace("band", "2100mhz_band66");
					} else if (cda.get("band").equals("LAA")
							|| (Integer.parseInt(cda.get("earfcndl").toString()) >= 46790
									&& Integer.parseInt(cda.get("earfcndl").toString()) <= 54539)) {
						cda.replace("band", "5000mhz");
						cda.replace("earfcndl", "0");
						cda.replace("earfcnul", "0");
					} else if ((Integer.parseInt(cda.get("earfcndl").toString()) >= 8040
							&& Integer.parseInt(cda.get("earfcndl").toString()) <= 8689)) {
						cda.replace("band", "1900mhz_band25");
					}
					if (cda.get("bandwidth").equals("10")) {
						cda.replace("bandwidth", "system-bandwidth-n50");
					} else if (cda.get("bandwidth").equals("20")) {
						cda.replace("bandwidth", "system-bandwidth-n100");

					} else if (cda.get("bandwidth").equals("15")) {
						cda.replace("bandwidth", "system-bandwidth-n75");
					} else if (cda.get("bandwidth").equals("5")) {
						cda.replace("bandwidth", "system-bandwidth-n25");
						;
					}

					pw.print("\"ADD" + "\",\"" + cda.get("sectid") + "\",\"" + cda.get("carrid") + "\",\""
							+ cda.get("LCCnum") + "\",\"" + cda.get("DspID") + "\",\"" + cda.get("DspCellIndex")
							+ "\",\"" + cda.get("cpristr") + "\",\"");
					pw.print(cda.get("RUPortID") + "\",\"");

					pw.print(cda.get("band"));

					if (cda.get("txd").equals("2") && cda.get("rxd").equals("2")) {
						pw.print("\",\"" + cda.get("earfcndl") + "\",\"" + cda.get("earfcnul") + "\",\""
								+ cda.get("bandwidth") + "\",\"" + "n2-tx-antenna-count\",\"n2-rx-antenna-count"
								+ "\",\"" + path + "\",\"" + cda.get("mct").toString().toLowerCase() + "\",\"");
					} else if (cda.get("txd").equals("2") && cda.get("rxd").equals("4")) {
						pw.print("\",\"" + cda.get("earfcndl") + "\",\"" + cda.get("earfcnul") + "\",\""
								+ cda.get("bandwidth") + "\",\"" + "n2-tx-antenna-count\",\"n4-rx-antenna-count"
								+ "\",\"" + path + "\",\"" + cda.get("mct").toString().toLowerCase() + "\",\"");
					} else if (cda.get("txd").equals("'0,bs4") && cda.get("rxd").equals("2")) {
						pw.print("\",\"" + cda.get("earfcndl") + "\",\"" + cda.get("earfcnul") + "\",\""
								+ cda.get("bandwidth") + "\",\"" + "n4-tx-antenna-count\",\"n2-rx-antenna-count"
								+ "\",\"" + path + "\",\"" + cda.get("mct").toString().toLowerCase() + "\",\"");
					} else if (cda.get("txd").equals("4") && cda.get("rxd").equals("4")) {
						pw.print("\",\"" + cda.get("earfcndl") + "\",\"" + cda.get("earfcnul") + "\",\""
								+ cda.get("bandwidth") + "\",\"" + "n4-tx-antenna-count\",\"n4-rx-antenna-count"
								+ "\",\"" + path + "\",\"" + cda.get("mct").toString().toLowerCase() + "\",\"");
					} else {
						String tx = "n" + cda.get("txd") + "-tx-antenna-count";
						String rx = "n" + cda.get("rxd") + "-rx-antenna-count";
						pw.print("\",\"" + cda.get("earfcndl") + "\",\"" + cda.get("earfcnul") + "\",\""
								+ cda.get("bandwidth") + "\",\"" + tx + "\",\"" + rx + "\",\"" + path + "\",\""
								+ cda.get("mct").toString().toLowerCase() + "\",\"");
					}

					pw.print(cda.get("crs") + "\",\"" + cda.get("pci") + "\",\"" + cda.get("tac") + "\",\"\",\"" + "0"
							+ "\",\"" + "false" + "\",\"" + cda.get("zczc") + "\",\"" + cda.get("rach") + "\",\""
							+ cda.get("pracformat") + "\",\"" + "off" + "\",\"");
					pw.print(cda.get("emtc") + "\",\"" + "subframe-assignment-sa2" + "\",\""
							+ "special-subframe-pattern-ssp7\",\"" + "-" + "\",\"" + cda.get("power") + "\",\""
							+ "edge-mode" + "\",\"" + "0" + "\",\"\",\"" + cda.get("thMaxEirp") + "\",\"" + "-25"
							+ "\",\"" + cda.get("PreferredEarfcn") + "\",\"" + "update-mode1" + "\",\""
							+ "spectrum-sharing-off\",\"" + "0\",\"" + "frame-level-dss-mode\",\"" + "non-blanking\",\""
							+ "0\",\"" + "0\",\"\",\"\",\"" + "0\",\"" + "true\",\"" + "N 000:00:00.000\",\""
							+ "E 000:00:00.000\",\"0.00m\"\n");

				}

				int lcccnt = 0;

				if (Integer.parseInt(cda.get("LCCnum").toString()) > lcccnt) {
					lcccnt = Integer.parseInt(cda.get("LCCnum").toString());
				}

				pw.print("\"@NB_IOT_CELL_INFORMATION\"\n");
				pw.print(
						"\"State\",\"Cell Num\",\"Parent Cell Number\",\"NB IoT PCI\",\"Operation Mode Info\",\"NB IoT TAC\",\"Use Parent PCI for Guard-band\",\"Initial Nprach\",");
				pw.print(
						"\"Nprach Start Time CL0\",\"Nprach Subcarrier Offset CL0\",\"Nprach Start Time CL1\",\"Nprach Subcarrier Offset CL1\",\"Nprach Start Time CL2\",");
				pw.print(
						"\"Nprach Subcarrier Offset CL2\",\"Guard Band\",\"Avoid UL Interfering\",\"DL RB\",\"UL RB\"\n");
				for (int i = 0; i < cDataAref.size(); i++) {
					cda = cDataAref.get(i);
					// if ((cda.get("band").toString().contains("700mhz") ||
					// cda.get("band").toString().contains("700"))) // &&
					// cda.get("nbiot").toString().equals("1")
					// if (cda.get("nbiot").toString().equals("1")) {
					int nb = Integer.parseInt(cda.get("nbiot").toString());
					int iotT = Integer.parseInt(cda.get("iottac").toString());
					if (nb > 0) {
						if (iotT != 0 && iotT > 0) {
							pw.print("\"ADD\",\"" + cda.get("cellid") + "\",\"" + cda.get("cellid") + "\",\""
									+ cda.get("pci") + "\",\"" + "guard-band\",\"" + cda.get("iottac") + "\",\"on\",\""
									+ "on\",\"" + "nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\"");
							pw.print("nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\""
									+ "nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\"" + "right\",\""
									+ "false\",\"" + "45\",\"" + "40\"\n");
						} else {
							pw.print("\"NONE\",\"" + cda.get("cellid") + "\",\"" + cda.get("cellid") + "\",\""
									+ cda.get("pci") + "\",\"guard-band\",\"" + "0\",\"" + "off\",\"" + "off\",\""
									+ "nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\"");
							pw.print("nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\""
									+ "nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\"" + "right\",\""
									+ "false\",\"" + "45\",\"" + "40\"\n");
						}

					} else {
						pw.print("\"NONE\",\"" + cda.get("cellid") + "\",\"" + cda.get("cellid") + "\",\""
								+ cda.get("pci") + "\",\"guard-band\",\"" + "0\",\"" + "off\",\"" + "off\",\""
								+ "nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\"");
						pw.print("nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\""
								+ "nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\"" + "right\",\""
								+ "false\",\"" + "45\",\"" + "40\"\n");
					}
				}

				ArrayList<String> addCpri2 = new ArrayList<String>();
				pw.print("\"@NON_ANCHOR_NB_IOT_CELL_INFORMATION\"\n");
				pw.print("\"State\",\"Cell Num\",\"Operation Mode Info\",\"Guard Band\",\"DL RB\",\"UL RB\"\n");

				for (int i = 0; i < rrhDataAref.size(); i++) {
					if (!addCpri2.contains(rrhDataAref.get(i).get("cpristr").toString())) {
						pw.print("\"NONE\",\"\",\"\",\"\",\"\",\"\"\n");
						addCpri2.add((String) rrhDataAref.get(i).get("cpristr"));
					}
				}

				pw.print("\"@CHANNEL_BOARD_INFORMATION\"\n");
				pw.print("\"State\",\"Unit Type\",\"Unit ID\",\"Board Type\"\n");
				for (int i = 0; i <= lcccnt; i++) {
					pw.print("\"ADD\",\"ecp\",\"" + i + "\",\"lcc4-b1\"\n");
				}

				///////////////////// @cpri port information/////////////////

				// pw.print("\"@CPRI_PORT_INFORMATION\"\n");
				// pw.print("\"State\",\"Unit Type\",\"Unit ID\",\"Port ID\",\"Connection
				// Type\",\"FSU Inter Node ID\",\"FSU DU CPRI Port ID\"\n");

				pw.print("\"@CPRI_PORT_INFORMATION\"\n");
				pw.print(
						"\"State\",\"Unit Type\",\"Unit ID\",\"Port ID\",\"CPRI Compression\",\"Connection Type\",\"FSU Inter Node ID\",\"FSU DU CPRI Port ID\"\n");
				ArrayList<String> cpri = new ArrayList<String>();
				ArrayList<String> cprival = new ArrayList<String>();
				for (int i = 0; i < cDataAref.size(); i++) {
					cda = cDataAref.get(i);

					if (!cpri.contains(cda.get("cpristr").toString())) {
						pw.print("\"ADD\",\"ecp\",\"" + cda.get("LCCnum") + "\",\"" + cda.get("port") + "\",\"\",\""
								+ "direct" + "\",\"" + "\",\"\"\n");
						cpri.add((String) cda.get("cpristr"));
					}
					if ((cda.get("RUPortID").toString()).equals("1")
							&& !cprival.contains(cda.get("cpristr").toString())) {
						pw.print("\"ADD\",\"ecp\",\"" + cda.get("LCCnum") + "\",\"" + cda.get("additional_port")
								+ "\",\"\",\"" + "direct" + "\",\"" + "\",\"\"\n");
						cprival.add((String) cda.get("cpristr"));

					}

				}

				pw.print("\"@RU_INFORMATION\"\n");
				pw.print(
						"\"State\",\"RU Conf\",\"RU Port\",\"Connected DU Board Type\",\"RU Type\",\"Start Earfcn1\",\"Start Earfcn2\",\"Serial Number\",\"Azimuth\",\"Beamwidth\",\"Bisector Mode\",");
				pw.print(
						"\"Fcc ID\",\"Call Sign\",\"CBSD Category\",\"X Pole Antenna\",\"Antenna Gain dBi\",\"Cable Loss\",\"Accuracy Margin dB\",\"Sharing Flag\",\"Primary Flag\"\n");
				ArrayList<String> al = new ArrayList<>();
				ArrayList<String> addCpri1 = new ArrayList<String>();
				for (int i = 0; i < rrhDataAref.size(); i++) {
					rda = rrhDataAref.get(i);

					if (rda.get("code").toString().equals("rt4401-480")) {

						((ArrayList<String>) rda.get("startearfcnA")).set(0, "55240");
						((ArrayList<String>) rda.get("startearfcnA")).set(1, "2400");
					}
					if (rda.get("code").toString().equals("rt2201-460")) {

						((ArrayList<String>) rda.get("startearfcnA")).set(0, "46790");
						((ArrayList<String>) rda.get("startearfcnA")).set(1, "2400");
					}
					if (rda.get("code").toString().equals("rfv01u-d20")) {

						((ArrayList<String>) rda.get("startearfcnA")).set(0, "5180");
						((ArrayList<String>) rda.get("startearfcnA")).set(1, "2400");
					}
					if (rda.get("code").toString().equals("rfv01u-d10")
							|| rda.get("code").toString().equals("rf4402d-d10")) {

						((ArrayList<String>) rda.get("startearfcnA")).set(0, "1950");
						((ArrayList<String>) rda.get("startearfcnA")).set(1, "600");
					}

					// ORAN radios
					if (rda.get("code").toString().equals("rf4439d-250")) {

						((ArrayList<String>) rda.get("startearfcnA")).set(0, "1950");
						((ArrayList<String>) rda.get("startearfcnA")).set(1, "600");
					}

					if (!addCpri1.contains(rda.get("cpristr").toString())) {
						pw.print("\"ADD" + "\",\"" + rda.get("cpristr") + "\",\"" + "0" + "\",\"" + "ecp" + "\",\""
								+ rda.get("code") + "\",\"" + ((ArrayList<String>) rda.get("startearfcnA")).get(0)
								+ "\",\"" + ((ArrayList<String>) rda.get("startearfcnA")).get(1) + "\",\"" + "\",\""
								+ "-1" + "\",\"" + "700\",\"bisector-mode-off\",\"");
						pw.print("A3LRT4401-48A" + "\",\"" + "na" + "\",\"" + "cbrs-not-avail" + "\",\"" + "true"
								+ "\",\"" + ((ArrayList<String>) rda.get("antennaGains")).get(0) + "\",\"" + "0"
								+ "\",\"" + "10\",\"\",\"\"\n");
						addCpri1.add((String) rda.get("cpristr"));

					}
				}

				ArrayList<String> addCpri = new ArrayList<String>();
				pw.print("\"@ADDITIONAL_CPRI_INFORMATION\"\n");
				pw.print(
						"\"State\",\"RU Conf\",\"Connected DU Board Type\",\"Additional Board ID\",\"Additional Port ID\",\"RU Additional Port ID\"\n");
				for (int i = 0; i < cDataAref.size(); i++) {
					cda = cDataAref.get(i);
					if ((cda.get("RUPortID").toString()).equals("1")) {
						if (!addCpri.contains(cda.get("cpristr").toString())) {
							pw.print("\"ADD" + "\",\"" + cda.get("cpristr") + "\",\"" + "ecp" + "\",\""
									+ cda.get("LCCnum") + "\",\"" + cda.get("additional_port") + "\",\""
									+ cda.get("RUPortID") + "\"\n");
							addCpri.add((String) cda.get("cpristr"));
						}
					}
				}
				// duplicate issue
				ArrayList<String> addCpri81 = new ArrayList<String>();
				ArrayList<TreeMap<String, Object>> deleteData = new ArrayList<>();
				for (TreeMap<String, Object> tdData : rrhDataAref) {
					if (tdData.get("bandpcs").toString().contains("AWS") && addCpri81.contains(tdData.get("cpristr"))) {
						deleteData.add(tdData);
					}
					if ((tdData.get("bandpcs")).toString().contains("AWS")) {
						addCpri81.add((String) tdData.get("cpristr"));
					}
				}

				for (TreeMap<String, Object> tdData : deleteData) {
					rrhDataAref.remove(tdData);
				}
				ArrayList<String> addCpri82 = new ArrayList<String>();
				ArrayList<TreeMap<String, Object>> deleteData1 = new ArrayList<>();
				for (TreeMap<String, Object> tdData : rrhDataAref) {
					if (tdData.get("bandpcs").toString().contains("AWS") && addCpri81.contains(tdData.get("cpristr"))) {
						deleteData1.add(tdData);
					}
					if ((tdData.get("bandpcs")).toString().contains("AWS")) {
						addCpri82.add((String) tdData.get("cpristr"));
					}
				}

				// duplicate for PCS-1 and PCS-2
				ArrayList<String> addCpri83 = new ArrayList<String>();
				ArrayList<TreeMap<String, Object>> deleteData2 = new ArrayList<>();
				for (TreeMap<String, Object> tdData : rrhDataAref) {
					if (tdData.get("bandpcs").toString().contains("PCS") && addCpri83.contains(tdData.get("cpristr"))) {
						deleteData2.add(tdData);
					}
					if ((tdData.get("bandpcs")).toString().contains("PCS")) {
						addCpri83.add((String) tdData.get("cpristr"));
					}
				}

				for (TreeMap<String, Object> tdData : deleteData2) {
					rrhDataAref.remove(tdData);
				}
				ArrayList<String> addCpri84 = new ArrayList<String>();
				ArrayList<TreeMap<String, Object>> deleteData3 = new ArrayList<>();
				for (TreeMap<String, Object> tdData : rrhDataAref) {
					if (tdData.get("bandpcs").toString().contains("PCS") && addCpri83.contains(tdData.get("cpristr"))) {
						deleteData3.add(tdData);
					}
					if ((tdData.get("bandpcs")).toString().contains("PCS")) {
						addCpri84.add((String) tdData.get("cpristr"));
					}
				}

				int a1 = 0;
				int a2 = 0;
				int a3 = 0;
				int a4 = 0;
				int a5 = 0;
				int a6 = 0;

				ArrayList<String> addCpri22 = new ArrayList<String>();

				pw.print("\"@RU_ANTENNA_PORT_INFORMATION\"\n");
				pw.print(
						"\"State\",\"RU Conf\",\"Connected DU Board Type\",\"Antenna Port ID\",\"External Antenna Port Tx Delay\",\"External Antenna Port Rx Delay\"\n");
				for (int i = 0; i < rrhDataAref.size(); i++) {
					if (!addCpri22.contains(rrhDataAref.get(i).get("cpristr").toString())) {

						if (rrhDataAref.get(i).get("bandpcs").toString().contains("PCS")
								&& rrhDataAref.get(i).get("txd").toString().contains("2")) {
							rrhDataAref.get(i).replace("antennaPortMapA", new int[] { -1, -1, -1, -1, 1, 1, -1, -1 });
						} else if (rrhDataAref.get(i).get("bandpcs").toString().contains("PCS")
								&& rrhDataAref.get(i).get("txd").toString().contains("4")) {
							rrhDataAref.get(i).replace("antennaPortMapA", new int[] { -1, -1, -1, -1, 1, 1, 1, 1 });
						} else if (rrhDataAref.get(i).get("bandpcs").toString().contains("AWS")
								&& rrhDataAref.get(i).get("txd").toString().contains("4")) {
							rrhDataAref.get(i).replace("antennaPortMapA", new int[] { 1, 1, 1, 1, -1, -1, -1, -1 });
						} else if (rrhDataAref.get(i).get("bandpcs").toString().contains("AWS")
								&& rrhDataAref.get(i).get("txd").toString().contains("2")) {
							rrhDataAref.get(i).replace("antennaPortMapA", new int[] { 1, 1, -1, -1, -1, -1, -1, -1 });
						}

						for (int j = 0; j < ((int[]) rrhDataAref.get(i).get("antennaPortMapA")).length; j++) {
							if (((int[]) rrhDataAref.get(i).get("antennaPortMapA"))[j] != -1) {
								int antport;
								antport = j + 1;

								if ((rrhDataAref.get(i).get("cpristr").toString()).equals("2_6_0")) {
									a1++;
									if (a1 < 5) {
										pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "ecp"
												+ "\",\"" + antport + "\",\"");
										pw.print(Math.round(
												(Float.parseFloat(rrhDataAref.get(i).get("delaydl").toString().trim())))
												+ "\",\"" + Math.round(Float.parseFloat(
														rrhDataAref.get(i).get("delayul").toString().trim()))
												+ "\"\n");
									}
								} else if ((rrhDataAref.get(i).get("cpristr").toString()).equals("1_6_0")) {
									a4++;
									if (a4 < 5) {
										pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "ecp"
												+ "\",\"" + antport + "\",\"");
										pw.print(Math.round(
												(Float.parseFloat(rrhDataAref.get(i).get("delaydl").toString().trim())))
												+ "\",\"" + Math.round(Float.parseFloat(
														rrhDataAref.get(i).get("delayul").toString().trim()))
												+ "\"\n");
									}
								} else if ((rrhDataAref.get(i).get("cpristr").toString()).contains("2_8_0")) {
									a2++;
									if (a2 < 5) {
										pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "ecp"
												+ "\",\"" + antport + "\",\"");
										pw.print(Math.round(
												(Float.parseFloat(rrhDataAref.get(i).get("delaydl").toString().trim())))
												+ "\",\"" + Math.round(Float.parseFloat(
														rrhDataAref.get(i).get("delayul").toString().trim()))
												+ "\"\n");
									}
								} else if ((rrhDataAref.get(i).get("cpristr").toString()).contains("1_8_0")) {
									a5++;
									if (a5 < 5) {
										pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "ecp"
												+ "\",\"" + antport + "\",\"");
										pw.print(Math.round(
												(Float.parseFloat(rrhDataAref.get(i).get("delaydl").toString().trim())))
												+ "\",\"" + Math.round(Float.parseFloat(
														rrhDataAref.get(i).get("delayul").toString().trim()))
												+ "\"\n");
									}
								} else if ((rrhDataAref.get(i).get("cpristr").toString()).contains("2_10_0")) {
									a6++;
									if (a6 < 5) {
										pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "ecp"
												+ "\",\"" + antport + "\",\"");
										pw.print(Math.round(
												(Float.parseFloat(rrhDataAref.get(i).get("delaydl").toString().trim())))
												+ "\",\"" + Math.round(Float.parseFloat(
														rrhDataAref.get(i).get("delayul").toString().trim()))
												+ "\"\n");
									}
								} else if ((rrhDataAref.get(i).get("cpristr").toString()).contains("1_10_0")) {
									a3++;
									if (a3 < 5) {
										pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "ecp"
												+ "\",\"" + antport + "\",\"");
										pw.print(Math.round(
												(Float.parseFloat(rrhDataAref.get(i).get("delaydl").toString().trim())))
												+ "\",\"" + Math.round(Float.parseFloat(
														rrhDataAref.get(i).get("delayul").toString().trim()))
												+ "\"\n");
									}
								} else {
									pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "ecp"
											+ "\",\"" + antport + "\",\"");
									pw.print(Math.round(
											(Float.parseFloat(rrhDataAref.get(i).get("delaydl").toString().trim())))
											+ "\",\""
											+ Math.round(Float
													.parseFloat(rrhDataAref.get(i).get("delayul").toString().trim()))
											+ "\"\n");
								}
							}
						}

					}
					// /*if((rrhDataAref.get(i).get("bandpcs")).toString().contains("PCS")) {
					// addCpri22.add((String)rrhDataAref.get(i).get("cpristr"));
					// }*/
					// if((rrhDataAref.get(i).get("bandpcs")).toString().contains("AWS") ||
					// rrhDataAref.get(i).get("bandpcs").toString().contains("LAA")
					// || rrhDataAref.get(i).get("bandpcs").toString().contains("CBRS")) {
					// addCpri33.add((String)rrhDataAref.get(i).get("cpristr"));
					// }
					// if((rrhDataAref.get(i).get("bandpcs")).toString().contains("850") ||
					// (rrhDataAref.get(i).get("bandpcs").toString().contains("700"))) {
					// addCpri44.add((String)rrhDataAref.get(i).get("cpristr"));
					// }
				}
				pw.print("\"@RU_GROUP_INFORMATION\"\n");
				pw.print("\"State\",\"RU Conf\",\"Group ID\"\n");
				Set<String> ruGroupInfo = new HashSet<>();
				for (int i = 0; i < cDataAref.size(); i++) {
					cda = cDataAref.get(i);
					if ((cda.get("RUPortID").toString()).equals("0")) {
						ruGroupInfo.add((String) cda.get("cpristr"));
					}

				}
				for (String value : ruGroupInfo) {
					pw.print("\"ADD" + "\",\"" + value + "\",\"" + "0\"\n");
				}
				pw.print("\"@DSP_INFORMATION\"\n");
				pw.print("\"State\",\"Unit Type\",\"Unit ID\",\"DSP ID\",\"Optic Distance\"\n");

				ArrayList<Integer> cardsProvisioned = new ArrayList<Integer>();
				for (int ci = 0; ci < cDataAref.size(); ci++) {
					if (Integer.parseInt(cDataAref.get(ci).get("LCCnum").toString()) == cardsProvisioned.size()) {
						cardsProvisioned.add(cardsProvisioned.size());
						continue;
					}
				}
				for (int li = 0; li < cardsProvisioned.size(); li++) {
					for (int di = 0; di < lccAref.get(li).size(); di++) {
						if ((Integer.parseInt(lccAref.get(li).get(di).get("opticDistance").toString())) == 0) {
							pw.print("\"ADD\",\"ecp\",\"" + li + "\",\"" + di + "\",\"20-km\"\n");
						} else {
							pw.print("\"ADD\",\"ecp\",\"" + li + "\",\"" + di + "\",\""
									+ lccAref.get(li).get(di).get("opticDistance") + "-km\"\n");
						}
					}
				}

				////////////////////// @CBRS_CHANNEL_INFORMATION////////////////////////////
				pw.print("@CBRS_CHANNEL_INFORMATION\n");
				pw.print("State,Sector ID,Carrier ID,Black Listed Channel\n");
				pw.println("NONE" + "," + "" + "," + "" + "," + "");

			} else if (supportCA.equals("true") && !cAddDataAref.isEmpty()) {

				// loop for administrative state = new/NEW
				AdNew = true;

				for (int i = 0; i < cAddDataAref.size(); i++) {
					ada = cAddDataAref.get(i);

					String path = "";

					if (ada.get("band").toString().contains("700") && ada.get("txd").equals("4")) {
						path = "select-abcd";
					} else if (ada.get("band").toString().contains("700") && ada.get("txd").equals("2")) {
						path = "select-ab";
					} else if (ada.get("band").equals("850-1") && ada.get("txd").equals("4")) {
						path = "select-efgh";
					} else if (ada.get("band").equals("850-1") && ada.get("txd").equals("2")) {
						if (ada.get("rrh").toString().contains("RIU")) {// new RIU
							path = "select-cd";
						} else {
							path = "select-ef";
						}
					} else if (ada.get("band").toString().contains("AWS") && ada.get("txd").equals("4")) {
						path = "select-abcd";
					} else if (ada.get("band").toString().contains("AWS") && ada.get("txd").equals("2")) {
						path = "select-ab";
					} else if (ada.get("band").toString().contains("PCS") && ada.get("txd").equals("4")) {
						path = "select-efgh";
					} else if (ada.get("band").toString().contains("PCS") && ada.get("txd").equals("2")) {
						if (ada.get("rrh").toString().contains("RIU")) {// new RIU
							path = "select-cd";
						} else {
							path = "select-ef";
						}
					} else if (ada.get("band").equals("CBRS") && ada.get("txd").equals("4")) {
						path = "select-abcd";
					} else if (ada.get("band").equals("LAA") && ada.get("txd").equals("4")) {
						path = "select-abcd";
					} else if (ada.get("band").equals("LAA") && ada.get("txd").equals("2")) {
						path = "select-ab";
					} // riu port
					else if (ada.get("band").toString().contains("AWS") && ada.get("txd").equals("1")) {
						path = "select-a";
					} else if (ada.get("band").toString().contains("700") && ada.get("txd").equals("1")) {
						path = "select-a";
					} else if (ada.get("band").toString().contains("PCS") && ada.get("txd").equals("1")) {
						path = "select-c";
					} else if (ada.get("band").toString().contains("850") && ada.get("txd").equals("1")) {
						path = "select-c";
					}

					System.out.println("Path value::::" + path);

					if (Integer.parseInt(ada.get("earfcndl").toString()) >= 5180
							&& Integer.parseInt(ada.get("earfcndl").toString()) <= 5279) {
						ada.replace("band", "700mhz");
					} else if (ada.get("band").equals("CBRS")
							|| (Integer.parseInt(ada.get("earfcndl").toString()) >= 55240
									&& Integer.parseInt(ada.get("earfcndl").toString()) <= 56739)) {
						ada.replace("band", "3500mhz");
						ada.replace("earfcndl", "0");
						ada.replace("earfcnul", "0");

					} else if ((Integer.parseInt(ada.get("earfcndl").toString()) >= 1950
							&& Integer.parseInt(ada.get("earfcndl").toString()) <= 2399)) {
						ada.replace("band", "2100mhz_band4");
					} else if (Integer.parseInt(ada.get("earfcndl").toString()) >= 600
							&& Integer.parseInt(ada.get("earfcndl").toString()) <= 1199) {
						ada.replace("band", "1900mhz");

					} else if (Integer.parseInt(ada.get("earfcndl").toString()) >= 2400
							&& Integer.parseInt(ada.get("earfcndl").toString()) <= 2649) {
						ada.replace("band", "850mhz");
					} else if ((Integer.parseInt(ada.get("earfcndl").toString()) >= 66436
							&& Integer.parseInt(ada.get("earfcndl").toString()) <= 67335)) {
						if (ada.get("DspCellIndex").equals("4") || ada.get("DspCellIndex").equals("5")) {
							ada.replace("typename", "cfg61-multi-carrier-10m-5m-6cell");
						}

						ada.replace("band", "2100mhz_band66");
					} else if (ada.get("band").equals("LAA")
							|| (Integer.parseInt(ada.get("earfcndl").toString()) >= 46790
									&& Integer.parseInt(ada.get("earfcndl").toString()) <= 54539)) {
						ada.replace("band", "5000mhz");
						ada.replace("earfcndl", "0");
						ada.replace("earfcnul", "0");
					} else if ((Integer.parseInt(ada.get("earfcndl").toString()) >= 8040
							&& Integer.parseInt(ada.get("earfcndl").toString()) <= 8689)) {
						ada.replace("band", "1900mhz_band25");
					}
					if (ada.get("bandwidth").equals("10")) {
						ada.replace("bandwidth", "system-bandwidth-n50");
					} else if (ada.get("bandwidth").equals("20")) {
						ada.replace("bandwidth", "system-bandwidth-n100");

					} else if (ada.get("bandwidth").equals("15")) {
						ada.replace("bandwidth", "system-bandwidth-n75");
					} else if (ada.get("bandwidth").equals("5")) {
						ada.replace("bandwidth", "system-bandwidth-n25");
						;
					}

					pw.print("\"ADD" + "\",\"" + ada.get("sectid") + "\",\"" + ada.get("carrid") + "\",\""
							+ ada.get("LCCnum") + "\",\"" + ada.get("DspID") + "\",\"" + ada.get("DspCellIndex")
							+ "\",\"" + ada.get("cpristr") + "\",\"");
					pw.print(ada.get("RUPortID") + "\",\"");

					pw.print(ada.get("band"));

					if (ada.get("txd").equals("2") && ada.get("rxd").equals("2")) {
						pw.print("\",\"" + ada.get("earfcndl") + "\",\"" + ada.get("earfcnul") + "\",\""
								+ ada.get("bandwidth") + "\",\"" + "n2-tx-antenna-count\",\"n2-rx-antenna-count"
								+ "\",\"" + path + "\",\"" + ada.get("mct").toString().toLowerCase() + "\",\"");
					} else if (ada.get("txd").equals("2") && ada.get("rxd").equals("4")) {
						pw.print("\",\"" + ada.get("earfcndl") + "\",\"" + ada.get("earfcnul") + "\",\""
								+ ada.get("bandwidth") + "\",\"" + "n2-tx-antenna-count\",\"n4-rx-antenna-count"
								+ "\",\"" + path + "\",\"" + ada.get("mct").toString().toLowerCase() + "\",\"");
					} else if (ada.get("txd").equals("'0,bs4") && ada.get("rxd").equals("2")) {
						pw.print("\",\"" + ada.get("earfcndl") + "\",\"" + ada.get("earfcnul") + "\",\""
								+ ada.get("bandwidth") + "\",\"" + "n4-tx-antenna-count\",\"n2-rx-antenna-count"
								+ "\",\"" + path + "\",\"" + ada.get("mct").toString().toLowerCase() + "\",\"");
					} else if (ada.get("txd").equals("4") && ada.get("rxd").equals("4")) {
						pw.print("\",\"" + ada.get("earfcndl") + "\",\"" + ada.get("earfcnul") + "\",\""
								+ ada.get("bandwidth") + "\",\"" + "n4-tx-antenna-count\",\"n4-rx-antenna-count"
								+ "\",\"" + path + "\",\"" + ada.get("mct").toString().toLowerCase() + "\",\"");
					} else {
						String tx = "n" + ada.get("txd") + "-tx-antenna-count";
						String rx = "n" + ada.get("rxd") + "-rx-antenna-count";
						pw.print("\",\"" + ada.get("earfcndl") + "\",\"" + ada.get("earfcnul") + "\",\""
								+ ada.get("bandwidth") + "\",\"" + tx + "\",\"" + rx + "\",\"" + path + "\",\""
								+ ada.get("mct").toString().toLowerCase() + "\",\"");
					}

					pw.print(ada.get("crs") + "\",\"" + ada.get("pci") + "\",\"" + ada.get("tac") + "\",\"\",\"" + "0"
							+ "\",\"" + "false" + "\",\"" + ada.get("zczc") + "\",\"" + ada.get("rach") + "\",\""
							+ ada.get("pracformat") + "\",\"" + "off" + "\",\"");
					pw.print(ada.get("emtc") + "\",\"" + "subframe-assignment-sa2" + "\",\""
							+ "special-subframe-pattern-ssp7\",\"" + "-" + "\",\"" + ada.get("power") + "\",\""
							+ "edge-mode" + "\",\"" + "0" + "\",\"\",\"" + ada.get("thMaxEirp") + "\",\"" + "-25"
							+ "\",\"" + ada.get("PreferredEarfcn") + "\",\"" + "update-mode1" + "\",\""
							+ "spectrum-sharing-off\",\"" + "0\",\"" + "frame-level-dss-mode\",\"" + "non-blanking\",\""
							+ "0\",\"" + "0\",\"\",\"\",\"" + "0\",\"" + "true\",\"" + "N 000:00:00.000\",\""
							+ "E 000:00:00.000\",\"0.00m\"\n");
				}

				// int lcccnt = 0;
				// if (Integer.parseInt(ada.get("LCCnum").toString()) > lcccnt) {
				// lcccnt = Integer.parseInt(ada.get("LCCnum").toString());
				// }

				pw.print("\"@NB_IOT_CELL_INFORMATION\"\n");
				pw.print(
						"\"State\",\"Cell Num\",\"Parent Cell Number\",\"NB IoT PCI\",\"Operation Mode Info\",\"NB IoT TAC\",\"Use Parent PCI for Guard-band\",\"Initial Nprach\",");
				pw.print(
						"\"Nprach Start Time CL0\",\"Nprach Subcarrier Offset CL0\",\"Nprach Start Time CL1\",\"Nprach Subcarrier Offset CL1\",\"Nprach Start Time CL2\",");
				pw.print(
						"\"Nprach Subcarrier Offset CL2\",\"Guard Band\",\"Avoid UL Interfering\",\"DL RB\",\"UL RB\"\n");
				for (int i = 0; i < cAddDataAref.size(); i++) {
					ada = cAddDataAref.get(i);
					// if ((ada.get("band").toString().contains("700mhz") ||
					// ada.get("band").toString().contains("700"))) // &&
					// ada.get("nbiot").toString().equals("1")
					// if (ada.get("nbiot").toString().equals("1")) {
					int nb = Integer.parseInt(ada.get("nbiot").toString());
					int iotT = Integer.parseInt(ada.get("iottac").toString());
					if (nb > 0) {
						if (iotT != 0 && iotT > 0) {
							pw.print("\"ADD\",\"" + ada.get("cellid") + "\",\"" + ada.get("cellid") + "\",\""
									+ ada.get("pci") + "\",\"" + "guard-band\",\"" + ada.get("iottac") + "\",\"on\",\""
									+ "on\",\"" + "nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\"");
							pw.print("nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\""
									+ "nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\"" + "right\",\""
									+ "false\",\"" + "45\",\"" + "40\"\n");
						} else {
							pw.print("\"NONE\",\"" + ada.get("cellid") + "\",\"" + ada.get("cellid") + "\",\""
									+ ada.get("pci") + "\",\"guard-band\",\"" + "0\",\"" + "off\",\"" + "off\",\""
									+ "nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\"");
							pw.print("nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\""
									+ "nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\"" + "right\",\""
									+ "false\",\"" + "45\",\"" + "40\"\n");
						}

					} else {
						pw.print("\"NONE\",\"" + ada.get("cellid") + "\",\"" + ada.get("cellid") + "\",\""
								+ ada.get("pci") + "\",\"guard-band\",\"" + "0\",\"" + "off\",\"" + "off\",\""
								+ "nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\"");
						pw.print("nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\""
								+ "nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\"" + "right\",\""
								+ "false\",\"" + "45\",\"" + "40\"\n");
					}
				}

				ArrayList<String> addCpri2 = new ArrayList<String>();
				pw.print("\"@NON_ANCHOR_NB_IOT_CELL_INFORMATION\"\n");
				pw.print("\"State\",\"Cell Num\",\"Operation Mode Info\",\"Guard Band\",\"DL RB\",\"UL RB\"\n");

				for (int i = 0; i < rrhDataAref.size(); i++) {
					if (!addCpri2.contains(rrhDataAref.get(i).get("cpristr").toString())) {
						pw.print("\"NONE\",\"\",\"\",\"\",\"\",\"\"\n");
						addCpri2.add((String) rrhDataAref.get(i).get("cpristr"));
					}
				}

				pw.print("\"@CHANNEL_BOARD_INFORMATION\"\n");
				pw.print("\"State\",\"Unit Type\",\"Unit ID\",\"Board Type\"\n");

				Set<String> lcc1 = new TreeSet<String>();
				Set<String> lcc2 = new TreeSet<String>();
				for (int i = 0; i < cAddDataAref.size(); i++) { // new
					ada = cAddDataAref.get(i);
					lcc1.add(ada.get("LCCnum").toString());
				}

				for (int i = 0; i < cDataAref.size(); i++) { // without new
					orgData = cDataAref.get(i);
					lcc2.add(orgData.get("LCCnum").toString());
				}

				lcc1.removeAll(lcc2);

				for (String s : lcc1) {
					pw.print("\"ADD\",\"ecp\",\"" + s + "\",\"lcc4-b1\"\n");
				}

				///////////////////// @cpri port information/////////////////

				// pw.print("\"@CPRI_PORT_INFORMATION\"\n");
				// pw.print("\"State\",\"Unit Type\",\"Unit ID\",\"Port ID\",\"Connection
				// Type\",\"FSU Inter Node ID\",\"FSU DU CPRI Port ID\"\n");

				pw.print("\"@CPRI_PORT_INFORMATION\"\n");
				pw.print(
						"\"State\",\"Unit Type\",\"Unit ID\",\"Port ID\",\"CPRI Compression\",\"Connection Type\",\"FSU Inter Node ID\",\"FSU DU CPRI Port ID\"\n");

				ArrayList<String> cprival = new ArrayList<String>();
				ArrayList<String> cprivalAdd = new ArrayList<String>();
				TreeSet<String> cpri = new TreeSet<String>();
				TreeSet<String> cpriAdd = new TreeSet<String>();
				TreeSet<String> ruPortCpri = new TreeSet<String>();

				for (int i = 0; i < cDataAref.size(); i++) {
					orgData = cDataAref.get(i);
					cpriAdd.add((String) orgData.get("cpristr"));

					if (orgData.get("RUPortID").equals("1")) {
						ruPortCpri.add((String) orgData.get("cpristr"));
					}
				}
				for (int i = 0; i < cAddDataAref.size(); i++) {
					ada = cAddDataAref.get(i);
					cpri.add((String) ada.get("cpristr"));
				}

				cpri.removeAll(cpriAdd);

				for (int i = 0; i < cAddDataAref.size(); i++) {

					ada = cAddDataAref.get(i);

					if (cpri.contains(ada.get("cpristr")) && !cprivalAdd.contains(ada.get("cpristr").toString())) {
						pw.print("\"ADD\",\"ecp\",\"" + ada.get("LCCnum") + "\",\"" + ada.get("port") + "\",\"\",\""
								+ "direct" + "\",\"" + "\",\"\"\n");
						cprivalAdd.add((String) ada.get("cpristr"));
					}
					if ((ada.get("RUPortID").toString()).equals("1")
							&& !cprival.contains(ada.get("cpristr").toString())) {
						if (!ruPortCpri.contains(ada.get("cpristr").toString())) {

							pw.print("\"ADD\",\"ecp\",\"" + ada.get("LCCnum") + "\",\"" + ada.get("additional_port")
									+ "\",\"\",\"" + "direct" + "\",\"" + "\",\"\"\n");
						}
						cprival.add((String) ada.get("cpristr"));
					}
				}

				pw.print("\"@RU_INFORMATION\"\n");
				pw.print(
						"\"State\",\"RU Conf\",\"RU Port\",\"Connected DU Board Type\",\"RU Type\",\"Start Earfcn1\",\"Start Earfcn2\",\"Serial Number\",\"Azimuth\",\"Beamwidth\",\"Bisector Mode\",");
				pw.print(
						"\"Fcc ID\",\"Call Sign\",\"CBSD Category\",\"X Pole Antenna\",\"Antenna Gain dBi\",\"Cable Loss\",\"Accuracy Margin dB\",\"Sharing Flag\",\"Primary Flag\"\n");

				ArrayList<String> addCpri1 = new ArrayList<String>();
				TreeSet<String> cpriA = new TreeSet<String>();
				TreeSet<String> codeA = new TreeSet<String>();
				TreeMap<String, Object> tmA = new TreeMap<>();

				for (int i = 0; i < rrhDataAref.size(); i++) {
					tmA = rrhDataAref.get(i);
					cpriA.add((String) tmA.get("cpristr"));
					codeA.add((String) tmA.get("code"));
				}

				for (int i = 0; i < rrhDataBref.size(); i++) {
					rda = rrhDataBref.get(i);

					if (rda.get("code").toString().equals("rt4401-480")) {

						((ArrayList<String>) rda.get("startearfcnA")).set(0, "55240");
						((ArrayList<String>) rda.get("startearfcnA")).set(1, "2400");
					}
					if (rda.get("code").toString().equals("rt2201-460")) {

						((ArrayList<String>) rda.get("startearfcnA")).set(0, "46790");
						((ArrayList<String>) rda.get("startearfcnA")).set(1, "2400");
					}
					if (rda.get("code").toString().equals("rfv01u-d20")) {

						((ArrayList<String>) rda.get("startearfcnA")).set(0, "5180");
						((ArrayList<String>) rda.get("startearfcnA")).set(1, "2400");
					}
					if (rda.get("code").toString().equals("rfv01u-d10")
							|| rda.get("code").toString().equals("rf4402d-d10")) {

						((ArrayList<String>) rda.get("startearfcnA")).set(0, "1950");
						((ArrayList<String>) rda.get("startearfcnA")).set(1, "600");
					}
					// ORAN radios
					if (rda.get("code").toString().equals("rf4439d-250")) {

						((ArrayList<String>) rda.get("startearfcnA")).set(0, "1950");
						((ArrayList<String>) rda.get("startearfcnA")).set(1, "600");
					}

					if (!addCpri1.contains(rda.get("cpristr").toString())) {
						if (cpriA.contains(rda.get("cpristr").toString())) {

							if (!codeA.contains(rda.get("code").toString())) {
								pw.print("\"ADD" + "\",\"" + rda.get("cpristr") + "\",\"" + "0" + "\",\"" + "ecp"
										+ "\",\"" + rda.get("code") + "\",\""
										+ ((ArrayList<String>) rda.get("startearfcnA")).get(0) + "\",\""
										+ ((ArrayList<String>) rda.get("startearfcnA")).get(1) + "\",\"" + "\",\""
										+ "-1" + "\",\"" + "700\",\"bisector-mode-off\",\"");
								pw.print("A3LRT4401-48A" + "\",\"" + "na" + "\",\"" + "cbrs-not-avail" + "\",\""
										+ "true" + "\",\"" + ((ArrayList<String>) rda.get("antennaGains")).get(0)
										+ "\",\"" + "0" + "\",\"" + "10\",\"\",\"\"\n");
								addCpri1.add((String) rda.get("cpristr"));
							}

						} else {
							pw.print("\"ADD" + "\",\"" + rda.get("cpristr") + "\",\"" + "0" + "\",\"" + "ecp" + "\",\""
									+ rda.get("code") + "\",\"" + ((ArrayList<String>) rda.get("startearfcnA")).get(0)
									+ "\",\"" + ((ArrayList<String>) rda.get("startearfcnA")).get(1) + "\",\"" + "\",\""
									+ "-1" + "\",\"" + "700\",\"bisector-mode-off\",\"");
							pw.print("A3LRT4401-48A" + "\",\"" + "na" + "\",\"" + "cbrs-not-avail" + "\",\"" + "true"
									+ "\",\"" + ((ArrayList<String>) rda.get("antennaGains")).get(0) + "\",\"" + "0"
									+ "\",\"" + "10\",\"\",\"\"\n");
							addCpri1.add((String) rda.get("cpristr"));
						}
					}
				}

				ArrayList<String> addCpri = new ArrayList<String>();
				pw.print("\"@ADDITIONAL_CPRI_INFORMATION\"\n");
				pw.print(
						"\"State\",\"RU Conf\",\"Connected DU Board Type\",\"Additional Board ID\",\"Additional Port ID\",\"RU Additional Port ID\"\n");
				TreeSet<String> addC = new TreeSet<String>();
				TreeSet<String> addAddPort = new TreeSet<String>();
				for (int i = 0; i < cDataAref.size(); i++) {
					orgData = cDataAref.get(i);
					if ((orgData.get("RUPortID").toString()).equals("1")) {
						addC.add((String) orgData.get("cpristr"));
						addAddPort.add((String) orgData.get("additional_port"));
					}
					// if (!addCpri.contains(ada.get("cpristr").toString())) {
					// pw.print("\"ADD" + "\",\"" + ada.get("cpristr") + "\",\"" + "ecp" + "\",\"" +
					// ada.get("LCCnum")
					// + "\",\"" + ada.get("additional_port") + "\",\"" + ada.get("RUPortID") +
					// "\"\n");
					// addCpri.add((String) ada.get("cpristr"));
					// }
				}

				// for carrier add
				for (int i = 0; i < cAddDataAref.size(); i++) {
					ada = cAddDataAref.get(i);
					if ((ada.get("RUPortID").toString()).equals("1")) {
						if (!addC.contains(ada.get("cpristr")) && !addAddPort.contains(ada.get("additional_port"))) {
							if (!addCpri.contains(ada.get("cpristr").toString())) {
								pw.print("\"ADD" + "\",\"" + ada.get("cpristr") + "\",\"" + "ecp" + "\",\""
										+ ada.get("LCCnum") + "\",\"" + ada.get("additional_port") + "\",\""
										+ ada.get("RUPortID") + "\"\n");
								addCpri.add((String) ada.get("cpristr"));
							}
						}
					}
				}

				// duplicate issue
				ArrayList<String> addCpri81 = new ArrayList<String>();
				ArrayList<TreeMap<String, Object>> deleteData = new ArrayList<>();
				for (TreeMap<String, Object> tdData : rrhDataBref) {
					if (tdData.get("bandpcs").toString().contains("AWS") && addCpri81.contains(tdData.get("cpristr"))) {
						deleteData.add(tdData);
					}
					if ((tdData.get("bandpcs")).toString().contains("AWS")) {
						addCpri81.add((String) tdData.get("cpristr"));
					}
				}

				for (TreeMap<String, Object> tdData : deleteData) {
					rrhDataBref.remove(tdData);
				}
				ArrayList<String> addCpri82 = new ArrayList<String>();
				ArrayList<TreeMap<String, Object>> deleteData1 = new ArrayList<>();
				for (TreeMap<String, Object> tdData : rrhDataBref) {
					if (tdData.get("bandpcs").toString().contains("AWS") && addCpri81.contains(tdData.get("cpristr"))) {
						deleteData1.add(tdData);
					}
					if ((tdData.get("bandpcs")).toString().contains("AWS")) {
						addCpri82.add((String) tdData.get("cpristr"));
					}
				}

				// duplicate for PCS-1 and PCS-2
				ArrayList<String> addCpri83 = new ArrayList<String>();
				ArrayList<TreeMap<String, Object>> deleteData2 = new ArrayList<>();
				for (TreeMap<String, Object> tdData : rrhDataBref) {
					if (tdData.get("bandpcs").toString().contains("PCS") && addCpri83.contains(tdData.get("cpristr"))) {
						deleteData2.add(tdData);
					}
					if ((tdData.get("bandpcs")).toString().contains("PCS")) {
						addCpri83.add((String) tdData.get("cpristr"));
					}
				}

				for (TreeMap<String, Object> tdData : deleteData2) {
					rrhDataBref.remove(tdData);
				}
				ArrayList<String> addCpri84 = new ArrayList<String>();
				ArrayList<TreeMap<String, Object>> deleteData3 = new ArrayList<>();
				for (TreeMap<String, Object> tdData : rrhDataBref) {
					if (tdData.get("bandpcs").toString().contains("PCS") && addCpri83.contains(tdData.get("cpristr"))) {
						deleteData3.add(tdData);
					}
					if ((tdData.get("bandpcs")).toString().contains("PCS")) {
						addCpri84.add((String) tdData.get("cpristr"));
					}
				}

				int a1 = 0;
				int a2 = 0;
				int a3 = 0;
				int a4 = 0;
				int a5 = 0;
				int a6 = 0;

				pw.print("\"@RU_ANTENNA_PORT_INFORMATION\"\n");
				pw.print(
						"\"State\",\"RU Conf\",\"Connected DU Board Type\",\"Antenna Port ID\",\"External Antenna Port Tx Delay\",\"External Antenna Port Rx Delay\"\n");
				Map<String, Object> ruMap = new TreeMap<String, Object>();
				Map<String, TreeSet<Integer>> ruMapA = new TreeMap<String, TreeSet<Integer>>();
				TreeSet<Integer> ruPort = new TreeSet<Integer>();
				TreeSet<String> ruCpriSet = new TreeSet<String>();
				TreeSet<Integer> trset = new TreeSet<Integer>();

				ArrayList<String> addCpri22 = new ArrayList<String>();
				ArrayList<String> addCpri33 = new ArrayList<String>();
				ArrayList<String> addCpri44 = new ArrayList<String>();

				for (int i = 0; i < rrhDataAref.size(); i++) {
					ruMap = rrhDataAref.get(i);
					ruCpriSet.add(ruMap.get("cpristr").toString());

					for (int j = 0; j < ((int[]) rrhDataAref.get(i).get("antennaPortMapA")).length; j++) {
						if (((int[]) rrhDataAref.get(i).get("antennaPortMapA"))[j] != -1) {
							int n = j + 1;
							ruPort.add(n);
						}
					}
					ruMapA.put(ruMap.get("cpristr").toString(), ruPort);
				}

				for (int i = 0; i < rrhDataBref.size(); i++) {
					// if (!addCpri22.contains(rrhDataBref.get(i).get("cpristr").toString()) &&
					// !addCpri33.contains(rrhDataBref.get(i).get("cpristr").toString())
					// && !addCpri44.contains(rrhDataBref.get(i).get("cpristr").toString())) {

					if (rrhDataBref.get(i).get("bandpcs").toString().contains("PCS")
							&& rrhDataBref.get(i).get("txd").toString().contains("2")) {
						rrhDataBref.get(i).replace("antennaPortMapA", new int[] { -1, -1, -1, -1, 1, 1, -1, -1 });
					} else if (rrhDataBref.get(i).get("bandpcs").toString().contains("PCS")
							&& rrhDataBref.get(i).get("txd").toString().contains("4")) {
						rrhDataBref.get(i).replace("antennaPortMapA", new int[] { -1, -1, -1, -1, 1, 1, 1, 1 });
					} else if (rrhDataBref.get(i).get("bandpcs").toString().contains("AWS")
							&& rrhDataBref.get(i).get("txd").toString().contains("4")) {
						rrhDataBref.get(i).replace("antennaPortMapA", new int[] { 1, 1, 1, 1, -1, -1, -1, -1 });
					} else if (rrhDataBref.get(i).get("bandpcs").toString().contains("AWS")
							&& rrhDataBref.get(i).get("txd").toString().contains("2")) {
						rrhDataBref.get(i).replace("antennaPortMapA", new int[] { 1, 1, -1, -1, -1, -1, -1, -1 });
					}

					for (int j = 0; j < ((int[]) rrhDataBref.get(i).get("antennaPortMapA")).length; j++) {

						if (((int[]) rrhDataBref.get(i).get("antennaPortMapA"))[j] != -1) {
							int antport = j + 1;
							if ((rrhDataBref.get(i).get("cpristr").toString()).equals("2_6_0")) {
								a1++;
								if (a1 < 5) {

									if (ruCpriSet.contains(rrhDataBref.get(i).get("cpristr"))) {

										trset = (TreeSet<Integer>) ruMapA
												.get(rrhDataBref.get(i).get("cpristr").toString());

										if (!trset.contains(antport)) {
											pw.print("\"ADD" + "\",\"" + rrhDataBref.get(i).get("cpristr") + "\",\""
													+ "ecp" + "\",\"" + antport + "\",\"");
											pw.print(Math
													.round((Float.parseFloat(
															rrhDataBref.get(i).get("delaydl").toString().trim())))
													+ "\",\""
													+ Math.round(Float.parseFloat(
															rrhDataBref.get(i).get("delayul").toString().trim()))
													+ "\"\n");
										}
									} else {
										pw.print("\"ADD" + "\",\"" + rrhDataBref.get(i).get("cpristr") + "\",\"" + "ecp"
												+ "\",\"" + antport + "\",\"");
										pw.print(Math.round(
												(Float.parseFloat(rrhDataBref.get(i).get("delaydl").toString().trim())))
												+ "\",\"" + Math.round(Float.parseFloat(
														rrhDataBref.get(i).get("delayul").toString().trim()))
												+ "\"\n");
									}
								}
							} else if ((rrhDataBref.get(i).get("cpristr").toString()).equals("1_6_0")) {
								a4++;
								if (a4 < 5) {

									if (ruCpriSet.contains(rrhDataBref.get(i).get("cpristr"))) {

										trset = (TreeSet<Integer>) ruMapA
												.get(rrhDataBref.get(i).get("cpristr").toString());

										if (!trset.contains(antport)) {
											pw.print("\"ADD" + "\",\"" + rrhDataBref.get(i).get("cpristr") + "\",\""
													+ "ecp" + "\",\"" + antport + "\",\"");
											pw.print(Math
													.round((Float.parseFloat(
															rrhDataBref.get(i).get("delaydl").toString().trim())))
													+ "\",\""
													+ Math.round(Float.parseFloat(
															rrhDataBref.get(i).get("delayul").toString().trim()))
													+ "\"\n");
										}
									}

									else {
										pw.print("\"ADD" + "\",\"" + rrhDataBref.get(i).get("cpristr") + "\",\"" + "ecp"
												+ "\",\"" + antport + "\",\"");
										pw.print(Math.round(
												(Float.parseFloat(rrhDataBref.get(i).get("delaydl").toString().trim())))
												+ "\",\"" + Math.round(Float.parseFloat(
														rrhDataBref.get(i).get("delayul").toString().trim()))
												+ "\"\n");
									}
								}
							} else if ((rrhDataBref.get(i).get("cpristr").toString()).contains("2_8_0")) {
								a2++;
								if (a2 < 5) {

									if (ruCpriSet.contains(rrhDataBref.get(i).get("cpristr"))) {

										trset = (TreeSet<Integer>) ruMapA
												.get(rrhDataBref.get(i).get("cpristr").toString());

										if (!trset.contains(antport)) {
											pw.print("\"ADD" + "\",\"" + rrhDataBref.get(i).get("cpristr") + "\",\""
													+ "ecp" + "\",\"" + antport + "\",\"");
											pw.print(Math
													.round((Float.parseFloat(
															rrhDataBref.get(i).get("delaydl").toString().trim())))
													+ "\",\""
													+ Math.round(Float.parseFloat(
															rrhDataBref.get(i).get("delayul").toString().trim()))
													+ "\"\n");
										}
									}

									else {
										pw.print("\"ADD" + "\",\"" + rrhDataBref.get(i).get("cpristr") + "\",\"" + "ecp"
												+ "\",\"" + antport + "\",\"");
										pw.print(Math.round(
												(Float.parseFloat(rrhDataBref.get(i).get("delaydl").toString().trim())))
												+ "\",\"" + Math.round(Float.parseFloat(
														rrhDataBref.get(i).get("delayul").toString().trim()))
												+ "\"\n");
									}
								}

							} else if ((rrhDataBref.get(i).get("cpristr").toString()).contains("1_8_0")) {
								a5++;
								if (a5 < 5) {

									if (ruCpriSet.contains(rrhDataBref.get(i).get("cpristr"))) {

										trset = (TreeSet<Integer>) ruMapA
												.get(rrhDataBref.get(i).get("cpristr").toString());

										if (!trset.contains(antport)) {
											pw.print("\"ADD" + "\",\"" + rrhDataBref.get(i).get("cpristr") + "\",\""
													+ "ecp" + "\",\"" + antport + "\",\"");
											pw.print(Math
													.round((Float.parseFloat(
															rrhDataBref.get(i).get("delaydl").toString().trim())))
													+ "\",\""
													+ Math.round(Float.parseFloat(
															rrhDataBref.get(i).get("delayul").toString().trim()))
													+ "\"\n");
										}
									} else {
										pw.print("\"ADD" + "\",\"" + rrhDataBref.get(i).get("cpristr") + "\",\"" + "ecp"
												+ "\",\"" + antport + "\",\"");
										pw.print(Math.round(
												(Float.parseFloat(rrhDataBref.get(i).get("delaydl").toString().trim())))
												+ "\",\"" + Math.round(Float.parseFloat(
														rrhDataBref.get(i).get("delayul").toString().trim()))
												+ "\"\n");
									}
								}
							} else if ((rrhDataBref.get(i).get("cpristr").toString()).contains("2_10_0")) {
								a6++;
								if (a6 < 5) {

									if (ruCpriSet.contains(rrhDataBref.get(i).get("cpristr"))) {

										trset = (TreeSet<Integer>) ruMapA
												.get(rrhDataBref.get(i).get("cpristr").toString());

										if (!trset.contains(antport)) {
											pw.print("\"ADD" + "\",\"" + rrhDataBref.get(i).get("cpristr") + "\",\""
													+ "ecp" + "\",\"" + antport + "\",\"");
											pw.print(Math
													.round((Float.parseFloat(
															rrhDataBref.get(i).get("delaydl").toString().trim())))
													+ "\",\""
													+ Math.round(Float.parseFloat(
															rrhDataBref.get(i).get("delayul").toString().trim()))
													+ "\"\n");
										}
									} else {
										pw.print("\"ADD" + "\",\"" + rrhDataBref.get(i).get("cpristr") + "\",\"" + "ecp"
												+ "\",\"" + antport + "\",\"");
										pw.print(Math.round(
												(Float.parseFloat(rrhDataBref.get(i).get("delaydl").toString().trim())))
												+ "\",\"" + Math.round(Float.parseFloat(
														rrhDataBref.get(i).get("delayul").toString().trim()))
												+ "\"\n");
									}
								}
							} else if ((rrhDataBref.get(i).get("cpristr").toString()).contains("1_10_0")) {
								a3++;
								if (a3 < 5) {

									if (ruCpriSet.contains(rrhDataBref.get(i).get("cpristr"))) {

										trset = (TreeSet<Integer>) ruMapA
												.get(rrhDataBref.get(i).get("cpristr").toString());

										if (!trset.contains(antport)) {
											pw.print("\"ADD" + "\",\"" + rrhDataBref.get(i).get("cpristr") + "\",\""
													+ "ecp" + "\",\"" + antport + "\",\"");
											pw.print(Math
													.round((Float.parseFloat(
															rrhDataBref.get(i).get("delaydl").toString().trim())))
													+ "\",\""
													+ Math.round(Float.parseFloat(
															rrhDataBref.get(i).get("delayul").toString().trim()))
													+ "\"\n");
										}
									}

									else {
										pw.print("\"ADD" + "\",\"" + rrhDataBref.get(i).get("cpristr") + "\",\"" + "ecp"
												+ "\",\"" + antport + "\",\"");
										pw.print(Math.round(
												(Float.parseFloat(rrhDataBref.get(i).get("delaydl").toString().trim())))
												+ "\",\"" + Math.round(Float.parseFloat(
														rrhDataBref.get(i).get("delayul").toString().trim()))
												+ "\"\n");
									}
								}
							} else {

								if (ruCpriSet.contains(rrhDataBref.get(i).get("cpristr"))) {

									trset = (TreeSet<Integer>) ruMapA.get(rrhDataBref.get(i).get("cpristr").toString());

									if (!trset.contains(antport)) {
										pw.print("\"ADD" + "\",\"" + rrhDataBref.get(i).get("cpristr") + "\",\"" + "ecp"
												+ "\",\"" + antport + "\",\"");
										pw.print(Math.round(
												(Float.parseFloat(rrhDataBref.get(i).get("delaydl").toString().trim())))
												+ "\",\"" + Math.round(Float.parseFloat(
														rrhDataBref.get(i).get("delayul").toString().trim()))
												+ "\"\n");
									}
								} else {
									pw.print("\"ADD" + "\",\"" + rrhDataBref.get(i).get("cpristr") + "\",\"" + "ecp"
											+ "\",\"" + antport + "\",\"");
									pw.print(Math.round(
											(Float.parseFloat(rrhDataBref.get(i).get("delaydl").toString().trim())))
											+ "\",\""
											+ Math.round(Float
													.parseFloat(rrhDataBref.get(i).get("delayul").toString().trim()))
											+ "\"\n");
								}
							}
						}

					}

				}

				pw.print("\"@RU_GROUP_INFORMATION\"\n");
				pw.print("\"State\",\"RU Conf\",\"Group ID\"\n");
				Set<String> ruGroupInfo = new HashSet<>();

				for (int i = 0; i < cAddDataAref.size(); i++) {
					ada = cAddDataAref.get(i);
					if ((ada.get("RUPortID").toString()).equals("0")) {
						ruGroupInfo.add((String) ada.get("cpristr"));
					}

				}

				// for carrier add
				Set<String> ruGroupAdd = new HashSet<>();

				for (int i = 0; i < cDataAref.size(); i++) {
					orgData = cDataAref.get(i);
					if ((orgData.get("RUPortID").toString()).equals("0")) {
						ruGroupAdd.add((String) orgData.get("cpristr"));
					}

				}
				///
				ruGroupInfo.removeAll(ruGroupAdd);
				for (String value : ruGroupInfo) {

					pw.print("\"ADD" + "\",\"" + value + "\",\"" + "0\"\n");

				}

				pw.print("\"@DSP_INFORMATION\"\n");
				pw.print("\"State\",\"Unit Type\",\"Unit ID\",\"DSP ID\",\"Optic Distance\"\n");

				ArrayList<Integer> cardsProvisioned = new ArrayList<Integer>();
				TreeSet<String> var = new TreeSet<>();
				TreeSet<String> varAdd = new TreeSet<>();
				System.out.println("Enter");
				int m, n = 0;
				for (int ci = 0; ci < cAddDataAref.size(); ci++) {

					m = Integer.parseInt(cAddDataAref.get(ci).get("LCCnum").toString());
					if (m >= n) {
						n = m;
						varAdd.add(cAddDataAref.get(ci).get("LCCnum").toString());
					}
					continue;
				}

				// checking without carrier add
				for (int ci = 0; ci < cDataAref.size(); ci++) {
					if (Integer.parseInt(cDataAref.get(ci).get("LCCnum").toString()) == cardsProvisioned.size()) {
						cardsProvisioned.add(cardsProvisioned.size());
						var.add(cDataAref.get(ci).get("LCCnum").toString());
						continue;
					}
				}

				varAdd.removeAll(var);
				String lowest = varAdd.first();
				String highest = varAdd.last();

				if (!varAdd.isEmpty()) {
					if (highest.equals(lowest)) {

						for (int li = Integer.parseInt(lowest); li <= varAdd.size(); li++) {
							for (int di = 0; di < lccAref.get(li).size(); di++) {
								if ((Integer.parseInt(lccAref.get(li).get(di).get("opticDistance").toString())) == 0) {
									pw.print("\"ADD\",\"ecp\",\"" + li + "\",\"" + di + "\",\"20-km\"\n");
								} else {
									pw.print("\"ADD\",\"ecp\",\"" + li + "\",\"" + di + "\",\""
											+ lccAref.get(li).get(di).get("opticDistance") + "-km\"\n");
								}
							}

						}
					} else {

						for (int li = Integer.parseInt(lowest); li < varAdd.size(); li++) {
							for (int di = 0; di < lccAref.get(li).size(); di++) {
								if ((Integer.parseInt(lccAref.get(li).get(di).get("opticDistance").toString())) == 0) {
									pw.print("\"ADD\",\"ecp\",\"" + li + "\",\"" + di + "\",\"20-km\"\n");
								} else {
									pw.print("\"ADD\",\"ecp\",\"" + li + "\",\"" + di + "\",\""
											+ lccAref.get(li).get(di).get("opticDistance") + "-km\"\n");
								}
							}

						}
					}
				}

				System.out.println("Size 2 : " + cardsProvisioned.size());

				////////////////////// @CBRS_CHANNEL_INFORMATION////////////////////////////
				pw.print("@CBRS_CHANNEL_INFORMATION\n");
				pw.print("State,Sector ID,Carrier ID,Black Listed Channel\n");
				pw.println("NONE" + "," + "" + "," + "" + "," + "");
			} else if (supportCA.equals("false")) {

				AdNew = false;

				for (int i = 0; i < addAref.size(); i++) {
					fda = addAref.get(i);
					String path = "";

					if (fda.get("band").toString().contains("700") && fda.get("txd").equals("4")) {
						path = "select-abcd";
					} else if (fda.get("band").toString().contains("700") && fda.get("txd").equals("2")) {
						path = "select-ab";
					} else if (fda.get("band").toString().contains("850") && fda.get("txd").equals("4")) {
						path = "select-efgh";
					} else if (fda.get("band").toString().contains("850") && fda.get("txd").equals("2")) {
						if (fda.get("rrh").toString().contains("RIU)")) {// new riu
							path = "select-cd";
						} else {
							path = "select-ef";
						}
					} else if (fda.get("band").toString().contains("AWS") && fda.get("txd").equals("4")) {
						path = "select-abcd";
					} else if (fda.get("band").toString().contains("AWS") && fda.get("txd").equals("2")) {
						path = "select-ab";
					} else if (fda.get("band").toString().contains("PCS") && fda.get("txd").equals("4")) {
						path = "select-efgh";
					} else if (fda.get("band").toString().contains("PCS") && fda.get("txd").equals("2")) {
						if (fda.get("rrh").toString().contains("RIU)")) {// new riu
							path = "select-cd";
						} else {
							path = "select-ef";
						}
					} else if (fda.get("band").equals("CBRS") && fda.get("txd").equals("4")) {
						path = "select-abcd";
					} else if (fda.get("band").equals("LAA") && fda.get("txd").equals("4")) {
						path = "select-abcd";
					} else if (fda.get("band").equals("LAA") && fda.get("txd").equals("2")) {
						path = "select-ab";
					} // riu port
					else if (fda.get("band").toString().contains("AWS") && fda.get("txd").equals("1")) {
						path = "select-a";
					} else if (fda.get("band").toString().contains("700") && fda.get("txd").equals("1")) {
						path = "select-a";
					} else if (fda.get("band").toString().contains("PCS") && fda.get("txd").equals("1")) {
						path = "select-c";
					} else if (fda.get("band").toString().contains("850") && fda.get("txd").equals("1")) {
						path = "select-c";
					}

					System.out.println("Path value::::" + path);

					if (Integer.parseInt(fda.get("earfcndl").toString()) >= 5180
							&& Integer.parseInt(fda.get("earfcndl").toString()) <= 5279) {
						fda.replace("band", "700mhz");
					} else if (fda.get("band").equals("CBRS")
							|| (Integer.parseInt(fda.get("earfcndl").toString()) >= 55240
									&& Integer.parseInt(fda.get("earfcndl").toString()) <= 56739)) {
						fda.replace("band", "3500mhz");
						fda.replace("earfcndl", "0");
						fda.replace("earfcnul", "0");

					} else if ((Integer.parseInt(fda.get("earfcndl").toString()) >= 1950
							&& Integer.parseInt(fda.get("earfcndl").toString()) <= 2399)) {
						fda.replace("band", "2100mhz_band4");
					} else if (Integer.parseInt(fda.get("earfcndl").toString()) >= 600
							&& Integer.parseInt(fda.get("earfcndl").toString()) <= 1199) {
						fda.replace("band", "1900mhz");

					} else if (Integer.parseInt(fda.get("earfcndl").toString()) >= 2400
							&& Integer.parseInt(fda.get("earfcndl").toString()) <= 2649) {
						fda.replace("band", "850mhz");
					} else if ((Integer.parseInt(fda.get("earfcndl").toString()) >= 66436
							&& Integer.parseInt(fda.get("earfcndl").toString()) <= 67335)) {
						if (fda.get("DspCellIndex").equals("4") || fda.get("DspCellIndex").equals("5")) {
							fda.replace("typename", "cfg61-multi-carrier-10m-5m-6cell");
						}

						fda.replace("band", "2100mhz_band66");
					} else if (fda.get("band").equals("LAA")
							|| (Integer.parseInt(fda.get("earfcndl").toString()) >= 46790
									&& Integer.parseInt(fda.get("earfcndl").toString()) <= 54539)) {
						fda.replace("band", "5000mhz");
						fda.replace("earfcndl", "0");
						fda.replace("earfcnul", "0");
					} else if ((Integer.parseInt(fda.get("earfcndl").toString()) >= 8040
							&& Integer.parseInt(fda.get("earfcndl").toString()) <= 8689)) {
						fda.replace("band", "1900mhz_band25");
					}
					if (fda.get("bandwidth").equals("10")) {
						fda.replace("bandwidth", "system-bandwidth-n50");
					} else if (fda.get("bandwidth").equals("20")) {
						fda.replace("bandwidth", "system-bandwidth-n100");

					} else if (fda.get("bandwidth").equals("15")) {
						fda.replace("bandwidth", "system-bandwidth-n75");
					} else if (fda.get("bandwidth").equals("5")) {
						fda.replace("bandwidth", "system-bandwidth-n25");
						;
					}

					pw.print("\"ADD" + "\",\"" + fda.get("sectid") + "\",\"" + fda.get("carrid") + "\",\""
							+ fda.get("LCCnum") + "\",\"" + fda.get("DspID") + "\",\"" + fda.get("DspCellIndex")
							+ "\",\"" + fda.get("cpristr") + "\",\"");
					pw.print(fda.get("RUPortID") + "\",\"");

					pw.print(fda.get("band"));

					if (fda.get("txd").equals("2") && fda.get("rxd").equals("2")) {
						pw.print("\",\"" + fda.get("earfcndl") + "\",\"" + fda.get("earfcnul") + "\",\""
								+ fda.get("bandwidth") + "\",\"" + "n2-tx-antenna-count\",\"n2-rx-antenna-count"
								+ "\",\"" + path + "\",\"" + fda.get("mct").toString().toLowerCase() + "\",\"");
					} else if (fda.get("txd").equals("2") && fda.get("rxd").equals("4")) {
						pw.print("\",\"" + fda.get("earfcndl") + "\",\"" + fda.get("earfcnul") + "\",\""
								+ fda.get("bandwidth") + "\",\"" + "n2-tx-antenna-count\",\"n4-rx-antenna-count"
								+ "\",\"" + path + "\",\"" + fda.get("mct").toString().toLowerCase() + "\",\"");
					} else if (fda.get("txd").equals("'0,bs4") && fda.get("rxd").equals("2")) {
						pw.print("\",\"" + fda.get("earfcndl") + "\",\"" + fda.get("earfcnul") + "\",\""
								+ fda.get("bandwidth") + "\",\"" + "n4-tx-antenna-count\",\"n2-rx-antenna-count"
								+ "\",\"" + path + "\",\"" + fda.get("mct").toString().toLowerCase() + "\",\"");
					} else if (fda.get("txd").equals("4") && fda.get("rxd").equals("4")) {
						pw.print("\",\"" + fda.get("earfcndl") + "\",\"" + fda.get("earfcnul") + "\",\""
								+ fda.get("bandwidth") + "\",\"" + "n4-tx-antenna-count\",\"n4-rx-antenna-count"
								+ "\",\"" + path + "\",\"" + fda.get("mct").toString().toLowerCase() + "\",\"");
					} else {
						String tx = "n" + fda.get("txd") + "-tx-antenna-count";
						String rx = "n" + fda.get("rxd") + "-rx-antenna-count";
						pw.print("\",\"" + fda.get("earfcndl") + "\",\"" + fda.get("earfcnul") + "\",\""
								+ fda.get("bandwidth") + "\",\"" + tx + "\",\"" + rx + "\",\"" + path + "\",\""
								+ fda.get("mct").toString().toLowerCase() + "\",\"");
					}

					pw.print(fda.get("crs") + "\",\"" + fda.get("pci") + "\",\"" + fda.get("tac") + "\",\"\",\"" + "0"
							+ "\",\"" + "false" + "\",\"" + fda.get("zczc") + "\",\"" + fda.get("rach") + "\",\""
							+ fda.get("pracformat") + "\",\"" + "off" + "\",\"");
					pw.print(fda.get("emtc") + "\",\"" + "subframe-assignment-sa2" + "\",\""
							+ "special-subframe-pattern-ssp7\",\"" + "-" + "\",\"" + fda.get("power") + "\",\""
							+ "edge-mode" + "\",\"" + "0" + "\",\"\",\"" + fda.get("thMaxEirp") + "\",\"" + "-25"
							+ "\",\"" + fda.get("PreferredEarfcn") + "\",\"" + "update-mode1" + "\",\""
							+ "spectrum-sharing-off\",\"" + "0\",\"" + "frame-level-dss-mode\",\"" + "non-blanking\",\""
							+ "0\",\"" + "0\",\"\",\"\",\"" + "0\",\"" + "true\",\"" + "N 000:00:00.000\",\""
							+ "E 000:00:00.000\",\"0.00m\"\n");

				}
				int lcccnt = 0;

				if (fda.containsKey("LCCnum") && StringUtils.isNotEmpty(fda.get("LCCnum").toString())
						&& Integer.parseInt(fda.get("LCCnum").toString()) > lcccnt) {
					lcccnt = Integer.parseInt(fda.get("LCCnum").toString());
				}

				pw.print("\"@NB_IOT_CELL_INFORMATION\"\n");
				pw.print(
						"\"State\",\"Cell Num\",\"Parent Cell Number\",\"NB IoT PCI\",\"Operation Mode Info\",\"NB IoT TAC\",\"Use Parent PCI for Guard-band\",\"Initial Nprach\",");
				pw.print(
						"\"Nprach Start Time CL0\",\"Nprach Subcarrier Offset CL0\",\"Nprach Start Time CL1\",\"Nprach Subcarrier Offset CL1\",\"Nprach Start Time CL2\",");
				pw.print(
						"\"Nprach Subcarrier Offset CL2\",\"Guard Band\",\"Avoid UL Interfering\",\"DL RB\",\"UL RB\"\n");
				for (int i = 0; i < addAref.size(); i++) {
					fda = addAref.get(i);

					// if (fda.get("nbiot").toString().equals("1")){

					// if ((fda.get("band").toString().contains("700mhz") ||
					// fda.get("band").toString().contains("700"))) {

					int nb = Integer.parseInt(fda.get("nbiot").toString());
					int iotT = Integer.parseInt(fda.get("iottac").toString());
					if (nb > 0) {
						if (iotT != 0 && iotT > 0) {
							pw.print("\"ADD\",\"" + fda.get("cellid") + "\",\"" + fda.get("cellid") + "\",\""
									+ fda.get("pci") + "\",\"" + "guard-band\",\"" + fda.get("iottac") + "\",\"on\",\""
									+ "on\",\"" + "nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\"");
							pw.print("nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\""
									+ "nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\"" + "right\",\""
									+ "false\",\"" + "45\",\"" + "40\"\n");
						} else {
							pw.print("\"NONE\",\"" + fda.get("cellid") + "\",\"" + fda.get("cellid") + "\",\""
									+ fda.get("pci") + "\",\"guard-band\",\"" + "0\",\"" + "off\",\"" + "off\",\""
									+ "nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\"");
							pw.print("nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\""
									+ "nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\"" + "right\",\""
									+ "false\",\"" + "45\",\"" + "40\"\n");
						}

					} else {
						pw.print("\"NONE\",\"" + fda.get("cellid") + "\",\"" + fda.get("cellid") + "\",\""
								+ fda.get("pci") + "\",\"guard-band\",\"" + "0\",\"" + "off\",\"" + "off\",\""
								+ "nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\"");
						pw.print("nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\""
								+ "nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\"" + "right\",\""
								+ "false\",\"" + "45\",\"" + "40\"\n");
					}
				}

				ArrayList<String> addCpri2 = new ArrayList<String>();
				pw.print("\"@NON_ANCHOR_NB_IOT_CELL_INFORMATION\"\n");
				pw.print("\"State\",\"Cell Num\",\"Operation Mode Info\",\"Guard Band\",\"DL RB\",\"UL RB\"\n");

				for (int i = 0; i < rrhDataCref.size(); i++) {
					if (!addCpri2.contains(rrhDataCref.get(i).get("cpristr").toString())) {
						pw.print("\"NONE\",\"\",\"\",\"\",\"\",\"\"\n");
						addCpri2.add((String) rrhDataCref.get(i).get("cpristr"));
					}
				}

				pw.print("\"@CHANNEL_BOARD_INFORMATION\"\n");
				pw.print("\"State\",\"Unit Type\",\"Unit ID\",\"Board Type\"\n");
				for (int i = 0; i <= lcccnt; i++) {
					pw.print("\"ADD\",\"ecp\",\"" + i + "\",\"lcc4-b1\"\n");
				}

				///////////////////// @cpri port information/////////////////

				// pw.print("\"@CPRI_PORT_INFORMATION\"\n");
				// pw.print("\"State\",\"Unit Type\",\"Unit ID\",\"Port ID\",\"Connection
				// Type\",\"FSU Inter Node ID\",\"FSU DU CPRI Port ID\"\n");

				pw.print("\"@CPRI_PORT_INFORMATION\"\n");
				pw.print(
						"\"State\",\"Unit Type\",\"Unit ID\",\"Port ID\",\"CPRI Compression\",\"Connection Type\",\"FSU Inter Node ID\",\"FSU DU CPRI Port ID\"\n");
				ArrayList<String> cpri = new ArrayList<String>();
				ArrayList<String> cprival = new ArrayList<String>();
				for (int i = 0; i < addAref.size(); i++) {
					fda = addAref.get(i);

					if (!cpri.contains(fda.get("cpristr").toString())) {
						pw.print("\"ADD\",\"ecp\",\"" + fda.get("LCCnum") + "\",\"" + fda.get("port") + "\",\"\",\""
								+ "direct" + "\",\"" + "\",\"\"\n");
						cpri.add((String) fda.get("cpristr"));
					}
					if ((fda.get("RUPortID").toString()).equals("1")
							&& !cprival.contains(fda.get("cpristr").toString())) {
						pw.print("\"ADD\",\"ecp\",\"" + fda.get("LCCnum") + "\",\"" + fda.get("additional_port")
								+ "\",\"\",\"" + "direct" + "\",\"" + "\",\"\"\n");
						cprival.add((String) fda.get("cpristr"));

					}

				}

				pw.print("\"@RU_INFORMATION\"\n");
				pw.print(
						"\"State\",\"RU Conf\",\"RU Port\",\"Connected DU Board Type\",\"RU Type\",\"Start Earfcn1\",\"Start Earfcn2\",\"Serial Number\",\"Azimuth\",\"Beamwidth\",\"Bisector Mode\",");
				pw.print(
						"\"Fcc ID\",\"Call Sign\",\"CBSD Category\",\"X Pole Antenna\",\"Antenna Gain dBi\",\"Cable Loss\",\"Accuracy Margin dB\",\"Sharing Flag\",\"Primary Flag\"\n");
				ArrayList<String> al = new ArrayList<>();
				ArrayList<String> addCpri1 = new ArrayList<String>();
				for (int i = 0; i < rrhDataCref.size(); i++) {
					rda = rrhDataCref.get(i);

					if (rda.get("code").toString().equals("rt4401-480")) {

						((ArrayList<String>) rda.get("startearfcnA")).set(0, "55240");
						((ArrayList<String>) rda.get("startearfcnA")).set(1, "2400");
					}
					if (rda.get("code").toString().equals("rt2201-460")) {

						((ArrayList<String>) rda.get("startearfcnA")).set(0, "46790");
						((ArrayList<String>) rda.get("startearfcnA")).set(1, "2400");
					}
					if (rda.get("code").toString().equals("rfv01u-d20")) {

						((ArrayList<String>) rda.get("startearfcnA")).set(0, "5180");
						((ArrayList<String>) rda.get("startearfcnA")).set(1, "2400");
					}
					if (rda.get("code").toString().equals("rfv01u-d10")
							|| rda.get("code").toString().equals("rf4402d-d10")) {

						((ArrayList<String>) rda.get("startearfcnA")).set(0, "1950");
						((ArrayList<String>) rda.get("startearfcnA")).set(1, "600");
					}
					// ORAN radios
					if (rda.get("code").toString().equals("rf4439d-250")) {

						((ArrayList<String>) rda.get("startearfcnA")).set(0, "1950");
						((ArrayList<String>) rda.get("startearfcnA")).set(1, "600");
					}

					if (!addCpri1.contains(rda.get("cpristr").toString())) {
						pw.print("\"ADD" + "\",\"" + rda.get("cpristr") + "\",\"" + "0" + "\",\"" + "ecp" + "\",\""
								+ rda.get("code") + "\",\"" + ((ArrayList<String>) rda.get("startearfcnA")).get(0)
								+ "\",\"" + ((ArrayList<String>) rda.get("startearfcnA")).get(1) + "\",\"" + "\",\""
								+ "-1" + "\",\"" + "700\",\"bisector-mode-off\",\"");
						pw.print("A3LRT4401-48A" + "\",\"" + "na" + "\",\"" + "cbrs-not-avail" + "\",\"" + "true"
								+ "\",\"" + ((ArrayList<String>) rda.get("antennaGains")).get(0) + "\",\"" + "0"
								+ "\",\"" + "10\",\"\",\"\"\n");
						addCpri1.add((String) rda.get("cpristr"));

					}
				}

				ArrayList<String> addCpri = new ArrayList<String>();
				pw.print("\"@ADDITIONAL_CPRI_INFORMATION\"\n");
				pw.print(
						"\"State\",\"RU Conf\",\"Connected DU Board Type\",\"Additional Board ID\",\"Additional Port ID\",\"RU Additional Port ID\"\n");
				for (int i = 0; i < addAref.size(); i++) {
					fda = addAref.get(i);
					if ((fda.get("RUPortID").toString()).equals("1")) {
						if (!addCpri.contains(fda.get("cpristr").toString())) {
							pw.print("\"ADD" + "\",\"" + fda.get("cpristr") + "\",\"" + "ecp" + "\",\""
									+ fda.get("LCCnum") + "\",\"" + fda.get("additional_port") + "\",\""
									+ fda.get("RUPortID") + "\"\n");
							addCpri.add((String) fda.get("cpristr"));
						}
					}
				}
				// duplicate issue
				ArrayList<String> addCpri81 = new ArrayList<String>();
				ArrayList<TreeMap<String, Object>> deleteData = new ArrayList<>();
				for (TreeMap<String, Object> tdData : rrhDataCref) {
					if (tdData.get("bandpcs").toString().contains("AWS") && addCpri81.contains(tdData.get("cpristr"))) {
						deleteData.add(tdData);
					}
					if ((tdData.get("bandpcs")).toString().contains("AWS")) {
						addCpri81.add((String) tdData.get("cpristr"));
					}
				}

				for (TreeMap<String, Object> tdData : deleteData) {
					rrhDataCref.remove(tdData);
				}
				ArrayList<String> addCpri82 = new ArrayList<String>();
				ArrayList<TreeMap<String, Object>> deleteData1 = new ArrayList<>();
				for (TreeMap<String, Object> tdData : rrhDataCref) {
					if (tdData.get("bandpcs").toString().contains("AWS") && addCpri81.contains(tdData.get("cpristr"))) {
						deleteData1.add(tdData);
					}
					if ((tdData.get("bandpcs")).toString().contains("AWS")) {
						addCpri82.add((String) tdData.get("cpristr"));
					}
				}

				// duplicate for PCS-1 and PCS-2
				ArrayList<String> addCpri83 = new ArrayList<String>();
				ArrayList<TreeMap<String, Object>> deleteData2 = new ArrayList<>();
				for (TreeMap<String, Object> tdData : rrhDataCref) {
					if (tdData.get("bandpcs").toString().contains("PCS") && addCpri83.contains(tdData.get("cpristr"))) {
						deleteData2.add(tdData);
					}
					if ((tdData.get("bandpcs")).toString().contains("PCS")) {
						addCpri83.add((String) tdData.get("cpristr"));
					}
				}

				for (TreeMap<String, Object> tdData : deleteData2) {
					rrhDataCref.remove(tdData);
				}
				ArrayList<String> addCpri84 = new ArrayList<String>();
				ArrayList<TreeMap<String, Object>> deleteData3 = new ArrayList<>();
				for (TreeMap<String, Object> tdData : rrhDataCref) {
					if (tdData.get("bandpcs").toString().contains("PCS") && addCpri83.contains(tdData.get("cpristr"))) {
						deleteData3.add(tdData);
					}
					if ((tdData.get("bandpcs")).toString().contains("PCS")) {
						addCpri84.add((String) tdData.get("cpristr"));
					}
				}

				int a1 = 0;
				int a2 = 0;
				int a3 = 0;
				int a4 = 0;
				int a5 = 0;
				int a6 = 0;

				ArrayList<String> addCpri22 = new ArrayList<String>();
				Map<String, TreeSet<Integer>> ruMap = new HashMap<String, TreeSet<Integer>>();
				ArrayList<String> addCpri33 = new ArrayList<String>();
				// ArrayList <String> addCpri44 = new ArrayList<String>();
				pw.print("\"@RU_ANTENNA_PORT_INFORMATION\"\n");
				pw.print(
						"\"State\",\"RU Conf\",\"Connected DU Board Type\",\"Antenna Port ID\",\"External Antenna Port Tx Delay\",\"External Antenna Port Rx Delay\"\n");

				for (int i = 0; i < rrhDataCref.size(); i++) {
					// TreeSet<Integer> ruSet = new TreeSet<Integer>();
					if (!addCpri22.contains(rrhDataCref.get(i).get("cpristr").toString())) {

						if (rrhDataCref.get(i).get("bandpcs").toString().contains("PCS")
								&& rrhDataCref.get(i).get("txd").toString().contains("2")) {
							rrhDataCref.get(i).replace("antennaPortMapA", new int[] { -1, -1, -1, -1, 1, 1, -1, -1 });
						} else if (rrhDataCref.get(i).get("bandpcs").toString().contains("PCS")
								&& rrhDataCref.get(i).get("txd").toString().contains("4")) {
							rrhDataCref.get(i).replace("antennaPortMapA", new int[] { -1, -1, -1, -1, 1, 1, 1, 1 });
						} else if (rrhDataCref.get(i).get("bandpcs").toString().contains("AWS")
								&& rrhDataCref.get(i).get("txd").toString().contains("4")) {
							rrhDataCref.get(i).replace("antennaPortMapA", new int[] { 1, 1, 1, 1, -1, -1, -1, -1 });
						} else if (rrhDataCref.get(i).get("bandpcs").toString().contains("AWS")
								&& rrhDataCref.get(i).get("txd").toString().contains("2")) {
							rrhDataCref.get(i).replace("antennaPortMapA", new int[] { 1, 1, -1, -1, -1, -1, -1, -1 });
						}

						for (int j = 0; j < ((int[]) rrhDataCref.get(i).get("antennaPortMapA")).length; j++) {
							if (((int[]) rrhDataCref.get(i).get("antennaPortMapA"))[j] != -1) {
								int antport;
								// if((rrhDataCref.get(i).get("bandpcs")).toString().contains("PCS")) {
								// if(j < 4) {
								// antport = j + 5;
								// }
								// else {
								// antport = j + 1;
								// }
								// }
								// else {
								antport = j + 1;
								// }

								if ((rrhDataCref.get(i).get("cpristr").toString()).equals("2_6_0")) {
									a1++;
									if (a1 < 5) {
										pw.print("\"ADD" + "\",\"" + rrhDataCref.get(i).get("cpristr") + "\",\"" + "ecp"
												+ "\",\"" + antport + "\",\"");
										pw.print(Math.round(
												(Float.parseFloat(rrhDataCref.get(i).get("delaydl").toString().trim())))
												+ "\",\"" + Math.round(Float.parseFloat(
														rrhDataCref.get(i).get("delayul").toString().trim()))
												+ "\"\n");
									}
								} else if ((rrhDataCref.get(i).get("cpristr").toString()).equals("1_6_0")) {
									a4++;
									if (a4 < 5) {
										pw.print("\"ADD" + "\",\"" + rrhDataCref.get(i).get("cpristr") + "\",\"" + "ecp"
												+ "\",\"" + antport + "\",\"");
										pw.print(Math.round(
												(Float.parseFloat(rrhDataCref.get(i).get("delaydl").toString().trim())))
												+ "\",\"" + Math.round(Float.parseFloat(
														rrhDataCref.get(i).get("delayul").toString().trim()))
												+ "\"\n");
									}
								} else if ((rrhDataCref.get(i).get("cpristr").toString()).contains("2_8_0")) {
									a2++;
									if (a2 < 5) {
										pw.print("\"ADD" + "\",\"" + rrhDataCref.get(i).get("cpristr") + "\",\"" + "ecp"
												+ "\",\"" + antport + "\",\"");
										pw.print(Math.round(
												(Float.parseFloat(rrhDataCref.get(i).get("delaydl").toString().trim())))
												+ "\",\"" + Math.round(Float.parseFloat(
														rrhDataCref.get(i).get("delayul").toString().trim()))
												+ "\"\n");
									}
								} else if ((rrhDataCref.get(i).get("cpristr").toString()).contains("1_8_0")) {
									a5++;
									if (a5 < 5) {
										pw.print("\"ADD" + "\",\"" + rrhDataCref.get(i).get("cpristr") + "\",\"" + "ecp"
												+ "\",\"" + antport + "\",\"");
										pw.print(Math.round(
												(Float.parseFloat(rrhDataCref.get(i).get("delaydl").toString().trim())))
												+ "\",\"" + Math.round(Float.parseFloat(
														rrhDataCref.get(i).get("delayul").toString().trim()))
												+ "\"\n");
									}
								} else if ((rrhDataCref.get(i).get("cpristr").toString()).contains("2_10_0")) {
									a6++;
									if (a6 < 5) {
										pw.print("\"ADD" + "\",\"" + rrhDataCref.get(i).get("cpristr") + "\",\"" + "ecp"
												+ "\",\"" + antport + "\",\"");
										pw.print(Math.round(
												(Float.parseFloat(rrhDataCref.get(i).get("delaydl").toString().trim())))
												+ "\",\"" + Math.round(Float.parseFloat(
														rrhDataCref.get(i).get("delayul").toString().trim()))
												+ "\"\n");
									}
								} else if ((rrhDataCref.get(i).get("cpristr").toString()).contains("1_10_0")) {
									a3++;
									if (a3 < 5) {
										pw.print("\"ADD" + "\",\"" + rrhDataCref.get(i).get("cpristr") + "\",\"" + "ecp"
												+ "\",\"" + antport + "\",\"");
										pw.print(Math.round(
												(Float.parseFloat(rrhDataCref.get(i).get("delaydl").toString().trim())))
												+ "\",\"" + Math.round(Float.parseFloat(
														rrhDataCref.get(i).get("delayul").toString().trim()))
												+ "\"\n");
									}
								} else {
									pw.print("\"ADD" + "\",\"" + rrhDataCref.get(i).get("cpristr") + "\",\"" + "ecp"
											+ "\",\"" + antport + "\",\"");
									pw.print(Math.round(
											(Float.parseFloat(rrhDataCref.get(i).get("delaydl").toString().trim())))
											+ "\",\""
											+ Math.round(Float
													.parseFloat(rrhDataCref.get(i).get("delayul").toString().trim()))
											+ "\"\n");
								}
								// ruSet.add(antport);
							}
						}

					}
					/*
					 * if((rrhDataCref.get(i).get("bandpcs")).toString().contains("PCS")) {
					 * addCpri22.add((String)rrhDataCref.get(i).get("cpristr")); } else {
					 * addCpri33.add((String)rrhDataCref.get(i).get("cpristr")); }
					 */
				}
				pw.print("\"@RU_GROUP_INFORMATION\"\n");
				pw.print("\"State\",\"RU Conf\",\"Group ID\"\n");
				Set<String> ruGroupInfo = new HashSet<>();
				for (int i = 0; i < addAref.size(); i++) {
					fda = addAref.get(i);
					if ((fda.get("RUPortID").toString()).equals("0")) {
						ruGroupInfo.add((String) fda.get("cpristr"));
					}

				}
				for (String value : ruGroupInfo) {
					pw.print("\"ADD" + "\",\"" + value + "\",\"" + "0\"\n");
				}
				pw.print("\"@DSP_INFORMATION\"\n");
				pw.print("\"State\",\"Unit Type\",\"Unit ID\",\"DSP ID\",\"Optic Distance\"\n");

				ArrayList<Integer> cardsProvisioned = new ArrayList<Integer>();
				for (int ci = 0; ci < addAref.size(); ci++) {
					if (Integer.parseInt(addAref.get(ci).get("LCCnum").toString()) == cardsProvisioned.size()) {
						cardsProvisioned.add(cardsProvisioned.size());
						continue;
					}
				}
				for (int li = 0; li < cardsProvisioned.size(); li++) {
					for (int di = 0; di < lccAref.get(li).size(); di++) {
						if ((Integer.parseInt(lccAref.get(li).get(di).get("opticDistance").toString())) == 0) {
							pw.print("\"ADD\",\"ecp\",\"" + li + "\",\"" + di + "\",\"20-km\"\n");
						} else {
							pw.print("\"ADD\",\"ecp\",\"" + li + "\",\"" + di + "\",\""
									+ lccAref.get(li).get(di).get("opticDistance") + "-km\"\n");
						}
					}
				}

				////////////////////// @CBRS_CHANNEL_INFORMATION////////////////////////////
				pw.print("@CBRS_CHANNEL_INFORMATION\n");
				pw.print("State,Sector ID,Carrier ID,Black Listed Channel\n");
				pw.println("NONE" + "," + "" + "," + "" + "," + "");
			}

		} catch (Exception e) {
			mydie(e.toString());
		} finally {
			if (pw != null) {
				pw.flush();
				pw.close();
			}
		}
		return AdNew;
	}

	// **********************************************21D support For
	// USM***********************************************************************************************//
	private static boolean createGrowTemplate_21D0(ArrayList<TreeMap<String, Object>> cellDataAref,
			ArrayList<TreeMap<String, Object>> rrhDataAref, ArrayList<ArrayList<TreeMap<String, Object>>> lccAref,
			String growfile, ArrayList<TreeMap<String, Object>> rrhDataBref,
			ArrayList<TreeMap<String, Object>> rrhDataCref) {
		PrintWriter pw = null;
		try {
			String val = "";
			File f = new File(growfile);
			f.createNewFile();
			pw = new PrintWriter(new BufferedWriter(new FileWriter(f)));

			add_optic_distance_to_dsps(lccAref, cellDataAref);

			int lcccnt = 0, lccnt_add = 0;

			pw.print("\"@CELL_INFORMATION\"\n");
			pw.print(
					"\"State\",\"Sector ID\",\"Carrier ID\",\"CC ID\",\"DSP ID\",\"Cell Index in DSP\",\"RU Conf\",\"RU Port ID\",");
			pw.print(
					"\"Cell Band Carrier\",\"Earfcn DL\",\"Earfcn UL\",\"Bandwidth\",\"DL Antenna Count\",\"UL Antenna Count\",\"Path\",");
			pw.print(
					"\"Multi Carrier Type\",\"CRS\",\"PCI\",\"TAC\",\"Cell Size\",\"EAID\",\"HSF\",\"ZCZC\",\"RSI\",\"PRACH Configuration Index\",\"Virtual RF Port Mapping\",\"eMTC\",\"Subframe Assignment\",\"Special Subframe Patterns\",");
			pw.print(
					"\"Frequency Profile\",\"Dl Max Tx Power\",\"Pucch Center Mode\",\"Rcc ID\",\"Max EIRP Selection Mode\",\"Preferred Earfcn\",\"CBRS Carrier Update Enable\",\"Dynamic Spectrum Sharing Mode\",\"DSS Target NR Cell Num\",\"Slot Level Operation Mode\",\"CDMA Blanking Case\",\"CDMA Blanking Upper\",\"CDMA Blanking Lower\",\"DSS PUCCH HARQ ACK for CA FDD\",\"MV IO Site Migration Indicator\",\"Term Point To DSS Index\",\"Auto GPS\",\"Latitude\",\"Longitude\",\"Height\"\n");

			TreeMap<String, Object> cda = new TreeMap<String, Object>();
			TreeMap<String, Object> rda = new TreeMap<String, Object>();

			// Adding new data structures for carrier add , administrative state = NEW/new
			TreeMap<String, Object> fda = new TreeMap<String, Object>();
			TreeMap<String, Object> ada = new TreeMap<String, Object>();
			TreeMap<String, Object> addData = new TreeMap<String, Object>();
			TreeMap<String, Object> caddData = new TreeMap<String, Object>();
			TreeMap<String, Object> orgData = new TreeMap<String, Object>();
			ArrayList<TreeMap<String, Object>> addAref = new ArrayList<TreeMap<String, Object>>();
			ArrayList<TreeMap<String, Object>> cDataAref = new ArrayList<TreeMap<String, Object>>();
			ArrayList<TreeMap<String, Object>> cAddDataAref = new ArrayList<TreeMap<String, Object>>();

			for (int i = 0; i < cellDataAref.size(); i++) {

				addData = cellDataAref.get(i);
				addAref.add(addData);
				// storing data based on administrative state
				if (addData.get("adstate").toString().contains("NEW")) {

					caddData = cellDataAref.get(i);
					cAddDataAref.add(caddData);

				} else {
					orgData = cellDataAref.get(i);
					cDataAref.add(orgData);

				}
			}

			if (supportCA.equals("true") && cAddDataAref.isEmpty()) {
				AdNew = false;

				for (int i = 0; i < cellDataAref.size(); i++) {
					cda = cellDataAref.get(i);

					String path = "";

					if (cda.get("band").toString().contains("700") && cda.get("txd").equals("4")) {
						path = "select-abcd";
					} else if (cda.get("band").toString().contains("700") && cda.get("txd").equals("2")) {
						path = "select-ab";
					} else if (cda.get("band").toString().contains("850") && cda.get("txd").equals("4")) {
						path = "select-efgh";
					} else if (cda.get("band").toString().contains("850") && cda.get("txd").equals("2")) {
						if (cda.get("rrh").toString().contains("RIU")) { // new riu
							path = "select-cd";
						} else {
							path = "select-ef";
						}
					} else if (cda.get("band").toString().contains("AWS") && cda.get("txd").equals("4")) {
						path = "select-abcd";
					} else if (cda.get("band").toString().contains("AWS") && cda.get("txd").equals("2")) {
						path = "select-ab";
					} else if (cda.get("band").toString().contains("PCS") && cda.get("txd").equals("4")) {
						path = "select-efgh";
					} else if (cda.get("band").toString().contains("PCS") && cda.get("txd").equals("2")) {
						if (cda.get("rrh").toString().contains("RIU")) { // new riu
							path = "select-cd";
						} else {
							path = "select-ef";
						}
					} else if (cda.get("band").equals("CBRS") && cda.get("txd").equals("4")) {
						path = "select-abcd";
					} else if (cda.get("band").equals("LAA") && cda.get("txd").equals("4")) {
						path = "select-abcd";
					} else if (cda.get("band").equals("LAA") && cda.get("txd").equals("2")) {
						path = "select-ab";
					} // riu port
					else if (cda.get("band").toString().contains("AWS") && cda.get("txd").equals("1")) {
						path = "select-a";
					} else if (cda.get("band").toString().contains("700") && cda.get("txd").equals("1")) {
						path = "select-a";
					} else if (cda.get("band").toString().contains("PCS") && cda.get("txd").equals("1")) {
						path = "select-c";
					} else if (cda.get("band").toString().contains("850") && cda.get("txd").equals("1")) {
						path = "select-c";
					}

					System.out.println("Path value::::" + path);

					if (Integer.parseInt(cda.get("earfcndl").toString()) >= 5180
							&& Integer.parseInt(cda.get("earfcndl").toString()) <= 5279) {
						cda.replace("band", "700mhz");
					} else if (cda.get("band").equals("CBRS")
							|| (Integer.parseInt(cda.get("earfcndl").toString()) >= 55240
									&& Integer.parseInt(cda.get("earfcndl").toString()) <= 56739)) {
						cda.replace("band", "3500mhz");
						cda.replace("earfcndl", "0");
						cda.replace("earfcnul", "0");

					} else if ((Integer.parseInt(cda.get("earfcndl").toString()) >= 1950
							&& Integer.parseInt(cda.get("earfcndl").toString()) <= 2399)) {
						cda.replace("band", "2100mhz_band4");
					} else if (Integer.parseInt(cda.get("earfcndl").toString()) >= 600
							&& Integer.parseInt(cda.get("earfcndl").toString()) <= 1199) {
						cda.replace("band", "1900mhz");

					} else if (Integer.parseInt(cda.get("earfcndl").toString()) >= 2400
							&& Integer.parseInt(cda.get("earfcndl").toString()) <= 2649) {
						cda.replace("band", "850mhz");
					} else if ((Integer.parseInt(cda.get("earfcndl").toString()) >= 66436
							&& Integer.parseInt(cda.get("earfcndl").toString()) <= 67335)) {
						if (cda.get("DspCellIndex").equals("4") || cda.get("DspCellIndex").equals("5")) {
							cda.replace("typename", "cfg61-multi-carrier-10m-5m-6cell");
						}

						cda.replace("band", "2100mhz_band66");
					} else if (cda.get("band").equals("LAA")
							|| (Integer.parseInt(cda.get("earfcndl").toString()) >= 46790
									&& Integer.parseInt(cda.get("earfcndl").toString()) <= 54539)) {
						cda.replace("band", "5000mhz");
						cda.replace("earfcndl", "0");
						cda.replace("earfcnul", "0");
					} else if ((Integer.parseInt(cda.get("earfcndl").toString()) >= 8040
							&& Integer.parseInt(cda.get("earfcndl").toString()) <= 8689)) {
						cda.replace("band", "1900mhz_band25");
					}
					if (cda.get("bandwidth").equals("10")) {
						cda.replace("bandwidth", "system-bandwidth-n50");
					} else if (cda.get("bandwidth").equals("20")) {
						cda.replace("bandwidth", "system-bandwidth-n100");

					} else if (cda.get("bandwidth").equals("15")) {
						cda.replace("bandwidth", "system-bandwidth-n75");
					} else if (cda.get("bandwidth").equals("5")) {
						cda.replace("bandwidth", "system-bandwidth-n25");
						;
					}

					pw.print("\"ADD" + "\",\"" + cda.get("sectid") + "\",\"" + cda.get("carrid") + "\",\""
							+ cda.get("LCCnum") + "\",\"" + cda.get("DspID") + "\",\"" + cda.get("DspCellIndex")
							+ "\",\"" + cda.get("cpristr") + "\",\"");
					pw.print(cda.get("RUPortID") + "\",\"");

					pw.print(cda.get("band"));

					if (cda.get("txd").equals("2") && cda.get("rxd").equals("2")) {
						pw.print("\",\"" + cda.get("earfcndl") + "\",\"" + cda.get("earfcnul") + "\",\""
								+ cda.get("bandwidth") + "\",\"" + "n2-tx-antenna-count\",\"n2-rx-antenna-count"
								+ "\",\"" + path + "\",\"" + cda.get("mct").toString().toLowerCase() + "\",\"");
					} else if (cda.get("txd").equals("2") && cda.get("rxd").equals("4")) {
						pw.print("\",\"" + cda.get("earfcndl") + "\",\"" + cda.get("earfcnul") + "\",\""
								+ cda.get("bandwidth") + "\",\"" + "n2-tx-antenna-count\",\"n4-rx-antenna-count"
								+ "\",\"" + path + "\",\"" + cda.get("mct").toString().toLowerCase() + "\",\"");
					} else if (cda.get("txd").equals("'0,bs4") && cda.get("rxd").equals("2")) {
						pw.print("\",\"" + cda.get("earfcndl") + "\",\"" + cda.get("earfcnul") + "\",\""
								+ cda.get("bandwidth") + "\",\"" + "n4-tx-antenna-count\",\"n2-rx-antenna-count"
								+ "\",\"" + path + "\",\"" + cda.get("mct").toString().toLowerCase() + "\",\"");
					} else if (cda.get("txd").equals("4") && cda.get("rxd").equals("4")) {
						pw.print("\",\"" + cda.get("earfcndl") + "\",\"" + cda.get("earfcnul") + "\",\""
								+ cda.get("bandwidth") + "\",\"" + "n4-tx-antenna-count\",\"n4-rx-antenna-count"
								+ "\",\"" + path + "\",\"" + cda.get("mct").toString().toLowerCase() + "\",\"");
					} else {
						String tx = "n" + cda.get("txd") + "-tx-antenna-count";
						String rx = "n" + cda.get("rxd") + "-rx-antenna-count";
						pw.print("\",\"" + cda.get("earfcndl") + "\",\"" + cda.get("earfcnul") + "\",\""
								+ cda.get("bandwidth") + "\",\"" + tx + "\",\"" + rx + "\",\"" + path + "\",\""
								+ cda.get("mct").toString().toLowerCase() + "\",\"");
					}

					pw.print(cda.get("crs") + "\",\"" + cda.get("pci") + "\",\"" + cda.get("tac") + "\",\"\",\"" + "0"
							+ "\",\"" + "false" + "\",\"" + cda.get("zczc") + "\",\"" + cda.get("rach") + "\",\""
							+ cda.get("pracformat") + "\",\"" + "off" + "\",\"");
					pw.print(cda.get("emtc") + "\",\"" + "subframe-assignment-sa2" + "\",\""
							+ "special-subframe-pattern-ssp7\",\"" + "-" + "\",\"" + cda.get("power") + "\",\""
							+ "edge-mode" + "\",\"" + "0" + "\",\"\",\"" + cda.get("PreferredEarfcn") + "\",\""
							+ "update-mode1" + "\",\"" + "spectrum-sharing-off\",\"" + "0\",\""
							+ "frame-level-dss-mode\",\"" + "non-blanking\",\"" + "0\",\"" + "0\",\"\",\"\",\""
							+ "0\",\"" + "true\",\"" + "N 000:00:00.000\",\"" + "E 000:00:00.000\",\"0.00m\"\n");

				}

				/*if (Integer.parseInt(cda.get("LCCnum").toString()) > lcccnt) {
					lcccnt = Integer.parseInt(cda.get("LCCnum").toString());
				}
				*/
				if (cda.containsKey("LCCnum") && StringUtils.isNotEmpty(cda.get("LCCnum").toString())
						&& Integer.parseInt(cda.get("LCCnum").toString()) > lcccnt) {
					lcccnt = Integer.parseInt(cda.get("LCCnum").toString());
				}


				pw.print("\"@NB_IOT_CELL_INFORMATION\"\n");
				pw.print(
						"\"State\",\"Cell Num\",\"Parent Cell Number\",\"NB IoT PCI\",\"Operation Mode Info\",\"NB IoT TAC\",\"Use Parent PCI for Guard-band\",\"Initial Nprach\",");
				pw.print(
						"\"Nprach Start Time CL0\",\"Nprach Subcarrier Offset CL0\",\"Nprach Start Time CL1\",\"Nprach Subcarrier Offset CL1\",\"Nprach Start Time CL2\",");
				pw.print(
						"\"Nprach Subcarrier Offset CL2\",\"Guard Band\",\"Avoid UL Interfering\",\"DL RB\",\"UL RB\"\n");
				for (int i = 0; i < cellDataAref.size(); i++) {
					cda = cellDataAref.get(i);

					int nb = Integer.parseInt(cda.get("nbiot").toString());
					int iotT = Integer.parseInt(cda.get("iottac").toString());
					String emtc = cda.get("emtc").toString();
					String bandW = cda.get("bandwidth").toString();
					if (nb > 0) {
						if (iotT != 0 && iotT > 0 /* && emtc.equals("enable") */) {
							// if(emtc.equals("enable")) {
							if (bandW.equals("system-bandwidth-n25")) {
								pw.print("\"ADD\",\"" + cda.get("cellid") + "\",\"" + cda.get("cellid") + "\",\""
										+ cda.get("pci") + "\",\"" + "in-band-same-pci\",\"" + cda.get("iottac")
										+ "\",\"on\",\"" + "on\",\"" + "nprach-start-time-ms8\",\""
										+ "nprach-subcarrier-offset-n36\",\"");
								pw.print("nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\""
										+ "nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\""
										+ "right\",\"" + "false\",\"" + "17\",\"" + "19\"\n");
							} else {
								pw.print("\"ADD\",\"" + cda.get("cellid") + "\",\"" + cda.get("cellid") + "\",\""
										+ cda.get("pci") + "\",\"" + "guard-band\",\"" + cda.get("iottac")
										+ "\",\"on\",\"" + "on\",\"" + "nprach-start-time-ms8\",\""
										+ "nprach-subcarrier-offset-n36\",\"");
								pw.print("nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\""
										+ "nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\""
										+ "right\",\"" + "false\",\"" + "45\",\"" + "40\"\n");
							}
						} else {
							pw.print("\"NONE\",\"" + cda.get("cellid") + "\",\"" + cda.get("cellid") + "\",\""
									+ cda.get("pci") + "\",\"guard-band\",\"" + "0\",\"" + "off\",\"" + "off\",\""
									+ "nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\"");
							pw.print("nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\""
									+ "nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\"" + "right\",\""
									+ "false\",\"" + "45\",\"" + "40\"\n");
						}

					} else {
						pw.print("\"NONE\",\"" + cda.get("cellid") + "\",\"" + cda.get("cellid") + "\",\""
								+ cda.get("pci") + "\",\"guard-band\",\"" + "0\",\"" + "off\",\"" + "off\",\""
								+ "nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\"");
						pw.print("nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\""
								+ "nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\"" + "right\",\""
								+ "false\",\"" + "45\",\"" + "40\"\n");
					}
				}
				ArrayList<String> addCpri2 = new ArrayList<String>();
				pw.print("\"@NON_ANCHOR_NB_IOT_CELL_INFORMATION\"\n");
				pw.print("\"State\",\"Cell Num\",\"Operation Mode Info\",\"Guard Band\",\"DL RB\",\"UL RB\"\n");

				for (int i = 0; i < rrhDataAref.size(); i++) {
					if (!addCpri2.contains(rrhDataAref.get(i).get("cpristr").toString())) {
						pw.print("\"NONE\",\"\",\"\",\"\",\"\",\"\"\n");
						addCpri2.add((String) rrhDataAref.get(i).get("cpristr"));
					}
				}
				pw.print("\"@CHANNEL_BOARD_INFORMATION\"\n");
				pw.print("\"State\",\"Unit Type\",\"Unit ID\",\"Board Type\"\n");
				for (int i = 0; i <= lcccnt; i++) {
					pw.print("\"ADD\",\"ecp\",\"" + i + "\",\"lcc4-b1\"\n");
				}

				///////////////////// @cpri port information/////////////////

				// pw.print("\"@CPRI_PORT_INFORMATION\"\n");
				// pw.print("\"State\",\"Unit Type\",\"Unit ID\",\"Port ID\",\"Connection
				// Type\",\"FSU Inter Node ID\",\"FSU DU CPRI Port ID\"\n");

				pw.print("\"@CPRI_PORT_INFORMATION\"\n");
				pw.print(
						"\"State\",\"Unit Type\",\"Unit ID\",\"Port ID\",\"CPRI Compression\",\"Connection Type\",\"FSU Inter Node ID\",\"FSU DU CPRI Port ID\"\n");
				ArrayList<String> cpri = new ArrayList<String>();
				ArrayList<String> cprival = new ArrayList<String>();
				for (int i = 0; i < cellDataAref.size(); i++) {
					cda = cellDataAref.get(i);

					if (!cpri.contains(cda.get("cpristr").toString())) {
						pw.print("\"ADD\",\"ecp\",\"" + cda.get("LCCnum") + "\",\"" + cda.get("port") + "\",\"\",\""
								+ "direct" + "\",\"" + "\",\"\"\n");
						cpri.add((String) cda.get("cpristr"));
					}
					if ((cda.get("RUPortID").toString()).equals("1")
							&& !cprival.contains(cda.get("cpristr").toString())) {
						pw.print("\"ADD\",\"ecp\",\"" + cda.get("LCCnum") + "\",\"" + cda.get("additional_port")
								+ "\",\"\",\"" + "direct" + "\",\"" + "\",\"\"\n");
						cprival.add((String) cda.get("cpristr"));

					}

				}

				pw.print("\"@RU_INFORMATION\"\n");
				pw.print(
						"\"State\",\"RU Conf\",\"RU Port\",\"Connected DU Board Type\",\"RU Type\",\"Start Earfcn1\",\"Start Earfcn2\",\"Serial Number\",\"Azimuth\",\"Beamwidth\",\"Bisector Mode\",");
				pw.print(
						"\"X Pole Antenna\",\"Antenna Gain dBi\",\"Cable Loss\",\"Accuracy Margin dB\",\"Sharing Flag\",\"Primary Flag\"\n");
				ArrayList<String> al = new ArrayList<>();
				ArrayList<String> addCpri1 = new ArrayList<String>();
				for (int i = 0; i < rrhDataAref.size(); i++) {
					rda = rrhDataAref.get(i);

					if (rda.get("code").toString().equals("rt4401-480")) {

						((ArrayList<String>) rda.get("startearfcnA")).set(0, "55240");
						((ArrayList<String>) rda.get("startearfcnA")).set(1, "2400");
					}
					if (rda.get("code").toString().equals("rt2201-460")) {

						((ArrayList<String>) rda.get("startearfcnA")).set(0, "46790");
						((ArrayList<String>) rda.get("startearfcnA")).set(1, "2400");
					}
					if (rda.get("code").toString().equals("rfv01u-d20")) {

						((ArrayList<String>) rda.get("startearfcnA")).set(0, "5180");
						((ArrayList<String>) rda.get("startearfcnA")).set(1, "2400");
					}
					if (rda.get("code").toString().equals("rfv01u-d10")
							|| rda.get("code").toString().equals("rf4402d-d10")) {

						((ArrayList<String>) rda.get("startearfcnA")).set(0, "1950");
						((ArrayList<String>) rda.get("startearfcnA")).set(1, "600");
					}
					// ORAN radios
					if (rda.get("code").toString().equals("rf4439d-250")) {

						((ArrayList<String>) rda.get("startearfcnA")).set(0, "1950");
						((ArrayList<String>) rda.get("startearfcnA")).set(1, "600");
					}

					if (!addCpri1.contains(rda.get("cpristr").toString())) {
						pw.print("\"ADD" + "\",\"" + rda.get("cpristr") + "\",\"" + "0" + "\",\"" + "ecp" + "\",\""
								+ rda.get("code") + "\",\"" + ((ArrayList<String>) rda.get("startearfcnA")).get(0)
								+ "\",\"" + ((ArrayList<String>) rda.get("startearfcnA")).get(1) + "\",\"" + "\",\""
								+ "-1" + "\",\"" + "700\",\"bisector-mode-off\",\"");
						pw.print("true" + "\",\"" + ((ArrayList<String>) rda.get("antennaGains")).get(0) + "\",\"" + "0"
								+ "\",\"" + "10\",\"\",\"\"\n");
						addCpri1.add((String) rda.get("cpristr"));

					}
				}

				ArrayList<String> addCpri = new ArrayList<String>();
				pw.print("\"@ADDITIONAL_CPRI_INFORMATION\"\n");
				pw.print(
						"\"State\",\"RU Conf\",\"Connected DU Board Type\",\"Additional Board ID\",\"Additional Port ID\",\"RU Additional Port ID\"\n");
				for (int i = 0; i < cellDataAref.size(); i++) {
					cda = cellDataAref.get(i);
					if ((cda.get("RUPortID").toString()).equals("1")) {
						if (!addCpri.contains(cda.get("cpristr").toString())) {
							pw.print("\"ADD" + "\",\"" + cda.get("cpristr") + "\",\"" + "ecp" + "\",\""
									+ cda.get("LCCnum") + "\",\"" + cda.get("additional_port") + "\",\""
									+ cda.get("RUPortID") + "\"\n");
							addCpri.add((String) cda.get("cpristr"));
						}
					}
				}

				// duplicate issue
				ArrayList<String> addCpri81 = new ArrayList<String>();
				ArrayList<TreeMap<String, Object>> deleteData = new ArrayList<>();
				for (TreeMap<String, Object> tdData : rrhDataAref) {
					if (tdData.get("bandpcs").toString().contains("AWS") && addCpri81.contains(tdData.get("cpristr"))) {
						deleteData.add(tdData);
					}
					if ((tdData.get("bandpcs")).toString().contains("AWS")) {
						addCpri81.add((String) tdData.get("cpristr"));
					}
				}

				for (TreeMap<String, Object> tdData : deleteData) {
					rrhDataAref.remove(tdData);
				}
				ArrayList<String> addCpri82 = new ArrayList<String>();
				ArrayList<TreeMap<String, Object>> deleteData1 = new ArrayList<>();
				for (TreeMap<String, Object> tdData : rrhDataAref) {
					if (tdData.get("bandpcs").toString().contains("AWS") && addCpri81.contains(tdData.get("cpristr"))) {
						deleteData1.add(tdData);
					}
					if ((tdData.get("bandpcs")).toString().contains("AWS")) {
						addCpri82.add((String) tdData.get("cpristr"));
					}
				}

				// duplicate for PCS-1 and PCS-2
				ArrayList<String> addCpri83 = new ArrayList<String>();
				ArrayList<TreeMap<String, Object>> deleteData2 = new ArrayList<>();
				for (TreeMap<String, Object> tdData : rrhDataAref) {
					if (tdData.get("bandpcs").toString().contains("PCS") && addCpri83.contains(tdData.get("cpristr"))) {
						deleteData2.add(tdData);
					}
					if ((tdData.get("bandpcs")).toString().contains("PCS")) {
						addCpri83.add((String) tdData.get("cpristr"));
					}
				}

				for (TreeMap<String, Object> tdData : deleteData2) {
					rrhDataAref.remove(tdData);
				}
				ArrayList<String> addCpri84 = new ArrayList<String>();
				ArrayList<TreeMap<String, Object>> deleteData3 = new ArrayList<>();
				for (TreeMap<String, Object> tdData : rrhDataAref) {
					if (tdData.get("bandpcs").toString().contains("PCS") && addCpri83.contains(tdData.get("cpristr"))) {
						deleteData3.add(tdData);
					}
					if ((tdData.get("bandpcs")).toString().contains("PCS")) {
						addCpri84.add((String) tdData.get("cpristr"));
					}
				}

				int a1 = 0;
				int a2 = 0;
				int a3 = 0;
				int a4 = 0;
				int a5 = 0;
				int a6 = 0;

				ArrayList<String> addCpri22 = new ArrayList<String>();
				pw.print("\"@RU_ANTENNA_PORT_INFORMATION\"\n");
				pw.print(
						"\"State\",\"RU Conf\",\"Connected DU Board Type\",\"Antenna Port ID\",\"External Antenna Port Tx Delay\",\"External Antenna Port Rx Delay\"\n");
				for (int i = 0; i < rrhDataAref.size(); i++) {

					if (rrhDataAref.get(i).get("bandpcs").toString().contains("PCS")
							&& rrhDataAref.get(i).get("txd").toString().contains("2")) {
						rrhDataAref.get(i).replace("antennaPortMapA", new int[] { -1, -1, -1, -1, 1, 1, -1, -1 });
					} else if (rrhDataAref.get(i).get("bandpcs").toString().contains("PCS")
							&& rrhDataAref.get(i).get("txd").toString().contains("4")) {
						rrhDataAref.get(i).replace("antennaPortMapA", new int[] { -1, -1, -1, -1, 1, 1, 1, 1 });
					} else if (rrhDataAref.get(i).get("bandpcs").toString().contains("AWS")
							&& rrhDataAref.get(i).get("txd").toString().contains("4")) {
						rrhDataAref.get(i).replace("antennaPortMapA", new int[] { 1, 1, 1, 1, -1, -1, -1, -1 });
					} else if (rrhDataAref.get(i).get("bandpcs").toString().contains("AWS")
							&& rrhDataAref.get(i).get("txd").toString().contains("2")) {
						rrhDataAref.get(i).replace("antennaPortMapA", new int[] { 1, 1, -1, -1, -1, -1, -1, -1 });
					}

					if (!addCpri22.contains(rrhDataAref.get(i).get("cpristr").toString())) {
						for (int j = 0; j < ((int[]) rrhDataAref.get(i).get("antennaPortMapA")).length; j++) {
							if (((int[]) rrhDataAref.get(i).get("antennaPortMapA"))[j] != -1) {
								int antport;
								antport = j + 1;

								if ((rrhDataAref.get(i).get("cpristr").toString()).equals("2_6_0")) {
									a1++;
									if (a1 < 5) {
										pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "ecp"
												+ "\",\"" + antport + "\",\"");
										pw.print(Math.round(
												(Float.parseFloat(rrhDataAref.get(i).get("delaydl").toString().trim())))
												+ "\",\"" + Math.round(Float.parseFloat(
														rrhDataAref.get(i).get("delayul").toString().trim()))
												+ "\"\n");
									}
								} else if ((rrhDataAref.get(i).get("cpristr").toString()).equals("1_6_0")) {
									a4++;
									if (a4 < 5) {
										pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "ecp"
												+ "\",\"" + antport + "\",\"");
										pw.print(Math.round(
												(Float.parseFloat(rrhDataAref.get(i).get("delaydl").toString().trim())))
												+ "\",\"" + Math.round(Float.parseFloat(
														rrhDataAref.get(i).get("delayul").toString().trim()))
												+ "\"\n");
									}
								} else if ((rrhDataAref.get(i).get("cpristr").toString()).contains("2_8_0")) {
									a2++;
									if (a2 < 5) {
										pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "ecp"
												+ "\",\"" + antport + "\",\"");
										pw.print(Math.round(
												(Float.parseFloat(rrhDataAref.get(i).get("delaydl").toString().trim())))
												+ "\",\"" + Math.round(Float.parseFloat(
														rrhDataAref.get(i).get("delayul").toString().trim()))
												+ "\"\n");
									}
								} else if ((rrhDataAref.get(i).get("cpristr").toString()).contains("1_8_0")) {
									a5++;
									if (a5 < 5) {
										pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "ecp"
												+ "\",\"" + antport + "\",\"");
										pw.print(Math.round(
												(Float.parseFloat(rrhDataAref.get(i).get("delaydl").toString().trim())))
												+ "\",\"" + Math.round(Float.parseFloat(
														rrhDataAref.get(i).get("delayul").toString().trim()))
												+ "\"\n");
									}
								} else if ((rrhDataAref.get(i).get("cpristr").toString()).contains("2_10_0")) {
									a6++;
									if (a6 < 5) {
										pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "ecp"
												+ "\",\"" + antport + "\",\"");
										pw.print(Math.round(
												(Float.parseFloat(rrhDataAref.get(i).get("delaydl").toString().trim())))
												+ "\",\"" + Math.round(Float.parseFloat(
														rrhDataAref.get(i).get("delayul").toString().trim()))
												+ "\"\n");
									}
								} else if ((rrhDataAref.get(i).get("cpristr").toString()).contains("1_10_0")) {
									a3++;
									if (a3 < 5) {
										pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "ecp"
												+ "\",\"" + antport + "\",\"");
										pw.print(Math.round(
												(Float.parseFloat(rrhDataAref.get(i).get("delaydl").toString().trim())))
												+ "\",\"" + Math.round(Float.parseFloat(
														rrhDataAref.get(i).get("delayul").toString().trim()))
												+ "\"\n");
									}
								} else {
									pw.print("\"ADD" + "\",\"" + rrhDataAref.get(i).get("cpristr") + "\",\"" + "ecp"
											+ "\",\"" + antport + "\",\"");
									pw.print(Math.round(
											(Float.parseFloat(rrhDataAref.get(i).get("delaydl").toString().trim())))
											+ "\",\""
											+ Math.round(Float
													.parseFloat(rrhDataAref.get(i).get("delayul").toString().trim()))
											+ "\"\n");
								}
							}
						}
						// if((rrhDataAref.get(i).get("bandpcs")).toString().contains("PCS")) {
						// addCpri22.add((String)rrhDataAref.get(i).get("cpristr"));
						// }
					}
				}

				pw.print("\"@DSP_INFORMATION\"\n");
				pw.print("\"State\",\"Unit Type\",\"Unit ID\",\"DSP ID\",\"Optic Distance\"\n");

				ArrayList<Integer> cardsProvisioned = new ArrayList<Integer>();
				for (int ci = 0; ci < cellDataAref.size(); ci++) {
					if (Integer.parseInt(cellDataAref.get(ci).get("LCCnum").toString()) == cardsProvisioned.size()) {
						cardsProvisioned.add(cardsProvisioned.size());
						continue;
					}
				}
				for (int li = 0; li < cardsProvisioned.size(); li++) {
					for (int di = 0; di < lccAref.get(li).size(); di++) {
						if ((Integer.parseInt(lccAref.get(li).get(di).get("opticDistance").toString())) == 0) {
							pw.print("\"ADD\",\"ecp\",\"" + li + "\",\"" + di + "\",\"20-km\"\n");
						} else {
							pw.print("\"ADD\",\"ecp\",\"" + li + "\",\"" + di + "\",\""
									+ lccAref.get(li).get(di).get("opticDistance") + "-km\"\n");
						}
					}
				}

				////////////////////// @CBRS_CHANNEL_INFORMATION////////////////////////////
				pw.print("@CBRS_CHANNEL_INFORMATION\n");
				pw.print("State,Sector ID,Carrier ID,Block Listed Channel\n");
				pw.println("NONE" + "," + "" + "," + "" + "," + "");

			} else if (supportCA.equals("true") && !cAddDataAref.isEmpty()) {

				AdNew = true;
				// loop for administrative state = new/NEW
				for (int i = 0; i < cAddDataAref.size(); i++) {
					ada = cAddDataAref.get(i);

					String path = "";

					if (ada.get("band").toString().contains("700") && ada.get("txd").equals("4")) {
						path = "select-abcd";
					} else if (ada.get("band").toString().contains("700") && ada.get("txd").equals("2")) {
						path = "select-ab";
					} else if (ada.get("band").equals("850-1") && ada.get("txd").equals("4")) {
						path = "select-efgh";
					} else if (ada.get("band").equals("850-1") && ada.get("txd").equals("2")) {
						if (ada.get("rrh").toString().contains("RIU")) {// new riu
							path = "select-cd";
						} else {
							path = "select-ef";
						}
					} else if (ada.get("band").toString().contains("AWS") && ada.get("txd").equals("4")) {
						path = "select-abcd";
					} else if (ada.get("band").toString().contains("AWS") && ada.get("txd").equals("2")) {
						path = "select-ab";
					} else if (ada.get("band").toString().contains("PCS") && ada.get("txd").equals("4")) {
						path = "select-efgh";
					} else if (ada.get("band").toString().contains("PCS") && ada.get("txd").equals("2")) {
						if (ada.get("rrh").toString().contains("RIU")) {// new riu
							path = "select-cd";
						} else {
							path = "select-ef";
						}
					} else if (ada.get("band").equals("CBRS") && ada.get("txd").equals("4")) {
						path = "select-abcd";
					} else if (ada.get("band").equals("LAA") && ada.get("txd").equals("4")) {
						path = "select-abcd";
					} else if (ada.get("band").equals("LAA") && ada.get("txd").equals("2")) {
						path = "select-ab";
					} // riu port
					else if (ada.get("band").toString().contains("AWS") && ada.get("txd").equals("1")) {
						path = "select-a";
					} else if (ada.get("band").toString().contains("700") && ada.get("txd").equals("1")) {
						path = "select-a";
					} else if (ada.get("band").toString().contains("PCS") && ada.get("txd").equals("1")) {
						path = "select-c";
					} else if (ada.get("band").toString().contains("850") && ada.get("txd").equals("1")) {
						path = "select-c";
					}

					System.out.println("Path value::::" + path);

					if (Integer.parseInt(ada.get("earfcndl").toString()) >= 5180
							&& Integer.parseInt(ada.get("earfcndl").toString()) <= 5279) {
						ada.replace("band", "700mhz");
					} else if (ada.get("band").equals("CBRS")
							|| (Integer.parseInt(ada.get("earfcndl").toString()) >= 55240
									&& Integer.parseInt(ada.get("earfcndl").toString()) <= 56739)) {
						ada.replace("band", "3500mhz");
						ada.replace("earfcndl", "0");
						ada.replace("earfcnul", "0");

					} else if ((Integer.parseInt(ada.get("earfcndl").toString()) >= 1950
							&& Integer.parseInt(ada.get("earfcndl").toString()) <= 2399)) {
						ada.replace("band", "2100mhz_band4");
					} else if (Integer.parseInt(ada.get("earfcndl").toString()) >= 600
							&& Integer.parseInt(ada.get("earfcndl").toString()) <= 1199) {
						ada.replace("band", "1900mhz");

					} else if (Integer.parseInt(ada.get("earfcndl").toString()) >= 2400
							&& Integer.parseInt(ada.get("earfcndl").toString()) <= 2649) {
						ada.replace("band", "850mhz");
					} else if ((Integer.parseInt(ada.get("earfcndl").toString()) >= 66436
							&& Integer.parseInt(ada.get("earfcndl").toString()) <= 67335)) {
						if (ada.get("DspCellIndex").equals("4") || ada.get("DspCellIndex").equals("5")) {
							ada.replace("typename", "cfg61-multi-carrier-10m-5m-6cell");
						}

						ada.replace("band", "2100mhz_band66");
					} else if (ada.get("band").equals("LAA")
							|| (Integer.parseInt(ada.get("earfcndl").toString()) >= 46790
									&& Integer.parseInt(ada.get("earfcndl").toString()) <= 54539)) {
						ada.replace("band", "5000mhz");
						ada.replace("earfcndl", "0");
						ada.replace("earfcnul", "0");
					} else if ((Integer.parseInt(ada.get("earfcndl").toString()) >= 8040
							&& Integer.parseInt(ada.get("earfcndl").toString()) <= 8689)) {
						ada.replace("band", "1900mhz_band25");
					}
					if (ada.get("bandwidth").equals("10")) {
						ada.replace("bandwidth", "system-bandwidth-n50");
					} else if (ada.get("bandwidth").equals("20")) {
						ada.replace("bandwidth", "system-bandwidth-n100");

					} else if (ada.get("bandwidth").equals("15")) {
						ada.replace("bandwidth", "system-bandwidth-n75");
					} else if (ada.get("bandwidth").equals("5")) {
						ada.replace("bandwidth", "system-bandwidth-n25");
						;
					}
					pw.print("\"ADD" + "\",\"" + ada.get("sectid") + "\",\"" + ada.get("carrid") + "\",\""
							+ ada.get("LCCnum") + "\",\"" + ada.get("DspID") + "\",\"" + ada.get("DspCellIndex")
							+ "\",\"" + ada.get("cpristr") + "\",\"");
					pw.print(ada.get("RUPortID") + "\",\"");

					pw.print(ada.get("band"));

					if (ada.get("txd").equals("2") && ada.get("rxd").equals("2")) {
						pw.print("\",\"" + ada.get("earfcndl") + "\",\"" + ada.get("earfcnul") + "\",\""
								+ ada.get("bandwidth") + "\",\"" + "n2-tx-antenna-count\",\"n2-rx-antenna-count"
								+ "\",\"" + path + "\",\"" + ada.get("mct").toString().toLowerCase() + "\",\"");
					} else if (ada.get("txd").equals("2") && ada.get("rxd").equals("4")) {
						pw.print("\",\"" + ada.get("earfcndl") + "\",\"" + ada.get("earfcnul") + "\",\""
								+ ada.get("bandwidth") + "\",\"" + "n2-tx-antenna-count\",\"n4-rx-antenna-count"
								+ "\",\"" + path + "\",\"" + ada.get("mct").toString().toLowerCase() + "\",\"");
					} else if (ada.get("txd").equals("'0,bs4") && ada.get("rxd").equals("2")) {
						pw.print("\",\"" + ada.get("earfcndl") + "\",\"" + ada.get("earfcnul") + "\",\""
								+ ada.get("bandwidth") + "\",\"" + "n4-tx-antenna-count\",\"n2-rx-antenna-count"
								+ "\",\"" + path + "\",\"" + ada.get("mct").toString().toLowerCase() + "\",\"");
					} else if (ada.get("txd").equals("4") && ada.get("rxd").equals("4")) {
						pw.print("\",\"" + ada.get("earfcndl") + "\",\"" + ada.get("earfcnul") + "\",\""
								+ ada.get("bandwidth") + "\",\"" + "n4-tx-antenna-count\",\"n4-rx-antenna-count"
								+ "\",\"" + path + "\",\"" + ada.get("mct").toString().toLowerCase() + "\",\"");
					} else {
						String tx = "n" + ada.get("txd") + "-tx-antenna-count";
						String rx = "n" + ada.get("rxd") + "-rx-antenna-count";
						pw.print("\",\"" + ada.get("earfcndl") + "\",\"" + ada.get("earfcnul") + "\",\""
								+ ada.get("bandwidth") + "\",\"" + tx + "\",\"" + rx + "\",\"" + path + "\",\""
								+ ada.get("mct").toString().toLowerCase() + "\",\"");
					}

					pw.print(ada.get("crs") + "\",\"" + ada.get("pci") + "\",\"" + ada.get("tac") + "\",\"\",\"" + "0"
							+ "\",\"" + "false" + "\",\"" + ada.get("zczc") + "\",\"" + ada.get("rach") + "\",\""
							+ ada.get("pracformat") + "\",\"" + "off" + "\",\"");
					pw.print(ada.get("emtc") + "\",\"" + "subframe-assignment-sa2" + "\",\""
							+ "special-subframe-pattern-ssp7\",\"" + "-" + "\",\"" + ada.get("power") + "\",\""
							+ "edge-mode" + "\",\"" + "0" + "\",\"\",\"" + ada.get("PreferredEarfcn") + "\",\""
							+ "update-mode1" + "\",\"" + "spectrum-sharing-off\",\"" + "0\",\""
							+ "frame-level-dss-mode\",\"" + "non-blanking\",\"" + "0\",\"" + "0\",\"\",\"\",\""
							+ "0\",\"" + "true\",\"" + "N 000:00:00.000\",\"" + "E 000:00:00.000\",\"0.00m\"\n");

				}
				if (ada.containsKey("LCCnum") && StringUtils.isNotEmpty(ada.get("LCCnum").toString())
						&& Integer.parseInt(ada.get("LCCnum").toString()) > lcccnt) {
					lcccnt = Integer.parseInt(ada.get("LCCnum").toString());
				}
				

				pw.print("\"@NB_IOT_CELL_INFORMATION\"\n");
				pw.print(
						"\"State\",\"Cell Num\",\"Parent Cell Number\",\"NB IoT PCI\",\"Operation Mode Info\",\"NB IoT TAC\",\"Use Parent PCI for Guard-band\",\"Initial Nprach\",");
				pw.print(
						"\"Nprach Start Time CL0\",\"Nprach Subcarrier Offset CL0\",\"Nprach Start Time CL1\",\"Nprach Subcarrier Offset CL1\",\"Nprach Start Time CL2\",");
				pw.print(
						"\"Nprach Subcarrier Offset CL2\",\"Guard Band\",\"Avoid UL Interfering\",\"DL RB\",\"UL RB\"\n");

				for (int i = 0; i < cAddDataAref.size(); i++) {
					ada = cAddDataAref.get(i);
					String emtc = ada.get("emtc").toString();
					int nb = Integer.parseInt(ada.get("nbiot").toString());
					int iotT = Integer.parseInt(ada.get("iottac").toString());
					String bandW = ada.get("bandwidth").toString();
					if (nb > 0) {
						if (iotT != 0 && iotT > 0) {
							// if(emtc.equals("enable")) {
							if (bandW.equals("system-bandwidth-n25")) {
								pw.print("\"ADD\",\"" + ada.get("cellid") + "\",\"" + ada.get("cellid") + "\",\""
										+ ada.get("pci") + "\",\"" + "in-band-same-pci\",\"" + ada.get("iottac")
										+ "\",\"on\",\"" + "on\",\"" + "nprach-start-time-ms8\",\""
										+ "nprach-subcarrier-offset-n36\",\"");
								pw.print("nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\""
										+ "nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\""
										+ "right\",\"" + "false\",\"" + "17\",\"" + "19\"\n");
							} else {
								pw.print("\"ADD\",\"" + ada.get("cellid") + "\",\"" + ada.get("cellid") + "\",\""
										+ ada.get("pci") + "\",\"" + "guard-band\",\"" + ada.get("iottac")
										+ "\",\"on\",\"" + "on\",\"" + "nprach-start-time-ms8\",\""
										+ "nprach-subcarrier-offset-n36\",\"");
								pw.print("nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\""
										+ "nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\""
										+ "right\",\"" + "false\",\"" + "45\",\"" + "40\"\n");
							}

						} else {
							pw.print("\"NONE\",\"" + ada.get("cellid") + "\",\"" + ada.get("cellid") + "\",\""
									+ ada.get("pci") + "\",\"guard-band\",\"" + "0\",\"" + "off\",\"" + "off\",\""
									+ "nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\"");
							pw.print("nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\""
									+ "nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\"" + "right\",\""
									+ "false\",\"" + "45\",\"" + "40\"\n");
						}

					} else {
						pw.print("\"NONE\",\"" + ada.get("cellid") + "\",\"" + ada.get("cellid") + "\",\""
								+ ada.get("pci") + "\",\"guard-band\",\"" + "0\",\"" + "off\",\"" + "off\",\""
								+ "nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\"");
						pw.print("nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\""
								+ "nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\"" + "right\",\""
								+ "false\",\"" + "45\",\"" + "40\"\n");
					}
				}
				ArrayList<String> addCpri2 = new ArrayList<String>();
				pw.print("\"@NON_ANCHOR_NB_IOT_CELL_INFORMATION\"\n");
				pw.print("\"State\",\"Cell Num\",\"Operation Mode Info\",\"Guard Band\",\"DL RB\",\"UL RB\"\n");

				for (int i = 0; i < rrhDataAref.size(); i++) {
					if (!addCpri2.contains(rrhDataAref.get(i).get("cpristr").toString())) {
						pw.print("\"NONE\",\"\",\"\",\"\",\"\",\"\"\n");
						addCpri2.add((String) rrhDataAref.get(i).get("cpristr"));
					}
				}

				pw.print("\"@CHANNEL_BOARD_INFORMATION\"\n");
				pw.print("\"State\",\"Unit Type\",\"Unit ID\",\"Board Type\"\n");

				// for comparison
				if (orgData.containsKey("LCCnum") && StringUtils.isNotEmpty(orgData.get("LCCnum").toString())
						&& Integer.parseInt(orgData.get("LCCnum").toString()) > lccnt_add) {
					lccnt_add = Integer.parseInt(orgData.get("LCCnum").toString());
					System.out.println("lccnt_add " + lccnt_add);
				}
				if (lcccnt > lccnt_add) {
					lccnt_add = lccnt_add + 1;

					for (int i = lccnt_add; i <= lcccnt; i++) {

						pw.print("\"ADD\",\"ecp\",\"" + i + "\",\"lcc4-b1\"\n");

					}
				}
				///////////////////// @cpri port information/////////////////

				// pw.print("\"@CPRI_PORT_INFORMATION\"\n");
				// pw.print("\"State\",\"Unit Type\",\"Unit ID\",\"Port ID\",\"Connection
				// Type\",\"FSU Inter Node ID\",\"FSU DU CPRI Port ID\"\n");

				pw.print("\"@CPRI_PORT_INFORMATION\"\n");
				pw.print(
						"\"State\",\"Unit Type\",\"Unit ID\",\"Port ID\",\"CPRI Compression\",\"Connection Type\",\"FSU Inter Node ID\",\"FSU DU CPRI Port ID\"\n");

				ArrayList<String> cprival = new ArrayList<String>();
				ArrayList<String> cprivalAdd = new ArrayList<String>();
				TreeSet<String> cpri = new TreeSet<String>();
				TreeSet<String> cpriAdd = new TreeSet<String>();
				TreeSet<String> ruPortCpri = new TreeSet<String>();

				for (int i = 0; i < cDataAref.size(); i++) {
					orgData = cDataAref.get(i);
					cpriAdd.add((String) orgData.get("cpristr"));

					if (orgData.get("RUPortID").equals("1")) {
						ruPortCpri.add((String) orgData.get("cpristr"));
					}
				}
				for (int i = 0; i < cAddDataAref.size(); i++) {
					ada = cAddDataAref.get(i);
					cpri.add((String) ada.get("cpristr"));
				}

				cpri.removeAll(cpriAdd);

				for (int i = 0; i < cAddDataAref.size(); i++) {

					ada = cAddDataAref.get(i);

					if (cpri.contains(ada.get("cpristr")) && !cprivalAdd.contains(ada.get("cpristr").toString())) {
						pw.print("\"ADD\",\"ecp\",\"" + ada.get("LCCnum") + "\",\"" + ada.get("port") + "\",\"\",\""
								+ "direct" + "\",\"" + "\",\"\"\n");
						cprivalAdd.add((String) ada.get("cpristr"));
					}
					if ((ada.get("RUPortID").toString()).equals("1")
							&& !cprival.contains(ada.get("cpristr").toString())) {
						if (!ruPortCpri.contains(ada.get("cpristr").toString())) {

							pw.print("\"ADD\",\"ecp\",\"" + ada.get("LCCnum") + "\",\"" + ada.get("additional_port")
									+ "\",\"\",\"" + "direct" + "\",\"" + "\",\"\"\n");
						}
						cprival.add((String) ada.get("cpristr"));
					}
				}
				pw.print("\"@RU_INFORMATION\"\n");
				pw.print(
						"\"State\",\"RU Conf\",\"RU Port\",\"Connected DU Board Type\",\"RU Type\",\"Start Earfcn1\",\"Start Earfcn2\",\"Serial Number\",\"Azimuth\",\"Beamwidth\",\"Bisector Mode\",");
				pw.print(
						"\"X Pole Antenna\",\"Antenna Gain dBi\",\"Cable Loss\",\"Accuracy Margin dB\",\"Sharing Flag\",\"Primary Flag\"\n");

				ArrayList<String> addCpri1 = new ArrayList<String>();
				TreeSet<String> cpriA = new TreeSet<String>();
				TreeSet<String> codeA = new TreeSet<String>();
				TreeMap<String, Object> tmA = new TreeMap<>();

				for (int i = 0; i < rrhDataAref.size(); i++) {
					tmA = rrhDataAref.get(i);
					cpriA.add((String) tmA.get("cpristr"));
					codeA.add((String) tmA.get("code"));
				}

				for (int i = 0; i < rrhDataBref.size(); i++) {
					rda = rrhDataBref.get(i);

					if (rda.get("code").toString().equals("rt4401-480")) {

						((ArrayList<String>) rda.get("startearfcnA")).set(0, "55240");
						((ArrayList<String>) rda.get("startearfcnA")).set(1, "2400");
					}
					if (rda.get("code").toString().equals("rt2201-460")) {

						((ArrayList<String>) rda.get("startearfcnA")).set(0, "46790");
						((ArrayList<String>) rda.get("startearfcnA")).set(1, "2400");
					}
					if (rda.get("code").toString().equals("rfv01u-d20")) {

						((ArrayList<String>) rda.get("startearfcnA")).set(0, "5180");
						((ArrayList<String>) rda.get("startearfcnA")).set(1, "2400");
					}
					if (rda.get("code").toString().equals("rfv01u-d10")
							|| rda.get("code").toString().equals("rf4402d-d10")) {

						((ArrayList<String>) rda.get("startearfcnA")).set(0, "1950");
						((ArrayList<String>) rda.get("startearfcnA")).set(1, "600");
					}
					// ORAN radios
					if (rda.get("code").toString().equals("rf4439d-250")) {

						((ArrayList<String>) rda.get("startearfcnA")).set(0, "1950");
						((ArrayList<String>) rda.get("startearfcnA")).set(1, "600");
					}

					if (!addCpri1.contains(rda.get("cpristr").toString())) {
						if (cpriA.contains(rda.get("cpristr").toString())) {

							if (!codeA.contains(rda.get("code").toString())) {
								pw.print("\"ADD" + "\",\"" + rda.get("cpristr") + "\",\"" + "0" + "\",\"" + "ecp"
										+ "\",\"" + rda.get("code") + "\",\""
										+ ((ArrayList<String>) rda.get("startearfcnA")).get(0) + "\",\""
										+ ((ArrayList<String>) rda.get("startearfcnA")).get(1) + "\",\"" + "\",\""
										+ "-1" + "\",\"" + "700\",\"bisector-mode-off\",\"");
								pw.print("true" + "\",\"" + ((ArrayList<String>) rda.get("antennaGains")).get(0)
										+ "\",\"" + "0" + "\",\"" + "10\",\"\",\"\"\n");
								addCpri1.add((String) rda.get("cpristr"));
							}

						} else {
							pw.print("\"ADD" + "\",\"" + rda.get("cpristr") + "\",\"" + "0" + "\",\"" + "ecp" + "\",\""
									+ rda.get("code") + "\",\"" + ((ArrayList<String>) rda.get("startearfcnA")).get(0)
									+ "\",\"" + ((ArrayList<String>) rda.get("startearfcnA")).get(1) + "\",\"" + "\",\""
									+ "-1" + "\",\"" + "700\",\"bisector-mode-off\",\"");
							pw.print("true" + "\",\"" + ((ArrayList<String>) rda.get("antennaGains")).get(0) + "\",\""
									+ "0" + "\",\"" + "10\",\"\",\"\"\n");
							addCpri1.add((String) rda.get("cpristr"));
						}
					}
				}

				ArrayList<String> addCpri = new ArrayList<String>();
				pw.print("\"@ADDITIONAL_CPRI_INFORMATION\"\n");
				pw.print(
						"\"State\",\"RU Conf\",\"Connected DU Board Type\",\"Additional Board ID\",\"Additional Port ID\",\"RU Additional Port ID\"\n");
				TreeSet<String> addC = new TreeSet<String>();
				TreeSet<String> addAddPort = new TreeSet<String>();
				for (int i = 0; i < cDataAref.size(); i++) {
					orgData = cDataAref.get(i);
					if ((orgData.get("RUPortID").toString()).equals("1")) {
						addC.add((String) orgData.get("cpristr"));
						addAddPort.add((String) orgData.get("additional_port"));
					}
					// if (!addCpri.contains(ada.get("cpristr").toString())) {
					// pw.print("\"ADD" + "\",\"" + ada.get("cpristr") + "\",\"" + "ecp" + "\",\"" +
					// ada.get("LCCnum")
					// + "\",\"" + ada.get("additional_port") + "\",\"" + ada.get("RUPortID") +
					// "\"\n");
					// addCpri.add((String) ada.get("cpristr"));
					// }
				}

				// for carrier add
				for (int i = 0; i < cAddDataAref.size(); i++) {
					ada = cAddDataAref.get(i);
					if ((ada.get("RUPortID").toString()).equals("1")) {
						if (!addC.contains(ada.get("cpristr")) && !addAddPort.contains(ada.get("additional_port"))) {
							if (!addCpri.contains(ada.get("cpristr").toString())) {
								pw.print("\"ADD" + "\",\"" + ada.get("cpristr") + "\",\"" + "ecp" + "\",\""
										+ ada.get("LCCnum") + "\",\"" + ada.get("additional_port") + "\",\""
										+ ada.get("RUPortID") + "\"\n");
								addCpri.add((String) ada.get("cpristr"));
							}
						}
					}
				}
				// duplicate issue
				ArrayList<String> addCpri81 = new ArrayList<String>();
				ArrayList<TreeMap<String, Object>> deleteData = new ArrayList<>();
				for (TreeMap<String, Object> tdData : rrhDataBref) {
					if (tdData.get("bandpcs").toString().contains("AWS") && addCpri81.contains(tdData.get("cpristr"))) {
						deleteData.add(tdData);
					}
					if ((tdData.get("bandpcs")).toString().contains("AWS")) {
						addCpri81.add((String) tdData.get("cpristr"));
					}
				}

				for (TreeMap<String, Object> tdData : deleteData) {
					rrhDataBref.remove(tdData);
				}
				ArrayList<String> addCpri82 = new ArrayList<String>();
				ArrayList<TreeMap<String, Object>> deleteData1 = new ArrayList<>();
				for (TreeMap<String, Object> tdData : rrhDataBref) {
					if (tdData.get("bandpcs").toString().contains("AWS") && addCpri81.contains(tdData.get("cpristr"))) {
						deleteData1.add(tdData);
					}
					if ((tdData.get("bandpcs")).toString().contains("AWS")) {
						addCpri82.add((String) tdData.get("cpristr"));
					}
				}

				// duplicate for PCS-1 and PCS-2
				ArrayList<String> addCpri83 = new ArrayList<String>();
				ArrayList<TreeMap<String, Object>> deleteData2 = new ArrayList<>();
				for (TreeMap<String, Object> tdData : rrhDataBref) {
					if (tdData.get("bandpcs").toString().contains("PCS") && addCpri83.contains(tdData.get("cpristr"))) {
						deleteData2.add(tdData);
					}
					if ((tdData.get("bandpcs")).toString().contains("PCS")) {
						addCpri83.add((String) tdData.get("cpristr"));
					}
				}

				for (TreeMap<String, Object> tdData : deleteData2) {
					rrhDataBref.remove(tdData);
				}
				ArrayList<String> addCpri84 = new ArrayList<String>();
				ArrayList<TreeMap<String, Object>> deleteData3 = new ArrayList<>();
				for (TreeMap<String, Object> tdData : rrhDataBref) {
					if (tdData.get("bandpcs").toString().contains("PCS") && addCpri83.contains(tdData.get("cpristr"))) {
						deleteData3.add(tdData);
					}
					if ((tdData.get("bandpcs")).toString().contains("PCS")) {
						addCpri84.add((String) tdData.get("cpristr"));
					}
				}

				int a1 = 0;
				int a2 = 0;
				int a3 = 0;
				int a4 = 0;
				int a5 = 0;
				int a6 = 0;

				pw.print("\"@RU_ANTENNA_PORT_INFORMATION\"\n");
				pw.print(
						"\"State\",\"RU Conf\",\"Connected DU Board Type\",\"Antenna Port ID\",\"External Antenna Port Tx Delay\",\"External Antenna Port Rx Delay\"\n");
				Map<String, Object> ruMap = new TreeMap<String, Object>();
				Map<String, TreeSet<Integer>> ruMapA = new TreeMap<String, TreeSet<Integer>>();
				TreeSet<Integer> ruPort = new TreeSet<Integer>();
				TreeSet<String> ruCpriSet = new TreeSet<String>();
				TreeSet<Integer> trset = new TreeSet<Integer>();

				ArrayList<String> addCpri22 = new ArrayList<String>();
				ArrayList<String> addCpri33 = new ArrayList<String>();
				ArrayList<String> addCpri44 = new ArrayList<String>();

				for (int i = 0; i < rrhDataAref.size(); i++) {
					ruMap = rrhDataAref.get(i);
					ruCpriSet.add(ruMap.get("cpristr").toString());

					for (int j = 0; j < ((int[]) rrhDataAref.get(i).get("antennaPortMapA")).length; j++) {
						if (((int[]) rrhDataAref.get(i).get("antennaPortMapA"))[j] != -1) {
							int n = j + 1;
							ruPort.add(n);
						}
					}
					ruMapA.put(ruMap.get("cpristr").toString(), ruPort);
				}

				for (int i = 0; i < rrhDataBref.size(); i++) {

					if (rrhDataBref.get(i).get("bandpcs").toString().contains("PCS")
							&& rrhDataBref.get(i).get("txd").toString().contains("2")) {
						rrhDataBref.get(i).replace("antennaPortMapA", new int[] { -1, -1, -1, -1, 1, 1, -1, -1 });
					} else if (rrhDataBref.get(i).get("bandpcs").toString().contains("PCS")
							&& rrhDataBref.get(i).get("txd").toString().contains("4")) {
						rrhDataBref.get(i).replace("antennaPortMapA", new int[] { -1, -1, -1, -1, 1, 1, 1, 1 });
					} else if (rrhDataBref.get(i).get("bandpcs").toString().contains("AWS")
							&& rrhDataBref.get(i).get("txd").toString().contains("4")) {
						rrhDataBref.get(i).replace("antennaPortMapA", new int[] { 1, 1, 1, 1, -1, -1, -1, -1 });
					} else if (rrhDataBref.get(i).get("bandpcs").toString().contains("AWS")
							&& rrhDataBref.get(i).get("txd").toString().contains("2")) {
						rrhDataBref.get(i).replace("antennaPortMapA", new int[] { 1, 1, -1, -1, -1, -1, -1, -1 });
					}

					// if (!addCpri22.contains(rrhDataBref.get(i).get("cpristr").toString()) &&
					// !addCpri33.contains(rrhDataBref.get(i).get("cpristr").toString())
					// && !addCpri44.contains(rrhDataBref.get(i).get("cpristr").toString())) {
					for (int j = 0; j < ((int[]) rrhDataBref.get(i).get("antennaPortMapA")).length; j++) {

						if (((int[]) rrhDataBref.get(i).get("antennaPortMapA"))[j] != -1) {
							int antport = j + 1;
							if ((rrhDataBref.get(i).get("cpristr").toString()).equals("2_6_0")) {
								a1++;
								if (a1 < 5) {

									if (ruCpriSet.contains(rrhDataBref.get(i).get("cpristr"))) {

										trset = (TreeSet<Integer>) ruMapA
												.get(rrhDataBref.get(i).get("cpristr").toString());

										if (!trset.contains(antport)) {
											pw.print("\"ADD" + "\",\"" + rrhDataBref.get(i).get("cpristr") + "\",\""
													+ "ecp" + "\",\"" + antport + "\",\"");
											pw.print(Math
													.round((Float.parseFloat(
															rrhDataBref.get(i).get("delaydl").toString().trim())))
													+ "\",\""
													+ Math.round(Float.parseFloat(
															rrhDataBref.get(i).get("delayul").toString().trim()))
													+ "\"\n");
										}
									} else {
										pw.print("\"ADD" + "\",\"" + rrhDataBref.get(i).get("cpristr") + "\",\"" + "ecp"
												+ "\",\"" + antport + "\",\"");
										pw.print(Math.round(
												(Float.parseFloat(rrhDataBref.get(i).get("delaydl").toString().trim())))
												+ "\",\"" + Math.round(Float.parseFloat(
														rrhDataBref.get(i).get("delayul").toString().trim()))
												+ "\"\n");
									}
								}
							} else if ((rrhDataBref.get(i).get("cpristr").toString()).equals("1_6_0")) {
								a4++;
								if (a4 < 5) {

									if (ruCpriSet.contains(rrhDataBref.get(i).get("cpristr"))) {

										trset = (TreeSet<Integer>) ruMapA
												.get(rrhDataBref.get(i).get("cpristr").toString());

										if (!trset.contains(antport)) {
											pw.print("\"ADD" + "\",\"" + rrhDataBref.get(i).get("cpristr") + "\",\""
													+ "ecp" + "\",\"" + antport + "\",\"");
											pw.print(Math
													.round((Float.parseFloat(
															rrhDataBref.get(i).get("delaydl").toString().trim())))
													+ "\",\""
													+ Math.round(Float.parseFloat(
															rrhDataBref.get(i).get("delayul").toString().trim()))
													+ "\"\n");
										}
									}

									else {
										pw.print("\"ADD" + "\",\"" + rrhDataBref.get(i).get("cpristr") + "\",\"" + "ecp"
												+ "\",\"" + antport + "\",\"");
										pw.print(Math.round(
												(Float.parseFloat(rrhDataBref.get(i).get("delaydl").toString().trim())))
												+ "\",\"" + Math.round(Float.parseFloat(
														rrhDataBref.get(i).get("delayul").toString().trim()))
												+ "\"\n");
									}
								}
							} else if ((rrhDataBref.get(i).get("cpristr").toString()).contains("2_8_0")) {
								a2++;
								if (a2 < 5) {

									if (ruCpriSet.contains(rrhDataBref.get(i).get("cpristr"))) {

										trset = (TreeSet<Integer>) ruMapA
												.get(rrhDataBref.get(i).get("cpristr").toString());

										if (!trset.contains(antport)) {
											pw.print("\"ADD" + "\",\"" + rrhDataBref.get(i).get("cpristr") + "\",\""
													+ "ecp" + "\",\"" + antport + "\",\"");
											pw.print(Math
													.round((Float.parseFloat(
															rrhDataBref.get(i).get("delaydl").toString().trim())))
													+ "\",\""
													+ Math.round(Float.parseFloat(
															rrhDataBref.get(i).get("delayul").toString().trim()))
													+ "\"\n");
										}
									}

									else {
										pw.print("\"ADD" + "\",\"" + rrhDataBref.get(i).get("cpristr") + "\",\"" + "ecp"
												+ "\",\"" + antport + "\",\"");
										pw.print(Math.round(
												(Float.parseFloat(rrhDataBref.get(i).get("delaydl").toString().trim())))
												+ "\",\"" + Math.round(Float.parseFloat(
														rrhDataBref.get(i).get("delayul").toString().trim()))
												+ "\"\n");
									}
								}

							} else if ((rrhDataBref.get(i).get("cpristr").toString()).contains("1_8_0")) {
								a5++;
								if (a5 < 5) {

									if (ruCpriSet.contains(rrhDataBref.get(i).get("cpristr"))) {

										trset = (TreeSet<Integer>) ruMapA
												.get(rrhDataBref.get(i).get("cpristr").toString());

										if (!trset.contains(antport)) {
											pw.print("\"ADD" + "\",\"" + rrhDataBref.get(i).get("cpristr") + "\",\""
													+ "ecp" + "\",\"" + antport + "\",\"");
											pw.print(Math
													.round((Float.parseFloat(
															rrhDataBref.get(i).get("delaydl").toString().trim())))
													+ "\",\""
													+ Math.round(Float.parseFloat(
															rrhDataBref.get(i).get("delayul").toString().trim()))
													+ "\"\n");
										}
									} else {
										pw.print("\"ADD" + "\",\"" + rrhDataBref.get(i).get("cpristr") + "\",\"" + "ecp"
												+ "\",\"" + antport + "\",\"");
										pw.print(Math.round(
												(Float.parseFloat(rrhDataBref.get(i).get("delaydl").toString().trim())))
												+ "\",\"" + Math.round(Float.parseFloat(
														rrhDataBref.get(i).get("delayul").toString().trim()))
												+ "\"\n");
									}
								}
							} else if ((rrhDataBref.get(i).get("cpristr").toString()).contains("2_10_0")) {
								a6++;
								if (a6 < 5) {

									if (ruCpriSet.contains(rrhDataBref.get(i).get("cpristr"))) {

										trset = (TreeSet<Integer>) ruMapA
												.get(rrhDataBref.get(i).get("cpristr").toString());

										if (!trset.contains(antport)) {
											pw.print("\"ADD" + "\",\"" + rrhDataBref.get(i).get("cpristr") + "\",\""
													+ "ecp" + "\",\"" + antport + "\",\"");
											pw.print(Math
													.round((Float.parseFloat(
															rrhDataBref.get(i).get("delaydl").toString().trim())))
													+ "\",\""
													+ Math.round(Float.parseFloat(
															rrhDataBref.get(i).get("delayul").toString().trim()))
													+ "\"\n");
										}
									} else {
										pw.print("\"ADD" + "\",\"" + rrhDataBref.get(i).get("cpristr") + "\",\"" + "ecp"
												+ "\",\"" + antport + "\",\"");
										pw.print(Math.round(
												(Float.parseFloat(rrhDataBref.get(i).get("delaydl").toString().trim())))
												+ "\",\"" + Math.round(Float.parseFloat(
														rrhDataBref.get(i).get("delayul").toString().trim()))
												+ "\"\n");
									}
								}
							} else if ((rrhDataBref.get(i).get("cpristr").toString()).contains("1_10_0")) {
								a3++;
								if (a3 < 5) {

									if (ruCpriSet.contains(rrhDataBref.get(i).get("cpristr"))) {

										trset = (TreeSet<Integer>) ruMapA
												.get(rrhDataBref.get(i).get("cpristr").toString());

										if (!trset.contains(antport)) {
											pw.print("\"ADD" + "\",\"" + rrhDataBref.get(i).get("cpristr") + "\",\""
													+ "ecp" + "\",\"" + antport + "\",\"");
											pw.print(Math
													.round((Float.parseFloat(
															rrhDataBref.get(i).get("delaydl").toString().trim())))
													+ "\",\""
													+ Math.round(Float.parseFloat(
															rrhDataBref.get(i).get("delayul").toString().trim()))
													+ "\"\n");
										}
									}

									else {
										pw.print("\"ADD" + "\",\"" + rrhDataBref.get(i).get("cpristr") + "\",\"" + "ecp"
												+ "\",\"" + antport + "\",\"");
										pw.print(Math.round(
												(Float.parseFloat(rrhDataBref.get(i).get("delaydl").toString().trim())))
												+ "\",\"" + Math.round(Float.parseFloat(
														rrhDataBref.get(i).get("delayul").toString().trim()))
												+ "\"\n");
									}
								}
							} else {

								if (ruCpriSet.contains(rrhDataBref.get(i).get("cpristr"))) {

									trset = (TreeSet<Integer>) ruMapA.get(rrhDataBref.get(i).get("cpristr").toString());

									if (!trset.contains(antport)) {
										pw.print("\"ADD" + "\",\"" + rrhDataBref.get(i).get("cpristr") + "\",\"" + "ecp"
												+ "\",\"" + antport + "\",\"");
										pw.print(Math.round(
												(Float.parseFloat(rrhDataBref.get(i).get("delaydl").toString().trim())))
												+ "\",\"" + Math.round(Float.parseFloat(
														rrhDataBref.get(i).get("delayul").toString().trim()))
												+ "\"\n");
									}
								} else {
									pw.print("\"ADD" + "\",\"" + rrhDataBref.get(i).get("cpristr") + "\",\"" + "ecp"
											+ "\",\"" + antport + "\",\"");
									pw.print(Math.round(
											(Float.parseFloat(rrhDataBref.get(i).get("delaydl").toString().trim())))
											+ "\",\""
											+ Math.round(Float
													.parseFloat(rrhDataBref.get(i).get("delayul").toString().trim()))
											+ "\"\n");
								}
							}
						}

					}

					// }
					//
					// if((rrhDataBref.get(i).get("bandpcs")).toString().contains("PCS")) {
					// addCpri22.add((String)rrhDataBref.get(i).get("cpristr"));
					// }
					// if((rrhDataBref.get(i).get("bandpcs")).toString().contains("AWS") ||
					// (rrhDataBref.get(i).get("bandpcs")).toString().contains("CBRS")
					// || (rrhDataBref.get(i).get("bandpcs")).toString().contains("LAA")) {
					// addCpri33.add((String)rrhDataBref.get(i).get("cpristr"));
					// }
					// if((rrhDataBref.get(i).get("bandpcs")).toString().contains("850") ||
					// (rrhDataBref.get(i).get("bandpcs").toString().contains("700"))) {
					// addCpri44.add((String)rrhDataBref.get(i).get("cpristr"));
					// }

				}

				pw.print("\"@DSP_INFORMATION\"\n");
				pw.print("\"State\",\"Unit Type\",\"Unit ID\",\"DSP ID\",\"Optic Distance\"\n");

				ArrayList<Integer> cardsProvisioned = new ArrayList<Integer>();
				TreeSet<String> var = new TreeSet<>();
				TreeSet<String> varAdd = new TreeSet<>();
				System.out.println("Enter");
				int m, n = 0;
				for (int ci = 0; ci < cAddDataAref.size(); ci++) {

					m = Integer.parseInt(cAddDataAref.get(ci).get("LCCnum").toString());
					if (m >= n) {
						n = m;
						varAdd.add(cAddDataAref.get(ci).get("LCCnum").toString());
					}
					continue;
				}

				// checking without carrier add
				for (int ci = 0; ci < cDataAref.size(); ci++) {
					if (Integer.parseInt(cDataAref.get(ci).get("LCCnum").toString()) == cardsProvisioned.size()) {
						cardsProvisioned.add(cardsProvisioned.size());
						var.add(cDataAref.get(ci).get("LCCnum").toString());
						continue;
					}
				}

				varAdd.removeAll(var);
				String lowest = varAdd.first();
				String highest = varAdd.last();

				if (!varAdd.isEmpty()) {
					if (highest.equals(lowest)) {

						for (int li = Integer.parseInt(lowest); li <= varAdd.size(); li++) {
							for (int di = 0; di < lccAref.get(li).size(); di++) {
								if ((Integer.parseInt(lccAref.get(li).get(di).get("opticDistance").toString())) == 0) {
									pw.print("\"ADD\",\"ecp\",\"" + li + "\",\"" + di + "\",\"20-km\"\n");
								} else {
									pw.print("\"ADD\",\"ecp\",\"" + li + "\",\"" + di + "\",\""
											+ lccAref.get(li).get(di).get("opticDistance") + "-km\"\n");
								}
							}

						}
					} else {

						for (int li = Integer.parseInt(lowest); li < varAdd.size(); li++) {
							for (int di = 0; di < lccAref.get(li).size(); di++) {
								if ((Integer.parseInt(lccAref.get(li).get(di).get("opticDistance").toString())) == 0) {
									pw.print("\"ADD\",\"ecp\",\"" + li + "\",\"" + di + "\",\"20-km\"\n");
								} else {
									pw.print("\"ADD\",\"ecp\",\"" + li + "\",\"" + di + "\",\""
											+ lccAref.get(li).get(di).get("opticDistance") + "-km\"\n");
								}
							}

						}
					}
				}

				System.out.println("Size 2 : " + cardsProvisioned.size());

				////////////////////// @CBRS_CHANNEL_INFORMATION////////////////////////////
				pw.print("@CBRS_CHANNEL_INFORMATION\n");
				pw.print("State,Sector ID,Carrier ID,Block Listed Channel\n");
				pw.println("NONE" + "," + "" + "," + "" + "," + "");

			} else if (supportCA.equals("false")) {

				AdNew = false;

				for (int i = 0; i < addAref.size(); i++) {
					fda = addAref.get(i);
					String path = "";

					if (fda.get("band").toString().contains("700") && fda.get("txd").equals("4")) {
						path = "select-abcd";
					} else if (fda.get("band").toString().contains("700") && fda.get("txd").equals("2")) {
						path = "select-ab";
					} else if (fda.get("band").toString().contains("850") && fda.get("txd").equals("4")) {
						path = "select-efgh";
					} else if (fda.get("band").toString().contains("850") && fda.get("txd").equals("2")) {
						if (fda.get("rrh").toString().contains("RIU")) {// new riu
							path = "select-cd";
						} else {
							path = "select-ef";
						}
					} else if (fda.get("band").toString().contains("AWS") && fda.get("txd").equals("4")) {
						path = "select-abcd";
					} else if (fda.get("band").toString().contains("AWS") && fda.get("txd").equals("2")) {
						path = "select-ab";
					} else if (fda.get("band").toString().contains("PCS") && fda.get("txd").equals("4")) {
						path = "select-efgh";
					} else if (fda.get("band").toString().contains("PCS") && fda.get("txd").equals("2")) {
						if (fda.get("rrh").toString().contains("RIU")) {// new riu
							path = "select-cd";
						} else {
							path = "select-ef";
						}
					} else if (fda.get("band").equals("CBRS") && fda.get("txd").equals("4")) {
						path = "select-abcd";
					} else if (fda.get("band").equals("LAA") && fda.get("txd").equals("4")) {
						path = "select-abcd";
					} else if (fda.get("band").equals("LAA") && fda.get("txd").equals("2")) {
						path = "select-ab";
					} // riu port
					else if (fda.get("band").toString().contains("AWS") && fda.get("txd").equals("1")) {
						path = "select-a";
					} else if (fda.get("band").toString().contains("700") && fda.get("txd").equals("1")) {
						path = "select-a";
					} else if (fda.get("band").toString().contains("PCS") && fda.get("txd").equals("1")) {
						path = "select-c";
					} else if (fda.get("band").toString().contains("850") && fda.get("txd").equals("1")) {
						path = "select-c";
					}

					System.out.println("Path value::::" + path);

					if (Integer.parseInt(fda.get("earfcndl").toString()) >= 5180
							&& Integer.parseInt(fda.get("earfcndl").toString()) <= 5279) {
						fda.replace("band", "700mhz");
					} else if (fda.get("band").equals("CBRS")
							|| (Integer.parseInt(fda.get("earfcndl").toString()) >= 55240
									&& Integer.parseInt(fda.get("earfcndl").toString()) <= 56739)) {
						fda.replace("band", "3500mhz");
						fda.replace("earfcndl", "0");
						fda.replace("earfcnul", "0");

					} else if ((Integer.parseInt(fda.get("earfcndl").toString()) >= 1950
							&& Integer.parseInt(fda.get("earfcndl").toString()) <= 2399)) {
						fda.replace("band", "2100mhz_band4");
					} else if (Integer.parseInt(fda.get("earfcndl").toString()) >= 600
							&& Integer.parseInt(fda.get("earfcndl").toString()) <= 1199) {
						fda.replace("band", "1900mhz");

					} else if (Integer.parseInt(fda.get("earfcndl").toString()) >= 2400
							&& Integer.parseInt(fda.get("earfcndl").toString()) <= 2649) {
						fda.replace("band", "850mhz");
					} else if ((Integer.parseInt(fda.get("earfcndl").toString()) >= 66436
							&& Integer.parseInt(fda.get("earfcndl").toString()) <= 67335)) {
						if (fda.get("DspCellIndex").equals("4") || fda.get("DspCellIndex").equals("5")) {
							fda.replace("typename", "cfg61-multi-carrier-10m-5m-6cell");
						}

						fda.replace("band", "2100mhz_band66");
					} else if (fda.get("band").equals("LAA")
							|| (Integer.parseInt(fda.get("earfcndl").toString()) >= 46790
									&& Integer.parseInt(fda.get("earfcndl").toString()) <= 54539)) {
						fda.replace("band", "5000mhz");
						fda.replace("earfcndl", "0");
						fda.replace("earfcnul", "0");
					} else if ((Integer.parseInt(fda.get("earfcndl").toString()) >= 8040
							&& Integer.parseInt(fda.get("earfcndl").toString()) <= 8689)) {
						fda.replace("band", "1900mhz_band25");
					}
					if (fda.get("bandwidth").equals("10")) {
						fda.replace("bandwidth", "system-bandwidth-n50");
					} else if (fda.get("bandwidth").equals("20")) {
						fda.replace("bandwidth", "system-bandwidth-n100");

					} else if (fda.get("bandwidth").equals("15")) {
						fda.replace("bandwidth", "system-bandwidth-n75");
					} else if (fda.get("bandwidth").equals("5")) {
						fda.replace("bandwidth", "system-bandwidth-n25");
						;
					}

					pw.print("\"ADD" + "\",\"" + fda.get("sectid") + "\",\"" + fda.get("carrid") + "\",\""
							+ fda.get("LCCnum") + "\",\"" + fda.get("DspID") + "\",\"" + fda.get("DspCellIndex")
							+ "\",\"" + fda.get("cpristr") + "\",\"");
					pw.print(fda.get("RUPortID") + "\",\"");

					pw.print(fda.get("band"));

					if (fda.get("txd").equals("2") && fda.get("rxd").equals("2")) {
						pw.print("\",\"" + fda.get("earfcndl") + "\",\"" + fda.get("earfcnul") + "\",\""
								+ fda.get("bandwidth") + "\",\"" + "n2-tx-antenna-count\",\"n2-rx-antenna-count"
								+ "\",\"" + path + "\",\"" + fda.get("mct").toString().toLowerCase() + "\",\"");
					} else if (fda.get("txd").equals("2") && fda.get("rxd").equals("4")) {
						pw.print("\",\"" + fda.get("earfcndl") + "\",\"" + fda.get("earfcnul") + "\",\""
								+ fda.get("bandwidth") + "\",\"" + "n2-tx-antenna-count\",\"n4-rx-antenna-count"
								+ "\",\"" + path + "\",\"" + fda.get("mct").toString().toLowerCase() + "\",\"");
					} else if (fda.get("txd").equals("'0,bs4") && fda.get("rxd").equals("2")) {
						pw.print("\",\"" + fda.get("earfcndl") + "\",\"" + fda.get("earfcnul") + "\",\""
								+ fda.get("bandwidth") + "\",\"" + "n4-tx-antenna-count\",\"n2-rx-antenna-count"
								+ "\",\"" + path + "\",\"" + fda.get("mct").toString().toLowerCase() + "\",\"");
					} else if (fda.get("txd").equals("4") && fda.get("rxd").equals("4")) {
						pw.print("\",\"" + fda.get("earfcndl") + "\",\"" + fda.get("earfcnul") + "\",\""
								+ fda.get("bandwidth") + "\",\"" + "n4-tx-antenna-count\",\"n4-rx-antenna-count"
								+ "\",\"" + path + "\",\"" + fda.get("mct").toString().toLowerCase() + "\",\"");
					} else {
						String tx = "n" + fda.get("txd") + "-tx-antenna-count";
						String rx = "n" + fda.get("rxd") + "-rx-antenna-count";
						pw.print("\",\"" + fda.get("earfcndl") + "\",\"" + fda.get("earfcnul") + "\",\""
								+ fda.get("bandwidth") + "\",\"" + tx + "\",\"" + rx + "\",\"" + path + "\",\""
								+ fda.get("mct").toString().toLowerCase() + "\",\"");
					}

					pw.print(fda.get("crs") + "\",\"" + fda.get("pci") + "\",\"" + fda.get("tac") + "\",\"\",\"" + "0"
							+ "\",\"" + "false" + "\",\"" + fda.get("zczc") + "\",\"" + fda.get("rach") + "\",\""
							+ fda.get("pracformat") + "\",\"" + "off" + "\",\"");
					pw.print(fda.get("emtc") + "\",\"" + "subframe-assignment-sa2" + "\",\""
							+ "special-subframe-pattern-ssp7\",\"" + "-" + "\",\"" + fda.get("power") + "\",\""
							+ "edge-mode" + "\",\"" + "0" + "\",\"\",\"" + fda.get("PreferredEarfcn") + "\",\""
							+ "update-mode1" + "\",\"" + "spectrum-sharing-off\",\"" + "0\",\""
							+ "frame-level-dss-mode\",\"" + "non-blanking\",\"" + "0\",\"" + "0\",\"\",\"\",\""
							+ "0\",\"" + "true\",\"" + "N 000:00:00.000\",\"" + "E 000:00:00.000\",\"0.00m\"\n");

				}

				if (fda.containsKey("LCCnum") && StringUtils.isNotEmpty(fda.get("LCCnum").toString())
						&& Integer.parseInt(fda.get("LCCnum").toString()) > lcccnt) {
					lcccnt = Integer.parseInt(fda.get("LCCnum").toString());
				}

				pw.print("\"@NB_IOT_CELL_INFORMATION\"\n");
				pw.print(
						"\"State\",\"Cell Num\",\"Parent Cell Number\",\"NB IoT PCI\",\"Operation Mode Info\",\"NB IoT TAC\",\"Use Parent PCI for Guard-band\",\"Initial Nprach\",");
				pw.print(
						"\"Nprach Start Time CL0\",\"Nprach Subcarrier Offset CL0\",\"Nprach Start Time CL1\",\"Nprach Subcarrier Offset CL1\",\"Nprach Start Time CL2\",");
				pw.print(
						"\"Nprach Subcarrier Offset CL2\",\"Guard Band\",\"Avoid UL Interfering\",\"DL RB\",\"UL RB\"\n");
				for (int i = 0; i < addAref.size(); i++) {
					fda = addAref.get(i);
					String emtc = fda.get("emtc").toString();
					int nb = Integer.parseInt(fda.get("nbiot").toString());
					int iotT = Integer.parseInt(fda.get("iottac").toString());
					String bandW = fda.get("bandwidth").toString();
					if (nb > 0) {
						if (iotT != 0 && iotT > 0) {
							// if(emtc.equals("enable")) {
							if (bandW.equals("system-bandwidth-n25")) {
								pw.print("\"ADD\",\"" + fda.get("cellid") + "\",\"" + fda.get("cellid") + "\",\""
										+ fda.get("pci") + "\",\"" + "in-band-same-pci\",\"" + fda.get("iottac")
										+ "\",\"on\",\"" + "on\",\"" + "nprach-start-time-ms8\",\""
										+ "nprach-subcarrier-offset-n36\",\"");
								pw.print("nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\""
										+ "nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\""
										+ "right\",\"" + "false\",\"" + "17\",\"" + "19\"\n");
							} else {
								pw.print("\"ADD\",\"" + fda.get("cellid") + "\",\"" + fda.get("cellid") + "\",\""
										+ fda.get("pci") + "\",\"" + "guard-band\",\"" + fda.get("iottac")
										+ "\",\"on\",\"" + "on\",\"" + "nprach-start-time-ms8\",\""
										+ "nprach-subcarrier-offset-n36\",\"");
								pw.print("nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\""
										+ "nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\""
										+ "right\",\"" + "false\",\"" + "45\",\"" + "40\"\n");
							}

						} else {
							pw.print("\"NONE\",\"" + fda.get("cellid") + "\",\"" + fda.get("cellid") + "\",\""
									+ fda.get("pci") + "\",\"guard-band\",\"" + "0\",\"" + "off\",\"" + "off\",\""
									+ "nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\"");
							pw.print("nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\""
									+ "nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\"" + "right\",\""
									+ "false\",\"" + "45\",\"" + "40\"\n");
						}

					} else {
						pw.print("\"NONE\",\"" + fda.get("cellid") + "\",\"" + fda.get("cellid") + "\",\""
								+ fda.get("pci") + "\",\"guard-band\",\"" + "0\",\"" + "off\",\"" + "off\",\""
								+ "nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\"");
						pw.print("nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\""
								+ "nprach-start-time-ms8\",\"" + "nprach-subcarrier-offset-n36\",\"" + "right\",\""
								+ "false\",\"" + "45\",\"" + "40\"\n");
					}
				}

				ArrayList<String> addCpri2 = new ArrayList<String>();
				pw.print("\"@NON_ANCHOR_NB_IOT_CELL_INFORMATION\"\n");
				pw.print("\"State\",\"Cell Num\",\"Operation Mode Info\",\"Guard Band\",\"DL RB\",\"UL RB\"\n");

				for (int i = 0; i < rrhDataCref.size(); i++) {
					if (!addCpri2.contains(rrhDataCref.get(i).get("cpristr").toString())) {
						pw.print("\"NONE\",\"\",\"\",\"\",\"\",\"\"\n");
						addCpri2.add((String) rrhDataCref.get(i).get("cpristr"));
					}
				}

				pw.print("\"@CHANNEL_BOARD_INFORMATION\"\n");
				pw.print("\"State\",\"Unit Type\",\"Unit ID\",\"Board Type\"\n");
				for (int i = 0; i <= lcccnt; i++) {
					pw.print("\"ADD\",\"ecp\",\"" + i + "\",\"lcc4-b1\"\n");
				}
				///////////////////// @cpri port information/////////////////

				// pw.print("\"@CPRI_PORT_INFORMATION\"\n");
				// pw.print("\"State\",\"Unit Type\",\"Unit ID\",\"Port ID\",\"Connection
				// Type\",\"FSU Inter Node ID\",\"FSU DU CPRI Port ID\"\n");

				pw.print("\"@CPRI_PORT_INFORMATION\"\n");
				pw.print(
						"\"State\",\"Unit Type\",\"Unit ID\",\"Port ID\",\"CPRI Compression\",\"Connection Type\",\"FSU Inter Node ID\",\"FSU DU CPRI Port ID\"\n");
				ArrayList<String> cpri = new ArrayList<String>();
				ArrayList<String> cprival = new ArrayList<String>();
				for (int i = 0; i < addAref.size(); i++) {
					fda = addAref.get(i);

					if (!cpri.contains(fda.get("cpristr").toString())) {
						pw.print("\"ADD\",\"ecp\",\"" + fda.get("LCCnum") + "\",\"" + fda.get("port") + "\",\"\",\""
								+ "direct" + "\",\"" + "\",\"\"\n");
						cpri.add((String) fda.get("cpristr"));
					}
					if ((fda.get("RUPortID").toString()).equals("1")
							&& !cprival.contains(fda.get("cpristr").toString())) {
						pw.print("\"ADD\",\"ecp\",\"" + fda.get("LCCnum") + "\",\"" + fda.get("additional_port")
								+ "\",\"\",\"" + "direct" + "\",\"" + "\",\"\"\n");
						cprival.add((String) fda.get("cpristr"));

					}

				}

				pw.print("\"@RU_INFORMATION\"\n");
				pw.print(
						"\"State\",\"RU Conf\",\"RU Port\",\"Connected DU Board Type\",\"RU Type\",\"Start Earfcn1\",\"Start Earfcn2\",\"Serial Number\",\"Azimuth\",\"Beamwidth\",\"Bisector Mode\",");
				pw.print(
						"\"X Pole Antenna\",\"Antenna Gain dBi\",\"Cable Loss\",\"Accuracy Margin dB\",\"Sharing Flag\",\"Primary Flag\"\n");
				ArrayList<String> al = new ArrayList<>();
				ArrayList<String> addCpri1 = new ArrayList<String>();
				for (int i = 0; i < rrhDataCref.size(); i++) {
					rda = rrhDataCref.get(i);

					if (rda.get("code").toString().equals("rt4401-480")) {

						((ArrayList<String>) rda.get("startearfcnA")).set(0, "55240");
						((ArrayList<String>) rda.get("startearfcnA")).set(1, "2400");
					}
					if (rda.get("code").toString().equals("rt2201-460")) {

						((ArrayList<String>) rda.get("startearfcnA")).set(0, "46790");
						((ArrayList<String>) rda.get("startearfcnA")).set(1, "2400");
					}
					if (rda.get("code").toString().equals("rfv01u-d20")) {

						((ArrayList<String>) rda.get("startearfcnA")).set(0, "5180");
						((ArrayList<String>) rda.get("startearfcnA")).set(1, "2400");
					}
					if (rda.get("code").toString().equals("rfv01u-d10")
							|| rda.get("code").toString().equals("rf4402d-d10")) {

						((ArrayList<String>) rda.get("startearfcnA")).set(0, "1950");
						((ArrayList<String>) rda.get("startearfcnA")).set(1, "600");
					}
					// ORAN radios
					if (rda.get("code").toString().equals("rf4439d-250")) {

						((ArrayList<String>) rda.get("startearfcnA")).set(0, "1950");
						((ArrayList<String>) rda.get("startearfcnA")).set(1, "600");
					}

					if (!addCpri1.contains(rda.get("cpristr").toString())) {
						pw.print("\"ADD" + "\",\"" + rda.get("cpristr") + "\",\"" + "0" + "\",\"" + "ecp" + "\",\""
								+ rda.get("code") + "\",\"" + ((ArrayList<String>) rda.get("startearfcnA")).get(0)
								+ "\",\"" + ((ArrayList<String>) rda.get("startearfcnA")).get(1) + "\",\"" + "\",\""
								+ "-1" + "\",\"" + "700\",\"bisector-mode-off\",\"");
						pw.print("true" + "\",\"" + ((ArrayList<String>) rda.get("antennaGains")).get(0) + "\",\"" + "0"
								+ "\",\"" + "10\",\"\",\"\"\n");
						addCpri1.add((String) rda.get("cpristr"));

					}
				}

				ArrayList<String> addCpri = new ArrayList<String>();
				pw.print("\"@ADDITIONAL_CPRI_INFORMATION\"\n");
				pw.print(
						"\"State\",\"RU Conf\",\"Connected DU Board Type\",\"Additional Board ID\",\"Additional Port ID\",\"RU Additional Port ID\"\n");
				for (int i = 0; i < addAref.size(); i++) {
					fda = addAref.get(i);
					if ((fda.get("RUPortID").toString()).equals("1")) {
						if (!addCpri.contains(fda.get("cpristr").toString())) {
							pw.print("\"ADD" + "\",\"" + fda.get("cpristr") + "\",\"" + "ecp" + "\",\""
									+ fda.get("LCCnum") + "\",\"" + fda.get("additional_port") + "\",\""
									+ fda.get("RUPortID") + "\"\n");
							addCpri.add((String) fda.get("cpristr"));
						}
					}
				}

				// duplicate issue
				ArrayList<String> addCpri81 = new ArrayList<String>();
				ArrayList<TreeMap<String, Object>> deleteData = new ArrayList<>();
				for (TreeMap<String, Object> tdData : rrhDataCref) {
					if (tdData.get("bandpcs").toString().contains("AWS") && addCpri81.contains(tdData.get("cpristr"))) {
						deleteData.add(tdData);
					}
					if ((tdData.get("bandpcs")).toString().contains("AWS")) {
						addCpri81.add((String) tdData.get("cpristr"));
					}
				}

				for (TreeMap<String, Object> tdData : deleteData) {
					rrhDataCref.remove(tdData);
				}
				ArrayList<String> addCpri82 = new ArrayList<String>();
				ArrayList<TreeMap<String, Object>> deleteData1 = new ArrayList<>();
				for (TreeMap<String, Object> tdData : rrhDataCref) {
					if (tdData.get("bandpcs").toString().contains("AWS") && addCpri81.contains(tdData.get("cpristr"))) {
						deleteData1.add(tdData);
					}
					if ((tdData.get("bandpcs")).toString().contains("AWS")) {
						addCpri82.add((String) tdData.get("cpristr"));
					}
				}

				// duplicate for PCS-1 and PCS-2
				ArrayList<String> addCpri83 = new ArrayList<String>();
				ArrayList<TreeMap<String, Object>> deleteData2 = new ArrayList<>();
				for (TreeMap<String, Object> tdData : rrhDataCref) {
					if (tdData.get("bandpcs").toString().contains("PCS") && addCpri83.contains(tdData.get("cpristr"))) {
						deleteData2.add(tdData);
					}
					if ((tdData.get("bandpcs")).toString().contains("PCS")) {
						addCpri83.add((String) tdData.get("cpristr"));
					}
				}

				for (TreeMap<String, Object> tdData : deleteData2) {
					rrhDataCref.remove(tdData);
				}
				ArrayList<String> addCpri84 = new ArrayList<String>();
				ArrayList<TreeMap<String, Object>> deleteData3 = new ArrayList<>();
				for (TreeMap<String, Object> tdData : rrhDataCref) {
					if (tdData.get("bandpcs").toString().contains("PCS") && addCpri83.contains(tdData.get("cpristr"))) {
						deleteData3.add(tdData);
					}
					if ((tdData.get("bandpcs")).toString().contains("PCS")) {
						addCpri84.add((String) tdData.get("cpristr"));
					}
				}

				int a1 = 0;
				int a2 = 0;
				int a3 = 0;
				int a4 = 0;
				int a5 = 0;
				int a6 = 0;

				ArrayList<String> addCpri22 = new ArrayList<String>();
				Map<String, TreeSet<Integer>> ruMap = new HashMap<String, TreeSet<Integer>>();
				ArrayList<String> addCpri33 = new ArrayList<String>();
				// ArrayList <String> addCpri44 = new ArrayList<String>();
				pw.print("\"@RU_ANTENNA_PORT_INFORMATION\"\n");
				pw.print(
						"\"State\",\"RU Conf\",\"Connected DU Board Type\",\"Antenna Port ID\",\"External Antenna Port Tx Delay\",\"External Antenna Port Rx Delay\"\n");
				for (int i = 0; i < rrhDataCref.size(); i++) {

					if (rrhDataCref.get(i).get("bandpcs").toString().contains("PCS")
							&& rrhDataCref.get(i).get("txd").toString().contains("2")) {
						rrhDataCref.get(i).replace("antennaPortMapA", new int[] { -1, -1, -1, -1, 1, 1, -1, -1 });
					} else if (rrhDataCref.get(i).get("bandpcs").toString().contains("PCS")
							&& rrhDataCref.get(i).get("txd").toString().contains("4")) {
						rrhDataCref.get(i).replace("antennaPortMapA", new int[] { -1, -1, -1, -1, 1, 1, 1, 1 });
					} else if (rrhDataCref.get(i).get("bandpcs").toString().contains("AWS")
							&& rrhDataCref.get(i).get("txd").toString().contains("4")) {
						rrhDataCref.get(i).replace("antennaPortMapA", new int[] { 1, 1, 1, 1, -1, -1, -1, -1 });
					} else if (rrhDataCref.get(i).get("bandpcs").toString().contains("AWS")
							&& rrhDataCref.get(i).get("txd").toString().contains("2")) {
						rrhDataCref.get(i).replace("antennaPortMapA", new int[] { 1, 1, -1, -1, -1, -1, -1, -1 });
					}

					if (!addCpri22.contains(rrhDataCref.get(i).get("cpristr").toString())) {

						for (int j = 0; j < ((int[]) rrhDataCref.get(i).get("antennaPortMapA")).length; j++) {

							if (((int[]) rrhDataCref.get(i).get("antennaPortMapA"))[j] != -1) {
								int antport;

								antport = j + 1;

								if ((rrhDataCref.get(i).get("cpristr").toString()).equals("2_6_0")) {
									a1++;
									if (a1 < 5) {
										pw.print("\"ADD" + "\",\"" + rrhDataCref.get(i).get("cpristr") + "\",\"" + "ecp"
												+ "\",\"" + antport + "\",\"");
										pw.print(Math.round(
												(Float.parseFloat(rrhDataCref.get(i).get("delaydl").toString().trim())))
												+ "\",\"" + Math.round(Float.parseFloat(
														rrhDataCref.get(i).get("delayul").toString().trim()))
												+ "\"\n");
									}
								} else if ((rrhDataCref.get(i).get("cpristr").toString()).equals("1_6_0")) {
									a4++;
									if (a4 < 5) {
										pw.print("\"ADD" + "\",\"" + rrhDataCref.get(i).get("cpristr") + "\",\"" + "ecp"
												+ "\",\"" + antport + "\",\"");
										pw.print(Math.round(
												(Float.parseFloat(rrhDataCref.get(i).get("delaydl").toString().trim())))
												+ "\",\"" + Math.round(Float.parseFloat(
														rrhDataCref.get(i).get("delayul").toString().trim()))
												+ "\"\n");
									}
								} else if ((rrhDataCref.get(i).get("cpristr").toString()).contains("2_8_0")) {
									a2++;
									if (a2 < 5) {
										pw.print("\"ADD" + "\",\"" + rrhDataCref.get(i).get("cpristr") + "\",\"" + "ecp"
												+ "\",\"" + antport + "\",\"");
										pw.print(Math.round(
												(Float.parseFloat(rrhDataCref.get(i).get("delaydl").toString().trim())))
												+ "\",\"" + Math.round(Float.parseFloat(
														rrhDataCref.get(i).get("delayul").toString().trim()))
												+ "\"\n");
									}
								} else if ((rrhDataCref.get(i).get("cpristr").toString()).contains("1_8_0")) {
									a5++;
									if (a5 < 5) {
										pw.print("\"ADD" + "\",\"" + rrhDataCref.get(i).get("cpristr") + "\",\"" + "ecp"
												+ "\",\"" + antport + "\",\"");
										pw.print(Math.round(
												(Float.parseFloat(rrhDataCref.get(i).get("delaydl").toString().trim())))
												+ "\",\"" + Math.round(Float.parseFloat(
														rrhDataCref.get(i).get("delayul").toString().trim()))
												+ "\"\n");
									}
								} else if ((rrhDataCref.get(i).get("cpristr").toString()).contains("2_10_0")) {
									a6++;
									if (a6 < 5) {
										pw.print("\"ADD" + "\",\"" + rrhDataCref.get(i).get("cpristr") + "\",\"" + "ecp"
												+ "\",\"" + antport + "\",\"");
										pw.print(Math.round(
												(Float.parseFloat(rrhDataCref.get(i).get("delaydl").toString().trim())))
												+ "\",\"" + Math.round(Float.parseFloat(
														rrhDataCref.get(i).get("delayul").toString().trim()))
												+ "\"\n");
									}
								} else if ((rrhDataCref.get(i).get("cpristr").toString()).contains("1_10_0")) {
									a3++;
									if (a3 < 5) {
										pw.print("\"ADD" + "\",\"" + rrhDataCref.get(i).get("cpristr") + "\",\"" + "ecp"
												+ "\",\"" + antport + "\",\"");
										pw.print(Math.round(
												(Float.parseFloat(rrhDataCref.get(i).get("delaydl").toString().trim())))
												+ "\",\"" + Math.round(Float.parseFloat(
														rrhDataCref.get(i).get("delayul").toString().trim()))
												+ "\"\n");
									}
								} else {
									pw.print("\"ADD" + "\",\"" + rrhDataCref.get(i).get("cpristr") + "\",\"" + "ecp"
											+ "\",\"" + antport + "\",\"");
									pw.print(Math.round(
											(Float.parseFloat(rrhDataCref.get(i).get("delaydl").toString().trim())))
											+ "\",\""
											+ Math.round(Float
													.parseFloat(rrhDataCref.get(i).get("delayul").toString().trim()))
											+ "\"\n");
								}
								// ruSet.add(antport);
							}
						}

					}
					/*
					 * if((rrhDataCref.get(i).get("bandpcs")).toString().contains("PCS")) {
					 * addCpri22.add((String)rrhDataCref.get(i).get("cpristr")); } else {
					 * addCpri33.add((String)rrhDataCref.get(i).get("cpristr")); }
					 */

				}

				pw.print("\"@DSP_INFORMATION\"\n");
				pw.print("\"State\",\"Unit Type\",\"Unit ID\",\"DSP ID\",\"Optic Distance\"\n");

				ArrayList<Integer> cardsProvisioned = new ArrayList<Integer>();
				for (int ci = 0; ci < addAref.size(); ci++) {
					if (Integer.parseInt(addAref.get(ci).get("LCCnum").toString()) == cardsProvisioned.size()) {
						cardsProvisioned.add(cardsProvisioned.size());
						continue;
					}
				}

				for (int li = 0; li < cardsProvisioned.size(); li++) {
					for (int di = 0; di < lccAref.get(li).size(); di++) {
						if ((Integer.parseInt(lccAref.get(li).get(di).get("opticDistance").toString())) == 0) {
							pw.print("\"ADD\",\"ecp\",\"" + li + "\",\"" + di + "\",\"20-km\"\n");
						} else {
							pw.print("\"ADD\",\"ecp\",\"" + li + "\",\"" + di + "\",\""
									+ lccAref.get(li).get(di).get("opticDistance") + "-km\"\n");
						}
					}
				}
				////////////////////// @CBRS_CHANNEL_INFORMATION////////////////////////////
				pw.print("@CBRS_CHANNEL_INFORMATION\n");
				pw.print("State,Sector ID,Carrier ID,Block Listed Channel\n");
				pw.println("NONE" + "," + "" + "," + "" + "," + "");
			}

		} catch (Exception e) {
			mydie(e.toString());
		} finally {
			if (pw != null) {
				pw.flush();
				pw.close();
			}
		}
		return AdNew;
	}
}
