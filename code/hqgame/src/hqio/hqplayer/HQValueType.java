package hqio.hqplayer;

import java.text.SimpleDateFormat;
import java.util.Date;

public enum HQValueType{
	Integer,VarChar,DateTime;
	// ���ڸ�ʽ��������д�����ݿ�
	private static  SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**����string���ͣ������ļ��еģ���ת����HQValueType����**/
	public static HQValueType getValueType(String classType){
		if(classType.equals("int") || classType.equals("bigint") ||classType.equals("tinyint"))
			return Integer;
		if(classType.equals("varchar"))
			return VarChar;
		if(classType.equals("datetime"))
			return DateTime;
		return Integer;
	}
	/**����HQValueType��ȡ��Ӧ��Class����**/
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
	/**����HQValueType��ȡ����Ӧ��String���ͣ���String���Ϳ����������ɳ�����Ҫ����game������������entityʱ��**/
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
	/**����HQValueType���ض�Ӧ���͵Ĵ�С������world�������м�������ڴ��Сʱ��**/
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
	/**����HQValueType�Ͷ�Ӧ��ֵ�����ش������ݿ�ʱ���ַ��������������(isDot=true)�͸��£�isDot=false��**/
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