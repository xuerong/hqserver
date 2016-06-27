package hqio.hqplayer;

import hqdb.HQDbOper;
import hqfile.HQPlayerVarModelReader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
	
	private static final List<HQPlayerVarModel> playerVarModels=HQPlayerVarModelReader.getInstance().getPlayerVarModels();
	/**每个玩家唯一标示的id，和数据库中相同**/
	private long playerId;
	/***玩家状态，服务器可根据玩家状态选择存储策略***/
	private HQPlayerState state;
	/**玩家的pairVar型变量的更新状况**/
	private HQRecordState pairVarState;
	/****/
	private long pairVarVersionNum;
	/**上一次处理该player的时间，根据该时间，决定是否降低缓存等级**/
	private long lastChangeTime;
	
	/**player中的列表型变量的列表,size是确定的**/
	private HashMap<String,HQPlayerListVar> playerListVarList;
	/**player中的对象型变量的列表,size是确定的**/
	private HashMap<String,HQPlayerObjectVar> playerObjectVarList;
	/**player中的变量型变量的列表,size是确定的**/
	private HashMap<String,HQPair> pairList;
	
	/**创建一个玩家(从数据库中调入内存时使用,根据优先级调入内存（-1表示全部调入）)***/
	public HQPlayer(long playerId,int priorityLevel){
		this.playerId=playerId;
		this.state=HQPlayerState.OnLine;
		this.pairVarState=HQRecordState.Identical;
		
		playerListVarList=new HashMap<String,HQPlayerListVar>();
		playerObjectVarList=new HashMap<String,HQPlayerObjectVar>();
		pairList=new HashMap<String,HQPair>();
		
		for (HQPlayerVarModel model : playerVarModels) {
			if(model.getVarType()==1){
				HQPlayerListVar listVar=new HQPlayerListVar(model.getVarName(), model.getTableName(),model.getPriorityLevel());
				if(priorityLevel==-1 || model.getPriorityLevel()<=priorityLevel){
					// 从数据库中取出，调入内存
					// select * from model.tableName where playerId=playerId
					List<HQRecord> recordList=HQDbOper.getInstance().getRecords(model.getTableName(), playerId, model.getColumnNames(), model.getColumnTypes());
					listVar.setRecordList(recordList);
				}
				playerListVarList.put(model.getVarName(),listVar);
			}else if(model.getVarType()==2){
				HQRecord record=HQDbOper.getInstance().getRecord(model.getTableName(), playerId, model.getColumnNames(), model.getColumnTypes());
				HQPlayerObjectVar objectVar=new HQPlayerObjectVar(model.getVarName(), model.getTableName(), record);
				playerObjectVarList.put(model.getVarName(),objectVar);
			}else if(model.getVarType()==3){
				HQRecord record=HQDbOper.getInstance().getRecord(model.getTableName(), playerId, model.getColumnNames(), model.getColumnTypes());
				for (HQPair pair : record.getPairs()) {
					pairList.put(pair.key,pair);
				}
			}
		}
		this.lastChangeTime=System.currentTimeMillis();
	}
	/**创建一个玩家(从数据库中调入内存时使用)***/
	public HQPlayer(long playerId){
		this(playerId, -1);
	}
	/**
	 * 获取数据
	 * 如果是获取所有数据，将本对象返回
	 * 如果不是，根据需要获取的数据返回，其它设为null
	 * **/
	public HQPlayer getPlayer(Boolean isAll,String[] listVarNames,
			String[] objectVarNames,String[] pairVarNames) throws CloneNotSupportedException{
		if(isAll)
			return this;
		HQPlayer result=(HQPlayer)this.clone();
		// playerListVarList
		if(listVarNames!=null && listVarNames.length>0){
			result.playerListVarList=new HashMap<String,HQPlayerListVar>();
			for (String listVarName : listVarNames) {
				HQPlayerListVar listVar=playerListVarList.get(listVarName);
				if(listVar.getRecordList()==null){
					// list被调出内存，需要去数据库加载
					// select * from var.tableName where playerId=this.playerId
					List<HQRecord> recordList=HQDbOper.getInstance().getRecords(listVarName,playerId);
					listVar.setRecordList(recordList);
				}
				result.playerListVarList.put(listVarName,listVar.clone());
			}
		}else{
			result.playerListVarList=null;
		}
		// playerObjectVarList
		if(objectVarNames!=null && objectVarNames.length>0){
			result.playerObjectVarList=new HashMap<String,HQPlayerObjectVar>();
			for (String objectVarName : objectVarNames) {
				HQPlayerObjectVar var=playerObjectVarList.get(objectVarName);
				result.playerObjectVarList.put(objectVarName,var.clone());
			}
		}else{
			result.playerObjectVarList=null;
		}
		// pairList
		if(pairVarNames!=null && pairVarNames.length>0){
			result.pairList=new HashMap<String,HQPair>();
			for (String pairVarName : pairVarNames) {
				HQPair var=pairList.get(pairVarName);
				result.pairList.put(pairVarName,var.clone());
			}
		}else{
			result.pairList=null;
		}
		return result;
	}
	
	/**
	 * put数据，包括添加新的和修改此处仅put到缓存中
	 * 
	 * put的数据以Record为单位
	 * 添加的，返回1
	 * 修改的，返回2
	 * 失败的，返回-1(变量不存在)
	 * 
	 * 进来之前，需要先验证是record正确的，通过varName
	 * 
	 * 目前在HQStorage.java中，对数据的操作全部在varName层，这个函数用不到
	 * **/
	public int putData(Object value , String varName){
		HQPlayerListVar listVar=playerListVarList.get(varName);
		if(listVar!=null){
			HQRecord record = (HQRecord)value;
			if(listVar.getRecordList()==null){
				// list被调出内存，需要去数据库加载
				// select * from var.tableName where playerId=this.playerId
				List<HQRecord> recordList=HQDbOper.getInstance().getRecords(varName,playerId);
				listVar.setRecordList(recordList);
			}
			for (HQRecord oldRecord : listVar.getRecordList()) {
				if(record.getRecordId()==oldRecord.getRecordId()){
					oldRecord.setPairs(record.getPairs());
					oldRecord.setRecordState(getPutRecordRecordState(oldRecord.getRecordState()));
					return 2;
				}
			}
			record.setRecordState(HQRecordState.Add);
			listVar.getRecordList().add(record);
			return 1;
		}
		HQPlayerObjectVar playerObjectVar=playerObjectVarList.get(varName);
		if(playerObjectVar!=null){
			HQRecord record = (HQRecord)value;
			if(playerObjectVar.getRecord()==null || 
					playerObjectVar.getRecord().getRecordId()!=record.getRecordId()){
				record.setRecordState(HQRecordState.Add);
				playerObjectVar.setRecord(record);
				return 1;
			}
			playerObjectVar.getRecord().setPairs(record.getPairs());
			playerObjectVar.getRecord().setRecordState(
					getPutRecordRecordState(playerObjectVar.getRecord().getRecordState()));
			return 2;
		}
		
		HQPair pair=pairList.get(varName);
		if(pair!=null){
			pair.valueType=HQValueType.getValueType(value.getClass().toString());
			pair.value=value.toString();
			return 1;
		}
		return -1;
	}
	/**
	 * delete数据，只能删除list中的record
	 * 
	 * delete的数据以Record为单位，传入的参数为recordId
	 * 不存在的，返回1
	 * 删除成功的，返回2
	 * 失败的，返回-1(变量不存在)
	 * 
	 * **/
	public int deleteData(long recordId,String varName){
		HQPlayerListVar listVar=playerListVarList.get(varName);
		if(listVar!=null){
			if(listVar.getRecordList()==null){
				// list被调出内存，需要去数据库加载
				// select * from var.tableName where playerId=this.playerId
				List<HQRecord> recordList=HQDbOper.getInstance().getRecords(varName,playerId);
				listVar.setRecordList(recordList);
			}
			for (HQRecord oldRecord : listVar.getRecordList()) {
				if(recordId==oldRecord.getRecordId()){
					oldRecord.setRecordState(HQRecordState.Delete);
					return 2;
				}
			}
			return 1;
		}
		return -1;
	}
	/**
	 * * 如果不存在：UpdateOrAdd
	 * Identical：Update,
	 * Add：Add,
	 * Update：Update,
	 * Delete：Update,
	 * UpdateOrAdd：UpdateOrAdd
	 * **/
	private HQRecordState getPutRecordRecordState(HQRecordState stateOld){
		switch (stateOld) {
		case Identical:
			return HQRecordState.Update;
		case Add:
			return HQRecordState.Add;
		case Update:
			return HQRecordState.Update;
		case Delete:
			return HQRecordState.Update;
		case UpdateOrAdd:
			return HQRecordState.UpdateOrAdd;
		}
		return HQRecordState.Add;
	}
	
	
	
	public long getPlayerId() {
		return playerId;
	}
	public HashMap<String,HQPlayerListVar> getPlayerListVarList() {
		return playerListVarList;
	}
	public HashMap<String,HQPlayerObjectVar> getPlayerObjectVarList() {
		return playerObjectVarList;
	}
	public HashMap<String,HQPair> getPairList() {
		return pairList;
	}
	
	public HQPlayerState getState() {
		return state;
	}
	public void setState(HQPlayerState state) {
		this.state = state;
	}
	
	public HQRecordState getPairVarState() {
		return pairVarState;
	}
	public void setPairVarState(HQRecordState pairVarState) {
		this.pairVarState = pairVarState;
	}
	public long getLastChangeTime() {
		return lastChangeTime;
	}
	public void setLastChangeTime(long lastChangeTime) {
		this.lastChangeTime = lastChangeTime;
	}
	
	public long getPairVarVersionNum() {
		return pairVarVersionNum;
	}
	public void setPairVarVersionNum(long pairVarVersionNum) {
		this.pairVarVersionNum = pairVarVersionNum;
	}
	/****/
	@Override
	public HQPlayer clone() throws CloneNotSupportedException{
		HQPlayer o=(HQPlayer)super.clone();
		if(playerListVarList!=null){
			o.playerListVarList=new HashMap<String,HQPlayerListVar>();
			Iterator iter = playerListVarList.entrySet().iterator();
			while (iter.hasNext()) {
				 Map.Entry entry = (Map.Entry) iter.next();
				 o.playerListVarList.put((String)entry.getKey(),((HQPlayerListVar)entry.getValue()).clone());
			}
		}
		if(playerObjectVarList!=null){
			o.playerObjectVarList=new HashMap<String,HQPlayerObjectVar>();
			Iterator iter = playerObjectVarList.entrySet().iterator();
			while (iter.hasNext()) {
				 Map.Entry entry = (Map.Entry) iter.next();
				 o.playerObjectVarList.put((String)entry.getKey(),((HQPlayerObjectVar)entry.getValue()).clone());
			}
		}
		if(pairList!=null){
			o.pairList=new HashMap<String,HQPair>();
			Iterator iter = pairList.entrySet().iterator();
			while (iter.hasNext()) {
				 Map.Entry entry = (Map.Entry) iter.next();
				 o.pairList.put((String)entry.getKey(),((HQPair)entry.getValue()).clone());
			}
		}
		return o;
	}
}
