package hqio.hqplayer;
/**
 * HQPlayerState���ڼ�¼��ǰ������player��״̬��������
 * �½�����½�����ߣ����ߣ�ɾ���ȼ�
 * 
 * ÿ�������ݿ��и������ݣ����ݵ�ɾ���ȼ�++
 * ÿ����game�������Ľ������ǵ����ݵ�����ȼ���ΪOnLine
 * ������Խ�ǳ�ʱ��û�и��µ����ݣ�Խ���ױ�ɾ��
 * **/
public enum HQPlayerState {
	New,Login,OnLine,UnderLine,DeleteLevel1,DeleteLevel2,DeleteLevel3;
	// ��game������֮�󣬵��øú�������״̬
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
	// �����ݿ������֮�󣬵��øú�������״̬
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
	// ����ʱ�䣬����״̬�����ظ��µ�ʱ�䱶��
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
