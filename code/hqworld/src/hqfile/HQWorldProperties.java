package hqfile;

import hqexceptions.HQManageExceptions;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
/**world配置文件管理类**/
public class HQWorldProperties {
	private static HQWorldProperties instance=new HQWorldProperties();
	public static HQWorldProperties getInstance(){
		return instance;
	}
	private final ConcurrentHashMap<String, String> map=new ConcurrentHashMap<String, String>();
	private final String filePath="hqworld.properties";
	public boolean init(){
		Properties pps = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(filePath));
			pps.load(in);
			Enumeration en = pps.propertyNames(); //得到配置文件的名字
			while(en.hasMoreElements()) {
				String strKey = (String) en.nextElement();
				String strValue = pps.getProperty(strKey);
				map.put(strKey, strValue);
			}
		}catch (IOException e) {
			HQManageExceptions.getInstance().manageExceptions(e);
			return false;
		}
		return true;
	}
	
	
	private HQWorldProperties(){
		
	}
	/**根据可以获取配置信息**/
	public String get(String key){
		return map.get(key);
	}
}
