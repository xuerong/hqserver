package hqexceptions;
/**
 * 获取连接异常，
 * 从连接池中获取连接超时，往往说明连接数量太大
 * **/
public class HQGetConnException extends Exception{
	public HQGetConnException() {
		super("get conn from pool fail");
	}
	public HQGetConnException(String msg) {
		super(msg);
	}
}
