package hqexceptions;
/**
 * �ڴ�����̫С�쳣��������HQStorageʱ���������̫Сʱ�׳�
 * **/
public class HQInitMemoryTooSmallException extends Exception{
	public HQInitMemoryTooSmallException() {
		super("init Memory is too small");
	}
	public HQInitMemoryTooSmallException(String msg) {
		super(msg);
	}
}
