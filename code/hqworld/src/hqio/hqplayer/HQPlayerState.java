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
	// 与game更新完之后，调用该函数更新状态
	public static void gameUpdateState(HQPlayer player){
		switch (player.getState()) {
		case New:player.setState(New);break;
		case Login:player.setState(OnLine);break;
		case OnLine:player.setState(OnLine);break;
		case UnderLine:player.setState(OnLine);break;
		case DeleteLevel1:player.setState(OnLine);break;
		default:
			break;
		}
	}
	// 与数据库更新完之后，调用该函数更新状态
	public static void dbUpdateState(HQPlayer player){
		switch (player.getState()) {
		case New:player.setState(OnLine);break;
		case Login:player.setState(OnLine);break;
		case OnLine:player.setState(DeleteLevel1);break;
		case UnderLine:player.setState(DeleteLevel2);break;
		case DeleteLevel1:player.setState(DeleteLevel2);break;
		case DeleteLevel2:player.setState(DeleteLevel3);break;
		case DeleteLevel3:break;
		default:
			break;
		}
	}
	// 跟新时间，根据状态，返回更新的时间倍数
	public static int dbUpdateStateMultiple(HQPlayerState state){
		switch (state) {
		case New:return 1;
		case Login:return 1;
		case OnLine:return 1;
		case UnderLine:return 1;
		case DeleteLevel1:return 2;
		case DeleteLevel2:return 3;
		case DeleteLevel3:return 1;
		default:
			break;
		}
		return 1;
	}
}
