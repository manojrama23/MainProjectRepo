package com.smart.rct.premigration.serviceImpl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UsmCellGrower22V {

	private static Logger logger = LoggerFactory.getLogger(UsmCellGrower22V.class);
	public static String supportCA;
	public static boolean AdNew = false;
	public static boolean remarks = true;

	public  static synchronized boolean createGrowTemplate_22A(ArrayList<TreeMap<String, Object>> cellDataAref,
			ArrayList<TreeMap<String, Object>> rrhDataAref, ArrayList<ArrayList<TreeMap<String, Object>>> lccAref,
			String growfile, ArrayList<TreeMap<String, Object>> rrhDataBref,
			ArrayList<TreeMap<String, Object>> rrhDataCref, String supportCA, String neId) {
		logger.error("start createGrowTemplate_22A**" + neId);
		PrintWriter pw = null;
		try {
			File f = new File(growfile);
			f.createNewFile();
			if(f.exists()) {
				logger.error(growfile+":file created**" + neId);
			}else {
				logger.error(growfile+":file  not created**" + neId);
			}
			pw = new PrintWriter(new BufferedWriter(new FileWriter(f)));

			UsmCellGrower.add_optic_distance_to_dsps(lccAref, cellDataAref);

			int lcccnt = 0, lccnt_add = 0;

			pw.print("\"@CELL_INFORMATION\"\n");
			pw.print(
					"\"State\",\"Sector ID\",\"Carrier ID\",\"CC ID\",\"DSP ID\",\"Cell Index in DSP\",\"RU Conf\",\"RU Port ID\",");
			pw.print(
					"\"Cell Band Carrier\",\"Earfcn DL\",\"Earfcn UL\",\"Bandwidth\",\"DL Antenna Count\",\"UL Antenna Count\",\"Path\",");
			pw.print(
					"\"Multi Carrier Type\",\"CRS\",\"PCI\",\"TAC\",\"Cell Size\",\"EAID\",\"HSF\",\"ZCZC\",\"RSI\",\"PRACH Configuration Index\",\"Virtual RF Port Mapping\",\"eMTC\",\"Subframe Assignment\",\"Special Subframe Patterns\",");
			pw.print(
					"\"Frequency Profile\",\"Dl Max Tx Power\",\"Pucch Center Mode\",\"Rcc ID\",\"Max EIRP Selection Mode\",\"CBRS Carrier Update Enable\",\"Dynamic Spectrum Sharing Mode\",\"DSS Target NR Cell Num\",\"Slot Level Operation Mode\",\"CDMA Blanking Case\",\"CDMA Blanking Upper\",\"CDMA Blanking Lower\",\"DSS PUCCH HARQ ACK for CA FDD\",\"MV IO Site Migration Indicator\",\"Term Point To DSS Index\",\"Auto GPS\",\"Latitude\",\"Longitude\",\"Height\"\n");

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
			logger.error("check status of middle*******"+neId);
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
			logger.error("check status of middle22*******"+neId);
			if (supportCA.equals("true") && cAddDataAref.isEmpty()) {
				AdNew = false;
				remarks = true;
				for (int i = 0; i < cellDataAref.size(); i++) {
					cda = prepareGrowCellCsv(cellDataAref, pw, i);

				}

				
				if (cda.containsKey("LCCnum") && StringUtils.isNotEmpty(cda.get("LCCnum").toString())
						&& Integer.parseInt(cda.get("LCCnum").toString()) > lcccnt) {
					lcccnt = Integer.parseInt(cda.get("LCCnum").toString());
				}

				/*if (Integer.parseInt(cda.get("LCCnum").toString()) > lcccnt) {
					lcccnt = Integer.parseInt(cda.get("LCCnum").toString());
				}
*/
				setGrowCellHeaders(pw);
				for (int i = 0; i < cellDataAref.size(); i++) {
					cda = cellDataAref.get(i);

					int nb = Integer.parseInt(cda.get("nbiot").toString());
					int iotT = Integer.parseInt(cda.get("iottac").toString());
					String bandW = cda.get("bandwidth").toString();
					if (nb > 0) {
						if (iotT != 0 && iotT > 0) {
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

					if (cda.get("txd").equals("16")) {// fdd mmu condition for cpri compression
						if (!cpri.contains(cda.get("cpristr").toString())) {
							pw.print("\"ADD\",\"ecp\",\"" + cda.get("LCCnum") + "\",\"" + cda.get("port")
									+ "\",\"on-2.5to1" + "\",\"" + "direct" + "\",\"" + "\",\"\"\n");
							cpri.add((String) cda.get("cpristr"));
						}
						if ((cda.get("RUPortID").toString()).equals("1")
								&& !cprival.contains(cda.get("cpristr").toString())) {
							pw.print("\"ADD\",\"ecp\",\"" + cda.get("LCCnum") + "\",\"" + cda.get("additional_port")
									+ "\",\"on-2.5to1" + "\",\"" + "direct" + "\",\"" + "\",\"\"\n");
							cprival.add((String) cda.get("cpristr"));

						}
					} else {
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

				}

				pw.print("\"@RU_INFORMATION\"\n");
				pw.print(
						"\"State\",\"RU Conf\",\"RU Port\",\"Connected DU Board Type\",\"RU Type\",\"Start Earfcn1\",\"Start Earfcn2\",\"Serial Number\",\"Subtype\",\"Azimuth\",\"Beamwidth\",\"Bisector Mode\",");
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
					// fdd mmu radios
					if (rda.get("code").toString().equals("mf1601d-250")) {

						((ArrayList<String>) rda.get("startearfcnA")).set(0, "66436");
						((ArrayList<String>) rda.get("startearfcnA")).set(1, "600");
					}

					// riu radios
					if (rda.get("code").toString().equals("rrb1-000")) {

						((ArrayList<String>) rda.get("startearfcnA")).set(0, "5180");
						((ArrayList<String>) rda.get("startearfcnA")).set(1, "66436");
					}
					if (rda.get("code").toString().equals("rrb2-001")) {

						((ArrayList<String>) rda.get("startearfcnA")).set(0, "2400");
						((ArrayList<String>) rda.get("startearfcnA")).set(1, "600");
					}

					if (!addCpri1.contains(rda.get("cpristr").toString())) {
						pw.print("\"ADD" + "\",\"" + rda.get("cpristr") + "\",\"" + "0" + "\",\"" + "ecp" + "\",\""
								+ rda.get("code") + "\",\"" + ((ArrayList<String>) rda.get("startearfcnA")).get(0)
								+ "\",\"" + ((ArrayList<String>) rda.get("startearfcnA")).get(1) + "\",\"" + "\",\"0"
								+ "\",\"" + "-1" + "\",\"" + "700" + "\",\"" + rda.get("mmuBisectorMode") + "\",\"");
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

				pw.print("\"@LTE_CBRS_CHANNEL_PREFERENCE_INFORMATION\"\n");
				pw.print(
						"\"State\",\"Sector ID\",\"Carrier ID\",\"Preferred Lowest Channel\",\"Preferred Highest Channel\",\"Preferred Earfcn\",\"Preference\"\n");

				String[] pref = null;
				String[] prefHl = null;
				String[] prefLl = null;
				String[] prefEf = null;

				for (int i = 0; i < cellDataAref.size(); i++) {
					cda = cellDataAref.get(i);

					pref = cda.get("preference").toString().split(";");
					prefLl = cda.get("preferredLowestChannel").toString().split(";");
					prefHl = cda.get("preferredHighestChannel").toString().split(";");
					prefEf = cda.get("PreferredEarfcn").toString().split(";");

					int count = 0;
					System.out.println(pref.length + " " + prefHl.length + " " + prefLl.length + " " + prefEf.length);

					if (cda.get("bandpcs").toString().contains("CBRS")) {
						if ((cda.get("preference").toString().isEmpty()
								&& cda.get("preferredLowestChannel").toString().isEmpty()
								&& cda.get("preferredHighestChannel").toString().isEmpty())
								|| (((pref.length == 0 && prefHl.length == 0 && prefLl.length == 0
										&& prefEf.length == 0)))
								|| (cda.get("PreferredEarfcn").toString().isEmpty()
										&& cda.get("preferredLowestChannel").toString().isEmpty()
										&& cda.get("preferredHighestChannel").toString().isEmpty()
										&& cda.get("preference").toString().equals("-"))) {
							// if(((pref.length == 1 && prefHl.length==1 && prefLl.length==1 &&
							// prefEf.length==1 ))) {
							count++;
							if (count == 1) {
								pw.print("\"None" + "\",\"" + "" + "\",\"" + "" + "\",\"" + "" + "\",\"" + "" + "\",\""
										+ "" + "\",\"" + "" + "\"\n");
							}

						}
						// else if ((pref.length == 1) //workaround
						// || (prefHl.length==1)
						// || (prefLl.length==1)
						// || (prefEf.length==1)){
						else if ((!cda.get("preference").toString().equals("-")
								&& !cda.get("preferredLowestChannel").toString().isEmpty()
								&& !cda.get("preferredHighestChannel").toString().isEmpty()
								&& cda.get("PreferredEarfcn").toString().isEmpty())
								|| (!cda.get("preference").toString().equals("-")
										&& !cda.get("preferredLowestChannel").toString().isEmpty()
										&& cda.get("preferredHighestChannel").toString().isEmpty()
										&& !cda.get("PreferredEarfcn").toString().isEmpty())
								|| (!cda.get("preference").toString().equals("-")
										&& cda.get("preferredLowestChannel").toString().isEmpty()
										&& !cda.get("preferredHighestChannel").toString().isEmpty()
										&& !cda.get("PreferredEarfcn").toString().isEmpty())
								|| (cda.get("preference").toString().equals("-")
										&& !cda.get("preferredLowestChannel").toString().isEmpty()
										&& !cda.get("preferredHighestChannel").toString().isEmpty()
										&& !cda.get("PreferredEarfcn").toString().isEmpty())
								|| (cda.get("preference").toString().equals("-")
										&& !cda.get("preferredLowestChannel").toString().isEmpty()
										&& !cda.get("preferredHighestChannel").toString().isEmpty()
										&& !cda.get("PreferredEarfcn").toString().isEmpty())) {

							remarks = false;

						} else if (!cda.get("preference").toString().equals("-")
								&& !cda.get("preferredLowestChannel").toString().isEmpty()
								&& cda.get("preferredHighestChannel").toString().isEmpty()
								&& cda.get("PreferredEarfcn").toString().isEmpty()) {

							remarks = false;

						} else if (!cda.get("preference").toString().equals("-")
								&& cda.get("preferredLowestChannel").toString().isEmpty()
								&& !cda.get("preferredHighestChannel").toString().isEmpty()
								&& cda.get("PreferredEarfcn").toString().isEmpty()) {

							remarks = false;

						} else if (cda.get("preference").toString().equals("-")
								&& !cda.get("preferredLowestChannel").toString().isEmpty()
								&& !cda.get("preferredHighestChannel").toString().isEmpty()
								&& cda.get("PreferredEarfcn").toString().isEmpty()) {

							remarks = false;

						} else if (cda.get("preference").toString().equals("-")
								&& !cda.get("preferredLowestChannel").toString().isEmpty()
								&& !cda.get("preferredHighestChannel").toString().isEmpty()
								&& cda.get("PreferredEarfcn").toString().isEmpty()) {

							remarks = false;

						} else if (cda.get("preference").toString().equals("-")
								&& cda.get("preferredLowestChannel").toString().isEmpty()
								&& cda.get("preferredHighestChannel").toString().isEmpty()
								&& !cda.get("PreferredEarfcn").toString().isEmpty()) {

							remarks = false;

						} else if (cda.get("preference").toString().equals("-")
								&& cda.get("preferredLowestChannel").toString().isEmpty()
								&& !cda.get("preferredHighestChannel").toString().isEmpty()
								&& cda.get("PreferredEarfcn").toString().isEmpty()) {

							remarks = false;

						} else if (cda.get("preference").toString().equals("-")
								&& !cda.get("preferredLowestChannel").toString().isEmpty()
								&& cda.get("preferredHighestChannel").toString().isEmpty()
								&& cda.get("PreferredEarfcn").toString().isEmpty()) {

							remarks = false;

						} else if (!cda.get("preference").toString().equals("-")
								&& cda.get("preferredLowestChannel").toString().isEmpty()
								&& cda.get("preferredHighestChannel").toString().isEmpty()
								&& cda.get("PreferredEarfcn").toString().isEmpty()) {

							remarks = false;

						} else {

							if (pref.length == prefHl.length && prefHl.length == prefLl.length
									&& prefLl.length == prefEf.length) {

								for (int l = 0; l < pref.length; l++) {

									pw.print("\"ADD" + "\",\"" + cda.get("sectid") + "\",\"" + cda.get("carrid")
											+ "\",\"" + prefLl[l].toLowerCase() + "\",\"" + prefHl[l].toLowerCase()
											+ "\",\"" + prefEf[l].toLowerCase() + "\",\"" + pref[l].toLowerCase()
											+ "\"\n");
								}
							}
						}
					}
					// else {
					// int count = 1;
					// if(count == 1) {
					// pw.print("\"None" + "\",\"" + "" + "\",\"" + "" + "\",\""
					// + "" + "\",\"" + "" + "\",\""
					// + "" + "\"\n");
					// }
					// }
					if (!remarks) {
						return remarks;
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
				// duplicate fdd mmu
				ArrayList<String> addCprifdd = new ArrayList<String>();
				ArrayList<TreeMap<String, Object>> deleteDatafdd = new ArrayList<>();
				for (TreeMap<String, Object> tdData : rrhDataAref) {
					if ((tdData.get("bandpcs").toString().contains("AWS")
							|| tdData.get("bandpcs").toString().contains("PCS"))
							&& addCprifdd.contains(tdData.get("cpristr"))
							&& tdData.get("txd").toString().equals("16")) {
						deleteDatafdd.add(tdData);
					}
					if ((tdData.get("bandpcs")).toString().contains("AWS")
							|| tdData.get("bandpcs").toString().contains("PCS")) {
						addCprifdd.add((String) tdData.get("cpristr"));
					}
				}

				for (TreeMap<String, Object> tdData : deleteDatafdd) {
					rrhDataAref.remove(tdData);
				}
				ArrayList<String> addCprifddM = new ArrayList<String>();
				ArrayList<TreeMap<String, Object>> deleteDatafddM = new ArrayList<>();
				for (TreeMap<String, Object> tdData : rrhDataAref) {
					if ((tdData.get("bandpcs").toString().contains("AWS")
							|| tdData.get("bandpcs").toString().contains("PCS"))
							&& addCprifdd.contains(tdData.get("cpristr"))
							&& tdData.get("txd").toString().equals("16")) {
						deleteDatafddM.add(tdData);
					}
					if ((tdData.get("bandpcs")).toString().contains("AWS")
							|| tdData.get("bandpcs").toString().contains("PCS")) {
						addCprifddM.add((String) tdData.get("cpristr"));
					}
				}

				// duplicate issue AWS 700 RIU
				ArrayList<String> addCpri86 = new ArrayList<String>();
				ArrayList<TreeMap<String, Object>> deleteDataX = new ArrayList<>();
				for (TreeMap<String, Object> tdData : rrhDataAref) {
					if (((tdData.get("bandpcs").toString().contains("AWS")
							&& tdData.get("code").toString().equals("rrb1-000"))
							|| (tdData.get("bandpcs").toString().contains("700")
									&& tdData.get("code").toString().equals("rrb1-000"))
									&& addCpri86.contains(tdData.get("cpristr")))) {
						deleteDataX.add(tdData);
					}
					if ((tdData.get("bandpcs").toString().contains("AWS")
							&& tdData.get("code").toString().equals("rrb1-000"))
							|| (tdData.get("bandpcs").toString().contains("700")
									&& tdData.get("code").toString().equals("rrb1-000"))) {
						addCpri86.add((String) tdData.get("cpristr"));
					}
				}

				for (TreeMap<String, Object> tdData : deleteDataX) {
					rrhDataAref.remove(tdData);
				}
				ArrayList<String> addCpri87 = new ArrayList<String>();
				ArrayList<TreeMap<String, Object>> deleteDataY = new ArrayList<>();
				for (TreeMap<String, Object> tdData : rrhDataAref) {
					if ((tdData.get("bandpcs").toString().contains("AWS")
							&& tdData.get("code").toString().equals("rrb1-000"))
							|| (tdData.get("bandpcs").toString().contains("700")
									&& tdData.get("code").toString().equals("rrb1-000"))
									&& addCpri86.contains(tdData.get("cpristr"))) {
						deleteDataY.add(tdData);
					}
					if ((tdData.get("bandpcs").toString().contains("AWS")
							&& tdData.get("code").toString().equals("rrb1-000"))
							|| (tdData.get("bandpcs").toString().contains("700")
									&& tdData.get("code").toString().equals("rrb1-000"))) {
						addCpri87.add((String) tdData.get("cpristr"));
					}
				}

				// duplicate issue PCS 850 RIU
				/*
				 * ArrayList <String> addCpri16 = new ArrayList<String>();
				 * ArrayList<TreeMap<String,Object>> deleteDataH = new ArrayList<>(); for
				 * (TreeMap<String, Object> tdData : rrhDataAref) { if
				 * (((tdData.get("bandpcs").toString().contains("PCS") &&
				 * tdData.get("code").toString().equals("rrb2-001")) ||
				 * (tdData.get("bandpcs").toString().contains("850") &&
				 * tdData.get("code").toString().equals("rrb2-001")) &&
				 * addCpri16.contains(tdData.get("cpristr")))) { deleteDataH.add(tdData); } if
				 * ((tdData.get("bandpcs").toString().contains("PCS") &&
				 * tdData.get("code").toString().equals("rrb2-001")) ||
				 * (tdData.get("bandpcs").toString().contains("850") &&
				 * tdData.get("code").toString().equals("rrb2-001"))) { addCpri16.add((String)
				 * tdData.get("cpristr")); } }
				 * 
				 * for (TreeMap<String, Object> tdData : deleteDataH) {
				 * rrhDataAref.remove(tdData); } ArrayList <String> addCpri17 = new
				 * ArrayList<String>(); ArrayList<TreeMap<String,Object>> deleteDataI = new
				 * ArrayList<>(); for (TreeMap<String, Object> tdData : rrhDataAref) { if
				 * ((tdData.get("bandpcs").toString().contains("PCS") &&
				 * tdData.get("code").toString().equals("rrb2-001")) ||
				 * (tdData.get("bandpcs").toString().contains("850") &&
				 * tdData.get("code").toString().equals("rrb2-001")) &&
				 * addCpri16.contains(tdData.get("cpristr"))) { deleteDataI.add(tdData); } if
				 * ((tdData.get("bandpcs").toString().contains("PCS") &&
				 * tdData.get("code").toString().equals("rrb2-001")) ||
				 * (tdData.get("bandpcs").toString().contains("850") &&
				 * tdData.get("code").toString().equals("rrb2-001"))) { addCpri17.add((String)
				 * tdData.get("cpristr")); } }
				 */

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
					} else if (rrhDataAref.get(i).get("bandpcs").toString().contains("AWS") // fdd mmu
							&& rrhDataAref.get(i).get("txd").toString().contains("16")) {
						rrhDataAref.get(i).replace("antennaPortMapA",
								new int[] { 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
										-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 });
					} else if (rrhDataAref.get(i).get("bandpcs").toString().contains("PCS")
							&& rrhDataAref.get(i).get("txd").toString().contains("16")) {
						rrhDataAref.get(i).replace("antennaPortMapA",
								new int[] { 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
										-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 });
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
					
					if (cellDataAref.get(ci).containsKey("LCCnum")
							&& StringUtils.isNotEmpty(cellDataAref.get(ci).get("LCCnum").toString())
							&& Integer.parseInt(cellDataAref.get(ci).get("LCCnum").toString()) == cardsProvisioned
									.size()) {
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

				for (int i = 0; i < cAddDataAref.size(); i++) {
					ada = prepareGrowCellCsv(cAddDataAref, pw, i);
				}

				if (ada.containsKey("LCCnum") && StringUtils.isNotEmpty(ada.get("LCCnum").toString())
						&& Integer.parseInt(ada.get("LCCnum").toString()) > lcccnt) {
					lcccnt = Integer.parseInt(ada.get("LCCnum").toString());
				}
				
				/*if (Integer.parseInt(ada.get("LCCnum").toString()) > lcccnt) {
					lcccnt = Integer.parseInt(ada.get("LCCnum").toString());
				}
*/
				setGrowCellHeaders(pw);
				for (int i = 0; i < cAddDataAref.size(); i++) {
					ada = cAddDataAref.get(i);

					int nb = Integer.parseInt(ada.get("nbiot").toString());
					int iotT = Integer.parseInt(ada.get("iottac").toString());
					String bandW = ada.get("bandwidth").toString();
					if (nb > 0) {
						if (iotT != 0 && iotT > 0) {
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
				/*if (Integer.parseInt(orgData.get("LCCnum").toString()) > lccnt_add) {
					lccnt_add = Integer.parseInt(orgData.get("LCCnum").toString());
					System.out.println("lccnt_add " + lccnt_add);
				}
				*/
				if (orgData.containsKey("LCCnum") && StringUtils.isNotEmpty(orgData.get("LCCnum").toString())
						&& Integer.parseInt(orgData.get("LCCnum").toString()) > lccnt_add) {
					lccnt_add = Integer.parseInt(orgData.get("LCCnum").toString());
					logger.error(neId+"lccnt_add " + lccnt_add);
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

					if (ada.get("txd").equals("16")) {// fdd mmu cpri compression
						if (cpri.contains(ada.get("cpristr")) && !cprivalAdd.contains(ada.get("cpristr").toString())) {
							pw.print("\"ADD\",\"ecp\",\"" + ada.get("LCCnum") + "\",\"" + ada.get("port")
									+ "\",\"on-2.5to1" + "\",\"" + "direct" + "\",\"" + "\",\"\"\n");
							cprivalAdd.add((String) ada.get("cpristr"));
						}

						if (cpri.contains(ada.get("cpristr")) && !cprivalAdd.contains(ada.get("cpristr").toString())) {
							pw.print("\"ADD\",\"ecp\",\"" + ada.get("LCCnum") + "\",\"" + ada.get("port")
									+ "\",\"on-2.5to1" + "\",\"" + "direct" + "\",\"" + "\",\"\"\n");
							cprivalAdd.add((String) ada.get("cpristr"));
						}
						if ((ada.get("RUPortID").toString()).equals("1")
								&& !cprival.contains(ada.get("cpristr").toString())) {
							if (!ruPortCpri.contains(ada.get("cpristr").toString())) {

								pw.print("\"ADD\",\"ecp\",\"" + ada.get("LCCnum") + "\",\"" + ada.get("additional_port")
										+ "\",\"on-2.5to1" + "\",\"" + "direct" + "\",\"" + "\",\"\"\n");
							}
							cprival.add((String) ada.get("cpristr"));
						}
					} else {

						if (cpri.contains(ada.get("cpristr")) && !cprivalAdd.contains(ada.get("cpristr").toString())) {
							pw.print("\"ADD\",\"ecp\",\"" + ada.get("LCCnum") + "\",\"" + ada.get("port") + "\",\"\",\""
									+ "direct" + "\",\"" + "\",\"\"\n");
							cprivalAdd.add((String) ada.get("cpristr"));
						}

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

				}

				pw.print("\"@RU_INFORMATION\"\n");
				pw.print(
						"\"State\",\"RU Conf\",\"RU Port\",\"Connected DU Board Type\",\"RU Type\",\"Start Earfcn1\",\"Start Earfcn2\",\"Serial Number\",\"Subtype\",\"Azimuth\",\"Beamwidth\",\"Bisector Mode\",");
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
					// fdd mmu radios
					if (rda.get("code").toString().equals("mf1601d-250")) {

						((ArrayList<String>) rda.get("startearfcnA")).set(0, "66436");
						((ArrayList<String>) rda.get("startearfcnA")).set(1, "600");
					}

					// riu radios
					if (rda.get("code").toString().equals("rrb1-000")) {

						((ArrayList<String>) rda.get("startearfcnA")).set(0, "5180");
						((ArrayList<String>) rda.get("startearfcnA")).set(1, "66436");
					}
					if (rda.get("code").toString().equals("rrb2-001")) {

						((ArrayList<String>) rda.get("startearfcnA")).set(0, "2400");
						((ArrayList<String>) rda.get("startearfcnA")).set(1, "600");
					}

					if (!addCpri1.contains(rda.get("cpristr").toString())) {
						if (cpriA.contains(rda.get("cpristr").toString())) {

							if (!codeA.contains(rda.get("code").toString())) {
								pw.print("\"ADD" + "\",\"" + rda.get("cpristr") + "\",\"" + "0" + "\",\"" + "ecp"
										+ "\",\"" + rda.get("code") + "\",\""
										+ ((ArrayList<String>) rda.get("startearfcnA")).get(0) + "\",\""
										+ ((ArrayList<String>) rda.get("startearfcnA")).get(1) + "\",\"" + "0" + "\",\""
										+ "-1" + "\",\"" + "700" + "\",\"" + rda.get("mmuBisectorMode") + "\",\"");
								pw.print("true" + "\",\"" + ((ArrayList<String>) rda.get("antennaGains")).get(0)
										+ "\",\"" + "0" + "\",\"" + "10\",\"\",\"\"\n");
								addCpri1.add((String) rda.get("cpristr"));
							}

						} else {
							pw.print("\"ADD" + "\",\"" + rda.get("cpristr") + "\",\"" + "0" + "\",\"" + "ecp" + "\",\""
									+ rda.get("code") + "\",\"" + ((ArrayList<String>) rda.get("startearfcnA")).get(0)
									+ "\",\"" + ((ArrayList<String>) rda.get("startearfcnA")).get(1) + "\",\""
									+ "\",\"0" + "\",\"" + "0" + "\",\"" + "-1" + "\",\"" + "700" + "\",\""
									+ rda.get("mmuBisectorMode") + "\",\"");
							pw.print("true" + "\",\"" + ((ArrayList<String>) rda.get("antennaGains")).get(0) + "\",\""
									+ "0" + "\",\"" + "10\",\"\",\"\"\n");
							addCpri1.add((String) rda.get("cpristr"));
						}
					}
				}
				ArrayList<String> addCpri = new ArrayList<String>();
				/*
				 * pw.print("\"@ADDITIONAL_CPRI_INFORMATION\"\n"); pw.print(
				 * "\"State\",\"RU Conf\",\"Connected DU Board Type\",\"Additional Board ID\",\"Additional Port ID\",\"RU Additional Port ID\"\n"
				 * ); for (int i = 0; i < addAref.size(); i++) { ada = addAref.get(i); if
				 * ((ada.get("RUPortID").toString()).equals("1")) { if
				 * (!addCpri.contains(ada.get("cpristr").toString())) { pw.print("\"ADD" +
				 * "\",\"" + ada.get("cpristr") + "\",\"" + "ecp" + "\",\"" + ada.get("LCCnum")
				 * + "\",\"" + ada.get("additional_port") + "\",\"" + ada.get("RUPortID") +
				 * "\"\n"); addCpri.add((String)ada.get("cpristr")); } } }
				 */
				// added
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
				// added

				pw.print("\"@LTE_CBRS_CHANNEL_PREFERENCE_INFORMATION\"\n");
				pw.print(
						"\"State\",\"Sector ID\",\"Carrier ID\",\"Preferred Lowest Channel\",\"Preferred Highest Channel\",\"Preferred Earfcn\",\"Preference\"\n");

				String[] pref = null;
				String[] prefHl = null;
				String[] prefLl = null;
				String[] prefEf = null;

				for (int i = 0; i < addAref.size(); i++) {
					ada = addAref.get(i);

					pref = ada.get("preference").toString().split(";");
					prefLl = ada.get("preferredLowestChannel").toString().split(";");
					prefHl = ada.get("preferredHighestChannel").toString().split(";");
					prefEf = ada.get("PreferredEarfcn").toString().split(";");

					System.out.println(pref.length + " " + prefHl.length + " " + prefLl.length + " " + prefEf.length);

					if (ada.get("bandpcs").toString().contains("CBRS")) {
						remarks = true;
						if ((ada.get("preference").toString().isEmpty()
								&& ada.get("preferredLowestChannel").toString().isEmpty()
								&& ada.get("preferredHighestChannel").toString().isEmpty())
								|| (pref.length == 0 && prefHl.length == 0 && prefLl.length == 0 && prefEf.length == 0)
								|| (ada.get("PreferredEarfcn").toString().isEmpty()
										&& ada.get("preferredLowestChannel").toString().isEmpty()
										&& ada.get("preferredHighestChannel").toString().isEmpty()
										&& ada.get("preference").toString().equals("-"))) {
							pw.print("\"None" + "\",\"" + "" + "\",\"" + "" + "\",\"" + "" + "\",\"" + "" + "\",\"" + ""
									+ "\",\"" + "" + "\"\n");
						}
						// else if ((pref.length == 1) //workaround
						// || (prefHl.length==1)
						// || (prefLl.length==1)
						// || (prefEf.length==1)){
						else if ((!ada.get("preference").toString().isEmpty()
								&& !ada.get("preferredLowestChannel").toString().isEmpty()
								&& !ada.get("preferredHighestChannel").toString().isEmpty()
								&& ada.get("PreferredEarfcn").toString().isEmpty())
								|| (!ada.get("preference").toString().isEmpty()
										&& !ada.get("preferredLowestChannel").toString().isEmpty()
										&& ada.get("preferredHighestChannel").toString().isEmpty()
										&& !ada.get("PreferredEarfcn").toString().isEmpty())
								|| (!ada.get("preference").toString().isEmpty()
										&& ada.get("preferredLowestChannel").toString().isEmpty()
										&& !ada.get("preferredHighestChannel").toString().isEmpty()
										&& !ada.get("PreferredEarfcn").toString().isEmpty())
								|| (ada.get("preference").toString().isEmpty()
										&& !ada.get("preferredLowestChannel").toString().isEmpty()
										&& !ada.get("preferredHighestChannel").toString().isEmpty()
										&& !ada.get("PreferredEarfcn").toString().isEmpty())
								|| (ada.get("preference").toString().equals("-")
										&& !ada.get("preferredLowestChannel").toString().isEmpty()
										&& !ada.get("preferredHighestChannel").toString().isEmpty()
										&& !ada.get("PreferredEarfcn").toString().isEmpty())) {

							remarks = false;

						} else {

							if (pref.length == prefHl.length && prefHl.length == prefLl.length
									&& prefLl.length == prefEf.length) {

								for (int l = 0; l < pref.length; l++) {

									pw.print("\"ADD" + "\",\"" + ada.get("sectid") + "\",\"" + ada.get("carrid")
											+ "\",\"" + prefLl[l].toLowerCase() + "\",\"" + prefHl[l].toLowerCase()
											+ "\",\"" + prefEf[l].toLowerCase() + "\",\"" + pref[l].toLowerCase()
											+ "\"\n");
								}
							}
						}
					}
					// else {
					// int count = 1;
					// if(count == 1) {
					// pw.print("\"None" + "\",\"" + "" + "\",\"" + "" + "\",\""
					// + "" + "\",\"" + "" + "\",\""
					// + "" + "\"\n");
					// }
					// }
					if (!remarks) {
						return remarks;
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

				// duplicate fdd mmu
				ArrayList<String> addCprifdd = new ArrayList<String>();
				ArrayList<TreeMap<String, Object>> deleteDatafdd = new ArrayList<>();
				for (TreeMap<String, Object> tdData : rrhDataBref) {
					if ((tdData.get("bandpcs").toString().contains("AWS")
							|| tdData.get("bandpcs").toString().contains("PCS"))
							&& addCprifdd.contains(tdData.get("cpristr"))
							&& tdData.get("txd").toString().equals("16")) {
						deleteDatafdd.add(tdData);
					}
					if ((tdData.get("bandpcs")).toString().contains("AWS")
							|| tdData.get("bandpcs").toString().contains("PCS")) {
						addCprifdd.add((String) tdData.get("cpristr"));
					}
				}

				for (TreeMap<String, Object> tdData : deleteDatafdd) {
					rrhDataBref.remove(tdData);
				}
				ArrayList<String> addCprifddM = new ArrayList<String>();
				ArrayList<TreeMap<String, Object>> deleteDatafddM = new ArrayList<>();
				for (TreeMap<String, Object> tdData : rrhDataBref) {
					if ((tdData.get("bandpcs").toString().contains("AWS")
							|| tdData.get("bandpcs").toString().contains("PCS"))
							&& addCprifdd.contains(tdData.get("cpristr"))
							&& tdData.get("txd").toString().equals("16")) {
						deleteDatafddM.add(tdData);
					}
					if ((tdData.get("bandpcs")).toString().contains("AWS")
							|| tdData.get("bandpcs").toString().contains("PCS")) {
						addCprifddM.add((String) tdData.get("cpristr"));
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
					} else if (rrhDataAref.get(i).get("bandpcs").toString().contains("AWS") // fdd mmu
							&& rrhDataAref.get(i).get("txd").toString().contains("16")) {
						rrhDataAref.get(i).replace("antennaPortMapA",
								new int[] { 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
										-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 });
					} else if (rrhDataAref.get(i).get("bandpcs").toString().contains("PCS")
							&& rrhDataAref.get(i).get("txd").toString().contains("16")) {
						rrhDataAref.get(i).replace("antennaPortMapA",
								new int[] { 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
										-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 });
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
				int m=0, n = 0;
				for (int ci = 0; ci < cAddDataAref.size(); ci++) {

					/*if (orgData.containsKey("LCCnum") && StringUtils.isNotEmpty(orgData.get("LCCnum").toString())
							&& Integer.parseInt(orgData.get("LCCnum").toString()) > lccnt_add) {
						lccnt_add = Integer.parseInt(orgData.get("LCCnum").toString());
						logger.error(neId+"lccnt_add " + lccnt_add);
					}*/
					if(cAddDataAref.get(ci).containsKey("LCCnum")&&StringUtils.isNotEmpty(cAddDataAref.get(ci).get("LCCnum").toString())) {
						m = Integer.parseInt(cAddDataAref.get(ci).get("LCCnum").toString());
						if (m >= n) {
							n = m;
							varAdd.add(cAddDataAref.get(ci).get("LCCnum").toString());
						}
					}else {
						logger.error(neId+"**else*****");
						varAdd.add(0+"");
					}
					
					/*m = Integer.parseInt(cAddDataAref.get(ci).get("LCCnum").toString());
					if (m >= n) {
						n = m;
						varAdd.add(cAddDataAref.get(ci).get("LCCnum").toString());
					}*/
					continue;
				}

				// checking without carrier add
				for (int ci = 0; ci < cDataAref.size(); ci++) {
					if (cDataAref.get(ci).containsKey("LCCnum")
							&& StringUtils.isNotEmpty(cDataAref.get(ci).get("LCCnum").toString()) && Integer
									.parseInt(cDataAref.get(ci).get("LCCnum").toString()) == cardsProvisioned.size()) {
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
				remarks = true;
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
						if (fda.get("rrh").toString().contains("RIU")) { // new riu
							path = "select-ab";
						} else {
							path = "select-ef";
						}
					} else if (fda.get("band").toString().contains("AWS") && fda.get("txd").equals("4")) {
						path = "select-abcd";
					} else if (fda.get("band").toString().contains("AWS") && fda.get("txd").equals("2")) {
						if (fda.get("rrh").toString().contains("RIU")) { // riu based path
							path = "select-cd";
						} else {
							path = "select-ab";
						}
					} else if (fda.get("band").toString().contains("PCS") && fda.get("txd").equals("4")) {
						path = "select-efgh";
					} else if (fda.get("band").toString().contains("PCS") && fda.get("txd").equals("2")) {
						if (fda.get("rrh").toString().contains("RIU")) { // new riu
							path = "select-cd";
						} else {
							path = "select-ef";
						}
					} else if (fda.get("band").equals("CBRS") && fda.get("txd").equals("4")) {
						path = "select-abcd";
					} else if (fda.get("band").equals("CBRS") && fda.get("txd").equals("2")) {
						path = "select-ab";
					} else if (fda.get("band").equals("LAA") && fda.get("txd").equals("4")) {
						path = "select-abcd";
					} else if (fda.get("band").equals("LAA") && fda.get("txd").equals("2")) {
						path = "select-ab";
					} // riu port
					else if (fda.get("band").toString().contains("AWS") && fda.get("txd").equals("1")) {
						path = "select-c";
					} else if (fda.get("band").toString().contains("700") && fda.get("txd").equals("1")) {
						path = "select-a";
					} else if (fda.get("band").toString().contains("PCS") && fda.get("txd").equals("1")) {
						path = "select-c";
					} else if (fda.get("band").toString().contains("850") && fda.get("txd").equals("1")) {
						path = "select-a";
					} // fdd mmu
					else if (fda.get("band").toString().contains("AWS") && fda.get("txd").equals("16")) {
						path = "select-16t-a";
					} else if (fda.get("band").toString().contains("PCS") && fda.get("txd").equals("16")) {
						path = "select-16t-b";
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
					} else if (fda.get("txd").equals("16") && fda.get("rxd").equals("16")) {// fdd mmu dl/ul
						pw.print("\",\"" + fda.get("earfcndl") + "\",\"" + fda.get("earfcnul") + "\",\""
								+ fda.get("bandwidth") + "\",\"" + "n16-tx-antenna-count\",\"n16-rx-antenna-count"
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
							+ "edge-mode" + "\",\"" + "0" + "\",\"\",\"" + "update-mode1" + "\",\""
							+ "spectrum-sharing-off\",\"" + "0\",\"" + "frame-level-dss-mode\",\"" + "non-blanking\",\""
							+ "0\",\"" + "0\",\"\",\"\",\"" + "0\",\"" + "true\",\"" + "N 000:00:00.000\",\""
							+ "E 000:00:00.000\",\"0.00m\"\n");

				}

				if (fda.containsKey("LCCnum") && StringUtils.isNotEmpty(fda.get("LCCnum").toString())
						&& Integer.parseInt(fda.get("LCCnum").toString()) > lcccnt) {
					lcccnt = Integer.parseInt(fda.get("LCCnum").toString());
				}

				setGrowCellHeaders(pw);
				for (int i = 0; i < addAref.size(); i++) {
					fda = addAref.get(i);

					int nb = Integer.parseInt(fda.get("nbiot").toString());
					int iotT = Integer.parseInt(fda.get("iottac").toString());
					String bandW = fda.get("bandwidth").toString();
					if (nb > 0) {
						if (iotT != 0 && iotT > 0) {
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
						"\"State\",\"RU Conf\",\"RU Port\",\"Connected DU Board Type\",\"RU Type\",\"Start Earfcn1\",\"Start Earfcn2\",\"Serial Number\",\"Subtype\",\"Azimuth\",\"Beamwidth\",\"Bisector Mode\",");
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
					// fdd mmu radios
					if (rda.get("code").toString().equals("mf1601d-250")) {

						((ArrayList<String>) rda.get("startearfcnA")).set(0, "66436");
						((ArrayList<String>) rda.get("startearfcnA")).set(1, "600");
					}

					// riu radios
					if (rda.get("code").toString().equals("rrb1-000")) {

						((ArrayList<String>) rda.get("startearfcnA")).set(0, "5180");
						((ArrayList<String>) rda.get("startearfcnA")).set(1, "66436");
					}
					if (rda.get("code").toString().equals("rrb2-001")) {

						((ArrayList<String>) rda.get("startearfcnA")).set(0, "2400");
						((ArrayList<String>) rda.get("startearfcnA")).set(1, "600");
					}

					if (!addCpri1.contains(rda.get("cpristr").toString())) {
						pw.print("\"ADD" + "\",\"" + rda.get("cpristr") + "\",\"" + "0" + "\",\"" + "ecp" + "\",\""
								+ rda.get("code") + "\",\"" + ((ArrayList<String>) rda.get("startearfcnA")).get(0)
								+ "\",\"" + ((ArrayList<String>) rda.get("startearfcnA")).get(1) + "\",\"" + "\",\"0"
								+ "\",\"" + "-1" + "\",\"" + "700" + "\",\"" + rda.get("mmuBisectorMode") + "\",\"");
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

				pw.print("\"@LTE_CBRS_CHANNEL_PREFERENCE_INFORMATION\"\n");
				pw.print(
						"\"State\",\"Sector ID\",\"Carrier ID\",\"Preferred Lowest Channel\",\"Preferred Highest Channel\",\"Preferred Earfcn\",\"Preference\"\n");

				String[] pref = null;
				String[] prefHl = null;
				String[] prefLl = null;
				String[] prefEf = null;
				int count = 0;

				for (int i = 0; i < addAref.size(); i++) {
					fda = addAref.get(i);

					pref = fda.get("preference").toString().split(";");
					prefLl = fda.get("preferredLowestChannel").toString().split(";");
					prefHl = fda.get("preferredHighestChannel").toString().split(";");
					prefEf = fda.get("PreferredEarfcn").toString().split(";");

					System.out.println(pref.length + " " + prefHl.length + " " + prefLl.length + " " + prefEf.length);

					if (fda.get("bandpcs").toString().contains("CBRS")) {

						if ((pref.length == 0 && prefHl.length == 0 && prefLl.length == 0 && prefEf.length == 0)
								|| (prefHl.length == 0 && prefLl.length == 0 && prefEf.length == 0
										&& fda.get("preference").toString().equals("-"))
								|| (fda.get("PreferredEarfcn").toString().isEmpty()
										&& fda.get("preferredLowestChannel").toString().isEmpty()
										&& fda.get("preferredHighestChannel").toString().isEmpty()
										&& fda.get("preference").toString().equals("-"))) {
							count++;
							if (count == 1) {
								pw.print("\"None" + "\",\"" + "" + "\",\"" + "" + "\",\"" + "" + "\",\"" + "" + "\",\""
										+ "" + "\",\"" + "" + "\"\n");
							}

						}
						// else if ((pref.length == 1) //workaround
						// || (prefHl.length==1)
						// || (prefLl.length==1)
						// || (prefEf.length==1)){
						else if ((!fda.get("preference").toString().equals("-")
								&& !fda.get("preferredLowestChannel").toString().isEmpty()
								&& !fda.get("preferredHighestChannel").toString().isEmpty()
								&& fda.get("PreferredEarfcn").toString().isEmpty())
								|| (!fda.get("preference").toString().equals("-")
										&& !fda.get("preferredLowestChannel").toString().isEmpty()
										&& fda.get("preferredHighestChannel").toString().isEmpty()
										&& !fda.get("PreferredEarfcn").toString().isEmpty())
								|| (!fda.get("preference").toString().equals("-")
										&& fda.get("preferredLowestChannel").toString().isEmpty()
										&& !fda.get("preferredHighestChannel").toString().isEmpty()
										&& !fda.get("PreferredEarfcn").toString().isEmpty())
								|| (fda.get("preference").toString().equals("-")
										&& !fda.get("preferredLowestChannel").toString().isEmpty()
										&& !fda.get("preferredHighestChannel").toString().isEmpty()
										&& !fda.get("PreferredEarfcn").toString().isEmpty())
								|| (fda.get("preference").toString().equals("-")
										&& !fda.get("preferredLowestChannel").toString().isEmpty()
										&& !fda.get("preferredHighestChannel").toString().isEmpty()
										&& !fda.get("PreferredEarfcn").toString().isEmpty())) {

							remarks = false;

						} else if (!fda.get("preference").toString().equals("-")
								&& !fda.get("preferredLowestChannel").toString().isEmpty()
								&& fda.get("preferredHighestChannel").toString().isEmpty()
								&& fda.get("PreferredEarfcn").toString().isEmpty()) {

							remarks = false;

						} else if (!fda.get("preference").toString().equals("-")
								&& fda.get("preferredLowestChannel").toString().isEmpty()
								&& !fda.get("preferredHighestChannel").toString().isEmpty()
								&& fda.get("PreferredEarfcn").toString().isEmpty()) {

							remarks = false;

						} else if (fda.get("preference").toString().equals("-")
								&& !fda.get("preferredLowestChannel").toString().isEmpty()
								&& !fda.get("preferredHighestChannel").toString().isEmpty()
								&& fda.get("PreferredEarfcn").toString().isEmpty()) {

							remarks = false;

						} else if (fda.get("preference").toString().equals("-")
								&& !fda.get("preferredLowestChannel").toString().isEmpty()
								&& !fda.get("preferredHighestChannel").toString().isEmpty()
								&& fda.get("PreferredEarfcn").toString().isEmpty()) {

							remarks = false;

						} else if (fda.get("preference").toString().equals("-")
								&& fda.get("preferredLowestChannel").toString().isEmpty()
								&& fda.get("preferredHighestChannel").toString().isEmpty()
								&& !fda.get("PreferredEarfcn").toString().isEmpty()) {

							remarks = false;

						} else if (fda.get("preference").toString().equals("-")
								&& fda.get("preferredLowestChannel").toString().isEmpty()
								&& !fda.get("preferredHighestChannel").toString().isEmpty()
								&& fda.get("PreferredEarfcn").toString().isEmpty()) {

							remarks = false;

						} else if (fda.get("preference").toString().equals("-")
								&& !fda.get("preferredLowestChannel").toString().isEmpty()
								&& fda.get("preferredHighestChannel").toString().isEmpty()
								&& fda.get("PreferredEarfcn").toString().isEmpty()) {

							remarks = false;

						} else if (!fda.get("preference").toString().equals("-")
								&& fda.get("preferredLowestChannel").toString().isEmpty()
								&& fda.get("preferredHighestChannel").toString().isEmpty()
								&& fda.get("PreferredEarfcn").toString().isEmpty()) {

							remarks = false;

						} else {

							if (pref.length == prefHl.length && prefHl.length == prefLl.length
									&& prefLl.length == prefEf.length) {

								for (int l = 0; l < pref.length; l++) {

									pw.print("\"ADD" + "\",\"" + fda.get("sectid") + "\",\"" + fda.get("carrid")
											+ "\",\"" + prefLl[l].toLowerCase() + "\",\"" + prefHl[l].toLowerCase()
											+ "\",\"" + prefEf[l].toLowerCase() + "\",\"" + pref[l].toLowerCase()
											+ "\"\n");
								}
							}
						}
					}
					// else {
					// count ++;
					// if(count == 1) {
					// pw.print("\"None" + "\",\"" + "" + "\",\"" + "" + "\",\""
					// + "" + "\",\"" + "" + "\",\""
					// + "" + "\"\n");
					// }
					// }
					if (!remarks) {
						return remarks;
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

				// duplicate fdd mmu
				ArrayList<String> addCprifdd = new ArrayList<String>();
				ArrayList<TreeMap<String, Object>> deleteDatafdd = new ArrayList<>();
				for (TreeMap<String, Object> tdData : rrhDataCref) {
					if ((tdData.get("bandpcs").toString().contains("AWS")
							|| tdData.get("bandpcs").toString().contains("PCS"))
							&& addCprifdd.contains(tdData.get("cpristr"))
							&& tdData.get("txd").toString().equals("16")) {
						deleteDatafdd.add(tdData);
					}
					if ((tdData.get("bandpcs")).toString().contains("AWS")
							|| tdData.get("bandpcs").toString().contains("PCS")) {
						addCprifdd.add((String) tdData.get("cpristr"));
					}
				}

				for (TreeMap<String, Object> tdData : deleteDatafdd) {
					rrhDataCref.remove(tdData);
				}
				ArrayList<String> addCprifddM = new ArrayList<String>();
				ArrayList<TreeMap<String, Object>> deleteDatafddM = new ArrayList<>();
				for (TreeMap<String, Object> tdData : rrhDataCref) {
					if ((tdData.get("bandpcs").toString().contains("AWS")
							|| tdData.get("bandpcs").toString().contains("PCS"))
							&& addCprifdd.contains(tdData.get("cpristr"))
							&& tdData.get("txd").toString().equals("16")) {
						deleteDatafddM.add(tdData);
					}
					if ((tdData.get("bandpcs")).toString().contains("AWS")
							|| tdData.get("bandpcs").toString().contains("PCS")) {
						addCprifddM.add((String) tdData.get("cpristr"));
					}
				}
				// duplicate issue AWS 700 RIU
				ArrayList<String> addCpri86 = new ArrayList<String>();
				ArrayList<TreeMap<String, Object>> deleteDataX = new ArrayList<>();
				for (TreeMap<String, Object> tdData : rrhDataCref) {
					if (((tdData.get("bandpcs").toString().contains("AWS")
							&& tdData.get("code").toString().equals("rrb1-000"))
							|| (tdData.get("bandpcs").toString().contains("700")
									&& tdData.get("code").toString().equals("rrb1-000"))
									&& addCpri86.contains(tdData.get("cpristr")))) {
						deleteDataX.add(tdData);
					}
					if ((tdData.get("bandpcs").toString().contains("AWS")
							&& tdData.get("code").toString().equals("rrb1-000"))
							|| (tdData.get("bandpcs").toString().contains("700")
									&& tdData.get("code").toString().equals("rrb1-000"))) {
						addCpri86.add((String) tdData.get("cpristr"));
					}
				}

				for (TreeMap<String, Object> tdData : deleteDataX) {
					rrhDataCref.remove(tdData);
				}
				ArrayList<String> addCpri87 = new ArrayList<String>();
				ArrayList<TreeMap<String, Object>> deleteDataY = new ArrayList<>();
				for (TreeMap<String, Object> tdData : rrhDataCref) {
					if ((tdData.get("bandpcs").toString().contains("AWS")
							&& tdData.get("code").toString().equals("rrb1-000"))
							|| (tdData.get("bandpcs").toString().contains("700")
									&& tdData.get("code").toString().equals("rrb1-000"))
									&& addCpri86.contains(tdData.get("cpristr"))) {
						deleteDataY.add(tdData);
					}
					if ((tdData.get("bandpcs").toString().contains("AWS")
							&& tdData.get("code").toString().equals("rrb1-000"))
							|| (tdData.get("bandpcs").toString().contains("700")
									&& tdData.get("code").toString().equals("rrb1-000"))) {
						addCpri87.add((String) tdData.get("cpristr"));
					}
				}

				// duplicate issue PCS 850 RIU
				/*
				 * ArrayList <String> addCpri16 = new ArrayList<String>();
				 * ArrayList<TreeMap<String,Object>> deleteDataH = new ArrayList<>(); for
				 * (TreeMap<String, Object> tdData : rrhDataCref) { if
				 * (((tdData.get("bandpcs").toString().contains("PCS") &&
				 * tdData.get("code").toString().equals("rrb2-001")) ||
				 * (tdData.get("bandpcs").toString().contains("850") &&
				 * tdData.get("code").toString().equals("rrb2-001")) &&
				 * addCpri16.contains(tdData.get("cpristr")))) { deleteDataH.add(tdData); } if
				 * ((tdData.get("bandpcs").toString().contains("PCS") &&
				 * tdData.get("code").toString().equals("rrb2-001")) ||
				 * (tdData.get("bandpcs").toString().contains("850") &&
				 * tdData.get("code").toString().equals("rrb2-001"))) { addCpri16.add((String)
				 * tdData.get("cpristr")); } }
				 * 
				 * for (TreeMap<String, Object> tdData : deleteDataH) {
				 * rrhDataCref.remove(tdData); } ArrayList <String> addCpri17 = new
				 * ArrayList<String>(); ArrayList<TreeMap<String,Object>> deleteDataI = new
				 * ArrayList<>(); for (TreeMap<String, Object> tdData : rrhDataCref) { if
				 * ((tdData.get("bandpcs").toString().contains("PCS") &&
				 * tdData.get("code").toString().equals("rrb2-001")) ||
				 * (tdData.get("bandpcs").toString().contains("850") &&
				 * tdData.get("code").toString().equals("rrb2-001")) &&
				 * addCpri16.contains(tdData.get("cpristr"))) { deleteDataI.add(tdData); } if
				 * ((tdData.get("bandpcs").toString().contains("PCS") &&
				 * tdData.get("code").toString().equals("rrb2-001")) ||
				 * (tdData.get("bandpcs").toString().contains("850") &&
				 * tdData.get("code").toString().equals("rrb2-001"))) { addCpri17.add((String)
				 * tdData.get("cpristr")); } }
				 */

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
					} else if (rrhDataCref.get(i).get("bandpcs").toString().contains("AWS") // fdd mmu
							&& rrhDataCref.get(i).get("txd").toString().contains("16")) {
						rrhDataCref.get(i).replace("antennaPortMapA",
								new int[] { 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
										-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 });
					} else if (rrhDataCref.get(i).get("bandpcs").toString().contains("PCS")
							&& rrhDataCref.get(i).get("txd").toString().contains("16")) {
						rrhDataCref.get(i).replace("antennaPortMapA",
								new int[] { 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
										-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 });
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
							}
						}
						// if((rrhDataCref.get(i).get("bandpcs")).toString().contains("PCS")) {
						// addCpri22.add((String)rrhDataCref.get(i).get("cpristr"));
						// }
					}
				}

				pw.print("\"@DSP_INFORMATION\"\n");
				pw.print("\"State\",\"Unit Type\",\"Unit ID\",\"DSP ID\",\"Optic Distance\"\n");

				ArrayList<Integer> cardsProvisioned = new ArrayList<Integer>();
				for (int ci = 0; ci < addAref.size(); ci++) {
					if (addAref.get(ci).containsKey("LCCnum")
							&& StringUtils.isNotEmpty(addAref.get(ci).get("LCCnum").toString())
							&& Integer.parseInt(addAref.get(ci).get("LCCnum").toString()) == cardsProvisioned.size()) {
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
			logger.error("end createGrowTemplate_22A**"+neId);
		} catch (Exception e) {
			UsmCellGrower.mydie("usmcellGrower22***" + e);
			System.out.println(ExceptionUtils.getFullStackTrace(e));
		} finally {
			if (pw != null) {
				pw.flush();
				pw.close();
			}
		}
		return AdNew;
	}

	private static void setGrowCellHeaders(PrintWriter pw) {
		pw.print("\"@NB_IOT_CELL_INFORMATION\"\n");
		pw.print(
				"\"State\",\"Cell Num\",\"Parent Cell Number\",\"NB IoT PCI\",\"Operation Mode Info\",\"NB IoT TAC\",\"Use Parent PCI for Guard-band\",\"Initial Nprach\",");
		pw.print(
				"\"Nprach Start Time CL0\",\"Nprach Subcarrier Offset CL0\",\"Nprach Start Time CL1\",\"Nprach Subcarrier Offset CL1\",\"Nprach Start Time CL2\",");
		pw.print(
				"\"Nprach Subcarrier Offset CL2\",\"Guard Band\",\"Avoid UL Interfering\",\"DL RB\",\"UL RB\"\n");
	}

	private static TreeMap<String, Object> prepareGrowCellCsv(ArrayList<TreeMap<String, Object>> cellDataAref, PrintWriter pw,
			int i) {
		TreeMap<String, Object> cda;
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
				path = "select-ab";
			} else {
				path = "select-ef";
			}
		} else if (cda.get("band").toString().contains("AWS") && cda.get("txd").equals("4")) {
			path = "select-abcd";
		} else if (cda.get("band").toString().contains("AWS") && cda.get("txd").equals("2")) {
			if (cda.get("rrh").toString().contains("RIU")) { // riu based path
				path = "select-cd";
			} else {
				path = "select-ab";
			}
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
		} else if (cda.get("band").equals("CBRS") && cda.get("txd").equals("2")) {
			path = "select-ab";
		} else if (cda.get("band").equals("LAA") && cda.get("txd").equals("4")) {
			path = "select-abcd";
		} else if (cda.get("band").equals("LAA") && cda.get("txd").equals("2")) {
			path = "select-ab";
		} // riu port
		else if (cda.get("band").toString().contains("AWS") && cda.get("txd").equals("1")) {
			path = "select-c";
		} else if (cda.get("band").toString().contains("700") && cda.get("txd").equals("1")) {
			path = "select-a";
		} else if (cda.get("band").toString().contains("PCS") && cda.get("txd").equals("1")) {
			path = "select-c";
		} else if (cda.get("band").toString().contains("850") && cda.get("txd").equals("1")) {
			path = "select-a";
		} // fdd mmu
		else if (cda.get("band").toString().contains("AWS") && cda.get("txd").equals("16")) {
			path = "select-16t-a";
		} else if (cda.get("band").toString().contains("PCS") && cda.get("txd").equals("16")) {
			path = "select-16t-b";
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
		} else if (cda.get("txd").equals("16") && cda.get("rxd").equals("16")) {// fdd mmu dl/ul
			pw.print("\",\"" + cda.get("earfcndl") + "\",\"" + cda.get("earfcnul") + "\",\""
					+ cda.get("bandwidth") + "\",\"" + "n16-tx-antenna-count\",\"n16-rx-antenna-count"
					+ "\",\"" + path + "\",\"" + cda.get("mct").toString().toLowerCase() + "\",\"");
		} else {
			String tx = "n" + cda.get("txd") + "-tx-antenna-count";
			String rx = "n" + cda.get("rxd") + "-rx-antenna-count";
			pw.print("\",\"" + cda.get("earfcndl") + "\",\"" + cda.get("earfcnul") + "\",\""
					+ cda.get("bandwidth") + "\",\"" + tx + "\",\"" + rx + "\",\"" + path + "\",\""
					+ cda.get("mct").toString().toLowerCase() + "\",\"");
		}
		if (cda.get("txd").equals("16")) { // fdd mmu
			pw.print("n4" + "\",\"" + cda.get("pci") + "\",\"" + cda.get("tac") + "\",\"large" + "\",\""
					+ "0" + "\",\"" + "false" + "\",\"" + cda.get("zczc") + "\",\"" + cda.get("rach")
					+ "\",\"" + cda.get("pracformat") + "\",\"" + "off" + "\",\"");
			pw.print(cda.get("emtc") + "\",\"" + "subframe-assignment-sa2" + "\",\""
					+ "special-subframe-pattern-ssp7\",\"" + "-" + "\",\"" + cda.get("power") + "\",\""
					+ "edge-mode" + "\",\"" + "0" + "\",\"\",\"" + "update-mode1" + "\",\""
					+ "spectrum-sharing-off\",\"" + "0\",\"" + "frame-level-dss-mode\",\""
					+ "non-blanking\",\"" + "0\",\"" + "0\",\"\",\"\",\"" + "0\",\"" + "true\",\""
					+ "N 000:00:00.000\",\"" + "E 000:00:00.000\",\"0.00m\"\n");
		} else {
			pw.print(cda.get("crs") + "\",\"" + cda.get("pci") + "\",\"" + cda.get("tac") + "\",\"\",\""
					+ "0" + "\",\"" + "false" + "\",\"" + cda.get("zczc") + "\",\"" + cda.get("rach")
					+ "\",\"" + cda.get("pracformat") + "\",\"" + "off" + "\",\"");
			pw.print(cda.get("emtc") + "\",\"" + "subframe-assignment-sa2" + "\",\""
					+ "special-subframe-pattern-ssp7\",\"" + "-" + "\",\"" + cda.get("power") + "\",\""
					+ "edge-mode" + "\",\"" + "0" + "\",\"\",\"" + "update-mode1" + "\",\""
					+ "spectrum-sharing-off\",\"" + "0\",\"" + "frame-level-dss-mode\",\""
					+ "non-blanking\",\"" + "0\",\"" + "0\",\"\",\"\",\"" + "0\",\"" + "true\",\""
					+ "N 000:00:00.000\",\"" + "E 000:00:00.000\",\"0.00m\"\n");
		}
		return cda;
	}
}
