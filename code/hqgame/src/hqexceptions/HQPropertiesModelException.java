package hqexceptions;
/**
 * �����ļ���ʽ����ʱ�׳�,����tablemodel�ĸ�ʽ����
 * **/
public class HQPropertiesModelException extends Exception{
	public HQPropertiesModelException() {
		super("init properties file exception");
	}
	public HQPropertiesModelException(String msg) {
		super(msg);
	}
}
