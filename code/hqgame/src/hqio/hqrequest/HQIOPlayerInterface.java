package hqio.hqrequest;

/**
 * 与world通信的接口
 * 定义对用户来讲game与world之间存在的通信规则
 * 由entityBuilder生成对应的实例，放在hqservice.hqworld中IOWorldEx.java
 * 包括：
 * 1 获取一个player
 * 2 增加一个player（即自己创建player，赋值，增加）
 * 3 删除一个player（）
 * 4 修改一个player（获取player后，修改数据，保存）
 * **/
public interface HQIOPlayerInterface {
	public HQPlayerInterface getPlayer(long key);
	public int addPlayer(HQPlayerInterface player);
	public int deletePlayer(HQPlayerInterface player);
	public int deletePlayer(long playerId);
	public int updatePlayer(HQPlayerInterface player);
}
