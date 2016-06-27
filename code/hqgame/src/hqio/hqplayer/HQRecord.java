package hqio.hqplayer;

import java.io.Serializable;
/**
 * һ��hqrecord����������ݿ��е�һ����¼����player��֮�⣩
 * **/
public class HQRecord implements Serializable, Cloneable{
	private static final long serialVersionUID = 1L;
	private long recordId;
	private long playerId;
	private HQPair[] pairs;
	/** ���������ݿ�֮��ͬ���Ĳ���**/
	private HQRecordState recordState; 
	/**������game֮���Ƿ���£����ö��gameͬ�¸������ݵ��µ����ݵĲ�һ�£�ÿ�θ���record��versionNum++**/
	private long versionNum=0;
	
	public HQRecord(){}
	// ��HQPlayerVarModel����HQRecord
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
