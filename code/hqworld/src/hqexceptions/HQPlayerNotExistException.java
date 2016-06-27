package hqexceptions;
/**数据库中的玩家意外被删除，查询玩家的时候报此异常**/
public class HQPlayerNotExistException extends Exception {
	public HQPlayerNotExistException() {
		super("player is not exist exception");
	}
	public HQPlayerNotExistException(String msg) {
		super(msg);
	}
}
