package com.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public final class ExcelFileHandlerUtils {

	public ExcelFileHandlerUtils() {}
	
	public static List<List<String>> readXlsx(String path) throws IOException {
		InputStream is = new FileInputStream(path);
		XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
		List<List<String>> result = new ArrayList<List<String>>();
		// 循环每一页，并处理当前循环页
		for (Sheet xssfSheet : xssfWorkbook) {
			if (xssfSheet == null) {
				continue;
			}
			// 处理当前页，循环读取每一行, 从第一行开始。
			for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
				Row xssfRow = xssfSheet.getRow(rowNum);
				int minColIx = xssfRow.getFirstCellNum();
				int maxColIx = xssfRow.getLastCellNum();
				List<String> rowList = new ArrayList<String>();
				for (int colIx = minColIx; colIx < maxColIx; colIx++) {
					Cell cell = xssfRow.getCell(colIx);
					if (cell == null) {
						continue;
					}
					rowList.add(cell.toString());
				}
				result.add(rowList);
			}
		}
		xssfWorkbook.close();
		xssfWorkbook = null;
		return result;
	}
	
	
	public static List<String> getFileList(String inputFilePath) throws IOException {
		List<String> fileNames = new ArrayList<String>();
		File file = new File(inputFilePath);
		File[] files = file.listFiles();
		int numOfFiles = files.length;
		if (numOfFiles > 0) {
			for (int i = 0; i < numOfFiles; i++) {
				if (files[i].isFile()) {
					String inputFileName = files[i].getAbsolutePath();
					fileNames.add(inputFileName);
				}
			}
		} else {
        	System.out.println("There's no file found under the path:" + inputFilePath);
		}
		return fileNames;
	}
	
	public static List<List<String>> getFileContent(List<String> fileNames) throws IOException {
		List<List<String>> inputDataList = null;
		if (fileNames.size() > 0) {
			for (String inputFileName : fileNames) {
				if (inputDataList == null) {
					inputDataList = readXlsx(inputFileName);
				} else {
					inputDataList.addAll(readXlsx(inputFileName));
				}
			}
		}
		return inputDataList;
	}
	
	
	public static void writeExcelBody(String fileName, Map<String, Map<String, DateMaxData>> data, List<String> dates) throws IOException {
		OutputStream os = new FileOutputStream(new File(fileName));
		Workbook wb = new XSSFWorkbook();  ;
		CellStyle cellStyle = null;
		try {
			cellStyle = wb.createCellStyle();
			cellStyle.setAlignment(HorizontalAlignment.CENTER);
			cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			Sheet sheet = wb.createSheet("Result");
			if (data.keySet().size() > 0) {
				int rowNum = 0;
				Row row = sheet.createRow(rowNum);
				//Write the excel sheet header.
				writeExcelHeader(row, dates);
				rowNum = rowNum + 2;
				List<String> rncList = new ArrayList<String>();
				rncList.addAll(data.keySet());
				Collections.sort(rncList);
				//Write each row into the file.
				for (String rncName: rncList) {
					int columnNum = 0;
					row = sheet.createRow(rowNum);
					Map<String, DateMaxData> dateData = data.get(rncName);
					if (dateData != null && dateData.size() > 0) {
						Cell cell = row.createCell(columnNum);
						cell.setCellValue(rncName);
						columnNum++;
						boolean unfilled = true;
						Double pmCapacity = null;
						for (String date : dates) {
							DateMaxData record = dateData.get(date);
							if(unfilled) {
								pmCapacity = record.getPmCapacityLimit();
								Cell currCell = row.createCell(columnNum);
								currCell.setCellValue(pmCapacity);
								unfilled = false;
								columnNum++;
							}
							Cell currCell = row.createCell(columnNum);
							currCell.setCellValue(record.getPmSumCapacity());
							columnNum++;
							Cell nextCell = row.createCell(columnNum);
							nextCell.setCellValue(record.getUtilizeRate());
							columnNum++;
						}
						
						Cell cell2 = row.createCell(1);
						cell2.setCellValue(pmCapacity);
					}
					rowNum++;
				} 
			}
			wb.write(os);
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (wb != null) {
				wb.close();
			}
			if (os != null) {
				os.close();
			}
		}
	}
	
	private static void writeExcelHeader(Row row, List<String> dates) throws IOException {
		int columnNum = 0;
		CellRangeAddress cra = new CellRangeAddress(0, 1, columnNum, columnNum);
		row.getSheet().addMergedRegion(cra);
		writeCell(row, "RNC", columnNum);
		columnNum++;
		CellRangeAddress cra2 = new CellRangeAddress(0, 1, columnNum, columnNum);
		row.getSheet().addMergedRegion(cra2);
		writeCell(row, "合同软件值（Mbps）", columnNum);
		columnNum++;
		if(dates != null && dates.size() > 0) {
			Row row2 = row.getSheet().createRow(1);
			for (String date: dates){
				CellRangeAddress currCRA = new CellRangeAddress(0,0, columnNum, columnNum + 1);
				row.getSheet().addMergedRegion(currCRA);
				writeCell(row, date, columnNum);
				writeCell(row2, "忙时平均Iub吞吐量(Mbps)", columnNum);
				writeCell(row2, "Iub吞吐量利用率(%)", columnNum+1);
				columnNum = columnNum + 2;
			}
		}
	}
	
	private static void writeCell(Row row, String cellValue, Integer columnNum) {
		Cell currCell = row.createCell(columnNum);
		currCell.setCellValue(cellValue);
	}
	
	public static List<ExcelRowVO> buildRowVO(List<List<String>> inputDataList) {
		List<ExcelRowVO> results = new ArrayList<ExcelRowVO>();
		if (inputDataList != null && inputDataList.size() > 0) {
			for (List<String> row : inputDataList) {
				// RNC, DATE_ID, HOUR_ID, pmCapacityLimit, pmSumCapacity
				String clientRNC = row.get(0);
				String calDate = row.get(1);
				String hourId = row.get(2);
				Double pmCapacityLimit = Double.valueOf(row.get(3));
				Double pmSumCapacity = Double.valueOf(row.get(4));
				ExcelRowVO rowVO = new ExcelRowVO();
				rowVO.setClientRNC(clientRNC);
				rowVO.setCalDate(calDate);
				rowVO.setHourId(hourId);
				rowVO.setPmCapacityLimit(pmCapacityLimit);
				rowVO.setPmSumCapacity(pmSumCapacity);
				results.add(rowVO);
			}
		} 
		return results;
	}
	
	public static Map<String, Map<String, DateMaxData>> buildFinalList(List<ExcelRowVO> results, List<String> dateList) {
		Map<String, Map<String, DateMaxData>> finalResult = new HashMap<String, Map<String, DateMaxData>>();
		Map<String, DateMaxData> rncDateDataMap = null;
		for (ExcelRowVO rowVO : results) {
			String rnc = rowVO.getClientRNC();
			String currDate = rowVO.getCalDate();
			rncDateDataMap = finalResult.get(rnc);
			//If the current RNC in the list
			if (rncDateDataMap != null && rncDateDataMap.size() > 0) {
				Set<String> dateSet = rncDateDataMap.keySet();
				if (dateSet.contains(currDate)) {
					//compare the value
					DateMaxData data = rncDateDataMap.get(currDate);
					double currVal = rowVO.getPmSumCapacity().doubleValue();
					if (currVal > data.getPmSumCapacity().doubleValue()) {
						data.setPmSumCapacity(currVal);
					}
				} else {
					DateMaxData data = new DateMaxData();
					data.setPmCapacityLimit(rowVO.getPmCapacityLimit());
					data.setPmSumCapacity(rowVO.getPmSumCapacity());
					rncDateDataMap.put(currDate, data);
					if (!dateList.contains(currDate)) {
						dateList.add(currDate);
					}
				}
			} else {
				//If there's no record found for this RNC, then new the record and set into the finalResult
				Map<String, DateMaxData> newData = new HashMap<String, DateMaxData>();
				DateMaxData data = new DateMaxData();
				data.setPmCapacityLimit(rowVO.getPmCapacityLimit());
				data.setPmSumCapacity(rowVO.getPmSumCapacity());
				newData.put(currDate, data);
				finalResult.put(rnc, newData);
				if (!dateList.contains(currDate)) {
					dateList.add(currDate);
				}
			}
		}
		return finalResult;
	}
	
	public static void sortDate(List<String> dateList) {
		Collections.sort(dateList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                // 返回值为int类型，大于0表示正序，小于0表示逆序
            	SimpleDateFormat format = new SimpleDateFormat("MM/DD/YYYY");
            	Date d1, d2;
    	        try {
    	            d1 = format.parse(o1);
    	            d2 = format.parse(o2);
    	        } catch (ParseException e) {
    	            return 0;
    	        }
    	        if (d1.before(d2)) {
    	            return 1;
    	        } else {
    	            return -1;
    	        }
            }
        });
	}
	
}
