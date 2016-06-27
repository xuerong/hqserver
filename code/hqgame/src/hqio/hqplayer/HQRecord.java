package hqio.hqplayer;

import java.io.Serializable;
/**
 * 一个hqrecord对象代表数据库中的一条记录（除player表之外）
 * **/
public class HQRecord implements Serializable, Cloneable{
	private static final long serialVersionUID = 1L;
	private long recordId;
	private long playerId;
	private HQPair[] pairs;
	/** 决定于数据库之间同步的策略**/
	private HQRecordState recordState; 
	/**决定与game之间是否更新，放置多个game同事更新数据导致的数据的不一致，每次更新record，versionNum++**/
	private long versionNum=0;
	
	public HQRecord(){}
	// 用HQPlayerVarModel构建HQRecord
	public HQRecord(HQPlayerVarModel playerVarModel){
		this.recordId=-1;
		this.setRecordState(HQRecordState.Add);
		this.setVersionNum(0);
		String[] varNames=playerVarModel.getColumnNames();
		HQValueType[] varTypes=playerVarModel.getColumnTypes();
		int pairCount=varNames.length;
		HQPair[] pairs=new HQPair[pairCount];
		for(int i=0;i<pairCount;i++){
			pairs[i]=new HQPair();
			pairs[i].key=varNames[i];
			pairs[i].valueType=varTypes[i];
			pairs[i].value=null;
		}
		this.setPairs(pairs);
	}
	
	@Override
	public boolean equals(Object record){
		return ((HQRecord)record).recordId==this.recordId;
	}
	
	@Override
	public HQRecord clone() throws CloneNotSupportedException{
		HQRecord o = (HQRecord) super.clone();
        if(pairs!=null){
        	o.pairs=new HQPair[pairs.length];
        	int i=0;
        	for (HQPair pair : pairs) {
				o.pairs[i]=pair.clone();
				i++;
			}
        }
        return o;
	}

	
	public void setRecordId(long recordId) {
		this.recordId = recordId;
	}
	public long getRecordId() {
		return recordId;
	}


//	public void setRecordId(long recordId) {
//		this.recordId = recordId;
//	}


	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	public HQPair[] getPairs() {
		return pairs;
	}


	public void setPairs(HQPair[] pairs) {
		this.pairs = pairs;
	}


	public HQRecordState getRecordState() {
		return recordState;
	}


	public void setRecordState(HQRecordState recordState) {
		this.recordState = recordState;
	}


	public long getVersionNum() {
		return versionNum;
	}


	public void setVersionNum(long versionNum) {
		this.versionNum = versionNum;
	}
	
	
}
