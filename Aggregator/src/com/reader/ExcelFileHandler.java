package com.reader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExcelFileHandler {

	public static void main(String[] args) {
		int numOfParam = args.length;
		String inputFilePath = null;
		String outputFileName = null;
		if (numOfParam == 2) {
			inputFilePath = args[0];
			outputFileName = args[1];
		} else {
			System.out.println("����ȷ����������� ��ʹ�����¸�ʽ���ã� ExcelFileHandler %�����ļ���·��% %����ļ���%");
			System.exit(-1);
		}
		
		try {
			//#1 Read the input data and form the list
			System.out.println("#1  �������ļ�·����ȡ���ݣ�" + inputFilePath);
			List<String> fileNames = ExcelFileHandlerUtils.getFileList(inputFilePath);
			if (fileNames == null || fileNames.size() == 0 ) {
				System.out.println("�������ļ���û���ҵ��κ��ļ� - �˳�");
				System.exit(-1);
			}
			List<List<String>> inputDataList = ExcelFileHandlerUtils.getFileContent(fileNames);
			if (inputDataList == null || inputDataList.size() ==  0) {
				System.out.println("û�ж�ȡ���κ����� - �˳�");
				System.exit(-1);
			}
			
			// #2 populate the excel data into the object list
			System.out.println("#2  ��ȡ������ɣ� ���ɽ�����ݽṹ");
			List<ExcelRowVO> results = ExcelFileHandlerUtils.buildRowVO(inputDataList);
			
			// #3 format the input data to the target structure
			System.out.println("#3  ׼��Ŀ��ṹ����");
			List<String> dateList = new ArrayList<String>();
			Map<String, Map<String, DateMaxData>> finalResult = ExcelFileHandlerUtils.buildFinalList(results, dateList);
			if (finalResult.size() == 0) {
				System.out.println("û�Ы@ȡ����K���� - �˳�");
				System.exit(-1);
			} 
			
			// #4 write the result back to the output excel file
			System.out.println("#4  д��Ŀ���ļ���" + outputFileName);
			ExcelFileHandlerUtils.writeExcelBody(outputFileName, finalResult, dateList);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
