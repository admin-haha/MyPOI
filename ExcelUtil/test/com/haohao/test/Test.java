package com.haohao.test;

import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import com.haohao.ExcelUtil.ExportExcelFromObject2;
import com.haohao.ExcelUtil.ImportExcelToObject;
import com.haohao.bean.Child;
import com.haohao.bean.Et0Need;
import com.haohao.bean.User;

public class Test {

	@org.junit.Test
	public void testImport() throws Exception {
		ImportExcelToObject ie = new ImportExcelToObject();
		HashMap<Integer, String> mapping = new HashMap<Integer, String>();
		mapping.put(0, "id");
		mapping.put(1, "p0");
		mapping.put(6, "tell");
		// mapping.put(2, "age");
		// mapping.put(4, "date");
		ie.setMapping(mapping);
		ie.setDatePattern("yyyy-MM-dd");
		ie.setStartRow(2);
		List list = ie.importExcel("张增强.xlsx", Et0Need.class);
		for (Object o : list) {
			Et0Need u = (Et0Need) o;
			System.out.println(u);
		}
	}

	@org.junit.Test
	public void testExport() throws Exception {
		ExportExcelFromObject2 ie = new ExportExcelFromObject2();
		List<User> beans = new ArrayList<User>();
		beans.add(new User("孙玉", 1, 22f, new Date(), new Child("小灰灰")));
		beans.add(new User("胖子", 2, 11.0f, new Date(), new Child("哈哈哈哈")));
		beans.add(new User("瘦子玉", 3, 2.11f, new Date(), new Child("黑恶虎ie")));
		HashMap<String, String> mapping = new HashMap<String, String>();
		mapping.put("id", "编号");
		mapping.put("name", "姓名");
		mapping.put("age", "年龄");
		mapping.put("date", "时间");
		mapping.put("child.name", "儿子名");
		ie.setMapping(mapping);
		ie.setDatePattern("yyyy-MM-dd");
		ie.exportExcel("C:\\Users\\admin_haha\\Desktop\\wocao.xlsx", beans);

	}

	@org.junit.Test
	public void test() {
		Calendar a = Calendar.getInstance();
		a.set(Calendar.MONTH, 2);
		a.set(Calendar.DAY_OF_MONTH, 31);
		a.add(Calendar.DAY_OF_MONTH, 1);
		System.out.println(new SimpleDateFormat("yyyy-MM-dd").format(a.getTime()));
	}

	@org.junit.Test
	public void testShow() throws IOException, UnsupportedLookAndFeelException {

		UIManager.setLookAndFeel(new NimbusLookAndFeel());
		Window window = new Window(null);
		window.setAlwaysOnTop(true);

		JFileChooser jfile = new JFileChooser();

		FileNameExtensionFilter filter = new FileNameExtensionFilter("excel 文件", "doc", "docx");
		jfile.setFileFilter(filter);
		jfile.setSelectedFile(new File(new Date().getTime() + ".xls"));
		jfile.getFileSystemView();
		jfile.showSaveDialog(window);
		File file = jfile.getSelectedFile();

		System.out.println(file.getAbsolutePath());
		System.out.println(file.getName());
		System.out.println(file.getPath());
	}
}
