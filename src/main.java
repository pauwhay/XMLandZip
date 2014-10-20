import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import net.lingala.zip4j.exception.ZipException;

import org.jdom2.JDOMException;

import compress.cps;

public class main {

	private main(String settingPath) throws JDOMException, IOException,
			ZipException {

		File file = new File(settingPath);
		boolean isSettingPath = false;
		
		if(file.exists() && file.getName().endsWith("setting.xml")){
			isSettingPath = true;
		}
		
		if (isSettingPath) {
			XML.xml xml = new XML.xml();
			xml.settingDoc(settingPath);
			xml.update(xml.getDoc());
			System.out.println("壓縮檔修改完成");
		} else {
			Scanner in = new Scanner(System.in);

			while (!isSettingPath) {
				System.out.println("您輸入的 setting.xml 路徑錯誤");
				System.out.print("請重新輸入 ==> ");
				file = new File(in.nextLine());
				if (file.exists() && file.getName().endsWith("setting.xml")) {
					isSettingPath = true;
				}
			}
			
			XML.xml xml = new XML.xml();
			xml.settingDoc(file.toString());
			xml.update(xml.getDoc());
			System.out.println("壓縮檔修改完成");
		}
	}

	public static void main(String[] argc) throws JDOMException, IOException,
			ZipException {
		new main(argc[0]);
	}
}
