package hqexceptions;
/**
 * ������ݶ�ʧʱ�����쳣
 * ���洢���ݿ���ļ���ʧ�ܣ����һ��潫��ɾ����ʱ�򣬱����쳣��˵�����������˴�����
 * **/
public class HQPlayerDataLoseException  extends Exception{
	public HQPlayerDataLoseException() {
		super("player data lose");
	}
	public HQPlayerDataLoseException(String msg) {
		super(msg);
	}
}
