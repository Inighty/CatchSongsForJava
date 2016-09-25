package com.uwhile.zervice.spider.common;

import java.io.File;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class ConfigReader {
	public String Read(String name) {
		String value = "";
		try {
			ClassLoader classLoader = getClass().getClassLoader();  
			System.out.println(classLoader.getResource("comment-config.xml"));
		    File f = new File(classLoader.getResource("comment-config.xml").getFile());  
			SAXReader reader = new SAXReader();
			Document doc = reader.read(f);
			Element root = doc.getRootElement();
			value = root.element(name).getTextTrim();
			return value;
		} catch (Exception e) {
			e.printStackTrace();
			return value;
		}
	}
}
