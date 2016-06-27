package hqio.hqplayer;

import hqfile.HQPlayerVarModelReader;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * HQPlayer是一个缓存单元
 * HQPlayer可能来源于gameserver（创建玩家），可能来源于数据库（调入内存）
 * 将具体游戏中的player对象中需要存取数据库的变量缓存成三种抽象的变量：
 * 列表型（多个同种对象,可能根据优先级调出内存），当时null时，说明被调出内存，需要去数据库找，如果size为0，说明value数量为0
 * 对象型（不会调出内存），
 * 变量型（不会调出内存）
 * ***/
public class HQPlayer implements Serializable, Cloneable{
	private static final long serialVersionUID = 1L;
	
	private static final ConcurrentHashMap<String,HQPlayerVarModel> playerVarModels=
			HQPlayerVarModelReader.getInstance().getPlayerVarModelsFromVarName();
	/**每个玩家唯一标示的id，和数据库中相同**/
	private long playerId;
	/***玩家状态，服务器可根据玩家状态选择存储策略***/
	private HQPlayerState state;
	/**玩家的pairVar型变量的更新状况**/
	private HQRecordState pairVarState;
	/****/
	private long pairVarVersionNum;
	/**上一次处理该player的时间，根据该时间，决定是否降低缓存等级**/
	private long lastChangeTime=System.currentTimeMillis();
	
	/**player中的列表型变量的列表,size是确定的**/
	private HashMap<String,HQPlayerListVar> playerListVarList;
	/**player中的对象型变量的列表,size是确定的**/
	private HashMap<String,HQPlayerObjectVar> playerObjectVarList;
	/**player中的变量型变量的列表,size是确定的**/
	private HashMap<String,HQPair> pairList;
	
	public HQPlayer(long playerId){
		this.playerId=playerId;
		this.state=HQPlayerState.New;
		this.pairVarState=HQRecordState.Add;
		playerListVarList=new HashMap<String,HQPlayerListVar>();
		playerObjectVarList=new HashMap<String,HQPlayerObjectVar>();
		pairList=new HashMap<String,HQPair>();
		// 根据HQPlayerVarModel初始化hqplayer
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
