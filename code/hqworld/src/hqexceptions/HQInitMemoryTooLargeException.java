package hqexceptions;
/**
 * �ڴ�����̫С�쳣��������HQStorageʱ���������̫Сʱ�׳�
 * **/
public class HQInitMemoryTooLargeException extends Exception{
	public HQInitMemoryTooLargeException() {
		super("init Memory is too large");
	}
	public HQInitMemoryTooLargeException(String msg) {
		super(msg);
	}
}
