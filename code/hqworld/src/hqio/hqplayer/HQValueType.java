package hqio.hqplayer;

import java.text.SimpleDateFormat;
import java.util.Date;

public enum HQValueType{
	Integer,VarChar,DateTime;
	// 日期格式化，用于写入数据库
	private static  SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**根据string类型（配置文件中的），转化成HQValueType类型**/
	public static HQValueType getValueType(String classType){
		if(classType.equals("int") || classType.equals("bigint") ||classType.equals("tinyint"))
			return Integer;
		if(classType.equals("varchar"))
			return VarChar;
		if(classType.equals("datetime"))
			return DateTime;
		return Integer;
	}
	/**根据HQValueType获取相应的Class类型**/
	public static Class getValueClass(HQValueType valueType){
		switch (valueType) {
		case Integer:
			return Integer.getClass();
		case VarChar:
			return String.class;
		case DateTime:
			return java.util.Date.class;
		}
		return Integer.getClass();
	}
	/**根据HQValueType获取它对应的String类型，该String类型可以用于生成程序，主要用在game服务器中生成entity时用**/
	public static String getValueClassStr(HQValueType valueType){
		switch (valueType) {
		case Integer:
			return "int";
		case VarChar:
			return "String";
		case DateTime:
			return "java.util.Date";
		}
		return "Object";
	}
	/**更具HQValueType返回对应类型的大小，用于world服务器中计算对象内存大小时用**/
	public static int getValueSize(HQValueType valueType){
		switch (valueType) {
		case Integer:
			return java.lang.Integer.SIZE/java.lang.Byte.SIZE;
		case VarChar:
			return 12;
		case DateTime:
			return 12;
		}
		return 8;
	}
	/**根据HQValueType和对应的值，返回存入数据库时的字符串，包括，添加(isDot=true)和更新（isDot=false）**/
	public static String getValueToDbString(HQValueType valueType,Object object,boolean isDot){
		String result=isDot?",":"";
		if(valueType==HQValueType.VarChar)
			result+=("\""+object.toString()+"\"");
		else if(valueType==HQValueType.DateTime)
			result+=("\""+df.format((Date)object)+"\"");
		else
			result+=object.toString();
		return result;
	}
}