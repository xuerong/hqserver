package hqexceptions;
/**���ݿ��е�������ⱻɾ������ѯ��ҵ�ʱ�򱨴��쳣**/
public class HQPlayerNotExistException extends Exception {
	public HQPlayerNotExistException() {
		super("player is not exist exception");
	}
	public HQPlayerNotExistException(String msg) {
		super(msg);
	}
}
