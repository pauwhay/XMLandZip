package XML;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import net.lingala.zip4j.exception.ZipException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Text;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPath;

import compress.cps;
//import Compress.compress;


public class xml {
	cps cps = new cps();
	private SAXBuilder saxbuilder = new SAXBuilder();
	private Document doc;
	private String srcFolder;
	private String warName;
	public static String bLine = System.getProperty("file.separator");

	/**
	 * 建立 setting.xml 並取得 srcFolder 和 warName
	 * 
	 * @param path
	 *            setting.xml 路徑
	 */
	public void settingDoc(String path) {
		try {
			doc = saxbuilder.build(path);
			getSrcFolder();
			getWarName();
		} catch (Exception e) {
			System.out.println(path + " 路徑錯誤");
		}
	}
	
	/**
	 * 取得 srcFolder
	 */
	private void getSrcFolder() {
		try {
			srcFolder = ((Text) XPath.selectSingleNode(doc,
					"/root/setting/srcFolder/text()")).getTextNormalize();
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			System.out.println("setting.xml 的 srcFolder 路徑錯誤");
		}
	}

	/**
	 * 取得 warName
	 */
	private void getWarName() {
		try {
			warName = ((Text) XPath.selectSingleNode(doc,
					"/root/setting/warName/text()")).getTextNormalize();
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			System.out.println("setting.xml 的 warName 路徑錯誤");
		}
	}

	/**
	 * 主要方法
	 * 
	 * @param doc
	 *            setting.xml
	 */
	public void update(Document doc) {
		try {
			cps.unzip(warName, srcFolder, "");
		} catch (ZipException e) {
			// TODO Auto-generated catch block
			System.out.println("setting.xml 的 warName 路徑錯誤");
		}
		Element root = doc.getRootElement();
		List<Element> settingList = root.getChildren("list");
		for (int i = 0; i < settingList.size(); i++) {
			Element elt = (Element) settingList.get(i);
			String needCps = elt.getChildText("needCps");
			String zipPath = elt.getChildText("zipPath");
			String fileName = elt.getChildText("fileName");
			String type = elt.getChildText("type");
			String attrName = elt.getChildText("attrName");
			String nodeXPath = elt.getChildText("nodeXPath");
			String modifyTo = elt.getChildText("modifyTo");

			if (!fileName.startsWith(bLine)) {
				fileName = fileName.replace(fileName, bLine + fileName);
			}
			if (!zipPath.startsWith(bLine)) {
				zipPath = zipPath.replace(zipPath, bLine + zipPath);
			}

			if (needCps.equals("n")) {
				Document document;
				try {
					document = saxbuilder.build(srcFolder + fileName);
					File file = new File(srcFolder + fileName);
					switch (type) {
					case "content":
						modify(document, nodeXPath, modifyTo);
						saveXML(document, fileName);
						System.out.println(i + 1 + ". " + file.getName()
								+ " 修改完成");
						break;
					case "attribute":
						modify(document, attrName, nodeXPath, modifyTo);
						saveXML(document, fileName);
						System.out.println(i + 1 + ". " + file.getName()
								+ " 修改完成");
						break;
					}
				} catch (JDOMException e) {
					// TODO Auto-generated catch block
					System.out.println("setting.xml 的 fileName 路徑錯誤");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				File file = new File(srcFolder + zipPath);

				File cpsFolder = new File(file.getParent() + bLine
						+ getZipName(file.getName()));

				String number = null;
				if (!zipPath.startsWith(bLine)) {
					zipPath = zipPath.replace(zipPath, bLine + zipPath);
				}

				if (cpsFolder.exists()) {

					number = ((int) (Math.random() * 999999 + 1)) + "";

					try {
						cps.unzip(srcFolder + zipPath, srcFolder
								+ getZipName(zipPath) + number, "");
					} catch (ZipException e) {
						// TODO Auto-generated catch block
						System.out.println("setting.xml 的 zipPath 路徑錯誤");
					}

					Document document;
					try {
						document = saxbuilder.build(srcFolder
								+ getZipName(zipPath) + number + fileName);
						switch (type) {
						case "content":
							modify(document, nodeXPath, modifyTo);
							saveXML(document, getZipName(zipPath) + number
									+ fileName);
							System.out.println(i + 1 + ". " + file.getName()
									+ " 修改完成");
							break;
						case "attribute":
							modify(document, attrName, nodeXPath, modifyTo);
							saveXML(document, getZipName(zipPath) + number
									+ fileName);
							System.out.println(i + 1 + ". " + file.getName()
									+ " 修改完成");
							break;
						}
					} catch (JDOMException e) {
						// TODO Auto-generated catch block
						System.out.println("setting.xml 的 fileName 路徑錯誤");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					cps.cpsAndDel(
							file.getParent() + bLine
									+ getZipName(file.getName()) + number,
							file.getPath());
				} else {

					try {
						cps.unzip(srcFolder + zipPath, srcFolder
								+ getZipName(zipPath), "");
						Document document = saxbuilder.build(srcFolder
								+ getZipName(zipPath) + fileName);
					} catch (JDOMException e) {
						// TODO Auto-generated catch block
						System.out.println("setting.xml 的 fileName 路徑錯誤");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ZipException e) {
						// TODO Auto-generated catch block
						System.out.println("setting.xml 的 zipPath 路徑錯誤");
					}
				}
			}
		}
		cps.cpsAndDel(srcFolder, warName);
	}

	/**
	 * 修改 xml 文件的 content
	 * 
	 * @param doc
	 *            欲修改的文件
	 * @param nodeXPath
	 *            欲修改的節點位置
	 * @param modifyTo
	 *            欲修改成什麼
	 */
	private void modify(Document doc, String nodeXPath, String modifyTo) {
		Element elt;
		try {
			elt = ((Element) XPath.selectSingleNode(doc, nodeXPath));
			elt.setText(modifyTo);
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 修改 xml 文件的 attribute
	 * 
	 * @param doc
	 *            欲修改的文件
	 * @param attrName
	 *            欲修改的屬性名稱
	 * @param nodeXPath
	 *            欲修改的節點位置
	 * @param modifyTo
	 *            欲修改成什麼
	 */
	private void modify(Document doc, String attrName, String nodeXPath,
			String modifyTo) {
		Element elt;
		try {
			elt = ((Element) XPath.selectSingleNode(doc, nodeXPath));
			elt.setAttribute(attrName, modifyTo);
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 存檔修改完的 xml
	 * 
	 * @param doc
	 * @param fileName
	 */
	private void saveXML(Document doc, String fileName) {
		XMLOutputter xmloutputter = new XMLOutputter();
		try {
			xmloutputter.output(doc, new FileWriter(srcFolder + fileName));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 取得壓縮檔名稱
	 * 
	 * @param zipPath
	 * @return
	 */
	private String getZipName(String zipPath) {
		for (int i = 0; i < zipPath.length(); i++) {
			if (zipPath.substring(i, i + 1).equals(".")) {
				zipPath = zipPath.substring(0, i);
			}
		}

		return zipPath;
	}

	/**
	 * 
	 * @return
	 */
	public Document getDoc() {
		return doc;
	}
}
