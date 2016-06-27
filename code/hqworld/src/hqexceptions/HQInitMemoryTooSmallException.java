package hqexceptions;
/**
 * 内存设置太小异常，当创建HQStorage时传入的内容太小时抛出
 * **/
public class HQInitMemoryTooSmallException extends Exception{
	public HQInitMemoryTooSmallException() {
		super("init Memory is too small");
	}
	public HQInitMemoryTooSmallException(String msg) {
		super(msg);
	}
}
