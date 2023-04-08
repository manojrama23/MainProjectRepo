package com.smart.rct.migration.serviceImpl;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.rct.migration.entity.RetTestEntity;
import com.smart.rct.migration.repository.RetRepository;
import com.smart.rct.migration.repository.RunTestRepository;
import com.smart.rct.migration.service.RetService;



@Service
public class RetServiceImpl implements RetService {
	
	private static final Logger logger = LoggerFactory.getLogger(RetServiceImpl.class);
	
	@Autowired
	RunTestRepository runTestRepository;

	@Autowired
	RetRepository retRepository;
	
		@Override
		public boolean saveRETform(XSSFSheet sheet, String fileN, Integer programId, String userName, String enbId, int uniqueId) {
			// TODO Auto-generated method stub
			boolean saveForm = false;
			String runId=Integer.toString(uniqueId);
			ArrayList<RetTestEntity> storeData = new ArrayList<RetTestEntity> ();
		
				for(int i = 8 ; i < sheet.getPhysicalNumberOfRows() ; i++) {
					RetTestEntity retTestEntity = new RetTestEntity();
					Row row = sheet.getRow(i);
					boolean xyz = isRowEmpty(row);
					if(xyz) {
					retTestEntity.setRetName(row.getCell(0).getStringCellValue());
					System.out.println("Retname : "+retTestEntity.getRetName());
					retTestEntity.setRemoteCellID(row.getCell(1).getStringCellValue());
					System.out.println("remote cellId : "+retTestEntity.getRemoteCellID());
					retTestEntity.setSectorId(row.getCell(2).getStringCellValue());
					System.out.println("SectorIdId : "+retTestEntity.getSectorId());
					Integer temp = (int)row.getCell(3).getNumericCellValue();
					String str = String.valueOf(temp);
					retTestEntity.setAntennaPosition(str);					
					System.out.println("AntennaPosition : "+retTestEntity.getAntennaPosition());
					retTestEntity.setMountType(row.getCell(4).getStringCellValue());
					System.out.println("MountType : "+retTestEntity.getMountType());					
					
					try {
	
						Integer bnd = (int)row.getCell(5).getNumericCellValue();
						str = String.valueOf(bnd);
						retTestEntity.setBand(str);

					} catch (Exception e) {
						retTestEntity.setBand(row.getCell(5).getStringCellValue());
					}		
					System.out.println("Band : "+retTestEntity.getBand());
					retTestEntity.setAntennaModel(row.getCell(9).getStringCellValue());
					System.out.println("AntennaModel : "+retTestEntity.getAntennaModel());
					retTestEntity.setRetSerialNumber(row.getCell(8).getStringCellValue());
					System.out.println("RetSerialNumber : "+retTestEntity.getRetSerialNumber());
					temp = (int)row.getCell(6).getNumericCellValue();
					str = String.valueOf(temp);
					retTestEntity.setElectricalTilt(str);
					System.out.println("ElectricalTilt : "+retTestEntity.getElectricalTilt());
					retTestEntity.setAntennaAisgRFPortNumber(row.getCell(11).getStringCellValue());
					System.out.println("AntennaAisgRFPortNumber : "+retTestEntity.getAntennaAisgRFPortNumber());
					retTestEntity.setRrhSerialNumber(row.getCell(7).getStringCellValue());
					System.out.println("RrhSerialNumber : "+retTestEntity.getRrhSerialNumber());
					Boolean p = row.getCell(13).getBooleanCellValue();
					str = String.valueOf(p);
					retTestEntity.setDiplexerPresent(str);
					System.out.println("DiplexerPresent : "+retTestEntity.getDiplexerPresent());
					retTestEntity.setPowerFeedingSwitch(row.getCell(12).getStringCellValue());
					System.out.println("PowerFeedingSwitch : "+retTestEntity.getPowerFeedingSwitch());
					retTestEntity.setAntennaSerialNumber(row.getCell(10).getStringCellValue());
					System.out.println("AntennaSerialNumber : "+retTestEntity.getAntennaSerialNumber());
					retTestEntity.setComments(row.getCell(14).getStringCellValue());					
					System.out.println("remote cellId m : "+retTestEntity.getComments());
					retTestEntity.setRetFileName(fileN);
					//String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date());
					retTestEntity.setTimeStamp(new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date()));
					retTestEntity.setUserName(userName);
					retTestEntity.setNeId(enbId);
					retTestEntity.setUniqueId(runId);
					
					storeData.add(retTestEntity);
					saveForm = true;
					
					if(saveForm && !retTestEntity.equals(null)) {
						
//						retTestRepository.saveAll(storeData);
//						storeData.clear();
						//retTestRepository.save(retTestEntity);
						retRepository.saveRetDeatil(retTestEntity);
						System.out.println("ID "+retTestEntity.getId());
						System.out.println("Repo check");
					}
				}
				}
				return saveForm;
			}
		
		
	public static boolean isRowEmpty(Row row) {

		boolean check = true;
		try {
			if (row == null) {
				check = false;
			} else {

				check = false;
				for (Cell c : row) {
					if (c.getCellType() != Cell.CELL_TYPE_BLANK) {
						check = true;
						break;
					}
				}

			}
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return check;
	}

	}
