package com.haohao.ExcelUtil;

import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 从excel表格中导入数据到javabean
 * 
 * 2016年3月14日16:29:28
 * 
 * @author haohao
 *
 * @version V1.0
 * 
 */
@SuppressWarnings("rawtypes")
public class ImportExcelToObject {

	private int startRow = 1;
	// 对时间的处理格式
	private String datePattern = "yyyy-MM-dd HH:mm:ss";
	// 存放excel中列和javabean中属性名字的映射
	private Map<Integer, String> mapping = new HashMap<Integer, String>();

	private List list = new ArrayList();

	public int getStartRow() {
		return startRow;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	public String getDatePattern() {
		return datePattern;
	}

	public void setDatePattern(String datePattern) {
		this.datePattern = datePattern;
	}

	public Map<Integer, String> getMapping() {
		return mapping;
	}

	public void setMapping(Map<Integer, String> mapping) {
		this.mapping = mapping;
	}

	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}

	/**
	 * 导入excel中数据到javabean中
	 * 
	 * @param fileName
	 *            excel文件名
	 * @param clazz
	 *            用于保存表格信息的对象的类
	 * @return 返回保存excel数据的javabean对象集合
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List importExcel(String fileName, Class clazz) throws Exception {

		// 根据所给的文件名创建excel工作簿
		Workbook workBook = null;

		if (fileName.endsWith(".xls")) {
			// 对于03~07版本的excel
			workBook = new HSSFWorkbook(new FileInputStream(fileName));
		} else if (fileName.endsWith(".xlsx")) {
			// 对于03~07版本以后版本的excel
			workBook = new XSSFWorkbook(new FileInputStream(fileName));
		}

		if (workBook != null) {
			// 获得工作表
			Sheet sheet = workBook.getSheetAt(0);

			// 遍历工作表的每一行

			for (int i = this.getStartRow(); i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				// 创建一个相应类型的javabean对象
				Object o = this.getObject(clazz);
				fillObjectFromRow(row, o);
				this.list.add(o);
			}

			// 关闭wookBook
			workBook.close();
		}

		return this.list;
	}

	/**
	 * 将excel中的一行数据保存到相应的对象中
	 * 
	 * @param row
	 *            excel中的数据行对象
	 * @param o
	 *            接收数据的对象
	 * @throws Exception
	 */
	private void fillObjectFromRow(Row row, Object o) throws Exception {
		// 获得映射的所有键的集合
		Set<Integer> set = this.mapping.keySet();
		for (Integer key : set) {
			// 获得单元格中的数据格式为字符串
			String value = this.getCellValue(row, key);

			// 对映射里面的字段名进行分割
			String[] fieldsNames = mapping.get(key).split("\\.");
			if (fieldsNames.length > 1) {
				Object object = this.setField(getChildObject(o, fieldsNames[0]), getMethodName(fieldsNames[1]), fieldsNames[1], value);
				this.setField(o, getMethodName(fieldsNames[0]), object);

			} else {
				// 获得方法名
				String methodName = getMethodName(mapping.get(key));
				this.setField(o, methodName, mapping.get(key), value);
			}
		}
	}

	private Object getChildObject(Object o, String name) throws InstantiationException, IllegalAccessException, NoSuchFieldException,
			SecurityException {
		return o.getClass().getDeclaredField(name).getType().newInstance();
	}

	/**
	 * 拼接set方法名
	 * 
	 * @param str
	 *            字段名
	 * @return 拼接后的set方法名
	 */
	private String getMethodName(String str) {
		// 拼接对象中的set方法名
		return new StringBuilder().append("set").append(str.substring(0, 1).toUpperCase()).append(str.substring(1)).toString();
	}

	/**
	 * 通过反射获得相应的javabean对象
	 * 
	 * @param clazz
	 *            要接收数据的javabean类
	 * @return 返回相应的对象实例
	 */
	private Object getObject(Class clazz) {
		Object o = null;
		try {
			o = clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			return o;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return o;
		}
		return o;
	}

	/**
	 * 通过反射动态调用相应的set方法将单元格中的值付给对象的相应属性
	 * 
	 * @param o
	 *            接收数据javabean的对象
	 * @param methodName
	 *            要调用的set方法的方法名
	 * @param value
	 *            单元格的值
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws Exception
	 *             出现异常将会抛出
	 */
	@SuppressWarnings("unchecked")
	private Object setField(Object o, String methodName, String paramName, String value) throws Exception {
		Method method = o.getClass().getMethod(methodName, o.getClass().getDeclaredField(paramName).getType());
		// set方法的参数
		Object param = null;
		// 获得javabean中set方法的参数类型
		Class type = method.getParameterTypes()[0];
		// 对整型数据进行处理
		if (type.equals(Integer.class)) {
			int index = value.indexOf(".");
			if (index != -1) {
				value = value.substring(0, index);
			}
		}
		// 对Date类型进行处理
		if (type.equals(Date.class)) {
			param = new SimpleDateFormat(this.datePattern).parse(value);
		} else {
			// 对TimeStamp类型进行处理
			if (type.equals(Timestamp.class)) {
				param = Timestamp.valueOf(value);
			} else {
				// 通过反射获得个同类型的带参构造，将String类型的值转换为相应的类型
				param = o.getClass().getDeclaredField(paramName).getType().getConstructor(String.class).newInstance(value);
			}
		}
		// type.getConstructor(String.class).newInstance(value);
		method.invoke(o, param);

		return o;
	}

	/**
	 * 通过反射动态调用相应的set方法将对象的值付给对象的相应属性
	 * 
	 * @param o
	 *            接收数据javabean的对象
	 * @param methodName
	 *            要调用的set方法的方法名
	 * @param value
	 *            要给的值
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * */
	private Object setField(Object o, String methodName, Object value) throws NoSuchMethodException, SecurityException, NoSuchFieldException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method method = o.getClass().getMethod(methodName, value.getClass());
		method.invoke(o, value);
		return o;
	}

	/**
	 * 获得指定单元格的值
	 * 
	 * @param index
	 *            单元格序号
	 * @param row
	 *            单元行
	 * @return 返回字符串形式的值
	 */
	private String getCellValue(Row row, int index) {
		// 获得单元格
		Cell cell = row.getCell(index);
		// 单元格的值
		String value = "";
		// 获得单元格类型
		int type = cell.getCellType();
		switch (type) {
		// 数字类型
		case Cell.CELL_TYPE_NUMERIC:
			double d = cell.getNumericCellValue();
			value = String.valueOf(d);
			if (value.contains("E")) {
				value = new DecimalFormat("#").format(d);
			} else {
				value = value.substring(0, value.indexOf("."));
			}
			break;
		// 字符串类型
		case Cell.CELL_TYPE_STRING:
			value = cell.getStringCellValue();
			break;
		// 布尔类型
		case Cell.CELL_TYPE_BOOLEAN:
			value = String.valueOf(cell.getBooleanCellValue());
			break;
		// 公式
		case Cell.CELL_TYPE_FORMULA:
			value = cell.getCellFormula();
			break;
		// 其他类新
		default:
			value = "";
		}
		return value;
	}
}
