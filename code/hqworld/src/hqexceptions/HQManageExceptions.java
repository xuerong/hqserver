package hqexceptions;
/**
 * �쳣������
 * �����쳣����������
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
