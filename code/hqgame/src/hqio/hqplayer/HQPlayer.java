package hqio.hqplayer;

import hqfile.HQPlayerVarModelReader;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * HQPlayer��һ�����浥Ԫ
 * HQPlayer������Դ��gameserver��������ң���������Դ�����ݿ⣨�����ڴ棩
 * ��������Ϸ�е�player��������Ҫ��ȡ���ݿ�ı�����������ֳ���ı�����
 * �б��ͣ����ͬ�ֶ���,���ܸ������ȼ������ڴ棩����ʱnullʱ��˵���������ڴ棬��Ҫȥ���ݿ��ң����sizeΪ0��˵��value����Ϊ0
 * �����ͣ���������ڴ棩��
 * �����ͣ���������ڴ棩
 * ***/
public class HQPlayer implements Serializable, Cloneable{
	private static final long serialVersionUID = 1L;
	
	private static final ConcurrentHashMap<String,HQPlayerVarModel> playerVarModels=
			HQPlayerVarModelReader.getInstance().getPlayerVarModelsFromVarName();
	/**ÿ�����Ψһ��ʾ��id�������ݿ�����ͬ**/
	private long playerId;
	/***���״̬���������ɸ������״̬ѡ��洢����***/
	private HQPlayerState state;
	/**��ҵ�pairVar�ͱ����ĸ���״��**/
	private HQRecordState pairVarState;
	/****/
	private long pairVarVersionNum;
	/**��һ�δ����player��ʱ�䣬���ݸ�ʱ�䣬�����Ƿ񽵵ͻ���ȼ�**/
	private long lastChangeTime=System.currentTimeMillis();
	
	/**player�е��б��ͱ������б�,size��ȷ����**/
	private HashMap<String,HQPlayerListVar> playerListVarList;
	/**player�еĶ����ͱ������б�,size��ȷ����**/
	private HashMap<String,HQPlayerObjectVar> playerObjectVarList;
	/**player�еı����ͱ������б�,size��ȷ����**/
	private HashMap<String,HQPair> pairList;
	
	public HQPlayer(long playerId){
		this.playerId=playerId;
		this.state=HQPlayerState.New;
		this.pairVarState=HQRecordState.Add;
		playerListVarList=new HashMap<String,HQPlayerListVar>();
		playerObjectVarList=new HashMap<String,HQPlayerObjectVar>();
		pairList=new HashMap<String,HQPair>();
		// ����HQPlayerVarModel��ʼ��hqplayer
		for (HQPlayerVarModel playerVarModel : playerVarModels.values()) {
			if(playerVarModel.getVarType()==1){
				playerListVarList.put(playerVarModel.getVarName(),new HQPlayerListVar(playerVarModel));
			}else if(playerVarModel.getVarType()==2){
				HQPlayerObjectVar objectVar=new HQPlayerObjectVar(playerVarModel);
				objectVar.getRecord().setPlayerId(playerId);
				playerObjectVarList.put(playerVarModel.getVarName(),objectVar);
			}else if(playerVarModel.getVarType()==3){
				String[] varNames=playerVarModel.getColumnNames();
				HQValueType[] varTypes=playerVarModel.getColumnTypes();
				int pairCount=varNames.length;
				for(int i=0;i<pairCount;i++){
					HQPair pair=new HQPair();
					pair.key=varNames[i];
					pair.valueType=varTypes[i];
					pair.value=null;
					pairList.put(varNames[i],pair);
				}
			}
		}
	}

	public long getPlayerId() {
		return playerId;
	}

	public HashMap<String,HQPlayerListVar> getPlayerListVarList() {
		return playerListVarList;
	}

	public void setPlayerListVarList(HashMap<String,HQPlayerListVar> playerListVarList) {
		this.playerListVarList = playerListVarList;
	}

	public HashMap<String,HQPlayerObjectVar> getPlayerObjectVarList() {
		return playerObjectVarList;
	}

	public void setPlayerObjectVarList(HashMap<String,HQPlayerObjectVar> playerObjectVarList) {
		this.playerObjectVarList = playerObjectVarList;
	}

	public HashMap<String,HQPair> getPairList() {
		return pairList;
	}

	public void setPairList(HashMap<String,HQPair> pairList) {
		this.pairList = pairList;
	}

	public HQRecordState getPairVarState() {
		return pairVarState;
	}

	public void setPairVarState(HQRecordState pairVarState) {
		this.pairVarState = pairVarState;
	}

	public long getPairVarVersionNum() {
		return pairVarVersionNum;
	}

	public void setPairVarVersionNum(long pairVarVersionNum) {
		this.pairVarVersionNum = pairVarVersionNum;
	}
	
	
}
