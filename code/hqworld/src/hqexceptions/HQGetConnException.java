package hqexceptions;
/**
 * ��ȡ�����쳣��
 * �����ӳ��л�ȡ���ӳ�ʱ������˵����������̫��
 * **/
public class HQGetConnException extends Exception{
	public HQGetConnException() {
		super("get conn from pool fail");
	}
	public HQGetConnException(String msg) {
		super(msg);
	}
}
