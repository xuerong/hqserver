package hqio.hqrequest;

/**
 * ��worldͨ�ŵĽӿ�
 * ������û�����game��world֮����ڵ�ͨ�Ź���
 * ��entityBuilder���ɶ�Ӧ��ʵ��������hqservice.hqworld��IOWorldEx.java
 * ������
 * 1 ��ȡһ��player
 * 2 ����һ��player�����Լ�����player����ֵ�����ӣ�
 * 3 ɾ��һ��player����
 * 4 �޸�һ��player����ȡplayer���޸����ݣ����棩
 * **/
public interface HQIOPlayerInterface {
	public HQPlayerInterface getPlayer(long key);
	public int addPlayer(HQPlayerInterface player);
	public int deletePlayer(HQPlayerInterface player);
	public int deletePlayer(long playerId);
	public int updatePlayer(HQPlayerInterface player);
}
