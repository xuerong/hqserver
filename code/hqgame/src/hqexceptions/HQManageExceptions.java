package hqexceptions;
/**
 * 异常处理类
 * 所有异常处理都交给它
 * **/
public class HQManageExceptions {
	
	private static final HQManageExceptions manageException=new HQManageExceptions();
	public static HQManageExceptions getInstance(){
		return manageException;
	}
	private HQManageExceptions(){}
	public boolean init(){
		return true;
	}
	
	public void manageExceptions(Exception e){
		e.printStackTrace();
	}
}
