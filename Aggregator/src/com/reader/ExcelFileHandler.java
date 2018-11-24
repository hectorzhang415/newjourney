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
			System.out.println("不正确的输入参数， 请使用如下格式调用： ExcelFileHandler %输入文件夹路径% %输出文件名%");
			System.exit(-1);
		}
		
		try {
			//#1 Read the input data and form the list
			System.out.println("#1  从以下文件路径读取数据：" + inputFilePath);
			List<String> fileNames = ExcelFileHandlerUtils.getFileList(inputFilePath);
			if (fileNames == null || fileNames.size() == 0 ) {
				System.out.println("在输入文件夹没有找到任何文件 - 退出");
				System.exit(-1);
			}
			List<List<String>> inputDataList = ExcelFileHandlerUtils.getFileContent(fileNames);
			if (inputDataList == null || inputDataList.size() ==  0) {
				System.out.println("没有读取到任何数据 - 退出");
				System.exit(-1);
			}
			
			// #2 populate the excel data into the object list
			System.out.println("#2  读取数据完成， 生成结果数据结构");
			List<ExcelRowVO> results = ExcelFileHandlerUtils.buildRowVO(inputDataList);
			
			// #3 format the input data to the target structure
			System.out.println("#3  准备目标结构数据");
			List<String> dateList = new ArrayList<String>();
			Map<String, Map<String, DateMaxData>> finalResult = ExcelFileHandlerUtils.buildFinalList(results, dateList);
			if (finalResult.size() == 0) {
				System.out.println("没有獲取到最終数据 - 退出");
				System.exit(-1);
			} 
			
			// #4 write the result back to the output excel file
			System.out.println("#4  写入目标文件：" + outputFileName);
			ExcelFileHandlerUtils.writeExcelBody(outputFileName, finalResult, dateList);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
