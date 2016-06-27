package hqexceptions;
/**
 * 内存设置太小异常，当创建HQStorage时传入的内容太小时抛出
 * **/
public class HQInitMemoryTooLargeException extends Exception{
	public HQInitMemoryTooLargeException() {
		super("init Memory is too large");
	}
	public HQInitMemoryTooLargeException(String msg) {
		super(msg);
	}
}
