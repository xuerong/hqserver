package hqexceptions;
/**
 * 玩家数据丢失时报此异常
 * 当存储数据库和文件都失败，并且缓存将其删除的时候，报此异常，说明服务器除了大问题
 * **/
public class HQPlayerDataLoseException  extends Exception{
	public HQPlayerDataLoseException() {
		super("player data lose");
	}
	public HQPlayerDataLoseException(String msg) {
		super(msg);
	}
}
