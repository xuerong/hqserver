package hqio.hqplayer;
/**
 * HQPlayerState用于记录当前缓存中player的状态，包括：
 * 新建，登陆，在线，下线，删除等级
 * 
 * 每次向数据库中更新数据，数据的删除等级++
 * 每次与game服务器的交互，是的数据的输出等级变为OnLine
 * 这样，越是长时间没有更新的数据，越容易被删除
 * **/
public enum HQPlayerState {
	New,Login,OnLine,UnderLine,DeleteLevel1,DeleteLevel2,DeleteLevel3;
}
