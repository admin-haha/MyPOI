package com.haohao.ExcelUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 
 * 把对象的信息导出到excel表格
 * 
 * 2016年3月14日16:28:29
 * 
 * @author haohao
 *
 * @version V1.0
 * 
 */
public class ExportExcelFromObject3 {

	// 存放要导入到excel中的javabean中的相应字段名称和其列名的映射
	private Map<String, String> mapping = new HashMap<String, String>();
	// 对时间的处理格式
	private String datePattern = "yyyy-MM-dd HH:mm:ss";
	// 用于创建单元格的序号
	private int colIndex = 0;

	public String getDatePattern() {
		return datePattern;
	}

	public void setDatePattern(String datePattern) {
		this.datePattern = datePattern;
	}

	public Map<String, String> getMapping() {
		return mapping;
	}

	public void setMapping(Map<String, String> mapping) {
		this.mapping = mapping;
	}

	public void exportExcel(String fileName, List beans) throws Exception {
		// 根据所给的文件名创建excel工作簿
		Workbook workBook = null;

		if (fileName.endsWith(".xls")) {
			// 对于03~07版本的excel
			workBook = new HSSFWorkbook();
		} else if (fileName.endsWith(".xlsx")) {
			// 对于03~07版本以后版本的excel
			workBook = new XSSFWorkbook();
		}

		if (workBook != null) {
			// 创建工作表
			Sheet sheet = workBook.createSheet();
			// 创建工作表的列名
			this.createHeader(sheet);
			// 获得对象个数
			int beanCount = beans.size();
			// 遍历对象集合
			for (int i = 1; i < beanCount * 2; i += 2) {
				// 将要导出的字段值添加到row中
				fillRowFromObject(sheet, i, beans.get(i / 2));
			}

			// 判断是否存在指定的文件
			File file = new File(fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			// 输出该excel表格
			workBook.write(new FileOutputStream(file));

			// 关闭流
			workBook.close();
		}

	}

	private void fillRowFromObject(Sheet sheet, int rowIndex, Object o) throws Exception {

		// 要导出的对象的相应字段名
		Set<String> set = this.mapping.keySet();
		colIndex = 0; // 用于创建单元格
		Object value; // 用于保存字段的值
		Class type;
		// for (int i = 0; i < set.size(); i++) {
		// // 创建Excel行来保存一个对象
		// sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex + 1, i,
		// i));
		// }
		// 遍历
		for (String key : set) {
			// 分割字符串
			String[] fieldsName = key.split("\\.");
			if (fieldsName.length > 1) {
				Object object = o.getClass().getMethod(getMethodName(fieldsName[0])).invoke(o);
				// 获得对象字段的值
				value = object.getClass().getMethod(getMethodName(fieldsName[1])).invoke(object);
				// 获得该字段的类型
				type = object.getClass().getDeclaredField(fieldsName[1]).getType();
			} else {
				// 获得对象字段的值
				value = o.getClass().getMethod(getMethodName(key)).invoke(o);
				// 获得该字段的类型
				type = o.getClass().getDeclaredField(key).getType();
			}

			Row row = sheet.createRow(rowIndex);
			// 创建Excel行来保存一个对象
			sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex + 1, colIndex, colIndex));
			// 判断是否是Date类型 要对其特殊处理
			if (type.equals(Date.class)) {
				row.createCell(colIndex).setCellValue(new SimpleDateFormat(this.datePattern).format(value));
			} else {
				row.createCell(colIndex).setCellValue(value.toString());
			}
			colIndex++;
		}
	}

	/**
	 * 拼接get方法名
	 * 
	 * @param name
	 *            字段名
	 * @return 返回拼接后的值
	 */
	private String getMethodName(String name) {
		return new StringBuilder().append("get").append(name.substring(0, 1).toUpperCase()).append(name.substring(1)).toString();
	}

	/**
	 * 为工作表创建列名
	 */
	private void createHeader(Sheet sheet) {
		Row row = sheet.createRow(0);
		colIndex = 0; // 用于创建单元格
		// 拿到列名集合
		Set<String> set = this.mapping.keySet();

		// 遍历
		for (String key : set) {
			String header = "";
			if (this.mapping.get(key).equals("")) {
				header = key;
			} else {
				header = this.mapping.get(key);
			}
			row.createCell(colIndex).setCellValue(header);
			colIndex++;
		}
	}
}
