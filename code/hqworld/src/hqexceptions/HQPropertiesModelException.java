package hqexceptions;
/**
 * 配置文件格式错误时抛出,比如tablemodel的格式错误
 * **/
public class HQPropertiesModelException extends Exception{
	public HQPropertiesModelException() {
		super("init properties file exception");
	}
	public HQPropertiesModelException(String msg) {
		super(msg);
	}
}
