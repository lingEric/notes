package util;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
	public static Properties  properties=new Properties();
	static{
		//读取配置文件中的basePath并加载到application中
    	try {
			InputStream in=new PropertiesUtil().getClass().getResourceAsStream("/config.properties");
			properties.load(in);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		System.out.println(properties.get("basePath"));
	}
}
