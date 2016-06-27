package hqexceptions;

public class HQModelErrorException extends Exception{
	public HQModelErrorException() {
		super("model is error to this object");
	}
	public HQModelErrorException(String msg) {
		super(msg);
	}
}
