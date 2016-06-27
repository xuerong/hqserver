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
 * HQPlayer��һ�����浥Ԫ
 * HQPlayer������Դ��gameserver��������ң���������Դ�����ݿ⣨�����ڴ棩
 * ��������Ϸ�е�player��������Ҫ��ȡ���ݿ�ı�����������ֳ���ı�����
 * �б��ͣ����ͬ�ֶ���,���ܸ������ȼ������ڴ棩����ʱnullʱ��˵���������ڴ棬��Ҫȥ���ݿ��ң����sizeΪ0��˵��value����Ϊ0
 * �����ͣ���������ڴ棩��
 * �����ͣ���������ڴ棩
 * ***/
public class HQPlayer implements Serializable, Cloneable{
	private static final long serialVersionUID = 1L;
	
	private static final List<HQPlayerVarModel> playerVarModels=HQPlayerVarModelReader.getInstance().getPlayerVarModels();
	/**ÿ�����Ψһ��ʾ��id�������ݿ�����ͬ**/
	private long playerId;
	/***���״̬���������ɸ������״̬ѡ��洢����***/
	private HQPlayerState state;
	/**��ҵ�pairVar�ͱ����ĸ���״��**/
	private HQRecordState pairVarState;
	/****/
	private long pairVarVersionNum;
	/**��һ�δ����player��ʱ�䣬���ݸ�ʱ�䣬�����Ƿ񽵵ͻ���ȼ�**/
	private long lastChangeTime;
	
	/**player�е��б��ͱ������б�,size��ȷ����**/
	private HashMap<String,HQPlayerListVar> playerListVarList;
	/**player�еĶ����ͱ������б�,size��ȷ����**/
	private HashMap<String,HQPlayerObjectVar> playerObjectVarList;
	/**player�еı����ͱ������б�,size��ȷ����**/
	private HashMap<String,HQPair> pairList;
	
	/**����һ�����(�����ݿ��е����ڴ�ʱʹ��,�������ȼ������ڴ棨-1��ʾȫ�����룩)***/
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
					// �����ݿ���ȡ���������ڴ�
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
	/**����һ�����(�����ݿ��е����ڴ�ʱʹ��)***/
	public HQPlayer(long playerId){
		this(playerId, -1);
	}
	/**
	 * ��ȡ����
	 * ����ǻ�ȡ�������ݣ��������󷵻�
	 * ������ǣ�������Ҫ��ȡ�����ݷ��أ�������Ϊnull
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
					// list�������ڴ棬��Ҫȥ���ݿ����
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
	 * put���ݣ���������µĺ��޸Ĵ˴���put��������
	 * 
	 * put��������RecordΪ��λ
	 * ��ӵģ�����1
	 * �޸ĵģ�����2
	 * ʧ�ܵģ�����-1(����������)
	 * 
	 * ����֮ǰ����Ҫ����֤��record��ȷ�ģ�ͨ��varName
	 * 
	 * Ŀǰ��HQStorage.java�У������ݵĲ���ȫ����varName�㣬��������ò���
	 * **/
	public int putData(Object value , String varName){
		HQPlayerListVar listVar=playerListVarList.get(varName);
		if(listVar!=null){
			HQRecord record = (HQRecord)value;
			if(listVar.getRecordList()==null){
				// list�������ڴ棬��Ҫȥ���ݿ����
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
	 * delete���ݣ�ֻ��ɾ��list�е�record
	 * 
	 * delete��������RecordΪ��λ������Ĳ���ΪrecordId
	 * �����ڵģ�����1
	 * ɾ���ɹ��ģ�����2
	 * ʧ�ܵģ�����-1(����������)
	 * 
	 * **/
	public int deleteData(long recordId,String varName){
		HQPlayerListVar listVar=playerListVarList.get(varName);
		if(listVar!=null){
			if(listVar.getRecordList()==null){
				// list�������ڴ棬��Ҫȥ���ݿ����
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
	 * * ��������ڣ�UpdateOrAdd
	 * Identical��Update,
	 * Add��Add,
	 * Update��Update,
	 * Delete��Update,
	 * UpdateOrAdd��UpdateOrAdd
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
