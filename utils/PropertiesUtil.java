package util;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
	public static Properties  properties=new Properties();
	static{
		//��ȡ�����ļ��е�basePath�����ص�application��
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
